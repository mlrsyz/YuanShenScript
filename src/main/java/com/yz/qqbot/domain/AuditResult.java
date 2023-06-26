package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 论坛帖子审核结果事件
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:44 <br/>
 */
@Data
public class AuditResult {
    /**
     * 频道ID
     */
    private String guild_id;
    /**
     * 子频道ID
     */
    private String channel_id;
    /**
     * 作者ID
     */
    private String author_id;
    /**
     * 主题ID
     */
    private String thread_id;
    /**
     * 帖子ID
     */
    private String post_id;
    /**
     * 回复ID
     */
    private String reply_id;
    /**
     * AuditType审核的类型
     * PUBLISH_THREAD	1	帖子
     * PUBLISH_POST	    2	评论
     * PUBLISH_REPLY	3	回复
     */
    private Integer type;
    /**
     * 审核结果. 0:成功 1:失败
     */
    private Integer result;
    /**
     * result不为0时错误信息
     */
    private String err_msg;
}
