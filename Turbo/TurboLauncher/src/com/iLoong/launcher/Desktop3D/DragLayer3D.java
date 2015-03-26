package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.recent.RecentApp;
import com.iLoong.launcher.recent.RecentAppHolder;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;


public class DragLayer3D extends ViewGroup3D
{
	
	String TAG = "DragLayer3D";
	public static final int MSG_DRAG_END = 0;
	public static final int MSG_DRAG_OVER = 1;
	public static final int MSG_DRAG_INBORDER = 2;
	public static final int MSG_DRAG_MOVE_2_WORKSPACE_FROM_APPLIST = 3;// xiatian
																		// add
																		// //for
																		// mainmenu
																		// sort
																		// by
																		// user
	public DragView3D dragView = new DragView3D( "dragview" );
	private ArrayList<DropTarget3D> dropTargets = new ArrayList<DropTarget3D>();
	private ArrayList<View3D> dragList = new ArrayList<View3D>();
	private float dropX , dropY;
	public static float dragStartX = -1f , dragStartY = -1f;
	public float offsetX = -1f , offsetY = -1f;
	public boolean draging = false;
	private DropTarget3D overTarget = null;
	private NinePatch moveToLeft = new NinePatch( R3D.getTextureRegion( "move_to_left_screen_bar_bg" ) , 0 , 0 , 30 , 30 );
	private NinePatch moveToRight = new NinePatch( R3D.getTextureRegion( "move_to_right_screen_bar_bg" ) , 0 , 0 , 30 , 30 );
	private float borderWidth = R3D.getInteger( "drag_border_width" );
	private float borderHeight = Utils3D.getScreenHeight() / 5 * 4;
	private int showBorder = 0;
	private long borderStayTime = 0;
	private float borderOpcity = 0f;
	private boolean dismissUp = false;
	private float lastX = 0;
	private float lastY = 0;
	// teapotXu add start for Folder in Mainmenu
	public boolean is_dragging_in_apphost = false;
	// teapotXu add end for Folder in Mainmenu
	/************************ added by zhenNan.ye begin *************************/
	private float last_x = 0;
	private float last_y = 0;
	/************************ added by zhenNan.ye end ***************************/
	private Timer timer = null;
	private TimerTask task = null;
	float mRealPostionY = 0;
	private float mDeltaY = 0;
	//这个是fling的速度
	protected float mVelocityY;
	protected float mVelocityX;
	//在屏幕上滑行的距离和屏幕高度的比例
	protected float mScaleY = 0f;
	protected boolean isRecycled = false;
	private float mRadians = 0;
	private float[] targetXY;
	private float mDegree = 1;
	
	public DragLayer3D(
			String name )
	{
		super( name );
		dragView.transform = true;
		dragView.color.a = 0.8f;
	}
	
	public ArrayList<View3D> getDragList()
	{
		return dragList;
	}
	
	public void startDrag(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		dragList.clear();
		removeAllViews();
		for( int i = 0 ; i < list.size() ; i++ )
		{
			View3D view = list.get( i );
			if( view.getParent() != null )
			{
				view.toAbsoluteCoords( point );
				view.x = point.x;
				view.y = point.y;
			}
			view.remove();
			view.isDragging = true;
			dragList.add( view );
		}
		list.clear();
		createDragView( x , y );
		draging = true;
	}
	
