package com.yz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * 作者：ymx <br/>
 * 创建时间：2022/5/15 18:01 <br/>
 */
@Slf4j
@SpringBootApplication
public class YuanShenApplication {

    //程序执行主main入口
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        log.info("### 服务启动完成 === " + SpringApplication.run(YuanShenApplication.class, args));
    }


}
