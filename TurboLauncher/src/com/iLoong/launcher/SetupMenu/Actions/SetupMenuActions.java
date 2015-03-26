package com.iLoong.launcher.SetupMenu.Actions;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.pub.provider.PubProviderHelper;


public class SetupMenuActions
{
	
	private HashMap<Integer , ArrayList<MenuActionListener>> mActionListeners = new HashMap<Integer , ArrayList<MenuActionListener>>();
	private HashMap<Integer , Action> mActions = new HashMap<Integer , Action>();
	private static SetupMenuActions mInstance = null;
	private Context mContext;
	
	public static synchronized SetupMenuActions getInstance()
	{
		if( mInstance == null )
			mInstance = new SetupMenuActions();
		return mInstance;
	}
	
	public void init(
			Context context )
	{
		mContext = context;
		SystemAction.Init();
		DesktopAction.Init();
		EffectAction.Init();
		StatusBarAction.Init();
		ThemeAction.Init();
		SystemPlugAction.Init();
		UpdateAction.Init();
		FeedBackAction.Init();
		BackupDesktopAction.Init();
		LockEditAction.Init();
		ScreenEditAction.Init();
		UpdateFeedbackAction.Init();
		InstallHelpAction.Init();
		ResetClingAction.Init();
		LockerSettingAction.Init();
		//Jone add start
		DesktopEffect.Init();
		SuggestFeedback.Init();
		ShowDesktop.Init();
		MoreAction.Init();
		ShowDesktop.Init();
		//Jone end
	}
	
	public synchronized void RegisterAction(
			int actionid ,
			Action action )
	{
		Integer key = Integer.valueOf( actionid );
		Action act = mActions.get( key );
		if( act == null )
		{
			mActions.put( key , action );
		}
	}
	
	public synchronized void RegisterListener(
			int actionid ,
			MenuActionListener ml )
	{
		Integer key = Integer.valueOf( actionid );
		ArrayList<MenuActionListener> mls = mActionListeners.get( key );
		if( mls == null )
		{
			mls = new ArrayList<MenuActionListener>();
			mls.add( ml );
			mActionListeners.put( key , mls );
		}
		else
		{
			mls.add( ml );
		}
	}
	
	public synchronized void UnRegisterListener(
			int actionid ,
			MenuActionListener ml )
	{
		Integer key = Integer.valueOf( actionid );
		if( mActionListeners != null )
		{
			mActionListeners.remove( key );
		}
	}
	
	public void Handle(
			int actionid )
	{
		Integer key = Integer.valueOf( actionid );
		Action action = mActions.get( key );
		if( action != null )
		{
			action.RunAction();
		}
	}
	
	private void NotifyListeners(
			int actionid ,
			Action act )
	{
		Integer key = Integer.valueOf( actionid );
		ArrayList<MenuActionListener> mls = mActionListeners.get( key );
		if( mls != null )
		{
			for( int i = 0 ; i < mls.size() ; i++ )
			{
				mls.get( i ).OnAction( actionid , act.getBundle() );
			}
		}
	}
	
	public// xiatian add //add 3 virtueIcon
	Context getContext()
	{
		return mContext;
	}
	
	public void ActivityFinish(
			int requestCode )
	{
		Integer key = Integer.valueOf( requestCode );
		Action action = mActions.get( key );
		if( action != null )
		{
			action.ActionFinish();
			NotifyListeners( requestCode , action );
			action.Clear();
		}
	}
	
	public Action getAction(
			int actionid )
	{
		Integer key = Integer.valueOf( actionid );
		Action action = mActions.get( key );
		return action;
	}
	
	public int getIntger(
			String key )
	{
		return PreferenceManager.getDefaultSharedPreferences( mContext ).getInt( key , 0 );
	}
	
	public String getString(
			String key )
	{
		return PreferenceManager.getDefaultSharedPreferences( mContext ).getString( key , null );
	}
	
	public int getStringToIntger(
			String key )
	{
		int val = 0;
		if( key.equals( SetupMenu.getKey( RR.string.setting_key_vibrator ) ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) ? 1 : 0;
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_circled ) ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_circled_state ) ? 1 : 0;
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_shake_wallpaper ) ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_open_shake_wallpaper ) ? 1 : 0;
		// teapotXu add start for shake changing theme
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_shake_theme ) ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_open_shake_theme ) ? 1 : 0;
		// teapotXu add end
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_sensor ) ) && ( DefaultLayout.show_sensor ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) ? 1 : 0;
		// xiatian add end
		/************** added by zhenNan.ye begin *******************/
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_particle ) ) && DefaultLayout.enable_particle )
		{
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , DefaultLayout.default_particle_settings_value ) ? 1 : 0;
		}
		/************** added by zhenNan.ye end *******************/
		else
		{
			val = Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getString( key , "-1" ) ).intValue();
			// teapotXu add start:解决新增的特效在某些语言中没有添加，导致获取type时越界出现重启的问题
			int value_max = -1;
			if( key.equals( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) )
			{
				//String[] value = iLoongLauncher.getInstance().getResources().getStringArray(RR.array.app_effectsvalue_list_preference);
				String[] value = R3D.app_list_string;
				value_max = value.length - 1;
				if( val >= value.length )
				{
					val = -1;
				}
			}
			else if( key.equals( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) )
			{
				//String[] value = iLoongLauncher.getInstance().getResources().getStringArray(RR.array.workspace_effectsvalue_list_preference);
				String[] value = R3D.workSpace_list_string;
				value_max = value.length - 1;
				if( val >= value.length )
				{
					val = -1;
				}
			}
			if( val == -1 )
			{
				if( key.equals( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) )
				{
					if( DefaultLayout.mainmenu_page_effect_id > value_max )
					{
						val = 0;
					}
					else
					{
						val = DefaultLayout.mainmenu_page_effect_id;
					}
				}
				else if( key.equals( SetupMenu.getKey( RR.string.setting_key_new_particle ) ) )
				{
					//甩墙纸
					val = 0;
				}
				else
				{
					if( DefaultLayout.desktop_page_effect_id > value_max )
					{
						//默认
						val = 0;
					}
					else
					{
						val = DefaultLayout.desktop_page_effect_id;
					}
				}
				PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putString( key , val + "" ).commit();
				PubProviderHelper.addOrUpdateValue( "effect" , key , String.valueOf( val ) );
				SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
			}
		}
		return val;
	}
	
	public boolean getBoolean(
			String key )
	{
		boolean val = false;
		if( DefaultLayout.enable_edit_mode_function && key.equals( SetupMenu.getKey( RR.string.setting_key_edit_mode ) ) )
		{
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false );
		}
		else if( RR.net_version )
		{
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false );
		}
		return val;
	}
}
