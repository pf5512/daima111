/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.cooee.android.launcher.framework;


import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.theme.ThemeManager;


/**
 * Cache of application icons.  Icons can be made from any thread.
 */
public class IconCache
{
	
	private static final String TAG = "Launcher.IconCache";
	private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
	
	private static class CacheEntry
	{
		
		public Bitmap icon;
		public String title;
		public Bitmap titleBitmap;
	}
	
	public static Drawable d;
	public static Bitmap mDefaultIcon;
	private final Context mContext;
	private static PackageManager mPackageManager;
	//    private final Utilities.BubbleText mBubble;
	private final HashMap<ComponentName , CacheEntry> mCache = new HashMap<ComponentName , CacheEntry>( INITIAL_ICON_CACHE_CAPACITY );
	
	public IconCache(
			Context context )
	{
		mContext = context;
		mPackageManager = context.getPackageManager();
		d = mPackageManager.getDefaultActivityIcon();
		//        mBubble = new Utilities.BubbleText(context);
		mDefaultIcon = makeDefaultIcon();
	}
	
	public static Bitmap makeDefaultIcon()
	{
		//        Drawable d = mPackageManager.getDefaultActivityIcon();
		Bitmap b = Bitmap.createBitmap( Math.max( d.getIntrinsicWidth() , 1 ) , Math.max( d.getIntrinsicHeight() , 1 ) , Bitmap.Config.ARGB_8888 );
		Canvas c = new Canvas( b );
		d.setBounds( 0 , 0 , b.getWidth() , b.getHeight() );
		d.draw( c );
		return b;
	}
	
	/**
	 * Remove any records for the supplied ComponentName.
	 */
	public void remove(
			ComponentName componentName )
	{
		synchronized( mCache )
		{
			mCache.remove( componentName );
		}
	}
	
	/**
	 * Empty out the cache.
	 */
	public void flush()
	{
		synchronized( mCache )
		{
			mCache.clear();
		}
	}
	
	public void flushIcon(
			Intent intent )
	{
		synchronized( mCache )
		{
			if( intent == null )
				return;
			ComponentName component = intent.getComponent();
			if( component == null )
			{
				return;
			}
			CacheEntry entry = mCache.get( component );
			if( entry != null )
			{
				Bitmap icon = entry.icon;
				if( icon == null || icon.isRecycled() )
				{
					mCache.remove( component );
				}
			}
		}
	}
	
	/**
	 * Fill in "application" with the icon and label for "info."
	 */
	public void getTitleAndIcon(
			ApplicationInfo application ,
			ResolveInfo info )
	{
		synchronized( mCache )
		{
			CacheEntry entry = cacheLocked( application.componentName , info );
			//            if (entry.titleBitmap == null) {
			//                entry.titleBitmap = mBubble.createTextBitmap(entry.title);
			//            }
			application.title = entry.title;
			application.titleBitmap = entry.titleBitmap;
			application.iconBitmap = entry.icon;
		}
	}
	
	public String getLabel(
			ResolveInfo info )
	{
		String label = info.loadLabel( mPackageManager ).toString();
		if( label == null )
		{
			label = info.activityInfo.name;
		}
		return label;
	}
	
	public Bitmap getIcon(
			Intent intent )
	{
		synchronized( mCache )
		{
			ComponentName component = intent.getComponent();
			if( component == null )
			{
				return mDefaultIcon;
			}
			CacheEntry entry = mCache.get( component );
			if( entry != null )
				return entry.icon;
			final ResolveInfo resolveInfo = mPackageManager.resolveActivity( intent , 0 );
			if( resolveInfo == null )
			{
				return mDefaultIcon;
			}
			entry = cacheLocked( component , resolveInfo );
			return entry.icon;
		}
	}
	
