package com.coco.theme.themebox.database.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coco.theme.themebox.database.DbHelper;
import com.coco.theme.themebox.database.model.ThemeInfoItem;


public class ConfigurationTabService
{
	
	public static final String TABLE_NAME = "configurationTab";
	public static final String FIELD_TYPE = "tabid";
	public static final String FIELD_ENNAME = "enname";
	public static final String FIELD_CNNAME = "cnname";
	public static final String FIELD_TWNAME = "twname";
	private DbHelper dbHelper;
	
	public static String getCreateSql()
	{
		String result = String.format( "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT,  %s TEXT,  %s TEXT );" , TABLE_NAME , FIELD_TYPE , FIELD_ENNAME , FIELD_CNNAME , FIELD_TWNAME );
		return result;
	}
	
	public static String getDropSql()
	{
		String result = "DROP TABLE IF EXISTS " + TABLE_NAME;
		return result;
	}
	
	public ConfigurationTabService(
			Context context )
	{
		dbHelper = new DbHelper( context );
	}
	
	public synchronized List<String> queryTabList()
	{
		ArrayList<String> list = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , null , null , null , null , null , null );
			while( cursor.moveToNext() )
			{
				String item = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_TYPE ) );
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
	
	public synchronized List<Map<String , String>> queryTable()
	{
		ArrayList<Map<String , String>> list = new ArrayList<Map<String , String>>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , null , null , null , null , null , null );
			while( cursor.moveToNext() )
			{
				Map<String , String> item = readTabInfo( cursor );
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
	
	public boolean batchInsert(
			List<Map<String , String>> list )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try
		{
			db.beginTransaction();
			ContentValues cv = new ContentValues();
			for( Map<String , String> info : list )
			{
				cv.put( FIELD_TYPE , info.get( FIELD_TYPE ) );
				cv.put( FIELD_ENNAME , info.get( FIELD_ENNAME ) );
				cv.put( FIELD_CNNAME , info.get( FIELD_CNNAME ) );
				cv.put( FIELD_TWNAME , info.get( FIELD_TWNAME ) );
				long insertResult = db.insert( TABLE_NAME , null , cv );
				if( insertResult == -1 )
				{
					return false;
				}
			}
			db.setTransactionSuccessful();
			return true;
		}
		catch( Exception e )
		{
			db.setTransactionSuccessful();
			e.printStackTrace();
			return false;
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
	
	private Map<String , String> readTabInfo(
			Cursor cursor )
	{
		try
		{
			Map<String , String> item = new HashMap<String , String>();
			item.put( FIELD_TYPE , cursor.getString( cursor.getColumnIndexOrThrow( FIELD_TYPE ) ) );
			item.put( FIELD_ENNAME , cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ENNAME ) ) );
			item.put( FIELD_CNNAME , cursor.getString( cursor.getColumnIndexOrThrow( FIELD_CNNAME ) ) );
			item.put( FIELD_TWNAME , cursor.getString( cursor.getColumnIndexOrThrow( FIELD_TWNAME ) ) );
			return item;
		}
		catch( IllegalArgumentException ex )
		{
			ex.printStackTrace();
			return null;
		}
	}
}
