package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreviewTips3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.ReloadCallback;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.action.ActionData;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.miui.MIUIWidgetHost;
import com.iLoong.launcher.miui.MIUIWidgetList;
import com.iLoong.launcher.miui.WorkspaceEditView;
import com.iLoong.launcher.recent.RecentAppHolder;
import com.iLoong.launcher.search.SearchEditTextGroup;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;
import com.umeng.analytics.MobclickAgent;


public class Workspace3D extends NPageBase implements DragSource3D , DropTarget3D , MenuActionListener
{
	
	private final String TAG = "workspace3D";
	public static final int MSG_START_DRAG = 0;
	public static final boolean NEW_CELLLAYOU = true;
	public static WorkspaceStatusEnum WorkspaceStatus = WorkspaceStatusEnum.NormalMode;
	public static final int EditModeAddItemType_NONE = -1;
	public static final int EditModeAddItemType_SHORTCUT = 0;
	public static final int EditModeAddItemType_WIDGET = 1;
	public static int EditModeAddItemType = EditModeAddItemType_NONE;
	public static boolean b_editmode_include_addpage = true;
	public static final int CIRCLE_POP_NONE_ACTION = 1;
	public static final int CIRCLE_POP_ACK_ACTION = 2;
	public static final int CIRCLE_POP_CANCEL_ACTION = 3;
	public static final int MSG_ADD_DB = 1;
	public static final int MSG_ADD_DRAGLAYER = 2;
	public static final int MSG_DEL_POP_ALL = 3;
	// public static final int MSG_MOVE_WIDGET = 4;
	public static final int MSG_LONGCLICK = 4;
	public static final int MSG_ADD_WIDGET = 5;
	public static final int MSG_ADD_SHORTCUT = 6;
	public static final int MSG_PAGE_SHOW_EDIT = 7;
	public static final int CIRCLE_FOLDER_MERGE = 1;
	public static final int CIRCLE_POPUP_WND = 0;
	public static final int MSG_GLOBAL_SEARCH = 8;
	public static final int MSG_CLICK_SPACE = 9;
	public static final int MSG_FINISH_EFFECT = 10;
	public static final int MSG_CHANGE_TO_NORMAL_MODE = 11;
	public static final int MSG_CHANGE_TO_DEL_PAGE = 12;
	public static final int MSG_CHANGE_TO_APPEND_PAGE = 13;
	public static final int ZOOM_DISTANCE = 100;
	private iLoongLauncher launcher;
	private IconCache iconCache;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	private CircleSomething3D circleSomething3D = null;
	private DealCircleSomething delCircleSomehing = null;
	private int mHomePage = 0;
	public boolean dropAnim = true;
	public boolean dropAnimating = false;
	public int dropScreen = -1;
	private int mCircle_State = CIRCLE_POPUP_WND;
	private Icon3D cur_icon;
	private int circleAble = 0;
	private boolean workspaceOnLong = false;
	public boolean workspacetonromal = false;
	// teapotXu add start for icon3D's double-click optimization
	private boolean icon_select_mode = false;
	// teapotXu add end
	public static NinePatch reflectView;
	public static NinePatch zoomView;
	public static boolean is_longKick = false;
	private boolean needStopCover = false;
	public boolean needMoveOutMTKWidget = false;
	private Intent touchintent = null;
	private Handler mhaHandler = null;
	// xiatian add start //EffectPreview
	private EffectPreview3D mWorkspaceEffectPreview;
	private EffectPreviewTips3D mEffectPreviewTips3D;
	// xiatian add end
	public static boolean isHideAll = false;
	// jbc add start
	private MediaView3D musicView;
	public static NinePatch mediaBg = null;
	public static float mediaBgAlpha = 0;
	// xujin
	private MediaView3D cameraView;
	// jbc add end
	// jbc start
	// Related to dragging, folder creation and reordering
	public static final int DRAG_MODE_NONE = 0;
	public static final int DRAG_MODE_CREATE_FOLDER = 1;
	public static final int DRAG_MODE_ADD_TO_FOLDER = 2;
	public static final int DRAG_MODE_REORDER = 3;
	public static int dragMode = DRAG_MODE_NONE;
	private CellLayout3D newsCellLayout3D;
	public Timeline edit_mode_click_2_add_our_widget = null;
	public static boolean hasDownInEditMode;
	public float deltaxnews = 0;
	public float newsRsponeArea = 20;
	public static int newsAniFlag = 0;
	private float mCurDownX = 0;
	public static float mCurDownY;
	public static boolean mCheckScrollDirection;
	public static boolean mScrollVertical;
	public static boolean mScrollGestureAction;
	public static Workspace3D instance;
	public static RecentAppHolder mRecentApplications = null;
	public static boolean isTouchFromWorkspace = false;
	public static boolean isMoved = false;
	public static boolean isDragFromIcon3D = false;
	public static TextureRegion zoomarrow = null;
	public static TextureRegion zoomarrow_top = null;
	public static TextureRegion zoomarrow_bottom = null;
	public static TextureRegion zoomarrow_left = null;
	public static TextureRegion zoomarrow_right = null;
	
	public static void setDragMode(
			int mode )
	{
		dragMode = mode;
	}
	
	public static int getDragMode()
	{
		return dragMode;
	}
	
	// jbc end
	public Icon3D getCurIcon()
	{
		return cur_icon;
	}
	
	public void setCurIcon(
			Icon3D icon )
	{
		cur_icon = icon;
	}
	
