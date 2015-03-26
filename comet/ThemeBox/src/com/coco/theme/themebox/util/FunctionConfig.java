package com.coco.theme.themebox.util;

public final class FunctionConfig {

	//是否显示应用推荐功能
	private static boolean recommendVisible = true;
	
	//是否显示锁屏
	private static boolean lockVisible = true;
	
	//是否需要分享功能
	private static boolean shareVisible = true;

	// 是否需要loading界面
	private static boolean loadVisible = true;
	
	//是否需要热门列表
	private static boolean hotThemeVisible = true;
	
	//是否下载到手机内存
	private static boolean downToInternal = false;
	
	public static void setDownToInternal(boolean visible){
		downToInternal = visible;
	}
	
	public static boolean isDownToInternal(){
		return downToInternal;
	}
	
	public static void setThemeHotVisible(boolean visible){
		hotThemeVisible = visible;
	}
	
	public static boolean isHotThemeVisible(){
		return hotThemeVisible;
	}
	
	public static void setLockVisible(boolean visible) {
		lockVisible = visible;
	}
	
	public static boolean isLockVisible() {
		return lockVisible;
	}
	
	public static void setShareVisible(boolean visible) {
		shareVisible = visible;
	}
	
	public static boolean isShareVisible() {
		return shareVisible;
	}
	
	public static void setLoadVisible(boolean visible) {
		loadVisible = visible;
	}
	
	public static boolean isLoadVisible() {
		return loadVisible;
	}
	
	public static void setRecommendVisible(boolean visible) {
		recommendVisible = visible;
	}
	
	public static boolean isRecommendVisible() {
		return recommendVisible;
	}
}
