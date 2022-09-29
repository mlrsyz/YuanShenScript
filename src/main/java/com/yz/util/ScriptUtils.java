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
        getBiLiBiLiActivityType();
    }

    public static void getBiLiBiLiActivityType() {
        String url = "https://api.bilibili.com/x/activity/mission/single_task";
        final HashMap<String, String> headMap = new HashMap<String, String>() {{
            put("authority", "api.bilibili.com");
            put("accept", "application/json, text/plain, */*");
            put("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            put("cookie", " b_lsid=27CA1821_183888E6DB3; _uuid=2FF103175-A344-258F-1952-C1C13BF1847879131infoc; buvid_fp=56b4602b8f6e18425c41ca4c8c5796aa; buvid3=18187E1A-64F0-5D82-767D-2D552E09201281270infoc; b_nut=1664443381; buvid4=5A71AB32-17F0-2766-DF3C-3364B84C447F81270-022092917-JhtBUeZUogfvog8fp3fwrQ%3D%3D; CURRENT_FNVAL=4048; sid=8rt0hv2u");
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
                        log.info("\"{}\",\"{}\",\"{}\",\"{}\"", id,
                                bRes.getData().getTask_info().getGroup_list().get(0).getAct_id(),
                                bRes.getData().getTask_info().getGroup_list().get(0).getTask_id(),
                                bRes.getData().getTask_info().getGroup_list().get(0).getGroup_id()
                        );
                    }
                });
    }
}