package com.iLoong.launcher.SetupMenu.Actions;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.CopyDirectory;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.update.UpdateTask;
import com.umeng.analytics.MobclickAgent;


public class DesktopAction extends Action
{
	
	private static final String DATABASE_NAME = "launcher.db";
	
	public DesktopAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , DesktopSettingActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_DESKTOP_SETTINGS , new DesktopAction( ActionSetting.ACTION_DESKTOP_SETTINGS , DesktopAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		// ClingManager.getInstance().cancelSettingCling();
		SynRunAction();
	}
	
	@Override
	protected void OnActionFinish()
	{
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
		if( key.equals( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) )
		{
			mBundle.putInt( key , Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) ) );
		}
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) )
		{
			mBundle.putInt( key , Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) ) );
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_vibrator ) ) )
		{
			int val = 0;
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) ? 1 : 0;
			mBundle.putInt( key , val );
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_circled ) ) )
		{
			int val = 0;
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false ) ? 1 : 0;
			mBundle.putInt( key , val );
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_resetwizard ) ) )
		{
			mBundle.putInt( key , 1 );
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_shake_wallpaper ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false ) );
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.icon_size_key ) ) )
		{
			mBundle.putString( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "-1" ) );
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_sensor ) ) && ( DefaultLayout.show_sensor ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_open_sensor ) );
			// xiatian add end
		}
		else if( key.equals( DesktopSettingActivity // added by zhenNan.ye begin
				.getKey( RR.string.setting_key_particle ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , ParticleManager.particleManagerEnable ) );
		} // added by zhenNan.ye end
			//xiatian add start	//Mainmenu Bg
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.mainmenu_bg_key ) ) )
		{
			mBundle.putString( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "-1" ) );
		}
		//xiatian add end		
	}
	
	public static class DesktopSettingActivity extends PreferenceActivity implements OnPreferenceChangeListener , Preference.OnPreferenceClickListener
	{
		
		@Override
		public void onCreate(
				Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( RR.xml.desktopsetting );
			showitem();
			callback();
		}
		
		private void callback()
		{
			if( findPreference( getKey( RR.string.setting_key_desktopeffects ) ) != null )
				findPreference( getKey( RR.string.setting_key_desktopeffects ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_appeffects ) ) != null )
				findPreference( getKey( RR.string.setting_key_appeffects ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_exit ) ) != null )
				findPreference( getKey( RR.string.setting_key_exit ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_backup ) ) != null )
				findPreference( getKey( RR.string.setting_key_backup ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_restore ) ) != null )
				findPreference( getKey( RR.string.setting_key_restore ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_restart ) ) != null )
				findPreference( getKey( RR.string.setting_key_restart ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_share ) ) != null )
				findPreference( getKey( RR.string.setting_key_share ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_update ) ) != null )
				findPreference( getKey( RR.string.setting_key_update ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_feedback ) ) != null )
				findPreference( getKey( RR.string.setting_key_feedback ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_vibrator ) ) != null )
				findPreference( getKey( RR.string.setting_key_vibrator ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_circled ) ) != null )
				findPreference( getKey( RR.string.setting_key_circled ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_installhelp ) ) != null )
				findPreference( getKey( RR.string.setting_key_installhelp ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_resetwizard ) ) != null )
				findPreference( getKey( RR.string.setting_key_resetwizard ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_shake_wallpaper ) ) != null )
				findPreference( getKey( RR.string.setting_key_shake_wallpaper ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.icon_size_key ) ) != null )
			{
				findPreference( getKey( RR.string.icon_size_key ) ).setOnPreferenceClickListener( this );
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if( findPreference( getKey( RR.string.setting_key_sensor ) ) != null && ( DefaultLayout.show_sensor ) )
				findPreference( getKey( RR.string.setting_key_sensor ) ).setOnPreferenceClickListener( this );
			// xiatian add end
			if( findPreference( getKey( RR.string.setting_update_desktop ) ) != null )
			{
				findPreference( getKey( RR.string.setting_update_desktop ) ).setOnPreferenceClickListener( this );
			}
			/************** added by zhenNan.ye begin*******************/
			if( findPreference( getKey( RR.string.setting_key_particle ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_particle ) ).setOnPreferenceClickListener( this );
			}
			/************** added by zhenNan.ye end*******************/
			//xiatian add start	//Mainmenu Bg
			if( findPreference( getKey( RR.string.mainmenu_bg_key ) ) != null )
			{
				findPreference( getKey( RR.string.mainmenu_bg_key ) ).setOnPreferenceClickListener( this );
			}
			//xiatian add end
			if( findPreference( getKey( RR.string.setting_key_rate ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_rate ) ).setOnPreferenceClickListener( this );
			}
		}
		
		private void showitem()
		{
			Resources res = SetupMenu.getContext().getResources();
			String key = res.getString( RR.string.setting_key_desktopeffects );
			ListPreference pf = (ListPreference)findPreference( key );
			CharSequence[] summarys;
			if( pf != null )
			{
				pf.setOnPreferenceChangeListener( this );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			if( DefaultLayout.disable_desktop_slide )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_effect_setting ) );
				if( pf != null )
					category.removePreference( pf );
			}
			key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			if( pf != null )
			{
				pf = (ListPreference)findPreference( key );
				pf.setOnPreferenceChangeListener( this );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
			boolean autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_vibrator ) , true );
			CheckBoxPreference vibratorpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_vibrator ) );
			if( autoStart )
			{
				vibratorpf.setChecked( autoStart );
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_circled ) , false );
			vibratorpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_circled ) );
			if( DefaultLayout.disable_circled )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( vibratorpf != null )
					category.removePreference( vibratorpf );
			}
			else
			{
				if( autoStart )
				{
					vibratorpf.setChecked( autoStart );
				}
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_wallpaper ) , false );
			CheckBoxPreference shake_wallpaper = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) );
			if( DefaultLayout.disable_shake_wallpaper )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( shake_wallpaper != null )
					category.removePreference( shake_wallpaper );
			}
			else
			{
				if( autoStart )
				{
					shake_wallpaper.setChecked( autoStart );
				}
			}
			key = res.getString( RR.string.icon_size_key );
			pf = (ListPreference)findPreference( key );
			if( pf != null )
			{
				pf.setOnPreferenceChangeListener( this );
				if( !DefaultLayout.show_icon_size )
				{
					PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_effect_setting ) );
					if( pf != null && category != null )
						category.removePreference( pf );
				}
				else
				{
					summarys = pf.getEntries();
					pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
				}
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_sensor ) , DefaultLayout.default_open_sensor );
			CheckBoxPreference sensorcbpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_sensor ) );
			if( !DefaultLayout.show_sensor )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( sensorcbpf != null )
					category.removePreference( sensorcbpf );
			}
			else
			{
				sensorcbpf.setOnPreferenceChangeListener( this );
				if( autoStart )
				{
					sensorcbpf.setChecked( autoStart );
				}
			}
			// xiatian add end
			/************** added by zhenNan.ye begin*******************/
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_particle ) , true );
			CheckBoxPreference particleCbpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_particle ) );
			if( !DefaultLayout.enable_particle || !ParticleManager.switchForTheme )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( particleCbpf != null )
				{
					category.removePreference( particleCbpf );
				}
			}
			else
			{
				if( autoStart )
				{
					particleCbpf.setChecked( autoStart );
				}
			}
			/************** added by zhenNan.ye end*******************/
			if( DefaultLayout.hide_backup_and_restore )
			{
				PreferenceScreen screen = (PreferenceScreen)this.findPreference( res.getString( RR.string.setting_key_desktop_setting ) );
				PreferenceCategory temp_category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_backup_and_restore ) );
				// Preference preference1 = (Preference) this
				// .findPreference(res
				// .getString(RR.string.backup_desktop));
				// Preference preference2 = (Preference) this
				// .findPreference(res
				// .getString(RR.string.restore_desktop));
				// temp_category.removePreference(preference1);
				// temp_category.removePreference(preference2);
				if( temp_category != null )
					screen.removePreference( temp_category );
			}
			else
			{
				key = res.getString( RR.string.setting_key_restore );
				Preference pfrestore = (Preference)findPreference( key );
				if( !checkBackup() )
				{
					pfrestore.setEnabled( false );
				}
				else
				{
					pfrestore.setEnabled( true );
				}
			}
			//xiatian add start	//Mainmenu Bg
			key = res.getString( RR.string.mainmenu_bg_key );
			pf = (ListPreference)findPreference( key );
			if( pf != null )
			{
				pf.setOnPreferenceChangeListener( this );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , DefaultLayout.defaultMainmenuBgIndex + "" ) )] );
			}
			//xiatian add end			
		}
		
		private boolean checkBackup()
		{
			boolean bret = false;
			String key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_backup );
			Preference pf = (Preference)findPreference( key );
			File file = new File( iLoongApplication.getBackupPath() , DATABASE_NAME );
			String strSumm = new String();
			if( file.exists() )
			{
				long createtime = file.lastModified();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis( createtime );
				strSumm = R3D.getString( RR.string.backup_latest ) + "  " + cal.getTime().toLocaleString();
				bret = true;
			}
			else
			{
				strSumm = R3D.getString( RR.string.backup_no_back );
			}
			pf.setSummary( strSumm );
			return bret;
		}
		
		private void backdialog(
				String title ,
				String msg )
		{
			AlertDialog.Builder builder = new Builder( this );
			builder.setMessage( msg );
			builder.setTitle( title );
			builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
				
				@Override
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					dialog.dismiss();
					bakcupdb();
					showitem();
				}
			} );
			builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
				
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
		
		private void restoredialog(
				String title ,
				String msg )
		{
			AlertDialog.Builder builder = new Builder( this );
			builder.setMessage( msg );
			builder.setTitle( title );
			builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
				
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
			builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
				
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
		
		private void resetClingdialog(
				String title ,
				String msg )
		{
			AlertDialog.Builder builder = new Builder( this );
			builder.setMessage( msg );
			builder.setTitle( title );
			builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
				
				@Override
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					dialog.dismiss();
					finish();
					SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESET_WIZARD );
					SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
				}
			} );
			builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
				
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
		
		public void bakcupdb()
		{
			String sourceDir = iLoongApplication.getInstance().getDatabasePath( DATABASE_NAME ).getParent();
			String targetDir = iLoongApplication.getBackupPath();
			// String sourcepath =
			// iLoongApplication.getInstance().getDatabasePath(DATABASE_NAME).getPath();
			// String targetpath = iLoongApplication.getBackupPath() + "/" +
			// DATABASE_NAME;
			// File sourceFile = new File(sourcepath);
			// File targetFile = new File(targetpath);
			try
			{
				CopyDirectory.copyDirectiory( sourceDir , targetDir );
				// CopyDirectory.copyFile(sourceFile, targetFile);
				Toast.makeText( this , R3D.getString( RR.string.backup_success ) , Toast.LENGTH_SHORT ).show();
			}
			catch( IOException e )
			{
				e.printStackTrace();
				Toast.makeText( this , R3D.getString( RR.string.backup_fail ) , Toast.LENGTH_SHORT ).show();
			}
		}
		
		public boolean restoredb()
		{
			boolean bret = true;
			String targetDir = iLoongApplication.getInstance().getDatabasePath( DATABASE_NAME ).getParent();
			String sourceDir = iLoongApplication.getBackupPath();
			// String targetpath =
			// iLoongApplication.getInstance().getDatabasePath(DATABASE_NAME).getPath();
			// String sourcepath = iLoongApplication.getBackupPath() + "/" +
			// DATABASE_NAME;
			// File sourceFile = new File(sourcepath);
			// File targetFile = new File(targetpath);
			try
			{
				CopyDirectory.copyDirectiory( sourceDir , targetDir );
				// CopyDirectory.copyFile(sourceFile, targetFile);
			}
			catch( IOException e )
			{
				bret = false;
				Toast.makeText( this , R3D.getString( RR.string.restore_fail ) , Toast.LENGTH_SHORT ).show();
				e.printStackTrace();
			}
			return bret;
		}
		
		private void updateDesktop()
		{
			UpdateTask updateTask = new UpdateTask( this );
			if( updateTask.IsHaveInternet( this ) == false )
			{
				Toast.makeText( iLoongLauncher.getInstance() , RR.string.update_err , Toast.LENGTH_SHORT ).show();
			}
			else
			{
				updateTask.checkUpdate( 1 );
			}
		}
		
		public static String getKey(
				int key )
		{
			return SetupMenu.getKey( key );
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
			if( this.isFinishing() )
			{
				SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_DESKTOP_SETTINGS );
			}
			super.onPause();
			//友盟统计
			MobclickAgent.onPause( this );
		}
		
		protected void onStop()
		{
			if( this.isFinishing() )
			{
				SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_DESKTOP_SETTINGS );
			}
			super.onStop();
		}
		
		@Override
		public boolean onPreferenceClick(
				Preference preference )
		{
			String key = preference.getKey();
			if( key == null )
				return false;
			if( key.equals( getKey( RR.string.setting_key_exit ) ) )
			{
				finish();
				iLoongApplication.getInstance().StopServer();
				Message msg = iLoongLauncher.getInstance().mMainHandler.obtainMessage( 7777 );
				iLoongLauncher.getInstance().mMainHandler.sendMessage( msg );
			}
			else if( key.equals( getKey( RR.string.setting_key_restart ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
			}
			else if( key.equals( getKey( RR.string.setting_key_backup ) ) )
			{
				if( iLoongApplication.getSDPath() == null )
				{
					Toast.makeText( this , R3D.getString( RR.string.backup_pls_insert_SD ) , Toast.LENGTH_SHORT ).show();
				}
				else
				{
					backdialog( R3D.getString( RR.string.backup_title_back ) , R3D.getString( RR.string.backup_back_to_SD ) );
				}
			}
			else if( key.equals( getKey( RR.string.setting_key_restore ) ) )
			{
				restoredialog( R3D.getString( RR.string.backup_title_restore ) , R3D.getString( RR.string.backup_restore ) );
			}
			else if( key.equals( getKey( RR.string.setting_key_update ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_UPDATE );
			}
			else if( key.equals( getKey( RR.string.setting_key_share ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_SHARE );
			}
			else if( key.equals( getKey( RR.string.setting_key_feedback ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_FEEDBACK );
			}
			else if( key.equals( getKey( RR.string.setting_key_desktopeffects ) ) || key.equals( getKey( RR.string.setting_key_appeffects ) ) || key.equals( getKey( RR.string.setting_key_vibrator ) ) || key
					.equals( getKey( RR.string.setting_key_circled ) ) || key.equals( getKey( RR.string.setting_key_shake_wallpaper ) ) || key.equals( getKey( RR.string.icon_size_key ) ) || ( key
					.equals( getKey( RR.string.setting_key_sensor ) ) && ( DefaultLayout.show_sensor ) )// xiatian
																										// add
																										// start
																										// //Widget3D
																										// adaptation
																										// "Naked eye 3D"
					|| key.equals( getKey( RR.string.setting_key_particle ) ) // added by zhenNan.ye
					|| key.equals( getKey( RR.string.icon_size_key ) ) //xiatian add	//Mainmenu Bg
			)
			{
				mKey.add( key );
			}
			else if( key.equals( getKey( RR.string.setting_key_installhelp ) ) )
			{
				// mKey.add(key);
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_INSTALL_HELP );
			}
			else if( key.equals( getKey( RR.string.setting_key_resetwizard ) ) )
			{
				resetClingdialog( R3D.getString( RR.string.guide_title ) , R3D.getString( RR.string.guide_proceed ) );
			}
			else if( key.equals( getKey( RR.string.setting_update_desktop ) ) )
			{
				updateDesktop();
			}
			else if( key.equals( getKey( RR.string.setting_key_rate ) ) )
			{
				rate();
			}
			return false;
		}
		
		@Override
		public boolean onPreferenceChange(
				Preference preference ,
				Object newValue )
		{
			Log.v( "preference" , "preference=" + preference + ";;newValue=" + newValue );
			String desktopkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_desktopeffects );
			String appkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			String iconSizeKey = SetupMenu.getContext().getResources().getString( RR.string.icon_size_key );
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			String sensorkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_sensor );
			// xiatian add end
			String mainmenu_bg_key = SetupMenu.getContext().getResources().getString( RR.string.mainmenu_bg_key ); //xiatian add	//Mainmenu Bg			
			if( preference.getKey().equals( desktopkey ) || preference.getKey().equals( appkey ) || preference.getKey().equals( iconSizeKey ) || preference.getKey().equals( mainmenu_bg_key ) //xiatian add	//Mainmenu Bg
			)
			{
				ListPreference pf = (ListPreference)preference;
				CharSequence[] summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( (String)newValue )] );
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			else if( preference.getKey().equals( sensorkey ) && ( DefaultLayout.show_sensor ) )
			{
				if( !iLoongLauncher.getInstance().isPhoneSupportSensor() )
				{
					Toast.makeText( iLoongLauncher.getInstance() , SetupMenu.getContext().getResources().getString( RR.string.sensor_not_supported ) , Toast.LENGTH_SHORT ).show();
					return false;
				}
			}
			// xiatian add end
			return true;
		}
		
		private void rate()
		{
			Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://play.google.com/store/apps/details?id=com.cooeecomet.launcher" ) );
			intent.addCategory( Intent.CATEGORY_BROWSABLE );
			iLoongLauncher.getInstance().startActivity( intent );
		}
	}
}
