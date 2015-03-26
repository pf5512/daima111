package com.coco.theme.themebox.database.service;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coco.theme.themebox.database.DbHelper;
import com.coco.theme.themebox.database.model.ThemeInfoItem;


public class HotService
{
	
	public static final String FIELD_PACKAGE_NAME = "packageName";
	public static final String FIELD_APPLICATION_NAME = "applicationName";
	public static final String FIELD_APPLICATION_NAME_EN = "applicationNameEn";
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
	public static final String TABLE_NAME = "hotTheme";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_PRICE = "price";
	public static final String FIELD_PRICE_POINT = "pricepoint";
	public static final String FIELD_ENGINE_PACKAGENAME = "enginepackname";
	public static final String FIELD_ENGINE_URL = "engineurl";
	public static final String FIELD_ENGINE_SIZE = "enginesize";
	public static final String FIELD_ENGINE_DESC = "enginedesc";
	public static final String FIELD_THIRDPARTY = "thirdparty";
	private DbHelper dbHelper;
	
	public static String getCreateSql()
	{
		String result = String
				.format(
						"CREATE TABLE %s (%s TEXT , %s TEXT, %s TEXT,  %s INTEGER, %s TEXT, %s INTEGER, %s TEXT," + " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT,%s TEXT, %s INTEGER, %s TEXT," + " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT,CONSTRAINT PK_%s PRIMARY KEY (%s,%s));" ,
						TABLE_NAME ,
						FIELD_PACKAGE_NAME ,
						FIELD_APPLICATION_NAME ,
						FIELD_APPLICATION_NAME_EN ,//Jone
						FIELD_VERSION_CODE ,
						FIELD_VERSION_NAME ,
						FIELD_APPLICATION_SIZE ,
						FIELD_AUTHOR ,
						FIELD_INTRODUCTION ,
						FIELD_UPDATE_TIME ,
						FIELD_THUMBIMG ,
						FIELD_PREVIEWLIST ,
						FIELD_RESURL ,
						FIELD_RESID ,
						FIELD_TYPE ,
						FIELD_PRICE ,
						FIELD_PRICE_POINT ,
						FIELD_ENGINE_PACKAGENAME ,
						FIELD_ENGINE_URL ,
						FIELD_ENGINE_SIZE ,
						FIELD_ENGINE_DESC ,
						FIELD_THIRDPARTY ,
						TABLE_NAME ,
						FIELD_PACKAGE_NAME ,
						FIELD_TYPE );
		return result;
	}
	
	public static String getDropSql()
	{
		String result = "DROP TABLE IF EXISTS " + TABLE_NAME;
		return result;
	}
	
	public HotService(
			Context context )
	{
		dbHelper = new DbHelper( context );
	}
	
