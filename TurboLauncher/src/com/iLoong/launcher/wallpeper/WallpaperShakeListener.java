package com.iLoong.launcher.wallpeper;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.preference.PreferenceManager;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.desktop.WallpaperChangedReceiver;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.wallpeper.ShakeListener.OnShakeListener;


public class WallpaperShakeListener implements OnShakeListener
{
	
	private static final String TAG = "WallpaperShakeListener";
	private Context mContext;
	private static final String wallpaperPath = "launcher/wallpapers";
	private ArrayList<WallPaperFile> mWallPapers = null;
	private int mCurWallpaperIndex = 0;
	private static String customWallpaperPath;
	private boolean useCustomWallpaper = false;
	private static String customAssetsWallpaperPath;
	private boolean useCustomAssetsWallpaper = false;
	
	public WallpaperShakeListener(
			Context context )
	{
		this.mContext = context;
		findWallpapers();
	}
	
	private int getRandomWallpaperIndex()
	{
		Random rand = new Random();
		int max = mWallPapers == null ? 0 : mWallPapers.size();
		if( max == 0 )
		{
			return max;
		}
		int newIndex = rand.nextInt( max );
		int count = 0;
		while( newIndex == mCurWallpaperIndex )
		{
			newIndex = rand.nextInt( max );
			count++;
			if( count > 100 )
			{
				count = 0;
				break;
			}
			Log.v( TAG , "newIndex:" + newIndex );
		}
		mCurWallpaperIndex = newIndex;
		return newIndex;
	}
	
