package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 创建子频道
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 2:03 <br/>
 */
@Data
public class UpdateChannel {
    /**
     * 子频道名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer position;
    /**
     * 分组 id
     */
    private String parent_id;
    /**
     * 子频道私密类型 PrivateType
     */
    private Integer private_type;
    /**
     * 子频道发言权限 SpeakPermission
     */
    private Integer speak_permission;
}
