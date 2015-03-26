package com.coco.download;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelperUILOG extends SQLiteOpenHelper
{
	
	private static final String DATABASE_NAME = "statisticsUILOG.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE = "configtable";
	
	public DatabaseHelperUILOG(
			Context context )
	{
		super( context , DATABASE_NAME , null , DATABASE_VERSION );
	}
	
	@Override
	public void onCreate(
			SQLiteDatabase db )
	{
		// TODO Auto-generated method stub
		db.execSQL( LogDB.getCreateSql() );
		db.execSQL( ConfigDB.getCreateSql() );
		db.execSQL( "INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES" + "('ErrorTime','YYYYMMDDHHMMSS');" );
		db.execSQL( "INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES" + "('ErrorCount','0');" );
		db.execSQL( "INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES" + "('SuccessTime','YYYYMMDDHHMMSS');" );
	}
	
	@Override
	public void onUpgrade(
			SQLiteDatabase db ,
			int oldVersion ,
			int newVersion )
	{
		// TODO Auto-generated method stub
		db.execSQL( LogDB.getDropSql() );
		db.execSQL( LogDB.getCreateSql() );
		db.execSQL( ConfigDB.getDropSql() );
		db.execSQL( ConfigDB.getCreateSql() );
		db.execSQL( "INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES" + "('ErrorTime','YYYYMMDDHHMMSS');" );
		db.execSQL( "INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES" + "('ErrorCount','0');" );
		db.execSQL( "INSERT INTO " + DATABASE_TABLE + "(name,value)" + "VALUES" + "('SuccessTime','YYYYMMDDHHMMSS');" );
	}
}