	private void findWallpapers()
	{
		mWallPapers = new ArrayList<WallPaperFile>( 24 );
		String[] wallpapers = null;
		if( !DefaultLayout.wallpapers_from_apk_packagename.equals( "" ) )
		{
			try
			{
				Context remountContext = mContext.createPackageContext( DefaultLayout.wallpapers_from_apk_packagename , Context.CONTEXT_IGNORE_SECURITY );
				Resources res = remountContext.getResources();
				for( int i = 1 ; ; i++ )
				{
					try
					{
						int drawable = res.getIdentifier( "wallpaper_" + ( i < 10 ? "0" + i : i ) , "drawable" , DefaultLayout.wallpapers_from_apk_packagename );
						if( drawable == 0 )
						{
							break;
						}
						System.out.println( "drawable = " + drawable + " i = " + i );
						WallPaperFile file = new WallPaperFile( drawable );
						mWallPapers.add( file );
					}
					catch( IllegalArgumentException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return;
			}
			catch( NameNotFoundException e )
			{
				Log.e( TAG , "createPackageContext exception: " + e );
			}
		}
		customWallpaperPath = DefaultLayout.custom_wallpapers_path;
		File dir = new File( customWallpaperPath );
		if( dir.exists() && dir.isDirectory() )
		{
			useCustomWallpaper = true;
		}
		else if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/wallpapers" ) )
		{
			customAssetsWallpaperPath = DefaultLayout.custom_assets_path + "/launcher/wallpapers";
			dir = new File( customAssetsWallpaperPath );
			if( dir.exists() && dir.isDirectory() )
			{
				useCustomAssetsWallpaper = true;
			}
		}
		//        try {
		//			if(dir.exists() && dir.isDirectory()){	        	
		//				wallpapers = dir.list();
		//			} else {
		//				AssetManager assManager = mContext.getResources().getAssets();
		//				wallpapers = assManager.list(wallpaperPath);
		//			}
		//			for(String name : wallpapers){
		//	        	Log.v("wallpaper",name);
		//	        	if(!name.contains("_small")){
		//	        		mImages.add(name);
		//	        	} 
		//	        	else{
		//	        		mTemp.add(name);
		//	        	}
		//	        }
		//			
		//        	for(String name : mImages){
		//  				for(String nameTmp : mTemp){
		//					if(name.equals(nameTmp.replace("_small", ""))){
		//						mThumbs.add(nameTmp);
		//						mFound.add(name);
		//						break ;
		//					}		
		//				}
		//        	}
		//        	mImages.clear();
		//			mImages.addAll(mFound);
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		try
		{
			if( useCustomWallpaper || useCustomAssetsWallpaper )
			{
				wallpapers = dir.list();
				for( String name : wallpapers )
				{
					if( !name.contains( "_small" ) && !name.contains( "_scene" ) )
					{
						WallPaperFile file = new WallPaperFile( name , WallPaperFileFromEnum.custom );
						mWallPapers.add( file );
					}
				}
			}
			else
			{
				AssetManager assManager = mContext.getResources().getAssets();
				wallpapers = assManager.list( wallpaperPath );
				for( String name : wallpapers )
				{
					if( !name.contains( "_small" ) && !name.contains( "_scene" ) )
					{
						WallPaperFile file = new WallPaperFile( name , WallPaperFileFromEnum.assets );
						mWallPapers.add( file );
					}
				}
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		String externalWallpapers = getExternalWallPath();
		//		if (externalWallpapers != null) {
		//			File wallPaperDir = new File(externalWallpapers);
		//			if (wallPaperDir.exists() && wallPaperDir.isDirectory()) {
		//				String[] files = wallPaperDir.list();
		//				for (int i = 0; i < files.length; i++) {
		//					WallPaperFile file = new WallPaperFile(files[i],
		//							WallPaperFileFromEnum.external);
		//					mWallPapers.add(file);
		//				}
		//			}
		//		}
	}
	
	private void selectWallpaper(
			int position )
	{
		InputStream stream = null;
		AssetManager asset = mContext.getResources().getAssets();
		try
		{
			WallpaperManager wpm = (WallpaperManager)mContext.getSystemService( Context.WALLPAPER_SERVICE );
			WallPaperFile file = mWallPapers.get( position );
			Log.v( "wallpaper" , "filename:" + file.fileName + "" + " from:" + file.fileFrom + " position:" + position );
			if( file.fileFrom == WallPaperFileFromEnum.assets )
			{
				stream = asset.open( wallpaperPath + "/" + file.fileName );
				wpm.setStream( stream );
			}
			else if( file.fileFrom == WallPaperFileFromEnum.custom )
			{
				try
				{
					if( useCustomWallpaper )
					{
						stream = new FileInputStream( customWallpaperPath + "/" + file.fileName );
					}
					else if( useCustomAssetsWallpaper )
					{
						stream = new FileInputStream( customAssetsWallpaperPath + "/" + file.fileName );
					}
					wpm.setStream( stream );
				}
				catch( FileNotFoundException e )
				{
					e.printStackTrace();
				}
			}
			else if( file.fileFrom == WallPaperFileFromEnum.otherapk )
			{
				if( !DefaultLayout.wallpapers_from_apk_packagename.equals( "" ) )
				{
					Context remountContext = mContext.createPackageContext( DefaultLayout.wallpapers_from_apk_packagename , Context.CONTEXT_IGNORE_SECURITY );
					Resources res = remountContext.getResources();
					Bitmap bitmap = Tools.drawableToBitmap( res.getDrawable( file.fileDrawable ) );
					if( bitmap != null && !bitmap.isRecycled() )
					{
						wpm.setBitmap( bitmap );
						bitmap.recycle();
					}
				}
			}
			//			Utils3D.showPidMemoryInfo(mContext, TAG);
			// wpm.setResource(0);
		}
		catch( Exception e )
		{
			Log.e( TAG , "Failed to set wallpaper: " + e );
		}
		finally
		{
			if( stream != null )
			{
				try
				{
					stream.close();
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
		if( DefaultLayout.wallpapers_from_apk_packagename.equals( "" ) )
		{
			pref.edit().putString( "currentWallpaper" , mWallPapers.get( position ).fileName ).commit();
			PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , mWallPapers.get( position ).fileName );
		}
		else
		{
			pref.edit().putString( "currentWallpaper" , "other" ).commit();
			PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , "other" );
		}
		pref.edit().putBoolean( "cooeechange" , true ).commit();
		PubProviderHelper.addOrUpdateValue( "wallpaper" , "cooeechange" , "true" );
	}
	
	MediaPlayer mp = null;
	
	private void playSound()
	{
		if( mp == null )
		{
			mp = MediaPlayer.create( mContext , RR.raw.shakenotify );
		}
		if( !mp.isPlaying() )
		{
			try
			{
				// mp.prepare();
				mp.setLooping( false );
				mp.setOnCompletionListener( new OnCompletionListener() {
					
					@Override
					public void onCompletion(
							MediaPlayer mp )
					{
						// TODO Auto-generated method stub
						Log.v( TAG , "playsound onCompletion" );
					}
				} );
				AudioManager mgr = (AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE );
				float streamVolumeCurrent = mgr.getStreamVolume( AudioManager.STREAM_MUSIC );
				float streamVolumeMax = mgr.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
				float volume = streamVolumeCurrent / streamVolumeMax;// 得到音量的大小
				mp.setVolume( volume , volume );
				int RingerMode = mgr.getRingerMode();
				if( RingerMode == AudioManager.RINGER_MODE_NORMAL )
				{
					mp.start();
				}
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onShake()
	{
		if( iLoongLauncher.getInstance().themeChanging )
			return;
		playSound();
		// TODO Auto-generated method stub
		int newIndex = getRandomWallpaperIndex();
		if( DefaultLayout.enable_scene_wallpaper && DefaultLayout.wallpapers_from_apk_packagename.equals( "" ) )
		{
			Intent it = new Intent( WallpaperChangedReceiver.SCENE_WALLPAPER_CHANGE );
			it.putExtra( "wallpaper" , mWallPapers.get( newIndex ).fileName );
			mContext.sendBroadcast( it );
		}
		else
			selectWallpaper( newIndex );
		// 甩动触发事件
		// Toast.makeText(mContext, "检测到晃动:" + mCurWallpaperIndex,
		// Toast.LENGTH_SHORT).show();
	}
	
	class WallPaperFile
	{
		
		public WallPaperFile(
				String fileName ,
				WallPaperFileFromEnum from )
		{
			this.fileName = fileName;
			this.fileFrom = from;
		}
		
		public WallPaperFile(
				int drawable )
		{
			this.fileDrawable = drawable;
			this.fileFrom = WallPaperFileFromEnum.otherapk;
		}
		
		String fileName;
		WallPaperFileFromEnum fileFrom;
		int fileDrawable;
	}
	
	enum WallPaperFileFromEnum
	{
		assets , custom , otherapk
	}
}
