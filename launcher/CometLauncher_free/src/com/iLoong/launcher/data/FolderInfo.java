package com.iLoong.launcher.data;


/**
 * Represents a folder containing shortcuts or apps.
 */
public abstract class FolderInfo extends ItemInfo
{
	
	/**
	 * Whether this folder has been opened
	 */
	public static final String FOLDER_FROM_NEW = "create_new_folder";
	public boolean opened;
	/**
	 * The folder name.
	 */
	public CharSequence title;
	public String folderFrom = "";
	//teapotXu add start for Folder in MainMenu
	public long lastUpdateTime = 0;
	public int use_frequency = 0;
	// this folder name is not real name, it will be used when sort apps in Mainmenu
	public CharSequence sort_folder_name;
	//teapotXu add end for Folder in MainMenu
}
