//$Id$
package com.jaxrs.simplerest.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class APIResponse 
{
	private int status = 0;
	private HashMap<String,String> headers = new HashMap<String,String>();
	private HashMap<String,Object> sessions = null;
	private ArrayList<CookieResponse> cookies = null;
	
	public int getStatus()
	{
		return status;
	}
	void setStatus(int status) 
	{
		this.status = status;
	}
	public Map<String, String> getHeaders() 
	{
		if(headers == null)
		{
			return headers;
		}
		return Collections.unmodifiableMap(headers);
	}
	public Map<String,Object> getSessions() 
	{
		if(sessions == null)
		{
			return sessions;
		}
		return  Collections.unmodifiableMap(sessions);
	}
	public List<CookieResponse> getCookies() 
	{
		if(cookies == null)
		{
			return null;
		}
		return Collections.unmodifiableList(cookies);
	}
	public void setContentType(String contentType) 
	{
		headers.put("Content-Type", contentType);
	}
	public void setHeader(String key, String value)
	{
		headers.put(key,value);
	}
	public void addCookie(String key, String value,String domain, int maxAge)
	{
		if(cookies == null)
		{
			cookies = new ArrayList<CookieResponse>();
		}
		cookies.add(new CookieResponse(key,value,domain,maxAge));
	}
	public void addCookie(CookieResponse cookie)
	{
		if(cookies == null)
		{
			cookies = new ArrayList<CookieResponse>();
		}
		cookies.add(cookie);
	}
	public void removeCookie(String key)
	{
		if(cookies == null)
		{
			cookies = new ArrayList<CookieResponse>();
		}
		cookies.add(new CookieResponse(key,"",null,0));
	}
	public void addSession(String key,Object obj)
	{
		if(sessions == null)
		{
			sessions = new HashMap<String,Object>();
		}
		sessions.put(key, obj);
	}
	public void removeSession(String key)
	{
		if(sessions == null)
		{
			sessions = new HashMap<String,Object>();
		}
		sessions.put(key, null);
	}
	public Object getSessionAttribute(String key)
	{
		if(sessions != null)
		{
			return sessions.get(key);
		}
		return null;
	}
}
