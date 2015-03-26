package com.coco.theme.themebox.util;


public final class FunctionConfig
{
	
	public static boolean personal_center_internal = true;
	// 是否显示锁屏(无论是否安装锁屏)
	private static boolean displayLock = true;
	// 是否去掉网络不稳定提示
	private static boolean netPromptVisible = true;
	// 是否显示应用推荐功能
	private static boolean recommendVisible = true;
	// 是否显示锁屏
	private static boolean lockVisible = true;
	// 是否需要分享功能
	private static boolean shareVisible = true;
	// 是否需要loading界面
	private static boolean loadVisible = true;
	// 是否需要热门列表
	private static boolean hotThemeVisible = true;
	// 是否下载到手机内存
	private static boolean downToInternal = false;
	// 墙纸、字体是否显示
	private static boolean isWallpaperVisible = false;
	private static boolean isFontVisible = false;
	// 是否需要热门锁屏
	private static boolean hotLockVisible = true;
	private static String customWallpaperPath = "";
	// 顶部状态栏是否需要改变为透明显示
	private static boolean statusbar_translucent = false;
	private static String statusbar_lost_focus_action = "com.konka.action.STATUSBAR_OPAQUE";
	// 是否为doov样式（显示壁纸，字体，去除title，热门主题，热门锁屏）
	private static boolean isdoovStyle = false;
	// 锁屏中设置是否显示
	private static boolean isLockSetVisible = true;
	// 主题预览界面主题简介是否显示
	private static boolean isIntroductionVisible = true;
	//zte.com.cn.gallery3d;
	private static String galleryPkg = "com.google.android.apps.plus;com.google.android.gallery3d;com.miui.gallery;com.android.gallery;com.cooliris.media;com.htc.album;com.google.android.gallery3d;com.cooliris.media.Gallery;com.sonyericsson.album;com.android.gallery3d;com.sec.android.gallery3d";
	private static boolean isThemeMoreShow = true;
	// 特效界面是否显示
	private static boolean isEffectVisiable = false;
	private static String[] app_list_string;
	private static String[] workSpace_list_string;
	private static boolean isPriceVisible = true;
	private static boolean page_effect_no_radom_style = false;
	// 进入主题盒子，第一次是否显示loading界面
	private static boolean isLoadingShow = false;
	private static boolean isShowSceneTab = true;
	private static boolean isShowHotScene = true;
	private static boolean isShowHotWallpaper = true;
	private static boolean isShowWidgetTab = true;
	private static boolean isShowHotWidget = true;
	private static boolean isShowHotFont = true;
	private static boolean disable_set_wallpaper_dimensions = false;
	private static boolean isInternal = false;
	private static boolean isLiveWallpaperShow = true;
	private static boolean themeVisible = true;
	// 是否支持后台配置tab功能
	private static boolean enable_background_configuration_tab = true;
	// 是否支持自更新功能
	private static boolean enable_update_self = true;
	private static String tab_sequence = null;
	private static String tab_default_highlight = null;
	private static boolean isStatictoIcon = false;
	private static boolean lockwallpaperShow = false;
	private static String customLockWallpaperPath = null;
	private static boolean enable_topwise_style = false;
	private static boolean enable_tophard_style = false;
	private static boolean enable_manual_update = false;
	private static boolean net_version = false;
	private static int gridWidth = 120;
	private static int gridHeight = 200;
	private static boolean enable_add_widget = false;
	private static String wallpapers_from_other_apk = null;
	
	public static boolean isEnableUpdateself()
	{
		return enable_update_self;
	}
	
	public static void setEnableUpdateself(
			boolean enable_update_self )
	{
		FunctionConfig.enable_update_self = enable_update_self;
	}
	
	public static void setNetVersion(
			boolean net_version )
	{
		FunctionConfig.net_version = net_version;
	}
	
	public static boolean isNetVersion()
	{
		Log.v( "ThemeBox" , " FunctionConfig.net_version :" + FunctionConfig.net_version );
		return FunctionConfig.net_version;
	}
	
	public static boolean isInternal()
	{
		return isInternal;
	}
	
