package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.umeng.analytics.MobclickAgent;


public class IconsActivity extends Activity implements View.OnClickListener , SeekBar.OnSeekBarChangeListener , CompoundButton.OnCheckedChangeListener
{
	
	LinearLayout iconsllback = null;
	//	SeekBar icons_iconsizeseekbar=null;
	//	TextView testtxt=null;
	//	ImageView icon_sizebg=null;
	//	ImageView icon_sizeicon=null;
	//	private LayoutParams lpiconsize;
	//	private int app_icon_big_size = 0;
	//	private int app_icon_normal_size = 0;
	//	private int app_icon_small_size = 0;
	RelativeLayout dsicon_big = null;
	RadioButton dsicon_bigradio = null;
	RelativeLayout dsicon_middle = null;
	RadioButton dsicon_middleradio = null;
	RelativeLayout dsicon_small = null;
	RadioButton dsicon_smallradio = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		//		setContentView(R.layout.dsiconsactivity);
		setContentView( R.layout.dsicons );
		iconsllback = (LinearLayout)findViewById( R.id.iconstmp_backll );
		iconsllback.setOnClickListener( this );
		dsicon_big = (RelativeLayout)findViewById( R.id.dsicon_big );
		dsicon_big.setOnClickListener( this );
		dsicon_bigradio = (RadioButton)findViewById( R.id.dsicon_bigradio );
		dsicon_bigradio.setOnClickListener( this );
		dsicon_bigradio.setOnCheckedChangeListener( this );
		dsicon_middle = (RelativeLayout)findViewById( R.id.dsicon_middle );
		dsicon_middle.setOnClickListener( this );
		dsicon_middleradio = (RadioButton)findViewById( R.id.dsicon_middleradio );
		dsicon_middleradio.setOnClickListener( this );
		dsicon_middleradio.setOnCheckedChangeListener( this );
		dsicon_small = (RelativeLayout)findViewById( R.id.dsicon_small );
		dsicon_small.setOnClickListener( this );
		dsicon_smallradio = (RadioButton)findViewById( R.id.dsicon_smallradio );
		dsicon_smallradio.setOnClickListener( this );
		dsicon_smallradio.setOnCheckedChangeListener( this );
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		int currentIconSize = sp.getInt( "currentIconSize" , -1 );
		switch( currentIconSize )
		{
			case R.id.dsicon_bigradio:
				dsicon_bigradio.setChecked( true );
				dsicon_middleradio.setChecked( false );
				dsicon_smallradio.setChecked( false );
				break;
			case R.id.dsicon_middleradio:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( true );
				dsicon_smallradio.setChecked( false );
				break;
			case R.id.dsicon_smallradio:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( false );
				dsicon_smallradio.setChecked( true );
				break;
			default:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( true );
				dsicon_smallradio.setChecked( false );
				break;
		}
		//		icons_iconsizeseekbar=(SeekBar)findViewById( R.id.icons_iconsizeseekbar );
		//		icons_iconsizeseekbar.setOnSeekBarChangeListener( this );
		//		
		//		icon_sizeicon=(ImageView)findViewById( R.id.icon_sizeicon );
		//		if( Utils3D.getScreenWidth() > 700 )
		//		{// 大屏
		//			app_icon_big_size = R3D.getInteger( "app_icon_size_big_screen_big_icon_same_layout" );
		//			app_icon_normal_size = R3D.getInteger( "app_icon_size_big_screen_normal_icon_same_layout" );
		//			app_icon_small_size = R3D.getInteger( "app_icon_size_big_screen_small_icon_same_layout" );
		//		}
		//		else if( Utils3D.getScreenWidth() > 400 )
		//		{// 中屏
		//			app_icon_big_size = R3D.getInteger( "app_icon_size_normal_screen_big_icon_same_layout" );
		//			app_icon_normal_size = R3D.getInteger( "app_icon_size_normal_screen_normal_icon_same_layout" );
		//			app_icon_small_size = R3D.getInteger( "app_icon_size_normal_screen_small_icon_same_layout" );
		//		}
		//		else
		//		{// 小屏
		//			app_icon_big_size = R3D.getInteger( "app_icon_size_small_screen_big_icon_same_layout" );
		//			app_icon_normal_size = R3D.getInteger( "app_icon_size_small_screen_normal_icon_same_layout" );
		//			app_icon_small_size = R3D.getInteger( "app_icon_size_small_screen_small_icon_same_layout" );
		//		}
		//		
		//		LayoutParams params=icon_sizeicon.getLayoutParams();
		//		params.height=app_icon_big_size;
		//		params.width=app_icon_big_size;
		//		icon_sizeicon.setLayoutParams( params );
		//		
		//		icons_iconsizeseekbar.setProgress( (int)((app_icon_normal_size-app_icon_small_size)*100f/(app_icon_big_size-app_icon_small_size))+1 );
		//		lpiconsize=icon_sizeicon.getLayoutParams();
		//		lpiconsize.height=app_icon_normal_size;
		//		icon_sizeicon.setLayoutParams( lpiconsize );
		//		
		//		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		//		int currentIconsize=sp.getInt( "currenticonsize" , app_icon_normal_size );
		//		icons_iconsizeseekbar.setProgress( (int)((currentIconsize-app_icon_small_size)*100f/(app_icon_big_size-app_icon_small_size)) );
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.iconstmp_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			case R.id.dsicon_big:
				dsicon_bigradio.setChecked( true );
				dsicon_middleradio.setChecked( false );
				dsicon_smallradio.setChecked( false );
				MobclickAgent.onEvent( this , "DSettingIconSettingBig" );
				break;
			case R.id.dsicon_bigradio:
				dsicon_bigradio.setChecked( true );
				dsicon_middleradio.setChecked( false );
				dsicon_smallradio.setChecked( false );
				MobclickAgent.onEvent( this , "DSettingIconSettingBig" );
				break;
			case R.id.dsicon_middle:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( true );
				dsicon_smallradio.setChecked( false );
				MobclickAgent.onEvent( this , "DSettingIconSettingMiddle" );
				break;
			case R.id.dsicon_middleradio:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( true );
				dsicon_smallradio.setChecked( false );
				MobclickAgent.onEvent( this , "DSettingIconSettingMiddle" );
				break;
			case R.id.dsicon_small:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( false );
				dsicon_smallradio.setChecked( true );
				MobclickAgent.onEvent( this , "DSettingIconSettingSmall" );
				break;
			case R.id.dsicon_smallradio:
				dsicon_bigradio.setChecked( false );
				dsicon_middleradio.setChecked( false );
				dsicon_smallradio.setChecked( true );
				MobclickAgent.onEvent( this , "DSettingIconSettingSmall" );
				break;
			default:
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
	public void onProgressChanged(
			SeekBar seekBar ,
			int progress ,
			boolean fromUser )
	{
		//		lpiconsize=icon_sizeicon.getLayoutParams();
		//		lpiconsize.height=app_icon_small_size+(int)(progress*(app_icon_big_size-app_icon_small_size)/100f);
		//		icon_sizeicon.setLayoutParams( lpiconsize );
	}
	
