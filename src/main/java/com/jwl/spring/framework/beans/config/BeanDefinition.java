package com.jwl.spring.framework.beans.config;

/**
 * bean的定义对象
 * @author wenlo
 */
public class BeanDefinition {
    /**
     * bean的全限定类名
     */
    private String beanClassName;

    /**
     *
     */
    private boolean lazyInit = false;

    /**
     * 保存beanName，在IOC容器中储存的key
     */
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
