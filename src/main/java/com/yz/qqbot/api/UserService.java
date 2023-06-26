package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.yz.qqbot.domain.GuildsMe;
import com.yz.qqbot.domain.User;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户API
 * 作者：ymx <br/>
 * 创建时间：2023/6/25 23:46 <br/>
 */
@Slf4j
@Service
public class UserService extends BaseService {

    /**
     * <a href="https://bot.q.qq.com/wiki/develop/api/openapi/user/me.html">用户API</a>
     */
    //GET /users/@me用于获取当前用户（机器人）详情
    private static final String user_me = "/users/@me";
    //获取用户频道列表
    private static final String guild_me = "/users/@me/guilds";

    /**
     * 用于获取当前用户（机器人）详情
     *
     * @return User
     */
    public User getCurrentUser() {
        String requestUrl = botConfig.getPrefixApi(user_me);
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, User.class);
    }

    /**
     * 获取用户频道列表
     */
    public List<GuildsMe> queryGuildMe(String before, String after, Integer limit) {
        Map<String, String> params = new HashMap<String, String>() {{
            if (!StringUtils.isEmpty(after)) {
                put("before", before);
            }
            if (!StringUtils.isEmpty(after)) {
                put("after", after);
            }
            put("limit", Objects.isNull(limit) ? "100" : after);
        }};
        String requestUrl = botConfig.getPrefixApi(guild_me);
        String result = ScriptUtils.sendGet(requestUrl, params, botConfig.getHeader());
        return JSONArray.parseArray(result, GuildsMe.class);
    }
}
