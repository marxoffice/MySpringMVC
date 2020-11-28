package utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassTool {
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 以文件形式获取包下所有class
     * @param pkgName 包名
     * @param pkgPath 包路径
     * @param cls class集合
     */
    private static void addClsByFile(String pkgName, String pkgPath, Set<Class<?>> cls) {
        File dir = new File(pkgPath);
        try {
            // 如果目录存在，则获取其下所有文件
            File[] files = dir.listFiles(pathname ->
                    (pathname.isFile() && pathname.getName().endsWith(".class")) || pathname.isDirectory());
            assert files != null;
            for (File file : files) {
                if (file.isDirectory()) {
                    addClsByFile(pkgName+"."+file.getName(), file.getAbsolutePath(), cls);
                } else {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String clsName = file.getName().substring(0, file.getName().lastIndexOf("."));
                    // 加载类
                    cls.add(getClassLoader().loadClass(pkgName+"."+clsName));
                }
            }
        } catch (Exception e) {
            System.out.println("添加文件出错");
        }
    }

    /**
     * 从pkgName中获取所有class
     * @param pkgName 包名
     * @return class set
     */
    public static Set<Class<?>> getClasses(String pkgName) {
        Set<Class<?>> clsSet = new HashSet<>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(pkgName.replace(".", "/"));
            while(urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    // 如果是以文件形式保存
                    if (protocol.equals("file")) {
                        String pkgPath = URLDecoder.decode(url.getFile(), "UTF-8");
                        addClsByFile(pkgName, pkgPath, clsSet);
                    } else if (protocol.equals("jar")) {
                        // 如果是jar包
                        JarURLConnection jarURLConn = (JarURLConnection) url.openConnection();
                        if (jarURLConn != null) {
                            JarFile jarFile = jarURLConn.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    // 如果是一个.class文件
                                    if (jarEntryName.endsWith(".class")) {
                                        // 获取类名
                                        String clsName = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).
                                                replaceAll("/", ".");
                                        // 加载类
                                        clsSet.add(getClassLoader().loadClass(pkgName+"."+clsName));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return clsSet;
    }
}
