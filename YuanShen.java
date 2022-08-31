import Script.Script;
import util.Utils;
import util.YuanShenThreadTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ymx <br/>
 * 创建时间：2022/5/15 18:01 <br/>
 */
public class YuanShen {
    //所有的执行脚本
    private final static List<Script> scripts = new ArrayList<>();
    //反射初始化设置所有脚本
    static {
        Utils.readCookie();
        for (Class<? extends Script> aClass : Utils.getScriptClasses()) {
            try {
                scripts.add(aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(aClass.getSimpleName() + "newInstance error", e);
            }
        }
    }

    //程序执行主main入口
    public static void main(String[] args) {
        //执行脚本路径
        startAndExit();
    }

    private static void startAndExit() {
        scripts.forEach(Script::execute);
        new Thread(() -> {
            while (!YuanShenThreadTool.ysThreadPoolExecutor.isTerminated()) {
                if (scripts.stream().noneMatch(script -> script.goOn)) {
                    YuanShenThreadTool.ysThreadPoolExecutor.shutdown();
                }
                Utils.sleep(1000);
            }
        }).start();
    }
}
