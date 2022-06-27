package hongwei.thread;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TestThread2 extends Thread{
    private String url;
    private String name;

    public TestThread2(String url,String name){
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
        t1.start();
        t2.start();
        t3.start();
    }
}

class WebDownloader{
    public void downloader(String url,String name){
        try {
            FileUtils.copyURLToFile(new URL(url),new File(name));
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("io异常");
        }
    }
}
