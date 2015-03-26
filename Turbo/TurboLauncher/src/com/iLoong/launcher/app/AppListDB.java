package com.iLoong.launcher.app;


import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;


public class AppListDB
{
	
	private SQLiteDatabase m_Database = null;
	private static final String DATABASE_NAME = "launcher.db";
	private Context mContext;
	private static AppListDB db;
	public static Object db_lock = new Object();
	static long id;
	
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
	
	public void addOrMoveItem(
			ItemInfo info ,
			long container ,
			Intent intent )
	{
		if( info.container < LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			info.container = container;
			addItem( info , intent );
		}
		else
		{
			info.container = container;
			moveItem( info );
		}
	}
	
	//xiatian del start	//for mainmenu sort by user
	//	private void addItem(ItemInfo info){
	//		final ContentValues values = new ContentValues();
	//		if(info instanceof UserFolderInfo){
	//			UserFolderInfo folderInfo = (UserFolderInfo)info;
	//			values.put("title", folderInfo.title.toString());
	//			values.put("item_type", LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER);
	//			values.put("container", folderInfo.container);
	//			values.put("last_update_time", folderInfo.lastUpdateTime);
	//			values.put("use_frequency", folderInfo.use_frequency);
	//			try {
	//				long id = m_Database.insertOrThrow("applist", null, values);
	//				folderInfo.id = id + LauncherSettings.Favorites.CONTAINER_APPLIST + 1;
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//		if(info instanceof ShortcutInfo){
	//			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
	//			values.put("title", shortcutInfo.title.toString());
	//			values.put("item_type", LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT);
	//			String uri = shortcutInfo.intent != null ? shortcutInfo.intent.toUri(0) : null;
	//			values.put("intent", uri);
	//			values.put("container", shortcutInfo.container);
	//			try {
	//				long id = m_Database.insertOrThrow("applist", null, values);
	//				shortcutInfo.id = id;
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	//	
	//	public void moveItem(ItemInfo info){
	//		final ContentValues values = new ContentValues();
	//		if(info instanceof UserFolderInfo){
	//			UserFolderInfo folderInfo = (UserFolderInfo)info;
	//			values.put("title", folderInfo.title.toString());
	//			values.put("item_type", LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER);
	//			values.put("container", folderInfo.container);
	//			values.put("last_update_time", folderInfo.lastUpdateTime);
	//			values.put("use_frequency", folderInfo.use_frequency);
	//			try {
	//				m_Database.update("applist", values,"id="+(folderInfo.id-LauncherSettings.Favorites.CONTAINER_APPLIST-1),null);
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//		if(info instanceof ShortcutInfo){
	//			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
	//			values.put("title", shortcutInfo.title.toString());
	//			values.put("item_type", LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT);
	//			String uri = shortcutInfo.intent != null ? shortcutInfo.intent.toUri(0) : null;
	//			values.put("intent", uri);
	//			values.put("container", shortcutInfo.container);
	//			try {
	//				m_Database.update("applist", values,"id="+shortcutInfo.id,null);
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	//xiatian del end
	public void deleteItem(
			ItemInfo info )
	{
		if( info instanceof UserFolderInfo )
		{
			final String id = "" + ( info.id - LauncherSettings.Favorites.CONTAINER_APPLIST - 1 );
			if( DefaultLayout.post_database )
			{
				LauncherModel.getWorkerThread().post( new Runnable() {
					
					public void run()
					{
						synchronized( db_lock )
						{
							m_Database.delete( "applist" , "id=" + id , null );
						}
					}
				} );
			}
			else
			{
				synchronized( db_lock )
				{
					m_Database.delete( "applist" , "id=" + id , null );
				}
			}
		}
		if( info instanceof ShortcutInfo )
		{
			final String id = "" + ( info.id );
			if( DefaultLayout.post_database )
			{
				LauncherModel.getWorkerThread().post( new Runnable() {
					
					public void run()
					{
						synchronized( db_lock )
						{
							m_Database.delete( "applist" , "id=" + id , null );
						}
					}
				} );
			}
			else
			{
				synchronized( db_lock )
				{
					m_Database.delete( "applist" , "id=" + id , null );
				}
			}
		}
	}
	
	//xiatian add start	//for mainmenu sort by user
	public Cursor query(
			String selection ,
			String[] selectionArgs )
	{
		Cursor mCursor;
		mCursor = m_Database.query( "applist" , new String[]{
				"id" ,
				"item_type" ,
				"container" ,
				"title" ,
				"intent" ,
				"text1" ,
				"text2" ,
				"text3" ,
				"text4" ,
				"text5" ,
				"last_update_time" ,
				"use_frequency" ,
				"int1" ,
				"int2" ,
				"int3" } , selection , selectionArgs , null , null , null );
		return mCursor;
	}
	
	public int BatchItemsInsert(
			ArrayList<ItemInfo> ItemInfoList )
	{
		m_Database.beginTransaction();
		try
		{
			for( ItemInfo itemInfo : ItemInfoList )
			{
				if( false == addItem( itemInfo ) )
				{
					return -1;
				}
			}
			m_Database.setTransactionSuccessful();
		}
		finally
		{
			m_Database.endTransaction();
		}
		return ItemInfoList.size();
	}
	
	public int BatchItemsUpdate(
			ArrayList<ItemInfo> ItemInfoList )
	{
		if( DefaultLayout.post_database )
		{
			for( ItemInfo itemInfo : ItemInfoList )
			{
				if( false == moveItem( itemInfo ) )
				{
					return -1;
				}
			}
		}
		else
		{
			m_Database.beginTransaction();
			try
			{
				for( ItemInfo itemInfo : ItemInfoList )
				{
					if( false == moveItem( itemInfo ) )
					{
						return -1;
					}
				}
				m_Database.setTransactionSuccessful();
			}
			finally
			{
				m_Database.endTransaction();
			}
		}
		return ItemInfoList.size();
	}
	
