package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class ScreenActivity extends Activity implements View.OnClickListener
{
	
	LinearLayout screenllback = null;
	LinearLayout screen_wallpaper = null;
	CheckBox screen_wallpaper_checkbox = null;
	LinearLayout screen_wallpaperscroll = null;
	boolean ifscreen_wallpaperscroll_checkbox = true;
	LinearLayout screen_infinitescroll = null;
	CheckBox screen_infinitescroll_checkbox = null;
	boolean ifscreen_infinitescroll_checkbox = true;
	LinearLayout screen_traneffect = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dsscreenactivity );
		screenllback = (LinearLayout)findViewById( R.id.screen_backll );
		screenllback.setOnClickListener( this );
		screen_wallpaper = (LinearLayout)findViewById( R.id.screen_wallpaper );
		screen_wallpaper.setOnClickListener( this );
		screen_wallpaperscroll = (LinearLayout)findViewById( R.id.screen_wallpaperscroll );
		screen_wallpaperscroll.setOnClickListener( this );
		screen_wallpaper_checkbox = (CheckBox)findViewById( R.id.screen_wscheckbox );
		SharedPreferences sps = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		boolean ifchecked = sps.getBoolean( "screen_wallpaper_checkbox" , true );
		ifscreen_wallpaperscroll_checkbox = ifchecked;
		screen_wallpaper_checkbox.setChecked( ifchecked );
		screen_wallpaper_checkbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				ifscreen_wallpaperscroll_checkbox = isChecked;
				SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
				Editor ed = sp.edit();
				ed.putBoolean( "screen_wallpaper_checkbox" , isChecked );
				ed.commit();
			}
		} );
		screen_traneffect = (LinearLayout)findViewById( R.id.screen_traneffect );
		screen_traneffect.setOnClickListener( this );
		screen_infinitescroll = (LinearLayout)findViewById( R.id.screen_infinitescroll );
		screen_infinitescroll.setOnClickListener( this );
		screen_infinitescroll_checkbox = (CheckBox)findViewById( R.id.screen_infinitescroll_checkbox );
		boolean ifscrollchecked = sps.getBoolean( "screen_infinitescroll_checkbox" , true );
		ifscreen_infinitescroll_checkbox = ifscrollchecked;
		screen_infinitescroll_checkbox.setChecked( ifscrollchecked );
		screen_infinitescroll_checkbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				ifscreen_infinitescroll_checkbox = isChecked;
				SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
				Editor ed = sp.edit();
				ed.putBoolean( "screen_infinitescroll_checkbox" , isChecked );
				ed.commit();
			}
		} );
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.screen_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			case R.id.screen_wallpaper:
				SendMsgToAndroid.sendSelectWallpaper();
				//MobclickAgent.onEvent( this , "DSettingToWallpaper" );
				break;
			case R.id.screen_wallpaperscroll:
				screen_wallpaper_checkbox.setChecked( !ifscreen_wallpaperscroll_checkbox );
				//UmEventUtil.wallpaperScroll( iLoongLauncher.getInstance() , "DSettingWallpaperScroll" , ifscreen_wallpaperscroll_checkbox ? "yes" : "no" , 60000 );
				break;
			case R.id.screen_infinitescroll:
				screen_infinitescroll_checkbox.setChecked( !ifscreen_infinitescroll_checkbox );
				//UmEventUtil.infiniteScroll( iLoongLauncher.getInstance() , "DSettingInfiniteScroll" , ifscreen_infinitescroll_checkbox ? "yes" : "no" , 60000 );
				break;
			case R.id.screen_traneffect:
				MobclickAgent.onEvent( this , "DSettingToDesktopEffect" );
				if( iLoongLauncher.entryWorksapceCount == iLoongLauncher.DOCKBAR_DELAY_COUNT )
				{
					iLoongLauncher.entryWorksapceCount--;
				}
				Intent intent = new Intent( this , iLoongLauncher.class );
				startActivity( intent );
				if( Desktop3DListener.root != null )
				{
					Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_LONGCLICK );
				}
				if( DesktopEditHost.getInstance() != null )
				{
					DesktopEditHost.getInstance().mulpMenuHost.MenuCallBack( 3 );
				}
				break;
		}
	}
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish();
			overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	protected void onPause()
	{
		Intent intent = new Intent( "com.cooee.desktopsettings.screen.screenchange" );
		intent.putExtra( "wallpaperscrollstate" , ifscreen_wallpaperscroll_checkbox );
		intent.putExtra( "ScreenInfinitescrollstate" , ifscreen_infinitescroll_checkbox );
		sendBroadcast( intent );
		super.onPause();
	}
}
