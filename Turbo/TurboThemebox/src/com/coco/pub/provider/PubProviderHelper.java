package com.coco.pub.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class PubProviderHelper
{
	
	static Context mContext = null;
	
	public PubProviderHelper(
			Context context )
	{
		mContext = context;
	}
	
	public static long insertValue(
			String table ,
			String name ,
			String value )
	{
		try
		{
			ContentResolver resolver = mContext.getContentResolver();
			Uri uri = null;
			if( com.coco.theme.themebox.util.FunctionConfig.personal_center_internal )
			{
				uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + table );
			}
			else
			{
				uri = Uri.parse( "content://" + PubContentProvider.PERSONAL_CENTER_AUTHORITY + "/" + table );
			}
			ContentValues values = new ContentValues();
			values.put( "propertyName" , name );
			values.put( "propertyValue" , value );
			Uri result = resolver.insert( uri , values );
			if( result != null )
			{
				return ContentUris.parseId( result );
			}
			else
			{
				return 0;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return 0;
	}
	
	public static int updateValue(
			String table ,
			String name ,
			String value )
	{
		try
		{
			ContentResolver resolver = mContext.getContentResolver();
			Uri uri = null;
			if( com.coco.theme.themebox.util.FunctionConfig.personal_center_internal )
			{
				uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + table );
			}
			else
			{
				uri = Uri.parse( "content://" + PubContentProvider.PERSONAL_CENTER_AUTHORITY + "/" + table );
			}
			String where = " propertyName=? ";
			String[] args = new String[]{ name };
			ContentValues values = new ContentValues();
			values.put( "propertyName" , name );
			values.put( "propertyValue" , value );
			return resolver.update( uri , values , where , args );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			return 0;
		}
	}
	
	public static long insertValue(
			String authority ,
			String table ,
			String name ,
			String value )
	{
		try
		{
			ContentResolver resolver = mContext.getContentResolver();
			Uri uri = null;
			uri = Uri.parse( "content://" + authority + "/" + table );
			ContentValues values = new ContentValues();
			values.put( "propertyName" , name );
			values.put( "propertyValue" , value );
			Uri result = resolver.insert( uri , values );
			if( result != null )
			{
				return ContentUris.parseId( result );
			}
			else
			{
				return 0;
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		return 0;
	}
	
	public static int updateValue(
			String authority ,
			String table ,
			String name ,
			String value )
	{
		try
		{
			ContentResolver resolver = mContext.getContentResolver();
			Uri uri = null;
			uri = Uri.parse( "content://" + authority + "/" + table );
			String where = " propertyName=? ";
			String[] args = new String[]{ name };
			ContentValues values = new ContentValues();
			values.put( "propertyName" , name );
			values.put( "propertyValue" , value );
			return resolver.update( uri , values , where , args );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			return 0;
		}
	}
	
	public static void addOrUpdateValue(
			String table ,
			String name ,
			String value )
	{
		try
		{
			ContentResolver resolver = mContext.getContentResolver();
			Uri uri = null;
			if( com.coco.theme.themebox.util.FunctionConfig.personal_center_internal )
			{
				uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + table );
			}
			else
			{
				uri = Uri.parse( "content://" + PubContentProvider.PERSONAL_CENTER_AUTHORITY + "/" + table );
			}
			String where = " propertyName=? ";
			String[] args = new String[]{ name };
			Cursor cursor = resolver.query( uri , null , where , args , null );
			if( cursor != null )
			{
				int cursorCount = cursor.getCount();
				cursor.close();
				if( cursorCount > 0 )
				{
					updateValue( table , name , value );
				}
				else
				{
					insertValue( table , name , value );
				}
			}
			else
			{
				insertValue( table , name , value );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public String queryValue(
			String table ,
			String name )
	{
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = null;
		if( com.coco.theme.themebox.util.FunctionConfig.personal_center_internal )
		{
			uri = Uri.parse( "content://" + PubContentProvider.LAUNCHER_AUTHORITY + "/" + table );
		}
		else
		{
			uri = Uri.parse( "content://" + PubContentProvider.PERSONAL_CENTER_AUTHORITY + "/" + table );
		}
		String where = " propertyName=? ";
		String[] args = new String[]{ name };
		Cursor cursor = resolver.query( uri , null , where , args , null );
		String result = null;
		if( cursor != null )
		{
			if( cursor.getCount() > 0 )
			{
				cursor.moveToFirst();
				int index = cursor.getColumnIndex( "propertyName" );
				if( index != -1 )
				{
					result = cursor.getString( index );
				}
				else
				{
					result = null;
				}
			}
			else
			{
				result = null;
			}
			cursor.close();
		}
		else
		{
			result = null;
		}
		return result;
	}
	
	public static void addOrUpdateValue(
			String authority ,
			String table ,
			String name ,
			String value )
	{
		try
		{
			ContentResolver resolver = mContext.getContentResolver();
			Uri uri = null;
			uri = Uri.parse( "content://" + authority + "/" + table );
			String where = " propertyName=? ";
			String[] args = new String[]{ name };
			Cursor cursor = resolver.query( uri , null , where , args , null );
			if( cursor != null )
			{
				int cursorCount = cursor.getCount();
				cursor.close();
				if( cursorCount > 0 )
				{
					updateValue( authority , table , name , value );
				}
				else
				{
					insertValue( authority , table , name , value );
				}
			}
			else
			{
				insertValue( authority , table , name , value );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
}
