package com.iLoong.launcher.UI3DEngine;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class Messenger
{
	
	private static Handler handler;
	public static final int CIRCLE_EVENT_DOWN = 0;
	public static final int CIRCLE_EVENT_UP = 1;
	public static final int CIRCLE_EVENT_DRAG = 2;
	public static final int EVENT_TOAST_USER = 3;
	public static final int EVENT_CREATE_POP_DIALOG = 4;
	public static final int CIRCLE_EVENT_TOAST = 5;
	public static final int EVENT_CREATE_PROG_DIALOG = 6;
	public static final int EVENT_CANCEL_PROG_DIALOG = 7;
	public static final int EVENT_UPDATE_SYS_WIDGET = 8;
	public static final int EVENT_ADD_SYS_WIDGET = 9;
	public static final int EVENT_ADD_SYS_SHORTCUT = 10;
	public static final int EVENT_DELETE_SYS_WIDGET = 11;
	public static final int EVENT_SELECT_SHORTCUT = 12;
	public static final int EVENT_WIDGET_GET_VIEW = 13;
	public static final int EVENT_CREATE_RENAME_FOLDER = 14;
	public static final int EVENT_START_ACTIVITY = 15;
	public static final int EVENT_DELETE_PAGE = 16;
	public static final int EVENT_LAUNCH_HOTBUTTON = 17;
	public static final int MSG_SHOW_WORKSPACE = 18;
	public static final int MSG_HIDE_WORKSPACE = 19;
	public static final int MSG_SCROLL_WORKSPACE = 20;
	public static final int MSG_ADD_WORKSPACE_CELL = 21;
	public static final int MSG_REMOVE_WORKSPACE_CELL = 22;
	public static final int MSG_VIBRATOR = 23;
	public static final int MSG_ON_BEGIN_OPEN_WIDGET = 24;
	public static final int MSG_ON_COMPLETE_CLOSE_WIDGET = 25;
	public static final int MSG_MOVE_WIDGET = 26;
	public static final int EVENT_DELETE_NOT_EMPTY_FOLDER = 27;
	public static final int EVENT_DOWNLOAD_WIDGET_DIALOG = 28;
	public static final int MSG_REFRESH_CLING_STATE = 29;
	public static final int MSG_HIDE_CLING_POINT = 30;
	public static final int MSG_SHOW_CLING_POINT = 31;
	public static final int MSG_SHOW_SORT_DIALOG = 32;
	public static final int MSG_SYS_HIDE_NOTICEBAR = 33;
	public static final int MSG_SYS_SHOW_NOTICEBAR = 34;
	public static final int EVENT_PICK_3DWIDGET = 35;
	public static final int MSG_START_COVER_MTKWIDGET = 36;
	public static final int MSG_STOP_COVER_MTKWIDGET = 37;
	public static final int MSG_MOVE_IN_MTKWIDGET = 38;
	public static final int MSG_MOVE_OUT_MTKWIDGET = 39;
	public static final int MSG_WAIT_CLING = 40;
	public static final int MSG_CANCEL_WAIT_CLING = 41;
	public static final int MSG_ADD_WIDGET = 42;
	public static final int MSG_ADD_SHORTCUT = 43;
	public static final int EVENT_CREATE_BLUETOOTH = 44;
	public static final int EVENT_CREATE_LOCKSCREEN = 45;
	public static final int MSG_WIDGET_FOCUS = 46;
	public static final int MSG_UPDATE_TEXTURE_DELAY = 47;
	public static final int MSG_RESET_TEXTURE_WRITE = 48;
	public static final int EVENT_CLICK_TO_WALLPAPER = 49;
	public static final int EVENT_CANNOT_FOUND_APK_DIALOG = 50;
	public static final int EVENT_CREATE_RESTART_DIALOG = 51;
	public static final int MSG_SYS_VIBRATOR = 52;
	public static final int MSG_SYS_PLAY_SOUND_EFFECT = 53;
	public static final int MSG_UPDATE_PACKAGE = 54;
	public static final int MSG_CHANGE_THREAD_PRIORITY = 55;
	//keypad start ,to release focus after leaving setmenu
	public static final int MSG_HIDE_WORKSAPCE_FOCES = 56;
	//keypad end
	public static final int EVENT_CREATE_EFFECT_DIALOG = 57;
	//xiatian start	//add 3 virtueIcon
	public static final int EVENT_SELECT_WALLPAPER = 58;
	public static final int EVENT_SELECT_ZHUTI = 59;
	public static final int EVENT_SEETINGS_ZHUOMIAN = 60;
	//xiatian end
	public static final int EVENT_START_ACTIVITY_FROM_INTENT = 61;
	public static final int EVENT_TOAST = 62;
	public static final int MSG_SYS_WIDGET_FOCUS = 63;
	public static final int EVENT_CHECK_SIZE = 64;
	public static final int MSG_SYS_WIDGET_UNFOCUS = 65;
	public static final int EVENT_START_ACTIVITY_INTENT = 66;
	public static final int EVENT_CHANGE_ICON_SCREEN_INDEX = 67;
	public static final int EVENT_ADD_APP_USE_FREQUENCY = 68;
	public static final int MSG_MOVE_IN_MTK3DWIDGET = 69;
	public static final int MSG_MOVE_OUT_MTK3DWIDGET = 70;
	public static final int MSG_HIDE_MAINMENU_WALLPAPER = 71;
	public static final int MSG_SHOW_MAINMENU_WALLPAPER = 72;
	public static final int EVENT_SELECT_CHANGJINGZHUOMIAN = 73; //xiatian add	//New Requirement 20130507
	public static final int MSG_SEARCH_REQUEST = 75;
	public static final int MSG_SYS_LIHGHT_CHANGE = 74;//添加场景桌面桌调节系统亮度
	public static final int MSG_SETUPMENU_HANDLE_ACTION = 75;
	public static final int MSG_SHOW_WORKSPACE_EX = 76;
	public static final int MSG_HIDE_WORKSPACE_EX = 77;
	public static final int MSG_CANCEL_SCENE_DIALOG = 78;
	public static final int MSG_ENTRY_THEME_MANAGER = 79;
	public static final int MSG_SHOW_MAINMENU_BG_DIALOG = 80;
	public static final int MSG_TOAST = 81;//xiatian add	//OperateFolder
	public static final int MSG_EVT_ADD_WIDGETS_DIALOG = 82; // teapotXu add
	public static final int EVENT_SELECTS_PERSONAL_ZITI = 83;
	public static final int EVENT_SELECTS_PERSONAL_TEXIAO = 84;
	public static final int EVENT_SELECTS_PERSONAL_LOCK = 85;
	public static final int MSG_UPDATE_TEXTURE_CIRCLE = 86; // sunyinwei add dynamicicon
	// alpha change message for status bar
	public static final int STATUSBAR_ALPHA_MSG = 87;// panchong add
	public static final int CHECK_RENDER_AFTER_RESUME = 88;//[dingzhi]fix no icon
	public static final int MSG_WRITE_RESUME_TIME = 89;
	public static final int MSG_FORCE_RESET_MAINMENU = 90;
	public static final int MSG_PICK_SYS_WIDGET_SHORTCUT = 91;
	//Jone add
	public static final int MSG_SHOW_SHARE_PROGRESS_DIALOG = 92;
	public static final int MSG_CANCEL_SHARE_PROGRESS_DIALOG = 93;
	public static final int MSG_SHAKE_WALLPAPER = 94;
	public static final int MSG_CREAT_DESKTOP_EFFECT = 95;
	public static final int MSG_NOT_FIND_SD_CARD = 96;
	//Jone end
	//xujin
	//添加news view
	public static final int MSG_INSTALL_NEWS_VIEW = 97;
	public static final int MSG_DO_NEWS_VIEW_AUTO_ANIM = 98;
	//移除news view
	public static final int MSG_REMOVE_NEWS_VIEW = 99;
	public static final int MSG_PAGEEDIT_ADD_WORKSPACE_CELL = 100;
	public static final int MSG_PAGEEDIT_REMOVE_WORKSPACE_CELL = 101;
	//关闭相机预览
	public static final int MSG_HIDE_CAMERA_PREVIEW = 102;
	public static final int MSG_HIDE_MENU_PREVIEW = 103;
	public static final int MSG_DOWNLOAD = 104;
	public static final int MSG_UNINSTALL_NEWS_VIEW = 105;
	/**
	 * 更换壁纸
	 */
	public static final int MSG_CHANGE_WALLPAPER = 106;
	/**
	 * 更换主题
	 */
	public static final int MSG_CHANGE_THEME = 107;
	public static final int EVENT_SELECT_HOT_WALLPAPER = 108;
	public static final int EVENT_SELECT_HOT_THEME = 109;
	public static final int EVENT_HIDE_STATUS_BAR = 110;
	public static final int EVENT_SHOW_STATUS_BAR = 111;
	public static final int MSG_SHOW_NEWS_AUTO = 112;
	public static final int MSG_REMOVE_NEWS_AUTO = 113;
	public static final int MSG_CHANGE_NEWS_HANDLE_POS = 114;
	public static final int MSG_CHANGE_UMEVENT_STATE = 115;
	public static final int MSG_HIDE_NEWSVIEW_HANDLE = 116;
	public static final int MSG_SHOW_NEWSVIEW_HANDLE = 117;
	public static final String TAG_DOWNLOAD_PACKAGE_TITLE = "Title";
	public static final String TAG_DOWNLOAD_PACKAGE_NAME = "PackageName";
	public static final String TAG_DOWNLOAD_PACKAGE_INSTALL_AFTER_DONWLOAD_COMPLETE = "IsInstallAfterDownloadComplete";
	public static final int MSG_SHOW_CUSTOM_DIALOG = 200;
	public static final int MSG_CANCEL_CUSTOM_DIALOG = 201;
	public static final int MSG_SHOW_DESKTOP_SETTING = 202;
	public static final int MSG_DELETE_MAIN_MENU_TIP = 203;
	//点击1X4扫帚启动闪电清理大师清理内存
	public static final int MSG_WIDGETClEAN4_CLEAN = 204;
	//点击1X1扫帚启动闪电清理大师清理内存
	public static final int MSG_WIDGETClEAN_CLEAN = 205;
	//点击清理条进入清理大师界面
	public static final int MSG_ENTER_MASTER_CLEAN = 206;
	public static final int MSG_CANCEL_LOADING_DIALOG = 207;
	//点击应用墙
	public static final int MSG_APPLICATION_WALL=208;
	public static void init(
			Handler _handler )
	{
		handler = _handler;
	}
	
	public static void sendMsg(
			int msgHeader ,
			Object obj ,
			float arg1 ,
			float arg2 )
	{
		Message msg = new Message();
		msg.what = msgHeader;
		msg.obj = obj;
		msg.arg1 = (int)arg1;
		msg.arg2 = (int)arg2;
		handler.sendMessage( msg );
	}
	
	public static void sendMsg(
			int msgHeader ,
			float arg1 ,
			float arg2 )
	{
		Message msg = new Message();
		msg.what = msgHeader;
		msg.arg1 = (int)arg1;
		msg.arg2 = (int)arg2;
		handler.sendMessage( msg );
	}
	
	public static void sendMsg(
			int msgHeader ,
			Object obj )
	{
		Message msg = new Message();
		msg.what = msgHeader;
		msg.obj = obj;
		handler.sendMessage( msg );
	}
	
	public static void sendMsgDelay(
			int msgHeader ,
			Object obj ,
			long delay )
	{
		Message msg = new Message();
		msg.what = msgHeader;
		msg.obj = obj;
		handler.sendMessageDelayed( msg , delay );
	}
	
	public static void removeMsg(
			int msgHeader )
	{
		handler.removeMessages( msgHeader );
	}
	
	public static void sendDownloadPackageMessage(
			String mTitle ,
			String mPackageName ,
			boolean mIsInstallAfterDownloadComplete )
	{
		Message msg = Message.obtain();
		msg.what = Messenger.MSG_DOWNLOAD;
		Bundle data = new Bundle();
		data.putString( TAG_DOWNLOAD_PACKAGE_TITLE , mTitle );
		data.putString( TAG_DOWNLOAD_PACKAGE_NAME , mPackageName );
		data.putBoolean( TAG_DOWNLOAD_PACKAGE_INSTALL_AFTER_DONWLOAD_COMPLETE , mIsInstallAfterDownloadComplete );
		msg.setData( data );
		handler.sendMessage( msg );
	}
}
