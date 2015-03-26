package com.iLoong.launcher.SetupMenu.Actions;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;


public class UpdateFeedbackAction extends Action
{
	
	public UpdateFeedbackAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , UpdateFeedbackSettingActivity.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_UPDATE_FEEDBACK , new UpdateFeedbackAction( ActionSetting.ACTION_UPDATE_FEEDBACK , UpdateFeedbackAction.class.getName() ) );
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
	}
	
	public static class UpdateFeedbackSettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener
	{
		
		@Override
		public void onCreate(
				Bundle savedInstanceState )
		{
			super.onCreate( savedInstanceState );
			addPreferencesFromResource( RR.xml.updatefeedbacksetting );
			findPreference( SetupMenu.getKey( RR.string.setting_key_update ) ).setOnPreferenceClickListener( this );
			findPreference( SetupMenu.getKey( RR.string.setting_key_share ) ).setOnPreferenceClickListener( this );
			findPreference( SetupMenu.getKey( RR.string.setting_key_feedback ) ).setOnPreferenceClickListener( this );
		}
		
		protected void onStop()
		{
			super.onStop();
		}
		
		@Override
		public boolean onPreferenceClick(
				Preference preference )
		{
			String key = preference.getKey();
			if( key.equals( SetupMenu.getKey( RR.string.setting_key_update ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_UPDATE );
			}
			else if( key.equals( SetupMenu.getKey( RR.string.setting_key_share ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_SHARE );
			}
			else if( key.equals( SetupMenu.getKey( RR.string.setting_key_feedback ) ) )
			{
				finish();
				SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_FEEDBACK );
			}
			return false;
		}
	}
}
