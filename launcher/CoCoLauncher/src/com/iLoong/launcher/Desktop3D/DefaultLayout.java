package com.iLoong.launcher.Desktop3D;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.R.bool;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Xml;
import android.view.ViewGroup.LayoutParams;

import com.badlogic.gdx.Gdx;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotDockGroup;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.HotSeatMainGroup;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.app.AirDefaultLayout;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.FeatureConfig;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;

class ShortcutItem {
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

class FolderList {
	int id;
	String name;
	int repeat_count;
	int cellX;
	int cellY;
	ArrayList<ShortcutItem> shortcutList;
}

class ShortcutGRP {
	String locate;
	int value;
	int id;
	ArrayList<ShortcutItem> shortcutList;
	ArrayList<FolderList> folderList;
}

class WidgetItem {
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
}

class VirtureIcon {
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

class ApplistVirtualIcon {
	String name;
	String packageName;
	String title;
	String imageName;
}

class DefaultIcon {
	String title;
	String pkgName;
	String imageName;
	String className;
	boolean duplicate;
	boolean dealed;
	ArrayList<String> pkgNameArray;
	ArrayList<String> classNameArray;
}

class SysWidget {
	String packageName;
	String className;
	int screen;
	int cellX;
	int cellY;
	int spanX;
	int spanY;
}

class SysShortcut {
	String name;
	String packageName;
	String className;
	String imageName;
	int screen;
	int cellX;
	int cellY;
}

class FactoryApp {
	int id;
	String packageName;
	String className;
	ArrayList<String> pkgNameArray;
}

class DefaultLayoutHandler extends DefaultHandler {
	int curShortGRP;
	int curFolderId;
	int curWidget;
	// int curHotSeat;
	int curApp;
	FolderList curFolder;
	static final String TAG_DEFAULT_LAYOUT = "default_layout";
	static final String TAG_SHORTGRP = "shortcutgroup";
	static final String TAG_ITEM = "item";
	static final String TAG_APP_SORT = "app_sort";
	static final String TAG_SHOW_APP = "show_app";
	static final String TAG_HIDE_APP = "hide_app";
	public static final String TAG_APP = "app";
	public static final String TAG_WIDGET = "widget";
	public static final String TAG_FOLDER = "folder";
	public static final String TAG_VIRTURE = "virtueIcon";
	public static final String TAG_SYSWIDGET = "syswidget";
	public static final String TAG_SYS_SHORTCUT = "sys_shortcut";
	public static final String TAG_APPLIST_VIRTUAL_ICON = "applist_virtual_icon";
	public static final String MM_SETTING = "mm_settting";

	public static final String GENERAl_CONFIG = "general_config";

	public static final String HOTSEAT_CONFIG = "hotseat_config";
	public static final String HOTSEAT_ITEM = "hotseat";

	public boolean parseThemeConfig = false;
	private List<String> themeConfigTag = new ArrayList<String>();
	
	public static final String CFG_S4_MODLE_NAME = "s4_model_name"; 


	public DefaultLayoutHandler() {
		this.curShortGRP = 0;
		this.curFolderId = -1;
		this.curWidget = 0;
		// this.curHotSeat = 0;
		this.curApp = 0;
		parseThemeConfig = false;
		themeConfigTag.add("icon");
	}

	public void startDocument() throws SAXException {
		// Utils3D.showPidMemoryInfo("startDocument");
	}

	public void endDocument() throws SAXException {
		// Utils3D.showPidMemoryInfo("endDocument");
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (parseThemeConfig && !themeConfigTag.contains(localName)) {
			return;
		}
		// Utils3D.showPidMemoryInfo("mem1");
		if (localName.equals(TAG_DEFAULT_LAYOUT)) {
			;
		} else if (localName.equals(TAG_SHORTGRP)) {
			// Log.v("test", "add group");
			ShortcutGRP grp = new ShortcutGRP();
			grp.id = Integer.valueOf(atts.getValue("id"));
			grp.locate = atts.getValue("locate");

			if (atts.getValue("locate_value") != null)
				grp.value = Integer.valueOf(atts.getValue("locate_value"));

			DefaultLayout.allShortcutList.add(this.curShortGRP, grp);
		} else if (localName.equals(HOTSEAT_ITEM)) {
			String hotImageName = null;
			String hotIntent = null;
			int hotid = 0;
			hotImageName = atts.getValue("image");
			if (hotImageName != null) {
				hotImageName = DefaultLayout.HOTSEAT_PATH + hotImageName;
			}
			hotIntent = atts.getValue("hotseatIntent");
			hotid = Integer.valueOf(atts.getValue("id"));
			iLoongLauncher.getInstance().initHotseatItem(hotid, hotImageName,
					hotIntent);

		} else if (localName.equals(GENERAl_CONFIG)) {
			String temp;
			temp = atts.getValue("default_explorer");
			if (temp == null) {
				DefaultLayout.default_explorer = null;
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.default_explorer = null;
				} else {
					DefaultLayout.default_explorer = temp;
				}
			}

			temp = atts.getValue("default_uri");
			if (temp == null) {
				DefaultLayout.defaultUri = null;
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.defaultUri = null;
				} else {
					DefaultLayout.defaultUri = temp;
				}
			}

			temp = atts.getValue("mainmenu_explorers_use_default_uri");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.mainmenu_explorers_use_default_uri = true;
			} else {
				DefaultLayout.mainmenu_explorers_use_default_uri = false;
			}

			temp = atts.getValue("install_change_wallpaper");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.install_change_wallpaper = false;
			} else {
				DefaultLayout.install_change_wallpaper = true;
			}

			// xiatian del start //Mainmenu Bg
			// temp = atts.getValue("mainmenu_addbackgroud");
			// if (temp != null && temp.equals("false")) {
			// DefaultLayout.mainmenu_addbackgroud = false;
			// } else {
			// DefaultLayout.mainmenu_addbackgroud = true;
			// }
			// xiatian del end

			temp = atts.getValue("ThirdAPK_add_background");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.ThirdAPK_add_background = false;
			} else {
				DefaultLayout.ThirdAPK_add_background = true;
			}

			temp = atts.getValue("hide_online_theme_button");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_online_theme_button = true;
			} else {
				DefaultLayout.hide_online_theme_button = false;
			}

			temp = atts.getValue("anti_aliasing");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.anti_aliasing = true;
			} else {
				DefaultLayout.anti_aliasing = false;
			}

			temp = atts.getValue("scene_old");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.scene_old = true;
			} else {
				DefaultLayout.scene_old = false;
			}
			temp = atts.getValue("enable_scroll_to_widget");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.enable_scroll_to_widget = true;
			} else {
				DefaultLayout.enable_scroll_to_widget = false;
			}

			temp = atts.getValue("hide_add_shortcut_dialog");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_add_shortcut_dialog = true;
			} else {
				DefaultLayout.hide_add_shortcut_dialog = false;
			}

			temp = atts.getValue("dispose_cell_count");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.dispose_cell_count = true;
			} else {
				DefaultLayout.dispose_cell_count = false;
			}
			if (atts.getValue("cellCountX") != null)
				DefaultLayout.cellCountX = Integer.valueOf(atts
						.getValue("cellCountX"));
			if (atts.getValue("cellCountY") != null)
				DefaultLayout.cellCountY = Integer.valueOf(atts
						.getValue("cellCountY"));

			// temp = atts.getValue("appbar_no_menu");
			// if (temp!=null && temp.equals("true")) {
			// DefaultLayout.appbar_no_menu = true;
			// } else {
			// DefaultLayout.appbar_no_menu = false;
			// }

			temp = atts.getValue("appbar_bag_icon");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.appbar_bag_icon = true;
			} else {
				DefaultLayout.appbar_bag_icon = false;
			}

			temp = atts.getValue("display_widget_preview_hole");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.display_widget_preview_hole = true;
			} else {
				DefaultLayout.display_widget_preview_hole = false;
			}

			if (atts.getValue("default_home_page") != null)
				DefaultLayout.default_home_page = Integer.valueOf(atts
						.getValue("default_home_page"));

			if (atts.getValue("default_workspace_pagecounts") != null)
				DefaultLayout.default_workspace_pagecounts = Integer
						.valueOf(atts.getValue("default_workspace_pagecounts"));

			if (atts.getValue("default_workspace_pagecount_min") != null)
				DefaultLayout.default_workspace_pagecount_min = Integer
						.valueOf(atts
								.getValue("default_workspace_pagecount_min"));

			if (atts.getValue("default_workspace_pagecount_max") != null)
				DefaultLayout.default_workspace_pagecount_max = Integer
						.valueOf(atts
								.getValue("default_workspace_pagecount_max"));

			temp = atts.getValue("disable_shake_wallpaper");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.disable_shake_wallpaper = true;
			} else {
				DefaultLayout.disable_shake_wallpaper = false;
			}
			temp = atts.getValue("default_open_shake_wallpaper");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.default_open_shake_wallpaper = true;
			} else {
				DefaultLayout.default_open_shake_wallpaper = false;
			}
			temp = atts.getValue("appbar_show_userapp_list");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.appbar_show_userapp_list = true;
			} else {
				DefaultLayout.appbar_show_userapp_list = false;
			}

			if (atts.getValue("icon_title_font") != null)
				DefaultLayout.icon_title_font = Integer.valueOf(atts
						.getValue("icon_title_font"));

			if (atts.getValue("widget_title_weight") != null)
				DefaultLayout.widget_title_weight = Float.valueOf(atts
						.getValue("widget_title_weight"));

			temp = atts.getValue("show_page_edit_on_key_back");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.show_page_edit_on_key_back = true;
			} else {
				DefaultLayout.show_page_edit_on_key_back = false;
			}

			temp = atts.getValue("disable_x_effect");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.disable_x_effect = true;
			} else {
				DefaultLayout.disable_x_effect = false;
			}

			temp = atts.getValue("loadapp_in_background");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.loadapp_in_background = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.loadapp_in_background = false;
			}

			temp = atts.getValue("enable_icon_effect");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.enable_icon_effect = true;
			} else {
				DefaultLayout.enable_icon_effect = false;
			}

			if (atts.getValue("mainmenu_page_effect_id") != null)
				DefaultLayout.mainmenu_page_effect_id = Integer.valueOf(atts
						.getValue("mainmenu_page_effect_id"));

			if (atts.getValue("desktop_page_effect_id") != null)
				DefaultLayout.desktop_page_effect_id = Integer.valueOf(atts
						.getValue("desktop_page_effect_id"));

			temp = atts.getValue("default_S3_theme");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.default_S3_theme = false;
			} else {
				DefaultLayout.default_S3_theme = true;
			}

			temp = atts.getValue("setupmenu_show_theme");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.setupmenu_show_theme = false;
			} else {
				DefaultLayout.setupmenu_show_theme = true;
			}

			temp = atts.getValue("custom_wallpapers_path");
			if (temp == null) {
				DefaultLayout.custom_wallpapers_path = "";
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.custom_wallpapers_path = "";
				} else {
					DefaultLayout.custom_wallpapers_path = temp;
				}
			}

			temp = atts.getValue("custom_default_wallpaper_name");
			if (temp == null) {
				DefaultLayout.custom_default_wallpaper_name = "";
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.custom_default_wallpaper_name = "";
				} else {
					DefaultLayout.custom_default_wallpaper_name = temp;
				}
			}

			temp = atts.getValue("release_memory_after_pause");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.release_memory_after_pause = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.release_memory_after_pause = false;
			}

			temp = atts.getValue("mainmenu_position");
			if (temp == null) {
				DefaultLayout.mainmenu_pos = 4;
			} else {
				if (temp != null && temp.toLowerCase().equals("right")) {
					DefaultLayout.mainmenu_pos = 4;
				} else {
					DefaultLayout.mainmenu_pos = 2;
				}
			}
			temp = atts.getValue("thirdapk_icon_scaleFactor");
			if (temp == null) {
				DefaultLayout.thirdapk_icon_scaleFactor = 1.0f;
			} else {
				if (temp.equals("100")) {
					temp = "70";
				}
				DefaultLayout.thirdapk_icon_scaleFactor = Integer.valueOf(temp) / 100f;
			}

			if (atts.getValue("trash_icon_pos") != null)
				DefaultLayout.trash_icon_pos = Integer.valueOf(atts
						.getValue("trash_icon_pos"));

			if (atts.getValue("hot_dock_icon_number") != null)
				DefaultLayout.hot_dock_icon_number = Integer.valueOf(atts
						.getValue("hot_dock_icon_number"));
			if (atts.getValue("sensor_delay_level") != null)
				DefaultLayout.sensor_delay_level = Integer.valueOf(atts
						.getValue("sensor_delay_level"));

			temp = atts.getValue("app_icon_size");
			if (temp == null) {
				DefaultLayout.app_icon_size = -1;
			} else {
				DefaultLayout.app_icon_size = Integer.valueOf(temp);
			}
			temp = atts.getValue("custom_virtual_path");
			if (temp == null) {
				DefaultLayout.custom_virtual_path = "";
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.custom_virtual_path = "";
				} else {
					DefaultLayout.custom_virtual_path = temp;
					File dir = new File(DefaultLayout.custom_virtual_path);
					if (dir.exists()) {
						DefaultLayout.useCustomVirtual = true;
					}
				}
			}
			temp = atts.getValue("custom_virtual_download_path");
			if (temp == null) {
				DefaultLayout.custom_virtual_download_path = "";
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.custom_virtual_download_path = "";
				} else {
					DefaultLayout.custom_virtual_download_path = temp;
					File dir = new File(
							DefaultLayout.custom_virtual_download_path);
					if (dir.exists()) {
						DefaultLayout.useCustomVirtualDownload = true;
					}
				}
			}
			temp = atts.getValue("custom_sys_shortcut_path");
			if (temp == null) {
				DefaultLayout.custom_sys_shortcut_path = "";
			} else {
				if (temp.equals("nothing")) {
					DefaultLayout.custom_sys_shortcut_path = "";
				} else {
					DefaultLayout.custom_sys_shortcut_path = temp;
					File dir = new File(DefaultLayout.custom_sys_shortcut_path);
					if (dir.exists()) {
						DefaultLayout.useCustomSysShortcut = true;
					}
				}
			}

			temp = atts.getValue("custom_virtual_icon");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.custom_virtual_icon = false;
			} else if (temp != null && temp.equals("true")) {
				DefaultLayout.custom_virtual_icon = true;
			}

			temp = atts.getValue("hide_mainmenu_widget");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_mainmenu_widget = false;
			} else if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_mainmenu_widget = true;
			}

			temp = atts.getValue("hide_launcher_wallpapers");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_launcher_wallpapers = false;
			} else if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_launcher_wallpapers = true;
			}

			temp = atts.getValue("broadcast_state");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.broadcast_state = false;
			} else if (temp != null && temp.equals("true")) {
				DefaultLayout.broadcast_state = true;
			}

			temp = atts.getValue("applist_style_classic");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.applist_style_classic = false;
			} else if (temp != null && temp.equals("true")) {
				DefaultLayout.applist_style_classic = true;
				DefaultLayout.mainmenu_pos = 2;
			}

			temp = atts.getValue("hide_appbar");
			if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_appbar = false;
			} else if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_appbar = true;
			}

			temp = atts.getValue("show_font_bg");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.show_font_bg = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.show_font_bg = false;
			}

			temp = atts.getValue("font_double_line");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.font_double_line = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.font_double_line = false;
			}

			temp = atts.getValue("hide_backup_and_restore");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_backup_and_restore = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_backup_and_restore = false;
			}

			// xiatian del start //Mainmenu Bg
			// temp = atts.getValue("mainmenu_add_black_ground");
			// if (temp != null && temp.equals("true")) {
			// DefaultLayout.mainmenu_add_black_ground = true;
			// } else if (temp != null && temp.equals("false")) {
			// DefaultLayout.mainmenu_add_black_ground = false;
			// }
			// xiatian del end

			temp = atts.getValue("hotseatbar_browser_special_name");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseatbar_browser_special_name = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseatbar_browser_special_name = false;
			}
			// Log.v("jbc","abcdabc="+R3D.getInteger("pop_setupmenu_style"));
			// if (R3D.getInteger("pop_setupmenu_style")==-1) {
			// if (atts.getValue("popmenu_style") != null)
			// DefaultLayout.popmenu_style =
			// Integer.valueOf(atts.getValue("popmenu_style"));
			// } else {
			// DefaultLayout.popmenu_style =
			// R3D.getInteger("pop_setupmenu_style");
			// }
			// //zqh modifies on 12-05-2012, in order to show PopMenu
			// if(DefaultLayout.show_explorer){
			// DefaultLayout.popmenu_style =SetupMenu.POPMENU_STYLE_ORIGINAL;
			// }

			temp = atts.getValue("click_indicator_enter_pageselect");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.click_indicator_enter_pageselect = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.click_indicator_enter_pageselect = false;
			}
			temp = atts.getValue("hotseat_title_no_background");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseat_title_no_background = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseat_title_no_background = false;
			}
			temp = atts.getValue("hotseat_icon_pos_fixed");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseat_icon_pos_fixed = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseat_icon_pos_fixed = false;
			}
			temp = atts.getValue("workspace_longclick_display_contacts");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.workspace_longclick_display_contacts = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.workspace_longclick_display_contacts = false;
			}
			temp = atts.getValue("mainmenu_widget_display_contacts");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.mainmenu_widget_display_contacts = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.mainmenu_widget_display_contacts = false;
			}
			temp = atts.getValue("hide_desktop_setup");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_desktop_setup = true;
				DefaultLayout.popmenu_style = 1;// 弹出菜单强制为android4.0风格
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_desktop_setup = false;
			}
			temp = atts.getValue("enable_service");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.enable_service = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.enable_service = false;
			}
			temp = atts.getValue("show_widget_shortcut_bg");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.show_widget_shortcut_bg = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.show_widget_shortcut_bg = false;
			}
			temp = atts.getValue("widget_shortcut_lefttop");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.widget_shortcut_lefttop = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.widget_shortcut_lefttop = false;
			}

			temp = atts.getValue("hide_remove_theme_button");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_remove_theme_button = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_remove_theme_button = false;
			}

			temp = atts.getValue("hotseat_style_ex");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseat_style_ex = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseat_style_ex = false;
			}

			temp = atts.getValue("hotseat_hide_title");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseat_hide_title = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseat_hide_title = false;
			}

			temp = atts.getValue("widget_shortcut_title_top");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.widget_shortcut_title_top = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.widget_shortcut_title_top = false;
			}

			temp = atts.getValue("hide_home_button");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_home_button = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_home_button = false;
			}

			temp = atts.getValue("setupmenu_show_clear");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.setupmenu_show_clear = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.setupmenu_show_clear = false;
			}

			temp = atts.getValue("disable_double_click");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.disable_double_click = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.disable_double_click = false;
			}

			temp = atts.getValue("hide_title_bg_shadow");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hide_title_bg_shadow = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hide_title_bg_shadow = false;
			}

			if (atts.getValue("title_outline_shadow_size") != null)
				DefaultLayout.title_outline_shadow_size = Integer.valueOf(atts
						.getValue("title_outline_shadow_size"));
			if (atts.getValue("title_outline_color") != null)
				DefaultLayout.title_outline_color = Color.parseColor(atts
						.getValue("title_outline_color"));
			if (atts.getValue("title_shadow_color") != null)
				DefaultLayout.title_shadow_color = Color.parseColor(atts
						.getValue("title_shadow_color"));

			temp = atts.getValue("hotseat_app_title_colorful");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseat_app_title_colorful = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseat_app_title_colorful = false;
			}
			if (atts.getValue("hotseat_app_title_r") != null)
				DefaultLayout.hotseat_app_title_r = Integer.valueOf(atts
						.getValue("hotseat_app_title_r"));
			if (atts.getValue("hotseat_app_title_g") != null)
				DefaultLayout.hotseat_app_title_g = Integer.valueOf(atts
						.getValue("hotseat_app_title_g"));
			if (atts.getValue("hotseat_app_title_b") != null)
				DefaultLayout.hotseat_app_title_b = Integer.valueOf(atts
						.getValue("hotseat_app_title_b"));
			if (atts.getValue("hotseat_app_title_a") != null)
				DefaultLayout.hotseat_app_title_a = Integer.valueOf(atts
						.getValue("hotseat_app_title_a"));

			if (atts.getValue("install_apk_delay") != null)
				DefaultLayout.install_apk_delay = Integer.valueOf(atts
						.getValue("install_apk_delay"));

			temp = atts.getValue("show_missed_call_sms");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.show_missed_call_sms = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.show_missed_call_sms = false;
			}
			temp = atts.getValue("call_component_name");
			if (temp != null) {
				DefaultLayout.call_component_name = temp;
			}

			temp = atts.getValue("title_style_bold");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.title_style_bold = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.title_style_bold = false;
			}

			temp = atts.getValue("disable_theme_preview_tween");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.disable_theme_preview_tween = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.disable_theme_preview_tween = false;
			}

			temp = atts.getValue("menu_wyd");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.menu_wyd = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.menu_wyd = false;
			}
			temp = atts.getValue("hotseatbar_no_panel");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.hotseatbar_no_panel = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.hotseatbar_no_panel = false;
			}
			temp = atts.getValue("empty_page_add_reminder");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.empty_page_add_reminder = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.empty_page_add_reminder = false;
			}
			if (atts.getValue("reminder_font") != null)
				DefaultLayout.reminder_font = Integer.valueOf(atts
						.getValue("reminder_font"));

			temp = atts.getValue("appbar_widgets_special_name");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.appbar_widgets_special_name = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.appbar_widgets_special_name = false;
			}
			temp = atts.getValue("disable_vibrator");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.disable_vibrator = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.disable_vibrator = false;
			}

			temp = atts.getValue("show_introduction");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.show_introduction = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.show_introduction = false;
			}
			temp = atts.getValue("default_close_vibrator");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.default_close_vibrator = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.default_close_vibrator = false;
			}
			temp = atts.getValue("restart_when_orientation_change");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.restartWhenOrientationChange = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.restartWhenOrientationChange = false;
			}

			
			 temp = atts.getValue("setup_menu_support_scroll_page");
            if (temp != null && temp.equals("true")) {
                DefaultLayout.setup_menu_support_scroll_page = true;
            } else if (temp != null && temp.equals("false")) {
                DefaultLayout.setup_menu_support_scroll_page = false;
            }
            
			temp = atts.getValue("keypad_event_of_focus");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.keypad_event_of_focus = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.keypad_event_of_focus = false;
			}
			temp = atts.getValue("show_explorer");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.enable_explorer = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.enable_explorer = false;
			}

			temp = atts.getValue("blend_func_dst_gl_one");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.blend_func_dst_gl_one = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.blend_func_dst_gl_one = false;
			}
			temp = atts.getValue("reduce_load_priority");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.reduce_load_priority = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.reduce_load_priority = false;
			}

			temp = atts.getValue("widget_revise_complete");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.widget_revise_complete = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.widget_revise_complete = false;
			}

			temp = atts.getValue("wallpaper_has_edage");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.wallpaper_has_edage = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.wallpaper_has_edage = false;
			}

			temp = atts.getValue("mainmenu_longclick_pageedit");
			if (temp != null) {
				DefaultLayout.mainmenu_longclick_pageedit = temp.equals("true");
			}

			temp = atts.getValue("hotseat_disable_make_folder");
			if (temp != null) {
				DefaultLayout.hotseat_disable_make_folder = temp.equals("true");
			}

			temp = atts.getValue("hotseat_title_disable_click");
			if (temp != null) {
				DefaultLayout.hotseat_title_disable_click = temp.equals("true");
			}

			temp = atts.getValue("bjx_sale_log");
			if (temp != null) {
				DefaultLayout.bjx_sale_log = temp.equals("true");
			}

			temp = atts.getValue("add_desktop_list_font_set_color");
			if (temp != null) {
				DefaultLayout.add_desktop_list_font_set_color = temp
						.equals("true");
			}
			if (atts.getValue("add_desktop_list_font_color") != null)
				DefaultLayout.add_desktop_list_font_color = Color
						.parseColor(atts
								.getValue("add_desktop_list_font_color"));

			temp = atts.getValue("desktop_hide_frame");
			if (temp != null) {
				DefaultLayout.desktop_hide_frame = temp.equals("true");
			}

			temp = atts.getValue("appbar_font_bigger");
			if (temp != null) {
				DefaultLayout.appbar_font_bigger = temp.equals("true");
			}

			temp = atts.getValue("disable_move_wallpaper");
			if (temp != null) {
				DefaultLayout.disable_move_wallpaper = temp.equals("true");
			}

			temp = atts.getValue("mainmenu_inout_no_anim");
			if (temp != null) {
				DefaultLayout.mainmenu_inout_no_anim = temp.equals("true");
			}

