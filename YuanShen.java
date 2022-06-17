// package com.yz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 作者：ymx <br/>
 * 创建时间：2022/5/15 18:01 <br/>
 */
public class YuanShen implements Runnable {
    //程序执行主main入口
    public static void main(String[] args) {
        YuanShen.startScript(ActivityType.ten_1000);
    }

    //b站用户cookie 网页获取 电脑进入下面链接 f12获取请求XHR请求single_task标头的cookie
    //数据格式 key(不重复的标识符):value(有效的用户cookie)
    //https://www.bilibili.com/blackboard/activity-award-exchange.html?task_id=cd684a9d
    private static final Map<String, String> cookieMap = new HashMap<String, String>() {{
        put("ytd", "buvid3=E9BFF8A5-FBBF-DB65-0D69-7ABB2A609A9481147infoc; _uuid=C8FE4C25-A756-5814-7E44-1A1A1247FA6280871infoc; buvid4=B26BD057-DE29-DC86-D59F-10C5D0C8EAC482237-022031619-kji2bknSwKco8f52F6rjzQu1hstbLjNPkiJRLc50Ghd04Ex55Uu24Q%3D%3D; rpdid=0zbfAHGGS5|SKQVpjnQ|3XA|3w1Nusda; LIVE_BUVID=AUTO5716475960417726; nostalgia_conf=-1; CURRENT_BLACKGAP=0; buvid_fp_plain=undefined; fingerprint3=e1300e98db6401ff20fa1b00d69641ed; i-wanna-go-back=-1; hit-dyn-v2=1; blackside_state=0; is-2022-channel=1; bp_video_offset_14051700=663437370565066900; CURRENT_QUALITY=120; bp_article_offset_102462368=661946626066088000; b_ut=5; bp_video_offset_4298545=668162753695842300; bp_article_offset_4298545=667675004331098200; fingerprint=8149e5cb2ae58043959cb7f6bdd1ebd5; DedeUserID=102462368; DedeUserID__ckMd5=76421ba71f67f422; buvid_fp=8149e5cb2ae58043959cb7f6bdd1ebd5; bp_video_offset_102462368=668139200108822500; PVID=2; SESSDATA=efc965ad%2C1670241490%2Cd9a28%2A61; bili_jct=a379da47b0c2ca327f4f5aaa7eb0e3b4; sid=kiup1qhq; innersign=1; CURRENT_FNVAL=80; b_lsid=215E4326_181436EE809; b_timer=%7B%22ffp%22%3A%7B%22444.45.fp.risk_E9BFF8A5%22%3A%22181431BC599%22%2C%22444.8.fp.risk_E9BFF8A5%22%3A%22181431C9F4F%22%2C%22333.967.fp.risk_E9BFF8A5%22%3A%2218143B14A01%22%2C%22888.45797.fp.risk_E9BFF8A5%22%3A%2218143B168D0%22%2C%22333.1007.fp.risk_E9BFF8A5%22%3A%22181432DE097%22%2C%22333.788.fp.risk_E9BFF8A5%22%3A%22181432E2BE6%22%2C%22888.59266.fp.risk_E9BFF8A5%22%3A%2218143B17DFE%22%7D%7D");
        put("lxy", "LIVE_BUVID=AUTO3315676072049185; rpdid=|(u)YJYkmm)~0J'ulY~Y|~JuY; buvid3=488DDBD5-C108-4D54-A4EE-1886F459C6C5155804infoc; _uuid=BDF9F661-3A1B-DED4-9600-7B5E84E4752D25433infoc; video_page_version=v_old_home; CURRENT_QUALITY=116; fingerprint_s=8eb37cd646568fa5169ebac35621fd58; buvid4=EBC13C78-A7B1-E13D-B5FE-68007860393B32734-022012119-kzJtxEKxhQdV7pYnCQQ/Sw%3D%3D; i-wanna-go-back=-1; CURRENT_BLACKGAP=0; buvid_fp_plain=undefined; fingerprint3=5370af94469be795c2cecf773118089c; fingerprint=4b6916867da41054a2d0fae33c4a7edb; blackside_state=0; b_ut=5; DedeUserID=4298545; DedeUserID__ckMd5=4f6932c38c9f902e; SESSDATA=dd8ca7da%2C1662220738%2C6e7a0*31; bili_jct=4d43f07588396fa5257bfaaab8bcb9b1; buvid_fp=4b6916867da41054a2d0fae33c4a7edb; nostalgia_conf=-1; PVID=1; bp_video_offset_4298545=666802430198939600; CURRENT_FNVAL=80; b_lsid=5AB657BC_1814405469E; b_timer=%7B%22ffp%22%3A%7B%22333.967.fp.risk_488DDBD5%22%3A%2218144055832%22%2C%22888.45797.fp.risk_488DDBD5%22%3A%22181440574E9%22%7D%7D");
    }};
    //原神获取奖励信息前提数据url
    private static final String singleTaskUrl = "https://api.bilibili.com/x/activity/mission/single_task";
    //领取奖励url
    private static final String receiveUrl = "https://api.bilibili.com/x/activity/mission/task/reward/receive";
    //同时执行的线程数量   最多同时执行50个
    private static int threadNum = 30;

