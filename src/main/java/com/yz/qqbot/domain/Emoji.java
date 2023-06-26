package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 表情对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:50 <br/>
 */
@Data
public class Emoji {
    /**
     * 表情ID，系统表情使用数字为ID，emoji使用emoji本身为id，参考 Emoji 列表
     */
    private String id;
    /**
     * 表情类型 EmojiType
     * 1	系统表情
     * 2	emoji表情
     */
    private Integer type;
}