//			temp = atts.getValue("folder_no_dragon");
//			if (temp != null) {
//				DefaultLayout.folder_no_dragon = temp.equals("true");
//			}

			if (atts.getValue("page_tween_time") != null)
				DefaultLayout.page_tween_time = Float.valueOf(atts
						.getValue("page_tween_time"));
			temp = atts.getValue("show_icon_size");
			if (temp != null) {
				DefaultLayout.show_icon_size = temp.equals("true");
			}

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			temp = atts.getValue("show_sensor");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.show_sensor = true;
			} else {
				DefaultLayout.show_sensor = false;
			}

			if (DefaultLayout.show_sensor) {
				temp = atts.getValue("default_open_sensor");
				if (temp != null && temp.equals("true")) {
					DefaultLayout.default_open_sensor = true;
				} else {
					DefaultLayout.default_open_sensor = false;
				}
			}
			// xiatian add end

			// removed by zhenNan.ye 
//			temp = atts.getValue("enable_air_default_layout");
//			if (temp != null) {
//				DefaultLayout.enable_air_default_layout = temp.equals("true");
//			}

			/***************** added by zhenNan.ye begin *******************/
			temp = atts.getValue("enable_particle");
			if (temp != null) {
				DefaultLayout.enable_particle = temp.equals("true");
			}
			/***************** added by zhenNan.ye end *******************/

		

			// xiatian add start //is_demo_version
			temp = atts.getValue("is_demo_version");
			if (temp != null) {
				DefaultLayout.is_demo_version = temp.equals("true");

				if (DefaultLayout.is_demo_version) {
					DefaultLayout.popmenu_style = SetupMenu.POPMENU_STYLE_ORIGINAL;
				}
			}
			// xiatian add end

			// teapotXu add start for Folder in Mainmenu
			temp = atts.getValue("mainmenu_folder_function");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.mainmenu_folder_function = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.mainmenu_folder_function = false;
			}
			// teapotXu add end for Folder in Mainmenu

			// teapotXu add start for pages effects in Mainmenu
			temp = atts.getValue("external_applist_page_effect");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.external_applist_page_effect = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.external_applist_page_effect = false;
			}
			// teapotXu add end

			// teapotXu add start for HotSeatBar icons Same spacing
			temp = atts.getValue("same_spacing_btw_hotseat_icons");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.same_spacing_btw_hotseat_icons = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.same_spacing_btw_hotseat_icons = false;
			}
			// teapotXu add end for hotSeatBar icons same Spacing
			// teapotXu add start for
			temp = atts.getValue("enhance_generate_mainmenu_folder_condition");
			if (temp != null) {
				DefaultLayout.enhance_generate_mainmenu_folder_condition = temp
						.equals("true");
			}
			// teapotXu add end

			// teapotXu add start for HotSeatBar icons Same spacing
			temp = atts.getValue("same_spacing_btw_hotseat_icons");
			if (temp != null && temp.equals("true")) {
				DefaultLayout.same_spacing_btw_hotseat_icons = true;
			} else if (temp != null && temp.equals("false")) {
				DefaultLayout.same_spacing_btw_hotseat_icons = false;
			}
			// teapotXu add end for hotSeatBar icons same Spacing

			temp = atts.getValue("enable_DesktopIndicatorScroll");
			if (temp != null) {
				DefaultLayout.enable_DesktopIndicatorScroll = temp
						.equals("true");
			}

			// xiatian add start //add icon shadow
			temp = atts.getValue("icon_shadow_radius");
			if (temp != null) {
				DefaultLayout.icon_shadow_radius = Float.valueOf(temp);
			}
			// xiatian add end
			temp = atts.getValue("enable_texture_pack");
			if (temp != null) {
				DefaultLayout.enable_texture_pack = temp.equals("true");
			}

		} else if (localName.equals(TAG_APP_SORT)) {
			String str = atts.getValue("show");
			if (str == null) {
				DefaultLayout.show_default_app_sort = false;
			} else {
				if (str.equals("true")) {
					DefaultLayout.show_default_app_sort = true;
				} else {
					DefaultLayout.show_default_app_sort = false;
				}
			}
		} else if (localName.equals(TAG_SHOW_APP)) {
			// enable_themebox
			String className = atts.getValue("pkgname");
			if (className
					.equals("com.iLoong.launcher.theme.ThemeManagerActivity")
					|| className.equals("com.coco.theme.themebox.MainActivity")) {
			} else if (className
					.equals("com.iLoong.launcher.theme.ThemeChangeActivity")
					&& FeatureConfig.enable_themebox) {

			} else
				DefaultLayout.showAppList.add(className);
			// enable_themebox
		} else if (localName.equals(TAG_HIDE_APP)) {
			DefaultLayout.hideAppList.add(atts.getValue("compname"));
		} else if (localName.equals(TAG_APP)) {
			FactoryApp app = new FactoryApp();
			app.id = Integer.valueOf(atts.getValue("id"));
			app.packageName = atts.getValue("pkgname");
			app.className = atts.getValue("componentName");

			DefaultLayout.facApp.add(this.curApp, app);
		} else if (localName.equals(MM_SETTING)) {
			Log.v("test", "add MM_SETTING");
			String index;

			for (int j = 0; j < DefaultLayout.MM_SETTING_ELEMENT; j++) {
				index = "e_" + j;
				DefaultLayout.mmData[j] = atts.getValue(index);
			}
		} else if (localName.equals("icon")) {
			// Log.e("theme", "parse icon");
			if (DefaultLayout.specifiedIconsLoaded) {
				return;
			}
			
			DefaultIcon temp = new DefaultIcon();
			temp.title = atts.getValue("name");
			Iterator<DefaultIcon> ite = DefaultLayout.defaultIcon.iterator();
			while (ite.hasNext()) {
				DefaultIcon icon = ite.next();
				if (icon.title.equals(temp.title))
					ite.remove();
			}
			if (atts.getValue("dup") == null)
				temp.duplicate = false;
			else    
				temp.duplicate = true;

			temp.pkgName = atts.getValue("pkgname");
			temp.imageName = atts.getValue("image");
			temp.className = atts.getValue("componentName");
			if (temp.className == null)
				temp.className = "";
			temp.dealed = false;
			DefaultLayout.defaultIcon.add(temp);
		} else if (localName.equals(TAG_WIDGET)) {
			// Log.v("test", "add widget");
			WidgetItem wdg = new WidgetItem();
			wdg.id = Integer.valueOf(atts.getValue("id"));
			wdg.name = DefaultLayout
					.pickNameByLanSetting(atts.getValue("name"));
			wdg.locate = atts.getValue("locate");
			wdg.value = Integer.valueOf(atts.getValue("locate_value"));
			wdg.pkgName = atts.getValue("pkgname");
			wdg.imageName = atts.getValue("image");
			wdg.apkName = atts.getValue("apkname");
			wdg.minWidth = Integer.valueOf(atts.getValue("width"));
			wdg.minHeight = Integer.valueOf(atts.getValue("height"));
			wdg.customID = atts.getValue("customID");
			String desk = atts.getValue("desktop");
			if (desk != null && desk.equals("false"))
				wdg.addDesktop = false;
			else
				wdg.addDesktop = true;

			if (atts.getValue("spanx") != null)
				wdg.spanX = Integer.valueOf(atts.getValue("spanx"));
			else
				wdg.spanX = 0;
			if (atts.getValue("spany") != null)
				wdg.spanY = Integer.valueOf(atts.getValue("spany"));
			else
				wdg.spanY = 0;

			if (atts.getValue("cellX") != null)
				wdg.cellX = Integer.valueOf(atts.getValue("cellX"));
			else
				wdg.cellX = -1;
			if (atts.getValue("cellY") != null)
				wdg.cellY = Integer.valueOf(atts.getValue("cellY"));
			else
				wdg.cellY = -1;
			String loadByInternal = "false";
			loadByInternal = atts.getValue("loadByInternal");
			if (loadByInternal != null && loadByInternal.equals("true")) {
				wdg.loadByInternal = true;
			} else {
				wdg.loadByInternal = false;
			}
			wdg.className = atts.getValue("classname");
			Log.d("launcher", "name:" + wdg.name + "," + wdg.spanX + ","
					+ wdg.spanY);

			boolean find = false;
			for (int i = 0; i < DefaultLayout.allWidget.size(); i++) {
				if (DefaultLayout.allWidget.get(i).pkgName.equals(wdg.pkgName)) {
					DefaultLayout.allWidget.set(i, wdg);
					find = true;
					break;
				}
			}
			if (!find) {
				DefaultLayout.allWidget.add(this.curWidget, wdg);
			}

			find = false;
			for (int i = 0; i < DefaultLayout.allWidgetFinal.size(); i++) {
				if (DefaultLayout.allWidgetFinal.get(i).pkgName
						.equals(wdg.pkgName)) {
					DefaultLayout.allWidgetFinal.set(i, wdg);
					find = true;
					break;
				}
			}

			if (!find) {
				DefaultLayout.allWidgetFinal.add(this.curWidget, wdg);
			}

		} else if (localName.equals(TAG_VIRTURE)) {
			// Log.v("test", "add virture");
			VirtureIcon wdg = new VirtureIcon();   
			wdg.name = DefaultLayout
					.pickNameByLanSetting(atts.getValue("name"));
			wdg.locate = atts.getValue("locate");

			if (atts.getValue("locate_value") != null)
				wdg.value = Integer.valueOf(atts.getValue("locate_value"));
			else
				wdg.value = 0;

			// wdg.x = Integer.valueOf(atts.getValue("x"));
			// wdg.y = Integer.valueOf(atts.getValue("y"));
			wdg.pkgName = atts.getValue("pkgname");
			wdg.className = atts.getValue("componentName");
			wdg.imageName = atts.getValue("image");
			wdg.apkName = atts.getValue("apkname");
			wdg.customID = atts.getValue("customID");
			if (atts.getValue("cellX") != null)
				wdg.cellX = Integer.valueOf(atts.getValue("cellX"));
			else
				wdg.cellX = -1;
			if (atts.getValue("cellY") != null)
				wdg.cellY = Integer.valueOf(atts.getValue("cellY"));
			else
				wdg.cellY = -1;
			if (atts.getValue("from_airpush") != null)
				wdg.fromAirPush = atts.getValue("from_airpush").equals("true");
			wdg.folder = this.curFolder;

			DefaultLayout.allVirture.add(wdg);
		} else if (localName.equals(TAG_FOLDER)) {
			// Log.v("test", "add folder");
			FolderList folder = new FolderList();
			folder.id = Integer.valueOf(atts.getValue("id"));
			folder.name = DefaultLayout.pickNameByLanSetting(atts
					.getValue("name"));
			folder.repeat_count = 0;
			folder.cellX = Integer.valueOf(atts.getValue("cellX"));
			folder.cellY = Integer.valueOf(atts.getValue("cellY"));

			this.curFolderId = folder.id;
			this.curFolder = folder;

			if (DefaultLayout.allShortcutList.get(this.curShortGRP).folderList == null) {
				Log.e("folder", "init folder:" + this.curShortGRP);
				DefaultLayout.allShortcutList.get(this.curShortGRP).folderList = new ArrayList<FolderList>();
			}
			// DefaultLayout.allShortcutList.get(this.curShortGRP).folderList.add(
			// folder.id, folder);
			DefaultLayout.allShortcutList.get(this.curShortGRP).folderList
					.add(folder);
		} else if (localName.equals(TAG_ITEM)) {
			// Log.v("test", "add item");
			boolean onFolder = false;
			ShortcutItem item = new ShortcutItem();

			item.id = Integer.valueOf(atts.getValue("id"));
			item.name = DefaultLayout.pickNameByLanSetting(atts
					.getValue("name"));
			item.imageName = atts.getValue("image");
			item.pkgName = atts.getValue("pkgname");
			item.className = atts.getValue("componentName");

			// Log.v("test", "name = " + item.pkgName);

			if (atts.getValue("cellX") != null)
				item.cellX = Integer.valueOf(atts.getValue("cellX"));
			else
				item.cellX = -1;
			if (atts.getValue("cellY") != null)
				item.cellY = Integer.valueOf(atts.getValue("cellY"));
			else
				item.cellY = -1;

			item.folderId = this.curFolderId;

			if (item.folderId != -1)
				onFolder = true;

			if (atts.getValue("cls_name") != null
					&& atts.getValue("cls_name").equals("Contact3DShortcut")) {
				item.isContact = true;
			}

			if (onFolder) {
				if (DefaultLayout.allShortcutList.get(this.curShortGRP).folderList == null) {
					DefaultLayout.allShortcutList.get(this.curShortGRP).folderList = new ArrayList<FolderList>();
				}
				if (DefaultLayout.allShortcutList.get(this.curShortGRP).folderList
						.get(this.curFolderId).shortcutList == null) {
					DefaultLayout.allShortcutList.get(this.curShortGRP).folderList
							.get(this.curFolderId).shortcutList = new ArrayList<ShortcutItem>();
				}
				DefaultLayout.allShortcutList.get(this.curShortGRP).folderList
						.get(this.curFolderId).shortcutList.add(item);
			} else {
				if (DefaultLayout.allShortcutList.get(this.curShortGRP).shortcutList == null) {
					DefaultLayout.allShortcutList.get(this.curShortGRP).shortcutList = new ArrayList<ShortcutItem>();
				}
				DefaultLayout.allShortcutList.get(this.curShortGRP).shortcutList
						.add(item);
			}
		} else if (localName.equals(TAG_SYSWIDGET)) {
			SysWidget widget = new SysWidget();
			widget.packageName = atts.getValue("packageName");
			widget.className = atts.getValue("className");

			if (atts.getValue("screen") != null)
				widget.screen = Integer.valueOf(atts.getValue("screen"));
			else
				widget.screen = 0;
			if (atts.getValue("cellX") != null)
				widget.cellX = Integer.valueOf(atts.getValue("cellX"));
			else
				widget.cellX = -1;
			if (atts.getValue("cellY") != null)
				widget.cellY = Integer.valueOf(atts.getValue("cellY"));
			else
				widget.cellY = -1;
			if (atts.getValue("spanX") != null)
				widget.spanX = Integer.valueOf(atts.getValue("spanX"));
			else
				widget.spanX = -1;
			if (atts.getValue("spanY") != null)
				widget.spanY = Integer.valueOf(atts.getValue("spanY"));
			else
				widget.spanY = -1;

			DefaultLayout.allSysWidget.add(widget);
		} else if (localName.equals(TAG_SYS_SHORTCUT)) {
			SysShortcut shortcut = new SysShortcut();
			shortcut.name = atts.getValue("name");
			shortcut.packageName = atts.getValue("packageName");
			shortcut.className = atts.getValue("className");
			shortcut.imageName = atts.getValue("image");

			if (atts.getValue("screen") != null)
				shortcut.screen = Integer.valueOf(atts.getValue("screen"));
			else
				shortcut.screen = 0;
			if (atts.getValue("cellX") != null)
				shortcut.cellX = Integer.valueOf(atts.getValue("cellX"));
			else
				shortcut.cellX = -1;
			if (atts.getValue("cellY") != null)
				shortcut.cellY = Integer.valueOf(atts.getValue("cellY"));
			else
				shortcut.cellY = -1;

			DefaultLayout.allSysShortcut.add(shortcut);
		}
		else if (localName.equals(TAG_APPLIST_VIRTUAL_ICON)) {
			ApplistVirtualIcon virtualIcon = new ApplistVirtualIcon();
			virtualIcon.name = atts.getValue("name");
			virtualIcon.title = atts.getValue("title");
			virtualIcon.packageName = atts.getValue("packagename");
			virtualIcon.imageName = atts.getValue("image");
			DefaultLayout.allAppListVirtualIcon.add(virtualIcon);
		}
		else if(localName.equals(CFG_S4_MODLE_NAME)){
			Log.v("test", "add CFG_S4_MODLE_NAME");
			String model_name_total_str = atts.getValue("model_name");
			if(model_name_total_str != null){
				DefaultLayout.s4_modle_name = model_name_total_str.split(",");
			}

		}

	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (parseThemeConfig && !themeConfigTag.contains(localName)) {
			return;
		}
		// Utils3D.showPidMemoryInfo("mem2");
		if (localName.equals(TAG_SHORTGRP)) {
			this.curShortGRP++;
			Log.e("folder", "curShortGRP:" + this.curShortGRP);
			// this.curFolder = 0;
		} else if (localName.equals(TAG_FOLDER)) {
			this.curFolderId = -1;
		} else if (localName.equals(TAG_WIDGET)) {
			this.curWidget++;
		} else if (localName.equals(TAG_APP)) {
			this.curApp++;
		}
	}

}

