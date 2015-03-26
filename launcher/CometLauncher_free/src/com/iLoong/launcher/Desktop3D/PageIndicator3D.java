package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PageIndicator3D extends View3D implements PageScrollListener , ClingManager.ClingTarget
{
	
	private int currentPage = -1;
	private int pageNum;
	private float degree;
	private Tween myTween;
	private Tween s4indicatorClickTween;
	private static final int hide_indicator = 0;
	private static final int show_indicator = 1;
	private Tween indicatorTween;
	private float indicatorAlpha = 0;
	private static float INDICATOR_FADE_TWEEN_DURATION = 1.0f;
	private TextureRegion selectedIndicator = null;
	private TextureRegion selectedIndicator1 = null;
	private TextureRegion selectedIndicator2 = null;
	private TextureRegion selectedIndicator_bg = null;
	public int clingR;
	public int clingX;
	public int clingY;
	private long downTime = 0;
	private float downX = 0;
	private float downY = 0;
	private int clingState = ClingManager.CLING_STATE_WAIT;
	public int pageMode = MODE_NORMAL;
	public static boolean animating = false;
	public static final float NORMAL_SCALE = 0.6f;
	public static final float ACTIVATE_SCALE = 0.75f;
	public static final int CLICK_TIME = 500;
	public static final int CLICK_MOVE = 40;
	public static final int PAGE_INDICATOR_CLICK = 0;
	public static final int PAGE_INDICATOR_UP = 1;
	public static final int PAGE_INDICATOR_DROP_OVER = 2;
	public static final int PAGE_INDICATOR_SCROLL = 3;
	public static final int MODE_ACTIVATE = 0;
	public static final int MODE_NORMAL = 1;
	public static final int PAGE_MODE_EDIT = 2;
	public float indicatorSize = (float)R3D.page_indicator_size;
	public int indicatorFocusW = R3D.page_indicator_focus_w * 10;
	public final int indicatorFocusH = R3D.page_indicator_focus_w / 4;
	public final int indicatorNormalW = R3D.page_indicator_normal_w;
	public int indicatorStyle = R3D.page_indicator_style;
	public final int indicatorTotalSize = R3D.page_indicator_total_size;
	public final int s4_page_indicator_bg_height = R3D.s4_page_indicator_bg_height;
	public final int s4_page_indicator_scroll_width = R3D.s4_page_indicator_scroll_width;
	public final int s4_page_indicator_scroll_height = R3D.s4_page_indicator_scroll_height;
	public final int s4_page_indicator_number_bg_size = R3D.s4_page_indicator_number_bg_size;
	public static float scroll_degree = 0;
	public static final int INDICATOR_STYLE_ANDROID4 = 0;
	
	public PageIndicator3D(
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
		setSize( Utils3D.getScreenWidth() , indicatorFocusH );
		setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , Utils3D.getScreenHeight() - indicatorFocusH );
		ClingManager.getInstance().firePageIndicatorCling( this );
		indicatorAlpha = 0;
		clingX = Utils3D.getScreenWidth() / 2;
		clingY = (int)( y + height / 2 );
		clingR = (int)( indicatorSize * 2 );
		//		iLoongLauncher.getInstance().postRunnable(runnable)
	}
	
	long drawTime = 0;
	public static final int BEI = 1000;
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( !Desktop3DListener.bSetHomepageDone )
			return;
		batch.setColor( color.r , color.g , color.b , color.a );
		Color old = batch.getColor();
		float oldA = old.a;
		if( selectedIndicator != null && anim_time > 0 )
		{
			//				int srcBlendFunc = batch.getSrcBlendFunc();
			//				int dstBlendFunc = batch.getDstBlendFunc();
			//				batch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);	
			for( int i = 0 ; i < particlelist.size() ; i++ )
			{
				particlelist.get( i ).draw( batch , old );
			}
			//				batch.setBlendFunction(srcBlendFunc, dstBlendFunc);
			old.a = oldA;
			old.a *= 1.0f * anim_time / ANIM_TIME;
			batch.setColor( old.r , old.g , old.b , old.a );
			PointF[] location = getLocation();
			Log.d( "draw" , "particlelist.size()=" + particlelist.size() );
			for( int i = 0 ; i < location.length ; i++ )
			{
				batch.draw( selectedIndicator1 , location[i].x , location[i].y , indicatorFocusH , indicatorFocusH );
				batch.draw( selectedIndicator , location[i].x + indicatorFocusH , location[i].y , indicatorFocusW - indicatorFocusH * 2 , indicatorFocusH );
				batch.draw( selectedIndicator2 , location[i].x + indicatorFocusW - indicatorFocusH , location[i].y , indicatorFocusH , indicatorFocusH );
			}
			long tick = ( System.currentTimeMillis() - drawTime ) * BEI;
			//				if(tick>50)
			//					tick=5;
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
		drawTime = System.currentTimeMillis();
		old.a = oldA;
		batch.setColor( old.r , old.g , old.b , old.a );
		if( clingState == ClingManager.CLING_STATE_WAIT )
		{
			clingState = ClingManager.getInstance().firePageIndicatorCling( this );
		}
	}
	
	int anim_time = 0;
	public List<Particle> particlelist = Collections.synchronizedList( new ArrayList<Particle>() );
	
	public void addParticle(
			Particle pi )
	{
		if( particlelist.size() <= 2000 )
		{
			particlelist.add( pi );
			//		Log.d("addParticle","pi.location.x  particlelist.size()"+pi.location.x+","+particlelist.size());
		}
	}
	
	class Particle
	{
		
		PointF location = null;
		public final static int Time = 400 * BEI;
		int time = Time;
		int TIME = Time;
		
		public void draw(
				SpriteBatch batch ,
				Color old )
		{
			if( time > 0 )
			{
				float oldA = old.a;
				//			if(time>=(int)TIME*0.5)
				//			{
				//				old.a *= 1.0f*(0.8+0.2*(time-(int)TIME*0.5)/(TIME*0.5));
				//			}else{
				//				old.a *= 1.0f*(0.8*(time)/(TIME*0.5));
				//			}
				old.a *= 0.25f * time / (float)( TIME );
				batch.setColor( old.r , old.g , old.b , old.a );
				//			batch.draw(selectedIndicator, location.x, location.y, indicatorFocusW, indicatorFocusH);
				batch.draw( selectedIndicator_bg , location.x , location.y , indicatorFocusW , indicatorFocusH );
				old.a = oldA;
				batch.setColor( old.r , old.g , old.b , old.a );
				//Log.d("Particle","draw ,time,old.a="+time+","+old.a);
			}
		}
	}
	
	private void particle_handle()
	{
		PointF[] ps = getLocation();
		for( int j = 0 ; j < ps.length ; j++ )
		{
			Particle pi = new Particle();
			pi.location = ps[j];
			addParticle( pi );
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
	
	@Override
	public void pageScroll(
			float degree ,
			int index ,
			int count )
	{
		indicatorAlpha = 1;
		if( indicatorTween != null && !indicatorTween.isFinished() )
		{
			indicatorTween.free();
			indicatorTween = null;
		}
		//Log.d("pageScroll", "degree="+degree);
		currentPage = index;
		this.degree = degree;
		pageNum = count;
		anim_time = ANIM_TIME;
		if( Math.abs( degree - old_degree ) > 0.01f )
		{
			particle_handle();
		}
		old_degree = degree;
		//print_particle();
	}
	
	public PointF[] getLocation()
	{
		PointF[] location = null;
		int size = pageNum;
		indicatorFocusW = Utils3D.getScreenWidth() / size;
		indicatorSize = indicatorFocusW;
		float focusWidth = Utils3D.getScreenWidth();
		int nextPage = currentPage;
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
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		//Log.v("jbc", "eee scroll degree="+degree);
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		if( Math.abs( x - downX ) > CLICK_MOVE || Math.abs( y - downY ) > CLICK_MOVE )
		{
			downTime = 0;
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer != 0 )
			return true;
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( degree != 0 || !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		downTime = System.currentTimeMillis();
		downX = x;
		downY = y;
		ClingManager.getInstance().cancelPageIndicatorCling();
		return true;
	}
	
	//
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer != 0 )
			return true;
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		if( System.currentTimeMillis() - downTime < CLICK_TIME && Math.abs( x - downX ) < CLICK_MOVE && Math.abs( y - downY ) < CLICK_MOVE )
			return viewParent.onCtrlEvent( this , PAGE_INDICATOR_CLICK );
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( degree != 0 || !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		downTime = 0;
		viewParent.onCtrlEvent( this , PAGE_INDICATOR_SCROLL );
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( degree != 0 || !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		return true;
	}
	
	@Override
	public int getIndex()
	{
		// TODO Auto-generated method stub
		return currentPage;
	}
	
	public void setPageNum(
			int num )
	{
		pageNum = num;
	}
	
	public void show()
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		indicatorAlpha = 1;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart show" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.4f , 1 , 0 , 0 ).setUserData( show_indicator ).setCallback( this );
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	public void showEx()
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		indicatorAlpha = 1;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart show" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.0f , 1 , 0 , 0 ).setUserData( show_indicator ).setCallback( this );
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	public void hide()
	{
		if( this.touchable == false )
		{
			return;
		}
		this.touchable = false;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart hide" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.4f , 0 , 0 , 0 ).setUserData( hide_indicator ).setCallback( this );
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	public void hideEx()
	{
		if( this.touchable == false )
		{
			return;
		}
		this.touchable = false;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart hide" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.0f , 0 , 0 , 0 ).setUserData( hide_indicator ).setCallback( this );
		//Log.v("indicator","myTween hide_indicator:" + myTween);
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	public void hideNoAnim()
	{
		this.touchable = false;
		this.visible = false;
		if( clingState == ClingManager.CLING_STATE_SHOW )
		{
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	@Override
	public void setCurrentPage(
			int current )
	{
		if( currentPage == -1 )
		{
			currentPage = current;
		}
		currentPage = current;
	}
	
	public void setDegree(
			float degree )
	{
		this.degree = degree;
	}
	
	@Override
	public boolean visible()
	{
		return this.isVisible() && color.a != 0 && touchable;
	}
	
	@Override
	public int getClingPriority()
	{
		return ClingManager.PAGEINDICATOR_CLING;
	}
	
	@Override
	public void dismissCling()
	{
		clingState = ClingManager.CLING_STATE_DISMISSED;
	}
	
	@Override
	public void setPriority(
			int priority )
	{
		// TODO Auto-generated method stub
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
	
	@Override
	public void setUser2(
			float value )
	{
		super.setUser2( value );
		setCurrentPage( (int)( value + 0.5 ) );
		Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
		workspace.setCurrentPage( (int)( value + 0.5 ) );
		float temp_degree = value - (int)value;
		if( temp_degree < 0.5 )
		{
			setDegree( -temp_degree );
			workspace.setDegreeOnly( -temp_degree );
		}
		else
		{
			setDegree( 1 - temp_degree );
			workspace.setDegreeOnly( 1 - temp_degree );
		}
		workspace.updateEffect( value );
	}
	
	public void finishAutoEffect()
	{
		indicatorAlpha = 1;
		if( indicatorTween != null && !indicatorTween.isFinished() )
		{
			indicatorTween.free();
			indicatorTween = null;
		}
		indicatorTween = startTween( View3DTweenAccessor.USER , Cubic.OUT , INDICATOR_FADE_TWEEN_DURATION , 0 , 0 , 0 ).setCallback( this );
		//Log.v("indicator","indicatorTween:" + indicatorTween);
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( type == TweenCallback.COMPLETE && source == myTween )
		{
			//Log.v("indicator","source:" + source);
			myTween = null;
			int animKind = (Integer)( source.getUserData() );
			if( animKind == hide_indicator )
			{
				Log.v( "PageIndicator3D" , "TweenComplete hide" );
				this.color.a = 0;
				if( !touchable )
					super.hide();
			}
			else if( animKind == show_indicator )
			{
				Log.v( "PageIndicator3D" , "TweenComplete show" );
				this.color.a = 1;
				super.show();
				finishAutoEffect();
			}
			return;
		}
		if( type == TweenCallback.COMPLETE && source == indicatorTween )
		{
			indicatorTween = null;
			indicatorAlpha = 0;
			return;
		}
		if( type == TweenCallback.COMPLETE && source == s4indicatorClickTween )
		{
			s4indicatorClickTween = null;
			Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			workspace.recoverPageSequence();
			this.releaseFocus();
			SendMsgToAndroid.sendShowWorkspaceMsgEx();
			return;
		}
	}
	
	public float getTempDegree(
			float touch_x ,
			int indicator_space )
	{
		int screenWidth = Utils3D.getScreenWidth();
		int total_w = indicator_space * ( pageNum - 1 );
		float start_x = ( screenWidth - total_w ) / 2;
		float degree = (float)( touch_x - start_x ) / indicator_space;
		if( degree < 0 )
			return 0;
		if( degree > pageNum - 1 )
			return pageNum - 1;
		return degree;
	}
	
	public float getScrollDegree()
	{
		return scroll_degree;
	}
}
