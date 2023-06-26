package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:22 <br/>
 */
@Data
public class InlineKeyboardRow {
    /**
     * 数组的一项代表一个按钮，每个 InlineKeyboardRow 最多含有 5 个 Button
     */
    private List<Button> buttons;
}
