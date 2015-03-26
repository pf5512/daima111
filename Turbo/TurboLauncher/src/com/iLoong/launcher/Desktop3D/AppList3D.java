package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL11;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.Folder3D.Folder3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Folder3D.FolderMIUI3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreviewTips3D;
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


public class AppList3D extends NPageBase implements MenuActionListener , DragSource3D
{
	
	public static boolean inited = false;
	public static int mCellCountX = 4;
	public static int mCellCountY = 5;
	public static int mWidgetCountX = 2;
	public static int mWidgetCountY = 3;
	public ArrayList<ApplicationInfo> mApps;
	private ArrayList<WidgetShortcutInfo> mWidgets;
	private List<View3D> mWidget3DList;
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
	public int pre_mode = APPLIST_MODE_NORMAL; // teapotXu added
	public int mode = APPLIST_MODE_NORMAL;
	// teapotXu add start for icon3D's double-click optimization
	public boolean select_mode = false; // indicate whether any icon in the applist is selected or not
	// teapotXu add end
	public int sortId = -1;
	public boolean saveType = true;
	public int normalType;
	public static final int SORT_INSTALL = 0;
	public static final int SORT_NAME = 1;
	public static final int SORT_USE = 2;
	public static final int SORT_FACTORY = 3;
	public static final int SORT_DEFAULT = 4;
	// xiatian add start //for mainmenu sort by user
	public static final int SORT_BY_USER = 5;
	private LauncherModel mLauncherModel = null;
	public static boolean mIsFirstSortApp = true;
	private final static int GRIDVIEW_EDITMODE_ADD_ITEM = 0;
	private final static int GRIDVIEW_EDITMODE_REMOVE_ITEM = 1;
	private final static int GRIDVIEW_EDITMODE_MOVE_ITEM_PRE = 2;
	private final static int GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT = 3;
	private Tween FolderLargeTween = null;
	private boolean haveEstablishFolder = false;
	private View3D dropOverFolder = null;
	private int lastDropTargetCellIndex = -1;
	private long dropOverOneCellIdxStayTime = 0;
	public static boolean mNeedRefreshListWhenColseFolder = false;
	private App2Workspace3D mApp2Workspace;
	private Timeline animation_line_hide = null;
	public static boolean needAddDragViewBackToFolder = true;
	private Timeline animation_line_DragBackList = null;
	// xiatian add end
	public static final int APP_LIST3D_KEY_BACK = 0;
	public static final int APP_LIST3D_SHOW = 1;
	public static final int APP_LIST3D_HIDE = 2;
	public static final int APPLIST_MODE_UNINSTALL = 0;
	public static final int APPLIST_MODE_HIDE = 1;
	public static final int APPLIST_MODE_NORMAL = 2;
	public static final int APPLIST_MODE_USERAPP = 3;
	// public static final int MSG_START_DRAG = 0;
	Mesh mesh = null;
	private AppBar3D appBar;
	public static TextureRegion translucentBgRegion;
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
	// xiatian start //for mainmenu sort by user
	// private HashMap<String, FolderIcon3D> folderInfoMap;//xiatian del
	private HashMap<String , FolderIcon3DInAppList3D> folderInfoMap;// xiatian add	// xiatian end
	private UserFolderInfo newFolderInfo = null;
	private ArrayList<View3D> onDropList = null;
	public boolean force_applist_refesh = false;
	public boolean is_maimenufolder_open = false;
	public FolderIcon3D mOpenFolderIcon = null;
	public static boolean syncAppAnimFinished = false;
	public static boolean allInit = false;
	// teapotXu add end for Folder in Mainmenu
	public static HashMap<Integer , String> bg_icon_name = new HashMap<Integer , String>(); // xiatian add	//Mainmenu Bg
	// xiatian add start //EffectPreview
	private EffectPreview3D mApplistEffectPreview;
	private EffectPreviewTips3D mEffectPreviewTips3D;
	// xiatian add end
	//Jone add start
	public static Vector2 widget3DSize = new Vector2();
	//Jone add end
	private List<View3D> all2DWigetView3d = null;
	private final String WIDGETGRID = "WidgetGrid";
	//	public static boolean isRelease2DWidget = false;
	public static boolean hasbind2Dwidget = false;
	public boolean isAppbarWidget = false;//表示是否在applist小组件中显示
	//	public static boolean isWidgetLongClick = false;
	public static float allmemnum = 0;
	public final static int START_EFFECT = 1;
	public boolean isScrollFinished = true;
	
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
			// xiatian start //for mainmenu sort by user
			// folderInfoMap = new HashMap<String, FolderIcon3D>();//xiatian del
			folderInfoMap = new HashMap<String , FolderIcon3DInAppList3D>();// xiatian
																			// add
			// xiatian end
			// xiatian add start //for mainmenu sort by user
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				mLauncherModel = iLoongApplication.getInstance().getModel();
			}
			// xiatian add end
		}
		// teapotXu add end for Folder in Mainmenu
		mWidgets = new ArrayList<WidgetShortcutInfo>();
		mWidget3DList = new ArrayList<View3D>();
		x = 0;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight();
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
		setEffectType( SetupMenuActions.getInstance().getStringToIntger( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) );
		sortId = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getInt( "sort_app" , -1 );
		if( sortId == -1 )
		{
			if( DefaultLayout.getInstance().show_default_app_sort )
			{
				sortId = SORT_FACTORY;
			}
			else
			{
				sortId = SORT_INSTALL;// SORT_DEFAULT;
			}
		}
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		mCellCountX = share.getInt( "savehang" , -1 );
		mCellCountY = share.getInt( "savelie" , -1 );
		if( mCellCountX == -1 || mCellCountY == -1 )
		{
			if( DefaultLayout.dispose_cell_count )
			{
				mCellCountX = DefaultLayout.cellCountX;
				mCellCountY = DefaultLayout.cellCountY;
				mCellCountX = mCellCountX > 5 ? 5 : mCellCountX;
				mCellCountY = mCellCountY > 6 ? 6 : mCellCountY;
			}
			else if( DefaultLayout.show_icon_size && DefaultLayout.show_icon_size_different_layout )
			{
				mCellCountX = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_COL );
				mCellCountY = DefaultLayout.getLayoutAttrValue( DefaultLayout.LAYOUT_ATTR_APPLIST_ROW );
			}
			else
			{
				if( Utils3D.getScreenDisplayMetricsHeight() > 854 )
				{
					mCellCountX = 4;
					mCellCountY = 5;
				}
				else
				{
					mCellCountX = 4;
					mCellCountY = 4;
				}
			}
		}
		appGridPool = new GridPool( "AppGrid" , 5 , Utils3D.getScreenWidth() , (int)height , mCellCountX , mCellCountY );
		widgetGridPool = new GridPool( "WidgetGrid" , 5 , Utils3D.getScreenWidth() , (int)height , mWidgetCountX , mWidgetCountY );
		iconMap = new HashMap<String , AppIcon3D>();
		widget2DMap = new HashMap<String , Widget2DShortcut>();
		if( DefaultLayout.mainmenu_background_alpha_progress )
		{
			String bg_name = "theme/pack_source/translucent-black.png";
			Bitmap bmp = ThemeManager.getInstance().getBitmap( bg_name );
			if( bmp.getConfig() != Config.ARGB_8888 )
			{
				bmp = bmp.copy( Config.ARGB_8888 , false );
			}
			Texture t = new BitmapTexture( bmp );
			// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			translucentBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
			bmp.recycle();
		}
		else
			initBgIconName();
		appPageCount = 0;
		widget3DPageCount = 0;
		widget2DPageCount = 0;
		widgetPageCount = 0;
		indicatorView = new IndicatorView( "npage_indicator" , R3D.page_indicator_style );
		// teapotXu add start
		if( true )
		{
			drawIndicator = false;
			this.addView( indicatorView );
			this.drawIndicator = false;
		}
		// teapotXu add end
		transform = true;
	}
	
	// teapotXu added: 当主菜单可以滑动到widget页时，在以下情况下还是不允许滑动到widget页
	private boolean get_enable_scroll_to_widget_condition()
	{
		if( mScrollToWidget == true )
		{
			if( this.mode == AppList3D.APPLIST_MODE_HIDE // 隐藏模式
					|| this.mode == AppList3D.APPLIST_MODE_UNINSTALL // 卸载模式
			// || (mTypelist.get(mType) == APageEase.COOLTOUCH_EFFECT_CRYSTAL)
			// //设置为水晶特效时
			)
			{
				return false;
			}
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview && ( mApplistEffectPreview != null && mApplistEffectPreview.isVisible() ) ) || ( !Desktop3DListener.bAppDone ) )
		{
			return true;
		}
		// xiatian add end
		// zjp
		if( ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		return super.onLongClick( x , y );
	}
	
	public void onThemeChanged()
	{
		indicatorView.onThemeChanged();
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			ViewGroup3D layout = (ViewGroup3D)view_list.get( i );
			for( int j = 0 ; j < layout.getChildCount() ; j++ )
			{
				View3D view = layout.getChildAt( j );
				if( view == null )
					continue;
				if( ( view instanceof Widget3DVirtual ) && ( DefaultLayout.isWidgetLoadByInternal( ( (Widget3DVirtual)view ).packageName ) ) )
				{
					( (Widget3DVirtual)view ).onThemeChanged();
				}
				else if( view instanceof Icon3D )
				{
					( (Icon3D)view ).onThemeChanged();
				}
				else if( view instanceof FolderIcon3D )
				{
					FolderIcon3D oldFolder = (FolderIcon3D)view;
					oldFolder.onThemeChanged();
				}
				else if( view instanceof Widget3DShortcut )
				{
					Widget3DShortcut widget3d = (Widget3DShortcut)view;
					widget3d.onThemeChanged();
				}
			}
		}
	}
	
	@Override
	protected int getIndicatorPageCount()
	{
		// TODO Auto-generated method stub
		if( mScrollToWidget && get_enable_scroll_to_widget_condition() )
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
		if( mScrollToWidget && get_enable_scroll_to_widget_condition() )
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
					if( ( DefaultLayout.mainmenu_sort_by_user_fun == false ) || ( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mode != APPLIST_MODE_UNINSTALL ) ) )//xiatian add	//for mainmenu sort by user
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
			else if( getItemInfo( i ) instanceof UserFolderInfo )
			{
				// now current item is folder
				UserFolderInfo folderInfo = (UserFolderInfo)getItemInfo( i );
				String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
				Log.v( "cooee" , "---syncAppsPageItems--- cur icon is a folder, info is : " + folderMap_key );
				// xiatian start //for mainmenu sort by user
				// FolderIcon3D folderIcon =
				// folderInfoMap.get(folderMap_key);//xiatian del
				FolderIcon3DInAppList3D folderIcon = folderInfoMap.get( folderMap_key );// xiatian add
				// xiatian end
				if( folderIcon != null )
				{
					// xiatian add start //for mainmenu sort by user
					if( ( DefaultLayout.mainmenu_sort_by_user_fun == false ) || ( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mode != APPLIST_MODE_UNINSTALL ) ) )
					{
						folderIcon.clearState();
					}
					// xiatian add end
					// 需要重置Folder中的ICon的状态
					resetAppIconsStatusInFolder( folderIcon );
					// xiatian add start //for mainmenu sort by user
					if( DefaultLayout.mainmenu_sort_by_user_fun )
					{
						folderIcon.newAppGridIndex = page;
						if( ( page != page_index && folderIcon.oldAppGridIndex != page_index ) )
							folderIcon.oldAppGridIndex = page;
						folderIcon.oldX = folderIcon.x;
						folderIcon.oldY = folderIcon.y;
					}
					// xiatian add end
					if( folderIcon.getVisible() == false )
					{
						folderIcon.setVisible( true );
					}
					// folderIcon.setApplistMode(this.mode);
					layout.addItem( folderIcon );
					// 更新folderInfo的坐标
					folderInfo.x = (int)folderIcon.getX();
					folderInfo.y = (int)folderIcon.getY();
					// 第一次加载的位置不对ybh
					folderIcon.setItemInfo( folderInfo );
					// xiatian add start //for mainmenu sort by user
					if( ( DefaultLayout.mainmenu_folder_function ) && ( mode == APPLIST_MODE_UNINSTALL ) )
					{
						folderIcon.showUninstall( false );
					}
					// xiatian add end
				}
				else
				{
					if( folderInfo == newFolderInfo )
					{
						newFolderInfo = null;
						// xiatian start //for mainmenu sort by user
						// xiatian del start
						// FolderIcon3D newFolder;
						// newFolder = new FolderIcon3D(folderInfo);
						// xiatian del end
						// xiatian add start
						FolderIcon3DInAppList3D newFolder;
						newFolder = new FolderIcon3DInAppList3D( folderInfo );
						// xiatian add end
						// xiatian end
						if( onDropList != null )
						{
							//							newFolder.onDrop( onDropList , 0 , 0 );
							newFolder.addFolderNode( onDropList , false );
							onDropList = null;
						}
						// new FolderIcon3D(folderInfo);
						// newFolder.createAndAddShortcut(this.iconCache,
						// folderInfo);
						// 需要重置Folder中的ICon的状态
						resetAppIconsStatusInFolder( newFolder );
						// xiatian add start //for mainmenu sort by user
						if( DefaultLayout.mainmenu_sort_by_user_fun )
						{
							newFolder.oldAppGridIndex = page;
							newFolder.newAppGridIndex = page;
							if( page == page_index && inited )
							{
								newFolder.oldVisible = false;
							}
							else
							{
								newFolder.oldVisible = true;
							}
						}
						// xiatian add end
						folderInfoMap.put( folderMap_key , newFolder );
						layout.addItem( newFolder );
						newFolder.setFromWhere( FolderIcon3D.FROM_APPLIST );
						// 更新folderInfo的坐标
						folderInfo.x = (int)newFolder.getX();
						folderInfo.y = (int)newFolder.getY();
						// xiatian add start //for mainmenu sort by user
						if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mode == APPLIST_MODE_UNINSTALL ) )
						{
							newFolder.showUninstall( true );
						}
						// xiatian add end
					}
					else
					{
						// xiatian start //for mainmenu sort by user
						// xiatian del start
						// FolderIcon3D newFolder;
						// newFolder = new FolderIcon3D(folderInfo);
						// xiatian del end
						// xiatian add start
						FolderIcon3DInAppList3D newFolder;
						newFolder = new FolderIcon3DInAppList3D( folderInfo );
						if( DefaultLayout.mainmenu_sort_by_user_fun )
						{
							newFolder.oldAppGridIndex = page;
							newFolder.newAppGridIndex = page;
							if( page == page_index && inited )
							{
								newFolder.oldVisible = false;
							}
							else
							{
								newFolder.oldVisible = true;
							}
						}
						// xiatian add end
						// xiatian end
						folderInfoMap.put( folderMap_key , newFolder );
						layout.addItem( newFolder );
						newFolder.setFromWhere( FolderIcon3D.FROM_APPLIST );
						// 更新folderInfo的坐标
						folderInfo.x = (int)newFolder.getX();
						folderInfo.y = (int)newFolder.getY();
						newFolder.createAndAddShortcut( iconCache , folderInfo );
						this.drageTarget_new_child = newFolder;
						viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
						// xiatian add start //for mainmenu sort by user
						if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mode == APPLIST_MODE_UNINSTALL ) )
						{
							newFolder.showUninstall( true );
						}
						// xiatian add end
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
						i--;
						continue;
					}
					//Jone add start,fix bug: Virture icon is unsuitable for the Widget List  after changing theme
					if( RR.net_version )
					{
						widget3DSize.x = layout.getCellWidth() - R3D.app_widget3d_gap;
						widget3DSize.y = layout.getCellHeight() - R3D.app_widget3d_gap;
					}
					//Jone end
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
					//					widgetShortcut = new Widget2DShortcut(widgetInfo.label,
					//							R3D.findRegion(widgetInfo.textureName));
					if( widgetInfo.widget2DBitmap != null )
					{
						Texture texture = new BitmapTexture( widgetInfo.widget2DBitmap );
						texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
						TextureRegion widgetRegion = new TextureRegion( texture );
						if( DefaultLayout.enable_workspace_miui_edit_mode )
						{
							if( !widgetInfo.isShortcut && !widgetInfo.widget2DBitmap.isRecycled() )
							{
								Log.v( "hjwapplist" , "widget2DBitmap recycle() label is " + widgetInfo.label );
								widgetInfo.widget2DBitmap.recycle();
								widgetInfo.widget2DBitmap = null;
								texture = null;
							}
						}
						else
						{
							if( !widgetInfo.widget2DBitmap.isRecycled() )
							{
								Log.v( "hjwapplist" , "widget2DBitmap recycle() label is " + widgetInfo.label );
								widgetInfo.widget2DBitmap.recycle();
								widgetInfo.widget2DBitmap = null;
								texture = null;
							}
						}
						widgetShortcut = new Widget2DShortcut( widgetInfo.label , widgetRegion );
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
						if( mode == APPLIST_MODE_HIDE )
							widgetShortcut.showHide();
						widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
						widgetShortcut.setInfo( widgetInfo );
					}
				}
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
					i--;
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
	
	public synchronized void checkItemInfos()
	{
		if( mItemInfos == null )
			return;
		ArrayList<String> names = new ArrayList<String>();
		ItemInfo info = null;
		for( int i = mItemInfos.size() - 1 ; i >= 0 ; i-- )
		{
			info = mItemInfos.get( i );
			if( info instanceof ApplicationInfo )
			{
				ApplicationInfo appInfo = (ApplicationInfo)info;
				ShortcutInfo sInfo = appInfo.makeShortcut();
				String infoName = R3D.getInfoName( sInfo );
				if( names.contains( infoName ) )
				{
					mItemInfos.remove( i );
				}
				else
				{
					names.add( infoName );
				}
			}
		}
	}
	
	public synchronized void syncAppsPages()
	{
		Log.v( "cooee" , " appList ---- syncAppsPages --- enter ---  view_list.size = " + view_list.size() + "appPageCount = " + appPageCount );
		checkItemInfos();
		Iterator<View3D> ite = view_list.iterator();
		ArrayList<View3D> listToRemove = new ArrayList<View3D>();
		while( ite.hasNext() )
		{
			GridView3D grid = (GridView3D)ite.next();
			if( grid.name.equals( "AppGrid" ) )
			{
				listToRemove.add( grid );
			}
		}
		if( listToRemove.size() > 0 )
		{
			for( View3D v : listToRemove )
			{
				GridView3D grid = (GridView3D)v;
				view_list.remove( grid );
				appGridPool.free( grid );
				this.removeView( grid );
			}
		}
		syncAppPageCount();
		for( int i = 0 ; i < appPageCount ; i++ )
		{
			GridView3D grid = appGridPool.create( mCellCountX , mCellCountY );
			// if(grid.name.equals("WidgetGrid"))
			// grid = new GridView3D("AppGrid", Utils3D.getScreenWidth(), (int)
			// height,
			// mCellCountX, mCellCountY);
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
			// if(grid.name.equals("WidgetGrid"))
			// grid = new GridView3D("AppGrid", Utils3D.getScreenWidth(), (int)
			// height,
			// mCellCountX, mCellCountY);
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
		Log.v( "cooee" , " appList ---- syncAppsPages --- exit ---" );
	}
	
	public boolean canPopMenu()
	{
		if( !Desktop3DListener.bAppDone )
			return false;
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
			if( appBar != null && AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_APP && appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT && this.is_maimenufolder_open == false )
				// if (AppHost3D.currentContentType ==
				// AppHost3D.CONTENT_TYPE_APP
				// && appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT)
				// teapotXu add end for Folder in Mainmenu
				return true;
			return false;
		}
	}
	
	public synchronized void syncWidgetPages()
	{
		Iterator<View3D> ite = view_list.iterator();
		ArrayList<View3D> listToRemove = new ArrayList<View3D>();
		while( ite.hasNext() )
		{
			GridView3D grid = (GridView3D)ite.next();
			if( grid.name.equals( WIDGETGRID ) )
			{
				listToRemove.add( grid );
			}
		}
		if( listToRemove.size() > 0 )
		{
			for( View3D v : listToRemove )
			{
				GridView3D grid = (GridView3D)v;
				view_list.remove( grid );
				widgetGridPool.free( grid );
				this.removeView( grid );
			}
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
		if( DefaultLayout.enable_release_2Dwidget && isAppbarWidget )
		{
			isAppbarWidget = false;
			if( appPageCount < view_list.size() )
				setCurrentPage( appPageCount );
		}
		Log.v( "cooee" , " appList ---- syncWidgetPages --- exit ----" );
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
		if (!iLoongApplication.getInstance().getModel().appListLoaded) return;
			
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
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			Iterator folderIter = folderInfoMap.entrySet().iterator();
			while( folderIter.hasNext() )
			{
				Map.Entry entry = (Map.Entry)folderIter.next();
				FolderIcon3DInAppList3D icon = (FolderIcon3DInAppList3D)entry.getValue();
				if( icon.getParent() != null )
				{
					if( !icon.oldVisible )
					{
						//						icon.setScale( 0 , 0 );
						//						tween = icon.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
						icon.oldVisible = true;
					}
					else if( icon.oldAppGridIndex != icon.newAppGridIndex || ( ( icon.oldX != icon.x || icon.oldY != icon.y ) && icon.newAppGridIndex == page_index ) )
					{
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
		}
		// xiatian add end
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
			// xiatian add start
			if( type == TweenCallback.COMPLETE )
			{
				if( DefaultLayout.miui_generate_folder_anim == true && FolderLargeTween == source )
				{
					// for miui produce a folder
					if( FolderLargeTween != null )
					{
						FolderLargeTween = null;
					}
				}
				else
				{
					// animalFinished =true;
					if( source == tween )
					{
						Log.v( "fuckLoading" , "onEvent 00000 " );
						allInit = true;
						DefaultLayout.getInstance().cancelProgressDialog();
						syncAppAnimFinished = true;
						if( tween.getUserData() != null && tween.getUserData().equals( START_EFFECT ) )
						{
							refreshUninstall( false );
						}
					}
					// xiatian add start //for mainmenu sort by user
					else if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( source == animation_line_hide ) )
					{
						animation_line_hide = null;
						mApp2Workspace.areaStayTime = 0;
						mApp2Workspace.setFocus( false );
					}
					else if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( source == animation_line_DragBackList ) )
					{
						animation_line_DragBackList = null;
						syncAppsPages();
						startAnimation();
					}
					// xiatian add end
				}
				Log.v( "focus" , "on event 11111" );
			}
			// xiatian add end
			// xiatian end
		}
		// teapotXu add end for Folder in Mainmenu
		boolean needSync = false;
		if( needLayout && source == tween )
		{
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					( (GridView3D)i ).layout_pub( 0 , false );
					if( appPageCount > 1 )
					{
						if( view_list.indexOf( i ) < appPageCount - 1 && !needSync )
						{
							needSync = !( (GridView3D)i ).testFull();
							Log.v( "applist" , "needSync:" + needSync );
						}
					}
				}
			}
		}
		if( needSync )
		{
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					syncAppsPages();
					startAnimation();
				}
			} );
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
		// xiatian add start //EffectPreview
		else if( ( DefaultLayout.enable_effect_preview ) && ( type == TweenCallback.COMPLETE && source == tween ) )
		{
			if( ( mApplistEffectPreview.isVisible() ) && ( !mEffectPreviewTips3D.isVisible() ) )
			{
				mEffectPreviewTips3D.show();
			}
		}
		else if( ( DefaultLayout.enable_effect_preview ) && ( type == TweenCallback.COMPLETE && source == mPreviewTween ) )
		{
			if( !mPreviewFirst )
			{
				if( ( mApplistEffectPreview.isVisible() ) && ( !mEffectPreviewTips3D.isVisible() ) )
				{
					mEffectPreviewTips3D.show();
				}
			}
		}
		// xiatian add end
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
		if( DefaultLayout.mainmenu_widget_dispale_sys_widgets )
		{
			mWidgets = (ArrayList<WidgetShortcutInfo>)widgets.clone();
		}
		else
		{
			List<WidgetShortcutInfo> add_widgetInfo = new ArrayList<WidgetShortcutInfo>();
			for( WidgetShortcutInfo widgetInfo : widgets )
			{
				String packageName = widgetInfo.component.getPackageName();
				try
				{
					PackageInfo pInfo = iLoongLauncher.getInstance().getPackageManager().getPackageInfo( packageName , 0 );
					// 该系统widget是安装的widget，那么显示
					if( pInfo != null && DefaultLayout.getInstance().isUserApp( pInfo ) )
					{
						boolean b_same_widget = false;
						for( WidgetShortcutInfo mWidgetInfo : mWidgets )
						{
							if( ( widgetInfo.component != null && widgetInfo.component.toString() != null ) && ( widgetInfo.component.toString().equals( mWidgetInfo.component.toString() ) && ( widgetInfo.label != null && widgetInfo.label
									.equals( mWidgetInfo.label ) ) ) )
							{
								b_same_widget = true;
								break;
							}
						}
						if( b_same_widget == false )
							//mWidgets.add( widgetInfo );
							add_widgetInfo.add( widgetInfo );
					}
				}
				catch( NameNotFoundException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mWidgets.addAll( add_widgetInfo );
		}
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
		if( mode == APPLIST_MODE_HIDE
		// xiatian add start //for mainmenu sort by user
		|| ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun && mode == APPLIST_MODE_UNINSTALL )
		// xiatian add end
		)
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
						//						if( ( !( (ApplicationInfo)itemInfo ).isHideIcon ) || ( ( (ApplicationInfo)itemInfo ).isHideIcon && this.pre_mode == APPLIST_MODE_HIDE && this.mode == APPLIST_MODE_UNINSTALL ) )
						if( !( (ApplicationInfo)itemInfo ).isHideIcon )
							appCount++;
					}
					else
					{
						// it is folderInfo
						if( !DefaultLayout.mainmenu_folder_hide_while_children_all_hided )
						{
							appCount++;
						}
						else
						{
							if( itemInfo instanceof UserFolderInfo )
							{
								// it also need to know whether the children of
								// folder are all hided or not
								if( this.pre_mode == APPLIST_MODE_HIDE && this.mode == APPLIST_MODE_UNINSTALL )
								{
									appCount++;
								}
								else
								{
									boolean whether_all_children_hided = true;
									if( itemInfo != newFolderInfo )
									{
										for( ShortcutInfo child_sInfo : ( (UserFolderInfo)itemInfo ).contents )
										{
											if( child_sInfo.appInfo != null && !child_sInfo.appInfo.isHideIcon )
											{
												whether_all_children_hided = false;
												break;
											}
										}
									}
									else
									{
										whether_all_children_hided = false;
									}
									if( !whether_all_children_hided )
										appCount++;
								}
							}
						}
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
		if( mode == APPLIST_MODE_HIDE
		// xiatian add start //for mainmenu sort by user
		|| ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun && mode == APPLIST_MODE_UNINSTALL )
		// xiatian add end
		)
		{
			if( sortArray == null )
				return mItemInfos.get( position );
			else
				return mItemInfos.get( sortArray[position] );
		}
		else if( mode == APPLIST_MODE_USERAPP )
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
				if( sortArray == null ){
					itemInfo = mItemInfos.get( i );
//					Log.v("diaosixu", "111 mItemInfos lenth = " + mItemInfos.size() + " i = " + i);
				}
				else {
					itemInfo = mItemInfos.get( sortArray[i] );
//					Log.v("diaosixu", "222 mItemInfos lenth = " + mItemInfos.size() + " sortArray[i] = " + sortArray[i]);
				}
				if( itemInfo instanceof ApplicationInfo )
				{
					//					if( ( !( (ApplicationInfo)itemInfo ).isHideIcon ) || ( ( (ApplicationInfo)itemInfo ).isHideIcon && this.pre_mode == APPLIST_MODE_HIDE && this.mode == APPLIST_MODE_UNINSTALL ) )
					if( !( (ApplicationInfo)itemInfo ).isHideIcon )
						n++;
					if( n == position )
						return itemInfo;
				}
				else
				{
					// when this item is a folder, always show
					if( !DefaultLayout.mainmenu_folder_hide_while_children_all_hided )
					{
						n++;
						if( n == position )
							return itemInfo;
					}
					else
					{
						// when this item is a folder,
						if( itemInfo instanceof UserFolderInfo )
						{
							// it also need to know whether the children of
							// folder are all hided or not
							if( this.pre_mode == APPLIST_MODE_HIDE && this.mode == APPLIST_MODE_UNINSTALL )
							{
								n++;
							}
							else
							{
								boolean whether_all_children_hided = true;
								if( itemInfo != newFolderInfo )
								{
									for( ShortcutInfo child_sInfo : ( (UserFolderInfo)itemInfo ).contents )
									{
										if( child_sInfo.appInfo != null && !child_sInfo.appInfo.isHideIcon )
										{
											whether_all_children_hided = false;
											break;
										}
									}
								}
								else
								{
									whether_all_children_hided = false;
								}
								if( !whether_all_children_hided )
									n++;
							}
							if( n == position )
								return itemInfo;
						}
					}
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
		// xiatian add start //for mainmenu sort by user
		if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mode == APPLIST_MODE_UNINSTALL ) )
		{
			show2WorkspaceView();
		}
		// xiatian add end
		if( this.mode != mode )
		{
			if( appBar != null )
			{
				appBar.appTab.setMode( mode );
				if( appBar.pluginTab != null )
					appBar.pluginTab.setMode( mode );
			}
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
			// teapotXu add start for folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function )
			{
				FolderIcon3D.setApplistMode( mode );
				this.pre_mode = this.mode;
				FolderIcon3D.setApplistPreMode( this.mode );
			}
			// teapotXu add end for folder in Mainmenu
			//			if( this.mode == APPLIST_MODE_HIDE && mode == APPLIST_MODE_UNINSTALL )
			//			{
			//				this.mode = mode;
			//				refresh();
			//			}
			//			else
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
	
	public void setMode(
			boolean isFolder ,
			int mode )
	{
		// xiatian add start //for mainmenu sort by user
		if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mode == APPLIST_MODE_UNINSTALL ) )
		{
			if( isFolder )
			{
				show2EditAppBarView();
			}
			else
			{
				show2WorkspaceView();
			}
		}
		// xiatian add end
		if( this.mode != mode )
		{
			if( appBar != null )
			{
				appBar.appTab.setMode( mode );
				if( appBar.pluginTab != null )
					appBar.pluginTab.setMode( mode );
			}
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
			// teapotXu add start for folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function )
			{
				FolderIcon3D.setApplistMode( mode );
				this.pre_mode = this.mode;
				FolderIcon3D.setApplistPreMode( this.mode );
			}
			// teapotXu add end for folder in Mainmenu
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
	
	private void resetUninstallStatusInFolder(
			FolderIcon3D folderIcon ,
			boolean isStop )
	{
		// 需要重置Folder中的ICon的状态
		UserFolderInfo mUserFolderInfo = (UserFolderInfo)folderIcon.getItemInfo();
		if( mUserFolderInfo.opened == false )
		{
			for( int index = 0 ; index < folderIcon.getChildCount() ; index++ )
			{
				View3D folder_child = folderIcon.getChildAt( index );
				if( folder_child != null && folder_child instanceof Icon3D )
				{
					Icon3D mIcon3D = (Icon3D)folder_child;
					if( isStop )
					{
						mIcon3D.reset_shake_status();
					}
					else
					{
						mIcon3D.showUninstall();
					}
				}
			}
		}
	}
	
	public void refreshUninstall(
			boolean isStop )
	{
		if( !DefaultLayout.enable_effect_preview || mode != APPLIST_MODE_UNINSTALL )
		{
			return;
		}
		if( mApplistEffectPreview != null && mApplistEffectPreview.isVisible() )
		{
			return;
		}
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			GridView3D layout = (GridView3D)view_list.get( i );
			for( int j = 0 ; j < layout.getChildCount() ; j++ )
			{
				View3D child = layout.getChildAt( j );
				if( child instanceof Icon3D )
				{
					Icon3D icon = (Icon3D)child;
					if( isStop )
					{
						icon.reset_shake_status();
					}
					else
					{
						icon.showUninstall();
					}
				}
				else if( child instanceof FolderIcon3D )
				{
					FolderIcon3D folder = (FolderIcon3D)child;
					if( isStop )
					{
						folder.reset_shake_status();
					}
					else
					{
						folder.showUninstall( false );
					}
					resetUninstallStatusInFolder( folder , isStop );
				}
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
				// teapotXu add start for Folder in Mainmenu
				else if( child instanceof FolderIcon3D )
				{
					FolderIcon3D folder = (FolderIcon3D)child;
					// xiatian add start //for mainmenu sort by user
					folder.clearState();
					if( this.mode == APPLIST_MODE_UNINSTALL )
					{
						folder.showUninstall( false );
					}
					// xiatian add end
					resetAppIconsStatusInFolder( folder );
				}
				// teapotXu add end for Folder in Mainmenu
			}
		}
	}
	
	// teapotXu add start for icon3D's double-click optimization
	private void updateAllIconsStateInAppList()
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
					else if( select_mode == true )
					{
						icon.setSelectMode( true );
					}
					else
						icon.clearState();
				}
			}
		}
	}
	
	// teapotXu add end
	public ArrayList<View3D> getDragObjects()
	{
		return dragObjects;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( !iLoongApplication.getInstance().getModel().appListLoaded )
		{
			return;
		}
		
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
		if( !( DefaultLayout.mainmenu_background_alpha_progress ) )// xiatian
																	// add
																	// //mainmenu_background_alpha_progress
			refreshBg(); // xiatian add //Mainmenu Bg
		if( translucentBg != null )
		{
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				float a = color.a * ( height + y - ( DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0 ) ) / height;
				// add start 20131021 by hupeng
				if( a > 1 )
				{
					a = 1;
				}
				// add end 20131021 by hupeng
				// xiatian add start //mainmenu_background_alpha_progress
				if( DefaultLayout.mainmenu_background_alpha_progress )
				{
					float alpha = (float)( 100 - AppHost3D.mainmenuBgAlpha ) / 100;
					// teapotXu add start
					if( DefaultLayout.mainmenu_background_translucent )
						alpha = (float)( DefaultLayout.mainmenu_background_default_alpha ) / 100;
					// teapotXu add end
					a *= alpha;
				}
				// xiatian add end
				batch.setColor( color.r , color.g , color.b , a );
			}
			else
			{
				float a = color.a * parentAlpha * ( height + y - ( DefaultLayout.applist_style_classic ? R3D.hot_obj_height : 0 ) ) / height;
				// xiatian add start //mainmenu_background_alpha_progress
				if( DefaultLayout.mainmenu_background_alpha_progress )
				{
					float alpha = (float)( 100 - AppHost3D.mainmenuBgAlpha ) / 100;
					a *= alpha;
				}
				// xiatian add end
				batch.setColor( color.r , color.g , color.b , a );
			}
			translucentBg.draw( batch , 0 , 0 , Utils3D.realWidth , h );
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
		if( DefaultLayout.enable_new_particle )
		{
			newDrawParticle( batch );
		}
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
				case Widget3DShortcut.MSG_WIDGET3D_SHORTCUT_CLICK:
				{
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.can_drag_to_desktop ) );
					break;
				}
			}
		}
		else if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			switch( event_id )
			{
				case Icon3D.MSG_ICON_LONGCLICK:
					// xiatian add start //for mainmenu sort by user
					if( DefaultLayout.mainmenu_sort_by_user_fun )
					{
						setMode( APPLIST_MODE_UNINSTALL );
					}
					// xiatian add end
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
							// xiatian add start //for mainmenu sort by user
							if( DefaultLayout.mainmenu_sort_by_user_fun == true )
							{
								GridView3D cur_grid_view = ( (GridView3D)view_list.get( page_index ) );
								( cur_grid_view ).handleGridViewTouchDrag( icon3d , point.x , point.y );
							}
							// xiatian add end
						}
					}
					// xiatian add start //for mainmenu sort by user
					if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( selectedObjects.size() > 1 ) )
					{
						dealDragListFromDifferentPages( selectedObjects , icon.getIndexInParent() );
					}
					// xiatian add end
					selectedObjects.clear();
					// xiatian start //for mainmenu sort by user
					// this.setTag( icon.getTag() );// xiatian del
					// xiatian add start
					if( DefaultLayout.mainmenu_sort_by_user_fun )
					{
						this.setTag( new Vector2( DragLayer3D.dragStartX - icon.width / 2 , DragLayer3D.dragStartY - icon.height / 2 ) );
					}
					else
					{
						this.setTag( icon.getTag() );
					}
					// xiatian add end
					// xiatian end
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Icon3D.MSG_ICON_SELECTED:
					selectedObjects.add( icon );
					// teapotXu add start for icon3D's double-click optimization
					if( selectedObjects.size() == 1 )
					{
						// 有icon被双击选中
						select_mode = true;
						updateAllIconsStateInAppList();
						// 提示
						SendMsgToAndroid.sendCircleToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.multi_choice_mode_enter ) );
					}
					// teapotXu add end
					// dragObjects.add(icon.clone());
					return true;
				case Icon3D.MSG_ICON_UNSELECTED:
					selectedObjects.remove( icon );
					// dragObjects.remove(icon);
					// teapotXu add start for icon3D's double-click optimization
					if( selectedObjects.size() == 0 )
					{
						// 没有icon被双击选中
						select_mode = false;
						updateAllIconsStateInAppList();
					}
					// teapotXu add end
					return true;
			}
		}
		else if( sender instanceof Widget2DShortcut )
		{
			Widget2DShortcut widget = (Widget2DShortcut)sender;
			switch( event_id )
			{
				case Widget2DShortcut.MSG_WIDGET_SHORTCUT_LONGCLICK:
					if( widget.canAddWidget() == false )
					{
						SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getString( RR.string.widget_cannot_add_duplicate , widget.name ) );
						return true;
					}
					clearDragObjs();
					dragObjects.clear();
					Widget2DShortcut widgetClone = widget.createDragView();
					widgetClone.toAbsoluteCoords( point );
					dragObjects.add( widgetClone );
					this.setTag( new Vector2( widgetClone.x , widgetClone.y ) );
					Vector2 v = (Vector2)widget.getTag();
					widgetClone.setPosition( v.x - widgetClone.width / 2 , v.y - widgetClone.height / 2 );
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Widget2DShortcut.MSG_WIDGET_SHORTCUT_CLICK:
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.can_drag_to_desktop ) );
					break;
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
					// teapotXu add start for widget scroll
					if( dragObj instanceof Widget3D )
					{
						( (Widget3D)dragObj ).setWidgetTag2( Widget3D.WIDGET3D_NEED_SCROLL_STR );
					}
					// teapotXu add end
					clearDragObjs();
					dragObjects.clear();
					dragObjects.add( dragObj );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Widget3DShortcut.MSG_WIDGET3D_SHORTCUT_CLICK:
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.can_drag_to_desktop ) );
					break;
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
								// xiatian add start //for mainmenu sort by user
								if( DefaultLayout.mainmenu_sort_by_user_fun == true && this.mode == APPLIST_MODE_UNINSTALL && draglayer.draging )
								{
									// xiatian start //for mainmenu sort by user
									// HandleItemInEditModeOperation(draglayer.getDragList().get(0),
									// GRIDVIEW_EDITMODE_MOVE_ITEM_PRE,false);//xiatian
									// del
									// xiatian add start
									if( DefaultLayout.mainmenu_sort_by_user_fun )
									{
										HandleItemInEditModeOperation( draglayer.getDragList() , GRIDVIEW_EDITMODE_MOVE_ITEM_PRE , false );
									}
									else
									{
										HandleItemInEditModeOperation( draglayer.getDragList().get( 0 ) , GRIDVIEW_EDITMODE_MOVE_ITEM_PRE , false );
									}
									// xiatian add end
									// xiatian end
								}
								// xiatian add end
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
								// xiatian add start //for mainmenu sort by user
								if( DefaultLayout.mainmenu_sort_by_user_fun == true && this.mode == APPLIST_MODE_UNINSTALL && draglayer.draging )
								{
									// xiatian start //for mainmenu sort by user
									// HandleItemInEditModeOperation(draglayer.getDragList().get(0),
									// GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT,false);//xiatian
									// del
									// xiatian add start
									if( DefaultLayout.mainmenu_sort_by_user_fun )
									{
										HandleItemInEditModeOperation( draglayer.getDragList() , GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT , false );
									}
									else
									{
										HandleItemInEditModeOperation( draglayer.getDragList().get( 0 ) , GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT , false );
									}
									// xiatian add end
									// xiatian end
								}
								// xiatian add end
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
		else if( sender instanceof FolderIcon3D )
		{
			FolderIcon3D foldericon = (FolderIcon3D)sender;
			switch( event_id )
			{
				case FolderIcon3D.MSG_FOLDERICON3D_LONGCLICK:
				{
					// xiatian add start //for mainmenu sort by user
					if( DefaultLayout.mainmenu_sort_by_user_fun )
					{
						setMode( true , APPLIST_MODE_UNINSTALL );
					}
					// xiatian add end
					UserFolderInfo mUserFolderInfo = (UserFolderInfo)foldericon.getItemInfo();
					// when FolderIcon3D draging in Mainmenu
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true && ( ( this.mode == AppList3D.APPLIST_MODE_NORMAL ) || ( DefaultLayout.mainmenu_sort_by_user_fun )// xiatian add //for mainmenu sort by user
					) )
					{
						dragObjects.clear();
						// xiatian start //for mainmenu sort by user
						// this.setTag( foldericon.getTag() );// xiatian del
						// xiatian add start
						if( DefaultLayout.mainmenu_sort_by_user_fun )
						{
							this.setTag( new Vector2( DragLayer3D.dragStartX - foldericon.width / 2 , DragLayer3D.dragStartY - foldericon.height / 2 ) );
						}
						else
						{
							this.setTag( foldericon.getTag() );
						}
						// xiatian add end
						// xiatian end
						releaseFocus();
						{
							FolderIcon3D folderClone = null;
							Vector2 pos_point = new Vector2();
							UserFolderInfo folderCloneInfo = new UserFolderInfo();
							UserFolderInfo folderOrigInfo = (UserFolderInfo)foldericon.getItemInfo();
							folderCloneInfo.title = folderOrigInfo.title;
							folderCloneInfo.lastUpdateTime = folderOrigInfo.lastUpdateTime;
							folderCloneInfo.use_frequency = folderOrigInfo.use_frequency;
							folderCloneInfo.id = folderOrigInfo.id;
							for( ShortcutInfo sItemInfo : folderOrigInfo.contents )
							{
								// if this icon is hide icon, so donot add into the
								// folder of workspace
								if( sItemInfo.appInfo != null && sItemInfo.appInfo.isHideIcon )
									continue;
								ShortcutInfo cloneFolderItemInfo = new ShortcutInfo( sItemInfo );
								folderCloneInfo.add( cloneFolderItemInfo );
							}
							folderClone = new FolderIcon3D( folderCloneInfo );
							folderClone.createAndAddShortcut( iconCache , folderCloneInfo );
							resetAppIconsStatusInFolder( folderClone );
							foldericon.toAbsoluteCoords( pos_point );
							folderClone.x = pos_point.x;
							folderClone.y = pos_point.y;
							// xiatian add start //for mainmenu sort by user
							if( DefaultLayout.mainmenu_sort_by_user_fun )
							{
								folderClone.uninstall = foldericon.uninstall;
							}
							// xiatian add end
							folderCloneInfo.x = (int)folderClone.getX();
							folderCloneInfo.y = (int)folderClone.getY();
							if( this.mode == AppList3D.APPLIST_MODE_UNINSTALL )
							{
								// now hide the original folderIcon3D
								foldericon.setVisible( false );
							}
							dragObjects.add( folderClone );
							// xiatian add start //for mainmenu sort by user
							if( DefaultLayout.mainmenu_sort_by_user_fun == true )
							{
								GridView3D cur_grid_view = ( (GridView3D)view_list.get( page_index ) );
								( cur_grid_view ).handleGridViewTouchDrag( foldericon , pos_point.x , pos_point.y );
							}
							// xiatian add end
						}
						folderAreaStayTime = 0;
						// xiatian add start //for mainmenu sort by user
						if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( DefaultLayout.generate_new_folder_in_top_trash_bar ) )
						{
							viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
						}
						// xiatian add end
						return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					}
					return false;
				}
				// xiatian add start //for mainmenu sort by user
				case FolderIcon3D.MSG_FOLDERICON_BREAK_UP_SELF_IN_APPLIST:
				{
					if( DefaultLayout.mainmenu_folder_function )
					{
						breakUpFolderInApplist( foldericon );
						if( foldericon != null && iLoongLauncher.getInstance() != null && iLoongLauncher.getInstance().getD3dListener() != null && iLoongLauncher.getInstance().getD3dListener().getDragLayer() != null)
						{
							iLoongLauncher.getInstance().getD3dListener().getDragLayer().removeDropTarget( foldericon );
						}
						return true;
					}
					else
					{
						return false;
					}
				}
				// xiatian add end
			}
		}
		// xiatian add start //for mainmenu sort by user
		else if( sender instanceof FolderMIUI3D )
		{
			FolderMIUI3D folder3d = (FolderMIUI3D)sender;
			switch( event_id )
			{
				case FolderMIUI3D.MSG_UPDATE_GRIDVIEW_FOR_LOCATION:
					ArrayList<View3D> dragList = folder3d.getDragList();
					if( DefaultLayout.mainmenu_sort_by_user_fun == true )
					{
						// 此View是从文件夹中拖动出来到主菜单的，需要添加到主菜单中，那么需要当前的gridview 排挤出一个位置，
						for( View3D dragview : dragList )
						{
							if( dragview instanceof Icon3D )
							{
								Icon3D addview = ( (Icon3D)dragview ).clone();
								// addview.
								addview.setVisible( false );
								addview.setItemInfo( ( (Icon3D)dragview ).getItemInfo() );
								HandleItemInEditModeOperation( addview , GRIDVIEW_EDITMODE_ADD_ITEM , true );
							}
						}
					}
					return true;
			}
		}
		else if( sender instanceof Folder3D )
		{
			Folder3D folder3d = (Folder3D)sender;
			switch( event_id )
			{
				case FolderMIUI3D.MSG_UPDATE_GRIDVIEW_FOR_LOCATION:
					ArrayList<View3D> dragList = folder3d.getDragList();
					if( DefaultLayout.mainmenu_sort_by_user_fun == true )
					{
						// 此View是从文件夹中拖动出来到主菜单的，需要添加到主菜单中，那么需要当前的gridview 排挤出一个位置，
						for( View3D dragview : dragList )
						{
							if( dragview instanceof Icon3D )
							{
								Icon3D addview = ( (Icon3D)dragview ).clone();
								// addview.
								addview.setVisible( false );
								addview.setItemInfo( ( (Icon3D)dragview ).getItemInfo() );
								HandleItemInEditModeOperation( addview , GRIDVIEW_EDITMODE_ADD_ITEM , true );
							}
						}
					}
					return true;
			}
		}
		// xiatian add end
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
		if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) )
			setEffectType( bundle.getInt( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) );
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
			if( ApplistVirtualIconManager.getAppList() == null )
			{
				ApplistVirtualIconManager.setAppList( this );
			}
			ApplistVirtualIconManager.onAppsAdd( list );
			// xiatian add start //for mainmenu sort by user
			if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( Desktop3DListener.bAppDone ) )
			{
				for( ApplicationInfo mApplicationInfo : list )
				{
					mApplicationInfo.location_in_mainmenu = mItemInfos.indexOf( mApplicationInfo );
					mApplicationInfo.container = ItemInfo.NO_ID;
					ShortcutInfo mShortcutInfo = mApplicationInfo.makeShortcut();
					AppListDB.getInstance().addOrMoveItem( mShortcutInfo , LauncherSettings.Favorites.CONTAINER_APPLIST );
					mApplicationInfo.container = LauncherSettings.Favorites.CONTAINER_APPLIST;
					mApplicationInfo.id = mShortcutInfo.id;
				}
			}
			// xiatian add end
			if( !is_need_sync_app )
				return;
		}
		else
		{
			addAppNoRepeat( mApps , list );
			if( ApplistVirtualIconManager.getAppList() == null )
			{
				ApplistVirtualIconManager.setAppList( this );
			}
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
					// xiatian add start //for mainmenu sort by user
					// should delete the app item in database too
					if( DefaultLayout.mainmenu_sort_by_user_fun == true )
					{
						Root3D.deleteFromDB( sInfo );
						updateItemsLoactionAfterRemoved( sInfo.location_in_mainmenu );
					}
					// xiatian add end
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
					FolderIcon3D folderIcon = mOpenFolderIcon; // folderInfoMap.get(folderMap_key);
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
						// current situation it donot need to syncApp at once
						is_need_sync_app_immediatly = false;
					}
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

			if( ApplistVirtualIconManager.getAppList() == null )
			{
				ApplistVirtualIconManager.setAppList( this );
			}
		if (ApplistVirtualIconManager.getCurrentVirtualIcons() != null )
		{
			ApplistVirtualIconManager.onAppsRemoved( list );	
		}
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
		// it is the applicaion in Folder, so search the container folder,\
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			ItemInfo itemInfo = mItemInfos.get( index );
			if( itemInfo instanceof UserFolderInfo )
			{
				UserFolderInfo folderInfo = (UserFolderInfo)itemInfo;
				String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
				FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
				List<ShortcutInfo> remove_folder_sInfo = new ArrayList<ShortcutInfo>();
				for( ShortcutInfo folder_sInfo : folderInfo.contents )
				{
					// ShortcutInfo folder_sInfo = folder.contents.get(i);
					ApplicationInfo appInfo = folder_sInfo.appInfo;
					if( appInfo != null && ( appInfo.packageName != null && folder_sInfo.appInfo.packageName.equals( info.packageName ) ) && ( appInfo.componentName != null && folder_sInfo.appInfo.componentName
							.equals( info.componentName ) ) )
					{
						//	folderInfo.contents.remove( folder_sInfo );
						remove_folder_sInfo.add( folder_sInfo );
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
				folderInfo.contents.removeAll( remove_folder_sInfo );
				//				if( folderInfo.contents.size() == 0 )
				//				{
				//					// remove this folder icon too
				//					folderInfoMap.remove( folderMap_key );
				//					Root3D.deleteFromDB( folderInfo );
				//					mItemInfos.remove( folderInfo );
				//				}
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
		boolean b_3d_widget = false;
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
					b_3d_widget = true;
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
					b_3d_widget = true;
					break;
				}
			}
		}
		if( b_3d_widget == false )
		{
			ArrayList<WidgetShortcutInfo> mWidgets_cpy = (ArrayList<WidgetShortcutInfo>)mWidgets.clone();
			for( WidgetShortcutInfo widgetInfo : mWidgets_cpy )
			{
				String localpackageName = widgetInfo.component.getPackageName();
				// maybe more than one widgets
				if( localpackageName != null && localpackageName.equals( packageName ) )
				{
					mWidgets.remove( widgetInfo );
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
				// teapotXu add start:因为在default_layout中没有配置该widget的图片，故不用显示虚图标
				if( bmp != null )
				{
					Widget3DVirtual icon = new Widget3DVirtual( name , bmp , name );
					icon.setItemInfo( info );
					icon.uninstall = shortcut.uninstall;
					icon.hide = shortcut.hide;
					mWidget3DList.add( widgetIconPosition , icon );
				}
				// teapotXu add end
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
		CleanDropStatus();// xiatian add //for mainmenu sort by user
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
		// teapotXu add start for icon3D's double-click optimization
		if( this.select_mode == true )
		{
			this.select_mode = false;
			updateAllIconsStateInAppList();
		}
		// teapotXu add end
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( !isScrollFinished )
			return true;
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview && ( mApplistEffectPreview != null && mApplistEffectPreview.isVisible() ) ) || ( !Desktop3DListener.bAppDone ) )
		{
			if( RR.net_version )
			{
				Desktop3DListener.root.backToBoxEffectTab();
				this.y = 0;
				if( crystalGroup != null )
				{
					crystalGroup.syncView( this );
				}
			}
			return true;
		}
		// xiatian add end
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
		if( DefaultLayout.enable_release_2Dwidget && DefaultLayout.enable_scroll_to_widget && !hasbind2Dwidget && iLoongApplication.BuiltIn )
		{
			bind2DWidget();
		}
		if( appBar != null )
			appBar.appTab.select();
		Color c = indicatorView.getColor();
		indicatorView.setColor( c.r , c.g , c.b , 0 );
		indicatorView.show();
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 1.0f, 0, 0);
		viewParent.onCtrlEvent( this , APP_LIST3D_SHOW );
		hideFocus( page_index );// zqh
		if( !iLoongApplication.getInstance().getModel().appListLoaded )
		{
			int marginTop = (int)( ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() ) / 2 );
			SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
		}
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				while (!Root3D.appListAniDone) {
//					Log.v("diaosixu", "appListAniDone not");
				}
				iLoongApplication.getInstance().getModel().loadAppList();
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						// teapotXu add start for Folder in Mainmenu
						while( true )
						{
							if( iLoongApplication.getInstance().getModel().appListLoaded )
							{
								break;
							}
						}
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							AppList3D.this.is_maimenufolder_open = false;
							if( force_applist_refesh == true )
							{
								// 强制排序
								sortApp( sortId , false );
								syncAppsPages();
								//				startAnimation();
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
						// xiatian add start //for mainmenu sort by user
						if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun )
							resetApplistIconState();
						// xiatian add end
						if( DefaultLayout.enable_release_2Dwidget && !hasbind2Dwidget && AppList3D.this.getCurrentPage() >= appPageCount && iLoongApplication.BuiltIn )
						{
							AppList3D.this.setCurrentPage( 0 );
						}
						SendMsgToAndroid.cancelCustomDialog();
					}
				} );
			}
		} ).start();
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
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			hide2WorkspaceViewNoAnim();
		}
		// xiatian add end
		clearDragObjs();
		super.hide();
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
			// xiatian add start //EffectPreview
			if( DefaultLayout.enable_effect_preview )
			{
				Root3D mRoot3D = iLoongLauncher.getInstance().getD3dListener().getRoot();
				if( mRoot3D.isApplistEffectPreviewMode() )
				{
					if( ( mPreviewTween == null ) )
					{
						mRoot3D.backToBoxEffectTab();
						this.y = 0;
						if( crystalGroup != null )
						{
							crystalGroup.syncView( this );
						}
					}
					return true;
				}
			}
			// xiatian add end
			if( inited )
			{
				if( mode != APPLIST_MODE_NORMAL )
				{
					if( RR.net_version && DefaultLayout.mainmenu_sort_by_user_fun )
					{
						hide2WorkspaceViewNoAnim();
					}
					setMode( APPLIST_MODE_NORMAL );
				}
				else
				{
					// teapotXu add start for icon3D's double-click optimization
					if( select_mode == true )
					{
						select_mode = false;
						updateAllIconsStateInAppList();
						return true;
					}
					else
					// teapotXu add end
					{
						// xiatian start //for mainmenu sort by user
						//viewParent.onCtrlEvent( this , APP_LIST3D_KEY_BACK );// xiatian del
						// xiatian add start
						if( ( DefaultLayout.mainmenu_folder_function ) && ( DefaultLayout.mainmenu_sort_by_user_fun ) )
						{
							AppHost3D mAppHost3D = (AppHost3D)getParent();
							FolderIcon3D mFolderIcon3D = mAppHost3D.getFolderIconOpendInAppHost();
							if( ( mFolderIcon3D == null ) || ( ( mFolderIcon3D != null ) && ( mFolderIcon3D.bAnimate == false ) ) )
							{
								viewParent.onCtrlEvent( this , APP_LIST3D_KEY_BACK );
							}
						}
						else
						{
							viewParent.onCtrlEvent( this , APP_LIST3D_KEY_BACK );
						}
						// xiatian add end
						// xiatian end
					}
				}
			}
			return true;
		}
		return super.keyUp( keycode );
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
	
	public void onKeyEvent(
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
						onKeyEvent( page_index + 1 );
					}
					else
					{
						onKeyEvent( 0 );
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
						onKeyEvent( page_index - 1 );
					else
						onKeyEvent( appPageCount - 1 );// show final page.
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
	
	public synchronized void sortApp(
			int checkId ,
			boolean refresh )
	{
		if (!iLoongApplication.getInstance().getModel().appListLoaded) {
			return;
		}
		
		if( !Desktop3DListener.bAppDone )
			return;
		try
		{
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
								if( appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
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
							}
							else
							{
								// xiatian add start //for mainmenu sort by user
								if( ( (FolderInfo)itemInfo ).sort_folder_name == null )
								{
									UserFolderInfo mUserFolderInfo = (UserFolderInfo)itemInfo;
									if( mUserFolderInfo.contents.size() != 0 )
									{
										ShortcutInfo mShortcutInfo = mUserFolderInfo.contents.get( 0 );
										if( ( mShortcutInfo.appInfo != null ) && ( mShortcutInfo.appInfo.componentName != null ) && DefaultLayout.getInstance().hasReplaceIcon(
												mShortcutInfo.appInfo.componentName.getPackageName() ,
												mShortcutInfo.appInfo.componentName.getClassName() ) )
										{
											if( mShortcutInfo.title != null )
											{
												mUserFolderInfo.sort_folder_name = mShortcutInfo.title + "_aaaa";
											}
										}
									}
								}
								// xiatian add end
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
							if( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
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
							else if( itemInfo instanceof FolderInfo )
							{
								// when select item is a folder
								nameKey[i] = "0000000";//( (FolderInfo)itemInfo ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );
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
							if( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
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
						int i;
						int[] facKey = new int[mItemInfos.size()];
						ArrayList<ItemInfo> embededItemInfo = new ArrayList<ItemInfo>();
						int embeded_app_num = getEmbededAppInfos( iLoongLauncher.getInstance() , embededItemInfo , mItemInfos );
						int[] embeded_facKey = new int[embeded_app_num];
						int[] embeded_sortKey = new int[embeded_app_num];
						int embeded_replace_icon_num = 0;
						for( int index = 0 ; index < embeded_app_num ; index++ )
						{
							ItemInfo appInfo = embededItemInfo.get( index );
							if( appInfo instanceof ApplicationInfo )
							{
								ApplicationInfo app_Info = (ApplicationInfo)appInfo;
								if( null != DefaultLayout.getInstance().getReplaceIcon( app_Info.packageName , app_Info.componentName.getClassName() ) )
								{
									embeded_facKey[index] = embeded_replace_icon_num - embeded_app_num;
									embeded_replace_icon_num++;
								}
								else
								{
									embeded_facKey[index] = (int)( ( (ApplicationInfo)appInfo ).lastUpdateTime / 1000 );
								}
							}
							else
							{
								embeded_facKey[index] = 0;
							}
						}
						cut.sort( 1 , embeded_facKey , embeded_sortKey );
						for( i = 0 ; i < mItemInfos.size() ; i++ )
						{
							ItemInfo appInfo = (ItemInfo)mItemInfos.get( i );
							if( appInfo instanceof ApplicationInfo )
							{
								if( embededItemInfo.contains( appInfo ) )
								{
									int app_tmp_index = getEmbededIndex( embededItemInfo , appInfo , embeded_sortKey );
									facKey[i] = app_tmp_index - embeded_app_num;
								}
								else
								{
									ApplicationInfo appInfo1 = (ApplicationInfo)mItemInfos.get( i );
									if( appInfo1.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
										facKey[i] = 0;
									else
										facKey[i] = (int)( appInfo1.lastUpdateTime / 1000 );
								}
							}
							else if( appInfo instanceof FolderInfo )
							{
								// when select item is a folder
								facKey[i] = (int)( (FolderInfo)appInfo ).lastUpdateTime;
							}
						}
						cut.sort( 1 , facKey , sortArray );
					}
					else
					// teapotXu add end for Folder in Mainmenu
					{
						int[] installKey = new int[mApps.size()];
						for( int i = 0 ; i < mApps.size() ; i++ )
						{
							if( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
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
						int useFrequency = 100000;
						for( int i = 0 ; i < mItemInfos.size() ; i++ )
						{
							ItemInfo itemInfo = mItemInfos.get( i );
							if( itemInfo instanceof ApplicationInfo )
							{
								if( ( (ApplicationInfo)itemInfo ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
									useKey[i] = 2147483647;
								else
									useKey[i] = (int)( ( (ApplicationInfo)itemInfo ).getUseFrequency() );
							}
							else if( itemInfo instanceof FolderInfo )
							{
								// when select item is a folder
								//useKey[i] = (int)( ( (FolderInfo)itemInfo ).use_frequency );
								//useKey[i] = (int)System.currentTimeMillis();
								useKey[i] = useFrequency--;
							}
						}
						cut.sort( 0 , useKey , sortArray );
					}
					else
					{
						int[] useKey = new int[mApps.size()];
						for( int i = 0 ; i < mApps.size() ; i++ )
						{
							if( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
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
									if( appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
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
								if( mApps.get( i ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
									facKey[i] = 0;
								else
									facKey[i] = (int)( mApps.get( i ).lastUpdateTime / 1000 );
							}
						}
						cut.sort( 1 , facKey , sortArray );
					}
					// teapotXu add end for Folder in Mainmenu
					break;
				// xiatian add start //for mainmenu sort by user
				case SORT_BY_USER:
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// 具体mItemInfos 保存的信息手动排序
						boolean mIsFollowDefault = false;
						for( int i = 0 ; i < mItemInfos.size() ; i++ )
						{
							ItemInfo itemInfo = mItemInfos.get( i );
							// 如果未完成初始化，那么暂时不排序
							if( !Desktop3DListener.bCreatDone )
							{
								sortArray[i] = i;
								continue;
							}
							if( itemInfo instanceof ApplicationInfo )
							{
								ApplicationInfo appInfo = (ApplicationInfo)itemInfo;
								int sort_index = appInfo.location_in_mainmenu;
								if( sort_index == -1 )
								{
									mIsFollowDefault = true;
									break;
								}
								if( sort_index >= 0 && sort_index < mItemInfos.size() )
								{
									sortArray[sort_index] = i;
								}
								else
								{
									// 如果该icon记录的location 大于所有的item总数，说明记录已经出错
									Log.e( "cooee" , "Applist3D ---sortApp----sort_by_user---error ---app:" + appInfo.title.toString() + "'s location_in_mainmenu is invalid value " );
								}
							}
							else if( itemInfo instanceof UserFolderInfo )
							{
								UserFolderInfo userFolderInfo = (UserFolderInfo)itemInfo;
								int sort_index = userFolderInfo.location_in_mainmenu;
								if( sort_index == -1 )
								{
									mIsFollowDefault = true;
									break;
								}
								if( sort_index >= 0 && sort_index < mItemInfos.size() )
								{
									sortArray[sort_index] = i;
								}
								else
								{
									// 如果该icon记录的location 大于所有的item总数，说明记录已经出错
									Log.e( "cooee" , "Applist3D ---sortApp----sort_by_user---error ---Folder: " + userFolderInfo.container + " location_in_mainmenu is invalid value " );
								}
							}
						}
						if( mIsFollowDefault )
						{
							sortAppByDefault();
						}
						else
						{
							checkDuplicatePosition();
						}
					}
					break;
			// xiatian add end
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
			// xiatian add start //for mainmenu sort by user
			if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun && mIsFirstSortApp )
			{
				mIsFirstSortApp = false;
				ItemInfo mItemInfo = null;
				for( int index = 0 ; index < mItemInfos.size() ; index++ )
				{
					mItemInfo = mItemInfos.get( index );
					if( mItemInfo instanceof ApplicationInfo )
					{
						break;
					}
				}
				if( ( mItemInfo.id == -1 ) && ( mItemInfo.container == -1 ) )
				{
					ArrayList<ItemInfo> ItemInfoList = new ArrayList<ItemInfo>();
					HashMap<String , ShortcutInfo> mAllShortcutInfoDB = mLauncherModel.getAppSInfosInAppListDB();
					HashMap<Long , FolderInfo> mAllFolderInfoInDB = mLauncherModel.getFolderInfosInAppListDB();
					for( int index = 0 ; index < mItemInfos.size() ; index++ )
					{
						ItemInfo itemInfo = mItemInfos.get( sortArray[index] );
						itemInfo.location_in_mainmenu = index;
						if( itemInfo instanceof ApplicationInfo )
						{
							ApplicationInfo mApplicationInfo = (ApplicationInfo)itemInfo;
							ShortcutInfo mShortcutInfoDB = mAllShortcutInfoDB.get( mApplicationInfo.componentName.toString() );
							if( mShortcutInfoDB != null )
							{
								mApplicationInfo.id = mShortcutInfoDB.id;
								mApplicationInfo.container = mShortcutInfoDB.container;
							}
							ItemInfoList.add( mApplicationInfo.makeShortcut() );
						}
						else if( itemInfo instanceof UserFolderInfo )
						{
							UserFolderInfo mUserFolderInfo = (UserFolderInfo)itemInfo;
							FolderInfo mFolderInfoDB = mAllFolderInfoInDB.get( mUserFolderInfo.id );
							if( mFolderInfoDB != null )
							{
								mUserFolderInfo.id = mFolderInfoDB.id;
								mUserFolderInfo.container = mFolderInfoDB.container;
							}
							ItemInfoList.add( mUserFolderInfo );
						}
					}
					AppListDB.getInstance().BatchItemsUpdate( ItemInfoList );
				}
			}
			// xiatian add end
			if( refresh )
			{
				syncAppsPages();
				startAnimation();
			}
		}
		catch( ArrayIndexOutOfBoundsException e )
		{
		}
	}
	
	//
	private int getEmbededAppInfos(
			Context mContext ,
			ArrayList<ItemInfo> des_arraylst ,
			ArrayList<ItemInfo> allItemsInfo )
	{
		final PackageManager manager = mContext.getPackageManager();
		int embeded_app_num = 0;
		if( des_arraylst == null || allItemsInfo == null )
			return 0;
		des_arraylst.clear();
		for( ItemInfo itemInfo : allItemsInfo )
		{
			if( itemInfo instanceof ApplicationInfo && ( (ApplicationInfo)itemInfo ).intent != null )
			{
				ResolveInfo resolveInfo = manager.resolveActivity( ( (ApplicationInfo)itemInfo ).intent , 0 );
				if( resolveInfo == null )
				{
					continue;
				}
				int flags = resolveInfo.activityInfo.applicationInfo.flags;
				if( ( flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 || ( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) != 0 ) )
				{
					// this app is embeded item
					des_arraylst.add( itemInfo );
					embeded_app_num++;
				}
			}
		}
		return embeded_app_num;
	}
	
	private int getEmbededIndex(
			ArrayList<ItemInfo> embededInfos ,
			ItemInfo itemInfo ,
			int[] sortArray )
	{
		if( embededInfos == null || sortArray == null || itemInfo == null )
			return 0;
		if( itemInfo instanceof ApplicationInfo )
		{
			for( int i = 0 ; i < embededInfos.size() ; i++ )
			{
				int sort_index = sortArray[i];
				ItemInfo cur_item = embededInfos.get( sort_index );
				if( cur_item != null && cur_item instanceof ApplicationInfo )
				{
					ApplicationInfo cur_app = (ApplicationInfo)cur_item;
					if( ( cur_app.packageName != null && cur_app.packageName.equals( ( (ApplicationInfo)itemInfo ).packageName ) ) && ( cur_app.componentName != null && cur_app.componentName
							.getClassName() != null && cur_app.componentName.getClassName().equals( ( (ApplicationInfo)itemInfo ).componentName.getClassName() ) ) )
					{
						return i;
					}
				}
			}
		}
		return 0;
	}
	
	public void setAppBar(
			AppBar3D appBar )
	{
		this.appBar = appBar;
	}
	
	protected int nextIndex()
	{
		if( mScrollToWidget && get_enable_scroll_to_widget_condition() )
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
					if( DefaultLayout.drawer_npages_circle_scroll_config )
					{
						return( page_index == appPageCount - 1 ? appPageCount - 1 : page_index + 1 );
					}
					if( page_index == appPageCount - 1 )
					{
						return 0;
					}
					else
					{
						return page_index + 1;
					}
				}
				else if( this.appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET )
				{
					if( DefaultLayout.drawer_npages_circle_scroll_config )
					{
						return( page_index == appPageCount + widgetPageCount - 1 ? appPageCount + widgetPageCount - 1 : page_index + 1 );
					}
					if( page_index == appPageCount + widgetPageCount - 1 )
					{
						return appPageCount;
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
		if( mScrollToWidget && get_enable_scroll_to_widget_condition() )
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
					if( DefaultLayout.drawer_npages_circle_scroll_config )
					{
						return( page_index == 0 ? 0 : page_index - 1 );
					}
					if( page_index == 0 )
					{
						return appPageCount - 1;
					}
					else
					{
						return page_index - 1;
					}
				}
				else if( this.appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET )
				{
					if( DefaultLayout.drawer_npages_circle_scroll_config )
					{
						return( page_index == appPageCount ? appPageCount : page_index - 1 );
					}
					if( page_index == appPageCount )
					{
						return appPageCount + widgetPageCount - 1;
					}
					else
					{
						return page_index - 1;
					}
				}
				else
				{
					return page_index;
				}
			}
		}
		// return (page_index == 0 ? view_list.size() - 1 : page_index - 1);
	}
	
	protected void updateEffect()
	{
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
		//		}
		// teapotXu add start
		if( DefaultLayout.enable_AppListIndicatorScroll )
		{
			if( Root3D.scroll_indicator )
			{
				return;
			}
		}
		// teapotXu add end
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
			if( mScrollToWidget && get_enable_scroll_to_widget_condition() )
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index == 0 || page_index >= appPageCount ) )
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
				}
			}
			else
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index >= appPageCount ) )
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
				}
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
			if( mScrollToWidget && get_enable_scroll_to_widget_condition() )
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index >= appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
				}
			}
			else
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
				}
			}
		}
		else if( yScale != 0 )
		{
			if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
			{
				pre_view = null;
				APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
			}
			else
			{
				pre_view = null;
				APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
			}
		}
		//		else
		//		{
		//			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
		//			{
		//				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
		//				{
		//					APageEase.updateEffect( cur_view , next_view , xScale , 0 , mTypelist.get( 0 ) );
		//				}
		//				else
		//					APageEase.updateEffect( cur_view , next_view , xScale , 0 , mTypelist.get( mType ) );
		//			}
		//		}
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
	
	// teapotXu add start for scroll the indicator of AppList
	protected void updateEffectS4()
	{
		if( view_list.size() == 0 )
			return;
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( getIndicatorPageCount() <= 1 )
		{
			return;
		}
		initView();
		APageEase.setStandard( true );
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex() );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex() );
		float tempYScale = 0;
		if( xScale > 0 )
		{
			next_view.hide();
			if( mScrollToWidget && get_enable_scroll_to_widget_condition() && mode != APPLIST_MODE_USERAPP )
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index == 0 || page_index >= appPageCount ) )
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
				}
			}
			else
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index >= appPageCount ) )
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
				}
			}
		}
		else if( xScale <= 0 )
		{
			pre_view.hide();
			// initData(pre_view);
			if( mScrollToWidget && get_enable_scroll_to_widget_condition() && mode != APPLIST_MODE_USERAPP )
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index >= appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
				}
			}
			else
			{
				if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
				else
				{
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
				}
			}
		}
		else if( yScale != 0 )
		{
			if( ( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && mode == APPLIST_MODE_UNINSTALL ) || ( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 ) )
			{
				pre_view = null;
				APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
			}
			else
			{
				pre_view = null;
				APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
			}
		}
	}
	
	public boolean appList_need_show_crystalGroup()
	{
		// 当在主菜单打开主菜单文件夹 && 主菜单编辑模式 && 主菜单处于卸载模式下，主菜单切页特效需要无效，因此也不需要绘制水晶
		if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_edit_mode && ( (AppList3D)this ).mode == AppList3D.APPLIST_MODE_UNINSTALL )
		{
			return false;
		}
		// 在主菜单widget界面滑动，也不需要显示水晶
		if( page_index > appPageCount - 1 && page_index <= appPageCount + widgetPageCount - 1 )
		{
			return false;
		}
		// 在支持主菜单可以滑动到widget界面的功能下，从主菜单滑动到widget界面时，也不需要显示水晶
		if( mScrollToWidget && ( ( ( (AppList3D)this ).mode == AppList3D.APPLIST_MODE_NORMAL ) ) && ( ( page_index == ( appPageCount - 1 ) && this.xScale <= 0 ) || ( page_index == 0 && this.xScale >= 0 ) ) )
		{
			return false;
		}
		return true;
	}
	
	public int getTotalPageIndex(
			int curPageIndex )
	{
		int totalPageIndex = curPageIndex;
		if( !mScrollToWidget )
		{
			/*
			 * if (DefaultLayout.appbar_show_download) { if
			 * (appBar.tabIndicator.tabId == AppBar3D.TAB_APP) {
			 * 
			 * } else if (appBar.tabIndicator.tabId == AppBar3D.TAB_DOWNLOAD) {
			 * totalPageIndex = curPageIndex + appPageCount; } else if
			 * (appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET) {
			 * totalPageIndex = curPageIndex + appPageCount + downloadPageCount;
			 * } } else
			 */
			if( appBar != null )
			{
				if( appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
				{
				}
				else if( appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET )
				{
					totalPageIndex = curPageIndex + appPageCount;
				}
			}
		}
		else
		{
		}
		return totalPageIndex;
	}
	
	public void setCurrentPageOnly(
			int index )
	{
		if( index < 0 )
			index = 0;
		else if( index >= view_list.size() )
			index = view_list.size() - 1;
		page_index = index;
	}
	
	// teapotXu add end
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
		isScrollFinished = true;
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
		if( pointer != 0 )
			return true;
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mPreviewTween != null ) )
		{
			requestFocus();
			return true;
		}
		// xiatian add end
		setInvisible();// zqh
		// teapotXu add start for Folder in Mainmenu
		// while syncApp's animation is running, we don't response the touch
		// event in Applist
		if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode )
		{
			if( ( tween == null || tween.isFinished() ) )
			{
				syncAppAnimFinished = true;
			}
			if( syncAppAnimFinished == false )
			{
				Log.v( "cooee" , "AppList3D---- onTouchDown--- invalid ---because syncApp's animation is running" );
				return true;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		refreshUninstall( true );
		// TODO Auto-generated method stub
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleStart( this.x , this.y , x , y );
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer != 0 )
			return true;
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mPreviewTween != null ) )
		{
			releaseFocus();
			return true;
		}
		// xiatian add end
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_WORKSPACE );
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		// xiatian add start //for mainmenu sort by user
		// while syncApp's animation is running, we don't response the touch
		// event in Applist
		if( DefaultLayout.mainmenu_folder_function && DefaultLayout.mainmenu_sort_by_user_fun && DefaultLayout.mainmenu_edit_mode )
		{
			if( tween == null || tween.isFinished() )
			{
				syncAppAnimFinished = true;
			}
			if( syncAppAnimFinished == false )
			{
				Log.v( "cooee" , "AppList3D---- onTouchUp--- invalid ---because syncApp's animation is running" );
				return true;
			}
		}
		// xiatian add end
		refreshUninstall( false );
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		if( ( DefaultLayout.enable_effect_preview && ( mPreviewTween != null ) ) || ( !Desktop3DListener.bAppDone ) )
		{
			return true;
		}
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
		if(appPageCount == 1)
			isScrollFinished = true;
		else
			isScrollFinished = false;
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview && ( mPreviewTween != null ) ) || ( !Desktop3DListener.bAppDone ) )
		{
			return true;
		}
		if( ( DefaultLayout.enable_effect_preview ) && ( mEffectPreviewTips3D.isVisible() ) )
		{
			mEffectPreviewTips3D.hide();
		}
		// xiatian add end
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
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
		private String name;
		
		public GridPool(
				String name ,
				int initCapacity ,
				float width ,
				float height ,
				int countX ,
				int countY )
		{
			this.name = name;
			grids = new ArrayList<GridView3D>( initCapacity );
			this.width = width;
			this.height = height;
			this.countX = countX;
			this.countY = countY;
		}
		
		private GridView3D create()
		{
			GridView3D grid = new GridView3D( name , width , height , countX , countY );
			grid.setPadding( R3D.applist_padding_left , R3D.applist_padding_right , R3D.applist_padding_top + Utils3D.getStatusBarHeight() , R3D.applist_padding_bottom + R3D.appbar_height );
			return grid;
		}
		
		private GridView3D create(
				int hang ,
				int lie )
		{
			GridView3D grid = new GridView3D( name , width , height , hang , lie );
			grid.setPadding( R3D.applist_padding_left , R3D.applist_padding_right , R3D.applist_padding_top + Utils3D.getStatusBarHeight() , R3D.applist_padding_bottom + R3D.appbar_height );
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
	
	public void finishBind()
	{
		if( this.mTypelist.get( this.mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
		{
			// 当launcher resume时，且当前的效果是水晶的效果，那么需要先init当前的applist，重新做水晶效果
			initView();
		}
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
		startAnimation();
	}
	
	public void reset()
	{
		long time = System.currentTimeMillis();
		// teapotXu add start
		if( this.mTypelist.get( this.mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
		{
			// 当launcher resume时，且当前的效果是水晶的效果，那么需要先init当前的applist，重新做水晶效果
			initView();
		}
		// teapotXu add end
	}
	
	public void addAppNoRepeat(
			ArrayList<ApplicationInfo> apps ,
			ArrayList<ApplicationInfo> adds )
	{
		for( int i = 0 ; i < adds.size() ; i++ )
		{
			if( apps.contains( adds.get( i ) ) )
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
			if( itemInfos.contains( adds.get( i ) ) )
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
			if( itemInfos.contains( adds.get( i ) ) )
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
						mItemInfos.remove( i-- );
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
					mApps.remove( i-- );
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
			if( DefaultLayout.mainmenu_sort_by_user_fun == false )// xiatian add //for mainmenu sort by user
				return true;
		}
		switch( cellDropType )
		{
			case CELL_DROPTYPE_SINGLE_DROP:
				// case CELL_DROPTYPE_FOLDER:
			default:
			{
				// for (View3D i: list) {
				if( list.size() == 1 )
				{
					View3D i = list.get( 0 );
					// 此处应该判断当前的拖动是否从文件夹拖出来的，
					// 如果是文件夹里拖出来的icon，应该增加cur_view 的整体个数，并且把该icon放置最后的位置。
					// 否则，回到原先的icon位置。
					if( i instanceof Icon3D && // xiatian add //for mainmenu sort by user
					( (Icon3D)i ).getInShowFolder() == true )// if(this.is_dragview_from_folder == true)
					{
						Icon3D icon = (Icon3D)i;
						if( icon.info != null && icon.info instanceof ShortcutInfo )
						{
							if( viewBackToFolder( i ) )
							{
								sortApp( sortId , true );
								return true;
							}
							// 获取到quiet view 对应的ApplicationInfo
							// xiatian start //for mainmenu sort by user
							// xiatian del start
							// //从文件夹中拖动出来的icon，应该在数据库中删除此项记录，
							// Root3D.deleteFromDB(icon.info);
							// ApplicationInfo view_applicationInfo =
							// ((ShortcutInfo)((Icon3D)icon).getItemInfo()).appInfo;
							// //恢复默认
							// view_applicationInfo.id = -1;
							// view_applicationInfo.container = -1;
							// ArrayList<ApplicationInfo> add_appList = new
							// ArrayList<ApplicationInfo>();
							// add_appList.add(view_applicationInfo);
							// xiatian del end
							// xiatian add start
							ApplicationInfo view_applicationInfo = ( (ShortcutInfo)( (Icon3D)icon ).getItemInfo() ).appInfo;
							ArrayList<ApplicationInfo> add_appList = new ArrayList<ApplicationInfo>();
							add_appList.add( view_applicationInfo );
							if( DefaultLayout.mainmenu_sort_by_user_fun == true )
							{
								updateCurrentAllAppRecorder();
							}
							else
							{
								// 从文件夹中拖动出来的icon，应该在数据库中删除此项记录，
								Root3D.deleteFromDB( icon.info );
								// 恢复默认
								view_applicationInfo.id = -1;
								view_applicationInfo.container = -1;
							}
							// xiatian add end
							// xiatian end
							// xiatian start //for mainmenu sort by user
							// addApps(add_appList);//xiatian del
							// xiatian add start
							if( DefaultLayout.mainmenu_sort_by_user_fun )
							{
								addAppsAndDelEmptyFolder( add_appList );
							}
							else
							{
								addApps( add_appList );
							}
							// xiatian add end
							// xiatian end
						}
						// is_dragview_from_folder = false;//xiatian del //for
						// mainmenu sort by user
					}
					else
					{
						// xiatian start //for mainmenu sort by user
						// xiatian del start
						// // 不支持 手动排序方式：
						// boolean is_found = false;
						//
						// //遍历所有Icon，找到在GridView中该拖动的Icon
						// for(int pageIndex=0;pageIndex<appPageCount;pageIndex++)
						// {
						// GridView3D layout = (GridView3D)
						// view_list.get(pageIndex);
						//
						// for(int
						// iconIdx=0;iconIdx<layout.getChildCount();iconIdx++)
						// {
						// View3D icon = layout.getChildAt(iconIdx);
						//
						//
						// if(icon instanceof Icon3D && (icon.name.equals(i.name)))
						// {
						// //find it
						// is_found = true;
						// float icon_original_posX = icon.getX();
						// float icon_original_posY = icon.getY();
						// if(icon.isVisible() == false)
						// icon.setVisible(true);
						//
						// //图标的中心坐标为x，y
						// float icon_cur_pos_x = x - icon.getWidth()/2;
						// float icon_cur_pos_y = y - icon.getHeight()/2;
						//
						// icon.setPosition(icon_cur_pos_x < 0 ? 0f:icon_cur_pos_x,
						// icon_cur_pos_y<0? 0f: icon_cur_pos_y);
						//
						// //start animation to
						// if(pageIndex == page_index)
						// {
						// //如果icon只是在本页中移动
						// dragIconBackTween =
						// icon.startTween(View3DTweenAccessor.POS_XY,
						// Linear.INOUT, 0.3f, icon_original_posX,
						// icon_original_posY, 0).setCallback(this);
						// }
						// else
						// {
						// //icon已经移动其他页面上，
						// float target_x = 0f;
						// float target_y = icon.getY();
						//
						// if(page_index > pageIndex)
						// {
						// target_x = 0;
						// }
						// else
						// {
						// target_x = this.width;
						// }
						// Icon3D icon_anim = (Icon3D)icon.clone();
						//
						// ViewGroup3D cur_grid_view =
						// (ViewGroup3D)view_list.get(page_index);
						//
						// icon_anim.setPosition(icon.getX(), icon.getY());
						// cur_grid_view.addView(icon_anim);
						//
						// dragIconBackTween =
						// icon_anim.startTween(View3DTweenAccessor.POS_XY,
						// Linear.INOUT, 0.3f, target_x, target_y,
						// 0).setCallback(this)
						// .setUserData(icon_anim);
						// }
						//
						// break;
						// }
						// }
						// if(is_found == true)
						// break;
						// }
						//
						// if(is_found == true)
						// {
						// //
						// }
						// else
						// {
						// sortApp(sortId, false);
						// syncAppsPages();
						// startAnimation();
						// }
						// xiatian del end
						// xiatian add start
						if( DefaultLayout.mainmenu_sort_by_user_fun == true )
						{
							// 此时的gridView中的children的顺序就是排列的顺序，需要把它保存下来，
							updateCurrentAllAppRecorder();
							// 回归动画
							boolean is_found = false;
							sortApp( sortId , false );
							GridView3D layout = (GridView3D)view_list.get( page_index );
							for( int iconIdx = 0 ; iconIdx < layout.getChildCount() ; iconIdx++ )
							{
								View3D icon = layout.getChildAt( iconIdx );
								if( i instanceof Icon3D )
								{
									if( icon instanceof Icon3D && ( icon.name.equals( i.name ) ) )
									{
										is_found = true;
									}
								}
								else if( i instanceof FolderIcon3D )
								{
									UserFolderInfo addView_folderInfo = (UserFolderInfo)( (FolderIcon3D)i ).getItemInfo();
									if( icon instanceof FolderIcon3D && ( (UserFolderInfo)( (FolderIcon3D)icon ).getItemInfo() ).id == addView_folderInfo.id )
									{
										is_found = true;
									}
								}
								if( is_found == true )
								{
									// find it										
									float icon_original_posX = icon.getX();
									float icon_original_posY = icon.getY();
									// xiatian add start //for mainmenu sort by user
									if( DefaultLayout.mainmenu_sort_by_user_fun )
									{
										Vector2 point = layout.getPos( icon.getIndexInParent() , true );
										icon_original_posX = point.x;
										icon_original_posY = point.y;
									}
									// xiatian add end
									if( icon.isVisible() == false )
										icon.setVisible( true );
									// 图标的中心坐标为x，y
									float icon_cur_pos_x = x - icon.getWidth() / 2;
									float icon_cur_pos_y = y - icon.getHeight() / 2;
									icon.setPosition( icon_cur_pos_x < 0 ? 0f : icon_cur_pos_x , icon_cur_pos_y < 0 ? 0f : icon_cur_pos_y );
									// start animation to
									{
										if( icon instanceof Icon3D )
										{
											Iterator iconIter = iconMap.entrySet().iterator();
											while( iconIter.hasNext() )
											{
												Map.Entry entry = (Map.Entry)iconIter.next();
												AppIcon3D mAppIcon3D = (AppIcon3D)entry.getValue();
												if( mAppIcon3D.name.equals( icon.name ) )
												{
													if( mAppIcon3D.getParent() != null )
													{
														mAppIcon3D.oldAppGridIndex = page_index;
														mAppIcon3D.oldX = icon_original_posX;
														mAppIcon3D.oldY = icon_original_posY;
													}
													break;
												}
											}
										}
										if( icon instanceof FolderIcon3DInAppList3D )
										{
											FolderIcon3DInAppList3D icon1 = (FolderIcon3DInAppList3D)icon;
											Iterator folderIter = folderInfoMap.entrySet().iterator();
											while( folderIter.hasNext() )
											{
												Map.Entry entry = (Map.Entry)folderIter.next();
												FolderIcon3DInAppList3D mFolderIcon3DInAppList3D = (FolderIcon3DInAppList3D)entry.getValue();
												if( ( mFolderIcon3DInAppList3D.name.equals( icon.name ) ) && ( mFolderIcon3DInAppList3D.mInfo.equals( icon1.mInfo ) ) )
												{
													if( mFolderIcon3DInAppList3D.getParent() != null )
													{
														mFolderIcon3DInAppList3D.oldAppGridIndex = page_index;
														mFolderIcon3DInAppList3D.oldX = icon_original_posX;
														mFolderIcon3DInAppList3D.oldY = icon_original_posY;
													}
													break;
												}
											}
										}
										// 如果icon只是在本页中移动
										dragIconBackTween = icon.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.3f , icon_original_posX , icon_original_posY , 0 ).setCallback( this );
									}
									break;
								}
							}
							if( is_found == false )
							{
								syncAppsPages();
								startAnimation();
							}
						}
						else
						{
							// 不支持 手动排序方式：
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
						// xiatian add end
						// xiatian end
					}
					cellDropType = CELL_DROPTYPE_SINGLE_DROP;
					res = true;
				}
				else if( DefaultLayout.mainmenu_sort_by_user_fun && ( list.size() > 1 ) )
				{
					// 此时的gridView中的children的顺序就是排列的顺序，需要把它保存下来，
					updateCurrentAllAppRecorder();
					// 回归动画
					boolean is_found = false;
					sortApp( sortId , false );
					GridView3D layout = (GridView3D)view_list.get( page_index );
					ArrayList<View3D> dragBackListShowTween = new ArrayList<View3D>();
					for( View3D i : list )
					{
						for( int iconIdx = 0 ; iconIdx < layout.getChildCount() ; iconIdx++ )
						{
							View3D icon = layout.getChildAt( iconIdx );
							if( i instanceof Icon3D )
							{
								if( icon instanceof Icon3D && ( icon.name.equals( i.name ) ) )
								{
									dragBackListShowTween.add( icon );
								}
							}
						}
					}
					if( dragBackListShowTween.size() > 0 )
					{
						animation_line_DragBackList = Timeline.createParallel();
						for( View3D icon : dragBackListShowTween )
						{
							// find it
							float icon_original_posX = icon.getX();
							float icon_original_posY = icon.getY();
							if( icon.isVisible() == false )
								icon.setVisible( true );
							// 图标的中心坐标为x，y
							float icon_cur_pos_x = x - icon.getWidth() / 2;
							float icon_cur_pos_y = y - icon.getHeight() / 2;
							icon.setPosition( icon_cur_pos_x < 0 ? 0f : icon_cur_pos_x , icon_cur_pos_y < 0 ? 0f : icon_cur_pos_y );
							// start animation to
							animation_line_DragBackList.push( Tween.to( icon , View3DTweenAccessor.POS_XY , 0.3f ).target( icon_original_posX , icon_original_posY , 0 ).ease( Linear.INOUT ) );
							Iterator iconIter = iconMap.entrySet().iterator();
							while( iconIter.hasNext() )
							{
								Map.Entry entry = (Map.Entry)iconIter.next();
								AppIcon3D mAppIcon3D = (AppIcon3D)entry.getValue();
								if( mAppIcon3D.name.equals( icon.name ) )
								{
									if( mAppIcon3D.getParent() != null )
									{
										mAppIcon3D.oldAppGridIndex = page_index;
										mAppIcon3D.oldX = icon_original_posX;
										mAppIcon3D.oldY = icon_original_posY;
									}
									break;
								}
							}
						}
						animation_line_DragBackList.start( View3DTweenAccessor.manager ).setCallback( this );
					}
				}
				break;
			}
			case CELL_DROPTYPE_SINGLE_DROP_FOLDER:
			{
				if( list.size() == 1 )
				{
					View3D target;
					View3D view = list.get( 0 );
					ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
					View3D newAddfolder = dropOverFolder;// xiatian add //for
															// mainmenu sort by user
					mTargetCellIndex = findNearestCellIndex( (int)x , (int)realY );
					if( mTargetCellIndex >= cur_view.getChildCount() )
					{
						Log.v( "cooee" , "------cooee------applist3D--- addDragView --- CELL_DROPTYPE_SINGLE_DROP_FOLDER --- mTargetCellIndex = " + mTargetCellIndex );
						break;
					}
					// xiatian add start //for miui generate folder animation
					if( DefaultLayout.miui_generate_folder_anim == true )
					{
						if( dropOverFolder instanceof FolderIcon3D && dropOverFolder.getVisible() )
						{
							newAddfolder.stopTween();
							newAddfolder.setScale( 1.0f , 1.0f );
							// newAddfolder.remove();
						}
					}
					// xiatian add end
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
							if( DefaultLayout.mainmenu_sort_by_user_fun == false ) // xiatian
																					// add
																					// //for
																					// mainmenu
																					// sort
																					// by
																					// user
								folderInfo.sort_folder_name = quiet_appInfo.title + "_aaa";
						}
					}
					// 把foldInfo 信息存入 mApps, 替换掉quiet
					if( icon.info != null && icon.info instanceof ShortcutInfo )
					{
						// xiatian start //for mainmenu sort by user
						// xiatian del start
						// AppListDB.getInstance().addOrMoveItem(folderInfo,LauncherSettings.Favorites.CONTAINER_APPLIST);
						// iLoongLauncher.getInstance().addFolderInfoToSFolders(folderInfo);
						// xiatian del end
						// xiatian add start
						if( DefaultLayout.mainmenu_sort_by_user_fun == false )
						{
							AppListDB.getInstance().addOrMoveItem( folderInfo , LauncherSettings.Favorites.CONTAINER_APPLIST );
							iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
						}
						// xiatian add end
						// xiatian end
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
						// xiatian start //for mainmenu sort by user
						// AddFolderAndRemoveAppInApps(folderInfo,remove_appList,quiet_apps_idx);//xiatian
						// del
						// xiatian add start
						if( DefaultLayout.mainmenu_sort_by_user_fun == true )
						{
							// 需要先更新生成文件夹前，移动了的icon位置
							updateCurrentAllAppRecorder();
							if( quiet_appInfo.location_in_mainmenu == -1 )
							{
								folderInfo.location_in_mainmenu = mTargetCellIndex;
							}
							else
							{
								folderInfo.location_in_mainmenu = quiet_appInfo.location_in_mainmenu;
							}
							AppListDB.getInstance().addOrMoveItem( folderInfo , LauncherSettings.Favorites.CONTAINER_APPLIST );
							iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
							AddFolderAndRemoveAppInApps( folderInfo , quiet_applicationInfo , view_applicationInfo , quiet_apps_idx );
						}
						else
						{
							AddFolderAndRemoveAppInApps( folderInfo , remove_appList , quiet_apps_idx );
						}
						// xiatian add end
						// xiatian end
						// 把当前的Folder增加入 Draglayer
						String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
						this.drageTarget_new_child = folderInfoMap.get( folderMap_key );
						viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
						res = true;
					}
				}
				else if( DefaultLayout.mainmenu_sort_by_user_fun && ( list.size() > 1 ) )
				{
					View3D target;
					ArrayList<View3D> viewList = list;
					if( viewList.size() > ( R3D.folder_max_num - 1 ) )
					{
						int len = viewList.size();
						len -= ( R3D.folder_max_num - 1 );
						for( int i = 0 ; i < len ; i++ )
						{
							viewList.remove( viewList.size() - 1 );
						}
					}
					ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
					View3D newAddfolder = dropOverFolder;
					mTargetCellIndex = findNearestCellIndex( (int)x , (int)realY );
					if( mTargetCellIndex >= cur_view.getChildCount() )
					{
						Log.v( "cooee" , "------cooee------applist3D--- addDragView --- CELL_DROPTYPE_SINGLE_DROP_FOLDER --- mTargetCellIndex = " + mTargetCellIndex );
						break;
					}
					// xiatian add start //for miui generate folder animation
					if( DefaultLayout.miui_generate_folder_anim == true )
					{
						if( dropOverFolder instanceof FolderIcon3D && dropOverFolder.getVisible() )
						{
							newAddfolder.stopTween();
							newAddfolder.setScale( 1.0f , 1.0f );
							// newAddfolder.remove();
						}
					}
					// xiatian add end
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
						addList.addAll( viewList );
						onDropList = addList;
						newFolderInfo = folderInfo;
						// 获取到quiet view 对应的ApplicationInfo
						ApplicationInfo quiet_applicationInfo = ( (ShortcutInfo)icon.getItemInfo() ).appInfo;
						ArrayList<ApplicationInfo> viewList_applicationInfo = new ArrayList<ApplicationInfo>();
						for( View3D mView3D : viewList )
						{
							viewList_applicationInfo.add( ( (ShortcutInfo)( (Icon3D)mView3D ).getItemInfo() ).appInfo );
						}
						int quiet_apps_idx = mItemInfos.indexOf( quiet_applicationInfo );
						// 需要先更新生成文件夹前，移动了的icon位置
						updateCurrentAllAppRecorder();
						if( quiet_appInfo.location_in_mainmenu == -1 )
						{
							folderInfo.location_in_mainmenu = mTargetCellIndex;
						}
						else
						{
							folderInfo.location_in_mainmenu = quiet_appInfo.location_in_mainmenu;
						}
						AppListDB.getInstance().addOrMoveItem( folderInfo , LauncherSettings.Favorites.CONTAINER_APPLIST );
						iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
						AddFolderAndRemoveAppInApps( folderInfo , quiet_applicationInfo , viewList_applicationInfo , quiet_apps_idx );
						// 把当前的Folder增加入 Draglayer
						String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
						this.drageTarget_new_child = folderInfoMap.get( folderMap_key );
						viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
						res = true;
					}
				}
				break;
			}
		}
		onDropLeave();
		return res;
	}
	
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		boolean dropRes = true;
		// xiatian add start
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			folderAreaStayTime = 0;
			dropRes = addDrageViewIntoAppList( list , (int)x , (int)y );
			onDropCompleted( list.get( 0 ) , true );
		}
		else
		{
			List<View3D> iconGroupInput = new ArrayList<View3D>();
			for( View3D view : list )
			{
				if( view instanceof ViewCircled3D )
				{
					iconGroupInput.add( (View3D)view );
				}
			}
			// xiatian start //for mainmenu sort by user
			// if (iconGroupInput.size() > 1) //xiatian del
			if( list.size() > 1 )// xiatian add
			// xiatian end
			{
			}
			else
			{
				View3D view = list.get( 0 );
				float oldX = view.x + view.getParent().x;
				float oldY = view.y + view.getParent().y;
				// xiatian del start //for mainmenu sort by user
				// //重新判断drop type
				// mTargetCellIndex = findNearestCellIndex((int)x, (int)y);
				//
				// int isOccupy = GetViewIsOccupy(view, mTargetCellIndex);
				//
				// if(isOccupy == ICON_DROP_OVER_NONE){
				// cellDropType = CELL_DROPTYPE_SINGLE_DROP;
				// }
				// else{
				// if(folderAreaStayTime == 0)
				// {
				// cellDropType = CELL_DROPTYPE_SINGLE_DROP;
				// }
				// else
				// {
				// if(System.currentTimeMillis() - folderAreaStayTime >
				// AppList3D.ICON_DROP_GEN_FOLDER_STAY_TIMER)
				// {
				// cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
				// }
				// else
				// cellDropType = CELL_DROPTYPE_SINGLE_DROP;
				//
				// folderAreaStayTime = 0;
				// }
				// }
				// xiatian del end
				{
					// group.setScreen(index);
					folderAreaStayTime = 0; // xiatian add //for mainmenu sort
											// by user
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
					// teapotXu add start for folder in Mainmenu
					// no need to add this info into DB
					// Root3D.addOrMoveDB(info,LauncherSettings.Favorites.CONTAINER_APPLIST);
					// teapotXu add end
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
		}
		// xiatian add end
		// xiatian end
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
			if( x < 0 || y < 0 || y > ( this.y + this.height ) )
			{
				// 超出applist的范围，返回false
				return false;
			}
			InitDragLayerData();
			if( view instanceof ViewCircled3D )
			{
				mTargetCellIndex = findNearestCellIndex( (int)x , (int)y );
				if( mTargetCellIndex != -1 )
				{
					if( mTargetCellIndex != lastDropTargetCellIndex )
					{
						isWaitingPush = true;
						dropOverOneCellIdxStayTime = System.currentTimeMillis();
						lastDropTargetCellIndex = mTargetCellIndex;
						iconDropOverType = ICON_DROP_OVER_NONE;
						if( DefaultLayout.miui_generate_folder_anim == true )
						{
							if( dropOverFolder != null )
							{
								dropOverFolder.setVisible( false );
								this.removeView( dropOverFolder );
								dropOverFolder = null;
							}
							haveEstablishFolder = false;
							if( FolderLargeTween != null )
							{
								FolderLargeTween.free();
								FolderLargeTween = null;
							}
						}
					}
					if( isWaitingPush )
					{
						if( System.currentTimeMillis() - dropOverOneCellIdxStayTime > 300 )
						{
							int isOccupy = GetViewIsOccupy( view , mTargetCellIndex );
							if( DefaultLayout.enhance_generate_mainmenu_folder_condition == true )
							{
								if( isOccupy != ICON_DROP_OVER_NONE )
								{
									isOccupy = enhance_generate_folder_condition( mTargetCellIndex , (int)x , (int)y );
								}
							}
							iconDropOverType = isOccupy;
							if( iconDropOverType == ICON_DROP_OVER_NONE )
							{
								needAddDragViewBackToFolder = false;
								if( DefaultLayout.mainmenu_sort_by_user_fun == true )
								{
									GridView3D cur_grid_view = ( (GridView3D)view_list.get( page_index ) );
									( cur_grid_view ).handleGridViewTouchDrag( view , x , y );
								}
								cellDropType = CELL_DROPTYPE_SINGLE_DROP;
							}
							else if( iconDropOverType == ICON_DROP_OVER_ICON )
							{
								if( DefaultLayout.miui_generate_folder_anim == true )
								{
									if( !haveEstablishFolder )
									{
										dropOverFolder = cellMakeFolder( mTargetCellIndex , false , true );
										haveEstablishFolder = true;
									}
									else
									{
										if( dropOverFolder == null )
										{
											dropOverFolder = cellMakeFolder( mTargetCellIndex , false , true );
										}
										haveEstablishFolder = true;
									}
								}
								else
								{
									cellMakeFolder( mTargetCellIndex , false , true );
								}
								// xiatian add end
								// xiatian end
								cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
							}
							else
							{
							}
							isWaitingPush = false;
						}
					}
				}
				else
				{
					lastDropTargetCellIndex = -1;
					dropOverOneCellIdxStayTime = 0;
				}
			}
			// xiatian add start //for mainmenu sort by user
			else if( view instanceof FolderIcon3D )
			{
				if( DefaultLayout.mainmenu_sort_by_user_fun == true )
				{
					GridView3D cur_grid_view = ( (GridView3D)view_list.get( page_index ) );
					( cur_grid_view ).handleGridViewTouchDrag( view , x , y );
				}
				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
			}
			// xiatian add end
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
	public static int iconDropOverType = ICON_DROP_OVER_NONE;
	public static boolean cellChanged = false;
	public static boolean isWaitingPush = false;
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
			if( DefaultLayout.enhance_generate_mainmenu_folder_condition == true && DefaultLayout.mainmenu_sort_by_user_fun == false// xiatian
																																	// add
																																	// //for
																																	// mainmenu
																																	// sort
																																	// by
																																	// user
			)
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
				if( ( cur_view_item.name.equals( view.name ) && view_sInfo.intent.getAction().equals( cur_view_item_sInfo.intent.getAction() ) && view_sInfo.intent.getComponent().equals(
						cur_view_item_sInfo.intent.getComponent() ) ) || ( cur_view_item_sInfo.appInfo != null && cur_view_item_sInfo.appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true ) )
				{
					// the same icon or it is virtual icon, do nothing
					Log.v( "cooee" , "------Applist3D ---GetViewIsOccupy---- this two view is the same ---- " );
					drop_status = ICON_DROP_OVER_NONE;
				}
				else
				{
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
					drop_status = ICON_DROP_OVER_FOLDER;
				}
			}
		}
		else
		{
			drop_status = ICON_DROP_OVER_NONE;
		}
		return drop_status;
	}
	
	private View3D cellMakeFolder(
			int item_index ,
			boolean preview ,
			boolean compress )
	{
		View3D folder;
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		// cellToPoint(cellX, cellY, mTargetPoint);
		folder = createVirturFolder( compress );
		folder.x = cur_view.getChildAt( item_index ).getX() + ( app_icon_width - folder.width ) / 2;
		folder.y = cur_view.getChildAt( item_index ).getY() + ( app_icon_height - folder.height ) / 2;
		// xiatian add start
		if( DefaultLayout.miui_generate_folder_anim == true )
		{
			folder.y += folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight();
			this.addViewAt( 0 , folder );
			FolderLargeAnim( 0.3f , folder );
		}
		else
		{
			if( preview )
			{
				Color color = folder.getColor();
				color.a = 0.5f;
				folder.setColor( color );
			}
		}
		// xiatian add end
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
		// xiatian add start //for miui generate folder animation
		if( DefaultLayout.miui_generate_folder_anim == true )
		{
			haveEstablishFolder = false;
			if( FolderLargeTween != null )
			{
				FolderLargeTween.free();
				FolderLargeTween = null;
			}
			if( dropOverFolder != null )
			{
				this.removeView( dropOverFolder );
				dropOverFolder = null;
			}
		}
		// xiatian add end
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_sort_by_user_fun == true )
		{
			cellDropType = CELL_DROPTYPE_SINGLE_DROP;
		}
		// xiatian add end
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
	
	public void resetAppIconsStatusInFolder(
			FolderIcon3D folderIcon )
	{
		// xiatian add start //for mainmenu sort by user
		// xiatian del start
		// //需要重置Folder中的ICon的状态
		// for(int index = 0; index < folderIcon.getChildCount();index++)
		// {
		// View3D folder_child = folderIcon.getChildAt(index);
		//
		// if(folder_child != null && folder_child instanceof Icon3D)
		// {
		// ((Icon3D)folder_child).clearState();
		//
		// if(((Icon3D)folder_child).getColor().a != 1.0f)
		// {
		// ((Icon3D)folder_child).getColor().a = 1.0f;
		//
		// }
		//
		// if(this.mode == APPLIST_MODE_UNINSTALL)
		// {
		// ((Icon3D)folder_child).showUninstall();
		// }
		// else if(this.mode == APPLIST_MODE_HIDE)
		// {
		// ((Icon3D)folder_child).showHide();
		// }
		// }
		// }
		// xiatian del end
		// xiatian add start
		// 需要重置Folder中的ICon的状态
		UserFolderInfo mUserFolderInfo = (UserFolderInfo)folderIcon.getItemInfo();
		if( mUserFolderInfo.opened == false )
		{
			for( int index = 0 ; index < folderIcon.getChildCount() ; index++ )
			{
				View3D folder_child = folderIcon.getChildAt( index );
				if( folder_child != null && folder_child instanceof Icon3D )
				{
					Icon3D mIcon3D = (Icon3D)folder_child;
					mIcon3D.clearState();
					if( mIcon3D.getColor().a != 1.0f )
					{
						mIcon3D.getColor().a = 1.0f;
					}
					if( this.mode == APPLIST_MODE_UNINSTALL )
					{
						mIcon3D.showUninstall();
					}
					else if( this.mode == APPLIST_MODE_HIDE )
					{
						mIcon3D.showHide();
					}
				}
			}
		}
		else
		{
			FolderMIUI3D mFolderMIUI3D = folderIcon.mFolderMIUI3D;
			if( this.mode == APPLIST_MODE_UNINSTALL )
			{
				mFolderMIUI3D.showButtonAdd();
			}
			for( int i = 0 ; i < mFolderMIUI3D.getChildCount() ; i++ )
			{
				View3D mView3D = mFolderMIUI3D.getChildAt( i );
				if( mView3D instanceof GridView3D )
				{
					GridView3D mGridView3D = (GridView3D)mView3D;
					for( int j = 0 ; j < mGridView3D.getChildCount() ; j++ )
					{
						View3D folder_child = mGridView3D.getChildAt( j );
						if( folder_child != null && folder_child instanceof Icon3D )
						{
							Icon3D mIcon3D = (Icon3D)folder_child;
							mIcon3D.clearState();
							if( mIcon3D.getColor().a != 1.0f )
							{
								mIcon3D.getColor().a = 1.0f;
							}
							if( this.mode == APPLIST_MODE_UNINSTALL )
							{
								mIcon3D.showUninstall();
							}
							else if( this.mode == APPLIST_MODE_HIDE )
							{
								mIcon3D.showHide();
							}
						}
					}
				}
			}
		}
		// xiatian add end
		// xiatian end
	}
	
	// xiatian del start //for mainmenu sort by user
	// public void removeDragViews(ArrayList<View3D> removedViewList)
	// {
	// //ArrayList<ApplicationInfo> remove_appList = new
	// ArrayList<ApplicationInfo>();
	// ArrayList<ItemInfo> remove_appList = new ArrayList<ItemInfo>();
	//
	// for(View3D dragview: removedViewList)
	// {
	// if(dragview != null && dragview instanceof Icon3D)
	// {
	// Icon3D dragIcon3D = (Icon3D)dragview;
	// ItemInfo dragItemInfo = dragIcon3D.getItemInfo();
	//
	// if(dragItemInfo!= null && dragItemInfo instanceof ShortcutInfo)
	// {
	// //获取到view 对应的ApplicationInfo
	// ApplicationInfo quiet_applicationInfo =
	// ((ShortcutInfo)dragItemInfo).appInfo;
	// remove_appList.add((ItemInfo)quiet_applicationInfo);
	// Root3D.deleteFromDB(quiet_applicationInfo);
	// }
	// }
	// else if(dragview != null && dragview instanceof FolderIcon3D)
	// {
	// ItemInfo folderInfo = ((FolderIcon3D)dragview).getItemInfo();
	// remove_appList.add(folderInfo);
	// //when delete the folder in the DB, it also need to delete the folderIcon
	// in folderInfoMap
	// String folderMap_key = ICON_MAP_FOLDER_LABEL +
	// ((UserFolderInfo)folderInfo).id;
	// folderInfoMap.remove(folderMap_key);
	//
	// Root3D.deleteFromDB(folderInfo);
	// }
	// }
	//
	// mItemInfos.removeAll(remove_appList);
	//
	// sortApp(sortId, false);
	// syncAppsPages();
	// startAnimation();
	//
	// }
	// xiatian del end
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
			//			if( ( child instanceof FolderIcon3D ) && ( ( (FolderIcon3D)child ).getFolderIconNum() == 0 ) && ( DefaultLayout.mainmenu_sort_by_user_fun == false )// xiatian add	//for mainmenu sort by user
			//			)
			if( false ) // modified by hugo.ye 允许空文件夹的存在
			{
				// 那么不需要发送 add_DrageLayer 消息,同时remove掉cur_view 下的空 Folder
				ArrayList<View3D> removedViewList = new ArrayList<View3D>();
				removedViewList.add( child );
				// xiatian start //for mainmenu sort by user
				// removeDragViews(removedViewList);//xiatian del
				// xiatian add start
				if( DefaultLayout.mainmenu_sort_by_user_fun == true )
				{
					for( View3D view : removedViewList )
					{
						HandleItemInEditModeOperation( view , GRIDVIEW_EDITMODE_REMOVE_ITEM , false );
					}
					removeDragViews( removedViewList , false );
					// and alos remove this folderIcon in appHost
					FolderIcon3D folder = (FolderIcon3D)child;
					String folderMap_key = ICON_MAP_FOLDER_LABEL + folder.getItemInfo().id;
					FolderIcon3D folderIcon = folderInfoMap.get( folderMap_key );
					if( folderIcon != null )
					{
						folderInfoMap.remove( folderMap_key );
					}
					folder.remove();
				}
				else
				{
					removeDragViews( removedViewList , true );
				}
				// xiatian add end
				// xiatian end
				CleanDropStatus();
				return;
			}
			drageTarget_new_child = child;
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
			// xiatian add start //for mainmenu sort by user
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				// add back foldicon
				ViewGroup3D curGridView = (ViewGroup3D)view_list.get( page_index );
				int index = ( (GridView3D)curGridView ).getIndex( x , y );
				if( index > curGridView.getChildCount() )
				{
					index = curGridView.getChildCount();
				}
				curGridView.addViewAt( index , child );
				if( this.mode == APPLIST_MODE_UNINSTALL )
				{
					( (FolderIcon3D)child ).clearState();
					( (FolderIcon3D)child ).showUninstall( false );
				}
				if( ( this.mode == APPLIST_MODE_UNINSTALL ) && ( mNeedRefreshListWhenColseFolder == false ) )
				{
					return;
				}
				mNeedRefreshListWhenColseFolder = false;
			}
			// xiatian add end
		}
		else // xiatian add //for mainmenu sort by user
		// when child is not in mItemInfos, so add it---fix bug0002651
		if( child instanceof Icon3D && ( (Icon3D)child ).getItemInfo() != null )
		{
			ShortcutInfo sItemInfo = (ShortcutInfo)( (Icon3D)child ).getItemInfo();
			if( sItemInfo.appInfo != null && !mItemInfos.contains( sItemInfo.appInfo ) )
			{
				// xiatian add start //for mainmenu sort by user
				// last position
				if( DefaultLayout.mainmenu_sort_by_user_fun == true )
				{
					if( sItemInfo.location_in_mainmenu < 0 || sItemInfo.location_in_mainmenu > mItemInfos.size() - 1 )
					{
						sItemInfo.appInfo.location_in_mainmenu = mItemInfos.size() - 1;
					}
				}
				// xiatian add end
				mItemInfos.add( sItemInfo.appInfo );
			}
		}
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
		CleanDropStatus();// group.cellCleanDropStatus();
	}
	
	// teapotXu add end for Folder in Mainmenu
	// zjp
	public ArrayList<ShortcutInfo> getAllAppFrequency()
	{
		ArrayList<ShortcutInfo> allApp = new ArrayList<ShortcutInfo>();
		for( int i = 0 ; i < mItemInfos.size() ; i++ )
		{
			ItemInfo itemInfo = mItemInfos.get( i );
			if( itemInfo instanceof ApplicationInfo )
			{
				if( ( (ApplicationInfo)itemInfo ).intent.getBooleanExtra( "isApplistVirtualIcon" , false ) != true )
				{
					allApp.add( new ShortcutInfo( (ApplicationInfo)itemInfo ) );
				}
			}
			else
			{
				if( itemInfo instanceof UserFolderInfo )
				{
					ArrayList<ShortcutInfo> list = ( (UserFolderInfo)itemInfo ).contents;
					for( ShortcutInfo info : list )
					{
						allApp.add( info );
					}
				}
			}
		}
		Collections.sort( allApp , new ByFrequency() );
		return allApp;
	}
	
	class ByFrequency implements Comparator<ShortcutInfo>
	{
		
		@Override
		public int compare(
				ShortcutInfo info1 ,
				ShortcutInfo info2 )
		{
			// TODO Auto-generated method stub
			int frequency1 = info1.getUseFrequency();
			int frequency2 = info2.getUseFrequency();
			return -( frequency1 - frequency2 );
		}
	}
	
	// xiatian add start //EffectPreview
	public void setApplistEffectPreview3D(
			View3D v )
	{
		this.mApplistEffectPreview = (EffectPreview3D)v;
	}
	
	public void setEffectPreviewTips3D(
			View3D v )
	{
		this.mEffectPreviewTips3D = (EffectPreviewTips3D)v;
	}
	
	public void startPreviewEffect(
			boolean isFirst )
	{
		if( mEffectPreviewTips3D.isVisible() )
		{
			mEffectPreviewTips3D.hide();
		}
		super.startPreviewEffect( isFirst );
	}
	
	public boolean isPreviewEffectFinished()
	{
		if( mPreviewTween == null || mPreviewTween.isFinished() )
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		if( ( DefaultLayout.enable_effect_preview ) && ( mApplistEffectPreview != null && mApplistEffectPreview.isVisible() ) || ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		return super.onDoubleClick( x , y );
	}
	
	// xiatian add end
	public float getXScale()
	{
		return xScale;
	}
	
	// xiatian add start //for mainmenu sort by user
	// this function handle the operation of items in AppList when in edit mode
	private void HandleItemInEditModeOperation(
			View3D view ,
			int operation ,
			boolean is_folder_opened )
	{
		int lastAppPageIndex = appPageCount - 1;
		int curIndex = page_index;
		ViewGroup3D curGridView = (ViewGroup3D)view_list.get( curIndex );
		switch( operation )
		{
			case GRIDVIEW_EDITMODE_ADD_ITEM:
				View3D addview = view;
				boolean is_addview_added = false;
				for( int pageIndex = curIndex ; pageIndex <= lastAppPageIndex ; pageIndex++ )
				{
					ViewGroup3D tempGridView = (ViewGroup3D)view_list.get( pageIndex );
					if( pageIndex < lastAppPageIndex )
					{
						// 非最后一页
						// 需要把当前页的最后一项icon添加到后一页中，把addview添加到该页的最后一项,以此类推
						int nextIndex = pageIndex + 1;
						View3D lastView = tempGridView.getChildAt( tempGridView.getChildCount() - 1 );
						ViewGroup3D nextGridView = (ViewGroup3D)view_list.get( nextIndex );
						nextGridView.addViewAt( 0 , lastView );
						if( pageIndex == curIndex )
						{
							tempGridView.addView( addview );
							is_addview_added = true;
						}
					}
					else
					{
						// 最后一页
						// 如果有空余位置，那么添加到最后即可
						boolean is_need_new_grid = false;
						if( is_addview_added == false )
						{
							if( ( is_folder_opened == true && ( tempGridView.getChildCount() < mCellCountX * mCellCountY - 1 ) && DefaultLayout.mainmenu_sort_by_user_fun == false// xiatian
																																													// add
																																													// //for
																																													// mainmenu
																																													// sort
																																													// by
																																													// user
							)
							// xiatian add start //for mainmenu sort by user
							|| ( is_folder_opened == true && ( tempGridView.getChildCount() < mCellCountX * mCellCountY - 2 ) && DefaultLayout.mainmenu_sort_by_user_fun )
							// xiatian add end
							|| ( is_folder_opened == false && ( tempGridView.getChildCount() < mCellCountX * mCellCountY ) ) )
							{
								tempGridView.addView( addview );
								is_addview_added = true;
								is_need_new_grid = false;
							}
							else
							{
								is_need_new_grid = true;
							}
						}
						else
						{
							if( tempGridView.getChildCount() > mCellCountX * mCellCountY )
							{
								is_need_new_grid = true;
							}
						}
						if( is_need_new_grid == true )
						{
							// 当前为最后一页，且已经排满，此时需要重新增加一页gridview
							GridView3D newgrid = appGridPool.get();
							View3D lastView = tempGridView.getChildAt( tempGridView.getChildCount() - 1 );
							newgrid.enableAnimation( false );
							newgrid.transform = true;
							addPage( lastAppPageIndex + 1 , newgrid );
							appPageCount++;
							newgrid.setAutoDrag( false );
							newgrid.addView( lastView );
							if( is_addview_added == false )
							{
								tempGridView.addView( addview );
								is_addview_added = true;
							}
						}
					}
				}
				break;
			case GRIDVIEW_EDITMODE_REMOVE_ITEM:
				View3D removeView = view;
				int MAX_ITEMS_GRIDVIEW = mCellCountX * mCellCountY;
				if( curIndex < lastAppPageIndex && curGridView.getChildCount() < MAX_ITEMS_GRIDVIEW )
				{
					// 非最后一页且当前页中的item个数不满 mCellCountX * mCellCountY
					// 需要由后一页的item填满,以此类推
					for( int pageIndex = curIndex ; pageIndex <= lastAppPageIndex ; pageIndex++ )
					{
						ViewGroup3D gridView = (ViewGroup3D)view_list.get( pageIndex );
						if( pageIndex < lastAppPageIndex && gridView.getChildCount() < MAX_ITEMS_GRIDVIEW )
						{
							ViewGroup3D nextGridview = (ViewGroup3D)view_list.get( pageIndex + 1 );
							for( int index = 0 ; index < MAX_ITEMS_GRIDVIEW ; index++ )
							{
								if( index < nextGridview.getChildCount() )
								{
									View3D tempView = nextGridview.getChildAt( index );
									gridView.addView( tempView );
									if( gridView.getChildCount() == MAX_ITEMS_GRIDVIEW )
										break;
								}
								else
								{
									Log.e(
											"cooee" ,
											"AppList3D --- HandleItemInEditModeOperation --- GRIDVIEW_EDITMODE_REMOVE_ITEM --- error --- no enough items: gridView:" + gridView.getChildCount() );
								}
							}
						}
						else if( pageIndex == lastAppPageIndex )
						{
							if( gridView.getChildCount() == 0 )
							{
								// 最后一页，
								// 需要remove掉
								this.removeView( gridView );
							}
						}
					}
				}
				break;
			case GRIDVIEW_EDITMODE_MOVE_ITEM_PRE:
				View3D dragView = view;
				int preIndex = page_index - 1;
				if( preIndex >= 0 )
				{
					// 需要把前一页的最后一项icon添加到当前页中，把当前的dragView添加到前一页
					ViewGroup3D preGridView = (ViewGroup3D)view_list.get( preIndex );
					View3D lastViewInPregridView = preGridView.getChildAt( preGridView.getChildCount() - 1 );
					View3D realViewInCurGridView = ( (GridView3D)curGridView ).getChildViewInGridView( dragView );
					curGridView.addViewAt( 0 , lastViewInPregridView );
					preGridView.addView( realViewInCurGridView );
				}
				break;
			case GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT:
				// 需要把后一页的第一项增加到当前页的最后一项中，dragView 需要增加到后一页中
				View3D dragView2 = view;
				int nextIndex = page_index + 1;
				if( nextIndex <= lastAppPageIndex )
				{
					ViewGroup3D nextGridView = (ViewGroup3D)view_list.get( nextIndex );
					View3D lastViewInNextgridView = nextGridView.getChildAt( 0 );
					View3D realViewInCurGridView = ( (GridView3D)curGridView ).getChildViewInGridView( dragView2 );
					curGridView.addView( lastViewInNextgridView );
					nextGridView.addViewAt( 0 , realViewInCurGridView );
				}
				break;
		}
	}
	
	private void updateCurrentAllAppRecorder()
	{
		// 此时的gridView中的children的顺序就是排列的顺序，需要把它保存下来，
		ArrayList<ItemInfo> itemsList = new ArrayList<ItemInfo>();
		// 遍历更新mItemInfos中item的location_in_mainmenu，并记录需要修改数据库的item
		for( int pageIndex = 0 ; pageIndex < appPageCount ; pageIndex++ )
		{
			GridView3D layout = (GridView3D)view_list.get( pageIndex );
			int numCells = mCellCountX * mCellCountY;
			for( int iconIdx = 0 ; iconIdx < layout.getChildCount() ; iconIdx++ )
			{
				View3D icon = layout.getChildAt( iconIdx );
				int location = pageIndex * numCells + iconIdx;
				if( icon instanceof Icon3D )
				{
					ApplicationInfo appInfo = ( (ShortcutInfo)( (Icon3D)icon ).getItemInfo() ).appInfo;
					if( ( appInfo.container == LauncherSettings.Favorites.CONTAINER_APPLIST ) && ( location != appInfo.location_in_mainmenu ) )
					{
						appInfo.location_in_mainmenu = location;
						itemsList.add( appInfo.makeShortcut() );
					}
				}
				else if( icon instanceof FolderIcon3D )
				{
					FolderInfo folderInfo = (FolderInfo)( (FolderIcon3D)icon ).getItemInfo();
					if( location != folderInfo.location_in_mainmenu )
					{
						folderInfo.location_in_mainmenu = location;
						itemsList.add( folderInfo );
					}
				}
			}
		}
		// 更新mItemInfos中需要修改数据库的item
		AppListDB.getInstance().BatchItemsUpdate( itemsList );
		// 此时需要更换排序的规则
		if( sortId != SORT_BY_USER )
		{
			sortId = SORT_BY_USER;
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putInt( "sort_app" , sortId ).commit();
		}
	}
	
	private void updateItemsLoactionAfterRemoved(
			int mMovedindex )
	{
		ArrayList<ItemInfo> updating_items = new ArrayList<ItemInfo>();
		if( mMovedindex == -1 )
		{
			return;
		}
		String removed_location_str = "" + mMovedindex;
		Cursor db_cursor = AppListDB.getInstance().query( "int1>?" , new String[]{ removed_location_str } );
		if( db_cursor != null )
		{
			while( db_cursor.moveToNext() )
			{
				int itemType = db_cursor.getInt( db_cursor.getColumnIndexOrThrow( "item_type" ) );
				int location_in_mainmenu = db_cursor.getInt( db_cursor.getColumnIndexOrThrow( "int1" ) );
				int container = db_cursor.getInt( db_cursor.getColumnIndexOrThrow( "container" ) );
				int id = db_cursor.getInt( db_cursor.getColumnIndexOrThrow( "id" ) );
				if( itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
				{
					if( container != LauncherSettings.Favorites.CONTAINER_APPLIST )
					{
						continue;
					}
					ShortcutInfo updating_item_sInfo = new ShortcutInfo();
					updating_item_sInfo.id = id;
					updating_item_sInfo.location_in_mainmenu = location_in_mainmenu - 1;
					updating_items.add( updating_item_sInfo );
				}
				else if( itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
				{
					for( int index = 0 ; index < mItemInfos.size() ; index++ )
					{
						ItemInfo itemInfo = mItemInfos.get( index );
						if( itemInfo instanceof UserFolderInfo )
						{
							UserFolderInfo mUserFolderInfo = new UserFolderInfo();
							mUserFolderInfo.id = itemInfo.id;
							mUserFolderInfo.location_in_mainmenu = itemInfo.location_in_mainmenu;
							if( mUserFolderInfo.location_in_mainmenu == location_in_mainmenu )
							{
								mUserFolderInfo.location_in_mainmenu--;
								updating_items.add( mUserFolderInfo );
							}
						}
					}
				}
			}
		}
		AppListDB.getInstance().BatchItemsUpdateLocation( updating_items );
		updatemItemsLoactionAfterIndex( mMovedindex );
	}
	
	private void updatemItemsLoactionAfterIndex(
			int mIndex )
	{
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			ItemInfo itemInfo = mItemInfos.get( index );
			if( itemInfo.location_in_mainmenu > mIndex )
				itemInfo.location_in_mainmenu--;
		}
	}
	
	private int enhance_generate_folder_condition(
			int nearestCellIndex ,
			int pixelX ,
			int pixelY )
	{
		ViewGroup3D cur_view_group = (ViewGroup3D)view_list.get( page_index );
		int drop_over_type = ICON_DROP_OVER_NONE;
		if( nearestCellIndex >= 0 && nearestCellIndex < cur_view_group.getChildCount() )
		{
			View3D nearestCellView = cur_view_group.getChildAt( nearestCellIndex );
			float avail_range_radius = nearestCellView.getWidth() / 3;
			float cell_view_center_x = nearestCellView.getX() + nearestCellView.getWidth() / 2;
			float cell_view_center_y = nearestCellView.getY() + nearestCellView.getHeight() / 2;
			float cell_view_icon_center_y = cell_view_center_y - ( nearestCellView.getHeight() - Utils3D.getIconBmpHeight() ) / 2;
			float avail_range_radiusY = Utils3D.getIconBmpHeight() / 2;
			if( pixelX < ( cell_view_center_x - avail_range_radius ) || pixelX > ( cell_view_center_x + avail_range_radius ) || pixelY < ( cell_view_icon_center_y - avail_range_radiusY ) || pixelY > ( cell_view_icon_center_y + avail_range_radiusY ) )
			{
				drop_over_type = ICON_DROP_OVER_NONE;//即使是在图标或文件夹上 也要挤走
			}
			else
			{
				if( nearestCellView instanceof Icon3D )
				{
					drop_over_type = ICON_DROP_OVER_ICON;//将生成文件夹
				}
				else if( nearestCellView instanceof FolderIcon3D )
				{
					drop_over_type = ICON_DROP_OVER_FOLDER;//将放进文件夹
				}
			}
		}
		return drop_over_type;
	}
	
	private void AddFolderAndRemoveAppInApps(
			ItemInfo folderInfo ,
			ApplicationInfo quietAppInfo ,
			ApplicationInfo movingAppInfo ,
			int insert_idx )
	{
		ArrayList<ApplicationInfo> remove_appList = new ArrayList<ApplicationInfo>();
		remove_appList.add( quietAppInfo );
		remove_appList.add( movingAppInfo );
		mItemInfos.add( insert_idx , folderInfo );
		mItemInfos.removeAll( remove_appList );
		if( DefaultLayout.mainmenu_sort_by_user_fun == true )
		{
			// 因为移除item之后，导致各个位置index需要前移，更新数据库
			updateItemsLoactionAfterRemoved( movingAppInfo.location_in_mainmenu );
		}
		ArrayList<ItemInfo> updating_items = new ArrayList<ItemInfo>();
		if( quietAppInfo.location_in_mainmenu != -1 )
		{
			quietAppInfo.location_in_mainmenu = -1;
			ShortcutInfo updating_item_sInfo = new ShortcutInfo();
			updating_item_sInfo.id = quietAppInfo.id;
			updating_item_sInfo.location_in_mainmenu = quietAppInfo.location_in_mainmenu;
			updating_items.add( updating_item_sInfo );
		}
		if( movingAppInfo.location_in_mainmenu != -1 )
		{
			movingAppInfo.location_in_mainmenu = -1;
			ShortcutInfo updating_item_sInfo = new ShortcutInfo();
			updating_item_sInfo.id = movingAppInfo.id;
			updating_item_sInfo.location_in_mainmenu = movingAppInfo.location_in_mainmenu;
			updating_items.add( updating_item_sInfo );
		}
		AppListDB.getInstance().BatchItemsUpdateLocation( updating_items );
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	public void removeDragViews(
			ArrayList<View3D> removedViewList ,
			boolean b_sync_app )
	{
		ArrayList<ItemInfo> remove_appList = new ArrayList<ItemInfo>();
		if( DefaultLayout.mainmenu_sort_by_user_fun == true && b_sync_app )
		{
			updateCurrentAllAppRecorder();
		}
		for( View3D dragview : removedViewList )
		{
			ItemInfo removeInfo = null;
			if( dragview != null && dragview instanceof Icon3D )
			{
				Icon3D dragIcon3D = (Icon3D)dragview;
				ItemInfo dragItemInfo = dragIcon3D.getItemInfo();
				if( dragItemInfo != null && dragItemInfo instanceof ShortcutInfo )
				{
					// 获取到view 对应的ApplicationInfo
					ApplicationInfo quiet_applicationInfo = ( (ShortcutInfo)dragItemInfo ).appInfo;
					remove_appList.add( (ItemInfo)quiet_applicationInfo );
					removeInfo = dragItemInfo;
					if( DefaultLayout.mainmenu_sort_by_user_fun == false )// xiatian add	//for mainmenu sort by user
					{
						Root3D.deleteFromDB( dragItemInfo );
					}
				}
			}
			else if( dragview != null && dragview instanceof FolderIcon3D )
			{
				ItemInfo folderInfo = ( (FolderIcon3D)dragview ).getItemInfo();
				remove_appList.add( folderInfo );
				// when delete the folder in the DB, it also need to delete the
				// folderIcon in folderInfoMap
				String folderMap_key = ICON_MAP_FOLDER_LABEL + ( (UserFolderInfo)folderInfo ).id;
				folderInfoMap.remove( folderMap_key );
				Root3D.deleteFromDB( folderInfo );
			}
			if( DefaultLayout.mainmenu_sort_by_user_fun == true )
			{
				if( removeInfo != null )
				{
					if( removeInfo instanceof ShortcutInfo )
					{
						if( ( (ShortcutInfo)removeInfo ).appInfo != null )
						{
							removeInfo.location_in_mainmenu = ( (ShortcutInfo)removeInfo ).appInfo.location_in_mainmenu;
						}
					}
					updateItemsLoactionAfterRemoved( removeInfo.location_in_mainmenu );
					ArrayList<ItemInfo> updating_items = new ArrayList<ItemInfo>();
					if( ( removeInfo.location_in_mainmenu != -1 ) || ( ( removeInfo instanceof ShortcutInfo ) && ( ( (ShortcutInfo)removeInfo ).appInfo != null ) && ( ( (ShortcutInfo)removeInfo ).appInfo.location_in_mainmenu != -1 ) ) )
					{
						removeInfo.location_in_mainmenu = -1;
						if( ( removeInfo instanceof ShortcutInfo ) && ( ( (ShortcutInfo)removeInfo ).appInfo != null ) )
						{
							( (ShortcutInfo)removeInfo ).appInfo.location_in_mainmenu = -1;
						}
						ShortcutInfo updating_item_sInfo = new ShortcutInfo();
						updating_item_sInfo.id = removeInfo.id;
						updating_item_sInfo.location_in_mainmenu = removeInfo.location_in_mainmenu;
						updating_items.add( updating_item_sInfo );
					}
					AppListDB.getInstance().BatchItemsUpdateLocation( updating_items );
				}
			}
		}
		mItemInfos.removeAll( remove_appList );
		if( b_sync_app == true )
		{
			sortApp( sortId , false );
			syncAppsPages();
			startAnimation();
		}
	}
	
	public void resetApplistIconState()
	{
		refresh();
		if( mOpenFolderIcon != null )
		{
			resetAppIconsStatusInFolder( mOpenFolderIcon );
		}
	}
	
	private void sortAppByDefault()
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
				if( appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
				{
					titleKey[i] = "0000000";
					priorityKey[i] = true;
				}
				else
				{
					titleKey[i] = appInfo.title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一样
					if( DefaultLayout.getInstance().hasReplaceIcon( appInfo.componentName.getPackageName() , appInfo.componentName.getClassName() ) )
					{
						priorityKey[i] = true;
					}
				}
			}
			else
			{
				if( ( (FolderInfo)itemInfo ).sort_folder_name != null )
				{
					titleKey[i] = ( (FolderInfo)itemInfo ).sort_folder_name.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一样
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
					titleKey[i] = ( (FolderInfo)itemInfo ).title.toString().replaceAll( " " , "" ).replaceAll( " " , "" );// 注意！！两个空格不一样
				}
			}
		}
		cut.sortByDefault( 1 , titleKey , priorityKey , sortArray );
		ArrayList<ItemInfo> ItemInfoList = new ArrayList<ItemInfo>();
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			ItemInfo itemInfo = mItemInfos.get( sortArray[index] );
			itemInfo.location_in_mainmenu = index;
			if( itemInfo instanceof ApplicationInfo )
			{
				ItemInfoList.add( ( (ApplicationInfo)itemInfo ).makeShortcut() );
			}
			else
			{
				ItemInfoList.add( itemInfo );
			}
		}
		AppListDB.getInstance().BatchItemsUpdate( ItemInfoList );
	}
	
	private void checkDuplicatePosition()
	{
		boolean mIsExist = false;
		boolean mIsHaveDuplicate = false;
		boolean mIsNeedSortAppByDefault = false;
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			mIsExist = false;
			mIsHaveDuplicate = false;
			for( int sortArrayIndex = 0 ; sortArrayIndex < mItemInfos.size() ; sortArrayIndex++ )
			{
				if( sortArray[sortArrayIndex] == index )
				{
					if( mIsExist == false )
					{
						mIsExist = true;
					}
					else
					{
						mIsHaveDuplicate = true;
						break;
					}
				}
			}
			if( mIsHaveDuplicate )
			{
				mIsNeedSortAppByDefault = true;
				break;
			}
		}
		if( mIsNeedSortAppByDefault )
		{
			sortAppByDefault();
		}
	}
	
	class FolderIcon3DInAppList3D extends FolderIcon3D
	{
		
		public int oldAppGridIndex;
		public int newAppGridIndex;
		public float oldX;
		public float oldY;
		public boolean oldVisible = false;
		
		public FolderIcon3DInAppList3D(
				UserFolderInfo folderInfo )
		{
			super( folderInfo );
		}
		
		@Override
		public boolean onDropOver(
				ArrayList<View3D> list ,
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			if( iconDropOverType == AppList3D.ICON_DROP_OVER_FOLDER )
			{
				return true;
			}
			return false;
		}
	}
	
	private void breakUpFolderInApplist(
			FolderIcon3D foldericon )
	{
		UserFolderInfo mUserFolderInfo = (UserFolderInfo)foldericon.getItemInfo();
		ArrayList<ShortcutInfo> contents = mUserFolderInfo.contents;
		int location = mUserFolderInfo.location_in_mainmenu;
		ArrayList<ItemInfo> updatingLocation_items = new ArrayList<ItemInfo>();
		ArrayList<ItemInfo> move_items = new ArrayList<ItemInfo>();
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			for( int index = location + 1 ; index < mItemInfos.size() ; index++ )
			{
				ItemInfo itemInfo = mItemInfos.get( sortArray[index] );
				itemInfo.location_in_mainmenu += ( contents.size() - 1 );
				if( itemInfo instanceof ApplicationInfo )
				{
					ApplicationInfo mApplicationInfo = (ApplicationInfo)itemInfo;
					ShortcutInfo updating_item = new ShortcutInfo();
					updating_item.id = mApplicationInfo.id;
					updating_item.location_in_mainmenu = mApplicationInfo.location_in_mainmenu;
					updatingLocation_items.add( updating_item );
				}
				else if( itemInfo instanceof UserFolderInfo )
				{
					UserFolderInfo mFolderInfo = (UserFolderInfo)itemInfo;
					UserFolderInfo updating_item = new UserFolderInfo();
					updating_item.id = mFolderInfo.id;
					updating_item.location_in_mainmenu = mFolderInfo.location_in_mainmenu;
					updatingLocation_items.add( updating_item );
				}
			}
		}
		for( int i = 0 ; i < contents.size() ; i++ )
		{
			ShortcutInfo mShortcutInfo = contents.get( i );
			ApplicationInfo mApplicationInfo = mShortcutInfo.appInfo;
			mApplicationInfo.container = LauncherSettings.Favorites.CONTAINER_APPLIST;
			mShortcutInfo.container = mApplicationInfo.container;
			if( DefaultLayout.mainmenu_sort_by_user_fun )
			{
				mApplicationInfo.location_in_mainmenu = location + i;
				mShortcutInfo.location_in_mainmenu = mApplicationInfo.location_in_mainmenu;
				move_items.add( mShortcutInfo );
				mItemInfos.add( mApplicationInfo );
			}
			else
			{
				move_items.add( mShortcutInfo );
				if( mApplicationInfo.angle < mItemInfos.size() )
				{
					mItemInfos.add( mApplicationInfo.angle , mApplicationInfo );
				}
				else
				{
					mItemInfos.add( mApplicationInfo );
				}
			}
		}
		mItemInfos.remove( mUserFolderInfo );
		iLoongLauncher.getInstance().removeFolder( mUserFolderInfo );
		String folderMap_key = ICON_MAP_FOLDER_LABEL + mUserFolderInfo.id;
		folderInfoMap.remove( folderMap_key );
		AppListDB.getInstance().deleteItem( mUserFolderInfo );
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			AppListDB.getInstance().BatchItemsUpdateLocation( updatingLocation_items );
		}
		AppListDB.getInstance().BatchItemsUpdate( move_items );
		sortApp( sortId , true );
	}
	
	public void setApp2Workspace(
			App2Workspace3D mApp2Workspace3D )
	{
		this.mApp2Workspace = mApp2Workspace3D;
	}
	
	public void show2EditAppBarView()
	{
		if( appBar == null )
			return;
		Timeline animation_line_show = Timeline.createParallel();
		float anim_duration = 0.25f;
		animation_line_show.push( Tween.to( appBar , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , height , 0 ).ease( Linear.INOUT ) );
		animation_line_show.start( View3DTweenAccessor.manager );
	}
	
	public void show2WorkspaceView()
	{
		//		Timeline animation_line_show = Timeline.createParallel();
		//		float anim_duration = 0.25f;
		//		if( appBar != null )
		//			animation_line_show.push( Tween.to( appBar , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , height + appBar.height , 0 ).ease( Linear.INOUT ) );
		//		animation_line_show.push( Tween.to( mApp2Workspace , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , height , 0 ).ease( Linear.INOUT ).delay( anim_duration ) );
		//		animation_line_show.start( View3DTweenAccessor.manager );
	}
	
	public void hide2WorkspaceView()
	{
		//		animation_line_hide = Timeline.createParallel();
		//		float anim_duration = 0.25f;
		//		if( appBar != null )
		//		{
		//			animation_line_hide.push( Tween.to( mApp2Workspace , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , height + appBar.height , 0 ).ease( Linear.INOUT ) );
		//			animation_line_hide.push( Tween.to( appBar , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , height , 0 ).ease( Linear.INOUT ).delay( anim_duration ) );
		//		}
		//		else
		//		{
		//			animation_line_hide.push( Tween.to( mApp2Workspace , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , height , 0 ).ease( Linear.INOUT ) );
		//		}
		//		animation_line_hide.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	private void hide2WorkspaceViewNoAnim()
	{
		if( appBar != null )
			mApp2Workspace.setPosition( 0 , height + appBar.height );
		else
			mApp2Workspace.setPosition( 0 , height );
		mApp2Workspace.areaStayTime = 0;
		mApp2Workspace.setFocus( false );
		//		if( appBar != null )
		//			appBar.setPosition( 0 , height );
	}
	
	public boolean viewBackToFolder(
			View3D mView3D )
	{
		if( needAddDragViewBackToFolder == false )
		{
			needAddDragViewBackToFolder = true;
			return false;
		}
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			GridView3D layout = (GridView3D)view_list.get( i );
			for( int j = 0 ; j < layout.getChildCount() ; j++ )
			{
				View3D child = layout.getChildAt( j );
				if( child instanceof FolderIcon3D )
				{
					FolderIcon3D folder = (FolderIcon3D)child;
					Icon3D mIcon3DToDragView = folder.mFolderMIUI3D.mIcon3DToDragView;
					if( ( mIcon3DToDragView != null ) && ( mIcon3DToDragView == mView3D ) )
					{
						ArrayList<View3D> list = new ArrayList<View3D>();
						Icon3D iconClone = (Icon3D)mView3D.clone();
						list.add( iconClone );
						folder.onDrop( list , 0 , 0 );
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void delEmptyFolder()
	{
		List remove_mUserFolderInfo = new ArrayList();
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			ItemInfo itemInfo = mItemInfos.get( index );
			if( itemInfo instanceof UserFolderInfo )
			{
				UserFolderInfo mUserFolderInfo = (UserFolderInfo)itemInfo;
				if( mUserFolderInfo.contents.size() == 0 )
				{
					//mItemInfos.remove( mUserFolderInfo );
					remove_mUserFolderInfo.add( mUserFolderInfo );
					iLoongLauncher.getInstance().removeFolder( mUserFolderInfo );
					String folderMap_key = ICON_MAP_FOLDER_LABEL + mUserFolderInfo.id;
					folderInfoMap.remove( folderMap_key );
					AppListDB.getInstance().deleteItem( mUserFolderInfo );
					updateItemsLoactionAfterRemoved( mUserFolderInfo.location_in_mainmenu );
				}
			}
		}
		mItemInfos.removeAll( remove_mUserFolderInfo );
	}
	
	public void addAppsAndDelEmptyFolder(
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
			if( ApplistVirtualIconManager.getAppList() == null )
			{
				ApplistVirtualIconManager.setAppList( this );
			}
			ApplistVirtualIconManager.onAppsAdd( list );
			if( !is_need_sync_app )
				return;
		}
		else
		{
			addAppNoRepeat( mApps , list );
			if( ApplistVirtualIconManager.getAppList() == null )
			{
				ApplistVirtualIconManager.setAppList( this );
			}
			ApplistVirtualIconManager.onAppsAdd( list );
		}
		delEmptyFolder();
		// teapotXu add end for Folder in Mainmenu
		sortApp( sortId , false );
		syncAppsPages();
		if( Desktop3DListener.bAppDone )
			startAnimation();
	}
	
	private int GetViewIsOccupy(
			ArrayList<View3D> list ,
			int item_index )
	{
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		if( ( item_index < 0 ) || ( item_index >= cur_view.getChildCount() ) )
		{
			return ICON_DROP_OVER_NONE;
		}
		for( View3D mView3D : list )
		{
			if( mView3D == null || !( mView3D instanceof Icon3D ) )
			{
				return ICON_DROP_OVER_NONE;
			}
		}
		View3D cur_view_item = cur_view.getChildAt( item_index );
		if( cur_view_item instanceof FolderIcon3D )
		{
			return ICON_DROP_OVER_NONE;
		}
		ShortcutInfo cur_view_item_sInfo = (ShortcutInfo)( (Icon3D)cur_view_item ).getItemInfo();
		if( cur_view_item_sInfo.appInfo != null && cur_view_item_sInfo.appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
		{
			return ICON_DROP_OVER_NONE;
		}
		for( View3D mView3D : list )
		{
			ShortcutInfo view_sInfo = (ShortcutInfo)( (Icon3D)mView3D ).getItemInfo();
			if( cur_view_item.name.equals( mView3D.name ) && view_sInfo.intent.getAction().equals( cur_view_item_sInfo.intent.getAction() ) && view_sInfo.intent.getComponent().equals(
					cur_view_item_sInfo.intent.getComponent() ) )
			{
				return ICON_DROP_OVER_NONE;
			}
		}
		return ICON_DROP_OVER_ICON;
	}
	
	private void HandleItemInEditModeOperation(
			ArrayList<View3D> list ,
			int operation ,
			boolean is_folder_opened )
	{
		int lastAppPageIndex = appPageCount - 1;
		int curIndex = page_index;
		ViewGroup3D curGridView = (ViewGroup3D)view_list.get( curIndex );
		switch( operation )
		{
			case GRIDVIEW_EDITMODE_MOVE_ITEM_PRE:
			{
				// 把前一页的最后几项icon添加到当前页中；把当前的dragView添加到前一页
				int preIndex = page_index - 1;
				if( preIndex >= 0 )
				{
					ViewGroup3D preGridView = (ViewGroup3D)view_list.get( preIndex );
					int size = ( ( list.size() <= curGridView.getChildCount() ) ? list.size() : curGridView.getChildCount() );
					for( int index = 0 ; index < size ; index++ )
					{
						View3D lastViewInPregridView = preGridView.getChildAt( preGridView.getChildCount() - 1 );
						curGridView.addViewAt( 0 , lastViewInPregridView );
					}
					for( int index = 0 ; index < size ; index++ )
					{
						View3D dragView = list.get( index );
						View3D realViewInCurGridView = ( (GridView3D)curGridView ).getChildViewInGridView( dragView );
						if( realViewInCurGridView == null )
						{
							Log.v( "cooee" , "HandleItemInEditModeOperation --- GRIDVIEW_EDITMODE_MOVE_ITEM_PRE --  dragView: " + dragView.name + " -- not in current grdiView " );
							continue;
						}
						preGridView.addView( realViewInCurGridView );
					}
					if( size < list.size() )
					{
						for( int index = size ; index < list.size() ; index++ )
						{
							if( preGridView.getChildCount() - 1 < 0 )
							{
								Log.v( "cooee" , "HandleItemInEditModeOperation --- GRIDVIEW_EDITMODE_MOVE_ITEM_PRE --  error --preGridView.getChildCount():" + preGridView.getChildCount() );
								break;
							}
							View3D dragView = list.get( index );
							View3D realViewInCurGridView = ( (GridView3D)preGridView ).getChildViewInGridView( dragView );
							preGridView.moveViewTo( realViewInCurGridView , preGridView.getChildCount() - 1 );
						}
					}
				}
				break;
			}
			case GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT:
			{
				// 把后一页的第一项增加到当前页的最后；dragView 需要增加到后一页中
				int nextIndex = page_index + 1;
				if( nextIndex <= lastAppPageIndex )
				{
					ViewGroup3D nextGridView = (ViewGroup3D)view_list.get( nextIndex );
					int size = ( ( list.size() <= nextGridView.getChildCount() ) ? list.size() : nextGridView.getChildCount() );
					for( int index = 0 ; index < size ; index++ )
					{
						View3D lastViewInNextgridView = nextGridView.getChildAt( 0 );
						curGridView.addView( lastViewInNextgridView );
					}
					for( int index = 0 ; index < size ; index++ )
					{
						View3D dragView2 = list.get( index );
						View3D realViewInCurGridView = ( (GridView3D)curGridView ).getChildViewInGridView( dragView2 );
						if( realViewInCurGridView == null )
						{
							Log.v( "cooee" , "HandleItemInEditModeOperation --- GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT --  dragView: " + dragView2.name + " -- not in current grdiView " );
							continue;
						}
						nextGridView.addView( realViewInCurGridView );
					}
					if( size < list.size() )
					{
						for( int index = size ; index < list.size() ; index++ )
						{
							View3D dragView = list.get( index );
							if( curGridView.getChildCount() - 1 - size < 0 )
							{
								Log.v(
										"cooee" ,
										"HandleItemInEditModeOperation --- GRIDVIEW_EDITMODE_MOVE_ITEM_NEXT --  error -- curGridView.getChildCount():" + curGridView.getChildCount() + " -- size:" + size );
								break;
							}
							View3D realViewInCurGridView = ( (GridView3D)curGridView ).getChildViewInGridView( dragView );
							curGridView.moveViewTo( realViewInCurGridView , curGridView.getChildCount() - 1 - size );
						}
					}
				}
				break;
			}
		}
	}
	
	private void AddFolderAndRemoveAppInApps(
			ItemInfo folderInfo ,
			ApplicationInfo quietAppInfo ,
			ArrayList<ApplicationInfo> movingList_AppInfo ,
			int insert_idx )
	{
		ArrayList<ApplicationInfo> remove_appList = new ArrayList<ApplicationInfo>();
		remove_appList.add( quietAppInfo );
		remove_appList.addAll( movingList_AppInfo );
		mItemInfos.add( insert_idx , folderInfo );
		mItemInfos.removeAll( remove_appList );
		for( ApplicationInfo movingAppInfo : movingList_AppInfo )
		{
			// 因为移除item之后，导致各个位置index需要前移，更新数据库
			updatemItemsLoactionAfterIndex( movingAppInfo.location_in_mainmenu );
		}
		ArrayList<ItemInfo> updatingLocation_items = new ArrayList<ItemInfo>();
		for( int index = 0 ; index < mItemInfos.size() ; index++ )
		{
			ItemInfo itemInfo = mItemInfos.get( index );
			if( itemInfo instanceof ApplicationInfo )
			{
				ApplicationInfo mApplicationInfo = (ApplicationInfo)itemInfo;
				ShortcutInfo updating_item = new ShortcutInfo();
				updating_item.id = mApplicationInfo.id;
				updating_item.location_in_mainmenu = mApplicationInfo.location_in_mainmenu;
				updatingLocation_items.add( updating_item );
			}
			else if( itemInfo instanceof UserFolderInfo )
			{
				UserFolderInfo mFolderInfo = (UserFolderInfo)itemInfo;
				UserFolderInfo updating_item = new UserFolderInfo();
				updating_item.id = mFolderInfo.id;
				updating_item.location_in_mainmenu = mFolderInfo.location_in_mainmenu;
				updatingLocation_items.add( updating_item );
			}
		}
		AppListDB.getInstance().BatchItemsUpdateLocation( updatingLocation_items );
		ArrayList<ItemInfo> updating_items = new ArrayList<ItemInfo>();
		if( quietAppInfo.location_in_mainmenu != -1 )
		{
			quietAppInfo.location_in_mainmenu = -1;
			ShortcutInfo updating_item_sInfo = new ShortcutInfo();
			updating_item_sInfo.id = quietAppInfo.id;
			updating_item_sInfo.location_in_mainmenu = quietAppInfo.location_in_mainmenu;
			updating_items.add( updating_item_sInfo );
		}
		for( ApplicationInfo movingAppInfo : movingList_AppInfo )
		{
			if( movingAppInfo.location_in_mainmenu != -1 )
			{
				movingAppInfo.location_in_mainmenu = -1;
				ShortcutInfo updating_item_sInfo = new ShortcutInfo();
				updating_item_sInfo.id = movingAppInfo.id;
				updating_item_sInfo.location_in_mainmenu = movingAppInfo.location_in_mainmenu;
				updating_items.add( updating_item_sInfo );
			}
		}
		AppListDB.getInstance().BatchItemsUpdateLocation( updating_items );
		sortApp( sortId , false );
		syncAppsPages();
		startAnimation();
	}
	
	private void dealDragListFromDifferentPages(
			ArrayList<View3D> gragList ,
			int curDragIconIndex )
	{
		GridView3D curPageGridView3D = (GridView3D)view_list.get( page_index );
		boolean listFromDifferentPages = false;
		for( View3D mView3D : gragList )
		{
			View3D mRealView3D = curPageGridView3D.getChildViewInGridView( mView3D );
			if( mRealView3D == null )
			{
				listFromDifferentPages = true;
				break;
			}
		}
		if( listFromDifferentPages == false )
		{
			return;
		}
		int gragListSize = gragList.size();
		ArrayList<ViewGroup3D> pageContainsGragListIconList = new ArrayList<ViewGroup3D>();
		int[] pageContainsGragListIconListSize = new int[appPageCount];
		for( int index = 0 ; index < appPageCount ; index++ )
		{
			ViewGroup3D pageContainsGragListIcon = new ViewGroup3D( "" + index );
			GridView3D applistPage = (GridView3D)view_list.get( index );
			for( View3D mView3D : gragList )
			{
				View3D mRealView3D = applistPage.getChildViewInGridView( mView3D );
				if( mRealView3D != null )
				{
					pageContainsGragListIcon.addView( mRealView3D );
				}
			}
			pageContainsGragListIconList.add( pageContainsGragListIcon );
			pageContainsGragListIconListSize[pageContainsGragListIconList.size() - 1] = pageContainsGragListIcon.getChildCount();
		}
		if( page_index == 0 )
		{
			int curPageCount = mCellCountX * mCellCountY - 1;
			int insertBeforeLastIndex = -1;
			int leastNeedIndex = curPageCount - gragListSize;
			if( curDragIconIndex < leastNeedIndex )
			{
				insertBeforeLastIndex = curPageCount - curDragIconIndex;
			}
			else
			{
				insertBeforeLastIndex = gragListSize;
			}
			if( pageContainsGragListIconListSize[page_index] > 0 )
			{
				insertBeforeLastIndex -= pageContainsGragListIconListSize[page_index];
				if( curDragIconIndex < leastNeedIndex )
				{
					insertBeforeLastIndex++;
				}
			}
			for( int index = 0 ; index < appPageCount ; index++ )
			{
				ViewGroup3D pageContainsGragListIcon = pageContainsGragListIconList.get( index );
				if( pageContainsGragListIcon.getChildCount() > 0 )
				{
					int len = pageContainsGragListIcon.getChildCount();
					for( int i = 0 ; i < len ; i++ )
					{
						View3D mAppIcon3D = null;
						if( index == page_index )
						{
							mAppIcon3D = pageContainsGragListIcon.getChildAt( i );
							curPageGridView3D.removeView( mAppIcon3D );
						}
						else
						{
							mAppIcon3D = pageContainsGragListIcon.getChildAt( 0 );
						}
						int position = curPageGridView3D.getChildCount() - insertBeforeLastIndex;
						if( curPageGridView3D.getChildCount() == 0 )
						{
							position = 0;
						}
						if( ( position < 0 ) || ( position > curPageGridView3D.getChildCount() ) )
						{
							position = curPageGridView3D.getChildCount();
						}
						curPageGridView3D.addViewAt( position , mAppIcon3D );
					}
				}
			}
			for( int index = page_index + 1 ; index < appPageCount ; index++ )
			{
				GridView3D curGridView3D = (GridView3D)view_list.get( index );
				GridView3D preGridView3D = (GridView3D)view_list.get( index - 1 );
				int pushIconCount = gragListSize;
				for( int i = page_index ; i < index ; i++ )
				{
					pushIconCount -= pageContainsGragListIconListSize[i];
				}
				for( int i = 0 ; i < pushIconCount ; i++ )
				{
					View3D pushIcon = preGridView3D.getChildAt( preGridView3D.getChildCount() - 1 );
					curGridView3D.addViewAt( 0 , pushIcon );
				}
			}
		}
		else if( page_index == appPageCount - 1 )
		{
			int insertIndex = -1;
			int leastNeedIndex = gragListSize;
			if( curDragIconIndex < leastNeedIndex )
			{
				insertIndex = leastNeedIndex - 1;
			}
			else
			{
				insertIndex = curDragIconIndex;
			}
			if( insertIndex > curPageGridView3D.getChildCount() )
			{
				insertIndex = curPageGridView3D.getChildCount();
			}
			for( int index = appPageCount - 1 ; index >= 0 ; index-- )
			{
				ViewGroup3D pageContainsGragListIcon = pageContainsGragListIconList.get( index );
				if( pageContainsGragListIcon.getChildCount() > 0 )
				{
					int len = pageContainsGragListIcon.getChildCount();
					for( int i = 0 ; i < len ; i++ )
					{
						View3D mAppIcon3D = null;
						if( index == page_index )
						{
							mAppIcon3D = pageContainsGragListIcon.getChildAt( i );
							curPageGridView3D.removeView( mAppIcon3D );
						}
						else
						{
							mAppIcon3D = pageContainsGragListIcon.getChildAt( 0 );
						}
						curPageGridView3D.addViewAt( insertIndex , mAppIcon3D );
					}
				}
			}
			for( int index = appPageCount - 2 ; index >= 0 ; index-- )
			{
				GridView3D curGridView3D = (GridView3D)view_list.get( index );
				GridView3D nextGridView3D = (GridView3D)view_list.get( index + 1 );
				int pushIconCount = gragListSize;
				for( int i = appPageCount - 1 ; i > index ; i-- )
				{
					pushIconCount -= pageContainsGragListIconListSize[i];
				}
				for( int i = 0 ; i < pushIconCount ; i++ )
				{
					View3D pushIcon = nextGridView3D.getChildAt( 0 );
					curGridView3D.addView( pushIcon );
				}
			}
		}
		else
		{
			int curPageCount = curPageGridView3D.getChildCount();
			int insertIndex = -1;
			int leastNeedIndex = 0;
			int maxIndex = curPageCount;
			for( int i = 0 ; i < page_index ; i++ )
			{
				leastNeedIndex += pageContainsGragListIconListSize[i];
			}
			for( int i = page_index + 1 ; i < appPageCount ; i++ )
			{
				maxIndex -= pageContainsGragListIconListSize[i];
			}
			if( curDragIconIndex < leastNeedIndex )
			{
				insertIndex = leastNeedIndex;
			}
			else if( curDragIconIndex > maxIndex )
			{
				insertIndex = maxIndex;
			}
			else
			{
				insertIndex = curDragIconIndex;
			}
			int position = insertIndex;
			if( curPageGridView3D.getChildCount() == 0 )
			{
				position = 0;
			}
			if( position > curPageGridView3D.getChildCount() )
			{
				position = curPageGridView3D.getChildCount();
			}
			{
				ViewGroup3D pageContainsGragListIcon = pageContainsGragListIconList.get( page_index );
				if( pageContainsGragListIcon.getChildCount() > 0 )
				{
					int len = pageContainsGragListIcon.getChildCount();
					for( int i = 0 ; i < len ; i++ )
					{
						View3D mAppIcon3D = pageContainsGragListIcon.getChildAt( i );
						curPageGridView3D.removeView( mAppIcon3D );
						curPageGridView3D.addViewAt( position , mAppIcon3D );
					}
				}
			}
			for( int index = 0 ; index < appPageCount ; index++ )
			{
				if( index == page_index )
				{
					continue;
				}
				ViewGroup3D pageContainsGragListIcon = pageContainsGragListIconList.get( index );
				if( pageContainsGragListIcon.getChildCount() > 0 )
				{
					int len = pageContainsGragListIcon.getChildCount();
					for( int i = 0 ; i < len ; i++ )
					{
						View3D mAppIcon3D = pageContainsGragListIcon.getChildAt( 0 );
						curPageGridView3D.addViewAt( position , mAppIcon3D );
					}
				}
			}
			for( int index = page_index - 1 ; index >= 0 ; index-- )
			{
				GridView3D curGridView3D = (GridView3D)view_list.get( index );
				GridView3D nextGridView3D = (GridView3D)view_list.get( index + 1 );
				int pushIconCount = gragListSize;
				for( int i = appPageCount - 1 ; i > index ; i-- )
				{
					pushIconCount -= pageContainsGragListIconListSize[i];
				}
				for( int i = 0 ; i < pushIconCount ; i++ )
				{
					View3D pushIcon = nextGridView3D.getChildAt( 0 );
					curGridView3D.addView( pushIcon );
				}
			}
			for( int index = page_index + 1 ; index < appPageCount ; index++ )
			{
				GridView3D curGridView3D = (GridView3D)view_list.get( index );
				GridView3D preGridView3D = (GridView3D)view_list.get( index - 1 );
				int pushIconCount = gragListSize;
				for( int i = 0 ; i < index ; i++ )
				{
					pushIconCount -= pageContainsGragListIconListSize[i];
				}
				for( int i = 0 ; i < pushIconCount ; i++ )
				{
					View3D pushIcon = preGridView3D.getChildAt( preGridView3D.getChildCount() - 1 );
					curGridView3D.addViewAt( 0 , pushIcon );
				}
			}
		}
	}
	
	// xiatian add end
	// xiatian add start //for miui generate folder animation
	private void FolderLargeAnim(
			float duration ,
			View3D view3D )
	{
		if( view3D == null || duration == 0 )
		{
			return;
		}
		if( FolderLargeTween != null )
		{
			return;
		}
		view3D.stopTween();
		view3D.setScale( 0 , 0 );
		view3D.setVisible( true );
		FolderLargeTween = view3D.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1.2f , 1.2f , 0 ).setUserData( view3D ).setCallback( this );
	}
	
	// xiatian add end
	public void release2DWidget()
	{
		if( !Desktop3DListener.bAppDone )
		{
			return;
		}
		allmemnum = 0;
		Iterator<View3D> ite = view_list.iterator();
		ArrayList<View3D> listToRemove = new ArrayList<View3D>();
		while( ite.hasNext() )
		{
			GridView3D grid = (GridView3D)ite.next();
			if( grid.name.equals( WIDGETGRID ) )
			{
				listToRemove.add( grid );
			}
		}
		if( listToRemove.size() > 0 )
		{
			for( View3D v : listToRemove )
			{
				GridView3D grid = (GridView3D)v;
				view_list.remove( grid );
				grid.releaseRegion();
				widgetGridPool.free( grid );
				AppList3D.this.removeView( grid );
			}
		}
		if( iLoongLauncher.getInstance().d3dListener != null )
		{
			iLoongLauncher.getInstance().d3dListener.onRelaeseWidget2D();
		}
		if( mWidgets != null )
			mWidgets.clear();
		if( widget2DMap != null )
		{
			widget2DMap.clear();
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( Desktop3DListener.root != null && Desktop3DListener.root.getWidgetHost() != null && Desktop3DListener.root.getWidgetHost().widgetList != null )
			{
				Desktop3DListener.root.getWidgetHost().widgetList.onPause();
			}
		}
		AppList3D.hasbind2Dwidget = false;
	}
	
	public void bind2DWidget()
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				if( !Desktop3DListener.bAppDone )
				{
					return;
				}
				Log.v( "" , "appwidgetmey applist bind2DWidget() 绑定小组件" );
				ArrayList<Object> mAllWidgets = getAll2DWidgetshortcuts();
				Log.v( "" , "appwidgetmey mAllWidgets size is " + mAllWidgets.size() );
				if( mAllWidgets != null && iLoongLauncher.getInstance().d3dListener != null )
				{
					iLoongLauncher.getInstance().d3dListener.bindAllWidgets( mAllWidgets );
				}
				AppList3D.hasbind2Dwidget = true;
			}
		} );
	}
	
	private ArrayList<Object> getAll2DWidgetshortcuts()
	{
		// TODO Auto-generated method stub
		ArrayList<Object> mAllWidgets = new ArrayList<Object>();
		final PackageManager packageManager = iLoongApplication.getInstance().getPackageManager();
		List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance( iLoongApplication.getInstance() ).getInstalledProviders();
		Intent shortcutsIntent = new Intent( Intent.ACTION_CREATE_SHORTCUT );
		List<ResolveInfo> shortcuts = packageManager.queryIntentActivities( shortcutsIntent , 0 );
		for( AppWidgetProviderInfo widget : widgets )
		{
			if( widget.minWidth > 0 && widget.minHeight > 0 )
			{
				mAllWidgets.add( widget );
			}
			else
			{
				Log.e( "" , "Widget " + widget.provider + " has invalid dimensions (" + widget.minWidth + ", " + widget.minHeight + ")" );
			}
		}
		mAllWidgets.addAll( shortcuts );
		Collections.sort( mAllWidgets , new LauncherModel.WidgetAndShortcutNameComparator( packageManager ) );
		return mAllWidgets;
	}
	
	public boolean iswidgetVisible()
	{
		// TODO Auto-generated method stub
		if( this.visible && this.getCurrentPage() >= appPageCount )
		{
			Log.v( "" , "widget可见" );
			return true;
		}
		return false;
	}
	
	private int getAllItemsNum()
	{
		int num = mItemInfos.size();
		for( ItemInfo info : mItemInfos )
		{
			if( info instanceof UserFolderInfo )
			{
				num += ( (UserFolderInfo)info ).contents.size();
			}
		}
		return num;
	}
	
	public void newFolderInApplist(
			ArrayList<View3D> selectIcons )
	{
		if( selectIcons.size() <= 0 )
		{
			return;
		}
		ArrayList<View3D> temList = new ArrayList<View3D>();
		for( View3D v : selectIcons )
		{
			Icon3D ic = (Icon3D)v;
			View3D shortcut = Workspace3D.createShortcut( (ShortcutInfo)ic.getItemInfo() , true );
			temList.add( shortcut );
		}
		selectIcons.clear();
		selectIcons.addAll( temList );
		UserFolderInfo folderInfo = new UserFolderInfo();
		ApplicationListHost.folderName = ApplicationListHost.folderName.trim();
		ApplicationListHost.folderName = ApplicationListHost.folderName.concat( "x.z" );
		if( ApplicationListHost.folderName.length() > 3 )
		{
			folderInfo.setTitle( ApplicationListHost.folderName );
		}
		else
		{
			folderInfo.setTitle( R3D.folder3D_name );
		}
		ArrayList<ApplicationInfo> removedAppList = new ArrayList<ApplicationInfo>();
		ApplicationInfo info = null;
		for( int i = 0 ; i < selectIcons.size() ; i++ )
		{
			info = ( (ShortcutInfo)( (Icon3D)selectIcons.get( i ) ).getItemInfo() ).appInfo;
			removedAppList.add( info );
			if( !DefaultLayout.mainmenu_sort_by_user_fun )
			{
				info.angle = mItemInfos.indexOf( info );
			}
		}
		info = ( (ShortcutInfo)( (Icon3D)selectIcons.get( 0 ) ).getItemInfo() ).appInfo;
		folderInfo.use_frequency = System.currentTimeMillis();//info.getUseFrequency();
		folderInfo.lastUpdateTime = -getAllItemsNum();
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			folderInfo.location_in_mainmenu = 0;
		}
		AppListDB.getInstance().addOrMoveItem( folderInfo , LauncherSettings.Favorites.CONTAINER_APPLIST );
		iLoongLauncher.getInstance().addFolderInfoToSFolders( folderInfo );
		mItemInfos.add( folderInfo );
		mItemInfos.removeAll( removedAppList );
		newFolderInfo = folderInfo;
		onDropList = selectIcons;
		if( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			ArrayList<ItemInfo> updating_items = new ArrayList<ItemInfo>();
			for( ApplicationInfo movingAppInfo : removedAppList )
			{
				if( movingAppInfo.location_in_mainmenu != -1 )
				{
					movingAppInfo.location_in_mainmenu = -1;
					ShortcutInfo updating_item_sInfo = new ShortcutInfo();
					updating_item_sInfo.id = movingAppInfo.id;
					updating_item_sInfo.location_in_mainmenu = movingAppInfo.location_in_mainmenu;
					updating_items.add( updating_item_sInfo );
				}
			}
			AppListDB.getInstance().BatchItemsUpdateLocation( updating_items );
		}
		sortApp( sortId , false );
		syncAppsPages();
		if( getCurrentPage() == 0 )
		{
			startAnimation();
		}
		else
		{
			scrollTo( 0 );
		}
		// 把当前的Folder增加入 Draglayer
		String folderMap_key = ICON_MAP_FOLDER_LABEL + folderInfo.id;
		this.drageTarget_new_child = folderInfoMap.get( folderMap_key );
		viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
	}
	
	@Override
	public void startAutoEffect()
	{
		float duration = DefaultLayout.page_tween_time;
		float density = Gdx.graphics.getDensity();
		if( Math.abs( mVelocityX ) >= DefaultLayout.MIN_FLING_VELOCITY * density )
		{
			duration = getFlingDuration();
			Log.i( "" , "############ flingDuration = " + duration );
		}
		TweenEquation easeEquation = Quint.OUT;
		if( DefaultLayout.external_applist_page_effect == true )
		{
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_ELASTICITY )
			{
				//弹性特效 才使用如下的动画方式
				APageEase.setTouchUpAnimEffectStatus( true );
				if( xScale > 0 )
				{
					APageEase.saveDegreeInfoWhnTouchUp( xScale - 1 );
				}
				else
				{
					APageEase.saveDegreeInfoWhnTouchUp( xScale );
				}
				easeEquation = Bounce.OUT;
				duration = duration + 0.2f;
			}
		}
		isManualScrollTo = false;
		boolean isAutoScrollBack = false;
		if( DefaultLayout.drawer_npages_circle_scroll_config && this.appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT )
		{
			if( ( page_index == ( appPageCount - 1 ) && APageEase.is_scroll_from_right_to_left == true ) || ( page_index == 0 && APageEase.is_scroll_from_right_to_left == false ) )
			{
				isAutoScrollBack = true;
			}
			else
			{
				isAutoScrollBack = false;
			}
		}
		else if( DefaultLayout.drawer_npages_circle_scroll_config && this.appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET )
		{
			if( ( page_index == ( appPageCount + widgetPageCount - 1 ) && APageEase.is_scroll_from_right_to_left == true ) || ( page_index == appPageCount && APageEase.is_scroll_from_right_to_left == false ) )
			{
				isAutoScrollBack = true;
			}
			else
			{
				isAutoScrollBack = false;
			}
		}
		//teapotXu add end
		if( xScale == 0 && mVelocityX == 0 )
		{
			return;
		}
		float scroll_sensitive = DefaultLayout.npagbse_scroll_nextpage_sensitive;
		float scroll_velocity_coefficient = DefaultLayout.SCROLL_VELOCITY_COEFFICIENT * density;
		Log.i( "" , "################# xScale + mVelocityX / s = " + ( xScale + mVelocityX / scroll_velocity_coefficient ) );
		if( xScale + mVelocityX / scroll_velocity_coefficient > scroll_sensitive && !isAutoScrollBack )
		{
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( 1 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 1 , 0 ).setCallback( this );
			}
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle && !DefaultLayout.enable_doov_spec_customization )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT , 0 , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		else if( ( xScale + mVelocityX / scroll_velocity_coefficient < ( -1 ) * scroll_sensitive ) && !( isAutoScrollBack ) )
		{
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( -1 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( -1 , 0 ).setCallback( this );
			}
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle && !DefaultLayout.enable_doov_spec_customization )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT , Gdx.graphics.getWidth() , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		else
		{
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( 0 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 0 , 0 ).setCallback( this );
			}
		}
		mVelocityX = 0;
		refreshUninstall( true );
		tween.start( View3DTweenAccessor.manager ).setUserData( START_EFFECT );
		if( crystalGroup != null )
			crystalGroup.stop( duration );
	}
	
	public void afainApp(
			int hang ,
			int lie )
	{
		if( !( mCellCountX == hang && mCellCountY == lie ) )
		{
			mCellCountX = hang;
			mCellCountY = lie;
			checkItemInfos();
			Iterator<View3D> ite = view_list.iterator();
			ArrayList<View3D> listToRemove = new ArrayList<View3D>();
			while( ite.hasNext() )
			{
				GridView3D grid = (GridView3D)ite.next();
				if( grid.name.equals( "AppGrid" ) )
				{
					listToRemove.add( grid );
				}
			}
			if( listToRemove.size() > 0 )
			{
				for( View3D v : listToRemove )
				{
					GridView3D grid = (GridView3D)v;
					view_list.remove( grid );
					appGridPool.free( grid );
					this.removeView( grid );
				}
			}
			syncAppPageCount();
			for( int i = 0 ; i < appPageCount ; i++ )
			{
				GridView3D grid = appGridPool.create( mCellCountX , mCellCountY );
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
	}
	
	public int[] getUseFrequency()
	{
		ArrayList<ApplicationInfo> aInfos = new ArrayList<ApplicationInfo>();
		for( int i = 0 ; i < mItemInfos.size() ; i++ )
		{
			ItemInfo info = mItemInfos.get( i );
			if( info instanceof FolderInfo )
			{
				ArrayList<ShortcutInfo> contents = ( (UserFolderInfo)info ).contents;
				if( contents != null && contents.size() > 0 )
				{
					for( int j = 0 ; j < contents.size() ; j++ )
					{
						if( !contents.get( j ).appInfo.isHideIcon )
						{
							aInfos.add( contents.get( j ).appInfo );
						}
					}
				}
			}
			else if( info instanceof ApplicationInfo )
			{
				if( !( (ApplicationInfo)info ).isHideIcon )
				{
					aInfos.add( (ApplicationInfo)info );
				}
			}
		}
		int[] useKey = new int[aInfos.size()];
		int[] sortArray = new int[aInfos.size()];
		for( int i = 0 ; i < aInfos.size() ; i++ )
		{
			useKey[i] = (int)( aInfos.get( i ).getUseFrequency() );
		}
		cut.sort( 0 , useKey , sortArray );
		return sortArray;
	}
}
