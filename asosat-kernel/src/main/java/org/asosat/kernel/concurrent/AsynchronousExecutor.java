/*
 * Copyright (c) 2013-2018. BIN.CHEN
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.asosat.kernel.concurrent;

import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.exception.NotSupportedException;
import org.asosat.kernel.resource.ConfigResource;
import org.asosat.kernel.util.ConvertUtils;

/**
 * @author bingo 下午6:07:59
 *
 */
@ApplicationScoped
public class AsynchronousExecutor {

  public static final String AE_NAME_DFLT = "asosat-async";
  public static final String AE = "async.executor";
  public static final String AE_NAME = AE + ".name";
  public static final String AE_CORE_SIZE = AE + ".coreSize";
  public static final String AE_MAX_SIZE = AE + ".maxSize";
  public static final String AE_KEEP_ALIVE_VALUE = AE + ".keepAlive.value";
  public static final String AE_KEEP_ALIVE_UNIT = AE + ".keepAlive.unit";
  public static final String AE_QUEUE_FAIR = AE + ".queue.fair.size";
  public static final String AE_QUEUE_CAPACITY = AE + ".queue.capacity";
  public static final String AE_REJEXE_HANDLER_NAME = AE + ".rejectedExecutionHandler.name";

  public static final String SE_NAME_DFLT = "asosat-schedule";
  public static final String SE = "scheduled.executor";
  public static final String SE_CORE_SIZE = SE + ".coreSize";
  public static final String SE_NAME = SE + ".name";
  public static final String SE_REJEXE_HANDLER_NAME = SE + ".rejectedExecutionHandler.name";

  @Inject
  BeanManager beanManager;

  @Inject
  ConfigResource config;

  ExecutorService linkedExecutorService;// fair

  ExecutorService arrayExecutorService;// unfair

  ScheduledExecutorService shceduledExecutorService;

  private volatile boolean shutdown = true;

  private final Collection<CreationalContext<?>> contexts = new ArrayList<CreationalContext<?>>(8);

  public AsynchronousExecutor() {}

  public static AsynchronousExecutor instance() {
    return DefaultContext.bean(AsynchronousExecutor.class);
  }

  public ExecutorService getArrayExecutorService() {
    this.check();
    return this.arrayExecutorService;
  }

  public ExecutorService getLinkedExecutorService() {
    this.check();
    return this.linkedExecutorService;
  }

  public ScheduledExecutorService getShceduledExecutorService() {
    this.check();
    return this.shceduledExecutorService;
  }

  public boolean isShutdown() {
    return this.shutdown;
  }

