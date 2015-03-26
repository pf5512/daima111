package com.iLoong.launcher.DesktopEdit;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.download.DownloadList;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.Tools;
import com.coco.wallpaper.wallpaperbox.WallpaperInformation;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.DesktopEdit.MessageData.VIEW_TYPE;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.umeng.analytics.MobclickAgent;


public class DesktopEditMenuItem extends ViewGroup3D implements TabChangeListenner
{
	
	public static boolean widgetInited = true;
	public static Object lock = new Object();
	public static final int TAB_ADD = 0;
	public static final int TAB_THEME = 1;
	public static final int TAB_WALLPAPER = 2;
	public static final int TAB_EFFECT = 3;
	public static final int SET_CONTAINER = 0;
	public static MessageData messageData;
	MenuShortcut shortcut = new MenuShortcut( "MenuShortcut" );
	ShortcutInfo info = new ShortcutInfo();
	public static boolean needScale = false;
	String action;
	ArrayList<View3D> shortcutlist = new ArrayList<View3D>();
	ArrayList<Widget3DShortcut> widgetList = new ArrayList<Widget3DShortcut>();
	ArrayList<View3D> menuItems2 = new ArrayList<View3D>();
	ArrayList<View3D> menuItems4 = new ArrayList<View3D>();
	public ArrayList<MenuItemPage> list_pages;
	public static int cur_tab = TAB_ADD;
	DesktopEditMenu popMenu;
	public static Bitmap middleImgView2;
	public float scale = Utils3D.getScreenWidth() / 720f;
	
	public DesktopEditMenuItem(
			String name )
	{
		super( name );
		x = 0;
		y = 0;
		this.width = Utils3D.getScreenWidth();
		this.height = R3D.pop_menu_height - R3D.pop_menu_title_height;
		this.setOrigin( width / 2 , height / 2 );
		list_pages = new ArrayList<MenuItemPage>();
		initMenuItems();
	}
	
	public ArrayList<MenuItemPage> getPopPages()
	{
		return list_pages;
	}
	
	public void setPopMenu(
			DesktopEditMenu pop )
	{
		popMenu = pop;
	}
	
	public static interface onMenuClick
	{
		
		boolean onAction();
	}
	
	public static class MenuItem extends Icon3D
	{
		
		onMenuClick onMyclick;
		
		public MenuItem(
				String name )
		{
			super( name );
		}
		
		public boolean onClick(
				float x ,
				float y )
		{
			return true;
		}
		
		public void setAction()
		{
		}
	}
	
	public void initMenuItems()
	{
		initPage1();
		initPage2();
		initPage3();
		initPage4();
	}
	
	private void initPage1()
	{
		Page1InitializeImpl page1 = new Page1InitializeImpl( "page1" );
		page1.setDisposable( false );
		page1.loadPage();
		this.addView( page1 );
		list_pages.add( page1 );
	}
	
	private void initPage2()
	{
		Page2InitializeImpl page2 = new Page2InitializeImpl( "page2" );
		page2.setDisposable( true );
		page2.hide();
		page2.setShowprogress( true );
		this.addView( page2 );
		list_pages.add( page2 );
	}
	
	private void initPage3()
	{
		Page3InitializeImpl page3 = new Page3InitializeImpl( "page3" );
		page3.setDisposable( true );
		page3.setShowprogress( true );
		page3.hide();
		this.addView( page3 );
		list_pages.add( page3 );
	}
	
	private void initPage4()
	{
		Page4InitializeImpl page4 = new Page4InitializeImpl( "page4" );
		page4.hide();
		page4.setDisposable( true );
		this.addView( page4 );
		list_pages.add( page4 );
	}
	
	public class ButtonIcon3D extends Icon3D
	{
		
		ShortcutInfo info = new ShortcutInfo();
		String action;
		public static final int MSG_NEW_FOLDER_IN_APPLIST = 110;
		
		public ButtonIcon3D(
				String name ,
				TextureRegion region )
		{
			super( name , region );
		}
		
