package com.iLoong.launcher.desktop;


import java.io.File;
import java.io.FileInputStream;
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


public class FeatureConfig
{
	
	public static final String FEATURE_CONFIG_FILENAME = "theme/feature_config.xml";
	public static final String CUSTOM_FEATURE_CONFIG_FILENAME = "/system/launcher/coco_feature_config.xml";
	public static final String CUSTOM_FIRST_FEATURE_CONFIG_FILENAME = "/system/oem/launcher/coco_feature_config.xml";
	public static boolean enable_themebox = true;
	//xiatian add start	//New Requirement 20130507
	public static boolean enable_WallpaperBox = false;
	public static boolean enable_SceneBox = false;
	//xiatian add end
	public static String default_theme_package_name = null;
	public static boolean lite_edition = false;
	public static boolean use_new_theme = false;
	
	public FeatureConfig()
	{
		//LoadFeatureConfigXml();
	}
	
	class FeatureConfigHandler extends DefaultHandler
	{
		
		public static final String GENERAl_CONFIG = "general_config";
		
		public void startDocument() throws SAXException
		{
			// Utils3D.showPidMemoryInfo("startDocument");
		}
		
		public void endDocument() throws SAXException
		{
			// Utils3D.showPidMemoryInfo("endDocument");
		}
		
		public void startElement(
				String namespaceURI ,
				String localName ,
				String qName ,
				Attributes atts ) throws SAXException
		{
			if( localName.equals( GENERAl_CONFIG ) )
			{
				String temp;
				temp = atts.getValue( "enable_themebox" );
				if( temp != null )
				{
					enable_themebox = temp.equals( "true" );
				}
				//xiatian add start	//New Requirement 20130507
				temp = atts.getValue( "enable_WallpaperBox" );
				if( temp != null )
				{
					enable_WallpaperBox = temp.equals( "true" );
				}
				temp = atts.getValue( "enable_SceneBox" );
				if( temp != null )
				{
					enable_SceneBox = temp.equals( "true" );
				}
				//xiatian add end
				temp = atts.getValue( "default_theme_package_name" );
				if( temp == null )
				{
					default_theme_package_name = null;
				}
				else
				{
					if( temp.equals( "nothing" ) )
					{
						default_theme_package_name = null;
					}
					else
					{
						default_theme_package_name = temp;
					}
				}
				temp = atts.getValue( "use_new_theme" );
				if( temp != null )
				{
					use_new_theme = temp.equals( "true" );
				}
				temp = atts.getValue( "lite_edition" );
				if( temp != null )
				{
					lite_edition = temp.equals( "true" );
				}
			}
		}
		
		public void endElement(
				String namespaceURI ,
				String localName ,
				String qName ) throws SAXException
		{
		}
	}
	
	private void LoadFeatureConfigXml()
	{
		InputStream in = null;
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			FeatureConfigHandler handler = new FeatureConfigHandler();
			xmlreader.setContentHandler( handler );
			InputSource xmlin = null;
			File f = new File( CUSTOM_FIRST_FEATURE_CONFIG_FILENAME );
			if( !f.exists() )
			{
				f = new File( CUSTOM_FEATURE_CONFIG_FILENAME );
			}
			if( f.exists() )
				in = new FileInputStream( f.getAbsolutePath() );
			else
				in = iLoongApplication.getInstance().getAssets().open( FEATURE_CONFIG_FILENAME );
			if( in == null )
			{
				return;
			}
			xmlin = new InputSource( in );
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
}
