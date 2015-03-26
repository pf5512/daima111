package com.iLoong.launcher.Desktop3D;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.theme.ThemeManager;


public class ApkConfig
{
	
	private static final String TAG_THEME = "apkconfig";
	private static final String TAG_ITEM = "item";
	private static final String APKCONFIG_FILENAME = "theme/apkconfig.xml";
	private static final String APKCONFIG_PATH = "apk/";
	// private Context mContext;
	private ArrayList<String> configApkList = new ArrayList<String>();
	
	public ApkConfig()
	{
		ApkConfigHandler apkConfighandler = new ApkConfigHandler();
		LoadXml( APKCONFIG_FILENAME , apkConfighandler );
	}
	
	public boolean apkConfigRight()
	{
		if( iLoongApplication.BuiltIn == false )
		{
			return true;
		}
		if( configApkList.size() == 0 )
		{
			return true;
		}
		for( int i = 0 ; i < configApkList.size() ; i++ )
		{
			String apkname = configApkList.get( i );
			apkname = APKCONFIG_PATH + apkname;
			try
			{
				InputStream inputStream = ThemeManager.getInstance().getInputStream( apkname );
				if( inputStream != null )
				{
					inputStream.close();
				}
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public void LoadXml(
			String Filename ,
			DefaultHandler handler )
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		InputStream in = null;
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler( handler );
			in = ThemeManager.getInstance().getInputStream( Filename );
			InputSource xmlin = new InputSource( in );
			xmlreader.parse( xmlin );
			handler = null;
			xmlin = null;
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
		finally
		{
			try
			{
				if( in != null )
				{
					in.close();
				}
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class ApkConfigHandler extends DefaultHandler
	{
		
		public ApkConfigHandler()
		{
		}
		
		public void startDocument() throws SAXException
		{
		}
		
		public void endDocument() throws SAXException
		{
		}
		
		public void startElement(
				String namespaceURI ,
				String localName ,
				String qName ,
				Attributes atts ) throws SAXException
		{
			if( localName.equals( TAG_THEME ) )
			{
			}
			else if( localName.equals( TAG_ITEM ) )
			{
				configApkList.add( atts.getValue( "apkname" ) );
			}
		}
		
		public void endElement(
				String namespaceURI ,
				String localName ,
				String qName ) throws SAXException
		{
		}
		
		public void characters(
				char ch[] ,
				int start ,
				int length )
		{
		}
	}
}
