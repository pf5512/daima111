/* Copyright (C) 2008 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.Widget3D;


import dalvik.system.DexClassLoader;


/**
 * Provides a simple {@link ClassLoader} implementation that operates on a list
 * of jar/apk files with classes.dex entries. The directory that holds the
 * optimized form of the files is specified explicitly. This can be used to
 * execute code not installed as part of an application.
 * 
 * The best place to put the optimized DEX files is in app-specific storage, so
 * that removal of the app will automatically remove the optimized DEX files. If
 * other storage is used (e.g. /sdcard), the app may not have an opportunity to
 * remove them.
 */
public class WidgetClassLoader extends DexClassLoader
{
	
	public WidgetClassLoader(
			String dexPath ,
			String dexOutputDir ,
			String libPath ,
			ClassLoader parent )
	{
		super( dexPath , dexOutputDir , libPath , parent );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Class<?> findClass(
			String name ) throws ClassNotFoundException
	{
		// TODO Auto-generated method stub
		//Log.e("findclass", "findclass 1 :" + name);
		return super.findClass( name );
	}
	
	@Override
	public Class<?> loadClass(
			String className ) throws ClassNotFoundException
	{
		// TODO Auto-generated method stub
		//Log.e("findclass", "loadClass: 1" + className);
		return super.loadClass( className );
	}
	
	@Override
	protected Class<?> loadClass(
			String className ,
			boolean resolve ) throws ClassNotFoundException
	{
		// TODO Auto-generated method stub
		//Log.e("findclass", "loadClass: 2" + className);
		return super.loadClass( className , resolve );
	}
	
	public Class<?> loadWidgetClass(
			String className )
	{
		try
		{
			// super.loadClass(className);
			Class<?> clazz = super.findClass( className );
			if( clazz == null )
			{
				return super.loadClass( className );
			}
			else
			{
				return clazz;
			}
		}
		catch( ClassNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
