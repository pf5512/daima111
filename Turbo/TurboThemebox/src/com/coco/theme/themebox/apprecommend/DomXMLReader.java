package com.coco.theme.themebox.apprecommend;


import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.format.Time;

import com.coco.theme.themebox.StaticClass;


public class DomXMLReader
{
	
	private Context mcontext;
	private String newVersion = "";
	private MyDBHelper mDbHelper = null;
	private SQLiteDatabase db = null;
	
	public DomXMLReader(
			Context context )
	{
		mcontext = context;
		mDbHelper = new MyDBHelper( context );
	}
	
	// 记录更新时间
	private void recordTime()
	{
		Time curTime = new Time();
		curTime.setToNow(); // 取得系统时间�?
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( mcontext ).edit();
		int year = curTime.year;
		editor.putInt( "entrance_year" , year );
		int month = curTime.month;
		editor.putInt( "entrance_month" , month + 1 );
		int day = curTime.monthDay;
		editor.putInt( "entrance_day" , day );
		int hour = curTime.hour;
		editor.putInt( "entrance_hour" , hour );
		editor.putString( "recommendVersion" , newVersion );
		editor.commit();
	}
	
	public void readXML(
			InputStream inStream )
	{
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// ContentResolver mContentResolver = mcontext.getContentResolver();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		String apkUrl = "";
		String iconUrl = "";
		// String newVersion = "";
		String isUpdate = "";
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse( inStream );
			Element root = dom.getDocumentElement();
			isUpdate = root.getAttribute( "isupdate" );
			if( isUpdate.equals( "0" ) )
			{
				return;
			}
			db.execSQL( "DROP TABLE IF EXISTS " + Profile.TABLE_NAME + ";" );
			db.execSQL( "CREATE TABLE IF NOT EXISTS " + Profile.TABLE_NAME + "(" + Profile.COLUMN_ITEMTYPE + " TEXT," + Profile.COLUMN_PACKAGE + " TEXT," + Profile.COLUMN_VERSION + " TEXT," + Profile.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Profile.COLUMN_NAME_CH + " TEXT," + Profile.COLUMN_NAME_EN + " TEXT," + Profile.COLUMN_APK + " TEXT," + Profile.COLUMN_ICON + " TEXT," + Profile.COLUMN_URL_APK + " TEXT," + Profile.COLUMN_URL_ICON + " TEXT);" );
			// mContentResolver.delete(Profile.CONTENT_URI, null,null);
			newVersion = root.getAttribute( "ver" );
			// 查找所有icon_down_url节点
			NodeList items2 = root.getElementsByTagName( "pic" );
			for( int i = 0 ; i < items2.getLength() ; i++ )
			{
				// 得到第一个unlock_entrance_file节点
				Element personNode = (Element)items2.item( i );
				// 获取unlock_entrance_file节点的属性�?
				iconUrl = personNode.getTextContent();
			}
			// 查找所有apk_down_url节点
			NodeList items3 = root.getElementsByTagName( "app" );
			for( int i = 0 ; i < items3.getLength() ; i++ )
			{
				// 得到第一个unlock_entrance_file节点
				Element personNode = (Element)items3.item( i );
				// 获取unlock_entrance_file节点的属性�?
				apkUrl = personNode.getTextContent();
			}
			ContentValues values1 = new ContentValues();
			values1.put( Profile.COLUMN_VERSION , newVersion );
			values1.put( Profile.COLUMN_URL_ICON , iconUrl );
			values1.put( Profile.COLUMN_URL_APK , apkUrl );
			db.insert( Profile.TABLE_NAME , null , values1 );
			// mContentResolver.insert(Profile.CONTENT_URI, values1);
			// 查找所有item_data节点
			NodeList items4 = root.getElementsByTagName( "ui" );
			for( int i = 0 ; i < items4.getLength() ; i++ )
			{
				ContentValues values = new ContentValues();
				// 得到第一个item_data节点
				Element personNode = (Element)items4.item( i );
				NodeList items5 = personNode.getElementsByTagName( "z" );
				for( int j = 0 ; j < items5.getLength() ; j++ )
				{
					// 得到第一个unlock_entrance_file节点
					Element personNode1 = (Element)items5.item( j );
					values.put( Profile.COLUMN_ITEMTYPE , personNode1.getTextContent() );
				}
				NodeList items6 = personNode.getElementsByTagName( "a" );
				for( int j = 0 ; j < items6.getLength() ; j++ )
				{
					// 得到第一个unlock_entrance_file节点
					Element personNode1 = (Element)items6.item( j );
					values.put( Profile.COLUMN_NAME_CH , personNode1.getTextContent() );
				}
				NodeList items7 = personNode.getElementsByTagName( "b" );
				for( int j = 0 ; j < items7.getLength() ; j++ )
				{
					// 得到第一个unlock_entrance_file节点
					Element personNode1 = (Element)items7.item( j );
					values.put( Profile.COLUMN_PACKAGE , personNode1.getTextContent() );
				}
				NodeList items8 = personNode.getElementsByTagName( "h" );
				for( int j = 0 ; j < items8.getLength() ; j++ )
				{
					// 得到第一个unlock_entrance_file节点
					Element personNode1 = (Element)items8.item( j );
					values.put( Profile.COLUMN_NAME_EN , personNode1.getTextContent() );
				}
				NodeList items9 = personNode.getElementsByTagName( "j" );
				for( int j = 0 ; j < items9.getLength() ; j++ )
				{
					// 得到第一个unlock_entrance_file节点
					Element personNode1 = (Element)items9.item( j );
					values.put( Profile.COLUMN_ICON , personNode1.getTextContent() );
				}
				values.put( Profile.COLUMN_URL_APK , apkUrl );
				values.put( Profile.COLUMN_URL_ICON , iconUrl );
				// 通过ContentResolver来向数据库插入数�?
				db.insert( Profile.TABLE_NAME , null , values );
				// mContentResolver.insert(Profile.CONTENT_URI, values);
			}
			db.close();
			recordTime();
			Intent intent = new Intent( StaticClass.ACTION_THEME_UPDATE_RECOMMEND );
			mcontext.sendBroadcast( intent );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}
