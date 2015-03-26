package com.iLoong.launcher.min3d;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class Vertex3D
{
	
	public float x;
	public float y;
	public float z;
	public float u;
	public float v;
	public float color;
	private static Vector3 _origin = new Vector3();
	private static Vector3 _temp = new Vector3();
	
	public Vertex3D()
	{
	}
	
	public static void setOrigin(
			Vector3 orig )
	{
		_origin = orig;
	}
	
	public void rotateX(
			float angle )
	{
		float cosRY = (float)Math.cos( angle * MathUtils.degreesToRadians );
		float sinRY = (float)Math.sin( angle * MathUtils.degreesToRadians );
		_temp.set( this.x - _origin.x , this.y - _origin.y , this.z - _origin.z );
		this.y = ( _temp.y * cosRY ) - ( _temp.z * sinRY ) + _origin.y;
		this.z = ( _temp.y * sinRY ) + ( _temp.z * cosRY ) + _origin.z;
	}
	
	public void rotateY(
			float angle )
	{
		float cosRY = (float)Math.cos( angle * MathUtils.degreesToRadians );
		float sinRY = (float)Math.sin( angle * MathUtils.degreesToRadians );
		_temp.set( this.x - _origin.x , this.y - _origin.y , this.z - _origin.z );
		this.x = ( _temp.x * cosRY ) + ( _temp.z * sinRY ) + _origin.x;
		this.z = ( _temp.x * -sinRY ) + ( _temp.z * cosRY ) + _origin.z;
	}
	
	public void rotateZ(
			float angle )
	{
		float cosRY = (float)Math.cos( angle * MathUtils.degreesToRadians );
		float sinRY = (float)Math.sin( angle * MathUtils.degreesToRadians );
		_temp.set( this.x - _origin.x , this.y - _origin.y , this.z - _origin.z );
		this.x = ( _temp.x * cosRY ) - ( _temp.y * sinRY ) + _origin.x;
		this.y = ( _temp.x * sinRY ) + ( _temp.y * cosRY ) + _origin.y;
	}
}
