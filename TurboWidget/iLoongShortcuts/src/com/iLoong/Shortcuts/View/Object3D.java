package com.iLoong.Shortcuts.View;


import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.Faces3D;
import com.iLoong.launcher.min3d.Vertices3D;
import com.iLoong.launcher.theme.ThemeManager;


public class Object3D extends View3D
{
	
	protected MainAppContext mAppContext;
	protected Faces3D faces;
	protected Vertices3D vertices;
	protected Mesh mesh;
	protected ShaderProgram shader = null;
	private boolean depth_test = false;
	protected final Matrix4 combinedMatrix = new Matrix4();
	protected final Matrix4 transformedMatrix = new Matrix4();
	final Color ambient = new Color( 0.5f , 0.5f , 0.5f , 5f );
	private Vector3 lightPos = new Vector3( 400 , 2000 , 2000 );
	private Vector3 lightPos2 = new Vector3( 0 , 0 , 0 );
	private Vector3 lightPos3 = new Vector3( 1000 , 5000 , 0 );
	private Vector3 cameraPos = new Vector3();
	public TextureRegion ambientTexture = null;
	public TextureRegion movingLightTexture = null;
	public TextureRegion hiliteTexture = null;
	// 倒影
	public int hasMirror;
	public float horizonalY;
	public float horizonalEndY;
	// widget position
	public float widgetY;
	public float movingLightX;
	public float hiliteLightAlpha;
	
	/**
	 * Returns a new instance of the default shader used by SpriteBatch for GL2
	 * when no shader is specified.
	 */
	public ShaderProgram createDefaultShader()
	{
		FileHandle vShader = new AndroidFiles( mAppContext.mWidgetContext.getAssets() ).internal( "facevertexshader.glsl" );
		FileHandle fShader = new AndroidFiles( mAppContext.mWidgetContext.getAssets() ).internal( "facefragmentshader.glsl" );
		String vShaderString = vShader.readString();
		String fShaderString = fShader.readString();
		ShaderProgram shader = new ShaderProgram( vShaderString , fShaderString );
		if( shader.isCompiled() == false )
			throw new IllegalArgumentException( "couldn't compile shader: " + shader.getLog() );
		return shader;
	}
	
	public Object3D(
			Gdx gdx ,
			String name )
	{
		this( name );
	}
	
	public Object3D(
			MainAppContext appContext ,
			String name )
	{
		this( name );
		mAppContext = appContext;
		cameraPos.x = Utils3D.getScreenWidth() / 2;
		cameraPos.y = Utils3D.getScreenHeight() / 2;
		cameraPos.z = (float)( Utils3D.getScreenHeight() / 2 / Math.tan( 35.0f / 2 * MathUtils.degreesToRadians ) );
		// cameraPos = getStage().getCamera().position;
		// 只为编译通过，不需要对appContext做处理
	}
	
	public Object3D(
			String name )
	{
		super( name );
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
	
	public void setShader(
			ShaderProgram glsl )
	{
		shader = glsl;
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
		shader.pedantic = false;
		// for(int i = 0; i < 4; i++){
		// for(int j = 0; j < 4; j++){
		// Log.d("xujihao", String.format("Matrix[%d][%d]=%f", i, j,
		// batch.getTransformMatrix().val[4*i+j]));
		// }
		// }
		widgetY = getParent().getParent().getParent().getY();
		movingLightX = getParent().getParent().getUser();
		hiliteLightAlpha = getParent().getUser();
		//Gdx.app.log("xujihao", "movingLightX is " + movingLightX);
		shader.setUniformf( "u_widgetY" , widgetY );
		shader.setUniformf( "u_width" , Utils3D.getScreenWidth() );
		shader.setUniformf( "u_height" , Utils3D.getScreenHeight() );
		shader.setUniformf( "u_movingLightX" , movingLightX );
		shader.setUniformf( "u_hiliteLightAlpha" , hiliteLightAlpha );
		//Gdx.app.log("xujihao", " y is " + widgetY);
		transformedMatrix.set( batch.getTransformMatrix() );
		shader.setUniformMatrix( "u_trans" , transformedMatrix );
		combinedMatrix.set( batch.getProjectionMatrix() ).mul( batch.getTransformMatrix() );
		if( hasMirror == 2 )
		{
			// 翻转
			Matrix4 tmpScaleMatrix = new Matrix4();
			tmpScaleMatrix.setToScaling( 1 , -1 , 1 );
			Matrix4 tmpTransMatrix = new Matrix4();
			tmpTransMatrix.setToTranslation( 0 , -horizonalY , 0 );
			Matrix4 tmpTransMatrix2 = new Matrix4();
			tmpTransMatrix2.setToTranslation( 0 , horizonalY , 0 );
			combinedMatrix.set( batch.getProjectionMatrix() ).mul( tmpTransMatrix ).mul( tmpScaleMatrix ).mul( tmpTransMatrix2 ).mul( batch.getTransformMatrix() );
		}
		shader.setUniformMatrix( "u_projTrans" , combinedMatrix );
		shader.setUniformf( "lightPos" , lightPos );
		shader.setUniformf( "lightPos2" , lightPos2 );
		shader.setUniformf( "lightPos3" , lightPos3 );
		shader.setUniformf( "cameraPos" , cameraPos );
		shader.setUniformf( "u_parentAlpha" , parentAlpha );
		shader.setUniformi( "u_texture" , 0 );
		shader.setUniformi( "u_texture_env" , 1 );
		shader.setUniformi( "u_texture_ml" , 2 );
		shader.setUniformi( "u_texture_hilite" , 3 );
		// mirror
		shader.setUniformi( "u_mirror" , hasMirror );
		shader.setUniformf( "u_horizon_y" , horizonalY );
		shader.setUniformf( "u_horizon_end_y" , horizonalEndY );
		shader.setUniformMatrix( "u_projMatrix" , batch.getProjectionMatrix() );
		Matrix4 transMatrix;
		transMatrix = batch.getTransformMatrix().cpy();
		shader.setUniformMatrix( "u_transMatrix" , transMatrix );
		Color cur_color = new Color( ambient );
		// cur_color.a *= parentAlpha;
		shader.setUniformf( "u_color" , cur_color );
		if( depth_test )
		{
			Gdx.gl.glDepthMask( true );
			Gdx.gl.glEnable( GL10.GL_DEPTH_TEST );
			// Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT); // 清除屏幕及深度缓存
			Gdx.gl.glDepthFunc( GL10.GL_LEQUAL );
		}
		if( ambientTexture != null )
		{
			if( ambientTexture.getTexture() != null )
			{
				ambientTexture.getTexture().bind( 1 );
			}
		}
		if( movingLightTexture != null )
		{
			if( movingLightTexture.getTexture() != null )
			{
				movingLightTexture.getTexture().bind( 2 );
			}
		}
		if( hiliteTexture != null )
		{
			if( hiliteTexture.getTexture() != null )
			{
				hiliteTexture.getTexture().bind( 3 );
			}
		}
		if( region.getTexture() != null )
		{
			region.getTexture().bind( 0 );
		}
		Gdx.gl.glEnable( GL10.GL_BLEND );
		Gdx.gl.glBlendFunc( GL10.GL_SRC_ALPHA , GL10.GL_ONE_MINUS_SRC_ALPHA );
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
		shader.end();
	}
	
	public void dispose()
	{
		super.dispose();
		if( this.mesh != null )
		{
			this.mesh.dispose();
		}
		if( shader != null )
		{
			shader.dispose();
		}
	}
}
