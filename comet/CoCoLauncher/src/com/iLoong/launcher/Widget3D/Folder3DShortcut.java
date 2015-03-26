package com.iLoong.launcher.Widget3D;

import java.io.InputStream;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import com.iLoong.launcher.Desktop3D.Log;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class Folder3DShortcut extends Widget3DShortcut {
	
	public Folder3DShortcut(String name) {
		super(name);
		//if(!AppHost3D.V2)addShortcut();
		canUninstall = false;
		packageName = "com.iLoong.widget.folder";
		resumeImage = new ImageView3D("resumeicon",Icon3D.resumeTexture,R3D.workspace_multicon_width,R3D.workspace_multicon_height); 
		hideImage = new ImageView3D("hideicon",Icon3D.hideTexture,R3D.workspace_multicon_width,R3D.workspace_multicon_height); 
		isHideWidget = PreferenceManager.getDefaultSharedPreferences(
    			iLoongLauncher.getInstance()).getBoolean("HIDE:"+packageName, false);
		bg = R3D.findRegion("widget-shortcut-bg");
		scale = SetupMenu.mScale;
	}
//	public void addShortcut() {
//		ShortcutInfo info = new ShortcutInfo();
//		FileHandle file = ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/widget-folder-icon.png");
//		Bitmap bmp1 = BitmapFactory.decodeStream(file.read());
//		Bitmap bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w, R3D.sidebar_widget_h, true);
//		Icon3D icon = new Icon3D(R3D.contact_name,bmp,R3D.folder_name);
//		icon.setItemInfo(info);
//		addView(icon);
//		setSize(icon.width,icon.height);
//		setOrigin(width/2, height/2);
//		bmp1.recycle();
//		bmp.recycle();
//	}
	
	public void makeShortcut(){
		if(titleRegion == null || cellRegion == null || previewRegion == null){
			title = R3D.folder_name;
			int[] span = {1,1};
			cellTitle = span[0]+"x"+span[1];
			titleRegion = new TextureRegion(new Texture3D(AppBar3D.titleToPixmap(title, (int)(width*3/4), 
					(int)(R3D.widget_preview_title_weight*height),AppBar3D.TEXT_ALIGN_LEFT,AppBar3D.TEXT_ALIGN_CENTER,true,Color.WHITE)));
			cellRegion[0] = R3D.findRegion(span[0]+"");
			cellRegion[1] = R3D.findRegion(span[1]+"");
			//preview = null;
			//FileHandle file = ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/widget-folder-icon.png");
			//preview = BitmapFactory.decodeStream(file.read());
			
			previewRegion = R3D.findRegion("folderswidget");
			
			
		}
		
	}
	
	
	
	@Override
	public View3D getWidget3D() {
		UserFolderInfo folderInfo=iLoongLauncher.getInstance().addFolder(0,0);
		//Root3D.addOrMoveDB(folderInfo,LauncherSettings.Favorites.CONTAINER_DESKTOP);
		FolderIcon3D folderIcon3D =new FolderIcon3D("FolderIcon3DView",
				folderInfo);
		folderIcon3D.setPosition(longClickX-folderIcon3D.width/2, longClickY-folderIcon3D.height/2);
		return folderIcon3D;
	}
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
