package com.yz.qqbot.handler;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.api.MessageService;
import com.yz.qqbot.api.UserService;
import com.yz.qqbot.api.enumtype.ChannelSubType;
import com.yz.qqbot.api.enumtype.Intents;
import com.yz.qqbot.domain.Channel;
import com.yz.qqbot.domain.WssPayload;
import com.yz.qqbot.request.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/28 11:53 <br/>
 */
@Slf4j
@Component
public class GuildEventHandle {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    public void execIntents(Intents intents, WssPayload payload) {
        String body = JSON.toJSONString(payload.getD());
        switch (intents) {
            case GUILD_CREATE:
            case GUILD_UPDATE:
            case GUILD_DELETE:
                break;
            case CHANNEL_CREATE:
            case CHANNEL_UPDATE:
            case CHANNEL_DELETE:
                Channel channel = JSON.parseObject(body, Channel.class);
                if (Intents.CHANNEL_CREATE.equals(intents)) {
                    SendMessageRequest request = new SendMessageRequest();
                    request.setContent("大家好 我是:" + userService.getCurrentUser().getUsername());
                    request.setContent(request.getContent() + "该子频道创建成功,类型::" + ChannelSubType.getSubType(channel.getSub_type()).getDesc());
                    log.info("触发 CHANNEL_CREATE({}) ⬇⬇⬇⬇⬇ send:{}", payload.getId(), messageService.sendChannelMsg(request, channel.getId()));
                }
                if (Intents.CHANNEL_UPDATE.equals(intents)) {
                    log.info("触发 CHANNEL_UPDATE({}) ⬇⬇⬇⬇⬇", payload.getId());
                }
                if (Intents.CHANNEL_DELETE.equals(intents)) {
                    log.info("触发 CHANNEL_DELETE({}) ⬇⬇⬇⬇⬇", payload.getId());
                }
                log.info("channel事件id:{}", payload.getId());
                break;
            case GUILD_MEMBER_ADD:
                break;
            case GUILD_MEMBER_UPDATE:
                break;
            case GUILD_MEMBER_REMOVE:
                break;
            case MESSAGE_CREATE:
                break;
            case MESSAGE_DELETE:
                break;
            case MESSAGE_REACTION_ADD:
                break;
            case MESSAGE_REACTION_REMOVE:
                break;
            case DIRECT_MESSAGE_CREATE:
                break;
            case DIRECT_MESSAGE_DELETE:
                break;
            case OPEN_FORUM_THREAD_CREATE:
                break;
            case OPEN_FORUM_THREAD_UPDATE:
                break;
            case OPEN_FORUM_THREAD_DELETE:
                break;
            case OPEN_FORUM_POST_CREATE:
                break;
            case OPEN_FORUM_POST_DELETE:
                break;
            case OPEN_FORUM_REPLY_CREATE:
                break;
            case OPEN_FORUM_REPLY_DELETE:
                break;
            case AUDIO_OR_LIVE_CHANNEL_MEMBER_ENTER:
                break;
            case AUDIO_OR_LIVE_CHANNEL_MEMBER_EXIT:
                break;
            case INTERACTION_CREATE:
                break;
            case MESSAGE_AUDIT_PASS:
                break;
            case MESSAGE_AUDIT_REJECT:
                break;
            case FORUM_THREAD_CREATE:
                break;
            case FORUM_THREAD_UPDATE:
                break;
            case FORUM_THREAD_DELETE:
                break;
            case FORUM_POST_CREATE:
                break;
            case FORUM_POST_DELETE:
                break;
            case FORUM_REPLY_CREATE:
                break;
            case FORUM_REPLY_DELETE:
                break;
            case FORUM_PUBLISH_AUDIT_RESULT:
                break;
            case AUDIO_START:
                break;
            case AUDIO_FINISH:
                break;
            case AUDIO_ON_MIC:
                break;
            case AUDIO_OFF_MIC:
                break;
            case AT_MESSAGE_CREATE:
                break;
            case PUBLIC_MESSAGE_DELETE:
                break;
        }
    }
}
