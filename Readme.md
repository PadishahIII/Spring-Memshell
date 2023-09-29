# Spring memory shell demo
This repository produces Spring Interceptor memory shell, Controller memory shell and an introspection controller(which display 
web server environments like Beans, HandlerMappings, etc.). For study use.

## Environment
- Tomcat 9.0.80
- SpringMVC 5.3.17

## Content
### Controller memshell
[Source code](https://github.com/PadishahIII/Spring-Memshell/blob/master/src/main/java/spring/vul/springvulenv/controller/MemshellController.java)

After start server, request `http://localhost:<your-port>/memshell-register` to load memshell, and request `http://localhost:<your-port>/memshell?cmd=whoami` to execute command.

### Interceptor memshell
[Source code]()

### Introspection: Get server information
[Source code](https://github.com/PadishahIII/Spring-Memshell/blob/master/src/main/java/spring/vul/springvulenv/controller/IntrospectController.java)

The `/introspect` controller would visit `RequestMappingHandlerMapping` Bean in current application context and try to extract all available informations including Beans, Resources, Controller mappings and Interceptors. Visit `http://localhost:<your-port>/introspect`.
