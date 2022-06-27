package com.hongwei.lamda;

public class Lamda2 {
    //3.静态内部类
    static class Love2 implements Ilove{
        @Override
        public void love(int a) {
            System.out.println("i love "+a);
        }
    }

    public static void main(String[] args) {
        Ilove love = new Love();
        love.love(1);

        love = new Love2();
        love.love(2);

        //4.局部内部类
        class love3 implements Ilove{
            @Override
            public void love(int a) {
                System.out.println("i love "+a);
            }
        }
        love = new love3();
        love.love(3);

        //5.匿名内部类
        love = new Ilove() {
            @Override
            public void love(int a) {
                System.out.println("i love "+a);
            }
        };
        love.love(4);

        //6.lamda简化
        love = (int a) -> {
            System.out.println("i love "+a);
        };
        love.love(5);

        //6.lamda简化1,去除参数类型（多个参数都需要去除）
        love = (a) -> {
            System.out.println("i love "+a);
        };
        love.love(6);

        //6.lamda简化2,去除括号（多参数不允许删除）
        love = a -> {
            System.out.println("i love "+a);
        };
        love.love(7);

        //6.lamda简化3,去除花括号（多行代码不允许删除）
        love = a -> System.out.println("i love "+a);

        love.love(8);

    }
}

//1.定义一个函数式接口：接口只有一个方法（lamda表达式只能是函数式接口）
interface Ilove{
    void love(int a);
}

//2.实现类
class Love implements Ilove{
    @Override
    public void love(int a) {
        System.out.println("i love "+a);
    }
}
