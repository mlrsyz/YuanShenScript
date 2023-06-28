package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 用户频道列表
 * 作者：ymx <br/>
 * 创建时间：2023/6/25 18:55 <br/>
 */
@Data
public class Guild {
    /**
     * 频道ID
     */
    private String id;
    /**
     * 频道名称
     */
    private String name;
    /**
     * 频道头像地址
     */
    private String icon;
    /**
     * 创建人用户ID
     */
    private String owner_id;
    /**
     * 当前人是否是创建人
     */
    private Boolean owner;
    /**
     * 加入时间
     */
    private String joined_at;
    /**
     * 成员数
     */
    private Integer member_count;
    /**
     * 最大成员数
     */
    private Integer max_members;
    /**
     * 描述
     */
    private String description;
    /**
     * 操作人
     */
    private String op_user_id;
}
