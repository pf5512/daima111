package com.iLoong.launcher.desktopEdit;


import java.util.ArrayList;

import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PlaneEditMode extends ViewGroup3D
{
	
	public static final int MSG_PLANE_EDIT_MODE_ENTER = 0;
	public static final int MSG_PLANE_EDIT_MODE_EXIT = 1;
	public static final int MSG_PLANE_PAGE_ADD_COMPLETE = 2;
	private final float MAX_X_ROTATION = 0.3f;
	private final float MIN_VELOCITY = 500f;
	private final float MAX_VELOCITY = 4000f;
	private DesktopEdit desktopEdit;
	private int pageGapX;
	private ViewGroup3D pageGroup;
	private float pageGroupMargin;
	private float pageGroupPosX;
	private float pageGroupPosY;
	private float pageGroupOffsetX;
	private float posRecord;
	private boolean bEnterAnim;
	private boolean bEnterFromOtherMode;
	private boolean bExitAnim;
	private float xScale;
	private float yScale;
	private Timeline timeline;
	private boolean bFlingAnim;
	private boolean bAutoEffectAnim;
	private View3D grooveView;
	private float grooveViewHeight = 38f;
	private boolean bAppendAnimtion;
	private boolean bDeleteAnimtion;
	private View3D deletePage;
	private View3D newPage;
	private View3D addPage;
	private float addPagePosY;
	private boolean bNotDispatchClick;
	private boolean bAutoMoveAnim;
	private boolean bDragActionFinish;
	private Tween exitExtraTween;
	private View3D curCelllayout;
	
	public PlaneEditMode()
	{
		// TODO Auto-generated constructor stub
	}
	
	public PlaneEditMode(
			String name ,
			DesktopEdit desktopEdit )
	{
		// TODO Auto-generated constructor stub
		super( name );
		this.desktopEdit = desktopEdit;
		xScale = 0;
		yScale = 0;
	}
	
	public void onDrop(
			PageContainer dropPage )
	{
		// TODO Auto-generated method stub
		float posX = pageGroupMargin + dropPage.pageIndex * pageGroupOffsetX;
		dropPage.stopAllTween();
		dropPage.x = dropPage.getParent().x + dropPage.pageIndex * pageGroupOffsetX;
		dropPage.y = dropPage.getParent().y - pageGroupPosY;
		dropPage.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , posX , 0 , 0 );
		pageGroup.addView( dropPage );
		DesktopEdit.dragX = 0;
		DesktopEdit.dragY = 0;
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
		if( bEnterAnim || bExitAnim || bAppendAnimtion || bDeleteAnimtion || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage )
		{
			return true;
		}
		requestFocus();
		stopAutoEffect();
		if( super.onTouchDown( x , y , pointer ) )
		{
			return false;
		}
		return false;
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
		if( bEnterAnim || bExitAnim || bAppendAnimtion || bDeleteAnimtion || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage )
		{
			return true;
		}
		releaseFocus();
		if( !bFlingAnim )
		{
			startAutoEffect();
		}
		if( super.onTouchUp( x , y , pointer ) )
		{
			return false;
		}
		return false;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( !bNotDispatchClick )
		{
			return super.onClick( x , y );
		}
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( !bNotDispatchClick )
		{
			return super.onLongClick( x , y );
		}
		return true;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		if( bEnterAnim || bExitAnim || bAppendAnimtion || bDeleteAnimtion || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage )
		{
			return true;
		}
		updateEffect( deltaX , deltaY );
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		if( bEnterAnim || bExitAnim || bAppendAnimtion || bDeleteAnimtion || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage )
		{
			return true;
		}
		if( Math.abs( velocityX ) < MIN_VELOCITY )
		{
			return true;
		}
		int pageNum = (int)( Math.abs( velocityX ) * DesktopEdit.PAGE_COUNT_MAX / MAX_VELOCITY );
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		int targetPageIndex = 0;
		if( velocityX > 0 )
		{
			targetPageIndex = curPageIndex - pageNum;
			targetPageIndex = ( targetPageIndex <= 0 ? 0 : targetPageIndex );
		}
		else
		{
			targetPageIndex = curPageIndex + pageNum;
			targetPageIndex = ( targetPageIndex >= desktopEdit.getPageNum() - 1 ? desktopEdit.getPageNum() - 1 : targetPageIndex );
		}
		float targetPosX = -targetPageIndex * pageGroupOffsetX;
		float duration = 1f;
		timeline = Timeline.createParallel();
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , targetPosX , pageGroupPosY , 0 ) );
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , 0 , 0 , 0 ) );
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		bFlingAnim = true;
		bNotDispatchClick = true;
		return true;
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		// TODO Auto-generated method stub
		if( sender instanceof PageContainer )
		{
			if( event_id == PageContainer.MSG_PAGE_CLICK )
			{
				exitPlaneEditMode();
			}
			else if( event_id == PageContainer.MSG_PAGE_LONGCLICK )
			{
				releaseFocus();
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( !bEnterAnim && !bAutoMoveAnim )
		{
			if( DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage )
			{
				if( canAutoMove( DesktopEdit.dragX , DesktopEdit.dragY ) )
				{
					if( DesktopEdit.autoMoveStayTime == 0 )
					{
						DesktopEdit.canAutoMove = true;
						autoMoveForDragAction();
					}
					if( ( System.currentTimeMillis() - DesktopEdit.autoMoveStayTime ) > 300 )
					{
						DesktopEdit.autoMoveStayTime = System.currentTimeMillis();
						if( DesktopEdit.canAutoMove )
						{
							autoMoveForDragAction();
							DesktopEdit.canAutoMove = false;
						}
					}
				}
				else
				{
					DesktopEdit.autoMoveStayTime = 0;
					DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_STOP;
				}
			}
		}
		if( bEnterAnim )
		{
			if( bEnterFromOtherMode )
			{
				pageGroup.setOriginZ( pageGroup.getUser2() );
				//				pageGroup.setZ(pageGroup.getUser());
			}
		}
		if( bExitAnim )
		{
			if( curCelllayout != null )
			{
				curCelllayout.setScaleZ( curCelllayout.getUser() );
			}
		}
		//		if (bAppendAnimtion || bDeleteAnimtion) {
		//			Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		//			Gdx.gl.glScissor(0, (int) (pageGroupPosY-grooveView.getHeight()/4),(int) Utils3D.getScreenWidth(),
		//					(int) (Utils3D.getScreenHeight()-pageGroupPosY));
		//		}
		super.draw( batch , parentAlpha );
		//		if (bAppendAnimtion || bDeleteAnimtion) {
		//			Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST); 
		//		}
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( !bEnterAnim && !bExitAnim && !bFlingAnim && !bAutoEffectAnim && !bAutoMoveAnim && !bAppendAnimtion && !bDeleteAnimtion )
			{
				exitPlaneEditMode();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub		
		if( source == exitExtraTween && type == TweenCallback.COMPLETE )
		{
			exitExtraTween.free();
			exitExtraTween = null;
			exitNormalAnim( false );
		}
		if( source == timeline && type == TweenCallback.COMPLETE )
		{
			if( bEnterAnim )
			{
				bEnterAnim = false;
				bNotDispatchClick = false;
				if( bEnterFromOtherMode )
				{
					bEnterFromOtherMode = false;
					destroyCylinderAnimDone();
				}
			}
			if( bExitAnim )
			{
				bExitAnim = false;
				curCelllayout = null;
				bNotDispatchClick = false;
				viewParent.onCtrlEvent( this , MSG_PLANE_EDIT_MODE_EXIT );
			}
			if( bAutoEffectAnim )
			{
				bAutoEffectAnim = false;
				bNotDispatchClick = false;
				xScale = 0f;
				yScale = 0f;
				posRecord = 0;
				setCurPageIndex();
			}
			if( bFlingAnim )
			{
				bFlingAnim = false;
				bNotDispatchClick = false;
				xScale = 0f;
				yScale = 0f;
				posRecord = 0;
				setCurPageIndex();
			}
			if( bAppendAnimtion )
			{
				if( deletePage != null )
				{
					deletePage.remove();
					deletePage = null;
				}
				if( newPage != null )
				{
					newPage.setPosition( pageGroupMargin + desktopEdit.getCurrentPageIndex() * pageGroupOffsetX , 0 );
					pageGroup.addView( newPage );
					newPage = null;
				}
				viewParent.onCtrlEvent( this , this.MSG_PLANE_PAGE_ADD_COMPLETE );
				bAppendAnimtion = false;
				bNotDispatchClick = false;
			}
			if( bDeleteAnimtion )
			{
				if( deletePage != null )
				{
					deletePage.remove();
					deletePage = null;
				}
				DragLayer3D dragLayer = iLoongLauncher.getInstance().getD3dListener().getDragLayer();
				dragLayer.removeAllViews();
				dragLayer.hide();
				viewParent.onCtrlEvent( desktopEdit , DesktopEdit.MSG_DESKTOP_EDIT_DELETE_PAGE );
				bDeleteAnimtion = false;
				bNotDispatchClick = false;
			}
			if( bAutoMoveAnim )
			{
				bAutoMoveAnim = false;
				setCurPageIndex();
				DesktopEdit.canAutoMove = true;
				DesktopEdit.autoMoveStayTime = System.currentTimeMillis();
				if( bDragActionFinish )
				{
					bDragActionFinish = false;
					if( DesktopEdit.bDragActionForIcon )
					{
						exitPlaneEditMode();
						DesktopEdit.bDragActionForIcon = false;
					}
					else if( DesktopEdit.bDragActionForPage )
					{
						DesktopEdit.bDragActionForPage = false;
						DragLayer3D dragLayer = iLoongLauncher.getInstance().getD3dListener().getDragLayer();
						dragLayer.onDrop();
						dragLayer.removeAllViews();
						dragLayer.hide();
					}
				}
			}
		}
	}
	
	private void setCurPageIndex()
	{
		float remainder = Math.abs( pageGroup.getX() ) % pageGroupOffsetX;
		int endPageIndex = (int)( Math.abs( pageGroup.getX() ) / pageGroupOffsetX );
		if( remainder >= pageGroupOffsetX / 2 )
		{
			endPageIndex++;
		}
		desktopEdit.setCurrentPageIndex( endPageIndex );
	}
	
	private void exitNormalAnim(
			boolean external )
	{
		float posX = 0;
		float posY = 0;
		float duration = 0.5f;
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		timeline = Timeline.createParallel();
		for( int i = 0 ; i < pageList.size() ; i++ )
		{
			View3D page = pageList.get( i );
			if( i == curPageIndex )
			{
				curCelllayout = ( (ViewGroup3D)page ).findView( "celllayout" );
				if( curCelllayout != null )
				{
					posX = pageGroupMargin;
					posY = pageGroupPosY - DesktopEdit.page_cell_padding_top;
					curCelllayout.setPosition( posX , posY );
					timeline.push( curCelllayout.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , 0 , 0 , 0 ) );
					timeline.push( curCelllayout.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1f , 1f , 0 ) );
					this.addView( curCelllayout );
					curCelllayout.setUser( 0.1f );
					timeline.push( curCelllayout.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 1 , 0 , 0 ) );
				}
				timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 0 , 0 , 0 ) );
			}
			else if( i == curPageIndex - 1 || i == curPageIndex + 1 )
			{
				timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 0 , 0 , 0 ) );
				if( i == curPageIndex - 1 )
				{
					posX = pageGroupMargin + i * pageGroupOffsetX - 2 * pageGroupMargin - pageGapX;
				}
				else if( i == curPageIndex + 1 )
				{
					posX = pageGroupMargin + i * pageGroupOffsetX + 2 * pageGroupMargin + pageGapX;
				}
				timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , posX , 0 , 0 ) );
			}
		}
		if( addPage.isVisible() )
		{
			timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , pageGroupMargin , -addPage.getHeight() , 0 ) );
			timeline.push( addPage.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 0 , 0 , 0 ) );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		bExitAnim = true;
		if( DesktopEdit.editModeChangeView != null )
		{
			DesktopEdit.editModeChangeView.stopAllTween();
			DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 0 , 0 , 0 );
		}
		if( !external && !DesktopEdit.bDragActionForIcon && exitExtraTween == null )
		{
			//			iLoongLauncher.getInstance().getD3dListener().getRoot().getHotSeatBar().startDockbarTweenExternal();
			iLoongLauncher.getInstance().getD3dListener().getRoot().getHotSeatBar().dockbarTurnUp();
		}
	}
	
	public void exitExternalAnim()
	{
		exitNormalAnim( true );
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		View3D temPage = pageList.get( curPageIndex );
		if( temPage.name.equals( "addPage" ) )
		{
			curPageIndex -= 1;
			desktopEdit.setCurrentPageIndex( curPageIndex );
		}
	}
	
	public void exitPlaneEditMode()
	{
		bNotDispatchClick = true;
		float posX = 0;
		float duration = 0.5f;
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		View3D temPage = pageList.get( curPageIndex );
		if( temPage.name.equals( "addPage" ) )
		{
			curPageIndex -= 1;
			desktopEdit.setCurrentPageIndex( curPageIndex );
			posX = -curPageIndex * pageGroupOffsetX;
			exitExtraTween = pageGroup.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , posX , pageGroupPosY , 0 ).setCallback( this );
		}
		else
		{
			exitNormalAnim( false );
		}
	}
	
	public void setupEditPageGroup(
			boolean fromOtherMode )
	{
		bEnterAnim = true;
		bNotDispatchClick = true;
		pageGroupMargin = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		pageGapX = (int)DesktopEdit.page_bg_gap_x;
		pageGroupPosX = 0;
		pageGroupPosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		pageGroupOffsetX = desktopEdit.getPageWidth() + pageGapX;
		float groupWidth = desktopEdit.getPageWidth() * DesktopEdit.PAGE_COUNT_MAX + pageGapX * ( DesktopEdit.PAGE_COUNT_MAX - 1 ) + 2 * pageGroupMargin;
		float groupHeight = desktopEdit.getPageHeight();
		pageGroup = new ViewGroup3D( "pageGroup" );
		pageGroup.setSize( groupWidth , groupHeight );
		pageGroup.setOrigin( groupWidth / 2 , groupHeight / 2 );
		pageGroup.transform = true;
		addPage = new PageContainer( "addPage" , desktopEdit.getPageWidth() , desktopEdit.getPageHeight() );
		( (PageContainer)addPage ).buildAddPage( -1 );
		addPagePosY = -addPage.getHeight() * 9 / 10;
		addPage.setPosition( pageGroupMargin , -addPage.getHeight() );
		this.addView( addPage );
		addPage.hide();
		if( fromOtherMode )
		{
			bEnterFromOtherMode = true;
			destroyCylinderAnim();
			if( DesktopEdit.editModeChangeView != null )
			{
				DesktopEdit.editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_plane );
				DesktopEdit.editModeChangeView.region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				this.addView( DesktopEdit.editModeChangeView );
				DesktopEdit.editModeChangeView.color.a = 1;
				DesktopEdit.editModeChangeView.stopAllTween();
				DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.6f , 0 , 0 , 0 );
			}
		}
		else
		{
			buildPlaneFromDesktop();
			if( DesktopEdit.editModeChangeView != null )
			{
				DesktopEdit.editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_cylinder );
				DesktopEdit.editModeChangeView.region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				this.addView( DesktopEdit.editModeChangeView );
				DesktopEdit.editModeChangeView.color.a = 0;
				DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 1 , 0 , 0 );
			}
		}
		grooveViewHeight *= DesktopEdit.scaleHeight;
		grooveView = new View3D( "grooveView" , R3D.getTextureRegion( R3D.desktopEdit_page_groove ) );
		grooveView.setSize( desktopEdit.getPageWidth() + 46f , grooveViewHeight );
		grooveView.hide();
		pageGroup.addView( grooveView );
	}
	
	private void buildPlaneFromDesktop()
	{
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		float posX = 0;
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		timeline = Timeline.createSequence();
		Timeline temTimeline = Timeline.createParallel();
		float duration = 0.5f;
		for( int i = 0 ; i < pageList.size() ; i++ )
		{
			View3D page = pageList.get( i );
			if( i == curPageIndex )
			{
				page.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
				temTimeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1f , 1f , 0 ) );
				View3D homeView = ( (ViewGroup3D)page ).findView( "pageHomeView" );
				if( homeView != null )
				{
					homeView.color.a = 0;
					temTimeline.push( homeView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 ) );
				}
				View3D bgView = ( (ViewGroup3D)page ).findView( "pageBg" );
				if( bgView != null )
				{
					bgView.color.a = 0;
					temTimeline.push( bgView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 ) );
				}
				View3D deleteView = ( (ViewGroup3D)page ).findView( "pageDeleteView" );
				if( deleteView != null )
				{
					deleteView.color.a = 0;
					temTimeline.push( deleteView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 ) );
				}
				posX = pageGroupMargin + i * pageGroupOffsetX;
				page.setPosition( posX , 0 );
			}
			else if( i == curPageIndex - 1 || i == curPageIndex + 1 )
			{
				page.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
				temTimeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1f , 1f , 0 ) );
				View3D bgView = ( (ViewGroup3D)page ).findView( "pageBg" );
				if( bgView != null )
				{
					bgView.color.a = 0;
					temTimeline.push( bgView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 ) );
				}
				View3D homeView = ( (ViewGroup3D)page ).findView( "pageHomeView" );
				if( homeView != null )
				{
					homeView.color.a = 0;
					temTimeline.push( homeView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 ) );
				}
				View3D deleteView = ( (ViewGroup3D)page ).findView( "pageDeleteView" );
				if( deleteView != null )
				{
					deleteView.color.a = 0;
					temTimeline.push( deleteView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 ) );
				}
				if( i == curPageIndex - 1 )
				{
					posX = pageGroupMargin + i * pageGroupOffsetX - 2 * pageGroupMargin - pageGapX;
				}
				else if( i == curPageIndex + 1 )
				{
					posX = pageGroupMargin + i * pageGroupOffsetX + 2 * pageGroupMargin + pageGapX;
				}
				page.setPosition( posX , 0 );
				posX = pageGroupMargin + i * pageGroupOffsetX;
				temTimeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , posX , 0 , 0 ) );
			}
			else
			{
				posX = pageGroupMargin + i * pageGroupOffsetX;
				page.setPosition( posX , 0 );
			}
			pageGroup.addView( page );
		}
		timeline.push( temTimeline );
		if( !DesktopEdit.bDragActionForIcon )
		{
			if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX && !addPage.isVisible() )
			{
				addPage.show();
				timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pageGroupMargin , addPagePosY , 0 ) );
			}
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		pageGroupPosX = -curPageIndex * pageGroupOffsetX;
		pageGroup.setPosition( pageGroupPosX , pageGroupPosY );
		this.addView( pageGroup );
	}
	
	private void destroyCylinderAnim()
	{
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		int pageNum = pageList.size();
		float radius = CylinderEditMode.calcCylinderRadius( pageNum );
		float polygonAngle = CylinderEditMode.calcPolygonAngle( pageNum );
		float duration = 0.6f;
		pageGroup.setRotationX( CylinderEditMode.PAGE_GROUP_X_ANGLE );
		//		pageGroup.setZ(CylinderEditMode.PAGE_GROUP_Z);
		pageGroup.setOriginZ( CylinderEditMode.PAGE_GROUP_ORIGIN_Z );
		//		pageGroup.setUser(CylinderEditMode.PAGE_GROUP_Z);
		pageGroup.setUser2( CylinderEditMode.PAGE_GROUP_ORIGIN_Z );
		timeline = Timeline.createSequence();
		Timeline temTimeline = Timeline.createParallel();
		temTimeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
		//		temTimeline.push(pageGroup.obtainTween(View3DTweenAccessor.USER, Linear.INOUT, duration, 0, 0, 0));
		temTimeline.push( pageGroup.obtainTween( View3DTweenAccessor.USER2 , Linear.INOUT , duration , 0 , 0 , 0 ) );
		if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX )
		{
			addPage.show();
			addPage.setPosition( pageGroupMargin , addPagePosY );
			temTimeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , pageGroupMargin , -addPage.getHeight() , 0 ) );
		}
		View3D curPage = pageList.get( curPageIndex );
		curPage.setPosition( pagePosX , 0 );
		if( pageNum == 2 )
		{
			if( curPageIndex == 0 )
			{
				View3D page = pageList.get( curPageIndex + 1 );
				pagePosX = pagePosX + page.getWidth() + DesktopEdit.page_bg_gap_x;
				page.setPosition( pagePosX , 0 );
				page.setRotationY( polygonAngle );
				page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
				page.setOriginZ( radius );
				pageGroup.addView( page );
				temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
			}
			else
			{
				View3D page = pageList.get( curPageIndex - 1 );
				pagePosX = pagePosX - page.getWidth() - DesktopEdit.page_bg_gap_x;
				page.setPosition( pagePosX , 0 );
				page.setRotationY( -polygonAngle );
				page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
				page.setOriginZ( radius );
				pageGroup.addView( page );
				temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
			}
		}
		else
		{
			for( int i = curPageIndex + 1 ; i < pageList.size() ; i++ )
			{
				if( i == curPageIndex + 1 )
				{
					View3D page = pageList.get( i );
					pagePosX = pagePosX + page.getWidth() + DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationY( polygonAngle );
					page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					pageGroup.addView( page );
					temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
				else
				{
					View3D parentView = pageList.get( i - 1 );
					View3D page = pageList.get( i );
					pagePosX = page.getWidth() + DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationY( polygonAngle );
					page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					( (ViewGroup3D)parentView ).addViewAt( 0 , page );
					temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
			}
			pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
			for( int i = curPageIndex - 1 ; i >= 0 ; i-- )
			{
				if( i == curPageIndex - 1 )
				{
					View3D page = pageList.get( i );
					pagePosX = pagePosX - page.getWidth() - DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationY( -polygonAngle );
					page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					pageGroup.addView( page );
					temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
				else
				{
					View3D parentView = pageList.get( i + 1 );
					View3D page = pageList.get( i );
					pagePosX = -page.getWidth() - DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationY( -polygonAngle );
					page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					( (ViewGroup3D)parentView ).addViewAt( 0 , page );
					temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
			}
		}
		pageGroup.addView( curPage );
		timeline.push( temTimeline );
		if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX )
		{
			addPage.show();
			timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pageGroupMargin , addPagePosY , 0 ) );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		pageGroup.setPosition( 0 , pageGroupPosY );
		this.addView( pageGroup );
	}
	
	private void destroyCylinderAnimDone()
	{
		pageGroup.removeAllViews();
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		float posX = 0;
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		for( int i = 0 ; i < pageList.size() ; i++ )
		{
			View3D page = pageList.get( i );
			posX = pageGroupMargin + i * pageGroupOffsetX;
			page.setPosition( posX , 0 );
			page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
			page.setOriginZ( 0 );
			pageGroup.addView( page );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		pageGroupPosX = -curPageIndex * pageGroupOffsetX;
		pageGroup.setPosition( pageGroupPosX , pageGroupPosY );
		grooveView.hide();
		pageGroup.addView( grooveView );
		if( DesktopEdit.editModeChangeView != null )
		{
			DesktopEdit.editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_cylinder );
			DesktopEdit.editModeChangeView.stopAllTween();
			DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 1 , 0 , 0 );
		}
	}
	
	public void handleAppendAnimtion(
			View3D newPage )
	{
		this.newPage = newPage;
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		timeline = Timeline.createSequence();
		Timeline temTimeline = Timeline.createParallel();
		for( int i = desktopEdit.getCurrentPageIndex() + 1 ; i < pageList.size() ; i++ )
		{
			View3D page = pageList.get( i );
			temTimeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , pageGroupMargin + i * pageGroupOffsetX , 0 , 0 ) );
		}
		timeline.push( temTimeline );
		this.addView( newPage );
		newPage.setPosition( pageGroupMargin , addPagePosY );
		timeline.push( newPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.INOUT , 1f , pageGroupMargin , pageGroupPosY , 0 ) );
		if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX )
		{
			addPage.setPosition( pageGroupMargin , -addPage.getHeight() );
			timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pageGroupMargin , addPagePosY , 0 ) );
		}
		else
		{
			addPage.setPosition( pageGroupMargin , -addPage.getHeight() );
			addPage.hide();
		}
		//		PageContainer addPage = null;
		//		int newPageIndex = ((PageContainer)newPage).pageIndex;
		//		float targetPosX = 0;
		//		if (pageList.size() == DesktopEdit.PAGE_COUNT_MAX) {
		//			deletePage = (PageContainer)pageList.set(pageList.size()-1, newPage);
		//			temTimeline.push(deletePage.obtainTween(View3DTweenAccessor.OPACITY,
		//					Cubic.OUT, 1f, 0, 0, 0));
		//		} else {
		//			pageList.add(newPageIndex, newPage);
		//			addPage = (PageContainer)pageList.get(pageList.size()-1);
		//			addPage.pageIndex = pageList.size()-1;
		//			targetPosX = pageGroupMargin + addPage.pageIndex*pageGroupOffsetX;
		//			temTimeline.push(addPage.obtainTween(View3DTweenAccessor.POS_XY,
		//					Cubic.OUT, 0.5f, targetPosX, 0, 0).delay(0.5f));
		//		}
		//		
		//		targetPosX = pageGroupMargin + newPageIndex*pageGroupOffsetX;
		//		newPage.setPosition(targetPosX, -newPage.getHeight()-grooveView.getHeight()/2);
		//		pageGroup.addViewAt(0,newPage);
		//
		//		float groovePosX = newPage.getX()-(grooveView.getWidth()-newPage.getWidth())/2;
		//		grooveView.setPosition(groovePosX, -grooveView.getHeight()/4);
		//		grooveView.show();
		//		grooveView.color.a = 0;
		//		temTimeline.push(grooveView.obtainTween(View3DTweenAccessor.OPACITY,
		//				Cubic.OUT, 1f, 1, 0, 0));
		//		
		//		timeline.push(temTimeline);
		//		timeline.push(newPage.obtainTween(View3DTweenAccessor.POS_XY,
		//				Elastic.INOUT, 1f, newPage.getX(), 0, 0));
		//		timeline.push(grooveView.obtainTween(View3DTweenAccessor.OPACITY,
		//				Cubic.OUT, 1f, 0, 0, 0));
		timeline.setCallback( this ).start( View3DTweenAccessor.manager );
		bAppendAnimtion = true;
		bNotDispatchClick = true;
	}
	
	public void handleDeleteAnimtion(
			View3D deletePage )
	{
		this.deletePage = deletePage;
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		timeline = Timeline.createSequence();
		Timeline temTimeline = Timeline.createParallel();
		Timeline temTimeline2 = Timeline.createParallel();
		temTimeline.push( deletePage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , deletePage.getX() , deletePage.getHeight() , 0 ) );
		temTimeline.push( deletePage.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , 0 , 0 , 0 ) );
		timeline.push( temTimeline );
		if( ( (PageContainer)deletePage ).pageIndex == pageList.size() )
		{
			pageGroupPosX = -( desktopEdit.getCurrentPageIndex() - 1 ) * pageGroupOffsetX;
			temTimeline2.push( pageGroup.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , pageGroupPosX , pageGroupPosY , 0 ) );
		}
		else
		{
			for( int i = desktopEdit.getCurrentPageIndex() ; i < pageList.size() ; i++ )
			{
				View3D page = pageList.get( i );
				temTimeline2.push( page.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , pageGroupMargin + i * pageGroupOffsetX , 0 , 0 ) );
			}
		}
		//		if (((PageContainer)deletePage).pageIndex == DesktopEdit.PAGE_COUNT_MAX-1) {
		//			PageContainer addPage = new PageContainer("addPage",desktopEdit.getPageWidth(),
		//					desktopEdit.getPageHeight());
		//			addPage.buildAddPage(((PageContainer)deletePage).pageIndex);
		//			pageList.add(addPage);
		//			addPage.setPosition(pageGroupMargin+addPage.pageIndex*pageGroupOffsetX, 0);
		//			pageGroup.addView(addPage);
		//			addPage.color.a = 0;
		//			temTimeline2.push(addPage.obtainTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 1f, 1, 0, 0));
		//		} else {
		//			if (pageGroup.findView("addPage") == null) {
		//				PageContainer addPage = new PageContainer("addPage",desktopEdit.getPageWidth(),
		//						desktopEdit.getPageHeight());
		//				pageList.add(addPage);
		//				addPage.buildAddPage(pageList.size()-1);
		//				addPage.setPosition(pageGroupMargin+(addPage.pageIndex+1)*pageGroupOffsetX, 0);
		//				pageGroup.addView(addPage);
		//			}
		//			for (int i = desktopEdit.getCurrentPageIndex(); i < pageList.size(); i++) {
		//				View3D page = pageList.get(i);
		//				temTimeline2.push(page.obtainTween(View3DTweenAccessor.POS_XY,
		//						Cubic.OUT, 0.5f, pageGroupMargin+i*pageGroupOffsetX, 0, 0));
		//			}
		//		}
		temTimeline2.push( DesktopEdit.editModeChangeView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 1 , 0 , 0 ) );
		timeline.push( temTimeline2 );
		if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX && !addPage.isVisible() )
		{
			addPage.show();
			timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pageGroupMargin , addPagePosY , 0 ) );
		}
		timeline.setCallback( this ).start( View3DTweenAccessor.manager );
		bDeleteAnimtion = true;
		bNotDispatchClick = true;
	}
	
	private void updateEffect(
			float deltaX ,
			float deltaY )
	{
		float yAmplify = deltaY * 1.3f;
		this.xScale = xScale + deltaX / Utils3D.getScreenWidth();
		this.yScale = yScale + yAmplify / Utils3D.getScreenHeight();
		float tempYScale;
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
		float xAngle = tempYScale * 90f;
		pageGroup.setRotationX( xAngle );
		float offsetX = xScale * Utils3D.getScreenWidth();
		if( desktopEdit.getCurrentPageIndex() == 0 )
		{
			if( offsetX >= pageGroupOffsetX / 2 )
			{
				offsetX = pageGroupOffsetX / 2;
			}
		}
		if( desktopEdit.getCurrentPageIndex() == ( desktopEdit.getPageNum() - 1 ) )
		{
			if( offsetX <= -pageGroupOffsetX / 2 )
			{
				offsetX = -( pageGroupOffsetX / 2 );
			}
		}
		float targetX = offsetX - posRecord;
		posRecord = offsetX;
		targetX += pageGroup.getX();
		pageGroup.setPosition( targetX , pageGroupPosY );
	}
	
	private void startAutoEffect()
	{
		float remainder = Math.abs( pageGroup.getX() ) % pageGroupOffsetX;
		boolean needAutoEffect = true;
		if( remainder <= DesktopEdit.ESPINON || ( pageGroupOffsetX - remainder <= DesktopEdit.ESPINON ) )
		{
			needAutoEffect = false;
		}
		if( xScale == 0 && yScale == 0 && !needAutoEffect )
		{
			bNotDispatchClick = false;
			int endPageIndex = (int)( Math.abs( pageGroup.getX() ) / pageGroupOffsetX );
			if( remainder >= pageGroupOffsetX / 2 )
			{
				endPageIndex++;
			}
			desktopEdit.setCurrentPageIndex( endPageIndex );
			pageGroupPosX = -endPageIndex * pageGroupOffsetX;
			return;
		}
		float duration = 0.5f;
		int currentPage = desktopEdit.getCurrentPageIndex();
		float offsetX = pageGroup.getX() - pageGroupPosX;
		timeline = Timeline.createParallel();
		if( offsetX >= pageGroupOffsetX / 2 )
		{
			if( currentPage == 0 )
			{
				pageGroupPosX = 0;
			}
			else
			{
				pageGroupPosX = -( currentPage - 1 ) * pageGroupOffsetX;
			}
		}
		else if( offsetX <= -pageGroupOffsetX / 2 )
		{
			if( currentPage == ( desktopEdit.getPageNum() - 1 ) )
			{
				pageGroupPosX = -currentPage * pageGroupOffsetX;
			}
			else
			{
				pageGroupPosX = -( currentPage + 1 ) * pageGroupOffsetX;
			}
		}
		else
		{
			pageGroupPosX = -currentPage * pageGroupOffsetX;
		}
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , pageGroupPosX , pageGroupPosY , 0 ) );
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , 0 , 0 , 0 ) );
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		bAutoEffectAnim = true;
		bNotDispatchClick = true;
	}
	
	private void stopAutoEffect()
	{
		xScale = 0;
		yScale = 0;
		posRecord = 0;
		int endPageIndex = (int)( Math.abs( pageGroup.getX() ) / pageGroupOffsetX );
		pageGroupPosX = -endPageIndex * pageGroupOffsetX;
		desktopEdit.setCurrentPageIndex( endPageIndex );
		bAutoEffectAnim = false;
		bFlingAnim = false;
		if( timeline != null && !timeline.isFinished() )
		{
			timeline.free();
			timeline = null;
		}
	}
	
	private boolean canAutoMove(
			float x ,
			float y )
	{
		DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_STOP;
		if( y >= R3D.workspace_cell_height && y < ( Utils3D.getScreenHeight() - R3D.workspace_cell_height ) )
		{
			if( x <= R3D.workspace_cell_width )
			{
				DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_LEFT;
			}
			else if( x >= ( Utils3D.getScreenWidth() - R3D.workspace_cell_width ) )
			{
				DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_RIGHT;
			}
			return DesktopEdit.autoMoveStatus != DesktopEdit.AUTO_MOVE_STOP;
		}
		return false;
	}
	
	private void autoMoveForDragAction()
	{
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		int targetPageIndex = -1;
		int movePageIndex = -1;
		float targetPosX = 0;
		if( DesktopEdit.bDragActionForIcon )
		{
			if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_LEFT )
			{
				targetPageIndex = curPageIndex - 1;
				if( targetPageIndex < 0 )
				{
					return;
				}
			}
			else if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_RIGHT )
			{
				targetPageIndex = curPageIndex + 1;
				if( targetPageIndex == DesktopEdit.PAGE_COUNT_MAX - 1 )
				{
					View3D page = desktopEdit.getPageList().get( targetPageIndex );
					if( page.name.equals( "addPage" ) )
					{
						return;
					}
				}
				else
				{
					if( targetPageIndex >= desktopEdit.getPageNum() )
					{
						return;
					}
				}
			}
			timeline = Timeline.createParallel();
			targetPosX = -targetPageIndex * pageGroupOffsetX;
			pageGroup.setRotationX( 0 );
			timeline.push( pageGroup.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , targetPosX , pageGroupPosY , 0 ) );
			timeline.start( View3DTweenAccessor.manager ).setCallback( this );
			bAutoMoveAnim = true;
			SendMsgToAndroid.vibrator( R3D.vibrator_duration );
		}
		else if( DesktopEdit.bDragActionForPage )
		{
			if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_LEFT )
			{
				targetPageIndex = curPageIndex - 1;
				movePageIndex = targetPageIndex;
				if( targetPageIndex < 0 )
				{
					return;
				}
			}
			else if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_RIGHT )
			{
				targetPageIndex = curPageIndex + 1;
				movePageIndex = curPageIndex;
				if( curPageIndex < desktopEdit.getPageNum() )
				{
					View3D page = desktopEdit.getPageList().get( curPageIndex );
					if( page.name.equals( "addPage" ) )
					{
						return;
					}
				}
				else if( targetPageIndex > desktopEdit.getPageNum() )
				{
					return;
				}
			}
			timeline = Timeline.createParallel();
			targetPosX = -targetPageIndex * pageGroupOffsetX;
			pageGroup.setRotationX( 0 );
			timeline.push( pageGroup.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , targetPosX , pageGroupPosY , 0 ) );
			PageContainer movePage = (PageContainer)desktopEdit.getPageList().get( movePageIndex );
			targetPosX = pageGroupMargin + curPageIndex * pageGroupOffsetX;
			timeline.push( movePage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , targetPosX , 0 , 0 ) );
			timeline.start( View3DTweenAccessor.manager ).setCallback( this );
			bAutoMoveAnim = true;
			SendMsgToAndroid.vibrator( R3D.vibrator_duration );
		}
	}
	
	public void dragActionFinish()
	{
		bDragActionFinish = true;
		if( DesktopEdit.bDragActionForIcon )
		{
			if( !bAutoMoveAnim )
			{
				exitPlaneEditMode();
				bDragActionFinish = false;
				DesktopEdit.bDragActionForIcon = false;
			}
		}
		else if( DesktopEdit.bDragActionForPage )
		{
			if( !bAutoMoveAnim )
			{
				DragLayer3D dragLayer = iLoongLauncher.getInstance().getD3dListener().getDragLayer();
				dragLayer.onDrop();
				dragLayer.removeAllViews();
				dragLayer.hide();
				bDragActionFinish = false;
				DesktopEdit.bDragActionForPage = false;
			}
		}
	}
	
	public boolean isAnimation()
	{
		return( bEnterAnim || bExitAnim || bAutoMoveAnim || bAutoEffectAnim || bAppendAnimtion || bDeleteAnimtion || bFlingAnim );
	}
	
	public void stopAllAnimation()
	{
		if( exitExtraTween != null )
		{
			exitExtraTween.free();
			exitExtraTween = null;
		}
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
	}
}
