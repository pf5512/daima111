package com.iLoong.launcher.Desktop3D;


import java.util.Calendar;

import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class DrawDynamicIcon
{
	
	public static TextureRegion deskclockhourpoint;
	public static TextureRegion deskclockminpoint;
	public static TextureRegion deskclocksecpoint;
	public static TextureRegion deskclockpoint;
	public static TextureRegion calendarnumtotal[];
	public static TextureRegion calendarweektotal[];
	public static TextureRegion weathertotal[];
	public static TextureRegion calendarnumone;
	public static TextureRegion calendarnumtwo;
	public static TextureRegion calendarweekday;
	public static TextureRegion weathericon;
	public static float icony;
	public static float hotseaty;
	public float iconusedy;
	float iconHeight;
	private static int deskclockinfo = -99;
	private static int calendarinfo = -99;
	private static int weatherinfo = -99;
	final public static int weathernum = 8;
	public static boolean needrefreshres;
	public static boolean needrefreshbg;
	public static boolean needsecpoint;
	public static boolean needweekday;
	public static String allBgPath[];
	public static int readXmlMode = -1;
	public static String dybmppath = "theme/icon/80/";
	public static String clrbmppath = "theme/icon/80/dynamicicon/calendar/";
	public static String weekbmppath = "theme/icon/80/dynamicicon/calendar/week";
	
	public DrawDynamicIcon()
	{
		iconHeight = Utilities.sIconTextureHeight;
		needsecpoint = false;
		needweekday = false;
		getDynamicIconRes();
		if( needsecpoint )
		{
			SendMsgToAndroid.updateTextureForDynamicIcon( 1000 );
		}
	}
	
	public static void seticony(
			float y )
	{
		icony = y;
	}
	
	public static void sethotseaty(
			float y )
	{
		hotseaty = y;
	}
	
	private final float getHourDegree()
	{
		float deskclock_hour_rotate = 0.0f;
		Calendar ca = Calendar.getInstance();
		float hour = ca.get( Calendar.HOUR );
		float min = ca.get( Calendar.MINUTE );
		deskclock_hour_rotate = ( hour + min / 60 ) / 12 * 360;
		return -deskclock_hour_rotate;
	}
	
	private final float getMinDegree()
	{
		float deskclock_min_rotate = 0.0f;
		Calendar ca = Calendar.getInstance();
		float min = ca.get( Calendar.MINUTE );
		deskclock_min_rotate = min / 60 * 360;
		return -deskclock_min_rotate;
	}
	
	private final float getSecDegree()
	{
		float deskclock_sec_rotate = 0.0f;
		Calendar ca = Calendar.getInstance();
		float sec = ca.get( Calendar.SECOND );
		deskclock_sec_rotate = sec / 60 * 360;
		return -deskclock_sec_rotate;
	}
	
	private final static boolean getDeskClockIcon()
	{
		if( ThemeManager.getInstance().isFileExistIgnoreSystem( "theme/icon/80/dynamicicon/deskclock/hour.png" ) && ThemeManager.getInstance().isFileExistIgnoreSystem(
				"theme/icon/80/dynamicicon/deskclock/min.png" ) && ThemeManager.getInstance().isFileExistIgnoreSystem( "theme/icon/80/dynamicicon/deskclock/point.png" ) )
		{
			if( deskclockhourpoint == null || deskclockminpoint == null || deskclockpoint == null || needrefreshres )
			{
				Bitmap hourpointbmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( "theme/icon/80/dynamicicon/deskclock/hour.png" );
				Bitmap minpointbmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( "theme/icon/80/dynamicicon/deskclock/min.png" );
				Bitmap pointbmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( "theme/icon/80/dynamicicon/deskclock/point.png" );
				Bitmap secpointbmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( "theme/icon/80/dynamicicon/deskclock/sec.png" );
				if( hourpointbmp == null || minpointbmp == null || pointbmp == null )
				{
					return false;
				}
				Bitmap hourpointbmpresize = Tools.resizeBitmap( hourpointbmp , ( DefaultLayout.app_icon_size * 1.0f ) / hourpointbmp.getHeight() );
				Bitmap minpointbmpresize = Tools.resizeBitmap( minpointbmp , ( DefaultLayout.app_icon_size * 1.0f ) / minpointbmp.getHeight() );
				Bitmap pointbmpresize = Tools.resizeBitmap( pointbmp , ( DefaultLayout.app_icon_size * 1.0f ) / pointbmp.getHeight() );
				if( deskclockhourpoint != null && deskclockminpoint != null && deskclockpoint != null )
				{
					( (BitmapTexture)deskclockhourpoint.getTexture() ).changeBitmap( hourpointbmpresize , true );
					( (BitmapTexture)deskclockminpoint.getTexture() ).changeBitmap( minpointbmpresize , true );
					( (BitmapTexture)deskclockpoint.getTexture() ).changeBitmap( pointbmpresize , true );
				}
				else
				{
					deskclockhourpoint = new TextureRegion( new BitmapTexture( hourpointbmpresize , true ) );
					deskclockminpoint = new TextureRegion( new BitmapTexture( minpointbmpresize , true ) );
					deskclockpoint = new TextureRegion( new BitmapTexture( pointbmpresize , true ) );
				}
				if( needsecpoint && secpointbmp != null )
				{
					Bitmap secpointbmpresize = Tools.resizeBitmap( secpointbmp , ( DefaultLayout.app_icon_size * 1.0f ) / secpointbmp.getHeight() );
					deskclocksecpoint = new TextureRegion( new BitmapTexture( secpointbmpresize , true ) );
				}
				else
				{
					needsecpoint = false;
				}
			}
			return true;
		}
		return false;
	}
	
	private final static boolean getCalendarBmp()
	{
		if( Calendar.getInstance() != null )
		{
			Calendar ca = Calendar.getInstance();
			int date = ca.get( Calendar.DATE );
			String numbmppath;
			if( calendarnumtotal == null || needrefreshres )
			{
				calendarnumtotal = new TextureRegion[10];
				for( int i = 0 ; i < 10 ; i++ )
				{
					numbmppath = clrbmppath + i + ".png";
					if( !ThemeManager.getInstance().isFileExistIgnoreSystem( numbmppath ) )
					{
						Log.v( "syw" , "getCalendarBmpbg" );
						return false;
					}
				}
				for( int i = 0 ; i < 10 ; i++ )
				{
					Texture t;
					numbmppath = clrbmppath + i + ".png";
					if( ThemeManager.getInstance().getBitmapIgnoreSystemTheme( numbmppath ) == null )
					{
						return false;
					}
					Bitmap calendarbmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( numbmppath );
					Bitmap tempbmp = Tools.resizeBitmap( calendarbmp , ( DefaultLayout.app_icon_size * 1.0f ) / calendarbmp.getHeight() );
					t = new BitmapTexture( tempbmp );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					calendarnumtotal[i] = new TextureRegion( t );
				}
			}
			calendarnumone = calendarnumtotal[date / 10];
			calendarnumtwo = calendarnumtotal[date % 10];
			if( needweekday )
			{
				int weekday = ca.get( Calendar.WEDNESDAY );
				if( calendarweektotal == null || needrefreshres )
				{
					calendarweektotal = new TextureRegion[7];
					for( int i = 0 ; i < 7 ; i++ )
					{
						Texture t;
						numbmppath = weekbmppath + i + ".png";
						if( ThemeManager.getInstance().getBitmapIgnoreSystemTheme( numbmppath ) == null )
						{
							needweekday = false;
							return true;
						}
						Bitmap weekdaybmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( numbmppath );
						Bitmap tempbmp = Tools.resizeBitmap( weekdaybmp , ( DefaultLayout.app_icon_size * 1.0f ) / weekdaybmp.getHeight() );
						if( calendarweektotal[i] != null )
						{
							( (BitmapTexture)calendarweektotal[i].getTexture() ).changeBitmap( tempbmp , true );
						}
						else
						{
							t = new BitmapTexture( tempbmp );
							t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
							calendarweektotal[i] = new TextureRegion( t );
						}
					}
				}
				calendarweekday = calendarweektotal[weekday - 1];
			}
		}
		return true;
	}
	
	private final static boolean getWeatherBmp()
	{
		String bmppath = "theme/icon/80/dynamicicon/weather/";
		String weatherbmppath;
		int j = 0;
		if( weathertotal == null || needrefreshres )
		{
			weathertotal = new TextureRegion[weathernum];
			for( int i = 0 ; i < weathernum ; i++ )
			{
				weatherbmppath = bmppath + i + ".png";
				if( !ThemeManager.getInstance().isFileExistIgnoreSystem( weatherbmppath ) )
				{
					return false;
				}
			}
			for( int i = 0 ; i < weathernum ; i++ )
			{
				Texture t;
				weatherbmppath = bmppath + i + ".png";
				if( ThemeManager.getInstance().getBitmapIgnoreSystemTheme( weatherbmppath ) == null )
				{
					return false;
				}
				Bitmap weatherbmp = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( weatherbmppath );
				Bitmap tempbmp = Tools.resizeBitmap( weatherbmp , ( DefaultLayout.app_icon_size * 1.0f ) / weatherbmp.getHeight() );
				if( weathertotal[i] != null )
				{
					( (BitmapTexture)weathertotal[i].getTexture() ).changeBitmap( tempbmp , true );
				}
				else
				{
					t = new BitmapTexture( tempbmp );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					weathertotal[i] = new TextureRegion( t );
				}
			}
		}
		if( CooeeWeather.getInstance() != null )
		{
			CooeeWeather weather = CooeeWeather.getInstance();
			j = weather.getnowweather();
		}
		weathericon = weathertotal[j];
		return true;
	}
	
	private final void drawdeskclock(
			SpriteBatch batch ,
			ItemInfo item ,
			float x ,
			float y ,
			float originX ,
			float originY ,
			float width ,
			float height ,
			float scaleX ,
			float scaleY ,
			float iconRotation )
	{
		float deskclock_min_stateX;
		float deskclock_min_stateY;
		float deskclock_hour_stateX;
		float deskclock_hour_stateY;
		float deskclock_point_stateX;
		float deskclock_point_stateY;
		float deskclock_min_originX;
		float deskclock_min_originY;
		float deskclock_hour_originX;
		float deskclock_hour_originY;
		float deskclock_point_originX;
		float deskclock_point_originY;
		getDeskClockIcon();
		if( iconHeight + icony > height )
		{
			iconusedy = hotseaty;
		}
		else
		{
			iconusedy = icony;
		}
		deskclock_min_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - deskclockminpoint.getRegionWidth() / 2;
		deskclock_min_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - deskclockminpoint.getRegionHeight() / 2;
		deskclock_hour_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - deskclockhourpoint.getRegionWidth() / 2;
		deskclock_hour_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - deskclockhourpoint.getRegionHeight() / 2;
		deskclock_point_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - deskclockpoint.getRegionWidth() / 2;
		deskclock_point_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - deskclockpoint.getRegionHeight() / 2;
		deskclock_min_originX = deskclockminpoint.getRegionWidth() / 2;
		deskclock_min_originY = deskclockminpoint.getRegionHeight() / 2;
		deskclock_hour_originX = deskclockhourpoint.getRegionWidth() / 2;
		deskclock_hour_originY = deskclockhourpoint.getRegionHeight() / 2;
		deskclock_point_originX = deskclockpoint.getRegionWidth() / 2;
		deskclock_point_originY = deskclockpoint.getRegionHeight() / 2;
		batch.draw(
				deskclockhourpoint ,
				deskclock_hour_stateX ,
				deskclock_hour_stateY ,
				deskclock_hour_originX ,
				deskclock_hour_originY ,
				deskclockhourpoint.getRegionWidth() ,
				deskclockhourpoint.getRegionHeight() ,
				scaleX ,
				scaleY ,
				getHourDegree() + iconRotation );
		batch.draw(
				deskclockminpoint ,
				deskclock_min_stateX ,
				deskclock_min_stateY ,
				deskclock_min_originX ,
				deskclock_min_originY ,
				deskclockminpoint.getRegionWidth() ,
				deskclockminpoint.getRegionHeight() ,
				scaleX ,
				scaleY ,
				getMinDegree() + iconRotation );
		batch.draw(
				deskclockpoint ,
				deskclock_point_stateX ,
				deskclock_point_stateY ,
				deskclock_point_originX ,
				deskclock_point_originY ,
				deskclockpoint.getRegionWidth() ,
				deskclockpoint.getRegionHeight() ,
				scaleX ,
				scaleY ,
				iconRotation );
		if( needsecpoint && deskclocksecpoint != null )
		{
			float deskclock_sec_stateX;
			float deskclock_sec_stateY;
			float deskclock_sec_originX;
			float deskclock_sec_originY;
			deskclock_sec_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - deskclocksecpoint.getRegionWidth() / 2;
			deskclock_sec_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - deskclocksecpoint.getRegionHeight() / 2;
			deskclock_sec_originX = deskclocksecpoint.getRegionWidth() / 2;
			deskclock_sec_originY = deskclocksecpoint.getRegionHeight() / 2;
			batch.draw(
					deskclocksecpoint ,
					deskclock_sec_stateX ,
					deskclock_sec_stateY ,
					deskclock_sec_originX ,
					deskclock_sec_originY ,
					deskclocksecpoint.getRegionWidth() ,
					deskclocksecpoint.getRegionHeight() ,
					scaleX ,
					scaleY ,
					getSecDegree() + iconRotation );
		}
	}
	
	private final void drawCalendar(
			SpriteBatch batch ,
			ItemInfo item ,
			float x ,
			float y ,
			float originX ,
			float originY ,
			float width ,
			float height ,
			float scaleX ,
			float scaleY ,
			float iconRotation )
	{
		float calendar_num1_stateX;
		float calendar_num1_stateY;
		float calendar_num2_stateX;
		float calendar_num2_stateY;
		float calendar_num1_originX;
		float calendar_num1_originY;
		float calendar_num2_originX;
		float calendar_num2_originY;
		getCalendarBmp();
		if( iconHeight + icony > height )
		{
			iconusedy = hotseaty;
		}
		else
		{
			iconusedy = icony;
		}
		calendar_num1_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - calendarnumone.getRegionWidth() / 2 - scaleX * calendarnumone.getRegionWidth() / 2;
		calendar_num1_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - calendarnumone.getRegionHeight() / 2;
		calendar_num2_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - calendarnumtwo.getRegionWidth() / 2 + scaleX * calendarnumtwo.getRegionWidth() / 2;
		calendar_num2_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - calendarnumtwo.getRegionHeight() / 2;
		calendar_num1_originX = calendarnumone.getRegionWidth() / 2;
		calendar_num1_originY = calendarnumone.getRegionHeight() / 2;
		calendar_num2_originX = calendarnumtwo.getRegionWidth() / 2;
		calendar_num2_originY = calendarnumtwo.getRegionHeight() / 2;
		batch.draw(
				calendarnumone ,
				calendar_num1_stateX ,
				calendar_num1_stateY ,
				calendar_num1_originX ,
				calendar_num1_originY ,
				calendarnumone.getRegionWidth() ,
				calendarnumone.getRegionHeight() ,
				scaleX ,
				scaleY ,
				iconRotation );
		batch.draw(
				calendarnumtwo ,
				calendar_num2_stateX ,
				calendar_num2_stateY ,
				calendar_num2_originX ,
				calendar_num2_originY ,
				calendarnumtwo.getRegionWidth() ,
				calendarnumtwo.getRegionHeight() ,
				scaleX ,
				scaleY ,
				iconRotation );
		if( needweekday )
		{
			float calendar_week_stateX;
			float calendar_week_stateY;
			float calendar_week_originX;
			float calendar_week_originY;
			calendar_week_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - calendarweekday.getRegionWidth() / 2;
			calendar_week_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - calendarweekday.getRegionHeight() / 2;
			calendar_week_originX = calendarweekday.getRegionWidth() / 2;
			calendar_week_originY = calendarweekday.getRegionHeight() / 2;
			batch.draw(
					calendarweekday ,
					calendar_week_stateX ,
					calendar_week_stateY ,
					calendar_week_originX ,
					calendar_week_originY ,
					calendarweekday.getRegionWidth() ,
					calendarweekday.getRegionHeight() ,
					scaleX ,
					scaleY ,
					iconRotation );
		}
	}
	
	private final void drawWeather(
			SpriteBatch batch ,
			ItemInfo item ,
			float x ,
			float y ,
			float originX ,
			float originY ,
			float width ,
			float height ,
			float scaleX ,
			float scaleY ,
			float iconRotation )
	{
		float weather_stateX;
		float weather_stateY;
		float weather_originX;
		float weather_originY;
		getWeatherBmp();
		if( iconHeight + icony > height )
		{
			iconusedy = hotseaty;
		}
		else
		{
			iconusedy = icony;
		}
		weather_stateX = x + ( originX - originX * scaleX ) + ( width / 2 * scaleX ) - weathericon.getRegionWidth() / 2;
		weather_stateY = y + ( originY - originY * scaleY ) + ( ( iconusedy + iconHeight / 2 ) * scaleY ) - weathericon.getRegionHeight() / 2;
		weather_originX = weathericon.getRegionWidth() / 2;
		weather_originY = weathericon.getRegionHeight() / 2;
		batch.draw( weathericon , weather_stateX , weather_stateY , weather_originX , weather_originY , weathericon.getRegionWidth() , weathericon.getRegionHeight() , scaleX , scaleY , iconRotation );
	}
	
	final void drawDynamicIcon(
			SpriteBatch batch ,
			ItemInfo item ,
			float x ,
			float y ,
			float originX ,
			float originY ,
			float width ,
			float height ,
			float scaleX ,
			float scaleY ,
			float iconRotation )
	{
		ShortcutInfo textureinfo = (ShortcutInfo)item;
		if( iLoongLauncher.getInstance().themeChanging || iLoongLauncher.getInstance() == null || !getDynamicIconRes() )
		{
			return;
		}
		if( DefaultLayout.getInstance().getDynamicIcon( textureinfo ) == deskclockinfo )
		{
			drawdeskclock( batch , item , x , y , originX , originY , width , height , scaleX , scaleY , iconRotation );
		}
		else if( DefaultLayout.getInstance().getDynamicIcon( textureinfo ) == calendarinfo )
		{
			drawCalendar( batch , item , x , y , originX , originY , width , height , scaleX , scaleY , iconRotation );
		}
		else if( DefaultLayout.getInstance().getDynamicIcon( textureinfo ) == weatherinfo )
		{
			drawWeather( batch , item , x , y , originX , originY , width , height , scaleX , scaleY , iconRotation );
		}
	}
	
	final static String changeDynamicIcon(
			ResolveInfo info )
	{ //配置app默认背景图
		if( !initDynamicIconBg() )
		{
			return null;
		}
		if( DefaultLayout.getInstance().getDynamicIcon( info ) == deskclockinfo )
		{
			return allBgPath[deskclockinfo];
		}
		else if( DefaultLayout.getInstance().getDynamicIcon( info ) == calendarinfo )
		{
			return allBgPath[calendarinfo];
		}
		else if( DefaultLayout.getInstance().getDynamicIcon( info ) == weatherinfo )
		{
			return allBgPath[weatherinfo];
		}
		return null;
	}
	
	final static boolean initDynamicIconBg()
	{
		int bgsize = 0;
		if( allBgPath == null || needrefreshbg )
		{
			allBgPath = new String[3];
			String allBgFullPath;
			deskclockinfo = -99;
			calendarinfo = -99;
			weatherinfo = -99;
			if( DefaultLayout.dynamiciconlist.size() == 0 )
			{
				deskclockinfo = 0;
				calendarinfo = 1;
				weatherinfo = 2;
				allBgPath[deskclockinfo] = "dynamicicon/deskclock/clock.png";
				allBgPath[calendarinfo] = "dynamicicon/calendar/calendar.png";
				allBgPath[weatherinfo] = "dynamicicon/weather/" + weathernum + ".png";
			}
			else
			{
				for( int i = 0 ; i < DefaultLayout.dynamiciconlist.size() ; i++ )
				{
					if( DefaultLayout.dynamiciconlist.get( i ).equals( "时钟" ) )
					{
						deskclockinfo = i;
						allBgPath[i] = "dynamicicon/deskclock/clock.png";
					}
					else if( DefaultLayout.dynamiciconlist.get( i ).equals( "日历" ) )
					{
						calendarinfo = i;
						allBgPath[i] = "dynamicicon/calendar/calendar.png";
					}
					else if( DefaultLayout.dynamiciconlist.get( i ).equals( "天气" ) )
					{
						weatherinfo = i;
						allBgPath[i] = "dynamicicon/weather/" + weathernum + ".png";
					}
				}
			}
			for( int i = 0 ; i < allBgPath.length ; i++ )
			{
				allBgFullPath = dybmppath + allBgPath[i];
				if( ThemeManager.getInstance().getBitmapIgnoreSystemTheme( allBgFullPath ) != null )
				{
					bgsize++;
				}
				else
				{
					allBgPath[i] = null;
				}
			}
			if( bgsize == 0 )
			{
				DrawDynamicIcon.needrefreshbg = false;
				return false;
			}
			DrawDynamicIcon.needrefreshbg = false;
			return true;
		}
		else
		{
			DrawDynamicIcon.needrefreshbg = false;
			return true;
		}
	}
	
	public final static boolean getDynamicIconRes()
	{
		if( ThemeManager.getInstance() == null )
		{
			return false;
		}
		if( getDeskClockIcon() && getCalendarBmp() && getWeatherBmp() && initDynamicIconBg() )
		{
			needrefreshres = false;
			return true;
		}
		else
		{
			needrefreshres = false;
			return false;
		}
	}
	
	public static void setDynamicIconMode()
	{
		if( DrawDynamicIcon.readXmlMode < 0 )
		{
			if( DefaultLayout.dynamiciconlist.size() > 0 )
			{
				DrawDynamicIcon.readXmlMode = 0; //最新配置，配置了title
				DefaultLayout.sysDynamiciconlist.addAll( DefaultLayout.dynamiciconlist );
			}
			else if( DefaultLayout.dynamicIcon.size() > 0 )
			{
				DrawDynamicIcon.readXmlMode = 1; //老配置，未配置title。只配置了包名类名
				DefaultLayout.sysDynamicIcon.addAll( DefaultLayout.dynamicIcon );
			}
			else
			{
				DrawDynamicIcon.readXmlMode = 2; //未配置
			}
		}
		else
		{
			DrawDynamicIcon.readXmlMode = 2; //未配置
		}
	}
}
