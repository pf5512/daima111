package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import android.os.Bundle;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PageContainer3D extends ViewGroup3D implements MenuActionListener
{
	
	public PageEdit3D pageEdit;
	public PageSelect3D pageSelect;
	public ArrayList<View3D> celllayoutList;
	private View3D pageModeIcon;
	public static float pageModeIconWidth = 55f;
	public static float pageModeIconHeight = 55f;
	private float returnIconWidth = 60f;
	private float returnIconHeight = 60f;
	public static float pageModeIconPadding = 15f;
	private iLoongLauncher launcher;
	private int homePage = 0;
	public int pageMode = PAGE_MODE_SELECT;
	public static final int PAGE_MODE_SELECT = 0;
	public static final int PAGE_MODE_EDIT = 1;
	public static NinePatch pageBg;
	public static NinePatch pageSelectedBg;
	static
	{
		TextureRegion unselected = R3D.getTextureRegion( "shell-select-page-bg-unselect" );
		unselected.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		TextureRegion selected = R3D.getTextureRegion( "shell-select-page-bg-select" );
		selected.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		pageBg = new NinePatch( unselected , 60 , 60 , 60 , 60 );
		pageSelectedBg = new NinePatch( selected , 60 , 60 , 60 , 60 );
	}
	public static final int MSG_PAGE_CONTAINER_HIDE = 0;
	public static final int MSG_PAGE_SET_HOME = 1;
	public static final int MSG_PAGE_APPEND_PAGE = 2;
	public static final int MSG_PAGE_REMOVE_CELL = 3;
	public static final int MSG_PAGE_ADD_CELL = 4;
	public static final int MSG_PAGE_SWITCH_PAGE = 5;
	public static final int MSG_PAGE_MODE_EDIT = 6;
	public static final int MSG_PAGE_MODE_SELECT = 7;
	public static final int MSG_PAGE_SHOW_EDIT = 8;
	
	public PageContainer3D(
			String name )
	{
		super( name );
		pageSelect = new PageSelect3D( "page_select" );
		pageSelect.setPageContainer( this );
		addView( pageSelect );
		pageEdit = new PageEdit3D( "page_edit" );
		pageEdit.setPageContainer( this );
		addView( pageEdit );
		pageEdit.hide();
		pageModeIcon = new View3D( "page_mode" );
		pageModeIcon.show();
		addView( pageModeIcon );
		setActionListener();
		float screenWidth = Utils3D.getScreenWidth();
		float screenScale = screenWidth / 480 < 1 ? screenWidth / 480 : 1;
		float density = iLoongLauncher.getInstance().getResources().getDisplayMetrics().density;
		pageModeIconWidth *= density;//SetupMenu.mScreenScale;
		pageModeIconHeight *= density;//SetupMenu.mScreenScale;
		returnIconWidth *= screenScale;
		returnIconHeight *= screenScale;
		pageModeIconPadding *= screenScale * density;//SetupMenu.mScreenScale;
		transform = true;
	}
	
	public void onThemeChanged()
	{
		pageSelect.onThemeChanged();
		pageEdit.onThemeChanged();
		pageBg = new NinePatch( R3D.getTextureRegion( "shell-select-page-bg-unselect" ) , 60 , 60 , 60 , 60 );
		pageSelectedBg = new NinePatch( R3D.getTextureRegion( "shell-select-page-bg-select" ) , 60 , 60 , 60 , 60 );
	}
	
	public void setLauncher(
			iLoongLauncher launcher )
	{
		this.launcher = launcher;
		pageSelect.setLauncher( launcher );
		pageEdit.setLauncher( launcher );
	}
	
	//show PageEdit
	public void setupPageEdit(
			ArrayList<View3D> workspaceList ,
			int selectedPage ,
			int home )
	{
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight();
		x = 0;
		y = 0;
		homePage = home;
		pageSelect.currentIndex = selectedPage;
		pageSelect.homePage = homePage;
		pageEdit.homePage = homePage;
		celllayoutList = workspaceList;
		pageSelect.setupPageEdit( celllayoutList );
		if( !pageSelect.hideForEdit() )
			return;
		pageEdit.setPageList( pageSelect.getViewList() , PageEdit3D.PAGE_SOURCE_WORKSPACE );
		pageEdit.show();
		pageMode = PAGE_MODE_EDIT;
		if( DefaultLayout.pageEditor_back_icon_shown )
		{
			pageModeIcon.region = R3D.getTextureRegion( "page-controlle-c" );
			pageModeIcon.setSize( returnIconWidth , returnIconHeight );
			pageModeIcon.x = width - returnIconWidth - pageModeIconPadding;
			pageModeIcon.y = pageModeIconPadding;
			pageModeIcon.setOrigin( pageModeIcon.getWidth() / 2 , pageModeIcon.getHeight() / 2 );
			pageModeIcon.scaleX = 0f;
			pageModeIcon.scaleY = 0f;
			pageModeIcon.startTween( View3DTweenAccessor.SCALE_XY , Back.OUT , 0.5f , 1 , 1 , 0 );
		}
		else
		{
			pageModeIcon.hide();
		}
	}
	
	//show PageSelect
	public void setupPageSelect(
			ArrayList<View3D> workspaceList ,
			int selectedPage ,
			int home )
	{
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight();
		x = 0;
		y = 0;
		homePage = home;
		pageSelect.currentIndex = selectedPage;
		pageSelect.homePage = homePage;
		pageEdit.homePage = homePage;
		celllayoutList = workspaceList;
		pageSelect.setupPage( celllayoutList );
		pageModeIcon.region = R3D.getTextureRegion( "page-controlle-b" );
		pageModeIcon.setSize( pageModeIconWidth , pageModeIconHeight );
		pageModeIcon.setOrigin( pageModeIcon.getWidth() / 2 , pageModeIcon.getHeight() / 2 );
		pageModeIcon.x = pageModeIconPadding;
		pageModeIcon.y = height - pageModeIconPadding - pageModeIcon.getHeight();
		pageModeIcon.scaleX = 0f;
		pageModeIcon.scaleY = 0f;
		pageModeIcon.startTween( View3DTweenAccessor.SCALE_XY , Back.OUT , 0.5f , 1 , 1 , 0 );
		pageMode = PAGE_MODE_SELECT;
	}
	
	public void setEnterPage(
			int enterPage )
	{
		pageSelect.enterIndex = enterPage;
	}
	
	public void changeMode()
	{
		if( pageSelect.isVisible() )
		{
			if( !pageSelect.hideForEdit() )
				return;
			pageEdit.setPageList( pageSelect.getViewList() , PageEdit3D.PAGE_SOURCE_SELECT );
			pageEdit.show();
			if( pageModeIcon.visible )
			{
				pageModeIcon.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setCallback( this );
			}
			viewParent.onCtrlEvent( this , MSG_PAGE_MODE_EDIT );
			pageMode = PAGE_MODE_EDIT;
		}
		else
		{
			pageSelect.resort();
			if( !pageEdit.hide( true ) )
				return;
			if( pageModeIcon.visible )
			{
				pageModeIcon.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setCallback( this );
			}
			viewParent.onCtrlEvent( this , MSG_PAGE_MODE_SELECT );
			pageMode = PAGE_MODE_SELECT;
		}
	}
	
	public int getSelectedIndex()
	{
		return pageSelect.currentIndex;
	}
	
	public void setSelectedIndex(
			int i )
	{
		pageSelect.currentIndex = i;
		viewParent.onCtrlEvent( this , MSG_PAGE_SWITCH_PAGE );
	}
	
	public int getHomePage()
	{
		return homePage;
	}
	
	public void setHomePage(
			int home )
	{
		pageSelect.homePage = home;
		homePage = home;
		viewParent.onCtrlEvent( this , MSG_PAGE_SET_HOME );
		DefaultLayout.getInstance().saveHomePage( homePage );
	}
	
	public void removeCellLayout(
			int i ,
			boolean temp )
	{
		ViewGroup3D deletedCell = (ViewGroup3D)celllayoutList.remove( i );
		for( int j = i ; j < getPageNum() ; j++ )
		{
			ViewGroup3D cell = (ViewGroup3D)celllayoutList.get( j );
			for( int k = 0 ; k < cell.getChildCount() ; k++ )
			{
				View3D v = cell.getChildAt( k );
				if( v instanceof IconBase3D )
				{
					ItemInfo info = ( (IconBase3D)v ).getItemInfo();
					info.screen = j;
					Root3D.addOrMoveDB( info );
				}
			}
		}
		if( !temp )
		{
			this.setTag( new ArrayList<View3D>( deletedCell.getActors() ) );
		}
		else
			this.setTag( null );
		//PreferenceManager.getDefaultSharedPreferences(SetupMenu.getContext()).edit().putInt("cell_num", getPageNum()).commit();
		ThemeManager.getInstance().getThemeDB().SaveScreenCount( getPageNum() );
		viewParent.onCtrlEvent( this , MSG_PAGE_REMOVE_CELL );
		SendMsgToAndroid.sendPageEditRemoveWorkspaceCellMsg( i );
	}
	
	public void addCellLayout(
			int i ,
			CellLayout3D celllayout )
	{
		celllayoutList.add( i , celllayout );
		for( int j = i ; j < getPageNum() ; j++ )
		{
			ViewGroup3D cell = (ViewGroup3D)celllayoutList.get( j );
			for( int k = 0 ; k < cell.getChildCount() ; k++ )
			{
				View3D v = cell.getChildAt( k );
				if( v instanceof IconBase3D )
				{
					ItemInfo info = ( (IconBase3D)v ).getItemInfo();
					info.screen = j;
					Root3D.addOrMoveDB( info );
				}
			}
		}
		//PreferenceManager.getDefaultSharedPreferences(SetupMenu.getContext()).edit().putInt("cell_num", getPageNum()).commit();
		ThemeManager.getInstance().getThemeDB().SaveScreenCount( getPageNum() );
		viewParent.onCtrlEvent( this , MSG_PAGE_ADD_CELL );
		SendMsgToAndroid.sendPageEditAddWorkspaceCellMsg( i );
	}
	
	public View3D onAppendSelectPage()
	{
		viewParent.onCtrlEvent( this , MSG_PAGE_APPEND_PAGE );
		//		celllayoutList.add(tmpCell);
		int size = celllayoutList.size();
		//PreferenceManager.getDefaultSharedPreferences(SetupMenu.getContext()).edit().putInt("cell_num", size).commit();
		ThemeManager.getInstance().getThemeDB().SaveScreenCount( size );
		return pageSelect.addPage( celllayoutList.get( size - 1 ) , size , true );
	}
	
	public void exeDeletePage()
	{
		pageEdit.exeDeletePage();
	}
	
	public int getPageNum()
	{
		return celllayoutList.size();
	}
	
	public void switchToPage(
			int selected )
	{
		if( pageSelect.isVisible() )
			pageSelect.switchToPage( selected );
	}
	
	public void hide(
			boolean anim )
	{
		if( !anim )
		{
			pageSelect.hide( false );
			return;
		}
		if( pageSelect.hide( true ) )
		{
			if( pageModeIcon.visible )
			{
				pageModeIcon.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 );
			}
		}
	}
	
	public void onPageSelectHide()
	{
		super.hide();
		viewParent.onCtrlEvent( this , MSG_PAGE_CONTAINER_HIDE );
		//		if(pageSelect.clingState==ClingManager.CLING_STATE_SHOW){
		//			SendMsgToAndroid.sendRefreshClingStateMsg();
		//		}
	}
	
	public void onPageEditHide()
	{
		pageSelect.show();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "click" , "ViewGroup3D onClick:" + name + " x:" + x + " y:" + y );
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = this.getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			if( child.pointerInParent( point.x , point.y ) )
			{
				if( child == pageModeIcon )
				{
					if( pageSelect.isVisible() )
					{
						changeMode();
						//pageSelect.dismissCling();
						//ClingManager.getInstance().cancelPageContainerCling(ClingManager.PAGEEDIT_CLING);
					}
					else
						pageEdit.goBack();
					return true;
				}
				else
				{
					toLocalCoordinates( child , point );
					if( child.onClick( point.x , point.y ) )
					{
						lastTouchedChild = child;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pageModeIcon.visible && pageModeIcon.pointerInAbs( x , y ) )
			return false;
		return super.onTouchUp( x , y , pointer );
	}
	
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pageModeIcon.visible && pageModeIcon.pointerInAbs( x , y ) )
			return false;
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source instanceof Tween )
		{
			Tween tween = (Tween)source;
			if( tween.getTarget() == pageModeIcon )
			{
				if( pageMode == PAGE_MODE_SELECT )
				{
					pageModeIcon.region = R3D.getTextureRegion( "page-controlle-b" );
					pageModeIcon.setSize( pageModeIconWidth , pageModeIconHeight );
					pageModeIcon.x = pageModeIconPadding;
					pageModeIcon.y = height - pageModeIconPadding - pageModeIconHeight;
				}
				else
				{
					pageModeIcon.region = R3D.getTextureRegion( "page-controlle-c" );
					pageModeIcon.setSize( returnIconWidth , returnIconHeight );
					pageModeIcon.x = width - returnIconWidth - pageModeIconPadding;
					pageModeIcon.y = pageModeIconPadding;
				}
				pageModeIcon.setOrigin( pageModeIcon.getWidth() / 2 , pageModeIcon.getHeight() / 2 );
				pageModeIcon.scaleX = 0f;
				pageModeIcon.scaleY = 0f;
				pageModeIcon.startTween( View3DTweenAccessor.SCALE_XY , Back.OUT , 0.5f , 1 , 1 , 0 );
			}
		}
	}
	
	@Override
	public void setActionListener()
	{
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_SCREEN_EDITING , this );
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		viewParent.onCtrlEvent( this , MSG_PAGE_SHOW_EDIT );
		Gdx.graphics.requestRendering();
	}
}
