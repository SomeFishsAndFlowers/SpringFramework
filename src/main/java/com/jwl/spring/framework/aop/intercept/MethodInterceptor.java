package com.jwl.spring.framework.aop.intercept;

/**
 * 方法拦截器
 * @author wenlo
 */
public interface MethodInterceptor {

    /**
     * 方法调用
     * @param methodInvocation methodInvocation
     * @return object
     * @throws Throwable exception
     */
    Object invoke(MethodInvocation methodInvocation) throws Throwable;

}
