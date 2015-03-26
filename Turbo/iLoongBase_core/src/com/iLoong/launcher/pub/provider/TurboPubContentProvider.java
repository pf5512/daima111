package com.iLoong.launcher.pub.provider;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class TurboPubContentProvider extends ContentProvider
{
	
	public static final String PERSONAL_CENTER_AUTHORITY = "com.coco.pub.provider";
	public static final String LAUNCHER_AUTHORITY = "com.cooeeui.brand.turbolauncher.pub.provider";
	private PubDbHelper dbOpenHelper;
	// 常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
	private static final UriMatcher MATCHER = new UriMatcher( UriMatcher.NO_MATCH );
	private static final int THEMES = 1;
	private static final int THEME = 2;
	//	private static final int LOCKS = 3;
	//	private static final int LOCK = 4;
	private static final int WALLPAPERS = 5;
	private static final int WALLPAPER = 6;
	private static final int FONTS = 7;
	private static final int FONT = 8;
	private static final int EFFECTS = 9;
	private static final int EFFECT = 10;
	private static final int WIDGETS = 11;
	private static final int WIDGET = 12;
	private static final int SCENES = 13;
	private static final int SCENE = 14;
	private static final int CONFIGS = 15;
	private static final int CONFIG = 16;
	private static HashMap<String , RouteMatch> matchSet = null;
	
	static class RouteMatch
	{
		
		public String key;
		public String table;
		public int code;
		public String type = "";
	}
	
	static
	{
		matchSet = new HashMap<String , RouteMatch>();
		// 主题
		RouteMatch route = new RouteMatch();
		route.code = THEMES;
		route.key = String.valueOf( THEMES );
		route.table = "theme";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = THEME;
		route.key = String.valueOf( THEME );
		route.table = "theme/#";
		route.type = "item";
		matchSet.put( route.key , route );
		//		// 锁屏
		//		route = new RouteMatch();
		//		route.code = LOCKS;
		//		route.key = String.valueOf( LOCKS );
		//		route.table = "lock";
		//		route.type = "dir";
		//		matchSet.put( route.key , route );
		//		route = new RouteMatch();
		//		route.code = LOCK;
		//		route.key = String.valueOf( LOCK );
		//		route.table = "lock/#";
		//		route.type = "item";
		//		matchSet.put( route.key , route );
		// 壁纸
		route = new RouteMatch();
		route.code = WALLPAPERS;
		route.key = String.valueOf( WALLPAPERS );
		route.table = "wallpaper";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = WALLPAPER;
		route.key = String.valueOf( WALLPAPER );
		route.table = "wallpaper/#";
		route.type = "item";
		matchSet.put( route.key , route );
		// 字体
		route = new RouteMatch();
		route.code = FONTS;
		route.key = String.valueOf( FONTS );
		route.table = "font";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = FONT;
		route.key = String.valueOf( FONT );
		route.table = "font/#";
		route.type = "item";
		matchSet.put( route.key , route );
		// 特效
		route = new RouteMatch();
		route.code = EFFECTS;
		route.key = String.valueOf( EFFECTS );
		route.table = "effect";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = EFFECT;
		route.key = String.valueOf( EFFECT );
		route.table = "effect/#";
		route.type = "item";
		matchSet.put( route.key , route );
		// 组件
		route = new RouteMatch();
		route.code = WIDGETS;
		route.key = String.valueOf( WIDGETS );
		route.table = "widget";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = WIDGET;
		route.key = String.valueOf( WIDGET );
		route.table = "widget/#";
		route.type = "item";
		matchSet.put( route.key , route );
		// 场景
		route = new RouteMatch();
		route.code = SCENES;
		route.key = String.valueOf( SCENES );
		route.table = "scene";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = SCENE;
		route.key = String.valueOf( SCENE );
		route.table = "scene/#";
		route.type = "item";
		matchSet.put( route.key , route );
		// config.ini
		route = new RouteMatch();
		route.code = CONFIGS;
		route.key = String.valueOf( CONFIGS );
		route.table = "config";
		route.type = "dir";
		matchSet.put( route.key , route );
		route = new RouteMatch();
		route.code = CONFIG;
		route.key = String.valueOf( CONFIG );
		route.table = "config/#";
		route.type = "item";
		matchSet.put( route.key , route );
		Iterator<Entry<String , RouteMatch>> iter = matchSet.entrySet().iterator();
		while( iter.hasNext() )
		{
			Entry<String , RouteMatch> entry = (Entry<String , RouteMatch>)iter.next();
			RouteMatch val = (RouteMatch)entry.getValue();
			MATCHER.addURI( LAUNCHER_AUTHORITY , val.table , val.code );
		}
	}
	
	@Override
	public int delete(
			Uri uri ,
			String selection ,
			String[] selectionArgs )
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = 0;
		int code = MATCHER.match( uri );
		RouteMatch routeMatch = matchSet.get( String.valueOf( code ) );
		if( routeMatch != null )
		{
			if( routeMatch.type.equals( "dir" ) )
			{
				count = db.delete( routeMatch.table , selection , selectionArgs );
				return count;
			}
			else if( routeMatch.type.equals( "item" ) )
			{
				long id = ContentUris.parseId( uri );
				String where = "_id = " + id;
				if( selection != null && !"".equals( selection ) )
				{
					where = selection + " and " + where;
				}
				count = db.delete( routeMatch.table , where , selectionArgs );
				return count;
			}
			else
			{
				throw new IllegalArgumentException( "Unkwon type:" + routeMatch.type + "for Uri:" + uri.toString() );
			}
		}
		else
		{
			throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
		}
	}
	
	/**
	 * 该方法用于返回当前Url所代表数据的MIME类型。
	 * 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头
	 * 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开头
	 */
	@Override
	public String getType(
			Uri uri )
	{
		int code = MATCHER.match( uri );
		RouteMatch routeMatch = matchSet.get( String.valueOf( code ) );
		if( routeMatch != null )
		{
			if( routeMatch.type.equals( "dir" ) )
			{
				return "vnd.android.cursor.dir/" + routeMatch.table;
			}
			else if( routeMatch.type.equals( "item" ) )
			{
				return "vnd.android.cursor.item/" + routeMatch.table;
			}
			else
			{
				throw new IllegalArgumentException( "Unkwon type:" + routeMatch.type + " Uri:" + uri.toString() );
			}
		}
		else
		{
			throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
		}
	}
	
	@Override
	public Uri insert(
			Uri uri ,
			ContentValues values )
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int code = MATCHER.match( uri );
		RouteMatch routeMatch = matchSet.get( String.valueOf( code ) );
		if( routeMatch != null )
		{
			long rowid = db.insert( routeMatch.table , "propertyName" , values );
			Uri insertUri = ContentUris.withAppendedId( uri , rowid );// 得到代表新增记录的Uri
			this.getContext().getContentResolver().notifyChange( uri , null );
			return insertUri;
		}
		else
		{
			throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
		}
	}
	
	@Override
	public boolean onCreate()
	{
		this.dbOpenHelper = new PubDbHelper( this.getContext() );
		return false;
	}
	
	@Override
	public Cursor query(
			Uri uri ,
			String[] projection ,
			String selection ,
			String[] selectionArgs ,
			String sortOrder )
	{
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		int code = MATCHER.match( uri );
		RouteMatch routeMatch = matchSet.get( String.valueOf( code ) );
		if( routeMatch != null )
		{
			if( routeMatch.type.equals( "dir" ) )
			{
				return db.query( routeMatch.table , projection , selection , selectionArgs , null , null , sortOrder );
			}
			else if( routeMatch.type.equals( "item" ) )
			{
				long id = ContentUris.parseId( uri );
				String where = "_id = " + id;
				if( selection != null && !"".equals( selection ) )
				{
					where = selection + " and " + where;
				}
				return db.query( routeMatch.table , projection , where , selectionArgs , null , null , sortOrder );
			}
			else
			{
				throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
			}
		}
		else
		{
			throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
		}
	}
	
	@Override
	public int update(
			Uri uri ,
			ContentValues values ,
			String selection ,
			String[] selectionArgs )
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int code = MATCHER.match( uri );
		int count = 0;
		RouteMatch routeMatch = matchSet.get( String.valueOf( code ) );
		if( routeMatch != null )
		{
			if( routeMatch.type.equals( "dir" ) )
			{
				count = db.update( routeMatch.table , values , selection , selectionArgs );
				return count;
			}
			else if( routeMatch.type.equals( "item" ) )
			{
				long id = ContentUris.parseId( uri );
				String where = "_id = " + id;
				if( selection != null && !"".equals( selection ) )
				{
					where = selection + " and " + where;
				}
				count = db.update( routeMatch.table , values , where , selectionArgs );
				return count;
			}
			else
			{
				throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
			}
		}
		else
		{
			throw new IllegalArgumentException( "Unkwon Uri:" + uri.toString() );
		}
	}
}
