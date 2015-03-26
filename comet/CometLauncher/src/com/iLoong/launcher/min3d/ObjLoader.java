/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 ******************************************************************************/
package com.iLoong.launcher.min3d;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.iLoong.launcher.UI3DEngine.adapter.Mesh;


/**
 * Loads Wavefront OBJ files, ignores material files.
 * 
 * @author mzechner
 */
public class ObjLoader
{
	
	/**
	 * Loads a Wavefront OBJ file from the given input stream.
	 * 
	 * @param in
	 *            the InputStream
	 */
	public static Mesh loadObj(
			InputStream in )
	{
		return loadObj( in , false , 0 , 0 , 0 , 1f , 1f , 1f );
	}
	
	/**
	 * Loads a Wavefront OBJ file from the given input stream.
	 * 
	 * @param in
	 *            the InputStream
	 * @param flipV
	 *            whether to flip the v texture coordinate or not
	 */
	public static Mesh loadObj(
			InputStream in ,
			boolean flipV )
	{
		// String line = "";
		//
		// try {
		// BufferedReader reader = new BufferedReader(
		// new InputStreamReader(in));
		// StringBuffer b = new StringBuffer();
		// String l = reader.readLine();
		// while (l != null) {
		// b.append(l);
		// b.append("\n");
		// l = reader.readLine();
		// }
		//
		// line = b.toString();
		// reader.close();
		// } catch (Exception ex) {
		// return null;
		// }
		// return loadObjFromString(line, flipV);
		return loadObj( in , flipV , 0 , 0 , 0 , 1f , 1f , 1f );
	}
	
	public static Mesh loadObj(
			InputStream in ,
			boolean flipV ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		String line = "";
		try
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			StringBuffer b = new StringBuffer();
			String l = reader.readLine();
			while( l != null )
			{
				b.append( l );
				b.append( "\n" );
				l = reader.readLine();
			}
			line = b.toString();
			reader.close();
		}
		catch( Exception ex )
		{
			return null;
		}
		return loadObjFromString( line , flipV , false , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
	}
	
	// public static Mesh loadObj(Gdx gdx, InputStream in, boolean flipV) {
	// return loadObj(gdx, in, flipV, 0f, 0f, 0f);
	// }
	public static Mesh loadObj(
			Gdx gdx ,
			InputStream in ,
			boolean flipV ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		return loadObj( in , flipV , offsetX , offsetY , offsetZ , scaleX , scaleY , scaleZ );
	}
	
	/**
	 * Loads a Wavefront OBJ file from the given input stream.
	 * 
	 * @param in
	 *            the InputStream
	 * @param flipV
	 *            whether to flip the v texture coordinate or not
	 */
	public static Mesh loadObj(
			InputStream in ,
			boolean flipV ,
			boolean useIndices )
	{
		String line = "";
		try
		{
			BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
			StringBuffer b = new StringBuffer();
			String l = reader.readLine();
			while( l != null )
			{
				b.append( l );
				b.append( "\n" );
				l = reader.readLine();
			}
			line = b.toString();
			reader.close();
		}
		catch( Exception ex )
		{
			return null;
		}
		return loadObjFromString( line , flipV , useIndices , 0 , 0 , 0 , 1f , 1f , 1f );
	}
	
	public static Mesh loadObj(
			Gdx gdx ,
			InputStream in ,
			boolean flipV )
	{
		return loadObj( in , flipV , false );
	}
	
	public static Mesh loadObj(
			Gdx gdx ,
			InputStream in ,
			boolean flipV ,
			boolean useIndices )
	{
		return loadObj( in , flipV , useIndices );
	}
	
	/**
	 * Loads a mesh from the given string in Wavefront OBJ format
	 * 
	 * @param obj
	 *            The string
	 * @return The Mesh
	 */
	public static Mesh loadObjFromString(
			String obj )
	{
		return loadObjFromString( obj , false , 0 , 0 , 0 );
	}
	
	/**
	 * Loads a mesh from the given string in Wavefront OBJ format
	 * 
	 * @param obj
	 *            The string
	 * @param flipV
	 *            whether to flip the v texture coordinate or not
	 * @return The Mesh
	 */
	// public static Mesh loadObjFromString(String obj, boolean flipV) {
	// return loadObjFromString(obj, flipV, 0, 0, 0);
	// }
	public static Mesh loadObjFromString(
			String obj ,
			boolean flipV ,
			float offsetX ,
			float offsetY ,
			float offsetZ )
	{
		return loadObjFromString( obj , flipV , false , offsetX , offsetY , offsetZ , 1f , 1f , 1f );
	}
	
