package com.iLoong.launcher.pub;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iLoong.launcher.DesktopEdit.EffectIcon3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.umeng.analytics.MobclickAgent;


public class UmEventUtil
{
	
	public static void workspaceEffect(
			final Context context ,
			final String key ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
					String value = sp.getString( key , null );
					MobclickAgent.onEvent( context , key , value );
					EffectIcon3D.isDelayEvent = false;
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
	
	public static void applistEffect(
			final Context context ,
			final String key ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
					String value = sp.getString( key , null );
					MobclickAgent.onEvent( context , key , value );
					EffectPreview3D.isDelayEvent = false;
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
	
	public static void applistSort(
			final Context context ,
			final String key ,
			final String name ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					MobclickAgent.onEvent( context , key , name );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
	
	public static void wallpaperScroll(
			final Context context ,
			final String key ,
			final String name ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					MobclickAgent.onEvent( context , key , name );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
	
	public static void infiniteScroll(
			final Context context ,
			final String key ,
			final String name ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					MobclickAgent.onEvent( context , key , name );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
	
	public static void vibrator(
			final Context context ,
			final String key ,
			final String name ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					MobclickAgent.onEvent( context , key , name );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
	
	public static void newspage(
			final Context context ,
			final String key ,
			final int state ,
			final String name ,
			final long delayMillis )
	{
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( delayMillis );
					MobclickAgent.onEvent( context , key , name , state );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		} ).start();
	}
}
