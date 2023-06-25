package com.yz.qqbot.domain;

import lombok.Data;

/**
 * <a href="https://bot.q.qq.com/wiki/develop/api/openapi/channel/model.html">子频道对象</a>
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 1:43 <br/>
 */
@Data
public class Channel {
    /**
     * 子频道id
     */
    private String id;
    /**
     * 频道 id
     */
    private String guild_id;
    /**
     * 子频道名
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
     * 排序值，具体请参考 有关 position 的说明
     */
    private Integer position;
    /**
     * 所属分组 id，仅对子频道有效，对 子频道分组（ChannelType=4） 无效
     */
    private String parent_id;
    /**
     * 创建人 id
     */
    private String owner_id;
    /**
     * 子频道私密类型 PrivateType
     */
    private Integer private_type;
    /**
     * 子频道发言权限 SpeakPermission
     */
    private Integer speak_permission;
    /**
     * 用于标识应用子频道应用类型，仅应用子频道时会使用该字段，具体定义请参考 应用子频道的应用类型
     */
    private String application_id;
    /**
     * 用户拥有的子频道权限 Permissions
     */
    private String permissions;
}
