package com.jwl.spring.framework.aop.intercept;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class AfterReturningAdvice implements MethodInterceptor {
    public AfterReturningAdvice(Method method, Object newInstance) {
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
