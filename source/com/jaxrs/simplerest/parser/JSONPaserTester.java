package com.jaxrs.simplerest.parser;

import com.jaxrs.simplerest.testbeans.MainClass1;

public class JSONPaserTester 
{
	public static void main(String... args)throws Exception
	{
		String[] type = { "{\"type\":\"sub1\",\"name\":\"prabu\",\"age\":27,\"gender\":\"male\",\"ids\":[\"2345\",\"4536789\",\"07654345678\",\"345678\"]}",
		"{\"type\":\"sub\",\"name\":\"prabu\",\"age\":26,\"gender\":\"female\"}",
		"{\"type\":\"sub2\",\"name\":\"prabu\",\"age\":28,\"gender\":\"female\"}",
		"{\"name\":\"prabu\",\"age\":29,\"gender\":\"female\",\"marks\":[{\"mark\":45},{\"mark\":78},{\"mark\":90}]}"};
		for(String s : type)
		{
			System.out.println(BeanParser.parse(JSONParser.parse(s, null, MainClass1.class, 0)));
		}
	}
	public static void test(String s) throws Exception
	{
		System.out.println(BeanParser.parse(JSONParser.parse(s, null, MainClass1.class, 0)));
	}
}
