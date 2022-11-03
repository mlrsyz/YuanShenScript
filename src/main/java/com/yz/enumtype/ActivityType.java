package com.yz.enumtype;


import java.time.LocalDateTime;

/**
 * @author ymx
 * @apiNote 活动类型
 * 活动奖励卡片 时间格式(年-月-日-小时-分钟)规范填写
 * 原神活动里程碑数据 需要抓接口数据填写
 **/
public enum ActivityType {
    //===================⬇DouYu活动⬇=============================================================================================================================================================
    douYu_3_330(PlatFormType.DouYu, "", "", "135632", "", "",
            LocalDateTime.of(2022, 11, 4, 2, 0)),
    douYu_5_660(PlatFormType.DouYu, "", "", "135633", "", "",
            LocalDateTime.of(2022, 11, 6, 2, 0)),
    douYu_10_1000(PlatFormType.DouYu, "", "", "135634", "", "",
            LocalDateTime.of(2022, 11, 11, 2, 0)),
    douYu_18_800(PlatFormType.DouYu, "", "", "135635", "", "",
            LocalDateTime.of(2022, 11, 19, 2, 0)),
    douYu_26_800(PlatFormType.DouYu, "", "", "135636", "", "",
            LocalDateTime.of(2022, 11, 25, 2, 0)),
    douYu_35_1000(PlatFormType.DouYu, "", "", "135640", "", "",
            LocalDateTime.of(2022, 12, 4, 2, 0)),
    //==================⬇BiLiBiLi活动⬇==============================================================================================================================================================
    bibi_3_330_test(PlatFormType.BiLiBiLi, "e62ce499", "425", "1598", "7712", "missionPage",
            LocalDateTime.of(2022, 8, 26, 2, 0)),
    bibi_3_330(PlatFormType.BiLiBiLi, "3764e3ce", "498", "1914", "0", "missionPage",
            LocalDateTime.of(2022, 11, 4, 2, 0)),
    bibi_5_660(PlatFormType.BiLiBiLi, "5b5134d3", "498", "1915", "0", "missionPage",
            LocalDateTime.of(2022, 11, 6, 2, 0)),
    bibi_10_1000(PlatFormType.BiLiBiLi, "7e7b1dc3", "498", "1916", "0", "missionPage",
            LocalDateTime.of(2022, 11, 11, 2, 0)),
    bibi_18_800(PlatFormType.BiLiBiLi, "ae6d8373", "498", "1917", "0", "missionPage",
            LocalDateTime.of(2022, 11, 19, 2, 0)),
    bibi_26_800(PlatFormType.BiLiBiLi, "443ffdb6", "498", "1918", "0", "missionPage",
            LocalDateTime.of(2022, 11, 25, 2, 0)),
    bibi_35_1000(PlatFormType.BiLiBiLi, "6e983794", "498", "1919", "0", "missionPage",
            LocalDateTime.of(2022, 12, 4, 2, 0));
    //================================================================================================================================================================================
    final PlatFormType type;
    final String id;
    final String act_id;
    final String task_id;
    final String group_id;
    final String receive_from;
    //活动的开始时间
    final LocalDateTime startTime;

    public PlatFormType getType() {
        return type;
    }

    ActivityType(PlatFormType type, String id, String act_id, String task_id, String group_id, String receive_from, LocalDateTime startTime) {
        this.type = type;
        this.id = id;
        this.act_id = act_id;
        this.task_id = task_id;
        this.group_id = group_id;
        this.receive_from = receive_from;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public String getAct_id() {
        return act_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public String getReceive_from() {
        return receive_from;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}