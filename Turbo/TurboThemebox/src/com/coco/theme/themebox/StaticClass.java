package com.coco.theme.themebox;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import com.iLoong.base.themebox.R;


public class StaticClass
{
	
	public static boolean canDownToInternal = false;
	public static String set_directory_path = "";
	public static String set_default_theme_thumb = "";
	public static final String HOT_LIST_DATE = "hotThemeListDate";
	public static final String ACTION_THEME_VIEW = "com.coco.theme.action.VIEW";
	public static final String ACTION_CHECK_ICON = "com.coco.theme.action.CHECK_ICON";
	public static final String ACTION_HOTLIST_CHANGED = "com.coco.action.HOTLIST_CHANGED";
	public static final String ACTION_DEFAULT_THEME_CHANGED = "com.coco.theme.action.DEFAULT_THEME_CHANGED";
	public static final String ACTION_START_DOWNLOAD_APK = "com.coco.theme.action.START_DOWNLOAD_APK";
	public static final String ACTION_PAUSE_DOWNLOAD_APK = "com.coco.theme.action.PAUSE_DOWNLOAD_APK";
	public static final String ACTION_THUMB_CHANGED = "com.coco.theme.action.THUMB_CHANGED";
	public static final String ACTION_PREVIEW_CHANGED = "com.coco.theme.action.PREVIEW_CHANGED";
	public static final String ACTION_DOWNLOAD_STATUS_CHANGED = "com.coco.theme.action.DOWNLOAD_STATUS_CHANGED";
	public static final String ACTION_DOWNLOAD_SIZE_CHANGED = "com.coco.theme.action.DOWNLOAD_SIZE_CHANGED";
	public static final String EXTRA_PACKAGE_NAME = "PACKAGE_NAME";
	public static final String EXTRA_CLASS_NAME = "CLASS_NAME";
	public static final String EXTRA_DOWNLOAD_SIZE = "EXTRA_DOWNLOAD_SIZE";
	public static final String EXTRA_TOTAL_SIZE = "EXTRA_TOTAL_SIZE";
	public static final String EXTRA_MAIN_TAB_INDEX = "MAIN_TAB_INDEX";
	public static final String DEFAULT_APK_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx";
	public static final String DEFAULT_IMAGE_URL = "http://tu01.coomoe.com/ui02/getimg.ashx";
	public static final String DEFAULT_LIST_URL = "http://yu01.coomoe.com/ui02/getapplist.ashx";
	public static final String LOCKBOX_PACKAGE_NAME = "com.coco.lock2.lockbox";
	public static final String LOCKBOX_PREVIEW_ACTIVITY = "com.coco.lock2.lockbox.preview.PreviewHotActivity";
	public static final String LOCKBOX_SETTING_ACTIVITY = "com.coco.lock2.lockbox.SettingActivity";
	public static final String ACTION_THEME_UPDATE_RECOMMEND = "com.coco.theme.action.update_recommend";
	public static final String ACTION_UPDATE_DEFAULT_FONT = "com.coco.font.action.update_recommend";
	
	public static boolean isLockBoxInstalled(
			Context cxt )
	{
		Intent intent = new Intent();
		intent.setClassName( LOCKBOX_PACKAGE_NAME , LOCKBOX_PREVIEW_ACTIVITY );
		ResolveInfo info = cxt.getPackageManager().resolveActivity( intent , 0 );
		if( info == null )
		{
			return false;
		}
		return true;
	}
	
	public static boolean isAllowDownload(
			Context cxt )
	{
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) && !canDownToInternal )
		{
			return false;
		}
		return true;
	}
	
	public static boolean isAllowDownloadWithToast(
			Context cxt )
	{
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) && !canDownToInternal )
		{
			Toast.makeText( cxt , cxt.getString( R.string.textSdCardNotPrepare ) , Toast.LENGTH_SHORT ).show();
			return false;
		}
		return true;
	}
	
	public static boolean saveMyBitmap(
			Context context ,
			String packageName ,
			String activityName ,
			Bitmap mBitmap )
	{
		if( getAvailableInternalMemorySize() <= 5242880 )
		{
			return false;
		}
		if( mBitmap == null || mBitmap.isRecycled() )
		{
			return false;
		}
		File f = context.getDir( "theme" , Context.MODE_PRIVATE );
		File f1 = new File( f + "/" + packageName );
		if( !f1.exists() )
		{
			f1.mkdir();
		}
		File fileName = new File( f1 + "/" + activityName + ".tupian" );
		FileOutputStream fOut = null;
		try
		{
			fOut = new FileOutputStream( fileName , false );
			mBitmap.compress( Bitmap.CompressFormat.PNG , 100 , fOut );
			return true;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( fOut != null )
			{
				try
				{
					fOut.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取手机的内部存储设备的可用空间
	 */
	public static long getAvailableInternalMemorySize()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs( path.getPath() );
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
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
}