		public ButtonIcon3D(
				String name ,
				Bitmap bmp ,
				String title ,
				Bitmap bg ,
				String action )
		{
			super( name , bmp , title , bg , false );
			info.spanX = 1;
			info.spanY = 1;
			info.container = ItemInfo.NO_ID;
			info.title = title;
			info.intent = new Intent( action );
			info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT;
			info.setIcon( middleImgView2 );
			this.setItemInfo( info );
			this.x = 0;
			this.y = 0;
			this.action = action;
		}
		
		public ButtonIcon3D(
				String name ,
				Bitmap bmp ,
				String title ,
				Bitmap bg )
		{
			super( name , bmp , title , bg , false );
			info.spanX = 1;
			info.spanY = 1;
			info.container = ItemInfo.NO_ID;
			info.title = title;
			info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
			info.intent.setComponent( new ComponentName( "com.ilong.cooee" , "com.ilong.cooee" ) );
			info.itemType = LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT;
			info.setIcon( middleImgView2 );
			this.setItemInfo( info );
			this.x = 0;
			this.y = 0;
		}
		
		public void onThemeChanged()
		{
			super.onThemeChanged();
		}
		
		public ButtonIcon3D(
				String name )
		{
			super( name );
		}
		
		public void setAction(
				String action )
		{
			this.info.intent = new Intent( action );
		}
		
		public String getAction()
		{
			return action;
		}
		
		public boolean onClick(
				float x ,
				float y )
		{
			if( this.name.equals( "addShortcut" ) )
			{
				showShortcut();
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModeShowShortcut" );
			}
			else if( this.name.equals( "addWidget" ) )
			{
				showWidget();
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModeShowWidget" );
			}
			else if( this.name.equals( "addApp" ) )
			{
				showAllApp();
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModeShowAllApp" );
			}
			else if( this.name.equals( "addFolder" ) )
			{
				viewParent.onCtrlEvent( this , MSG_NEW_FOLDER_IN_APPLIST );
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EditModeNewFolder" );
			}
			iLoongLauncher.getInstance().d3dListener.root.workspaceChangeForPopMenu();
			return true;
		}
		
		@Override
		public boolean onDoubleClick(
				float x ,
				float y )
		{
			return true;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			//if( this.name.equals( "addShortcut" ) || this.name.equals( "addWidget" ) || this.name.equals( "addApp" ) || this.name.equals( "addFolder" ) )
			return true;
			//else
			//return super.onLongClick( x , y );
		}
	}
	
	@Override
	public void onTabChange(
			Object object )
	{
		// TODO Auto-generated method stub
	}
	
	public synchronized void showWidget()
	{
		if( !widgetInited )
		{
			//return;
		}
		synchronized( lock )
		{
			widgetInited = false;
			//			Contact3DShortcut contact3DHost = new Contact3DShortcut( "contact3d" );
			//			contact3DHost.setWidget3DShortcutShownPlace( true );
			//			widgetList.add( contact3DHost );
			MessageData msgData = new MessageData<ButtonIcon3D>( VIEW_TYPE.TYPE_WIDGET3D );
			msgData.setObject( widgetList );
			msgData.setTitle( iLoongLauncher.getInstance().getString( RR.string.system_widget ) );
			msgData.setTabIndex( 0 );
			msgData.setDispose( false );
			this.setTag( msgData );
			this.viewParent.onCtrlEvent( this , SET_CONTAINER );
		}
	}
	
