package com.coco.download;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class ConfigDB
{
	
	public static final String KEY_NAME = "name";
	public static final String KEY_VALUE = "value";
	private static final String DATABASE_TABLE = "configtable";
	private final Context context;
	private DatabaseHelperUILOG DBHelper;
	private SQLiteDatabase db;
	
	public ConfigDB(
			Context ctx )
	{
		this.context = ctx;
		DBHelper = new DatabaseHelperUILOG( context );
	}
	
	public static String getCreateSql()
	{
		String result = "create table configtable (name TEXT primary key not null, " + "value text);";
		return result;
	}
	
	public static String getDropSql()
	{
		String result = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
		return result;
	}
	
	public ConfigDB open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		db.close();
		DBHelper.close();
	}
	
	public long insertRecord(
			String sName ,
			String sValue )
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put( KEY_NAME , sName );
		initialValues.put( KEY_VALUE , sValue );
		return db.insert( DATABASE_TABLE , null , initialValues );
	}
	
	public boolean deleteRecord(
			String sName )
	{
		return db.delete( DATABASE_TABLE , KEY_NAME + "=" + sName , null ) > 0;
	}
	
	public Cursor getAllRecord()
	{
		return db.query( DATABASE_TABLE , new String[]{ KEY_NAME , KEY_VALUE } , null , null , null , null , null );
	}
	
	private SQLiteDatabase getDatabase()
	{
		return DBHelper.getWritableDatabase();
	}
	
	public String getRecord(
			String sName ) throws SQLException
	{
		String result = "";
		SQLiteDatabase db = getDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( DATABASE_TABLE , null , KEY_NAME + "=?" , new String[]{ sName } , null , null , null );
			if( cursor.moveToFirst() )
			{
				result = cursor.getString( cursor.getColumnIndexOrThrow( KEY_VALUE ) );
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
	
	public boolean updateRecord(
			String sName ,
			String sValue )
	{
		ContentValues args = new ContentValues();
		args.put( KEY_VALUE , sValue );
		// return db.update(DATABASE_TABLE, args, KEY_NAME + "=" + sName, null)
		// > 0;
		return db.update( DATABASE_TABLE , args , KEY_NAME + "=?" , new String[]{ sName } ) > 0;
	}
}
