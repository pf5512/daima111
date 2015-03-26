package com.iLoong.Clock.View;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ObjBaseView extends Object3DBase
{
	
	private Context mContext;
	public String objName;
	public String imgName;
	public static Map<String , Texture> mTextures;
	private Cache<String , Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	private float depth;
	
	public ObjBaseView(
			String name ,
			MainAppContext appContext ,
			Cache<String , Mesh> cache ,
			String imgName ,
			String objName ,
			float depth )
	{
		super( name );
		this.objName = objName;
		this.imgName = imgName;
		this.mContext = appContext.mWidgetContext;
		this.mAppContext = appContext;
		this.x = 0;
		this.y = 0;
		this.depth = depth;
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin( width / 2 , height / 2 );
		init( cache );
	}
	
	public ObjBaseView(
			String name )
	{
		super( name );
	}
	
	public void initResource()
	{
		String path = ClockComponent.imgPath;
		mTextures = new HashMap<String , Texture>();
		try
		{
			BitmapTexture texture = new BitmapTexture( BitmapFactory.decodeStream( mContext.getAssets().open( path + imgName ) ) );
			texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			mTextures.put( objName , texture );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	Color cur_color = new Color();
	
	public void init(
			Cache<String , Mesh> cache )
	{
		initResource();
		setMeshCacheEx( cache );
		renderMeshEx( this.x , this.y );
		this.region.setRegion( mTextures.get( objName ) );
		this.bringToFront();
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	public void setMeshCacheEx(
			Cache<String , Mesh> cache )
	{
		this.mMeshCache = cache;
	}
	
	public void renderMeshEx(
			float dx ,
			float dy )
	{
		Mesh mesh = getMeshEx();
		//  ClockComponent.moveTo(mesh, 0, 0, depth);
		setMesh( mesh );
		enableDepthMode( true );
	}
	
	public Mesh getMeshEx()
	{
		Mesh mesh = ClockHelper.getMesh( mMeshCache , mAppContext , objName , width / 2 , height / 2 , 0 , WidgetClock.SCALE_X , WidgetClock.SCALE_Y , WidgetClock.SCALE_Z );
		return mesh;
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
