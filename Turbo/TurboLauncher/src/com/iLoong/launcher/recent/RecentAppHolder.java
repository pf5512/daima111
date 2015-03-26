package com.iLoong.launcher.recent;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.recent.RecentAppPage.IOnScrollListener;
import com.iLoong.launcher.recent.RecentAppPage.IRecentAppTracker;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class RecentAppHolder extends ViewGroup3D
{
	
	private final String TAG = "RecentAppHolder";
	private boolean isNorecent = false;
	public static float mStartPosY;
	public static float mStageHeight = 0;
	public static float mTouchableHeight;
	public static float mTouchableLow;
	public final float mBufferLineUp;
	public final float mBufferLineBelow;
	public final static int RECENT_MSG_DESTORY = 0;
	public static float mScaleFactor;
	private boolean mGLScissor;
	private Tween mScissorTween;
	private final float mTitleHeight;
	private IRecentAppTracker mIRecentAppTracker;
	public IOnScrollListener mOnScrollListener;
	public RecentAppPage mRecentAppPage;
	private final int row = 1 , column = 4;
	private ClearButton mBtnClear;
	private TextureRegion mBtnClearPress;
	private TextureRegion mBtnClearNormal;
	private RecentAppBiz mRecentAppBiz;
	public float mLastX;
	public float mLastY;
	public ArrayList<ViewRecord> viewList;
	private CellLayout3D layout;
	private boolean isChinese = true;
	private ImageView3D mImgTitle;
	private ImageView3D mImgNoRecent;
	private ArrayList<ImageView3D> mImgTips = new ArrayList<ImageView3D>();
	private TextureRegion mTxtRegionRecycle;
	private TextureRegion mTxtRegionLock;
	private TextureRegion mTxtRegionUnlock;
	private View3D bg;
	public static int mAnimState = 0; //0:无动画,1:正在打开，2：正在关闭
	private float duration = 0.5f;
	public static float colorAlpha = 1;
	private float realPosY = 0;
	private float destPosY = 0;
	private float cellWidth;
	private float cellHeight;
	private ArrayList<Vector2> mPoints;
	private int curPage;
	public static RecentApp fireupApp = null;
	
	public RecentAppHolder(
			String name ,
			float mCurDownY )
	{
		super( name );
		mAnimState = 0;
		fireupApp = null;
		transform = true;
		this.width = Utils3D.getScreenWidth();
		this.height = Utils3D.getScreenHeight();
		mScaleFactor = Utils3D.getScreenWidth() / 720f;
		mStartPosY = mCurDownY;
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			isChinese = true;
		}
		else
		{
			isChinese = false;
		}
		//绘制背景
		Bitmap bitmap = ThemeManager.getInstance().getBitmap( "theme/recentApplications/bg.png" );
		bg = new View3D( "bg" , new BitmapTexture( bitmap , true ) );
		bg.width = Utils3D.getScreenWidth();
		bg.height = bg.getHeight() * mScaleFactor;
		bg.setPosition( 0 , mStartPosY );
		bitmap.recycle();
		addView( bg );
		mStageHeight = bg.height;
		mTitleHeight = 72 * mScaleFactor;
		mTouchableHeight = mStartPosY + mStageHeight - Utilities.sIconTextureHeight * 4 / 3.0f;
		mTouchableLow = mStartPosY - Utilities.sIconTextureHeight * 2 / 3;
		mBufferLineUp = mStartPosY + mStageHeight - Utilities.sIconTextureHeight * 1.5f;
		mBufferLineBelow = mStartPosY;
		Bitmap title;
		if( isChinese )
		{
			title = ThemeManager.getInstance().getBitmap( "theme/recentApplications/title_cn.png" );
		}
		else
		{
			title = ThemeManager.getInstance().getBitmap( "theme/recentApplications/title_en.png" );
		}
		title = Tools.resizeBitmap( title , mScaleFactor );
		mImgTitle = new ImageView3D( "mImgTitle" , new BitmapTexture( title ) );
		mImgTitle.setPosition( 10 * mScaleFactor , mStartPosY + ( mStageHeight - mTitleHeight ) + ( mTitleHeight - mImgTitle.height ) / 2 );
		this.addView( mImgTitle );
		title.recycle();
		Bitmap noRecent;
		if( isChinese )
		{
			noRecent = ThemeManager.getInstance().getBitmap( "theme/recentApplications/norecent_cn.png" );
		}
		else
		{
			noRecent = ThemeManager.getInstance().getBitmap( "theme/recentApplications/norecent_en.png" );
		}
		noRecent = Tools.resizeBitmap( noRecent , mScaleFactor );
		mImgNoRecent = new ImageView3D( "mImgNoRecent" , new BitmapTexture( noRecent ) );
		mImgNoRecent.setPosition( ( this.width - mImgNoRecent.width ) / 2.0f , mStartPosY + +( ( mStageHeight - mTitleHeight ) - mImgNoRecent.height ) / 2 );
		mImgNoRecent.hide();
		this.addView( mImgNoRecent );
		noRecent.recycle();
		//垃圾箱正常状态
		Bitmap bitmapdn = ThemeManager.getInstance().getBitmap( "theme/recentApplications/delete_normal.png" );
		bitmapdn = Tools.resizeBitmap( bitmapdn , mScaleFactor );
		mBtnClearNormal = new TextureRegion( new BitmapTexture( bitmapdn , true ) );
		mBtnClear = new ClearButton( "vdn" , mBtnClearNormal );
		mBtnClear.setPosition( Utils3D.getScreenWidth() - bitmapdn.getWidth() * 3 / 2 , mStartPosY + ( mStageHeight - mTitleHeight ) + ( mTitleHeight - bitmapdn.getHeight() ) / 2.0f );
		bitmapdn.recycle();
		addView( mBtnClear );
		//垃圾箱按下状态
		Bitmap bitmapdp = ThemeManager.getInstance().getBitmap( "theme/recentApplications/delete_focus.png" );
		bitmapdp = Tools.resizeBitmap( bitmapdp , mScaleFactor );
		mBtnClearPress = new TextureRegion( new BitmapTexture( bitmapdp , true ) );
		bitmapdp.recycle();
		initTips();
		initPages();
	}
	
	public static int getState()
	{
		return mAnimState;
	}
	
	public void setTipIconVisible(
			boolean flag )
	{
		if( !flag )
		{
			for( int i = 0 ; i < mImgTips.size() ; i++ )
			{
				mImgTips.get( i ).hide();
			}
		}
		else
		{
			if( mRecentAppPage.getChildCount() == 0 )
				return;
			//int size=mRecentAppBiz.getRecentAppCount()-mRecentAppPage.getCurrentPage()*column;
			int size = Math.min( ( (ViewGroup3D)mRecentAppPage.getChildAt( mRecentAppPage.getCurrentPage() ) ).getChildCount() , column );
			for( int i = 0 ; i < mImgTips.size() ; i++ )
			{
				if( i < size )
				{
					mImgTips.get( i ).show();
				}
			}
		}
	}
	
	public void initTips()
	{
		Bitmap bitry = ThemeManager.getInstance().getBitmap( "theme/recentApplications/torecycle.png" );
		bitry = Tools.resizeBitmap( bitry , mScaleFactor );
		mTxtRegionRecycle = new TextureRegion( new BitmapTexture( bitry , true ) );
		Bitmap bitlock = ThemeManager.getInstance().getBitmap( "theme/recentApplications/tolock.png" );
		bitlock = Tools.resizeBitmap( bitlock , mScaleFactor );
		mTxtRegionLock = new TextureRegion( new BitmapTexture( bitlock , true ) );
		bitlock.recycle();
		Bitmap bitunlock = ThemeManager.getInstance().getBitmap( "theme/recentApplications/tounlock.png" );
		bitunlock = Tools.resizeBitmap( bitunlock , mScaleFactor );
		mTxtRegionUnlock = new TextureRegion( new BitmapTexture( bitunlock , true ) );
		bitunlock.recycle();
		for( int i = 0 ; i < column ; i++ )
		{
			ImageView3D mImgTip = new ImageView3D( "mImgTip" + i , mTxtRegionRecycle );
			mImgTips.add( mImgTip );
			mImgTip.hide();
			this.addView( mImgTip );
		}
	}
	
	public void setViewList(
			ArrayList<? extends View3D> list ,
			CellLayout3D layout )
	{
		viewList = new ArrayList<RecentAppHolder.ViewRecord>();
		for( View3D v : list )
		{
			ViewRecord vr = new ViewRecord();
			vr.v = v;
			vr.orgx = v.x;
			vr.orgy = v.y;
			viewList.add( vr );
		}
		this.layout = layout;
		//		for( int i = 0 ; i < layout.getChildCount() ; i++ )
		//		{
		//			layout.getChildAt( i ).color.a = 0.5f;
		//		}
	}
	
	public void initPages()
	{
		mRecentAppBiz = new RecentAppBiz();
		mIRecentAppTracker = new IRecentAppTrackerImpl();
		mOnScrollListener = new OnScrollListenerImpl();
		mRecentAppPage = new RecentAppPage( "mRecentAppPage" );
		mRecentAppPage.setSize( this.width , this.height );
		mRecentAppPage.setPosition( 0 , 0 );
		mRecentAppPage.init( row , column , 0 , mStartPosY , this.width , mStageHeight - mTitleHeight , mIRecentAppTracker , mOnScrollListener );
		this.addView( mRecentAppPage );
	}
	
	public void addRecent(
			ShortcutInfo info )
	{
		mRecentAppBiz.addLockeddApp( info );
	}
	
	float test = 0;
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( mGLScissor )
		{
			Gdx.gl.glEnable( GL10.GL_SCISSOR_TEST );
			//动画上滑的时候需要记录最后一次真实的滑动距离，因为Tween动画到最后一帧有可能并没有真正到达指定的高度，会造成动画动画抖动
			if( mAnimState == 1 )
			{
				test = realPosY = this.getUser();
			}
			else if( mAnimState == 2 )
			{
				test = this.getUser();
			}
			if( viewList == null )
			{
				if( iLoongLauncher.getInstance() != null && iLoongLauncher.getInstance().d3dListener != null && iLoongLauncher.getInstance().d3dListener.root != null && iLoongLauncher.getInstance().d3dListener.root
						.getWorkspace() != null && iLoongLauncher.getInstance().d3dListener.root.getWorkspace().getCurrentCellLayout() != null )
				{
					@SuppressWarnings( "unchecked" )
					ArrayList<? extends View3D> list = iLoongLauncher.getInstance().d3dListener.root.getWorkspace().getCurrentCellLayout().getAllViews();
					for( View3D v : list )
					{
						ViewRecord vr = new ViewRecord();
						vr.v = v;
						vr.orgx = v.x;
						vr.orgy = v.y;
						viewList.add( vr );
					}
				}
			}
			if( viewList != null )
			{
				NPageBase.scoolif = false;
				//Log.i( "getUser()" , "getUser()" + this.getUser() );
				//Log.i( "getUser()" , "mStageHeight" + mStageHeight );
				if( this.getUser() <= mStageHeight )
				{
					for( ViewRecord vr : viewList )
					{
						vr.v.setPosition( vr.orgx , vr.orgy + test );
					}
				}
			}
			Gdx.gl.glScissor( 0 , (int)mStartPosY , Utils3D.getScreenWidth() , (int)test );
			if( layout != null )
			{
				if( mAnimState == 1 )
				{
					colorAlpha = 1.0f - (float)( ( this.getUser() * 0.5 ) / (float)mStageHeight );
				}
				else if( mAnimState == 2 )
				{
					colorAlpha = 0.5f + ( 0.5f - (float)( ( this.getUser() * 0.5 ) / (float)mStageHeight ) );
				}
				for( int i = 0 ; i < layout.getChildCount() ; i++ )
				{
					layout.getChildAt( i ).color.a = colorAlpha;
				}
				onDockbarFadaInout( colorAlpha );
			}
		}
		super.draw( batch , parentAlpha );
		if( mGLScissor )
		{
			Gdx.gl.glDisable( GL10.GL_SCISSOR_TEST );
		}
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof RecentApp )
		{
			switch( event_id )
			{
				case RecentApp.EVENT_MSG_ID_DESTORY:
					destory();
					break;
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		////		// TODO Auto-generated method stub
		if( mScissorTween == source && type == TweenCallback.COMPLETE )
		{
			if( this.getUser() <= 0 )
			{
				if( fireupApp != null )
				{
					if( fireupApp.pointer == 1 )
					{
						fireupApp.onClick( fireupApp.x + fireupApp.width / 2 , fireupApp.y + fireupApp.height / 2 );
					}
					else if( fireupApp.pointer == 2 )
					{
						fireupApp.onLongClick( fireupApp.x + fireupApp.width / 2 , fireupApp.y + fireupApp.height / 2 );
					}
				}
				fireupApp = null;
				onDelete();
			}
			else
			{
				if (!iLoongApplication.getInstance().getModel().appListLoaded)
				{ 
//					Log.v("diaosixu", String.format("abc %f %f %f %d", mStartPosY, mStageHeight, mTitleHeight, Utils3D.getScreenHeight()));
					int marginTop = (int)( Utils3D.getScreenHeight() ) - (int)( mStartPosY + (mStageHeight - mTitleHeight) / 2 ) - (int)(40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density);
					SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );	

					iLoongApplication.getInstance().getModel().loadAppList();
					
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							mRecentAppPage.refreshRecentAppList();
							
							onAnimCompleted();
							
							SendMsgToAndroid.cancelCustomDialog();
						}
					} );
				}
				else
				{
					onAnimCompleted();
				}
				
