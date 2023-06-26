package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.domain.Dms;
import com.yz.qqbot.request.SendMessageRequest;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 19:02 <br/>
 */
@Slf4j
@Service
public class DmsService extends BaseService {
    @Autowired
    private MessageService messageService;
    //创建私信会话
    private static final String createDms = "/users/@me/dms";

    //撤回私信
    String withdrawMessageUrl(String guild_id, String message_id, boolean hidetip) {
        return MessageFormat.format("/dms/{0}/messages/{1}?hidetip=false", guild_id, message_id) + "?hidetip=" + hidetip;
    }


    /**
     * 创建私信会话
     *
     * @param recipient_id    接收者 id
     * @param source_guild_id 源频道 id
     */
    public Dms createDms(String recipient_id, String source_guild_id) {
        Map<String, String> params = new HashMap<String, String>() {{
            put("recipient_id", recipient_id);
            put("source_guild_id", source_guild_id);
        }};
        String requestUrl = botConfig.getPrefixApi(createDms);
        String result = ScriptUtils.sendPost(requestUrl, params, botConfig.getHeader());
        return JSON.parseObject(result, Dms.class);
    }

    /**
     * 用于发送私信消息，前提是已经创建了私信会话。
     * 私信的 guild_id 在创建私信会话时以及私信消息事件中获取。
     * 私信场景下，每个机器人每天可以对一个用户发 2 条主动消息。
     * 私信场景下，每个机器人每天累计可以发 200 条主动消息。
     * 私信场景下，被动消息没有条数限制
     */
    public Object sendDmsMsg(SendMessageRequest botBody, String channel_id) {
        return messageService.sendChannelMsg(botBody, channel_id, true);
    }

    /**
     * 用于撤回私信频道 guild_id 中 message_id 指定的私信消息。只能用于撤回机器人自己发送的私信。
     * 注意:
     * 公域机器人暂不支持申请，仅私域机器人可用，选择私域机器人后默认开通。
     * 注意: 开通后需要先将机器人从频道移除，然后重新添加，方可生效
     *
     * @param guild_id   频道Id
     * @param message_id 消息Id
     * @param hidetip    选填，是否隐藏提示小灰条，true 为隐藏，false 为显示。默认填false
     */
    public boolean withdrawMessage(String guild_id, String message_id, boolean hidetip) {
        String requestUrl = botConfig.getPrefixApi(withdrawMessageUrl(guild_id, message_id, hidetip));
        ResponseEntity<Object> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(null, botConfig.getHeader2()), Object.class);
        return HttpStatus.OK.equals(response.getStatusCode());
    }
}