public class DefaultLayout {
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
	public static HashMap<FolderList, UserFolderInfo> allFolder = new HashMap<FolderList, UserFolderInfo>();
	public static ArrayList<DefaultIcon> defaultIcon = new ArrayList<DefaultIcon>();

	// public static ArrayList<ShortcutItem> allHotSeatList = new
	// ArrayList<ShortcutItem>();
	public static List<ResolveInfo> allApp;
	public static ArrayList<SysWidget> allSysWidget = new ArrayList<SysWidget>();
	public static ArrayList<SysShortcut> allSysShortcut = new ArrayList<SysShortcut>();
	public static ArrayList<FactoryApp> facApp = new ArrayList<FactoryApp>();
	public static ArrayList<String> showAppList = new ArrayList<String>();
	public static ArrayList<ApplistVirtualIcon> allAppListVirtualIcon = new ArrayList<ApplistVirtualIcon>();

	static {
		// enable_themebox
		String className = "";
		if (FeatureConfig.enable_themebox)
			className = "com.coco.theme.themebox.MainActivity";
		else
			className = "com.iLoong.launcher.theme.ThemeManagerActivity";
		showAppList.add(className);
		// enable_themebox

		// xiatian add start //New Requirement 20130507
		if (FeatureConfig.enable_WallpaperBox) {
			// showAppList.add(iLoongLauncher.WALLPAPER_BOX_MAIN_ACTIVITY);
		}

		if (FeatureConfig.enable_SceneBox) {
			// showAppList.add(iLoongLauncher.SCENE_BOX_MAIN_ACTIVITY);
		}
		// xiatian add end

	}
	public static ArrayList<String> hideAppList = new ArrayList<String>();
	static DefaultLayout defLayout;

	/* special key function */
	// public static int MM_SETTING_NUM = 3;
	public static int MM_SETTING_ELEMENT = 5;

	public static String mmData[] = new String[MM_SETTING_ELEMENT];

	public static String default_explorer = null;
	public static String defaultUri = null;
	public static boolean mainmenu_explorers_use_default_uri = false;
	public static boolean install_change_wallpaper = false;
	// public static boolean mainmenu_addbackgroud = false; //xiatian del
	// //Mainmenu Bg
	public static boolean show_default_app_sort = false;
	public static boolean ThirdAPK_add_background = false;
	public static boolean hide_online_theme_button = false;
	public static boolean anti_aliasing = false;
	public static boolean scene_old = false;// 判断场景桌面是否存储上一个，true表示允许  // wanghongjian add
	public static boolean enable_scroll_to_widget = false;
	public static boolean hide_add_shortcut_dialog = false;
	public static boolean dispose_cell_count = false;
	public static int cellCountX = 4;
	public static int cellCountY = 4;
	public static boolean appbar_no_menu = true;
	public static boolean appbar_bag_icon = false;
	public static int default_home_page = 0;
	public static int default_workspace_pagecounts = 5;
	public static int default_workspace_pagecount_min = 5;
	public static int default_workspace_pagecount_max = 9;
	public static boolean disable_shake_wallpaper = false;
	public static boolean default_open_shake_wallpaper = false;
	public static int icon_title_font = 14;// dip
	public static boolean show_page_edit_on_key_back = false;
	public static boolean disable_x_effect = false;
	public static boolean loadapp_in_background = true;
	public static boolean enable_icon_effect = false;
	public static int mainmenu_page_effect_id = 2;
	public static int desktop_page_effect_id = 9;
	public static boolean display_widget_preview_hole = false;
	public static boolean default_S3_theme = true;
	public static boolean setupmenu_show_theme = true;
	public static String custom_wallpapers_path = "";
	public static String custom_default_wallpaper_name = "";
	public static boolean release_memory_after_pause = false;
	public static String custom_virtual_path = "";
	public static boolean useCustomVirtual = false;
	public static String custom_virtual_download_path = "";
	public static boolean useCustomVirtualDownload = false;
	public static int mainmenu_pos = 4;
	public static String custom_sys_shortcut_path = "";
	public static boolean useCustomSysShortcut = false;
	public static int app_icon_size = -1;
	public static float thirdapk_icon_scaleFactor = 1.0f;
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
	// public static boolean mainmenu_add_black_ground = false; //xiatian del
	// //Mainmenu Bg
	public static int popmenu_style =SetupMenu.POPMENU_STYLE_ORIGINAL;
	//public static int popmenu_style = SetupMenu.POPMENU_STYLE_ANDROID4;
	public static boolean click_indicator_enter_pageselect = true;
	public static boolean hotseat_title_no_background = false;
	public static boolean hotseat_icon_pos_fixed = false;
	public static boolean workspace_longclick_display_contacts = true;
	public static boolean enable_explorer = false;
	public static boolean show_home_button = true;
	public static boolean hide_desktop_setup = false;
	public static boolean mainmenu_widget_display_contacts = true;
	public static boolean enable_service = false;
	public static boolean show_widget_shortcut_bg = false;
	public static boolean widget_shortcut_lefttop = false;
	public static boolean hide_remove_theme_button = false;
	public static boolean hotseat_style_ex = true;
	public static boolean hotseat_hide_title = false;
	public static boolean widget_shortcut_title_top = false;
	public static boolean hide_home_button = false;
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
	public static String call_component_name = "com.android.contacts.DialtactsActivity";
	public static boolean title_style_bold = false;
	public static boolean disable_theme_preview_tween = false;
	public static boolean menu_wyd = false;
	public static boolean hotseatbar_no_panel = false;
	public static boolean pop_menu_focus_focus_effect = false;// zqh ,
	public static boolean keypad_event_of_focus = false; // zqh
	public static int install_apk_delay = 0;
	public static boolean empty_page_add_reminder = false;
	public static int reminder_font = 15;// dip
	public static boolean appbar_widgets_special_name = false;
	public static boolean disable_circled = false;
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
	public static boolean blend_func_dst_gl_one = false;
	public static boolean disable_move_wallpaper = false;
	public static boolean mainmenu_inout_no_anim = false;
	public static boolean folder_no_dragon = false;
	public static boolean show_icon_size = true;
	public static boolean ui_style_miui2 = false;
	public static float page_tween_time = 0.5f;
	public static HashMap<String, Integer> iconSizeMap = new HashMap<String, Integer>();

	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	public static boolean show_sensor = true;
	public static boolean default_open_sensor = true;
	public static int is_supported_sensor = -1;
	// xiatian add end

