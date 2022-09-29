package com.yz.script;


import com.alibaba.fastjson.JSON;
import com.yz.domain.BRes;
import com.yz.enumtype.ActivityType;
import com.yz.enumtype.PlatFormType;
import com.yz.enumtype.RequestEntry;
import com.yz.enumtype.UrlType;
import com.yz.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ymx
 * @apiNote
 **/
@Slf4j
public class BiBiScript extends Script {
    //原神获取奖励信息前提数据url
    private static final String singleTaskUrl = "https://api.bilibili.com/x/activity/mission/single_task";
    //领取奖励url
    private static final String receiveUrl = "https://api.bilibili.com/x/activity/mission/task/reward/receive";

    @Override
    public void initData() {
        threadNum = 10;
        if (StringUtils.isEmpty(cookie)) {
            sendMessage("请设置cookie信息!执行结束");
            goOn = false;
            return;
        }
        if (!cookie.contains("SESSDATA")) {
            sendMessage("请检查 cookie 信息!执行结束");
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
            sendMessage("请检查 cookie 信息!执行结束");
        }
    }

    @Override
    public void preExecute() {
        //bibi的领奖前置数据条件  单独线程设置ReceiveId
        while (goOn) {
            String receiveIdNow = cookieData.get("receiveIdNow");
            String singleTaskResponse = ScriptUtils.sendGet(singleTaskUrl, getRequestData(RequestEntry.param, UrlType.singleTask, activityType), getRequestData(RequestEntry.header, UrlType.singleTask, activityType));
            if (!StringUtils.isEmpty(singleTaskResponse) && singleTaskResponse.contains("receive_id")) {
                final BRes response = JSON.parseObject(singleTaskResponse, BRes.class);
                receiveIdNow = Objects.nonNull(response) ? response.getData().getTask_info().getReceive_id().toString() : null;
            }
            if (receiveIdValid(receiveIdNow)) {
                cookieData.put("receiveIdNow", receiveIdNow);
                sendMessage("----->Live broadcast is complete!");
                break;
            }
            ScriptUtils.sleep(150);
        }
    }

    @Override
    public void run() {
        while (count.get() > 0) {
            synchronized (this) {
                if (!goOn) {
                    log.info("执行结束 :{}", this);
                    return;
                }
                if (isExit()) {
                    goOn = false;
                    sendMessage("===============执行结束==================");
                    log.info("执行结束 :{}", this);
                    if (cookieData.containsKey("ckd")) {
                        sendMessage(cookieData.getOrDefault("ckd", ""));
                    }
                    return;
                }
            }
            String postResult = ScriptUtils.sendPost(receiveUrl, getRequestData(RequestEntry.param, UrlType.receive, activityType), getRequestData(RequestEntry.header, UrlType.receive, activityType));
//            sendMessage(postResult);
            int status = 2;
            if (postResult.contains("请求过于频繁")) {
                status = -1;
            } else if (postResult.contains("任务奖励已领取") || postResult.contains("奖品已被领完")) {
                status = 1;
            } else if (postResult.contains("CdKeyV2")) {
                status = 0;
            }
            synchronized (BiBiScript.class) {
                if (!isExit()) {
                    switch (status) {
                        case -1:
                            sendMessage("频繁的请求:" + LocalTime.now() + "*********脚本剩余 " + count.getAndDecrement() + " 次执行");
                            break;
                        case 0:
                            cookieData.put("exit", "true");
                            cookieData.put("ckd", "移步活动平台查看cdk");
                            sendMessage("!!!!!!!!!!!成功抢到ckd:(第" + count.getAndDecrement() + "次抢到)" + LocalTime.now());
                            break;
                        case 1:
                            if (LocalDateTime.now().isAfter(activityType.getStartTime())) {
                                cookieData.put("exit", "true");
                            }
                            sendMessage("(第 " + count.getAndDecrement() + "次执行结束) 结束时间:" + LocalTime.now() + "---->" + postResult);
                            break;
                        default:
                            sendMessage("时间:" + LocalTime.now() + "*********脚本剩余 " + count.getAndDecrement() + " 次执行---->" + postResult);
                    }
                }
            }
//            ScriptUtils.sleep(sleepTime);
        }
    }

    @Override
    public PlatFormType platFormType() {
        return PlatFormType.BiLiBiLi;
    }

    //获取信息的参数和header
    private Map<String, String> getRequestData(RequestEntry requestEntry, UrlType urlType, ActivityType activityType) {
        Map<String, String> res = new HashMap<>();
        if (RequestEntry.header.equals(requestEntry)) {
            res.put("authority", "api.bilibili.com");
            res.put("accept", "application/json, text/plain, */*");
            res.put("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            if (UrlType.receive.equals(urlType)) {
                res.put("content-type", "application/x-www-form-urlencoded");
            }
            res.put("cookie", cookie);
            res.put("origin", "https://www.bilibili.com");
            res.put("referer", "https://www.bilibili.com/");
            res.put("sec-fetch-dest", "empty");
            res.put("sec-fetch-mode", "cors");
            res.put("sec-fetch-site", "same-site");
            res.put("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/101.0.4951.64");
        }
        if (RequestEntry.param.equals(requestEntry)) {
            res.put("csrf", cookieData.get("bili_jct"));
            if (UrlType.receive.equals(urlType)) {
                res.put("act_id", activityType.getAct_id());
                res.put("task_id", activityType.getTask_id());
                res.put("group_id", activityType.getGroup_id());
                res.put("receive_id", cookieData.get("receiveIdNow"));
                res.put("receive_from", activityType.getReceive_from());
            } else if (UrlType.singleTask.equals(urlType)) {
                res.put("id", activityType.getId());
            }
        }
        return res;
    }

    public static boolean receiveIdValid(String receiveIdNow) {
        return !StringUtils.isEmpty(receiveIdNow) && !"0".equals(receiveIdNow);
    }

    public boolean isExit() {
        return Boolean.parseBoolean(cookieData.get("exit"));
    }
}