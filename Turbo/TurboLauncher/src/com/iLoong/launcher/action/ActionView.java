package com.iLoong.launcher.action;


import java.util.ArrayList;

import android.graphics.Bitmap;
import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Cache;
import com.iLoong.launcher.Widget3D.CacheManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public abstract class ActionView extends ViewGroup3D implements IActionNext
{
	
	public String TAG="ActionView";

	public static String ACTION_CACHE_MANAGER_LABEL = "ACTION_CACHE_MANAGER_LABEL";
	public static final int EVENT_DO_NEXT=0;
	public static final int EVENT_DO_HIDE=1;
	public static final int EVENT_DO_DESTORY=2;
	public Root3D root;
	public Timeline mMainTimeline = null;
	public Timeline mSlaveTimeLine = null;
	public final int TIMELINE_TYPE_MAIN = 0;
	public final int TIMELINE_TYPE_SLAVE = 1;
	protected ActionTitleUtil actionUtil = new ActionTitleUtil();
	private View3D mFocusView;
	protected float mScale = 1;
	ActionData mActionData;
	public ActionView(
			String name )
	{
		super( name );
		this.setPosition( 0 , 0 );
		this.setSize( Utils3D.getScreenWidth() ,Utils3D.getScreenHeight() );
		
		attach();
	}
	
	
	public void attach()
	{
		setData();
		measureSize();
		initView();
		initEvent();
		//	onAnimStarted();
	}
	
	public void setFocusView(
			View3D focus )
	{
		mFocusView = focus;
	}
	
	public View3D getFocusView()
	{
		return mFocusView;
	}
	
	public void measureSize()
	{
		mScale = this.width / 720.0f;
	}
	
	public float fixPosX(float v){
		return v*mScale;
	}
	
	public float fixPosY(float y){
		return Utils3D.getScreenHeight()-y*mScale;
	}
	
	public float fixPosY(float y,View3D v){
		return fixPosY( y)-v.height;
	}
	
	public Timeline getParTimeline()
	{
		stopTween();
		mMainTimeline = Timeline.createParallel();
		return mMainTimeline;
	}
	
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( isPointIn( x , y ) )
			return super.onTouchDown( x , y , pointer );
		else
			return true;
	}
	
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( isPointIn( x , y ) )
			return super.onTouchUp( x , y , pointer );
		else
			return true;
	}
	
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( isPointIn( x , y ) )
			return super.scroll( x , y , deltaX , deltaY );
		else
			return true;
	}
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		//Log.v("click", " onLongClick:" + name + " x:" + x + " y:" + y);
		return true;
	}
	public boolean onClick(
			float x ,
			float y )
	{
		if( isPointIn( x , y ) )
			return super.onClick( x , y );
		else
			return true;
	}
	
	public void startTween()
	{
		if( mMainTimeline != null )
			mMainTimeline.start( View3DTweenAccessor.manager ).setUserData( TIMELINE_TYPE_MAIN ).setCallback( this );
	}
	
	public void stopTween(){
		if( mMainTimeline != null && !mMainTimeline.isFinished() )
		{
			mMainTimeline.free();
			mMainTimeline = null;
		}
	}
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		if(mActionData.isShowWorkspaceFront()&&isShowFront()){
			if( ActionData.bg == null )
			{
				Bitmap blackBmp = ThemeManager.getInstance().getBitmap( "theme/desktopAction/black.png" );
				ActionData.bg = new NinePatch( new TextureRegion( new BitmapTexture(blackBmp ) ) , 2 , 2 , 2 , 2 );
				blackBmp.recycle();
			}
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			
	//		ActionData.bg.draw( batch , 0 , iLoongLauncher.getInstance().d3dListener.root.getHotSeatBar().height , this.width , this.height );
		
			ActionData.bg.draw( batch , 0 ,0 , this.width , mActionData.getFrontPosition()[0]);
//			
			ActionData.bg.draw( batch , 0 , mActionData.getFrontPosition()[1] , this.width , this.height );
//			
		}
	}
	
	public void attachView(
			View3D v )
	{
		if( root == null )
		{
			throw new RuntimeException( "uninitialize root.." );
		}
//		Cache<String , View3D> cache = (Cache<String , View3D>)CacheManager.getCache( ACTION_CACHE_MANAGER_LABEL );
//		cache.put( v.name , v );
		root.addView( v );
		v.bringToFront();
	}
	
	public  boolean isPointIn(
			float x ,
			float y ){
		
		View3D focus =ActionData.getInstance().getFocusView();
		
		if(focus!=null){
			if((x>focus.x&&x<(focus.x+focus.width))
					&&(y>focus.y&&y<(focus.y+focus.height)))
			{
				
				return true;
			}
			else 
				return false;
		}
		else
			return false;
		
	}
	
	public void setData(){
		this.mActionData=ActionData.getInstance();
		mActionData.setCurrAction( this );
		root = iLoongLauncher.getInstance().d3dListener.getRoot();
	}
	
	public String getString(int resId){
	   return  iLoongLauncher.getInstance().getString( resId );
	}
	
	public abstract void initView();
	
	public abstract void initEvent();
	
	public abstract void ondetach();
	
	public abstract void onAnimStarted();
	
	public  boolean isShowFront(){
		return true;
	}
}
