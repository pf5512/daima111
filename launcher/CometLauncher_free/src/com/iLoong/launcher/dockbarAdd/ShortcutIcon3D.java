package com.iLoong.launcher.dockbarAdd;


import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.desktopEdit.DesktopEdit;


public class ShortcutIcon3D extends Icon3D
{
	
	private List<Icon3D> icons = new ArrayList<Icon3D>();
	public static final int MSG_HIDE_ADD_VIEW = 0;
	
	public ShortcutIcon3D(
			String name )
	{
		super( name );
	}
	
	public ShortcutIcon3D(
			String name ,
			TextureRegion t )
	{
		super( name , t );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( hide || uninstall )
			return true;
		icons.add( this );
		Workspace3D.getInstance().getSelectShortcut( icons );
		Workspace3D.getInstance().addToDesk();
		viewParent.onCtrlEvent( this , ShortcutIcon3D.MSG_HIDE_ADD_VIEW );
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		viewParent.onCtrlEvent( this , Icon3D.MSG_ICON_LONGCLICK );
		return true;
	}
	
	@Override
	public void onParticleCallback(
			int type )
	{
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
}
