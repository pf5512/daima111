package com.coco.theme.themebox.database.service;


import java.util.ArrayList;
import java.util.List;

import com.coco.theme.themebox.database.DbHelper;
import com.coco.theme.themebox.database.model.ThemeInfoItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class HotThemeService
{
	
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_APPLICATION_NAME = "applicationName";
	public static final String FIELD_VERSION_CODE = "versionCode";
	public static final String FIELD_VERSION_NAME = "versionName";
	public static final String FIELD_APPLICATION_SIZE = "applicationSize";
	public static final String FIELD_AUTHOR = "author";
	public static final String FIELD_INTRODUCTION = "introduction";
	public static final String FIELD_UPDATE_TIME = "updateTime";
	public static final String TABLE_NAME = "hotTheme";
	private DbHelper dbHelper;
	
	public static String getCreateSql()
	{
		String result = String.format(
				"CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT);" ,
				TABLE_NAME ,
				FIELD_PACKAGE_NAME ,
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
	
	public HotThemeService(
			Context context )
	{
		dbHelper = new DbHelper( context );
	}
	
	public List<ThemeInfoItem> queryTable()
	{
		ArrayList<ThemeInfoItem> list = new ArrayList<ThemeInfoItem>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , null , null , null , null , null );
			while( cursor.moveToNext() )
			{
				ThemeInfoItem item = readLockInfo( cursor );
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
		}
		return list;
	}
	
	public ThemeInfoItem queryByPackageName(
			String packageName )
	{
		ThemeInfoItem result = null;
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
		}
		return result;
	}
	
	public boolean batchInsert(
			List<ThemeInfoItem> lockList )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try
		{
			db.beginTransaction();
			ContentValues cv = new ContentValues();
			for( ThemeInfoItem info : lockList )
			{
				cv.put( FIELD_PACKAGE_NAME , info.getPackageName() );
				cv.put( FIELD_APPLICATION_NAME , info.getApplicationName() );
				cv.put( FIELD_VERSION_CODE , info.getVersionCode() );
				cv.put( FIELD_VERSION_NAME , info.getVersionName() );
				cv.put( FIELD_APPLICATION_SIZE , info.getApplicationSize() );
				cv.put( FIELD_AUTHOR , info.getAuthor() );
				cv.put( FIELD_INTRODUCTION , info.getIntroduction() );
				cv.put( FIELD_UPDATE_TIME , info.getUpdateTime() );
				long insertResult = db.insert( TABLE_NAME , null , cv );
				if( insertResult == -1 )
				{
					return false;
				}
			}
			db.setTransactionSuccessful();
			return true;
		}
		finally
		{
			db.endTransaction();
			db.close();
			DbHelper.lock_db = false;
		}
	}
	
	public void clearTable()
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete( TABLE_NAME , null , null );
		db.close();
		DbHelper.lock_db = false;
	}
	
	public boolean deleteItem(
			String packageName )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int delResult = db.delete( TABLE_NAME , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } );
		db.close();
		DbHelper.lock_db = false;
		if( delResult <= 0 )
		{
			return false;
		}
		return true;
	}
	
	private ThemeInfoItem readLockInfo(
			Cursor cursor )
	{
		try
		{
			ThemeInfoItem item = new ThemeInfoItem();
			item.setPackageName( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_PACKAGE_NAME ) ) );
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
