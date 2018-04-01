package com.jaxrs.simplerest.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jaxrs.simplerest.annotation.CaseConversion;
import com.jaxrs.simplerest.annotation.ClassMapper;
import com.jaxrs.simplerest.annotation.ClassIdentifier;
import com.jaxrs.simplerest.utils.RestUtils;

public final class JSONParser
{
	public static Object parse(String payload,Method method,Class<?> parameterClass,int parameterIndex) throws Exception
	{
		Class<?> elementCls = null;
		if(Collection.class.isAssignableFrom(parameterClass))
		{
			ParameterizedType paramType = (ParameterizedType)method.getGenericParameterTypes()[parameterIndex];
			elementCls = (Class<?>)paramType.getActualTypeArguments()[0];
		}
		return privateParse(payload,parameterClass,elementCls);
	}
	public static Object parse(String payload,Class<?> cls) throws Exception
	{
		return privateParse(payload,cls,null);
	}
	public static Object parse(String payload,Class<?> collectionCls, Class<?> actualTypeArgumentCls) throws Exception
	{
		return privateParse(payload,collectionCls,actualTypeArgumentCls);
	}
	private static Object privateParse(String payload,Class<?> actualCls, Class<?> actualTypeArgumentCls) throws Exception
	{
		Object obj = null;
		if(payload == null)
		{
			return null;
		}
		if(actualCls.isArray() || Collection.class.isAssignableFrom(actualCls))
		{
			JSONArray jsonArray = new JSONArray(payload);
			if(actualCls.isArray())
			{
				obj = convertToJavaArray(jsonArray,actualCls.getComponentType());
			}
			else
			{
				obj = convertToJavaCollection(jsonArray,actualTypeArgumentCls,actualCls);
			}
		}
		else
		{
			JSONObject json = new JSONObject(payload);
			obj = convertToJavaObject(json,actualCls);
		}
		return obj;
	}
	private static void findAllFields(ArrayList<Field> fields,Class<?> cls) throws Exception
	{
		fields.addAll(Arrays.asList(cls.getDeclaredFields()));
		if(cls.getSuperclass() != null && cls.getSuperclass() != Object.class)
		{
        		findAllFields(fields,cls.getSuperclass());
    		}
	}
	private static Class getClass(JSONObject jsonObject, Class<?> cls) throws Exception
	{
		Annotation classAnnotation = cls.getAnnotation(ClassIdentifier.class);
		if(classAnnotation!=null)
		{
			String key = ((ClassIdentifier) classAnnotation).key();
			if(key != null&&jsonObject.has(key))
			{
				String value = jsonObject.getString(key);
				ClassMapper[] mapping = ((ClassIdentifier) classAnnotation).mapping();
				for(ClassMapper k : mapping)
				{
					if(value.equals(k.value()))
					{
						return k.cls();
					}
				}
			}
		}
		return null;
	}
	private static Object convertToJavaObject(JSONObject jsonObj,Class<?> cls) throws Exception
	{
		if(jsonObj.length() == 0)
		{
			return null;
		}
		Class<?> beanCls = getClass(jsonObj,cls);
		if(beanCls != null)
		{
			if(!cls.isAssignableFrom(beanCls))
			{
				throw new IllegalArgumentException(beanCls.getName()+" is not a subclass of "+cls.getName());//No i18n
			}
		}
		else
		{
			beanCls = cls;
		}
		ArrayList<Field> fields = new ArrayList<Field>();
		findAllFields(fields,beanCls);
		Object pojo = beanCls.newInstance();
		for(int i=0; i<fields.size(); i++)
		{
			Field fd = fields.get(i);
			String fdName = fd.getName();
			Class fdClass = fd.getType();
			Object fdValue = null;
			
			if(!jsonObj.has(fdName))
			{
				continue;
			}
			if(RestUtils.primitiveMap.get(fdClass) != null)
			{
				String value = jsonObj.getString(fdName);
				value = value != null ? value.trim() : value;
				fdValue = convertToJavaPrimitive(value,fdClass);
			}
			else if(fdClass.isEnum())
			{
				if(fd.getAnnotation(CaseConversion.class) != null)
				{
					fdValue = Enum.valueOf(fdClass,jsonObj.getString(fdName).toUpperCase());
				}
				else
				{
					fdValue = Enum.valueOf(fdClass,jsonObj.getString(fdName));
				}
			}
			else if(fdClass.isArray())
			{
				fdValue = convertToJavaArray(jsonObj.getJSONArray(fdName),fdClass.getComponentType(),fd.getAnnotations());
			}
			else if(Collection.class.isAssignableFrom(fdClass))
			{
				JSONArray jsonArr = jsonObj.getJSONArray(fdName);
				Class<?> elementCls = (Class<?>)((ParameterizedType)fd.getGenericType()).getActualTypeArguments()[0];
				fdValue = convertToJavaCollection(jsonArr,elementCls,fdClass,fd.getAnnotations());
			}
			else
			{
				fdValue = convertToJavaObject(jsonObj.getJSONObject(fdName),fdClass);
			}
			if(fdValue != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append("set"); //No i18n
				sb.append(Character.toUpperCase(fdName.charAt(0)));
				sb.append(fdName.substring(1));
				Method m = pojo.getClass().getMethod(sb.toString(),fdClass);
				m.invoke(pojo,fdValue);
			}
		}
		return pojo;
	}
	private static Object convertToJavaPrimitive(String value,Class<?> primitiveCls) throws Exception
	{
		Class<?> wrapperCls = RestUtils.primitiveMap.get(primitiveCls);
		if(primitiveCls == char.class || primitiveCls == Character.class)
		{
			return new Character(value.charAt(0));
		}
		Constructor<?> ctor = wrapperCls.getConstructor(String.class);
		return ctor.newInstance(value);
	}
	private static Collection<?> convertToJavaCollection(JSONArray jsonArr,Class elementCls,Class<?> collectionCls,Annotation... annotations) throws Exception
	{
		Object arrObj = convertToJavaArray(jsonArr,elementCls,annotations);
		if(arrObj == null)
		{
			return null;
		}
		ArrayList<Object> list = new ArrayList<Object>(Array.getLength(arrObj));
		for(int i=0; i<Array.getLength(arrObj); i++)
		{
			list.add(Array.get(arrObj,i));
		}
		Constructor<?> ctor = collectionCls.getConstructor(Collection.class);
		return (Collection<?>)ctor.newInstance(list);
	}
	public static Object convertToJavaArray(JSONArray jsonArr,Class arrCls,Annotation... annotations) throws Exception
	{
		int arrLength = jsonArr.length();
		if(arrLength == 0)
		{
			return null;
		}
		boolean caseConversion = false;
		if(annotations != null)
		{
			for(int i=0; i<annotations.length; i++)
			{
				if(annotations[i].annotationType() == CaseConversion.class)
				{
					caseConversion = true;
				}
			}
		}
		ArrayList list = new ArrayList(arrLength);
		for(int i=0; i<arrLength; i++)
		{
			Object obj = null;
			if(RestUtils.primitiveMap.get(arrCls) != null)
			{
				obj = convertToJavaPrimitive(jsonArr.getString(i),arrCls);
			}
			else if(arrCls.isEnum())
			{
				if(caseConversion)
				{
					obj = Enum.valueOf(arrCls,jsonArr.getString(i).toUpperCase());
				}
				else
				{
					obj = Enum.valueOf(arrCls,jsonArr.getString(i));
				}
			}
			else if(arrCls.isArray())
			{
				obj = convertToJavaArray(jsonArr.getJSONArray(i),arrCls.getComponentType(),annotations);
			}
			else
			{
				obj = convertToJavaObject(jsonArr.getJSONObject(i),arrCls);
			}
			if(obj != null)
			{
				list.add(obj);
			}
		}
		Object fdValue = Array.newInstance(arrCls,list.size());
		for(int i=0; i<list.size(); i++)
		{
			Array.set(fdValue,i,list.get(i));
		}
		return fdValue;
	}
}
