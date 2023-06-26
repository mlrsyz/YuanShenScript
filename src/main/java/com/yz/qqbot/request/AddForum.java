package com.yz.qqbot.request;

import lombok.Data;

/**
 * 发表帖子
 * 作者：ymx <br/>
 * 创建时间：2023/6/27 2:01 <br/>
 */
@Data
public class AddForum {
    /**
     * 帖子标题
     */
    private String title;
    /**
     * 帖子内容
     */
    private String content;
    /**
     * 帖子文本格式
     * FORMAT_TEXT	    1	普通文本
     * FORMAT_HTML	    2	HTML
     * FORMAT_MARKDOWN	3	Markdown
     * FORMAT_JSON	    4	JSON（content参数可参照RichText结构）
     */
    private Integer format;
}
