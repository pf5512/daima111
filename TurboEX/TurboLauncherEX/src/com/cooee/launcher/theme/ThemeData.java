package com.cooee.launcher.theme;

import java.util.ArrayList;

/**
 * 
 * @author zhongqihong
 * 
 *         ThemeData :a cache to keep some config data.
 * **/
public class ThemeData {

	public static ArrayList<DefaultIcon> defaultIcons = new ArrayList<DefaultIcon>();

	// God loves Zhongqihong@2014/12/19 ADD START
	// this is a repository of items in the Hotseat ,it is initialized on start
	// of launcher.
	public static ArrayList<FavoriteInfo> hotseatIcons = new ArrayList<FavoriteInfo>();
	// God loves Zhongqihong@2014/12/19 ADD END

	// this is a repository of items which are declared as virtual icons
	public static ArrayList<FavoriteInfo> virtualIcons = new ArrayList<FavoriteInfo>();

	public static float icon_size_scale;
	public static float icon_offset_left;
	public static float icon_offset_right;
	public static float icon_offset_top;

}
