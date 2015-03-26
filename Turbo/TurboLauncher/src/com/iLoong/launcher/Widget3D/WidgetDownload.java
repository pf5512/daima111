package com.iLoong.launcher.Widget3D;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class WidgetDownload implements DLManager.DownLoadListener
{
	
	private static final String APK_MIMETYPE = "application/vnd.android.package-archive";
	private static final String DownLoadPath = iLoongApplication.getDownloadPath();
	private static WidgetDownload mInstance;
	
	public synchronized static WidgetDownload getInstance()
	{
		if( mInstance == null )
			mInstance = new WidgetDownload();
		return mInstance;
	}
	
	public static void showInfo(
			String title ,
			String info )
	{
		//		String localTitle = title;
		//		if (localTitle == null)
		//			localTitle = "提示";
		//		new AlertDialog.Builder(iLoongLauncher.getInstance())
		//		.setTitle(localTitle)
		//		.setMessage(info)
		//		.setPositiveButton(iLoongLauncher.getInstance().getString(R.string.circle_ok_action), null)
		//		.show();
		SendMsgToAndroid.sendOurToastMsg( info );
	}
	
	public static void installAPK(
			String path )
	{
		Intent intent = new Intent( Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( new File( path ) ) , "application/vnd.android.package-archive" );
		iLoongLauncher.getInstance().startActivity( intent );
	}
	
	public static boolean checkToDownload(
			ShortcutInfo info ,
			boolean needed )
	{
		boolean checkInfo = false;
		if( info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
		{
			checkInfo = true;
			Widget3DManager.curDownload = info;
		}
		else
			return false;
		String pkgname = info.intent.getComponent().getPackageName();
		String classname = info.intent.getComponent().getClassName();
		String apkname = DefaultLayout.GetDefaultWidgetApkname( pkgname , classname );
		String dlTitle = (String)Widget3DManager.curDownload.title;
		String customID = null;
		if( Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW )
			customID = DefaultLayout.GetDefaultWidgetCustomID( pkgname );
		else if( Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
		{
			customID = DefaultLayout.GetVirtureCustomID( pkgname , classname );
		}
		if( inAssertDir( apkname ) )
		{
			String path = getAssertAPKPath( apkname );
			if( path != null )
			{
				installAPK( path );
				return true;
			}
		}
		if( DLManager.getInstance().isAPKExist( apkname ) )
		{
			if( needed && customID != null && customID.equals( "widget" ) )
				DLManager.getInstance().CheckWidgetApk( dlTitle , apkname , pkgname , getInstance() );
			else
				installAPK( iLoongApplication.getDownloadPath() + "/" + apkname );
			return true;
		}
		else if( DLManager.getInstance().HaveDownLoad( pkgname ) )
		{
			showInfo( null , dlTitle + iLoongLauncher.languageSpace + R3D.getString( RR.string.app_downloading ) );
			return true;
		}
		if( checkInfo )
		{
			if( DefaultLayout.custom_virtual_icon && Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
				SendMsgToAndroid.sendCannotFoundApkMsg();
			else
				SendMsgToAndroid.sendDownloadWidgetMsg();
			return true;
		}
		return false;
	}
	
	public static boolean downloadWithoutCheckVersion(
			String title ,
			String apkName ,
			String pkgName ,
			String customID )
	{
		boolean checkInfo = true;
		if( DLManager.getInstance().isAPKExist( apkName ) )
		{
			installAPK( iLoongApplication.getDownloadPath() + "/" + apkName );
			return true;
		}
		else if( DLManager.getInstance().HaveDownLoad( pkgName ) )
		{
			iLoongLauncher.getInstance();
			showInfo( null , title + iLoongLauncher.languageSpace + R3D.getString( RR.string.app_downloading ) );
			return true;
		}
		if( checkInfo )
		{
			DLManager.getInstance().DownloadWidget( title , apkName , pkgName , customID , getInstance() );
			return true;
		}
		return false;
	}
	
	public static boolean inAssertDir(
			String apkName )
	{
		String[] apks = null;
		try
		{
			apks = iLoongLauncher.getInstance().getAssets().list( "apk" );
		}
		catch( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		for( int i = 0 ; i < apks.length ; i++ )
		{
			if( apks[i].equals( apkName ) )
				return true;
		}
		return false;
	}
	
	public static String getAssertAPKPath(
			String apkName )
	{
		File dir = new File( Environment.getExternalStorageDirectory() + "/cooee/assert/" );
		dir.mkdirs();
		File tmp = new File( Environment.getExternalStorageDirectory() + "/cooee/assert/" + apkName );
		if( tmp.exists() )
			tmp.delete();
		try
		{
			tmp.createNewFile();
		}
		catch( IOException e1 )
		{
			e1.printStackTrace();
			return null;
		}
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		try
		{
			bis = new BufferedInputStream( ThemeManager.getInstance().getInputStream( "apk/" + apkName ) );
			fos = new FileOutputStream( tmp );
			while( ( size = bis.read( buf ) ) != -1 )
				fos.write( buf , 0 , size );
			fos.close();
			bis.close();
			return tmp.getAbsolutePath();
		}
		catch( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean CheckAppIsDownloaded(
			String path )
	{
		boolean res = false;
		Intent intent = new Intent( Intent.ACTION_VIEW );
		intent.setDataAndType( Uri.fromFile( new File( path ) ) , "application/vnd.android.package-archive" );
		iLoongLauncher.getInstance().startActivity( intent );
		return res;
	}
	
	@Override
	public void OnError(
			String ApkName ,
			String packname ,
			String errmsg )
	{
		// TODO Auto-generated method stub
		String title = DefaultLayout.GetDefaultWidgetNameWithPkgName( packname );
		new AlertDialog.Builder( iLoongLauncher.getInstance() ).setTitle( title ).setMessage( errmsg ).setPositiveButton( iLoongLauncher.getInstance().getString( RR.string.circle_ok_action ) , null )
				.show();
	}
	
	@Override
	public void OnDownLoadComplete(
			String ApkName ,
			String packnam )
	{
		// TODO Auto-generated method stub
		String paramString = DownLoadPath + "/" + ApkName;
		Intent localIntent = new Intent( Intent.ACTION_VIEW );
		Uri localUri = Uri.parse( "file://" + paramString );
		String str = MimeTypeMap.getSingleton().getMimeTypeFromExtension( MimeTypeMap.getFileExtensionFromUrl( paramString ) );
		if( ( str == null ) && ( paramString.toLowerCase().endsWith( "apk" ) ) )
			str = APK_MIMETYPE;
		if( str != null )
			localIntent.setDataAndType( localUri , str );
		try
		{
			iLoongLauncher.getInstance().startActivity( localIntent );
		}
		catch( Exception e )
		{
		}
	}
	
	@Override
	public void OnCheckedComplete(
			String ApkName ,
			String packname ,
			int flag ,
			String VersionCode ,
			String VersionName )
	{
		// TODO Auto-generated method stub
		if( flag == 0 )
		{
			PackageInfo info = null;
			PackageManager pm = iLoongApplication.getInstance().getPackageManager();
			info = pm.getPackageArchiveInfo( DownLoadPath + "/" + ApkName , PackageManager.GET_SIGNATURES );
			if( info.versionCode >= Integer.valueOf( VersionCode ) )
			{
				installAPK( iLoongApplication.getDownloadPath() + "/" + ApkName );
			}
			else
			{
				if( Widget3DManager.curDownload != null )
					SendMsgToAndroid.sendDownloadWidgetMsg();
				else
					Log.v( "test" , "WidgetDownload OnCheckedComplete curDownload is null" );
			}
		}
		else
		{
			Log.v( "test" , "WidgetDownload OnCheckedComplete flag in unnormol" );
			checkToDownload( Widget3DManager.curDownload , false );
		}
	}
}
