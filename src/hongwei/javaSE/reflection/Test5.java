package hongwei.javaSE.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Test5 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        Class aClass = Class.forName("hongwei.javaSE.annotation.Student");
        
        //获取类的名字
        System.out.println(aClass.getName());        //获取包名+类名
        System.out.println(aClass.getSimpleName());  //获取类名
        
        //获取类的属性
        Field[] fields = aClass.getFields();  //只能找到public属性
        fields = aClass.getDeclaredFields();  //找到所有属性
        for (Field field : fields) {
            System.out.println(field);
        }
        
        //获取指定属性的值
        Field name = aClass.getDeclaredField("name");
        System.out.println(name);
        
        //获取类的方法
        Method[] methods = aClass.getMethods();  //获取本类及父类所有public方法
        for (Method method : methods) {
            System.out.println("getMethods:"+method);
        }
        Method[] declaredMethods = aClass.getDeclaredMethods();  //获取本类所有方法
        for (Method declaredMethod : declaredMethods) {
            System.out.println(declaredMethod);
        }

        //获取指定方法
        Method getName = aClass.getMethod("getName", null);
        Method setName = aClass.getMethod("setName", String.class);
        System.out.println(getName);
        System.out.println(setName);

        //获取构造器
        Constructor[] constructors = aClass.getConstructors();  //public构造器
        for (Constructor constructor : constructors) {
            System.out.println("getConstructors:"+constructor);
        }
        Constructor[] declaredConstructors = aClass.getDeclaredConstructors();  //所有构造器
        for (Constructor declaredConstructor : declaredConstructors) {
            System.out.println(declaredConstructor);
        }

        //获取指定的构造器
        Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class, String.class, String.class);
        System.out.println(declaredConstructor);
    }
}
