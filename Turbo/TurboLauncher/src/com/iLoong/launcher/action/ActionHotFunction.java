package com.iLoong.launcher.action;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.NetworkInfo.DetailedState;
import android.util.Log;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.FirstActivity;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import  com.iLoong.launcher.action.ActionNextButton.IOnBtnClickEvent;
import com.iLoong.launcher.search.QSearchGroup;
import com.iLoong.launcher.setting.FakeLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class ActionHotFunction extends ActionView
{

	ImageView3D arrowLeft;
	ImageView3D arrowRight;
	ImageView3D tipView;
	ActionNextButton mButton;
	ActionNextButton nextButton;
	float basePosY=0;
	boolean isClickEvent=true;
	public ActionHotFunction(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object next()
	{
		if(isClickEvent){
			isClickEvent=false;
			viewParent.onCtrlEvent( this , EVENT_DO_NEXT );
		}
		
		return true;
	}

	@Override
	public void initView()
	{
		
	
		ActionData.getInstance().setFocusView(iLoongLauncher.getInstance().d3dListener.root.hotseatBar );
		
		basePosY =ActionData.getInstance().getFocusView().height;
		Bitmap arrow =ThemeManager.getInstance().getBitmap( "theme/desktopAction/harrow.png" );
		arrow=Utils3D.resizeBmp( arrow , mScale*arrow.getWidth(),mScale*arrow.getHeight() );
		arrowRight=new ImageView3D("arrowRight",arrow);
		
		Bitmap arrowRb=ImageUtils.getHorizontalImage( arrow , false );
		arrowLeft=new ImageView3D("arrowLeft",arrowRb);
		arrowLeft.setPosition( fixPosX( 21 ) , basePosY);
		arrowRight.setPosition(this.width-fixPosX( 21 )-arrowRight.width  , basePosY);
		attachView( arrowLeft);
		attachView( arrowRight);
		arrow.recycle();
		arrowRb.recycle();
		
		
		String title=getString( R.string.action_slide_around);
		actionUtil.setFontSize( R3D.icon_title_font+10*mScale );
		actionUtil.setTitle( title );
		tipView =new ImageView3D("tipView",actionUtil.getTextureRegion(  ));
		tipView.setPosition(( this.width-tipView.width)/2.0F , basePosY );
		attachView( tipView );
		
		IOnBtnClickEvent OnBtnClickEventImpl=new IOnBtnClickEvent() {
			
			@Override
			public Object onBtnClick()
			{
				
				ondetach();
				mActionData.setValidity( ActionData.VALIDITY_LOCK_NORMAL);
				//Workspace3D.getInstance().requestFocus();
				root.hotseatBar.startMyTween();
				DefaultLayout. mClockInstance.getWidgetPluginView3D(). changeWidgetState( 0 );
				viewParent.onCtrlEvent( ActionHotFunction.this , EVENT_DO_HIDE );
				QSearchGroup.canShow=true;
				ActionData.isActionShow=false;
				
				HotSeat3D view=iLoongLauncher.getInstance().d3dListener.root.hotseatBar;
				if(iLoongLauncher.getInstance().d3dListener.root!=null){
					if(view!=null&&iLoongLauncher.getInstance().d3dListener.root.findView( view.name )!=null){
						iLoongLauncher.getInstance().d3dListener.root.removeView(view);
						iLoongLauncher.getInstance().d3dListener.root.addViewAfter( iLoongLauncher.getInstance().d3dListener.root.getWorkspace() , view );
					}
				}
				return true;
				
			}
		};
		String btnTitle=getString( R.string.action_start);
		nextButton= new ActionNextButton( btnTitle );
		nextButton.setTitle( btnTitle ,R3D.icon_title_font );
		nextButton.setIOnBtnClickEvent(OnBtnClickEventImpl);
		
		
		nextButton.setPosition(  fixPosX( 480 ) , fixPosY( 872 ) );
		attachView( nextButton );
		
		
		
	}

	@Override
	public void initEvent()
	{
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void ondetach()
	{
		nextButton.remove();
		//mButton.remove();
		arrowRight.remove();
		arrowLeft.remove();
		tipView.remove();
		nextButton.region.getTexture();
		//mButton.region.getTexture().dispose();
		arrowRight.region.getTexture().dispose();
		arrowLeft.region.getTexture().dispose();
		tipView.region.getTexture().dispose();
		
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
			return super.isPointIn( x , y );
	}

	@Override
	public void onAnimStarted()
	{
		// TODO Auto-generated method stub
		
	}


	public void setData(){
		super.setData();
	
		mActionData.setValidity( ActionData.VALIDITY_LOCK_HOTMAINGROUP);
	}
	
	
}
