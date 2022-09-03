package com.yz.script;

import com.yz.enumtype.ActivityType;
import com.yz.enumtype.PlatFormType;
import com.yz.util.ScriptUtils;
import com.yz.util.YuanShenThreadTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ymx
 * @apiNote 脚本模板
 **/
@Slf4j
public abstract class Script implements Runnable {
    //是否继续处理
    public boolean goOn = true;

    //用户cookie 网页获取
    public String cookie = null;
    //每个脚本同时执行的线程数量  超过20算20
    public int threadNum = Math.min(15, 20);
    //总执行次数
    public AtomicInteger count = new AtomicInteger(10000);
    public Long sleepTime = 100L;
    public ActivityType activityType = null;

    //脚本执行时使用的cookie数据 需要使用先完成initData()逻辑
    public final Map<String, String> cookieData = new HashMap<>();

    //用户响应
    private WebSocketSession session;

    //初始化数据
    public abstract void initData();

    //线程提交执行前自定义处理(获取网站的前置请求数据)
    public abstract void preExecute();

    public void sendMessage(String str) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(str));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //线程提交执行
    public void execute(String cookie, ActivityType activityType, LocalDateTime executionTime, WebSocketSession session) {
        this.session = session;
        if (StringUtils.isEmpty(cookie) || Objects.isNull(activityType) || Objects.isNull(session)) {
            return;
        }
        //判断开始执行时间 提前3s执行
        LocalDateTime localTime;
        if (Objects.nonNull(executionTime)) {
            localTime = executionTime.minusSeconds(3);
        } else {
            localTime = activityType.getStartTime().minusSeconds(3);
        }
        this.cookie = cookie;
        this.activityType = activityType;
        initData();
        sendMessage("活动开始时间 " + activityType.getStartTime() + "-----脚本执行时间: " + localTime);
        new Thread(() -> {
            while (goOn) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(localTime)) {
                    if (now.getSecond() % 25 == 0) {
                        long millis = Duration.between(now, localTime).toMillis() / 1000;
                        long day = millis / 86400;
                        long hour = millis % 86400 / 3600;
                        long minute = millis % 86400 % 3600 / 60;
                        long second = millis % 60;
                        sendMessage("剩余  [" + day + " 天 " + hour + ":" + minute + ":" + second + "]");
                    }
                    ScriptUtils.sleep(1000);
                } else {
                    sendMessage(" 开始执行开始时间 " + now);
                    break;
                }
            }
            preExecute();
            //线程开始执行
            for (int i = 0; i < threadNum; i++) {
                YuanShenThreadTool.ysThreadPoolExecutor.submit(this);
            }
        }).start();
    }

    public abstract PlatFormType platFormType();
}
