package com.utils.taskExecutor.Bean;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * 任务类
 *
 * @author myp
 * @since 2019-08-30 20:35:29
 */
public class Task {
  private boolean type;// 任务类型：普通任务/cron任务

  private Date start;// 任务的首次启动时间
  private Integer period;// 任务的执行周期，以秒为周期
  private Boolean isRunNow = false;

  private String cron;// 任务执行周期的cron表达式

  private Runnable runnable;
  private Future<?> future;

  /**
   * 立即执行一次任务，之后不再执行。
   * @author myp
   * @param runnable 所执行的任务内容
   */
  public Task(Runnable runnable) {
    this.start = null;
    this.period = null;
    this.runnable = runnable;
    this.isRunNow = true;
    this.type = false;
  }

  /**
   * 依据参数start，period，runNow（默认为false）的情况，共分为以下四种执行情况：
   * 1.null，null，true/false：若为true则立即执行一次。
   * 2.null，not null，true/false：若为true则立即执行一次；之后周期执行。
   * 3.not null，null，true/false：若为true则立即执行一次；到start时间执行一次。
   * 4.not null，not null，true/false：若为true则立即执行一次；到start时间执行一次，之后周期执行。
   * @author myp
   * @param runnable 所执行的任务内容
   * @param start 任务起始时间
   * @param period 任务执行周期
   * @param runNow 是否立即执行一次，独立于任务起始时间的那一次执行。
   */
  public Task(Runnable runnable, Date start, Integer period, Boolean ... runNow) {
    this.start = start;
    if (period != null && period < 0) {
      this.period = null;
    } else {
      this.period = period;
    }
    this.runnable = runnable;
    Boolean isRun = false;
    for (Boolean i : runNow) {
      if (i != null) {
        isRun = i;
      }
    }
    this.isRunNow = isRun;
    this.type = false;
  }

  /**
   * 使用cron表达式表示任务运行周期
   * @param runnable
   * @param cron
   */
  public Task(Runnable runnable, String cron) {
    this.type = true;
    this.runnable = runnable;
    this.cron = cron;
  }

  @Override
  protected void finalize() throws Throwable {
    stop(false);
    super.finalize();
  }

  /**
   * 尝试终止此任务的调度
   * @param isForceToStop 若为真，如果执行此任务的线程应该被打断;否则，在正在运行的任务允许完成
   * @return false：如果任务不能被取消，通常是因为它已经正常完成;否则返回true
   */
  public final Boolean stop(boolean isForceToStop) {
    if (future != null) {
      return future.cancel(isForceToStop);
    }
    return null;
  }

  public final Date getStart() {
    return start;
  }

  public final Integer getPeriod() {
    return (period != null && period < 0) ? null : period;
  }

  public final Boolean isRunNow() {
    return isRunNow;
  }

  public final Runnable getRunnable() {
    return runnable;
  }

  public final Future<?> getFuture() {
    return future;
  }

  public final void setFuture(Future<?> future) {
    this.future = future;
  }

  public final boolean isType() {
    return type;
  }

  public final String getCron() {
    return cron;
  }
}
