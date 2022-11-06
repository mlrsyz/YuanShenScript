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
        //b站限制了30s/116次请求  就被被拒绝访问 写死1个线程跑吧...
        threadNum = 1;
        sleepTime = 285L;
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
                sendMessage("----->bilibili直播时长完成!");
                break;
            }
            sendMessage("bilibili直播时长还未完成,刷新中....");
            ScriptUtils.sleep(sleepTime);
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
                    sendMessage("===============执行结束 继续任务请选择任务继续执行 断开连接才会释放该次任务实例==================");
                    log.info("执行结束 :{}", this);
                    return;
                }
            }
            String postResult = ScriptUtils.sendPost(receiveUrl, getRequestData(RequestEntry.param, UrlType.receive, activityType), getRequestData(RequestEntry.header, UrlType.receive, activityType));
            synchronized (BiBiScript.class) {
                if (!isExit()) {
                    sendMessage(postResult);
                    if (postResult.contains("已领取") || postResult.contains("领完")) {
                        if (LocalDateTime.now().isAfter(activityType.getStartTime())) {
                            cookieData.put("exit", "true");
                        }
                    } else if (postResult.contains("CdKeyV2")) {
                        cookieData.put("exit", "true");
                    }
                    sendMessage("时间:" + LocalTime.now() + "*********脚本剩余 " + count.getAndDecrement() + " 次执行---->" + postResult);
                }
            }
            ScriptUtils.sleep(sleepTime);
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
            res.put("sec-ch-ua", "\"Microsoft Edge\";v=\"107\", \"Chromium\";v=\"107\", \"Not=A?Brand\";v=\"24\"");
            res.put("sec-ch-ua-mobile", "?0");
            res.put("sec-ch-ua-platform", "\"Windows\"");
            res.put("sec-fetch-dest", "empty");
            res.put("sec-fetch-mode", "cors");
            res.put("sec-fetch-site", "same-site");
            res.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.35");
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
}