package com.iLoong.launcher.theme;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.iLoong.launcher.Desktop3D.Log;


public class ThemeDetailedActivity extends Activity
{
	
	public ThemeDetailed mThemeDetailed;
	private int index;
	private ThemeDescription theme;
	
	public void onCreate(
			Bundle bundle )
	{
		Log.v( "ThemeDetailedActivity" , "onCreate" );
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		super.onCreate( bundle );
		requestWindowFeature( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		mThemeDetailed = new ThemeDetailed( this );
		Bundle extras = getIntent().getExtras();
		index = extras.getInt( ThemeDetailedActivity.class.getSimpleName() );
		if( index >= ThemeManager.getInstance().getThemeDescriptions().size() )
		{
			finish();
		}
		else
		{
			theme = ThemeManager.getInstance().getThemeDescriptions().elementAt( index );
			if( !ThemeManager.getInstance().FindThemes( theme.componentName.getPackageName() ) )
			{
				finish();
			}
			else
			{
				ThemeManager.getInstance().pushActivity( this );
				setContentView( mThemeDetailed );
				mThemeDetailed.LoadData( index );
			}
		}
	}
	
	protected void onStart()
	{
		Log.v( "ThemeDetailedActivity" , "onStart" );
		super.onStart();
		if( !ThemeManager.getInstance().FindThemes( theme.componentName.getPackageName() ) )
		{
			finish();
		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		mThemeDetailed.Release();
		ThemeManager.getInstance().popupActivity( this );
		Log.v( "ThemeDetailedActivity" , "onDestroy" );
	}
}
