package com.coco.scene.scenebox;


import android.content.Context;

import com.coco.download.Assets;
import com.coco.pub.provider.PubProviderHelper;


class SceneConfig
{
	
	public String scene;
}

public class SceneDB
{
	
	public static String LAUNCHER_PACKAGENAME = "com.cool.launcher";
	public static String default_theme_package_name = null;
	Context mContext;
	
	public SceneDB(
			Context context )
	{
		mContext = context;
	}
	
	public SceneConfig getScene()
	{
		SceneConfig conf = new SceneConfig();
		String scene = Assets.getScene( mContext , "scenepkg" );
		if( scene == null || scene.trim().length() == 0 )
		{
			scene = SceneDB.default_theme_package_name;
		}
		conf.scene = scene;
		return conf;
	}
	
	public void saveScene(
			String conf )
	{
		PubProviderHelper.addOrUpdateValue( "scene" , "scenepkg" , conf );
	}
}
