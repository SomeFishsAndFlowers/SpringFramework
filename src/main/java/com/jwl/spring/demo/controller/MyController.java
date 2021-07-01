package com.jwl.spring.demo.controller;

import com.jwl.spring.framework.annotation.RequestParam;
import com.jwl.spring.demo.service.IModifiedService;
import com.jwl.spring.demo.service.IQueryService;
import com.jwl.spring.framework.annotation.Autowired;
import com.jwl.spring.framework.annotation.Controller;
import com.jwl.spring.framework.annotation.RequestMapping;
import com.jwl.spring.framework.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wenlo
 */
@Controller
@RequestMapping("/web")
public class MyController {

    @Autowired
    IQueryService queryService;

    @Autowired
    IModifiedService modifiedService;

    @RequestMapping("/query.json")
    public ModelAndView query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name) {
        String result = queryService.query(name);
        return out(response, result);
    }

    @RequestMapping("/add*.json")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("name") String name, @RequestParam("addr") String addr) throws Exception {
        String result = modifiedService.add(name, addr);
        return out(response, result);
    }

    @RequestMapping("/remove.json")
    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam("id") Integer id) {
        String result = modifiedService.remove(id);
        return out(response, result);
    }

    @RequestMapping("/edit.json")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                               @RequestParam("id") Integer id, @RequestParam("name") String name) {
        String result = modifiedService.edit(id, name);
        return out(response, result);
    }

    private ModelAndView out(HttpServletResponse response, String result) {
        try {
            response.setHeader("Content-Type", "text/json;charset=utf-8");
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