	public static void setInternal(
			boolean isInternal )
	{
		FunctionConfig.isInternal = isInternal;
	}
	
	public static void setStatusBarTranslucent(
			boolean bTranslucent ,
			String lost_focus_action )
	{
		statusbar_translucent = bTranslucent;
		statusbar_lost_focus_action = lost_focus_action;
	}
	
	public static boolean isStatusBarTranslucent()
	{
		return statusbar_translucent;
	}
	
	public static String getLostFocusAction()
	{
		return statusbar_lost_focus_action;
	}
	
	// 设置下载路径
	public static void setThemePath(
			String path )
	{
		com.coco.theme.themebox.StaticClass.set_directory_path = path;
	}
	
	public static void setDownToInternal(
			boolean visible )
	{
		downToInternal = visible;
	}
	
	public static boolean isDownToInternal()
	{
		return downToInternal;
	}
	
	public static void setThemeHotVisible(
			boolean visible )
	{
		hotThemeVisible = visible;
	}
	
	public static boolean isHotThemeVisible()
	{
		return hotThemeVisible;
	}
	
	public static void setLockVisible(
			boolean visible )
	{
		lockVisible = visible;
	}
	
	public static boolean isLockVisible()
	{
		return lockVisible;
	}
	
	public static void setShareVisible(
			boolean visible )
	{
		shareVisible = visible;
	}
	
	public static boolean isShareVisible()
	{
		return shareVisible;
	}
	
	public static void setLoadVisible(
			boolean visible )
	{
		loadVisible = visible;
	}
	
	public static boolean isLoadVisible()
	{
		return loadVisible;
	}
	
	public static void setRecommendVisible(
			boolean visible )
	{
		recommendVisible = visible;
	}
	
	public static boolean isRecommendVisible()
	{
		return recommendVisible;
	}
	
	public static void setPromptVisible(
			boolean visible )
	{
		netPromptVisible = visible;
	}
	
	public static boolean isPromptVisible()
	{
		return netPromptVisible;
	}
	
	public static boolean isWallpaperVisible()
	{
		return isWallpaperVisible;
	}
	
	public static void setWallpaperVisible(
			boolean visible )
	{
		FunctionConfig.isWallpaperVisible = visible;
	}
	
	public static boolean isHotLockVisible()
	{
		return hotLockVisible;
	}
	
	public static void setHotLockVisible(
			boolean hotLockVisible )
	{
		FunctionConfig.hotLockVisible = hotLockVisible;
	}
	
	public static boolean isFontVisible()
	{
		return isFontVisible;
	}
	
	public static void setFontVisible(
			boolean isFontVisible )
	{
		FunctionConfig.isFontVisible = isFontVisible;
	}
	
	public static void setDisplayLock(
			boolean visible )
	{
		displayLock = visible;
	}
	
	public static boolean isDisplayLock()
	{
		return displayLock;
	}
	
	// 获取launcher中壁纸的路径
	public static String getCustomWallpaperPath()
	{
		return customWallpaperPath;
	}
	
	public static void setCustomWallpaperPath(
			String customWallpaperPath )
	{
		FunctionConfig.customWallpaperPath = customWallpaperPath;
	}
	
	public static boolean isdoovStyle()
	{
		return isdoovStyle;
	}
	
	public static void setdoovStyle(
			boolean isdoovStyle )
	{
		FunctionConfig.isdoovStyle = isdoovStyle;
		if( isdoovStyle )
		{
			setThemeMoreShow( false );
		}
	}
	
	public static boolean isLockSetVisible()
	{
		return isLockSetVisible;
	}
	
	public static void setLockSetVisible(
			boolean isLockSetVisible )
	{
		FunctionConfig.isLockSetVisible = isLockSetVisible;
	}
	
	public static boolean isIntroductionVisible()
	{
		return isIntroductionVisible;
	}
	
	public static void setIntroductionVisible(
			boolean isIntroductionVisible )
	{
		FunctionConfig.isIntroductionVisible = isIntroductionVisible;
	}
	
	public static String getGalleryPkg()
	{
		return galleryPkg;
	}
	
