package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 音频动作
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 23:27 <br/>
 */
@Data
public class AudioAction {
    /**
     * 频道id
     */
    private String guild_id;
    /**
     * 子频道id
     */
    private String channel_id;
    /**
     * 音频数据的url status为0时传
     */
    private String audio_url;
    /**
     * 状态文本（比如：简单爱-周杰伦），可选，status为0时传，其他操作不传
     */
    private String text;
}
