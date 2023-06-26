package com.yz.qqbot.api;

import com.yz.qqbot.request.ChannelsEmoji;
import com.yz.qqbot.response.QueryChannelsEmoji;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:51 <br/>
 */
@Slf4j
@Service
public class EmojiService extends BaseService {
    //PUT /channels/{channel_id}/messages/{message_id}/reactions/{type}/{id} 对消息 message_id 进行表情表态
    String channelsEmoji(String channel_id, String message_id, Integer emojiType, Integer emojiId) {
        return MessageFormat.format("/channels/{0}/messages/{1}/reactions/{2}/{3}", channel_id, message_id, emojiType, emojiId);
    }

    //拉取对消息 message_id 指定表情表态的用户列表
    String channelsEmojiUsers(String channel_id, String message_id, Integer emojiType, Integer emojiId, String cookie, Integer limit) {
        if (limit <= 0 || limit > 50) {
            limit = 50;
        }
        return channelsEmoji(channel_id, message_id, emojiType, emojiId) + MessageFormat.format("?cookie={0}&limit={1}", cookie, limit);
    }

    public boolean channelsEmoji(ChannelsEmoji emoji) {
        String requestUrl = botConfig.getPrefixApi(channelsEmoji(emoji.getChannel_id(), emoji.getMessage_id(), emoji.getType(), emoji.getId()));
        HttpMethod httpMethod = (emoji.getOperationType() == 1) ? HttpMethod.PUT : HttpMethod.DELETE;
        ResponseEntity<Void> response = restTemplate.exchange(requestUrl, httpMethod, new HttpEntity<>(null, botConfig.getHeader2()), Void.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    /**
     * 对消息 message_id 进行表情表态
     */
    public boolean addChannelsEmoji(ChannelsEmoji emoji) {
        emoji.setOperationType(1);
        return channelsEmoji(emoji);
    }

    /**
     * 删除对消息 message_id 表情表态
     */
    public boolean delChannelsEmoji(ChannelsEmoji emoji) {
        emoji.setOperationType(2);
        return channelsEmoji(emoji);
    }


    /**
     * 拉取对消息 message_id 指定表情表态的用户列表
     */
    public QueryChannelsEmoji queryChannelsEmoji(ChannelsEmoji emoji) {
        String requestUrl = botConfig.getPrefixApi(channelsEmojiUsers(emoji.getChannel_id(), emoji.getMessage_id(), emoji.getType(), emoji.getId(), emoji.getCookie(), emoji.getLimit()));
        return restTemplate.getForObject(requestUrl, QueryChannelsEmoji.class, new HttpEntity<>(null, botConfig.getHeader2()));
    }
}
