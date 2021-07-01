package com.jwl.spring.demo.service;

/**
 * @author wenlo
 */
public interface IModifiedService {
    /**
     * add
     * @param name name
     * @param addr addr
     * @return r
     * @throws Exception e
     */
    String add(String name, String addr) throws Exception;

    /**
     * edit
     * @param id id
     * @param name name
     * @return string
     */
    String edit(Integer id, String name);

    /**
     * remove
     * @param id id
     * @return string
     */
    String remove(Integer id);

}
