package com.iLoong.launcher.search;


import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.TextField3D;
import com.iLoong.launcher.UI3DEngine.TextFieldListener;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class SearchEditTextGroup extends ViewGroup3D implements TextFieldListener
{
	
	private View3D mEditFrame;
	private View3D mSearchBtn;
	private View3D mSearchHint;
	public TextField3D mEditText;
	private Paint mPaint;
	public final static int POS_STATUS_TOP = 0;
	public final static int POS_STATUS_BOTTOM = 1;
	public static int mStatus = POS_STATUS_BOTTOM;
	
	public SearchEditTextGroup(
			String name )
	{
		super( name );
		setSize( QsearchConstants.W_SEARCH_EDIT_GROUP , QsearchConstants.H_SEARCH_EDIT_GROUP );
		if( mPaint == null )
		{
			mPaint = new Paint();
			mPaint.setColor( Color.WHITE );
			mPaint.setAntiAlias( true );
			mPaint.setTextSize( QsearchConstants.S_INPUT_TEXT );
		}
		try
		{
			Bitmap editFrameBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/search_circle.png" ) );
			NinePatch editFramsNp = new NinePatch( new BitmapTexture( editFrameBmp , true ) , 8 , 8 , 20 , 20 );
			mEditFrame = new View3D( "editFrame" ) {
				
				@Override
				public boolean onClick(
						float x ,
						float y )
				{
					if( mStatus == POS_STATUS_BOTTOM )
					{
						Desktop3DListener.root.qSearchGroup.mSearchResultGroup.mSearchResultList.query( "" );
						startAnim();
					}
					MobclickAgent.onEvent( iLoongLauncher.getInstance() , "EntertheQuickFindsearchInterface" );
					mEditText.showInputKeyboard();
					return true;
				}
			};
			mEditFrame.setBackgroud( editFramsNp );
			mEditFrame.setSize( QsearchConstants.W_SEATCH_EDIT_FRAME , QsearchConstants.H_SEARCH_EDIT_FRAME );
			mEditFrame.setPosition( QsearchConstants.X_SEARCH_EDIT_FRAME , QsearchConstants.Y_SEARCH_EDIT_FRAME );
			Bitmap searchBtnBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets().open( "theme/quick_search/search_icon.png" ) );
			mSearchBtn = new View3D( "searchBtn" , new BitmapTexture( searchBtnBmp , true ) );
			mSearchBtn.setSize( QsearchConstants.W_SEARCH_BTN , QsearchConstants.H_SEARCH_BTN );
			mSearchBtn.setPosition( QsearchConstants.X_SEARCH_BTN , QsearchConstants.Y_SEARCH_BTN );
			Bitmap searchHintBmp = Tools.getImageFromInStream( iLoongLauncher.getInstance().getAssets()
					.open( iLoongLauncher.curLanguage == 2 ? "theme/quick_search/search_hint.png" : "theme/quick_search/search_hint_zh.png" ) );
			mSearchHint = new View3D( "searchHint" , new BitmapTexture( searchHintBmp , true ) );
			mSearchHint.setSize( QsearchConstants.W_SEARCH_HINT , QsearchConstants.H_SEARCH_HINT );
			mSearchHint.setPosition( QsearchConstants.X_SEARCH_HINT , QsearchConstants.Y_SEARCH_HINT );
			buildTextField3D( QsearchConstants.W_SEARCH_TEXT_FIELD , QsearchConstants.H_SEARCH_TEXT_FIELD );
			addView( mEditFrame );
			addView( mSearchBtn );
			addView( mSearchHint );
			addView( mEditText );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
	}
	
	public void resetPostion()
	{
		mEditFrame.setPosition( QsearchConstants.X_SEARCH_EDIT_FRAME , QsearchConstants.Y_SEARCH_EDIT_FRAME );
		mSearchBtn.setPosition( QsearchConstants.X_SEARCH_BTN , QsearchConstants.Y_SEARCH_BTN );
		mSearchHint.setPosition( QsearchConstants.X_SEARCH_HINT , QsearchConstants.Y_SEARCH_HINT );
		mSearchHint.show();
		if( mEditText != null )
		{
			mEditText.hide();
			mEditText.hideInputKeyboard();
			mEditText.setPosition( QsearchConstants.X_SEARCH_TEXT_FIELD , QsearchConstants.Y_SEARCH_TEXT_FIELD );
		}
		mStatus = POS_STATUS_BOTTOM;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		float scrollX = this.x;
		if( mStatus == POS_STATUS_BOTTOM && Math.abs( deltaY ) / Math.abs( deltaX ) < 0.5f )
		{
			if( deltaX > 0 )
			{
				this.setPosition( this.x + deltaX , 0 );
				Root3D.hotseatBar.setPosition( Root3D.hotseatBar.x + deltaX , 0 );
			}
			else if( deltaX < 0 )
			{
				if( scrollX > 0 )
				{
					this.setPosition( this.x + Math.max( deltaX , -scrollX ) , 0 );
					Root3D.hotseatBar.setPosition( Root3D.hotseatBar.x + Math.min( deltaX , scrollX ) , 0 );
				}
			}
		}
		return true;
	}
	
	private float mVelocityX;
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		mVelocityX = velocityX;
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		requestFocus();
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( this.x > UtilsBase.getScreenWidth() / 2 || mVelocityX > 1000 )
		{
			Desktop3DListener.root.qSearchGroup.startQuickSearchAnimQuit( true );
		}
		else
		{
			if( mStatus == POS_STATUS_BOTTOM )
			{
				Desktop3DListener.root.qSearchGroup.startResetAnim( false );
			}
		}
		releaseFocus();
		mVelocityX = 0;
		Desktop3DListener.root.qSearchGroup.startPopFadeOutAnim();
		return super.onTouchUp( x , y , pointer );
	}
	
	private void buildTextField3D(
			float textWidth ,
			float textHeight )
	{
		if( findView( "editText" ) == null )
		{
			mEditText = new TextField3D( "editText" , textWidth , textHeight , mPaint );
			mEditText.setPosition( QsearchConstants.X_SEARCH_TEXT_FIELD , QsearchConstants.Y_SEARCH_TEXT_FIELD );
			mEditText.setOrigin( mEditText.width / 2 , mEditText.height / 2 );
			mEditText.setText( "" );
			mEditText.updateDisplayOpacityText();
			mEditText.setTextFieldListener( this );
			mEditText.setKeyboardAdapter( null );
			mEditText.setEditable( true );
			mEditText.setMediate( false );
			mEditText.show();
		}
	}
	
	@Override
	public void valueChanged(
			TextField3D textField ,
			String newValue )
	{
		if( newValue.equals( "" ) )
		{
			mSearchHint.show();
		}
		else
		{
			mSearchHint.hide();
		}
		Desktop3DListener.root.qSearchGroup.mSearchResultGroup.mSearchResultList.query( newValue );
	}
	
	Timeline timeline = null;
	
	private void startAnim()
	{
		if( mStatus == POS_STATUS_TOP )
		{
			return;
		}
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
		mEditText.setText( "" );
		mSearchHint.show();
		float duration = 0.5f;
		timeline = Timeline.createParallel();
		timeline.push( Tween.to( this , View3DTweenAccessor.POS_XY , duration ).target( 0 , QsearchConstants.H_QUICK_SEARCH - this.height + 44 * QsearchConstants.S_SCALE ) );
		timeline.push( Tween.to( mEditFrame , View3DTweenAccessor.POS_XY , duration ).target( mEditFrame.x , QsearchConstants.S_SCALE * 47 / 2 ) );
		timeline.push( Tween.to( mSearchBtn , View3DTweenAccessor.POS_XY , duration ).target( mSearchBtn.x , mSearchBtn.y - QsearchConstants.S_SCALE * 47 / 2 ) );
		timeline.push( Tween.to( mEditText , View3DTweenAccessor.POS_XY , duration ).target( mEditText.x , mEditText.y - QsearchConstants.S_SCALE * 47 / 2 ) );
		timeline.push( Tween.to( mSearchHint , View3DTweenAccessor.POS_XY , duration ).target( mSearchHint.x , mSearchHint.y - QsearchConstants.S_SCALE * 47 / 2 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mSearchResultGroup , View3DTweenAccessor.POS_XY , duration ).target( 0 , 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.getGuessGroup() , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mAllAppGroup , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.getLettersGroup() , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.getGuessGroup().mGuessPage.indicatorView , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mLettersGroupButtomFill , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mButtonFill , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mTopFill , View3DTweenAccessor.OPACITY , duration ).target( 0 ) );
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		mEditText.show();
		mStatus = POS_STATUS_TOP;
	}
	
	public void startAnimQuit()
	{
		if( timeline != null )
		{
			timeline.free();
			timeline = null;
		}
		float duration = 0.5f;
		timeline = Timeline.createParallel();
		timeline.push( Tween.to( this , View3DTweenAccessor.POS_XY , duration ).target( 0 , 0 ) );
		timeline.push( Tween.to( mEditFrame , View3DTweenAccessor.POS_XY , duration ).target( mEditFrame.x , QsearchConstants.Y_SEARCH_EDIT_FRAME ) );
		timeline.push( Tween.to( mSearchBtn , View3DTweenAccessor.POS_XY , duration ).target( mSearchBtn.x , QsearchConstants.Y_SEARCH_BTN ) );
		timeline.push( Tween.to( mEditText , View3DTweenAccessor.POS_XY , duration ).target( QsearchConstants.X_SEARCH_TEXT_FIELD , QsearchConstants.Y_SEARCH_TEXT_FIELD ) );
		timeline.push( Tween.to( mSearchHint , View3DTweenAccessor.POS_XY , duration ).target( QsearchConstants.X_SEARCH_HINT , QsearchConstants.Y_SEARCH_HINT ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mSearchResultGroup , View3DTweenAccessor.POS_XY , duration ).target( 0 , -UtilsBase.getScreenHeight() ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.getGuessGroup() , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mAllAppGroup , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.getLettersGroup() , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.getGuessGroup().mGuessPage.indicatorView , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mLettersGroupButtomFill , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mButtonFill , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.push( Tween.to( Desktop3DListener.root.qSearchGroup.mTopFill , View3DTweenAccessor.OPACITY , duration ).target( 1 ) );
		timeline.start( View3DTweenAccessor.manager ).setCallback( this );
		mEditText.clear();
		mEditText.hide();
		mEditText.hideInputKeyboard();
		mStatus = POS_STATUS_BOTTOM;
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( timeline != null && source == timeline && type == TweenCallback.COMPLETE )
		{
			if( mStatus == POS_STATUS_BOTTOM )
			{
				mSearchHint.show();
			}
		}
		super.onEvent( type , source );
	}
}
