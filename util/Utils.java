package util;

import Enum.ActivityType;
import Enum.PlatFormType;
import Script.Script;

import java.io.*;
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
public class Utils {

    public static boolean collectionIsEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean stringIsNotBlack(Object str) {
        return str != null && !"".equals(str);
    }

    public static boolean stringIsEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    //读取cookie文件设置
    public static void readCookie() {
        LocalDateTime now = LocalDateTime.now();
        final List<PlatFormType> types = Arrays.stream(ActivityType.values()).map(ActivityType::getType).distinct().collect(Collectors.toList());
        types.forEach(type -> Script.AllActivityType.put(type,
                Arrays.stream(ActivityType.values())
                        .filter(activityType -> Objects.equals(activityType.getType(), type) && activityType.getStartTime().isAfter(now))
                        .findFirst().orElse(null)));
        final URL cookieURL = Thread.currentThread().getContextClassLoader().getResource("cookieFile.properties");
        if (Objects.isNull(cookieURL)) {
            System.out.println("please set right scriptUrl and cookieURL!! Script.Script exit!");
            System.exit(1);
        }
        try (FileInputStream inputStream = new FileInputStream(cookieURL.getFile());
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(streamReader);
            properties.keySet().stream().filter(Utils::stringIsNotBlack).forEach(key -> {
                String keyName = ((String) key);
                final String propertyValue = properties.getProperty(keyName);
                final PlatFormType type = Arrays.stream(PlatFormType.values())
                        .filter(platformType -> keyName.contains(platformType.name()))
                        .findFirst().orElse(null);
                if (Objects.nonNull(type) && Utils.stringIsNotBlack(propertyValue)) {
                    if (!Script.AllUserCookieMap.containsKey(type)) {
                        Script.AllUserCookieMap.put(type, new HashMap<>());
                    }
                    Script.AllUserCookieMap.get(type).put(keyName, propertyValue);
                }
                PlatFormType settingType = null;
                switch (keyName) {
                    case "BlActivityType":
                        settingType = PlatFormType.BiLiBiLi;
                        break;
                    case "hyActivityType":
                        settingType = PlatFormType.HuYa;
                        break;
                    case "DyActivityType":
                        settingType = PlatFormType.DouYu;
                        break;
                }
                if (Objects.nonNull(settingType) && Utils.stringIsNotBlack(propertyValue)) {
                    final ActivityType activityType = ActivityType.getByName(propertyValue);
                    if (Objects.nonNull(activityType)) {
                        Script.AllActivityType.put(settingType, activityType);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        if (Objects.nonNull(aPackage)) {
            pk = aPackage.getName() + ".";
        }
        final URL scriptUrl = Thread.currentThread().getContextClassLoader().getResource("");
        if (Objects.isNull(scriptUrl)) {
            System.exit(1);
        }
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
                    throw new RuntimeException(pk + name + "加载异常", e);
                }
            }
        }
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
            if (!collectionIsEmpty(params)) {
                String param = params.entrySet().stream()
                        .filter(entry -> stringIsNotBlack(entry.getKey()) && stringIsNotBlack(entry.getValue()))
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining("&"));
                urlNameString += "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            if (!collectionIsEmpty(headers)) {
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
            if (!collectionIsEmpty(headers)) {
                headers.forEach(conn::setRequestProperty);
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            if (!collectionIsEmpty(params)) {
                String param = params.entrySet().stream()
                        .filter(entry -> stringIsNotBlack(entry.getKey()) && stringIsNotBlack(entry.getValue()))
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
}
