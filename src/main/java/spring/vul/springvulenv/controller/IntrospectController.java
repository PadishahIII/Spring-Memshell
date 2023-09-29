package spring.vul.springvulenv.controller;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

@Controller
public class IntrospectController {
    @RequestMapping("introspect")
    public void Introspect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
            assert context != null;
//            RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
            Map<String, RequestMappingHandlerMapping> beanMap = context.getBeansOfType(RequestMappingHandlerMapping.class);
            for (String type : beanMap.keySet()) {
                if (type.contains("requestMappingHandlerMapping")) {

                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<html><body>");

            for (String type : beanMap.keySet()) {
                stringBuilder.append("<h2>Introspection for ").append(type).append("</h2><br>");
                RequestMappingHandlerMapping requestMappingHandlerMapping = beanMap.get(type);

                String[] beanDefNames = context.getBeanDefinitionNames();
                stringBuilder.append("Bean Definition names:<br>");
                int i = 0;
                for (String n : beanDefNames) {
                    stringBuilder.append(String.format("%d: %s<br>", i, n));
                    i++;
                }

                Resource[] resources = context.getResources("/**");
                stringBuilder.append("<br>Resources:<br>");
                i = 0;
                for (Resource resource : resources) {
                    stringBuilder.append(String.format("%d: %s<br>", i, resource.getFilename()));
                    i++;
                }

                Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
                AntPathMatcher pathMatcher = (AntPathMatcher) requestMappingHandlerMapping.getPathMatcher();
                stringBuilder.append("<br>Controller Mappings:<br>");
                i = 0;
                stringBuilder.append(String.format("PathMatcher:%s<br>", pathMatcher.getClass().getName()));

                for (RequestMappingInfo info : map.keySet()) {
                    HandlerMethod method = map.get(info);
                    String handlerClsName = method.getBeanType().getName();
                    String methodName = method.getMethod().getName();

                    PathPatternsRequestCondition condition = info.getPathPatternsCondition();
                    String infoStr = info.toString();
                    StringBuilder patterns = new StringBuilder();
                    if (condition == null) {
                        Set<String> set = info.getPatternValues();
                        if (set == null) {
                            patterns.append("None");
                        } else {
                            for (String s : set) {
                                patterns.append(s).append(",");
                            }
                        }
                    } else {
                        for (String s : condition.getPatternValues()) {
                            patterns.append(s).append(",");
                        }
                    }
                    stringBuilder.append(String.format("%d:<br>Name:%s<br>Path Pattern:%s<br>Method:%s.%s<br>InfoString:%s<br>", i, info.getName(), patterns.toString(), handlerClsName, methodName, infoStr));
                    i++;
                }

                stringBuilder.append("<br>Interceptors:<br>");
                i = 0;
                Method getMappedInterceptorsMethod = AbstractHandlerMapping.class.getDeclaredMethod("getMappedInterceptors");
                getMappedInterceptorsMethod.setAccessible(true);
                MappedInterceptor[] interceptors = (MappedInterceptor[]) getMappedInterceptorsMethod.invoke(requestMappingHandlerMapping);
                for (MappedInterceptor interceptor : interceptors) {
                    String[] patterns = interceptor.getPathPatterns();
                    StringBuilder patternStr = new StringBuilder();

//                Class pathPatternCls = Class.forName("org.springframework.web.servlet.handler.PathPattern");
//                Field patternStringField = pathPatternCls.getField("patternString");
//                patternStringField.setAccessible(true);
                    Field includePatternsField = interceptor.getClass().getDeclaredField("includePatterns");
                    includePatternsField.setAccessible(true);

                    Object[] includePatterns = (Object[]) includePatternsField.get(interceptor);
                    if (includePatterns != null) {
                        for (Object pathPattern : includePatterns) {
                            Class cls = pathPattern.getClass();
                            Field field = cls.getDeclaredField("patternString");
                            field.setAccessible(true);
                            String patternString = (String) field.get(pathPattern);
                            if (patternString != null) {
                                patternStr.append(patternString).append(",");
                            }
                        }
                    }

                    if (patterns == null) {
                        patternStr.append("None");
                    } else {
                        for (String p : patterns) {
                            patternStr.append(p).append(", ");
                        }
                    }
                    String clsname = interceptor.getInterceptor().getClass().getName();
                    stringBuilder.append(String.format("%d:<br>Pattern:%s<br>Interceptor:%s<br>", i, patternStr.toString(), clsname));
                    i++;
                }
                stringBuilder.append("<br><h3>---------------------------------------------------</h3><br>");
            }
            stringBuilder.append("</body></html>");
            response.getWriter().println(stringBuilder.toString());


        } catch (Exception e) {
            throw e;
        }

    }
}
