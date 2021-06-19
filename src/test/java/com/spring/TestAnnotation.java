package com.spring;

import com.spring.config.TestConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestAnnotation {


    @Test
    public void test1() {
        new AnnotationConfigApplicationContext(TestConfig.class);
    }

}
