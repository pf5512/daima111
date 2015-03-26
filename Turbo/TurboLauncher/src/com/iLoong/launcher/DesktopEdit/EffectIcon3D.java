package com.iLoong.launcher.DesktopEdit;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.pub.UmEventUtil;
import com.iLoong.launcher.pub.provider.PubProviderHelper;


public class EffectIcon3D extends Icon3D
{
	
	private int index;
	public static boolean isDelayEvent = false;
	
	public EffectIcon3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap IconBg ,
			int index )
	{
		super( name , bmp , title , IconBg , false );
		this.index = index;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Workspace3D.hasDownInEditMode = true;
		iLoongLauncher.getInstance().d3dListener.getWorkspace3D().initView();
		Context mContext = iLoongLauncher.getInstance();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( mContext );
		String prefsKey = "";
		prefsKey = mContext.getResources().getString( RR.string.setting_key_desktopeffects );
		prefs.edit().putString( prefsKey , String.valueOf( index ) ).commit();
		PubProviderHelper.addOrUpdateValue( "effect" , prefsKey , String.valueOf( index ) );
		iLoongLauncher.getInstance().d3dListener.getWorkspace3D().setEffectType( index );
		NPageBase.autoEffect = true;
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				iLoongLauncher.getInstance().d3dListener.getWorkspace3D().startPreviewEffect( true );
			}
		} );
		Log.i( "isDelayEvent" , isDelayEvent + "" );
		if( !isDelayEvent )
		{
			isDelayEvent = true;
			UmEventUtil.workspaceEffect( mContext , prefsKey , 60000 );
		}
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
}
