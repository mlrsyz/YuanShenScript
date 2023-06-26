package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 推荐子频道对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:20 <br/>
 */
@Data
public class RecommendChannel {
    /**
     * 子频道 id
     */
    private String channel_id;
    /**
     * 推荐语
     */
    private String introduce;
}
