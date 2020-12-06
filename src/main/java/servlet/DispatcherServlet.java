package servlet;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import annotation.Autowired;
import annotation.RequestMapping;
import annotation.Controller;
import annotation.RequestParam;
import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.ClassTool;
import utils.Reflection;
import utils.UrlMatcher;
import view.View;

/**
 * Servlet implementation class DispatcherServlet
 */
@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<Class<?>, Object> beanMap=new HashMap<>();
	private Map<Method,Object> beanMethodMap =new HashMap<>();
	private Map<String,Method> urlMethodMap=new HashMap<>();


	public DispatcherServlet() {
		super();
	}


	public void init(ServletConfig config) throws ServletException {
		// 这里的init是一个示范 它处理了controller 以及其requestmap autowired
		// 如果需要使用复杂的component 和 componentScan
		// 等其它注解 请查看test下appcontext的模板
		System.out.println("这里开始init");
		Set<Class<?>> clzSet=ClassTool.getClasses("controller");
		for(Class<?> c:clzSet) // 遍历所有的controller class
		{
			Object o = Reflection.newInstance(c);

			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				Class<?> fieldClass = f.getType();
				if (f.isAnnotationPresent(Autowired.class)) {
					Object field = Reflection.newInstance(fieldClass);
					Reflection.setField(o, f, field);
					beanMap.put(fieldClass, field);
				}

			}

			Controller rc=c.getAnnotation(Controller.class);
			if(rc!=null)
			{
				try {
					Method[] methods=c.getDeclaredMethods();
					for(Method m:methods)
					{
						RequestMapping rm=m.getAnnotation(RequestMapping.class);
						if(rm!=null)
						{
							String object_uri = rc.value();
							String uri=rm.value();
							urlMethodMap.put(object_uri+uri, m); // 类uri + method uri
							beanMethodMap.put(m, o);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			beanMap.put(c, o);
		}


		ServletContext servletContext = config.getServletContext();

		// 处理用户的静态资源
		registerStatic(servletContext);
	}

	private void registerStatic(ServletContext servletContext) {

		//注册jsp文件
		ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
		jspServlet.addMapping("/WEB-INF/*");

		//动态注册处理静态资源的默认Servlet
		ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
		defaultServlet.addMapping("/favicon.ico"); //网站头像
		// defaultServlet.addMapping("/" + "*");

		// 三个常用的网页元素
		defaultServlet.addMapping("/static/images/*");
		defaultServlet.addMapping("/static/js/*");
		defaultServlet.addMapping("/static/css/*");

		// 允许用户自己添加自己的文件或类型
		defaultServlet.addMapping("/static/files/*");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("这里开始doget");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
//		System.out.println(request.getParameter("name"));
		String uri=request.getRequestURI();
		System.out.println("访问的uri"+uri);
		// 判断uri是否macher
//		UrlMatcher.matchUrl()
		List<String> attr = null;
		Method m = null;
		for(Map.Entry<String, Method> entry : urlMethodMap.entrySet()){
			String mapUri = entry.getKey();
			Method mapMethod = entry.getValue();
			attr = UrlMatcher.matchUrl(uri,mapUri);
			if(attr == null){ // 未匹配成功
				System.out.println("未匹配成功");
				System.out.println("mapUri:"+mapUri);
				continue;
			}
			else{ // 匹配成功(包含有rest参数和无rest参数的empty)
				m = mapMethod;
				System.out.println("匹配成功");
				System.out.println("mapUri:"+mapUri);
				System.out.println(attr.size());
				break;
			}

		}
//		Method m=methodsMap.get(uri); // 获得方法

		InputStream fileSourceStream;
		if(m!=null){
			Parameter[] parameters=m.getParameters();  // 方法参数
//			System.out.println("parameters[0]："+parameters[0].getName());
			System.out.println("方法参数长度："+parameters.length);
			Object[] args=new Object[parameters.length];
			// 判断是否为文件
			if(ServletFileUpload.isMultipartContent(request)){
				System.out.println("文件流");

				Parameter p=parameters[0];
				fileSourceStream = request.getInputStream();
				args[0] = fileSourceStream;
			}
			else{
				System.out.println("普通request");
				int i = 0;
				if(!attr.isEmpty()) {
					System.out.println("rest开始");
					for (String para : attr) {
						System.out.println("rest args_value = "+para);
						args[i] = para; // rest参数类型直接为String
						i += 1;
					}
				}
				System.out.println("request参数添加");
				System.out.println("i="+i);
//				args[i] = request.getParameterMap(); // 这个方法会返回一个 Map<String, String[]> 可以获得所有的parameter
				for(;i<parameters.length;i++) // 添加剩余的request中参数
				{
					Parameter p=parameters[i];
					RequestParam paramAnno = p.getAnnotation(RequestParam.class);
					if(paramAnno != null){
						System.out.println("用户自定义普通url参数"+paramAnno.value());
						String value = request.getParameter(paramAnno.value());
						args[i]=value;
						System.out.println("request参数"+args[i]);
					} else if(i == parameters.length-1){
						System.out.println("用户需要获得所有的url参数，包括多参数状态的");
						args[i] = request.getParameterMap();
					}
				}
			}

			Object obj= beanMethodMap.get(m);
			try {
				System.out.println("invoke method "+m.getName()+" in obj: "+obj+" in class: "+obj.getClass());
				Object ret=m.invoke(obj, args);
				if(ret instanceof View){
					// TODO ViewModel bug fix
					View viewCurr = (View) ret;
					if (viewCurr.getPath() != null) {
						String path = viewCurr.getPath();
						if (path.endsWith(".jsp")) { // 渲染jsp文件
							Map<String, Object> model = viewCurr.getAttribute();
							for (Map.Entry<String, Object> entry : model.entrySet()) {
								request.setAttribute(entry.getKey(), entry.getValue());
							}
							System.out.println("渲染jsp页面 " + path);
							request.getRequestDispatcher(path).forward(request, response);
						} else { // 重定向操作
							System.out.println("触发了重定向操作 "+path);
							response.sendRedirect(path);
						}
					}
				} else { // 用户直接返回了一些数据 这里转成JSON 返回即可
					response.getWriter().write(JSON.toJSONString(ret));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
