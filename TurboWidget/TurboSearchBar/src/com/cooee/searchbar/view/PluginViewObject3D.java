package com.cooee.searchbar.view;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.UI3DEngine.adapter.Texture;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;
import com.iLoong.launcher.min3d.Object3DBase;


public class PluginViewObject3D extends Object3DBase
{
	
	// 全局Context参数，包括从Launcher中传递过来的参数
	protected MainAppContext appContext;
	// 主题名称，通过主题名称确定皮肤资源
	protected String themeName;
	// 图片名称
	protected String textureName;
	// 模型名称
	protected String objName;
	// 加载的模型类型，目前分为原始大小模型与压缩过后的模型，压缩过后的模型可以提高读取速度
	protected TextureRegion originalRegion;
	// 模型坐标系中心需要往X方向移动距离，使模型居中
	private float mMoveOffsetX;
	// 模型坐标系中心需要往Y方向移动距离，使模型居中
	private float mMoveOffsetY;
	// 模型坐标系中心需要往Z方向移动距离，使模型居中
	private float mMoveOffsetZ;
	// obj模型X方向缩放比例
	protected float mScaleX = 1f;
	// obj模型X方向缩放比例
	protected float mScaleY = 1f;
	// obj模型X方向缩放比例
	protected float mScaleZ = 1f;
	
	/**
	 * 
	 * @param appContext
	 * @param name
	 * @param region
	 * @param objName
	 */
	public PluginViewObject3D(
			MainAppContext appContext ,
			String name ,
			TextureRegion region ,
			String objName )
	{
		super( appContext , name );
		originalRegion = region;
		if( region != null )
		{
			this.region.setRegion( region );
		}
		//		Log.d("weijie", ">>>>PluginViewObject3D,objName=" + objName);
		this.objName = objName;
		this.appContext = appContext;
		this.themeName = appContext.mThemeName;
		setDepthMode( true );
	}
	
	/**
	 * 
	 * @param appContext
	 * @param name
	 * @param textureName
	 * @param objName
	 */
	public PluginViewObject3D(
			MainAppContext appContext ,
			String name ,
			String textureName ,
			String objName )
	{
		super( appContext , name );
		this.textureName = textureName;
		this.objName = objName;
		this.appContext = appContext;
		this.themeName = getThemeName();
		setDepthMode( true );
	}
	
	protected void build()
	{
		//		Log.d("weijie", ">>>>PluginViewObject3D build start");
		initObjScale();
		initTexture();
		initMesh();
		//		Log.d("weijie", ">>>>PluginViewObject3D build end");
	}
	
	protected void setDepthMode(
			boolean depthMode )
	{
		enableDepthMode( depthMode );
	}
	
	protected void setAppContext(
			MainAppContext appContext )
	{
		this.appContext = appContext;
	}
	
	public void setSize(
			float width ,
			float height )
	{
		super.setSize( width , height );
		this.setOrigin( width / 2 , height / 2 );
	}
	
	protected void setMoveOffset(
			float moveOffsetX ,
			float moveOffsetY ,
			float moveOffsetZ )
	{
		this.mMoveOffsetX = moveOffsetX;
		this.mMoveOffsetY = moveOffsetY;
		this.mMoveOffsetZ = moveOffsetZ;
	}
	
	protected void setObjScale(
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		this.mScaleX = scaleX;
		this.mScaleY = scaleY;
		this.mScaleZ = scaleZ;
	}
	
	protected void move(
			float moveOffsetX ,
			float moveOffsetY ,
			float moveOffsetZ )
	{
		this.x += moveOffsetX;
		this.y += moveOffsetY;
		this.z += moveOffsetZ;
		move( (com.iLoong.launcher.UI3DEngine.adapter.Mesh)mesh , moveOffsetX , moveOffsetY , moveOffsetZ );
	}
	
	protected void initObjScale()
	{
		this.mScaleX = this.mScaleY = this.mScaleZ = WidgetSearchBar.scale;
		// ((float) appContext.mWidgetContext
		// .getResources().getDisplayMetrics().density);
		// Log.d("weijie",
		// ">>>>PluginViewObject3D initObjScale end mScaleX="+this.mScaleX);
	}
	
	protected void initTexture()
	{
		if( region == null || region.getTexture() == null )
		{
			if( originalRegion == null )
			{
				Texture texture = new Texture( appContext.gdx , new AndroidFiles( appContext.mWidgetContext.getAssets() ).internal( getThemeTexturePath() ) );
				texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				originalRegion = new TextureRegion( texture );
			}
			this.region.setRegion( originalRegion );
		}
	}
	
	protected void initMesh()
	{
		//		Log.d("weijie", ">>>>PluginViewObject3D initMesh start");
		Mesh mesh = loadMesh();
		//		Log.d("weijie", ">>>>PluginViewObject3D mesh=" + mesh);
		this.setMesh( mesh );
		//		Log.d("weijie", ">>>>PluginViewObject3D initMesh end");
	}
	
