package com.iLoong.launcher.DesktopEdit;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.coco.download.Assets;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.coco.wallpaper.wallpaperbox.WallpaperInfo;
import com.coco.wallpaper.wallpaperbox.WallpaperInformation;
import com.coco.wallpaper.wallpaperbox.WallpaperService;
import com.cooeeui.brand.turbolauncher.R;


public class WallpaperQuery
{
	
	private Context mContext;
	private String currentWallpaper;;
	public List<WallpaperInformation> localList = new ArrayList<WallpaperInformation>();
	private Bitmap imgDefaultThumb;
	private DownModule downThumb;
	private Set<String> packageNameSet = new HashSet<String>();
	List<ResolveInfo> mResolveInfoList = new ArrayList<ResolveInfo>();
	private List<String> mThumbs = new ArrayList<String>( 24 );
	private Set<ImageView> recycle = new HashSet<ImageView>();
	
	public WallpaperQuery(
			Context context ,
			DownModule module )
	{
		mContext = context;
		downThumb = module;
		imgDefaultThumb = ( (BitmapDrawable)mContext.getResources().getDrawable( R.drawable.default_img_large ) ).getBitmap();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
		currentWallpaper = Assets.getWallpaper( context , "currentWallpaper" );
		if( currentWallpaper == null || currentWallpaper.trim().length() == 0 )
		{
			currentWallpaper = "default";
		}
		infos = new WallpaperInfo( mContext );
		getWallpapersInfo();
		queryPackage();
	}
	
	String[] galleryPkg;
	private boolean isfindGallery = false;
	private boolean isfindLivepaper = false;
	private String GALLERY = "";
	private final String LIVEPICKER = "com.android.wallpaper.livepicker";
	private final String UPDATA_CURRENT = "com.coco.wallpaper.update";
	private final String HTC_ONE_PACK_NAME = "com.htc.home.personalize";
	private final String HTC_ONE_RESOLVE_NAME = "com.htc.home.personalize.picker.WallpaperLivePicker";
	public List<Bitmap> localBmp = new ArrayList<Bitmap>();
	public List<WallpaperImage> wallpaperimage = new ArrayList<WallpaperImage>();
	private WallpaperInfo infos;
	boolean useCustomWallpaper = false;
	private final String wallpaperPath = "launcher/wallpapers";
	String customWallpaperPath;
	
