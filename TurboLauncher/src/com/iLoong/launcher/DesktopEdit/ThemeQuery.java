package com.iLoong.launcher.DesktopEdit;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.service.ThemeService;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.core.Utilities;


public class ThemeQuery
{
	
	public List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
	private Context context;
	
	public ThemeQuery(
			Context cxt )
	{
		context = cxt;
		localList = queryPackage();
	}
	
	public void free()
	{
		if( localList != null )
		{
			localList.clear();
			localList = null;
		}
	}
	
	private List<ThemeInformation> queryPackage()
	{
		List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
		ThemeService themeSv = new ThemeService( context );
		List<ThemeInformation> installList = themeSv.queryInstallList();
		for( ThemeInformation info : installList )
		{
			if( info.getThumbImage() == null )
			{
				getItemThumb( info );
			}
			localList.add( info );
		}
		return localList;
	}
	
	
	public void onDestory()
	{
		for( ThemeInformation info : localList )
		{
			info.disposeThumb();
			info = null;
		}
	}
	
	private void getItemThumb(
			ThemeInformation themeInfo )
	{
		int iconSize = DefaultLayout.app_icon_size;
		if( !R3D.doNotNeedScale( themeInfo.getPackageName() , themeInfo.getClassName() ) )
			iconSize *= DefaultLayout.thirdapk_icon_scaleFactor;
		Bitmap iconBit = Utilities.createIconBitmap( themeInfo.iconDrawable , context , iconSize );
		if( iconBit != null )
		{
			themeInfo.setIconImage( iconBit );
		}
	}
}
