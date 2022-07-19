package hongwei.javaSE.thread;

public class TestThread5 implements Runnable{
    private int ticketNum = 10;
    @Override
    public void run() {
        while (true){
            if(ticketNum<=0){
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"拿到了第"+ticketNum--+"张票");
        }
    }

    public static void main(String[] args) {
        TestThread5 testThread5 = new TestThread5();
        new Thread(testThread5,"张三").start();
        new Thread(testThread5,"李四").start();
        new Thread(testThread5,"王五").start();
    }
}
