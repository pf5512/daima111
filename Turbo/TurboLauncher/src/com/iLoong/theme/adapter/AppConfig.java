package com.iLoong.theme.adapter;


import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.iLoong.launcher.Desktop3D.Log;


public class AppConfig
{
	
	private static AppConfig mInstance = null;
	
	public static AppConfig getInstance(
			Context context )
	{
		if( mInstance != null )
		{
			return mInstance;
		}
		AppConfig ins = new AppConfig();
		ins.loadXml( context );
		mInstance = ins;
		return mInstance;
	}
	
	private String default_lockscreen_package = "";// 默认解锁的包名
	private String default_lockscreen_class = ""; // 默认解锁的类名
	private String default_lockscreen_wrap = "";// 默认解锁的反射名
	private boolean zheshanVisible = true; // 内置解锁是否显示折扇
	private boolean systemWallpaper = false; // S4解锁是否使用系统壁纸
	private boolean lightColor = false;// S4是否在2.3版本显示浅色图片
	private boolean volume = false;//
	private boolean needIcon = false;
	private int unlockTime = 1000;
	private int xinYiX = 0;
	private int xinYiY = 0;
	private int xinYiProportion = 0;
	public String timeXy = "0";
	public int slidingScreenWidth = 0;
	public boolean useSystemIconStyle = false;
	private int IconX = 0;
	private int IconY = 0;
	private int SimX = 0;
	private int SimY = 0;
	private int PromptX = 0;
	private int PromptY = 0;
	private int IconProportion = 0;
	private int dataX = 0;
	private int dataY = 0;
	private int timeX = 0;
	private int timeY = 0;
	private boolean xinYiDisplay = true;
	private int pHoneIconIn = 0;
	private int sMsIconIn = 0;
	private int timefontsize = 100;
	private int datefontsize = 36;
	static String[] PackageName = new String[4];
	static String[] ClassName = new String[4];
	static String[] ImageNames = new String[4];
	private String bgPath = "";
	private boolean Increaseweek = false;
	private boolean systemWallpaper4_0 = false;
	private int missingInformationY = 0;
	private int mSmsbl = 100;
	private boolean Accordingtocarrier = true;
	private int Powerfontsize = 30;
	private int powerX = 0;
	private int powerY = 0;
	private boolean missingInformationTimeDis = true;
	private boolean AmFixed = true;
	private boolean shielding = false;
	private boolean themeVisible = true;
	
	public boolean themeVisible()
	{
		return themeVisible;
	}
	
	public boolean shielding()
	{
		return shielding;
	}
	
	public boolean AmFixed()
	{
		return AmFixed;
	}
	
	public boolean missingInformationTimeDis()
	{
		return missingInformationTimeDis;
	}
	
	public int Powerfontsize()
	{
		return Powerfontsize;
	}
	
	public int powerX()
	{
		return powerX;
	}
	
	public int powerY()
	{
		return powerY;
	}
	
	public boolean Accordingtocarrier()
	{
		return Accordingtocarrier;
	}
	
	public int mSmsbl()
	{
		return mSmsbl;
	}
	
	public int missingInformationY()
	{
		return missingInformationY;
	}
	
	public boolean systemWallpaper4_0()
	{
		return systemWallpaper4_0;
	}
	
	public boolean Increaseweek()
	{
		return Increaseweek;
	}
	
	public int unlockTime()
	{
		return unlockTime;
	}
	
	public int datefontsize()
	{
		return datefontsize;
	}
	
	public int timefontsize()
	{
		return timefontsize;
	}
	
	public String bgPath()
	{
		return bgPath;
	}
	
	public int pHoneIconIn()
	{
		return pHoneIconIn;
	}
	
	public int sMsIconIn()
	{
		return sMsIconIn;
	}
	
	public String[] PackageName()
	{
		return PackageName;
	}
	
	public String[] ClassName()
	{
		return ClassName;
	}
	
	public String[] ImageNames()
	{
		return ImageNames;
	}
	
	public boolean xinYiDisplay()
	{
		return xinYiDisplay;
	}
	
	public int dataX()
	{
		return dataX;
	}
	
	public int dataY()
	{
		return dataY;
	}
	
	public int timeX()
	{
		return timeX;
	}
	
	public int timeY()
	{
		return timeY;
	}
	
