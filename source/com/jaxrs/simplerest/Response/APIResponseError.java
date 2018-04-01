//$Id$
package com.jaxrs.simplerest.Response;


public class APIResponseError extends APIResponse
{
	private int errorcode = 0;
	private String errormessage = null;
	private Exception exception = null;
	
	public APIResponseError(int errorcode)
	{
		this.errorcode = errorcode;
	}
	public APIResponseError(String errormessage)
	{
		this.errormessage = errormessage;
	}
	public APIResponseError(int errorcode, String errormessage)
	{
		this.errormessage = errormessage;
		this.errorcode = errorcode;
	}
	public APIResponseError(int errorcode, String errormessage, Exception e)
	{
		this.errormessage = errormessage;
		this.errorcode = errorcode;
		this.exception = e;
	}
	public String getErrormessage() 
	{
		return errormessage;
	}
	public int getErrorcode()
	{
		return errorcode;
	}
	public Exception getException() 
	{
		return exception;
	}
}
