package com.yz.domain;

import com.yz.enumtype.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScriptReq {
    /**
     * cookie
     */
    private String cookie;

    /**
     * 活动
     */
    private ActivityType activityType;
    /**
     * 活动时间
     */
    private LocalDateTime dateTime;
}
