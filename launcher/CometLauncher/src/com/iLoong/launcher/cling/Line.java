package com.iLoong.launcher.cling;


import android.graphics.PointF;


public class Line
{
	
	public PointF dirVector = new PointF( 1 , 0 );
	public PointF fixPoint = new PointF( 0 , 0 );
	
	public Line()
	{
	}
	
	public Line(
			PointF dirVector ,
			PointF fixPoint )
	{
		makeLineByDirVectorAndFixPoint( dirVector , fixPoint );
	}
	
	public boolean makeLineByDirVectorAndFixPoint(
			PointF dirVector ,
			PointF fixPoint )
	{
		if( dirVector.length() == 0 )
		{
			return false;
		}
		float vectorLen = dirVector.length();
		this.dirVector.set( dirVector.x / vectorLen , dirVector.y / vectorLen );
		this.fixPoint.set( fixPoint );
		return true;
	}
	
	public boolean makeLineByTwoDifferentPoint(
			PointF p1 ,
			PointF p2 )
	{
		if( p1.equals( p2 ) )
		{
			return false;
		}
		PointF vector = new PointF( p2.x - p1.x , p2.y - p1.y );
		makeLineByDirVectorAndFixPoint( vector , p1 );
		return true;
	}
	
	public boolean isParallel(
			Line other )
	{
		return MathEx.isPointFSame( dirVector , other.dirVector );
	}
	
	public boolean equals(
			Line other )
	{
		if( !isParallel( other ) )
		{
			return false;
		}
		if( fixPoint.equals( other.fixPoint ) )
		{
			return true;
		}
		return( MathEx.isFloatEqual( dirVector.x * ( other.fixPoint.y - fixPoint.y ) , dirVector.y * ( other.fixPoint.x - fixPoint.x ) ) );
	}
	
	public PointF getCross(
			Line other )
	{
		PointF pt = new PointF( 0 , 0 );
		if( isParallel( other ) )
		{
			pt.set( Float.POSITIVE_INFINITY , Float.POSITIVE_INFINITY );
		}
		if( MathEx.isFloatEqual( dirVector.x , 0 ) )
		{
			pt.x = fixPoint.x;
			pt.y = other.dirVector.y / other.dirVector.x * ( pt.x - other.fixPoint.x ) + other.fixPoint.y;
		}
		else if( MathEx.isFloatEqual( other.dirVector.x , 0 ) )
		{
			pt.x = other.fixPoint.x;
			pt.y = dirVector.y / dirVector.x * ( pt.x - fixPoint.x ) + fixPoint.y;
		}
		else
		{
			float a1 = dirVector.y / dirVector.x;
			float b1 = fixPoint.y - a1 * fixPoint.x;
			float a2 = other.dirVector.y / other.dirVector.x;
			float b2 = other.fixPoint.y - a2 * other.fixPoint.x;
			pt.x = ( b2 - b1 ) / ( a1 - a2 );
			pt.y = a1 * pt.x + b1;
		}
		return pt;
	}
	
	public float distanceFromPoint(
			PointF pt )
	{
		float distance = 0.0f;
		if( MathEx.isFloatEqual( dirVector.x , 0 ) )
		{
			distance = Math.abs( pt.x - fixPoint.x );
		}
		else if( MathEx.isFloatEqual( dirVector.y , 0 ) )
		{
			distance = Math.abs( pt.y - fixPoint.y );
		}
		else
		{
			Line tmpLine = new Line();
			PointF tmpVector = new PointF( dirVector.y , -dirVector.x );
			tmpLine.makeLineByDirVectorAndFixPoint( tmpVector , pt );
			PointF crossPoint = getCross( tmpLine );
			crossPoint.offset( -pt.x , -pt.y );
			distance = crossPoint.length();
		}
		return distance;
	}
}
