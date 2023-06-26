package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.domain.Message;
import com.yz.qqbot.domain.MessageAudited;
import com.yz.qqbot.domain.MessageSetting;
import com.yz.qqbot.request.SendMessageRequest;
import com.yz.util.ScriptUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * 发送频道消息
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 0:38 <br/>
 */
@Slf4j
@Service
public class MessageService extends BaseService {
    /**
     * <a href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html">发送消息接口</a>
     */

    //用于获取子频道 channel_id 下的消息 message_id 的详情
    String channelMessage(String channel_id, String message_id) {
        return MessageFormat.format("/channels/{0}/messages/{1}", channel_id, message_id);
    }

    //发送消息
    String sendMessageUrl(String channel_id, boolean isDms) {
        return MessageFormat.format(isDms ? "/dms/{0}/messages" : "/channels/{0}/messages", channel_id);
    }

    //撤回消息
    String withdrawMessageUrl(String channel_id, String message_id, boolean hidetip) {
        return channelMessage(channel_id, message_id) + "?hidetip=" + hidetip;
    }

    //用于获取机器人在频道 guild_id 内的消息频率设置
    String guildsMessageSetting(String guild_id) {
        return MessageFormat.format("/guilds/{guild_id}/message/setting", guild_id);
    }

    /**
     * 用于获取子频道 channel_id 下的消息 message_id 的详情
     *
     * @param channel_id 子频道Id
     * @param message_id 消息Id
     */
    public Message getChannelMessage(String channel_id, String message_id) {
        String requestUrl = botConfig.getPrefixApi(channelMessage(channel_id, message_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, Message.class);
    }

    /**
     * 要求操作人在该子频道具有发送消息的权限。
     * 主动消息在频道主或管理设置了情况下，按设置的数量进行限频。在未设置的情况遵循如下限制:
     * 主动推送消息，默认每天往每个子频道可推送的消息数是 20 条，超过会被限制。
     * 主动推送消息在每个频道中，每天可以往 2 个子频道推送消息。超过后会被限制。
     * 不论主动消息还是被动消息，在一个子频道中，每 1s 只能发送 5 条消息。
     * 被动回复消息有效期为 5 分钟。超时会报错。
     * 发送消息接口要求机器人接口需要连接到 websocket 上保持在线状态
     * 有关主动消息审核，可以通过 Intents 中审核事件 MESSAGE_AUDIT 返回 MessageAudited 对象获取结果
     *
     * @param botBody    消息体
     * @param channel_id 子频道(私信则是私信频道ID)
     */
    public Object sendChannelMsg(SendMessageRequest botBody, String channel_id) {
        return sendChannelMsg(botBody, channel_id, false);
    }

    /**
     * 增加发送私信模式消息
     *
     * @param isDms true则是私信模式
     */
    public Object sendChannelMsg(SendMessageRequest botBody, String channel_id, boolean isDms) {
        String result;
        String requestUrl = botConfig.getPrefixApi(sendMessageUrl(channel_id, isDms));
        HttpHeaders header = botConfig.getHeader2();
        if (Objects.nonNull(botBody.getFile_image())) {
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
            ByteArrayResource fileAsResource = new ByteArrayResource(botBody.getFile_image()) {
                @Override
                public String getFilename() {
                    return botBody.getImageName();
                }

                @Override
                public long contentLength() {
                    return botBody.getFile_image().length;
                }
            };
            MultiValueMap<String, Object> fileBotBody = new LinkedMultiValueMap<String, Object>(9) {{
                add("file_image", fileAsResource);
                if (!StringUtils.isEmpty(botBody.getContent())) {
                    add("content", botBody.getContent());
                }
                if (Objects.nonNull(botBody.getEmbed())) {
                    add("embed", JSON.toJSONString(botBody.getEmbed()));
                }
                if (Objects.nonNull(botBody.getArk())) {
                    add("ark", JSON.toJSONString(botBody.getArk()));
                }
                if (Objects.nonNull(botBody.getMessage_reference())) {
                    add("message_reference", JSON.toJSONString(botBody.getMessage_reference()));
                }
                if (!StringUtils.isEmpty(botBody.getImage())) {
                    add("image", botBody.getImage());
                }
                if (!StringUtils.isEmpty(botBody.getMsg_id())) {
                    add("msg_id", botBody.getMsg_id());
                }
                if (!StringUtils.isEmpty(botBody.getEvent_id())) {
                    add("event_id", botBody.getEvent_id());
                }
                if (Objects.nonNull(botBody.getMarkdown())) {
                    add("markdown", JSON.toJSONString(botBody.getMarkdown()));
                }
            }};
            result = restTemplate.postForObject(requestUrl, new HttpEntity<>(fileBotBody, header), String.class);
        } else {
            result = restTemplate.postForObject(requestUrl, new HttpEntity<>(botBody, header), String.class);
        }
        if (!StringUtils.isEmpty(result) && result.contains("data")) {
            return JSON.parseObject(result, sendChannelResult.class);
        }
        return JSON.parseObject(result, Message.class);
    }

    @Data
    static class sendChannelResult {
        /**
         * 错误码
         */
        private Integer code;
        private String message;
        private MessageAudit data;
    }

    @Data
    static class MessageAudit {
        private MessageAudited message_audit;
    }


    /**
     * 用于撤回子频道 channel_id 下的消息 message_id。
     * 管理员可以撤回普通成员的消息。
     * 频道主可以撤回所有人的消息。
     * 注意:
     * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
     * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效
     *
     * @param channel_id 子频道Id
     * @param message_id 消息Id
     * @param hidetip    选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认为false
     */
    public boolean withdrawMessage(String channel_id, String message_id, boolean hidetip) {
        String requestUrl = botConfig.getPrefixApi(withdrawMessageUrl(channel_id, message_id, hidetip));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.OK.equals(response.getStatusCode());
    }


    /**
     * 用于获取机器人在频道 guild_id 内的消息频率设置
     */
    public MessageSetting getGuildsMessageSetting(String guild_id) {
        String requestUrl = botConfig.getPrefixApi(guildsMessageSetting(guild_id));
        String result = ScriptUtils.sendPost(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, MessageSetting.class);
    }
}
