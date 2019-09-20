package com.utils.taskExecutor.utils;

import com.utils.taskExecutor.Bean.Task;
import lombok.Data;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 为适应Cron表达式，使用动态生成下次执行时间的方法
 * @author myp
 * @since 2019-09-19 23:52:38
 */
@Data
public class TaskRunnable implements Runnable {
    private Runnable runnable;
    private String cron;
    private Task task;
    private ScheduledExecutorService service;

    private Future<?> future;

    @Override
    public void run() {
        runnable.run();
        future = service.schedule(this, this.getNextTriggerTime(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            future.cancel(true);
        } catch (Exception e) {
        }
        super.finalize();
    }

    //获取下次执行时间
    public long getNextTriggerTime(){
        if(!CronExpression.isValidExpression(cron)){
            return 0;
        }
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("Caclulate Date").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        Date time0 = trigger.getStartTime();
        Date time1 = trigger.getFireTimeAfter(time0);
        return (time1.getTime() - new Date().getTime());
    }
}
