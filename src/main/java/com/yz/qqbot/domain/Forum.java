package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 帖子
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:13 <br/>
 */
@Data
public class Forum {

    @Data
    public static class Thread {
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
         * 主帖内容
         */
        private ThreadInfo thread_info;
    }

    @Data
    public static class ThreadInfo {
        /**
         * 主帖ID
         */
        private String thread_id;
        /**
         * 帖子标题
         */
        private String title;
        /**
         * 帖子内容
         */
        private String content;
        /**
         * 发表时间
         */
        private String date_time;
    }
}
