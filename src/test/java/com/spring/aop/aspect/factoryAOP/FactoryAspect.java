package com.spring.aop.aspect.factoryAOP;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FactoryAspect {

    @Before("args(java.lang.String)")
    public void before() {
        System.out.println("match args string");
    }

    @After("@args(com.spring.aop.aspect.factoryAOP.Listen)")
    public void after() {
        System.out.println("match @args listen");
    }
}
