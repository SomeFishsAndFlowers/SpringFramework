package com.jwl.spring.framework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author jiwenlong
 */
public class HandlerMapping {

    private Pattern pattern;
    private Object controller;
    private Method method;


    public HandlerMapping(Pattern pattern, Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
