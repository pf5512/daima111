package com.iLoong.Clock.View;


import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.min3d.Object3DBase;


public class ObjectView3D extends Object3DBase
{
	
	public CooGdx cooGdx;
	
	public ObjectView3D(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		// batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		cooGdx.gl.glDepthMask( true );
		cooGdx.gl.glEnable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		super.draw( batch , parentAlpha );
		cooGdx.gl.glDisable( GL10.GL_DEPTH_TEST );
		cooGdx.gl.glClear( GL10.GL_DEPTH_BUFFER_BIT ); // 清除屏幕及深度缓存
		cooGdx.gl.glDepthMask( false );
	}
}
