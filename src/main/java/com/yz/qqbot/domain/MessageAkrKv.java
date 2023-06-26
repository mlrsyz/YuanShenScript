package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:01 <br/>
 */
@Data
public class MessageAkrKv {
    /**
     * key
     */
    private String key;
    /**
     * value
     */
    private String value;
    /**
     * arkobj类型的数组	ark obj类型的列表
     */
    private List<MessageArkObj> obj;
}
