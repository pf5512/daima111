package com.iLoong.launcher.SetupMenu;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DownLoadDB
{
	
	private SQLiteDatabase m_Database = null;
	private static final String DATABASE_NAME = "download.db";
	Context mContext;
	private Object db_lock = new Object();
	
	public DownLoadDB(
			Context context )
	{
		mContext = context;
		Init();
	}
	
	public void Init()
	{
		CreateDataBase();
		DBInit();
	}
	
	private void CreateDataBase()
	{
		try
		{
			synchronized( db_lock )
			{
				m_Database = mContext.openOrCreateDatabase( DATABASE_NAME , Activity.MODE_WORLD_WRITEABLE , null );
			}
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}
	
	public void DBInit()
	{
		CreateTable();
	}
	
	private void CreateTable()
	{
		CreateTabDownLoad();
	}
	
	private void CreateTabDownLoad()
	{
		String sql;
		sql = "create table IF NOT EXISTS tb_downloads ( ";
		sql += "filename varchar(128),";
		sql += "starttime integer,";
		sql += "filebytes integer,";
		sql += "downloadbytes integer";
		sql += " );";
		try
		{
			synchronized( db_lock )
			{
				m_Database.execSQL( sql );
			}
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
		return;
	}
	
	public boolean HaveRecord(
			String filename )
	{
		boolean bhave = false;
		String sql;
		sql = "select filename from tb_downloads where filename='" + filename + "';";
		Cursor result;
		synchronized( db_lock )
		{
			result = m_Database.rawQuery( sql , null );
		}
		result.moveToFirst();
		while( !result.isAfterLast() )
		{
			bhave = true;
			break;
		}
		result.close();
		return bhave;
	}
	
	public void Remove(
			String filename )
	{
		String sql;
		sql = "delete from tb_downloads where filename='" + filename + "';";
		ExecSql( sql );
	}
	
	public void Insert(
			String filename ,
			int len )
	{
		String sql;
		if( HaveRecord( filename ) )
		{
			sql = "update tb_downloads set filebytes=" + String.valueOf( len ) + ", downloadbytes=0 ";
			sql += "where filename='" + filename + "';";
		}
		else
		{
			sql = "insert into tb_downloads ";
			sql += "values(";
			sql += "'" + filename + "',";
			sql += "" + ( System.currentTimeMillis() / 1000 ) + ",";
			sql += "" + String.valueOf( len ) + ",";
			sql += " 0 ";
			sql += ");";
		}
		ExecSql( sql );
	}
	
	public void Update(
			String filename ,
			int downloadbytes )
	{
		String sql;
		sql = "update tb_downloads set ";
		sql += "downloadbytes=" + downloadbytes + " ";
		sql += "where filename='" + filename + "';";
		ExecSql( sql );
	}
	
	public int getDownLoadBytes(
			String filename )
	{
		int downloadbytes = -1;
		String sql;
		sql = "select downloadbytes from tb_downloads where filename='" + filename + "';";
		Cursor result;
		synchronized( db_lock )
		{
			result = m_Database.rawQuery( sql , null );
		}
		result.moveToFirst();
		while( !result.isAfterLast() )
		{
			downloadbytes = result.getInt( 0 );
			break;
		}
		result.close();
		return downloadbytes;
	}
	
	public int getDownLoadStartTime(
			String filename )
	{
		int starttime = 0;
		String sql;
		sql = "select starttime from tb_downloads where filename='" + filename + "';";
		Cursor result;
		synchronized( db_lock )
		{
			result = m_Database.rawQuery( sql , null );
		}
		result.moveToFirst();
		while( !result.isAfterLast() )
		{
			starttime = result.getInt( 0 );
			break;
		}
		result.close();
		return starttime;
	}
	
	private void ExecSql(
			String sql )
	{
		try
		{
			synchronized( db_lock )
			{
				m_Database.execSQL( sql );
			}
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
		return;
	}
}
