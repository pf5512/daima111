package com.iLoong.launcher.Widget3D;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.theme.ThemeManager;


public class Folder3DShortcut extends Widget3DShortcut
{
	
	float Folder3DWidth;
	public static TextureRegion folderpreviewRegion = null;
	
	public Folder3DShortcut(
			String name )
	{
		super( name );
		// if(!AppHost3D.V2)addShortcut();
		canUninstall = false;
		packageName = "com.iLoong.widget.folder";
		// resumeImage = new
		// ImageView3D("resumeicon",Icon3D.resumeTexture,R3D.workspace_multicon_width,R3D.workspace_multicon_height);
		// hideImage = new
		// ImageView3D("hideicon",Icon3D.hideTexture,R3D.workspace_multicon_width,R3D.workspace_multicon_height);
		isHideWidget = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + packageName , false );
		if(!RR.net_version)
			bg = R3D.findRegion( "widget-shortcut-bg" );
		scale = SetupMenu.mScale;
	}
	
	public void onThemeChanged()
	{
		if( titleRegion == null || cellRegion == null || folderpreviewRegion == null )
			return;
		if( preview != null )
			preview.recycle();
		preview = ThemeManager.getInstance().getBitmap( "theme/iconbg/widget-folder-icon.png" );
		( (BitmapTexture)folderpreviewRegion.getTexture() ).changeBitmap( preview , true );
		folderpreviewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		previewRegion = folderpreviewRegion;
		Bitmap bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/widget-folder-icon.png" );
		Bitmap bmp = null;
		if( Icon3D.getIconBg() != null )
		{
			if( bmp1.getWidth() != R3D.sidebar_widget_w * DefaultLayout.thirdapk_icon_scaleFactor )
			{
				bmp = Tools.resizeBitmap( bmp1 , (int)( R3D.sidebar_widget_w * DefaultLayout.thirdapk_icon_scaleFactor ) , (int)( R3D.sidebar_widget_w * DefaultLayout.thirdapk_icon_scaleFactor ) );
			}
			else
			{
				bmp = Bitmap.createScaledBitmap( bmp1 , bmp1.getWidth() , bmp1.getWidth() , true );
			}
		}
		else
		{
			bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
		}
		//		R3D.repack( R3D.folder_name , Utils3D.IconToPixmap3D( bmp , R3D.folder_name , Icon3D.getIconBg() , Icon3D.titleBg ) );
		if( bmp1 != null && !bmp1.isRecycled() )
		{
			bmp1.recycle();
		}
		// Folder3DWidth = iLoongApplication.getInstance()
		// .getResources().getDimension(RR.dimen.app_icon_size);
		// Folder3DWidth=Folder3DWidth*Root3D.scaleFactor;
		// float realWidth=previewRegion.getRegionWidth();
		// scale=Folder3DWidth/realWidth;
	}
	
	// public void addShortcut() {
	// ShortcutInfo info = new ShortcutInfo();
	// FileHandle file =
	// ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/widget-folder-icon.png");
	// Bitmap bmp1 = BitmapFactory.decodeStream(file.read());
	// Bitmap bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w,
	// R3D.sidebar_widget_h, true);
	// Icon3D icon = new Icon3D(R3D.contact_name,bmp,R3D.folder_name);
	// icon.setItemInfo(info);
	// addView(icon);
	// setSize(icon.width,icon.height);
	// setOrigin(width/2, height/2);
	// bmp1.recycle();
	// bmp.recycle();
	// }
	public void makeShortcut()
	{
		if( titleRegion == null || cellRegion == null || folderpreviewRegion == null )
		{
			title = R3D.folder_name;
			int[] span = { 1 , 1 };
			cellTitle = span[0] + "x" + span[1];
			int titleWidth = (int)( width * 3 / 4 );
			int alignH = AppBar3D.TEXT_ALIGN_LEFT;
			int titleHeight = (int)( R3D.widget_preview_title_weight * height );
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
			{
				titleWidth = (int)( width );
				alignH = AppBar3D.TEXT_ALIGN_CENTER;
				titleHeight = MIUIWidgetList.getSingleLineHeight();
			}
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( title , titleWidth , titleHeight , alignH , AppBar3D.TEXT_ALIGN_CENTER , true , Color.WHITE ) ) );
			cellRegion[0] = R3D.findRegion( span[0] + "" );
			cellRegion[1] = R3D.findRegion( span[1] + "" );
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
			{
				if(!RR.net_version)
					miuiCellRegion = R3D.findRegion( "miui-widget-indicator" + span[0] + span[1] );
			}
			// preview = null;
			// FileHandle file =
			// ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/widget-folder-icon.png");
			// preview = BitmapFactory.decodeStream(file.read());
			// teapotXu add start
			preview = ThemeManager.getInstance().getBitmap( "theme/iconbg/widget-folder-icon.png" );
			if( !( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true ) )
			{
				preview = Desktop3DListener.getWidgetPreview( preview , 1 , 1 );
			}
			folderpreviewRegion = new TextureRegion( new BitmapTexture( preview , true ) );
			// previewRegion = R3D.findRegion("folderswidget");
			// teapotXu add end
			if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
			{
				float Folder3DWidth = iLoongApplication.getInstance().getResources().getDimension( RR.dimen.app_icon_size );
				// Folder3DWidth=Folder3DWidth*Root3D.scaleFactor;
				float realWidth = folderpreviewRegion.getRegionWidth();
				scale = Folder3DWidth / realWidth;
				folderpreviewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
		previewRegion = folderpreviewRegion;
	}
	
	@Override
	public View3D getWidget3D()
	{
		UserFolderInfo folderInfo = iLoongLauncher.getInstance().addFolder( 0 , 0 );
		// Root3D.addOrMoveDB(folderInfo,LauncherSettings.Favorites.CONTAINER_DESKTOP);
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DView" , folderInfo );
		folderIcon3D.setPosition( longClickX - folderIcon3D.width / 2 , longClickY - folderIcon3D.height / 2 );
		return folderIcon3D;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode == true && this.widget_shown_in_workspace_edit_mode == true )
		{
			float Folder3DWidth = iLoongApplication.getInstance().getResources().getDimension( RR.dimen.app_icon_size );
			// Folder3DWidth=Folder3DWidth*Root3D.scaleFactor;
			float realWidth = folderpreviewRegion.getRegionWidth();
			scale = Folder3DWidth / realWidth;
			folderpreviewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public void releaseRegion()
	{
		// TODO Auto-generated method stub
		Log.v( "" , "releaseRegion name is Folder3DShortcut" );
		super.releaseRegion();
	}
}
