package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 私信数据
 * 作者：ymx <br/>
 * 创建时间：2023/6/25 19:06 <br/>
 */
@Data
public class Dms {
    /**
     * 私信会话关联的频道 id
     */
    private String guild_id;
    /**
     * 私信会话关联的子频道 id
     */
    private String channel_id;
    /**
     * 创建私信会话时间戳
     */
    private String create_time;
}
