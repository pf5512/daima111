package com.coco.theme.themebox.database.service;


import java.util.ArrayList;
import java.util.List;

import com.coco.theme.themebox.database.DbHelper;
import com.coco.theme.themebox.database.model.AddressType;
import com.coco.theme.themebox.database.model.ApplicationType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UrlAddressThemeService
{
	
	public static final String FIELD_APPLICATION_TYPE = "applicationType";
	public static final String FIELD_ADDRESS_TYPE = "addressType";
	public static final String FIELD_ADDRESS = "address";
	public static final String TABLE_NAME = "themeUrlAddress";
	private DbHelper dbHelper;
	
	public static String getCreateSql()
	{
		String result = String.format(
				"CREATE TABLE %s (%s INTEGER, %s INTEGER, %s TEXT);" ,
				TABLE_NAME ,
				FIELD_APPLICATION_TYPE ,
				FIELD_ADDRESS_TYPE ,
				FIELD_ADDRESS ,
				FIELD_APPLICATION_TYPE ,
				FIELD_ADDRESS_TYPE );
		return result;
	}
	
	public static String getDropSql()
	{
		String result = "DROP TABLE IF EXISTS " + TABLE_NAME;
		return result;
	}
	
	public UrlAddressThemeService(
			Context context )
	{
		dbHelper = new DbHelper( context );
	}
	
	public boolean batchInsertAddress(
			ApplicationType appType ,
			AddressType addrType ,
			String[] addrArr )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try
		{
			db.beginTransaction();
			ContentValues cv = new ContentValues();
			for( String addr : addrArr )
			{
				cv.put( FIELD_APPLICATION_TYPE , appType.getValue() );
				cv.put( FIELD_ADDRESS_TYPE , addrType.getValue() );
				cv.put( FIELD_ADDRESS , addr );
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
	
	public void deleteAddress(
			ApplicationType appType ,
			AddressType addrType )
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try
		{
			db.delete(
					TABLE_NAME ,
					String.format( "%s=? and %s=?" , FIELD_APPLICATION_TYPE , FIELD_ADDRESS_TYPE ) ,
					new String[]{ Integer.toString( appType.getValue() ) , Integer.toString( addrType.getValue() ) } );
		}
		finally
		{
			db.close();
			DbHelper.lock_db = false;
		}
	}
	
	public List<String> queryAddress(
			ApplicationType appType ,
			AddressType addrType )
	{
		List<String> result = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query(
					TABLE_NAME ,
					new String[]{ FIELD_ADDRESS } ,
					String.format( "%s=? and %s=?" , FIELD_APPLICATION_TYPE , FIELD_ADDRESS_TYPE ) ,
					new String[]{ Integer.toString( appType.getValue() ) , Integer.toString( addrType.getValue() ) } ,
					null ,
					null ,
					null );
			while( cursor.moveToNext() )
			{
				result.add( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ADDRESS ) ) );
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
}
