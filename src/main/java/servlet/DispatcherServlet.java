package servlet;

import java.io.IOException;
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
import utils.ClassTool;

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
		Set<Class<?>> clzSet=ClassTool.getClasses("controller");
		for(Class<?> c:clzSet)
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
							String uri=rm.value();
							methodsMap.put(config.getServletContext().getContextPath()+uri, m);
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		String uri=request.getRequestURI();
		Method m=methodsMap.get(uri);
		Parameter[] parameters=m.getParameters();
		Object[] args=new Object[parameters.length];
		for(int i=0;i<parameters.length;i++)
		{
			Parameter p=parameters[i];
			String value=request.getParameter(p.getName());
			args[i]=convert(value,p.getType());
		}
		Object obj=beansMap.get(m);
		try {
			Object ret=m.invoke(obj, args);
			//TODO view model
//			response.getWriter().write(JSON.toJSONString(ret));
		} catch (Exception e) {
			e.printStackTrace();
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
