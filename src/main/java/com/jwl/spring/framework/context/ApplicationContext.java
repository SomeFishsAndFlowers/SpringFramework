package com.jwl.spring.framework.context;

import com.jwl.spring.framework.annotation.Controller;
import com.jwl.spring.framework.annotation.Service;
import com.jwl.spring.framework.annotation.Autowired;
import com.jwl.spring.framework.beans.BeanWrapper;
import com.jwl.spring.framework.beans.config.BeanDefinition;
import com.jwl.spring.framework.beans.config.BeanPostProcessor;
import com.jwl.spring.framework.beans.support.BeanDefinitionReader;
import com.jwl.spring.framework.beans.support.DefaultListableBeanFactory;
import com.jwl.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jiwenlong
 */
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
     * @return bean instance
     */
    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);

        try {
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            Object instance = instanceBean(beanDefinition);
            if (null == instance) {
                return null;
            }
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            BeanWrapper beanWrapper = new BeanWrapper(instance);

            factoryBeanInstanceCache.put(beanName, beanWrapper);

            beanPostProcessor.postProcessAfterInitialization(instance);

            populateBean(beanName, instance);
            // 通过这样调用，相当于给我们自己留有了可操作的空间
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(String beanName, Object instance) {

        Class<?> clazz = instance.getClass();

        if (!(clazz.isAnnotationPresent(Controller.class))
                || clazz.isAnnotationPresent(Service.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);

            String autowiredBeanName = autowired.value().trim();

            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instanceBean(BeanDefinition beanDefinition) {

        Object instance = null;

        String className = beanDefinition.getBeanClassName();

        try {
            if (factoryBeanObjectCache.containsKey(className)) {
                instance = factoryBeanObjectCache.get(className);
            }
            else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                factoryBeanObjectCache.put(className, instance);
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return super.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public int getBeanDefinitionCount() {
        return super.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return reader.getConfig();
    }
}
