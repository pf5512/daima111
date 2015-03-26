package com.iLoong.launcher.Folder3D;


import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class FolderIconPath extends ViewGroup3D
{
	
	public Vector3 downPath = new Vector3();
	public Vector3 dragPath = new Vector3();
	private final int CIRCLE_DST_TOLERANCE = 24;
	private final int CIRCLE_DST_AVG = 4;
	private float totalDragDst = 0;
	private FolderIcon3D mFolderIcon;
	private CatmullRomSpline spline;
	//private Vector3[] path;
	private List<SplinePoint> TotalPath = new ArrayList<SplinePoint>();
	private ArrayList<View3D> controlIcon = new ArrayList<View3D>();
	private Timeline animation_line = null;
	private Tween buttonShowTween = null;
	private Timeline buttonHideTween = null;
	private Timeline viewTween = null;
	private ImageView3D buttonOK;
	private boolean bCanDealButtonDown = false;
	private boolean bTouchDown = false;
	private float buttonScale = 1.0f;
	
	public FolderIconPath(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
		// this.setPosition(0, 0);
	}
	
	private boolean multiTouch2DiscardDrag()
	{
		if( totalDragDst > ( getWidth() + getHeight() ) * 2.5 )
		{
			return true;
		}
		return false;
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
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			DealButtonOKDown();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	/* (non-Javadoc)
	 * @see com.iLoong.launcher.UI3DEngine.ViewGroup3D#onTouchDown(float, float, int)
	 */
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( buttonOK.isVisible() == true && pointer == 0 && bCanDealButtonDown == true )
		{
			if( x > buttonOK.x - buttonOK.width / 2 && x < buttonOK.x + buttonOK.width + buttonOK.width / 2 && y > buttonOK.y - buttonOK.height / 2 && y < buttonOK.y + buttonOK.height + buttonOK.height / 2 )
			{
				bTouchDown = true;
			}
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( buttonOK.isVisible() == false && pointer == 0 )
		{
			buttonOK.show();
			buttonOK.setScale( 0f , 0f );
			buttonOK.startTween( View3DTweenAccessor.SCALE_XY , Quad.IN , 0.5f , 0f , 0 , 0f );
			buttonShowTween = Tween.to( buttonOK , View3DTweenAccessor.ROTATION , 0.5f ).target( 360 , 0 , 0 ).ease( Quad.OUT ).start( View3DTweenAccessor.manager ).setCallback( this );
			return true;
		}
		////		
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( buttonOK.isVisible() == true && bTouchDown )
		{
			if( x > buttonOK.x - buttonOK.width / 2 && x < buttonOK.x + buttonOK.width + buttonOK.width / 2 && y > buttonOK.y - buttonOK.height / 2 && y < buttonOK.y + buttonOK.height + buttonOK.height / 2 )
			{
				DealButtonOKDown();
				return true;
			}
		}
		return super.onClick( x , y );
	}
	
	private void animation_rotate()
	{
		int Count = controlIcon.size();
		View3D view;
		float delayFactor = 0;
		if( Count > R3D.folder_max_num / 2 )
		{
			delayFactor = 0.06f;
		}
		else
		{
			delayFactor = 0.1f;
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			view = controlIcon.get( i );
			if( view instanceof Icon3D )
			{
				Icon3D iconView = (Icon3D)view;
				iconView.setInShowFolder( false );
				iconView.setItemInfo( iconView.getItemInfo() );
			}
			view.x = view.x - mFolderIcon.x;
			view.y = view.y - mFolderIcon.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
			viewTween = null;
			viewTween = Timeline.createParallel();
			view.setRotationVector( 0 , 0 , 1 );
			if( i == Count - 2 )
			{
				mFolderIcon.getPos( 1 );
			}
			else if( i == Count - 3 )
			{
				mFolderIcon.getPos( 2 );
			}
			else
			{
				mFolderIcon.getPos( 0 );
			}
			viewTween.push( Tween.to( view , View3DTweenAccessor.ROTATION , 0.2f ).target( mFolderIcon.getRotateDegree() , 0 , 0 ).ease( Linear.INOUT ).delay( 0.3f + delayFactor * i ) );
			viewTween.push( Tween.to( view , View3DTweenAccessor.SCALE_XY , 0.2f ).target( mFolderIcon.getScaleFactor( 0 ) , mFolderIcon.getScaleFactor( 0 ) , 0 ).delay( 0.3f + delayFactor * i )
					.ease( Linear.INOUT ) );
			viewTween.push( Tween.to( view , View3DTweenAccessor.POS_XY , 0.5f ).target( mFolderIcon.getPosx() , mFolderIcon.getPosy() ).ease( Cubic.OUT ).delay( delayFactor * ( i ) ) );
			buttonHideTween.push( viewTween );
		}
	}
	
	private void animation_iphone()
	{
		int Count = controlIcon.size();
		View3D view;
		float delayFactor = 0;
		if( Count > R3D.folder_max_num / 2 )
		{
			delayFactor = 0.02f;
		}
		else
		{
			delayFactor = 0.04f;
		}
		float duration = 0.4f;
		for( int i = 0 ; i < Count ; i++ )
		{
			view = controlIcon.get( i );
			if( view instanceof Icon3D )
			{
				Icon3D iconView = (Icon3D)view;
				iconView.setInShowFolder( false );
				iconView.setItemInfo( iconView.getItemInfo() );
			}
			view.x = view.x - mFolderIcon.x;
			view.y = view.y - mFolderIcon.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
			;
			viewTween = null;
			viewTween = Timeline.createParallel();
			mFolderIcon.getPos( i );
			viewTween.push( Tween.to( view , View3DTweenAccessor.POS_XY , duration ).target( mFolderIcon.getPosx() , mFolderIcon.getPosy() , 0 ).delay( delayFactor * i ).ease( Linear.INOUT ) );
			viewTween.push( Tween.to( view , View3DTweenAccessor.SCALE_XY , duration ).target( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) , 0 ).delay( delayFactor * i )
					.ease( Cubic.OUT ) );
			buttonHideTween.push( viewTween );
		}
	}
	
	public void DealButtonOKDown()
	{
		if( buttonOK.isVisible() && bCanDealButtonDown == true )
		{
			bCanDealButtonDown = false;
			bTouchDown = false;
			mFolderIcon.bAnimate = true;
			totalDragDst = 0;
			TotalPath.clear();
			stopAnimation();
			mFolderIcon.changeTextureRegion( controlIcon , mFolderIcon.getIconBmpHeight() );
			mFolderIcon.FolderIconNormalScreen();
			buttonOK.x = buttonOK.x - mFolderIcon.x;
			buttonOK.y = buttonOK.y - mFolderIcon.y;
			buttonHideTween = Timeline.createParallel();
			if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
			{
				animation_rotate();
			}
			else
			{
				animation_iphone();
			}
			if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
			{
				buttonHideTween.push( Tween.to( buttonOK , View3DTweenAccessor.ROTATION , 0.5f ).target( -360 , 0 , 0 ).ease( Quad.OUT ) );
				buttonHideTween.push( Tween.to( buttonOK , View3DTweenAccessor.SCALE_XY , 0.5f ).target( 0 , 0 , 0 ).ease( Quad.OUT ) );
			}
			buttonHideTween.start( View3DTweenAccessor.manager ).setCallback( this );
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source == animation_line )
		{
			animation_line = null;
		}
		if( source == buttonShowTween && type == TweenCallback.COMPLETE )
		{
			buttonShowTween = null;
			buttonOK.setRotation( 0 );
			bCanDealButtonDown = true;
		}
		if( source == buttonHideTween && type == TweenCallback.COMPLETE )
		{
			buttonHideTween = null;
			mFolderIcon.closeFolderIconPath();
		}
		if( source == viewTween && type == TweenCallback.COMPLETE )
		{
			viewTween = null;
		}
	}
	
	private void stopAnimation()
	{
		if( animation_line != null && !animation_line.isFinished() )
		{
			animation_line.free();
			animation_line = null;
		}
		if( buttonHideTween != null && !buttonHideTween.isFinished() )
		{
			buttonHideTween.free();
			buttonHideTween = null;
		}
		if( viewTween != null && !viewTween.isFinished() )
		{
			viewTween.free();
			viewTween = null;
		}
	}
	
	private void starAnimation()
	{
		View3D view;
		stopAnimation();
		animation_line = Timeline.createParallel();
		int Count = controlIcon.size();
		int targetPosOffset = ( TotalPath.size() - 1 ) / Count;
		/*全部图标都可以等距离移动*/
		if( Count > 4 )
		{
			for( int i = 0 ; i < Count ; i++ )
			{
				view = controlIcon.get( i );
				if( view instanceof Icon3D )
				{
					Icon3D iconView = (Icon3D)view;
					iconView.setInShowFolder( true );
					iconView.setItemInfo( iconView.getItemInfo() );
				}
				if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
				{
					animation_line.push( Tween.to( view , View3DTweenAccessor.CPOS_XY , 0.1f )
							.target( TotalPath.get( targetPosOffset * i + targetPosOffset / 2 ).x , TotalPath.get( targetPosOffset * i + targetPosOffset / 2 ).y ).ease( Linear.INOUT ) );
				}
				else
				{
					animation_line.push( Tween.to( view , View3DTweenAccessor.CPOS_XY , 0.1f )
							.target( TotalPath.get( targetPosOffset * ( Count - 1 - i ) + targetPosOffset / 2 ).x , TotalPath.get( targetPosOffset * ( Count - 1 - i ) + targetPosOffset / 2 ).y )
							.ease( Linear.INOUT ) );
				}
			}
		}
		else
		{
			for( int i = 0 ; i < Count ; i++ )
			{
				view = controlIcon.get( i );
				if( view instanceof Icon3D )
				{
					Icon3D iconView = (Icon3D)view;
					iconView.setInShowFolder( true );
					iconView.setItemInfo( iconView.getItemInfo() );
				}
				if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
				{
					animation_line.push( Tween.to( view , View3DTweenAccessor.CPOS_XY , 0.1f )
							.target( TotalPath.get( targetPosOffset * i + targetPosOffset / 2 ).x , TotalPath.get( targetPosOffset * i + targetPosOffset / 2 ).y ).ease( Linear.INOUT ) );
				}
				else
				{
					animation_line.push( Tween.to( view , View3DTweenAccessor.CPOS_XY , 0.1f )
							.target( TotalPath.get( targetPosOffset * ( Count - 1 - i ) + targetPosOffset ).x , TotalPath.get( targetPosOffset * ( Count - 1 - i ) + targetPosOffset ).y )
							.ease( Linear.INOUT ) );
				}
			}
		}
		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	private void circle_sendMoveMsg(
			float x ,
			float y ,
			float moveDst )
	{
		spline.getControlPoints().clear();
		spline.add( downPath );
		spline.add( downPath );
		spline.add( dragPath );
		spline.add( dragPath );
		int numPoints = (int)( ( moveDst - CIRCLE_DST_AVG + 1 ) / CIRCLE_DST_AVG );
		List<Vector3> segmentPath = spline.getPath( numPoints );
		for( int i = 1 ; i < segmentPath.size() ; i++ )
		{
			SplinePoint temp = new SplinePoint();
			temp.x = segmentPath.get( i ).x;
			temp.y = segmentPath.get( i ).y;
			TotalPath.add( temp );
		}
		segmentPath.clear();
		starAnimation();
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( multiTouch2DiscardDrag() == true )
		{
			return true;
		}
		if( buttonOK.isVisible() )
		{
			return true;
		}
		if( controlIcon.size() <= 0 )
		{
			return true;
		}
		circle_operate( x , y );
		return true;
	}
	
	private void circle_operate(
			float x ,
			float y )
	{
		float moveDst = dragPath.dst( x , y , 0 );
		if( moveDst > CIRCLE_DST_TOLERANCE )
		{
			totalDragDst += moveDst;
			if( multiTouch2DiscardDrag() == true )
			{
				/* Do nothing */
			}
			else
			{
				dragPath.set( x , y , 0 );
				circle_sendMoveMsg( x , y , moveDst );
				downPath.set( dragPath );
			}
		}
	}
	
	void setFolderIcon(
			FolderIcon3D icon )
	{
		View3D view;
		mFolderIcon = icon;
		spline = new CatmullRomSpline();
		buttonOK = new ImageView3D( "button_ok" , R3D.getTextureRegion( "public-button-return" ) );
		buttonScale = 1.0f;
		buttonOK.setSize( R3D.icongroup_button_width * buttonScale , R3D.icongroup_button_height * buttonScale );
		buttonOK.setOrigin( buttonOK.width / 2 , buttonOK.height / 2 );
		if( ( mFolderIcon.mInfo.x + mFolderIcon.folder_front.width > getWidth() - buttonOK.width ) )
		{
			if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
			{
				buttonOK.setPosition( mFolderIcon.mInfo.x - buttonOK.width / 2 , mFolderIcon.mInfo.y - buttonOK.height / 2 );
			}
			else
			{
				buttonOK.setPosition( mFolderIcon.mInfo.x - buttonOK.width / 2 , mFolderIcon.mInfo.y );
			}
		}
		else if( mFolderIcon.mInfo.y < buttonOK.height / 2 )
		{
			float pos_x = mFolderIcon.mInfo.x - buttonOK.width / 2;
			if( mFolderIcon.mInfo.x < buttonOK.height / 2 )
			{
				pos_x = mFolderIcon.mInfo.x + mFolderIcon.folder_front.width - buttonOK.width / 2;
			}
			if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
			{
				buttonOK.setPosition( pos_x , mFolderIcon.mInfo.y + ( mFolderIcon.folder_front.height - buttonOK.height ) / 2 );
			}
			else
			{
				buttonOK.setPosition( pos_x , mFolderIcon.mInfo.y );
			}
		}
		else
		{
			if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
			{
				buttonOK.setPosition( mFolderIcon.mInfo.x + mFolderIcon.folder_front.width - buttonOK.width / 2 , mFolderIcon.mInfo.y - buttonOK.height / 2 );
			}
			else
			{
				buttonOK.setPosition( mFolderIcon.mInfo.x + mFolderIcon.folder_front.width - buttonOK.width / 2 , mFolderIcon.mInfo.y );
			}
		}
		addView( buttonOK );
		buttonOK.hide();
		SplinePoint temp = new SplinePoint();
		temp.x = downPath.x = mFolderIcon.mInfo.x + R3D.folder_front_width / 2;
		temp.y = downPath.y = mFolderIcon.mInfo.y + mFolderIcon.folder_front.height / 2;
		TotalPath.add( temp );
		int Count = mFolderIcon.getChildCount();
		controlIcon.clear();
		for( int i = 0 ; i < Count ; i++ )
		{
			view = mFolderIcon.getChildAt( i );
			if( view instanceof Icon3D )
			{
				view.show();
				view.setScale( 1f , 1f );
				view.setRotation( 0 );
				mFolderIcon.changeTextureRegion( view , R3D.workspace_cell_height );
				controlIcon.add( view );
			}
		}
	}
	
	/*End of class*/
	public void changeIcon(
			View3D oldIcon ,
			View3D newIcon )
	{
		for( int i = 0 ; i < controlIcon.size() ; i++ )
		{
			View3D view = controlIcon.get( i );
			if( view == oldIcon )
			{
				controlIcon.add( i , newIcon );
				controlIcon.remove( oldIcon );
				return;
			}
		}
	}
}

class SplinePoint
{
	
	public float x;
	public float y;
	
	/** Constructs a new vector at (0,0) */
	public SplinePoint()
	{
	}
}
