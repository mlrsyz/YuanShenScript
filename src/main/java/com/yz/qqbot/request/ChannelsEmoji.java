package com.yz.qqbot.request;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:56 <br/>
 */
@Data
public class ChannelsEmoji {
    /**
     * 子频道ID
     */
    private String channel_id;
    /**
     * 消息ID
     */
    private String message_id;
    /**
     * 表情类型，参考  <a href="https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#EmojiType">EmojiType列表</a>
     */
    private Integer type;
    /**
     * 表情ID，参考 <a href="https://bot.q.qq.com/wiki/develop/api/openapi/emoji/model.html#Emoji%20%E5%88%97%E8%A1%A8">Emoji</a> 列表
     */
    private Integer id;
    /**
     * 上次请求返回的cookie，第一次请求无需填写
     */
    private String cookie;
    /**
     * 每次拉取数量，默认20，最多50，只在第一次请求时设置
     */
    private Integer limit;
    /**
     * 操作类型 1:add 2:删除 内部处理无需设置
     */
    private Integer operationType = 1;
}
