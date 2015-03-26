package com.iLoong.launcher.Widget3D;


import java.util.HashMap;


public class Cache<K , V>
{
	
	private final HashMap<K , V> mLruMap;
	
	public Cache(
			final int capacity )
	{
		mLruMap = new HashMap<K , V>();
	}
	
	public synchronized V put(
			K key ,
			V value )
	{
		// cleanUpWeakMap();
		mLruMap.put( key , value );
		return value;
	}
	
	public synchronized V get(
			K key )
	{
		// cleanUpWeakMap();
		return mLruMap.get( key );
	}
	
	public synchronized void clear()
	{
		mLruMap.clear();
	}
}
