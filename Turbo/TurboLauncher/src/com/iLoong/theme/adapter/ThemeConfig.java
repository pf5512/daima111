package com.iLoong.theme.adapter;


public class ThemeConfig
{
	
	public boolean include_theme_box = true;
	public String launcherApplyThemeAction = "com.coco.launcher.apply_theme";
	public String launcherRestartAction = "com.coco.launcher.restart";
	public String defaultThemePackageName;
	public String launcherPackageName;
	public String customWallpaperPath = "";
	public boolean disable_set_wallpaper_dimensions = false;
	// 是否为doov样式（显示壁纸，字体，去除title，热门主题，热门锁屏）
	public boolean isdoovStyle = false;
	public String galleryPkg = "com.google.android.gallery3d;com.miui.gallery;com.android.gallery;com.cooliris.media;com.htc.album;com.google.android.gallery3d;com.cooliris.media.Gallery;com.sonyericsson.album;com.android.gallery3d;com.sec.android.gallery3d";
	// 特效界面是否显示
	public boolean isEffectVisiable = false;
	public String[] app_list_string;
	public String[] workSpace_list_string;
	public boolean page_effect_no_radom_style = false;
	public String set_directory_path;
	public boolean personal_center_internal = false;
	public boolean net_version = true;
	
	public void setGalleryPkg(
			String galleryPkg )
	{
		if( galleryPkg != null && !galleryPkg.equals( "" ) )
			this.galleryPkg = galleryPkg;
	}
}
