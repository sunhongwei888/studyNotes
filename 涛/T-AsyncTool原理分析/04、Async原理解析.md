### 1、AsyncTool总览

####  1.1、项目包结构：



<img src="https://s2.loli.net/2022/06/12/b8HC36TcN1zqMBV.png" alt="image-20220603195716881" style="zoom:50%;" />

类很少，核心代码就在WorkerWrapper和Acync类当中。

| 核心类                | 代码行数 |
| --------------------- | -------- |
| Async：执行器类       | 158行    |
| WorkerWrapper：包装类 | 624行    |
| SystemClock：时钟类   | 61行     |



#### 1.2、类关系结构：

![、](https://s2.loli.net/2022/06/12/9QKGEcqDIimlhoa.png)

#### 1.3 、简单的调用流程

整个框架的调用流程特别简单，Async执行器触发WorkerWrapper包装器类执行，WorkerWrapper中包装了IWorker和ICallback，以及建立了多个Worker之间的相互依赖模型。根据多个Worker之间的相互依赖模型，加上超时、异常的处理，编排整个任务链的串并行执行。

![image-20220603200642390](https://s2.loli.net/2022/06/12/6SGWcdsY2oqLZQ8.png)



### 2、回调接口：Iworker、Icallback



2个功能接口使用**@FunctionalInterface**修饰，表示函数式接口。可以使用Lambda表达式的方式创建。

T，V两个泛型，分别是入参和出参类型。多个不同的worker之间，分别可以有不同的入参、出参类型



（1）IWorker：每Worker最小执行单元需要实现该接口。

- action：处理耗时操作的方法，参数allWrappers是持有所有当前全组Wrapper的引用，key是id。可以通过这个参数，获取其他Worker的执行结果。
- defaultValue：超时、异常时的默认返回值。

```java
/**
 * 每个最小执行单元需要实现该接口
 * T，V两个泛型，分别是入参和出参类型
 *
 * 多个不同的worker之间，没有关联，分别可以有不同的入参、出参类型
 */
@FunctionalInterface
public interface IWorker<T, V> {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object      object
     * @param allWrappers 持有所有Wrapper的引用，key是id
     */
    V action(T object, Map<String, WorkerWrapper> allWrappers) throws InterruptedException;

    /**
     * 超时、异常时，返回的默认值
     *
     * @return 默认值
     */
    default V defaultValue() {
        return null;
    }
}
```

（2）ICallback：回调接口。

- begin：每个执行单元Worker执行前调用begin方法
- result：Worker执行完毕后调用，带着执行成功、失败、原始入参、和详细的结果。

```java
/**
 * 每个执行单元Worker执行前调用begin，执行后调用result
 */
@FunctionalInterface
public interface ICallback<T, V> {

    /**
     * 任务开始的监听
     */
    default void begin() {

    }

    /**
     * worker执行完毕后，会回调该接口，带着执行成功、失败、原始入参、和详细的结果。
     */
    void result(boolean success, T param, WorkResult<V> workResult);
}
```

（3）DefaultCallback：默认的回调接口。如果Worker没有实现ICallback，则默认回调这个方法，这是一个空的方法。

```java
/**
 * 默认回调类，如果不设置的话，会默认给这个回调
 */
public class DefaultCallback<T, V> implements ICallback<T, V> {
    @Override
    public void begin() {
        
    }

    @Override
    public void result(boolean success, T param, WorkResult<V> workResult) {

    }

}
```



### 3、包装类WorkerWrapper

- 包装类WorkerWrapper：对Worker任务单元（IWorker）和回调（ICallback）的包装，是一个 最小的调度单元。客户端通过编排wrapper之间的关系，达到组合各个worker顺序的目的。

- DpendWrapper：依赖的任务，可以指定依赖任务是否是must，如果是must，则依赖任务必须执行完毕，才能执行自己。

<img src="https://s2.loli.net/2022/06/12/237kD1NIdhAY8yz.png" alt="image-20220603193931562" style="zoom:50%;" />



（1）WorkerWrapper源码

```java
/**
 * 对每个worker及callback进行包装
 */
public class WorkerWrapper<T, V> {
    //该wrapper的唯一标识
    private String id;
    //worker将来要处理的param
    private T param;
    private IWorker<T, V> worker;
    private ICallback<T, V> callback;
    /**
     * 在自己后面的wrapper，如果没有，自己就是末尾；如果有一个，就是串行；如果有多个，有几个就需要开几个线程</p>
     */
    private List<WorkerWrapper<?, ?>> nextWrappers;
    /**
     * 依赖的wrappers，有2种情况，1:必须依赖的全部完成后，才能执行自己 2:依赖的任何一个、多个完成了，就可以执行自己
     * 通过must字段来控制是否依赖项必须完成
     */
    private List<DependWrapper> dependWrappers;
    /**
     * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
     * <p>
     * 0-init, 1-finish, 2-error, 3-working
     */
    private AtomicInteger state = new AtomicInteger(0);
    /**
     * 收集所有的wrapper，key是id，以便用于在Worker工作单元中，获取任意Worker的执行结果。
     */
    private Map<String, WorkerWrapper> forParamUseWrappers;
    /**
     * 存放任务结果，action中的返回值会赋值给它，在result的回调中，可以拿到这个结果。
     */
    private volatile WorkResult<V> workResult = WorkResult.defaultResult();
    /**
     * 是否在执行自己前，去校验nextWrapper的执行结果<p>
     * 1   4
     * -------3
     * 2
     * 如这种在4执行前，可能3已经执行完毕了（被2执行完后触发的），那么4就没必要执行了。
     * 注意，该属性仅在nextWrapper数量<=1时有效，>1时的情况是不存在的
     */
    private volatile boolean needCheckNextWrapperResult = true;
}
```

几个重要的属性：

1. nextWrappers：后继任务，可以指定多个。如果为null，则当前任务就是最后一个任务节点。如果只有1个任务，就是串行执行场景，使用当前的线程来执行next任务；如果有多个任务，就是并行场景，通过多线程处理next任务。

2. dependWrappers：依赖任务，可以指定多个。如果为null，当前任务是最开始的任务节点。DependWrapper可以通过must控制依赖任务是否必须完成。

   有2种情况：

   - 必须依赖的全部完成后，才能执行自己 
   - 可以具体指定依赖的任何一个、多个完成了，就可以执行自己

   ```java
   /**
    * 对依赖的wrapper的封装
    */
   public class DependWrapper {
       private WorkerWrapper<?, ?> dependWrapper;
       /**
        * 是否该依赖必须完成后才能执行自己.<p>
        * 因为存在一个任务，依赖于多个任务，是让这多个任务全部完成后才执行自己，还是某几个执行完毕就可以执行自己
        */
       private boolean must = true;
   }
   ```

3. forParamUseWrappers：运行过程中，使用Map收集所有的wrapper。key是id，value是任务的返回值。以便用于在Worker工作单元中action方法里，获取任意Worker的执行结果。有相互依赖关系的任务，可以通过这个参数获取其他任务的结果。

4. state：用来标记Worker的运行状态，AsyncTool框架内部运行时使用，不对外暴露。这个字段可以保证任务不被重复执行。

   ```java
   /**
        * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
        * <p>
        * 0-init, 1-finish, 2-error, 3-working
        */
       private AtomicInteger state = new AtomicInteger(0);
   ```

   <img src="https://s2.loli.net/2022/06/12/moNsc5AWYCqDEOz.png" alt="WorkerWrapper的运行状态" style="zoom:50%;" />

5. workResult：WorkerWrapper的运行结果，其中包含了：结果状态标记resultState、Worker返回值result、异常信息ex

   workResult对AsnycTool框架外部暴露。当前任务action方法可以获取其他worker任务的返回值；当前任务result回调接口可以处理这个返回值；并且如果超时、异常了，可以拿到异常信息。

   ```java
   //执行结果
   public class WorkResult<V> {
       //结果
       private V result;
       //结果状态
       private ResultState resultState;
     	//异常信息
       private Exception ex;
   }
   ```

​		结果状态resultState有如下几种状态标记：

​									<img src="https://s2.loli.net/2022/06/12/XyCDiOdExz3bA47.png" alt="回调函数中WorkResult几种状态" style="zoom:50%;" />





### 4、依赖模型 WorkerWrapper的构建过程。



1、WorkerWrapper.Builder：使用建造者模式，一步步构建WorkerWrapper包装类，WorkerWrapper是用来编排的最小的调度单元。

建造者模式：创建型模式，使用多个简单的对象，一步一步构建成一个复杂的对象。

```java
//使用建造者模式构建WorkerWrapper
WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerA")
        .worker(workerA)
        .callback(workerA)
        .param(null)
        .depend(wrapperB, wrapperC)
        .build();
```



2、Builder中的属性和WorkerWrapper中是一样的，主要是通过build方法，构建WorkerWrapper包装类。

```java
public static class Builder<W, C> {
    /**
     * 该wrapper的唯一标识
     */
    private String id = UUID.randomUUID().toString();
    /**
     * worker将来要处理的param
     */
    private W param;
    private IWorker<W, C> worker;
    private ICallback<W, C> callback;
    /**
     * 自己后面的所有
     */
    private List<WorkerWrapper<?, ?>> nextWrappers;
    /**
     * 自己依赖的所有
     */
    private List<DependWrapper> dependWrappers;
    /**
     * 存储强依赖于自己的wrapper集合
     */
    private Set<WorkerWrapper<?, ?>> selfIsMustSet;

    private boolean needCheckNextWrapperResult = true;
}
```



3、Builder.depend()：用于绑定当前WorkerWrapper任务前置依赖了哪些任务，可以指定依赖项是否必须执行完成，才能执行自己。

```java
//public Builder<W, C> depend(WorkerWrapper<?, ?>... wrappers) 
//public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper)
public Builder<W, C> depend(WorkerWrapper<?, ?> wrapper, boolean isMust) {
    if (wrapper == null) {
        return this;
    }
    DependWrapper dependWrapper = new DependWrapper(wrapper, isMust);
    if (dependWrappers == null) {
        dependWrappers = new ArrayList<>();
    }
    dependWrappers.add(dependWrapper);
    return this;
}
```



4、Builder.next()：用于绑定当前WorkerWrapper任务的后置任务节点，可以是多个，后置任务一定是强依赖于自己的。

```java
//public Builder<W, C> next(WorkerWrapper<?, ?>... wrappers)
public Builder<W, C> next(WorkerWrapper<?, ?> wrapper, boolean selfIsMust) {
    if (nextWrappers == null) {
        nextWrappers = new ArrayList<>();
    }
    nextWrappers.add(wrapper);

    //强依赖自己
    if (selfIsMust) {
        if (selfIsMustSet == null) {
            selfIsMustSet = new HashSet<>();
        }
        selfIsMustSet.add(wrapper);
    }
    return this;
}
```



5、Builder.build()：构建过程。绑定了任务的前置依赖和后置依赖。最终形成任务的相互依赖关系。

```java
public WorkerWrapper<W, C> build() {
  //new WorkerWrapper
  WorkerWrapper<W, C> wrapper = new WorkerWrapper<>(id, worker, param, callback);
  wrapper.setNeedCheckNextWrapperResult(needCheckNextWrapperResult);
  //1.添加前置依赖
  if (dependWrappers != null) {
    for (DependWrapper workerWrapper : dependWrappers) {
      workerWrapper.getDependWrapper().addNext(wrapper);
      wrapper.addDepend(workerWrapper);
    }
  }
  //2.添加后置依赖
  if (nextWrappers != null) {
    for (WorkerWrapper<?, ?> workerWrapper : nextWrappers) {
      boolean must = false;
      if (selfIsMustSet != null && selfIsMustSet.contains(workerWrapper)) {
        must = true;
      }
      //2.1.后置任务的前置依赖是自己
      workerWrapper.addDepend(wrapper, must);
      //2.2.添加后置任务
      wrapper.addNext(workerWrapper);
    }
  }

  return wrapper;
}
```



6、构建过程图解：一步一步形成相互依赖关系。

（1）起始节点A的dependwrappers是null

（2）结束节点F的nextWrappers是null

<img src="https://s2.loli.net/2022/06/12/svagjXSbwBYdyzZ.png" alt="WorkerWrapper.Builder构建过程。串并行相互依赖" style="zoom:200%;" />



###  5、AsyncTool执行器工作过程

当一组WorkerWrapper通过以上第4步构建完之后，一组任务就编排好顺序了，就调用Async执行器执行任务beginWork。

```java
//执行器开始工作
Async.beginWork(1000, pool, wrapperA);

//（整组任务的）超时时间
//自定义线程池
//开始的任务节点，可以是多个
beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper)
```

这里需要注意，beginWork中的Wrapper是最先执行的任务，是编排任务当中开始的节点，也就是说它是没有dependWrapper前置依赖的。beginWork开始的任务可以是多个，如果存在开始并行多个任务的，需要把多个任务都传进来。

执行器beginWork的3个参数，分别是：

- `timeout`：整组任务的超时时间，
- `executorService`：用户自定义线程池
- `workerWrapper`：开始的任务节点。

其中，timeout超时时间是整组任务的超时时间，如果当前任务超时，则当前任务FastFail，next任务继续执行。如果超过总得超时时间，则所有待执行、执行中的任务FastFail，返回默认值。

executorService支持用户自定义线程池，如果未设置，则使用不定长线程池Executors.newCachedThreadPool。

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



Async执行器代码如下：

```java
//执行器
public class Async {
    /**
     * 默认不定长线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    /**
     * 注意，这里是个static，同一组任务，只能使用一个自定义线程池
     */
    private static ExecutorService executorService;
  
		//开始工作
    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper) {
        if(workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<WorkerWrapper> workerWrappers =  Arrays.stream(workerWrapper).collect(Collectors.toList());
        return beginWork(timeout, executorService, workerWrappers);
    }
}
```



执行器`Async.beginWork()`具体执行逻辑如下：

<img src="https://s2.loli.net/2022/06/12/ZwhVie8SjOQnpIA.png" alt="beginWork的执行流程" style="zoom:50%;" />

主要分为3步工作：

1. CompletableFuture循环提交任务，调用 `wrapper.work()`
2. 阻塞获取结果（带有超时监控的）：`CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);`
3. 全组任务超时异常处理

```java
public static boolean beginWork(long timeout, ExecutorService executorService, List<WorkerWrapper> workerWrappers) {
    if(workerWrappers == null || workerWrappers.size() == 0) {
        return false;
    }
    
    //保存线程池变量
    Async.executorService = executorService;
    //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
    Map<String, WorkerWrapper> forParamUseWrappers = new ConcurrentHashMap<>();
    CompletableFuture[] futures = new CompletableFuture[workerWrappers.size()];
    
    //1、开始工作
    for (int i = 0; i < workerWrappers.size(); i++) {
        WorkerWrapper wrapper = workerWrappers.get(i);
        futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
    }
    //2、阻塞获取结果
    try {
        CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
        return true;
    //3、超时异常处理
    } catch (TimeoutException e) {
        Set<WorkerWrapper> set = new HashSet<>();
        //3.1、递归获取所有任务
        totalWorkers(workerWrappers, set);
        //3.2、循环停止所有尚未执行、正在执行的任务。将state状态设置为ERROR，返回改任务设置的默认值。
        for (WorkerWrapper wrapper : set) {
            wrapper.stopNow();
        }
        return false;
    }
}
```



先来看一下，超时异常处理逻辑吧。如上代码中分为2步。3.1 递归获取所有任务。3.2 停止所有尚未执行、正在执行的任务。

3.1。递归获取所有的WorkerWrapper，通过一个Set将所有WorkerWrapper通过递归的方式统计起来。

```java
//1.递归获取所有的WorkerWrapper
private static void totalWorkers(List<WorkerWrapper> workerWrappers, Set<WorkerWrapper> set) {
    set.addAll(workerWrappers);
    for (WorkerWrapper wrapper : workerWrappers) {
        if (wrapper.getNextWrappers() == null) {
            continue;
        }
        List<WorkerWrapper> wrappers = wrapper.getNextWrappers();
      	//递归
        totalWorkers(wrappers, set);
    }

}
```

3.2。根据3.1中统计的任务集合。循环停止所有尚未执行、正在执行的任务。注意已经执行完毕的任务是不处理的（包括异常的）。

```java
/**
 * 总控制台超时，停止所有任务
 */
public void stopNow() {
    if (getState() == INIT || getState() == WORKING) {
        fastFail(getState(), null);
    }
}
```

3.2 。`fastFail()` 首页通过CAS方式，将WorkerWrapper的state属性设置为ERROR状态；并且将workResult设置为默认值，然后调用`callback.result()`回调方法处理。

```java
/**
 * 快速失败
 */
private boolean fastFail(int expect, Exception e) {
    //试图将它从expect状态,改成Error
    if (!compareAndSetState(expect, ERROR)) {
        return false;
    }

    //只要任务没有完成就处理；完成的就不管了，包括超时的和异常的任务
    if (checkIsNullResult()) {
        if (e == null) {
            workResult = defaultResult();
        } else {
            workResult = defaultExResult(e);
        }
    }
		//回调result
    callback.result(false, param, workResult);
    return true;
}
```





### 6、 WorkerWrapper处理任务

下面一起看任务执行、依赖depend、后继next的任务是如何处理的具体细节。

#### 6.1、核心流程：运筹帷幄 WorkerWrapper.work()

`WorkerWrapper.work()` 方法是WorkerWrapper任务单元执行的入口。每次处理一个任务。

1、`work()`方法的定义：

```java
work(ExecutorService executorService, WorkerWrapper fromWrapper, long remainTime, Map<String, WorkerWrapper> forParamUseWrappers) 
```

- executorService：自定义线程池，不自定义的话，就走默认的COMMON_POOL。**默认的线程池是不定长线程池**。关于不定长线程池，在《第5步AsyncTool执行器工作过程》中已经分析了。
- fromWrapper：本次work是由哪个上游WorkerWrapper发起的。
- remainTime：剩余的时间，用来监控任务超时的。随着一组任务的执行，这个值从全局设置的timeout时间逐渐减少，当remainTime<=0时，任务就超时了。
- forParamUseWrappers：缓存一组任务所有的WorkerWrapper。key：id，value：WorkerWrapper引用。



2、`work()` 具体执行逻辑。主要分为6步：

1. 缓存所有WorkerWrapper
2. 任务超时处理
3. Check是否执行过了，避免重复处理
4. Check 后继next是否已经开始执行了，避免多余的处理
5. 没有依赖Wrapper情况处理，则当前任务就是起始节点。
6. 有依赖Wrapper情况处理，又区分只有1个依赖任务，或者有多个依赖任务的处理。

<img src="https://s2.loli.net/2022/06/12/5vbDQ4f6GzX7JZk.png" alt="WorkerWrapper.work的执行流程" style="zoom:50%;" />

3、`work()` 具体源码及注释如下： 结合上图的步骤看代码

```java
/**
* 开始工作
* fromWrapper代表这次work是由哪个上游wrapper发起的
*/
private void work(ExecutorService executorService, WorkerWrapper fromWrapper, long remainTime, Map<String, WorkerWrapper> forParamUseWrappers) {
  //引用指向
  this.forParamUseWrappers = forParamUseWrappers;
  
  //1.收集所有的wrapper，key是id，以便用于在Worker工作单元中，获取任意Worker的执行结果。
  forParamUseWrappers.put(id, this);
  
  //时钟类获取当前时间
  long now = SystemClock.now();

  //2.总的已经超时了，就快速失败，进行下一个
  if (remainTime <= 0) {
    fastFail(INIT, null);
    beginNext(executorService, now, remainTime);
    return;
  }

  //3.如果自己已经执行过了，继续处理下一个任务
  //可能有多个依赖，其中的一个依赖已经执行完了，并且自己也已开始执行或执行完毕。当另一个依赖执行完毕，又进来该方法时，就不重复处理了
  if (getState() == FINISH || getState() == ERROR) {
    beginNext(executorService, now, remainTime);
    return;
  }

  //4.如果在执行前需要校验nextWrapper的状态，仅在nextWrappers <= 1时有效
  if (needCheckNextWrapperResult) {
    //如果自己的next链上有已经出结果或已经开始执行的任务了，自己就不用继续了，SKIP跳过任务，不执行。
    if (!checkNextWrapperResult()) {
      //FastFail SKIP，new SkippedException()
      fastFail(INIT, new SkippedException());
      beginNext(executorService, now, remainTime);
      return;
    }
  }

  //5.如果没有任何依赖，说明自己就是第一批要执行的
  if (dependWrappers == null || dependWrappers.size() == 0) {
    //5.1 执行当前任务
    fire();
    //5.2 开始后继任务
    beginNext(executorService, now, remainTime);
    return;
  }

  /*如果有前方依赖，存在两种情况
         一种是前面只有一个wrapper。即 A  ->  B
        一种是前面有多个wrapper。A C D ->   B。需要A、C、D都完成了才能轮到B。但是无论是A执行完，还是C执行完，都会去唤醒B。
        所以需要B来做判断，必须A、C、D都完成，自己才能执行 */

  //6.处理前置有依赖的情况
  //6.1只有一个依赖
  if (dependWrappers.size() == 1) {
    //6-1.1：依赖任务正常结束了，就执行自己
    doDependsOneJob(fromWrapper);
    //6-1.2：开始后继任务
    beginNext(executorService, now, remainTime);
  }
  //6.2有多个依赖时
  else {
    //6-2.1：多个依赖任务的判断处理
    doDependsJobs(executorService, dependWrappers, fromWrapper, now, remainTime);
  }

}
```

第5步中，代码5.1和5.2中，`fire()` 和 `beginNext()` 是每个正常执行的任务的流程。

第6步处理前置依赖的情况，分为2种情况。

1. 只有1个依赖任务时，只要依赖任务正常结束了，就可以执行自己、以及后继任务了。
2. 有多个依赖任务时，需要判断must的依赖任务是否执行完了，如果执行完了才能执行自己。这里需要注意，多个依赖的任务，每个任务执行完，都会唤醒当前任务。如果当前任务已经被某个依赖任务执行完毕了，当下一个依赖任务执行完，还会唤醒当前任务，此时需要注意不能重复处理，上图中的第3点保证。



#### 6.2、开炮：身先士卒 fire()

1、`fire()` 方法是具体执行Worker任务的，Worker任务的超时判断，是在6.1中work处理的。

```java
/**
* 执行自己的job.具体的执行是在另一个线程里,但判断阻塞超时是在work线程
*/
private void fire() {
  //阻塞取结果
  workResult = workerDoJob();
}
```

2、`workerDoJob()`  具体的单个Worker任务执行逻辑。主要有7个步骤：

1. Check 重复执行，避免任务重复执行。
2. `CAS`设置任务状态，`state`运行状态由 `init` - > `working`
3. 回调 `callback.begin()`
4. 执行耗时操作action
5. `CAS`设置任务状态，`state`运行状态由 `working` - > `finsh`
6. 回调 `callback.result()`
7. 异常处理 `fastFail()`。`CAS`设置任务状态，`state`运行状态由 `working` - > `finsh`；设置默认值、异常信息；



<img src="https://s2.loli.net/2022/06/12/kLJ7CM9o8rudmyx.png" alt="fire" style="zoom:50%;" />

3、`workerDoJob()` 具体源码及注释如下：

```java
/**
* 具体的单个worker执行任务
*/
private WorkResult<V> workerDoJob() {

    //1.Check重复执行
    if (!checkIsNullResult()) {
      return workResult;
    }
  
    try {
      //2.设置Wrapper状态为Working
      //如果已经不是init状态了，说明正在被执行或已执行完毕。这一步很重要，可以保证任务不被重复执行
      if (!compareAndSetState(INIT, WORKING)) {
        return workResult;
      }

      //3.回调begin
      callback.begin();

      //4.执行耗时操作action
      V resultValue = worker.action(param, forParamUseWrappers);

      //5.设置Wrapper状态为FINISH
      //如果状态不是在working,说明别的地方已经修改了
      if (!compareAndSetState(WORKING, FINISH)) {
        return workResult;
      }

      workResult.setResultState(ResultState.SUCCESS);
      workResult.setResult(resultValue);
      //6.回调result
      callback.result(true, param, workResult);

      return workResult;
    } catch (Exception e) {
      //7.异常处理：设置状态ERROR\EXCEPTION，结果设置为默认值
      fastFail(WORKING, e);
      return workResult;
    }
}
```



#### 6.3、接着奏乐接着舞：前赴后继 beginNext()

 当前WorkerWrapper的`fire()` 执行完毕后，就调用 `beginNext()` 方法执行后续节点。

1、`beginNext()` 的执行步骤主要有4步：

1. 判断当前任务是否有next后续任务，如果没有任务了，就是最后一个任务，就结束了。
2. next后续只有1个任务：判断next任务数量，如果数量只有1个，使用当前任务的线程执行next任务（调用`work()`方法）
3. next后续有多个任务：判断next任务数量，如果有多个，有几个任务就新起几个线程执行（调用`work()`方法）
4. 阻塞get获取结果。（针对处理next任务有多个的场景）

<img src="https://s2.loli.net/2022/06/12/qsYBmorPV1SJywE.png" alt="beginNext" style="zoom:50%;" />

2、`beginNext()` 源码及注释如下：

```java
/**
 * 进行下一个任务
 */
private void beginNext(ExecutorService executorService, long now, long remainTime) {
    //花费的时间
    long costTime = SystemClock.now() - now;

    //1.后续没有任务了
    if (nextWrappers == null) {
        return;
    }

    //2.后续只有1个任务，使用当前任务的线程执行next任务
    if (nextWrappers.size() == 1) {
        nextWrappers.get(0).work(executorService, WorkerWrapper.this, remainTime - costTime, forParamUseWrappers);
        return;
    }

    //3.后续有多个任务，使用CompletableFuture[]包装，有几个任务就起几个线程执行
    CompletableFuture[] futures = new CompletableFuture[nextWrappers.size()];
    for (int i = 0; i < nextWrappers.size(); i++) {
        int finalI = i;
        futures[i] = CompletableFuture.runAsync(() -> nextWrappers.get(finalI)
                .work(executorService, WorkerWrapper.this, remainTime - costTime, forParamUseWrappers), executorService);
    }

    //4.阻塞获取Future结果，注意这里没有超时时间，超时时间由全局统一控制。
    try {
        CompletableFuture.allOf(futures).get();
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
}
```



3、`beginNext()` 有2处细节处理：

1. `beginNext()` 中后续任务的处理，也是通过 `work()` 来处理逻辑的。注意超时时间的处理，使用 `remainTime` 剩余时间 - `costTime`花费时间，这个值在整组任务的执行过程中，是逐渐减小的。例如A、B、C串行执行，整组任务的超时时间是1000ms，A执行消耗了200ms，到B执行时，B的可用时间 = 1000-200 = 800ms，这个时间是逐渐减小的。如果这个值小于0了，说明已经超过了整组任务设定的超时时间，任务就 `FastFail()` 了。
2. `beginNext()` 中第4点针对后续有多个任务的处理，这里并没有使用带有超时的get方法。单个任务是没有超时监控的，如果要监控每个任务的超时，就需要一个额外的线程，有几个任务就需要几个线程，高并发场景下，会造成线程 ”**爆炸**“。全组任务超时，是在`Async执行器`中控制的。



####	6.4、生死相依：单依赖 doDependsOneJob()

1、`doDependsOneJob()` 方法是`work()` 方法中处理单依赖的场景。`work()` 中的第6步。6-1.1先判断依赖任务是否完成，如果完成了就执行自己，然后6-1.2再开始执行next后继任务。

```java
//6.处理前置有依赖的情况
//只有一个依赖，如果依赖任务正常结束，就执行自己，否则FastFail
if (dependWrappers.size() == 1) {
    //6-1.1：依赖任务正常结束了，就执行自己
    doDependsOneJob(fromWrapper);
    //6-1.2：开始后继任务
    beginNext(executorService, now, remainTime);
}
```



2、单依赖的场景，就是依赖任务只有1个。依赖任务和当前任务之间是串行的关系。当前任务依赖于前置任务，它对依赖任务是 ”**生死相依**“ 的。如果依赖任务超时，那当前任务也跟着超时；如果依赖任务异常了，当前任务也跟着异常。只有依赖的任务正常执行完毕了，当前任务才正常执行。

生死相依有哪几步？

1. 判断依赖任务是否超时，如果超时，则自己也超时。
2. 判断依赖任务是否异常，如果异常，则自己也异常。
3. 依赖任务正常完成了，则自己正常执行。

<img src="https://s2.loli.net/2022/06/12/m4bH5AxaMECuRBG.png" alt="执行单个依赖任务" style="zoom:50%;" />



3、单依赖 `doDependsOneJob()` 的源码逻辑比较简单。

```java
private void doDependsOneJob(WorkerWrapper dependWrapper) {
    //1.依赖超时？
    if (ResultState.TIMEOUT == dependWrapper.getWorkResult().getResultState()) {
        workResult = defaultResult();
        fastFail(INIT, null);
    } 
    //2.依赖异常？
    else if (ResultState.EXCEPTION == dependWrapper.getWorkResult().getResultState()) {
        workResult = defaultExResult(dependWrapper.getWorkResult().getEx());
        fastFail(INIT, null);
    } 
    //3.依赖正常
    else {
        //前面任务正常完毕了，该自己了
        fire();
    }
}
```



#### 6.5、良禽择木而栖：多依赖 doDependsJobs()

下图中的4️⃣号场景，就是多依赖的场景。

<img src="https://s2.loli.net/2022/06/12/29GKINldfrpq1tC.png" alt="image-20220607230648353" style="zoom:50%;" />

1、`doDependsJobs()` 是 work() 方法中的第6步，处理多个依赖的场景。如下：6-2.1：多个依赖任务的判断处理

```java
//6.处理前置有依赖的情况

//6.1只有一个依赖
if (dependWrappers.size() == 1) {
		//dosomething
}

//6.2有多个依赖时
else {
  //6-2.1：多个依赖任务的判断处理
  doDependsJobs(executorService, dependWrappers, fromWrapper, now, remainTime);
}
```



2、处理多个依赖任务，需要考虑到依赖任务的配置。分为3种情况：

1. 依赖任务全部执行完才能执行自己。
2. 指定某个依赖任务完成，就可以执行自己。
3. 依赖任务都不是must属性，也就是说不是强依赖，此时当前任务会在运行最快的那个依赖任务的线程上执行。



3、执行流程如下：

1. 判断是否有must强依赖，如果没有强依赖，当前任务就可以正常执行了。
2. 如果有强依赖，判断依赖任务是否是must？如果不是must的就return了。
3. 有强依赖，需要看依赖任务中，是否有超时或者异常的任务，如果有，当前任务也超时、异常，fastFail。
4. 有强依赖，判断依赖任务是否全部完成？如果完成了，可以执行当前任务；如果没有完成，return什么也不做。

<img src="https://s2.loli.net/2022/06/12/EtfB5rjJbLQNRFX.png" alt="执行多个依赖任务 (2)" style="zoom:50%;" />

注意：多个依赖的任务，每个任务执行完，都会唤醒当前任务。如果当前任务已经被某个依赖任务执行完毕了，当下一个依赖任务执行完后，还会唤醒当前任务，此时需要注意不能重复处理。`work()`中的第3点保证了。



4、源码及注释如下：注意 使用 `synchronized` 修饰了 `doDependsJobs()` 方法，保证了避免多线程中的多个依赖任务，使当前任务不能正确执行，或者重复执行。

```java
private synchronized void doDependsJobs(ExecutorService executorService, List<DependWrapper> dependWrappers, WorkerWrapper fromWrapper, long now, long remainTime) {
  	//如果当前依赖是非必须的，跳过不处理
  	boolean nowDependIsMust = false;
    //Set统计必须完成的上游wrapper集合
    Set<DependWrapper> mustWrapper = new HashSet<>();
    for (DependWrapper dependWrapper : dependWrappers) {
        if (dependWrapper.isMust()) {
            mustWrapper.add(dependWrapper);
        }
      	if (dependWrapper.getDependWrapper().equals(fromWrapper)) {
           nowDependIsMust = dependWrapper.isMust();
        }
    }

    //1.如果全部是不必须的条件，那么只要到了这里，就执行自己。
    if (mustWrapper.size() == 0) {
        //超时处理
        if (ResultState.TIMEOUT == fromWrapper.getWorkResult().getResultState()) {
            fastFail(INIT, null);
        } 
        //正常执行情况
        else {
            fire();
        }
        beginNext(executorService, now, remainTime);
        return;
    }
  
  	//2.如果当前依赖是非必须的，跳过不处理（非must情况）
    if (!nowDependIsMust) {
        return;
    }


    //如果fromWrapper是必须的
    boolean existNoFinish = false;
    boolean hasError = false;
    //先判断前面必须要执行的依赖任务的执行结果，如果有任何一个失败，那就不用走action了，直接给自己设置为失败，进行下一步就是了
    for (DependWrapper dependWrapper : mustWrapper) {
        WorkerWrapper workerWrapper = dependWrapper.getDependWrapper();
        WorkResult tempWorkResult = workerWrapper.getWorkResult();
        //为null或者isWorking，说明它依赖的某个任务还没执行到或没执行完
        if (workerWrapper.getState() == INIT || workerWrapper.getState() == WORKING) {
            existNoFinish = true;
            break;
        }
        if (ResultState.TIMEOUT == tempWorkResult.getResultState()) {
            workResult = defaultResult();
            hasError = true;
            break;
        }
        if (ResultState.EXCEPTION == tempWorkResult.getResultState()) {
            workResult = defaultExResult(workerWrapper.getWorkResult().getEx());
            hasError = true;
            break;
        }

    }
    //3.只要有失败、异常的
    if (hasError) {
        fastFail(INIT, null);
        beginNext(executorService, now, remainTime);
        return;
    }

    //如果上游都没有失败，分为两种情况，一种是都finish了，一种是有的在working
    //4.依赖任务都完成了，可以执行自己了。
    if (!existNoFinish) {
        fire();
        beginNext(executorService, now, remainTime);
        return;
    }
}
```



------

5、发现源码中的问题：

经过测试，源码中针对处理多依赖的场景，有一点 ”小问题“ ，如下图所示：

这个问题我准备提交issue，Fork 后提通过 Pull Request 贡献代码。感觉这个项目已经不活跃了，看作者是否处理吧。

![image-20220608233110321](https://s2.loli.net/2022/06/12/eYJvfzBF4i1QGL2.png)



