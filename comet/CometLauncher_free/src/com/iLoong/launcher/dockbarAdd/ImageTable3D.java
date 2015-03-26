package com.iLoong.launcher.dockbarAdd;


import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class ImageTable3D extends ViewGroup3D
{
	
	private TextureRegion regionFocusBg;
	private TextureRegion regionNormalBg;
	private TextureRegion regionFocusTitle;
	private TextureRegion regionNormalTitle;
	private CallBack event;
	private View3D titleView;
	
	public interface CallBack
	{
		
		public void callback();
	}
	
	public void setEvent(
			CallBack event )
	{
		this.event = event;
	}
	
	public ImageTable3D(
			String name ,
			TextureRegion region1 ,
			TextureRegion region2 )
	{
		super( name );
		regionNormalBg = region1;
		regionFocusBg = region2;
		//		regionNormalTitle = region3;
		//		regionFocusTitle = region4;
		titleView = new View3D( "title" );
		addView( titleView );
	}
	
	public View3D getTitleView()
	{
		return titleView;
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
		//		this.region = regionFocus;
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
		//		this.region = regionNormal;
		return true;
	}
}
