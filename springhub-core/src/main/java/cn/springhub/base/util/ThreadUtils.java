package cn.springhub.base.util;

import cn.springhub.base.builder.ThreadExecutorBuilder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 现场相关工具类
 * @author AI
 * @date 2024/12/23 13:59
 * @version 1.0
**/
public class ThreadUtils {

    /**
     * 默认线程池
     */
    public static final ExecutorService DEFAULT_EXECUTOR = create();

    /**
     * 定时任务线程池
     */
    public static final ScheduledThreadPoolExecutor DEFAULT_SCHEDULE_EXECUTOR = createScheduledExecutor();

    /**
     * 新建一个线程池
     * <pre>
     *     1、线程池大小：CPU核数 * 2
     *     2、没有最大线程数限制
     *     3、使用LinkedBlockingQueue，队列大小为1024
     *     4、线程最大空闲时间最多为60s
     * </pre>
     * @return  线程池
     */
    public static ThreadPoolExecutor create() {
        return ThreadExecutorBuilder
            .builder()
            .build();
    }

    /**
     * 新建一个线程池
     * <pre>
     *     1、线程池大小为指定大小corePoolSize
     *     2、没有最大线程数限制
     *     3、使用LinkedBlockingQueue，队列大小为1024
     *     4、线程最大空闲时间最多为60s
     * </pre>
     * @param corePoolSize 线程池大小
     * @return  线程池
     */
    public static ThreadPoolExecutor create(int corePoolSize) {
        return ThreadExecutorBuilder
            .builder()
            .setCorePoolSize(corePoolSize)
            .build();
    }

    /**
     * 新建一个线程池
     * <pre>
     *     1、线程池大小为指定大小corePoolSize
     *     2、没有最大线程数限制
     *     3、使用LinkedBlockingQueue，队列大小为指定maxQueueSize
     *     4、线程最大空闲时间最多为60s
     * </pre>
     * @param corePoolSize 线程池大小
     * @param maxQueueSize 最大任务队列大小
     * @return  线程池
     */
    public static ThreadPoolExecutor create(int corePoolSize, int maxQueueSize) {
        return ThreadExecutorBuilder
            .builder()
            .setCorePoolSize(corePoolSize)
            .setWorkQueue(new LinkedBlockingQueue<>(maxQueueSize))
            .build();
    }

    /**
     * 新建一个线程池
     * <pre>
     *     1、线程池大小为指定大小corePoolSize
     *     2、没有最大线程数限制
     *     3、使用LinkedBlockingQueue，队列大小为指定maxQueueSize
     *     4、线程最大空闲时间最多为60s
     * </pre>
     * @param corePoolSize 线程池大小
     * @param maxQueueSize 最大任务队列大小
     * @param handler 线程拒绝策略
     * @return  线程池
     */
    public static ThreadPoolExecutor create(int corePoolSize, int maxQueueSize, RejectedExecutionHandler handler) {
        return ThreadExecutorBuilder
            .builder()
            .setCorePoolSize(corePoolSize)
            .setHandler(handler)
            .setWorkQueue(new LinkedBlockingQueue<>(maxQueueSize))
            .build();
    }

    /**
     * 新建一个无缓冲等待队列线程池
     * <pre>
     *     1、初始线程数为0
     *     2、最大线程数为Integer.MAX_VALUE
     *     3、使用SynchronousQueue
     *     4、任务直接提交给线程而不保持它们
     *     5、线程最大空闲时间最多为60s
     * </pre>
     * @return  线程池
     */
    public static ThreadPoolExecutor createSynchronousExecutor() {
        return ThreadExecutorBuilder
            .builder()
            .setMaxPoolSize(Integer.MAX_VALUE)
            .setWorkQueue(new SynchronousQueue<>())
            .build();
    }

    /**
     * 新建一个有界缓存等待队列线程池
     * <pre>
     *     1、初始线程数为：CPU核数 * 2
     *     2、最大线程数为：CPU核数 * 8
     *     3、使用ArrayBlockingQueue，容量为指定的capacity
     *     4、任务直接提交给线程而不保持它们
     *     5、线程最大空闲时间最多为60s
     * </pre>
     * @return  线程池
     */
    public static ThreadPoolExecutor createArrayBlockingExecutor(int capacity) {
        return ThreadExecutorBuilder
            .builder()
            .setWorkQueue(new ArrayBlockingQueue<>(capacity))
            .build();
    }

    /**
     * 创建有调度任务线程池
     *
     * @return {@link ScheduledThreadPoolExecutor}
     * @since 5.5.8
     */
    public static ScheduledThreadPoolExecutor createScheduledExecutor() {
        return createScheduledExecutor(OsUtils.getCpuCores() * 2);
    }

