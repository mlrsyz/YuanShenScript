package Script;

import Enum.ActivityType;
import Enum.PlatFormType;
import Enum.RequestEntry;
import util.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        if (Utils.collectionIsEmpty(cookieMap)) {
            System.out.println(this.getClass().getSimpleName() + ": Please set cookie information!");
            goOn = false;
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

    }

    @Override
    public void run() {
        for (String name : cookieMap.keySet()) {
            String postResult = Utils.sendPost(receiveUrl, getRequestData(name, RequestEntry.param, activityType), getRequestData(name, RequestEntry.header, activityType));
            System.out.println(postResult);
        }
        goOn = false;
    }

    @Override
    public PlatFormType platFormType() {
        return PlatFormType.DouYu;
    }

    //获取信息的参数和header
    private Map<String, String> getRequestData(String userName, RequestEntry requestEntry, ActivityType activityType) {
        Map<String, String> res = new HashMap<>();
        if (RequestEntry.header.equals(requestEntry)) {
            res.put("authority", "www.douyu.com");
            res.put("accept", "application/json, text/plain, */*");
            res.put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            res.put("content-type", "application/x-www-form-urlencoded");
            res.put("cookie", cookieMap.get(userName));
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
