package Script;

import Enum.ActivityType;
import Enum.PlatFormType;
import util.Utils;
import util.YuanShenThreadTool;

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
public abstract class Script implements Runnable {
    public static final Map<PlatFormType, Map<String, String>> AllUserCookieMap = new HashMap<>();
    public static final Map<PlatFormType, ActivityType> AllActivityType = new HashMap<>();
    private boolean execute = false;
    //是否继续处理
    public boolean goOn = true;
    //用户cookie 网页获取
    //数据格式 key(不重复的标识符):value(有效的用户cookie)
    public final Map<String, String> cookieMap = AllUserCookieMap.get(platFormType());
    //每个脚本同时执行的线程数量  超过20算20
    public int threadNum = Math.min(15, 20);
    //总执行次数
    public AtomicInteger count = new AtomicInteger(100000);
    public Long sleepTime = 100L;
    public final ActivityType activityType = AllActivityType.get(platFormType());

    //脚本执行时使用的cookie数据 需要使用先完成initData()逻辑
    public final Map<String, Map<String, String>> cookieData = new HashMap<>();

    //初始化数据
    public abstract void initData();

    //线程提交执行前自定义处理(获取网站的前置请求数据)
    public abstract void preExecute();

    //线程提交执行
    public void execute() {
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
            LocalDateTime localTime = activityType.getStartTime().minusSeconds(3);
            System.out.println(this.getClass().getSimpleName() + "Event start time " + activityType.getStartTime() + "-----script execution time " + localTime);
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
                    Utils.sleep(1000);
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
                YuanShenThreadTool.ysThreadPoolExecutor.submit(this);
            }
        }).start();
        execute = true;
    }

    public abstract PlatFormType platFormType();
}
