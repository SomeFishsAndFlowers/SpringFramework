package com.jwl.spring.framework.aop.aspect;

import com.jwl.spring.framework.aop.intercept.MethodInterceptor;
import com.jwl.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class AfterThrowingAdvice extends AbstractAspectjAdvice implements Advice, MethodInterceptor {

    private MethodInvocation methodInvocation;
    private String throwingName;

    public AfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch (Throwable ex) {
            invokeAdviceMethod(methodInvocation, null, ex.getCause());
            throw ex;
        }
    }
}
