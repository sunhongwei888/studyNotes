package hongwei.thread;

public class TestThread4 implements Runnable{
    private String url;
    private String name;

    public TestThread4(String url,String name){
        this.url=url;
        this.name=name;
    }

    @Override
    public void run() {
        WebDownloader webDownloader = new WebDownloader();
        webDownloader.downloader(url,name);
        System.out.println("下载了文件名："+name);
    }


    public static void main(String[] args) {
        TestThread2 t1 = new TestThread2("https://img1.baidu.com/it/u=722430420,1974228945&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500","1.jpg");
        TestThread2 t2 = new TestThread2("https://img0.baidu.com/it/u=3798217922,3880088897&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500","2.jpg");
        TestThread2 t3 = new TestThread2("https://img1.baidu.com/it/u=902830885,4052311299&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500","3.jpg");
        new Thread(t1).start();
        new Thread(t2).start();
        new Thread(t3).start();
    }
}
