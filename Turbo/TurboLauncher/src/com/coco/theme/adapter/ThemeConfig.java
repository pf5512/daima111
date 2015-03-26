package com.coco.theme.adapter;


public class ThemeConfig
{
	
	public boolean include_theme_box = true;
	public String launcherApplyThemeAction = "com.coco.launcher.apply_theme";
	public String launcherRestartAction = "com.coco.launcher.restart";
	public String defaultThemePackageName;
	public String launcherPackageName;
	// 是否显示锁屏(无论是否安装锁屏)
	public boolean displayLock = true;
	// 是否去掉网络不稳定提示
	public boolean netPromptVisible = true;
	// 是否显示应用推荐功能
	public boolean recommendVisible = true;
	// 是否显示锁屏
	public boolean lockVisible = true;
	// 是否需要分享功能
	public boolean shareVisible = true;
	// 是否需要loading界面
	public boolean loadVisible = true;
	// 是否需要热门列表
	public boolean hotThemeVisible = true;
	// 是否下载到手机内存
	public boolean downToInternal = false;
	// 墙纸、字体是否显示
	public boolean isWallpaperVisible = false;
	public boolean isFontVisible = false;
	// 是否需要热门锁屏
	public boolean hotLockVisible = true;
	public String customWallpaperPath = "";
	// 顶部状态栏是否需要改变为透明显示
	public boolean statusbar_translucent = false;
	public String statusbar_lost_focus_action = "com.konka.action.STATUSBAR_OPAQUE";
	// 是否为doov样式（显示壁纸，字体，去除title，热门主题，热门锁屏）
	public boolean isdoovStyle = false;
	// 锁屏中设置是否显示
	public boolean isLockSetVisible = true;
	// 主题预览界面主题简介是否显示
	public boolean isIntroductionVisible = true;
	public String galleryPkg = "com.google.android.gallery3d;com.miui.gallery;com.android.gallery;com.cooliris.media;com.htc.album;com.google.android.gallery3d;com.cooliris.media.Gallery;com.sonyericsson.album;com.android.gallery3d;com.sec.android.gallery3d";
	public boolean isThemeMoreShow = true;
	// 特效界面是否显示
	public boolean isEffectVisiable = false;
	public String[] app_list_string;
	public String[] workSpace_list_string;
	public boolean isPriceVisible = false;
	public boolean page_effect_no_radom_style = false;
	// 进入主题盒子，第一次是否显示loading界面
	public boolean isLoadingShow = false;
	public boolean isShowStar = false;
	public String set_directory_path;
	public boolean disable_set_wallpaper_dimensions = false;
	
	public boolean isShowStar()
	{
		return isShowStar;
	}
	
	public void setShowStar(
			boolean isShowStar )
	{
		this.isShowStar = isShowStar;
	}
	
	public void setStatusBarTranslucent(
			boolean bTranslucent ,
			String lost_focus_action )
	{
		statusbar_translucent = bTranslucent;
		statusbar_lost_focus_action = lost_focus_action;
	}
	
	public boolean isStatusBarTranslucent()
	{
		return statusbar_translucent;
	}
	
	public String getLostFocusAction()
	{
		return statusbar_lost_focus_action;
	}
	
	// 设置下载路径
	public void setThemePath(
			String path )
	{
		this.set_directory_path = path;
		// com.coco.theme.themebox.Class.set_directory_path = path;
	}
	
	public void setDownToInternal(
			boolean visible )
	{
		downToInternal = visible;
	}
	
	public boolean isDownToInternal()
	{
		return downToInternal;
	}
	
	public void setThemeHotVisible(
			boolean visible )
	{
		hotThemeVisible = visible;
	}
	
	public boolean isHotThemeVisible()
	{
		return hotThemeVisible;
	}
	
	public void setLockVisible(
			boolean visible )
	{
		lockVisible = visible;
	}
	
	public boolean isLockVisible()
	{
		return lockVisible;
	}
	
	public void setShareVisible(
			boolean visible )
	{
		shareVisible = visible;
	}
	
	public boolean isShareVisible()
	{
		return shareVisible;
	}
	
	public void setLoadVisible(
			boolean visible )
	{
		loadVisible = visible;
	}
	
	public boolean isLoadVisible()
	{
		return loadVisible;
	}
	
