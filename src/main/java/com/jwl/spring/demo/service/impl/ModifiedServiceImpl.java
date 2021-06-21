package com.jwl.spring.demo.service.impl;

import com.jwl.spring.demo.service.IModifiedService;
import com.jwl.spring.framework.annotation.Service;

/**
 * @author wenlo
 */
@Service
public class ModifiedServiceImpl implements IModifiedService {
    @Override
    public String add(String name, String addr) {
        return "modifiedService add, name=" + name + ", addr=" + addr;
    }

    @Override
    public String edit(Integer id, String name) {
        return "modifiedService edit, id=" + id + ", name=" + name;
    }

    @Override
    public String remove(Integer id) {
        return "modifiedService remove, id=" + id;
    }
}
