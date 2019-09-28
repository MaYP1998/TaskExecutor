package com.github.mayp1998.taskExecutor.util;


import com.github.mayp1998.taskExecutor.bean.Task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * 任务调度工具类
 *
 * @author myp
 * @since 2019-08-30 20:34:51
 */
public class TaskExecutor {
  private Map<String, Task> group = new ConcurrentHashMap<>();// 任务队列

  private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();// 定时任务工具类

  private TaskExecutor() {
  }

  private static class Singleton {// 静态内部类实现单例
    private static TaskExecutor instance = new TaskExecutor();
  }

  /**
   * 获取对象，实现单例
   * @return
   */
  public static TaskExecutor getInstance() {
    return Singleton.instance;
  }

  /**
   * 额外创建新对象，不影响原单例对象的使用
   * @return
   */
  public static TaskExecutor getNewInstance() {
    return new TaskExecutor();
  }

  @Override
  protected void finalize() throws Throwable {
    shutdownAll();
    super.finalize();
  }

  /**
   * 停止所有任务，并清空队列
   * @param isForceToStop
   */
  public void shutdownAll(boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    for (String id : group.keySet()) {
      if (id != null) {
        shutdownTask(id, forceToStop);
      }
    }
    service = Executors.newSingleThreadScheduledExecutor();
    group.clear();
    group = new ConcurrentHashMap<>();
  }

  public Map<String, Long> getAllTaskRunNumber() {
    Map<String, Long> result = new HashMap<>();
    for (Map.Entry<String, Task> task : group.entrySet()) {
      result.put(task.getKey(), (task.getValue().getRun()));
    }
    return result;
  }

  /**
   * 获取某任务的运行次数
   * @param taskId
   * @return
   */
  public Long getTaskRunNumber(String taskId) {
    if (group.containsKey(taskId)) {
      return group.get(taskId).getRun();
    }
    return null;
  }

  /**
   * 获取某个任务
   * @param taskId
   * @return
   */
  public Task getTask(String taskId) {
    if (group.containsKey(taskId)) {
      return group.get(taskId);
    }
    return null;
  }

  /**
   * 获取所有任务队列中的任务状态：运行/暂停
   * @author myp
   * @return
   */
  public Map<String, Boolean> getAllTaskStatus() {
    Map<String, Boolean> result = new HashMap<>();
    for (Map.Entry<String, Task> task : group.entrySet()) {
      result.put(task.getKey(), (task.getValue().getFuture() != null));
    }
    return result;
  }

  /**
   * 获取任务状态
   * @param taskId
   * @return true代表任务正在运行，false代表任务存在于队列中但未运行，null代表队列中没有这个任务
   */
  public Boolean getTaskStatus(String taskId) {
    if (group.containsKey(taskId)) {
      Task task = group.get(taskId);
      return task.getFuture() != null;
    }
    return null;
  }

  /**
   * 修改并运行任务
   * @param taskId
   * @param newTask
   * @param isForceToStop
   */
  public void alterAndRunTask(String taskId, Task newTask, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    if (taskId != null && newTask != null) {
      alterTask(taskId, newTask, forceToStop);
      startTask(taskId);
    }
  }

  /**
   * 修改并运行多个任务
   * @param newTaskMap
   * @param isForceToStop
   */
  public void alterAndRunTasks(Map<String,Task> newTaskMap, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    for (Map.Entry<String,Task> newTask : newTaskMap.entrySet()) {
      alterAndRunTask(newTask.getKey(), newTask.getValue(), forceToStop);
    }
  }

  /**
   * 修改任务
   * @param taskId
   * @param newTask
   * @param isForceToStop
   */
  public void alterTask(String taskId, Task newTask, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    if (taskId != null && newTask != null) {
      if (group.containsKey(taskId)) {
        Task task = group.get(taskId);
        if (task.getFuture() != null) {
          task.stop(forceToStop);
          task.setFuture(null);
        }
        group.remove(taskId);
      }
      group.put(taskId, newTask);
    }
  }

  /**
   * 修改多个任务
   * @param newTaskMap
   * @param isForceToStop
   */
  public void alterTasks(Map<String,Task> newTaskMap, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    for (Map.Entry<String,Task> newTask : newTaskMap.entrySet()) {
      alterTask(newTask.getKey(), newTask.getValue(), forceToStop);
    }
  }

  /**
   * 暂停任务
   * @param taskId
   * @param isForceToStop 是否强制终止正在运行中的任务
   * @return
   */
  public boolean pauseTask(String taskId, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    if (group.containsKey(taskId)) {
      Task task = group.get(taskId);
      task.setFuture(null);
      task.stop(forceToStop);// || task.isType());
      return true;
    }
    return false;
  }

