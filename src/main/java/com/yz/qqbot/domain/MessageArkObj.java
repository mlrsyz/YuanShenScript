package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:03 <br/>
 */
@Data
public class MessageArkObj {
    /**
     * ark objkv列表
     */
    private List<MessageArkObjKv> obj_kv;
}
