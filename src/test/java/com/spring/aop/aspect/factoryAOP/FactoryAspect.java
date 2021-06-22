package com.spring.aop.aspect.factoryAOP;


import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FactoryAspect {

    @Before("args(java.lang.String) && args(address)") // ..代表任意个参数 *代表一个参数
    public void before(String address) {
        System.out.println("before address: " + address);
        System.out.println("match args string");
    }

    @After("@args(com.spring.aop.aspect.factoryAOP.Listen) && args(factory)")
    public void after(Factory factory) {
        factory.make();
        System.out.println("match @args listen");
    }
}
