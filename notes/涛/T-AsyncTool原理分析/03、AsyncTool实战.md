### 1、串行场景

<img src="https://s2.loli.net/2022/06/12/maYEoy3fMchKtq6.png" alt="串行" style="zoom:50%;" />

在实战之前，先看一下Worker最小任务单元的使用。

Worker的定义如下，**实现IWorker，ICallback函数式接口**，并重写下面的4个方法。4个方法的说明如下：

**1、begin()**：Worker开始执行前，先回调begin()

**2、action()**：Worker中执行耗时操作的地方，比如RPC接口调用。

**3、result()**：action()执行完毕后，回调result方法，可以在此处处理action中的返回值。

**4、defaultValue()**：整个Worker执行异常，或者超时，会回调defaultValue()，Worker返回**默认值**。



如果没有实现ICallback，会默认执行DefaultCallback的回调方法。DefaultCallback是一个空的回调，里面没有任何逻辑。



模拟串行场景：A任务对参数+1，之后B任务对参数+2，之后C任务对参数+3。（场景没有实际业务含义，很简单）

（1）workerA：

```java
public class WorkerA implements IWorker<Integer, Integer>, ICallback<Integer, Integer> {

    /**
     * Worker开始的时候先执行begin
     */
    @Override
    public void begin() {
        System.out.println("A - Thread:" + Thread.currentThread().getName() + "- start --" + SystemClock.now());
    }

    /**
     * Worker中耗时操作在此执行RPC/IO
     * @param object      object
     * @param allWrappers 任务包装
     * @return
     */
    @Override
    public Integer action(Integer object, Map<String, WorkerWrapper> allWrappers) {
        Integer res = object + 1;
        return res;
    }

    /**
     * action执行结果的回调
     * @param success
     * @param param
     * @param workResult
     */
    @Override
    public void result(boolean success, Integer param, WorkResult<Integer> workResult) {
        System.out.println("A - param:" + JSON.toJSONString(param));
        System.out.println("A - result:" + JSON.toJSONString(workResult));

        System.out.println("A - Thread:" + Thread.currentThread().getName() + "- end --" + SystemClock.now());
    }

    /**
     * Worker异常时的回调
     * @return
     */
    @Override
    public Integer defaultValue() {
        System.out.println("A - defaultValue");
        return 101;
    }

}
```



（2）workerB：

```java
public class WorkerB implements IWorker<Integer, Integer>, ICallback<Integer, Integer> {

    /**
     * Worker开始的时候先执行begin
     */
    @Override
    public void begin() {
        System.out.println("B - Thread:" + Thread.currentThread().getName() + "- start --" + SystemClock.now());
    }

    /**
     * Worker中耗时操作在此执行RPC/IO
     * @param object      object
     * @param allWrappers 任务包装
     * @return
     */
    @Override
    public Integer action(Integer object, Map<String, WorkerWrapper> allWrappers) {
        Integer res = object + 2;
        return res;
    }

    /**
     * action执行结果的回调
     * @param success
     * @param param
     * @param workResult
     */
    @Override
    public void result(boolean success, Integer param, WorkResult<Integer> workResult) {
        System.out.println("B - param:" + JSON.toJSONString(param));
        System.out.println("B - result:" + JSON.toJSONString(workResult));

        System.out.println("B - Thread:" + Thread.currentThread().getName() + "- end --" + SystemClock.now());
    }

    /**
     * Worker异常时的回调
     * @return
     */
    @Override
    public Integer defaultValue() {
        System.out.println("B - defaultValue");
        return 102;
    }
}
```



（3）WorkerC：

```java
public class WorkerC implements IWorker<Integer, Integer>, ICallback<Integer, Integer> {

    /**
     * Worker开始的时候先执行begin
     */
    @Override
    public void begin() {
        System.out.println("C - Thread:" + Thread.currentThread().getName() + "- start --" + SystemClock.now());
    }

    /**
     * Worker中耗时操作在此执行RPC/IO
     * @param object      object
     * @param allWrappers 任务包装
     * @return
     */
    @Override
    public Integer action(Integer object, Map<String, WorkerWrapper> allWrappers) {
        Integer res = object + 3;
        return res;
    }

    /**
     * action执行结果的回调
     * @param success
     * @param param
     * @param workResult
     */
    @Override
    public void result(boolean success, Integer param, WorkResult<Integer> workResult) {
        System.out.println("C - param:" + JSON.toJSONString(param));
        System.out.println("C - result:" + JSON.toJSONString(workResult));

        System.out.println("C - Thread:" + Thread.currentThread().getName() + "- end --" + SystemClock.now());
    }

    /**
     * Worker异常时的回调
     * @return
     */
    @Override
    public Integer defaultValue() {
        System.out.println("C - defaultValue");
        return 103;
    }
}
```



