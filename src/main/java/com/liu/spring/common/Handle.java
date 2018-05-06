package com.liu.spring.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.liu.spring.annotation.LCHRequestMapping;
import com.liu.spring.annotation.LCHRequestParam;

/**
* @author kkagr
* @version 创建时间：2018年5月6日 下午1:36:48
* 类说明
*/
public class Handle {
	public  Object controller;//保存方法对应的实例
	public Method method;//保存方法
	public Pattern pattern;
	public Map<String,Integer> paramIndexMapping;
	
	public Handle(Object controller, Method method, Pattern pattern) {
		this.controller = controller;
		this.method = method;
		this.pattern = pattern;
		paramIndexMapping = new HashMap<String, Integer>();
		paramIndexMapping(method);
	}
	private void paramIndexMapping(Method method2) {
		Annotation[][] pa = method.getParameterAnnotations();
		for(int i = 0; i<pa.length; i++){
			for(Annotation a:pa[i]){
				if(a instanceof LCHRequestParam){
					String paramName = ((LCHRequestParam) a).value();
					if(StringUtils.isNotBlank(paramName)){
						paramIndexMapping.put(paramName, i);
					}
				}
			}
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		for(int i=0;i<parameterTypes.length; i++){
			Class<?> type = parameterTypes[i];
			if(type == HttpServletRequest.class
				||type == HttpServletResponse.class){
				paramIndexMapping.put(type.getName(), i);
			}
		}
	}
	public Object convert(Class<?> type, String value) {
		if(Integer.class == type){
			return Integer.valueOf(value);
		}
		return value;
	}
	public Object getController() {
		return controller;
	}
	public void setController(Object controller) {
		this.controller = controller;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	public Map<String, Integer> getParamIndexMapping() {
		return paramIndexMapping;
	}
	public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
		this.paramIndexMapping = paramIndexMapping;
	}
	
}
