package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 富文本内容
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:45 <br/>
 */
@Data
public class RichObject {
    /**
     * 富文本类型
     * TEXT	    1	普通文本
     * AT	    2	at信息
     * URL	    3	url信息
     * EMOJI	4	表情
     * CHANNEL	5	#子频道
     * VIDEO	10	视频
     * IMAGE	11	图片
     */
    private Integer type;
    /**
     * 文本
     */
    private TextInfo text_info;
    /**
     * @ 内容
     */
    private AtInfo at_info;
    /**
     * 链接
     */
    private URLInfo url_info;
    /**
     * 表情
     */
    private EmojiInfo emoji_info;
    /**
     * 提到的子频道
     */
    private ChannelInfo channel_info;

    @Data
    public static class TextInfo {

    }

    @Data
    public static class AtInfo {

    }

    @Data
    public static class URLInfo {

    }

    @Data
    public static class EmojiInfo {

    }

    @Data
    public static class ChannelInfo {

    }
}
