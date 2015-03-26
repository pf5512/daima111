package com.iLoong.launcher.action;


import android.graphics.Bitmap;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Color;
import com.coco.theme.themebox.util.Tools;
import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.action.ActionNextButton.IOnBtnClickEvent;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ActionWeatherClock extends ActionView
{
//	ImageView3D mHandView;
	ImageView3D mHandView1;
	ActionNextButton mButton;
	ImageView3D mTitleView;
//	ImageView3D mTitleView1;
	ActionNextButton nextButton;
	boolean isClickEvent=false;
	public ActionWeatherClock(
			String name )
	{
		super( name );
		
	}

	@Override
	public boolean isPointIn(
			float x ,
			float y )
	{
		if((x>nextButton.x&&x<(nextButton.x+nextButton.width))
				&&(y>nextButton.y&&y<(nextButton.y+nextButton.height)))
		{
			nextButton.requestFocus();
			return true;
		}
		else 
			return false;
	}
	
	@Override
	public void initView()
	{
		this.requestFocus();
		
		String title=getString( R.string.action_locate_city);
		actionUtil.setFontSize( R3D.icon_title_font );
		actionUtil.setTitle( title );
	
		mTitleView =new ImageView3D("mTitleView",actionUtil.getTextureRegion(  ));
		attachView( mTitleView );
		mTitleView.setPosition( this.width-mTitleView.width-70*mScale ,  fixPosY( 159 ));
		//mTitleView.setPosition( fixPosX( 464 ) , fixPosY( 129 )  );
		
//		String title1=getString( R.string.action_see_weather);
//		actionUtil.setFontSize( R3D.icon_title_font );
//		actionUtil.setTitle( title1 );
//		mTitleView1 =new ImageView3D("mTitleView1",actionUtil.getTextureRegion(  ));
//		mTitleView1.setPosition( this.width-mTitleView1.width-10*mScale ,fixPosY(597)  );
//		attachView( mTitleView1 );
	
	
		
		
		String gotit=getString( R.string.action_next);
		nextButton=new ActionNextButton( gotit );
		nextButton.setTitle(gotit , R3D.icon_title_font  );
		nextButton.setPosition( fixPosX(464) ,fixPosY(628)  );
		attachView( nextButton );
		
		
		
//		View3D focus =ActionData.getInstance().getFocusView();
//		Bitmap lhand =ThemeManager.getInstance().getBitmap( "theme/desktopAction/hand.png" );
//		lhand=Tools.resizeBitmap( lhand , mScale );
		//lhand=ImageUtils.getHorizontalImage( lhand , true );
		//fixPosY( 469,mHandView )
		// fixPosX( 69 )
//		mHandView = new ImageView3D( "mHandView" , lhand );
//		mHandView.setPosition(this.width/2-mHandView.width ,mActionData.getFocusView().y-mHandView.height+
//				ActionTimeClock.clockWidth*0.05f);
//	
//		mHandView.color.a=0.0f;
//		attachView( mHandView );
//		lhand.recycle();
	
		Bitmap rhand =ThemeManager.getInstance().getBitmap( "theme/desktopAction/hand.png" );
		rhand=Tools.resizeBitmap( rhand , mScale );
		rhand=ImageUtils.getHorizontalImage( rhand , true );
		mHandView1 = new ImageView3D( "mHandView1" , rhand	 );
		mHandView1.setPosition(  this.width/2+fixPosX( 80 ), fixPosY( 250,mHandView1 ));
		
		//mHandView1.setPosition(  fixPosX( 450 ), fixPosY( 131,mHandView1 ));
		mHandView1.color.a=0.0f;
		attachView( mHandView1 );
		rhand.recycle();
		
		onAnimStarted();
	}
	
	
	@Override
	public void initEvent()
	{
		IOnBtnClickEvent OnBtnClickEventImpl=new IOnBtnClickEvent() {
			
			@Override
			public Object onBtnClick()
			{
				isClickEvent =true;
				return  next();
			}
		};
		nextButton.setIOnBtnClickEvent( OnBtnClickEventImpl );
	}
	
	@Override
	public void ondetach()
	{
//		mHandView.region.getTexture().dispose();
//		mHandView.remove();
		
		mHandView1.region.getTexture().dispose();
		mHandView1.remove();
		
		mTitleView.remove();
		mTitleView.region.getTexture().dispose();
//		mTitleView1.remove();
//		mTitleView1.region.getTexture().dispose();
		nextButton.remove();
		nextButton.region.getTexture().dispose();
		
		ActionData.getInstance().getFocusView().remove();
		Widget3DInfo info=ActionData.getInstance().getClockInfo();
		iLoongLauncher.getInstance().d3dListener.workspace.addInScreen(ActionData.getInstance().getFocusView() ,info .screen , info.x , info.y , false );

	}
	
	@Override
	public void onAnimStarted()
	{
		float duration=3f;
		Timeline tl=getParTimeline();
	

	
//		tl.push( Tween.to( mHandView , View3DTweenAccessor.OPACITY , duration ).target( 1));
		tl.push( Tween.to( mHandView1 , View3DTweenAccessor.OPACITY , duration ).target( 1));
		
	
		startTween();
	}
	

	@Override
	public Object next()
	{
		Log.v( TAG ,"curr aCtion: "+this.getClass().toString());
		if(isClickEvent){
			isClickEvent=false;
			ondetach();
			viewParent.onCtrlEvent( this , EVENT_DO_NEXT );
		}
		
		return true;
	}
	public void setData(){
		super.setData();
	
		mActionData.setValidity( ActionData.VALIDITY_LOCK_WEATHERCLOCK);
	}
	
	
	
}
