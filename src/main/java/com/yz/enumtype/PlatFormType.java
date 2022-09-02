package com.yz.enumtype;

import com.yz.script.BiBiScript;
import com.yz.script.DouYuScript;
import com.yz.script.HuYaScript;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ymx
 * @apiNote 平台枚举
 **/
@AllArgsConstructor
public enum PlatFormType {
    BiLiBiLi(BiBiScript.class.getSimpleName()),
    HuYa(HuYaScript.class.getSimpleName()),
    DouYu(DouYuScript.class.getSimpleName());

    @Getter
    final String className;
}
