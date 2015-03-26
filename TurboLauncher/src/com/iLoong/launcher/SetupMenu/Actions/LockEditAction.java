package com.iLoong.launcher.SetupMenu.Actions;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.SetupMenu.SetupMenuItem;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class LockEditAction extends Action
{
	
	private static boolean mLock = false;
	
	public LockEditAction(
			int actionid ,
			String action )
	{
		super( actionid , action );
		putIntentAction( SetupMenu.getContext() , LockEditAction.class );
	}
	
	public static void Init()
	{
		SetupMenuActions.getInstance().RegisterAction( ActionSetting.ACTION_LOCKED_EDITING , new LockEditAction( ActionSetting.ACTION_LOCKED_EDITING , LockEditAction.class.getName() ) );
	}
	
	@Override
	protected void OnRunAction()
	{
		if( SetupMenu.getInstance() != null )
			SetupMenu.getInstance().getSetMenuDesktop().OnUnLoad();
		if( mLock )
		{
			mLock = false;
			if( SetupMenu.getInstance() != null )
			{
				SetupMenuItem menuitem = SetupMenu.getInstance().getMenuItem( ActionSetting.ACTION_LOCKED_EDITING );
				SetupMenu.getInstance().getMenuDeskTop().UpdateMenuItemBitmap( menuitem.page , menuitem.id , getBitmap() );
			}
		}
		else
		{
			dialog();
		}
	}
	
	protected void dialog()
	{
		AlertDialog.Builder builder = new Builder( iLoongLauncher.getInstance() );
		builder.setMessage( R3D.getString( RR.string.lock_editor ) );
		builder.setTitle( R3D.getString( RR.string.lock_editor_title ) );
		builder.setPositiveButton( R3D.getString( RR.string.circle_ok_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				mLock = true;
				SetupMenuItem menuitem = SetupMenu.getInstance().getMenuItem( ActionSetting.ACTION_LOCKED_EDITING );
				SetupMenu.getInstance().getMenuDeskTop().UpdateMenuItemBitmap( menuitem.page , menuitem.id , getBitmap() );
				dialog.dismiss();
				SetupMenuActions.getInstance().ActivityFinish( ActionSetting.ACTION_LOCKED_EDITING );
			}
		} );
		builder.setNegativeButton( R3D.getString( RR.string.circle_cancel_action ) , new OnClickListener() {
			
			@Override
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				dialog.dismiss();
			}
		} );
		builder.create().show();
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
	
	public Bitmap getBitmap()
	{
		Bitmap icon;
		SetupMenuItem menuitem = SetupMenu.getInstance().getMenuItem( mActionID );
		if( !mLock )
		{
			icon = menuitem.iconbmp;
		}
		else
		{
			icon = menuitem.icon2bmp;
		}
		return icon;
	}
}
