package com.coco.theme.themebox.apprecommend;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;


/**
 * contentprovider的调用者有可能是Activity,Service,Application�?种context，被谁调�?
 * getContext就是�?
 */
public class MyProvider extends ContentProvider
{
	
	/*
	 * DBHelper mDbHelper = null; SQLiteDatabase db = null;
	 * 
	 * private static final UriMatcher mMatcher; // 1.第一步把你需要匹配Uri路径全部给注册上
	 * static { // UriMatcher：用于匹配Uri //
	 * 常量UriMatcher.NO_MATCH表示不匹配任何路径的返回�?-1)�? mMatcher = new
	 * UriMatcher(UriMatcher.NO_MATCH); //
	 * 如果match()方法匹配content://com.wirlessqa.content.provider/profile路径，返回匹配码�?
	 * mMatcher.addURI(Profile.AUTOHORITY, Profile.TABLE_NAME, Profile.ITEM); //
	 * 添加需要匹配uri，如果匹配就会返回匹配码 // 如果match()方法匹配 //
	 * content://com.wirlessqa.content.provider/profile/#路径，返回匹配码�?
	 * mMatcher.addURI(Profile.AUTOHORITY, Profile.TABLE_NAME + "/#",
	 * Profile.ITEM_ID); // #号为通配�? //
	 * 注册完需要匹配的Uri后，就可以使用mMatcher.match(uri)方法对输入的Uri进行匹配，如果匹配就返回匹配码， //
	 * 匹配码是调用addURI
	 * ()方法传入的第三个参数，假设匹配content://com.wirelessqa.content.provider/profile路径
	 * ，返回的匹配码为1 }
	 */
	// onCreate()方法在ContentProvider创建后就会被调用，Android系统运行后，ContentProvider只有在被第一次使用它时才会被创建�?
	// @Override
	public boolean onCreate()
	{
		// mDbHelper = new DBHelper(getContext());
		// db = mDbHelper.getReadableDatabase();
		return true;
	}
	
	@Override
	public int delete(
			Uri uri ,
			String selection ,
			String[] selectionArgs )
	{
		/*
		 * long rowId; if (mMatcher.match(uri) != Profile.ITEM) { throw new
		 * IllegalArgumentException("Unknown URI" + uri); } rowId =
		 * db.delete(Profile.TABLE_NAME, selection, selectionArgs); //
		 * if(rowId>0){ // Uri noteUri =
		 * ContentUris.withAppendedId(Profile.CONTENT_URI, rowId); //
		 * getContext().getContentResolver().notifyChange(noteUri, null); // }
		 */
		return 0;
	}
	
	@Override
	public String getType(
			Uri uri )
	{
		// switch (mMatcher.match(uri)) {
		// case Profile.ITEM:
		// return Profile.CONTENT_TYPE;
		// case Profile.ITEM_ID:
		// return Profile.CONTENT_ITEM_TYPE;
		// default:
		// throw new IllegalArgumentException("Unknown URI" + uri);
		// }
		throw new IllegalArgumentException( "Unknown URI" + uri );
	}
	
	// 外部应用程序通过这个方法�?ContentProvider添加数据�? @Override
	public Uri insert(
			Uri uri ,
			ContentValues values )
	{
		long rowId;
		// mMatcher.match(uri)对输入的Uri进行匹配，如果匹配就返回匹配�?// if (mMatcher.match(uri)
		// != Profile.ITEM) {
		// throw new IllegalArgumentException("Unknown URI" + uri);
		// }
		// rowId = db.insert(Profile.TABLE_NAME, null, values); // 向数据库里插入数�?//
		// if (rowId > 0) {
		// // ContentUris.withAoppendedId 用于为路径加上ID部分
		// Uri noteUri = ContentUris
		// .withAppendedId(Profile.CONTENT_URI, rowId);
		// // 当外部应用需要对ContentProvider中的数据进行添加、删除、修改和查询操作时，可以使用ContentResolver
		// // 类来完成
		// // ContentResolver是属于context�?通过getContentResolver获取
		// //
		// ContentResolver可以通过registerContentObserver注册观察者（观察者是ContentObserver
		// // 的派生类�?// //
		// 一旦ContentProvider操作的数据变化后，调用ContentResolver的notifyChange方法即可通知到观察者（回调观察者的onChange方法�?//
		// // 注册观察者不是必须的，所有notifyChange不是必须调用�?//
		// getContext().getContentResolver().notifyChange(noteUri, null);
		// return noteUri;
		// }
		throw new SQLException( "Failed to insert row into " + uri );
	}
	
	@Override
	public Cursor query(
			Uri uri ,
			String[] projection ,
			String selection ,
			String[] selectionArgs ,
			String sortOrder )
	{
		Cursor c = null;
		// switch (mMatcher.match(uri)) {
		// case Profile.ITEM:
		// c = db.query(Profile.TABLE_NAME, projection, selection,
		// selectionArgs, null, null, sortOrder);
		// break;
		// case Profile.ITEM_ID:
		// c = db.query(Profile.TABLE_NAME, projection, Profile.COLUMN_ID
		// + "=" + uri.getLastPathSegment(), selectionArgs, null,
		// null, sortOrder);
		// break;
		// default:
		// throw new IllegalArgumentException("Unknown URI" + uri);
		// }
		// 从而在ContentService中注册contentservice的观察者，这个观察者是cursor的内部成员（cursor是一个接口，此处真正的cursor是sqlitecursor�?
		// //
		// 这样每个查询返回的cursor都能在contentprovider对应数据改变时得到通知，因为这些cursor都有一个成员注册成了contentservice的观察�?
		// c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public int update(
			Uri uri ,
			ContentValues values ,
			String selection ,
			String[] selectionArgs )
	{
		return 0;
	}
}
