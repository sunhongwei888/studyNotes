package hongwei.lamda;

public class Lamda1 {
    //3.静态内部类
    static class like2 implements Ilike{
        @Override
        public void lamda() {
            System.out.println("i like lamda1");
        }
    }

    public static void main(String[] args) {
        Ilike like = new Like();
        like.lamda();

        like = new Lamda1.like2();
        like.lamda();

        //4.局部内部类
        class like3 implements Ilike{
            @Override
            public void lamda() {
                System.out.println("i like lamda2");
            }
        }
        like = new like3();
        like.lamda();

        //5.匿名内部类
        like = new Ilike() {
            @Override
            public void lamda() {
                System.out.println("i like lamda3");
            }
        };
        like.lamda();

        //6.lamda简化
        like = () -> {
            System.out.println("i like lamda4");
        };
        like.lamda();

    }
}

//1.定义一个函数式接口
interface Ilike{
    void lamda();
}

//2.实现类
class Like implements Ilike{
    @Override
    public void lamda() {
        System.out.println("i like lamda");
    }
}
