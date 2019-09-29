package com.github.mayp1998.taskExecutor.util;



import com.github.mayp1998.taskExecutor.bean.Task;
import lombok.Data;
import org.junit.Test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskExecutorTest {

    @Test
    public void test() {
        TaskExecutor taskExecutor = TaskExecutor.getInstance();
        Map<String, Task> map = new HashMap<>();

        Task task1 = new Task(() -> {
//            System.out.println("Hello!" + LocalDateTime.now().toLocalTime()+ ";");
        }, "0/1 * * * * ?");
        //task1.setMaxRunNumber(10);
        map.put("task1", task1);
        Task task2 = new Task(() -> {
            //System.out.println("Hello2!" + LocalDateTime.now().toLocalTime()+ ";");
        }, "25-36 1/2 * * * ? ");
        map.put("task2", task2);
        Task task3 = new Task(() -> {
            // System.out.println("Hello3!" + LocalDateTime.now().toLocalTime()+ ";");
        }, "30/30 * * * * ?");
        map.put("task3", task3);
//
//        Task task4 = new Task(() -> {
//            //System.out.println("Hello4!" + LocalDateTime.now().toLocalTime()+ ";");
//        }, new Date(new Date().getTime()+1000), 1, true);
//        task4.setMaxRunNumber(3);
//        map.put("task4", task4);


        taskExecutor.runTasks(map);

        long starttime = new Date().getTime();
        boolean t = true;
        while (true) {
            break;
//            long nowtime = new Date().getTime();
//            long temp = (nowtime - starttime) % 60000;
            //break;
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

    @Test
    public void test2() throws InterruptedException {
        TaskExecutor taskExecutor = TaskExecutor.getNewInstance();
        boolean t1 = true;
        while (true) {
            Task task = new Task(() -> {
            }, "0/1 * * * * ?");
            taskExecutor.alterAndRunTask("task", task);
            break;
        }
    }

    @Test
    public void test3() throws Exception {// 综合测试 startTime & period 类型任务的运行精确度，同理也可以用来测试cron表达式类型的任务
        File f = new File("C:\\Users\\Administrator\\Desktop\\out.txt");// 日志文件路径
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
        System.out.println("默认输出到了文件out.txt");

        TaskExecutor taskExecutor = TaskExecutor.getInstance();
        long datelong = new Date().getTime()+ (long)5000;
        Date date = new Date(datelong);
        System.out.println("startTime:" + date.toString());
        Task task1 = new Task(() -> {
            System.out.println("Running minute! Time:" + LocalDateTime.now().toString());
        }, date, 60);
        taskExecutor.runTask("1", task1);

        Task task2 = new Task(() -> {
            System.out.println("Running hour! Time:" + LocalDateTime.now().toString());
        }, date, 3600);
        taskExecutor.runTask("2", task2);

        Task task3 = new Task(() -> {
            System.out.println("\nRunning day! Time:" + LocalDateTime.now().toString() + "\n");
        }, date, 86400);
        taskExecutor.runTask("3", task3);

        while (true) {
            Thread.sleep(1000000000);
        }
    }

    @Test
    public void test4() throws InterruptedException {
        TaskExecutor taskExecutor = TaskExecutor.getInstance();

        while (true) {
            Task task = new Task(() -> {
                try {
                    Thread.sleep(1500);
                    System.out.println("HHHh" + LocalDateTime.now().toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "* * * * * ?");
            taskExecutor.alterAndRunTask("1", task);
            Thread.sleep(100);
        }

    }
}