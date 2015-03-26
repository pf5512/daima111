package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class GridView3D extends ViewGroup3D
{
	
	public static final int MSG_VIEW_OUTREGION = 0;
	public static final int MSG_VIEW_MOVED = 1;
	public static final int MSG_CLOSE = 2;
	public static final int MSG_VIEW_OUTREGION_DRAG = 3;
	public static final int MSG_VIEW_TOUCH_UP = 4;
	public static final int MSG_GRID_VIEW_REQUEST_FOCUS = 5;
	private View3D mFocus;
	private int mPosx;
	private int mPosy;
	private int mCountX;
	private int mCountY;
	private int mGapX;
	private int mGapY;
	private int mCellWidth;
	private int mCellHeight;
	private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingTop;
	private int mPaddingBottom;
	private boolean animation_flag = true;
	private float animation_delay = 0f;
	private boolean auto_focus = true;
	Timeline animation_line = null;
	Tween animation_focus = null;
	/************************ added by zhenNan.ye begin *************************/
	private float last_x = 0;
	private float last_y = 0;
	
	/************************ added by zhenNan.ye end ***************************/
	public GridView3D(
			String name ,
			float width ,
			float height ,
			int countx ,
			int county )
	{
		super( name );
		this.width = width;
		this.height = height;
		this.setOrigin( width / 2 , height / 2 );
		mCountX = countx;
		mCountY = county;
		mPaddingLeft = 0;
		mPaddingRight = 0;
		mPaddingTop = 0;
		mPaddingBottom = 0;
		mCellWidth = (int)( ( this.width - mPaddingLeft - mPaddingRight ) / mCountX );
		mCellHeight = (int)( ( this.height - mPaddingTop - mPaddingBottom ) / mCountY );
	}
	
	public void onThemeChanged()
	{
		this.setOrigin( width / 2 , height / 2 );
		for( int j = 0 ; j < getChildCount() ; j++ )
		{
			View3D view = getChildAt( j );
			if( view == null )
				continue;
			if( view instanceof Icon3D )
			{
				( (Icon3D)view ).onThemeChanged();
			}
			else if( view instanceof FolderIcon3D )
			{
				FolderIcon3D oldFolder = (FolderIcon3D)view;
				oldFolder.onThemeChanged();
			}
		}
	}
	
	public void setAnimationDelay(
			float delay )
	{
		animation_delay = delay;
	}
	
	public void stopAnimation()
	{
		if( animation_line != null && !animation_line.isFinished() )
		{
			animation_line.free();
			animation_line = null;
		}
		if( animation_focus != null && !animation_focus.isFinished() )
		{
			// animation_focus.kill();
			if( mFocus != null )
				mFocus.stopTween();
			animation_focus = null;
		}
	}
	
	public void enableAnimation(
			boolean open_anim )
	{
		animation_flag = open_anim;
		if( animation_flag == false )
		{
			stopAnimation();
			layout( 0 );
		}
	}
	
	public boolean getAnimationFlag()
	{
		return animation_flag;
	}
	
	public void addItem(
			View3D child )
	{
		addView( child );
		layout( 0 );
	}
	
	public void addItem(
			List<View3D> child_list )
	{
		for( View3D i : child_list )
		{
			addView( i );
		}
		layout( 0 );
	}
	
	public void addItem(
			View3D[] child_list )
	{
		for( View3D i : child_list )
		{
			addView( i );
		}
		layout( 0 );
	}
	
	public void addItem(
			View3D child ,
			int index )
	{
		addViewAt( index , child );
		layout( 0 );
	}
	
	public void setPadding(
			int left ,
			int right ,
			int top ,
			int bottom )
	{
		mPaddingLeft = left;
		mPaddingRight = right;
		mPaddingTop = top;
		mPaddingBottom = bottom;
		mCellWidth = (int)( ( this.width - mPaddingLeft - mPaddingRight ) / mCountX );
		mCellHeight = (int)( ( this.height - mPaddingTop - mPaddingBottom ) / mCountY );
	}
	
	public void setSize(
			int width ,
			int height )
	{
		if( width == this.width && height == this.height )
			return;
		this.width = width;
		this.height = height;
		layout( 0 );
	}
	
	public void setCellCount(
			int countx ,
			int county )
	{
		if( countx == mCountX && county == mCountY )
			return;
		mCountX = countx;
		mCountY = county;
		layout( 0 );
	}
	
	public int getCellCountX()
	{
		return mCountX;
	}
	
	public int getCellCountY()
	{
		return mCountY;
	}
	
	public int getCellWidth()
	{
		return mCellWidth;
	}
	
	public int getCellHeight()
	{
		return mCellHeight;
	}
	
	public int getPaddingTop()
	{
		return mPaddingTop;
	}
	
	public void setAutoDrag(
			boolean drag )
	{
		auto_focus = drag;
	}
	
	public void layout_pub(
			int index ,
			boolean animation )
	{
		animation_flag = animation;
		layout( index );
	}
	
	public boolean testFull()
	{
		return this.getChildCount() == mCountX * mCountY;
	}
	
	public void addItem_hotdock(
			View3D child )
	{
		addView( child );
		layout_hotdock( child , 0 );
	}
	
	public void addItem_hotdock(
			View3D child ,
			int index )
	{
		addViewAt( index , child );
		layout_hotdock( child , 0 );
	}
	
	private void layout_hotdock(
			View3D child ,
			int index )
	{
		if( mCountX == 0 || mCountY == 0 )
			return;
		View3D cur_view;
		mCellWidth = (int)( ( this.width - mPaddingLeft - mPaddingRight ) / mCountX );
		mCellHeight = (int)( ( this.height - mPaddingTop - mPaddingBottom ) / mCountY );
		mGapX = 0;
		mGapY = 0;
		if( getChildCount() > 0 )
		{
			cur_view = getChildAt( 0 );
			if( mCountX > 1 )
				mGapX = (int)0;
			mGapY = (int)( ( mCellHeight - cur_view.height ) / ( mCountY ) );
		}
		int size = getChildCount();
		if( animation_flag )
		{
			stopAnimation();
			animation_line = Timeline.createParallel();
		}
		for( int i = index ; i < size ; i++ )
		{
			cur_view = getChildAt( i );
			if( cur_view != mFocus )
			{
				getPos( i );
				if( i >= mCountX )
				{
					mPosx += this.width;
					mPosy += ( index / mCountX ) * ( mCellHeight + mGapY );
				}
				if( animation_flag && cur_view.name.equals( child.name ) )
				{
					cur_view.color.a = 0f;
					cur_view.setScale( 0 , 0 );
					cur_view.setPosition( mPosx + ( mCellWidth - cur_view.width ) / 2 , mPosy );
					if( size < R3D.hot_dock_item_num )
					{
						this.setPosition( 0 , y );
					}
					else
						this.setPosition( -( getCellWidth() * size - Utils3D.getScreenWidth() ) , y );
					animation_line.push( Tween.to( cur_view , View3DTweenAccessor.OPACITY , 0.6f ).target( 1.0f ).ease( Cubic.IN ).delay( 0.2f ) );
					animation_line.push( Tween.to( cur_view , View3DTweenAccessor.SCALE_XY , 0.6f ).target( 1.0f , 1.0f ).ease( Cubic.OUT ).delay( 0.2f ) );
				}
				else
				{
					cur_view.setPosition( mPosx + ( mCellWidth - cur_view.width ) / 2 , mPosy );
				}
			}
		}
		if( animation_flag )
			animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	private void layout(
			int index )
	{
		if( mCountX == 0 || mCountY == 0 )
			return;
		View3D cur_view;
		mCellWidth = (int)( ( this.width - mPaddingLeft - mPaddingRight ) / mCountX );
		mCellHeight = (int)( ( this.height - mPaddingTop - mPaddingBottom ) / mCountY );
		mGapX = 0;
		mGapY = 0;
		if( getChildCount() > 0 )
		{
			cur_view = getChildAt( 0 );
			if( mCountX > 1 )
				mGapX = (int)( ( mCellWidth - cur_view.width ) / ( mCountX - 1 ) );
			mGapY = (int)( ( mCellHeight - cur_view.height ) / ( mCountY ) );
		}
		int size = getChildCount();
		if( animation_flag )
		{
			stopAnimation();
			animation_line = Timeline.createParallel();
		}
		for( int i = index ; i < size ; i++ )
		{
			cur_view = getChildAt( i );
			if( cur_view != mFocus )
			{
				// old_posx = cur_view.x;
				// old_posy = cur_view.y;
				getPos( i );
				// cur_view.setPosition(mPosx, mPosy);
				if( animation_flag )
				{
					animation_line.push( Tween.to( cur_view , View3DTweenAccessor.POS_XY , 0.5f ).target( mPosx , mPosy ).ease( Cubic.OUT ).delay( i * animation_delay ) );
				}
				else
				{
					cur_view.setPosition( mPosx , mPosy );
				}
			}
		}
		if( animation_flag )
			animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public View3D getFocusView()
	{
		return mFocus;
	}
	
	public void setFocusView(
			View3D focusView )
	{
		this.mFocus = focusView;
	}
	
	private void getPos(
			int index )
	{
		// 返回值存在mPosx和mPosy
		mPosx = ( index % mCountX ) * ( mCellWidth + mGapX ) + mPaddingLeft;
		mPosy = (int)( this.height - mPaddingTop - mCellHeight ) - ( index / mCountX ) * ( mCellHeight + mGapY ) + mGapY * mCountY;
	}
	
	public int getIndex(
			int x ,
			int y )
	{
		Log.v( "click" , "GridView3D getIndex:" + name + " x:" + x + " y:" + y );
		if( x < mPaddingLeft || y < mPaddingBottom || x > this.width - mPaddingRight || y > this.height - mPaddingTop )
			return -1;
		return( ( ( (int)this.height - y - mPaddingTop - mPaddingBottom ) / mCellHeight ) * mCountX + ( x - mPaddingLeft ) / mCellWidth );
	}
	
	private void centerFocus(
			int x ,
			int y )
	{
		mFocus.setPosition( x - mFocus.width / 2 , y - mFocus.height / 2 );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( auto_focus )
		{
			animation_flag = true;
			mFocus = null;
			int index = getIndex( (int)x , (int)y );
			if( index >= 0 && index < this.getChildCount() )
			{
				SendMsgToAndroid.vibrator( R3D.vibrator_duration );
				mFocus = children.get( getIndex( (int)x , (int)y ) );// (View3D)
																		// hit(x,y);
			}
			if( mFocus == this )
				mFocus = null;
			if( mFocus != null )
			{
				stopAnimation();
				centerFocus( (int)x , (int)y );
				viewParent.onCtrlEvent( this , MSG_GRID_VIEW_REQUEST_FOCUS );
				requestFocus();
				/*
				 * 长按动画 xp_20120425
				 */
				float tempScalex = mFocus.scaleX;
				float tempScaley = mFocus.scaleY;
				mFocus.setScale( tempScalex * 0.6f , tempScaley * 0.6f );
				mFocus.startTween( View3DTweenAccessor.SCALE_XY , Elastic.OUT , 0.8f , tempScalex , tempScaley , 0f );
				// mFocus.setScale(0.6f, 0.6f);
				// mFocus.startTween(View3DTweenAccessor.SCALE_XY,Elastic.OUT,
				// 0.8f, 1f, 1f, 0f);
				// Tween.to(mFocus,View3DTweenAccessor.SCALE_XY, 0.5f)
				// .target(mFocus.width*1.2f,mFocus.height*1.2f)
				// .ease(Cubic.OUT)
				// .start(View3DTweenAccessor.manager);
				/************************ added by zhenNan.ye begin ***************************/
				if( DefaultLayout.enable_particle )
				{
					if( ParticleManager.particleManagerEnable )
					{
						float positionX , positionY;
						float iconHeight = Utils3D.getIconBmpHeight();
						Vector2 point = new Vector2();
						mFocus.toAbsoluteCoords( point );
						positionX = point.x + mFocus.width / 2;
						positionY = point.y + ( mFocus.height - iconHeight ) + iconHeight / 2;
						mFocus.startParticle( ParticleManager.PARTICLE_TYPE_NAME_START_DRAG , positionX , positionY );
					}
				}
				/************************ added by zhenNan.ye end ***************************/
				return true;
			}
		}
		return super.onLongClick( x , y );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iLoong.launcher.UI3DEngine.ViewGroup3D#scroll(float, float,
	 * float, float)
	 */
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
		if( mFocus != null )
			return true;
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	static final int MAX_X_REGION = 70;
	static final int MAX_AUTO_SCROLL_STEP = 20;
	static final int MOVE_LEFT = -1;
	static final int MOVE_RIGHT = 1;
	static final int MOVE_STOP = 0;
	private float VOLOCITY_MAX = 6.0f;
	public boolean isAutoMove = false;
	
	private void startMove(
			int dire )
	{
		Log.v( "test" , "startMove  = " + dire );
		if( dire != MOVE_STOP )
		{
			setUser( VOLOCITY_MAX * -dire );
			isAutoMove = true;
		}
		else
		{
			isAutoMove = false;
			setUser( 0 );
		}
	}
	
	public void autoMoveUpdate()
	{
		layout( 0 );
	}
	
	public boolean isScrollToEnd(
			int dire )
	{ // first or last in screen
		boolean res = false;
		if( getChildCount() == 0 )
			return res;
		if( dire == MOVE_STOP )
			return res;
		int temp = 0;
		if( dire > 0 )
			temp = MOVE_RIGHT;
		else
			temp = MOVE_LEFT;
		float mFocusViewX = this.x;
		float mFocusViewWidth = getEffectiveWidth();
		// Log.v("test", "mFocusViewWidth = " + mFocusViewWidth +
		// "mFocusViewX = " + mFocusViewX);
		switch( temp )
		{
			case MOVE_LEFT:
				if( -mFocusViewX > 30 )
					res = true;
				break;
			case MOVE_RIGHT:
				if( -mFocusViewX + Utils3D.getScreenWidth() < mFocusViewWidth - 20 )
					res = true;
				break;
			default:
				break;
		}
		if( !res )
			isAutoMove = false;/* 如果不能移动�?刚把移动的也关闭 */
		return res;
	}
	
	// @Override
	// public void draw(SpriteBatch batch, float parentAlpha) {
	// // TODO Auto-generated method stub
	// super.draw(batch, parentAlpha);
	//
	// if (isAutoMove) {
	// if (!isScrollToEnd(autoMoveDire)) {// && autoMoveTween.isFinished()
	// if (autoMoveTween != null) {
	// this.stopTween();
	// autoMoveTween = null;
	// isAutoMove = false;
	// }
	// } else {
	// // this.x += getUser();
	// }
	// }
	// }
	// zhujieping add
	public void clearFocusView()
	{
		this.mFocus = null;
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		if( mFocus != null )
		{
			boolean handled = false;
			int index = getIndex( (int)x , (int)y );
			if( index == -1 )
			{
				/************************ added by zhenNan.ye begin ***************************/
				if( DefaultLayout.enable_particle )
				{
					if( ParticleManager.particleManagerEnable )
					{
						mFocus.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_START_DRAG );
						mFocus.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
					}
				}
				/************************ added by zhenNan.ye end ***************************/
				point.x = x;
				point.y = y;
				this.toAbsolute( point );
				DragLayer3D.dragStartX = point.x;
				DragLayer3D.dragStartY = point.y;
				handled = viewParent.onCtrlEvent( this , MSG_VIEW_OUTREGION_DRAG );
				if( handled )
				{
					releaseFocus();
					mFocus = null;
				}
			}
			else
			{
				handled = viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
				Vector2 abPoint = new Vector2();
				mFocus.toAbsoluteCoords( abPoint );
				abPoint.x += mFocus.width / 2;
				if( abPoint.x < MAX_X_REGION && y < this.height )
				{
					if( isScrollToEnd( MOVE_LEFT ) )
					{
						startMove( MOVE_LEFT );
					}
					else
					{
						Log.v( "test" , "left fail " );
						startMove( MOVE_STOP );
					}
				}
				else if( abPoint.x > Utils3D.getScreenWidth() - MAX_X_REGION && y < this.height )
				{
					if( isScrollToEnd( MOVE_RIGHT ) )
					{
						startMove( MOVE_RIGHT );
					}
					else
					{
						startMove( MOVE_STOP );
					}
				}
				else
				{
					startMove( MOVE_STOP );
				}
				if( !handled )
				{
					if( index >= 0 && index < getChildCount() )
					{
						moveViewTo( mFocus , index );
						layout( 0 );
					}
				}
				/************************ added by zhenNan.ye begin ***************************/
				if( DefaultLayout.enable_particle )
				{
					if( ParticleManager.particleManagerEnable )
					{
						if( Math.abs( x - last_x ) > 10 || Math.abs( y - last_y ) > 10 )
						{
							float positionX , positionY;
							float iconHeight = Utils3D.getIconBmpHeight();
							Vector2 point = new Vector2();
							mFocus.toAbsoluteCoords( point );
							positionX = point.x + mFocus.width / 2;
							positionY = point.y + ( mFocus.height - iconHeight ) + iconHeight / 2;
							mFocus.updateParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG , positionX , positionY );
							last_x = x;
							last_y = y;
						}
						else
						{
							mFocus.pauseParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
						}
					}
				}
				/************************ added by zhenNan.ye end ***************************/
			}
			if( !handled )
				centerFocus( (int)x , (int)y );
			return true;
		}
		return super.onTouchDragged( x , y , pointer );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
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
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( isAutoMove )
		{
			startMove( MOVE_STOP );
		}
		if( mFocus != null )
		{
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					mFocus.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_START_DRAG );
					mFocus.pauseParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			boolean handled = false;
			int index = getIndex( (int)x , (int)y );
			if( index == -1 )
			{
				handled = viewParent.onCtrlEvent( this , MSG_VIEW_OUTREGION );
			}
			if( handled == false )
			{
				getPos( children.indexOf( mFocus ) );
				animation_focus = Tween.to( mFocus , View3DTweenAccessor.POS_XY , 0.5f ).target( mPosx , mPosy ).ease( Cubic.OUT );
				animation_focus.start( View3DTweenAccessor.manager ).setCallback( this );
			}
			mFocus = null;
			releaseFocus();
			viewParent.onCtrlEvent( this , MSG_VIEW_TOUCH_UP );
			return true;
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( type == TweenCallback.COMPLETE && source == animation_line )
		{
			animation_line = null;
			animation_delay = 0;
		}
		if( type == TweenCallback.COMPLETE && source == animation_focus )
		{
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					View3D view = (View3D)animation_focus.getTarget();
					Vector2 point = new Vector2();
					view.toAbsoluteCoords( point );
					float iconHeight = Utils3D.getIconBmpHeight();
					float positionX = point.x + view.width / 2;
					float positionY = point.y + ( view.height - iconHeight ) + iconHeight / 2;
					view.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP );
					view.startParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP , positionX , positionY );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			animation_focus = null;
		}
		super.onEvent( type , source );
	}
	
	public float getEffectiveWidth()
	{
		if( getChildCount() <= mCountX )
			return getChildCount() * mCellWidth + mPaddingLeft + mPaddingRight;
		return mCountX * mCellWidth + mPaddingLeft + mPaddingRight;
	}
	
	public float getEffectiveHeight()
	{
		return ( getChildCount() / mCountX + 1 ) * mCellHeight + mPaddingLeft + mPaddingRight;
	}
	
	@Override
	public void removeView(
			View3D actor )
	{
		// TODO Auto-generated method stub
		super.removeView( actor );
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( actor instanceof FolderIcon3D && ( (FolderIcon3D)actor ).is_applist_folder_no_refresh == true )
			{
				return;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		layout( 0 );
	}
	
	// xiatian add start //newHotSeatMainGroup
	public void setFocusView(
			int x ,
			int y )
	{
		int index = getIndex( (int)x , (int)y );
		if( index >= 0 && index < this.getChildCount() )
		{
			SendMsgToAndroid.vibrator( R3D.vibrator_duration );
			this.mFocus = children.get( getIndex( (int)x , (int)y ) );
		}
		else
		{
			this.mFocus = null;
		}
	}
	
	// xiatian add end
	// xiatian add start //for mainmenu sort by user
	public boolean handleGridViewTouchDrag(
			View3D dragView ,
			float x ,
			float y )
	{
		boolean handled = false;
		int index = getIndex( (int)x , (int)y );
		View3D real_view = getChildViewInGridView( dragView );
		if( real_view != null )
		{
			animation_flag = true;
			ViewGroup3D dragview_parent = real_view.getParent();
			handled = dragview_parent.onCtrlEvent( this , MSG_VIEW_MOVED );
			Vector2 abPoint = new Vector2();
			real_view.toAbsoluteCoords( abPoint );
			abPoint.x += real_view.width / 2;
			if( abPoint.x < MAX_X_REGION && y < this.height )
			{
				if( isScrollToEnd( MOVE_LEFT ) )
				{
					startMove( MOVE_LEFT );
				}
				else
				{
					Log.v( "test" , "left fail " );
					startMove( MOVE_STOP );
				}
			}
			else if( abPoint.x > Utils3D.getScreenWidth() - MAX_X_REGION && y < this.height )
			{
				if( isScrollToEnd( MOVE_RIGHT ) )
				{
					startMove( MOVE_RIGHT );
				}
				else
				{
					startMove( MOVE_STOP );
				}
			}
			else
			{
				startMove( MOVE_STOP );
			}
			if( !handled )
			{
				if( index >= 0 && index < getChildCount() )
				{
					if( real_view != null )
					{
						moveViewTo( real_view , index );
					}
					layout( 0 );
				}
			}
		}
		if( !handled )
		{
			if( real_view != null )
			{
				real_view.setPosition( x - real_view.width / 2 , y - real_view.height / 2 );
			}
		}
		return true;
	}
	
	public View3D getChildViewInGridView(
			View3D view )
	{
		View3D realView = null;
		if( view instanceof Icon3D )
		{
			realView = this.findView( view.name );
		}
		else if( view instanceof FolderIcon3D )
		{
			for( View3D child : this.children )
			{
				if( child instanceof FolderIcon3D )
				{
					if( view.name.equals( child.name ) && ( (FolderIcon3D)view ).getItemInfo().id == ( (FolderIcon3D)child ).getItemInfo().id )
					{
						realView = child;
						break;
					}
				}
			}
			if( realView == null )
			{
				Log.e( "cooee" , "GridView3D ---- handleGridViewTouchDrag ---- can not find the same FolderIcon in this GridView" );
			}
		}
		else
		{
			realView = this.findView( view.name );
		}
		return realView;
	}
	
	public boolean handleGridViewTouchDrag(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		boolean handled = false;
		int index = getIndex( (int)x , (int)y );
		for( View3D mView3D : list )
		{
			View3D real_view = getChildViewInGridView( mView3D );
			if( real_view != null )
			{
				animation_flag = true;
				ViewGroup3D dragview_parent = real_view.getParent();
				handled = dragview_parent.onCtrlEvent( this , MSG_VIEW_MOVED );
				Vector2 abPoint = new Vector2();
				real_view.toAbsoluteCoords( abPoint );
				abPoint.x += real_view.width / 2;
				if( abPoint.x < MAX_X_REGION && y < this.height )
				{
					if( isScrollToEnd( MOVE_LEFT ) )
					{
						startMove( MOVE_LEFT );
					}
					else
					{
						Log.v( "test" , "left fail " );
						startMove( MOVE_STOP );
					}
				}
				else if( abPoint.x > Utils3D.getScreenWidth() - MAX_X_REGION && y < this.height )
				{
					if( isScrollToEnd( MOVE_RIGHT ) )
					{
						startMove( MOVE_RIGHT );
					}
					else
					{
						startMove( MOVE_STOP );
					}
				}
				else
				{
					startMove( MOVE_STOP );
				}
				if( !handled )
				{
					if( index >= 0 && index < getChildCount() )
					{
						if( real_view != null )
						{
							moveViewTo( real_view , index );
						}
						layout( 0 );
					}
				}
			}
		}
		return true;
	}
	
	public Vector2 getPos(
			int index ,
			boolean notUse )
	{
		Vector2 point = new Vector2();
		point.x = ( index % mCountX ) * ( mCellWidth + mGapX ) + mPaddingLeft;
		point.y = (int)( this.height - mPaddingTop - mCellHeight ) - ( index / mCountX ) * ( mCellHeight + mGapY ) + mGapY * mCountY;
		return point;
	}
	
	// xiatian add end
	@Override
	public void releaseRegion()
	{
		// TODO Auto-generated method stub
		super.releaseRegion();
		Log.v( "" , "releaseRegion GridView3D name is " + name );
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			this.getChildAt( i ).releaseRegion();
		}
	}
}
