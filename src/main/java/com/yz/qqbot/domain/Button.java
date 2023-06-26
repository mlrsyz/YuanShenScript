package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:23 <br/>
 */
@Data
public class Button {
    /**
     * 按钮 id
     */
    private String id;
    /**
     * 按纽渲染展示对象	用于设定按钮的显示效果
     */
    private RenderData render_data;
    /**
     * 该按纽操作相关字段	用于设定按钮点击后的操作
     */
    private Action action;
}
