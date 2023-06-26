package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 消息审核对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 18:34 <br/>
 */
@Data
public class MessageAudited {
    /**
     * 消息审核 id
     */
    private String audit_id;
    /**
     * 消息 id，只有审核通过事件才会有值
     */
    private String message_id;
    /**
     * 频道 id
     */
    private String guild_id;
    /**
     * 子频道 id
     */
    private String channel_id;
    /**
     * 消息审核时间
     */
    private String audit_time;
    /**
     * 消息创建时间
     */
    private String create_time;
    /**
     * 子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序
     */
    private String seq_in_channel;
}
