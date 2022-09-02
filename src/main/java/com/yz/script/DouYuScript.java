package com.yz.script;

import com.yz.enumtype.ActivityType;
import com.yz.enumtype.PlatFormType;
import com.yz.enumtype.RequestEntry;
import com.yz.util.ScriptUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ymx
 * @apiNote 斗鱼脚本
 **/
public class DouYuScript extends Script {
    //领取奖励url
    private static final String receiveUrl = "https://www.douyu.com/japi/carnival/nc/roomTask/getPrize";

    @Override
    public void initData() {
        //斗鱼的线程设置少一点 推荐1-5即可
        threadNum = 1;
        if (StringUtils.isEmpty(cookie)) {
            sendMessage("请设置cookie信息!");
            goOn = false;
            return;
        }
        //设置cookieData运行数据
        cookieData.putAll(Arrays.stream(cookie.split(";"))
                .map(String::trim)
                .filter(s -> s.contains("="))
                .collect(Collectors.toMap(v -> v.substring(0, v.indexOf("=")), v -> v.substring(v.indexOf("=") + 1))));
        if (cookieData.size() < 3) {
            goOn = false;
        }
    }

    @Override
    public void preExecute() {

    }

    @Override
    public void run() {
        String postResult = ScriptUtils.sendPost(receiveUrl, getRequestData(RequestEntry.param, activityType), getRequestData(RequestEntry.header, activityType));
        sendMessage(postResult);
        goOn = false;
    }

    @Override
    public PlatFormType platFormType() {
        return PlatFormType.DouYu;
    }

    //获取信息的参数和header
    private Map<String, String> getRequestData(RequestEntry requestEntry, ActivityType activityType) {
        Map<String, String> res = new HashMap<>();
        if (RequestEntry.header.equals(requestEntry)) {
            res.put("authority", "www.douyu.com");
            res.put("accept", "application/json, text/plain, */*");
            res.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            res.put("content-type", "application/x-www-form-urlencoded");
            res.put("cookie", cookie);
            res.put("origin", "https://www.douyu.com");
            res.put("referer", "https://www.douyu.com/topic/ys30?rid=479079");
            res.put("sec-ch-ua", "Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Microsoft Edge\";v=\"104\"");
            res.put("sec-ch-ua-mobile", "?0");
            res.put("sec-ch-ua-platform", "macOS");
            res.put("sec-fetch-dest", "empty");
            res.put("sec-fetch-mode", "cors");
            res.put("sec-fetch-site", "same-origin");
            res.put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.102 Safari/537.36 Edg/104.0.1293.63");
            res.put("x-dy-traceid", "159f077d292b67b0:159f077d292b67b0:0:008521");
            res.put("x-requested-with", "XMLHttpRequest");
        }
        if (RequestEntry.param.equals(requestEntry)) {
            res.put("taskId", activityType.getTask_id());
        }
        return res;
    }
}
