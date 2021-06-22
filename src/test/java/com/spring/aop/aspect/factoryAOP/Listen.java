package com.spring.aop.aspect.factoryAOP;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
//@Inherited
public @interface Listen {

    String value() default "";
}
