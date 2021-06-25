package com.jwl.spring.framework.aop.intercept;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class AfterThrowingAdvice implements MethodInterceptor {
    public AfterThrowingAdvice(Method method, Object newInstance) {
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }

    public void setThrowingName(String aspectAfterThrowingName) {
    }
}
