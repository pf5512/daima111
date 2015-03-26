package com.cooee.searchbar.theme;


import java.util.HashMap;


public class WidgetAssetFile
{
	
	private static WidgetAssetFile instance = null;
	private HashMap<String , AssetFilePrefix> filePrefixHashMap = null;
	
	private WidgetAssetFile()
	{
		filePrefixHashMap = new HashMap<String , WidgetAssetFile.AssetFilePrefix>();
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
	}
	
	public static WidgetAssetFile getInstance()
	{
		if( instance == null )
		{
			instance = new WidgetAssetFile();
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
