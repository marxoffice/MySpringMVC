package test;

import java.util.HashMap;
import java.util.Map;

public class UrlTest {

    public static void main(String[] args) {
        String reqUrl1 = "/au/bu/cu/";
        String url1 = "/au/bu/cu/";
        System.out.println(url1.equals(reqUrl1));

        String reqUrl2 = "/user/{id}/";
        String url2 = "/user/1234/";

        for(String val: reqUrl2.split("/"))
            System.out.println(val);
        for(String val: url2.split("/"))
            System.out.println(val);

        Map<String, String> attr1 = new HashMap<>();
        System.out.println(attr1.isEmpty());

        String restPattern = "{id}";
        System.out.println(restPattern.replace("{","").replace("}",""));
        System.out.println(restPattern);
    }
}
