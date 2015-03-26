package com.iLoong.launcher.SetupMenu.Actions;


import java.lang.reflect.Method;
import com.iLoong.launcher.SetupMenu.SetupMenu;


public class StatusBarAction extends Action
{
	
	public StatusBarAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , StatusBarAction.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_SHOW_NOTIFICATIONS , new StatusBarAction( ActionSetting.ACTION_SHOW_NOTIFICATIONS , StatusBarAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		Method expand = null;
		try
		{
			Object service = SetupMenu.getContext().getSystemService( "statusbar" );
			Class<?> statusbarManager = Class.forName( "android.app.StatusBarManager" );
			if( statusbarManager == null )
				return;
			if( ( expand = statusbarManager.getMethod( "expand" ) ) != null )
			{
				expand.invoke( service );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
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
}
