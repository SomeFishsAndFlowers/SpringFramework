package com.jwl.spring.framework.beans.config;

/**
 * @author jiwenlong
 */
public class BeanPostProcessor {


    public Object postProcessBeforeInitialization(Object instance, String beanName) {
        return instance;
    }

    public Object postProcessAfterInitialization(Object instance) {
        return instance;
    }
}
