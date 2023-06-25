package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:08 <br/>
 */
@Data
public class MessageMarkdownParams {
    //markdown 模版 key
    private String key;
    //类型的数组	markdown 模版 key 对应的 values ，列表长度大小为 1，传入多个会报错
    private String values;
}
