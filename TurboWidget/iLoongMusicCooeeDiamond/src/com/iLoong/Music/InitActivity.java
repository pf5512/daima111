package com.iLoong.Music;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.umeng.analytics.CoCoMobclickAgent;
import com.umeng.analytics.MobclickAgent;


public class InitActivity extends Activity
{
	
	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume( this );
		CoCoMobclickAgent.NewUser( this );
		MobclickAgent.onError( this );
		if( isAvilible( this , "com.cooeeui.turbolauncher" ) )
		{
			Intent intent = getPackageManager().getLaunchIntentForPackage( "com.cooeeui.turbolauncher" );
			startActivity( intent );
		}
		else
		{
			Intent intent = new Intent( InitActivity.this , TurboActivity.class );
			startActivity( intent );
			finish();
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause( this );
	}
	
	private boolean isAvilible(
			Context context ,
			String packageName )
	{
		PackageInfo packageInfo;
		try
		{
			packageInfo = context.getPackageManager().getPackageInfo( packageName , 0 );
		}
		catch( NameNotFoundException e )
		{
			packageInfo = null;
			e.printStackTrace();
		}
		if( packageInfo == null )
			return false;
		else
			return true;
	}
}
