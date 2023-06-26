package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 精华消息对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:38 <br/>
 */
@Data
public class PinsMessage {
    /**
     * 频道 id
     */
    private String guild_id;
    /**
     * 子频道 id
     */
    private String channel_id;
    /**
     * 子频道内精华消息 id 数组
     */
    private List<String> message_ids;
}
