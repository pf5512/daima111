package com.coco.theme.themebox.database.service;


import java.util.ArrayList;
import java.util.List;

import com.coco.theme.themebox.database.DbHelper;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DownloadThemeService
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
	public static final String TABLE_NAME = "downloadTheme";
	private DbHelper dbHelper;
	
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
	
	public DownloadThemeService(
			Context context )
	{
		dbHelper = new DbHelper( context );
	}
	
	public List<DownloadThemeItem> queryTable()
	{
		ArrayList<DownloadThemeItem> list = new ArrayList<DownloadThemeItem>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , null , null , null , null , null );
			while( cursor.moveToNext() )
			{
				DownloadThemeItem item = readLockInfo( cursor );
				if( item == null )
				{
					break;
				}
				list.add( item );
			}
		}
		finally
		{
			if( cursor != null )
			{
				cursor.close();
			}
			db.close();
			DbHelper.lock_db = false;
			DbHelper.lock_db = false;
		}
		return list;
	}
	
	public boolean insertItem(
			DownloadThemeItem item )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
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
		long insertResult = db.insert( TABLE_NAME , null , cv );
		db.close();
		DbHelper.lock_db = false;
		DbHelper.lock_db = false;
		if( insertResult == -1 )
		{
			return false;
		}
		return true;
	}
	
	public DownloadThemeItem queryByPackageName(
			String packageName )
	{
		DownloadThemeItem result = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } , null , null , null );
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
			db.close();
			DbHelper.lock_db = false;
			DbHelper.lock_db = false;
		}
		return result;
	}
	
	public boolean updateDownloadSizeAndStatus(
			String packageName ,
			int size ,
			DownloadStatus status )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put( FIELD_DOWNLOAD_SIZE , size );
		cv.put( FIELD_DOWNLOAD_STATUS , status.getValue() );
		int updateResult = db.update( TABLE_NAME , cv , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		db.close();
		DbHelper.lock_db = false;
		DbHelper.lock_db = false;
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
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put( FIELD_DOWNLOAD_SIZE , downSize );
		cv.put( FIELD_APPLICATION_SIZE , totalSize );
		cv.put( FIELD_DOWNLOAD_STATUS , status.getValue() );
		int updateResult = db.update( TABLE_NAME , cv , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		db.close();
		DbHelper.lock_db = false;
		DbHelper.lock_db = false;
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
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put( FIELD_DOWNLOAD_STATUS , status.getValue() );
		int updateResult = db.update( TABLE_NAME , cv , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		db.close();
		DbHelper.lock_db = false;
		DbHelper.lock_db = false;
		if( updateResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	public boolean deleteItem(
			String packageName )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int delResult = db.delete( TABLE_NAME , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		db.close();
		DbHelper.lock_db = false;
		DbHelper.lock_db = false;
		if( delResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	public void clearTable()
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete( TABLE_NAME , null , null );
		db.close();
		DbHelper.lock_db = false;
		DbHelper.lock_db = false;
	}
	
	private DownloadThemeItem readLockInfo(
			Cursor cursor )
	{
		try
		{
			DownloadThemeItem item = new DownloadThemeItem();
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
			return item;
		}
		catch( IllegalArgumentException ex )
		{
			ex.printStackTrace();
			return null;
		}
	}
}
