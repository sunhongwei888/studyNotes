package hongwei.javaSE.annotation;

public class Test1 extends Object{

    //@Override 方法重写
    @Override
    public String toString() {
        return super.toString();
    }

    //@Deprecated 方法过时，不建议使用，但仍可以使用
    @Deprecated
    public static void test(){
        System.out.println("测试@Deprecated");
    }

    //@SuppressWarnings 抑制警告 , 可以传参数
    @SuppressWarnings("all")
    public static void test1(){}

    public static void main(String[] args) {
        test();
    }
}
