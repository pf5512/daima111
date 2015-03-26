/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.desktop;


import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.data.ShortcutInfo;


public class UninstallShortcutReceiver extends BroadcastReceiver
{
	
	private static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
	
	public void onReceive(
			Context context ,
			Intent data )
	{
		if( !ACTION_UNINSTALL_SHORTCUT.equals( data.getAction() ) )
		{
			return;
		}
		Intent intent = data.getParcelableExtra( Intent.EXTRA_SHORTCUT_INTENT );
		String name = data.getStringExtra( Intent.EXTRA_SHORTCUT_NAME );
		boolean duplicate = data.getBooleanExtra( iLoongLauncher.EXTRA_SHORTCUT_DUPLICATE , true );
		if( intent != null && name != null )
		{
			boolean changed = false;
			iLoongLauncher launcher = iLoongLauncher.getInstance();
			if( launcher == null )
				return;
			ArrayList<ShortcutInfo> new_add_shortcuts = launcher.d3dListener.getShortcutlist();
			//first query the shortcutList which will be added into launcher when launcher resumes.
			if( new_add_shortcuts != null && new_add_shortcuts.size() > 0 )
			{
				ArrayList<ShortcutInfo> removed_list = new ArrayList<ShortcutInfo>();
				for( ShortcutInfo addSInfo : new_add_shortcuts )
				{
					if( addSInfo.intent != null && addSInfo.intent.toString().equals( intent.toString() ) )
					{
						removed_list.add( addSInfo );
					}
				}
				if( removed_list.size() > 0 )
				{
					new_add_shortcuts.removeAll( removed_list );
					changed = true;
				}
			}
			final ContentResolver cr = context.getContentResolver();
			Cursor c = cr.query(
					LauncherSettings.Favorites.CONTENT_URI ,
					new String[]{ LauncherSettings.Favorites._ID , LauncherSettings.Favorites.INTENT } ,
					LauncherSettings.Favorites.TITLE + "=?" ,
					new String[]{ name } ,
					null );
			final int intentIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.INTENT );
			final int idIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites._ID );
			try
			{
				while( c.moveToNext() )
				{
					// try {
					//if (intent.filterEquals(Intent.parseUri(c.getString(intentIndex), 0))) 
					{
						//iLoongLauncher launcher = iLoongLauncher.getInstance();
						//if(launcher == null)return;
						if( true == launcher.deleteShortcutOnWorkspace( intent ) )
						{
							final long id = c.getLong( idIndex );
							final Uri uri = LauncherSettings.Favorites.getContentUri( id , false );
							cr.delete( uri , null , null );
							changed = true;
						}
						if( !duplicate )
						{
							break;
						}
					}
					//                    } catch (URISyntaxException e) {
					//                        // Ignore
					//                    }
				}
			}
			finally
			{
				c.close();
			}
			if( changed )
			{
				cr.notifyChange( LauncherSettings.Favorites.CONTENT_URI , null );
				Toast.makeText( context , context.getString( RR.string.shortcut_uninstalled , name ) , Toast.LENGTH_SHORT ).show();
			}
		}
	}
}
