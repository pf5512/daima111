package com.iLoong.launcher.Widget3D;


import com.badlogic.gdx.Gdx;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;


public class Widget3DRefreshRender implements IRefreshRender
{
	
	@Override
	public void RefreshRender()
	{
		if( Gdx.graphics != null )
			Gdx.graphics.requestRendering();
	}
	
	@Override
	public void setContinuousRendering(
			boolean isContinuous )
	{
		// TODO Auto-generated method stub
	}
}
