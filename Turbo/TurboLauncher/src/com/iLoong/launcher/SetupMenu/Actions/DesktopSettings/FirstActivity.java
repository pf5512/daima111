package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.setting.DefaultClear;
import com.iLoong.launcher.setting.DefaultSetting;
import com.iLoong.launcher.setting.FakeLauncher;
import com.tencent.open.yyb.AppbarAgent;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;


public class FirstActivity extends Activity implements View.OnClickListener
{
	
	private final int RESULT_CODE_ASK_DEFAULT = 0;
	private LinearLayout ll_moren;
	private LinearLayout ll_screen;
	private LinearLayout ll_drawer;
	private LinearLayout ll_icon;
	private LinearLayout ll_system;
	private LinearLayout ll_newspage;
	private LinearLayout ll_appbar;
	private ImageView rateButton;
	private LinearLayout ll_about;
	private ImageView ll_default;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dsfirstactivity );
		ll_moren = (LinearLayout)findViewById( R.id.first_moren );
		//ll_moren.setVisibility( View.GONE );
		ll_screen = (LinearLayout)findViewById( R.id.first_screen );
		ll_drawer = (LinearLayout)findViewById( R.id.first_drawer );
		ll_icon = (LinearLayout)findViewById( R.id.first_icon );
		ll_system = (LinearLayout)findViewById( R.id.first_system );
		ll_newspage = (LinearLayout)findViewById( R.id.ll_newspage );
		ll_appbar = (LinearLayout)findViewById( R.id.first_appbar );
		ll_about = (LinearLayout)findViewById( R.id.first_about );
		ll_default = (ImageView)findViewById( R.id.is_default );
		if( !DefaultLayout.enable_show_appbar )
		{
			ll_appbar.setVisibility( View.GONE );
		}
		rateButton = (ImageView)findViewById( R.id.button_rate );
		if( !DefaultLayout.enable_google_version )
		{
			rateButton.setVisibility( View.INVISIBLE );
		}
		else
		{
			if( iLoongLauncher.getInstance().appReminder.isInRemind( "coco.desktopsettings" ) )
			{
				rateButton.setBackgroundResource( R.drawable.dsraterp );
			}
			//			Animation animation = AnimationUtils.loadAnimation( this , R.anim.rate_ani );
			//			rateButton.setAnimation( animation );
		}
		ll_moren.setOnClickListener( this );
		ll_screen.setOnClickListener( this );
		ll_drawer.setOnClickListener( this );
		ll_icon.setOnClickListener( this );
		ll_system.setOnClickListener( this );
		ll_newspage.setOnClickListener( this );
		ll_appbar.setOnClickListener( this );
		rateButton.setOnClickListener( this );
		ll_about.setOnClickListener( this );
		if( DefaultLayout.enable_google_version || VERSION.SDK_INT < 15 )
		{
			ll_newspage.setVisibility( View.GONE );
		}
	}
	
	public void onResume()
	{
		super.onResume();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		if( DefaultSetting.isMyLauncher() )
		{
			ll_default.setImageResource( R.drawable.dscheckbox_selected );
			pref.edit().putBoolean( "first_set_default" , true ).commit();
		}
		else
		{
			ll_default.setImageResource( R.drawable.dscheckbox_normal );
			if( !pref.getBoolean( "first_set_default" , false ) )
			{
				pref.edit().putBoolean( "first_set_default" , true ).commit();
				Intent intent = new Intent( FirstActivity.this , FakeLauncher.class );
				startActivityForResult( intent , RESULT_CODE_ASK_DEFAULT );
			}
		}
	}
	
	@Override
	protected void onActivityResult(
			int requestCode ,
			int resultCode ,
			Intent data )
	{
		if( resultCode == RESULT_OK )
		{
			switch( requestCode )
			{
				case RESULT_CODE_ASK_DEFAULT:
					//this.finish();
					break;
			}
		}
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.first_moren:
				if( DefaultSetting.isMyLauncher() )
				{
					startActivityForId( DefaultClear.class );
				}
				else
				{
					startActivityForId( DefaultSetting.class );
				}
				break;
			case R.id.button_rate:
				if( iLoongLauncher.getInstance().appReminder.isRemindApp( "coco.desktopsettings" ) )
				{
					iLoongLauncher.getInstance().appReminder.endRemind( "coco.desktopsettings" );
					rateButton.setBackgroundResource( R.drawable.dsrate );
				}
				startActivityForId( RateDialogActivity.class );
				break;
			case R.id.first_screen:
				//MobclickAgent.onEvent( this , "DSettingToScreen" );
				startActivityForId( ScreenActivity.class );
				break;
			case R.id.first_drawer:
				MobclickAgent.onEvent( this , "DSettingToDrawer" );
				startActivityForId( DrawerActivity.class );
				break;
			case R.id.first_icon:
				//MobclickAgent.onEvent( this , "DSettingToIcon" );
				startActivityForId( IconsActivity.class );
				break;
			case R.id.first_system:
				//MobclickAgent.onEvent( this , "DSettingToSys" );
				startActivityForId( SystemActivity.class );
				break;
			case R.id.ll_newspage:
				startActivityForId( NewspageSettingActivity.class );
				break;
			case R.id.first_appbar:
				MobclickAgent.onEvent( this , "DSettingToAppbar" );
				Tencent mTencent = Tencent.createInstance( "1101476808" , FirstActivity.this );
				mTencent.startAppbar( FirstActivity.this , AppbarAgent.TO_APPBAR_DETAIL );
				break;
			case R.id.first_about:
				startActivityForId( AboutActivity.class );
				break;
		}
	}
	
	UmengUpdateListener updateListener = new UmengUpdateListener() {
		
		@Override
		public void onUpdateReturned(
				int updateStatus ,
				UpdateResponse updateInfo )
		{
			switch( updateStatus )
			{
				case UpdateStatus.Yes: //has update
					UmengUpdateAgent.showUpdateDialog( FirstActivity.this , updateInfo );
					break;
				case UpdateStatus.No: //has no update
					showDialog( FirstActivity.this.getResources().getString( R.string.update_dialogmsg ) );
					break;
				case UpdateStatus.NoneWifi: //none wifi
					Toast.makeText( FirstActivity.this , FirstActivity.this.getResources().getString( R.string.update_wifi ) , Toast.LENGTH_SHORT ).show();
					break;
				case UpdateStatus.Timeout: //time out
					Toast.makeText( FirstActivity.this , FirstActivity.this.getResources().getString( R.string.update_timeout ) , Toast.LENGTH_SHORT ).show();
					break;
				default:
					break;
			}
		}
	};
	
	private void showDialog(
			String content )
	{
		AlertDialog.Builder builder = new Builder( this ).setTitle( RR.string.version_update ).setMessage( content ).setPositiveButton( android.R.string.ok , null );
		builder.show();
	}
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish();
			overridePendingTransition( R.anim.dszoomout , R.anim.dszoomin );
		}
		return super.onKeyDown( keyCode , event );
	}
	
	private void startActivityForId(
			Class<?> cls )
	{
		Intent in = new Intent( FirstActivity.this , cls );
		startActivity( in );
		if( Integer.valueOf( Build.VERSION.SDK ).intValue() >= 5 )
		{
			overridePendingTransition( R.anim.dsmove_in_right , R.anim.dsalphaout );
		}
	}
}
