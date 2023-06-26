package com.yz.qqbot.response;

import com.yz.qqbot.domain.User;
import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/26 23:21 <br/>
 */
@Data
public class QueryChannelsEmoji {
    /**
     * 用户对象，参考 User，会返回 id, username, avatar
     */
    private List<User> users;
    /**
     * 分页参数，用于拉取下一页
     */
    private String cookie;
    /**
     * 是否已拉取完成到最后一页，true代表完成
     */
    private Boolean is_end;
}
