package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 0:59 <br/>
 */
@Data
public class MessageArk {
    /**
     * ark模板id（需要先申请）
     */
    private Integer template_id;
    /**
     * arkkv数组	kv值列表 #MessageArkKv
     */
    private List<MessageAkrKv> kv;
}
