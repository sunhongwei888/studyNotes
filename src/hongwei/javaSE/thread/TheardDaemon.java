package hongwei.javaSE.thread;

public class TheardDaemon {
    public static void main(String[] args) {
        Thread gThread = new Thread(new God());
        Thread yThread = new Thread(new You());

        gThread.setDaemon(true);
        gThread.start();
        yThread.start();

    }
}

class God implements Runnable{
    @Override
    public void run() {
        while (true){
            System.out.println("上帝守护你");
        }
    }
}
class You implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            System.out.println("开心"+i);
        }
        System.out.println("结束");
    }
}
