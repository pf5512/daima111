package com.iLoong.launcher.Desktop3D;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;


public class WidgetView extends View3D implements IconBase3D
{
	
	public static final int MSG_WIDGETVIEW_LONGCLICK = 0;
	private TextureRegion title;
	ItemInfo info;
	
	//	public int width;
	//	public int height;
	public WidgetView(
			String name ,
			Texture texture )
	{
		super( name , texture );
		// TODO Auto-generated constructor stub
		this.width = texture.getWidth();
		this.height = texture.getHeight();
	}
	
	public WidgetView(
			String name ,
			Texture texture ,
			String title )
	{
		super( name , texture );
		// TODO Auto-generated constructor stub
		//		this.width = texture.getWidth();
		//		this.height = texture.getHeight();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "test" , "widgetView onClick" );
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		if( WidgetDownload.checkToDownload( info , true ) )
			return true;
		else
			return super.onClick( x , y );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "test" , "widgetView onLongClick" );
		if( !this.isDragging )
		{
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			return viewParent.onCtrlEvent( this , MSG_WIDGETVIEW_LONGCLICK );
		}
		return super.onLongClick( x , y );
	}
	
	public void setTitle(
			TextureRegion t )
	{
		title = t;
	}
	
	public void setItemInfo(
			ItemInfo info )
	{
		this.info = info;
	}
	
	public ItemInfo getItemInfo()
	{
		return this.info;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		super.draw( batch , parentAlpha );
		if( title != null )
		{
			batch.draw( title , x , y , title.getRegionWidth() , title.getRegionHeight() );
		}
	}
}
