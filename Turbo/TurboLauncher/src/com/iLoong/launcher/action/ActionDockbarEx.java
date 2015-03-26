package com.iLoong.launcher.action;


import android.graphics.Bitmap;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.coco.theme.themebox.util.Tools;
import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ActionDockbarEx extends ActionView
{
	
	ImageView3D mHandView;
	ImageView3D mTitleView;
	float clockWidth = 455;
	float handX = 0;
	float handY = 0;
	float targetX = 0;
	
	public ActionDockbarEx(
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
		View3D focus = ActionData.getInstance().getFocusView();
		Bitmap hand = ThemeManager.getInstance().getBitmap( "theme/desktopAction/hand.png" );
		hand = Tools.resizeBitmap( hand , mScale );
		hand = ImageUtils.getHorizontalImage( hand , true );
		mHandView = new ImageView3D( "mHandView" , hand );
		handX = Utils3D.getScreenWidth() - hand.getWidth();
		handY = handY;//(focus.height-mHandView.height)/2;
		targetX = handX - 300;
		mHandView.setPosition( handX , handY );
		attachView( mHandView );
		String title = getString( R.string.action_slide_left_dockbar );
		actionUtil.setFontSize( R3D.icon_title_font + 10 * mScale );
		actionUtil.setTitle( title );
		mTitleView = new ImageView3D( "mTitleView" , actionUtil.getTextureRegion() );
		mTitleView.setPosition( 10 * mScale , iLoongLauncher.getInstance().d3dListener.root.hotseatBar.height + 10 * mScale );
		attachView( mTitleView );
		onAnimStarted();
		hand.recycle();
		this.color.a = 0;
	}
	
	@Override
	public void initEvent()
	{
		Root3D root =iLoongLauncher.getInstance().d3dListener.getRoot();
		if(root!=null&&root.hotseatBar.getType()!=HotSeat3D.TYPE_WIDGET)
			root.hotseatBar.startMyTween();
	}
	
	@Override
	public void ondetach()
	{
		mHandView.remove();
		mHandView.region.getTexture().dispose();
		mTitleView.region.getTexture().dispose();
		mTitleView.remove();
	}
	
	public void hide(){
		HotSeat3D view=iLoongLauncher.getInstance().d3dListener.root.hotseatBar;
		if(iLoongLauncher.getInstance().d3dListener.root!=null){
			if(view!=null&&iLoongLauncher.getInstance().d3dListener.root.findView( view.name )!=null){
				iLoongLauncher.getInstance().d3dListener.root.removeView(view);
				iLoongLauncher.getInstance().d3dListener.root.addViewAfter( iLoongLauncher.getInstance().d3dListener.root.getWorkspace() , view );
			}
		}
	}
	@Override
	public void onAnimStarted()
	{
		float duration = 1.2f;
		Timeline tl = getParTimeline();
		tl.push( Tween.to( mHandView , View3DTweenAccessor.POS_XY , duration ).ease( Linear.INOUT ).repeat( 10000 , 0 ).target( targetX , handY ) );
		tl.push( Tween.to( mHandView , View3DTweenAccessor.OPACITY , duration ).target( 0 ).repeat( 10000 , 0 ) );
		tl.push( Tween.to( this , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
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
	
	public void setData()
	{
		super.setData();
		mActionData.isActionShow = true;
		//mActionData.setValidity( ActionData.VALIDITY_LOCK_DOCKBAR );
		mActionData.setValidity( ActionData.VALIDITY_LOCK_HOTMAINGROUP);
		mActionData.setShowHotFront( true );
		mActionData.setShowWorkspaceFront( true );
		iLoongLauncher.getInstance().d3dListener.root.hotseatBar.bringToFront();
		float shadowWidth= iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height ;
		mActionData.setFrontPosition( shadowWidth ,shadowWidth);
		
		//mActionData.setFrontPosition( 0 , iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height );
		mActionData.setFocusView( iLoongLauncher.getInstance().d3dListener.root.hotseatBar );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
	}
}
