package com.yz.qqbot.request;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:12 <br/>
 */
@Data
public class MuteUsers {
    /**
     * 频道id
     */
    private String guild_id;
    /**
     * 禁言某个用户的id
     */
    private String user_id;
    /**
     * 批量禁言的用户id
     */
    private List<String> user_ids;
    /**
     * 禁言到期时间戳，绝对时间戳，单位：秒（与 mute_seconds 字段同时赋值的话，以该字段为准）
     */
    private String mute_end_timestamp;
    /**
     * 禁言多少秒（两个字段二选一，默认以 mute_end_timestamp 为准）
     */
    private String mute_second;
}
