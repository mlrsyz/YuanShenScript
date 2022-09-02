package com.yz.controller;


import com.alibaba.fastjson.JSON;
import com.yz.domain.ScriptReq;
import com.yz.script.Script;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 作者：ymx <br/>
 * 创建时间：2022/9/3 0:09 <br/>
 */

@Slf4j
@Component
public class ScriptWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("和客户端建立连接");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
        log.error("连接异常", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.info("客户端主动断开连接");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info(message.getPayload());
        if (StringUtils.isEmpty(message.getPayload())) {
            session.sendMessage(new TextMessage("请勿发生无效信息"));
        }
        // 获取到客户端发送过来的消息
        ScriptReq req;
        try {
            req = JSON.parseObject(message.getPayload(), ScriptReq.class);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("入参异常"));
            session.close(CloseStatus.NORMAL);
            return;
        }
        if (StringUtils.isEmpty(req.getCookie()) || StringUtils.isEmpty(req.getActivityType())) {
            session.sendMessage(new TextMessage("请填写cookie && 活动类型"));
            session.close(CloseStatus.NORMAL);
            return;
        }
        Script script = ScriptUtils.createScript(req.getActivityType().getType());
        // 发送消息给客户端
        if (script == null) {
            session.sendMessage(new TextMessage("没有当前活动的执行脚本"));
            session.close(CloseStatus.NORMAL);
            return;
        }
        script.execute(req.getCookie(), req.getActivityType(), req.getDateTime(), session);
    }
}