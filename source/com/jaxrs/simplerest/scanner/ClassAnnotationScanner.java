package com.jaxrs.simplerest.scanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;

public class ClassAnnotationScanner implements Scanner
{
	private HashSet<String> annotations;
	public ClassAnnotationScanner(Class<? extends Annotation>... annotation)
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
		ArrayList<Class>  list = (ArrayList<Class>) c;
		Scanner s = new ClassScanner();
		ArrayList<String> names = new ArrayList<String>();
		s.scan(packageName, names);
		for(String name : names)
		{
			Class<?> cls = Class.forName(name);
			Annotation[] annotations = cls.getAnnotations();
			for(Annotation a:annotations)
			{
				if(onAccept(a.annotationType().getName()))
				{
					System.out.println(a.toString());
					list.add(cls);
				}
			}
		}
	}

	@Override
	public boolean onAccept(String name) {
		 return annotations.contains(name);
	}
}
