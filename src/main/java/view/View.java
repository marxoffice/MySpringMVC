package view;

import java.util.HashMap;
import java.util.Map;

/**
 * 视图映射类
 * path 返回视图的地址
 * attributeMap 返回页面的参数set
 */
public class View {
    private String path;
    Map<String, Object> attributeMap;

    public View(String path) {
        this.path = path;
        attributeMap = new HashMap<String, Object>();
    }

    public View addModel(String key, Object value) {
        attributeMap.put(key, value);
        return this;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getModel() {
        return attributeMap;
    }
}
