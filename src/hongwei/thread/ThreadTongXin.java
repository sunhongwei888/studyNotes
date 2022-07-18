package hongwei.thread;

//有三个线程，分别为ABC线程，需要线程交替打印：ABCABC…打印10遍
//分析：需要使用线程间的通信，A给B通信，A进行notifyB进行wait；B给C通信，B进行notifyC进行Wait；同理C给A通信，C进行notifyA进行wait。
//给每个线程给定编号，表明是第几个进程，再给定一个共享对象，共享对象进行notify、wait等操作，
//共享对象本身需要携带信息表明下一个执行的线程编号，如果当前线程的编号与共享对象中的信息比较，如果相等就执行，否则就阻塞。

public class ThreadTongXin {
    public static void main(String[] args) {
        NextOpt nextOpt = new NextOpt();
        nextOpt.setNextValue(0);
        ThreadMain threadMain1 = new ThreadMain(nextOpt, 0);
        ThreadMain threadMain2 = new ThreadMain(nextOpt, 1);
        ThreadMain threadMain3 = new ThreadMain(nextOpt, 2);
        new Thread(threadMain1,"1").start();
        new Thread(threadMain2,"2").start();
        new Thread(threadMain3,"3").start();
    }
}

//先创建个共享对象,携带下一个线程编号信息
class NextOpt{
    //下一个线程编号
    private Integer nextValue;

    public Integer getNextValue() {
        return nextValue;
    }

    public void setNextValue(Integer nextValue) {
        this.nextValue = nextValue;
    }
}

//线程执行类
class ThreadMain implements Runnable{
    //共享对象
    private NextOpt nextOpt;

    //打印名称从数组获取
    String[] arr= {"A","B","C"};

    //线程编号
    private int index;

    //执行次数
    int count = 0;

    public ThreadMain(NextOpt nextOpt,int index) {
        this.nextOpt = nextOpt;
        this.index = index;
    }

    @Override
    public void run() {
        while (true){
            //同步共享对象
            synchronized (nextOpt){
                //下一线程编号不是本线程编号，阻塞
                while (nextOpt.getNextValue() != index){
                    try {
                        nextOpt.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //打印
                System.out.println(Thread.currentThread().getName()+":"+arr[index]);

                //指定下一个线程编号
                nextOpt.setNextValue((index+1)%3);

                //唤醒所有阻塞线程
                nextOpt.notifyAll();

                //计数
                if (count++>9){
                    break;
                }
            }
        }
    }
}
