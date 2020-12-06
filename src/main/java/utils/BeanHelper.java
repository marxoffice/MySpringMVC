package utils;

import annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class BeanHelper {
    /**
     * 加载bean容器中所有bean的属性
     * @param cls 某个class
     * @param beanMap bean容器
     */
    public static void loadBeans(Class<?> cls, Map<Class<?>, Object> beanMap) {
        Configuration conf = cls.getAnnotation(Configuration.class);
        assert conf != null;
        Object confObj = Reflection.newInstance(cls);
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Bean.class)) {
                Object res = Reflection.invokeMethod(confObj, method);
                beanMap.put(method.getReturnType(), res);
            }
        }
    }

    /**
     * 扫描带有component的类
     * @param cls 配置@ComponentScan的类
     * @param beanMap bean容器
     */
    public static void scanComponents(Class<?> cls, Map<Class<?>, Object> beanMap) {
        ComponentScan cs = cls.getAnnotation(ComponentScan.class);
        assert cs != null;
        String[] pkgs = cs.value();
        if (pkgs.length == 0)
            pkgs = new String[] { cls.getPackage().getName() };
        for (String pk : pkgs) {
            Set<Class<?>> clsSet = ClassTool.getClasses(pk);
            for (Class<?> c : clsSet) {
                if (c.isAnnotationPresent(Component.class)) {
                    BeanHelper.loadComponent(c, beanMap);
                }
            }
        }
    }

    /**
     * 为所有带@Autowired注解的属性注入实例
     * @param cls
     * @param beanMap
     */
    public static void loadComponent(Class<?> cls, Map<Class<?>, Object> beanMap) {
        Object o = Reflection.newInstance(cls);
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Autowired.class)) {
                Reflection.setField(o, f, beanMap.get(f.getType()));
            }
        }
        beanMap.put(cls, o);
    }

}
