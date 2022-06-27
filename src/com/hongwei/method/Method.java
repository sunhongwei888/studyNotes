package com.hongwei.method;

public class Method {
    //加法
    public static int add(int a, int b){
        return a+b;
    }


    //main
    public static void main(String[] args) {
        /*int c = add(1,2);
        System.out.println(c);*/
        for (int i=0;i<args.length;i++){
            System.out.println(i+":"+args[i]);
        }
    }
}
