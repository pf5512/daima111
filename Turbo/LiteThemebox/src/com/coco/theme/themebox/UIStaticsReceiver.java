package com.coco.theme.themebox;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.service.HotService;
import com.coco.theme.themebox.update.UpdateService;
import com.coco.theme.themebox.util.DownloadEngineApkService;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;


public class UIStaticsReceiver extends BroadcastReceiver
{
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.v( "UIStaticsReceiver" , "onReceive----action=" + action );
		if( Intent.ACTION_PACKAGE_ADDED.equals( action ) )
		{
			String packageName = intent.getData().getSchemeSpecificPart();
			HotService sv = new HotService( context );
			String resid = sv.queryResid( packageName );
			if( resid != null )
			{
				DownloadList.getInstance( context ).startUICenterLog( DownloadList.ACTION_INSTALL_LOG , resid , packageName );
			}
			Log.v( "UIStaticsReceiver" , "ACTION_PACKAGE_ADDED----packageName=" + packageName );
		}
		else if( Intent.ACTION_PACKAGE_REMOVED.equals( action ) )
		{
			String packageName = intent.getData().getSchemeSpecificPart();
			HotService sv = new HotService( context );
			String resid = sv.queryResid( packageName );
			if( resid != null )
			{
				DownloadList.getInstance( context ).startUICenterLog( DownloadList.ACTION_UNINSTALL_LOG , resid , packageName );
			}
			Log.v( "UIStaticsReceiver" , "ACTION_PACKAGE_REMOVED----packageName=" + packageName );
		}
		else if( "cn.moppo.fontstore.flipfont.font_set".equals( action ) )
		{
			ActivityManager.KillSomeActivity( "FontPreviewActivity" );
		}
		else if( "com.coco.personalCenter.stop.update".equals( action ) )
		{
			Intent it = new Intent( context , UpdateService.class );
			context.stopService( it );
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( context ).edit();
			editor.putLong( "selfrefresh" , 0 );
			editor.commit();
		}
		else if( "com.coco.engine.stop.download".equals( action ) )
		{
			Intent it = new Intent( context , DownloadEngineApkService.class );
			it.putExtra( "stop" , "stopcurrent" );
			context.startService( it );
		}
		else if( "com.android.topwise.system_fonts".equals( action ) )
		{
			if( FunctionConfig.isEnable_topwise_style() )
			{
				String path = intent.getStringExtra( "path" );
				if( path != null )
				{
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
					pref.edit().putString( "currentFont" , path ).commit();
				}
			}
		}
	}
}
