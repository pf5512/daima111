package com.iLoong.launcher.Desktop3D;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.HotSeatMainGroup;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.NewspageSettingActivity;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.app.AirDefaultLayout;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.CooeePluginInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.smartLaytout.SLConfigHandler;
import com.iLoong.launcher.smartLaytout.SLLayout;
import com.iLoong.launcher.theme.ThemeManager;


class ShortcutItem
{
	
	int id;
	String name;
	String pkgName;
	String className;
	String imageName;
	ShortcutInfo info;
	boolean isContact;
	int cellX;
	int cellY;
	int folderId;
	ArrayList<String> pkgNameArray;
	ArrayList<String> classNameArray;
}

class FolderList
{
	
	int id;
	String name;
	int repeat_count;
	int cellX;
	int cellY;
	String iconResource;
	ArrayList<ShortcutItem> shortcutList;
}

class ShortcutGRP
{
	
	String locate;
	int value;
	int id;
	ArrayList<ShortcutItem> shortcutList;
	ArrayList<FolderList> folderList;
}

class WidgetItem
{
	
	String locate;
	int value;
	int id;
	String name;
	String pkgName;
	String imageName;
	String apkName;
	String customID;
	int minWidth;
	int minHeight;
	int spanX;
	int spanY;
	int cellX;
	int cellY;
	boolean hasInstall = false;
	boolean addDesktop;
	ResolveInfo resolveInfo;
	boolean fromAirpush = false;
	boolean loadByInternal = false;
	String className;
	String thumbName;
}

class VirtureIcon
{
	
	String locate;
	int value;
	int x;
	int y;
	String name;
	String pkgName;
	String className;
	String imageName;
	String apkName;
	String customID;
	ShortcutInfo info;
	FolderList folder;
	int cellX;
	int cellY;
	boolean fromAirPush = false;
}

class ApplistVirtualIcon
{
	
	String name;
	String packageName;
	String title;
	String imageName;
}

class DefaultIcon
{
	
	String title;
	String pkgName;
	String imageName;
	String className;
	boolean duplicate;
	boolean dealed;
	ArrayList<String> pkgNameArray;
	ArrayList<String> classNameArray;
}

class DynamicIcon
{
	
	String title;
	String pkgName;
	String compName;
	String[] pkgNameArray;
	String[] compNameArray;
}

class DefaultShortcutIcon
{
	
	String title;
	String imageName;
	String pkgName;
	String className;
	String[] pkgNameArray;
	String[] compNameArray;
}

class SysWidget
{
	
	String packageName;
	String className;
	int screen;
	int cellX;
	int cellY;
	int spanX;
	int spanY;
}

class SysShortcut
{
	
	String name;
	String packageName;
	String className;
	String imageName;
	int screen;
	int cellX;
	int cellY;
}

class CooeePlugin
{
	
	String name;
	String pkgName;
	int screen;
	boolean fullScreen = false;
}

class FactoryApp
{
	
	int id;
	String packageName;
	String className;
	ArrayList<String> pkgNameArray;
}

class MediaSeatIcon
{
	
	String title;
	String pkgName;
	String className;
	String imageName;
}

class DefaultLayoutHandler extends DefaultHandler
{
	
	int curShortGRP;
	int curFolderId;
	int curWidget;
	// boolean not_allowed_add_into_allWidget = false;
	// int curHotSeat;
	int curApp;
	FolderList curFolder;
	static final String TAG_DEFAULT_LAYOUT = "default_layout";
	static final String TAG_SHORTGRP = "shortcutgroup";
	static final String TAG_ITEM = "item";
	static final String TAG_APP_SORT = "app_sort";
	static final String TAG_SHOW_APP = "show_app";
	static final String TAG_HIDE_APP = "hide_app";
	static final String TAG_HIDE_WIDGET = "hide_widget";
	public static final String TAG_APP = "app";
	public static final String TAG_WIDGET = "widget";
	public static final String TAG_FOLDER = "folder";
	public static final String TAG_VIRTURE = "virtueIcon";
	public static final String TAG_SYSWIDGET = "syswidget";
	public static final String TAG_SYS_SHORTCUT = "sys_shortcut";
	public static final String TAG_APPLIST_VIRTUAL_ICON = "applist_virtual_icon";
	public static final String TAG_COOEE_PLUGIN = "cooee_plugin";
	public static final String TAG_DESKTOP_EFFECT_LIST = "desktop_effect_list";
	public static final String TAG_MAINMENU_EFFECT_LIST = "mainmenu_effect_list";
	public static final String MM_SETTING = "mm_settting";
	public static final String IDLE_PAGE_TRANSFORM_OVER_MSG = "idle_page_transform_over_msg";
	public static final String GENERAl_CONFIG = "general_config";
	public static final String HOTSEAT_ITEM = "hotseat";
	private static final String ICON_DEFAULT_FOLDER = "theme/uishowfolder/";
	public boolean parseThemeConfig = false;
	private List<String> themeConfigTag = new ArrayList<String>();
	
	public DefaultLayoutHandler()
	{
		this.curShortGRP = 0;
		this.curFolderId = -1;
		this.curWidget = 0;
		// this.curHotSeat = 0;
		this.curApp = 0;
		parseThemeConfig = false;
		themeConfigTag.add( "icon" );
		themeConfigTag.add( "dynamicicon" );
	}
	
	public void startDocument() throws SAXException
	{
		// Utils3D.showPidMemoryInfo("startDocument");
	}
	
	public void endDocument() throws SAXException
	{
		// Utils3D.showPidMemoryInfo("endDocument");
	}
	
