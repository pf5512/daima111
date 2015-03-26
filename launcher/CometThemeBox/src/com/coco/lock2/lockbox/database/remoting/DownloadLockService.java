package com.coco.lock2.lockbox.database.remoting;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.coco.lock2.lockbox.database.model.DownloadLockItem;
import com.coco.lock2.lockbox.database.model.DownloadStatus;


public class DownloadLockService
{
	
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_APPLICATION_NAME = "applicationName";
	public static final String FIELD_VERSION_CODE = "versionCode";
	public static final String FIELD_VERSION_NAME = "versionName";
	public static final String FIELD_APPLICATION_SIZE = "applicationSize";
	public static final String FIELD_AUTHOR = "author";
	public static final String FIELD_INTRODUCTION = "introduction";
	public static final String FIELD_UPDATE_TIME = "updateTime";
	public static final String FIELD_DOWNLOAD_SIZE = "downloadSize";
	public static final String FIELD_DOWNLOAD_STATUS = "downloadStatus";
	public static final String FIELD_APPLICATION_NAME_EN = "applicationName_en";
	public static final String FIELD_INTRODUCTION_EN = "introduction_en";
	public static final String TABLE_NAME = "downloadLock";
	public static final Uri CONTENT_URI = Uri.parse( "content://com.coco.lock2.lockbox/" + TABLE_NAME );
	private Context mContext;
	
	public static String getCreateSql()
	{
		String result = String.format(
				"CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT);" ,
				TABLE_NAME ,
				FIELD_PACKAGE_NAME ,
				FIELD_DOWNLOAD_SIZE ,
				FIELD_DOWNLOAD_STATUS ,
				FIELD_APPLICATION_NAME ,
				FIELD_VERSION_CODE ,
				FIELD_VERSION_NAME ,
				FIELD_APPLICATION_SIZE ,
				FIELD_AUTHOR ,
				FIELD_INTRODUCTION ,
				FIELD_UPDATE_TIME );
		return result;
	}
	
	public static String getDropSql()
	{
		String result = "DROP TABLE IF EXISTS " + TABLE_NAME;
		return result;
	}
	
	public DownloadLockService(
			Context context )
	{
		mContext = context;
	}
	
	public List<DownloadLockItem> queryTable()
	{
		ArrayList<DownloadLockItem> list = new ArrayList<DownloadLockItem>();
		Cursor cursor = null;
		try
		{
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query( CONTENT_URI , null , null , null , null );
			if( cursor != null )
			{
				while( cursor.moveToNext() )
				{
					DownloadLockItem item = readLockInfo( cursor );
					if( item == null )
					{
						break;
					}
					list.add( item );
				}
			}
		}
		finally
		{
			if( cursor != null )
			{
				cursor.close();
			}
		}
		return list;
	}
	
	public boolean insertItem(
			DownloadLockItem item )
	{
		ContentValues cv = new ContentValues();
		cv.put( FIELD_PACKAGE_NAME , item.getPackageName() );
		cv.put( FIELD_DOWNLOAD_SIZE , item.getDownloadSize() );
		cv.put( FIELD_DOWNLOAD_STATUS , item.getDownloadStatus().getValue() );
		cv.put( FIELD_APPLICATION_NAME , item.getApplicationName() );
		cv.put( FIELD_VERSION_CODE , item.getVersionCode() );
		cv.put( FIELD_VERSION_NAME , item.getVersionName() );
		cv.put( FIELD_APPLICATION_SIZE , item.getApplicationSize() );
		cv.put( FIELD_AUTHOR , item.getAuthor() );
		cv.put( FIELD_INTRODUCTION , item.getIntroduction() );
		cv.put( FIELD_UPDATE_TIME , item.getUpdateTime() );
		cv.put( FIELD_APPLICATION_NAME_EN , item.getApplicationName_en() );
		cv.put( FIELD_INTRODUCTION_EN , item.getIntroduction_en() );
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri result = contentResolver.insert( CONTENT_URI , cv );
		if( result == null )
		{
			return false;
		}
		return true;
	}
	
	public DownloadLockItem queryByPackageName(
			String packageName )
	{
		DownloadLockItem result = null;
		Cursor cursor = null;
		try
		{
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query( CONTENT_URI , null , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } , null );
			if( cursor.moveToFirst() )
			{
				result = readLockInfo( cursor );
			}
		}
		finally
		{
			if( cursor != null )
			{
				cursor.close();
			}
		}
		return result;
	}
	
	public boolean updateDownloadSizeAndStatus(
			String packageName ,
			int size ,
			DownloadStatus status )
	{
		ContentValues cv = new ContentValues();
		cv.put( FIELD_DOWNLOAD_SIZE , size );
		cv.put( FIELD_DOWNLOAD_STATUS , status.getValue() );
		ContentResolver contentResolver = mContext.getContentResolver();
		int updateResult = contentResolver.update( CONTENT_URI , cv , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		if( updateResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	public boolean updateDownloadSizeAndStatus(
			String packageName ,
			int downSize ,
			int totalSize ,
			DownloadStatus status )
	{
		ContentValues cv = new ContentValues();
		cv.put( FIELD_DOWNLOAD_SIZE , downSize );
		cv.put( FIELD_APPLICATION_SIZE , totalSize );
		cv.put( FIELD_DOWNLOAD_STATUS , status.getValue() );
		ContentResolver contentResolver = mContext.getContentResolver();
		int updateResult = contentResolver.update( CONTENT_URI , cv , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		if( updateResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	public boolean updateDownloadStatus(
			String packageName ,
			DownloadStatus status )
	{
		ContentValues cv = new ContentValues();
		cv.put( FIELD_DOWNLOAD_STATUS , status.getValue() );
		ContentResolver contentResolver = mContext.getContentResolver();
		int updateResult = contentResolver.update( CONTENT_URI , cv , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		if( updateResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	public boolean deleteItem(
			String packageName )
	{
		ContentResolver contentResolver = mContext.getContentResolver();
		int delResult = contentResolver.delete( CONTENT_URI , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		if( delResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	public void clearTable()
	{
		ContentResolver contentResolver = mContext.getContentResolver();
		contentResolver.delete( CONTENT_URI , null , null );
	}
	
	private DownloadLockItem readLockInfo(
			Cursor cursor )
	{
		try
		{
			DownloadLockItem item = new DownloadLockItem();
			item.setPackageName( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_PACKAGE_NAME ) ) );
			item.setDownloadSize( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_DOWNLOAD_SIZE ) ) );
			item.setDownloadStatus( DownloadStatus.fromValue( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_DOWNLOAD_STATUS ) ) ) );
			item.setApplicationName( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_APPLICATION_NAME ) ) );
			item.setVersionCode( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_VERSION_CODE ) ) );
			item.setVersionName( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_VERSION_NAME ) ) );
			item.setApplicationSize( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_APPLICATION_SIZE ) ) );
			item.setAuthor( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_AUTHOR ) ) );
			item.setIntroduction( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_INTRODUCTION ) ) );
			item.setUpdateTime( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_UPDATE_TIME ) ) );
			item.setApplicationName_en( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_APPLICATION_NAME_EN ) ) );
			item.setIntroduction_en( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_INTRODUCTION_EN ) ) );
			return item;
		}
		catch( IllegalArgumentException ex )
		{
			ex.printStackTrace();
			return null;
		}
	}
}
