package com.iLoong.launcher.app;


import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class AppListDB
{
	
	private SQLiteDatabase m_Database = null;
	private static final String DATABASE_NAME = "launcher.db";
	private Context mContext;
	private static AppListDB db;
	
	public static AppListDB getInstance()
	{
		if( db == null )
		{
			synchronized( AppListDB.class )
			{
				if( db == null )
					db = new AppListDB();
			}
		}
		return db;
	}
	
	public void Init(
			Context context )
	{
		mContext = context;
		CreateDataBase();
		CreateTable();
	}
	
	private void CreateDataBase()
	{
		try
		{
			m_Database = mContext.openOrCreateDatabase( DATABASE_NAME , Activity.MODE_WORLD_WRITEABLE , null );
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
	}
	
	private void CreateTable()
	{
		String sql;
		sql = "create table IF NOT EXISTS applist ( ";
		sql += "id INTEGER PRIMARY KEY,";
		sql += "item_type int,";
		sql += "container int,";
		sql += "title TEXT,";
		sql += "intent TEXT,";
		sql += "text1 TEXT,";
		sql += "text2 TEXT,";
		sql += "text3 TEXT,";
		sql += "text4 TEXT,";
		sql += "text5 TEXT,";
		sql += "last_update_time long,";
		sql += "use_frequency int,";
		sql += "int1 int,";
		sql += "int2 int,";
		sql += "int3 int";
		sql += "int4 int";
		sql += "int5 int";
		sql += " );";
		try
		{
			m_Database.execSQL( sql );
		}
		catch( SQLException e )
		{
			e.printStackTrace();
		}
		return;
	}
	
	public Cursor queryAll()
	{
		String sql = "select * from applist;";
		Cursor result = m_Database.rawQuery( sql , null );
		return result;
	}
	
	public void addOrMoveItem(
			ItemInfo info ,
			long container )
	{
		if( info.container < LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			info.container = container;
			addItem( info );
		}
		else
		{
			info.container = container;
			moveItem( info );
		}
	}
	
	private void addItem(
			ItemInfo info )
	{
		final ContentValues values = new ContentValues();
		if( info instanceof UserFolderInfo )
		{
			UserFolderInfo folderInfo = (UserFolderInfo)info;
			values.put( "title" , folderInfo.title.toString() );
			values.put( "item_type" , LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER );
			values.put( "container" , folderInfo.container );
			values.put( "last_update_time" , folderInfo.lastUpdateTime );
			values.put( "use_frequency" , folderInfo.use_frequency );
			try
			{
				long id = m_Database.insertOrThrow( "applist" , null , values );
				folderInfo.id = id + LauncherSettings.Favorites.CONTAINER_APPLIST + 1;
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		}
		if( info instanceof ShortcutInfo )
		{
			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
			values.put( "title" , shortcutInfo.title.toString() );
			values.put( "item_type" , LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT );
			String uri = shortcutInfo.intent != null ? shortcutInfo.intent.toUri( 0 ) : null;
			values.put( "intent" , uri );
			values.put( "container" , shortcutInfo.container );
			try
			{
				long id = m_Database.insertOrThrow( "applist" , null , values );
				shortcutInfo.id = id;
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public void moveItem(
			ItemInfo info )
	{
		final ContentValues values = new ContentValues();
		if( info instanceof UserFolderInfo )
		{
			UserFolderInfo folderInfo = (UserFolderInfo)info;
			values.put( "title" , folderInfo.title.toString() );
			values.put( "item_type" , LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER );
			values.put( "container" , folderInfo.container );
			values.put( "last_update_time" , folderInfo.lastUpdateTime );
			values.put( "use_frequency" , folderInfo.use_frequency );
			try
			{
				m_Database.update( "applist" , values , "id=" + ( folderInfo.id - LauncherSettings.Favorites.CONTAINER_APPLIST - 1 ) , null );
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		}
		if( info instanceof ShortcutInfo )
		{
			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
			values.put( "title" , shortcutInfo.title.toString() );
			values.put( "item_type" , LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT );
			String uri = shortcutInfo.intent != null ? shortcutInfo.intent.toUri( 0 ) : null;
			values.put( "intent" , uri );
			values.put( "container" , shortcutInfo.container );
			try
			{
				m_Database.update( "applist" , values , "id=" + shortcutInfo.id , null );
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public void deleteItem(
			ItemInfo info )
	{
		if( info instanceof UserFolderInfo )
		{
			m_Database.delete( "applist" , "id=" + ( info.id - LauncherSettings.Favorites.CONTAINER_APPLIST - 1 ) , null );
		}
		if( info instanceof ShortcutInfo )
		{
			m_Database.delete( "applist" , "id=" + info.id , null );
		}
	}
}