    //哔哩哔哩的活动奖励卡片
    //元神活动里程碑数据 需要抓接口数据填写
    enum ActivityType {
        three_330("87488df4", "334", "1145", "6043", "missionLandingPage",
                LocalDateTime.of(2022, 6, 2, 2, 0)),
        five_660("eb1abec4", "334", "1146", "6044", "missionLandingPage",
                LocalDateTime.of(2022, 6, 5, 0, 0)),
        ten_1000("afca6e9e", "334", "1147", "6045", "missionLandingPage",
                LocalDateTime.of(2022, 6, 9, 2, 0)),
        twenty_1000("faeb521d", "334", "1148", "6046", "missionLandingPage",
                LocalDateTime.of(2022, 6, 19, 2, 0)),
        thirty_1000("2a675c72", "334", "1149", "6047", "missionLandingPage",
                LocalDateTime.of(2022, 6, 29, 2, 0)),
        forty_1000("b1498697", "334", "1150", "6048", "missionLandingPage",
                LocalDateTime.of(2022, 7, 9, 2, 0)),
        test("8adc9750", "315", "1078", "5627", "missionLandingPage",
                LocalDateTime.of(2022, 5, 9, 2, 0));
        final String id;
        final String act_id;
        final String task_id;
        final String group_id;
        final String receive_from;
        //活动的开始时间
        final LocalDateTime startTime;

        ActivityType(String id, String act_id, String task_id, String group_id, String receive_from, LocalDateTime startTime) {
            this.id = id;
            this.act_id = act_id;
            this.task_id = task_id;
            this.group_id = group_id;
            this.receive_from = receive_from;
            this.startTime = startTime;
        }
    }

    /**
     * 执行脚本主入口
     *
     * @param type 要抢的原石里程碑类型
     */
    public static void startScript(ActivityType type) {
        new YuanShen(type);
    }

