package com.jaxrs.simplerest.service;

import com.jaxrs.simplerest.annotation.ClassAnnotation;
import com.jaxrs.simplerest.annotation.Path;
@ClassAnnotation
public final class TestService 
{
	@Path(value = "api/v1/,test",type="put")
	public static void testMethod()
	{
		
	}
	public static void withOutAnnotation()
	{
		
	}
	public static void withClassAnnotation()
	{
		
	}
}
