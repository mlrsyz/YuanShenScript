package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 语音对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 23:24 <br/>
 */
@Data
public class AudioControl {
    /**
     * 音频数据的url status为0时传
     */
    private String audio_url;
    /**
     * 状态文本（比如：简单爱-周杰伦），可选，status为0时传，其他操作不传
     */
    private String text;
    /**
     * 播放状态，参考 STATUS
     * START	0	开始播放操作
     * PAUSE	1	暂停播放操作
     * RESUME	2	继续播放操作
     * STOP 	3	停止播放操作
     */
    private Integer status;
}
