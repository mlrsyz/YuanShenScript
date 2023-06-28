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
     * 0	    文字子频道
     * 1	    保留，不可用
     * 2	    语音子频道
     * 3	    保留，不可用
     * 4	    子频道分组
     * 10005	直播子频道
     * 10006	应用子频道
     * 10007	论坛子频道
     */
    private Integer type;
    /**
     * 子频道子类型 ChannelSubType
     * 0	闲聊
     * 1	公告
     * 2	攻略
     * 3	开黑
     */
    private Integer sub_type;
    /**
     * 排序值，具体请参考 有关 position 的说明
     * 从 1 开始
     * 当子频道类型为 子频道分组（ChannelType=4）时，由于 position 1 被未分组占用，所以 position 只能从 2 开始
     * 如果不传默认追加到分组下最后一个
     * 如果填写一个已经存在的值，那么会插入在原来的元素之前
     * 如果填写一个较大值，与不填是相同的表现，同时存储的值会根据真实的 position 进行重新计算，并不会直接使用传入的值
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
     * 0	公开频道
     * 1	群主管理员可见
     * 2	群主管理员+指定成员，可使用 修改子频道权限接口 指定成员
     */
    private Integer private_type;
    /**
     * 子频道发言权限 SpeakPermission
     * 0	无效类型
     * 1	所有人
     * 2	群主管理员+指定成员，可使用 修改子频道权限接口 指定成员
     */
    private Integer speak_permission;
    /**
     * 用于标识应用子频道应用类型，仅应用子频道时会使用该字段，具体定义请参考 应用子频道的应用类型
     * 1000000	王者开黑大厅
     * 1000001	互动小游戏
     * 1000010	腾讯投票
     * 1000051	飞车开黑大厅
     * 1000050	日程提醒
     * 1000070	CoDM 开黑大厅
     * 1010000	和平精英开黑大厅
     */
    private String application_id;
    /**
     * 用户拥有的子频道权限 Permissions
     */
    private String permissions;
    /**
     * 操作人
     */
    private String op_user_id;
}
