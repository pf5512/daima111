package com.iLoong.launcher.Widget3D;


import android.graphics.Color;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;


public class OtherTools3DShortcut extends Widget3DShortcut
{
	
	public OtherTools3DShortcut(
			String name )
	{
		super( name );
		scale = SetupMenu.mScale;
	}
	
	public void makeShortcut()
	{
		if( titleRegion == null || previewRegion == null )
		{
			title = R3D.widget_otherTools_title;
			int titleWidth = (int)( width );
			int alignH = AppBar3D.TEXT_ALIGN_CENTER;
			int titleHeight = MIUIWidgetList.getSingleLineHeight();
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) ) );
			preview = null;
			preview = ThemeManager.getInstance().getBitmap( "theme/miui_source/other-tools.png" );
			previewRegion = new TextureRegion( new BitmapTexture( preview ) );
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		SendMsgToAndroid.sendAddShortcutMsg( 0 , 0 );
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		SendMsgToAndroid.sendAddShortcutMsg( 0 , 0 );
		return true;
	}
	
	@Override
	public View3D getWidget3D()
	{
		return null;
	}
}
