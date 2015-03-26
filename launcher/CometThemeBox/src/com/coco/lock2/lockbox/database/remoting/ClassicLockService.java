package com.coco.lock2.lockbox.database.remoting;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.coco.lock2.lockbox.database.model.LockInfoItem;


public class ClassicLockService
{
	
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_APPLICATION_NAME = "applicationName";
	public static final String FIELD_VERSION_CODE = "versionCode";
	public static final String FIELD_VERSION_NAME = "versionName";
	public static final String FIELD_APPLICATION_SIZE = "applicationSize";
	public static final String FIELD_AUTHOR = "author";
	public static final String FIELD_INTRODUCTION = "introduction";
	public static final String FIELD_UPDATE_TIME = "updateTime";
	public static final String FIELD_APPLICATION_NAME_EN = "applicationName_en";
	public static final String FIELD_INTRODUCTION_EN = "introduction_en";
	public static final String TABLE_NAME = "classicLock";
	public static final Uri CONTENT_URI = Uri.parse( "content://com.coco.lock2.lockbox/" + TABLE_NAME );
	private Context mContext;
	
	public ClassicLockService(
			Context context )
	{
		mContext = context;
	}
	
	public List<LockInfoItem> queryTable()
	{
		ArrayList<LockInfoItem> list = new ArrayList<LockInfoItem>();
		Cursor cursor = null;
		try
		{
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query( CONTENT_URI , null , null , null , null );
			if( cursor != null )
			{
				while( cursor.moveToNext() )
				{
					LockInfoItem item = readLockInfo( cursor );
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
	
	public LockInfoItem queryByPackageName(
			String packageName )
	{
		LockInfoItem result = null;
		Cursor cursor = null;
		try
		{
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query( CONTENT_URI , null , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } , null );
			if( cursor == null )
			{
				return null;
			}
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
	
	public boolean batchInsert(
			List<LockInfoItem> lockList )
	{
		ContentValues[] cvs = new ContentValues[lockList.size()];
		for( int i = 0 ; i < cvs.length ; i++ )
		{
			ContentValues cv = new ContentValues();
			cvs[i] = cv;
			LockInfoItem info = lockList.get( i );
			cv.put( FIELD_PACKAGE_NAME , info.getPackageName() );
			cv.put( FIELD_APPLICATION_NAME , info.getApplicationName() );
			cv.put( FIELD_VERSION_CODE , info.getVersionCode() );
			cv.put( FIELD_VERSION_NAME , info.getVersionName() );
			cv.put( FIELD_APPLICATION_SIZE , info.getApplicationSize() );
			cv.put( FIELD_AUTHOR , info.getAuthor() );
			cv.put( FIELD_INTRODUCTION , info.getIntroduction() );
			cv.put( FIELD_UPDATE_TIME , info.getUpdateTime() );
			cv.put( FIELD_APPLICATION_NAME_EN , info.getApplicationName_en() );
			cv.put( FIELD_INTRODUCTION_EN , info.getIntroduction_en() );
		}
		ContentResolver contentResolver = mContext.getContentResolver();
		int result = contentResolver.bulkInsert( CONTENT_URI , cvs );
		if( result > 0 )
		{
			return true;
		}
		return false;
	}
	
	public void clearTable()
	{
		ContentResolver contentResolver = mContext.getContentResolver();
		contentResolver.delete( CONTENT_URI , null , null );
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
	
	private LockInfoItem readLockInfo(
			Cursor cursor )
	{
		try
		{
			LockInfoItem item = new LockInfoItem();
			item.setPackageName( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_PACKAGE_NAME ) ) );
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
