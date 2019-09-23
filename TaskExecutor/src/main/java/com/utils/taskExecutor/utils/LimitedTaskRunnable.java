package com.utils.taskExecutor.utils;

import com.utils.taskExecutor.Bean.Task;

import java.util.concurrent.Future;

public class LimitedTaskRunnable implements Runnable {

    private Task task;
    private Runnable runnable;

    private boolean type;
    private int runNumber;

    private int run;// 已运行次数

    public LimitedTaskRunnable(Task task) {
        this.task = task;
        this.runnable = task.getRunnable();
        if (task.getMaxRunNumber() > 0) {
            this.type = true;
            this.runNumber = task.getMaxRunNumber();
        } else {
            this.type = false;
        }
        this.run = 0;
    }

    @Override
    public void run() {
        if (type && run >= runNumber) {
            Future<?> future = task.getFuture();
            task.setFuture(null);
            future.cancel(true);
        } else {
            runnable.run();
            run++;
            task.setRun(task.getRun() + 1);
            if (type) {
//            System.out.println("run:" + run + ", runNumber:" + runNumber);
                if (run >= runNumber) {
                    Future<?> future = task.getFuture();
                    task.setFuture(null);
                    future.cancel(true);
                }
            }
        }

    }
}
