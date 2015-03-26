package com.iLoong.launcher.DesktopEdit;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;


public class AppIcon3D extends Icon3D
{
	
	public AppIcon3D(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public Icon3D clone()
	{
		if( this.region.getTexture() == null )
		{
			Log.e( "iLoong" , " icon:" + this + " region is null!!" );
			return null;
		}
		Icon3D icon = new Icon3D( this.name , this.region );
		if( this.background9 != null )
		{
			icon.setBackgroud( this.background9 );
		}
		icon.setItemInfo( this.getItemInfo() );
		icon.setPosition( this.getX() , this.getY() );
		return icon;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Desktop3DListener.root.addFlyView( this.clone() );
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
}
