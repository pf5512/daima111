package com.iLoong.launcher.newspage;


import android.util.Log;

import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.wallpeper.ShakeListener.OnShakeListener;


public class NewspageShakeListener implements OnShakeListener
{
	
	@Override
	public void onShake()
	{
		Log.i( "NewspageShakeListener" , "onShake()" );
		if( Desktop3DListener.root != null && Desktop3DListener.root.appHost != null && Desktop3DListener.root.appHost.appList != null )
		{
			if( Desktop3DListener.root.appHost.appList.isVisible() )
			{
				return;
			}
		}
		if( Desktop3DListener.root != null )
		{
			if( Desktop3DListener.root.newsHandle != null )
			{
				if( Desktop3DListener.root.newsHandle.isDragging )
				{
					return;
				}
			}
		}
		if( !iLoongLauncher.isShowNews )
		{
			if( Desktop3DListener.root != null && Desktop3DListener.root.newsHandle != null )
			{
				if( Desktop3DListener.root.newsHandle.x == 0 )
				{
					Messenger.sendMsg( Messenger.MSG_SHOW_NEWS_AUTO , 0 );
				}
				else
				{
					Messenger.sendMsg( Messenger.MSG_SHOW_NEWS_AUTO , 1 );
				}
			}
		}
		else
		{
			Messenger.sendMsg( Messenger.MSG_REMOVE_NEWS_AUTO , 0 );
		}
	}
}