	public synchronized List<ThemeInfoItem> queryTable(
			String type )
	{
		ArrayList<ThemeInfoItem> list = new ArrayList<ThemeInfoItem>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , FIELD_TYPE + "=?" , new String[]{ type } , null , null , null , null );
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
			String packageName ,
			String type )
	{
		ThemeInfoItem result = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , null , FIELD_PACKAGE_NAME + "=? and " + FIELD_TYPE + "=?" , new String[]{ packageName , type } , null , null , null );
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
	
	public String queryResid(
			String packageName )
	{
		String resid = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , new String[]{ FIELD_RESID } , FIELD_PACKAGE_NAME + "=?" , new String[]{ packageName } , null , null , null );
			if( cursor.moveToFirst() )
			{
				resid = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_RESID ) );
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
		return resid;
	}
	
	public String queryResid(
			String packageName ,
			String type )
	{
		String resid = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , new String[]{ FIELD_RESID } , FIELD_PACKAGE_NAME + "=? and " + FIELD_TYPE + "=?" , new String[]{ packageName , type } , null , null , null );
			if( cursor.moveToFirst() )
			{
				resid = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_RESID ) );
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
		return resid;
	}
	
	public String queryThumbimg(
			String packageName ,
			String type )
	{
		String thumbimg = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , new String[]{ FIELD_THUMBIMG } , FIELD_PACKAGE_NAME + "=? and " + FIELD_TYPE + "=?" , new String[]{ packageName , type } , null , null , null );
			if( cursor.moveToFirst() )
			{
				thumbimg = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_THUMBIMG ) );
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
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
		return thumbimg;
	}
	
	public String queryPreviewAddress(
			String packageName ,
			String type )
	{
		String resurl = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , new String[]{ FIELD_PREVIEWLIST } , FIELD_PACKAGE_NAME + "=? and " + FIELD_TYPE + "=?" , new String[]{ packageName , type } , null , null , null );
			if( cursor.moveToFirst() )
			{
				resurl = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_PREVIEWLIST ) );
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
		return resurl;
	}
	
	public String queryResurlAddress(
			String packageName ,
			String type )
	{
		String resurl = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try
		{
			cursor = db.query( TABLE_NAME , new String[]{ FIELD_RESURL } , FIELD_PACKAGE_NAME + "=? and " + FIELD_TYPE + "=?" , new String[]{ packageName , type } , null , null , null );
			if( cursor.moveToFirst() )
			{
				resurl = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_RESURL ) );
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
		return resurl;
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
				cv.put( FIELD_APPLICATION_NAME_EN , info.getApplicationName_en() );
				cv.put( FIELD_VERSION_CODE , info.getVersionCode() );
				cv.put( FIELD_VERSION_NAME , info.getVersionName() );
				cv.put( FIELD_APPLICATION_SIZE , info.getApplicationSize() );
				cv.put( FIELD_AUTHOR , info.getAuthor() );
				cv.put( FIELD_INTRODUCTION , info.getIntroduction() );
				cv.put( FIELD_UPDATE_TIME , info.getUpdateTime() );
				cv.put( FIELD_THUMBIMG , info.getThumbimgUrl() );
				cv.put( FIELD_RESURL , info.getResurl() );
				cv.put( FIELD_RESID , info.getResid() );
				cv.put( FIELD_TYPE , info.getType() );
				cv.put( FIELD_PRICE , info.getPrice() );
				cv.put( FIELD_PRICE_POINT , info.getPricepoint() );
				cv.put( FIELD_ENGINE_PACKAGENAME , info.getEnginepackname() );
				cv.put( FIELD_ENGINE_URL , info.getEngineurl() );
				cv.put( FIELD_ENGINE_SIZE , info.getEnginesize() );
				cv.put( FIELD_ENGINE_DESC , info.getEnginedesc() );
				System.out.println( "info.getEnginedesc() = " + info.getEnginedesc() );
				cv.put( FIELD_THIRDPARTY , info.getThirdparty() );
				String str = "";
				for( int i = 0 ; i < info.getPreviewlist().length ; i++ )
				{
					str += info.getPreviewlist()[i] + ";";
				}
				cv.put( FIELD_PREVIEWLIST , str );
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
			item.setApplicationName_en( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_APPLICATION_NAME_EN ) ) );
			item.setVersionCode( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_VERSION_CODE ) ) );
			item.setVersionName( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_VERSION_NAME ) ) );
			item.setApplicationSize( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_APPLICATION_SIZE ) ) );
			item.setAuthor( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_AUTHOR ) ) );
			item.setIntroduction( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_INTRODUCTION ) ) );
			item.setUpdateTime( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_UPDATE_TIME ) ) );
			item.setResid( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_RESID ) ) );
			item.setThumbimgUrl( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_THUMBIMG ) ) );
			item.setResurl( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_RESURL ) ) );
			item.setType( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_TYPE ) ) );
			item.setPrice( cursor.getInt( cursor.getColumnIndexOrThrow( FIELD_PRICE ) ) );
			item.setPricepoint( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_PRICE_POINT ) ) );
			item.setEnginepackname( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ENGINE_PACKAGENAME ) ) );
			item.setEngineurl( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ENGINE_URL ) ) );
			item.setEnginesize( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ENGINE_SIZE ) ) );
			item.setEnginedesc( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ENGINE_DESC ) ) );
			item.setThirdparty( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_THIRDPARTY ) ) );
			String[] temStr = cursor.getString( cursor.getColumnIndexOrThrow( FIELD_PREVIEWLIST ) ).split( ";" );
			item.setPreviewlist( temStr );
			return item;
		}
		catch( IllegalArgumentException ex )
		{
			ex.printStackTrace();
			return null;
		}
	}
}