	private void createDragView(
			float x ,
			float y )
	{
		if( this.dragView.getChildCount() > 0 )
		{
			this.dragView.removeAllViews();
		}
		int offsetX = R3D.workspace_multiviews_offset;
		int offsetY = R3D.workspace_multiviews_offset;
		int size = dragList.size();
		View3D view0 = dragList.get( 0 );
		dragView.setSize( view0.width + ( size - 1 ) * offsetX , view0.height + ( size - 1 ) * offsetY );
		dragView.setOrigin( dragView.width / 2 , dragView.height / 2 );
		dragView.x = x;
		dragView.y = y;
		if( DefaultLayout.rapidly_remove_shortcut )
		{
			mDeltaY = 0;
			mVelocityX = 1000;
			mVelocityY = 1000;
			isRecycled = false;
			dragView.color.a = 1;
			mRadians = 0;
			mDegree = 1;
		}
		vecY = mRealPostionY = y;
		addView( dragView );
		for( View3D view : dragList )
		{
			float tx = ( size - 1 - dragView.getChildCount() ) * offsetX;
			float ty = ( size - 1 - dragView.getChildCount() ) * offsetY;
			view.x = view.x - dragView.x;
			view.y = view.y - dragView.y;
			view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , tx , ty , 0 );
			dragView.addView( view );
		}
		if( Workspace3D.isRecentAppVisible() )
		{
			return;
		}
		if( dragView.getChildCount() == 1 )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				if( DesktopEditHost.curPopMenuStyle == DesktopEditHost.POP_MENU_STYLE_4X1 )
				{
					dragView.setScale( DesktopEditHost.scaleFactor * 0.6f , DesktopEditHost.scaleFactor * 0.6f );
					dragView.startTween( View3DTweenAccessor.SCALE_XY , Elastic.OUT , 0.8f , DesktopEditHost.scaleFactor , DesktopEditHost.scaleFactor , 0f );
				}
				else
				{
					dragView.setScale( DesktopEditHost.scaleFactor2 * 0.6f , DesktopEditHost.scaleFactor2 * 0.6f );
					dragView.startTween( View3DTweenAccessor.SCALE_XY , Elastic.OUT , 0.8f , DesktopEditHost.scaleFactor2 , DesktopEditHost.scaleFactor2 , 0f );
				}
			}
			else
			{
				dragView.setScale( 0.8f , 0.8f );
				dragView.startTween( View3DTweenAccessor.SCALE_XY , Elastic.OUT , 0.8f , 1 , 1 , 0f );
			}
		}
	}
	
	public void addDropTarget(
			DropTarget3D target )
	{
		if( !dropTargets.contains( target ) )
		{
			this.dropTargets.add( target );
		}
	}
	
	public void addDropTargetBefore(
			DropTarget3D before ,
			DropTarget3D target )
	{
		if( !dropTargets.contains( target ) )
		{
			int index = dropTargets.indexOf( before );
			dropTargets.add( index , target );
		}
	}
	
	public void removeDropTarget(
			DropTarget3D target )
	{
		this.dropTargets.remove( target );
	}
	
	private DropTarget3D dropTarget(
			float x ,
			float y )
	{
		int count = this.dropTargets.size();
		for( int i = count - 1 ; i >= 0 ; i-- )
		{
			DropTarget3D target = dropTargets.get( i );
			if( target.pointerInAbs( x , y ) && target.isVisibleInParent() )
				if( target.onDrop( getDragList() , x , y ) )
				{
					return target;
				}
		}
		return null;
	}
	
	public float[] getTargetXY()
	{
		if( targetXY == null )
		{
			targetXY = new float[2];
			targetXY[1] = this.y + this.height;
			targetXY[0] = this.x + this.width;
		}
		return targetXY;
	}
	
	public DropTarget3D onDrop()
	{
		/************************ added by zhenNan.ye begin ***************************/
		View3D particleView = dragList.get( 0 );
		/************************ added by zhenNan.ye end ***************************/
		DropTarget3D result;
		for( View3D view : dragList )
		{
			view.stopTween();
			view.isDragging = false;
		}
		if( DefaultLayout.rapidly_remove_shortcut && !Workspace3D.isRecentAppVisible() )
		{
			if( !iLoongLauncher.getInstance().d3dListener.pageContainer.isVisible() )
			{
				if( recycle() )
				{
					if( Workspace3D.getInstance().isVisible() )
					{
						Workspace3D.getInstance().getCurrentCellLayout().onDropLeave();
					}
					return Desktop3DListener.root.trashIcon;
				}
			}
		}
		result = dropTarget( dropX , dropY );
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				particleView.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_START_DRAG );
				if( result instanceof FolderIcon3D || result instanceof HotSeat3D )
				{
					particleView.particleCanRender = false;
					particleView.particleType = null;
					particleView.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
				}
				else
				{
					particleView.pauseParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		if( Workspace3D.isRecentAppVisible() )
			Workspace3D.mRecentApplications.mOnScrollListener.onDragCompleted( getDragList() , dropX , dropY );
		return result;
	}
	
	public DropTarget3D dropTargetOver(
			float x ,
			float y )
	{
		int count = this.dropTargets.size();
		for( int i = count - 1 ; i >= 0 ; i-- )
		{
			DropTarget3D target = dropTargets.get( i );
			if( target.pointerInAbs( x , y ) && target.isVisibleInParent() )
				if( target.onDropOver( dragList , x , y ) )
				{
					return target;
				}
		}
		return null;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		this.pointer = pointer;
		lastX = x;
		lastY = y;
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
	
	float vecY;
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( Workspace3D.isRecentAppVisible() )
		{
			if( this.pointer == 1 )
				return true;
		}
		if( !Workspace3D.isMoved )
		{
			return true;
		}
		mDeltaY -= deltaY;
		float curX = x /* + deltaX */;
		float curY = y /*- deltaY*/;
		mRealPostionY -= deltaY;
		if( DefaultLayout.rapidly_remove_shortcut )
		{
			//计算出当前滑动的距离和整个屏幕的比例
			mScaleY = ( Math.abs( mDeltaY ) / this.height );
		}
		if( Workspace3D.isRecentAppVisible() )
		{
			if( vecY > RecentAppHolder.mTouchableHeight )
			{
				Workspace3D.mRecentApplications.mLastY = RecentAppHolder.mTouchableHeight;
				dragView.setPosition( Workspace3D.mRecentApplications.mLastX , Workspace3D.mRecentApplications.mLastY );
				return true;
			}
			else if( vecY <= RecentAppHolder.mTouchableLow )
			{
				dragView.setPosition( Workspace3D.mRecentApplications.mLastX , Workspace3D.mRecentApplications.mLastY );
				Workspace3D.mRecentApplications.mOnScrollListener.onLock( getDragList() , dropX , vecY );
				Workspace3D.mRecentApplications.mLastY = RecentAppHolder.mTouchableLow;
				return true;
			}
			if( dragView.getChildAt( 0 ) instanceof RecentApp )
			{
				float a = 0.8f;
				float orgy = ( (Icon3D)dragView.getChildAt( 0 ) ).recentOrgPos.y;
				float distance = RecentAppHolder.mTouchableHeight - orgy;
				if( mDeltaY > 0 )
				{
					if( mDeltaY > distance )
					{
						mDeltaY = distance;
					}
					a = ( distance - mDeltaY ) / ( distance ) * 0.8f;
					( (Icon3D)dragView.getChildAt( 0 ) ).color.a = 0.2f + Math.abs( a );
				}
				curX = ( (Icon3D)dragView.getChildAt( 0 ) ).recentOrgPos.x;
			}
			Workspace3D.mRecentApplications.mLastX = curX;
			//entry the buffer district
			if( mRealPostionY < Workspace3D.mRecentApplications.mBufferLineBelow )
			{
				Workspace3D.mRecentApplications.mLastY = vecY = Workspace3D.mRecentApplications.mBufferLineBelow - ( Workspace3D.mRecentApplications.mBufferLineBelow - mRealPostionY ) / 3;
			}
			else if( mRealPostionY > Workspace3D.mRecentApplications.mBufferLineUp )
			{
				Workspace3D.mRecentApplications.mLastY = vecY = Workspace3D.mRecentApplications.mBufferLineUp + ( mRealPostionY - Workspace3D.mRecentApplications.mBufferLineUp ) / 3;
			}
			else
			{
				vecY = Workspace3D.mRecentApplications.mLastY = mRealPostionY;
			}
		}
		lastX = x;
		lastY = y;
		if( offsetX == -1 || offsetY == -1 )
		{
			offsetX = dragStartX - dragView.x;
			offsetY = dragStartY - dragView.y;
		}
		if( Workspace3D.isRecentAppVisible() )
		{
			dragView.setPosition( curX , vecY );
		}
		else
		{
			dragView.setPosition( curX - offsetX , curY - offsetY );
		}
		DropTarget3D target = null;
		if( Workspace3D.isRecentAppVisible() )
			target = dropTargetOver( curX , vecY );
		else
			target = dropTargetOver( curX , curY );
		if( overTarget != target )
		{
			overTarget = target;
			this.setTag( overTarget );
			viewParent.onCtrlEvent( this , MSG_DRAG_OVER );
		}
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				View3D particleView = dragList.get( 0 );
				if( Math.abs( x - last_x ) > 10 || Math.abs( y - last_y ) > 10 )
				{
					float positionX , positionY;
					Vector2 point = new Vector2();
					particleView.toAbsoluteCoords( point );
					if( dragList.size() > 1 )
					{
						float iconHeight = Utils3D.getIconBmpHeight();
						positionX = point.x + particleView.width / 2;
						positionY = point.y + ( particleView.height - iconHeight ) + iconHeight / 2;
					}
					else
					{
						if( particleView instanceof WidgetView || particleView instanceof Widget3D || particleView instanceof Widget )
						{
							positionX = point.x + particleView.width / 2;
							positionY = point.y + particleView.height / 2;
						}
						else
						{
							float iconHeight = Utils3D.getIconBmpHeight();
							positionX = point.x + particleView.width / 2;
							positionY = point.y + ( particleView.height - iconHeight ) + iconHeight / 2;
						}
					}
					particleView.updateParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG , positionX , positionY );
					last_x = x;
					last_y = y;
				}
				else
				{
					particleView.pauseParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		View3D parent = this.getParent();
		if( parent instanceof Root3D )
		{
			Root3D root = (Root3D)parent;
			// teapotXu add start for Folder in Mainmenu
			// when folder in Mainmenu is open, no need to send
			// MSG_DRAG_INBORDER, wait folder closed
			if( root.folderOpened || Root3D.appHost.folderOpened || Workspace3D.isRecentAppVisible() || root.newsHandle.isDragging )
			{
				// if(root.folderOpened ){
				// teapotXu add end for Folder in Mainmenu
				borderStayTime = 0;
				showBorder = 0;
				return true;
			}
			else if( root.isPageContainerVisible() )
			{
				borderStayTime = 0;
				showBorder = 0;
				return false;
			}
			else
			{
				float px = curX , py = curY;
				if( ( curX > Utils3D.getScreenWidth() / 2 && offsetX < dragView.width / 2 ) || ( curX < Utils3D.getScreenWidth() / 2 && offsetX > dragView.width / 2 ) )
				{
					px = dragView.x + dragView.width / 2;
				}
				if( inBorder( px , py ) )
				{
					if( DefaultLayout.mainmenu_folder_function && iLoongLauncher.getInstance().getD3dListener().getRoot().getAppHost().isVisible() == true && is_dragging_in_apphost == true )
					{
						if( borderStayTime == 0 )
						{
							borderStayTime = System.currentTimeMillis();
						}
						else
						{
							if( System.currentTimeMillis() - borderStayTime > 500 )
							{
								viewParent.onCtrlEvent( this , MSG_DRAG_INBORDER );
							}
						}
					}
					else
					{
						if( !DefaultLayout.page_container_shown )// teapotXu add
																	// for donot
																	// show
																	// pageContainer
						{
							if( Root3D.isDragAutoEffect )
								return true;
							final Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
							if( getBorder() == -1 )
							{
								if( task == null )
								{
									task = new TimerTask() {
										
										@Override
										public void run()
										{
											if( workspace.WorkspaceNeedDragAutoEffect( getBorder() ) )
											{
												workspace.startAutoEffect( false );
											}
										}
									};
								}
							}
							else if( getBorder() == 1 )
							{
								if( task == null )
								{
									task = new TimerTask() {
										
										@Override
										public void run()
										{
											if( workspace.WorkspaceNeedDragAutoEffect( getBorder() ) )
											{
												workspace.startAutoEffect( true );
											}
										}
									};
								}
							}
							if( timer == null )
							{
								timer = new Timer();
								timer.schedule( task , 570 , 570 + (int)( DefaultLayout.page_tween_time * 1000 ) );
							}
						}
						else
						{
							if( borderStayTime == 0 )
							{
								borderStayTime = System.currentTimeMillis();
							}
							else
							{
								if( System.currentTimeMillis() - borderStayTime > 500 )
								{
									viewParent.onCtrlEvent( this , MSG_DRAG_INBORDER );
								}
							}
						}
					}
					if( borderOpcity == 0f )
					{
						this.startTween( View3DTweenAccessor.USER , Cubic.INOUT , 0.5f , 1f , 0f , 0f );
					}
					else
					{
						borderOpcity = 1f;
					}
				}
				else
				{
					if( borderOpcity == 1f )
					{
						this.startTween( View3DTweenAccessor.USER , Cubic.INOUT , 0.5f , 0f , 0f , 0f ).setCallback( this );
					}
					else
					{
						showBorder = 0;
					}
					borderStayTime = 0;
					if( timer != null )
					{
						timer.cancel();
						timer = null;
					}
					if( task != null )
					{
						task.cancel();
						task = null;
					}
				}
				// xiatian add start //for mainmenu sort by user
				if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( overTarget instanceof AppHost3D ) )
				{
					AppHost3D mAppHost3D = (AppHost3D)overTarget;
					if( AppHost3D.mMoveDrag2Workspace )
					{
						AppHost3D.mMoveDrag2Workspace = false;
						viewParent.onCtrlEvent( this , MSG_DRAG_MOVE_2_WORKSPACE_FROM_APPLIST );
					}
				}
				// xiatian add end
				return true;
			}
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	int pointer = 0;
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		this.pointer = pointer;
		vecY = 0;
		if( pointer > 0 )
			return true;
		if( dismissUp )
		{
			Log.v( "launcher" , "dismiss up" );
			dismissUp = false;
			x = lastX;
			y = lastY;
		}
		offsetX = offsetY = -1f;
		// dragView.setPosition(x - offsetX, y - offsetY);
		dropX = x;
		dropY = y;
		this.setColor( color.r , color.g , color.b , color.a );
		this.viewParent.onCtrlEvent( this , MSG_DRAG_END );
		draging = false;
		overTarget = null;
		showBorder = 0;
		if( timer != null )
		{
			timer.cancel();
			timer = null;
		}
		if( task != null )
		{
			task.cancel();
			task = null;
		}
		return true;// super.onTouchUp(x, y, pointer);
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( DefaultLayout.rapidly_remove_shortcut && !Workspace3D.isRecentAppVisible() )
		{
			mVelocityY = velocityY;
			mVelocityX = velocityX;
			mRadians = (float)Math.atan( ( mVelocityY / mVelocityX ) );//(float)( Math.atan((velocityY/velocityX) )* (180/Math.PI));
			Log.v( TAG , "mAngle.. " + mRadians + " ,mVelocityX " + mVelocityX + " ,mVelocityY " + mVelocityY );
		}
		return true;
	}
	
	public boolean setTargetXY()
	{
		if( !( mVelocityX < 0 && mVelocityY < 0 || mVelocityX > 0 && mVelocityY < 0 ) )
		{
			return false;
		}
		if( targetXY == null )
		{
			targetXY = new float[2];
			targetXY[0] = 0;
			targetXY[1] = 0;
		}
		if( mRadians > 0 )
		{//方向是左上角
			float distanceX = dropX;
			float distanceY = (float)( distanceX * Math.tan( mRadians ) );
			targetXY[0] = 0;
			targetXY[1] = dropY + distanceY;
		}
		else
		{//方向是右上角
			float distanceX = this.width - dropX;
			float distanceY = (float)Math.abs( distanceX * Math.tan( mRadians ) );
			targetXY[0] = this.width;
			targetXY[1] = dropY + distanceY;
		}
		if( targetXY[1] < this.height - dragView.height / 2 )
		{
			Log.v( TAG , "mAngle return false.." );
			return false;
		}
		if( targetXY[1] > this.height )
		{
			targetXY[1] = this.height;
			float distanceY = this.height - dropY;
			float distanceX = (float)( distanceY / Math.tan( mRadians ) );
			targetXY[0] = dropX - distanceX;
		}
		return true;
	}
	
	public boolean recycle()
	{
		mDeltaY = 0;
		float density = Gdx.graphics.getDensity();
		float scroll_sensitive = DefaultLayout.npagbse_scroll_nextpage_sensitive;
		//float scroll_velocity =625 * density;
		float scroll_velocity = 10 * density;
		if( !setTargetXY() )
		{
			return false;
		}
		double angle = mRadians * ( 180 / Math.PI );
		Log.v( TAG , "angle" + angle + " ,result:" + Math.abs( mVelocityY / ( scroll_velocity * angle ) ) );
		if( Math.abs( mVelocityY / ( scroll_velocity * angle ) ) > scroll_sensitive )
		{
			isRecycled = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		return true;
	}
	
	@Override
	public void setColor(
			float r ,
			float g ,
			float b ,
			float a )
	{
		for( View3D view : dragList )
		{
			view.setColor( r , g , b , a );
		}
	}
	
	private boolean inBorder(
			float x ,
			float y )
	{
		showBorder = 0;
		if( y >= R3D.workspace_cell_height && y < ( Utils3D.getScreenHeight() - R3D.workspace_cell_height ) )
			if( x <= borderWidth * 2 )
				showBorder = -1;
			else if( x >= ( Utils3D.getScreenWidth() - borderWidth * 2 ) )
				showBorder = 1;
		return showBorder != 0;
	}
	
	public DropTarget3D getTargetOver()
	{
		return overTarget;
	}
	
	// teapotXu add start for Folder in Mainmenu
	public ArrayList<DropTarget3D> getDropTargetList()
	{
		return this.dropTargets;
	}
	
	public int getBorder()
	{
		return showBorder;
	}
	
	// teapotXu add end for Folder in Mainmenu
	public void setBorder(
			int isShow )
	{
		showBorder = isShow;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		if( showBorder != 0 )
		{
			batch.setColor( color.r , color.g , color.b , color.a * borderOpcity );
			float y;
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				borderHeight = UtilsBase.getScreenHeight() - R3D.pop_menu_height;
				y = R3D.pop_menu_height;
			}
			else
			{
				borderHeight = UtilsBase.getScreenHeight() - R3D.hot_obj_height;
				y = R3D.hot_obj_height;
			}
			if( showBorder == -1 )
			{
				if( iLoongLauncher.getInstance() != null && iLoongLauncher.getInstance().d3dListener != null && iLoongLauncher.getInstance().d3dListener.root != null && iLoongLauncher.getInstance().d3dListener.root
						.getWorkspace() != null )
				{
					iLoongLauncher.getInstance().d3dListener.root.getWorkspace().getCurrentCellLayout().cellCleanDropStatus();
					if( DefaultLayout.enable_workspace_push_icon )
					{
						CellLayout3D.cleanLastReorder();
						iLoongLauncher.getInstance().d3dListener.root.getWorkspace().getCurrentCellLayout().startReorderTween( CellLayout3D.REORDER_TWEEN_TYPE_MOVE_TO_ORI );
						Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
					}
				}
				moveToLeft.draw( batch , 0 , y , borderWidth , borderHeight );
			}
			else if( showBorder == 1 )
			{
				if( iLoongLauncher.getInstance() != null && iLoongLauncher.getInstance().d3dListener != null && iLoongLauncher.getInstance().d3dListener.root != null && iLoongLauncher.getInstance().d3dListener.root
						.getWorkspace() != null )
				{
					iLoongLauncher.getInstance().d3dListener.root.getWorkspace().getCurrentCellLayout().cellCleanDropStatus();
					if( DefaultLayout.enable_workspace_push_icon )
					{
						CellLayout3D.cleanLastReorder();
						iLoongLauncher.getInstance().d3dListener.root.getWorkspace().getCurrentCellLayout().startReorderTween( CellLayout3D.REORDER_TWEEN_TYPE_MOVE_TO_ORI );
						Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
					}
				}
				moveToRight.draw( batch , Utils3D.getScreenWidth() - borderWidth , y , borderWidth , borderHeight );
			}
		}
	}
	
	@Override
	public void setUser(
			float value )
	{
		this.borderOpcity = value;
	}
	
	@Override
	public float getUser()
	{
		return this.borderOpcity;
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == this.getTween() && type == TweenCallback.COMPLETE )
		{
			showBorder = 0;
			this.stopTween();
		}
	}
	
	public void forceTouchUp()
	{
		if( draging )
		{
			dismissUp = true;
		}
	}
	
	public void onResume()
	{
		if( draging )
		{
			Log.v( "DragLayer" , "onResume" );
			dismissUp = true;
			onTouchUp( 0 , 0 , 0 );
		}
	}
}
