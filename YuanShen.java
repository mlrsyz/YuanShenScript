//package com.yz.YuanShenScript;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 作者：ymx <br/>
 * 创建时间：2022/5/15 18:01 <br/>
 */
public class YuanShen {
    private static final Map<PlatformType, Map<String, String>> AllUserCookieMap = new HashMap<>();

    //活动奖励卡片 时间格式(年-月-日-小时-分钟)规范填写
    //原神活动里程碑数据 需要抓接口数据填写
    enum ActivityType {
        //===================⬇DouYu活动⬇=============================================================================================================================================================
        douYu_3_330(PlatformType.DouYu, "", "", "126315", "", "",
                LocalDateTime.of(2022, 8, 26, 2, 0)),
        douYu_5_660(PlatformType.DouYu, "", "", "126316", "", "",
                LocalDateTime.of(2022, 8, 28, 2, 0)),
        douYu_10_1000(PlatformType.DouYu, "", "", "126317", "", "",
                LocalDateTime.of(2022, 9, 2, 2, 0)),
        douYu_18_800(PlatformType.DouYu, "", "", "126318", "", "",
                LocalDateTime.of(2022, 9, 10, 2, 0)),
        douYu_26_800(PlatformType.DouYu, "", "", "126319", "", "",
                LocalDateTime.of(2022, 9, 18, 2, 0)),
        douYu_35_1000(PlatformType.DouYu, "", "", "126323", "", "",
                LocalDateTime.of(2022, 9, 27, 2, 0)),
        //==================⬇BiLiBiLi活动⬇==============================================================================================================================================================
        bibi_3_330(PlatformType.BiLiBiLi, "e62ce499", "425", "1598", "7712", "missionLandingPage",
                LocalDateTime.of(2022, 8, 26, 2, 0)),
        bibi_5_660(PlatformType.BiLiBiLi, "1a468055", "425", "1599", "7714", "missionLandingPage",
                LocalDateTime.of(2022, 8, 28, 2, 0)),
        bibi_10_1000(PlatformType.BiLiBiLi, "56cb6703", "425", "1600", "7715", "missionLandingPage",
                LocalDateTime.of(2022, 9, 2, 2, 0)),
        bibi_18_800(PlatformType.BiLiBiLi, "99e34eec", "425", "1601", "7716", "missionLandingPage",
                LocalDateTime.of(2022, 9, 10, 2, 0)),
        bibi_26_800(PlatformType.BiLiBiLi, "c8daa502", "425", "1602", "7717", "missionLandingPage",
                LocalDateTime.of(2022, 9, 18, 2, 0)),
        bibi_35_1000(PlatformType.BiLiBiLi, "08bb556a", "425", "1603", "7718", "missionLandingPage",
                LocalDateTime.of(2022, 9, 27, 2, 0));
        //================================================================================================================================================================================
        final PlatformType type;
        final String id;
        final String act_id;
        final String task_id;
        final String group_id;
        final String receive_from;
        //活动的开始时间
        final LocalDateTime startTime;

        public PlatformType getType() {
            return type;
        }

        ActivityType(PlatformType type, String id, String act_id, String task_id, String group_id, String receive_from, LocalDateTime startTime) {
            this.type = type;
            this.id = id;
            this.act_id = act_id;
            this.task_id = task_id;
            this.group_id = group_id;
            this.receive_from = receive_from;
            this.startTime = startTime;
        }
    }

    //程序执行主main入口
    public static void main(String[] args) {
        YuanShen.startScript();
    }


    /**
     * 执行脚本主入口
     *
     * @param types 要抢的原石里程碑类型
     */
    public static void startScript(ActivityType... types) {
        if (Objects.nonNull(types) && types.length > 0) {
            Map<PlatformType, ActivityType> activityTypeMap = Arrays.stream(types)
                    .collect(Collectors.toMap(ActivityType::getType, Function.identity(), (v1, v2) -> v1));
            initScript(activityTypeMap);
        }
        startAndExit();
    }

    private final static List<Script> scripts = new ArrayList<>();

