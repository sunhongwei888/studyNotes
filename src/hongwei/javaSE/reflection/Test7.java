package hongwei.javaSE.reflection;

import hongwei.javaSE.annotation.Student;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test7 {

    //普通方式调用
    public static void test1(){
        Student student = new Student();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            student.getName();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("普通方式："+(endTime-startTime)+"ms");
    }

    //反射调用-安全检测
    public static void test2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Student student = new Student();
        Class aClass = Class.forName("hongwei.javaSE.annotation.Student");
        Method getName = aClass.getDeclaredMethod("getName", null);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            getName.invoke(student,null);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("反射调用-安全检测："+(endTime-startTime)+"ms");
    }

    //反射调用-关闭安全检测
    public static void test3() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Student student = new Student();
        Class aClass = Class.forName("hongwei.javaSE.annotation.Student");
        Method getName = aClass.getDeclaredMethod("getName", null);
        getName.setAccessible(true);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000000; i++) {
            getName.invoke(student,null);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("反射调用-关闭安全检测："+(endTime-startTime)+"ms");
    }

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test1();
        test2();
        test3();
    }
}
