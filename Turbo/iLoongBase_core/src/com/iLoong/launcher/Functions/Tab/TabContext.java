package com.iLoong.launcher.Functions.Tab;


import java.util.HashMap;

import android.content.Context;

import com.badlogic.gdx.backends.android.AndroidApplication;


public class TabContext
{
	
	public Context mContainerContext;
	public Context mWidgetContext;
	public AndroidApplication mGdxApplication;
	public HashMap<String , Object> paramsMap = new HashMap<String , Object>();
	
	public void addParam(
			String key ,
			Object value )
	{
		paramsMap.put( key , value );
	}
}
