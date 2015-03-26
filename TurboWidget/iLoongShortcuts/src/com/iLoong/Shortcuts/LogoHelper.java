package com.iLoong.Shortcuts;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.MainAppContext;


public class LogoHelper
{
	
	public static Mesh loadMesh(
			Gdx gdx ,
			InputStream stream ) throws IOException
	{
		Mesh mesh = null;
		DataInputStream oos = new DataInputStream( stream );
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>( verAttr_size );
		for( int i = 0 ; i < verAttr_size ; i++ )
		{
			attributes.add( new VertexAttribute( oos.readInt() , oos.readInt() , oos.readUTF() ) );
		}
		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		for( int i = 0 ; i < vertices_size ; i++ )
		{
			vertices[i] = oos.readFloat();
		}
		int indices_size = oos.readInt();
		short indices[] = new short[indices_size];
		for( int i = 0 ; i < indices_size ; i++ )
		{
			indices[i] = oos.readShort();
		}
		oos.close();
		stream.close();
		mesh = new Mesh( gdx , true , vertices.length , indices.length , attributes.toArray( new VertexAttribute[attributes.size()] ) );
		mesh.setVertices( vertices );
		mesh.setIndices( indices );
		return mesh;
	}
	
	public static Mesh loadMesh(
			Gdx gdx ,
			Context context ,
			String meshObjFileName )
	{
		AndroidFiles gdxFile = new AndroidFiles( context.getAssets() );
		String filePath = meshObjFileName;
		Mesh mesh = null;
		try
		{
			FileHandle fileHandle = gdxFile.internal( filePath );
			if( !fileHandle.exists() )
			{
				filePath = "iLoong" + File.separator + meshObjFileName;
			}
			InputStream is = null;
			try
			{
				is = context.getAssets().open( filePath );
				mesh = loadMesh( gdx , is );
				is.close();
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
					is.close();
				}
				catch( IOException e1 )
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return mesh;
	}
	
	public static String getThemeImagePath(
			String themeName ,
			String imageName )
	{
		Log.v( "weijie" , "getThemeImagePath return= " + themeName + "/image/" + imageName );
		return themeName + "/image/" + imageName;
	}
	
	public static String getThemeObjPath(
			String themeName ,
			String objName )
	{
		return themeName + "/original_obj/" + objName;
	}
}
