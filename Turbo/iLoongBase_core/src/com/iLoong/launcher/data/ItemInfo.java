/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.data;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.graphics.Bitmap;

import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.Desktop3D.Log;


/**
 * Represents an item in the launcher.
 */
public class ItemInfo
{
	
	public static final int NO_ID = -1;
	/**
	 * The id in the settings database for this item
	 */
	public long id = NO_ID;
	/**
	 * One of {@link LauncherSettings.Favorites#ITEM_TYPE_APPLICATION},
	 * {@link LauncherSettings.Favorites#ITEM_TYPE_SHORTCUT},
	 * {@link LauncherSettings.Favorites#ITEM_TYPE_USER_FOLDER}, or
	 * {@link LauncherSettings.Favorites#ITEM_TYPE_APPWIDGET}.
	 */
	public int itemType;
	/**
	 * The id of the container that holds this item. For the desktop, this will be 
	 * {@link LauncherSettings.Favorites#CONTAINER_DESKTOP}. For the all applications folder it
	 * will be {@link #NO_ID} (since it is not stored in the settings DB). For user folders
	 * it will be the id of the folder.
	 */
	public long container = NO_ID;
	/**
	 * Iindicates the screen in which the shortcut appears.
	 */
	public int screen = -1;
	/**
	 * The application name.
	 */
	public CharSequence title;
	//added for workspace
	//坐标位置为相对当前的Layout,
	//相对坐标位置x
	public int x = 0;
	//相对坐标位置y
	public int y = 0;
	//    //added for multi-selected
	//    public boolean isSelected = false;
	//    private static Bitmap multiBmp;
	//    private static Bitmap backgroundBmp;
	//3D dockbar中区分装载在那个页面上
	public int angle = 0;
	//added for workspace
	/**
	 * Indicates the X position of the associated cell.
	 */
	public int cellX = -1;
	/**
	 * Indicates the Y position of the associated cell.
	 */
	public int cellY = -1;
	public int cellTempX = -1;
	public int cellTempY = -1;
	/**
	 * Indicates the X cell span.
	 */
	public int spanX = 0;
	/**
	 * Indicates the Y cell span.
	 */
	public int spanY = 0;
	/**
	 * Indicates whether the item is a gesture.
	 */
	boolean isGesture = false;
	//teapotXu add start for New added app flag
	//it can be extendible, now -1 is the default value, 
	//0 indicates that it is the new added item.
	public int extendible_flag = -1;
	//teapotXu add start for New added app flag
	public int location_in_mainmenu = -1;//xiatian add	//for mainmenu sort by user
	
	ItemInfo()
	{
	}
	
	public ItemInfo(
			ItemInfo info )
	{
		id = info.id;
		cellX = info.cellX;
		cellY = info.cellY;
		spanX = info.spanX;
		spanY = info.spanY;
		screen = info.screen;
		itemType = info.itemType;
		container = info.container;
		//added for workspace
		x = info.x;
		y = info.y;
	}
	
	/**
	 * Write the fields of this item to the DB
	 * 
	 * @param values
	 */
	public void onAddToDatabase(
			ContentValues values )
	{
		values.put( LauncherSettings.BaseLauncherColumns.ITEM_TYPE , itemType );
		if( !isGesture )
		{
			values.put( LauncherSettings.Favorites.CONTAINER , container );
			values.put( LauncherSettings.Favorites.SCREEN , screen );
			values.put( LauncherSettings.Favorites.CELLX , cellX );
			values.put( LauncherSettings.Favorites.CELLY , cellY );
			values.put( LauncherSettings.Favorites.SPANX , spanX );
			values.put( LauncherSettings.Favorites.SPANY , spanY );
			values.put( LauncherSettings.Favorites.X , x );
			values.put( LauncherSettings.Favorites.Y , y );
			values.put( LauncherSettings.Favorites.ANGLE , angle );
		}
	}
	
	public static byte[] flattenBitmap(
			Bitmap bitmap )
	{
		// Try go guesstimate how much space the icon will take when serialized
		// to avoid unnecessary allocations/copies during the write.
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream( size );
		try
		{
			bitmap.compress( Bitmap.CompressFormat.PNG , 100 , out );
			out.flush();
			out.close();
			return out.toByteArray();
		}
		catch( IOException e )
		{
			Log.w( "Favorite" , "Could not write icon" );
			return null;
		}
	}
	
	static void writeBitmap(
			ContentValues values ,
			Bitmap bitmap )
	{
		if( bitmap != null && !bitmap.isRecycled() )
		{
			byte[] data = flattenBitmap( bitmap );
			values.put( LauncherSettings.Favorites.ICON , data );
		}
	}
	
	void unbind()
	{
	}
	
	@Override
	public String toString()
	{
		return "Item(id=" + this.id + " type=" + this.itemType + ")";
	}
}
