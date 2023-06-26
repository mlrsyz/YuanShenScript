package com.yz.qqbot.request;

import lombok.Data;

/**
 * 用于修改频道 guild_id 下 role_id 指定的身份组
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 22:07 <br/>
 */
@Data
public class UpdateGuildRoles {
    /**
     * 频道id
     */
    private String guild_id;
    /**
     * 角色ID
     */
    private String role_id;
    /**
     * 名称(非必填)
     */
    private String name;
    /**
     * ARGB 的 HEX 十六进制颜色值转换后的十进制数值(非必填)
     */
    private Integer color;
    /**
     * 在成员列表中单独展示: 0-否, 1-是(非必填)
     */
    private Integer hoist;
}
