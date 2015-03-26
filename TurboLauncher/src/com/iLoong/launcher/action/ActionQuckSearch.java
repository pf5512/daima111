package com.iLoong.launcher.action;


import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.action.ActionNextButton.IOnBtnClickEvent;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class ActionQuckSearch extends ActionView
{
	
	ActionNextButton nextButton;
	
	public ActionQuckSearch(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Object next()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void initView()
	{
		this.color.a=0;
		init();
		IOnBtnClickEvent OnBtnClickEventImpl = new IOnBtnClickEvent() {
			
			@Override
			public Object onBtnClick()
			{
				ondetach();
				Desktop3DListener.root.startQsearchFadeoutAmin();
				//Desktop3DListener.root.qSearchGroup.startQuickSearchAnimQuit( false );
				viewParent.onCtrlEvent( ActionQuckSearch.this , EVENT_DO_NEXT );
				return true;
			}
		};
		String btnTitle = getString( R.string.action_start );
		nextButton = new ActionNextButton( btnTitle );
		nextButton.setTitle( btnTitle , R3D.icon_title_font );
		nextButton.setIOnBtnClickEvent( OnBtnClickEventImpl );
		nextButton.setPosition( fixPosX( 480 ) , fixPosY( 872 ) );
		attachView( nextButton );
		
		onAnimStarted();
	}
	
	@Override
	public void initEvent()
	{
		// TODO Auto-generated method stub
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
		return false;
	}
	
	@Override
	public void ondetach()
	{
		nextButton.remove();
		nextButton.region.getTexture().dispose();
	}
	
	@Override
	public void onAnimStarted()
	{
		float duration=1.5f;
		Timeline tl=getParTimeline();
	
		tl.push( Tween.to( this , View3DTweenAccessor.OPACITY , duration ).target( 1));
	
		startTween();
	}
	
	private void init()
	{
		mActionData = ActionData.getInstance();
		mActionData.setShowHotFront( false );
		mActionData.setShowWorkspaceFront( false );
		mActionData.setFrontPosition( iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height , iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height );
	}
	
}
