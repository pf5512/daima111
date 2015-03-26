package com.iLoong.launcher.recent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class RecentAppPage extends NPageBase implements DragSource3D , DropTarget3D
{
	
	public final int TweenUserData_RemoveApp = 0;
	public final int TweenUserData_RefreshApp = 1;
	public boolean isProcessed = false;
	//这变量标志是否在滑页
	public static boolean onMove = false;
	private final String TAG = "RecentAppPage";
	private IRecentAppTracker tracker;
	private int mRow = 2;
	private int mPageNum = 0;
	private int mColumn = 4;
	private float mStartX;
	private float mStartY;
	private float standardY = 0;
	private int indexOfFocus = 0;
	private float mStageHeight;
	private float mStageWidth;
	private View3D mFocusView;
	private View3D mMovingView;
	private ArrayList<RecentApp> mRecentAppList = null;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	private ArrayList<View3D> selectedObjects = new ArrayList<View3D>();
	public static Timeline mMainTimeline = null;
	public static Timeline mSlaveTimeLine = null;
	private IOnScrollListener mOnScrollListener;
	private ArrayList<RecentApp> needRemovedList;
	public static boolean isdragged = false;
	private float bufferLength = 0;
	private float MovingViewAlpha = 1;
	//this map represents state of all items .when an item is removed ,its state will be set as "false".
	private HashMap<Integer , Boolean> mItemState = new HashMap<Integer , Boolean>();
	//BitmapTexture txt;
	float temX , temY;
	float mScaleXY = 0;
	float gapx;
	float gapy;
	float cellWidth;
	float cellHeight;
	private ArrayList<View3D> removedlist;
	private ArrayList<Vector2> mPoints = new ArrayList<Vector2>();
	
	public RecentAppPage(
			String name )
	{
		super( name );
		setWholePageList();
		setEffectType( APageEase.COOLTOUCH_EFFECT_DEFAULT );
	}
	
	public void init(
			int row ,
			int column ,
			float startX ,
			float startY ,
			float stageWidth ,
			float stageHeight ,
			IRecentAppTracker tracker ,
			IOnScrollListener scrollListener )
	{
		mRow = row;
		mColumn = column;
		mStartX = startX;
		mStartY = startY;
		mStageHeight = stageHeight;
		mStageWidth = stageWidth;
		this.tracker = tracker;
		this.mOnScrollListener = scrollListener;
		mRecentAppList = tracker.initRecentApps();
		int size = mRecentAppList.size();
		if( size == 0 )
		{
			return;
		}
		mPageNum = ( size + mRow * mColumn - 1 ) / ( mRow * mColumn );
		for( int i = 0 ; i < size ; i++ )
		{
			mItemState.put( i , true );
		}
		mPoints.clear();
		setPage();
	}
	
	public void refreshRecentAppList()
	{
		mRecentAppList = tracker.refreshRecentApps();  
		int size = mRecentAppList.size();
		if( size == 0 )
		{
			return;
		}
		mPageNum = ( size + mRow * mColumn - 1 ) / ( mRow * mColumn );
		for( int i = 0 ; i < size ; i++ )
		{
			mItemState.put( i , true );
		}
		mPoints.clear();
		setPage();
	}
	
	public void refresh(
			ArrayList<ShortcutInfo> list )
	{
		if( list == null || list.size() < 1 )
			return;
		mOnScrollListener.startRemoved();
		float duration = 0.4f;
		float delay = 0.1f;
		//只对当前页的应用做动画，后面一页的直接删除不做动画
		//int size = Math.min( mRecentAppList.size() , mColumn );
		ArrayList<ShortcutInfo> curPageList = new ArrayList<ShortcutInfo>();
		ViewGroup3D vg = (ViewGroup3D)this.getChildAt( getCurrentPage() );
		for( int i = 0 ; i < vg.getChildCount() ; i++ )
		{
			RecentApp app = (RecentApp)vg.getChildAt( i );
			ShortcutInfo info = (ShortcutInfo)app.getItemInfo();
			for( ShortcutInfo si : list )
			{
				if( info.intent.getComponent().getPackageName().equals( si.intent.getComponent().getPackageName() ) )
				{
					curPageList.add( si );
				}
			}
		}
		if( mMainTimeline != null )
		{
			mMainTimeline.free();
		}
		mMainTimeline = Timeline.createParallel();
		ShortcutInfo info = null;
		//		//因为动画要从右往左开始删除，所以右边的动画要先跑，这里先计算出总共有几个图片需要做动画。
		//		int num=0;
		//		for( ShortcutInfo ri : curPageList )
		//		{
		//			for( int i = 0 ; i < mRecentAppList.size() ; i++ )
		//			{
		//				info = (ShortcutInfo)mRecentAppList.get( i ).getItemInfo();
		//				if( info.intent.getComponent().getPackageName().equals( ri.intent.getComponent().getPackageName() ) ){
		//					num++;
		//				}
		//			}
		//		}
		for( ShortcutInfo ri : curPageList )
		{
			for( int i = 0 ; i < mRecentAppList.size() ; i++ )
			{
				info = (ShortcutInfo)mRecentAppList.get( i ).getItemInfo();
				if( info.intent.getComponent().getPackageName().equals( ri.intent.getComponent().getPackageName() ) )
				{
					mMainTimeline.push( Tween.to( mRecentAppList.get( i ) , View3DTweenAccessor.SCALE_XY , duration ).delay( delay * i ).target( 0f , 0f , 0f ) );
					//					num--;
				}
			}
		}
		mMainTimeline.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( TweenUserData_RefreshApp );
	}
	
	public void execRefresh()
	{
		mRecentAppList = this.tracker.refreshRecentApps();
		int size = mRecentAppList.size();
		if( size < 1 )
		{
			clearPage();
			return;
		}
		mPageNum = ( size + mRow * mColumn ) / ( mRow * mColumn );
		setPage();
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		if( mMainTimeline != null )
		{
			mScaleXY = this.getUser();
			if( mMovingView != null )
			{
				mMovingView.setScale( mScaleXY , mScaleXY );
			}
		}
		//batch.draw( txt , temX , temY , 300, 20 );
	}
	
	public void setPage()
	{
		clearPage();
		//增加页面
		for( int i = 0 ; i < mPageNum ; i++ )
		{
			ViewGroup3D page = new ViewGroup3D( "page_" + i ) {
				
				public boolean onClick(
						float x ,
						float y )
				{
					if( !( y < ( mStartY + mStageHeight ) && y > mStartY ) )
					{
						return true;
					}
					return super.onClick( x , y );
				}
			};
			//			page.setBackgroud( new NinePatch( R3D.findRegion( "XXXX" ) ) );
			page.setSize( this.width , this.height );
			page.clear();
			this.addPage( page );
		}
		this.setCurrentPage( 0 );
		//往页面添加元素
		setItems();
		this.tracker.onLoadCompleted( mPoints );
	}
	
	public void clearPage()
	{
		this.clear();
		view_list.clear();
	}
	
	public void setItems()
	{
		//mRecentAppList = this.tracker.initRecentApps();
		int pageIndex = 0;
		float w = cellWidth = mRecentAppList.get( 0 ).width;
		float h = cellHeight = mRecentAppList.get( 0 ).height;
		gapx = ( mStageWidth - mColumn * w ) / ( mColumn + 1.0f );
		gapy = ( mStageHeight - mRow * h ) / ( mRow + 1.0f );
		if( gapx < 0 )
			gapx = 0;
		if( gapy < 0 )
			gapy = 0;
		for( int j = 0 ; j < mRecentAppList.size() ; j++ )
		{
			mItemState.put( j , true );
			float posx = mStartX + gapx + ( gapx + w ) * ( j % mColumn );
			float posy = mStartY + gapy + ( gapy + h ) * ( 1 - j ) % mRow;
			standardY = posy;
			mRecentAppList.get( j ).setPosition( posx , posy );
			pageIndex = j / ( mRow * mColumn );
			( (ViewGroup3D)this.getChildAt( pageIndex ) ).addView( mRecentAppList.get( j ) );
			Vector2 v = new Vector2();
			v.set( posx , posy );
			mPoints.add( v );
		}
		bufferLength = mPoints.get( 0 ).y - RecentAppHolder.mTouchableLow;
		//	Gdx.graphics.requestRendering();
	}
	
	public static interface IRecentAppTracker
	{
		
		public ArrayList<RecentApp> initRecentApps();
		
		public ArrayList<RecentApp> refreshRecentApps();
		
		public void onLoadCompleted(
				ArrayList<Vector2> points );
		
		public void onRefreshCompleted();
	}
	
	public static interface IRecentAppFavariteDAO
	{
		
		public LinkedList<View3D> getFavarites();
		
		public boolean setFavarites(
				ArrayList<? extends View3D> app );
		
		public boolean equals(
				View3D v );
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
				case Icon3D.MSG_ICON_LONGCLICK:
					Icon3D icon = (Icon3D)sender;
					if( selectedObjects.size() == 0 )
					{
						selectedObjects.add( icon );
					}
					if( !selectedObjects.contains( icon ) )
					{
						clearDragObjs();
						selectedObjects.add( icon );
					}
					dragObjects.clear();
					for( View3D view : selectedObjects )
					{
						if( view instanceof Icon3D )
						{
							icon.recentOrgPos = new Vector2();
							icon.recentOrgPos.x = icon.x;
							icon.recentOrgPos.y = icon.y;
							Icon3D icon3d = (Icon3D)view;
							icon3d.hideSelectedIcon();
							Icon3D iconClone = icon3d.clone();
							icon3d.toAbsoluteCoords( point );
							iconClone.x = point.x;
							iconClone.y = point.y;
							dragObjects.add( iconClone );
						}
					}
					icon.hide();
					selectedObjects.clear();
					this.setTag( icon.getTag() );
					mFocusView = icon;
					this.releaseFocus();
					iLoongLauncher.getInstance().d3dListener.getWorkspace3D().releaseFocus();
					for( int i = 0 ; i < mRecentAppList.size() ; i++ )
					{
						if( mFocusView == mRecentAppList.get( i ) )
						{
							indexOfFocus = i;
						}
					}
					indexOfFocus = indexOfFocus % mColumn;
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
			}
		}
		return super.onCtrlEvent( sender , event_id );
	}
	
	protected void clearDragObjs()
	{
		for( View3D view : selectedObjects )
		{
			( (Icon3D)view ).hideSelectedIcon();
		}
		selectedObjects.clear();
	}
	
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		return (ArrayList<View3D>)dragObjects;
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		isdragged = false;
		list.get( 0 ).color.a = MovingViewAlpha;
		mMovingView = list.get( 0 );
		removedlist = list;
		if( mMovingView instanceof RecentApp )
		{
			if( y <= Workspace3D.getInstance().mRecentApplications.mTouchableLow )
			{
				//( (Icon3D)mFocusView ).isLocked = !( (Icon3D)mFocusView ).isLocked;
				//( (Icon3D)mMovingView ).isLocked = !( (Icon3D)mMovingView ).isLocked;
			}
		}
		temX = Workspace3D.getInstance().mRecentApplications.mLastX;
		temY = Workspace3D.getInstance().mRecentApplications.mLastY;
		mMovingView.setPosition( temX , temY );
		//list.get( 0 ).setPosition( temX , temY - list.get( 0 ).height / 2 );
		this.addView( list.get( 0 ) );
		//list.get( 0 ).setPosition( temX - list.get( 0 ).width / 2 , temY - list.get( 0 ).height / 2 );
		if( temY >= Workspace3D.getInstance().mRecentApplications.mTouchableHeight )
		{
			//mFocusView.show();
			onAppRemoved( list.get( 0 ) );
		}
		//else if(y<mStartY-list.get( 0 ).height/2){
		else
		{
			onAppReturn( list.get( 0 ) );
		}
		return true;
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		MovingViewAlpha = list.get( 0 ).color.a;
		if( y > standardY )
			mOnScrollListener.onStateChanged( indexOfFocus , 1 , ( (Icon3D)mFocusView ).isLocked );
		else
			mOnScrollListener.onStateChanged( indexOfFocus , 0 , ( (Icon3D)mFocusView ).isLocked );
		return true;
	}
	
	//	public void onAppRemoved(
	//			View3D movedView )
	//	{
	//		mOnScrollListener.startRemoved();
	//		
	//		View3D nextPageFirstView = null;
	//		int needNum = 0;
	//		ViewGroup3D curpage = (ViewGroup3D)getChildAt( getCurrentPage() );
	//		ViewGroup3D otherpage = null;
	//		int pageOther = 1 - getCurrentPage();
	//		ArrayList<View3D> nextPageViews = null;
	//		int lastIndex = 0;
	//		float duration = 0.5f;
	//		mRecentAppList.clear();
	//		for( int i = 0 ; i < curpage.getChildCount() ; i++ )
	//		{
	//			mRecentAppList.add( (RecentApp)curpage.getChildAt( i ) );
	//		}
	//		if( this.getChildCount() > 1 )
	//		{
	//			otherpage = (ViewGroup3D)getChildAt( pageOther );
	//			for( int i = 0 ; i < otherpage.getChildCount() ; i++ )
	//			{
	//				mRecentAppList.add( (RecentApp)otherpage.getChildAt( i ) );
	//			}
	//		}
	//		for( int i = 0 ; i < mRecentAppList.size() ; i++ )
	//		{
	//			if( mRecentAppList.get( i ) == mFocusView )
	//			{
	//				lastIndex = i;
	//				break;
	//			}
	//		}
	//		
	//		if( mMainTimeline != null )
	//		{
	//			mMainTimeline.free();
	//		}
	//		mMainTimeline = Timeline.createParallel();
	//		//		mMainTimeline.push( Tween.to( movedView , View3DTweenAccessor.SCALE_XY
	//		//				, duration+0.2f ).target( 0 ) );
	//		setUser( 1 );
	//		
	//		
	//		//		//如果页数大于2页需要将另一页的元素往前推动
	//		if( getChildCount() > 1 )
	//		{
	////			//只有在第二页删除且该页只有一个元素的时候，需要将第一个页的全部元素移过来
	////			if(getCurrentPage()==1 &&curpage.getChildCount()==1){
	////				
	////			}
	//			needNum = mColumn - ( curpage.getChildCount() - 1 );
	//			if( needNum > 0 )
	//			{
	//				nextPageViews = new ArrayList<View3D>();
	//				int realNum = Math.min( needNum , otherpage.getChildCount() );
	//				ArrayList<View3D> removed = new ArrayList<View3D>();
	//				for( int i = 0 ; i < realNum ; i++ )
	//				{
	//					View3D v = otherpage.getChildAt( i );
	//					removed.add( v );
	//				}
	//				for( int i = 0 ; i < realNum ; i++ )
	//				{
	//					View3D v = removed.get( i );
	//					v.setPosition( this.width + ( gapx + v.width ) * ( i ) , v.y );
	//					otherpage.removeView( removed.get( i ) );
	//					curpage.addView( v );
	//					nextPageViews.add( v );
	//				}
	//				//如果下一页已经没有应用了，则该页需要从Npage中释放掉
	//				if( otherpage.getChildCount() < 1 )
	//				{
	//					view_list.remove( pageOther );
	//					otherpage.remove();
	//					setCurrentPage( 0 );
	//				}
	//				else
	//				{//否则需要对下一页的所有元素重新排列
	//					measurePos( otherpage );
	//				}
	//			}
	//		}
	//		{
	//			int maxCell = ( (ViewGroup3D)getChildAt( getCurrentPage() ) ).getChildCount();
	//			int num = Math.min( maxCell , mRecentAppList.size() );
	//			for( int i = lastIndex + 1 ; i < num ; i++ )
	//			{
	//				//if( mItemState.get( i ) == true )
	//				{
	//					//((ViewGroup3D)getChildAt( getCurrentPage() )).getChildAt( i )
	//					mMainTimeline.push( Tween.to( mRecentAppList.get( i ) , View3DTweenAccessor.POS_XY , duration ).target( mPoints.get( i - 1 ).x , mPoints.get( i - 1 ).y ) );
	//				}
	//			}
	//			//			if(nextPageFirstView!=null){
	//			//				
	//			//					mMainTimeline.push( Tween.to( nextPageFirstView, View3DTweenAccessor.POS_XY , duration ).target(  mPoints.get(mColumn-1 ).x,  mPoints.get( mColumn-1).y ) );
	//			//				
	//			//			}
	//			if( nextPageViews != null )
	//			{
	//				for( int i = 0 ; i < nextPageViews.size() ; i++ )
	//					mMainTimeline.push( Tween.to( nextPageViews.get( i ) , View3DTweenAccessor.POS_XY , duration ).target(
	//							mPoints.get( mColumn - needNum + i ).x ,
	//							mPoints.get( mColumn - needNum + i ).y ) );
	//			}
	//		}
	//		mMainTimeline.push( Tween.to( this , View3DTweenAccessor.USER , duration - 0.2f ).target( 0 ) );
	//		mMainTimeline.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( TweenUserData_RemoveApp );
	//	}
	public void onAppRemoved(
			View3D movedView )
	{
		mOnScrollListener.startRemoved();
		View3D nextPageFirstView = null;
		int needNum = 0;
		ViewGroup3D curpage = (ViewGroup3D)getChildAt( getCurrentPage() );
		ViewGroup3D otherpage = null;
		int pageOther = 1 - getCurrentPage();
		ArrayList<View3D> nextPageViews = null;
		int lastIndex = 0;
		float duration = 0.5f;
		mRecentAppList.clear();
		for( int i = 0 ; i < curpage.getChildCount() ; i++ )
		{
			mRecentAppList.add( (RecentApp)curpage.getChildAt( i ) );
		}
		if( this.getChildCount() > 1 )
		{
			otherpage = (ViewGroup3D)getChildAt( pageOther );
			for( int i = 0 ; i < otherpage.getChildCount() ; i++ )
			{
				mRecentAppList.add( (RecentApp)otherpage.getChildAt( i ) );
			}
		}
		for( int i = 0 ; i < mRecentAppList.size() ; i++ )
		{
			if( mRecentAppList.get( i ) == mFocusView )
			{
				lastIndex = i;
				break;
			}
		}
		if( mMainTimeline != null )
		{
			mMainTimeline.free();
		}
		mMainTimeline = Timeline.createParallel();
		//		mMainTimeline.push( Tween.to( movedView , View3DTweenAccessor.SCALE_XY
		//				, duration+0.2f ).target( 0 ) );
		setUser( 1 );
		//		//如果页数大于2页需要将另一页的元素往前推动
		if( getChildCount() > 1 )
		{
			//只有在第二页删除且该页只有一个元素的时候，需要将第一个页的全部元素移过来
			if( ( getCurrentPage() == 1 && curpage.getChildCount() == 1 ) || ( getCurrentPage() == 0 ) )
			{
				needNum = mColumn - ( curpage.getChildCount() - 1 );
				if( needNum > 0 )
				{
					nextPageViews = new ArrayList<View3D>();
					int realNum = Math.min( needNum , otherpage.getChildCount() );
					ArrayList<View3D> removed = new ArrayList<View3D>();
					for( int i = 0 ; i < realNum ; i++ )
					{
						View3D v = otherpage.getChildAt( i );
						removed.add( v );
					}
					for( int i = 0 ; i < realNum ; i++ )
					{
						View3D v = removed.get( i );
						v.setPosition( this.width + ( gapx + v.width ) * ( i ) , v.y );
						otherpage.removeView( removed.get( i ) );
						curpage.addView( v );
						nextPageViews.add( v );
					}
					//如果下一页已经没有应用了，则该页需要从Npage中释放掉
					if( otherpage.getChildCount() < 1 )
					{
						view_list.remove( pageOther );
						otherpage.remove();
						setCurrentPage( 0 );
					}
					else
					{//否则需要对下一页的所有元素重新排列
						measurePos( otherpage );
					}
				}
			}
		}
		int maxCell = ( (ViewGroup3D)getChildAt( getCurrentPage() ) ).getChildCount();
		int num = Math.min( maxCell , mRecentAppList.size() );
		for( int i = lastIndex + 1 ; i < num ; i++ )
		{
			mMainTimeline.push( Tween.to( mRecentAppList.get( i ) , View3DTweenAccessor.POS_XY , duration ).target( mPoints.get( i - 1 ).x , mPoints.get( i - 1 ).y ) );
		}
		if( nextPageViews != null )
		{
			for( int i = 0 ; i < nextPageViews.size() ; i++ )
				mMainTimeline
						.push( Tween.to( nextPageViews.get( i ) , View3DTweenAccessor.POS_XY , duration ).target( mPoints.get( mColumn - needNum + i ).x , mPoints.get( mColumn - needNum + i ).y ) );
		}
		mMainTimeline.push( Tween.to( this , View3DTweenAccessor.USER , duration - 0.2f ).target( 0 ) );
		mMainTimeline.start( View3DTweenAccessor.manager ).setCallback( this ).setUserData( TweenUserData_RemoveApp );
	}
	
	public void onAppReturn(
			View3D movedView )
	{
		float duration = 0.5f;
		if( mSlaveTimeLine != null )
		{
			mSlaveTimeLine.free();
		}
		mSlaveTimeLine = Timeline.createParallel();
		mSlaveTimeLine.push( Tween.to( movedView , View3DTweenAccessor.POS_XY , duration ).target( mFocusView.x , mFocusView.y ) );
		mSlaveTimeLine.push( Tween.to( movedView , View3DTweenAccessor.OPACITY , duration ).target( 1 , 0 ) );
		mSlaveTimeLine.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE )
		{
			if( source == mMainTimeline )
			{
				if( (Integer)source.getUserData() == TweenUserData_RemoveApp )
				{
					mRecentAppList.remove( mFocusView );
					Icon3D icon = (Icon3D)mMovingView;
					if( icon.mLockIcon != null )
					{
						icon.mLockIcon.getTexture().dispose();
					}
					mMovingView.remove();
					ViewGroup3D vg = (ViewGroup3D)this.getChildAt( getCurrentPage() );
					vg.removeView( mFocusView );
					mOnScrollListener.onRemoved( indexOfFocus , removedlist , mMovingView.x , mMovingView.y );
				}
				else if( (Integer)source.getUserData() == TweenUserData_RefreshApp )
				{
					this.tracker.onRefreshCompleted();
					this.execRefresh();
				}
				mMainTimeline = null;
			}
			else if( source == mSlaveTimeLine )
			{
				mSlaveTimeLine = null;
				//			mMovingView.region.getTexture().dispose();
				mMovingView.remove();
				mFocusView.show();
			}
			mOnScrollListener.setStateTipVisible( false );
		}
		if( type == TweenCallback.COMPLETE )
		{
		}
		super.onEvent( type , source );
	}
	
	public float totalDeltaX = 0;
	
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( RecentAppHolder.getState() != 0 || RecentAppPage.mMainTimeline != null || RecentAppPage.mSlaveTimeLine != null || isdragged )
			return true;
		if( this.getChildCount() == 0 )
		{
			return true;
		}
		totalDeltaX += deltaX;
		if( this.getCurrentPage() == 0 )
		{
			if( totalDeltaX >= bufferLength )
				return true;
			else if( deltaX > 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x > 0 )
				{
					for( int i = 1 ; i < this.getChildCount() ; i++ )
					{
						this.getChildAt( i ).hide();
						this.getChildAt( i ).alwaysHided = true;
					}
				}
			}
			else if( deltaX < 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x <= 0 )
				{
					for( int i = 1 ; i < this.getChildCount() ; i++ )
					{
						this.getChildAt( i ).alwaysHided = false;
						//this.getChildAt( i ).show();
					}
				}
			}
		}
		else if( ( this.getCurrentPage() == this.getChildCount() - 1 ) )
		{
			if( totalDeltaX <= -bufferLength )
			{
				for( int i = 0 ; i < this.getChildCount() - 1 ; i++ )
				{
					this.getChildAt( i ).hide();
					this.getChildAt( i ).alwaysHided = true;
				}
				return true;
			}
			else if( deltaX < 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x < 0 )
				{
					for( int i = 0 ; i < this.getChildCount() - 1 ; i++ )
					{
						this.getChildAt( i ).hide();
						this.getChildAt( i ).alwaysHided = true;
					}
				}
			}
			else if( deltaX > 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x >= 0 )
				{
					for( int i = 0 ; i < this.getChildCount() - 1 ; i++ )
					{
						this.getChildAt( i ).alwaysHided = false;
						//this.getChildAt( i ).show();
					}
				}
			}
		}
		if( !( y < ( mStartY + mStageHeight ) && y > mStartY ) )
		{
			return true;
		}
		if( ( deltaX == 0 || Math.abs( deltaY ) / Math.abs( deltaX ) > 1 ) )
		{
			moving = false;
			//return true;
		}
		else
		{
			mOnScrollListener.onPageScrolledStart();
			if( this.getChildCount() > 1 )
				onMove = true;
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( this.getChildCount() == 0 )
		{
			return true;
		}
		if( this.getCurrentPage() == 0 )
		{
			if( velocityX < 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x > 0 )
				{
					for( int i = 1 ; i < this.getChildCount() ; i++ )
					{
						this.getChildAt( i ).hide();
						this.getChildAt( i ).alwaysHided = true;
					}
				}
			}
			else if( velocityX > 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x <= 0 )
				{
					for( int i = 1 ; i < this.getChildCount() ; i++ )
					{
						this.getChildAt( i ).alwaysHided = false;
						//this.getChildAt( i ).show();
					}
				}
			}
		}
		else if( ( this.getCurrentPage() == this.getChildCount() - 1 ) )
		{
			if( velocityX < 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x < 0 )
				{
					for( int i = 0 ; i < this.getChildCount() - 1 ; i++ )
					{
						this.getChildAt( i ).hide();
						this.getChildAt( i ).alwaysHided = true;
					}
				}
			}
			else if( velocityX > 0 )
			{
				if( this.getChildAt( this.getCurrentPage() ).x >= 0 )
				{
					for( int i = 0 ; i < this.getChildCount() - 1 ; i++ )
					{
						this.getChildAt( i ).alwaysHided = false;
						//this.getChildAt( i ).show();
					}
				}
			}
		}
		if( velocityX > 0 )
		{
			if( getCurrentPage() == 0 )
			{
				return true;
			}
		}
		else
		{
			if( getCurrentPage() == getPageNum() - 1 )
			{
				return true;
			}
		}
		return super.fling( velocityX , velocityY );
	}
	
	public static interface IOnScrollListener
	{
		
		public void startRemoved();
		
		public void onLock(
				ArrayList<View3D> list ,
				float x ,
				float y );
		
		public void onDragCompleted(
				ArrayList<View3D> list ,
				float x ,
				float y );
		
		public void onRemoved(
				int index ,
				ArrayList<View3D> list ,
				float x ,
				float y );
		
		public void onDragOver(
				ArrayList<View3D> list ,
				float x ,
				float y );
		
		public void onStateChanged(
				int index ,
				int movingDir ,
				boolean curState );
		
		public void onTouchEvent(
				float x ,
				float y ,
				int point );
		
		public void onPageScrolledFinished(
				int pageIndex );
		
		public void onPageScrolledStart();
		
		public void setCurrPage(
				int index );
		
		public void setStateTipVisible(
				boolean visible );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		totalDeltaX = 0;
		//	这个地方不能请求焦点，否则Draglayout不会调用onDrop事件
		if( isdragged || pointer == 1 )
		{
			return true;
		}
		if( mMainTimeline != null || mSlaveTimeLine != null || RecentAppHolder.getState() != 0 )
		{
			this.requestFocus();
			return true;
		}
		if( y < mStartY || y > mStartY + RecentAppHolder.mStageHeight )
		{
			Workspace3D.getInstance().mRecentApplications.destory();
		}
		else
		{
			//Workspace3D.mRecentApplications.setTipIconVisible( true );
			canScroll = true;
			mOnScrollListener.onTouchEvent( x , y , 1 );
		}
		Workspace3D.getInstance().releaseFocus();
		this.requestFocus();
		if( y < ( mStartY + mStageHeight ) && y > mStartY )
		{
			//mOnScrollListener.setStateTipVisible( true );
			return super.onTouchDown( x , y , pointer );
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( isdragged || pointer == 1 || mMainTimeline != null || mSlaveTimeLine != null || RecentAppHolder.getState() != 0 )
		{
			return true;
		}
		canScroll = false;
		mOnScrollListener.onTouchEvent( x , y , 2 );
		//onMove = false;
		//if(y<(mStartY+mStageHeight)&&y>mStartY){
		return super.onTouchUp( x , y , pointer );
		//}
		//return true;
	}
	
	public int getPageCount(
			ViewGroup3D vg )
	{
		int num = 0;
		for( int i = 0 ; i < vg.getChildCount() ; i++ )
		{
			if( vg.getChildAt( i ) instanceof ViewGroup3D )
			{
				num++;
			}
		}
		return num;
	}
	
	public int getChildCount()
	{
		int num = 0;
		for( int i = 0 ; i < children.size() ; i++ )
		{
			if( children.get( i ) != mMovingView )
			{
				num++;
			}
		}
		return num;
	}
	
	public void measurePos(
			ViewGroup3D vg )
	{
		for( int j = 0 ; j < vg.getChildCount() ; j++ )
		{
			float posx = mStartX + gapx + ( gapx + cellWidth ) * ( j % mColumn );
			float posy = mStartY + gapy + ( gapy + cellHeight ) * ( 1 - j ) % mRow;
			vg.getChildAt( j ).setPosition( posx , posy );
		}
	}
	
	protected void finishAutoEffect()
	{
		super.finishAutoEffect();
		onMove = false;
		//mOnScrollListener.onPageScrolledFinished( getCurrentPage() );
	}
	
	public void setCurrentPage(
			int index )
	{
		super.setCurrentPage( index );
		//mOnScrollListener.setCurrPage( index );
	}
	
	public void setLocked(
			boolean isLocked )
	{
		if( mFocusView != null )
			( (Icon3D)mFocusView ).isLocked = isLocked;
	}
}
