package com.spring.aop.aspect.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
@ComponentScan("com.spring.aop.aspect")
public class AspectConfig {
}
