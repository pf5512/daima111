package com.iLoong.launcher.SetupMenu;


import java.io.IOException;

import com.iLoong.launcher.SetupMenu.SetupMenu.SetupMenuItem;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;


public class SMIText extends TextView
{
	
	private Bitmap mIcon;
	
	public SMIText(
			Context context )
	{
		super( context );
	}
	
	public void setItem(
			SetupMenuItem item )
	{
		String file = SetupMenu.SETUPMENU_FOLDERNAME + item.icon;
		try
		{
			mIcon = ThemeManager.getInstance().getBitmap( file );
			//mIcon = Tools.getImageFromInStream(iLoongLauncher.getInstance().getAssets().open(file));
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setCompoundDrawablesWithIntrinsicBounds( null , new FastBitmapDrawable( mIcon , SetupMenu.mScale ) , null , null );
		setText( item.name );
		setTag( item );
	}
}
