package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 话题频道内对主题的评论称为帖子
 * 话题频道内对帖子主题评论或删除时生产事件中包含该对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:40 <br/>
 */
@Data
public class Post {
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
     * 帖子内容
     */
    private PostInfo post_info;

    @Data
    public  static class PostInfo {
        /**
         * 主题ID
         */
        private String thread_id;
        /**
         * 帖子ID
         */
        private String post_id;
        /**
         * 帖子内容
         */
        private String content;
        /**
         * 评论时间
         */
        private String date_time;
    }
}
