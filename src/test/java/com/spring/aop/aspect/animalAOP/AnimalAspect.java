package com.spring.aop.aspect.animalAOP;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AnimalAspect {

    @Pointcut("execution(* com.spring.aop.aspect.animalAOP.Animal.*(..))")
    public void cuttingPoint() {

    }

    @Before("cuttingPoint()")
    public void before() {
        System.out.println("before");
    }

    @After("cuttingPoint()")
    public void after() {
        System.out.println("after");
    }

    @Around("cuttingPoint()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("around before");
        Object result = proceedingJoinPoint.proceed();
        System.out.println("around after");
        return result;
    }

    @AfterReturning("cuttingPoint()")
    public void afterReturn() {
        System.out.println("afterReturn");
    }

    @AfterThrowing(value = "cuttingPoint()", throwing = "ex")
    public void afterThrow(JoinPoint joinPoint, Exception ex) {
        System.out.println(joinPoint);
        System.out.println(ex);
        System.out.println("afterThrow");
    }

}
