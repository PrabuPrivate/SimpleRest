package com.jaxrs.simplerest.scanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

public class MethodAnnotationScanner implements Scanner
{
	private HashSet<String> annotations;
	public MethodAnnotationScanner(Class<? extends Annotation>... annotation)
	{
		annotations = new HashSet<String>();
		for(int i=0;i<annotation.length;i++)
		{
			annotations.add(annotation[i].getName());
		}
		
	}

	@Override
	public void scan(String packageName, Object c) throws Exception 
	{
		ArrayList<Method>  list = (ArrayList<Method>) c;
		Scanner s = new ClassScanner();
		ArrayList<String> names = new ArrayList<String>();
		s.scan(packageName, names);
		for(String name : names)
		{
			Class<?> cls = Class.forName(name);
			Method[] methods = cls.getDeclaredMethods();
			for(Method m : methods)
			{
				Annotation[] annotations = m.getDeclaredAnnotations();
				for(Annotation a:annotations)
				{
					if(onAccept(a.annotationType().getName()))
					{
						list.add(m);
					}
				}
			}
		}
	}

	@Override
	public boolean onAccept(String name) {
		 return annotations.contains(name);
	}
}
