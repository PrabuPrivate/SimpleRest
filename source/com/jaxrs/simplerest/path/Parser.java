package com.jaxrs.simplerest.path;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaxrs.simplerest.parser.ParserUtill;
import com.jaxrs.simplerest.path.URLConstant.ElementName;
import com.jaxrs.simplerest.path.URLConstant.URLAttribute;

public final class Parser 
{
	private Parser(){}
	public static void init(String files[]) throws Exception
	{
		if(files != null)
		{
			for(int i=0;i < files.length;i++)
			{
				File xmlFile = new File(files[i]);
				Document doc = ParserUtill.getDocumentBuilder().parse(xmlFile);
				loadPathRule(doc);
			}
		}
	}
	
	private static void loadPathRule(Document doc)
	{
		if(doc.hasChildNodes())
		{
			parseNode(doc.getChildNodes());
		}
	}
	private static void parseNode(NodeList nodes)
	{
		for(int i=0;i<nodes.getLength();i++)
		{
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				if(node.getNodeName().equals(ElementName.URL.type))
				{
					Element e = (Element) node;
					PathRule rule = new PathRule(PathUtil.getURLAttributeValue(e, URLAttribute.HTTP_METHOD),
								"true".equals(PathUtil.getURLAttributeValue(e, URLAttribute.PATH_REGEX)),
								PathUtil.getURLAttributeValue(e, URLAttribute.PATH),
								PathUtil.getURLAttributeValue(e, URLAttribute.SERVICE_CLASS),
								PathUtil.getURLAttributeValue(e, URLAttribute.SERVICE_METHOD));
					PathContainer.addPathRule(rule);
				}
				if(node.hasChildNodes())
					parseNode(node.getChildNodes());
			}
		}
	}
	public static void main(String args[])throws Exception
	{
		String files[] = {"PathClassMapper.xml"};
		Parser.init(files);
		PathRule r = PathContainer.getRule("/newui2", "put");
		if(r != null)
		{
			System.out.println(r);
		}
		else
		{
			System.out.println("********");
		}
	}
}
