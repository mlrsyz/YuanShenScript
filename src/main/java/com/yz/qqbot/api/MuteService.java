package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yz.qqbot.request.MuteUsers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 禁言API
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 19:16 <br/>
 */
@Slf4j
@Service
public class MuteService extends BaseService {
    //PATCH /guilds/{guild_id}/mute 禁言全员/禁言批量成员
    String muteUsersUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}/mute", guild_id);
    }

    //禁言指定成员 PATCH /guilds/{guild_id}/members/{user_id}/mute
    String muteUserUrl(String guild_id, String user_id) {
        return MessageFormat.format("/guilds/{0}/members/{1}/mute", guild_id, user_id);
    }

    /**
     * 用于将频道的全体成员（非管理员）禁言。
     * 需要使用的 token 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
     * 该接口同样可用于解除禁言，具体使用见解除全员禁言
     */
    public boolean muteAll(MuteUsers muteUsers) {
        String requestUrl = botConfig.getPrefixApi(muteUsersUrl(muteUsers.getGuild_id()));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.PATCH, new HttpEntity<>(muteUsers, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    /**
     * 用于禁言频道 guild_id 下的成员 user_id。
     * <p>
     * 需要使用的 token 对应的用户具备管理员权限。如果是机器人，要求被添加为管理员。
     * 该接口同样可用于解除禁言，具体使用见解除指定成员禁言
     */
    public boolean muteUser(MuteUsers muteUsers) {
        String requestUrl = botConfig.getPrefixApi(muteUserUrl(muteUsers.getGuild_id(), muteUsers.getUser_id()));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.PATCH, new HttpEntity<>(muteUsers, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    public List<String> muteUsers(MuteUsers muteUsers) {
        String requestUrl = botConfig.getPrefixApi(muteUsersUrl(muteUsers.getGuild_id()));
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.PATCH, new HttpEntity<>(muteUsers, botConfig.getHeader2()), String.class);
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            String resIds = JSON.parseObject(response.getBody()).getString("user_ids");
            return JSONArray.parseArray(resIds, String.class);
        }
        return new ArrayList<>();
    }
}
