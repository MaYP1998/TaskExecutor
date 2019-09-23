package com.utils.taskExecutor.utils;



import com.utils.taskExecutor.Bean.Task;
import org.junit.Test;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class TaskExecutorTest {

    @Test
    public void test() {
        TaskExecutor taskExecutor = TaskExecutor.getInstance();
        Map<String, Task> map = new HashMap<>();

        Task task1 = new Task(() -> {
            System.out.println("Hello!" + LocalDateTime.now().toLocalTime()+ ";");
        }, "0/1 * * * * ?");
        task1.setMaxRunNumber(10);
        map.put("task1", task1);
        Task task2 = new Task(() -> {
            //System.out.println("Hello2!" + LocalDateTime.now().toLocalTime()+ ";");
        }, "25-36 1/2 * * * ? ");
        map.put("task2", task2);
        Task task3 = new Task(() -> {
            // System.out.println("Hello3!" + LocalDateTime.now().toLocalTime()+ ";");
        }, "30/30 * * * * ?");
        map.put("task3", task3);

        Task task4 = new Task(() -> {
            System.out.println("Hello4!" + LocalDateTime.now().toLocalTime()+ ";");
        }, new Date(new Date().getTime()+1000), 1, true);
        task4.setMaxRunNumber(3);
        map.put("task4", task4);


        taskExecutor.runTasks(map);

        long starttime = new Date().getTime();
        boolean t = true;
        while (true) {
            long nowtime = new Date().getTime();
            long temp = (nowtime - starttime) % 60000;
            break;
//            if (temp <= 15000) {
//                if (t) {
//                    taskExecutor.startTask("task1");
//                    t = false;
//                }
//            } else if (temp <= 30000) {
//                taskExecutor.pauseTask("task1");
//            } else if (temp <= 45000) {
//                taskExecutor.shutdownTask("task1");
//            } else {
//                taskExecutor.addTask("task1", task1);
//                t = true;
//            }
//            //System.out.println("time:" + LocalDateTime.now().toLocalTime() + ", status:" + taskExecutor.getAllTaskStatus());
//            System.out.println("runNumber:" + taskExecutor.getAllTaskRunNumber());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}