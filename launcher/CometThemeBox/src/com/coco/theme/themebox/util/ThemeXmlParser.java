package com.coco.theme.themebox.util;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.coco.theme.themebox.database.model.ThemeInfoItem;

import android.content.Context;


public class ThemeXmlParser
{
	
	Context mcontext;
	
	public ThemeXmlParser(
			Context context )
	{
		mcontext = context;
	}
	
	private List<ThemeInfoItem> mThemeList = new ArrayList<ThemeInfoItem>();
	private String[] pictureAddress = new String[]{};
	private String[] applicationAddress = new String[]{};
	private String version = "";
	private DocumentBuilderFactory factory = null;
	
	public List<ThemeInfoItem> getThemeList()
	{
		return mThemeList;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public String[] getPictureAddress()
	{
		return pictureAddress;
	}
	
	public String[] getApplicationAddress()
	{
		return applicationAddress;
	}
	
	private void reset()
	{
		mThemeList.clear();
		pictureAddress = new String[]{};
		applicationAddress = new String[]{};
	}
	
	public boolean parseList(
			String xmlPath )
	{
		reset();
		if( factory == null )
		{
			factory = DocumentBuilderFactory.newInstance();
		}
		InputStream xmlStream = null;
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
			xmlStream = new FileInputStream( xmlPath );
			Document doc = builder.parse( xmlStream );
			Element rootElement = doc.getDocumentElement();
			version = rootElement.getAttribute( "ver" );
			if( version == null )
			{
				return false;
			}
			{
				Element eleHead = getChildElementByTag( rootElement , "header" );
				if( eleHead == null )
				{
					return false;
				}
				String pic = getElementValue( eleHead , "pic" );
				if( pic == null )
				{
					return false;
				}
				pictureAddress = pic.split( "," );
				String app = getElementValue( eleHead , "app" );
				if( app == null )
				{
					return false;
				}
				applicationAddress = app.split( "," );
			}
			Element eleLabel = getChildElementByTag( rootElement , "label" );
			if( eleLabel == null )
			{
				return false;
			}
			List<Node> uiList = getChildNodeList( eleLabel , "ui" );
			for( int i = 0 ; i < uiList.size() ; i++ )
			{
				Node uiNode = uiList.get( i );
				if( !( uiNode instanceof Element ) )
				{
					return false;
				}
				Element uiEle = (Element)uiNode;
				ThemeInfoItem item = new ThemeInfoItem();
				item.setApplicationName( getElementValue( uiEle , "a" ) );
				item.setPackageName( getElementValue( uiEle , "b" ) );
				item.setApplicationSize( Integer.parseInt( getElementValue( uiEle , "c" ) ) );
				item.setAuthor( getElementValue( uiEle , "d" ) );
				item.setIntroduction( getElementValue( uiEle , "e" ) );
				item.setVersionCode( Integer.parseInt( getElementValue( uiEle , "f" ) ) );
				item.setVersionName( getElementValue( uiEle , "g" ) );
				mThemeList.add( item );
			}
			return true;
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
			return false;
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
		catch( SAXException e )
		{
			e.printStackTrace();
			return false;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if( xmlStream != null )
			{
				try
				{
					xmlStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getElementValue(
			Element parent ,
			String tagName )
	{
		Element ele = getChildElementByTag( parent , tagName );
		if( ele == null )
		{
			return "";
		}
		return ele.getTextContent();
	}
	
	private Element getChildElementByTag(
			Element parent ,
			String name )
	{
		List<Node> nodeList = getChildNodeList( parent , name );
		if( nodeList.size() <= 0 )
		{
			return null;
		}
		Node node = nodeList.get( 0 );
		if( node instanceof Element )
		{
			return (Element)node;
		}
		return null;
	}
	
	private List<Node> getChildNodeList(
			Element parent ,
			String name )
	{
		List<Node> ret = new ArrayList<Node>();
		NodeList childList = parent.getChildNodes();
		for( int i = 0 ; i < childList.getLength() ; i++ )
		{
			Node node = childList.item( i );
			if( node.getNodeName().equals( name ) )
			{
				ret.add( node );
			}
		}
		return ret;
	}
}
