package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.domain.GuildIdRoles;
import com.yz.qqbot.domain.Role;
import com.yz.qqbot.request.OperationGuildRolesMember;
import com.yz.qqbot.request.UpdateGuildRoles;
import com.yz.util.ScriptUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 频道身份组API
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 3:05 <br/>
 */
@Slf4j
@Service
public class GuildRolesService extends BaseService {
    //用于获取 guild_id指定的频道下的身份组列表。
    String guildRolesUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}/roles", guild_id);
    }

    /**
     * 用于修改频道 guild_id 下 role_id 指定的身份组。
     * <p>
     * 需要使用的 token 对应的用户具备修改身份组权限。如果是机器人，要求被添加为管理员。
     * 接口会修改传入的字段，不传入的默认不会修改，至少要传入一个参数
     */
    String updateGuildRolesUrl(String guild_id, String role_id) {
        return MessageFormat.format("/guilds/{0}/roles/{1}", guild_id, role_id);
    }

    //创建频道身份组成员
    String guildRolesMemberUrl(String guild_id, String user_id, String role_id) {
        return MessageFormat.format("/guilds/{0}/members/{1}/roles/{2}", guild_id, user_id, role_id);
    }

    @Data
    static class GuildRoles {
        private String guild_id;
        private String role_id;
        private Role role;
    }

    /**
     * 用于获取 guild_id指定的频道下的身份组列表。
     */
    public GuildIdRoles getGuildRoles(String guild_id) {
        String requestUrl = botConfig.getPrefixApi(guildRolesUrl(guild_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, GuildIdRoles.class);
    }


    /**
     * 用于在guild_id 指定的频道下创建一个身份组。
     * 需要使用的 token 对应的用户具备创建身份组权限。如果是机器人，要求被添加为管理员。
     * 参数为非必填，但至少需要传其中之一，默认为空或 0。
     */
    public GuildRoles createGuildRoles(UpdateGuildRoles updateGuildRoles) {
        String requestUrl = botConfig.getPrefixApi(guildRolesUrl(updateGuildRoles.getGuild_id()));
        return restTemplate.postForObject(requestUrl, new HttpEntity<>(updateGuildRoles, botConfig.getHeader2()), GuildRoles.class);
    }


    /**
     * 用于修改频道 guild_id 下 role_id 指定的身份组
     */
    public GuildRoles updateGuildRoles(UpdateGuildRoles updateGuildRoles) {
        String requestUrl = botConfig.getPrefixApi(updateGuildRolesUrl(updateGuildRoles.getGuild_id(), updateGuildRoles.getRole_id()));
        return restTemplate.patchForObject(requestUrl, new HttpEntity<>(updateGuildRoles, botConfig.getHeader2()), GuildRoles.class);
    }

    /**
     * 用于删除频道guild_id下 role_id 对应的身份组
     * 需要使用的 token 对应的用户具备删除身份组权限。如果是机器人，要求被添加为管理员
     */
    public boolean delGuildRoles(String guild_id, String role_id) {
        String requestUrl = botConfig.getPrefixApi(updateGuildRolesUrl(guild_id, role_id));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

    /**
     * 用于将频道guild_id下的用户 user_id 添加到身份组 role_id 。
     * 需要使用的 token 对应的用户具备增加身份组成员权限。如果是机器人，要求被添加为管理员。
     * 如果要增加的身份组 ID 是5-子频道管理员，需要增加 channel 对象来指定具体是哪个子频道。
     */
    public boolean addGuildRolesMember(OperationGuildRolesMember opGuildRolesMember) {
        opGuildRolesMember.setOperationType(1);
        return guildRolesMember(opGuildRolesMember);
    }

    /**
     * 用于将 用户 user_id 从 频道 guild_id 的 role_id 身份组中移除。
     * 需要使用的 token 对应的用户具备删除身份组成员权限。如果是机器人，要求被添加为管理员。
     * 如果要删除的身份组 ID 是5-子频道管理员，需要增加 channel 对象来指定具体是哪个子频道。
     */
    public boolean delGuildRolesMember(OperationGuildRolesMember opGuildRolesMember) {
        opGuildRolesMember.setOperationType(2);
        return guildRolesMember(opGuildRolesMember);
    }

    public boolean guildRolesMember(OperationGuildRolesMember opGuildRolesMember) {
        String requestUrl = botConfig.getPrefixApi(guildRolesMemberUrl(opGuildRolesMember.getGuild_id(), opGuildRolesMember.getUser_id(), opGuildRolesMember.getRole_id()));
        Map<String, Object> botBody = new HashMap<String, Object>(1) {{
            if (!StringUtils.isEmpty(opGuildRolesMember.getChannel_id())) {
                put("channel", Collections.singletonMap("id", opGuildRolesMember.getChannel_id()));
            }
        }};
        HttpMethod httpMethod = opGuildRolesMember.getOperationType() == 1 ? HttpMethod.PUT : HttpMethod.DELETE;
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, httpMethod, new HttpEntity<>(botBody, botConfig.getHeader2()), Object.class);
        return HttpStatus.NO_CONTENT.equals(response.getStatusCode());
    }

}
