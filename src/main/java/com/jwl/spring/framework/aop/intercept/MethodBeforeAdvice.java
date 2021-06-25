package com.jwl.spring.framework.aop.intercept;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class MethodBeforeAdvice implements MethodInterceptor {
    public MethodBeforeAdvice(Method method, Object newInstance) {
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }
}
