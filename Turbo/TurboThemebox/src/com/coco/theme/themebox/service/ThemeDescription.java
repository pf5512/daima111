package com.coco.theme.themebox.service;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;

import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;


public class ThemeDescription
{
	
	public static final String HOME_DIR = "theme/";
	public static final String PREVIEW_DIR = "theme/preview/";
	private static final String PREVIEW_FILENAME = PREVIEW_DIR + "preview.xml";
	private static final String TAG_WIDGET_THEME = "widget_theme";
	private static final String TAG_THEME = "themepreview";
	private static final String TAG_VERSION = "version";
	private static final String TAG_INFO = "info";
	private static final String TAG_ITEM = "item";
	private static final String TAG_THUMB = "thumb";
	private static final String TAG_INTRODUCTION = "introduction";
	public ComponentName componentName;
	public CharSequence title;
	public boolean mUse = false;
	public boolean mSystem = false;
	public boolean mBuiltIn = false;
	public String themeversion;
	public String themedata;
	public String themeauthor;
	public String themename;
	public String themetype;
	public String themefeedback;
	public String widgettheme;
	public String thumbimg;// 缩略图
	public String introduction; // 简介
	public ArrayList<String> themeimgs = new ArrayList<String>();
	private HashMap<String , Integer> mInteger = new HashMap<String , Integer>();
	private HashMap<String , String> mStrings = new HashMap<String , String>();
	private HashMap<String , String> mIcons = new HashMap<String , String>();
	private Context mContext;
	
	public ThemeDescription(
			Context context )
	{
		mContext = context;
		PreViewHandler handler = new PreViewHandler();
		LoadXml( PREVIEW_FILENAME , handler );
	}
	
	public void destroy()
	{
		mContext = null;
		componentName = null;
	}
	
	public Context getContext()
	{
		return mContext;
	}
	
	public Bitmap getDefaultBitmap()
	{
		Bitmap defaultbitmap = null;
		try
		{
			if( themeimgs.size() > 0 )
				defaultbitmap = Tools.getImageFromInStream( mContext.getAssets().open( PREVIEW_DIR + themeimgs.get( 0 ) ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return defaultbitmap;
	}
	
	public Bitmap getBitmap(
			String filename )
	{
		Bitmap bmp = null;
		try
		{
			bmp = Tools.getImageFromInStream( mContext.getAssets().open( filename ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return bmp;
	}
	
	public InputStream getStream(
			String filename )
	{
		InputStream stream = null;
		try
		{
			stream = mContext.getAssets().open( getFileForDpi( filename ) );
		}
		catch( IOException e )
		{
			try
			{
				stream = mContext.getAssets().open( "theme/" + filename );
			}
			catch( IOException e1 )
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return stream;
	}
	
	public String getFileForDpi(
			String filename )
	{
		float mScreenScale = mContext.getResources().getDisplayMetrics().density;
		if( mScreenScale <= 0.75f )
		{
			Log.d( "launcher" , "dpi=0.75" );
			filename = "theme-ldpi/" + filename;
		}
		else if( mScreenScale <= 1f )
		{
			Log.d( "launcher" , "dpi=1" );
			filename = "theme-mdpi/" + filename;
		}
		else if( mScreenScale <= 1.5f )
		{
			Log.d( "launcher" , "dpi=1.5" );
			filename = "theme-hdpi/" + filename;
		}
		else
			filename = "theme-xhdpi/" + filename;
		return filename;
	}
	
	public ArrayList<String> getBitmaps()
	{
		return themeimgs;
	}
	
	public Set<String> getIcons()
	{
		return mIcons.keySet();
	}
	
	public void LoadXml(
			String Filename ,
			DefaultHandler handler )
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			xmlreader.setContentHandler( handler );
			InputSource xmlin = new InputSource( mContext.getAssets().open( Filename ) );
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
	}
	
	/*
	 * private void getInfo(TextView text) { String info = new String(); Context
	 * context = ThemeManager.getInstance().getSystemContext();
	 * 
	 * info += context.getString(R.string.themetype) + "\n" + themetype; info +=
	 * "\n\n"; info += context.getString(R.string.themeversion) + "\n" +
	 * themeversion; info += "\n\n"; info +=
	 * context.getString(R.string.themeauthor) + "\n" + themeauthor; info +=
	 * "\n\n"; info += context.getString(R.string.themedata) + "\n" + themedata;
	 * info += "\n\n"; info += context.getString(R.string.themefeedback) + "\n";
	 * text.append(info); String link = new String(); link = "<a href=\"" +
	 * themefeedback + "\">" + themefeedback + "</a>";
	 * text.append(Html.fromHtml(link)); }
	 */
	public int getInteger(
			String key )
	{
		int result = -1;
		Integer value = mInteger.get( key );
		if( value != null )
		{
			result = value.intValue();
		}
		return result;
	}
	
	public String getString(
			String key )
	{
		String value = mStrings.get( key );
		if( value == null )
		{
			return null;
		}
		return value;
	}
	
	class PreViewHandler extends DefaultHandler
	{
		
		public PreViewHandler()
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
			else if( localName.equals( TAG_VERSION ) )
			{
				themeversion = atts.getValue( "value" );
			}
			else if( localName.equals( TAG_INFO ) )
			{
				themedata = atts.getValue( "date" );
				themeauthor = atts.getValue( "author" );
				themename = atts.getValue( "name" );
				themetype = atts.getValue( "type" );
				themefeedback = atts.getValue( "feedback" );
			}
			else if( localName.equals( TAG_ITEM ) )
			{
				themeimgs.add( atts.getValue( "image" ) );
			}
			else if( localName.equals( TAG_WIDGET_THEME ) )
			{
				widgettheme = atts.getValue( "theme" );
			}
			else if( localName.equals( TAG_THUMB ) )
			{
				thumbimg = atts.getValue( "path" );
				Log.v( "***********" , "thumbimg = " + thumbimg );
			}
			else if( localName.equals( TAG_INTRODUCTION ) )
			{
				introduction = qName;
				Log.v( "***********" , introduction );
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
