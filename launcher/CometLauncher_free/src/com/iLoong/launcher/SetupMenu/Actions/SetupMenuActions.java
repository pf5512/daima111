package com.iLoong.launcher.SetupMenu.Actions;


import java.util.ArrayList;
import java.util.HashMap;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.DesktopAction.DesktopSettingActivity;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Context;
import android.preference.PreferenceManager;


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
		ShareAction.Init();
		UpdateAction.Init();
		FeedBackAction.Init();
		BackupDesktopAction.Init();
		LockEditAction.Init();
		ScreenEditAction.Init();
		UpdateFeedbackAction.Init();
		InstallHelpAction.Init();
		ResetClingAction.Init();
		LockerSettingAction.Init();
		DefaultSceneAction.Init(); // xiatian add //DefaultScene
		// wanghongjian add start //enable_DefaultScene
		ChangeSceneAction.Init();
		SceneMainAction.Init();
		SceneClassicAction.Init();
		SceneSettingAction.Init();
		// wanghongjian add end
		//zqh add ,for test
		DesktopEffect.Init();
		SuggestFeedback.Init();
		ShowDesktop.Init();
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
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false ) ? 1 : 0;
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_shake_wallpaper ) ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , false ) ? 1 : 0;
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_sensor ) ) && ( DefaultLayout.show_sensor ) )
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) ? 1 : 0;
		// xiatian add end
		/************** added by zhenNan.ye begin *******************/
		else if( key.equals( SetupMenu.getKey( RR.string.setting_key_particle ) ) && DefaultLayout.enable_particle )
		{
			val = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean( key , true ) ? 1 : 0;
		}
		/************** added by zhenNan.ye end *******************/
		else
		{
			val = Integer.valueOf( PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getString( key , "-1" ) ).intValue();
			if( val == -1 )
			{
				if( key.equals( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) )
				{
					val = DefaultLayout.mainmenu_page_effect_id;
				}
				else
				{
					val = DefaultLayout.desktop_page_effect_id;
				}
				PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putString( key , val + "" ).commit();
			}
		}
		return val;
	}
}
