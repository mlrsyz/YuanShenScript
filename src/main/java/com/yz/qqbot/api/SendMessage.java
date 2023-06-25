package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.domain.Dms;
import com.yz.qqbot.request.SendMessageRequest;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 发送频道消息
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 0:38 <br/>
 */
@Slf4j
@Service
public class SendMessage extends BaseService {
    /**
     * <a href="https://bot.q.qq.com/wiki/develop/api/openapi/message/post_messages.html">发送消息接口</a>
     */
    private static final String sendMessageUrl = "/channels/{0}/messages";
    //创建私信会话
    private static final String createDms = "/users/@me/dms";
    //发送私信
    private static final String sendDms = "/dms/{0}/messages";

    /**
     * 默认发送主频道
     *
     * @param botBody 消息体
     */
    public void sendDefaultChannelMsg(SendMessageRequest botBody) {
        sendChannelMsg(botBody, "481170028", false);
    }

    /**
     * @param botBody    消息体
     * @param channelsId 子频道(私信则是私信频道ID)
     * @param isDms      是否私信
     */
    public void sendChannelMsg(SendMessageRequest botBody, String channelsId, boolean isDms) {
        String result;
        String requestUrl = botConfig.getPrefixApi(MessageFormat.format(isDms ? sendDms : sendMessageUrl, channelsId));
        HttpHeaders header = botConfig.getHeader2();
        if (Objects.nonNull(botBody.getFile_image())) {
            header.setContentType(MediaType.MULTIPART_FORM_DATA);
            ByteArrayResource fileAsResource = new ByteArrayResource(botBody.getFile_image()) {
                @Override
                public String getFilename() {
                    return botBody.getImageName();
                }

                @Override
                public long contentLength() {
                    return botBody.getFile_image().length;
                }
            };
            MultiValueMap<String, Object> fileBotBody = new LinkedMultiValueMap<String, Object>(9) {{
                add("file_image", fileAsResource);
                if (!StringUtils.isEmpty(botBody.getContent())) {
                    add("content", botBody.getContent());
                }
                if (Objects.nonNull(botBody.getEmbed())) {
                    add("embed", JSON.toJSONString(botBody.getEmbed()));
                }
                if (Objects.nonNull(botBody.getArk())) {
                    add("ark", JSON.toJSONString(botBody.getArk()));
                }
                if (Objects.nonNull(botBody.getMessage_reference())) {
                    add("message_reference", JSON.toJSONString(botBody.getMessage_reference()));
                }
                if (!StringUtils.isEmpty(botBody.getImage())) {
                    add("image", botBody.getImage());
                }
                if (!StringUtils.isEmpty(botBody.getMsg_id())) {
                    add("msg_id", botBody.getMsg_id());
                }
                if (!StringUtils.isEmpty(botBody.getEvent_id())) {
                    add("event_id", botBody.getEvent_id());
                }
                if (Objects.nonNull(botBody.getMarkdown())) {
                    add("markdown", JSON.toJSONString(botBody.getMarkdown()));
                }
            }};
            result = restTemplate.postForObject(requestUrl, new HttpEntity<>(fileBotBody, header), String.class);
        } else {
            result = restTemplate.postForObject(requestUrl, new HttpEntity<>(botBody, header), String.class);
        }
        log.info("sendChannelMsg===>{}", JSON.toJSONString(result));
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
}
