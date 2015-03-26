package com.iLoong.NumberClock.common;


import java.io.IOException;
import java.util.Calendar;

import android.content.Context;
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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.NumberClock.view.WidgetNumberClock;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class NumberClockHelper
{
	
	public static TextureRegion getRegion(
			Context maincontext ,
			String path ,
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( maincontext.getAssets().open( path + name ) ) , true );
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
	
	public static TextureRegion drawTmpRegion(
			String highTem ,
			String lowTem )
	{
		Bitmap backImage = null;
		float width = WidgetNumberClock.TempWidth * WidgetNumberClock.scale;
		float height = WidgetNumberClock.TempHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setTextSize( 27f * WidgetNumberClock.scale );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		String AllTmp = highTem + "°/" + lowTem + "°";
		paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
		canvas.drawText( AllTmp , ( width - paint.measureText( AllTmp ) ) / 2 , posY , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawTimeRegion(
			MainAppContext mAppContext ,
			int mHeadHour ,
			int mHeadMinute )
	{
		Bitmap backImage = null;
		float width = WidgetNumberClock.TimeWidth * WidgetNumberClock.scale;
		float height = WidgetNumberClock.TimeHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		//		paint.setARGB( 255 , 255 , 255 , 255 );
		//		paint.setTextSize( 25f * WidgetNumberClock.scale );
		String watchHourFileNameLeft = "num_" + mHeadHour / 10 + ".png";
		String watchHourFileNameRight = "num_" + mHeadHour % 10 + ".png";
		String watchMinuteFileNameLeft = "num_" + mHeadMinute / 10 + ".png";
		String watchMinuteFileNameRight = "num_" + mHeadMinute % 10 + ".png";
		Bitmap bt = null;
		Bitmap newbt = null;
		Bitmap bt1 = null;
		Bitmap newbt1 = null;
		Bitmap bt2 = null;
		Bitmap newbt2 = null;
		Bitmap bt3 = null;
		Bitmap newbt3 = null;
		Bitmap bt4 = null;
		Bitmap newbt4 = null;
		try
		{
			bt = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.NUMPATH + watchHourFileNameLeft ) );
			newbt = resizeBitmap( bt , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt , 0 , 0 , paint );
			bt1 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.NUMPATH + watchHourFileNameRight ) );
			newbt1 = resizeBitmap( bt1 , (int)( bt1.getWidth() * WidgetNumberClock.scale ) , (int)( bt1.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt1 , bt.getWidth() * WidgetNumberClock.scale + WidgetNumberClock.NumSpace * WidgetNumberClock.scale , 0 , paint );
			bt2 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.NUMPATH + "maohao.png" ) );
			newbt2 = resizeBitmap( bt2 , (int)( bt2.getWidth() * WidgetNumberClock.scale ) , (int)( bt2.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt2 , bt.getWidth() * WidgetNumberClock.scale * 2 + WidgetNumberClock.NumSpace * WidgetNumberClock.scale * 2 , 0 , paint );
			bt3 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.NUMPATH + watchMinuteFileNameLeft ) );
			newbt3 = resizeBitmap( bt3 , (int)( bt3.getWidth() * WidgetNumberClock.scale ) , (int)( bt3.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt3 , bt.getWidth() * WidgetNumberClock.scale * 2 + WidgetNumberClock.NumSpace * WidgetNumberClock.scale * 3 + bt2.getWidth() * WidgetNumberClock.scale , 0 , paint );
			bt4 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.NUMPATH + watchMinuteFileNameRight ) );
			newbt4 = resizeBitmap( bt4 , (int)( bt4.getWidth() * WidgetNumberClock.scale ) , (int)( bt4.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt4 , bt.getWidth() * WidgetNumberClock.scale * 3 + WidgetNumberClock.NumSpace * WidgetNumberClock.scale * 4 + bt2.getWidth() * WidgetNumberClock.scale , 0 , paint );
		}
		catch( IOException e )
		{
			e.printStackTrace();
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
		return newTextureRegion;
	}
	
	public static TextureRegion drawDateRegion(
			int week ,
			int month ,
			int day ,
			int year )
	{
		Bitmap backImage = null;
		float width = WidgetNumberClock.DateWidth * WidgetNumberClock.scale;
		float height = WidgetNumberClock.DateHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setTextSize( 24f * WidgetNumberClock.scale );
		paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		String AllCHar = "";
		String weekstring = WeekChange( week );
		if( DefaultLayout.enable_google_version )
		{
			if( weekstring != null )
			{
				AllCHar = AllCHar + weekstring;
			}
			AllCHar = AllCHar + month + "/";
			AllCHar = AllCHar + day + "/" + year;
		}
		else
		{
			AllCHar = AllCHar + year + "/" + month + "/" + day + "  " + weekstring;
		}
		canvas.drawText( AllCHar , width - paint.measureText( AllCHar ) , posY , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawCityRegion(
			MainAppContext mAppContext ,
			String city )
	{
		Bitmap backImage = null;
		float width = WidgetNumberClock.DateWidth * WidgetNumberClock.scale;
		float height = WidgetNumberClock.DateHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setTextSize( 24f * WidgetNumberClock.scale );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		Bitmap bt = null;
		Bitmap newbt = null;
		try
		{
			bt = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.IMGPATH + "city.png" ) );
			newbt = resizeBitmap( bt , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt , width - paint.measureText( city ) - bt.getWidth() * WidgetNumberClock.scale , ( height - bt.getHeight() * WidgetNumberClock.scale ) / 2 , paint );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
		canvas.drawText( city , width - paint.measureText( city ) , posY , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		if( bt != null )
		{
			bt.recycle();
		}
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
	
	public static TextureRegion drawAMORPMRegion(
			String amorpm )
	{
		Bitmap backImage = null;
		float width = WidgetNumberClock.AMPMWidth * WidgetNumberClock.scale;
		float height = WidgetNumberClock.AMPMHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setTextSize( 25f * WidgetNumberClock.scale );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float lineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float posY = backImage.getHeight() - ( backImage.getHeight() - lineHeight ) / 2 - fontMetrics.bottom;
		paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
		if( amorpm != null )
		{
			canvas.drawText( amorpm , width - paint.measureText( amorpm ) , posY , paint );
		}
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		if( backImage != null )
		{
			backImage.recycle();
		}
		return newTextureRegion;
	}
	
	public static String WeekChange(
			int week )
	{
		String weekstring = null;
		if( !DefaultLayout.enable_google_version )
		{
			switch( week )
			{
				case 1:
					weekstring = "星期日";
					break;
				case 2:
					weekstring = "星期一";
					break;
				case 3:
					weekstring = "星期二";
					break;
				case 4:
					weekstring = "星期三";
					break;
				case 5:
					weekstring = "星期四";
					break;
				case 6:
					weekstring = "星期五";
					break;
				case 7:
					weekstring = "星期六";
					break;
				default:
					weekstring = "未知";
					break;
			}
		}
		else
		{
			switch( week )
			{
				case 1:
					weekstring = "Sun.";
					break;
				case 2:
					weekstring = "Mon.";
					break;
				case 3:
					weekstring = "Tues.";
					break;
				case 4:
					weekstring = "Wed.";
					break;
				case 5:
					weekstring = "Thur.";
					break;
				case 6:
					weekstring = "Fri.";
					break;
				case 7:
					weekstring = "Sat.";
					break;
				default:
					weekstring = "unknow";
					break;
			}
		}
		return weekstring;
	}
	
	public static String WeekChangeInland(
			int week )
	{
		String weekstring = null;
		switch( week )
		{
			case 1:
				weekstring = "周一";
				break;
			case 2:
				weekstring = "周二";
				break;
			case 3:
				weekstring = "周三";
				break;
			case 4:
				weekstring = "周四";
				break;
			case 5:
				weekstring = "周五";
				break;
			case 6:
				weekstring = "周六";
				break;
			case 0:
				weekstring = "周日";
				break;
			default:
				weekstring = "未知";
				break;
		}
		return weekstring;
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
	
	public static String codeForPath(
			String weathercode )
	{
		int code = Integer.parseInt( weathercode );
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		int hour = c.get( Calendar.HOUR_OF_DAY );
		String path = null;
		if( hour >= 18 )
		{
			switch( code )
			{
				case 0:
				case 1:
				case 2:
				case 23:
				case 24:
				case 25:
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
				case 5:
				case 6:
				case 7:
				case 17:
				case 18:
				case 35:
				case 19:
				case 22:
				case 20:
				case 21:
				case 31:
				case 32:
				case 33:
				case 34:
				case 36:
					path = "reaching.png";
					break;
				case 8:
				case 9:
				case 10:
					path = "rainshowerslate.png";
					break;
				case 13:
				case 42:
				case 46:
				case 14:
				case 15:
				case 16:
				case 41:
				case 43:
					path = "snowshowerslate.png";
					break;
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 44:
					path = "latecloudy.png";
					break;
				default:
					path = "unknow.png";
					break;
			}
		}
		else
		{
			switch( code )
			{
				case 0:
				case 1:
				case 2:
				case 23:
				case 24:
				case 25:
					path = "jufeng.png";
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
					path = "thunderstorms.png";
					break;
				case 5:
				case 6:
				case 7:
					path = "sleet.png";
					break;
				case 8:
				case 9:
				case 10:
					path = "smallrain.png";
					break;
				case 13:
				case 42:
				case 46:
					path = "baosnow.png";
					break;
				case 14:
				case 15:
				case 16:
					path = "smallsnow.png";
					break;
				case 17:
				case 18:
				case 35:
					path = "bingbao.png";
					break;
				case 19:
				case 22:
					path = "sand.png";
					break;
				case 20:
				case 21:
					path = "fog.png";
					break;
				case 26:
				case 27:
				case 28:
				case 29:
				case 30:
				case 44:
					path = "cloudyday.png";
					break;
				case 31:
				case 32:
				case 33:
				case 34:
				case 36:
					path = "sunny.png";
					break;
				case 41:
				case 43:
					path = "bigsnow.png";
					break;
				default:
					path = "unknow.png";
					break;
			}
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
	
	public static TextureRegion drawCurveRegion(
			MainAppContext mAppContext ,
			String icon1 ,
			String icon2 ,
			String icon3 ,
			String icon4 ,
			String icon5 ,
			int hightmp1 ,
			int hightmp2 ,
			int hightmp3 ,
			int hightmp4 ,
			int hightmp5 ,
			int lowtmp1 ,
			int lowtmp2 ,
			int lowtmp3 ,
			int lowtmp4 ,
			int lowtmp5 ,
			String date1 ,
			String date2 ,
			String date3 ,
			String date4 ,
			String date5 )
	{
		Bitmap backImage = null;
		float width = (WidgetNumberClock.CurveGroupWidth ) * WidgetNumberClock.scale;
		float height = WidgetNumberClock.CurveGroupHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setTextSize( 20f * WidgetNumberClock.scale );
		paint.setStrokeWidth( 2f * WidgetNumberClock.scale );
		float everyWidth = ( WidgetNumberClock.CurveGroupWidth - WidgetNumberClock.WEATHERSMALLWidth * 5 ) / 4 * WidgetNumberClock.scale;
		float curveHeight = ( WidgetNumberClock.CurveGroupHeight - WidgetNumberClock.WEATHERSMALLHeight - 20f ) / 2 * WidgetNumberClock.scale;
		Bitmap bt = null;
		Bitmap newbt = null;
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
		Bitmap bt6 = null;
		Bitmap newbt6 = null;
		String path1 = null;
		String path2 = null;
		String path3 = null;
		String path4 = null;
		String path5 = null;
		if( icon1 != null && icon2 != null && icon3 != null && icon4 != null && icon5 != null )
		{
			path1 = codeForSmallPath( icon1 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path2 = codeForSmallPath( icon2 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path3 = codeForSmallPath( icon3 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path4 = codeForSmallPath( icon4 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path5 = codeForSmallPath( icon5 , WidgetNumberClock.WEATHERICONSMALlPATH );
		}
		try
		{
			bt = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path1 ) );
			newbt = resizeBitmap( bt , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt , 0 , 0 , paint );
			bt1 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path2 ) );
			newbt1 = resizeBitmap( bt1 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt1 , bt.getWidth() * WidgetNumberClock.scale + everyWidth , 0 , paint );
			bt2 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path3 ) );
			newbt2 = resizeBitmap( bt2 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt2 , bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 , 0 , paint );
			bt3 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path4 ) );
			newbt3 = resizeBitmap( bt3 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt3 , bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 , 0 , paint );
			bt4 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path5 ) );
			newbt4 = resizeBitmap( bt4 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt4 , bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 , 0 , paint );
			bt5 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.IMGPATH + "dotoday.png" ) );
			newbt5 = resizeBitmap( bt5 , (int)( bt5.getWidth() * WidgetNumberClock.scale ) , (int)( bt5.getHeight() * WidgetNumberClock.scale ) );
			bt6 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.IMGPATH + "dot.png" ) );
			newbt6 = resizeBitmap( bt6 , (int)( bt6.getWidth() * WidgetNumberClock.scale ) , (int)( bt6.getHeight() * WidgetNumberClock.scale ) );
			int maxHighNum = ReturnMaxInFive( hightmp1 , hightmp2 , hightmp3 , hightmp4 , hightmp5 );
			int minHighNum = ReturnMinInFive( hightmp1 , hightmp2 , hightmp3 , hightmp4 , hightmp5 );
			float JianJu = 40 * WidgetNumberClock.scale;
			if( maxHighNum == minHighNum )
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						hightmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
			}
			else
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp1 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp5 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp1 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp5 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						hightmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp1 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp5 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
			}
			int maxLowNum = ReturnMaxInFive( lowtmp1 , lowtmp2 , lowtmp3 , lowtmp4 , lowtmp5 );
			int minLowNum = ReturnMinInFive( lowtmp1 , lowtmp2 , lowtmp3 , lowtmp4 , lowtmp5 );
			paint.setShadowLayer( 0 , 0 , 0 , 0xDD000000 );
			if( maxLowNum == minLowNum )
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						lowtmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
			}
			else
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp1 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp5 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp1 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp5 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						lowtmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp1 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp5 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
		paint.setTextSize( 20f * WidgetNumberClock.scale );
		canvas.drawText( date1 , ( bt.getWidth() - paint.measureText( date1 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		canvas.drawText( date2 , bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() - paint.measureText( date2 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		canvas.drawText( date3 , bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() - paint.measureText( date3 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		canvas.drawText( date4 , bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() - paint.measureText( date4 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		canvas.drawText( date5 , bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() - paint.measureText( date5 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
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
		return newTextureRegion;
	}
	
	public static TextureRegion drawCurveRegionInLand(
			MainAppContext mAppContext ,
			String icon1 ,
			String icon2 ,
			String icon3 ,
			String icon4 ,
			String icon5 ,
			int hightmp1 ,
			int hightmp2 ,
			int hightmp3 ,
			int hightmp4 ,
			int hightmp5 ,
			int lowtmp1 ,
			int lowtmp2 ,
			int lowtmp3 ,
			int lowtmp4 ,
			int lowtmp5 ,
			int date1 ,
			int date2 ,
			int date3 ,
			int date4 ,
			int date5 )
	{
		Bitmap backImage = null;
		float width = ( WidgetNumberClock.CurveGroupWidth ) * WidgetNumberClock.scale;
		float height = WidgetNumberClock.CurveGroupHeight * WidgetNumberClock.scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );// .TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );// 防锯齿
		paint.setDither( true );// 防抖动
		paint.setSubpixelText( true );
		paint.setARGB( 255 , 255 , 255 , 255 );
		paint.setTextSize( 20f * WidgetNumberClock.scale );
		paint.setStrokeWidth( 2f * WidgetNumberClock.scale );
		float everyWidth = ( WidgetNumberClock.CurveGroupWidth - WidgetNumberClock.WEATHERSMALLWidth * 5 ) / 4 * WidgetNumberClock.scale;
		float curveHeight = (WidgetNumberClock.CurveGroupHeight - WidgetNumberClock.WEATHERSMALLHeight - 20f ) / 2 * WidgetNumberClock.scale;
		Bitmap bt = null;
		Bitmap newbt = null;
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
		Bitmap bt6 = null;
		Bitmap newbt6 = null;
		String path1 = null;
		String path2 = null;
		String path3 = null;
		String path4 = null;
		String path5 = null;
		if( icon1 != null && icon2 != null && icon3 != null && icon4 != null && icon5 != null )
		{
			path1 = StringForStringPath( icon1 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path2 = StringForStringPath( icon2 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path3 = StringForStringPath( icon3 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path4 = StringForStringPath( icon4 , WidgetNumberClock.WEATHERICONSMALlPATH );
			path5 = StringForStringPath( icon5 , WidgetNumberClock.WEATHERICONSMALlPATH );
		}
		try
		{
			bt = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path1 ) );
			newbt = resizeBitmap( bt , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt , 0 , 0 , paint );
			bt1 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path2 ) );
			newbt1 = resizeBitmap( bt1 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt1 , bt.getWidth() * WidgetNumberClock.scale + everyWidth , 0 , paint );
			bt2 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path3 ) );
			newbt2 = resizeBitmap( bt2 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt2 , bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 , 0 , paint );
			bt3 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path4 ) );
			newbt3 = resizeBitmap( bt3 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt3 , bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 , 0 , paint );
			bt4 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( path5 ) );
			newbt4 = resizeBitmap( bt4 , (int)( bt.getWidth() * WidgetNumberClock.scale ) , (int)( bt.getHeight() * WidgetNumberClock.scale ) );
			canvas.drawBitmap( newbt4 , bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 , 0 , paint );
			bt5 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.IMGPATH + "dotoday.png" ) );
			newbt5 = resizeBitmap( bt5 , (int)( bt5.getWidth() * WidgetNumberClock.scale ) , (int)( bt5.getHeight() * WidgetNumberClock.scale ) );
			bt6 = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( WidgetNumberClock.IMGPATH + "dot.png" ) );
			newbt6 = resizeBitmap( bt6 , (int)( bt6.getWidth() * WidgetNumberClock.scale ) , (int)( bt6.getHeight() * WidgetNumberClock.scale ) );
			int maxHighNum = ReturnMaxInFive( hightmp1 , hightmp2 , hightmp3 , hightmp4 , hightmp5 );
			int minHighNum = ReturnMinInFive( hightmp1 , hightmp2 , hightmp3 , hightmp4 , hightmp5 );
			float JianJu = 40 * WidgetNumberClock.scale;
			if( maxHighNum == minHighNum )
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						hightmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						hightmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight / 2 + 17f * WidgetNumberClock.scale ,
						paint );
			}
			else
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp1 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp5 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp1 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp5 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						hightmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp1 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp2 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp3 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp4 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						hightmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( hightmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight - JianJu - ( hightmp5 - minHighNum ) * ( ( curveHeight - JianJu ) / ( maxHighNum - minHighNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
			}
			int maxLowNum = ReturnMaxInFive( lowtmp1 , lowtmp2 , lowtmp3 , lowtmp4 , lowtmp5 );
			int minLowNum = ReturnMinInFive( lowtmp1 , lowtmp2 , lowtmp3 , lowtmp4 , lowtmp5 );
			paint.setShadowLayer( 0 , 0 , 0 , 0xDD000000 );
			if( maxLowNum == minLowNum )
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 - bt6.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						lowtmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
				canvas.drawText(
						lowtmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 3 / 2 + 17f * WidgetNumberClock.scale ,
						paint );
			}
			else
			{
				canvas.drawBitmap(
						newbt5 ,
						( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp1 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawBitmap(
						newbt6 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - bt5.getWidth() * WidgetNumberClock.scale ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp5 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp1 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				canvas.drawLine(
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + bt.getWidth() * WidgetNumberClock.scale / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp5 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale / 2 ,
						paint );
				paint.setTextSize( 17f * WidgetNumberClock.scale );
				paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
				canvas.drawText(
						lowtmp1 + "°" ,
						( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp1 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp1 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp2 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp2 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp2 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp3 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp3 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp3 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp4 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp4 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp4 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
				canvas.drawText(
						lowtmp5 + "°" ,
						bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() * WidgetNumberClock.scale - paint.measureText( lowtmp5 + "°" ) ) / 2 ,
						WidgetNumberClock.WEATHERSMALLHeight * WidgetNumberClock.scale + curveHeight * 2 - JianJu - ( lowtmp5 - minLowNum ) * ( ( curveHeight - JianJu ) / ( maxLowNum - minLowNum ) ) + bt6
								.getHeight() * WidgetNumberClock.scale * 3 / 2 ,
						paint );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		paint.setShadowLayer( 0.5f , 0 , 1 , 0xDD000000 );
		paint.setTextSize( 20f * WidgetNumberClock.scale );
		String week1 = WeekChangeInland( date1 );
		String week2 = WeekChangeInland( date2 );
		String week3 = WeekChangeInland( date3 );
		String week4 = WeekChangeInland( date4 );
		String week5 = WeekChangeInland( date5 );
		if( week1 != null )
		{
			canvas.drawText( week1 , ( bt.getWidth() - paint.measureText( week1 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		}
		if( week2 != null )
		{
			canvas.drawText( week2 , bt.getWidth() * WidgetNumberClock.scale + everyWidth + ( bt.getWidth() - paint.measureText( week2 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		}
		if( week3 != null )
		{
			canvas.drawText( week3 , bt.getWidth() * WidgetNumberClock.scale * 2 + everyWidth * 2 + ( bt.getWidth() - paint.measureText( week3 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		}
		if( week4 != null )
		{
			canvas.drawText( week4 , bt.getWidth() * WidgetNumberClock.scale * 3 + everyWidth * 3 + ( bt.getWidth() - paint.measureText( week4 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
		}
		if( week5 != null )
		{
			canvas.drawText( week5 , bt.getWidth() * WidgetNumberClock.scale * 4 + everyWidth * 4 + ( bt.getWidth() - paint.measureText( week5 ) ) / 2 , height - 2 * WidgetNumberClock.scale , paint );
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
		return newTextureRegion;
	}
	
	public static String StringForStringPath(
			String name ,
			String path )
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
				bitPath = path + "jufeng.png";
			}
			if( weathericon.contains( "冰雹" ) )
			{
				bitPath = path + "bingbao.png";
			}
			if( weathericon.contains( "暴雪" ) )
			{
				bitPath = path + "baosnow.png";
			}
			else if( weathericon.contains( "雷阵雨" ) )
			{
				bitPath = path + "thunderstorms.png";
			}
			else if( weathericon.contains( "雨加雪" ) )
			{
				bitPath = path + "sleet.png";
			}
			else if( weathericon.contains( "阵雨" ) )
			{
				bitPath = path + "zhenrain.png";
			}
			else if( weathericon.contains( "阵雪" ) )
			{
				bitPath = path + "zhensnow.png";
			}
			else if( weathericon.contains( "沙" ) )
			{
				bitPath = path + "sand.png";
			}
			else if( weathericon.contains( "雾" ) )
			{
				bitPath = path + "fog.png";
			}
			else if( weathericon.contains( "大雪" ) )
			{
				bitPath = path + "bigsnow.png";
			}
			else if( weathericon.contains( "大雨" ) || weathericon.contains( "暴雨" ) )
			{
				bitPath = path + "bigrain.png";
			}
			else if( weathericon.contains( "小雪" ) )
			{
				bitPath = path + "smallsnow.png";
			}
			else if( weathericon.contains( "小雨" ) )
			{
				bitPath = path + "smallrain.png";
			}
			else if( weathericon.contains( "中雪" ) )
			{
				bitPath = path + "middlesnow.png";
			}
			else if( weathericon.contains( "中雨" ) )
			{
				bitPath = path + "middlerain.png";
			}
			else if( weathericon.contains( "云" ) )
			{
				bitPath = path + "cloudy.png";
			}
			else if( weathericon.contains( "阴" ) )
			{
				bitPath = path + "cloudyday.png";
			}
			else if( weathericon.contains( "晴" ) )
			{
				bitPath = path + "sunny.png";
			}
			else
			{
				bitPath = path + "unknow.png";
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
				bitPath = "latecloudy.png";
			}
			else if( weathericon.contains( "雨" ) )
			{
				bitPath = "rainshowerslate.png";
			}
			else if( weathericon.contains( "雪" ) )
			{
				bitPath = "snowshowerslate.png";
			}
			else
			{
				bitPath = "reaching.png";
			}
		}
		else
		{
			if( weathericon.contains( "飓风" ) )
			{
				bitPath = "jufeng.png";
			}
			if( weathericon.contains( "冰雹" ) )
			{
				bitPath = "bingbao.png";
			}
			if( weathericon.contains( "暴雪" ) )
			{
				bitPath = "baosnow.png";
			}
			else if( weathericon.contains( "雷阵雨" ) )
			{
				bitPath = "thunderstorms.png";
			}
			else if( weathericon.contains( "雨加雪" ) )
			{
				bitPath = "sleet.png";
			}
			else if( weathericon.contains( "阵雨" ) )
			{
				bitPath = "zhenrain.png";
			}
			else if( weathericon.contains( "阵雪" ) )
			{
				bitPath = "zhensnow.png";
			}
			else if( weathericon.contains( "沙" ) )
			{
				bitPath = "sand.png";
			}
			else if( weathericon.contains( "雾" ) )
			{
				bitPath = "fog.png";
			}
			else if( weathericon.contains( "大雪" ) )
			{
				bitPath = "bigsnow.png";
			}
			else if( weathericon.contains( "大雨" ) || weathericon.contains( "暴雨" ) )
			{
				bitPath = "bigrain.png";
			}
			else if( weathericon.contains( "小雪" ) )
			{
				bitPath = "smallsnow.png";
			}
			else if( weathericon.contains( "小雨" ) )
			{
				bitPath = "smallrain.png";
			}
			else if( weathericon.contains( "中雪" ) )
			{
				bitPath = "middlesnow.png";
			}
			else if( weathericon.contains( "中雨" ) )
			{
				bitPath = "middlerain.png";
			}
			else if( weathericon.contains( "云" ) )
			{
				bitPath = "cloudy.png";
			}
			else if( weathericon.contains( "阴" ) )
			{
				bitPath = "cloudyday.png";
			}
			else if( weathericon.contains( "晴" ) )
			{
				bitPath = "sunny.png";
			}
			else
			{
				bitPath = "unknow.png";
			}
		}
		return bitPath;
	}
}
