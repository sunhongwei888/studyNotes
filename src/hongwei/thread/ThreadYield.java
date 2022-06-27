package hongwei.thread;

public class ThreadYield {
    public static void main(String[] args) {
        MyYield myYield = new MyYield();
        new Thread(myYield,"a").start();
        new Thread(myYield,"b").start();
    }
}

class MyYield implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+"执行开始");
        Thread.yield();
        System.out.println(Thread.currentThread().getName()+"执行结束");
    }
}