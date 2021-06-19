package com.jwl.mvc.annotation;


import java.lang.annotation.*;

/**
 * @author jiwenlong
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String value() default "";

}
