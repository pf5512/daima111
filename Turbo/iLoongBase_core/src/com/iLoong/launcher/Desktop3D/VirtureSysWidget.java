package com.iLoong.launcher.Desktop3D;


import android.graphics.Rect;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ItemInfo;


public class VirtureSysWidget extends View3D implements IconBase3D
{
	
	public static final int MSG_LONGCLICK = 0;
	private ItemInfo info;
	
	public VirtureSysWidget(
			String name ,
			Texture texture )
	{
		super( name , texture );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setItemInfo(
			ItemInfo info )
	{
		this.info = info;
	}
	
	@Override
	public ItemInfo getItemInfo()
	{
		return info;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		if( is3dRotation() )
			batch.draw( region , (int)x , (int)y , width , height );
		else
			batch.draw( region , (int)x , (int)y , originX , originY , width , height , scaleX , scaleY , rotation );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		//Log.v("test123", " Foldericon3d onLongClick:" + name + " x:" + x + " y:" + y);
		if( !this.isDragging )
		{
			this.toAbsoluteCoords( point );
			Rect rect = new Rect();
			rect.left = (int)point.x;
			rect.top = (int)point.y;
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			rect.right = (int)point.x;
			rect.bottom = (int)point.y;
			this.setTag( rect );
			return viewParent.onCtrlEvent( this , MSG_LONGCLICK );
		}
		return super.onLongClick( x , y );
	}
}
