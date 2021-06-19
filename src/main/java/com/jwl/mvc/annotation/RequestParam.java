package com.jwl.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author jiwenlong
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";

}
