package com.jwl.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jiwenlong
 */
public class View {


    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    private final Pattern pattern = Pattern.compile("¥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);

    public View(File templateFile) {
        this.viewFile = templateFile;
    }

    public static String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

    public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(viewFile, "r");

        try {
            String line = null;
            while (null != (line = ra.readLine())) {
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("¥\\{[^\\}]+\\}", "");

                    Object paramValue = model.get(paramName);

                    if (null == paramValue) {
                        continue;
                    }
                    //要把¥{}中间的这个字符取出来
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);

                }
                sb.append(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            ra.close();
        }
        resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        resp.getWriter().write(sb.toString());
    }

    private String makeStringForRegExp(String str) {

        return str.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("?", "\\?")
                .replace(",", "\\,")
                .replace(".", "\\.")
                .replace("&", "\\&");
    }
}
