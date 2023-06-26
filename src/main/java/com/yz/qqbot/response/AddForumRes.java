package com.yz.qqbot.response;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:04 <br/>
 */
@Data
public class AddForumRes {
    /**
     * 帖子任务ID
     */
    private String task_id;
    /**
     * 发帖时间戳，单位：秒
     */
    private String create_time;
}
