package com.jwl.mvc.annotation;


import java.lang.annotation.*;

/**
 * @author jiwenlong
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";

}
