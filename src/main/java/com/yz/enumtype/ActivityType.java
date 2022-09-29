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
    douYu_3_330(PlatFormType.DouYu, "", "", "126315", "", "",
            LocalDateTime.of(2022, 8, 26, 2, 0)),
    douYu_5_660(PlatFormType.DouYu, "", "", "126316", "", "",
            LocalDateTime.of(2022, 8, 28, 2, 0)),
    douYu_10_1000(PlatFormType.DouYu, "", "", "126317", "", "",
            LocalDateTime.of(2022, 9, 2, 2, 0)),
    douYu_18_800(PlatFormType.DouYu, "", "", "126318", "", "",
            LocalDateTime.of(2022, 9, 10, 2, 0)),
    douYu_26_800(PlatFormType.DouYu, "", "", "126319", "", "",
            LocalDateTime.of(2022, 9, 18, 2, 0)),
    douYu_35_1000(PlatFormType.DouYu, "", "", "126323", "", "",
            LocalDateTime.of(2022, 9, 27, 2, 0)),
    //==================⬇BiLiBiLi活动⬇==============================================================================================================================================================
    bibi_3_330_test(PlatFormType.BiLiBiLi, "e62ce499", "425", "1598", "7712", "missionLandingPage",
            LocalDateTime.of(2022, 8, 26, 2, 0)),
    bibi_3_330(PlatFormType.BiLiBiLi, "2aa3293e", "454", "1773", "7973", "missionLandingPage",
            LocalDateTime.of(2022, 9, 30, 2, 0)),
    bibi_5_660(PlatFormType.BiLiBiLi, "52bab58a", "454", "1774", "7974", "missionLandingPage",
            LocalDateTime.of(2022, 10, 2, 2, 0)),
    bibi_10_1000(PlatFormType.BiLiBiLi, "76a756b4", "454", "1775", "7975", "missionLandingPage",
            LocalDateTime.of(2022, 10, 7, 2, 0)),
    bibi_18_800(PlatFormType.BiLiBiLi, "9d1d7ba3", "454", "1776", "7977", "missionLandingPage",
            LocalDateTime.of(2022, 10, 15, 2, 0)),
    bibi_26_800(PlatFormType.BiLiBiLi, "c0d9081e", "454", "1777", "7978", "missionLandingPage",
            LocalDateTime.of(2022, 10, 28, 2, 0)),
    bibi_35_1000(PlatFormType.BiLiBiLi, "eb86ebd5", "454", "1778", "7979", "missionLandingPage",
            LocalDateTime.of(2022, 11, 6, 2, 0));
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