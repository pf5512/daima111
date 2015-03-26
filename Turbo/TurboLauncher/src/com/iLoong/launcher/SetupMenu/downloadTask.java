package com.iLoong.launcher.SetupMenu;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.os.Handler;
import android.os.Message;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.DLManager.downloadinfo;
import com.iLoong.launcher.desktop.iLoongApplication;


public class downloadTask extends Thread
{
	
	private static final int BUFFER_SIZE = 8192;
	private int fileSize = 0;
	private String urlStr , fileName;
	private Handler handler;
	private downloadinfo dli;
	private int tryTimes = 0;
	
	public downloadTask(
			Handler handler ,
			downloadinfo dli )
	{
		this.handler = handler;
		this.dli = dli;
		this.urlStr = dli.url;
		//this.urlStr = "http://www.cootesttest.cn/download/apk/CooeeRobottest.apk";
		this.fileName = iLoongApplication.getDownloadPath() + "/" + dli.name;
	}
	
	@Override
	public void run()
	{
		int filelen = 0;
		int TotalFileSize = 0;
		HttpURLConnection conn = null;
		;
		byte[] buf = new byte[BUFFER_SIZE];
		while( true )
		{
			try
			{
				URL url = new URL( urlStr );
				conn = (HttpURLConnection)url.openConnection();
				conn.setReadTimeout( 30000 );
				conn.setAllowUserInteraction( true );
				conn.setRequestProperty( "Range" , "bytes=" + filelen + "-" );
				Log.v( "testdrag" , conn.getResponseMessage() + "Test ResponseCode=" + conn.getResponseCode() );
				if( conn.getResponseCode() != HttpStatus.SC_NOT_FOUND )
				{
					File file = new File( fileName );
					int starttime = DLManager.getInstance().getDB().getDownLoadStartTime( dli.name );
					filelen = DLManager.getInstance().getDB().getDownLoadBytes( dli.name );
					if( filelen == -1 || filelen != file.length() || ( System.currentTimeMillis() / 1000 - starttime ) > 86400 )
					{
						filelen = 0;
						DLManager.getInstance().getDB().Update( dli.name , filelen );
						if( file.exists() )
							file.delete();
					}
					fileSize = conn.getContentLength();
					if( filelen == 0 )
					{
						DLManager.getInstance().getDB().Insert( dli.name , fileSize );
					}
					int curPosition = filelen;
					TotalFileSize = filelen + fileSize;
					RandomAccessFile fos = new RandomAccessFile( file , "rw" );
					fos.seek( filelen );
					BufferedInputStream bis = new BufferedInputStream( conn.getInputStream() );
					long nextmsg = System.currentTimeMillis();
					while( curPosition < TotalFileSize )
					{
						int len = bis.read( buf , 0 , BUFFER_SIZE );
						if( len == -1 )
						{
							break;
						}
						fos.write( buf , 0 , len );
						curPosition += len;
						filelen += len;
						DLManager.getInstance().getDB().Update( dli.name , filelen );
						if( System.currentTimeMillis() > nextmsg && TotalFileSize != filelen )
						{
							Message msg = Message.obtain();
							msg.what = 0;
							msg.arg1 = TotalFileSize;
							msg.arg2 = filelen;
							msg.obj = dli;
							handler.sendMessage( msg );
							nextmsg = System.currentTimeMillis() + 1000;
						}
					}
					bis.close();
					fos.close();
					if( TotalFileSize == filelen )
					{
						Message msg = Message.obtain();
						msg.what = 100;
						msg.arg1 = TotalFileSize;
						msg.arg2 = filelen;
						msg.obj = dli;
						handler.sendMessage( msg );
					}
					else
					{
						Message msg = Message.obtain();
						msg.what = 101;
						msg.obj = dli;
						handler.sendMessage( msg );
					}
				}
				else
				{
					Message msg = Message.obtain();
					msg.what = 404;
					msg.obj = dli;
					handler.sendMessage( msg );
				}
				break;
			}
			catch( Exception e )
			{
				tryTimes++;
				if( conn != null )
				{
					conn.disconnect();
				}
				try
				{
					Thread.sleep( 5000 );
				}
				catch( InterruptedException e1 )
				{
				}
				if( tryTimes > 3 )
				{
					tryTimes = 0;
					Message msg = Message.obtain();
					msg.what = -1;
					msg.obj = dli;
					handler.sendMessage( msg );
					Log.v( "testdrag" , "tryTimes is 3" );
					break;
				}
			}
		}
		Log.v( "testdrag" , "break downloadTask loop" );
		buf = null;
	}
}
