package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 子频道权限对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 17:31 <br/>
 */
@Data
public class ChannelPermissions {
    /**
     * 子频道 id
     */
    private String channel_id;
    /**
     * 用户 id  用户 id 或 身份组 id，只会返回其中之一
     */
    private String user_id;
    /**
     * 身份组 id
     */
    private String role_id;
    /**
     * 用户拥有的子频道权限
     */
    private String permissions;
}
