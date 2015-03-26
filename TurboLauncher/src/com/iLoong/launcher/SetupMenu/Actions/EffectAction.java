package com.iLoong.launcher.SetupMenu.Actions;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;


public class EffectAction extends Action
{
	
	public EffectAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , EffectSettingActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_EFFECTS_SETTINGS , new EffectAction( ActionSetting.ACTION_EFFECTS_SETTINGS , EffectAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
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
		if( key.equals( SetupMenu.getContext().getResources().getString( RR.string.setting_key_desktopeffects ) ) || key.equals( SetupMenu.getContext().getResources()
				.getString( RR.string.setting_key_appeffects ) ) )
		{
			mBundle.putInt( key , Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "0" ) ) );
		}
	}
	
	public static class EffectSettingActivity extends PreferenceActivity implements OnPreferenceChangeListener
	{
		
		@Override
		public void onCreate(
				Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( RR.xml.effectsetting );
			//			String key = SetupMenu.getContext().getResources().getString(RR.string.setting_key_desktopeffects);
			//			ListPreference pf = (ListPreference) findPreference(key);
			//			pf.setOnPreferenceChangeListener(this);
			//			CharSequence[] summarys = pf.getEntries();
			//			pf.setSummary(summarys[Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(SetupMenu.getContext()).getString(key, "0"))]);
			String key = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			ListPreference pf = (ListPreference)findPreference( key );
			pf.setOnPreferenceChangeListener( this );
			CharSequence[] summarys = pf.getEntries();
			pf.setSummary( summarys[Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( key , "2" ) )] );
		}
		
		protected void onStop()
		{
			super.onStop();
			SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_EFFECTS_SETTINGS );
		}
		
		@Override
		public boolean onPreferenceTreeClick(
				PreferenceScreen preferenceScreen ,
				Preference preference )
		{
			String key = preference.getKey();
			if( key != null )
			{
				mKey.add( key );
			}
			return false;
		}
		
		@Override
		public boolean onPreferenceChange(
				Preference preference ,
				Object newValue )
		{
			String desktopkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_desktopeffects );
			String appkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
			if( preference.getKey().equals( desktopkey ) || preference.getKey().equals( appkey ) )
			{
				ListPreference pf = (ListPreference)preference;
				CharSequence[] summarys = pf.getEntries();
				pf.setSummary( summarys[Integer.valueOf( (String)newValue )] );
			}
			return true;
		}
	}
}
