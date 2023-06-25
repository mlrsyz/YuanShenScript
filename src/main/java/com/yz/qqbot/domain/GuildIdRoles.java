package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 3:06 <br/>
 */
@Data
public class GuildIdRoles {
    /**
     * 频道 ID
     */
    private String guild_id;
    /**
     * 对象数组	一组频道身份组对象
     */
    private List<Role> roles;
    /**
     * 默认分组上限
     */
    private String role_num_limit;
}
