package com.iLoong.launcher.action;

import android.graphics.Bitmap;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.coco.theme.themebox.util.Tools;
import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ActionDockbar extends ActionView
{
	ImageView3D mHandView;
	ImageView3D mTitleView;
	float clockWidth=455;
	float handX=0;
	float handY=0;
	float targetY=0;
	public ActionDockbar(
			String name )
	{
		super( name );
		
	}

	
	
	@Override
	public boolean isPointIn(
			float x ,
			float y )
	{
		return super.isPointIn( x , y );
	}
	
	@Override
	public void initView()
	{
	
		View3D focus =ActionData.getInstance().getFocusView();
		Bitmap hand =ThemeManager.getInstance().getBitmap( "theme/desktopAction/hand.png" );
		hand=Tools.resizeBitmap( hand , mScale);
		
		hand=ImageUtils.getHorizontalImage( hand , true );
		mHandView = new ImageView3D( "mHandView" , hand );
		handX=Utils3D.getScreenWidth()-hand.getWidth();
		handY=(focus.height-mHandView.height)/2;
		targetY=focus.y+focus.height;
		mHandView.setPosition(  handX, handY);
		attachView( mHandView );
		
		
		String title=getString( R.string.action_slide_up_dockbar);
		actionUtil.setFontSize( R3D.icon_title_font+10*mScale );
		actionUtil.setTitle( title );
		mTitleView =new ImageView3D("mTitleView",actionUtil.getTextureRegion(  ));
		mTitleView.setPosition( 10*mScale ,iLoongLauncher.getInstance().d3dListener.root.hotseatBar.height+10*mScale);
		attachView( mTitleView );
		
		onAnimStarted();
		
		hand.recycle();
		
	}
	
	@Override
	public void initEvent()
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void ondetach()
	{
		mHandView.remove();
		mHandView.region.getTexture().dispose();
		mTitleView.region.getTexture().dispose();
		mTitleView.remove();
	}
	
	@Override
	public void onAnimStarted()
	{
		float duration=1.2f;
		Timeline tl=getParTimeline();
	
		tl.push( Tween.to( mHandView , View3DTweenAccessor.POS_XY , duration ).ease( Linear.INOUT ).repeat( 10000 , 0 ) .target( handX ,targetY));
		tl.push( Tween.to( mHandView , View3DTweenAccessor.OPACITY , duration ).target( 0).repeat( 10000, 0 ));
	
		startTween();
	}
	
	
	
	@Override
	public Object next()
	{
		stopTween();
		ondetach();
		viewParent.onCtrlEvent( this , EVENT_DO_NEXT );
		return null;
	}
	public void setData(){
		super.setData();
		mActionData.setValidity(ActionData. VALIDITY_LOCK_DOCKBAR);
		mActionData.setShowHotFront( true );
		iLoongLauncher.getInstance().d3dListener.root.hotseatBar.bringToFront();
		float shadowWidth= iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height ;
		mActionData.setFrontPosition( shadowWidth ,shadowWidth);
		//mActionData.setFrontPosition(0,  iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height );
		mActionData.setFocusView(iLoongLauncher.getInstance().d3dListener.root.hotseatBar );
	}
	
}
