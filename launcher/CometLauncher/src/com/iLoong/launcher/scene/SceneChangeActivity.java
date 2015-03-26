// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.scene;


import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;

import android.app.Activity;
import android.os.Bundle;
import com.iLoong.launcher.Desktop3D.Log;
import com.umeng.analytics.MobclickAgent;


public class SceneChangeActivity extends Activity
{
	
	private SceneManager mThemeManager;
	private SceneDescription theme;
	
	public void onCreate(
			Bundle bundle )
	{
		Log.v( "ThemeChangeActivity" , "onCreate" );
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		super.onCreate( bundle );
		mThemeManager = SceneManager.getInstance();
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
			// mThemeManager.ApplyTheme(theme);
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
	
	@Override
	protected void onResume()
	{
		super.onResume();
		//友盟统计
		MobclickAgent.onResume( this );
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		//友盟统计
		MobclickAgent.onPause( this );
	}
}
