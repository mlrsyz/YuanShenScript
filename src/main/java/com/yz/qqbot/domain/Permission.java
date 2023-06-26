package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:26 <br/>
 */
@Data
public class Permission {
    /**
     * 权限类型，参考 PermissionType
     */
    private int type;
    /**
     * 数组	有权限的身份组id的列表
     */
    private List<String> specify_role_ids;
    /**
     * 数组	有权限的用户id的列表
     */
    private List<String> specify_user_ids;
}
