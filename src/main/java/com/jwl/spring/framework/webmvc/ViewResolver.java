package com.jwl.spring.framework.webmvc;

import java.io.File;

/**
 * @author jiwenlong
 */
public class ViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";


    private File templateRootDir;
    private String viewName;


    public ViewResolver(String templateRoot) {
        String templateRootPath = getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public View resolverViewName(String viewName, Object locale) {
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);

        File templateFile = new File((templateRootDir.getPath() + "/" + viewName)
                .replaceAll("/+", "/"));

        return new View(templateFile);
    }

    public String getViewName() {
        return viewName;
    }
}
