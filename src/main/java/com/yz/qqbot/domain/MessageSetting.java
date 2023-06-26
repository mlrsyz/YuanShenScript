package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 频道消息频率设置对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 18:47 <br/>
 */
@Data
public class MessageSetting {
    /**
     * 是否允许创建私信
     */
    private String disable_create_dm;
    /**
     * 是否允许发主动消息
     */
    private String disable_push_msg;
    /**
     * 子频道 id 数组
     */
    private List<String> channel_ids;
    /**
     * 每个子频道允许主动推送消息最大消息条数
     */
    private String channel_push_max_num;
}
