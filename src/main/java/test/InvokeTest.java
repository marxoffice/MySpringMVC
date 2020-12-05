package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class InvokeTest {
    static class A{
        public static void outString(String s){
            System.out.println(s);
        }

        public static void silenceString(){
            System.out.println("This is a silence sign");
        }
    }
    // 经过测试,invoke的参数不能多也不能少
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Method[] ms = A.class.getDeclaredMethods();
        Object[] argi = new String[4];
        for(Method m:ms){
            if("outString".equals(m.getName())){
                // fixme 这里有个大问题,jdk 1.8及其以下的版本 method.getParameters会返回arg0 arg1这种的
                // 不会返回定义名String id这种 需要手动指定顺序 因为1.8使用的很多 所以就不贸然提升版本
                // 经过小组讨论,最终决定不贸然提升jdk版本 强制让用户定义rest参数时按顺序定义

                m.invoke(A.class.newInstance(),argi[0]);
                System.out.println(m.getName());
                System.out.println(m.getParameters());
                Parameter[] ps = m.getParameters();
                System.out.println(ps.length);
                System.out.println(ps[0].getName());
                System.out.println(ps[0].toString());
            }
            else if("silenceString".equals(m.getName()))
                m.invoke(A.class.newInstance()/*,argi[0]*/);
        }
    }
}
