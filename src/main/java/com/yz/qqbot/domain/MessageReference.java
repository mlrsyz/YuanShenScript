package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:05 <br/>
 */
@Data
public class MessageReference {
    //需要引用回复的消息 id
    private String message_id;
    //是否忽略获取引用消息详情错误，默认否
    private Boolean ignore_get_message_error;
}
