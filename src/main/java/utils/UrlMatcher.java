package utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * url与方法的匹配类
 * 需要完成url的匹配
 * 对于restful的url,还需要给出rest参数
 * 对于普通的参数 需要别的类使用Map getParameterMap()来获得参数
 * 这里的输入url来自String getRequestURI,就没有普通的参数了 ?parameter=XXX
 */
public class UrlMatcher {

    public static List<String> matchUrl(String url, String pattern){
        String[] splitUrl = url.split("/");
        String[] splitPattern = pattern.split("/");
        if(splitUrl.length != splitPattern.length) // 长度不相等 显然不是匹配的url
            return null;

        List<String> restParameters = new LinkedList<String>();
        for(int i=0;i<splitUrl.length;i++){
            if(isRestPattern(splitPattern[i])){
                restParameters.add(getRestAttr(splitUrl[i]));
            } else {
                if(!splitUrl[i].equals(splitPattern[i])) { // 中间有普通url不匹配 直接返回null
                    return null;
                }
            }
        }
        return restParameters; // 匹配成功 返回一个包含了rest attr的列表 用户必须按顺序定义rest参数
    }

    /**
     * 进行url的匹配
     * @param url 用户从浏览器访问的url
     * @param pattern 用户在requestmap中定义的url模型
     * @return 不匹配返回null 否则返回匹配成功的所有的restful参数
     */
//    public static Map<String, String> matchUrl(String url, String pattern){
////        System.out.println("========"+url+"========="+pattern+"======");
//        Map<String, String> attr = new HashMap<>();
//        String[] splitUrl = url.split("/");
//        String[] splitPattern = pattern.split("/");
//        if(splitUrl.length != splitPattern.length) // 长度不相等 显然不是匹配的url
//            return null;
//
//        for(int i=0;i<splitUrl.length;i++){
//            if(isRestPattern(splitPattern[i])){
//                attr.put(getRestAttr(splitPattern[i]),splitUrl[i]);
//            } else {
//                if(!splitUrl[i].equals(splitPattern[i])) { // 中间有普通url不匹配 直接返回null
//                    return null;
//                }
//            }
//        }
//        return attr; // 匹配成功 返回一个包含了rest attr的属性集合
//    }

    public static boolean isRestPattern(String pattern){
       return pattern.startsWith("{") && pattern.endsWith("}");
    }

    public static String getRestAttr(String pattern){
        return pattern.replace("{","").replace("}","");
    }
}

