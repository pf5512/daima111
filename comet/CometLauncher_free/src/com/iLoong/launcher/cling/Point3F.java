package com.iLoong.launcher.cling;


import android.util.FloatMath;


public class Point3F
{
	
	public float x;
	public float y;
	public float z;
	
	public Point3F()
	{
		set( 0 , 0 , 0 );
	}
	
	public Point3F(
			float x ,
			float y ,
			float z )
	{
		set( x , y , z );
	}
	
	public Point3F(
			Point3F p )
	{
		set( p );
	}
	
	public final void set(
			float x ,
			float y ,
			float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public final void set(
			Point3F p )
	{
		set( p.x , p.y , p.z );
	}
	
	public final void negate()
	{
		x = -x;
		y = -y;
		z = -z;
	}
	
	public final void offset(
			float dx ,
			float dy ,
			float dz )
	{
		x += dx;
		y += dy;
		z += dz;
	}
	
	public final boolean equals(
			float x ,
			float y ,
			float z )
	{
		return this.x == x && this.y == y && this.z == z;
	}
	
	public final float length()
	{
		return length( x , y , z );
	}
	
	public static float length(
			float x ,
			float y ,
			float z )
	{
		return FloatMath.sqrt( x * x + y * y + z * z );
	}
}
