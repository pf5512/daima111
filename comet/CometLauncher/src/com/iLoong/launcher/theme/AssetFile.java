package com.iLoong.launcher.theme;


import java.util.HashMap;


public class AssetFile
{
	
	private static AssetFile instance = null;
	private HashMap<String , AssetFilePrefix> filePrefixHashMap = null;
	
	private AssetFile()
	{
		filePrefixHashMap = new HashMap<String , AssetFile.AssetFilePrefix>();
		AssetFilePrefix subFile = new AssetFilePrefix();
		subFile.prefix = "theme";
		subFile.needAdapt = true;
		subFile.loadFromTheme = true;
		subFile.needLoadLauncherIsNotFound = true;
		filePrefixHashMap.put( "theme" , subFile );
		subFile = new AssetFilePrefix();
		subFile.prefix = "launcher";
		subFile.needAdapt = true;
		subFile.loadFromTheme = false;
		subFile.needLoadLauncherIsNotFound = false;
		filePrefixHashMap.put( subFile.prefix , subFile );
		//teapotXu add start: Add "theme/iconbg", and this assertFilePrefix's needLoadLauncherIsNotFound is false
		subFile = new AssetFilePrefix();
		subFile.prefix = "theme/iconbg";
		subFile.needAdapt = true;
		subFile.loadFromTheme = true;
		subFile.needLoadLauncherIsNotFound = false;
		filePrefixHashMap.put( subFile.prefix , subFile );
		//teapotXu add end
		//xiatian add start	//Mainmenu Bg	//load bg image only in SystemTheme
		subFile = new AssetFilePrefix();
		subFile.prefix = "theme/applist_bg";
		subFile.needAdapt = true;
		subFile.loadFromTheme = false;
		subFile.needLoadLauncherIsNotFound = false;
		filePrefixHashMap.put( subFile.prefix , subFile );
		//xiatian add end
	}
	
	public static AssetFile getInstance()
	{
		if( instance == null )
		{
			instance = new AssetFile();
		}
		return instance;
	}
	
	public AssetFilePrefix getAssetSubFile(
			String filePrefix )
	{
		AssetFilePrefix subFile = filePrefixHashMap.get( filePrefix );
		if( subFile == null )
		{
			subFile = new AssetFilePrefix();
			subFile.prefix = filePrefix;
			subFile.needAdapt = false;
			subFile.loadFromTheme = false;
			subFile.needLoadLauncherIsNotFound = false;
		}
		return subFile;
	}
	
	public static class AssetFilePrefix
	{
		
		public String prefix;
		public boolean needAdapt;
		public boolean loadFromTheme;
		public boolean needLoadLauncherIsNotFound;
	}
}
