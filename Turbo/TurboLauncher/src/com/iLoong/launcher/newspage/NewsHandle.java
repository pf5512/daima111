package com.iLoong.launcher.newspage;


import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;


public class NewsHandle extends View3D implements DragSource3D
{
	
	public TextureRegion mLeftHandle;
	public TextureRegion mRightHandle;
	private Context mContext;
	public static final float mWidth = 80 * Utils3D.getScreenWidth() / 720f;
	public static final float mHeight = 100 * UtilsBase.getScreenWidth() / 720f;
	
	public NewsHandle(
			String name ,
			Context context )
	{
		super( name );
		mContext = context;
		mLeftHandle = new TextureRegion( getRegion( "news_handle_left.png" ) );
		mRightHandle = new TextureRegion( getRegion( "news_handle_right.png" ) );
		region = mLeftHandle;
		setSize( mWidth , mHeight );
		setPosition( 0 , ( UtilsBase.getScreenHeight() - mHeight ) / 2 );
		this.color.a = 0.0f;
	}
	
	public NewsHandle(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( this.x == 0 )
		{
			Messenger.sendMsg( Messenger.MSG_SHOW_NEWS_AUTO , 0 );
		}
		else
		{
			Messenger.sendMsg( Messenger.MSG_SHOW_NEWS_AUTO , 1 );
		}
		hide();
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( !this.isDragging )
		{
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
			this.color.a = 1.0f;
			this.isDragging = true;
			return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
		}
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		requestFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		releaseFocus();
		return super.onTouchUp( x , y , pointer );
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( "theme/pack_source/" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
		Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
		return;
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		ArrayList<View3D> l = new ArrayList<View3D>();
		l.add( this );
		return l;
	}
}