	public static void setGalleryPkg(
			String galleryPkg )
	{
		if( galleryPkg != null && !galleryPkg.equals( "" ) )
			FunctionConfig.galleryPkg = galleryPkg;
	}
	
	public static boolean isThemeMoreShow()
	{
		return isThemeMoreShow;
	}
	
	public static void setThemeMoreShow(
			boolean isThemeMoreShow )
	{
		FunctionConfig.isThemeMoreShow = isThemeMoreShow;
	}
	
	public static boolean isEffectVisiable()
	{
		return isEffectVisiable;
	}
	
	public static void setEffectVisiable(
			boolean isEffectVisiable )
	{
		if( FunctionConfig.net_version )
			FunctionConfig.isEffectVisiable = false;
		else
			FunctionConfig.isEffectVisiable = isEffectVisiable;
	}
	
	public static boolean isPriceVisible()
	{
		return isPriceVisible;
	}
	
	public static void setPriceVisible(
			boolean isPriceVisible )
	{
		FunctionConfig.isPriceVisible = isPriceVisible;
	}
	
	public static String[] getAppliststring()
	{
		return app_list_string;
	}
	
	public static void setAppliststring(
			String[] app_list_string )
	{
		FunctionConfig.app_list_string = app_list_string;
	}
	
	public static String[] getWorkSpaceliststring()
	{
		return workSpace_list_string;
	}
	
	public static void setWorkSpaceliststring(
			String[] workSpace_list_string )
	{
		FunctionConfig.workSpace_list_string = workSpace_list_string;
	}
	
	public static boolean isPage_effect_no_radom_style()
	{
		return page_effect_no_radom_style;
	}
	
	public static void setPage_effect_no_radom_style(
			boolean page_effect_no_radom_style )
	{
		FunctionConfig.page_effect_no_radom_style = page_effect_no_radom_style;
	}
	
	public static boolean isLoadingShow()
	{
		return isLoadingShow;
	}
	
	public static void setLoadingShow(
			boolean isLoadingShow )
	{
		FunctionConfig.isLoadingShow = isLoadingShow;
	}
	
	public static boolean isShowSceneTab()
	{
		return isShowSceneTab;
	}
	
	public static void setShowSceneTab(
			boolean isShowSceneTab )
	{
		if( FunctionConfig.net_version )
			FunctionConfig.isShowSceneTab = false;
		else
			FunctionConfig.isShowSceneTab = isShowSceneTab;
	}
	
	public static boolean isShowHotScene()
	{
		return isShowHotScene;
	}
	
	public static void setShowHotScene(
			boolean isShowHotScene )
	{
		FunctionConfig.isShowHotScene = isShowHotScene;
	}
	
	public static boolean isShowHotWallpaper()
	{
		return isShowHotWallpaper;
	}
	
	public static void setShowHotWallpaper(
			boolean isShowHotWallpaper )
	{
		FunctionConfig.isShowHotWallpaper = isShowHotWallpaper;
	}
	
	public static boolean isShowWidgetTab()
	{
		return isShowWidgetTab;
	}
	
	public static void setShowWidgetTab(
			boolean isShowWidgetTab )
	{
		FunctionConfig.isShowWidgetTab = isShowWidgetTab;
	}
	
	public static boolean isShowHotWidget()
	{
		return isShowHotWidget;
	}
	
	public static void setShowHotWidget(
			boolean isShowHotWidget )
	{
		FunctionConfig.isShowHotWidget = isShowHotWidget;
	}
	
	public static boolean isShowHotFont()
	{
		return isShowHotFont;
	}
	
	public static void setShowHotFont(
			boolean isShowHotFont )
	{
		FunctionConfig.isShowHotFont = isShowHotFont;
	}
	
	public static void setDisableSetWallpaperDimensions(
			boolean disableSetWallpaperDimensions )
	{
		FunctionConfig.disable_set_wallpaper_dimensions = disableSetWallpaperDimensions;
	}
	
	public static boolean getDisableSetWallpaperDimensions()
	{
		return FunctionConfig.disable_set_wallpaper_dimensions;
	}
	