（4） 编排WorkerWrapper包装类：

上面Worker创建好之后，使用WorkerWrapper对Worker进行包装以及编排，WorkerWrapper是AsyncTool组件的最小可执行任务单元。

C是最后一步，它没有next。B的next是C，A的next是B。编排顺序就是：C <- B <- A

```java
public class Test {

    public static void main(String[] args) {
        //引入Worker工作单元
        WorkerA workerA = new WorkerA();
        WorkerB workerB = new WorkerB();
        WorkerC workerC = new WorkerC();

        //包装Worker，编排串行顺序：C <- B <- A
        //C是最后一步，它没有next
        WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
                .id("workerC")
                .worker(workerC)
                .callback(workerC)
                .param(3)//3+3
                .build();
        //B的next是C
        WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
                .id("workerB")
                .worker(workerB)
                .callback(workerB)
                .param(2)//2+2
                .next(wrapperC)
                .build();
        //A的next是B
        WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
                .id("workerA")
                .worker(workerA)
                .callback(workerA)
                .param(1)//1+1
                .next(wrapperB)
                .build();
        try {
            //Action
            Async.beginWork(1000, wrapperA);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

（5）通过执行器类Async的beginWork方法提交任务执行。

1. Timeout：全组任务超时时间设定，如果Worker任务超时，则Worker结果使用defaultValue()默认值。
2. ExecutorService executorService：自定义线程池，不自定义的话，就走默认的COMMON_POOL。**默认的线程池是不定长线程池**。
3. WorkerWrapper... workerWrapper：起始任务，可以是多个。注意不要提交中间节点的任务，只需要提交起始任务即可，编排的后续任务会自动执行。

```java
//默认不定长线程池
private static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();

Async.beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper)
```



（6）上面只是一种写法，如果觉得这种写法反人类，也可以使用depend方式编排：

```java
//A没有depend
WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerA")
        .worker(workerA)
        .callback(workerA)
        .param(1)
        .build();

//B的depend是A
WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerB")
        .worker(workerB)
        .callback(workerB)
        .param(2)
        .depend(wrapperA)
        .build();

//C的depend是B
WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerC")
        .worker(workerC)
        .callback(workerC)
        .param(3)
        .depend(wrapperB)
        .build();
//begin
Async.beginWork(1000, wrapperA);
```



运行结果：A：1+1=2；B：2+2=4；C：3+3=6

<img src="https://s2.loli.net/2022/06/12/eovYPnOr98xNkFi.png" alt="image-20220601142129209" style="zoom:50%;" />





###  2、并行场景

<img src="https://s2.loli.net/2022/06/12/5edOTvDahmurXWI.png" alt="image-20220607214004363" style="zoom:50%;" />

场景模拟：基于串行场景，。A任务对参数+1，B任务对参数+2，C任务对参数+3。并行执行。

WorkerWrapper并行编排：A\B\C都没有next和depend， 3个WorkerWrapper一起begin。Async.beginWork(1000, wrapperA, wrapperB, wrapperC);

```java
public class Test {

