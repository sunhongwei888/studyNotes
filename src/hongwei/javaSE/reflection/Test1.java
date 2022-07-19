package hongwei.javaSE.reflection;

public class Test1 {
    public static void main(String[] args) throws ClassNotFoundException {
        //通过完整类名反射获取class
        Class<?> aClass = Class.forName("hongwei.javaSE.annotation.Student");
        Class<?> bClass = Class.forName("hongwei.javaSE.annotation.Student");
        System.out.println(aClass);
        System.out.println(bClass);
        System.out.println(aClass.hashCode());
        System.out.println(bClass.hashCode());
    }
}
