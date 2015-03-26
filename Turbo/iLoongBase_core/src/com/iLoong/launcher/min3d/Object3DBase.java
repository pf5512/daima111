package com.iLoong.launcher.min3d;


import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class Object3DBase extends View3D
{
	
	protected Faces3D faces;
	protected Vertices3D vertices;
	protected Mesh mesh;
	protected ShaderProgram shader = null;
	protected boolean depth_test = false;
	// 背面裁切
	protected boolean cullFace = false;
	protected boolean enableLghtShader = false;
	protected int blendSrcAlpha = 1;
	protected final Matrix4 combinedMatrix = new Matrix4();
	Vector3 lightPoint = new Vector3();
	Vector3 cameraPoint = new Vector3();
	
	public ShaderProgram createAmbientShader()
	{
		ShaderProgram shader = ShaderUtil.createAmbientShader();
		float fieldOfView = 35f;
		float cameraZ = (float)( UtilsBase.getScreenHeight() / 2 / Math.tan( fieldOfView / 2 * MathUtils.degreesToRadians ) );
		cameraPoint.x = UtilsBase.getScreenWidth() / 2;
		cameraPoint.y = UtilsBase.getScreenHeight() / 2;
		cameraPoint.z = cameraZ;
		lightPoint.x = UtilsBase.getScreenWidth() / 2;
		lightPoint.y = UtilsBase.getScreenHeight() / 2;
		lightPoint.z = cameraZ;
		return shader;
	}
	
	public static ShaderProgram createDefaultShader()
	{
		return ShaderUtil.createDefaultShader();
	}
	
	public Object3DBase(
			Gdx gdx ,
			String name )
	{
		this( name );
	}
	
	public Object3DBase(
			MainAppContext appContext ,
			String name )
	{
		this( name );
		// 只为编译通过，不需要对appContext做处�?
	}
	
	public Object3DBase(
			String name )
	{
		super( name );
	}
	
	public void setMesh(
			int face_num ,
			int vertices_num )
	{
		faces = new Faces3D( face_num );
		vertices = new Vertices3D( vertices_num );
		mesh = new Mesh( Mesh.VertexDataType.VertexArray , false , vertices_num * 2 , face_num * 3 , new VertexAttribute[]{
				new VertexAttribute( 0 , 3 , "a_position" ) ,
				new VertexAttribute( 5 , 4 , "a_color" ) ,
				new VertexAttribute( 3 , 2 , "a_texCoord0" ) } );
	}
	
	public void setTexture(
			Texture texture )
	{
		region.setRegion( texture );
	}
	
	public void setMesh(
			Mesh mesh )
	{
		this.mesh = mesh;
		faces = null;
		vertices = null;
	}
	
	public void setVertices(
			float[] meshVertices )
	{
	}
	
	public void enableDepthMode(
			boolean depth_mode )
	{
		depth_test = depth_mode;
	}
	
	public void enableCullFace(
			boolean cullFace )
	{
		this.cullFace = cullFace;
	}
	
	public void setBlendSrcAlpha(
			int blendSrcAlpha )
	{
		this.blendSrcAlpha = blendSrcAlpha;
	}
	
	@Override
	public void setColor(
			float r ,
			float g ,
			float b ,
			float a )
	{
		// TODO Auto-generated method stub
		color.r = r;
		color.g = g;
		color.b = b;
		color.a = a;
		// if (vertices != null) {
		// vertices.setColor(color);
		// } else {
		// if (mesh != null) {
		// meshVertices = new float[mesh.getNumVertices()
		// * mesh.getVertexSize() / 4];
		// mesh.getVertices(meshVertices);
		// int step = mesh.getVertexSize() / 4;
		// int index = 3;
		// for (int i = 0; i < mesh.getNumVertices(); i++) {
		// meshVertices[index] = color.toFloatBits();
		// index += step;
		// }
		// mesh.setVertices(meshVertices);
		// }
		//
		// }
	}
	
	Color cur_color = new Color();
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// if (shader == null) {
		// shader = createDefaultShader();
		// }
		if( shader == null )
		{
			if( enableLghtShader )
			{
				shader = createAmbientShader();
			}
			else
			{
				shader = createDefaultShader();
			}
		}
		shader.begin();
		combinedMatrix.set( batch.getProjectionMatrix() ).mul( batch.getTransformMatrix() );
		shader.setUniformMatrix( "u_projTrans" , combinedMatrix );
		shader.setUniformi( "u_texture" , 0 );
		if( enableLghtShader )
		{
			shader.setUniformMatrix( "uMMatrix" , batch.getTransformMatrix() );
			shader.setUniformf( "uLightLocation" , lightPoint );
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
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓�?
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
		if( mesh != null && faces != null )
			mesh.setIndices( faces.getIndices() );
		if( mesh != null && vertices != null )
			mesh.setVertices( vertices.getVertices() );
		if( mesh != null )
		{
			if( Gdx.graphics.isGL20Available() )
			{
				mesh.render( shader , GL10.GL_TRIANGLES );
			}
			else
			{
				mesh.render( GL10.GL_TRIANGLES );
			}
		}
		if( depth_test )
		{
			Gdx.gl.glDisable( GL10.GL_DEPTH_TEST );
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓�?
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
	
	public void dispose()
	{
		super.dispose();
		if( this.mesh != null )
		{
			this.mesh.dispose();
			this.mesh = null;
		}
		//		if( shader != null )
		//		{
		//			shader.dispose();
		//		}
	}
	
	public void moveObj(
			com.badlogic.gdx.graphics.Mesh mesh ,
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
	
	public Mesh getMesh()
	{
		return this.mesh;
	}
	
	@Override
	public boolean is3dRotation()
	{
		return true;
	}
}
