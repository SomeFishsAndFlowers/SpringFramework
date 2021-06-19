package com.jwl.spring.framework.beans.support;

import com.jwl.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行查找、读取、解析
 * @author wenlo
 */
public class BeanDefinitionReader {

    private List<String> registerBeanClasses = new ArrayList<>();
    private Properties config = new Properties();

    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String[] configLocations) {
        try (InputStream is = getClass().getClassLoader().
                getResourceAsStream(configLocations[0].replace("classPath:", ""))) {
            config.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            }
            else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName()).replace(".class", "");
                registerBeanClasses.add(className);
            }
        }
    }

    public List<BeanDefinition> loadBeanDefinitions() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        try {
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                beanDefinitions.add(doCreateBeanDefinition(
                        toLowFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    beanDefinitions.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return beanDefinitions;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return config;
    }
}