	public Workspace3D(
			String name )
	{
		super( name );
		instance = this;
		setActionListener();
		setWholePageList();
		addCircleSomething3D();
		delCircleSomehing = new DealCircleSomething( "delCircleSomehing" , this );
		drawIndicator = false;
		transform = true;
		this.needXRotation = false;
		circleAble = SetupMenuActions.getInstance().getStringToIntger( "circled" );
		if( DefConfig.DEF_S3_SUPPORT == true )
		{
			circleAble = 0;
		}
		zoomarrow = R3D.getTextureRegion( "workspace-zoomarrow" );
		zoomarrow_top = R3D.getTextureRegion( "workspace-zoomarrow_top" );
		zoomarrow_bottom = R3D.getTextureRegion( "workspace-zoomarrow_bottom" );
		zoomarrow_left = R3D.getTextureRegion( "workspace-zoomarrow_left" );
		zoomarrow_right = R3D.getTextureRegion( "workspace-zoomarrow_right" );
		TextureRegion reflect = R3D.getTextureRegion( "workspace-reflect-view" );
		// teapotXu add start for add crystal page effect into workspace
		{
			setEffectType( SetupMenuActions.getInstance().getStringToIntger( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) );
		}
		// teapotXu add end
		TextureRegion zoom = R3D.getTextureRegion( "workspace-zoom-view" );
		reflectView = new NinePatch( reflect , 6 , 6 , 6 , 6 );
		zoomView = new NinePatch( zoom , 6 , 6 , 6 , 6 );
		indicatorView = new IndicatorView( "npage_indicator" , R3D.page_indicator_style );
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			TextureRegion backgroundTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/miui_source/cellLayout-ninepatch.png" ) ) );
			cellLayoutBg = new NinePatch( backgroundTexture , 60 , 60 , 60 , 60 );
		}
		// xujin 添加对bg的判断
		if( ( DefaultLayout.show_music_page || DefaultLayout.show_music_page_enable_config ) && mediaBg == null )
		{
			String bg_name = null;
			if( DefaultLayout.media_view_black_bg )
			{
				bg_name = "theme/pack_source/translucent-black.png";
			}
			else
			{
				bg_name = "theme/mediaview/media_bg.png";
			}
			Bitmap bmp = ThemeManager.getInstance().getBitmap( bg_name );
			if( bmp.getConfig() != Config.ARGB_8888 )
			{
				bmp = bmp.copy( Config.ARGB_8888 , false );
			}
			Texture t = new BitmapTexture( bmp , true , true , new ReloadCallback() {
				
				@Override
				public Bitmap reload()
				{
					String bg_name = "theme/mediaview/media_bg.png";
					Bitmap bmp = ThemeManager.getInstance().getBitmap( bg_name );
					if( bmp.getConfig() != Config.ARGB_8888 )
					{
						bmp = bmp.copy( Config.ARGB_8888 , false );
					}
					return bmp;
				}
			} );
			// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			mediaBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
		}
		// xujin 添加camera bg
		if( ( DefaultLayout.enable_camera || DefaultLayout.show_camera_page_enable_config ) && mediaBg == null )
		{
			String bg_name = "theme/mediaview/media_bg.png";
			Bitmap bmp = ThemeManager.getInstance().getBitmap( bg_name );
			if( bmp.getConfig() != Config.ARGB_8888 )
			{
				bmp = bmp.copy( Config.ARGB_8888 , false );
			}
			Texture t = new BitmapTexture( bmp );
			// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			mediaBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
			bmp.recycle();
		}
	}
	
	public static Workspace3D getInstance()
	{
		return instance;
	}
	
	public void onThemeChanged()
	{
		Log.d( "theme" , "workspace3d onThemeChanged" );
		final int cur = page_index;
		if( DefaultLayout.dynamic_icon )
		{
			DrawDynamicIcon.needrefreshbg = true;
		}
		if( view_list.get( cur ) instanceof CellLayout3D )
		{
			CellLayout3D layout = (CellLayout3D)view_list.get( cur );
			layout.onThemeChanged();
		}
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				for( int i = 0 ; i < view_list.size() ; i++ )
				{
					if( i != cur && view_list.get( i ) instanceof CellLayout3D )
					{
						CellLayout3D layout = (CellLayout3D)view_list.get( i );
						layout.onThemeChanged();
					}
				}
				iLoongLauncher.getInstance().themeChanging = false;
				Desktop3DListener.d3d.ignoreClick( false );
				Desktop3DListener.d3d.ignoreLongClick( false );
			}
		} );
	}
	
	public boolean addInCurrenScreen(
			View3D child ,
			int x ,
			int y ,
			boolean bInEditMode )
	{
		return addInScreen( child , getCurrentPage() , x , y , bInEditMode );
	}
	
	public View3D getViewByItemInfo(
			ItemInfo info )
	{
		if( info == null )
			return null;
		if( info.screen < 0 || info.screen >= view_list.size() )
			return null;
		final CellLayout3D group = (CellLayout3D)view_list.get( info.screen );
		return group.getViewInCell( info.cellX , info.cellY );
	}
	
	public void addBackInScreen(
			View3D child ,
			int x ,
			int y )
	{
		int screen = 0;
		ItemInfo item = ( (IconBase3D)child ).getItemInfo();
		screen = item.screen;
		if( screen < 0 || screen >= view_list.size() )
		{
			ItemInfo itemInfo;
			Log.e( TAG , "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child:" + child );
			if( child instanceof Widget3D )
			{
				itemInfo = ( (IconBase3D)child ).getItemInfo();
				Root3D.deleteFromDB( itemInfo );
				Widget3D widget3D = (Widget3D)child;
				Widget3DManager.getInstance().deleteWidget3D( widget3D );
			}
			return;
		}
		final CellLayout3D group;
		// teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
			{
				group = (CellLayout3D)view_list.get( screen );
			}
			else
			{
				group = (CellLayout3D)view_list.get( screen + 1 );
			}
		}
		else
		{
			group = (CellLayout3D)view_list.get( screen );
		}
		// teapotXu add end
		group.addView( child , item.cellTempX , item.cellTempY );
		if( child instanceof DropTarget3D )
		{
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag( child );
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		}
		group.cellCleanDropStatus();
	}
	
	// teapotXu add start
	public boolean addBackIntoWorkspaceFolder(
			ArrayList<View3D> child_list ,
			long container )
	{
		if( view_list == null || view_list.size() <= 0 )
			return false;
		for( int index = 0 ; index < view_list.size() ; index++ )
		{
			View3D view = view_list.get( index );
			if( view instanceof CellLayout3D )
			{
				for( int i = 0 ; i < ( (CellLayout3D)view ).getChildCount() ; i++ )
				{
					View3D cell_child = ( (CellLayout3D)view ).getChildAt( i );
					if( cell_child instanceof FolderIcon3D )
					{
						UserFolderInfo folderInfo = (UserFolderInfo)( (FolderIcon3D)cell_child ).getItemInfo();
						if( folderInfo != null && folderInfo.id == container )
						{
							( (FolderIcon3D)cell_child ).onDrop( child_list , 0 , 0 );
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	// teapotXu add end
	public boolean removeViewInWorkspace(
			View3D view )
	{
		boolean res = true;
		if( view == null )
			return false;
		ItemInfo item = ( (IconBase3D)view ).getItemInfo();
		int screen = item.screen;
		if( WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			screen = screen + 1;
		}
		if( screen < 0 || screen >= view_list.size() )
			return false;
		final CellLayout3D group = (CellLayout3D)view_list.get( screen );
		group.removeView( view );
		return res;
	}
	
	public boolean addInScreen(
			View3D child ,
			int screen ,
			int x ,
			int y ,
			boolean bInEditMode )
	{
		boolean res = false;
		if( screen < 0 || screen >= view_list.size() )
		{
			Log.e( TAG , "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child:" + child );
			return false;
		}
		if( !( view_list.get( screen ) instanceof CellLayout3D ) )
		{
			if( view_list.get( screen ) instanceof WorkspaceEditView )
			{
				SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getString( RR.string.add_widget_error_tip ) );
			}
			return false;
		}
		final CellLayout3D group = (CellLayout3D)view_list.get( screen );
		group.setScreen( screen );
		child.x = x;
		child.y = y;
		// child.width = width;
		// child.height = height;
		// group.addView(child, screen, lp);
		ItemInfo item = ( (IconBase3D)child ).getItemInfo();
		if( item.cellX == -1 || item.cellY == -1 )
		{
			if( bInEditMode )
			{
				ArrayList<View3D> list = new ArrayList<View3D>();
				list.add( child );
				res = group.addViewInDesktopEditMode( list );
			}
			else
			{
				res = group.addToList( child );
			}
		}
		else
		{
			if( bInEditMode )
			{
				ArrayList<View3D> list = new ArrayList<View3D>();
				list.add( child );
				res = group.addViewInDesktopEditMode( list );
			}
			else
			{
				res = group.addView( child , item.cellX , item.cellY );
			}
		}
		if( res )
		{
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				MIUIWidgetHost mMIUIWidgetHost = iLoongLauncher.getInstance().getD3dListener().getWidgetList();
				MIUIWidgetList mMIUIWidgetList = mMIUIWidgetHost.widgetList;
				if( ( mMIUIWidgetHost != null ) && ( mMIUIWidgetList != null ) )
				{
					Object mObject = mMIUIWidgetList.getTag();
					if( ( mObject != null ) && ( mObject instanceof Float ) )
					{
						edit_mode_click_2_add_our_widget = Timeline.createParallel();
						float anim_duration = 0.5f;
						if( child instanceof Widget3D )
						{
							anim_duration = 1f;
						}
						float endPointX = child.x;
						float endPointY = child.y;
						Vector2 v = new Vector2( 0 , 0 );
						float position = (Float)mObject;
						mMIUIWidgetList.setTag( null );
						v.x += R3D.workspace_cell_width * position;
						float startPointX = v.x;
						float startPointY = v.y;
						child.setPosition( startPointX , startPointY );
						edit_mode_click_2_add_our_widget.push( Tween.to( child , View3DTweenAccessor.POS_XY , anim_duration ).target( endPointX , endPointY , 0 ).ease( Expo.IN ) );
						child.setScale( 0 , 0 );
						child.setOrigin( R3D.workspace_cell_width / 2 , R3D.workspace_cell_height / 2 );
						edit_mode_click_2_add_our_widget.push( Tween.to( child , View3DTweenAccessor.SCALE_XY , anim_duration ).target( 1 , 1 , 0 ).ease( Expo.IN ) );
						child.color.a = 0f;
						edit_mode_click_2_add_our_widget.push( Tween.to( child , View3DTweenAccessor.OPACITY , anim_duration ).target( 1.0f ).ease( Expo.IN ) );
						edit_mode_click_2_add_our_widget.start( View3DTweenAccessor.manager ).setCallback( this );
					}
				}
			}
			Root3D.addOrMoveDB( item );
			if( item instanceof UserFolderInfo )
			{
				iLoongLauncher.getInstance().addFolderInfoToSFolders( (UserFolderInfo)item );
			}
		}
		else
		{
			ItemInfo itemInfo;
			if( child instanceof Widget3D )
			{
				itemInfo = ( (IconBase3D)child ).getItemInfo();
				Root3D.deleteFromDB( itemInfo );
				Widget3D widget3D = (Widget3D)child;
				Widget3DManager.getInstance().deleteWidget3D( widget3D );
			}
		}
		// checkBoundary(child);
		// if(child instanceof Icon3D){
		// info.screen = screen;
		// info.x = x;
		// info.y = y;
		// Root3D.addOrMoveDB(info);
		// }
		if( child instanceof DropTarget3D && res )
		{
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag( child );
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		}
		// Jone add
		Gdx.graphics.requestRendering();
		// Jone ene
		return res;
	}
	
	public static View3D createShortcut(
			ShortcutInfo info ,
			boolean ifShadow )
	{
		Icon3D icon = null;
		if( info.intent != null && info.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
		{
			Bitmap bmp = Bitmap.createBitmap( info.getIcon( iLoongApplication.mIconCache ) );
			float scale = 1f;
			if( bmp.getWidth() != DefaultLayout.app_icon_size || bmp.getHeight() != DefaultLayout.app_icon_size )
			{
				scale = (float)DefaultLayout.app_icon_size / bmp.getWidth();
			}
			if( DefaultLayout.thirdapk_icon_scaleFactor != 1f && !R3D.doNotNeedScale( null , null ) )
			{
				scale = scale * DefaultLayout.thirdapk_icon_scaleFactor;
			}
			if( scale != 1f )
			{
				bmp = Tools.resizeBitmap( bmp , scale );
			}
			icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() , ifShadow );
		}
		else
		{
			if( ifShadow )
			{
				if( Contact3DShortcut.isAContactShortcut( info.intent ) != Contact3DShortcut.CONTACT_DEFAULT )
				{
					icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
				}
				else
				{
					icon = new Icon3D( info.title.toString() , R3D.findRegion( R3D.contact_name ) );
				}
			}
			else
			{
				Bitmap replaceIcon = null;
				Bitmap bmp = null;
				if( Contact3DShortcut.isAContactShortcut( info.intent ) == Contact3DShortcut.CONTACT_DEFAULT )
				{
					Bitmap bmp1 = ThemeManager.getInstance().getBitmap( "theme/iconbg/contactperson-icon.png" );
					bmp = Bitmap.createScaledBitmap( bmp1 , R3D.sidebar_widget_w , R3D.sidebar_widget_h , true );
					icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , null , false );
					bmp1.recycle();
					bmp.recycle();
				}
				else
				{
					int findIndex = iLoongLauncher.getInstance().equalHotSeatIntent( info.intent );
					if( findIndex != -1 )
					{
						/* 需要从主题中寻找替换的图标 */
						Bitmap findBmp = iLoongLauncher.getInstance().findHotSeatBitmap( findIndex );
						if( findBmp != null )
						{
							// teapotXu_20130328 add start: 如果是HotSeat的图标，且已经更换图标，那么增加标识
							info.hotseatDefaultIcon = true;
							// teapotXu_20130328 add end
							info.setIcon( findBmp );
							info.title = iLoongLauncher.getInstance().getHotSeatString( findIndex );
							info.usingFallbackIcon = false;
							bmp = findBmp;
						}
						else
						{
							iLoongApplication.mIconCache.flushIcon( info.intent );
							bmp = iLoongApplication.mIconCache.getIcon( info.intent );
						}
					}
					else
					{
						iLoongApplication.mIconCache.flushIcon( info.intent );
						bmp = iLoongApplication.mIconCache.getIcon( info.intent );
					}
					if( bmp == IconCache.mDefaultIcon )
					{
						info.usingFallbackIcon = true;
						bmp = IconCache.makeDefaultIcon();
					}
					Bitmap bg;
					bg = Icon3D.getIconBg();
					if( info.intent != null && info.intent.getComponent() != null && info.intent.getComponent().getPackageName() != null && info.intent.getComponent().getClassName() != null )
					{
						replaceIcon = DefaultLayout.getInstance().getDefaultShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() );
						if( replaceIcon != null )
						{
							bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
							info.setIcon( bmp );
							bg = null;
						}
						else if( DefaultLayout.getInstance().hasSysShortcutIcon( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
						{
							replaceIcon = info.getIcon( iLoongApplication.mIconCache );
							if( replaceIcon != null )
							{
								bmp = Tools.resizeBitmap( replaceIcon , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
								info.setIcon( bmp );
								bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
							}
						}
					}
					if( info.intent == null || info.intent.getComponent() == null || info.intent.getComponent().getPackageName() == null )
					{
						if( !R3D.doNotNeedScale( null , null ) )
						{
							if( info.intent == null || info.intent.getAction() == null )
							{
								if( Icon3D.getIconBg() != null && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
								{
									bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
									bg = Icon3D.getIconBg();
								}
								else
								{
									bg = null;
								}
							}
							else
							{
								if( Icon3D.getIconBg() != null && info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && !iLoongLauncher.getInstance().isDefaultHotseats( info.intent ) )
								{
									bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
									bg = Icon3D.getIconBg();
								}
								else
								{
									bg = null;
								}
							}
							icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , bg , false );
						}
						else
						{
							icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() , false );
						}
					}
					else
					{
						if( !R3D.doNotNeedScale( info.intent.getComponent().getPackageName() , info.intent.getComponent().getClassName() ) )
						{
							if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT && replaceIcon == null )
							{
								bmp = Tools.resizeBitmap( bmp , DefaultLayout.thirdapk_icon_scaleFactor );
							}
							icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , bg , false );
						}
						else
						{
							icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , null , false );
						}
					}
				}
			}
		}
		if( icon != null )
		{
			icon.setItemInfo( info );
		}
		return icon;
	}
	
	View3D createShortcut(
			ViewGroup3D parent ,
			ShortcutInfo info )
	{
		Icon3D icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
		icon.setItemInfo( info );
		// TextView favorite = (TextView) mInflater.inflate(layoutResId, parent,
		// false);
		//
		// favorite.setCompoundDrawablesWithIntrinsicBounds(null,
		// new
		// FastBitmapDrawable(info.getIcon(mIconCache),info.getIconBackgroudBmp(context)),
		// null, null);
		// favorite.setText(info.title);
		// favorite.setTag(info);
		// favorite.setOnClickListener(launcher);
		// return favorite;
		return icon;
	}
	
	public void bindItem(
			ItemInfo item )
	{
		switch( item.itemType )
		{
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
				final View3D shortcut = createShortcut( (ShortcutInfo)item , true );
				// sunyinwei added for do not add shortcut icon when package is not
				// exist
				ShortcutInfo app = (ShortcutInfo)item;
				if( app.intent == null || app.intent.getComponent() == null || app.intent.getComponent().getPackageName() == null )
				{
					addInScreen( shortcut , item.screen , item.x , item.y , false );
				}
				else if( DefaultLayout.checkApkExist( iLoongLauncher.getInstance() , app.intent.getComponent().getPackageName() ) )
				{
					addInScreen( shortcut , item.screen , item.x , item.y , false );
				}
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER:
				// FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon,
				// this,
				// (ViewGroup3D)getChildAt(getCurrentScreen()), (UserFolderInfo)
				// item, mIconCache);
				// String TitleStr=((UserFolderInfo)(item)).title.toString();
				FolderIcon3D rootFolder = iLoongLauncher.getInstance().getOpenFolder();
				/*
				 * 表示在插拔USB或者T卡的时候，有存在的已经打开的文件夹�? 这种情况下不需要创建新的文件夹，仅仅需要替换打开的文件夹中图标的纹理
				 */
				if( rootFolder != null && rootFolder.getItemInfo().id == item.id )
				{
					// Log.e("test", "root 3d have opend folder");
					// zhujieping add
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						// rootFolder.mFolderMIUI3D.updateTexture();
						rootFolder.mFolderMIUI3D.DealButtonNoAnim();
					}
					else
						rootFolder.mFolder.DealButtonOKDown();// rootFolder.mFolder.updateTexture();
					// then remove the foldericon
					removeViewInWorkspace( rootFolder );
				}
				// else
				{
					UserFolderInfo folderInfo = (UserFolderInfo)item;
					FolderIcon3D newFolder = new FolderIcon3D( folderInfo );
					addInScreen( newFolder , item.screen , item.x , item.y , false );
					newFolder.createAndAddShortcut( iconCache , folderInfo );
				}
				break;
			case LauncherSettings.Favorites.ITEM_TYPE_LIVE_FOLDER:
				// final FolderIcon newLiveFolder = LiveFolderIcon.fromXml(
				// R.layout.live_folder_icon, this,
				// (ViewGroup) workspace.getChildAt(workspace.getCurrentScreen()),
				// (LiveFolderInfo) item);
				// workspace.addInScreen(newLiveFolder, item.screen, item.cellX,
				// item.cellY, 1, 1,
				// false);
				break;
		}
		// CellLayout3D cellLayout = this.getCurrentCellLayout();
		// cellLayout.resetInfo();
	}
	
	public boolean hasNextPage()
	{
		if( getCurrentPage() < this.getPageNum() - 1 )
		{
			return true;
		}
		return false;
	}
	
	public boolean hasPreviousPage()
	{
		if( getCurrentPage() > 0 )
			return true;
		else
			return false;
	}
	
	public int getNextCellLayoutCount()
	{
		int size = 0;
		int pageNum = this.getPageNum();
		int current = this.getCurrentPage();
		if( current < this.getPageNum() - 1 )
		{
			size = ( (ViewGroup3D)getChildAt( current + 1 ) ).getChildCount();
		}
		return size;
	}
	
	public int getPreviousCellLayoutCount()
	{
		int size = 0;
		int pageNum = this.getPageNum();
		int current = this.getCurrentPage();
		if( current > 0 )
		{
			size = ( (ViewGroup3D)getChildAt( current - 1 ) ).getChildCount();
		}
		return size;
	}
	
	public int getCurrentScreen()
	{
		return page_index;
	}
	
	public void setCurrentScreen(
			int index )
	{
		removeIconGroupAndPopView();
		page_index = index;
		initView();
		setDegree( 0 , 0 );
	}
	
	public CellLayout3D getCurrentCellLayout()
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
			{
				View3D temp;
				if( page_index >= this.getChildCount() )
				{
					temp = getChildAt( 0 );
				}
				else
				{
					temp = getChildAt( page_index );
				}
				if( temp instanceof CellLayout3D )
				{
					return (CellLayout3D)getChildAt( page_index );
				}
				else
				{
					for( int i = 0 ; i < this.getPageNum() ; i++ )
					{
						temp = getChildAt( i );
						if( temp instanceof CellLayout3D )
						{
							page_index = i;
							return (CellLayout3D)temp;
						}
					}
					Log.i( "cooee" , "workspace3D --- getCurrentCellLayout --pageIndex:" + page_index + " getChildCount():" + this.getChildCount() + " celllayout null" );
					return null;
				}
				// if( page_index >= this.getChildCount() )
				// return (CellLayout3D)getChildAt( 0 );
				// return (CellLayout3D)getChildAt( page_index );
			}
			else
			{
				View3D temp;
				if( page_index >= this.getChildCount() )
				{
					temp = getChildAt( 0 );
				}
				else
				{
					temp = getChildAt( page_index );
				}
				if( temp instanceof CellLayout3D )
				{
					return (CellLayout3D)getChildAt( page_index );
				}
				else if( temp instanceof WorkspaceEditView )
				{
					return null;
				}
				else
				{
					for( int i = 0 ; i < this.getPageNum() ; i++ )
					{
						temp = getChildAt( i );
						if( temp instanceof CellLayout3D )
						{
							return (CellLayout3D)temp;
						}
					}
					return null;
				}
			}
		}
		else
		{
			View3D temp;
			if( page_index >= this.getChildCount() )
			{
				temp = getChildAt( 0 );
			}
			else
			{
				temp = getChildAt( page_index );
			}
			if( temp instanceof CellLayout3D )
			{
				return (CellLayout3D)getChildAt( page_index );
			}
			else
			{
				for( int i = 0 ; i < this.getPageNum() ; i++ )
				{
					temp = getChildAt( i );
					if( temp instanceof CellLayout3D )
					{
						page_index = i;
						return (CellLayout3D)temp;
					}
				}
				return null;
			}
		}
	}
	
	@Override
	public void addView(
			View3D actor )
	{
		if( ( circleSomething3D = (CircleSomething3D)findView( "CircleSomething3D" ) ) != null )
		{
			super.addViewBefore( circleSomething3D , actor );
		}
		else
		{
			super.addView( actor );
		}
	}
	
	public void setIconCache(
			IconCache iconCache )
	{
		this.iconCache = iconCache;
	}
	
	public void setHomePage(
			int homePage )
	{
		mHomePage = homePage;
	}
	
	public int getHomePage()
	{
		return mHomePage;
	}
	
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		boolean dropRes = true;
		List<View3D> iconGroupInput = new ArrayList<View3D>();
		for( View3D view : list )
		{
			if( DefaultLayout.enable_show_widgetzoom )
			{
				if( view instanceof ViewCircled3D )
				{
					iconGroupInput.add( (View3D)view );
				}
				else if( view instanceof Widget3D && Math.abs( mCurDownY - y ) < 10f )
				{
					if( ( (Widget3D)view ).itemInfo.screen == this.getCurrentScreen() )
					{
						if( !( (Widget3D)view ).getPackageName().equals( "com.iLoong.Clean" ) )
						{
							int count = ( (ViewGroup3D)( this.getChildAt( this.getCurrentPage() ) ) ).getChildCount();
							for( int i = 0 ; i < count ; i++ )
							{
								( (ViewGroup3D)( this.getChildAt( this.getCurrentPage() ) ) ).getChildAt( i ).startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 0.5f , 0 , 0 );
							}
							iLoongLauncher.getInstance().getD3dListener().getRoot().hotseatBar.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 0.5f , 0 , 0 );
							view.color.a = 1f;
							iLoongLauncher.getInstance().getD3dListener().getRoot().addZoomWidget( view );
						}
					}
				}
				else if( view instanceof Widget && Math.abs( mCurDownY - y ) < 10f )
				{
					if( ( (Widget)view ).itemInfo.screen == this.getCurrentScreen() )
					{
						MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ResizeSysWidget" );
						int count = ( (ViewGroup3D)( this.getChildAt( this.getCurrentPage() ) ) ).getChildCount();
						for( int i = 0 ; i < count ; i++ )
						{
							( (ViewGroup3D)( this.getChildAt( this.getCurrentPage() ) ) ).getChildAt( i ).startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 0.5f , 0 , 0 );
						}
						iLoongLauncher.getInstance().getD3dListener().getRoot().hotseatBar.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 0.5f , 0 , 0 );
						view.color.a = 1f;
						iLoongLauncher.getInstance().getD3dListener().getRoot().addZoomWidget( view );
					}
					//					CellLayout3D cellLayout = new CellLayout3D( "celllayout" );
					//					zbutton = cellLayout.reflectViewPosition( view );
					//					iLoongLauncher.getInstance().getD3dListener().getRoot().addView( zbutton );
					//					zbutton.show();
					//					zoomkuang = cellLayout.reflectView( view );
					//					iLoongLauncher.getInstance().getD3dListener().getRoot().addView( zoomkuang );
				}
			}
			else
			{
				if( view instanceof ViewCircled3D )
				{
					// view.x = x;
					// view.y = y;
					iconGroupInput.add( (View3D)view );
				}
			}
			// if (view instanceof ViewGroupCircled3D)
			// {
			// view.x = x;
			// view.y = y;
			// iconGroupInput.add((View3D) view);
			// }
		}
		int index;
		View3D tempGroup;
		CellLayout3D group;
		if( dropScreen != -1 )
		{
			index = dropScreen;
			tempGroup = getChildAt( dropScreen );
			dropScreen = -1;
		}
		else
		{
			index = this.getCurrentScreen();
			tempGroup = getChildAt( this.getCurrentScreen() );
		}
		// teapotXu add start for longClick in Workspace to editMode as miui
		if( tempGroup == null || ( tempGroup instanceof CellLayout3D ) == false )
		{
			if( tempGroup instanceof WorkspaceEditView )
			{
				{
					tempGroup.setTag( tempGroup );
					this.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
				}
				index = this.getCurrentScreen();
				tempGroup = getChildAt( this.getCurrentScreen() );
			}
			else
			{
				if( list.size() == 1 )
				{
					View3D view = list.get( 0 );
					if( view instanceof Widget3D )
					{/* widget3D from mainmenu */
						Widget3D widget3D = (Widget3D)view;
						Widget3DManager.getInstance().deleteWidget3D( widget3D );
					}
					else if( view instanceof Widget )
					{
						SendMsgToAndroid.deleteSysWidget( (Widget)view );
						Widget widget = (Widget)view;
						widget.dispose();
					}
				}
				return true;
			}
		}
		// teapotXu add end
		group = (CellLayout3D)tempGroup;
		if( group == null )
		{
			Log.e( "launcher" , " group == null!!!" );
			return true;
		}
		if( iconGroupInput.size() > 1 )
		{
			if( Workspace3D.NEW_CELLLAYOU )
			{
				group.setScreen( index );
				View3D temp = iconGroupInput.get( 0 );
				View3D viewParent = temp.getParent();
				return group.addView( list , scaleX( viewParent.x + viewParent.width / 2 ) , scaleY( viewParent.y + viewParent.height / 2 ) );
			}
			else
			{
				if( !group.checkIconNums( iconGroupInput.size() ) )
					return true;
				IconGroup3D iconGroup = new IconGroup3D( "IconGroupOnDropView" , iconGroupInput );
				delCircleSomehing.addIconView( iconGroup );
				// group.addView(iconGroup);
			}
		}
		else
		{
			View3D view = list.get( 0 );
			if( DefaultLayout.enable_workspace_push_icon )
			{
				group.startReorderTween( CellLayout3D.REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP );
			}
			if( view instanceof Widget2DShortcut )
			{
				WidgetShortcutInfo info = ( (Widget2DShortcut)view ).widgetInfo;
				this.setTag( view );
				view.setTag( new int[]{ scaleX( x ) , scaleY( y ) } );
				if( info.isWidget )
					viewParent.onCtrlEvent( this , MSG_ADD_WIDGET );
				else if( info.isShortcut )
					viewParent.onCtrlEvent( this , MSG_ADD_SHORTCUT );
				view.remove();
				return true;
			}
			float oldX = view.x + view.getParent().x;
			float oldY = view.y + view.getParent().y;
			if( Workspace3D.NEW_CELLLAYOU )
			{
				group.setScreen( index );
				View3D viewParent = view.getParent();
				dropRes = group.addView( list , scaleX( viewParent.x + viewParent.width / 2 ) , scaleY( viewParent.y + viewParent.height / 2 ) );
				int test = group.getCellDropType();
				if( test == CellLayout3D.CELL_DROPTYPE_SINGLE_DROP_FOLDER )
				{
					onDropCompleted( view , true );
					return dropRes;
				}
				if( dropRes && view instanceof Widget )
				{
					Widget widget = (Widget)view;
					SendMsgToAndroid.sendMoveWidgetMsg( view , widget.getItemInfo().screen );
				}
			}
			else
			{
				group.addView( view );
			}
			onDropCompleted( view , true );
			if( dropAnim && dropRes )
			{
				// dropAnimating = true;
				// view.setScale(0.7f, 0.7f);
				// view.startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT,
				// 0.7f, 1f,
				// 1f, 0f).setCallback(this);
			}
			if( dropRes && view instanceof IconBase3D )
			{
				Log.v( "test" , "workspace3D add to database" );
				ItemInfo info = ( (IconBase3D)view ).getItemInfo();
				long old_info_id = info.id;
				info.screen = index;
				info.x = (int)view.x;
				info.y = (int)view.y;
				Root3D.addOrMoveDB( info );
				if( view instanceof Icon3D )
				{
					Icon3D iconView = (Icon3D)view;
					iconView.setItemInfo( iconView.getItemInfo() );
				}
				if( view instanceof DropTarget3D )
				{
					// mDragController.addDropTarget((DropTarget)child);
					if( info instanceof UserFolderInfo )
					{
						iLoongLauncher.getInstance().addFolderInfoToSFolders( (UserFolderInfo)info );
						// teapotXu add start for Folder in mainmenu
						if( DefaultLayout.mainmenu_folder_function == true && old_info_id > LauncherSettings.Favorites.CONTAINER_APPLIST )
						{
							for( ShortcutInfo sInfo : ( (UserFolderInfo)info ).contents )
							{
								sInfo.screen = ( (UserFolderInfo)info ).contents.size();
								Root3D.addOrMoveDB( sInfo , ( (UserFolderInfo)info ).id );
							}
						}
						// teapotXu add end
					}
					( (IconBase3D)view ).setItemInfo( info );
					this.setTag( view );
					viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
				}
			}
			if( dropRes )
			{
				int tx = (int)view.x;
				int ty = (int)view.y;
				if( x != tx || y != ty )
				{
					dropAnimating = true;
					view.setPosition( oldX , oldY );
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , tx , ty , 0 ).setCallback( this );
					/************************ added by zhenNan.ye begin ***************************/
					if( DefaultLayout.enable_particle )
					{
						if( ParticleManager.particleManagerEnable )
						{
							ParticleManager.dropEnable = true;
						}
					}
					/************************ added by zhenNan.ye end ***************************/
				}
			}
		}
		// list.clear();
		return dropRes;
	}
	
	private int scaleX(
			float x )
	{
		return (int)( width / 2 + ( x - width / 2 ) / scaleX );
	}
	
	private int scaleY(
			float y )
	{
		return (int)( height / 2 + ( y - height / 2 ) / scaleY );
	}
	
	void forceSetCellLayoutDropType(
			int index )
	{
		CellLayout3D group = (CellLayout3D)getChildAt( index );
		group.setCellDropTypeArrayDrop();
	}
	
	private void removeIconGroupAndPopView()
	{
		View3D actor;
		if( ( actor = findView( "IconGroupOnDropView" ) ) != null )
		{
			actor.releaseFocus();
			this.removeView( actor );
		}
		if( ( actor = findView( "PopIconGroupView" ) ) != null )
		{
			( (IconGroup3D)actor ).DealButtonOKDown();
			this.removeView( actor );
		}
		if( ( actor = findView( "circlePopWnd3D" ) ) != null )
		{
			delCircleSomehing.resetToNothingCircle();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iLoong.launcher.Desktop3D.NPageBase#hide()
	 */
	@Override
	public void hide()
	{
		Messenger.sendMsg( Messenger.MSG_START_COVER_MTKWIDGET , 0 , 0 );
		needStopCover = true;
		onDragOverLeave();
		removeIconGroupAndPopView();
		super.hide();
		SendMsgToAndroid.sendHideWorkspaceMsg();
		clearDragObjs();
	}
	
	public void show()
	{
		// if (!isVisible()){
		// if(DefaultLayout.enable_workspace_miui_edit_mode &&
		// Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode){
		//
		// }
		// else
		// {
		if( !isVisible() && this.scaleX == 1 && this.getUser() == 0 && this.color.a != 0 )
		{
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		// }
		// if (!isVisible() && this.scaleX == 1 && this.getUser() == 0
		// && this.color.a != 0) {
		// SendMsgToAndroid.sendShowWorkspaceMsg();
		// }
		if( needStopCover )
		{
			SendMsgToAndroid.sendStopCoverMTKWidgetMsg();
			needStopCover = false;
		}
		super.show();
		//iLoongLauncher.getInstance().fireupRecentApp();
	}
	
	@Override
	protected void finishAutoEffect()
	{
		// //xujin
		// if( lastType >= 0 )
		// {
		// mType = lastType;
		// }
		// xujin 确保专属页 hotseat不显示
		HotSeat3D hotseat = (HotSeat3D)iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar();
		if( curIsMusicView() || curIsCameraView() )
		{
			hotseat.y = -R3D.seatbar_hide_height;
		}
		super.finishAutoEffect();
		if( DefaultLayout.show_music_page || DefaultLayout.enable_camera )// xujin
		{
			( (Root3D)viewParent ).showSuitableSeat();
		}
		if( xScale == 0 && mVelocityX == 0 )
		{
			SendMsgToAndroid.sendMoveInMTKWidgetMsg();
			// if(DefaultLayout.enable_workspace_miui_edit_mode &&
			// Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode){
			// }else
			// {
			SendMsgToAndroid.sendShowWorkspaceMsg();
			// }
			// SendMsgToAndroid.sendShowWorkspaceMsg();
			// for(View3D view : children){
			// if(view instanceof CellLayout3D){
			// ((CellLayout3D)view).hideWidget();
			// }
			// }
			if( DefaultLayout.keypad_event_of_focus )
			{
				CellLayout3D cellLayout = this.getCurrentCellLayout();
				if( cellLayout != null )
				{
					cellLayout.changeFocus();
					cellLayout.setVisible();
				}
			}
			SendMsgToAndroid.sendCancelWaitClingMsg();
			this.onCtrlEvent( this , MSG_FINISH_EFFECT );
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( Desktop3DListener.root.qSearchGroup != null )
		{
			if( Desktop3DListener.root.qSearchGroup.visible || SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
			{
				return true;
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			viewParent.onCtrlEvent( this , MSG_CHANGE_TO_NORMAL_MODE );
			return true;
		}
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) )
		{
			if( RR.net_version )
			{
				Desktop3DListener.root.backToBoxEffectTab();
			}
			return true;
		}
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
		if( ret == true )
		{
			return ret;
		}
		this.setTag( new Vector2( x , Utils3D.getScreenHeight() - y ) );
		// Jone add start
		/****
		 * while being clicked on blind space,workspace will change to normal
		 * style.
		 */
		if( RR.net_version )
		{
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				viewParent.onCtrlEvent( this , MSG_CHANGE_TO_NORMAL_MODE );
			}
		}
		// Jone end
		return viewParent.onCtrlEvent( this , MSG_CLICK_SPACE );
	}
	
	// xujin
	// private int lastType;
	// private static final int defaultType = 0;
	// xujin
	// @Override
	// public void setEffectType(
	// int type )
	// {
	// super.setEffectType( type );
	// lastType = type;
	// }
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			if( DesktopEditHost.getPopMenuStyle() == DesktopEditHost.POP_MENU_STYLE_4X2 )
			{
				if( y > ( R3D.pop_menu_container_height ) )
					return true;
				else
					return false;
			}
			else
			{
				if( y > ( R3D.pop_menu_height ) )
					return true;
				else
					return false;
			}
		}
		return super.pointerInParent( x , y );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( Desktop3DListener.root.qSearchGroup != null && Desktop3DListener.root.qSearchGroup.visible )
		{
			return true;
		}
		isMoved = false;
		mCurDownX = x;
		prepareGesture( y );
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mPreviewTween != null ) )
		{
			requestFocus();
			return true;
		}
		// xiatian add end
		SendMsgToAndroid.sendHideWorkspaceMsg();
		needMoveOutMTKWidget = true;
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleStart( this.x , this.y , x , y );
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	public static void prepareGesture(
			float y )
	{
		mCurDownY = y;
		isTouchFromWorkspace = true;
		mCheckScrollDirection = true;
		mScrollVertical = false;
		mScrollGestureAction = false;
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		boolean ret = super.onTouchDragged( x , y , pointer );
		if( Math.abs( y - mCurDownY ) > 10f || Math.abs( x - mCurDownX ) > 10f )
		{
			isMoved = true;
		}
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
		return ret;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( Desktop3DListener.root.qSearchGroup != null && Desktop3DListener.root.qSearchGroup.visible )
		{
			return true;
		}
		isTouchFromWorkspace = false;
		if( mScrollGestureAction )
		{
			releaseFocus();
			return true;
		}
		if( ( DefaultLayout.enable_effect_preview ) && ( mPreviewTween != null ) )
		{
			releaseFocus();
			return true;
		}
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_WORKSPACE );
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		if( workspaceOnLong )
			mVelocityX = 0;
		boolean bool = super.onTouchUp( x , y , pointer );
		Log.d( "test" , " workspace3D onTouchUp" );
		workspaceOnLong = false;
		workspacetonromal = false;
		if( xScale == 0 && mVelocityX == 0 && !circleSomething3D.isVisible() && !dropAnimating )
		{
			SendMsgToAndroid.sendMoveInMTKWidgetMsg();
			// if(DefaultLayout.enable_workspace_miui_edit_mode &&
			// Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode){
			// }else
			// {
			SendMsgToAndroid.sendShowWorkspaceMsg();
			// }
			// SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		return bool;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( iLoongLauncher.isShowNews )
		{
			return true;
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
		if( this.mRecentApplications != null && mRecentApplications.mAnimState != 0 )
		{
			return false;
		}
		checkScrollDirection( x , y );
		scrollGestureEvent( x , y , deltaY );
		if( mScrollVertical || mScrollGestureAction )
		{
			return true;
		}
		if( ( DefaultLayout.enable_effect_preview ) && ( mPreviewTween != null ) )
		{
			return true;
		}
		if( ( DefaultLayout.enable_effect_preview ) && ( mEffectPreviewTips3D.isVisible() ) )
		{
			mEffectPreviewTips3D.hide();
		}
		if( workspaceOnLong == true )
		{
			return true;
		}
		if( DefaultLayout.optimize_hotseat_scroll_back )
		{
			// 只有当下上滑动操作，并且在如下的区域内时，才强制响应为hotseat scroll
			if( ( Math.abs( deltaX ) < Math.abs( deltaY ) && Math.abs( deltaX ) < R3D.getInteger( "scroll_up_and_down_min_delta_y" ) ) && ( x >= 0 && x < Utils3D.getScreenWidth() && y >= 0 && y < R3D.page_indicator_y + R3D
					.getInteger( "page_indicator_height" ) + R3D.getInteger( "scroll_back_hotseat_offset" ) ) )
			{
				HotSeat3D hotseat = (HotSeat3D)iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar();
				if( hotseat != null && HotSeat3D.STATE_BACK == hotseat.getHot3DState() )
				{
					if( hotseat.getMainGroup() != null )
					{
						hotseat.getMainGroup().scroll( x , y , deltaX , deltaY );
						return true;
					}
				}
			}
		}
		SendMsgToAndroid.sendMoveOutMTKWidgetMsg();
		if( needMoveOutMTKWidget )
		{
			needMoveOutMTKWidget = false;
			SendMsgToAndroid.sendMoveOutMTKWidgetMsg();
		}
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	public void startAutoEffect(
			boolean next )
	{
		Root3D.isDragAutoEffect = true;
		super.startAutoEffect( next );
	}
	
	protected void clearDragObjs()
	{
		for( View3D view : dragObjects )
		{
			if( view instanceof Icon3D )
			{
				Icon3D icon1 = (Icon3D)view;
				icon1.hideSelectedIcon();
			}
		}
		dragObjects.clear();
		// teapotXu add start for icon3D's double-click optimization
		if( this.icon_select_mode == true )
		{
			this.icon_select_mode = false;
			updateAllIconsSelectStateInWorkspace();
		}
		// teapotXu add end
	}
	
	public void setIconsSelectState()
	{
		for( View3D view : dragObjects )
		{
			if( view instanceof Icon3D )
			{
				Icon3D icon1 = (Icon3D)view;
				icon1.hideSelectedIcon();
			}
		}
		dragObjects.clear();
		if( this.icon_select_mode == true )
		{
			this.icon_select_mode = false;
			updateAllIconsSelectStateInWorkspace();
		}
	}
	
	public void DealCircleSomethingResult(
			float x ,
			float y )
	{
		// delCircleSomehing = new
		// DealCircleSomething("delCircleSomehing",this);
		delCircleSomehing.DealCircleSomethingResult( x , y , mCircle_State );
	}
	
	public void addCircleSomething3D()
	{
		circleSomething3D = new CircleSomething3D( "CircleSomething3D" );
		if( circleSomething3D != null )
		{
			circleSomething3D.hide();
		}
		addView( circleSomething3D );
	}
	
	// teapotXu add start for icon3D's double-click optimization
	private void updateAllIconsSelectStateInWorkspace()
	{
		for( int index = 0 ; index < this.getChildCount() ; index++ )
		{
			View3D child_group = this.getChildAt( index );
			if( child_group instanceof CellLayout3D )
			{
				for( int i = 0 ; i < ( (CellLayout3D)child_group ).getChildCount() ; i++ )
				{
					View3D child_view = ( (CellLayout3D)child_group ).getChildAt( i );
					if( child_view instanceof Icon3D )
					{
						if( this.icon_select_mode == true )
						{
							( (Icon3D)child_view ).setSelectMode( true );
						}
						else
							( (Icon3D)child_view ).clearState();
					}
				}
			}
		}
	}
	
	// teapotXu add end
	public void onHomeKey(
			boolean alreadyOnHome )
	{
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		if( ActionData.isActionShow )
		{
			return true;
		}
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			boolean key_back_dealed = false;
			if( icon_select_mode )
			{
				icon_select_mode = false;
				key_back_dealed = true;
				updateAllIconsSelectStateInWorkspace();
			}
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				if( key_back_dealed == false )
				{
					viewParent.onCtrlEvent( this , MSG_CHANGE_TO_NORMAL_MODE );
				}
			}
			return true;
		}
		return super.keyDown( keycode );
	}
	
	private long gapTime = 0;
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK && iLoongLauncher.isShowNews == true )
		{
			Messenger.sendMsg( Messenger.MSG_REMOVE_NEWS_AUTO , 0 );
			return true;
		}
		if( ActionData.isActionShow )
		{
			return true;
		}
		if( DefaultLayout.enable_effect_preview )
		{
			if( keycode == KeyEvent.KEYCODE_BACK )
			{
				Root3D mRoot3D = iLoongLauncher.getInstance().getD3dListener().getRoot();
				if( mRoot3D.isWorkspaceEffectPreviewMode() )
				{
					if( ( mPreviewTween == null ) && ( mEffectPreviewTips3D.isVisible() ) )
					{
						mRoot3D.backToBoxEffectTab();
					}
					return true;
				}
			}
		}
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview != null )
			{
				ViewGroup3D zoom = iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview;
				if( zoom != null )
				{
					iLoongLauncher.getInstance().getD3dListener().getRoot().removeView( zoom );
					zoom = null;
					iLoongLauncher.getInstance().getD3dListener().getRoot().zoomview = null;
				}
				iLoongLauncher.getInstance().getD3dListener().getRoot().ReturnClor();
				return true;
			}
			final FolderIcon3D findFolder = iLoongLauncher.getInstance().d3dListener.getOpenFolder();
			final FolderIcon3D findFolderInMainmenu = iLoongLauncher.getInstance().d3dListener.getOpenFolderInMainmenu();
			if( Workspace3D.isRecentAppVisible() )
			{
				Workspace3D.getInstance().mRecentApplications.destory();
			}
			else if( findFolder != null && findFolder.mInfo.opened == true )
			{
				if( !iLoongLauncher.getInstance().d3dListener.isApplitionListToAddShow() )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
							{
								if( findFolder.mFolderMIUI3D.bEnableTouch == true )
								{
									findFolder.mFolderMIUI3D.DealButtonOKDown();
								}
							}
							else if( findFolder.mFolder.bEnableTouch == true )
							{
								findFolder.mFolder.DealButtonOKDown();
							}
						}
					} );
				}
			}
			else if( DefaultLayout.enable_news && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && Desktop3DListener.root.qSearchGroup != null && !Desktop3DListener.root.qSearchGroup.visible )
			{
				if( DefaultLayout.show_newspage_with_back )
				{
					if( !iLoongLauncher.isShowNews )
					{
						if( System.currentTimeMillis() - gapTime > 500 )
						{
							gapTime = System.currentTimeMillis();
						}
						else
						{
							if( Desktop3DListener.root.newsHandle != null )
							{
								if( Desktop3DListener.root.newsHandle.x == 0 )
								{
									Messenger.sendMsg( Messenger.MSG_SHOW_NEWS_AUTO , 0 );
								}
								else
								{
									Messenger.sendMsg( Messenger.MSG_SHOW_NEWS_AUTO , 1 );
								}
							}
							gapTime = 0;
						}
					}
					else
					{
						Messenger.sendMsg( Messenger.MSG_REMOVE_NEWS_AUTO , 0 );
					}
				}
				else
				{
					Messenger.sendMsg( Messenger.MSG_REMOVE_NEWS_AUTO , 0 );
				}
			}
			return true;
		}
		return super.keyUp( keycode );
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof CellLayout3D )
		{
			switch( event_id )
			{
				case CellLayout3D.MSG_ADD_DRAGLAYER:
					setTag( sender.getTag() );
					return viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
				case CellLayout3D.MSG_PAGE_TO:// zqh
					Object obj = sender.getTag();
					if( obj instanceof String )
					{
						String s = (String)obj;
						int next = 0;
						if( s.equals( "left" ) )
							next = this.getCurrentPage() - 1;
						else if( s.equals( "right" ) )
							next = this.getCurrentPage() + 1;
						else if( s.equals( "firt" ) )
						{
							next = 0;
						}
						else if( s.equals( "last" ) )
						{
							next = this.getPageNum() - 1;
						}
						CellLayout3D layout = this.getCurrentCellLayout();
						CellLayout3D layout3d = null;
						View3D view = this.getChildAt( next );
						if( view instanceof CellLayout3D )
							layout3d = (CellLayout3D)view;
						if( layout != null && layout3d != null )
						{
							layout3d.cursorX = layout.cursorX;
							layout3d.cursorY = layout.cursorY;
							if( layout.touchEvent == true )
							{
								layout3d.touchEvent = true;
							}
							else
							{
								layout3d.touchEvent = false;
							}
							layout.setInvisible();
							// layout3d.setVisible();
						}
						CellLayout3D.nextPageIndex = next;
						this.scrollTo( next );
					}
					return true;
				case CellLayout3D.MSG_CHANGE_TO_DEL_PAGE:
					setTag( sender );
					if( page_index <= mHomePage )
					{
						if( mHomePage > 0 )
						{
							mHomePage--;
							DefaultLayout.getInstance().saveHomePage( mHomePage );
						}
					}
					// SendMsgToAndroid.sendRemoveWorkspaceCellMsg(page_index - 1);
					// setTag(sender.getTag());
					return viewParent.onCtrlEvent( this , MSG_CHANGE_TO_DEL_PAGE );
			}
		}
		if( sender instanceof WorkspaceEditView )
		{
			switch( event_id )
			{
				case WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE:
					if( getPageNum() >= 2 + DefaultLayout.default_workspace_pagecount_max )
					{
						// SendMsgToAndroid.sendOurToastMsg("Pages num is Maximum");
						SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.page_num_over_maximum ) );
						return true;
					}
					setTag( sender.getTag() );
					CellLayout3D cell = new CellLayout3D( "celllayout" );
					WorkspaceEditView tempView = (WorkspaceEditView)sender;
					Log.v( "test123" , "w3D add page begin" );
					if( tempView.isFirstPage() == true )
					{
						addPage( 1 , cell );
						mHomePage++;
						DefaultLayout.getInstance().saveHomePage( mHomePage );
						// SendMsgToAndroid.sendAddWorkspaceCellMsg(-2);
						Log.v( "test123" , "W3D first add page end" );
					}
					else
					{
						addPage( getPageNum() - 1 , cell );
						// SendMsgToAndroid.sendAddWorkspaceCellMsg(-1);
						Log.v( "test123" , "W3D last add page end" );
					}
					setFactorAndOrigY( tempView.scaleX , tempView.originY , cell );
					return viewParent.onCtrlEvent( this , MSG_CHANGE_TO_APPEND_PAGE );
			}
		}
		if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			switch( event_id )
			{
				case Icon3D.MSG_ICON_CLICK:
					// if (icon.getParent() instanceof CellLayout3D)
					// icon.bringToFront();
					setTag( icon );
					cur_icon = icon;
					return viewParent.onCtrlEvent( sender , event_id );
				case Icon3D.MSG_ICON_LONGCLICK:
					if( dragObjects.size() == 0 )
					{
						dragObjects.add( icon );
					}
					for( View3D view : dragObjects )
					{
						if( view instanceof Icon3D )
						{
							Icon3D icon1 = (Icon3D)view;
							icon1.hideSelectedIcon();
						}
					}
					if( !dragObjects.contains( icon ) )
					{
						dragObjects.clear();
						dragObjects.add( icon );
					}
					// teapotXu add start for icon3D's double-click optimization
					this.icon_select_mode = false;
					updateAllIconsSelectStateInWorkspace();
					// teapotXu add end
					releaseFocus();
					this.setTag( icon.getTag() );
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					isDragFromIcon3D = true;
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Icon3D.MSG_ICON_SELECTED:
					dragObjects.add( icon );
					// teapotXu add start for icon3D's double-click optimization
					if( dragObjects.size() == 1 )
					{
						icon_select_mode = true;
						updateAllIconsSelectStateInWorkspace();
						SendMsgToAndroid.sendCircleToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.multi_choice_mode_enter ) );
					}
					// teapotXu add end
					return true;
				case Icon3D.MSG_ICON_UNSELECTED:
					dragObjects.remove( icon );
					// teapotXu add start for icon3D's double-click optimization
					if( dragObjects.size() <= 0 )
					{
						icon_select_mode = false;
						updateAllIconsSelectStateInWorkspace();
					}
					// teapotXu add end
					return true;
			}
		}
		else if( sender instanceof WidgetView )
		{
			switch( event_id )
			{
				case WidgetView.MSG_WIDGETVIEW_LONGCLICK:
					WidgetView widget = (WidgetView)sender;
					if( dragObjects.size() > 0 )
					{
						dragObjects.clear();
					}
					dragObjects.add( widget );
					releaseFocus();
					this.setTag( widget.getTag() );
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
			return viewParent.onCtrlEvent( sender , event_id );
		}
		else if( sender instanceof Widget )
		{
			switch( event_id )
			{
				case Widget.MSG_WIDGET_LONGCLICK:
					Widget widget = (Widget)sender;
					if( dragObjects.size() > 0 )
					{
						for( View3D view : dragObjects )
						{
							if( view instanceof Icon3D )
							{
								Icon3D icon1 = (Icon3D)view;
								icon1.hideSelectedIcon();
							}
						}
						dragObjects.clear();
					}
					dragObjects.add( widget );
					this.icon_select_mode = false;
					updateAllIconsSelectStateInWorkspace();
					releaseFocus();
					Rect rect = (Rect)widget.getTag();
					DragLayer3D.dragStartX = rect.right;
					DragLayer3D.dragStartY = rect.bottom;
					this.setTag( new Vector2( rect.left , rect.top ) );
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
			return viewParent.onCtrlEvent( sender , event_id );
		}
		else if( sender instanceof Widget3D )
		{
			Widget3D widget = (Widget3D)sender;
			switch( event_id )
			{
				case Widget3D.MSG_Widget3D_LONGCLICK:
					if( !Workspace3D.getInstance().getDragList().isEmpty() )
					{
						for( View3D view : dragObjects )
						{
							if( view instanceof Icon3D )
							{
								Icon3D icon1 = (Icon3D)view;
								icon1.hideSelectedIcon();
							}
						}
						dragObjects.clear();
					}
					this.icon_select_mode = false;
					updateAllIconsSelectStateInWorkspace();
					if( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode )
					{
						return true;
					}
					if( widget != null )
					{
						dragObjects.add( widget );
						this.setTag( widget.getTag() );
					}
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Widget3D.MSG_Widget3D_LONGCLICK_FOR_WEATHERCLOCK:
					return viewParent.onCtrlEvent( this , MSG_LONGCLICK );
			}
		}
		else if( sender instanceof FolderIcon3D )
		{
			FolderIcon3D foldericon = (FolderIcon3D)sender;
			switch( event_id )
			{
				case FolderIcon3D.MSG_FOLDERICON3D_LONGCLICK:
					if( !Workspace3D.getInstance().getDragList().isEmpty() )
					{
						for( View3D view : dragObjects )
						{
							if( view instanceof Icon3D )
							{
								Icon3D icon1 = (Icon3D)view;
								icon1.hideSelectedIcon();
							}
						}
						dragObjects.clear();
					}
					this.icon_select_mode = false;
					updateAllIconsSelectStateInWorkspace();
					if( dragObjects.size() == 0 )
					{
						dragObjects.add( foldericon );
					}
					this.setTag( foldericon.getTag() );
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		else if( sender instanceof circlePopWnd3D )
		{
			delCircleSomehing.dealEvent_circlePopWnd3D( event_id );
			return true;
		}
		else if( sender instanceof RecentAppHolder )
		{
			switch( event_id )
			{
				case RecentAppHolder.RECENT_MSG_DESTORY:
					iLoongLauncher.getInstance().d3dListener.getDragLayer().removeDropTarget( mRecentApplications.mRecentAppPage );
					mRecentApplications.remove();
					mRecentApplications = null;
					mScrollGestureAction = false;
					isTouchFromWorkspace = false;
					return true;
			}
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			if( DefaultLayout.enable_effect_preview && !mPreviewFirst )
			{
				hasDownInEditMode = false;
				initView();
			}
		}
		if( dropAnimating && source instanceof Tween )
		{
			dropAnimating = false;
			// if(DefaultLayout.enable_workspace_miui_edit_mode &&
			// Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode){
			// }else
			// {
			if( xScale == 0 && mVelocityX == 0 && !circleSomething3D.isVisible() )
				SendMsgToAndroid.sendShowWorkspaceMsg();
			// }
			// if (xScale == 0 && mVelocityX == 0
			// && !circleSomething3D.isVisible())
			// SendMsgToAndroid.sendShowWorkspaceMsg();
			Object target = ( (Tween)source ).getTarget();
			if( target instanceof Widget2DShortcut )
			{
				this.setTag( target );
				viewParent.onCtrlEvent( this , MSG_ADD_WIDGET );
				( (View3D)target ).remove();
			}
		}
		// xiatian add start //EffectPreview
		else if( ( DefaultLayout.enable_effect_preview ) && ( type == TweenCallback.COMPLETE && source == tween ) )
		{
			if( ( mWorkspaceEffectPreview.isVisible() ) && ( !mEffectPreviewTips3D.isVisible() ) )
			{
				mEffectPreviewTips3D.show();
			}
		}
		else if( ( DefaultLayout.enable_effect_preview ) && ( type == TweenCallback.COMPLETE && source == mPreviewTween ) )
		{
			if( !mPreviewFirst )
			{
				if( ( mWorkspaceEffectPreview.isVisible() ) && ( !mEffectPreviewTips3D.isVisible() ) )
				{
					mEffectPreviewTips3D.show();
				}
			}
		}
		// xiatian add end
		else if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( type == TweenCallback.COMPLETE && source == edit_mode_click_2_add_our_widget ) )
		{
			Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
			edit_mode_click_2_add_our_widget = null;
		}
		super.onEvent( type , source );
	}
	
	public static void checkBoundary(
			View3D target )
	{
		ViewGroup3D parent = target.getParent();
		if( parent != null )
		{
			if( target.x < 0 )
				target.x = 0;
			if( target.y < 0 )
				target.y = 0;
			if( target.x + target.width > parent.width )
				target.x = parent.width - target.width;
			if( target.y + target.height > parent.height )
				target.y = parent.height - target.height;
		}
		else
		{
			Log.e( "checkBoundary" , "Can not check boundary whose parent is null :" + target );
		}
	}
	
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
		if( Workspace3D.NEW_CELLLAYOU )
		{
			CellLayout3D group = (CellLayout3D)getChildAt( this.getCurrentScreen() );
			group.onDropCompleted( target , success );
		}
		else if( success )
		{
			checkBoundary( target );
		}
		if( DefaultLayout.keypad_event_of_focus )
		{
			CellLayout3D cellLayout = getCurrentCellLayout();
			if( cellLayout != null )
				cellLayout.resetCurrFocus();
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( iLoongLauncher.isShowNews )
		{
			return true;
		}
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) )
		{
			return true;
		}
		if( ( DefaultLayout.enable_takein_workspace_by_longclick && isHideAll ) || ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		if( super.onLongClick( x , y ) )
			return true;
		this.setTag( new Vector2( x , y ) );
		workspaceOnLong = true;
		return viewParent.onCtrlEvent( this , MSG_LONGCLICK );
	}
	
	@Override
	public void setActionListener()
	{
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_DESKTOP_SETTINGS , this );
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) )
		{
			setEffectType( bundle.getInt( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) );
		}
		if( bundle.containsKey( "circled" ) )
		{
			circleAble = bundle.getInt( "circled" );
		}
	}
	
	@Override
	public void onDegreeChanged()
	{
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && getPageNum() < 9 && ( workspaceOnLong || workspacetonromal ) )
		{
		}
		else
		{
			if( viewParent != null && this.isVisible() )
				viewParent.onCtrlEvent( this , Root3D.MSG_SET_WALLPAPER_OFFSET );
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			y = this.getUser();
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			//			if( editModeBg == null )
			//			{
			//				editModeBg = new NinePatch( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/desktopEdit/bg.png" ) , true ) , 1 , 1 , 1 , 1 );
			//			}
			batch.setColor( 1.0f , 1.0f , 1.0f , 1.0f );
			//editModeBg.draw( batch , 0 , 0 , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		}
		if( mediaBg != null )
		{
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha * mediaBgAlpha * DefaultLayout.media_view_dispose_bg_alpha );
			mediaBg.draw( batch , 0 , 0 , width , height );
			if( mediaBg.getPatches() != null && mediaBg.getPatches().length > 0 && mediaBg.getPatches()[0].getTexture() != null && ( (BitmapTexture)( mediaBg.getPatches()[0].getTexture() ) )
					.isDisposed() )
			{
				( (BitmapTexture)( mediaBg.getPatches()[0].getTexture() ) ).dynamicLoad();
			}
		}
		if( iLoongLauncher.getInstance().popResult > CIRCLE_POP_NONE_ACTION )
		{
			delCircleSomehing.Process_delALL( iLoongLauncher.getInstance().popResult );
			if( iLoongLauncher.getInstance().popResult == CIRCLE_POP_ACK_ACTION )
			{
				ArrayList<View3D> listToDel = (ArrayList<View3D>)this.getTag();
				View3D temp;
				CellLayout3D group = (CellLayout3D)getChildAt( this.getCurrentScreen() );
				for( int i = 0 ; i < listToDel.size() ; i++ )
				{
					temp = listToDel.get( i );
					group.removeView( temp );
				}
				viewParent.onCtrlEvent( this , MSG_DEL_POP_ALL );
			}
			iLoongLauncher.getInstance().popResult = CIRCLE_POP_NONE_ACTION;
		}
		super.draw( batch , parentAlpha );
		if( DefaultLayout.enable_new_particle )
		{
			newDrawParticle( batch );
		}
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		if( DefaultLayout.show_music_page )
		{
			ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
			if( cur_view instanceof MediaView3D )
			{
				return true;
			}
		}
		// xujin
		if( DefaultLayout.enable_camera )
		{
			ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
			if( cur_view instanceof MediaView3D )
			{
				return true;
			}
		}
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) )
		{
			return true;
		}
		// xiatian add end
		// ZJP
		if( ( DefaultLayout.enable_takein_workspace_by_longclick && Workspace3D.isHideAll ) || ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		Root3D root = (Root3D)this.viewParent;
		if( root.isPageContainerVisible() || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
		{
			return true;
		}
		// Log.d("test12345", "multiTouch2 workspaceOnlong ="+workspaceOnLong+
		// " moving="+moving);
		if( circleAble == 0 && workspaceOnLong == false )
		{
			float zoomDst = initialFirstPointer.dst( initialSecondPointer ) - firstPointer.dst( secondPointer );
			Log.d( "testdrag" , "zoomDst=" + zoomDst + initialFirstPointer + initialSecondPointer + firstPointer + secondPointer );
			if( zoomDst >= 0 && zoomDst < ZOOM_DISTANCE )
			{
				return true;
			}
			if( zoomDst >= ZOOM_DISTANCE )
			{
				// when workspace edtiMode's anim playing, donot show pageEdit
				Root3D mRoot3D = iLoongLauncher.getInstance().getD3dListener().getRoot();
				if( mRoot3D != null && mRoot3D.getWorkspaceEditModeAnimStatus() )
					return false;
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "DoubleRefersToPageEdit" );
				viewParent.onCtrlEvent( this , MSG_PAGE_SHOW_EDIT );
				iLoongLauncher.getInstance().cleaWidgetStatus( page_index );
				Gdx.graphics.requestRendering();
				return true;
			}
		}
		if( circleAble == 1 && workspaceOnLong == false )
		{
			float zoomDst = initialFirstPointer.dst( initialSecondPointer ) - firstPointer.dst( secondPointer );
			Log.d( "testdrag" , "zoomDst=" + zoomDst + initialFirstPointer + initialSecondPointer + firstPointer + secondPointer );
			if( zoomDst >= 0 && zoomDst < ZOOM_DISTANCE )
			{
				return true;
			}
			if( zoomDst >= ZOOM_DISTANCE )
			{
				// when workspace edtiMode's anim playing, donot show pageEdit
				Root3D mRoot3D = iLoongLauncher.getInstance().getD3dListener().getRoot();
				if( mRoot3D != null && mRoot3D.getWorkspaceEditModeAnimStatus() )
					return true;
				viewParent.onCtrlEvent( this , MSG_PAGE_SHOW_EDIT );
				iLoongLauncher.getInstance().cleaWidgetStatus( page_index );
				Gdx.graphics.requestRendering();
				return true;
			}
			else
			{
				CellLayout3D cellLayout = getCurrentCellLayout();
				if( cellLayout != null && cellLayout.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer ) == true )
				{
					mCircle_State = CIRCLE_FOLDER_MERGE;
				}
				else
				{
					mCircle_State = CIRCLE_POPUP_WND;
				}
				/* 如果最顶上是圈选VIEW，就允许消息分发，否则就直接返回 */
				circleSomething3D.show();
				// ClingManager.getInstance().cancelCircleCling();
				if( getChildAt( this.getChildCount() - 1 ).name == "CircleSomething3D" )
				{
					return super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
				}
			}
		}
		return true;
	}
	
	@Override
	public void initView()
	{
		Root3D root = (Root3D)this.viewParent;
		if( root != null && root.isPageContainerVisible() )
		{
			return;
		}
		// teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode && this instanceof Workspace3D )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				View3D view = null;
				for( int i = 0 ; i < view_list.size() ; i++ )
				{
					view = view_list.get( i );
					if( i == page_index - 1 )
					{
						view.setPosition( -view.width , 0 );
					}
					else if( i == page_index + 1 )
					{
						view.setPosition( view.width , 0 );
					}
					else
					{
						view.setPosition( 0 , 0 );
					}
					if( hasDownInEditMode )
					{
						view.setPosition( 0 , 0 );
					}
					view.setRotationZ( 0 );
					view.setScale( 1 , 1 );
					if( !( Workspace3D.isRecentAppVisible() ) )
						view.setColor( initColor );
					view.setOrigin( view.width / 2 , /* pageOrigy */
							view.height / 2 );
					view.setZ( 0 );
					view.setOriginZ( 0 );
					if( i != page_index && i != page_index - 1 && i != page_index + 1 )
						view.hide();
					else
						view.show();
					if( hasDownInEditMode && ( i == page_index - 1 || i == page_index + 1 ) )
					{
						view.hide();
					}
					// 还原icon
					if( view instanceof ViewGroup3D )
					{
						resetIcons( (ViewGroup3D)view );
					}
				}
				this.moving = false;
				return;
			}
		}
		// teapotXu add end
		super.initView();
	}
	
	private void resetIcons(
			ViewGroup3D group )
	{
		int size = group.getChildCount();
		View3D iconTem = null;
		for( int j = 0 ; j < size ; j++ )
		{
			iconTem = group.getChildAt( j );
			iconTem.setRotationZ( 0 );
			iconTem.setScale( 1.0f , 1.0f );
			if( !( Workspace3D.isRecentAppVisible() ) )
				iconTem.setColor( initColor );
			iconTem.setOrigin( iconTem.width / 2 , iconTem.height / 2 );
			iconTem.setOriginZ( 0 );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iLoong.launcher.Desktop3D.NPageBase#setCurrentPage(int)
	 */
	@Override
	public void setCurrentPage(
			int index )
	{
		removeIconGroupAndPopView();
		super.setCurrentPage( index );
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		if( Workspace3D.NEW_CELLLAYOU )
		{
			View3D childView = getChildAt( this.getCurrentScreen() );
			if( childView instanceof CellLayout3D )
			{
				CellLayout3D group = (CellLayout3D)childView;
				if( list.size() > 0 )
				{
					View3D view = list.get( 0 );
					View3D viewParent = view.getParent();
					x = (int)( viewParent.x + viewParent.width / 2 );
					y = (int)( viewParent.y + viewParent.height / 2 );
					x = width / 2 + ( x - width / 2 ) / scaleX;
					y = height / 2 + ( y - height / 2 ) / scaleY;
					return group.onDropOver( list , x , y );
				}
				else
					return false;
			}
			else
			{
				return false;
			}
		}
		else
			return false;
	}
	
	public void onDragOverLeave()
	{
		CellLayout3D group = getCurrentCellLayout();
		if( group != null )
		{
			group.onDropLeave();
		}
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		return dragObjects;
	}
	
	@Override
	public void setScaleZ(
			float f )
	{
		for( View3D child : children )
			if( child instanceof CellLayout3D )
			{
				child.setScaleZ( f );
			}
	}
	
	public void setWorkspaceEffectPreview3D(
			View3D v )
	{
		this.mWorkspaceEffectPreview = (EffectPreview3D)v;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) || ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		return super.onDoubleClick( x , y );
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
	
	public static NinePatch cellLayoutBg = null;
	
	public void setFactorAndOrigY(
			float scaleFactor ,
			float setOrigY ,
			CellLayout3D newCell )
	{
		newCell.setScale( scaleFactor , scaleFactor );
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
		{
			newCell.setCellBackgroud( null );
			newCell.setOrigin( newCell.width / 2 , this.height / 2 );
		}
		else
		{
			newCell.setCellBackgroud( cellLayoutBg );
			newCell.setOrigin( newCell.width / 2 , setOrigY );
		}
	}
	
	public void setFactorAndOrigY(
			int setOrigY )
	{
		setNinePatch( setOrigY );
	}
	
	public void setEditModeOrigY(
			CellLayout3D cell )
	{
		cell.setCellBackgroud( cellLayoutBg );
		cell.setOrigin( cell.width / 2 , Utils3D.getScreenHeight() - R3D.workspace_cell_height );
	}
	
	private void setNinePatch(
			float pageOrigy )
	{
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			if( getChildAt( i ) instanceof CellLayout3D )
			{
				CellLayout3D temp = ( (CellLayout3D)getChildAt( i ) );
				// temp.setScale(scaleFactor, scaleFactor);
				if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
				{
					temp.setCellBackgroud( cellLayoutBg );
					temp.setOrigin( temp.width / 2 , this.height / 2 );
				}
				else
				{
					temp.setCellBackgroud( cellLayoutBg );
					temp.setOrigin( temp.width / 2 , pageOrigy );
				}
			}
		}
	}
	
	//
	public boolean WorkspaceNeedDragAutoEffect(
			int showBorder )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			if( !( this.getCurrentView() instanceof CellLayout3D ) )
			{
				if( ( showBorder == -1 && this.getCurrentPage() == 0 ) // move to
																		// left
																		// &&
																		// the
																		// first
																		// view
						|| ( showBorder == 1 && this.getCurrentPage() == this.getPageNum() - 1 ) // move to right && the laset
																									// view
				)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public View3D findViewNew(
			String name )
	{
		View3D actor = null;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			actor = getChildAt( i );
			if( actor instanceof WorkspaceEditView )
			{
				if( actor.name.equals( name ) )
				{
					return actor;
				}
			}
		}
		return null;
	}
	
	public View3D findViewMedia(
			String name )
	{
		View3D actor = null;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			actor = getChildAt( i );
			if( actor instanceof MediaView3D )
			{
				if( actor.name.equals( name ) )
				{
					return actor;
				}
			}
		}
		return null;
	}
	
	public boolean findNewsPage()
	{
		View3D actor = null;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			actor = getChildAt( i );
			if( actor instanceof CellLayout3D )
			{
				if( actor.name.equals( "newsView" ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeMusicView()
	{
		if( findViewMedia( "musicView" ) != null )
		{
			if( findNewsPage() )
			{
				SendMsgToAndroid.sendRemoveWorkspaceCellMsg( getPageNum() - 2 );
				removePage( musicView );
				newsCellLayout3D.setScreen( getPageNum() - 1 );
			}
			else
			{
				SendMsgToAndroid.sendRemoveWorkspaceCellMsg( getPageNum() - 1 );
				removePage( musicView );
			}
		}
		if( page_index > getPageNum() - 1 )
		{
			page_index = getPageNum() - 1;
		}
		PageIndicator3D pageIndicator = iLoongLauncher.getInstance().d3dListener.getRoot().getPageIndicator();
		pageIndicator.setPageNum( getPageNum() );
		if( pageIndicator.getIndex() > getPageNum() - 1 )
		{
			pageIndicator.setCurrentPage( getPageNum() - 1 );
		}
	}
	
	public void removeMusicViewAndRelease()
	{
		removeMusicView();
		if( musicView != null )
		{
			View3D view = musicView.getChildAt( 0 );
			if( view != null )
			{
				view.remove();
				if( view instanceof Widget3D )
				{
					Widget3D widget3D = (Widget3D)view;
					widget3D.releaseFocus();
					widget3D.onDelete();
				}
			}
			musicView = null;
		}
	}
	
	/**
	 * xujin 移除camera页，并释放
	 */
	public void removeCameraViewAndRelease()
	{
		removeCameraView();
		if( cameraView != null )
		{
			View3D view = cameraView.getChildAt( 0 );
			if( view != null )
			{
				view.remove();
				if( view instanceof Widget3D )
				{
					// 手动删除相机widget
					Widget3D widget3D = (Widget3D)view;
					widget3D.releaseFocus();
					widget3D.onDelete();
				}
			}
			cameraView = null;
		}
	}
	
	/**
	 * 移除相机页
	 */
	public void removeCameraView()
	{
		if( findViewMedia( "cameraView" ) != null )
		{
			int mediaViewCount = 0;
			if( findNewsPage() )
			{
				mediaViewCount++;
			}
			if( findViewMedia( "musicView" ) != null )
			{
				mediaViewCount++;
			}
			SendMsgToAndroid.sendRemoveWorkspaceCellMsg( getPageNum() - mediaViewCount - 1 );
			removePage( cameraView );
			// 存在newsPage
			if( findNewsPage() )
			{
				newsCellLayout3D.setScreen( getPageNum() - 1 );
			}
			// 存在musicPage
			if( findViewMedia( "musicView" ) != null )
			{
				if( findNewsPage() )
				{
					musicView.setScreen( getPageNum() - 2 );
				}
				else
				{
					musicView.setScreen( getPageNum() - 1 );
				}
			}
		}
		if( page_index > getPageNum() - 1 )
		{
			page_index = getPageNum() - 1;
		}
		PageIndicator3D pageIndicator = iLoongLauncher.getInstance().d3dListener.getRoot().getPageIndicator();
		pageIndicator.setPageNum( getPageNum() );
		if( pageIndicator.getIndex() > getPageNum() - 1 )
		{
			pageIndicator.setCurrentPage( getPageNum() - 1 );
		}
	}
	
	public void removePage(
			View3D view )
	{
		Log.v( "NPageBase" , "removePage" );
		if( view != null )
		{
			view_list.remove( view );
			removeView( view );
		}
	}
	
	public MediaView3D getMusicView()
	{
		return musicView;
	}
	
	// xujin
	public MediaView3D getCameraView()
	{
		return cameraView;
	}
	
	public void addMusicView()
	{
		if( !DefaultLayout.show_music_page )
		{
			return;
		}
		if( findViewMedia( "musicView" ) != null )
		{
			return;
		}
		if( musicView == null )
		{
			musicView = new MediaView3D( "musicView" , "com.iLoong.OppoMusic" , "com.iLoong.OppoMusic.iLoongMusic" , iLoongLauncher.getInstance().getD3dListener().getRoot().getMusicSeat() );
		}
		int mediaPageCount = 0;
		if( findNewsPage() )
		{
			newsCellLayout3D.setScreen( getPageNum() );
			mediaPageCount++;
		}
		if( DefaultLayout.put_music_before_camera )
		{
			if( findViewMedia( "cameraView" ) != null )
			{
				cameraView.setScreen( getPageNum() - mediaPageCount );
				mediaPageCount++;
			}
		}
		musicView.setScreen( getPageNum() - mediaPageCount );
		addPage( getPageNum() - mediaPageCount , musicView );
		SendMsgToAndroid.sendAddWorkspaceCellMsg( getPageNum() - mediaPageCount - 1 );
		PageIndicator3D pageIndicator = iLoongLauncher.getInstance().d3dListener.getRoot().getPageIndicator();
		pageIndicator.setPageNum( getPageNum() );
	}
	
	// xujin
	/**
	 * 添加相机页
	 */
	public void addCameraView()
	{
		if( !DefaultLayout.enable_camera )
		{
			return;
		}
		if( findViewMedia( "cameraView" ) != null )
		{
			return;
		}
		if( cameraView == null )
		{
			cameraView = new MediaView3D( "cameraView" , "com.cooee.camerawidget" , "com.cooee.camerawidget.MainActivity" , iLoongLauncher.getInstance().getD3dListener().getRoot().getCameraSeat() );
		}
		int mediaPageCount = 0;
		if( findNewsPage() )
		{
			newsCellLayout3D.setScreen( getPageNum() );
			mediaPageCount++;
		}
		if( !DefaultLayout.put_music_before_camera )
		{
			if( findViewMedia( "musicView" ) != null )
			{
				musicView.setScreen( getPageNum() - mediaPageCount );
				mediaPageCount++;
			}
		}
		cameraView.setScreen( getPageNum() - mediaPageCount );
		addPage( getPageNum() - mediaPageCount , cameraView );
		SendMsgToAndroid.sendAddWorkspaceCellMsg( getPageNum() - mediaPageCount - 1 );
		PageIndicator3D pageIndicator = iLoongLauncher.getInstance().d3dListener.getRoot().getPageIndicator();
		pageIndicator.setPageNum( getPageNum() );
	}
	
	public boolean curIsMusicView()
	{
		if( !DefaultLayout.show_music_page )
		{
			return false;
		}
		if( view_list == null || ( view_list != null && view_list.size() == 0 ) )
		{
			return false;
		}
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		if( cur_view instanceof MediaView3D && cur_view.name.equals( "musicView" ) )
		{
			return true;
		}
		return false;
	}
	
	public circlePopWnd3D getCirclePopWin3d()
	{
		View3D findView = this.findView( "circlePopWnd3D" );
		if( findView != null && findView instanceof circlePopWnd3D )
		{
			return (circlePopWnd3D)findView;
		}
		else
			return null;
	}
	
	// xujin
	public boolean curIsCameraView()
	{
		if( !DefaultLayout.enable_camera )
		{
			return false;
		}
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		if( cur_view instanceof MediaView3D && cur_view.name.equals( "cameraView" ) )
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断某页是否为音乐页
	 * 
	 * @param pageIndex
	 *            页码
	 * @return 如果是音乐页返回true，否则返回false
	 */
	public boolean isMusicView(
			int pageIndex )
	{
		if( !DefaultLayout.show_music_page )
		{
			return false;
		}
		ViewGroup3D view = (ViewGroup3D)view_list.get( pageIndex );
		if( view instanceof MediaView3D && view.name.equals( "musicView" ) )
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断某页是否为相机页
	 * 
	 * @param pageIndex
	 *            页码
	 * @return 如果是相机页返回true，否则返回false
	 */
	public boolean isCameraView(
			int pageIndex )
	{
		if( !DefaultLayout.enable_camera )
		{
			return false;
		}
		ViewGroup3D view = (ViewGroup3D)view_list.get( pageIndex );
		if( view instanceof MediaView3D && view.name.equals( "cameraView" ) )
		{
			return true;
		}
		return false;
	}
	
	public void addEditModeCellLayout(
			boolean isFistAddpage )
	{
		if( isFistAddpage == true )
		{
			for( int j = 0 ; j < getPageNum() ; j++ )
			{
				ViewGroup3D cell = (ViewGroup3D)getChildAt( j );
				if( cell instanceof CellLayout3D )
				{
					for( int k = 0 ; k < cell.getChildCount() ; k++ )
					{
						View3D v = cell.getChildAt( k );
						if( v instanceof IconBase3D )
						{
							ItemInfo info = ( (IconBase3D)v ).getItemInfo();
							// 因为该screen值将在addOrMoveDB中减1
							if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
							{
								info.screen += 1;
							}
							info.screen = info.screen + 1;
							Root3D.addOrMoveDB( info );
							// info.screen = info.screen + 1;
							// Log.v("test123",
							// "del page begin index="+info.screen
							// + " title="+info.title);
							// Root3D.addOrMoveDB(info);
						}
					}
				}
			}
		}
		SendMsgToAndroid.sendAddWorkspaceCellMsg( isFistAddpage == true ? -2 : -1 );
		// iLoongApplication app = (iLoongApplication) iLoongLauncher
		// .getInstance().getApplication();
		// app.getLauncherProvider().batchUpdateItem(0, getPageNum() - 1, true);
	}
	
	public void removeEditModeCellLayout(
			int i )
	{
		Log.e( "root3D" , "removeEditModeCellLayout" );
		for( int j = i ; j < getPageNum() ; j++ )
		{
			ViewGroup3D cell = (ViewGroup3D)getChildAt( j );
			if( cell instanceof CellLayout3D )
			{
				for( int k = 0 ; k < cell.getChildCount() ; k++ )
				{
					View3D v = cell.getChildAt( k );
					if( v instanceof IconBase3D )
					{
						ItemInfo info = ( (IconBase3D)v ).getItemInfo();
						// 因为该screen值将在addOrMoveDB中减1
						// info.screen = info.screen - 1;
						Root3D.addOrMoveDB( info );
						// if (info.screen > 0) {
						// info.screen = info.screen - 1;
						// }
						// info.screen = j;
						// Root3D.addOrMoveDB(info);
					}
				}
			}
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
		{
			SendMsgToAndroid.sendRemoveWorkspaceCellMsg( i - 1 );
		}
		else
		{
			SendMsgToAndroid.sendRemoveWorkspaceCellMsg( i );
		}
		// iLoongApplication app = (iLoongApplication) iLoongLauncher
		// .getInstance().getApplication();
		// app.getLauncherProvider().batchUpdateItem(i, getPageNum() - 1,
		// false);
	}
	
	public static final int DROPTYPE_SYS_WIDGET_INTENT = 0;
	public static final int DROPTYPE_SYS_SHORTCUT_INTENT = 1;
	public static final int DROPTYPE_SYS_WIDGET_CMPNAME = 2;
	public static final int DROPTYPE_SYS_SHORTCUT_CMPNAME = 3;
	public static final int DROPTYPE_3D_WIDGET_RESOLVEINFO = 4;
	public int delay_drop_type = -1;
	private Intent drop_widget_intent = null;
	private ComponentName widget_cmpName = null;
	
	public void saveDropWidgetsInfo(
			int add_type ,
			Intent drop_intent ,
			ComponentName drop_cmpName ,
			ResolveInfo drop_resolveInfo )
	{
		if( add_type < 0 )
			return;
		this.delay_drop_type = -1;
		this.drop_widget_intent = null;
		switch( add_type )
		{
			case DROPTYPE_SYS_WIDGET_INTENT:
			case DROPTYPE_SYS_SHORTCUT_INTENT:
				if( drop_intent == null )
					return;
				delay_drop_type = add_type;
				this.drop_widget_intent = new Intent( drop_intent );
				break;
			case DROPTYPE_SYS_WIDGET_CMPNAME:
			case DROPTYPE_SYS_SHORTCUT_CMPNAME:
				if( widget_cmpName == null )
					return;
				delay_drop_type = add_type;
				break;
			case DROPTYPE_3D_WIDGET_RESOLVEINFO:
				if( drop_resolveInfo == null )
					return;
				delay_drop_type = DROPTYPE_3D_WIDGET_RESOLVEINFO;
				break;
		}
	}
	
	public boolean b_continue_add_widget_shortcut()
	{
		return delay_drop_type < 0 ? false : true;
	}
	
	public void continue_adding_widget_shortcut()
	{
		switch( delay_drop_type )
		{
			case DROPTYPE_SYS_WIDGET_INTENT:
			case DROPTYPE_SYS_SHORTCUT_INTENT:
				if( this.drop_widget_intent == null )
					return;
				if( delay_drop_type == DROPTYPE_SYS_WIDGET_INTENT )
					SendMsgToAndroid.PickWidgetAndShortCutFromAllApp( drop_widget_intent , 0 );
				else
					SendMsgToAndroid.PickWidgetAndShortCutFromAllApp( drop_widget_intent , 1 );
				delay_drop_type = -1;
				this.drop_widget_intent = null;
				break;
			case DROPTYPE_SYS_WIDGET_CMPNAME:
			case DROPTYPE_SYS_SHORTCUT_CMPNAME:
				if( this.widget_cmpName == null )
					return;
				if( delay_drop_type == DROPTYPE_SYS_WIDGET_CMPNAME )
				{
					SendMsgToAndroid.addWidgetFromAllApp( widget_cmpName , (int)0 , (int)0 );
				}
				else
				{
					SendMsgToAndroid.addShortcutFromAllApp( widget_cmpName , 0 , 0 );
				}
				break;
			case DROPTYPE_3D_WIDGET_RESOLVEINFO:
				// if(resolveInfo == null)
				// return;
				break;
		}
	}
	
	// teapotXu add end
	public float getXScale()
	{
		return xScale;
	}
	
	private void checkScrollDirection(
			float x ,
			float y )
	{
		if( xScale != 0 )
		{
			return;
		}
		if( mCheckScrollDirection )
		{
			if( Math.abs( y - mCurDownY ) > Math.abs( x - mCurDownX ) )
			{
				mScrollVertical = true;
				mCheckScrollDirection = false;
			}
			else if( Math.abs( x - mCurDownX ) > Math.abs( y - mCurDownY ) )
			{
				mScrollVertical = false;
				mCheckScrollDirection = false;
			}
		}
	}
	
	private void scrollGestureEvent(
			float x ,
			float y ,
			float deltaY )
	{
		if( mScrollGestureAction || Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return;
		}
		if( mScrollVertical )
		{
			//			if(!isTouchFromWorkspace){
			//				return ;
			//			}
			if( mCurDownY > R3D.Workspace_celllayout_bottompadding + R3D.workspace_cell_height && !isRecentAppVisible() )
			{
				if( ( ( mCurDownY - y ) > R3D.workspace_cell_height ) && ( deltaY > 0 ) )
				{
					Utils3D.launchNotification();
					mScrollGestureAction = true;
				}
			}
			//if( mCurDownY > R3D.Workspace_celllayout_bottompadding )
			{
				if( ( ( y - mCurDownY ) > R3D.workspace_cell_height ) && ( deltaY < 0 ) )
				{
					showRecentApplications();
					mScrollGestureAction = true;
				}
			}
		}
	}
	
	public void showRecentApplications()
	{
		mScrollGestureAction = true;
		if( mRecentApplications != null )
		{
			return;
		}
		float startY = getCurrentCellLayout().getPaddingBottom();
		if( Utils3D.hasMeiZuSmartBar() )
		{
			startY += Utils3D.px2dip( iLoongLauncher.getInstance() , 100 );
		}
		mRecentApplications = new RecentAppHolder( "recentApplications" , startY );
		mRecentApplications.x = 0;
		mRecentApplications.y = 0;
		addView( mRecentApplications );
		iLoongLauncher.getInstance().d3dListener.getDragLayer().addDropTarget( mRecentApplications.mRecentAppPage );
		mRecentApplications.show();
		mRecentApplications.setViewList( getCurrentCellLayout().getAllViews() , getCurrentCellLayout() );
		if( DefaultLayout.enable_news )
		{
			if( DefaultLayout.show_newspage_with_handle )
			{
				Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
			}
		}
		MobclickAgent.onEvent( launcher , "DesktopRecentlyGesture" );
	}
	
	public static boolean isRecentAppVisible()
	{
		if( mRecentApplications != null && mRecentApplications.isVisible() )
		{
			return true;
		}
		else
			return false;
	}
}
