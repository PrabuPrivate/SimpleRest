//$Id$
package com.jaxrs.simplerest.Response;

public class CookieResponse 
{
	private  String key = null;
	private  String value = null;
	private  String domain = null;
	private  int maxAge = 0;
	
	public CookieResponse(String key, String value, String domain,int maxAge)
	{
		this.key = key;
		this.value = value;
		this.maxAge = maxAge;
		this.domain = domain;
	}
	public String getKey() 
	{
		return key;
	}
	public String getValue() 
	{
		return value;
	}
	public String getDomain() 
	{
		return domain;
	}
	public int getMaxAge() 
	{
		return maxAge;
	}
}
