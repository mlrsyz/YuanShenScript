package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.domain.Guild;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 频道API
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 1:35 <br/>
 */
@Slf4j
@Service
public class GuildService extends BaseService {

    //GET /guilds/{guild_id}
    String guildUrl(String guild_id) {
        return MessageFormat.format("/guilds/{0}", guild_id);
    }

    /**
     * 用于获取 guild_id 指定的频道的详情
     *
     * @param guild_id 频道ID
     */
    public Guild getGuild(String guild_id) {
        String requestUrl = botConfig.getPrefixApi(guildUrl(guild_id));
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, Guild.class);
    }
}
