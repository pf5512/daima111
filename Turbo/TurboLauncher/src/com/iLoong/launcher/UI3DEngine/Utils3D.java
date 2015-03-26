package com.iLoong.launcher.UI3DEngine;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.cooee.android.launcher.framework.IconCache;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DrawDynamicIcon;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.cut;
import com.iLoong.launcher.SetupMenu.Actions.ShowDesktop;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class Utils3D
{
	
	static Vector2 liangVector = new Vector2( 0 , 1 );
	static Object exe_lock = new Object();
	static int[] defaultPixels;
	static byte[] defaultBmpbuff;
	private static int width = 0;
	private static int height = 0;
	public static int statusBarHeight = 0;
	public static int navigationBarHeight = 0;
	static float folderTitleFontTopping = 0;
	public static int realWidth;
	public static int realHeight;
	public static Paint paint = new Paint();
	public static Canvas canvas = new Canvas();
	public static FontMetrics fontMetrics = new FontMetrics();
	public static float iconBmpHeight = -1;
	public static int fullWidth = 0;
	public static int fullHeight = 0;
	public static String mScreenShotPath = null;
	
	static public int showPidMemoryInfo(
			String title )
	{
		//		int myProcessID = Process.myPid();
		//		int pids[] = new int[1];
		//		pids[0] = myProcessID;
		//		ActivityManager am = (ActivityManager) iLoongLauncher.getInstance()
		//				.getSystemService(Context.ACTIVITY_SERVICE);
		//		MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo(pids);
		//		MemoryInfo pidMemoryInfo = memoryInfoArray[0];
		//		Log.i("MemoryInfo",
		//				"MemoryInfo:" + title + ":pss = " + pidMemoryInfo.getTotalPss()
		//						+ "K" + " ,uss:" + pidMemoryInfo.getTotalPrivateDirty());
		//		return pidMemoryInfo.getTotalPss();
		return 0;
	}
	
	static public int showPidMemoryInfo(
			Context context ,
			String title )
	{
		//		int myProcessID = Process.myPid();
		//		int pids[] = new int[1];
		//		pids[0] = myProcessID;
		//		ActivityManager am = (ActivityManager) context
		//				.getSystemService(Context.ACTIVITY_SERVICE);
		//		MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo(pids);
		//		MemoryInfo pidMemoryInfo = memoryInfoArray[0];
		//		Log.i("MemoryInfo",
		//				"MemoryInfo:" + title + " = " + pidMemoryInfo.getTotalPss()
		//						+ "K" + " time:" + System.currentTimeMillis());
		//		return pidMemoryInfo.getTotalPss();
		return 0;
	}
	
	static public boolean isLowMemory()
	{
		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		ActivityManager am = (ActivityManager)iLoongLauncher.getInstance().getSystemService( Context.ACTIVITY_SERVICE );
		am.getMemoryInfo( memInfo );
		Log.i( "MemoryInfo" , "availMem:" + memInfo.availMem / 1024 / 1024 + ",threshold" + memInfo.threshold / 1024 / 1024 );
		return memInfo.lowMemory;
	}
	
	public static void showTimeFromStart(
			String s )
	{
		//Log.i("Launcher.Model",s+":"+(System.currentTimeMillis()-iLoongLauncher._time));
	}
	
	static public String sync_do_exec(
			String cmd )
	{
		String s = "\n";
		try
		{
			java.lang.Process p = Runtime.getRuntime().exec( cmd );
			BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String line = null;
			while( ( line = in.readLine() ) != null )
			{
				s += line + "\n";
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	static public boolean do_exec(
			final String cmd ,
			String packageName )
	{
		boolean success = false;
		new Thread() {
			
			@Override
			public void run()
			{
				String s = "\n";
				try
				{
					java.lang.Process p = Runtime.getRuntime().exec( cmd );
					BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
					String line = null;
					while( ( line = in.readLine() ) != null )
					{
						s += line + "\n";
					}
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
				synchronized( exe_lock )
				{
					Log.d( "apk" , "exe_lock notify" );
					exe_lock.notify();
				}
			}
		}.start();
		int i = 0;
		PackageManager pm = iLoongLauncher.getInstance().getPackageManager();
		synchronized( exe_lock )
		{
			while( !success && i < 12 )
			{
				Log.d( "apk" , "exe_lock wait" );
				try
				{
					exe_lock.wait( 10000 );
					Log.d( "apk" , "exe_lock wait finish" );
				}
				catch( InterruptedException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if( packageName != null )
				{
					try
					{
						pm.getPackageInfo( packageName , PackageManager.GET_ACTIVITIES );
						Log.e( "apk" , "has install,do not wait:" + packageName );
						success = true;
						break;
					}
					catch( Exception e )
					{
						Log.e( "apk" , "wait again:" + packageName );
					}
				}
				i++;
			}
		}
		return success;
	}
	
	public static boolean ExistSDCard()
	{
		if( android.os.Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED ) )
		{
			return true;
		}
		else
			return false;
	}
	
	public static void showMessage(
			String str )
	{
		Toast.makeText( iLoongApplication.getInstance() , str , Toast.LENGTH_SHORT ).show();
	}
	
	public static int getStatusBarHeight()
	{
		if( statusBarHeight != 0 )
			return statusBarHeight;
		Resources res = iLoongLauncher.getInstance().getResources();
		int resourceId = res.getIdentifier( "status_bar_height" , "dimen" , "android" );
		if( resourceId > 0 )
		{
			statusBarHeight = res.getDimensionPixelSize( resourceId );
		}
		return statusBarHeight;
	}
	
	public static int getNavigationBarHeight()
	{
		if( navigationBarHeight != 0 )
			return navigationBarHeight;
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
		{
			if( !ViewConfiguration.get( iLoongLauncher.getInstance() ).hasPermanentMenuKey() )
			{
				int resourceId = iLoongLauncher.getInstance().getResources().getIdentifier( "navigation_bar_height" , "dimen" , "android" );
				if( resourceId > 0 )
				{
					navigationBarHeight = iLoongLauncher.getInstance().getResources().getDimensionPixelSize( resourceId );
				}
			}
		}
		return navigationBarHeight;
	}
	
	// fuck the魅族的SmartBar
	public static boolean hasMeiZuSmartBar()
	{
		boolean result = false;
		try
		{
			// 新型号可用反射调用Build.hasSmartBar()
			Method method = Class.forName( "android.os.Build" ).getMethod( "hasSmartBar" );
			return ( (Boolean)method.invoke( null ) ).booleanValue();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if( Build.DEVICE.equals( "mx2" ) )
		{
			result = true;
		}
		else if( Build.DEVICE.equals( "mx" ) || Build.DEVICE.equals( "m9" ) )
		{
			result = false;
		}
		return result;
	}
	
	/**
	 * 方法一:uc等在使用的方法(新旧版flyme均有效)，
	 * 此方法需要配合requestWindowFeature(Window.FEATURE_NO_TITLE
	 * )使用,缺点是程序无法使用系统actionbar
	 * 
	 * @param decorView
	 *            window.getDecorView
	 */
	public static void hideNavigationBar(
			View decorView )
	{
		try
		{
			@SuppressWarnings( "rawtypes" )
			Class[] arrayOfClass = new Class[1];
			arrayOfClass[0] = Integer.TYPE;
			Method localMethod = View.class.getMethod( "setSystemUiVisibility" , arrayOfClass );
			Field localField = View.class.getField( "SYSTEM_UI_FLAG_HIDE_NAVIGATION" );
			Object[] arrayOfObject = new Object[1];
			try
			{
				arrayOfObject[0] = localField.get( null );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
			localMethod.invoke( decorView , arrayOfObject );
			return;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static int getScreenHeight()
	{
		if( height != 0 )
		{
			return height;
		}
		Resources res = iLoongLauncher.getInstance().getResources();
		int screenHeight = res.getDisplayMetrics().heightPixels;
		height = screenHeight;
		return height;
	}
	
	public static int getScreenDisplayMetricsHeight()
	{
		Resources res = iLoongLauncher.getInstance().getResources();
		return res.getDisplayMetrics().heightPixels;
	}
	
	public static int getScreenWidth()
	{
		if( width != 0 )
			return width;
		Resources res = iLoongLauncher.getInstance().getResources();
		width = res.getDisplayMetrics().widthPixels;
		return width;
	}
	
	public static void resetSize()
	{
		width = 0;
		height = 0;
	}
	
	public static void calibration()
	{
		Resources res = iLoongLauncher.getInstance().getResources();
		int widthPixels = res.getDisplayMetrics().widthPixels;
		int heightPixels = res.getDisplayMetrics().heightPixels;
		int screenHeight = Math.max( heightPixels , widthPixels );
		int screenWidth = Math.min( widthPixels , heightPixels );
		fullWidth = screenWidth;
		fullHeight = screenHeight;
		width = screenWidth;
		height = screenHeight;
		realWidth = width;
		realHeight = height;
	}
	
	public static float getDensity()
	{
		return Gdx.graphics.getDensity();
	}
	
	public static byte[] bmp2Array(
			Bitmap bmp )
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress( Bitmap.CompressFormat.PNG , 100 , baos );
		return baos.toByteArray();
	}
	
	public static Pixmap bmp2Pixmap(
			Bitmap bmp )
	{
		return bmp2Pixmap( bmp , true );
	}
	
	public synchronized static Pixmap bmp2Pixmap(
			Bitmap bmp ,
			boolean recycle )
	{
		if( defaultPixels == null )
		{
			defaultPixels = new int[R3D.workspace_cell_width * R3D.workspace_cell_height + 64];
			defaultBmpbuff = new byte[R3D.workspace_cell_width * R3D.workspace_cell_height * 4 + 256];
		}
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		int[] pixels;
		byte[] bmpbuff;
		if( w == R3D.workspace_cell_width && h == R3D.workspace_cell_height )
		{
			pixels = defaultPixels;
			bmpbuff = defaultBmpbuff;
		}
		else
		{
			pixels = new int[w * h + 64];
			bmpbuff = new byte[w * h * 4 + 256];
		}
		bmp.getPixels( pixels , 0 , w , 0 , 0 , w , h );
		int filesize = cut.bmp( w , h , pixels , bmpbuff );
		Pixmap p = new Pixmap( bmpbuff , 0 , filesize );
		if( bmpbuff != defaultBmpbuff )
			bmpbuff = null;
		if( recycle )
		{
			bmp.recycle();
			bmp = null;
		}
		// FileHandle file = new FileHandle("/mnt/sdcard/test/" + "pack" +
		// System.currentTimeMillis() + ".png");
		// PixmapIO.writePNG(file, p);
		return p;
	}
	
	public static boolean pointInRectangle(
			Rectangle r ,
			float x ,
			float y )
	{
		return r.x <= x && r.x + r.width >= x && r.y <= y && r.y + r.height >= y;
	}
	
	/* 判断特定Actor是否被框选中 */
	public static boolean overlapRectangles(
			View3D actor ,
			Rectangle r2 )
	{
		if( actor.x < r2.x + r2.width && actor.x + actor.width > r2.x && actor.y < r2.y + r2.height && actor.y + actor.height > r2.y )
			return true;
		else
			return false;
	}
	
	public static Bitmap resizeBmp(
			Bitmap bmp ,
			float maxWidth ,
			float maxHeight )
	{
		float scale = 1f;
		int bitmapWidth = bmp.getWidth();
		int bitmapHeight = bmp.getHeight();
		if( bitmapWidth > maxWidth )
		{
			scale = maxWidth / (float)bitmapWidth;
		}
		if( bitmapHeight * scale > maxHeight )
		{
			scale = maxHeight / (float)bitmapHeight;
		}
		if( scale != 1f )
		{
			bitmapWidth = (int)( scale * bitmapWidth );
			bitmapHeight = (int)( scale * bitmapHeight );
			Bitmap res = Bitmap.createScaledBitmap( bmp , bitmapWidth , bitmapHeight , true );
			bmp.recycle();
			return res;
		}
		else
			return bmp;
	}
	
	public static boolean lineInActor(
			View3D actor ,
			Vector2 startPoint ,
			Vector2 endPoint )
	{
		int xwmin = (int)actor.x;
		int ywmin = (int)actor.y;
		int xwmax = (int)( actor.x + actor.width );
		int ywmax = (int)( actor.y + actor.height );
		int x1 = (int)startPoint.x;
		int y1 = (int)startPoint.y;
		int x2 = (int)endPoint.x;
		int y2 = (int)endPoint.y;
		float dx , dy , u1 , u2;
		u1 = (float)0.0;
		u2 = (float)1.0;
		liangVector.x = (float)u1;
		liangVector.y = (float)u2;
		dx = x2 - x1;
		if( ClipT( -dx , x1 - xwmin ) )
			if( ClipT( dx , xwmax - x1 ) )
			{
				dy = y2 - y1;
				if( ClipT( -dy , y1 - ywmin ) )
					if( ClipT( dy , ywmax - y1 ) )
					{
						if( liangVector.y < 1.0 )
						{
							x2 = (int)( x1 + liangVector.y * dx );
							y2 = (int)( y1 + liangVector.y * dy );
						}
						if( liangVector.x > 0.0 )
						{
							x1 = (int)( x1 + liangVector.x * dx );
							y1 = (int)( y1 + liangVector.x * dy );
						}
						// line(x1,y1,x2,y2);
						return true;
					}
			}
		return false;
	}
	
	public static float getRotDegrees(
			float moveX ,
			float moveY ,
			float centerX ,
			float centerY )
	{
		float retDeg;
		float dx = moveX - centerX;
		float dy = moveY - centerY;
		if( moveX == centerX )
		{
			if( moveY - centerY > 0 )
			{
				retDeg = 90;
				return retDeg;
			}
			else
			{
				retDeg = 270;
				return retDeg;
			}
		}
		retDeg = (float)Math.atan( ( dy / dx ) );
		retDeg = (float)( retDeg * 180 / Math.PI );
		if( dx > 0 && dy > 0 )
		{
			// retDeg= retDeg;
		}
		else if( dx < 0 && dy > 0 )
		{
			retDeg = 180 + retDeg;
		}
		else if( dx < 0 && dy < 0 )
		{
			retDeg = retDeg + 180;
		}
		else if( dx > 0 && dy < 0 )
		{
			retDeg = 360 + retDeg;
		}
		return retDeg;
	}
	
	private static boolean ClipT(
			float p ,
			float q )
	{
		boolean flag = true;
		float r;
		if( p < 0.0 )
		{
			r = q / p;
			if( r > liangVector.y )
				flag = false;
			else if( r > liangVector.x )
			{
				liangVector.x = r;
			}
		}
		else if( p > 0.0 )
		{
			r = q / p;
			if( r < liangVector.x )
				flag = false;
			else if( r < liangVector.y )
			{
				liangVector.y = r;
			}
		}
		else if( q < 0.0 )
			flag = false;
		return flag;
	}
	
	public static Bitmap GetBmpFromImageName(
			String imageName )
	{
		Bitmap image;
		Bitmap retImage;
		if( imageName == null )
		{
			return null;
		}
		image = ThemeManager.getInstance().getBitmap( imageName );
		// //float
		// density=iLoongApplication.ctx.getResources().getDisplayMetrics().density;
		// //image = Tools.resizeBitmap(image, density / 1.5f);
		// if (R3D.icon_bg_num>0)
		// {
		// /*将图标放到到和ICON背景一样大*/
		// if (DefaultLayout.app_icon_size>= (image.getHeight()))
		// {
		// retImage=Tools.resizeBitmap(image, DefaultLayout.app_icon_size,
		// DefaultLayout.app_icon_size);
		// }
		// else
		// {
		// retImage= Utilities.createIconBitmap(new BitmapDrawable(
		// (Bitmap) image), iLoongApplication.ctx);
		// }
		// }
		// else
		// {
		retImage = Utilities.createIconBitmap( new BitmapDrawable( (Bitmap)image ) , iLoongApplication.getInstance() );
		// }
		if( image != null )
		{
			image.recycle();
		}
		return retImage;
	}
	
	public static void changeTextureRegionHeight(
			TextureRegion region ,
			float scale )
	{
		float V2 = region.getV() + ( region.getV2() - region.getV() ) * scale;
		region.setV2( V2 );
	}
	
	public static void changeTextureRegion(
			View3D myActor ,
			float iconHeight ,
			boolean bNeedScale )
	{
		if( myActor instanceof Icon3D )
		{
			float scale = ( (float)Utils3D.getIconBmpHeight() / (float)R3D.workspace_cell_height );
			// Log.d("title",
			// "iconHeight,iconBmp="+iconHeight+","+Utils3D.getIconBmpHeight());
			if( myActor.height == R3D.workspace_cell_height && iconHeight != myActor.height )
			{
				float V2 = myActor.region.getV() + ( myActor.region.getV2() - myActor.region.getV() ) * scale;
				myActor.region.setV2( V2 );
				// myActor.setScale(0.8f, 0.8f);
			}
			else if( myActor.height != R3D.workspace_cell_height && iconHeight != myActor.height )
			{
				float V2 = myActor.region.getV() + ( myActor.region.getV2() - myActor.region.getV() ) / scale;
				myActor.region.setV2( V2 );
			}
			myActor.setSize( R3D.workspace_cell_width , iconHeight );
			myActor.setOrigin( myActor.width / 2 , myActor.height / 2 );
			if( R3D.icon_bg_num >= 1 && bNeedScale )
			{
				// myActor.setScale(0.8f, 0.8f);
			}
		}
		else
		{
			// myActor.setScale(0.6f, 0.6f);
		}
	}
	
	public static void changeTextureRegion(
			ArrayList<View3D> view3DArray ,
			float iconHeight ,
			boolean bNeedScale )
	{
		View3D myActor;
		int Count = view3DArray.size();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = view3DArray.get( i );
			changeTextureRegion( myActor , iconHeight , bNeedScale );
		}
	}
	
	// public static float getTopPading()
	// {
	// float paddingTop = 0;
	// float iconHeight = Utilities.sIconTextureHeight;
	// float cellHeight = R3D.workspace_cell_height;
	// Paint paint = new Paint();
	// paint.setColor(Color.WHITE);
	// paint.setAntiAlias(true);
	//
	// paint.setTextSize(R3D.icon_title_font);
	// FontMetrics fontMetrics = paint.getFontMetrics();
	// float singleLineHeight = (float) Math.ceil(fontMetrics.bottom
	// - fontMetrics.top);
	// float space_height = iconHeight/10;
	// float tmp = cellHeight - iconHeight -space_height - 2*singleLineHeight;
	//
	// if(tmp<0){
	//
	// paddingTop = (cellHeight - iconHeight - space_height -
	// singleLineHeight)/2;
	//
	// }
	// else {
	//
	// paddingTop = tmp/2;
	// }
	// return paddingTop;
	// }
	public static float getfolderTitleFontTopping()
	{
		if( Icon3D.titleBg != null && folderTitleFontTopping < 0 )
		{
			return -folderTitleFontTopping;
		}
		return 0;
	}
	
	public static synchronized Bitmap folderTitleToBitmap(
			String title ,
			Bitmap title_bg ,
			int textureWidth )
	{
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
		float iconWidth = Utilities.sIconTextureWidth;
		float bmpHeight = iconWidth;
		float cellHeight = R3D.workspace_cell_height;
		int titleHeight = 0;
		// paddingLeft = (textureWidth - iconWidth) / 2;
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
		String[] splitTitle;
		// boolean split = false;
		float space_height = 0;// iconHeight/10;
		int paddingTop;
		// float tmp = cellHeight - iconHeight -space_height -
		// 2*singleLineHeight;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2 );
			if( title != null )
			{
				splitTitle = splitTitle( title , R3D.icon_title_font , textureWidth );
			}
			else
			{
				splitTitle = null;
			}
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2 );
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
		}
		if( paddingTop < 0 )
			paddingTop = 0;
		bmpHeight += ( paddingTop + space_height );
		// if(tmp<0){
		// space_height = iconHeight/10;
		// splitTitle = new String[1];
		// splitTitle[0] = title;
		// paddingTop = (cellHeight - iconHeight - space_height -
		// singleLineHeight)/2;
		// iconHeight += (paddingTop + space_height);
		// }
		// else
		// {
		// paddingTop = tmp/2;
		// iconHeight += (paddingTop+space_height);
		// splitTitle = splitTitle(title,R3D.icon_title_font,textureWidth);
		// split = true;
		// }
		titleHeight = Math.round( cellHeight - bmpHeight );
		if( title_bg != null )
		{
			titleHeight = Math.round( cellHeight - bmpHeight - singleLineHeight / 2 + title_bg.getHeight() / 2 );
		}
		Bitmap bmp = Bitmap.createBitmap( textureWidth , titleHeight , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		Log.d( "title" , "folder:width,height=" + textureWidth + "," + titleHeight );
		if( title_bg != null )
		{
			folderTitleFontTopping = singleLineHeight / 2f - title_bg.getHeight() / 2f;
			if( folderTitleFontTopping < 0 )
			{
				canvas.drawBitmap( Icon3D.titleBg , 0 , 0 , null );
			}
			else
			{
				canvas.drawBitmap( Icon3D.titleBg , 0 , folderTitleFontTopping , null );
			}
		}
		for( int i = 0 ; i < splitTitle.length ; i++ )
		{
			if( splitTitle[i].equals( "" ) )
				break;
			float textWidth = paint.measureText( splitTitle[i] );
			float paddintText = ( textureWidth - textWidth ) / 2;
			if( textWidth > R3D.workspace_cell_width )
			{
				if( title_bg != null )
				{
					paddintText = title_bg.getWidth() / 15f;
					if( textWidth > textureWidth - 2 * paddintText )
					{
						int length = paint.breakText( splitTitle[i] , 0 , splitTitle[i].length() , true , textureWidth - 2 * paddintText , null );
						splitTitle[i] = splitTitle[i].substring( 0 , length + 1 );
					}
				}
				else
				{
					paddintText = 0;
				}
			}
			float titleY = -fontMetrics.top + i * singleLineHeight;
			if( RR.net_version )
			{
				Resources res = iLoongLauncher.getInstance().getResources();
				int screenHeight = res.getDisplayMetrics().heightPixels;
				if( screenHeight == 1776 )
					titleY -= 5;
			}
			if( folderTitleFontTopping < 0 )
			{
				titleY -= folderTitleFontTopping / 2f;
			}
			// if (title_bg == null) {
			// paint.setColor(0x80000000);
			// paint.setShadowLayer(1f, -1f, 0f, 0x20000000);
			// canvas.drawText(splitTitle[i], paddintText - 1, titleY, paint);
			// paint.setShadowLayer(1f, 0f, -1f, 0x20000000);
			// canvas.drawText(splitTitle[i], paddintText, titleY - 1, paint);
			// paint.setShadowLayer(1f, 1f, 0f, 0x20000000);
			// canvas.drawText(splitTitle[i], paddintText + 1, titleY, paint);
			// paint.setShadowLayer(1f, 0f, 1f, 0x20000000);
			// canvas.drawText(splitTitle[i], paddintText, titleY + 1, paint);
			// paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
			// canvas.drawText(splitTitle[i], paddintText, titleY, paint);
			// paint.clearShadowLayer();
			// paint.setColor(Color.WHITE);
			// }
			// canvas.drawText(splitTitle[i], paddintText, titleY, paint);
			paint.setColor( Color.WHITE );
			if( title_bg == null )
			{
				paint.setShadowLayer( SHADOW_LARGE_RADIUS , 0.0f , SHADOW_Y_OFFSET , SHADOW_LARGE_COLOUR );
				canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
				paint.setShadowLayer( SHADOW_SMALL_RADIUS , 0.0f , 0.0f , SHADOW_SMALL_COLOUR );
				canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
			}
			else
			{
				paint.clearShadowLayer();
				canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
			}
			// draw opacity
			if( textWidth > textureWidth && title_bg == null )
			{
				Paint mErasePaint = new Paint();
				mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
				mErasePaint.setAntiAlias( true );
				float alphaW = paint.measureText( "x" );
				float a = 255f / alphaW;
				for( int j = 0 ; j < alphaW ; j++ )
				{
					mErasePaint.setAlpha( (int)( a * j ) );
					canvas.drawLine( textureWidth - j - 1 , (float)( titleY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , textureWidth - j , titleHeight , mErasePaint );
				}
			}
		}
		return bmp;
	}
	
	// public static float getFolderTitleHeight(){
	// Paint paint = new Paint();
	// paint.setColor(Color.WHITE);
	// paint.setAntiAlias(true);
	//
	// paint.setTextSize(R3D.icon_title_font);
	// float paddingLeft = 0;
	// float iconHeight = Utilities.sIconTextureWidth;
	// float cellHeight = R3D.workspace_cell_height;
	// int titleHeight = 0;
	// // if (Icon3D.getIconBg()!=null)
	// // {
	// // iconHeight=R3D.folder_front_width;
	// // }
	// paddingLeft = (R3D.workspace_cell_width - iconHeight) / 2;
	//
	// FontMetrics fontMetrics = paint.getFontMetrics();
	// float singleLineHeight = (float) Math.ceil(fontMetrics.bottom
	// - fontMetrics.top);
	// float tmp = cellHeight - iconHeight - 2*singleLineHeight;
	//
	// if(tmp<0){
	// iconHeight += paddingLeft*3/4;
	// }
	// else {
	// if(tmp > paddingLeft*2)tmp = paddingLeft*2;
	// iconHeight += tmp/2;
	// }
	// titleHeight = (int) (cellHeight - iconHeight);
	// return titleHeight;
	// }
	public static synchronized Bitmap titleToBitmap(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			int textureWidth ,
			int textureHeight )
	{
		return titleToBitmap( b , title , icn_bg , title_bg , textureWidth , textureHeight , true );
	}
	
	//zhujieping add
	public static float measureText(
			Paint paint ,
			String s )
	{
		Rect rect = new Rect();
		paint.getTextBounds( s , 0 , s.length() , rect );
		return rect.width();
	}
	
	public static Bitmap titleToPixmapWidthLimit(
			String title ,
			int widthLimit ,
			int size ,
			int color ,
			int line )
	{
		Paint paint = new Paint();
		paint.setColor( color );
		paint.setAntiAlias( true );
		paint.setTextSize( size );
		String[] splitTitle;
		if( line == 2 )
		{
			if( title != null )
			{
				splitTitle = splitTitle( title , size , widthLimit );
			}
			else
			{
				splitTitle = null;
			}
		}
		else
		{
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
		}
		Bitmap bmp = null;
		if( splitTitle != null && splitTitle.length > 0 )
		{
			int titleWidth = Math.min( (int)( paint.measureText( title ) ) , widthLimit );
			paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
			FontMetrics fontMetrics = paint.getFontMetrics();
			int singleLineHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
			bmp = Bitmap.createBitmap( titleWidth , singleLineHeight * splitTitle.length , Config.ARGB_8888 );
			Canvas canvas = new Canvas( bmp );
			float x = 0;
			float titleY = (float)( -fontMetrics.ascent );
			for( int i = 0 ; i < splitTitle.length ; i++ )
			{
				if( splitTitle[i].equals( "" ) )
					break;
				float textWidth = paint.measureText( splitTitle[i] );
				float paddingText = ( widthLimit - textWidth ) / 2;
				if( paddingText < 0 )
				{
					paddingText = 0;
				}
				titleY += i * singleLineHeight;
				paint.setColor( 0x80000000 );
				paint.setShadowLayer( 1f , -1f , 0f , 0x20000000 );
				canvas.drawText( splitTitle[i] , paddingText - 1 , titleY , paint );
				paint.setShadowLayer( 1f , 0f , -1f , 0x20000000 );
				canvas.drawText( splitTitle[i] , paddingText , titleY - 1 , paint );
				paint.setShadowLayer( 1f , 1f , 0f , 0x20000000 );
				canvas.drawText( splitTitle[i] , paddingText + 1 , titleY , paint );
				paint.setShadowLayer( 1f , 0f , 1f , 0x20000000 );
				canvas.drawText( splitTitle[i] , paddingText , titleY + 1 , paint );
				paint.setShadowLayer( 2f , 0.0f , 1.0f , 0xcc000000 );
				canvas.drawText( splitTitle[i] , paddingText , titleY , paint );
				paint.clearShadowLayer();
				paint.setColor( Color.WHITE );
				canvas.drawText( splitTitle[i] , paddingText , titleY , paint );
				// draw opacity
				if( textWidth > widthLimit )
				{
					Paint mErasePaint = new Paint();
					mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
					mErasePaint.setAntiAlias( true );
					float alphaW = paint.measureText( "x" );
					float a = 255f / alphaW;
					for( int j = 0 ; j < alphaW ; j++ )
					{
						mErasePaint.setAlpha( (int)( a * j ) );
						canvas.drawLine( widthLimit - j - 1 , (float)( titleY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , widthLimit - j , titleY , mErasePaint );
					}
				}
			}
		}
		return bmp;
	}
	
	static final float SHADOW_LARGE_RADIUS = 2.0f;
	static final float SHADOW_SMALL_RADIUS = 1.75f;
	static final float SHADOW_Y_OFFSET = 2.0f;
	static final int SHADOW_LARGE_COLOUR = 0xDD000000;
	static final int SHADOW_SMALL_COLOUR = 0xCC000000;
	
	public static synchronized Bitmap titleToBitmap(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			int textureWidth ,
			int textureHeight ,
			boolean recycle )
	{
		Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		int paddingTop;
		String[] splitTitle;
		int iconbgPaddingTop = 0;
		int paddingLeft = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
		//teapotXu_20130128: add icon_cover_plate start
		int iconcoverplatePaddingTop = 0;
		//		Bitmap icon_cover_plate_bmp = null;
		//		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap("theme/iconbg/icon_cover_plate.png");
		//		if(null != original_icon_cover_bmp){
		//			icon_cover_plate_bmp = Bitmap.createScaledBitmap(original_icon_cover_bmp,
		//					DefaultLayout.app_icon_size, DefaultLayout.app_icon_size,true);
		//		}		
		//teapotXu_20130128: add icon_cover_plate end
		//xiatian add start	//adjust third apk icon offset when have iconbg
		Rect iconbgRect = new Rect();
		int iconbgRectTop = 0;
		//xiatian add end
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2 );
			if( title != null )
			{
				splitTitle = splitTitle( title , R3D.icon_title_font , textureWidth );
			}
			else
			{
				splitTitle = null;
			}
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2 );
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
		}
		if( paddingTop < 0 )
			paddingTop = 0;
		//teapotXu_20130128: add icon_cover_plate start
		//save paddingTop first cause it will be modified when draw icon bitmap
		iconcoverplatePaddingTop = paddingTop;
		//teapotXu_20130128: add icon_cover_plate end		
		iconbgRectTop = (int)paddingTop; //xiatian add	//adjust third apk icon offset when have iconbg
		bmpHeight += ( paddingTop + space_height );
		int bitwidth = 0;
		if( (int)( DefaultLayout.app_icon_size * 142 / 116f ) > R3D.workspace_cell_width || (int)( DefaultLayout.app_icon_size * 142 / 96f ) > R3D.workspace_cell_height )
		{
			bitwidth = Math.min( R3D.workspace_cell_width , R3D.workspace_cell_height );
		}
		else
		{
			bitwidth = (int)( DefaultLayout.app_icon_size * 142 / 116f );
		}
		String str = R3D.getString( RR.string.mainmenu );
		if( title != null && !title.equals( str ) )
		{
			Bitmap shadow = null;
			Bitmap orishadow = ThemeManager.getInstance().getBitmap( "theme/iconbg/shadow.png" );
			if( orishadow != null )
			{
				shadow = Bitmap.createScaledBitmap( orishadow , bitwidth , bitwidth , true );
				if( orishadow != shadow )
					orishadow.recycle();
			}
			if( shadow != null )
			{
				float shadowPaddingLeft = 0;
				if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
				{
					shadowPaddingLeft = ( textureWidth - shadow.getWidth() ) / 2;
				}
				canvas.drawBitmap( shadow , shadowPaddingLeft , paddingTop , paint );
			}
		}
		if( title_bg != null )
			canvas.drawBitmap( title_bg , 0 , bmpHeight + singleLineHeight / 2 - title_bg.getHeight() / 2 , null );
		// Log.d("title", "icon:width,height="+textureWidth+","+(textureHeight -
		// bmpHeight));
		if( icn_bg != null )
		{
			//xiatian add start	//add icon shadow
			if( DefaultLayout.icon_shadow_radius != 0f )
			{
				icn_bg = addShadowToBitmap( icn_bg , false );
			}
			//xiatian add end
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					iconbgPaddingTop = paddingTop - ( icn_bg.getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( iconbgPaddingTop < 0 )
					{
						iconbgPaddingTop = 0;
					}
				}
				canvas.drawBitmap( icn_bg , ( textureWidth - icn_bg.getWidth() ) / 2 , iconbgPaddingTop , null );
				//xiatian add start	//adjust third apk icon offset when have iconbg
				iconbgRect.left = (int)( textureWidth - icn_bg.getWidth() ) / 2;
				iconbgRect.top = (int)iconbgPaddingTop;
				//xiatian add end
			}
			else
			{
				canvas.drawBitmap( icn_bg , 0 , 0 , null );
				//xiatian add start	//adjust third apk icon offset when have iconbg
				iconbgRect.left = 0;
				iconbgRect.top = 0;
				//xiatian add end
			}
			//xiatian add start	//adjust third apk icon offset when have iconbg
			iconbgRect.bottom = iconbgRect.top + icn_bg.getHeight();
			iconbgRect.right = iconbgRect.left + icn_bg.getWidth();
			//xiatian add end
		}
		int saveLayer = -1;
		if( b != null && !b.isRecycled() && Icon3D.getMask() != null && icn_bg != null )
		{
			saveLayer = canvas.saveLayer(
					0 ,
					0 ,
					textureWidth ,
					textureHeight ,
					null ,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG );
		}
		if( b != null && !b.isRecycled() )
		{
			//xiatian add start	//add icon shadow
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg == null ) )
			{
				b = addShadowToBitmap( b , true );
			}
			//xiatian add end
			// paint.setShadowLayer(2f, 0f, 2f, 0xff000000);
			if( b.getHeight() != Utilities.sIconTextureHeight )
			{
				if( icn_bg != null )
				{
					if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f )
					{
						paddingTop = iconbgPaddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
					}
					else
					{
						paddingTop = iconbgPaddingTop;
					}
				}
				else
				{
					paddingTop = paddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
				}
			}
			else
			{
				if( ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f ) && ( DefaultLayout.icon_shadow_radius != 0f ) )
				{
					paddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
			}
			if( b.getWidth() != Utilities.sIconTextureWidth )
			{
				paddingLeft = paddingLeft - ( b.getWidth() - Utilities.sIconTextureWidth ) / 2;
			}
			if( paddingTop < 0 )
			{
				paddingTop = 0;
			}
			if( paddingLeft < 0 )
			{
				paddingLeft = 0;
			}
			//xiatian add start	//add icon shadow
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f ) )
			{
				paddingTop += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
			}
			//xiatian add end
			//			//xiatian add start	//adjust third apk icon offset when have iconbg
			if( ( icn_bg != null ) && ( ( R3D.Third_APK_Icon_Offset_X != 0 ) || ( R3D.Third_APK_Icon_Offset_Y != 0 ) ) )
			{
				drawThirdAPKIconByOffsetInTheme( b , iconbgRect , iconbgPaddingTop , paddingTop , paddingLeft , canvas );
			}
			else
			//			//xiatian add end			
			//xiatian start	//support "thirdapk_icon_scaleFactor > 1",cut off the part which over icon region 
			//canvas.drawBitmap(b, paddingLeft, paddingTop, null);	//xiatian del
			//xiatian add start
			{
				if( ( DefaultLayout.thirdapk_icon_scaleFactor > 1.0f ) && ( icn_bg != null ) )
				{
					Rect src = new Rect( 0 , 0 , 0 , 0 );
					src.left = ( b.getWidth() - DefaultLayout.app_icon_size ) / 2;
					src.top = ( b.getHeight() - DefaultLayout.app_icon_size ) / 2;
					src.right = src.left + DefaultLayout.app_icon_size;
					src.bottom = src.top + DefaultLayout.app_icon_size;
					Rect dst = new Rect( 0 , 0 , 0 , 0 );
					dst.left = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
					dst.top = (int)iconbgPaddingTop;
					if( DefaultLayout.icon_shadow_radius != 0f )
					{
						dst.top += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					dst.right = dst.left + DefaultLayout.app_icon_size;
					dst.bottom = dst.top + DefaultLayout.app_icon_size;
					canvas.drawBitmap( b , src , dst , null );
				}
				else
				{
					canvas.drawBitmap( b , paddingLeft , paddingTop , null );
				}
			}
			//xiatian add end
			//xiatian end
			if( recycle && b != IconCache.mDefaultIcon )
				b.recycle();
		}
		if( DefaultLayout.dynamic_icon && icn_bg == null )
		{
			float icony;
			float hotseaty;
			icony = R3D.workspace_cell_height - ( paddingTop + Utilities.sIconTextureHeight ) - ( Math.min( b.getHeight() , b.getWidth() ) - Utilities.sIconTextureHeight ) / 2;
			DrawDynamicIcon.seticony( icony );
			hotseaty = icony + Utils3D.getIconBmpHeight() - R3D.workspace_cell_height;
			DrawDynamicIcon.sethotseaty( hotseaty );
		}
		if( Icon3D.getMask() != null && icn_bg != null && saveLayer != -1 )
		{
			float maskPaddingTop = 0;
			float maskPaddingLeft = 0;
			//			Bitmap icon_mask;
			//			//teapotXu add: when add icon shadow, the mask image should be recreated,
			//			if(DefaultLayout.icon_shadow_radius != 0){
			//				icon_mask = Bitmap.createBitmap(icn_bg.getWidth(), icn_bg.getHeight(),Config.ARGB_8888);
			//				Canvas c = new Canvas(icon_mask);
			//				
			//				c.drawBitmap(Icon3D.getMask(), DefaultLayout.icon_shadow_radius, DefaultLayout.icon_shadow_radius-1, null);
			//			}else{
			//				icon_mask = Icon3D.getMask();
			//			}
			//			//teapotXu add end
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					maskPaddingTop = iconcoverplatePaddingTop - ( Icon3D.getMask().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( DefaultLayout.icon_shadow_radius != 0 )
					{
						if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
							maskPaddingTop = paddingTop;
						else
							maskPaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					if( maskPaddingTop < 0 )
					{
						maskPaddingTop = 0;
					}
				}
				maskPaddingLeft = ( textureWidth - Icon3D.getMask().getWidth() ) / 2;
			}
			paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
			if( DefaultLayout.if_show_mask )
			{
				canvas.drawBitmap( Icon3D.getMask() , maskPaddingLeft , maskPaddingTop , paint );
			}
			paint.setXfermode( null );
			canvas.restoreToCount( saveLayer );
		}
		//teapotXu_20130128: add icon_cover_plate start
		if( DefaultLayout.if_show_cover )
		{
			if( ( icn_bg != null ) && Icon3D.getCover() != null )
			{
				if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
				{
					if( b != null )
					{
						iconcoverplatePaddingTop = iconcoverplatePaddingTop - ( Icon3D.getCover().getHeight() - Utilities.sIconTextureHeight ) / 2;
						if( DefaultLayout.icon_shadow_radius != 0 )
						{
							if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
								iconcoverplatePaddingTop = paddingTop;
							else
								iconcoverplatePaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
						}
						if( iconcoverplatePaddingTop < 0 )
						{
							iconcoverplatePaddingTop = 0;
						}
					}
					canvas.drawBitmap( Icon3D.getCover() , ( textureWidth - Icon3D.getCover().getWidth() ) / 2 , iconcoverplatePaddingTop , null );
				}
				else
				{
					canvas.drawBitmap( Icon3D.getCover() , 0 , 0 , null );
				}
			}
		}
		//teapotXu_20130128: add icon_cover_plate end
		if( splitTitle != null && splitTitle.length > 0 )
		{
			for( int i = 0 ; i < splitTitle.length ; i++ )
			{
				if( splitTitle[i].equals( "" ) )
					break;
				paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
				float textWidth = paint.measureText( splitTitle[i] );
				float paddintText = ( textureWidth - textWidth ) / 2;
				if( textWidth > R3D.workspace_cell_width )
				{
					if( title_bg != null )
					{
						paddintText = title_bg.getWidth() / 10f;
						if( textWidth > textureWidth - 2 * paddintText )
						{
							int length = paint.breakText( splitTitle[i] , 0 , splitTitle[i].length() , true , textureWidth - 2 * paddintText , null );
							splitTitle[i] = splitTitle[i].substring( 0 , length + 1 );
						}
					}
					else
					{
						paddintText = 0;
					}
				}
				// float tmpf = (textureHeight - bmpHeight -
				// 2*singleLineHeight)/ 2;
				// if(tmpf < 0)tmpf = 0;
				// float titleY = bmpHeight + tmpf -
				// fontMetrics.top+i*singleLineHeight;
				float titleY = bmpHeight - fontMetrics.ascent + i * singleLineHeight;
				paint.setColor( Color.WHITE );
				if( title_bg == null )
				{
					paint.setShadowLayer( SHADOW_LARGE_RADIUS , 0.0f , SHADOW_Y_OFFSET , SHADOW_LARGE_COLOUR );
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					//teapotXu add start for eton specific requirement
					if( DefaultLayout.setupmenu_yitong && DefaultLayout.icon_title_font == 15 )
					{
						//亿通项目15号字体显示有些粗，所以阴影只绘制一次，减小粗细
						// do nothing
					}
					else
					{
						paint.setShadowLayer( SHADOW_SMALL_RADIUS , 0.0f , 0.0f , SHADOW_SMALL_COLOUR );
						canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					}
					//teapotXu add end
				}
				else
				{
					paint.clearShadowLayer();
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
				}
				// draw opacity
				if( textWidth > textureWidth && title_bg == null )
				{
					Paint mErasePaint = new Paint();
					mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
					mErasePaint.setAntiAlias( true );
					float alphaW = paint.measureText( "x" );
					float a = 255f / alphaW;
					for( int j = 0 ; j < alphaW ; j++ )
					{
						mErasePaint.setAlpha( (int)( a * j ) );
						canvas.drawLine( textureWidth - j - 1 , (float)( titleY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , textureWidth - j , textureHeight , mErasePaint );
					}
				}
			}
		}
		return bmp;
	}
	
	public static boolean textShadow = true;
	public static int textColor = Color.WHITE;
	
	public static synchronized Bitmap titleToBitmap(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			int textureWidth ,
			int textureHeight ,
			boolean recycle ,
			boolean ifshadow )
	{
		Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		int paddingTop;
		String[] splitTitle;
		int iconbgPaddingTop = 0;
		int paddingLeft = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
		//teapotXu_20130128: add icon_cover_plate start
		int iconcoverplatePaddingTop = 0;
		//		Bitmap icon_cover_plate_bmp = null;
		//		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap("theme/iconbg/icon_cover_plate.png");
		//		if(null != original_icon_cover_bmp){
		//			icon_cover_plate_bmp = Bitmap.createScaledBitmap(original_icon_cover_bmp,
		//					DefaultLayout.app_icon_size, DefaultLayout.app_icon_size,true);
		//		}		
		//teapotXu_20130128: add icon_cover_plate end
		//xiatian add start	//adjust third apk icon offset when have iconbg
		Rect iconbgRect = new Rect();
		int iconbgRectTop = 0;
		//xiatian add end
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2 );
			if( title != null )
			{
				splitTitle = splitTitle( title , R3D.icon_title_font , textureWidth );
			}
			else
			{
				splitTitle = null;
			}
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2 );
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
		}
		if( paddingTop < 0 )
			paddingTop = 0;
		//teapotXu_20130128: add icon_cover_plate start
		//save paddingTop first cause it will be modified when draw icon bitmap
		iconcoverplatePaddingTop = paddingTop;
		//teapotXu_20130128: add icon_cover_plate end		
		iconbgRectTop = (int)paddingTop; //xiatian add	//adjust third apk icon offset when have iconbg
		bmpHeight += ( paddingTop + space_height );
		int bitwidth = 0;
		if( (int)( DefaultLayout.app_icon_size * 142 / 116f ) > R3D.workspace_cell_width || (int)( DefaultLayout.app_icon_size * 142 / 96f ) > R3D.workspace_cell_height )
		{
			bitwidth = Math.min( R3D.workspace_cell_width , R3D.workspace_cell_height );
		}
		else
		{
			bitwidth = (int)( DefaultLayout.app_icon_size * 142 / 116f );
		}
		if( ifshadow )
		{
			Bitmap shadow = null;
			Bitmap orishadow = ThemeManager.getInstance().getBitmap( "theme/iconbg/shadow.png" );
			if( orishadow != null )
			{
				shadow = Bitmap.createScaledBitmap( orishadow , bitwidth , bitwidth , true );
				if( orishadow != shadow )
					orishadow.recycle();
			}
			if( shadow != null )
			{
				float shadowPaddingLeft = 0;
				if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
				{
					shadowPaddingLeft = ( textureWidth - shadow.getWidth() ) / 2;
				}
				canvas.drawBitmap( shadow , shadowPaddingLeft , paddingTop , paint );
			}
		}
		if( title_bg != null )
			canvas.drawBitmap( title_bg , 0 , bmpHeight + singleLineHeight / 2 - title_bg.getHeight() / 2 , null );
		// Log.d("title", "icon:width,height="+textureWidth+","+(textureHeight -
		// bmpHeight));
		if( icn_bg != null )
		{
			//xiatian add start	//add icon shadow
			if( DefaultLayout.icon_shadow_radius != 0f )
			{
				icn_bg = addShadowToBitmap( icn_bg , false );
			}
			//xiatian add end
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					iconbgPaddingTop = paddingTop - ( icn_bg.getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( iconbgPaddingTop < 0 )
					{
						iconbgPaddingTop = 0;
					}
				}
				canvas.drawBitmap( icn_bg , ( textureWidth - icn_bg.getWidth() ) / 2 , iconbgPaddingTop , null );
				//xiatian add start	//adjust third apk icon offset when have iconbg
				iconbgRect.left = (int)( textureWidth - icn_bg.getWidth() ) / 2;
				iconbgRect.top = (int)iconbgPaddingTop;
				//xiatian add end
			}
			else
			{
				canvas.drawBitmap( icn_bg , 0 , 0 , null );
				//xiatian add start	//adjust third apk icon offset when have iconbg
				iconbgRect.left = 0;
				iconbgRect.top = 0;
				//xiatian add end
			}
			//xiatian add start	//adjust third apk icon offset when have iconbg
			iconbgRect.bottom = iconbgRect.top + icn_bg.getHeight();
			iconbgRect.right = iconbgRect.left + icn_bg.getWidth();
			//xiatian add end
		}
		int saveLayer = -1;
		if( b != null && !b.isRecycled() && Icon3D.getMask() != null && icn_bg != null )
		{
			saveLayer = canvas.saveLayer(
					0 ,
					0 ,
					textureWidth ,
					textureHeight ,
					null ,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG );
		}
		if( b != null && !b.isRecycled() )
		{
			//xiatian add start	//add icon shadow
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg == null ) )
			{
				b = addShadowToBitmap( b , true );
			}
			//xiatian add end
			// paint.setShadowLayer(2f, 0f, 2f, 0xff000000);
			if( b.getHeight() != Utilities.sIconTextureHeight )
			{
				if( icn_bg != null )
				{
					if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f )
					{
						paddingTop = iconbgPaddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
					}
					else
					{
						paddingTop = iconbgPaddingTop;
					}
				}
				else
				{
					paddingTop = paddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
				}
			}
			else
			{
				if( ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f ) && ( DefaultLayout.icon_shadow_radius != 0f ) )
				{
					paddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
			}
			if( b.getWidth() != Utilities.sIconTextureWidth )
			{
				paddingLeft = paddingLeft - ( b.getWidth() - Utilities.sIconTextureWidth ) / 2;
			}
			if( paddingTop < 0 )
			{
				paddingTop = 0;
			}
			if( paddingLeft < 0 )
			{
				paddingLeft = 0;
			}
			//xiatian add start	//add icon shadow
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f ) )
			{
				paddingTop += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
			}
			//xiatian add end
			//			//xiatian add start	//adjust third apk icon offset when have iconbg
			if( ( icn_bg != null ) && ( ( R3D.Third_APK_Icon_Offset_X != 0 ) || ( R3D.Third_APK_Icon_Offset_Y != 0 ) ) )
			{
				drawThirdAPKIconByOffsetInTheme( b , iconbgRect , iconbgPaddingTop , paddingTop , paddingLeft , canvas );
			}
			else
			//			//xiatian add end			
			//xiatian start	//support "thirdapk_icon_scaleFactor > 1",cut off the part which over icon region 
			//canvas.drawBitmap(b, paddingLeft, paddingTop, null);	//xiatian del
			//xiatian add start
			{
				if( ( DefaultLayout.thirdapk_icon_scaleFactor > 1.0f ) && ( icn_bg != null ) )
				{
					Rect src = new Rect( 0 , 0 , 0 , 0 );
					src.left = ( b.getWidth() - DefaultLayout.app_icon_size ) / 2;
					src.top = ( b.getHeight() - DefaultLayout.app_icon_size ) / 2;
					src.right = src.left + DefaultLayout.app_icon_size;
					src.bottom = src.top + DefaultLayout.app_icon_size;
					Rect dst = new Rect( 0 , 0 , 0 , 0 );
					dst.left = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
					dst.top = (int)iconbgPaddingTop;
					if( DefaultLayout.icon_shadow_radius != 0f )
					{
						dst.top += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					dst.right = dst.left + DefaultLayout.app_icon_size;
					dst.bottom = dst.top + DefaultLayout.app_icon_size;
					canvas.drawBitmap( b , src , dst , null );
				}
				else
				{
					canvas.drawBitmap( b , paddingLeft , paddingTop , null );
				}
			}
			//xiatian add end
			//xiatian end
			if( recycle && b != IconCache.mDefaultIcon )
				b.recycle();
		}
		if( DefaultLayout.dynamic_icon && icn_bg == null )
		{
			float icony;
			float hotseaty;
			icony = R3D.workspace_cell_height - ( paddingTop + Utilities.sIconTextureHeight ) - ( Math.min( b.getHeight() , b.getWidth() ) - Utilities.sIconTextureHeight ) / 2;
			DrawDynamicIcon.seticony( icony );
			hotseaty = icony + Utils3D.getIconBmpHeight() - R3D.workspace_cell_height;
			DrawDynamicIcon.sethotseaty( hotseaty );
		}
		if( Icon3D.getMask() != null && icn_bg != null && saveLayer != -1 )
		{
			float maskPaddingTop = 0;
			float maskPaddingLeft = 0;
			//			Bitmap icon_mask;
			//			//teapotXu add: when add icon shadow, the mask image should be recreated,
			//			if(DefaultLayout.icon_shadow_radius != 0){
			//				icon_mask = Bitmap.createBitmap(icn_bg.getWidth(), icn_bg.getHeight(),Config.ARGB_8888);
			//				Canvas c = new Canvas(icon_mask);
			//				
			//				c.drawBitmap(Icon3D.getMask(), DefaultLayout.icon_shadow_radius, DefaultLayout.icon_shadow_radius-1, null);
			//			}else{
			//				icon_mask = Icon3D.getMask();
			//			}
			//			//teapotXu add end
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					maskPaddingTop = iconcoverplatePaddingTop - ( Icon3D.getMask().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( DefaultLayout.icon_shadow_radius != 0 )
					{
						if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
							maskPaddingTop = paddingTop;
						else
							maskPaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					if( maskPaddingTop < 0 )
					{
						maskPaddingTop = 0;
					}
				}
				maskPaddingLeft = ( textureWidth - Icon3D.getMask().getWidth() ) / 2;
			}
			paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
			if( DefaultLayout.if_show_mask )
			{
				canvas.drawBitmap( Icon3D.getMask() , maskPaddingLeft , maskPaddingTop , paint );
			}
			paint.setXfermode( null );
			canvas.restoreToCount( saveLayer );
		}
		//teapotXu_20130128: add icon_cover_plate start
		if( DefaultLayout.if_show_cover )
		{
			if( ( icn_bg != null ) && Icon3D.getCover() != null )
			{
				if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
				{
					if( b != null )
					{
						iconcoverplatePaddingTop = iconcoverplatePaddingTop - ( Icon3D.getCover().getHeight() - Utilities.sIconTextureHeight ) / 2;
						if( DefaultLayout.icon_shadow_radius != 0 )
						{
							if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
								iconcoverplatePaddingTop = paddingTop;
							else
								iconcoverplatePaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
						}
						if( iconcoverplatePaddingTop < 0 )
						{
							iconcoverplatePaddingTop = 0;
						}
					}
					canvas.drawBitmap( Icon3D.getCover() , ( textureWidth - Icon3D.getCover().getWidth() ) / 2 , iconcoverplatePaddingTop , null );
				}
				else
				{
					canvas.drawBitmap( Icon3D.getCover() , 0 , 0 , null );
				}
			}
		}
		//teapotXu_20130128: add icon_cover_plate end
		if( splitTitle != null && splitTitle.length > 0 )
		{
			for( int i = 0 ; i < splitTitle.length ; i++ )
			{
				if( splitTitle[i].equals( "" ) )
					break;
				paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
				float textWidth = paint.measureText( splitTitle[i] );
				float paddintText = ( textureWidth - textWidth ) / 2;
				if( textWidth > R3D.workspace_cell_width )
				{
					if( title_bg != null )
					{
						paddintText = title_bg.getWidth() / 10f;
						if( textWidth > textureWidth - 2 * paddintText )
						{
							int length = paint.breakText( splitTitle[i] , 0 , splitTitle[i].length() , true , textureWidth - 2 * paddintText , null );
							splitTitle[i] = splitTitle[i].substring( 0 , length + 1 );
						}
					}
					else
					{
						paddintText = 0;
					}
				}
				// float tmpf = (textureHeight - bmpHeight -
				// 2*singleLineHeight)/ 2;
				// if(tmpf < 0)tmpf = 0;
				// float titleY = bmpHeight + tmpf -
				// fontMetrics.top+i*singleLineHeight;
				float titleY = bmpHeight - fontMetrics.ascent + i * singleLineHeight;
				paint.setColor( textColor );
				if( title_bg == null )
				{
					if( textShadow )
					{
						paint.setShadowLayer( SHADOW_LARGE_RADIUS , 0.0f , SHADOW_Y_OFFSET , SHADOW_LARGE_COLOUR );
					}
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					//teapotXu add start for eton specific requirement
					if( DefaultLayout.setupmenu_yitong && DefaultLayout.icon_title_font == 15 )
					{
						//亿通项目15号字体显示有些粗，所以阴影只绘制一次，减小粗细
						// do nothing
					}
					else
					{
						if( textShadow )
						{
							paint.setShadowLayer( SHADOW_SMALL_RADIUS , 0.0f , 0.0f , SHADOW_SMALL_COLOUR );
						}
						canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					}
					//teapotXu add end
				}
				else
				{
					paint.clearShadowLayer();
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
				}
				// draw opacity
				if( textWidth > textureWidth && title_bg == null )
				{
					Paint mErasePaint = new Paint();
					mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
					mErasePaint.setAntiAlias( true );
					float alphaW = paint.measureText( "x" );
					float a = 255f / alphaW;
					for( int j = 0 ; j < alphaW ; j++ )
					{
						mErasePaint.setAlpha( (int)( a * j ) );
						canvas.drawLine( textureWidth - j - 1 , (float)( titleY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , textureWidth - j , textureHeight , mErasePaint );
					}
				}
			}
		}
		return bmp;
	}
	
	public static float getNumberWidth(
			int number )
	{
		String s = number + "";
		char c;
		TextureRegion region;
		float width = 0;
		for( int i = 0 ; i < s.length() ; i++ )
		{
			c = s.charAt( i );
			region = R3D.findRegion( "photo-" + c );
			width += region.getRegionWidth();
		}
		return width;
	}
	
	public static float getTitleHeight(
			float size ,
			int line )
	{
		Paint paint = new Paint();
		paint.setTextSize( size );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
		return line * singleLineHeight;
	}
	
	public static float getTitleWidth(
			String s ,
			float size )
	{
		Paint paint = new Paint();
		paint.setTextSize( size );
		return paint.measureText( s );
	}
	
	public static float getIconBmpHeight()
	{
		if( iconBmpHeight != -1 )
			return iconBmpHeight;
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		// paint.setColor(Color.WHITE);
		// paint.setAntiAlias(true);
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		int paddingTop;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2 );
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = (int)( ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2 );
		}
		if( paddingTop < 0 )
		{
			paddingTop = 0;
		}
		space_height -= R3D.hot_top_ascent_distance_hide_title;
		if( DefaultLayout.show_font_bg == true && Icon3D.titleBg != null )
		{
			space_height -= ( Icon3D.titleBg.getHeight() - singleLineHeight ) / 2f;
		}
		if( space_height < 0 )
			space_height = 0;
		iconBmpHeight = bmpHeight + paddingTop + space_height;
		return iconBmpHeight;
	}
	
	public static String[] splitTitle(
			String title ,
			float textSize ,
			float maxWidth )
	{
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setTextSize( textSize );
		String[] s = title.split( " " );
		String s1 = s[0];
		String s2 = "";
		float s1Width = paint.measureText( s1 );
		if( s1Width > maxWidth )
		{
			int tmp = paint.breakText( s1 , true , maxWidth , null );
			s1 = s1.substring( 0 , tmp );
			int n = s1.length();
			if( n < title.length() )
			{
				s2 = title.substring( n );
			}
			// Log.e("launcher", "1 splitTitle:s1,s2="+s1+","+s2);
			return new String[]{ s1 , s2 };
		}
		float blankWidth = paint.measureText( " " );
		for( int i = 1 ; i < s.length ; i++ )
		{
			s1Width = paint.measureText( s1 );
			float tmpWidth = paint.measureText( s[i] );
			if( ( s1Width + tmpWidth + blankWidth ) <= maxWidth )
			{
				s1 += " " + s[i];
			}
			else
			{
				int n = s1.length();
				if( n + 1 < title.length() )
				{
					s2 = title.substring( n );
				}
				break;
			}
		}
		// Log.e("launcher", "2 splitTitle:s1,s2="+s1+","+s2);
		return new String[]{ s1 , s2 };
	}
	
	public static Bitmap IconToPixmap3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg )
	{
		return IconToPixmap3D( b , title , icn_bg , title_bg , true );
	}
	
	public static Bitmap IconToPixmap3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			boolean recycle )
	{
		int textureWidth = R3D.workspace_cell_width;
		int textureHeight = R3D.workspace_cell_height;
		return titleToBitmap( b , title , icn_bg , title_bg , textureWidth , textureHeight , recycle );
		// enhance the shadow by drawing the shadow twice
		//
		// paint.setShadowLayer(1f, 1.0f, 0.0f, 0xaa000000);
		// canvas.drawText(title, paddintText, fontPosY, paint);
		// paint.setShadowLayer(1f, -1.0f, 0.0f, 0xaa000000);
		// canvas.drawText(title, paddintText, fontPosY, paint);
		// paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
		// canvas.drawText(title, paddintText, fontPosY, paint);
		// paint.setShadowLayer(2f, 0.0f, -1.0f, 0xcc000000);
		// canvas.drawText(title, paddintText, fontPosY, paint);
	}
	
	public static Bitmap IconToPixmap3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			boolean recycle ,
			boolean ifshadow )
	{
		int textureWidth = R3D.workspace_cell_width;
		int textureHeight = R3D.workspace_cell_height;
		return titleToBitmap( b , title , icn_bg , title_bg , textureWidth , textureHeight , recycle , ifshadow );
	}
	
	public static int dip2px(
			Context context ,
			float dpValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		Log.v( "dip2px" , "scale:" + scale + "original:" + dpValue + " now:" + ( dpValue * scale + 0.5f ) );
		return (int)( dpValue * scale );
	}
	
	public static int px2dip(
			Context context ,
			float pxValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( pxValue / scale );
	}
	
	public static Drawable createIconThumbnail(
			Drawable icon ,
			int width ,
			int height )
	{
		Rect mOldBounds = new Rect();
		Canvas mCanvas = new Canvas();
		DisplayMetrics mMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( mMetrics );
		// int mIconWidth = (int) iLoongLauncher.getInstance().getResources()
		// .getDimension(R.dimen.app_icon_size);
		// int mIconHeight = (int) iLoongLauncher.getInstance().getResources()
		// .getDimension(R.dimen.app_icon_size);
		// int width = mIconWidth;
		// int height = mIconHeight;
		try
		{
			if( icon instanceof PaintDrawable )
			{
				PaintDrawable painter = (PaintDrawable)icon;
				painter.setIntrinsicWidth( width );
				painter.setIntrinsicHeight( height );
			}
			else if( icon instanceof BitmapDrawable )
			{
				// Ensure the bitmap has a density.
				BitmapDrawable bitmapDrawable = (BitmapDrawable)icon;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if( bitmap.getDensity() == Bitmap.DENSITY_NONE )
				{
					bitmapDrawable.setTargetDensity( mMetrics );
				}
			}
			int iconWidth = icon.getIntrinsicWidth();
			int iconHeight = icon.getIntrinsicHeight();
			if( iconWidth > 0 && iconHeight > 0 )
			{
				if( width < iconWidth || height < iconHeight )
				{
					final float ratio = (float)iconWidth / iconHeight;
					if( iconWidth > iconHeight )
					{
						height = (int)( width / ratio );
					}
					else if( iconHeight > iconWidth )
					{
						width = (int)( height * ratio );
					}
					final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
					final Bitmap thumb = Bitmap.createBitmap( width , height , c );
					final Canvas canvas = mCanvas;
					canvas.setBitmap( thumb );
					// Copy the old bounds to restore them later
					// If we were to do oldBounds = icon.getBounds(),
					// the call to setBounds() that follows would
					// change the same instance and we would lose the
					// old bounds
					mOldBounds.set( icon.getBounds() );
					final int x = 0;
					final int y = 0;
					icon.setBounds( x , y , x + width , y + height );
					icon.draw( canvas );
					icon.setBounds( mOldBounds );
					// noinspection deprecation
					( (BitmapDrawable)icon ).getBitmap().recycle();
					icon = new BitmapDrawable( thumb );
					( (BitmapDrawable)icon ).setTargetDensity( mMetrics );
				}
				else if( iconWidth < width && iconHeight < height )
				{
					final Bitmap.Config c = Bitmap.Config.ARGB_8888;
					final Bitmap thumb = Bitmap.createBitmap( width , height , c );
					final Canvas canvas = mCanvas;
					canvas.setBitmap( thumb );
					mOldBounds.set( icon.getBounds() );
					final int x = ( width - iconWidth ) / 2;
					final int y = ( height - iconHeight ) / 2;
					icon.setBounds( x , y , x + iconWidth , y + iconHeight );
					icon.draw( canvas );
					icon.setBounds( mOldBounds );
					// noinspection deprecation
					( (BitmapDrawable)icon ).getBitmap().recycle();
					icon = new BitmapDrawable( thumb );
					( (BitmapDrawable)icon ).setTargetDensity( mMetrics );
				}
			}
		}
		catch( Throwable t )
		{
			t.printStackTrace();
		}
		return icon;
	}
	
	public static void setLanguage(
			Context context ,
			String lang )
	{
		Log.e( "iLoongLauncher" , "setLanguage:" + lang );
		String languageToLoad = lang;
		Locale locale = new Locale( languageToLoad );
		Locale.setDefault( locale );
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration( config , null );
	}
	
	//xiatian add start	//adjust third apk icon offset when have iconbg
	public static void drawThirdAPKIconByOffsetInTheme(
			Bitmap b ,
			Rect iconbgRect ,
			int iconbgPaddingTop ,
			int paddingTop ,
			int paddingLeft ,
			Canvas canvas )
	{
		int offsetX = R3D.Third_APK_Icon_Offset_X;//<0：水平左移；=0：水平居中；>0：水平右移；
		int offsetY = R3D.Third_APK_Icon_Offset_Y;//<0：垂直下移；=0：垂直居中；>0：垂直上移；
		Rect src = new Rect( 0 , 0 , b.getWidth() , b.getHeight() );
		Rect dst = new Rect( iconbgRect.left , iconbgRect.top , iconbgRect.right , iconbgRect.bottom );
		int iconBgPaddingTop = (int)iconbgPaddingTop;
		int iconBgPaddingLeft = iconbgRect.left;
		int bgPaddingTopiconBg = (int)( paddingTop - iconBgPaddingTop );
		int bgPaddingLefticonBg = (int)( paddingLeft - iconBgPaddingLeft );
		if( offsetX < 0 )
		{//<0：水平左移
			if( Math.abs( offsetX ) <= bgPaddingLefticonBg )
			{
				dst.right -= ( Math.abs( offsetX ) + bgPaddingLefticonBg );
				dst.left = dst.right - b.getWidth();
			}
			else
			{
				if( DefaultLayout.icon_shadow_radius != 0f )
				{
					dst.left += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
				src.left = Math.abs( offsetX ) - bgPaddingLefticonBg;
				dst.right = dst.left + ( src.right - src.left );
			}
		}
		else if( offsetX == 0 )
		{//=0：水平居中
			if( DefaultLayout.thirdapk_icon_scaleFactor > 1.0f )
			{
			}
			else
			{
				dst.left += bgPaddingLefticonBg;
				dst.right = dst.left + b.getWidth();
			}
		}
		else if( offsetX > 0 )
		{//>0：水平右移
			if( offsetX <= bgPaddingLefticonBg )
			{
				dst.left += ( offsetX + bgPaddingLefticonBg );
				dst.right = dst.left + b.getWidth();
			}
			else
			{
				if( DefaultLayout.icon_shadow_radius != 0f )
				{
					dst.right -= Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
				src.right = b.getWidth() - ( offsetX - bgPaddingLefticonBg );
				dst.left = dst.right - src.right;
			}
		}
		if( offsetY < 0 )
		{//<0：垂直下移
			if( Math.abs( offsetY ) <= bgPaddingTopiconBg )
			{
				dst.top += ( Math.abs( offsetY ) + bgPaddingTopiconBg );
				dst.bottom = dst.top + b.getHeight();
			}
			else
			{
				if( DefaultLayout.icon_shadow_radius != 0f )
				{
					dst.bottom -= 3 * Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
				src.bottom = b.getHeight() - ( Math.abs( offsetY ) - bgPaddingTopiconBg );
				dst.top = dst.bottom - src.bottom;
			}
		}
		else if( offsetY == 0 )
		{//=0：垂直居中
			if( ( DefaultLayout.thirdapk_icon_scaleFactor > 1.0f ) )
			{
			}
			else
			{
				dst.top += bgPaddingTopiconBg;
				dst.bottom = dst.top + b.getHeight();
			}
		}
		else if( offsetY > 0 )
		{//>0：垂直上移
			if( offsetY <= bgPaddingTopiconBg )
			{
				dst.bottom -= ( offsetY + bgPaddingTopiconBg );
				if( DefaultLayout.icon_shadow_radius != 0f )
				{
					dst.bottom -= 2 * Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
				dst.top = dst.bottom - b.getHeight();
			}
			else
			{
				if( DefaultLayout.icon_shadow_radius != 0f )
				{
					dst.top += 2 * Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
				src.top = offsetY - bgPaddingTopiconBg;
				dst.bottom = dst.top + ( src.bottom - src.top );
			}
		}
		canvas.drawBitmap( b , src , dst , null );
	}
	
	//xiatian add end
	//xiatian add start	//add icon shadow
	private static Bitmap addShadowToBitmap(
			Bitmap originalBitmap ,
			boolean recycle )
	{
		if( DefaultLayout.icon_shadow_radius == 0f )
			return originalBitmap;
		Paint shadowPaint = new Paint();
		shadowPaint.setAlpha( 0x80 );//0x00(全透明),0x80(半透明),0xFF(不透明)
		BlurMaskFilter blurFilter = new BlurMaskFilter( Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius ) , BlurMaskFilter.Blur.NORMAL );
		shadowPaint.setMaskFilter( blurFilter );
		int[] offsetXY = new int[2];
		Bitmap shadowBitmap = originalBitmap.extractAlpha( shadowPaint , offsetXY );
		Bitmap shadowBitmap32 = shadowBitmap.copy( Config.ARGB_8888 , true );
		Bitmap ret = Bitmap.createBitmap( shadowBitmap32.getWidth() , shadowBitmap32.getHeight() - 2 * offsetXY[1] , Config.ARGB_8888 );
		if( android.os.Build.VERSION.SDK_INT >= 19 )
		{
			//shadowBitmap32.setPremultiplied( true );
			try
			{
				Method method = shadowBitmap32.getClass().getDeclaredMethod( "setPremultiplied" , boolean.class );
				method.invoke( shadowBitmap32 , true );
				method.invoke( ret , true );
			}
			catch( IllegalArgumentException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( NoSuchMethodException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( IllegalAccessException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( InvocationTargetException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Canvas c = new Canvas( ret );
		Rect srcShadow = new Rect( -offsetXY[0] , -offsetXY[1] , -offsetXY[0] + originalBitmap.getWidth() , shadowBitmap32.getHeight() );
		Rect dstShadow = new Rect( -offsetXY[0] , -2 * offsetXY[1] , -offsetXY[0] + originalBitmap.getWidth() , -offsetXY[1] + shadowBitmap32.getHeight() );
		c.drawBitmap( shadowBitmap32 , srcShadow , dstShadow , shadowPaint );
		Rect srcOriginal = new Rect( 0 , 0 , originalBitmap.getWidth() , originalBitmap.getHeight() );
		Rect dstOriginal = new Rect( -offsetXY[0] , -offsetXY[1] , -offsetXY[0] + originalBitmap.getWidth() , -offsetXY[1] + originalBitmap.getHeight() );
		c.drawBitmap( originalBitmap , srcOriginal , dstOriginal , null );
		if( recycle == true )
			originalBitmap.recycle();
		return ret;
	}
	
	//xiatian add end
	public static synchronized Bitmap TitleToBitmap(
			String title ,
			Bitmap title_bg ,
			int textureWidth ,
			int fontSize ,
			int color )
	{
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setColor( color );
		paint.setTextSize( fontSize );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float fontLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		Bitmap bmp = Bitmap.createBitmap( textureWidth , (int)fontLineHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		if( title_bg != null )
		{
			canvas.drawBitmap( title_bg , 0 , 0 , null );
		}
		float textWidth = paint.measureText( title );
		float marginLeft = ( textureWidth - textWidth ) / 2f;
		if( marginLeft < 0 )
		{
			marginLeft = 0;
		}
		canvas.drawText( title , marginLeft , fontLineHeight - fontMetrics.bottom , paint );
		return bmp;
	}
	
	public static synchronized void changeStatusbar(
			String string ,
			boolean bool1 ,
			boolean bool2 )
	{
		if( !DefaultLayout.reflect_change_statusbar )
			return;
		Class<?> mStatusBarManager = null;
		Object obj = null;
		try
		{
			mStatusBarManager = Class.forName( "android.app.StatusBarManager" );
			obj = iLoongLauncher.getInstance().getSystemService( "statusbar" );
			Method m = mStatusBarManager.getDeclaredMethod( "setViewToStatusbar" , String.class , boolean.class , boolean.class );
			m.invoke( obj , string , bool1 , bool2 );
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		//		StatusBarManager mStatusBarManager =
		//				  (StatusBarManager)iLoongLauncher.getInstance().getSystemService("statusbar");
		//				 
		//		//topwise zyf add for statusbar alpha
		//	      if (mStatusBarManager != null){
		//	                  Log.d("statusbarzm", "onResume  .............");
		//	                  if (isAllAppsVisible()){
		//	 
		//							  mStatusBarManager.setViewToStatusbar("topwisemenu",true,false);
		//							                        mStatusBarManager.setViewToStatusbar
		//							  ("topwiseidle",true,true);
		//	                        Log.d("statusbarzm", "onWindowFocusChanged,isallappvisible");
		//	                  }else{
		//	 
		//	  mStatusBarManager.setViewToStatusbar("topwiseidle",true,true);
		//	                        Log.d("statusbarzm", "onWindowFocusChanged,isidle");
		//	                  }
		//	      }
		//	    //topwise zyf add for statusbar alpha
		//	      if (mStatusBarManager != null){
		//	 
		//	  mStatusBarManager.setViewToStatusbar("topwisemenu",false,false);
		//	 
		//	  mStatusBarManager.setViewToStatusbar("topwiseidle",false,false);
		//	                  Log.d("statusbarzm", "onPause ...........");
		//	              }
		//	  //topwise zyf add for statusbar alpha end
		//				 
		//	  ////topwise zyf add for statusbar alpha
		//		  if (mStatusBarManager != null)
		//		  {
		//		            Log.d("statusbarzm", "showWorkspace..............");
		//		 
		//		  mStatusBarManager.setViewToStatusbar("topwisemenu",false,false);
		//		 
		//		  mStatusBarManager.setViewToStatusbar("topwiseidle",true,true);
		//		  }
		//		  ////topwise zyf add for statusbar alpha end
		//		  
		//		////topwise zyf add for statusbar alpha
		//		  if (mStatusBarManager != null)
		//		  {
		//		            Log.d("statusbarzm", "showAllApps..............");
		//		 
		//		  mStatusBarManager.setViewToStatusbar("topwiseidle",false,false);
		//		 
		//		  mStatusBarManager.setViewToStatusbar("topwisemenu",true,true);
		//		  }
		//		  ////topwise zyf add for statusbar alpha end
	}
	
	public static synchronized String getHQEditmodeAction()
	{
		if( !DefaultLayout.huaqin_enable_edit_mode )
			return "";
		Log.d( "doov" , "getHQEditmodeAction" );
		Class<?> Doovvistorfeature = null;
		try
		{
			Doovvistorfeature = Class.forName( "android.util.Doovvistorfeature" );
			Field field = Doovvistorfeature.getDeclaredField( "ACTION_DOOV_VISTOR" );
			String s = field.get( null ).toString();
			Log.d( "doov" , "action:" + s );
			return s;
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return "";
	}
	
	public static synchronized boolean isHQEditmode(
			Context context ,
			String javafile ,
			String function )
	{
		if( !DefaultLayout.huaqin_enable_edit_mode )
			return false;
		Log.d( "doov" , "isHQEditmode" );
		Class<?> Doovvistorfeature = null;
		try
		{
			Doovvistorfeature = Class.forName( "android.util.Doovvistorfeature" );
			Method m = Doovvistorfeature.getDeclaredMethod( "initDoovVistor" , Context.class , String.class , String.class );
			Object obj = m.invoke( null , new Object[]{ context , javafile , function } );
			Log.d( "doov" , "editMode:" + obj );
			if( obj instanceof Boolean )
			{
				return (Boolean)obj;
			}
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
		}
		return false;
	}
	
	public static void launchNotification()
	{
		Method expand = null;
		int curApiVersion = android.os.Build.VERSION.SDK_INT;
		try
		{
			Object service = iLoongLauncher.getInstance().getSystemService( "statusbar" );
			Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
			if( statusbarManager == null )
				return;
			if( curApiVersion <= 16 )
			{
				expand = statusbarManager.getMethod( "expand" );
			}
			else
			{
				expand = statusbarManager.getMethod( "expandNotificationsPanel" );
			}
			if( expand != null )
			{
				expand.setAccessible( true );
				expand.invoke( service );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	//	public static String distinctMerge(
	//			String name1 ,
	//			String name2 )
	//	{
	//		String s = "";
	//		String[] array = ( name1 + ";" + name2 ).split( ";" );
	//		String[] res = new String[array.length];
	//		for( int i = 0 ; i < array.length ; i++ )
	//		{
	//			String tmp = array[i];
	//			if( tmp == null || tmp.equals( "" ) )
	//				continue;
	//			boolean found = false;
	//			for( int j = 0 ; j < res.length ; j++ )
	//			{
	//				if( res[j] != null && tmp.equals( res[j] ) )
	//				{
	//					found = true;
	//					break;
	//				}
	//			}
	//			if( !found )
	//			{
	//				res[i] = tmp;
	//				if( !s.equals( "" ) )
	//					s += ";" + tmp;
	//				else
	//					s = tmp;
	//			}
	//		}
	//		return s;
	//	}
	// Jone add ,following codes are added for capturing the screenshot
	public static void RGBA8888toARGB8888(
			byte[] src ,
			int[] dest )
	{
		int j = 0;
		for( int i = 0 ; i < src.length ; i += 4 )
		{
			dest[j++] = ( src[i + 3] & 0x000000ff ) << 24 | ( src[i] & 0x000000ff ) << 16 | ( src[i + 1] & 0x000000ff ) << 8 | ( src[i + 2] & 0x000000ff );
		}
	}
	
	public static String getScreenShotPath()
	{
		return mScreenShotPath;
	}
	
	public static Bitmap setScreenshotBackground(
			Bitmap fore ,
			Bitmap bg )
	{
		Bitmap bmp = Bitmap.createBitmap( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		canvas.drawBitmap( bg , 0 , 0 , null );
		canvas.drawBitmap( fore , 0 , 0 , null );
		return bmp;
	}
	
	public static long cur;
	
	public static String getScreenShot(
			Bitmap bg )
	{
		byte[] buffer;
		int[] bmpBuffer = new int[Utils3D.getScreenWidth() * Utils3D.getScreenHeight()];
		cur = System.currentTimeMillis();
		Log.v( "Root3D" , " Begin cur: " + cur );
		buffer = ScreenUtils.getFrameBufferPixels( true );
		Log.v( "Root3D" , " end cur: " + ( System.currentTimeMillis() - cur ) );
		cur = System.currentTimeMillis();
		Log.v( "Root3D" , " Begin cur1111: " + cur );
		RGBA8888toARGB8888( buffer , bmpBuffer );
		Log.v( "Root3D" , " end cur11111: " + ( System.currentTimeMillis() - cur ) );
		Bitmap bitmap = Bitmap.createBitmap( bmpBuffer , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() , Config.ARGB_8888 );
		mScreenShotPath = Environment.getExternalStorageDirectory() + File.separator + "coco" + File.separator + "share" + File.separator + "cooeeShare.png";
		// String tempPath
		// =Environment.getExternalStorageDirectory()+File.separator+"coco"+File.separator+"share"+File.separator+"foreground.png";
		// ShowDesktop.saveBmp(bitmap,tempPath);
		ShowDesktop.saveBmp( setScreenshotBackground( bitmap , bg ) , mScreenShotPath );
		// ShowDesktop.saveBmpToSystem(setScreenshotBackground(bitmap,bg),mScreenShotPath);
		return mScreenShotPath;
	}
	
	// Jone end
	//************************************以下代码是判断手机上是否有指定的APP如果没有将安装Assets-->APK目录下的apk文件*********************************************
	/**
	 * 此函数是判断手机上是否有指定的APP
	 * @param context 上下文对象
	 * @param packageName app的包名
	 * @return 如果手机上已经存在指定的app则返回true
	 * 否则返回false
	 */
	public static boolean isAPKInstalled(
			Context context ,
			String packageName )
	{
		try
		{
			context.getPackageManager().getPackageInfo( packageName , PackageManager.GET_ACTIVITIES );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 此函数为安装Assets目录的APK目录下指定的的APK文件
	 * @param mContext 上下文对象
	 * @param apkName  需要安装的APK的名称该APK位于安装Assets目录的APK目录下
	 */
	public static void installAssetsAPK(
			Context mContext ,
			String apkName )
	{
		if( copyApkFromAssets( mContext , apkName , Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + apkName ) )
		{
			Intent intent = new Intent( Intent.ACTION_VIEW );
			intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			intent.setDataAndType( Uri.parse( "file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + apkName ) , "application/vnd.android.package-archive" );
			mContext.startActivity( intent );
		}
	}
	
	/**
	 * 此函数配合 installAssetsAPK函数使用,其作用是将Assets中的APK到指定的path目录中
	 * @param context 上下文对象
	 * @param fileName 拷贝后的文件名
	 * @param path 指定的拷贝路径
	 * @return 返回是否拷贝成功，成功返回true,失败返回false;
	 */
	public static boolean copyApkFromAssets(
			Context context ,
			String fileName ,
			String path )
	{
		boolean copyIsFinish = false;
		try
		{
			InputStream is = context.getAssets().open( "apk/" + fileName );
			File file = new File( path );
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream( file );
			byte[] temp = new byte[1024];
			int i = 0;
			while( ( i = is.read( temp ) ) > 0 )
			{
				fos.write( temp , 0 , i );
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return copyIsFinish;
	}
	
	//************************************End*****代码是判断手机上是否有指定的APP如果没有将安装Assets-->APK目录下的apk文件*********************************************
	/**
	 * 判断Assets文件下的apk文件夹下是否存在于fileName匹配的文件
	 * @param context 上下文对象
	 * @param fileName 文件名
	 * @return 存在返回TRUE
	 *         不存在返回FALSE
	 */
	public static boolean isOrnotFileExists(
			Context context ,
			String fileName )
	{
		boolean isOrNotExists = false;
		try
		{
			InputStream is = context.getAssets().open( "apk/" + fileName );
			is.close();
			isOrNotExists = true;
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			isOrNotExists = false;
		}
		return isOrNotExists;
	}
	
	public static String getTopActivity(
			Context context )
	{
		ActivityManager manager = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks( 1 );
		if( runningTaskInfos != null )
			return ( runningTaskInfos.get( 0 ).topActivity ).toString();
		else
			return null;
	}
}
