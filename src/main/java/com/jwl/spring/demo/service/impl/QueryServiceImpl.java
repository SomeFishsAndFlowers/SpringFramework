package com.jwl.spring.demo.service.impl;

import com.jwl.spring.demo.service.IQueryService;
import com.jwl.spring.framework.annotation.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wenlo
 */
@Service
@Slf4j
public class QueryServiceImpl implements IQueryService {
    @Override
    public String query(String name) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowTime = formatter.format(now);
        String json = "{name:\"" + name + "\", time: \"" + nowTime + "\"}";
        log.info("{}", json);
        return json;
    }
}
