package com.jwl.spring.demo.controller;

import com.jwl.spring.framework.annotation.RequestParam;
import com.jwl.spring.demo.service.IQueryService;
import com.jwl.spring.framework.annotation.Autowired;
import com.jwl.spring.framework.annotation.Controller;
import com.jwl.spring.framework.annotation.RequestMapping;
import com.jwl.spring.framework.webmvc.ModelAndView;

import java.util.HashMap;

/**
 * @author wenlo
 */
@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    IQueryService queryService;

    @RequestMapping("/first.html")
    public ModelAndView query(@RequestParam("teacher") String teacher) {
        String result = queryService.query(teacher);

        HashMap<String, Object> model = new HashMap<>(3);
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new ModelAndView("first.html", model);
    }

}
