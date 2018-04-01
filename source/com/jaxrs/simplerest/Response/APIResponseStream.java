//$Id$
package com.jaxrs.simplerest.Response;

import java.io.InputStream;

public class APIResponseStream extends APIResponse
{
	private InputStream inputStream=null;
	private String fileName = null;
	public APIResponseStream(InputStream inputStream) 
	{
		this.inputStream = inputStream;
	}
	public APIResponseStream(InputStream inputStream,String fileName) 
	{
		this.inputStream = inputStream;
		this.fileName = fileName;
	}
	public InputStream getInputStream()
	{
		return inputStream;
	}
	public String getFileName()
	{
		return fileName;
	}
}
