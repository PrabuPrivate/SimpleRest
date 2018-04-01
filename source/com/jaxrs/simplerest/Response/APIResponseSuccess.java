//$Id$
package com.jaxrs.simplerest.Response;

public class APIResponseSuccess extends APIResponse 
{
	private Object content = null;
	public APIResponseSuccess(Object content) 
	{
		this.content = content;
	}
	public Object getContent() 
	{
		return content;
	}
}
