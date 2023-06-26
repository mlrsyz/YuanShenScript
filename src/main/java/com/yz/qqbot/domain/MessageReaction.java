package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 表情表态对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:43 <br/>
 */
@Data
public class MessageReaction {
    /**
     * 用户ID
     */
    private String user_id;
    /**
     * 频道ID
     */
    private String guild_id;
    /**
     * 子频道ID
     */
    private String channel_id;
    /**
     * 表态对象
     */
    private ReactionTarget target;
    /**
     * 表态所用表情
     */
    private Emoji emoji;

    @Data
    static class ReactionTarget {
        /**
         * 表态对象ID
         */
        private String id;
        /**
         * 表态对象类型，参考 ReactionTargetType
         * 0	消息
         * 1	帖子
         * 2	评论
         * 3	回复
         */
        private Integer type;
    }
}
