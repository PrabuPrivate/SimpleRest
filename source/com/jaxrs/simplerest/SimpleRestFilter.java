package com.jaxrs.simplerest;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;

import com.jaxrs.simplerest.Response.APIResponse;
import com.jaxrs.simplerest.annotation.AttributeParam;
import com.jaxrs.simplerest.annotation.BeanParam;
import com.jaxrs.simplerest.annotation.ContentType;
import com.jaxrs.simplerest.annotation.CookieParam;
import com.jaxrs.simplerest.annotation.HeaderParam;
import com.jaxrs.simplerest.annotation.PathParam;
import com.jaxrs.simplerest.annotation.QueryParam;
import com.jaxrs.simplerest.annotation.RequestMethodParam;
import com.jaxrs.simplerest.annotation.SessionParam;
import com.jaxrs.simplerest.annotation.UploadParam;
import com.jaxrs.simplerest.parser.JSONParser;
import com.jaxrs.simplerest.path.Parser;
import com.jaxrs.simplerest.path.PathContainer;
import com.jaxrs.simplerest.path.PathRule;
import com.jaxrs.simplerest.utils.RestUtils;

public class SimpleRestFilter implements Filter {
	
	private static final Map<String,Method> API_METHODS = new ConcurrentHashMap<String, Method>();
	
	public void init(FilterConfig config) throws ServletException {
		String temp = config.getInitParameter("config-files");
		String files[] = null;
		try{
			if(temp != null){
				files= temp.split(";");
				Parser.init(files);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse)response;
		try {
			PathRule rule = PathContainer.getRule(req.getRequestURI(), req.getMethod());
			Method method = getMethod(rule);
			Object[] parameterValues = getParameterValues(req,method,rule);
			method.getAnnotations();
			APIResponse apiResponse = (APIResponse) method.invoke(null, parameterValues);//processMethodAnnotation(method,parameterValues);
			/*if(req.getAttribute(FrameworkRequestAttribute.CONTENT_TYPE.getValue()) != null)
			{
				apiResponse = RestUtils.processResponseBasedOnContentType(apiResponse,req);
			}*/
			RestUtils.setHttpResponse(req, res, apiResponse);
		}
		catch(Exception e) {
			//logger.log(Level.SEVERE, "Error occured invoking the API Method : "+ apiMethod,(e.getCause() != null? e.getCause():e));
			//RestUtils.setHttpResponse(req, res, APIResponseBuilder.internalServerError());
		}
		return;
	}
	private static Object stringToDataType(String string,Class<?> clazz) 
	{
		if(clazz.isArray()) 
		{
			String[] array = string.split(",");
			return (array==null)?null:ConvertUtils.convert(array, clazz);
		}
		return ConvertUtils.convert(string, clazz);
	}
	private Object[] getParameterValues(HttpServletRequest request,Method method,PathRule rule)throws Exception
	{
		List<Object> paramList = new ArrayList<Object>();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		int noOfParams = parameterTypes.length;
		ArrayList<Integer> pathParamIndex = null;
		ArrayList<String[]> pathParamResources = null;
		for(int i=0;i<noOfParams;i++) 
		{
			Annotation a =annotations[i][0];
			Class<?> paramClass = parameterTypes[i];
			if(a instanceof QueryParam) 
			{
				String paramName = ((QueryParam) a).value();
				String paramValue = request.getParameter(paramName);
				Object convertedValue = stringToDataType(paramValue, paramClass);
				paramList.add(convertedValue);
			}
			else if(a instanceof PathParam) 
			{
				if(pathParamIndex == null)
				{
					pathParamIndex = new ArrayList<>();
					pathParamResources = new ArrayList<>();
				}
				pathParamResources.add(((PathParam)a).value());
				pathParamIndex.add(i);
				paramList.add(null);
			}
			else if(a instanceof AttributeParam) 
			{
				String paramName = ((AttributeParam) a).value();
				paramList.add(request.getAttribute(paramName));
			}
			else if(a instanceof BeanParam)
			{
				String payload = request.getInputStream().toString();
				paramList.add(JSONParser.parse(payload, method,paramClass,i));
			}
			else if(a instanceof SessionParam)
			{
				String paramName = ((SessionParam) a).value();
				paramList.add(paramClass.cast(request.getSession().getAttribute(paramName)));
			}
			else if(a instanceof RequestMethodParam)
			{
				String methodName = ((RequestMethodParam) a).methodName();
				paramList.add((request.getClass().getMethod(methodName)).invoke(request));
			}
			else if(a instanceof HeaderParam) 
			{
				String paramName = ((HeaderParam) a).value();
				String paramValue = request.getHeader(paramName);
				Object convertedValue = stringToDataType(paramValue, paramClass);
				paramList.add(convertedValue);
			}
			else if(a instanceof UploadParam)
			{
				//(request.getAttribute(SecurityUtil.MULTIPART_FORM_REQUEST) == null)? null : (ArrayList<UploadedFileItem>)request.getAttribute(SecurityUtil.MULTIPART_FORM_REQUEST)
				paramList.add(null);
			}
			else if(a instanceof CookieParam)
			{
				String cookieName = ((CookieParam) a).value();
				paramList.add(RestUtils.getCookieResponse(request, cookieName));
			}
			else if(a instanceof ContentType)
			{
				paramList.add(request.getContentType());
			}
		}
		setResourceValue(request,pathParamIndex,pathParamResources,parameterTypes,paramList);
		return paramList.toArray();
	}
	private Method getMethod(PathRule rule) throws Exception
	{
		String absoluteName = rule.getAbsoluteMethodName();
		if(API_METHODS.containsKey(absoluteName))
		{
			return API_METHODS.get(absoluteName);
		}
		String className = rule.getClassName();
		Class<?> clazz = Class.forName(className);
		Method[] methods = clazz.getDeclaredMethods();
		for(int i=0;i<methods.length;i++) 
		{
			Method method = methods[i];
			API_METHODS.put(absoluteName, method);
		}
		return API_METHODS.get(absoluteName);
	}
	public static void setResourceValue(HttpServletRequest request,ArrayList<Integer> pathParamIndex,ArrayList<String[]> pathParamResources,Class<?>[] parameterTypes,List<Object> paramList)
	{
		if(pathParamIndex != null)
		{
			ArrayList<String> resourceValue = getResourceValue(request,pathParamResources);
			for(int index = 0; index< resourceValue.size(); index++)
			{
				int paramIndex = pathParamIndex.get(index);
				Class<?> paramClass = parameterTypes[paramIndex];
				paramList.set(paramIndex, stringToDataType(resourceValue.get(index), paramClass));
			}
		}
	}
	private static ArrayList<String> getResourceValue(HttpServletRequest request,ArrayList<String[]> resourceList)
	{
		HashMap<String,String> resourceMap = RestUtils.getResourceMap(request);
		ArrayList<String> resourceValue = new ArrayList<>();
		for(int index=0;index < resourceList.size();index++)
		{
			String [] resources = resourceList.get(index);
			String value = null;
			for(String resource : resources)
			{
				value = resourceMap.get(resource);
				if(value != null)
				{
					break;
				}
			}
			resourceValue.add(value);
		}
		return resourceValue;
	}
	public static HashMap<String,String> getResourceMap(String requestURI)
	{
		String[] uri = requestURI.split("/");
		HashMap<String,String> resourceMap = new HashMap<String,String>();
		for(int index=1;index<uri.length-1;index++)
		{
			resourceMap.put(uri[index], uri[index+1]);
		}
		return resourceMap;
	}
	public void destroy() {
	}
}
