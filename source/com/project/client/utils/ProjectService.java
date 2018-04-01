package com.project.client.utils;

import com.jaxrs.simplerest.Response.APIResponse;
import com.jaxrs.simplerest.Response.APIResponseBuilder;
import com.project.client.utils.ProjectConstant.HttpStausCode;
import com.project.client.utils.ProjectConstant.ResponseContentType;

public final class ProjectService {
	private ProjectService(){}
	public static APIResponse getResponse()
	{
		return APIResponseBuilder.setSuccessResponse(HttpStausCode.CREATED, ResponseContentType.HTML);
	}
	public static void main(String args[])throws Exception
	{
			System.out.println(getResponse());
	}
}
