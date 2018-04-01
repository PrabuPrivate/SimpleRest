package com.jaxrs.simplerest.scanner;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.jaxrs.simplerest.annotation.ClassIdentifier;
import com.jaxrs.simplerest.annotation.Path;

public class ScannerTester 
{
	public static void ClassAnnotationScanner()throws Exception
	{
		try 
		{
			Scanner c = new ClassAnnotationScanner(ClassIdentifier.class);
			ArrayList<Class> methods = new ArrayList<Class>();
			String packageName = "com.jaxrs.simplerest";
			c.scan(packageName,methods);
			System.out.println(methods);
		} 
		catch (Exception e) { e.printStackTrace();} 
	}
	public static void MethodAnnotationScanner()throws Exception
	{
		try 
		{
			Scanner c = new MethodAnnotationScanner(Path.class);
			ArrayList<Method> methods = new ArrayList<Method>();
			String packageName = "com.jaxrs.simplerest.service";
			c.scan(packageName,methods);
			System.out.println(methods);
		} 
		catch (Exception e) { e.printStackTrace();} 
	}
	public static void ClassScanner()throws Exception
	{
		try 
		{
			Scanner c = new ClassScanner();
			ArrayList<String> names = new ArrayList<String>();
			String packageName = "com.jaxrs.simplerest";
			c.scan(packageName,names);
			System.out.println("Class Name list\n"+names);
		} 
		catch (Exception e) { e.printStackTrace();} 
	}
	public static void main(String...strings)throws Exception
	{
		ClassAnnotationScanner();
	}
}
