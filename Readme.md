# Spring memory shell
This repository produces Spring Interceptor memory shell, Controller memory shell and an introspection controller(which display 
web server environments like Beans, HandlerMappings, etc.). **For study use**.

## Environment
- Tomcat 9.0.80
- SpringMVC 5.3.17

## Content
### Controller memshell
[Source code](https://github.com/PadishahIII/Spring-Memshell/blob/master/src/main/java/spring/vul/springvulenv/controller/MemshellController.java)

After start server, request `http://localhost:<your-port>/memshell-register` to load memshell, and request `http://localhost:<your-port>/memshell?cmd=whoami` to execute command. 

![image](https://github.com/PadishahIII/Spring-Memshell/assets/83501709/6ab1c872-12fb-4d95-b73a-afcf7b273cd2)



You can check the injected controller at `/introspect` page as shown below.
![image](https://github.com/PadishahIII/Spring-Memshell/assets/83501709/e7ff8870-e8ed-4f2d-9d9d-d57ead6d68c6)

---

### Interceptor memshell
[Source code](https://github.com/PadishahIII/Spring-Memshell/blob/master/src/main/java/spring/vul/springvulenv/controller/InterceptorInjectController.java)

Visit `http://localhost:<your-port>/interceptor-register` to load evil interceptor whose path pattern is bound to `/hello*`. Then request `http://localhost:<your-port>/hello?cmd=whoami` to execute command.

<img src="https://github.com/PadishahIII/Spring-Memshell/assets/83501709/de5d8210-d4fa-4893-8eb7-535da4bdb166" alt="drawing" width="200">


You can check the injected interceptor at `/introspect` page.
![image](https://github.com/PadishahIII/Spring-Memshell/assets/83501709/eeff6c37-76a9-4dce-ad0f-df99212bde94)

---

### Introspection: Get server information
[Source code](https://github.com/PadishahIII/Spring-Memshell/blob/master/src/main/java/spring/vul/springvulenv/controller/IntrospectController.java)

The `/introspect` controller would visit `RequestMappingHandlerMapping` Bean in current application context and try to extract all available informations including Beans, Resources, Controller mappings and Interceptors. Visit `http://localhost:<your-port>/introspect`.

![image](https://github.com/PadishahIII/Spring-Memshell/assets/83501709/e92cc4e8-4922-49d7-b625-d36b9317e10c)

