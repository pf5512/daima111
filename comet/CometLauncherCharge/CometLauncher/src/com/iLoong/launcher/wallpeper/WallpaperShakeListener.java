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
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import com.iLoong.launcher.Desktop3D.Log;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.wallpeper.ShakeListener.OnShakeListener;


public class WallpaperShakeListener implements OnShakeListener
{
	
	private static final String TAG = "WallpaperShakeListener";
	private Context mContext;
	private static final String wallpaperPath = "launcher/wallpapers";
	private ArrayList<WallPaperFile> mWallPapers = null;
	private int mCurWallpaperIndex = 0;
	
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
		File dir = new File( DefaultLayout.custom_wallpapers_path );
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
			if( dir.exists() && dir.isDirectory() )
			{
				wallpapers = dir.list();
				for( String name : wallpapers )
				{
					if( !name.contains( "_small" ) )
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
					if( !name.contains( "_small" ) )
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
			}
			else if( file.fileFrom == WallPaperFileFromEnum.custom )
			{
				try
				{
					stream = new FileInputStream( DefaultLayout.custom_wallpapers_path + "/" + file.fileName );
				}
				catch( FileNotFoundException e )
				{
					e.printStackTrace();
				}
			}
			wpm.setStream( stream );
			Utils3D.showPidMemoryInfo( mContext , TAG );
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
	}
	
	MediaPlayer mp = null;
	
	private void playSound()
	{
		if( mp == null )
		{
			mp = MediaPlayer.create( mContext , com.iLoong.RR.raw.shakenotify );
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
		playSound();
		// TODO Auto-generated method stub
		int newIndex = getRandomWallpaperIndex();
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
		
		String fileName;
		WallPaperFileFromEnum fileFrom;
	}
	
	enum WallPaperFileFromEnum
	{
		assets , custom
	}
}
