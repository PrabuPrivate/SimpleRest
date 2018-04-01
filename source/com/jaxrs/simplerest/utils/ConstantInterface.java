package com.jaxrs.simplerest.utils;

public final class ConstantInterface 
{
	public interface HttpStausCode{
		public int getStatus();
	}
	public interface ResponseContentType{
		public String getContentType();
	}
	public interface APIStatus{
		public int code();
		public String getMessage();
	}
	public abstract class Resource{
		public abstract String[] getValue();
	}
}
