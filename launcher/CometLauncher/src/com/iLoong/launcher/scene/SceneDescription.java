// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.scene;


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
import android.text.Html;
import android.util.DisplayMetrics;
import com.iLoong.launcher.Desktop3D.Log;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;


public class SceneDescription
{
	
	public static final String HOME_DIR = "theme/";
	public static final String PREVIEW_DIR = "theme/preview/";
	private static final String PREVIEW_FILENAME = PREVIEW_DIR + "preview.xml";
	private static final String CONFIG_FILENAME = HOME_DIR + "config.xml";
	private static final String ICON_FILENAME = HOME_DIR + "icon/icon.xml";
	private static final String TAG_WIDGET_THEME = "widget_theme";
	private static final String TAG_THEME = "themepreview";
	private static final String TAG_VERSION = "version";
	private static final String TAG_INFO = "info";
	private static final String TAG_ITEM = "item";
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
	public ArrayList<String> themeimgs = new ArrayList<String>();
	private HashMap<String , Integer> mInteger = new HashMap<String , Integer>();
	private HashMap<String , String> mStrings = new HashMap<String , String>();
	private HashMap<String , String> mIcons = new HashMap<String , String>();
	private Context mContext;
	private AndroidFiles mGdxContext;
	
	public SceneDescription(
			Context context )
	{
		mContext = context;
		mGdxContext = new AndroidFiles( mContext.getAssets() );
		PreViewHandler handler = new PreViewHandler();
		LoadXml( PREVIEW_FILENAME , handler );
		ConfigHandler cfghandler = new ConfigHandler();
		LoadXml( CONFIG_FILENAME , cfghandler );
		IconHandler iconhandler = new IconHandler();
		LoadXml( ICON_FILENAME , iconhandler );
	}
	
	public SceneDescription(
			Context context ,
			String defaultFileName )
	{
		mContext = context;
		mGdxContext = new AndroidFiles( mContext.getAssets() );
		// PreViewHandler handler = new PreViewHandler();
		// LoadXml(PREVIEW_FILENAME, handler);
		ConfigHandler cfghandler = new ConfigHandler();
		LoadXml( defaultFileName , cfghandler );
		// IconHandler iconhandler = new IconHandler();
		// LoadXml(ICON_FILENAME, iconhandler);
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
	
	public AndroidFiles getGdxContext()
	{
		return mGdxContext;
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
	
	public Bitmap getIcon(
			String icon )
	{
		Bitmap iconbmp = null;
		String path = new String();
		path = "theme/icon/";
		if( SetupMenu.mScreenScale == 2.0 )
			path += "100";
		else if( SetupMenu.mScreenScale == 1.5 )
			path += "80";
		else
			path += "60";
		path += icon;
		try
		{
			iconbmp = Tools.getImageFromInStream( mContext.getAssets().open( path ) );
		}
		catch( IOException e )
		{
		}
		return iconbmp;
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
				filePrefix = filePrefix + "-" + mContext.getResources().getDisplayMetrics().heightPixels + "x" + mContext.getResources().getDisplayMetrics().widthPixels;
			}
			// 查找精确分辨率如960*540
			try
			{
				String tempFileName = filePrefix + fileName.substring( fileName.indexOf( "/" ) );
				instr = mContext.getAssets().open( tempFileName );
			}
			catch( IOException e )
			{
				instr = null;
			}
			String dpiFilePrefix = this.getAutoAdaptDir( mContext , filePrefix.substring( 0 , filePrefix.indexOf( "-" ) ) );
			if( instr == null )
			{
				String dpiFileName = dpiFilePrefix + fileName.substring( fileName.indexOf( "/" ) );
				try
				{
					instr = mContext.getAssets().open( dpiFileName );
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
						instr = mContext.getAssets().open( fileName );
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
					instr = mContext.getAssets().open( fileName );
				}
				catch( IOException e )
				{
				}
			}
		}
		return instr;
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
	
	public void getInfo(
			TextView text )
	{
		String info = new String();
		Context context = SceneManager.getInstance().getSystemContext();
		info += context.getString( RR.string.themetype ) + "\n" + themetype;
		info += "\n\n";
		info += context.getString( RR.string.themeversion ) + "\n" + themeversion;
		info += "\n\n";
		info += context.getString( RR.string.themeauthor ) + "\n" + themeauthor;
		info += "\n\n";
		info += context.getString( RR.string.themedata ) + "\n" + themedata;
		info += "\n\n";
		info += context.getString( RR.string.themefeedback ) + "\n";
		text.append( info );
		String link = new String();
		link = "<a href=\"" + themefeedback + "\">" + themefeedback + "</a>";
		text.append( Html.fromHtml( link ) );
	}
	
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
	
	class ConfigHandler extends DefaultHandler
	{
		
		public ConfigHandler()
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
			if( localName.equals( "resources" ) )
			{
			}
			else if( localName.equals( "interge" ) )
			{
				if( atts.getValue( "type" ).equals( "dip" ) )
				{
					mInteger.put( atts.getValue( "name" ) , Tools.dip2px( mContext , Integer.valueOf( atts.getValue( "value" ) ) ) );
				}
				else
				{
					mInteger.put( atts.getValue( "name" ) , Integer.valueOf( atts.getValue( "value" ) ) );
				}
			}
			else if( localName.equals( "string" ) )
			{
				mStrings.put( atts.getValue( "name" ) , atts.getValue( "value" ) );
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
	
	class IconHandler extends DefaultHandler
	{
		
		public IconHandler()
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
			if( localName.equals( "resources" ) )
			{
			}
			else if( localName.equals( "item" ) )
			{
				mIcons.put( atts.getValue( "component" ) , atts.getValue( "image" ) );
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
