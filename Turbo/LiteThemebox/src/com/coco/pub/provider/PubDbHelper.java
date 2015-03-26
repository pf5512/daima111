package com.coco.pub.provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.coco.theme.themebox.util.Log;


public class PubDbHelper extends SQLiteOpenHelper
{
	
	private static final String LOG_TAG = "PubDbHelper";
	private static final String DATABASE_NAME = "pub_provider.db";
	private static final int DATABASE_VERSION = 2;
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_APPLICATION_NAME = "applicationName";
	public static final String FIELD_APPLICATION_NAME_EN = "applicationNameEn";//Jone add
	public static final String FIELD_VERSION_CODE = "versionCode";
	public static final String FIELD_VERSION_NAME = "versionName";
	public static final String FIELD_APPLICATION_SIZE = "applicationSize";
	public static final String FIELD_AUTHOR = "author";
	public static final String FIELD_INTRODUCTION = "introduction";
	public static final String FIELD_UPDATE_TIME = "updateTime";
	public static final String FIELD_THUMBIMG = "thumbimg";
	public static final String FIELD_PREVIEWLIST = "previewlist";
	public static final String FIELD_RESURL = "resurl";
	public static final String FIELD_RESID = "resid";
	public static final String TABLE_NAME = "lock";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_PRICE = "price";
	
	public PubDbHelper(
			Context cxt )
	{
		super( cxt , DATABASE_NAME , null , DATABASE_VERSION );
	}
	
	public static String getDroptgetThemeSql()
	{
		String result = "DROP TABLE IF EXISTS theme";
		return result;
	}
	
	public static String getDroptLockSql()
	{
		String result = "DROP TABLE IF EXISTS lock";
		return result;
	}
	
	public static String getDroptWallpaperSql()
	{
		String result = "DROP TABLE IF EXISTS wallpaper";
		return result;
	}
	
	public static String getDroptFontSql()
	{
		String result = "DROP TABLE IF EXISTS font";
		return result;
	}
	
	public static String getDroptEffectSql()
	{
		String result = "DROP TABLE IF EXISTS effect";
		return result;
	}
	
	public static String getDroptWidgetSql()
	{
		String result = "DROP TABLE IF EXISTS widget";
		return result;
	}
	
	public static String getDroptSceneSql()
	{
		String result = "DROP TABLE IF EXISTS scene";
		return result;
	}
	
	public static String getThemeCreateSql()
	{
		String result = "CREATE TABLE theme (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	public static String getLockCreateSql()
	{
		String result = "CREATE TABLE lock (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	public static String getWallPaperCreateSql()
	{
		String result = "CREATE TABLE wallpaper (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	public static String getFontCreateSql()
	{
		String result = "CREATE TABLE font (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	public static String getEffectCreateSql()
	{
		String result = "CREATE TABLE effect (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	public static String getWidgetCreateSql()
	{
		String result = "CREATE TABLE widget (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	public static String getSceneCreateSql()
	{
		String result = "CREATE TABLE scene (_id INTEGER PRIMARY KEY AUTOINCREMENT,propertyName NVARCHAR(100) , propertyValue NVARCHAR(800));";
		return result;
	}
	
	@Override
	public void onCreate(
			SQLiteDatabase db )
	{
		Log.i( LOG_TAG , "onCreate," + DATABASE_NAME + "," + DATABASE_VERSION );
		db.execSQL( getThemeCreateSql() );
		db.execSQL( getLockCreateSql() );
		db.execSQL( getWallPaperCreateSql() );
		db.execSQL( getFontCreateSql() );
		db.execSQL( getEffectCreateSql() );
		db.execSQL( getWidgetCreateSql() );
		db.execSQL( getSceneCreateSql() );
	}
	
	@Override
	public void onUpgrade(
			SQLiteDatabase db ,
			int oldVersion ,
			int newVersion )
	{
		Log.i( LOG_TAG , String.format( "onUpgrade,dbName=%s,old=%d,new=%d" , DATABASE_NAME , oldVersion , newVersion ) );
		db.execSQL( getDroptgetThemeSql() );
		db.execSQL( getDroptLockSql() );
		db.execSQL( getDroptWallpaperSql() );
		db.execSQL( getDroptFontSql() );
		db.execSQL( getDroptEffectSql() );
		db.execSQL( getDroptWidgetSql() );
		db.execSQL( getDroptSceneSql() );
		db.execSQL( getThemeCreateSql() );
		db.execSQL( getLockCreateSql() );
		db.execSQL( getWallPaperCreateSql() );
		db.execSQL( getFontCreateSql() );
		db.execSQL( getEffectCreateSql() );
		db.execSQL( getWidgetCreateSql() );
		db.execSQL( getSceneCreateSql() );
	}
	
	@Override
	public synchronized SQLiteDatabase getReadableDatabase()
	{
		// Log.e("db", "getRead");
		SQLiteDatabase db = null;
		boolean exception = true;
		while( exception )
		{
			try
			{
				exception = false;
				db = super.getReadableDatabase();
			}
			catch( Exception e )
			{
				e.printStackTrace();
				exception = true;
				try
				{
					this.wait( 10 );
				}
				catch( InterruptedException e1 )
				{
					e1.printStackTrace();
				}
			}
		}
		return db;
	}
	
	@Override
	public synchronized SQLiteDatabase getWritableDatabase()
	{
		// Log.e("db", "getWrite");
		SQLiteDatabase db = null;
		boolean exception = true;
		while( exception )
		{
			try
			{
				exception = false;
				db = super.getWritableDatabase();
			}
			catch( Exception e )
			{
				e.printStackTrace();
				exception = true;
				try
				{
					this.wait( 10 );
				}
				catch( InterruptedException e1 )
				{
					e1.printStackTrace();
				}
			}
		}
		return db;
	}
}
