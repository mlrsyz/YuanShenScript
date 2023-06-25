package com.yz.qqbot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 0:01 <br/>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "qq-bot")
@EnableConfigurationProperties(BotConfig.class)
public class BotConfig {
    /**
     * 机器人appID
     */
    private String appId;
    /**
     * 机器人token
     */
    private String token;
    /**
     * 机器人密钥  auth认证需要
     */
    private String secretKey;
    /**
     * Bot Authorization前缀
     */
    private String tokenHeaderPrefix = "Bot ";

    /**
     * Bot Token
     * 使用申请机器人时平台返回的机器人 appID + token 拼接而成。此时，所有的操作都是以机器人身份来完成的。
     * Authorization: Bot 100000.Cl2FMQZnCjm1XVW7vRze4b7Cq4se7kKWs
     */
    private String tokenHeader;

    public String getTokenHeader() {
        return tokenHeaderPrefix + appId + "." + token;
    }

    /**
     * 是否沙箱
     */
    private boolean isSandbox;
    /**
     * 正式环境
     */
    private String prodDomain;
    /**
     * 沙箱
     */
    private String sandboxDomain;

    public String getPrefixApi(String suffixUrl) {
        return (isSandbox() ? getSandboxDomain() : getProdDomain()) + suffixUrl;
    }

    /**
     * 获取header
     */
    public Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.put(HttpHeaders.AUTHORIZATION, getTokenHeader());
        headers.put(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.35");
        return headers;
    }

    /**
     * 获取HttpHeaders
     */
    public HttpHeaders getHeader2() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set(HttpHeaders.AUTHORIZATION, getTokenHeader());
        header.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.35");
        return header;
    }
}
