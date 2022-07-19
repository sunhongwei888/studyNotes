package hongwei.javaSE.annotation;

import java.lang.annotation.*;

public class Test2 {
    @MyAnnotation
    public void test(){}
}

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@interface MyAnnotation{}
