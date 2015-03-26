package com.example.icsdk_app;


import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import cool.sdk.Category.CaregoryReqCallBack;
import cool.sdk.Category.CaregoyReqType;
import cool.sdk.Category.CategoryHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


public class CategoryReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		final String pkgName = intent.getDataString().substring( 8 );
		final Context mContext = context;
		// TODO Auto-generated method stub
		if( intent.getAction().equals( Intent.ACTION_PACKAGE_ADDED ) || intent.getAction().equals( Intent.ACTION_PACKAGE_REPLACED ) )
		{
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					try
					{
						final JSONArray appList = new JSONArray();
						PackageManager Pkgmanger = mContext.getPackageManager();
						PackageInfo pkgInfo = Pkgmanger.getPackageInfo( pkgName , 0 );
						JSONObject curPkgJson = new JSONObject();
						curPkgJson.put( "pn" , pkgInfo.packageName );
						if( ( pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) == 0 )
						{
							//非系统应用
							curPkgJson.put( "sy" , "1" );
						}
						else
						{
							//系统应用
							curPkgJson.put( "sy" , "0" );
						}
						curPkgJson.put( "cn" , Pkgmanger.getApplicationLabel( pkgInfo.applicationInfo ) );
						curPkgJson.put( "en" , Pkgmanger.getApplicationLabel( pkgInfo.applicationInfo ) );
						curPkgJson.put( "vr" , pkgInfo.versionName );
						curPkgJson.put( "vn" , pkgInfo.versionCode );
						appList.put( curPkgJson );
						curPkgJson = null;
						
						CategoryHelper.getInstance( mContext ).doCategoryQuery( appList, new CaregoryReqCallBack() {
							
							@Override
							public void ReqSucess(
									CaregoyReqType type ,
									List<String> appList )
							{
								// TODO Auto-generated method stub
								Log.v( "COOL " , type + "SUCESS" );
//								Log.v( "COOL" , "dictMap: " + CategoryHelper.dictMap );
//								Log.v( "COOL" , "treeMap: " + CategoryHelper.treeMap );
//								Log.v( "COOL" , "cateinfoMap: " + CategoryHelper.cateinfoMap );
							}
							
							@Override
							public void ReqFailed(
									CaregoyReqType type ,
									String Msg )
							{
								// TODO Auto-generated method stub
								Log.v( "COOL " , type + "Failed" + Msg);
//								Log.v( "COOL" , "dictMap: " + CategoryHelper.dictMap );
//								Log.v( "COOL" , "treeMap: " + CategoryHelper.treeMap );
//								Log.v( "COOL" , "cateinfoMap: " + CategoryHelper.cateinfoMap );
							}
						} );
					}
					catch( Exception e )
					{
						// TODO: handle exception
					}
				}
			} ).start();
		}
	}
}
