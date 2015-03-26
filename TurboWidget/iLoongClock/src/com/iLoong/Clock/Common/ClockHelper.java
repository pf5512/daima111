package com.iLoong.Clock.Common;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ComponentName;
import android.content.Context;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.iLoong.Clock.View.WidgetClock;
import com.iLoong.Widget.theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.min3d.ObjLoader;


public class ClockHelper
{
	
	public static float getSecondHandRotation()
	{
		Calendar calendar = Calendar.getInstance();
		int currentSecond = calendar.get( Calendar.SECOND );
		float secondRadians = ( 360 - ( ( currentSecond * 6 ) - 90 ) ) % 360;
		Log.e( "**************" , "currentSecond:" + currentSecond + " angle:" + secondRadians );
		return secondRadians;
	}
	
	public static float getSecondHandRotation(
			int currentSecond )
	{
		return ( 360 - ( ( currentSecond * 6 ) - 90 ) ) % 360;
	}
	
	public static float getMinuteHandRotation()
	{
		Calendar calendar = Calendar.getInstance();
		int currentMinute = calendar.get( Calendar.MINUTE );
		float minuteRadians = ( 360 - ( ( currentMinute * 6 ) - 90 ) ) % 360;
		return minuteRadians;
	}
	
	public static float getMinuteHandRotation(
			int currentMinute )
	{
		return ( 360 - ( ( currentMinute * 6 ) - 90 ) ) % 360;
	}
	
	public static float getHourHandRotation()
	{
		Calendar calendar = Calendar.getInstance();
		int currentMinute = calendar.get( Calendar.MINUTE );
		int currentHour = calendar.get( Calendar.HOUR );
		float hourRadian = ( 360 - ( ( currentHour * 30 ) - 90 ) ) % 360 - ( 30 * currentMinute / 60 );
		return hourRadian;
	}
	
	public static float getHourHandRotation(
			int currentMinute ,
			int currentHour )
	{
		return ( 360 - ( ( currentHour * 30 ) - 90 ) ) % 360 - ( 30 * currentMinute / 60 );
	}
	
	String SDPATH = "mnt/sdcard/";
	
