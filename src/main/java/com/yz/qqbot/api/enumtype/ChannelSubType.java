package com.yz.qqbot.api.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/28 13:04 <br/>
 */
@Getter
@AllArgsConstructor
public enum ChannelSubType {
    NON(-1, "未知类型"),
    Chatting(0, "闲聊"),
    Announcement(1, "公告"),
    Introduction(2, "攻略"),
    OpenBlack(3, "开黑");
    private final int code;
    private final String desc;

    public static ChannelSubType getSubType(int code) {
        return Arrays.stream(values())
                .filter(event -> Objects.equals(code, event.getCode()))
                .findFirst().orElse(NON);
    }
}
