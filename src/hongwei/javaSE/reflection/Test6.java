package hongwei.javaSE.reflection;

import hongwei.javaSE.annotation.Student;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test6 {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Class aClass = Class.forName("hongwei.javaSE.annotation.Student");

        //构造一个对象
        Student s = (Student)aClass.newInstance();  //本质是调用了无参构造
        System.out.println(s);

        //通过构造器创建对象
        Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class, String.class, String.class);
        Student s1 = (Student)declaredConstructor.newInstance("1","awei","18");
        System.out.println(s1);

        //通过反射获取方法
        Method setName = aClass.getDeclaredMethod("setName", String.class);
        //invoke：激活的意思
        //（对象，方法的值）
        setName.invoke(s1,"阿伟");
        System.out.println(s1);

        //通过反射操作属性
        Field name = aClass.getDeclaredField("name");
        name.setAccessible(true);  //不能直接操作私有属性，需要关闭程序的安全检测
        name.set(s1,"阿伟啊");
        System.out.println(s1);
    }
}
