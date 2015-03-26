package com.iLoong.launcher.SetupMenu.Actions;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class DesktopEffect extends Action
{
	
	public DesktopEffect(
			int actionid ,
			String action )
	{
		super( actionid , action );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_DESKTOP_EFFECT , new DesktopEffect( ActionSetting.ACTION_DESKTOP_EFFECT , DesktopEffect.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		//Jone start
		if( RR.net_version )
		{
			final int INDEX_WORKSPACE = 0;
			final int INDEX_APP = 1;
			final int position = Desktop3DListener.root.getWorkspace().getEffectType();
			final String ACTION_EFFECT_PREVIEW = "com.cool.action.EffectPreview";
			final String ACTION_EFFECT_PREVIEW_EXTRA_TYPE = "EffectPreviewExtraType";
			final String ACTION_EFFECT_PREVIEW_EXTRA_INDEX = "EffectPreviewExtraIndex";
			Intent it = new Intent( ACTION_EFFECT_PREVIEW );
			it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_TYPE , INDEX_WORKSPACE );
			it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_INDEX , position );
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) )
				iLoongLauncher.getInstance().getD3dListener().getRoot().dealEffectPreview( it );
			//SetupMenu.getInstance().getContext().sendBroadcast( it );
		}
		//Jone end
		else
		{
			//        SendMsgToAndroid.sendShowDeskEffectDialgMsg();
			final String TAG_STRING = "currentTab";
			final String TAG_THEME = "tagEffect";
			final String TAB_TYPE = "type";
			final int TAB_IND = 0;
			final Intent intent = new Intent( mAction );
			mComponent = new ComponentName( "com.cooeeui.brand.turbolauncher" , "com.coco.theme.themebox.MainActivity" );
			intent.setComponent( mComponent );
			try
			{
				if( DefaultLayout.personal_center_internal )
				{
					//	intent.setComponent(new ComponentName(iLoongLauncher.getInstance(), "com.coco.theme.themebox.MainActivity"));
					iLoongLauncher.getInstance().bindThemeActivityData( intent );
					intent.putExtra( TAG_STRING , TAG_THEME );
					intent.putExtra( TAB_TYPE , TAB_IND );
					SetupMenuActions.getInstance().getContext().startActivity( intent );
				}
				else
				{
					PackageManager pm = SetupMenuActions.getInstance().getContext().getPackageManager();
					if( pm.queryIntentActivities( intent , 0 ).size() == 0 )
					{
						iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
							
							@Override
							public void run()
							{
								// TODO Auto-generated method stub
								iLoongLauncher.getInstance().themeCenterDown.ToDownloadApkDialog( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getResources()
										.getString( RR.string.theme ) , "com.iLoong.base.themebox" );
							}
						} );
					}
					else
					{
						iLoongLauncher.getInstance().bindThemeActivityData( intent );
						SetupMenuActions.getInstance().getContext().startActivity( intent );
					}
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		return;
	}
	
	@Override
	protected void OnActionFinish()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void OnPutValue(
			String key )
	{
		// TODO Auto-generated method stub
	}
}
