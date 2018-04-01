package com.jaxrs.simplerest.path;

public final class URLConstant 
{
	public enum ElementName
	{
		URL("url"),
		GROUP_URL("group-url");
		public String type;
		ElementName(String type)
		{
			this.type = type;
		}
		public String getValue()
		{
			return type;	
		}
	}
	
	public enum URLAttribute
	{
		SERVICE_CLASS("service_class"),
		SERVICE_METHOD("service_method"),
		PATH("path"),
		PATH_REGEX("path_regex"),
		HTTP_METHOD("method");
		public String attribute;
		URLAttribute(String attribute)
		{
			this.attribute = attribute;
		}
	}
}
