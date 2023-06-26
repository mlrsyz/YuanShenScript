package com.yz.qqbot.response;

import com.yz.qqbot.domain.APIPermission;
import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:26 <br/>
 */
@Data
public class ApiAuthorizationRes {
    /**
     * 申请接口权限的频道 id
     */
    private String guild_id;
    /**
     * 申请接口权限的频道 id
     */
    private String channel_id;
    /**
     * 申请接口权限的频道 id
     */
    private APIPermission.APIPermissionDemandIdentify api_identify;
    /**
     * 申请接口权限的频道 id
     */
    private String title;
    /**
     * 申请接口权限的频道 id
     */
    private String desc;

}
