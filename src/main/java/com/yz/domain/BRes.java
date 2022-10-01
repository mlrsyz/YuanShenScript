package com.yz.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ymx
 * @apiNote bilibil活动DTO
 **/
@Data
public class BRes {
    private Integer code;
    private String message = "";
    private Integer ttl;
    private Result data = new Result();

    @Data
    public static class Result {
        private TaskInfo task_info = new TaskInfo();
    }

    @Data
    public static class TaskInfo {
        private Integer id;
        private Integer act_id;
        private List<GroupList> group_list = new ArrayList<>();
        private Integer receive_id = 0;
        private String task_name;
        private String task_receive_description;
    }

    @Data
    public static class GroupList {
        private Integer task_id;
        private Integer act_id;
        private Integer group_id;
        private Integer group_base_num;
        private Integer group_complete_num;
        private String act_counter_name;
    }
}
