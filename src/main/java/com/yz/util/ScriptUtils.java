package com.yz.util;

import com.alibaba.fastjson.JSON;
import com.yz.domain.BRes;
import com.yz.enumtype.ActivityType;
import com.yz.enumtype.PlatFormType;
import com.yz.script.Script;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ymx
 * @apiNote 工具类
 **/
@Slf4j
public class ScriptUtils {
    private final static List<Class<? extends Script>> scriptsClass = getScriptClasses();

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 取得当前类路径下的所有类
     *
     * @return Script路径下所有集合
     */
    private static List<Class<? extends Script>> getScriptClasses() {
        List<Class<? extends Script>> classes = new ArrayList<>();
        Package aPackage = Script.class.getPackage();
        String pk = "";
        if (Objects.nonNull(aPackage)) {
            pk = aPackage.getName() + ".";
        }
        for (Class<?> c : getScriptClasses(pk)) {
            if (Script.class.isAssignableFrom(c) && !Script.class.equals(c)) {
                classes.add(c.asSubclass(Script.class));
            }
        }
        return classes;
    }

    /**
     * 迭代查找类
     *
     * @return List<Class < ?>> 包路径下class
     */
    private static List<Class<?>> getScriptClasses(String pk) {
        List<Class<?>> classes = new ArrayList<>();
        Arrays.stream(PlatFormType.values()).map(PlatFormType::getClassName).collect(Collectors.toList()).forEach(name -> {
            try {
                classes.add(Class.forName(pk + name));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(pk + name + "加载异常", e);
            }
        });
        return classes;
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
                        .filter(entry -> !StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue()))
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

