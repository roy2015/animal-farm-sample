package com.animal.farm.infrastructure.foundation.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : zhengyangyong
 */
public class ExecutorUtil {
  public static ScheduledExecutorService createScheduledExecutorService(String executorName, int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize, r -> {
      Thread t = new Thread(r, executorName);
      t.setDaemon(true);
      return t;
    });
  }

  public static ThreadPoolExecutor createThreadPoolExecutor(String executorName, int corePoolSize) {
    return createThreadPoolExecutor(executorName, corePoolSize, 1000000);
  }

  public static ThreadPoolExecutor createThreadPoolExecutor(String executorName, int corePoolSize, int maxQueueSize) {
    return new ThreadPoolExecutor(corePoolSize, corePoolSize, 15, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(maxQueueSize),
        r -> {
          Thread t = new Thread(r, executorName);
          t.setDaemon(true);
          return t;
        });
  }
}
