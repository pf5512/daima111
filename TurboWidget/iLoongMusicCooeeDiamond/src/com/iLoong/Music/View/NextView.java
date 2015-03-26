package com.iLoong.Music.View;


import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class NextView extends PluginViewObject3D implements MusicListener
{
	
	public MusicController musicController = null;
	private TextureRegion nextRegion = null;
	private TextureRegion nextPressRegion = null;
	private Timer mTimer = new Timer();
	private TimerTask mTimerTask = null;
	
	public MusicController getMusicController()
	{
		return musicController;
	}
	
	public void setMusicController(
			MusicController musicController )
	{
		this.musicController = musicController;
		musicController.addMusicListener( this );
	}
	
	public NextView(
			String name ,
			MainAppContext appContext ,
			TextureRegion region )
	{
		super( appContext , name , region , "next.obj" , null );
		this.nextRegion = new TextureRegion( WidgetThemeManager.getInstance().getThemeTexture( "next.png" ) );
		this.nextPressRegion = new TextureRegion( WidgetThemeManager.getInstance().getThemeTexture( "next_press.png" ) );
		this.region = nextRegion;
		float next_width = WidgetThemeManager.getInstance().getFloat( "next_width" );
		float next_height = WidgetThemeManager.getInstance().getFloat( "next_height" );
		this.x = WidgetThemeManager.getInstance().getFloat( "next_x" );
		this.y = WidgetThemeManager.getInstance().getFloat( "next_y" );
		this.setSize( next_width , next_height );
		super.build();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( musicController != null )
		{
			musicController.nextMusic();
		}
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		this.region = nextRegion;
		return super.onLongClick( x , y );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		this.region = nextPressRegion;
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
		this.region = nextRegion;
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public void onNext(
			MusicData music )
	{
		// TODO Auto-generated method stub
		this.region = nextPressRegion;
		releaseNextPressed();
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
	}
	
	@Override
	public void onPlay(
			MusicData music )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onMetaChanged(
			MusicData music )
	{
		// TODO Auto-generated method stub
		this.region = nextRegion;
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
				NextView.this.region = nextRegion;
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
				Texture nextTexture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "next.png" );
				if( nextTexture != null )
				{
					nextTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					if( NextView.this.region.equals( nextRegion ) )
					{
						TextureRegion oldRegion = NextView.this.region;
						NextView.this.region = nextRegion = new TextureRegion( nextTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
					else
					{
						TextureRegion oldRegion = nextRegion;
						nextRegion = new TextureRegion( nextTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
				}
				Texture nextPressTexture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "next_press.png" );
				if( nextPressTexture != null )
				{
					nextPressTexture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					if( NextView.this.region.equals( nextPressRegion ) )
					{
						TextureRegion oldRegion = NextView.this.region;
						NextView.this.region = nextPressRegion = new TextureRegion( nextPressTexture );
						if( oldRegion != null && oldRegion.getTexture() != null )
						{
							oldRegion.getTexture().dispose();
						}
					}
					else
					{
						TextureRegion oldRegion = nextPressRegion;
						nextPressRegion = new TextureRegion( nextPressTexture );
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
		if( nextRegion != null )
		{
			if( nextRegion.getTexture() != null )
			{
				nextRegion.getTexture().dispose();
			}
		}
		if( nextPressRegion != null )
		{
			if( nextPressRegion.getTexture() != null )
			{
				nextPressRegion.getTexture().dispose();
			}
		}
		if( mTimerTask != null )
		{
			mTimerTask.cancel();
			mTimerTask = null;
		}
		if( mTimer != null )
		{
			mTimer.cancel();
			mTimer = null;
		}
	}
}