	public void setRecommendVisible(
			boolean visible )
	{
		recommendVisible = visible;
	}
	
	public boolean isRecommendVisible()
	{
		return recommendVisible;
	}
	
	public void setPromptVisible(
			boolean visible )
	{
		netPromptVisible = visible;
	}
	
	public boolean isPromptVisible()
	{
		return netPromptVisible;
	}
	
	public boolean isWallpaperVisible()
	{
		return isWallpaperVisible;
	}
	
	public void setWallpaperVisible(
			boolean visible )
	{
		isWallpaperVisible = visible;
	}
	
	public boolean isHotLockVisible()
	{
		return hotLockVisible;
	}
	
	public void setHotLockVisible(
			boolean hotLockVisible )
	{
		this.hotLockVisible = hotLockVisible;
	}
	
	public boolean isFontVisible()
	{
		return isFontVisible;
	}
	
	public void setFontVisible(
			boolean isFontVisible )
	{
		this.isFontVisible = isFontVisible;
	}
	
	public void setDisplayLock(
			boolean visible )
	{
		this.displayLock = visible;
	}
	
	public boolean isDisplayLock()
	{
		return displayLock;
	}
	
	// 获取launcher中壁纸的路径
	public String getCustomWallpaperPath()
	{
		return customWallpaperPath;
	}
	
	public void setCustomWallpaperPath(
			String customWallpaperPath )
	{
		this.customWallpaperPath = customWallpaperPath;
	}
	
	public boolean isdoovStyle()
	{
		return isdoovStyle;
	}
	
	public void setdoovStyle(
			boolean isdoovStyle )
	{
		this.isdoovStyle = isdoovStyle;
		if( isdoovStyle )
		{
			setThemeMoreShow( false );
		}
	}
	
	public boolean isLockSetVisible()
	{
		return isLockSetVisible;
	}
	
	public void setLockSetVisible(
			boolean isLockSetVisible )
	{
		this.isLockSetVisible = isLockSetVisible;
	}
	
	public boolean isIntroductionVisible()
	{
		return isIntroductionVisible;
	}
	
	public void setIntroductionVisible(
			boolean isIntroductionVisible )
	{
		this.isIntroductionVisible = isIntroductionVisible;
	}
	
	public String getGalleryPkg()
	{
		return galleryPkg;
	}
	
	public void setGalleryPkg(
			String galleryPkg )
	{
		if( galleryPkg != null && !galleryPkg.equals( "" ) )
			this.galleryPkg = galleryPkg;
	}
	
	public boolean isThemeMoreShow()
	{
		return isThemeMoreShow;
	}
	
	public void setThemeMoreShow(
			boolean isThemeMoreShow )
	{
		this.isThemeMoreShow = isThemeMoreShow;
	}
	
	public boolean isEffectVisiable()
	{
		return isEffectVisiable;
	}
	
	public void setEffectVisiable(
			boolean isEffectVisiable )
	{
		this.isEffectVisiable = isEffectVisiable;
	}
	
	public boolean isPriceVisible()
	{
		return isPriceVisible;
	}
	
	public void setPriceVisible(
			boolean isPriceVisible )
	{
		this.isPriceVisible = isPriceVisible;
	}
	
	public String[] getAppliststring()
	{
		return app_list_string;
	}
	
	public void setAppliststring(
			String[] app_list_string )
	{
		this.app_list_string = app_list_string;
	}
	
	public String[] getWorkSpaceliststring()
	{
		return workSpace_list_string;
	}
	
	public void setWorkSpaceliststring(
			String[] workSpace_list_string )
	{
		this.workSpace_list_string = workSpace_list_string;
	}
	
	public boolean isPage_effect_no_radom_style()
	{
		return page_effect_no_radom_style;
	}
	
	public void setPage_effect_no_radom_style(
			boolean page_effect_no_radom_style )
	{
		this.page_effect_no_radom_style = page_effect_no_radom_style;
	}
	
	public boolean isLoadingShow()
	{
		return isLoadingShow;
	}
	
	public void setLoadingShow(
			boolean isLoadingShow )
	{
		this.isLoadingShow = isLoadingShow;
	}
	
	public void setDisableSetWallpaperDimensions(
			boolean disableSetWallpaperDimensions )
	{
		this.disable_set_wallpaper_dimensions = disableSetWallpaperDimensions;
	}
}
