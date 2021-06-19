package com.jwl.spring.framework.context;

import com.jwl.spring.framework.beans.BeanWrapper;
import com.jwl.spring.framework.beans.config.BeanDefinition;
import com.jwl.spring.framework.beans.support.BeanDefinitionReader;
import com.jwl.spring.framework.beans.support.DefaultListableBeanFactory;
import com.jwl.spring.framework.core.BeanFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;
    private BeanDefinitionReader reader;

    /**
     * 单例Bean实例缓存
     */
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    /**
     * 通用的IOC容器，用来储存所有的被代理过的对象
     */
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1. 定位，定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);

        //2. 加载配置文件，扫描相关的类，把它们封装成beanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3. 注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4. 把不是延迟加载的类提前初始化
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The \"" + beanDefinition.getFactoryBeanName() + "\" is exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //到这里为止，容器初始化完毕
    }

    /**
     * 依赖注入，从这里开始，读取BeanDefinition中的信息
     * 然后通过反射机制创建一个实例并返回
     * Spring的做法是，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
     * 装饰器模式：
     *  1. 保留原来的oop关系
     *  2. 需要对它进行扩展、增强（为以后的AOP打基础）
     * @param beanName bean name
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        // todo
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return reader.getConfig();
    }
}
