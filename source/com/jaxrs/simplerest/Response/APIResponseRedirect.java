//$Id$
package com.jaxrs.simplerest.Response;

public class APIResponseRedirect extends APIResponse
{
	private String url = null;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url) 
	{
		this.url = url;
	}

}
