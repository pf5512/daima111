package com.coco.theme.themebox;


import java.io.File;
import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.Log;


public class ThemeReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			final Context context ,
			Intent intent )
	{
		if( intent.getAction().equals( Intent.ACTION_PACKAGE_ADDED ) )
		{
			String packageName = intent.getData().getSchemeSpecificPart();
			Intent themeIntent = new Intent( "com.cooeecomet.themes" , null );
			themeIntent.setPackage( packageName );
			List<ResolveInfo> themesinfo = context.getPackageManager().queryIntentActivities( themeIntent , 0 );
			Collections.sort( themesinfo , new ResolveInfo.DisplayNameComparator( context.getPackageManager() ) );
			if( packageName == null || packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
			{
				Intent systemmain = new Intent( "android.intent.action.MAIN" , null );
				systemmain.addCategory( "android.intent.category.LAUNCHER" );
				systemmain.setPackage( ThemesDB.LAUNCHER_PACKAGENAME );
				themesinfo = context.getPackageManager().queryIntentActivities( systemmain , 0 );
			}
			if( themesinfo != null )
			{
				if( themesinfo.size() > 0 )
				{
					ResolveInfo item = themesinfo.get( 0 );
					ThemeInformation infor = new ThemeInformation();
					infor.setActivity( context , item.activityInfo );
					infor.loadDetail( context );
					Bitmap imgThumb = infor.getThumbImage();
					if( imgThumb != null )
					{
						StaticClass.saveMyBitmap( context , infor.getPackageName() , infor.getClassName() , imgThumb );
					}
				}
			}
		}
		else if( intent.getAction().equals( Intent.ACTION_PACKAGE_REMOVED ) )
		{
			Log.d( "TabLockContentFactory" , String.format( "action=%s" , "dsfdggd" ) );
			String packageName = intent.getData().getSchemeSpecificPart();
			File f = context.getDir( "theme" , Context.MODE_PRIVATE );
			File f1 = new File( f + "/" + packageName );
			recursionDeleteFile( f1 );
		}
	}
	
	private static void recursionDeleteFile(
			File file )
	{
		if( file.isFile() )
		{
			file.delete();
			return;
		}
		if( file.isDirectory() )
		{
			File[] childFile = file.listFiles();
			if( childFile == null )
			{
				return;
			}
			for( File f : childFile )
			{
				recursionDeleteFile( f );
			}
			file.delete();
		}
	}
}
