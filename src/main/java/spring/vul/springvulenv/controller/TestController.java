package spring.vul.springvulenv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @ResponseBody
    @RequestMapping("/*")
    public String Hello() {
        return "Hello World!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    }
}