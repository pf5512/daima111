package com.iLoong.launcher.Functions.Tab;


import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.util.DisplayMetrics;


public class ThemeHelper
{
	
	public static InputStream getInputStream(
			Context context ,
			boolean autoAdapt ,
			String fileName )
	{
		InputStream instr = null;
		if( autoAdapt )
		{
			String filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
			if( !filePrefix.contains( "-" ) )
			{
				filePrefix = filePrefix + "-" + context.getResources().getDisplayMetrics().heightPixels + "x" + context.getResources().getDisplayMetrics().widthPixels;
			}
			// 查找精确分辨率如960*540
			try
			{
				String tempFileName = filePrefix + fileName.substring( fileName.indexOf( "/" ) );
				instr = context.getAssets().open( tempFileName );
			}
			catch( IOException e )
			{
				instr = null;
			}
			String dpiFilePrefix = getAutoAdaptDir( context , filePrefix.substring( 0 , filePrefix.indexOf( "-" ) ) );
			if( instr == null )
			{
				String dpiFileName = dpiFilePrefix + fileName.substring( fileName.indexOf( "/" ) );
				try
				{
					instr = context.getAssets().open( dpiFileName );
				}
				catch( IOException e )
				{
				}
			}
			// 在不带dpi的目录下寻找资源，目前系统资源统一放在不带dpi的目录，所以首先寻找不带dpi的目录
			if( instr == null )
			{
				filePrefix = fileName.substring( 0 , fileName.indexOf( "/" ) );
				if( !filePrefix.equals( dpiFilePrefix ) )
				{
					try
					{
						instr = context.getAssets().open( fileName );
					}
					catch( IOException e )
					{
					}
				}
			}
		}
		else
		{
			if( instr == null )
			{
				try
				{
					instr = context.getAssets().open( fileName );
				}
				catch( IOException e )
				{
				}
			}
		}
		return instr;
	}
	
	public static String getAutoAdaptDir(
			Context context ,
			String prefix )
	{
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		if( metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH )
		{
			prefix = prefix + "-xhdpi";
		}
		else if( metrics.densityDpi == DisplayMetrics.DENSITY_HIGH )
		{
			prefix = prefix + "-hdpi";
		}
		else if( metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM )
		{
			prefix = prefix + "-mdpi";
		}
		else if( metrics.densityDpi == DisplayMetrics.DENSITY_LOW )
		{
			prefix = prefix + "-ldpi";
		}
		return prefix;
	}
}
