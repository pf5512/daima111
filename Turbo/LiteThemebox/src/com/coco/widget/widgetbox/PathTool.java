package com.coco.widget.widgetbox;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import android.os.Environment;

import com.coco.theme.themebox.util.Log;


public class PathTool
{
	
	private static final String LOG_TAG = "PathTool";
	private static String custom_sdcard_root_path = "/Coco/";
	
	public static String getCustomRootPath()
	{
		return custom_sdcard_root_path;
	}
	
	public static void setCustomRootPath(
			String customRootPath )
	{
		custom_sdcard_root_path = customRootPath;
	}
	
	public static String getWidgetDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Widget/";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Widget/";
	}
	
	public static String getRecommendDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Widget/recommend";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Widget/recommend";
	}
	
	public static String getImageDir(
			String packageName )
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Widget/Image/" + packageName;
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Widget/Image/" + packageName;
	}
	
	public static String getDownloadingDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Widget/Downloading";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Widget/Downloading";
	}
	
	public static String getAppDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Widget/App";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Widget/App";
	}
	
	public static String getTempDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Widget/Temp";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Widget/Temp";
	}
	
	public static String getAppFile(
			String packageName )
	{
		return getAppDir() + "/" + packageName + ".apk";
	}
	
	public static String getThumbFile(
			String packageName )
	{
		return getImageDir( packageName ) + "/thumb.tupian";
	}
	
	public static String getPreviewDir(
			String packageName )
	{
		return getImageDir( packageName ) + "/Preview";
	}
	
	public static String[] getPreviewLists(
			String packageName )
	{
		String rootPath = getPreviewDir( packageName );
		File rootFile = new File( rootPath );
		String[] fileNames = rootFile.list( new FilenameFilter() {
			
			@Override
			public boolean accept(
					File dir ,
					String filename )
			{
				if( filename.endsWith( ".tupian" ) )
				{
					return true;
				}
				return false;
			}
		} );
		if( fileNames == null || fileNames.length == 0 )
		{
			return new String[]{};
		}
		String[] filePaths = new String[fileNames.length];
		for( int i = 0 ; i < filePaths.length ; i++ )
		{
			filePaths[i] = rootPath + "/" + fileNames[i];
		}
		return filePaths;
	}
	
	public static String getDownloadingThumb(
			String packageName )
	{
		return getDownloadingDir() + "/" + packageName + "_thumb.tmp";
	}
	
	public static String getDownloadingPreview(
			String packageName )
	{
		return getDownloadingDir() + "/" + packageName + "_preview.tmp";
	}
	
	public static String getDownloadingApp(
			String packageName )
	{
		return getDownloadingDir() + "/" + packageName + "_app.tmp";
	}
	
	public static String getWidgetDownloadingList()
	{
		return getDownloadingDir() + "/widgetlist.tmp";
	}
	
	public static String getThumbTempFile()
	{
		String result = getTempDir() + "/share_thumb.png";
		Log.d( LOG_TAG , "thumbTempPath=" + result );
		return result;
	}
	
	public static boolean copyFile(
			String sourceFile ,
			String targetFile )
	{
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try
		{
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream( new FileInputStream( sourceFile ) );
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream( new FileOutputStream( targetFile ) );
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while( ( len = inBuff.read( b ) ) != -1 )
			{
				outBuff.write( b , 0 , len );
			}
			// 刷新此缓冲的输出�?
			outBuff.flush();
			return true;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭�?
			if( inBuff != null )
			{
				try
				{
					inBuff.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			if( outBuff != null )
			{
				try
				{
					outBuff.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public static void makeDirApp()
	{
		try
		{
			String appDir = getAppDir();
			new File( getAppDir() ).mkdirs();
			Log.d( LOG_TAG , "appDir=" + appDir );
			String recommendDir = getRecommendDir();
			new File( recommendDir ).mkdirs();
			String downloadingDir = getDownloadingDir();
			new File( downloadingDir ).mkdirs();
			Log.d( LOG_TAG , "downloadingDir=" + downloadingDir );
			String tempDir = getTempDir();
			new File( tempDir ).mkdirs();
			Log.d( LOG_TAG , "tempDir=" + tempDir );
			new File( getWidgetDir() , ".nomedia" ).createNewFile();
		}
		catch( SecurityException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public static void moveFile(
			String sourceFile ,
			String targetFile )
	{
		if( copyFile( sourceFile , targetFile ) )
		{
			new File( sourceFile ).delete();
		}
	}
	
	public static void makeDirImage(
			String packageName )
	{
		try
		{
			String imageDir = getImageDir( packageName );
			new File( imageDir ).mkdirs();
			Log.d( LOG_TAG , "imageDir=" + imageDir );
		}
		catch( SecurityException e )
		{
			e.printStackTrace();
		}
	}
	
	public static void makePreviewDir(
			String packageName )
	{
		try
		{
			String imageDir = getPreviewDir( packageName );
			new File( imageDir ).mkdirs();
			Log.d( LOG_TAG , "previewDir=" + imageDir );
		}
		catch( SecurityException e )
		{
			e.printStackTrace();
		}
	}
}
