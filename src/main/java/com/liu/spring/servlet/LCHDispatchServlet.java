package com.liu.spring.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

import com.liu.spring.annotation.LCHAutorited;
import com.liu.spring.annotation.LCHController;
import com.liu.spring.annotation.LCHRequestMapping;
import com.liu.spring.annotation.LCHService;
import com.liu.spring.common.Handle;


/**
* @author kkagr
* @version 创建时间：2018年5月6日 上午10:17:41
* 类说明
*/
public class LCHDispatchServlet extends HttpServlet{
	//所有的配载都放入到properties中
	private Properties properties = new Properties();
	
	private List<String> classNames = new ArrayList<String>();
	
	private Map<String,Object> ioc = new HashMap<String, Object>();
	//private Map<String,Object> handleMapping = new HashMap<String, Object>();
	
	private List<Handle> hadleMapping = new ArrayList<Handle>();
	
	//初始化阶段调用的方法
	@Override
	public void init(ServletConfig config) throws ServletException {
		//1.加载配置文件
		doLoadConfig(config.getInitParameter("contextConfigLocation").toString());
		
		//2.根据配置文件扫描所有的相关类
		doScanner(properties.get("scanPackage").toString());
		
		//3.初始化所有相关类的实例，并且将其放入到ioc容器中，也就是map中
		doInstance();
		
		//4.自动实现依赖注入
		doAutiWrited();
		//5.初始化HandleMapping
		doInitHandlerMapping();
		
	}


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}
	
	//6.等待请求
	//运行阶段执行的方法
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			doDispatch(req, resp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.getWriter().write("500 Exception");
		}
		
	}
	private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Handle handle = getHandler(req);
		if(handle == null){
			resp.getWriter().write("404 NOT Found");
			return;
		}
		Class<?>[] parameterTypes = handle.method.getParameterTypes();
		Object[] paramValues = new Object[parameterTypes.length];
		Map<String,String[]> params = req.getParameterMap();
		for(Map.Entry<String, String[]> param:params.entrySet()){
			String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "\\");
			if(!handle.paramIndexMapping.containsKey(param.getKey())){continue;}
			int index = handle.paramIndexMapping.get(param.getKey());
			paramValues[index] = handle.convert(parameterTypes[index],value);
		}
		int reqIndex = handle.paramIndexMapping.get(HttpServletRequest.class.getName());
		paramValues[reqIndex] = req;
		int respIndex = handle.paramIndexMapping.get(HttpServletResponse.class.getName());
		paramValues[respIndex] = resp;
		handle.method.invoke(handle.controller, paramValues);
	}



	private Handle getHandler(HttpServletRequest req) {
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replace("/+", "/");

		for(Handle handle:hadleMapping){
			Matcher matcher = handle.pattern.matcher(url);
			if(!matcher.matches()){continue;}
			return handle;
		}
		return null;
	}


	private void doLoadConfig(String location) {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
		try {
			properties.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null!=resourceAsStream){
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private void doScanner(String packageName) {
		
		//递归扫描
		URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
		File classDir = new File(url.getFile());
		for(File file:classDir.listFiles()){
			if(file.isDirectory()){
				doScanner(packageName+"."+file.getName());
			}else{
				String className = packageName+"."+file.getName().replaceAll(".class", "");
				classNames.add(className);
			}
		}
	}

	private void doInstance() {
		if(classNames.isEmpty()){
			return;
		}
		try {
			for(String className:classNames){
				Class<?> clazz = Class.forName(className);
				//接下来进入bean实例化，初始化ioc容器
				
				//ioc规则
				//1.keykey是用类名首字符小写
				//2.如果用户自定义名字，那么就使用自定义名字
				
				if(clazz.isAnnotationPresent(LCHController.class)){
					String beanName = lowerFirstKey(clazz.getSimpleName());
					ioc.put(beanName, clazz.newInstance());
				}else if(clazz.isAnnotationPresent(LCHService.class)){
					LCHService service = clazz.getAnnotation(LCHService.class);
					String beanName = service.value();
					if(StringUtils.isBlank(beanName)){
						beanName = lowerFirstKey(clazz.getSimpleName());
					}
					ioc.put(beanName, clazz.newInstance());
					//3.如果是接口，使用接口类型作为key
					Class<?>[] interfaces = clazz.getInterfaces();
					for(Class<?> i:interfaces){
						ioc.put(i.getName(), clazz.newInstance());
					}
				}else{
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doAutiWrited() {
		if(ioc.isEmpty()){return;}
		for(Map.Entry<String, Object> entry:ioc.entrySet()){
			//首先第一步提取所有的字段Field
			
			
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for(Field field:fields){
				if(field.isAnnotationPresent(LCHAutorited.class)){
					LCHAutorited autorited = field.getAnnotation(LCHAutorited.class);
					String beanName = autorited.value().trim();
					if(StringUtils.isBlank(beanName)){
						beanName = field.getType().getName();
					}
					//不管public还是private都要强制注入
					field.setAccessible(true);
					try {
						field.set(entry.getValue(), ioc.get(beanName));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		
		}
	}

	private void doInitHandlerMapping() {
		if(ioc.isEmpty()){return;}
		for(Map.Entry<String, Object> entry:ioc.entrySet()){
			Class<?> clazz = entry.getValue().getClass();
			if(!clazz.isAnnotationPresent(LCHController.class)){continue;}
			
			String baseUrl = "";
			if(clazz.isAnnotationPresent(LCHRequestMapping.class)){
				LCHRequestMapping requestMapping = clazz.getAnnotation(LCHRequestMapping.class);
				baseUrl = requestMapping.value().trim();
			}
			Method[] methods = clazz.getMethods();
			for(Method method:methods){
				if(!method.isAnnotationPresent(LCHRequestMapping.class)){continue;}
				 LCHRequestMapping requestMapping = method.getAnnotation(LCHRequestMapping.class);
				 String url = (baseUrl+requestMapping.value()).replaceAll("/+", "/");
				 Pattern pattern = Pattern.compile(url);
				 hadleMapping.add(new Handle(entry.getValue(), method, pattern));
				 System.out.println("Mapping"+url+"method"+method);
				
			}
		}
		
	}
	private String lowerFirstKey(String str){
		char[] chars = str.toCharArray();
		chars[0]+=32;
		return chars.toString();
	}
	
}
