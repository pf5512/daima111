// xiatian add whole file //EffectPreview
package com.iLoong.launcher.Functions.EffectPreview;


import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.pub.UmEventUtil;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class EffectPreview3D extends ViewGroup3D
{
	
	public int MAX_ICON_NUM = -1;
	private float cellWidth = 0;
	private GridView3D mShortcutView;
	public final static int TYPE_WORKSPACE = 0;
	public final static int TYPE_APPLIST = 1;
	private int mCurType = -1;
	private final int SCROLL_LEFT = 0;
	private final int SCROLL_RIGHT = 1;
	private int scrollDir = -1;
	private boolean isFling = false;
	private boolean bPermitAnim = true;
	private final float VELOCITY_DIV = 30f;
	private float velocity = 0f;
	private Tween flingTween = null;
	private Workspace3D mWorkspace3D;
	private AppHost3D mAppHost3D;
	public final static String ACTION_EFFECT_PREVIEW = "com.cool.action.EffectPreview";
	public final static String ACTION_EFFECT_PREVIEW_EXTRA_TYPE = "EffectPreviewExtraType";
	public final static String ACTION_EFFECT_PREVIEW_EXTRA_INDEX = "EffectPreviewExtraIndex";
	public final static String ICON_EXTRA_TYPE = "EffectPreviewIconExtraType";
	public final static String ICON_EXTRA_INDEX = "EffectPreviewIconExtraIndex";
	private static TextureRegion bgTextureRegion = null;
	private Timeline workspaceAndAppTween;
	private final static float workspaceTweenDuration = 0.2f;
	private static float applistMoveHeight = -1f;
	public static boolean isDelayEvent = false;
	
	public EffectPreview3D(
			String name ,
			int type )
	{
		super( name + type );
		x = 0;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = R3D.workspace_cell_height;
		setOrigin( width / 2 , height / 2 );
		init( type );
		bindItems();
	}
	
	private void init(
			int type )
	{
		mCurType = type;
		if( type == TYPE_WORKSPACE )
		{
			MAX_ICON_NUM = R3D.workSpace_list_string.length;
		}
		else if( type == TYPE_APPLIST )
		{
			MAX_ICON_NUM = R3D.app_list_string.length;
		}
		cellWidth = R3D.workspace_cell_width;
		if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() == 800 )
		{
			mShortcutView = new GridView3D( "EffectPreviewView3D - GridView3D" , cellWidth * MAX_ICON_NUM , height - 12 , MAX_ICON_NUM , 1 );
		}
		else
		{
			mShortcutView = new GridView3D( "EffectPreviewView3D - GridView3D" , cellWidth * MAX_ICON_NUM , height , MAX_ICON_NUM , 1 );
		}
		mShortcutView.setPadding( 0 , 0 , R3D.hot_sidebar_top_margin , 0 );
		mShortcutView.setPosition( 0 , 0 );
		this.addView( mShortcutView );
		bgTextureRegion = R3D.findRegion( R3D.mEffectPreviewBgRegionName );
		applistMoveHeight = R3D.appbar_height / 3;
	}
	
	private void bindItems()
	{
		Context themeBoxContext = null;
		if( DefaultLayout.personal_center_internal )
		{
			themeBoxContext = iLoongLauncher.getInstance();
		}
		else
		{
			try
			{
				themeBoxContext = iLoongLauncher.getInstance().createPackageContext( "com.iLoong.base.themebox" , 0 );
			}
			catch( NameNotFoundException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
				themeBoxContext = iLoongLauncher.getInstance();
			}
		}
		for( int i = 0 ; i < MAX_ICON_NUM ; i++ )
		{
			String title = "";
			Bitmap bmp = null;
			String path = null;
			Icon3D mIcon3D = null;
			if( mCurType == TYPE_WORKSPACE )
			{
				title = /*String.valueOf(i);*/R3D.workSpace_list_string[i];
				path = "launcher/effects/workspace/";
			}
			else if( mCurType == TYPE_APPLIST )
			{
				title = /*String.valueOf(i);*/R3D.app_list_string[i];
				path = "launcher/effects/applist/";
			}
			try
			{
				int index = i;
				if( DefaultLayout.page_effect_no_radom_style )
				{
					if( index != 0 )
					{
						index++;
					}
				}
				bmp = Tools.getImageFromInStream( themeBoxContext.getAssets().open( path + index + ".png" ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			if( bmp != null )
			{
				bmp = Tools.resizeBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
				mIcon3D = new Icon3D( title , bmp , title , null );
				ApplicationInfo mApplicationInfo = new ApplicationInfo( "EffectPreview3D" , null );
				mApplicationInfo.intent.putExtra( ICON_EXTRA_TYPE , mCurType );
				mApplicationInfo.intent.putExtra( ICON_EXTRA_INDEX , i );
				ShortcutInfo mShortcutInfo = mApplicationInfo.makeShortcut();
				mIcon3D.setItemInfo( mShortcutInfo );
				if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() == 800 )
				{
					mIcon3D.setScale( 0.8f , 0.8f );
				}
				mShortcutView.addItem( (View3D)mIcon3D );
			}
		}
	}
	
	public void setWorkspace(
			Workspace3D workspace )
	{
		this.mWorkspace3D = workspace;
	}
	
	public void setAppHost(
			AppHost3D apphost )
	{
		this.mAppHost3D = apphost;
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( !super.scroll( x , y , deltaX , deltaY ) )
		{
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					ParticleManager.disableClickIcon = true;
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			if( deltaX > 0 )
			{
				scrollDir = SCROLL_RIGHT;
			}
			else if( deltaX < 0 )
			{
				scrollDir = SCROLL_LEFT;
			}
			if( ( mShortcutView != null ) && ( mShortcutView.isVisible() ) )
			{
				mShortcutView.x += deltaX;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		super.onTouchDown( x , y , pointer );//icon点击变暗效果
		mShortcutView.stopTween();
		mShortcutView.setUser( 0 );
		isFling = false;
		bPermitAnim = true;
		requestFocus();
		return true;// super.onTouchDown(x, y, pointer);
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( mShortcutView.lastTouchedChild != null )
		{
			mShortcutView.lastTouchedChild.color.a = 1f;
		}
		super.onTouchUp( x , y , pointer );
		if( !isFling )
		{
			startScrollTween();
		}
		releaseFocus();
		return super.onTouchUp( x , y , pointer );
	}
	
	private void startScrollTween()
	{
		if( bPermitAnim == false || mShortcutView == null || mShortcutView.getChildCount() == 0 )
			return;
		float mFocusViewX = ( mShortcutView.x );
		float mFocusViewWidth = mShortcutView.getEffectiveWidth();
		isFling = false;
		if( mFocusViewWidth <= width && isScrollEnd() )
		{
			{
				mShortcutView.stopTween();
				mShortcutView.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , 0 , mShortcutView.y , 0 );
				isFling = false;
			}
		}
		else if( mFocusViewX < 0 || mFocusViewX + mFocusViewWidth > width )
		{
			if( isScrollEnd() )
			{
				if( scrollDir == SCROLL_LEFT )
				{
					mShortcutView.stopTween();
					mShortcutView.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , width - mFocusViewWidth , mShortcutView.y , 0 );
					isFling = false;
				}
				else if( scrollDir == SCROLL_RIGHT )
				{
					mShortcutView.stopTween();
					mShortcutView.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 1f , 0 , mShortcutView.y , 0 );
					isFling = false;
				}
			}
		}
	}
	
	private boolean isScrollEnd()
	{// first or last in screen
		if( mShortcutView.getChildCount() == 0 )
			return false;
		float mFocusViewX = mShortcutView.x;
		float mFocusViewWidth = mShortcutView.getEffectiveWidth();
		if( mFocusViewX + mFocusViewWidth < width || mFocusViewX > 0 )
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( bPermitAnim == false )
		{
			scrollDir = -1;
			return true;
		}
		if( velocityX > 0 )
		{
			scrollDir = SCROLL_RIGHT;
		}
		else if( velocityX < 0 )
		{
			scrollDir = SCROLL_LEFT;
		}
		if( Math.abs( velocityX ) < VELOCITY_DIV * 2 )
		{
			return true;
		}
		isFling = true;
		velocity = velocityX / VELOCITY_DIV;
		mShortcutView.setUser( velocity );
		mShortcutView.stopTween();
		flingTween = mShortcutView.startTween( View3DTweenAccessor.USER , Cubic.OUT , 2.5f , 0 , 0 , 0 );
		return true;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == flingTween && type == TweenCallback.COMPLETE )
		{
			startScrollTween();
		}
		else if( source == workspaceAndAppTween && type == TweenCallback.COMPLETE )
		{
			if( workspaceAndAppTween.getUserData().equals( TYPE_APPLIST ) )
			{
				//show mApplistEffectPreview
				workspaceAndAppTween = null;
				if( mAppHost3D.appBar != null )
				{
					mAppHost3D.appBar.hide();
				}
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						mAppHost3D.appList.startPreviewEffect( true );
					}
				} );
			}
			else if( workspaceAndAppTween.getUserData().equals( TYPE_WORKSPACE ) )
			{
				//show mWorkspaceEffectPreview
				workspaceAndAppTween = null;
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						mWorkspace3D.startPreviewEffect( true );
					}
				} );
			}
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		int w = bgTextureRegion.getRegionWidth();
		for( int i = 0 ; i < width / w + 1 ; i++ )
		{
			if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() == 800 )
			{
				batch.draw( bgTextureRegion , i * w , y , w , height - 30 );
			}
			else
			{
				batch.draw( bgTextureRegion , i * w , y , w , height );
			}
		}
		super.draw( batch , parentAlpha );
		if( isScrollEnd() )
		{
			if( flingTween != null && !flingTween.isFinished() )
			{
				mShortcutView.stopTween();
				flingTween = null;
			}
			if( !mShortcutView.isAutoMove && mShortcutView.getTween() == null )
			{
				mShortcutView.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.5f , 0f , 0f , 0f );
			}
		}
		if( mShortcutView.isAutoMove && mShortcutView.getFocusView() != null )
		{
			if( mShortcutView.isScrollToEnd( -(int)mShortcutView.getUser() ) )
			{
				mShortcutView.x += mShortcutView.getUser();
				mShortcutView.getFocusView().x += -mShortcutView.getUser();
			}
		}
		else
		{
			if( Math.abs( mShortcutView.getUser() ) > 2 )
			{
				mShortcutView.x += mShortcutView.getUser();
			}
			else
			{
				if( isFling )
				{
					startScrollTween();
				}
			}
		}
	}
	
	@Override
	protected void drawChildren(
			SpriteBatch batch ,
			float parentAlpha )
	{
		parentAlpha *= color.a;
		if( cullingArea != null )
		{
			if( transform )
			{
				for( int i = 0 ; i < children.size() ; i++ )
				{
					View3D child = children.get( i );
					if( !child.visible )
						continue;
					if( child.x <= cullingArea.x + cullingArea.width && child.x + child.width >= cullingArea.x && child.y <= cullingArea.y + cullingArea.height && child.y + child.height >= cullingArea.y )
					{
						if( child.background9 != null )
						{
							child.background9.draw( batch , child.x , child.y , child.width , child.height );
						}
						child.draw( batch , parentAlpha );
					}
				}
				batch.flush();
			}
			else
			{
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				for( int i = 0 ; i < children.size() ; i++ )
				{
					View3D child = children.get( i );
					if( !child.visible )
						continue;
					if( child.x <= cullingArea.x + cullingArea.width && child.x + child.width >= cullingArea.x && child.y <= cullingArea.y + cullingArea.height && child.y + child.height >= cullingArea.y )
					{
						child.x += offsetX;
						child.y += offsetY;
						if( child.background9 != null )
						{
							child.background9.draw( batch , child.x , child.y , child.width , child.height );
						}
						child.draw( batch , parentAlpha );
						child.x -= offsetX;
						child.y -= offsetY;
					}
				}
				x = offsetX;
				y = offsetY;
			}
		}
		else
		{
			if( transform )
			{
				for( int i = 0 ; i < children.size() ; i++ )
				{
					View3D child = children.get( i );
					if( !child.visible )
						continue;
					if( child instanceof ViewGroup3D )
					{
						if( child.background9 != null )
						{
							if( child.is3dRotation() )
								child.applyTransformChild( batch );
							batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
							child.background9.draw( batch , child.x , child.y , child.width , child.height );
							if( child.is3dRotation() )
								child.resetTransformChild( batch );
						}
						child.draw( batch , parentAlpha );
						continue;
					}
					if( child.is3dRotation() )
						child.applyTransformChild( batch );
					if( child.background9 != null )
					{
						batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
						child.background9.draw( batch , child.x , child.y , child.width , child.height );
					}
					child.draw( batch , parentAlpha );
					if( child.is3dRotation() )
						child.resetTransformChild( batch );
				}
			}
			else
			{
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				for( int i = 0 ; i < children.size() ; i++ )
				{
					View3D child = children.get( i );
					if( !child.visible )
						continue;
					child.x += offsetX;
					child.y += offsetY;
					if( child.background9 != null )
					{
						batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
						child.background9.draw( batch , child.x , child.y , child.width , child.height );
					}
					child.draw( batch , parentAlpha );
					child.x -= offsetX;
					child.y -= offsetY;
				}
				x = offsetX;
				y = offsetY;
			}
		}
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		mShortcutView.setFocusView( (int)x , (int)y );
		if( mShortcutView.getFocusView() != null )
		{
			mShortcutView.getFocusView().color.a = 1f;
			mShortcutView.releaseDark();
			mShortcutView.releaseFocus();
			mShortcutView.clearFocusView();
		}
		//		SendMsgToAndroid.vibrator(R3D.vibrator_duration);
		return true;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( AppHost3D.appList != null && !AppHost3D.appList.isPreviewEffectFinished() )
		{
			return true;
		}
		int index = mShortcutView.getIndex( (int)( -mShortcutView.x + x ) , (int)y );
		Context mContext = iLoongLauncher.getInstance();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( mContext );
		String prefsKey = "";
		if( mCurType == TYPE_WORKSPACE )
		{
			prefsKey = mContext.getResources().getString( RR.string.setting_key_desktopeffects );
			prefs.edit().putString( prefsKey , String.valueOf( index ) ).commit();
			PubProviderHelper.addOrUpdateValue( "effect" , prefsKey , String.valueOf( index ) );
			SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
			mWorkspace3D.setEffectType( index );
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					mWorkspace3D.startPreviewEffect( true );
				}
			} );
		}
		else if( mCurType == TYPE_APPLIST )
		{
			prefsKey = mContext.getResources().getString( RR.string.setting_key_appeffects );
			prefs.edit().putString( prefsKey , String.valueOf( index ) ).commit();
			PubProviderHelper.addOrUpdateValue( "effect" , prefsKey , String.valueOf( index ) );
			mAppHost3D.appList.setEffectType( index );
			SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					mAppHost3D.appList.startPreviewEffect( true );
				}
			} );
			if( !isDelayEvent )
			{
				isDelayEvent = true;
				UmEventUtil.applistEffect( mContext , prefsKey , 60000 );
			}
		}
		return true;
	}
	
	public void show()
	{
		int index = -1;
		if( mCurType == TYPE_WORKSPACE )
		{
			index = mWorkspace3D.mType;
		}
		else if( mCurType == TYPE_APPLIST )
		{
			index = mAppHost3D.appList.mType;
		}
		if( index != -1 )
		{
			int position = -1;
			if( index <= 3 )
			{
				position = 0;
			}
			else if( ( index >= MAX_ICON_NUM - 4 ) && ( index < MAX_ICON_NUM ) )
			{
				position = MAX_ICON_NUM - 4;
			}
			else
			{
				position = index;
			}
			mShortcutView.x = -position * cellWidth;
		}
		super.show();
	}
	
	public void hide()
	{
		mShortcutView.x = 0;
		super.hide();
		if( AppHost3D.appList != null )
		{
			AppHost3D.appList.refreshUninstall( false );
		}
	}
	
	public void startApplistEffectPreviewAnim(
			int mEffectIndex )
	{
		if( workspaceAndAppTween != null && workspaceAndAppTween.isStarted() )
		{
			return;
		}
		if( AppHost3D.appList != null )
		{
			AppHost3D.appList.refreshUninstall( true );
		}
		workspaceAndAppTween = Timeline.createParallel();
		if( mAppHost3D.appBar != null )
		{
			workspaceAndAppTween.push( mAppHost3D.appBar.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , workspaceTweenDuration , 0 , 0 , 0 ) );
		}
		if( mAppHost3D.appList.getVisible() == false )
		{
			mAppHost3D.appList.setVisible( true );
			mAppHost3D.appList.touchable = true;
		}
		int cur_applist_page = mAppHost3D.appList.getCurrentPage();
		int applist_apppagecnt = mAppHost3D.appList.appPageCount;
		int widgetPageCount = mAppHost3D.appList.widgetPageCount;
		// when applist curpage is in widget pages, so set curpage 0
		if( cur_applist_page > applist_apppagecnt - 1 && cur_applist_page <= applist_apppagecnt + widgetPageCount - 1 )
		{
			mAppHost3D.appList.setCurrentPageOnly( 0 );
		}
		mAppHost3D.appList.setUser( 0 );
		if( Utils3D.getScreenWidth() == 480 && Utils3D.getScreenHeight() == 800 )
		{
			workspaceAndAppTween.push( mAppHost3D.appList.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , workspaceTweenDuration , applistMoveHeight - 16 , 0 , 0 ) );
		}
		else
		{
			workspaceAndAppTween.push( mAppHost3D.appList.obtainTween( View3DTweenAccessor.USER , Linear.INOUT , workspaceTweenDuration , applistMoveHeight , 0 , 0 ) );
		}
		Context mContext = iLoongLauncher.getInstance();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( mContext );
		String prefsKey = mContext.getResources().getString( RR.string.setting_key_appeffects );
		prefs.edit().putString( prefsKey , String.valueOf( mEffectIndex ) ).commit();
		PubProviderHelper.addOrUpdateValue( "effect" , prefsKey , String.valueOf( mEffectIndex ) );
		SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
		mAppHost3D.appList.setEffectType( mEffectIndex );
		this.setPosition( 0 , -height );
		this.show();
		workspaceAndAppTween.push( this.obtainTween( View3DTweenAccessor.POS_XY , Cubic.INOUT , workspaceTweenDuration , 0 , 0 , 0 ) );
		workspaceAndAppTween.setCallback( this ).start( View3DTweenAccessor.manager ).setUserData( TYPE_APPLIST );
	}
	
	public void startWorkspaceEffectPreviewAnim(
			int mEffectIndex ,
			HotSeat3D hotseatBar )
	{
		if( workspaceAndAppTween != null && workspaceAndAppTween.isStarted() )
		{
			return;
		}
		hotseatBar.hideNoAnim();
		workspaceAndAppTween = Timeline.createParallel();
		Context mContext = iLoongLauncher.getInstance();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( mContext );
		String prefsKey = mContext.getResources().getString( RR.string.setting_key_desktopeffects );
		prefs.edit().putString( prefsKey , String.valueOf( mEffectIndex ) ).commit();
		PubProviderHelper.addOrUpdateValue( "effect" , prefsKey , String.valueOf( mEffectIndex ) );
		SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
		mWorkspace3D.setEffectType( mEffectIndex );
		this.setPosition( 0 , -height );
		this.show();
		workspaceAndAppTween.push( this.obtainTween( View3DTweenAccessor.POS_XY , Linear.INOUT , workspaceTweenDuration , 0 , 0 , 0 ) );
		workspaceAndAppTween.setCallback( this ).start( View3DTweenAccessor.manager ).setUserData( TYPE_WORKSPACE );
	}
}
