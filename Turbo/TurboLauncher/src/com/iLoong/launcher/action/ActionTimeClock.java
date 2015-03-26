package com.iLoong.launcher.action;


import android.graphics.Bitmap;
import android.util.Log;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ActionTimeClock extends ActionView
{

	String TAG="TimeClockAction";
	ImageView3D mTitleView;
	
	ImageView3D mHandView;
	public static float clockWidth=455;
	static float handX=0;
	static float handY=0;
	static float targetY=0;
	public ActionTimeClock(
			String name )
	{
		super( name );
		
	}

	
	
	
	@Override
	public void initView()
	{
		clockWidth=455;
		
		View3D focus=mActionData.getFocusView();
		super.setFocusView( focus );
		String title=getString( R.string.action_slide_down);
		actionUtil.setFontSize( R3D.icon_title_font );
		actionUtil.setTitle( title );
	
		
		mTitleView =new ImageView3D("mTitleView",actionUtil.getTextureRegion(  ));
		//mTitleView.setPosition( this.width-mTitleView.width-10*mScale ,fixPosY( Utils3D.getStatusBarHeight()/2+mTitleView.height) );
		mTitleView.setPosition( this.width-mTitleView.width-10*mScale ,fixPosY( 120,mTitleView) );
		
		attachView( mTitleView );
		
		attachView( focus );
		focus.bringToFront();
		float scale=Math.min( Utils3D.getScreenWidth() / 720f , R3D.Workspace_cell_each_height / 240f );
		clockWidth =clockWidth*scale;
		Bitmap hand =ThemeManager.getInstance().getBitmap( "theme/desktopAction/hand.png" );
		hand=Tools.resizeBitmap( hand , mScale );
		hand=ImageUtils.getHorizontalImage( hand , true );
		mHandView = new ImageView3D( "mHandView" , hand );
		
		
		handX=Utils3D.getScreenWidth()/2+clockWidth/2-hand.getWidth()/3;
		handY=focus.y+ focus.height/3;
		targetY=handY-mHandView.height;
		mHandView.setPosition(  handX, handY);
		attachView( mHandView );
		mHandView.bringToFront();
		hand.recycle();
		onAnimStarted();
		
	}
	
	@Override
	public void initEvent()
	{
	
	}
	

	
	@Override
	public void ondetach()
	{
		mTitleView.remove();
		mHandView.remove();
		
		mHandView.region.getTexture().dispose();
		mTitleView.region.getTexture().dispose();
	}
	public void setFocusView(
			View3D focus ){
		
		
	
	}
	
	
	
	@Override
	public void onAnimStarted()
	{
		float duration=1.2f;
		Timeline tl=getParTimeline();
		float startY=mHandView.y;
		float endY=mHandView.y-mHandView.height;
		targetY=handY-mHandView.height/2;
		mHandView.setPosition( handX , handY);
		mHandView.color.a=1.0f;
		tl.push( Tween.to( mHandView , View3DTweenAccessor.POS_XY , duration ).ease( Linear.INOUT ).repeat( 10000 , 0 ) .target( handX ,targetY));
		tl.push( Tween.to( mHandView , View3DTweenAccessor.OPACITY , duration ).target( 0).repeat( 10000, 0 ));
		
	
		startTween();
	}

	

	

	@Override
	public boolean isPointIn(float x,float y)
	{
		
		if((x>getFocusView().x&&x<(getFocusView().x+getFocusView().width))
				&&(y>getFocusView().y&&y<(getFocusView().y+getFocusView().height)))
		{
			return true;
		}
		else 
			return false;
		
	}
	public void setData(){
		super.setData();
		mActionData.setValidity( ActionData.VALIDITY_LOCK_TIMECLOCK);
		mActionData.setFocusView(DefaultLayout.mClockInstance );
	}
	public void hide(){
		super.hide();
		mHandView.hide();
		mTitleView.hide();
		mActionData.getFocusView().hide();
		
	}
	public void show(){
		super.show();
		
		mTitleView.show();
		mActionData.getFocusView().show();
		mHandView.show();
		mActionData.getFocusView().bringToFront();
		mTitleView.bringToFront();
		attachView(mHandView);
		//mHandView.bringToFront();
		//onAnimStarted();
	}
	@Override
	public Object next()
	{
		
		ondetach();
	
		viewParent.onCtrlEvent( this , EVENT_DO_NEXT );
		
		return null;
	}


	

}
