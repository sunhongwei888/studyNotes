package hongwei.javaSE.thread;

public class ThreadStop implements Runnable{
    private boolean flag = true;
    @Override
    public void run() {
        int i=0;
        while (flag){
            System.out.println("thread run "+i++);
        }
    }

    public void stop(){
        this.flag = false;
    }

    public static void main(String[] args) {
        ThreadStop threadStop = new ThreadStop();
        new Thread(threadStop).start();

        for (int i = 0; i < 1000; i++) {
            System.out.println("main "+i);
            if(i == 900){
                threadStop.stop();
            }
        }
    }
}
