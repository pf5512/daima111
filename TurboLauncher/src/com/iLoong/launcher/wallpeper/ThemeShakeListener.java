package com.iLoong.launcher.wallpeper;


import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.theme.ThemeDescription;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.wallpeper.ShakeListener.OnShakeListener;


public class ThemeShakeListener implements OnShakeListener
{
	
	private static final String TAG = "ThemeShakeListener";
	private Context mContext;
	
	public ThemeShakeListener(
			Context context )
	{
		this.mContext = context;
		//findWallpapers();
	}
	
	private int getRandomIndex(
			int max ,
			int cur )
	{
		Random rand = new Random();
		if( max == 0 )
		{
			return max;
		}
		int newIndex = rand.nextInt( max );
		int count = 0;
		while( newIndex == cur )
		{
			newIndex = rand.nextInt( max );
			count++;
			if( count > 100 )
			{
				count = 0;
				break;
			}
		}
		return newIndex;
	}
	
	private void applyNewTheme()
	{
		ThemeManager mThemeManager = ThemeManager.getInstance();
		Vector<ThemeDescription> vector = mThemeManager.getThemeDescriptions();
		if( vector.size() <= 1 )
		{
			return;
		}
		ThemeDescription current = mThemeManager.getCurrentThemeDescription();
		int currentIndex = vector.indexOf( current );
		int random = getRandomIndex( vector.size() , currentIndex );
		mThemeManager.ApplyTheme( vector.elementAt( random ) );
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
				float volume = streamVolumeCurrent / streamVolumeMax;
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
		applyNewTheme();
	}
	//	class WallPaperFile {
	//		public WallPaperFile(String fileName, WallPaperFileFromEnum from) {
	//			this.fileName = fileName;
	//			this.fileFrom = from;
	//		}
	//
	//		String fileName;
	//		WallPaperFileFromEnum fileFrom;
	//	}
	//
	//	enum WallPaperFileFromEnum {
	//		assets, custom
	//	}
}
