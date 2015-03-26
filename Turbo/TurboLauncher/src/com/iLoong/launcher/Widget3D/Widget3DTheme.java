package com.iLoong.launcher.Widget3D;


import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.theme.ThemeManager;


public class Widget3DTheme
{
	
	private static String TAG = "Widget3DTheme";
	private static final String defaultThemeName = "iLoong";
	private String mWidgetThemeConfig = "theme/widget/config.xml";
	private String hostTheme = null;
	private String mWidgetThemeName = null;
	private String packageName = "";
	
	public void setWidgetThemeConfig(
			String widgetThemeConfig )
	{
		this.mWidgetThemeConfig = widgetThemeConfig;
	}
	
	public String getWidget3DThemeName(
			String packageName ,
			String hostTheme )
	{
		this.hostTheme = hostTheme;
		this.packageName = packageName;
		LoadDefaultLayoutXml();
		if( mWidgetThemeName == null || mWidgetThemeName.trim() == "" )
		{
			mWidgetThemeName = defaultThemeName;
		}
		return mWidgetThemeName;
	}
	
	private void LoadDefaultLayoutXml()
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			WidgetThemeHandler handler = new WidgetThemeHandler();
			xmlreader.setContentHandler( handler );
			InputStream in = ThemeManager.getInstance().getInputStream( mWidgetThemeConfig );
			InputSource xmlin = new InputSource( in );
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
	
	class WidgetThemeHandler extends DefaultHandler
	{
		
		String tmpPackageName = "";
		String tmpThemeName = "";
		String ThemeConfigNode = "";
		String WidgetNode = "";
		String ThemeNode = "";
		String HostThemeNode = "";
		
		public void startDocument() throws SAXException
		{
			Log.v( TAG , "startDocument" );
		}
		
		public void endDocument() throws SAXException
		{
			Log.v( TAG , "endDocument" );
			if( mWidgetThemeName != null )
			{
				Log.v( TAG , mWidgetThemeName );
			}
			else
			{
				Log.v( TAG , "find no theme,return default themeName:" + defaultThemeName );
			}
		}
		
		public void startElement(
				String namespaceURI ,
				String localName ,
				String qName ,
				Attributes atts ) throws SAXException
		{
			if( localName.equals( "widget" ) )
			{
				WidgetNode = localName;
				tmpPackageName = atts.getValue( "packageName" );
			}
			else if( localName.equals( "themeConfig" ) )
			{
				ThemeConfigNode = localName;
			}
			else if( localName.equals( "theme" ) )
			{
				ThemeNode = localName;
				if( ThemeConfigNode.equals( "themeConfig" ) && WidgetNode.equals( "widget" ) )
				{
					tmpThemeName = atts.getValue( "name" );
				}
			}
			else if( localName.equals( "hostTheme" ) )
			{
				HostThemeNode = localName;
				if( ThemeConfigNode.equals( "themeConfig" ) && WidgetNode.equals( "widget" ) && ThemeNode.equals( "theme" ) )
				{
					if( tmpPackageName.equals( packageName ) && hostTheme.equals( atts.getValue( "name" ) ) )
					{
						mWidgetThemeName = tmpThemeName;
					}
				}
			}
		}
		
		public void endElement(
				String namespaceURI ,
				String localName ,
				String qName ) throws SAXException
		{
			if( localName.equals( "" ) )
			{
				// this.curFolder = 0;
			}
		}
	}
}