	private boolean addItem(
			ItemInfo info ,
			Intent intent )
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
			String uri = intent != null ? intent.toUri( 0 ) : null;
			values.put( "intent" , uri );
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				values.put( "int1" , folderInfo.location_in_mainmenu );
			}
			try
			{
				id = m_Database.insertOrThrow( "applist" , null , values );
				folderInfo.id = id + LauncherSettings.Favorites.CONTAINER_APPLIST + 1;
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
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
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				if( shortcutInfo.appInfo != null )
				{
					shortcutInfo.location_in_mainmenu = shortcutInfo.appInfo.location_in_mainmenu;
				}
				values.put( "int1" , shortcutInfo.location_in_mainmenu );
			}
			try
			{
				id = m_Database.insertOrThrow( "applist" , null , values );
				shortcutInfo.id = id;
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private boolean addItem(
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
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				values.put( "int1" , folderInfo.location_in_mainmenu );
			}
			try
			{
				id = m_Database.insertOrThrow( "applist" , null , values );
				folderInfo.id = id + LauncherSettings.Favorites.CONTAINER_APPLIST + 1;
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
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
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				if( shortcutInfo.appInfo != null )
				{
					shortcutInfo.location_in_mainmenu = shortcutInfo.appInfo.location_in_mainmenu;
				}
				values.put( "int1" , shortcutInfo.location_in_mainmenu );
			}
			try
			{
				id = m_Database.insertOrThrow( "applist" , null , values );
				shortcutInfo.id = id;
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public boolean moveItem(
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
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				values.put( "int1" , folderInfo.location_in_mainmenu );
			}
			try
			{
				final String id = "" + ( folderInfo.id - LauncherSettings.Favorites.CONTAINER_APPLIST - 1 );
				if( DefaultLayout.post_database )
				{
					LauncherModel.getWorkerThread().post( new Runnable() {
						
						public void run()
						{
							synchronized( db_lock )
							{
								m_Database.update( "applist" , values , "id=" + id , null );
							}
						}
					} );
				}
				else
				{
					synchronized( db_lock )
					{
						m_Database.update( "applist" , values , "id=" + id , null );
					}
				}
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
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
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				if( shortcutInfo.appInfo != null )
				{
					shortcutInfo.location_in_mainmenu = shortcutInfo.appInfo.location_in_mainmenu;
				}
				values.put( "int1" , shortcutInfo.location_in_mainmenu );
			}
			try
			{
				final String id = "" + ( shortcutInfo.id );
				if( DefaultLayout.post_database )
				{
					LauncherModel.getWorkerThread().post( new Runnable() {
						
						public void run()
						{
							synchronized( db_lock )
							{
								m_Database.update( "applist" , values , "id=" + id , null );
							}
						}
					} );
				}
				else
				{
					synchronized( db_lock )
					{
						m_Database.update( "applist" , values , "id=" + id , null );
					}
				}
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public int BatchItemsUpdateLocation(
			ArrayList<ItemInfo> ItemInfoList )
	{
		if( DefaultLayout.post_database )
		{
			for( ItemInfo itemInfo : ItemInfoList )
			{
				if( false == moveItemLocation( itemInfo ) )
				{
					return -1;
				}
			}
		}
		else
		{
			m_Database.beginTransaction();
			try
			{
				for( ItemInfo itemInfo : ItemInfoList )
				{
					if( false == moveItemLocation( itemInfo ) )
					{
						return -1;
					}
				}
				m_Database.setTransactionSuccessful();
			}
			finally
			{
				m_Database.endTransaction();
			}
		}
		return ItemInfoList.size();
	}
	
	public boolean moveItemLocation(
			ItemInfo info )
	{
		final ContentValues values = new ContentValues();
		if( info instanceof UserFolderInfo )
		{
			UserFolderInfo folderInfo = (UserFolderInfo)info;
			values.put( "int1" , folderInfo.location_in_mainmenu );
			try
			{
				final String id = "" + ( folderInfo.id - LauncherSettings.Favorites.CONTAINER_APPLIST - 1 );
				if( DefaultLayout.post_database )
				{
					LauncherModel.getWorkerThread().post( new Runnable() {
						
						public void run()
						{
							synchronized( db_lock )
							{
								m_Database.update( "applist" , values , "id=" + id , null );
							}
						}
					} );
				}
				else
				{
					synchronized( db_lock )
					{
						m_Database.update( "applist" , values , "id=" + id , null );
					}
				}
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
			}
		}
		else if( info instanceof ShortcutInfo )
		{
			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
			if( shortcutInfo.appInfo != null )
			{
				shortcutInfo.location_in_mainmenu = shortcutInfo.appInfo.location_in_mainmenu;
			}
			values.put( "int1" , shortcutInfo.location_in_mainmenu );
			try
			{
				final String id = "" + ( shortcutInfo.id );
				if( DefaultLayout.post_database )
				{
					LauncherModel.getWorkerThread().post( new Runnable() {
						
						public void run()
						{
							synchronized( db_lock )
							{
								m_Database.update( "applist" , values , "id=" + id , null );
							}
						}
					} );
				}
				else
				{
					synchronized( db_lock )
					{
						m_Database.update( "applist" , values , "id=" + id , null );
					}
				}
			}
			catch( SQLException e )
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	//xiatian add end
}
