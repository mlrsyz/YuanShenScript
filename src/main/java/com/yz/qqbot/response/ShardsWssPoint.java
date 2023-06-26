package com.yz.qqbot.response;

import lombok.Data;

/**
 * 带分片 WSS 接入点
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:36 <br/>
 */
@Data
public class ShardsWssPoint {
    /**
     * WebSocket 的连接地址
     */
    private String url;
    /**
     * 建议的 shard 数
     */
    private Integer shards;
    /**
     * 创建 Session 限制信息
     */
    private SessionStartLimit session_start_limit;

    @Data
    public static class SessionStartLimit {
        /**
         * 每 24 小时可创建 Session 数
         */
        private Integer total;
        /**
         * 目前还可以创建的 Session 数
         */
        private Integer remaining;
        /**
         * 重置计数的剩余时间(ms)
         */
        private Integer reset_after;
        /**
         * 每 5s 可以创建的 Session 数
         */
        private Integer max_concurrency;
    }
}
