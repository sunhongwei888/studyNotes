package hongwei.javaSE.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadSleep implements Runnable{

    @Override
    public void run() {
        Date date = new Date(System.currentTimeMillis());
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(date));
            date = new Date(System.currentTimeMillis());
        }

    }

    public static void main(String[] args) {
        ThreadSleep threadSleep = new ThreadSleep();
        new Thread(threadSleep).start();
    }
}
