package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    /**
     * 创建实例
     * @param cls 需要创建实例的类
     * @return instance
     */
    public static Object newInstance(Class<?> cls) {
        Object inst;
        try {
            inst = cls.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        return inst;
    }

    /**
     * 根据类名创建实例
     * @param clsName
     * @return
     */
    public static Object newInstance(String clsName) throws ClassNotFoundException {
        Class<?> cls = ClassTool.getClassLoader().loadClass(clsName);
        return newInstance(cls);
    }

    /**
     * 调用方法
     * @param obj
     * @param method
     * @param args
     * @return
     */
    public static Object invokeMethod(Object obj, Method method, Object... args) {
        Object result = null;
        try {
            // 去除私有权限
            method.setAccessible(true);
            result = method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            System.out.println("Invoke method error!");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 设置成员变量的值
     * @param obj 对象
     * @param field
     * @param val 需要设置的值
     */
    public static void setField(Object obj, Field field, Object val) {
        // 去除私有权限
        field.setAccessible(true);
        try {
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            System.out.println("set value error!");
            e.printStackTrace();
        }
    }
}