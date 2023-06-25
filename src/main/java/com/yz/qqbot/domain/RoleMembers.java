package com.yz.qqbot.domain;

import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:17 <br/>
 */
@Data
public class RoleMembers {
    /**
     * 一组用户信息对象
     */
    private List<Member> data;
    /**
     * 下一次请求的分页标识
     */
    private String next;
}
