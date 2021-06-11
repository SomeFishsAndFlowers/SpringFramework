package com.jwl.demo.service.impl;

import com.jwl.demo.service.IDemoService;
import com.jwl.mvc.annotation.Service;

@Service
public class DemoService implements IDemoService {

    @Override
    public String get(String name) {
        return "My name is " + name;
    }

}
