package com.coco.theme.themebox.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.iLoong.base.themebox.R;


public class Tools
{
	
	public static Bitmap getImageFromSDCardFile(
			final String foldname ,
			final String filename )
	{
		Bitmap image = null;
		String file = Environment.getExternalStorageDirectory() + File.separator + foldname + File.separator + filename;
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			image = BitmapFactory.decodeFile( file , opts );
		}
		catch( Exception e )
		{
		}
		return image;
	}
	
	public static Bitmap getImageFromInStream(
			InputStream is )
	{
		Bitmap image = null;
		try
		{
			image = BitmapFactory.decodeStream( is );
		}
		catch( Exception e )
		{
		}
		return image;
	}
	
	public static Bitmap getPurgeableBitmap(
			InputStream is ,
			int width ,
			int height )//width、height传入负值，不缩放
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeStream( is , null , option );
		option.inJustDecodeBounds = false;
		option.inPurgeable = true;// 允许可清除
		option.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
		int w = option.outWidth / width;
		int h = option.outHeight / height;
		option.inSampleSize = w < h ? w : h;
		if( option.inSampleSize < 1 )
		{
			option.inSampleSize = 1;
		}
		try
		{
			Bitmap result = BitmapFactory.decodeStream( is , null , option );
			is.close();
			is = null;
			return result;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Bitmap getPurgeableBitmap(
			String path ,
			int width ,
			int height )//width、height传入负值，不缩放
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile( path , option );
		option.inJustDecodeBounds = false;
		option.inPurgeable = true;// 允许可清除
		option.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
		int w = option.outWidth / width;
		int h = option.outHeight / height;
		option.inSampleSize = w < h ? w : h;
		if( option.inSampleSize < 1 )
		{
			option.inSampleSize = 1;
		}
		try
		{
			Bitmap result = BitmapFactory.decodeFile( path , option );
			return result;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Drawable zoomDrawable(
			Drawable drawable ,
			float scale )
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap( drawable ); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		matrix.postScale( scale , scale ); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap( oldbmp , 0 , 0 , width , height , matrix , true ); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		return new BitmapDrawable( newbmp ); // 把bitmap转换成drawable并返�?
	}
	
	public static Drawable zoomDrawable(
			Drawable drawable ,
			int w ,
			int h )
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap( drawable ); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = ( (float)w / width ); // 计算缩放比例
		float scaleHeight = ( (float)h / height );
		matrix.postScale( scaleWidth , scaleHeight ); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap( oldbmp , 0 , 0 , width , height , matrix , true ); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		return new BitmapDrawable( newbmp ); // 把bitmap转换成drawable并返�?
	}
	
	// drawable 转换成bitmap
	public static Bitmap drawableToBitmap(
			Drawable drawable )
	{
		int width = drawable.getIntrinsicWidth(); // 取drawable的长�?
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565; // 取drawable的颜色格�?
		Bitmap bitmap = Bitmap.createBitmap( width , height , config ); // 建立对应bitmap
		Canvas canvas = new Canvas( bitmap ); // 建立对应bitmap的画�?
		drawable.setBounds( 0 , 0 , width , height );
		drawable.draw( canvas ); // 把drawable内容画到画布�?
		return bitmap;
	}
	
	/**
	 * 图片旋转
	 * 
	 * @param bmp
	 *            要旋转的图片
	 * @param degree
	 *            图片旋转的角度，负�?为�?时针旋转，正值为顺时针旋�? * @return
	 */
	public static Bitmap rotateBitmap(
			Bitmap bmp ,
			float degree )
	{
		Matrix matrix = new Matrix();
		matrix.postRotate( degree );
		return Bitmap.createBitmap( bmp , 0 , 0 , bmp.getWidth() , bmp.getHeight() , matrix , true );
	}
	
	/**
	 * 图片缩放
	 * 
	 * @param bm
	 * @param scale
	 *            值小于则为缩小，否则为放�? * @return
	 */
	public static Bitmap resizeBitmap(
			Bitmap bm ,
			float scale )
	{
		// Matrix matrix = new Matrix();
		// matrix.postScale(scale, scale);
		if( scale == 1 )
			return bm;
		if( (int)bm.getWidth() * scale < 1 || (int)bm.getHeight() * scale < 1 )
		{
			return bm;
		}
		Bitmap tmp = Bitmap.createScaledBitmap( bm , (int)( bm.getWidth() * scale ) , (int)( bm.getHeight() * scale ) , true );
		bm.recycle();
		return tmp;
		// return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
		// matrix, true);
	}
	
	/**
	 * 图片缩放
	 * 
	 * @param bm
	 * @param w
	 *            缩小或放大成的宽
	 * @param h
	 *            缩小或放大成的高
	 * @return
	 */
	public static Bitmap resizeBitmap(
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
	
	/**
	 * 图片反转
	 * 
	 * @param bm
	 * @param flag
	 *            0为水平反转，1为垂直反�? * @return
	 */
	public static Bitmap reverseBitmap(
			Bitmap bmp ,
			int flag )
	{
		float[] floats = null;
		switch( flag )
		{
			case 0: // 水平反转
				floats = new float[]{ -1f , 0f , 0f , 0f , 1f , 0f , 0f , 0f , 1f };
				break;
			case 1: // 垂直反转
				floats = new float[]{ 1f , 0f , 0f , 0f , -1f , 0f , 0f , 0f , 1f };
				break;
		}
		if( floats != null )
		{
			Matrix matrix = new Matrix();
			matrix.setValues( floats );
			return Bitmap.createBitmap( bmp , 0 , 0 , bmp.getWidth() , bmp.getHeight() , matrix , true );
		}
		return null;
	}
	
	public static int dip2px(
			Context context ,
			float dipValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( dipValue * scale + 0.5f );
	}
	
	public static int px2dip(
			Context context ,
			float pxValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( pxValue / scale + 0.5f );
	}
	
	private static int XIA = -1;
	
	public static int compareImage(
			Bitmap bitmap ,
			Bitmap bg[] ,
			int count )
	{
		if( -1 == XIA )
		{
			XIA = 50;
		}
		int similar = 0;
		int similarMax = 0;
		int imageIndex = 0;
		Bitmap tempBitmap = null;
		Matrix mx = new Matrix();
		float scaleX = 48.0f / bitmap.getWidth();
		float scaleY = 48.0f / bitmap.getHeight();
		mx.postScale( scaleX , scaleY );
		tempBitmap = Bitmap.createBitmap( bitmap , 0 , 0 , bitmap.getWidth() , bitmap.getHeight() , mx , true );
		Bitmap bit = bg[0];
		Matrix m = new Matrix();
		float X = 48.0f / bit.getWidth();
		float Y = 48.0f / bit.getHeight();
		m.postScale( X , Y );
		int width = tempBitmap.getWidth();
		int height = tempBitmap.getHeight();
		int backRgb = 0;
		int tempRgb = 0;
		Bitmap backBitmap = null;
		for( int index = 0 ; index < count ; index++ )
		{
			backBitmap = Bitmap.createBitmap( bg[index] , 0 , 0 , bg[index].getWidth() , bg[index].getHeight() , mx , true );
			similar = 0;
			for( int x = 0 ; x < width ; x++ )
			{
				for( int y = 0 ; y < height ; y++ )
				{
					backRgb = backBitmap.getPixel( x , y );
					tempRgb = tempBitmap.getPixel( x , y );
					if( Math.abs( Color.red( backRgb ) - Color.red( tempRgb ) ) < XIA && Math.abs( Color.green( backRgb ) - Color.green( tempRgb ) ) < XIA && Math.abs( Color.blue( backRgb ) - Color
							.blue( tempRgb ) ) < XIA )
					{
						similar += 1;
					}
				}
			}
			if( similar > similarMax )
			{
				similarMax = similar;
				imageIndex = index;
			}
			if( null != backBitmap && !backBitmap.isRecycled() )
			{
				backBitmap.recycle();
				backBitmap = null;
			}
		}
		if( null != tempBitmap && !tempBitmap.isRecycled() )
		{
			tempBitmap.recycle();
			tempBitmap = null;
		}
		return imageIndex;
	}
	
	public static void writelogTosd(
			String content )
	{
		FileOutputStream fos = null;
		String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Coco/";
		try
		{
			File file = new File( filepath , "log.txt" );
			if( !file.exists() )
				file.createNewFile();
			fos = new FileOutputStream( file , true );
			fos.write( content.getBytes() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fos.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void writePurchasedData(
			Context ctx ,
			String type ,
			String pkgName )
	{
		String purchased = "buy:" + type + ":" + pkgName + " ";
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
		pref.edit().putBoolean( purchased , true ).commit();
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			return;
		}
		FileOutputStream fos = null;
		String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Coco/";
		try
		{
			File file = new File( filepath , "purchased.temp" );
			if( !file.exists() )
				file.createNewFile();
			fos = new FileOutputStream( file , true );
			fos.write( purchased.getBytes() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				fos.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isContentPurchased(
			Context ctx ,
			String type ,
			String pkgName )
	{
		String purchased = "buy:" + type + ":" + pkgName + " ";
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( ctx );
		if( pref.getBoolean( purchased , false ) )
		{
			return true;
		}
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			return false;
		}
		FileInputStream fis = null;
		String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Coco/";
		ByteArrayOutputStream baos = null;
		try
		{
			File file = new File( filepath , "purchased.temp" );
			if( !file.exists() )
			{
				return false;
			}
			fis = new FileInputStream( file );
			byte[] buff = new byte[1024];// 设置一个缓冲
			baos = new ByteArrayOutputStream();
			int len;
			while( ( len = fis.read( buff ) ) != -1 )
			{// 如果buff没有读完
				baos.write( buff , 0 , len );// 就将buff内容保存到字节数组输出流中
			}
			String data = new String( baos.toByteArray() );
			if( data.contains( purchased ) )
			{
				return true;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( baos != null )
				{
					baos.close();
				}
				if( fis != null )
				{
					fis.close();
				}
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static Bitmap recycleImageBitmap(
			ImageView imag )
	{
		try
		{
			Drawable drawable = imag.getDrawable();
			if( BitmapDrawable.class.isInstance( drawable ) )
			{
				return ( (BitmapDrawable)drawable ).getBitmap();
			}
		}
		catch( Exception e )
		{
		}
		return null;
	}
	
	public static boolean isServiceRunning(
			Context context ,
			String className )
	{
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices( 30 );
		if( serviceList.size() <= 0 )
		{
			return false;
		}
		for( int i = 0 ; i < serviceList.size() ; i++ )
		{
			if( serviceList.get( i ).service.getClassName().equals( className ) == true )
			{
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	
	public static boolean isAppInstalled(
			Context context ,
			String pkg )
	{
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try
		{
			pm.getPackageInfo( pkg , PackageManager.GET_ACTIVITIES );
			installed = true;
		}
		catch( PackageManager.NameNotFoundException e )
		{
			installed = false;
		}
		return installed;
	}
	
	public static boolean isAppExist(
			Context context ,
			String pkg ,
			String size )
	{
		String path = com.coco.theme.themebox.util.PathTool.getDownloadDir();
		File packageFile = new File( path , pkg + ".apk" );
		if( packageFile.exists() )
		{
			try
			{
				long length = Long.parseLong( size );
				if( packageFile.length() == length )
				{
					return true;
				}
			}
			catch( Exception e )
			{
				return false;
			}
		}
		return false;
	}
	
	public static void installApk(
			Context context ,
			String pkg )
	{
		String path = com.coco.theme.themebox.util.PathTool.getDownloadDir();
		File apkfile = new File( path , pkg + ".apk" );
		if( !apkfile.exists() )
		{
			return;
		}
		Intent i = new Intent( Intent.ACTION_VIEW );
		i.setDataAndType( Uri.parse( "file://" + apkfile.toString() ) , "application/vnd.android.package-archive" );
		i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		context.startActivity( i );
	}
	
	public static void showNoticeDialog(
			final Context context ,
			final String pkg ,
			String dec ,
			final String url ,
			final String size )
	{
		// 构造对话框
		if( DownloadEngineApkService.isDownload( pkg , url ) )
		{
			Toast.makeText( context , R.string.is_downloading , Toast.LENGTH_SHORT ).show();
			return;
		}
		AlertDialog.Builder builder = new Builder( context );
		builder.setTitle( R.string.download_enginer );
		builder.setMessage( dec );
		// 更新
		builder.setPositiveButton( R.string.delete_ok , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				if( isAppExist( context , pkg , size ) )
				{
					installApk( context , pkg );
				}
				else
				{
					Intent it = new Intent( context , DownloadEngineApkService.class );
					it.putExtra( "packagename" , pkg );
					it.putExtra( "url" , url );
					context.startService( it );
				}
			}
		} );
		// 稍后更新
		builder.setNegativeButton( R.string.delete_cancel , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
			}
		} );
		builder.setOnCancelListener( new OnCancelListener() {
			
			public void onCancel(
					DialogInterface dialog )
			{
			}
		} );
		AlertDialog noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	public static void getThumblist(
			String[] wallpapers ,
			List<String> wallpaperLocal )
	{
		ArrayList<String> mImages = new ArrayList<String>();
		for( String name : wallpapers )
		{
			Log.v( "wallpaper" , name );
			if( !name.contains( "_small" ) )
			{
				mImages.add( name );
			}
			else
			{
				wallpaperLocal.add( name );
			}
		}
		Iterator<String> iterator = wallpaperLocal.iterator();
		while( iterator.hasNext() )
		{
			String thumb = iterator.next();
			boolean isExsit = false;
			for( String img : mImages )
			{
				if( img.equals( thumb.replace( "_small" , "" ) ) )
				{
					isExsit = true;
				}
			}
			if( !isExsit )
			{
				iterator.remove();
			}
		}
	}
	
	public static void Recyclebitmap(
			Bitmap imgDefaultThumb ,
			Bitmap current ,
			ImageView view ,
			Set<ImageView> recycle )
	{
		boolean isrecycle = true;
		Bitmap bmp = Tools.recycleImageBitmap( view );
		if( current == bmp )
		{
			return;
		}
		if( bmp == null || bmp.isRecycled() || bmp == imgDefaultThumb )
		{
			return;
		}
		for( ImageView v : recycle )
		{
			if( v == view )
			{
				continue;
			}
			Bitmap temp = recycleImageBitmap( v );
			if( temp == bmp )
			{
				isrecycle = false;
				break;
			}
		}
		if( isrecycle )
		{
			bmp.recycle();
		}
	}
	
	static public int showPidMemoryInfo(
			Context context ,
			String title )
	{
		int myProcessID = android.os.Process.myPid();
		int pids[] = new int[1];
		pids[0] = myProcessID;
		ActivityManager am = (ActivityManager)context.getSystemService( Context.ACTIVITY_SERVICE );
		MemoryInfo[] memoryInfoArray = am.getProcessMemoryInfo( pids );
		MemoryInfo pidMemoryInfo = memoryInfoArray[0];
		Log.i( "MemoryInfo" , "MemoryInfo:" + title + " = " + pidMemoryInfo.getTotalPss() + "K" + " time:" + System.currentTimeMillis() );
		return pidMemoryInfo.getTotalPss();
	}
}