	/**
	 * Loads a mesh from the given string in Wavefront OBJ format
	 * 
	 * @param obj
	 *            The string
	 * @param flipV
	 *            whether to flip the v texture coordinate or not
	 * @param useIndices
	 *            whether to create an array of indices or not
	 * @return The Mesh
	 */
	public static Mesh loadObjFromString(
			String obj ,
			boolean flipV ,
			boolean useIndices ,
			float offsetX ,
			float offsetY ,
			float offsetZ ,
			float scaleX ,
			float scaleY ,
			float scaleZ )
	{
		String[] lines = obj.split( "\n" );
		float[] vertices = new float[lines.length * 3];
		float[] normals = new float[lines.length * 3];
		float[] uv = new float[lines.length * 3];
		int numVertices = 0;
		int numNormals = 0;
		int numUV = 0;
		int numFaces = 0;
		int[] facesVerts = new int[lines.length * 3];
		int[] facesNormals = new int[lines.length * 3];
		int[] facesUV = new int[lines.length * 3];
		int vertexIndex = 0;
		int normalIndex = 0;
		int uvIndex = 0;
		int faceIndex = 0;
		Color default_color = new Color( 1.0f , 1.0f , 1.0f , 1.0f );
		for( int i = 0 ; i < lines.length ; i++ )
		{
			String line = lines[i];
			if( line.startsWith( "v " ) )
			{
				String[] tokens = line.split( "[ ]+" );
				float vertex_x = Float.parseFloat( tokens[1] );
				float vertex_y = Float.parseFloat( tokens[2] );
				float vertex_z = Float.parseFloat( tokens[3] );
				if( scaleX != 1f )
				{
					vertex_x = vertex_x * scaleX;
				}
				if( scaleY != 1f )
				{
					vertex_y = vertex_y * scaleY;
				}
				if( scaleZ != 1f )
				{
					vertex_z = vertex_z * scaleZ;
				}
				vertices[vertexIndex] = vertex_x + offsetX;
				vertices[vertexIndex + 1] = vertex_y + offsetY;
				vertices[vertexIndex + 2] = vertex_z + offsetZ;
				vertexIndex += 3;
				numVertices++;
				continue;
			}
			if( line.startsWith( "vn " ) )
			{
				String[] tokens = line.split( "[ ]+" );
				normals[normalIndex] = Float.parseFloat( tokens[1] );
				normals[normalIndex + 1] = Float.parseFloat( tokens[2] );
				normals[normalIndex + 2] = Float.parseFloat( tokens[3] );
				normalIndex += 3;
				numNormals++;
				continue;
			}
			if( line.startsWith( "vt" ) )
			{
				String[] tokens = line.split( "[ ]+" );
				uv[uvIndex] = Float.parseFloat( tokens[1] );
				uv[uvIndex + 1] = flipV ? 1 - Float.parseFloat( tokens[2] ) : Float.parseFloat( tokens[2] );
				uvIndex += 2;
				numUV++;
				continue;
			}
			if( line.startsWith( "f " ) )
			{
				String[] tokens = line.split( "[ ]+" );
				if( tokens.length == 5 )
				{
					String[] parts = tokens[1].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					parts = tokens[2].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					parts = tokens[4].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					numFaces++;
					parts = tokens[2].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					parts = tokens[3].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					parts = tokens[4].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					numFaces++;
				}
				else
				{
					String[] parts = tokens[1].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					parts = tokens[2].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					parts = tokens[3].split( "/" );
					facesVerts[faceIndex] = getIndex( parts[0] , numVertices );
					if( parts.length > 2 )
						facesNormals[faceIndex] = getIndex( parts[2] , numNormals );
					if( parts.length > 1 )
						facesUV[faceIndex] = getIndex( parts[1] , numUV );
					faceIndex++;
					numFaces++;
				}
				continue;
			}
		}
		Mesh mesh = null;
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>();
		attributes.add( new VertexAttribute( Usage.Position , 3 , ShaderProgram.POSITION_ATTRIBUTE ) );
		attributes.add( new VertexAttribute( Usage.ColorPacked , 4 , ShaderProgram.COLOR_ATTRIBUTE ) );
		if( numNormals > 0 )
			attributes.add( new VertexAttribute( Usage.Normal , 3 , ShaderProgram.NORMAL_ATTRIBUTE ) );
		if( numUV > 0 )
			attributes.add( new VertexAttribute( Usage.TextureCoordinates , 2 , ShaderProgram.TEXCOORD_ATTRIBUTE + "0" ) );
		if( useIndices )
		{
			int attrCount = 4 + ( numNormals > 0 ? 3 : 0 ) + ( numUV > 0 ? 2 : 0 );
			int normOffset = 4;
			int uvOffset = 4 + ( numNormals > 0 ? 3 : 0 );
			float verts[] = new float[numVertices * attrCount];
			for( int i = 0 ; i < numVertices ; i++ )
			{
				verts[i * attrCount] = vertices[i * 3];
				verts[i * attrCount + 1] = vertices[i * 3 + 1];
				verts[i * attrCount + 2] = vertices[i * 3 + 2];
				verts[i * attrCount + 3] = default_color.toFloatBits();
			}
			for( int i = 0 ; i < numFaces * 3 ; i++ )
			{
				int vertexIdx = facesVerts[i];
				if( numNormals > 0 )
				{
					int normalIdx = facesNormals[i] * 3;
					verts[vertexIdx * attrCount + normOffset] = normals[normalIdx];
					verts[vertexIdx * attrCount + normOffset + 1] = normals[normalIdx + 1];
					verts[vertexIdx * attrCount + normOffset + 2] = normals[normalIdx + 2];
				}
				if( numUV > 0 )
				{
					int uvIdx = facesUV[i] * 2;
					verts[vertexIdx * attrCount + uvOffset] = uv[uvIdx];
					verts[vertexIdx * attrCount + uvOffset + 1] = uv[uvIdx + 1];
				}
			}
			short[] indices = new short[numFaces * 3];
			for( int i = 0 ; i < indices.length ; i++ )
				indices[i] = (short)facesVerts[i];
			mesh = new Mesh( null , true , verts.length , indices.length , attributes.toArray( new VertexAttribute[attributes.size()] ) );
			mesh.setVertices( verts );
			mesh.setIndices( indices );
		}
		else
		{
			float[] verts = new float[( numFaces * 3 ) * ( 4 + ( numNormals > 0 ? 3 : 0 ) + ( numUV > 0 ? 2 : 0 ) )];
			for( int i = 0 , vi = 0 ; i < numFaces * 3 ; i++ )
			{
				int vertexIdx = facesVerts[i] * 3;
				verts[vi++] = vertices[vertexIdx];
				verts[vi++] = vertices[vertexIdx + 1];
				verts[vi++] = vertices[vertexIdx + 2];
				verts[vi++] = default_color.toFloatBits();
				if( numNormals > 0 )
				{
					int normalIdx = facesNormals[i] * 3;
					verts[vi++] = normals[normalIdx];
					verts[vi++] = normals[normalIdx + 1];
					verts[vi++] = normals[normalIdx + 2];
				}
				if( numUV > 0 )
				{
					int uvIdx = facesUV[i] * 2;
					verts[vi++] = uv[uvIdx];
					verts[vi++] = uv[uvIdx + 1];
				}
			}
			mesh = new Mesh( null , true , numFaces * 3 , 0 , attributes.toArray( new VertexAttribute[attributes.size()] ) );
			mesh.setVertices( verts );
		}
		return mesh;
	}
	
