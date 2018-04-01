//$Id$
package com.jaxrs.simplerest.Response;

import java.util.HashMap;

public class APIResponseForward extends APIResponse
{
	private HashMap<String,String> attribute = null;
	private String url = null;
	public APIResponseForward(String url)
	{
		this.url = url;
	}
	public HashMap<String,String> getAttribute() 
	{
		return attribute;
	}
	public String getURL() 
	{
		return url;
	}
	public void setAttribute(String key, String value)
	{
		if(attribute == null)
		{
			attribute = new HashMap<String,String>();
		}
		attribute.put(key, value);
	}
}
