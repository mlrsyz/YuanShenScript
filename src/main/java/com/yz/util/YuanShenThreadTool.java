package com.yz.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ymx
 * @apiNote 线程池
 **/
public class YuanShenThreadTool {
    //原神脚本线程池
    public static class YuanShenThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        YuanShenThreadFactory() {
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

    public static final ThreadPoolExecutor ysThreadPoolExecutor = new ThreadPoolExecutor(
            1,
            200,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(500),
            new YuanShenThreadFactory(),
            (r, e) -> System.out.println("ys Thread Pool:" + e.toString() + "--- 任务数超过50，线程被拒绝: " + r.toString()));

}
