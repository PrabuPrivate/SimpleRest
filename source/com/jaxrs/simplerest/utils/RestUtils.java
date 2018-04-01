package com.jaxrs.simplerest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.jaxrs.simplerest.Response.APIResponse;
import com.jaxrs.simplerest.Response.APIResponseError;
import com.jaxrs.simplerest.Response.APIResponseForward;
import com.jaxrs.simplerest.Response.APIResponseRedirect;
import com.jaxrs.simplerest.Response.APIResponseStream;
import com.jaxrs.simplerest.Response.APIResponseSuccess;
import com.jaxrs.simplerest.Response.CookieResponse;
import com.jaxrs.simplerest.parser.BeanParser;


public class RestUtils 
{
	private RestUtils(){}
	private static Logger logger = Logger.getLogger(RestUtils.class.getName());
	public static HashMap<Class,Class> primitiveMap = new HashMap<Class,Class>();//temp
	static
	{
		primitiveMap.put(boolean.class,Boolean.class);
		primitiveMap.put(byte.class,Byte.class);
		primitiveMap.put(char.class,Character.class);
		primitiveMap.put(short.class,Short.class);
		primitiveMap.put(int.class,Integer.class);
		primitiveMap.put(long.class,Long.class);
		primitiveMap.put(float.class,Float.class);
		primitiveMap.put(double.class,Double.class);
		primitiveMap.put(String.class,String.class);
		primitiveMap.put(Boolean.class,Boolean.class);
		primitiveMap.put(Byte.class,Byte.class);
		primitiveMap.put(Character.class,Character.class);
		primitiveMap.put(Short.class,Short.class);
		primitiveMap.put(Integer.class,Integer.class);
		primitiveMap.put(Long.class,Long.class);
		primitiveMap.put(Float.class,Float.class);
		primitiveMap.put(Double.class,Double.class);
	}
	