    public YuanShen(ActivityType activityType) {
        if (Objects.isNull(activityType)) {
            System.out.println("Please set the activity type!");
            System.exit(0);
        }
        this.activityType = activityType;
        LocalDateTime localTime = activityType.startTime.minusSeconds(3);
        System.out.println("Event start time " + activityType.startTime + "-----script execution time " + localTime);
        if (CollectionUtils.isEmpty(cookieMap)) {
            System.out.println("Please set cookie information!");
            System.exit(0);
        }
        for (Iterator<Map.Entry<String, String>> iterator = cookieMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, String> entry = iterator.next();
            if (StringUtils.isEmpty(entry.getKey()) ||
                    StringUtils.isEmpty(entry.getValue()) ||
                    !entry.getValue().contains("SESSDATA")) {
                iterator.remove();
                System.out.println("Please check the cookie information of " + entry.getKey());
            }
        }
        if (CollectionUtils.isEmpty(cookieMap)) {
            System.out.println("Please set the correct cookie information!");
            System.exit(0);
        }
        //设置cookieData运行数据
        cookieData.putAll(cookieMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Arrays.stream(entry.getValue().split(";")).map(String::trim)
                                .collect(Collectors.toMap(v -> v.substring(0, v.indexOf("=")),
                                        v -> v.substring(v.indexOf("=") + 1))))));
        //判断开始执行时间 提前3s执行
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(localTime)) {
                if (now.getSecond() % 25 == 0) {
                    long millis = Duration.between(now, localTime).toMillis() / 1000;
                    long day = millis / 86400;
                    long hour = millis % 86400 / 3600;
                    long minute = millis % 86400 % 3600 / 60;
                    long second = millis % 60;
                    System.out.println("surplus [" + day + " day " + hour + ":" + minute + ":" + second + "]");
                }
                sleep(1000);
            } else {
                System.out.println("script starts executing start time " + now + "===========================================================================================================================================================================================================================================================================================================");
                break;
            }
        }
        //单独线程设置ReceiveId
        new Thread(() -> {
            while (true) {
                for (String name : cookieMap.keySet()) {
                    if (StringUtils.isExitByName(name)) {
                        continue;
                    }
                    String receiveIdNow = cookieData.get(name).get("receiveIdNow");
                    if (StringUtils.receiveIdValid(receiveIdNow)) {
                        continue;
                    }
                    String singleTaskResponse = sendGet(singleTaskUrl, getRequestData(name, RequestEntry.param, UrlType.singleTask, activityType), getRequestData(name, RequestEntry.header, UrlType.singleTask, activityType));
                    if (!StringUtils.isEmpty(singleTaskResponse) && singleTaskResponse.contains("receive_id")) {
                        receiveIdNow = Arrays.stream(singleTaskResponse.split(",")).filter(t -> t.contains("receive_id"))
                                .map(t -> t.split(":")[1]).findFirst().orElse(null);
                    }
                    if (StringUtils.receiveIdValid(receiveIdNow)) {
                        cookieData.get(name).put("receiveIdNow", receiveIdNow);
                        System.out.println(name + "----->Live broadcast is complete!");
                    }
                }
                if (cookieData.values().stream()
                        .map(map -> map.get("receiveIdNow"))
                        .noneMatch(receive_id -> StringUtils.isEmpty(receive_id) || "0".equals(receive_id))) {
                    break;
                }
                sleep(150);
            }
        }).start();
        //线程开始执行
        for (int i = 0; i < (threadNum = Math.min(threadNum, 50)); i++) {
            ysThreadPoolExecutor.submit(this);
        }
        ysThreadPoolExecutor.shutdown();
    }

    @Override
    public void run() {
        while (count.get() > 0) {
            if (StringUtils.isExitAll()) {
                synchronized (this) {
                    System.out.println("===============脚本执行结束=============================================================================================================================================================================================");
                    cookieData.forEach((key, value) -> System.out.println(key + "------>" + value.get("ckd")));
                    System.exit(0);
                }
            }
            for (String name : cookieMap.keySet()) {
                if (StringUtils.isExitByName(name)) {
                    continue;
                }
                String receive_id = cookieData.get(name).get("receiveIdNow");
                if (!StringUtils.receiveIdValid(receive_id)) {
                    continue;
                }
                String postResult = sendPost(receiveUrl, getRequestData(name, RequestEntry.param, UrlType.receive, activityType), getRequestData(name, RequestEntry.header, UrlType.receive, activityType));
                //System.out.println(postResult);
                //q请求一次算一次
                int status = 2;
                if (postResult.contains("请求过于频繁")) {
                    status = -1;
                } else if (postResult.contains("任务奖励已领取") || postResult.contains("奖品已被领完")) {
                    status = 1;
                } else if (postResult.contains("cdkey_content")) {
                    status = 0;
                }
                synchronized (cookieMap.get(name)) {
                    if (!StringUtils.isExitByName(name)) {
                        switch (status) {
                            case -1:
                                System.out.println(name + "--frequent requests--now time:" + LocalTime.now() + "*********The script has " + count.getAndDecrement() + " execution remaining");
                                break;
                            case 0:
                                try {
                                    cookieData.get(name).put("exit", "true");
                                    String cdKeyContent = postResult.substring(postResult.indexOf("cdkey_content"));
                                    String ckd = cdKeyContent.substring(cdKeyContent.indexOf(":") + 1, cdKeyContent.indexOf(",")).trim().replaceAll("\"", "");
                                    cookieData.get(name).put("ckd", ckd);
                                    System.out.println(name + "!!!!!!!!!!!successfully grabbed ckd:(" + count.getAndDecrement() + "nd time)" + LocalTime.now() + "---->" + ckd);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                break;
                            case 1:
                                if (LocalDateTime.now().isAfter(activityType.startTime)) {
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
            sleep();
        }
    }

    //总执行次数
    private final static AtomicInteger count = new AtomicInteger(100000);
    private final static Long sleepTime = 100L;
    private final ActivityType activityType;
    //执行时的cookie数据
    private static final Map<String, Map<String, String>> cookieData = new HashMap<>();

    //获取信息的参数和header
    public static Map<String, String> getRequestData(String userName, RequestEntry requestEntry, UrlType urlType, ActivityType activityType) {
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
                res.put("act_id", activityType.act_id);
                res.put("task_id", activityType.task_id);
                res.put("group_id", activityType.group_id);
                res.put("receive_id", cookieData.get(userName).get("receiveIdNow"));
                res.put("receive_from", activityType.receive_from);
            } else if (UrlType.singleTask.equals(urlType)) {
                res.put("id", activityType.id);
            }
        }
        return res;
    }

    //header/param
    enum RequestEntry {
        header,
        param
    }

    //调用的url场景
    enum UrlType {
        singleTask,
        receive
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url     发送请求的URL
     * @param params  请求参数
     * @param headers 请求header
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, Map<String, String> params, Map<String, String> headers) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url;
            if (!CollectionUtils.isEmpty(params)) {
                String param = params.entrySet().stream()
                        .filter(entry -> !StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue()))
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
                urlNameString += "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(connection::setRequestProperty);
            }
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("An exception occurred when sending a GET request！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url     发送请求的 URL
     * @param params  请求参数
     * @param headers 请求header
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, String> params, Map<String, String> headers) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            if (!CollectionUtils.isEmpty(headers)) {
                headers.forEach(conn::setRequestProperty);
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            if (!CollectionUtils.isEmpty(params)) {
                String param = params.entrySet().stream()
                        .filter(entry -> !StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue()))
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
                out.print(param);
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("An exception occurred when sending a POST request!" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    //原神脚本线程池
    static final ThreadPoolExecutor ysThreadPoolExecutor = new ThreadPoolExecutor(
            1,
            50,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1),
            new TestThreadFactory(),
            (r, e) -> System.out.println("Thread Pool:" + e.toString() + "--- The number of tasks exceeds 10 and the thread is rejected: " + r.toString()));

    static class TestThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        TestThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }


        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, "YsPool" + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    static class CollectionUtils {
        public static boolean isEmpty(Map<?, ?> map) {
            return map == null || map.isEmpty();
        }
    }

    static class StringUtils {
        public static boolean isEmpty(Object str) {
            return str == null || "".equals(str);
        }

        public static boolean receiveIdValid(String receiveIdNow) {
            return !StringUtils.isEmpty(receiveIdNow) && !"0".equals(receiveIdNow);
        }

        public static boolean isExitByName(String name) {
            return Boolean.parseBoolean(cookieData.get(name).get("exit"));
        }

        public static boolean isExitAll() {
            return cookieData.values().stream().allMatch(cookie -> Boolean.parseBoolean(cookie.get("exit")));
        }
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
