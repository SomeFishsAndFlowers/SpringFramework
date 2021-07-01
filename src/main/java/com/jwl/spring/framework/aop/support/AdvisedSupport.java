package com.jwl.spring.framework.aop.support;

import com.jwl.spring.framework.aop.AopConfig;
import com.jwl.spring.framework.aop.aspect.AfterReturningAdvice;
import com.jwl.spring.framework.aop.aspect.AfterThrowingAdvice;
import com.jwl.spring.framework.aop.aspect.MethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主要完成对aop配置的解析
 * @author wenlo
 */
public class AdvisedSupport {

    private Class<?> targetClass;
    private Object target;
    private Pattern pointCutClassPattern;

    private  transient Map<Method, List<Object>> methodCache;

    private AopConfig config;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));

        methodCache = new HashMap<>(8);
        Pattern pattern = Pattern.compile(pointCut);

        try {
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            HashMap<String, Method> aspectMethods = new HashMap<>(8);
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }
            for (Method m : targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    LinkedList<Object> advises = new LinkedList<>();
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore().trim()))) {
                        advises.add(new MethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter().trim()))) {
                        advises.add(new AfterReturningAdvice(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow().trim()))) {
                        AfterThrowingAdvice afterThrowingAdvice = new AfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advises.add(afterThrowingAdvice);
                    }
                    methodCache.put(m, advises);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
