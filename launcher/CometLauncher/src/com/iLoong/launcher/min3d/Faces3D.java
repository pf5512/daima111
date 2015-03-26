package com.iLoong.launcher.min3d;


public class Faces3D
{
	
	private short[] m_indices = null;
	private int m_size;
	private int m_max_size;
	
	public Faces3D(
			int size )
	{
		m_size = 0;
		m_max_size = size;
		m_indices = new short[size * 3];
	}
	
	public int addFace(
			short index1 ,
			short index2 ,
			short index3 )
	{
		if( m_size == m_max_size )
			return -1;
		int index = m_size * 3;
		m_indices[index++] = index1;
		m_indices[index++] = index2;
		m_indices[index++] = index3;
		m_size++;
		return m_size;
	}
	
	public int addFace(
			int index1 ,
			int index2 ,
			int index3 )
	{
		return addFace( (short)index1 , (short)index2 , (short)index3 );
	}
	
	public short[] getIndices()
	{
		return m_indices;
	}
	
	public int getSize()
	{
		return m_size;
	}
}
