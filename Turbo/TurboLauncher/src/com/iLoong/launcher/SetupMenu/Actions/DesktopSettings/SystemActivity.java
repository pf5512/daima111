package com.iLoong.launcher.SetupMenu.Actions.DesktopSettings;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.CopyDirectory;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class SystemActivity extends Activity implements View.OnClickListener
{
	
	private static final String DATABASE_NAME = "launcher.db";
	LinearLayout systemllback = null;
	LinearLayout system_vibrator = null;
	LinearLayout system_guide = null;
	CheckBox system_vibrator_checkbox = null;
	boolean ifsystem_vibrator_checkbox = true;
	LinearLayout system_backupdesktop = null;
	LinearLayout system_restoredesktop = null;
	LinearLayout system_restoredefault = null;
	TextView system_backuptxt = null;
	TextView system_restore = null;
	TextView system_restoretxt = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dssystemactivity );
		systemllback = (LinearLayout)findViewById( R.id.system_backll );
		systemllback.setOnClickListener( this );
		system_vibrator = (LinearLayout)findViewById( R.id.system_vibrator );
		system_vibrator.setOnClickListener( this );
		system_guide = (LinearLayout)findViewById( R.id.system_guide );
		system_guide.setOnClickListener( this );
		system_vibrator_checkbox = (CheckBox)findViewById( R.id.system_vibrator_checkbox );
		SharedPreferences sps = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
		boolean ifchecked = sps.getBoolean( "system_vibrator_checkbox" , true );
		ifsystem_vibrator_checkbox = ifchecked;
		system_vibrator_checkbox.setChecked( ifchecked );
		system_vibrator_checkbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				ifsystem_vibrator_checkbox = isChecked;
				SharedPreferences sp = getSharedPreferences( "DesktopSetting" , MODE_PRIVATE );
				Editor ed = sp.edit();
				ed.putBoolean( "system_vibrator_checkbox" , isChecked );
				ed.commit();
			}
		} );
		system_backupdesktop = (LinearLayout)findViewById( R.id.system_backupdesktop );
		system_backupdesktop.setOnClickListener( this );
		system_restoredesktop = (LinearLayout)findViewById( R.id.system_restoredesktop );
		system_restoredesktop.setOnClickListener( this );
		system_restoredefault = (LinearLayout)findViewById( R.id.system_restoredefault );
		system_restoredefault.setOnClickListener( this );
		system_backuptxt = (TextView)findViewById( R.id.system_backuptxt );
		system_restoretxt = (TextView)findViewById( R.id.system_restoretxt );
		system_restore = (TextView)findViewById( R.id.system_restore );
		setCheckBackup();
	}
	
	public void setCheckBackup()
	{
		int androidSDKVersion = Integer.parseInt( VERSION.SDK );
		if( checkBackup() )
		{
			system_restoredesktop.setEnabled( true );

//			if( androidSDKVersion >= 11 )
//			{
//				system_restoredesktop.setAlpha( 1.0f );
//			}

		}
		else
		{
			system_restoredesktop.setEnabled( false );

//			if( androidSDKVersion >= 11 )
//			{
//				system_restoredesktop.setAlpha( 0.7f );
//			}

		}
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.system_guide:
				//MobclickAgent.onEvent( this , "DSettingToGuide" );
				Intent intent = new Intent( this , iLoongLauncher.class );
				startActivity( intent );
				//Desktop3DListener.getInstance().startWelcomeGuide();
				this.finish();
				break;
			case R.id.system_backll:
				finish();
				overridePendingTransition( R.anim.dsalphain , R.anim.dsmove_out_right );
				break;
			case R.id.system_vibrator:
				system_vibrator_checkbox.setChecked( !ifsystem_vibrator_checkbox );
				//UmEventUtil.vibrator( iLoongLauncher.getInstance() , "DSettingSysVibrator" , ifsystem_vibrator_checkbox ? "yes" : "no" , 60000 );
				break;
			case R.id.system_backupdesktop:
				MobclickAgent.onEvent( this , "DSettingBackupDesktop" );
				if( iLoongApplication.getSDPath() == null )
				{
					Toast.makeText( this , getString( R.string.backup_pls_insert_SD ) , Toast.LENGTH_SHORT ).show();
				}
				else
				{
					backdialog( getString( R.string.backup_title_back ) , getString( R.string.backup_back_to_SD ) );
				}
				break;
			case R.id.system_restoredesktop:
				restoredialog( getString( R.string.backup_title_restore ) , getString( R.string.backup_restore ) );
				MobclickAgent.onEvent( this , "DSettingRestoreDesktop" );
				break;
			case R.id.system_restoredefault:
				resetialog( getResources().getString( R.string.system_restoredefault ) , getResources().getString( R.string.system_reset_msg ) );
				MobclickAgent.onEvent( this , "DSettingRestoreDefaultDesktop" );
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
	protected void onPause()
	{
		Intent intent = new Intent( "com.cooee.desktopsettings.system.vibratorchange" );
		intent.putExtra( "vibratorstate" , ifsystem_vibrator_checkbox );
		sendBroadcast( intent );
		super.onPause();
	}
	
	@SuppressLint( "NewApi" )
	private void backdialog(
			String title ,
			String msg )
	{
		AlertDialog.Builder builder = null;
		int androidSDKVersion = Integer.parseInt( VERSION.SDK );
		if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
		{
			builder = new AlertDialog.Builder( this , AlertDialog.THEME_HOLO_LIGHT );
		}
		else
		{
			builder = new AlertDialog.Builder( this );
		}
		builder.setMessage( msg );
		builder.setTitle( title );
		builder.setPositiveButton( getString( R.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				backupdb();
			}
		} );
		builder.setNegativeButton( getString( R.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
	
	public void backupdb()
	{
		String dbSourceDir = iLoongApplication.getInstance().getDatabasePath( DATABASE_NAME ).getParent();
		String dbTargetDir = iLoongApplication.getBackupPath() + "/databases/";
		String prefsSourceDir = iLoongApplication.getPreferencePath();
		String prefsTargetDir = iLoongApplication.getBackupPath() + "/shared_prefs/";
		CopyDirectory.delete( new File( iLoongApplication.getBackupPath() ) );
		try
		{
			CopyDirectory.copyDirectiory( dbSourceDir , dbTargetDir );
			CopyDirectory.copyDirectiory( prefsSourceDir , prefsTargetDir );
			Toast.makeText( this , getString( R.string.backup_success ) , Toast.LENGTH_SHORT ).show();
			setCheckBackup();
		}
		catch( IOException e )
		{
			e.printStackTrace();
			Toast.makeText( this , getString( R.string.backup_fail ) , Toast.LENGTH_SHORT ).show();
		}
	}
	
	@SuppressLint( "NewApi" )
	private void restoredialog(
			String title ,
			String msg )
	{
		AlertDialog.Builder builder = null;
		int androidSDKVersion = Integer.parseInt( VERSION.SDK );
		if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
		{
			builder = new AlertDialog.Builder( this , AlertDialog.THEME_HOLO_LIGHT );
		}
		else
		{
			builder = new AlertDialog.Builder( this );
		}
		builder.setMessage( msg );
		builder.setTitle( title );
		builder.setPositiveButton( getString( R.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				if( restoredb() )
				{
					finish();
					SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
				}
			}
		} );
		builder.setNegativeButton( getString( R.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
	
	public boolean restoredb()
	{
		boolean bret = true;
		String dbTargetDir = iLoongApplication.getInstance().getDatabasePath( DATABASE_NAME ).getParent();
		String dbSourceDir = iLoongApplication.getBackupPath() + "/databases/";
		String prefsTargetDir = iLoongApplication.getPreferencePath();
		String prefsSourceDir = iLoongApplication.getBackupPath() + "/shared_prefs/";
		CopyDirectory.delete( new File( dbTargetDir ) );
		CopyDirectory.delete( new File( prefsTargetDir ) );
		try
		{
			CopyDirectory.copyDirectiory( dbSourceDir , dbTargetDir );
			CopyDirectory.copyDirectiory( prefsSourceDir , prefsTargetDir );
		}
		catch( IOException e )
		{
			bret = false;
			Toast.makeText( this , getString( R.string.restore_fail ) , Toast.LENGTH_SHORT ).show();
			e.printStackTrace();
		}
		return bret;
	}
	
	private void resetialog(
			String title ,
			String msg )
	{
		AlertDialog.Builder builder = null;
		int androidSDKVersion = Integer.parseInt( VERSION.SDK );
		if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
		{
			builder = new AlertDialog.Builder( this , AlertDialog.THEME_HOLO_LIGHT );
		}
		else
		{
			builder = new AlertDialog.Builder( this );
		}
		builder.setMessage( msg );
		builder.setTitle( title );
		builder.setPositiveButton( getString( R.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
				DataCleanManager.cleanInternalCache( iLoongLauncher.getInstance() );
				DataCleanManager.cleanDatabases( iLoongLauncher.getInstance() );
				DataCleanManager.cleanSharedPreference( iLoongLauncher.getInstance() );
				DataCleanManager.cleanFiles( iLoongLauncher.getInstance() );
				DataCleanManager.cleanExternalCache( iLoongLauncher.getInstance() );
				iLoongLauncher.getInstance().finish();
				Intent intent = new Intent( SystemActivity.this , iLoongLauncher.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivity( intent );
				finish();
			}
		} );
		builder.setNegativeButton( getString( R.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
	}
	
	private boolean checkBackup()
	{
		boolean bret = false;
		File file = new File( iLoongApplication.getBackupPath() + "/databases/" , DATABASE_NAME );
		String strSumm = new String();
		String restoretxt = new String();
		if( file.exists() )
		{
			long createtime = file.lastModified();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis( createtime );
			strSumm = getString( R.string.backup_latest ) + "  " + cal.getTime().toLocaleString();
			restoretxt = getString( R.string.restore_desktoptxt );
			bret = true;
			//system_restoretxt.setTextColor( R.color.FontColor );
			//system_restore.setTextColor( R.color.FontColor );
			//system_restoretxt.setTextColor( R.color.dsfont_selector );
			//system_restore.setTextColor( R.color.dsfont_selector );
		}
		else
		{
			strSumm = getString( R.string.backup_no_back );
			restoretxt = getString( R.string.restore_desktoptxt_nolatest );
			//system_restoretxt.setTextColor( R.color.GreyColor );
			//system_restore.setTextColor( R.color.GreyColor );
		}
		system_backuptxt.setText( strSumm );
		system_restoretxt.setText( restoretxt );
		return bret;
	}
}
