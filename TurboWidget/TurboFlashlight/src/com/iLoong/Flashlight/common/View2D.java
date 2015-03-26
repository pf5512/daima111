package com.iLoong.Flashlight.common;


import android.graphics.Bitmap;


public class View2D
{
	
	private Bitmap bitmap;
	private float x;
	private float y;
	private float width;
	private float height;
	
	public View2D()
	{
	}
	
	public View2D(
			Bitmap bitmap )
	{
		this.bitmap = bitmap;
	}
	
	public View2D(
			Bitmap bitmap ,
			float x ,
			float y )
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}
	
	public View2D(
			Bitmap bitmap ,
			float x ,
			float y ,
			float width ,
			float height )
	{
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}
	
	public void setBitmap(
			Bitmap bitmap )
	{
		this.bitmap = bitmap;
	}
	
	public float getX()
	{
		return x;
	}
	
	public void setX(
			float x )
	{
		this.x = x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public void setY(
			float y )
	{
		this.y = y;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public void setWidth(
			float width )
	{
		this.width = width;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public void setHeight(
			float height )
	{
		this.height = height;
	}
}
