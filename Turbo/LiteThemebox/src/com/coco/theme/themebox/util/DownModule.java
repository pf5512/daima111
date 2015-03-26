package com.coco.theme.themebox.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.database.service.HotService;


public class DownModule
{
	
	private final String LOG_TAG = "DownModule";
	private Context mContext;
	private DownloadThumbThread downThumbThread = null;
	private List<DownImageNode> downThumbList = new ArrayList<DownImageNode>();
	private Object syncThumbObject = new Object();
	private DownloadPreviewThread downPreviewThread = null;
	private List<DownImageNode> downPreviewList = new ArrayList<DownImageNode>();
	private Object syncPreviewObject = new Object();
	private static DownModule mDownModule = null;
	private boolean run=true;
	public DownModule(
			Context context )
	{
		mContext = context;
	}
	
	public static DownModule getInstance(
			Context context )
	{
		if( mDownModule == null )
		{
			mDownModule = new DownModule( context );
		}
		return mDownModule;
	}
	
	public void dispose()
	{
		Log.d( LOG_TAG , "dispose" );
		synchronized( this.syncThumbObject )
		{
			downThumbList.clear();
			if( downThumbThread != null )
			{
				downThumbThread.stopRun();
				downThumbThread = null;
			}
			DownloadList.getInstance( mContext ).dispose();
		}
		synchronized( this.syncPreviewObject )
		{
			downPreviewList.clear();
			if( downPreviewThread != null )
			{
				downPreviewThread.stopRun();
				downPreviewThread = null;
			}
		}
	}
	
	public void stopDownlist()
	{
		DownloadList.getInstance( mContext ).stopDownloadList();
	}
	
	// 安装apk
	public void installApk(
			String pkgName ,
			String type )
	{
		String filepath = getAppFile( pkgName , type );
		File file = new File( filepath );
		Log.v( "OpenFile" , file.getName() );
		Intent intent = new Intent();
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setAction( android.content.Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
		mContext.startActivity( intent );
	}
	
	// 下载缩略图完�?
	private void downloadThumbFinish(
			String pkgName ,
			String type )
	{
		PathTool.makeDir( getImageDir( pkgName , type ) );
		boolean result = PathTool.moveFile( getDownloadingThumb( pkgName , type ) , getThumbFile( pkgName , type ) );
		if( result )
		{
			Intent intent = new Intent( getACTION_THUMB_CHANGED( type ) );
			intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
			mContext.sendBroadcast( intent );
		}
	}
	
	// 下载预览图完成
	private void downloadPreviewFinish(
			String pkgName ,
			String type )
	{
		Log.d( LOG_TAG , "downloadPreviewFinish=" + pkgName );
		Intent intent = new Intent( getACTION_PREVIEW_CHANGED( type ) );
		intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , pkgName );
		mContext.sendBroadcast( intent );
	}
	
	public void downloadList()
	{
		Log.v( LOG_TAG , "downloadList" );
		if( !isAllowDownload() )
		{
			return;
		}
		DownloadList.getInstance( mContext ).downList();
	}
	
	private boolean isAllowDownload()
	{
		return StaticClass.isAllowDownload( mContext );
	}
	
	public void downloadThumb(
			String pkgName ,
			String tabType )
	{
		Log.v( LOG_TAG , "downloadThumb=" + pkgName );
		if( !isAllowDownload() )
		{
			return;
		}
		synchronized( this.syncThumbObject )
		{
			int index = findImageDownData( pkgName , DownType.TYPE_IMAGE_THUMB , tabType );
			if( index != -1 )
			{
				return;
			}
			if( downThumbThread != null && downThumbThread.isPackage( pkgName ) )
			{
				return;
			}
			downThumbList.add( new DownImageNode( pkgName , DownType.TYPE_IMAGE_THUMB , tabType ) );
			resetdownThumbList( tabType );
			if( downThumbThread == null )
			{
				downThumbThread = new DownloadThumbThread();
				downThumbThread.start();
			}
		}
	}
	
