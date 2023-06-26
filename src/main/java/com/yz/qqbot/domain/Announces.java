package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 公告对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:19 <br/>
 */
@Data
public class Announces {
    /**
     * 频道 id
     */
    private String guild_id;
    /**
     * 子频道 id
     */
    private String channel_id;
    /**
     * 消息 id
     */
    private String message_id;
    /**
     * 公告类别 0:成员公告 1:欢迎公告，默认成员公告
     */
    private Integer announces_type;
    /**
     * 数组	推荐子频道详情列表
     */
    private List<RecommendChannel> recommend_channels;
}
