package com.iLoong.launcher.UI3DEngine;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.PNGEncode;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.cut;
import com.iLoong.launcher.SetupMenu.Actions.ShowDesktop;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class Utils3D
{
	
	private static final boolean DEBUG = false; // 增加debug调试开关,保证一些代码只在调试时执行,默认为关闭.
	static Vector2 liangVector = new Vector2( 0 , 1 );
	static Object exe_lock = new Object();
	static int[] defaultPixels;
	static byte[] defaultBmpbuff;
	private static int width = 0;
	private static int height = 0;
	public static int statusBarHeight = 0;
	static float folderTitleFontTopping = 0;
	public static int realWidth;
	public static int realHeight;
	public static String mScreenShotPath = null;
	public static Paint paint = new Paint();
	public static Canvas canvas = new Canvas();
	public static FontMetrics fontMetrics = new FontMetrics();
	public static float iconBmpHeight = -1;
	
	static public int showPidMemoryInfo(
			String title )
	{
		if( DEBUG )
		{
			int myProcessID = Process.myPid();
			int pids[] = new int[1];
			pids[0] = myProcessID;
			ActivityManager am = (ActivityManager)iLoongLauncher.getInstance().getSystemService( Context.ACTIVITY_SERVICE );
			MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo( pids );
			MemoryInfo pidMemoryInfo = memoryInfoArray[0];
			Log.i( "MemoryInfo" , "MemoryInfo:" + title + ":pss = " + pidMemoryInfo.getTotalPss() + "K" + " ,uss:" + pidMemoryInfo.getTotalPrivateDirty() );
			return pidMemoryInfo.getTotalPss();
		}
		return -1;
	}
	
	static public int showPidMemoryInfo(
			Context context ,
			String title )
	{
		if( DEBUG )
		{
			int myProcessID = Process.myPid();
			int pids[] = new int[1];
			pids[0] = myProcessID;
			ActivityManager am = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
			MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo( pids );
			MemoryInfo pidMemoryInfo = memoryInfoArray[0];
			Log.i( "MemoryInfo" , "MemoryInfo:" + title + " = " + pidMemoryInfo.getTotalPss() + "K" + " time:" + System.currentTimeMillis() );
			return pidMemoryInfo.getTotalPss();
		}
		return -1;
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
		Toast.makeText( iLoongApplication.ctx , str , Toast.LENGTH_SHORT ).show();
	}
	
	public static int getStatusBarHeight()
	{
		if( statusBarHeight != 0 )
			return statusBarHeight;
		Resources res = iLoongLauncher.getInstance().getResources();
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try
		{
			c = Class.forName( "com.android.internal.R$dimen" );
			obj = c.newInstance();
			field = c.getField( "status_bar_height" );
			x = Integer.parseInt( field.get( obj ).toString() );
			statusBarHeight = res.getDimensionPixelSize( x );
		}
		catch( Exception e1 )
		{
			e1.printStackTrace();
			Rect rect = new Rect();
			View v = iLoongLauncher.getInstance().getWindow().findViewById( Window.ID_ANDROID_CONTENT );
			v.getWindowVisibleDisplayFrame( rect );
			if( rect.top > 0 )
			{
				statusBarHeight = rect.top;
			}
		}
		return statusBarHeight;
	}
	
	public static int getScreenHeight()
	{
		if( height != 0 )
		{
			// Log.e("height", "1:"+height);
			return height;
		}
		Resources res = iLoongLauncher.getInstance().getResources();
		int screenHeight = res.getDisplayMetrics().heightPixels;
		height = screenHeight - getStatusBarHeight();
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
		width = screenWidth;
		height = screenHeight - getStatusBarHeight();
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
			Bitmap res = Bitmap.createScaledBitmap( bmp , bitmapWidth , bitmapHeight , false );
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
		retImage = Utilities.createIconBitmap( new BitmapDrawable( (Bitmap)image ) , iLoongApplication.ctx );
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
		float iconWidth = Utilities.sIconTextureWidth;
		float bmpHeight = iconWidth;
		float cellHeight = R3D.workspace_cell_height;
		int titleHeight = 0;
		// paddingLeft = (textureWidth - iconWidth) / 2;
		paint.getFontMetrics( fontMetrics );
		//		float singleLineHeight = (float) Math.ceil(fontMetrics.bottom
		//				- fontMetrics.top);
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		String[] splitTitle;
		// boolean split = false;
		float space_height = 0;// iconHeight/10;
		float paddingTop;
		// float tmp = cellHeight - iconHeight -space_height -
		// 2*singleLineHeight;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
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
			space_height = bmpHeight / 10;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
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
		float paddingTop;
		String[] splitTitle;
		float iconbgPaddingTop = 0;
		float paddingLeft = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
		//teapotXu_20130128: add icon_cover_plate start
		float iconcoverplatePaddingTop = 0;
		//teapotXu_20130128: add icon_cover_plate end
		//xiatian add start	//adjust third apk icon offset when have iconbg
		Rect iconbgRect = new Rect();
		int iconbgRectTop = 0;
		//xiatian add end
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
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
			space_height = bmpHeight / 10;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
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
					iconbgPaddingTop = paddingTop - ( icn_bg.getHeight() - Utilities.sIconTextureHeight ) / 2 - 1;
					if( iconbgPaddingTop < 0 )
					{
						iconbgPaddingTop = 0;
					}
				}
				canvas.drawBitmap( icn_bg , ( textureWidth - icn_bg.getWidth() ) / 2 , iconbgPaddingTop , null );
				//xiatian add start	//adjust third apk icon offset when have iconbg
				iconbgRect.left = (int)( textureWidth - icn_bg.getWidth() ) / 2;
				iconbgRect.top = iconbgRectTop;
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
					paddingTop = iconbgPaddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
				}
				else
				{
					paddingTop = paddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
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
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg != null ) )
			{
				paddingTop += DefaultLayout.icon_shadow_radius;
			}
			//xiatian add start	//adjust third apk icon offset when have iconbg
			if( icn_bg != null )
				drawThirdAPKIconByOffsetInTheme( b , iconbgRect , iconbgPaddingTop , paddingTop , paddingLeft , canvas );
			else
				//xiatian add end			
				canvas.drawBitmap( b , paddingLeft , paddingTop , null );
			if( recycle && b != IconCache.mDefaultIcon )
				b.recycle();
		}
		if( Icon3D.getMask() != null && icn_bg != null && saveLayer != -1 )
		{
			float maskPaddingTop = 0;
			float maskPaddingLeft = 0;
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					maskPaddingTop = iconcoverplatePaddingTop - ( Icon3D.getMask().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( maskPaddingTop < 0 )
					{
						maskPaddingTop = 0;
					}
				}
				maskPaddingLeft = ( textureWidth - Icon3D.getMask().getWidth() ) / 2;
			}
			paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
			canvas.drawBitmap( Icon3D.getMask() , maskPaddingLeft , maskPaddingTop , paint );
			paint.setXfermode( null );
			canvas.restoreToCount( saveLayer );
		}
		//teapotXu_20130128: add icon_cover_plate start
		if( ( icn_bg != null ) && Icon3D.getCover() != null )
		{
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					iconcoverplatePaddingTop = iconcoverplatePaddingTop - ( Icon3D.getCover().getHeight() - Utilities.sIconTextureHeight ) / 2;
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
		//teapotXu_20130128: add icon_cover_plate end
		if( splitTitle != null && splitTitle.length > 0 )
		{
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
		{
			return iconBmpHeight;
		}
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		// paint.setColor(Color.WHITE);
		// paint.setAntiAlias(true);
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		float paddingTop;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
		}
		else
		{
			space_height = bmpHeight / 10;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
		}
		if( paddingTop < 0 )
		{
			paddingTop = 0;
		}
		if( DefaultLayout.show_font_bg == true && Icon3D.titleBg != null )
		{
			space_height -= ( Icon3D.titleBg.getHeight() - singleLineHeight ) / 2f;
		}
		if( space_height < 0 )
		{
			space_height = 0;
		}
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
			float iconbgPaddingTop ,
			float paddingTop ,
			float paddingLeft ,
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
				src.left = Math.abs( offsetX ) - bgPaddingLefticonBg;
				dst.right = dst.left + ( src.right - src.left );
			}
		}
		else if( offsetX == 0 )
		{//=0：水平居中
			dst.left += bgPaddingLefticonBg;
			dst.right = dst.left + b.getWidth();
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
				src.bottom = b.getHeight() - ( Math.abs( offsetY ) - bgPaddingTopiconBg );
				dst.top = dst.bottom - src.bottom;
			}
		}
		else if( offsetY == 0 )
		{//=0：垂直居中
			dst.top += bgPaddingTopiconBg;
			dst.bottom = dst.top + b.getHeight();
		}
		else if( offsetY > 0 )
		{//>0：垂直上移
			if( offsetY <= bgPaddingTopiconBg )
			{
				dst.bottom -= ( offsetY + bgPaddingTopiconBg );
				dst.top = dst.bottom - b.getHeight();
			}
			else
			{
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
		shadowPaint.setAlpha( 0x45 );//0x00(全透明),0x80(半透明),0xFF(不透明)
		BlurMaskFilter blurFilter = new BlurMaskFilter( DefaultLayout.icon_shadow_radius , BlurMaskFilter.Blur.NORMAL );
		shadowPaint.setMaskFilter( blurFilter );
		int[] offsetXY = new int[2];
		Bitmap shadowBitmap = originalBitmap.extractAlpha( shadowPaint , offsetXY );
		Bitmap shadowBitmap32 = shadowBitmap.copy( Config.ARGB_8888 , true );
		Bitmap ret = Bitmap.createBitmap( shadowBitmap32.getWidth() , shadowBitmap32.getHeight() - 2 * offsetXY[1] , Config.ARGB_8888 );
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
	
	public static void saveScreenShot(
			Texture t )
	{
		//       try{
		String path = Environment.getExternalStorageDirectory() + File.separator + "coco" + File.separator + "share" + File.separator;
		File file = new File( path , "xxxx.png" );
		saveScreenshot( new FileHandle( file ) , t );
		//        }catch(IOException e){
		//            e.printStackTrace();
		//        }
	}
	
	public static void saveScreenshot(
			FileHandle file ,
			Texture t )
	{
		Pixmap pixmap = t.getTextureData().consumePixmap();//getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		byte[] bytes;
		//        
		try
		{
			bytes = PNGEncode.toPNG( pixmap );
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return;
		}
		boolean append = false;
		file.writeBytes( bytes , append );
	}
	
	public static Pixmap getScreenshot(
			int x ,
			int y ,
			int w ,
			int h ,
			boolean flipY )
	{
		Gdx.gl.glPixelStorei( GL10.GL_PACK_ALIGNMENT , 1 );
		final Pixmap pixmap = new Pixmap( w , h , Format.RGBA8888 );
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels( x , y , w , h , GL10.GL_RGBA , GL10.GL_UNSIGNED_BYTE , pixels );
		final int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];
		if( flipY )
		{
			final int numBytesPerLine = w * 4;
			for( int i = 0 ; i < h ; i++ )
			{
				pixels.position( ( h - i - 1 ) * numBytesPerLine );
				pixels.get( lines , i * numBytesPerLine , numBytesPerLine );
			}
			pixels.clear();
			pixels.put( lines );
		}
		else
		{
			pixels.clear();
			pixels.get( lines );
		}
		return pixmap;
	}
	
	public static Bitmap GetScreenPixels(
			int x ,
			int y ,
			int w ,
			int h ,
			GL20 gl )
	{
		int b[] = new int[w * ( y + h )];
		int bt[] = new int[w * h];
		IntBuffer ib = IntBuffer.wrap( b );
		ib.position( 0 );
		gl.glReadPixels( x , 0 , w , y + h , GL20.GL_RGBA , GL20.GL_UNSIGNED_BYTE , ib );
		for( int i = 0 , k = 0 ; i < h ; i++ , k++ )
		{//remember, that OpenGL bitmap is incompatible with Android bitmap
			//and so, some correction need.        
			for( int j = 0 ; j < w ; j++ )
			{
				int pix = b[i * w + j];
				int pb = ( pix >> 16 ) & 0xff;
				int pr = ( pix << 16 ) & 0x00ff0000;
				int pix1 = ( pix & 0xff00ff00 ) | pr | pb;
				bt[( h - k - 1 ) * w + j] = pix1;
			}
		}
		Bitmap sb = Bitmap.createBitmap( bt , w , h , Bitmap.Config.ARGB_8888 );
		String path = Environment.getExternalStorageDirectory() + File.separator + "coco" + File.separator + "share" + File.separator + "cooeeShare111.png";
		ShowDesktop.saveBmp( sb , path );
		return sb;
	}
	
	//    public void getScreenTexture(int x, int y, int w, int h,GL20 gl){
	//        gl.glCopyTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_DEPTH_COMPONENT, 
	//                0, 0, w, h, 0);
	//    }
	//   
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
		//        String tempPath =Environment.getExternalStorageDirectory()+File.separator+"coco"+File.separator+"share"+File.separator+"foreground.png";
		//        ShowDesktop.saveBmp(bitmap,tempPath);
		ShowDesktop.saveBmp( setScreenshotBackground( bitmap , bg ) , mScreenShotPath );
		// ShowDesktop.saveBmpToSystem(setScreenshotBackground(bitmap,bg),mScreenShotPath);
		return mScreenShotPath;
	}
	
	public static String getScreenShot()
	{
		byte[] buffer;
		int[] bmpBuffer = new int[Utils3D.getScreenWidth() * Utils3D.getScreenHeight()];
		buffer = ScreenUtils.getFrameBufferPixels( true );
		RGBA8888toARGB8888( buffer , bmpBuffer );
		Bitmap bitmap = Bitmap.createBitmap( bmpBuffer , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() , Config.ARGB_8888 );
		mScreenShotPath = Environment.getExternalStorageDirectory() + File.separator + "coco" + File.separator + "share" + File.separator + "cooeeShare.png";
		String tempPath = Environment.getExternalStorageDirectory() + File.separator + "coco" + File.separator + "share" + File.separator + "foreground.png";
		ShowDesktop.saveBmp( bitmap , tempPath );
		// ShowDesktop.saveBmp(setScreenshotBackground(bitmap,bg), mScreenShotPath);
		// ShowDesktop.saveBmpToSystem(setScreenshotBackground(bitmap,bg),mScreenShotPath);
		return mScreenShotPath;
	}
	
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
}