	public void setIcon(
			ResolveInfo info ,
			Bitmap bitmap )
	{
		ComponentName component = new ComponentName( info.activityInfo.applicationInfo.packageName , info.activityInfo.name );
		if( info == null || component == null )
		{
			if( bitmap != null )
				bitmap.recycle();
			return;
		}
		CacheEntry entry = mCache.get( component );
		if( entry == null )
		{
			entry = new CacheEntry();
			mCache.put( component , entry );
			entry.title = info.loadLabel( mPackageManager ).toString();
			if( entry.title == null )
			{
				entry.title = info.activityInfo.name;
			}
			entry.icon = bitmap;
		}
		else
		{
			if( entry.icon != null )
				entry.icon.recycle();
			entry.icon = bitmap;
			mCache.put( component , entry );
		}
	}
	
	public Bitmap getIcon(
			ComponentName component ,
			ResolveInfo resolveInfo )
	{
		synchronized( mCache )
		{
			if( !LauncherModel.mWorkspaceLoaded )
			{
				return null;
			}
			if( resolveInfo == null || component == null )
			{
				return null;
			}
			CacheEntry entry = cacheLocked( component , resolveInfo );
			return entry.icon;
		}
	}
	
	public boolean isDefaultIcon(
			Bitmap icon )
	{
		return mDefaultIcon == icon;
	}
	
	private CacheEntry cacheLocked(
			ComponentName componentName ,
			ResolveInfo info )
	{
		CacheEntry entry = mCache.get( componentName );
		if( entry == null )
		{
			DefaultLayout.getInstance().getDefaultIcon( info );
			entry = mCache.get( componentName );
			if( entry == null )
			{
				entry = new CacheEntry();
				mCache.put( componentName , entry );
				entry.title = info.loadLabel( mPackageManager ).toString();
				if( entry.title == null )
				{
					entry.title = info.activityInfo.name;
				}
				String appPackName = info.activityInfo.applicationInfo.packageName;
				String appClassName = info.activityInfo.name;
				int iconSize = DefaultLayout.app_icon_size;
				if( !R3D.doNotNeedScale( appPackName , appClassName ) )
					iconSize *= DefaultLayout.thirdapk_icon_scaleFactor;
				entry.icon = Utilities.createIconBitmap( info.activityInfo.loadIcon( mPackageManager ) , mContext , iconSize );
			}
			else
			{
				String appPackageName = info.activityInfo.applicationInfo.packageName;
				String appClassName = componentName.getClassName();
				String iconPath = DefaultLayout.getInstance().getReplaceIconPath( appPackageName , appClassName );
				if( null == ThemeManager.getInstance().getCurrentThemeBitmap( iconPath ) )
				{
					entry.icon = Tools.resizeBitmap(
							entry.icon ,
							(int)( DefaultLayout.app_icon_size * DefaultLayout.thirdapk_icon_scaleFactor ) ,
							(int)( DefaultLayout.app_icon_size * DefaultLayout.thirdapk_icon_scaleFactor ) );
				}
			}
		}
		return entry;
	}
	
	public Drawable getFullResIcon(
			String packageName ,
			int iconId )
	{
		Resources resources;
		try
		{
			resources = mPackageManager.getResourcesForApplication( packageName );
		}
		catch( PackageManager.NameNotFoundException e )
		{
			resources = null;
		}
		if( resources != null )
		{
			if( iconId != 0 )
			{
				return mPackageManager.getDrawable( packageName , iconId , null );
			}
		}
		return null;
	}
	
	public Drawable getFullResIcon(
			ResolveInfo info )
	{
		Resources resources;
		try
		{
			resources = mPackageManager.getResourcesForApplication( info.activityInfo.applicationInfo );
		}
		catch( PackageManager.NameNotFoundException e )
		{
			resources = null;
		}
		if( resources != null )
		{
			int iconId = info.activityInfo.getIconResource();
			if( iconId != 0 )
			{
				return resources.getDrawable( iconId );
			}
		}
		return null;
	}
	
	public Drawable getDefaultIconDrawable()
	{
		return mPackageManager.getDefaultActivityIcon();
	}
}