	/***************** added by zhenNan.ye begin *******************/
	public static boolean enable_particle = false;
	/***************** added by zhenNan.ye end *******************/

	// wanghongjian add start //enable_DefaultScene
	public static boolean free_scene_menu = false;// 场景桌面时是否允许有经典菜单入口，true表示允许，默认为false
	public static boolean scene_menu_style = false;// 判断手机中是否有场景桌面的安装，true表示有安装
	public static boolean isLong = false;// 判断场景桌面进入后applist是否允许长按，false表示允许
	public static boolean scene_main_menu = false;// 判断场景桌面菜单选项，默认为false
	public static boolean cooee_scene_style = false;// 设置是否进入场景桌面，false表示没进�?
	public static boolean scene_change = true;// 表示场景是否可以选择，true表示可以。默认为开true
	// wanghongjian add end

	// add zhujieping begin
//	miui_v5_folder： 小米V5风格的文件夹开关,默认为false
//	blur_enable: 是否打开模糊效果(miui_v5_folder开启，此开关有效)
//  fboScale: 新创建的Framebuffer的缩放比例,此参数理论上值越大越影响文件夹打开动画速度,720p打分辨率的一般设为0.2f,但是为了效果小分辨率的则可设置为0.5f(视具体机型性能而定)
//  blurInterate: 模糊迭代次数,决定模糊效果的平滑度(范围2-5,一般迭代2次就可以了,理论上分辨率越高值越大,模糊效果越好,但很耗时,需兼顾速度)
//  blurRadius: 模糊半径,决定整体模糊效果(范围3.0f-6.0f,此值受fboScale影响,fboScale越大则此值也相应可以越大,分辨率越高值越大,模糊效果越好,但很耗时,需兼顾速度)
//  blurDuration: 动画时间 (范围0.35f-0.55f,文件夹打开的动画时间,主要配合模糊效果,避免模糊效果因耗时而卡住动画帧)
	public static boolean miui_v5_folder = true;
	public static boolean blur_enable = true;
	public static float fboScale = 0.2f;
	public static int blurInterate = 2;
	public static int blurRadius = 6;
	public static float blurDuration = 0.4f;
	public static float maskOpacity = 1f;
	// add zhujieping end
	public static boolean external_applist_page_effect = false; // teapotXu add
																// for pages
																// effects in
																// Mainmenu

	public static boolean same_spacing_btw_hotseat_icons = true; // teapotXu add
																	// for
																	// hotSeatBar
																	// icons
																	// same
																	// Spacing

	// teapotXu add start for Folder in MainMenu
	public static boolean mainmenu_folder_function = false;

	public static boolean enhance_generate_mainmenu_folder_condition = true;

	// teapotXu add end for Folder in MainMenu

	public static boolean is_demo_version = false; // xiatian add
													// //is_demo_version

	public static boolean enable_texture_pack = true;

	// xiatian add start //Mainmenu Bg
	public static int defaultMainmenuBgIndex = 0;
	public static int lastAppListMainmenuBgIndex = -1;
	public static int lastMediaListMainmenuBgIndex = -1;
	// xiatian add end

	public static boolean enable_DesktopIndicatorScroll = false;

	public static float icon_shadow_radius = 5f; // xiatian add //add icon
													// shadow
	// added by ZhenNan.ye begin
	public static final String CUSTOM_XML_FILE_NAME = "specifiedIcon.xml";
	public static final String CUSTOM_XML_PREFERENCES_NAME = "specifiedIcon";
	public static final String CUSTOM_XML_KEY_EXIST = "xmlExist";
	public static final String CUSTOM_XML_KEY_OLD_VERSION = "oldVersion";
	public static final String PHONE_KEY_OLD_VERSION = "phoneOldVersion";
	public static final String PHONE_KEY_OLD_DISPLAY = "phoneOldDisplay";
	public static boolean needToSaveSpecifiedIconXml = true;
	public static boolean specifiedIconsLoaded;
	public static ArrayList<DefaultIcon> customDefaultIcon = new ArrayList<DefaultIcon>();
	// added by ZhenNan.ye end
	
	//zqh add ,this flag will enable option menu on idle to scroll page
	public static boolean setup_menu_support_scroll_page=true;

	public static String s4_modle_name[] = null;

	
	public DefaultLayout() {
		// Log.v("test", "11111");

		LoadDefaultLayoutXml();
		defLayout = this;
		setDefaultIconSize();

		initMainmenuBgListPreference(); // xiatian add //Mainmenu Bg

		/*
		 * 这里一定要做，保证Utilities中的函数initStatics 尽早调用�?这样sIconTextureWidth
		 * sIconTextureHeight就能够尽早和我们配置的app_icon_size相关绑定起来�?
		 */
		Utilities.initStatics(iLoongLauncher.getInstance());
	}

	private void setDefaultIconSize() {
		iconSizeMap.clear();
		iconSizeMap.put(
				"0",
				Integer.valueOf((int) iLoongLauncher.getInstance()
						.getResources().getDimension(RR.dimen.big)));
		iconSizeMap.put(
				"1",
				Integer.valueOf((int) iLoongLauncher.getInstance()
						.getResources().getDimension(RR.dimen.mid)));
		iconSizeMap.put(
				"2",
				Integer.valueOf((int) iLoongLauncher.getInstance()
						.getResources().getDimension(RR.dimen.small)));
		if (DefaultLayout.show_icon_size) {
			String icon_size_key = iLoongLauncher.getInstance().getResources()
					.getString(RR.string.icon_size_key);
			String icon_size_value = PreferenceManager
					.getDefaultSharedPreferences(SetupMenu.getContext())
					.getString(icon_size_key, "-1");
			if (icon_size_value.equals("-1")) {
				if (Utils3D.getScreenWidth() <= 480) {
					PreferenceManager
							.getDefaultSharedPreferences(SetupMenu.getContext())
							.edit().putString(icon_size_key, "1").commit();
					app_icon_size = iconSizeMap.get("1");
				} else {
					PreferenceManager
							.getDefaultSharedPreferences(SetupMenu.getContext())
							.edit().putString(icon_size_key, "0").commit();
					app_icon_size = iconSizeMap.get("0");
				}

			} else {
				app_icon_size = iconSizeMap.get(icon_size_value);
			}
		} else {
			if (DefaultLayout.app_icon_size != -1) {
				float density = iLoongLauncher.getInstance().getResources()
						.getDisplayMetrics().density;

				app_icon_size = (int) (density * DefaultLayout.app_icon_size / 1.5f);

				app_icon_size = DefaultLayout.pxRoundToOurValue(app_icon_size);
			} else {

				app_icon_size = (int) iLoongLauncher.getInstance()
						.getResources()
						.getDimension(android.R.dimen.app_icon_size);
			}
		}
	}

	public static int getMMSettingValue() {
		if (mmData[2] == null)
			return 0;

		if (!mmData[2].equals("default")) /* check package name */
			return 1;
		else
			return 0;
	}

	public static int startKeyAction() {
		int res = 1;

		if (mmData[2] == null)
			return 0;

		if (mmData[2].equals("default")) /* check package name */
			return 0;/* default process */

		String title = mmData[0];
		String pkgName = mmData[2];
		String componentName = mmData[4];

		if (checkApkExist(iLoongLauncher.getInstance(), pkgName)) {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(pkgName, componentName));
			iLoongLauncher.getInstance().startActivity(intent);
		} else {
			// WidgetDownload.downloadWithoutCheckVersion(title, ApkName,
			// pkgName,
			// customID);
			if (DLManager.getInstance().HaveDownLoad(pkgName)) {
				WidgetDownload.showInfo(
						null,
						title + iLoongLauncher.languageSpace
								+ R3D.getString(RR.string.app_downloading));
				return 1;
			}
			Widget3DManager.curDownload = null;
			SendMsgToAndroid.sendDownloadWidgetMsg();
		}

