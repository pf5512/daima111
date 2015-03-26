package com.iLoong.MusicPlay.view;


import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.MusicPlay.common.Parameter;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class PauseView extends PluginViewObject3D implements MusicListener
{
	
	private final static String PLAY_OBJ = "pause.obj";
	private final static String PLAY_TEXTURE = "play.png";
	private final static String PLAY_PRESS_TEXTURE = "play_press.png";
	private final static String PAUSE_TEXTURE = "pause.png";
	private final static String PAUSE_PRESS_TEXTURE = "pause_press.png";
	private MusicController musicController = null;
	private TextureRegion pausePressedRegion;
	private TextureRegion pauseRegion = null;
	private TextureRegion playRegion = null;
	private TextureRegion playPressedRegion = null;
	public int status = 0;// 0 暂停状态（暂停状态显示播放按钮） 1：播放状态（播放状态显示暂停按钮）
	private TimerTask mTimerTask;
	private Timer mTimer;
	private MainAppContext mAppContext = null;
	
	public PauseView(
			String name ,
			MainAppContext appContext )
	{
		super( appContext , name , PLAY_TEXTURE , PLAY_OBJ );
		mAppContext = appContext;
		this.setDepthMode( true );
		initTextureRegion( mAppContext );
		this.region = playRegion;
		super.build();
		move( 100 / 2f * WidgetMusicPlayGroup.scale , 45 / 2 * WidgetMusicPlayGroup.scale , Parameter.PAUSE_TOZ * WidgetMusicPlayGroup.scale );
		mTimer = new Timer();
		status = 0;
	}
	
	private void initTextureRegion(
			MainAppContext appContext )
	{
		AndroidFiles gdxFile = new AndroidFiles( appContext.mWidgetContext.getAssets() );
		String texturePath = getThemeTexturePath( PLAY_TEXTURE );
		FileHandle fileHandle = gdxFile.internal( texturePath );
		Bitmap bm = BitmapFactory.decodeStream( fileHandle.read() );
		BitmapTexture bt = new BitmapTexture( bm );
		bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		playRegion = new TextureRegion( bt );
		bm.recycle();
		bm = null;
		texturePath = getThemeTexturePath( PLAY_PRESS_TEXTURE );
		fileHandle = gdxFile.internal( texturePath );
		Bitmap bm1 = BitmapFactory.decodeStream( fileHandle.read() );
		BitmapTexture bt1 = new BitmapTexture( bm1 );
		bt1.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		playPressedRegion = new TextureRegion( bt1 );
		bm1.recycle();
		bm1 = null;
		texturePath = getThemeTexturePath( PAUSE_PRESS_TEXTURE );
		fileHandle = gdxFile.internal( texturePath );
		Bitmap bm2 = BitmapFactory.decodeStream( fileHandle.read() );
		BitmapTexture bt2 = new BitmapTexture( bm2 );
		bt2.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		pausePressedRegion = new TextureRegion( bt2 );
		bm2.recycle();
		bm2 = null;
		texturePath = getThemeTexturePath( PAUSE_TEXTURE );
		fileHandle = gdxFile.internal( texturePath );
		Bitmap bm3 = BitmapFactory.decodeStream( fileHandle.read() );
		BitmapTexture bt3 = new BitmapTexture( bm3 );
		bt3.setFilter( TextureFilter.Linear , TextureFilter.Linear );
		pauseRegion = new TextureRegion( bt3 );
		bm3.recycle();
		bm3 = null;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( musicController != null )
		{
			musicController.togglePlay();
		}
		return true;
	}
	
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( status == 0 )
		{
			this.region = playRegion;
		}
		else if( status == 1 )
		{
			this.region = pauseRegion;
		}
		return super.onLongClick( x , y );
	}
	
	public void setMusicController(
			MusicController musicController )
	{
		// TODO Auto-generated method stub
		this.musicController = musicController;
		musicController.addMusicListener( this );
	}
	
	@Override
	public void onNext(
			MusicData music )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onPrevious(
			MusicData music )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onPause(
			MusicData music )
	{
		// TODO Auto-generated method stub
		if( music.playing == true )
		{
			status = 1;
			this.region = pauseRegion;
		}
		else
		{
			status = 0;
			this.region = playRegion;
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( status == 0 )
		{
			this.region = playPressedRegion;
		}
		else if( status == 1 )
		{
			this.region = pausePressedRegion;
		}
		releaseNextPressed();
		return super.onTouchDown( x , y , pointer );
	}

	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( status == 0 )
		{
			this.region = playRegion;
		}
		else if( status == 1 )
		{
			this.region = pauseRegion;
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public void onPlay(
			MusicData music )
	{
		// TODO Auto-generated method stub
		if( music.playing == true )
		{
			status = 1;
			this.region = pauseRegion;
		}
		else
		{
			status = 0;
			this.region = playRegion;
		}
	}
	
	@Override
	public void onMetaChanged(
			MusicData music )
	{
		// TODO Auto-generated method stub
		if( music.playing == true )
		{
			status = 1;
			this.region = pauseRegion;
		}
		else
		{
			status = 0;
			this.region = playRegion;
		}
	}
	
	private void releaseNextPressed()
	{
		if( mTimerTask != null )
		{
			mTimerTask.cancel();
		}
		mTimerTask = new TimerTask() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if( status == 0 )
				{
					PauseView.this.region = playRegion;
				}
				else if( status == 1 )
				{
					PauseView.this.region = pauseRegion;
				}
			}
		};
		mTimer.schedule( mTimerTask , 50 );
	}
	
	public void changeSkin(
			final String subTheme )
	{
		appContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Texture playTexture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "play.png" );
				if( playTexture != null )
				{
					playTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					if( PauseView.this.region.equals( playRegion ) )
					{
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = playRegion = new TextureRegion( playTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
					else
					{
						TextureRegion oldRegion = playRegion;
						playRegion = new TextureRegion( playTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
				}
				Texture playPressTexture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "play_press.png" );
				if( playPressTexture != null )
				{
					playPressTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					if( PauseView.this.region.equals( playPressedRegion ) )
					{
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = playPressedRegion = new TextureRegion( playPressTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
					else
					{
						TextureRegion oldRegion = playPressedRegion;
						playPressedRegion = new TextureRegion( playPressTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
				}
				Texture pauseTexture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "pause.png" );
				if( pauseTexture != null )
				{
					pauseTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					if( PauseView.this.region.equals( pauseRegion ) )
					{
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = pauseRegion = new TextureRegion( pauseTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
					else
					{
						TextureRegion oldRegion = pauseRegion;
						pauseRegion = new TextureRegion( pauseTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
				}
				Texture pausePressTexture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "pause_press.png" );
				if( pausePressTexture != null )
				{
					pausePressTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					if( PauseView.this.region.equals( pausePressedRegion ) )
					{
						TextureRegion oldRegion = PauseView.this.region;
						PauseView.this.region = pausePressedRegion = new TextureRegion( pausePressTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
					else
					{
						TextureRegion oldRegion = pausePressedRegion;
						pausePressedRegion = new TextureRegion( pausePressTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
				}
			}
		} );
	}
	
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		super.dispose();
		if( pausePressedRegion != null )
		{
			if( pausePressedRegion.getTexture() != null )
			{
				pausePressedRegion.getTexture().dispose();
			}
		}
		if( pauseRegion != null )
		{
			if( pauseRegion.getTexture() != null )
			{
				pauseRegion.getTexture().dispose();
			}
		}
		if( playRegion != null )
		{
			if( playRegion.getTexture() != null )
			{
				playRegion.getTexture().dispose();
			}
		}
		if( playPressedRegion != null )
		{
			if( playPressedRegion.getTexture() != null )
			{
				playPressedRegion.getTexture().dispose();
			}
		}
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		boolean ret = view3dTouchCheck.isPointIn( this , getParent().getX() , getParent().getY(), 100 * WidgetMusicPlayGroup.scale , 45 * WidgetMusicPlayGroup.scale , x , y );
		return ret;
	}
}