    public static Script createScript(PlatFormType type) {
        Class<? extends Script> aClass1 = scriptsClass.stream().filter(aClass -> Objects.equals(aClass.getSimpleName(), type.getClassName()))
                .findFirst().orElse(null);
        if (Objects.nonNull(aClass1)) {
            try {
                return aClass1.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        List<Script> scripts = new ArrayList<>();
        String dyCookie = "acf_did=0aeea4178642c76031cb792200051601; dy_did=0aeea4178642c76031cb792200051601; smidV2=20220827131020bb46ebadce070958fd2fc3f106ba7bfe006ea6ecd50bf3ae0; acf_auth=528asTQ2QpVe8oyhM4Hfpab59XghxOjlH6irp1iyJYAFESIhulzbRB1435USh3ML5nDulXEyF0645/aY1N/lK4Aav2UmnuZ3Yf2GeQkzP8WNoNsnnaM9; dy_auth=8361AaYxHlebE3434n9K6ClLDshCiBIeSeiQH9D0YjPHbbIYoanB5FWjBeNaGkRzUGd/n2Fawi28y87ZFodjo2Ydk310gyWthehx/eIxERfDSxyP8/1p; wan_auth37wan=3a1a71c6f759pPu0Ed01H3mlKJXsdi3KL3tyKv/dnOYbH7HpVEREzSh/QeNeCdmElDLIJYjVwVHjAR42DP2jC8P0aDrzGA3IYjoq+bDGSC5akBhPYA; acf_uid=94497424; acf_username=94497424; acf_nickname=梦離若殇; acf_own_room=1; acf_groupid=1; acf_phonestatus=1; acf_ct=0; acf_ltkid=81259575; acf_biz=1; acf_stk=9966ad5f7c0b7fcd; acf_avatar=//apic.douyucdn.cn/upload/avanew/face/201612/14/23/1a93a78bc843685d713602689ed7fbe9_; acf_ccn=9f7834c49d22598fc43ef4715effd587; Hm_lvt_e99aee90ec1b2106afe7ec3b199020a7=1667489594,1667489675,1667491157,1667491932; PHPSESSID=2n08gs91u0rlj30krtf9f55ts5; Hm_lpvt_e99aee90ec1b2106afe7ec3b199020a7=1667492033";
        String biCookie = "buvid3=E9BFF8A5-FBBF-DB65-0D69-7ABB2A609A9481147infoc; _uuid=C8FE4C25-A756-5814-7E44-1A1A1247FA6280871infoc; buvid4=B26BD057-DE29-DC86-D59F-10C5D0C8EAC482237-022031619-kji2bknSwKco8f52F6rjzQu1hstbLjNPkiJRLc50Ghd04Ex55Uu24Q==; rpdid=0zbfAHGGS5|SKQVpjnQ|3XA|3w1Nusda; LIVE_BUVID=AUTO5716475960417726; nostalgia_conf=-1; CURRENT_BLACKGAP=0; buvid_fp_plain=undefined; i-wanna-go-back=-1; hit-dyn-v2=1; blackside_state=0; is-2022-channel=1; b_nut=100; fingerprint3=69925bb39c9877b53c6667b700549ddb; CURRENT_QUALITY=80; DedeUserID=102462368; DedeUserID__ckMd5=76421ba71f67f422; b_ut=5; dy_spec_agreed=1; fingerprint=06815c97c25c72f4a04ff58786b8d5ef; bp_video_offset_102462368=725007882943201300; _dfcaptcha=2aacca25a2d2cc52ff704b23c441228b; SESSDATA=8c627420,1683214942,e100b*b2; bili_jct=04331b2ad350ddab1d7dd3d997475477; sid=8k36vfd3; buvid_fp=51bd9db970ac122cc84e2ffa7c02ff27; b_lsid=48968FD7_18448883C23; CURRENT_FNVAL=4048; PVID=2";
        LocalDateTime dateTime = null;
//        dateTime = LocalDateTime.of(2022, 10, 16, 0, 0, 1);
        for (int i = 0; i < 1; i++) {
            scripts.add(execLocalScript(biCookie, ActivityType.bibi_3_330, dateTime));
//            scripts.add(execLocalScript(dyCookie, ActivityType.douYu_3_330, dateTime));
        }
        while (true) {
            if (scripts.stream().filter(Objects::nonNull).noneMatch(script -> script.goOn)) {
                YuanShenThreadTool.ysThreadPoolExecutor.shutdown();
                break;
            }
            sleep(3000);
        }
//        getBiLiBiLiActivityType(biCookie);
    }

    public static void getBiLiBiLiActivityType(String biCookie) {
        String url = "https://api.bilibili.com/x/activity/mission/single_task";
        final HashMap<String, String> headMap = new HashMap<String, String>() {{
            put("authority", "api.bilibili.com");
            put("accept", "application/json, text/plain, */*");
            put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            put("cookie", biCookie);
            put("dnt", "1");
            put("origin", "https://www.bilibili.com");
            put("referer", " https://www.bilibili.com/");
            put("sec-ch-ua", "\"Microsoft Edge\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
            put("sec-ch-ua-mobile", " ?0");
            put("sec-ch-ua-platform", "macOS");
            put("sec-fetch-dest", "empty");
            put("sec-fetch-mode", "cors");
            put("sec-fetch-site", "same-site");
            put("sec-gpc", "1");
            put("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.53");
        }};
        final HashMap<String, String> params = new HashMap<>();
        Arrays.stream(ActivityType.values()).filter(activityType -> PlatFormType.BiLiBiLi.equals(activityType.getType()))
                .map(ActivityType::getId).collect(Collectors.toList())
                .forEach(id -> {
                    params.put("id", id);
                    final String result = sendGet(url, params, headMap);
                    if (!StringUtils.isEmpty(result)) {
                        final BRes bRes = JSON.parseObject(result, BRes.class);
                        log.info("id:[[{}]]   Act_id:[{}]   Task_id:[{}]   Group_id:[{}]",
                                id,
                                bRes.getData().getTask_info().getGroup_list().get(0).getAct_id(),
                                bRes.getData().getTask_info().getGroup_list().get(0).getTask_id(),
                                bRes.getData().getTask_info().getGroup_list().get(0).getGroup_id()
                        );
                    }
                });
    }

    public static Script execLocalScript(String cookie, ActivityType activityType, LocalDateTime dateTime) {
        Script script = createScript(activityType.getType());
        // 发送消息给客户端
        if (script == null) {
            log.info("没有当前活动的执行脚本");
            return null;
        }
        script.execute(cookie, activityType, dateTime, null);
        return script;
    }
}