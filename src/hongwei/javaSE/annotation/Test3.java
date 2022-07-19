package hongwei.javaSE.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Test3 {
    //显示定义值，默认值可以不赋值
    //@MyAnnotation2(name="张三",age=10,{"张三","李四"})
    @MyAnnotation2(age=10)
    public void test2(){}

    //只有一个参数，默认设置为value，使用时可省略不写。（规范）
    @MyAnnotation3("张三")
    public void test3(){}
}

//多参数注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation2{
    String name() default "";
    int age();
    String[] students() default {"1","2"};
}

//单参数注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation3{
    String value();
}