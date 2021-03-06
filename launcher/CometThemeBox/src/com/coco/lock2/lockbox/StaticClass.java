﻿package com.coco.lock2.lockbox;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.iLoong.base.themebox.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;


public class StaticClass
{
	
	public static final String HOT_LIST_DATE = "hotLockListDate";
	public static final String DEFAULT_LOCKSCREEN_PACKAGE = "com.coco.lock2.lockbox";
	public static final String DEFAULT_LOCKSCREEN_CLASS = "com.coco.lock2.lockbox.zheshan.ZheshanActivity";
	public static final String USE_LOCK_PACKAGE_NAME = "lockPackageName";
	public static final String USE_LOCK_CLASS_NAME = "lockClassName";
	public static final String CREATE_SHORTCUT = "createShortcut";
	public static final String LIST_VER = "listver";
	public static final String EXTRA_PACKAGE_NAME = "PACKAGE_NAME";
	public static final String EXTRA_CLASS_NAME = "CLASS_NAME";
	public static final String ACTION_HOTLIST_NOCHANGED = "com.coco.lock.action.HOTLIST_NOCHANGED";
	public static final String THEMEBOX_PACKAGE_NAME = "com.coco.lock2.themebox";
	public static final String ACTION_DISABLE_SYSLOCK = "com.coco.action.DISABLE_SYSLOCK";
	public static final String ACTION_LOCK_VIEW = "com.coco.lock.action.VIEW";
	public static final String ACTION_CHECK_ICON = "com.coco.lock.action.CHECK_ICON";
	public static final String ACTION_HOTLIST_CHANGED = "com.coco.lock.action.HOTLIST_CHANGED";
	public static final String ACTION_DEFAULT_LOCK_CHANGED = "com.coco.lock.action.DEFAULT_LOCK_CHANGED";
	public static final String ACTION_START_DOWNLOAD_APK = "com.coco.lock.action.START_DOWNLOAD_APK";
	public static final String ACTION_PAUSE_DOWNLOAD_APK = "com.coco.lock.action.PAUSE_DOWNLOAD_APK";
	public static final String ACTION_THUMB_CHANGED = "com.coco.lock.action.THUMB_CHANGED";
	public static final String ACTION_PREVIEW_CHANGED = "com.coco.lock.action.PREVIEW_CHANGED";
	public static final String ACTION_DOWNLOAD_STATUS_CHANGED = "com.coco.lock.action.DOWNLOAD_STATUS_CHANGED";
	public static final String ACTION_DOWNLOAD_SIZE_CHANGED = "com.coco.lock.action.DOWNLOAD_SIZE_CHANGED";
	public static final String EXTRA_DOWNLOAD_SIZE = "EXTRA_DOWNLOAD_SIZE";
	public static final String EXTRA_TOTAL_SIZE = "EXTRA_TOTAL_SIZE";
	public static final String DEFAULT_APK_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx";
	public static final String DEFAULT_IMAGE_URL = "http://tu01.coomoe.com/ui02/getimg.ashx";
	public static final String DEFAULT_LIST_URL = "http://yu01.coomoe.com/ui02/getapplist.ashx";
	public static final String LOCKBOX_PACKAGE_NAME = "com.coco.lock2.lockbox";
	public static final String LOCKBOX_PREVIEW_ACTIVITY = "com.coco.lock2.lockbox.preview.PreviewHotActivity";
	public static final String LOCKBOX_SETTING_ACTIVITY = "com.coco.lock2.lockbox.SettingActivity";
	
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
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
		{
			return false;
		}
		return true;
	}
	
	public static boolean isAllowDownloadWithToast(
			Context cxt )
	{
		if( !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) && !com.coco.theme.themebox.StaticClass.canDownToInternal )
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
		File f = context.getDir( "coco" , Context.MODE_PRIVATE );
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
		catch( FileNotFoundException e )
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
	private static long getAvailableInternalMemorySize()
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs( path.getPath() );
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}
}
