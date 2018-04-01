package com.jaxrs.simplerest.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jaxrs.simplerest.annotation.CaseConversion;
import com.jaxrs.simplerest.annotation.Ignore;
import com.jaxrs.simplerest.annotation.StringConversion;
import com.jaxrs.simplerest.utils.RestUtils;

public final class BeanParser
{	
	private BeanParser() {}
	
	public static Object parse(Object obj) throws Exception
	{
		if(obj == null)
		{
			return null;
		}
		if(obj.getClass().isArray())
		{
			return convertToJSONArray(obj);
		}
		else if(obj instanceof ArrayList)
		{
			return convertToJSONArray((ArrayList)obj);
		}
		else if(obj instanceof Map)
		{
			return convertToJSONObject((Map)obj);
		}
		else
		{
			return convertToJSONObject(obj);
		}
	}
	
	private static JSONObject convertToJSONObject(Object javaObj) throws Exception
	{
		if(javaObj == null)
		{
			return null;
		}
		ArrayList<Field> fields = new ArrayList<Field>();
		findAllFields(fields,javaObj.getClass());
		JSONObject jsonObj = new JSONObject();
		for(int i=0; i<fields.size(); i++)
		{
			Field fd = fields.get(i);
			String fdName = fd.getName();
			Class fdClass = fd.getType();
			Object fdValue = null;
			
			if(fd.getAnnotation(Ignore.class) != null)
			{
				continue;
			}
			StringBuilder sb = new StringBuilder();
			if(fdClass == Boolean.class || fdClass == boolean.class)
			{
				sb.append("is");//No i18n
			}
			else
			{
				sb.append("get");//No i18n
			}
			sb.append(Character.toUpperCase(fdName.charAt(0)));
			sb.append(fdName.substring(1));
			Method m = javaObj.getClass().getMethod(sb.toString());
			fdValue = m.invoke(javaObj);
			if(fdValue == null)
			{
				continue;
			}
			if(RestUtils.primitiveMap.get(fdValue.getClass()) != null)
			{
				if(fd.getAnnotation(StringConversion.class) != null)
				{
					fdValue = String.valueOf(fdValue);
				}
			}
			else if(fdValue.getClass().isEnum())
			{
				if(fd.getAnnotation(CaseConversion.class) != null)
				{
					fdValue = ((Enum)fdValue).name().toLowerCase();
				}
				else
				{
					fdValue = ((Enum)fdValue).name();
				}
			}
			else if(fdValue.getClass().isArray())
			{
				fdValue = convertToJSONArray(fdValue,fd.getAnnotations());
			}
			else if(Collection.class.isAssignableFrom(fdValue.getClass()))
			{
				fdValue = convertToJSONArray((Collection)fdValue,fd.getAnnotations());
			}
			else
			{
				fdValue = convertToJSONObject(fdValue);
			}
			jsonObj.put(fdName,fdValue);
		}
		return jsonObj;
	}
	
	private static void findAllFields(ArrayList<Field> fields,Class cls) throws Exception
	{
  	fields.addAll(Arrays.asList(cls.getDeclaredFields()));
		if(cls.getSuperclass() != null && cls.getSuperclass() != Object.class)
		{
      	findAllFields(fields,cls.getSuperclass());
  	}
	}

	private static JSONArray convertToJSONArray(Object arrObj,Annotation... annotations) throws Exception
	{
		if(arrObj == null)
		{
			return null;
		}
		int arrLength = Array.getLength(arrObj);
		if(arrLength == 0)
		{
			return new JSONArray();
		}
		boolean stringConversion = false;
		boolean caseConversion = false;
		if(annotations != null)
		{
			for(int i=0; i<annotations.length; i++)
			{
				if(annotations[i].annotationType() == StringConversion.class)
				{
					stringConversion = true;
				}
				else if(annotations[i].annotationType() == CaseConversion.class)
				{
					caseConversion = true;
				}
			}
		}
		JSONArray jsonArr = new JSONArray();
		for(int i=0; i<arrLength; i++)
		{
			Object obj = Array.get(arrObj,i);
			if(obj == null)
			{
				continue;
			}
			if(RestUtils.primitiveMap.get(obj.getClass()) != null)
			{
				if(stringConversion)
				{
					obj = String.valueOf(obj);
				}
				else if(obj instanceof Long)
				{
					obj = String.valueOf(obj);
				}
			}
			else if(obj.getClass().isEnum())
			{
				if(caseConversion)
				{
					obj = ((Enum)obj).name().toLowerCase();
				}
				else
				{
					obj = ((Enum)obj).name();
				}
			}
			else if(obj.getClass().isArray())
			{
				obj = convertToJSONArray(obj,annotations);
			}
			else if(ArrayList.class.isAssignableFrom(obj.getClass()))
			{
				obj = parse((ArrayList)obj);
			}
			else if(Map.class.isAssignableFrom(obj.getClass()))
			{
				obj = parse((Map)obj);
			}
			else
			{
				obj = convertToJSONObject(obj);
			}
			jsonArr.put(obj);
		}
		return jsonArr;
	}
	
	private static JSONArray convertToJSONArray(Collection collectionObj,Annotation... annotations) throws Exception
	{
		if(collectionObj == null)
		{
			return null;
		}
		return convertToJSONArray(collectionObj.toArray(),annotations);
	}
	
	private static JSONObject convertToJSONObject(Map mapObj) throws Exception
	{
		if(mapObj == null)
		{
			return null;
		}
		JSONObject jsonObj = new JSONObject();
		Iterator<Map.Entry> it = mapObj.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry pair = it.next();
			String key = pair.getKey().toString();
			Object value = pair.getValue();
			
			if(RestUtils.primitiveMap.get(value.getClass()) != null)
			{
				if(value instanceof Long || value instanceof Float || value instanceof Double)
				{
					value = String.valueOf(value);
				}
				jsonObj.put(key,value);
			}
			else if(value.getClass().isEnum())
			{
				jsonObj.put(key,((Enum)value).name());
			}
			else
			{
				jsonObj.put(key,parse(value));
			}
		}
		return jsonObj;
	}
}


