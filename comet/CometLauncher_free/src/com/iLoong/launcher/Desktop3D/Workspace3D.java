package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.dockbarAdd.CooeeIcon3D;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;


public class Workspace3D extends NPageBase implements DragSource3D , DropTarget3D , MenuActionListener
{
	
	public static Workspace3D instance;
	private final String TAG = "workspace3D";
	public static final int MSG_START_DRAG = 0;
	public static final boolean NEW_CELLLAYOU = true;
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
	// public static final int ZOOM_DISTANCE = 30; //wanghongjian del
	// //enable_DefaultScene
	public static final int ZOOM_DISTANCE = 100; // wanghongjian add
													// //enable_DefaultScene
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
	private ArrayList<View3D> selectIcon = new ArrayList<View3D>();
	public static NinePatch reflectView;
	public static boolean is_longKick = false;
	private boolean needStopCover = false;
	public boolean needMoveOutMTKWidget = false;
	private int mLongClickX;
	private int mLongClickY;
	
	public Icon3D getCurIcon()
	{
		return cur_icon;
	}
	
	public void setCurIcon(
			Icon3D icon )
	{
		cur_icon = icon;
	}
	
	public static Workspace3D getInstance()
	{
		return instance;
	}
	
	public Workspace3D(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
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
		TextureRegion reflect = R3D.getTextureRegion( "workspace-reflect-view" );
		reflectView = new NinePatch( reflect , 6 , 6 , 6 , 6 );
		indicatorView = new IndicatorView( "npage_indicator" );
	}
	
	public boolean addInCurrenScreen(
			View3D child ,
			int x ,
			int y )
	{
		return addInScreen( child , getCurrentPage() , x , y , false );
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
		final CellLayout3D group = (CellLayout3D)view_list.get( screen );
		group.addView( child , item.cellTempX , item.cellTempY );
		if( child instanceof DropTarget3D )
		{
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag( child );
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		}
		group.cellCleanDropStatus();
	}
	
	public boolean removeViewInWorkspace(
			View3D view )
	{
		boolean res = true;
		if( view == null )
			return false;
		ItemInfo item = ( (IconBase3D)view ).getItemInfo();
		if( item.screen < 0 || item.screen >= view_list.size() )
			return false;
		final CellLayout3D group = (CellLayout3D)view_list.get( item.screen );
		group.removeView( view );
		return res;
	}
	
