package com.hongwei.thread;

public class TheardDaemon {
    public static void main(String[] args) {
        God god = new God();
        You you = new You();

        Thread gThread = new Thread(god);
        Thread yThread = new Thread(you);

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
        for (int i = 0; i < 3000; i++) {
            System.out.println("开心"+i);
        }
        System.out.println("结束");
    }
}
