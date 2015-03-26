package com.iLoong.launcher.min3d;


import com.badlogic.gdx.graphics.Color;


public class Vertices3D
{
	
	private final int vertex_size = 6;
	private float[] vertices = null;
	private int m_size;
	private int m_max_size;
	
	public Vertices3D(
			int size )
	{
		m_size = 0;
		m_max_size = size;
		vertices = new float[size * vertex_size];
	}
	
	public int addVertex(
			float x ,
			float y ,
			float z )
	{
		return addVertex( x , y , z , 0 , 0 , Color.WHITE );
	}
	
	public int addVertex(
			float x ,
			float y ,
			float z ,
			Color color )
	{
		return addVertex( x , y , z , 0 , 0 , color );
	}
	
	public int addVertex(
			float x ,
			float y ,
			float z ,
			float u ,
			float v )
	{
		return addVertex( x , y , z , u , v , Color.WHITE );
	}
	
	public int addVertex(
			float x ,
			float y ,
			float z ,
			float u ,
			float v ,
			Color color )
	{
		if( m_size == m_max_size )
			return -1;
		int index = m_size * vertex_size;
		vertices[index++] = x;
		vertices[index++] = y;
		vertices[index++] = z;
		vertices[index++] = color.toFloatBits();
		vertices[index++] = u;
		vertices[index++] = v;
		m_size++;
		return m_size;
	}
	
	public int setVertex(
			int idx ,
			Vertex3D vtx )
	{
		if( idx < m_size )
		{
			int index = idx * vertex_size;
			vertices[index++] = vtx.x;
			vertices[index++] = vtx.y;
			vertices[index++] = vtx.z;
			vertices[index++] = vtx.color;
			vertices[index++] = vtx.u;
			vertices[index++] = vtx.v;
			return idx;
		}
		else
		{
			return -1;
		}
	}
	
	public void setColor(
			Color color )
	{
		int index = 3;
		for( int i = 0 ; i < m_size ; i++ )
		{
			vertices[index] = color.toFloatBits();
			index += vertex_size;
		}
	}
	
	public Vertex3D getVertex(
			int idx )
	{
		if( idx < m_size )
		{
			int index = idx * vertex_size;
			Vertex3D vtx = new Vertex3D();
			vtx.x = vertices[index++];
			vtx.y = vertices[index++];
			vtx.z = vertices[index++];
			vtx.color = vertices[index++];
			vtx.u = vertices[index++];
			vtx.v = vertices[index++];
			return vtx;
		}
		else
		{
			return null;
		}
	}
	
	public float[] getVertices()
	{
		return vertices;
	}
	
	public int getSize()
	{
		return m_size;
	}
}
