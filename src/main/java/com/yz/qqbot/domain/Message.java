package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:09 <br/>
 */
@Data
public class Message {
    /**
     * 消息 id
     */
    private String id;
    /**
     * 子频道 id
     */
    private String channel_id;
    /**
     * 频道 id
     */
    private String guild_id;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息创建时间
     */
    private String timestamp;
    /**
     * 消息编辑时间
     */
    private String edited_timestamp;
    /**
     * 是否是@全员消息
     */
    private Boolean mention_everyone;
    /**
     * 消息创建者
     */
    private User author;
    /**
     * 附件
     */
    private List<MessageAttachment> attachments;
    /**
     * embed
     */
    private List<MessageEmbed> embeds;
    /**
     * 消息中@的人
     */
    private List<User> mentions;
    /**
     * 消息创建者的member信息
     */
    private Member member;
    /**
     * ark消息
     */
    private MessageArk ark;
    /**
     * 用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序。(目前只在消息事件中有值，2022年8月1日 后续废弃)
     */
    private int seq;
    /**
     * 子频道消息 seq，用于消息间的排序，seq 在同一子频道中按从先到后的顺序递增，不同的子频道之间消息无法排序
     */
    private String seq_in_channel;
    /**
     * 引用消息对象
     */
    private MessageReference message_reference;
    /**
     * 用于私信场景下识别真实的来源频道id
     */
    private String src_guild_id;
}
