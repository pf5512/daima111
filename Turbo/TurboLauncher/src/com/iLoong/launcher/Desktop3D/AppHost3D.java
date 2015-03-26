package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cooee.android.launcher.framework.IconCache;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D.TabTitleFactory;
import com.iLoong.launcher.Folder3D.Folder3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Folder3D.FolderMIUI3D;
import com.iLoong.launcher.Functions.Tab.ITabTitlePlugin;
import com.iLoong.launcher.Functions.Tab.Plugin;
import com.iLoong.launcher.Functions.Tab.TabContext;
import com.iLoong.launcher.Functions.Tab.TabPluginManager;
import com.iLoong.launcher.Functions.Tab.TabPluginManager.PluginDataObserver;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class AppHost3D extends ViewGroup3D implements AppBar3D.OnTabChangeListener , DropTarget3D , PluginDataObserver
{
	
	public static final int CONTENT_TYPE_AUDIO_ALBUM = 0;
	public static final int CONTENT_TYPE_PHOTO_BUCKET = 1;
	public static final int CONTENT_TYPE_VIDEO = 2;
	public static final int CONTENT_TYPE_APP = 3;
	public static final int CONTENT_TYPE_PHOTO = 4;
	public static final int CONTENT_TYPE_AUDIO = 5;
	public static final int CONTENT_TYPE_WIDGET = 6;
	public static int currentContentType = CONTENT_TYPE_APP;
	private int photoBucketPageIndex = 0;
	private int audioAlbumPageIndex = 0;
	private int videoPageIndex = 0;
	public static AppBar3D appBar;
	public static AppList3D appList;
	public static AppPopMenu2 popMenu2;
	public static boolean selectState = false;
	public int applistIndex = 0;
	public static int mainmenuBgAlpha = 100;//xiatian add	//mainmenu_background_alpha_progress
	public static int old_mainmenuBgAlpha = 100;
	public TabPluginHost3D tabPluginHost = null;
	ArrayList<Plugin> plugins = null;
	TabPluginManager tabPluginManager = null;
	//xiatian add start	//for mainmenu sort by user
	public static boolean onHomeKey = false;
	public static App2Workspace3D app2workspace;
	public static boolean mMoveDrag2Workspace = false;
	
	//xiatian add end
	public AppHost3D(
			String name )
	{
		super( name );
		if( DefaultLayout.setupmenu_by_system || RR.net_version )
		{
			iLoongLauncher.getInstance().initAppMenu();
		}
		// xiatian add start //mainmenu_background_alpha_progress
		mainmenuBgAlpha = iLoongLauncher.getInstance().prefs.getInt( "mainmenu_bg_alpha" , DefaultLayout.mainmenu_background_default_alpha );
		// xiatian add end
		appList = new AppList3D( "applist" );
		this.addView( appList );
		if( DefaultLayout.enable_tab_plugin )
		{
			TabContext tc = new TabContext();
			tc.mContainerContext = iLoongLauncher.getInstance();
			tc.mGdxApplication = iLoongLauncher.getInstance();
			tc.addParam( "width" , Utils3D.getScreenWidth() );
			tc.addParam( "height" , Utils3D.getScreenHeight() - R3D.appbar_height );
			tabPluginManager = new TabPluginManager( tc );
			ITabTitlePlugin titleFactory = new TabTitleFactory();
			tabPluginManager.setmTabTitlePlugin( titleFactory );
			tabPluginManager.addTabPluginDataObserver( this );
			tabPluginManager.build();
			tabPluginHost = new TabPluginHost3D( "tabHost" );
			tabPluginHost.appHost = this;
			this.addView( tabPluginHost );
		}
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
		{
			popMenu2 = new AppPopMenu2( "apppopmenu2" , null );// zjp new
																// AppPopMenu2("apppopmenu2")
			popMenu2.setAppList( appList );
			popMenu2.hideNoAnim();
			if( !DefaultLayout.applist_style_classic && !DefaultLayout.hide_appbar )
			{
				appBar = new AppBar3D( "appbar" );
				appBar.tabPluginManager = tabPluginManager;
				this.addView( appBar );
				appBar.setOnTabChangeListener( this );
				appList.setAppBar( appBar );
				appList.addScrollListener( appBar );
				appBar.setAppList( appList );
				appBar.setAppPopMenu2( popMenu2 );
				appBar.setAppHost( this );// zqh;
				if( DefaultLayout.enable_tab_plugin )
				{
					appBar.buildPluginTab();
					this.addView( appBar.tabPopMenu );
				}
			}
			this.addView( popMenu2 );
			//xiatian add start	//for mainmenu sort by user
			if( DefaultLayout.mainmenu_sort_by_user_fun )
			{
				app2workspace = new App2Workspace3D( "app2workspace" );
				app2workspace.setAppHost( this );
				appList.setApp2Workspace( app2workspace );
				this.addView( app2workspace );
				//				app2workspace.hide();
			}
			//xiatian add end
		}
		else
		{
			//Jone start .
			if( RR.net_version )
			{
				popMenu2 = new AppPopMenuSideBar( "popMenu2" );//AppPopMenu2Square( "apppopmenu2" );
				popMenu2.setAppList( appList );
				popMenu2.hideNoAnim();
			}
			else
			//Jone end
			if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ORIGINAL )
			{
				popMenu2 = new AppPopMenu2( "apppopmenu2" , null );// zjp new
																	// AppPopMenu2("apppopmenu2")
				popMenu2.setAppList( appList );
				popMenu2.hideNoAnim();
			}
			else if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_SQUARE )
			{// zjp
				popMenu2 = new AppPopMenu2Square( "apppopmenu2" );
				popMenu2.setAppList( appList );
				popMenu2.hideNoAnim();
			}
			if( !DefaultLayout.applist_style_classic && !DefaultLayout.hide_appbar )
			{
				appBar = new AppBar3D( "appbar" );
				this.addView( appBar );
				appBar.tabPluginManager = tabPluginManager;
				appBar.setOnTabChangeListener( this );
				appList.setAppBar( appBar );
				appList.addScrollListener( appBar );
				appBar.setAppList( appList );
				appBar.setAppHost( this );// zqh
				if( DefaultLayout.enable_tab_plugin )
				{
					appBar.buildPluginTab();
					this.addView( appBar.tabPopMenu );
				}
			}
			if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ORIGINAL || DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_SQUARE )
			{
				this.addView( popMenu2 );
			}
			//xiatian add start	//for mainmenu sort by user
			if( DefaultLayout.mainmenu_sort_by_user_fun )
			{
				app2workspace = new App2Workspace3D( "app2workspace" );
				app2workspace.setAppHost( this );
				appList.setApp2Workspace( app2workspace );
				this.addView( app2workspace );
				//				app2workspace.hide();
			}
			//xiatian add end
		}
		selectState = false;
		currentContentType = CONTENT_TYPE_APP;
		if( appList != null )
			applistIndex = appList.getIndexInParent();
	}
	
	public void onThemeChanged()
	{
		appList.onThemeChanged();
		if( !DefaultLayout.applist_style_classic && !DefaultLayout.hide_appbar )
		{
			appBar.onThemeChanged();
		}
	}
	
	public void show()
	{
		super.show();
		if( appBar != null && ( appBar.tabIndicator.tabId == AppBar3D.TAB_CONTENT || appBar.tabIndicator.tabId == AppBar3D.TAB_WIDGET ) )
		{
			appList.show();
			if( tabPluginHost != null )
			{
				tabPluginHost.hide();
			}
		}
		else if( appBar != null && ( appBar.tabIndicator.tabId == AppBar3D.TAB_PLUGIN || appBar.tabIndicator.tabId == AppBar3D.TAB_MORE ) )
		{
			appList.hide();
		}
		else if( appBar == null )
		{
			appList.show();
		}
		if( appBar != null )
			appBar.show();
	}
	
	public void hide()
	{
		super.hide();
		if( appBar != null && appBar.tabPopMenu != null )
		{
			appBar.tabPopMenu.hide();
		}
		appList.hide();
		if( appBar != null )
		{
			if( appBar.popMenu != null )
			{
				if( appBar.popMenu.isVisible() )
				{
					appBar.popMenu.hide();
				}
			}
		}
	}
	
	public void changeContentType()
	{
	}
	
	@Override
	public void onTabChange(
			int tabId )
	{
		if( tabId == AppBar3D.TAB_CONTENT )
		{
			if( this.visible )
			{
				if( DefaultLayout.enable_tab_plugin )
				{
					tabPluginHost.onLeave();
				}
			}
			else
			{
				if( DefaultLayout.enable_tab_plugin )
				{
					return;
				}
			}
			if( AppHost3D.currentContentType == AppHost3D.CONTENT_TYPE_APP )
			{
				appList.setCurrentPage( 0 );
				appList.show();
			}
			else
			{
				appList.justHide();
				appList.setCurrentPage( 0 );
			}
			if( currentContentType == CONTENT_TYPE_PHOTO || currentContentType == CONTENT_TYPE_AUDIO )
			{
			}
		}
		else if( tabId == AppBar3D.TAB_WIDGET )
		{
			if( this.visible )
			{
				if( DefaultLayout.enable_tab_plugin )
				{
					tabPluginHost.onLeave();
				}
			}
			appList.show();
			if( DefaultLayout.enable_release_2Dwidget && !AppList3D.hasbind2Dwidget && iLoongApplication.BuiltIn )
			{
				appList.isAppbarWidget = true;
				appList.bind2DWidget();
			}
			if( appList.appPageCount < appList.view_list.size() )
				appList.setCurrentPage( appList.appPageCount );
		}
		else if( tabId == AppBar3D.TAB_PLUGIN || tabId == AppBar3D.TAB_MORE )
		{
			if( this.visible )
			{
				appList.hide();
				tabPluginHost.show();
				plugins = TabPluginManager.getInstance().getPluginList();
				for( int i = 0 ; i < plugins.size() ; i++ )
				{
					Plugin plugin = plugins.get( i );
					if( plugin.mSelected )
					{
						tabPluginHost.onTabChange( plugin );
						break;
					}
				}
			}
		}
	}
	
	public void clearDragObjs()
	{
		appList.clearDragObjs();
	}
	
	public void setIconCache(
			IconCache iconCache )
	{
		appList.setIconCache( iconCache );
	}
	
	public void addApps(
			ArrayList<ApplicationInfo> apps )
	{
		for( int i = 0 ; i < apps.size() ; i++ )
		{
			ApplicationInfo info = apps.get( i );
			for( int j = 0 ; j < DefaultLayout.hideAppList.size() ; j++ )
			{
				String compName = DefaultLayout.hideAppList.get( j );
				ComponentName componentName = new ComponentName( info.packageName , info.componentName.getClassName() );
				if( componentName.toString().contains( compName ) )
				{
					apps.remove( i );
					i--;
					break;
				}
			}
		}
		appList.addApps( apps );
	}
	
	public void addFolders(
			ArrayList<FolderInfo> folders )
	{
		appList.addFolders( folders );
	}
	
	public void addWidget(
			Widget3DShortcut widget )
	{
		appList.addWidget( widget );
	}
	
	public void setWidgets(
			ArrayList<WidgetShortcutInfo> widgets )
	{
		appList.setWidgets( widgets );
	}
	
	public void setApps(
			ArrayList<ApplicationInfo> apps )
	{
		appList.setApps( apps );
	}
	
	public void setFolders(
			ArrayList<FolderInfo> folders )
	{
		appList.setFolders( folders );
	}
	
	public synchronized void reomveApps(
			ArrayList<ApplicationInfo> apps ,
			boolean permanent )
	{
		appList.reomveApps( apps , permanent );
	}
	
	public void updateApps(
			ArrayList<ApplicationInfo> apps )
	{
		for( int i = 0 ; i < apps.size() ; i++ )
		{
			ApplicationInfo info = apps.get( i );
			for( int j = 0 ; j < DefaultLayout.hideAppList.size() ; j++ )
			{
				String compName = DefaultLayout.hideAppList.get( j );
				ComponentName componentName = new ComponentName( info.packageName , info.componentName.getClassName() );
				if( componentName.toString().contains( compName ) )
				{
					apps.remove( i );
					i--;
					break;
				}
			}
		}
		appList.updateApps( apps );
	}
	
	public void removeWidget(
			String packageName )
	{
		appList.removeWidget( packageName );
	}
	
	public int estimateWidgetCellWidth(
			int cellHSpan )
	{
		// TODO Auto-generated method stub
		return appList.estimateWidgetCellWidth( cellHSpan );
	}
	
	public int estimateWidgetCellHeight(
			int cellVSpan )
	{
		// TODO Auto-generated method stub
		return appList.estimateWidgetCellHeight( cellVSpan );
	}
	
	public NPageBase getContentList()
	{
		if( appList.visible )
			return appList;
		return null;
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK && appList.xScale == 0 )
		{
			if( popMenu2 != null && popMenu2.isVisible() && popMenu2.touchable )
			{
				popMenu2.hide();
				return true;
			}
			else if( appBar != null && appBar.popMenu != null && appBar.popMenu.isVisible() && appBar.popMenu.touchable )
			{
				appBar.popMenu.hide();
				return true;
			}
			else if( appBar != null && appBar.tabPopMenu != null && appBar.tabPopMenu.isVisible() && appBar.tabPopMenu.touchable )
			{
				appBar.tabPopMenu.hide();
				return true;
			}
		}
		else if( keycode == KeyEvent.KEYCODE_MENU && appList.xScale == 0 )
		{
			// xiatian add start //EffectPreview
			if( DefaultLayout.enable_effect_preview )
			{
				Root3D mRoot3D = iLoongLauncher.getInstance().getD3dListener().getRoot();
				if( mRoot3D.isApplistEffectPreviewMode() )
					return true;
			}
			// xiatian add end
			if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
			{
				if( popMenu2 != null && popMenu2.isVisible() )
				{
					popMenu2.hide();
					return true;
				}
				else if( appBar != null && appBar.popMenu != null && appBar.popMenu.isVisible() )
				{
					appBar.popMenu.hide();
					return true;
				}
				else if( appBar != null && appBar.tabPopMenu != null && appBar.tabPopMenu.isVisible() )
				{
					appBar.tabPopMenu.hide();
					return true;
				}
				else if( popMenu2 != null && !popMenu2.isVisible() && appList.canPopMenu() )
				{
					if( appBar != null && appBar.tabPopMenu != null )
					{
						appBar.tabPopMenu.hide();
					}
					if( !DefaultLayout.setupmenu_by_system )
						popMenu2.show();
					return true;
				}
			}
			else
			{
				// xiatian start //is_demo_version
				// xiatian del start
				// if (appBar != null && appBar.popMenu != null) {
				// if (appBar.popMenu.isVisible())
				// appBar.popMenu.hide();
				// else if (appList.canPopMenu())
				// appBar.popMenu.show();
				// return true;
				// }
				// xiatian del end
				// xiatian add start
				if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ORIGINAL || DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_SQUARE )
				{
					if( popMenu2 != null && popMenu2.isVisible() )
					{
						popMenu2.hide();
						return true;
					}
					else if( appBar != null && appBar.popMenu != null && appBar.popMenu.isVisible() )
					{
						appBar.popMenu.hide();
						return true;
					}
					else if( appBar != null && appBar.tabPopMenu != null && appBar.tabPopMenu.isVisible() )
					{
						appBar.tabPopMenu.hide();
						return true;
					}
					else if( popMenu2 != null && !popMenu2.isVisible() && appList.canPopMenu() )
					{
						if( appBar != null && appBar.tabPopMenu != null )
						{
							appBar.tabPopMenu.hide();
						}
						if( !DefaultLayout.setupmenu_by_system )
							popMenu2.show();
						return true;
					}
				}
				else
				{
					if( appBar != null && appBar.tabPopMenu != null )
					{
						appBar.tabPopMenu.hide();
					}
					if( appBar != null && appBar.popMenu != null )
					{
						if( appBar.popMenu.isVisible() )
							appBar.popMenu.hide();
						else if( appList.canPopMenu() )
							appBar.popMenu.show();
						return true;
					}
				}
				// xiatian add end
				// xiatian end
			}
		}
		return super.keyUp( keycode );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// teapotXu add start for merge MIUI_V5_folder style
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && ( folderOpened && appHost_folder != null ) )
		{
			if( DefaultLayout.blur_enable )
				MiuiV5FolderBoxBlurInAppHost( batch , parentAlpha );
			else
			{
				super.draw( batch , parentAlpha );
			}
		}
		else
		{
			super.draw( batch , parentAlpha );
		}
		// super.draw(batch, parentAlpha);
		// teapotXu add end
	}
	
	public void sortApp(
			int checkId )
	{
		if( appList.sortId != checkId )
			appList.sortApp( checkId , true );
	}
	
	public boolean isNeedReset()
	{
		if( appList != null && appList.sortId == AppList3D.SORT_USE )
		{
			return true;
		}
		return false;
	}
	
	public void reset()
	{
		if( appList != null )
			appList.reset();
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true && sender instanceof DragLayer3D )
		{
			switch( event_id )
			{
				case DragLayer3D.MSG_DRAG_INBORDER:
					appList.onCtrlEvent( sender , event_id );
					return true;
			}
		}
		else if( sender instanceof FolderIcon3D )
		{
			FolderIcon3D folderIcon3D = (FolderIcon3D)sender;
			switch( event_id )
			{
				case FolderIcon3D.MSG_FOLDERICON_FROME_APPLIST:
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder == true )
					{
						// 小米V5文件夹风格
						this.touchable = false;
					}
					else
					{
						appList.setAppListHideReason( AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN );
						appList.hide();
						if( appBar != null )
							appBar.hide();
					}
					// ?????FolderIcon3D
					// this.touchable = false;
					appHost_folder = folderIcon3D;
					appHost_dragLayer.removeDropTarget( folderIcon3D );
					folderOpened = true;
					appList.is_maimenufolder_open = true;
					appList.clearDragObjs();// add for optimize the icon3d's
											// double-click
					appList.mOpenFolderIcon = folderIcon3D;
					folderIcon3D.is_applist_folder_no_refresh = true;
					// ??addview???????????addView????folderIcon3D ??viewParent??
					addView( folderIcon3D );
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_APPLIST:
					// teapotXu add 20130808: only for coco, no need to wait, can
					// add folderIcon back to appHost directly
					// if(ThemeManager.getInstance().getBoolean("miui_v5_folder")||DefaultLayout.miui_v5_folder
					// == true)
					if( false )
					{
						Tween tween = this.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 1f , 1f , 0f ).setCallback( this ); // todo：这里有可能收不到回调！！！！！
						// removeView(folderIcon3D);
						appHost_tag = folderIcon3D;
						folderIcon3D.is_applist_folder_no_refresh = false;
						// teapotXu Add 类似Root3D:
						/*
						 * 避免这里的动画有时候收不到回调，启动定时器�?
						 * 定时器到时长度一定要大于这里启动动画的时长，保证动画的回调函数能跑到一次，
						 * 避免回调跑不到，root3d.touchable为False，触摸无法反应，定屏问题的解�? added by
						 * zfshi 2012-08-19
						 */
						startProtectedTimer( (long)( appHostFolderTweenDuration * 4 * 1000 ) );
						// appHost.setAppHostTag(folderIcon3D);
						// appHost.show();
					}
					else
					{
						// before appList show, add folder backto applist
						appHost_tag = folderIcon3D;
						folderIcon3D.is_applist_folder_no_refresh = false;
						dealFolderInAppListTweenFinish();
						appList.setAppListHideReason( AppList3D.APP_HIDE_REASON_FOR_NONE );
						//xiatian add start	///for mainmenu sort by user
						if( onHomeKey )
						{
							onHomeKey = false;
							return true;
						}
						//xiatian add end
						appList.show();
						if( appBar != null )
							appBar.show();
					}
					return true;
				case FolderIcon3D.MSG_FOLDERICON_TO_CELLLAYOUT:
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder == true )
					{
						// none
					}
					else
					{
						appList.show();
						if( appBar != null )
							appBar.show();
					}
					break;
				case FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE:
				{
					// In the folder of Mainmenu, this message do nothing now
					return true;
				}
				case FolderIcon3D.MSG_FOLDERICON_BACKTO_ORIG:
					ArrayList<View3D> child_list = (ArrayList<View3D>)folderIcon3D.getTag();
					//xiatian start	//for mainmenu sort by user
					// appHost_folder_DropList_backtoOrig( child_list );//xiatian del
					//xiatian add start
					if( DefaultLayout.mainmenu_folder_function )
					{
						ArrayList<View3D> dragViewList = iLoongLauncher.getInstance().getD3dListener().getDragLayer().getDragList();
						dragViewList.removeAll( child_list );
						removeDragViews( dragViewList , false );
						dragViewList.addAll( child_list );
					}
					else
					{
						appHost_folder_DropList_backtoOrig( child_list );
					}
					//xiatian add end
					//xiatian end
					return true;
			}
		}
		//xiatian add start	//for mainmenu sort by user
		else if( sender instanceof FolderMIUI3D )
		{
			FolderMIUI3D folderMi3d = (FolderMIUI3D)sender;
			switch( event_id )
			{
				case FolderMIUI3D.MSG_UPDATE_GRIDVIEW_FOR_LOCATION:
					appList.onCtrlEvent( sender , event_id );
					return true;
			}
		}
		else if( sender instanceof Folder3D )
		{
			Folder3D folder3d = (Folder3D)sender;
			switch( event_id )
			{
				case Folder3D.MSG_UPDATE_GRIDVIEW_FOR_LOCATION:
					appList.onCtrlEvent( sender , event_id );
					return true;
			}
		}
		else if( sender instanceof ApplicationListHost )
		{
			final ApplicationListHost list = (ApplicationListHost)sender;
			switch( event_id )
			{
				case ApplicationListHost.MSG_FOCUS_FOLDER:
					if( folderOpened && appHost_folder != null && appHost_folder.mFolderMIUI3D != null )
					{
						appHost_folder.requestFocus();
					}
					return true;
				case ApplicationListHost.MSG_VIEW_TO_ADD_FOLDER:
					if( folderOpened && appHost_folder != null && appHost_folder.mFolderMIUI3D != null )
					{
						ApplicationInfo info = null;
						for( int i = 0 ; i < list.listToAdd().size() ; i++ )
						{
							info = ( (ShortcutInfo)( (Icon3D)list.listToAdd().get( i ) ).getItemInfo() ).appInfo;
							if( appList.mItemInfos.indexOf( info ) != -1 )
							{
								info.angle = appList.mItemInfos.indexOf( info );
								appList.mItemInfos.remove( info );
							}
						}
						ApplicationInfo info2 = null;
						ArrayList<ShortcutInfo> removedAppList = new ArrayList<ShortcutInfo>();
						boolean exist = false;
						for( ShortcutInfo shortcutInfo : appHost_folder.mInfo.contents )
						{
							exist = false;
							info2 = shortcutInfo.appInfo;
							if(info2.isHideIcon)
							{
								continue;
							}
							for( int i = 0 ; i < list.listToAdd().size() ; i++ )
							{
								info = ( (ShortcutInfo)( (Icon3D)list.listToAdd().get( i ) ).getItemInfo() ).appInfo;
								if(info.title.equals( info2.title ) )
								{
									exist = true;
									break;
								}
							}
							if(!exist)
							{
								appList.mItemInfos.add(info2 );
								removedAppList.add(shortcutInfo);
							}
						}
						appHost_folder.mInfo.contents.removeAll(removedAppList);
						appHost_folder.mFolderMIUI3D.setEditText( ApplicationListHost.folderName );
						appHost_folder.mFolderMIUI3D.addChildrenToFolder( list.listToAdd() );
						appList.resetAppIconsStatusInFolder( appHost_folder );
						appList.mNeedRefreshListWhenColseFolder = true;
					}
					return true;
				case ApplicationListHost.MSG_NEW_FOLDER_IN_APPLIST:
					appList.newFolderInApplist( list.listToAdd() );
					return true;
			}
		}
		//xiatian add end  
		// teapotXu add end for Folder in Mainmenu
		return super.onCtrlEvent( sender , event_id );
	}
	
	public void hideApphostV5()
	{
		if( appList != null )
		{
			appList.color.a = 0f;
		}
		if( appBar != null )
		{
			appBar.color.a = 0f;
		}
	}
	
	public void showAppHostV5()
	{
		if( appList != null )
		{
			appList.color.a = 1f;
		}
		if( appBar != null )
		{
			appBar.color.a = 1f;
		}
	}
	
	public static Intent createSetAsIntent(
			Uri uri ,
			String mimeType )
	{
		// Infer MIME type if missing for file URLs.
		if( uri.getScheme().equals( "file" ) )
		{
			String path = uri.getPath();
			int lastDotIndex = path.lastIndexOf( '.' );
			if( lastDotIndex != -1 )
			{
				mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( uri.getPath().substring( lastDotIndex + 1 ).toLowerCase() );
			}
		}
		Intent intent = new Intent( Intent.ACTION_ATTACH_DATA );
		intent.setDataAndType( uri , mimeType );
		intent.putExtra( "mimeType" , mimeType );
		return intent;
	}
	
	// teapotXu add start for Folder in Mainmenu
	public View3D appHost_tag = null;
	public boolean folderOpened = false;
	public boolean closeFolder2goHome = false;
	private FolderIcon3D appHost_folder = null;
	private DragLayer3D appHost_dragLayer = null;
	private Timer timer = null;
	private appHostFolderTweenTask TweenTimerTask;
	private static float appHostFolderTweenDuration = 0.1f;
	
	public void setDragLayer(
			View3D v )
	{
		this.appHost_dragLayer = (DragLayer3D)v;
	}
	
	public FolderIcon3D getFolderIconOpendInAppHost()
	{
		return appHost_folder;
	}
	
	public void setFolderIconOpenedInAppHost(
			FolderIcon3D folder )
	{
		appHost_folder = folder;
	}
	
	public int getAppList3DMode()
	{
		return appList.mode;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true && source == this.getTween() && type == TweenCallback.COMPLETE )
		{
			dealFolderInAppListTweenFinish();
		}
		// teapotXu add end for Folder in Mainmenu
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		Log.v( "cooee" , "AppHost3D---onDrop ---enter --- " );
		if( appList.pointerInAbs( x , y ) && ( appList.isVisibleInParent() || ( appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN ) ) )
		{
			Log.v( "cooee" , "AppHost3D---onDrop --- in AppList area --- " );
			appList.onDrop( list , x , y );
		}
		else if( appBar != null && appBar.pointerInAbs( x , y ) && ( appBar.isVisibleInParent() || ( appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN ) ) )
		{
			Log.v( "cooee" , "AppHost3D---onDrop --- in AppBar area ,and deal with Applist --- " );
			appList.cellDropType = AppList3D.CELL_DROPTYPE_SINGLE_DROP;
			appList.onDrop( list , x , appList.y + appList.getHeight() - 1 );
		}
		else if( DefaultLayout.mainmenu_sort_by_user_fun && app2workspace.pointerInAbs( x , y ) && ( app2workspace.isVisibleInParent() || ( appList.isVisibleInParent() == false && appList
				.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN ) ) )
		{
			Log.v( "cooee" , "AppHost3D---onDrop --- in app2workspace area ,and deal with Applist --- " );
			appList.cellDropType = AppList3D.CELL_DROPTYPE_SINGLE_DROP;
			appList.onDrop( list , x , appList.y + appList.getHeight() - 1 );
		}
		else
		{
			Log.v( "cooee" , "AppHost3D---onDrop --- in error area --- x:" + x + "---y: " + y );
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		if( appList.pointerInAbs( x , y ) && ( appList.isVisibleInParent() || ( appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN ) ) )
		{
			appList.onDropOver( list , x , y );
			if( DefaultLayout.mainmenu_sort_by_user_fun )
			{
				app2workspace.setFocus( false );
			}
		}
		else if( appBar != null && appBar.pointerInAbs( x , y ) && ( appBar.isVisibleInParent() || ( appList.isVisibleInParent() == false && appList.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN ) ) )
		{
		}
		else if( DefaultLayout.mainmenu_sort_by_user_fun && app2workspace.pointerInAbs( x , y ) && ( app2workspace.isVisibleInParent() || ( appList.isVisibleInParent() == false && appList
				.getAppListHideReason() == AppList3D.APP_HIDE_REASON_FOR_FOLDER_OPEN ) ) )
		{
			Log.v( "cooee" , "AppHost3D---onDrop --- in app2workspace area ,and deal with Applist --- " );
			app2workspace.onDropOver( list , x , y );
		}
		else
		{
			return false;
		}
		return true;
	}
	
	public boolean onDropLeave()
	{
		return true;
	}
	
	public void addBackInScreen(
			View3D child ,
			int x ,
			int y )
	{
		appList.addBackInScreen( child , x , y );
	}
	
	public void setDragviewAddFromFolderStatus(
			boolean status )
	{
		appList.is_dragview_from_folder = status;
	}
	
	public boolean getDragviewAddFromFolderStatus()
	{
		return appList.is_dragview_from_folder;
	}
	
	private void dealFolderInAppListTweenFinish()
	{
		{
			if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder == true ) )
			{
				if( this.touchable )
					return;
			}
			View3D view = appHost_tag;
			stopProtectedTimer();
			if( view == null )
			{
				return;
			}
			else if( view instanceof FolderIcon3D )
			{
				FolderIcon3D folderIcon3D = (FolderIcon3D)view;
				if( folderIcon3D.getFromWhere() == FolderIcon3D.FROM_APPLIST )
				{
					addBackInScreen( appHost_folder , appHost_folder.mInfo.x , appHost_folder.mInfo.y );
				}
				folderOpened = false;
				appList.is_maimenufolder_open = false;
				appList.mOpenFolderIcon = null;
				if( folderIcon3D.getParent() == this )
					folderIcon3D.remove();
				folderIcon3D = null;
				if( appHost_dragLayer.isVisible() && !appHost_dragLayer.draging )
				{
					if( appHost_dragLayer.getDragList().size() > 0 && appHost_dragLayer.onDrop() == null )
					{
						// dropListBacktoOrig(dragLayer.getDragList());
					}
					appHost_dragLayer.removeAllViews();
					appHost_dragLayer.hide();
				}
				if( closeFolder2goHome )
				{
					closeFolder2goHome = false;
					// workspace.scrollTo(workspace.getHomePage());
				}
				// hotseatBar.touchable = true;
				// workspace.touchable = true;
			}
		}
		this.touchable = true;
	}
	
	private void appHost_folder_DropList_backtoOrig(
			ArrayList<View3D> child_list )
	{
		View3D view = child_list.get( 0 );
		View3D parent = view.getParent();
		ItemInfo info = null;
		if( view instanceof IconBase3D )
			info = ( (IconBase3D)view ).getItemInfo();
		if( info != null /*
							* && info.container ==
							* LauncherSettings.Favorites.CONTAINER_DESKTOP
							*/) /* is not come from mainmenu */
		{
			for( int i = 0 ; i < child_list.size() ; i++ )
			{
				view = child_list.get( i );
				addBackInScreen( view , 0 , 0 );
			}
		}
	}
	
	// teapotXu add timer function
	class appHostFolderTweenTask extends TimerTask
	{
		
		@Override
		public void run()
		{
			dealFolderInAppListTweenFinish();
		}
	}
	
	private void startProtectedTimer(
			long duration )
	{
		if( timer != null )
			timer.cancel();
		TweenTimerTask = new appHostFolderTweenTask();
		timer = new Timer();
		timer.schedule( TweenTimerTask , duration );
	}
	
	private void stopProtectedTimer()
	{
		if( timer != null )
		{
			TweenTimerTask.cancel();
			timer.cancel();
			timer = null;
		}
	}
	
	// zhujieping add begin
	private void MiuiV5FolderBoxBlurInAppHost(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( appHost_folder.captureCurScreen )
		{
			super.draw( batch , parentAlpha );
			// draw to fbo
			appHost_folder.fbo.begin();
			Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
			// 先绘制壁纸到fbo上
			if( DefaultLayout.blur_bg_enable )
			{
				if( FolderIcon3D.wallpaperTextureRegion != null )
				{
					int wpWidth = FolderIcon3D.wallpaperTextureRegion.getTexture().getWidth();
					int wpHeight = FolderIcon3D.wallpaperTextureRegion.getTexture().getHeight();
					batch.draw( FolderIcon3D.wallpaperTextureRegion , appHost_folder.wpOffsetX , 0 , wpWidth , wpHeight );
				}
				else
				{
					if( FolderIcon3D.liveWallpaperActive )
					{
						if( AppList3D.translucentBg != null )
						{
							batch.setColor( color.r , color.g , color.b , 0.7f );
							AppList3D.translucentBg.draw( batch , 0 , 0 , Utils3D.realWidth , iLoongLauncher.getInstance().getResources().getDisplayMetrics().heightPixels );
						}
					}
				}
			}
			// if (Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.EditMode)
			{
				this.setScale( 0.8f , 0.8f );
				this.transform = true;
			}
			// 绘制当前屏幕画面到fbo上
			super.draw( batch , parentAlpha );
			appHost_folder.fbo.end();
			// if (Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.EditMode)
			{
				appHost_folder.mFolderIndex = appHost_folder.getViewIndex( appHost_folder.mFolderMIUI3D );
				appHost_folder.mFolderMIUI3D.remove();
				this.setScale( 1.0f , 1.0f );
				this.startTween( View3DTweenAccessor.SCALE_XY , Quint.IN , DefaultLayout.blurDuration , 0.8f , 0.8f , 0 ).setCallback( new TweenCallback() {
					
					@Override
					public void onEvent(
							int arg0 ,
							BaseTween arg1 )
					{
						if( arg0 == TweenCallback.COMPLETE )
						{
							setScale( 1.0f , 1.0f );
						}
					}
				} );
			}
			appHost_folder.captureCurScreen = false;
			appHost_folder.blurBegin = true;
		}
		else
		{
			super.draw( batch , parentAlpha );
			if( transform )
			{
				this.appHost_folder.mFolderMIUI3D.draw( batch , parentAlpha );
			}
			if( appHost_folder.blurBegin )
			{
				if( ( appHost_folder.blurCount % 2 ) == 0 )
				{
					appHost_folder.renderTo( appHost_folder.fbo , appHost_folder.fbo2 );
				}
				else
				{
					appHost_folder.renderTo( appHost_folder.fbo2 , appHost_folder.fbo );
				}
				appHost_folder.blurCount++;
				if( appHost_folder.blurCount >= ( DefaultLayout.blurInterate << 1 ) )
				{
					appHost_folder.blurBegin = false;
				}
			}
			if( !appHost_folder.blurCompleted )
			{
				int count = ( DefaultLayout.blurInterate << 1 ) - appHost_folder.blurCount;
				for( int i = 0 ; i < count ; i++ )
				{
					if( ( appHost_folder.blurCount % 2 ) == 0 )
					{
						appHost_folder.renderTo( appHost_folder.fbo , appHost_folder.fbo2 );
					}
					else
					{
						appHost_folder.renderTo( appHost_folder.fbo2 , appHost_folder.fbo );
					}
					appHost_folder.blurCount++;
				}
				if( appHost_folder.blurredView == null )
				{
					float scaleFactor = 1 / DefaultLayout.fboScale;
					appHost_folder.blurredView = new ImageView3D( "blurredView" , appHost_folder.fboRegion );
					appHost_folder.blurredView.show();
					appHost_folder.blurredView.setScale( scaleFactor , scaleFactor );
					appHost_folder.blurredView.setPosition(
							appHost_folder.blurredView.getWidth() / 2 + ( Gdx.graphics.getWidth() / 2 - appHost_folder.blurredView.getWidth() ) ,
							appHost_folder.blurredView.getHeight() / 2 + ( Gdx.graphics.getHeight() / 2 - appHost_folder.blurredView.getHeight() ) );
					Log.v( "blur" , "aaaa " + appHost_folder.blurredView );
					// if (Workspace3D.WorkspaceStatus !=
					// WorkspaceStatusEnum.EditMode)
					{
						this.transform = false;
						this.setScale( 1.0f , 1.0f );
						appHost_folder.addViewAt( appHost_folder.mFolderIndex , appHost_folder.mFolderMIUI3D );
						appHost_folder.addView( appHost_folder.blurredView );
						// this.hideOtherView();
						// appBar.hide();
						// if (FolderIcon3D.liveWallpaperActive) {
						// FolderIcon3D.lwpBackView = new
						// ImageView3D("lwpBackView",
						// ThemeManager.getInstance().getBitmap(
						// "theme/pack_source/translucent-bg.png"));
						// FolderIcon3D.lwpBackView.setSize(Gdx.graphics.getWidth(),
						// Gdx.graphics.getHeight());
						// FolderIcon3D.lwpBackView.setPosition(0, 0);
						// appHost_folder.addView(FolderIcon3D.lwpBackView);
						// hideApphostV5();
						// appHost_folder.ishide = true;
						// }
					}
					// else {
					// this.transform = false;
					//
					// // this.addViewBefore(workspace, folder.blurredView);
					// this.addView(appHost_folder.mFolderMIUI3D);
					// }
				}
				appHost_folder.blurCompleted = true;
			}
		}
	}
	
	// teapotXu add end for Folder in Mainmenu
	@Override
	public void update(
			ArrayList<Plugin> plugins )
	{
		if( tabPluginHost != null )
		{
			tabPluginHost.hide();
			tabPluginHost.removeAllViews();
			if( appBar != null && appBar.tabPopMenu != null )
			{
				appBar.tabPopMenu.hide();
			}
		}
		if( appBar != null )
			appBar.update( plugins );
	}
	
	public void removeDragViews(
			ArrayList<View3D> removedViewList ,
			boolean b_sync_app )
	{
		appList.removeDragViews( removedViewList , b_sync_app );
	}
	
	public void dragEnd()
	{
		appList.hide2WorkspaceView();
	}
	
	public void setModeOnly(
			int mode )
	{
		appList.mode = mode;
		FolderIcon3D.setApplistMode( mode );
		if( appBar != null )
			appBar.appTab.setMode( mode );
	}
	
	public void afainApp(
			int hang ,
			int lie )
	{
		appList.afainApp( hang , lie );
	}
}
