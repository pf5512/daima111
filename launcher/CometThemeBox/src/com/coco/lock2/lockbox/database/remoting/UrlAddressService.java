package com.coco.lock2.lockbox.database.remoting;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.coco.lock2.lockbox.database.model.AddressType;
import com.coco.lock2.lockbox.database.model.ApplicationType;


public class UrlAddressService
{
	
	public static final String FIELD_APPLICATION_TYPE = "applicationType";
	public static final String FIELD_ADDRESS_TYPE = "addressType";
	public static final String FIELD_ADDRESS = "address";
	public static final String TABLE_NAME = "urlAddress";
	public static final Uri CONTENT_URI = Uri.parse( "content://com.coco.lock2.lockbox/" + TABLE_NAME );
	private Context mContext;
	
	public UrlAddressService(
			Context context )
	{
		mContext = context;
	}
	
	public boolean batchInsertAddress(
			ApplicationType appType ,
			AddressType addrType ,
			String[] addrArr )
	{
		ContentValues[] cvs = new ContentValues[addrArr.length];
		for( int i = 0 ; i < addrArr.length ; i++ )
		{
			ContentValues cv = new ContentValues();
			cvs[i] = cv;
			cv.put( FIELD_APPLICATION_TYPE , appType.getValue() );
			cv.put( FIELD_ADDRESS_TYPE , addrType.getValue() );
			cv.put( FIELD_ADDRESS , addrArr[i] );
		}
		ContentResolver contentResolver = mContext.getContentResolver();
		contentResolver.bulkInsert( CONTENT_URI , cvs );
		return true;
	}
	
	public void deleteAddress(
			ApplicationType appType ,
			AddressType addrType )
	{
		ContentResolver contentResolver = mContext.getContentResolver();
		contentResolver.delete(
				CONTENT_URI ,
				String.format( "%s=? and %s=?" , FIELD_APPLICATION_TYPE , FIELD_ADDRESS_TYPE ) ,
				new String[]{ Integer.toString( appType.getValue() ) , Integer.toString( addrType.getValue() ) } );
	}
	
	public List<String> queryAddress(
			ApplicationType appType ,
			AddressType addrType )
	{
		List<String> result = new ArrayList<String>();
		Cursor cursor = null;
		try
		{
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query(
					CONTENT_URI ,
					new String[]{ FIELD_ADDRESS } ,
					String.format( "%s=? and %s=?" , FIELD_APPLICATION_TYPE , FIELD_ADDRESS_TYPE ) ,
					new String[]{ Integer.toString( appType.getValue() ) , Integer.toString( addrType.getValue() ) } ,
					null );
			while( cursor.moveToNext() )
			{
				result.add( cursor.getString( cursor.getColumnIndexOrThrow( FIELD_ADDRESS ) ) );
			}
		}
		finally
		{
			if( cursor != null )
			{
				cursor.close();
			}
		}
		return result;
	}
}
