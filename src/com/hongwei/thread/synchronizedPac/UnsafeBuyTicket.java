package com.hongwei.thread.synchronizedPac;

public class UnsafeBuyTicket {
    public static void main(String[] args) {
        BuyTicket buyTicket = new BuyTicket();

        new Thread(buyTicket,"1").start();
        new Thread(buyTicket,"2").start();
        new Thread(buyTicket,"3").start();

    }
}

class BuyTicket implements Runnable{
    private int ticketNum = 10;
    boolean flag = true;

    @Override
    public void run() {
        while (flag){
            try {
                buy();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void buy() throws InterruptedException {
        if(ticketNum <= 0){
            flag = false;
            return;
        }
        Thread.sleep(100);
        System.out.println(Thread.currentThread().getName()+"买了第"+ticketNum+"张票");
        ticketNum--;
    }
}