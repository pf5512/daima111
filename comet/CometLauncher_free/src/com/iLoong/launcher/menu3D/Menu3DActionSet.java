package com.iLoong.launcher.menu3D;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.HotSeat3D.HotObjMenuFront.Menu3DAction;
import com.iLoong.launcher.SetupMenu.Actions.DesktopAction;
import com.iLoong.launcher.SetupMenu.Actions.ShowDesktop;
import com.iLoong.launcher.SetupMenu.Actions.UpdateManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.fb.FeedbackAgent;


public class Menu3DActionSet
{
	
	Context mContext;
	
	public static class DesktopUpdate extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			// super.onAction();
			UpdateManager.getInstance().checkClientVersion();
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class Suggest extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			//  super.onAction();
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				public void run()
				{
					FeedbackAgent agent = new FeedbackAgent( iLoongLauncher.getInstance() );
					agent.startFeedbackActivity();
				}
			} );
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class DesktopSetting extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			//  super.onAction();
			ComponentName mComponent = new ComponentName( RR.getPackageName() , DesktopAction.DesktopSettingActivity.class.getName() );
			final Intent intent = new Intent( "com.iLoong.launcher.SetupMenu.Actions.DesktopAction" );
			intent.setComponent( mComponent );
			iLoongLauncher.getInstance().startActivity( intent );
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class SystemSetting extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			// super.onAction();
			final Intent intent = new Intent( "android.settings.SETTINGS" );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			iLoongLauncher.getInstance().startActivity( intent );
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class WallpagerSetting extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			//   super.onAction();
			final Intent pickWallpaper = new Intent( Intent.ACTION_SET_WALLPAPER );
			if( DefaultLayout.wallpaper_has_edage )
			{
				iLoongLauncher.getInstance().startActivity( pickWallpaper );
				return;
			}
			Intent chooser = Intent.createChooser( pickWallpaper , iLoongLauncher.getInstance().getText( RR.string.chooser_wallpaper ) );
			iLoongLauncher.getInstance().startActivity( chooser );
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class ScreenEffect extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			// super.onAction();
			SendMsgToAndroid.sendShowDeskEffectDialgMsg();
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class WidgetAdd extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			super.onAction();
			Root3D.clickPoint.set( Utils3D.getScreenWidth() / 2 , Utils3D.getScreenHeight() / 2 );
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				public void run()
				{
					Root3D.getInstance().getStarted();
				}
			} );
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class DesktopShare extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			//  super.onAction();
			new ShowDesktop().OnRunAction();
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
	
	public static class ScreenCompiler extends Menu3DAction
	{
		
		@Override
		public void onAction()
		{
			super.onAction();
			Root3D.getInstance().entryCompiler();
			// Root3D.getInstance().showPageEdit();
		}
		
		@Override
		public void setParams(
				Object obj )
		{
			// TODO Auto-generated method stub
		}
	}
}
