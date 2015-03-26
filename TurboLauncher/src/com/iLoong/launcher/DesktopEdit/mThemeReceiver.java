package com.iLoong.launcher.DesktopEdit;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.coco.wallpaper.wallpaperbox.StaticClass;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class mThemeReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		String actionName = intent.getAction();
		if( "com.coco.theme.action.DEFAULT_THEME_CHANGED".equals( actionName ) )
		{
			String pkgName = intent.getStringExtra( "pkgName" );
			if( pkgName != null )
			{
				Context mcontext = iLoongLauncher.getInstance();
				SharedPreferences sp = mcontext.getSharedPreferences( "CurrentTheme" , mcontext.MODE_PRIVATE );
				Editor editor = sp.edit();
				editor.putString( "currenttheme_pkg" , pkgName );
				editor.commit();
				PopThemeImageView3D.changeCurrentTheme( pkgName );
			}
		}
		else if( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED.equals( actionName ) )
		{
			if( DesktopEditHost.instance != null && DesktopEditHost.instance.mulpMenuHost.editMenu.pages.size() == 4 )
			{
				DesktopEditHost.instance.mulpMenuHost.editMenu.onMenuFresh( 2 );
			}
		}
		else if( "com.android.launcher.changed.resume".equals( actionName ) )
		{
			if( DesktopEditHost.instance != null )
			{
				if( DesktopEditMenuItem.cur_tab == DesktopEditMenuItem.TAB_THEME )
				{
					if( DesktopEditHost.instance.mulpMenuHost.editMenu.pages.size() == 4 )
					{
						DesktopEditHost.instance.mulpMenuHost.editMenu.onMenuFresh( 1 );
					}
				}
			}
		}
		//		else if( actionName.equals( com.coco.theme.themebox.StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
		//		{
		//			if( DesktopEditHost.instance.mulpMenuHost.editMenu.pages.size() == 4 )
		//			{
		//				DesktopEditHost.instance.mulpMenuHost.editMenu.onMenuFresh( 1 );
		//			}
		//		}
		//		else if( actionName.equals( "android.intent.action.PACKAGE_REMOVED" ) || actionName.equals( "android.intent.action.PACKAGE_ADDED" ) )
		//		{
		//			if( DesktopEditHost.instance.mulpMenuHost.editMenu.pages.size() == 4 )
		//			{
		//				DesktopEditHost.instance.mulpMenuHost.editMenu.onMenuFresh( 1 );
		//			}
		//		}
	}
}
