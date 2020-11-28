package test;

import utils.BeanHelper;

import java.util.HashMap;
import java.util.Map;

public class AppContext {
    private final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

    public AppContext(Class<?>... classes){
        try {
            for (Class<?> cls : classes) {
                BeanHelper.loadBeans(cls, BEAN_MAP);
                BeanHelper.scanComponents(cls, BEAN_MAP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> cls) {
        return (T) BEAN_MAP.get(cls);
    }
}
