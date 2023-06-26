package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.domain.ChannelPermissions;
import com.yz.qqbot.request.UpdateChannelRolesPermissions;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 17:34 <br/>
 */
@Slf4j
@Service
public class ChannelPermissionsService extends BaseService {
    /**
     * 用于获取 子频道channel_id 下用户 user_id 的权限。
     * 获取子频道用户权限。
     * 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员
     */
    String channelUserPermissionsUrl(String channel_id, String user_id) {
        return MessageFormat.format("/channels/{0}/members/{1}/permissions", channel_id, user_id);
    }

    /**
     * 用于获取子频道 channel_id 下身份组 role_id 的权限。
     */
    String channelRolesPermissionsUrl(String channel_id, String role_id) {
        return MessageFormat.format("/channels/{0}/roles/{1}/permissions", channel_id, role_id);
    }


    /**
     * 用于获取 子频道channel_id 下用户 user_id 的权限。
     *
     * @param channel_id 子频道ID
     * @param user_id    用户id
     */
    public ChannelPermissions getChannelUserPermissions(String channel_id, String user_id) {
        String requestUrl = botConfig.getPrefixApi(channelUserPermissionsUrl(channel_id, user_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, ChannelPermissions.class);
    }


    /**
     * 用于修改子频道 channel_id 下用户 user_id 的权限
     * 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
     * 参数包括add和remove两个字段，分别表示授予的权限以及删除的权限。要授予用户权限即把add对应位置 1，删除用户权限即把remove对应位置 1。当两个字段同一位都为 1，表现为删除权限。
     * 本接口不支持修改可管理子频道权限
     *
     * @param channel_id 子频道ID
     * @param user_id    用户id
     */
    public boolean updateChannelUserPermissions(String channel_id, String user_id) {
        String requestUrl = botConfig.getPrefixApi(channelUserPermissionsUrl(channel_id, user_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.PUT, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    /**
     * 用于获取子频道 channel_id 下身份组 role_id 的权限。
     * 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员
     *
     * @param channel_id 子频道ID
     * @param role_id    角色id
     */
    public ChannelPermissions getChannelRolesPermissions(String channel_id, String role_id) {
        String requestUrl = botConfig.getPrefixApi(channelRolesPermissionsUrl(channel_id, role_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, ChannelPermissions.class);
    }

    /**
     * 用于获取子频道 channel_id 下身份组 role_id 的权限。
     * 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员
     */
    public boolean updateChannelRolesPermissions(UpdateChannelRolesPermissions channelRolesPermissions) {
        String requestUrl = botConfig.getPrefixApi(channelRolesPermissionsUrl(channelRolesPermissions.getChannel_id(), channelRolesPermissions.getRole_id()));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.PUT, new HttpEntity<>(channelRolesPermissions, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }
}
