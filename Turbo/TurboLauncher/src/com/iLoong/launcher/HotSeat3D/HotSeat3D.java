package com.iLoong.launcher.HotSeat3D;


import java.util.ArrayList;

import android.graphics.Bitmap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.DrawDynamicIcon;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.CustomShortcutIcon;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.action.ActionData;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.core.MD5;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class HotSeat3D extends ViewGroupOBJ3D implements DropTarget3D , DragSource3D
{
	
	public static final int MSG_START_DRAG = 0;
	public static final int TYPE_ICON = 0;
	public static final int TYPE_WIDGET = 1;
	public static final int MSG_ON_DROP = 2;
	public static final int MSG_LONGCLICK_INAPPLIST = 3;
	public static final int MSG_MAINGROUP_SCROLL_DOWN = 4;
	public static final int MSG_DOCKGROUP_SCROLL_UP = 5;
	public static final int MSG_VIEW_START_MAIN = 6;
	public static final int MSG_ADD_DRAGLAYER = 7;
	public static final int MSG_DOCKGROUP_SCROLL_DOWN = 8;
	public static final int MSG_DOCKGROUP_SCROLL_FLING = 9;
	public static final int MSG_VIEW_HIDE_MAIN = 10;
	/* 动画管理部分变量定义 */
	private int obj_state;
	private Timeline animation_line = null;
	private Tween myTween;
	private static final int rot_to_back = 0;
	private static final int rot_to_front = 1;
	private static final int rot_to_down_effect = 2;
	private static final int rot_to_reset_effect = 3;
	private static final int rot_to_circular = 4;
	public static final int STATE_FRONT = 1;
	public static final int STATE_BACK = 2;
	public static final int STATE_ROTATE = 3;
	private static final int hide_dock = 0;
	private static final int show_dock = 1;
	/* 动画管理部分变量定义 */
	private View3D dragObj;
	public static int curType;
	private HotSeatMainGroup mainGroup;
	private HotObjLoader3D Model3DGroup;
	public HotDockGroup dockGroup;
	private IconCache iconCache;
	private Object tag2;
	private boolean bOutDragAnim = false;
	private boolean finishBind = false;
	public static Object lock = new Object();
	private Timeline animation_line_show = null;
	private static boolean mIsNeedShake = false;
	public static boolean enableRollDockbar = false;
	public int recordRotation = 0;
	private final int SCROLL_UP = 0;
	private final int SCROLL_DOWN = 1;
	private int mScrollType = 0;
	
	public HotSeat3D(
			String name )
	{
		super( name );
		width = Utils3D.getScreenWidth();
		height = R3D.hot_obj_height;
		x = 0;
		y = 0;
		setOrigin( width / 2 , height / 2 );
		curType = TYPE_WIDGET;
		recordRotation = 0;
		buildElements();
	}
	
	public void onThemeChanged()
	{
		height = R3D.hot_obj_height;
		if( DefaultLayout.dynamic_icon )
		{
			DrawDynamicIcon.needrefreshbg = true;
		}
		setOrigin( Utils3D.getScreenWidth() / 2 , height / 2 );
		dockGroup.setSize( width , height );
		dockGroup.onThemeChanged();
		if( DefaultLayout.enable_hotseat_rolling )
		{
			mainGroup.setSize( width , height );
			mainGroup.onThemeChanged();
			Model3DGroup.setSize( width , height );
			Model3DGroup.onThemeChanged();
		}
	}
	
	private void buildElements()
	{
		if( DefaultLayout.enable_hotseat_rolling )
		{
			mainGroup = new HotSeatMainGroup( "HotSeatMainGroup" , (int)width , (int)R3D.hot_obj_height );
			Model3DGroup = new HotObjLoader3D( "Model3DGroup" , (int)width , R3D.hot_obj_height );
			addView( Model3DGroup );
			Model3DGroup.HotObj_Loader();
			Model3DGroup.setOriginZ( -R3D.hot_obj_origin_z );
			Model3DGroup.setRotationVector( 1 , 0 , 0 );
			mainGroup.setRotationVector( 1 , 0 , 0 );
			//	mainGroup.setOriginZ( -R3D.hot_obj_origin_z );
			mainGroup.setRotation( R3D.hot_obj_rot_deg );
			mainGroup.hide();
			mainGroup.color.a = 0;
			addView( mainGroup );
		}
		dockGroup = new HotDockGroup( "dockGroup" , (int)width , (int)R3D.hot_obj_height );
		addView( dockGroup );
		setHot3DState( STATE_FRONT );
		dockGroup.setRotationVector( 1 , 0 , 0 );
		//		dockGroup.setOriginZ( -R3D.hot_frontgrid_origin_z );
		// dockGroup.setZ(-31);
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
		if( !showAndShake( delay ) )
		{
			this.setPosition( 0 , -R3D.seatbar_hide_height );
			myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setUserData( show_dock ).setCallback( this ).delay( delay );
		}
	}
	
	public void show()
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		stopTween();
		if( !showAndShake( 0 ) )// xiatian add //HotSeat3DShake
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
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , 0 , -R3D.seatbar_hide_height , 0 ).setUserData( hide_dock ).setCallback( this );
		// super.hide();
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
		if( y > this.height )
		{
			Log.v( "workspace3D" , "onTouchDown hotseat" );
			return false;
		}
		if( Workspace3D.isRecentAppVisible() )
		{
			return false;
		}
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( pointer == 1 )
		{
			return false;
		}
		else
		{
			if( DefaultLayout.enable_new_particle )
			{
				Desktop3DListener.root.particleStart( this.x , this.y , x , y );
			}
			boolean ret = super.onTouchDown( x , y , pointer );
			return ret;
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( y > this.height )
		{
			return false;
		}
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		if( Workspace3D.getInstance() != null && Workspace3D.getInstance().getDragList() != null && !Workspace3D.getInstance().getDragList().isEmpty() )
		{
			return true;
		}
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		return super.onTouchDragged( x , y , pointer );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		
		if (Workspace3D.getInstance() == null)
		{
			return true;
		}
		
		if (Workspace3D.getInstance().getDragList() == null)
		{
			return true;
		}
			
		if( !Workspace3D.getInstance().getDragList().isEmpty() )
		{
			return true;
		}
		if( DefaultLayout.WorkspaceActionGuide )
		{
			if( ActionData.getInstance().checkValidity() > 0 )
			{
				return true;
			}
		}
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		if( Workspace3D.isRecentAppVisible() )
		{
			return false;
		}
		return super.onClick( x , y );
	}
	
	public void onDragOverLeave()
	{
		dockGroup.onDragOverLeave();
	}
	
	public int getType()
	{
		return curType;
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
	
	public void bindItems(
			ArrayList<ItemInfo> items )
	{
		sortItemsByIndex( items );
		GridView3D viewMain = null;
		if( DefaultLayout.enable_hotseat_rolling )
		{
			viewMain = mainGroup.getShortcutGridview();
			viewMain.enableAnimation( false );
		}
		HotGridView3D viewDock = dockGroup.getShortcutGridview();
		viewDock.enableAnimation( false );
		// teapotXu add start
		// 由于初始化的时候，model 暂时不显示，此时加载DockBar Icon时，才显示
		if( !Desktop3DListener.bCreatDone )
		{
			if( Model3DGroup != null )
				Model3DGroup.ShowAllDockOBJViews();
			if( DefaultLayout.same_spacing_btw_hotseat_icons == true )
			{
				if( dockGroup.mHotSeatMiddleImgView != null )
					dockGroup.mHotSeatMiddleImgView.show();
			}
		}
		// teapotXu add end
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
					if( DefaultLayout.hotseat_hide_title )
					{
						icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() , false );
						Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
					}
					else
					{
						icon = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() );
					}
				}
				else
				{
					if( DefaultLayout.hotseat_hide_title )
					{
						icon = (Icon3D)Workspace3D.createShortcut( info , false );
						Utils3D.changeTextureRegion( icon , Utils3D.getIconBmpHeight() , true );
					}
					else
					{
						icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
					}
				}
				icon.setItemInfo( info );
				if( item.angle == TYPE_ICON )
				{
					if( DefaultLayout.enable_hotseat_rolling )
					{
						mainGroup.bindItem( icon );
					}
				}
				else
				{
					dockGroup.bindItem( icon , info.screen );
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
					if( DefaultLayout.hotseat_hide_title )
					{
						Utils3D.changeTextureRegion( view , Utils3D.getIconBmpHeight() , true );
					}
					if( item.angle == TYPE_ICON )
					{
						if( DefaultLayout.enable_hotseat_rolling )
						{
							mainGroup.bindItem( view );
						}
					}
					else
					{
						dockGroup.bindItem( view );
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
					if( DefaultLayout.hotseat_hide_title )
					{
						newFolder.folder_front.region.setTexture( FolderIcon3D.folderFrontWithoutShadowRegion.getTexture() );
						newFolder.changeFolderFrontRegion( true );
					}
					dockGroup.bindItem( (View3D)newFolder , folderInfo.screen );
					newFolder.createAndAddShortcut( iconCache , folderInfo );
				}
				else
				{
					FolderIcon3D newFolder = new FolderIcon3D( folderInfo );
					if( DefaultLayout.hotseat_hide_title )
					{
						newFolder.folder_front.region.setTexture( FolderIcon3D.folderFrontWithoutShadowRegion.getTexture() );
						newFolder.changeFolderFrontRegion( true );
					}
					dockGroup.bindItem( (View3D)newFolder , folderInfo.screen );
				}
			}
			else if( item.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT )
			{
				View3D view;
				view = CustomShortcutIcon.createCustomShortcut( item , false );
				if( view != null )
				{
					if( DefaultLayout.hotseat_hide_title )
					{
						Utils3D.changeTextureRegion( view , Utils3D.getIconBmpHeight() , true );
					}
					if( item.angle == TYPE_ICON )
					{
						if( DefaultLayout.enable_hotseat_rolling )
						{
							mainGroup.bindItem( view );
						}
					}
					else
					{
						dockGroup.bindItem( view );
					}
				}
			}
		}
		if( DefaultLayout.enable_hotseat_rolling )
		{
			viewMain.enableAnimation( true );
		}
		viewDock.enableAnimation( true );
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
		HotGridView3D hotGrid = dockGroup.getShortcutGridview();
		int focusIndex = hotGrid.getFocusIndex();
		if( Model3DGroup != null )
			Model3DGroup.updateShowStatus( focusIndex );
	}
	
	private void dealUpdateObj3DShowStatus()
	{
		if( dockGroup.isVisible() == false )
		{
			return;
		}
		HotGridView3D hotGrid = dockGroup.getShortcutGridview();
		// if (hotGrid.getChildCount()==4)
		// {
		// Model3DGroup.updateShowStatus(false,false,false,false);
		// }
		// else
		if( hotGrid.getChildCount() == 0 )
		{
			if( Model3DGroup != null )
				Model3DGroup.updateShowStatus( true , true , true , true );
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
			if( Model3DGroup != null )
				Model3DGroup.updateShowStatus( leftShow , leftMiddleShow , rightMiddleShow , rightShow );
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
					if( mainGroup != null )
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
					//					if(deltaY==HotSeatMainGroup.ROLL_BACK_FLAG){
					//						return false;
					//					}
					if( deltaY < 0 )
					{
						mScrollType = SCROLL_UP;
					}
					else
					{
						mScrollType = SCROLL_DOWN;
					}
					if( deltaY > 0 && STATE_BACK == obj_state )
					{
						// mainGroup.hide();
						//Jone modify, this varilable is assigned on {@link startMyTween}
						//type = TYPE_WIDGET;
						startMyTween();
					}
					else
					{//Jone add, it enable us to scroll the dockbar circularly
						startMyTween();
					}
					return true;
			}
		}
		if( sender instanceof HotDockGroup )
		{
			switch( event_id )
			{
				case HotSeat3D.MSG_ON_DROP:
					View3D focus = dockGroup.getFocusView();
					if( DefaultLayout.hotseat_hide_title )
					{
						if( focus instanceof FolderIcon3D )
						{
							( (FolderIcon3D)focus ).changeFolderFrontRegion( false );
						}
						else
						{
							Utils3D.changeTextureRegion( focus , R3D.workspace_cell_height , true );
						}
					}
					this.setTag( focus );
					return viewParent.onCtrlEvent( this , MSG_ON_DROP );
				case HotSeat3D.MSG_LONGCLICK_INAPPLIST:
					return viewParent.onCtrlEvent( this , event_id );
				case HotSeat3D.MSG_DOCKGROUP_SCROLL_DOWN:
				case HotSeat3D.MSG_DOCKGROUP_SCROLL_UP:
					float tagY = (Float)dockGroup.getTag();
					if( tagY < 0 )
					{
						mScrollType = SCROLL_UP;
					}
					else
					{
						mScrollType = SCROLL_DOWN;
					}
					startMyTween();
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
				case HotSeat3D.MSG_VIEW_HIDE_MAIN:
					if( DefaultLayout.enable_takein_workspace_by_longclick )
						viewParent.onCtrlEvent( this , event_id );
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
					hotGridView3D.releaseFocus();
					bOutDragAnim = false;
					// hotGridView3D.removeView(focus);
					focus.toAbsoluteCoords( point );
					this.setTag( new Vector2( point.x , point.y ) );
					if( focus instanceof FolderIcon3D )
					{
						if( DefaultLayout.hotseat_hide_title )
						{
							( (FolderIcon3D)focus ).changeFolderFrontRegion( false );
							( (FolderIcon3D)focus ).folder_front.region.setTexture( FolderIcon3D.folderFrontRegion.getTexture() );
						}
						( (FolderIcon3D)focus ).setLongClick( true );
					}
					else
					{
						if( DefaultLayout.hotseat_hide_title )
						{
							ItemInfo info;
							View3D temView = null;
							info = ( (IconBase3D)focus ).getItemInfo();
							if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
							{
								if( ( (ShortcutInfo)info ).intent != null && ( (ShortcutInfo)info ).intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
								{
									Bitmap bmp = Bitmap.createBitmap( ( (ShortcutInfo)info ).getIcon( iLoongApplication.mIconCache ) );
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
									temView = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() );
								}
								else
								{
									temView = Workspace3D.createShortcut( (ShortcutInfo)info , true );
								}
								( (IconBase3D)focus ).setItemInfo( info );
							}
							else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
							{
								temView = WidgetIcon.createWidgetIcon( (ShortcutInfo)info , true );
							}
							else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT )
							{
								temView = CustomShortcutIcon.createCustomShortcut( info , true );
							}
							Utils3D.changeTextureRegion( temView , R3D.workspace_cell_height , true );
							temView.x = focus.x;
							temView.y = focus.y;
							focus.remove();
							focus = temView;
						}
					}
					dragObj = focus;
					dragObj.x = focus.x;
					dragObj.y = focus.y;
					//
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( focus , Root3D.MSG_SET_TRASH_POS );
					}
					boolean result = viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					if( DefaultLayout.newHotSeatMainGroup )
					{
						mainGroup.addAllMainGroup();
					}
					return result;
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
					if( DefaultLayout.hotseat_hide_title )
					{
						Utils3D.changeTextureRegion( focus , R3D.workspace_cell_height , true );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	public void startMainGroupOutDragAnim()
	{
		if( bOutDragAnim == true )
		{
			if( mainGroup != null )
				mainGroup.startOutDragAnim();
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( !Workspace3D.getInstance().getDragList().isEmpty() )
		{
			return true;
		}
		if( DefaultLayout.WorkspaceActionGuide )
		{
			if( ActionHolder.getInstance() != null )
			{
				return true;
			}
		}
		if( !Desktop3DListener.bCreatDone )
			return true;
		return super.onLongClick( x , y );
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
		// xiatian add start //newHotSeatMainGroup
		if( DefaultLayout.newHotSeatMainGroup )
		{
			mainGroup.backtoOrig( list );
			// SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.catnot_add_to_sidebar));
			list.clear();
			return true;
		}
		else
		{
			// xiatian add end
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
		}// xiatian add //newHotSeatMainGroup
	}
	
	public boolean dealDockGroupOnDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		
		
		if(dockGroup.getChildCount()>=HotDockGroup.CELL_MAX_NUM_X||!(list.get( 0 ) instanceof Icon3D))
			return  false;
		dockGroup.outView = list.get(0);
		if( DefaultLayout.hotseat_hide_title )
		{
			ArrayList<View3D> temList = new ArrayList<View3D>();
			ItemInfo info;
			View3D temView;
			for( View3D view : list )
			{
				info = ( (IconBase3D)view ).getItemInfo();
				if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION )
				{
					if( ( (ShortcutInfo)info ).intent != null && ( (ShortcutInfo)info ).intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
					{
						Bitmap bmp = Bitmap.createBitmap( ( (ShortcutInfo)info ).getIcon( iLoongApplication.mIconCache ) );
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
						temView = new Icon3D( info.title.toString() , bmp , info.title.toString() , Icon3D.getIconBg() , false );
					}
					else
					{
						temView = Workspace3D.createShortcut( (ShortcutInfo)info , false );
					}
					( (IconBase3D)temView ).setItemInfo( info );
					temList.add( temView );
					temView.viewParent = view.viewParent;
					temView.x = view.x;
					temView.y = view.y;
				}
				else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
				{
					temView = WidgetIcon.createWidgetIcon( (ShortcutInfo)info , false );
					temList.add( temView );
					temView.viewParent = view.viewParent;
					temView.x = view.x;
					temView.y = view.y;
				}
				else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT )
				{
					temView = CustomShortcutIcon.createCustomShortcut( info , false );
					temList.add( temView );
					temView.viewParent = view.viewParent;
					temView.x = view.x;
					temView.y = view.y;
				}
				else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_USER_FOLDER )
				{
					( (FolderIcon3D)view ).folder_front.region.setTexture( FolderIcon3D.folderFrontWithoutShadowRegion.getTexture() );
					temList.add( view );
				}
			}
			list.clear();
			dockGroup.addItems( temList );
			temList.clear();
		}
		else
		{
			dockGroup.addItems( list );
			list.clear();
		}
		if( DefaultLayout.newHotSeatMainGroup )
		{
			mainGroup.addAllMainGroup();
		}
		return true;
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		 if( curType == TYPE_WIDGET ){
			 if(dockGroup.getShortcutCount()>=HotDockGroup.CELL_MAX_NUM_X||!(list.get( 0 ) instanceof Icon3D)||list.size()>1)
					return  false;
		 }
		isDrop = true;
		if( list.size() > 0 )
		{
			View3D view = list.get( 0 );
			if( !( view instanceof Icon3D ) )
			{
				if( view instanceof FolderIcon3D )
				{
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
		if( curType == TYPE_ICON )
		{
			if( RR.net_version )
				return false;
			return dealMainGroupOnDrop( list , x , y );
		}
		else if( curType == TYPE_WIDGET )
		{
			return dealDockGroupOnDrop( list , x , y );
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
		if( curType == TYPE_WIDGET )
		{
			if( list.size() > 0 )
			{
				
				if( list.get( 0 ) instanceof Icon3D )
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
		if( RR.net_version && !enableRollDockbar )
		{
			//return;
		}
		if( !DefaultLayout.enable_hotseat_rolling )
			return;
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
		}
		else
		{
			Model3DGroup.setRotation( 15 + 15 * scaleY );
			dockGroup.setRotation( 15 + 15 * scaleY );
		}
	}
	
	public void startMyTween()
	{
		if( DefaultLayout.WorkspaceActionGuide )
		{
			if( ActionData.getInstance().checkValidity() == ActionData.VALIDITY_LOCK_HOTMAINGROUP )
			{
				return;
			}
		}
		if( RR.net_version && !enableRollDockbar )
		{
			//	return;
		}
		if( !DefaultLayout.enable_hotseat_rolling )
			return;
		if( STATE_ROTATE == obj_state )
		{
			return;
		}
		if( animation_line != null )
		{
			return;
		}
		float anim_duration = 0.35f;
		if( iLoongLauncher.getInstance().d3dListener.d3d.isOnLongClick )
			return;
		iLoongLauncher.getInstance().d3dListener.d3d.isOnLongClick = true;
		animation_line = Timeline.createParallel();
		//if( STATE_FRONT == getHot3DState() )
		{
			setType();
			if( mScrollType == SCROLL_UP )
			{
				recordRotation = ( recordRotation + ( -R3D.hot_obj_rot_deg ) );
			}
			else
			{
				recordRotation = ( recordRotation + R3D.hot_obj_rot_deg );
			}
			this.setOrigin( this.width / 2 , this.height / 2 );
			this.setOriginZ( 0 );
			this.setRotationVector( 1 , 0 , 0 );
			animation_line.push( Tween.to( this , View3DTweenAccessor.ROTATION , anim_duration ).target( recordRotation , 0 , 0 ).ease( Linear.INOUT ) );
			if( curType != TYPE_WIDGET )
			{
				mainGroup.color.a = 0;
				mainGroup.show();
				//	mainGroup.bringToFront();
				animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.OPACITY , 0.20f ).target( 1 , 0 , 0 ).delay( 0.15f ).ease( Linear.INOUT ) );
				animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.OPACITY , 0.3f ).target( 0 , 0 , 0 ).ease( Linear.INOUT ) );
			}
			else
			{
				dockGroup.color.a = 0;
				dockGroup.show();
				//	dockGroup.bringToFront();
				animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.OPACITY , 0.20f ).target( 1 , 0 , 0 ).delay( 0.15f ).ease( Linear.INOUT ) );
				animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.OPACITY , 0.3f ).target( 0 , 0 , 0 ).ease( Linear.INOUT ) );
			}
			//			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( recordRotation , 0 , 0 ).ease( Linear.INOUT) );
			//			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , -R3D.hot_obj_trans_y , R3D.hot_obj_trans_z ).ease( Linear.INOUT ) );
			//			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( 0 , 0 , 0 ).ease( Linear.INOUT ) );
			//			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 1 , 0 , 0 ).ease(Linear.INOUT ) );
			//			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , -R3D.hot_dock_trans_y , 0 ).ease( Linear.INOUT ) );
			//			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 0 , 0 , 0 ).ease( Linear.INOUT) );
			//			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( recordRotation, 0 , 0 ).ease( Linear.INOUT ) );
			setHot3DState( STATE_ROTATE );
			animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_circular ).setCallback( this );
			MobclickAgent.onEventValue( iLoongLauncher.getInstance() , "HotSeatOverturn" , null , 24 * 60 * 60 );
		}
		//		else if( STATE_BACK == getHot3DState() )
		//		{
		//			if(mScrollType !=SCROLL_UP){
		//				recordRotation=(recordRotation+(-R3D.hot_obj_rot_deg)) %360;
		//			}
		//			else{
		//				recordRotation=(recordRotation+R3D.hot_obj_rot_deg) %360;
		//			}
		//			setType();
		//			dockGroup.show();
		//			dockGroup.bringToFront();
		//			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( recordRotation , 0 , 0 ).ease( Linear.INOUT  ) );
		//			animation_line.push( Tween.to( Model3DGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , -R3D.hot_obj_trans_z ).ease(  Linear.INOUT  ) );
		//			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.ROTATION , anim_duration ).target(recordRotation, 0 , 0 ).ease(  Linear.INOUT  ) );//
		//			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 0 , 0 , 0 ).ease(  Linear.INOUT  ) );
		//			animation_line.push( Tween.to( mainGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , -R3D.hot_obj_trans_z ).ease(  Linear.INOUT  ) );
		//			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.OPACITY , anim_duration ).target( 1 , 0 , 0 ).ease(  Linear.INOUT ) );
		//			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( recordRotation , 0 , 0 ).ease( Linear.INOUT  ) );
		//			animation_line.push( Tween.to( dockGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , 0 ).ease( Linear.INOUT ) );
		//			setHot3DState( STATE_ROTATE );
		//			animation_line.start( View3DTweenAccessor.manager ).setUserData( rot_to_front ).setCallback( this );
		//		}
		//		else
		//		{
		//			if( animation_line != null )
		//			{
		//				animation_line = null;
		//			}
		//		}
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
		return dockGroup.getShortcutGridview().findExistView( index );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( FolderIcon3D.captureCurScreen )
		{
			if( Model3DGroup != null )
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
		if( DefaultLayout.enable_new_particle )
		{
			newDrawParticle( batch );
		}
	}
	
	// teapotXu add start
	public boolean getHotseatClickPermition()
	{
		if( ( animation_line != null && !animation_line.isFinished() ) || ( animation_line_show != null && !animation_line_show.isFinished() ) )
		{
			// 当播放上翻/下翻动画时，不允许响应onClick
			Log.v( "cooee" , "----HotSeat3D --- playing animation now ------ " );
			return false;
		}
		return true;
	}
	
	// teapotXu add end
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source == animation_line )
		{
			int animKind = (Integer)( source.getUserData() );
			animation_line = null;
			if( animKind == rot_to_circular )
			{
				if( curType == TYPE_WIDGET )
				{
					setHot3DState( STATE_BACK );
					mainGroup.hide();
					//Commonly,we set the menu-page always be default page.
					mainGroup.hotFunctions.setCurrentPage( 1 );
					//					if(DefaultLayout.WorkspaceActionGuide){
					//						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					//						if( !pref.getBoolean( "first_set_default" , false ) )
					//						{
					//							if(DefaultSetting.isMyLauncher())
					//							{
					//								pref.edit().putBoolean( "first_set_default" , true ).commit();
					//							}
					//							else{
					//								if(index==0)
					//								{
					//								Intent intent = new Intent(iLoongLauncher.getInstance(), FirstActivity.class  );
					//								iLoongLauncher.getInstance().startActivity( intent );
					//								}
					//								index++;
					//							}
					//							
					//						}
					//					}	
				}
				else
				{
					if( DefaultLayout.WorkspaceActionGuide )
					{
						if( ActionHolder.getInstance() != null )
							ActionHolder.getInstance().onDockBarChanged();
					}
					setHot3DState( STATE_FRONT );
					dockGroup.hide();
				}
				dockGroup.originalTouch = false;
				mainGroup.originalTouch = false;
				recordRotation = recordRotation % 360;
				this.rotation = recordRotation;
			}
			else if( animKind == rot_to_back )
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
					dockGroup.setZ( 0 );
					if( Model3DGroup != null )
					{
						Model3DGroup.setRotation( 0 );
						Model3DGroup.setPosition( 0 , 0 );
						Model3DGroup.setZ( 0 );
					}
					dockGroup.removeVirtueFolderIcon();
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
			}
			return;
		}
		// xiatian add start //HotSeat3DShake
		if( DefaultLayout.enable_HotSeat3DShake && type == TweenCallback.COMPLETE && source == animation_line_show )
		{
			int animKind = (Integer)( source.getUserData() );
			animation_line_show = null;
			if( animKind == hide_dock )
			{
				if( dockGroup.isVisible() == true )
				{
					dockGroup.setRotation( 0 );
					dockGroup.setPosition( 0 , 0 );
					dockGroup.setZ( 0 );
					if( Model3DGroup != null )
					{
						Model3DGroup.setRotation( 0 );
						Model3DGroup.setPosition( 0 , 0 );
						Model3DGroup.setZ( 0 );
					}
					dockGroup.removeVirtueFolderIcon();
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
			}
			return;
		}
		// xiatian add end
	}
	
	public void rollDockbarToMenu()
	{
		mScrollType = SCROLL_UP;
		startMyTween();
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
	
	// xiatian add start //EffectPreview
	public void hideNoAnim()
	{
		if( this.touchable == false )
		{
			return;
		}
		stopTween();
		setPosition( 0 , -R3D.seatbar_hide_height );
		if( dockGroup.isVisible() == true )
		{
			dockGroup.setRotation( 0 );
			dockGroup.setPosition( 0 , 0 );
			dockGroup.setZ( 0 );
			if( Model3DGroup != null )
			{
				Model3DGroup.setRotation( 0 );
				Model3DGroup.setPosition( 0 , 0 );
				Model3DGroup.setZ( 0 );
			}
			dockGroup.removeVirtueFolderIcon();
		}
		super.hide();
	}
	
	// xiatian add end
	// xiatian add start //HotSeat3DShake
	public void setShake()
	{
		mIsNeedShake = true;
	}
	
	public void stopShakeTween()
	{
		if( animation_line_show != null )
		{
			animation_line_show.free();
			animation_line_show = null;
		}
	}
	
	//Jone add: alternatively ,we make the value of {@link #type} always be the other TYPE.
	private void setType()
	{
		if( curType == TYPE_ICON )
			curType = TYPE_WIDGET;
		else
			curType = TYPE_ICON;
	}
	
	private boolean showAndShake(
			float delay )
	{
		if( ( !DefaultLayout.enable_HotSeat3DShake ) || ( !mIsNeedShake ) || ( !DefaultLayout.enable_hotseat_rolling ) || ( STATE_BACK == obj_state ) || ( animation_line_show != null ) )
		{
			return false;
		}
		animation_line_show = Timeline.createParallel();
		float anim_duration = 0.75f;
		float mModel3DGroupRotationX = -R3D.hot_obj_rot_deg / 8;
		float mDockGroupPosY = -mModel3DGroupRotationX;
		animation_line_show.push( Tween.to( this , View3DTweenAccessor.POS_XY , anim_duration / 4 ).target( 0 , 0 , 0 ).ease( Linear.INOUT ).delay( delay ) );
		animation_line_show.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration / 4 ).target( mModel3DGroupRotationX , 0 , 0 ).ease( Linear.INOUT )
				.delay( anim_duration / 4 + delay ) );
		animation_line_show.push( Tween.to( dockGroup , View3DTweenAccessor.POS_XY , anim_duration / 4 ).target( 0 , mDockGroupPosY , 0 ).ease( Linear.INOUT ).delay( anim_duration / 4 + delay ) );
		animation_line_show.push( Tween.to( Model3DGroup , View3DTweenAccessor.ROTATION , anim_duration ).target( 0 , 0 , 0 ).ease( Bounce.OUT ).delay( anim_duration / 2 + delay ) );
		animation_line_show.push( Tween.to( dockGroup , View3DTweenAccessor.POS_XY , anim_duration ).target( 0 , 0 , 0 ).ease( Bounce.OUT ).delay( anim_duration / 2 + delay ) );
		animation_line_show.start( View3DTweenAccessor.manager ).setUserData( show_dock ).setCallback( this );
		mIsNeedShake = false;
		return true;
	}
	// xiatian add end
}
