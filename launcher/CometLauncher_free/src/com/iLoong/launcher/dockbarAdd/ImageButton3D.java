package com.iLoong.launcher.dockbarAdd;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.View3D;


public class ImageButton3D extends View3D
{
	
	private TextureRegion regionFocus;
	private TextureRegion regionNormal;
	private Event event;
	
	public interface Event
	{
		
		public void callback();
	}
	
	public void setEvent(
			Event event )
	{
		this.event = event;
	}
	
	public ImageButton3D(
			String name ,
			TextureRegion region1 ,
			TextureRegion region2 )
	{
		super( name );
		regionNormal = region1;
		regionFocus = region2;
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
		//		requestFocus();
		this.region = regionFocus;
		return true;
	}
	
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
		//		releaseFocus();
		this.region = regionNormal;
		return true;
	}
}
