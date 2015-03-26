package com.iLoong.launcher.pub;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.macinfo.Installation;


public class PublicHelper
{
	
	public static int getScreenWidth()
	{
		return Utils3D.getScreenWidth();
	}
	
	public static int getScreenHeight()
	{
		return Utils3D.getScreenHeight();
	}
	
	public static int getAppbarHeight()
	{
		return R3D.appbar_height;
	}
	
	public static int getStatusBarHeight()
	{
		return Utils3D.getStatusBarHeight();
	}
	
	public static int getWorkspaceCellWidth()
	{
		return R3D.Workspace_cell_each_width;
	}
	
	public static int getWorkspaceCellHeight()
	{
		return R3D.Workspace_cell_each_height;
	}
	
	public static int getAppListCountX()
	{
		int mCellCountX = 4;
		if( DefaultLayout.dispose_cell_count )
		{
			mCellCountX = DefaultLayout.cellCountX;
			mCellCountX = mCellCountX > 5 ? 5 : mCellCountX;
		}
		else
		{
			if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
			{
				mCellCountX = 4;
			}
			else
			{
				mCellCountX = 4;
			}
		}
		return mCellCountX;
	}
	
	public static int getAppListCountY()
	{
		int mCellCountY = 4;
		if( DefaultLayout.dispose_cell_count )
		{
			mCellCountY = DefaultLayout.cellCountY;
			mCellCountY = mCellCountY > 5 ? 5 : mCellCountY;
		}
		else
		{
			if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
			{
				mCellCountY = 5;
			}
			else
			{
				mCellCountY = 4;
			}
		}
		return mCellCountY;
	}
	
	public static int getAppIconSize()
	{
		return DefaultLayout.app_icon_size;
	}
	
	public static Bitmap IconToPixmap3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg )
	{
		return Utils3D.IconToPixmap3D( b , title , icn_bg , title_bg , true );
	}
	
	public static Bitmap createIconBitmap(
			Drawable icon ,
			Context context )
	{
		return Utilities.createIconBitmap( icon , context , DefaultLayout.app_icon_size );
	}
	
	public static String getInstallationid(
			Context context )
	{
		return Installation.id( context );
	}
	
	public static float getPageTweenTime()
	{
		return DefaultLayout.page_tween_time;
	}
}
