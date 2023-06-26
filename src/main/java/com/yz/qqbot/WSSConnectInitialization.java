package com.yz.qqbot;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.api.MessageService;
import com.yz.qqbot.api.WSSService;
import com.yz.qqbot.domain.Message;
import com.yz.qqbot.domain.WssPayload;
import com.yz.qqbot.request.SendMessageRequest;
import com.yz.util.ScriptUtils;
import com.yz.util.YuanShenThreadTool;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:29 <br/>
 */
@Slf4j
@Configuration
public class WSSConnectInitialization {
    protected BotConfig botConfig;
    private final WSSService wSSService;
    private final MessageService messageService;
    //消息下行序列号
    private static Integer heartbeatSerialNumber = null;
    //心跳周期，单位毫秒
    private static Integer heartbeatInterval = -1;
    private static boolean existHeartbeat = false;
    //机器人ws实例
    private static WebSocketClient botWss = null;
    private static String session_id;

    public WSSConnectInitialization(BotConfig botConfig, WSSService wSSService, MessageService messageService) {
        this.botConfig = botConfig;
        this.wSSService = wSSService;
        this.messageService = messageService;
        botWss = getBotWss();
    }

    private WebSocketClient getBotWss() {
        try {
            String wssPointUrl = wSSService.getWssPoint();
            WebSocketClient webSocketClient = new WebSocketClient(new URI(wssPointUrl), new Draft_6455(), botConfig.getHeader()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    log.info("ws 连接成功");
                }

                @Override
                public void onMessage(String message) {
                    log.info("ws 收到消息" + JSON.toJSONString(message));
                    if (StringUtils.isEmpty(message)) {
                        return;
                    }
                    WssPayload payload = JSON.parseObject(message, WssPayload.class);
                    if (Objects.nonNull(payload.getS())) {
                        //设置消息下行序列号
                        heartbeatSerialNumber = payload.getS();
                    }
                    //连接成功返回Hello
                    if (payload.getOp() == 10 && Objects.nonNull(payload.getD())) {
                        //心跳
                        heartbeatInterval = JSON.parseObject(JSON.toJSONString(payload.getD())).getInteger("heartbeat_interval");
                        sendIdentify();
                        return;
                    }
                    //鉴权返回消息
                    if (payload.getOp() == 0 && Objects.equals(payload.getT(), "READY")) {
                        WssPayload.ReadyEvent readyEvent = JSON.parseObject(JSON.toJSONString(payload.getD()), WssPayload.ReadyEvent.class);
                        session_id = readyEvent.getSession_id();
                        //保持心跳检测
                        keepHeartbeat();
                    }
                    //恢复成功之后，就开始补发遗漏事件，所有事件补发完成之后，会下发一个 Resumed Event
                    if (payload.getOp() == 0 && Objects.equals(payload.getT(), "RESUMED")) {
                        log.info("ws 恢复成功 补发消息完成" + message);
                    }
                    if (payload.getOp() == 7) {
                        resumeConnection();
                    }
                    //事件监听事件回调处理
                    if (Objects.equals("AT_MESSAGE_CREATE", payload.getT())) {
                        //发送时机
                        //用户发送消息，@当前机器人或回复机器人消息时
                        //为保障消息投递的速度，消息顺序我们虽然会尽量有序，但是并不保证是严格有序的，如开发者对消息顺序有严格有序的需求，可以自行缓冲消息事件之后，基于 Message.seq 进行排序
                        Message messageRes = JSON.parseObject(JSON.toJSONString(payload.getD()), Message.class);
                        SendMessageRequest sendMessageRequest = new SendMessageRequest();
                        String content = messageRes.getContent().substring(messageRes.getContent().indexOf(" "));
                        String replyMsg = MessageFormat.format("<@!{0}> 你发送的是:{1}", messageRes.getAuthor().getId(), content);
                        sendMessageRequest.setContent(replyMsg);
                        sendMessageRequest.setMsg_id(messageRes.getId());
                        log.info("sendChannelMsg ====> {}", messageService.sendChannelMsg(sendMessageRequest, messageRes.getChannel_id()));
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    // websocket 错误码:https://bot.q.qq.com/wiki/develop/api/gateway/error/error.html
                    log.info("ws 连接关闭 code:{},reason:{},remote:{}", code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    log.info("连接错误" + ex.getMessage());
                }
            };
            webSocketClient.connect();
            return webSocketClient;
        } catch (Exception ex) {
            log.error("建立连接异常" + ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 鉴权连接
     */
    private void sendIdentify() {
        WssPayload payload = new WssPayload();
        payload.setOp(2);
        WssPayload.Identify identify = new WssPayload.Identify();
        identify.setToken(botConfig.getTokenHeader());
        identify.setIntents(1 << 30);
//        identify.setIntents(1 << 1);
//        identify.setShard(Arrays.asList(0, 0));
        WssPayload.Properties properties = new WssPayload.Properties();
        properties.setOs("linux");
        properties.setBrowser("edge");
        properties.setDevice("5600x");
        identify.setProperties(properties);
        payload.setD(identify);
        String sendMsg = JSON.toJSONString(payload);
        log.info("发送鉴权连接：{}", sendMsg);
        botWss.send(sendMsg);
    }


    /**
     * 保持心跳检测
     */
    public synchronized void keepHeartbeat() {
        if (existHeartbeat) {
            return;
        }
        YuanShenThreadTool.ysThreadPoolExecutor.submit(() -> {
            existHeartbeat = true;
            while (Objects.nonNull(botWss)) {
                WssPayload payload = new WssPayload();
                payload.setOp(1);
                payload.setD(heartbeatSerialNumber);
                String sendMsg = JSON.toJSONString(payload);
                log.info("发送心跳：{}", sendMsg);
                botWss.send(sendMsg);
                ScriptUtils.sleep(heartbeatInterval - 500);
            }
            existHeartbeat = false;
        });
    }


    /**
     * 恢复连接
     */
    public synchronized void resumeConnection() {
        WssPayload payload = new WssPayload();
        payload.setOp(6);
        WssPayload.Resume resume = new WssPayload.Resume();
        resume.setToken(botConfig.getTokenHeader());
        resume.setSession_id(session_id);
        resume.setSeq(heartbeatSerialNumber);
        payload.setD(resume);
        String sendMsg = JSON.toJSONString(payload);
        log.info("准备恢复连接：{}", sendMsg);
        botWss.send(sendMsg);
    }
}
