package com.yz.qqbot.domain;

import lombok.Data;

/**
 * 作者：ymx <br/>
 * 创建时间：2023/5/11 1:24 <br/>
 */
@Data
public class RenderData {
    //按纽上的文字
    private String label;
    //点击后按纽上文字
    private String visited_label;
    //按钮样式，参考 RenderStyle
    private int style;
}
