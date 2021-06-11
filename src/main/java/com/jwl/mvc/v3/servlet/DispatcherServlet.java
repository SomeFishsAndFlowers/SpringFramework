package com.jwl.mvc.v3.servlet;

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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    public static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    // 保存配置文件的内容
    private final Properties configContext = new Properties();

    // 保存扫描到的所有类名
    private final List<String> classNames = new ArrayList<>();

    // ioc容器，为了简单，暂时不使用CurrentHashMap
    private final Map<String, Object> ioc = new HashMap<>();

    // url与method的映射
    private final List<Handler> handlerMapping = new ArrayList<>();

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

    private Handler getHandler(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        requestURI = requestURI.replace(contextPath, "").replaceAll("/+", "/");
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.pattern.matcher(requestURI);
            if (matcher.matches()) {
                return handler;
            }
        }
        return null;
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Handler handler = getHandler(req);
            if (handler == null) {
                resp.getWriter().write("404 Not Found");
            } else {
                Class<?>[] parameterTypes = handler.method.getParameterTypes();
                Object[] params = new Object[parameterTypes.length];
                Map<String, String[]> parameterMap = req.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String value = Arrays.toString(entry.getValue()).
                            replaceAll("\\[|\\]", "").
                            replaceAll("\\s", ",");
                    if (!handler.paramIndexMapping.containsKey(entry.getKey())) {
                        continue;
                    }
                    int index = handler.paramIndexMapping.get(entry.getKey());
                    params[index] = convert(parameterTypes[index], value);
                }
                if (handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
                    Integer index = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
                    params[index] = req;
                }

                if (handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
                    Integer index = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
                    params[index] = resp;
                }

                Object returnValue = handler.method.invoke(handler.controller, params);
                if (returnValue == null) {
                    return;
                }
                resp.getWriter().write(returnValue.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object convert(Class<?> parameterType, String value) {
        if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        }
        return value;
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
            } else if (clazz.isAnnotationPresent(Service.class)) {
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
                        String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                        Pattern pattern = Pattern.compile(url);
                        Handler handler = new Handler(entry.getValue(), method, pattern);
                        handlerMapping.add(handler);
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

    private static class Handler {
        protected Object controller;
        protected Method method;
        protected Pattern pattern;
        protected Map<String, Integer> paramIndexMapping; // 保存参数顺序

        public Handler(Object controller, Method method, Pattern pattern) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            this.paramIndexMapping = new HashMap<>();
            putIndexMapping(method);
        }

        private void putIndexMapping(Method method) {
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof RequestParam) {
                        String paramName = ((RequestParam) annotation).value();
                        if (!"".equals(paramName)) {
                            paramIndexMapping.put(paramName, i);
                        }
                    }
                }
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                    paramIndexMapping.put(parameterType.getName(), i);
                }
            }
        }
    }
}
