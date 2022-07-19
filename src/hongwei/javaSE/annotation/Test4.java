package hongwei.javaSE.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Field;

//使用反射读取注解信息三步:
// 1.定义注解 ,
// 2.在类中使用注解 ,
// 3. 使用反射获取注解 , 一般都是现成框架实现 , 我们手动实现
public class Test4 {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        //反射 , Class可以获得类的全部信息 , 所有的东西
        Class aClass = Class.forName("hongwei.javaSE.annotation.Student");
        //获得这个类的注解
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }

        //获得类的注解value的值
        TableAnnotation table = (TableAnnotation)aClass.getAnnotation(TableAnnotation.class);
        System.out.println(table.value());

        //获得类指定注解的值
        Field name = aClass.getDeclaredField("name");
        FieldAnnotation field = name.getAnnotation(FieldAnnotation.class);
        System.out.println(field.columnName()+"--"+field.type()+"--"+field.length());
    }
    
}

//表名注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface TableAnnotation{
    String value();
}

//字段注解
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface FieldAnnotation{
    String columnName();  //字段名
    String type();  //类型
    int length();   //长度
}


