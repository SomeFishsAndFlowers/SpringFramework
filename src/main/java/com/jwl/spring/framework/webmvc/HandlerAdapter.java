package com.jwl.spring.framework.webmvc;

import com.jwl.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiwenlong
 */
public class HandlerAdapter {
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws InvocationTargetException, IllegalAccessException {
        HandlerMapping handlerMapping = (HandlerMapping) handler;

        HashMap<String, Integer> paramMapping = new HashMap<>(8);

        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i=0; i<pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class
                    || parameterType == HttpServletResponse.class) {
                paramMapping.put(parameterType.getName(), i);
            }
        }

        Map<String, String[]> reqParameterMap = req.getParameterMap();

        Object[] paramValues = new Object[parameterTypes.length];
        for (Map.Entry<String, String[]> param : reqParameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                    .replaceAll("\\s", "");
            if (!paramMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value, parameterTypes[index]);
        }

        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }

        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);

        if (result == null) {
            return null;
        }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == ModelAndView.class;

        if (isModelAndView) {
            return (ModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> parameterType) {
        if (parameterType == String.class) {
            return value;
        }
        else if (parameterType == Integer.class || parameterType == int.class) {
            return Integer.valueOf(value);
        }
        return null;
    }

    public boolean support(Object handler) {
        return (handler instanceof HandlerMapping);
    }
}
