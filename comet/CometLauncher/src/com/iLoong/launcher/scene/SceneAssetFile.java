// wanghongjian add whole file //enable_DefaultScene
package com.iLoong.launcher.scene;


import java.util.HashMap;


public class SceneAssetFile
{
	
	private static SceneAssetFile instance = null;
	private HashMap<String , SceneAssetFilePrefix> filePrefixHashMap = null;
	
	private SceneAssetFile()
	{
		filePrefixHashMap = new HashMap<String , SceneAssetFile.SceneAssetFilePrefix>();
		SceneAssetFilePrefix subFile = new SceneAssetFilePrefix();
		subFile.prefix = "theme";
		subFile.needAdapt = true;
		subFile.loadFromTheme = true;
		subFile.needLoadLauncherIsNotFound = true;
		filePrefixHashMap.put( "theme" , subFile );
		subFile = new SceneAssetFilePrefix();
		subFile.prefix = "launcher";
		subFile.needAdapt = true;
		subFile.loadFromTheme = false;
		subFile.needLoadLauncherIsNotFound = false;
		filePrefixHashMap.put( subFile.prefix , subFile );
	}
	
	public static SceneAssetFile getInstance()
	{
		if( instance == null )
		{
			instance = new SceneAssetFile();
		}
		return instance;
	}
	
	public SceneAssetFilePrefix getAssetSubFile(
			String filePrefix )
	{
		SceneAssetFilePrefix subFile = filePrefixHashMap.get( filePrefix );
		if( subFile == null )
		{
			subFile = new SceneAssetFilePrefix();
			subFile.prefix = filePrefix;
			subFile.needAdapt = false;
			subFile.loadFromTheme = false;
			subFile.needLoadLauncherIsNotFound = false;
		}
		return subFile;
	}
	
	public static class SceneAssetFilePrefix
	{
		
		public String prefix;
		public boolean needAdapt;
		public boolean loadFromTheme;
		public boolean needLoadLauncherIsNotFound;
	}
}
