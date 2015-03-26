package com.coco.lock2.lockbox;


import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;


public class LockHomeActivity extends Activity
{
	
	private SharedPreferences sharedPrefer;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		sharedPrefer = PreferenceManager.getDefaultSharedPreferences( this );
		SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences( this );
		String defaultPkgName = sharedPre.getString( StaticClass.DEFAULT_LOCKSCREEN_PACKAGE , "" );
		String defaultClassName = sharedPre.getString( StaticClass.DEFAULT_LOCKSCREEN_CLASS , "" );
		String curPackagename = sharedPrefer.getString( StaticClass.USE_LOCK_PACKAGE_NAME , defaultPkgName );
		String curClassname = sharedPrefer.getString( StaticClass.USE_LOCK_CLASS_NAME , defaultClassName );
		Log.v( "LockHomeActivity" , "curpackname = " + curPackagename + " curClassname = " + curClassname );
		if( cooeeLockerIsTopActivity( curPackagename , curClassname ) )
		{
			Log.v( "LockHomeActivity" , "istop = " + true );
			finish();
		}
		else
		{
			Log.v( "LockHomeActivity" , "istop = " + false );
			PackageManager pkgMgt = this.getPackageManager();
			Intent it = new Intent( Intent.ACTION_MAIN );
			it.addCategory( Intent.CATEGORY_HOME );
			List<ResolveInfo> ra = pkgMgt.queryIntentActivities( it , 0 );
			String default_packname = ra.get( 0 ).activityInfo.packageName;
			String default_classname = ra.get( 0 ).activityInfo.name;
			String packname = sharedPrefer.getString( StaticClass.CUR_LAUNCHER_PACKNAME , default_packname );
			String classname = sharedPrefer.getString( StaticClass.CUR_LAUNCHER_CLASSNAME , default_classname );
			Intent intent = new Intent();
			intent.setClassName( packname , classname );
			startActivity( intent );
			finish();
		}
	}
	
	@Override
	protected void onNewIntent(
			Intent intent )
	{
		super.onNewIntent( intent );
		// String curPackagename = sharedPrefer.getString(
		// StaticClass.USE_LOCK_PACKAGE_NAME, "");
		// String curClassname = sharedPrefer.getString(
		// StaticClass.USE_LOCK_CLASS_NAME, "");
		// Log.v("LockHomeActivity", "curpackname = " + curPackagename
		// + " curClassname = " + curClassname);
		// if (cooeeLockerIsTopActivity(curPackagename, curClassname)) {
		// Log.v("LockHomeActivity", "istop = " + true);
		// finish();
		// } else {
		// String packname = sharedPrefer.getString(
		// StaticClass.CUR_LAUNCHER_PACKNAME, "");
		// String classname = sharedPrefer.getString(
		// StaticClass.CUR_LAUNCHER_CLASSNAME, "");
		// Intent intent1 = new Intent();
		// intent1.setClassName(packname, classname);
		// startActivity(intent1);
		// finish();
		// }
	}
	
	private boolean cooeeLockerIsTopActivity(
			String packname ,
			String classname )
	{
		ActivityManager am = (ActivityManager)getSystemService( Context.ACTIVITY_SERVICE );
		List<RunningTaskInfo> runningTasks = am.getRunningTasks( 1000 );
		for( RunningTaskInfo rti : runningTasks )
		{
			ComponentName component = rti.topActivity;
			Log.v( "service1" , "packname = " + packname + "  classname = " + classname );
			Log.v( "service1" , "TopActivity packname = " + component.getPackageName() + "  classname = " + component.getClassName() );
			if( component.getPackageName().equals( packname ) && component.getClassName().equals( classname ) )
				return true;
		}
		return false;
	}
}
