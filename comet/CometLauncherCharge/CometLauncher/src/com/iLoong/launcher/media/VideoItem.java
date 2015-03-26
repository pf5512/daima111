package com.iLoong.launcher.media;


import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.PhotoItem.PhotoView;
import com.iLoong.launcher.media.ThumbnailThread.ThumbnailClient;
import com.iLoong.launcher.theme.ThemeManager;


public class VideoItem extends ReusableTextureHolder implements ThumbnailClient
{
	
	public static final int EVENT_VIDEO_LONGCLICK = 0;
	public String data;
	public String title;
	public String bucketDisplayName;
	public long id;
	public MediaCache mediaCache;
	public ReusableTexture texture;
	public Bitmap thumbnailBmp;
	private static TextureRegion bgRegion;
	private VideoView view;
	private float titleHeight;
	private float bgWidth;
	private float bgHeight;
	
	//gets default texture for display
	public VideoItem()
	{
		if( bgRegion == null )
			bgRegion = R3D.findRegion( "default-video" );
		titleHeight = Utils3D.getTitleHeight( R3D.photo_title_size , R3D.photo_title_line );
		bgWidth = R3D.video_width + 2 * R3D.video_padding;
		bgHeight = R3D.video_height + 2 * R3D.video_padding;
	}
	
	public View3D obtainView()
	{
		if( view == null )
		{
			view = new VideoView( title );
			view.video = this;
		}
		return view;
	}
	
	//删除图片
	public void onDelete()
	{
		if( mediaCache.deleteVideo( this ) )
		{
			free();
		}
	}
	
	public void setThumbnailBmp(
			Bitmap bmp )
	{
		if( free || texture != null )
		{
			bmp.recycle();
			return;
		}
		thumbnailBmp = Utils3D.resizeBmp( bmp , view.width , view.height );
		pixmap = Utils3D.bmp2Pixmap( thumbnailBmp );
		disposed = false;
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				if( free )
				{
					return;
				}
				if( texture != null )
					return;
				texture = ReusableTexturePool.getInstance().get( VideoItem.this );
				pixmap.dispose();
				disposed = true;
				Gdx.graphics.requestRendering();
			}
		} );
	}
	
	public long getThumbnailId()
	{
		return id;
	}
	
	@Override
	public String getThumbnailPath()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getResType()
	{
		return AppHost3D.CONTENT_TYPE_VIDEO;
	}
	
	public class VideoView extends View3D implements MediaView
	{
		
		public VideoItem video;
		public boolean selected = false;
		
		public VideoView(
				String name )
		{
			super( name );
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			int x = Math.round( this.x );
			int y = Math.round( this.y );
			int _x = 0 , _y = 0;
			if( video.texture != null )
			{
				_x = Math.round( ( width - video.texture.getWidth() ) / 2 );
				_y = Math.round( ( height - titleHeight - video.texture.getHeight() ) / 2 + titleHeight );
			}
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			int _xBg = Math.round( ( width - bgWidth ) / 2 );
			int _yBg = Math.round( ( height - titleHeight - bgHeight ) / 2 + titleHeight );
			if( is3dRotation() )
			{
				if( video.texture != null )
					batch.draw( video.texture , x + _x , y + _y , video.texture.getWidth() , video.texture.getHeight() );
				batch.draw( bgRegion , x + _xBg , y + _yBg , bgWidth , bgHeight );
				if( AppHost3D.selectState && AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_VIDEO )
				{
					if( selected )
						batch.draw(
								R3D.selectedRegion ,
								x + _xBg + bgWidth - R3D.selectedRegion.getRegionWidth() ,
								y + _yBg + 6 ,
								R3D.selectedRegion.getRegionWidth() ,
								R3D.selectedRegion.getRegionHeight() );
					else
						batch.draw(
								R3D.unselectRegion ,
								x + _xBg + bgWidth - R3D.unselectRegion.getRegionWidth() ,
								y + _yBg + 6 ,
								R3D.unselectRegion.getRegionWidth() ,
								R3D.unselectRegion.getRegionHeight() );
				}
			}
			else
			{
				if( video.texture != null )
					batch.draw( video.texture , x + _x , y + _y , video.texture.getWidth() , video.texture.getHeight() );
				batch.draw( bgRegion , x + _xBg , y + _yBg , originX - _xBg , originY - _yBg , bgWidth , bgHeight , scaleX , scaleY , rotation );
				if( AppHost3D.selectState && AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_VIDEO )
				{
					if( selected )
						batch.draw(
								R3D.selectedRegion ,
								x + _xBg + bgWidth - R3D.selectedRegion.getRegionWidth() ,
								y + _yBg + 6 ,
								originX - _x ,
								originY - _y ,
								R3D.selectedRegion.getRegionWidth() ,
								R3D.selectedRegion.getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
					else
						batch.draw(
								R3D.unselectRegion ,
								x + _xBg + bgWidth - R3D.unselectRegion.getRegionWidth() ,
								y + _yBg + 6 ,
								originX - _x ,
								originY - _y ,
								R3D.unselectRegion.getRegionWidth() ,
								R3D.unselectRegion.getRegionHeight() ,
								scaleX ,
								scaleY ,
								rotation );
				}
			}
			if( region.getTexture() != null )
			{
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				batch.draw( region , x + ( width - region.getRegionWidth() ) / 2 , y );
			}
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			if( AppHost3D.selectState )
			{
				selected = !selected;
			}
			else
			{
				if( !Utils3D.ExistSDCard() )
				{
					SendMsgToAndroid.sendCircleToastMsg( "SD卡已移除，无法打开文件" );
					return true;
				}
				Intent intent = new Intent();
				intent.setAction( android.content.Intent.ACTION_VIEW );
				intent.setDataAndType( Uri.fromFile( new File( video.data ) ) , "video/*" );
				iLoongLauncher.getInstance().startActivity( intent );
			}
			return true;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			if( AppHost3D.selectState )
				return true;
			else
			{
				viewParent.onCtrlEvent( this , EVENT_VIDEO_LONGCLICK );
				selected = true;
			}
			return true;
		}
		
		@Override
		public void setSize(
				float w ,
				float h )
		{
			super.setSize( w , h );
			this.setOrigin( w / 2 , h / 2 );
			if( region.getTexture() == null )
			{
				region.setRegion( new BitmapTexture( ( Utils3D.titleToPixmapWidthLimit( title , (int)w - 4 , R3D.photo_title_size , Color.WHITE , 2 ) ) ) );
			}
		}
		
		@Override
		public void prepare(
				int priority )
		{
			video.prepare( priority );
		}
		
		@Override
		public void free()
		{
			video.free();
		}
		
		@Override
		public void refresh()
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void select()
		{
			selected = true;
		}
		
		@Override
		public void share(
				ArrayList<Uri> list )
		{
			if( !selected )
				return;
			if( video.data != null )
			{
				list.add( Uri.parse( "file://" + video.data ) );
			}
		}
		
		@Override
		public void onDelete()
		{
			if( !selected )
				return;
			video.onDelete();
		}
		
		@Override
		public void clearSelect()
		{
			selected = false;
		}
	}
}
