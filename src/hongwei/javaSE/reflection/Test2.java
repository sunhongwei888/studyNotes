package hongwei.javaSE.reflection;

import hongwei.javaSE.annotation.Student;

public class Test2 {
    public static void main(String[] args) throws ClassNotFoundException {
        Student student = new Student();
        //方式一：通过对象获得
        Class c1 = student.getClass();
        //方式二：通过字符串获得（包名+类名）
        Class c2 = Class.forName("hongwei.javaSE.annotation.Student");
        //方式三：通过类的静态成员class获得
        Class c3 = Student.class;
        //方式四：只针对内置的基本数据类型
        Class c4 = Integer.TYPE;
        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);
        System.out.println(c4);
    }
}
