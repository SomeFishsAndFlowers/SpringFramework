package com.jwl.spring.framework.webmvc.servlet;

import com.jwl.spring.framework.annotation.Controller;
import com.jwl.spring.framework.annotation.RequestMapping;
import com.jwl.spring.framework.context.ApplicationContext;
import com.jwl.spring.framework.webmvc.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Servlet作为MVC的启动入口
 * @author wenlo
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    /**
     *
     */
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();

    private ApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            processDispatchResult(req, resp, new ModelAndView("500"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        HandlerMapping handler = getHandler(req);

        if (handler == null) {
            processDispatchResult(req, resp, new ModelAndView("404"));
        }

        HandlerAdapter ha = getHandlerAdapter(handler);

        ModelAndView mv = ha.handle(req, resp, handler);

        processDispatchResult(req, resp, mv);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (handlerAdapters.isEmpty()) {
            return null;
        }
        HandlerAdapter handlerAdapter = handlerAdapters.get(handler);
        if (handlerAdapter.support(handler)) {
            return handlerAdapter;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView modelAndView) {
        if (modelAndView == null) {
            return;
        }
        if (viewResolvers.isEmpty()) {
            return;
        }
        for (ViewResolver viewResolver : viewResolvers) {
            View view = viewResolver.resolverViewName(modelAndView.getViewName(), null);
            if (view != null) {
                try {
                    view.render(modelAndView.getModel(), req, resp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private HandlerMapping getHandler(HttpServletRequest req) {

        if (handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();

        String contextPath = req.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);

            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) {

        context = new ApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);

    }

    private void initStrategies(ApplicationContext context) {

        initMultipartResolver(context);

        initLocaleResolver(context);

        initHandlerMappings(context);

        initHandlerAdapters(context);

        initHandlerExceptionResolvers(context);

        initRequestToViewNameTranslator(context);

        initViewResolvers(context);

        initFlashMapManager(context);
    }

    private void initViewResolvers(ApplicationContext context) {

        String templateRoot = context.getConfig().getProperty("templateRoot");

        String templateRootPath = getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File template : templateRootDir.listFiles()) {
            viewResolvers.add(new ViewResolver(templateRoot));
        }

    }

    private void initHandlerAdapters(ApplicationContext context) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }

    private void initHandlerMappings(ApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(Controller.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }

                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    String regex = ("/" + baseUrl +
                            requestMapping.value().replaceAll("\\*", ".*"))
                            .replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new HandlerMapping(pattern, controller, method));
                    log.info("Mapping: {}, {}", regex, method);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFlashMapManager(ApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
    }

    private void initLocaleResolver(ApplicationContext context) {
    }

    private void initMultipartResolver(ApplicationContext context) {
    }
}
