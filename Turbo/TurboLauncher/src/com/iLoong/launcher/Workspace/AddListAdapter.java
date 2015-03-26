/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.Workspace;


import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


/**
 * Adapter showing the types of items that can be added to a {@link Workspace}.
 */
public class AddListAdapter extends BaseAdapter
{
	
	private final LayoutInflater mInflater;
	private final ArrayList<ListItem> mItems = new ArrayList<ListItem>();
	private int mIconWidth = 0;
	private int mIconHeight = 0;
	//	public static final int ITEM_3D_WIDGET = 0;
	//	public static final int ITEM_CONTACT = 1;
	//	public static final int ITEM_LIVE_FOLDER = 2;
	//	public static final int ITEM_APPWIDGET = 3;
	//	public static final int ITEM_WALLPAPER = 4;
	//	public static final int ITEM_SHORTCUT = 5;
	public static final int ITEM_3D_WIDGET = 100;
	public static final int ITEM_CONTACT = 0;
	public static final int ITEM_LIVE_FOLDER = 1;
	public static final int ITEM_APPWIDGET = 2;
	public static final int ITEM_SHORTCUT = 3;
	public static final int ITEM_WALLPAPER = 4;
	
	/**
	 * Specific item in our list.
	 */
	public class ListItem
	{
		
		public final CharSequence text;
		public Drawable image;
		public final int actionTag;
		
		public ListItem(
				Resources res ,
				int textResourceId ,
				int imageResourceId ,
				int actionTag )
		{
			text = res.getString( textResourceId );
			if( imageResourceId != -1 )
			{
				image = res.getDrawable( imageResourceId );
			}
			else
			{
				image = null;
			}
			this.actionTag = actionTag;
		}
	}
	
	public AddListAdapter(
			iLoongLauncher launcher )
	{
		super();
		mIconWidth = mIconHeight = Tools.dip2px( iLoongLauncher.getInstance() , 48 );//Utilities.sIconTextureWidth;
		mInflater = (LayoutInflater)launcher.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		// Create default actions
		Resources res = launcher.getResources();
		//if (DefConfig.DEF_S3_SUPPORT==false)
		//{
		//		mItems.add(new ListItem(res, RR.string.group_3D_widgets,
		//				RR.drawable.ic_3dwidget, ITEM_3D_WIDGET));
		//}
		if( DefaultLayout.workspace_longclick_display_contacts == true )
		{
			mItems.add( new ListItem( res , RR.string.group_contacts , RR.drawable.ic_contact , ITEM_CONTACT ) );
		}
		mItems.add( new ListItem( res , RR.string.group_live_folders , RR.drawable.ic_folder , ITEM_LIVE_FOLDER ) );
		if( DefaultLayout.appbar_widgets_special_name == true )
		{
			mItems.add( new ListItem( res , RR.string.appbar_tab_widget_ex , RR.drawable.ic_launcher_appwidget , ITEM_APPWIDGET ) );
		}
		else
		{
			mItems.add( new ListItem( res , RR.string.group_widgets , RR.drawable.ic_launcher_appwidget , ITEM_APPWIDGET ) );
		}
		mItems.add( new ListItem( res , RR.string.group_shortcuts , RR.drawable.ic_launcher_shortcut , ITEM_SHORTCUT ) );
		mItems.add( new ListItem( res , RR.string.group_wallpapers , RR.drawable.ic_launcher_wallpaper_menu , ITEM_WALLPAPER ) );
		// shortcut is not in add list
		// mItems.add(new ListItem(res, R.string.group_shortcuts,
		// R.drawable.ic_launcher_shortcut, ITEM_SHORTCUT));
		//
		// //shortcut is not in add list
		// mItems.add(new ListItem(res, R.string.group_live_folders,
		// R.drawable.ic_launcher_folder, ITEM_LIVE_FOLDER));
	}
	
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		ListItem item = (ListItem)getItem( position );
		if( convertView == null )
		{
			convertView = mInflater.inflate( RR.layout.add_list_item , parent , false );
		}
		TextView textView = (TextView)convertView;
		textView.setTag( item );
		textView.setText( item.text );
		textView.setTextColor( R3D.addList_item_text_color );
		item.image = Utils3D.createIconThumbnail( item.image , mIconWidth , mIconHeight );
		textView.setCompoundDrawablesWithIntrinsicBounds( item.image , null , null , null );
		return convertView;
	}
	
	public int getCount()
	{
		return mItems.size();
	}
	
	public Object getItem(
			int position )
	{
		return mItems.get( position );
	}
	
	public long getItemId(
			int position )
	{
		return position;
	}
	
	public int getSelectId(
			int which )
	{
		if( DefaultLayout.workspace_longclick_display_contacts == true )
		{
			return which;
		}
		else
		{
			return which + 1;
		}
	}
}
