package com.iLoong.launcher.desktopEdit;


import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.dockbarAdd.AddApp;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class DesktopEdit extends ViewGroup3D implements DragSource3D , DropTarget3D
{
	
	public static final String DESKTOP_EDIT_PREFERENCES = "desktopEditPreferences";
	public static final String KEY_EDIT_MODE = "desktopEditMode";
	public static final float ESPINON = 0.999999f;
	public static final int MSG_DESKTOP_EDIT_MODE_ENTER = 0;
	public static final int MSG_DESKTOP_EDIT_MODE_EXIT = 1;
	public static final int MSG_DESKTOP_EDIT_SET_HOME = 2;
	public static final int MSG_DESKTOP_EDIT_APPEND_PAGE = 3;
	public static final int MSG_DESKTOP_EDIT_DELETE_PAGE = 4;
	public static final int MSG_DESKTOP_EDIT_PAGE_POSITION_CHANGE = 5;
	public static final int DESKTOP_EDIT_MODE_PLANE = 0; // 平面编辑模式
	public static final int DESKTOP_EDIT_MODE_CYLINDER = 1; // 圆柱体编辑模式
	public static int curDesktopEditMode;
	public static EditModeChangeView editModeChangeView;
	public static float changeIconSize = 73f;
	public static final int AUTO_MOVE_LEFT = -1;
	public static final int AUTO_MOVE_RIGHT = 1;
	public static final int AUTO_MOVE_STOP = 0;
	public static int autoMoveStatus;
	public static long autoMoveStayTime;
	public static boolean canAutoMove;
	public static float dragX;
	public static float dragY;
	public static boolean bDragActionForIcon;
	public static boolean bDragActionForPage;
	private static final String EDIT_MODE_VIEW_NAME = "editModeView";
	private static DesktopEdit instance;
	public static float scaleWidth;
	public static float scaleHeight;
	public static final float scaleFactor = 0.7f;// 0.586f;
	public static int PAGE_COUNT_MAX = 9;
	public static float page_cell_padding_top = 30f;
	public static float page_bg_gap_x = 32f;
	private ArrayList<View3D> cellLayoutList;
	private ArrayList<View3D> pageList;
	private int homePageIndex;
	private int curPageIndex;
	private int pageWidth;
	private int pageHeight;
	private PageContainer dragPage;
	public int toChangePageIndex;
	private ArrayList<View3D> dragList = new ArrayList<View3D>();
	private ArrayList<ItemInfo> shortcutItem = new ArrayList<ItemInfo>();
	
	public DesktopEdit()
	{
		// TODO Auto-generated constructor stub
	}
	
	public DesktopEdit(
			String name )
	{
		// TODO Auto-generated constructor stub
		super( name );
		instance = this;
		PAGE_COUNT_MAX = DefaultLayout.default_workspace_pagecount_max;
		pageWidth = (int)( Utils3D.getScreenWidth() * scaleFactor );
		pageHeight = (int)( Utils3D.getScreenHeight() * scaleFactor );
		pageList = new ArrayList<View3D>();
		scaleWidth = Utils3D.getScreenWidth() / 720f;
		scaleHeight = Utils3D.getScreenHeight() / 1230f;
		page_cell_padding_top *= scaleHeight;
		page_bg_gap_x *= scaleWidth;
		changeIconSize *= scaleWidth;
	}
	
	public static DesktopEdit getInstance()
	{
		if( instance != null )
		{
			return instance;
		}
		return null;
	}
	
	public void setupDesktopEditMode(
			ArrayList<View3D> workspaceList ,
			int homePage ,
			int currentPage )
	{
		SharedPreferences preferences = iLoongLauncher.getInstance().getSharedPreferences( DESKTOP_EDIT_PREFERENCES , Activity.MODE_PRIVATE );
		curDesktopEditMode = preferences.getInt( KEY_EDIT_MODE , DESKTOP_EDIT_MODE_CYLINDER );
		cellLayoutList = workspaceList;
		this.homePageIndex = homePage;
		this.curPageIndex = currentPage;
		setupPageList();
		editModeChangeView = new EditModeChangeView( "pageEditModeView" );
		float posX = this.getWidth() - changeIconSize;
		float posY = this.getHeight() - changeIconSize;
		editModeChangeView.setPosition( posX , posY );
		editModeChangeView.setSize( changeIconSize , changeIconSize );
		editModeChangeView.show();
		this.removeAllViews();
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editMode = new PlaneEditMode( EDIT_MODE_VIEW_NAME , this );
			editMode.setupEditPageGroup( false );
			this.addView( editMode );
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode editMode = new CylinderEditMode( EDIT_MODE_VIEW_NAME , this );
			editMode.setupEditPageGroup( false );
			this.addView( editMode );
		}
	}
	
	private void setupPageList()
	{
		for( int i = 0 ; i < cellLayoutList.size() ; i++ )
		{
			View3D cell = cellLayoutList.get( i );
			ViewInfoHolder holder = new ViewInfoHolder();
			holder.getInfo( cell );
			holder.parent = cell.getParent();
			cell.setTag( holder );
			cell.setScale( scaleFactor , scaleFactor );
			cell.setPosition( 0 , -page_cell_padding_top );
			cell.setOrigin( 0 , 0 );
			cell.setRotation( 0 );
			cell.show();
			cell.touchable = false;
			PageContainer editPage = new PageContainer( "editPage" , pageWidth , pageHeight );
			editPage.buildEditPage( cell , i );
			pageList.add( i , editPage );
		}
		if( pageList.size() < PAGE_COUNT_MAX )
		{
			PageContainer addPage = new PageContainer( "addPage" , pageWidth , pageHeight );
			addPage.buildAddPage( cellLayoutList.size() );
			pageList.add( cellLayoutList.size() , addPage );
		}
	}
	
	private void changeEditMode()
	{
		curDesktopEditMode ^= 1;
		SharedPreferences preferences = iLoongLauncher.getInstance().getSharedPreferences( DESKTOP_EDIT_PREFERENCES , Activity.MODE_PRIVATE );
		preferences.edit().putInt( KEY_EDIT_MODE , curDesktopEditMode ).commit();
		for( int i = 0 ; i < pageList.size() ; i++ )
		{
			View3D page = pageList.get( i );
			page.setRotation( 0 );
			page.setOriginZ( 0 );
			page.stopAllTween();
			ViewGroup3D cell = (ViewGroup3D)( (ViewGroup3D)page ).findView( "celllayout" );
			if( cell != null )
			{
				for( int k = 0 ; k < cell.getChildCount() ; k++ )
				{
					View3D view = cell.getChildAt( k );
					view.color.a = 1f;
				}
			}
		}
		this.removeAllViews();
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editMode = new PlaneEditMode( EDIT_MODE_VIEW_NAME , this );
			editMode.setupEditPageGroup( true );
			this.addView( editMode );
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode editMode = new CylinderEditMode( EDIT_MODE_VIEW_NAME , this );
			editMode.setupEditPageGroup( true );
			this.addView( editMode );
		}
	}
	
	private void handleAppendAnimtion(
			View3D newPage )
	{
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				editView.handleAppendAnimtion( newPage );
			}
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode cylinderEdit = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( cylinderEdit != null )
			{
				cylinderEdit.handleAppendAnimtion( newPage );
			}
		}
	}
	
	private void handleDeleteAnimtion(
			View3D deletePage )
	{
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				editView.handleDeleteAnimtion( deletePage );
			}
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode cylinderEdit = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( cylinderEdit != null )
			{
				cylinderEdit.handleDeleteAnimtion( deletePage );
			}
		}
	}
	
	private void onAppendPage()
	{
		View3D cell = cellLayoutList.get( cellLayoutList.size() - 1 );
		ViewInfoHolder holder = new ViewInfoHolder();
		holder.getInfo( cell );
		holder.parent = cell.getParent();
		cell.setTag( holder );
		cell.setScale( scaleFactor , scaleFactor );
		cell.setPosition( 0 , 0 );
		cell.setOrigin( 0 , 0 );
		cell.setRotation( 0 );
		cell.touchable = false;
		cell.show();
		PageContainer newPage = new PageContainer( "editPage" , pageWidth , pageHeight );
		newPage.buildEditPage( cell , pageList.size() - 1 );
		handleAppendAnimtion( newPage );
	}
	
	public void deletePage()
	{
		if( ( (ViewGroup3D)cellLayoutList.get( dragPage.pageIndex ) ).getChildCount() > 0 )
		{
			SendMsgToAndroid.sendDeletePageMsg();
		}
		else
		{
			exeDeletePage();
		}
	}
	
	public void exeDeletePage()
	{
		iLoongLauncher.getInstance().getD3dListener().getTrashIcon().hide();
		if( homePageIndex > dragPage.pageIndex )
		{
			homePageIndex--;
			viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_SET_HOME );
		}
		else if( homePageIndex == dragPage.pageIndex )
		{
			PageContainer homePage = (PageContainer)pageList.get( homePageIndex );
			homePage.isHomePage = true;
			View3D homeIcon = homePage.findView( "pageHomeView" );
			if( homeIcon != null )
			{
				homeIcon.region = R3D.getTextureRegion( R3D.desktopEdit_page_cur_home );
			}
		}
		handleDeleteAnimtion( dragPage );
	}
	
	public void cancelExeDeletePage()
	{
		iLoongLauncher.getInstance().getD3dListener().getTrashIcon().hide();
		editModeChangeView.stopAllTween();
		editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 1 , 0 , 0 );
		DragLayer3D dragLayer = iLoongLauncher.getInstance().getD3dListener().getDragLayer();
		onDrop( dragLayer.getDragList() , 0 , 0 );
		dragLayer.removeAllViews();
		dragLayer.hide();
	}
	
	int page_add_type = 0;
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// TODO Auto-generated method stub
		if( sender instanceof PlaneEditMode )
		{
			if( event_id == PlaneEditMode.MSG_PLANE_EDIT_MODE_EXIT )
			{
				prepareHide();
				viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_MODE_EXIT );
			}
			if( event_id == PlaneEditMode.MSG_PLANE_PAGE_ADD_COMPLETE )
			{
				if( page_add_type == 1 )
				{
					page_add_type = 0;
				}
				else if( page_add_type == 2 )
				{
					page_add_type = 0;
				}
				else if( page_add_type == 3 )
				{
					page_add_type = 0;
				}
				else if( page_add_type == 4 )
				{
					page_add_type = 0;
				}
			}
		}
		if( sender instanceof CylinderEditMode )
		{
			if( event_id == CylinderEditMode.MSG_CYLINDER_EDIT_MODE_EXIT )
			{
				prepareHide();
				viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_MODE_EXIT );
			}
			if( event_id == CylinderEditMode.MSG_CYLINDER_PAGE_ADD_COMPLETE )
			{
			}
		}
		if( sender instanceof PageContainer )
		{
			switch( event_id )
			{
				case PageContainer.MSG_PAGE_APPEND:
					viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_APPEND_PAGE );
					onAppendPage();
					return true;
				case PageContainer.MSG_PAGE_SET_HOME:
					PageContainer oldhomePage = (PageContainer)pageList.get( getHomePageIndex() );
					oldhomePage.isHomePage = false;
					View3D pageHomeView = oldhomePage.findView( "pageHomeView" );
					if( pageHomeView != null )
					{
						pageHomeView.region = R3D.getTextureRegion( R3D.desktopEdit_page_home );
					}
					setHomePageIndex( ( (PageContainer)sender ).pageIndex );
					viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_SET_HOME );
					return true;
				case PageContainer.MSG_PAGE_LONGCLICK:
					bDragActionForPage = true;
					dragPage = (PageContainer)sender;
					toChangePageIndex = dragPage.pageIndex;
					pageList.remove( dragPage );
					for( int i = dragPage.pageIndex ; i < pageList.size() ; i++ )
					{
						PageContainer page = (PageContainer)pageList.get( i );
						page.pageIndex = i;
					}
					dragList.clear();
					dragList.add( dragPage );
					this.setTag( dragPage.getTag() );
					viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					return true;
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	public void prepareHide()
	{
		if( cellLayoutList != null )
		{
			for( int i = 0 ; i < cellLayoutList.size() ; i++ )
			{
				View3D cell = cellLayoutList.get( i );
				ViewInfoHolder tag = (ViewInfoHolder)cell.getTag();
				ViewGroup3D parent = tag.parent;
				if( parent != null )
					parent.addView( cell );
				tag.applyInfo( cell );
			}
		}
		if( pageList != null )
		{
			pageList.clear();
		}
		View3D editModeView = this.findView( EDIT_MODE_VIEW_NAME );
		if( editModeView != null )
		{
			editModeView.remove();
		}
	}
	
	public ArrayList<View3D> getCelllayoutList()
	{
		return this.cellLayoutList;
	}
	
	public ArrayList<View3D> getPageList()
	{
		return this.pageList;
	}
	
	public int getHomePageIndex()
	{
		return this.homePageIndex;
	}
	
	public void setHomePageIndex(
			int homePageIndex )
	{
		this.homePageIndex = homePageIndex;
	}
	
	public int getCurrentPageIndex()
	{
		return this.curPageIndex;
	}
	
	public void setCurrentPageIndex(
			int currentPage )
	{
		this.curPageIndex = currentPage;
	}
	
	public int nextPageIndex()
	{
		return( this.curPageIndex == getPageNumIncludeAddPage() - 1 ? 0 : this.curPageIndex + 1 );
	}
	
	public int prePageIndex()
	{
		return( this.curPageIndex == 0 ? getPageNumIncludeAddPage() - 1 : this.curPageIndex - 1 );
	}
	
	public int getPageNumIncludeAddPage()
	{
		return pageList.size();
	}
	
	public int getPageWidth()
	{
		return this.pageWidth;
	}
	
	public int getPageHeight()
	{
		return this.pageHeight;
	}
	
	public PageContainer getDragPage()
	{
		return this.dragPage;
	}
	
	public void dragActionFinish()
	{
		iLoongLauncher.getInstance().getD3dListener().getTrashIcon().hide();
		editModeChangeView.stopAllTween();
		editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 1 , 0 , 0 );
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				editView.dragActionFinish();
			}
		}
		else
		{
			CylinderEditMode editMode = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editMode != null )
			{
				editMode.dragActionFinish();
			}
		}
	}
	
	public void exitEditModeExternal()
	{
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				editView.exitExternalAnim();
			}
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode editMode = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editMode != null )
			{
				editMode.exitExternalAnim();
			}
		}
	}
	
	public void exitEditMode()
	{
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				editView.exitPlaneEditMode();
			}
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode editMode = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editMode != null )
			{
				editMode.exitCylinderEditMode();
			}
		}
	}
	
	public boolean isAnimation()
	{
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				return editView.isAnimation();
			}
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode editMode = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editMode != null )
			{
				return editMode.isAnimation();
			}
		}
		return false;
	}
	
	public void stopAllAnimation()
	{
		if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
		{
			PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editView != null )
			{
				editView.stopAllAnimation();
			}
		}
		else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
		{
			CylinderEditMode editMode = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
			if( editMode != null )
			{
				editMode.stopAllAnimation();
			}
		}
	}
	
	public class EditModeChangeView extends View3D
	{
		
		public EditModeChangeView(
				String name )
		{
			// TODO Auto-generated constructor stub
			super( name );
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			if( pointer > 0 )
			{
				return false;
			}
			requestDark();
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			if( pointer > 0 )
			{
				return false;
			}
			releaseDark();
			return true;
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			changeEditMode();
			return true;
		}
		
		private void hilightEditModeView(
				boolean hilight )
		{
			if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
			{
				if( hilight )
				{
					editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_cylinder_hilight );
				}
				else
				{
					editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_cylinder );
				}
			}
			else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
			{
				if( hilight )
				{
					editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_plane_hilight );
				}
				else
				{
					editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_plane );
				}
			}
		}
		
		public void requestDark()
		{
			hilightEditModeView( true );
		}
		
		@Override
		public void releaseDark()
		{
			// TODO Auto-generated method stub
			hilightEditModeView( false );
		}
	}
	
	class ViewInfoHolder
	{
		
		public ViewGroup3D parent;
		public float rotation;
		public float scaleX;
		public float scaleY;
		public float originX;
		public float originY;
		public float x;
		public float y;
		public float width;
		public float height;
		
		public ViewInfoHolder()
		{
		}
		
		public void getInfo(
				View3D view )
		{
			this.x = view.x;
			this.y = view.y;
			this.width = view.width;
			this.height = view.height;
			this.originX = view.originX;
			this.originY = view.originY;
			this.scaleX = view.scaleX;
			this.scaleY = view.scaleY;
			this.rotation = view.rotation;
		}
		
		public void applyInfo(
				View3D v )
		{
			v.x = this.x;
			v.y = this.y;
			v.width = this.width;
			v.height = this.height;
			v.originX = this.originX;
			v.originY = this.originY;
			v.scaleX = this.scaleX;
			v.scaleY = this.scaleY;
			v.rotation = this.rotation;
		}
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( list.get( 0 ) instanceof PageContainer )
		{
			PageContainer dropPage = (PageContainer)list.get( 0 );
			dropPage.pageIndex = getCurrentPageIndex();
			pageList.add( dropPage.pageIndex , dropPage );
			for( int i = 0 ; i < pageList.size() ; i++ )
			{
				PageContainer page = (PageContainer)pageList.get( i );
				page.pageIndex = i;
				if( page.isHomePage )
				{
					homePageIndex = i;
				}
			}
			if( curDesktopEditMode == DESKTOP_EDIT_MODE_PLANE )
			{
				PlaneEditMode editView = (PlaneEditMode)this.findView( EDIT_MODE_VIEW_NAME );
				if( editView != null )
				{
					editView.onDrop( dropPage );
				}
			}
			else if( curDesktopEditMode == DESKTOP_EDIT_MODE_CYLINDER )
			{
				CylinderEditMode cylinderEditMode = (CylinderEditMode)this.findView( EDIT_MODE_VIEW_NAME );
				if( cylinderEditMode != null )
				{
					cylinderEditMode.onDrop( dropPage );
				}
			}
			viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_SET_HOME );
			viewParent.onCtrlEvent( this , MSG_DESKTOP_EDIT_PAGE_POSITION_CHANGE );
		}
		return true;
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		dragX = x;
		dragY = y;
		return true;
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
		return dragList;
	}
	
	public void addApps(
			ArrayList<ApplicationInfo> apps )
	{
		AddApp addEditMode = (AddApp)findView( "appAdd" );
		if( addEditMode != null )
		{
			addEditMode.startapp();
		}
	}
	
	public void getShortcut(
			ArrayList<ItemInfo> apps )
	{
		shortcutItem = apps;
	}
	
	public String foldername;
	
	public void setFoldername(
			String foldername )
	{
		this.foldername = foldername;
	}
	
	private ArrayList<View3D> selectIcon = new ArrayList<View3D>();
	
	public void showEditMode()
	{
		com.iLoong.launcher.dockbarAdd.ImageView3D bg = (com.iLoong.launcher.dockbarAdd.ImageView3D)findView( "editmodebg" );
		if( bg != null )
		{
			bg.hide();
		}
	}
	
	public void hideEditMode()
	{
		com.iLoong.launcher.dockbarAdd.ImageView3D bg = (com.iLoong.launcher.dockbarAdd.ImageView3D)findView( "editmodebg" );
		if( bg == null )
		{
			TextureRegion texture = R3D.getTextureRegion( R3D.dockbar_editmode_add_bg );
			bg = new com.iLoong.launcher.dockbarAdd.ImageView3D( "editmodebg" , texture );
			bg.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
			bg.x = Utils3D.getScreenWidth() / 2 - bg.getWidth() / 2;
			bg.y = Utils3D.getScreenHeight() / 2 - bg.getHeight() / 2;
			bg.show();
			this.addView( bg );
		}
		else
		{
			bg.show();
		}
	}
	
	Widget3D widget = null;
	
	public void setWidget(
			Widget3D dragObj )
	{
		widget = dragObj;
	}
	
	public Widget3D getWidget()
	{
		// ArrayList<View3D> list=new ArrayList<View3D>();
		// list.add(widget);
		return widget;
	}
	
	public void addWidget()
	{
		this.addView( widget );
	}
}
