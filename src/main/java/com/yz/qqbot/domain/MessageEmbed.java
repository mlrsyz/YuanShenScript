package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 0:53 <br/>
 */
@Data
public class MessageEmbed {
    /**
     * 标题
     */
    private String title;
    /**
     * 消息弹窗内容
     */
    private String prompt;
    /**
     * 缩略图
     */
    private MessageEmbedThumbnail thumbnail;
    /**
     * embed 字段数据
     */
    private List<MessageEmbedField> fields;
}
