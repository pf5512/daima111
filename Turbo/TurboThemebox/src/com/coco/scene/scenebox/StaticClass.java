package com.coco.scene.scenebox;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;


public class StaticClass
{
	
	public static boolean canDownToInternal = false;
	public static String set_directory_path = "";
	public static String set_default_theme_thumb = "";
	public static final String HOT_LIST_DATE = "hotSceneListDate";
	public static final String ACTION_THEME_VIEW = "com.coco.scene.action.VIEW";
	public static final String ACTION_CHECK_ICON = "com.coco.scene.action.CHECK_ICON";
	public static final String ACTION_HOTLIST_CHANGED = "com.coco.action.HOTLIST_CHANGED";
	public static final String ACTION_DEFAULT_THEME_CHANGED = "com.coco.scene.action.DEFAULT_THEME_CHANGED";
	public static final String ACTION_START_DOWNLOAD_APK = "com.coco.scene.action.START_DOWNLOAD_APK";
	public static final String ACTION_PAUSE_DOWNLOAD_APK = "com.coco.scene.action.PAUSE_DOWNLOAD_APK";
	public static final String ACTION_THUMB_CHANGED = "com.coco.scene.action.THUMB_CHANGED";
	public static final String ACTION_PREVIEW_CHANGED = "com.coco.scene.action.PREVIEW_CHANGED";
	public static final String ACTION_DOWNLOAD_STATUS_CHANGED = "com.coco.scene.action.DOWNLOAD_STATUS_CHANGED";
	public static final String ACTION_DOWNLOAD_SIZE_CHANGED = "com.coco.scene.action.DOWNLOAD_SIZE_CHANGED";
	public static final String EXTRA_PACKAGE_NAME = "PACKAGE_NAME";
	public static final String EXTRA_CLASS_NAME = "CLASS_NAME";
	public static final String EXTRA_DOWNLOAD_SIZE = "EXTRA_DOWNLOAD_SIZE";
	public static final String EXTRA_TOTAL_SIZE = "EXTRA_TOTAL_SIZE";
	public static final String EXTRA_MAIN_TAB_INDEX = "MAIN_TAB_INDEX";
	public static final String DEFAULT_APK_URL = "http://ku01.coomoe.com/uiv2/getApp.ashx";
	public static final String DEFAULT_IMAGE_URL = "http://tu01.coomoe.com/ui02/getimg.ashx";
	public static final String DEFAULT_LIST_URL = "http://yu01.coomoe.com/ui02/getapplist.ashx";
	
	public static boolean saveMyBitmap(
			Context context ,
			String packageName ,
			String activityName ,
			Bitmap mBitmap )
	{
		if( com.coco.theme.themebox.StaticClass.getAvailableInternalMemorySize() <= 5242880 )
		{
			return false;
		}
		if( mBitmap == null || mBitmap.isRecycled() )
		{
			return false;
		}
		File f = context.getDir( "scene" , Context.MODE_PRIVATE );
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
}
