package hongwei.thread;

public class TestThread3 implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            System.out.println("我在看代码"+i);
        }
    }

    public static void main(String[] args) {
        TestThread3 testThread = new TestThread3();
        new Thread(testThread).start();

        for (int i = 0; i < 2000; i++) {
            System.out.println("我在学习多线程"+i);
        }
    }
}
