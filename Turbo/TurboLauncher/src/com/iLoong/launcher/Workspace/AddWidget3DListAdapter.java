/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.Workspace;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


/**
 * Adapter showing the types of items that can be added to a {@link Workspace}.
 */
public class AddWidget3DListAdapter extends BaseAdapter
{
	
	private final LayoutInflater mInflater;
	private int mIconWidth = 0;
	private int mIconHeight = 0;
	private final ArrayList<ListItem> mItems = new ArrayList<ListItem>();
	public static final int ITEM_3D_WIDGET = 0;
	public static final int ITEM_CONTACT = 1;
	public static final int ITEM_LIVE_FOLDER = 2;
	public static final int ITEM_APPWIDGET = 3;
	public static final int ITEM_WALLPAPER = 4;
	public static final int ITEM_SHORTCUT = 5;
	public static final int LIST_3D_WIDGET = 6;
	public static final int ITEM_THEME_DEFAULT_ICON = 7;
	public static final int ITEM_THEME_DEFAULT_OTHER = 8;
	
	/**
	 * Specific item in our list.
	 */
	public class ListItem
	{
		
		public ResolveInfo resolveInfo;
		public CharSequence text;
		public Drawable image;
		public int actionTag;
		public boolean installed = true;
		public String packageName;
		public String apkName;
		public boolean isInternal = false;
		public int iconResourceId;
		
		public ListItem(
				Resources res ,
				int textResourceId ,
				Bitmap bitmap ,
				int actionTag ,
				ResolveInfo resolveInfo ,
				boolean installed )
		{
			text = res.getString( textResourceId );
			image = new BitmapDrawable( bitmap );
			this.actionTag = actionTag;
			this.resolveInfo = resolveInfo;
			this.installed = installed;
			this.packageName = resolveInfo.activityInfo.packageName;
		}
		
		public ListItem(
				String name ,
				Drawable image ,
				String packageName ,
				String apkName ,
				boolean installed )
		{
			this.text = name;
			this.image = image;
			this.installed = installed;
			this.packageName = packageName;
			this.apkName = apkName;
		}
	}
	
	public AddWidget3DListAdapter(
			iLoongLauncher launcher )
	{
		super();
		mIconWidth = mIconHeight = Utilities.sIconTextureWidth;
		mInflater = (LayoutInflater)launcher.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		PackageManager pm = launcher.getPackageManager();
		Intent intent = new Intent( "com.iLoong.widget" , null );
		List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		for( ResolveInfo resolve : mWidgetResolveInfoList )
		{
			try
			{
				String themeName = "";
				themeName = ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName();
				themeName = themeName.substring( themeName.lastIndexOf( "." ) + 1 );
				Context widgetPluginContext = iLoongApplication.getInstance().createPackageContext( resolve.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
				if( !themeName.equals( "iLoong" ) )
				{
					String packageName = resolve.activityInfo.packageName;
					String widgetName = packageName.substring( packageName.lastIndexOf( "." ) + 1 ).toLowerCase();
					themeName = Widget3DManager.getInstance().checkThemeExist( ThemeManager.getInstance().getCurrentThemeContext() , widgetName , themeName );
				}
				widgetPluginContext.getResources().updateConfiguration(
						iLoongApplication.getInstance().getResources().getConfiguration() ,
						iLoongApplication.getInstance().getResources().getDisplayMetrics() );
				int appNameIdentifier = widgetPluginContext.getResources().getIdentifier( "app_name" , "string" , resolve.activityInfo.packageName );
				InputStream is = null;
				String iconPath = "widget_ico.png";
				Bundle meta = resolve.activityInfo.applicationInfo.metaData;
				if( meta != null && meta.containsKey( "useTheme" ) )
				{
					boolean useTheme = meta.getBoolean( "useTheme" , false );
					if( useTheme )
					{
						themeName = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme;
						if( themeName == null || themeName.trim().equals( "" ) )
						{
							themeName = Widget3DManager.getInstance().getWidget3DTheme(
									resolve.activityInfo.packageName ,
									ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName() );
						}
						if( !themeName.equals( "iLoong" ) )
						{
							String packageName = resolve.activityInfo.packageName;
							String widgetName = packageName.substring( packageName.lastIndexOf( "." ) + 1 ).toLowerCase();
							themeName = Widget3DManager.getInstance().checkThemeExist( widgetPluginContext , widgetName , themeName );
						}
						iconPath = themeName + "/image/widget_ico.png";
					}
				}
				// teapotXu_20130225: add start
				// modified for: get resource from themeBox of Launcher
				String shortName = widgetPluginContext.getPackageName().substring( widgetPluginContext.getPackageName().lastIndexOf( "." ) + 1 ).toLowerCase();
				String iconPathInThemeMgr = "theme/widget/" + shortName + "/" + iconPath;
				is = ThemeManager.getInstance().getCurrentThemeInputStream( iconPathInThemeMgr );
				if( is == null )
				{
					is = widgetPluginContext.getAssets().open( iconPath );
				}
				// before:
				// is = widgetPluginContext.getAssets().open(iconPath);
				// teapotXu_20130225: add end
				Bitmap bitmap = ThemeManager.getInstance().getBitmap( is );
				is.close();
				ListItem item = new ListItem( widgetPluginContext.getResources() , appNameIdentifier , bitmap , ITEM_THEME_DEFAULT_ICON , resolve , true );
				if( DefaultLayout.isWidgetLoadByInternal( widgetPluginContext.getPackageName() ) )
				{
					item.isInternal = true;
				}
				mItems.add( item );
				DefaultLayout.RemoveDefWidgetWithPkgname( resolve.activityInfo.packageName );
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	public ResolveInfo getItemResolveInfo(
			int position )
	{
		ListItem item = (ListItem)getItem( position );
		return item.resolveInfo;
	}
}