    public static void main(String[] args) {
        //引入Worker工作单元
        WorkerA workerA = new WorkerA();
        WorkerB workerB = new WorkerB();
        WorkerC workerC = new WorkerC();

        /**
         * 包装Worker，编排并行顺序
         */

        //A
        WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
                .id("workerA")
                .worker(workerA)
                .callback(workerA)
                .param(1)//1+1
                .build();
        //B
        WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
                .id("workerB")
                .worker(workerB)
                .callback(workerB)
                .param(2)//2+2
                .build();
        //C
        WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
                .id("workerC")
                .worker(workerC)
                .callback(workerC)
                .param(3)//3+3
                .build();
        try {
            //3个WorkerWrapper一起begin
            Async.beginWork(1000, wrapperA, wrapperB, wrapperC);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
```

执行结果：ABC分别使用不同的线程并行执行。A：1+1=2；B：2+2=4；C：3+3=6

<img src="https://s2.loli.net/2022/06/12/tb8cxX2eAyfj6DF.png" alt="image-20220601155013918" style="zoom:50%;" />





###  3、阻塞等待 - 先串行，后并行

<img src="https://s2.loli.net/2022/06/12/Za5vjkBKGIF3uMx.png" alt="image-20220607214201748" style="zoom:50%;" />

阻塞等待 - 先串行，后并行场景模拟：A先执行，对参数+1；A执行完毕之后，B\C同时并行执行，B任务基于A的返回值+2，C任务基于A的返回值+3

（1）next写法：

```java
public static void nextWork() {
    //引入Worker工作单元
    WorkerA workerA = new WorkerA();
    WorkerB workerB = new WorkerB();
    WorkerC workerC = new WorkerC();

    //C是最后一步，它没有next
    WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
            .id("workerC")
            .worker(workerC)
            .callback(workerC)
            .param(null)//没有参数，根据A的返回值+3
            .build();
    //B是最后一步，它没有next
    WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
            .id("workerB")
            .worker(workerB)
            .callback(workerB)
            .param(null)//没有参数，根据A的返回值+2
            .build();
    //A的next是B、C
    WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
            .id("workerA")
            .worker(workerA)
            .callback(workerA)
            .param(1)//1+1
      			//next是B、C
            .next(wrapperB, wrapperC)
            .build();


    try {
        //Action
        Async.beginWork(1000, wrapperA);
    } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
    }
}
```

（2）depend写法：

```java
//A没有depend，就是开始
WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerA")
        .worker(workerA)
        .callback(workerA)
        .param(1)
        .build();

//C depend A
WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerC")
        .worker(workerC)
        .callback(workerC)
        .param(null)
        .depend(wrapperA)
        .build();
//B depend A
WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerB")
        .worker(workerB)
        .callback(workerB)
        .param(null)
        .depend(wrapperA)
        .build();
```



执行结果：A：1+1 = 2；B：2+2 =4；C：3+2 = 5

<img src="https://s2.loli.net/2022/06/12/RDIgqumsKyjAkQ9.png" alt="image-20220607214523341" style="zoom:50%;" />



###  4、阻塞等待 - 先并行，后串行

<img src="https://s2.loli.net/2022/06/12/29GKINldfrpq1tC.png" alt="image-20220607230648353" style="zoom:50%;" />

场景模拟：阻塞等待 - 先并行，后串行。

B\C并行执行。B对参数+2，C对参数+3，B\C全部执行完后，A = B返回值+C返回值。

注意：需要B和C同时begin。Async.beginWork(4000, wrapperB, wrapperC);

（1）next写法：

```java
public static void nextWork() {

    //引入Worker工作单元
    WorkerA workerA = new WorkerA();
    WorkerB workerB = new WorkerB();
    WorkerC workerC = new WorkerC();

    //A是最后一步，没有next
    WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
            .id("workerA")
            .worker(workerA)
            .callback(workerA)
            .param(null)//参数是null，A = B + C
            .build();

    //C next A
    WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
            .id("workerC")
            .worker(workerC)
            .callback(workerC)
            .param(3)//3+3 = 6
            .next(wrapperA)
            .build();
    //B next A
    WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
            .id("workerB")
            .worker(workerB)
            .callback(workerB)
            .param(2)//2+2 = 4
            .next(wrapperA)
            .build();

    try {new SynchronousQueue<Runnable>();
        //Action
        Async.beginWork(4000, wrapperB, wrapperC);
    } catch (ExecutionException | InterruptedException e) {
        e.printStackTrace();
    }
}
```

（2）depend写法：

```java
//C没有depend，是起始节点
WorkerWrapper wrapperC = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerC")
        .worker(workerC)
        .callback(workerC)
        .param(3)//3+3 = 6
        .build();
//B没有depend，是起始节点
WorkerWrapper wrapperB = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerB")
        .worker(workerB)
        .callback(workerB)
        .param(2)//2+2 = 4
        .build();

//A depend B,C
WorkerWrapper wrapperA = new WorkerWrapper.Builder<Integer, Integer>()
        .id("workerA")
        .worker(workerA)
        .callback(workerA)
        .param(null)//参数是null，A = B + C
        .depend(wrapperB, wrapperC)
        .build();
```

执行结果：B：2+2=4；C：3+3 = 6；A = B+C = 10

<img src="https://s2.loli.net/2022/06/12/SEKdN8juyqeRLCb.png" alt="image-20220601205219987" style="zoom:50%;" />



###  5、异常、超时回调场景

这2种场景，可以基于以上场景微调，即可debug调试。

```java
//超时时间，线程池，初始Wrapper，多个
Async.beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper)
```

1. 基于全组设定的timeout，如果超时了，则worker中的返回值使用defaultValue()
2. 如果当前Worker任务异常了，则当前任务使用defaultValue()，并且depend当前任务的，也FastFail，返回defaultValue()

