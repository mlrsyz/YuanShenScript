package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 日程对象
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:17 <br/>
 */
@Data
public class Schedule {
    /**
     * 日程 id
     */
    private String id;
    /**
     * 日程名称
     */
    private String name;
    /**
     * 日程描述
     */
    private String description;
    /**
     * 日程开始时间戳(ms)
     */
    private String start_timestamp;
    /**
     * 日程结束时间戳(ms)
     */
    private String end_timestamp;
    /**
     * 创建者
     */
    private Member creator;
    /**
     * 日程开始时跳转到的子频道 id
     */
    private String jump_channel_id;
    /**
     * 日程提醒类型，取值参考RemindType
     * 提醒类型 id	描述
     * 0	不提醒
     * 1	开始时提醒
     * 2	开始前 5 分钟提醒
     * 3	开始前 15 分钟提醒
     * 4	开始前 30 分钟提醒
     * 5	开始前 60 分钟提醒
     */
    private String remind_type;
}
