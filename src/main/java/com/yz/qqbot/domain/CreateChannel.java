package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 创建子频道
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 2:03 <br/>
 */
@Data
public class CreateChannel {
    /**
     * 子频道名称
     */
    private String name;
    /**
     * 子频道类型 ChannelType
     */
    private Integer type;
    /**
     * 子频道子类型 ChannelSubType
     */
    private Integer sub_type;
    /**
     * 子频道排序，必填；当子频道类型为 子频道分组（ChannelType=4）时，必须大于等于 2
     */
    private Integer position;
    /**
     * 子频道所属分组ID
     */
    private String parent_id;
    /**
     * 子频道私密类型 PrivateType
     */
    private Integer private_type;
    /**
     * 数组	子频道私密类型成员 ID
     */
    private List<String> private_user_ids;
    /**
     * 子频道发言权限 SpeakPermission
     */
    private Integer speak_permission;
    /**
     * 应用类型子频道应用 AppID，仅应用子频道需要该字段
     */
    private String application_id;
}
