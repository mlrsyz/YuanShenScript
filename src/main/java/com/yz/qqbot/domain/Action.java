package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:25 <br/>
 */
@Data
public class Action {
    //操作类型，参考 ActionType
    private int type;
    //用于设定操作按钮所需的权限
    private Permission permission;
    //可点击的次数, 默认不限
    private int click_limit;
    //操作相关数据
    private String data;
    //false:不弹出子频道选择器 true:弹出子频道选择器
    private boolean at_bot_show_channel_list;
}
