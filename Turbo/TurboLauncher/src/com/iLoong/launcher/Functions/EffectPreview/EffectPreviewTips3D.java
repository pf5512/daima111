// xiatian add whole file //EffectPreview
package com.iLoong.launcher.Functions.EffectPreview;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class EffectPreviewTips3D extends View3D
{
	
	private static TextureRegion bgTextureRegion = null;
	private TextureRegion titleTextureRegion;
	private TextureRegion backButtonTextureRegion;
	private TextureRegion backButtonFocusTextureRegion;
	private EffectPreview3D mWorkspaceEffectPreview;
	private EffectPreview3D mApplistEffectPreview;
	private static final String EFFECT_BOX_MAIN_ACTIVITY = "com.coco.theme.themebox.MainActivity";
	private static final String EFFECT_BOX_TYPE = "type";
	private HotSeat3D mHotSeat3D;
	private AppHost3D mAppHost3D;
	private static boolean isButtonFocus = false;
	private Workspace3D mWorkspace3D;
	private Timeline workspaceAndAppTween;
	
	public EffectPreviewTips3D(
			String name )
	{
		super( name );
		x = 0;
		y = Utils3D.getScreenHeight() - R3D.appbar_height;
		width = Utils3D.getScreenWidth();
		height = R3D.appbar_height;
		setOrigin( width / 2 , height / 2 );
		bgTextureRegion = R3D.findRegion( R3D.mEffectPreviewBgRegionName );
		if( titleTextureRegion == null )
		{
			String tips = iLoongLauncher.getInstance().getResources().getString( RR.string.EffectPreviewTips );//"请滑动屏幕预览特效动画"
			titleTextureRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( tips , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );
		}
		backButtonTextureRegion = R3D.findRegion( R3D.mEffectPreviewButtonRegionName );
		backButtonFocusTextureRegion = R3D.findRegion( R3D.mEffectPreviewButtonFocusRegionName );
	}
	
	@Override
	public void show()
	{
		//		super.show();
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		int w = bgTextureRegion.getRegionWidth();
		for( int i = 0 ; i < width / w + 1 ; i++ )
		{
			batch.draw( bgTextureRegion , i * w , y , w , height );
		}
		batch.draw( titleTextureRegion , ( width - titleTextureRegion.getRegionWidth() ) / 2 , y + ( height - titleTextureRegion.getRegionHeight() ) / 2 );
		batch.draw( isButtonFocus ? backButtonFocusTextureRegion : backButtonTextureRegion , x , y , width / 6 , height );
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		isButtonFocus = false;
		if( isPointInButtonRect( x , y ) )
		{
			isButtonFocus = true;
			requestFocus();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		isButtonFocus = false;
		releaseFocus();
		if( isPointInButtonRect( x , y ) )
		{
			dealButton();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( iLoongLauncher.getInstance().getDesktop().getFocus() == this )
		{
			if( ( isButtonFocus ) && ( !isPointInButtonRect( x , y ) ) )
			{
				isButtonFocus = false;
			}
			else if( ( !isButtonFocus ) && ( isPointInButtonRect( x , y ) ) )
			{
				isButtonFocus = true;
			}
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( isPointInButtonRect( x , y ) )
		{
			dealButton();
			return true;
		}
		return false;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( source == workspaceAndAppTween && type == TweenCallback.COMPLETE )
		{
			workspaceAndAppTween = null;
			Desktop3DListener.root.mApplistEffectPreview.hide();
		}
	}
	
	public boolean isPointInButtonRect(
			float mPointX ,
			float mPointY )
	{
		if( ( mPointX >= 0 && mPointX <= width / 6 ) && ( mPointY >= 0 && mPointY <= height ) )
		{
			return true;
		}
		return false;
	}
	
	private void dealButton()
	{
		int type = mWorkspaceEffectPreview.isVisible() ? 0 : ( mApplistEffectPreview.isVisible() ? 1 : 0 );
		if( type == 0 )
		{
			if( DefaultLayout.show_music_page )
			{
				mWorkspace3D.addMusicView();
			}
			//xujin
			if( DefaultLayout.enable_camera )
			{
				mWorkspace3D.addCameraView();
			}
		}
		AndroidGraphics graphics = (AndroidGraphics)Gdx.graphics;
		graphics.forceRender( 30 );
		iLoongLauncher.getInstance().getD3dListener().getRoot().mIsInEffectPreviewMode = -1;
		Context mContext = iLoongLauncher.getInstance();
		if( RR.net_version )
		{
			Root3D mRoot = Desktop3DListener.root;
			if( ( mRoot.mWorkspaceEffectPreview != null ) && ( mRoot.mWorkspaceEffectPreview.isVisible() ) )
			{
				mRoot.mWorkspaceEffectPreview.hide();
				mRoot.getHotSeatBar().showNoAnim();
			}
			if( ( mRoot.mApplistEffectPreview != null ) && ( mRoot.mApplistEffectPreview.isVisible() ) )
			{
				mAppHost3D.appBar.show();
				workspaceAndAppTween = Timeline.createParallel();
				workspaceAndAppTween.push( mAppHost3D.appBar.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.2f , 1 , 0 , 0 ) );
				workspaceAndAppTween.push( mAppHost3D.appList.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , 0.2f , 0 , 0 , 0 ) );
				workspaceAndAppTween.push( mRoot.mApplistEffectPreview.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.2f , 0 , -mRoot.mApplistEffectPreview.getHeight() , 0 ) );
				workspaceAndAppTween.start( View3DTweenAccessor.manager ).setCallback( this );
			}
			if( ( mRoot.mEffectPreviewTips3D != null ) && ( mRoot.mEffectPreviewTips3D.isVisible() ) )
			{
				mRoot.mEffectPreviewTips3D.hide();
			}
			return;
		}
		Intent mIntent = new Intent();
		if( DefaultLayout.personal_center_internal )
		{
			mIntent.setComponent( new ComponentName( mContext , EFFECT_BOX_MAIN_ACTIVITY ) );
		}
		else
		{
			mIntent.setComponent( new ComponentName( "com.iLoong.base.themebox" , EFFECT_BOX_MAIN_ACTIVITY ) );
		}
		iLoongLauncher.getInstance().bindThemeActivityData( mIntent );
		mIntent.putExtra( EFFECT_BOX_TYPE , type );
		SendMsgToAndroid.startActivity( mIntent );
	}
	
	public void setWorkspaceEffectPreview3D(
			View3D v )
	{
		this.mWorkspaceEffectPreview = (EffectPreview3D)v;
	}
	
	public void setApplistEffectPreview3D(
			View3D v )
	{
		this.mApplistEffectPreview = (EffectPreview3D)v;
	}
	
	public void setHotSeat3D(
			HotSeat3D hotseat )
	{
		this.mHotSeat3D = hotseat;
	}
	
	public void setAppHost3D(
			AppHost3D appHost )
	{
		this.mAppHost3D = appHost;
	}
	
	public void backToBoxEffectTab()
	{
		dealButton();
	}
	
	public void setWorkspace(
			Workspace3D workspace )
	{
		this.mWorkspace3D = workspace;
	}
}
