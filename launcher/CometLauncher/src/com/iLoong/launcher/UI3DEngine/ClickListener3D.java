package com.iLoong.launcher.UI3DEngine;


public interface ClickListener3D
{
	
	public boolean onClick(
			float x ,
			float y ,
			int pointer );
	
	public boolean onDoubleClick(
			float x ,
			float y ,
			int pointer );
	
	public boolean onLongClick(
			float x ,
			float y ,
			int pointer );
}
