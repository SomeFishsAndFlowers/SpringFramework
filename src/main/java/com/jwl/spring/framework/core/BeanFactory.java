package com.jwl.spring.framework.core;

/**
 * 单例工厂顶层设计
 * @author wenlo
 */
public interface BeanFactory {

    /**
     * 根据beanName从IOC容器中获取bean实例
     * @param beanName bean name
     * @return bean instance
     * @throws Exception exception
     */
    Object getBean(String beanName) throws Exception;

    /**
     * 根据bean class从IOC容器中获取bean实例
     * @param beanClass bean class
     * @return bean instance
     * @throws Exception exception
     */
    Object getBean(Class<?> beanClass) throws Exception;
}
