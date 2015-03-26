package com.iLoong.launcher.Desktop3D;


import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.desktop.iLoongLauncher.CustomDialogInfo;
import com.iLoong.launcher.widget.Widget;


public class SendMsgToAndroid
{
	
	private static iLoongLauncher launcher;
	
	public static void vibrator(
			int ms )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_VIBRATOR;
		msg.arg1 = ms;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sysVibrator()
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_SYS_VIBRATOR;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sysPlaySoundEffect()
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_SYS_PLAY_SOUND_EFFECT;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void launchHotButton()
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_LAUNCH_HOTBUTTON;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void startActivity(
			ItemInfo info )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_START_ACTIVITY;
		msg.obj = info;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void startActivity(
			Intent intent )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_START_ACTIVITY_FROM_INTENT;
		msg.obj = intent;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sceneLightChange(
			int brightNess )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_SYS_LIHGHT_CHANGE;
		msg.obj = brightNess;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void deleteSysWidget(
			Widget widget )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_DELETE_SYS_WIDGET;
		msg.obj = widget;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendSelectShortcutMsg(
			Intent intent )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_SELECT_SHORTCUT;
		msg.obj = intent;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendAddShortcutMsg(
			int x ,
			int y )
	{
		Message msg = new Message();
		msg.arg1 = x;
		msg.arg2 = y;
		msg.what = Messenger.EVENT_ADD_SYS_SHORTCUT;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendClickToWallPaperMsg(
			int x ,
			int y )
	{
		Message msg = new Message();
		msg.arg1 = x;
		msg.arg2 = y;
		msg.what = Messenger.EVENT_CLICK_TO_WALLPAPER;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendCreateBlueTooth()
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_CREATE_BLUETOOTH;
		//msg.obj = lock;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendCreateLockScreen()
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_CREATE_LOCKSCREEN;
		//msg.obj = lock;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendAddAppWidgetMsg(
			Widget widget )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_ADD_SYS_WIDGET;
		msg.arg1 = 0;
		msg.arg2 = 0;
		msg.obj = widget;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendWidgetFocusMsg(
			WidgetPluginView3D widget ,
			int state )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_WIDGET_FOCUS;
		msg.arg1 = state;
		msg.arg2 = 0;
		msg.obj = widget;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendOurToastMsg(
			String toastString )
	{
		Message msg = new Message();
		if( toastString != null )
		{
			Bundle b = new Bundle();
			b.putString( "toastString" , toastString );
			msg.setData( b );
		}
		msg.what = Messenger.EVENT_TOAST_USER;
		msg.arg1 = 0;
		msg.arg2 = 0;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendCircleToastMsg(
			String toastString )
	{
		Message msg = new Message();
		if( toastString != null )
		{
			Bundle b = new Bundle();
			b.putString( "toastString" , toastString );
			msg.setData( b );
		}
		msg.what = Messenger.CIRCLE_EVENT_TOAST;
		msg.arg1 = 0;
		msg.arg2 = 0;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendToastMsg(
			String toastString )
	{
		Message msg = new Message();
		if( toastString != null )
		{
			Bundle b = new Bundle();
			b.putString( "toastString" , toastString );
			msg.setData( b );
		}
		msg.what = Messenger.EVENT_TOAST;
		msg.arg1 = 0;
		msg.arg2 = 0;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendOurEventMsg(
			int msgHeader ,
			float x ,
			float y )
	{
		Message msg = new Message();
		msg.what = msgHeader;
		msg.arg1 = (int)x;
		msg.arg2 = (int)y;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendCreateProgressDialogMsg()
	{
		sendOurEventMsg( Messenger.EVENT_CREATE_PROG_DIALOG , 0 , 0 );
	}
	
	public static void sendCreatePopDialogMsg()
	{
		sendOurEventMsg( Messenger.EVENT_CREATE_POP_DIALOG , 0 , 0 );
	}
	
	public static void sendCreateRestartDialogMsg()
	{
		sendOurEventMsg( Messenger.EVENT_CREATE_RESTART_DIALOG , 0 , 0 );
	}
	
	public static void sendPopDeleteFolderDialogMsg()
	{
		sendOurEventMsg( Messenger.EVENT_DELETE_NOT_EMPTY_FOLDER , 0 , 0 );
	}
	
	public static void sendCannotFoundApkMsg()
	{
		sendOurEventMsg( Messenger.EVENT_CANNOT_FOUND_APK_DIALOG , 0 , 0 );
	}
	
	public static void sendDownloadWidgetMsg()
	{
		sendOurEventMsg( Messenger.EVENT_DOWNLOAD_WIDGET_DIALOG , 0 , 0 );
	}
	
	public static void sendRenameFolderMsg(
			FolderIcon3D info )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_CREATE_RENAME_FOLDER;
		msg.arg1 = 0;
		msg.arg2 = 0;
		msg.obj = info;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
		// sendOurEventMsg(EVENT_CREATE_RENAME_FOLDER, 0, 0);
	}
	
	public static void sendDeletePageMsg()
	{
		sendOurEventMsg( Messenger.EVENT_DELETE_PAGE , 0 , 0 );
	}
	
	public static void sendShowWorkspaceMsg()
	{
		sendShowWorkspaceMsgEx();
	}
	
	public static void sendShowWorkspaceMsgEx()
	{
		if( iLoongLauncher.getInstance().xWorkspace.getVisibility() != View.VISIBLE )
		{
			Log.i( "widget" , "sendShowWorkspaceMsgEx" );
			sendOurEventMsg( Messenger.MSG_SHOW_WORKSPACE_EX , 0 , 0 );
		}
	}
	
	public static void sendHideWorkspaceMsg()
	{
		sendHideWorkspaceMsgEx();
	}
	
	public static void sendHideWorkspaceMsgEx()
	{
		if( iLoongLauncher.getInstance().xWorkspace.getVisibility() == View.VISIBLE )
		{
			Log.i( "widget" , "sendHideWorkspaceMsgEx" );
			sendOurEventMsg( Messenger.MSG_HIDE_WORKSPACE_EX , 0 , 0 );
		}
	}
	
	public static void sendAddWorkspaceCellMsg(
			int i )
	{
		sendOurEventMsg( Messenger.MSG_ADD_WORKSPACE_CELL , i , 0 );
	}
	
	public static void sendRemoveWorkspaceCellMsg(
			int i )
	{
		sendOurEventMsg( Messenger.MSG_REMOVE_WORKSPACE_CELL , i , 0 );
	}
	
	public static void sendPageEditAddWorkspaceCellMsg(
			int i )
	{
		sendOurEventMsg( Messenger.MSG_PAGEEDIT_ADD_WORKSPACE_CELL , i , 0 );
	}
	
	public static void sendPageEditRemoveWorkspaceCellMsg(
			int i )
	{
		sendOurEventMsg( Messenger.MSG_PAGEEDIT_REMOVE_WORKSPACE_CELL , i , 0 );
	}
	
	public static void sendMoveWidgetMsg(
			View3D view ,
			int screen/* ,int y */)
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_MOVE_WIDGET;
		msg.arg1 = screen;
		// msg.arg2 = y;
		msg.obj = view;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void addWidgetFromAllApp(
			ComponentName name ,
			int x ,
			int y )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_ADD_WIDGET;
		msg.arg1 = x;
		msg.arg2 = y;
		msg.obj = name;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void PickWidgetAndShortCutFromAllApp(
			Intent data ,
			int type )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_PICK_SYS_WIDGET_SHORTCUT;
		msg.arg1 = type;
		msg.obj = data;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void addShortcutFromAllApp(
			ComponentName name ,
			int x ,
			int y )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_ADD_SHORTCUT;
		msg.arg1 = x;
		msg.arg2 = y;
		msg.obj = name;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendRefreshClingStateMsg()
	{
		sendOurEventMsg( Messenger.MSG_REFRESH_CLING_STATE , 0 , 0 );
	}
	
	public static void sendHideClingPointMsg()
	{
		sendOurEventMsg( Messenger.MSG_HIDE_CLING_POINT , 0 , 0 );
	}
	
	public static void sendShowClingPointMsg()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_CLING_POINT , 0 , 0 );
	}
	
	public static void sendWaitClingMsg()
	{
		sendOurEventMsg( Messenger.MSG_WAIT_CLING , 0 , 0 );
	}
	
	public static void sendCancelWaitClingMsg()
	{
		sendOurEventMsg( Messenger.MSG_CANCEL_WAIT_CLING , 0 , 0 );
	}
	
	public static void sendShowSortDialogMsg(
			int sortId ,
			int sortOrigin )
	{
		sendOurEventMsg( Messenger.MSG_SHOW_SORT_DIALOG , sortId , sortOrigin );
	}
	
	public static void sendShowNoticeMsg()
	{
		sendOurEventMsg( Messenger.MSG_SYS_SHOW_NOTICEBAR , 0 , 0 );
	}
	
	public static void sendHideNoticeMsg()
	{
		sendOurEventMsg( Messenger.MSG_SYS_HIDE_NOTICEBAR , 0 , 0 );
	}
	
	public static void sendStopCoverMTKWidgetMsg()
	{
		sendOurEventMsg( Messenger.MSG_STOP_COVER_MTKWIDGET , 0 , 0 );
	}
	
	public static void sendMoveInMTKWidgetMsg()
	{
		sendOurEventMsg( Messenger.MSG_MOVE_IN_MTKWIDGET , 0 , 0 );
	}
	
	public static void sendMoveOutMTKWidgetMsg()
	{
		sendOurEventMsg( Messenger.MSG_MOVE_OUT_MTKWIDGET , 0 , 0 );
	}
	
	public static void updateTextureAtlasDelay(
			long packDelay )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_UPDATE_TEXTURE_DELAY;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.MSG_UPDATE_TEXTURE_DELAY );
		launcher.mMainHandler.sendMessageDelayed( msg , packDelay );
	}
	
