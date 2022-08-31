package Script;

import Enum.ActivityType;
import Enum.PlatFormType;
import Enum.RequestEntry;
import Enum.UrlType;
import util.Utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ymx
 * @apiNote
 **/
public class BiBiScript extends Script {
    //原神获取奖励信息前提数据url
    private static final String singleTaskUrl = "https://api.bilibili.com/x/activity/mission/single_task";
    //领取奖励url
    private static final String receiveUrl = "https://api.bilibili.com/x/activity/mission/task/reward/receive";

    @Override
    public void initData() {
        if (Utils.collectionIsEmpty(cookieMap)) {
            System.out.println(this.getClass().getSimpleName() + ": Please set cookie information!");
            goOn = false;
            return;
        }
        for (Iterator<Map.Entry<String, String>> iterator = cookieMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> entry = iterator.next();
            if (Utils.stringIsEmpty(entry.getKey()) ||
                    Utils.stringIsEmpty(entry.getValue()) ||
                    !entry.getValue().contains("SESSDATA")) {
                iterator.remove();
                System.out.println(this.getClass().getSimpleName() + ": Please check the cookie information of " + entry.getKey());
            }
        }
        if (Utils.collectionIsEmpty(cookieMap)) {
            goOn = false;
            System.out.println(this.getClass().getSimpleName() + ": Please set the correct cookie information!");
            return;
        }
        //设置cookieData运行数据
        cookieData.putAll(cookieMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Arrays.stream(entry.getValue().split(";"))
                                .map(String::trim)
                                .filter(s -> s.contains("="))
                                .collect(Collectors.toMap(v -> v.substring(0, v.indexOf("=")), v -> v.substring(v.indexOf("=") + 1))))));
        for (Map.Entry<String, Map<String, String>> entry : cookieData.entrySet()) {
            if (Objects.isNull(entry.getValue()) || Utils.collectionIsEmpty(entry.getValue()) || entry.getValue().size() < 3) {
                goOn = false;
                System.out.println(entry.getKey() + " : cookie error!");
            } else {
                System.out.println(entry.getKey() + " : cookie ok!");
            }
        }

    }

    @Override
    public void preExecute() {
        //bibi的领奖前置数据条件  单独线程设置ReceiveId
        new Thread(() -> {
            while (true) {
                for (String name : cookieMap.keySet()) {
                    if (isExitByName(name)) {
                        continue;
                    }
                    String receiveIdNow = cookieData.get(name).get("receiveIdNow");
                    if (receiveIdValid(receiveIdNow)) {
                        continue;
                    }
                    String singleTaskResponse = Utils.sendGet(singleTaskUrl, getRequestData(name, RequestEntry.param, UrlType.singleTask, activityType), getRequestData(name, RequestEntry.header, UrlType.singleTask, activityType));
                    if (!Utils.stringIsEmpty(singleTaskResponse) && singleTaskResponse.contains("receive_id")) {
                        receiveIdNow = Arrays.stream(singleTaskResponse.split(",")).filter(t -> t.contains("receive_id"))
                                .map(t -> t.split(":")[1]).findFirst().orElse(null);
                    }
                    if (receiveIdValid(receiveIdNow)) {
                        cookieData.get(name).put("receiveIdNow", receiveIdNow);
                        System.out.println(name + "----->Live broadcast is complete!");
                    }
                }
                if (cookieData.values().stream()
                        .map(map -> map.get("receiveIdNow"))
                        .noneMatch(receive_id -> Utils.stringIsEmpty(receive_id) || "0".equals(receive_id))) {
                    break;
                }
                Utils.sleep(150);
            }
        }).start();
    }

    @Override
    public void run() {
        while (count.get() > 0 && goOn) {
            if (isExitAll()) {
                synchronized (BiBiScript.class) {
                    if (!goOn) {
                        return;
                    }
                    goOn = false;
                    System.out.println(this.getClass().getSimpleName() + "===============脚本执行结束=============================================================================================================================================================================================");
                    cookieData.forEach((key, value) -> System.out.println(key + "------>" + value.get("ckd")));
                    return;
                }
            }
            for (String name : cookieMap.keySet()) {
                if (isExitByName(name)) {
                    continue;
                }
                String receive_id = cookieData.get(name).get("receiveIdNow");
                if (!receiveIdValid(receive_id)) {
                    continue;
                }
                String postResult = Utils.sendPost(receiveUrl, getRequestData(name, RequestEntry.param, UrlType.receive, activityType), getRequestData(name, RequestEntry.header, UrlType.receive, activityType));
                //System.out.println(postResult);
                //q请求一次算一次
                int status = 2;
                if (postResult.contains("请求过于频繁")) {
                    status = -1;
                } else if (postResult.contains("任务奖励已领取") || postResult.contains("奖品已被领完")) {
                    status = 1;
                } else if (postResult.contains("CdKeyV2")) {
                    status = 0;
                }
                synchronized (cookieMap.get(name)) {
                    if (!isExitByName(name)) {
                        switch (status) {
                            case -1:
                                System.out.println(name + "--frequent requests--now time:" + LocalTime.now() + "*********The script has " + count.getAndDecrement() + " execution remaining");
                                break;
                            case 0:
                                cookieData.get(name).put("exit", "true");
                                cookieData.get(name).put("ckd", "移步活动平台查看cdk");
                                System.out.println(name + "!!!!!!!!!!!successfully grabbed ckd:(" + count.getAndDecrement() + "nd time)" + LocalTime.now());
                                break;
                            case 1:
                                if (LocalDateTime.now().isAfter(activityType.getStartTime())) {
                                    cookieData.get(name).put("exit", "true");
                                }
                                System.out.println(name + "(The " + count.getAndDecrement() + "rd execution ended) the end time:" + LocalTime.now() + "---->" + postResult);
                                break;
                            default:
                                System.out.println(name + " time:" + LocalTime.now() + "*********The script has " + count.getAndDecrement() + " execution remaining---->" + postResult);
                        }
                    }
                }
            }
            Utils.sleep(sleepTime);
        }
    }

    @Override
    public PlatFormType platFormType() {
        return PlatFormType.BiLiBiLi;
    }

    //获取信息的参数和header
    private Map<String, String> getRequestData(String userName, RequestEntry requestEntry, UrlType urlType, ActivityType activityType) {
        Map<String, String> res = new HashMap<>();
        if (RequestEntry.header.equals(requestEntry)) {
            res.put("authority", "api.bilibili.com");
            res.put("accept", "application/json, text/plain, */*");
            res.put("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            if (UrlType.receive.equals(urlType)) {
                res.put("content-type", "application/x-www-form-urlencoded");
            }
            res.put("cookie", cookieMap.get(userName));
            res.put("origin", "https://www.bilibili.com");
            res.put("referer", "https://www.bilibili.com/");
            res.put("sec-fetch-dest", "empty");
            res.put("sec-fetch-mode", "cors");
            res.put("sec-fetch-site", "same-site");
            res.put("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/101.0.4951.64");
        }
        if (RequestEntry.param.equals(requestEntry)) {
            res.put("csrf", cookieData.get(userName).get("bili_jct"));
            if (UrlType.receive.equals(urlType)) {
                res.put("act_id", activityType.getAct_id());
                res.put("task_id", activityType.getTask_id());
                res.put("group_id", activityType.getGroup_id());
                res.put("receive_id", cookieData.get(userName).get("receiveIdNow"));
                res.put("receive_from", activityType.getReceive_from());
            } else if (UrlType.singleTask.equals(urlType)) {
                res.put("id", activityType.getId());
            }
        }
        return res;
    }

    public static boolean receiveIdValid(String receiveIdNow) {
        return !Utils.stringIsEmpty(receiveIdNow) && !"0".equals(receiveIdNow);
    }

    public boolean isExitByName(String name) {
        return Boolean.parseBoolean(cookieData.get(name).get("exit"));
    }

    public boolean isExitAll() {
        return cookieData.values().stream().allMatch(cookie -> Boolean.parseBoolean(cookie.get("exit")));
    }
}