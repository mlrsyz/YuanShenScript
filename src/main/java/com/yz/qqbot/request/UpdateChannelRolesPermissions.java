package com.yz.qqbot.request;

import lombok.Data;

/**
 * 更新子频道 channel_id 下身份组 role_id 的权限
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:56 <br/>
 */
@Data
public class UpdateChannelRolesPermissions {
    /**
     * 子频道ID
     */
    private String channel_id;
    /**
     * 角色id
     */
    private String role_id;
    /**
     * 字符串形式的位图表示赋予用户的权限
     */
    private String add;
    /**
     * 字符串形式的位图表示赋予用户的权限
     */
    private String remove;
}
