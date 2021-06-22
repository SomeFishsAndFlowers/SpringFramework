package com.spring.aop.aspect.animalAOP;

import org.springframework.stereotype.Component;

@Component
public class Animal {

    public void eat(String food) {
        System.out.println("I am eating " + food);
    }

    public void throwsEx() throws Exception {
        throw new Exception("error");
    }

}
