package com.iLoong.launcher.SetupMenu.Actions;


import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.SetupMenu;


public class DesktopSettingManualCtrlActivity extends PreferenceActivity implements OnPreferenceChangeListener , OnPreferenceClickListener
{
	
	@Override
	public void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		int androidSDKVersion = Integer.parseInt( VERSION.SDK );
		if( DefaultLayout.enable_desktopsettings_menu_style )
		{
			if( androidSDKVersion < 14 )// less than 4.0.2 version
			{
				addPreferencesFromResource( RR.xml.desktop_settings_manual_control_lowversion );
			}
			else
			{
				addPreferencesFromResource( RR.xml.desktop_settings_manual_control );
			}
		}
		else
		{
			addPreferencesFromResource( RR.xml.desktop_settings_manual_control_lowversion );
		}
		Resources res = SetupMenu.getContext().getResources();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
		boolean autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_circled ) , DefaultLayout.default_circled_state );
		PreferenceScreen mPrefScreen = (PreferenceScreen)this.findPreference( res.getString( RR.string.desktop_settins_manual_control ) );
		if( DefaultLayout.enable_desktopsettings_menu_style && androidSDKVersion > 14 )
		{
			SwitchPreferenceUserDefined checkBoxPrefs = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_circled ) );
			if( DefaultLayout.disable_circled )
			{
				if( checkBoxPrefs != null )
					mPrefScreen.removePreference( checkBoxPrefs );
			}
			else
			{
				if( autoStart )
				{
					checkBoxPrefs.setChecked( autoStart );
				}
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_wallpaper ) , DefaultLayout.default_open_shake_wallpaper );
			SwitchPreferenceUserDefined shake_wallpaper = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) );
			if( DefaultLayout.disable_shake_wallpaper )
			{
				if( shake_wallpaper != null )
					mPrefScreen.removePreference( shake_wallpaper );
			}
			else
			{
				if( autoStart )
				{
					shake_wallpaper.setChecked( autoStart );
				}
				shake_wallpaper.setOnPreferenceChangeListener( this );
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_theme ) , DefaultLayout.default_open_shake_theme );
			SwitchPreferenceUserDefined shake_theme = (SwitchPreferenceUserDefined)findPreference( res.getString( RR.string.setting_key_shake_theme ) );
			if( DefaultLayout.disable_shake_change_theme )
			{
				if( shake_theme != null )
					mPrefScreen.removePreference( shake_theme );
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
		else
		{
			CheckBoxPreference checkBoxPrefs = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_circled ) );
			if( DefaultLayout.disable_circled )
			{
				if( checkBoxPrefs != null )
					mPrefScreen.removePreference( checkBoxPrefs );
			}
			else
			{
				if( autoStart )
				{
					checkBoxPrefs.setChecked( autoStart );
				}
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_wallpaper ) , DefaultLayout.default_open_shake_wallpaper );
			CheckBoxPreference shake_wallpaper = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) );
			if( DefaultLayout.disable_shake_wallpaper )
			{
				if( shake_wallpaper != null )
					mPrefScreen.removePreference( shake_wallpaper );
			}
			else
			{
				if( autoStart )
				{
					shake_wallpaper.setChecked( autoStart );
				}
				shake_wallpaper.setOnPreferenceChangeListener( this );
			}
			autoStart = prefs.getBoolean( res.getString( RR.string.setting_key_shake_theme ) , DefaultLayout.default_open_shake_theme );
			CheckBoxPreference shake_theme = (CheckBoxPreference)findPreference( res.getString( RR.string.setting_key_shake_theme ) );
			if( DefaultLayout.disable_shake_change_theme )
			{
				if( shake_theme != null )
					mPrefScreen.removePreference( shake_theme );
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
		//callbacks
		if( findPreference( res.getString( RR.string.setting_key_circled ) ) != null )
		{
			findPreference( res.getString( RR.string.setting_key_circled ) ).setOnPreferenceClickListener( this );
		}
		if( findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) ) != null )
		{
			findPreference( res.getString( RR.string.setting_key_shake_wallpaper ) ).setOnPreferenceClickListener( this );
		}
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
	
	@Override
	public boolean onPreferenceClick(
			Preference preference )
	{
		// TODO Auto-generated method stub
		String key = preference.getKey();
		Resources res = SetupMenu.getContext().getResources();
		if( key == null )
			return false;
		if( key.equals( res.getString( RR.string.setting_key_circled ) ) || key.equals( res.getString( RR.string.setting_key_shake_wallpaper ) ) )
		{
			DesktopAction.getInstance().mKey.add( key );
		}
		return false;
	}
	
	@Override
	protected void onPause()
	{
		if( this.isFinishing() )
		{
			SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_DESKTOP_SETTINGS );
		}
		super.onPause();
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
	public boolean onPreferenceChange(
			Preference preference ,
			Object newValue )
	{
		// TODO Auto-generated method stub
		int androidSDKVersion = Integer.parseInt( VERSION.SDK );
		String shakeWallpaperKey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_shake_wallpaper );
		String shakeThemeKey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_shake_theme );
		if( preference.getKey().equals( shakeWallpaperKey ) )
		{
			//boolean check_state = (boolean)newValue;
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
		return true;
	}
}
