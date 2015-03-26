package com.iLoong.Widget3D.Layout;


import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.DisplayMetrics;

import com.iLoong.Widget3D.BaseView.PluginViewGroup3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class ViewInflater
{
	
	private MainAppContext appContext;
	private String viewLayoutFile = "layout/layout.xml";
	
	public ViewInflater(
			MainAppContext appContext )
	{
		this.appContext = appContext;
	}
	
	public PluginViewGroup3D inflaterWidget()
	{
		LayoutHandler viewHandler = new LayoutHandler( appContext );
		LoadXml( viewLayoutFile , viewHandler );
		PluginViewGroup3D viewRoot = viewHandler.getViewRoot();
		viewRoot.build();
		return viewRoot;
	}
	
	public void LoadXml(
			String fileName ,
			DefaultHandler handler )
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler( handler );
			InputStream inputStream = getInputStream( false , fileName );
			InputSource xmlin = null;
			xmlin = new InputSource( inputStream );
			xmlreader.parse( xmlin );
			if( inputStream != null )
			{
				inputStream.close();
			}
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
	}
	
	public String getAutoAdaptDir(
			Context context ,
			String prefix )
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		if( metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH )
		{
			prefix = prefix + "-xhdpi";
		}
		else if( metrics.densityDpi == DisplayMetrics.DENSITY_HIGH )
		{
			prefix = prefix + "-hdpi";
		}
		else if( metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM )
		{
			prefix = prefix + "-mdpi";
		}
		else if( metrics.densityDpi == DisplayMetrics.DENSITY_LOW )
		{
			prefix = prefix + "-ldpi";
		}
		return prefix;
	}
	
	public InputStream getInputStream(
			boolean autoAdapt ,
			String fileName )
	{
		InputStream instr = null;
		if( autoAdapt )
		{
			String filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
			if( !filePrefix.contains( "-" ) )
			{
				filePrefix = filePrefix + "-" + appContext.mWidgetContext.getResources().getDisplayMetrics().heightPixels + "x" + appContext.mWidgetContext.getResources().getDisplayMetrics().widthPixels;
			}
			// 查找精确分辨率如960*540
			try
			{
				String tempFileName = filePrefix + fileName.substring( fileName.indexOf( "/" ) );
				instr = appContext.mWidgetContext.getAssets().open( tempFileName );
			}
			catch( IOException e )
			{
				instr = null;
			}
			String dpiFilePrefix = this.getAutoAdaptDir( appContext.mWidgetContext , filePrefix.substring( 0 , filePrefix.indexOf( "-" ) ) );
			if( instr == null )
			{
				String dpiFileName = dpiFilePrefix + fileName.substring( fileName.indexOf( "/" ) );
				try
				{
					instr = appContext.mWidgetContext.getAssets().open( dpiFileName );
				}
				catch( IOException e )
				{
				}
			}
			// 在不带dpi的目录下寻找资源，目前系统资源统一放在不带dpi的目录，所以首先寻找不带dpi的目录
			if( instr == null )
			{
				filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
				if( !filePrefix.equals( dpiFilePrefix ) )
				{
					try
					{
						instr = appContext.mWidgetContext.getAssets().open( fileName );
					}
					catch( IOException e )
					{
					}
				}
			}
		}
		else
		{
			if( instr == null )
			{
				try
				{
					instr = appContext.mWidgetContext.getAssets().open( fileName );
				}
				catch( IOException e )
				{
				}
			}
		}
		return instr;
	}
}