	public void resetdownThumbList(
			String type )
	{
		if( type == null )
		{
			return;
		}
		Collections.sort( downThumbList , new ByTypeValue( type ) );
	}
	
	class ByTypeValue implements Comparator<DownImageNode>
	{
		
		private String type = null;
		
		public ByTypeValue(
				String type )
		{
			this.type = type;
		}
		
		@Override
		public int compare(
				DownImageNode lhs ,
				DownImageNode rhs )
		{
			// TODO Auto-generated method stub
			if( type == null )
			{
				return 0;
			}
			try
			{
				if( lhs.tabType.equals( rhs.tabType ) )
				{
					return 0;
				}
				else if( lhs.tabType.equals( type ) && !rhs.tabType.equals( type ) )
				{
					return -1;
				}
				else if( !lhs.tabType.equals( type ) && rhs.tabType.equals( type ) )
				{
					return 1;
				}
				return 0;
			}
			catch( Exception e )
			{
				return 0;
			}
		}
	}
	
	public boolean isRefreshList()
	{
		return DownloadList.getInstance( mContext ).isRefreshList();
	}
	
	public void downloadPreview(
			String pkgName ,
			String tabType )
	{
		Log.v( LOG_TAG , "downloadPreview=" + pkgName );
		if( !isAllowDownload() )
		{
			return;
		}
		synchronized( this.syncPreviewObject )
		{
			int index = findImageDownData( pkgName , DownType.TYPE_IMAGE_PREVIEW , tabType );
			if( index != -1 )
			{
				DownImageNode node = downPreviewList.get( index );
				downPreviewList.remove( node );
				downPreviewList.add( 0 , node );
				return;
			}
			if( downPreviewThread != null && downPreviewThread.isPackage( pkgName ) )
			{
				return;
			}
			downPreviewList.add( 0 , new DownImageNode( pkgName , DownType.TYPE_IMAGE_PREVIEW , tabType ) );
			if( downPreviewThread == null )
			{
				downPreviewThread = new DownloadPreviewThread();
				downPreviewThread.start();
			}
		}
	}
	
