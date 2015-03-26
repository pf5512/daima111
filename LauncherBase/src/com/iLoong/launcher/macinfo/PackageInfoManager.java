package com.iLoong.launcher.macinfo;


import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.iLoong.launcher.Desktop3D.Log;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;


public class PackageInfoManager
{
	
	private static Context mContext;
	private static PackageManager pManager;
	private static HashMap<String , PackageInfoEx> items = new HashMap<String , PackageInfoEx>();
	
	// public PackageInfoManager(Context context) {
	// mContext = context;
	// pManager = mContext.getPackageManager();
	// items = new HashMap<String, PackageInfoEx>();
	// }
	public static void initPackageInfoManager(
			Context context )
	{
		mContext = context;
		pManager = mContext.getPackageManager();
	}
	
	/**
	 * ��������Ѱ�װ��apk��Ϣ
	 * 
	 * @param flags
	 * @return
	 */
	public static List<PackageInfo> getInstalledPackages(
			int flags )
	{
		return pManager.getInstalledPackages( flags );
	}
	
	/**
	 * ͨ��packageName���ĳһ��apk�İ�װ��Ϣ
	 * 
	 * @param packageName
	 * @param flags
	 * @return
	 */
	public static PackageInfo getPackageInfo(
			String packageName ,
			int flags )
	{
		try
		{
			return pManager.getPackageInfo( packageName , flags );
		}
		catch( NameNotFoundException e )
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ͨ��packageName����Լ������PackageInfoEx��Ϣ�������¼��apk�Ļ���Ϣ
	 * 
	 * @param packageName
	 * @return
	 */
	public static PackageInfoEx getPackageInfoEx(
			String packageName )
	{
		if( items != null && items.containsKey( packageName ) )
		{
			return items.get( packageName );
		}
		PackageInfoEx item = new PackageInfoEx();
		PackageInfo pInfo = getPackageInfo( packageName , 0 );
		if( pInfo != null )
		{
			item.setPackageInfo( pInfo );
			String appName = pManager.getApplicationLabel( pInfo.applicationInfo ).toString();
			item.setAppName( appName );
			String pName = pInfo.packageName.toString();
			item.setPackgeName( pName );
			Drawable icon = pManager.getApplicationIcon( pInfo.applicationInfo );
			item.setIcon( icon );
			String appDir = pInfo.applicationInfo.publicSourceDir;
			int appSize = Integer.valueOf( (int)new File( appDir ).length() );
			item.setSize( appSize );
			if( appDir.substring( appDir.indexOf( "/" ) + 1 , appDir.indexOf( "/" , 1 ) ).equals( "mnt" ) )
			{
				item.setLocaltion( "��װλ�ã�SDcard" );
			}
			else
			{
				item.setLocaltion( "��װλ�ã��ֻ��ڴ�" );
			}
			item.setVersionCode( pInfo.versionCode );
			item.setVersionName( pInfo.versionName );
			items.put( packageName , item );
			return item;
		}
		return null;
	}
	
	public static void installApp(
			String packageName ,
			String fileName )
	{
		if( packageName == null )
			return;
		PackageManager pManager;
		pManager = mContext.getPackageManager();
		Intent intent = pManager.getLaunchIntentForPackage( "com.cooee.InstallCallback" );
		if( intent == null )
		{
			Log.i( "install error" , "û�о�Ĭ��װ����" );
			return;
		}
		intent.putExtra( "key" , "cooee312" );
		intent.putExtra( "fileName" , fileName );
		intent.putExtra( "packageName" , packageName );
		if( intent != null )
		{
			mContext.startActivity( intent );
		}
	}
	
	/**
	 * ͨ��PackageInfo����Լ������PackageInfoEx��Ϣ
	 * 
	 * @param pInfo
	 * @return
	 */
	public static PackageInfoEx getPackageInfoEx(
			PackageInfo pInfo )
	{
		if( items != null && items.containsKey( pInfo.packageName.toString() ) )
		{
			return items.get( pInfo.packageName.toString() );
		}
		PackageInfoEx item = new PackageInfoEx();
		if( pInfo != null )
		{
			item.setPackageInfo( pInfo );
			String appName = pManager.getApplicationLabel( pInfo.applicationInfo ).toString();
			item.setAppName( appName );
			String pName = pInfo.packageName.toString();
			item.setPackgeName( pName );
			Drawable icon = pManager.getApplicationIcon( pInfo.applicationInfo );
			item.setIcon( icon );
			String appDir = pInfo.applicationInfo.publicSourceDir;
			long appSize = Long.valueOf( new File( appDir ).length() );
			// String size = sizeFormat(appSize);
			item.setSize( appSize );
			if( appDir.substring( appDir.indexOf( "/" ) + 1 , appDir.indexOf( "/" , 1 ) ).equals( "mnt" ) )
			{
				item.setLocaltion( "��װλ�ã�SDcard" );
			}
			else
			{
				item.setLocaltion( "��װλ�ã��ֻ��ڴ�" );
			}
			item.setVersionCode( pInfo.versionCode );
			item.setVersionName( pInfo.versionName );
			items.put( pName , item );
			return item;
		}
		return null;
	}
	
	public static String getInstallApkInfo()
	{
		List<PackageInfo> appList = PackageInfoManager.getInstalledPackages( 0 );
		ArrayList<PackageInfo> apkInfoList = new ArrayList<PackageInfo>();
		for( int i = 0 ; i < appList.size() ; i++ )
		{
			PackageInfo pak = (PackageInfo)appList.get( i );
			// �ж��Ƿ�Ϊ��ϵͳԤװ��Ӧ�ó���
			if( ( pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) <= 0 )
			{
				apkInfoList.add( pak );
			}
		}
		ListIterator<PackageInfo> it = (ListIterator<PackageInfo>)apkInfoList.listIterator();
		StringBuffer sBuffer = new StringBuffer();
		while( it.hasNext() )
		{
			PackageInfo pInfo = it.next();
			sBuffer.append( pInfo.packageName );
			sBuffer.append( "|" );
			sBuffer.append( pInfo.versionCode );
			sBuffer.append( "," );
		}
		return sBuffer.toString();
	}
	
	/**
	 * ����Ӧ��
	 * 
	 * @param packageName
	 */
	public static void startApp(
			String packageName )
	{
		if( packageName == null )
			return;
		Intent intent = pManager.getLaunchIntentForPackage( packageName );
		if( intent != null )
		{
			mContext.startActivity( intent );
		}
	}
	
	private static void updateCacheItem(
			String packageName )
	{
		PackageInfo pInfo = getPackageInfo( packageName , 0 );
		PackageInfoEx item = new PackageInfoEx();
		if( pInfo != null )
		{
			item.setPackageInfo( pInfo );
			String appName = pManager.getApplicationLabel( pInfo.applicationInfo ).toString();
			item.setAppName( appName );
			String pName = pInfo.packageName.toString();
			item.setPackgeName( pName );
			Drawable icon = pManager.getApplicationIcon( pInfo.applicationInfo );
			item.setIcon( icon );
			String appDir = pInfo.applicationInfo.publicSourceDir;
			long appSize = Long.valueOf( new File( appDir ).length() );
			// String size = sizeFormat(appSize);
			item.setSize( appSize );
			if( appDir.substring( appDir.indexOf( "/" ) + 1 , appDir.indexOf( "/" , 1 ) ).equals( "mnt" ) )
			{
				item.setLocaltion( "��װλ�ã�SDcard" );
			}
			else
			{
				item.setLocaltion( "��װλ�ã��ֻ��ڴ�" );
			}
			item.setVersionCode( pInfo.versionCode );
			item.setVersionName( pInfo.versionName );
			items.put( packageName , item );
		}
	}
	
	/**
	 * ͨ��packageName����HashMap��������cache
	 * 
	 * @param packageName
	 */
	public static void onUpdateCacheItem(
			String packageName )
	{
		updateCacheItem( packageName );
	}
	
	public static void removeCacheItem(
			String packageName )
	{
		items.remove( packageName );
	}
	
	/**
	 * ͨ��packageNameɾ��ĳһ��
	 * 
	 * @param packageName
	 */
	public static void onRemoveCacheItem(
			String packageName )
	{
		removeCacheItem( packageName );
	}
	
	private static void addCacheItem(
			String packageName )
	{
		updateCacheItem( packageName );
	}
	
	/**
	 * ͨ��packageName����һ��
	 * 
	 * @param packageName
	 */
	public static void onAddCacheItem(
			String packageName )
	{
		addCacheItem( packageName );
	}
	
	private static String sizeFormat(
			long size )
	{
		DecimalFormat df = new DecimalFormat( "###.##" );
		float f;
		if( size < 1024 * 1024 )
		{
			f = (float)( (float)size / (float)1024 );
			return( df.format( new Float( f ).doubleValue() ) + "KB" );
		}
		else
		{
			f = (float)( (float)size / (float)( 1024 * 1024 ) );
			return( df.format( new Float( f ).doubleValue() ) + "MB" );
		}
	}
}
