package com.iLoong.Clock.View;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

import java.io.IOException;


public class test extends Object3DBase
{
	
	private final String OBJ_NAME = "test.obj";
	private MainAppContext appContext;
	private Cache<String , Mesh> mMeshCache = null;
	
	public test(
			String name ,
			MainAppContext appContext )
	{
		super( name );
		this.appContext = appContext;
		Texture texture = null;
		x = 0;
		y = 0;
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin( width / 2 , height / 2 );
		try
		{
			texture = new BitmapTexture( BitmapFactory.decodeStream( appContext.mWidgetContext.getAssets().open( ClockComponent.imgPath + "test.png" ) ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		this.setTexture( texture );
	}
	
	public void setMeshCache(
			Cache<String , Mesh> cache )
	{
		this.mMeshCache = cache;
	}
	
	public void renderMesh(
			float dx ,
			float dy )
	{
		Mesh mesh = getMesh();
		setMesh( mesh );
		enableDepthMode( true );
	}
	
	public Mesh getMesh()
	{
		return ClockHelper.getMesh( mMeshCache , appContext , OBJ_NAME , width / 2 , height / 2 , 0 , WidgetClock.SCALE_X , WidgetClock.SCALE_Y , WidgetClock.SCALE_Z );
	}
	
	public void updateSecondView(
			float secondRotation )
	{
		setRotationAngle( 0 , 0 , secondRotation - 90 );
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	Color cur_color = new Color();
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( shader == null )
		{
			shader = createDefaultShader();
		}
		shader.begin();
		combinedMatrix.set( batch.getProjectionMatrix() ).mul( batch.getTransformMatrix() );
		shader.setUniformMatrix( "u_projTrans" , combinedMatrix );
		shader.setUniformi( "u_texture" , 0 );
		cur_color.r = color.r;
		cur_color.g = color.g;
		cur_color.b = color.b;
		cur_color.a = color.a;
		cur_color.a *= parentAlpha;
		shader.setUniformf( "u_color" , cur_color );
		if( depth_test )
		{
			Gdx.gl.glDepthMask( true );
			Gdx.gl.glEnable( GL10.GL_DEPTH_TEST );
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		}
		if( region.getTexture() != null )
		{
			region.getTexture().bind();
		}
		if( cullFace )
		{
			if( Gdx.graphics.isGL20Available() )
			{
				Gdx.gl.glEnable( GLES20.GL_CULL_FACE );
			}
			else
			{
				Gdx.gl.glEnable( GL10.GL_CULL_FACE );
			}
		}
		Gdx.gl.glEnable( GL10.GL_BLEND );
		Gdx.gl.glBlendFunc( 1 , 771 );
		if( faces != null )
			mesh.setIndices( faces.getIndices() );
		if( vertices != null )
			mesh.setVertices( vertices.getVertices() );
		if( Gdx.graphics.isGL20Available() )
		{
			mesh.render( shader , GL10.GL_TRIANGLES );
		}
		else
		{
			mesh.render( GL10.GL_TRIANGLES );
		}
		if( depth_test )
		{
			Gdx.gl.glDisable( GL10.GL_DEPTH_TEST );
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthMask( false );
		}
		if( cullFace )
		{
			if( Gdx.graphics.isGL20Available() )
			{
				Gdx.gl.glDisable( GLES20.GL_CULL_FACE );
			}
			else
			{
				Gdx.gl.glDisable( GL10.GL_CULL_FACE );
			}
		}
		shader.end();
	}
}
