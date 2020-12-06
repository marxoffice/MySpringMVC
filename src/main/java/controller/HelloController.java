package controller;

import annotation.Controller;
import annotation.RequestMapping;
import annotation.RequestParam;
import view.View;

import java.util.Map;

@Controller(value="/HelloWorld")
public class HelloController {
    // 一个普通url参数形式的测试
    @RequestMapping(value="/hello")
    public String hello(@RequestParam("name") String name)
    {
        return "Hello World "+name;
    }

    // rest风格 测试 注意rest函数的参数一定要按rest url的顺序
    @RequestMapping(value="/welcome.do/{name}/{password}")
    public String welcome(String name, String password)
    {
//        String name = attr.get("name");
//        String password = attr.get("password");
        return "welcome "+name+" "+password;
    }

    @RequestMapping("/getAllParams")
    public String paramMap(Map<String, String[]> paraMaps){
        String mapContent = " ";
        for(Map.Entry<String, String[]> entry : paraMaps.entrySet()){
            String paraName = entry.getKey();
            String[] paraValues = entry.getValue();
            mapContent += " parameter Name: " + paraName + " ValueLength: "+ paraValues.length;
            for(String value : paraValues){
                mapContent += " "+value;
            }
            mapContent += "  ends    ";
        }
        return mapContent;
    }

    // 既包含 rest参数 又包含用户指定参数 还包括所有参数Map的测试
    @RequestMapping(value="/params/{user}/hello/{tag}")
    public String paramTest(String user, String tag, @RequestParam("isVIP") String isVIP,Map<String, String[]> paramMaps){
        String responseText = "user: " + user + " tag: " + tag + " is VIP: " + isVIP + " ParameterCount: " + paramMaps.size();
        return responseText;
    }

    @RequestMapping(value="/testStatic")
    public View testStatic(){
        return new View("/WEB-INF/testStatic.jsp");
    }

//    @RequestMapping(value="/welcome.do/{name}/{password}")
//    public String test2(Map<String, String> attr)
//    {
//        String name = attr.get("name");
//        String password = attr.get("password");
//        return "welcome "+name+password;
//    }
}
