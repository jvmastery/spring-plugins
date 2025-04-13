package cn.jvmaster.core.builder;

import cn.jvmaster.core.util.OsUtils;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程建造器
 * @author AI
 * @date 2024/12/23 14:35
 * @version 1.0
**/
public class ThreadExecutorBuilder {
    public static final Integer DEFAULT_QUEUE_SIZE = 1024;

    /**
     * 核心线程数，即线程池大小
     * 核心线程会一直存活，即使没有任务需要执行
     * 当线程数小于核心线程数时，即使有线程空闲，线程池也会优先创建新线程处理
     * 设置allowCoreThreadTimeout=true（默认false）时，核心线程会超时关闭
     * 通常情况下，可以设置为CPU 核数 * 2 到 4
     */
    private Integer corePoolSize;

    /**
     * 允许同时执行的最大线程数
     * 当线程数>=corePoolSize，且任务队列已满时。线程池会创建新线程来处理任务
     * 当线程数=maxPoolSize，且任务队列已满时，线程池会拒绝处理任务而抛出异常
     * 通常情况下，可以设置为CPU 核数 * 4 到 8
     */
    private Integer maxPoolSize;

    /**
     * 线程存活时间
     * 当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
     * 如果allowCoreThreadTimeout=true，则会直到线程数量=0
     */
    private long keepAliveTime = TimeUnit.SECONDS.toMillis(60);

    /**
     * 是否允许核心线程超时
     */
    private boolean allowCoreThreadTimeout = false;

    /**
     * 阻塞队列，用于存放未执行的线程
     * <pre>
     * LinkedBlockingQueue: 是一个无界缓存等待队列。当前执行的线程数量达到corePoolSize的数量时，剩余的元素会在阻塞队列里等待。（所以在使用此阻塞队列时maximumPoolSizes就相当于无效了）
     *                      每个线程完全独立于其他线程。生产者和消费者使用独立的锁来控制数据的同步，即在高并发的情况下可以并行操作队列中的数据
     *                      虽然通常称其为一个无界队列，但是可以人为指定队列大小，而且由于其用于记录队列大小的参数是int类型字段，所以通常意义上的无界其实就是队列长度为 Integer.MAX_VALUE，且在不指定队列大小的情况下也会默认队列大小为 Integer.MAX_VALUE。
     *
     * SynchronousQueue： 没有容量，是无缓冲等待队列，是一个不存储元素的阻塞队列，会直接将任务交给消费者，必须等队列中的添加元素被消费后才能继续添加新的元素。
     *                    使用SynchronousQueue阻塞队列一般要求maximumPoolSizes为无界(Integer.MAX_VALUE)，避免线程拒绝执行操作
     *
     * ArrayBlockingQueue： 是一个有界缓存等待队列，可以指定缓存队列的大小，当正在执行的线程数等于corePoolSize时，多余的元素缓存在ArrayBlockingQueue队列中等待有空闲的线程时继续执行
     *                      当ArrayBlockingQueue已满时，加入ArrayBlockingQueue失败，会开启新的线程去执行，当线程数已经达到最大的maximumPoolSizes时，再有新的元素尝试加入ArrayBlockingQueue时会报错
     *
     * DelayedWorkQueue：内部元素并不是按照放入的时间排序，而是会按照延迟的时间长短对任务进行排序，内部采用的是“堆”的数据结构，可以把任务按时间进行排序，方便任务的执行
     * </pre>
     */
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_SIZE);

    /**
     * 线程工厂，用于自定义线程创建
     */
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    /**
     * 任务拒绝处理器
     * <pre>
     * 两种情况会拒绝处理任务：
     *  - 当线程数已经达到maxPoolSize，切队列已满，会拒绝新任务
     *  - 当线程池被调用shutdown()后，会等待线程池里的任务执行完毕，再shutdown。如果在调用shutdown()和线程池真正shutdown之间提交任务，会拒绝新任务
     * </pre>
     * <pre>
     * 线程池会调用rejectedExecutionHandler来处理这个任务。如果没有设置默认是AbortPolicy，会抛出异常。处理器类型：
     * - AbortPolicy 丢弃任务，抛运行时异常
     * - CallerRunsPolicy 执行任务
     * - DiscardPolicy 忽视，什么都不会发生
     * - DiscardOldestPolicy 从队列中踢出最先进入队列（最后一个执行）的任务
     * </pre>
     */
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    private ThreadExecutorBuilder() {
    }

    public ThreadExecutorBuilder setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadExecutorBuilder setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public ThreadExecutorBuilder setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadExecutorBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
        return setKeepAliveTime(unit.toMillis(keepAliveTime));
    }

    public ThreadExecutorBuilder setKeepAliveTime(Duration duration) {
        return setKeepAliveTime(duration.getSeconds() * 1000);
    }

    public ThreadExecutorBuilder setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
        this.allowCoreThreadTimeout = allowCoreThreadTimeout;
        return this;
    }

    public ThreadExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadExecutorBuilder setHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    public ThreadExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * 实例构造器
     * @return  构造器
     */
    public static ThreadExecutorBuilder builder() {
        return new ThreadExecutorBuilder();
    }

    /**
     * 创建线程池
     */
    public ThreadPoolExecutor build() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            corePoolSize == null ? OsUtils.getCpuCores() * 2 : corePoolSize,
            maxPoolSize == null ? OsUtils.getCpuCores() * 8 : maxPoolSize,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            workQueue,
            threadFactory,
            handler
        );
        if (allowCoreThreadTimeout) {
            threadPoolExecutor.allowCoreThreadTimeOut(true);
        }

        return threadPoolExecutor;
    }

}
