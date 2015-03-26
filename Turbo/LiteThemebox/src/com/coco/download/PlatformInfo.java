package com.coco.download;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
import android.content.pm.PackageManager;

import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;


public class PlatformInfo
{
	
	private static final String LOG_TAG = "PlatformInfo";
	public static final int MODE_DEFAULT = 0;
	public static final int MODE_VIEW = 1;
	private static PlatformInfo mInstance = null;
	public static String lockV = null;
	
	public static PlatformInfo getInstance(
			Context context )
	{
		if( mInstance == null )
		{
			mInstance = new PlatformInfo();
			mInstance.init( context );
			mInstance.checkState( context );
		}
		return mInstance;
	}
	
	private int versionCode = 0;
	private int mode = 0;
	private String channel = "";
	
	public int getVersionCode()
	{
		return versionCode;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public boolean isSupportViewLock()
	{
		if( FunctionConfig.isNetVersion() )
		{
			return false;
		}
		boolean result = ( versionCode > 0 ) && ( mode == MODE_VIEW );
		if( !result )
		{//没有移植包时，看是否打开锁屏tab
			if( !"true".equals( lockV ) )//没有打开锁屏tab，则认为有移植包，不开启服务等
			{
				return true;
			}
		}
		return result;
	}
	
	private final String PERSONALBOX_CONFIG_FILENAME = "personalbox_config.xml";
	private final String CUSTOM_PERSONALBOX_CONFIG_FILENAME = "/system/launcher/personalbox_config.xml";
	private final String CUSTOM_FIRST_PERSONALBOX_CONFIG_FILENAME = "/system/oem/launcher/personalbox_config.xml";
	
	private void readDefaultData(
			Context mContext )
	{
		InputSource xmlin = null;
		File f1 = new File( CUSTOM_FIRST_PERSONALBOX_CONFIG_FILENAME );
		if( !f1.exists() )
		{
			f1 = new File( CUSTOM_PERSONALBOX_CONFIG_FILENAME );
		}
		boolean builtIn = ( mContext.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0;
		try
		{
			if( builtIn && f1.exists() )
				xmlin = new InputSource( new FileInputStream( f1.getAbsolutePath() ) );
			else
				xmlin = new InputSource( mContext.getAssets().open( PERSONALBOX_CONFIG_FILENAME ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		if( xmlin != null )
		{
			try
			{
				SAXParserFactory factoey = SAXParserFactory.newInstance();
				SAXParser parser = factoey.newSAXParser();
				XMLReader xmlreader = parser.getXMLReader();
				DefaultLayoutHandler handler = new DefaultLayoutHandler();
				parser = factoey.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler( handler );
				xmlreader.parse( xmlin );
				handler = null;
				xmlin = null;
			}
			catch( ParserConfigurationException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( SAXException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	class DefaultLayoutHandler extends DefaultHandler
	{
		
		public static final String GENERAl_CONFIG = "general_config";
		
		public DefaultLayoutHandler()
		{
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
				temp = atts.getValue( "show_theme_lock" );
				if( temp != null )
				{
					lockV = temp;
				}
			}
		}
	}
	
	private PlatformInfo()
	{
	}
	
	private static String PLATFORM_SETTING_NAME = "cooee_lock_config";
	
	private String readChannel(
			Context context )
	{
		return "";
	}
	
	private void resetVersionDefault(
			String defaultChannel )
	{
		versionCode = 0;
		mode = MODE_DEFAULT;
		channel = defaultChannel;
	}
	
	private void init(
			Context context )
	{
		final String defaultChannel = readChannel( context );
		Log.d( LOG_TAG , "defaultChannel=" + channel );
		resetVersionDefault( defaultChannel );
		String settingStr = android.provider.Settings.System.getString( context.getContentResolver() , PLATFORM_SETTING_NAME );
		Log.d( LOG_TAG , "settingStr=" + settingStr );
		if( settingStr != null )
		{
			String[] strArray = settingStr.split( "," );
			if( strArray.length >= 3 )
			{
				try
				{
					versionCode = Integer.parseInt( strArray[0] );
					if( versionCode >= 1000 )
					{
						mode = Integer.parseInt( strArray[1] );
						channel = strArray[2];
					}
					else
					{
						resetVersionDefault( defaultChannel );
					}
				}
				catch( NumberFormatException ex )
				{
					ex.printStackTrace();
					Log.d( LOG_TAG , ex.toString() );
					resetVersionDefault( defaultChannel );
				}
			}
		}
	}
	
	private void checkState(
			Context context )
	{
		PackageManager pkgMgr = context.getPackageManager();
		readDefaultData( context );
		
	}
	
	private void setComponentEnabled(
			PackageManager pkgMgr ,
			ComponentName compName ,
			int newStat )
	{
		if( pkgMgr.getComponentEnabledSetting( compName ) != newStat )
		{
			Log.d( LOG_TAG , "change " + compName.getShortClassName() + "=" + newStat );
			pkgMgr.setComponentEnabledSetting( compName , newStat , PackageManager.DONT_KILL_APP );
		}
	}
}