	private int findImageDownData(
			String pkgName ,
			DownType type ,
			String tabtype )
	{
		if( type == DownType.TYPE_IMAGE_THUMB )
		{
			for( int i = 0 ; i < downThumbList.size() ; i++ )
			{
				DownImageNode node = downThumbList.get( i );
				if( node.packname.equals( pkgName ) && node.downType == type && node.tabType.equals( tabtype ) )
				{
					return i;
				}
			}
		}
		else if( type == DownType.TYPE_IMAGE_PREVIEW )
		{
			for( int i = 0 ; i < downPreviewList.size() ; i++ )
			{
				DownImageNode node = downPreviewList.get( i );
				if( node.packname.equals( pkgName ) && node.downType == type && node.tabType.equals( tabtype ) )
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	private class DownloadThumbThread extends Thread
	{
		
		private volatile DownImageNode curDownImage;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private HotService hotServer = new HotService( mContext );
		
		public void stopRun()
		{
			run=false;
			isExit = true;
			if( urlConn != null )
			{
				new Thread( new Runnable() {
					
					@Override
					public void run()
					{
						if( urlConn != null )
						{
							urlConn.disconnect();
							urlConn = null;
						}
					}
				} ).start();
			}
		}
		
		public boolean isPackage(
				String pkgName )
		{
			DownImageNode node = curDownImage;
			if( node != null && node.packname.equals( pkgName ) )
			{
				return true;
			}
			return false;
		}
		
		private String getThumbimgUrlAddress(
				String packageName ,
				String type )
		{
			return hotServer.queryThumbimg( packageName , type );
		}
		
		@Override
		public void run()
		{
			while( run )
			{
				synchronized( syncThumbObject )
				{
					if( downThumbList != null && downThumbList.size() == 0 )
					{
						break;
					}
					curDownImage = downThumbList.get( 0 );
					downThumbList.remove( 0 );
				}
				FileOutputStream fileOut = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try
				{
					String sdpath = null;
					String addr = null;
					if( curDownImage.downType == DownType.TYPE_IMAGE_THUMB )
					{
						sdpath = getDownloadingThumb( curDownImage.packname , curDownImage.tabType );
						if( sdpath == null )
						{
							break;
						}
						addr = getThumbimgUrlAddress( curDownImage.packname , curDownImage.tabType );
						if( addr == null )
						{
							break;
						}
						URL url = new URL( addr );
						Log.d( LOG_TAG , "downImage,url=" + url.toString() );
						// 创建连接
						urlConn = (HttpURLConnection)url.openConnection();
						urlConn.connect();
						if( isExit )
						{
							break;
						}
						// 获取文件大小
						// urlConn.getContentLength();
						// 创建输入
						netStream = urlConn.getInputStream();
						File apkFile = new File( sdpath );
						fileOut = new FileOutputStream( apkFile , false );
						byte buf[] = new byte[1024];
						// 写入到文件中
						while( true )
						{
							int numread = netStream.read( buf );
							if( numread <= 0 )
							{
								break;
							}
							// 写入文件
							fileOut.write( buf , 0 , numread );
						}
						isSucceed = true;
					}
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				finally
				{
					if( fileOut != null )
					{
						try
						{
							fileOut.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						fileOut = null;
					}
					if( netStream != null )
					{
						try
						{
							netStream.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						netStream = null;
					}
					if( urlConn != null )
					{
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if( isSucceed )
				{
					if( curDownImage != null && curDownImage.downType == DownType.TYPE_IMAGE_THUMB )
					{
						downloadThumbFinish( curDownImage.packname , curDownImage.tabType );
					}
				}
			}
			synchronized( syncThumbObject )
			{
				downThumbThread = null;
			}
		}
	}
	
	private class DownloadPreviewThread extends Thread
	{
		
		private volatile DownImageNode curDownImage;
		private volatile HttpURLConnection urlConn;
		private volatile boolean isExit = false;
		private HotService hotServer = new HotService( mContext );
		
		public void stopRun()
		{
			isExit = true;
			if( urlConn != null )
			{
				new Thread( new Runnable() {
					
					@Override
					public void run()
					{
						if( urlConn != null )
						{
							urlConn.disconnect();
							urlConn = null;
						}
					}
				} ).start();
			}
		}
		
		public boolean isPackage(
				String pkgName )
		{
			DownImageNode node = curDownImage;
			if( node != null && node.packname.equals( pkgName ) )
			{
				return true;
			}
			return false;
		}
		
		private String getResrulAddress(
				String packageName ,
				String type )
		{
			return hotServer.queryPreviewAddress( packageName , type );
		}
		
		@Override
		public void run()
		{
			while( true )
			{
				synchronized( syncPreviewObject )
				{
					if( downPreviewList.size() == 0 )
					{
						break;
					}
					curDownImage = downPreviewList.get( 0 );
					downPreviewList.remove( 0 );
				}
				FileOutputStream fileOut = null;
				InputStream netStream = null;
				boolean isSucceed = false;
				try
				{
					String sdpath = null;
					String addr = null;
					if( curDownImage.downType == DownType.TYPE_IMAGE_PREVIEW )
					{
						sdpath = getDownloadingPreview( curDownImage.packname , curDownImage.tabType );
						if( sdpath == null )
						{
							break;
						}
						File apkFile = new File( sdpath );
						addr = getResrulAddress( curDownImage.packname , curDownImage.tabType );
						if( addr == null )
						{
							continue;
						}
						if( isExit )
						{
							break;
						}
						String[] addrUrl = addr.split( ";" );
						if( addrUrl == null ){
							break;
						}
						for( int i = 0 ; i < addrUrl.length ; i++ )
						{
							URL url = new URL( addrUrl[i] );
							Log.d( LOG_TAG , "downImage,url=" + url.toString() );
							// 创建连接
							if( urlConn != null )
							{
								urlConn.disconnect();
								urlConn = null;
							}
							urlConn = (HttpURLConnection)url.openConnection();
							urlConn.connect();
							// 获取文件大小
							// urlConn.getContentLength();
							// 创建输入
							netStream = urlConn.getInputStream();
							sdpath += i;
							apkFile = new File( sdpath );
							fileOut = new FileOutputStream( apkFile , false );
							// int count = 0;
							// 缓存
							byte buf[] = new byte[1024];
							// 写入到文件中
							while( true )
							{
								int numread = netStream.read( buf );
								if( numread <= 0 )
								{
									break;
								}
								fileOut.write( buf , 0 , numread );
							}
							PathTool.makeDir( getPreviewDir( curDownImage.packname , curDownImage.tabType ) );
							PathTool.moveFile( sdpath , getPreviewDir( curDownImage.packname , curDownImage.tabType ) + "/preview" + i + ".tupian" );
						}
						isSucceed = true;
					}
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				finally
				{
					if( fileOut != null )
					{
						try
						{
							fileOut.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						fileOut = null;
					}
					if( netStream != null )
					{
						try
						{
							netStream.close();
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
						netStream = null;
					}
					if( urlConn != null )
					{
						urlConn.disconnect();
						urlConn = null;
					}
				}
				if( isSucceed )
				{
					if( curDownImage.downType == DownType.TYPE_IMAGE_PREVIEW )
					{
						downloadPreviewFinish( curDownImage.packname , curDownImage.tabType );
					}
				}
			}
			synchronized( syncPreviewObject )
			{
				downPreviewThread = null;
			}
		}
	}
	
	private String getAppFile(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getAppFile( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getAppLiveFile( pkgName );
		}
		return null;
	}
	
	private String getPreviewDir(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getPreviewDir( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getPreviewDir( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getPreviewDir( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getPreviewDir( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getPreviewDir( pkgName );
		}
		return null;
	}
	
	private String getDownloadingPreview(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getDownloadingPreview( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getDownloadingPreview( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getDownloadingPreview( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getDownloadingPreview( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getDownloadingPreview( pkgName );
		}
		return null;
	}
	
	private String getDownloadingThumb(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getDownloadingThumb( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getDownloadingThumb( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getDownloadingThumb( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getDownloadingThumb( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getDownloadingThumb( pkgName );
		}
		return null;
	}
	
	private String getThumbFile(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getThumbFile( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getThumbFile( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getThumbFile( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getThumbFile( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getThumbFile( pkgName );
		}
		return null;
	}
	
	private String getImageDir(
			String pkgName ,
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.util.PathTool.getImageDir( pkgName );
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.PathTool.getImageDir( pkgName );
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getImageDir( pkgName );
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.PathTool.getImageDir( pkgName );
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.PathTool.getImageDir( pkgName );
		}
		return null;
	}
	
	private String getACTION_PREVIEW_CHANGED(
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.StaticClass.ACTION_PREVIEW_CHANGED;
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.StaticClass.ACTION_PREVIEW_CHANGED;
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_PREVIEW_CHANGED;
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.StaticClass.ACTION_PREVIEW_CHANGED;
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_LIVE_PREVIEW_CHANGED;
		}
		return null;
	}
	
	private String getACTION_THUMB_CHANGED(
			String type )
	{
		if( type.equals( DownloadList.Theme_Type ) )
		{
			return com.coco.theme.themebox.StaticClass.ACTION_THUMB_CHANGED;
		}
		else if( type.equals( DownloadList.Widget_Type ) )
		{
			return com.coco.widget.widgetbox.StaticClass.ACTION_THUMB_CHANGED;
		}
		else if( type.equals( DownloadList.Wallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_THUMB_CHANGED;
		}
		else if( type.equals( DownloadList.Font_Type ) )
		{
			return com.coco.font.fontbox.StaticClass.ACTION_THUMB_CHANGED;
		}
		else if( type.equals( DownloadList.LiveWallpaper_Type ) )
		{
			return com.coco.wallpaper.wallpaperbox.StaticClass.ACTION_LIVE_THUMB_CHANGED;
		}
		return null;
	}
}