	public boolean useSystemIconStyle()
	{
		return useSystemIconStyle;
	}
	
	public int slidingScreenWidth()
	{
		return slidingScreenWidth;
	}
	
	public int IconProportion()
	{
		return IconProportion;
	}
	
	public int IconX()
	{
		return IconX;
	}
	
	public int IconY()
	{
		return IconY;
	}
	
	public int SimX()
	{
		return SimX;
	}
	
	public int SimY()
	{
		return SimY;
	}
	
	public int PromptX()
	{
		return PromptX;
	}
	
	public int PromptY()
	{
		return PromptY;
	}
	
	public int xinYiProportion()
	{
		return xinYiProportion;
	}
	
	public int xinYiX()
	{
		return xinYiX;
	}
	
	public int xinYiY()
	{
		return xinYiY;
	}
	
	public String timeXy()
	{
		return timeXy;
	}
	
	public boolean needIcon()
	{
		return needIcon;
	}
	
	public boolean volume()
	{
		return volume;
	}
	
	public boolean lightColor()
	{
		return lightColor;
	}
	
	public boolean getSystemWallpaper()
	{
		return systemWallpaper;
	}
	
	public boolean getZheshanVisible()
	{
		return zheshanVisible;
	}
	
	public String getDefaultLockscreenPackage()
	{
		return default_lockscreen_package;
	}
	
	public String getDefaultLockscreenClass()
	{
		return default_lockscreen_class;
	}
	
	public String getDefaultLockscreenWrap()
	{
		return default_lockscreen_wrap;
	}
	
	private AppConfig()
	{
	}
	