	public static boolean isValid(String text)
	{
		return (text!=null &&  text.length()>0 && !text.equals("null"));
	}
	public static void setHttpResponse(HttpServletRequest request, HttpServletResponse response,APIResponse apiResponse)
	{
		try 
		{
			response.setStatus(apiResponse.getStatus());
			Map<String, String> headers = apiResponse.getHeaders();
			if(headers.isEmpty() == false) 
			{
				Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
				while(iter.hasNext()) 
				{
					Entry<String,String> entry = iter.next();
					response.setHeader(entry.getKey(), entry.getValue());
				}
			}
			Map<String, Object> sessions = apiResponse.getSessions();
			if(sessions != null && sessions.isEmpty() == false) 
			{
				HttpSession session = request.getSession();
				Iterator<Entry<String, Object>> iter = sessions.entrySet().iterator();
				while(iter.hasNext()) 
				{
					Entry<String,Object> entry = iter.next();
					Object obj = entry.getValue();
					if(obj != null)
					{
						session.setAttribute(entry.getKey(), obj);
					}
					else
					{
						session.removeAttribute(entry.getKey());
					}
					
				}
			}
			List<CookieResponse> cookies = apiResponse.getCookies();
			if(cookies!=null) 
			{
				for(int index=0;index<cookies.size();index++)
				{
					CookieResponse cookie = cookies.get(index);
					setCookieValue(request, response, cookie.getKey(), cookie.getValue(), cookie.getMaxAge(), cookie.getDomain());
				}
			}
			if(apiResponse instanceof APIResponseSuccess)
			{
				response.setCharacterEncoding("UTF-8");//No i18n
				Object obj= ((APIResponseSuccess) apiResponse).getContent();
				if(obj != null)
				{
					try
					{	boolean isArrayList = obj instanceof ArrayList;
						obj = BeanParser.parse(obj);
						if(obj != null && isArrayList)
						{
							response.getWriter().write(obj.toString());
						}
						else if(isArrayList)
						{
							obj = new JSONArray();
							response.getWriter().write(obj.toString());
						}
					}
					catch(Exception e)
					{
						logger.log(Level.SEVERE, "Error occured while parsing java object to json ",e);
						throw new RuntimeException(e);
					}
				}
			}
			else if(apiResponse instanceof APIResponseError)
			{
				response.setCharacterEncoding("UTF-8");//No i18n
				APIResponseError res= ((APIResponseError) apiResponse);
				HashMap<String,Object> responseMap = new HashMap<String,Object>();
				if(res.getErrorcode() != 0)
				{
					responseMap.put("errorcode", res.getErrorcode());
				}
				if(res.getErrormessage() != null)
				{
					responseMap.put("errormessage", res.getErrormessage());
				}
				//Currently we are not supporting exception in response
				/*if(res.getException() != null)
				{
					responseMap.put("exception",res.getException().getMessage());
				}*/
				if(responseMap.size() > 0)
				{
					try
					{	
						response.getWriter().write(BeanParser.parse((responseMap).toString()).toString());
					}
					catch(Exception e)
					{
						logger.log(Level.SEVERE, "Error occured while parsing error map to json ",e);
						throw new RuntimeException(e);
					}
				}
				
			}
			else if(apiResponse instanceof APIResponseStream)
			{
				int content_length = copyStream(((APIResponseStream) apiResponse).getInputStream(), response.getOutputStream());
				response.setContentLength( content_length );
			}
			else if(apiResponse instanceof APIResponseForward)
			{
				HashMap<String, String > attributes = ((APIResponseForward) apiResponse).getAttribute();
				if(attributes != null) 
				{
					Iterator<Entry<String, String>> iter = attributes.entrySet().iterator();
					while(iter.hasNext()) 
					{
						Entry<String,String> entry = iter.next();
						request.setAttribute(entry.getKey(), entry.getValue());
					}
				}
				try 
				{
					request.getRequestDispatcher(((APIResponseForward) apiResponse).getURL()).forward(request, response);
				} 
				catch (ServletException e) 
				{
					logger.log(Level.SEVERE, "Error occured while forwarding to "+((APIResponseForward) apiResponse).getURL(),e);
					throw new RuntimeException(e);
				}
			}
			else if(apiResponse instanceof APIResponseRedirect)
			{
				try 
				{
					response.sendRedirect(((APIResponseRedirect)apiResponse).getUrl());
				} 
				catch (Exception e) 
				{
					logger.log(Level.SEVERE, "Error occured while Redirect to "+((APIResponseRedirect) apiResponse).getUrl(),e);
					throw new RuntimeException(e);
				}
			}
		}
		catch (IOException e) 
		{
			logger.log(Level.SEVERE, "IOException occured while setting APIResponse ",e);
			throw new RuntimeException(e);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Error occured while setting APIResponse ",e);
			throw new RuntimeException(e);
		}
	}
	private static int copyStream(InputStream in, OutputStream out)throws IOException
	{
		int content_length = 0;
		try
		{
			byte[] array=new byte[64*1024];
			while(true)
			{
				int bytesRead=in.read(array);
				if(bytesRead==-1)
				{
					break;
				}
				out.write(array,0,bytesRead);
				content_length+= bytesRead;
			}
			out.flush();
		}
		finally
		{
			out.close();
			in.close();
		}
		return content_length;
	}
	public static CookieResponse getCookieResponse(HttpServletRequest request, String key)
	{
		Cookie[] cookiesList = request.getCookies();
		if(cookiesList != null)
		{
			for(Cookie cookie : cookiesList)
			{
				if (cookie.getName().equals(key))
				{
					return new CookieResponse(key,cookie.getValue(),cookie.getDomain(),cookie.getMaxAge());
				}
			}
		}
		return null;
	}
	static void setCookieValue(HttpServletRequest request,HttpServletResponse response,String key,String value,int maxAge,String parentDomain)
	{
		Cookie[] cookiesList = request.getCookies();
		if(cookiesList != null)
		{
			for(Cookie cookie : cookiesList)
			{
				if (cookie.getName().equals(key))
				{
					if(maxAge == 0)
					{
						removeCookie(response,cookie);
					}
					else
					{
						cookie.setValue(value);
						addCookie(response, cookie, maxAge,parentDomain);
					}
					return ;
				}
			}
		}
		if(maxAge > 0)
		{
			Cookie cookie = new Cookie(key, value);
			addCookie(response, cookie, maxAge,parentDomain);
		}
	}
	static void addCookie(HttpServletResponse response,Cookie cookie ,int maxAge,String parentDomain)
	{
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		if(parentDomain != null)
		{
			cookie.setDomain(parentDomain);
		}
		response.addCookie(cookie);
	}
	static void removeCookie(HttpServletResponse response,Cookie cookie)
	{
		cookie.setMaxAge(0);
		cookie.setValue("");
		response.addCookie(cookie);
	}
	public static HashMap<String,String> getResourceMap(HttpServletRequest hreq)
	{
		HashMap<String,String> resourceMap = null;
		if(hreq.getAttribute("")!= null)
		{
			resourceMap = (HashMap<String, String>) hreq.getAttribute("resourcemap");
		}
		else
		{
			resourceMap = getResourceMap(hreq.getRequestURI());
			hreq.setAttribute("resourcemap",resourceMap);
		}
		return resourceMap;
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

}