	public void showShortcut()
	{
		shortcutlist.clear();
		Bitmap addBg = Icon3D.getIconBg();
		Bitmap bttheme = null;
		try
		{
			if( addBg == null )
			{
				addBg = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/shortcut_bg.png" ) );
				addBg = Tools.resizeBitmap( addBg , Utils3D.getScreenWidth() / 720f );
				addBg = Tools.resizeBitmap( addBg , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
			}
			bttheme = getDefaultBitmap( "coco.zhutixuanze" , "theme.png" );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		CustomShortcutIcon theme;
		if( needScale )
			theme = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_THEME ,
					bttheme ,
					iLoongLauncher.getInstance().getString( RR.string.theme ) ,
					addBg ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_THEME ,
					true );
		else
			theme = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_THEME ,
					bttheme ,
					iLoongLauncher.getInstance().getString( RR.string.theme ) ,
					null ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_THEME ,
					true );
		shortcutlist.add( theme );
		Bitmap wp = getDefaultBitmap( "coco.bizhi" , "wallpaper.png" );
		CustomShortcutIcon wallpaper;
		if( needScale )
			wallpaper = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_WALLPAPER ,
					wp ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_wallpaper ) ,
					addBg ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_WALLPAPER ,
					true );
		else
			wallpaper = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_WALLPAPER ,
					wp ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_wallpaper ) ,
					null ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_WALLPAPER ,
					true );
		shortcutlist.add( wallpaper );
		Bitmap btpreview = getDefaultBitmap( "preview.png" , "preview.png" );
		CustomShortcutIcon preview;
		if( needScale )
			preview = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_PREVIEW ,
					btpreview ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_preview ) ,
					addBg ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_PREVIEW ,
					true );
		else
			preview = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_PREVIEW ,
					btpreview ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_preview ) ,
					null ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_PREVIEW ,
					true );
		shortcutlist.add( preview );
		Bitmap btsetting = getDefaultBitmap( "desksettings.png" , "desksettings.png" );//ThemeManager.getInstance().getBitmap( "theme/desktopEdit/edit_setting.png" );
		CustomShortcutIcon setting;
		if( needScale )
			setting = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_SETTINGS ,
					btsetting ,
					iLoongLauncher.getInstance().getString( RR.string.desktop_setting ) ,
					addBg ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_SETTINGS ,
					true );
		else
			setting = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_SETTINGS ,
					btsetting ,
					iLoongLauncher.getInstance().getString( RR.string.desktop_setting ) ,
					null ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_SETTINGS ,
					true );
		shortcutlist.add( setting );
		Bitmap btapp = getDefaultBitmap( "app.png" , "app.png" );//ThemeManager.getInstance().getBitmap( "theme/desktopEdit/edit_setting.png" );
		CustomShortcutIcon app;
		if( needScale )
			app = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_APP ,
					btapp ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_app ) ,
					addBg ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_APP ,
					true );
		else
			app = new CustomShortcutIcon(
					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_APP ,
					btapp ,
					iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_shortcut_app ) ,
					null ,
					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_APP ,
					true );
		shortcutlist.add( app );
		shortcutlist.add( shortcut );
		CustomShortcutIcon appList;
		Bitmap btapplist = getDefaultBitmap( "middle.png" , "middle.png" );//ThemeManager.getInstance().getBitmap( "theme/desktopEdit/edit_setting.png" );
		//		if( needScale )
		//			appList = new CustomShortcutIcon(
		//					CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_APPLIST ,
		//					btapp ,
		//					iLoongLauncher.getInstance().getString( RR.string.main_menu ) ,
		//					addBg ,
		//					CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_APPLIST );
		//		else
		/*we consider all Themes have at least one of main_menu icons*/
		appList = new CustomShortcutIcon(
				CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_APPLIST ,
				btapplist ,
				iLoongLauncher.getInstance().getString( RR.string.mainmenu ) ,
				null ,
				CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_APPLIST ,
				false );
		shortcutlist.add( appList );
		MessageData msgData = new MessageData<ButtonIcon3D>( VIEW_TYPE.TYPE_SHORTCUT );
		msgData.setObject( shortcutlist );
		msgData.setTitle( iLoongLauncher.getInstance().getString( RR.string.shortcut_title ) );
		msgData.setTabIndex( 0 );
		msgData.setDispose( false );
		this.setTag( msgData );
		this.viewParent.onCtrlEvent( this , SET_CONTAINER );
	}
	
	public static Canvas canvas = new Canvas();
	
	public static Bitmap drawBg2Bitmap(
			Bitmap bitmap ,
			int size ,
			Bitmap bg )
	{
		Bitmap bmp = Bitmap.createBitmap( size , size , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		if( bg.getWidth() > size || bg.getHeight() > size )
		{
			canvas.drawBitmap( bg , 0 , 0 , null );
		}
		else
		{
			canvas.drawBitmap( bg , ( size - bg.getWidth() ) / 2 , ( size - bg.getHeight() ) / 2 , null );
		}
		canvas.drawBitmap( bitmap , ( size - bitmap.getWidth() ) / 2 , ( size - bitmap.getHeight() ) / 2 , null );
		paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
		if( Icon3D.getMask() != null )
			canvas.drawBitmap( Icon3D.getMask() , size - Icon3D.getMask().getWidth() , size - Icon3D.getMask().getHeight() , paint );
		paint.setXfermode( null );
		return bmp;
	}
	
	public static Bitmap getEditBitmap(
			Bitmap bitmap )
	{
		int size = (int)( 116 * Utils3D.getScreenWidth() / 720f );
		float bitmapScale = 1.0f;
		if( bitmap.getWidth() != size || bitmap.getHeight() != size )
		{
			bitmapScale = (float)size / (float)bitmap.getWidth();
		}
		if( DefaultLayout.thirdapk_icon_scaleFactor != 1f )
		{
			bitmapScale = bitmapScale * DefaultLayout.thirdapk_icon_scaleFactor * 0.98f;
		}
		if( bitmapScale != 1f )
		{
			bitmap = Tools.resizeBitmap( bitmap , bitmapScale );
		}
		Bitmap bg = Icon3D.getIconBg();
		if( bg == null )
		{
			bg = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/shortcut_bg.png" );
			bg = Tools.resizeBitmap( bg , ( Utils3D.getScreenWidth() / ( 720f * bg.getWidth() / 116 ) ) );
		}
		return drawBg2Bitmap( bitmap , size , bg );
	}
	
	public static Bitmap getDefaultBitmap(
			String packName ,
			String className )
	{
		needScale = false;
		InputStream is = null;
		String imagePath = null;
		imagePath = DefaultLayout.getDefaultVirtureImage( packName );
		if( imagePath == null )
		{
			imagePath = DefaultLayout.GetVirtureImageWithPkgClassName( packName , className );
			if( imagePath != null )
			{
				if( DefaultLayout.useCustomVirtual )
				{
					try
					{
						is = new FileInputStream( imagePath );
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
					}
				}
				else
				{
					is = ThemeManager.getInstance().getInputStream( imagePath );
				}
			}
		}
		else
		{
			is = ThemeManager.getInstance().getInputStream( imagePath );
		}
		if( is == null )
		{
			if( packName.equals( "middle.png" ) )
			{
				is = ThemeManager.getInstance().getCurrThemeInput( "theme/dock3dbar/" + className );
			}
			else
			{
				is = ThemeManager.getInstance().getCurrThemeInput( "theme/icon/80/" + className );
			}
		}
		if( is == null )
		{
			needScale = true;
			try
			{
				is = iLoongLauncher.getInstance().getAssets().open( "theme/icon/80/" + className );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			if( is != null )
			{
				Bitmap bitmap = ThemeManager.getInstance().getBitmap( is );
				bitmap = Tools.resizeBitmap(
						bitmap ,
						(int)( DefaultLayout.app_icon_size * DefaultLayout.thirdapk_icon_scaleFactor ) ,
						(int)( DefaultLayout.app_icon_size * DefaultLayout.thirdapk_icon_scaleFactor ) );
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
		Bitmap bitmap = ThemeManager.getInstance().getBitmap( is );
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
	
	public void showAllApp()
	{
		//		List<AppIcon3D> allApps = getAllApp();
		MessageData<?> msgData = new MessageData<ButtonIcon3D>( VIEW_TYPE.TYPE_BUTTON_ICON3D );
		msgData.setObject( null );
		msgData.setTitle( iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_add_app ) );
		msgData.setTabIndex( 0 );
		msgData.setDispose( false );
		this.setTag( msgData );
		this.viewParent.onCtrlEvent( this , SET_CONTAINER );
	}
	
	public class Page4InitializeImpl extends MenuItemPage
	{
		
		public Page4InitializeImpl(
				String name )
		{
			super( name );
			this.setProgressMargin( (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_height - 0 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 ) );
		}
		
		@Override
		public boolean pageInitialize()
		{
			int num = R3D.workSpace_list_string.length;
			String path = "launcher/effects/workspace/";
			DefaultLayout.if_show_cover = false;
			DefaultLayout.if_show_mask = false;
			for( int i = 0 ; i < num ; i++ )
			{
				String title = "";
				Bitmap bmp = null;
				Bitmap bg = null;
				EffectIcon3D mIcon3D = null;
				title = R3D.workSpace_list_string[i];
				try
				{
					int index = i;
					if( DefaultLayout.page_effect_no_radom_style )
					{
						if( index != 0 )
						{
							index++;
						}
					}
					bmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( path + index + ".png" ) );
					bg = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( path + "effect_bg.png" ) );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				if( bmp != null )
				{
					bmp = Tools.resizeBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
					bg = Tools.resizeBitmap( bg , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
					mIcon3D = new EffectIcon3D( title , bmp , title , bg , i );
					ApplicationInfo mApplicationInfo = new ApplicationInfo( "EffectPreview3D" , null );
					mApplicationInfo.intent.putExtra( EffectPreview3D.ICON_EXTRA_TYPE , 0 );
					mApplicationInfo.intent.putExtra( EffectPreview3D.ICON_EXTRA_INDEX , i );
					ShortcutInfo mShortcutInfo = mApplicationInfo.makeShortcut();
					mIcon3D.setItemInfo( mShortcutInfo );
					menuItems4.add( mIcon3D );
				}
			}
			this.hide();
			this.setButtons( menuItems4 , 4 , 1 , Utils3D.getScreenWidth() , R3D.pop_menu_height - R3D.pop_menu_title_height );
			this.setCurrentPage( 0 );
			cur_tab = TAB_EFFECT;
			DefaultLayout.if_show_cover = true;
			DefaultLayout.if_show_mask = true;
			return false;
		}
		
		@Override
		public boolean pageFinalize()
		{
			for( int i = 0 ; i < this.getCurrList().size() ; i++ )
			{
				DesktopEditHost.disposeAllViews( this.getCurrList().get( i ) );
			}
			this.getCurrList().clear();
			this.removeAllViews();
			return true;
		}
	}
	
	public static List<PopThemeImageView3D> popimageviewlist = new ArrayList<PopThemeImageView3D>();
	public static List<ImageView3D> imageviewlist = new ArrayList<ImageView3D>();
	
	public class Page2InitializeImpl extends MenuItemPage
	{
		
		ThemeQuery themequery;
		
		public Page2InitializeImpl(
				String name )
		{
			super( name );
			this.setProgressMargin( (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_height - 0 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 ) );
		}
		
		@Override
		public boolean pageInitialize()
		{
			menuItems2.clear();
			this.setShowprogress( true );
			Context mcontext = iLoongLauncher.getInstance();
			SharedPreferences sp = mcontext.getSharedPreferences( "CurrentTheme" , mcontext.MODE_PRIVATE );
			String currentTheme = sp.getString( "currenttheme_pkg" , "com.cooeeui.brand.turbolauncher" );
			themequery = new ThemeQuery( iLoongLauncher.getInstance() );
			if( themequery.localList.size() > 0 )
			{
				for( int i = themequery.localList.size() - 1 ; i >= 0 ; i-- )
				{
					Bitmap imgThumb = null;
					ThemeInformation themeInfo = (ThemeInformation)themequery.localList.get( i );
					if( themeInfo.getDisplayName().equals( iLoongLauncher.getInstance().getString( RR.string.default_theme ) ) )
					{
						imgThumb = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/defaultthemeiloong.png" );
					}
					else
					{
						imgThumb = themeInfo.getIconImage();
					}
					if( imgThumb != null )
					{
						ViewGroup3D vg = new ViewGroup3D( "big" );
						vg.setSize( 148 * Utils3D.getScreenWidth() / 720f , 172 * Utils3D.getScreenWidth() / 720f );
						Bitmap bit = Tools.resizeBitmap( imgThumb , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
						Bitmap temp1 = ThemeAndPagerHelper.BitmapChange(
								bit ,
								DefaultLayout.app_icon_size ,
								DefaultLayout.app_icon_size ,
								true ,
								ThemeManager.getInstance().getBitmap( "theme/desktopEdit/popupmask.png" ) );
						String title = "";
						String[] tiltles = themequery.localList.get( i ).getDisplayName().split( "_" );
						if( tiltles.length == 2 )
						{
							if( !tiltles[0].contains( "Turbo" ) && tiltles[1].contains( "Turbo" ) )
							{
								title = tiltles[0];
							}
							else if( !tiltles[1].contains( "Turbo" ) && tiltles[0].contains( "Turbo" ) )
							{
								title = tiltles[1];
							}
							else
							{
								title = tiltles[1];
							}
						}
						else
						{
							title = themequery.localList.get( i ).getDisplayName();
						}
						PopThemeImageView3D themeimage = new PopThemeImageView3D( themequery.localList.get( i ).getDisplayName() , temp1 , title , null , themeInfo );
						themeimage.setPosition( ( vg.width - themeimage.width ) / 2 , ( vg.height - themeimage.height ) / 2 );
						vg.addView( themeimage );
						popimageviewlist.add( themeimage );
						//				if( currentTheme != null && currentTheme.equals( themeInfo.getPackageName() ))
						//				{
						Bitmap temp2 = ThemeAndPagerHelper.getBitmap( "used.png" );
						if( temp2 != null )
						{
							ImageView3D used = new ImageView3D( "used" , temp2 );
							used.setSize( 37 * Utils3D.getScreenWidth() / 720f , 37 * Utils3D.getScreenWidth() / 720f );
							used.setPosition( ( vg.width - DefaultLayout.app_icon_size ) / 2 + DefaultLayout.app_icon_size - 37 * scale , vg.height - getIconBmpHeight() );
							if( currentTheme.equals( themeInfo.getPackageName() ) )
							{
								used.show();
							}
							else
							{
								used.hide();
							}
							vg.addView( used );
							imageviewlist.add( used );
						}
						//				}
						menuItems2.add( vg );
					}
				}
				ViewGroup3D vg = new ViewGroup3D( "vg" );
				vg.setSize( 148 * scale , 172 * scale );
				Bitmap originBit = null;
				try
				{
					originBit = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/add.png" ) );
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				PopThemeImageView3D ivadd = new PopThemeImageView3D( "iv_add" , Tools.resizeBitmap( originBit , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size ) , mcontext.getResources()
						.getString( RR.string.more ) , null );
				ivadd.setPosition( ( vg.width - ivadd.width ) / 2 , ( vg.height - ivadd.height ) / 2 );
				vg.addView( ivadd );
				menuItems2.add( vg );
			}
			this.setButtons( menuItems2 , 4 , 1 , Utils3D.getScreenWidth() , R3D.pop_menu_height - R3D.pop_menu_title_height );
			this.setCurrentPage( 0 );
			cur_tab = TAB_THEME;
			return true;
		}
		
		@Override
		public boolean pageFinalize()
		{
			if( popimageviewlist != null )
			{
				for( int i = 0 ; i < popimageviewlist.size() ; i++ )
				{
					popimageviewlist.get( i ).mydispose();
				}
				popimageviewlist.clear();
			}
			if( imageviewlist != null )
			{
				for( int i = 0 ; i < imageviewlist.size() ; i++ )
				{
					imageviewlist.get( i ).dispose();
				}
				imageviewlist.clear();
			}
			for( int i = 0 ; i < this.getCurrList().size() ; i++ )
			{
				DesktopEditHost.disposeAllViews( this.getCurrList().get( i ) );
			}
			this.getCurrList().clear();
			this.removeAllViews();
			if( themequery != null )
			{
				themequery.free();
				themequery = null;
			}
			//menuItems2.clear();
			return true;
		}
	}
	
	public static Paint paint = new Paint();
	public static FontMetrics fontMetrics = new FontMetrics();
	
	public float getIconBmpHeight()
	{
		float iconBmpHeight = 0;
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		float paddingTop;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
		}
		if( paddingTop < 0 )
		{
			paddingTop = 0;
		}
		space_height -= R3D.hot_top_ascent_distance_hide_title;
		if( DefaultLayout.show_font_bg == true && Icon3D.titleBg != null )
		{
			space_height -= ( Icon3D.titleBg.getHeight() - singleLineHeight ) / 2f;
		}
		if( space_height < 0 )
			space_height = 0;
		iconBmpHeight = bmpHeight + paddingTop;
		return iconBmpHeight;
	}
	
	public class Page3InitializeImpl extends MenuItemPage
	{
		
		public Page3InitializeImpl(
				String name )
		{
			super( name );
			this.setProgressMargin( (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_height - 0 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 ) );
		}
		
		@Override
		public boolean pageInitialize()
		{
			ArrayList<View3D> menuItems3 = new ArrayList<View3D>();
			this.setShowprogress( true );
			menuItems3.clear();
			ThemeAndPagerHelper.findWallpapers();
			Context context = iLoongLauncher.getInstance();
			DownModule dm = DownModule.getInstance( context );
			WallpaperQuery wq = new WallpaperQuery( context , dm );
			if( wq.wallpaperimage.size() != 0 )
			{
				for( int i = 0 ; i < wq.wallpaperimage.size() ; i++ )
				{
					Bitmap bitm = wq.wallpaperimage.get( i ).getBit();
					if( bitm != null )
					{
						ViewGroup3D vg = new ViewGroup3D( "vg" );
						vg.setSize( 148 * scale , 172 * scale );
						Bitmap bit = Tools.resizeBitmap( bitm , (int)( 116 * scale ) , (int)( 116 * scale ) );
						Bitmap temp = ThemeAndPagerHelper.BitmapChange(
								bit ,
								(int)( 116 * scale ) ,
								(int)( 116 * scale ) ,
								true ,
								ThemeManager.getInstance().getBitmap( "theme/desktopEdit/popupmask.png" ) );
						PopImageView3D iv = new PopImageView3D( "PopImageView3D" , temp , wq.wallpaperimage.get( i ).getName() , null );
						iv.setPosition( ( vg.width - iv.width ) / 2 , ( vg.height - iv.height ) / 2 );
						vg.addView( iv );
						menuItems3.add( vg );
					}
				}
			}
			if( wq.localList.size() != 0 )
			{
				for( int j = 0 ; j < wq.localList.size() ; j++ )
				{
					WallpaperInformation Info = (WallpaperInformation)wq.localList.get( j );
					if( Info.isNeedLoadDetail() )
					{
						Bitmap imgThumb = Info.getThumbImage();
						if( imgThumb == null )
						{
							Info.loadDetail( context );
						}
						if( Info.getThumbImage() == null )
						{
							dm.downloadThumb( Info.getPackageName() , DownloadList.Wallpaper_Type );
						}
					}
					Bitmap imgThumb = Info.getThumbImage();
					if( imgThumb != null )
					{
						ViewGroup3D vg = new ViewGroup3D( "vg" );
						vg.setSize( 148 * scale , 172 * scale );
						Bitmap bit = Tools.resizeBitmap( imgThumb , (int)( 116 * scale ) , (int)( 116 * scale ) );
						Bitmap temp = ThemeAndPagerHelper.BitmapChange(
								bit ,
								(int)( 116 * scale ) ,
								(int)( 116 * scale ) ,
								true ,
								ThemeManager.getInstance().getBitmap( "theme/desktopEdit/popupmask.png" ) );
						PopImageView3D iv = new PopImageView3D( "PopImageView3D" , temp , Info.getDisplayName() , Info.getPackageName() );
						iv.setPosition( ( vg.width - iv.width ) / 2 , ( vg.height - iv.height ) / 2 );
						vg.addView( iv );
						menuItems3.add( vg );
					}
				}
			}
			ViewGroup3D vgadd = new ViewGroup3D( "vgadd" );
			vgadd.setSize( 148 * scale , 172 * scale );
			PopImageView3D ivadd = new PopImageView3D( "iv_add" , ThemeAndPagerHelper.getRegion( "add.png" ) , ThemeAndPagerHelper.getRegion( "addclick.png" ) );
			ivadd.setSize( 116 * scale , 116 * scale );
			ivadd.setPosition( ( vgadd.width - ivadd.width ) / 2 , ( vgadd.height - ivadd.height ) / 2 );
			vgadd.addView( ivadd );
			menuItems3.add( vgadd );
			this.setButtons( menuItems3 , 4 , 1 , Utils3D.getScreenWidth() , R3D.pop_menu_height - R3D.pop_menu_title_height );
			this.setCurrentPage( 0 );
			cur_tab = TAB_WALLPAPER;
			return true;
		}
		
		@Override
		public boolean pageFinalize()
		{
			for( int i = 0 ; i < this.getCurrList().size() ; i++ )
			{
				DesktopEditHost.disposeAllViews( this.getCurrList().get( i ) );
			}
			this.getCurrList().clear();
			this.removeAllViews();
			return true;
		}
	}
	
	public class Page1InitializeImpl extends MenuItemPage
	{
		
		public Page1InitializeImpl(
				String name )
		{
			super( name );
			this.setProgressMargin( (int)( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( R3D.pop_menu_height - 0 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 ) );
		}
		
		@Override
		public boolean pageInitialize()
		{
			ArrayList<View3D> menuItems = new ArrayList<View3D>();
			Bitmap addWidgetBmp = null;
			Bitmap addShortcutBmp = null;
			Bitmap addAppBmp = null;
			Bitmap addFolderBmp = null;
			Bitmap addBg = null;
			try
			{
				addWidgetBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/add_widget_nor.png" ) );
				addShortcutBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/add_shortcut_nor.png" ) );
				addAppBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/add_app_nor.png" ) );
				addFolderBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/add_folder_nor.png" ) );
				addBg = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/desktopEdit/add_bg.png" ) );
				addWidgetBmp = Tools.resizeBitmap( addWidgetBmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				addShortcutBmp = Tools.resizeBitmap( addShortcutBmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				addAppBmp = Tools.resizeBitmap( addAppBmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				addFolderBmp = Tools.resizeBitmap( addFolderBmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				addBg = Tools.resizeBitmap( addBg , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			DefaultLayout.if_show_cover = false;
			DefaultLayout.if_show_mask = false;
			ButtonIcon3D addWidget = new ButtonIcon3D( "addWidget" , addWidgetBmp , iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_add_widget ) , addBg );
			addWidgetBmp.recycle();
			addWidgetBmp = null;
			ButtonIcon3D addShortcut = new ButtonIcon3D( "addShortcut" , addShortcutBmp , iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_add_shortcut ) , addBg );
			addShortcutBmp.recycle();
			addShortcutBmp = null;
			ButtonIcon3D addApp = new ButtonIcon3D( "addApp" , addAppBmp , iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_add_app ) , addBg );
			addAppBmp.recycle();
			addAppBmp = null;
			ButtonIcon3D addFolder = new ButtonIcon3D( "addFolder" , addFolderBmp , iLoongLauncher.getInstance().getString( RR.string.title_destop_edit_add_folder ) , addBg );
			addFolderBmp.recycle();
			addFolderBmp = null;
			menuItems.clear();
			menuItems.add( addWidget );
			menuItems.add( addShortcut );
			menuItems.add( addApp );
			menuItems.add( addFolder );
			this.setButtons( menuItems , 4 , 1 , Utils3D.getScreenWidth() , R3D.pop_menu_height - R3D.pop_menu_title_height );
			this.setCurrentPage( 0 );
			cur_tab = TAB_ADD;
			DefaultLayout.if_show_cover = true;
			DefaultLayout.if_show_mask = true;
			return true;
		}
		
		@Override
		public boolean pageFinalize()
		{
			for( int i = 0 ; i < this.getCurrList().size() ; i++ )
			{
				DesktopEditHost.disposeAllViews( this.getCurrList().get( i ) );
			}
			this.getCurrList().clear();
			this.removeAllViews();
			return true;
		}
	}
}
