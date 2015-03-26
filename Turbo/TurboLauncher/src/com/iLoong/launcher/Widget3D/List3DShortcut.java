package com.iLoong.launcher.Widget3D;


import android.graphics.Color;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;
import com.umeng.analytics.MobclickAgent;


public class List3DShortcut extends Widget3DShortcut
{
	
	public List3DShortcut(
			String name )
	{
		super( name );
		scale = SetupMenu.mScale;
	}
	
	public void makeShortcut()
	{
		if( titleRegion == null || previewRegion == null )
		{
			title = R3D.widget_shortcut_title;
			int titleWidth = (int)( width * 3 / 4 );
			int alignH = AppBar3D.TEXT_ALIGN_LEFT;
			int titleHeight = (int)( R3D.widget_preview_title_weight * height );
			if( !RR.net_version )
			{
				titleWidth = (int)( width );
				alignH = AppBar3D.TEXT_ALIGN_CENTER;
				titleHeight = MIUIWidgetList.getSingleLineHeight();
			}
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) ) );
			preview = null;
			if( RR.net_version )
				preview = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/launcher_shortcut.png" );
			else
				preview = ThemeManager.getInstance().getBitmap( "theme/miui_source/launcher_shortcut.png" );
			previewRegion = new TextureRegion( new BitmapTexture( preview ) );
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		SendMsgToAndroid.sendAddShortcutMsg( 1 , 1 );
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "SysShortCutClick" );
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		SendMsgToAndroid.sendAddShortcutMsg( 1 , 1 );
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "SysShortCutClick" );
		return true;
	}
	
	@Override
	public View3D getWidget3D()
	{
		return null;
	}
	
	@Override
	public void releaseRegion()
	{
		// TODO Auto-generated method stub
		super.releaseRegion();
	}
}
