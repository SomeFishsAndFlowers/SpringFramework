package com.jwl.spring.demo.aspect;

import com.jwl.spring.framework.aop.aspect.JoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author wenlo
 */
@Slf4j
public class LogAspect {

    public void before(JoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        log.info("Invoker Before Method!!! \nTargetObject: {} \nArgs: {}",
                joinPoint.getThis(), Arrays.toString(joinPoint.getArguments()));
    }

    public void after(JoinPoint joinPoint) {
        log.info("Invoker After Method!!! \nTargetObject: {} \nArgs: {}",
                joinPoint.getThis(), Arrays.toString(joinPoint.getArguments()));
        long startTime = (long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        log.info("running time: {} ms", endTime - startTime);
    }

    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.info("Invoker afterThrowing Method!!! \nTargetObject: {} \nArgs: {} \nThrows: {}",
                joinPoint.getThis(), Arrays.toString(joinPoint.getArguments()), ex.getMessage());
    }

}