	private static int getIndex(
			String index ,
			int size )
	{
		if( index == null || index.length() == 0 )
			return 0;
		int idx = Integer.parseInt( index );
		if( idx < 0 )
			return size + idx;
		else
			return idx - 1;
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
		Mesh mesh = null;
		DataInputStream oos = new DataInputStream( stream );
		int verAttr_size = oos.readInt();
		ArrayList<VertexAttribute> attributes = new ArrayList<VertexAttribute>( verAttr_size );
		for( int i = 0 ; i < verAttr_size ; i++ )
		{
			attributes.add( new VertexAttribute( oos.readInt() , oos.readInt() , oos.readUTF() ) );
		}
		int singleVertexSize = calculateOffsets( attributes ) / 4;
		int vertices_size = oos.readInt();
		float vertices[] = new float[vertices_size];
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
		for( int i = 0 ; i < vertices_size ; i += singleVertexSize )
		{
			vertices[i] = (float)vertices[i] * (float)scaleX;
			vertices[i] += offsetX;
			vertices[i + 1] = (float)vertices[i + 1] * (float)scaleY;
			vertices[i + 1] += offsetY;
			vertices[i + 2] = (float)vertices[i + 2] * (float)scaleZ;
			vertices[i + 2] += offsetZ;
		}
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
}
