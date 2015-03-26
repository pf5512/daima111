package com.coco.theme.themebox.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import android.os.Environment;


public class PathTool
{
	
	private static final String LOG_TAG = "PathTool";
	private static String custom_sdcard_root_path = "/Coco/";
	private static String themebox_profile_authority_path = "com.coco.theme.themebox.apprecommend";
	
	public static String getCustomRootPath()
	{
		return custom_sdcard_root_path;
	}
	
	public static String getDownloadDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Download/";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Download/";
	}
	
	public static void setCustomRootPath(
			String customRootPath )
	{
		if( customRootPath != null )
		{
			custom_sdcard_root_path = customRootPath;
			com.coco.lock2.lockbox.util.PathTool.setCustomRootPath( customRootPath );
			com.coco.scene.scenebox.PathTool.setCustomRootPath( customRootPath );
			com.coco.wallpaper.wallpaperbox.PathTool.setCustomRootPath( customRootPath );
			com.coco.font.fontbox.PathTool.setCustomRootPath( customRootPath );
			com.coco.widget.widgetbox.PathTool.setCustomRootPath( customRootPath );
		}
	}
	
	public static String getProfilePath()
	{
		return themebox_profile_authority_path;
	}
	
	public static void setProfilePath(
			String customProfilePath )
	{
		if( customProfilePath != null )
		{
			themebox_profile_authority_path = customProfilePath;
		}
	}
	
	public static String getThemeDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Theme/";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Theme/";
	}
	
	public static String getRecommendDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Theme/recommend";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Theme/recommend";
	}
	
	public static String getImageDir(
			String packageName )
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Theme/Image/" + packageName;
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Theme/Image/" + packageName;
	}
	
	public static String getDownloadingDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Theme/Downloading";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Theme/Downloading";
	}
	
	public static String getAppDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Theme/App";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Theme/App";
	}
	
	public static String getTempDir()
	{
		if( !com.coco.theme.themebox.StaticClass.set_directory_path.equals( "" ) )
		{
			return com.coco.theme.themebox.StaticClass.set_directory_path + custom_sdcard_root_path + "Theme/Temp";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath() + custom_sdcard_root_path + "Theme/Temp";
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
	
	public static String getThemeDownloadingList()
	{
		return getDownloadingDir() + "/themelist.tmp";
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
			String themeDir = getThemeDir();
			// new File(getThemeDir()).mkdirs();
			Log.d( LOG_TAG , "themeDir=" + themeDir );
			new File( themeDir ).mkdirs();
			new File( themeDir , ".nomedia" ).createNewFile();
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
	
	public static boolean moveFile(
			String sourceFile ,
			String targetFile )
	{
		//		if( copyFile( sourceFile , targetFile ) )
		//		{
		//			new File( sourceFile ).delete();
		//		}
		try
		{
			File oldFile = new File( sourceFile );
			File fnew = new File( targetFile );
			oldFile.renameTo( fnew );
			return true;
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	public static void makeDir(
			String path )
	{
		try
		{
			new File( path ).mkdirs();
			Log.d( LOG_TAG , "makDir=" + path );
		}
		catch( SecurityException e )
		{
			e.printStackTrace();
		}
	}
}
