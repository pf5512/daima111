package com.iLoong.Clock.View;


import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class ObjHandView extends ObjBaseView
{
	
	public ObjHandView(
			String name )
	{
		super( name );
	}
	
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
		if( WidgetClock.live == 0 )
		{
			Gdx.gl.glBlendFunc( 1 , 771 );
		}
		else
		{
			Gdx.gl.glBlendFunc( 770 , 771 );
		}
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
