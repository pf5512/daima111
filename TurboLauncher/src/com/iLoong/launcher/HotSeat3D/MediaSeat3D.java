package com.iLoong.launcher.HotSeat3D;


import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.camera.CameraManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class MediaSeat3D extends ViewGroup3D
{
	
	private Tween myTween;
	private static final int hide_dock = 0;
	private static final int show_dock = 1;
	
	public MediaSeat3D(
			String name ,
			String iconPath )
	{
		super( name );
		width = Utils3D.getScreenWidth();
		height = R3D.hot_obj_height;
		x = 0;
		y = -R3D.seatbar_hide_height;
		setOrigin( width / 2 , height / 2 );
		int size = DefaultLayout.musicSeatIcon.size();
		int iconCount = 4;
		if( size > 0 )
		{
			int i = 0;
			for( int j = 0 ; j < size ; j++ )
			{
				String path = "theme/mediaview/" + iconPath + "/" + DefaultLayout.getMediaSeatIconImg( DefaultLayout.musicSeatIcon , j );
				Bitmap bmp = ThemeManager.getInstance().getBitmap( path );
				//xujin  
				if( bmp == null )
				{
					continue;
				}
				int dstWidth = Tools.dip2px( iLoongLauncher.getInstance() , 64 );
				int dstHeight = dstWidth;
				if( bmp.getWidth() != dstWidth || bmp.getHeight() != dstHeight )
				{
					bmp = Bitmap.createScaledBitmap( bmp , dstWidth , dstHeight , true );
				}
				Texture t = new BitmapTexture( bmp );
				TextureRegion tr = new TextureRegion( t );
				if( !iLoongLauncher.releaseTexture )
					bmp.recycle();
				Intent intent = new Intent( Intent.ACTION_MAIN );
				intent.setComponent( new ComponentName( DefaultLayout.getMediaSeatIconPkg( DefaultLayout.musicSeatIcon , j ) , DefaultLayout.getMediaSeatIconCls( DefaultLayout.musicSeatIcon , j ) ) );
				Log.i( "jinxu" , "" + intent.getComponent().getPackageName() + " " + intent.getComponent().getClassName() );
				intent.addCategory( Intent.CATEGORY_LAUNCHER );
				intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
				MediaSeatIcon3D mediaSeatIcon = new MediaSeatIcon3D( DefaultLayout.getMediaSeatIconTitle( DefaultLayout.musicSeatIcon , j ) , tr , intent );
				mediaSeatIcon.setPosition(
						( Utils3D.getScreenWidth() / iconCount ) * i + ( Utils3D.getScreenWidth() / iconCount - mediaSeatIcon.getWidth() ) / 2 ,
						( this.getHeight() - mediaSeatIcon.getHeight() ) / 2 );
				addView( (View3D)mediaSeatIcon );
				i++;
			}
		}
	}
	
	public void hide()
	{
		//		if (this.touchable == false) {
		//			return;
		//		}
		//		this.touchable = false;
		stopTween();
		this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 0 , 0 , 0 );
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , 0 , -R3D.seatbar_hide_height , 0 );
	}
	
	public void hideNoAnim()
	{
		//		if (this.touchable == false) {
		//			return;
		//		}
		//		this.touchable = false;
		stopTween();
		setPosition( 0 , -R3D.seatbar_hide_height );
		setUser( 0 );
	}
	
	//xujin
	public void hideNoAnim(
			int alpha )
	{
		stopTween();
		setPosition( 0 , -R3D.seatbar_hide_height );
		setUser( alpha );
	}
	
	public void show()
	{
		//		if (this.touchable == true) {
		//			return;
		//		}
		super.show();
		stopTween();
		this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 1 , 0 , 0 );
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 );
	}
	
	public void showNoAnim()
	{
		//		if (this.touchable == true) {
		//			return;
		//		}		
		super.show();
		stopTween();
		setPosition( 0 , 0 );
		setUser( 1 );
	}
	
	public void showDelay(
			float delay )
	{
		//		if (this.touchable == true) {
		//			return;
		//		}
		super.show();
		stopTween();
		this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.3f , 1 , 0 , 0 ).delay( delay );
		myTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).delay( delay );
	}
	
	@Override
	public void setUser(
			float value )
	{
		// TODO Auto-generated method stub
		Workspace3D.mediaBgAlpha = value;
	}
	
	@Override
	public float getUser()
	{
		// TODO Auto-generated method stub
		return Workspace3D.mediaBgAlpha;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		requestFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		boolean rlt = super.onTouchDragged( x , y , pointer );
		if( !rlt )
		{
			int len = children.size() - 1;
			for( int i = len ; i >= 0 ; i-- )
			{
				( (MediaSeatIcon3D)children.get( i ) ).releaseHighlight();
			}
		}
		return rlt;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		releaseFocus();
		boolean rlt = super.onTouchUp( x , y , pointer );
		int len = children.size() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			( (MediaSeatIcon3D)children.get( i ) ).releaseHighlight();
		}
		return rlt;
	}
	
	//	@Override
	//	public void onEvent(int type, BaseTween source) {
	//		// TODO Auto-generated method stub
	//		if (type == TweenCallback.COMPLETE && source == myTween) {
	//			int animKind = (Integer) (source.getUserData());
	//			if (animKind == hide_dock) {
	//				super.hide();
	//			} else if (animKind == show_dock) {
	//			}
	//			return;
	//		}
	//	}
	class MediaSeatIcon3D extends View3D
	{
		
		Intent intent;
		private boolean isHightlight = false;//是否被按下
		
		public MediaSeatIcon3D(
				String name ,
				TextureRegion region ,
				Intent intent )
		{
			super( name , region );
			this.intent = intent;
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			requestHighlight();
			return true;
		}
		
		@Override
		public boolean onTouchDragged(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub			
			if( isHightlight )
			{
				if( intent.getComponent().toString().contains( "com.cooee.launcher" ) && intent.getComponent().toString().contains( "com.cooee.launcher.ShowApp" ) )
				{
					//xujin 
					if( DefaultLayout.enable_camera )
					{
						CameraManager.instance().hidePreview();
					}
					//如果相机开启，就等待关闭
					//等待camera释放
					if( CameraManager.instance().cameraIsOpened() )
					{
						synchronized( CameraManager.instance() )
						{
							try
							{
								CameraManager.instance().wait();
							}
							catch( InterruptedException e )
							{
								e.printStackTrace();
							}
						}
					}
					iLoongLauncher.getInstance().getD3dListener().getRoot().showAllAppFromWorkspace();
				}
				else
				{
					intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					String toast = iLoongLauncher.getInstance().getResources().getString( RR.string.activity_not_found );
					try
					{
						iLoongLauncher.getInstance().startActivity( intent );
					}
					catch( ActivityNotFoundException e )
					{
						SendMsgToAndroid.sendOurToastMsg( toast );
					}
					catch( SecurityException e )
					{
						SendMsgToAndroid.sendOurToastMsg( toast );
					}
				}
			}
			releaseHighlight();
			return true;
		}
		
		public void requestHighlight()
		{
			if( !isHightlight )
			{
				this.color.r /= 2;
				this.color.g /= 2;
				this.color.b /= 2;
				isHightlight = true;
			}
		}
		
		public void releaseHighlight()
		{
			if( isHightlight )
			{
				this.color.r *= 2;
				if( this.color.r > 1f )
					this.color.r = 1f;
				this.color.g *= 2;
				if( this.color.g > 1f )
					this.color.g = 1f;
				this.color.b *= 2;
				if( this.color.b > 1f )
					this.color.b = 1f;
				isHightlight = false;
			}
		}
	}
}
