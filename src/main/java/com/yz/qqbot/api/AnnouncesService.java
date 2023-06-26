package com.yz.qqbot.api;

import com.yz.qqbot.domain.Announces;
import com.yz.qqbot.domain.PinsMessage;
import com.yz.qqbot.request.CreateAnnounces;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 公告API
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:22 <br/>
 */
@Slf4j
@Service
public class AnnouncesService extends BaseService {
    //POST /guilds/{guild_id}/announces 用于创建频道全局公告，公告类型分为 消息类型的频道公告 和 推荐子频道类型的频道公告
    String createAnnouncesUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}/announces", guild_id);
    }

    //DELETE /guilds/{guild_id}/announces/{message_id} 删除频道公告
    String delAnnouncesUrl(String guild_id, String message_id) {
        return MessageFormat.format("/guilds/{0}/announces/{1}", guild_id, message_id);
    }

    //PUT /channels/{channel_id}/pins/{message_id} 添加/删除精华消息
    String pinsMsgUrl(String channel_id, String message_id) {
        return MessageFormat.format("/channels/{0}/pins/{1}", channel_id, message_id);
    }

    //GET /channels/{channel_id}/pins 获取精华消息
    String getPinsMsgUrl(String channel_id) {
        return MessageFormat.format("/channels/{0}/pins", channel_id);
    }

    /**
     * <a href="https://bot.q.qq.com/wiki/develop/api/openapi/announces/post_guild_announces.html">公告</a>
     * 用于创建频道全局公告，公告类型分为 消息类型的频道公告 和 推荐子频道类型的频道公告 。
     * 当请求参数 message_id 有值时，优先创建消息类型的频道公告， 消息类型的频道公告只能创建成员公告类型的频道公告。
     * 创建推荐子频道类型的频道全局公告请将 message_id 设置为空，并设置对应的 announces_type 和 recommend_channels 请求参数，会一次全部替换推荐子频道公司。
     * 推荐子频道和消息类型全局公告不能同时存在，会互相顶替设置。
     * 同频道内推荐子频道最多只能创建 3 条。
     * 只有子频道权限为全体成员可见才可设置为推荐子频道。
     * 删除推荐子频道类型的频道公告请使用 删除频道公告,并将 message_id 设置为 all
     */
    public Announces createAnnounces(String guild_id, CreateAnnounces createAnnounces) {
        String requestUrl = botConfig.getPrefixApi(createAnnouncesUrl(guild_id));
        return restTemplate.postForObject(requestUrl, new HttpEntity<>(createAnnounces, botConfig.getHeader2()), Announces.class);
    }

    /**
     * 用于删除频道 guild_id 下指定 message_id 的全局公告。
     * message_id 有值时，会校验 message_id 合法性，若不校验校验 message_id，请将 message_id 设置为 all
     */
    public boolean delAnnounces(String guild_id, String message_id) {
        String requestUrl = botConfig.getPrefixApi(delAnnouncesUrl(guild_id, message_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    /**
     * 用于添加子频道 channel_id 内的精华消息。
     * 精华消息在一个子频道内最多只能创建 20 条。
     * 只有可见的消息才能被设置为精华消息。
     * 接口返回对象中 message_ids 为当前请求后子频道内所有精华消息 message_id 数组
     */
    public PinsMessage addPins(String guild_id, String message_id) {
        String requestUrl = botConfig.getPrefixApi(pinsMsgUrl(guild_id, message_id));
        return restTemplate.exchange(requestUrl, HttpMethod.PUT, new HttpEntity<>(null, botConfig.getHeader2()), PinsMessage.class).getBody();
    }

    /**
     * 用于删除子频道 channel_id 下指定 message_id 的精华消息。
     * 删除子频道内全部精华消息，请将 message_id 设置为 all
     */
    public boolean delPins(String guild_id, String message_id) {
        String requestUrl = botConfig.getPrefixApi(pinsMsgUrl(guild_id, message_id));
        ResponseEntity<Void> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Void.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    /**
     * 用于获取子频道 channel_id 内的精华消息。
         */
    public PinsMessage getPinsMsg(String guild_id) {
        String requestUrl = botConfig.getPrefixApi(getPinsMsgUrl(guild_id));
        return restTemplate.getForObject(requestUrl, PinsMessage.class, new HttpEntity<>(null, botConfig.getHeader2()));
    }
}
