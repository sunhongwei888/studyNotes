### 1、AsyncTool简介

![image.png](https://s2.loli.net/2022/06/12/MGlwrybcoaOASQd.png)

它是由京东零售-平台业务中心-京东APP后台-购物车研发组开源的项目，作者是武伟峰。

该框架目前正在 **京东App后台** 接受苛刻、高并发、海量用户等复杂场景业务的检验测试，随时会根据实际情况发布更新和bugFix。目前来说，2019年底上线已经稳定运行2年+

在京东集团内部也有广泛使用，健康下边部门使用的也很多。



###  2、解决什么问题？

在微服务系统中，经常会有这样的调用场景：用户请求接口，需要调用其他多个微服务接口来获取数据，最终汇总一个最终结果返回给用户。AsyncTool由此诞生。

它解决任意的多线程并行、串行、阻塞、依赖、回调的并行框架。可以任意组合各线程的执行顺序，并且带有全链路执行结果回调。是多线程编排一站式解决方案。

它是单机的，不支持分布式编排。

<img src="https://s2.loli.net/2022/06/12/3XuSQYLH9MoyJfv.png" alt="常见微服务RPC调用" style="zoom:50%;" />

如果让你来设计一个多线程支持编排、异步回调的框架，你会如何设计？没错，使用Java8的CompletableFuture可以完成大部分编排的逻辑，但是对于一些特殊的场景，它是不支持的；而且它对回调的支持并不是很完善。

如果对CompletableFuture还不是很了的同学，推荐你看这边文章。https://juejin.cn/post/6844903594165026829



### 3、如何设计一个多线程支持编排、回调的框架？

那么到底如何设计这样一个框架呢？



####  3.1、并发场景下可能存在的需求 - 任意编排、执行顺序的强依赖和弱依赖

**（1）多线程任务任意编排**：主要有以下5种场景，如下图所示。如果有更复杂的场景，使用前四种场景组合，也是可以实现的。

<img src="https://s2.loli.net/2022/06/12/dSFZhMrE8HzGTiX.png" alt="任意编排" style="zoom:50%;" />

1. 串行：A、B、C串行执行，可用的实现方式为：CompletableFuture.thenApply()、thenAccept() 和 thenRun() 都可以实现。
2. 并行：A、B、C全并行执行，可用的实现方式为：CompletableFuture.allOf()
3. 阻塞 - 先串行后并行：A执行完毕，B、C全并行执行。可用的实现方式为：CompletableFuture.runAsync()，阻塞get获取结果，再调用CompletableFuture.allOf()
4. 阻塞 -先并行后串行：B、C全并行执行，执行完毕后，A执行。可用的实现方式为：CompletableFuture.allOf(futures...).then()



**（2）执行顺序的强依赖和若依赖**：如上图中的第④种场景，A依赖于B和C。分以下三种情况

（1）B、C全部执行完毕之后，才能执行A。可用的实现方式为：CompletableFuture.allOf(futures...).then()；或者runAfterBoth

（2）B、C任意一个执行完后，就执行A。可用的实现方式为：CompletableFuture里有个anyOf(futures...).then()；或者runAfterEither

（3）指定B或者C执行完后，就执行A。CompletableFuture不支持。



#### 3.2、并发场景可能存在的需求-每个执行结果的回调

CompleteableFuture大家都用过，里面有supply、then、combine、allOf等等方法，都可以用来接收一个任务，最终将多个任务汇总成一个结果。

但有一个问题，你supply一个任务后，**这个任务就黑盒了**。如果你编排了很多个任务，每一个任务的执行情况，执行到哪一步了，每一步的执行结果情况，我们是不知道的。只能等它最终执行完毕后，最后汇总结果。

一个并行框架，它最好是对每一步的执行都能监控。每一步的执行结果，无论成功与失败，它应该有个**回调**，才算完整。拥有回调的任务，可以监控任务的执行状况，如果执行失败、超时，可以记录异常信息或者处理个性化的**默认值**。

CompleteableFuture中也有一些回调方法，例如：thenAccept()，whenComplete()，handle()，exceptionally()等，这些方法也能支持任务的回调，但是前提是任务执行了，才能完成回调。在某些场景中，有些任务单元是可能被SKIP跳过不执行的，不执行的任务也应该有回调。



#### 3.3、并发场景可能存在的需求-依赖上游的执行结果作为入参

如上图①中的串行场景。A执行完后，B任务依赖于A的返回值，C任务依赖B处理的返回值。

可用的实现方式是：CompletableFuture.thenCompose()



#### 3.4、并发场景可能存在的需求-全组任务的超时

一组任务，虽然内部的各个执行单元的时间不可控，但是可以控制全组的执行时间不超过某个值。通过设置timeOut，来控制全组的执行阈值。

```java
CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
```



#### 3.5、并发场景可能存在的需求-高性能、低线程数

该框架全程无锁，没有一个加锁的地方。

创建线程量少。如上图④中场景。A会运行在B、C执行更慢的那个单元的线程上，而不会额外创建线程。





#### 3.6、总结

一个并发框架可能需要具备哪些能力？

1、提供任何形式的串行、并行执行单元的组合

2、为每个执行单元提供执行成功、失败、超时、异常的回调

3、支持为单个执行单元设置异常、失败后的默认值

4、支持为整个group（多个任意组合的执行单元）设置超时时间。单个执行单元失败，不影响其他单元的回调和最终结果获取。如果自己依赖的任务失败，则自己也失败，并返回默认值。

5、整个group执行完毕或超时后，同步阻塞返回所有执行单元结果集，按添加的顺序返回list。也支持整个group的异步回调不阻塞主线程

6、支持每个group独享线程池，或所有group共享线程池（默认）





### 4、异步回调如何实现

上面我们总结了多线程的编排场景及实现，以及并发场景的一些潜在需求及实现。

该框架的难点和重点，主要有两点，分别是任务的**顺序编排**和任务结果的**回调**。



回调是个很有用的模式，譬如我的主线程执行过程中，要执行一个非常耗时的逻辑。为了不阻塞主线程，自然我们会想到用异步的形式去执行这个耗时逻辑，新建个线程，让这个耗时的逻辑在线程中执行，不阻塞主线程。但问题来了，异步执行没毛病，执行成功、失败后出结果了，该怎么通知主线程？



#### 4.1、CompletableFuture的回调

CompletableFuture提供了许多回调的方法，例如：thenAccept()，whenComplete()，handle()，exceptionally()等。下面列举一些比较常用的回调方法，如下：

```java
//1.计算结果完成，或者异常时执行给定action(当前线程执行)
CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action);
//2.计算结果完成，或者异常时执行给定action（另起线程执行）
CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action);
//3.执行完成时对结果进行处理，还可以处理异常
<U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> fn)
//4.异常时，返回指定结果
CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn)
```

CompleteableFuture提供的回调方法，这些方法也能支持任务的回调，但是前提是任务执行了，才能完成回调。在某些场景中，有些任务单元是可能被SKIP跳过不执行的，不执行的任务也应该有回调。



#### 4.2、Netty future中的回调

Netty中的回调是非常多的。netty中的future，可以添加Listener，当异步任务执行完毕后，主动回调一下自己就可以了。

<img src="https://s2.loli.net/2022/06/12/DTRnUsoMz4yKuvJ.png" alt="image.png"  />



整个netty里面大量充斥着类似的回调，但是如果我们要用，仅仅是针对一个或多个异步任务，希望能有个类似的回调，netty就帮不上忙了。

Netty回调的伪代码：其中doSomething是在异步线程里，而回调是在主线程里的。

```java
//主线程
main {
  //doSomething是在异步线程里，回调是在主线程
  doSomething().async().addListener(new Listener(){
    @Override
    public void complete() throws Exception {
      //do your job
    }
  });
}
```





#### 4.3、如何自己实现一个简单回调的异步任务

首先我们来拆分一下需求，我有N个耗时任务，可能是一次网络请求，可能是一个耗时文件IO，可能是一堆复杂的逻辑，我在主线程里发起这个任务的调用，但不希望它阻塞主线程，而期望它执行完毕（成功\失败）后，来发起一次回调，最好还有超时、异常的回调控制。

据此，我们拆分出几个角色：Bootstrap（主线程），Worker（异步工作单元），Listener（回调器）。然后将他们组合起来，完成异步回调。



下面来看一下worker的定义：action就可以理解为一个耗时任务。

```java
/**
 * 工作单元
 */
public interface Worker {

    /**
     * 耗时的操作，网络请求/IO等
     * @param obj
     * @return
     */
    String action(String obj);
}
```



回调器：这个listener用来做为回调，将worker的执行结果，放到result的参数里。

```java
/**
 * 回调器
 */
public interface Listener {
    void result(Object obj);
}
```



此外，我们还需要一个包装器Wrapper，来将worker和回调器包装一下。

```java
/**
 * 包装类，包装Worker和回调器
 */
@Data
public class Wrapper {
    private String param;
    private Worker worker;
    private Listener listener;

  	//添加回调器
    public void addListener(Listener listener) {
        this.listener = listener;
    }
}
```



主线程：发起任务

```java
public class Bootstrap {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        Worker worker = bootstrap.newWorker();

        Wrapper wrapper = new Wrapper();
        wrapper.setWorker(worker);
        wrapper.setParam("hello");

				//2.回调方法，输出worker中的内容
        bootstrap.doWorker(wrapper).addListener(new Listener() {
            @Override
            public void result(Object result) {
                System.out.println(Thread.currentThread().getName());
                System.out.println(result);
            }
        });
      
				//1.主线程不阻塞，打印当前线程
        System.out.println(Thread.currentThread().getName());
    }

  
    private Wrapper doWorker(Wrapper wrapper) {
        new Thread(() -> {
            Worker worker = wrapper.getWorker();
            String result = worker.action(wrapper.getParam());
            wrapper.getListener().result(result);
        }).start();

        return wrapper;
    }
    

    private Worker newWorker() {
        return new Worker() {
            @Override
            public String action(String obj) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return obj + " callback!";
            }
        };
    }

}
```

输出结果：

![image.png](https://s2.loli.net/2022/06/12/ezUoBStluJ2v8hj.png)

主线程没有被耗时的线程阻塞掉，耗时线程在执行完毕后，进行了回调。



#### 4.4、实现一个简单带有回调、超时的异步任务

```java
public class BootstrapTimeOut {


    public static void main(String[] args)  {
        BootstrapTimeOut bootstrap = new BootstrapTimeOut();
        Worker worker = bootstrap.newWorker();

        Wrapper wrapper = new Wrapper();
        wrapper.setWorker(worker);
        wrapper.setParam("hello");
      	//1.添加回调器
        wrapper.addListener(new Listener() {
            @Override
            public void result(Object result) {
                System.out.println(result);
            }
        });

      	//2.主线程执行
        System.out.println(Thread.currentThread().getName());
	
      	//3.耗时任务执行
        CompletableFuture<Wrapper> future = CompletableFuture.supplyAsync(() -> bootstrap.doWorker(wrapper));

        try {
          	//4.超时回调，耗时任务睡1000，这里超时了
            future.get(800, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            wrapper.getListener().result("time out exception");
        }
        

    }


    private Wrapper doWorker(Wrapper wrapper) {
        Worker worker = wrapper.getWorker();
        String result = worker.action(wrapper.getParam());
        wrapper.getListener().result(result);

        return wrapper;
    }


    private Worker newWorker() {
        return new Worker() {
            @Override
            public String action(String obj) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return obj + " callback!";
            }
        };
    }
}
```

执行结果如下：
![image.png](https://s2.loli.net/2022/06/12/obyekwpnj5978LT.png)



### 5、相互依赖模型的建立

所谓的相互依赖模型，通过3种状态任务状态理解。任务初始化，任务执行中，任务完成

**（1）任务初始化**：WorkerWrapper包装类是对Worker执行任务的包装，包装了worker和callback。通过WorkerWrapper.Build.build()建造者模式，构建了WorkerWrapper复杂对象。并且在构建的过程中，形成了多个WorkerWrapper之间的前置、后置节点的依赖关系。就是通过nextWrappers和dependWrappers指定WorkerWrapper的依赖关系。

另外需要注意的是，可以指定依赖节点是否是must，例如上述的第④种情况，B和C并行执行，A可以指定，B或者C完成了，就可以执行A。

**（2）任务执行中**：任务执行过程中，如果当前任务依赖的前置任务没有完成，当前任务是不能执行的。而且，如果前置依赖任务异常了，那么当前任务FastFail。

**（3）任务完成阶段**：通过forParamUserWrappers参数收集了所有WorkerWrapper的引用。支持了在各个Worker中可以获取其他wrapper的执行结果。

![image-20220603193931562](https://s2.loli.net/2022/06/12/237kD1NIdhAY8yz.png)



  










