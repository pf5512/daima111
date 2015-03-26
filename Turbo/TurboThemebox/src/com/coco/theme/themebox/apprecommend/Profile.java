package com.coco.theme.themebox.apprecommend;


/**
 * Profile类用于存放各种常酿
 */
public class Profile
{
	
	public static final String TABLE_NAME = "recommend";
	public static final String COLUMN_ITEMTYPE = "item_type";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME_CH = "name_ch";
	public static final String COLUMN_NAME_EN = "name_en";
	public static final String COLUMN_APK = "apk";
	public static final String COLUMN_ICON = "icon";
	public static final String COLUMN_PACKAGE = "package_name";
	public static final String COLUMN_URL_APK = "apk_url";
	public static final String COLUMN_URL_ICON = "icon_url";
	public static final String COLUMN_VERSION = "version";
	public static String AUTOHORITY = "com.coco.theme.themebox.apprecommend";
	// public static final int ITEM = 1;
	// public static final int ITEM_ID = 2;
	// 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头，
	// public static final String CONTENT_TYPE =
	// "vnd.android.cursor.dir/profile";
	// 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开墿 public static
	// final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/profile";
	// public static final Uri CONTENT_URI = Uri.parse("content://" + AUTOHORITY
	// + "/profile");
}
