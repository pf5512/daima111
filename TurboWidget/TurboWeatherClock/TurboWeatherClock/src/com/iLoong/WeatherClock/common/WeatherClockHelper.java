package com.iLoong.WeatherClock.common;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.FontMetrics;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.WeatherClock.view.WidgetWeatherClock;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class WeatherClockHelper
{
	
	public static String Amipkgname = "com.gionee.amiweather";
	public static String Amiclsname = "com.gionee.amiweather.business.activities.SplashActivity";
	public static String PATH = "theme/widget/com.iLoong.WeatherClock/";
	public static String PATHWeatherBig = "theme/widget/com.iLoong.WeatherClock/theme/weather/";
	public static String PATHWeatherSmall = "theme/widget/com.iLoong.WeatherClock/theme/weathersmall/";
	//监听数据库需要的URI
	public static final String AUTHORITY = "com.gionee.amiweather";
	public static final Uri AUTHORITY_URI = Uri.parse( "content://" + AUTHORITY );
	public static final Uri WEATHER_SEARCH_FROM_WALLPAPER = Uri.withAppendedPath( AUTHORITY_URI , "search_from_wallpaper" );
	
	public static boolean getDataState(
			Context mContext )
	{
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		State state = null;
		if( connManager != null )
		{
			state = connManager.getNetworkInfo( ConnectivityManager.TYPE_MOBILE ).getState(); // 获取网络连接状态
		}
		if( state == null )
		{
			return false;
		}
		if( State.UNKNOWN == state )
		{ // 判断是否正在使用GPRS网络
			return false;
		}
		else if( state == NetworkInfo.State.CONNECTED )
		{
			Log.d( "apn state" , "CONNECTED" );
			return true;
		}
		else if( state == NetworkInfo.State.DISCONNECTED )
		{
			Log.d( "apn state" , "DISCONNECT" );
			return false;
		}
		else if( state == NetworkInfo.State.DISCONNECTING || state == NetworkInfo.State.CONNECTING )
		{
			Log.d( "apn state" , "DISCONNECTING || CONNECTING" );
			return false;
		}
		return false;
	}
	
	public static TextureRegion getRegion(
			Context maincontext ,
			Context currentThemecontext ,
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( currentThemecontext.getAssets().open( PATH + "theme/image/" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			BitmapTexture bt;
			try
			{
				bt = new BitmapTexture( BitmapFactory.decodeStream( maincontext.getAssets().open( PATH + "theme/image/" + name ) ) , true );
				bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				TextureRegion mBackRegion = new TextureRegion( bt );
				return mBackRegion;
			}
			catch( IOException e1 )
			{
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	public static TextureRegion getRegion(
			Context maincontext ,
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( maincontext.getAssets().open( PATH + "theme/image/" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean ifHaveAMIapp(
			Context context )
	{
		PackageManager manager = context.getPackageManager();
		try
		{
			PackageInfo info = manager.getPackageInfo( Amipkgname , PackageManager.GET_ACTIVITIES );
			if( info != null )
			{
				return true;
			}
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static boolean isHaveInternet(
			Context context )
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<WeatherInfo> QueryByUri(
			ContentResolver contentResolver )
	{
		List<WeatherInfo> list = new ArrayList<WeatherInfo>();
		Cursor cursor = contentResolver.query( WEATHER_SEARCH_FROM_WALLPAPER , null , null , null , null );
		if( cursor != null && cursor.getCount() > 0 )
		{
			while( cursor.moveToNext() )
			{
				WeatherInfo weather = new WeatherInfo();
				weather.set_id( cursor.getLong( cursor.getColumnIndex( "_id" ) ) );
				weather.setDate( cursor.getString( cursor.getColumnIndex( "date" ) ) );
				weather.setWeek( cursor.getInt( cursor.getColumnIndex( "week" ) ) );
				weather.setCity( cursor.getString( cursor.getColumnIndex( "city" ) ) );
				weather.setStatus( cursor.getString( cursor.getColumnIndex( "status" ) ) );
				weather.setStatus1( cursor.getString( cursor.getColumnIndex( "status1" ) ) );
				weather.setStatus2( cursor.getString( cursor.getColumnIndex( "status2" ) ) );
				weather.setDirection1( cursor.getString( cursor.getColumnIndex( "direction1" ) ) );
				weather.setDirection2( cursor.getString( cursor.getColumnIndex( "direction2" ) ) );
				weather.setDescription1( cursor.getString( cursor.getColumnIndex( "description1" ) ) );
				weather.setDescription2( cursor.getString( cursor.getColumnIndex( "description2" ) ) );
				weather.setPower( cursor.getString( cursor.getColumnIndex( "power" ) ) );
				weather.setPower1( cursor.getString( cursor.getColumnIndex( "power1" ) ) );
				weather.setPower2( cursor.getString( cursor.getColumnIndex( "power2" ) ) );
				weather.setTemperature( cursor.getString( cursor.getColumnIndex( "temperature" ) ) );
				weather.setTemperature1( cursor.getInt( cursor.getColumnIndex( "temperature1" ) ) );
				weather.setTemperature2( cursor.getInt( cursor.getColumnIndex( "temperature2" ) ) );
				weather.setAqiDec( cursor.getString( cursor.getColumnIndex( "aqiDec" ) ) );
				weather.setAqiVal( cursor.getString( cursor.getColumnIndex( "aqiVal" ) ) );
				weather.setPm25Val( cursor.getString( cursor.getColumnIndex( "pm25Val" ) ) );
				weather.setTgd1( cursor.getString( cursor.getColumnIndex( "tgd1" ) ) );
				weather.setTgd2( cursor.getString( cursor.getColumnIndex( "tgd2" ) ) );
				list.add( weather );
			}
			cursor.close();
		}
		return list;
	}
	
	private static Bitmap resizeBitmap(
			Bitmap bm ,
			int w ,
			int h )
	{
		Bitmap BitmapOrg = bm;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		if( width == w && height == h )
			return bm;
		float scaleWidth = ( (float)w ) / width;
		float scaleHeight = ( (float)h ) / height;
		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth , scaleHeight );
		Bitmap tmp = Bitmap.createBitmap( BitmapOrg , 0 , 0 , width , height , matrix , true );
		bm.recycle();
		return tmp;
	}
	
	//	public static TextureRegion drawCityTextureRegion(
	//			MainAppContext appContext ,
	//			String cityName )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_CITY_WIDTH;
	//		float height = Parameter.WEATHER_CITY_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setARGB( 255 , 82 , 94 , 116 );
	//		paint.setSubpixelText( true );
	//		paint.setTextSize( 24 * Parameter.scale );
	//		FontMetrics fontMetrics = paint.getFontMetrics();
	//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
	//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
	//		Bitmap bt = null;
	//		Bitmap newbt = null;
	//		try
	//		{
	//			bt = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/city_1.png" ) );
	//			newbt = resizeBitmap( bt , (int)width , (int)height );
	//			canvas.drawBitmap( newbt , 0f , 0f , paint );
	//		}
	//		catch( IOException e )
	//		{
	//			e.printStackTrace();
	//		}
	//		if( cityName != null )
	//		{
	//			canvas.drawText( cityName , 25f * Parameter.scale , posY , paint );
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		if( bt != null )
	//		{
	//			bt.recycle();
	//		}
	//		if( newbt != null )
	//		{
	//			newbt.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	//	public static TextureRegion drawNameTextureRegion(
	//			MainAppContext appContext ,
	//			String name )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_NAME_WIDTH;
	//		float height = Parameter.WEATHER_NAME_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setARGB( 255 , 82 , 94 , 116 );
	//		paint.setSubpixelText( true );
	//		paint.setTextSize( 30 * Parameter.scale );
	//		FontMetrics fontMetrics = paint.getFontMetrics();
	//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
	//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
	//		if( name != null )
	//		{
	//			canvas.drawText( name , 5f * Parameter.scale , posY , paint );
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	//	public static TextureRegion drawAirTextureRegion(
	//			MainAppContext appContext ,
	//			String air )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_AIR_WIDTH;
	//		float height = Parameter.WEATHER_AIR_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setARGB( 255 , 82 , 94 , 116 );
	//		paint.setSubpixelText( true );
	//		paint.setTextSize( 25 * Parameter.scale );
	//		FontMetrics fontMetrics = paint.getFontMetrics();
	//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
	//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
	//		if( air != null )
	//		{
	//			canvas.drawText( air , 40f * Parameter.scale , posY , paint );
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	//	public static TextureRegion drawDateTextureRegion(
	//			MainAppContext appContext ,
	//			String week ,
	//			String highTem ,
	//			String lowTem )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_DATE_WIDTH;
	//		float height = Parameter.WEATHER_DATE_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setARGB( 255 , 82 , 94 , 116 );
	//		paint.setSubpixelText( true );
	//		paint.setTextSize( 23 * Parameter.scale );
	//		FontMetrics fontMetrics = paint.getFontMetrics();
	//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
	//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
	//		if( highTem != null && lowTem != null && week != null )
	//		{
	//			//			canvas.drawText( highTem + "°" , 0 , posY - 15f , paint );
	//			//			paint.setTextSize( 20 * Parameter.scale );
	//			//			if( paint.measureText( highTem ) < paint.measureText( "-27" ) )
	//			//			{
	//			//				if( paint.measureText( lowTem ) < paint.measureText( "27" ) )
	//			//				{
	//			//					canvas.drawText( "/" + lowTem + "°" , 30 * Parameter.scale , posY - 15f * Parameter.scale , paint );
	//			//				}
	//			//				else if( paint.measureText( lowTem ) < paint.measureText( "-27" ) )
	//			//				{
	//			//					canvas.drawText( "/" + lowTem + "°" , 45 * Parameter.scale , posY - 15f * Parameter.scale , paint );
	//			//				}
	//			//				else
	//			//				{
	//			//					canvas.drawText( "/" + lowTem + "°" , 50 * Parameter.scale , posY - 15f * Parameter.scale , paint );
	//			//				}
	//			//			}
	//			//			else
	//			//			{
	//			//				canvas.drawText( "/" + lowTem + "°" , 55 * Parameter.scale , posY - 15f * Parameter.scale , paint );
	//			//			}
	//			canvas.drawText( highTem + "°/" + lowTem + "°" , 0 , posY - 15f * Parameter.scale , paint );
	//			canvas.drawText( week , paint.measureText( highTem + "°/" + lowTem + "°" ) - paint.measureText( week ) , posY + 15f * Parameter.scale , paint );
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	//	public static TextureRegion drawTemTextureRegion(
	//			MainAppContext appContext ,
	//			String temperture )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_TEM_WIDTH;
	//		float height = Parameter.WEATHER_TEM_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setARGB( 255 , 7 , 141 , 104 );
	//		paint.setSubpixelText( true );
	//		paint.setTextSize( 85f * Parameter.scale );
	//		FontMetrics fontMetrics = paint.getFontMetrics();
	//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
	//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
	//		Bitmap bt = null;
	//		Bitmap newbt = null;
	//		try
	//		{
	//			bt = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/temperture_1.png" ) );
	//			newbt = resizeBitmap( bt , (int)width , (int)height );
	//			canvas.drawBitmap( newbt , 0f , 0f , paint );
	//		}
	//		catch( IOException e )
	//		{
	//			e.printStackTrace();
	//		}
	//		Bitmap bt1 = null;
	//		Bitmap newbt1 = null;
	//		if( temperture != null )
	//		{
	//			char[] tmps = temperture.toCharArray();
	//			if( tmps.length == 3 )
	//			{
	//				if( tmps[0] == '-' )
	//				{
	//					canvas.drawText( "-" , width - ( 31f + 48f * 2 ) * Parameter.scale - paint.measureText( "-" ) , posY , paint );
	//					if( TmpPath( tmps[2] ) != null )
	//					{
	//						try
	//						{
	//							bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( TmpPath( tmps[2] ) ) );
	//							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
	//							canvas.drawBitmap( newbt1 , width - ( 31f + 48f ) * Parameter.scale , 10f * Parameter.scale , paint );
	//						}
	//						catch( IOException e )
	//						{
	//							e.printStackTrace();
	//						}
	//					}
	//					if( TmpPath( tmps[1] ) != null )
	//					{
	//						try
	//						{
	//							bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
	//							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
	//							canvas.drawBitmap( newbt1 , width - ( 31f + 48f * 2 ) * Parameter.scale , 10f * Parameter.scale , paint );
	//						}
	//						catch( IOException e )
	//						{
	//							e.printStackTrace();
	//						}
	//					}
	//				}
	//			}
	//			else if( tmps.length == 2 )
	//			{
	//				if( tmps[0] == '-' )
	//				{
	//					canvas.drawText( "-" , width - ( 31f + 47f ) * Parameter.scale - paint.measureText( "-" ) , posY , paint );
	//					if( TmpPath( tmps[1] ) != null )
	//					{
	//						try
	//						{
	//							bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
	//							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
	//							canvas.drawBitmap( newbt1 , width - ( 31f + 48f ) * Parameter.scale , 10f * Parameter.scale , paint );
	//						}
	//						catch( IOException e )
	//						{
	//							e.printStackTrace();
	//						}
	//					}
	//				}
	//				else
	//				{
	//					if( TmpPath( tmps[1] ) != null )
	//					{
	//						try
	//						{
	//							bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
	//							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
	//							canvas.drawBitmap( newbt1 , width - ( 31f + 48f ) * Parameter.scale , 10f * Parameter.scale , paint );
	//						}
	//						catch( IOException e )
	//						{
	//							e.printStackTrace();
	//						}
	//					}
	//					if( TmpPath( tmps[0] ) != null )
	//					{
	//						try
	//						{
	//							bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( TmpPath( tmps[0] ) ) );
	//							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
	//							canvas.drawBitmap( newbt1 , width - ( 31f + 48f * 2 ) * Parameter.scale , 10f * Parameter.scale , paint );
	//						}
	//						catch( IOException e )
	//						{
	//							e.printStackTrace();
	//						}
	//					}
	//				}
	//			}
	//			else if( tmps.length == 1 )
	//			{
	//				if( TmpPath( tmps[0] ) != null )
	//				{
	//					try
	//					{
	//						bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( TmpPath( tmps[0] ) ) );
	//						newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
	//						canvas.drawBitmap( newbt1 , width - ( 31f + 48f ) * Parameter.scale , 10f * Parameter.scale , paint );
	//					}
	//					catch( IOException e )
	//					{
	//						e.printStackTrace();
	//					}
	//				}
	//			}
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		if( bt != null )
	//		{
	//			bt.recycle();
	//		}
	//		if( newbt != null )
	//		{
	//			newbt.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	public static TextureRegion drawCurveTopTextureRegion(
			MainAppContext appContext ,
			String icon1 ,
			String icon2 ,
			String icon3 ,
			String icon4 ,
			String icon5 ,
			int tmp1 ,
			int tmp2 ,
			int tmp3 ,
			int tmp4 ,
			int tmp5 )
	{
		Bitmap backImage = null;
		float width = Parameter.WEATHER_ROUND_WIDTH;
		float height = Parameter.WEATHER_ROUND_HEIGHT;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setARGB( 255 , 246 , 91 , 8 );
		paint.setSubpixelText( true );
		paint.setStrokeWidth( 2f * WidgetWeatherClock.scale );
//		if( Utils3D.getScreenWidth() < 500 )
//		{
//			paint.setTextSize( 15f );
//		}
//		else
//		{
//			paint.setTextSize( 20f * WidgetWeatherClock.scale );
//		}
		paint.setTextSize( 20f * WidgetWeatherClock.scale );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		int max = ReturnMaxInFive( tmp1 , tmp2 , tmp3 , tmp4 , tmp5 );
		int min = ReturnMinInFive( tmp1 , tmp2 , tmp3 , tmp4 , tmp5 );
		int difference = max - min;
		Bitmap bit = null;
		Bitmap newbit = null;
		Bitmap bit1 = null;
		Bitmap newbit1 = null;
		String path1 = null;
		String path2 = null;
		String path3 = null;
		String path4 = null;
		String path5 = null;
		if( icon1 != null && icon2 != null && icon3 != null && icon4 != null && icon5 != null )
		{
			if( !DefaultLayout.enable_google_version )
			{
				path1 = StringForStringPath( icon1 );
				path2 = StringForStringPath( icon2 );
				path3 = StringForStringPath( icon3 );
				path4 = StringForStringPath( icon4 );
				path5 = StringForStringPath( icon5 );
			}
			else
			{
				path1 = codeForSmallPath( icon1 , PATHWeatherSmall );
				path2 = codeForSmallPath( icon2 , PATHWeatherSmall );
				path3 = codeForSmallPath( icon3 , PATHWeatherSmall );
				path4 = codeForSmallPath( icon4 , PATHWeatherSmall );
				path5 = codeForSmallPath( icon5 , PATHWeatherSmall );
			}
		}
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		Bitmap bt2 = null;
		Bitmap newbt2 = null;
		Bitmap bt3 = null;
		Bitmap newbt3 = null;
		Bitmap bt4 = null;
		Bitmap newbt4 = null;
		Bitmap bt5 = null;
		Bitmap newbt5 = null;
		float firstLineX = ( width - Parameter.WEATHER_CURVEICON_WIDTH ) / 2 + Parameter.WEATHER_CURVEICONSMALL_WIDTH / 2;
		float firstDotX = ( width - Parameter.WEATHER_CURVEICON_WIDTH ) / 2 + ( Parameter.WEATHER_CURVEICONSMALL_WIDTH - Parameter.WEATHER_DOT_WIDTH ) / 2;
		float firstIconX = ( width - Parameter.WEATHER_CURVEICON_WIDTH ) / 2;
		float everyX = ( Parameter.WEATHER_CURVEICON_WIDTH - Parameter.WEATHER_CURVEICONSMALL_WIDTH ) / 4;
		float nowCurveY = Parameter.WEATHER_CURVETOP_HEIGHT - Parameter.WEATHER_FONT_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 - Parameter.WEATHER_CURVEICON_HEIGHT;
		float firstLineY = Parameter.WEATHER_FONT_HEIGHT;
		float ch = 47 * WidgetWeatherClock.scale;
		if( path1 != null && path2 != null && path3 != null && path4 != null && path5 != null )
		{
			if( difference == 0 )
			{
				try
				{
					bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path1 ) );
					newbt1 = resizeBitmap( bt1 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap( newbt1 , firstIconX , posY + ch - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bt2 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path2 ) );
					newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap( newbt2 , firstIconX + everyX , posY + ch - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bt3 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path3 ) );
					newbt3 = resizeBitmap( bt3 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap( newbt3 , firstIconX + everyX * 2 , posY + ch - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bt4 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path4 ) );
					newbt4 = resizeBitmap( bt4 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap( newbt4 , firstIconX + everyX * 3 , posY + ch - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bt5 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path5 ) );
					newbt5 = resizeBitmap( bt5 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap( newbt5 , firstIconX + everyX * 4 , posY + ch - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				canvas.drawLine( firstLineX , posY + ch , firstLineX + everyX , posY + ch , paint );
				canvas.drawLine( firstLineX + everyX , posY + ch , firstLineX + everyX * 2 , posY + ch , paint );
				canvas.drawLine( firstLineX + everyX * 2 , posY + ch , firstLineX + everyX * 3 , posY + ch , paint );
				canvas.drawLine( firstLineX + everyX * 3 , posY + ch , firstLineX + everyX * 4 , posY + ch , paint );
				try
				{
					bit1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dottoptoday.png" ) );
					newbit1 = resizeBitmap( bit1 , (int)Parameter.WEATHER_DOTTODAY_WIDTH , (int)Parameter.WEATHER_DOTTODAY_HEIGHT );
					canvas.drawBitmap( newbit1 , firstDotX , posY + ch - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bit = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dottop.png" ) );
					newbit = resizeBitmap( bit , (int)Parameter.WEATHER_DOT_WIDTH , (int)Parameter.WEATHER_DOT_HEIGHT );
					canvas.drawBitmap( newbit , firstDotX + everyX , posY + ch - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 2 , posY + ch - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 3 , posY + ch - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 4 , posY + ch - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				canvas.drawText( "" + tmp1 + "°" , firstDotX , posY + ch + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp2 + "°" , firstDotX + everyX , posY + ch + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp3 + "°" , firstDotX + everyX * 2 , posY + ch + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp4 + "°" , firstDotX + everyX * 3 , posY + ch + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp5 + "°" , firstDotX + everyX * 4 , posY + ch + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			}
			else if( difference > 10 )
			{
				try
				{
					bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path1 ) );
					newbt1 = resizeBitmap( bt1 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt1 ,
							firstIconX ,
							height - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt2 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path2 ) );
					newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt2 ,
							firstIconX + everyX ,
							height - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt3 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path3 ) );
					newbt3 = resizeBitmap( bt3 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt3 ,
							firstIconX + everyX * 2 ,
							height - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt4 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path4 ) );
					newbt4 = resizeBitmap( bt4 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt4 ,
							firstIconX + everyX * 3 ,
							height - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt5 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path5 ) );
					newbt5 = resizeBitmap( bt5 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt5 ,
							firstIconX + everyX * 4 ,
							height - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				canvas.drawLine(
						firstLineX ,
						height - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) ,
						firstLineX + everyX ,
						height - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) ,
						paint );
				canvas.drawLine(
						firstLineX + everyX ,
						height - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) ,
						firstLineX + everyX * 2 ,
						height - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) ,
						paint );
				canvas.drawLine(
						firstLineX + everyX * 2 ,
						height - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) ,
						firstLineX + everyX * 3 ,
						height - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) ,
						paint );
				canvas.drawLine(
						firstLineX + everyX * 3 ,
						height - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) ,
						firstLineX + everyX * 4 ,
						height - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) ,
						paint );
				try
				{
					bit1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dottoptoday.png" ) );
					newbit1 = resizeBitmap( bit1 , (int)Parameter.WEATHER_DOTTODAY_WIDTH , (int)Parameter.WEATHER_DOTTODAY_HEIGHT );
					canvas.drawBitmap( newbit1 , firstDotX , height - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bit = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dottop.png" ) );
					newbit = resizeBitmap( bit , (int)Parameter.WEATHER_DOT_WIDTH , (int)Parameter.WEATHER_DOT_HEIGHT );
					canvas.drawBitmap( newbit , firstDotX + everyX , height - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 2 , height - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 3 , height - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 4 , height - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				canvas.drawText( "" + tmp1 + "°" , firstDotX , height - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp2 + "°" , firstDotX + everyX , height - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp3 + "°" , firstDotX + everyX * 2 , height - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp4 + "°" , firstDotX + everyX * 3 , height - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp5 + "°" , firstDotX + everyX * 4 , height - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			}
			else
			{
				try
				{
					bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path1 ) );
					newbt1 = resizeBitmap( bt1 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt1 ,
							firstIconX ,
							height - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt2 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path2 ) );
					newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt2 ,
							firstIconX + everyX ,
							height - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt3 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path3 ) );
					newbt3 = resizeBitmap( bt3 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt3 ,
							firstIconX + everyX * 2 ,
							height - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt4 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path4 ) );
					newbt4 = resizeBitmap( bt4 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt4 ,
							firstIconX + everyX * 3 ,
							height - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
					bt5 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path5 ) );
					newbt5 = resizeBitmap( bt5 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
					canvas.drawBitmap(
							newbt5 ,
							firstIconX + everyX * 4 ,
							height - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_CURVEICONSMALL_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2 ,
							paint );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				canvas.drawLine(
						firstLineX ,
						height - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						firstLineX + everyX ,
						height - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						paint );
				canvas.drawLine(
						firstLineX + everyX ,
						height - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						firstLineX + everyX * 2 ,
						height - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						paint );
				canvas.drawLine(
						firstLineX + everyX * 2 ,
						height - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						firstLineX + everyX * 3 ,
						height - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						paint );
				canvas.drawLine(
						firstLineX + everyX * 3 ,
						height - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						firstLineX + everyX * 4 ,
						height - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) ,
						paint );
				try
				{
					bit1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dottoptoday.png" ) );
					newbit1 = resizeBitmap( bit1 , (int)Parameter.WEATHER_DOTTODAY_WIDTH , (int)Parameter.WEATHER_DOTTODAY_HEIGHT );
					canvas.drawBitmap( newbit1 , firstDotX , height - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					bit = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dottop.png" ) );
					newbit = resizeBitmap( bit , (int)Parameter.WEATHER_DOT_WIDTH , (int)Parameter.WEATHER_DOT_HEIGHT );
					canvas.drawBitmap( newbit , firstDotX + everyX , height - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 2 , height - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 3 , height - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
					canvas.drawBitmap( newbit , firstDotX + everyX * 4 , height - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				canvas.drawText( "" + tmp1 + "°" , firstDotX , height - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp2 + "°" , firstDotX + everyX , height - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp3 + "°" , firstDotX + everyX * 2 , height - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp4 + "°" , firstDotX + everyX * 3 , height - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
				canvas.drawText( "" + tmp5 + "°" , firstDotX + everyX * 4 , height - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bit != null )
		{
			bit.recycle();
		}
		if( newbit != null )
		{
			newbit.recycle();
		}
		if( bt1 != null )
		{
			bt1.recycle();
		}
		if( newbt1 != null )
		{
			newbt1.recycle();
		}
		if( bt2 != null )
		{
			bt2.recycle();
		}
		if( newbt2 != null )
		{
			newbt2.recycle();
		}
		if( bt3 != null )
		{
			bt3.recycle();
		}
		if( newbt3 != null )
		{
			newbt3.recycle();
		}
		if( bt4 != null )
		{
			bt4.recycle();
		}
		if( newbt4 != null )
		{
			newbt4.recycle();
		}
		if( bt5 != null )
		{
			bt5.recycle();
		}
		if( newbt5 != null )
		{
			newbt5.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawCurveBottomTextureRegion(
			MainAppContext appContext ,
			int tmp1 ,
			int tmp2 ,
			int tmp3 ,
			int tmp4 ,
			int tmp5 ,
			String date1 ,
			String date2 ,
			String date3 ,
			String date4 ,
			String date5 )
	{
		Bitmap backImage = null;
		float width = Parameter.WEATHER_ROUND_WIDTH;
		float height = Parameter.WEATHER_ROUND_HEIGHT;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setARGB( 255 , 0 , 181 , 132 );
		paint.setSubpixelText( true );
		paint.setStrokeWidth( 2f * WidgetWeatherClock.scale  );
//		if( Utils3D.getScreenWidth() < 500 )
//		{
//			paint.setTextSize( 15f );
//		}
//		else
//		{
//			paint.setTextSize( 20f * WidgetWeatherClock.scale  );
//		}
		paint.setTextSize( 20f * WidgetWeatherClock.scale  );
		//		FontMetrics fontMetrics = paint.getFontMetrics();
		//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		int max = ReturnMaxInFive( tmp1 , tmp2 , tmp3 , tmp4 , tmp5 );
		int min = ReturnMinInFive( tmp1 , tmp2 , tmp3 , tmp4 , tmp5 );
		int difference = max - min;
		Bitmap bt = null;
		Bitmap newbt = null;
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		float firstLineX = ( width - Parameter.WEATHER_CURVEICON_WIDTH ) / 2 + Parameter.WEATHER_CURVEICONSMALL_WIDTH / 2;
		float firstDotX = ( width - Parameter.WEATHER_CURVEICON_WIDTH ) / 2 + ( Parameter.WEATHER_CURVEICONSMALL_WIDTH - Parameter.WEATHER_DOT_WIDTH ) / 2;
		float everyX = ( Parameter.WEATHER_CURVEICON_WIDTH - Parameter.WEATHER_CURVEICONSMALL_WIDTH ) / 4;
		float nowCurveY = Parameter.WEATHER_CURVEBOTTOM_HEIGHT - Parameter.WEATHER_FONT_HEIGHT - Parameter.WEATHER_DOT_HEIGHT / 2;
		float firstLineY = 15 * WidgetWeatherClock.scale;
		float firstLY = 55 * WidgetWeatherClock.scale;
		if( difference == 0 )
		{
			canvas.drawLine( firstLineX , firstLY , firstLineX + everyX , firstLY , paint );
			canvas.drawLine( firstLineX + everyX , firstLY , firstLineX + everyX * 2 , firstLY , paint );
			canvas.drawLine( firstLineX + everyX * 2 , firstLY , firstLineX + everyX * 3 , firstLY , paint );
			canvas.drawLine( firstLineX + everyX * 3 , firstLY , firstLineX + everyX * 4 , firstLY , paint );
			try
			{
				bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dotbottomtoday.png" ) );
				newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_DOTTODAY_WIDTH , (int)Parameter.WEATHER_DOTTODAY_HEIGHT );
				canvas.drawBitmap( newbt1 , firstDotX , firstLY - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				bt = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dotbottom.png" ) );
				newbt = resizeBitmap( bt , (int)Parameter.WEATHER_DOT_WIDTH , (int)Parameter.WEATHER_DOT_HEIGHT );
				canvas.drawBitmap( newbt , firstDotX + everyX , firstLY - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				canvas.drawBitmap( newbt , firstDotX + everyX * 2 , firstLY - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				canvas.drawBitmap( newbt , firstDotX + everyX * 3 , firstLY - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				canvas.drawBitmap( newbt , firstDotX + everyX * 4 , firstLY - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			paint.setARGB( 255 , 2 , 182 , 133 );
			canvas.drawText( "" + tmp1 + "°" , firstDotX , firstLY + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			canvas.drawText( "" + tmp2 + "°" , firstDotX + everyX , firstLY + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			canvas.drawText( "" + tmp3 + "°" , firstDotX + everyX * 2 , firstLY + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			canvas.drawText( "" + tmp4 + "°" , firstDotX + everyX * 3 , firstLY + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			canvas.drawText( "" + tmp5 + "°" , firstDotX + everyX * 4 , firstLY + Parameter.WEATHER_DOT_MyHEIGHT , paint );
		}
		else if( difference > 10 )
		{
			canvas.drawLine(
					firstLineX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) ,
					firstLineX + everyX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) ,
					paint );
			canvas.drawLine(
					firstLineX + everyX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) ,
					firstLineX + everyX * 2 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) ,
					paint );
			canvas.drawLine(
					firstLineX + everyX * 2 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) ,
					firstLineX + everyX * 3 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) ,
					paint );
			canvas.drawLine(
					firstLineX + everyX * 3 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) ,
					firstLineX + everyX * 4 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) ,
					paint );
			try
			{
				bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dotbottomtoday.png" ) );
				newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_DOTTODAY_WIDTH , (int)Parameter.WEATHER_DOTTODAY_HEIGHT );
				canvas.drawBitmap( newbt1 , firstDotX , Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				bt = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dotbottom.png" ) );
				newbt = resizeBitmap( bt , (int)Parameter.WEATHER_DOT_WIDTH , (int)Parameter.WEATHER_DOT_HEIGHT );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX * 2 ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX * 3 ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX * 4 ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			paint.setARGB( 255 , 2 , 182 , 133 );
			canvas.drawText( "" + tmp1 + "°" , firstDotX , Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp1 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			canvas.drawText(
					"" + tmp2 + "°" ,
					firstDotX + everyX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
			canvas.drawText(
					"" + tmp3 + "°" ,
					firstDotX + everyX * 2 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
			canvas.drawText(
					"" + tmp4 + "°" ,
					firstDotX + everyX * 3 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
			canvas.drawText(
					"" + tmp5 + "°" ,
					firstDotX + everyX * 4 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp5 - min ) * ( nowCurveY / difference ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
		}
		else
		{
			canvas.drawLine(
					firstLineX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					firstLineX + everyX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					paint );
			canvas.drawLine(
					firstLineX + everyX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					firstLineX + everyX * 2 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					paint );
			canvas.drawLine(
					firstLineX + everyX * 2 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					firstLineX + everyX * 3 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					paint );
			canvas.drawLine(
					firstLineX + everyX * 3 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					firstLineX + everyX * 4 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) ,
					paint );
			try
			{
				bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dotbottomtoday.png" ) );
				newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_DOTTODAY_WIDTH , (int)Parameter.WEATHER_DOTTODAY_HEIGHT );
				canvas.drawBitmap( newbt1 , firstDotX , Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 , paint );
				bt = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( PATH + "theme/image/dotbottom.png" ) );
				newbt = resizeBitmap( bt , (int)Parameter.WEATHER_DOT_WIDTH , (int)Parameter.WEATHER_DOT_HEIGHT );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX * 2 ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX * 3 ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
				canvas.drawBitmap(
						newbt ,
						firstDotX + everyX * 4 ,
						Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) - Parameter.WEATHER_DOT_HEIGHT / 2 ,
						paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			paint.setARGB( 255 , 2 , 182 , 133 );
			canvas.drawText( "" + tmp1 + "°" , firstDotX , Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp1 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT , paint );
			canvas.drawText(
					"" + tmp2 + "°" ,
					firstDotX + everyX ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp2 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
			canvas.drawText(
					"" + tmp3 + "°" ,
					firstDotX + everyX * 2 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp3 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
			canvas.drawText(
					"" + tmp4 + "°" ,
					firstDotX + everyX * 3 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp4 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
			canvas.drawText(
					"" + tmp5 + "°" ,
					firstDotX + everyX * 4 ,
					Parameter.WEATHER_CURVEBOTTOM_HEIGHT - ( firstLineY + ( tmp5 - max + 10 ) * ( nowCurveY / 10 ) ) + Parameter.WEATHER_DOT_MyHEIGHT ,
					paint );
		}
		paint.setARGB( 255 , 79 , 90 , 107 );
//		if( Utils3D.getScreenWidth() < 500 )
//		{
//			paint.setTextSize( 15f );
//		}
//		else
//		{
//			paint.setTextSize( 20f * WidgetWeatherClock.scale );
//		}
		paint.setTextSize( 20f * WidgetWeatherClock.scale );
		if( date1 != null && date2 != null && date3 != null && date4 != null && date5 != null )
		{
			if( !DefaultLayout.enable_google_version )
			{
				canvas.drawText( date1 , 58 * WidgetWeatherClock.scale , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date2 , 58 * WidgetWeatherClock.scale + ( Parameter.WEATHER_CURVEDATE_WIDTH - paint.measureText( "今天" ) ) / 4 , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date3 , 58 * WidgetWeatherClock.scale + ( Parameter.WEATHER_CURVEDATE_WIDTH - paint.measureText( "今天" ) ) / 4 * 2 , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date4 , 58 * WidgetWeatherClock.scale + ( Parameter.WEATHER_CURVEDATE_WIDTH - paint.measureText( "今天" ) ) / 4 * 3 , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date5 , 58 * WidgetWeatherClock.scale + ( Parameter.WEATHER_CURVEDATE_WIDTH - paint.measureText( "今天" ) ) / 4 * 4 , 130 * WidgetWeatherClock.scale , paint );
			}
			else
			{
				canvas.drawText( date1 , firstDotX , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date2 , firstDotX + everyX , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date3 , firstDotX + everyX * 2 , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date4 , firstDotX + everyX * 3 , 130 * WidgetWeatherClock.scale , paint );
				canvas.drawText( date5 , firstDotX + everyX * 4 , 130 * WidgetWeatherClock.scale , paint );
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bt != null )
		{
			bt.recycle();
		}
		if( newbt != null )
		{
			newbt.recycle();
		}
		if( bt1 != null )
		{
			bt1.recycle();
		}
		if( newbt1 != null )
		{
			newbt1.recycle();
		}
		return newTextureRegion;
	}
	
	//	public static TextureRegion drawCurveIconTextureRegion(
	//			MainAppContext appContext ,
	//			String icon1 ,
	//			String icon2 ,
	//			String icon3 ,
	//			String icon4 ,
	//			String icon5 )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_CURVEICON_WIDTH;
	//		float height = Parameter.WEATHER_CURVEICON_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setSubpixelText( true );
	//		String path1 = null;
	//		String path2 = null;
	//		String path3 = null;
	//		String path4 = null;
	//		String path5 = null;
	//		if( icon1 != null && icon2 != null && icon3 != null && icon4 != null && icon5 != null )
	//		{
	//			path1 = StringForStringPath( icon1 );
	//			path2 = StringForStringPath( icon2 );
	//			path3 = StringForStringPath( icon3 );
	//			path4 = StringForStringPath( icon4 );
	//			path5 = StringForStringPath( icon5 );
	//		}
	//		Bitmap bt1 = null;
	//		Bitmap newbt1 = null;
	//		Bitmap bt2 = null;
	//		Bitmap newbt2 = null;
	//		Bitmap bt3 = null;
	//		Bitmap newbt3 = null;
	//		Bitmap bt4 = null;
	//		Bitmap newbt4 = null;
	//		Bitmap bt5 = null;
	//		Bitmap newbt5 = null;
	//		if( path1 != null && path2 != null && path3 != null && path4 != null && path5 != null )
	//		{
	//			try
	//			{
	//				bt1 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path1 ) );
	//				newbt1 = resizeBitmap( bt1 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
	//				canvas.drawBitmap( newbt1 , 0 , ( height - Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) / 2 , paint );
	//				bt2 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path2 ) );
	//				newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
	//				canvas.drawBitmap( newbt2 , ( width - Parameter.WEATHER_CURVEICONSMALL_WIDTH ) / 4 , ( height - Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) / 2 , paint );
	//				bt3 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path3 ) );
	//				newbt3 = resizeBitmap( bt3 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
	//				canvas.drawBitmap( newbt3 , ( width - Parameter.WEATHER_CURVEICONSMALL_WIDTH ) / 4 * 2 , ( height - Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) / 2 , paint );
	//				bt4 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path4 ) );
	//				newbt4 = resizeBitmap( bt4 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
	//				canvas.drawBitmap( newbt4 , ( width - Parameter.WEATHER_CURVEICONSMALL_WIDTH ) / 4 * 3 , ( height - Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) / 2 , paint );
	//				bt5 = BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( path5 ) );
	//				newbt5 = resizeBitmap( bt5 , (int)( Parameter.WEATHER_CURVEICONSMALL_WIDTH ) , (int)( Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) );
	//				canvas.drawBitmap( newbt5 , ( width - Parameter.WEATHER_CURVEICONSMALL_WIDTH ) / 4 * 4 , ( height - Parameter.WEATHER_CURVEICONSMALL_HEIGHT ) / 2 , paint );
	//			}
	//			catch( IOException e )
	//			{
	//				e.printStackTrace();
	//			}
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		if( bt1 != null )
	//		{
	//			bt1.recycle();
	//		}
	//		if( newbt1 != null )
	//		{
	//			newbt1.recycle();
	//		}
	//		if( bt2 != null )
	//		{
	//			bt2.recycle();
	//		}
	//		if( newbt2 != null )
	//		{
	//			newbt2.recycle();
	//		}
	//		if( bt3 != null )
	//		{
	//			bt3.recycle();
	//		}
	//		if( newbt3 != null )
	//		{
	//			newbt3.recycle();
	//		}
	//		if( bt4 != null )
	//		{
	//			bt4.recycle();
	//		}
	//		if( newbt4 != null )
	//		{
	//			newbt4.recycle();
	//		}
	//		if( bt5 != null )
	//		{
	//			bt5.recycle();
	//		}
	//		if( newbt5 != null )
	//		{
	//			newbt5.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	//	public static TextureRegion drawCurveDateTextureRegion(
	//			MainAppContext appContext ,
	//			String date1 ,
	//			String date2 ,
	//			String date3 ,
	//			String date4 ,
	//			String date5 )
	//	{
	//		Bitmap backImage = null;
	//		float width = Parameter.WEATHER_CURVEDATE_WIDTH;
	//		float height = Parameter.WEATHER_CURVEDATE_HEIGHT;
	//		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
	//		Canvas canvas = new Canvas( backImage );
	//		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
	//		Paint paint = new Paint();
	//		paint.setAntiAlias( true );// 防锯齿
	//		paint.setDither( true );// 防抖动
	//		paint.setARGB( 255 , 79 , 90 , 107 );
	//		paint.setSubpixelText( true );
	//		paint.setTextSize( 16f * Parameter.scale );
	//		FontMetrics fontMetrics = paint.getFontMetrics();
	//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
	//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
	//		if( date1 != null && date2 != null && date3 != null && date4 != null && date5 != null )
	//		{
	//			canvas.drawText( date1 , 0 , posY , paint );
	//			canvas.drawText( date2 , ( width - paint.measureText( "今天" ) ) / 4 , posY , paint );
	//			canvas.drawText( date3 , ( width - paint.measureText( "今天" ) ) / 4 * 2 , posY , paint );
	//			canvas.drawText( date4 , ( width - paint.measureText( "今天" ) ) / 4 * 3 , posY , paint );
	//			canvas.drawText( date5 , ( width - paint.measureText( "今天" ) ) / 4 * 4 , posY , paint );
	//		}
	//		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
	//		if( backImage != null )
	//		{
	//			backImage.recycle();
	//		}
	//		return newTextureRegion;
	//	}
	//	
	public static String splitString(
			String name )
	{
		if( name != null )
		{
			String[] names = name.split( "-" );
			if( names.length == 2 )
			{
				return names[0];
			}
		}
		return null;
	}
	
	public static String changeWeek(
			int week )
	{
		if( week >= 0 )
		{
			String weekname = "";
			switch( week )
			{
				case 0:
					weekname = "星期日";
					break;
				case 1:
					weekname = "星期一";
					break;
				case 2:
					weekname = "星期二";
					break;
				case 3:
					weekname = "星期三";
					break;
				case 4:
					weekname = "星期四";
					break;
				case 5:
					weekname = "星期五";
					break;
				case 6:
					weekname = "星期六";
					break;
				default:
					weekname = "未知";
					break;
			}
			return weekname;
		}
		return null;
	}
	
	public static String changeForWeek(
			int week )
	{
		if( week >= 0 )
		{
			String weekname = "";
			switch( week )
			{
				case 0:
					weekname = "周日";
					break;
				case 1:
					weekname = "周一";
					break;
				case 2:
					weekname = "周二";
					break;
				case 3:
					weekname = "周三";
					break;
				case 4:
					weekname = "周四";
					break;
				case 5:
					weekname = "周五";
					break;
				case 6:
					weekname = "周六";
					break;
				default:
					weekname = "未知";
					break;
			}
			return weekname;
		}
		return null;
	}
	
	public static String splitTem(
			String tem )
	{
		if( tem != null )
		{
			String[] tems = tem.split( "°" );
			if( tems.length == 2 )
			{
				return tems[0];
			}
		}
		return null;
	}
	
	public static String ResolveAir(
			String air )
	{
		if( air != null )
		{
			Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
			int hour = c.get( Calendar.HOUR_OF_DAY );
			String weathericon = null;
			String[] names = air.split( "转" );
			if( names.length == 2 )
			{
				if( hour >= 18 )
				{
					weathericon = names[1];
				}
				else
				{
					weathericon = names[0];
				}
			}
			else
			{
				weathericon = air;
			}
			return weathericon;
		}
		return null;
	}
	
	//	public static TextureRegion AccordToNameForBit(
	//			MainAppContext maincontext ,
	//			String name )
	//	{
	//		if( name != null )
	//		{
	//			String bitPath = null;
	//			Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
	//			int hour = c.get( Calendar.HOUR_OF_DAY );
	//			String weathericon = null;
	//			String[] names = name.split( "转" );
	//			if( names.length == 2 )
	//			{
	//				if( hour >= 18 )
	//				{
	//					weathericon = names[1];
	//				}
	//				else
	//				{
	//					weathericon = names[0];
	//				}
	//			}
	//			else
	//			{
	//				weathericon = name;
	//			}
	//			if( hour >= 18 )
	//			{
	//				if( weathericon.contains( "云" ) || weathericon.contains( "阴" ) )
	//				{
	//					bitPath = PATH + "theme/weather/latecloudy.png";
	//				}
	//				else if( weathericon.contains( "雨" ) )
	//				{
	//					bitPath = PATH + "theme/weather/rainshowerslate.png";
	//				}
	//				else if( weathericon.contains( "雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/snowshowerslate.png";
	//				}
	//				else
	//				{
	//					bitPath = PATH + "theme/weather/reaching.png";
	//				}
	//			}
	//			else
	//			{
	//				if( weathericon.contains( "暴雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/baosnow.png";
	//				}
	//				else if( weathericon.contains( "雷阵雨" ) )
	//				{
	//					bitPath = PATH + "theme/weather/thunderstorms.png";
	//				}
	//				else if( weathericon.contains( "雨加雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/sleet.png";
	//				}
	//				else if( weathericon.contains( "阵雨" ) )
	//				{
	//					bitPath = PATH + "theme/weather/zhenrain.png";
	//				}
	//				else if( weathericon.contains( "阵雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/zhensnow.png";
	//				}
	//				else if( weathericon.contains( "沙" ) )
	//				{
	//					bitPath = PATH + "theme/weather/sand.png";
	//				}
	//				else if( weathericon.contains( "雾" ) )
	//				{
	//					bitPath = PATH + "theme/weather/fog.png";
	//				}
	//				else if( weathericon.contains( "大雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/bigsnow.png";
	//				}
	//				else if( weathericon.contains( "大雨" ) || weathericon.contains( "暴雨" ) )
	//				{
	//					bitPath = PATH + "theme/weather/bigrain.png";
	//				}
	//				else if( weathericon.contains( "小雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/smallsnow.png";
	//				}
	//				else if( weathericon.contains( "小雨" ) )
	//				{
	//					bitPath = PATH + "theme/weather/smallrain.png";
	//				}
	//				else if( weathericon.contains( "中雪" ) )
	//				{
	//					bitPath = PATH + "theme/weather/middlesnow.png";
	//				}
	//				else if( weathericon.contains( "中雨" ) )
	//				{
	//					bitPath = PATH + "theme/weather/middlerain.png";
	//				}
	//				else if( weathericon.contains( "云" ) )
	//				{
	//					bitPath = PATH + "theme/weather/cloudy.png";
	//				}
	//				else if( weathericon.contains( "阴" ) )
	//				{
	//					bitPath = PATH + "theme/weather/cloudyday.png";
	//				}
	//				else if( weathericon.contains( "晴" ) )
	//				{
	//					bitPath = PATH + "theme/weather/sunny.png";
	//				}
	//				else
	//				{
	//					bitPath = PATH + "theme/weather/unknow.png";
	//				}
	//			}
	//			try
	//			{
	//				BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( bitPath ) ) , true );
	//				bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
	//				TextureRegion mBackRegion = new TextureRegion( bt );
	//				return mBackRegion;
	//			}
	//			catch( IOException e )
	//			{
	//				e.printStackTrace();
	//			}
	//			return null;
	//		}
	//		return null;
	//	}
	public static TextureRegion drawWeatherTopCity(
			MainAppContext maincontext ,
			String weatherName ,
			String city )
	{
		Bitmap backImage = null;
		float width = Parameter.WEATHER_ROUND_WIDTH;
		float height = Parameter.WEATHER_ROUND_HEIGHT;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 82 , 94 , 116 );
		//		FontMetrics fontMetrics = paint.getFontMetrics();
		//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		//		Bitmap bt = null;
		//		Bitmap newbt = null;
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		Bitmap bt2 = null;
		Bitmap newbt2 = null;
		if( weatherName != null )
		{
			String bitPath = StringForPath( weatherName );
			try
			{
				bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( bitPath ) );
				newbt1 = resizeBitmap( bt1 , (int)( Parameter.WEATHER_ICON_WIDTH ) , (int)( Parameter.WEATHER_ICON_HEIGHT ) );
				canvas.drawBitmap( newbt1 , 44 * WidgetWeatherClock.scale , 68 * WidgetWeatherClock.scale , paint );
				paint.setTextSize( 30 * WidgetWeatherClock.scale );
				canvas.drawText( weatherName , 229 * WidgetWeatherClock.scale , 110 * WidgetWeatherClock.scale , paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		if( city != null )
		{
			try
			{
				bt2 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( PATH + "theme/image/city_1.png" ) );
				newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_NAME_WIDTH ) , (int)( Parameter.WEATHER_NAME_HEIGHT ) );
				canvas.drawBitmap( newbt2 , 229 * WidgetWeatherClock.scale , 132 * WidgetWeatherClock.scale , paint );
				paint.setTextSize( 24 * WidgetWeatherClock.scale );
				canvas.drawText( city , 254f * WidgetWeatherClock.scale , 158 * WidgetWeatherClock.scale , paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		//		if( bt != null )
		//		{
		//			bt.recycle();
		//		}
		//		if( newbt != null )
		//		{
		//			newbt.recycle();
		//		}
		if( bt1 != null )
		{
			bt1.recycle();
		}
		if( newbt1 != null )
		{
			newbt1.recycle();
		}
		if( bt2 != null )
		{
			bt2.recycle();
		}
		if( newbt2 != null )
		{
			newbt2.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawWeatherTopCityfor(
			MainAppContext maincontext ,
			String weatherName ,
			String city ,
			String weathercode )
	{
		Bitmap backImage = null;
		float width = Parameter.WEATHER_ROUND_WIDTH;
		float height = Parameter.WEATHER_ROUND_HEIGHT;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 82 , 94 , 116 );
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		Bitmap bt2 = null;
		Bitmap newbt2 = null;
		if( weathercode != null )
		{
			String bitPath = codeForPath( weathercode , PATHWeatherBig );
			try
			{
				bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( bitPath ) );
				newbt1 = resizeBitmap( bt1 , (int)( Parameter.WEATHER_ICON_WIDTH ) , (int)( Parameter.WEATHER_ICON_HEIGHT ) );
				canvas.drawBitmap( newbt1 , 35 * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		if( weatherName != null )
		{
			paint.setTextSize( 25 * WidgetWeatherClock.scale );
			String front = weatherName;
			String back = null;
			if( paint.measureText( front ) > Parameter.WEATHER_NAME_WIDTH + 40 * WidgetWeatherClock.scale )
			{
				while( paint.measureText( front ) > Parameter.WEATHER_NAME_WIDTH )
				{
					front = front.substring( 0 , front.length() - 1 );
				}
				back = weatherName.substring( front.length() , weatherName.length() );
				canvas.drawText( front , 225 * WidgetWeatherClock.scale , 100 * WidgetWeatherClock.scale , paint );
				canvas.drawText( back , 225 * WidgetWeatherClock.scale , 130 * WidgetWeatherClock.scale , paint );
			}
			else
			{
				canvas.drawText( weatherName , 225 * WidgetWeatherClock.scale , 130 * WidgetWeatherClock.scale , paint );
			}
		}
		if( city != null )
		{
			try
			{
				bt2 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( PATH + "theme/image/city_1.png" ) );
				newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_NAME_WIDTH ) , (int)( Parameter.WEATHER_NAME_HEIGHT ) );
				canvas.drawBitmap( newbt2 , 220 * WidgetWeatherClock.scale , 152 * WidgetWeatherClock.scale , paint );
				paint.setTextSize( 25 * WidgetWeatherClock.scale );
				if( paint.measureText( city ) > Parameter.WEATHER_NAME_WIDTH + 45 * WidgetWeatherClock.scale )
				{
					while( paint.measureText( city ) > Parameter.WEATHER_NAME_WIDTH + 45 * WidgetWeatherClock.scale - paint.measureText( "..." ) )
					{
						city = city.substring( 0 , city.length() - 1 );
					}
					city += "...";
				}
				canvas.drawText( city , 245f * WidgetWeatherClock.scale , 178 * WidgetWeatherClock.scale , paint );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bt1 != null )
		{
			bt1.recycle();
		}
		if( newbt1 != null )
		{
			newbt1.recycle();
		}
		if( bt2 != null )
		{
			bt2.recycle();
		}
		if( newbt2 != null )
		{
			newbt2.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawWeatherBottomTmp(
			MainAppContext maincontext ,
			String air ,
			String week ,
			String highTem ,
			String lowTem ,
			String temperture )
	{
		Bitmap backImage = null;
		float width = Parameter.WEATHER_ROUND_WIDTH;
		float height = Parameter.WEATHER_ROUND_HEIGHT;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 82 , 94 , 116 );
		//		FontMetrics fontMetrics = paint.getFontMetrics();
		//		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		//		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		//		Bitmap bt = null;
		//		Bitmap newbt = null;
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		Bitmap bt2 = null;
		Bitmap newbt2 = null;
		if( air != null )
		{
			paint.setTextSize( 25 * WidgetWeatherClock.scale );
			canvas.drawText( air , 40f * WidgetWeatherClock.scale , 30f * WidgetWeatherClock.scale , paint );
		}
		if( highTem != null && lowTem != null && week != null )
		{
			paint.setTextSize( 25 * WidgetWeatherClock.scale );
			canvas.drawText( highTem + "°/" + lowTem + "°" , 75 * WidgetWeatherClock.scale , 110f * WidgetWeatherClock.scale , paint );
			canvas.drawText( week , 75 * WidgetWeatherClock.scale + paint.measureText( highTem + "°/" + lowTem + "°" ) - paint.measureText( week ) , 140f * WidgetWeatherClock.scale , paint );
		}
		if( temperture != null )
		{
			paint.setARGB( 255 , 7 , 141 , 104 );
			paint.setTextSize( 85f * WidgetWeatherClock.scale );
			try
			{
				bt2 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( PATH + "theme/image/temperture_1.png" ) );
				newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_TEM_WIDTH ) , (int)( Parameter.WEATHER_TEM_HEIGHT ) );
				canvas.drawBitmap( newbt2 , 192 * WidgetWeatherClock.scale , 56 * WidgetWeatherClock.scale , paint );
			}
			catch( IOException e1 )
			{
				e1.printStackTrace();
			}
			char[] tmps = temperture.toCharArray();
			if( tmps.length == 3 )
			{
				if( tmps[0] == '-' )
				{
					canvas.drawText( "-" , ( 192 + 150 - 31 - 54 * 2 ) * WidgetWeatherClock.scale - paint.measureText( "-" ) , ( 66 + 64 ) * WidgetWeatherClock.scale , paint );
					if( TmpPath( tmps[2] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[2] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					if( TmpPath( tmps[1] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 * 2 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if( tmps.length == 2 )
			{
				if( tmps[0] == '-' )
				{
					canvas.drawText( "-" , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale - paint.measureText( "-" ) , ( 66 + 64 ) * WidgetWeatherClock.scale , paint );
					if( TmpPath( tmps[1] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					if( TmpPath( tmps[1] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					if( TmpPath( tmps[0] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[0] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 * 2 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if( tmps.length == 1 )
			{
				if( TmpPath( tmps[0] ) != null )
				{
					try
					{
						bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[0] ) ) );
						newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
						canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
				}
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bt1 != null )
		{
			bt1.recycle();
		}
		if( newbt1 != null )
		{
			newbt1.recycle();
		}
		if( bt2 != null )
		{
			bt2.recycle();
		}
		if( newbt2 != null )
		{
			newbt2.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawWeatherBottomTmpFor(
			MainAppContext maincontext ,
			String week ,
			String highTem ,
			String lowTem ,
			String temperture ,
			String tmptype ,
			String shidu )
	{
		Bitmap backImage = null;
		float width = Parameter.WEATHER_ROUND_WIDTH;
		float height = Parameter.WEATHER_ROUND_HEIGHT;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 82 , 94 , 116 );
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		Bitmap bt2 = null;
		Bitmap newbt2 = null;
		if( shidu != null )
		{
			paint.setTextSize( 25 * WidgetWeatherClock.scale );
			canvas.drawText( "Humidity : " + shidu + "%" , 40f * WidgetWeatherClock.scale , 30f * WidgetWeatherClock.scale , paint );
		}
		if( highTem != null && lowTem != null && week != null )
		{
			paint.setTextSize( 25 * WidgetWeatherClock.scale );
			canvas.drawText( highTem + "°/" + lowTem + "°" , 75 * WidgetWeatherClock.scale , 110f * WidgetWeatherClock.scale , paint );
			canvas.drawText( week , 75 * WidgetWeatherClock.scale + paint.measureText( highTem + "°/" + lowTem + "°" ) - paint.measureText( week ) , 140f * WidgetWeatherClock.scale , paint );
		}
		if( temperture != null && tmptype != null )
		{
			paint.setARGB( 255 , 7 , 141 , 104 );
			paint.setTextSize( 85f * WidgetWeatherClock.scale );
			try
			{
				if( tmptype.equals( "c" ) )
				{
					bt2 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( PATH + "theme/image/temperture_1.png" ) );
				}
				else if( tmptype.equals( "f" ) )
				{
					bt2 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( PATH + "theme/image/temperture_2.png" ) );
				}
				newbt2 = resizeBitmap( bt2 , (int)( Parameter.WEATHER_TEM_WIDTH ) , (int)( Parameter.WEATHER_TEM_HEIGHT ) );
				canvas.drawBitmap( newbt2 , 192 * WidgetWeatherClock.scale , 56 * WidgetWeatherClock.scale , paint );
			}
			catch( IOException e1 )
			{
				e1.printStackTrace();
			}
			char[] tmps = temperture.toCharArray();
			if( tmps.length == 3 )
			{
				if( tmps[0] == '-' )
				{
					canvas.drawText( "-" , ( 192 + 150 - 31 - 54 * 2 ) * WidgetWeatherClock.scale - paint.measureText( "-" ) , ( 66 + 64 ) * WidgetWeatherClock.scale , paint );
					if( TmpPath( tmps[2] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[2] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					if( TmpPath( tmps[1] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 * 2 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if( tmps.length == 2 )
			{
				if( tmps[0] == '-' )
				{
					canvas.drawText( "-" , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale - paint.measureText( "-" ) , ( 66 + 64 ) * WidgetWeatherClock.scale , paint );
					if( TmpPath( tmps[1] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					if( TmpPath( tmps[1] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[1] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					if( TmpPath( tmps[0] ) != null )
					{
						try
						{
							bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[0] ) ) );
							newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
							canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 * 2 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if( tmps.length == 1 )
			{
				if( TmpPath( tmps[0] ) != null )
				{
					try
					{
						bt1 = BitmapFactory.decodeStream( maincontext.mWidgetContext.getAssets().open( TmpPath( tmps[0] ) ) );
						newbt1 = resizeBitmap( bt1 , (int)Parameter.WEATHER_TMP_WIDTH , (int)Parameter.WEATHER_TMP_HEIGHT );
						canvas.drawBitmap( newbt1 , ( 192 + 150 - 31 - 54 ) * WidgetWeatherClock.scale , 66 * WidgetWeatherClock.scale , paint );
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
				}
			}
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bt1 != null )
		{
			bt1.recycle();
		}
		if( newbt1 != null )
		{
			newbt1.recycle();
		}
		if( bt2 != null )
		{
			bt2.recycle();
		}
		if( newbt2 != null )
		{
			newbt2.recycle();
		}
		return newTextureRegion;
	}
	
	public static String StringForStringPath(
			String name )
	{
		String bitPath = null;
		if( name != null )
		{
			String weathericon = null;
			String[] names = name.split( "转" );
			if( names.length == 2 )
			{
				weathericon = names[0];
			}
			else
			{
				weathericon = name;
			}
			if( weathericon.contains( "飓风" ) )
			{
				bitPath = PATH + "theme/weathersmall/jufeng.png";
			}
			if( weathericon.contains( "冰雹" ) )
			{
				bitPath = PATH + "theme/weathersmall/bingbao.png";
			}
			if( weathericon.contains( "暴雪" ) )
			{
				bitPath = PATH + "theme/weathersmall/baosnow.png";
			}
			else if( weathericon.contains( "雷阵雨" ) )
			{
				bitPath = PATH + "theme/weathersmall/thunderstorms.png";
			}
			else if( weathericon.contains( "雨加雪" ) )
			{
				bitPath = PATH + "theme/weathersmall/sleet.png";
			}
			else if( weathericon.contains( "阵雨" ) )
			{
				bitPath = PATH + "theme/weathersmall/zhenrain.png";
			}
			else if( weathericon.contains( "阵雪" ) )
			{
				bitPath = PATH + "theme/weathersmall/zhensnow.png";
			}
			else if( weathericon.contains( "沙" ) )
			{
				bitPath = PATH + "theme/weathersmall/sand.png";
			}
			else if( weathericon.contains( "雾" ) )
			{
				bitPath = PATH + "theme/weathersmall/fog.png";
			}
			else if( weathericon.contains( "大雪" ) )
			{
				bitPath = PATH + "theme/weathersmall/bigsnow.png";
			}
			else if( weathericon.contains( "大雨" ) || weathericon.contains( "暴雨" ) )
			{
				bitPath = PATH + "theme/weathersmall/bigrain.png";
			}
			else if( weathericon.contains( "小雪" ) )
			{
				bitPath = PATH + "theme/weathersmall/smallsnow.png";
			}
			else if( weathericon.contains( "小雨" ) )
			{
				bitPath = PATH + "theme/weathersmall/smallrain.png";
			}
			else if( weathericon.contains( "中雪" ) )
			{
				bitPath = PATH + "theme/weathersmall/middlesnow.png";
			}
			else if( weathericon.contains( "中雨" ) )
			{
				bitPath = PATH + "theme/weathersmall/middlerain.png";
			}
			else if( weathericon.contains( "云" ) )
			{
				bitPath = PATH + "theme/weathersmall/cloudy.png";
			}
			else if( weathericon.contains( "阴" ) )
			{
				bitPath = PATH + "theme/weathersmall/cloudyday.png";
			}
			else if( weathericon.contains( "晴" ) )
			{
				bitPath = PATH + "theme/weathersmall/sunny.png";
			}
			else
			{
				bitPath = PATH + "theme/weathersmall/unknow.png";
			}
		}
		return bitPath;
	}
	
	public static String StringForPath(
			String name )
	{
		String bitPath = null;
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int hour = c.get( Calendar.HOUR_OF_DAY );
		String weathericon = null;
		String[] names = name.split( "转" );
		if( names.length == 2 )
		{
			if( hour >= 18 )
			{
				weathericon = names[1];
			}
			else
			{
				weathericon = names[0];
			}
		}
		else
		{
			weathericon = name;
		}
		if( hour >= 18 )
		{
			if( weathericon.contains( "云" ) || weathericon.contains( "阴" ) )
			{
				bitPath = PATH + "theme/weather/latecloudy.png";
			}
			else if( weathericon.contains( "雨" ) )
			{
				bitPath = PATH + "theme/weather/rainshowerslate.png";
			}
			else if( weathericon.contains( "雪" ) )
			{
				bitPath = PATH + "theme/weather/snowshowerslate.png";
			}
			else
			{
				bitPath = PATH + "theme/weather/reaching.png";
			}
		}
		else
		{
			if( weathericon.contains( "飓风" ) )
			{
				bitPath = PATH + "theme/weather/jufeng.png";
			}
			if( weathericon.contains( "冰雹" ) )
			{
				bitPath = PATH + "theme/weather/bingbao.png";
			}
			if( weathericon.contains( "暴雪" ) )
			{
				bitPath = PATH + "theme/weather/baosnow.png";
			}
			else if( weathericon.contains( "雷阵雨" ) )
			{
				bitPath = PATH + "theme/weather/thunderstorms.png";
			}
			else if( weathericon.contains( "雨加雪" ) )
			{
				bitPath = PATH + "theme/weather/sleet.png";
			}
			else if( weathericon.contains( "阵雨" ) )
			{
				bitPath = PATH + "theme/weather/zhenrain.png";
			}
			else if( weathericon.contains( "阵雪" ) )
			{
				bitPath = PATH + "theme/weather/zhensnow.png";
			}
			else if( weathericon.contains( "沙" ) )
			{
				bitPath = PATH + "theme/weather/sand.png";
			}
			else if( weathericon.contains( "雾" ) )
			{
				bitPath = PATH + "theme/weather/fog.png";
			}
			else if( weathericon.contains( "大雪" ) )
			{
				bitPath = PATH + "theme/weather/bigsnow.png";
			}
			else if( weathericon.contains( "大雨" ) || weathericon.contains( "暴雨" ) )
			{
				bitPath = PATH + "theme/weather/bigrain.png";
			}
			else if( weathericon.contains( "小雪" ) )
			{
				bitPath = PATH + "theme/weather/smallsnow.png";
			}
			else if( weathericon.contains( "小雨" ) )
			{
				bitPath = PATH + "theme/weather/smallrain.png";
			}
			else if( weathericon.contains( "中雪" ) )
			{
				bitPath = PATH + "theme/weather/middlesnow.png";
			}
			else if( weathericon.contains( "中雨" ) )
			{
				bitPath = PATH + "theme/weather/middlerain.png";
			}
			else if( weathericon.contains( "云" ) )
			{
				bitPath = PATH + "theme/weather/cloudy.png";
			}
			else if( weathericon.contains( "阴" ) )
			{
				bitPath = PATH + "theme/weather/cloudyday.png";
			}
			else if( weathericon.contains( "晴" ) )
			{
				bitPath = PATH + "theme/weather/sunny.png";
			}
			else
			{
				bitPath = PATH + "theme/weather/unknow.png";
			}
		}
		return bitPath;
	}
	
	public static int ReturnMaxInFive(
			int num1 ,
			int num2 ,
			int num3 ,
			int num4 ,
			int num5 )
	{
		int[] intArray = { num1 , num2 , num3 , num4 , num5 };
		int max = intArray[0];
		for( int i = 0 ; i < intArray.length ; i++ )
		{
			if( intArray[i] > max )
			{
				max = intArray[i];
			}
		}
		return max;
	}
	
	public static int ReturnMinInFive(
			int num1 ,
			int num2 ,
			int num3 ,
			int num4 ,
			int num5 )
	{
		int[] intArray = { num1 , num2 , num3 , num4 , num5 };
		int min = intArray[0];
		for( int i = 0 ; i < intArray.length ; i++ )
		{
			if( intArray[i] < min )
			{
				min = intArray[i];
			}
		}
		return min;
	}
	
	public static String TmpPath(
			char tmp )
	{
		String path = null;
		switch( tmp )
		{
			case '0':
				path = PATH + "theme/tmp/num_0.png";
				break;
			case '1':
				path = PATH + "theme/tmp/num_1.png";
				break;
			case '2':
				path = PATH + "theme/tmp/num_2.png";
				break;
			case '3':
				path = PATH + "theme/tmp/num_3.png";
				break;
			case '4':
				path = PATH + "theme/tmp/num_4.png";
				break;
			case '5':
				path = PATH + "theme/tmp/num_5.png";
				break;
			case '6':
				path = PATH + "theme/tmp/num_6.png";
				break;
			case '7':
				path = PATH + "theme/tmp/num_7.png";
				break;
			case '8':
				path = PATH + "theme/tmp/num_8.png";
				break;
			case '9':
				path = PATH + "theme/tmp/num_9.png";
				break;
			default:
				path = null;
				break;
		}
		return path;
	}
	
	public static String codeForPath(
			String weathercode ,
			String folder )
	{
		int code = Integer.parseInt( weathercode );
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int hour = c.get( Calendar.HOUR_OF_DAY );
		String path = null;
		switch( code )
		{
			case 0:
			case 1:
			case 2:
			case 23:
			case 24:
			case 25:
				path = folder + "jufeng.png";
				break;
			case 3:
			case 4:
			case 11:
			case 12:
			case 37:
			case 38:
			case 39:
			case 40:
			case 45:
			case 47:
				path = folder + "thunderstorms.png";
				break;
			case 5:
			case 6:
			case 7:
				path = folder + "sleet.png";
				break;
			case 8:
			case 9:
			case 10:
				path = folder + "smallrain.png";
				break;
			case 13:
			case 42:
			case 46:
				path = folder + "baosnow.png";
				break;
			case 14:
			case 15:
			case 16:
				path = folder + "smallsnow.png";
				break;
			case 17:
			case 18:
			case 35:
				path = folder + "bingbao.png";
				break;
			case 19:
			case 22:
				path = folder + "sand.png";
				break;
			case 20:
			case 21:
				path = folder + "fog.png";
				break;
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 44:
				if( hour >= 18 )
				{
					path = folder + "latecloudy.png";
				}
				else
				{
					path = folder + "cloudyday.png";
				}
				break;
			case 31:
			case 32:
			case 33:
			case 34:
			case 36:
				if( hour >= 18 )
				{
					path = folder + "reaching.png";
				}
				else
				{
					path = folder + "sunny.png";
				}
				break;
			case 41:
			case 43:
				path = folder + "bigsnow.png";
				break;
			default:
				path = folder + "unknow.png";
				break;
		}
		return path;
	}
	
	public static String codeForSmallPath(
			String weathercode ,
			String folder )
	{
		int code = Integer.parseInt( weathercode );
		String path = null;
		switch( code )
		{
			case 0:
			case 1:
			case 2:
			case 23:
			case 24:
			case 25:
				path = folder + "jufeng.png";
				break;
			case 3:
			case 4:
			case 11:
			case 12:
			case 37:
			case 38:
			case 39:
			case 40:
			case 45:
			case 47:
				path = folder + "thunderstorms.png";
				break;
			case 5:
			case 6:
			case 7:
				path = folder + "sleet.png";
				break;
			case 8:
			case 9:
			case 10:
				path = folder + "smallrain.png";
				break;
			case 13:
			case 42:
			case 46:
				path = folder + "baosnow.png";
				break;
			case 14:
			case 15:
			case 16:
				path = folder + "smallsnow.png";
				break;
			case 17:
			case 18:
			case 35:
				path = folder + "bingbao.png";
				break;
			case 19:
			case 22:
				path = folder + "sand.png";
				break;
			case 20:
			case 21:
				path = folder + "fog.png";
				break;
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 44:
				path = folder + "cloudyday.png";
				break;
			case 31:
			case 32:
			case 33:
			case 34:
			case 36:
				path = folder + "sunny.png";
				break;
			case 41:
			case 43:
				path = folder + "bigsnow.png";
				break;
			default:
				path = folder + "unknow.png";
				break;
		}
		return path;
	}
}
