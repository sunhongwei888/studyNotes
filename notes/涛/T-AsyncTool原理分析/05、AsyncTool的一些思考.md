### 1、为什么第一次请求耗时长？

经咨询AsyncTool作者，他们也有这种问题。

作者表示：CompletableFuture就第一次初始化时长。之前做时我就试过了。他初始化线程池那一下耗时500+，之后就不需要了

针对作者的回答，我再本地环境测试没有复现。可能在docker中的tomcat进程中运行会不一样，这个问题我觉得还需要使用arthas等工具，从头到尾的排查一下。

![what的表情包图片- 燕子图片网,一个充满欢乐的表情包、头像图片乐园](data:image/jpeg;base64,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)





### 2、全组任务超时？还是单步任务超时？

如果能够对单步任务执行超时，是不是每个执行任务的时间更可控了？

在gitee上，作者也对这个问题进行了讨论。

作者给出的解释是：如果要支持单步任务超时，每个任务的超时监控都需要一个额外的线程。会造成线程池翻倍。

退而求其次，使用了全组任务超时设置。实际上，全组任务超时，已经能满足绝大部分的场景了。



另外，在timewheel分支，实现了 一个线程监控所有任务的超时时间。原理是单线程时间轮对所有注册的wrapper进行检查。

没有深入研究，感兴趣的自行了解。

<img src="https://s2.loli.net/2022/06/12/B7CZOx1h6sDifYU.png" alt="image-20220609004940456" style="zoom:50%;" />





### 3、关于线程池的思考？

作者在issue上推荐一个tomcat只使用一个不定长线程池。

AsyncTool关于多线程并发执行的问题。如果不指定线程池，默认使用不定长线程池。

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
```

**注意**，newCachedThreadPool() 它的核心线程数是0，最大线程数是Integer.MAX_VALUE。可以说它的线程数是可以无限扩大的。

他使用的队列是没有存储空间的，只要请求过来了，如果没有空闲线程，就会创建一个新的线程。

这种不定长线程池，适合处理执行时间比较短的任务。在高并发下，如果在耗时长的RPC\IO操作中使用该线程池，势必会造成系统 **线程爆炸**。

详见：https://gitee.com/jd-platform-opensource/asyncTool/issues/I4HLQE?from=project-issue



AsyncTool作者京东使用的场景多数为低耗时（10ms）高并发，瞬间冲击的场景。这种高并发，且任务耗时较少的，适合使用不定长线程池。

但是这种低耗时的场景也不多，对于耗时较长的场景，使用自定义线程池，可以避免那些耗时长的任务造成线程 ”**爆炸** “，容错率更高。





### 4、AsyncTool框架执行的任务如何监控？

（1）背景：在618备战前，一般都会对系统进行压测。使用AsyncTool框架的接口，压测结果不达标，通过UMP的调用链追踪，发现trace调用链出现”断点“了，使用AsyncTool框架执行的任务调用链丢了，这是什么情况？



trace调用链：

-  trace：又名调用链，指pfinder对一次单一业务请求调用过程的跟踪

- traceId：每一个trace都会被分配一个全局唯一的ID



（2）为什么AsyncTool框架执行的任务trace丢了呢？

业务开发中，一般都会使用ThreadLocal保存一些上下文信息，但是在线程池中执行对应逻辑时，由于是不同线程所以无法获取之前线程的上下文信息。

`JDK`的[`InheritableThreadLocal`](https://docs.oracle.com/javase/10/docs/api/java/lang/InheritableThreadLocal.html)类可以完成父线程到子线程的值传递。但对于使用线程池等会池化复用线程的情况，线程由线程池创建好，并且线程是池化起来反复使用的；这时父子线程关系的`ThreadLocal`值传递已经没有意义，应用需要的实际上是把 **任务提交给线程池时**的`ThreadLocal`值传递到 **任务执行时**。



（3）如何解决？

京东的`Pfinder`和阿里李鼎大神开源的`TransmittableThreadLocal`(`TTL`)框架都可以解决分布式系统trace的传递问题。`TTL`对`JDK`的[`InheritableThreadLocal`](https://docs.oracle.com/javase/10/docs/api/java/lang/InheritableThreadLocal.html)类进行了增强，在使用线程池等会池化复用线程的执行组件情况下，提供`ThreadLocal`值的传递功能，解决异步执行时上下文传递的问题。

他们的使用也很简单，对线程池包装一下即可。

```java
//Pfinder
PfinderContext.executorServiceWrapper(new BaseThreadPoolExecutor(1000, 1000, 2, TimeUnit.MINUTES,
        new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder().setNameFormat("skuCardWorkerCommonPool-pool-%d").build(),
        new PoolPolicy("商品卡片WORKER版线程池")));