	private void getWallpapersInfo()
	{
		List<ResolveInfo> temp;
		galleryPkg = FunctionConfig.getGalleryPkg().split( ";" );
		Intent pickWallpaper = new Intent( Intent.ACTION_SET_WALLPAPER );
		temp = mContext.getPackageManager().queryIntentActivities( pickWallpaper , PackageManager.GET_ACTIVITIES );
		mResolveInfoList.clear();
		packageNameSet.clear();
		ResolveInfo gallertResolveInfo = null;
		ResolveInfo LivepaperResolveInfo = null;
		for( ResolveInfo info : temp )
		{
			String packagename = info.activityInfo.packageName;
			String name = info.activityInfo.name;
			if( !isfindGallery )
			{
				for( int i = 0 ; i < galleryPkg.length ; i++ )
				{
					if( galleryPkg[i].equals( packagename ) )
					{
						GALLERY = packagename;
						//							mResolveInfoList.add( info );
						gallertResolveInfo = info;
						isfindGallery = true;
						break;
					}
				}
			}
			if( !isfindLivepaper )
			{
				if( LIVEPICKER.equals( packagename ) || ( packagename.equals( HTC_ONE_PACK_NAME ) && name.equals( HTC_ONE_RESOLVE_NAME ) ) )
				{
					LivepaperResolveInfo = info;
					isfindLivepaper = true;
				}
			}
		}
		if( gallertResolveInfo != null )
		{
			mResolveInfoList.add( gallertResolveInfo );
		}
		else
		{
			if( temp != null && temp.size() > 0 )
			{
				if( !isfindGallery )
				{
					mResolveInfoList.add( temp.get( 0 ) );
				}
			}
		}
		if( LivepaperResolveInfo != null )
		{
			mResolveInfoList.add( LivepaperResolveInfo );
		}
		else
		{
			if( temp != null && temp.size() > 0 )
			{
				if( !isfindLivepaper )
				{
					mResolveInfoList.add( temp.get( 0 ) );
				}
			}
		}
		temp = mContext.getPackageManager().queryIntentActivities( new Intent( "com.coco.action.wallpaper" ) , PackageManager.GET_ACTIVITIES );
		for( ResolveInfo info : temp )
		{
			String packagename = info.activityInfo.packageName;
			String cls = info.activityInfo.name;
			if( FunctionConfig.isStatictoIcon() )
			{
				if( mContext.getPackageName().equals( packagename ) && "com.coco.wallpaper.wallpaperbox.WallpaperPreviewActivity".equals( cls ) )
				{
					mResolveInfoList.add( info );
				}
			}
			if( FunctionConfig.isLockwallpaperShow() )
			{
				if( mContext.getPackageName().equals( packagename ) && "com.coco.wallpaper.wallpaperbox.LockWallpaperPreview".equals( cls ) )
				{
					mResolveInfoList.add( info );
				}
			}
		}
		if( FunctionConfig.isEnable_topwise_style() )
		{
			Intent it = new Intent();
			it.setPackage( "topwise.shark.wallpaperSet" );
			it.addCategory( "android.intent.category.LAUNCHER" );
			temp = mContext.getPackageManager().queryIntentActivities( it , PackageManager.GET_ACTIVITIES );
			for( ResolveInfo info : temp )
			{
				mResolveInfoList.add( info );
			}
		}
		if( !FunctionConfig.isStatictoIcon() )
		{
			if( FunctionConfig.getWallpapers_from_other_apk() != null )
			{
				try
				{
					Context remountContext = mContext.createPackageContext( FunctionConfig.getWallpapers_from_other_apk() , Context.CONTEXT_IGNORE_SECURITY );
					Resources res = remountContext.getResources();
					for( int i = 1 ; ; i++ )
					{
						try
						{
							int drawable = res.getIdentifier( "wallpaper_" + ( i < 10 ? "0" + i : i ) + "_small" , "drawable" , FunctionConfig.getWallpapers_from_other_apk() );
							if( drawable == 0 )
							{
								break;
							}
							Bitmap bitmap = Tools.drawableToBitmap( res.getDrawable( drawable ) );
							mThumbs.add( "wallpaper_" + ( i < 10 ? "0" + i : i ) );
							localBmp.add( bitmap );
						}
						catch( IllegalArgumentException e )
						{
							e.printStackTrace();
						}
					}
					return;
				}
				catch( NameNotFoundException e )
				{
					Log.e( "tabwallpaper" , "createPackageContext exception: " + e );
				}
				return;
			}
			infos.findWallpapers( mThumbs );
			useCustomWallpaper = infos.isUseCustomWallpaper();
			customWallpaperPath = infos.getCustomWallpaperPath();
			for( String str : mThumbs )
			{
				InputStream is = null;
				if( useCustomWallpaper )
				{
					try
					{
						is = new FileInputStream( customWallpaperPath + "/" + str );
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
					}
				}
				else
				{
					try
					{
						Context remoteContext = mContext.createPackageContext( ThemesDB.LAUNCHER_PACKAGENAME , Context.CONTEXT_IGNORE_SECURITY );
						AssetManager asset = remoteContext.getResources().getAssets();
						try
						{
							is = asset.open( wallpaperPath + "/" + str );
						}
						catch( IOException e )
						{
							e.printStackTrace();
						}
					}
					catch( NameNotFoundException e1 )
					{
						e1.printStackTrace();
					}
				}
				if( is != null )
				{
					Bitmap bitmap = BitmapFactory.decodeStream( is );
					localBmp.add( bitmap );
					WallpaperImage wi = new WallpaperImage();
					wi.setName( str );
					wi.setBit( bitmap );
					wallpaperimage.add( wi );
					try
					{
						is.close();
					}
					catch( IOException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void queryPackage()
	{
		if( !FunctionConfig.isStatictoIcon() )
		{
			packageNameSet.clear();
			//			for( ResolveInfo info : mResolveInfoList )
			//			{
			//				packageNameSet.add( info.activityInfo.packageName + " local" );
			//			}
			for( String str : mThumbs )
			{
				packageNameSet.add( str + " local" );
			}
			WallpaperService themeSv = new WallpaperService( mContext );
			List<WallpaperInformation> installList = themeSv.queryDownloadList();
			localList.clear();
			Log.v( "tagWallpaper" , "installList.size() = " + installList.size() );
			for( WallpaperInformation info : installList )
			{
				info.setThumbImage( mContext , info.getPackageName() , info.getClassName() );
				localList.add( info );
				packageNameSet.add( info.getPackageName() );
			}
			Log.v( "tagWallpaper" , Thread.currentThread().getId() + "locallIST.SIZE-- = " + localList.size() );
		}
	}
}
