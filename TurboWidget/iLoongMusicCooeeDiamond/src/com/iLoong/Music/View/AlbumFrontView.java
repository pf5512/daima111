package com.iLoong.Music.View;


import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class AlbumFrontView extends PluginViewObject3D implements MusicListener , LoadBitmapCallback
{
	
	private MusicController musicController;
	private TextureRegion defaultAlbumRegion = null;
	private MusicData currentMusic;
	private String[] activitys = new String[3];
	public static String defaultPkgName;
	
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
	
	public AlbumFrontView(
			String name ,
			MainAppContext appContext ,
			TextureRegion region )
	{
		super( appContext , name , region , "album_front.obj" , null );
		this.region = defaultAlbumRegion = new TextureRegion( WidgetThemeManager.getInstance().getThemeTexture( "default_album.png" ) );
		float album_width = WidgetThemeManager.getInstance().getFloat( "album_front_width" );
		float album_height = WidgetThemeManager.getInstance().getFloat( "album_front_height" );
		this.x = WidgetThemeManager.getInstance().getFloat( "album_front_x" );
		this.y = WidgetThemeManager.getInstance().getFloat( "album_front_y" );
		this.setSize( album_width , album_height );
		// this.setMoveOffset(WidgetMusic.MODEL_WIDTH / 2,
		// WidgetMusic.MODEL_HEIGHT / 2, 0);
		super.build();
		getDefaultPkgName();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		PackageManager pm = appContext.mContainerContext.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage( defaultPkgName );
		appContext.mContainerContext.startActivity( intent );
		//		if (currentMusic != null) {
		//		try
		//		{
		//			Intent intent = null;
		//			PackageManager packageManager = appContext.mContainerContext.getPackageManager();
		//			if( currentMusic != null )
		//			{
		//				activitys = appContext.mWidgetContext.getResources().getStringArray( R.array.music_back_activity );
		//			}
		//			else
		//			{
		//				activitys = appContext.mWidgetContext.getResources().getStringArray( R.array.music_activity );
		//			}
		//			for( int i = 0 ; i < activitys.length ; i++ )
		//			{
		//				String serviceStr = activitys[i];
		//				String[] serviceComponentarray = serviceStr.split( ";" );
		//				if( serviceComponentarray.length >= 2 )
		//				{
		//					intent = new Intent();
		//					intent.setClassName( serviceComponentarray[0] , serviceComponentarray[1] );
		//					List<ResolveInfo> resoveInfos = packageManager.queryIntentServices( intent , 0 );
		//					if( resoveInfos.size() > 0 )
		//					{
		//						break;
		//					}
		//				}
		//			}
		//			if( intent != null && currentMusic != null )
		//			{
		//				Uri personUri = ContentUris.withAppendedId( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , currentMusic.songId );
		//				intent.setDataAndType( personUri , "audio/*" );
		//				intent.setData( null );
		//				appContext.mContainerContext.startActivity( intent );
		//			}
		//			else
		//			{
		//				intent.setAction( android.content.Intent.ACTION_VIEW );
		//				appContext.mContainerContext.startActivity( intent );
		//			}
		//		}
		//		catch( Exception e )
		//		{
		//			e.printStackTrace();
		//		}
		//		}
		return true;
	}
	
	@Override
	public void onNext(
			MusicData music )
	{
		// TODO Auto-generated method stub
		updateAlbumRegion( music );
	}
	
	@Override
	public void onPrevious(
			MusicData music )
	{
		// TODO Auto-generated method stub
		updateAlbumRegion( music );
	}
	
	@Override
	public void onPause(
			MusicData music )
	{
		// TODO Auto-generated method stub
		updateAlbumRegion( music );
	}
	
	@Override
	public void onPlay(
			MusicData music )
	{
		// TODO Auto-generated method stub
		updateAlbumRegion( music );
	}
	
	private void updateAlbumRegion(
			MusicData music )
	{
		Log.e( "AlbumFrontView" , "updateAlbumRegion 1: data:" + music );
		if( music != null && music.id != -1 )
		{
			currentMusic = music;
			musicController.loadArtBitmap( music , this );
		}
		else
		{
			currentMusic = null;
			musicController.loadArtBitmap( music , this );
		}
	}
	
	@Override
	public void loadCompleted(
			final Bitmap bitmap )
	{
		// TODO Auto-generated method stub
		Log.e( "AlbumFrontView" , "loadCompleted 1: bitmap:" + bitmap );
		appContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				if( bitmap != null )
				{
					// TODO Auto-generated method stub
					TextureRegion newRegion = new TextureRegion( new BitmapTexture( bitmap ) );
					Log.e( "AlbumFrontView" , "loadCompleted 2: bitmap:" + bitmap );
					if( !AlbumFrontView.this.region.equals( defaultAlbumRegion ) )
					{
						Texture oldTexture = AlbumFrontView.this.region.getTexture();
						AlbumFrontView.this.region = newRegion;
						if( oldTexture != null )
						{
							oldTexture.dispose();
						}
					}
					else
					{
						AlbumFrontView.this.region = newRegion;
					}
				}
				else
				{
					Log.e( "AlbumFrontView" , "loadCompleted 3: bitmap:" + bitmap );
					if( !AlbumFrontView.this.region.equals( defaultAlbumRegion ) )
					{
						Log.e( "AlbumFrontView" , "loadCompleted 4: bitmap:" + bitmap );
						Texture oldTexture = AlbumFrontView.this.region.getTexture();
						AlbumFrontView.this.region = defaultAlbumRegion;
						if( oldTexture != null )
						{
							oldTexture.dispose();
						}
					}
					else
					{
						Log.e( "AlbumFrontView" , "loadCompleted 5: bitmap:" + bitmap );
						AlbumFrontView.this.region = defaultAlbumRegion;
					}
				}
			}
		} );
	}
	
	@Override
	public void onMetaChanged(
			MusicData music )
	{
		// TODO Auto-generated method stub.
		Log.e( "AlbumFrontView" , "onMetaChanged 1: data:" + music );
		updateAlbumRegion( music );
	}
	
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		super.dispose();
		if( defaultAlbumRegion != null )
		{
			if( defaultAlbumRegion.getTexture() != null )
			{
				defaultAlbumRegion.getTexture().dispose();
			}
		}
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Group.toChildCoordinates( this , x , y , point );
		return( ( point.x >= 0 && point.x < width ) && ( point.y >= 0 && point.y < height ) );
	}
	
	private void getDefaultPkgName()
	{
		PackageManager pm = appContext.mContainerContext.getPackageManager();
		Intent intent = new Intent( MediaStore.INTENT_ACTION_MUSIC_PLAYER );
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		if( resolveInfos != null )
		{
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					defaultPkgName = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
		else
		{
			intent = new Intent( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_APP_MUSIC );
			resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					defaultPkgName = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
	}
}
