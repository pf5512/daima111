package com.iLoong.launcher.DesktopEdit;


import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class CustomShortcutIcon extends Icon3D
{
	
	public static final String CUSTOM_SHORTCUT_TITLE_THEME = "shortcut_theme";
	public static final String CUSTOM_SHORTCUT_TITLE_WALLPAPER = "shortcut_wallpaper";
	public static final String CUSTOM_SHORTCUT_TITLE_PREVIEW = "shortcut_preview";
	public static final String CUSTOM_SHORTCUT_TITLE_EFFECT = "shortcut_effect";
	public static final String CUSTOM_SHORTCUT_TITLE_SETTINGS = "shortcut_settings";
	public static final String CUSTOM_SHORTCUT_TITLE_APP = "shortcut_app";
	public static final String CUSTOM_SHORTCUT_TITLE_APPRAISE = "shortcut_appraise";
	public static final String CUSTOM_SHORTCUT_TITLE_APPLIST = "shortcut_applist";
	public static final String CUSTOM_SHORTCUT_ACTION_THEME = "turbo.shortcut.action.theme";
	public static final String CUSTOM_SHORTCUT_ACTION_WALLPAPER = "turbo.shortcut.action.wallpaper";
	public static final String CUSTOM_SHORTCUT_ACTION_PREVIEW = "turbo.shortcut.action.preview";
	public static final String CUSTOM_SHORTCUT_ACTION_EFFECT = "turbo.shortcut.action.effect";
	public static final String CUSTOM_SHORTCUT_ACTION_SETTINGS = "turbo.shortcut.action.settings";
	public static final String CUSTOM_SHORTCUT_ACTION_APP = "turbo.shortcut.action.app";
	public static final String CUSTOM_SHORTCUT_ACTION_APPRAISE = "turbo.shortcut.action.appraise";
	public static final String CUSTOM_SHORTCUT_ACTION_APPLIST = "turbo.shortcut.action.applist";
	
	public CustomShortcutIcon(
			String name )
	{
		// TODO Auto-generated constructor stub
		super( name );
	}
	
	public CustomShortcutIcon(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	public CustomShortcutIcon(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap bg ,
			String action ,
			boolean ifShadow )
	{
		super( name , bmp , title , bg , ifShadow );
		ShortcutInfo info = new ShortcutInfo();
		info.container = ItemInfo.NO_ID;
		info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT;
		info.spanX = 1;
		info.spanY = 1;
		info.title = name;
		info.setIcon( bmp );
		info.intent = new Intent( action );
		this.setItemInfo( info );
	}
	
	public boolean onClick(
			float x ,
			float y )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				if( this.name.equals( CUSTOM_SHORTCUT_TITLE_THEME ) || this.name.equals( CUSTOM_SHORTCUT_TITLE_WALLPAPER ) || this.name.equals( CUSTOM_SHORTCUT_TITLE_PREVIEW ) || this.name
						.equals( CUSTOM_SHORTCUT_TITLE_EFFECT ) || this.name.equals( CUSTOM_SHORTCUT_TITLE_SETTINGS ) || this.name.equals( CUSTOM_SHORTCUT_TITLE_APP ) || this.name
						.equals( CUSTOM_SHORTCUT_TITLE_APPRAISE ) || this.name.equals( CUSTOM_SHORTCUT_TITLE_APPLIST ) )
				{
					Desktop3DListener.root.addFlyView( this.clone() );
					//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "AddTurboShortcut" , this.name );
				}
				return true;
			}
		}
		return super.onClick( x , y );
	}
	
	@Override
	public CustomShortcutIcon clone()
	{
		if( this.region.getTexture() == null )
		{
			Log.e( "iLoong" , " icon:" + this + " region is null!!" );
			return null;
		}
		CustomShortcutIcon icon = new CustomShortcutIcon( this.name , this.region );
		if( this.background9 != null )
		{
			icon.setBackgroud( this.background9 );
		}
		// this.info.itemType=
		// LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
		icon.setItemInfo( new ShortcutInfo( (ShortcutInfo)( this ).getItemInfo() ) );
		icon.setPosition( this.getX() , this.getY() );
		icon.needShowPaoPao = this.needShowPaoPao;
		return icon;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				return true;
			}
		}
		return super.onDoubleClick( x , y );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( this.getItemInfo().container == ItemInfo.NO_ID )
			return true;
		else
			return super.onLongClick( x , y );
	}
	
	static boolean needScale = false;
	
	public static Bitmap getDefaultBitmap(
			String image )
	{
		needScale = false;
		InputStream is = null;
		Bitmap bitmap = null;
		is = ThemeManager.getInstance().getCurrThemeInput( "theme/icon/80/" + image );
		float scalef = DefaultLayout.thirdapk_icon_scaleFactor;
		if( scalef > 1.0 )
		{
			scalef = 1.0f;
		}
		if( image.equals( "middle.png" ) )
		{
			is = ThemeManager.getInstance().getCurrThemeInput( "theme/dock3dbar/" + image );
		}
		else
		{
			is = ThemeManager.getInstance().getCurrThemeInput( "theme/icon/80/" + image );
		}
		if( is == null )
		{
			needScale = true;
			try
			{
				is = iLoongLauncher.getInstance().getAssets().open( "theme/icon/80/" + image );
				if( is != null )
				{
					bitmap = BitmapFactory.decodeStream( is );
					bitmap = Tools.resizeBitmap( bitmap , (int)( DefaultLayout.app_icon_size * scalef ) , (int)( DefaultLayout.app_icon_size * scalef ) );
					try
					{
						if( is != null )
						{
							is.close();
						}
					}
					catch( IOException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return bitmap;
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		bitmap = ThemeManager.getInstance().getBitmap( is );
		bitmap = Tools.resizeBitmap( bitmap , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
		try
		{
			if( is != null )
			{
				is.close();
			}
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
	
	public void onThemeChanged()
	{
		ShortcutInfo info = (ShortcutInfo)getItemInfo();
		Bitmap bitmap = null;
		Bitmap bg = Icon3D.getIconBg();
		Bitmap newBitmap = null;
		String imgPath = null;
		String title = null;
		if( bg == null )
		{
			bg = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/shortcut_bg.png" );
		}
		if( info.title.equals( CUSTOM_SHORTCUT_TITLE_THEME ) )
		{
			imgPath = "theme.png";
			title = iLoongLauncher.getInstance().getString( RR.string.theme );
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_WALLPAPER ) )
		{
			imgPath = "wallpaper.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_wallpaper );
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_PREVIEW ) )
		{
			imgPath = "preview.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_preview );
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_EFFECT ) )
		{
			imgPath = "effect.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_effect );
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_SETTINGS ) )
		{
			imgPath = "desksettings.png";
			title = iLoongLauncher.getInstance().getString( RR.string.desktop_setting );
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_APP ) )
		{
			imgPath = "app.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_app );
		}
		if( info.title.equals( CUSTOM_SHORTCUT_TITLE_APPLIST ) )
		{
			//	bitmap = getDefaultBitmap( "middle.png" );
			imgPath = "middle.png";
			title = iLoongLauncher.getInstance().getString( RR.string.mainmenu );
			//newBitmap = Utils3D.titleToBitmap( bitmap , iLoongLauncher.getInstance().getString( RR.string.mainmenu ) , null , null , R3D.workspace_cell_width , R3D.workspace_cell_height , true , false );
		}
		bitmap = getDefaultBitmap( imgPath );
		if( !needScale )
		{
			bg = null;
		}
		if( info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
		{
			newBitmap = Utils3D.titleToBitmap( bitmap , title , bg , null , R3D.workspace_cell_width , R3D.workspace_cell_height , true , false );
		}
		else
		{
			newBitmap = Utils3D.titleToBitmap( bitmap , title , bg , null , R3D.workspace_cell_width , R3D.workspace_cell_height , true );
		}
		if( newBitmap == null )
		{
			return;
		}
		if( this.region.getTexture() != null )
		{
			region.getTexture().dispose();
		}
		TextureRegion newRegion = new TextureRegion( new BitmapTexture( newBitmap ) );
		newRegion.setU( this.region.getU() );
		newRegion.setU2( this.region.getU2() );
		newRegion.setV( this.region.getV() );
		newRegion.setV2( this.region.getV2() );
		this.region = newRegion;
	}
	
	public static View3D createCustomShortcut(
			ItemInfo info ,
			boolean ifShadow )
	{
		CustomShortcutIcon shortcut = null;
		Bitmap bitmap = null;
		String name = null;
		String imgPath = null;
		String title = null;
		String action = null;
		Bitmap addBg = Icon3D.getIconBg();
		try
		{
			if( addBg == null )
			{
				addBg = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/shortcut_bg.png" ) );
				addBg = Tools.resizeBitmap( addBg , Utils3D.getScreenWidth() / 720f );
				addBg = Tools.resizeBitmap( addBg , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
			}
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		if( info.title.equals( CUSTOM_SHORTCUT_TITLE_THEME ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_THEME;
			imgPath = "theme.png";
			title = iLoongLauncher.getInstance().getString( RR.string.theme );
			action = CUSTOM_SHORTCUT_ACTION_THEME;
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_WALLPAPER ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_WALLPAPER;
			imgPath = "wallpaper.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_wallpaper );
			action = CUSTOM_SHORTCUT_ACTION_WALLPAPER;
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_PREVIEW ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_PREVIEW;
			imgPath = "preview.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_preview );
			action = CUSTOM_SHORTCUT_ACTION_PREVIEW;
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_EFFECT ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_EFFECT;
			imgPath = "effect.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_effect );
			action = CUSTOM_SHORTCUT_ACTION_EFFECT;
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_SETTINGS ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_SETTINGS;
			imgPath = "desksettings.png";
			title = iLoongLauncher.getInstance().getString( RR.string.desktop_setting );
			action = CUSTOM_SHORTCUT_ACTION_SETTINGS;
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_APP ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_APP;
			imgPath = "app.png";
			title = iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_app );
			action = CUSTOM_SHORTCUT_ACTION_APP;
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_APPRAISE ) )
		{
		}
		else if( info.title.equals( CUSTOM_SHORTCUT_TITLE_APPLIST ) )
		{
			name = CUSTOM_SHORTCUT_TITLE_APPLIST;
			imgPath = "middle.png";
			title = iLoongLauncher.getInstance().getString( RR.string.mainmenu );
			action = CUSTOM_SHORTCUT_ACTION_APPLIST;
			//Bitmap btapp = getDefaultBitmap( "middle.png" );
			//shortcut = new CustomShortcutIcon( CUSTOM_SHORTCUT_TITLE_APPLIST , btapp , iLoongLauncher.getInstance().getString( RR.string.mainmenu ) , null , CUSTOM_SHORTCUT_ACTION_APPLIST );
		}
		bitmap = getDefaultBitmap( imgPath );
		if( !needScale )
		{
			addBg = null;
		}
		if( info.title.equals( CUSTOM_SHORTCUT_TITLE_APPLIST ) )
		{
			shortcut = new CustomShortcutIcon( name , bitmap , title , addBg , action , false );
		}
		else
		{
			shortcut = new CustomShortcutIcon( name , bitmap , title , addBg , action , ifShadow );
		}
		shortcut.setItemInfo( info );
		return shortcut;
	}
}
