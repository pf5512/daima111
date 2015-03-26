package com.iLoong.launcher.theme;


import android.app.Activity;
import android.os.Bundle;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;


public class ThemeChangeActivity extends Activity
{
	
	private ThemeManager mThemeManager;
	private ThemeDescription theme;
	
	public void onCreate(
			Bundle bundle )
	{
		Log.v( "ThemeChangeActivity" , "onCreate" );
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate( bundle );
		mThemeManager = ThemeManager.getInstance();
		int theme_num = mThemeManager.getThemeDescriptions().size();
		if( theme_num == 1 )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_theme_to_be_replaced ) );
			finish();
			return;
		}
		int theme_index = 0;
		theme = mThemeManager.getCurrentThemeDescription();
		for( int i = 0 ; i < theme_num ; i++ )
		{
			if( theme == mThemeManager.getThemeDescriptions().elementAt( i ) )
			{
				theme_index = i;
				break;
			}
		}
		theme_index++;
		if( theme_index >= theme_num )
		{
			theme_index = 0;
		}
		theme = mThemeManager.getThemeDescriptions().elementAt( theme_index );
		if( !mThemeManager.FindThemes( theme.componentName.getPackageName() ) )
		{
			finish();
		}
		else
		{
			mThemeManager.pushActivity( this );
			mThemeManager.ApplyTheme( theme );
		}
	}
	
	protected void onStart()
	{
		Log.v( "ThemeChangeActivity" , "onStart" );
		super.onStart();
	}
	
	protected void onDestroy()
	{
		Log.v( "ThemeChangeActivity" , "onDestroy" );
		super.onDestroy();
		mThemeManager.popupActivity( this );
	}
}
