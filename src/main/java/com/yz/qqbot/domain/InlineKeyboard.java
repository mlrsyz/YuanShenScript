package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:22 <br/>
 */
@Data
public class InlineKeyboard {
    /**
     * 数组的一项代表消息按钮组件的一行,最多含有 5 行
     */
    private List<InlineKeyboardRow> rows;
    /**
     * 机器人id
     */
    private Long bot_appid;
}
