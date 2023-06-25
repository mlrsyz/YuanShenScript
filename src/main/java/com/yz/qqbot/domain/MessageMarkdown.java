package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:07 <br/>
 */
@Data
public class MessageMarkdown {
    //markdown 模板 id
    private int template_id;
    //markdown 自定义模板 id
    private String custom_template_id;
    //markdown 模板模板参数
    private MessageMarkdownParams params;
    //原生 markdown 内容,与上面三个参数互斥,参数都传值将报错。
    private String content;
}