	@Override
	public void onStartTrackingTouch(
			SeekBar seekBar )
	{
	}
	
	@Override
	public void onStopTrackingTouch(
			SeekBar seekBar )
	{
		//		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		//		Editor ed = sp.edit();
		//		ed.putInt( "currenticonsize" , app_icon_small_size+(int)(seekBar.getProgress()*(app_icon_big_size-app_icon_small_size)/100f) );
		//		ed.commit();
		//		
		//		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
		//		pref.edit().putString( "icon_size_key" , "" + 2).commit();
	}
	
	@Override
	protected void onPause()
	{
		//		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		//		int currentIconsize=sp.getInt( "currenticonsize" , app_icon_normal_size );
		//	
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		int currentIconSize = sp.getInt( "currentIconSize" , -1 );
		switch( currentIconSize )
		{
			case R.id.dsicon_bigradio:
				pref.edit().putString( "icon_size_key" , "" + 0 ).commit();
				break;
			case R.id.dsicon_middleradio:
				pref.edit().putString( "icon_size_key" , "" + 1 ).commit();
				break;
			case R.id.dsicon_smallradio:
				pref.edit().putString( "icon_size_key" , "" + 2 ).commit();
				break;
			default:
				break;
		}
		Intent intent = new Intent( "com.cooee.desktopsettings.iconsizechange" );
		//				intent.putExtra( "Iconsize" , currentIconsize );
		sendBroadcast( intent );
		super.onPause();
	}
	
	@Override
	public void onCheckedChanged(
			CompoundButton buttonView ,
			boolean isChecked )
	{
		SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		Editor ed = sp.edit();
		switch( buttonView.getId() )
		{
			case R.id.dsicon_bigradio:
				if( isChecked )
				{
					ed.putInt( "currentIconSize" , R.id.dsicon_bigradio );
					ed.commit();
				}
				break;
			case R.id.dsicon_middleradio:
				if( isChecked )
				{
					ed.putInt( "currentIconSize" , R.id.dsicon_middleradio );
					ed.commit();
				}
				break;
			case R.id.dsicon_smallradio:
				if( isChecked )
				{
					ed.putInt( "currentIconSize" , R.id.dsicon_smallradio );
					ed.commit();
				}
				break;
			default:
				break;
		}
	}
}
