package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yz.qqbot.domain.Channel;
import com.yz.qqbot.domain.CreateChannel;
import com.yz.qqbot.domain.UpdateChannel;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 1:48 <br/>
 */
@Slf4j
@Service
public class ChannelService extends BaseService {
    //用于获取 guild_id 指定的频道下的子频道列表
    String guildsListUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}/channels", guild_id);
    }

    /**
     * 用于获取 channel_id 指定的子频道的详情 && 用于修改 channel_id 指定的子频道的信息。 && 用于删除 channel_id 指定的子频道。
     * 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
     * 修改成功后，会触发子频道更新事件。
     * 注意
     * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
     * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效。
     * PATCH /channels/{channel_id}
     **/
    String channelDetail(String channel_id) {
        return MessageFormat.format("/channels/{0}", channel_id);
    }

    /**
     * 用于在 guild_id 指定的频道下创建一个子频道。
     * 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员。
     * 创建成功后，返回创建成功的子频道对象，同时会触发一个频道创建的事件通知。POST /guilds/{guild_id}/channels
     **/
    String createChannel(String guild_id) {
        return MessageFormat.format("/guilds/{0}/channels", guild_id);
    }

    //GET /channels/{channel_id}/online_nums 用于查询音视频/直播子频道 channel_id 的在线成员数。
    String onlineNumsUrl(String channel_id) {
        return MessageFormat.format("/channels/{0}/online_nums", channel_id);
    }

    /**
     * 用于获取 guild_id 指定的频道下的子频道列表
     */
    public List<Channel> getGuildsList(String guild_id) {
        String requestUrl = botConfig.getPrefixApi(guildsListUrl(guild_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSONArray.parseArray(result, Channel.class);
    }

    /**
     * 用于获取 channel_id 指定的子频道的详情
     */
    public Channel getChannelDetail(String channel_id) {
        String requestUrl = botConfig.getPrefixApi(channelDetail(channel_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, Channel.class);
    }

    /**
     * 用于在 guild_id 指定的频道下创建一个子频道。
     */
    public Channel createChannel(String guild_id, CreateChannel createChannel) {
        String requestUrl = botConfig.getPrefixApi(createChannel(guild_id));
        return restTemplate.postForObject(requestUrl, new HttpEntity<>(createChannel, botConfig.getHeader2()), Channel.class);
    }

    /**
     * 用于修改 channel_id 指定的子频道的信息。
     */
    public Channel updateChannel(String channel_id, UpdateChannel updateChannel) {
        String requestUrl = botConfig.getPrefixApi(channelDetail(channel_id));
        return restTemplate.patchForObject(requestUrl, new HttpEntity<>(updateChannel, botConfig.getHeader2()), Channel.class);
    }

    /**
     * 用于删除 channel_id 指定的子频道。
     */
    public boolean deleteChannel(String channel_id) {
        String requestUrl = botConfig.getPrefixApi(channelDetail(channel_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    /**
     * 用于查询音视频/直播子频道 channel_id 的在线成员数。
     */
    public Integer onlineNums(String channel_id) {
        String requestUrl = botConfig.getPrefixApi(onlineNumsUrl(channel_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return StringUtils.isEmpty(result) ? 0 : JSON.parseObject(result).getInteger("online_nums");
    }
}