	public static void updateTextureForDynamicIcon(
			long packDelay )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_UPDATE_TEXTURE_CIRCLE;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.MSG_UPDATE_TEXTURE_CIRCLE );
		launcher.mMainHandler.sendMessageDelayed( msg , packDelay );
	}
	
	public static void resetTextureWriteDelay(
			long packDelay )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_RESET_TEXTURE_WRITE;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.MSG_RESET_TEXTURE_WRITE );
		launcher.mMainHandler.sendMessageDelayed( msg , packDelay );
	}
	
	public static void updatePackage()
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_UPDATE_PACKAGE;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.MSG_UPDATE_PACKAGE );
		launcher.mMainHandler.sendMessageDelayed( msg , 3000 );
	}
	
	public static void changeLoadThreadPriority()
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_CHANGE_THREAD_PRIORITY;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.MSG_CHANGE_THREAD_PRIORITY );
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendShowAppEffectDialogMsg()
	{
		sendOurEventMsg( Messenger.EVENT_CREATE_EFFECT_DIALOG , 0 , 0 );
	}
	
	//xiatian start	//add 3 virtueIcon
	public static void sendSelectWallpaper()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_WALLPAPER , 0 , 0 );
	}
	
	public static void sendSelectHotWallpaper()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_HOT_WALLPAPER , 0 , 0 );
	}
	
	public static void sendSelectZhuTi()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_ZHUTI , 0 , 0 );
	}
	
	public static void sendSelectHotZhuTi()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_HOT_THEME , 0 , 0 );
	}
	
	public static void sendSelectZhuMianSheZhi()
	{
		sendOurEventMsg( Messenger.EVENT_SEETINGS_ZHUOMIAN , 0 , 0 );
	}
	
	//xiatian end
	public static void sendSelectPersonalZiTi()
	{
		sendOurEventMsg( Messenger.EVENT_SELECTS_PERSONAL_ZITI , 0 , 0 );
	}
	
	public static void sendSelectPersonalLock()
	{
		sendOurEventMsg( Messenger.EVENT_SELECTS_PERSONAL_LOCK , 0 , 0 );
	}
	
	public static void sendSelectPersonalTeXiao()
	{
		sendOurEventMsg( Messenger.EVENT_SELECTS_PERSONAL_TEXIAO , 0 , 0 );
	}
	
	//xiatian add start	//New Requirement 20130507
	public static void sendSelectChangJingZhuoMian()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_CHANGJINGZHUOMIAN , 0 , 0 );
	}
	
	public static void senddesktopsettings()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_DESKTOP_SETTING , 0 , 0 );
	}
	
	//xiatian add end
	public static void sendCheckSizeMsg(
			int i )
	{
		Message msg = new Message();
		msg.what = Messenger.EVENT_CHECK_SIZE;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.EVENT_CHECK_SIZE );
		launcher.mMainHandler.sendMessageDelayed( msg , i );
	}
	
	//teapotXu add start
	public static void sendOpenAddWidgetsDialogMsg(
			int x ,
			int y )
	{
		sendOurEventMsg( Messenger.MSG_EVT_ADD_WIDGETS_DIALOG , x , y );
	}
	
	//teapotXu add end
	//xiatian add start	//mainmenu_background_alpha_progress
	public static void sendShowMainmenuBgDialogMsg()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_MAINMENU_BG_DIALOG , 0 , 0 );
	}
	
	//xiatian add end
	public static void sendForceResetMainmenuMessage(
			int i )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_FORCE_RESET_MAINMENU;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.removeMessages( Messenger.MSG_FORCE_RESET_MAINMENU );
		launcher.mMainHandler.sendMessageDelayed( msg , i );
	}
	
	//Jone add start
	public static void sendCreateShareProgressDialogMsg()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_SHARE_PROGRESS_DIALOG , 0 , 0 );
	}
	
	public static void sendCancelShareProgressDialogMsg()
	{
		sendOurEventMsg( Messenger.MSG_CANCEL_SHARE_PROGRESS_DIALOG , 0 , 0 );
	}
	
	public static void sendShakeWallpaper()
	{
		sendOurEventMsg( Messenger.MSG_SHAKE_WALLPAPER , 0 , 0 );
	}
	
	//Jone add end
	//xujin
	/**
	 * 隐藏camera preview
	 */
	public static void sendHidePreviewMessage()
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_HIDE_CAMERA_PREVIEW;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void changeWallpager(
			String displayname ,
			String pkgname )
	{
		Message msg = new Message();
		Bundle b = new Bundle();
		b.putString( "displayname" , displayname );
		b.putString( "pkgname" , pkgname );
		msg.setData( b );
		msg.what = Messenger.MSG_CHANGE_WALLPAPER;
		msg.arg1 = 0;
		msg.arg2 = 0;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void changeTheme(
			String pkgname ,
			String classname ,
			String displayname )
	{
		Message msg = new Message();
		if( pkgname != null && classname != null && displayname != null )
		{
			Bundle b = new Bundle();
			b.putString( "pkgname" , pkgname );
			b.putString( "classname" , classname );
			b.putString( "displayname" , displayname );
			msg.setData( b );
		}
		msg.what = Messenger.MSG_CHANGE_THEME;
		msg.arg1 = 0;
		msg.arg2 = 0;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void showCustomDialog(
			int x ,
			int y )
	{
		CustomDialogInfo dialog = new CustomDialogInfo();
		dialog.x = x;
		dialog.y = y;
		Message msg = new Message();
		msg.what = Messenger.MSG_SHOW_CUSTOM_DIALOG;
		msg.obj = dialog;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void cancelCustomDialog()
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_CANCEL_CUSTOM_DIALOG;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void cancelLoadingDialog(){
		Message msg = new Message();
		msg.what = Messenger.MSG_CANCEL_LOADING_DIALOG;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	public static void showStatusBar()
	{
		sendOurEventMsg( Messenger.EVENT_SHOW_STATUS_BAR , 0 , 0 );
	}
	
	public static void hideStatusBar()
	{
		sendOurEventMsg( Messenger.EVENT_HIDE_STATUS_BAR , 0 , 0 );
	}
}
