package com.cooee.searchbar;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper
{
	
	public static final String DB_NAME = "cooee.db";
	public static final String TABLE_NAME = "history";
	public static final String COLUMN_NAME_ID = "_id";
	public static final String COLUMN_NAME_KEY = "keywords";
	public static final String COLUMN_NAME_URL = "url";
	private static final String CREATE_TABLE = "create table if not exists history(_id integer primary key autoincrement, keywords text, url text)";
	private SQLiteDatabase db;
	
	public DBHelper(
			Context c )
	{
		super( c , DB_NAME , null , 2 );
	}
	
	@Override
	public void onCreate(
			SQLiteDatabase db )
	{
		this.db = db;
		db.execSQL( CREATE_TABLE );
	}
	
	@Override
	public void onUpgrade(
			SQLiteDatabase db ,
			int oldVersion ,
			int newVersion )
	{
	}
	
	public void insert(
			ContentValues values )
	{
		db = getWritableDatabase();
		db.insert( TABLE_NAME , null , values );
		db.close();
	}
	
	public Cursor query()
	{
		db = getReadableDatabase();
		Cursor c = db.query( TABLE_NAME , null , null , null , null , null , null );
		return c;
	}
	
	public void del()
	{
		//		if( db == null )
		//		{
		//			db = getWritableDatabase();
		//		}
		db = getWritableDatabase();
		db.delete( TABLE_NAME , null , null );
	}
	
	public void close()
	{
		if( db != null )
		{
			db.close();
		}
	}
}