	public static String getCooeePayID(
			int price )
	{
		int p = price / 100;
		if( p < 10 )
		{
			return "U0" + p;
		}
		else
		{
			return "U" + p;
		}
	}
	
	public static String getSmsPurchasedPayID(
			int price )
	{
		String LEASE_PAYCODE = null;
		if( price > 0 )
		{
			if( price / 100 < 10 )
				LEASE_PAYCODE = "3000029163830" + price / 100;
			else if( price / 100 < 100 && price / 100 >= 10 )
				LEASE_PAYCODE = "300002916383" + price / 100;
		}
		return LEASE_PAYCODE;
	}
	
	public static boolean isLiveWallpaperShow()
	{
		return isLiveWallpaperShow;
	}
	
	public static void setLiveWallpaperShow(
			boolean isLiveWallpaperShow )
	{
		FunctionConfig.isLiveWallpaperShow = isLiveWallpaperShow;
	}
	
	public static boolean isEnable_background_configuration_tab()
	{
		return enable_background_configuration_tab;
	}
	
	public static void setEnable_background_configuration_tab(
			boolean enable_background_configuration_tab )
	{
		FunctionConfig.enable_background_configuration_tab = enable_background_configuration_tab;
	}
	
	public static boolean isThemeVisible()
	{
		return themeVisible;
	}
	
	public static void setThemeVisible(
			boolean themeVisible )
	{
		FunctionConfig.themeVisible = themeVisible;
	}
	
	public static String getTab_sequence()
	{
		return tab_sequence;
	}
	
	public static void setTab_sequence(
			String tab_sequence )
	{
		FunctionConfig.tab_sequence = tab_sequence;
	}
	
	public static String getTabdefaultHighlight()
	{
		return tab_default_highlight;
	}
	
	public static void setTabdefaultHighlight(
			String tab_default_highlight )
	{
		FunctionConfig.tab_default_highlight = tab_default_highlight;
	}
	
	public static boolean isStatictoIcon()
	{
		return isStatictoIcon;
	}
	
	public static void setStatictoIcon(
			boolean isStatictoIcon )
	{
		FunctionConfig.isStatictoIcon = isStatictoIcon;
	}
	
	public static boolean isLockwallpaperShow()
	{
		return lockwallpaperShow;
	}
	
	public static void setLockwallpaperShow(
			boolean lockwallpaperShow )
	{
		FunctionConfig.lockwallpaperShow = lockwallpaperShow;
	}
	
	public static String getCustomLockWallpaperPath()
	{
		return customLockWallpaperPath;
	}
	
	public static void setCustomLockWallpaperPath(
			String customLockWallpaperPath )
	{
		FunctionConfig.customLockWallpaperPath = customLockWallpaperPath;
	}
	
	public static boolean isEnable_topwise_style()
	{
		return enable_topwise_style;
	}
	
	public static void setEnable_topwise_style(
			boolean enable_topwise_style )
	{
		FunctionConfig.enable_topwise_style = enable_topwise_style;
	}
	
	public static boolean isEnable_tophard_style()
	{
		return enable_tophard_style;
	}
	
	public static void setEnable_tophard_style(
			boolean enable_tophard_style )
	{
		FunctionConfig.enable_tophard_style = enable_tophard_style;
	}
	
	public static boolean isEnable_manual_update()
	{
		return enable_manual_update;
	}
	
	public static void setEnable_manual_update(
			boolean enable_manual_update )
	{
		FunctionConfig.enable_manual_update = enable_manual_update;
	}
	
	public static int getGridWidth()
	{
		return gridWidth;
	}
	
	public static int getGridHeight()
	{
		return gridHeight;
	}
	
	public static boolean isEnable_add_widget()
	{
		return enable_add_widget;
	}
	
	public static void setEnable_add_widget(
			boolean enable_add_widget )
	{
		FunctionConfig.enable_add_widget = enable_add_widget;
	}
	
	public static String getWallpapers_from_other_apk()
	{
		return wallpapers_from_other_apk;
	}
	
	public static void setWallpapers_from_other_apk(
			String wallpapers_from_other_apk )
	{
		FunctionConfig.wallpapers_from_other_apk = wallpapers_from_other_apk;
	}
}
