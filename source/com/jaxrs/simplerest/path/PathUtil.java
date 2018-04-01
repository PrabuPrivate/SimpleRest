package com.jaxrs.simplerest.path;

import org.w3c.dom.Element;

import com.jaxrs.simplerest.path.URLConstant.ElementName;
import com.jaxrs.simplerest.path.URLConstant.URLAttribute;
import com.jaxrs.simplerest.utils.RestUtils;

public final class PathUtil 
{
	private PathUtil(){}
	public static String getURLAttributeValue(Element e,URLAttribute name)
	{
		String val = e.getAttribute(name.attribute);
		if(RestUtils.isValid(val))
		{
			return val;
		}
		val = null;
		if(isGroupURLElement((Element) e.getParentNode()))
		{
			val= ((Element) e.getParentNode()).getAttribute(name.attribute);
		}
		return RestUtils.isValid(val)?val:null;
	}
	public static boolean isGroupURLElement(Element e)
	{
		return e != null && e.getNodeName().equals(ElementName.GROUP_URL.type);
	}
	public static String getPathName(String method,String path)
	{
		return "/"+method+"/"+path;
	}
}