	public static void saveMesh(
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
		oos.writeInt( verAttr.size() );
		for( int i = 0 ; i < verAttr.size() ; i++ )
		{
			VertexAttribute curattr = verAttr.get( i );
			oos.writeInt( curattr.usage );
			oos.writeInt( curattr.numComponents );
			oos.writeUTF( curattr.alias );
		}
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
	
	public static Mesh loadCompressedMesh(
			Gdx gdx ,
			InputStream stream ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ ) throws IOException
	{
		return loadCompressedMeshOpt( gdx , stream , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
		//		Mesh mesh = null;
		//
		//		DataInputStream oos = new DataInputStream(stream);
		//		int verAttr_size = oos.readInt();
		//		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>(
		//				verAttr_size);
		//		for (int i = 0; i < verAttr_size; i++) {
		//			attributes.add(new VertexAttribute(oos.readInt(), oos.readInt(),
		//					oos.readUTF()));
		//		}
		//		int singleVertexSize = calculateOffsets(attributes) / 4;
		//		int vertices_size = oos.readInt();
		//		Log.i("clock", "vertices_size:"+vertices_size);
		//		float vertices[] = new float[vertices_size];
		//		int index = 0;
		//		long start = System.currentTimeMillis();
		//		for (int i = 0; i < vertices_size; i++) {
		////			if(i == 0){
		////				Log.i("clock", "first:"+oos.readByte());
		////				Log.i("clock", "first:"+oos.readByte());
		////				Log.i("clock", "first:"+oos.readByte());
		////				Log.i("clock", "first:"+oos.readByte());
		////			}
		//			
		//			float vertex_v = oos.readFloat();
		//			if(i == 0){
		//				Log.i("clock", "float:"+vertex_v);
		//			}
		//			if (index == 0) {
		//				if (scaleX != 1f) {
		//					vertex_v = (float) vertex_v * (float) scaleX;
		//				}
		//				vertex_v += offsetX;
		//			} else if (index == 1) {
		//				if (scaleY != 1f) {
		//					vertex_v = (float) vertex_v * (float) scaleY;
		//				}
		//				vertex_v += offsetY;
		//			} else if (index == 2) {
		//				if (scaleZ != 1f) {
		//					vertex_v = (float) vertex_v * (float) scaleZ;
		//				}
		//				vertex_v += offsetZ;
		//			}
		//			vertices[i] = vertex_v;
		//			index++;
		//			if (index == singleVertexSize) {
		//				index = 0;
		//			}
		//		}
		//		Log.i("clock", "load mesh:"+(System.currentTimeMillis()-start));
		//		start = System.currentTimeMillis();
		//		int indices_size = oos.readInt();
		//		short indices[] = new short[indices_size];
		//		for (int i = 0; i < indices_size; i++) {
		//			indices[i] = oos.readShort();
		//		}
		//
		//		oos.close();
		//		stream.close();
		//		mesh = new Mesh(gdx, true, vertices.length, indices.length,
		//				attributes.toArray(new VertexAttribute[attributes.size()]));
		//		mesh.setVertices(vertices);
		//		mesh.setIndices(indices);
		//		return mesh;
	}
	
	public static Mesh loadCompressedMeshOpt(
			Gdx gdx ,
			InputStream stream ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ ) throws IOException
	{
		Mesh mesh = null;
		DataInputStream oos = new DataInputStream( stream );
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>( verAttr_size );
		for( int i = 0 ; i < verAttr_size ; i++ )
		{
			attributes.add( new VertexAttribute( oos.readInt() , oos.readInt() , oos.readUTF() ) );
		}
		int singleVertexSize = calculateOffsets( attributes ) / 4;
		//Log.v("loadCompressedMesh", "singleVertexSize:" + singleVertexSize);
		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
		long start = System.currentTimeMillis();
		byte[] buf = new byte[2048];
		int length = buf.length;
		int readCount = vertices_size * 4 / length;
		if( readCount <= 0 )
			length = vertices_size * 4;
		int read = 0;
		int num = -1;
		int pos = 0;
		int tmp = 0;
		int tmp2 = 0;
		while( read < readCount )
		{
			num = oos.read( buf , 0 , length );
			tmp = num / 4;
			for( tmp2 = 0 ; tmp2 < tmp ; tmp2++ )
			{
				vertices[pos] = getFloat( buf , tmp2 * 4 );
				pos++;
			}
			read++;
		}
		if( read == readCount )
		{
			length = vertices_size * 4 - length * readCount;
			num = oos.read( buf , 0 , length );
			tmp = num / 4;
			for( tmp2 = 0 ; tmp2 < tmp ; tmp2++ )
			{
				vertices[pos] = getFloat( buf , tmp2 * 4 );
				pos++;
			}
		}
		//Log.i("clock", "load mesh 1:"+(System.currentTimeMillis()-start));
		start = System.currentTimeMillis();
		for( int i = 0 ; i < vertices_size ; i += singleVertexSize )
		{
			vertices[i] = (float)vertices[i] * (float)scaleX;
			vertices[i] += offsetX;
			vertices[i + 1] = (float)vertices[i + 1] * (float)scaleY;
			vertices[i + 1] += offsetY;
			vertices[i + 2] = (float)vertices[i + 2] * (float)scaleZ;
			vertices[i + 2] += offsetZ;
		}
		//Log.i("clock", "load mesh 2:"+(System.currentTimeMillis()-start));
		start = System.currentTimeMillis();
		oos.read( buf , 0 , 4 );
		int indices_size = getInt( buf , 0 );
		short indices[] = new short[indices_size];
		length = buf.length;
		readCount = indices_size * 2 / length;
		if( readCount <= 0 )
			length = indices_size * 2;
		read = 0;
		num = -1;
		pos = 0;
		tmp = 0;
		tmp2 = 0;
		while( read <= readCount )
		{
			num = oos.read( buf , 0 , length );
			tmp = num / 2;
			for( tmp2 = 0 ; tmp2 < tmp ; tmp2++ )
			{
				indices[pos] = getShort( buf , tmp2 * 2 );
				pos++;
			}
			if( read == readCount - 1 )
			{
				length = indices_size * 2 - length * readCount;
			}
			read++;
		}
		oos.close();
		stream.close();
		mesh = new Mesh( gdx , true , vertices.length , indices.length , attributes.toArray( new VertexAttribute[attributes.size()] ) );
		mesh.setVertices( vertices );
		mesh.setIndices( indices );
		return mesh;
	}
	
	public static float getFloat(
			byte[] b ,
			int offset )
	{
		// 4 bytes
		int accum = 0;
		for( int shiftBy = 0 ; shiftBy < 4 ; shiftBy++ )
		{
			accum |= ( b[offset + shiftBy] & 0xff ) << ( 3 - shiftBy ) * 8;
		}
		return Float.intBitsToFloat( accum );
	}
	
	public static int getInt(
			byte[] b ,
			int offset )
	{
		// 4 bytes
		int accum = 0;
		for( int shiftBy = 0 ; shiftBy < 4 ; shiftBy++ )
		{
			accum |= ( b[offset + shiftBy] & 0xff ) << ( 3 - shiftBy ) * 8;
		}
		return accum;
	}
	
	public static short getShort(
			byte[] b ,
			int offset )
	{
		// 2 bytes
		short accum = 0;
		for( int shiftBy = 0 ; shiftBy < 2 ; shiftBy++ )
		{
			accum |= ( b[offset + shiftBy] & 0xff ) << ( 1 - shiftBy ) * 8;
		}
		return accum;
	}
	
	private static int calculateOffsets(
			ArrayList<VertexAttribute> attributes )
	{
		int count = 0;
		for( int i = 0 ; i < attributes.size() ; i++ )
		{
			VertexAttribute attribute = attributes.get( i );
			attribute.offset = count;
			if( attribute.usage == VertexAttributes.Usage.ColorPacked )
				count += 4;
			else
				count += 4 * attribute.numComponents;
		}
		return count;
	}
	
	public static Mesh loadMesh(
			Gdx gdx ,
			String fileName ) throws IOException
	{
		Mesh mesh = null;
		File file = new File( "/mnt/sdcard/" + fileName );
		file.createNewFile();
		FileInputStream fileInStream = new FileInputStream( file );
		DataInputStream oos = new DataInputStream( fileInStream );
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>();
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
		fileInStream.close();
		// mesh = new Mesh(gdx, true, vertices.length, indices.length,
		// attributes.toArray(new VertexAttribute[attributes.size()]));
		VertexAttributes att = new VertexAttributes( attributes.toArray( new VertexAttribute[attributes.size()] ) );
		mesh = new Mesh( gdx , true , vertices.length , indices.length , att );
		mesh.setVertices( vertices );
		mesh.setIndices( indices );
		return mesh;
	}
	
	public static Mesh loadCompressedMesh(
			MainAppContext appContext ,
			String objName ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		Mesh mesh = null;
		InputStream is = null;
		try
		{
			String objPath = ClockHelper.getLauncherThemeObjPath( appContext.mThemeName , objName );
			Log.v( "WidgetClock" , "objPath " + objPath );
			is = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
			if( is == null )
			{
				if( appContext.mWidgetContext.equals( appContext.mContainerContext ) )
				{
					is = WidgetThemeManager.getInstance().getSysteThemeInputStream( objPath );
					if( is == null )
					{
						objPath = ClockHelper.getLauncherThemeObjPath( "iLoong" , objName );
						is = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
						if( is == null )
						{
							is = WidgetThemeManager.getInstance().getSysteThemeInputStream( objPath );
						}
					}
				}
				else
				{
					objPath = ClockHelper.getThemeObjPath( appContext.mThemeName , objName );
					try
					{
						is = appContext.mWidgetContext.getAssets().open( objPath );
					}
					catch( Exception e )
					{
					}
					if( is == null )
					{
						objPath = ClockHelper.getLauncherThemeObjPath( "iLoong" , objName );
						is = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
						if( is == null )
						{
							objPath = ClockHelper.getThemeObjPath( "iLoong" , objName );
							is = appContext.mWidgetContext.getAssets().open( objPath );
						}
					}
				}
			}
			mesh = loadCompressedMesh( appContext.gdx , is , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
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
		return mesh;
	}
	
	public static void move(
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
	
	public static Mesh copyMesh(
			Mesh originalMesh ,
			MainAppContext appContext )
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
	
	public static String transformThemeName(
			Context widgetContext ,
			ComponentName themeComponentName )
	{
		if( themeComponentName == null )
		{
			return "iLoong";
		}
		String themeName = themeComponentName.getPackageName().substring( themeComponentName.getPackageName().lastIndexOf( "." ) + 1 );
		if( themeName == null || themeName.length() == 0 )
		{
			return "iLoong";
		}
		themeName = findThemeDir( widgetContext , themeName );
		return themeName;
	}
	
	public static String findThemeDir(
			Context widgetContext ,
			String themeName )
	{
		try
		{
			boolean foundTheme = false;
			String[] themeArray = widgetContext.getAssets().list( "" );
			for( String tmpTheme : themeArray )
			{
				if( tmpTheme.equals( themeName ) )
				{
					foundTheme = true;
					break;
				}
			}
			if( !foundTheme )
			{
				themeName = "iLoong";
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return themeName;
	}
	
	public static String getThemeImagePath(
			String themeName ,
			String imageName )
	{
		return themeName + "/image/" + imageName;
	}
	
	public static String getLauncherThemeImagePath(
			String themeName ,
			String imageName )
	{
		return "theme/widget/clock/" + WidgetClock.WidgetLable + "/image/" + imageName;
	}
	
	public static String getThemeObjPath(
			String themeName ,
			String objName )
	{
		if( !WidgetClock.loadOriginalObj )
		{
			return themeName + "/obj/" + objName;
		}
		else
		{
			return themeName + "/original_obj/" + objName;
		}
	}
	
	public static String getLauncherThemeObjPath(
			String themeName ,
			String objName )
	{
		if( !WidgetClock.loadOriginalObj )
		{
			return "theme/widget/clock/" + WidgetClock.WidgetLable + "/obj/" + objName;
		}
		else
		{
			return "theme/widget/clock/" + WidgetClock.WidgetLable + "/original_obj/" + objName;
		}
	}
	
	public static Mesh loadOriginalMesh(
			MainAppContext mainAppContext ,
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
		objPath = ClockHelper.getLauncherThemeObjPath( mainAppContext.mThemeName , objName );
		try
		{
			stream = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
			if( stream == null )
			{
				if( mainAppContext.mWidgetContext.equals( mainAppContext.mContainerContext ) )
				{
					stream = WidgetThemeManager.getInstance().getSysteThemeInputStream( objPath );
					if( stream == null )
					{
						objPath = ClockHelper.getLauncherThemeObjPath( "iLoong" , objName );
						stream = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
						if( stream == null )
						{
							stream = WidgetThemeManager.getInstance().getSysteThemeInputStream( objPath );
						}
					}
				}
				else
				{
					objPath = ClockHelper.getThemeObjPath( mainAppContext.mThemeName , objName );
					try
					{
						stream = mainAppContext.mWidgetContext.getAssets().open( objPath );
					}
					catch( Exception e )
					{
					}
					if( stream == null )
					{
						objPath = ClockHelper.getLauncherThemeObjPath( "iLoong" , objName );
						stream = WidgetThemeManager.getInstance().getCurrentThemeInputStream( objPath );
						if( stream == null )
						{
							objPath = ClockHelper.getThemeObjPath( "iLoong" , objName );
							stream = mainAppContext.mWidgetContext.getAssets().open( objPath );
						}
					}
				}
			}
			mesh = (Mesh)ObjLoader.loadObj( mainAppContext.gdx , stream , true , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
			if( WidgetClock.saveCompressObj )
			{
				ClockHelper.saveMesh( mesh , objName , -offsetX , -offsetY , -offsetZ , scaleX , scaleY , scaleZ );
			}
			stream.close();
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mesh;
	}
	
	public static Mesh getMesh(
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
		// String objPath = ClockHelper.getThemeObjPath(appContext.mThemeName,
		// objName);
		if( WidgetClock.loadOriginalObj )
		{
			if( WidgetClock.useCache )
			{
				Mesh mesh = meshCache.get( objName );
				if( mesh == null )
				{
					mesh = ClockHelper.loadOriginalMesh( appContext , objName , objName , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
					meshCache.put( objName , mesh );
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return ClockHelper.copyMesh( mesh , appContext );
			}
			else
			{
				return ClockHelper.loadOriginalMesh( appContext , objName , objName , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
			}
		}
		else
		{
			if( WidgetClock.useCache )
			{
				Mesh mesh = meshCache.get( objName );
				if( mesh == null )
				{
					mesh = ClockHelper.loadCompressedMesh( appContext , objName , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
					meshCache.put( objName , mesh );
				}
				// 如果使用缓存，返回值必须copy一份mesh，防止修改缓存中内容
				return ClockHelper.copyMesh( mesh , appContext );
			}
			else
			{
				return ClockHelper.loadCompressedMesh( appContext , objName , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
			}
		}
	}
}
