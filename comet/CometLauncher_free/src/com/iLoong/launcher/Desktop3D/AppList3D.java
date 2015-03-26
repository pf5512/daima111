package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL11;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.cut;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DVirtual;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class AppList3D extends NPageBase implements MenuActionListener , DragSource3D , ClingManager.ClingTarget
{
	
	public static boolean inited = false;
	private static int mCellCountX = 4;
	private static int mCellCountY = 5;
	public static int mWidgetCountX = 2;
	public static int mWidgetCountY = 3;
	public ArrayList<ApplicationInfo> mApps;
	public ArrayList<WidgetShortcutInfo> mWidgets;
	public List<View3D> mWidget3DList;
	private GridPool appGridPool;
	private GridPool widgetGridPool;
	public HashMap<String , AppIcon3D> iconMap;
	private HashMap<String , Widget2DShortcut> widget2DMap;
	private int[] sortArray;
	public static int appPageCount = 0;
	public static int widget3DPageCount = 0;
	public static int widget2DPageCount = 0;
	public static int widgetPageCount = 0;
	private IconCache iconCache;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	private ArrayList<View3D> selectedObjects = new ArrayList<View3D>();
	public int mode = APPLIST_MODE_NORMAL;
	public int sortId = -1;
	public boolean saveType = true;
	public int normalType;
	public static final int SORT_INSTALL = 0;
	public static final int SORT_NAME = 1;
	public static final int SORT_USE = 2;
	public static final int SORT_FACTORY = 3;
	public static final int SORT_DEFAULT = 4;
	public static final int APP_LIST3D_KEY_BACK = 0;
	public static final int APP_LIST3D_SHOW = 1;
	public static final int APP_LIST3D_HIDE = 2;
	public static final int APPLIST_MODE_UNINSTALL = 0;
	public static final int APPLIST_MODE_HIDE = 1;
	public static final int APPLIST_MODE_NORMAL = 2;
	public static final int APPLIST_MODE_USERAPP = 3;
	private int clingState = ClingManager.CLING_STATE_WAIT;
	// public static final int MSG_START_DRAG = 0;
	Mesh mesh = null;
	private AppBar3D appBar;
	public static NinePatch translucentBg;
	public boolean mScrollToWidget = true;
	public boolean mHideMainmenuWidget = false;
	public static int currentCell = 0; // current cell focused on
	private float locationX = 0;
	private float locationY = 0;
	private float iconWidth = 0;
	private float iconHeight = 0;
	private boolean firstlyCome = true;// zqh18
	private boolean hideFocus = true;// zqh18
	private NinePatch iconFocus = new NinePatch( R3D.findRegion( "icon_focus" ) , 20 , 20 , 20 , 20 );
	public boolean toastLoad = true;
	// teapotXu add start for Folder in Mainmenu
	public ArrayList<ItemInfo> mItemInfos;
	private final static int ICON_DROP_GEN_FOLDER_STAY_TIMER = 150;
	private final static String ICON_MAP_DOWNLOAD_APP_LABEL = "download_";
	private final static String ICON_MAP_FOLDER_LABEL = "folderId: ";
	private HashMap<String , FolderIcon3D> folderInfoMap;
	private UserFolderInfo newFolderInfo = null;
	private ArrayList<View3D> onDropList = null;
	public boolean force_applist_refesh = false;
	public boolean is_maimenufolder_open = false;
	public FolderIcon3D mOpenFolderIcon = null;
	public boolean syncAppAnimFinished = true;
	// teapotXu add end for Folder in Mainmenu
	public static HashMap<Integer , String> bg_icon_name = new HashMap<Integer , String>(); // xiatian
																							// add
																							// //Mainmenu
																							// Bg
	
	public AppList3D(
			String name )
	{
		super( name );
		mScrollToWidget = DefaultLayout.enable_scroll_to_widget;
		mHideMainmenuWidget = DefaultLayout.hide_mainmenu_widget;
		if( mHideMainmenuWidget )
			mScrollToWidget = false;
		else if( DefaultLayout.hide_appbar )
			mScrollToWidget = true;
		// mScrollToWidget = iLoongLauncher.getInstance().getResources()
		// .getBoolean(R.bool.enable_scroll_to_widget);
		mApps = new ArrayList<ApplicationInfo>();
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			mItemInfos = new ArrayList<ItemInfo>();
			this.is_maimenufolder_open = false;
			folderInfoMap = new HashMap<String , FolderIcon3D>();
		}
		// teapotXu add end for Folder in Mainmenu
		mWidgets = new ArrayList<WidgetShortcutInfo>();
		mWidget3DList = new ArrayList<View3D>();
		x = 0;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight() - R3D.appbar_height;
		if( DefaultLayout.hide_appbar )
		{
			height = Utils3D.getScreenHeight();
		}
		if( DefaultLayout.applist_style_classic )
		{
			y = R3D.hot_obj_height;
			height = Utils3D.getScreenHeight() - y;
		}
		setActionListener();
		setEffectType( SetupMenuActions.getInstance().getStringToIntger( "appeffects" ) );
		clingState = ClingManager.getInstance().fireSelectCling( this );
		sortId = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getInt( "sort_app" , -1 );
		if( sortId == -1 )
		{
			if( DefaultLayout.getInstance().show_default_app_sort )
			{
				sortId = SORT_FACTORY;
			}
			else
			{
				sortId = SORT_DEFAULT;
			}
		}
		if( DefaultLayout.dispose_cell_count )
		{
			mCellCountX = DefaultLayout.cellCountX;
			mCellCountY = DefaultLayout.cellCountY;
			mCellCountX = mCellCountX > 5 ? 5 : mCellCountX;
			mCellCountY = mCellCountY > 5 ? 5 : mCellCountY;
		}
		else
		{
			if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
			{
				mCellCountX = 4;
				mCellCountY = 5;
			}
			else
			{
				mCellCountX = 4;
				mCellCountY = 4;
			}
			// mCellCountX = (Utils3D.getScreenWidth() -
			// R3D.applist_padding_left - R3D.applist_padding_right)
			// / (R3D.workspace_cell_width + R3D.workspace_cell_adjust);
			// mCellCountY = ((int)height
			// - R3D.applist_padding_top - R3D.applist_padding_bottom)
			// / (R3D.workspace_cell_height + R3D.workspace_cell_adjust);
		}
		appGridPool = new GridPool( 5 , Utils3D.getScreenWidth() , (int)height , mCellCountX , mCellCountY );
		widgetGridPool = new GridPool( 5 , Utils3D.getScreenWidth() , (int)height , mWidgetCountX , mWidgetCountY );
		iconMap = new HashMap<String , AppIcon3D>();
		widget2DMap = new HashMap<String , Widget2DShortcut>();
		// if (Utils3D.getScreenHeight() < 500) {
		// translucentBg = new NinePatch(
		// R3D.findRegion("translucent-bg-small"), 3, 3, 3, 3);
		// } else
		// if (DefaultLayout.getInstance().mainmenu_addbackgroud
		// || DefaultLayout.getInstance().mainmenu_add_black_ground) {
		// translucentBg = new NinePatch(R3D.findRegion("translucent-bg-opa"),
		// 3, 3, 3, 3);
		// } else {
		// translucentBg = new NinePatch(R3D.findRegion("translucent-bg"), 3,
		// 3, 3, 3);
		// }
		// xiatian start //Mainmenu Bg
		initBgIconName(); // xiatian add
		// xiatian del start
		// String bg_name = null;
		// if (DefaultLayout.mainmenu_add_black_ground) {
		// bg_name = "theme/pack_source/translucent-black.png";
		// } else if (DefaultLayout.mainmenu_addbackgroud) {
		// bg_name = "theme/pack_source/translucent-bg-opa.png";
		// } else {
		// bg_name = "theme/pack_source/translucent-bg.png";
		// }
		// Bitmap bmp = ThemeManager.getInstance().getBitmap(bg_name);
		// if (bmp.getConfig() != Config.ARGB_8888)
		// {
		// bmp = bmp.copy(Config.ARGB_8888, false);
		// }
		// Texture t = new BitmapTexture(bmp);
		// // t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		// translucentBg = new NinePatch(new TextureRegion(t),1,1,1,1);
		// bmp.recycle();
		// xiatian del end
		// xiatian end
		appPageCount = 0;
		widget3DPageCount = 0;
		widget2DPageCount = 0;
		widgetPageCount = 0;
		indicatorView = new IndicatorView( "npage_indicator" );
		transform = true;
	}
	
	// wanghongjian add start //enable_DefaultScene
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( DefaultLayout.isLong )
			return true;
		return super.onLongClick( x , y );
	}
	
	// wanghongjian add end
	@Override
	protected int getIndicatorPageCount()
	{
		// TODO Auto-generated method stub
		if( mScrollToWidget )
		{
			return super.getIndicatorPageCount();
		}
		else
		{
			if( appBar == null )
			{
				return appPageCount;
			}
			else
			{
				if( appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
				{
					return appPageCount;
				}
				else
				{
					return widgetPageCount;
					// return widget3DPageCount + widget2DPageCount;
				}
			}
		}
	}
	
	@Override
	protected int getIndicatorPageIndex()
	{
		if( mScrollToWidget )
		{
			// TODO Auto-generated method stub
			return super.getIndicatorPageIndex();
		}
		else
		{
			if( appBar == null )
			{
				return page_index;
			}
			else
			{
				if( appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
				{
					return page_index;
				}
				else
				{
					return page_index - appPageCount;
				}
			}
		}
	}
	
	// View3D createShortcut(ViewGroup3D parent, ShortcutInfo info) {
	// Icon3D icon = new Icon3D(info.title.toString(),
	// info.getIcon(iconCache), info.title.toString());
	// icon.setItemInfo(info);
	// return icon;
	// }
	public void syncAppsPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mCellCountX * mCellCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getAppCount() );
		GridView3D layout = (GridView3D)view_list.get( page );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; ++i )
		{
			// teapotXu add start for Folder in Mainmenu
			if( ( DefaultLayout.mainmenu_folder_function == true && getItemInfo( i ) instanceof ApplicationInfo ) || DefaultLayout.mainmenu_folder_function == false )
			// teapotXu add end for Folder in Mainmenu
			{
				// ApplicationInfo info = getApp(i);
				ApplicationInfo info;
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					info = (ApplicationInfo)getItemInfo( i );
				}
				else
				{
					info = getApp( i );
				}
				ShortcutInfo sInfo = info.makeShortcut();
				String infoName = R3D.getInfoName( sInfo );
				AppIcon3D icon = iconMap.get( infoName );
				if( icon != null )
				{
					icon.clearState();
					icon.newAppGridIndex = page;
					if( ( page != page_index && icon.oldAppGridIndex != page_index ) )
						icon.oldAppGridIndex = page;
					icon.oldX = icon.x;
					icon.oldY = icon.y;
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						if( icon.getVisible() == false )
						{
							icon.setVisible( true );
						}
					}
					// teapotXu add end for Folder in Mainmenu
					layout.addItem( icon );
				}
				else
				{
					icon = new AppIcon3D( info.title.toString() , R3D.findRegion( sInfo ) );
					icon.oldAppGridIndex = page;
					icon.newAppGridIndex = page;
					if( page == page_index && inited )
					{
						icon.oldVisible = false;
					}
					else
					{
						icon.oldVisible = true;
					}
					iconMap.put( infoName , icon );
					layout.addItem( icon );
				}
				icon.setItemInfo( sInfo );
				if( mode == APPLIST_MODE_UNINSTALL )
					icon.showUninstall();
				else if( mode == APPLIST_MODE_HIDE )
					icon.showHide();
			}
			// teapotXu add start for Folder in Mainmenu
			else
			{
				// now current item is folder
				UserFolderInfo folderInfo = (UserFolderInfo)getItemInfo( i );
				String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
				Log.v( "cooee" , "---syncAppsPageItems--- cur icon is a folder, info is : " + folderMap_key );
				FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
				if( folderIcon != null )
				{
					// 需要重置Folder中的ICon的状态
					resetAppIconsStatusInFolder( folderIcon );
					if( folderIcon.getVisible() == false )
					{
						folderIcon.setVisible( true );
					}
					folderIcon.setApplistMode( this.mode );
					layout.addItem( folderIcon );
					// 更新folderInfo的坐标
					folderInfo.x = (int)folderIcon.getX();
					folderInfo.y = (int)folderIcon.getY();
				}
				else
				{
					if( folderInfo == newFolderInfo )
					{
						newFolderInfo = null;
						FolderIcon3D newFolder;
						// if(DefaultLayout.minu2_style_folder)
						// newFolder = new FolderIconMi(folderInfo);
						// else
						newFolder = new FolderIcon3D( folderInfo );
						if( onDropList != null )
						{
							newFolder.onDrop( onDropList , 0 , 0 );
							onDropList = null;
						}
						// new FolderIcon3D(folderInfo);
						// newFolder.createAndAddShortcut(this.iconCache,
						// folderInfo);
						// 需要重置Folder中的ICon的状态
						resetAppIconsStatusInFolder( newFolder );
						folderInfoMap.put( folderMap_key , newFolder );
						layout.addItem( newFolder );
						// 更新folderInfo的坐标
						folderInfo.x = (int)newFolder.getX();
						folderInfo.y = (int)newFolder.getY();
					}
					else
					{
						FolderIcon3D newFolder;
						// if(DefaultLayout.minu2_style_folder)
						// newFolder = new FolderIconMi(folderInfo);
						// else
						newFolder = new FolderIcon3D( folderInfo );
						folderInfoMap.put( folderMap_key , newFolder );
						layout.addItem( newFolder );
						newFolder.createAndAddShortcut( iconCache , folderInfo );
						this.drageTarget_new_child = newFolder;
						viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
					}
				}
			}
			// teapotXu add end for Folder in Mainmenu
		}
	}
	
	public void syncWidgetPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getWidgetCount() );
		GridView3D layout = (GridView3D)view_list.get( page + appPageCount );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; i++ )
		{
			if( i < getWidget3DCount() )
			{
				View3D widget = getWidget3D( i );
				if( widget instanceof Widget3DShortcut )
				{
					Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
					widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetShortcut.makeShortcut();
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetShortcut.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetShortcut.showHide();
					else
						widgetShortcut.clearState();
					widgetShortcut.newAppGridIndex = page + appPageCount;
					if( ( widgetShortcut.newAppGridIndex != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
						widgetShortcut.oldAppGridIndex = page + appPageCount;
					widgetShortcut.oldX = widgetShortcut.x;
					widgetShortcut.oldY = widgetShortcut.y;
				}
				else if( widget instanceof Widget3DVirtual )
				{
					Widget3DVirtual widgetIcon = (Widget3DVirtual)widget;
					if( widgetIcon.uninstalled )
					{
						mWidget3DList.remove( widgetIcon );
						endIndex--;
						continue;
					}
					widgetIcon.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetIcon.makeShortcut();
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetIcon.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetIcon.showHide();
					else
						widgetIcon.clearState();
					widgetIcon.newAppGridIndex = page + appPageCount;
					if( ( widgetIcon.newAppGridIndex != page_index && widgetIcon.oldAppGridIndex != page_index ) )
						widgetIcon.oldAppGridIndex = page + appPageCount;
					widgetIcon.oldX = widgetIcon.x;
					widgetIcon.oldY = widgetIcon.y;
				}
				layout.addItem( widget );
			}
			else
			{
				WidgetShortcutInfo widgetInfo = getWidget2D( i - getWidget3DCount() );
				if( widgetInfo == null )
					continue;
				Widget2DShortcut widgetShortcut = widget2DMap.get( widgetInfo.textureName );
				if( widgetShortcut != null )
				{
					widgetShortcut.clearState();
					widgetShortcut.newAppGridIndex = page;
					if( ( page != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
						widgetShortcut.oldAppGridIndex = page;
					widgetShortcut.oldX = widgetShortcut.x;
					widgetShortcut.oldY = widgetShortcut.y;
					layout.addItem( widgetShortcut );
				}
				else
				{
					widgetShortcut = new Widget2DShortcut( widgetInfo.label , R3D.findRegion( widgetInfo.textureName ) );
					widgetShortcut.oldAppGridIndex = page;
					widgetShortcut.newAppGridIndex = page;
					if( page == page_index && inited )
					{
						widgetShortcut.oldVisible = false;
					}
					else
					{
						widgetShortcut.oldVisible = true;
					}
					widget2DMap.put( widgetInfo.textureName , widgetShortcut );
					layout.addItem( widgetShortcut );
				}
				if( mode == APPLIST_MODE_HIDE )
					widgetShortcut.showHide();
				widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
				widgetShortcut.setInfo( widgetInfo );
			}
		}
	}
	
	public void syncWidget3DPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getWidget3DCount() );
		GridView3D layout = (GridView3D)view_list.get( page + appPageCount );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; i++ )
		{
			View3D widget = getWidget3D( i );
			if( widget instanceof Widget3DShortcut )
			{
				Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
				widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
				widgetShortcut.makeShortcut();
				if( mode == APPLIST_MODE_UNINSTALL )
					widgetShortcut.showUninstall();
				else if( mode == APPLIST_MODE_HIDE )
					widgetShortcut.showHide();
				else
					widgetShortcut.clearState();
				widgetShortcut.newAppGridIndex = page + appPageCount;
				if( ( widgetShortcut.newAppGridIndex != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
					widgetShortcut.oldAppGridIndex = page + appPageCount;
				widgetShortcut.oldX = widgetShortcut.x;
				widgetShortcut.oldY = widgetShortcut.y;
			}
			else if( widget instanceof Widget3DVirtual )
			{
				Widget3DVirtual widgetIcon = (Widget3DVirtual)widget;
				if( widgetIcon.uninstalled )
				{
					mWidget3DList.remove( widgetIcon );
					endIndex--;
					continue;
				}
				widgetIcon.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
				widgetIcon.makeShortcut();
				if( mode == APPLIST_MODE_UNINSTALL )
					widgetIcon.showUninstall();
				else if( mode == APPLIST_MODE_HIDE )
					widgetIcon.showHide();
				else
					widgetIcon.clearState();
				widgetIcon.newAppGridIndex = page + appPageCount;
				if( ( widgetIcon.newAppGridIndex != page_index && widgetIcon.oldAppGridIndex != page_index ) )
					widgetIcon.oldAppGridIndex = page + appPageCount;
				widgetIcon.oldX = widgetIcon.x;
				widgetIcon.oldY = widgetIcon.y;
			}
			layout.addItem( widget );
		}
	}
	
	public void syncWidget2DPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getWidget2DCount() );
		GridView3D layout = (GridView3D)view_list.get( page + appPageCount + widget3DPageCount );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; ++i )
		{
			WidgetShortcutInfo widgetInfo = getWidget2D( i );
			Widget2DShortcut widgetShortcut = widget2DMap.get( widgetInfo.textureName );
			if( widgetShortcut != null )
			{
				widgetShortcut.clearState();
				widgetShortcut.newAppGridIndex = page;
				if( ( page != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
					widgetShortcut.oldAppGridIndex = page;
				widgetShortcut.oldX = widgetShortcut.x;
				widgetShortcut.oldY = widgetShortcut.y;
				layout.addItem( widgetShortcut );
			}
			else
			{
				widgetShortcut = new Widget2DShortcut( widgetInfo.label , R3D.findRegion( widgetInfo.textureName ) );
				widgetShortcut.oldAppGridIndex = page;
				widgetShortcut.newAppGridIndex = page;
				if( page == page_index && inited )
				{
					widgetShortcut.oldVisible = false;
				}
				else
				{
					widgetShortcut.oldVisible = true;
				}
				widget2DMap.put( widgetInfo.textureName , widgetShortcut );
				layout.addItem( widgetShortcut );
			}
			if( mode == APPLIST_MODE_HIDE )
				widgetShortcut.showHide();
			widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
			widgetShortcut.setInfo( widgetInfo );
		}
	}
	
	public void setIconCache(
			IconCache iconCache )
	{
		this.iconCache = iconCache;
	}
	
	public void syncAppsPages()
	{
		for( int i = 0 ; i < appPageCount ; i++ )
		{
			appGridPool.free( (GridView3D)view_list.get( 0 ) );
			this.removeView( view_list.get( 0 ) );
			view_list.remove( 0 );
		}
		syncAppPageCount();
		for( int i = 0 ; i < appPageCount ; i++ )
		{
			GridView3D grid = appGridPool.get();
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( i , grid );
			syncAppsPageItems( i , true );
			grid.setAutoDrag( false );
		}
		if( appPageCount == 0 )
		{
			appPageCount = 1;
			GridView3D grid = appGridPool.get();
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( 0 , grid );
			grid.setAutoDrag( false );
		}
		if( appBar == null )
		{
			if( DefaultLayout.hide_mainmenu_widget )
			{
				if( page_index >= appPageCount )
					setCurrentPage( appPageCount - 1 );
				else
					setCurrentPage( page_index );
			}
			else
			{
				if( page_index >= view_list.size() )
					setCurrentPage( view_list.size() - 1 );
				else
					setCurrentPage( page_index );
			}
		}
		else
		{
			if( appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
			{
				if( page_index >= appPageCount )
					setCurrentPage( appPageCount - 1 );
				else
					setCurrentPage( page_index );
			}
			else
			{
				if( page_index >= view_list.size() )
					setCurrentPage( view_list.size() - 1 );
				else
					setCurrentPage( page_index );
			}
		}
	}
	
	public boolean canPopMenu()
	{
		if( DefaultLayout.hide_mainmenu_widget )
			return true;
		if( appBar == null )
		{
			if( page_index < appPageCount )
				return true;
			return false;
		}
		else
		{
			// teapotXu add start for Folder in Mainmenu
			if( AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_APP && appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT && this.is_maimenufolder_open == false )
				// if (AppHost3D.currentContentType ==
				// AppHost3D.CONTENT_TYPE_APP
				// && appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT)
				// teapotXu add end for Folder in Mainmenu
				return true;
			return false;
		}
	}
	
	public void syncWidgetPages()
	{
		int n = view_list.size();
		for( int i = appPageCount ; i < n ; i++ )
		{
			widgetGridPool.free( (GridView3D)view_list.get( appPageCount ) );
			this.removeView( view_list.get( appPageCount ) );
			view_list.remove( appPageCount );
		}
		syncWidgetPageCount();
		for( int i = 0 ; i < widgetPageCount ; i++ )
		{
			GridView3D grid = widgetGridPool.get();
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( i + appPageCount , grid );
			syncWidgetPageItems( i , true );
			grid.setAutoDrag( false );
		}
		if( widgetPageCount == 0 && !iLoongApplication.BuiltIn )
		{
			widgetPageCount = 1;
			GridView3D grid = widgetGridPool.get();
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( appPageCount , grid );
			grid.setAutoDrag( false );
		}
		if( page_index >= view_list.size() )
			setCurrentPage( view_list.size() - 1 );
		else
			setCurrentPage( page_index );
	}
	
	public void syncAppPageCount()
	{
		int numCells = mCellCountX * mCellCountY;
		appPageCount = ( getAppCount() + numCells - 1 ) / numCells;
		if( page_index >= appPageCount + widgetPageCount )
			page_index = appPageCount + widgetPageCount - 1;
	}
	
	public void syncWidgetPageCount()
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		widgetPageCount = ( getWidget3DCount() + getWidget2DCount() + numCells - 1 ) / numCells;
		if( page_index >= appPageCount + widgetPageCount )
			page_index = appPageCount + widgetPageCount - 1;
	}
	
	public void syncWidget3DPageCount()
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		widget3DPageCount = ( getWidget3DCount() + numCells - 1 ) / numCells;
		if( page_index >= appPageCount + widget3DPageCount + widget2DPageCount )
			page_index = appPageCount + widget3DPageCount + widget2DPageCount - 1;
	}
	
	public void syncWidget2DPageCount()
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		widget2DPageCount = ( getWidget2DCount() + numCells - 1 ) / numCells;
		if( page_index >= appPageCount + widget3DPageCount + widget2DPageCount )
			page_index = appPageCount + widget3DPageCount + widget2DPageCount - 1;
	}
	
	public void startAnimation()
	{
		Iterator iconIter = iconMap.entrySet().iterator();
		tween = null;
		while( iconIter.hasNext() )
		{
			Map.Entry entry = (Map.Entry)iconIter.next();
			AppIcon3D icon = (AppIcon3D)entry.getValue();
			if( icon.getParent() != null )
			{
				if( !icon.oldVisible )
				{
					icon.setScale( 0 , 0 );
					tween = icon.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
					icon.oldVisible = true;
				}
				else if( icon.oldAppGridIndex != icon.newAppGridIndex || ( ( icon.oldX != icon.x || icon.oldY != icon.y ) && icon.newAppGridIndex == page_index ) )
				{
					// Log.d("launcher",
					// "anim:"+val.name+","+val.oldAppGridIndex+","+val.newAppGridIndex);
					float oldX = ( icon.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + icon.oldX;
					float oldY = icon.oldY;
					float newX = ( icon.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + icon.x;
					float newY = icon.y;
					icon.setPosition( oldX , oldY );
					tween = icon.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , newX , newY , 0 );
					icon.oldX = newX;
					icon.oldY = newY;
					icon.oldAppGridIndex = icon.newAppGridIndex;
				}
			}
			else
				icon.oldVisible = false;
		}
		if( DefaultLayout.hide_mainmenu_widget )
		{
			needLayout = true;
			if( tween != null )
			{
				tween.setCallback( this );
			}
			return;
		}
		Iterator widgetIter = widget2DMap.entrySet().iterator();
		while( widgetIter.hasNext() )
		{
			Map.Entry entry = (Map.Entry)widgetIter.next();
			Widget2DShortcut val = (Widget2DShortcut)entry.getValue();
			if( val.getParent() != null )
			{
				if( !val.oldVisible )
				{
					val.setScale( 0 , 0 );
					val.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
					val.oldVisible = true;
				}
				else if( val.oldAppGridIndex != val.newAppGridIndex || ( ( val.oldX != val.x || val.oldY != val.y ) && val.newAppGridIndex == page_index ) )
				{
					// Log.d("launcher",
					// "anim:"+val.name+","+val.oldAppGridIndex+","+val.newAppGridIndex);
					float oldX = ( val.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + val.oldX;
					float oldY = val.oldY;
					float newX = ( val.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + val.x;
					float newY = val.y;
					val.setPosition( oldX , oldY );
					val.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , newX , newY , 0 );
					val.oldX = newX;
					val.oldY = newY;
					val.oldAppGridIndex = val.newAppGridIndex;
				}
			}
			else
				val.oldVisible = false;
		}
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof Widget3DShortcut )
			{
				Widget3DShortcut widget = (Widget3DShortcut)view;
				if( widget.getParent() != null )
				{
					if( !widget.inited )
					{
						widget.inited = true;
						widget.oldVisible = true;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
					if( !widget.oldVisible )
					{
						widget.setScale( 0 , 0 );
						widget.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , 1 , 1 , 0 );
						widget.oldVisible = true;
					}
					else if( widget.oldAppGridIndex != widget.newAppGridIndex || ( ( widget.oldX != widget.x || widget.oldY != widget.y ) && widget.newAppGridIndex == page_index ) )
					{
						float oldX = ( widget.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.oldX;
						float oldY = widget.oldY;
						float newX = ( widget.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.x;
						float newY = widget.y;
						widget.setPosition( oldX , oldY );
						widget.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , newX , newY , 0 );
						widget.oldX = newX;
						widget.oldY = newY;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
				}
				else
				{
					widget.inited = true;
					widget.oldVisible = false;
				}
			}
			else if( view instanceof WidgetIcon )
			{
				WidgetIcon widget = (WidgetIcon)view;
				if( widget.getParent() != null )
				{
					if( !widget.inited )
					{
						widget.inited = true;
						widget.oldVisible = true;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
					if( !widget.oldVisible )
					{
						widget.setScale( 0 , 0 );
						widget.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
						widget.oldVisible = true;
					}
					else if( widget.oldAppGridIndex != widget.newAppGridIndex || ( ( widget.oldX != widget.x || widget.oldY != widget.y ) && widget.newAppGridIndex == page_index ) )
					{
						float oldX = ( widget.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.oldX;
						float oldY = widget.oldY;
						float newX = ( widget.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.x;
						float newY = widget.y;
						widget.setPosition( oldX , oldY );
						widget.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , newX , newY , 0 );
						widget.oldX = newX;
						widget.oldY = newY;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
				}
				else
				{
					widget.oldVisible = false;
					if( !widget.inited )
					{
						widget.inited = true;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
				}
			}
		}
		needLayout = true;
		if( tween != null )
		{
			tween.setCallback( this );
			// teapotXu add start for Folder in Mainmenu
			// when this animation begins, first set this flag false
			if( DefaultLayout.mainmenu_folder_function == true )
				syncAppAnimFinished = false;
			// teapotXu add end for Folder in Mainmenu
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( type == TweenCallback.COMPLETE )
			{
				// animalFinished =true;
				if( source == tween )
				{
					syncAppAnimFinished = true;
				}
				Log.v( "focus" , "on event 11111" );
			}
		}
		// teapotXu add end for Folder in Mainmenu
		if( needLayout && source == tween )
		{
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					( (GridView3D)i ).layout_pub( 0 , false );
				}
			}
		}
		// teapotXu add start for Folder in Mainmenu
		else if( dragIconBackTween == source && type == TweenCallback.COMPLETE )
		{
			// drag icon动画移动结束
			// sortApp(sortId, false);
			Object userData = dragIconBackTween.getUserData();
			if( userData != null && userData instanceof Icon3D )
			{
				//
				ViewGroup3D cur_gridview = (ViewGroup3D)view_list.get( page_index );
				cur_gridview.removeView( (Icon3D)userData );
			}
			syncAppsPages();
			startAnimation();
		}
		// teapotXu add end for Folder in Mainmenu
		super.onEvent( type , source );
	}
	
	public void setApps(
			ArrayList<ApplicationInfo> list )
	{
		if( inited )
		{
			Log.i( "load" , "apps has init,return" );
			return;
		}
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( list.size() > 0 )
			{
				mItemInfos.clear();
				for( ApplicationInfo appInfo : list )
				{
					mItemInfos.add( (ItemInfo)appInfo );
				}
			}
		}
		else
		{
			mApps = list;
		}
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , false );
		syncAppsPages();
		setWidget3D();
		if( !iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget )
		{
			syncWidgetPages();
		}
		inited = true;
		ApplistVirtualIconManager virtualIconConfig = new ApplistVirtualIconManager( this );
		virtualIconConfig.insertVirtualIcon();
	}
	
	public void setFolders(
			ArrayList<FolderInfo> list )
	{
		if( inited )
		{
			Log.d( "launcher" , ":has init,return" );
			return;
		}
		if( list.size() > 0 )// mApps = list;
		{
			mItemInfos.clear();
			for( FolderInfo folderInfo : list )
			{
				mItemInfos.add( folderInfo );
			}
		}
		sortApp( sortId , false );
		syncAppsPages();
		setWidget3D();
		if( !iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget )
		{
			syncWidgetPages();
		}
		inited = true;
		ApplistVirtualIconManager virtualIconConfig = new ApplistVirtualIconManager( this );
		virtualIconConfig.insertVirtualIcon();
	}
	
	public void setWidgets(
			ArrayList<WidgetShortcutInfo> widgets )
	{
		mWidgets = widgets;
		syncWidgetPages();
	}
	
	public void setWidget3D()
	{
		mWidget3DList.clear();
		mWidget3DList.add( Desktop3DListener.folder3DHost );
		if( DefaultLayout.mainmenu_widget_display_contacts )
			mWidget3DList.add( Desktop3DListener.contact3DHost );
		List<Widget3DShortcut> mWidgetList = Widget3DManager.getInstance().getWidgetList();
		for( int i = 0 ; i < mWidgetList.size() ; i++ )
		{
			Widget3DShortcut view = mWidgetList.get( i );
			mWidget3DList.add( view );
		}
		for( int j = 0 ; j < Widget3DManager.defIcon3DList.size() ; j++ )
		{
			mWidget3DList.add( Widget3DManager.defIcon3DList.get( j ) );
		}
		// syncWidget3DPages();
	}
	
	private boolean isUserApp(
			int flags )
	{
		boolean canUninstall = false;
		// int flags = info.activityInfo.applicationInfo.flags;
		if( ( flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
		{
			canUninstall = true;
		}
		else if( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) == 0 )
		{
			canUninstall = true;
		}
		return canUninstall;
	}
	
	public int getAppCount()
	{
		int appCount = 0;
		if( mode == APPLIST_MODE_HIDE )
		{
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				appCount = mItemInfos.size();
			}
			else
			{
				appCount = mApps.size();
			}
			// teapotXu add end for Folder in Mainmenu
		}
		else if( mode == APPLIST_MODE_USERAPP )
		{
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				for( int i = 0 ; i < mItemInfos.size() ; i++ )
				{
					ItemInfo itemInfo = mItemInfos.get( i );
					if( itemInfo instanceof ApplicationInfo )
					{
						if( isUserApp( ( (ApplicationInfo)itemInfo ).flags ) )
							appCount++;
					}
					else
					{
						// it is folderInfo
						appCount++;
					}
				}
			}
			else
			{
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					if( isUserApp( mApps.get( i ).flags ) )
						appCount++;
				}
			}
			// teapotXu add end for Folder in Mainmenu
		}
		else
		{
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				for( int i = 0 ; i < mItemInfos.size() ; i++ )
				{
					ItemInfo itemInfo = mItemInfos.get( i );
					if( itemInfo instanceof ApplicationInfo )
					{
						if( !( (ApplicationInfo)itemInfo ).isHideIcon )
							appCount++;
					}
					else
					{
						// it is folderInfo
						appCount++;
					}
				}
			}
			else
			{
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					if( !mApps.get( i ).isHideIcon )
						appCount++;
				}
			}
			// teapotXu add end for Folder in Mainmenu
		}
		return appCount;
	}
	
	private ApplicationInfo getApp(
			int position )
	{
		if( mode == APPLIST_MODE_HIDE )
		{
			if( sortArray == null )
				return mApps.get( position );
			return mApps.get( sortArray[position] );
		}
		else if( mode == APPLIST_MODE_USERAPP )
		{
			int n = -1;
			for( int i = 0 ; i < mApps.size() ; i++ )
			{
				ApplicationInfo app = null;
				if( sortArray == null )
					app = mApps.get( i );
				else
					app = mApps.get( sortArray[i] );
				if( isUserApp( app.flags ) )
					n++;
				if( n == position )
					return app;
			}
		}
		else
		{
			int n = -1;
			for( int i = 0 ; i < mApps.size() ; i++ )
			{
				ApplicationInfo app = null;
				if( sortArray == null )
					app = mApps.get( i );
				else
					app = mApps.get( sortArray[i] );
				if( !app.isHideIcon )
					n++;
				if( n == position )
					return app;
			}
		}
		return null;
	}
	
	// teapotXu add start for Folder in Mainmenu
	private ItemInfo getItemInfo(
			int position )
	{
		if( mode == APPLIST_MODE_HIDE )
		{
			return mItemInfos.get( sortArray[position] );
		}
		else if( mode == APPLIST_MODE_USERAPP )
		{
			int n = -1;
			for( int i = 0 ; i < mItemInfos.size() ; i++ )
			{
				ItemInfo itemInfo = mItemInfos.get( sortArray[i] );
				if( itemInfo instanceof ApplicationInfo )
				{
					if( isUserApp( ( (ApplicationInfo)itemInfo ).flags ) )
						n++;
					if( n == position )
						return itemInfo;
				}
				else
				{
					// when this item is a folder, always show
					n++;
					if( n == position )
						return itemInfo;
				}
			}
		}
		else
		{
			int n = -1;
			for( int i = 0 ; i < mItemInfos.size() ; i++ )
			{
				ItemInfo itemInfo = null;
				if( sortArray == null )
					itemInfo = mItemInfos.get( i );
				else
					itemInfo = mItemInfos.get( sortArray[i] );
				if( itemInfo instanceof ApplicationInfo )
				{
					if( !( (ApplicationInfo)itemInfo ).isHideIcon )
						n++;
					if( n == position )
						return itemInfo;
				}
				else
				{
					// when this item is a folder, always show
					n++;
					if( n == position )
						return itemInfo;
				}
			}
		}
		return null;
	}
	
	// teapotXu add end for Folder in Mainmenu
	private int getWidgetCount()
	{
		int widgetCount = 0;
		if( mode == APPLIST_MODE_HIDE )
			widgetCount = mWidget3DList.size() + mWidgets.size();
		else
		{
			for( int i = 0 ; i < mWidget3DList.size() ; i++ )
			{
				if( mWidget3DList.get( i ) instanceof Widget3DShortcut )
				{
					Widget3DShortcut shortcut = (Widget3DShortcut)mWidget3DList.get( i );
					if( !shortcut.isHideWidget )
						widgetCount++;
				}
				else if( mWidget3DList.get( i ) instanceof Widget3DVirtual )
				{
					Widget3DVirtual shortcut = (Widget3DVirtual)mWidget3DList.get( i );
					if( !shortcut.isHide )
						widgetCount++;
				}
			}
			for( int i = 0 ; i < mWidgets.size() ; i++ )
			{
				WidgetShortcutInfo info = mWidgets.get( i );
				if( !info.isHide )
					widgetCount++;
			}
		}
		return widgetCount;
	}
	
	private int getWidget3DCount()
	{
		int widgetCount = 0;
		if( mode == APPLIST_MODE_HIDE )
			widgetCount = mWidget3DList.size();
		else
		{
			for( int i = 0 ; i < mWidget3DList.size() ; i++ )
			{
				if( mWidget3DList.get( i ) instanceof Widget3DShortcut )
				{
					Widget3DShortcut shortcut = (Widget3DShortcut)mWidget3DList.get( i );
					if( !shortcut.isHideWidget )
						widgetCount++;
				}
				else if( mWidget3DList.get( i ) instanceof Widget3DVirtual )
				{
					Widget3DVirtual shortcut = (Widget3DVirtual)mWidget3DList.get( i );
					if( !shortcut.isHide )
						widgetCount++;
				}
			}
		}
		return widgetCount;
	}
	
	private View3D getWidget3D(
			int position )
	{
		if( mode == APPLIST_MODE_HIDE )
		{
			return mWidget3DList.get( position );
		}
		int n = -1;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof Widget3DShortcut )
			{
				if( !( (Widget3DShortcut)view ).isHideWidget )
					n++;
			}
			else if( view instanceof Widget3DVirtual )
			{
				if( !( (Widget3DVirtual)view ).isHide )
					n++;
			}
			if( n == position )
				return view;
		}
		return null;
	}
	
	private int getWidget2DCount()
	{
		int widgetCount = 0;
		if( mode == APPLIST_MODE_HIDE )
			widgetCount = mWidgets.size();
		else
		{
			for( int i = 0 ; i < mWidgets.size() ; i++ )
			{
				WidgetShortcutInfo info = mWidgets.get( i );
				if( !info.isHide )
					widgetCount++;
			}
		}
		return widgetCount;
	}
	
	private WidgetShortcutInfo getWidget2D(
			int position )
	{
		if( mode == APPLIST_MODE_HIDE )
		{
			return mWidgets.get( position );
		}
		int n = -1;
		for( int i = 0 ; i < mWidgets.size() ; i++ )
		{
			WidgetShortcutInfo info = mWidgets.get( i );
			if( !info.isHide )
				n++;
			if( n == position )
				return info;
		}
		return null;
	}
	
	public void setMode(
			int mode )
	{
		if( this.mode != mode )
		{
			if( appBar != null )
				appBar.tab.setMode( mode );
			// if(this.mode == APPLIST_MODE_NORMAL || mode ==
			// APPLIST_MODE_NORMAL){
			// this.mode = mode;
			// syncAppsPages();
			// syncWidget3DPages();
			// }
			// else if(this.mode == APPLIST_MODE_HIDE && mode ==
			// APPLIST_MODE_NORMAL){
			// this.mode = mode;
			// syncAppsPages();
			// syncWidget3DPages();
			// }
			if( this.mode == APPLIST_MODE_HIDE && mode == APPLIST_MODE_UNINSTALL )
			{
				this.mode = mode;
				refresh();
			}
			else
			{
				this.mode = mode;
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					boolean is_need_sync_app = true;
					// when addApp, if folder is open,close the folder first
					if( this.is_maimenufolder_open && this.mOpenFolderIcon != null )
					{
						String folderMap_key = ICON_MAP_FOLDER_LABEL + mOpenFolderIcon.getItemInfo().id;
						FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
						if( folderIcon != null )
						{
							folderIcon.stopTween();
							// if(DefaultLayout.minu2_style_folder == true)
							// {
							// ((FolderIconMi)folderIcon).getMIUIFolder().DealButtonOKDown();
							// }
							// else
							if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
							{
								( folderIcon ).getMIUI3DFolder().DealButtonOKDown();
							}
							else
							{
								folderIcon.getFolder().DealButtonOKDown();
							}
						}
						is_need_sync_app = false;
					}
					if( !is_need_sync_app )
						return;
				}
				// teapotXu add end for Folder in Mainmenu
				syncAppsPages();
				if( iLoongApplication.BuiltIn && !DefaultLayout.hide_mainmenu_widget )
					syncWidgetPages();
				// syncWidget3DPages();
				// syncWidget2DPages();
				startAnimation();
			}
		}
	}
	
	private void refresh()
	{
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			GridView3D layout = (GridView3D)view_list.get( i );
			for( int j = 0 ; j < layout.getChildCount() ; j++ )
			{
				View3D child = layout.getChildAt( j );
				if( child instanceof Icon3D )
				{
					Icon3D icon = (Icon3D)child;
					if( mode == APPLIST_MODE_UNINSTALL )
						icon.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						icon.showHide();
					else
						icon.clearState();
				}
				else if( child instanceof Widget3DShortcut )
				{
					Widget3DShortcut widgetShortcut = (Widget3DShortcut)child;
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetShortcut.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetShortcut.showHide();
					else
						widgetShortcut.clearState();
				}
				else if( child instanceof Widget3DVirtual )
				{
					Widget3DVirtual widgetVirtual = (Widget3DVirtual)child;
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetVirtual.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetVirtual.showHide();
					else
						widgetVirtual.clearState();
				}
				else if( child instanceof Widget2DShortcut )
				{
					Widget2DShortcut shortcut = (Widget2DShortcut)child;
					if( mode == APPLIST_MODE_HIDE )
						shortcut.showHide();
					else
						shortcut.clearState();
				}
			}
		}
	}
	
	public ArrayList<View3D> getDragObjects()
	{
		return dragObjects;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		int srcBlendFunc = 0 , dstBlendFunc = 0;
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			/* 获取获取混合方式 */
			srcBlendFunc = batch.getSrcBlendFunc();
			dstBlendFunc = batch.getDstBlendFunc();
			if( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE );
		}
		y = this.getUser();
		int h = iLoongLauncher.getInstance().getResources().getDisplayMetrics().heightPixels;
		refreshBg(); // xiatian add //Mainmenu Bg
		if( translucentBg != null )
		{
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha * ( height + y - ( DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0 ) ) / height );
			translucentBg.draw( batch , 0 , 0 , Utils3D.realWidth , h );
			// batch.draw(R3D.findRegion("translucent-bg"),0,0);
		}
		if( !firstlyCome && page_index != appPageCount )
		{
			getFocusLocation( currentCell );
			iconFocus.draw(
					batch ,
					locationX - ( R3D.Workspace_cell_each_width - R3D.workspace_cell_width ) / 2 ,
					locationY + ( R3D.workspace_cell_height - R3D.Workspace_cell_each_height ) / 2 ,
					R3D.Workspace_cell_each_width ,
					R3D.Workspace_cell_each_height );
		}
		super.draw( batch , parentAlpha );
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			if( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE )
				batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
		}
		// if(mesh==null)
		// mesh =
		// ObjLoader.loadObj(ThemeManager.getInstance().getFile("earth.obj"));
		// mesh.render(SpriteBatch.createDefaultShader(),GL10.GL_TRIANGLES);
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof Widget3DVirtual )
		{
			Widget3DVirtual widget = (Widget3DVirtual)sender;
			ShortcutInfo info = (ShortcutInfo)widget.getItemInfo();
			switch( event_id )
			{
				case Widget3DVirtual.MSG_WIDGET3D_SHORTCUT_LONGCLICK:
				{
					String className = DefaultLayout.getWidgetItemClassName( info.intent.getComponent().getPackageName() );
					Widget3D dragObj = Widget3DManager.getInstance().getWidget3D( info.intent.getComponent().getPackageName() , className );
					if( dragObj == null )
						return true;
					dragObj.setPosition( DragLayer3D.dragStartX - dragObj.width / 2 , DragLayer3D.dragStartY - dragObj.height / 2 );
					clearDragObjs();
					dragObjects.clear();
					dragObjects.add( dragObj );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				}
			}
		}
		else if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			switch( event_id )
			{
				case Icon3D.MSG_ICON_LONGCLICK:
					if( selectedObjects.size() == 0 )
					{
						selectedObjects.add( icon );
						// dragObjects.add(icon.clone());
					}
					if( !selectedObjects.contains( icon ) )
					{
						clearDragObjs();
						selectedObjects.add( icon );
					}
					dragObjects.clear();
					for( View3D view : selectedObjects )
					{
						if( view instanceof Icon3D )
						{
							Icon3D icon3d = (Icon3D)view;
							icon3d.hideSelectedIcon();
							Icon3D iconClone = icon3d.clone();
							icon3d.toAbsoluteCoords( point );
							iconClone.x = point.x;
							iconClone.y = point.y;
							// teapotXu add start for Folder in Mainmenu
							if( DefaultLayout.mainmenu_folder_function == true && this.mode == APPLIST_MODE_UNINSTALL )
							{
								icon3d.setVisible( false );
							}
							// teapotXu add end for Folder in Mainmenu
							dragObjects.add( iconClone );
						}
					}
					selectedObjects.clear();
					this.setTag( icon.getTag() );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Icon3D.MSG_ICON_SELECTED:
					selectedObjects.add( icon );
					// dragObjects.add(icon.clone());
					return true;
				case Icon3D.MSG_ICON_UNSELECTED:
					selectedObjects.remove( icon );
					// dragObjects.remove(icon);
					return true;
			}
		}
		else if( sender instanceof Widget2DShortcut )
		{
			Widget2DShortcut widget = (Widget2DShortcut)sender;
			switch( event_id )
			{
				case Widget2DShortcut.MSG_WIDGET_SHORTCUT_LONGCLICK:
					clearDragObjs();
					dragObjects.clear();
					Widget2DShortcut widgetClone = widget.createDragView();
					widgetClone.toAbsoluteCoords( point );
					dragObjects.add( widgetClone );
					this.setTag( new Vector2( widgetClone.x , widgetClone.y ) );
					Vector2 v = (Vector2)widget.getTag();
					widgetClone.setPosition( v.x - widgetClone.width / 2 , v.y - widgetClone.height / 2 );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		else if( sender instanceof Widget3DShortcut )
		{
			Widget3DShortcut widget = (Widget3DShortcut)sender;
			switch( event_id )
			{
				case Widget3DShortcut.MSG_WIDGET3D_SHORTCUT_LONGCLICK:
					View3D dragObj = widget.getWidget3D();
					if( dragObj == null )
						return true;
					clearDragObjs();
					dragObjects.clear();
					dragObjects.add( dragObj );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		// teapotXu add start for Folder in Mainmenu
		else if( sender instanceof DragLayer3D )
		{
			DragLayer3D draglayer = (DragLayer3D)sender;
			int draglayer_border_shown = draglayer.getBorder();
			switch( event_id )
			{
				case DragLayer3D.MSG_DRAG_INBORDER:
					if( draglayer_border_shown == -1 )// move to left
					{
						if( page_index != 0 )
						{
							if( this.NPage_IsManualScrollTo() == false )
							{
								this.scrollTo( preIndex() );
							}
						}
						else
						{
							// hide draglayer Border
							// draglayer.setBorder(0);
						}
					}
					else if( draglayer_border_shown == 1 ) // move to right
					{
						if( page_index != appPageCount - 1 )
						{
							if( this.NPage_IsManualScrollTo() == false )
							{
								this.scrollTo( nextIndex() );
							}
						}
						else
						{
							// hide draglayer Border either
							// draglayer.setBorder(0);
						}
					}
					return true;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void setActionListener()
	{
		// TODO Auto-generated method stub
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_DESKTOP_SETTINGS , this );
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		// TODO Auto-generated method stub
		if( bundle.containsKey( "appeffects" ) )
			setEffectType( bundle.getInt( "appeffects" ) );
	}
	
	public void addApps(
			ArrayList<ApplicationInfo> list )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			boolean is_need_sync_app = true;
			// when addApp, if folder is open,close the folder first
			if( this.is_maimenufolder_open && this.mOpenFolderIcon != null )
			{
				String folderMap_key = ICON_MAP_FOLDER_LABEL + mOpenFolderIcon.getItemInfo().id;
				FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
				if( folderIcon != null )
				{
					folderIcon.stopTween();
					// if(DefaultLayout.minu2_style_folder == true)
					// {
					// ((FolderIconMi)folderIcon).getMIUIFolder().DealButtonOKDown();
					// }
					// else
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						( folderIcon ).getMIUI3DFolder().DealButtonOKDown();
					}
					else
					{
						folderIcon.getFolder().DealButtonOKDown();
					}
				}
				is_need_sync_app = false;
			}
			// mItemInfos.addAll(list);
			addAppIntoItemInfoNoRepeat( mItemInfos , list );
			ApplistVirtualIconManager.onAppsAdd( list );
			if( !is_need_sync_app )
				return;
		}
		else
		{
			addAppNoRepeat( mApps , list );
			ApplistVirtualIconManager.onAppsAdd( list );
		}
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , false );
		syncAppsPages();
		if( Desktop3DListener.bAppDone )
			startAnimation();
	}
	
	public void addFolders(
			ArrayList<FolderInfo> folders )
	{
		// teapotXu add start for Folder in Mainmenu
		mItemInfos.addAll( folders );
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	public void reomveApps(
			ArrayList<ApplicationInfo> list ,
			boolean permanent )
	{
		boolean is_need_sync_app_immediatly = true; // teapotXu add for Folder
													// in Mainmenu
		for( int i = 0 ; i < list.size() ; i++ )
		{
			if( permanent )
				list.get( i ).removeUseFrequency();
			if( permanent )
				list.get( i ).removeHide();
			ApplicationInfo info = list.get( i );
			ShortcutInfo sInfo = info.makeShortcut();
			String infoName = R3D.getInfoName( sInfo );
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				if( isUserApp( info.flags ) )
				{
					Icon3D icon_1 = iconMap.get( infoName );
					if( icon_1 != null )
					{
						icon_1.clearState();
					}
					iconMap.remove( infoName );
				}
				else
				{
					if( iconMap.get( infoName ) != null )
					{
						iconMap.get( infoName ).clearState();
					}
					iconMap.remove( infoName );
				}
				if( mItemInfos.contains( info ) )
				{
					// current info is instanceof Application, and not in
					// Folder, so remove it directly
					mItemInfos.remove( info );
				}
				else
				{
					boolean is_removed = false;
					is_removed = removeAppWhenInFolder( info );
					if( !is_removed )
					{
						Log.e( "cooee" , " error -- AppList3D---reomveApps --- removeAppWhenInFolder --- return false---" );
					}
				}
				// if mainmenu folder is open, close the folder first
				if( this.is_maimenufolder_open == true && mOpenFolderIcon != null )
				{
					String folderMap_key = ICON_MAP_FOLDER_LABEL + mOpenFolderIcon.getItemInfo().id;
					FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
					folderIcon.stopTween();
					// if(DefaultLayout.minu2_style_folder == true)
					// {
					// ((FolderIconMi)folderIcon).getMIUIFolder().DealButtonOKDown();
					// }
					// else
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						( folderIcon ).getMIUI3DFolder().DealButtonOKDown();
					}
					else
					{
						folderIcon.getFolder().DealButtonOKDown();
					}
					// current situation it donot need to syncApp at once
					is_need_sync_app_immediatly = false;
				}
			}
			else
			{
				iconMap.remove( infoName );
			}
			// iconMap.remove(infoName);
			// teapotXu add end for Folder in Mainmenu
			Log.d( "launcher" , "iconMap.size=" + iconMap.size() );
		}
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == false )
		{
			mApps.removeAll( list );
		}
		// teapotXu add end for Folder in Mainmenu
		ApplistVirtualIconManager.onAppsRemoved( list );
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( !is_need_sync_app_immediatly )
				return;
		}
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	// teapotXu add start for Folder in Mainmenu
	// remove the appInfo when the MainmenuFolder includes it.
	private boolean removeAppWhenInFolder(
			ApplicationInfo info )
	{
		boolean folder_info_removed = false;
		// it is the applicaion in Folder, so search the container folder,
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			ItemInfo itemInfo = mItemInfos.get( index );
			if( itemInfo instanceof UserFolderInfo )
			{
				UserFolderInfo folderInfo = (UserFolderInfo)itemInfo;
				String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
				FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
				for( ShortcutInfo folder_sInfo : folderInfo.contents )
				{
					// ShortcutInfo folder_sInfo = folder.contents.get(i);
					ApplicationInfo appInfo = folder_sInfo.appInfo;
					if( appInfo != null && ( appInfo.packageName != null && folder_sInfo.appInfo.packageName.equals( info.packageName ) ) && ( appInfo.componentName != null && folder_sInfo.appInfo.componentName
							.equals( info.componentName ) ) )
					{
						folderInfo.contents.remove( folder_sInfo );
						// then remove the info in UserFolderInfo
						{
							if( folderIcon != null )
							{
								folderIcon.onRemove( folder_sInfo );
							}
							else
							{
								Log.e( "cooee" , " error --- AppList3D--- removeAppWhenInFolder --- cannot find FolderIcon ---" );
							}
						}
						// Then also remove this iconinfo from applist db
						Root3D.deleteFromDB( folder_sInfo );
						folder_info_removed = true;
						break;
					}
				}
				if( folderInfo.contents.size() == 0 )
				{
					// remove this folder icon too
					folderInfoMap.remove( folderMap_key );
					Root3D.deleteFromDB( folderInfo );
					mItemInfos.remove( folderInfo );
				}
				if( folder_info_removed == true )
					break;
			}
		}
		return folder_info_removed;
	}
	
	// teapotXu add end for Folder in Mainmenu
	public void updateApps(
			ArrayList<ApplicationInfo> list )
	{
		for( int i = 0 ; i < list.size() ; i++ )
		{
			ApplicationInfo info = list.get( i );
			ShortcutInfo sInfo = info.makeShortcut();
			String infoName = R3D.getInfoName( sInfo );
			iconMap.remove( infoName );
			Log.d( "launcher" , "iconMap.size=" + iconMap.size() );
		}
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			boolean is_need_sync_app_immediatly = true;
			// if mainmenu folder is open, close the folder first
			if( this.is_maimenufolder_open == true && mOpenFolderIcon != null )
			{
				String folderMap_key = ICON_MAP_FOLDER_LABEL + mOpenFolderIcon.getItemInfo().id;
				FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
				folderIcon.stopTween();
				if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				{
					( folderIcon ).getMIUI3DFolder().DealButtonOKDown();
				}
				else
				{
					folderIcon.getFolder().DealButtonOKDown();
				}
				// current situation it donot need to syncApp at once
				is_need_sync_app_immediatly = false;
			}
			if( !is_need_sync_app_immediatly )
			{
				return;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	public void addWidget(
			Widget3DShortcut widget )
	{
		// if (!iLoongApplication.BuiltIn ||
		// DefaultLayout.hide_mainmenu_widget)return;
		int widgetIconPosition = -1;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof WidgetIcon )
			{
				WidgetIcon widgetIcon = (WidgetIcon)view;
				ShortcutInfo info = (ShortcutInfo)widgetIcon.getItemInfo();
				if( info.intent.getComponent().getPackageName().equals( widget.resolve_info.activityInfo.packageName ) )
				{
					mWidget3DList.remove( view );
					widgetIconPosition = i;
					break;
				}
			}
		}
		if( widgetIconPosition != -1 )
			mWidget3DList.add( widgetIconPosition , widget );
		else
			mWidget3DList.add( widget );
		syncWidgetPages();
		// syncWidget3DPages();
	}
	
	public void removeWidget(
			String packageName )
	{
		// if (!iLoongApplication.BuiltIn ||
		// DefaultLayout.hide_mainmenu_widget)return;
		int widgetIconPosition = -1;
		Widget3DShortcut shortcut = null;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof Widget3DVirtual )
			{
				Widget3DVirtual widgetIcon = (Widget3DVirtual)view;
				ShortcutInfo info = (ShortcutInfo)widgetIcon.getItemInfo();
				if( info.intent.getComponent().getPackageName().equals( packageName ) )
				{
					widgetIcon.removeHide();
					mWidget3DList.remove( view );
					break;
				}
			}
			else if( view instanceof Widget3DShortcut )
			{
				Widget3DShortcut widget = (Widget3DShortcut)view;
				if( widget.resolve_info != null && widget.resolve_info.activityInfo.packageName.equals( packageName ) )
				{
					widget.removeHide();
					mWidget3DList.remove( view );
					widgetIconPosition = i;
					shortcut = widget;
					break;
				}
			}
		}
		if( widgetIconPosition != -1 )
		{
			if( DefaultLayout.GetDefaultWidgetImage( packageName ) != null )
			{
				String imageName = DefaultLayout.THEME_WIDGET_APPLIST + DefaultLayout.GetDefaultWidgetImage( packageName );
				String name = DefaultLayout.GetDefaultWidgetName( packageName );
				int spanX = DefaultLayout.GetDefaultWidgetHSpan( packageName );
				int spanY = DefaultLayout.GetDefaultWidgetVSpan( packageName );
				ShortcutInfo info = new ShortcutInfo();
				info.title = name;
				info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
				info.intent.setComponent( new ComponentName( packageName , packageName ) );
				info.spanX = spanX;
				info.spanY = spanY;
				info.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
				// FileHandle file = ThemeManager.getInstance()
				// .getGdxTextureResource(imageName);
				// Bitmap bmp = BitmapFactory.decodeStream(file.read());
				Bitmap bmp = ThemeManager.getInstance().getBitmap( imageName );
				Widget3DVirtual icon = new Widget3DVirtual( name , bmp , name );
				icon.setItemInfo( info );
				icon.uninstall = shortcut.uninstall;
				icon.hide = shortcut.hide;
				mWidget3DList.add( widgetIconPosition , icon );
			}
		}
		syncWidgetPages();
		// syncWidget3DPages();
	}
	
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		// TODO Auto-generated method stub
		return dragObjects;
	}
	
	protected void clearDragObjs()
	{
		for( View3D view : selectedObjects )
		{
			( (Icon3D)view ).hideSelectedIcon();
		}
		selectedObjects.clear();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		boolean ret = super.onClick( x , y );
		if( !ret )
		{
			clearDragObjs();
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_WORKSPACE , x , y );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		return ret;
	}
	
	public void show()
	{
		super.show();
		Color c = indicatorView.getColor();
		indicatorView.setColor( c.r , c.g , c.b , 0 );
		indicatorView.show();
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 1.0f, 0, 0);
		viewParent.onCtrlEvent( this , APP_LIST3D_SHOW );
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			if( page_index >= appPageCount )
			{
				SendMsgToAndroid.sendWaitClingMsg();
			}
			else
			{
				SendMsgToAndroid.sendCancelWaitClingMsg();
			}
		}
		hideFocus( page_index );// zqh
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			this.is_maimenufolder_open = false;
			if( force_applist_refesh == true )
			{
				// 强制排序
				sortApp( sortId , false );
				syncAppsPages();
				force_applist_refesh = false;
			}
		}
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( ( mItemInfos == null || mItemInfos.size() == 0 ) && toastLoad )
			{
				toastLoad = false;
				SendMsgToAndroid.sendCircleToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.applist_load_toast ) );
			}
		}
		else
		{
			if( ( mApps == null || mApps.size() == 0 ) && toastLoad )
			{
				toastLoad = false;
				SendMsgToAndroid.sendCircleToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.applist_load_toast ) );
			}
		}
		// teapotXu add end for Folder in Mainmenu
	}
	
	public void justHide()
	{
		super.hide();
	}
	
	@Override
	public void hide()
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true && this.applist_hide_reason == this.APP_HIDE_REASON_FOR_FOLDER_OPEN )
		{
			Log.v( "cooee" , "AppList3d ----- hide() -----app list hide because folder in Mainmenu wants to open " );
		}
		else
			viewParent.onCtrlEvent( this , APP_LIST3D_HIDE );
		// viewParent.onCtrlEvent(this, APP_LIST3D_HIDE);
		// teapotXu add end for Folder in Mainmenu
		clearDragObjs();
		super.hide();
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK || keycode == KeyEvent.KEYCODE_MENU )
			return true;
		/**
		 * zqh start on 11-15-2012
		 * 
		 * **/
		if( DefaultLayout.keypad_event_of_focus )
		{
			if( AppPopMenu2.isVisible || AppPopMenu2.origin )
			{
				AppPopMenu2.origin = false;
				return true;
			}
			if( hideFocus )
			{
				hideFocus = false;
				firstlyCome = false;
				return false;
			}
			if( keycode == KeyEvent.KEYCODE_DPAD_CENTER )
			{
				onKeySelect();
				return true;// super.keyDown(keycode);
			}
			// updates focus
			updateFocus( page_index , keycode );
			// process down event
			/** zqh end **/
		}
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( inited )
			{
				if( mode != APPLIST_MODE_NORMAL )
				{
					setMode( APPLIST_MODE_NORMAL );
				}
				else
					viewParent.onCtrlEvent( this , APP_LIST3D_KEY_BACK );
			}
			return true;
		}
		return super.keyUp( keycode );
	}
	
	@Override
	public boolean visible()
	{
		return this.isVisible() && page_index < appPageCount;
	}
	
	@Override
	public int getClingPriority()
	{
		return ClingManager.SELECT_CLING;
	}
	
	@Override
	public void dismissCling()
	{
		clingState = ClingManager.CLING_STATE_DISMISSED;
	}
	
	@Override
	public void setPriority(
			int priority )
	{
		// TODO Auto-generated method stub
	}
	
	public void hideFocus(
			int page )
	{
		int numCells = mCellCountX * mCellCountY;
		currentCell = page * numCells;
		firstlyCome = true;
		hideFocus = true;
	}
	
	/**
	 * to get location that focus image will be show in
	 * 
	 * @param :
	 * **/
	public void getFocusLocation(
			int focus )
	{
		// teapotXu add start for Folder in Mainmenu
		View3D icon = null;
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			ItemInfo info = getItemInfo( currentCell );
			if( info instanceof ApplicationInfo )
			{
				ShortcutInfo sInfo = ( (ApplicationInfo)info ).makeShortcut();
				String infoName = R3D.getInfoName( sInfo );
				icon = iconMap.get( infoName );
			}
			else if( info instanceof FolderInfo )
			{
				UserFolderInfo folderInfo = (UserFolderInfo)info;
				String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
				icon = folderInfoMap.get( folderMap_key );
			}
		}
		else
		// teapotXu add end for Folder in Mainmenu
		{
			ApplicationInfo info = getApp( currentCell );
			ShortcutInfo sInfo = info.makeShortcut();
			String infoName = R3D.getInfoName( sInfo );
			icon = iconMap.get( infoName );
		}
		// ApplicationInfo info = getApp(currentCell);
		// ShortcutInfo sInfo = info.makeShortcut();
		// String infoName = R3D.getInfoName(sInfo);
		// AppIcon3D icon = iconMap.get(infoName);
		// teapotXu add end for Folder in Mainmenu
		locationX = icon.x;
		locationY = icon.y;
		iconHeight = icon.height;
		iconWidth = icon.width;
	}
	
	/**/
	public void onKeySelect()
	{
		if( hideFocus == false )
		{
			// teapotXu add start for Folder in Mainmenu
			View3D icon = null;
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				ItemInfo info = getItemInfo( currentCell );
				if( info instanceof ApplicationInfo )
				{
					ShortcutInfo sInfo = ( (ApplicationInfo)info ).makeShortcut();
					String infoName = R3D.getInfoName( sInfo );
					icon = iconMap.get( infoName );
				}
				else if( info instanceof FolderInfo )
				{
					UserFolderInfo folderInfo = (UserFolderInfo)info;
					String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
					icon = folderInfoMap.get( folderMap_key );
				}
			}
			else
			// teapotXu add end for Folder in Mainmenu
			{
				ApplicationInfo info = getApp( currentCell );
				ShortcutInfo sInfo = info.makeShortcut();
				String infoName = R3D.getInfoName( sInfo );
				icon = iconMap.get( infoName );
			}
			// ApplicationInfo info = getApp(currentCell);
			// ShortcutInfo sInfo = info.makeShortcut();
			// String infoName = R3D.getInfoName(sInfo);
			// AppIcon3D icon = iconMap.get(infoName);
			// teapotXu add end for Folder in Mainmenu
			icon.onClick( locationX + iconWidth / 2 , locationY + iconHeight / 2 );
		}
	}
	
	public void onKeyEven(
			int page )
	{
		hideFocus( page );
		scrollTo( page );
		setVisible();
	}
	
	public void hideCurrPageFocus()
	{
		hideFocus( page_index );
	}
	
	public void setInvisible()
	{
		hideFocus = true;
		firstlyCome = true;
	}
	
	public void setVisible()
	{
		hideFocus = false;
	}
	
	protected void updateFocus(
			int pageIndex ,
			int direction )
	{
		int numCells = mCellCountX * mCellCountY;
		int startIndex = pageIndex * numCells;
		int endIndex = Math.min( startIndex + numCells , getAppCount() );
		int temFocus = 0;
		int lastIndex = currentCell;
		int appCount = getAppCount();
		switch( direction )
		{
			case KeyEvent.KEYCODE_DPAD_UP:
				if( currentCell < startIndex + mCellCountX )
				{
				}
				else
				{
					//
					currentCell = currentCell - mCellCountX;
					if( currentCell >= appCount )
					{
						currentCell = lastIndex;
						return;
					}
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if( ( ( currentCell - startIndex + 1 ) % mCellCountX ) == 0 )
				{
					if( page_index < appPageCount - 1 )
					{
						onKeyEven( page_index + 1 );
					}
					else
					{
						onKeyEven( 0 );
					}
				}
				else
				{
					currentCell = currentCell + 1;
					if( currentCell >= appCount )
					{
						currentCell = lastIndex;
						return;
					}
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if( currentCell < endIndex - mCellCountX )
				{
					currentCell = currentCell + mCellCountX;
					if( currentCell >= appCount )
					{
						currentCell = lastIndex;
						return;
					}
				}
				else
				{
					// temFocus=(currentCell - startIndex)%numCells;
					// currentCell = temFocus+startIndex;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if( ( ( currentCell - startIndex + 1 ) % mCellCountX ) == 1 )
				{
					if( page_index == getPageNum() - 1 )
						return;
					if( page_index > 0 )
						onKeyEven( page_index - 1 );
					else
						onKeyEven( appPageCount - 1 );// show final page.
				}
				else
				{
					currentCell = currentCell - 1;
					if( currentCell >= appCount )
					{
						currentCell = lastIndex;
						return;
					}
				}
				break;
			default:
				break;
		}
		Log.v( "LauncherFocus" , "currentCell :" + currentCell );
	}
	
	/** zqh end **/
	public int estimateWidgetCellWidth(
			int cellHSpan )
	{
		if( cellHSpan > 4 )
			cellHSpan = 4;
		float widgetWidth = (float)( width - R3D.applist_padding_left - R3D.applist_padding_right ) / ( (float)mWidgetCountX ) - R3D.app_widget3d_gap;
		float widgetCellWidth = widgetWidth / ( (float)4 );
		return (int)( widgetCellWidth * ( (float)cellHSpan ) );
	}
	
	public int estimateWidgetCellHeight(
			int cellVSpan )
	{
		if( cellVSpan > 4 )
			cellVSpan = 4;
		float widgetHeight = ( (float)( height - R3D.applist_padding_top - R3D.applist_padding_bottom ) / ( (float)mWidgetCountY ) - R3D.app_widget3d_gap ) * ( 1 - R3D.widget_preview_title_weight );
		float widgetCellHeight = widgetHeight / ( (float)4 );
		return (int)( widgetCellHeight * ( (float)cellVSpan ) );
	}
	
	public void sortApp(
			int checkId ,
			boolean refresh )
	{
		if( !Desktop3DListener.bAppDone )
			return;
		if( checkId != sortId )
		{
			sortId = checkId;
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putInt( "sort_app" , sortId ).commit();
		}
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			sortArray = new int[mItemInfos.size()];
		}
		else
		{
			sortArray = new int[mApps.size()];
		}
		// sortArray = new int[mApps.size()];
		// teapotXu add end for Folder in Mainmenu
		switch( sortId )
		{
			case SORT_DEFAULT:
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					String[] titleKey = new String[mItemInfos.size()];
					boolean[] priorityKey = new boolean[mItemInfos.size()];
					for( int i = 0 ; i < mItemInfos.size() ; i++ )
					{
						ItemInfo itemInfo = mItemInfos.get( i );
						if( mItemInfos.get( i ) instanceof ApplicationInfo )
						{
							ApplicationInfo appInfo = (ApplicationInfo)itemInfo;
							priorityKey[i] = false;
							if( ( appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
							{
								titleKey[i] = "0000000";
								priorityKey[i] = true;
							}
							else
							{
								titleKey[i] = appInfo.title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
								if( DefaultLayout.getInstance().hasReplaceIcon( appInfo.componentName.getPackageName() , appInfo.componentName.getClassName() ) )
								{
									priorityKey[i] = true;
								}
							}
							// xiatian add end
						}
						else
						{
							if( ( (FolderInfo)itemInfo ).sort_folder_name != null )
							{
								titleKey[i] = ( (FolderInfo)itemInfo ).sort_folder_name.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
								if( ( (FolderInfo)itemInfo ).sort_folder_name.toString().contains( "_aaaa" ) )
								{
									priorityKey[i] = true;
								}
								else
								{
									priorityKey[i] = false;
								}
							}
							else
							{
								titleKey[i] = ( (FolderInfo)itemInfo ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
							}
						}
					}
					cut.sortByDefault( 1 , titleKey , priorityKey , sortArray );
				}
				else
				{
					String[] titleKey = new String[mApps.size()];
					boolean[] priorityKey = new boolean[mApps.size()];
					for( int i = 0 ; i < mApps.size() ; i++ )
					{
						priorityKey[i] = false;
						if( ( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
						{
							titleKey[i] = "0000000";
							priorityKey[i] = true;
						}
						else
						{
							titleKey[i] = mApps.get( i ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
							if( DefaultLayout.getInstance().hasReplaceIcon( mApps.get( i ).componentName.getPackageName() , mApps.get( i ).componentName.getClassName() ) )
							{
								priorityKey[i] = true;
							}
						}
					}
					cut.sortByDefault( 1 , titleKey , priorityKey , sortArray );
				}
				break;
			case SORT_NAME:
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					String[] nameKey = new String[mItemInfos.size()];
					for( int i = 0 ; i < mItemInfos.size() ; i++ )
					{
						ItemInfo itemInfo = mItemInfos.get( i );
						if( itemInfo instanceof ApplicationInfo )
						{
							if( ( (ApplicationInfo)itemInfo ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
								nameKey[i] = "0000000";
							else
								nameKey[i] = itemInfo.title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
						}
						else
						{
							// when select item is a folder
							nameKey[i] = ( (FolderInfo)itemInfo ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );
						}
					}
					cut.sortByAlpha( 1 , nameKey , sortArray );
				}
				else
				// teapotXu add end for Folder in Mainmenu
				{
					String[] nameKey = new String[mApps.size()];
					for( int i = 0 ; i < mApps.size() ; i++ )
					{
						if( ( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
							nameKey[i] = "0000000";
						else
							nameKey[i] = mApps.get( i ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一�?
					}
					cut.sortByAlpha( 1 , nameKey , sortArray );
				}
				break;
			case SORT_INSTALL:
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					int[] installKey = new int[mItemInfos.size()];
					for( int i = 0 ; i < mItemInfos.size() ; i++ )
					{
						ItemInfo itemInfo = mItemInfos.get( i );
						if( itemInfo instanceof ApplicationInfo )
						{
							ApplicationInfo appInfo = (ApplicationInfo)itemInfo;
							if( ( appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
								installKey[i] = 2147483647;
							else
								installKey[i] = (int)( ( (ApplicationInfo)itemInfo ).lastUpdateTime / 1000 );
						}
						else
						{
							// when select item is a folder
							if( itemInfo instanceof FolderInfo )
							{
								installKey[i] = (int)( ( (FolderInfo)itemInfo ).lastUpdateTime / 1000 );
							}
						}
					}
					cut.sort( 0 , installKey , sortArray );
					// cut.sort(1, installKey, sortArray);
				}
				else
				// teapotXu add end for Folder in Mainmenu
				{
					int[] installKey = new int[mApps.size()];
					for( int i = 0 ; i < mApps.size() ; i++ )
					{
						if( ( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
							installKey[i] = 2147483647;
						else
							installKey[i] = (int)( mApps.get( i ).lastUpdateTime / 1000 );
					}
					cut.sort( 0 , installKey , sortArray );
				}
				break;
			case SORT_USE:
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					int[] useKey = new int[mItemInfos.size()];
					for( int i = 0 ; i < mItemInfos.size() ; i++ )
					{
						ItemInfo itemInfo = mItemInfos.get( i );
						if( itemInfo instanceof ApplicationInfo )
						{
							if( ( ( (ApplicationInfo)itemInfo ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
								useKey[i] = 2147483647;
							else
								useKey[i] = (int)( ( (ApplicationInfo)itemInfo ).getUseFrequency() / 1000 );
						}
						else
						{
							// when select item is a folder
							if( itemInfo instanceof FolderInfo )
							{
								useKey[i] = (int)( ( (FolderInfo)itemInfo ).use_frequency / 1000 );
							}
						}
					}
					cut.sort( 0 , useKey , sortArray );
				}
				else
				{
					int[] useKey = new int[mApps.size()];
					for( int i = 0 ; i < mApps.size() ; i++ )
					{
						if( ( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
							useKey[i] = 2147483647;
						else
							useKey[i] = (int)( mApps.get( i ).getUseFrequency() );
					}
					cut.sort( 0 , useKey , sortArray );
				}
				// teapotXu add end for Folder in Mainmenu
				break;
			case SORT_FACTORY:
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					int i , j;
					int[] facKey = new int[mItemInfos.size()];
					int facAppNum = DefaultLayout.facApp.size();
					boolean is_exist;
					for( i = 0 ; i < mItemInfos.size() ; i++ )
					{
						is_exist = false;
						for( j = 0 ; j < facAppNum ; j++ )
						{
							for( int k = 0 ; k < DefaultLayout.facApp.get( j ).pkgNameArray.size() ; k++ )
							{
								ItemInfo appInfo = (ItemInfo)mItemInfos.get( i );
								if( appInfo instanceof ApplicationInfo )
								{
									if( ( (ApplicationInfo)appInfo ).packageName.equals( DefaultLayout.facApp.get( j ).pkgNameArray.get( k ) ) )
									{
										if( DefaultLayout.facApp.get( j ).className == null || ( (ApplicationInfo)appInfo ).componentName.getClassName().equals(
												DefaultLayout.facApp.get( j ).className ) )
										{
											facKey[i] = j - facAppNum;
											is_exist = true;
											break;
										}
									}
								}
								else
								{
									// when select item is a folder
									// 此处一般情况下不会运行，暂不处理
								}
							}
							if( is_exist )
								break;
						}
						if( j == facAppNum )
						{
							if( mItemInfos.get( i ) instanceof ApplicationInfo )
							{
								ApplicationInfo appInfo = (ApplicationInfo)mItemInfos.get( i );
								if( ( appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
									facKey[i] = 0;
								else
									facKey[i] = (int)( appInfo.lastUpdateTime / 1000 );
							}
							else
							{
								// when select item is a folder
								ItemInfo app_itemInfo = mItemInfos.get( i );
								if( app_itemInfo instanceof FolderInfo )
								{
									facKey[i] = (int)( ( (FolderInfo)app_itemInfo ).lastUpdateTime / 1000 );
								}
							}
						}
					}
					cut.sort( 1 , facKey , sortArray );
				}
				else
				{
					int i , j;
					int[] facKey = new int[mApps.size()];
					int facAppNum = DefaultLayout.facApp.size();
					boolean is_exist;
					for( i = 0 ; i < mApps.size() ; i++ )
					{
						is_exist = false;
						for( j = 0 ; j < facAppNum ; j++ )
						{
							for( int k = 0 ; k < DefaultLayout.facApp.get( j ).pkgNameArray.size() ; k++ )
							{
								if( mApps.get( i ).packageName.equals( DefaultLayout.facApp.get( j ).pkgNameArray.get( k ) ) )
								{
									if( DefaultLayout.facApp.get( j ).className == null || mApps.get( i ).componentName.getClassName().equals( DefaultLayout.facApp.get( j ).className ) )
									{
										facKey[i] = j - facAppNum;
										is_exist = true;
										break;
									}
								}
							}
							if( is_exist )
								break;
						}
						if( j == facAppNum )
						{
							if( ( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
								facKey[i] = 0;
							else
								facKey[i] = (int)( mApps.get( i ).lastUpdateTime / 1000 );
						}
					}
					cut.sort( 1 , facKey , sortArray );
				}
				// teapotXu add end for Folder in Mainmenu
				break;
		}
		// SharedPreferences prefs =
		// iLoongLauncher.getInstance().getSharedPreferences("appsort",Activity.MODE_PRIVATE);
		// Editor edit = prefs.edit();
		// for(int i = 0;i < sortArray.length && i < mApps.size();i++){
		// int value = sortArray[i];
		// ComponentName component = mApps.get(value).componentName;
		// edit.putInt(component.toString(), i);
		// }
		// edit.commit();
		if( refresh )
		{
			syncAppsPages();
			startAnimation();
		}
	}
	
	public void setAppBar(
			AppBar3D appBar )
	{
		this.appBar = appBar;
	}
	
	protected int nextIndex()
	{
		if( mScrollToWidget )
		{
			return super.nextIndex();
		}
		else
		{
			if( appBar == null )
			{
				if( page_index == appPageCount - 1 )
				{
					return 0;
				}
				else
				{
					return page_index + 1;
				}
			}
			else
			{
				if( this.appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
				{
					if( page_index == appPageCount - 1 )
					{
						return 0;
					}
					else
					{
						return page_index + 1;
					}
				}
				else
				{
					if( page_index == appPageCount + widgetPageCount - 1 )
					{
						return appPageCount;
					}
					else
					{
						return page_index + 1;
					}
				}
			}
		}
	}
	
	protected int preIndex()
	{
		if( mScrollToWidget )
		{
			return super.preIndex();
		}
		else
		{
			if( appBar == null )
			{
				if( page_index == 0 )
				{
					return appPageCount - 1;
				}
				else
				{
					return page_index - 1;
				}
			}
			else
			{
				if( this.appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
				{
					if( page_index == 0 )
					{
						return appPageCount - 1;
					}
					else
					{
						return page_index - 1;
					}
				}
				else
				{
					if( page_index == appPageCount )
					{
						return appPageCount + widgetPageCount - 1;
					}
					else
					{
						return page_index - 1;
					}
				}
			}
		}
		// return (page_index == 0 ? view_list.size() - 1 : page_index - 1);
	}
	
	protected void updateEffect()
	{
		// Log.v("AppList3D", "updateEffect");
		if( view_list.size() == 0 )
			return;
		int preIndex = preIndex();
		int nextIndex = nextIndex();
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( preIndex < 0 )
		{
			return;
		}
		if( nextIndex < 0 )
		{
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( preIndex > view_list.size() - 1 )
		{
			return;
		}
		if( nextIndex > view_list.size() - 1 )
		{
			return;
		}
		if( needLayout )
		{
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					( (GridView3D)i ).layout_pub( 0 , false );
				}
			}
			needLayout = false;
		}
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex );
		if( !moving )
		{
			changeEffect();
			moving = true;
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					for( int j = 0 ; j < ( (GridView3D)i ).getChildCount() ; j++ )
					{
						View3D icon = ( (GridView3D)i ).getChildAt( j );
						icon.setTag( new Vector2( icon.getX() , icon.getY() ) );
					}
				}
			}
		}
		float tempYScale = 0;
		if( needXRotation )
		{
			if( yScale > MAX_X_ROTATION )
			{
				tempYScale = MAX_X_ROTATION;
			}
			else if( yScale < -MAX_X_ROTATION )
			{
				tempYScale = -MAX_X_ROTATION;
			}
			else
			{
				tempYScale = yScale;
			}
		}
		tempYScale = -tempYScale;
		if( super.getRandom() == false && this.mType == 0 )
		{
			APageEase.setStandard( true );
		}
		else
		{
			APageEase.setStandard( false );
		}
		// Log.v("abc",
		// "xScale="+xScale+" yScale="+yScale+" pre="+pre_view.getChildAt(0).name+" cur="+cur_view.getChildAt(0).name+" next="+next_view.getChildAt(0).name);
		if( xScale > 0 )
		{
			next_view.hide();
			// teapotXu_20130307 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				APageEase.setScrolldirection( false );
			}
			// teapotXu_20130307: add end
			// initData(next_view);
			if( mScrollToWidget )
			{
				if( page_index == 0 || page_index >= appPageCount )
				{
					APageEase.updateEffect( pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
				else
					APageEase.updateEffect( pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
			}
			else
			{
				if( page_index >= appPageCount )
				{
					APageEase.updateEffect( pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
				else
					APageEase.updateEffect( pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
			}
		}
		else if( xScale < 0 )
		{
			pre_view.hide();
			// initData(pre_view);
			// teapotXu_20130307 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				APageEase.setScrolldirection( true );
			}
			// teapotXu_20130307: add end
			if( mScrollToWidget )
			{
				if( page_index >= appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 )
				{
					APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
				else
					APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
			}
			else
			{
				if( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 )
				{
					APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
				else
					APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
			}
		}
		else if( yScale != 0 )
		{
			if( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 )
			{
				APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
			}
			else
				APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
		}
		if( xScale < -1f )
		{
			cur_view.hide();
			// initData(cur_view);
			page_index = nextIndex();
			setDegree( xScale + 1f );
			changeEffect();
		}
		if( xScale > 1f )
		{
			cur_view.hide();
			// initData(cur_view);
			page_index = preIndex();
			changeEffect();
		}
	}
	
	public View3D getFirstIcon()
	{
		if( view_list.size() < 1 )
			return null;
		ViewGroup3D vg = (ViewGroup3D)view_list.get( 0 );
		if( vg.getChildCount() < 1 )
			return null;
		if( vg.getChildAt( 0 ) == null || !( vg.getChildAt( 0 ) instanceof Icon3D ) )
			return null;
		return vg.getChildAt( 0 );
	}
	
	public int getIconGap()
	{
		if( view_list.size() < 1 )
			return -1;
		ViewGroup3D vg = (ViewGroup3D)view_list.get( 0 );
		if( vg.getChildCount() < 2 )
			return -1;
		if( vg.getChildAt( 0 ) == null || !( vg.getChildAt( 0 ) instanceof Icon3D ) )
			return -1;
		if( vg.getChildAt( 1 ) == null || !( vg.getChildAt( 1 ) instanceof Icon3D ) )
			return -1;
		return (int)( vg.getChildAt( 1 ).x - vg.getChildAt( 0 ).x );
	}
	
	@Override
	protected void finishAutoEffect()
	{
		indicatorView.finishAutoEffect();
		super.finishAutoEffect();
		if( page_index >= appPageCount )
		{
			SendMsgToAndroid.sendWaitClingMsg();
		}
		else
		{
			SendMsgToAndroid.sendCancelWaitClingMsg();
		}
		hideFocus( page_index );// zqh
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		setInvisible();// zqh
		// teapotXu add start for Folder in Mainmenu
		// while syncApp's animation is running, we don't response the touch
		// event in Applist
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( syncAppAnimFinished == false )
			{
				Log.v( "cooee" , "AppList3D---- onTouchDown--- invalid ---because syncApp's animation is running" );
				return true;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		// TODO Auto-generated method stub
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_WORKSPACE );
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		if( !mScrollToWidget )
		{
			if( appBar == null )
			{
				if( widgetPageCount + appPageCount == 1 )
					return true;
			}
			else
			{
				if( this.appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET )
				{
					if( widgetPageCount == 1 )
					{
						return true;
					}
				}
				else
				{
					if( appPageCount == 1 )
					{
						return true;
					}
				}
			}
		}
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// Log.v("AppList3D", "scroll: x:" + x + " y:" + y + " deltaX:" + deltaX
		// + " deltaY:" + deltaY + " xScale:" + xScale);
		if( !mScrollToWidget )
		{
			if( appBar == null )
			{
				if( widgetPageCount + appPageCount == 1 )
					return true;
			}
			else
			{
				if( this.appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET )
				{
					if( widgetPageCount == 1 )
					{
						return true;
					}
				}
				else
				{
					if( appPageCount == 1 )
					{
						return true;
					}
				}
			}
		}
		indicatorView.setAlpha( 1.0f );
		if( indicatorView.getIndicatorTween() != null && !indicatorView.getIndicatorTween().isFinished() )
		{
			indicatorView.getIndicatorTween().free();
			indicatorView.setIndicatorTween( null );
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	class GridPool
	{
		
		private ArrayList<GridView3D> grids;
		private float width;
		private float height;
		private int countX;
		private int countY;
		
		public GridPool(
				int initCapacity ,
				float width ,
				float height ,
				int countX ,
				int countY )
		{
			grids = new ArrayList<GridView3D>( initCapacity );
			this.width = width;
			this.height = height;
			this.countX = countX;
			this.countY = countY;
		}
		
		private GridView3D create()
		{
			GridView3D grid = new GridView3D( "allapplist" , width , height , countX , countY );
			grid.setPadding( R3D.applist_padding_left , R3D.applist_padding_right , R3D.applist_padding_top , R3D.applist_padding_bottom );
			if( mCellCountX == 3 )
			{
				grid.setPadding( R3D.applist_padding_left_ex , R3D.applist_padding_right_ex , R3D.applist_padding_top , R3D.applist_padding_bottom );
			}
			return grid;
		}
		
		public GridView3D get()
		{
			GridView3D grid = grids.isEmpty() ? create() : grids.remove( grids.size() - 1 );
			return grid;
		}
		
		public void free(
				GridView3D grid )
		{
			if( !grids.contains( grid ) )
			{
				grid.removeAllViews();
				grids.add( grid );
			}
		}
	}
	
	class AppIcon3D extends Icon3D
	{
		
		public int oldAppGridIndex;
		public int newAppGridIndex;
		public float oldX;
		public float oldY;
		public boolean oldVisible = false;
		
		public AppIcon3D(
				String name ,
				TextureRegion region )
		{
			super( name , region );
			// TODO Auto-generated constructor stub
		}
	}
	
	public void resume()
	{
		sortApp( sortId , false );
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			// when resume, if the folder is open, there is no need to
			// syncAppsPages()
			if( this.is_maimenufolder_open && this.mOpenFolderIcon != null )
			{
				Log.d( "cooee" , "AppList3D ----- resume() ----- donot sysncAppsPages  because folder in Mainmenu is open !----" );
				return;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		syncAppsPages();
	}
	
	public void addAppNoRepeat(
			ArrayList<ApplicationInfo> apps ,
			ArrayList<ApplicationInfo> adds )
	{
		for( int i = 0 ; i < adds.size() ; i++ )
		{
			if( apps.contains( adds ) )
				continue;
			apps.add( adds.get( i ) );
		}
	}
	
	// teapotXu add start for Folder in Mainmenu
	public void addAppIntoItemInfoNoRepeat(
			ArrayList<ItemInfo> itemInfos ,
			ArrayList<ApplicationInfo> adds )
	{
		for( int i = 0 ; i < adds.size() ; i++ )
		{
			if( itemInfos.contains( adds ) )
				continue;
			itemInfos.add( adds.get( i ) );
		}
	}
	
	public void addItemIntoItemInfoNoRepeat(
			ArrayList<ItemInfo> itemInfos ,
			ArrayList<ItemInfo> adds )
	{
		for( int i = 0 ; i < adds.size() ; i++ )
		{
			if( itemInfos.contains( adds ) )
				continue;
			itemInfos.add( adds.get( i ) );
		}
	}
	
	// teapotXu add end for Folder in Mainmenu
	public void addApps(
			ArrayList<ApplicationInfo> list ,
			boolean isSortApps ,
			boolean isSynchronization )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			boolean is_need_sync_app = true;
			// when addApp, if folder is open,close the folder first
			if( this.is_maimenufolder_open && this.mOpenFolderIcon != null )
			{
				String folderMap_key = ICON_MAP_FOLDER_LABEL + mOpenFolderIcon.getItemInfo().id;
				FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
				if( folderIcon != null )
				{
					folderIcon.stopTween();
					{
						if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
						{
							( folderIcon ).getMIUI3DFolder().DealButtonOKDown();
						}
						else
						{
							folderIcon.getFolder().DealButtonOKDown();
						}
					}
				}
				is_need_sync_app = false;
			}
			// mItemInfos.addAll(list);
			addAppIntoItemInfoNoRepeat( mItemInfos , list );
			if( !is_need_sync_app )
				return;
		}
		else
		{
			addAppNoRepeat( mApps , list );
		}
		// addAppNoRepeat(mApps,list);
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , isSortApps );
		if( isSynchronization == false )
		{
			return;
		}
		syncAppsPages();
		startAnimation();
	}
	
	public void removeVirtualApps(
			ApplicationInfo app ,
			boolean permanent ,
			boolean isSortApps ,
			boolean isSynchronization )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			for( int i = 0 ; i < mItemInfos.size() ; i++ )
			{
				ItemInfo itemInfo = mItemInfos.get( i );
				if( itemInfo instanceof ApplicationInfo )
				{
					if( ( (ApplicationInfo)itemInfo ).packageName.equals( app.packageName ) && ( (ApplicationInfo)itemInfo ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) )
					{
						mItemInfos.remove( i );
						break;
					}
				}
			}
		}
		else
		{
			for( int i = 0 ; i < mApps.size() ; i++ )
			{
				if( mApps.get( i ).packageName.equals( app.packageName ) && mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) )
				{
					mApps.remove( i );
					break;
				}
			}
		}
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , isSortApps );
		if( isSynchronization == false )
		{
			return;
		}
		syncAppsPages();
		startAnimation();
	}
	
	public void addVirtualApps(
			ArrayList<ApplicationInfo> list )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			addAppIntoItemInfoNoRepeat( mItemInfos , list );
		}
		else
			// teapotXu add end for Folder in Mainmenu
			addAppNoRepeat( mApps , list );
	}
	
	public void removeVirtualApps(
			ArrayList<ApplicationInfo> apps )
	{
		for( int j = 0 ; j < apps.size() ; j++ )
		{
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				for( int i = 0 ; i < mItemInfos.size() ; i++ )
				{
					ItemInfo itemInfo = mItemInfos.get( i );
					if( itemInfo instanceof ApplicationInfo )
					{
						if( ( (ApplicationInfo)itemInfo ).packageName.equals( apps.get( j ).packageName ) && ( (ApplicationInfo)itemInfo ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) )
						{
							mItemInfos.remove( i );
							break;
						}
					}
				}
			}
			else
			{
				for( int i = 0 ; i < mApps.size() ; i++ )
				{
					if( mApps.get( i ).packageName.equals( apps.get( j ).packageName ) && mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) )
					{
						mApps.remove( i );
						break;
					}
				}
			}
		}
	}
	
	public void reomveApps(
			ArrayList<ApplicationInfo> list ,
			boolean permanent ,
			boolean isSortApps ,
			boolean isSynchronization )
	{
		boolean is_need_sync_app_immediatly = true; // teapotXu add for Folder
													// in Mainmenu
		for( int i = 0 ; i < list.size() ; i++ )
		{
			if( permanent )
				list.get( i ).removeUseFrequency();
			if( permanent )
				list.get( i ).removeHide();
			ApplicationInfo info = list.get( i );
			ShortcutInfo sInfo = info.makeShortcut();
			String infoName = R3D.getInfoName( sInfo );
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				if( isUserApp( info.flags ) )
				{
					Icon3D icon_1 = iconMap.get( infoName );
					if( icon_1 != null )
					{
						icon_1.clearState();
					}
					iconMap.remove( infoName );
				}
				else
				{
					if( iconMap.get( infoName ) != null )
					{
						iconMap.get( infoName ).clearState();
					}
					iconMap.remove( infoName );
				}
				if( mItemInfos.contains( info ) )
				{
					// current info is instanceof Application, and not in
					// Folder, so remove it directly
					mItemInfos.remove( info );
				}
				else
				{
					boolean is_removed = false;
					is_removed = removeAppWhenInFolder( info );
					if( !is_removed )
					{
						Log.e( "cooee" , " error -- AppList3D---reomveApps --- removeAppWhenInFolder --- return false---" );
					}
				}
				// if mainmenu folder is open, close the folder first
				if( this.is_maimenufolder_open == true && mOpenFolderIcon != null )
				{
					String folderMap_key = ICON_MAP_FOLDER_LABEL + mOpenFolderIcon.getItemInfo().id;
					FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
					folderIcon.stopTween();
					{
						if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
						{
							( folderIcon ).getMIUI3DFolder().DealButtonOKDown();
						}
						else
						{
							folderIcon.getFolder().DealButtonOKDown();
						}
					}
					// current situation it donot need to syncApp at once
					is_need_sync_app_immediatly = false;
				}
			}
			else
			{
				iconMap.remove( infoName );
			}
			// iconMap.remove(infoName);
			// teapotXu add end for Folder in Mainmenu
			Log.d( "launcher" , "iconMap.size=" + iconMap.size() );
		}
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == false )
		{
			mApps.removeAll( list );
		}
		// teapotXu add end for Folder in Mainmenu
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( is_need_sync_app_immediatly == false )
				return;
		}
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , isSortApps );
		if( isSynchronization == false )
		{
			return;
		}
		syncAppsPages();
		startAnimation();
	}
	
	// xiatian add start //Mainmenu Bg
	private void initBgIconName()
	{
		bg_icon_name.put( 0 , "theme/pack_source/translucent-bg.png" );
		bg_icon_name.put( 1 , "theme/pack_source/translucent-bg-opa.png" );
		bg_icon_name.put( 2 , "theme/pack_source/translucent-black.png" );
	}
	
	private void refreshBg()
	{
		String mainmenu_bg_key = iLoongLauncher.getInstance().getResources().getString( RR.string.mainmenu_bg_key );
		String mainmenu_bg_value = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( mainmenu_bg_key , "-1" );
		if( mainmenu_bg_value.equals( "-1" ) )
		{
			PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putString( mainmenu_bg_key , DefaultLayout.defaultMainmenuBgIndex + "" ).commit();
			mainmenu_bg_value = DefaultLayout.defaultMainmenuBgIndex + "";
			DefaultLayout.lastAppListMainmenuBgIndex = DefaultLayout.defaultMainmenuBgIndex;
		}
		else if( ( mainmenu_bg_value.equals( DefaultLayout.lastAppListMainmenuBgIndex + "" ) ) && ( translucentBg != null ) )
		{
			return;
		}
		else
		{
			DefaultLayout.lastAppListMainmenuBgIndex = Integer.valueOf( mainmenu_bg_value ).intValue();
		}
		int mainmenu_bg_index = Integer.valueOf( mainmenu_bg_value ).intValue();
		String bg_name = bg_icon_name.get( mainmenu_bg_index );
		Bitmap bmp = ThemeManager.getInstance().getBitmap( bg_name );
		if( bmp.getConfig() != Config.ARGB_8888 )
		{
			bmp = bmp.copy( Config.ARGB_8888 , false );
		}
		Texture t = new BitmapTexture( bmp );
		translucentBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
		bmp.recycle();
	}
	
	// xiatian add end
	// teapotXu add start for Folder in Mainmenu
	public boolean addDrageViewIntoAppList(
			ArrayList<View3D> list ,
			int x ,
			int y )
	{
		// TODO Auto-generated method stub
		boolean res = true;
		ItemInfo itemInfo;
		InitDragLayerData();// clearDragTemp();
		int realY = (int)y;
		if( list.size() == 1 )
		{
			if( cellDropType != CELL_DROPTYPE_SINGLE_DROP_FOLDER )
				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
		}
		else
		{
			// now donot support size>1
			return true;
		}
		switch( cellDropType )
		{
			case CELL_DROPTYPE_SINGLE_DROP:
				// case CELL_DROPTYPE_FOLDER:
			default:
				for( View3D i : list )
				{
					// 此处应该判断当前的拖动是否从文件夹拖出来的，
					// 如果是文件夹里拖出来的icon，应该增加cur_view 的整体个数，并且把该icon放置最后的位置。
					// 否则，回到原先的icon位置。
					if( ( (Icon3D)i ).getInShowFolder() == true )// if(this.is_dragview_from_folder
																	// == true)
					{
						Icon3D icon = (Icon3D)i;
						if( icon.info != null && icon.info instanceof ShortcutInfo )
						{
							// 获取到quiet view 对应的ApplicationInfo
							ApplicationInfo view_applicationInfo = ( (ShortcutInfo)( (Icon3D)i ).getItemInfo() ).appInfo;
							ArrayList<ApplicationInfo> add_appList = new ArrayList<ApplicationInfo>();
							add_appList.add( view_applicationInfo );
							addApps( add_appList );
						}
						is_dragview_from_folder = false;
					}
					else
					{
						boolean is_found = false;
						// 遍历所有Icon，找到在GridView中该拖动的Icon
						for( int pageIndex = 0 ; pageIndex < appPageCount ; pageIndex++ )
						{
							GridView3D layout = (GridView3D)view_list.get( pageIndex );
							for( int iconIdx = 0 ; iconIdx < layout.getChildCount() ; iconIdx++ )
							{
								View3D icon = layout.getChildAt( iconIdx );
								if( icon instanceof Icon3D && ( icon.name.equals( i.name ) ) )
								{
									// find it
									is_found = true;
									float icon_original_posX = icon.getX();
									float icon_original_posY = icon.getY();
									if( icon.isVisible() == false )
										icon.setVisible( true );
									// 图标的中心坐标为x，y
									float icon_cur_pos_x = x - icon.getWidth() / 2;
									float icon_cur_pos_y = y - icon.getHeight() / 2;
									icon.setPosition( icon_cur_pos_x < 0 ? 0f : icon_cur_pos_x , icon_cur_pos_y < 0 ? 0f : icon_cur_pos_y );
									// start animation to
									if( pageIndex == page_index )
									{
										// 如果icon只是在本页中移动
										dragIconBackTween = icon.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.3f , icon_original_posX , icon_original_posY , 0 ).setCallback( this );
									}
									else
									{
										// icon已经移动其他页面上，
										float target_x = 0f;
										float target_y = icon.getY();
										if( page_index > pageIndex )
										{
											target_x = 0;
										}
										else
										{
											target_x = this.width;
										}
										Icon3D icon_anim = (Icon3D)icon.clone();
										ViewGroup3D cur_grid_view = (ViewGroup3D)view_list.get( page_index );
										icon_anim.setPosition( icon.getX() , icon.getY() );
										cur_grid_view.addView( icon_anim );
										dragIconBackTween = icon_anim.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.3f , target_x , target_y , 0 ).setCallback( this )
												.setUserData( icon_anim );
									}
									break;
								}
							}
							if( is_found == true )
								break;
						}
						if( is_found == true )
						{
							//
						}
						else
						{
							sortApp( sortId , false );
							syncAppsPages();
							startAnimation();
						}
					}
					cellDropType = CELL_DROPTYPE_SINGLE_DROP;
					res = true;
				}
				break;
			case CELL_DROPTYPE_SINGLE_DROP_FOLDER:
				View3D target;
				View3D view = list.get( 0 );
				ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
				mTargetCellIndex = findNearestCellIndex( (int)x , (int)realY );
				if( mTargetCellIndex >= cur_view.getChildCount() )
				{
					Log.v( "cooee" , "------cooee------applist3D--- addDragView --- CELL_DROPTYPE_SINGLE_DROP_FOLDER --- mTargetCellIndex = " + mTargetCellIndex );
					break;
				}
				// folder = FindFolderInReflectView();
				// folder = cellMakeFolder(mTargetCellIndex, false,true);
				// folder.x = cur_view.getChildAt(mTargetCellIndex).x;
				// folder.y = cur_view.getChildAt(mTargetCellIndex).y;
				target = cur_view.getChildAt( mTargetCellIndex );
				cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
				if( target == null || !( target instanceof Icon3D ) )
					break;
				UserFolderInfo folderInfo = new UserFolderInfo();
				folderInfo.title = R3D.folder3D_name;
				// 获取quiet的信息给FolderInfo：updateTime, use Frequency etc.
				Icon3D icon = (Icon3D)target;
				ApplicationInfo quiet_appInfo = ( (ShortcutInfo)icon.getItemInfo() ).appInfo;
				if( quiet_appInfo != null )
				{
					folderInfo.lastUpdateTime = quiet_appInfo.lastUpdateTime;
					folderInfo.use_frequency = quiet_appInfo.getUseFrequency();
					if( DefaultLayout.getInstance().hasReplaceIcon( quiet_appInfo.componentName.getPackageName() , quiet_appInfo.componentName.getClassName() ) )
					{
						folderInfo.sort_folder_name = quiet_appInfo.title + "_aaaa";
					}
					else
					{
						folderInfo.sort_folder_name = quiet_appInfo.title + "_aaa";
					}
				}
				// 把foldInfo 信息存入 mApps, 替换掉quiet
				if( icon.info != null && icon.info instanceof ShortcutInfo )
				{
					AppListDB.getInstance().addOrMoveItem( folderInfo , LauncherSettings.Favorites.CONTAINER_APPLIST );
					iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
					ArrayList<View3D> addList = new ArrayList<View3D>();
					Icon3D quietClone = icon.clone();
					Vector2 icon_point = new Vector2();
					icon.toAbsoluteCoords( icon_point );
					quietClone.x = icon_point.x;
					quietClone.y = icon_point.y;
					addList.add( quietClone );
					addList.add( view );
					onDropList = addList;
					newFolderInfo = folderInfo;
					// 获取到quiet view 对应的ApplicationInfo
					ApplicationInfo quiet_applicationInfo = ( (ShortcutInfo)icon.getItemInfo() ).appInfo;
					ApplicationInfo view_applicationInfo = ( (ShortcutInfo)( (Icon3D)view ).getItemInfo() ).appInfo;
					ArrayList<ApplicationInfo> remove_appList = new ArrayList<ApplicationInfo>();
					int quiet_apps_idx = mItemInfos.indexOf( quiet_applicationInfo );
					remove_appList.add( quiet_applicationInfo );
					remove_appList.add( view_applicationInfo );
					// 此处会把folder add入 folderInfoMap
					AddFolderAndRemoveAppInApps( folderInfo , remove_appList , quiet_apps_idx );
					// 把当前的Folder增加入 Draglayer
					String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
					this.drageTarget_new_child = folderInfoMap.get( folderMap_key );
					viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
					res = true;
				}
				break;
		}
		// if (!res) {
		//
		// IconBase3D view = (IconBase3D) list.get(0);
		// ItemInfo info = view.getItemInfo();
		// Log.v("test", "error start...........");
		// Log.v("test", "cellDropType " + cellDropType + ",name = " +
		// list.get(0).name + " screen: " + info.screen + " x:" + info.x + " y:"
		// + info.y + " sx:" + info.spanX + " sy:" + info.spanY + " cx:" +
		// info.cellX + " cy:" + info.cellY);
		// Log.v("test", "error end  ...........");
		// itemInfo = ((IconBase3D)view).getItemInfo();
		// if (view instanceof Widget3D && itemInfo.container == -1) {/*
		// widget3D from mainmenu */
		// Root3D.deleteFromDB(info);
		// Widget3D widget3D = (Widget3D)view;
		// Widget3DManager.getInstance().deleteWidget3D(widget3D);
		// Log.d("launcher", "deleteWidget3D:"+info.title);
		// }
		// SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.no_space_add_icon));
		// }
		onDropLeave();
		return res;
	}
	
	//
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		boolean dropRes = true;
		List<View3D> iconGroupInput = new ArrayList<View3D>();
		for( View3D view : list )
		{
			if( view instanceof ViewCircled3D )
			{
				iconGroupInput.add( (View3D)view );
			}
		}
		if( iconGroupInput.size() > 1 )
		{
		}
		else
		{
			View3D view = list.get( 0 );
			float oldX = view.x + view.getParent().x;
			float oldY = view.y + view.getParent().y;
			// 重新判断drop type
			mTargetCellIndex = findNearestCellIndex( (int)x , (int)y );
			int isOccupy = GetViewIsOccupy( view , mTargetCellIndex );
			if( isOccupy == ICON_DROP_OVER_NONE )
			{
				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
			}
			else
			{
				if( folderAreaStayTime == 0 )
				{
					cellDropType = CELL_DROPTYPE_SINGLE_DROP;
				}
				else
				{
					if( System.currentTimeMillis() - folderAreaStayTime > AppList3D.ICON_DROP_GEN_FOLDER_STAY_TIMER )
					{
						cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
					}
					else
						cellDropType = CELL_DROPTYPE_SINGLE_DROP;
					folderAreaStayTime = 0;
				}
			}
			{
				// group.setScreen(index);
				dropRes = addDrageViewIntoAppList( list , (int)x , (int)y );
				int test = cellDropType;
				if( test == CELL_DROPTYPE_SINGLE_DROP_FOLDER )
				{
					onDropCompleted( view , true );
					return dropRes;
				}
			}
			onDropCompleted( view , true );
			if( dropRes && view instanceof IconBase3D )
			{
				Log.v( "test" , "workspace3D add to database" );
				ItemInfo info = ( (IconBase3D)view ).getItemInfo();
				info.screen = page_index;// index;
				info.x = (int)view.x;
				info.y = (int)view.y;
				Root3D.addOrMoveDB( info , LauncherSettings.Favorites.CONTAINER_APPLIST );
				if( view instanceof Icon3D )
				{
					Icon3D iconView = (Icon3D)view;
					iconView.setItemInfo( iconView.getItemInfo() );
				}
			}
			if( dropRes )
			{
				int tx = (int)view.x;
				int ty = (int)view.y;
				if( x != tx || y != ty )
				{
					// dropAnimating = true;
					view.setPosition( oldX , oldY );
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , tx , ty , 0 ).setCallback( this );
				}
			}
		}
		return dropRes;
	}
	
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( list.size() > 0 )
		{
			View3D view = list.get( 0 );
			boolean res = true;
			if( x < 0 || y < 0 || y > ( this.y + this.height ) )
			{
				// 超出applist的范围，返回false
				return false;
			}
			if( list.size() > 1 )
			{
				// 暂时不考虑size > 1 的情况
			}
			else
			{
				view = list.get( 0 );// only drag one source
				InitDragLayerData();
				if( view instanceof ViewCircled3D )
				{
					ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
					mTargetCellIndex = findNearestCellIndex( (int)x , (int)y );
					int isOccupy = GetViewIsOccupy( view , mTargetCellIndex );
					if( isOccupy == ICON_DROP_OVER_NONE )
					{
						cellDropType = CELL_DROPTYPE_SINGLE_DROP;
					}
					else
					{
						cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
						if( folderAreaStayTime == 0 )
						{
							folderAreaStayTime = System.currentTimeMillis();
							cellDropType = CELL_DROPTYPE_SINGLE_DROP;
						}
					}
				}
			}
			return true;
		}
		else
			return false;
	}
	
	public static final int MSG_ADD_DRAGLAYER = 4;
	public View3D drageTarget_new_child = null;
	public final static int CELL_DROPTYPE_NONE = 0;
	public final static int CELL_DROPTYPE_SINGLE_DROP = 1;
	public final static int CELL_DROPTYPE_SINGLE_DROP_FOLDER = 2;
	public final static int CELL_DROPTYPE_ICON_INTO_FOLDER = 3;
	private static final int ICON_DROP_OVER_NONE = 0;
	private static final int ICON_DROP_OVER_ICON = 1;
	private static final int ICON_DROP_OVER_FOLDER = 2;
	protected int cellDropType = CELL_DROPTYPE_SINGLE_DROP;
	private int mTargetCellIndex = 0;
	private float app_icon_width = 0f;
	private float app_icon_height = 0f;
	private long folderAreaStayTime = 0;
	private View3D add_new_folder_in_applist = null;
	private Tween dragIconBackTween = null;
	public final static int APP_HIDE_REASON_FOR_NONE = 0;
	public final static int APP_HIDE_REASON_FOR_FOLDER_OPEN = 1;
	// 标识applist在hide的时候，处于何种状态
	// 0: default value, none;
	// 1: 表示由于主菜单文件夹打开时，需要applist hide
	private int applist_hide_reason = APP_HIDE_REASON_FOR_NONE;
	// 标识是否在AppList的Folder中拖动Icon出来到AppList
	protected boolean is_dragview_from_folder = false;
	
	public void setAppListHideReason(
			int hide_reason )
	{
		this.applist_hide_reason = hide_reason;
	}
	
	public int getAppListHideReason()
	{
		return this.applist_hide_reason;
	}
	
	void InitDragLayerData()
	{
		ViewGroup3D cur_view_group = (ViewGroup3D)view_list.get( page_index );
		if( cur_view_group.getChildCount() > 0 )
		{
			app_icon_width = cur_view_group.getChildAt( 0 ).getWidth();
			app_icon_height = cur_view_group.getChildAt( 0 ).getHeight();
		}
		mTargetCellIndex = 0;
	}
	
	// 根据传入的坐标(x,y)，获取离当前坐标最近的一个icon cell 的index, 该index上可能不存在icon
	private int findNearestCellIndex(
			int pixelX ,
			int pixelY )
	{
		int nearestCellIndex = 0;
		int cur_view_size = 0;
		ViewGroup3D cur_view_group = (ViewGroup3D)view_list.get( page_index );
		cur_view_size = cur_view_group.getChildCount();
		if( cur_view_group instanceof GridView3D )
		{
			nearestCellIndex = ( (GridView3D)cur_view_group ).getIndex( pixelX , pixelY );
			if( DefaultLayout.enhance_generate_mainmenu_folder_condition == true )
			{
				// 增强判断：需要判断当前点击位置是否位于该最近的CellIndex
				// 的中心周围，只有在CellIndex中心周围的区域内，才算有效
				if( nearestCellIndex < cur_view_size && nearestCellIndex >= 0 )
				{
					View3D nearestCellView = cur_view_group.getChildAt( nearestCellIndex );
					float avail_range_radius = nearestCellView.getWidth() / 4;
					float cell_view_center_x = nearestCellView.getX() + nearestCellView.getWidth() / 2;
					float cell_view_center_y = nearestCellView.getY() + nearestCellView.getHeight() / 2;
					if( pixelX < ( cell_view_center_x - avail_range_radius ) || pixelX > ( cell_view_center_x + avail_range_radius ) || pixelY < ( cell_view_center_y - avail_range_radius ) || pixelY > ( cell_view_center_y + avail_range_radius ) )
					{
						nearestCellIndex = -1;
						Log.v( "cooee" , " AppList3D ---- findNearestCellIndex ----- current position is not in the availible area of " + nearestCellView.name );
					}
				}
			}
		}
		return nearestCellIndex;
	}
	
	private int GetViewIsOccupy(
			View3D view ,
			int item_index )
	{
		int drop_status = ICON_DROP_OVER_NONE;
		if( view == null || !( view instanceof Icon3D ) || item_index < 0 )
			return ICON_DROP_OVER_NONE;
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		if( item_index < cur_view.getChildCount() )
		{
			View3D cur_view_item = cur_view.getChildAt( item_index );
			if( cur_view_item instanceof Icon3D )
			{
				ShortcutInfo cur_view_item_sInfo = (ShortcutInfo)( (Icon3D)cur_view_item ).getItemInfo();
				ShortcutInfo view_sInfo = (ShortcutInfo)( (Icon3D)view ).getItemInfo();
				Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- source view = " + view.name );
				Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- cur_view_item = " + cur_view_item.name );
				if( cur_view_item.name.equals( view.name ) && ( view_sInfo.intent.getAction().equals( cur_view_item_sInfo.intent.getAction() ) && view_sInfo.intent.getComponent().equals(
						cur_view_item_sInfo.intent.getComponent() ) ) )
				{
					// the same icon, do nothing
					Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- this two view is the same ---- " );
					drop_status = ICON_DROP_OVER_NONE;
				}
				else
				{
					Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- they are different--- cause a folder ---- " );
					drop_status = ICON_DROP_OVER_ICON;
				}
			}
			else if( cur_view_item instanceof FolderIcon3D )
			{
				if( this.pointerInAbs( x , y ) && this.isVisibleInParent() )
				{
					// 此时的ICon放入Folder的动作不应该在此处，而是应该在FolderIcon3D中OnDropOver()完成，
					// 此时直接把该Icon放回到原来的位置
					// drop_status = ICON_DROP_OVER_NONE;
					// drop_status = ICON_DROP_OVER_FOLDER;
				}
				Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- cur_view_item is a folder ---- error case ---- " );
				drop_status = ICON_DROP_OVER_NONE;
			}
		}
		else
		{
			Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- index is over flow ---- error case ---- " );
			drop_status = ICON_DROP_OVER_NONE;
		}
		Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- exit ---- drop_status ---- " + drop_status );
		return drop_status;
	}
	
	private View3D cellMakeFolder(
			int item_index ,
			boolean preview ,
			boolean compress )
	{
		// Log.d("launcher", "make folder");
		View3D folder;
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		// cellToPoint(cellX, cellY, mTargetPoint);
		folder = createVirturFolder( compress );
		folder.x = cur_view.getChildAt( item_index ).getX() + ( app_icon_width - folder.width ) / 2;
		folder.y = cur_view.getChildAt( item_index ).getY() + ( app_icon_height - folder.height ) / 2;
		// Log.d("launcher",
		// "x,y="+folder.x+","+folder.y+" width,height="+folder.width+","+folder.height);
		if( preview )
		{
			Color color = folder.getColor();
			color.a = 0.5f;
			folder.setColor( color );
		}
		return folder;
	}
	
	private View3D createVirturFolder(
			boolean compress )
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		FolderIcon3D folderIcon3D;
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = page_index;// workspace.getCurrentScreen();
		folderInfo.x = 0;
		folderInfo.y = 0;
		// if(DefaultLayout.minu2_style_folder)
		// folderIcon3D = new FolderIconMi("FolderIcon3DView",
		// folderInfo);
		// else
		folderIcon3D = new FolderIcon3D( "FolderIcon3DView" , folderInfo );
		if( compress )
			folderIcon3D.changeFolderFrontRegion( true );
		return folderIcon3D;
	}
	
	public boolean onDropLeave()
	{
		CleanDropStatus();
		return true;
	}
	
	public void CleanDropStatus()
	{
		//
	}
	
	private void AddFolderAndRemoveAppInApps(
			ItemInfo folderInfo ,
			ArrayList<ApplicationInfo> removedAppList ,
			int insert_idx )
	{
		mItemInfos.add( insert_idx , folderInfo );
		mItemInfos.removeAll( removedAppList );
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	private void resetAppIconsStatusInFolder(
			FolderIcon3D folderIcon )
	{
		// 需要重置Folder中的ICon的状态
		for( int index = 0 ; index < folderIcon.getChildCount() ; index++ )
		{
			View3D folder_child = folderIcon.getChildAt( index );
			if( folder_child != null && folder_child instanceof Icon3D )
			{
				( (Icon3D)folder_child ).clearState();
				if( ( (Icon3D)folder_child ).getColor().a != 1.0f )
				{
					( (Icon3D)folder_child ).getColor().a = 1.0f;
				}
				if( this.mode == APPLIST_MODE_UNINSTALL )
				{
					( (Icon3D)folder_child ).showUninstall();
				}
				else if( this.mode == APPLIST_MODE_HIDE )
				{
					( (Icon3D)folder_child ).showHide();
				}
			}
		}
	}
	
	public void removeDragViews(
			ArrayList<View3D> removedViewList )
	{
		// ArrayList<ApplicationInfo> remove_appList = new
		// ArrayList<ApplicationInfo>();
		ArrayList<ItemInfo> remove_appList = new ArrayList<ItemInfo>();
		for( View3D dragview : removedViewList )
		{
			if( dragview != null && dragview instanceof Icon3D )
			{
				Icon3D dragIcon3D = (Icon3D)dragview;
				ItemInfo dragItemInfo = dragIcon3D.getItemInfo();
				if( dragItemInfo != null && dragItemInfo instanceof ShortcutInfo )
				{
					// 获取到view 对应的ApplicationInfo
					ApplicationInfo quiet_applicationInfo = ( (ShortcutInfo)dragItemInfo ).appInfo;
					remove_appList.add( (ItemInfo)quiet_applicationInfo );
					Root3D.deleteFromDB( quiet_applicationInfo );
				}
			}
			else if( dragview != null && dragview instanceof FolderIcon3D )
			{
				ItemInfo folderInfo = ( (FolderIcon3D)dragview ).getItemInfo();
				remove_appList.add( folderInfo );
				Root3D.deleteFromDB( folderInfo );
			}
		}
		mItemInfos.removeAll( remove_appList );
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	public void addBackInScreen(
			View3D child ,
			int x ,
			int y )
	{
		// int screen = 0;
		ItemInfo item = ( (IconBase3D)child ).getItemInfo();
		if( child instanceof DropTarget3D )
		{
			// 判断如果是FolderIcon3D，并且该文件中没有任何的child icon，那么需要移除该文件夹
			if( child instanceof FolderIcon3D && ( (FolderIcon3D)child ).getFolderIconNum() == 0 )
			{
				// 那么不需要发送 add_DrageLayer 消息,同时remove掉cur_view 下的空 Folder
				ArrayList<View3D> removedViewList = new ArrayList<View3D>();
				removedViewList.add( child );
				removeDragViews( removedViewList );
				CleanDropStatus();
				return;
			}
			drageTarget_new_child = child;
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		}
		// when child is not in mItemInfos, so add it---fix bug0002651
		if( child instanceof Icon3D && ( (Icon3D)child ).getItemInfo() != null )
		{
			ShortcutInfo sItemInfo = (ShortcutInfo)( (Icon3D)child ).getItemInfo();
			if( sItemInfo.appInfo != null && !mItemInfos.contains( sItemInfo.appInfo ) )
			{
				mItemInfos.add( sItemInfo.appInfo );
			}
		}
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
		CleanDropStatus();// group.cellCleanDropStatus();
	}
	// teapotXu add end for Folder in Mainmenu
}
