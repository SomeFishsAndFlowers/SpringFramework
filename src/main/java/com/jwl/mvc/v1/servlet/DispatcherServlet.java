package com.jwl.mvc.v1.servlet;

import com.jwl.mvc.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    public static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private static final String CONTEXT_LOCATION_PARAM = "contextConfigLocation";

    private static final ClassLoader CLASS_LOADER = DispatcherServlet.class.getClassLoader();

    private final Map<String, Object> mapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    private void doDispatch(
            HttpServletRequest req,
            HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        log.debug("request: {}", requestURI);
        Object o = mapping.get(requestURI);
        try {
            if (o instanceof Method) {
                Method method = (Method) o;
                Parameter[] parameters = method.getParameters();
                Object[] params = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class<?> parameterType = parameter.getType();
                    if (parameter.isAnnotationPresent(RequestParam.class)) {
                        RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                        String paramName = annotation.value();
                        if ("".equals(paramName)) {
                            paramName = parameter.getName(); // 获取不到参数的原始名称
                        }
                        switch (parameterType.getTypeName()) {
                            case "java.lang.Integer":
                                params[i] = Integer.valueOf(req.getParameter(paramName));
                                break;
                            case "java.lang.Long":
                                params[i] = Long.valueOf(req.getParameter(paramName));
                                break;
                            case "java.lang.Float":
                                params[i] = Float.valueOf(req.getParameter(paramName));
                                break;
                            case "java.lang.Double":
                                params[i] = Double.valueOf(req.getParameter(paramName));
                                break;
                            case "java.lang.Short":
                                params[i] = Short.valueOf(req.getParameter(paramName));
                                break;
                            case "java.lang.Boolean":
                                params[i] = Boolean.valueOf(req.getParameter(paramName));
                                break;
                            default:
                                params[i] = req.getParameter(paramName);
                        }
                    }
                    else if (parameterType.isInstance(req)) {
                        params[i] = req;
                    }
                    else if (parameterType.isInstance(resp)) {
                        params[i] = resp;
                    }
                    else {
                        params[i] = mapping.get(parameterType.getName());
                    }
                }
                String result = (String) ((Method) o).invoke(
                        mapping.get(((Method) o).getDeclaringClass().getName()), params);
                if (result != null)
                    resp.getWriter().write(result);
            }
            else {
                resp.getWriter().write("404 Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        log.info("initialize DispatcherServlet...");
        InputStream is = null;
        try {
            Properties configContext = new Properties();
            String contextLocation = config.getInitParameter(CONTEXT_LOCATION_PARAM);
            String classPath = Objects.requireNonNull(CLASS_LOADER.getResource("/")).getFile();
            log.debug("context location: {}{}", classPath, contextLocation);
            is = CLASS_LOADER.getResourceAsStream(contextLocation);
            configContext.load(is);
            String scanPackage = configContext.getProperty("scanPackage");
            log.debug("scanPackage: {}", scanPackage);
            doScanner(scanPackage);
            ArrayList<String> classNames = new ArrayList<>(mapping.keySet());
            for (String className : classNames) {
                if (!className.contains(".")) {
                    continue;
                }
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    mapping.put(className, clazz.newInstance());
                    String baseUrl = "";
                    if (clazz.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                        baseUrl = requestMapping.value();
                    }
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                            mapping.put(url, method);
                            log.debug("url mapping {} : {}", url, method);
                        }
                    }
                }
                else if (clazz.isAnnotationPresent(Service.class)) {
                    Service service = clazz.getAnnotation(Service.class);
                    String beanName = service.value();
                    if ("".equals(beanName)) {
                        beanName = clazz.getName();
                    }
                    Object object = clazz.newInstance();
                    mapping.put(beanName, object);
                    for (Class<?> i : clazz.getInterfaces()) {
                        mapping.put(i.getName(), object);
                    }
                }
            }
            classNames = null;
            for (Object object : mapping.values()) {
                if (object != null) {
                    Class<?> clazz = object.getClass();
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(Autowired.class)) {
                                Autowired autowired = field.getAnnotation(Autowired.class);
                                String beanName = autowired.value();
                                if ("".equals(beanName)) {
                                    beanName = field.getType().getName();
                                }
                                field.setAccessible(true);
                                field.set(mapping.get(clazz.getName()), mapping.get(beanName));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("DispatcherServlet is initialized");
    }

    private void doScanner(String scanPackage) {
        URL url = CLASS_LOADER.getResource("/" + scanPackage.replaceAll("\\.", "/"));
        assert url != null;
        File classDir = new File(url.getFile());
        for (File file : Objects.requireNonNull(classDir.listFiles())) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    String clazzName = scanPackage + "." + file.getName().replace(".class", "");
                    log.debug("find class: {}", clazzName);
                    mapping.put(clazzName, null);
                }
            }
        }
    }
}
