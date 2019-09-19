# TaskExecutor
Java universal task executor, base on the java.util.concurrent.ScheduledExecutorService.

Java通用的任务执行器，基于java.util.concurrent.ScheduledExecutorService工具类实现。


#### A. 拥有多项任务调度功能：

  1. 对单项/批量任务的操作：添加、启动、添加并启动、修改（等待启动）、修改并启动、暂停（与启动配合使用）、删除。
  2. 查看任务队列执行状态、查看某任务执行状态、停止所有任务并清空队列、获取某个任务的对象、获取某个任务的运行状态。
  3. 支持立即执行、周期执行等，或依据cron表达式来执行。
  
#### B. 多线程，各任务的执行互不影响。
 
#### C. 低误差。
 
#### D. 采用<key,task>键值对的形式，来进行任务的调度控制。

#### E. 没有多余的环境依赖，可以运行在几乎所有的Java 1.8平台上。
