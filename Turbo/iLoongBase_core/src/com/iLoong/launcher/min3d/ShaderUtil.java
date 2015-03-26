package com.iLoong.launcher.min3d;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.iLoong.launcher.Desktop3D.Log;


public class ShaderUtil
{
	
	public static Context context = null;
	public static ShaderProgram ambientShader = null;
	public static ShaderProgram defaultShader = null;
	
	public static void initContext(
			Context _context )
	{
		context = _context;
	}
	
	//�����ƶ�shader�ķ���
	public static int loadShader(
			int shaderType , //shader������  GLES20.GL_VERTEX_SHADER   GLES20.GL_FRAGMENT_SHADER
			String source //shader�Ľű��ַ�
	)
	{
		//����һ����shader
		int shader = GLES20.glCreateShader( shaderType );
		//�������ɹ������shader
		if( shader != 0 )
		{
			//����shader��Դ����
			GLES20.glShaderSource( shader , source );
			//����shader
			GLES20.glCompileShader( shader );
			//��ű���ɹ�shader����������
			int[] compiled = new int[1];
			//��ȡShader�ı������
			GLES20.glGetShaderiv( shader , GLES20.GL_COMPILE_STATUS , compiled , 0 );
			if( compiled[0] == 0 )
			{//������ʧ������ʾ������־��ɾ���shader
				Log.e( "ES20_ERROR" , "Could not compile shader " + shaderType + ":" );
				Log.e( "ES20_ERROR" , GLES20.glGetShaderInfoLog( shader ) );
				GLES20.glDeleteShader( shader );
				shader = 0;
			}
		}
		return shader;
	}
	
	//����shader����ķ���
	public static int createProgram(
			String vertexSource ,
			String fragmentSource )
	{
		//���ض�����ɫ��
		int vertexShader = loadShader( GLES20.GL_VERTEX_SHADER , vertexSource );
		if( vertexShader == 0 )
		{
			return 0;
		}
		//����ƬԪ��ɫ��
		int pixelShader = loadShader( GLES20.GL_FRAGMENT_SHADER , fragmentSource );
		if( pixelShader == 0 )
		{
			return 0;
		}
		//��������
		int program = GLES20.glCreateProgram();
		//�����򴴽��ɹ���������м��붥����ɫ����ƬԪ��ɫ��
		if( program != 0 )
		{
			//������м��붥����ɫ��
			GLES20.glAttachShader( program , vertexShader );
			checkGlError( "glAttachShader" );
			//������м���ƬԪ��ɫ��
			GLES20.glAttachShader( program , pixelShader );
			checkGlError( "glAttachShader" );
			//���ӳ���
			GLES20.glLinkProgram( program );
			//������ӳɹ�program����������
			int[] linkStatus = new int[1];
			//��ȡprogram���������
			GLES20.glGetProgramiv( program , GLES20.GL_LINK_STATUS , linkStatus , 0 );
			//������ʧ���򱨴?ɾ�����
			if( linkStatus[0] != GLES20.GL_TRUE )
			{
				Log.e( "ES20_ERROR" , "Could not link program: " );
				Log.e( "ES20_ERROR" , GLES20.glGetProgramInfoLog( program ) );
				GLES20.glDeleteProgram( program );
				program = 0;
			}
		}
		return program;
	}
	
	//���ÿһ�������Ƿ��д���ķ��� 
	public static void checkGlError(
			String op )
	{
		int error;
		while( ( error = GLES20.glGetError() ) != GLES20.GL_NO_ERROR )
		{
			Log.e( "ES20_ERROR" , op + ": glError " + error );
			throw new RuntimeException( op + ": glError " + error );
		}
	}
	
	public static ShaderProgram createAmbientShader()
	{
		if( ambientShader != null )
			return ambientShader;
		String mVertexShader = ShaderUtil.loadFromAssetsFile( "vertex.sh" , context.getResources() );
		// 加载片元着色器的脚本内容
		String mFragmentShader = ShaderUtil.loadFromAssetsFile( "frag.sh" , context.getResources() );
		ShaderProgram shader = new ShaderProgram( mVertexShader , mFragmentShader );
		if( shader.isCompiled() == false )
			throw new IllegalArgumentException( "couldn't compile shader: " + shader.getLog() );
		ambientShader = shader;
		return shader;
	}
	
	// NOTICE: We can not use id-code "Mali-400 MP" directly, since the other 
	// devices such as lenovo lephone do not work well with pargb shader.
	// We should find another code to distinguish between good devices and bad 
	// devices(for example, note2 and lephone).
	private static final String GPU_MODELS[] = {"PowerVR SGX 544" };
	
	/**
	 * Returns a new instance of the default shader used by SpriteBatch for GL2
	 * when no shader is specified.
	 */
	static public ShaderProgram createDefaultShader()
	{
		if( defaultShader != null )
			return defaultShader;
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "uniform vec4 u_color;\n" //
				+ "varying vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = u_color * " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "}";
		// special shader string for GPU:PowerVR SGX 544
		String fragmentShader_s4 = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "varying LOWP vec4 v_color;\n" //
				+ "varying vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "void main()\n"//
				+ "{\n" //
				+ "  vec4 l_color = v_color * texture2D(u_texture, v_texCoords);\n" //
				+ "  l_color.x = l_color.x * l_color.w;\n" + "  l_color.y = l_color.y * l_color.w;\n" + "  l_color.z = l_color.z * l_color.w;\n" + "  gl_FragColor = l_color;\n" //
				+ "}";
		String gpuModel = Gdx.gl.glGetString( GL10.GL_RENDERER );
		for( String model : GPU_MODELS )
		{
			if( gpuModel.startsWith( model ) )
			{
				fragmentShader = fragmentShader_s4;
				break;
			}
		}
		ShaderProgram shader = new ShaderProgram( vertexShader , fragmentShader );
		if( shader.isCompiled() == false )
			throw new IllegalArgumentException( "couldn't compile shader: " + shader.getLog() );
		defaultShader = shader;
		return shader;
	}
	
	public static String loadFromAssetsFile(
			String fname ,
			Resources r )
	{
		String result = null;
		try
		{
			InputStream in = r.getAssets().open( fname );
			int ch = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while( ( ch = in.read() ) != -1 )
			{
				baos.write( ch );
			}
			byte[] buff = baos.toByteArray();
			baos.close();
			in.close();
			result = new String( buff , "UTF-8" );
			result = result.replaceAll( "\\r\\n" , "\n" );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return result;
	}
}
