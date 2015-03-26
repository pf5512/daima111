package com.iLoong.launcher.Functions.Tab;


import android.content.Context;
import android.content.pm.PackageInfo;


public class TabPuginPathUtil
{
	
	public static String getTabPluginListFilePath(
			Context context )
	{
		try
		{
			String packageName = context.getPackageName();
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo( packageName , 0 );
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
	}
}
