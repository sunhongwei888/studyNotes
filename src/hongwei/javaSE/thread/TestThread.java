package hongwei.javaSE.thread;

public class TestThread extends Thread{
    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            System.out.println("我在看代码"+i);
        }
    }

    public static void main(String[] args) {
        TestThread testThread = new TestThread();
        testThread.start();

        for (int i = 0; i < 2000; i++) {
            System.out.println("我在学习多线程"+i);
        }
    }
}