	private void loadXml(
			Context context )
	{
		InputStream xmlStream = null;
		XmlPullParser xmlPull = null;
		try
		{
			xmlStream = context.getAssets().open( "appconfig.xml" );
			xmlPull = XmlPullParserFactory.newInstance().newPullParser();
			xmlPull.setInput( xmlStream , "UTF-8" );
			int eventType = xmlPull.getEventType();
			while( eventType != XmlPullParser.END_DOCUMENT )
			{
				switch( eventType )
				{
					case XmlPullParser.START_TAG:
					{
						if( "item".equals( xmlPull.getName() ) )
						{
							String itemName = getAttributeValue( xmlPull , "name" , "" );
							String itemValue = getAttributeValue( xmlPull , "value" , "" );
							readItem( itemName , itemValue );
						}
					}
						break;
					default:
						break;
				}
				eventType = xmlPull.next();
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		catch( XmlPullParserException e )
		{
			e.printStackTrace();
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
	
	private String getAttributeValue(
			XmlPullParser pull ,
			String attName ,
			String defaultValue )
	{
		for( int i = 0 ; i < pull.getAttributeCount() ; i++ )
		{
			if( pull.getAttributeName( i ).equals( attName ) )
			{
				return pull.getAttributeValue( i );
			}
		}
		return defaultValue;
	}
	
	private void readItem(
			String itemName ,
			String itemValue )
	{
		Log.v( "AppConfig" , itemName + "=" + itemValue );
		if( itemName.equals( "zheshanVisible" ) )
		{
			zheshanVisible = itemValue.equals( "true" );
		}
		else if( itemName.equals( "default_lockscreen_package" ) )
		{
			default_lockscreen_package = itemValue;
		}
		else if( itemName.equals( "default_lockscreen_class" ) )
		{
			default_lockscreen_class = itemValue;
		}
		else if( itemName.equals( "default_lockscreen_wrap" ) )
		{
			default_lockscreen_wrap = itemValue;
		}
		else if( itemName.equals( "systemWallpaper" ) )
		{
			systemWallpaper = itemValue.equals( "true" );
		}
		else if( itemName.equals( "lightColor" ) )
		{
			lightColor = itemValue.equals( "true" );
		}
		else if( itemName.equals( "volume" ) )
		{
			volume = itemValue.equals( "true" );
		}
		else if( itemName.equals( "timeXy" ) )
		{
			timeXy = itemValue;
		}
		else if( itemName.equals( "xinYiX" ) )
		{
			xinYiX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "xinYiY" ) )
		{
			xinYiY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "xinYiProportion" ) )
		{
			xinYiProportion = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "needIcon" ) )
		{
			needIcon = itemValue.equals( "true" );
		}
		else if( itemName.equals( "slidingScreenWidth" ) )
		{
			slidingScreenWidth = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "useSystemIconStyle" ) )
		{
			useSystemIconStyle = itemValue.equals( "true" );
		}
		else if( itemName.equals( "IconX" ) )
		{
			IconX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "SimX" ) )
		{
			SimX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "SimY" ) )
		{
			SimY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "IconY" ) )
		{
			IconY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "PromptX" ) )
		{
			PromptX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "PromptY" ) )
		{
			PromptY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "IconProportion" ) )
		{
			IconProportion = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "dataX" ) )
		{
			dataX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "dataY" ) )
		{
			dataY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "timeX" ) )
		{
			timeX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "timeY" ) )
		{
			timeY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "xinYiDisplay" ) )
		{
			xinYiDisplay = itemValue.equals( "true" );
		}
		else if( itemName.equals( "pHoneIconIn" ) )
		{
			pHoneIconIn = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "SmsIconIn" ) )
		{
			sMsIconIn = Integer.parseInt( itemValue );
		}
		// //////////////////包名///////////////////
		else if( itemName.equals( "Icon1PackageName" ) )
		{
			PackageName[0] = itemValue;
		}
		else if( itemName.equals( "Icon2PackageName" ) )
		{
			PackageName[1] = itemValue;
		}
		else if( itemName.equals( "Icon3PackageName" ) )
		{
			PackageName[2] = itemValue;
		}
		else if( itemName.equals( "Icon4PackageName" ) )
		{
			PackageName[3] = itemValue;
		}
		// //////////////////类名///////////////////
		else if( itemName.equals( "Icon1ClassName" ) )
		{
			ClassName[0] = itemValue;
		}
		else if( itemName.equals( "Icon2ClassName" ) )
		{
			ClassName[1] = itemValue;
		}
		else if( itemName.equals( "Icon3ClassName" ) )
		{
			ClassName[2] = itemValue;
		}
		else if( itemName.equals( "Icon4ClassName" ) )
		{
			ClassName[3] = itemValue;
		}
		// //////////////////图标名字///////////////////
		else if( itemName.equals( "Icon1ImageNames" ) )
		{
			ImageNames[0] = itemValue;
		}
		else if( itemName.equals( "Icon2ImageNames" ) )
		{
			ImageNames[1] = itemValue;
		}
		else if( itemName.equals( "Icon3ImageNames" ) )
		{
			ImageNames[2] = itemValue;
		}
		else if( itemName.equals( "Icon4ImageNames" ) )
		{
			ImageNames[3] = itemValue;
		}
		// //////////////////////////////////////////
		else if( itemName.equals( "bgPath" ) )
		{
			bgPath = itemValue;
		}
		else if( itemName.equals( "timefontsize" ) )
		{
			timefontsize = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "datefontsize" ) )
		{
			datefontsize = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "unlockTime" ) )
		{
			unlockTime = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "Increaseweek" ) )
		{
			Increaseweek = itemValue.equals( "true" );
		}
		else if( itemName.equals( "systemWallpaper4_0" ) )
		{
			systemWallpaper4_0 = itemValue.equals( "true" );
		}
		else if( itemName.equals( "missingInformationY" ) )
		{
			missingInformationY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "mSmsbl" ) )
		{
			mSmsbl = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "Accordingtocarrier" ) )
		{
			Accordingtocarrier = itemValue.equals( "true" );
		}
		else if( itemName.equals( "Powerfontsize" ) )
		{
			Powerfontsize = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "powerX" ) )
		{
			powerX = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "powerY" ) )
		{
			powerY = Integer.parseInt( itemValue );
		}
		else if( itemName.equals( "missingInformationTimeDis" ) )
		{
			missingInformationTimeDis = itemValue.equals( "true" );
		}
		else if( itemName.equals( "AmFixed" ) )
		{
			AmFixed = itemValue.equals( "true" );
		}
		else if( itemName.equals( "shielding" ) )
		{
			shielding = itemValue.equals( "true" );
		}
		else if( itemName.equals( "themeVisible" ) )
		{
			themeVisible = itemValue.equals( "true" );
		}
		else
		{
			Log.e( "AppConfig" , "ERROR item:" + itemName + "=" + itemValue );
		}
	}
}
