package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 私信数据
 * 作者：ymx <br/>
 * 创建时间：2023/6/25 19:06 <br/>
 */
@Data
public class Dms {
    private String guild_id;
    private String channel_id;
    private String create_time;
}
