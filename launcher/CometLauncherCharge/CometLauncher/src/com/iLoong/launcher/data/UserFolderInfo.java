package com.iLoong.launcher.data;


import android.content.ContentValues;
import java.util.ArrayList;
import com.iLoong.launcher.app.LauncherSettings;


/**
 * Represents a folder containing shortcuts or apps.
 */
public class UserFolderInfo extends FolderInfo
{
	
	/**
	 * The apps and shortcuts
	 */
	public ArrayList<ShortcutInfo> contents = new ArrayList<ShortcutInfo>();
	ArrayList<FolderListener> listeners = new ArrayList<FolderListener>();
	
	public UserFolderInfo()
	{
		itemType = LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER;
	}
	
	/**
	 * Add an app or shortcut
	 *
	 * @param item
	 */
	public void add(
			ShortcutInfo item )
	{
		contents.add( item );
		for( int i = 0 ; i < listeners.size() ; i++ )
		{
			listeners.get( i ).onAdd( item );
		}
		itemsChanged();
	}
	
	/**
	 * Remove an app or shortcut. Does not change the DB.
	 *
	 * @param item
	 */
	public void remove(
			ShortcutInfo item )
	{
		contents.remove( item );
		for( int i = 0 ; i < listeners.size() ; i++ )
		{
			listeners.get( i ).onRemove( item );
		}
		itemsChanged();
	}
	
	public void setTitle(
			CharSequence title )
	{
		this.title = title;
		for( int i = 0 ; i < listeners.size() ; i++ )
		{
			listeners.get( i ).onTitleChanged( title );
		}
	}
	
	@Override
	public void onAddToDatabase(
			ContentValues values )
	{
		super.onAddToDatabase( values );
		values.put( LauncherSettings.Favorites.TITLE , title.toString() );
	}
	
	public void addListener(
			FolderListener listener )
	{
		listeners.add( listener );
	}
	
	public void removeListener(
			FolderListener listener )
	{
		if( listeners.contains( listener ) )
		{
			listeners.remove( listener );
		}
	}
	
	public void itemsChanged()
	{
		for( int i = 0 ; i < listeners.size() ; i++ )
		{
			listeners.get( i ).onItemsChanged();
		}
	}
	
	@Override
	public void unbind()
	{
		super.unbind();
		listeners.clear();
	}
	
	public interface FolderListener
	{
		
		public void onAdd(
				ItemInfo item );
		
		public void onRemove(
				ItemInfo item );
		
		public void onTitleChanged(
				CharSequence title );
		
		public void onItemsChanged();
	}
}
