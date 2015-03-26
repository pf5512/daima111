package com.iLoong.launcher.action;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.action.ActionNextButton.IOnBtnClickEvent;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.recent.RecentAppHolder;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ActionRecent extends ActionView
{
	
	ImageView3D mHandView;
	ImageView3D arrowUp;
	ActionNextButton nextButton;
	ImageView3D mTitleView;
	float basePosY;
	
	public ActionRecent(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Object next()
	{
		ondetach();
		if( viewParent != null )
		{
			viewParent.onCtrlEvent( this , EVENT_DO_NEXT );
		}
		return true;
	}
	
	public void setData()
	{
		super.setData();
		mActionData.isActionShow = true;
		mActionData.setShowHotFront( true );
		mActionData.setFrontPosition( RecentAppHolder.mStartPosY , RecentAppHolder.mStageHeight + RecentAppHolder.mStartPosY );
		//		mActionData.setFrontPosition(RecentAppHolder.mStartPosY- iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height,
		//										RecentAppHolder.mStageHeight+RecentAppHolder.mStartPosY);
	}
	
	@Override
	public void initView()
	{
		Bitmap arrow = ThemeManager.getInstance().getBitmap( "theme/desktopAction/varrow.png" );
		arrow = Utils3D.resizeBmp( arrow , mScale * arrow.getWidth() , mScale * arrow.getHeight() );
		arrowUp = new ImageView3D( "arrowRight" , arrow );
		arrowUp.setPosition( ( this.width - arrowUp.width ) / 2 , RecentAppHolder.mStartPosY + RecentAppHolder.mStageHeight / 2 + 10 * mScale );
		attachView( arrowUp );
		arrow.recycle();
		Bitmap hand = ThemeManager.getInstance().getBitmap( "theme/desktopAction/hand.png" );
		hand = Utils3D.resizeBmp( hand , mScale * hand.getWidth() , mScale * hand.getHeight() );
		hand = ImageUtils.getHorizontalImage( hand , true );
		mHandView = new ImageView3D( "mHandView" , hand );
		mHandView.setPosition( this.width / 2 + arrowUp.width / 2 , RecentAppHolder.mStartPosY + ( RecentAppHolder.mStageHeight - mHandView.height ) / 2.0f );
		mHandView.color.a = 0;
		attachView( mHandView );
		IOnBtnClickEvent OnBtnClickEventImpl = new IOnBtnClickEvent() {
			
			@Override
			public Object onBtnClick()
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				pref.edit().putBoolean( "first_back_from_app" , true ).commit();
				return next();
			}
		};
		String gotit = getString( R.string.action_gotit );
		nextButton = new ActionNextButton( gotit );
		nextButton.setTitle( gotit , R3D.icon_title_font );
		nextButton.setIOnBtnClickEvent( OnBtnClickEventImpl );
		// ActionNextButton.getInstance( gotit , R3D.icon_title_font,OnBtnClickEventImpl );
		nextButton.setPosition( fixPosX( 480 ) , RecentAppHolder.mStartPosY - 10 * mScale - nextButton.height );
		attachView( nextButton );
		//	nextButton.requestFocus();
		String title = getString( R.string.action_slide_up );
		actionUtil.setFontSize( R3D.icon_title_font + 10 * mScale );
		actionUtil.setTitle( title );
		mTitleView = new ImageView3D( "mTitleView" , actionUtil.getTextureRegion() );
		mTitleView.setPosition( ( this.width - mTitleView.width ) / 2.0f , RecentAppHolder.mStartPosY + 10 * mScale );
		attachView( mTitleView );
		onAnimStarted();
	}
	
	@Override
	public void initEvent()
	{
	}
	
	@Override
	public void ondetach()
	{
		nextButton.releaseFocus();
		mHandView.remove();
		mHandView.region.getTexture().dispose();
		arrowUp.remove();
		arrowUp.region.getTexture().dispose();
		nextButton.remove();
		nextButton.region.getTexture().dispose();
		mTitleView.remove();
		mTitleView.region.getTexture().dispose();
	}
	
	@Override
	public void onAnimStarted()
	{
		float duration = 1.2f;
		Timeline tl = getParTimeline();
		tl.push( Tween.to( mHandView , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		startTween();
	}
	
	@Override
	public boolean isShowFront()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isPointIn(
			float x ,
			float y )
	{
		if( ( x > nextButton.x && x < ( nextButton.x + nextButton.width ) ) && ( y > nextButton.y && y < ( nextButton.y + nextButton.height ) ) )
		{
			nextButton.requestFocus();
			return true;
		}
		else
			return false;
	}
}