    static {
        for (Class<? extends Script> aClass : getScriptClasses()) {
            try {
                scripts.add(aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(aClass.getSimpleName() + "newInstance error", e);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        Map<PlatformType, ActivityType> activityTypeMap = new HashMap<>();
        final List<PlatformType> types = Arrays.stream(ActivityType.values()).map(ActivityType::getType).distinct().collect(Collectors.toList());
        types.forEach(type -> activityTypeMap.put(type,
                Arrays.stream(ActivityType.values())
                        .filter(activityType -> Objects.equals(activityType.type, type) && activityType.startTime.isAfter(now))
                        .findFirst().orElse(null)));
        initScript(activityTypeMap);
    }

    private static void initScript(Map<PlatformType, ActivityType> activityTypeMap) {
        if (CollectionUtils.isEmpty(activityTypeMap)) {
            return;
        }
        scripts.forEach(script -> script.setActivityType(activityTypeMap));
    }

    private static void startAndExit() {
        scripts.forEach(Script::execute);
        new Thread(() -> {
            while (!ysThreadPoolExecutor.isTerminated()) {
                if (scripts.stream().noneMatch(script -> script.goOn)) {
                    ysThreadPoolExecutor.shutdown();
                }
                sleep(1000);
            }
        }).start();
    }

    enum PlatformType {
        BiLiBiLi,
        HuYa,
        DouYu
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
                        .filter(entry -> StringUtils.isNotBlack(entry.getKey()) && StringUtils.isNotBlack(entry.getValue()))
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
                    connection.getInputStream(), StandardCharsets.UTF_8));
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
                        .filter(entry -> StringUtils.isNotBlack(entry.getKey()) && StringUtils.isNotBlack(entry.getValue()))
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
                out.print(param);
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), StandardCharsets.UTF_8));
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

    /**
     * 取得当前类路径下的所有类
     *
     * @return Script路径下所有集合
     */
    public static List<Class<? extends Script>> getScriptClasses() {
        List<Class<? extends Script>> classes = new ArrayList<>();
        Package aPackage = Script.class.getPackage();
        String pk = "";
        String cookiePath = "cookieFile.properties";
        if (Objects.nonNull(aPackage)) {
            pk = aPackage.getName() + ".";
            cookiePath = aPackage.getName().replace(".", "/") + "/" + cookiePath;
        }
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL scriptUrl = classLoader.getResource("");
        final URL cookieURL = classLoader.getResource(cookiePath);
        if (Objects.isNull(scriptUrl) || Objects.isNull(cookieURL)) {
            System.out.println("please set right scriptUrl and cookieURL!! Script exit!");
            System.exit(1);
        }
        readCookie(cookieURL.getFile());
        for (Class<?> c : getScriptClasses(new File(scriptUrl.getFile()), pk)) {
            if (Script.class.isAssignableFrom(c) && !Script.class.equals(c)) {
                classes.add(c.asSubclass(Script.class));
            }
        }
        return classes;
    }

    /**
     * 迭代查找类
     *
     * @param dir 资源
     * @return List<Class < ?>> 包路径下class
     */
    private static List<Class<?>> getScriptClasses(File dir, String pk) {
        List<Class<?>> classes = new ArrayList<>();
        if (!dir.exists()) {
            return classes;
        }
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) {
                classes.addAll(getScriptClasses(f, pk));
            }
            String name = f.getName();
            if (name.endsWith(".class") && name.contains("Script")) {
                try {
                    classes.add(Class.forName(pk + name.substring(0, name.length() - 6)));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(name + "加载异常", e);
                }
            }
        }
        return classes;
    }

    //读取cookie文件设置
    private static void readCookie(String cookieFile) {
        try (FileInputStream inputStream = new FileInputStream(cookieFile);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(streamReader);
            properties.keySet().forEach(key -> {
                if (StringUtils.isNotBlack(key)) {
                    String keyName = ((String) key);
                    final PlatformType type = Arrays.stream(PlatformType.values())
                            .filter(platformType -> keyName.contains(platformType.name()))
                            .findFirst().orElse(null);
                    if (Objects.nonNull(type)) {
                        if (!AllUserCookieMap.containsKey(type)) {
                            AllUserCookieMap.put(type, new HashMap<>());
                        }
                        AllUserCookieMap.get(type).put(keyName, properties.getProperty(keyName));
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static class StringUtils {
        public static boolean isNotBlack(Object str) {
            return str != null && !"".equals(str);
        }

        public static boolean isEmpty(Object str) {
            return str == null || "".equals(str);
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static abstract class Script implements Runnable {
        private boolean execute = false;
        //是否继续处理
        public boolean goOn = true;
        //用户cookie 网页获取
        //数据格式 key(不重复的标识符):value(有效的用户cookie)
        public final Map<String, String> cookieMap = AllUserCookieMap.get(platformType());
        //每个脚本同时执行的线程数量  超过20算20
        public int threadNum = Math.min(15, 20);
        //总执行次数
        public AtomicInteger count = new AtomicInteger(100000);
        public Long sleepTime = 100L;
        public ActivityType activityType = null;

        public void setActivityType(Map<PlatformType, ActivityType> activityTypeMap) {
            this.activityType = activityTypeMap.get(platformType());
        }

        //脚本执行时使用的cookie数据 需要使用先完成initData()逻辑
        public final Map<String, Map<String, String>> cookieData = new HashMap<>();

        //初始化数据
        public abstract void initData();

        //线程提交执行前自定义处理(获取网站的前置请求数据)
        public abstract void preExecute();

        //线程提交执行
        private void execute() {
            if (execute) {
                return;
            }
            new Thread(() -> {
                if (Objects.isNull(activityType)) {
                    goOn = false;
                    System.out.println(this.getClass().getSimpleName() + ": Please set the activity type!(maybe activity is done?)");
                    return;
                }
                initData();
                if (!goOn) {
                    return;
                }
                //判断开始执行时间 提前3s执行
                LocalDateTime localTime = activityType.startTime.minusSeconds(3);
                System.out.println(this.getClass().getSimpleName() + "Event start time " + activityType.startTime + "-----script execution time " + localTime);
                while (true) {
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(localTime)) {
                        if (now.getSecond() % 25 == 0) {
                            long millis = Duration.between(now, localTime).toMillis() / 1000;
                            long day = millis / 86400;
                            long hour = millis % 86400 / 3600;
                            long minute = millis % 86400 % 3600 / 60;
                            long second = millis % 60;
                            System.out.println(this.getClass().getSimpleName() + "surplus [" + day + " day " + hour + ":" + minute + ":" + second + "]");
                        }
                        sleep(1000);
                    } else {
                        System.out.println(this.getClass().getSimpleName() + " starts executing start time " + now + "===========================================================================================================================================================================================================================================================================================================");
                        break;
                    }
                }
                preExecute();
                if (!goOn) {
                    return;
                }
                //线程开始执行
                for (int i = 0; i < threadNum; i++) {
                    ysThreadPoolExecutor.submit(this);
                }
            }).start();
            execute = true;
        }

        public abstract PlatformType platformType();
    }

    public static class BiBiScript extends Script {
        //原神获取奖励信息前提数据url
        private static final String singleTaskUrl = "https://api.bilibili.com/x/activity/mission/single_task";
        //领取奖励url
        private static final String receiveUrl = "https://api.bilibili.com/x/activity/mission/task/reward/receive";

        @Override
        public void initData() {
            if (CollectionUtils.isEmpty(cookieMap)) {
                System.out.println(this.getClass().getSimpleName() + ": Please set cookie information!");
                goOn = false;
                return;
            }
            for (Iterator<Map.Entry<String, String>> iterator = cookieMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> entry = iterator.next();
                if (StringUtils.isEmpty(entry.getKey()) ||
                        StringUtils.isEmpty(entry.getValue()) ||
                        !entry.getValue().contains("SESSDATA")) {
                    iterator.remove();
                    System.out.println(this.getClass().getSimpleName() + ": Please check the cookie information of " + entry.getKey());
                }
            }
            if (CollectionUtils.isEmpty(cookieMap)) {
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
                if (Objects.isNull(entry.getValue()) || CollectionUtils.isEmpty(entry.getValue()) || entry.getValue().size() < 3) {
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
                        String singleTaskResponse = sendGet(singleTaskUrl, getRequestData(name, RequestEntry.param, UrlType.singleTask, activityType), getRequestData(name, RequestEntry.header, UrlType.singleTask, activityType));
                        if (!StringUtils.isEmpty(singleTaskResponse) && singleTaskResponse.contains("receive_id")) {
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
                            .noneMatch(receive_id -> StringUtils.isEmpty(receive_id) || "0".equals(receive_id))) {
                        break;
                    }
                    sleep(150);
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
                    String postResult = sendPost(receiveUrl, getRequestData(name, RequestEntry.param, UrlType.receive, activityType), getRequestData(name, RequestEntry.header, UrlType.receive, activityType));
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
                sleep(sleepTime);
            }
        }

        @Override
        public PlatformType platformType() {
            return PlatformType.BiLiBiLi;
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

        public static boolean receiveIdValid(String receiveIdNow) {
            return !StringUtils.isEmpty(receiveIdNow) && !"0".equals(receiveIdNow);
        }

        public boolean isExitByName(String name) {
            return Boolean.parseBoolean(cookieData.get(name).get("exit"));
        }

        public boolean isExitAll() {
            return cookieData.values().stream().allMatch(cookie -> Boolean.parseBoolean(cookie.get("exit")));
        }
    }

    public static class HuYaScript extends Script {
        @Override
        public void initData() {
        }

        @Override
        public void preExecute() {

        }

        @Override
        public void run() {

        }

        @Override
        public PlatformType platformType() {
            return PlatformType.HuYa;
        }

    }

    public static class DouYuScript extends Script {
        //领取奖励url
        private static final String receiveUrl = "https://www.douyu.com/japi/carnival/nc/roomTask/getPrize";

        @Override
        public void initData() {
            //斗鱼的线程设置少一点 推荐1-5即可
            threadNum = 1;
            if (CollectionUtils.isEmpty(cookieMap)) {
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
                if (Objects.isNull(entry.getValue()) || CollectionUtils.isEmpty(entry.getValue()) || entry.getValue().size() < 3) {
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
                String postResult = sendPost(receiveUrl, getRequestData(name, RequestEntry.param, activityType), getRequestData(name, RequestEntry.header, activityType));
                System.out.println(postResult);
            }
            goOn = false;
        }

        @Override
        public PlatformType platformType() {
            return PlatformType.DouYu;
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
                res.put("taskId", activityType.task_id);
            }
            return res;
        }
    }
}
