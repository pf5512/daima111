package com.iLoong.launcher.Widget3D;


import android.graphics.Bitmap;

import com.iLoong.launcher.Desktop3D.Desktop3DListener;


public class OtherTools3DShortcutIn extends Widget3DVirtual
{
	
	public OtherTools3DShortcutIn(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name , bmp , title );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Desktop3DListener.root.showWorkSpaceFromAllApp1();
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return super.onLongClick( x , y );
	}
}