//TTL
ExecutorService executorService = TtlExecutors.getTtlExecutorService(executorService);
```



这里没有做深入研究，感兴趣的小伙伴可以自行了解。

TTL:https://github.com/alibaba/transmittable-thread-local

Pfinder:https://cf.jd.com/pages/viewpage.action?pageId=243749048





### 5、关于缓存时钟类的思考：SystemClock

AsyncTool框架任务的超时控制，是通过缓存时钟类 `SystemClock`控制的，获取当前时间使用`SystemClock.now()`，为什么不直接使用`System.currentTimeMillis()`？

先看一下 `SystemClock`时钟类的设计。

（1）使用单例模式创建了一个时钟类`SystemClock`

（2）`ScheduledExecutorService` 是 `ExecutorService` 的子类，它基于 `ExecutorService` 功能实现周期执行的任务。

（3）创建了一个守护线程，每1ms对 `AtomicLong now`进行更新 `System.currentTimeMillis()`，因为守护线程的执行周期是每1ms执行一次，这里是有1ms的延迟。

```java
/**
 * 用于解决高并发下System.currentTimeMillis卡顿
 * @author lry
 */
public class SystemClock {

    private final int period;

    private final AtomicLong now;

    /**
     * 单例模式
     */
    private static class InstanceHolder {
        private static final SystemClock INSTANCE = new SystemClock(1);
    }

    private SystemClock(int period) {
        this.period = period;
        this.now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 守护线程
     * 每1ms写一次now系统当前时间。
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            //守护线程，必须在线程启动前设置
            thread.setDaemon(true);
            return thread;
        });
        //定时周期执行任务
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }

    private long currentTimeMillis() {
        return now.get();
    }

    /**
     * 用来替换原来的System.currentTimeMillis()
     */
    public static long now() {
        return instance().currentTimeMillis();
    }
}

```

通过jstack可以看到后台运行着一个守护线程；

![image-20220601214620578](https://s2.loli.net/2022/06/12/AkzqcW5wLujTMPU.png)



`System.currentTimeMillis()`真的有性能问题吗？



说有性能问题的，有以下几种观点：

- System.currentTimeMillis**要访问系统时钟**，这属于临界区资源，并发情况下必然导致多线程的争用。
- System.currentTimeMillis()之所以慢是因为去**跟系统打了一次交道**，System.currentTimeMillis **比 new一个普通对象耗时还要高100倍左右**



Linux和Windows系统的时钟实现不同，windows系统的时钟访问，比linux要快很多。

Linux系统有2中常用的时钟源，TSC、HPET，这些时钟源都属于硬件；

根据测试，Linux操作系统下，TSC时钟源比HPET时钟源快10倍以上。



热心网友：

这个问题根源上是要切换时钟策略，本身代码角度来说应该是没有啥优化的必要。我就遇到过这个问题，场景就是高并发和大数据量，调用这个方法，现场部署的时候，相同程序个别机器性能奇差，一启动CPU 就打满了，机器都变的很卡，最终通过切换TSC时钟策略解决的。切换不了的机器，直接让服务器厂商把主板换了，然后就好了



具体可阅读如下文章：

- [System.currentTimeMillis的性能真有如此不堪吗？](https://juejin.cn/post/6887743425437925383#comment)

- [缓慢的 currentTimeMillis()](http://pzemtsov.github.io/2017/07/23/the-slow-currenttimemillis.html)



### 6、扩展

编排框架有哪些：

1、Gobrs-Async：https://async.sizegang.cn/

2、Disruptor：http://ifeve.com/disruptor-dsl/

https://github.com/LMAX-Exchange/disruptor

3、[parseq](https://github.com/linkedin/parseq)：https://github.com/linkedin/parseq



