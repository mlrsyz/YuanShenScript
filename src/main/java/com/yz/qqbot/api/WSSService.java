package com.yz.qqbot.api;

import com.alibaba.fastjson.JSON;
import com.yz.qqbot.response.ShardsWssPoint;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:29 <br/>
 */
@Slf4j
@Service
public class WSSService extends BaseService {
    //GET /gateway 用于获取 WSS 接入地址，通过该地址可建立 websocket 长连接
    private static final String WSS_POINT = "/gateway";

    //GET /gateway/bot 用于获取 WSS 接入地址及相关信息，通过该地址可建立 websocket 长连接
    private static final String SHARDS_WSS_POINT = "/gateway/bot";

    /**
     * 用于获取 WSS 接入地址，通过该地址可建立 websocket 长连接
     */
    public String getWssPoint() {
        String requestUrl = botConfig.getPrefixApi(WSS_POINT);
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result).getString("url");
    }

    /**
     * 用于获取 WSS 接入地址及相关信息，通过该地址可建立 websocket 长连接。相关信息包括：
     * 建议的分片数。
     * 目前连接数使用情况
     */
    public ShardsWssPoint getShardsWssPoint() {
        String requestUrl = botConfig.getPrefixApi(SHARDS_WSS_POINT);
        String result = ScriptUtils.sendGet(requestUrl, null, botConfig.getHeader());
        return JSON.parseObject(result, ShardsWssPoint.class);
    }
}
