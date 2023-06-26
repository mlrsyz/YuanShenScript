package com.yz.qqbot.request;

import com.yz.qqbot.domain.APIPermission;
import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:24 <br/>
 */
@Data
public class ApiAuthorization {
    /**
     * 授权链接发送的子频道 id
     */
    private String channel_id;
    /**
     * 对象	api 权限需求标识对象
     */
    private APIPermission.APIPermissionDemandIdentify api_identify;
    /**
     * 机器人申请对应的 API 接口权限后可以使用功能的描述
     */
    private String desc;
}
