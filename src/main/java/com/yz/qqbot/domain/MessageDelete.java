package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:09 <br/>
 */
@Data
public class MessageDelete {
    //被删除的消息内容
    private Message message;
    //执行删除操作的用户
    private User op_user;
}