		return res;
	}

	public static String getmmSettingTitle() {
		return mmData[0];
	}

	public static void startDownLoadmmSetting() {
		String title = mmData[0];
		String ApkName = mmData[1];
		String pkgName = mmData[2];
		String customID = mmData[3];

		WidgetDownload.downloadWithoutCheckVersion(title, ApkName, pkgName,
				customID);
	}

	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
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
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);

		} catch (NameNotFoundException e) {
			// e.printStackTrace();
		}

		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	public void setApplist(List<ResolveInfo> app) {
		Log.v("test", "----------------setApplist");
		allApp = app;
	}

	public static DefaultLayout getInstance() {
		return defLayout;
	}

	public static int GetTotalDefaultWidgetNumber() {
		if (allWidget == null)
			return 0;
		return allWidget.size();
	}

	public static String GetDefaultWidgetName(int i) {
		if (allWidget == null)
			return "";
		return allWidget.get(i).name;
	}

	public static String GetDefaultWidgetName(String packageName) {
		if (allWidgetFinal == null)
			return "";
		for (int i = 0; i < allWidgetFinal.size(); i++) {
			if (allWidgetFinal.get(i).pkgName.equals(packageName)) {
				return allWidgetFinal.get(i).name;
			}
		}
		return "";
	}

	public static int GetDefaultWidgetMinWidth(int i) {
		if (allWidget == null)
			return 1;
		return allWidget.get(i).minWidth;
	}

	public static int GetDefaultWidgetMinHeight(int i) {
		if (allWidget == null)
			return 1;
		return allWidget.get(i).minHeight;
	}

	public static int GetDefaultWidgetHSpan(int i) {
		if (allWidget == null)
			return 0;
		return allWidget.get(i).spanX;
	}

	public static int GetDefaultWidgetVSpan(int i) {
		if (allWidget == null)
			return 0;
		return allWidget.get(i).spanY;
	}

	public static int GetDefaultWidgetHSpan(String packageName) {
		if (allWidgetFinal == null)
			return 0;
		for (int i = 0; i < allWidgetFinal.size(); i++) {
			if (allWidgetFinal.get(i).pkgName.equals(packageName)) {
				return allWidgetFinal.get(i).spanX;
			}
		}
		return 0;
	}

	public static int GetDefaultWidgetVSpan(String packageName) {
		if (allWidgetFinal == null)
			return 0;
		for (int i = 0; i < allWidgetFinal.size(); i++) {
			if (allWidgetFinal.get(i).pkgName.equals(packageName)) {
				return allWidgetFinal.get(i).spanY;
			}
		}
		return 0;
	}

	public static String GetDefaultWidgetPkgname(int i) {
		if (allWidget == null)
			return "";
		return allWidget.get(i).pkgName;
	}

	public static void RemoveDefWidgetWithPkgname(String pkgName) {
		if (allWidget == null)
			return;

		for (int i = 0; i < allWidget.size(); i++) {
			if (allWidget.get(i).pkgName.equals(pkgName)) {
				allWidget.remove(i);
				break;
			}
		}
		removeDefWidgetView(pkgName);
	}

	public static void installWidget(ResolveInfo widget3d, String pkgName) {
		if (allWidget == null)
			return;
		WidgetView wdgView;
		ShortcutInfo info;
		for (int i = 0; i < widView.size(); i++) {
			wdgView = widView.get(i);
			info = (ShortcutInfo) wdgView.getItemInfo();
			if (info.intent.getComponent().getPackageName().equals(pkgName)) {
				if (widget3d != null)
					Widget3DManager.getInstance().installWidget(widget3d);
				else {
					Root3D.deleteFromDB(wdgView.getItemInfo());
					wdgView.remove();
					widView.remove(i);
				}
				return;
			}
		}
		for (int i = 0; i < allWidget.size(); i++) {
			if (allWidget.get(i).pkgName.equals(pkgName)) {
				allWidget.get(i).hasInstall = true;
				if (widget3d != null)
					allWidget.get(i).resolveInfo = widget3d;
				break;
			}
		}
	}

	public static String GetDefaultWidgetImage(int i) {
		if (allWidget == null)
			return "";
		return allWidget.get(i).imageName;
	}

	public static String GetDefaultWidgetImage(String pkgName) {
		if (allWidgetFinal == null)
			return "";

		String imageName = null;

		for (int i = 0; i < allWidgetFinal.size(); i++) {
			if (allWidgetFinal.get(i).pkgName.equals(pkgName)) {
				imageName = allWidgetFinal.get(i).imageName;
				return imageName;
			}
		}
		return null;
	}

	public static String GetDefaultWidgetApkname(String pkgName,
			String className) {
		if (allWidgetFinal == null)
			return null;

		String apkName = null;

		for (int i = 0; i < allWidgetFinal.size(); i++) {
			if (allWidgetFinal.get(i).pkgName.equals(pkgName)) {
				apkName = allWidgetFinal.get(i).apkName;
				break;
			}
		}

		/* 下载时两个都需要检 */
		if (apkName == null) {
			for (int i = 0; i < allVirture.size(); i++) {
				if (allVirture.get(i).className != null && className != null) {
					if (allVirture.get(i).pkgName.equals(pkgName)
							&& allVirture.get(i).className.equals(className)) {
						apkName = allVirture.get(i).apkName;
						break;
					}
				} else {
					if (allVirture.get(i).pkgName.equals(pkgName)) {
						apkName = allVirture.get(i).apkName;
						break;
					}
				}
			}
		}

		return apkName;
	}

	public static boolean GetDefaultWidgetFromAirpush(String pkgName,
			String className) {
		if (allWidgetFinal == null)
			return false;

		boolean fromAirpush = false;

		for (int i = 0; i < allVirture.size(); i++) {
			if (allVirture.get(i).className != null && className != null) {
				if (allVirture.get(i).pkgName.equals(pkgName)
						&& allVirture.get(i).className.equals(className)) {
					fromAirpush = allVirture.get(i).fromAirPush;
					break;
				}
			} else {
				if (allVirture.get(i).pkgName.equals(pkgName)) {
					fromAirpush = allVirture.get(i).fromAirPush;
					break;
				}
			}
		}

		return fromAirpush;
	}

	public static String GetDefaultWidgetCustomID(String pkgName) {
		if (allWidgetFinal == null)
			return null;

		String customID = null;

		for (int i = 0; i < allWidgetFinal.size(); i++) {
			if (allWidgetFinal.get(i).pkgName.equals(pkgName)) {
				customID = allWidgetFinal.get(i).customID;
				break;
			}
		}

		return customID;
	}

	public static String GetVirtureCustomID(String pkgName) {
		if (allVirture == null)
			return null;

		String customID = null;

		for (int i = 0; i < allVirture.size(); i++) {
			if (allVirture.get(i).pkgName.equals(pkgName)) {
				customID = allVirture.get(i).customID;
				break;
			}
		}

		return customID;
	}

	public static String GetVirtureCustomID(String pkgName, String className) {
		if (allVirture == null)
			return null;
		String customID = null;

		for (int i = 0; i < allVirture.size(); i++) {
			if (allVirture.get(i).className != null && className != null) {
				if (allVirture.get(i).pkgName.equals(pkgName)
						&& allVirture.get(i).className.equals(className)) {
					customID = allVirture.get(i).customID;
					break;
				}
			} else {
				if (allVirture.get(i).pkgName.equals(pkgName)) {
					customID = allVirture.get(i).customID;
					break;
				}
			}
		}

		return customID;
	}

	public static String GetDefaultWidgetNameWithPkgName(String pkgName) {
		if (allWidget == null)
			return null;

		String name = null;

		for (int i = 0; i < allWidget.size(); i++) {
			if (allWidget.get(i).pkgName.equals(pkgName)) {
				name = allWidget.get(i).name;
				break;
			}
		}

		return name;
	}

	public static String GetDefaultVirtureNameWithPkgName(String pkgName) {
		if (allVirture == null)
			return null;

		String name = null;

		for (int i = 0; i < allVirture.size(); i++) {
			if (allVirture.get(i).pkgName.equals(pkgName)) {
				name = allVirture.get(i).name;
				break;
			}
		}

		return name;
	}

	public static String GetDefaultVirtureNameWithPkgClassName(String pkgName,
			String className) {
		if (allVirture == null)
			return null;
		String name = null;

		for (int i = 0; i < allVirture.size(); i++) {
			if (allVirture.get(i).className != null && className != null) {
				if (allVirture.get(i).pkgName.equals(pkgName)
						&& allVirture.get(i).className.equals(className)) {
					name = allVirture.get(i).name;
					break;
				}
			} else {
				if (allVirture.get(i).pkgName.equals(pkgName)) {
					name = allVirture.get(i).name;
					break;
				}
			}
		}

		return name;
	}

	public static String GetDefaultWidgetImageWithNameInDesktop(String name) {
		if (allWidget == null)
			return null;

		String image_name = null;

		for (int i = 0; i < allWidget.size(); i++) {
			if (allWidget.get(i).name.equals(name)) {
				image_name = THEME_WIDGET_FOLDER;
				image_name += allWidget.get(i).imageName;
				break;
			}
		}

		return image_name;
	}

	public static String GetDefaultWidgetImageWithNameInDesktop(int i) {
		if (allWidget == null)
			return null;
		String image_name = THEME_WIDGET_APPLIST;
		image_name += allWidget.get(i).imageName;
		return image_name;
	}

	public static boolean isWidgetLoadByInternal(ShortcutInfo info) {
		boolean loadByInternal = false;
		if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW) {
			for (int i = 0; i < DefaultLayout.allWidgetFinal.size(); i++) {
				if (DefaultLayout.allWidgetFinal.get(i).pkgName
						.equals(info.intent.getComponent().getPackageName())) {
					loadByInternal = true;
					break;
				}
			}
		}
		return loadByInternal;
	}

	public static String getWidgetItemClassName(String packageName) {
		String className = "";
		for (int i = 0; i < DefaultLayout.allWidgetFinal.size(); i++) {
			if (DefaultLayout.allWidgetFinal.get(i).pkgName.equals(packageName)
					&& DefaultLayout.allWidgetFinal.get(i).loadByInternal) {
				className = DefaultLayout.allWidgetFinal.get(i).className;
				break;
			}
		}
		return className;
	}

	public static boolean isWidgetLoadByInternal(String packageName) {
		boolean loadByInternal = false;
		for (int i = 0; i < DefaultLayout.allWidgetFinal.size(); i++) {
			if (DefaultLayout.allWidgetFinal.get(i).pkgName.equals(packageName)
					&& DefaultLayout.allWidgetFinal.get(i).loadByInternal) {
				loadByInternal = true;
				break;
			}
		}
		return loadByInternal;
	}

	public static void setIconCache(IconCache cache) {
		iconCache = cache;
	}

	public static void setEnv(Root3D rootPara, Workspace3D workspacePara) {
		Log.v("test", "setEnv");
		root = rootPara;
		workspace = workspacePara;

		sideBar = root.getHotSeatBar();
	}

	public static boolean hasInstallWidget(String pkgName) {
		if (allWidget == null)
			return false;

		for (int i = 0; i < allWidget.size(); i++) {
			Log.d("launcher", "isWidgetInstalled:" + allWidget.get(i).pkgName);
			if (allWidget.get(i).pkgName.equals(pkgName)) {
				return allWidget.get(i).hasInstall;
			}
		}
		return false;
	}

	public static ResolveInfo getInstallWidgetResolveInfo(String pkgName) {
		if (allWidget == null)
			return null;

		for (int i = 0; i < allWidget.size(); i++) {
			Log.d("launcher", "getInstallWidgetResolveInfo:"
					+ allWidget.get(i).pkgName);
			if (allWidget.get(i).pkgName.equals(pkgName)) {
				return allWidget.get(i).resolveInfo;
			}
		}
		return null;
	}

	public static String GetDefaultImageWithPkgName(String pkgName) {
		if (allWidget == null)
			return null;

		for (int i = 0; i < allWidget.size(); i++) {
			Log.d("launcher", allWidget.get(i).pkgName);
			if (allWidget.get(i).pkgName.equals(pkgName)) {
				return THEME_WIDGET_FOLDER + allWidget.get(i).imageName;
			}
		}

		Log.d("launcher", "no default image");
		return null;
	}

	public static String GetVirtureImageWithPkgClassName(String pkgName,
			String className) {
		if (allVirture == null)
			return null;
		for (int i = 0; i < allVirture.size(); i++) {
			if (allVirture.get(i).className != null && className != null) {
				if (allVirture.get(i).pkgName.equals(pkgName)
						&& allVirture.get(i).className.equals(className)) {
					if (DefaultLayout.useCustomVirtual)
						return DefaultLayout.custom_virtual_path + "/"
								+ allVirture.get(i).imageName;
					return THEME_VIRTURE_FOLDER + allVirture.get(i).imageName;
				}
			} else {
				if (allVirture.get(i).pkgName.equals(pkgName)) {
					if (DefaultLayout.useCustomVirtual)
						return DefaultLayout.custom_virtual_path + "/"
								+ allVirture.get(i).imageName;
					return THEME_VIRTURE_FOLDER + allVirture.get(i).imageName;
				}
			}
		}   

		return null;
	}

	public static String pickNameByLanSetting(String name) {
		String res = null;
		String tmp = null;
		int curLan = iLoongLauncher.curLanguage;

		if (name == null)
			return null;

		ArrayList<String> nameList = new ArrayList<String>();

		GetShortcutItemString(nameList, name);

		if (nameList.size() < curLan + 1)
			return nameList.get(0);

		tmp = nameList.get(curLan);

		if (tmp != null)
			res = tmp;
		else
			res = nameList.get(0);/* if not exit return zh_SM */

		return res;
	}

	public static void GetShortcutItemString(ArrayList<String> stringArray,
			String allName) {
		if (allName == null)
			return;

		int length = allName.length();
		int start = 0;
		int end = 0;
		String part = allName;
		String leftName = allName;
		int limit = ';'; /* delimiter */

		stringArray.clear();

		if (length <= 0)
			return;

		while (leftName != null) {
			end = leftName.indexOf(limit);

			if (end == -1) {
				part = leftName;
			} else
				part = leftName.substring(start, end);

			// Log.v("test", "part string = <<" + part + ">>");

			if (end == -1)
				leftName = null;
			else
				leftName = leftName.substring(end + 1);

			if (part != null)
				stringArray.add(part);
		}
	}

	void MakeIconForItem(ResolveInfo info, ShortcutItem item) {
		// ShortcutInfo shortcutInfo = null;

		/*
		 * if (item.imageName != null && !item.imageName.isEmpty()) { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); } else { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); }
		 */

		item.info = new ApplicationInfo(info, iLoongApplication.mIconCache)
				.makeShortcut();

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
	public static int pxRoundToOurValue(int pxValue) {
		int retVal = pxValue;
		retVal = Tools.px2dip(iLoongLauncher.getInstance(), pxValue);
		retVal = Tools.dip2px(iLoongLauncher.getInstance(), retVal);
		return retVal;
	}

	void changeIcon(ResolveInfo info, String image) {
		if (image == null || info == null)
			return;
		Bitmap bitmap = GetPKGThemeIconPath(image);
		// boolean bNeedScale=false;
		if (bitmap != null)

		{

			// if
			// (pxRoundToOurValue(bitmap.getWidth())!=DefaultLayout.app_icon_size
			// && DefaultLayout.thirdapk_icon_scaleFactor!=1.0f)
			// {
			// bNeedScale=true;
			// }
			bitmap = Utilities.resampleIconBitmap(bitmap,
					iLoongLauncher.getInstance());

			// if (bNeedScale)
			// {
			// bitmap=Tools.resizeBitmap(bitmap,
			// DefaultLayout.thirdapk_icon_scaleFactor);
			// }
			iconCache.setIcon(info, bitmap);
		}

	}

	void MakeIconForVirtual(ResolveInfo info, VirtureIcon virIcon) {
		// ShortcutInfo shortcutInfo = null;

		/*
		 * if (item.imageName != null && !item.imageName.isEmpty()) { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); } else { iconbmp =
		 * GetPKGThemeIconPath(temp.get(j)); }
		 */

		virIcon.info = new ApplicationInfo(info, iLoongApplication.mIconCache)
				.makeShortcut();

		// item.icon = new
		// Icon3D(shortcutInfo.title.toString(),shortcutInfo.getIcon(iconCache),shortcutInfo.title.toString());
		// virIcon.icon = new
		// Icon3D(shortcutInfo.title.toString(),R3D.findRegion(shortcutInfo));
		// virIcon.icon.setItemInfo(shortcutInfo);
	}

	int checkAppType(String pname) {
		try {
			PackageInfo pInfo = iLoongLauncher.getInstance()
					.getPackageManager().getPackageInfo(pname, 0);
			// 是系统软件或者是系统软件更新
			if (isSystemApp(pInfo) || isSystemUpdateApp(pInfo)) {
				return 1; /* system app */
			} else {
				return 0;/* user app */
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public boolean isSystemApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0);
	}

	public boolean isSystemUpdateApp(PackageInfo pInfo) {
		return ((pInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}

	public boolean isUserApp(PackageInfo pInfo) {
		return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
	}

	public void setDefaultIcon() {
		int i = 0;
		int size = defaultIcon.size();

		if (size < 1)
			return;

		for (i = 0; i < size; i++) {
			if (!defaultIcon.get(i).dealed) {
				defaultIcon.get(i).pkgNameArray = new ArrayList<String>();
				GetShortcutItemString(defaultIcon.get(i).pkgNameArray,
						defaultIcon.get(i).pkgName);
			}
		}
		for (i = 0; i < size; i++) {
			if (!defaultIcon.get(i).dealed) {
				defaultIcon.get(i).classNameArray = new ArrayList<String>();
				GetShortcutItemString(defaultIcon.get(i).classNameArray,
						defaultIcon.get(i).className);
			}
		}

		// for (i = 0; i < defaultIcon.size(); i++) {
		// if (!defaultIcon.get(i).dealed) {
		// Log.v("test", "package name ="
		// + defaultIcon.get(i).pkgNameArray.get(0)
		// + "is not found");
		// }
		// }
	}        
           
	public void getDefaultIcon(ResolveInfo info) {
		ArrayList<String> allNamePatten;
		String packageName = info.activityInfo.applicationInfo.packageName;
		DefaultIcon tem = new DefaultIcon();	// added by zhenNan.ye
		// Log.e("getDefaultIcon",
		// "packageName:"+packageName+" className:"+info.activityInfo.name);
		// if (checkAppType(appName) == 1) {/* 内置应用 */
		for (int inter = 0; inter < defaultIcon.size(); inter++) {
			// if (defaultIcon.get(inter).dealed)
			// continue;

			allNamePatten = defaultIcon.get(inter).pkgNameArray;

			if (allNamePatten == null || allNamePatten.size() <= 0)
				continue;
  
			for (int k = 0; k < allNamePatten.size(); k++) {
				if (packageName.equals(allNamePatten.get(k))) {
					if (defaultIcon.get(inter).duplicate) {
						ArrayList<String> allCompNames = defaultIcon.get(inter).classNameArray;
						for (int m = 0; m < allCompNames.size(); m++) {
							if (info.activityInfo.name.equals(allCompNames
									.get(m))) {
								// defaultIcon.get(inter).dealed = true;
								changeIcon(info,
										defaultIcon.get(inter).imageName);
								// added by zhenNan.ye begin
								if (needToSaveSpecifiedIconXml) {
									tem.title = defaultIcon.get(inter).title;
									tem.duplicate = defaultIcon.get(inter).duplicate;
									if (packageName.equals("com.coco.launcher")) {
										tem.pkgName = defaultIcon.get(inter).pkgName;
									} else {
										tem.pkgName = packageName;
									}
									tem.className = allCompNames.get(m);
									tem.imageName = defaultIcon.get(inter).imageName;
									customDefaultIcon.add(tem);
								}	
								return;	   
								// added by zhenNan.ye end								
							}
						}

					} else {
						// defaultIcon.get(inter).dealed = true;
						changeIcon(info, defaultIcon.get(inter).imageName);
						// added by zhenNan.ye begin
						if (needToSaveSpecifiedIconXml) {
							tem.title = defaultIcon.get(inter).title;
							if (packageName.equals("com.coco.launcher")) {
								tem.pkgName = defaultIcon.get(inter).pkgName;
							} else {
								tem.pkgName = packageName;
							}
							tem.imageName = defaultIcon.get(inter).imageName;
							customDefaultIcon.add(tem);   
						}
						return;
						// added by zhenNan.ye end
					}
				}				   
			}   
		}
		// }
	}

	public boolean hasReplaceIcon(String appName, String compName) {
		boolean bRet = false;
		ArrayList<String> allNamePatten;
		for (int inter = 0; inter < defaultIcon.size(); inter++) {
			allNamePatten = defaultIcon.get(inter).pkgNameArray;

			if (allNamePatten == null || allNamePatten.size() <= 0)
				continue;
			for (int k = 0; k < allNamePatten.size(); k++) {
				if (appName.equals(allNamePatten.get(k))) {
					if (defaultIcon.get(inter).duplicate) {
						ArrayList<String> allCompNames = defaultIcon.get(inter).classNameArray;
						for (int m = 0; m < allCompNames.size(); m++) {
							if (compName.equals(allCompNames.get(m))) {
								Bitmap bmp = GetPKGThemeIconPath(defaultIcon
										.get(inter).imageName);
								if (bmp != null) {
									return true;
								} else {
									return false;
								}
							}
						}
					} else {
						Bitmap bmp = GetPKGThemeIconPath(defaultIcon.get(inter).imageName);
						if (bmp != null) {
							return true;
						} else {
							return false;
						}
					}

				}
			}
		}
		// }

		return bRet;

	}

	// xiatian add start
	// fix bug:when a icon in replace list,but a theme do not have this icon.in
	// the theme this icon use default Theme icon and not have iconBg and not
	// scale
	public String getReplaceIconPath(String appName, String compName) {
		ArrayList<String> allNamePatten;
		for (int inter = 0; inter < defaultIcon.size(); inter++) {
			allNamePatten = defaultIcon.get(inter).pkgNameArray;

			if (allNamePatten == null || allNamePatten.size() <= 0)
				continue;

			for (int k = 0; k < allNamePatten.size(); k++) {
				if (appName.equals(allNamePatten.get(k))) {
					if (defaultIcon.get(inter).duplicate) {
						ArrayList<String> allCompNames = defaultIcon.get(inter).classNameArray;
						for (int m = 0; m < allCompNames.size(); m++) {
							if (compName.equals(allCompNames.get(m))) {
								return (THEME_ICON_FOLDER + THEME_NAME + defaultIcon
										.get(inter).imageName);
							}
						}
					} else {
						return (THEME_ICON_FOLDER + THEME_NAME + defaultIcon
								.get(inter).imageName);
					}
				}
			}
		}

		return null;
	}

	// xiatian add end

	public static String getDefaultVirtureImage(String packageName) {
		if (packageName == null)
			return null;
		ArrayList<String> allNamePatten;
		DefaultIcon tem = new DefaultIcon();	// added by zhenNan.ye
		for (int inter = 0; inter < defaultIcon.size(); inter++) {
			allNamePatten = defaultIcon.get(inter).pkgNameArray;

			if (allNamePatten == null || allNamePatten.size() <= 0)
				continue;

			for (int k = 0; k < allNamePatten.size(); k++) {
				if (packageName.equals(allNamePatten.get(k))) {
					
					// added by zhenNan.ye begin
					if (needToSaveSpecifiedIconXml) {
						tem.title = defaultIcon.get(inter).title;
						tem.duplicate = defaultIcon.get(inter).duplicate;
						tem.pkgName = defaultIcon.get(inter).pkgName;
						tem.className = defaultIcon.get(inter).className;
						tem.imageName = defaultIcon.get(inter).imageName;
						customDefaultIcon.add(tem);  
					}	
					// added by zhenNan.ye end
					
					return THEME_ICON_FOLDER + THEME_NAME
							+ defaultIcon.get(inter).imageName;
				}
			}
		}
		return null;
	}

	public void GetFactoryAppInfo() {
		if (show_default_app_sort) {
			for (int i = 0; i < facApp.size(); i++) {
				facApp.get(i).pkgNameArray = new ArrayList<String>();
				GetShortcutItemString(facApp.get(i).pkgNameArray,
						facApp.get(i).packageName);
			}
		}
	}

	public void GetAllViewInfo() {
		ArrayList<ShortcutItem> temp = new ArrayList<ShortcutItem>();
		int i, j, k = 0;
		int total = 0;
		int remove = 0;

		for (i = 0; i < allShortcutList.size(); i++) {
			if (allShortcutList.get(i).shortcutList != null
					&& allShortcutList.get(i).shortcutList.size() > 0) {
				for (j = 0; j < allShortcutList.get(i).shortcutList.size(); j++) {
					temp.add(total, allShortcutList.get(i).shortcutList.get(j));
					total++;
				}
			}

			if (allShortcutList.get(i).folderList != null
					&& allShortcutList.get(i).folderList.size() > 0) {
				for (k = 0; k < allShortcutList.get(i).folderList.size(); k++) {
					if (allShortcutList.get(i).folderList.get(k).shortcutList != null
							&& allShortcutList.get(i).folderList.get(k).shortcutList
									.size() > 0) {
						for (j = 0; j < allShortcutList.get(i).folderList
								.get(k).shortcutList.size(); j++) {
							temp.add(total, allShortcutList.get(i).folderList
									.get(k).shortcutList.get(j));
							total++;
						}
					}
				}
			}
		}

		/* init string array */
		for (i = 0; i < temp.size(); i++) {
			if (!temp.get(i).isContact && temp.get(i).info == null) {
				temp.get(i).pkgNameArray = new ArrayList<String>();
				GetShortcutItemString(temp.get(i).pkgNameArray,
						temp.get(i).pkgName);

				temp.get(i).classNameArray = new ArrayList<String>();
				GetShortcutItemString(temp.get(i).classNameArray,
						temp.get(i).className);
			}
		}

		/* init shortcut info */
		for (i = 0; i < allApp.size(); i++) {
			ArrayList<String> packageNamePatten;
			ResolveInfo info = allApp.get(i);

			String packageName = info.activityInfo.applicationInfo.packageName;
			String className = info.activityInfo.name;

			for (int indexVir = 0; indexVir < allVirture.size(); indexVir++) {
				VirtureIcon virIcon = allVirture.get(indexVir);
				if (virIcon.info != null)
					continue;

				if (virIcon.className != null) {
					if (packageName.equals(virIcon.pkgName)
							&& className.equals(virIcon.className)) {
						MakeIconForVirtual(info, virIcon);
					}
				} else {
					if (packageName.equals(virIcon.pkgName)) {
						MakeIconForVirtual(info, virIcon);
					}
				}
			}

			App: for (j = 0; j < temp.size(); j++) {
				String part = null;
				ShortcutItem item = temp.get(j);

				if (item.isContact && item.info == null) {
					item.info = Contact3DShortcut.addContactInfo();
				}

				if (item.info != null)
					continue;

				packageNamePatten = item.pkgNameArray;

				if (packageNamePatten.size() <= 0)
					continue;

				for (k = 0; k < packageNamePatten.size(); k++) {
					if (packageNamePatten.get(k).charAt(0) == '*') {
						part = packageNamePatten.get(k).substring(1);
						if (packageName.lastIndexOf(part) != -1) {
							MakeIconForItem(info, item);
							// break App;
						}
					} else {
						if (packageName.equals(packageNamePatten.get(k))) {
							if (item.classNameArray.size() > 0) {
								int count = item.classNameArray.size();
								for (int cmp = 0; cmp < count; cmp++) {
									if (item.classNameArray.get(cmp).equals(
											className)) {
										MakeIconForItem(info, item);
										// break App;
									}
								}
							} else {
								MakeIconForItem(info, item);
								// break App;
							}
						}
					}
				}
			}
		}

		/* remove items never be found */
		for (i = 0; i < allShortcutList.size(); i++) {
			if (allShortcutList.get(i).shortcutList != null
					&& allShortcutList.get(i).shortcutList.size() > 0) {
				for (j = 0; j < allShortcutList.get(i).shortcutList.size(); j++) {
					// temp.add(total,
					// allShortcutList.get(i).shortcutList.get(j));
					if (allShortcutList.get(i).shortcutList.get(j).info == null) {
						allShortcutList.get(i).shortcutList.remove(j);
						remove++;
					}
				}
			}

			if (allShortcutList.get(i).folderList != null
					&& allShortcutList.get(i).folderList.size() > 0) {
				for (k = 0; k < allShortcutList.get(i).folderList.size(); k++) {
					if (allShortcutList.get(i).folderList.get(k).shortcutList != null
							&& allShortcutList.get(i).folderList.get(k).shortcutList
									.size() > 0) {
						for (j = 0; j < allShortcutList.get(i).folderList
								.get(k).shortcutList.size(); j++) {
							temp.add(total, allShortcutList.get(i).folderList
									.get(k).shortcutList.get(j));
							if (allShortcutList.get(i).folderList.get(k).shortcutList
									.get(j).info == null) {
								allShortcutList.get(i).folderList.get(k).shortcutList
										.remove(j);
								remove++;
							}
						}
					}
				}
			}
		}

		Log.v("test", "totla item = " + total + " remove = " + remove);
	}

	public void InsertAllItem() {
		// Log.v("test", "----------------InsertAllItem");
		Log.d("launcher", "insert all item");
		// Icon3D.init();
		GetAllViewInfo();

		// Log.v("test", "----------------1111");
		InsertDefaultShortcut();
		// Log.v("test", "----------------2222");
		InsertDefaultWidget();

		InsertVirtureIcon();
		// Log.v("test", "----------------end InsertAllItem");

		InsertHotSeatIcon();

		if (iLoongApplication.BuiltIn)
			InsertSysWidget();

		InsertSysShortcut();

		SharedPreferences prefs = iLoongLauncher.getInstance()
				.getSharedPreferences("launcher", Activity.MODE_PRIVATE);
		prefs.edit().putBoolean("first_run", false).commit();
	}

	void InsertShortCut(ShortcutGRP grp) {
		int i = 0;

		if (grp.locate != null && grp.locate.equals("sidebar")) {
			if (grp.shortcutList.size() > 0) {
				// ArrayList<View3D> list = new ArrayList<View3D>();

				for (i = 0; i < grp.shortcutList.size(); i++) {
					if (grp.shortcutList.get(i).info != null) {
						// grp.shortcutList.get(i).info.container =
						// LauncherSettings.Favorites.CONTAINER_SIDEBAR;
						if (DefConfig.DEF_NEW_SIDEBAR == true) {
							grp.shortcutList.get(i).info.angle = HotSeat3D.TYPE_ICON;
							Root3D.addOrMoveDB(
									grp.shortcutList.get(i).info,
									LauncherSettings.Favorites.CONTAINER_HOTSEAT);
						} else {
							Root3D.addOrMoveDB(
									grp.shortcutList.get(i).info,
									LauncherSettings.Favorites.CONTAINER_SIDEBAR);
						}
					}
				}
				// root.getSidebar().onDrop(list, 0, 0);
			}
		} else if (grp.locate != null && grp.locate.equals("workspace")) {/* workspace */
			if (grp.shortcutList != null && grp.shortcutList.size() > 0) {
				ItemInfo info = null;
				int iconWidth = 0;
				int width = Utils3D.getScreenWidth() / grp.shortcutList.size();
				if (grp.shortcutList.get(0).info != null)
					iconWidth = R3D.workspace_cell_width;

				if (width < iconWidth) {
					iconWidth = width;
				}

				for (i = 0; i < grp.shortcutList.size(); i++) {
					ShortcutItem item = grp.shortcutList.get(i);

					if (item.info != null) {
						info = item.info;
						info.x = (int) ((width - iconWidth) / 2) + i * width;
						info.y = R3D.def_layout_y;
						info.screen = grp.value;
						info.cellX = item.cellX;
						info.cellY = item.cellY;

						Root3D.addOrMoveDB(info);
					}
				}
			}
			if (grp.folderList != null && grp.folderList.size() > 0) {
				for (i = 0; i < grp.folderList.size(); i++)
					InsertFolder(grp.folderList.get(i), grp.value, i,
							grp.folderList.size());
			}
		} else if (grp.locate != null && grp.locate.equals("applist")) {/* applist */
			if (grp.folderList != null && grp.folderList.size() > 0) {
				for (i = 0; i < grp.folderList.size(); i++)
					InsertAppListFolder(grp.folderList.get(i), i,
							grp.folderList.size());
			}
		}
	}

	void InsertFolder(FolderList folder, int screen, int index, int total) {
		int i = 0;
		ItemInfo info = null;

		// folderinfo fo = (FolderIcon3D)
		// SidebarMainGroup.folder3DHost.getWidget3D();
		UserFolderInfo fo = iLoongLauncher.getInstance().addFolder(folder.name);

		int width = Utils3D.getScreenWidth() / 4;
		int folderWidth = R3D.sidebar_widget_w;

		info = fo;
		info.x = (int) ((width - folderWidth) / 2) + index * width;
		info.y = R3D.def_layout_y;
		info.cellX = folder.cellX;
		info.cellY = folder.cellY;
		info.cellTempX = folder.cellX;
		info.cellTempY = folder.cellY;
		info.screen = screen;

		LauncherModel.updateItemInDatabase(iLoongLauncher.getInstance(), fo);
		Root3D.addOrMoveDB(info);

		if (folder.shortcutList != null)
			for (i = 0; i < folder.shortcutList.size(); i++) {
				if (folder.shortcutList.get(i).info != null) {
					Root3D.addOrMoveDB(folder.shortcutList.get(i).info, fo.id);
				}
			}

		allFolder.put(folder, fo);
	}

	void InsertAppListFolder(FolderList folder, int index, int total) {
		int i = 0;

		// folderinfo fo = (FolderIcon3D)
		// SidebarMainGroup.folder3DHost.getWidget3D();
		UserFolderInfo fo = iLoongLauncher.getInstance().addFolder(folder.name);
		// fo.container = LauncherSettings.Favorites.CONTAINER_APPLIST;
		// Root3D.updateItemInDatabase(fo);
		Root3D.addOrMoveDB(fo, LauncherSettings.Favorites.CONTAINER_APPLIST);

		if (folder.shortcutList != null) {
			for (i = 0; i < folder.shortcutList.size(); i++) {
				if (folder.shortcutList.get(i).info != null) {
					Root3D.addOrMoveDB(folder.shortcutList.get(i).info, fo.id);
				}
			}
		}

		allFolder.put(folder, fo);
	}

	public void InsertSysWidget() {
		Context context = iLoongLauncher.getInstance();

		Log.v("test", "allSysWidget size = " + allSysWidget.size());
		for (int i = 0; i < allSysWidget.size(); i++) {
			SysWidget widget = allSysWidget.get(i);
			if (widget.packageName == null || widget.className == null) {
				continue;
			}

			boolean hasPackage = true;
			ComponentName cn = new ComponentName(widget.packageName,
					widget.className);
			PackageManager packageManager = context.getPackageManager();
			try {
				packageManager.getReceiverInfo(cn, 0);
			} catch (Exception e) {
				String[] packages = packageManager
						.currentToCanonicalPackageNames(new String[] { widget.packageName });
				cn = new ComponentName(packages[0], widget.className);
				try {
					packageManager.getReceiverInfo(cn, 0);
				} catch (Exception e1) {
					hasPackage = false;
				}
			}

			if (hasPackage) {
				final AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);

				try {
					int appWidgetId = iLoongLauncher.getInstance().mAppWidgetHost
							.allocateAppWidgetId();
					Widget2DInfo appWidgetInfo = new Widget2DInfo(appWidgetId);
					appWidgetInfo.setInfo(widget.packageName, widget.className);
					appWidgetInfo.cellX = widget.cellX;
					appWidgetInfo.cellY = widget.cellY;
					appWidgetInfo.spanX = widget.spanX;
					appWidgetInfo.spanY = widget.spanY;
					appWidgetInfo.screen = widget.screen;
					LauncherModel
							.addSysWidgetInDatabase(context, appWidgetInfo);
					appWidgetManager.bindAppWidgetId(appWidgetId, cn);
				} catch (RuntimeException ex) {
					Log.e("launcher", "Problem allocating appWidgetId", ex);
				}
			} else
				continue;
		}
	}

	public void InsertSysShortcut() {
		Context context = iLoongLauncher.getInstance();

		Log.v("test", "allSysShortcut size = " + allSysShortcut.size());
		for (int i = 0; i < allSysShortcut.size(); i++) {
			SysShortcut shortcut = allSysShortcut.get(i);
			if (shortcut.packageName == null || shortcut.className == null) {
				continue;
			}

			boolean hasPackage = true;
			ComponentName cn = new ComponentName(shortcut.packageName,
					shortcut.className);
			PackageManager packageManager = context.getPackageManager();
			try {
				packageManager.getActivityInfo(cn, 0);
			} catch (Exception e) {
				String[] packages = packageManager
						.currentToCanonicalPackageNames(new String[] { shortcut.packageName });
				cn = new ComponentName(packages[0], shortcut.className);
				try {
					packageManager.getActivityInfo(cn, 0);
				} catch (Exception e1) {
					hasPackage = false;
				}
			}

			if (hasPackage) {
				Bitmap bitmap;
				Bitmap icon = null;
				boolean customIcon = false;
				ShortcutIconResource iconResource = null;

				InputStream is = null;
				String imgname;
				if (DefaultLayout.useCustomSysShortcut) {
					imgname = DefaultLayout.custom_sys_shortcut_path + "/"
							+ shortcut.imageName;
					try {
						is = new FileInputStream(imgname);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					imgname = THEME_SYS_SHORTCUT_FOLDER + shortcut.imageName;
					is = ThemeManager.getInstance().getInputStream(imgname);
				}
				bitmap = BitmapFactory.decodeStream(is);
				// float density =
				// iLoongLauncher.getInstance().getResources().getDisplayMetrics().density;
				// bitmap=Tools.resizeBitmap(bitmap, density / 1.5f);
				if (bitmap != null && bitmap instanceof Bitmap) {
					icon = Utilities.createIconBitmap(new BitmapDrawable(
							(Bitmap) bitmap), context);
					((Bitmap) bitmap).recycle();
					customIcon = true;
				}

				Intent intent = new Intent();
				intent.setClassName(shortcut.packageName, shortcut.className);

				final ShortcutInfo info = new ShortcutInfo();
				// int icon_size = Utilities.sIconTextureWidth;//(int)
				// iLoongLauncher.getInstance().getResources().getDimension(android.R.dimen.app_icon_size);
				// Tools.resizeBitmap(icon, icon_size, icon_size);
				info.setIcon(icon);
				info.title = shortcut.name;
				info.intent = intent;
				info.customIcon = customIcon;
				info.iconResource = iconResource;

				if (icon != null) {
					R3D.pack(info, shortcut.name, Utils3D.IconToPixmap3D(icon,
							info.title.toString(), Icon3D.getIconBg(),
							Icon3D.titleBg, false), false);
				} else
					R3D.pack(info, shortcut.name);

				info.x = Utils3D.getScreenWidth() / 2;
				info.y = Utils3D.getScreenHeight() / 2;
				info.screen = shortcut.screen;
				info.cellX = shortcut.cellX;
				info.cellY = shortcut.cellY;
				if (iLoongLauncher.getInstance() != null) {
					// iLoongLauncher.getInstance().getD3dListener().addShortcutFromDrop(info);
					// LauncherModel
					// .addSysShortcutInDatabase(context, info);
					Root3D.addOrMoveDB(info);
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
			} else
				continue;
		}
	}

	void InsertDefaultShortcut() {

		Log.v("test", "allShortcutList size = " + allShortcutList.size());
		for (int i = 0; i < allShortcutList.size(); i++) {
			// Log.v("test", "----------------i = " + i);
			InsertShortCut(allShortcutList.get(i));
		}      
	}

	public static boolean DefaultWidgetProcess(ItemInfo info) {
		boolean res = false;

		ShortcutInfo shrtInfo = (ShortcutInfo) info;

		String imagePath = GetDefaultImageWithPkgName(shrtInfo.intent
				.getComponent().getPackageName());

		if (imagePath == null)
			return res;

		WidgetView widgetView = new WidgetView((String) info.title,
				new BitmapTexture(BitmapFactory.decodeStream(Gdx.files
						.internal(imagePath).read())));
		widgetView.setItemInfo(shrtInfo);

		aliveDefWidget.add(widgetView);

		res = true;
		return res;
	}

	public static ArrayList<WidgetIcon> virtureView = new ArrayList<WidgetIcon>();

	public static ArrayList<WidgetView> widView = new ArrayList<WidgetView>();

	public static boolean needAddWidget(String pkgName) {
		WidgetView wdgView;
		ShortcutInfo info;
		Log.d("launcher", "needAddWidget");
		for (int i = 0; i < widView.size(); i++) {
			wdgView = widView.get(i);
			info = (ShortcutInfo) wdgView.getItemInfo();

			if (info.intent.getComponent().getPackageName()
					.equals("iLoongMemo.apk"))
				continue;

			if (info.intent.getComponent().getPackageName().equals(pkgName))
				return true;
		}

		return false;
	}

	public static void addWidgetView(Widget3D wdg, String pkgName) {
		WidgetView wdgView;
		ShortcutInfo info;
		Log.d("launcher", "addWidgetView");
		for (int i = 0; i < widView.size(); i++) {
			wdgView = widView.get(i);

			info = (ShortcutInfo) wdgView.getItemInfo();

			if (info.intent.getComponent().getPackageName().equals(pkgName)) {
				Widget3DInfo wdgInfo = wdg.getItemInfo();

				wdgInfo.x = (int) wdgView.x
						- ((int) wdg.width - (int) wdgView.width) / 2;
				wdgInfo.y = (int) wdgView.y;
				wdgInfo.screen = info.screen;

				wdgInfo.cellX = info.cellX;
				wdgInfo.cellY = info.cellY;

				Root3D.deleteFromDB(widView.get(i).getItemInfo());
				widView.get(i).remove();
				widView.remove(i);

				// Root3D.addOrMoveDB(wdgInfo);

				workspace.addInScreen(wdg, wdgInfo.screen, wdgInfo.x,
						wdgInfo.y, false);

			}
		}
	}

	public static void removeDefWidgetView(String pkgName) {
		Log.d("launcher", "removeDefWidgetView");
		WidgetView wdgView;
		ShortcutInfo info;

		for (int i = 0; i < widView.size(); i++) {
			wdgView = widView.get(i);

			info = (ShortcutInfo) wdgView.getItemInfo();

			if (info.intent.getComponent().getPackageName().equals(pkgName)) {
				Root3D.deleteFromDB(widView.get(i).getItemInfo());
				widView.get(i).remove();
				widView.remove(i);
			}
		}
	}

	public static void onDropToTrash(View3D view) {
		boolean haveView = false;
		int i = 0;
		if ((view instanceof IconBase3D) == false) {
			return;
		}

		ItemInfo info = ((IconBase3D) view).getItemInfo();

		if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW) {
			for (; i < widView.size(); i++) {
				if (widView.get(i) == view) {
					haveView = true;
					break;
				}
			}

			if (haveView) {
				widView.get(i).remove();
				widView.remove(i);
			}

		} else if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW) {
			for (; i < virtureView.size(); i++) {
				if (virtureView.get(i) == view) {
					haveView = true;
					break;
				}
			}

			if (haveView) {
				virtureView.get(i).remove();
				virtureView.remove(i);
			}
		}
	}

	public static WidgetItem getWidgetItem(String packageName) {
		for (int i = 0; i < allWidget.size(); i++) {
			if (allWidget.get(i).pkgName.equals(packageName)) {
				return allWidget.get(i);
			}
		}
		return null;
	}

	/*
	 * need set name
	 */
	public static View3D showDefaultWidgetView(ShortcutInfo info) {
		String imagePath;

		if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW) {
			WidgetItem widgetItem = getWidgetItem(info.intent.getComponent()
					.getPackageName());
			if(widgetItem == null)return null;
			if (widgetItem.loadByInternal) {
				try {
					Widget3D widget3D = Widget3DManager.getInstance()
							.getWidget3D(widgetItem.pkgName,
									widgetItem.className);
					if (widget3D != null) {
						Widget3DInfo wdgInfo = widget3D.getItemInfo();
						wdgInfo.intent = info.intent;
						wdgInfo.x = info.x;
						wdgInfo.y = info.y;
						wdgInfo.screen = info.screen;
						wdgInfo.cellX = info.cellX;
						wdgInfo.cellY = info.cellY;
						wdgInfo.spanX = GetDefaultWidgetHSpan(info.intent
								.getComponent().getPackageName());
						wdgInfo.spanY = GetDefaultWidgetVSpan(info.intent
								.getComponent().getPackageName());
						workspace.addInScreen(widget3D, wdgInfo.screen,
								wdgInfo.x, wdgInfo.y, false);
						Root3D.deleteFromDB(info);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (hasInstallWidget(info.intent.getComponent()
						.getPackageName())) {
					ResolveInfo resolveInfo = getInstallWidgetResolveInfo(info.intent
							.getComponent().getPackageName());
					if (resolveInfo == null)
						return null;
					Root3D.deleteFromDB(info);
					Widget3D wdg = Widget3DManager.getInstance().getWidget3D(
							resolveInfo);
					Widget3DInfo wdgInfo = wdg.getItemInfo();
					wdgInfo.x = info.x;
					wdgInfo.y = info.y;
					wdgInfo.screen = info.screen;
					wdgInfo.cellX = info.cellX;
					wdgInfo.cellY = info.cellY;
					wdgInfo.spanX = GetDefaultWidgetHSpan(info.intent
							.getComponent().getPackageName());
					wdgInfo.spanY = GetDefaultWidgetVSpan(info.intent
							.getComponent().getPackageName());

					workspace.addInScreen(wdg, wdgInfo.screen, wdgInfo.x,
							wdgInfo.y, false);
				} else {
					WidgetView widgetView;
					imagePath = GetDefaultImageWithPkgName(info.intent
							.getComponent().getPackageName());
					if (imagePath == null)
						return null;
					Bitmap bmp = ThemeManager.getInstance().getBitmap(imagePath);
					if(bmp == null)return null;
					widgetView = new WidgetView((String) info.title,new BitmapTexture(bmp));
					bmp.recycle();
					bmp = null;
					if (info.intent.getComponent().getPackageName()
							.equals("com.iLoong.Clock"))
						widgetView.setSize(
								widgetView.width * Utils3D.getDensity(),
								widgetView.height * Utils3D.getDensity());
					else
						widgetView
								.setSize(
										widgetView.width * Utils3D.getDensity()
												/ 1.5f, widgetView.height
												* Utils3D.getDensity() / 1.5f);
					Log.d("launcher", "old span:"
							+ info.intent.getComponent().getPackageName() + ","
							+ info.spanX + "," + info.spanY);
					info.spanX = GetDefaultWidgetHSpan(info.intent
							.getComponent().getPackageName());
					info.spanY = GetDefaultWidgetVSpan(info.intent
							.getComponent().getPackageName());
					Log.d("launcher", "new span:"
							+ info.intent.getComponent().getPackageName() + ","
							+ info.spanX + "," + info.spanY);
					widgetView.setItemInfo(info);
					widView.add(widgetView);
					workspace.addInScreen(widgetView, info.screen, info.x,
							info.y, false);
					if (hasInstallWidget(info.intent.getComponent()
							.getPackageName())) {
						ResolveInfo resolveInfo = getInstallWidgetResolveInfo(info.intent
								.getComponent().getPackageName());
						if (resolveInfo != null) {
							Widget3DManager.getInstance().installWidget(
									resolveInfo);
						}
					}
				}
			}
		} else {
			WidgetIcon widgetIcon;
			InputStream is = null;
			imagePath = getDefaultVirtureImage(info.intent.getComponent()
					.getPackageName());
			boolean scale = false;
			if (imagePath == null) {
				scale = true;
				imagePath = GetVirtureImageWithPkgClassName(info.intent
						.getComponent().getPackageName(), info.intent
						.getComponent().getClassName());
				if (DefaultLayout.useCustomVirtual) {
					try {
						is = new FileInputStream(imagePath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					// FileHandle file = ThemeManager.getInstance()
					// .getGdxTextureResource(imagePath);
					is = ThemeManager.getInstance().getInputStream(imagePath);
				}
			} else {
				// FileHandle file = ThemeManager.getInstance()
				// .getGdxTextureResource(imagePath);
				is = ThemeManager.getInstance().getInputStream(imagePath);

				scale = !(ThemeManager.getInstance().loadFromTheme(imagePath)); // xiatian
																				// add
																				// //fix
																				// bug:when
																				// a
																				// icon
																				// in
																				// replace
																				// list,but
																				// a
																				// theme
																				// do
																				// not
																				// have
																				// this
																				// icon.in
																				// the
																				// theme
																				// this
																				// icon
																				// use
																				// default
																				// Theme
																				// icon
																				// and
																				// not
																				// have
																				// iconBg
																				// and
																				// not
																				// scale

			}

			Bitmap bmp = ThemeManager.getInstance().getBitmap(is);

			// if (!AppHost3D.V2)
			// bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w - 10,
			// R3D.sidebar_widget_h - 10, true);
			// bmp.setDensity(160);
			int iconSize = DefaultLayout.app_icon_size;
			if (thirdapk_icon_scaleFactor != 1.0f
					&& !R3D.doNotNeedScale(info.intent.getComponent()
							.getPackageName(), info.intent.getComponent()
							.getClassName()) && scale) {
				iconSize *= DefaultLayout.thirdapk_icon_scaleFactor;
			}
			BitmapDrawable drawable = new BitmapDrawable(bmp);
			drawable.setTargetDensity(iLoongLauncher.getInstance()
					.getResources().getDisplayMetrics());
			bmp = Utilities.createIconBitmap(drawable,
					iLoongLauncher.getInstance(), iconSize);
			// teapotXu add start:
			Bitmap bg = null;
			if (iconSize != DefaultLayout.app_icon_size)
				bg = Icon3D.getIconBg();
			IconToTexture3D texture = new IconToTexture3D(bmp,
					(String) info.title, bg, Icon3D.titleBg);
			widgetIcon = new WidgetIcon((String) info.title, texture);
			// teapotXu add end
			widgetIcon.setItemInfo(info);
			bmp.recycle();
			bmp = null;

			virtureView.add(widgetIcon);

			if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
				workspace.addInScreen(widgetIcon, info.screen, info.x, info.y,
						false);

			} else if (info.container == LauncherSettings.Favorites.CONTAINER_SIDEBAR
					|| info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT)
				return widgetIcon;
			else
				return widgetIcon;
		}

		return null;
	}

	public static void onAddApp(ApplicationInfo appInfo) {
		WidgetIcon wdgIcon;
		ShortcutInfo info;
		Icon3D icon;

		for (int i = 0; i < virtureView.size(); i++) {
			wdgIcon = virtureView.get(i);
			info = (ShortcutInfo) wdgIcon.getItemInfo();

			if (info.intent.getComponent().getPackageName()
					.equals(appInfo.intent.getComponent().getPackageName())) {
				ShortcutInfo temp = appInfo.makeShortcut();

				temp.x = info.x;
				temp.y = info.y;
				// temp.container = info.container;
				temp.screen = info.screen;

				icon = new Icon3D(temp.title.toString(), R3D.findRegion(temp));
				icon.setItemInfo(temp);

				if (info.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
					Root3D.deleteFromDB(info);
					workspace.removeViewInWorkspace(wdgIcon);

					workspace.addInScreen(icon, temp.screen, temp.x, temp.y,
							false);
				} else if (info.container == LauncherSettings.Favorites.CONTAINER_SIDEBAR
						|| info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
					final ViewGroup3D mainGroup = sideBar.getMainGroup();
					final ViewGroup3D dockGroup = sideBar.getDockGroup();

					Utils3D.changeTextureRegion(icon,
							Utils3D.getIconBmpHeight(), true);

					if (info.angle == HotSeat3D.TYPE_ICON) {
						temp.angle = HotSeat3D.TYPE_ICON;
						((HotSeatMainGroup) mainGroup).removeItem(wdgIcon);
						((HotSeatMainGroup) mainGroup).bindItem(icon);
					} else {
						temp.angle = HotSeat3D.TYPE_WIDGET;
						((HotDockGroup) dockGroup).removeItem(wdgIcon);
						((HotDockGroup) dockGroup).bindItem(icon);
					}

					Root3D.addOrMoveDB(temp,
							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
					Root3D.deleteFromDB(info);
				} else {
					// UserFolderInfo fo =
					// iLoongApplication.getInstance().mModel.getFolderInfoWithContainerID(info.container);
					// fo.remove(info);
					// fo.add(temp);
					// Root3D.addOrMoveDB(temp, fo.id);
					// Root3D.addOrMoveDB(info, fo.id);
					FolderIcon3D folderIcon = (FolderIcon3D) wdgIcon.getTag2();
					folderIcon.updateIcon(wdgIcon, icon);
				}

				wdgIcon.remove();
				// virtureView.remove(i);
			}
		}
	}

	public static boolean isAVirtureIcon(String pkgName) {
		boolean res = false;
		ShortcutInfo info = null;

		if (virtureView == null || pkgName == null)
			return res;

		for (int i = 0; i < virtureView.size(); i++) {
			info = (ShortcutInfo) virtureView.get(i).info;
			if (pkgName.equals(info.intent.getComponent().getPackageName())) {
				return true;
			}
		}

		return res;
	}

	void InsertDefaultWidget() {
		int width = Utils3D.getScreenWidth();
		int height = Utils3D.getScreenHeight();

		for (int i = 0; i < allWidget.size(); i++) {
			WidgetItem widget;

			widget = allWidget.get(i);

			if (!widget.addDesktop)
				continue;

			ShortcutInfo info = new ShortcutInfo();
			info.title = widget.name;
			info.intent = new Intent(Intent.ACTION_PACKAGE_INSTALL);
			info.intent.setComponent(new ComponentName(widget.pkgName,
					widget.pkgName));
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

			Log.d("launcher", "insert:" + info.title);
			Root3D.addOrMoveDB(info);
		}
	}

	void InsertHotSeatIcon() {

		if (DefConfig.DEF_NEW_SIDEBAR == false) {
			return;
		} else {
			ShortcutItem item;
			Icon3D icon;
			for (int i = 0; i < iLoongLauncher.getInstance().getHotSeatLength(); i++) {
				// item = allHotSeatList.get(i);
				icon = new Icon3D("test");
				ShortcutInfo info = new ShortcutInfo();
				// item.info = info;
				info.title = iLoongLauncher.getInstance().getHotSeatString(i);
				if (info.title == null) {
					// info.title=item.name;
				}

				Intent findIntent = null;

				findIntent = iLoongLauncher.getInstance().getHotSeatIntent(i);
				info.angle = HotSeat3D.TYPE_WIDGET;
				// info.setIcon(GetBmpFromImageName(item.imageName));
				info.screen = i;
				if (findIntent != null) {
					info.intent = findIntent;
					icon.setItemInfo(info);
					// list.add((View3D) (icon));
					Root3D.addOrMoveDB(info,
							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}

			}

		}

	}

	public static String getAppListVirtualIconName(int index) {
		return allAppListVirtualIcon.get(index).name;
	}

	public static String getAppListVirtualIconTitle(int index) {
		return allAppListVirtualIcon.get(index).title;
	}

	public static String getAppListVirtualIconPkgName(int index) {
		return allAppListVirtualIcon.get(index).packageName;
	}

	public static String getAppListVirtualIconImageName(int index) {
		return allAppListVirtualIcon.get(index).imageName;
	}

	// public Bitmap GetBmpFromImageName(String imageName) {
	// Bitmap image;
	// String imagePath;
	// if (imageName == null) {
	// return null;
	// }
	// imagePath = HOTSEAT_PATH + imageName;
	// image = ThemeManager.getInstance().getBitmap(imagePath);
	// float density = iLoongApplication.ctx.getResources()
	// .getDisplayMetrics().density;
	// image = Tools.resizeBitmap(image, density / 1.5f);
	// return image;
	// }

	void InsertVirtureIcon() {
		VirtureIcon virIcon;
		ShortcutInfo info;
		UserFolderInfo fo;

		Log.v("test", "allVirture size = " + allVirture.size());
		HashMap<UserFolderInfo, Integer> folderIconNum = new HashMap<UserFolderInfo, Integer>();/*
																								 * 记录由默认列表添加的icon
																								 * 文件夹中添加的icon数量
																								 * ，
																								 * 防止超过最
																								 * �
																								 * ?
																								 * 只针对虚图标的处
																								 * �
																								 * ?
																								 */

		for (int i = 0; i < allVirture.size(); i++) {
			virIcon = allVirture.get(i);

			if (virIcon.folder != null)
				fo = allFolder.get(virIcon.folder);
			else
				fo = null;

			if (fo != null) {
				if (!folderIconNum.containsKey(fo))
					folderIconNum.put(fo, 0);
				if (folderIconNum.get(fo) >= R3D.folder_max_num)
					continue;
				else
					folderIconNum.put(fo, folderIconNum.get(fo) + 1);
			}

			if (virIcon.info == null) {
				info = new ShortcutInfo();

				info.x = virIcon.x;// (Utils3D.getScreenWidth() -
									// R3D.sidebar_widget_w) / 2;
				info.y = virIcon.y;// /R3D.def_layout_y + R3D.def_layout_y_dura;

				info.title = virIcon.name;

				info.intent = new Intent(Intent.ACTION_PACKAGE_INSTALL);
				if (virIcon.className != null) {
					info.intent.setComponent(new ComponentName(virIcon.pkgName,
							virIcon.className));
				} else {
					info.intent.setComponent(new ComponentName(virIcon.pkgName,
							virIcon.pkgName));
				}
				info.itemType = LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW;
				info.spanX = 1;
				info.spanY = 1;
				info.cellX = virIcon.cellX;
				info.cellY = virIcon.cellY;

				if (fo == null) {
					info.screen = virIcon.value;
					Root3D.addOrMoveDB(info);
				} else
					Root3D.addOrMoveDB(info, fo.id);
			} else {
				ItemInfo itemInfo = virIcon.info;
				itemInfo.x = virIcon.x;
				itemInfo.y = virIcon.y;
				itemInfo.spanX = 1;
				itemInfo.spanY = 1;
				itemInfo.cellX = virIcon.cellX;
				itemInfo.cellY = virIcon.cellY;

				if (fo == null) {
					itemInfo.screen = virIcon.value;
					Root3D.addOrMoveDB(itemInfo);
				} else
					Root3D.addOrMoveDB(itemInfo, fo.id);
			}
		}
	}

	public Bitmap GetPKGThemeIconPath(String imageName) {
		Bitmap image = null;
		String imagePath;

		if (imageName == null)
			return null;
		imagePath = THEME_ICON_FOLDER + THEME_NAME + imageName;

		// image = Tools.getImageFromInStream(DefaultLayout.class
		// .getResourceAsStream(imagePath));

		image = ThemeManager.getInstance().getBitmap(imagePath);
		return image;
	}

	final private void LoadDefaultLayoutXml() {
		// Utils3D.showPidMemoryInfo("default");
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();

			DefaultLayoutHandler handler = new DefaultLayoutHandler();
			handler.parseThemeConfig = false;
			xmlreader.setContentHandler(handler);
			InputSource xmlin;
			
			// added by ZhenNan.ye begin	
			// 加载用户手机私有目录data/data/com.coco.launcher/files/下的xml文件
			if (isCustomXmlExist()) {
				InputStream inputStream = iLoongLauncher.getInstance()
						.openFileInput(CUSTOM_XML_FILE_NAME);  
				if (inputStream != null) {
					needToSaveSpecifiedIconXml = false;
					xmlin = new InputSource(inputStream);   
					parser = factoey.newSAXParser();
					xmlreader = parser.getXMLReader();
					xmlreader.setContentHandler(handler);
					xmlreader.parse(xmlin);
					
					specifiedIconsLoaded = true;
				} else {
					needToSaveSpecifiedIconXml = true;
					showProgressDialog();
				}
			} else {
				showProgressDialog();
			}
			// added by ZhenNan.ye end
			
			File f = new File(CUSTOM_FIRST_DEFAULT_LAYOUT_FILENAME);
			if (!f.exists()) {
				f = new File(CUSTOM_DEFAULT_LAYOUT_FILENAME);
				if (!f.exists()) {
					f = new File(CUSTOM_FIRST_DEFAULT_LAYOUT_FILENAME_PUBLIC);
					if (!f.exists()) {
						f = new File(CUSTOM_DEFAULT_LAYOUT_FILENAME_PUBLIC);
					}
				}   
			}
			if (iLoongApplication.BuiltIn && f.exists()) {
				xmlin = new InputSource(
						new FileInputStream(f.getAbsolutePath()));
			} else {
				xmlin = new InputSource(iLoongLauncher.getInstance()
						.getAssets().open(DEFAULT_LAYOUT_FILENAME));
			}

			xmlreader.parse(xmlin);
			
			// removed by zhenNan.ye 
//			boolean parseTheme = true;
			// 加载服务器default_layout
//			if (enable_air_default_layout) {
//				try {
//					File file = new File(
//							Environment.getExternalStorageDirectory()
//									+ AirDefaultLayout.FILE);
//					InputStream is = new FileInputStream(file);
//					if (is != null) {
//						handler.parseThemeConfig = true;
//						xmlin = new InputSource(is);
//						parser = factoey.newSAXParser();
//						xmlreader = parser.getXMLReader();
//						xmlreader.setContentHandler(handler);
//						xmlreader.parse(xmlin);
//						handler.parseThemeConfig = false;
//						parseTheme = false;
//					}
//				} catch (Exception e) {
//				}
//			}
			// 加载主题个性化配置
			if (!ThemeManager.getInstance().currentThemeIsSystemTheme()) {
				InputStream is = ThemeManager.getInstance()
						.getCurrentThemeInputStream(DEFAULT_LAYOUT_FILENAME);
				if (is != null) {
					// added by ZhenNan.ye
					specifiedIconsLoaded = false;
					
					handler.parseThemeConfig = true;
					xmlin = new InputSource(is);
					parser = factoey.newSAXParser();
					xmlreader = parser.getXMLReader();
					xmlreader.setContentHandler(handler);
					xmlreader.parse(xmlin);
					handler.parseThemeConfig = false;
				}
			}

			handler = null;
			xmlin = null;
			// Utils3D.showPidMemoryInfo("default2");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// xiatian add start //Mainmenu Bg
	private void initMainmenuBgListPreference() {
		String mainmenu_bg_key = iLoongLauncher.getInstance().getResources()
				.getString(RR.string.mainmenu_bg_key);
		String mainmenu_bg_value = PreferenceManager
				.getDefaultSharedPreferences(SetupMenu.getContext()).getString(
						mainmenu_bg_key, "-1");
		if (mainmenu_bg_value.equals("-1")) {
			PreferenceManager
					.getDefaultSharedPreferences(SetupMenu.getContext()).edit()
					.putString(mainmenu_bg_key, defaultMainmenuBgIndex + "")
					.commit();
			lastAppListMainmenuBgIndex = defaultMainmenuBgIndex;
			lastMediaListMainmenuBgIndex = defaultMainmenuBgIndex;
		} else {
			PreferenceManager
					.getDefaultSharedPreferences(SetupMenu.getContext()).edit()
					.putString(mainmenu_bg_key, mainmenu_bg_value).commit();
			lastAppListMainmenuBgIndex = Integer.valueOf(mainmenu_bg_value)
					.intValue();
			lastMediaListMainmenuBgIndex = Integer.valueOf(mainmenu_bg_value)
					.intValue();
		}
	}
	// xiatian add end

	//zhongqihong add
	
	public String[] getDefaultPckCls(String name){
	    String info[]=new String [2];

	    
	    for(DefaultIcon icon : DefaultLayout.customDefaultIcon){
	       
	        if(icon.title.equals(name)){
	            info[0]=icon.pkgName;
	            info[1]=icon.className;
	        }
	    }
	    return info;
	}

	//zhongqihong end
	// added by zhenNan.ye
	// the specifiedIcon.xml file is saved at "data/data/com.coco.launcher/files/"
	public void saveSpecifiedIconToXml() {
		final String TAG_ICON = "icon";
		final String TAG_NAME = "name";
		final String TAG_DUPLICATE = "dup";
		final String TAG_PKGNAME = "pkgname";
		final String TAG_COMPONENTNAME = "componentName";
		final String TAG_IMAGE = "image";
		
		try {						
			OutputStream os = iLoongLauncher.getInstance().openFileOutput(CUSTOM_XML_FILE_NAME, 
					Activity.MODE_APPEND);
			
			XmlSerializer serializer = Xml.newSerializer();
			serializer.setOutput(os, "UTF-8");
			serializer.startDocument(null, null);   
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			serializer.startTag(null, DefaultLayoutHandler.TAG_DEFAULT_LAYOUT);
			serializer.attribute(null, "ver", "1.0");
			serializer.startTag(null, DefaultLayoutHandler.GENERAl_CONFIG);
			serializer.attribute(null, "anti_aliasing", "true");
			serializer.endTag(null, DefaultLayoutHandler.GENERAl_CONFIG);
			
			for (DefaultIcon icon : customDefaultIcon) {   
				serializer.startTag(null, TAG_ICON);
				serializer.attribute(null, TAG_NAME, icon.title);
				if (icon.duplicate) {
					serializer.attribute(null, TAG_DUPLICATE, Boolean.toString(icon.duplicate));
				}
				serializer.attribute(null, TAG_PKGNAME, icon.pkgName);
				if(icon.className != null) {
					serializer.attribute(null, TAG_COMPONENTNAME, icon.className);
				}
				serializer.attribute(null, TAG_IMAGE, icon.imageName);
				serializer.endTag(null, TAG_ICON);
			}
			
			serializer.endTag(null, DefaultLayoutHandler.TAG_DEFAULT_LAYOUT);
			serializer.endDocument();
			serializer.flush();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		SharedPreferences pre = iLoongLauncher.getInstance()
				.getSharedPreferences(CUSTOM_XML_PREFERENCES_NAME,
						Activity.MODE_PRIVATE);
		pre.edit().putBoolean(CUSTOM_XML_KEY_EXIST, true).commit();
	}
	
	// added by zhenNan.ye
	private boolean isCustomXmlExist() {
		SharedPreferences preferences = iLoongLauncher.getInstance()
				.getSharedPreferences(CUSTOM_XML_PREFERENCES_NAME,
						Activity.MODE_PRIVATE);
		boolean customXmlExist = preferences.getBoolean(CUSTOM_XML_KEY_EXIST, false);
		int oldVersion = preferences.getInt(CUSTOM_XML_KEY_OLD_VERSION, -1);
		if (oldVersion == -1) {
			try {
				oldVersion = iLoongApplication.ctx.getPackageManager()
						.getPackageInfo(
								iLoongApplication.ctx.getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			if (oldVersion != -1) {
				preferences.edit().putInt(CUSTOM_XML_KEY_OLD_VERSION, oldVersion).commit();
			}
		} else {
			int curVersion = -1;
			try {
				curVersion = iLoongApplication.ctx.getPackageManager()
						.getPackageInfo(
								iLoongApplication.ctx.getPackageName(), 0).versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();  
			}
			
			if (curVersion > oldVersion) {
				Log.i("DefaultLayout", "CoCoLauncher is update!");
				iLoongLauncher.getInstance().deleteFile(CUSTOM_XML_FILE_NAME);
				preferences.edit().putBoolean(CUSTOM_XML_KEY_EXIST, false).commit();
				customXmlExist = false;
			}
		}
		
		if(isPhoneUpdate()) {
			Log.i("DefaultLayout", "phone is update!");
			iLoongLauncher.getInstance().deleteFile(CUSTOM_XML_FILE_NAME);
			preferences.edit().putBoolean(CUSTOM_XML_KEY_EXIST, false).commit();
			customXmlExist = false;
		}
		
		return customXmlExist;
	}
	  
	// added by zhenNan.ye
	public static boolean isPhoneUpdate() {
		String oldVersion = null;
		String oldDisplay = null;
		
		SharedPreferences preferences = iLoongLauncher.getInstance()
				.getSharedPreferences(CUSTOM_XML_PREFERENCES_NAME,
						Activity.MODE_PRIVATE);
		oldVersion = preferences.getString(PHONE_KEY_OLD_VERSION, null);
		oldDisplay = preferences.getString(PHONE_KEY_OLD_DISPLAY, null);
		
		if (oldVersion != null && oldDisplay != null) {
			if (!oldVersion.equals(android.os.Build.VERSION.RELEASE)
					|| !oldDisplay.equals(android.os.Build.DISPLAY)) {
				return true;
			} else {
				return false;
			}
		}
   
		if (oldVersion == null) {
			oldVersion = android.os.Build.VERSION.RELEASE;
			preferences.edit().putString(PHONE_KEY_OLD_VERSION, oldVersion).commit();
		}
		
		if (oldDisplay == null) {
			oldDisplay = android.os.Build.DISPLAY;
			preferences.edit().putString(PHONE_KEY_OLD_DISPLAY, oldDisplay).commit();
		}

		return false;
	}
	
	
	private LoadingDialog mDialogView;
	private Dialog mProgressDialog;
	public void showProgressDialog() {		
		mDialogView = new LoadingDialog(iLoongLauncher.getInstance());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
        		LayoutParams.MATCH_PARENT);
		mProgressDialog = new Dialog(iLoongLauncher.getInstance(), RR.style.Dialog_FullScreen);
		mProgressDialog.setContentView(mDialogView, params);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}  
	
	public void cancelProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.cancel();
		}
		
		if (mDialogView != null) {
			mDialogView.destory();
			mDialogView = null;
		}
	}
}
