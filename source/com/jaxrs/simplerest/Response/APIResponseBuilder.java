//$Id$
package com.jaxrs.simplerest.Response;

import java.io.InputStream;

import com.jaxrs.simplerest.utils.ConstantInterface.HttpStausCode;
import com.jaxrs.simplerest.utils.ConstantInterface.ResponseContentType;


public final class APIResponseBuilder 
{
	private APIResponseBuilder(){}
	public static APIResponse setSuccessResponse(HttpStausCode code,ResponseContentType contentType)
	{
		APIResponseSuccess response = new APIResponseSuccess(null);
		response.setStatus(code.getStatus());
		response.setContentType(contentType.getContentType());
		return response;
	}
	/*public static APIResponse setSuccessResponse(Object obj)
	{
		APIResponseSuccess response = new APIResponseSuccess(obj);
		response.setStatus(HttpStausCode.OK.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse setCreatedSuccessResponse()
	{
		APIResponseSuccess response = new APIResponseSuccess(null);
		response.setStatus(HttpStausCode.CREATED.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse setCreatedSuccessResponse(Object obj)
	{
		APIResponseSuccess response = new APIResponseSuccess(obj);
		response.setStatus(HttpStausCode.CREATED.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse setStreamResponse(InputStream in,String contentType)
	{
		APIResponseStream response = new APIResponseStream(in);
		response.setStatus(HttpStausCode.OK.getStatus());
		response.setContentType(contentType);
		return response;
	}
	public static APIResponse setStreamResponse(InputStream in,String contentType,String fileName)
	{
		APIResponseStream response = new APIResponseStream(in,fileName);
		response.setStatus(HttpStausCode.OK.getStatus());
		response.setContentType(contentType);
		return response;
	}
	public static APIResponse setForwardResponse(String url)
	{
		APIResponseForward response = new APIResponseForward(url);
		response.setStatus(HttpStausCode.OK.getStatus());
		return response;
	}
	
	public static APIResponse badRequestResponse(int code, String msg)
	{
		APIResponseError response = new APIResponseError(code,msg);
		response.setStatus(HttpStausCode.BAD_REQUEST.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse forbiddenResponse(APIStatus status)
	{
		APIResponseError response = new APIResponseError(status.getCode(),status.getMessage());
		response.setStatus(HttpStausCode.FORBIDDEN.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse methodNotAllowed() 
	{
		APIResponseError response  = new APIResponseError(HttpStausCode.METHOD_NOT_ALLOWED.getStatus(),"METHOD_NOT_ALLOWED");//No i18n
		response.setStatus(HttpStausCode.METHOD_NOT_ALLOWED.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse internalServerError() 
	{
		APIResponseError response  = new APIResponseError(HttpStausCode.INTERNAL_SERVER_ERROR.getStatus(),"INTERNAL_SERVER_ERROR");//No i18n
		response.setStatus(HttpStausCode.INTERNAL_SERVER_ERROR.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	public static APIResponse internalServerError(Exception e) 
	{
		APIResponseError response  = new APIResponseError(0,"INTERNAL_SERVER_ERROR",e);//No i18n
		response.setStatus(HttpStausCode.INTERNAL_SERVER_ERROR.getStatus());
		response.setContentType(ResponseContentType.JSON.getType());
		return response;
	}
	
	public static APIResponse setKnownError(APIStatus status)
	{
		return  setKnownError(status,(Exception)null);
	}
	public static APIResponse setKnownError(APIStatus status,Exception e)
	{
		return setKnownError(status.getCode(),status.getMessage(),e);
	}
	public static APIResponse setKnownError(APIStatus status,String msg)
	{
		return setKnownError(status.getCode(),msg,null);
	}
	private static APIResponse setKnownError(int code,String msg,Exception e)
	{
		APIResponseError response = new APIResponseError(code,msg,e);
		response.setStatus(HttpStausCode.KNOWN_ERROR.getStatus());
		response.setContentType("application/json");//No i18n
		return response;
	}*/
}
