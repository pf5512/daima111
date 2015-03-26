package com.coco.lock2.lockbox.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.coco.lock2.lockbox.LockInformation;
import com.coco.lock2.lockbox.StaticClass;
import com.coco.lock2.lockbox.database.model.DownloadLockItem;
import com.coco.lock2.lockbox.database.model.DownloadStatus;
import com.coco.lock2.lockbox.database.model.LockInfoItem;
import com.coco.lock2.lockbox.database.remoting.ClassicLockService;
import com.coco.lock2.lockbox.database.remoting.DownloadLockService;
import com.coco.lock2.lockbox.database.remoting.HotLockService;
import com.coco.lock2.lockbox.database.remoting.SimpleLockService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;


public class LockManager
{
	
	private static final Uri CURENT_LOCK_URI = Uri.parse( "content://" + StaticClass.LOCKBOX_PACKAGE_NAME + "/currentLock" );
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_CLASS_NAME = "className";
	private Context mContext;
	
	public LockManager(
			Context context )
	{
		mContext = context;
	}
	
	public List<LockInformation> queryInstallList()
	{
		Intent intentLockView = new Intent( StaticClass.ACTION_LOCK_VIEW );
		intentLockView.addCategory( Intent.CATEGORY_INFO );
		List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLockView , 0 );
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( ResolveInfo info : infoList )
		{
			LockInformation infor = new LockInformation();
			infor.setActivity( mContext , info.activityInfo );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryShowList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		List<LockInformation> hotList = queryHotList();
		for( LockInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> downList = queryDownloadList();
		for( LockInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> installList = queryInstallList();
		for( LockInformation item : installList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> resultList = new ArrayList<LockInformation>();
		for( LockInformation item : hotList )
		{
			resultList.add( allMap.get( item.getPackageName() ) );
		}
		return resultList;
	}
	
	public List<LockInformation> queryDownloadList()
	{
		DownloadLockService dSv = new DownloadLockService( mContext );
		List<DownloadLockItem> downlist = dSv.queryTable();
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( DownloadLockItem item : downlist )
		{
			LockInformation infor = new LockInformation();
			infor.setDownloadItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryHotList()
	{
		HotLockService hotSv = new HotLockService( mContext );
		List<LockInfoItem> hotList = hotSv.queryTable();
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( LockInfoItem item : hotList )
		{
			LockInformation infor = new LockInformation();
			infor.setLockItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryShowHotList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		List<LockInformation> hotList = queryHotList();
		for( LockInformation item : hotList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> downList = queryDownloadList();
		for( LockInformation item : downList )
		{
			allMap.put( item.getPackageName() , item );
			if( item.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				allMap.remove( item.getPackageName() );
			}
		}
		List<LockInformation> installList = queryInstallList();
		for( LockInformation item : installList )
		{
			allMap.remove( item.getPackageName() );
		}
		List<LockInformation> resultList = new ArrayList<LockInformation>();
		for( LockInformation item : hotList )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				resultList.add( allMap.get( item.getPackageName() ) );
			}
		}
		//		resultList.addAll(allMap.values());
		return resultList;
	}
	
	public List<LockInformation> queryDownFinishList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		Map<String , LockInformation> installMap = new HashMap<String , LockInformation>();
		DownloadLockService dSv = new DownloadLockService( mContext );
		List<DownloadLockItem> downlist = dSv.queryTable();
		List<LockInformation> installlist = queryInstallList();
		for( DownloadLockItem item : downlist )
		{
			LockInformation infor = new LockInformation();
			infor.setDownloadItem( item );
			if( infor.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				allMap.put( infor.getPackageName() , infor );
			}
		}
		for( LockInformation item : installlist )
		{
			installMap.put( item.getPackageName() , item );
		}
		for( LockInformation item : installlist )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				allMap.remove( item.getPackageName() );
			}
		}
		List<LockInformation> result = new ArrayList<LockInformation>();
		result.addAll( allMap.values() );
		return result;
	}
	
	public List<LockInformation> queryShowSimpleList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		List<LockInformation> simpleList = querySimpleList();
		for( LockInformation item : simpleList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> downList = queryDownloadList();
		for( LockInformation item : downList )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				allMap.put( item.getPackageName() , item );
			}
			if( item.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				allMap.remove( item.getPackageName() );
			}
		}
		List<LockInformation> installList = queryInstallList();
		for( LockInformation item : installList )
		{
			allMap.remove( item.getPackageName() );
		}
		List<LockInformation> resultList = new ArrayList<LockInformation>();
		for( LockInformation item : simpleList )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				resultList.add( allMap.get( item.getPackageName() ) );
			}
		}
		return resultList;
	}
	
	public List<LockInformation> queryShowClassicList()
	{
		Map<String , LockInformation> allMap = new HashMap<String , LockInformation>();
		List<LockInformation> classicList = queryClassicList();
		for( LockInformation item : classicList )
		{
			allMap.put( item.getPackageName() , item );
		}
		List<LockInformation> downList = queryDownloadList();
		for( LockInformation item : downList )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				allMap.put( item.getPackageName() , item );
			}
			if( item.getDownloadStatus() == DownloadStatus.StatusFinish )
			{
				allMap.remove( item.getPackageName() );
			}
		}
		List<LockInformation> installList = queryInstallList();
		for( LockInformation item : installList )
		{
			allMap.remove( item.getPackageName() );
		}
		List<LockInformation> resultList = new ArrayList<LockInformation>();
		//		resultList.addAll(allMap.values());
		for( LockInformation item : classicList )
		{
			if( allMap.containsKey( item.getPackageName() ) )
			{
				resultList.add( allMap.get( item.getPackageName() ) );
			}
		}
		return resultList;
	}
	
	public ComponentName queryCurrentLock()
	{
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query( CURENT_LOCK_URI , null , null , null , null );
		if( cursor == null )
		{
			return new ComponentName( "" , "" );
		}
		if( !cursor.moveToFirst() )
		{
			cursor.close();
			return new ComponentName( "" , "" );
		}
		String pkgName = cursor.getString( cursor.getColumnIndex( FIELD_PACKAGE_NAME ) );
		String clsName = cursor.getString( cursor.getColumnIndex( FIELD_CLASS_NAME ) );
		cursor.close();
		return new ComponentName( pkgName , clsName );
	}
	
	public LockInformation queryLock(
			String packageName ,
			String className )
	{
		if( className != null && !className.equals( "" ) )
		{
			Intent intentLock = new Intent( StaticClass.ACTION_LOCK_VIEW );
			intentLock.addCategory( Intent.CATEGORY_INFO );
			intentLock.setPackage( packageName );
			List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLock , 0 );
			for( ResolveInfo item : infoList )
			{
				if( item.activityInfo.packageName.equals( packageName ) && item.activityInfo.name.equals( className ) )
				{
					LockInformation infor = new LockInformation();
					infor.setActivity( mContext , item.activityInfo );
					return infor;
				}
			}
		}
		DownloadLockService dSv = new DownloadLockService( mContext );
		DownloadLockItem downItem = dSv.queryByPackageName( packageName );
		if( downItem != null )
		{
			LockInformation infor = new LockInformation();
			infor.setDownloadItem( downItem );
			return infor;
		}
		HotLockService hotSv = new HotLockService( mContext );
		LockInfoItem hotItem = hotSv.queryByPackageName( packageName );
		if( hotItem != null )
		{
			LockInformation infor = new LockInformation();
			infor.setLockItem( hotItem );
			return infor;
		}
		return null;
	}
	
	public List<LockInformation> querySimpleList()
	{
		SimpleLockService simpleSv = new SimpleLockService( mContext );
		List<LockInfoItem> hotList = simpleSv.queryTable();
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( LockInfoItem item : hotList )
		{
			LockInformation infor = new LockInformation();
			infor.setLockItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public List<LockInformation> queryClassicList()
	{
		ClassicLockService classicSv = new ClassicLockService( mContext );
		List<LockInfoItem> hotList = classicSv.queryTable();
		List<LockInformation> result = new ArrayList<LockInformation>();
		for( LockInfoItem item : hotList )
		{
			LockInformation infor = new LockInformation();
			infor.setLockItem( item );
			result.add( infor );
		}
		return result;
	}
	
	public ComponentName queryComponent(
			String packageName )
	{
		Intent intentLock = new Intent( StaticClass.ACTION_LOCK_VIEW );
		intentLock.addCategory( Intent.CATEGORY_INFO );
		intentLock.setPackage( packageName );
		List<ResolveInfo> infoList = mContext.getPackageManager().queryIntentActivities( intentLock , 0 );
		if( infoList != null )
		{
			if( infoList.size() == 0 )
			{
				return null;
			}
			return null;
		}
		ResolveInfo item = infoList.get( 0 );
		return new ComponentName( packageName , item.activityInfo.name );
	}
}
