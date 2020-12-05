package controller;

import annotation.Controller;
import annotation.RequestMapping;

import java.util.Map;

@Controller(value="/HelloWorld")
public class HelloController {
    @RequestMapping(value="/hello.do")
    public String hello(String name)
    {
        return "hello world "+name;
    }

    @RequestMapping(value="/welcome.do/{name}/{password}")
    public String welcome(String name, String password)
    {
//        String name = attr.get("name");
//        String password = attr.get("password");
        return "welcome "+name+password;
    }

//    @RequestMapping(value="/welcome.do/{name}/{password}")
//    public String test2(Map<String, String> attr)
//    {
//        String name = attr.get("name");
//        String password = attr.get("password");
//        return "welcome "+name+password;
//    }
}
