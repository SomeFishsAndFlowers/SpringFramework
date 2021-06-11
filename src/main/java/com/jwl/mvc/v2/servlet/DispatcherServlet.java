package com.jwl.mvc.v2.servlet;

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
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    public static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    // 保存配置文件的内容
    private final Properties configContext = new Properties();

    // 保存扫描到的所有类名
    private final List<String> classNames = new ArrayList<>();

    // ioc容器，为了简单，暂时不使用CurrentHashMap
    private final Map<String, Object> ioc = new HashMap<>();

    // url与method的映射
    private final Map<String, Object> handlerMapping = new HashMap<>();

    // web.xml文件servlet的初始化参数key
    private static final String CONTEXT_LOCATION_PARAM = "contextConfigLocation";


    private static final ClassLoader CLASS_LOADER = DispatcherServlet.class.getClassLoader();







    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        String requestURI = req.getRequestURI();
        log.debug("request: {}", requestURI);
        Object o = handlerMapping.get(requestURI);
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
                        params[i] = ioc.get(toFirstLowerCase(parameterType.getName()));
                    }
                }
                String result = (String) ((Method) o).invoke(
                        ioc.get(toFirstLowerCase(((Method) o).getDeclaringClass().getName())), params);
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
        try {
            doLoadConfig(config.getInitParameter(CONTEXT_LOCATION_PARAM));
            String scanPackage = configContext.getProperty("scanPackage");
            doScanner(scanPackage);
            doInstance();
            doAutowire();
            initHandlerMapping();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("DispatcherServlet is initialized");
    }

    private void doLoadConfig(String contextConfigLocation) {
        try (InputStream is = CLASS_LOADER.getResourceAsStream(contextConfigLocation)) {
            String classPath = Objects.requireNonNull(CLASS_LOADER.getResource("/")).getFile();
            log.debug("context location: {}{}", classPath, contextConfigLocation);
            configContext.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage) {
        log.debug("scanPackage: {}", scanPackage);
        URL url = CLASS_LOADER.getResource("/" + scanPackage.replaceAll("\\.", "/"));
        assert url != null;
        File classPath = new File(url.getFile());
        for (File file : Objects.requireNonNull(classPath.listFiles())) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    String clazzName = scanPackage + "." + file.getName().replace(".class", "");
                    log.debug("find class: {}", clazzName);
                    classNames.add(clazzName);
                }
            }
        }
    }

    private void doInstance() throws Exception {
        for (String className : classNames) {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Controller.class)) {
                Object instance = clazz.newInstance();
                String beanName = toFirstLowerCase(className);
                log.debug("initialize {} : {}", beanName, instance);
                ioc.put(beanName, instance);
            }
            else if (clazz.isAnnotationPresent(Service.class)) {
                Service service = clazz.getAnnotation(Service.class);
                String beanName = service.value().trim();
                if ("".equals(beanName)) {
                    beanName = toFirstLowerCase(clazz.getName());
                }
                Object instance = clazz.newInstance();
                log.debug("initialize {} : {}", beanName, instance);
                ioc.put(beanName, instance);
                for (Class<?> i : clazz.getInterfaces()) {
                    beanName = toFirstLowerCase(i.getName());
                    if (ioc.containsKey(beanName)) {
                        throw new Exception("The \"" + beanName + "\" exists!");
                    }
                    ioc.put(beanName, instance);
                }
            }
        }
    }

    private void doAutowire() throws IllegalAccessException {
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String beanName = autowired.value().trim();
                    if ("".equals(beanName)) {
                        beanName = toFirstLowerCase(field.getType().getName());
                    }
                    field.setAccessible(true);
                    log.debug("autowired object {} 's field {} : {}", entry.getValue(), field.getName(), beanName);
                    field.set(entry.getValue(), ioc.get(beanName));
                }
            }
        }
    }

    private void initHandlerMapping() {
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(Controller.class)) {
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
                        handlerMapping.put(url, method);
                        log.debug("url mapping {} : {}", url, method);
                    }
                }
            }
        }


    }

    private String toFirstLowerCase(String className) {
        String[] strings = className.trim().split("\\.");
        String name = strings[strings.length - 1];
        final int MASK = 0x00100000;
        char[] chars = name.toCharArray();
        chars[0] ^= MASK;
        name = new String(chars);
        StringBuilder beanName = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            beanName.append(strings[i]);
            beanName.append(".");
        }
        beanName.append(name);
        return beanName.toString();
    }
}
