package com.iLoong.launcher.UI3DEngine.adapter;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;


public class Mesh extends com.badlogic.gdx.graphics.Mesh
{
	
	public Mesh(
			Gdx gdx ,
			boolean isStatic ,
			int maxVertices ,
			int maxIndices ,
			VertexAttribute[] attributes )
	{
		super( isStatic , maxVertices , maxIndices , attributes );
		// TODO Auto-generated constructor stub
	}
	
	public Mesh(
			Gdx gdx ,
			boolean isStatic ,
			int maxVertices ,
			int maxIndices ,
			VertexAttributes attributes )
	{
		super( isStatic , maxVertices , maxIndices , attributes );
		// TODO Auto-generated constructor stub
	}
	
	public Mesh(
			Gdx gdx ,
			VertexDataType type ,
			boolean isStatic ,
			int maxVertices ,
			int maxIndices ,
			VertexAttribute ... attributes )
	{
		super( type , isStatic , maxVertices , maxIndices , attributes );
		// TODO Auto-generated constructor stub
	}
}
