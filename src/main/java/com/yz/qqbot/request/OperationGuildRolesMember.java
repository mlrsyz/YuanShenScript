package com.yz.qqbot.request;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:59 <br/>
 */
@Data
public class OperationGuildRolesMember {
    /**
     * 频道id
     */
    private String guild_id;
    /**
     * 用户id
     */
    private String user_id;
    /**
     * 角色id
     */
    private String role_id;
    /**
     * 子频道id
     */
    private String channel_id;
    /**
     * 操作类型 1:add 2:del 自动填充无需处理
     */
    private int operationType = 1;
}
