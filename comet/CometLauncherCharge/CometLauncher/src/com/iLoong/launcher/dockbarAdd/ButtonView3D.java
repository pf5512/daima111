package com.iLoong.launcher.dockbarAdd;


import android.graphics.Bitmap;
import android.graphics.Color;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;


class ButtonView3D extends View3D
{
	
	public interface Event
	{
		
		public void callback();
	}
	
	//	private NinePatch backNormalground = null;
	//	private NinePatch backFocusground = null;
	private TextureRegion titleRegion = null;
	private TextureRegion titleRegion1 = null;
	private float titleTopOffset = 0;
	Event event;
	
	public void setEvent(
			Event event )
	{
		this.event = event;
	}
	
	public ButtonView3D(
			String name ,
			TextureRegion texture ,
			TextureRegion texture1 ,
			TextureRegion texture2 )
	{
		super( name );
		// TODO Auto-generated constructor stub
		setSize( texture.getRegionWidth() , texture.getRegionHeight() );
		this.setOrigin( width / 2 , height / 2 );
		//		TextureRegion backgroundTexture = texture1;
		//		backFocusground = new NinePatch(backgroundTexture, 15, 15, 15, 15);
		//		backgroundTexture  =texture2;
		//		backNormalground = new NinePatch(backgroundTexture, 15, 15, 15, 15);
		titleRegion = texture;
		titleRegion1 = texture1;
		titleTopOffset = ( this.height - titleRegion.getRegionHeight() ) / 2f;
		if( titleTopOffset < 0 )
		{
			titleTopOffset = 0;
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer > 0 )
		{
			return false;
		}
		displayFocusBG( true );
		return true;
	}
	
	public void displayFocusBG(
			boolean bFocus )
	{
		if( bFocus )
		{
			//this.setBackgroud(backFocusground);
			this.region = titleRegion1;
		}
		else
		{
			//this.setBackgroud(backNormalground);
			this.region = titleRegion;
		}
	}
	
	//	@Override
	//	public void draw(SpriteBatch batch, float parentAlpha) {
	//		super.draw(batch, parentAlpha);
	//		batch.draw(titleRegion, this.x, this.y + titleTopOffset);
	//	}
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer > 0 )
		{
			return false;
		}
		if( event != null )
		{
			event.callback();
		}
		displayFocusBG( false );
		return true;
	}
}
