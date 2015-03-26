package com.iLoong.launcher.DesktopEdit;


import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.ThemeInformation;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class PopThemeImageView3D extends Icon3D
{
	
	ShortcutInfo info = new ShortcutInfo();
	String action;
	public ThemeInformation theme = null;
	private TextureRegion titleRegion = null;
	private TextureRegion titleRegion1 = null;
	PopThemeImageView3D pop = null;
	boolean ifclick = false;
	
	//	public PopThemeImageView3D(
	//			String name ,
	//			Bitmap orgRegion ,
	//			Bitmap fosRegion ,
	//			String title ,
	//			Bitmap bg )
	//	{
	//		super( name , orgRegion , title , bg );
	//		info.spanX = 1;
	//		info.spanY = 1;
	//		info.container = LauncherSettings.Favorites.CONTAINER_POPUP;
	//		info.title = "quick";
	//		info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
	//		info.intent.setComponent( new ComponentName( "com.ilong.cooee" , "com.ilong.cooee" ) );
	//		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW;
	//		// info.setIcon(orgRegion);
	//		this.setItemInfo( info );
	//		this.x = 0;
	//		this.y = 0;
	//		titleRegion = new TextureRegion( new BitmapTexture( orgRegion ) );
	//		titleRegion1 = new TextureRegion( new BitmapTexture( fosRegion ) );
	//	}
	public PopThemeImageView3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap bg ,
			ThemeInformation theme )
	{
		super( name , bmp , title , bg );
		info.spanX = 1;
		info.spanY = 1;
		info.container = ItemInfo.NO_ID;
		info.title = "quick";
		info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
		info.intent.setComponent( new ComponentName( "com.ilong.cooee" , "com.ilong.cooee" ) );
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT;
		// info.setIcon(bmp);
		this.setItemInfo( info );
		this.x = 0;
		this.y = 0;
		this.theme = theme;
	}
	
	public PopThemeImageView3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap bg )
	{
		super( name , bmp , title , bg );
		info.spanX = 1;
		info.spanY = 1;
		info.container = ItemInfo.NO_ID;
		info.title = "quick";
		info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
		info.intent.setComponent( new ComponentName( "com.ilong.cooee" , "com.ilong.cooee" ) );
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT;
		// info.setIcon(bmp);
		this.setItemInfo( info );
		this.x = 0;
		this.y = 0;
	}
	
	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	//
	// if (titleRegion != null && titleRegion1 != null) {
	// if (ifclick) {
	// batch.draw(titleRegion1, this.x, this.y);
	// } else {
	// batch.draw(titleRegion, this.x, this.y);
	// }
	// }
	// super.draw(batch, parentAlpha);
	// }
	// @Override
	// public boolean onTouchDown(float x, float y, int pointer) {
	// if (titleRegion != null && titleRegion1 != null) {
	// ifclick = true;
	// }
	// // if (titleRegion1 != null) {
	// //
	// // if (pointer > 0) {
	// // return false;
	// // }
	// // displayFocusBG(true);
	// // return true;
	// // } else {
	// return super.onTouchDown(x, y, pointer);
	// // }
	// }
	//
	// @Override
	// public boolean onTouchUp(float x, float y, int pointer) {
	// if (titleRegion != null && titleRegion1 != null) {
	// ifclick = false;
	// }
	// // if (titleRegion1 != null) {
	// // if (pointer > 0) {
	// // return false;
	// // }
	// // displayFocusBG(false);
	// // }
	// return super.onTouchUp(x, y, pointer);
	// }
	public void displayFocusBG(
			boolean bFocus )
	{
		if( bFocus )
		{
			// this.setBackgroud(backFocusground);
			this.region = titleRegion1;
		}
		else
		{
			// this.setBackgroud(backNormalground);
			this.region = titleRegion;
		}
	}
	
	public static String displayName = null;
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( name.equals( "iv_add" ) )
		{
			SendMsgToAndroid.sendSelectHotZhuTi();
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelToUiCenter" );
			return true;
		}
		else
		{
			// ThemeInformation themeInfo = theme;
			// Intent i = new Intent();
			// i.putExtra(StaticClass.EXTRA_PACKAGE_NAME,
			// themeInfo.getPackageName());
			// i.putExtra(StaticClass.EXTRA_CLASS_NAME,
			// themeInfo.getClassName());
			// i.setClass(iLoongLauncher.getInstance(),
			// ThemePreviewHotActivity.class);
			// SendMsgToAndroid.startActivity(i);
			if( theme != null )
			{
				SendMsgToAndroid.changeTheme( theme.getPackageName() , theme.getClassName() , theme.getDisplayName() );
				displayName = theme.getDisplayName();
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModelChangeTheme" , theme.getPackageName() );
				return true;
			}
		}
		return super.onClick( x , y );
	}
	
	public void setAction(
			String action )
	{
		this.info.intent = new Intent( action );
	}
	
	public static void changeCurrentTheme(
			String name )
	{
		if( DesktopEditHost.instance == null )
		{
			return;
		}
		ArrayList<View3D> menuItems2 = DesktopEditHost.instance.mulpMenuHost.editMenuItem.menuItems2;
		if( menuItems2.size() > 0 )
		{
			if( name != null )
			{
				for( int i = 0 ; i < menuItems2.size() ; i++ )
				{
					ViewGroup3D vg = (ViewGroup3D)menuItems2.get( i );
					if( vg.getChildCount() == 2 )
					{
						vg.getChildAt( 1 ).hide();
						PopThemeImageView3D pop = (PopThemeImageView3D)vg.getChildAt( 0 );
						if( pop.theme != null && pop.theme.getPackageName().equals( name ) )
						{
							vg.getChildAt( 1 ).show();
						}
					}
				}
				return;
			}
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	public void mydispose()
	{
		if( titleRegion != null )
		{
			titleRegion.getTexture().dispose();
			titleRegion = null;
		}
		if( titleRegion1 != null )
		{
			titleRegion1.getTexture().dispose();
			titleRegion1 = null;
		}
		this.dispose();
		this.disposeTexture();
	}
}
