package com.jaxrs.simplerest.parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class ParserUtill 
{
	private static ThreadLocal<DocumentBuilder> threadLocalDocumentBuilder = new ThreadLocal<DocumentBuilder>();
	
	public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException
	{
		DocumentBuilder documentBuilder = threadLocalDocumentBuilder.get();
		if(documentBuilder == null)
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			documentBuilder = dbFactory.newDocumentBuilder();
			threadLocalDocumentBuilder.set(documentBuilder);
		}
		return documentBuilder;
	}
}
