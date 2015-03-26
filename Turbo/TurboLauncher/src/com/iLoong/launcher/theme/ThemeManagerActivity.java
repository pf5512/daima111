package com.iLoong.launcher.theme;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.iLoong.launcher.Desktop3D.Log;


public class ThemeManagerActivity extends Activity
{
	
	public ThemesDesktop mThemeDesktop;
	
	public void onCreate(
			Bundle bundle )
	{
		Log.v( "ThemeManagerActivity" , "onCreate" );
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		super.onCreate( bundle );
		ThemeManager.getInstance().pushActivity( this );
		requestWindowFeature( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		mThemeDesktop = new ThemesDesktop( this );
		setContentView( mThemeDesktop );
	}
	
	protected void onStart()
	{
		Log.v( "ThemeManagerActivity" , "onStart" );
		super.onStart();
		mThemeDesktop.LoadData();
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		mThemeDesktop.Release();
		ThemeManager.getInstance().popupActivity( this );
		Log.v( "ThemeManagerActivity" , "onDestroy" );
	}
}
