package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:19 <br/>
 */
@Data
public class MemberWithGuildID {
    /**
     * 频道id
     */
    private String guild_id;
    /**
     * 用户的频道基础信息
     */
    private User user;
    /**
     * 用户的昵称
     */
    private String nick;
    /**
     * 数组	用户在频道内的身份
     */
    private List<String> roles;
    /**
     * 用户加入频道的时间
     */
    private String joined_at;
}
