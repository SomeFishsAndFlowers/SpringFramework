package com.spring.aop.aspect;

import com.spring.aop.aspect.animalAOP.Animal;
import com.spring.aop.aspect.config.AspectConfig;
import com.spring.aop.aspect.factoryAOP.FoodFactory;
import com.spring.aop.aspect.factoryAOP.FreshFactory;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AspectAOPTest {

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AspectConfig.class);

    @Test
    public void test2() {
        Animal animal = context.getBean(Animal.class);

        animal.eat("ice cream");
        System.out.println("--------------------");
        try {
            animal.throwsEx();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        FoodFactory foodFactory = (FoodFactory) context.getBean("foodFactory");
//        foodFactory.make();
//        System.out.println("--------------------");
        foodFactory.delivery("beijing");
//        System.out.println("--------------------");
//        foodFactory.testArgsAnnotation((FreshFactory) context.getBean("freshFactory"));
        System.out.println("--------------------");
        foodFactory.testArgs("123", 123);
    }

}
