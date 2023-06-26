package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yz.qqbot.domain.APIPermission;
import com.yz.qqbot.request.ApiAuthorization;
import com.yz.qqbot.response.ApiAuthorizationRes;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:14 <br/>
 */
@Slf4j
@Service
public class APIPermissionService extends BaseService {
    //GET /guilds/{guild_id}/api_permission 用于获取机器人在频道 guild_id 内可以使用的权限列表。
    String apiPermissionUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}/api_permission", guild_id);
    }

    //POST /guilds/{guild_id}/api_permission/demand 用于创建 API 接口权限授权链接，该链接指向guild_id对应的频道
    String apiAuthorizationUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}/api_permission/demand", guild_id);
    }

    /**
     * 用于获取机器人在频道 guild_id 内可以使用的权限列表
     */
    public List<APIPermission> getApiPermissions(String guild_id) {
        String requestUrl = botConfig.getPrefixApi(apiPermissionUrl(guild_id));
        String response = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        if (!StringUtils.isEmpty(requestUrl)) {
            return JSONArray.parseArray(JSON.parseObject(response).getString("apis"), APIPermission.class);
        }
        return new ArrayList<>();
    }

    /**
     * 用于创建 API 接口权限授权链接，该链接指向guild_id对应的频道 。
     * 需要注意，私信场景中，当需要查询私信来源频道的权限时，应使用src_guild_id，即 message中的src_guild_id
     * 每天只能在一个频道内发 3 条（默认值）频道权限授权链接。
     */
    public ApiAuthorizationRes apiAuthorization(String guild_id, ApiAuthorization apiAuthorization) {
        String requestUrl = botConfig.getPrefixApi(apiAuthorizationUrl(guild_id));
        return restTemplate.postForObject(requestUrl, new HttpEntity<>(apiAuthorization, botConfig.getHeader2()), ApiAuthorizationRes.class);

    }
}
