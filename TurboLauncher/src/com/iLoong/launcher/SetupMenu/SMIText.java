package com.iLoong.launcher.SetupMenu;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.SetupMenu.SetupMenu.SetupMenuItem;
import com.iLoong.launcher.theme.ThemeManager;


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
		if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/" + SetupMenu.SETUPMENU_FOLDERNAME + item.icon ) )
		{
			mIcon = BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/" + SetupMenu.SETUPMENU_FOLDERNAME + item.icon );
		}
		else
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
		}
		setCompoundDrawablesWithIntrinsicBounds( null , new FastBitmapDrawable( mIcon , SetupMenu.mScale ) , null , null );
		setText( item.name );
		setTag( item );
	}
}
