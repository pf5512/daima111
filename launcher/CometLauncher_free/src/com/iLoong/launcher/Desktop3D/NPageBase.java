package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class NPageBase extends ViewGroup3D
{
	
	final public static float MAX_X_ROTATION = 0.5f;
	public boolean needXRotation = true;
	public IndicatorView indicatorView;
	public float indicatorPaddingBottom = 0;
	protected boolean drawIndicator = true;
	// private TextureRegion indicator1 = null;
	// private TextureRegion indicator2 = null;
	protected int page_index;
	protected ArrayList<View3D> view_list;
	private boolean random;
	private boolean sequence;
	private boolean canScroll;
	protected float xScale;
	protected float yScale;
	public boolean moving;
	protected int mType;
	protected float mVelocityX;
	protected boolean needLayout = false;
	private static float INDICATOR_FADE_TWEEN_DURATION = 1.0f;
	private static Color initColor = new Color( 1.0f , 1.0f , 1.0f , 1.0f );
	protected Tween tween;
	List<PageScrollListener> scrollListeners = new ArrayList<PageScrollListener>();
	protected ArrayList<Integer> mTypelist;
	/************************ added by zhenNan.ye begin ***************************/
	private float last_x = 0;
	private float last_y = 0;
	
	/************************ added by zhenNan.ye end ***************************/
	public NPageBase(
			String name )
	{
		super( name );
		// if(indicatorView == null)indicatorView = new
		// IndicatorView("npage_indicator",R3D.getTextureRegion("application-page-nv-point1"),R3D.getTextureRegion("application-page-nv-point2"));
		page_index = 0;
		xScale = 0f;
		yScale = 0f;
		mType = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		random = true;
		canScroll = false;
		view_list = new ArrayList<View3D>();
		moving = false;
		mTypelist = new ArrayList<Integer>();
		APageEase.initEffectMap();
		setTotalList();
	}
	
	public void setWholePageList()
	{
		mTypelist.clear();
		for( int i = 0 ; i < R3D.workSpace_list_string.length ; i++ )
		{
			mTypelist.add( APageEase.mEffectMap.get( R3D.workSpace_list_string[i] ) );
		}
	}
	
	void setTotalList()
	{
		mTypelist.clear();
		for( int i = 0 ; i < R3D.app_list_string.length ; i++ )
		{
			mTypelist.add( APageEase.mEffectMap.get( R3D.app_list_string[i] ) );
		}
		setEffectType( SetupMenuActions.getInstance().getStringToIntger( "desktopeffects" ) );
	}
	
	protected void initView()
	{
		View3D view;
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			view = view_list.get( i );
			view.setPosition( 0 , 0 );
			view.setRotationZ( 0 );
			view.setScale( 1.0f , 1.0f );
			view.setColor( initColor );
			view.setOrigin( view.width / 2 , view.height / 2 );
			view.setZ( 0 );
			view.setOriginZ( 0 );
			if( i != page_index )
				view.hide();
			else
				view.show();
			// 还原icon
			if( view instanceof ViewGroup3D )
			{
				int size = ( (ViewGroup3D)view ).getChildCount();
				View3D icon;
				for( int j = 0 ; j < size ; j++ )
				{
					icon = ( (ViewGroup3D)view ).getChildAt( j );
					icon.setRotationZ( 0 );
					icon.setScale( 1.0f , 1.0f );
					icon.setColor( initColor );
					icon.setOrigin( icon.width / 2 , icon.height / 2 );
					icon.setOriginZ( 0 );
					// Object obj = icon.getTag();
					// if(obj!= null)
					// {
					// if(obj instanceof Vector2)
					// {
					// icon.setPosition(((Vector2)icon.getTag()).x,((Vector2)icon.getTag()).y);
					// }
					// }
				}
				if( view instanceof GridView3D )
					( (GridView3D)view ).layout_pub( 0 , false );
			}
		}
		// setDegree(0f);
		// setDegree(0f, 0f);
		moving = false;
	}
	
	public void initData(
			ViewGroup3D view )
	{
		// Log.v("NpageBase", "initData");
		// Color temp_color;
		//
		// view.setPosition(0, 0);
		// view.setZ(0);
		// view.setRotationVector(0, 0, 1);
		// view.setRotation(0);
		// view.setRotationAngle(0, 0, 0);
		// view.setScale(1.0f, 1.0f);
		// view.setScaleZ(1.0f);
		// view.setOrigin(width / 2, height / 2);
		// view.setOriginZ(0);
		// temp_color = getColor();
		// temp_color.a = 1;
		// view.setColor(temp_color);
		//
		// int size;
		// if(view instanceof GridView3D){
		// size = ((GridView3D)view).getChildCount();
		//
		// }
		// else
		// size = view.getChildCount();
		// for(int i = 0;i<size;i++)
		// {
		// View3D icon;
		// if(view instanceof GridView3D)
		// {
		// icon = ((GridView3D)view).getChildAt(i);
		// Object obj = icon.getTag();
		// if(obj!= null)
		// {
		// if(obj instanceof Vector2)
		// {
		// icon.setPosition(((Vector2)icon.getTag()).x,((Vector2)icon.getTag()).y);
		// }
		// }
		// }
		// else
		// {
		// icon = view.getChildAt(i);
		// }
		// icon.setZ(0);
		// icon.setRotationVector(0, 0, 1);
		// icon.setRotation(0);
		// icon.setRotationAngle(0, 0, 0);
		// icon.setScale(1.0f, 1.0f);
		// icon.setScaleZ(1.0f);
		// icon.setOriginZ(0);
		// temp_color = icon.getColor();
		// temp_color.a = 1;
		// icon.setColor(temp_color);
		// icon.show();
		// }
	}
	
	public boolean getRandom()
	{
		return this.random;
	}
	
	protected int getIndicatorPageCount()
	{
		return this.view_list.size();
	}
	
	protected int getIndicatorPageIndex()
	{
		return this.page_index;
	}
	
	protected void setEffectType(
			int type )
	{
		this.random = false;
		this.sequence = false;
		/*
		 * if (type == APageEase.COOLTOUCH_EFFECT_SEQUENCE) { this.mType =
		 * APageEase.COOLTOUCH_EFFECT_BINARIES; this.sequence = true; } else
		 */
		if( type == 1 )
		{
			this.mType = 2;
			this.random = true;
		}
		else
		{
			this.mType = type;
		}
		if( this.isVisible() )
			initView();
	}
	
	protected void addPage(
			View3D view )
	{
		view.setPosition( 0 , 0 );
		if( view_list.size() != 0 )
		{
			view.hide();
		}
		view_list.add( view );
		addView( view );
	}
	
	public void addPage(
			int index ,
			View3D view )
	{
		view.setPosition( 0 , 0 );
		if( view_list.size() != 0 )
		{
			view.hide();
		}
		view_list.add( index , view );
		this.addViewAt( index , view );
	}
	
	protected int nextIndex()
	{
		return( page_index == view_list.size() - 1 ? 0 : page_index + 1 );
	}
	
	protected int preIndex()
	{
		return( page_index == 0 ? view_list.size() - 1 : page_index - 1 );
	}
	
	protected void changeEffect()
	{
		initView();
		if( this.random )
		{
			moving = true;
			mType = MathUtils.random( 3 , mTypelist.size() - 1 );
		}
		else if( this.sequence )
		{
			moving = true;
			mType++;
			if( mType == mTypelist.size() )
				mType = 3;
		}
	}
	
	protected void updateEffect()
	{
		// Log.v("NPageBase", "updateEffect");
		if( view_list.size() == 0 )
			return;
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( DefaultLayout.enable_DesktopIndicatorScroll && Root3D.scroll_indicator )
		{
			return;
		}
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex() );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex() );
		if( !moving )
		{
			changeEffect();
			moving = true;
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					for( int j = 0 ; j < ( (GridView3D)i ).getChildCount() ; j++ )
					{
						View3D icon = ( (GridView3D)i ).getChildAt( j );
						icon.setTag( new Vector2( icon.getX() , icon.getY() ) );
					}
				}
			}
		}
		float tempYScale = 0;
		if( needXRotation )
		{
			if( yScale > MAX_X_ROTATION )
			{
				tempYScale = MAX_X_ROTATION;
			}
			else if( yScale < -MAX_X_ROTATION )
			{
				tempYScale = -MAX_X_ROTATION;
			}
			else
			{
				tempYScale = yScale;
			}
		}
		tempYScale = -tempYScale;
		if( this.random == false && this.mType == 0 )
		{
			APageEase.setStandard( true );
		}
		else
		{
			APageEase.setStandard( false );
		}
		if( xScale > 0 )
		{
			next_view.hide();
			// initData(next_view);
			APageEase.updateEffect( pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
		}
		else if( xScale < 0 )
		{
			pre_view.hide();
			// initData(pre_view);
			APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
		}
		else if( yScale != 0 )
		{
			APageEase.updateEffect( cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
		}
		if( xScale < -1f )
		{
			cur_view.hide();
			// initData(cur_view);
			page_index = nextIndex();
			setDegree( xScale + 1f );
			changeEffect();
		}
		if( xScale > 1f )
		{
			cur_view.hide();
			// initData(cur_view);
			page_index = preIndex();
			changeEffect();
		}
	}
	
	public void updateEffect(
			float scroll_degree )
	{
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( view_list.size() <= 1 )
		{
			return;
		}
		if( getRandom() == false && mType == 0 )
		{
			APageEase.setStandard( true );
		}
		else
		{
			APageEase.setStandard( false );
		}
		page_index = (int)( scroll_degree + 0.5 );
		Log.v( "Root3D" , "updateEffect " + page_index );
		// Log.v("jbc","888update temp_degree="+scroll_degree+" page_index="+page_index);
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex() );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex() );
		if( !moving )
		{
			moving = true;
		}
		APageEase.updateEffect( pre_view , cur_view , next_view , scroll_degree , false );
	}
	
	protected void setDegree(
			float degree )
	{
		this.xScale = degree;
		// TODO Auto-generated method stub
		if( this.scrollListeners != null )
		{
			for( int i = 0 ; i < scrollListeners.size() ; i++ )
			{
				PageScrollListener scrollListener = scrollListeners.get( i );
				scrollListener.pageScroll( xScale , page_index , view_list.size() );
				if( xScale == 0 && scrollListener.getIndex() != this.getCurrentPage() )
				{
					scrollListener.setCurrentPage( this.getCurrentPage() );
				}
			}
		}
		onDegreeChanged();
	}
	
	void setDegreeOnly(
			float degree )
	{
		this.xScale = degree;
		// TODO Auto-generated method stub
	}
	
	void setDegree(
			float xScale ,
			float yScale )
	{
		if( indicatorView != null )
		{
			indicatorView.pariticle_handle( xScale );
		}
		this.xScale = xScale;
		this.yScale = yScale;
		if( this.scrollListeners != null )
		{
			for( int i = 0 ; i < scrollListeners.size() ; i++ )
			{
				PageScrollListener scrollListener = scrollListeners.get( i );
				scrollListener.pageScroll( xScale , page_index , view_list.size() );
				if( xScale == 0 && scrollListener.getIndex() != this.getCurrentPage() )
				{
					scrollListener.setCurrentPage( this.getCurrentPage() );
				}
			}
		}
		onDegreeChanged();
	}
	
	void stopAutoEffect()
	{
		// TODO Auto-generated method stub
		if( tween != null && !tween.isFinished() )
		{
			tween.free();
			tween = null;
		}
	}
	
	@Override
	public float getX()
	{
		// TODO Auto-generated method stub
		return xScale;
	}
	
	@Override
	public float getY()
	{
		// TODO Auto-generated method stub
		return yScale;
	}
	
	public void onDegreeChanged()
	{
	}
	
	protected boolean isManualScrollTo = false;
	int ScrollDestPage = 0;
	int ScrollstartPage = 0;
	int scrollDire = 1;
	// float scrollxScale = 0;
	int scrollCurPage = 0;
	
	// teapotXu add start for Folder in Mainmenu
	public boolean NPage_IsManualScrollTo()
	{
		return isManualScrollTo;
	}
	
	// teapotXu add end for Folder in Mainmenu
	public boolean scrollTo(
			int destPage )
	{
		// scrollxScale = 0;
		// Log.d("launcher", "eee NPAGE scrollTo:"+destPage);
		if( destPage < 0 || destPage > view_list.size() - 1 )
			return false;
		ScrollDestPage = destPage;
		ScrollstartPage = page_index;
		if( page_index == destPage )
			return false;
		indicatorView.setAlpha( 1.0f );
		if( indicatorView.getIndicatorTween() != null && !indicatorView.getIndicatorTween().isFinished() )
		{
			indicatorView.getIndicatorTween().free();
			indicatorView.setIndicatorTween( null );
		}
		if( page_index > destPage )
		{
			scrollDire = -1;
			xScale = 0.0001f;
		}
		else
		{
			scrollDire = 1;
			xScale = -0.0001f;
		}
		// Log.d("launcher",
		// "currentPage scrollTo isManualScrollTo="+isManualScrollTo);
		isManualScrollTo = true;
		stopAutoEffect();
		startAutoEffectMini();
		// indicatorView.stopTween();
		// indicatorView.color.a = 1;
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 0.0f, 0, 0).delay(1f);
		return true;
	}
	
	// ViewGroup3D lastCurView = null;
	private void updateEffectMini()
	{
		ViewGroup3D next_view;
		int type = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		if( view_list.size() == 0 )
			return;
		int cur_page = ScrollstartPage;
		// Log.v("npg",
		// "xScale="+xScale+" ScrollstartPage="+ScrollstartPage+" cur_page="+cur_page);
		if( cur_page < 0 || cur_page >= view_list.size() )
			return;
		if( !moving )
		{
			initView();
			moving = true;
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					for( int j = 0 ; j < ( (GridView3D)i ).getChildCount() ; j++ )
					{
						View3D icon = ( (GridView3D)i ).getChildAt( j );
						icon.setTag( new Vector2( icon.getX() , icon.getY() ) );
					}
				}
			}
		}
		float tempxScale = xScale - (int)xScale;
		// scrollxScale = xScale;
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( cur_page );
		// if (lastCurView != null && lastCurView != cur_view)
		// lastCurView.hide();
		int next_page = ScrollDestPage;
		if( next_page >= 0 && next_page < view_list.size() )
			next_view = (ViewGroup3D)view_list.get( next_page );
		else
			next_view = cur_view;
		// if(this.random==false && this.mType==0)
		// {
		// APageEase.setStandard(true);
		// }
		// else
		// {
		// APageEase.setStandard(false);
		// }
		// Log.d("launcher", "tempxScale="+tempxScale);
		if( tempxScale > 0 )
			APageEase.updateEffect( next_view , cur_view , tempxScale - 1 , 0 , type );
		else if( tempxScale < 0 )
			APageEase.updateEffect( cur_view , next_view , tempxScale , 0 , type );
		// lastCurView = cur_view;
		// scrollCurPage = cur_page;
		// Log.d("launcher", "cur,next="+cur_page+","+next_page);
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			if( i != cur_page && i != next_page )
				view_list.get( i ).hide();
			// Log.d("launcher", "i:"+view_list.get(i).visible);
		}
	}
	
	void startAutoEffectMini()
	{
		int totalOffset = 1;// (ScrollDestPage - ScrollstartPage) * scrollDire;
		float duration = DefaultLayout.page_tween_time;// + totalOffset * 1 / 8;
		mVelocityX = 1000f;
		if( xScale > 0 )
		{
			tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Cubic.OUT ).target( 1 * totalOffset , 0 ).setCallback( this );
		}
		else
		{
			tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Cubic.OUT ).target( -1 * totalOffset , 0 ).setCallback( this );
		}
		mVelocityX = 0;
		tween.start( View3DTweenAccessor.manager );
	}
	
	@Override
	public void setPosition(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( isManualScrollTo )
		{
			// Log.d("launcher", "setPosition 1");
			this.xScale = x;
			this.yScale = y;
			updateEffectMini();
			onDegreeChanged();
		}
		else
		{
			// Log.d("launcher", "setPosition 2");
			setDegree( x , y );
			updateEffect();
		}
	}
	
	public void startAutoEffect()
	{
		float duration = DefaultLayout.page_tween_time;
		// Log.d("launcher",
		// "currentPage startAutoEffect isManualScrollTo="+isManualScrollTo);
		TweenEquation easeEquation = Quint.OUT;
		// teapotXu_20130316 add start:
		if( DefaultLayout.external_applist_page_effect == true )
		{
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_ELASTICITY )
			{
				// 弹性特效 才使用如下的动画方式
				APageEase.setTouchUpAnimEffectStatus( true );
				if( xScale > 0 )
				{
					APageEase.saveDegreeInfoWhnTouchUp( xScale - 1 );
				}
				else
				{
					APageEase.saveDegreeInfoWhnTouchUp( xScale );
				}
				easeEquation = Bounce.OUT;
				duration = duration + 0.2f;
			}
		}
		// teapotXu_20130316: add end
		isManualScrollTo = false;
		if( xScale == 0 && mVelocityX == 0 )
			return;
		if( xScale + mVelocityX / 1000 > 0.5 )
		{
			// speed = 2.0f - (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			// teapotXu_20130316 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( 1 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 1 , 0 ).setCallback( this );
			}
			// tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			// .ease(Quint.OUT).target(1, 0).setCallback(this);
			// teapotXu_20130316 add end
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT , 0 , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		else if( xScale + mVelocityX / 1000 < -0.5 )
		{
			// speed = 2.0f + (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			// teapotXu_20130316 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( -1 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( -1 , 0 ).setCallback( this );
			}
			// tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			// .ease(Quint.OUT).target(-1, 0).setCallback(this);
			// teapotXu_20130316 add end
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT , Gdx.graphics.getWidth() , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		else
		{
			// speed = 0.5f + Math.abs((xScale + mVelocityX / 1000));
			// speed = speed > 2.0f ? 2.0f : speed;
			// teapotXu_20130316 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( 0 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 0 , 0 ).setCallback( this );
			}
			// tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			// .ease(Quint.OUT).target(0, 0).setCallback(this);
			// teapotXu_20130316 add end
		}
		android.util.Log.i( "onEvent" , "startAutoEffect" );
		mVelocityX = 0;
		tween.start( View3DTweenAccessor.manager );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( view_list.size() <= 1 )
			return super.fling( velocityX , velocityY );
		mVelocityX = velocityX;
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				last_x = x;
				last_y = y;
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		canScroll = true;
		// Log.d("launcher", "eee NPAGE onTouchDown:"+x);
		if( view_list.size() <= 1 )
			return super.onTouchDown( x , y , pointer );
		if( isManualScrollTo )
		{
			return true;
		}
		mVelocityX = 0;
		if( xScale > 0.5 )
		{
			page_index = preIndex();
			setDegree( xScale - 1f );
			changeEffect();
		}
		if( xScale < -0.5 )
		{
			page_index = nextIndex();
			setDegree( xScale + 1f );
			changeEffect();
		}
		stopAutoEffect();
		isManualScrollTo = false;
		if( indicatorView != null ) // wanghongjian add //enable_DefaultScene
			indicatorView.stopTween();
		boolean res = super.onTouchDown( x , y , pointer );
		if( pointer == 0 )
		{
			Log.i( "focus" , "npagebase" );
			requestFocus();
		}
		// Color c = indicatorView.getColor();
		// indicatorView.setColor(c.r, c.g, c.b, 0);
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.2f, 1.0f, 0, 0);
		return res;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		// TODO Auto-generated method stub
		// Log.d(
		// "test12345","NPageBase Math.abs(xScale)="+Math.abs(xScale)+" moving="+moving);
		if( Math.abs( xScale ) < 0.00005f )
		{
			if( moving )
				initView();
			return super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
		}
		else
			return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// Log.d("launcher", "eee NPAGE onClick:"+x);
		// TODO Auto-generated method stub
		if( Math.abs( xScale ) < 0.01f )
		{
			if( moving )
			{
				initView();
			}
			return super.onClick( x , y );
		}
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( Math.abs( xScale ) < 0.01f )
		{
			if( moving )
			{
				initView();
			}
			return super.onLongClick( x , y );
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT );
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT );
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING );
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		canScroll = false;
		// Log.d("launcher", "eee NPAGE onTouchUp:"+x);
		if( view_list.size() == 0 )
			return super.onTouchUp( x , y , pointer );
		if( isManualScrollTo && xScale != 0 )
		{
			return true;
		}
		if( view_list.size() == 1 )
			return super.onTouchUp( x , y , pointer );
		if( indicatorView != null ) // wanghongjian add //enable_DefaultScene
			indicatorView.stopTween();
		// Color c = indicatorView.getColor();
		// indicatorView.setColor(c.r, c.g, c.b, 1);
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 0.0f, 0, 0).delay(1f);
		startAutoEffect();
		releaseFocus();
		/* if workspace moving,not distribute TouchUp to children by zfshi */
		if( moving )
		{
			return false;
		}
		/* added by zfshi ended */
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( !canScroll )
			return false;
		// Log.d("launcher", "eee NPAGE scroll:"+x);
		if( view_list.size() <= 1 )
			return super.scroll( x , y , deltaX , deltaY );
		if( isManualScrollTo )
		{
			return true;
		}
		if( !moving && super.scroll( x , y , deltaX , deltaY ) )
		{
			// Log.d("launcher", "eee NPAGE scroll222:"+x);
			return true;
		}
		// setDegree(xScale - (-deltaX) / this.width);
		if( DefaultLayout.enable_DesktopIndicatorScroll && Root3D.scroll_indicator )
		{
			return false;
		}
		float yAmplify = deltaY * 1.3f;
		setDegree( xScale - ( -deltaX ) / this.width , yScale + ( -yAmplify ) / this.height );
		// teapotXu_20130319: add start
		if( DefaultLayout.external_applist_page_effect == true )
		{
			APageEase.setTouchUpAnimEffectStatus( false );
		}
		// teapotXu_20130319: add end
		updateEffect();
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( ( Math.abs( x - last_x ) > 10 ) || ( Math.abs( y - last_y ) > 10 ) )
				{
					updateParticle( ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING , x , y );
					last_x = x;
					last_y = y;
				}
				else
				{
					pauseParticle( ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING );
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		if( moving )
			return true;
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		super.draw( batch , parentAlpha );
		if( drawIndicator )
			if( indicatorView != null )
			{
				indicatorView.draw( batch , parentAlpha );
			}
		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect( batch );
		/************************ added by zhenNan.ye end *************************/
	}
	
	@Override
	public void show()
	{
		// TODO Auto-generated method stub
		initView();
		super.show();
	}
	
	@Override
	public void hide()
	{
		super.hide();
		this.stopAutoEffect();
		// setDegree(0f);
		setDegree( 0f , 0f );
		mVelocityX = 0;
		initView();
	}
	
	public int getPageNum()
	{
		return view_list.size();
	}
	
	public float getTotalOffset()
	{
		float ret = 0;
		int totalPage = getPageNum();
		// int tmp = (totalPage-1);
		// Log.v("jbc", "getTotalOffset xScale="+xScale+", ret="+ret);
		if( isManualScrollTo )
		{
			int destPage = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D().getHomePage();
			if( CellLayout3D.keyPadInvoked && DefaultLayout.keypad_event_of_focus )
			{
				destPage = CellLayout3D.nextPageIndex;
			}
			if( page_index != destPage )
				ret = 1.0f / ( totalPage - 1 ) * ( page_index * ( 1 - Math.abs( xScale ) ) + destPage * Math.abs( xScale ) );
		}
		else
		{
			if( page_index == 0 && xScale > 0 )
			{
				ret = ( ( (float)totalPage - 1 ) / ( totalPage - 1 ) ) * xScale;
			}
			else if( page_index == totalPage - 1 && xScale < 0 )
			{
				ret = ( (float)totalPage - 1 ) / ( totalPage - 1 ) + ( ( (float)totalPage - 1 ) / ( totalPage - 1 ) ) * xScale;
			}
			else
				ret = ( page_index - xScale ) / ( ( totalPage - 1 ) );
		}
		//		if(FeaturePanel.currScreen!=page_index){
		//		    if(FeaturePanel.StepCount<1){
		//		        FeaturePanel.StepCount++;
		//		    }
		//		    else{
		//		        FeaturePanel.StepCount=0;
		//		        FeaturePanel.currScreen=page_index;
		//		        Root3D.getSceenDrawable();
		//		    }
		//		}
		// Log.v("jbc", "getTotalOffset isManualScrollTo="+isManualScrollTo);
		// Log.v("jbc", "getTotalOffset ret="+ret);
		if( ret < 0 )
		{
			ret = 0;
		}
		else if( ret > ( totalPage - 1.0f ) / ( totalPage - 1 ) )
		{
			ret = ( totalPage - 1.0f ) / ( totalPage - 1 );
		}
		return ret;
	}
	
	public ArrayList<View3D> getViewList()
	{
		return this.view_list;
	}
	
	public View3D getCurrentView()
	{
		if( view_list.size() <= page_index )
			return null;
		return view_list.get( page_index );
	}
	
	public int getCurrentPage()
	{
		return page_index;
	}
	
	public void setCurrentPage(
			int index )
	{
		if( index < 0 )
			index = 0;
		else if( index >= view_list.size() )
			index = view_list.size() - 1;
		page_index = index;
		// setDegree(0f);
		initView();
		setDegree( 0f , 0f );
	}
	
	public void addScrollListener(
			PageScrollListener l )
	{
		scrollListeners.add( l );
	}
	
	// xiatian add start //Widget adaptation "com.android.gallery3d"
	public void removeScrollListener(
			PageScrollListener l )
	{
		if( scrollListeners.contains( l ) )
			scrollListeners.remove( l );
	}
	
	// xiatian add end
	protected void finishAutoEffect()
	{
		// ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
		// ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
		// ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());
		// initData(cur_view);
		// initData(pre_view);
		// initData(next_view);
		//  Root3D.getSceenDrawable();
		// teapotXu_20130325 add start : for effect
		if( DefaultLayout.external_applist_page_effect == true )
		{
			ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
			ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex() );
			ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex() );
			if( cur_view.getChildrenDrawOrder() == true )
			{
				cur_view.setChildrenDrawOrder( false );
			}
			if( pre_view.getChildrenDrawOrder() == true )
			{
				pre_view.setChildrenDrawOrder( false );
			}
			if( next_view.getChildrenDrawOrder() == true )
			{
				next_view.setChildrenDrawOrder( false );
			}
		}
		// teapotXu_20130325 add end : for effect
	}
	
	public void recoverPageSequence()
	{
		for( int i = 0 ; i < view_list.size() && i < children.size() ; i++ )
		{
			children.set( i , view_list.get( i ) );
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( type == TweenCallback.COMPLETE && source == tween )
		{
			if( isManualScrollTo )
			{
				setCurrentPage( ScrollDestPage );
				isManualScrollTo = false;
			}
			else
			{
				if( xScale <= -1f )
				{
					setCurrentPage( nextIndex() );
				}
				if( xScale >= 1f )
				{
					setCurrentPage( preIndex() );
				}
			}
			initView();
			tween = null;
			finishAutoEffect();
			recoverPageSequence();
		}
		super.onEvent( type , source );
	}
	
	/************************ added by zhenNan.ye begin *************************/
	private void drawParticleEffect(
			SpriteBatch batch )
	{
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				drawParticle( batch );
			}
		}
	}
	
	/************************ added by zhenNan.ye end ***************************/
	public class IndicatorView extends View3D
	{
		
		// NinePatch focus, unfocus;
		private TextureRegion selectedIndicator = null;
		private TextureRegion selectedIndicator1 = null;
		private TextureRegion selectedIndicator2 = null;
		private TextureRegion selectedIndicator_bg = null;
		public float indicatorSize = (float)R3D.page_indicator_size;
		public float indicatorFocusW = R3D.page_indicator_focus_w;
		public final int indicatorNormalW = R3D.page_indicator_normal_w;
		public final int indicatorStyle = R3D.page_indicator_style;
		public final int indicatorTotalSize = R3D.page_indicator_total_size;
		public static final int INDICATOR_STYLE_ANDROID4 = 0;
		public static final int INDICATOR_STYLE_S3 = 1;
		public static final int INDICATOR_STYLE_S2 = 2;
		// xiatian add start //add new page_indicator_style
		public static final int INDICATOR_STYLE_COCO_AND_ANDROID4 = 3;
		public float indicatorSizeXian = (float)R3D.page_indicator_size;
		private Tween myTween;
		private Tween indicatorTween;
		private float indicatorAlpha = 0;
		
		public IndicatorView(
				String name )
		{
			super( name );
			if( selectedIndicator == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current2.png" );
				Texture t = new BitmapTexture( bmIndicator );
				selectedIndicator = new TextureRegion( t );
				Bitmap bmIndicator2 = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current1.png" );
				Texture t2 = new BitmapTexture( bmIndicator2 );
				selectedIndicator1 = new TextureRegion( t2 );
				Bitmap bmIndicator3 = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current3.png" );
				Texture t3 = new BitmapTexture( bmIndicator3 );
				selectedIndicator2 = new TextureRegion( t3 );
				Bitmap bmIndicator1 = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_bg.png" );
				Texture t1 = new BitmapTexture( bmIndicator1 );
				selectedIndicator_bg = new TextureRegion( t1 );
				if( !iLoongLauncher.releaseTexture )
					bmIndicator.recycle();
			}
			setSize( Utils3D.getScreenWidth() , indicatorFocusH * 1.1f );
			setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , +NPageBase.this.height - indicatorFocusH );
		}
		
		public IndicatorView(
				String name ,
				int width )
		{
			this( name );
			setSize( width , indicatorFocusH * 1.1f );
		}
		
		public void pariticle_handle(
				float xScale )
		{
			if( !visible )
			{
				return;
			}
			anim_time = ANIM_TIME;
			particle_handle();
			// print_particle();
			// Log.d("particle_handle","particle_handle xScale="+xScale);
		}
		
		public void show()
		{
			super.show();
			indicatorAlpha = 1;
			stopTween();
			Log.v( "IndicatorView" , "TweenStart show" );
			myTween = startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.4f , 1 , 0 , 0 ).setCallback( this );
		}
		
		public void showEx()
		{
			super.show();
			indicatorAlpha = 1;
			stopTween();
			Log.v( "IndicatorView" , "TweenStart show" );
			myTween = startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.0f , 1 , 0 , 0 ).setCallback( this );
		}
		
		@Override
		public void setUser(
				float value )
		{
			// TODO Auto-generated method stub
			indicatorAlpha = value;
		}
		
		@Override
		public float getUser()
		{
			// TODO Auto-generated method stub
			return indicatorAlpha;
		}
		
		public void setAlpha(
				float value )
		{
			indicatorAlpha = value;
		}
		
		public void setIndicatorTween(
				Tween tween )
		{
			indicatorTween = tween;
		}
		
		public Tween getIndicatorTween()
		{
			return indicatorTween;
		}
		
		public void finishAutoEffect()
		{
			// TODO Auto-generated method stub
			indicatorAlpha = 1;
			if( indicatorTween != null && !indicatorTween.isFinished() )
			{
				indicatorTween.free();
				indicatorTween = null;
			}
			indicatorTween = startTween( View3DTweenAccessor.USER , Cubic.OUT , INDICATOR_FADE_TWEEN_DURATION , 0 , 0 , 0 ).setCallback( this );
		}
		
		@Override
		public void onEvent(
				int type ,
				BaseTween source )
		{
			// TODO Auto-generated method stub
			if( type == TweenCallback.COMPLETE && source == myTween )
			{
				myTween = null;
				this.color.a = 1;
				super.show();
				finishAutoEffect();
				return;
			}
			if( type == TweenCallback.COMPLETE && source == indicatorTween )
			{
				indicatorTween = null;
				indicatorAlpha = 0;
				return;
			}
		}
		
		long drawTime = 0;
		public static final int BEI = 1000;
		public final int indicatorFocusH = R3D.page_indicator_focus_w / 4;
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			if( !visible )
			{
				return;
			}
			if( getIndicatorPageCount() <= 1 )
				return;
			batch.setColor( color.r , color.g , color.b , color.a );
			Color old = batch.getColor();
			float oldA = old.a;
			if( selectedIndicator != null && anim_time > 0 )
			{
				// int srcBlendFunc = batch.getSrcBlendFunc();
				// int dstBlendFunc = batch.getDstBlendFunc();
				// batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
				for( int i = 0 ; i < particlelist.size() ; i++ )
				{
					particlelist.get( i ).draw( batch , old );
				}
				// batch.setBlendFunction(srcBlendFunc, dstBlendFunc);
				old.a = oldA;
				old.a *= 1.0f * anim_time / ANIM_TIME;
				batch.setColor( old.r , old.g , old.b , old.a );
				PointF[] location = getLocation( xScale );
				Log.d( "draw" , "particlelist.size()=" + particlelist.size() );
				if( location != null )
					for( int i = 0 ; i < location.length ; i++ )
					{
						float indicatorFocusW1 = indicatorFocusW;
						float indicatorFocusW2 = 0;
						if( location[i].x < x - 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
						{
							indicatorFocusW2 = (float)( x - 10 * (float)Utils3D.getScreenWidth() / 720 - location[i].x );
						}
						if( indicatorFocusW2 > indicatorFocusW - indicatorFocusH * 2 )
						{
							continue;
						}
						if( location[i].x + indicatorFocusW1 > x + width + 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
						{
							indicatorFocusW1 = (float)( x + width - location[i].x + 10 * (float)Utils3D.getScreenWidth() / 720 );
						}
						if( indicatorFocusW1 < indicatorFocusH * 2 )
						{
							continue;
						}
						batch.draw( selectedIndicator1 , location[i].x + indicatorFocusW2 , location[i].y , indicatorFocusH , indicatorFocusH );
						batch.draw(
								selectedIndicator ,
								location[i].x + indicatorFocusH + indicatorFocusW2 ,
								location[i].y ,
								indicatorFocusW1 - indicatorFocusH * 2 - indicatorFocusW2 ,
								indicatorFocusH );
						batch.draw( selectedIndicator2 , location[i].x + indicatorFocusW1 - indicatorFocusH , location[i].y , indicatorFocusH , indicatorFocusH );
					}
				long tick = ( System.currentTimeMillis() - drawTime ) * BEI;
				// if(tick>50)
				// tick=5;
				anim_time -= tick;
				List<Particle> particlelists = new ArrayList<Particle>();
				for( Particle p : particlelist )
				{
					p.time -= tick;
					if( p.time <= 0 || p.time > p.TIME )
					{
						particlelists.add( p );
					}
				}
				if( particlelists.size() > 0 )
					particlelist.removeAll( particlelists );
				Gdx.graphics.requestRendering();
			}
			else
			{
				particlelist.retainAll( particlelist );
			}
			drawTime = System.currentTimeMillis();
			old.a = oldA;
			batch.setColor( old.r , old.g , old.b , old.a );
		}
		
		int anim_time = 0;
		public List<Particle> particlelist = Collections.synchronizedList( new ArrayList<Particle>() );
		
		public void addParticle(
				Particle pi )
		{
			if( particlelist.size() <= 2000 )
			{
				particlelist.add( pi );
				Log.d( "addParticle" , "pi.location.x  particlelist.size()" + pi.location.x + "," + particlelist.size() );
			}
		}
		
		class Particle
		{
			
			PointF location = null;
			public final static int Time = 400 * BEI;
			long time = Time;
			long TIME = Time;
			
			public void draw(
					SpriteBatch batch ,
					Color old )
			{
				if( location.x + indicatorFocusW > x + width + 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
				{
					return;
				}
				if( location.x < x - 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
				{
					return;
				}
				if( time > 0 )
				{
					float oldA = old.a;
					// if(time>=(int)TIME*0.5)
					// {
					// old.a *= 1.0f*(0.8+0.2*(time-(int)TIME*0.5)/(TIME*0.5));
					// }else{
					// old.a *= 1.0f*(0.8*(time)/(TIME*0.5));
					// }
					old.a *= 0.2f * time / (float)( TIME );
					// old.a *=0.30f;
					batch.setColor( old.r , old.g , old.b , old.a );
					batch.draw( selectedIndicator_bg , location.x , location.y , indicatorFocusW , indicatorFocusH );
					old.a = oldA;
					batch.setColor( old.r , old.g , old.b , old.a );
					// Log.d("Particle","draw ,time,old.a="+time+","+old.a);
				}
			}
		}
		
		private void particle_handle()
		{
			PointF[] ps = getLocation( xScale );
			if( ps != null && Math.abs( xScale ) != 1.0f )
			{
				for( int j = 0 ; j < ps.length ; j++ )
				{
					Particle pi = new Particle();
					pi.location = ps[j];
					addParticle( pi );
					// Log.d("particle_handle","particle_handle pi.location.x="+pi.location.x);
				}
				// if(ps.length==2)
				// {
				// Log.d("particle_handle","particle_handle length==2 ");
				// }
			}
		}
		
		public void print_particle()
		{
			try
			{
				Log.d( "print_particle" , "particlelist.size()=" + particlelist.size() );
				for( int i = 0 ; i < particlelist.size() ; i++ )
				{
					Log.d( "print_particle" , "particlelist[" + i + "]  x=" + particlelist.get( i ).location.x + ",y=" + particlelist.get( i ).location.y + ",time=" + particlelist.get( i ).time );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		
		float old_degree = 0;
		private final int ANIM_TIME = 4000 * BEI;
		
		public PointF[] getLocation(
				float degree )
		{
			PointF[] location = null;
			int pageNum = getIndicatorPageCount();
			int size = getIndicatorPageCount();
			if( size == 0 )
				return location;
			indicatorFocusW = (float)( width / size );
			indicatorSize = indicatorFocusW;
			int currentPage = getIndicatorPageIndex();
			float focusWidth = width;
			int nextPage = getIndicatorPageIndex();
			int signDirection = degree >= 0 ? 1 : -1;
			if( degree == 0 )
				nextPage = currentPage;
			else if( degree == 1 )
				nextPage = currentPage == 0 ? size - 1 : currentPage - 1;
			else if( degree == -1 )
				nextPage = currentPage == size - 1 ? 0 : currentPage + 1;
			else if( currentPage == 0 && degree > 0 )
				nextPage = size - 1;
			else if( currentPage == size - 1 && degree < 0 )
				nextPage = 0;
			else
				nextPage = currentPage - (int)( degree + 1.0 * signDirection );
			// Log.d("getLocation","nextPage,currentPage,degree"+nextPage+","+currentPage+","+degree);
			float focusH , focusY;
			float startX = this.x + ( this.width - focusWidth ) / 2.0f;
			focusH = indicatorFocusH;
			focusY = this.y + ( this.height - focusH ) / 2.0f;
			float focus_offset_x = ( indicatorSize - indicatorFocusW ) / 2.0f;
			if( currentPage == pageNum - 1 && nextPage == 0 )
			{
				location = new PointF[2];
				location[0] = new PointF();
				location[0].x = startX + ( -degree - 1 ) * indicatorSize + focus_offset_x;
				location[0].y = focusY;
				location[1] = new PointF();
				location[1].x = startX + ( currentPage - degree ) * indicatorSize + focus_offset_x;
				location[1].y = focusY;
			}
			else if( currentPage == 0 && nextPage == pageNum - 1 )
			{
				location = new PointF[2];
				location[0] = new PointF();
				location[0].x = startX + ( -degree ) * indicatorSize + focus_offset_x;
				location[0].y = focusY;
				location[1] = new PointF();
				location[1].x = startX + ( nextPage + 1 - degree ) * indicatorSize + focus_offset_x;
				location[1].y = focusY;
			}
			else
			{
				if( nextPage > currentPage )
				{
					location = new PointF[1];
					location[0] = new PointF();
					location[0].x = startX + ( nextPage - 1 - degree ) * indicatorSize + focus_offset_x;
					location[0].y = focusY;
				}
				else if( nextPage == currentPage )
				{
					location = new PointF[1];
					location[0] = new PointF();
					location[0].x = startX + nextPage * indicatorSize + focus_offset_x;
					location[0].y = focusY;
				}
				else
				{
					location = new PointF[1];
					location[0] = new PointF();
					location[0].x = startX + ( nextPage + 1 - degree ) * indicatorSize + focus_offset_x;
					location[0].y = focusY;
				}
			}
			return location;
		}
	}
}
