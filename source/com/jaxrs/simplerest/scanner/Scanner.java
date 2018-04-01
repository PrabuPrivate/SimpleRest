package com.jaxrs.simplerest.scanner;

public interface Scanner 
{
	public void scan(String packageName,Object returnObject)throws Exception;
	public boolean onAccept(String name);
}
