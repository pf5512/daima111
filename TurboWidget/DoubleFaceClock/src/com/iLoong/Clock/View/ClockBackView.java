package com.iLoong.Clock.View;


import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.iLoong.Clock.Common.ClockHelper;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Object3DBase;


public class ClockBackView extends Object3DBase
{
	
	public static final String WATCH_BACK_OBJ = "watch_db_back.obj";
	private Cache<String , Mesh> mMeshCache = null;
	private MainAppContext mAppContext;
	public boolean enableLight = false;
	private Vector3 lightDirection = new Vector3();
	private Vector3 cameraPoint = new Vector3();
	
	public ClockBackView(
			String name ,
			MainAppContext appContext ,
			TextureRegion region )
	{
		super( appContext , name );
		this.mAppContext = appContext;
		x = 0;
		y = 0;
		this.region.setRegion( region );
		this.width = WidgetClock.MODEL_WIDTH;
		this.height = WidgetClock.MODEL_HEIGHT;
		this.setOrigin( width / 2 , height / 2 );
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
		// if (WidgetClock.SCALE_SIZE != 1f) {
		// mesh.scale(WidgetClock.SCALE_X, WidgetClock.SCALE_Y,
		// WidgetClock.SCALE_Z);
		// }
		setMesh( mesh );
		enableDepthMode( true );
	}
	
	public Mesh getMesh()
	{
		Mesh mesh = ClockHelper.getMesh( mMeshCache , mAppContext , WATCH_BACK_OBJ , width / 2 , height / 2 , 0 , WidgetClock.SCALE_X , WidgetClock.SCALE_Y , WidgetClock.SCALE_Z );
		return mesh;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( enableLight )
		{
			if( shader == null )
			{
				shader = createAmbientShader();
			}
		}
		else
		{
			if( shader == null )
			{
				shader = createDefaultShader();
			}
		}
		shader.begin();
		combinedMatrix.set( batch.getProjectionMatrix() ).mul( batch.getTransformMatrix() );
		shader.setUniformMatrix( "u_projTrans" , combinedMatrix );
		shader.setUniformi( "u_texture" , 0 );
		if( enableLight )
		{
			shader.setUniformMatrix( "uMMatrix" , batch.getTransformMatrix() );
			shader.setUniformf( "uLightDirction" , lightDirection );
			shader.setUniformf( "uCamera" , cameraPoint );
		}
		cur_color.r = color.r;
		cur_color.g = color.g;
		cur_color.b = color.b;
		cur_color.a = color.a;
		cur_color.a *= parentAlpha;
		//alpha premutiplied 解决半透明留白问题  wangjing&xupin 20131018
		//blendSrcAlpha  必须等于1;
		cur_color.r *= cur_color.a;
		cur_color.g *= cur_color.a;
		cur_color.b *= cur_color.a;
		//alpha premutiplied 解决半透明留白问题 wangjing&xupin 20131018
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
		Gdx.gl.glBlendFunc( blendSrcAlpha , GL10.GL_ONE_MINUS_SRC_ALPHA );
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
	
	public void move(
			Mesh mesh ,
			float dx ,
			float dy ,
			float dz )
	{
		VertexAttribute posAttr = mesh.getVertexAttribute( Usage.Position );
		int offset = posAttr.offset / 4;
		int numVertices = mesh.getNumVertices();
		int vertexSize = mesh.getVertexSize() / 4;
		float[] vertices = new float[numVertices * vertexSize];
		mesh.getVertices( vertices );
		int idx = offset;
		for( int i = 0 ; i < numVertices ; i++ )
		{
			vertices[idx] += dx;
			vertices[idx + 1] += dy;
			vertices[idx + 2] += dz;
			idx += vertexSize;
		}
		mesh.setVertices( vertices );
	}
	
	public ShaderProgram createAmbientShader()
	{
		String mVertexShader = Gdx.files.internal( "lightScript/vertex.sh" ).readString();
		// 加载片元着色器的脚本内容
		String mFragmentShader = Gdx.files.internal( "lightScript/frag.sh" ).readString();
		ShaderProgram shader = new ShaderProgram( mVertexShader , mFragmentShader );
		if( shader.isCompiled() == false )
			throw new IllegalArgumentException( "couldn't compile shader: " + shader.getLog() );
		float fieldOfView = 35f;
		float cameraZ = (float)( UtilsBase.getScreenHeight() / 2 / Math.tan( fieldOfView / 2 * MathUtils.degreesToRadians ) );
		cameraPoint.x = UtilsBase.getScreenWidth() / 2;
		cameraPoint.y = UtilsBase.getScreenHeight() / 2;
		cameraPoint.z = cameraZ;
		lightDirection.x = 0;
		lightDirection.y = 1;
		lightDirection.z = 1;
		blendSrcAlpha = 1;
		return shader;
	}
}
