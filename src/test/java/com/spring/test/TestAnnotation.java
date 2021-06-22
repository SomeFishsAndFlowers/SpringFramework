package com.spring.test;

import com.spring.test.config.TestConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestAnnotation {


    public void test1() {
        new AnnotationConfigApplicationContext(TestConfig.class);
    }

}
