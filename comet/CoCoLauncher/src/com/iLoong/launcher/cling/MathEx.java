package com.iLoong.launcher.cling;

import android.graphics.Point;
import android.graphics.PointF;

public abstract class MathEx 
{
	public static boolean isFloatEqual(float value1, float value2)
	{
		return (Math.abs(value1 - value2) < 0.01);
	}
	
	public static boolean isFloatZero(float value)
	{
		return (isFloatEqual(value, 0));
	}
	
	public static boolean isPointFSame(PointF p1, PointF p2)
	{
		return (isFloatEqual(p1.x, p2.x) && isFloatEqual(p1.y, p2.y));
	}
	
	public static PointF getCenter(PointF pt1, PointF pt2)
	{
		PointF ptCenter = new PointF((pt1.x+pt2.x)/2, (pt1.y+pt2.y)/2);
		return ptCenter;
	}
	
	public static Point getCenter(Point pt1, Point pt2)
	{
		Point ptCenter = new Point((pt1.x+pt2.x)/2, (pt1.y+pt2.y)/2);
		return ptCenter;		
	}
}
