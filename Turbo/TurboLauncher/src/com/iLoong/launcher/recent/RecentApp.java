package com.iLoong.launcher.recent;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class RecentApp extends Icon3D
{
	
	public final static int EVENT_MSG_ID_DESTORY = 0;
	private final String TAG = "RecentApp";
	private static final String SCHEME = "package";
	/** 
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本) 
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/** 
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2) 
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/** 
	* InstalledAppDetails所在包名 
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/** 
	 * InstalledAppDetails类名 
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	
	/** 
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level 
	* 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。 
	 *  
	 * @param context 
	 *  
	 * @param packageName 
	 *            应用程序的包名 
	*/
	public RecentApp(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	public RecentApp(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name , bmp , title );
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( RecentAppPage.mMainTimeline != null || RecentAppPage.mSlaveTimeLine != null )
			return true;
		if( pointer == 0 )
		{
			pointer = 2;
			RecentAppHolder.fireupApp = this;
			Workspace3D.getInstance().mRecentApplications.destory();
			return true;
		}
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		String pkgname = info.intent.getComponent().getPackageName();
		if( pkgname.equalsIgnoreCase( "coco.bizhi" ) )
		{
			SendMsgToAndroid.sendSelectWallpaper();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhutixuanze" ) || pkgname.equalsIgnoreCase( "coco.uicenter" ) )
		{
			SendMsgToAndroid.sendSelectZhuTi();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhuomianshezhi" ) )
		{
			SendMsgToAndroid.sendSelectZhuMianSheZhi();
			return true;
		}
		//teapotXu add start 
		else if( pkgname.equalsIgnoreCase( "coco.texiao" ) )
		{
			SendMsgToAndroid.sendSelectPersonalTeXiao();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.ziti" ) )
		{
			SendMsgToAndroid.sendSelectPersonalZiTi();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.lock" ) )
		{
			SendMsgToAndroid.sendSelectPersonalLock();
			return true;
		}
		//teapotXu add end
		//xiatian add start	//New Requirement 20130507
		else if( pkgname.equalsIgnoreCase( "coco.scene" ) )
		{
			SendMsgToAndroid.sendSelectChangJingZhuoMian();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.desktopsettings" ) )
		{
			SendMsgToAndroid.senddesktopsettings();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.pingmuyulan" ) )
		{
			Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_PAGE_SHOW_EDIT );
			return true;
		}
		onLongkeyEvent();
		//viewParent.onCtrlEvent( this , EVENT_MSG_ID_DESTORY );
		return true;
	}
	
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( RecentAppPage.onMove )
		{
			return super.scroll( x , y , deltaX , deltaY );
		}
		if( ( deltaX == 0 || Math.abs( deltaY ) / Math.abs( deltaX ) > 1 ) )
		{
			if( !this.isDragging && !RecentAppPage.isdragged )
			{
				Workspace3D.getInstance().mRecentApplications.setTipIconVisible( true );
				RecentAppPage.isdragged = true;
				this.toAbsoluteCoords( point );
				this.setTag( new Vector2( point.x , point.y ) );
				point.x = x;
				point.y = y;
				this.toAbsolute( point );
				DragLayer3D.dragStartX = point.x;
				DragLayer3D.dragStartY = point.y;
				viewParent.onCtrlEvent( this , MSG_ICON_LONGCLICK );
			}
			return true;
		}
		return true;
	}
	
	public static void showInstalledAppDetails(
			Context context ,
			String packageName )
	{
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if( apiLevel >= 9 )
		{ // 2.3（ApiLevel 9）以上，使用SDK提供的接口  
			intent.setAction( "android.settings.APPLICATION_DETAILS_SETTINGS" );
			Uri uri = Uri.fromParts( SCHEME , packageName , null );
			intent.setData( uri );
		}
		else
		{ // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）  
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。  
			final String appPkgName = ( apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21 );
			intent.setAction( Intent.ACTION_VIEW );
			intent.setClassName( APP_DETAILS_PACKAGE_NAME , APP_DETAILS_CLASS_NAME );
			intent.putExtra( appPkgName , packageName );
		}
		context.startActivity( intent );
	}
	
	public void onLongkeyEvent()
	{
		String packageName = ( (ShortcutInfo)this.getItemInfo() ).intent.getComponent().getPackageName().toString();
		Context context = iLoongLauncher.getInstance();
		showInstalledAppDetails( context , packageName );
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	int pointer = 0;//0:第一次点击 关闭动画，1 onclick点击，2，onlongclick点击
	
	public boolean onClick(
			float x ,
			float y )
	{
		if( pointer == 0 )
		{
			pointer = 1;
			RecentAppHolder.fireupApp = this;
			Workspace3D.getInstance().mRecentApplications.destory();
			return true;
		}
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		String pkgname = info.intent.getComponent().getPackageName();
		if( pkgname.equalsIgnoreCase( "coco.bizhi" ) )
		{
			SendMsgToAndroid.sendSelectWallpaper();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhutixuanze" ) || pkgname.equalsIgnoreCase( "coco.uicenter" ) )
		{
			SendMsgToAndroid.sendSelectZhuTi();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhuomianshezhi" ) )
		{
			SendMsgToAndroid.sendSelectZhuMianSheZhi();
			return true;
		}
		//teapotXu add start 
		else if( pkgname.equalsIgnoreCase( "coco.texiao" ) )
		{
			SendMsgToAndroid.sendSelectPersonalTeXiao();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.ziti" ) )
		{
			SendMsgToAndroid.sendSelectPersonalZiTi();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.lock" ) )
		{
			SendMsgToAndroid.sendSelectPersonalLock();
			return true;
		}
		//teapotXu add end
		//xiatian add start	//New Requirement 20130507
		else if( pkgname.equalsIgnoreCase( "coco.scene" ) )
		{
			SendMsgToAndroid.sendSelectChangJingZhuoMian();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.desktopsettings" ) )
		{
			SendMsgToAndroid.senddesktopsettings();
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.pingmuyulan" ) )
		{
			Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_PAGE_SHOW_EDIT );
			return true;
		}
		return super.onClick( x , y );
	}
}
