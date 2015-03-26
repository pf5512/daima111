package com.iLoong.launcher.Desktop3D;


import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
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
	
	public static void sendCreateShareProgressDialogMsg()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_SHARE_PROGRESS_DIALOG , 0 , 0 );
	}
	
	public static void sendCancelShareProgressDialogMsg()
	{
		sendOurEventMsg( Messenger.MSG_CANCEL_SHARE_PROGRESS_DIALOG , 0 , 0 );
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
	
	public static void SendCometFolderRename(
			FolderIcon3D info )
	{
		Message msg = new Message();
		msg.what = Messenger.MSG_COMET_RENAME_FOLDER;
		msg.arg1 = 0;
		msg.arg2 = 0;
		msg.obj = info;
		launcher = iLoongLauncher.getInstance();
		launcher.mMainHandler.sendMessage( msg );
	}
	
	public static void sendDeletePageMsg()
	{
		sendOurEventMsg( Messenger.EVENT_DELETE_PAGE , 0 , 0 );
	}
	
	public static void sendShowWorkspaceMsg()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_WORKSPACE , 0 , 0 );
	}
	
	public static void sendShowWorkspaceMsgEx()
	{
		sendOurEventMsg( Messenger.MSG_SHOW_WORKSPACE_EX , 0 , 0 );
	}
	
	public static void sendHideWorkspaceMsg()
	{
		sendOurEventMsg( Messenger.MSG_HIDE_WORKSPACE , 0 , 0 );
	}
	
	public static void sendHideWorkspaceMsgEx()
	{
		sendOurEventMsg( Messenger.MSG_HIDE_WORKSPACE_EX , 0 , 0 );
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
	
	public static void sendReorderWorkspaceCellMsg(
			int oldIndex ,
			int newIndex )
	{
		sendOurEventMsg( Messenger.MSG_REORDER_WORKSPACE_CELL , oldIndex , newIndex );
	}
	
	public static void sendExchangeWorkspaceCellMsg(
			int minIndex ,
			int maxIndex )
	{
		sendOurEventMsg( Messenger.MSG_EXCHANGE_WORKSPACE_CELL , minIndex , maxIndex );
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
			int i )
	{
		sendOurEventMsg( Messenger.MSG_SHOW_SORT_DIALOG , i , 0 );
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
	
	public static void sendShowDeskEffectDialgMsg()
	{
		sendOurEventMsg( Messenger.MSG_CREAT_DESKTOP_EFFECT , 0 , 0 );
	}
	
	//xiatian start	//add 3 virtueIcon
	public static void sendSelectWallpaper()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_WALLPAPER , 0 , 0 );
	}
	
	public static void sendSelectZhuTi()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_ZHUTI , 0 , 0 );
	}
	
	public static void sendSelectZhuMianSheZhi()
	{
		sendOurEventMsg( Messenger.EVENT_SEETINGS_ZHUOMIAN , 0 , 0 );
	}
	
	public static void sendSelectUpgrade()
	{
		sendOurEventMsg( Messenger.MSG_EVENT_SELECT_UPGRADE , 0 , 0 );
	}
	
	//xiatian end
	//xiatian add start	//New Requirement 20130507
	public static void sendSelectChangJingZhuoMian()
	{
		sendOurEventMsg( Messenger.EVENT_SELECT_CHANGJINGZHUOMIAN , 0 , 0 );
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
}
