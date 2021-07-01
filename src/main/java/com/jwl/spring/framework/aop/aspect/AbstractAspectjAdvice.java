package com.jwl.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author wenlo
 */
public class AbstractAspectjAdvice implements Advice {


    private Method aspectMethod;
    private Object aspectTarget;

    public AbstractAspectjAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {
        Class<?>[] parameterTypes = aspectMethod.getParameterTypes();
        if (parameterTypes.length == 0) {
            return aspectMethod.invoke(aspectTarget);
        }
        else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                }
                else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
                else if (parameterTypes[i] == Throwable.class) {
                    args[i] = ex;
                }
            }
            return aspectMethod.invoke(aspectTarget, args);
        }
    }
}
