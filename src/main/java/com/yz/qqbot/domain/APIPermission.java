package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:11 <br/>
 */
@Data
public class APIPermission {
    /**
     * API 接口名，例如 /guilds/{guild_id}/members/{user_id}
     */
    private String path;
    /**
     * 请求方法，例如 GET
     */
    private String method;
    /**
     * API 接口名称，例如 获取频道信
     */
    private String desc;
    /**
     * 授权状态，auth_stats 为 1 时已授权
     */
    private Integer auth_status;

    @Data
    public static class APIPermissionDemand {
        /**
         * 申请接口权限的频道 id
         */
        private String guild_id;
        /**
         * 接口权限需求授权链接发送的子频道 id
         */
        private String channel_id;
        /**
         * 权限接口唯一标识
         */
        private APIPermissionDemandIdentify api_identify;
        /**
         * 接口权限链接中的接口权限描述信息
         */
        private String title;
        /**
         * 接口权限链接中的机器人可使用功能的描述信息
         */
        private String desc;
    }

    @Data
    public static class APIPermissionDemandIdentify {
        /**
         * API 接口名，例如 /guilds/{guild_id}/members/{user_id}
         */
        private String path;
        /**
         * 请求方法，例如 GET
         */
        private String method;
    }
}
