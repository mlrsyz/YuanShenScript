package com.yz.qqbot.response;

import com.yz.qqbot.domain.Forum;
import lombok.Data;

import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 1:55 <br/>
 */
@Data
public class GetForums {
    /**
     * 帖子列表对象（返回值里面的content字段，可参照RichText结构）
     */
    private List<Forum.Thread> threads;
    /**
     * 是否拉取完毕(0:否；1:是)
     */
    private Integer is_finish;
}
