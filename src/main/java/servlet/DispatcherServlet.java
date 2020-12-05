package servlet;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
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
import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import utils.ClassTool;
import utils.UrlMatcher;

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
		System.out.println(request.getParameter("name"));
		String uri=request.getRequestURI();
		// 判断uri是否macher
//		UrlMatcher.matchUrl()
		Map<String, String> attr = null;
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
				break;
			}

		}
//		Method m=methodsMap.get(uri); // 获得方法

		InputStream fileSourceStream;
		if(m!=null){
			Parameter[] parameters=m.getParameters();  // 方法参数
			System.out.println("parameters[0]："+parameters[0]);
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
				if(attr != null) {
					System.out.println("rest开始");
					for (Map.Entry<String, String> entry : attr.entrySet()) {
						String args_name = entry.getKey();
						String args_value = entry.getValue();
						System.out.println("rest args_name="+args_name+"; args_value = "+args_value);
						args[i] = args_value; // rest参数类型直接为String
						i += 1;
					}
//					args[0] = attr; // 参数0位置直接放rest的map
				}
				System.out.println("request参数添加");
				System.out.println("i="+i);
				for(;i<parameters.length;i++) // 添加剩余的request中参数
				{
					Parameter p=parameters[i];
					System.out.println(p.getName());
					String value=request.getParameter(p.getName());
					args[i]=convert(value,p.getType());
					System.out.println("request参数"+args[i]);
				}
			}

			Object obj=beansMap.get(m);
			try {
				System.out.println("args[]"+args[0]);
				Object ret=m.invoke(obj, args);
				//TODO view model
				response.getWriter().write(JSON.toJSONString(ret));
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
