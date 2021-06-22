package com.spring.aop.aspect.factoryAOP;

import org.springframework.stereotype.Component;

@Listen
@Component
public class FoodFactory implements Factory {


    @Override
    public void make() {
        System.out.println("making food");
    }

    @Override
    public void delivery(String address) {
        System.out.println("selling food to " + address);
    }

    public void testArgsAnnotation(FoodFactory freshFactory) {

    }
}
