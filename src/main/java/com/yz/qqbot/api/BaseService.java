package com.yz.qqbot.api;

import com.yz.qqbot.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/25 23:47 <br/>
 */
public abstract class BaseService {
    @Autowired
    protected BotConfig botConfig;
    protected final RestTemplate restTemplate = new RestTemplate();
}
