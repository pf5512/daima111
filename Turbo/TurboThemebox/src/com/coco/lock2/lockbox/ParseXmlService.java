package com.coco.lock2.lockbox;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class ParseXmlService
{
	
	public HashMap<String , String> parseXml(
			InputStream inStream )
	{
		HashMap<String , String> hashMap = new HashMap<String , String>();
		try
		{
			// 实例化一个文档构建器工厂
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// 通过文档构建器工厂获取一个文档构建器
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 通过文档通过文档构建器构建一个文档实例
			Document document = builder.parse( inStream );
			// 获取XML文件根节点
			Element root = document.getDocumentElement();
			// 获得所有子节点
			NodeList childNodes = root.getChildNodes();
			for( int j = 0 ; j < childNodes.getLength() ; j++ )
			{
				// 遍历子节点
				Node childNode = (Node)childNodes.item( j );
				if( childNode.getNodeType() == Node.ELEMENT_NODE )
				{
					Element childElement = (Element)childNode;
					String nodeName = childElement.getNodeName();
					String nodeValue = childElement.getFirstChild().getNodeValue();
					hashMap.put( nodeName , nodeValue );
				}
			}
		}
		catch( ParserConfigurationException ex )
		{
			ex.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return hashMap;
	}
}
