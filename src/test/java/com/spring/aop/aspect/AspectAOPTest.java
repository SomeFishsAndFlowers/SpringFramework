package com.spring.aop.aspect;

import com.spring.aop.aspect.animalAOP.Animal;
import com.spring.aop.aspect.config.AspectConfig;
import com.spring.aop.aspect.factoryAOP.FoodFactory;
import com.spring.aop.aspect.factoryAOP.FreshFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AspectAOPTest {


    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AspectConfig.class);
//        Animal animal = context.getBean(Animal.class);
//
//        animal.eat("ice cream");
//        System.out.println("--------------------");
//        try {
//            animal.throwsEx();
//        } catch (Exception e) {
//        }

        FoodFactory foodFactory = (FoodFactory) context.getBean("foodFactory");
        foodFactory.make();
        System.out.println("--------------------");
        foodFactory.delivery("beijing");
        System.out.println("--------------------");
        foodFactory.testArgsAnnotation((FreshFactory) context.getBean("freshFactory"));
    }

}
