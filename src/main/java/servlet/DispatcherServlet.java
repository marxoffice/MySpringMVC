package servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import annotation.RequestMapping;
import annotation.Controller;
import annotation.RequestParam;
import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.ClassTool;
import utils.UrlMatcher;
import view.View;

/**
 * Servlet implementation class DispatcherServlet
 */
@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<Method,Object> beansMap=new HashMap<>();
	private Map<String,Method> methodsMap=new HashMap<>();


	public DispatcherServlet() {
		super();
	}


	public void init(ServletConfig config) throws ServletException {
		System.out.println("这里开始init");
		Set<Class<?>> clzSet=ClassTool.getClasses("controller");
		for(Class<?> c:clzSet) // 遍历所有的controller class
		{
			Controller rc=c.getAnnotation(Controller.class);
			if(rc!=null)
			{
				try {
					Object o=c.newInstance();
					Method[] methods=c.getDeclaredMethods();
					for(Method m:methods)
					{
						RequestMapping rm=m.getAnnotation(RequestMapping.class);
						if(rm!=null)
						{
							//TODO restful url regex mapping
							String object_uri = rc.value();
							String uri=rm.value();
							methodsMap.put(object_uri+uri, m); // 类uri + method uri
							beansMap.put(m, o);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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
		for(Map.Entry<String, Method> entry : methodsMap.entrySet()){
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
						args[i]=convert(value,p.getType());
						System.out.println("request参数"+args[i]);
					} else if(i == parameters.length-1){
						System.out.println("用户需要获得所有的url参数，包括多参数状态的");
						args[i] = request.getParameterMap();
					}
				}
			}

			Object obj=beansMap.get(m);
			try {
				System.out.println("invoke method "+m.getName()+" in obj: "+obj+" in class: "+obj.getClass());
				Object ret=m.invoke(obj, args);
				if(ret instanceof View){
					// TODO ViewModel bug fix
					View viewCurr = (View) ret;
					if (viewCurr.getPath() != null) {
						String path = viewCurr.getPath();
						if (path.startsWith("/")) { // 重定向操作
							response.sendRedirect(path);
						} else { // 渲染jsp 界面
							Map<String, Object> model = viewCurr.getAttribute();
							for (Map.Entry<String, Object> entry : model.entrySet()) {
								request.setAttribute(entry.getKey(), entry.getValue());
							}
							System.out.println("渲染jsp页面 " + path);
							request.getRequestDispatcher(path).forward(request, response);
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

	private Object convert(String value, Class<?> type) {
		// TODO Auto-generated method stub
		return value;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
