package com.yz.controller;

import com.yz.enumtype.ActivityType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2022/9/2 23:53 <br/>
 */
@RequestMapping("/scripts")
@RestController
public class ScriptsController {

    @PostMapping("/getActivityType")
    public List<ActivityType> getActivityType() {
        return Arrays.asList(ActivityType.values());
    }
}