//				if( DefaultLayout.WorkspaceActionGuide )
//				{
//					if( ActionHolder.getInstance() != null )
//					{
//						if( DefaultLayout.WorkspaceActionGuide )
//						{
//							SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
//							if( !pref.getBoolean( "first_back_from_app" , false ) )
//							{
//								{
//									pref.edit().putBoolean( "first_back_from_app" , true ).commit();
//									ActionHolder.getInstance().onRecentStarted();
//								}
//							}
//						}
//					}
//				}
//				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
//				if( !pref.getBoolean( "first_back_from_app" , false ) )
//				{
//					{
//						pref.edit().putBoolean( "first_back_from_app" , true ).commit();
//					}
//				}
			}
		}
	}
	
	public void onDockbarFadaInout(
			float alpha )
	{
		if( alpha < 0.5f )
		{
			return;
		}
		ViewGroup3D dg = iLoongLauncher.getInstance().d3dListener.root.hotseatBar.dockGroup;
		ViewGroup3D mg = iLoongLauncher.getInstance().d3dListener.root.hotseatBar.getMainGroup();
		mg.color.a = alpha;
		for( int i = 0 ; i < dg.getChildCount() ; i++ )
		{
			dg.getChildAt( i ).color.a = alpha;
		}
	}
	
	public void onAnimCompleted()
	{
		mAnimState = 0;
		mGLScissor = false;
	}
	
	public void onDelete()
	{
		onDockbarFadaInout( 1 );
		onAnimCompleted();
		viewParent.onCtrlEvent( this , RECENT_MSG_DESTORY );
		onDestory();
		for( int i = 0 ; i < layout.getChildCount() ; i++ )
		{
			layout.getChildAt( i ).color.a = 1;
		}
		for( int i = 0 ; i < viewList.size() ; i++ )
		{
			viewList.get( i ).v.setPosition( viewList.get( i ).orgx , viewList.get( i ).orgy );
		}
	}
	
	//	@Override
	//	public boolean onTouchDown(
	//			float x ,
	//			float y ,
	//			int pointer )
	//	{
	//		// TODO Auto-generated method stub
	//		if( y < mStartPosY || y > mStartPosY + mStageHeight )
	//			destory();
	//		return super.onTouchDown( x , y , pointer );
	//	}
	public void destory()
	{
		if( mAnimState != 0 )
			return;
		mAnimState = 2;
		if( mScissorTween != null )
		{
			mScissorTween.free();
			mScissorTween = null;
		}
		mGLScissor = true;
		destPosY = 0;
		this.setUser( realPosY );
		mScissorTween = this.startTween( View3DTweenAccessor.USER , Cubic.OUT , duration , destPosY , 0 , 0 ).setCallback( this );
		if( DefaultLayout.enable_news )
		{
			if( DefaultLayout.show_newspage_with_handle )
			{
				Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
			}
		}
	}
	
	@Override
	public void show()
	{
		mAnimState = 1;
		this.setUser( 0 );
		mGLScissor = true;
		destPosY = mStageHeight;
		mScissorTween = this.startTween( View3DTweenAccessor.USER , Cubic.OUT , duration , mStageHeight , 0 , 0 ).setCallback( this );
		super.show();
	}
	
	@Override
	public void hide()
	{
		super.hide();
		//		mAnimState = 2;
		//		if( mScissorTween != null )
		//		{
		//			mScissorTween.free();
		//			mScissorTween = null;
		//		}
		//		mGLScissor = true;
		//		destPosY = 0;
		//		this.setUser( realPosY );
		//		mScissorTween = this.startTween( View3DTweenAccessor.USER , Cubic.OUT , duration , destPosY , 0 , 0 ).setCallback( this );
	}
	
	public void forceDestory()
	{
		if( mAnimState != 0 )
			return;
		this.hide();
		if( mScissorTween != null )
		{
			mScissorTween.free();
			mScissorTween = null;
		}
		//		mAnimState = 0;
		//		mGLScissor = false;
		//		
		onDelete();
	}
	
	class IRecentAppTrackerImpl implements IRecentAppTracker
	{
		
		@Override
		public ArrayList<RecentApp> initRecentApps()
		{
			//因为这个方法只允许掉调用一次，并且在注销的时候没有需要释放的资源所以可以使用匿名对象
			return generateLockedAppList();
		}
		
		@Override
		public ArrayList<RecentApp> refreshRecentApps()
		{
			return generateLockedAppList();
		}
		
		public void onLoadCompleted(
				ArrayList<Vector2> points )
		{
			mPoints = points;
			for( int i = 0 ; i < column ; i++ )
			{
				if( i < mRecentAppBiz.getRecentAppCount() )
				{
					mImgTips.get( i ).setPosition( mPoints.get( i ).x + ( cellWidth - mImgTips.get( i ).width ) / 2 , mPoints.get( i ).y + ( cellHeight - mImgTips.get( i ).height ) / 2 );
					mImgTips.get( i ).hide();
				}
				else
				{
					mImgTips.get( i ).hide();
				}
			}
		}
		
		@Override
		public void onRefreshCompleted()
		{
			mRecentAppBiz.execClear();
			mRecentAppPage.execRefresh();
			mRecentAppBiz.resetClearState();
			if( mRecentAppBiz.getRecentAppCount() < 1 )
			{
				setNoRecentState( true );
			}
			for( int i = 0 ; i < column ; i++ )
			{
				if( i > mRecentAppBiz.getRecentAppCount() - 1 )
				{
					mImgTips.get( i ).hide();
				}
			}
			setTipIconVisible( true );
		}
	}
	
	class OnScrollListenerImpl implements IOnScrollListener
	{
		
		boolean isDropStarted = false;
		
		@Override
		public void onLock(
				ArrayList<View3D> list ,
				float x ,
				float y )
		{
			if( !isDropStarted )
			{
				for( View3D v : list )
				{
					if( v instanceof RecentApp )
					{
						boolean isLocked = !( (Icon3D)v ).isLocked;
						( (Icon3D)v ).isLocked = isLocked;
						mRecentAppPage.setLocked( isLocked );
						if( ( (Icon3D)v ).isLocked )
						{
							mRecentAppBiz.addLockeddApp( (ShortcutInfo)( (Icon3D)v ).getItemInfo() );
						}
						else
						{
							mRecentAppBiz.removeLockedApp( (ShortcutInfo)( (Icon3D)v ).getItemInfo() );
						}
					}
				}
				isDropStarted = true;
			}
		}
		
		@Override
		public void onDragCompleted(
				ArrayList<View3D> list ,
				float x ,
				float y )
		{
			isDropStarted = false;
		}
		
		@Override
		public void onRemoved(
				int index ,
				ArrayList<View3D> list ,
				float x ,
				float y )
		{
			for( View3D v : list )
			{
				if( v instanceof RecentApp )
				{
					mRecentAppBiz.deleteApp( (ShortcutInfo)( (Icon3D)v ).getItemInfo() );
				}
			}
			if( index < mImgTips.size() )
			{
				mImgTips.get( index ).hide();
			}
			if( mRecentAppBiz.getRecentAppCount() < 1 )
			{
				setNoRecentState( true );
			}
			setTipIconVisible( true );
		}
		
		@Override
		public void onTouchEvent(
				float x ,
				float y ,
				int point )
		{
			if( ( x > mBtnClear.x && x < ( mBtnClear.x + mBtnClear.width ) ) && ( y > mBtnClear.y && y < mBtnClear.y + mBtnClear.height ) )
			{
				if( point == 1 )
				{
					mBtnClear.onTouchDown( x , y , 1 );
				}
				else
				{
					mBtnClear.onTouchUp( x , y , 1 );
				}
			}
		}
		
		@Override
		public void onStateChanged(
				int index ,
				int movingDir ,
				boolean curState )
		{
			Log.v( TAG , "index ." + index + " ,movingDir:" + movingDir + " ,curState:" + curState );
			if( movingDir == 0 )
			{
				//当前是锁定状态
				if( curState )
				{
					//当前icon的位置在下半区间需要把锁定状态改成未锁定状态
					for( int i = 0 ; i < mImgTips.size() ; i++ )
						mImgTips.get( i ).region = mTxtRegionUnlock;
				}
				else
				{//当前是未锁定状态
					for( int i = 0 ; i < mImgTips.size() ; i++ )
						mImgTips.get( i ).region = mTxtRegionLock;
					//mImgTips.get( index ).region = mTxtRegionLock;
				}
			}
			else
			{//当前icon是在上半区间，无论当前是否锁定状态都只需要显示删除图片就可以了
				//mImgTips.get( index ).region = mTxtRegionRecycle;
				for( int i = 0 ; i < mImgTips.size() ; i++ )
					mImgTips.get( i ).region = mTxtRegionRecycle;
			}
		}
		
		@Override
		public void onPageScrolledFinished(
				int pageIndex )
		{
			setTipIconVisible( true );
			//			for( int i = 0 ; i < mImgTips.size() ; i++ )
			//			{
			//				if( i < mRecentAppBiz.getRecentAppCount()-pageIndex*column )
			//					mImgTips.get( i ).show();
			//			}
		}
		
		@Override
		public void onPageScrolledStart()
		{
			for( int i = 0 ; i < mImgTips.size() ; i++ )
			{
				mImgTips.get( i ).hide();
			}
		}
		
		@Override
		public void startRemoved()
		{
			setTipIconVisible( false );
		}
		
		@Override
		public void setCurrPage(
				int index )
		{
			curPage = index;
		}
		
		@Override
		public void onDragOver(
				ArrayList<View3D> list ,
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void setStateTipVisible(
				boolean visible )
		{
			setTipIconVisible( visible );
		}
	}
	
	private ArrayList<RecentApp> generateLockedAppList()
	{
		ArrayList<RecentApp> list = mRecentAppBiz.getRecentAppList();
		if( list == null || list.size() < 1 )
		{
			setNoRecentState( true );
		}
		else
		{
			cellWidth = list.get( 0 ).width;
			cellHeight = list.get( 0 ).height;
			setNoRecentState( false );
		}
		return list;
	}
	
	public void setNoRecentState(
			boolean isNoRecent )
	{
		if( isNoRecent )
		{
			isNorecent = true;
			mImgNoRecent.show();
			mBtnClear.region = mBtnClearPress;
		}
		else
		{
			mBtnClear.region = mBtnClearNormal;
			isNorecent = false;
			mImgNoRecent.hide();
		}
	}
	
	class ClearButton extends View3D
	{
		
		public ClearButton(
				String name )
		{
			super( name );
		}
		
		public ClearButton(
				String name ,
				Texture texture )
		{
			super( name , texture );
		}
		
		public ClearButton(
				String name ,
				TextureRegion region )
		{
			super( name , region );
		}
		
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			if( isNorecent )
			{
				return true;
			}
			this.region = mBtnClearPress;
			kill( iLoongLauncher.getInstance() );
			clear();
			return true;
		}
		
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			if( isNorecent )
			{
				return true;
			}
			this.region = mBtnClearNormal;
			return true;
		}
		
		public boolean onClick(
				float x ,
				float y )
		{
			return true;
		}
		
		private void kill(
				Context context )
		{
			ActivityManager activityManager = (ActivityManager)context.getSystemService( context.ACTIVITY_SERVICE );
			List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
			if( list != null )
			{
				for( int i = 0 ; i < list.size() ; i++ )
				{
					ActivityManager.RunningAppProcessInfo appInfo = list.get( i );
					String[] pkgList = appInfo.pkgList;
					if( appInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE )
					{
						for( int j = 0 ; j < pkgList.length ; j++ )
						{
							activityManager.killBackgroundProcesses( pkgList[j] );
						}
					}
				}
			}
		}
	}
	
	class ViewRecord
	{
		
		View3D v;
		float orgx;
		float orgy;
	}
	
	public void clear()
	{
		//删除的时候先隐藏掉需要删除应用背后的小图片
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ClicktheTrash" );
		int count = mRecentAppBiz.getLockedAppCount();
		for( int i = 0 ; i < column ; i++ )
		{
			if( i >= count )
			{
				mImgTips.get( i ).hide();
			}
		}
		mRecentAppPage.refresh( mRecentAppBiz.getClearedList() );
	}
	
	public void onDestory()
	{
		NPageBase.scoolif = true;
		Log.v( TAG , "RecentAppHolder onDestory..." );
		mTxtRegionRecycle.getTexture().dispose();
		mTxtRegionLock.getTexture().dispose();
		mTxtRegionUnlock.getTexture().dispose();
		mImgTitle.region.getTexture().dispose();
		mBtnClear.region.getTexture().dispose();
		bg.region.getTexture().dispose();
		mBtnClearPress.getTexture().dispose();
		mBtnClearNormal.getTexture().dispose();
		mImgNoRecent.region.getTexture().dispose();
		System.gc();
	}
}