	public boolean addInScreen(
			View3D child ,
			int screen ,
			int x ,
			int y ,
			boolean insert )
	{
		boolean res = false;
		if( screen < 0 || screen >= view_list.size() )
		{
			Log.e( TAG , "The screen must be >= 0 and < " + getChildCount() + " (was " + screen + "); skipping child:" + child );
			return false;
		}
		final CellLayout3D group = (CellLayout3D)view_list.get( screen );
		child.x = x;
		child.y = y;
		// child.width = width;
		// child.height = height;
		// group.addView(child, screen, lp);
		ItemInfo item = ( (IconBase3D)child ).getItemInfo();
		if( item.cellX == -1 || item.cellY == -1 )
			res = group.addToList( child );
		else
			res = group.addView( child , item.cellX , item.cellY );
		if( res )
		{
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
		return res;
	}
	
	public View3D createShortcut(
			ShortcutInfo info )
	{
		// return createShortcut(
		// (ViewGroup3D) this.getChildAt(getCurrentScreen()), info);
		Icon3D icon;
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
		}
		else
		{
			icon = new Icon3D( info.title.toString() , R3D.findRegion( info ) );
		}
		icon.setItemInfo( info );
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
				final View3D shortcut = createShortcut( (ShortcutInfo)item );
				addInScreen( shortcut , item.screen , item.x , item.y , false );
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
						rootFolder.mFolderMIUI3D.updateTexture();
					}
					else
						rootFolder.mFolder.updateTexture();
				}
				else
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
		CellLayout3D cellLayout = this.getCurrentCellLayout();
		cellLayout.resetInfo();
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
		if( page_index >= this.getChildCount() )
			return (CellLayout3D)getChildAt( 0 );
		return (CellLayout3D)getChildAt( page_index );
	}
	
	@Override
	public void addView(
			View3D actor )
	{
		// TODO Auto-generated method stub
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
	
	// @Override
	// protected boolean touchDown (float x, float y, int pointer) {
	// Log.v("touch"," touchDown:" + name +" x:" + x + " y:"+ y);
	// return super.touchDown(x, y, pointer);
	// }
	//
	// @Override
	// protected boolean touchDragged (float x, float y, int pointer) {
	// Log.v("touch"," touchDragged:" + name +" x:" + x + " y:"+ y);
	// return super.touchDragged(x, y, pointer);
	// }
	// @Override
	// protected boolean touchUp (float x, float y, int pointer) {
	// return super.touchUp(x, y, pointer);
	// }
	@SuppressWarnings( "unchecked" )
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		boolean dropRes = true;
		// 这里有报空指针错�?!!!!
		List<View3D> iconGroupInput = new ArrayList<View3D>();
		for( View3D view : list )
		{
			if( view instanceof ViewCircled3D )
			{
				// view.x = x;
				// view.y = y;
				iconGroupInput.add( (View3D)view );
			}
			// if (view instanceof ViewGroupCircled3D)
			// {
			// view.x = x;
			// view.y = y;
			// iconGroupInput.add((View3D) view);
			// }
		}
		int index;
		CellLayout3D group;
		if( dropScreen != -1 )
		{
			index = dropScreen;
			group = (CellLayout3D)getChildAt( dropScreen );
			dropScreen = -1;
		}
		else
		{
			index = this.getCurrentScreen();
			group = (CellLayout3D)getChildAt( this.getCurrentScreen() );
		}
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
		if( ( actor = findView( "IconTrans3D" ) ) != null )
		{
			( (IconTrans3D)actor ).DealButtonOKDown();
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
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if( DefaultLayout.show_sensor )
		{
			iLoongLauncher.getInstance().sensorListenerPause();
		}
		// xiatian add end
		super.hide();
		SendMsgToAndroid.sendHideWorkspaceMsg();
		clearDragObjs();
		if( ClingManager.getInstance().folderClingFired )
			SendMsgToAndroid.sendRefreshClingStateMsg();
	}
	
	public void show()
	{
		// if (!isVisible()){
		if( !isVisible() && this.scaleX == 1 && this.getUser() == 0 && this.color.a != 0 )
		{
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		if( needStopCover )
		{
			SendMsgToAndroid.sendStopCoverMTKWidgetMsg();
			needStopCover = false;
		}
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if( DefaultLayout.show_sensor )
		{
			iLoongLauncher.getInstance().sensorListenerResume();
		}
		// xiatian add end
		super.show();
		if( ClingManager.getInstance().folderClingFired )
			SendMsgToAndroid.sendRefreshClingStateMsg();
	}
	
	@Override
	protected void finishAutoEffect()
	{
		super.finishAutoEffect();
		if( xScale == 0 && mVelocityX == 0 )
		{
			SendMsgToAndroid.sendMoveInMTKWidgetMsg();
			SendMsgToAndroid.sendShowWorkspaceMsg();
			// for(View3D view : children){
			// if(view instanceof CellLayout3D){
			// ((CellLayout3D)view).hideWidget();
			// }
			// }
			if( DefaultLayout.keypad_event_of_focus )
			{
				CellLayout3D cellLayout = this.getCurrentCellLayout();
				cellLayout.changeFocus();
				cellLayout.setVisible();
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
		if( ret == true )
		{
			return ret;
		}
		this.setTag( new Vector2( x , Utils3D.getScreenHeight() - y ) );
		return viewParent.onCtrlEvent( this , MSG_CLICK_SPACE );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// wanghongjian add start //enable_DefaultScene
		if( pointer > 0 )
		{
			this.releaseFocus();
			return true;
		}// 双指取消focus
			// wanghongjian add end
		Root3D.getInstance().setWidgetLiveState( 1 );
		SendMsgToAndroid.sendHideWorkspaceMsg();
		needMoveOutMTKWidget = true;
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
		if( workspaceOnLong )
			mVelocityX = 0;
		boolean bool = super.onTouchUp( x , y , pointer );
		Log.d( "test" , " workspace3D onTouchUp" );
		workspaceOnLong = false;
		// Log.d("test12345", "onTouchup workspaceOnlong false");
		if( xScale == 0 && mVelocityX == 0 && !circleSomething3D.isVisible() && !dropAnimating )
		{
			SendMsgToAndroid.sendMoveInMTKWidgetMsg();
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		return bool;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// removed by zhenNan.ye 
		//		if (workspaceOnLong == true) {
		//			return true;
		//		}
		clearDragObjs();
		SendMsgToAndroid.sendMoveOutMTKWidgetMsg();
		if( needMoveOutMTKWidget )
		{
			needMoveOutMTKWidget = false;
			SendMsgToAndroid.sendMoveOutMTKWidgetMsg();
		}
		// indicatorView.setAlpha(1.0f);
		// if(indicatorView.getIndicatorTween() != null &&
		// !indicatorView.getIndicatorTween().isFinished()){
		// indicatorView.getIndicatorTween().free();
		// indicatorView.setIndicatorTween(null);
		// }
		return super.scroll( x , y , deltaX , deltaY );
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
	
	public void onHomeKey(
			boolean alreadyOnHome )
	{
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		// if(keycode == KeyEvent.KEYCODE_SEARCH)
		// {
		// viewParent.onCtrlEvent(this, MSG_GLOBAL_SEARCH);
		// return true;
		// }
		return super.keyUp( keycode );
	}
	
	//
	// public ArrayList<View3D> getDragObjects() {
	// return dragObjects;
	// }
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
					releaseFocus();
					this.setTag( icon.getTag() );
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Icon3D.MSG_ICON_SELECTED:
					dragObjects.add( icon );
					return true;
				case Icon3D.MSG_ICON_UNSELECTED:
					dragObjects.remove( icon );
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
					releaseFocus();
					Rect rect = (Rect)widget.getTag();
					DragLayer3D.dragStartX = rect.right;
					DragLayer3D.dragStartY = rect.bottom;
					this.setTag( new Vector2( rect.left , rect.top ) );
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
					if( widget != null )
					{
						dragObjects.add( widget );
						this.setTag( widget.getTag() );
					}
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		else if( sender instanceof FolderIcon3D )
		{
			FolderIcon3D foldericon = (FolderIcon3D)sender;
			switch( event_id )
			{
			// case FolderIcon3D.MSG_FOLDERICON_OPEN:
			// CellLayout3D layout = getCurrentCellLayout();
			// int count = layout.getChildCount();
			// for(int i = 0; i < count; i++){
			// View3D view = layout.getChildAt(i);
			// if(view != foldericon){
			// view.stopTween();
			// view.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.5f, 0,
			// 0, 0).setCallback(this);
			// }
			// }
			// return true;
			// case FolderIcon3D.MSG_FOLDERICON_CLOSE:
			// CellLayout3D layout1 = getCurrentCellLayout();
			// int count1 = layout1.getChildCount();
			//
			// for(int i = 0; i < count1; i++){
			// View3D view = layout1.getChildAt(i);
			// if(view != foldericon){
			// view.show();
			// view.stopTween();
			// view.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.5f,
			// 1f, 1f, 0);
			// }
			// }
			// return true;
				case FolderIcon3D.MSG_FOLDERICON3D_LONGCLICK:
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
					if( dragObjects.size() == 0 )
					{
						dragObjects.add( foldericon );
					}
					this.setTag( foldericon.getTag() );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		else if( sender instanceof circlePopWnd3D )
		{
			delCircleSomehing.dealEvent_circlePopWnd3D( event_id );
			return true;
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		Root3D.getInstance().setWidgetLiveState( 0 );
		if( dropAnimating && source instanceof Tween )
		{
			dropAnimating = false;
			if( xScale == 0 && mVelocityX == 0 && !circleSomething3D.isVisible() )
				SendMsgToAndroid.sendShowWorkspaceMsg();
			Object target = ( (Tween)source ).getTarget();
			if( target instanceof Widget2DShortcut )
			{
				this.setTag( target );
				viewParent.onCtrlEvent( this , MSG_ADD_WIDGET );
				( (View3D)target ).remove();
			}
		}
		super.onEvent( type , source );
	}
	
	public static void checkBoundary(
			View3D target )
	{
		ViewGroup3D parent = target.getParent();
		if( parent != null )
		{
			// if(target.x < PageIndicator3D.pageIndicatorWidth*0.6f && target.y
			// < PageIndicator3D.pageIndicatorHeight*0.6f){
			// if((PageIndicator3D.pageIndicatorWidth*0.6f - target.x) <
			// (PageIndicator3D.pageIndicatorHeight*0.6f - target.y))
			// target.x = PageIndicator3D.pageIndicatorWidth*0.6f;
			// else
			// target.y = PageIndicator3D.pageIndicatorHeight*0.6f;
			// }
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
		// TODO Auto-generated method stub
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
			getCurrentCellLayout().resetCurrFocus();
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( super.onLongClick( x , y ) )
			return true;
		//zqh add start
		Root3D.clickPoint.set( x , y );
		this.toAbsolute( Root3D.clickPoint );
		Log.v( "Root3D" , "PP X " + Root3D.clickPoint.x + " PP Y" + Root3D.clickPoint.y );
		//zqh add end 
		this.setTag( new Vector2( x , y ) );
		workspaceOnLong = true;
		// Log.d("test12345", "onLongClick workspaceOnlong true");
		//zqh add start 
		//release focus here when long clicked, make sure  new layout can get focus later.. 
		this.releaseFocus();
		//zqh add end
		mLongClickX = (int)x;
		mLongClickY = (int)y;
		return viewParent.onCtrlEvent( this , MSG_LONGCLICK );
	}
	
	@Override
	public void setActionListener()
	{
		// TODO Auto-generated method stub
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_DESKTOP_SETTINGS , this );
	}
	
	//zqh add , set workspace fling effect
	public void setDesktopEffectType(
			int type )
	{
		setEffectType( type );
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		// TODO Auto-generated method stub
		if( bundle.containsKey( "desktopeffects" ) )
		{
			setEffectType( bundle.getInt( "desktopeffects" ) );
		}
		if( bundle.containsKey( "circled" ) )
		{
			circleAble = bundle.getInt( "circled" );
		}
		// if (bundle.containsKey("vibrator") )
		// setEffectType(bundle.getInt("vibrator"));
	}
	
	@Override
	public void onDegreeChanged()
	{
		if( viewParent != null && this.isVisible() )
			viewParent.onCtrlEvent( this , Root3D.MSG_SET_WALLPAPER_OFFSET );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		y = this.getUser();
		// Log.d("launcher", "y="+y);
		if( iLoongLauncher.getInstance().popResult > CIRCLE_POP_NONE_ACTION )
		{
			// Log.v("CircleSomething","CellLayout3D draw popResult="+iLoongLauncher.getInstance().popResult);
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
		//		float scaleZ = 1f;
		//		if (xScale != 0f) {
		//			scaleZ = 0.1f;
		//		}
		//		setScaleZ(scaleZ);
		super.draw( batch , parentAlpha );
		if( ClingManager.getInstance().folderClingFired )
			SendMsgToAndroid.sendRefreshClingStateMsg();
		SendMsgToAndroid.sendRefreshClingStateMsg();
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		Root3D root = (Root3D)this.viewParent;
		if( root.getDesktopEdit().isVisible() )
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
				// return true; //wanghongjian del //enable_DefaultScene
				return false; // wanghongjian add //enable_DefaultScene
			}
			if( zoomDst >= ZOOM_DISTANCE )
			{
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
				viewParent.onCtrlEvent( this , MSG_PAGE_SHOW_EDIT );
				iLoongLauncher.getInstance().cleaWidgetStatus( page_index );
				Gdx.graphics.requestRendering();
				return true;
			}
			else
			{
				if( getCurrentCellLayout().multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer ) == true )
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
	protected void initView()
	{
		Root3D root = (Root3D)this.viewParent;
		if( root != null && root.getDesktopEdit().isVisible() )
		{
			return;
		}
		super.initView();
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
		// TODO Auto-generated method stub
		removeIconGroupAndPopView();
		super.setCurrentPage( index );
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( Workspace3D.NEW_CELLLAYOU )
		{
			CellLayout3D group = (CellLayout3D)getChildAt( this.getCurrentScreen() );
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
			return false;
	}
	
	public void addToDesk()
	{
		CellLayout3D celllayout = getCurrentCellLayout();
		celllayout.setScreen( getCurrentScreen() );
		celllayout.setScaleAnim = true;
		celllayout.addView( selectIcon , mLongClickX , mLongClickY );
	}
	
	public int getCurCellIconCount()
	{
		int count = 0;
		CellLayout3D celllayout = getCurrentCellLayout();
		for( int i = 0 ; i < celllayout.mCountX ; i++ )
		{
			for( int j = 0 ; j < celllayout.mCountY ; j++ )
			{
				if( celllayout.mOccupied[i][j] == false )
				{
					count++;
				}
			}
		}
		return count;
	}
	
	public void getSelectShortcut(
			List<Icon3D> list )
	{
		selectIcon.clear();
		if( list != null )
		{
			for( int i = 0 ; i < list.size() ; i++ )
			{
				Icon3D icon = list.get( i );
				ShortcutInfo itemInfo = (ShortcutInfo)icon.getItemInfo();
				if( itemInfo instanceof ShortcutInfo )
				{
					if( ( (ShortcutInfo)itemInfo ).intent != null )
					{
						TextureRegion cregion = new TextureRegion( icon.region , 0 , 0 , icon.region.getRegionWidth() , icon.region.getRegionHeight() );
						if( itemInfo.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
						{
							WidgetIcon v = new WidgetIcon( icon.name , cregion );
							v.setItemInfo( itemInfo );
							v.setSize( icon.getWidth() , icon.getHeight() );
							selectIcon.add( v );
						}
						else
						{
							Icon3D v = new Icon3D( icon.name , cregion );
							v.setItemInfo( itemInfo );
							v.setSize( icon.getWidth() , icon.getHeight() );
							selectIcon.add( v );
						}
					}
				}
			}
		}
	}
	
	public void createVirturFolder(
			FolderIcon3D folderIcon3D )
	{
		if( selectIcon.size() <= 0 )
			return;
		ItemInfo info = ( (IconBase3D)folderIcon3D ).getItemInfo();
		info.screen = getCurrentScreen();
		info.x = (int)folderIcon3D.x;
		info.y = (int)folderIcon3D.y;
		Root3D.addOrMoveDB( info );
		iLoongLauncher.getInstance().addFolderInfoToSFolders( (UserFolderInfo)info );
		( (IconBase3D)folderIcon3D ).setItemInfo( info );
		folderIcon3D.addFolderNode( selectIcon );
		CellLayout3D celllayout = getCurrentCellLayout();
		celllayout.setScreen( getCurrentScreen() );
		ArrayList<View3D> list = new ArrayList<View3D>();
		list.add( folderIcon3D );
		celllayout.addView( list , mLongClickX , mLongClickY );
		info.x = (int)folderIcon3D.x;
		info.y = (int)folderIcon3D.y;
		String folderName = Root3D.getInstance().getDesktopEdit().foldername;
		if( !folderIcon3D.mInfo.title.equals( folderName ) )
		{
			folderIcon3D.onTitleChanged( folderName );
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				if( folderName.endsWith( "x.z" ) )
				{
					int length = folderName.length();
					if( length > 3 )
					{
						folderName = folderName.substring( 0 , length - 3 );
					}
				}
				folderIcon3D.mFolderMIUI3D.setEditText( folderName );
			}
			else
			{
				folderIcon3D.mFolder.setEditText( folderName );
			}
		}
		this.setTag( folderIcon3D );
		viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		folderIcon3D.mInfo.folderFrom = "";
	}
	
	public void getSelectApp(
			List<Icon3D> list )
	{
		selectIcon.clear();
		if( list != null )
		{
			for( int i = 0 ; i < list.size() ; i++ )
			{
				Icon3D icon = CooeeIcon3D.icons.get( i ).clone();
				ShortcutInfo itemInfo = (ShortcutInfo)icon.getItemInfo();
				if( itemInfo instanceof ShortcutInfo )
				{
					if( ( (ShortcutInfo)itemInfo ).intent != null )
					{
						TextureRegion cregion = new TextureRegion( icon.region , 0 , 0 , icon.region.getRegionWidth() , icon.region.getRegionHeight() );
						Icon3D v = new Icon3D( icon.name , cregion );
						v.setItemInfo( itemInfo );
						v.setSize( icon.getWidth() , icon.getHeight() );
						selectIcon.add( v );
					}
				}
			}
		}
	}
	
	public void onDragOverLeave()
	{
		if( page_index < this.getChildCount() && page_index >= 0 )
		{
			CellLayout3D group = (CellLayout3D)getChildAt( page_index );
			group.onDropLeave();
		}
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		// TODO Auto-generated method stub
		return dragObjects;
	}
	
	@Override
	public void setScaleZ(
			float f )
	{
		// TODO Auto-generated method stub
		for( View3D child : children )
			if( child instanceof CellLayout3D )
			{
				child.setScaleZ( f );
			}
	}
}