	private Mesh loadMesh()
	{
		String objPath = getThemeObjPath();
		//		Log.d("weijie", ">>>>PluginViewObject3D loadMesh start objPath="
		//				+ objPath);
		Mesh mesh = loadOriginalMesh( appContext , this.objName , objPath , mMoveOffsetX , mMoveOffsetY , mMoveOffsetZ , this.mScaleX , this.mScaleY , this.mScaleZ );
		//		Log.d("weijie", ">>>>PluginViewObject3D loadMesh start objPath1="
		//				+ objPath);
		//		if (mesh != null)
		//			Log.d("weijie", ">>>>PluginViewObject3D loadMesh start objPath21="
		//					+ objPath);
		return mesh;
	}
	
	public Mesh loadOriginalMesh(
			MainAppContext appContext ,
			String objName ,
			String objPath ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		InputStream stream = null;
		Mesh mesh = null;
		try
		{
			stream = appContext.mWidgetContext.getAssets().open( objPath );
			mesh = (Mesh)ObjLoader.loadObj( appContext.gdx , stream , true , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mesh;
	}
	
	protected String getThemeName()
	{
		if( appContext.mThemeName == null )
		{
			return "iLoong";
		}
		String themeName = appContext.mThemeName;
		if( themeName == null || themeName.length() == 0 )
		{
			return "iLoong";
		}
		if( !themeName.equals( "iLoong" ) && !themeName.equals( "female" ) )
		{
			themeName = "iLoong";
		}
		return themeName;
	}
	
	protected String getThemeTexturePath()
	{
		// return themeName + "/image/" + textureName;
		return "theme/widget/calender/comet/image/" + textureName;
	}
	
	protected String getThemeObjPath()
	{
		//		return themeName + "/original_obj/" + objName;
		return "theme/widget/searchbar/iLoongSearchBar/original_obj/" + objName;
	}
	
	public boolean is3dRotation()
	{
		return true;
	}
	
	public Mesh getMesh(
			Cache<String , Mesh> meshCache ,
			MainAppContext appContext ,
			String objName ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		String objPath = getThemeObjPath();
		return loadOriginalMesh( appContext , objName , objPath , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
	}
	
	public void saveMesh(
			Mesh mesh ,
			String fileName ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ ) throws IOException
	{
		float vertices[] = new float[mesh.getNumVertices() * mesh.getVertexSize() / 4];
		short indices[] = new short[mesh.getNumIndices()];
		VertexAttributes verAttr = mesh.getVertexAttributes();
		mesh.getVertices( vertices );
		mesh.getIndices( indices );
		File file = new File( "mnt/sdcard/" + fileName );
		file.createNewFile();
		FileOutputStream fileOutStream = new FileOutputStream( file );
		DataOutputStream oos = new DataOutputStream( fileOutStream );
		mesh.getVertexAttributes();
//		Log.v( "weijie" , "saveMesh verAttr.size=" + verAttr.size() );
		oos.writeInt( verAttr.size() );
		for( int i = 0 ; i < verAttr.size() ; i++ )
		{
			VertexAttribute curattr = verAttr.get( i );
			oos.writeInt( curattr.usage );
			oos.writeInt( curattr.numComponents );
			oos.writeUTF( curattr.alias );
		}
//		Log.v( "weijie" , "saveMesh vertices.length=" + vertices.length );
		oos.writeInt( vertices.length );
		int index = 0;
		int vertex_size = verAttr.vertexSize / 4;
		for( int i = 0 ; i < vertices.length ; i++ )
		{
			if( index == 0 )
			{
				oos.writeFloat( ( vertices[i] + offsetX ) / scaleX );
			}
			else if( index == 1 )
			{
				oos.writeFloat( ( vertices[i] + offsetY ) / scaleY );
			}
			else if( index == 2 )
			{
				oos.writeFloat( ( vertices[i] + offsetZ ) / scaleZ );
			}
			else
			{
				oos.writeFloat( vertices[i] );
			}
			index++;
			if( index == vertex_size )
			{
				index = 0;
			}
		}
		oos.writeInt( indices.length );
		for( int i = 0 ; i < indices.length ; i++ )
		{
			oos.writeShort( indices[i] );
		}
		oos.close();
		fileOutStream.close();
	}
	
	public Mesh copyMesh(
			Mesh originalMesh )
	{
		Mesh mesh = null;
		// 创建Mesh的副本
		float vertices[] = new float[originalMesh.getNumVertices() * originalMesh.getVertexSize() / 4];
		short indices[] = new short[originalMesh.getNumIndices()];
		originalMesh.getVertices( vertices );
		originalMesh.getIndices( indices );
		mesh = new Mesh( appContext.gdx , true , vertices.length , indices.length , originalMesh.getVertexAttributes() );
		mesh.setVertices( vertices );
		mesh.setIndices( indices );
		return mesh;
	}
	
	private void move(
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
	
	public void dispose()
	{
		super.dispose();
		if( originalRegion != null && originalRegion.getTexture() != null )
		{
			originalRegion.getTexture().dispose();
			originalRegion = null;
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return super.onClick( x , y );
	}
}
