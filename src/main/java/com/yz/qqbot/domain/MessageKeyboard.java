package com.yz.qqbot.domain;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:20 <br/>
 */
public class MessageKeyboard {
    //keyboard 模板 id
 private String   id;
 //自定义 keyboard 内容,与 id 参数互斥,参数都传值将报错
    private InlineKeyboard content;
}
