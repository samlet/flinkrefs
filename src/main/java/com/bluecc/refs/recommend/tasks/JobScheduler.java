package com.bluecc.refs.recommend.tasks;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobScheduler {

    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * 每12小时定时调度一次 基于两个推荐策略的 产品评分计算
     * 策略1 ：协同过滤
     * <p>
     * 数据写入表  px
     * <p>
     * 策略2 ： 基于产品标签 计算产品的余弦相似度
     * <p>
     * 数据写入表 ps
     */
    public static void start(IStore store, String[] args) {
        //	ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(5);
        Timer qTimer = new Timer();
        qTimer.scheduleAtFixedRate(new RefreshTask(store), 0, 15 * 1000);// 定时每15分钟
    }

    private static class RefreshTask extends TimerTask {
        IStore store;

        RefreshTask(IStore store) {
            this.store = store;
        }


        @Override
        public void run() {
            System.out.println(new Date() + " 开始执行任务！");
            /**
             * 取出被用户点击过的产品id，getAllKey只是一个示例，真实情况不太可能把所有的产品取出来
             *
             */
            List<String> allProId = new ArrayList<>();
            try {
                allProId = store.getAllKey("p_history");
            } catch (IOException e) {
                System.err.println("获取历史产品id异常: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            /**
             * 可以考虑任务执行前是否需要把历史记录删掉
             */
            for (String id : allProId) {
                // 每12小时调度一次
                executorService.execute(new Task(store, id, allProId));
            }
        }
    }

    private static class Task implements Runnable {

        private String id;
        private List<String> others;
        IStore store;

        public Task(IStore store, String id, List<String> others) {
            this.store=store;
            this.id = id;
            this.others = others;
        }


        ItemCfCoeff item = new ItemCfCoeff(store);
        ProductCoeff prod = new ProductCoeff(store);

        @Override
        public void run() {
            try {
                item.getSingelItemCfCoeff(id, others);
                prod.getSingelProductCoeff(id, others);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