	public void startElement(
			String namespaceURI ,
			String localName ,
			String qName ,
			Attributes atts ) throws SAXException
	{
		if( parseThemeConfig && !themeConfigTag.contains( localName ) )
		{
			return;
		}
		// Utils3D.showPidMemoryInfo("mem1");
		if( localName.equals( TAG_DEFAULT_LAYOUT ) )
		{
			;
		}
		else if( localName.equals( TAG_SHORTGRP ) )
		{
			ShortcutGRP grp = new ShortcutGRP();
			grp.id = Integer.valueOf( atts.getValue( "id" ) );
			grp.locate = atts.getValue( "locate" );
			if( atts.getValue( "locate_value" ) != null )
				grp.value = Integer.valueOf( atts.getValue( "locate_value" ) );
			DefaultLayout.allShortcutList.add( this.curShortGRP , grp );
		}
		else if( localName.equals( HOTSEAT_ITEM ) )
		{
			String hotImageName = null;
			String hotIntent = null;
			int hotid = 0;
			hotImageName = atts.getValue( "image" );
			if( hotImageName != null )
			{
				hotImageName = DefaultLayout.HOTSEAT_PATH + hotImageName;
			}
			hotIntent = atts.getValue( "hotseatIntent" );
			hotid = Integer.valueOf( atts.getValue( "id" ) );
			iLoongLauncher.getInstance().initDefaultHotseatItem( hotid , hotIntent );
			iLoongLauncher.getInstance().initHotseatItem( hotid , hotImageName , hotIntent );
		}
		else if( localName.equals( GENERAl_CONFIG ) )
		{
			String temp;
			// from feature_config start
			temp = atts.getValue( "enable_themebox" );
			// xujia add
			temp = atts.getValue( "media_view_black_bg" );
			if( temp != null )
			{
				DefaultLayout.media_view_black_bg = temp.equals( "true" );
			}
			temp = atts.getValue( "media_view_dispose_bg_alpha" );
			if( temp != null )
			{
				DefaultLayout.media_view_dispose_bg_alpha = Float.valueOf( temp );
			}
			// xujia add end
			if( temp != null )
			{
				DefaultLayout.enable_themebox = temp.equals( "true" );
			}
			// xiatian add start //New Requirement 20130507
			temp = atts.getValue( "enable_WallpaperBox" );
			if( temp != null )
			{
				DefaultLayout.enable_WallpaperBox = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_SceneBox" );
			if( temp != null )
			{
				DefaultLayout.enable_SceneBox = temp.equals( "true" );
			}
			// xiatian add end
			temp = atts.getValue( "default_theme_package_name" );
			if( temp == null )
			{
				DefaultLayout.default_theme_package_name = null;
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.default_theme_package_name = null;
				}
				else
				{
					DefaultLayout.default_theme_package_name = temp;
				}
			}
			temp = atts.getValue( "use_new_theme" );
			if( temp != null )
			{
				DefaultLayout.use_new_theme = temp.equals( "true" );
			}
			temp = atts.getValue( "lite_edition" );
			if( temp != null )
			{
				DefaultLayout.lite_edition = temp.equals( "true" );
				if( DefaultLayout.lite_edition )
				{
					DefaultLayout.enable_themebox = false;
					DefaultLayout.use_new_theme = true;
				}
			}
			// from feature_config end
			temp = atts.getValue( "default_explorer" );
			if( temp == null )
			{
				DefaultLayout.default_explorer = null;
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.default_explorer = null;
				}
				else
				{
					DefaultLayout.default_explorer = temp;
				}
			}
			temp = atts.getValue( "default_uri" );
			if( temp == null )
			{
				DefaultLayout.defaultUri = null;
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.defaultUri = null;
				}
				else
				{
					DefaultLayout.defaultUri = temp;
				}
			}
			temp = atts.getValue( "install_change_wallpaper" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.install_change_wallpaper = false;
			}
			else
			{
				DefaultLayout.install_change_wallpaper = true;
			}
			temp = atts.getValue( "hide_status_bar" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_status_bar = false;
			}
			else
			{
				DefaultLayout.hide_status_bar = true;
			}
			temp = atts.getValue( "hide_online_theme_button" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_online_theme_button = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_online_theme_button = false;
			}
			temp = atts.getValue( "anti_aliasing" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.anti_aliasing = true;
			}
			else
			{
				DefaultLayout.anti_aliasing = false;
			}
			temp = atts.getValue( "enable_scroll_to_widget" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.enable_scroll_to_widget = true;
			}
			else
			{
				DefaultLayout.enable_scroll_to_widget = false;
			}
			temp = atts.getValue( "dispose_cell_count" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.dispose_cell_count = true;
			}
			else
			{
				DefaultLayout.dispose_cell_count = false;
			}
			if( atts.getValue( "cellCountX" ) != null )
				DefaultLayout.cellCountX = Integer.valueOf( atts.getValue( "cellCountX" ) );
			if( atts.getValue( "cellCountY" ) != null )
				DefaultLayout.cellCountY = Integer.valueOf( atts.getValue( "cellCountY" ) );
			temp = atts.getValue( "appbar_bag_icon" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.appbar_bag_icon = true;
			}
			else
			{
				DefaultLayout.appbar_bag_icon = false;
			}
			temp = atts.getValue( "display_widget_preview_hole" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.display_widget_preview_hole = true;
			}
			else
			{
				DefaultLayout.display_widget_preview_hole = false;
			}
			if( atts.getValue( "default_home_page" ) != null )
				DefaultLayout.default_home_page = Integer.valueOf( atts.getValue( "default_home_page" ) );
			if( atts.getValue( "default_workspace_pagecounts" ) != null )
				DefaultLayout.default_workspace_pagecounts = Integer.valueOf( atts.getValue( "default_workspace_pagecounts" ) );
			if( atts.getValue( "default_workspace_pagecount_min" ) != null )
				DefaultLayout.default_workspace_pagecount_min = Integer.valueOf( atts.getValue( "default_workspace_pagecount_min" ) );
			if( atts.getValue( "default_workspace_pagecount_max" ) != null )
				DefaultLayout.default_workspace_pagecount_max = Integer.valueOf( atts.getValue( "default_workspace_pagecount_max" ) );
			temp = atts.getValue( "disable_shake_wallpaper" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_shake_wallpaper = true;
			}
			else
			{
				DefaultLayout.disable_shake_wallpaper = false;
			}
			temp = atts.getValue( "default_open_shake_wallpaper" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.default_open_shake_wallpaper = true;
			}
			else
			{
				DefaultLayout.default_open_shake_wallpaper = false;
			}
			temp = atts.getValue( "disable_shake_change_theme" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_shake_change_theme = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.disable_shake_change_theme = false;
			}
			temp = atts.getValue( "default_open_shake_theme" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.default_open_shake_theme = true;
			}
			else
			{
				DefaultLayout.default_open_shake_theme = false;
			}
			temp = atts.getValue( "appbar_show_userapp_list" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.appbar_show_userapp_list = true;
			}
			else
			{
				DefaultLayout.appbar_show_userapp_list = false;
			}
			if( atts.getValue( "icon_title_font" ) != null )
				DefaultLayout.icon_title_font = Integer.valueOf( atts.getValue( "icon_title_font" ) );
			if( atts.getValue( "widget_title_weight" ) != null )
				DefaultLayout.widget_title_weight = Float.valueOf( atts.getValue( "widget_title_weight" ) );
			temp = atts.getValue( "show_page_edit_on_key_back" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.show_page_edit_on_key_back = true;
			}
			else
			{
				DefaultLayout.show_page_edit_on_key_back = false;
			}
			temp = atts.getValue( "disable_x_effect" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_x_effect = true;
			}
			else
			{
				DefaultLayout.disable_x_effect = false;
			}
			temp = atts.getValue( "loadapp_in_background" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.loadapp_in_background = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.loadapp_in_background = false;
			}
			temp = atts.getValue( "enable_icon_effect" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.enable_icon_effect = true;
			}
			else
			{
				DefaultLayout.enable_icon_effect = false;
			}
			if( atts.getValue( "mainmenu_page_effect_id" ) != null )
				DefaultLayout.mainmenu_page_effect_id = Integer.valueOf( atts.getValue( "mainmenu_page_effect_id" ) );
			if( atts.getValue( "desktop_page_effect_id" ) != null )
				DefaultLayout.desktop_page_effect_id = Integer.valueOf( atts.getValue( "desktop_page_effect_id" ) );
			temp = atts.getValue( "setupmenu_show_theme" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.setupmenu_show_theme = false;
			}
			else
			{
				DefaultLayout.setupmenu_show_theme = true;
			}
			temp = atts.getValue( "setupmenu_yitong" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.setupmenu_yitong = true;
			}
			else
			{
				DefaultLayout.setupmenu_yitong = false;
			}
			temp = atts.getValue( "custom_wallpapers_path" );
			if( temp == null )
			{
				DefaultLayout.custom_wallpapers_path = "";
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.custom_wallpapers_path = "";
				}
				else
				{
					DefaultLayout.custom_wallpapers_path = temp;
				}
			}
			temp = atts.getValue( "custom_default_wallpaper_name" );
			if( temp == null )
			{
				DefaultLayout.custom_default_wallpaper_name = "";
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.custom_default_wallpaper_name = "";
				}
				else
				{
					DefaultLayout.custom_default_wallpaper_name = temp;
				}
			}
			temp = atts.getValue( "release_memory_after_pause" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.release_memory_after_pause = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.release_memory_after_pause = false;
			}
			temp = atts.getValue( "thirdapk_icon_scaleFactor" );
			if( temp != null )
			{
				DefaultLayout.thirdapk_icon_scaleFactor = Integer.valueOf( temp ) / 100f;
			}
			if( atts.getValue( "hot_dock_icon_number" ) != null )
			{
				DefaultLayout.hot_dock_icon_number = Integer.valueOf( atts.getValue( "hot_dock_icon_number" ) );
				if( DefaultLayout.hot_dock_icon_number != 4 && DefaultLayout.hot_dock_icon_number != 5 )
				{
					DefaultLayout.hot_dock_icon_number = 5;
				}
			}
			if( atts.getValue( "sensor_delay_level" ) != null )
				DefaultLayout.sensor_delay_level = Integer.valueOf( atts.getValue( "sensor_delay_level" ) );
			temp = atts.getValue( "app_icon_size" );
			if( temp == null )
			{
				DefaultLayout.app_icon_size = -1;
			}
			else
			{
				DefaultLayout.app_icon_size = Integer.valueOf( temp );
			}
			temp = atts.getValue( "custom_virtual_path" );
			if( temp == null )
			{
				DefaultLayout.custom_virtual_path = "";
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.custom_virtual_path = "";
				}
				else
				{
					DefaultLayout.custom_virtual_path = temp;
					File dir = new File( DefaultLayout.custom_virtual_path );
					if( dir.exists() )
					{
						DefaultLayout.useCustomVirtual = true;
					}
				}
			}
			temp = atts.getValue( "custom_virtual_download_path" );
			if( temp == null )
			{
				DefaultLayout.custom_virtual_download_path = "";
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.custom_virtual_download_path = "";
				}
				else
				{
					DefaultLayout.custom_virtual_download_path = temp;
					File dir = new File( DefaultLayout.custom_virtual_download_path );
					if( dir.exists() )
					{
						DefaultLayout.useCustomVirtualDownload = true;
					}
				}
			}
			temp = atts.getValue( "custom_sys_shortcut_path" );
			if( temp == null )
			{
				DefaultLayout.custom_sys_shortcut_path = "";
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.custom_sys_shortcut_path = "";
				}
				else
				{
					DefaultLayout.custom_sys_shortcut_path = temp;
					File dir = new File( DefaultLayout.custom_sys_shortcut_path );
					if( dir.exists() )
					{
						DefaultLayout.useCustomSysShortcut = true;
					}
				}
			}
			temp = atts.getValue( "custom_virtual_icon" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.custom_virtual_icon = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.custom_virtual_icon = true;
			}
			temp = atts.getValue( "hide_mainmenu_widget" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_mainmenu_widget = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_mainmenu_widget = true;
			}
			temp = atts.getValue( "hide_launcher_wallpapers" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_launcher_wallpapers = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_launcher_wallpapers = true;
			}
			temp = atts.getValue( "broadcast_state" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.broadcast_state = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.broadcast_state = true;
			}
			temp = atts.getValue( "applist_style_classic" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.applist_style_classic = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.applist_style_classic = true;
			}
			temp = atts.getValue( "hide_appbar" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_appbar = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_appbar = true;
			}
			temp = atts.getValue( "show_font_bg" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.show_font_bg = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.show_font_bg = false;
			}
			temp = atts.getValue( "font_double_line" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.font_double_line = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.font_double_line = false;
			}
			temp = atts.getValue( "hide_backup_and_restore" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_backup_and_restore = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_backup_and_restore = false;
			}
			temp = atts.getValue( "hotseatbar_browser_special_name" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hotseatbar_browser_special_name = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hotseatbar_browser_special_name = false;
			}
			temp = atts.getValue( "click_indicator_enter_pageselect" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.click_indicator_enter_pageselect = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.click_indicator_enter_pageselect = false;
			}
			temp = atts.getValue( "workspace_longclick_display_contacts" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.workspace_longclick_display_contacts = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.workspace_longclick_display_contacts = false;
			}
			temp = atts.getValue( "mainmenu_widget_display_contacts" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.mainmenu_widget_display_contacts = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.mainmenu_widget_display_contacts = false;
			}
			// teapotXu add start for whether display the system widgets in
			// mainmenu widgets view
			temp = atts.getValue( "mainmenu_widget_dispale_sys_widgets" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.mainmenu_widget_dispale_sys_widgets = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.mainmenu_widget_dispale_sys_widgets = false;
			}
			// teapotXu add end
			temp = atts.getValue( "hide_desktop_setup" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_desktop_setup = true;
				DefaultLayout.popmenu_style = 1;// 弹出菜单强制为android4.0风格
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_desktop_setup = false;
			}
			temp = atts.getValue( "enable_service" );
			if( iLoongApplication.BuiltIn == false )
			{
				DefaultLayout.enable_service = true;
			}
			else
			{
				if( temp != null && temp.equals( "true" ) )
				{
					DefaultLayout.enable_service = true;
				}
				else if( temp != null && temp.equals( "false" ) )
				{
					DefaultLayout.enable_service = false;
				}
			}
			temp = atts.getValue( "show_widget_shortcut_bg" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.show_widget_shortcut_bg = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.show_widget_shortcut_bg = false;
			}
			temp = atts.getValue( "widget_shortcut_lefttop" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.widget_shortcut_lefttop = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.widget_shortcut_lefttop = false;
			}
			temp = atts.getValue( "hide_remove_theme_button" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_remove_theme_button = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_remove_theme_button = false;
			}
			temp = atts.getValue( "hotseat_hide_title" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hotseat_hide_title = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hotseat_hide_title = false;
			}
			temp = atts.getValue( "widget_shortcut_title_top" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.widget_shortcut_title_top = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.widget_shortcut_title_top = false;
			}
			temp = atts.getValue( "show_home_button" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.show_home_button = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.show_home_button = false;
			}
			temp = atts.getValue( "setupmenu_show_clear" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.setupmenu_show_clear = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.setupmenu_show_clear = false;
			}
			temp = atts.getValue( "disable_double_click" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_double_click = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.disable_double_click = false;
			}
			temp = atts.getValue( "hide_title_bg_shadow" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hide_title_bg_shadow = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hide_title_bg_shadow = false;
			}
			if( atts.getValue( "title_outline_shadow_size" ) != null )
				DefaultLayout.title_outline_shadow_size = Integer.valueOf( atts.getValue( "title_outline_shadow_size" ) );
			if( atts.getValue( "title_outline_color" ) != null )
				DefaultLayout.title_outline_color = Color.parseColor( atts.getValue( "title_outline_color" ) );
			if( atts.getValue( "title_shadow_color" ) != null )
				DefaultLayout.title_shadow_color = Color.parseColor( atts.getValue( "title_shadow_color" ) );
			temp = atts.getValue( "hotseat_app_title_colorful" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.hotseat_app_title_colorful = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.hotseat_app_title_colorful = false;
			}
			if( atts.getValue( "hotseat_app_title_r" ) != null )
				DefaultLayout.hotseat_app_title_r = Integer.valueOf( atts.getValue( "hotseat_app_title_r" ) );
			if( atts.getValue( "hotseat_app_title_g" ) != null )
				DefaultLayout.hotseat_app_title_g = Integer.valueOf( atts.getValue( "hotseat_app_title_g" ) );
			if( atts.getValue( "hotseat_app_title_b" ) != null )
				DefaultLayout.hotseat_app_title_b = Integer.valueOf( atts.getValue( "hotseat_app_title_b" ) );
			if( atts.getValue( "hotseat_app_title_a" ) != null )
				DefaultLayout.hotseat_app_title_a = Integer.valueOf( atts.getValue( "hotseat_app_title_a" ) );
			if( atts.getValue( "install_apk_delay" ) != null )
				DefaultLayout.install_apk_delay = Integer.valueOf( atts.getValue( "install_apk_delay" ) );
			temp = atts.getValue( "show_missed_call_sms" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.show_missed_call_sms = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.show_missed_call_sms = false;
			}
			temp = atts.getValue( "call_component_name" );
			if( temp != null )
			{
				DefaultLayout.call_component_name = temp;
			}
			// teapotXu add start for missed call && sms numbers shown in
			// appList
			temp = atts.getValue( "show_missed_call_sms_in_appList" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.show_missed_call_sms_in_appList = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.show_missed_call_sms_in_appList = false;
			}
			temp = atts.getValue( "call_component_name_applist" );
			if( temp != null )
			{
				DefaultLayout.call_component_name_applist = temp;
			}
			// teapotXu add end
			temp = atts.getValue( "title_style_bold" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.title_style_bold = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.title_style_bold = false;
			}
			temp = atts.getValue( "disable_theme_preview_tween" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_theme_preview_tween = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.disable_theme_preview_tween = false;
			}
			temp = atts.getValue( "menu_wyd" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.menu_wyd = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.menu_wyd = false;
			}
			temp = atts.getValue( "empty_page_add_reminder" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.empty_page_add_reminder = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.empty_page_add_reminder = false;
			}
			if( atts.getValue( "reminder_font" ) != null )
				DefaultLayout.reminder_font = Integer.valueOf( atts.getValue( "reminder_font" ) );
			temp = atts.getValue( "appbar_widgets_special_name" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.appbar_widgets_special_name = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.appbar_widgets_special_name = false;
			}
			temp = atts.getValue( "disable_vibrator" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_vibrator = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.disable_vibrator = false;
			}
			temp = atts.getValue( "disable_circled" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.disable_circled = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.disable_circled = false;
			}
			temp = atts.getValue( "default_circled_state" );
			if( temp != null && temp.equals( "true" ) && DefaultLayout.disable_circled == false )
			{
				DefaultLayout.default_circled_state = true;
			}
			else
			{
				DefaultLayout.default_circled_state = false;
			}
			// temp = atts.getValue("show_introduction");
			// if (temp != null && temp.equals("true")) {
			// DefaultLayout.show_introduction = true;
			// } else if (temp != null && temp.equals("false")) {
			// DefaultLayout.show_introduction = false;
			// }
			temp = atts.getValue( "default_close_vibrator" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.default_close_vibrator = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.default_close_vibrator = false;
			}
			temp = atts.getValue( "restart_when_orientation_change" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.restartWhenOrientationChange = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.restartWhenOrientationChange = false;
			}
			temp = atts.getValue( "keypad_event_of_focus" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.keypad_event_of_focus = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.keypad_event_of_focus = false;
			}
			temp = atts.getValue( "blend_func_dst_gl_one" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.blend_func_dst_gl_one = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.blend_func_dst_gl_one = false;
			}
			temp = atts.getValue( "reduce_load_priority" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.reduce_load_priority = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.reduce_load_priority = false;
			}
			temp = atts.getValue( "widget_revise_complete" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.widget_revise_complete = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.widget_revise_complete = false;
			}
			temp = atts.getValue( "wallpaper_has_edage" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.wallpaper_has_edage = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.wallpaper_has_edage = false;
			}
			temp = atts.getValue( "mainmenu_longclick_pageedit" );
			if( temp != null )
			{
				DefaultLayout.mainmenu_longclick_pageedit = temp.equals( "true" );
			}
			temp = atts.getValue( "hotseat_disable_make_folder" );
			if( temp != null )
			{
				DefaultLayout.hotseat_disable_make_folder = temp.equals( "true" );
			}
			temp = atts.getValue( "hotseat_title_disable_click" );
			if( temp != null )
			{
				DefaultLayout.hotseat_title_disable_click = temp.equals( "true" );
			}
			temp = atts.getValue( "bjx_sale_log" );
			if( temp != null )
			{
				DefaultLayout.bjx_sale_log = temp.equals( "true" );
			}
			temp = atts.getValue( "add_desktop_list_font_set_color" );
			if( temp != null )
			{
				DefaultLayout.add_desktop_list_font_set_color = temp.equals( "true" );
			}
			if( atts.getValue( "add_desktop_list_font_color" ) != null )
				DefaultLayout.add_desktop_list_font_color = Color.parseColor( atts.getValue( "add_desktop_list_font_color" ) );
			temp = atts.getValue( "desktop_hide_frame" );
			if( temp != null )
			{
				DefaultLayout.desktop_hide_frame = temp.equals( "true" );
			}
			temp = atts.getValue( "appbar_font_bigger" );
			if( temp != null )
			{
				DefaultLayout.appbar_font_bigger = temp.equals( "true" );
			}
			temp = atts.getValue( "disable_move_wallpaper" );
			if( temp != null )
			{
				DefaultLayout.disable_move_wallpaper = temp.equals( "true" );
			}
			temp = atts.getValue( "mainmenu_inout_no_anim" );
			if( temp != null )
			{
				DefaultLayout.mainmenu_inout_no_anim = temp.equals( "true" );
			}
			temp = atts.getValue( "folder_no_dragon" );
			if( temp != null )
			{
				DefaultLayout.folder_no_dragon = temp.equals( "true" );
			}
			if( atts.getValue( "page_tween_time" ) != null )
				DefaultLayout.page_tween_time = Float.valueOf( atts.getValue( "page_tween_time" ) );
			temp = atts.getValue( "show_icon_size" );
			if( temp != null )
			{
				DefaultLayout.show_icon_size = temp.equals( "true" );
			}
			// teapotXu add start
			temp = atts.getValue( "hide_big_icon_size" );
			if( temp != null )
			{
				DefaultLayout.hide_big_icon_size = temp.equals( "true" );
				// if(Utils3D.getScreenWidth() > 480){
				// //对于大屏幕的手机，此开关无效，因为大屏手机的最佳icon size 是大号的Icon
				// DefaultLayout.hide_big_icon_size = false;
				// }
			}
			// teapotXu add end
			temp = atts.getValue( "optimize_hotseat_scroll_back" );
			if( temp != null )
			{
				DefaultLayout.optimize_hotseat_scroll_back = temp.equals( "true" );
			}
			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			temp = atts.getValue( "show_sensor" );
			if( temp != null )
			{
				DefaultLayout.show_sensor = temp.equals( "true" );
			}
			// xujin news
			temp = atts.getValue( "enable_news" );
			if( temp != null )
			{
				DefaultLayout.enable_news = temp.equals( "true" );
				// 4.0以下不支持
				if( Build.VERSION.SDK_INT < 14 )
				{
					DefaultLayout.enable_news = false;
				}
			}
			if( DefaultLayout.enable_google_version )
			{
				DefaultLayout.enable_news = false;
			}
			temp = atts.getValue( "enable_show_widgetzoom" );
			if( temp != null )
			{
				DefaultLayout.enable_show_widgetzoom = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_news_config" );
			if( temp != null )
			{
				DefaultLayout.show_news_page_enable_config = temp.equals( "true" );
			}
			if( DefaultLayout.enable_google_version )
			{
				DefaultLayout.show_news_page_enable_config = false;
			}
			temp = atts.getValue( "enable_quick_search" );
			if( temp != null )
			{
				DefaultLayout.enable_quick_search = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_show_appbar" );
			if( temp != null )
			{
				DefaultLayout.enable_show_appbar = temp.equals( "true" );
			}
			if( DefaultLayout.enable_google_version )
			{
				DefaultLayout.enable_show_appbar = false;
			}
			temp = atts.getValue( "enable_news_config_default" );
			if( temp != null )
			{
				DefaultLayout.show_news_page_config_default_value = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_news_handle" );
			if( temp != null )
			{
				DefaultLayout.enable_news_handle = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_news_back" );
			if( temp != null )
			{
				DefaultLayout.enable_news_back = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_news_shake" );
			if( temp != null )
			{
				DefaultLayout.enable_news_shake = temp.equals( "true" );
			}
			// xujin camera
			temp = atts.getValue( "enable_camera" );
			if( temp != null )
			{
				DefaultLayout.enable_camera = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_camera_config" );
			if( temp != null )
			{
				DefaultLayout.show_camera_page_enable_config = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_camera_config_default" );
			if( temp != null )
			{
				DefaultLayout.show_camera_page_config_default_value = temp.equals( "true" );
			}
			//
			if( DefaultLayout.show_sensor )
			{
				temp = atts.getValue( "default_open_sensor" );
				if( temp != null )
				{
					DefaultLayout.default_open_sensor = temp.equals( "true" );
				}
			}
			// xiatian add end
			// temp = atts.getValue("enable_air_default_layout");
			// if (temp != null) {
			// DefaultLayout.enable_air_default_layout = temp.equals("true");
			// }
			/***************** added by zhenNan.ye begin *******************/
			temp = atts.getValue( "enable_particle" );
			if( temp != null )
			{
				DefaultLayout.enable_particle = temp.equals( "true" );
			}
			temp = atts.getValue( "default_particle_settings_value" );
			if( temp != null )
			{
				DefaultLayout.default_particle_settings_value = temp.equals( "true" );
			}
			/***************** added by zhenNan.ye end *******************/
			temp = atts.getValue( "enable_new_particle" );
			if( temp != null )
			{
				DefaultLayout.enable_new_particle = temp.equals( "true" );
				if( DefaultLayout.enable_new_particle )
				{
					DefaultLayout.enable_particle = false;
				}
			}
			temp = atts.getValue( "particle_scroll_distance" );
			if( temp != null )
				DefaultLayout.particle_scroll_distance = Integer.valueOf( temp );
			temp = atts.getValue( "particle_scroll_distance" );
			temp = atts.getValue( "particle_max_num" );
			if( temp != null )
				ConfigBase.particle_max_num = Integer.valueOf( temp );
			if( ConfigBase.particle_max_num < 1 )
			{
				ConfigBase.particle_max_num = 1;
			}
			// xiatian add start //is_demo_version
			temp = atts.getValue( "is_demo_version" );
			if( temp != null )
			{
				DefaultLayout.is_demo_version = temp.equals( "true" );
				if( DefaultLayout.is_demo_version )
				{
					DefaultLayout.popmenu_style = SetupMenu.POPMENU_STYLE_ORIGINAL;
				}
			}
			// xiatian add end
			// teapotXu add start for NewAddedApp Flag
			temp = atts.getValue( "enable_new_add_app_flag" );
			if( temp != null )
			{
				DefaultLayout.enable_new_add_app_flag = temp.equals( "true" );
			}
			// teapotXu add end
			// //teapotXu add start for Folder in Mainmenu
			// temp = atts.getValue("mainmenu_folder_function");
			// if (temp!=null && temp.equals("true")) {
			// DefaultLayout.mainmenu_folder_function = true;
			// } else if (temp!=null && temp.equals("false")) {
			// DefaultLayout.mainmenu_folder_function = false;
			// }
			// //teapotXu add end for Folder in Mainmenu
			// teapotXu add start for pages effects in Mainmenu
			temp = atts.getValue( "external_applist_page_effect" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.external_applist_page_effect = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.external_applist_page_effect = false;
			}
			// teapotXu add end
			temp = atts.getValue( "enable_hotseat_middle_icon_horizontal" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.enable_hotseat_middle_icon_horizontal = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.enable_hotseat_middle_icon_horizontal = false;
			}
			// teapotXu add start for
			temp = atts.getValue( "enhance_generate_mainmenu_folder_condition" );
			if( temp != null )
			{
				DefaultLayout.enhance_generate_mainmenu_folder_condition = temp.equals( "true" );
			}
			// teapotXu add end
			// zhujieping add begin
			temp = atts.getValue( "miui_v5_folder" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.miui_v5_folder = true;
			}
			else
			{
				DefaultLayout.miui_v5_folder = false;
			}
			if( DefaultLayout.miui_v5_folder )
			{
				temp = atts.getValue( "blur_enable" );
				if( temp != null && temp.equals( "true" ) )
				{
					DefaultLayout.blur_enable = true;
				}
				else
				{
					DefaultLayout.blur_enable = false;
				}
				temp = atts.getValue( "blur_bg_enable" );
				if( temp != null )
				{
					DefaultLayout.blur_bg_enable = temp.equals( "true" );
				}
				temp = atts.getValue( "fboScale" );
				if( temp != null )
					DefaultLayout.fboScale = Float.valueOf( temp );
				temp = atts.getValue( "blurInterate" );
				if( temp != null )
					DefaultLayout.blurInterate = Integer.valueOf( temp );
				temp = atts.getValue( "blurRadius" );
				if( temp != null )
					DefaultLayout.blurRadius = Float.valueOf( temp );
				temp = atts.getValue( "blurDuration" );
				if( temp != null )
					DefaultLayout.blurDuration = Float.valueOf( temp );
				temp = atts.getValue( "maskOpacity" );
				if( temp != null )
					DefaultLayout.maskOpacity = Float.valueOf( temp );
			}
			// zhujieping add end
			// teapotXu add start for HotSeatBar icons Same spacing
			temp = atts.getValue( "same_spacing_btw_hotseat_icons" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.same_spacing_btw_hotseat_icons = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.same_spacing_btw_hotseat_icons = false;
			}
			// teapotXu add end for hotSeatBar icons same Spacing
			temp = atts.getValue( "enable_DesktopIndicatorScroll" );
			if( temp != null )
			{
				DefaultLayout.enable_DesktopIndicatorScroll = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_AppListIndicatorScroll" );
			if( temp != null )
			{
				DefaultLayout.enable_AppListIndicatorScroll = temp.equals( "true" );
			}
			// teapotXu add start
			if( atts.getValue( "popmenu_style" ) != null )
				DefaultLayout.popmenu_style = Integer.valueOf( atts.getValue( "popmenu_style" ) );
			temp = atts.getValue( "butterfly_style" );
			if( temp != null )
			{
				DefaultLayout.butterfly_style = temp.equals( "true" );
				if( DefaultLayout.butterfly_style == true )
				{
					DefaultLayout.default_workspace_pagecount_max = 7;
				}
			}
			temp = atts.getValue( "workspace_npages_circle_scroll_config" );
			if( temp != null )
			{
				DefaultLayout.workspace_npages_circle_scroll_config = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_configmenu_for_move_wallpaper" );
			if( temp != null )
			{
				DefaultLayout.enable_configmenu_for_move_wallpaper = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_desktopsettings_menu_style" );
			if( temp != null )
			{
				DefaultLayout.enable_desktopsettings_menu_style = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_doov_spec_customization" );
			if( temp != null )
			{
				DefaultLayout.enable_doov_spec_customization = temp.equals( "true" );
			}
			// teapotXu add end
			// xiatian add start //add icon shadow
			temp = atts.getValue( "icon_shadow_radius" );
			if( temp != null )
			{
				DefaultLayout.icon_shadow_radius = Float.valueOf( temp );
			}
			// xiatian add end
			temp = atts.getValue( "enable_texture_pack" );
			if( temp != null )
			{
				DefaultLayout.enable_texture_pack = temp.equals( "true" );
			}
			// huwenhao
			temp = atts.getValue( "enable_auto_update" );
			if( temp != null )
			{
				DefaultLayout.enable_auto_update = temp.equals( "true" );
			}
			// xiatian add start //mainmenu_background_alpha_progress
			temp = atts.getValue( "mainmenu_background_alpha_progress" );
			if( temp != null )
			{
				DefaultLayout.mainmenu_background_alpha_progress = temp.equals( "true" );
				if( DefaultLayout.mainmenu_background_alpha_progress )
				{
					temp = atts.getValue( "mainmenu_background_default_alpha" );
					if( temp != null )
						DefaultLayout.mainmenu_background_default_alpha = Integer.parseInt( temp );
				}
			}
			// this on-off is depended by mainmenu_background_alpha_progress
			temp = atts.getValue( "mainmenu_background_translucent" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.mainmenu_background_translucent = true;
			}
			else
			{
				DefaultLayout.mainmenu_background_translucent = false;
			}
			// xiatian add end
			temp = atts.getValue( "clock_widget_scrollV" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.clock_widget_scrollV = true;
				ConfigBase.clock_widget_scrollV = DefaultLayout.clock_widget_scrollV;
			}
			else
			{
				DefaultLayout.clock_widget_scrollV = false;
				ConfigBase.clock_widget_scrollV = DefaultLayout.clock_widget_scrollV;
			}
			temp = atts.getValue( "widget_scrollV_pkgs" );
			if( temp != null && !temp.equals( "nothing" ) )
			{
				ConfigBase.widget_scrollV_pkgs = temp;
			}
			temp = atts.getValue( "widget_click_opt" );
			if( temp != null && temp.equals( "true" ) )
			{
				ConfigBase.widget_click_opt = true;
			}
			else
			{
				ConfigBase.widget_click_opt = false;
			}
			temp = atts.getValue( "page_container_shown" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.page_container_shown = true;
			}
			else
			{
				DefaultLayout.page_container_shown = false;
			}
			temp = atts.getValue( "enable_desktop_longclick_to_add_widget" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.enable_desktop_longclick_to_add_widget = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.enable_desktop_longclick_to_add_widget = false;
			}
			temp = atts.getValue( "generate_new_folder_in_top_trash_bar" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.generate_new_folder_in_top_trash_bar = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.generate_new_folder_in_top_trash_bar = false;
			}
			temp = atts.getValue( "send_msg_in_appList_onWindowFocusChanged" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.send_msg_in_appList_onWindowFocusChanged = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.send_msg_in_appList_onWindowFocusChanged = false;
			}
			temp = atts.getValue( "enable_workspace_miui_edit_mode" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.enable_workspace_miui_edit_mode = true;
			}
			else if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.enable_workspace_miui_edit_mode = false;
			}
			// teapotXu add end
			temp = atts.getValue( "enable_hotseat_rolling" );// zjp
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.enable_hotseat_rolling = false;
				DefaultLayout.newHotSeatMainGroup = false;
			}
			else
			{
				DefaultLayout.enable_hotseat_rolling = true;
			}
			// xiatian add start //EffectPreview
			temp = atts.getValue( "enable_effect_preview" );
			if( temp != null )
			{
				DefaultLayout.enable_effect_preview = temp.equals( "true" );
			}
			// xiatian add end
			// zjp add
			temp = atts.getValue( "enable_scene_wallpaper" );
			if( temp != null )
			{
				DefaultLayout.enable_scene_wallpaper = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_edit_mode_function" );
			if( temp != null )
			{
				DefaultLayout.enable_edit_mode_function = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_takein_workspace_by_longclick" );
			if( temp != null )
			{
				DefaultLayout.enable_takein_workspace_by_longclick = temp.equals( "true" );
			}
			temp = atts.getValue( "diable_enter_applist_when_takein_mode" );
			if( temp != null )
			{
				DefaultLayout.diable_enter_applist_when_takein_mode = temp.equals( "true" );
			}
			temp = atts.getValue( "dynamic_icon" );// pchong
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.dynamic_icon = false;
			}
			else
			{
				DefaultLayout.dynamic_icon = true;
			}
			temp = atts.getValue( "custom_assets_path" );
			if( temp != null )
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.custom_assets_path = "";
				}
				else
				{
					DefaultLayout.custom_assets_path = temp;
					File dir = new File( DefaultLayout.custom_assets_path );
					if( dir.exists() )
					{
						DefaultLayout.useCustomAssets = true;
					}
				}
			}
			// teapotXu add start for no radom style in page changed effect
			temp = atts.getValue( "page_effect_no_radom_style" );
			if( temp != null )
			{
				DefaultLayout.page_effect_no_radom_style = temp.equals( "true" );
			}
			// teapotXu add end
			temp = atts.getValue( "fast_change_theme" );
			if( temp != null )
			{
				DefaultLayout.fast_change_theme = temp.equals( "true" );
				if( DefaultLayout.fast_change_theme )
				{
					DefaultLayout.enable_texture_pack = false;
				}
			}
			temp = atts.getValue( "dingzhihomekey" );
			if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.dingzhihomekey = true;
			}
			else
			{
				DefaultLayout.dingzhihomekey = false;
			}
			temp = atts.getValue( "popmenu_gravity_right_when_special_language" );
			if( temp != null )
			{
				DefaultLayout.popmenu_gravity_right_when_special_language = temp.equals( "true" );
			}
			temp = atts.getValue( "show_icon_size_different_layout" );
			if( temp != null )
			{
				DefaultLayout.show_icon_size_different_layout = temp.equals( "true" );
			}
			// xiatian add start //HotSeat3DShake
			temp = atts.getValue( "enable_HotSeat3DShake" );
			if( temp != null )
			{
				DefaultLayout.enable_HotSeat3DShake = temp.equals( "true" ) && DefaultLayout.enable_hotseat_rolling;
			}
			// xiatian add end
			temp = atts.getValue( "desktop_setting_style_white" );
			// zjp
			if( temp != null )
			{
				DefaultLayout.desktop_setting_style_white = temp.equals( "true" );
			}
			// zjp add
			temp = atts.getValue( "popup_menu_no_background_shadow" );
			if( temp != null )
			{
				DefaultLayout.popup_menu_no_background_shadow = temp.equals( "true" );
			}
			temp = atts.getValue( "wallpapers_from_apk_packagename" );
			if( temp == null )
			{
				DefaultLayout.wallpapers_from_apk_packagename = "";
			}
			else
			{
				if( temp.equals( "nothing" ) )
				{
					DefaultLayout.wallpapers_from_apk_packagename = "";
				}
				else
				{
					DefaultLayout.wallpapers_from_apk_packagename = temp;
				}
			}
			temp = atts.getValue( "scene_menu_text_size" );
			if( temp != null )
			{
				DefaultLayout.scene_menu_text_size = temp;
			}
			temp = atts.getValue( "reflect_change_statusbar" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.reflect_change_statusbar = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.reflect_change_statusbar = true;
			}
			temp = atts.getValue( "screen_mess" );
			if( temp != null && temp.equals( "false" ) )
			{
				DefaultLayout.screen_mess = false;
			}
			else if( temp != null && temp.equals( "true" ) )
			{
				DefaultLayout.screen_mess = true;
			}
			temp = atts.getValue( "setupmenu_android4_with_no_icons" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_android4_with_no_icons = temp.equals( "true" );
			}
			temp = atts.getValue( "big_icon_hot_dock_icon_number_always_4" );
			if( temp != null )
			{
				DefaultLayout.big_icon_hot_dock_icon_number_always_4 = temp.equals( "true" );
			}
			temp = atts.getValue( "setupmenu_by_system" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_by_system = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_cooee_log" );
			if( temp != null )
			{
				Log.setEnableLog( temp.equals( "true" ) );
			}
			temp = atts.getValue( "pageEditor_back_icon_shown" );
			if( temp != null )
			{
				DefaultLayout.pageEditor_back_icon_shown = temp.equals( "true" );
			}
			temp = atts.getValue( "pageIndicator_scroll_num_shown" );
			if( temp != null )
			{
				DefaultLayout.pageIndicator_scroll_num_shown = temp.equals( "true" );
			}
			temp = atts.getValue( "huaqin_enable_edit_mode" );
			if( temp != null )
			{
				DefaultLayout.huaqin_enable_edit_mode = temp.equals( "true" );
				if( DefaultLayout.huaqin_enable_edit_mode )
					DefaultLayout.enable_edit_mode_function = true;
			}
			temp = atts.getValue( "desk_menu_add_OnKeyLove_item" );
			if( temp != null )
			{
				DefaultLayout.desk_menu_add_OnKeyLove_item = temp.equals( "true" );
			}
			temp = atts.getValue( "desk_menu_change_SystemWidget_to_OnKeyLove" );
			if( temp != null )
			{
				DefaultLayout.desk_menu_change_SystemWidget_to_OnKeyLove = temp.equals( "true" );
			}
			temp = atts.getValue( "widget_preview_title_span_offsetX" );
			if( temp != null )
			{
				DefaultLayout.widget_preview_title_span_offsetX = Integer.valueOf( temp );
			}
			temp = atts.getValue( "enable_apply_saved_cur_screen_when_reboot" );
			if( temp != null )
			{
				DefaultLayout.enable_apply_saved_cur_screen_when_reboot = temp.equals( "true" );
			}
			temp = atts.getValue( "customer_vanzo_operations" );
			if( temp != null )
			{
				DefaultLayout.customer_vanzo_operations = temp.equals( "true" );
			}
			temp = atts.getValue( "pause_egl_helper" );
			if( temp != null )
			{
				DefaultLayout.pauseEglHelper = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_deletefolder_when_drag_no_icon_or_one" );
			if( temp != null )
			{
				DefaultLayout.enable_deletefolder = temp.equals( "true" );
			}
			temp = atts.getValue( "setupmenu_idle_wallpaper_shown" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_idle_wallpaper_shown = temp.equals( "true" );
			}
			temp = atts.getValue( "setupmenu_idle_sofwareManager_shown" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_idle_sofwareManager_shown = temp.equals( "true" );
			}
			temp = atts.getValue( "setupmenu_idle_systemWidget_shown" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_idle_systemWidget_shown = temp.equals( "true" );
			}
			temp = atts.getValue( "setupmenu_idle_systemSettings_shown" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_idle_systemSettings_shown = temp.equals( "true" );
			}
			temp = atts.getValue( "setupmenu_screenEdit_startScene_repulsion" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_screenEdit_startScene_repulsion = temp.equals( "true" );
			}
			temp = atts.getValue( "halfTapSquareSize" );
			if( temp != null )
			{
				DefaultLayout.halfTapSquareSize = Integer.valueOf( temp );
			}
			temp = atts.getValue( "disable_set_wallpaper_dimensions" );
			if( temp != null )
			{
				DefaultLayout.disable_set_wallpaper_dimensions = temp.equals( "true" );
			}
			temp = atts.getValue( "include_theme_box" );
			if( temp != null )
			{
				DefaultLayout.include_theme_box = temp.equals( "true" );
			}
			else
			{
				DefaultLayout.include_theme_box = true;
			}
			temp = atts.getValue( "enable_haocheng_sys_widget_anim" );
			if( temp != null )
			{
				DefaultLayout.enable_haocheng_sys_widget_anim = temp.equals( "true" );
			}
			temp = atts.getValue( "show_popup_menu_anim" );
			if( temp != null )
			{
				DefaultLayout.show_popup_menu_anim = temp.equals( "true" );
			}
			temp = atts.getValue( "show_appbar_indicatorBg" );
			if( temp != null )
			{
				DefaultLayout.show_appbar_indicatorBg = temp.equals( "true" );
			}
			// temp = atts.getValue("personal_center_internal");
			// if (temp != null) {
			// DefaultLayout.personal_center_internal = temp.equals("true");
			// }
			temp = atts.getValue( "show_music_page" );
			if( temp != null )
			{
				DefaultLayout.show_music_page = temp.equals( "true" );
			}
			// Jone add start
			// temp = atts.getValue( "net_version" );
			if( RR.net_version )
			{
				DefaultLayout.setupmenu_by_system = false;
				DefaultLayout.setupmenu_by_view3d = false;
				RR.net_version = true;
				DefaultLayout.show_roll_dockbar_checkbox = true;
				DefaultLayout.net_lite = true;
				DefaultLayout.show_music_page = false;
				DefaultLayout.personal_center_internal = true;
				DefaultLayout.googleHomePage = "http://www.google.com";
			}
			temp = atts.getValue( "show_music_page_enable_config" );
			if( temp != null )
			{
				DefaultLayout.show_music_page_enable_config = temp.equals( "true" );
			}
			temp = atts.getValue( "show_music_page_config_default_value" );
			if( temp != null )
			{
				DefaultLayout.show_music_page_config_default_value = temp.equals( "true" );
			}
			// xiatian add start //for mainmenu sort by user
			temp = atts.getValue( "mainmenu_sort_by_user_fun" );
			if( temp != null )
			{
				DefaultLayout.mainmenu_sort_by_user_fun = temp.equals( "true" );
			}
			// xiatian add end
			temp = atts.getValue( "enable_allow_add_more_widgets_to_desktop" );
			if( temp != null )
			{
				DefaultLayout.enable_allow_add_more_widgets_to_desktop = temp.equals( "true" );
			}
			temp = atts.getValue( "mainmenu_position" );
			if( temp == null )
				DefaultLayout.mainmenu_pos = 2; // middle
			else
			{
				if( temp != null && temp.toLowerCase().equals( "right" ) )
				{
					DefaultLayout.mainmenu_pos = 4; // right
				}
				else
				{
					DefaultLayout.mainmenu_pos = 2; // middle
				}
			}
			temp = atts.getValue( "setupmenu_wallpaper_entrance_themebox" );
			if( temp != null )
			{
				DefaultLayout.setupmenu_wallpaper_entrance_themebox = temp.equals( "true" );
			}
			temp = atts.getValue( "post_database" );
			if( temp != null )
			{
				DefaultLayout.post_database = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_workspace_push_icon" );
			if( temp != null )
			{
				DefaultLayout.enable_workspace_push_icon = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_personalcenetr_click_widget_to_add" );
			if( temp != null )
			{
				DefaultLayout.enable_personalcenetr_click_widget_to_add = temp.equals( "true" );
			}
			temp = atts.getValue( "seatbar_hide_height_ratio" );
			if( temp != null )
			{
				DefaultLayout.seatbar_hide_height_ratio = Float.valueOf( temp );
			}
			temp = atts.getValue( "release_widget" );
			if( temp != null )
			{
				DefaultLayout.release_widget = temp.equals( "true" );
			}
			temp = atts.getValue( "enable_release_2Dwidget" );
			if( temp != null )
			{
				DefaultLayout.enable_release_2Dwidget = temp.equals( "true" );
			}
			temp = atts.getValue( "isScaleBitmap" );
			if( temp != null )
			{
				DefaultLayout.isScaleBitmap = temp.equals( "true" );
			}
			temp = atts.getValue( "put_music_before_camera" );
			if( temp != null )
			{
				DefaultLayout.put_music_before_camera = temp.equals( "true" );
			}
			//xujia add start
			temp = atts.getValue( "enable_singleHandler" );
			if( temp != null )
			{
				DefaultLayout.enable_singleHandler = temp.equals( "true" );
			}
			//xujia add end
			temp = atts.getValue( "rapidly_remove_shortcut" );
			if( temp != null )
			{
				DefaultLayout.rapidly_remove_shortcut = temp.equals( "true" );
			}
		}
		else if( localName.equals( TAG_APP_SORT ) )
		{
			String str = atts.getValue( "show" );
			if( str == null )
			{
				DefaultLayout.show_default_app_sort = false;
			}
			else
			{
				if( str.equals( "true" ) )
				{
					DefaultLayout.show_default_app_sort = true;
				}
				else
				{
					DefaultLayout.show_default_app_sort = false;
				}
			}
		}
		else if( localName.equals( TAG_SHOW_APP ) )
		{
			// enable_themebox
			String className = atts.getValue( "pkgname" );
			if( className.equals( "com.iLoong.launcher.theme.ThemeManagerActivity" ) )
			{
			}
			else if( className.equals( "com.iLoong.launcher.theme.ThemeChangeActivity" ) && DefaultLayout.enable_themebox )
			{
				DefaultLayout.showAppList.add( className );
			}
			else
				DefaultLayout.showAppList.add( className );
			// enable_themebox
		}
		else if( localName.equals( TAG_HIDE_APP ) )
		{
			DefaultLayout.hideAppList.add( atts.getValue( "compname" ) );
		}
		else if( localName.equals( TAG_HIDE_WIDGET ) )
		{
			DefaultLayout.hideWidgetList.add( atts.getValue( "compname" ) );
		}
		else if( localName.equals( TAG_APP ) )
		{
			FactoryApp app = new FactoryApp();
			app.id = Integer.valueOf( atts.getValue( "id" ) );
			app.packageName = atts.getValue( "pkgname" );
			app.className = atts.getValue( "componentName" );
			DefaultLayout.facApp.add( this.curApp , app );
		}
		else if( localName.equals( MM_SETTING ) )
		{
			Log.v( "test" , "add MM_SETTING" );
			String index;
			for( int j = 0 ; j < DefaultLayout.MM_SETTING_ELEMENT ; j++ )
			{
				index = "e_" + j;
				DefaultLayout.mmData[j] = atts.getValue( index );
			}
		}
		else if( localName.equals( TAG_DESKTOP_EFFECT_LIST ) )
		{
			Log.v( "test" , "add TAG_DESKTOP_EFFECT_LIST" );
			String deskop_effect_list_str = atts.getValue( "effect_list" );
			if( deskop_effect_list_str != null )
			{
				DefaultLayout.page_effect_no_radom_style = true;
				DefaultLayout.desktop_effect_list = deskop_effect_list_str.split( "," );
				for( int i = 0 ; i < DefaultLayout.desktop_effect_list.length ; i++ )
				{
					DefaultLayout.desktop_effect_list[i] = DefaultLayout.desktop_effect_list[i].trim();
					if( DefaultLayout.desktop_effect_list[i].equals( "effect_random" ) )
						DefaultLayout.page_effect_no_radom_style = false;
				}
			}
		}
		else if( localName.equals( TAG_MAINMENU_EFFECT_LIST ) )
		{
			Log.v( "test" , "add TAG_MAINMENU_EFFECT_LIST" );
			String mainmenu_effect_list_str = atts.getValue( "effect_list" );
			if( mainmenu_effect_list_str != null )
			{
				DefaultLayout.page_effect_no_radom_style = true;
				DefaultLayout.mainmenu_effect_list = mainmenu_effect_list_str.split( "," );
				for( int i = 0 ; i < DefaultLayout.mainmenu_effect_list.length ; i++ )
				{
					DefaultLayout.mainmenu_effect_list[i] = DefaultLayout.mainmenu_effect_list[i].trim();
					if( DefaultLayout.mainmenu_effect_list[i].equals( "effect_random" ) )
						DefaultLayout.page_effect_no_radom_style = false;
				}
			}
		}
		else if( localName.equals( IDLE_PAGE_TRANSFORM_OVER_MSG ) )
		{
			Log.v( "test" , "add IDLE_PAGE_TRANSFORM_OVER_MSG" );
			String enable = atts.getValue( "enable" );
			if( enable != null && enable.equals( "true" ) )
			{
				for( int index = 1 ; index < atts.getLength() ; index++ )
				{
					String index_str = "intent_action_" + ( index - 1 );
					String intent_action = atts.getValue( index_str );
					if( intent_action != null && DefaultLayout.idlePageTransformOverIntentList != null )
					{
						DefaultLayout.idlePageTransformOverIntentList.add( intent_action );
					}
				}
			}
			else
			{
				DefaultLayout.idlePageTransformOverIntentList.clear();
			}
		}
		else if( localName.equals( "icon" ) )
		{
			if( DefaultLayout.specifiedIconsLoaded )
			{
				return;
			}
			DefaultIcon temp = new DefaultIcon();
			temp.title = atts.getValue( "name" );
			if( atts.getValue( "dup" ) == null )
				temp.duplicate = false;
			else
				temp.duplicate = true;
			temp.pkgName = atts.getValue( "pkgname" );
			temp.imageName = atts.getValue( "image" );
			temp.className = atts.getValue( "componentName" );
			if( temp.className == null )
				temp.className = "";
			temp.dealed = false;
			DefaultLayout.defaultIcon.add( temp );
		}
		else if( localName.equals( "musicSeatIcon" ) )
		{
			MediaSeatIcon temp = new MediaSeatIcon();
			temp.title = atts.getValue( "name" );
			temp.pkgName = atts.getValue( "pkgname" );
			temp.className = atts.getValue( "componentName" );
			temp.imageName = atts.getValue( "image" );
			DefaultLayout.musicSeatIcon.add( temp );
		}
		else if( localName.equals( "cameraSeatIcon" ) )
		{
			MediaSeatIcon temp = new MediaSeatIcon();
			temp.title = atts.getValue( "name" );
			temp.pkgName = atts.getValue( "pkgname" );
			temp.className = atts.getValue( "componentName" );
			temp.imageName = atts.getValue( "image" );
			DefaultLayout.musicSeatIcon.add( temp );
		}
		else if( localName.equals( "dynamicicon" ) )
		{
			DynamicIcon temp = new DynamicIcon();
			temp.pkgName = atts.getValue( "pkgname" );
			temp.compName = atts.getValue( "componentName" );
			temp.title = atts.getValue( "name" );
			if( temp.compName == null )
				temp.compName = "";
			if( temp.pkgName != null )
			{
				DefaultLayout.dynamicIcon.add( temp );
			}
			if( temp.title != null )
			{
				if( DefaultLayout.dynamiciconlist.size() != 0 )
				{
					DefaultLayout.dynamiciconlist.add( temp.title );
				}
				else
				{
					String[] dynlist = temp.title.split( ";" );
					if( dynlist.length > 1 )
					{
						for( int i = 0 ; i < dynlist.length ; i++ )
						{
							DefaultLayout.dynamiciconlist.add( dynlist[i] );
						}
					}
					else
					{
						DefaultLayout.dynamiciconlist.add( temp.title );
					}
				}
			}
		}
		else if( localName.equals( TAG_WIDGET ) )
		{
			// Log.v("test", "add widget");
			// not_allowed_add_into_allWidget = false;
			WidgetItem wdg = new WidgetItem();
			wdg.id = Integer.valueOf( atts.getValue( "id" ) );
			wdg.name = DefaultLayout.pickNameByLanSetting( atts.getValue( "name" ) );
			wdg.locate = atts.getValue( "locate" );
			wdg.value = Integer.valueOf( atts.getValue( "locate_value" ) );
			wdg.pkgName = atts.getValue( "pkgname" );
			wdg.imageName = atts.getValue( "image" );
			wdg.apkName = atts.getValue( "apkname" );
			wdg.minWidth = Integer.valueOf( atts.getValue( "width" ) );
			wdg.minHeight = Integer.valueOf( atts.getValue( "height" ) );
			wdg.customID = atts.getValue( "customID" );
			if( RR.net_version )
			{
				wdg.thumbName = atts.getValue( "thumbName" );
			}
			String desk = atts.getValue( "desktop" );
			if( desk != null && desk.equals( "false" ) )
				wdg.addDesktop = false;
			else
				wdg.addDesktop = true;
			if( atts.getValue( "spanx" ) != null )
				wdg.spanX = Integer.valueOf( atts.getValue( "spanx" ) );
			else
				wdg.spanX = 0;
			if( atts.getValue( "spany" ) != null )
				wdg.spanY = Integer.valueOf( atts.getValue( "spany" ) );
			else
				wdg.spanY = 0;
			if( atts.getValue( "cellX" ) != null )
				wdg.cellX = Integer.valueOf( atts.getValue( "cellX" ) );
			else
				wdg.cellX = -1;
			if( atts.getValue( "cellY" ) != null )
				wdg.cellY = Integer.valueOf( atts.getValue( "cellY" ) );
			else
				wdg.cellY = -1;
			String loadByInternal = "false";
			loadByInternal = atts.getValue( "loadByInternal" );
			if( loadByInternal != null && loadByInternal.equals( "true" ) )
			{
				wdg.loadByInternal = true;
			}
			else
			{
				wdg.loadByInternal = false;
			}
			wdg.className = atts.getValue( "classname" );
			Log.d( "launcher" , "name:" + wdg.name + "," + wdg.spanX + "," + wdg.spanY );
			// String imageName = null;
			// if( wdg.loadByInternal )
			// {
			// String widgetShortName = wdg.pkgName;
			// imageName = "theme/widget/" + widgetShortName + "/" +
			// ThemeManager.getInstance().getCurrentThemeDescription().widgettheme
			// + "/image/widget_ico.png";
			// wdg.imageName = imageName;
			// }
			// else
			// {
			// imageName = DefaultLayout.THEME_WIDGET_APPLIST + wdg.imageName;
			// }
			// Bitmap image_bmp = ThemeManager.getInstance().getBitmap(
			// imageName );
			// final PackageManager packageManager =
			// iLoongLauncher.getInstance().getPackageManager();
			// PackageInfo packageInfo;
			// try
			// {
			// packageInfo = packageManager.getPackageInfo( wdg.pkgName , 0 );
			// }
			// catch( NameNotFoundException e )
			// {
			// packageInfo = null;
			// // e.printStackTrace();
			// }
			// // 当widget配置的image存在 或者 当该配置的widget已经安装时，添加入allWidget列表中
			// if( image_bmp != null || packageInfo != null )
			// {
			// not_allowed_add_into_allWidget = false;
			// boolean find = false;
			// for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
			// {
			// if( DefaultLayout.allWidget.get( i ).pkgName.equals( wdg.pkgName
			// ) )
			// {
			// DefaultLayout.allWidget.set( i , wdg );
			// find = true;
			// break;
			// }
			// }
			// if( !find )
			// {
			// DefaultLayout.allWidget.add( this.curWidget , wdg );
			// }
			// find = false;
			// for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
			// {
			// if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals(
			// wdg.pkgName ) )
			// {
			// DefaultLayout.allWidgetFinal.set( i , wdg );
			// find = true;
			// break;
			// }
			// }
			// if( !find )
			// {
			// DefaultLayout.allWidgetFinal.add( this.curWidget , wdg );
			// }
			// if( image_bmp != null )
			// image_bmp.recycle();
			// }
			// else
			// {
			// not_allowed_add_into_allWidget = true;
			// }
			boolean find = false;
			for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
			{
				if( DefaultLayout.allWidget.get( i ).pkgName.equals( wdg.pkgName ) )
				{
					DefaultLayout.allWidget.set( i , wdg );
					find = true;
					break;
				}
			}
			if( !find )
			{
				DefaultLayout.allWidget.add( this.curWidget , wdg );
			}
			find = false;
			for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
			{
				if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals( wdg.pkgName ) )
				{
					DefaultLayout.allWidgetFinal.set( i , wdg );
					find = true;
					break;
				}
			}
			if( !find )
			{
				DefaultLayout.allWidgetFinal.add( this.curWidget , wdg );
			}
		}
		else if( localName.equals( TAG_VIRTURE ) )
		{
			// Log.v("test", "add virture");
			VirtureIcon wdg = new VirtureIcon();
			if( RR.net_version )
			{
				wdg.name = getVirtureIconName( atts.getValue( "name" ) );
			}
			else
				wdg.name = DefaultLayout.pickNameByLanSetting( atts.getValue( "name" ) );
			wdg.locate = atts.getValue( "locate" );
			if( atts.getValue( "locate_value" ) != null )
				wdg.value = Integer.valueOf( atts.getValue( "locate_value" ) );
			else
				wdg.value = 0;
			// wdg.x = Integer.valueOf(atts.getValue("x"));
			// wdg.y = Integer.valueOf(atts.getValue("y"));
			wdg.pkgName = atts.getValue( "pkgname" );
			wdg.className = atts.getValue( "componentName" );
			wdg.imageName = atts.getValue( "image" );
			wdg.apkName = atts.getValue( "apkname" );
			wdg.customID = atts.getValue( "customID" );
			if( atts.getValue( "cellX" ) != null )
				wdg.cellX = Integer.valueOf( atts.getValue( "cellX" ) );
			else
				wdg.cellX = -1;
			if( atts.getValue( "cellY" ) != null )
				wdg.cellY = Integer.valueOf( atts.getValue( "cellY" ) );
			else
				wdg.cellY = -1;
			if( atts.getValue( "from_airpush" ) != null )
				wdg.fromAirPush = atts.getValue( "from_airpush" ).equals( "true" );
			wdg.folder = this.curFolder;
			DefaultLayout.allVirture.add( wdg );
		}
		else if( localName.equals( TAG_FOLDER ) )
		{
			// Log.v("test", "add folder");
			FolderList folder = new FolderList();
			folder.id = Integer.valueOf( atts.getValue( "id" ) );
			folder.name = DefaultLayout.pickNameByLanSetting( atts.getValue( "name" ) );
			folder.repeat_count = 0;
			folder.cellX = Integer.valueOf( atts.getValue( "cellX" ) );
			folder.cellY = Integer.valueOf( atts.getValue( "cellY" ) );
			folder.iconResource = atts.getValue( "iconResource" );
			this.curFolderId = folder.id;
			this.curFolder = folder;
			if( DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList == null )
			{
				Log.e( "folder" , "init folder:" + this.curShortGRP );
				DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList = new ArrayList<FolderList>();
			}
			// DefaultLayout.allShortcutList.get(this.curShortGRP).folderList.add(
			// folder.id, folder);
			DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList.add( folder );
		}
		else if( localName.equals( TAG_ITEM ) )
		{
			// Log.v("test", "add item");
			boolean onFolder = false;
			ShortcutItem item = new ShortcutItem();
			item.id = Integer.valueOf( atts.getValue( "id" ) );
			item.name = DefaultLayout.pickNameByLanSetting( atts.getValue( "name" ) );
			item.imageName = atts.getValue( "image" );
			item.pkgName = atts.getValue( "pkgname" );
			item.className = atts.getValue( "componentName" );
			// Log.v("test", "name = " + item.pkgName);
			if( atts.getValue( "cellX" ) != null )
				item.cellX = Integer.valueOf( atts.getValue( "cellX" ) );
			else
				item.cellX = -1;
			if( atts.getValue( "cellY" ) != null )
				item.cellY = Integer.valueOf( atts.getValue( "cellY" ) );
			else
				item.cellY = -1;
			item.folderId = this.curFolderId;
			if( item.folderId != -1 )
				onFolder = true;
			if( atts.getValue( "cls_name" ) != null && atts.getValue( "cls_name" ).equals( "Contact3DShortcut" ) )
			{
				item.isContact = true;
			}
			if( onFolder )
			{
				if( DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList == null )
				{
					DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList = new ArrayList<FolderList>();
				}
				if( DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList.get( this.curFolderId ).shortcutList == null )
				{
					DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList.get( this.curFolderId ).shortcutList = new ArrayList<ShortcutItem>();
				}
				DefaultLayout.allShortcutList.get( this.curShortGRP ).folderList.get( this.curFolderId ).shortcutList.add( item );
			}
			else
			{
				if( DefaultLayout.allShortcutList.get( this.curShortGRP ).shortcutList == null )
				{
					DefaultLayout.allShortcutList.get( this.curShortGRP ).shortcutList = new ArrayList<ShortcutItem>();
				}
				DefaultLayout.allShortcutList.get( this.curShortGRP ).shortcutList.add( item );
			}
		}
		else if( localName.equals( TAG_SYSWIDGET ) )
		{
			SysWidget widget = new SysWidget();
			widget.packageName = atts.getValue( "packageName" );
			widget.className = atts.getValue( "className" );
			if( atts.getValue( "screen" ) != null )
				widget.screen = Integer.valueOf( atts.getValue( "screen" ) );
			else
				widget.screen = 0;
			if( atts.getValue( "cellX" ) != null )
				widget.cellX = Integer.valueOf( atts.getValue( "cellX" ) );
			else
				widget.cellX = -1;
			if( atts.getValue( "cellY" ) != null )
				widget.cellY = Integer.valueOf( atts.getValue( "cellY" ) );
			else
				widget.cellY = -1;
			if( atts.getValue( "spanX" ) != null )
				widget.spanX = Integer.valueOf( atts.getValue( "spanX" ) );
			else
				widget.spanX = -1;
			if( atts.getValue( "spanY" ) != null )
				widget.spanY = Integer.valueOf( atts.getValue( "spanY" ) );
			else
				widget.spanY = -1;
			DefaultLayout.allSysWidget.add( widget );
		}
		else if( localName.equals( TAG_SYS_SHORTCUT ) )
		{
			SysShortcut shortcut = new SysShortcut();
			shortcut.name = atts.getValue( "name" );
			shortcut.packageName = atts.getValue( "packageName" );
			shortcut.className = atts.getValue( "className" );
			shortcut.imageName = atts.getValue( "image" );
			if( atts.getValue( "screen" ) != null )
				shortcut.screen = Integer.valueOf( atts.getValue( "screen" ) );
			else
				shortcut.screen = 0;
			if( atts.getValue( "cellX" ) != null )
				shortcut.cellX = Integer.valueOf( atts.getValue( "cellX" ) );
			else
				shortcut.cellX = -1;
			if( atts.getValue( "cellY" ) != null )
				shortcut.cellY = Integer.valueOf( atts.getValue( "cellY" ) );
			else
				shortcut.cellY = -1;
			DefaultShortcutIcon temp = new DefaultShortcutIcon();
			temp.title = atts.getValue( "name" );
			Iterator<DefaultIcon> ite = DefaultLayout.defaultIcon.iterator();
			while( ite.hasNext() )
			{
				DefaultIcon icon = ite.next();
				if( icon.title.equals( temp.title ) )
					ite.remove();
			}
			temp.pkgName = atts.getValue( "packageName" );
			temp.imageName = atts.getValue( "image" );
			temp.className = atts.getValue( "className" );
			if( temp.className == null )
				temp.className = "";
			DefaultLayout.defaultshortcutIcon.add( temp );
			DefaultLayout.allSysShortcut.add( shortcut );
		}
		else if( localName.equals( TAG_COOEE_PLUGIN ) )
		{
			CooeePlugin plugin = new CooeePlugin();
			plugin.name = atts.getValue( "name" );
			plugin.pkgName = atts.getValue( "pkgname" );
			plugin.screen = Integer.valueOf( atts.getValue( "locate_value" ) );
			plugin.fullScreen = Boolean.valueOf( atts.getValue( "fullscreen" ) );
			DefaultLayout.cooeePlugins.add( plugin );
		}
		else if( localName.equals( TAG_APPLIST_VIRTUAL_ICON ) )
		{
			ApplistVirtualIcon virtualIcon = new ApplistVirtualIcon();
			virtualIcon.name = atts.getValue( "name" );
			virtualIcon.title = atts.getValue( "title" );
			virtualIcon.packageName = atts.getValue( "packagename" );
			virtualIcon.imageName = atts.getValue( "image" );
			DefaultLayout.allAppListVirtualIcon.add( virtualIcon );
		}
	}
	
	public String getVirtureIconName(
			String defaultName )
	{
		String name = "";
		if( defaultName.equals( "Personal Center" ) )
		{
			//			if( iLoongApplication.getInstance() != null )
			//			{
			//				name = iLoongApplication.getInstance().getString( RR.string.virtue_personal_center );
			//			}
			//			else
			{
				name = iLoongApplication.getInstance().getString( RR.string.virtue_personal_center );
			}
		}
		else if( defaultName.equals( "Wallpaper" ) )
		{
			//			if( iLoongApplication.getInstance() != null )
			//			{
			//				name = iLoongApplication.getInstance().getString( RR.string.virtue_wallpaper );
			//			}
			//			else
			{
				name = iLoongApplication.getInstance().getString( RR.string.virtue_wallpaper );
			}
		}
		else if( defaultName.equals( "Effects" ) )
		{
			//			if( iLoongApplication.getInstance() != null )
			//			{
			//				name = iLoongApplication.getInstance().getString( RR.string.virtue_effects );
			//			}
			//			else
			{
				name = iLoongApplication.getInstance().getString( RR.string.virtue_effects );
			}
		}
		else if( defaultName.equals( "Lock Screen" ) )
		{
			//			if( iLoongApplication.getInstance() != null )
			//			{
			//				name = iLoongApplication.getInstance().getString( RR.string.virtue_turbo_lock_screen );
			//			}
			//			else
			{
				name = iLoongApplication.getInstance().getString( RR.string.virtue_turbo_lock_screen );
			}
		}
		else if( defaultName.equals( "DesktopSettings" ) )
		{
			//			if( iLoongApplication.getInstance() != null )
			//			{
			//				name = iLoongApplication.getInstance().getString( RR.string.desktop_setting );
			//			}
			//			else
			{
				name = iLoongApplication.getInstance().getString( RR.string.desktop_setting );
			}
		}
		else if( defaultName.equals( "Pingmuyulang" ) )
		{
			//			if( iLoongApplication.getInstance() != null )
			//			{
			//				name = iLoongApplication.getInstance().getString( RR.string.title_destop_edit_shortcut_preview );
			//			}
			//			else
			{
				name = iLoongApplication.getInstance().getString( RR.string.title_destop_edit_shortcut_preview );
			}
		}
		return name;
	}
	
	public void endElement(
			String namespaceURI ,
			String localName ,
			String qName ) throws SAXException
	{
		if( parseThemeConfig && !themeConfigTag.contains( localName ) )
		{
			return;
		}
		// Utils3D.showPidMemoryInfo("mem2");
		if( localName.equals( TAG_SHORTGRP ) )
		{
			this.curShortGRP++;
			Log.e( "folder" , "curShortGRP:" + this.curShortGRP );
			// this.curFolder = 0;
		}
		else if( localName.equals( TAG_FOLDER ) )
		{
			this.curFolderId = -1;
		}
		else if( localName.equals( TAG_WIDGET ) )
		{
			this.curWidget++;
		}
		else if( localName.equals( TAG_APP ) )
		{
			this.curApp++;
		}
	}
	
	private String getFolderLocalName()
	{
		String local = iLoongLauncher.getInstance().getResources().getConfiguration().locale.toString();
		if( local.contains( "CN" ) )
		{
			return "foldercname";
		}
		else if( local.contains( "TW" ) )
		{
			return "folderbname";
		}
		else
		{
			return "folderename";
		}
	}
	
	private String getAppLocalName()
	{
		String local = iLoongLauncher.getInstance().getResources().getConfiguration().locale.toString();
		if( local.contains( "CN" ) )
		{
			return "appchname";
		}
		else if( local.contains( "TW" ) )
		{
			return "appbigname";
		}
		else
		{
			return "appname";
		}
	}
}

public class DefaultLayout
{
	
	public static boolean mainmenu_longclick_pageedit;
	public static String DEFAULT_LAYOUT_FILENAME = "default/default_layout.xml";
	public static final String CUSTOM_DEFAULT_LAYOUT_FILENAME_PUBLIC = "/system/launcher/default_layout.xml";
	public static final String CUSTOM_FIRST_DEFAULT_LAYOUT_FILENAME_PUBLIC = "/system/oem/launcher/default_layout.xml";
	public static final String CUSTOM_DEFAULT_LAYOUT_FILENAME = "/system/launcher/coco_default_layout.xml";
	public static final String CUSTOM_FIRST_DEFAULT_LAYOUT_FILENAME = "/system/oem/launcher/coco_default_layout.xml";
	public static int LOCATE_SIDEBAR = 0;
	public static int LOCATE_WORKSPACE = 1;
	public static int LOCATE_HOTSEAT = 2;
	public static final String THEME_ICON_FOLDER = "theme/icon/";
	public static final String THEME_WIDGET_FOLDER = "theme/widget/";
	public static final String THEME_VIRTURE_FOLDER = "theme/virture/";
	public static final String THEME_SYS_SHORTCUT_FOLDER = "theme/shortcut/";
	public static final String THEME_WIDGET_APPLIST = "theme/widget/applist-";
	public static final String THEME_NAME = "80/";
	public static final String HOTSEAT_PATH = "theme/hotseatbar/";
	public static final String THEME_ICON_80_FOLDER = "theme/icon/80";
	// public static int hotLength=0;
	static Root3D root;
	static Workspace3D workspace;
	static IconCache iconCache;
	static HotSeat3D sideBar;
	// public static HashMap<Integer, ShortcutGRP> allShortcutList;
	// public static HashMap<Integer, WidgetItem> allWidget;
	public static ArrayList<ShortcutGRP> allShortcutList = new ArrayList<ShortcutGRP>();
	public static ArrayList<WidgetItem> allWidget = new ArrayList<WidgetItem>();
	public static ArrayList<WidgetItem> allWidgetFinal = new ArrayList<WidgetItem>();
	public static ArrayList<WidgetView> aliveDefWidget = new ArrayList<WidgetView>();
	public static ArrayList<VirtureIcon> allVirture = new ArrayList<VirtureIcon>();
	public static HashMap<FolderList , UserFolderInfo> allFolder = new HashMap<FolderList , UserFolderInfo>();
	public static ArrayList<DefaultIcon> defaultIcon = new ArrayList<DefaultIcon>();
	public static ArrayList<DynamicIcon> dynamicIcon = new ArrayList<DynamicIcon>();// dynamicicon
																					// replace
	public static ArrayList<DynamicIcon> sysDynamicIcon = new ArrayList<DynamicIcon>();
	public static ArrayList<String> dynamiciconlist = new ArrayList<String>();// dynamicicon
																				// replace
	public static ArrayList<String> sysDynamiciconlist = new ArrayList<String>();
	public static ArrayList<DefaultShortcutIcon> defaultshortcutIcon = new ArrayList<DefaultShortcutIcon>();
	// public static ArrayList<ShortcutItem> allHotSeatList = new
	// ArrayList<ShortcutItem>();
	public static List<ResolveInfo> allApp;
	public static ArrayList<SysWidget> allSysWidget = new ArrayList<SysWidget>();
	public static ArrayList<SysShortcut> allSysShortcut = new ArrayList<SysShortcut>();
	public static ArrayList<CooeePlugin> cooeePlugins = new ArrayList<CooeePlugin>();
	public static ArrayList<FactoryApp> facApp = new ArrayList<FactoryApp>();
	public static ArrayList<String> showAppList = new ArrayList<String>();
	public static ArrayList<ApplistVirtualIcon> allAppListVirtualIcon = new ArrayList<ApplistVirtualIcon>();
	public static ArrayList<String> hideWidgetList = new ArrayList<String>();
	public static ArrayList<MediaSeatIcon> musicSeatIcon = new ArrayList<MediaSeatIcon>();
	public static ArrayList<MediaSeatIcon> cameraSeatIcon = new ArrayList<MediaSeatIcon>();
	// xujia add
	public static boolean media_view_black_bg = false;
	public static float media_view_dispose_bg_alpha = 1.0f;
	// xujia add end
	static
	{
		// enable_themebox zjp remove
		// String className = "";
		// if (FeatureConfig.enable_themebox)
		// className = "com.coco.theme.themebox.MainActivity";
		// else
		// className = "com.iLoong.launcher.theme.ThemeManagerActivity";
		// showAppList.add(className);
		// enable_themebox
		// xiatian add start //New Requirement 20130507
		if( DefaultLayout.enable_WallpaperBox )
		{
			// showAppList.add(iLoongLauncher.WALLPAPER_BOX_MAIN_ACTIVITY);
		}
		if( DefaultLayout.enable_SceneBox )
		{
			// showAppList.add(iLoongLauncher.SCENE_BOX_MAIN_ACTIVITY);
		}
		// xiatian add end
	}
	public static ArrayList<String> hideAppList = new ArrayList<String>();
	static DefaultLayout defLayout;
	/* special key function */
	// public static int MM_SETTING_NUM = 3;
	public static int MM_SETTING_ELEMENT = 6;
	public static String mmData[] = new String[MM_SETTING_ELEMENT];
	public static ArrayList<String> idlePageTransformOverIntentList = new ArrayList<String>();
	// from feature_config start
	public static boolean enable_themebox = true;
	// xiatian add start //New Requirement 20130507
	public static boolean enable_WallpaperBox = false;
	public static boolean enable_SceneBox = false;
	// xiatian add end
	public static String default_theme_package_name = null;
	public static boolean lite_edition = false;
	public static boolean use_new_theme = false;
	// from feature_config end
	// teapotXu add start
	//	public static String desktop_effect_list[] = {
	//			"effect_standard" ,
	//			"effect_random" ,
	//			"effect_inbox" ,
	//			"effect_outbox" ,
	//			"effect_flip" ,
	//			"effect_bigfan" ,
	//			"effect_fan" ,
	//			"effect_wave" ,
	//			"effect_default"
	//	// ,"effect_crystal"
	//	};
	public static String desktop_effect_list[] = { "effect_standard" , "effect_inbox" , "effect_outbox" , "effect_wave" , "effect_flip" , "effect_bigfan" , "effect_fan" , "effect_random" };
	public static String mainmenu_effect_list[] = {
			"effect_standard" ,
			"effect_cascade" ,
			"effect_inbox" ,
			"effect_outbox" ,
			"effect_flip" ,
			"effect_bigfan" ,
			"effect_fan" ,
			"effect_wave" ,
			"effect_wheel" ,
			"effect_ball" ,
			"effect_cylinder" ,
			"effect_binaries" ,
			"effect_jump" ,
			"effect_melt" ,
			"effect_window" ,
			"effect_tornado" ,
			"effect_erase" ,
			"effect_cross" ,
			"effect_ceraunite" ,
			"effect_snake" ,
			"effect_crystal" ,
			"effect_random" };
	// teapotXu add end
	public static String default_explorer = null;
	public static String defaultUri = null;
	public static boolean install_change_wallpaper = false;
	public static boolean show_default_app_sort = false;
	public static boolean hide_online_theme_button = true;
	public static boolean anti_aliasing = false;
	public static boolean enable_scroll_to_widget = false;
	public static boolean dispose_cell_count = false;
	public static int cellCountX = 4;
	public static int cellCountY = 4;
	public static boolean appbar_bag_icon = false;
	public static int default_home_page = 0;
	public static int default_workspace_pagecounts = 5;
	public static int default_workspace_pagecount_min = 5;
	public static int default_workspace_pagecount_max = 9;
	public static boolean disable_shake_wallpaper = false;
	public static boolean default_open_shake_wallpaper = false;
	public static boolean disable_shake_change_theme = true;// teapotXu added
	public static boolean default_open_shake_theme = false;
	public static int icon_title_font = 14;// dip
	public static boolean show_page_edit_on_key_back = false;
	public static boolean disable_x_effect = false;
	public static boolean loadapp_in_background = true;
	public static boolean scene_jingwei = false;
	public static boolean enable_icon_effect = false;
	public static int mainmenu_page_effect_id = 2;
	public static int desktop_page_effect_id = 8;
	public static boolean display_widget_preview_hole = false;
	public static boolean setupmenu_show_theme = true;
	public static String custom_wallpapers_path = "";
	public static String custom_default_wallpaper_name = "";
	public static boolean release_memory_after_pause = false;
	public static String custom_virtual_path = "";
	public static boolean useCustomVirtual = false;
	public static String custom_virtual_download_path = "";
	public static boolean useCustomVirtualDownload = false;
	public static String custom_sys_shortcut_path = "";
	public static boolean useCustomSysShortcut = false;
	public static int app_icon_size = -1;
	public static float thirdapk_icon_scaleFactor = 0.9f;
	public static boolean custom_virtual_icon = false;
	public static int sensor_delay_level = 5;
	public static boolean hide_mainmenu_widget = false;
	public static boolean hide_launcher_wallpapers = false;
	public static boolean broadcast_state = false;
	public static boolean applist_style_classic = false;
	public static boolean hide_appbar = false;
	public static float widget_title_weight = 0.25f;
	public static boolean appbar_show_userapp_list = false;
	public static boolean show_font_bg = false;
	public static boolean font_double_line = false;
	public static boolean hide_backup_and_restore = false;
	public static boolean hotseatbar_browser_special_name = false;
	public static int hot_dock_icon_number = 5;
	public static int trash_icon_pos = TrashIcon3D.TRASH_POS_TOP;
	public static int popmenu_style = SetupMenu.POPMENU_STYLE_ANDROID4;
	public static boolean click_indicator_enter_pageselect = true;
	public static boolean workspace_longclick_display_contacts = true;
	public static boolean show_home_button = true;
	public static boolean hide_desktop_setup = false;
	public static boolean mainmenu_widget_display_contacts = true;
	public static boolean mainmenu_widget_dispale_sys_widgets = true;
	public static boolean enable_service = false;
	public static boolean show_widget_shortcut_bg = false;
	public static boolean widget_shortcut_lefttop = false;
	public static boolean hide_remove_theme_button = false;
	public static boolean hotseat_hide_title = true;
	public static boolean widget_shortcut_title_top = false;
	public static boolean setupmenu_show_clear = false;
	public static boolean disable_double_click = false;
	public static boolean hide_title_bg_shadow = false;
	public static int title_outline_shadow_size = 1;
	public static int title_outline_color = 0xaa000000;
	public static int title_shadow_color = 0x20000000;
	public static boolean hotseat_app_title_colorful = false;
	public static int hotseat_app_title_r = 255;
	public static int hotseat_app_title_g = 255;
	public static int hotseat_app_title_b = 255;
	public static int hotseat_app_title_a = 255;
	public static boolean show_missed_call_sms = false;
	public static boolean show_missed_call_sms_in_appList = false; // teapotXu
																	// added
																	// 在主菜单也显示未接来电/短信条数
	public static String call_component_name = "com.android.contacts.DialtactsActivity";
	public static String call_component_name_applist = "com.android.contacts.activities.DialtactsActivity";
	public static boolean title_style_bold = false;
	public static boolean disable_theme_preview_tween = false;
	public static boolean menu_wyd = false;
	public static boolean pop_menu_focus_focus_effect = false;// zqh ,
	public static boolean keypad_event_of_focus = false; // zqh
	public static int install_apk_delay = 60;
	public static boolean empty_page_add_reminder = false;
	public static int reminder_font = 15;// dip
	public static boolean appbar_widgets_special_name = false;
	public static boolean hide_status_bar = false; // zqh
	public static boolean disable_circled = false;
	public static boolean default_circled_state = false; // teapotXu add
	public static boolean show_introduction = false;
	public static boolean disable_vibrator = false;
	public static boolean default_close_vibrator = false;
	public static boolean restartWhenOrientationChange = false;
	public static boolean reduce_load_priority = false;
	public static boolean widget_revise_complete = false;
	public static boolean wallpaper_has_edage = true;
	public static boolean hotseat_disable_make_folder = false;
	public static boolean hotseat_title_disable_click = false;
	public static boolean bjx_sale_log = false;
	public static boolean add_desktop_list_font_set_color = false;
	public static int add_desktop_list_font_color = 0xFF000000;
	public static boolean desktop_hide_frame = false;
	public static boolean appbar_font_bigger = false;
	public static boolean cancel_dialog_last = false;
	public static boolean blend_func_dst_gl_one = false;
	public static boolean disable_move_wallpaper = false;
	public static boolean mainmenu_inout_no_anim = false;
	public static boolean folder_no_dragon = false;
	public static boolean show_icon_size = true;
	public static boolean hide_big_icon_size = false; // teapotXu added for hide
														// the biggest icon size
	public static boolean ui_style_miui2 = false;
	public static float page_tween_time = 0.7f;
	public static float page_tween_time_min = 0.2f;
	public static boolean show_icon_size_different_layout = false;
	public static boolean reflect_change_statusbar = false;
	public static String scene_menu_text_size = "-1";
	public static HashMap<String , Integer> bigIconMap = new HashMap<String , Integer>();
	public static HashMap<String , Integer> normalIconMap = new HashMap<String , Integer>();
	public static HashMap<String , Integer> smallIconMap = new HashMap<String , Integer>();
	public static final int LAYOUT_ATTR_ICON_SIZE = 0;
	public static final int LAYOUT_ATTR_TITLE_SIZE = 1;
	public static final int LAYOUT_ATTR_WORKSPACE_ROW = 2;
	public static final int LAYOUT_ATTR_WORKSPACE_COL = 3;
	public static final int LAYOUT_ATTR_WORKSPACE_PADDING_TOP = 4;
	public static final int LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM = 5;
	public static final int LAYOUT_ATTR_WORKSPACE_INDICATOR_Y = 6;
	public static final int LAYOUT_ATTR_APPLIST_ROW = 7;
	public static final int LAYOUT_ATTR_APPLIST_COL = 8;
	public static final int LAYOUT_ATTR_APPLIST_PADDING_LEFT = 9;
	public static final int LAYOUT_ATTR_APPLIST_PADDING_RIGHT = 10;
	public static final int LAYOUT_ATTR_APPLIST_PADDING_TOP = 11;
	public static final int LAYOUT_ATTR_APPLIST_PADDING_BOTTOM = 12;
	public static final int LAYOUT_ATTR_APPLIST_INDICATOR_Y = 13;
	public static final int LAYOUT_ATTR_WORKSPACE_CELL_WIDTH = 14;
	public static final int LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT = 15;
	public static boolean optimize_hotseat_scroll_back = true; // teapotXu add
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public static boolean show_sensor = true;
	public static boolean default_open_sensor = true;
	public static int is_supported_sensor = -1;
	// xiatian add end
	public static boolean enable_air_default_layout = false;
	/***************** added by zhenNan.ye begin *******************/
	public static boolean enable_particle = false;
	public static boolean default_particle_settings_value = true;
	/***************** added by zhenNan.ye end *******************/
	public static boolean enable_new_particle = false;
	public static int particle_scroll_distance = 180;
	// add zhujieping begin
	public static boolean miui_v5_folder = false;
	public static boolean blur_enable = false;
	public static boolean blur_bg_enable = false;
	public static float fboScale = 0.2f;
	public static int blurInterate = 3;
	public static float blurRadius = 5.0f;
	public static float blurDuration = 0.38f;
	public static float maskOpacity = 0.52f;
	// add zhujieping end
	public static boolean external_applist_page_effect = true; // teapotXu add
																// for pages
																// effects in
																// Mainmenu
	public static boolean same_spacing_btw_hotseat_icons = true; // teapotXu add
																	// for
																	// hotSeatBar
																	// icons
																	// same
																	// Spacing
	public static boolean enable_hotseat_middle_icon_horizontal = false; // teapotXu
																			// added
	// teapotXu add start for NewAddedApp Flag
	public static boolean enable_new_add_app_flag = false;
	// teapotXu add end
	// teapotXu add start for Folder in MainMenu
	public static boolean mainmenu_folder_function = true; // 默认开启
	public static boolean mainmenu_edit_mode = false;
	public static boolean enhance_generate_mainmenu_folder_condition = false;
	public static boolean mainmenu_folder_hide_while_children_all_hided = false;
	public static boolean mainmenu_sort_by_user_fun = false;// xiatian add //for
															// mainmenu sort by
															// user  
	public static boolean miui_generate_folder_anim = false;// xiatian add //for
															// miui generate
															// folder animation
	// teapotXu add end for Folder in MainMenu
	public static boolean is_demo_version = false; // xiatian add
													// //is_demo_version
	public static boolean enable_texture_pack = true;
	// xiatian add start //Mainmenu Bg
	public static int defaultMainmenuBgIndex = 0;
	public static int lastAppListMainmenuBgIndex = -1;
	public static int lastMediaListMainmenuBgIndex = -1;
	// xiatian add end
	// teapotXu add start
	public static boolean enable_doov_spec_customization = false;
	public static boolean butterfly_style = false;
	public static boolean workspace_npages_circle_scroll_config = false;
	public static boolean drawer_npages_circle_scroll_config = false;
	public static boolean enable_configmenu_for_move_wallpaper = false;
	public static boolean enable_desktopsettings_menu_style = false;
	// teapotXu add end
	public static boolean enable_DesktopIndicatorScroll = false;
	public static boolean enable_AppListIndicatorScroll = true;
	public static float icon_shadow_radius = 3.0f; // xiatian add //add icon
													// shadow
	public static boolean enable_auto_update = false;// huwenhao add
	// xiatian add start //mainmenu_background_alpha_progress
	public static boolean mainmenu_background_alpha_progress = false;
	public static int mainmenu_background_default_alpha = 50;
	public static boolean mainmenu_background_translucent = false;// teapotXu
																	// add
	// xiatian add end
	public static boolean clock_widget_scrollV = false;
	public static String widget_scrollV_pkgs = "";
	public static boolean setupmenu_yitong = false;
	public static boolean newHotSeatMainGroup = false; // xiatian add
														// //newHotSeatMainGroup
	public static boolean page_container_shown = false; // teapotXu add for
														// donot show the
														// pageContainer
	public static boolean setupmenu_by_view3d = true;
	public static boolean generate_new_folder_in_top_trash_bar = true; // teapotXu
																		// added
	public static boolean enable_desktop_longclick_to_add_widget = false;
	public static boolean enable_workspace_miui_edit_mode = false; // teapotXu
																	// added for
																	// longclick
																	// workspace,enter
																	// into
																	// editMode
																	// like miui
	public static boolean send_msg_in_appList_onWindowFocusChanged = false;
	public static boolean enable_hotseat_rolling = true;
	public static boolean enable_effect_preview = false; // xiatian add
															// //EffectPreview
	public static boolean enable_scene_wallpaper = false; // zjp
	public static boolean enable_takein_workspace_by_longclick = false;
	public static boolean diable_enter_applist_when_takein_mode = false; // on/off
																			// to
																			// control
																			// whether
																			// it
																			// can
																			// enter
																			// mainmenu
																			// or
																			// not
																			// when
																			// in
																			// takein
																			// mode.
	public static boolean enable_edit_mode_function = false;
	public static String custom_assets_path = "";
	public static boolean useCustomAssets = false;
	public static boolean enable_content_staistic = true;
	public static boolean dynamic_icon = false; // pchong add
	public static boolean page_effect_no_radom_style = false;
	public static boolean fast_change_theme = false;
	public static boolean enable_tab_plugin = false;
	public static final int TAB_PLATFORM_VERSION = 1;
	public static boolean popmenu_gravity_right_when_special_language = false;
	public static boolean enable_HotSeat3DShake = true; // xiatian add
														// //HotSeat3DShake
	public static boolean desktop_setting_style_white = true;
	public static boolean popup_menu_no_background_shadow = false;
	public static String wallpapers_from_apk_packagename = "";
	public static boolean screen_mess = false;
	public static boolean setupmenu_android4_with_no_icons = false;
	public static boolean big_icon_hot_dock_icon_number_always_4 = true;
	public static boolean dingzhihomekey = false;
	public static boolean disable_crystal_effect_in_workspace = true;
	public static boolean setupmenu_by_system = true;
	public static float npagbse_scroll_nextpage_sensitive = 0.5f;
	public static final int SCROLL_VELOCITY_COEFFICIENT = 500;
	public static final int MIN_FLING_VELOCITY = 250;
	public static boolean pageEditor_back_icon_shown = true;
	public static boolean pageIndicator_scroll_num_shown = true;
	public static boolean huaqin_enable_edit_mode = false;
	public static boolean desk_menu_add_OnKeyLove_item = false;
	public static boolean desk_menu_change_SystemWidget_to_OnKeyLove = false;
	public static int widget_preview_title_span_offsetX = 0;
	public static boolean enable_apply_saved_cur_screen_when_reboot = false;
	public static boolean customer_vanzo_operations = false;
	public static boolean pauseEglHelper = false;
	public static boolean enable_deletefolder = true;
	public static int halfTapSquareSize = 15;
	public static boolean setupmenu_idle_wallpaper_shown = true; // teapotXu
																	// added
	public static boolean setupmenu_idle_sofwareManager_shown = false; // teapotXu
																		// added
	public static boolean setupmenu_idle_systemWidget_shown = true;
	public static boolean setupmenu_idle_systemSettings_shown = true;
	public static boolean setupmenu_screenEdit_startScene_repulsion = true; // teapotXu
																			// added
	public static boolean disable_set_wallpaper_dimensions = false;
	public static boolean enable_haocheng_sys_widget_anim = false;
	public static boolean show_popup_menu_anim = true;
	public static boolean show_appbar_indicatorBg = false;
	public static boolean personal_center_internal = false;
	public static boolean show_music_page = true;
	public static boolean show_music_page_enable_config = true;
	public static boolean show_music_page_config_default_value = true;
	public static boolean enable_allow_add_more_widgets_to_desktop = true;
	public static int mainmenu_pos = 2; // middle
	public static boolean setupmenu_wallpaper_entrance_themebox = true;
	public static boolean post_database = false;
	// xujin
	public static boolean enable_hardware_accel = false;
	public static boolean enable_news = false;
	public static boolean enable_news_handle = true;
	public static boolean enable_news_back = true;
	public static boolean enable_news_shake = true;
	public static boolean show_newspage_with_handle = false;
	public static boolean show_newspage_with_back = false;
	public static boolean show_newspage_with_shake = false;
	public static boolean enable_quick_search = true;
	public static boolean enable_show_appbar = false;
	public static boolean enable_show_widgetzoom = true;
	public static boolean enable_camera = true;
	public static boolean show_news_page_enable_config = true;
	public static boolean show_news_page_config_default_value = true;
	public static boolean show_camera_page_enable_config = true;
	public static boolean show_camera_page_config_default_value = true;
	public static boolean enable_workspace_push_icon = true;
	// Jone add start
	// public static boolean net_version = true;
	public static boolean show_roll_dockbar_checkbox = false;
	public static boolean net_lite = true;
	public static String googleHomePage = "http://www.google.cn";
	// Jone end
	public static boolean setup_menu_support_scroll_page = true;
	public static final String CUSTOM_XML_FILE_NAME = "specifiedIcon.xml";
	public static final String CUSTOM_XML_PREFERENCES_NAME = "specifiedIcon";
	public static final String CUSTOM_XML_KEY_EXIST = "xmlExist";
	public static final String CUSTOM_XML_KEY_OLD_VERSION = "oldVersion";
	public static final String PHONE_KEY_OLD_VERSION = "phoneOldVersion";
	public static final String PHONE_KEY_OLD_DISPLAY = "phoneOldDisplay";
	public static boolean needToSaveSpecifiedIconXml = true;
	public static boolean specifiedIconsLoaded;
	public static ArrayList<DefaultIcon> customDefaultIcon = new ArrayList<DefaultIcon>();
	public static boolean shake_features_menu = false;
	// Jone add end
	public static boolean enable_personalcenetr_click_widget_to_add = false;
	public static float seatbar_hide_height_ratio = 1.2f;
	public static boolean release_widget = false;
	public static boolean enable_release_2Dwidget = false;
	public static boolean isScaleBitmap = true;
	public static boolean release_gl = false;
	public static boolean put_music_before_camera = false;;
	//xujia add start
	public static boolean enable_singleHandler = false;
	//xujia add end
	public static boolean enable_google_version = false;
	public static boolean if_show_mask = true;
	public static boolean if_show_cover = true;
	public static boolean rapidly_remove_shortcut = false;
	public static boolean WorkspaceActionGuide = false;
	public static Object loadingLock = new Object();
	public static Boolean loadingDone = false;
	public static boolean isLoading = false;
	public static boolean isWaittingForCancle = false;
	
	public DefaultLayout()
	{
		LoadDefaultLayoutXml();
		defLayout = this;
	}
	
	public void DefaultLayout_init2()
	{
		if( RR.net_version )
		{
			if( needToSaveSpecifiedIconXml )
			{
				showProgressDialog();
			}
		}
		LoadDefaultLayoutXmlFromThemes();
		initWidgetList();
		setDesktopSetupMenuIconSizeType();
		if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
		{
			initIconSizeMap();
			if( DefaultLayout.big_icon_hot_dock_icon_number_always_4 )
			{
				int icon_size_type = DefaultLayout.getDefaultIconSizeType();
				if( icon_size_type == 0 )
				{// 大图标
					DefaultLayout.hot_dock_icon_number = 4;
				}
			}
		}
		setDefaultIconSize();
		initMainmenuBgListPreference(); // xiatian add //Mainmenu Bg
		if( Utils3D.getScreenWidth() > 480 )
		{
			// 对于大屏幕的手机，此开关无效，因为大屏手机的最佳icon size 是大号的Icon
			DefaultLayout.hide_big_icon_size = false;
		}
		/*
		 * 这里一定要做，保证Utilities中的函数initStatics 尽早调用�?这样sIconTextureWidth
		 * sIconTextureHeight就能够尽早和我们配置的app_icon_size相关绑定起来�?
		 */
		if( DefaultLayout.enable_doov_spec_customization == true )
		{
			DefaultLayout.popmenu_style = SetupMenu.POPMENU_STYLE_ORIGINAL;
			DefaultLayout.butterfly_style = true;
			DefaultLayout.disable_move_wallpaper = true;
			DefaultLayout.default_workspace_pagecount_max = 7;
			DefaultLayout.default_workspace_pagecount_min = 1;
			DefaultLayout.enable_configmenu_for_move_wallpaper = true;
			DefaultLayout.workspace_npages_circle_scroll_config = true;
			// DefaultLayout.enable_desktopsettings_menu_style = true;
			DefaultLayout.enable_desktop_longclick_to_add_widget = true;
		}
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun )
		{
			DefaultLayout.mainmenu_edit_mode = true;
			DefaultLayout.mainmenu_folder_hide_while_children_all_hided = true;
			DefaultLayout.miui_generate_folder_anim = true;
		}
		// xiatian add end
		// xujin camera 开关
		if( DefaultLayout.enable_camera )
		{
			if( DefaultLayout.show_camera_page_enable_config )
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
				Resources res = SetupMenu.getContext().getResources();
				DefaultLayout.enable_camera = pref.getBoolean( res.getString( RR.string.setting_key_camera_page ) , DefaultLayout.show_camera_page_config_default_value );
			}
		}
		else
		{
			DefaultLayout.show_camera_page_enable_config = false;
		}
		if( DefaultLayout.enable_news )
		{
			if( DefaultLayout.show_news_page_enable_config )
			{
				SharedPreferences sp = iLoongLauncher.getInstance().getSharedPreferences( NewspageSettingActivity.DESKTOP_SETTING_SP_NAME , Context.MODE_PRIVATE );
				DefaultLayout.enable_news = sp.getBoolean( NewspageSettingActivity.DESKTOP_SETTING_SP_OPEN_NEWSPAGE_KEY , DefaultLayout.show_news_page_config_default_value );
				DefaultLayout.show_newspage_with_handle = sp.getBoolean( NewspageSettingActivity.DESKTOP_SETTING_SP_ENABLE_HANDLE_KEY , false );
				DefaultLayout.show_newspage_with_back = sp.getBoolean( NewspageSettingActivity.DESKTOP_SETTING_SP_ENABLE_BACK_KEY , true );
				DefaultLayout.show_newspage_with_shake = sp.getBoolean( NewspageSettingActivity.DESKTOP_SETTING_SP_ENABLE_SHAKE_KEY , false );
				NewspageSettingActivity.setIsNewspageOpened( sp.getBoolean( NewspageSettingActivity.DESKTOP_SETTING_SP_OPEN_NEWSPAGE_KEY , true ) );
			}
		}
		else
		{
			DefaultLayout.show_news_page_enable_config = false;
		}
		if( DefaultLayout.show_music_page )
		{
			if( DefaultLayout.show_music_page_enable_config )
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
				Resources res = SetupMenu.getContext().getResources();
				DefaultLayout.show_music_page = pref.getBoolean( res.getString( RR.string.setting_key_music_page ) , DefaultLayout.show_music_page_config_default_value );
			}
		}
		else
		{
			DefaultLayout.show_music_page_enable_config = false;
		}
		Utilities.initStatics( iLoongLauncher.getInstance() );
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		DefaultLayout.drawer_npages_circle_scroll_config = !( share.getBoolean( "drawer_npages_circle_scroll_config" , true ) );
		DefaultLayout.workspace_npages_circle_scroll_config = !( share.getBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) , true ) );
	}
	
	private void initWidgetList()
	{
		String imageName = null;
		ArrayList<WidgetItem> need_removed_Widget = new ArrayList<WidgetItem>();
		for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
		{
			WidgetItem wdg = DefaultLayout.allWidget.get( i );
			{
				if( RR.net_version && wdg.loadByInternal )
				{
					continue;
				}
				if( wdg.loadByInternal )
				{
					String widgetShortName = wdg.apkName.substring( wdg.apkName.lastIndexOf( "." ) + 1 ).toLowerCase( Locale.ENGLISH );
					imageName = "theme/widget/" + widgetShortName + "/" + ThemeManager.getInstance().getCurrentThemeDescription().widgettheme + "/image/widget_ico.png";
				}
				else
				{
					imageName = DefaultLayout.THEME_WIDGET_APPLIST + wdg.imageName;
				}
				Bitmap image_bmp = ThemeManager.getInstance().getBitmap( imageName );
				final PackageManager packageManager = iLoongLauncher.getInstance().getPackageManager();
				PackageInfo packageInfo;
				try
				{
					packageInfo = packageManager.getPackageInfo( wdg.pkgName , 0 );
				}
				catch( NameNotFoundException e )
				{
					packageInfo = null;
					// e.printStackTrace();
				}
				// 当widget配置的image存在 或者 当该配置的widget已经安装时，添加入allWidget列表中
				if( image_bmp == null && packageInfo == null )
				{
					if( RR.net_version && packageInfo != null || !RR.net_version )
						need_removed_Widget.add( wdg );
				}
				else if( image_bmp != null )
				{
					image_bmp.recycle();
				}
			}
		}
		if( need_removed_Widget.size() > 0 )
		{
			DefaultLayout.allWidget.removeAll( need_removed_Widget );
			DefaultLayout.allWidgetFinal.removeAll( need_removed_Widget );
		}
	}
	
	private void setDesktopSetupMenuIconSizeType()
	{
		Log.d( "mytag" , "setDesktopSetupMenuIconSizeType" );
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
		int icon_size_type = Integer.parseInt( pref.getString( "icon_size_key" , "-1" ) );
		if( icon_size_type == -1 )
		{
			icon_size_type = ( R3D.getInteger( "default_icon_size" ) == -1 ) ? 1 : R3D.getInteger( "default_icon_size" );// 默认为中图标
			if( icon_size_type > 2 )
			{
				icon_size_type = 1;
			}
			pref.edit().putString( "icon_size_key" , "" + icon_size_type ).commit();
		}
		ConfigBase.icon_size_type = icon_size_type;
	}
	
	private void initIconSizeMap()
	{
		bigIconMap.clear();
		normalIconMap.clear();
		smallIconMap.clear();
		if( Utils3D.getScreenWidth() > 700 )
		{// 大屏
			bigIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_big_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_big_screen_big_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_big_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_big_screen_normal_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_big_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_big_screen_small_icon" ) );
		}
		else if( Utils3D.getScreenWidth() > 400 )
		{// 中屏
			bigIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_normal_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_normal_screen_big_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_normal_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_normal_screen_normal_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_normal_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_normal_screen_small_icon" ) );
		}
		else
		{// 小屏
			bigIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_small_screen_big_icon" ) );
			bigIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_small_screen_big_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_small_screen_normal_icon" ) );
			normalIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_small_screen_normal_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_ICON_SIZE , R3D.getInteger( "app_icon_size_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_TITLE_SIZE , R3D.getInteger( "icon_title_font_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_ROW , R3D.getInteger( "workspace_row_num_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_COL , R3D.getInteger( "workspace_col_num_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_TOP , R3D.getInteger( "workspace_topPadding_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_PADDING_BOTTOM , R3D.getInteger( "workspace_bottomPadding_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_INDICATOR_Y , R3D.getInteger( "page_indicator_y_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_ROW , R3D.getInteger( "applist_row_num_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_COL , R3D.getInteger( "applist_col_num_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_LEFT , R3D.getInteger( "applist_padding_left_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_RIGHT , R3D.getInteger( "applist_padding_right_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_TOP , R3D.getInteger( "applist_padding_top_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_PADDING_BOTTOM , R3D.getInteger( "applist_padding_bottom_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_APPLIST_INDICATOR_Y , R3D.getInteger( "applist_indicator_y_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_WIDTH , R3D.getInteger( "workspace_cell_width_small_screen_small_icon" ) );
			smallIconMap.put( "" + LAYOUT_ATTR_WORKSPACE_CELL_HEIGHT , R3D.getInteger( "workspace_cell_height_small_screen_small_icon" ) );
		}
	}
	
	private void setDefaultIconSize()
	{
		Log.d( "mytag" , "setDefaultIconSize" );
		if( DefaultLayout.show_icon_size )
		{
			if( DefaultLayout.show_icon_size_different_layout )
			{
				app_icon_size = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_ICON_SIZE );
			}
			else
			{
				int icon_size_type = DefaultLayout.getDefaultIconSizeType();
				switch( icon_size_type )
				{
					case 0:
						if( Utils3D.getScreenWidth() > 700 )
						{// 大屏
							app_icon_size = R3D.getInteger( "app_icon_size_big_screen_big_icon_same_layout" );
						}
						else if( Utils3D.getScreenWidth() > 400 )
						{// 中屏
							app_icon_size = R3D.getInteger( "app_icon_size_normal_screen_big_icon_same_layout" );
						}
						else
						{// 小屏
							app_icon_size = R3D.getInteger( "app_icon_size_small_screen_big_icon_same_layout" );
						}
						break;
					case 1:
						if( Utils3D.getScreenWidth() > 700 )
						{// 大屏
							app_icon_size = R3D.getInteger( "app_icon_size_big_screen_normal_icon_same_layout" );
						}
						else if( Utils3D.getScreenWidth() > 400 )
						{// 中屏
							app_icon_size = R3D.getInteger( "app_icon_size_normal_screen_normal_icon_same_layout" );
						}
						else
						{// 小屏
							app_icon_size = R3D.getInteger( "app_icon_size_small_screen_normal_icon_same_layout" );
						}
						break;
					case 2:
						if( Utils3D.getScreenWidth() > 700 )
						{// 大屏
							app_icon_size = R3D.getInteger( "app_icon_size_big_screen_small_icon_same_layout" );
						}
						else if( Utils3D.getScreenWidth() > 400 )
						{// 中屏
							app_icon_size = R3D.getInteger( "app_icon_size_normal_screen_small_icon_same_layout" );
						}
						else
						{// 小屏
							app_icon_size = R3D.getInteger( "app_icon_size_small_screen_small_icon_same_layout" );
						}
						break;
					default:
						Toast.makeText( iLoongLauncher.getInstance() , "setDefaultIconSize error!!" , Toast.LENGTH_SHORT ).show();
						break;
				}
			}
		}
		else
		{
			if( DefaultLayout.app_icon_size != -1 )
			{
				float density = iLoongLauncher.getInstance().getResources().getDisplayMetrics().density;
				app_icon_size = (int)( density * DefaultLayout.app_icon_size / 1.5f );
				app_icon_size = DefaultLayout.pxRoundToOurValue( app_icon_size );
			}
			else
			{
				app_icon_size = (int)iLoongLauncher.getInstance().getResources().getDimension( android.R.dimen.app_icon_size );
			}
		}
	}
	
	public static int getDefaultIconSizeType()
	{
		// 0 - big
		// 1 - normal
		// 2 - small
		if( DefaultLayout.show_icon_size )
		{
			int icon_size_type = Integer.parseInt( PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( "icon_size_key" , "-1" ) );
			if( icon_size_type == -1 )
			{
			}
			return icon_size_type;
		}
		else
		{
			return 1;
		}
	}
	
	public int getDynamicIcon(
			ResolveInfo info )
	{
		String[] allNamePatten;
		String appName = info.activityInfo.applicationInfo.packageName;
		String className = info.activityInfo.name;
		for( int inter = 0 ; inter < dynamicIcon.size() ; inter++ )
		{
			allNamePatten = dynamicIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.length <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.length ; k++ )
			{
				if( appName.equals( allNamePatten[k] ) )
				{
					String[] allCompNames = dynamicIcon.get( inter ).compNameArray;
					if( allCompNames == null || allCompNames[0].equals( "" ) || allCompNames.length <= 0 )
					{
						return inter;
					}
					if( allCompNames.length > 0 )
					{
						for( int m = 0 ; m < allCompNames.length ; m++ )
						{
							if( className.equals( allCompNames[m] ) )
							{
								return inter;
							}
						}
					}
				}
			}
		}
		return -1;
	}
	
	public int getDynamicIcon(
			ShortcutInfo info )
	{
		String[] allNamePatten;
		String appName;
		String className;
		if( info != null && info.intent != null && info.intent.getComponent() != null && ( info.intent.getComponent().getPackageName() != null && ( info.intent.getComponent().getClassName() != null ) ) )
		{
			appName = info.intent.getComponent().getPackageName();
			className = info.intent.getComponent().getClassName();
		}
		else
		{
			return -1;
		}
		for( int inter = 0 ; inter < dynamicIcon.size() ; inter++ )
		{
			allNamePatten = dynamicIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.length <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.length ; k++ )
			{
				if( appName.equals( allNamePatten[k] ) )
				{
					String[] allCompNames = dynamicIcon.get( inter ).compNameArray;
					if( allCompNames == null || allCompNames[0].equals( "" ) || allCompNames.length <= 0 )
					{
						return inter;
					}
					if( allCompNames.length > 0 )
					{
						for( int m = 0 ; m < allCompNames.length ; m++ )
						{
							if( className.equals( allCompNames[m] ) )
							{
								return inter;
							}
						}
					}
				}
			}
		}
		return -1;
	}
	
	public Bitmap getDefaultShortcutIcon(
			String appName ,
			String compName )
	{
		String[] allNamePatten;
		for( int inter = 0 ; inter < defaultshortcutIcon.size() ; inter++ )
		{
			allNamePatten = defaultshortcutIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.length <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.length ; k++ )
			{
				if( appName.equals( allNamePatten[k] ) )
				{
					String[] allCompNames = defaultshortcutIcon.get( inter ).compNameArray;
					for( int m = 0 ; m < allCompNames.length ; m++ )
					{
						if( compName.equals( allCompNames[m] ) )
						{
							Bitmap bmp = GetShortcutThemeIconPath( defaultshortcutIcon.get( inter ).imageName );
							if( bmp != null )
							{
								return bmp;
							}
							else
							{
								return null;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public boolean hasSysShortcutIcon(
			String appName ,
			String compName )
	{
		String[] allNamePatten;
		for( int inter = 0 ; inter < defaultshortcutIcon.size() ; inter++ )
		{
			allNamePatten = defaultshortcutIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.length <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.length ; k++ )
			{
				if( appName.equals( allNamePatten[k] ) )
				{
					String[] allCompNames = defaultshortcutIcon.get( inter ).compNameArray;
					for( int m = 0 ; m < allCompNames.length ; m++ )
					{
						if( compName.equals( allCompNames[m] ) )
						{
							Bitmap bmp = getSysShortcutThemeIconPath( defaultshortcutIcon.get( inter ).imageName );
							if( bmp != null )
							{
								return true;
							}
							else
							{
								return false;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	// xiatian add start //fix bug:a apk in defaultIcon list, after auto
	// addShortcut,the icon not replace and icon scale as thirdapk icon
	public Bitmap getReplaceIcon(
			String appName ,
			String compName )
	{
		ArrayList<String> allNamePatten;
		for( int inter = 0 ; inter < defaultIcon.size() ; inter++ )
		{
			allNamePatten = defaultIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.size() <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.size() ; k++ )
			{
				if( appName.equals( allNamePatten.get( k ) ) )
				{
					if( defaultIcon.get( inter ).duplicate )
					{
						ArrayList<String> allCompNames = defaultIcon.get( inter ).classNameArray;
						for( int m = 0 ; m < allCompNames.size() ; m++ )
						{
							if( compName.equals( allCompNames.get( m ) ) )
							{
								Bitmap bmp = GetPKGThemeIconPath( defaultIcon.get( inter ).imageName );
								if( bmp != null )
								{
									return bmp;
								}
								else
								{
									return null;
								}
							}
						}
					}
					else
					{
						Bitmap bmp = GetPKGThemeIconPath( defaultIcon.get( inter ).imageName );
						if( bmp != null )
						{
							return bmp;
						}
						else
						{
							return null;
						}
					}
				}
			}
		}
		return null;
	}
	
	// xiatian add end
	public static int getMMSettingValue()
	{
		if( mmData[2] == null )
			return 0;
		if( !mmData[2].equals( "default" ) ) /* check package name */
		{
			if( mmData[2].equals( "com.iLoong.base.themebox" ) && "com.coco.theme.themebox.MainActivity".equals( mmData[4] ) )
			{
				return 3;
			}
			if( mmData[2].equals( "com.cool.launcher" ) && "com.iLoong.launcher.theme.ThemeManagerActivity".equals( mmData[4] ) )
			{
				return 2;
			}
			return 1;
		}
		else
			return 0;
	}
	
	public static int getAppHomeIconCount()
	{
		if( mmData[5] == null )
		{
			return 1;
		}
		return Integer.parseInt( mmData[5] );
	}
	
	public static int startKeyAction()
	{
		int res = 1;
		if( mmData[2] == null )
			return 0;
		if( mmData[2].equals( "default" ) ) /* check package name */
			return 0;/* default process */
		String title = mmData[0];
		String pkgName = mmData[2];
		String componentName = mmData[4];
		if( checkApkExist( iLoongLauncher.getInstance() , pkgName ) )
		{
			Intent intent = new Intent();
			intent.setComponent( new ComponentName( pkgName , componentName ) );
			if( getMMSettingValue() == 3 )
				iLoongLauncher.getInstance().bindThemeActivityData( intent );
			iLoongLauncher.getInstance().startActivity( intent );
		}
		else
		{
			// WidgetDownload.downloadWithoutCheckVersion(title, ApkName,
			// pkgName,
			// customID);
			if( mmData[2].equals( "com.iLoong.base.themebox" ) && "com.coco.theme.themebox.MainActivity".equals( mmData[4] ) )
			{
				iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
					
					@Override
					public void run()
					{
						iLoongLauncher.getInstance().themeCenterDown.ToDownloadApkDialog(
								iLoongLauncher.getInstance() ,
								iLoongLauncher.getInstance().getResources().getString( RR.string.theme ) ,
								mmData[2] );
					}
				} );
				return res;
			}
			if( DLManager.getInstance().HaveDownLoad( pkgName ) )
			{
				WidgetDownload.showInfo( null , title + iLoongLauncher.languageSpace + R3D.getString( RR.string.app_downloading ) );
				return 1;
			}
			Widget3DManager.curDownload = null;
			SendMsgToAndroid.sendDownloadWidgetMsg();
		}
		return res;
	}
	
	public static String getmmSettingTitle()
	{
		return mmData[0];
	}
	
	public static void startDownLoadmmSetting()
	{
		String title = mmData[0];
		String ApkName = mmData[1];
		String pkgName = mmData[2];
		String customID = mmData[3];
		WidgetDownload.downloadWithoutCheckVersion( title , ApkName , pkgName , customID );
	}
	
	public static boolean checkApkExist(
			Context context ,
			String packageName )
	{
		if( packageName == null || "".equals( packageName ) )
			return false;
		// ApplicationInfo info =
		// context.getPackageManager().getApplicationInfo(packageName,
		// PackageManager.GET_UNINSTALLED_PACKAGES);
		// return true;
		// try {
		// android.content.pm.ApplicationInfo info = context.getPackageManager()
		// .getApplicationInfo(packageName,
		// PackageManager.GET_UNINSTALLED_PACKAGES);
		//
		// return true;
		// } catch (NameNotFoundException e) {
		// return false;
		// }
		PackageInfo packageInfo = null;
		try
		{
			packageInfo = context.getPackageManager().getPackageInfo( packageName , 0 );
		}
		catch( NameNotFoundException e )
		{
			// e.printStackTrace();
		}
		if( packageInfo == null )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void setApplist(
			List<ResolveInfo> app )
	{
		Log.v( "test" , "----------------setApplist" );
		allApp = app;
	}
	
	public static DefaultLayout getInstance()
	{
		return defLayout;
	}
	
	public static int GetTotalDefaultWidgetNumber()
	{
		if( allWidget == null )
			return 0;
		return allWidget.size();
	}
	
	public static String GetDefaultWidgetName(
			int i )
	{
		if( allWidget == null )
			return "";
		return allWidget.get( i ).name;
	}
	
	public static String GetDefaultWidgetName(
			String packageName )
	{
		if( allWidgetFinal == null )
			return "";
		for( int i = 0 ; i < allWidgetFinal.size() ; i++ )
		{
			if( allWidgetFinal.get( i ).pkgName.equals( packageName ) )
			{
				return allWidgetFinal.get( i ).name;
			}
		}
		return "";
	}
	
	public static int GetDefaultWidgetMinWidth(
			int i )
	{
		if( allWidget == null )
			return 1;
		return allWidget.get( i ).minWidth;
	}
	
	public static int GetDefaultWidgetMinHeight(
			int i )
	{
		if( allWidget == null )
			return 1;
		return allWidget.get( i ).minHeight;
	}
	
	public static int GetDefaultWidgetHSpan(
			int i )
	{
		if( allWidget == null )
			return 0;
		return allWidget.get( i ).spanX;
	}
	
	public static int GetDefaultWidgetVSpan(
			int i )
	{
		if( allWidget == null )
			return 0;
		return allWidget.get( i ).spanY;
	}
	
	public static int GetDefaultWidgetHSpan(
			String packageName )
	{
		if( allWidgetFinal == null )
			return 0;
		for( int i = 0 ; i < allWidgetFinal.size() ; i++ )
		{
			if( allWidgetFinal.get( i ).pkgName.equals( packageName ) )
			{
				return allWidgetFinal.get( i ).spanX;
			}
		}
		return 0;
	}
	
	public static int GetDefaultWidgetVSpan(
			String packageName )
	{
		if( allWidgetFinal == null )
			return 0;
		for( int i = 0 ; i < allWidgetFinal.size() ; i++ )
		{
			if( allWidgetFinal.get( i ).pkgName.equals( packageName ) )
			{
				return allWidgetFinal.get( i ).spanY;
			}
		}
		return 0;
	}
	
	public static String GetDefaultWidgetPkgname(
			int i )
	{
		if( allWidget == null )
			return "";
		return allWidget.get( i ).pkgName;
	}
	
	public static void RemoveDefWidgetWithPkgname(
			String pkgName )
	{
		if( allWidget == null )
			return;
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			if( allWidget.get( i ).pkgName.equals( pkgName ) )
			{
				allWidget.remove( i );
				break;
			}
		}
		removeDefWidgetView( pkgName );
	}
	
	public static void installWidget(
			ResolveInfo widget3d ,
			String pkgName )
	{
		if( allWidget == null )
			return;
		WidgetView wdgView;
		ShortcutInfo info;
		for( int i = 0 ; i < widView.size() ; i++ )
		{
			wdgView = widView.get( i );
			info = (ShortcutInfo)wdgView.getItemInfo();
			if( info.intent.getComponent().getPackageName().equals( pkgName ) )
			{
				if( widget3d != null )
					Widget3DManager.getInstance().installWidget( widget3d );
				else
				{
					Root3D.deleteFromDB( wdgView.getItemInfo() );
					wdgView.remove();
					widView.remove( i );
				}
				return;
			}
		}
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			if( allWidget.get( i ).pkgName.equals( pkgName ) )
			{
				allWidget.get( i ).hasInstall = true;
				if( widget3d != null )
					allWidget.get( i ).resolveInfo = widget3d;
				break;
			}
		}
	}
	
	public static String GetDefaultWidgetImage(
			int i )
	{
		if( allWidget == null )
			return "";
		return allWidget.get( i ).imageName;
	}
	
	public static String GetDefaultWidgetImage(
			String pkgName )
	{
		if( allWidgetFinal == null )
			return "";
		String imageName = null;
		for( int i = 0 ; i < allWidgetFinal.size() ; i++ )
		{
			if( allWidgetFinal.get( i ).pkgName.equals( pkgName ) )
			{
				imageName = allWidgetFinal.get( i ).imageName;
				return imageName;
			}
		}
		return null;
	}
	
	public static String GetDefaultWidgetApkname(
			String pkgName ,
			String className )
	{
		if( allWidgetFinal == null )
			return null;
		String apkName = null;
		for( int i = 0 ; i < allWidgetFinal.size() ; i++ )
		{
			if( allWidgetFinal.get( i ).pkgName.equals( pkgName ) )
			{
				apkName = allWidgetFinal.get( i ).apkName;
				break;
			}
		}
		/* 下载时两个都需要检 */
		if( apkName == null )
		{
			for( int i = 0 ; i < allVirture.size() ; i++ )
			{
				if( allVirture.get( i ).className != null && className != null )
				{
					if( allVirture.get( i ).pkgName.equals( pkgName ) && allVirture.get( i ).className.equals( className ) )
					{
						apkName = allVirture.get( i ).apkName;
						break;
					}
				}
				else
				{
					if( allVirture.get( i ).pkgName.equals( pkgName ) )
					{
						apkName = allVirture.get( i ).apkName;
						break;
					}
				}
			}
		}
		return apkName;
	}
	
	public static boolean GetDefaultWidgetFromAirpush(
			String pkgName ,
			String className )
	{
		if( allWidgetFinal == null )
			return false;
		boolean fromAirpush = false;
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			if( allVirture.get( i ).className != null && className != null )
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) && allVirture.get( i ).className.equals( className ) )
				{
					fromAirpush = allVirture.get( i ).fromAirPush;
					break;
				}
			}
			else
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) )
				{
					fromAirpush = allVirture.get( i ).fromAirPush;
					break;
				}
			}
		}
		return fromAirpush;
	}
	
	public static String GetDefaultWidgetCustomID(
			String pkgName )
	{
		if( allWidgetFinal == null )
			return null;
		String customID = null;
		for( int i = 0 ; i < allWidgetFinal.size() ; i++ )
		{
			if( allWidgetFinal.get( i ).pkgName.equals( pkgName ) )
			{
				customID = allWidgetFinal.get( i ).customID;
				break;
			}
		}
		return customID;
	}
	
	public static String GetVirtureCustomID(
			String pkgName )
	{
		if( allVirture == null )
			return null;
		String customID = null;
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			if( allVirture.get( i ).pkgName.equals( pkgName ) )
			{
				customID = allVirture.get( i ).customID;
				break;
			}
		}
		return customID;
	}
	
	public static String GetVirtureCustomID(
			String pkgName ,
			String className )
	{
		if( allVirture == null )
			return null;
		String customID = null;
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			if( allVirture.get( i ).className != null && className != null )
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) && allVirture.get( i ).className.equals( className ) )
				{
					customID = allVirture.get( i ).customID;
					break;
				}
			}
			else
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) )
				{
					customID = allVirture.get( i ).customID;
					break;
				}
			}
		}
		return customID;
	}
	
	public static String GetDefaultWidgetNameWithPkgName(
			String pkgName )
	{
		if( allWidget == null )
			return null;
		String name = null;
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			if( allWidget.get( i ).pkgName.equals( pkgName ) )
			{
				name = allWidget.get( i ).name;
				break;
			}
		}
		return name;
	}
	
	public static String GetDefaultVirtureNameWithPkgName(
			String pkgName )
	{
		if( allVirture == null )
			return null;
		String name = null;
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			if( allVirture.get( i ).pkgName.equals( pkgName ) )
			{
				name = allVirture.get( i ).name;
				break;
			}
		}
		return name;
	}
	
	public static String GetDefaultVirtureNameWithPkgClassName(
			String pkgName ,
			String className )
	{
		if( allVirture == null )
			return null;
		String name = null;
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			if( allVirture.get( i ).className != null && className != null )
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) && allVirture.get( i ).className.equals( className ) )
				{
					name = allVirture.get( i ).name;
					break;
				}
			}
			else
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) )
				{
					name = allVirture.get( i ).name;
					break;
				}
			}
		}
		return name;
	}
	
	public static String GetDefaultWidgetImageWithNameInDesktop(
			String name )
	{
		if( allWidget == null )
			return null;
		String image_name = null;
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			if( allWidget.get( i ).name.equals( name ) )
			{
				image_name = THEME_WIDGET_FOLDER;
				image_name += allWidget.get( i ).imageName;
				break;
			}
		}
		return image_name;
	}
	
	public static String GetDefaultWidgetImageWithNameInDesktop(
			int i )
	{
		if( allWidget == null )
			return null;
		String image_name = THEME_WIDGET_APPLIST;
		image_name += allWidget.get( i ).imageName;
		return image_name;
	}
	
	public static boolean isWidgetLoadByInternal(
			ShortcutInfo info )
	{
		boolean loadByInternal = false;
		if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW )
		{
			for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
			{
				if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals( info.intent.getComponent().getPackageName() ) )
				{
					loadByInternal = true;
					break;
				}
			}
		}
		return loadByInternal;
	}
	
	public static String getWidgetItemClassName(
			String packageName )
	{
		String className = "";
		for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
		{
			if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals( packageName ) && DefaultLayout.allWidgetFinal.get( i ).loadByInternal )
			{
				className = DefaultLayout.allWidgetFinal.get( i ).className;
				break;
			}
		}
		return className;
	}
	
	public static boolean isWidgetLoadByInternal(
			String packageName )
	{
		boolean loadByInternal = false;
		for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
		{
			if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals( packageName ) && DefaultLayout.allWidgetFinal.get( i ).loadByInternal )
			{
				loadByInternal = true;
				break;
			}
		}
		return loadByInternal;
	}
	
	public static String getThumbName(
			String packageName )
	{
		String thumbName = "";
		for( int i = 0 ; i < DefaultLayout.allWidgetFinal.size() ; i++ )
		{
			if( DefaultLayout.allWidgetFinal.get( i ).pkgName.equals( packageName ) )
			{
				thumbName = DefaultLayout.allWidgetFinal.get( i ).thumbName;
				break;
			}
		}
		return thumbName;
	}
	
	public static String InternalWidgetBitmap(
			String packageName )
	{
		for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
		{
			if( DefaultLayout.allWidget.get( i ).pkgName.equals( ( packageName ) ) )
			{
				String bitmap = DefaultLayout.allWidget.get( i ).imageName;
				if( RR.net_version )
				{
					if( DefaultLayout.allWidget.get( i ).loadByInternal )
					{
						String widgetShortName;
						String imageName = "";
						widgetShortName = packageName.substring( packageName.lastIndexOf( "." ) + 1 ).toLowerCase( Locale.ENGLISH );
						if( DefaultLayout.allWidget.get( i ).thumbName != null && !DefaultLayout.allWidget.get( i ).thumbName.equals( "" ) )
						{
							imageName = "theme/widget/" + widgetShortName + "/" + DefaultLayout.allWidget.get( i ).thumbName + "/image/widget_ico.png";
						}
						return imageName;
					}
					// if(packageName.equals("com.iLoong.Clock" )){
					// bitmap="theme/widget/clock/iLoongClock/image/"+DefaultLayout.allWidget.get(
					// i ).imageName;
					// }
					// else if(packageName.equals("com.cooee.searchbar" )){
					// bitmap="theme/widget/searchbar/iLoongSearchBar/image/"+DefaultLayout.allWidget.get(
					// i ).imageName;
					// }
				}
			}
		}
		return "";
	}
	
	public static String InternalWidgetTitle(
			String packageName )
	{
		for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
		{
			if( DefaultLayout.allWidget.get( i ).pkgName.equals( ( packageName ) ) )
			{
				String title = DefaultLayout.allWidget.get( i ).name;
				return title;
			}
		}
		return "";
	}
	
	public static int InternalWidgetSpanX(
			String packageName )
	{
		for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
		{
			if( DefaultLayout.allWidget.get( i ).pkgName.equals( ( packageName ) ) )
			{
				int spanX = DefaultLayout.allWidget.get( i ).spanX;
				return spanX;
			}
		}
		return 0;
	}
	
	public static int InternalWidgetSpanY(
			String packageName )
	{
		for( int i = 0 ; i < DefaultLayout.allWidget.size() ; i++ )
		{
			if( DefaultLayout.allWidget.get( i ).pkgName.equals( ( packageName ) ) )
			{
				int spanY = DefaultLayout.allWidget.get( i ).spanY;
				return spanY;
			}
		}
		return 0;
	}
	
	public static void setIconCache(
			IconCache cache )
	{
		iconCache = cache;
	}
	
	public static void setEnv(
			Root3D rootPara ,
			Workspace3D workspacePara )
	{
		Log.v( "test" , "setEnv" );
		root = rootPara;
		workspace = workspacePara;
		sideBar = root.getHotSeatBar();
	}
	
	public static boolean hasInstallWidget(
			String pkgName )
	{
		if( allWidget == null )
			return false;
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			Log.d( "launcher" , "isWidgetInstalled:" + allWidget.get( i ).pkgName );
			if( allWidget.get( i ).pkgName.equals( pkgName ) )
			{
				return allWidget.get( i ).hasInstall;
			}
		}
		return false;
	}
	
	public static ResolveInfo getInstallWidgetResolveInfo(
			String pkgName )
	{
		if( allWidget == null )
			return null;
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			Log.d( "launcher" , "getInstallWidgetResolveInfo:" + allWidget.get( i ).pkgName );
			if( allWidget.get( i ).pkgName.equals( pkgName ) )
			{
				return allWidget.get( i ).resolveInfo;
			}
		}
		return null;
	}
	
	public static String GetDefaultImageWithPkgName(
			String pkgName )
	{
		if( allWidget == null )
			return null;
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			Log.d( "launcher" , allWidget.get( i ).pkgName );
			if( allWidget.get( i ).pkgName.equals( pkgName ) )
			{
				return THEME_WIDGET_FOLDER + allWidget.get( i ).imageName;
			}
		}
		Log.d( "launcher" , "no default image" );
		return null;
	}
	
	public static String GetVirtureImageWithPkgClassName(
			String pkgName ,
			String className )
	{
		if( allVirture == null )
			return null;
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			if( allVirture.get( i ).className != null && className != null )
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) && allVirture.get( i ).className.equals( className ) )
				{
					if( DefaultLayout.useCustomVirtual )
						return DefaultLayout.custom_virtual_path + "/" + allVirture.get( i ).imageName;
					else if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/" + THEME_VIRTURE_FOLDER + allVirture.get( i ).imageName ) )
						return DefaultLayout.custom_assets_path + "/" + THEME_VIRTURE_FOLDER + allVirture.get( i ).imageName;
					return THEME_VIRTURE_FOLDER + allVirture.get( i ).imageName;
				}
			}
			else
			{
				if( allVirture.get( i ).pkgName.equals( pkgName ) )
				{
					if( DefaultLayout.useCustomVirtual )
						return DefaultLayout.custom_virtual_path + "/" + allVirture.get( i ).imageName;
					else if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/" + THEME_VIRTURE_FOLDER + allVirture.get( i ).imageName ) )
						return DefaultLayout.custom_assets_path + "/" + THEME_VIRTURE_FOLDER + allVirture.get( i ).imageName;
					return THEME_VIRTURE_FOLDER + allVirture.get( i ).imageName;
				}
			}
		}
		return null;
	}
	
	public static String pickNameByLanSetting(
			String name )
	{
		String res = null;
		String tmp = null;
		int curLan = iLoongLauncher.curLanguage;
		if( name == null )
			return null;
		ArrayList<String> nameList = new ArrayList<String>();
		GetShortcutItemString( nameList , name );
		if( nameList.size() < curLan + 1 )
			return nameList.get( 0 );
		tmp = nameList.get( curLan );
		if( tmp != null )
			res = tmp;
		else
			res = nameList.get( 0 );/* if not exit return zh_SM */
		return res;
	}
	
	public static void GetShortcutItemString(
			ArrayList<String> stringArray ,
			String allName )
	{
		if( allName == null )
			return;
		int length = allName.length();
		int start = 0;
		int end = 0;
		String part = allName;
		String leftName = allName;
		int limit = ';'; /* delimiter */
		stringArray.clear();
		if( length <= 0 )
			return;
		while( leftName != null )
		{
			end = leftName.indexOf( limit );
			if( end == -1 )
			{
				part = leftName;
			}
			else
				part = leftName.substring( start , end );
			// Log.v("test", "part string = <<" + part + ">>");
			if( end == -1 )
				leftName = null;
			else
				leftName = leftName.substring( end + 1 );
			if( part != null )
				stringArray.add( part );
		}
	}
	
	void MakeIconForItem(
			ResolveInfo info ,
			ShortcutItem item )
	{
		// ShortcutInfo shortcutInfo = null;
		/*
		 * if (item.imageName != null && !item.imageName.isEmpty()) { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); } else { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); }
		 */
		item.info = new ApplicationInfo( info , iLoongApplication.mIconCache ).makeShortcut();
		// item.icon = new
		// Icon3D(shortcutInfo.title.toString(),shortcutInfo.getIcon(iconCache),shortcutInfo.title.toString());
		// item.icon = new
		// Icon3D(shortcutInfo.title.toString(),R3D.findRegion(shortcutInfo));
		// item.icon.setItemInfo(shortcutInfo);
	}
	
	/*
	 * 在我们的系统中，如果dip设定�?7，在density�?.5的屏幕上返回值为86，尺寸为85的图片，在比较是�?
	 * 要加框或者缩放的时候，用bitmap.getWidth()的时候，得到�?5和app_icon_size相比较的时候，总是
	 * 不能成功。将bitmap.getWidth()获取的PX值圆整为我们通过dip的方式获取PX的方法�?�?5的px先转换为
	 * 57dip,然后转换为px�?6，这样比较的基础就一样了。added by zfshi
	 */
	public static int pxRoundToOurValue(
			int pxValue )
	{
		int retVal = pxValue;
		retVal = Tools.px2dip( iLoongLauncher.getInstance() , pxValue );
		retVal = Tools.dip2px( iLoongLauncher.getInstance() , retVal );
		return retVal;
	}
	
	void changeIcon(
			ResolveInfo info ,
			String image )
	{
		if( image == null || info == null )
			return;
		Bitmap bitmap = GetPKGThemeIconPath( image );
		// boolean bNeedScale=false;
		if( bitmap != null )
		{
			// if
			// (pxRoundToOurValue(bitmap.getWidth())!=DefaultLayout.app_icon_size
			// && DefaultLayout.thirdapk_icon_scaleFactor!=1.0f)
			// {
			// bNeedScale=true;
			// }
			bitmap = Utilities.resampleIconBitmap( bitmap , iLoongLauncher.getInstance() );
			// if (bNeedScale)
			// {
			// bitmap=Tools.resizeBitmap(bitmap,
			// DefaultLayout.thirdapk_icon_scaleFactor);
			// }
			iconCache.setIcon( info , bitmap );
		}
	}
	
	void MakeIconForVirtual(
			ResolveInfo info ,
			VirtureIcon virIcon )
	{
		// ShortcutInfo shortcutInfo = null;
		/*
		 * if (item.imageName != null && !item.imageName.isEmpty()) { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); } else { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); }
		 */
		virIcon.info = new ApplicationInfo( info , iLoongApplication.mIconCache ).makeShortcut();
		// item.icon = new
		// Icon3D(shortcutInfo.title.toString(),shortcutInfo.getIcon(iconCache),shortcutInfo.title.toString());
		// virIcon.icon = new
		// Icon3D(shortcutInfo.title.toString(),R3D.findRegion(shortcutInfo));
		// virIcon.icon.setItemInfo(shortcutInfo);
	}
	
	int checkAppType(
			String pname )
	{
		try
		{
			PackageInfo pInfo = iLoongLauncher.getInstance().getPackageManager().getPackageInfo( pname , 0 );
			// 是系统软件或者是系统软件更新
			if( isSystemApp( pInfo ) || isSystemUpdateApp( pInfo ) )
			{
				return 1; /* system app */
			}
			else
			{
				return 0;/* user app */
			}
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean isSystemApp(
			PackageInfo pInfo )
	{
		return( ( pInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 );
	}
	
	public boolean isSystemUpdateApp(
			PackageInfo pInfo )
	{
		return( ( pInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 );
	}
	
	public boolean isUserApp(
			PackageInfo pInfo )
	{
		return( !isSystemApp( pInfo ) && !isSystemUpdateApp( pInfo ) );
	}
	
	public void setDefaultIcon()
	{
		int i = 0;
		int size = defaultIcon.size();
		if( size < 1 )
			return;
		for( i = 0 ; i < size ; i++ )
		{
			if( !defaultIcon.get( i ).dealed )
			{
				defaultIcon.get( i ).pkgNameArray = new ArrayList<String>();
				GetShortcutItemString( defaultIcon.get( i ).pkgNameArray , defaultIcon.get( i ).pkgName );
			}
		}
		for( i = 0 ; i < size ; i++ )
		{
			if( !defaultIcon.get( i ).dealed )
			{
				defaultIcon.get( i ).classNameArray = new ArrayList<String>();
				GetShortcutItemString( defaultIcon.get( i ).classNameArray , defaultIcon.get( i ).className );
			}
		}
		// for (i = 0; i < defaultIcon.size(); i++) {
		// if (!defaultIcon.get(i).dealed) {
		// Log.v("test", "package name ="
		// + defaultIcon.get(i).pkgNameArray.get(0)
		// + "is not found");
		// }
		// }
		checkDefaultIconListHaveMatchIconInCurrentTheme();
	}
	
	public static String getGalleryPackage()
	{
		int i = 0;
		int size = defaultIcon.size();
		if( size < 1 )
			return null;
		for( i = 0 ; i < size ; i++ )
		{
			if( "图库".equals( defaultIcon.get( i ).title ) )
			{
				return defaultIcon.get( i ).pkgName;
			}
		}
		return null;
	}
	
	public void getDefaultIcon(
			ResolveInfo info )
	{
		ArrayList<String> allNamePatten;
		String packageName = info.activityInfo.applicationInfo.packageName;
		DefaultIcon tem = new DefaultIcon(); // added by zhenNan.ye
		// Log.e("getDefaultIcon",
		// "packageName:"+packageName+" className:"+info.activityInfo.name);
		// if (checkAppType(appName) == 1) {/* 内置应用 */
		for( int inter = 0 ; inter < defaultIcon.size() ; inter++ )
		{
			// if (defaultIcon.get(inter).dealed)
			// continue;
			allNamePatten = defaultIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.size() <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.size() ; k++ )
			{
				if( packageName.equals( allNamePatten.get( k ) ) )
				{
					if( defaultIcon.get( inter ).duplicate )
					{
						ArrayList<String> allCompNames = defaultIcon.get( inter ).classNameArray;
						for( int m = 0 ; m < allCompNames.size() ; m++ )
						{
							if( info.activityInfo.name.equals( allCompNames.get( m ) ) )
							{
								changeIcon( info , defaultIcon.get( inter ).imageName );
								if( defaultIcon.get( inter ).title.equals( "浏览器" ) )
								{
									if( packageName != null && allCompNames.get( m ) != null )
									{
										SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
										Editor editor = prefs.edit();
										editor.putString( "BROWERpackagerName" , packageName );
										editor.putString( "BROWERclassName" , allCompNames.get( m ) );
										editor.commit();
									}
								}
								// added by hugo.ye begin
								if( needToSaveSpecifiedIconXml )
								{
									tem.title = defaultIcon.get( inter ).title;
									tem.duplicate = defaultIcon.get( inter ).duplicate;
									if( packageName.equals( "com.cooeeui.brand.turbolauncher" ) )
									{
										tem.pkgName = defaultIcon.get( inter ).pkgName;
									}
									else
									{
										tem.pkgName = packageName;
									}
									tem.className = allCompNames.get( m );
									tem.imageName = defaultIcon.get( inter ).imageName;
									customDefaultIcon.add( tem );
								}
								return;
								// added by hugo.ye end		
							}
						}
					}
					else
					{
						// defaultIcon.get(inter).dealed = true;
						changeIcon( info , defaultIcon.get( inter ).imageName );
						if( defaultIcon.get( inter ).title.equals( "时钟" ) )
						{
							SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
							Editor editor = prefs.edit();
							editor.putString( "CLOCKpackagerName" , packageName );
							editor.commit();
						}
						// added by zhenNan.ye begin
						if( needToSaveSpecifiedIconXml )
						{
							tem.title = defaultIcon.get( inter ).title;
							if( packageName.equals( "com.cooeeui.brand.turbolauncher" ) )
							{
								tem.pkgName = defaultIcon.get( inter ).pkgName;
							}
							else
							{
								tem.pkgName = packageName;
							}
							tem.imageName = defaultIcon.get( inter ).imageName;
							customDefaultIcon.add( tem );
						}
						return;
						// added by zhenNan.ye end
					}
				}
			}
		}
		if( dynamic_icon )
		{
			String imageName = DrawDynamicIcon.changeDynamicIcon( info );
			if( imageName != null )
			{
				changeIcon( info , imageName );
			}
		}
	}
	
	public void getDynamicInfo()
	{
		if( DrawDynamicIcon.readXmlMode == 0 && dynamiciconlist.size() == 0 )
		{
			dynamiciconlist.addAll( sysDynamiciconlist );
			dynamicIcon.clear();
		}
		else if( DrawDynamicIcon.readXmlMode == 1 && dynamicIcon.size() == 0 )
		{
			dynamicIcon.addAll( sysDynamicIcon );
		}
		else if( dynamiciconlist.size() > 0 && dynamicIcon.size() != dynamiciconlist.size() )
		{
			dynamicIcon.clear();
		}
		if( dynamiciconlist == null || dynamicIcon.size() != 0 )
		{
			return;
		}
		for( String dynamic : dynamiciconlist )
		{
		}
	}
	
	public void setDynamicIcon()
	{
		int i = 0;
		int size = dynamicIcon.size();
		if( size < 1 )
			return;
		for( i = 0 ; i < size ; i++ )
		{
			dynamicIcon.get( i ).pkgNameArray = dynamicIcon.get( i ).pkgName.split( ";" );
		}
		for( i = 0 ; i < size ; i++ )
		{
			dynamicIcon.get( i ).compNameArray = dynamicIcon.get( i ).compName.split( ";" );
		}
	}
	
	public void setDefaultShortcutIcon()
	{
		int i = 0;
		int size = defaultshortcutIcon.size();
		if( size < 1 )
			return;
		for( i = 0 ; i < size ; i++ )
		{
			defaultshortcutIcon.get( i ).pkgNameArray = defaultshortcutIcon.get( i ).pkgName.split( ";" );
		}
		for( i = 0 ; i < size ; i++ )
		{
			defaultshortcutIcon.get( i ).compNameArray = defaultshortcutIcon.get( i ).className.split( ";" );
		}
	}
	
	public boolean hasReplaceIcon(
			String appName ,
			String compName )
	{
		boolean bRet = false;
		ArrayList<String> allNamePatten;
		for( int inter = 0 ; inter < defaultIcon.size() ; inter++ )
		{
			allNamePatten = defaultIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.size() <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.size() ; k++ )
			{
				if( appName.equals( allNamePatten.get( k ) ) )
				{
					if( defaultIcon.get( inter ).duplicate )
					{
						ArrayList<String> allCompNames = defaultIcon.get( inter ).classNameArray;
						for( int m = 0 ; m < allCompNames.size() ; m++ )
						{
							if( compName.equals( allCompNames.get( m ) ) )
							{
								return isImageExist( defaultIcon.get( inter ).imageName );
							}
						}
					}
					else
					{
						return isImageExist( defaultIcon.get( inter ).imageName );
					}
				}
			}
		}
		// }
		return bRet;
	}
	
	public boolean hasReplaceShortcutIcon(
			String appName ,
			String compName )
	{
		boolean bRet = false;
		String[] allNamePatten;
		for( int inter = 0 ; inter < defaultshortcutIcon.size() ; inter++ )
		{
			allNamePatten = defaultshortcutIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.length <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.length ; k++ )
			{
				if( appName.equals( allNamePatten[k] ) )
				{
					String[] allCompNames = defaultshortcutIcon.get( inter ).compNameArray;
					for( int m = 0 ; m < allCompNames.length ; m++ )
					{
						if( compName.equals( allCompNames[m] ) )
						{
							return isImageExist( defaultshortcutIcon.get( inter ).imageName );
						}
					}
				}
			}
		}
		return bRet;
	}
	
	// xiatian add start
	// fix bug:when a icon in replace list,but a theme do not have this icon.in
	// the theme this icon use default Theme icon and not have iconBg and not
	// scale
	public String getReplaceIconPath(
			String appName ,
			String compName )
	{
		ArrayList<String> allNamePatten;
		for( int inter = 0 ; inter < defaultIcon.size() ; inter++ )
		{
			allNamePatten = defaultIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.size() <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.size() ; k++ )
			{
				if( appName.equals( allNamePatten.get( k ) ) )
				{
					if( defaultIcon.get( inter ).duplicate )
					{
						ArrayList<String> allCompNames = defaultIcon.get( inter ).classNameArray;
						for( int m = 0 ; m < allCompNames.size() ; m++ )
						{
							if( compName.equals( allCompNames.get( m ) ) )
							{
								return( THEME_ICON_FOLDER + THEME_NAME + defaultIcon.get( inter ).imageName );
							}
						}
					}
					else
					{
						return( THEME_ICON_FOLDER + THEME_NAME + defaultIcon.get( inter ).imageName );
					}
				}
			}
		}
		return null;
	}
	
	// xiatian add end
	public static String getDefaultVirtureImage(
			String packageName )
	{
		if( packageName == null )
			return null;
		ArrayList<String> allNamePatten;
		DefaultIcon tem = new DefaultIcon(); // added by zhenNan.ye
		for( int inter = 0 ; inter < defaultIcon.size() ; inter++ )
		{
			allNamePatten = defaultIcon.get( inter ).pkgNameArray;
			if( allNamePatten == null || allNamePatten.size() <= 0 )
				continue;
			for( int k = 0 ; k < allNamePatten.size() ; k++ )
			{
				if( packageName.equals( allNamePatten.get( k ) ) )
				{
					// added by hugo.ye begin
					if( needToSaveSpecifiedIconXml )
					{
						tem.title = defaultIcon.get( inter ).title;
						tem.duplicate = defaultIcon.get( inter ).duplicate;
						tem.pkgName = defaultIcon.get( inter ).pkgName;
						tem.className = defaultIcon.get( inter ).className;
						tem.imageName = defaultIcon.get( inter ).imageName;
						customDefaultIcon.add( tem );
					}
					// added by hugo.ye end
					return THEME_ICON_FOLDER + THEME_NAME + defaultIcon.get( inter ).imageName;
				}
			}
		}
		return null;
	}
	
	public void GetFactoryAppInfo()
	{
		if( show_default_app_sort )
		{
			for( int i = 0 ; i < facApp.size() ; i++ )
			{
				facApp.get( i ).pkgNameArray = new ArrayList<String>();
				GetShortcutItemString( facApp.get( i ).pkgNameArray , facApp.get( i ).packageName );
			}
		}
		else
			facApp.clear();
	}
	
	public void GetAllViewInfo()
	{
		ArrayList<ShortcutItem> temp = new ArrayList<ShortcutItem>();
		int i , j , k = 0;
		int total = 0;
		int remove = 0;
		for( i = 0 ; i < allShortcutList.size() ; i++ )
		{
			if( allShortcutList.get( i ).shortcutList != null && allShortcutList.get( i ).shortcutList.size() > 0 )
			{
				for( j = 0 ; j < allShortcutList.get( i ).shortcutList.size() ; j++ )
				{
					temp.add( total , allShortcutList.get( i ).shortcutList.get( j ) );
					total++;
				}
			}
			if( allShortcutList.get( i ).folderList != null && allShortcutList.get( i ).folderList.size() > 0 )
			{
				for( k = 0 ; k < allShortcutList.get( i ).folderList.size() ; k++ )
				{
					if( allShortcutList.get( i ).folderList.get( k ).shortcutList != null && allShortcutList.get( i ).folderList.get( k ).shortcutList.size() > 0 )
					{
						for( j = 0 ; j < allShortcutList.get( i ).folderList.get( k ).shortcutList.size() ; j++ )
						{
							temp.add( total , allShortcutList.get( i ).folderList.get( k ).shortcutList.get( j ) );
							total++;
						}
					}
				}
			}
		}
		/* init string array */
		for( i = 0 ; i < temp.size() ; i++ )
		{
			if( !temp.get( i ).isContact && temp.get( i ).info == null )
			{
				temp.get( i ).pkgNameArray = new ArrayList<String>();
				GetShortcutItemString( temp.get( i ).pkgNameArray , temp.get( i ).pkgName );
				temp.get( i ).classNameArray = new ArrayList<String>();
				GetShortcutItemString( temp.get( i ).classNameArray , temp.get( i ).className );
			}
		}
		/* init shortcut info */
		for( i = 0 ; i < allApp.size() ; i++ )
		{
			ArrayList<String> packageNamePatten;
			ResolveInfo info = allApp.get( i );
			String packageName = info.activityInfo.applicationInfo.packageName;
			String className = info.activityInfo.name;
			for( int indexVir = 0 ; indexVir < allVirture.size() ; indexVir++ )
			{
				VirtureIcon virIcon = allVirture.get( indexVir );
				if( virIcon.info != null )
					continue;
				if( virIcon.className != null )
				{
					if( packageName.equals( virIcon.pkgName ) && className.equals( virIcon.className ) )
					{
						MakeIconForVirtual( info , virIcon );
					}
				}
				else
				{
					if( packageName.equals( virIcon.pkgName ) )
					{
						MakeIconForVirtual( info , virIcon );
					}
				}
			}
			App:
			for( j = 0 ; j < temp.size() ; j++ )
			{
				String part = null;
				ShortcutItem item = temp.get( j );
				if( item.isContact && item.info == null )
				{
					item.info = Contact3DShortcut.addContactInfo();
				}
				if( item.info != null )
					continue;
				packageNamePatten = item.pkgNameArray;
				if( packageNamePatten.size() <= 0 )
					continue;
				for( k = 0 ; k < packageNamePatten.size() ; k++ )
				{
					if( packageNamePatten.get( k ).charAt( 0 ) == '*' )
					{
						part = packageNamePatten.get( k ).substring( 1 );
						if( packageName.lastIndexOf( part ) != -1 )
						{
							MakeIconForItem( info , item );
							// break App;
						}
					}
					else
					{
						if( packageName.equals( packageNamePatten.get( k ) ) )
						{
							if( item.classNameArray.size() > 0 )
							{
								int count = item.classNameArray.size();
								for( int cmp = 0 ; cmp < count ; cmp++ )
								{
									if( item.classNameArray.get( cmp ).equals( className ) )
									{
										MakeIconForItem( info , item );
										// break App;
									}
								}
							}
							else
							{
								MakeIconForItem( info , item );
								// break App;
							}
						}
					}
				}
			}
		}
		/* remove items never be found */
		for( i = 0 ; i < allShortcutList.size() ; i++ )
		{
			if( allShortcutList.get( i ).shortcutList != null && allShortcutList.get( i ).shortcutList.size() > 0 )
			{
				for( j = 0 ; j < allShortcutList.get( i ).shortcutList.size() ; j++ )
				{
					// temp.add(total,
					// allShortcutList.get(i).shortcutList.get(j));
					if( allShortcutList.get( i ).shortcutList.get( j ).info == null )
					{
						allShortcutList.get( i ).shortcutList.remove( j );
						remove++;
					}
				}
			}
			if( allShortcutList.get( i ).folderList != null && allShortcutList.get( i ).folderList.size() > 0 )
			{
				for( k = 0 ; k < allShortcutList.get( i ).folderList.size() ; k++ )
				{
					if( allShortcutList.get( i ).folderList.get( k ).shortcutList != null && allShortcutList.get( i ).folderList.get( k ).shortcutList.size() > 0 )
					{
						for( j = 0 ; j < allShortcutList.get( i ).folderList.get( k ).shortcutList.size() ; j++ )
						{
							temp.add( total , allShortcutList.get( i ).folderList.get( k ).shortcutList.get( j ) );
							if( allShortcutList.get( i ).folderList.get( k ).shortcutList.get( j ).info == null )
							{
								allShortcutList.get( i ).folderList.get( k ).shortcutList.remove( j );
								remove++;
							}
						}
					}
				}
			}
		}
		temp.clear();
	}
	
	public void InsertAllItem()
	{
		Log.d( "launcher" , "insert all item" );
		GetAllViewInfo();
		InsertDefaultShortcut();
		InsertDefaultWidget();
		InsertVirtureIcon();
		InsertHotSeatIcon();
		insertSmartLayout();
		if( iLoongApplication.BuiltIn )
			InsertSysWidget();
		InsertSysShortcut();
		if( enable_news )
			insertCooeePlugin();
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			prefs.edit().putBoolean( "first_run_big_icon" , false ).commit();
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			prefs.edit().putBoolean( "first_run_small_icon" , false ).commit();
		}
		else
		{
			prefs.edit().putBoolean( "first_run" , false ).commit();
		}
		release();
	}
	
	private void release()
	{
		allShortcutList.clear();
		allFolder.clear();
		allSysWidget.clear();
		allSysShortcut.clear();
		cooeePlugins.clear();
	}
	
	void InsertShortCut(
			ShortcutGRP grp )
	{
		int i = 0;
		if( grp.locate != null && grp.locate.equals( "sidebar" ) && ( DefaultLayout.newHotSeatMainGroup == false )// xiatian add
																													// //newHotSeatMainGroup
		)
		{
			if( grp.shortcutList.size() > 0 )
			{
				// ArrayList<View3D> list = new ArrayList<View3D>();
				for( i = 0 ; i < grp.shortcutList.size() ; i++ )
				{
					if( grp.shortcutList.get( i ).info != null )
					{
						// grp.shortcutList.get(i).info.container =
						// LauncherSettings.Favorites.CONTAINER_SIDEBAR;
						grp.shortcutList.get( i ).info.angle = HotSeat3D.TYPE_ICON;
						Root3D.addOrMoveDB( grp.shortcutList.get( i ).info , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
					}
				}
				// root.getSidebar().onDrop(list, 0, 0);
			}
		}
		else if( grp.locate != null && grp.locate.equals( "workspace" ) )
		{/* workspace */
			if( grp.shortcutList != null && grp.shortcutList.size() > 0 )
			{
				ItemInfo info = null;
				int iconWidth = 0;
				int width = Utils3D.getScreenWidth() / grp.shortcutList.size();
				if( grp.shortcutList.get( 0 ).info != null )
					iconWidth = R3D.workspace_cell_width;
				if( width < iconWidth )
				{
					iconWidth = width;
				}
				for( i = 0 ; i < grp.shortcutList.size() ; i++ )
				{
					ShortcutItem item = grp.shortcutList.get( i );
					if( item.info != null )
					{
						info = item.info;
						info.x = (int)( ( width - iconWidth ) / 2 ) + i * width;
						info.y = R3D.def_layout_y;
						info.screen = grp.value;
						info.cellX = item.cellX;
						info.cellY = item.cellY;
						Root3D.addOrMoveDB( info );
					}
				}
			}
			if( grp.folderList != null && grp.folderList.size() > 0 )
			{
				for( i = 0 ; i < grp.folderList.size() ; i++ )
					InsertFolder( grp.folderList.get( i ) , grp.value , i , grp.folderList.size() );
			}
		}
		else if( grp.locate != null && grp.locate.equals( "applist" ) )
		{/* applist */
			if( grp.folderList != null && grp.folderList.size() > 0 )
			{
				for( i = 0 ; i < grp.folderList.size() ; i++ )
					InsertAppListFolder( grp.folderList.get( i ) , i , grp.folderList.size() );
			}
		}
	}
	
	void InsertFolder(
			FolderList folder ,
			int screen ,
			int index ,
			int total )
	{
		int i = 0;
		ItemInfo info = null;
		// folderinfo fo = (FolderIcon3D)
		// SidebarMainGroup.folder3DHost.getWidget3D();
		UserFolderInfo fo = iLoongLauncher.getInstance().addFolder( folder.name );
		fo.iconResource = folder.iconResource;
		int width = Utils3D.getScreenWidth() / 4;
		int folderWidth = R3D.sidebar_widget_w;
		info = fo;
		info.x = (int)( ( width - folderWidth ) / 2 ) + index * width;
		info.y = R3D.def_layout_y;
		info.cellX = folder.cellX;
		info.cellY = folder.cellY;
		info.cellTempX = folder.cellX;
		info.cellTempY = folder.cellY;
		info.screen = screen;
		LauncherModel.updateItemInDatabase( iLoongLauncher.getInstance() , fo );
		Root3D.addOrMoveDB( info );
		if( folder.shortcutList != null )
			for( i = 0 ; i < folder.shortcutList.size() ; i++ )
			{
				if( folder.shortcutList.get( i ).info != null )
				{
					Root3D.addOrMoveDB( folder.shortcutList.get( i ).info , fo.id );
				}
			}
		allFolder.put( folder , fo );
	}
	
	void InsertAppListFolder(
			FolderList folder ,
			int index ,
			int total )
	{
		int i = 0;
		// folderinfo fo = (FolderIcon3D)
		// SidebarMainGroup.folder3DHost.getWidget3D();
		UserFolderInfo fo = iLoongLauncher.getInstance().addFolder( folder.name );
		// fo.container = LauncherSettings.Favorites.CONTAINER_APPLIST;
		// Root3D.updateItemInDatabase(fo);
		Root3D.addOrMoveDB( fo , LauncherSettings.Favorites.CONTAINER_APPLIST );
		if( folder.shortcutList != null )
		{
			for( i = 0 ; i < folder.shortcutList.size() ; i++ )
			{
				if( folder.shortcutList.get( i ).info != null )
				{
					Root3D.addOrMoveDB( folder.shortcutList.get( i ).info , fo.id );
				}
			}
		}
		allFolder.put( folder , fo );
	}
	
	public void InsertSysWidget()
	{
		Context context = iLoongLauncher.getInstance();
		Log.v( "test" , "allSysWidget size = " + allSysWidget.size() );
		for( int i = 0 ; i < allSysWidget.size() ; i++ )
		{
			SysWidget widget = allSysWidget.get( i );
			if( widget.packageName == null || widget.className == null )
			{
				continue;
			}
			boolean hasPackage = true;
			ComponentName cn = new ComponentName( widget.packageName , widget.className );
			PackageManager packageManager = context.getPackageManager();
			try
			{
				packageManager.getReceiverInfo( cn , 0 );
			}
			catch( Exception e )
			{
				String[] packages = packageManager.currentToCanonicalPackageNames( new String[]{ widget.packageName } );
				cn = new ComponentName( packages[0] , widget.className );
				try
				{
					packageManager.getReceiverInfo( cn , 0 );
				}
				catch( Exception e1 )
				{
					hasPackage = false;
				}
			}
			if( hasPackage )
			{
				final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( context );
				try
				{
					int appWidgetId = iLoongLauncher.getInstance().mAppWidgetHost.allocateAppWidgetId();
					Widget2DInfo appWidgetInfo = new Widget2DInfo( appWidgetId );
					appWidgetInfo.setInfo( widget.packageName , widget.className );
					appWidgetInfo.cellX = widget.cellX;
					appWidgetInfo.cellY = widget.cellY;
					appWidgetInfo.spanX = widget.spanX;
					appWidgetInfo.spanY = widget.spanY;
					appWidgetInfo.screen = widget.screen;
					LauncherModel.addSysWidgetInDatabase( context , appWidgetInfo );
					// //if(DefaultLayout.net_version){
					// //=============================Jone modify for matching
					// 4.1 or upper.================================
					// //label: JONE_MOD_WIDGET
					// appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId,
					// cn);
					//
					// //==================================original
					// source====================================
					// //appWidgetManager.bindAppWidgetId( appWidgetId , cn );
					// //=====================================================================================
					// xujin 适用于api 16+
					UtilsBase.bindAppWidgetId( appWidgetManager , appWidgetId , cn );
					// appWidgetManager.bindAppWidgetId( appWidgetId , cn );
				}
				catch( RuntimeException ex )
				{
					Log.e( "launcher" , "Problem allocating appWidgetId" , ex );
				}
			}
			else
				continue;
		}
	}
	
	public void InsertSysShortcut()
	{
		Context context = iLoongLauncher.getInstance();
		Log.v( "test" , "allSysShortcut size = " + allSysShortcut.size() );
		for( int i = 0 ; i < allSysShortcut.size() ; i++ )
		{
			SysShortcut shortcut = allSysShortcut.get( i );
			if( shortcut.packageName == null || shortcut.className == null )
			{
				continue;
			}
			boolean hasPackage = true;
			ComponentName cn = new ComponentName( shortcut.packageName , shortcut.className );
			PackageManager packageManager = context.getPackageManager();
			try
			{
				packageManager.getActivityInfo( cn , 0 );
			}
			catch( Exception e )
			{
				String[] packages = packageManager.currentToCanonicalPackageNames( new String[]{ shortcut.packageName } );
				cn = new ComponentName( packages[0] , shortcut.className );
				try
				{
					packageManager.getActivityInfo( cn , 0 );
				}
				catch( Exception e1 )
				{
					hasPackage = false;
				}
			}
			if( hasPackage )
			{
				Bitmap bitmap;
				Bitmap icon = null;
				boolean customIcon = false;
				ShortcutIconResource iconResource = null;
				InputStream is = null;
				String imgname;
				if( DefaultLayout.useCustomSysShortcut )
				{
					imgname = DefaultLayout.custom_sys_shortcut_path + "/" + shortcut.imageName;
					try
					{
						is = new FileInputStream( imgname );
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
					}
				}
				else if( isCustomAssetsFileExist( "/" + THEME_SYS_SHORTCUT_FOLDER + shortcut.imageName ) )
				{
					imgname = DefaultLayout.custom_assets_path + "/" + THEME_SYS_SHORTCUT_FOLDER + shortcut.imageName;
					try
					{
						is = new FileInputStream( imgname );
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
					}
				}
				else
				{
					imgname = THEME_SYS_SHORTCUT_FOLDER + shortcut.imageName;
					is = ThemeManager.getInstance().getInputStream( imgname );
				}
				bitmap = BitmapFactory.decodeStream( is );
				try
				{
					is.close();
				}
				catch( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if( bitmap != null && bitmap instanceof Bitmap )
				{
					icon = Utilities.createIconBitmap( new BitmapDrawable( (Bitmap)bitmap ) , context );
					( (Bitmap)bitmap ).recycle();
					customIcon = true;
				}
				Intent intent = new Intent();
				intent.setClassName( shortcut.packageName , shortcut.className );
				final ShortcutInfo info = new ShortcutInfo();
				info.setIcon( icon );
				info.title = shortcut.name;
				info.intent = intent;
				info.customIcon = customIcon;
				info.iconResource = iconResource;
				// sunyinwei add for default system shortcut start 20131031
				Bitmap bg = null;
				bg = Icon3D.getIconBg();
				if( intent.getComponent().getPackageName() != null && intent.getComponent().getClassName() != null )
				{
					if( DefaultLayout.getInstance().hasReplaceShortcutIcon( intent.getComponent().getPackageName() , intent.getComponent().getClassName() ) )
					{
						bg = null;
					}
				}
				if( icon == null )
				{
					icon = info.getIcon( iLoongApplication.mIconCache );
					info.setIcon( icon );
				}
				// sunyinwei add for default system shortcut end 20131031
				if( icon != null )
				{
					R3D.pack( info , shortcut.name , Utils3D.IconToPixmap3D( icon , info.title.toString() , bg , Icon3D.titleBg , false ) , false );
				}
				else
					R3D.pack( info , shortcut.name );
				info.x = Utils3D.getScreenWidth() / 2;
				info.y = Utils3D.getScreenHeight() / 2;
				info.screen = shortcut.screen;
				info.cellX = shortcut.cellX;
				info.cellY = shortcut.cellY;
				if( iLoongLauncher.getInstance() != null )
				{
					// iLoongLauncher.getInstance().getD3dListener().addShortcutFromDrop(info);
					// LauncherModel
					// .addSysShortcutInDatabase(context, info);
					Root3D.addOrMoveDB( info );
				}
				// LauncherModel.addSysShortcutInDatabase(context, info);
				// final AppWidgetManager appWidgetManager = AppWidgetManager
				// .getInstance(context);
				//
				// try {
				// int appWidgetId = iLoongLauncher.getInstance().mAppWidgetHost
				// .allocateAppWidgetId();
				// Widget2DInfo appWidgetInfo = new Widget2DInfo(appWidgetId);
				// appWidgetInfo.cellX = widget.cellX;
				// appWidgetInfo.cellY = widget.cellY;
				// appWidgetInfo.spanX = widget.spanX;
				// appWidgetInfo.spanY = widget.spanY;
				// appWidgetInfo.screen = widget.screen;
				// LauncherModel
				// .addSysShortcutInDatabase(context, item)InDatabase(context,
				// appWidgetInfo);
				// appWidgetManager.bindAppWidgetId(appWidgetId, cn);
				// } catch (RuntimeException ex) {
				// Log.e("launcher", "Problem allocating appWidgetId", ex);
				// }
			}
			else
				continue;
		}
	}
	
	public void insertCooeePlugin()
	{
		Context context = iLoongLauncher.getInstance();
		PackageManager packageManager = context.getPackageManager();
		for( int i = 0 ; i < cooeePlugins.size() ; i++ )
		{
			CooeePlugin plugin = cooeePlugins.get( i );
			if( plugin.pkgName == null )
			{
				continue;
			}
			boolean hasPackage = true;
			try
			{
				packageManager.getApplicationInfo( plugin.pkgName , 0 );
			}
			catch( Exception e )
			{
				hasPackage = false;
			}
			if( hasPackage )
			{
				final CooeePluginInfo info = new CooeePluginInfo();
				info.title = plugin.name;
				info.setInfo( plugin.pkgName , null );
				info.fullScreen = plugin.fullScreen;
				info.screen = plugin.screen;
				if( iLoongLauncher.getInstance() != null )
				{
					Root3D.addOrMoveDB( info );
				}
			}
		}
	}
	
	void InsertDefaultShortcut()
	{
		Log.v( "test" , "allShortcutList size = " + allShortcutList.size() );
		for( int i = 0 ; i < allShortcutList.size() ; i++ )
		{
			InsertShortCut( allShortcutList.get( i ) );
		}
	}
	
	public static boolean DefaultWidgetProcess(
			ItemInfo info )
	{
		boolean res = false;
		ShortcutInfo shrtInfo = (ShortcutInfo)info;
		String imagePath = GetDefaultImageWithPkgName( shrtInfo.intent.getComponent().getPackageName() );
		if( imagePath == null )
			return res;
		WidgetView widgetView = new WidgetView( (String)info.title , new BitmapTexture( BitmapFactory.decodeStream( Gdx.files.internal( imagePath ).read() ) , true ) );
		widgetView.setItemInfo( shrtInfo );
		aliveDefWidget.add( widgetView );
		res = true;
		return res;
	}
	
	public static ArrayList<WidgetIcon> virtureView = new ArrayList<WidgetIcon>();
	public static ArrayList<WidgetView> widView = new ArrayList<WidgetView>();
	public static boolean include_theme_box = true;
	
	public static boolean needAddWidget(
			String pkgName )
	{
		WidgetView wdgView;
		ShortcutInfo info;
		Log.d( "launcher" , "needAddWidget" );
		for( int i = 0 ; i < widView.size() ; i++ )
		{
			wdgView = widView.get( i );
			info = (ShortcutInfo)wdgView.getItemInfo();
			if( info.intent.getComponent().getPackageName().equals( "iLoongMemo.apk" ) )
				continue;
			if( info.intent.getComponent().getPackageName().equals( pkgName ) )
				return true;
		}
		return false;
	}
	
	public static void addWidgetView(
			Widget3D wdg ,
			String pkgName )
	{
		WidgetView wdgView;
		ShortcutInfo info;
		Log.d( "launcher" , "addWidgetView" );
		for( int i = 0 ; i < widView.size() ; i++ )
		{
			wdgView = widView.get( i );
			info = (ShortcutInfo)wdgView.getItemInfo();
			if( info.intent.getComponent().getPackageName().equals( pkgName ) )
			{
				Widget3DInfo wdgInfo = wdg.getItemInfo();
				wdgInfo.x = (int)wdgView.x - ( (int)wdg.width - (int)wdgView.width ) / 2;
				wdgInfo.y = (int)wdgView.y;
				wdgInfo.screen = info.screen;
				wdgInfo.cellX = info.cellX;
				wdgInfo.cellY = info.cellY;
				Root3D.deleteFromDB( widView.get( i ).getItemInfo() );
				widView.get( i ).remove();
				widView.remove( i );
				// Root3D.addOrMoveDB(wdgInfo);
				workspace.addInScreen( wdg , wdgInfo.screen , wdgInfo.x , wdgInfo.y , false );
			}
		}
	}
	
	public static void removeDefWidgetView(
			String pkgName )
	{
		Log.d( "launcher" , "removeDefWidgetView" );
		WidgetView wdgView;
		ShortcutInfo info;
		for( int i = 0 ; i < widView.size() ; i++ )
		{
			wdgView = widView.get( i );
			info = (ShortcutInfo)wdgView.getItemInfo();
			if( info.intent.getComponent().getPackageName().equals( pkgName ) )
			{
				Root3D.deleteFromDB( widView.get( i ).getItemInfo() );
				widView.get( i ).remove();
				widView.remove( i );
			}
		}
	}
	
	public static void onDropToTrash(
			View3D view )
	{
		boolean haveView = false;
		int i = 0;
		if( ( view instanceof IconBase3D ) == false )
		{
			return;
		}
		ItemInfo info = ( (IconBase3D)view ).getItemInfo();
		if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW )
		{
			for( ; i < widView.size() ; i++ )
			{
				if( widView.get( i ) == view )
				{
					haveView = true;
					break;
				}
			}
			if( haveView )
			{
				widView.get( i ).remove();
				widView.remove( i );
			}
		}
		else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
		{
			for( ; i < virtureView.size() ; i++ )
			{
				if( virtureView.get( i ) == view )
				{
					haveView = true;
					break;
				}
			}
			if( haveView )
			{
				virtureView.get( i ).remove();
				virtureView.remove( i );
			}
		}
	}
	
	public static WidgetItem getWidgetItem(
			String packageName )
	{
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			if( allWidget.get( i ).pkgName.equals( packageName ) )
			{
				return allWidget.get( i );
			}
		}
		return null;
	}
	
	public static Widget3D mClockInstance = null;
	public static ActionHolder mActionHolder;
	
	/*
	 * need set name
	 */
	public static View3D showDefaultWidgetView(
			ShortcutInfo info )
	{
		String imagePath;
		if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW )
		{
			WidgetItem widgetItem = getWidgetItem( info.intent.getComponent().getPackageName() );
			if( widgetItem == null )
				return null;
			if( widgetItem.loadByInternal )
			{
				try
				{
					Widget3D widget3D = Widget3DManager.getInstance().getWidget3D( widgetItem.pkgName , widgetItem.className );
					if( widget3D != null )
					{
						Widget3DInfo wdgInfo = widget3D.getItemInfo();
						wdgInfo.intent = info.intent;
						wdgInfo.x = info.x;
						wdgInfo.y = info.y;
						wdgInfo.screen = info.screen;
						wdgInfo.cellX = info.cellX;
						wdgInfo.cellY = info.cellY;
						wdgInfo.spanX = GetDefaultWidgetHSpan( info.intent.getComponent().getPackageName() );
						wdgInfo.spanY = GetDefaultWidgetVSpan( info.intent.getComponent().getPackageName() );
						workspace.addInScreen( widget3D , wdgInfo.screen , wdgInfo.x , wdgInfo.y , false );
						Root3D.deleteFromDB( info );
						if( DefaultLayout.WorkspaceActionGuide )
						{
							if( widgetItem.pkgName.equalsIgnoreCase( "com.iLoong.WeatherClock" ) )
							{
								mClockInstance = widget3D;
								mActionHolder = new ActionHolder( "mActionHolder" );
								mActionHolder.setClockIns( mClockInstance );
								mActionHolder.setClockInfo( wdgInfo );
								root.addView( mActionHolder );
								//workspace.getCurrentCellLayout().setBackgroud( bg );
								//mActionHolder.hide();
							}
						}
					}
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
			}
			else
			{
				if( hasInstallWidget( info.intent.getComponent().getPackageName() ) )
				{
					ResolveInfo resolveInfo = getInstallWidgetResolveInfo( info.intent.getComponent().getPackageName() );
					if( resolveInfo == null )
						return null;
					Root3D.deleteFromDB( info );
					Widget3D wdg = Widget3DManager.getInstance().getWidget3D( resolveInfo );
					Widget3DInfo wdgInfo = wdg.getItemInfo();
					wdgInfo.x = info.x;
					wdgInfo.y = info.y;
					wdgInfo.screen = info.screen;
					wdgInfo.cellX = info.cellX;
					wdgInfo.cellY = info.cellY;
					wdgInfo.spanX = GetDefaultWidgetHSpan( info.intent.getComponent().getPackageName() );
					wdgInfo.spanY = GetDefaultWidgetVSpan( info.intent.getComponent().getPackageName() );
					workspace.addInScreen( wdg , wdgInfo.screen , wdgInfo.x , wdgInfo.y , false );
				}
				else
				{
					WidgetView widgetView;
					imagePath = GetDefaultImageWithPkgName( info.intent.getComponent().getPackageName() );
					if( imagePath == null )
						return null;
					Bitmap bmp = ThemeManager.getInstance().getBitmap( imagePath );
					if( bmp == null )
						return null;
					widgetView = new WidgetView( (String)info.title , new BitmapTexture( bmp ) );
					bmp.recycle();
					bmp = null;
					if( info.intent.getComponent().getPackageName().equals( "com.iLoong.Clock" ) )
						widgetView.setSize( widgetView.width * Utils3D.getDensity() , widgetView.height * Utils3D.getDensity() );
					else if( info.intent.getComponent().getPackageName().equals( "com.iLoong.Calendar" ) )
					{
						float widthfloat = Utils3D.getScreenWidth() / 720f;
						float heightfloat = R3D.Workspace_cell_each_height * 4 / 960f;
						float nowfloat = Math.min( widthfloat , heightfloat );
						widgetView.setSize( widgetView.width * nowfloat , widgetView.height * nowfloat );
					}
					else
						widgetView.setSize( widgetView.width * Utils3D.getDensity() / 1.5f , widgetView.height * Utils3D.getDensity() / 1.5f );
					Log.d( "launcher" , "old span:" + info.intent.getComponent().getPackageName() + "," + info.spanX + "," + info.spanY );
					info.spanX = GetDefaultWidgetHSpan( info.intent.getComponent().getPackageName() );
					info.spanY = GetDefaultWidgetVSpan( info.intent.getComponent().getPackageName() );
					Log.d( "launcher" , "new span:" + info.intent.getComponent().getPackageName() + "," + info.spanX + "," + info.spanY );
					widgetView.setItemInfo( info );
					widView.add( widgetView );
					workspace.addInScreen( widgetView , info.screen , info.x , info.y , false );
					if( hasInstallWidget( info.intent.getComponent().getPackageName() ) )
					{
						ResolveInfo resolveInfo = getInstallWidgetResolveInfo( info.intent.getComponent().getPackageName() );
						if( resolveInfo != null )
						{
							Widget3DManager.getInstance().installWidget( resolveInfo );
						}
					}
				}
			}
		}
		else
		{
			WidgetIcon widgetIcon = null;
			if( info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
			{
				widgetIcon = WidgetIcon.createWidgetIcon( info , true );
				if( widgetIcon != null )
				{
					virtureView.add( widgetIcon );
					workspace.addInScreen( widgetIcon , info.screen , info.x , info.y , false );
				}
				return widgetIcon;
			}
			else if( info.container == LauncherSettings.Favorites.CONTAINER_SIDEBAR || info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				widgetIcon = WidgetIcon.createWidgetIcon( info , false );
				if( widgetIcon != null )
				{
					virtureView.add( widgetIcon );
				}
				return widgetIcon;
			}
			else
			{
				widgetIcon = WidgetIcon.createWidgetIcon( info , true );
				if( widgetIcon != null )
				{
					virtureView.add( widgetIcon );
				}
				return widgetIcon;
			}
		}
		return null;
	}
	
	public static void onAddApp(
			ApplicationInfo appInfo )
	{
		WidgetIcon wdgIcon;
		ShortcutInfo info;
		Icon3D icon;
		for( int i = 0 ; i < virtureView.size() ; i++ )
		{
			wdgIcon = virtureView.get( i );
			if( wdgIcon == null )
				return;
			info = (ShortcutInfo)wdgIcon.getItemInfo();
			if( info.intent.getComponent().getPackageName().equals( appInfo.intent.getComponent().getPackageName() ) )
			{
				ShortcutInfo temp = appInfo.makeShortcut();
				temp.x = info.x;
				temp.y = info.y;
				// temp.container = info.container;
				temp.screen = info.screen;
				icon = new Icon3D( temp.title.toString() , R3D.findRegion( temp ) );
				icon.setItemInfo( temp );
				if( info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
				{
					Root3D.deleteFromDB( info );
					workspace.removeViewInWorkspace( wdgIcon );
					workspace.addInScreen( icon , temp.screen , temp.x , temp.y , false );
				}
				else if( info.container == LauncherSettings.Favorites.CONTAINER_SIDEBAR || info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
				{
					final ViewGroup3D mainGroup = sideBar.getMainGroup();
					final ViewGroup3D dockGroup = sideBar.getDockGroup();
					Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
					if( info.angle == HotSeat3D.TYPE_ICON )
					{
						if( DefaultLayout.enable_hotseat_rolling )
						{
							temp.angle = HotSeat3D.TYPE_ICON;
							( (HotSeatMainGroup)mainGroup ).removeItem( wdgIcon );
							( (HotSeatMainGroup)mainGroup ).bindItem( icon );
						}
					}
					else
					{
						temp.angle = HotSeat3D.TYPE_WIDGET;
						( (HotDockGroup)dockGroup ).removeItem( wdgIcon );
						( (HotDockGroup)dockGroup ).bindItem( icon );
					}
					Root3D.addOrMoveDB( temp , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
					Root3D.deleteFromDB( info );
				}
				else
				{
					// UserFolderInfo fo =
					// iLoongApplication.getInstance().mModel.getFolderInfoWithContainerID(info.container);
					// fo.remove(info);
					// fo.add(temp);
					// Root3D.addOrMoveDB(temp, fo.id);
					// Root3D.addOrMoveDB(info, fo.id);
					FolderIcon3D folderIcon = (FolderIcon3D)wdgIcon.getTag2();
					folderIcon.updateIcon( wdgIcon , icon );
				}
				wdgIcon.remove();
				// virtureView.remove(i);
			}
		}
	}
	
	public static boolean isAVirtureIcon(
			String pkgName )
	{
		boolean res = false;
		ShortcutInfo info = null;
		if( virtureView == null || pkgName == null )
			return res;
		for( int i = 0 ; i < virtureView.size() ; i++ )
		{
			info = (ShortcutInfo)virtureView.get( i ).info;
			if( pkgName.equals( info.intent.getComponent().getPackageName() ) )
			{
				return true;
			}
		}
		return res;
	}
	
	void InsertDefaultWidget()
	{
		int width = Utils3D.getScreenWidth();
		int height = Utils3D.getScreenHeight();
		for( int i = 0 ; i < allWidget.size() ; i++ )
		{
			WidgetItem widget;
			widget = allWidget.get( i );
			if( !widget.addDesktop )
				continue;
			ShortcutInfo info = new ShortcutInfo();
			info.title = widget.name;
			info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
			info.intent.setComponent( new ComponentName( widget.pkgName , widget.pkgName ) );
			info.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
			info.minWidth = widget.minWidth;
			info.minHeight = widget.minHeight;
			info.spanX = widget.spanX;
			info.spanY = widget.spanY;
			info.cellX = widget.cellX;
			info.cellY = widget.cellY;
			// String imagePath = THEME_WIDGET_FOLDER + widget.imageName;
			// widgetView = new WidgetView(widget.name, R3D.defWidget);
			// widgetView.setItemInfo(info);
			//
			// widgetView.setSize(R3D.defWidget.getWidth() *
			// Utils3D.getDensity(), R3D.defWidget.getHeight() *
			// Utils3D.getDensity());
			//
			// widView.add(widgetView);
			//
			// info.x = (int) ((width - R3D.clockTexture.getWidth() *
			// Utils3D.getDensity()) / 2);
			// info.y = (int) (height - R3D.clockTexture.getHeight() *
			// Utils3D.getDensity() - 40);
			info.screen = widget.value;
			// workspace.addInScreen(widgetView, info.screen, info.x, info.y,
			// false);
			Log.d( "launcher" , "insert:" + info.title );
			Root3D.addOrMoveDB( info );
		}
	}
	
	void insertSmartLayout()
	{
		SLConfigHandler SLConfig = new SLConfigHandler();
		List<ResolveInfo> apps = SLConfig.getApplicationInfo();
		LinkedList<SLLayout> layout = SLConfig.getLayout();
		if( apps.size() < layout.size() )
		{
			int num = layout.size() - apps.size();
			getDefaultIcon( apps , num );
		}
		for( int i = 0 ; i < layout.size() ; i++ )
		{
			if( i < apps.size() )
			{
				ApplicationInfo appInfo = new ApplicationInfo( apps.get( i ) , iLoongApplication.mIconCache );
				ShortcutInfo info = new ShortcutInfo( appInfo );
				info.screen = layout.get( i ).getScreen();
				info.spanX = 1;
				info.spanY = 1;
				info.cellX = layout.get( i ).getCellX();
				info.cellY = layout.get( i ).getCellY();
				Root3D.addOrMoveDB( info , LauncherSettings.Favorites.CONTAINER_DESKTOP );
			}
		}
		SLConfig.clear();
	}
	
	public List<ResolveInfo> getDefaultIcon(
			List<ResolveInfo> apps ,
			int num )
	{
		num += 4;
		if( apps == null )
			return null;
		List<ResolveInfo> newList = new LinkedList<ResolveInfo>();
		final Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
		mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
		final PackageManager packageManager = iLoongLauncher.getInstance().getPackageManager();
		List<ResolveInfo> resInfos = packageManager.queryIntentActivities( mainIntent , 0 );
		int index = 0;
		if( resInfos != null )
		{
			for( ResolveInfo info : resInfos )
			{
				ArrayList<String> allNamePatten;
				String packageName = info.activityInfo.applicationInfo.packageName;
				DefaultIcon tem = new DefaultIcon();
				for( int inter = 0 ; inter < defaultIcon.size() && index < num ; inter++ )
				{
					int ih = 0;
					for( ; ih < iLoongLauncher.getInstance().mHotseatIconsName.length ; ih++ )
					{
						String imgName = iLoongLauncher.getInstance().mHotseatIconsName[ih];
						int imgIndex = imgName.lastIndexOf( "/" );
						String image = imgName.substring( imgIndex + 1 );
						if( defaultIcon.get( inter ).imageName.equalsIgnoreCase( image ) )
						{
							break;
						}
					}
					if( ih < iLoongLauncher.getInstance().mHotseatIconsName.length )
					{
						Log.v( "SMARTLAYOUT" , "SET NEW INSTANCE :" + info.activityInfo.packageName );
						continue;
					}
					allNamePatten = defaultIcon.get( inter ).pkgNameArray;
					if( allNamePatten == null || allNamePatten.size() <= 0 )
						continue;
					for( int k = 0 ; k < allNamePatten.size() ; k++ )
					{
						if( packageName.equals( allNamePatten.get( k ) ) )
						{
							if( defaultIcon.get( inter ).duplicate )
							{
								ArrayList<String> allCompNames = defaultIcon.get( inter ).classNameArray;
								int m = 0;
								for( ; m < allCompNames.size() ; m++ )
								{
									if( info.activityInfo.name.equals( allCompNames.get( m ) ) )
									{
										int l = 0;
										for( l = 0 ; l < apps.size() ; l++ )
										{
											if( apps.get( l ).activityInfo.applicationInfo.packageName.equalsIgnoreCase( packageName ) && apps.get( l ).activityInfo.applicationInfo.className
													.equalsIgnoreCase( info.activityInfo.name ) )
											{
												break;
											}
										}
										if( l >= apps.size() )
										{
											index++;
											newList.add( info );
											break;
										}
									}
								}
								if( m < allCompNames.size() )
								{
									break;
								}
							}
							else
							{
								int l = 0;
								for( l = 0 ; l < apps.size() ; l++ )
								{
									if( apps.get( l ).activityInfo.applicationInfo.packageName.equalsIgnoreCase( packageName ) )
									{
										break;
									}
								}
								if( l >= apps.size() )
								{
									index++;
									newList.add( info );
									break;
								}
							}
						}
					}
				}
			}
		}
		/*if(iLoongLauncher.getInstance().mHotseats!=null&&iLoongLauncher.getInstance().mHotseats.length>0){
			int lenght=iLoongLauncher.getInstance().mHotseats.length;
			int l=0;
			Iterator<ResolveInfo> ite=newList.iterator();
			while(ite.hasNext()&&l<lenght){
				ResolveInfo info=ite.next();
				Intent intent=iLoongLauncher.getInstance().mHotseats[l];

				Log.v( "SMARTLAYOUT" , "SET NEW INSTANCE :"+info.activityInfo.packageName );
				if(info.activityInfo.packageName.equalsIgnoreCase( intent.getComponent().getPackageName() ))
				{
					l++;
					ite.remove();
				}
			}
			
		}*/
		//		
		apps.addAll( newList );
		return apps;
	}
	
	public void getHotseatNameString(
			ArrayList<String> stringArray ,
			String allName )
	{
		if( allName == null )
			return;
		int length = allName.length();
		int start = 0;
		int end = 0;
		String part = allName;
		String leftName = allName;
		int limit = '/'; /* delimiter */
		stringArray.clear();
		if( length <= 0 )
			return;
		while( leftName != null )
		{
			end = leftName.indexOf( limit );
			if( end == -1 )
			{
				part = leftName;
			}
			else
				part = leftName.substring( start , end );
			// Log.v("test", "part string = <<" + part + ">>");
			if( end == -1 )
				leftName = null;
			else
				leftName = leftName.substring( end + 1 );
			if( part != null )
				stringArray.add( part );
		}
	}
	
	void InsertHotSeatIcon()
	{
		ShortcutItem item;
		Icon3D icon;
		for( int i = 0 ; i < iLoongLauncher.getInstance().getHotSeatLength() ; i++ )
		{
			// item = allHotSeatList.get(i);
			icon = new Icon3D( "test" );
			ShortcutInfo info = new ShortcutInfo();
			// item.info = info;
			info.title = iLoongLauncher.getInstance().getHotSeatString( i );
			if( info.title == null )
			{
				// info.title=item.name;
			}
			Intent findIntent = null;
			findIntent = iLoongLauncher.getInstance().getHotSeatIntent( i );
			info.angle = HotSeat3D.TYPE_WIDGET;
			// info.setIcon(GetBmpFromImageName(item.imageName));
			info.screen = i;
			if( findIntent != null )
			{
				info.intent = findIntent;
				icon.setItemInfo( info );
				// list.add((View3D) (icon));
				Root3D.addOrMoveDB( info , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
			}
		}
	}
	
	public static String getAppListVirtualIconName(
			int index )
	{
		return allAppListVirtualIcon.get( index ).name;
	}
	
	public static String getAppListVirtualIconTitle(
			int index )
	{
		return allAppListVirtualIcon.get( index ).title;
	}
	
	public static String getAppListVirtualIconPkgName(
			int index )
	{
		return allAppListVirtualIcon.get( index ).packageName;
	}
	
	public static String getAppListVirtualIconImageName(
			int index )
	{
		return allAppListVirtualIcon.get( index ).imageName;
	}
	
	void InsertVirtureIcon()
	{
		VirtureIcon virIcon;
		ShortcutInfo info;
		UserFolderInfo fo;
		Log.v( "test" , "allVirture size = " + allVirture.size() );
		HashMap<UserFolderInfo , Integer> folderIconNum = new HashMap<UserFolderInfo , Integer>();
		for( int i = 0 ; i < allVirture.size() ; i++ )
		{
			virIcon = allVirture.get( i );
			if( virIcon.folder != null )
				fo = allFolder.get( virIcon.folder );
			else
				fo = null;
			if( fo != null )
			{
				if( !folderIconNum.containsKey( fo ) )
					folderIconNum.put( fo , 0 );
				if( folderIconNum.get( fo ) >= R3D.folder_max_num )
					continue;
				else
					folderIconNum.put( fo , folderIconNum.get( fo ) + 1 );
			}
			if( virIcon.info == null )
			{
				info = new ShortcutInfo();
				info.x = virIcon.x;// (Utils3D.getScreenWidth() -
									// R3D.sidebar_widget_w) / 2;
				info.y = virIcon.y;// /R3D.def_layout_y + R3D.def_layout_y_dura;
				info.title = virIcon.name;
				info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
				if( virIcon.className != null )
				{
					info.intent.setComponent( new ComponentName( virIcon.pkgName , virIcon.className ) );
				}
				else
				{
					info.intent.setComponent( new ComponentName( virIcon.pkgName , virIcon.pkgName ) );
				}
				info.itemType = LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW;
				info.spanX = 1;
				info.spanY = 1;
				info.cellX = virIcon.cellX;
				info.cellY = virIcon.cellY;
				if( fo == null )
				{
					info.screen = virIcon.value;
					Root3D.addOrMoveDB( info );
				}
				else
					Root3D.addOrMoveDB( info , fo.id );
			}
			else
			{
				ItemInfo itemInfo = virIcon.info;
				itemInfo.x = virIcon.x;
				itemInfo.y = virIcon.y;
				itemInfo.spanX = 1;
				itemInfo.spanY = 1;
				itemInfo.cellX = virIcon.cellX;
				itemInfo.cellY = virIcon.cellY;
				if( fo == null )
				{
					itemInfo.screen = virIcon.value;
					Root3D.addOrMoveDB( itemInfo );
				}
				else
					Root3D.addOrMoveDB( itemInfo , fo.id );
			}
		}
	}
	
	public boolean isImageExist(
			String imageName )
	{
		String imagePath;
		if( imageName == null )
			return false;
		imagePath = THEME_ICON_FOLDER + THEME_NAME + imageName;
		return ThemeManager.getInstance().isFileExistIgnoreSystem( imagePath );
	}
	
	public Bitmap GetPKGThemeIconPath(
			String imageName )
	{
		Bitmap image = null;
		String imagePath;
		if( imageName == null )
			return null;
		imagePath = THEME_ICON_FOLDER + THEME_NAME + imageName;
		// image = Tools.getImageFromInStream(DefaultLayout.class
		// .getResourceAsStream(imagePath));
		image = ThemeManager.getInstance().getBitmap( imagePath );
		return image;
	}
	
	public Bitmap GetShortcutThemeIconPath(
			String imageName )
	{
		Bitmap image = null;
		String imagePath;
		if( imageName == null )
			return null;
		imagePath = THEME_SYS_SHORTCUT_FOLDER + imageName;
		// image = Tools.getImageFromInStream(DefaultLayout.class
		// .getResourceAsStream(imagePath));
		image = ThemeManager.getInstance().getBitmapIgnoreSystemTheme( imagePath );
		return image;
	}
	
	public Bitmap getSysShortcutThemeIconPath(
			String imageName )
	{
		Bitmap image = null;
		String imagePath;
		if( imageName == null )
			return null;
		imagePath = THEME_SYS_SHORTCUT_FOLDER + imageName;
		// image = Tools.getImageFromInStream(DefaultLayout.class
		// .getResourceAsStream(imagePath));
		image = ThemeManager.getInstance().getBitmap( imagePath );
		return image;
	}
	
	public void onThemeChanged()
	{
		// 加载主题个性化配置
		InputStream is = ThemeManager.getInstance().getCurrentThemeInputStream( DEFAULT_LAYOUT_FILENAME );
		if( is != null )
		{
			try
			{
				SAXParserFactory factoey = SAXParserFactory.newInstance();
				SAXParser parser = factoey.newSAXParser();
				XMLReader xmlreader = parser.getXMLReader();
				DefaultLayoutHandler handler = new DefaultLayoutHandler();
				InputSource xmlin;
				defaultIcon.clear();
				if( DefaultLayout.dynamic_icon )
				{
					dynamicIcon.clear();
					dynamiciconlist.clear();
				}
				handler.parseThemeConfig = true;
				xmlin = new InputSource( is );
				parser = factoey.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler( handler );
				xmlreader.parse( xmlin );
				is.close();
				handler.parseThemeConfig = false;
				handler = null;
				xmlin = null;
			}
			catch( ParserConfigurationException e )
			{
				e.printStackTrace();
			}
			catch( SAXException e )
			{
				e.printStackTrace();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		int size = defaultIcon.size();
		for( int i = 0 ; i < size ; i++ )
		{
			defaultIcon.get( i ).dealed = false;
		}
		setDefaultIcon();
		setDefaultIconSize();
		if( DefaultLayout.dynamic_icon )
		{
			DefaultLayout.getInstance().getDynamicInfo();
			DefaultLayout.getInstance().setDynamicIcon();
		}
		Utilities.initStatics( iLoongLauncher.getInstance() );
	}
	
	final private void LoadDefaultLayoutXml()
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			DefaultLayoutHandler handler = new DefaultLayoutHandler();
			handler.parseThemeConfig = false;
			xmlreader.setContentHandler( handler );
			InputSource xmlin;
			if( isCustomXmlExist() )
			{
				InputStream inputStream = iLoongApplication.getInstance().openFileInput( CUSTOM_XML_FILE_NAME );
				if( inputStream != null )
				{
					needToSaveSpecifiedIconXml = false;
					xmlin = new InputSource( inputStream );
					parser = factoey.newSAXParser();
					xmlreader = parser.getXMLReader();
					xmlreader.setContentHandler( handler );
					xmlreader.parse( xmlin );
					specifiedIconsLoaded = true;
					inputStream.close();
				}
				else
				{
					needToSaveSpecifiedIconXml = true;
				}
			}
			if( !RR.net_version )
			{
				File f = new File( CUSTOM_FIRST_DEFAULT_LAYOUT_FILENAME );
				if( !f.exists() )
				{
					f = new File( CUSTOM_DEFAULT_LAYOUT_FILENAME );
					if( !f.exists() )
					{
						f = new File( CUSTOM_FIRST_DEFAULT_LAYOUT_FILENAME_PUBLIC );
						if( !f.exists() )
						{
							f = new File( CUSTOM_DEFAULT_LAYOUT_FILENAME_PUBLIC );
						}
					}
				}
				if( iLoongApplication.BuiltIn && f.exists() )
				{
					xmlin = new InputSource( new FileInputStream( f.getAbsolutePath() ) );
				}
				else
				{
					xmlin = new InputSource( iLoongApplication.getInstance().getAssets().open( DEFAULT_LAYOUT_FILENAME ) );
				}
			}
			else
			{
				xmlin = new InputSource( iLoongApplication.getInstance().getAssets().open( DEFAULT_LAYOUT_FILENAME ) );
			}
			if( DefaultLayout.dynamic_icon )
			{
				dynamicIcon.clear();
				dynamiciconlist.clear();
			}
			xmlreader.parse( xmlin );
			DrawDynamicIcon.setDynamicIconMode();
			boolean parseTheme = true;
			// 加载服务器default_layout
			if( enable_air_default_layout )
			{
				try
				{
					File file = new File( Environment.getExternalStorageDirectory() + AirDefaultLayout.FILE );
					InputStream is = new FileInputStream( file );
					if( is != null )
					{
						handler.parseThemeConfig = true;
						xmlin = new InputSource( is );
						if( DefaultLayout.dynamic_icon )
						{
							dynamicIcon.clear();
							dynamiciconlist.clear();
						}
						parser = factoey.newSAXParser();
						xmlreader = parser.getXMLReader();
						xmlreader.setContentHandler( handler );
						xmlreader.parse( xmlin );
						handler.parseThemeConfig = false;
						parseTheme = false;
						is.close();
					}
				}
				catch( Exception e )
				{
				}
			}
			handler = null;
			xmlin = null;
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		// xujin 4.0以下关闭新闻
		if( Integer.parseInt( VERSION.SDK ) < 14 )
		{
			DefaultLayout.enable_camera = false;
			DefaultLayout.show_camera_page_enable_config = false;
			DefaultLayout.show_camera_page_config_default_value = false;
		}
		// 硬件加速
		enable_hardware_accel = enable_camera | enable_news;
	}
	
	private void LoadDefaultLayoutXmlFromThemes()
	{
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try
		{
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			DefaultLayoutHandler handler = new DefaultLayoutHandler();
			handler.parseThemeConfig = false;
			xmlreader.setContentHandler( handler );
			InputSource xmlin;
			boolean parseTheme = true;
			// 加载主题个性化配置
			if( parseTheme && !ThemeManager.getInstance().currentThemeIsSystemTheme() )
			{
				InputStream is = ThemeManager.getInstance().getCurrentThemeInputStream( DEFAULT_LAYOUT_FILENAME );
				if( is != null )
				{
					handler.parseThemeConfig = true;
					xmlin = new InputSource( is );
					if( DefaultLayout.dynamic_icon )
					{
						dynamicIcon.clear();
						dynamiciconlist.clear();
					}
					parser = factoey.newSAXParser();
					xmlreader = parser.getXMLReader();
					xmlreader.setContentHandler( handler );
					xmlreader.parse( xmlin );
					handler.parseThemeConfig = false;
					is.close();
				}
			}
			handler = null;
			xmlin = null;
		}
		catch( ParserConfigurationException e )
		{
			e.printStackTrace();
		}
		catch( SAXException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	// xiatian add start //Mainmenu Bg
	private void initMainmenuBgListPreference()
	{
		String mainmenu_bg_key = iLoongLauncher.getInstance().getResources().getString( RR.string.mainmenu_bg_key );
		String mainmenu_bg_value = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( mainmenu_bg_key , "-1" );
		if( mainmenu_bg_value.equals( "-1" ) )
		{
			PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putString( mainmenu_bg_key , defaultMainmenuBgIndex + "" ).commit();
			lastAppListMainmenuBgIndex = defaultMainmenuBgIndex;
			lastMediaListMainmenuBgIndex = defaultMainmenuBgIndex;
		}
		else
		{
			PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putString( mainmenu_bg_key , mainmenu_bg_value ).commit();
			lastAppListMainmenuBgIndex = Integer.valueOf( mainmenu_bg_value ).intValue();
			lastMediaListMainmenuBgIndex = Integer.valueOf( mainmenu_bg_value ).intValue();
		}
	}
	
	// xiatian add end
	public boolean isCustomAssetsFileExist(
			String fileName )
	{
		if( !DefaultLayout.useCustomAssets )
			return false;
		String pathName = DefaultLayout.custom_assets_path + fileName;
		File file = new File( pathName );
		if( file.exists() )
			return true;
		return false;
	}
	
	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public boolean isPhoneSupportSensor()
	{
		if( DefaultLayout.show_sensor )
		{
			if( DefaultLayout.is_supported_sensor != -1 )
			{
				return( DefaultLayout.is_supported_sensor > 0 ? true : false );
			}
			SensorManager sensorMgr = (SensorManager)iLoongLauncher.getInstance().getSystemService( Context.SENSOR_SERVICE );
			Sensor sensor = sensorMgr.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
			if( sensor == null )
			{
				DefaultLayout.is_supported_sensor = 0;
				return false;
			}
			else
			{
				DefaultLayout.is_supported_sensor = 1;
				return true;
			}
		}
		else
		{
			return false;
		}
	}
	
	public boolean isOpenSensor()
	{
		if( DefaultLayout.show_sensor )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
			boolean cbx_sensor = prefs.getBoolean( iLoongLauncher.getInstance().getResources().getString( RR.string.setting_key_sensor ) , DefaultLayout.default_open_sensor );
			return cbx_sensor;
		}
		else
		{
			return false;
		}
	}
	
	// xiatian add end
	public static int getLayoutAttrValue(
			int key )
	{
		if( !( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout ) )
		{
			Toast.makeText( iLoongLauncher.getInstance() , "getLayoutAttrValue error!!" , Toast.LENGTH_SHORT ).show();
			return -1;
		}
		int icon_size_type = getDefaultIconSizeType();
		switch( icon_size_type )
		{
			case 0:
				return bigIconMap.get( "" + key );
			case 1:
				return normalIconMap.get( "" + key );
			case 2:
				return smallIconMap.get( "" + key );
			default:
				Toast.makeText( iLoongLauncher.getInstance() , "getLayoutAttrValue error!!" , Toast.LENGTH_SHORT ).show();
				return -1;
		}
	}
	
	public static int getLayoutAttrValueByType(
			int type ,
			int key )
	{
		if( !( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout ) )
		{
			Toast.makeText( iLoongLauncher.getInstance() , "getLayoutAttrValueByType error!!" , Toast.LENGTH_SHORT ).show();
			return -1;
		}
		switch( type )
		{
			case 0:
				return bigIconMap.get( "" + key );
			case 1:
				return normalIconMap.get( "" + key );
			case 2:
				return smallIconMap.get( "" + key );
			default:
				Toast.makeText( iLoongLauncher.getInstance() , "getLayoutAttrValueByType error!!" , Toast.LENGTH_SHORT ).show();
				return -1;
		}
	}
	
	public void saveHomePage(
			int homePage )
	{
		int icon_size_type = getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putInt( "home_page_big_icon" , homePage ).commit();
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putInt( "home_page_small_icon" , homePage ).commit();
		}
		else
		{
			PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putInt( "home_page" , homePage ).commit();
		}
	}
	
	public int loadHomePage()
	{
		int defaultHomePageIndex = DefaultLayout.default_home_page;
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			return PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getInt( "home_page_big_icon" , defaultHomePageIndex );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			return PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getInt( "home_page_small_icon" , defaultHomePageIndex );
		}
		else
		{
			return PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getInt( "home_page" , defaultHomePageIndex );
		}
	}
	
	// teapotXu add start
	public int loadCurrentScreenNum()
	{
		int currentPage = -1;
		if( DefaultLayout.enable_apply_saved_cur_screen_when_reboot )
		{
			boolean b_apply_currentScreenNum = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "apply_current_screen_num" , false );
			if( b_apply_currentScreenNum )
			{
				currentPage = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getInt( "current_screen_num" , loadHomePage() );
				PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putBoolean( "apply_current_screen_num" , false ).commit();
			}
		}
		return currentPage;
	}
	
	// teapotXu add end
	public static int[] findEmptyCellDataByType(
			int type )
	{
		int[] ret = new int[3];
		ret[0] = -1;
		ret[1] = -1;
		ret[2] = -1;
		final ContentResolver cr = iLoongApplication.getInstance().getContentResolver();
		Cursor c = null;
		int screenCount = ThemeManager.getInstance().getThemeDB().getScreenCountByType( type );
		if( screenCount == 0 )
			screenCount = DefaultLayout.default_workspace_pagecounts;
		int cellCountX = DefaultLayout.getLayoutAttrValueByType( type , DefaultLayout.LAYOUT_ATTR_WORKSPACE_COL );
		int cellCountY = DefaultLayout.getLayoutAttrValueByType( type , DefaultLayout.LAYOUT_ATTR_WORKSPACE_ROW );
		boolean has_item[][] = new boolean[cellCountY][cellCountX];
		for( int screen = 0 ; screen < screenCount ; screen++ )
		{
			for( int i = cellCountY - 1 ; i >= 0 ; i-- )
			{
				for( int j = 0 ; j < cellCountX ; j++ )
				{
					has_item[i][j] = false;
				}
			}
			if( type == 0 )
			{
				c = cr.query(
						LauncherSettings.Favorites.CONTENT_URI_BIG_ICON ,
						new String[]{
								LauncherSettings.Favorites.TITLE ,
								LauncherSettings.Favorites.CONTAINER ,
								LauncherSettings.Favorites.ITEM_TYPE ,
								LauncherSettings.Favorites.SCREEN ,
								LauncherSettings.Favorites.CELLX ,
								LauncherSettings.Favorites.CELLY } ,
						"container=? and screen=? and (itemType=? or itemType=? or itemType=? or itemType=?)" ,
						new String[]{
								String.valueOf( -100 ) ,
								String.valueOf( screen ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW ) } ,
						null );
			}
			else if( type == 2 )
			{
				c = cr.query(
						LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON ,
						new String[]{
								LauncherSettings.Favorites.TITLE ,
								LauncherSettings.Favorites.CONTAINER ,
								LauncherSettings.Favorites.ITEM_TYPE ,
								LauncherSettings.Favorites.SCREEN ,
								LauncherSettings.Favorites.CELLX ,
								LauncherSettings.Favorites.CELLY } ,
						"container=? and screen=? and (itemType=? or itemType=? or itemType=? or itemType=?)" ,
						new String[]{
								String.valueOf( -100 ) ,
								String.valueOf( screen ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW ) } ,
						null );
			}
			else
			{
				c = cr.query(
						LauncherSettings.Favorites.CONTENT_URI ,
						new String[]{
								LauncherSettings.Favorites.TITLE ,
								LauncherSettings.Favorites.CONTAINER ,
								LauncherSettings.Favorites.ITEM_TYPE ,
								LauncherSettings.Favorites.SCREEN ,
								LauncherSettings.Favorites.CELLX ,
								LauncherSettings.Favorites.CELLY } ,
						"container=? and screen=? and (itemType=? or itemType=? or itemType=? or itemType=?)" ,
						new String[]{
								String.valueOf( -100 ) ,
								String.valueOf( screen ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW ) } ,
						null );
			}
			if( c != null )
			{
				final int cellXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLX );
				final int cellYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLY );
				while( c.moveToNext() )
				{
					int cellX = c.getInt( cellXIndex );
					int cellY = c.getInt( cellYIndex );
					if( cellY < cellCountY && cellX < cellCountX )
					{
						has_item[cellY][cellX] = true;
					}
				}
			}
			// widget
			if( type == 0 )
			{
				c = cr.query(
						LauncherSettings.Favorites.CONTENT_URI_BIG_ICON ,
						new String[]{
								LauncherSettings.Favorites.TITLE ,
								LauncherSettings.Favorites.CONTAINER ,
								LauncherSettings.Favorites.ITEM_TYPE ,
								LauncherSettings.Favorites.SCREEN ,
								LauncherSettings.Favorites.CELLX ,
								LauncherSettings.Favorites.CELLY ,
								LauncherSettings.Favorites.SPANX ,
								LauncherSettings.Favorites.SPANY } ,
						"container=? and screen=? and (itemType>? and itemType<>?)" ,
						new String[]{
								String.valueOf( -100 ) ,
								String.valueOf( screen ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW ) } ,
						null );
			}
			else if( type == 2 )
			{
				c = cr.query(
						LauncherSettings.Favorites.CONTENT_URI_SMALL_ICON ,
						new String[]{
								LauncherSettings.Favorites.TITLE ,
								LauncherSettings.Favorites.CONTAINER ,
								LauncherSettings.Favorites.ITEM_TYPE ,
								LauncherSettings.Favorites.SCREEN ,
								LauncherSettings.Favorites.CELLX ,
								LauncherSettings.Favorites.CELLY ,
								LauncherSettings.Favorites.SPANX ,
								LauncherSettings.Favorites.SPANY } ,
						"container=? and screen=? and (itemType>? and itemType<>?)" ,
						new String[]{
								String.valueOf( -100 ) ,
								String.valueOf( screen ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW ) } ,
						null );
			}
			else
			{
				c = cr.query(
						LauncherSettings.Favorites.CONTENT_URI ,
						new String[]{
								LauncherSettings.Favorites.TITLE ,
								LauncherSettings.Favorites.CONTAINER ,
								LauncherSettings.Favorites.ITEM_TYPE ,
								LauncherSettings.Favorites.SCREEN ,
								LauncherSettings.Favorites.CELLX ,
								LauncherSettings.Favorites.CELLY ,
								LauncherSettings.Favorites.SPANX ,
								LauncherSettings.Favorites.SPANY } ,
						"container=? and screen=? and (itemType>? and itemType<>?)" ,
						new String[]{
								String.valueOf( -100 ) ,
								String.valueOf( screen ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER ) ,
								String.valueOf( LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW ) } ,
						null );
			}
			if( c != null )
			{
				final int cellXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLX );
				final int cellYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.CELLY );
				final int spanXIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SPANX );
				final int spanYIndex = c.getColumnIndexOrThrow( LauncherSettings.Favorites.SPANY );
				while( c.moveToNext() )
				{
					int cellX = c.getInt( cellXIndex );
					int cellY = c.getInt( cellYIndex );
					int spanX = c.getInt( spanXIndex );
					int spanY = c.getInt( spanYIndex );
					if( cellY < cellCountY && cellX < cellCountX && cellY + spanY <= cellCountY && cellX + spanX <= cellCountX )
					{
						for( int i = cellY ; i < cellY + spanY ; i++ )
						{
							for( int j = cellX ; j < cellX + spanX ; j++ )
							{
								has_item[i][j] = true;
							}
						}
					}
				}
			}
			for( int i = cellCountY - 1 ; i >= 0 ; i-- )
			{
				for( int j = 0 ; j < cellCountX ; j++ )
				{
					if( has_item[i][j] == false )
					{
						ret[0] = screen;
						ret[1] = j;
						ret[2] = i;
						return ret;
					}
				}
			}
		}
		// }
		return ret;
	}
	
	private void checkDefaultIconListHaveMatchIconInCurrentTheme()
	{
		String[] iconList = ThemeManager.getInstance().listCurrentThemeAssetFiles( THEME_ICON_80_FOLDER );
		for( int i = 0 ; i < defaultIcon.size() ; i++ )
		{
			String iconFullName = defaultIcon.get( i ).imageName;
			boolean isHaveMatchIcon = false;
			for( int j = 0 ; j < iconList.length ; j++ )
			{
				if( iconFullName.equals( iconList[j] ) )
				{
					isHaveMatchIcon = true;
					break;
				}
			}
			if( isHaveMatchIcon == false )
			{
				defaultIcon.remove( i );
				i--;
			}
		}
	}
	
	public static String getMediaSeatIconTitle(
			ArrayList<MediaSeatIcon> list ,
			int index )
	{
		if( list != null && list.size() > 0 )
		{
			return list.get( index ).title;
		}
		return null;
	}
	
	public static String getMediaSeatIconPkg(
			ArrayList<MediaSeatIcon> list ,
			int index )
	{
		if( list != null && list.size() > 0 )
		{
			return list.get( index ).pkgName;
		}
		return null;
	}
	
	public static String getMediaSeatIconCls(
			ArrayList<MediaSeatIcon> list ,
			int index )
	{
		if( list != null && list.size() > 0 )
		{
			return list.get( index ).className;
		}
		return null;
	}
	
	public static String getMediaSeatIconImg(
			ArrayList<MediaSeatIcon> list ,
			int index )
	{
		if( list != null && list.size() > 0 )
		{
			return list.get( index ).imageName;
		}
		return null;
	}
	
	// added by zhenNan.ye
	// the specifiedIcon.xml file is saved at
	// "data/data/com.coco.launcher/files/"
	public void saveSpecifiedIconToXml()
	{
		final String TAG_ICON = "icon";
		final String TAG_NAME = "name";
		final String TAG_DUPLICATE = "dup";
		final String TAG_PKGNAME = "pkgname";
		final String TAG_COMPONENTNAME = "componentName";
		final String TAG_IMAGE = "image";
		try
		{
			OutputStream os = iLoongLauncher.getInstance().openFileOutput( CUSTOM_XML_FILE_NAME , Activity.MODE_APPEND );
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput( os , "UTF-8" );
			serializer.startDocument( null , null );
			serializer.setFeature( "http://xmlpull.org/v1/doc/features.html#indent-output" , true );
			serializer.startTag( null , DefaultLayoutHandler.TAG_DEFAULT_LAYOUT );
			serializer.attribute( null , "ver" , "1.0" );
			serializer.startTag( null , DefaultLayoutHandler.GENERAl_CONFIG );
			serializer.attribute( null , "anti_aliasing" , "true" );
			serializer.endTag( null , DefaultLayoutHandler.GENERAl_CONFIG );
			//for( DefaultIcon icon : customDefaultIcon )
			for( int i = 0 ; i < customDefaultIcon.size() ; i++ )
			{
				DefaultIcon icon = customDefaultIcon.get( i );
				serializer.startTag( null , TAG_ICON );
				serializer.attribute( null , TAG_NAME , icon.title );
				if( icon.duplicate )
				{
					serializer.attribute( null , TAG_DUPLICATE , Boolean.toString( icon.duplicate ) );
				}
				serializer.attribute( null , TAG_PKGNAME , icon.pkgName );
				if( icon.className != null )
				{
					serializer.attribute( null , TAG_COMPONENTNAME , icon.className );
				}
				serializer.attribute( null , TAG_IMAGE , icon.imageName );
				serializer.endTag( null , TAG_ICON );
			}
			serializer.endTag( null , DefaultLayoutHandler.TAG_DEFAULT_LAYOUT );
			serializer.endDocument();
			serializer.flush();
			os.close();
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		SharedPreferences pre = iLoongLauncher.getInstance().getSharedPreferences( CUSTOM_XML_PREFERENCES_NAME , Activity.MODE_PRIVATE );
		pre.edit().putBoolean( CUSTOM_XML_KEY_EXIST , true ).commit();
	}
	
	// added by zhenNan.ye
	private boolean isCustomXmlExist()
	{
		SharedPreferences preferences = iLoongApplication.getInstance().getSharedPreferences( CUSTOM_XML_PREFERENCES_NAME , Activity.MODE_PRIVATE );
		boolean customXmlExist = preferences.getBoolean( CUSTOM_XML_KEY_EXIST , false );
		int oldVersion = preferences.getInt( CUSTOM_XML_KEY_OLD_VERSION , -1 );
		if( oldVersion == -1 )
		{
			try
			{
				oldVersion = iLoongApplication.getInstance().getPackageManager().getPackageInfo( iLoongApplication.getInstance().getPackageName() , 0 ).versionCode;
			}
			catch( NameNotFoundException e )
			{
				e.printStackTrace();
			}
			if( oldVersion != -1 )
			{
				preferences.edit().putInt( CUSTOM_XML_KEY_OLD_VERSION , oldVersion ).commit();
			}
		}
		else
		{
			int curVersion = -1;
			try
			{
				curVersion = iLoongApplication.getInstance().getPackageManager().getPackageInfo( iLoongApplication.getInstance().getPackageName() , 0 ).versionCode;
			}
			catch( NameNotFoundException e )
			{
				e.printStackTrace();
			}
			if( curVersion > oldVersion )
			{
				Log.i( "DefaultLayout" , "CometLauncher is update!" );
				iLoongApplication.getInstance().deleteFile( CUSTOM_XML_FILE_NAME );
				preferences.edit().putBoolean( CUSTOM_XML_KEY_EXIST , false ).commit();
				preferences.edit().putInt( CUSTOM_XML_KEY_OLD_VERSION , curVersion ).commit();
				customXmlExist = false;
			}
		}
		if( isPhoneUpdate() )
		{
			Log.i( "DefaultLayout" , "phone is update!" );
			iLoongApplication.getInstance().deleteFile( CUSTOM_XML_FILE_NAME );
			preferences.edit().putBoolean( CUSTOM_XML_KEY_EXIST , false ).commit();
			customXmlExist = false;
		}
		return customXmlExist;
	}
	
	// added by zhenNan.ye
	public static boolean isPhoneUpdate()
	{
		String oldVersion = null;
		String oldDisplay = null;
		SharedPreferences preferences = iLoongApplication.getInstance().getSharedPreferences( CUSTOM_XML_PREFERENCES_NAME , Activity.MODE_PRIVATE );
		oldVersion = preferences.getString( PHONE_KEY_OLD_VERSION , null );
		oldDisplay = preferences.getString( PHONE_KEY_OLD_DISPLAY , null );
		if( oldVersion != null && oldDisplay != null )
		{
			if( !oldVersion.equals( android.os.Build.VERSION.RELEASE ) || !oldDisplay.equals( android.os.Build.DISPLAY ) )
			{
				preferences.edit().putString( PHONE_KEY_OLD_VERSION , android.os.Build.VERSION.RELEASE ).commit();
				preferences.edit().putString( PHONE_KEY_OLD_DISPLAY , android.os.Build.DISPLAY ).commit();
				return true;
			}
			else
			{
				return false;
			}
		}
		if( oldVersion == null )
		{
			oldVersion = android.os.Build.VERSION.RELEASE;
			preferences.edit().putString( PHONE_KEY_OLD_VERSION , oldVersion ).commit();
		}
		if( oldDisplay == null )
		{
			oldDisplay = android.os.Build.DISPLAY;
			preferences.edit().putString( PHONE_KEY_OLD_DISPLAY , oldDisplay ).commit();
		}
		return false;
	}
	
	public static LoadingDialog mDialogView;
	private Dialog mProgressDialog;
	
	public void showProgressDialog()
	{
		showdialog();
	}
	
	Animation animation_alpha;
	
	private void setParams(
			LayoutParams lay )
	{
		DisplayMetrics dm = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( dm );
		Rect rect = new Rect();
		View view = iLoongLauncher.getInstance().getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame( rect );
		lay.height = dm.heightPixels;
		lay.width = dm.widthPixels;
	}
	
	public void showdialog()
	{
		animation_alpha = new AlphaAnimation( 1f , 0.1f );
		animation_alpha.setRepeatCount( 0 );//设置循环   
		animation_alpha.setDuration( 500 );//设置时间持续时间为 5000毫秒   
		animation_alpha.setAnimationListener( new AnimationListener() {
			
			@Override
			public void onAnimationStart(
					Animation animation )
			{
			}
			
			@Override
			public void onAnimationRepeat(
					Animation animation )
			{
			}
			
			@Override
			public void onAnimationEnd(
					Animation animation )
			{
				if( mProgressDialog != null )
				{
					mProgressDialog.cancel();
				}
				if( mDialogView != null )
				{
					mDialogView.destory();
					mDialogView = null;
				}
			}
		} );
		mDialogView = new LoadingDialog( iLoongLauncher.getInstance() );
		LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
		mProgressDialog = new Dialog( iLoongLauncher.getInstance() , RR.style.LoadingDialogStyle );
		mProgressDialog.setContentView( mDialogView , params );
		mProgressDialog.setCancelable( false );
		mProgressDialog.setCanceledOnTouchOutside( false );
		WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
		mProgressDialog.getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		setParams( lp );
		mProgressDialog.show();
	}
	
	public void dismissLoadingDialog()
	{
		if( mDialogView == null )
			return;
		if( isLoadCompleted() )
		{
			if( mActionHolder != null )
				mActionHolder.show();
			//mDialogView.post( new Runnable() {
			//	@Override
			//	public void run()
			{
				mDialogView.startAnimation( animation_alpha );
			}
			//} );
		}
	}
	
	public static boolean isLoadCompleted()
	{
		return AppList3D.allInit && iLoongLauncher.finishLoad;
	}
	
	public void cancelProgressDialog()
	{
		if( mDialogView == null )
			return;
		if( isWaittingForCancle )
			return;
		isWaittingForCancle = true;
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				while( !isLoadCompleted() )
				{
//					Log.v( "fuckdialog" , "dialog in process" );
				}
				SendMsgToAndroid.cancelLoadingDialog();
			}
		} ) {}.start();
	}
}
