package com.iLoong.theme.adapter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ContentConfig
{
	
	private static final String LOG_TAG = "ContentConfig";
	private DocumentBuilderFactory factory = null;
	private String thumbPath = "";
	private String previewPath = "";
	private String[] previewFiles = new String[]{};
	private String settingClassName = "";
	private String packageName = "";
	private String applicationName = "";
	private int versionCode = 0;
	private String versionName = "";
	private long applicationSize = 0;
	private String author = "";
	private String introduction = "";
	private String updateTime = "";
	private String applicationName_en = "";
	private String introduction_en = "";
	private String wrapName = "";
	
	public boolean isSettingExist()
	{
		return !settingClassName.equals( "" );
	}
	
	public String getSettingClassName()
	{
		return settingClassName;
	}
	
	public int getVersionCode()
	{
		return versionCode;
	}
	
	public String getVersionName()
	{
		return versionName;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public String getApplicationName()
	{
		return applicationName;
	}
	
	public String getApplicationName_en()
	{
		return applicationName_en;
	}
	
	public long getApplicationSize()
	{
		return applicationSize;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public String getIntroduction()
	{
		return introduction;
	}
	
	public String getIntroduction_en()
	{
		return introduction_en;
	}
	
	public String getUpdateTime()
	{
		return updateTime;
	}
	
	public String getWrapName()
	{
		return wrapName;
	}
	
	public boolean saveThumb(
			Context remoteContext ,
			String filePath )
	{
		InputStream thumbStream = null;
		FileOutputStream fileOut = null;
		byte[] buffer = new byte[10 * 1024];
		try
		{
			fileOut = new FileOutputStream( filePath );
			thumbStream = remoteContext.getAssets().open( thumbPath );
			int readSize = thumbStream.read( buffer );
			while( readSize > 0 )
			{
				fileOut.write( buffer , 0 , readSize );
				readSize = thumbStream.read( buffer );
			}
			return true;
		}
		catch( FileNotFoundException e )
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
			if( thumbStream != null )
			{
				try
				{
					thumbStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			if( fileOut != null )
			{
				try
				{
					fileOut.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public Bitmap loadThumbImage(
			Context remoteContext )
	{
		Bitmap result = null;
		try
		{
			result = BitmapFactory.decodeStream( remoteContext.getAssets().open( thumbPath ) );
		}
		catch( FileNotFoundException e )
		{
			result = null;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public Bitmap loadPreviewImage(
			Context remoteContext ,
			int index )
	{
		if( index < 0 || index >= previewFiles.length )
		{
			return null;
		}
		Bitmap result = null;
		try
		{
			result = BitmapFactory.decodeStream( remoteContext.getAssets().open( previewPath + "/" + previewFiles[index] ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			result = null;
		}
		return result;
	}
	
	public int getPreviewArrayLength()
	{
		return previewFiles.length;
	}
	
	public void reset()
	{
		thumbPath = "";
		previewPath = "";
		previewFiles = new String[]{};
		settingClassName = "";
		versionCode = 0;
		versionName = "";
		packageName = "";
		applicationName = "";
		applicationSize = 0;
		author = "";
		introduction = "";
		updateTime = "";
		applicationName_en = "";
		introduction_en = "";
	}
	
	public void loadContentWrap(
			Context remoteContext ,
			String className )
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			ConfigHandler handler = new ConfigHandler();
			xmlreader.setContentHandler( handler );
			InputSource xmlin;
			InputStream in = remoteContext.getAssets().open( "config.xml" );
			if( in == null )
			{
				return;
			}
			else
			{
				xmlin = new InputSource( in );
			}
			xmlreader.parse( xmlin );
			handler = null;
			xmlin = null;
			in.close();
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	class ConfigHandler extends DefaultHandler
	{
		
		@Override
		public void startElement(
				String namespaceURI ,
				String localName ,
				String qName ,
				Attributes atts ) throws SAXException
		{
			if( localName.equals( "viewWrap" ) )
			{
				String temp = atts.getValue( "className" );
				if( temp == null || temp.equals( "" ) )
				{
					return;
				}
				wrapName = temp;
				System.out.println( "wrapName = " + wrapName );
				return;
			}
		}
		
		@Override
		public void endDocument() throws SAXException
		{
			// TODO Auto-generated method stub
			super.endDocument();
		}
	}
	
	public boolean loadConfig(
			Context remoteContext ,
			String className )
	{
		reset();
		if( factory == null )
		{
			factory = DocumentBuilderFactory.newInstance();
		}
		InputStream configStream = null;
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			configStream = remoteContext.getAssets().open( "config.xml" );
			Document doc = builder.parse( configStream );
			Element rootElement = doc.getDocumentElement();
			Element parentElement = rootElement;
			List<Node> appsList = getChildNodeList( parentElement , "app" );
			for( int i = 0 ; i < appsList.size() ; i++ )
			{
				Node appNode = appsList.get( i );
				if( appNode instanceof Element )
				{
					Element ele = (Element)appNode;
					if( className.equals( ele.getAttribute( "className" ) ) )
					{
						parentElement = ele;
						break;
					}
				}
			}
			thumbPath = getAttributeValue( parentElement , "thumb" , "path" );
			if( thumbPath.equals( "" ) )
			{
				return false;
			}
			previewPath = getAttributeValue( parentElement , "preview" , "path" );
			if( previewPath.equals( "" ) )
			{
				return false;
			}
			try
			{
				previewFiles = remoteContext.getAssets().list( previewPath );
			}
			catch( IOException e1 )
			{
				e1.printStackTrace();
				return false;
			}
			settingClassName = getAttributeValue( parentElement , "setting" , "className" );
			wrapName = getAttributeValue( parentElement , "viewWrap" , "className" );
			author = getAttributeValue( parentElement , "author" , "name" );
			updateTime = getAttributeValue( parentElement , "updatetime" , "value" );
			introduction = getElementValue( parentElement , "introduction" );
			introduction_en = getElementValue( parentElement , "introductionEn" );
			applicationName_en = remoteContext.getPackageName();
			packageName = remoteContext.getPackageName();
			PackageInfo pkgInfo = remoteContext.getPackageManager().getPackageInfo( packageName , 0 );
			versionCode = pkgInfo.versionCode;
			versionName = pkgInfo.versionName;
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			applicationName = remoteContext.getPackageManager().getApplicationLabel( appInfo ).toString();
			applicationSize = new File( appInfo.publicSourceDir ).length();
			return true;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( configStream != null )
			{
				try
				{
					configStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private String getAttributeValue(
			Element parent ,
			String tagName ,
			String attName )
	{
		Element ele = getChildElementByTag( parent , tagName );
		if( ele == null )
		{
			return "";
		}
		attName = ele.getAttribute( attName );
		return attName;
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
