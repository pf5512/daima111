package com.iLoong.Music.View;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class AlbumFrontHighlibhgView extends PluginViewObject3D
{
	
	public AlbumFrontHighlibhgView(
			String name ,
			MainAppContext appContext ,
			TextureRegion region )
	{
		super( appContext , name , region , "album_front_highlight.obj" , "cover.png" );
		//		this.setSize(WidgetMusic.MODEL_WIDTH, WidgetMusic.MODEL_HEIGHT);
		//		this.setMoveOffset(WidgetMusic.MODEL_WIDTH / 2,
		//				WidgetMusic.MODEL_HEIGHT / 2, 0);
		setBlendSrcAlpha( 1 );
		super.build();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return super.onClick( x , y );
	}
	
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		super.dispose();
	}
	
	public void changeSkin(
			final String subTheme )
	{
		appContext.mGdxApplication.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Texture texture = ThemeHelper.getThemeSubTexture( appContext , subTheme , "cover.png" );
				if( texture != null )
				{
					texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					TextureRegion oldRegion = AlbumFrontHighlibhgView.this.region;
					AlbumFrontHighlibhgView.this.region = new TextureRegion( texture );
					if( oldRegion != null && oldRegion.getTexture() != null )
					{
						oldRegion.getTexture().dispose();
					}
				}
			}
		} );
	}
	
	public void pageScroll(
			float degree ,
			int index ,
			int count )
	{
		Log.e( "WidgetMusic" , "pageScroll degree:" + degree + " index:" + index + " count:" + count );
		if( degree <= 0 )
		{
			if( degree >= -1 )
			{
				int alpha = (int)( 770 * ( 1 + degree ) - degree );
				setBlendSrcAlpha( alpha );
			}
		}
		else
		{
			if( degree <= 1 )
			{
				int alpha = (int)( 770 * ( 1 - degree ) + degree );
				setBlendSrcAlpha( alpha );
			}
		}
	}
}
