package com.yz.qqbot.request;

import com.yz.qqbot.domain.RecommendChannel;
import lombok.Data;

import java.util.List;

/**
 * 创建公告
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 21:30 <br/>
 */
@Data
public class CreateAnnounces {
    /**
     * 选填，消息 id，message_id 有值则优选将某条消息设置为成员公告
     */
    private String message_id;
    /**
     * 选填，子频道 id，message_id 有值则为必填。
     */
    private String channel_id;
    /**
     * 选填，公告类别 0:成员公告，1:欢迎公告，默认为成员公告
     */
    private Integer announces_type;
    /**
     * 选填，推荐子频道列表，会一次全部替换推荐子频道列表
     */
    private List<RecommendChannel> recommend_channels;
}
