package com.jwl.demo.controller;

import com.jwl.demo.service.IDemoService;
import com.jwl.mvc.annotation.Autowired;
import com.jwl.mvc.annotation.Controller;
import com.jwl.mvc.annotation.RequestMapping;
import com.jwl.mvc.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/demo")
public class DemoController  {

    @Autowired
    private IDemoService service;

    @RequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp, @RequestParam("name") String name) {
        String result = service.get(name);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/add")
    public void add(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam("a") Integer a,
            @RequestParam("b") Integer b) {
        try {
            resp.getWriter().write(a + "+" + b + "=" + (a + b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/remove")
    public void remove(
            HttpServletRequest req,
            HttpServletResponse resp,
            @RequestParam("id") Integer id) {
    }

}
