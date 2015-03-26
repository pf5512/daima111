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
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class CylinderEditMode extends ViewGroup3D
{
	
	public static final int MSG_CYLINDER_EDIT_MODE_ENTER = 0;
	public static final int MSG_CYLINDER_EDIT_MODE_EXIT = 1;
	public static final int MSG_CYLINDER_PAGE_ADD_COMPLETE = 2;
	public static final float PAGE_GROUP_X_ANGLE = 25f;
	public static final float PAGE_GROUP_ORIGIN_Z = -Utils3D.getScreenWidth() / 4;
	public static float page3DPosX;
	public static float page3DPosY;
	private final float MIN_VELOCITY = 500f;
	private DesktopEdit desktopEdit;
	private boolean bEnterAnim;
	private boolean bExitAnim;
	private PageGroup pageGroup;
	private float xScale;
	private float yScale;
	private float rotateAngleRec;
	private boolean bAutoEffectAnim;
	private boolean bFlingAnim;
	private Timeline timeline;
	private boolean bAutoMoveAnim;
	private boolean bDragActionFinish;
	private boolean bAppendAnim;
	private View3D appendPage;
	private boolean bDeleteAnim;
	private View3D deletePage;
	private View3D addPage;
	private float addPagePosY;
	private View3D grooveView;
	private float grooveViewHeight = 38f;
	private boolean bNotDispatchClick;
	private boolean bExitAnimExtra;
	private View3D curCelllayout;
	private Tween dropTween;
	private boolean bDropTween;
	
	public CylinderEditMode()
	{
		// TODO Auto-generated constructor stub
	}
	
	public CylinderEditMode(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	public CylinderEditMode(
			String name ,
			DesktopEdit desktopEdit )
	{
		super( name );
		this.desktopEdit = desktopEdit;
		xScale = 0;
		yScale = 0;
		DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_STOP;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( !bEnterAnim && !bAutoMoveAnim )
		{
			if( ( DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage ) )
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
					//					DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_STOP;  
				}
			}
		}
		updateCelllayoutAlpha();
		if( bEnterAnim || bExitAnim )
		{
			pageGroup.setOriginZ( pageGroup.getUser2() );
			//			pageGroup.setZ(pageGroup.getUser());
			if( bExitAnim )
			{
				if( curCelllayout != null )
				{
					curCelllayout.setScaleZ( curCelllayout.getUser() );
				}
				View3D temPageGroup = pageGroup.findView( "temPageGroup" );
				if( temPageGroup != null )
				{
					temPageGroup.setOrigin( temPageGroup.getUser() , temPageGroup.getHeight() / 2 );
				}
				View3D temPageGroup2 = pageGroup.findView( "temPageGroup2" );
				if( temPageGroup2 != null )
				{
					temPageGroup2.setOrigin( temPageGroup2.getUser() , temPageGroup2.getHeight() / 2 );
				}
			}
		}
		if( !bEnterAnim && !bExitAnim && !bAppendAnim )
		{
			updateAllPagesDrawOrder();
		}
		if( bAppendAnim || bDeleteAnim )
		{
			int pageNum = desktopEdit.getPageList().size();
			for( int i = 0 ; i < pageNum ; i++ )
			{
				View3D page = desktopEdit.getPageList().get( i );
				//				if (page.getUser() != 0) {
				page.setOriginZ( page.getUser() );
				//				}
			}
		}
		//		if (bAppendAnim2) {
		//			Gdx.gl.glEnable(GL10.GL_SCISSOR_TEST);
		//			Gdx.gl.glScissor(0, (int) (page3DPosY-grooveView.getHeight()/3),(int) Utils3D.getScreenWidth(),
		//					(int) (Utils3D.getScreenHeight()-page3DPosY));
		//		}			
		super.draw( batch , parentAlpha );
		//		if (bAppendAnim2) {
		//			Gdx.gl.glDisable(GL10.GL_SCISSOR_TEST);
		//		}
	}
	
	@Override
	public void toLocalCoordinates(
			View3D descendant ,
			Vector2 point )
	{
		// TODO Auto-generated method stub
		if( !( descendant instanceof PageGroup ) )
		{
			super.toLocalCoordinates( descendant , point );
		}
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
		if( bEnterAnim || bExitAnim || bAppendAnim || bDeleteAnim || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage || bDropTween )
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
		if( bEnterAnim || bExitAnim || bAppendAnim || bDeleteAnim || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage || bDropTween )
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
		if( bEnterAnim || bExitAnim || bAppendAnim || bDeleteAnim || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage || bDropTween )
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
		if( bEnterAnim || bExitAnim || bAppendAnim || bDeleteAnim || bAutoMoveAnim || DesktopEdit.bDragActionForIcon || DesktopEdit.bDragActionForPage || bDropTween )
		{
			return true;
		}
		if( Math.abs( velocityX ) < MIN_VELOCITY )
		{
			return true;
		}
		velocityX /= 2;
		int pageNum = desktopEdit.getPageList().size();
		if( pageNum == 1 )
		{
			return true;
		}
		float polygonAngle = calcPolygonAngle( pageNum );
		int circleNum = 0;
		float flingAngle = 0;
		float duration = 0f;
		if( Math.abs( velocityX ) <= 1250f )
		{
			duration = 0.5f;
			flingAngle = 0;
		}
		else
		{
			if( Math.abs( velocityX ) > 1250f && Math.abs( velocityX ) <= 1500f )
			{
				duration = 1.5f;
				circleNum = (int)( Math.abs( velocityX ) / ( 5 * polygonAngle ) );
			}
			else
			{
				duration = 2f;
				circleNum = (int)( Math.abs( velocityX ) / ( 2 * polygonAngle ) );
			}
			if( velocityX > 0 )
			{
				flingAngle = circleNum * polygonAngle;
			}
			else
			{
				flingAngle = -circleNum * polygonAngle;
			}
		}
		timeline = Timeline.createParallel();
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , PAGE_GROUP_X_ANGLE , 0 , 0 ) );
		float targetAngle = 0;
		float temAngle = desktopEdit.getPageList().get( 0 ).getRotation() % polygonAngle;
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			if( velocityX >= 0 )
			{
				if( temAngle < 0 )
				{
					targetAngle = page.getRotation() + flingAngle - temAngle;
				}
				else
				{
					targetAngle = page.getRotation() + flingAngle + ( polygonAngle - temAngle );
				}
			}
			else
			{
				if( temAngle > 0 )
				{
					targetAngle = page.getRotation() + flingAngle - temAngle;
				}
				else
				{
					targetAngle = page.getRotation() + flingAngle + ( -polygonAngle - temAngle );
				}
			}
			timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , targetAngle , 0 , 0 ) );
		}
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
				exitCylinderEditMode();
			}
			else if( event_id == PageContainer.MSG_PAGE_LONGCLICK )
			{
				releaseFocus();
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( !bEnterAnim && !bExitAnim && !bExitAnimExtra && !bFlingAnim && !bAutoEffectAnim && !bAutoMoveAnim && !bAppendAnim && !bDeleteAnim )
			{
				exitCylinderEditMode();
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
		if( source == timeline && type == TweenCallback.COMPLETE )
		{
			if( bEnterAnim )
			{
				bEnterAnim = false;
				bNotDispatchClick = false;
				buildCylinderAnimDone();
			}
			if( bExitAnim )
			{
				bExitAnim = false;
				curCelllayout = null;
				bNotDispatchClick = false;
				viewParent.onCtrlEvent( this , MSG_CYLINDER_EDIT_MODE_EXIT );
			}
			if( bExitAnimExtra )
			{
				bExitAnimExtra = false;
				exitNormalAnim( false );
			}
			if( bAutoEffectAnim )
			{
				bAutoEffectAnim = false;
				bNotDispatchClick = false;
				xScale = 0;
				yScale = 0;
				rotateAngleRec = 0;
				adjustAllPagesRotation();
				setCurPageIndex();
			}
			if( bFlingAnim )
			{
				bFlingAnim = false;
				bNotDispatchClick = false;
				xScale = 0;
				yScale = 0;
				rotateAngleRec = 0;
				adjustAllPagesRotation();
				setCurPageIndex();
			}
			if( bAutoMoveAnim )
			{
				DesktopEdit.canAutoMove = true;
				DesktopEdit.autoMoveStayTime = System.currentTimeMillis();
				adjustAllPagesRotation();
				if( DesktopEdit.bDragActionForIcon )
				{
					setCurPageIndex();
				}
				else if( DesktopEdit.bDragActionForPage )
				{
					if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_LEFT )
					{
						desktopEdit.setCurrentPageIndex( ( desktopEdit.getCurrentPageIndex() == 0 ? desktopEdit.getPageNum() : desktopEdit.getCurrentPageIndex() - 1 ) );
					}
					else if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_RIGHT )
					{
						desktopEdit.setCurrentPageIndex( ( desktopEdit.getCurrentPageIndex() == desktopEdit.getPageNum() ? 0 : desktopEdit.getCurrentPageIndex() + 1 ) );
					}
				}
				if( bDragActionFinish )
				{
					bDragActionFinish = false;
					if( DesktopEdit.bDragActionForIcon )
					{
						exitCylinderEditMode();
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
				bAutoMoveAnim = false;
			}
			if( bAppendAnim )
			{
				if( appendPage != null )
				{
					ArrayList<View3D> pageList = desktopEdit.getPageList();
					float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
					appendPage.setRotationY( 0 );
					appendPage.setOriginZ( calcCylinderRadius( pageList.size() ) );
					pageGroup.addView( appendPage );
					if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX )
					{
						addPage.startTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pagePosX , addPagePosY , 0 );
					}
					else
					{
						addPage.setPosition( pagePosX , -addPage.getHeight() );
						addPage.hide();
					}
					appendPage = null;
				}
				adjustAllPagesRotation();
				setCurPageIndex();
				bAppendAnim = false;
				bNotDispatchClick = false;
				viewParent.onCtrlEvent( this , this.MSG_CYLINDER_PAGE_ADD_COMPLETE );
			}
			if( bDeleteAnim )
			{
				if( deletePage != null )
				{
					deletePage.remove();
					deletePage = null;
				}
				adjustAllPagesRotation();
				setCurPageIndex();
				DragLayer3D dragLayer = iLoongLauncher.getInstance().getD3dListener().getDragLayer();
				dragLayer.removeAllViews();
				dragLayer.hide();
				viewParent.onCtrlEvent( desktopEdit , DesktopEdit.MSG_DESKTOP_EDIT_DELETE_PAGE );
				bDeleteAnim = false;
				bNotDispatchClick = false;
			}
		}
		if( source == dropTween && type == TweenCallback.COMPLETE )
		{
			bDropTween = false;
			bNotDispatchClick = false;
		}
	}
	
	public void onDrop(
			PageContainer dropPage )
	{
		// TODO Auto-generated method stub
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		float pagePosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		int pageNum = desktopEdit.getPageList().size();
		float radius = calcCylinderRadius( pageNum );
		dropPage.setZ( 0 );
		dropPage.setOriginZ( radius );
		dropPage.setRotationY( 0 );
		dropPage.x = dropPage.getParent().x;
		dropPage.y = dropPage.getParent().y;
		dropTween = dropPage.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , pagePosX , pagePosY , 0 ).setCallback( this );
		bDropTween = true;
		bNotDispatchClick = true;
		pageGroup.addView( dropPage );
		DesktopEdit.dragX = 0;
		DesktopEdit.dragY = 0;
		DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_STOP;
	}
	
	private void adjustAllPagesRotation()
	{
		int pageNum = desktopEdit.getPageList().size();
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			page.setRotation( page.getRotation() % 360 );
		}
	}
	
	private void setCurPageIndex()
	{
		int pageNum = desktopEdit.getPageList().size();
		float rotation;
		for( int i = 0 ; i < pageNum ; i++ )
		{
			PageContainer page = (PageContainer)desktopEdit.getPageList().get( i );
			rotation = Math.abs( page.getRotation() );
			if( rotation <= DesktopEdit.ESPINON )
			{
				desktopEdit.setCurrentPageIndex( page.pageIndex );
			}
		}
	}
	
	private void exitNormalAnim(
			boolean external )
	{
		ViewGroup3D temPageGroup = new ViewGroup3D( "temPageGroup" );
		temPageGroup.transform = true;
		temPageGroup.setSize( desktopEdit.getPageWidth() , desktopEdit.getPageHeight() );
		ViewGroup3D temPageGroup2 = new ViewGroup3D( "temPageGroup2" );
		temPageGroup2.transform = true;
		temPageGroup2.setSize( desktopEdit.getPageWidth() , desktopEdit.getPageHeight() );
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		float pagePosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		int pageNum = pageList.size();
		float radius = calcCylinderRadius( pageNum );
		float polygonAngle = calcPolygonAngle( pageNum );
		float duration = 0.5f;
		timeline = Timeline.createParallel();
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.USER2 , Linear.INOUT , duration , 0 , 0 , 0 ) );
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , duration , 0 , 0 , 0 ) );
		if( pageNum == 2 )
		{
			if( curPageIndex == 0 )
			{
				View3D page = pageList.get( curPageIndex + 1 );
				page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
				page.setOriginZ( 0 );
				page.setRotation( 0 );
				page.setPosition( 0 , 0 );
				temPageGroup.addView( page );
				pagePosX = pagePosX + page.getWidth() + DesktopEdit.page_bg_gap_x;
				temPageGroup.setPosition( pagePosX , pagePosY );
				temPageGroup.setRotationY( polygonAngle );
				temPageGroup.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
				temPageGroup.setOriginZ( radius );
				pageGroup.addView( temPageGroup );
				timeline.push( temPageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
				// 退出这部分动画多轴旋转没有规律,手动调试出的效果,哎...悲催滴...
				temPageGroup.setUser( -DesktopEdit.page_bg_gap_x / 2 );
				timeline.push( temPageGroup.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , duration , temPageGroup.getWidth() / 3 , 0 , 0 ) );
				pagePosX = Utils3D.getScreenWidth() - desktopEdit.getPageWidth();
				timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , pagePosX , 0 , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
			}
			else
			{
				View3D page = pageList.get( curPageIndex - 1 );
				page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
				page.setOriginZ( 0 );
				page.setRotation( 0 );
				page.setPosition( 0 , 0 );
				temPageGroup2.addView( page );
				pagePosX = pagePosX - page.getWidth() - DesktopEdit.page_bg_gap_x;
				temPageGroup2.setPosition( pagePosX , pagePosY );
				temPageGroup2.setRotationY( -polygonAngle );
				temPageGroup2.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
				temPageGroup2.setOriginZ( radius );
				pageGroup.addView( temPageGroup2 );
				timeline.push( temPageGroup2.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
				// 退出这部分动画多轴旋转没有规律,手动调试出的效果,哎...悲催滴...
				temPageGroup2.setUser( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 );
				timeline.push( temPageGroup2.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , duration , page.getWidth() / 1.5f , 0 , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , pagePosX , 0 , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
			}
		}
		else
		{
			for( int i = curPageIndex + 1 ; i < pageList.size() ; i++ )
			{
				if( i == curPageIndex + 1 || i == curPageIndex + 2 )
				{
					if( i == curPageIndex + 1 )
					{
						View3D page = pageList.get( i );
						page.setPosition( 0 , 0 );
						page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
						page.setOriginZ( 0 );
						page.setRotation( 0 );
						temPageGroup.addView( page );
						pagePosX = pagePosX + page.getWidth() + DesktopEdit.page_bg_gap_x;
						temPageGroup.setPosition( pagePosX , pagePosY );
						temPageGroup.setRotationY( polygonAngle );
						temPageGroup.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						temPageGroup.setOriginZ( 0 );
						pageGroup.addView( temPageGroup );
						timeline.push( temPageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
						// 退出这部分动画多轴旋转没有规律,手动调试出的效果,哎...悲催滴...
						temPageGroup.setUser( -DesktopEdit.page_bg_gap_x / 2 );
						timeline.push( temPageGroup.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , duration , temPageGroup.getWidth() / 3 , 0 , 0 ) );
						pagePosX = Utils3D.getScreenWidth() - desktopEdit.getPageWidth();
						timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , pagePosX , 0 , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
					}
					else
					{
						View3D page = pageList.get( i );
						pagePosX = page.getWidth() + DesktopEdit.page_bg_gap_x;
						page.setPosition( pagePosX , 0 );
						page.setRotationY( polygonAngle );
						page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						page.setOriginZ( 0 );
						temPageGroup.addViewAt( 0 , page );
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ).delay( 0.15f ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
					}
				}
				else
				{
					View3D parentView = pageList.get( i - 1 );
					View3D page = pageList.get( i );
					pagePosX = page.getWidth() + DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationY( polygonAngle );
					page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					page.setOriginZ( 0 );
					( (ViewGroup3D)parentView ).addViewAt( 0 , page );
					timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
					timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ).delay( 0.25f ) );
					timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
			}
			pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
			for( int i = curPageIndex - 1 ; i >= 0 ; i-- )
			{
				if( i == curPageIndex - 1 || i == curPageIndex - 2 )
				{
					if( i == curPageIndex - 1 )
					{
						View3D page = pageList.get( i );
						page.setPosition( 0 , 0 );
						page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
						page.setOriginZ( 0 );
						page.setRotation( 0 );
						temPageGroup2.addView( page );
						pagePosX = pagePosX - page.getWidth() - DesktopEdit.page_bg_gap_x;
						temPageGroup2.setPosition( pagePosX , pagePosY );
						temPageGroup2.setRotationY( -polygonAngle );
						temPageGroup2.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						temPageGroup2.setOriginZ( 0 );
						pageGroup.addView( temPageGroup2 );
						timeline.push( temPageGroup2.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
						// 退出这部分动画多轴旋转没有规律,手动调试出的效果,哎...悲催滴...
						temPageGroup2.setUser( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 );
						timeline.push( temPageGroup2.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , duration , page.getWidth() / 1.5f , 0 , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , pagePosX , 0 , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
					}
					else
					{
						View3D page = pageList.get( i );
						pagePosX = -page.getWidth() - DesktopEdit.page_bg_gap_x;
						page.setPosition( pagePosX , 0 );
						page.setRotationY( -polygonAngle );
						page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						page.setOriginZ( 0 );
						temPageGroup2.addViewAt( 0 , page );
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ).delay( 0.15f ) );
						timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
					}
				}
				else
				{
					View3D parentView = pageList.get( i + 1 );
					View3D page = pageList.get( i );
					pagePosX = -page.getWidth() - DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationY( -polygonAngle );
					page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					page.setOriginZ( 0 );
					( (ViewGroup3D)parentView ).addViewAt( 0 , page );
					timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , 0 , 0 , 0 ) );
					timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ).delay( 0.25f ) );
					timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
			}
		}
		View3D curPage = pageList.get( curPageIndex );
		pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		pagePosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		curPage.setPosition( pagePosX , pagePosY );
		pageGroup.addView( curPage );
		curCelllayout = ( (ViewGroup3D)curPage ).findView( "celllayout" );
		if( curCelllayout != null )
		{
			pagePosY = pagePosY - DesktopEdit.page_cell_padding_top;
			curCelllayout.setPosition( pagePosX , pagePosY );
			timeline.push( curCelllayout.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , 0 , 0 , 0 ) );
			timeline.push( curCelllayout.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f , 1f , 0 ) );
			pageGroup.addView( curCelllayout );
			curCelllayout.setUser( 0.1f );
			timeline.push( curCelllayout.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 1 , 0 , 0 ) );
		}
		timeline.push( curPage.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor , 0 ) );
		timeline.push( curPage.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 0 , 0 ) );
		if( addPage.isVisible() )
		{
			pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
			timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , pagePosX , -addPage.getHeight() , 0 ) );
			timeline.push( addPage.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 0 , 0 , 0 ) );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		bExitAnim = true;
		if( DesktopEdit.editModeChangeView != null )
		{
			DesktopEdit.editModeChangeView.stopAllTween();
			DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 0 , 0 , 0 );
		}
		if( !external && !DesktopEdit.bDragActionForIcon && !bExitAnimExtra )
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
	
	public void exitCylinderEditMode()
	{
		bNotDispatchClick = true;
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		View3D temPage = pageList.get( curPageIndex );
		if( temPage.name.equals( "addPage" ) )
		{
			curPageIndex -= 1;
			desktopEdit.setCurrentPageIndex( curPageIndex );
			int pageNum = pageList.size();
			float polygonAngle = calcPolygonAngle( pageNum );
			timeline = Timeline.createParallel();
			for( int i = 0 ; i < pageNum ; i++ )
			{
				View3D page = desktopEdit.getPageList().get( i );
				timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , page.getRotation() + polygonAngle , 0 , 0 ) );
			}
			timeline.start( View3DTweenAccessor.manager ).setCallback( this );
			bExitAnimExtra = true;
		}
		else
		{
			exitNormalAnim( false );
		}
	}
	
	public void buildCylinderAnim(
			boolean fromOtherMode )
	{
		bEnterAnim = true;
		bNotDispatchClick = true;
		ViewGroup3D temPageGroup = new ViewGroup3D( "temPageGroup" );
		temPageGroup.transform = true;
		temPageGroup.setSize( desktopEdit.getPageWidth() , desktopEdit.getPageHeight() );
		ViewGroup3D temPageGroup2 = new ViewGroup3D( "temPageGroup" );
		temPageGroup2.transform = true;
		temPageGroup2.setSize( desktopEdit.getPageWidth() , desktopEdit.getPageHeight() );
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		float pagePosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		int pageNum = pageList.size();
		float radius = calcCylinderRadius( pageNum );
		float polygonAngle = calcPolygonAngle( pageNum );
		float duration = 0f;
		if( fromOtherMode )
		{
			duration = 0.6f;
		}
		else
		{
			duration = 0.5f;
		}
		timeline = Timeline.createParallel();
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , PAGE_GROUP_X_ANGLE , 0 , 0 ) );
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.USER2 , Linear.INOUT , duration , PAGE_GROUP_ORIGIN_Z , 0 , 0 ) );
		//		timeline.push(pageGroup.obtainTween(View3DTweenAccessor.USER,
		//				Linear.INOUT, duration, PAGE_GROUP_Z, 0, 0));
		if( fromOtherMode )
		{
			if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX )
			{
				addPage.show();
				addPage.setPosition( pagePosX , addPagePosY );
				timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , pagePosX , -addPage.getHeight() , 0 ) );
			}
		}
		View3D curPage = pageList.get( curPageIndex );
		curPage.setPosition( pagePosX , pagePosY );
		if( !fromOtherMode )
		{
			curPage.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
			timeline.push( curPage.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f , 1f , 0 ) );
			View3D homeView = ( (ViewGroup3D)curPage ).findView( "pageHomeView" );
			if( homeView != null )
			{
				homeView.color.a = 0;
				timeline.push( homeView.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1 , 0 , 0 ) );
			}
			View3D bgView = ( (ViewGroup3D)curPage ).findView( "pageBg" );
			if( bgView != null )
			{
				bgView.color.a = 0;
				timeline.push( bgView.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1 , 0 , 0 ) );
			}
			View3D deleteView = ( (ViewGroup3D)curPage ).findView( "pageDeleteView" );
			if( deleteView != null )
			{
				deleteView.color.a = 0;
				timeline.push( deleteView.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1 , 0 , 0 ) );
			}
		}
		if( pageNum == 2 )
		{
			if( curPageIndex == 0 )
			{
				View3D page = pageList.get( curPageIndex + 1 );
				page.setPosition( 0 , 0 );
				temPageGroup.addView( page );
				pagePosX = pagePosX + page.getWidth() + DesktopEdit.page_bg_gap_x;
				temPageGroup.setPosition( pagePosX , pagePosY );
				temPageGroup.setRotationVector( 0 , 1 , 0 );
				temPageGroup.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
				temPageGroup.setOriginZ( radius );
				pageGroup.addView( temPageGroup );
				timeline.push( temPageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , polygonAngle , 0 , 0 ) );
				if( !fromOtherMode )
				{
					page.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
					timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f , 1f , 0 ) );
					page.color.a = 0;
					timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1f , 0 , 0 ) );
					pagePosX = Utils3D.getScreenWidth() - desktopEdit.getPageWidth() + DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
			}
			else
			{
				View3D page = pageList.get( curPageIndex - 1 );
				page.setPosition( 0 , 0 );
				temPageGroup2.addView( page );
				pagePosX = pagePosX - page.getWidth() - DesktopEdit.page_bg_gap_x;
				temPageGroup2.setPosition( pagePosX , pagePosY );
				temPageGroup2.setRotationVector( 0 , 1 , 0 );
				temPageGroup2.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
				temPageGroup2.setOriginZ( radius );
				pageGroup.addView( temPageGroup2 );
				timeline.push( temPageGroup2.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , -polygonAngle , 0 , 0 ) );
				if( !fromOtherMode )
				{
					page.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
					timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f , 1f , 0 ) );
					page.color.a = 0;
					timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1f , 0 , 0 ) );
					pagePosX = -( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) - DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , 0 , 0 , 0 ) );
				}
			}
		}
		else
		{
			for( int i = curPageIndex + 1 ; i < pageList.size() ; i++ )
			{
				if( i == curPageIndex + 1 || i == curPageIndex + 2 )
				{
					if( i == curPageIndex + 1 )
					{
						View3D page = pageList.get( i );
						page.setPosition( 0 , 0 );
						temPageGroup.addView( page );
						pagePosX = pagePosX + page.getWidth() + DesktopEdit.page_bg_gap_x;
						temPageGroup.setPosition( pagePosX , pagePosY );
						temPageGroup.setRotationVector( 0 , 1 , 0 );
						temPageGroup.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						pageGroup.addView( temPageGroup );
						timeline.push( temPageGroup.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , polygonAngle , 0 , 0 ) );
						if( !fromOtherMode )
						{
							page.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
							timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f , 1f , 0 ) );
							page.color.a = 0;
							timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1f , 0 , 0 ) );
							pagePosX = Utils3D.getScreenWidth() - desktopEdit.getPageWidth() + DesktopEdit.page_bg_gap_x;
							page.setPosition( pagePosX , 0 );
							timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , 0 , 0 , 0 ) );
						}
					}
					else
					{
						View3D page = pageList.get( i );
						pagePosX = page.getWidth() + DesktopEdit.page_bg_gap_x;
						page.setPosition( pagePosX , 0 );
						page.setRotationVector( 0 , 1 , 0 );
						page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						temPageGroup.addViewAt( 0 , page );
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , polygonAngle , 0 , 0 ) );
					}
				}
				else
				{
					View3D parentView = pageList.get( i - 1 );
					View3D page = pageList.get( i );
					pagePosX = page.getWidth() + DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationVector( 0 , 1 , 0 );
					page.setOrigin( -DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					( (ViewGroup3D)parentView ).addViewAt( 0 , page );
					timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , polygonAngle , 0 , 0 ) );
				}
			}
			pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
			for( int i = curPageIndex - 1 ; i >= 0 ; i-- )
			{
				if( i == curPageIndex - 1 || i == curPageIndex - 2 )
				{
					if( i == curPageIndex - 1 )
					{
						View3D page = pageList.get( i );
						page.setPosition( 0 , 0 );
						temPageGroup2.addView( page );
						pagePosX = pagePosX - page.getWidth() - DesktopEdit.page_bg_gap_x;
						temPageGroup2.setPosition( pagePosX , pagePosY );
						temPageGroup2.setRotationVector( 0 , 1 , 0 );
						temPageGroup2.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						pageGroup.addView( temPageGroup2 );
						timeline.push( temPageGroup2.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , -polygonAngle , 0 , 0 ) );
						if( !fromOtherMode )
						{
							page.setScale( 1f / DesktopEdit.scaleFactor , 1f / DesktopEdit.scaleFactor );
							timeline.push( page.obtainTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , duration , 1f , 1f , 0 ) );
							page.color.a = 0;
							timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1f , 0 , 0 ) );
							pagePosX = -( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) - DesktopEdit.page_bg_gap_x;
							page.setPosition( pagePosX , 0 );
							timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , duration , 0 , 0 , 0 ) );
						}
					}
					else
					{
						View3D page = pageList.get( i );
						pagePosX = -page.getWidth() - DesktopEdit.page_bg_gap_x;
						page.setPosition( pagePosX , 0 );
						page.setRotationVector( 0 , 1 , 0 );
						page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
						temPageGroup2.addViewAt( 0 , page );
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , -polygonAngle , 0 , 0 ) );
					}
				}
				else
				{
					View3D parentView = pageList.get( i + 1 );
					View3D page = pageList.get( i );
					pagePosX = -page.getWidth() - DesktopEdit.page_bg_gap_x;
					page.setPosition( pagePosX , 0 );
					page.setRotationVector( 0 , 1 , 0 );
					page.setOrigin( page.getWidth() + DesktopEdit.page_bg_gap_x / 2 , page.getHeight() / 2 );
					( (ViewGroup3D)parentView ).addViewAt( 0 , page );
					timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Linear.INOUT , duration , -polygonAngle , 0 , 0 ) );
				}
			}
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		pageGroup.addView( curPage );
	}
	
	public void buildCylinderAnimDone()
	{
		pageGroup.removeAllViews();
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int currentPageIndex = desktopEdit.getCurrentPageIndex();
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		float pagePosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		int pageNum = pageList.size();
		float radius = calcCylinderRadius( pageNum );
		float polygonAngle = calcPolygonAngle( pageNum );
		int j = 0;
		for( int i = currentPageIndex ; i < pageNum ; i++ )
		{
			View3D page = pageList.get( i );
			page.setPosition( pagePosX , pagePosY );
			page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
			page.setOriginZ( radius );
			page.setRotationY( j * polygonAngle );
			pageGroup.addView( page );
			j++;
		}
		for( int i = 0 ; i <= currentPageIndex - 1 ; i++ )
		{
			View3D page = pageList.get( i );
			page.setPosition( pagePosX , pagePosY );
			page.setOrigin( page.getWidth() / 2 , page.getHeight() / 2 );
			page.setOriginZ( radius );
			page.setRotationY( j * polygonAngle );
			pageGroup.addView( page );
			j++;
		}
		View3D curPage;
		View3D prePage;
		View3D nextPage;
		curPage = pageList.get( currentPageIndex );
		pageGroup.addView( curPage );
		if( pageNum > 4 )
		{
			prePage = pageList.get( desktopEdit.prePageIndex() );
			pageGroup.addViewBefore( curPage , prePage );
			nextPage = pageList.get( desktopEdit.nextPageIndex() );
			pageGroup.addViewBefore( curPage , nextPage );
		}
		grooveView.hide();
		pageGroup.addView( grooveView );
		if( !DesktopEdit.bDragActionForIcon )
		{
			if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX )
			{
				addPage.show();
				pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
				addPage.startTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pagePosX , addPagePosY , 0 );
			}
		}
		if( DesktopEdit.editModeChangeView != null )
		{
			DesktopEdit.editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_plane );
			DesktopEdit.editModeChangeView.stopAllTween();
			DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 1 , 0 , 0 );
		}
	}
	
	public void setupEditPageGroup(
			boolean fromOtherMode )
	{
		pageGroup = new PageGroup( "pageGroup" );
		pageGroup.transform = true;
		//		pageGroup.setZ(0f);
		pageGroup.setOriginZ( 0f );
		pageGroup.setRotation( 0f );
		pageGroup.setRotationVector( 1 , 0 , 0 );
		this.addView( pageGroup );
		addPage = new PageContainer( "addPage" , desktopEdit.getPageWidth() , desktopEdit.getPageHeight() );
		( (PageContainer)addPage ).buildAddPage( -1 );
		addPagePosY = -addPage.getHeight() * 9 / 10;
		addPage.setPosition( ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2 , -addPage.getHeight() );
		this.addView( addPage );
		addPage.hide();
		buildCylinderAnim( fromOtherMode );
		grooveViewHeight *= DesktopEdit.scaleHeight;
		grooveView = new View3D( "grooveView" , R3D.getTextureRegion( R3D.desktopEdit_page_groove ) );
		grooveView.setSize( desktopEdit.getPageWidth() + 46f , grooveViewHeight );
		grooveView.hide();
		pageGroup.addView( grooveView );
		if( fromOtherMode )
		{
			if( DesktopEdit.editModeChangeView != null )
			{
				DesktopEdit.editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_cylinder );
				DesktopEdit.editModeChangeView.region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				this.addView( DesktopEdit.editModeChangeView );
				DesktopEdit.editModeChangeView.color.a = 1;
				DesktopEdit.editModeChangeView.stopAllTween();
				DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.6f , 0 , 0 , 0 );
			}
		}
		else
		{
			if( DesktopEdit.editModeChangeView != null )
			{
				DesktopEdit.editModeChangeView.region = R3D.getTextureRegion( R3D.desktopEdit_mode_plane );
				DesktopEdit.editModeChangeView.region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				this.addView( DesktopEdit.editModeChangeView );
				DesktopEdit.editModeChangeView.color.a = 0;
				DesktopEdit.editModeChangeView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.5f , 1 , 0 , 0 );
			}
		}
	}
	
	public static float calcPolygonAngle(
			int pageNum )
	{
		return (float)( 360f / pageNum );
	}
	
	public static float calcCylinderRadius(
			int pageNum )
	{
		if( pageNum == 2 )
		{
			return -Utils3D.getScreenWidth() / 4.5f;
		}
		else if( pageNum == 3 )
		{
			return -Utils3D.getScreenWidth() / 4.5f;
		}
		else if( pageNum == 4 )
		{
			return -Utils3D.getScreenWidth() / 2.67f;
		}
		else if( pageNum == 5 )
		{
			return -Utils3D.getScreenWidth() / 1.95f;
		}
		else if( pageNum == 6 )
		{
			return -Utils3D.getScreenWidth() / 1.55f;
		}
		else if( pageNum == 7 )
		{
			return -Utils3D.getScreenWidth() / 1.29f;
		}
		else if( pageNum == 8 )
		{
			return -Utils3D.getScreenWidth() / 1.11f;
		}
		else if( pageNum == 9 )
		{
			return -Utils3D.getScreenWidth() / 0.985f;
		}
		return 0;
	}
	
	private float calcRotationAngle(
			int pageNum )
	{
		if( pageNum == 1 )
		{
			return xScale * 60;
		}
		else if( pageNum == 2 )
		{
			return xScale * 290;
		}
		else if( pageNum == 3 )
		{
			return xScale * 200;
		}
		else if( pageNum == 4 )
		{
			return xScale * 160;
		}
		else if( pageNum == 5 )
		{
			return xScale * 130;
		}
		else if( pageNum == 6 )
		{
			return xScale * 110;
		}
		else if( pageNum == 7 )
		{
			return xScale * 90;
		}
		else if( pageNum == 8 )
		{
			return xScale * 75;
		}
		else if( pageNum == 9 )
		{
			return xScale * 65;
		}
		return 0;
	}
	
	private void updateAllPagesDrawOrder()
	{
		int pageNum = desktopEdit.getPageList().size();
		float polygonAngle = calcPolygonAngle( pageNum );
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			if( pageNum == 2 || pageNum == 3 )
			{
				if( ( Math.abs( ( page.getRotation() % 360 ) ) >= 0 && Math.abs( ( page.getRotation() % 360 ) ) < 90 ) || ( Math.abs( ( page.getRotation() % 360 ) ) > 270 && Math.abs( ( page
						.getRotation() % 360 ) ) < 360 ) )
				{
					pageGroup.addView( page );
				}
			}
			else if( pageNum == 4 )
			{
				if( ( Math.abs( ( page.getRotation() % 360 ) ) >= 0 && Math.abs( ( page.getRotation() % 360 ) ) < 80 ) || ( Math.abs( ( page.getRotation() % 360 ) ) > 280 && Math.abs( ( page
						.getRotation() % 360 ) ) < 360 ) )
				{
					pageGroup.addView( page );
				}
			}
			else
			{
				if( ( Math.abs( ( page.getRotation() % 360 ) ) >= 0 && Math.abs( ( page.getRotation() % 360 ) ) <= Math.abs( polygonAngle ) + 10 ) || ( Math.abs( ( page.getRotation() % 360 ) ) >= ( pageNum - 1 ) * Math
						.abs( polygonAngle ) - 10 && Math.abs( ( page.getRotation() % 360 ) ) < 360 ) )
				{
					pageGroup.addView( page );
				}
			}
		}
	}
	
	private void updateEffect(
			float deltaX ,
			float deltaY )
	{
		float yAmplify = deltaY * 1.3f;
		this.xScale = xScale + deltaX / Utils3D.getScreenWidth();
		this.yScale = yScale + yAmplify / Utils3D.getScreenHeight();
		float tempYScale;
		float yScaleRatio = PAGE_GROUP_X_ANGLE / 90;
		if( yScale > yScaleRatio )
		{
			tempYScale = yScaleRatio;
		}
		else if( yScale < -yScaleRatio )
		{
			tempYScale = -yScaleRatio;
		}
		else
		{
			tempYScale = yScale;
		}
		float xAngle = tempYScale * 90;
		pageGroup.setRotationX( PAGE_GROUP_X_ANGLE + xAngle );
		int pageNum = desktopEdit.getPageList().size();
		float rotateAngle = calcRotationAngle( pageNum );
		float gapAngle = rotateAngle - rotateAngleRec;
		rotateAngleRec = rotateAngle;
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			page.setRotationY( page.getRotation() + gapAngle );
		}
	}
	
	private void stopAutoEffect()
	{
		bAutoEffectAnim = false;
		bFlingAnim = false;
		xScale = 0;
		yScale = 0;
		rotateAngleRec = 0;
		if( timeline != null && !timeline.isFinished() )
		{
			timeline.free();
			timeline = null;
		}
		adjustAllPagesRotation();
	}
	
	private void startAutoEffect()
	{
		boolean needAutoEffect = true;
		int pageNum = desktopEdit.getPageList().size();
		float polygonAngle = calcPolygonAngle( pageNum );
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			if( Math.abs( page.getRotation() ) <= DesktopEdit.ESPINON )
			{
				needAutoEffect = false;
			}
		}
		if( xScale == 0 && yScale == 0 && !needAutoEffect )
		{
			bNotDispatchClick = false;
			adjustAllPagesRotation();
			setCurPageIndex();
			return;
		}
		bAutoEffectAnim = true;
		bNotDispatchClick = true;
		float temAngle = 0;
		float targetAngle = 0;
		timeline = Timeline.createParallel();
		timeline.push( pageGroup.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.8f , PAGE_GROUP_X_ANGLE , 0 , 0 ) );
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			temAngle = page.getRotation() % polygonAngle;
			if( temAngle >= 0 )
			{
				if( temAngle >= polygonAngle / 2 )
				{
					targetAngle = page.getRotation() + ( polygonAngle - temAngle );
				}
				else
				{
					targetAngle = page.getRotation() - temAngle;
				}
			}
			else
			{
				if( Math.abs( temAngle ) >= polygonAngle / 2 )
				{
					targetAngle = page.getRotation() - ( polygonAngle + temAngle );
				}
				else
				{
					targetAngle = page.getRotation() - temAngle;
				}
			}
			timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.8f , targetAngle , 0 , 0 ) );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	private void updateCelllayoutAlpha()
	{
		int pageNum = desktopEdit.getPageList().size();
		for( int i = 0 ; i < pageNum ; i++ )
		{
			View3D page = desktopEdit.getPageList().get( i );
			ViewGroup3D cell = (ViewGroup3D)( (ViewGroup3D)page ).findView( "celllayout" );
			if( cell != null )
			{
				for( int k = 0 ; k < cell.getChildCount() ; k++ )
				{
					View3D view = cell.getChildAt( k );
					if( Math.abs( page.getRotation() % 360 ) > 90 && Math.abs( page.getRotation() % 360 ) < 270 )
					{
						view.color.a = 0.35f;
					}
					else
					{
						view.color.a = 1f;
					}
				}
			}
		}
	}
	
	public void handleAppendAnimtion(
			View3D newPage )
	{
		this.appendPage = newPage;
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		float pagePosY = ( Utils3D.getScreenHeight() - desktopEdit.getPageHeight() ) / 2;
		int pageNum = 0;
		float radius = 0;
		float polygonAngle = 0;
		float duration = 1f;
		timeline = Timeline.createSequence();
		Timeline temTimeline = Timeline.createParallel();
		pageNum = pageList.size();
		radius = calcCylinderRadius( pageNum );
		polygonAngle = calcPolygonAngle( pageNum );
		int j = 1;
		float targetAngle = 0;
		int curPageIndex = desktopEdit.getCurrentPageIndex();
		for( int i = curPageIndex + 1 ; i < pageNum ; i++ )
		{
			View3D page = pageList.get( i );
			if( page.getRotation() < 0 )
			{
				targetAngle = j * polygonAngle - 360;
			}
			else
			{
				targetAngle = j * polygonAngle;
			}
			j++;
			temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , targetAngle , 0 , 0 ) );
			page.setUser( page.getOriginZ() );
			temTimeline.push( page.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , duration , radius , 0 , 0 ) );
		}
		for( int i = 0 ; i < curPageIndex ; i++ )
		{
			View3D page = pageList.get( i );
			if( page.getRotation() < 0 )
			{
				targetAngle = j * polygonAngle - 360;
			}
			else
			{
				targetAngle = j * polygonAngle;
			}
			j++;
			temTimeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , targetAngle , 0 , 0 ) );
			page.setUser( page.getOriginZ() );
			temTimeline.push( page.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , duration , radius , 0 , 0 ) );
		}
		timeline.push( temTimeline );
		addPage.setPosition( pagePosX , -addPage.getHeight() );
		appendPage.setPosition( pagePosX , addPagePosY );
		appendPage.setOriginZ( 0 );
		appendPage.setRotationVector( 1 , 0 , 0 );
		this.addView( appendPage );
		Timeline temTimeline2 = Timeline.createParallel();
		temTimeline2.push( appendPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.INOUT , 1f , appendPage.getX() , pagePosY , 0 ) );
		temTimeline2.push( appendPage.obtainTween( View3DTweenAccessor.ROTATION , Elastic.INOUT , 1f , PAGE_GROUP_X_ANGLE , 0 , 0 ) );
		appendPage.setUser( radius );
		temTimeline2.push( appendPage.obtainTween( View3DTweenAccessor.USER , Elastic.INOUT , 1f , PAGE_GROUP_ORIGIN_Z , 0 , 0 ) );
		timeline.push( temTimeline2 );
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		bAppendAnim = true;
		bNotDispatchClick = true;
	}
	
	public void handleDeleteAnimtion(
			View3D deletePage )
	{
		this.deletePage = deletePage;
		timeline = Timeline.createSequence();
		Timeline temTimeline = Timeline.createParallel();
		Timeline temTimeline2 = Timeline.createParallel();
		temTimeline.push( deletePage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , deletePage.getX() , deletePage.getHeight() , 0 ) );
		temTimeline.push( deletePage.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , 0 , 0 , 0 ) );
		timeline.push( temTimeline );
		ArrayList<View3D> pageList = desktopEdit.getPageList();
		int pageNum = pageList.size();
		float radius = calcCylinderRadius( pageNum );
		float polygonAngle = calcPolygonAngle( pageNum );
		int j = 0;
		float rotateAngle = 0;
		for( int i = ( (PageContainer)deletePage ).pageIndex ; i < pageNum ; i++ )
		{
			View3D page = pageList.get( i );
			if( page.getRotation() < 0 )
			{
				rotateAngle = j * polygonAngle - ( page.getRotation() + 360 );
			}
			else
			{
				rotateAngle = j * polygonAngle - page.getRotation();
			}
			temTimeline2.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 1f , page.getRotation() + rotateAngle , 0 , 0 ) );
			page.setUser( page.getOriginZ() );
			temTimeline2.push( page.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , 1f , radius , 0 , 0 ) );
			j++;
		}
		for( int i = 0 ; i < ( (PageContainer)deletePage ).pageIndex ; i++ )
		{
			View3D page = pageList.get( i );
			if( page.getRotation() < 0 )
			{
				rotateAngle = j * polygonAngle - ( page.getRotation() + 360 );
			}
			else
			{
				rotateAngle = j * polygonAngle - page.getRotation();
			}
			temTimeline2.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 1f , page.getRotation() + rotateAngle , 0 , 0 ) );
			page.setUser( page.getOriginZ() );
			temTimeline2.push( page.obtainTween( View3DTweenAccessor.USER , Cubic.OUT , 1f , radius , 0 , 0 ) );
			j++;
		}
		temTimeline2.push( DesktopEdit.editModeChangeView.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.5f , 1 , 0 , 0 ) );
		timeline.push( temTimeline2 );
		float pagePosX = ( Utils3D.getScreenWidth() - desktopEdit.getPageWidth() ) / 2;
		if( pageList.size() < DesktopEdit.PAGE_COUNT_MAX && !addPage.isVisible() )
		{
			addPage.show();
			timeline.push( addPage.obtainTween( View3DTweenAccessor.POS_XY , Elastic.OUT , 0.5f , pagePosX , addPagePosY , 0 ) );
		}
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		bDeleteAnim = true;
		bNotDispatchClick = true;
	}
	
	private boolean canAutoMove(
			float x ,
			float y )
	{
		//		DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_STOP;
		if( desktopEdit.getPageNum() == 0 || desktopEdit.getPageNum() == 1 )
		{
			return false;
		}
		if( y >= R3D.workspace_cell_height && y < ( Utils3D.getScreenHeight() - R3D.workspace_cell_height ) )
		{
			if( x <= R3D.workspace_cell_width )
			{
				DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_LEFT;
				return true;
			}
			else if( x >= ( Utils3D.getScreenWidth() - R3D.workspace_cell_width ) )
			{
				DesktopEdit.autoMoveStatus = DesktopEdit.AUTO_MOVE_RIGHT;
				return true;
			}
		}
		return false;
	}
	
	private void autoMoveForDragAction()
	{
		int pageNum = desktopEdit.getPageList().size();
		float polygonAngle = 0;
		float targetAngle = 0;
		if( DesktopEdit.bDragActionForIcon )
		{
			polygonAngle = calcPolygonAngle( pageNum );
			if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_LEFT )
			{
				targetAngle = polygonAngle;
			}
			else if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_RIGHT )
			{
				targetAngle = -polygonAngle;
			}
			timeline = Timeline.createParallel();
			for( int i = 0 ; i < pageNum ; i++ )
			{
				View3D page = desktopEdit.getPageList().get( i );
				timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , page.getRotation() + targetAngle , 0 , 0 ) );
			}
			timeline.start( View3DTweenAccessor.manager ).setCallback( this );
			SendMsgToAndroid.vibrator( R3D.vibrator_duration );
			bAutoMoveAnim = true;
		}
		else if( DesktopEdit.bDragActionForPage )
		{
			polygonAngle = calcPolygonAngle( pageNum + 1 );
			timeline = Timeline.createParallel();
			if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_LEFT )
			{
				targetAngle = polygonAngle;
				for( int i = 0 ; i < pageNum ; i++ )
				{
					View3D page = desktopEdit.getPageList().get( i );
					// 通过tween动画计算得到的rotation值是个float近似值,所以定个范围
					if( ( ( page.getRotation() >= pageNum * polygonAngle - 10 ) && ( page.getRotation() <= pageNum * polygonAngle + 10 ) ) || ( ( page.getRotation() >= -polygonAngle - 10 ) && ( page
							.getRotation() <= -polygonAngle + 10 ) ) )
					{
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , page.getRotation() + targetAngle * 2 , 0 , 0 ) );
					}
					else
					{
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , page.getRotation() + targetAngle , 0 , 0 ) );
					}
				}
			}
			else if( DesktopEdit.autoMoveStatus == DesktopEdit.AUTO_MOVE_RIGHT )
			{
				targetAngle = -polygonAngle;
				for( int i = 0 ; i < pageNum ; i++ )
				{
					View3D page = desktopEdit.getPageList().get( i );
					if( ( ( page.getRotation() >= polygonAngle - 10 ) && ( page.getRotation() <= polygonAngle + 10 ) ) || ( ( page.getRotation() >= ( -pageNum * polygonAngle ) - 10 ) && ( page
							.getRotation() <= ( -pageNum * polygonAngle ) + 10 ) ) )
					{
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , page.getRotation() + targetAngle * 2 , 0 , 0 ) );
					}
					else
					{
						timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , page.getRotation() + targetAngle , 0 , 0 ) );
					}
				}
			}
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
				exitCylinderEditMode();
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
		return( bEnterAnim || bExitAnim || bAutoMoveAnim || bAutoEffectAnim || bDeleteAnim || bFlingAnim );
	}
	
	public void stopAllAnimation()
	{
		if( dropTween != null )
		{
			dropTween.free();
			dropTween = null;
		}
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
	}
	
	class PageGroup extends ViewGroup3D
	{
		
		public PageGroup()
		{
			super();
			// TODO Auto-generated constructor stub
		}
		
		public PageGroup(
				String name )
		{
			super( name );
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean pointerInParent(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			return view3dTouchCheck.isPointIn( this , 0 , 0 , this.getWidth() , this.getHeight() , x , y );
		}
		
		@Override
		public void toLocalCoordinates(
				View3D descendant ,
				Vector2 point )
		{
			// TODO Auto-generated method stub
		}
	}
}
