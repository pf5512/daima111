package com.iLoong.launcher.HotSeat3D;


import java.util.ArrayList;

import android.graphics.Bitmap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.desktopEdit.PageContainer;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotSeat3D extends ViewGroupOBJ3D implements DropTarget3D , DragSource3D
{
	
	public static final int MSG_START_DRAG = 0;
	public static final int TYPE_ICON = 0;
	public static final int TYPE_WIDGET = 1;
	public static final int MSG_ON_DROP = 2;
	public static final int MSG_LONGCLICK_INAPPLIST = 3;
	public static final int MSG_MAINGROUP_SCROLL_DOWN = 4;
	public static final int MSG_MAINGROUP_SCROLL_UP = 10;
	public static final int MSG_DOCKGROUP_SCROLL_UP = 5;
	public static final int MSG_VIEW_START_MAIN = 6;
	public static final int MSG_ADD_DRAGLAYER = 7;
	public static final int MSG_DOCKGROUP_SCROLL_DOWN = 8;
	public static final int MSG_DOCKGROUP_SCROLL_FLING = 9;
	public static final int MSG_MODEL_SCROLL_UP = 11;
	public static final int MSG_MODEL_SCROLL_DOWN = 12;
	public static final int MSG_SHOW_ADD_APP_VIEW = 13;
	public static final int MSG_SHOW_FOLDER_VIEW = 14;
	public static final int MSG_SHOW_ADD_WIDGET_VIEW = 15;
	public static final int MSG_SHOW_ADD_SHORTCUT_VIEW = 16;
	public static final int MSG_UPDATE_HOTGRIDVIEW_LAYOUT = 17;
	/* 动画管理部分变量定义 */
	private int obj_state;
	private Timeline animation_line = null;
	private Tween myTween;
	private static final int rot_to_back = 0;
	private static final int rot_to_front = 1;
	private static final int rot_to_down_effect = 2;
	private static final int rot_to_reset_effect = 3;
	private static final int model_to_reset_effect = 4;
	private static final int rot_to_out = 5;
	private static final int rot_to_in = 6;
	public static final int STATE_FRONT = 1;
	public static final int STATE_BACK = 2;
	public static final int STATE_ROTATE = 3;
	private boolean bRot_Effect = false;
	private static final int hide_dock = 0;
	private static final int show_dock = 1;
	/* 动画管理部分变量定义 */
	public static boolean menuOpened = false;
	public static boolean menuAnimalComplete = true;
	public static boolean isOnBackSide = false;;
	private View3D dragObj;
	private int type;
	private static HotSeatMainGroup mainGroup;
	public static HotObjLoader3D Model3DGroup;
	public static ImageView3D menuBg;
	public static HotDockGroup dockGroup;
	private IconCache iconCache;
	private Object tag2;
	private boolean bOutDragAnim = false;
	private boolean finishBind = false;
	public static Object lock = new Object();
	//翻转的方向,true 顺时针，false 逆时针
	private boolean mClockwise = false;
	//表示当前两块模型的上下顺序,true: objectUP 在上面开始往下翻
	private boolean frontToBack = false;
	private boolean modelUp = false;
	
	public HotSeat3D(
			String name )
	{
		super( name );
		width = Utils3D.getScreenWidth();
		height = R3D.hot_obj_height;
		x = 0;
		y = 0;
		setOrigin( width / 2 , height / 2 );
		type = TYPE_WIDGET;
		buildElements();
	}
	
	private void buildElements()
	{
		R3D.hot_obj_rot_deg = 180;
		menuBg = new ImageView3D( "menuBackground" , R3D.getTextureRegion( R3D.apphost_bg ) );
		menuBg.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		menuBg.setPosition( 0 , 0 );
		menuBg.hide();
		this.addView( menuBg );
		mainGroup = new HotSeatMainGroup( "HotSeatMainGroup" , (int)width , (int)R3D.hot_obj_height );
		Model3DGroup = new HotObjLoader3D( "Model3DGroup" , (int)width , R3D.hot_obj_height );
		//		Model3DGroup.y+=100;
		addView( Model3DGroup );
		Model3DGroup.HotObj_Loader();
		dockGroup = new HotDockGroup( "dockGroup" , (int)width , (int)R3D.hot_obj_height );
		//		dockGroup.y+=100;
		dockGroup.y -= 10;
		addView( dockGroup );
		setHot3DState( STATE_FRONT );
		// Model3DGroup.setOriginZ(-R3D.hot_obj_origin_z);
		//		Model3DGroup.setOriginZ(60f);
		Model3DGroup.setRotationVector( 1 , 0 , 0 );
		dockGroup.setRotationVector( 1 , 0 , 0 );
		// dockGroup.setOriginZ(-50f);
		//		dockGroup.setOriginZ(-R3D.hot_frontgrid_origin_z);
		// dockGroup.setZ(-31);
		mainGroup.setRotationVector( 1 , 0 , 0 );
		//		mainGroup.setOriginZ(-R3D.hot_obj_origin_z);
		//		mainGroup.y += 30;
		mainGroup.setOrigin( width / 2 , (float)( height / 2 * Math.sin( 15 * Math.PI / 180 ) ) );
		mainGroup.setOriginZ( -(float)( ( this.height / 2 ) * Math.cos( 15 * Math.PI / 180 ) ) );
		mainGroup.setRotation( R3D.hot_obj_rot_deg );
		//		mainGroup.y = -40;
		//		mainGroup.setZ(-120f);
		mainGroup.hide();
		mainGroup.color.a = 0;
		addView( mainGroup );
		R3D.hot_obj_rot_deg = 0;
	}
	
	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha)
	// {
	// super.draw(batch, parentAlpha);
	// }
	public void showDelay(
			float delay )
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		stopTween();
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setUserData( show_dock ).setCallback( this ).delay( delay );
	}
	
	public void show()
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		stopTween();
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setUserData( show_dock ).setCallback( this );
	}
	
	public void showNoAnim()
	{
		if( this.touchable == true )
		{
			return;
		}
		setPosition( 0 , 0 );
		super.show();
		stopTween();
	}
	
	public void hide()
	{
		if( this.touchable == false )
		{
			return;
		}
		this.touchable = false;
		stopTween();
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , 0 , -R3D.workspace_cell_height , 0 ).setUserData( hide_dock ).setCallback( this );
		// super.hide();
	}
	
	public boolean isIconListShow()
	{
		return mainGroup.isInShortcutList();
	}
	
	public ViewGroup3D getMainGroup()
	{
		return this.mainGroup;
	}
	
	public HotDockGroup getDockGroup()
	{
		return this.dockGroup;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( pointer == 1 )
		{
			return false;
		}
		else
		{
			return super.onTouchDown( x , y , pointer );
		}
	}
	
	public void onDragOverLeave()
	{
		dockGroup.onDragOverLeave();
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setWorkspace(
			View3D v )
	{
		if( dockGroup != null )
		{
			dockGroup.setWorkspace( v );
		}
		if( mainGroup != null )
		{
			mainGroup.setWorkspace( v );
		}
	}
	
	private void sortItemsByIndex(
			ArrayList<ItemInfo> items )
	{
		int i , j;
		int index1 = 0 , index2 = 0;
		ItemInfo tempInfo1 = null , tempInfo2 = null;
		int count = items.size();
		for( j = 0 ; j <= count - 1 ; j++ )
		{
			for( i = j ; i < count ; i++ )
			{
				tempInfo1 = items.get( j );
				tempInfo2 = items.get( i );
				if( tempInfo1.angle == TYPE_ICON && tempInfo1.angle == tempInfo2.angle )
				{
					index1 = tempInfo1.screen;
					index2 = tempInfo2.screen;
					if( index1 > index2 )
					{
						items.set( j , tempInfo2 );
						items.set( i , tempInfo1 );
					}
				}
			}
		}
	}
	
	public void bindShortItems(
			ArrayList<ItemInfo> items )
	{
		sortItemsByIndex( items );
		DefaultLayout.cometShortcutView.clear();
		for( ItemInfo item : items )
		{
			if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
			{
				ShortcutInfo info = (ShortcutInfo)item;
				Icon3D icon = null;
				// Icon3D icon = new Icon3D(info.title.toString(),
				// info.getIcon(iconCache), info.title.toString());
				// Utils3D.changeTextureRegion(icon, Utils3D.getIconBmpHeight(),
				// true);
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
					icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() );
					Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
				}
				else
				{
					icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
				}
				//				Utils3D.changeTextureRegion(icon, Utils3D.getIconBmpHeight(),
				//						true);
				icon.setItemInfo( info );
				DefaultLayout.cometShortcutView.add( icon );
			}
		}
	}
	
	public void bindItems(
			ArrayList<ItemInfo> items )
	{
		sortItemsByIndex( items );
		AddFeatures viewMain = mainGroup.getShortcutGridview();
		viewMain.enableAnimation( false );
		HotGridView3D gridPage = null;
		for( int i = 0 ; i < dockGroup.getPageNum() ; i++ )
		{
			gridPage = (HotGridView3D)dockGroup.getChildAt( i );
			gridPage.removeAllViews();
			gridPage.enableAnimation( false );
		}
		for( ItemInfo item : items )
		{
			if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
			{
				ShortcutInfo info = (ShortcutInfo)item;
				Icon3D icon = null;
				// Icon3D icon = new Icon3D(info.title.toString(),
				// info.getIcon(iconCache), info.title.toString());
				// Utils3D.changeTextureRegion(icon, Utils3D.getIconBmpHeight(),
				// true);
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
					icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() );
					Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
				}
				else
				{
					icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
				}
				Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
				icon.setItemInfo( info );
				if( item.angle == TYPE_ICON )
				{
					mainGroup.bindItem( icon );
				}
				else
				{
					if( info.cellX < dockGroup.getPageNum() )
					{
						dockGroup.bindItem( icon , info.cellX );
						dockGroup.addToHotseatItemsList( icon );
					}
				}
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
			{
				View3D view;
				view = DefaultLayout.showDefaultWidgetView( (ShortcutInfo)item );
				if( view != null )
				{
					// Utils3D.changeTextureRegion(view,
					// R3D.workspace_cell_width,
					// true);
					Utils3D.changeTextureRegion( view , Utils3D.getIconBmpHeight() , true );
					if( item.angle == TYPE_ICON )
					{
						mainGroup.bindItem( view );
					}
					else
					{
						if( item.cellX < dockGroup.getPageNum() )
						{
							dockGroup.bindItem( view , item.cellX );
							dockGroup.addToHotseatItemsList( view );
						}
					}
				}
				else
					Log.v( "sidebar" , "add virture ICON error! name = " + item.title );
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
			{
				UserFolderInfo folderInfo = (UserFolderInfo)item;
				if( folderInfo.contents.size() > 0 )
				{
					FolderIcon3D newFolder = new FolderIcon3D( folderInfo );
					newFolder.changeFolderFrontRegion( true );
					if( item.cellX < dockGroup.getPageNum() )
					{
						dockGroup.bindItem( (View3D)newFolder , item.cellX );
						dockGroup.addToHotseatItemsList( (View3D)newFolder );
					}
					newFolder.createAndAddShortcut( iconCache , folderInfo );
				}
				else
				{
					FolderIcon3D newFolder = new FolderIcon3D( folderInfo );
					newFolder.changeFolderFrontRegion( true );
					if( item.cellX < dockGroup.getPageNum() )
					{
						dockGroup.bindItem( (View3D)newFolder , item.cellX );
						dockGroup.addToHotseatItemsList( (View3D)newFolder );
					}
				}
			}
		}
		viewMain.enableAnimation( true );
		for( int i = 0 ; i < dockGroup.getPageNum() ; i++ )
		{
			gridPage = (HotGridView3D)dockGroup.getChildAt( i );
			gridPage.enableAnimation( true );
			gridPage.setCellCount( gridPage.getChildCount() , 1 );
		}
		finishBind = true;
	}
	
	public void bindBackItems(
			ArrayList<ItemInfo> items )
	{
		sortItemsByIndex( items );
		AddFeatures viewMain = mainGroup.getShortcutGridview();
		viewMain.enableAnimation( false );
		HotGridView3D gridPage = null;
		for( int i = 0 ; i < dockGroup.getPageNum() ; i++ )
		{
			gridPage = (HotGridView3D)dockGroup.getChildAt( i );
			gridPage.enableAnimation( false );
		}
		for( ItemInfo item : items )
		{
			if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || item.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
			{
				ShortcutInfo info = (ShortcutInfo)item;
				Icon3D icon = null;
				// Icon3D icon = new Icon3D(info.title.toString(),
				// info.getIcon(iconCache), info.title.toString());
				// Utils3D.changeTextureRegion(icon, Utils3D.getIconBmpHeight(),
				// true);
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
					icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() );
					Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
				}
				else
				{
					icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
				}
				Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
				icon.setItemInfo( info );
				if( item.angle == TYPE_ICON )
				{
					mainGroup.bindItem( icon );
				}
				else
				{
					if( info.cellX < dockGroup.getPageNum() )
					{
						dockGroup.bindBackItem( icon , info.cellX );
						dockGroup.addToHotseatItemsList( icon );
					}
				}
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
			{
				View3D view;
				view = DefaultLayout.showDefaultWidgetView( (ShortcutInfo)item );
				if( view != null )
				{
					// Utils3D.changeTextureRegion(view,
					// R3D.workspace_cell_width,
					// true);
					Utils3D.changeTextureRegion( view , Utils3D.getIconBmpHeight() , true );
					if( item.angle == TYPE_ICON )
					{
						mainGroup.bindItem( view );
					}
					else
					{
						if( item.cellX < dockGroup.getPageNum() )
						{
							dockGroup.bindBackItem( view , item.cellX );
							dockGroup.addToHotseatItemsList( view );
						}
					}
				}
				else
					Log.v( "sidebar" , "add virture ICON error! name = " + item.title );
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
			{
				UserFolderInfo folderInfo = (UserFolderInfo)item;
				if( folderInfo.contents.size() > 0 )
				{
					FolderIcon3D newFolder = new FolderIcon3D( folderInfo );
					newFolder.changeFolderFrontRegion( true );
					if( item.cellX < dockGroup.getPageNum() )
					{
						dockGroup.bindBackItem( (View3D)newFolder , item.cellX );
						dockGroup.addToHotseatItemsList( (View3D)newFolder );
					}
					newFolder.createAndAddShortcut( iconCache , folderInfo );
				}
				else
				{
					FolderIcon3D newFolder = new FolderIcon3D( folderInfo );
					newFolder.changeFolderFrontRegion( true );
					if( item.cellX < dockGroup.getPageNum() )
					{
						dockGroup.bindBackItem( (View3D)newFolder , item.cellX );
						dockGroup.addToHotseatItemsList( (View3D)newFolder );
					}
				}
			}
		}
		viewMain.enableAnimation( true );
		for( int i = 0 ; i < dockGroup.getPageNum() ; i++ )
		{
			gridPage = (HotGridView3D)dockGroup.getChildAt( i );
			gridPage.enableAnimation( true );
			gridPage.setCellCount( gridPage.getChildCount() , 1 );
		}
		finishBind = true;
	}
	
	public ArrayList<View3D> getDragObjects()
	{
		ArrayList<View3D> list = new ArrayList<View3D>();
		// list.add(dragObj);
		if( dragObj != null )
		{
			list.add( dragObj );
		}
		return list;
	}
	
	public void setIconCache(
			IconCache iconCache )
	{
		this.iconCache = iconCache;
	}
	
	private void dealUpdateFocusViewShowStatus()
	{
		//		HotGridView3D hotGrid = (HotGridView3D)dockGroup.getCurrentView();
		//		int focusIndex = hotGrid.getFocusIndex();
	}
	
	private void dealUpdateObj3DShowStatus()
	{
		if( dockGroup.isVisible() == false )
		{
			return;
		}
		HotGridView3D hotGrid = (HotGridView3D)dockGroup.getCurrentView();
		// if (hotGrid.getChildCount()==4)
		// {
		// Model3DGroup.updateShowStatus(false,false,false,false);
		// }
		// else
		if( hotGrid.getChildCount() == 0 )
		{
		}
		else
		{
			boolean leftShow = true;
			boolean leftMiddleShow = true;
			boolean rightMiddleShow = true;
			boolean rightShow = true;
			int gridIndex = 0;
			View3D myActor;
			for( int i = 0 ; i < hotGrid.getChildCount() ; i++ )
			{
				myActor = hotGrid.getChildAt( i );
				gridIndex = ( (IconBase3D)myActor ).getItemInfo().screen;
				if( gridIndex == 0 )
				{
					leftShow = false;
				}
				else if( gridIndex == 1 )
				{
					leftMiddleShow = false;
				}
				else if( gridIndex == 2 )
				{
					rightMiddleShow = false;
				}
				else if( gridIndex == 3 )
				{
					rightShow = false;
				}
			}
		}
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			Vector2 vec = null;
			if( icon.getTag() instanceof Vector2 )
			{
				vec = (Vector2)icon.getTag();
				vec.y += icon.height / 2;// 向上偏移
			}
			switch( event_id )
			{
				case Icon3D.MSG_ICON_LONGCLICK:
					if( icon.getParent() instanceof Widget3DShortcut )
					{
						Widget3DShortcut shortcut = (Widget3DShortcut)icon.getParent();
						dragObj = shortcut.getWidget3D();
						icon.toAbsoluteCoords( point );
						if( dragObj != null )
						{
							dragObj.setPosition( point.x , point.y );
						}
						else
						{
							return true;
						}
					}
					else
						dragObj = icon;
					vec.x += icon.width / 2 - dragObj.width / 2;
					this.setTag( vec );
					mainGroup.releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		if( sender instanceof HotSeatMainGroup )
		{
			switch( event_id )
			{
				case HotSeat3D.MSG_ON_DROP:
					View3D focus = mainGroup.getFocusView();
					Utils3D.changeTextureRegion( focus , R3D.workspace_cell_height , true );
					this.setTag( focus );
					return viewParent.onCtrlEvent( this , MSG_ON_DROP );
				case HotSeat3D.MSG_LONGCLICK_INAPPLIST:
					return viewParent.onCtrlEvent( this , event_id );
				case HotSeat3D.MSG_MAINGROUP_SCROLL_DOWN:
					float deltaY = (Float)mainGroup.getTag();
					if( deltaY > 0 && STATE_BACK == obj_state )
					{
						// mainGroup.hide();
						type = TYPE_WIDGET;
						R3D.hot_obj_rot_deg += 180;
						mClockwise = true;
						//					startMyTween();
						//					iLoongLauncher.getInstance().getD3dListener().getRoot().getDesktopEdit().exitEditModeExternal();	// added by zhenNan.ye
					}
					return true;
				case HotSeat3D.MSG_MAINGROUP_SCROLL_UP:
					float delY = (Float)mainGroup.getTag();
					if( delY < 0 && STATE_BACK == obj_state )
					{
						// mainGroup.hide();
						type = TYPE_WIDGET;
						R3D.hot_obj_rot_deg -= 180;
						mClockwise = false;
						//					startMyTween();
						//					iLoongLauncher.getInstance().getD3dListener().getRoot().getDesktopEdit().exitEditModeExternal();	// added by zhenNan.ye
					}
					return true;
				case HotSeat3D.MSG_SHOW_ADD_APP_VIEW:
				case HotSeat3D.MSG_SHOW_FOLDER_VIEW://song 文件夹添加
				case HotSeat3D.MSG_SHOW_ADD_SHORTCUT_VIEW:
				case HotSeat3D.MSG_SHOW_ADD_WIDGET_VIEW:
					return viewParent.onCtrlEvent( this , event_id );
			}
		}
		if( sender instanceof HotDockGroup )
		{
			switch( event_id )
			{
				case HotSeat3D.MSG_ON_DROP:
					View3D focus = dockGroup.getFocusView();
					if( focus instanceof FolderIcon3D )
					{
						( (FolderIcon3D)focus ).changeFolderFrontRegion( false );
					}
					else
					{
						Utils3D.changeTextureRegion( focus , R3D.workspace_cell_height , true );
					}
					this.setTag( focus );
					return viewParent.onCtrlEvent( this , MSG_ON_DROP );
				case HotSeat3D.MSG_LONGCLICK_INAPPLIST:
					return viewParent.onCtrlEvent( this , event_id );
				case HotSeat3D.MSG_DOCKGROUP_SCROLL_DOWN:
					if( STATE_FRONT == obj_state )
					{
						float scaleY = (Float)dockGroup.getTag();
						type = TYPE_WIDGET;
						// startAutoEffect(scaleY);
						if( scaleY > 0 && bRot_Effect == false )
						{
							type = TYPE_ICON;
							R3D.hot_obj_rot_deg += 180;
							mClockwise = true;
							//						startMyTween();
							//						iLoongLauncher.getInstance().getD3dListener().getRoot().enterDesktopEditMode();	// added by zhenNan.ye
						}
					}
					return true;
				case HotSeat3D.MSG_DOCKGROUP_SCROLL_UP:
					float tagY = (Float)dockGroup.getTag();
					if( tagY < 0 && STATE_FRONT == obj_state && bRot_Effect == false )
					{
						type = TYPE_ICON;
						R3D.hot_obj_rot_deg -= 180;
						mClockwise = false;
						//					startMyTween();
						//					iLoongLauncher.getInstance().getD3dListener().getRoot().enterDesktopEditMode();	// added by zhenNan.ye
					}
					return true;
				case HotSeat3D.MSG_VIEW_START_MAIN:
					if( !Desktop3DListener.bCreatDone )
						return true;
					viewParent.onCtrlEvent( this , event_id );
					return true;
				case HotSeat3D.MSG_ADD_DRAGLAYER:
					this.setTag( dockGroup.getTag() );
					viewParent.onCtrlEvent( this , event_id );
					return true;
				case HotSeat3D.MSG_MODEL_SCROLL_UP:
					if( modelUp )
					{
						return true;
					}
					if( animation_line != null )
					{
						return true;
					}
					animation_line = Timeline.createParallel();
					animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , 0.2f ).target( R3D.hot_obj_rot_deg - 45 , 0 , 0 ).ease( Cubic.OUT ) );
					animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.POS_XY , 0.2f ).target( 0 , -50 , 0 ).ease( Cubic.OUT ) );
					animation_line.start( View3DTweenAccessor.manager ).setUserData( model_to_reset_effect ).setCallback( this );
					modelUp = true;
					return true;
				case HotSeat3D.MSG_MODEL_SCROLL_DOWN:
					modelUp = false;
					if( animation_line != null )
					{
						stopAutoEffect();
					}
					animation_line = Timeline.createParallel();
					animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , 0.2f ).target( R3D.hot_obj_rot_deg , 0 , 0 ).ease( Cubic.OUT ) );
					animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.POS_XY , 0.2f ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
					animation_line.start( View3DTweenAccessor.manager ).setUserData( model_to_reset_effect ).setCallback( this );
					return true;
			}
		}
		if( sender instanceof HotGridView3D )
		{
			HotGridView3D hotGridView3D = (HotGridView3D)sender;
			switch( event_id )
			{
				case HotGridView3D.MSG_UPDATE_OBJ3D_SHOW_STATUS:
					dealUpdateObj3DShowStatus();
					return true;
				case HotGridView3D.MSG_UPDATE_OBJ3D_INDEX_SHOW_STATUS:
					dealUpdateFocusViewShowStatus();
					return true;
				case HotGridView3D.MSG_VIEW_OUTREGION_DRAG:
					View3D focus = hotGridView3D.getFocusView();
					dockGroup.releaseFocus();
					dockGroup.deleteFromHotseatItemsList( focus );
					hotGridView3D.releaseFocus();
					bOutDragAnim = false;
					// hotGridView3D.removeView(focus);
					focus.toAbsoluteCoords( point );
					this.setTag( new Vector2( point.x , point.y ) );
					if( focus instanceof FolderIcon3D )
					{
						( (FolderIcon3D)focus ).changeFolderFrontRegion( false );
						( (FolderIcon3D)focus ).setLongClick( true );
					}
					else
					{
						Utils3D.changeTextureRegion( focus , R3D.workspace_cell_height , true );
					}
					dragObj = focus;
					dragObj.x = focus.x;
					dragObj.y = focus.y;
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		if( sender instanceof GridView3D )
		{
			GridView3D gridView3D = (GridView3D)sender;
			switch( event_id )
			{
				case GridView3D.MSG_VIEW_OUTREGION_DRAG:
					View3D focus = gridView3D.getFocusView();
					gridView3D.releaseFocus();
					// gridView3D.removeView(focus);
					bOutDragAnim = true;
					focus.toAbsoluteCoords( point );
					this.setTag( new Vector2( point.x , point.y ) );
					dragObj = focus;
					dragObj.x = focus.x;
					dragObj.y = focus.y;
					Utils3D.changeTextureRegion( focus , R3D.workspace_cell_height , true );
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	public void startMainGroupOutDragAnim()
	{
		if( bOutDragAnim == true )
		{
			mainGroup.startOutDragAnim();
		}
	}
	
	// public void changeTextureRegion(View3D myActor,float iconHeight)
	// {
	// if (myActor instanceof Icon3D)
	// {
	//
	// float scale = ((float) R3D.workspace_cell_width / (float)
	// R3D.workspace_cell_height);
	// if (iconHeight == R3D.workspace_cell_width
	// && myActor.height == R3D.workspace_cell_height) {
	// float V2 = myActor.region.getV()
	// + (myActor.region.getV2() - myActor.region.getV())
	// * scale;
	// myActor.region.setV2(V2);
	// //myActor.setScale(0.8f, 0.8f);
	//
	// }
	//
	// else if (iconHeight == R3D.workspace_cell_height
	// && myActor.height == R3D.workspace_cell_width) {
	// float V2 = myActor.region.getV()
	// + (myActor.region.getV2() - myActor.region.getV())
	// / scale;
	// myActor.region.setV2(V2);
	// //myActor.setScale(1.0f, 1.0f);
	//
	// }
	// myActor.setSize(R3D.workspace_cell_width, iconHeight);
	// myActor.setOrigin(myActor.width / 2, myActor.height / 2);
	// if (R3D.icon_bg_num==1)
	// {
	// myActor.setScale(0.8f, 0.8f);
	// }
	// }
	// else
	// {
	// //myActor.setScale(0.6f, 0.6f);
	// }
	//
	// }
	// public void changeTextureRegion(ArrayList<View3D> view3DArray,float
	// iconHeight)
	// {
	// View3D myActor;
	// int Count = view3DArray.size();
	// for (int i=0;i<Count;i++)
	// {
	// myActor=view3DArray.get(i);
	// changeTextureRegion(myActor,iconHeight);
	// }
	// }
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
		ArrayList<View3D> l = new ArrayList<View3D>();
		l.add( dragObj );
		return l;
	}
	
	boolean isDrop = false;
	
	public boolean dealMainGroupOnDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		int count = mainGroup.getShortcutCount() + list.size();
		if( count > HotSeatMainGroup.MAX_ICON_NUM )
		{
			mainGroup.backtoOrig( list );
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.max_added_twenty ) );
			Log.e( "sidebar" , "ondrop X:" + this.x + " Y:" + y );
			list.clear();
			return true;
		}
		if( list.size() > 0 )
		{
			View3D view = list.get( 0 );
			if( !( view instanceof Icon3D ) )
			{
				mainGroup.backtoOrig( list );
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.catnot_add_to_sidebar ) );
				list.clear();
				return true;
			}
		}
		Utils3D.changeTextureRegion( list , Utils3D.getIconBmpHeight() , true );
		mainGroup.addItems( list );
		list.clear();
		return true;
	}
	
	public boolean dealDockGroupOnDrop(
			ArrayList<View3D> list ,
			int page ,
			float x ,
			float y )
	{
		Log.v( "ondrop" , "hotseat3d dealDockGroupOnDrop" );
		dockGroup.addItems( list , page );
		list.clear();
		return true;
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		Log.v( "ondrop" , "hotseat3d onDrop" );
		isDrop = true;
		if( list.size() > 0 )
		{
			View3D view = list.get( 0 );
			if( !( view instanceof Icon3D ) )
			{
				if( view instanceof FolderIcon3D )
				{
				}
				else if( view instanceof PageContainer )
				{ // added by Hugo.ye
					return false;
				}
				else
				{
					if( view instanceof Widget3D && list.size() == 1 && ( (Widget3D)view ).getItemInfo().container == -1 )
					{/*
						* 处理将widget
						* 3d
						* 扔到其它地方时的资源收集
						*/
						Root3D.deleteFromDB( (ItemInfo)( (Widget3D)view ).getItemInfo() );
						Widget3D widget3D = (Widget3D)view;
						Widget3DManager.getInstance().deleteWidget3D( widget3D );
					}
					else
					{
						dockGroup.backtoOrig( list );
					}
					SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.catnot_add_to_sidebar ) );
					list.clear();
					return true;
				}
			}
		}
		if( type == TYPE_ICON )
		{
			return dealMainGroupOnDrop( list , x , y );
		}
		else if( type == TYPE_WIDGET )
		{
			return dealDockGroupOnDrop( list , dockGroup.getCurrentPage() , x , y );
		}
		list.clear();
		return true;
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		if( type == TYPE_WIDGET )
		{
			if( list.size() > 0 )
			{
				if( list.get( 0 ) instanceof Icon3D || list.get( 0 ) instanceof FolderIcon3D )
				{
					dockGroup.dealDockGroupDropOver( list , x , y );
					return true;
				}
			}
		}
		return false;
	}
	
	public int getHot3DState()
	{
		return obj_state;
	}
	
	public void setHot3DState(
			int state )
	{
		obj_state = state;
	}
	
	private void stopAutoEffect()
	{
		if( animation_line != null && !animation_line.isFinished() )
		{
			animation_line.free();
			animation_line = null;
		}
	}
	
	private void startAutoEffect(
			float scaleY )
	{
		float anim_duration = 0.5f;
		int effect_kind = rot_to_reset_effect;
		if( scaleY != -2.0f )
		{
			effect_kind = rot_to_down_effect;
		}
		else
		{
			effect_kind = rot_to_reset_effect;
			stopAutoEffect();
			animation_line = Timeline.createParallel();
		}
		if( effect_kind == rot_to_reset_effect )
		{
			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , -R3D.hot_obj_trans_z ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.start( View3DTweenAccessor.manager ).setUserData( effect_kind ).setCallback( this );
			bRot_Effect = true;
		}
		else
		{
			bRot_Effect = false;
			Model3DGroup.setRotation( 15 + 15 * scaleY );
			dockGroup.setRotation( 15 + 15 * scaleY );
		}
	}
	
	//	private void startModelTween(){
	//		if (STATE_UP == obj_state) {
	//			return;
	//		}
	//
	//		if (animation_line != null) {
	//			return;
	//		}
	//		animation_line = Timeline.createParallel();
	//		animation_line.push(Tween
	//				.to(Model3DGroup, View3DTweenAccessor.ROTATION,
	//						1f).target(R3D.hot_obj_rot_deg - 45, 0, 0).ease(Cubic.OUT));
	//		animation_line
	//		.push(Tween
	//				.to(Model3DGroup, View3DTweenAccessor.POS_XY,
	//						1f).target(0, -50, 0)
	//				.ease(Cubic.OUT));
	//		animation_line.start(View3DTweenAccessor.manager)
	//		.setUserData(model_to_reset_effect).setCallback(this);
	//	}
	public void startDockbarTweenExternal()
	{
		type = TYPE_WIDGET;
		R3D.hot_obj_rot_deg += 180;
		mClockwise = true;
		startMyTween();
	}
	
	Tween modelTween;
	
	public void startMyTween()
	{
		if( STATE_ROTATE == obj_state )
		{
			return;
		}
		if( animation_line != null )
		{
			return;
		}
		float anim_duration = 0.5f;
		animation_line = Timeline.createParallel();
		if( STATE_FRONT == getHot3DState() )
		{
			//   Model3DGroup.swapSide();
			mainGroup.show();
			mainGroup.bringToFront();
			frontToBack = true;
			Model3DGroup.setOriginZ( -(float)( ( this.height / 2 ) * Math.cos( 15 * Math.PI / 180 ) ) - 8 );
			//Model3DGroup.setRotationAngle(1, 0, 0);
			Model3DGroup.setOrigin( width / 2 , (float)( height / 2 * Math.sin( 15 * Math.PI / 180 ) ) );
			dockGroup.setOriginZ( -(float)( ( this.height / 2 ) * Math.cos( 15 * Math.PI / 180 ) ) );
			//Model3DGroup.setRotationAngle(1, 0, 0);
			dockGroup.setOrigin( width / 2 , (float)( height / 2 * Math.sin( 15 * Math.PI / 180 ) ) );
			mainGroup.setOriginZ( -(float)( ( this.height / 2 ) * Math.cos( 15 * Math.PI / 180 ) ) );
			//Model3DGroup.setRotationAngle(1, 0, 0);
			mainGroup.setOrigin( width / 2 , (float)( height / 2 * Math.sin( 15 * Math.PI / 180 ) ) );
			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( R3D.hot_obj_rot_deg , 0 , 0 ).ease( Cubic.OUT ) );
			android.util.Log.i( "hot_obj_rot_deg" , Integer.toString( R3D.hot_obj_rot_deg ) );
			//			animation_line
			//					.push(Tween
			//							.to(Model3DGroup, View3DTweenAccessor.POS_XY,
			//									anim_duration).target(0, -30, 0)
			//							.ease(Cubic.OUT));
			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( R3D.hot_obj_rot_deg + 180 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 1 , 0 , 0 ).ease( Cubic.OUT ) );
			//			animation_line.push(Tween
			//					.to(mainGroup, View3DTweenAccessor.POS_XY, anim_duration)
			//					.target(0, 30, 0).ease(Cubic.OUT));
			//			animation_line.push(Tween
			//					.to(dockGroup, View3DTweenAccessor.POS_XY, anim_duration)
			//					.target(0, -R3D.hot_dock_trans_y, 0).ease(Cubic.OUT));
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( R3D.hot_obj_rot_deg , 0 , 0 ).ease( Cubic.OUT ) );
			setHot3DState( STATE_ROTATE );
			animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_back ).setCallback( this );
		}
		else if( STATE_BACK == getHot3DState() )
		{
			if( modelTween != null )
			{
				modelTween.free();
				modelTween = null;
			}
			dockGroup.show();
			dockGroup.bringToFront();
			frontToBack = false;
			android.util.Log.i( "hot_obj_rot_deg" , Integer.toString( (int)Model3DGroup.getRotation() ) );
			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( R3D.hot_obj_rot_deg , 0 , 0 ).ease( Cubic.OUT ) );
			android.util.Log.i( "hot_obj_rot_deg" , Integer.toString( R3D.hot_obj_rot_deg ) );
			android.util.Log.i( "hot_obj_rot_deg" , Integer.toString( (int)Model3DGroup.getRotation() ) );
			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( R3D.hot_obj_rot_deg + 180 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , -R3D.hot_obj_trans_z ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 1 , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( R3D.hot_obj_rot_deg , 0 , 0 ).ease( Cubic.OUT ) );
			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
			for( int i = 0 ; i < dockGroup.getPageNum() ; i++ )
			{
				if( i != dockGroup.getCurrentPage() )
				{
					View3D view = dockGroup.getChildAt( i );
					view.hide();
				}
			}
			setHot3DState( STATE_ROTATE );
			animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_front ).setCallback( this );
		}
		else
		{
			if( animation_line != null )
			{
				animation_line = null;
			}
		}
	}
	
	public void setTag2(
			Object obj )
	{
		tag2 = obj;
	}
	
	public Object getTag2()
	{
		return tag2;
	}
	
	public View3D findExistView(
			int index )
	{
		if( dockGroup.isVisible() == false )
		{
			return null;
		}
		HotGridView3D gridView3D = (HotGridView3D)dockGroup.getCurrentView();
		return gridView3D.findExistView( index );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( FolderIcon3D.captureCurScreen )
		{
			Model3DGroup.color.a = 0;
		}
		super.draw( batch , parentAlpha );
		if( LauncherModel.waitSidebar && finishBind )
		{
			LauncherModel.waitSidebar = false;
			synchronized( lock )
			{
				lock.notify();
			}
			if( iLoongLauncher.showAllAppFirst )
				super.hide();
		}
		//		if (mClockwise) {
		//			if (R3D.hot_obj_rot_deg - Model3DGroup.getRotation() < 42) {
		//				if (frontToBack) {
		//					Model3DGroup.mBackViewUp.bringToFront();
		//				}else {
		//					Model3DGroup.mBackViewDown.bringToFront();
		//				}
		//			}
		//		}else {
		//			if (R3D.hot_obj_rot_deg - Model3DGroup.getRotation() > -135) {
		//				if (frontToBack) {
		//					Model3DGroup.mBackViewUp.bringToFront();
		//				}else {
		//					Model3DGroup.mBackViewDown.bringToFront();
		//				}
		//			}
		//		}
		// if(!menuOpened){
		if( mClockwise )
		{
			if( R3D.hot_obj_rot_deg - Model3DGroup.getRotation() < 30 )
			{
				if( frontToBack )
				{
					isOnBackSide = true;
					Model3DGroup.objMenuUp.mBackViewUp.hide();
					// Model3DGroup.objMenuUp.mBackViewUp.bringToFront();
				}
				else
				{
					isOnBackSide = false;
					Model3DGroup.objMenuUp.mBackViewUp.show();
					//Model3DGroup.objMenuUp.objMenuDown.bringToFront();
				}
			}
		}
		else
		{
			// Model3DGroup.objMenuUp.mBackViewUp.bringToFront();
			if( R3D.hot_obj_rot_deg - Model3DGroup.getRotation() > -145 )
			{
				if( frontToBack )
				{
					isOnBackSide = true;
					Model3DGroup.objMenuUp.mBackViewUp.hide();
					//  Model3DGroup.objMenuUp.mBackViewUp.bringToFront();
				}
				else
				{
					isOnBackSide = false;
					Model3DGroup.objMenuUp.mBackViewUp.show();
					//  Model3DGroup.objMenuUp.objMenuDown.bringToFront();
				}
			}
		}
		//  }
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source == animation_line )
		{
			int animKind = (Integer)( source.getUserData() );
			animation_line = null;
			if( animKind == rot_to_back )
			{
				setHot3DState( STATE_BACK );
				dockGroup.hide();
				type = TYPE_ICON;
				mainGroup.bringToFront();
			}
			else if( animKind == rot_to_front )
			{
				setHot3DState( STATE_FRONT );
				mainGroup.hide();
				mainGroup.gridPosReset();
				type = TYPE_WIDGET;
				dockGroup.bringToFront();
			}
			else if( animKind == rot_to_reset_effect )
			{
				setHot3DState( STATE_FRONT );
				mainGroup.hide();
				type = TYPE_WIDGET;
				dockGroup.bringToFront();
				bRot_Effect = false;
			}
			else if( animKind == rot_to_out )
			{
				this.hide();
			}
			return;
		}
		if( type == TweenCallback.COMPLETE && source == myTween )
		{
			int animKind = (Integer)( source.getUserData() );
			if( animKind == hide_dock )
			{
				if( dockGroup.isVisible() == true )
				{
					dockGroup.setRotation( 0 );
					dockGroup.setPosition( 0 , 0 );
					dockGroup.moving = false;
					dockGroup.setZ( 0 );
					Model3DGroup.setRotation( 0 );
					Model3DGroup.setPosition( 0 , 0 );
					Model3DGroup.setZ( 0 );
					mainGroup.setRotation( 180 );
					mainGroup.setPosition( 0 , 0 );
					mainGroup.setZ( 0 );
					dockGroup.removeVirtueFolderIcon();
					R3D.hot_obj_rot_deg = 0;
				}
				super.hide();
			}
			else if( animKind == show_dock )
			{
				Object view = getTag2();
				if( view != null && view instanceof FolderIcon3D )
				{
					dockGroup.bindItem( (FolderIcon3D)view );
				}
				setTag2( null );
				// super.show();
			}
			return;
		}
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		if( Gdx.graphics.getDensity() > 1 )
		{
			Group.toChildCoordinates( this , x , y , point );
			float offsetY = 20 * Gdx.graphics.getDensity();
			return( ( point.x >= 0 && point.x < width ) && ( point.y >= 0 && point.y < ( height + offsetY ) ) );
		}
		else
		{
			return super.pointerInAbs( x , y );
		}
	}
	
	// added by zhenNan.ye
	public HotObjLoader3D getModel3DGroup()
	{
		return Model3DGroup;
	}
	
	public static void startModelAnimal(
			float duration )
	{
		if( Model3DGroup != null )
			Model3DGroup.startScrollDownView( duration );
		//this.hide();
		//		mainGroup.hide();
		//		dockGroup.hide();
	}
	
	public static void closeMenu()
	{
		if( Model3DGroup != null )
			Model3DGroup.closeMenu();
	}
	
	public void dockbarTurnUp()
	{
		this.show();
		if( animation_line != null )
		{
			int animKind = (Integer)( animation_line.getUserData() );
			if( animKind == rot_to_in )
			{
				return;
			}
			animation_line.free();
		}
		animation_line = Timeline.createParallel();
		animation_line.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.5f ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
		animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_in ).setCallback( this );
	}
	
	public void dockbarTurnDown()
	{
		if( animation_line != null )
		{
			int animKind = (Integer)( animation_line.getUserData() );
			if( animKind == rot_to_out )
			{
				return;
			}
			animation_line.free();
		}
		animation_line = Timeline.createParallel();
		this.setRotationAngle( 1 , 0 , 0 );
		this.setOriginZ( 5 * width / 720 );
		this.setOrigin( width / 2 , 0 );
		animation_line.push( Tween.to( this , View3DTweenAccessor.ROTATION , 0.5f ).target( -105 , 0 , 0 ).ease( Cubic.OUT ) );
		animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_out ).setCallback( this );
	}
}
