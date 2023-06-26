package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 话题频道对帖子回复或删除时生产该事件中包含该对象
 * 话题频道内对帖子的评论称为回复
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:42 <br/>
 */
@Data
public class Reply {
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
     * 回复内容
     */
    private ReplyInfo reply_info;

    @Data
    public static class ReplyInfo {
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
         * 回复内容
         */
        private String content;
        /**
         * 回复时间
         */
        private String date_time;
    }
}
