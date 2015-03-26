package com.iLoong.launcher.Desktop3D;


import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;

import java.io.IOException;


public class ScreenUtils3D
{
	
	public TextureRegion wallpaperTextureRegion = new TextureRegion();
	private Texture screenTexture;
	private SpriteBatch batch;
	private float parentAlpha;
	private FrameBuffer fb = null;
	private boolean useWallpaper;
	private float wpOffsetX;
	private boolean ready = false;
	public boolean isLiveWp = false;
	
	public ScreenUtils3D()
	{
		this.batch = new SpriteBatch();
		fb = new FrameBuffer( Format.RGBA8888 , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() , true );
	}
	
	public Texture getCurrScreenTexture()
	{
		return screenTexture;
	}
	
	public void setWallpagerBitmap()
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			public void run()
			{
				setWallpaperBitmapDrawable();
			}
		} );
	}
	
	public void setDefaultWp()
	{
		WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
		WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		if( wallpaperInfo != null )
		{
			try
			{
				Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/translucent-black.png" ) );
				if( bmp.getConfig() != Config.ARGB_8888 )
				{
					bmp = bmp.copy( Config.ARGB_8888 , false );
				}
				Texture t = new BitmapTexture( bmp );
				wallpaperTextureRegion.setTexture( t );
				bmp.recycle();
				isLiveWp = true;
				Log.v( "Root3D" , "USE DEFAULT BG" );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setWallpaperBitmapDrawable()
	{
		WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
		WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		if( wallpaperInfo != null )
		{
			try
			{
				Bitmap bmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/translucent-black.png" ) );
				if( bmp.getConfig() != Config.ARGB_8888 )
				{
					bmp = bmp.copy( Config.ARGB_8888 , false );
				}
				Texture t = new BitmapTexture( bmp );
				wallpaperTextureRegion.setTexture( t );
				bmp.recycle();
				isLiveWp = true;
				Log.v( "Root3D" , "USE DEFAULT BG" );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			isLiveWp = false;
			Drawable drawable = wallpaperManager.getDrawable();
			Bitmap wallpaperBitmap = ( (BitmapDrawable)drawable ).getBitmap();
			BitmapTexture texture = new BitmapTexture( wallpaperBitmap );
			wallpaperTextureRegion.setRegion( texture );
			Log.v( "Root3D" , "TEXTURE SIZE: " + wallpaperTextureRegion.getRegionWidth() + " height: " + wallpaperTextureRegion.getRegionHeight() );
		}
		ready = true;
	}
	
	public void setCurrScreenWPOffset()
	{
		if( wallpaperTextureRegion != null )
		{
			int wpWidth = wallpaperTextureRegion.getTexture().getWidth();
			int screenWidth = Utils3D.getScreenWidth();
			if( wpWidth > screenWidth )
			{
				int curScreen = iLoongLauncher.getInstance().getCurrentScreen();
				int screenNum = iLoongLauncher.getInstance().getScreenCount();
				int gapWidth = wpWidth - screenWidth;
				wpOffsetX = -(int)( (float)gapWidth * curScreen / ( screenNum - 1 ) );
			}
			else
			{
				wpOffsetX = 0;
			}
		}
	}
	
	public void setCurrScreenWPTexture()
	{
		setCurrScreenWPOffset();
		fb.begin();
		if( wallpaperTextureRegion != null && !isLiveWp )
		{
			batch.begin();
			Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
			Gdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
			int wpWidth = wallpaperTextureRegion.getTexture().getWidth();
			int wpHeight = wallpaperTextureRegion.getTexture().getHeight();
			batch.draw( wallpaperTextureRegion , wpOffsetX , 0 , wpWidth , wpHeight );
			batch.end();
			screenTexture = fb.getColorBufferTexture();
			Log.v( "Root3D" , "SIZE " + wallpaperTextureRegion.getRegionWidth() + " SIZE1:" + wallpaperTextureRegion.getRegionHeight() );
		}
		fb.end();
	}
	
	public TextureRegion getSreenRetion()
	{
		return wallpaperTextureRegion;
	}
	
	public boolean getReady()
	{
		return ready;
	}
	
	public boolean isLiveWP()
	{
		return isLiveWp;
	}
}
