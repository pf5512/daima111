package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.Messenger;


public class NewspageSettingActivity extends Activity implements OnClickListener , OnCheckedChangeListener
{
	
	public static final String DESKTOP_SETTING_SP_NAME = "DesktopSetting";
	public static final String DESKTOP_SETTING_SP_OPEN_NEWSPAGE_KEY = "open_newspage";
	public static final String DESKTOP_SETTING_SP_ENABLE_HANDLE_KEY = "desktop_setting_enable_handle";
	public static final String DESKTOP_SETTING_SP_ENABLE_BACK_KEY = "desktop_setting_enable_back";
	public static final String DESKTOP_SETTING_SP_ENABLE_SHAKE_KEY = "desktop_setting_enable_shake";
	private LinearLayout ll_back;
	private LinearLayout ll_open_newspage;
	private LinearLayout ll_nhandle;
	private LinearLayout ll_nback;
	private LinearLayout ll_nshake;
	private CheckBox cb_open_newspage;
	private CheckBox cb_mhandle;
	private CheckBox cb_mback;
	private CheckBox cb_mshake;
	private static boolean isNewspageOpened = false;
	private SharedPreferences sp;
	private Editor editor;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_desktop_setting_newspage );
		findViewById();
		setListener();
		init();
	}
	
	private void init()
	{
		sp = getSharedPreferences( DESKTOP_SETTING_SP_NAME , MODE_PRIVATE );
		editor = sp.edit();
		isNewspageOpened = sp.getBoolean( DESKTOP_SETTING_SP_OPEN_NEWSPAGE_KEY , true );
		if( isNewspageOpened )
		{
			open();
		}
		else
		{
			close();
		}
		cb_mhandle.setChecked( sp.getBoolean( DESKTOP_SETTING_SP_ENABLE_HANDLE_KEY , false ) );
		cb_mback.setChecked( sp.getBoolean( DESKTOP_SETTING_SP_ENABLE_BACK_KEY , true ) );
		cb_mshake.setChecked( sp.getBoolean( DESKTOP_SETTING_SP_ENABLE_SHAKE_KEY , false ) );
	}
	
	private void findViewById()
	{
		ll_back = (LinearLayout)findViewById( R.id.ll_back );
		ll_open_newspage = (LinearLayout)findViewById( R.id.ll_open_newspage );
		cb_open_newspage = (CheckBox)findViewById( R.id.cb_open_newspage );
		ll_nhandle = (LinearLayout)findViewById( R.id.ll_nhandle );
		ll_nback = (LinearLayout)findViewById( R.id.ll_nback );
		ll_nshake = (LinearLayout)findViewById( R.id.ll_nshake );
		cb_mhandle = (CheckBox)findViewById( R.id.cb_mhandle );
		cb_mback = (CheckBox)findViewById( R.id.cb_mback );
		cb_mshake = (CheckBox)findViewById( R.id.cb_mshake );
	}
	
	private void setListener()
	{
		ll_back.setOnClickListener( this );
		ll_open_newspage.setOnClickListener( this );
		cb_open_newspage.setOnCheckedChangeListener( this );
		ll_nhandle.setOnClickListener( this );
		ll_nback.setOnClickListener( this );
		ll_nshake.setOnClickListener( this );
	}
	
	private void open()
	{
		isNewspageOpened = true;
		cb_open_newspage.setChecked( true );
		DefaultLayout.enable_news = true;
		ll_nhandle.setVisibility( View.VISIBLE );
		ll_nback.setVisibility( View.VISIBLE );
		ll_nshake.setVisibility( View.VISIBLE );
	}
	
	private void close()
	{
		isNewspageOpened = false;
		cb_open_newspage.setChecked( false );
		DefaultLayout.enable_news = false;
		ll_nhandle.setVisibility( View.INVISIBLE );
		ll_nback.setVisibility( View.INVISIBLE );
		ll_nshake.setVisibility( View.INVISIBLE );
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
	protected void onDestroy()
	{
		overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
		editor.putBoolean( DESKTOP_SETTING_SP_OPEN_NEWSPAGE_KEY , isNewspageOpened );
		editor.putBoolean( DESKTOP_SETTING_SP_ENABLE_HANDLE_KEY , cb_mhandle.isChecked() );
		editor.putBoolean( DESKTOP_SETTING_SP_ENABLE_BACK_KEY , cb_mback.isChecked() );
		editor.putBoolean( DESKTOP_SETTING_SP_ENABLE_SHAKE_KEY , cb_mshake.isChecked() );
		if( cb_mhandle.isChecked() )
		{
			if( isNewspageOpened )
			{
				Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
			}
			else
			{
				Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
			}
		}
		else
		{
			Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
		}
		editor.commit();
		super.onDestroy();
	}
	
	public static boolean getIsNewspageOpened()
	{
		return isNewspageOpened;
	}
	
	public static void setIsNewspageOpened(
			boolean b )
	{
		isNewspageOpened = b;
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.ll_back:
				finish();
				break;
			case R.id.ll_open_newspage:
				if( !isNewspageOpened )
					open();
				else
					close();
				break;
			case R.id.ll_nhandle:
				cb_mhandle.setChecked( !cb_mhandle.isChecked() );
				DefaultLayout.show_newspage_with_handle = cb_mhandle.isChecked();
				break;
			case R.id.ll_nback:
				cb_mback.setChecked( !cb_mback.isChecked() );
				DefaultLayout.show_newspage_with_back = cb_mback.isChecked();
				break;
			case R.id.ll_nshake:
				cb_mshake.setChecked( !cb_mshake.isChecked() );
				DefaultLayout.show_newspage_with_shake = cb_mshake.isChecked();
				break;
		}
	}
	
	@Override
	public void onCheckedChanged(
			CompoundButton buttonView ,
			boolean isChecked )
	{
		switch( buttonView.getId() )
		{
			case R.id.cb_open_newspage:
				break;
		}
	}
}