  /**
   * 暂停多个任务
   * @param idList
   * @param isForceToStop
   */
  public void pauseTasks(List<String> idList, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    for (String id : idList) {
      if (id != null) {
        pauseTask(id, forceToStop);
      }
    }
  }

  /**
   * 添加并执行任务
   * @param taskId
   * @param task
   */
  public void runTask(String taskId, Task task) {
    addTask(taskId, task);
    startTask(taskId);
  }

  /**
   * 添加并执行多个任务
   * @param taskMap
   */
  public void runTasks(Map<String, Task> taskMap) {

    addTasks(taskMap);
    for (Map.Entry<String,Task> newTask : taskMap.entrySet()) {
      Task task = newTask.getValue();
      if (newTask.getKey() != null && task != null) {
        startTask(task);
      }
    }
  }

  /**
   * 添加任务
   * @author myp
   * @param task 任务对象
   */
  public void addTask(String taskId, Task task) {
    if (taskId != null && task != null) {
      group.put(taskId, task);
    }
  }

  /**
   * 添加多个任务
   * @author myp
   * @param taskMap
   */
  public void addTasks(Map<String, Task> taskMap) {
    for (Map.Entry<String,Task> newTask : taskMap.entrySet()) {
      addTask(newTask.getKey(), newTask.getValue());
    }
  }

  /**
   * 执行任务，需先添加任务
   * @author myp
   * @param taskId
   * @return
   */
  public boolean startTask(String taskId) {
    if (group.containsKey(taskId)) {
      startTask(group.get(taskId));
      return true;
    }
    return false;
  }

  /**
   * 执行多个任务，需先添加这些任务
   * @param idList
   */
  public void startTasks(List<String> idList) {
    for (String id : idList) {
      if (id != null) {
        startTask(id);
      }
    }
  }

  /**
   * 关闭任务
   * @param taskId
   * @param isForceToStop 是否强制终止正在运行中的任务
   * @return
   */
  public Boolean shutdownTask(String taskId, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    if (group.containsKey(taskId)) {
      Task task = group.get(taskId);
      task.setFuture(null);
      Boolean result = task.stop(forceToStop);// || task.isType());
      group.remove(taskId);
      return result;
    }
    return null;
  }

  /**
   * 关闭多个任务
   * @param idList
   * @param isForceToStop
   */
  public void shutdownTasks(List<String> idList, boolean ... isForceToStop) {
    boolean forceToStop = false;
    for (boolean i : isForceToStop) {
      forceToStop = i;
    }
    for (String id : idList) {
      shutdownTask(id, forceToStop);
    }
  }

  /**
   * 基于ScheduleExecutorService工具类实现的简单任务调度方法。
   *
   * ScheduledExecutorService是从Java SE5的java.util.concurrent里，做为并发工具类被引进的，这是最理想的定时任务实现方式。
   * 它有以下好处：
   * 1.相比于Timer的单线程，它是通过线程池的方式来执行任务的
   * 2.可以很灵活的去设定第一次执行任务delay时间
   * 3.提供了良好的约定，以便设定执行的时间间隔
   *
   * @author myp
   * @param task
   */
  private void startTask(Task task) {
    if (task.getFuture() == null) {
      if (!task.isType()) {// 判断任务类型，真表示为cron类型任务
        Date start = task.getStart();
        Long period = task.getPeriod();
        Boolean isRunNow = task.isRunNow();
        //Runnable runnable = task.getRunnable();
        Runnable runnable = new LimitedTaskRunnable(task);
        Future<?> future = null;
        if (isRunNow) {
          service.schedule(runnable, 0, TimeUnit.MILLISECONDS);
        }
        if (start != null && period != null) {
          long starttemp = start.getTime();
          long periodtemp = period * 1000;
          long starttime = starttemp - new Date().getTime();
          while (starttime < 0) {
            starttemp += periodtemp;
            starttime = starttemp - new Date().getTime();
          }
          // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
          future = service.scheduleAtFixedRate(runnable, starttime, period * 1000, TimeUnit.MILLISECONDS);
        } else if (start == null && period != null) {
          future = service.scheduleAtFixedRate(runnable, period * 1000, period * 1000, TimeUnit.MILLISECONDS);
        } else if (start != null) {
          long starttemp = start.getTime();
          int starttime = (int)(starttemp - new Date().getTime());
          future = service.schedule(runnable, starttime, TimeUnit.MILLISECONDS);
        }
        task.setFuture(future);
      } else {
        TaskRunnable runnable = new TaskRunnable(new LimitedTaskRunnable(task), task.getCron(), task, service);
        Future<?> future = service.schedule(runnable, runnable.getNextTriggerTime(), TimeUnit.MILLISECONDS);
        task.setFuture(future);
      }
    }
  }
}