  public CompletableFuture<Void> runAsync(Runnable runnable) {
    this.check();
    return CompletableFuture.runAsync(runnable, this.arrayExecutorService);
  }

  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    this.check();
    return this.shceduledExecutorService.schedule(callable, delay, unit);
  }

  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    this.check();
    return this.shceduledExecutorService.schedule(command, delay, unit);
  }

  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
      TimeUnit unit) {
    this.check();
    return this.shceduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
      TimeUnit unit) {
    this.check();
    return this.shceduledExecutorService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }

  public void shutdown(Consumer<List<Runnable>> commencedTasks) {
    if (commencedTasks == null) {
      this.linkedExecutorService.shutdown();
      this.arrayExecutorService.shutdown();
      this.shceduledExecutorService.shutdown();
    } else {
      commencedTasks.accept(this.linkedExecutorService.shutdownNow());
      commencedTasks.accept(this.arrayExecutorService.shutdownNow());
      commencedTasks.accept(this.shceduledExecutorService.shutdownNow());
    }
    for (final CreationalContext<?> ctx : this.contexts) {
      ctx.release();
    }
    this.contexts.clear();
    this.shutdown = true;
  }

  public <T> Future<T> submit(Callable<T> task) {
    return this.submit(task, true);
  }

  public <T> Future<T> submit(Callable<T> task, boolean fair) {
    this.check();
    if (fair) {
      return this.linkedExecutorService.submit(task);
    } else {
      return this.arrayExecutorService.submit(task);
    }
  }

  public Future<?> submit(Runnable task) {
    return this.submit(task, true);
  }

  public Future<?> submit(Runnable task, boolean fair) {
    this.check();
    if (fair) {
      return this.linkedExecutorService.submit(task);
    } else {
      return this.arrayExecutorService.submit(task);
    }
  }

  public <T> Future<T> submit(Runnable task, T result) {
    return this.submit(task, result, true);
  }

  public <T> Future<T> submit(Runnable task, T result, boolean fair) {
    this.check();
    if (fair) {
      return this.linkedExecutorService.submit(task, result);
    } else {
      return this.arrayExecutorService.submit(task, result);
    }
  }

  public <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
    this.check();
    return CompletableFuture.supplyAsync(supplier, this.arrayExecutorService);
  }

  void check() {
    if (this.shutdown) {
      throw new NotSupportedException();
    }
  }

  @PreDestroy
  synchronized void destroy() {
    this.shutdown(null);
  }

  @PostConstruct
  synchronized void enable() {
    this.initExecutorJsr236();
    this.initExecutorManual();
    this.shutdown = this.linkedExecutorService == null || this.shceduledExecutorService == null
        || this.arrayExecutorService == null;
  }

  private void initExecutorJsr236() {
    if (tryToLoadClassForName("javax.enterprise.concurrent.ManagedExecutorService") != null
        && this.linkedExecutorService == null) {
      this.linkedExecutorService = DefaultContext.bean(ManagedExecutorService.class);
    }
    if (tryToLoadClassForName("javax.enterprise.concurrent.ManagedScheduledExecutorService") != null
        && this.shceduledExecutorService == null) {
      this.shceduledExecutorService = DefaultContext.bean(ManagedScheduledExecutorService.class);
    }
  }

  private void initExecutorManual() {
    final int coreSize =
        this.config.getValue(AE_CORE_SIZE, ConvertUtils::toInteger,
            Math.max(2, Runtime.getRuntime().availableProcessors())),
        maxSize = this.config.getValue(AE_MAX_SIZE, ConvertUtils::toInteger, coreSize);
    final long keepAlive = this.config.getValue(AE_KEEP_ALIVE_VALUE, ConvertUtils::toLong, 0L);
    final String keepAliveUnit =
        this.config.getValue(AE_KEEP_ALIVE_UNIT, ConvertUtils::toString, "MILLISECONDS");
    final TimeUnit timeUnit = TimeUnit.valueOf(keepAliveUnit);
    final String rejectedHandlerName =
        this.config.getValue(AE_REJEXE_HANDLER_NAME, ConvertUtils::toString);
    final RejectedExecutionHandler rejectedHandler;
    if (rejectedHandlerName != null) {
      rejectedHandler = this.lookupByName(rejectedHandlerName, RejectedExecutionHandler.class);
    } else {
      rejectedHandler = new ThreadPoolExecutor.AbortPolicy();
    }
    final String threadFactoryName =
        this.config.getValue(AE_NAME, ConvertUtils::toString, AE_NAME_DFLT);

    if (this.linkedExecutorService == null) {
      final int capacity =
          this.config.getValue(AE_QUEUE_CAPACITY, ConvertUtils::toInteger, Integer.MAX_VALUE);
      final BlockingQueue<Runnable> linked = new LinkedBlockingQueue<Runnable>(capacity);
      final ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName + "-linked");
      this.linkedExecutorService = new ThreadPoolExecutor(coreSize, maxSize, keepAlive, timeUnit,
          linked, threadFactory, rejectedHandler);
    }

    if (this.arrayExecutorService == null) {
      final int size = this.config.getValue(AE_QUEUE_FAIR, ConvertUtils::toInteger, 1024);
      final BlockingQueue<Runnable> array = new ArrayBlockingQueue<Runnable>(size, false);
      final ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName + "-array");
      this.arrayExecutorService = new ThreadPoolExecutor(coreSize, maxSize, keepAlive, timeUnit,
          array, threadFactory, rejectedHandler);
    }

    if (this.shceduledExecutorService == null) {
      final String sthreadFactoryName =
          this.config.getValue(SE_NAME, ConvertUtils::toString, SE_NAME_DFLT);
      final ThreadFactory threadFactory = new DefaultThreadFactory(sthreadFactoryName);
      this.shceduledExecutorService =
          new ScheduledThreadPoolExecutor(coreSize, threadFactory, rejectedHandler);
    }
  }

  private <T> T lookupByName(final String name, final Class<T> type) {
    final Set<Bean<?>> tfb = this.beanManager.getBeans(name);
    final Bean<?> bean = this.beanManager.resolve(tfb);
    final CreationalContext<?> ctx = this.beanManager.createCreationalContext(null);
    if (!this.beanManager.isNormalScope(bean.getScope())) {
      this.contexts.add(ctx);
    }
    return type.cast(this.beanManager.getReference(bean, type, ctx));
  }

  static class DefaultThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    DefaultThreadFactory(String name) {
      SecurityManager s = System.getSecurityManager();
      this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = name + "-pool-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t =
          new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
      if (t.isDaemon()) {
        t.setDaemon(false);
      }
      if (t.getPriority() != Thread.NORM_PRIORITY) {
        t.setPriority(Thread.NORM_PRIORITY);
      }
      return t;
    }
  }
}
