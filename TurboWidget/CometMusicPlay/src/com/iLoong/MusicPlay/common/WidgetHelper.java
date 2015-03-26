package com.iLoong.MusicPlay.common;

import java.util.List;

import com.iLoong.launcher.Widget3D.MainAppContext;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;


public class WidgetHelper
{
	public static  String  getDefaultPkgName(MainAppContext appContext)
	{
		String defaultPkgName=null;
		PackageManager pm = appContext.mContainerContext.getPackageManager();
		Intent intent = new Intent( MediaStore.INTENT_ACTION_MUSIC_PLAYER );
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		if( resolveInfos != null )
		{
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					defaultPkgName = resolveInfos.get( i ).activityInfo.packageName;
					return defaultPkgName;
				}
			}
		}
		else
		{
			intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_APP_MUSIC );
			resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					defaultPkgName = resolveInfos.get( i ).activityInfo.packageName;
					return defaultPkgName;
				}
			}
		}
		return null;
	}
}
