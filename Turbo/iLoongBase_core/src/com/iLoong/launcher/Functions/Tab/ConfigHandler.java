package com.iLoong.launcher.Functions.Tab;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.iLoong.launcher.Desktop3D.Log;

import android.os.Environment;


public class ConfigHandler extends DefaultHandler
{
	
	private String internalConfigFile = "theme/plugin/tab/config.xml";
	private String externalConfigFile = "/cooee/launcher/plugin/tab/config.xml";
	StringBuilder builder = null;
	private TabContext mTabContext;
	private ArrayList<TabPluginMetaData> mPluginList = new ArrayList<TabPluginMetaData>();
	
	public ArrayList<TabPluginMetaData> getTabPluginMetaDataList()
	{
		return mPluginList;
	}
	
	public ConfigHandler(
			TabContext tabContext )
	{
		this.mTabContext = tabContext;
		if( mPluginList == null )
		{
			mPluginList = new ArrayList<TabPluginMetaData>();
		}
		if( mPluginList.size() > 0 )
		{
			mPluginList.clear();
		}
		if( tabContext.paramsMap.containsKey( "internalConfig" ) )
		{
			internalConfigFile = (String)tabContext.paramsMap.get( "internalConfig" );
		}
		if( tabContext.paramsMap.containsKey( "externalConfig" ) )
		{
			externalConfigFile = (String)tabContext.paramsMap.get( "externalConfig" );
		}
	}
	
	public void LoadXml(
			String tabListFile )
	{
		InputStream is = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// 加载自身配置文件
			SAXParser parser = null;
			XMLReader xmlreader = null;
			InputSource xmlin = null;
			// 加载config_base文件
			boolean sdCardExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED );
			File file = null;
			if( sdCardExist )
			{
				file = new File( tabListFile );
			}
			if( file == null || ( !file.exists() ) )
			{
				file = new File( mTabContext.mContainerContext.getFilesDir().getAbsolutePath() + "/cooee/launcher/plugin/tab/remotePluginList.xml" );
			}
			if( file.exists() )
			{
				is = new FileInputStream( file.getAbsolutePath() );
				xmlin = new InputSource( is );
				parser = factory.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler( this );
				xmlreader.parse( xmlin );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( is != null )
			{
				try
				{
					is.close();
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void LoadDefaultLayoutXml()
	{
		InputStream is = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// 加载自身配置文件
			SAXParser parser = null;
			XMLReader xmlreader = null;
			InputSource xmlin = null;
			// 加载config_base文件
			is = ThemeHelper.getInputStream( mTabContext.mContainerContext , true , internalConfigFile );
			if( is != null )
			{
				xmlin = new InputSource( is );
				parser = factory.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler( this );
				xmlreader.parse( xmlin );
				is.close();
				is = null;
			}
			File file = new File( externalConfigFile );
			if( file.exists() )
			{
				is = new FileInputStream( file.getAbsolutePath() );
				xmlin = new InputSource( is );
				parser = factory.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler( this );
				xmlreader.parse( xmlin );
				is.close();
				is = null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( is != null )
			{
				try
				{
					is.close();
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void endDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		super.endDocument();
	}
	
	Plugin plugin = null;
	TabPluginMetaData metaData;
	
	@Override
	public void endElement(
			String uri ,
			String localName ,
			String qName ) throws SAXException
	{
		// TODO Auto-generated method stub
		if( localName.equals( "plugin" ) )
		{
			this.mPluginList.add( metaData );
		}
		else if( localName.equals( "pluginid" ) )
		{
			metaData.pluginId = builder.toString();
		}
		else if( localName.equals( "enname" ) )
		{
			metaData.enName = builder.toString().trim();
		}
		else if( localName.equals( "cnname" ) )
		{
			metaData.cnName = builder.toString().trim();
		}
		else if( localName.equals( "twname" ) )
		{
			metaData.twName = builder.toString().trim();
		}
		else if( localName.equals( "classname" ) )
		{
			metaData.className = builder.toString().trim();
		}
		else if( localName.equals( "packagename" ) )
		{
			metaData.packageName = builder.toString().trim();
		}
		else if( localName.equals( "url" ) )
		{
			metaData.url = builder.toString().trim();
		}
		else if( localName.equals( "order" ) )
		{
			metaData.order = Integer.valueOf( builder.toString().trim() );
		}
		else if( localName.equals( "show" ) )
		{
			metaData.show = builder.toString().trim().equals( "1" );
		}
	}
	
	@Override
	public void startDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		super.startDocument();
		builder = new StringBuilder();
	}
	
	@Override
	public void characters(
			char[] ch ,
			int start ,
			int length ) throws SAXException
	{
		// TODO Auto-generated method stub
		super.characters( ch , start , length );
		builder.append( ch , start , length ); // 将读取的字符数组追加到builder中
	}
	
	@Override
	public void startElement(
			String uri ,
			String localName ,
			String qName ,
			Attributes atts ) throws SAXException
	{
		// TODO Auto-generated method stub
		builder.setLength( 0 );
		if( localName.equals( "plugin" ) )
		{
			metaData = new TabPluginMetaData();
		}
	}
}
