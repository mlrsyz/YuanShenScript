package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 3:03 <br/>
 */
@Data
public class Role {
    /**
     * 身份组ID
     * 身份组ID默认值	描述
     * 1	全体成员
     * 2	管理员
     * 4	群主/创建者
     * 5	子频道管理员
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * ARGB的HEX十六进制颜色值转换后的十进制数值
     */
    private Integer color;
    /**
     * 是否在成员列表中单独展示: 0-否, 1-是
     */
    private Integer hoist;
    /**
     * 人数
     */
    private Integer number;
    /**
     * 成员上限
     */
    private Integer member_limit;
}
