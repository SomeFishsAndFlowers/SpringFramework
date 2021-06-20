package com.jwl.spring.framework.beans.support;

import com.jwl.spring.framework.beans.config.BeanDefinition;
import com.jwl.spring.framework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiwenlong
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {

    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

}