    /**
     * 创建有调度任务线程池
     * <pre>
     *     适用场景：
     *     1、定时任务：适用于需要定时执行的任务，如定时数据备份、定时报表生成等
     *     2、周期性任务‌：适用于需要按照固定频率执行的任务，如每天定时清理缓存、定时检查系统状态等
     *     3、延迟任务‌：适用于需要在项目启动后延迟一段时间执行的任务，如初始化数据加载、用户登录验证等。
     * </pre>
     *
     * @param corePoolSize 初始线程池大小
     * @return {@link ScheduledThreadPoolExecutor}
     * @since 5.5.8
     */
    public static ScheduledThreadPoolExecutor createScheduledExecutor(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

    /**
     * 执行线程
     *
     * @param runnable  可运行对象
     */
    public static void execute(Runnable runnable) {
        DEFAULT_EXECUTOR.execute(runnable);
    }

    /**
     * 执行有返回值的异步方法。
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     *
     * @param runnable  可运行对象
     * @return  返回数据
     */
    public static Future<?> executeAndGet(Runnable runnable) {
        return DEFAULT_EXECUTOR.submit(runnable);
    }

    /**
     * 执行一个线程
     * 主要区别：非守护线程没有执行完成，jvm不会退出，只有所有非守护线程结束后，程序才会退出；
     *         而守护线程会在所有非守护线程执行完成后，自动终止，无论它是否完成
     * <pre>
     *     守护线程特点：
     *        1、守护线程是为其他线程服务的线程
     *        2、JVM中，程序中存在的其他线程（即非守护线程）执行完成后，守护线程会自动退出，而不需要显式地调用 Thread.stop() 或 Thread.interrupt() 来停止。简单来说，守护线程主要用于提供后台服务，它的生命周期依赖于程序中的非守护线程
     *        3、守护线程不能持有需要关闭的资源（如打开文件等）
     * </pre>
     *
     * @param runnable  可运行对象
     * @param daemon    是否是守护线程
     * @return  线程
     */
    public static Thread execute(Runnable runnable, boolean daemon) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(daemon);
        thread.start();

        return thread;
    }

    /**
     * 新建一个CompletionService，调用其submit方法可以异步执行多个任务，最后调用take方法按照完成的顺序获得其结果。<br>
     * 若未完成，则会阻塞
     *
     * @return CompletionService
     * @param <T> 回调对象类型
     */
    public static <T> CompletionService<T> createCompletionService() {
        return createCompletionService(DEFAULT_EXECUTOR);
    }

    /**
     * 新建一个CompletionService，调用其submit方法可以异步执行多个任务，最后调用take方法按照完成的顺序获得其结果。<br>
     * 若未完成，则会阻塞
     *
     * @param executorService 执行器
     * @return CompletionService
     * @param <T> 回调对象类型
     */
    public static <T> CompletionService<T> createCompletionService(ExecutorService executorService) {
        return new ExecutorCompletionService<>(executorService);
    }

    /**
     * 新建一个CountDownLatch，一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
     *
     * @param threadCount 等待完成的线程数量
     * @return CountDownLatch
     */
    public static CountDownLatch newCountDownLatch(int threadCount) {
        return new CountDownLatch(threadCount);
    }

    /**
     * 创建本地线程对象
     *
     * @param <T>           持有对象类型
     * @param isInheritable 是否为子线程提供从父线程那里继承的值
     * @return 本地线程
     */
    public static <T> ThreadLocal<T> createThreadLocal(boolean isInheritable) {
        if (isInheritable) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    }

    /**
     * 创建本地线程对象
     *
     * @param <T>      持有对象类型
     * @param supplier 初始化线程对象函数
     * @return 本地线程
     * @see ThreadLocal#withInitial(Supplier)
     * @since 5.6.7
     */
    public static <T> ThreadLocal<T> createThreadLocal(Supplier<? extends T> supplier) {
        return ThreadLocal.withInitial(supplier);
    }

    /**
     * 挂起线程
     *
     * @param seconds   秒数
     */
    public static void sleep(long seconds) {
        sleep(seconds, TimeUnit.SECONDS);
    }

    /**
     * 挂起线程
     *
     * @param time  挂起时间
     * @param unit 时间单位
     */
    public static void sleep(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开始执行一个定时任务
     *
     * @param runnable  可运行对象
     * @param period        执行周期
     * @param timeUnit      时间单位
     * @return 调度任务线程池
     */
    public static ScheduledThreadPoolExecutor schedule(Runnable runnable, long period, TimeUnit timeUnit) {
        return schedule(runnable, 0, period, timeUnit, true);
    }

    /**
     * 开始执行一个定时任务
     *
     * @param runnable  可运行对象
     * @param initialDelay  初始延迟
     * @param period        执行周期
     * @param timeUnit      时间单位
     * @param fixedRateMode 是否是fixedRate模式
     * @return 调度任务线程池
     */
    public static ScheduledThreadPoolExecutor schedule(Runnable runnable,
                                                        long initialDelay,
                                                        long period,
                                                        TimeUnit timeUnit,
                                                        boolean fixedRateMode) {
        return schedule(DEFAULT_SCHEDULE_EXECUTOR, runnable, initialDelay, period, timeUnit, fixedRateMode);
    }

    /**
     * 开始执行一个定时任务
     *
     * @param executor  定时任务线程池
     * @param runnable  可运行对象
     * @param initialDelay  初始延迟
     * @param period        执行周期
     * @param timeUnit      时间单位
     * @param fixedRateMode 是否是fixedRate模式
     * @return 调度任务线程池
     */
    public static ScheduledThreadPoolExecutor schedule(ScheduledThreadPoolExecutor executor,
                                                        Runnable runnable,
                                                        long initialDelay,
                                                        long period,
                                                        TimeUnit timeUnit,
                                                        boolean fixedRateMode) {
        if (executor == null) {
            executor = DEFAULT_SCHEDULE_EXECUTOR;
        }

        if (fixedRateMode) {
            // fixedRate 模式：以固定的频率执行。每period的时刻检查，如果上个任务完成，启动下个任务，否则等待上个任务结束后立即启动。
            executor.scheduleAtFixedRate(runnable, initialDelay, period, timeUnit);
        } else {
            // fixedDelay模式：以固定的延时执行。上次任务结束后等待period再执行下个任务。
            executor.scheduleWithFixedDelay(runnable, initialDelay, period, timeUnit);
        }

        return executor;
    }
}
