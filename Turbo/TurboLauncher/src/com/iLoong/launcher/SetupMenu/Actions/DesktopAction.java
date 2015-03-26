package com.iLoong.launcher.SetupMenu.Actions;


import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.CopyDirectory;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.FirstActivity;
import com.iLoong.launcher.UI3DEngine.ParticleLoader;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.update.UpdateTask;


public class DesktopAction extends Action
{
	
	private static final String DATABASE_NAME = "launcher.db";
	private static DesktopAction mInstance = null;
	
	public DesktopAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		//		putIntentAction( SetupMenu.getContext() , DesktopSettingActivity.class );
		putIntentAction( SetupMenu.getContext() , FirstActivity.class );
	}
	
	public static DesktopAction getInstance()
	{
		return mInstance;
	}
	
	public static void Init()
	{
		mInstance = new DesktopAction( ActionSetting.ACTION_DESKTOP_SETTINGS , DesktopAction.class.getName() );
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_DESKTOP_SETTINGS , mInstance );
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
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_new_particle ) ) )
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
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_open_shake_wallpaper ) );
			// teapotXu add start
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_shake_theme ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_open_shake_theme ) );
			// teapotXu add end
			// xujia add start
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.icon_size_key ) ) )
		{
			int icon_size_type = Integer.parseInt( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "1" ) );
			mBundle.putString( key , "" + icon_size_type );
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
		}
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_sensor ) ) && ( DefaultLayout.show_sensor ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_open_sensor ) );
			// xiatian add end
		}
		else if( key.equals( DesktopSettingActivity // added by zhenNan.ye
													// begin
				.getKey( RR.string.setting_key_particle ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , ParticleManager.particleManagerEnable ) );
		} // added by zhenNan.ye end
			// xiatian add start //Mainmenu Bg
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.mainmenu_bg_key ) ) )
		{
			mBundle.putString( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "-1" ) );
		}
		// xiatian add end
		// teapotXu add start for add menu
		else if( DefaultLayout.workspace_npages_circle_scroll_config && key.equals( DesktopSettingActivity.getKey( RR.string.screen_scroll_circle ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) );
		}
		else if( DefaultLayout.enable_configmenu_for_move_wallpaper && key.equals( DesktopSettingActivity.getKey( RR.string.desktop_wallpaper_mv ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) );
		}
		// teapotXu add end
		// zjp
		else if( DefaultLayout.enable_edit_mode_function && key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_edit_mode ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false ) );
		}
		// jbc add
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_music_page ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.show_music_page_config_default_value ) );
		}
		// xujin
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_camera_page ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.show_camera_page_config_default_value ) );
		}// xujin
		else if( key.equals( DesktopSettingActivity.getKey( RR.string.setting_key_news_page ) ) )
		{
			mBundle.putBoolean( key , PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.show_news_page_config_default_value ) );
		}
	}
	
	//xujia add start
	public static DesktopSettingActivity instanceDesktopSettingActivity;
	
	//xujia add end
	public static class DesktopSettingActivity extends PreferenceActivity implements OnPreferenceChangeListener , Preference.OnPreferenceClickListener
	{
		
		// xujia add start
		private static final String SINGLEHANDLERACTION = "com.cooee.receiver.PrefrenceFlagReceiver";
		
		// xujia add end
		@Override
		public void onCreate(
				Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			//xujia add start
			instanceDesktopSettingActivity = this;
			//xujia add end
			// teapotXu add start for settingMenu style
			if( DefaultLayout.enable_desktopsettings_menu_style )
			{
				int androidSDKVersion = Integer.parseInt( VERSION.SDK );
				if( androidSDKVersion >= 14 )
				{// higher than 4.0.2 version
					addPreferencesFromResource( RR.xml.desktop_settings_switcher_version );
					showitem_switcher_version( 0 );// 0: it is init shown
				}
				else
				{
					addPreferencesFromResource( RR.xml.desktopsetting );
					showitem( 0 ); // 0: it is init shown
				}
			}
			else
			{
				addPreferencesFromResource( RR.xml.desktopsetting );
				showitem( 0 ); // 0: it is init shown
			}
			// teapotXu add end
			callback();
		}
		
		private void callback()
		{
			if( findPreference( getKey( RR.string.setting_key_desktopeffects ) ) != null )
				findPreference( getKey( RR.string.setting_key_desktopeffects ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_appeffects ) ) != null )
				findPreference( getKey( RR.string.setting_key_appeffects ) ).setOnPreferenceClickListener( this );
			if( findPreference( getKey( RR.string.setting_key_new_particle ) ) != null )
				findPreference( getKey( RR.string.setting_key_new_particle ) ).setOnPreferenceClickListener( this );
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
			if( findPreference( getKey( RR.string.setting_key_shake_theme ) ) != null )
				findPreference( getKey( RR.string.setting_key_shake_theme ) ).setOnPreferenceClickListener( this );
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
			/************** added by zhenNan.ye begin *******************/
			if( findPreference( getKey( RR.string.setting_key_particle ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_particle ) ).setOnPreferenceClickListener( this );
			}
			/************** added by zhenNan.ye end *******************/
			// xiatian add start //Mainmenu Bg
			if( findPreference( getKey( RR.string.mainmenu_bg_key ) ) != null )
			{
				findPreference( getKey( RR.string.mainmenu_bg_key ) ).setOnPreferenceClickListener( this );
			}
			// xiatian add end
			// teapotXu add start
			if( DefaultLayout.workspace_npages_circle_scroll_config == true )
			{
				if( findPreference( getKey( RR.string.screen_scroll_circle ) ) != null )
				{
					findPreference( getKey( RR.string.screen_scroll_circle ) ).setOnPreferenceClickListener( this );
				}
			}
			if( DefaultLayout.enable_configmenu_for_move_wallpaper == true )
			{
				if( findPreference( getKey( RR.string.desktop_wallpaper_mv ) ) != null )
				{
					findPreference( getKey( RR.string.desktop_wallpaper_mv ) ).setOnPreferenceClickListener( this );
				}
			}
			if( DefaultLayout.enable_doov_spec_customization == true )
			{
				if( findPreference( getKey( RR.string.desktop_manual_control ) ) != null )
				{
					findPreference( getKey( RR.string.desktop_manual_control ) ).setOnPreferenceClickListener( this );
				}
			}
			// teapotXu add end
			// zjp
			if( DefaultLayout.enable_edit_mode_function && findPreference( getKey( RR.string.setting_key_edit_mode ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_edit_mode ) ).setOnPreferenceClickListener( this );
			}
			// jbc add
			if( findPreference( getKey( RR.string.setting_key_music_page ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_music_page ) ).setOnPreferenceClickListener( this );
			}
			// xujin add camera click event
			if( findPreference( getKey( RR.string.setting_key_camera_page ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_camera_page ) ).setOnPreferenceClickListener( this );
			}
			// xujin add news click event
			if( findPreference( getKey( RR.string.setting_key_news_page ) ) != null )
			{
				findPreference( getKey( RR.string.setting_key_news_page ) ).setOnPreferenceClickListener( this );
			}
			if( RR.net_version && DefaultLayout.show_roll_dockbar_checkbox )
			{
				if( findPreference( getKey( RR.string.setting_key_roll_dockbar ) ) != null )
					findPreference( getKey( RR.string.setting_key_roll_dockbar ) ).setOnPreferenceClickListener( this );
			}
			// xujia add start
			if( DefaultLayout.enable_singleHandler )
			{
				if( findPreference( getKey( RR.string.setting_single_handler_flag ) ) != null )
				{
					findPreference( getKey( RR.string.setting_single_handler_flag ) ).setOnPreferenceChangeListener( this );
				}
			}
			else
			{
				if( ( (PreferenceGroup)findPreference( getResources().getString( RR.string.setting_key_basic_setting ) ) ) != null && findPreference( getKey( RR.string.setting_single_handler_flag ) ) != null )
				{
					( (PreferenceGroup)findPreference( getResources().getString( RR.string.setting_key_basic_setting ) ) )
							.removePreference( findPreference( getKey( RR.string.setting_single_handler_flag ) ) );
				}
			}
			// xujia add end
		}
		
		private void showitem(
				int show_type )
		{
			Resources res = SetupMenu.getContext().getResources();
			String key = res.getString( RR.string.setting_key_desktopeffects );
			ListPreference pf = (ListPreference)findPreference( key );
			pf.setOnPreferenceChangeListener( this );
			CharSequence[] summarys;
			String language = Locale.getDefault().getLanguage();
			// if(DefaultLayout.page_effect_no_radom_style && show_type == 0){
			// CharSequence[] entries_no_radom = null;
			// CharSequence[] enteryValue_no_radom = null;
			//
			// entries_no_radom = getListContent_no_radom(pf,0);
			// enteryValue_no_radom = getListContent_no_radom(pf,1);
			//
			// if(entries_no_radom != null)
			// pf.setEntries(entries_no_radom);
			//
			// if(enteryValue_no_radom != null)
			// pf.setEntryValues(enteryValue_no_radom);
			//
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }else if(DefaultLayout.disable_crystal_effect_in_workspace &&
			// show_type == 0 &&
			// (language!=null&&(language.equals("en")||language.equals("zh")))){
			//
			// CharSequence[] entries_original = pf.getEntries();
			// CharSequence[] entries_no_crystal = new
			// CharSequence[entries_original.length - 1];
			// for(int i=0; i<entries_original.length -1; i++){
			// entries_no_crystal[i] = entries_original[i];
			// }
			//
			// CharSequence[] enteryValue_original = pf.getEntryValues();
			// CharSequence[] enteryValue_no_crystal = new
			// CharSequence[enteryValue_original.length - 1];
			// for(int i=0; i<enteryValue_original.length -1; i++){
			// enteryValue_no_crystal[i] = enteryValue_original[i];
			// }
			//
			// if(entries_no_crystal != null)
			// pf.setEntries(entries_no_crystal);
			//
			// if(enteryValue_original != null)
			// pf.setEntryValues(enteryValue_no_crystal);
			//
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }else{
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }
			{
				CharSequence[] enteries = new CharSequence[R3D.workSpace_list_string.length];
				CharSequence[] enteryValue = new CharSequence[R3D.workSpace_list_string.length];
				for( int i = 0 ; i < R3D.workSpace_list_string.length ; i++ )
				{
					enteries[i] = R3D.workSpace_list_string[i];
					enteryValue[i] = "" + i;
				}
				pf.setEntries( enteries );
				pf.setEntryValues( enteryValue );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			pf = (ListPreference)findPreference( key );
			pf.setOnPreferenceChangeListener( this );
			// if(DefaultLayout.page_effect_no_radom_style && show_type == 0){
			//
			// CharSequence[] entries_no_radom = null;
			// CharSequence[] enteryValue_no_radom = null;
			//
			// entries_no_radom = getListContent_no_radom(pf,0);
			// enteryValue_no_radom = getListContent_no_radom(pf,1);
			//
			// if(entries_no_radom != null)
			// pf.setEntries(entries_no_radom);
			//
			// if(enteryValue_no_radom != null)
			// pf.setEntryValues(enteryValue_no_radom);
			//
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "1"))]);
			//
			// }else{
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			//
			// }
			{
				CharSequence[] enteries = new CharSequence[R3D.app_list_string.length];
				CharSequence[] enteryValue = new CharSequence[R3D.app_list_string.length];
				for( int i = 0 ; i < R3D.app_list_string.length ; i++ )
				{
					enteries[i] = R3D.app_list_string[i];
					enteryValue[i] = "" + i;
				}
				pf.setEntries( enteries );
				pf.setEntryValues( enteryValue );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_new_particle );
			pf = (ListPreference)findPreference( key );
			if( DefaultLayout.enable_new_particle )
			{
				pf.setOnPreferenceChangeListener( this );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			else
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_effect_setting ) );
				if( pf != null )
					category.removePreference( pf );
			}
			// Jone add start
			SharedPreferences rollPrefs = PreferenceManager.getDefaultSharedPreferences( this );
			boolean enableRolling = rollPrefs.getBoolean( res.getString( RR.string.setting_key_roll_dockbar ) , false );
			CheckBoxPreference rollDockbar = (CheckBoxPreference)this.findPreference( res.getString( RR.string.setting_key_roll_dockbar ) );
			if( !DefaultLayout.show_roll_dockbar_checkbox )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( rollDockbar != null )
				{
					category.removePreference( rollDockbar );
				}
			}
			else
			{
				if( rollDockbar != null )
					rollDockbar.setChecked( enableRolling );
			}
			// Jone add end
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
			boolean autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_vibrator ) , true );
			CheckBoxPreference vibratorpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_vibrator ) );
			// teapotXu add start for doov special customization
			if( DefaultLayout.enable_doov_spec_customization == true )
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
			// teapotXu add end
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_circled ) , DefaultLayout.default_circled_state );
			vibratorpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_circled ) );
			if( DefaultLayout.disable_circled )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( vibratorpf != null )
					category.removePreference( vibratorpf );
			}
			else
			{
				// teapotXu add start for doov special customization
				if( DefaultLayout.enable_doov_spec_customization == true )
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
					vibratorpf.setOnPreferenceChangeListener( this );// xiatian
																		// add
																		// //Introduction
				}
				// teapotXu add end
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_wallpaper ) , DefaultLayout.default_open_shake_wallpaper );
			CheckBoxPreference shake_wallpaper = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) );
			if( DefaultLayout.disable_shake_wallpaper )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( shake_wallpaper != null )
					category.removePreference( shake_wallpaper );
			}
			else
			{
				// teapotXu add start for doov special customization
				if( DefaultLayout.enable_doov_spec_customization == true )
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
					shake_wallpaper.setOnPreferenceChangeListener( this );
				}
				// teapotXu add end
			}
			// teapotXu add start
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_theme ) , DefaultLayout.default_open_shake_theme );
			CheckBoxPreference shake_theme = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_shake_theme ) );
			if( DefaultLayout.disable_shake_change_theme )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( shake_theme != null )
					category.removePreference( shake_theme );
			}
			else
			{
				// for doov special customization
				if( DefaultLayout.enable_doov_spec_customization == true )
				{
					PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
					if( shake_theme != null )
						category.removePreference( shake_theme );
				}
				else
				{
					if( shake_wallpaper != null && shake_wallpaper.isChecked() )
					{
						// shake theme should be disable
						if( shake_theme != null )
						{
							shake_theme.setChecked( false );
							shake_theme.setEnabled( false );
						}
					}
					else
					{
						if( autoStart )
						{
							shake_theme.setChecked( autoStart );
						}
						if( shake_wallpaper != null && autoStart == true )
						{
							shake_wallpaper.setChecked( false );
							shake_wallpaper.setEnabled( false );
						}
						shake_theme.setOnPreferenceChangeListener( this );
					}
				}
			}
			// teapotXu add end
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
					if( DefaultLayout.hide_big_icon_size )
					{
						CharSequence[] summarys_2 = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.icon_size_no_middle_display );
						CharSequence[] enteryValue = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.icon_size_no_middle_value );
						pf.setEntries( summarys_2 );
						pf.setEntryValues( enteryValue );
						summarys = pf.getEntries();
						pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) ) - 1] );
					}
					else
					{
						summarys = pf.getEntries();
						pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
					}
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
			/************** added by zhenNan.ye begin *******************/
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_particle ) , DefaultLayout.default_particle_settings_value );
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
			/************** added by zhenNan.ye end *******************/
			// zjp
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_edit_mode ) , false );
			CheckBoxPreference editmodeCbpf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_edit_mode ) );
			if( !DefaultLayout.enable_edit_mode_function || DefaultLayout.huaqin_enable_edit_mode )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( editmodeCbpf != null )
				{
					category.removePreference( editmodeCbpf );
				}
			}
			else
			{
				if( autoStart )
				{
					editmodeCbpf.setChecked( autoStart );
				}
				if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
				{
					editmodeCbpf.setEnabled( false );
				}
			}
			// jbc add start
			CheckBoxPreference musicPageCheckboxPrf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_music_page ) );
			if( !DefaultLayout.show_music_page_enable_config )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_exclusive_pages ) );
				if( musicPageCheckboxPrf != null )
				{
					category.removePreference( musicPageCheckboxPrf );
				}
			}
			else
			{
				autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_music_page ) , DefaultLayout.show_music_page_config_default_value );
				musicPageCheckboxPrf.setChecked( autoStart );
			}
			// jbc add end
			// xujin add start camera checkbox
			CheckBoxPreference cameraPref = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_camera_page ) );
			if( !DefaultLayout.show_camera_page_enable_config )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_exclusive_pages ) );
				if( cameraPref != null )
				{
					category.removePreference( cameraPref );
				}
			}
			else
			{
				autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_camera_page ) , DefaultLayout.show_camera_page_config_default_value );
				cameraPref.setChecked( autoStart );
			}
			// news checkbox
			CheckBoxPreference newsPref = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_news_page ) );
			if( !DefaultLayout.show_news_page_enable_config )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_exclusive_pages ) );
				if( newsPref != null )
				{
					category.removePreference( newsPref );
				}
			}
			else
			{
				autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_news_page ) , DefaultLayout.show_news_page_config_default_value );
				newsPref.setChecked( autoStart );
			}
			// xujin add end
			if( ( !DefaultLayout.show_news_page_enable_config && !DefaultLayout.show_music_page_enable_config && !DefaultLayout.show_camera_page_enable_config ) || ( !( !DefaultLayout.show_news_page_enable_config || DefaultLayout.show_music_page_enable_config || DefaultLayout.show_camera_page_enable_config ) ) )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_exclusive_pages ) );
				if( ( category != null ) && category.getPreferenceCount() == 0 )
				{
					PreferenceScreen screen = (PreferenceScreen)this.findPreference( res.getString( RR.string.setting_key_desktop_setting ) );
					screen.removePreference( category );
				}
			}
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
				if( !DefaultLayout.enable_auto_update )
				{
					PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_backup_and_restore ) );
					Preference autoUpdate = category.findPreference( res.getString( RR.string.setting_update_desktop ) );
					if( autoUpdate != null )
						category.removePreference( autoUpdate );
				}
			}
			// xiatian add start //Mainmenu Bg
			key = res.getString( RR.string.mainmenu_bg_key );
			pf = (ListPreference)findPreference( key );
			// teapotXu add start for doov special customization
			if( ( DefaultLayout.enable_doov_spec_customization == true ) || ( DefaultLayout.mainmenu_background_alpha_progress )// xiatian
			// add
			// //mainmenu_background_alpha_progress
			)
			{
				if( pf != null )
				{
					PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_effect_setting ) );
					category.removePreference( pf );
				}
			}
			else
			{
				if( pf != null )
				{
					pf.setOnPreferenceChangeListener( this );
					summarys = pf.getEntries();
					pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , DefaultLayout.defaultMainmenuBgIndex + "" ) )] );
				}
			}
			// teapotXu add end
			// xiatian add end
			// teapotXu add start
			PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
			if( DefaultLayout.workspace_npages_circle_scroll_config )
			{
				CheckBoxPreference mCheckPrefernce = (CheckBoxPreference)findPreference( res.getString( RR.string.screen_scroll_circle ) );
				boolean checkstate = prefs.getBoolean( res.getString( RR.string.screen_scroll_circle ) , true );
				if( mCheckPrefernce != null )
				{
					mCheckPrefernce.setChecked( checkstate );
				}
			}
			else
			{
				CheckBoxPreference mCheckPrefernce = (CheckBoxPreference)findPreference( res.getString( RR.string.screen_scroll_circle ) );
				if( mCheckPrefernce != null )
				{
					category.removePreference( mCheckPrefernce );
				}
			}
			if( DefaultLayout.workspace_npages_circle_scroll_config )
			{
				CheckBoxPreference mCheckPrefernce = (CheckBoxPreference)findPreference( res.getString( RR.string.desktop_wallpaper_mv ) );
				boolean checkstate = prefs.getBoolean( res.getString( RR.string.desktop_wallpaper_mv ) , true );
				if( mCheckPrefernce != null )
				{
					mCheckPrefernce.setChecked( checkstate );
				}
			}
			else
			{
				CheckBoxPreference mCheckPrefernce = (CheckBoxPreference)findPreference( res.getString( RR.string.desktop_wallpaper_mv ) );
				if( mCheckPrefernce != null )
				{
					category.removePreference( mCheckPrefernce );
				}
			}
			// for doov special customization
			if( !DefaultLayout.enable_doov_spec_customization )
			{
				Preference mPreference = findPreference( res.getString( RR.string.desktop_manual_control ) );
				if( mPreference != null )
				{
					category.removePreference( mPreference );
				}
			}
			// teapotXu add end
		}
		
		// teapotXu add start for enable desktop settings menu style
		private void showitem_switcher_version(
				int show_type )
		{
			Resources res = SetupMenu.getContext().getResources();
			String key = res.getString( RR.string.setting_key_desktopeffects );
			ListPreference pf = (ListPreference)findPreference( key );
			pf.setOnPreferenceChangeListener( this );
			CharSequence[] summarys;
			String language = Locale.getDefault().getLanguage();
			// if(DefaultLayout.page_effect_no_radom_style && show_type == 0){
			// CharSequence[] entries_no_radom = null;
			// CharSequence[] enteryValue_no_radom = null;
			//
			// entries_no_radom = getListContent_no_radom(pf,0);
			// enteryValue_no_radom = getListContent_no_radom(pf,1);
			//
			// if(entries_no_radom != null)
			// pf.setEntries(entries_no_radom);
			//
			// if(enteryValue_no_radom != null)
			// pf.setEntryValues(enteryValue_no_radom);
			//
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }else if(DefaultLayout.disable_crystal_effect_in_workspace &&
			// show_type == 0 &&
			// (language!=null&&(language.equals("en")||language.equals("zh")))){
			//
			// CharSequence[] entries_original = pf.getEntries();
			// CharSequence[] entries_no_crystal = new
			// CharSequence[entries_original.length - 1];
			// for(int i=0; i<entries_original.length -1; i++){
			// entries_no_crystal[i] = entries_original[i];
			// }
			//
			// CharSequence[] enteryValue_original = pf.getEntryValues();
			// CharSequence[] enteryValue_no_crystal = new
			// CharSequence[enteryValue_original.length - 1];
			// for(int i=0; i<enteryValue_original.length -1; i++){
			// enteryValue_no_crystal[i] = enteryValue_original[i];
			// }
			//
			// if(entries_no_crystal != null)
			// pf.setEntries(entries_no_crystal);
			//
			// if(enteryValue_original != null)
			// pf.setEntryValues(enteryValue_no_crystal);
			//
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }else{
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }
			{
				CharSequence[] enteries = new CharSequence[R3D.workSpace_list_string.length];
				CharSequence[] enteryValue = new CharSequence[R3D.workSpace_list_string.length];
				for( int i = 0 ; i < R3D.workSpace_list_string.length ; i++ )
				{
					enteries[i] = R3D.workSpace_list_string[i];
					enteryValue[i] = "" + i;
				}
				pf.setEntries( enteries );
				pf.setEntryValues( enteryValue );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_new_particle );
			pf = (ListPreference)findPreference( key );
			if( DefaultLayout.enable_new_particle )
			{
				pf.setOnPreferenceChangeListener( this );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			else
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_effect_setting ) );
				if( pf != null )
					category.removePreference( pf );
			}
			key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			pf = (ListPreference)findPreference( key );
			pf.setOnPreferenceChangeListener( this );
			key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_new_particle );
			pf = (ListPreference)findPreference( key );
			pf.setOnPreferenceChangeListener( this );
			// if(DefaultLayout.page_effect_no_radom_style && show_type == 0){
			// CharSequence[] entries_no_radom = null;
			// CharSequence[] enteryValue_no_radom = null;
			//
			// entries_no_radom = getListContent_no_radom(pf,0);
			// enteryValue_no_radom = getListContent_no_radom(pf,1);
			//
			// if(entries_no_radom != null)
			// pf.setEntries(entries_no_radom);
			//
			// if(enteryValue_no_radom != null)
			// pf.setEntryValues(enteryValue_no_radom);
			//
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "1"))]);
			// }else{
			// summarys = pf.getEntries();
			// pf.setSummary(summarys[Integer.valueOf(PreferenceManager
			// .getDefaultSharedPreferences(SetupMenu.getContext())
			// .getString(key, "0"))]);
			// }
			{
				CharSequence[] enteries = new CharSequence[R3D.app_list_string.length];
				CharSequence[] enteryValue = new CharSequence[R3D.app_list_string.length];
				for( int i = 0 ; i < R3D.app_list_string.length ; i++ )
				{
					enteries[i] = R3D.app_list_string[i];
					enteryValue[i] = "" + i;
				}
				pf.setEntries( enteries );
				pf.setEntryValues( enteryValue );
				summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
			}
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
			boolean autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_vibrator ) , true );
			SwitchPreferenceUserDefined vibratorpf = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_vibrator ) );
			// for doov special customization
			if( DefaultLayout.enable_doov_spec_customization == true )
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
			// teapotXu add end
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_circled ) , DefaultLayout.default_circled_state );
			vibratorpf = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_circled ) );
			if( DefaultLayout.disable_circled )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( vibratorpf != null )
					category.removePreference( vibratorpf );
			}
			else
			{
				// teapotXu add start
				if( DefaultLayout.enable_doov_spec_customization == true )
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
				// teapotXu add end
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_wallpaper ) , DefaultLayout.default_open_shake_wallpaper );
			SwitchPreferenceUserDefined shake_wallpaper = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) );
			if( DefaultLayout.disable_shake_wallpaper )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( shake_wallpaper != null )
					category.removePreference( shake_wallpaper );
			}
			else
			{
				// teapotXu add start
				if( DefaultLayout.enable_doov_spec_customization == true )
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
					shake_wallpaper.setOnPreferenceChangeListener( this );
				}
				// teapotXu add end
			}
			// teapotXu add start
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_theme ) , DefaultLayout.default_open_shake_theme );
			SwitchPreferenceUserDefined shake_theme = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_shake_theme ) );
			if( DefaultLayout.disable_shake_change_theme )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( shake_theme != null )
					category.removePreference( shake_theme );
			}
			else
			{
				// for doov special customization
				if( DefaultLayout.enable_doov_spec_customization == true )
				{
					PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
					if( shake_theme != null )
						category.removePreference( shake_theme );
				}
				else
				{
					if( shake_wallpaper != null && shake_wallpaper.isChecked() )
					{
						// shake theme should be disable
						if( shake_theme != null )
						{
							shake_theme.setChecked( false );
							shake_theme.setEnabled( false );
						}
					}
					else
					{
						if( autoStart )
						{
							shake_theme.setChecked( autoStart );
						}
						if( shake_wallpaper != null && autoStart == true )
						{
							shake_wallpaper.setChecked( false );
							shake_wallpaper.setEnabled( false );
						}
						shake_theme.setOnPreferenceChangeListener( this );
					}
				}
			}
			// teapotXu add end
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
					if( DefaultLayout.hide_big_icon_size )
					{
						CharSequence[] summarys_2 = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.icon_size_no_middle_display );
						CharSequence[] enteryValue = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.icon_size_no_middle_value );
						pf.setEntries( summarys_2 );
						pf.setEntryValues( enteryValue );
						summarys = pf.getEntries();
						pf.setSummary( summarys[Integer.parseInt( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) ) - 1] );
					}
					else
					{
						summarys = pf.getEntries();
						pf.setSummary( summarys[Integer.parseInt( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) )] );
					}
				}
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_sensor ) , DefaultLayout.default_open_sensor );
			SwitchPreferenceUserDefined sensorcbpf = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_sensor ) );
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
			/************** added by zhenNan.ye begin *******************/
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_particle ) , DefaultLayout.default_particle_settings_value );
			SwitchPreferenceUserDefined particleCbpf = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_particle ) );
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
			/************** added by zhenNan.ye end *******************/
			// zjp
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_edit_mode ) , false );
			SwitchPreferenceUserDefined editmodeCbpf = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_edit_mode ) );
			if( !DefaultLayout.enable_edit_mode_function || DefaultLayout.huaqin_enable_edit_mode )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( editmodeCbpf != null )
				{
					category.removePreference( editmodeCbpf );
				}
			}
			else
			{
				if( autoStart )
				{
					editmodeCbpf.setChecked( autoStart );
				}
				if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
				{
					editmodeCbpf.setEnabled( false );
				}
			}
			// jbc add start
			CheckBoxPreference musicPageCheckboxPrf = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_music_page ) );
			if( !DefaultLayout.show_music_page_enable_config )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( musicPageCheckboxPrf != null )
				{
					category.removePreference( musicPageCheckboxPrf );
				}
			}
			else
			{
				autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_music_page ) , DefaultLayout.show_music_page_config_default_value );
				musicPageCheckboxPrf.setChecked( autoStart );
			}
			// jbc add end
			// xujin add start camera checkbox
			CheckBoxPreference cameraPref = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_camera_page ) );
			if( !DefaultLayout.show_camera_page_enable_config )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( cameraPref != null )
				{
					category.removePreference( cameraPref );
				}
			}
			else
			{
				autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_camera_page ) , DefaultLayout.show_camera_page_config_default_value );
				cameraPref.setChecked( autoStart );
			}
			// news checkbox
			CheckBoxPreference newsPref = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_news_page ) );
			if( !DefaultLayout.show_news_page_enable_config )
			{
				PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
				if( newsPref != null )
				{
					category.removePreference( newsPref );
				}
			}
			else
			{
				autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_news_page ) , DefaultLayout.show_news_page_config_default_value );
				newsPref.setChecked( autoStart );
			}
			// xujin add end
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
			// xiatian add start //Mainmenu Bg
			key = res.getString( RR.string.mainmenu_bg_key );
			pf = (ListPreference)findPreference( key );
			// teapotXu add start
			if( DefaultLayout.enable_doov_spec_customization == true || ( DefaultLayout.mainmenu_background_alpha_progress ) )
			{
				if( pf != null )
				{
					PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_effect_setting ) );
					category.removePreference( pf );
				}
			}
			else
			{
				if( pf != null )
				{
					pf.setOnPreferenceChangeListener( this );
					summarys = pf.getEntries();
					pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , DefaultLayout.defaultMainmenuBgIndex + "" ) )] );
				}
			}
			// teapotXu add end
			// xiatian add end
			// teapotXu add start
			PreferenceCategory category = (PreferenceCategory)this.findPreference( res.getString( RR.string.setting_key_basic_setting ) );
			if( DefaultLayout.workspace_npages_circle_scroll_config )
			{
				SwitchPreferenceUserDefined mCheckPrefernce = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.screen_scroll_circle ) );
				boolean checkstate = prefs.getBoolean( res.getString( RR.string.screen_scroll_circle ) , true );
				if( mCheckPrefernce != null )
				{
					mCheckPrefernce.setChecked( checkstate );
				}
			}
			else
			{
				SwitchPreferenceUserDefined mCheckPrefernce = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.screen_scroll_circle ) );
				if( mCheckPrefernce != null )
				{
					category.removePreference( mCheckPrefernce );
				}
			}
			if( DefaultLayout.workspace_npages_circle_scroll_config )
			{
				SwitchPreferenceUserDefined mCheckPrefernce = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.desktop_wallpaper_mv ) );
				boolean checkstate = prefs.getBoolean( res.getString( RR.string.desktop_wallpaper_mv ) , true );
				if( mCheckPrefernce != null )
				{
					mCheckPrefernce.setChecked( checkstate );
				}
			}
			else
			{
				SwitchPreferenceUserDefined mCheckPrefernce = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.desktop_wallpaper_mv ) );
				if( mCheckPrefernce != null )
				{
					category.removePreference( mCheckPrefernce );
				}
			}
			// for doov special customization
			if( !DefaultLayout.enable_doov_spec_customization )
			{
				Preference mPreference = findPreference( res.getString( RR.string.desktop_manual_control ) );
				if( mPreference != null )
				{
					category.removePreference( mPreference );
				}
			}
			// teapotXu add end
		}
		
		// private CharSequence[] getListContent_no_radom(ListPreference pf, int
		// content_type){
		// if(DefaultLayout.page_effect_no_radom_style){
		//
		// if(content_type == 0){
		// CharSequence[] entries_original = pf.getEntries();
		// CharSequence[] entries_no_radom = new
		// CharSequence[entries_original.length - 1];
		// int indicator = 0;
		// for(int i=0; i<entries_original.length; i++){
		// if(i == 1){
		// indicator = 1;
		// continue;
		// }
		// entries_no_radom[i - indicator] = entries_original[i];
		// }
		//
		// return entries_no_radom;
		//
		// }
		// else if(content_type == 1){
		// CharSequence[] enteryValue_original = pf.getEntryValues();
		//
		// CharSequence[] enteryValue_no_radom = new
		// CharSequence[enteryValue_original.length - 1];
		//
		// int indicator = 0;
		// for(int i=0; i<enteryValue_original.length - 1; i++){
		// enteryValue_no_radom[i] = enteryValue_original[i];
		// }
		//
		// return enteryValue_no_radom;
		//
		// }
		// }
		// return null;
		// }
		private boolean checkBackup()
		{
			boolean bret = false;
			String key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_backup );
			Preference pf = (Preference)findPreference( key );
			File file = new File( iLoongApplication.getBackupPath() + "/databases/" , DATABASE_NAME );
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
					backupdb();
					if( DefaultLayout.enable_desktopsettings_menu_style )
					{
						int androidSDKVersion = Integer.parseInt( VERSION.SDK );
						if( androidSDKVersion >= 14 )
						{// higher than
							// 4.0.2 version
							showitem_switcher_version( 1 );
						}
						else
						{
							showitem( 1 );// 1: it is backup shown
						}
					}
					else
					{
						showitem( 1 );// 1: it is backup shown
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
		
		private void findAllCheckBoxPreferenceThenAddKeys(
				PreferenceGroup preferenceGroup )
		{
			int pref_count = preferenceGroup.getPreferenceCount();
			for( int index = 0 ; index < pref_count ; index++ )
			{
				Preference preference = preferenceGroup.getPreference( index );
				if( preference instanceof PreferenceGroup )
				{
					findAllCheckBoxPreferenceThenAddKeys( (PreferenceGroup)preference );
				}
				else if( preference instanceof CheckBoxPreference )
				{
					String key = preference.getKey();
					if( !mKey.contains( key ) )
						mKey.add( key );
				}
			}
		}
		
		@Override
		protected void onPause()
		{
			// if (this.isFinishing()) {
			SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_DESKTOP_SETTINGS );
			// }
			super.onPause();
		}
		
		protected void onStop()
		{
			// if (this.isFinishing()) {
			// SetupMenuActions.getInstance().ActivityFinish(
			// ActionSetting.ACTION_DESKTOP_SETTINGS);
			// }
			super.onStop();
		}
		
		@Override
		public boolean onPreferenceClick(
				Preference preference )
		{
			String key = preference.getKey();
			if( key == null )
				return false;
			// Jone add
			if( RR.net_version && DefaultLayout.show_roll_dockbar_checkbox )
			{
				String rollDockbar_key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_roll_dockbar );
				if( preference.getKey().equals( rollDockbar_key ) )
				{
					HotSeat3D.enableRollDockbar = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false );
				}
			}
			// Jone end
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
					.equals( getKey( RR.string.setting_key_circled ) ) || key.equals( getKey( RR.string.setting_key_shake_wallpaper ) ) || key.equals( getKey( RR.string.setting_key_shake_theme ) ) || key
					.equals( getKey( RR.string.icon_size_key ) ) || key.equals( getKey( RR.string.setting_key_new_particle ) ) || ( key.equals( getKey( RR.string.setting_key_sensor ) ) && ( DefaultLayout.show_sensor ) )// xiatian
																																																							// add
																																																							// start
																																																							// //Widget3D
																																																							// adaptation
																																																							// "Naked eye 3D"
					|| key.equals( getKey( RR.string.setting_key_particle ) ) // added
																				// by
																				// zhenNan.ye
					|| key.equals( getKey( RR.string.icon_size_key ) ) // xiatian
																		// add
																		// //Mainmenu
																		// Bg
					|| key.equals( getKey( RR.string.setting_key_music_page ) )// jbc
																				// add
					|| key.equals( getKey( RR.string.setting_key_camera_page ) )// xujin
																				// add
					|| key.equals( getKey( RR.string.setting_key_news_page ) )// xujin
																				// add
					|| ( key.equals( getKey( RR.string.screen_scroll_circle ) ) && ( DefaultLayout.workspace_npages_circle_scroll_config ) ) // teapotXu
																																				// added
					|| ( key.equals( getKey( RR.string.desktop_wallpaper_mv ) ) && ( DefaultLayout.enable_configmenu_for_move_wallpaper ) ) // teapotXu
																																			// added
					|| ( DefaultLayout.enable_edit_mode_function && key.equals( getKey( RR.string.setting_key_edit_mode ) ) ) )
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
			return false;
		}
		
		@Override
		public boolean onPreferenceChange(
				Preference preference ,
				Object newValue )
		{
			Log.v( "preference" , "preference=" + preference + "; newValue=" + newValue );
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			String desktopkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_desktopeffects );
			String appkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			String particlekey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_new_particle );
			String iconSizeKey = SetupMenu.getContext().getResources().getString( RR.string.icon_size_key );
			String shakeWallpaperKey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_shake_wallpaper );
			String shakeThemeKey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_shake_theme );
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			String sensorkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_sensor );
			// xiatian add end
			String mainmenu_bg_key = SetupMenu.getContext().getResources().getString( RR.string.mainmenu_bg_key ); // xiatian add
																													// //Mainmenu Bg
			String circled_key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_circled ); // xiatian add
																													// //Introduction
			// Jone add
			String rollDockbar_key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_roll_dockbar );
			if( preference.getKey().equals( rollDockbar_key ) )
			{
				HotSeat3D.enableRollDockbar = (Boolean)newValue;
			}
			// Jone end
			if( preference.getKey().equals( desktopkey ) || preference.getKey().equals( appkey ) || preference.getKey().equals( iconSizeKey ) || preference.getKey().equals( mainmenu_bg_key ) // xiatian
																																																// add
																																																// //Mainmenu
																																																// Bg
			)
			{
				ListPreference pf = (ListPreference)preference;
				if( DefaultLayout.hide_big_icon_size )
				{
					if( preference.getKey().equals( iconSizeKey ) )
					{
						CharSequence[] summarys = pf.getEntries();
						pf.setSummary( summarys[Integer.valueOf( (String)newValue ) - 1] );
						return true;
					}
				}
				CharSequence[] summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( (String)newValue )] );
			}
			if( preference.getKey().equals( particlekey ) )
			{
				ListPreference pf = (ListPreference)preference;
				CharSequence[] summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( (String)newValue )] );
				if( Integer.valueOf( (String)newValue ) == 0 )
				{
					Desktop3DListener.currentParticleType = null;
				}
				else
				{
					Desktop3DListener.currentParticleType = ParticleLoader.NEW_PARTICLE_TYPE[Integer.valueOf( (String)newValue ) - 1];
				}
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			else if( preference.getKey().equals( sensorkey ) && ( DefaultLayout.show_sensor ) )
			{
				if( !DefaultLayout.getInstance().isPhoneSupportSensor() )
				{
					Toast.makeText( iLoongLauncher.getInstance() , SetupMenu.getContext().getResources().getString( RR.string.sensor_not_supported ) , Toast.LENGTH_SHORT ).show();
					return false;
				}
			}
			// xiatian add end
			else if( preference.getKey().equals( shakeWallpaperKey ) )
			{
				// boolean check_state = (boolean)newValue;
				if( DefaultLayout.enable_desktopsettings_menu_style && androidSDKVersion > 14 )
				{
					SwitchPreferenceUserDefined shake_theme = (SwitchPreferenceUserDefined)findPreference( shakeThemeKey );
					if( shake_theme != null )
					{
						boolean check_state = ( (Boolean)newValue );
						if( check_state )
						{
							shake_theme.setChecked( false );
							shake_theme.setEnabled( false );
						}
						else
						{
							shake_theme.setChecked( false );
							shake_theme.setEnabled( true );
						}
					}
				}
				else
				{
					CheckBoxPreference shake_theme = (CheckBoxPreference)findPreference( shakeThemeKey );
					if( shake_theme != null )
					{
						boolean check_state = ( (Boolean)newValue );
						if( check_state )
						{
							shake_theme.setChecked( false );
							shake_theme.setEnabled( false );
						}
						else
						{
							shake_theme.setChecked( false );
							shake_theme.setEnabled( true );
						}
					}
				}
			}
			else if( preference.getKey().equals( shakeThemeKey ) )
			{
				if( DefaultLayout.enable_desktopsettings_menu_style && androidSDKVersion > 14 )
				{
					SwitchPreferenceUserDefined shake_wallpaper = (SwitchPreferenceUserDefined)findPreference( shakeWallpaperKey );
					if( shake_wallpaper != null )
					{
						boolean check_state = ( (Boolean)newValue );
						if( check_state )
						{
							shake_wallpaper.setChecked( false );
							shake_wallpaper.setEnabled( false );
						}
						else
						{
							shake_wallpaper.setChecked( false );
							shake_wallpaper.setEnabled( true );
						}
					}
				}
				else
				{
					CheckBoxPreference shake_wallpaper = (CheckBoxPreference)findPreference( shakeWallpaperKey );
					if( shake_wallpaper != null )
					{
						boolean check_state = ( (Boolean)newValue );
						if( check_state )
						{
							shake_wallpaper.setChecked( false );
							shake_wallpaper.setEnabled( false );
						}
						else
						{
							shake_wallpaper.setChecked( false );
							shake_wallpaper.setEnabled( true );
						}
					}
				}
			}
			// xujia add start
			String singleHandler_key = SetupMenu.getContext().getResources().getString( RR.string.setting_single_handler_flag );
			if( preference.getKey().equals( singleHandler_key ) )
			{
				Intent startSingleHandlerIntent = new Intent();
				startSingleHandlerIntent.setAction( SINGLEHANDLERACTION );
				startSingleHandlerIntent.putExtra( "preference_flag" , (Boolean)newValue );
				DesktopSettingActivity.this.sendBroadcast( startSingleHandlerIntent );
			}
			// xujia add end
			return true;
		}
		
		@Override
		public void setTheme(
				int resid )
		{
			// TODO Auto-generated method stub
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
				super.setTheme( android.R.style.Theme_Holo_Light );
			else
				super.setTheme( resid );
		}
	}
}
