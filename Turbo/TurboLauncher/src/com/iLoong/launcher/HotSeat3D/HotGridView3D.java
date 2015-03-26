package com.iLoong.launcher.HotSeat3D;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.OrderedMap;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class HotGridView3D extends ViewGroup3D
{
	
	public static final int MSG_VIEW_OUTREGION = 0;
	public static final int MSG_VIEW_MOVED = 1;
	public static final int MSG_CLOSE = 2;
	public static final int MSG_VIEW_OUTREGION_DRAG = 3;
	public static final int MSG_VIEW_START_MAIN = 4;
	public static final int MSG_ADD_DRAGLAYER = 5;
	public static final int MSG_VIEW_CREATE_FOLDER = 6;
	public static final int MSG_VIEW_MERGE_FOLDER = 7;
	public static final int MSG_UPDATE_OBJ3D_SHOW_STATUS = 8;
	public static final int MSG_UPDATE_OBJ3D_INDEX_SHOW_STATUS = 9;
	public static Paint paint = new Paint();
	public static Canvas canvas = new Canvas();
	public static FontMetrics fontMetrics = new FontMetrics();
	private View3D mFocus;
	private TextureRegion frameRegion;
	private int mPosx;
	private int mPosy;
	private int mGapX;
	private int mGapY;
	private int mCountX;
	private int mCountY;
	// private int mGapX;
	// private int mGapY;
	private int mCellWidth;
	private int mCellHeight;
	private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingTop;
	private int mPaddingBottom;
	private boolean animation_flag = false;
	private float animation_delay = 0f;
	private boolean auto_focus = true;
	Timeline animation_line = null;
	Tween animation_focus = null;
	public static final int State_CreateFolder = 1;
	public static final int State_ChangePosition = 0;
	private int dragState = State_ChangePosition;
	/************************ added by zhenNan.ye begin *************************/
	private float last_x = 0;
	private float last_y = 0;
	/************************ added by zhenNan.ye end ***************************/
	private float viewWidth;
	public boolean creatFold = false;
	private boolean exchangeIndex = false;
	private int dragViewScreen = -1;
	private float mFocusToClickY;
	private int pageIndex;
	private boolean bNeedAdjustIndex;
	
	public HotGridView3D(
			String name ,
			float width ,
			float height ,
			int countx ,
			int county )
	{
		super( name );
		this.width = width;
		this.height = height;
		mCountX = countx;
		mCountY = county;
		mPaddingLeft = 0;
		mPaddingRight = 0;
		mPaddingTop = 0;
		mPaddingBottom = 0;
		mCellWidth = (int)( ( this.width - mPaddingLeft - mPaddingRight ) / mCountX );
		mCellHeight = (int)( ( this.height - mPaddingTop - mPaddingBottom ) / mCountY );
		if( frameRegion == null )
		{
			try
			{
				Bitmap frameBitmap = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/hotseatbar/dockbar_frame.png" ) );
				if( frameBitmap != null )
				{
					frameRegion = new TextureRegion( new BitmapTexture( frameBitmap , true ) );
					frameRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
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
		animation_flag = false;
		if( animation_flag == false )
		{
			// stopAnimation();
			// layout(0);
		}
	}
	
	public void addFolder(
			FolderIcon3D child )
	{
		addView( child );
	}
	
	public void addItem(
			View3D child )
	{
		addView( child );
		if( bNeedAdjustIndex )
		{
			adjustItemIndex();
			bNeedAdjustIndex = false;
		}
		layout( 0 );
	}
	
	public void addBackItem(
			View3D child )
	{
		ItemInfo backInfo = ( (IconBase3D)child ).getItemInfo();
		ItemInfo info = null;
		View3D view;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			view = getChildAt( i );
			view.stopAllTween();
			info = ( (IconBase3D)view ).getItemInfo();
			if( info.screen >= backInfo.screen && info.screen < 4 )
			{
				info.screen++;
				( (IconBase3D)view ).setItemInfo( info );
				Root3D.addOrMoveDB( info , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
			}
		}
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
		if( bNeedAdjustIndex )
		{
			adjustItemIndex();
			bNeedAdjustIndex = false;
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
		if( bNeedAdjustIndex )
		{
			adjustItemIndex();
			bNeedAdjustIndex = false;
		}
		layout( 0 );
	}
	
	public void addItem(
			View3D child ,
			int index )
	{
		// addViewAt(index,child);
		InsertHotView( child , index );
		if( bNeedAdjustIndex )
		{
			adjustItemIndex();
			bNeedAdjustIndex = false;
		}
		layout( 0 );
	}
	
	@Override
	public void addView(
			View3D actor )
	{
		super.addView( actor );
		this.viewParent.onCtrlEvent( this , MSG_UPDATE_OBJ3D_SHOW_STATUS );
		if( actor instanceof DropTarget3D )
		{
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag( actor );
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		}
	}
	
	@Override
	public void addViewAt(
			int index ,
			View3D actor )
	{
		super.addViewAt( index , actor );
		this.viewParent.onCtrlEvent( this , MSG_UPDATE_OBJ3D_SHOW_STATUS );
		if( actor instanceof DropTarget3D )
		{
			// mDragController.addDropTarget((DropTarget)child);
			this.setTag( actor );
			viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
		}
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
	}
	
	public void setSize(
			int width ,
			int height )
	{
		if( width == this.width && height == this.height )
			return;
		this.width = width;
		this.height = height;
		this.originX = width / 2;
		this.originY = height / 2;
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
	
	public int getDockBarCount(){
		
		int num=0;
		
		for(int i=0;i<this.getChildCount();i++){
			ItemInfo info = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
			if(info.container== LauncherSettings.Favorites.CONTAINER_HOTSEAT ){
				num++;
			}
		}
		
		return num;
	}
	
	public int getCellCountY()
	{
		return mCountY;
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
	
	private void layout(
			int index )
	{
		mCountX = getChildCount();
		if( mCountX == 0 || mCountY == 0 )
			return;
		View3D cur_view;
		mGapX = 0;
		mGapY = 0;
		int size = getChildCount();
		if( getChildCount() > 0 )
		{
			cur_view = getChildAt( 0 );
			viewWidth = cur_view.width;
			if( mCountX > 1 )
				mGapX = (int)( ( mCellWidth - cur_view.width ) / ( mCountX - 1 ) );
			mGapY = (int)( ( mCellHeight - cur_view.height ) / ( mCountY ) );
		}
		if( animation_flag )
		{
			stopAnimation();
			animation_line = Timeline.createParallel();
		}
		for( int i = index ; i < size ; i++ )
		{
			cur_view = getChildAt( i );
			cur_view.releaseDark();
			if( cur_view != mFocus )
			{
				getPos( cur_view );
				Log.v( "Hotseat3Dlayout" , " i: " + i + ": x = " + mPosx + " , y=" + mPosy );
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
	
	public int getFocusIndex()
	{
		return ( (IconBase3D)mFocus ).getItemInfo().screen;
	}
	
	public int getDragState()
	{
		return dragState;
	}
	
	public void getViewPos(
			View3D cur_view ,
			Vector2 IconPoint )
	{
		ItemInfo info = ( (IconBase3D)cur_view ).getItemInfo();
		getPos( cur_view , info.screen );
		IconPoint.x = mPosx;
		IconPoint.y = mPosy;
	}
	
	public void getEmptyCellIndexPos(
			int index ,
			Vector2 IconPoint ,
			boolean bFolder )
	{
		// if (index < 2) {
		// IconPoint.x = (index % mCountX) * (mCellWidth) + mPaddingLeft;
		// } else if (index == 2) {
		// IconPoint.x = (int) this.width - mPaddingRight - 2 * (mCellWidth);
		// } else {
		// IconPoint.x = (int) this.width - mPaddingRight - (mCellWidth);
		// }
		if( mCountX == 1 )
		{
			IconPoint.x = (int)( this.width - viewWidth ) / 2;
		}
		else if( mCountX == 2 )
		{
			IconPoint.x = (int)( ( index + 1 ) * ( this.width - viewWidth * 2 ) / 3 + index * viewWidth );
		}
		else if( mCountX == 3 )
		{
			IconPoint.x = (int)( ( index + 1 ) * ( this.width - viewWidth * 3 ) / 4 + index * viewWidth );
		}
		else if( mCountX == 4 )
		{
			IconPoint.x = (int)( ( index + 1 ) * ( this.width - viewWidth * 4 ) / 5 + index * viewWidth );
		}
		else if( mCountX == 5 )
		{
			IconPoint.x = (int)( ( index + 1 ) * ( this.width - viewWidth * 5 ) / 6 + index * viewWidth );
		}
		if( bFolder )
		{
			IconPoint.y = (int)mPaddingBottom;
		}
		else
		{
			IconPoint.y = (int)mPaddingBottom;
		}
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			if( R3D.workspace_cell_width > mCellWidth )
			{
				IconPoint.x -= ( R3D.workspace_cell_width - mCellWidth ) / 2.0f;
			}
			else
			{
				IconPoint.x += ( mCellWidth - R3D.workspace_cell_width ) / 2.0f;
			}
		}
	}
	
	public void getIndexPos(
			int index ,
			Vector2 IconPoint )
	{
		View3D view = findExistView( index );
		if( view == null )
		{
			IconPoint.x = 0;
			IconPoint.y = 0;
		}
		else
		{
			getPos( view , index );
			IconPoint.x = mPosx;
			IconPoint.y = mPosy;
		}
	}
	
	private void getPos(
			View3D cur_view ,
			int index )
	{
		if( cur_view == null )
		{
			mPosx = mPosy = 0;
			return;
		}
		// ItemInfo info = ((IconBase3D) cur_view).getItemInfo();
		// int index = info.screen;
		// getPos(index);
		// 返回值存在mPosx和mPosy
		// if (index < 2) {
		// mPosx = (index % mCountX) * (mCellWidth) + mPaddingLeft;
		// } else if (index == 2) {
		// mPosx = (int) this.width - mPaddingRight - 2 * (mCellWidth);
		// } else {
		// mPosx = (int) this.width - mPaddingRight - (mCellWidth);
		// }
		viewWidth = cur_view.width;
		if( mCountX == 1 )
		{
			mPosx = (int)( this.width - cur_view.width ) / 2;
		}
		else if( mCountX == 2 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - cur_view.width * 2 ) / 3 + index * cur_view.width );
		}
		else if( mCountX == 3 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - cur_view.width * 3 ) / 4 + index * cur_view.width );
		}
		else if( mCountX == 4 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - cur_view.width * 4 ) / 5 + index * cur_view.width );
		}
		else if( mCountX == 5 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - cur_view.width * 5 ) / 6 + index * cur_view.width );
		}
		// mPosx=(index%mCountX)*(mCellWidth+mGapX) + mPaddingLeft;
		if( mCountX != 0 )
		{
			mPosy = (int)( this.height - mPaddingTop - mCellHeight ) - ( index / mCountX ) * ( mCellHeight + mGapY ) + mGapY * mCountY;
		}
		// mPosy=(int) (this.height - mPaddingTop -
		// mCellHeight)-(index/mCountX)*(mCellHeight+mGapY)+mGapY*mCountY;
		//		if( cur_view instanceof FolderIcon3D )
		//		{
		//			mPosy = mPaddingBottom;
		//		}
		//		else
		//		{
		//			mPosy = (int)mPaddingBottom;
		//		}
		mPosy = (int)( ( R3D.hot_obj_height - Utils3D.getIconBmpHeight() + getIconGap() ) / 2 - getIconSpace() );
	}
	
	private void getPos(
			View3D cur_view )
	{
		ItemInfo info = ( (IconBase3D)cur_view ).getItemInfo();
		int index = info.screen;
		if( mCountX == 1 )
		{
			mPosx = (int)( mPaddingLeft + ( this.width - mPaddingLeft - mPaddingRight - viewWidth ) / 2 );
		}
		else if( mCountX == 2 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 2 ) / 3 + index * viewWidth );
		}
		else if( mCountX == 3 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 3 ) / 4 + index * viewWidth );
		}
		else if( mCountX == 4 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 4 ) / 5 + index * viewWidth );
		}
		else if( mCountX == 5 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 5 ) / 6 + index * viewWidth );
		}
		if( mCountX != 0 )
		{
			mPosy = (int)( this.height - mPaddingTop - mCellHeight ) - ( index / mCountX ) * ( mCellHeight + mGapY ) + mGapY * mCountY;
		}
		if( mFocus instanceof FolderIcon3D )
		{
			mPosy = mPaddingBottom;
		}
		mPosy = (int)( ( R3D.hot_obj_height - Utils3D.getIconBmpHeight() + getIconGap() ) / 2 - getIconSpace() );
		info.x = mPosx;
		info.y = mPosy;
		( (IconBase3D)cur_view ).setItemInfo( info );
	}
	
	/* index 输入参数表示在网格中的位置，不是在children中的index */
	private void getPos(
			int index )
	{
		if( mCountX == 1 )
		{
			mPosx = (int)( mPaddingLeft + ( this.width - mPaddingLeft - mPaddingRight - viewWidth ) / 2 );
		}
		else if( mCountX == 2 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 2 ) / 3 + index * viewWidth );
		}
		else if( mCountX == 3 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 3 ) / 4 + index * viewWidth );
		}
		else if( mCountX == 4 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 4 ) / 5 + index * viewWidth );
		}
		else if( mCountX == 5 )
		{
			mPosx = (int)( ( index + 1 ) * ( this.width - viewWidth * 5 ) / 6 + index * viewWidth );
		}
		if( mCountX != 0 )
		{
			mPosy = (int)( this.height - mPaddingTop - mCellHeight ) - ( index / mCountX ) * ( mCellHeight + mGapY ) + mGapY * mCountY;
		}
		//		if( mFocus instanceof FolderIcon3D )
		//		{
		//			mPosy = mPaddingBottom;
		//		}
		//		else
		//		{
		//			mPosy = (int)mPaddingBottom;
		//		}
		mPosy = (int)( ( R3D.hot_obj_height - Utils3D.getIconBmpHeight() + getIconGap() ) / 2 - getIconSpace() );
	}
	
	public int getIndex(
			int x ,
			int y )
	{
		if( /* y<mPaddingBottom|| */y > this.height - mPaddingTop )
		{
			setCellCount( getChildCount() , 1 );
			return -1;
		}
		return curIndex( x , y );
	}
	
	private void centerFocus(
			int x ,
			int y )
	{
		// mFocus.setPosition(x - mFocus.width/2, y - mFocus.height/2);
		//		mFocus.setPosition(x - mFocus.width / 2, y - mFocus.height / 8);
		mFocus.setPosition( x - mFocus.width / 2 , y - mFocusToClickY );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		mCountX = getChildCount();
		int retIndex = clickIcon( (int)x , (int)y );
		if( mCountX <= 0 || retIndex < 0 )
		{
			return true;
		}
		View3D child = children.get( retIndex );
		//		if (child != null) {
		//			String iconName = iLoongLauncher.getInstance().getResources()
		//					.getString(R.string.mainmenu);
		//			if (child.name.equals(iconName)) {
		//				this.viewParent.onCtrlEvent(this,
		//						HotGridView3D.MSG_VIEW_START_MAIN);
		//				return true;
		//			}
		//		}
		return super.onClick( x , y );
	}
	
	public View3D hit(
			float x ,
			float y )
	{
		int len = children.size() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			View3D child = children.get( i );
			if( !child.visible )
				continue;
			// toChildCoordinates(child, x, y, point);
			if( child.pointerInParent( x , y ) )
			{
				return child;
			}
		}
		return null;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( auto_focus )
		{
			animation_flag = false;
			mFocus = null;
			mFocus = (View3D)hit( x , y );
			if( mFocus == this )
				mFocus = null;
			if( mFocus != null )
			{
				SendMsgToAndroid.vibrator( R3D.vibrator_duration );
				stopAnimation();
				// centerFocus((int)x,(int)y);
				requestFocus();
				/*
				 * 长按动画 xp_20120425
				 */
				float tempScalex = mFocus.scaleX;
				float tempScaley = mFocus.scaleY;
				mFocus.setScale( tempScalex * 0.6f , tempScaley * 0.6f );
				mFocus.startTween( View3DTweenAccessor.SCALE_XY , Elastic.OUT , 0.8f , tempScalex , tempScaley , 0f );
				/************************ added by zhenNan.ye begin ***************************/
				if( DefaultLayout.enable_particle )
				{
					if( ParticleManager.particleManagerEnable )
					{
						float positionX , positionY;
						Vector2 point = new Vector2();
						mFocus.toAbsoluteCoords( point );
						positionX = point.x + mFocus.width / 2;
						positionY = point.y + mFocus.height / 2;
						mFocus.startParticle( ParticleManager.PARTICLE_TYPE_NAME_START_DRAG , positionX , positionY );
					}
				}
				/************************ added by zhenNan.ye end ***************************/
				mFocus.bringToFront();
				mFocusToClickY = y - mFocus.y;
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
		Log.v( "hotgridview" , "scroll" );
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
		if( bNeedAdjustIndex )
		{
			adjustItemIndex();
			bNeedAdjustIndex = false;
		}
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
	public View3D findExistView(
			int index )
	{
		//		if (creatFold == false) {
		//			Log.v("ondrop", "findExistView");
		//			return null;
		//		}
		int Count = getChildCount();
		for( int i = 0 ; i < Count ; i++ )
		{
			View3D view1 = getChildAt( i );
			// Log.v("HotObj", "findExistView view1.name ="+view1.name+
			// " view1.screen="+((IconBase3D)view1).getItemInfo().screen);
			Log.v( "ondrop" , "findExistView" + index );
			Log.v( "ondrop" , "findExistView" + ( (IconBase3D)view1 ).getItemInfo().screen );
			if( ( (IconBase3D)view1 ).getItemInfo().screen == index )
			{
				Log.v( "ondrop" , "findExistView1111111" );
				return view1;
			}
		}
		Log.v( "ondrop" , "findExistView222222222" );
		return null;
	}
	
	public void InsertHotView(
			View3D view ,
			int index )
	{
		int Count = getChildCount();
		int screenIndex = 0;
		ItemInfo itemInfo;
		if( Count > mCountX )
		{
			/* No Empty 无法添加 */
			return;
		}
		View3D FindExistView = findExistView( index );
		if( FindExistView != null )
		{
			for( int i = 0 ; i < Count ; i++ )
			{
				View3D view1 = getChildAt( i );
				itemInfo = ( (IconBase3D)view1 ).getItemInfo();
				screenIndex = itemInfo.screen;
				// Log.v("HotObj", " InsertHotView view1.name =" + view1.name
				// + " view1.screen="
				// + ((IconBase3D) view1).getItemInfo().screen);
				if( screenIndex >= index && screenIndex < ( mCountX - 2 ) )
				{
					itemInfo.screen = screenIndex + 1;
					//					itemInfo.cellX = pageNum;
					Root3D.addOrMoveDB( itemInfo , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
				}
			}
		}
		addViewAt( index , view );
	}
	
	private void changeItemCellIndex(
			View3D findView )
	{
		ItemInfo Focus_ItemInfo = ( (IconBase3D)mFocus ).getItemInfo();
		ItemInfo findView_ItemInfo = ( (IconBase3D)findView ).getItemInfo();
		int findView_screen = findView_ItemInfo.screen;
		int focus_screen = Focus_ItemInfo.screen;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View3D view = getChildAt( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			if( focus_screen < findView_screen )
			{
				if( info.screen > focus_screen && info.screen < findView_screen )
				{
					info.screen--;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
				else if( info.screen == focus_screen )
				{
					info.screen = findView_screen;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
			else
			{
				if( info.screen >= findView_screen && info.screen < focus_screen )
				{
					info.screen++;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
				else if( info.screen == focus_screen )
				{
					info.screen = findView_screen;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
		}
	}
	
	public void changeItemCellIndex(
			int findView_screen )
	{
		ItemInfo Focus_ItemInfo = ( (IconBase3D)mFocus ).getItemInfo();
		int focus_screen = Focus_ItemInfo.screen;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View3D view = getChildAt( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			if( focus_screen < findView_screen )
			{
				if( info.screen > focus_screen && info.screen <= findView_screen )
				{
					info.screen--;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
				else if( info.screen == focus_screen )
				{
					info.screen = findView_screen;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
			else
			{
				if( info.screen >= findView_screen && info.screen < focus_screen )
				{
					info.screen++;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
				else if( info.screen == focus_screen )
				{
					info.screen = findView_screen;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
		}
	}
	
	public void changeItemCellIndex(
			int findView_screen ,
			int focus_screen )
	{
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View3D view = getChildAt( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			if( focus_screen < findView_screen )
			{
				if( info.screen > focus_screen && info.screen <= findView_screen )
				{
					info.screen--;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
				else if( info.screen == focus_screen )
				{
					info.screen = findView_screen;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
			else
			{
				if( info.screen >= findView_screen && info.screen < focus_screen )
				{
					info.screen++;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
				else if( info.screen == focus_screen )
				{
					info.screen = findView_screen;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
		}
	}
	
	private void dealTouchDragged(
			float x ,
			float y )
	{
		exchangeIndex = true;
		int index = getIndex( (int)x , (int)y );
		exchangeIndex = false;
		ItemInfo Focus_ItemInfo = ( (IconBase3D)mFocus ).getItemInfo();
		View3D findView = findExistView( index );
		dragState = State_ChangePosition;
		viewParent.onCtrlEvent( this , MSG_UPDATE_OBJ3D_INDEX_SHOW_STATUS );
		if( index == Focus_ItemInfo.screen )
		{
			viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
			return;
		}
		if( findView == null )
		{
			/* Do nothing ,等到TouchUP去处理 */
			Focus_ItemInfo.screen = index;
			getPos( index );
			Focus_ItemInfo.x = mPosx;
			Focus_ItemInfo.y = mPosy;
			( (IconBase3D)mFocus ).setItemInfo( Focus_ItemInfo );
		}
		else
		{
			// Focus_ItemInfo.screen = dragIndex;
			//			if( mFocus instanceof Icon3D )
			//			{
			//				// changeItemCellIndex(findView);
			//				/* 判断移动方向 */
			//				if( Focus_ItemInfo.screen/* dragIndex */< index )
			//				{
			//					getPos( index );
			//					if( x > mPosx + mCellWidth)// * 3 / 4 )
			//					{
			//						changeItemCellIndex( findView );
			//					}
			//					else
			//					{
			//						this.setTag( findView );
			//						dragState = State_CreateFolder;
			//					}
			//				}
			//				else
			//				{
			//					getPos( index );
			//					if( x < mPosx + mCellWidth / 4 )
			//					{
			//						changeItemCellIndex( findView );
			//					}
			//					else
			//					{
			//						this.setTag( findView );
			//						dragState = State_CreateFolder;
			//					}
			//				}
			//				viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
			//			}
			//			else
			{
				changeItemCellIndex( index );
			}
		}
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
			exchangeIndex = true;
			int index = getIndex( (int)x , (int)y );
			exchangeIndex = false;
			bNeedAdjustIndex = true;
			// int index = getExchangeIndex((int) x, (int) y);
			// Log.v("HotObj", "TouchDragged index="+index);
			if( index == -1 )
			{
				/************************ added by zhenNan.ye begin ***************************/
				if( DefaultLayout.enable_particle )
				{
					if( ParticleManager.particleManagerEnable )
					{
						mFocus.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
					}
				}
				/************************ added by zhenNan.ye end ***************************/
				point.x = x;
				point.y = y;
				this.toAbsolute( point );
				DragLayer3D.dragStartX = point.x;
				DragLayer3D.dragStartY = point.y;
				dragState = State_ChangePosition;
				viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
				handled = viewParent.onCtrlEvent( this , MSG_VIEW_OUTREGION_DRAG );
				if( handled )
				{
					releaseFocus();
					mFocus = null;
				}
			}
			else if( index == -2 )
			{
				/* Do Nothing */
			}
			else
			{
				dealTouchDragged( x , y );
				Log.v( "ondrop" , "hotgridview onTouchDragged" );
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
			Log.v( "Moving" , "onTouchDragged" );
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
		Log.v( "hotgridview" , "onTouchDown" );
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
		Log.v( "hotgridview" , "onTouchUp" );
		if( isAutoMove )
		{
			startMove( MOVE_STOP );
		}
		if( mFocus != null )
		{
			//			getPos(mFocus, ((IconBase3D) mFocus).getItemInfo().screen);
			//			mFocus.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.5f,
			//					mPosx, mPosy, 0);
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					mFocus.stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DRAG );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			boolean handled = false;
			exchangeIndex = true;
			int index = getIndex( (int)x , (int)y );
			exchangeIndex = false;
			if( index == -1 )
			{
				handled = viewParent.onCtrlEvent( this , MSG_VIEW_OUTREGION );
			}
			if( handled == false )
			{
				View3D findView = findExistView( index );
				if( dragState == State_CreateFolder )
				{
					this.setTag( findView );
					if( findView instanceof FolderIcon3D )
					{
						viewParent.onCtrlEvent( this , MSG_VIEW_MERGE_FOLDER );
					}
					else
					{
						viewParent.onCtrlEvent( this , MSG_VIEW_CREATE_FOLDER );
					}
				}
				else
				{
					getPos( index );
					animation_focus = Tween.to( mFocus , View3DTweenAccessor.POS_XY , 0.5f ).target( mPosx , mPosy ).ease( Cubic.OUT );
					animation_focus.start( View3DTweenAccessor.manager );
				}
			}
			mFocus = null;
			releaseFocus();
			this.viewParent.onCtrlEvent( this , MSG_UPDATE_OBJ3D_SHOW_STATUS );
			updateItemInfoDB();
			return true;
		}
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source == animation_line )
		{
			animation_line = null;
			animation_delay = 0;
		}
		if( type == TweenCallback.COMPLETE && source == animation_focus )
		{
			animation_focus = null;
		}
		super.onEvent( type , source );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		float temp_xScale = 0;
		temp_xScale = this.getParent().getX();
		if( transform )
			applyTransform( batch );
		if( frameRegion != null )
		{
			batch.setColor( color.r , color.g , color.b , ( 1 - 2 * Math.abs( Math.abs( temp_xScale ) - 0.5f ) ) );
			batch.draw( frameRegion , 0 , R3D.hot_dock_grid_pos_y , width , height );
		}
		drawChildren( batch , parentAlpha );
		if( transform )
			resetTransform( batch );
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
		super.removeView( actor );
		this.viewParent.onCtrlEvent( this , MSG_UPDATE_OBJ3D_SHOW_STATUS );
		// layout(0);
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		if( Gdx.graphics.getDensity() > 1 )
		{
			Group.toChildCoordinates( this , x , y , point );
			float offsetY = 20 * Gdx.graphics.getDensity();
			return( ( point.x >= 0 && point.x < width ) && ( point.y >= 0 && point.y < ( height + offsetY ) ) );
		}
		else
		{
			return super.pointerInAbs( x , y );
		}
	}
	
	public void setCount(
			int num )
	{
		mCountX = getChildCount();
		if( bNeedAdjustIndex )
		{
			adjustItemIndex();
			bNeedAdjustIndex = false;
		}
		for( int i = 0 ; i < mCountX ; i++ )
		{
			View3D view = children.get( i );
			view.releaseDark();
			ItemInfo view_ItemInfo = ( (IconBase3D)view ).getItemInfo();
			getPos( view_ItemInfo.screen );
			view.stopAllTween();
			view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
			view_ItemInfo.x = mPosx;
			view_ItemInfo.y = mPosy;
			( (IconBase3D)view ).setItemInfo( view_ItemInfo );
			Root3D.addOrMoveDB( view_ItemInfo , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
		}
	}
	
	public void setReleaseDark()
	{
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View3D view = children.get( i );
			view.releaseDark();
		}
	}
	
	public int getAutoIndex(
			int x ,
			int y )
	{
		int curindex = 0;
		int num = 0;
		if( mCountX >= 5 )
			return curIndex( x , y );
		int[] indexArray = new int[]{ 0 , 1 , 2 , 3 , 4 };
		int[] existArray = new int[children.size()];
		for( int i = 0 ; i < children.size() ; i++ )
		{
			View3D view = children.get( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			existArray[i] = info.screen;
		}
		if( creatFold )
		{
			num = children.size() - 1;
			return curIndex( x , y );
		}
		else
		{
			num = children.size();
		}
		Log.v( "ondrop" , "getAutoIndex" + num + " " + mCountX + " " + children.size() + " " + creatFold );
		for( int i = 0 ; i <= num ; i++ )
		{
			int j;
			for( j = 0 ; j < children.size() ; j++ )
			{
				if( indexArray[i] == existArray[j] )
				{
					break;
				}
			}
			if( j == children.size() )
			{
				curindex = i;
				Log.v( "Hotseat3D" , "index111 : " + curindex );
				return curindex;
			}
		}
		Log.v( "Hotseat3D" , "index222 : " + curindex );
		return curindex;
	}
	
	private int curIndex(
			int x ,
			int y )
	{
		//		int curindex = 0;
		//		if( mCountX == 0 )
		//		{
		//			curindex = 0;
		//		}
		//		else if( mCountX == 1 )
		//		{
		//			if( x < (int)( this.width - viewWidth ) / 2 )
		//			{
		//				creatFold = false;
		//				curindex = 0;
		//			}
		//			else if( x > (int)( this.width - viewWidth ) / 2 + viewWidth )
		//			{
		//				creatFold = false;
		//				curindex = 1;
		//			}
		//			else
		//			{
		//				creatFold = true;
		//				curindex = 0;
		//			}
		//		}
		//		else if( mCountX == 2 )
		//		{
		//			if( x < (int)( ( this.width - viewWidth * 2 ) / 3 ) )
		//			{
		//				creatFold = false;
		//				curindex = 0;
		//			}
		//			else if( x > (int)( ( this.width - viewWidth * 2 ) / 3 ) && x < (int)( ( this.width - viewWidth * 2 ) / 3 + viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 0 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 0 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 0;
		//			}
		//			else if( x > (int)( ( this.width - viewWidth * 2 ) / 3 + viewWidth ) && x < (int)( 2 * ( this.width - viewWidth * 2 ) / 3 + viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( mFocus == null )
		//				{
		//					if( exchangeIndex && dragViewScreen == 0 )
		//					{
		//						curindex = 0;
		//					}
		//					else
		//					{
		//						curindex = 1;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 0 )
		//					{
		//						curindex = 0;
		//					}
		//					else
		//					{
		//						curindex = 1;
		//					}
		//				}
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 2 ) / 3 + viewWidth ) && x < (int)( 2 * ( this.width - viewWidth * 2 ) / 3 + 2 * viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 1 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 1 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 1;
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 2 ) / 3 + 2 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( exchangeIndex )
		//				{
		//					curindex = 1;
		//				}
		//				else
		//				{
		//					curindex = 2;
		//				}
		//			}
		//		}
		//		else if( mCountX == 3 )
		//		{
		//			if( x < (int)( ( this.width - viewWidth * 3 ) / 4 ) )
		//			{
		//				creatFold = false;
		//				curindex = 0;
		//			}
		//			else if( x > (int)( ( this.width - viewWidth * 3 ) / 4 ) && x < (int)( ( this.width - viewWidth * 3 ) / 4 + viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 0 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 0 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 0;
		//			}
		//			else if( x > (int)( ( this.width - viewWidth * 3 ) / 4 + viewWidth ) && x < (int)( ( 1 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 1 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( mFocus == null )
		//				{
		//					if( exchangeIndex && dragViewScreen == 0 )
		//					{
		//						curindex = 0;
		//					}
		//					else
		//					{
		//						curindex = 1;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 0 )
		//					{
		//						curindex = 0;
		//					}
		//					else
		//					{
		//						curindex = 1;
		//					}
		//				}
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 3 ) / 4 + viewWidth ) && x < (int)( ( 1 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 2 * viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 1 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 1 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 1;
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 3 ) / 4 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 2 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( mFocus == null )
		//				{
		//					if( exchangeIndex && dragViewScreen == 1 )
		//					{
		//						curindex = 1;
		//					}
		//					else
		//					{
		//						curindex = 2;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 1 )
		//					{
		//						curindex = 1;
		//					}
		//					else
		//					{
		//						curindex = 2;
		//					}
		//				}
		//			}
		//			else if( x > (int)( 3 * ( this.width - viewWidth * 3 ) / 4 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 3 * viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 2 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 2 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 2;
		//			}
		//			else if( x > (int)( ( 2 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 3 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( exchangeIndex )
		//				{
		//					curindex = 2;
		//				}
		//				else
		//				{
		//					curindex = 3;
		//				}
		//			}
		//		}
		//		else if( mCountX == 4 )
		//		{
		//			if( x < (int)( ( this.width - viewWidth * 4 ) / 5 ) )
		//			{
		//				creatFold = false;
		//				curindex = 0;
		//			}
		//			else if( x > (int)( ( this.width - viewWidth * 4 ) / 5 ) && x < (int)( ( this.width - viewWidth * 4 ) / 5 + viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 0 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 0 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 0;
		//			}
		//			else if( x > (int)( ( this.width - viewWidth * 4 ) / 5 + viewWidth ) && x < (int)( ( 1 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 1 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( mFocus == null )
		//				{
		//					if( exchangeIndex && dragViewScreen == 0 )
		//					{
		//						curindex = 0;
		//					}
		//					else
		//					{
		//						curindex = 1;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 0 )
		//					{
		//						curindex = 0;
		//					}
		//					else
		//					{
		//						curindex = 1;
		//					}
		//				}
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 4 ) / 5 + viewWidth ) && x < (int)( ( 1 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 2 * viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 1 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 1 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 1;
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 4 ) / 5 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 2 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( mFocus == null )
		//				{
		//					if( exchangeIndex && dragViewScreen == 1 )
		//					{
		//						curindex = 1;
		//					}
		//					else
		//					{
		//						curindex = 2;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 1 )
		//					{
		//						curindex = 1;
		//					}
		//					else
		//					{
		//						curindex = 2;
		//					}
		//				}
		//			}
		//			else if( x > (int)( 3 * ( this.width - viewWidth * 4 ) / 5 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 3 * viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 2 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 2 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 2;
		//			}
		//			else if( x > (int)( 3 * ( this.width - viewWidth * 4 ) / 5 + 3 * viewWidth ) && x < (int)( ( 3 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 3 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( mFocus == null )
		//				{
		//					if( exchangeIndex && dragViewScreen == 2 )
		//					{
		//						curindex = 2;
		//					}
		//					else
		//					{
		//						curindex = 3;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 2 )
		//					{
		//						curindex = 2;
		//					}
		//					else
		//					{
		//						curindex = 3;
		//					}
		//				}
		//			}
		//			else if( x > (int)( 4 * ( this.width - viewWidth * 4 ) / 5 + 3 * viewWidth ) && x < (int)( ( 3 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 4 * viewWidth ) )
		//			{
		//				if( mFocus == null )
		//				{
		//					if( mCountX != getChildCount() && dragViewScreen == 3 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				else
		//				{
		//					if( exchangeIndex && ( (IconBase3D)mFocus ).getItemInfo().screen == 3 )
		//					{
		//						creatFold = false;
		//					}
		//					else
		//					{
		//						creatFold = true;
		//					}
		//				}
		//				curindex = 3;
		//			}
		//			else if( x > (int)( ( 3 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 4 * viewWidth ) )
		//			{
		//				creatFold = false;
		//				if( exchangeIndex )
		//				{
		//					curindex = 3;
		//				}
		//				else
		//				{
		//					curindex = 4;
		//				}
		//			}
		//		}
		//		else if( mCountX == 5 )
		//		{
		//			if( x > (int)( ( this.width - viewWidth * 5 ) / 6 ) && x < (int)( ( this.width - viewWidth * 5 ) / 6 + viewWidth ) )
		//			{
		//				creatFold = true;
		//				curindex = 0;
		//			}
		//			else if( x > (int)( 2 * ( this.width - viewWidth * 5 ) / 6 + viewWidth ) && x < (int)( 2 * ( this.width - viewWidth * 5 ) / 6 + 2 * viewWidth ) )
		//			{
		//				creatFold = true;
		//				curindex = 1;
		//			}
		//			else if( x > (int)( 3 * ( this.width - viewWidth * 5 ) / 6 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 5 ) / 6 + 3 * viewWidth ) )
		//			{
		//				creatFold = true;
		//				curindex = 2;
		//			}
		//			else if( x > (int)( 4 * ( this.width - viewWidth * 5 ) / 6 + 3 * viewWidth ) && x < (int)( ( 3 + 1 ) * ( this.width - viewWidth * 5 ) / 6 + 4 * viewWidth ) )
		//			{
		//				creatFold = true;
		//				curindex = 3;
		//			}
		//			else if( x > (int)( 5 * ( this.width - viewWidth * 5 ) / 6 + 4 * viewWidth ) && x < (int)( ( 4 + 1 ) * ( this.width - viewWidth * 5 ) / 6 + 5 * viewWidth ) )
		//			{
		//				creatFold = true;
		//				curindex = 4;
		//			}
		//			else
		//			{
		//				creatFold = false;
		//			}
		//		}
		//		return curindex;
		return getVacantIndex( x , y );
	}
	
	private int findNearestIndex(
			float x ,
			float y )
	{
		int indX = 0;
		int indW = 0;
		float disX = Integer.MAX_VALUE;
		//首先找到与x最近的索引
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			ItemInfo info = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
			if( ( x - info.x > 0 ) && ( x - info.x < disX ) )
			{
				disX = x - info.x;
				indX = i;
			}
		}
		//找到离图片右边最近的索引
		float disW = Integer.MAX_VALUE;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			ItemInfo info = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
			if( ( info.x - ( mFocus.width + x ) > 0 ) && ( info.x - ( mFocus.width + x ) < disW ) )
			{
				disW = info.x - ( mFocus.width + x );
				indW = i;
			}
		}
		//比较这个图片是离左边的图片更近还是右边的
		if( disW < disX )
			return indW;
		else
			return indX;
	}
	
	public int getVacantIndex(
			float x ,
			float y )
	{
		int cursor = 0;
		//		int hotsetCurrCount=0;
		creatFold = false;
		float distance = Integer.MAX_VALUE;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			ItemInfo info = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
			if( mFocus != getChildAt( i ) )
			{
				if( ( x - info.x > 0 ) && ( x - info.x < distance ) )
				{
					cursor = info.screen + 1;
					distance = x - info.x;
				}
			}
			//			if(info.container==LauncherSettings.Favorites.CONTAINER_HOTSEAT){
			//				hotsetCurrCount++;
			//			}
		}
		//这种情况发生在同样是在dockbar上的元素交换位置，而不是从其他的容器加进来的新元素
		if( mFocus != null )
		{
			ItemInfo info = ( (IconBase3D)mFocus ).getItemInfo();
			if( info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				//当与后面的元素交换的时候
				if( info.screen < cursor )
				{
					cursor--;
				}
				//当与第一个元素交换的时候
				else
				{
					for( int i = 0 ; i < getChildCount() ; i++ )
					{
						ItemInfo item = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
						if( item.screen == 0 )
						{
							if( x < item.x + mFocus.width / 3 )
								cursor = 0;
							break;
						}
					}
				}
				//				if(cursor>=hotsetCurrCount)
				//					cursor=hotsetCurrCount-1;
			}
		}
		else {
//			ItemInfo item = ( (IconBase3D)HotDockGroup.outView ).getItemInfo();
//			if(item.screen<cursor&&getChildCount()>=cursor)
//				cursor--;
			
		}
		//		if(HotDockGroup.outView!=null){
		//			mFocus =null;
		//		}
		if( cursor == 5 )
			cursor = -1;
		Log.v( "revisionDockbar" , "cursor " + cursor );
		return cursor;
	}
	
	public int getCount()
	{
		return mCountX;
	}
	
	private int previewIndex = 0;
	
	public void changeIconIndex(
			float x ,
			float y )
	{
		float duration = 0.5f;
		int index = getIndex( (int)x , (int)y );
//		if( previewIndex == index )
//			return;
//		else
//			previewIndex = index;
		mCountX = getChildCount() + 1;
//		ArrayList<View3D> orderedList = new ArrayList<View3D>();
//		for( int i = 0 ; i < getChildCount() ; i++ )
//		{
//			View3D view = getChildAt( i );
//			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
//			if( info.screen >= index )
//			{
//				orderedList .add( view );
//				//info.screen++;
//			}
//			else{
//				getPos( info.screen );
//				info.x = mPosx;
//				info.y = mPosx;
//				info.angle = HotSeat3D.TYPE_WIDGET;
//				info.cellX = pageIndex;
//				view.stopAllTween();
//				view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
//			}
//			Log.v( "dealPos" , "title " + info.title + " scree: " + info.screen );
//			
//		}
		
		sort(index,this.children);
	}
	
	
	public void sort(
			int index,List<View3D> toList ){
		
		float duration=0.5f;
		int[] numbers =new int[toList.size()];
		for(int i=0;i<toList.size();i++){
			View3D view = toList.get( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			numbers[i]=info.screen;
					
					
		}
		sort(numbers);
		
		ArrayList<View3D>orderedList=new ArrayList<View3D>();
		for(int i=0;i<numbers.length;i++){
			for(int j=0;j<toList.size();j++){
				View3D view = toList.get( j );
				ItemInfo info = ( (IconBase3D)view ).getItemInfo();
				if(numbers[i]==info.screen){
					orderedList.add( view );
				}
			}
			
		}
		for(int j=0;j<orderedList.size();j++){
			View3D view = orderedList.get( j );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			if(info.screen<index){
				info.screen=j;
			}
			else{
				info.screen++;
			}
			getPos( info.screen );
			info.x = mPosx;
			info.y = mPosx;
			info.angle = HotSeat3D.TYPE_WIDGET;
			info.cellX = pageIndex;
			view.stopAllTween();
			view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
			
		}
		numbers=null;
	}
	public void sort(
			int[] a )
	{
		int temp = 0;
		for( int i = a.length - 1 ; i > 0 ; --i )
		{
			for( int j = 0 ; j < i ; ++j )
			{
				if( a[j + 1] < a[j] )
				{
					temp = a[j];
					a[j] = a[j + 1];
					a[j + 1] = temp;
				}
			}
		}
		
	}
	
//	public int getMoveIndex(int x,int y){
//		
//		int cursor = 0;
//		//		int hotsetCurrCount=0;
//		creatFold = false;
//		float distance = Integer.MAX_VALUE;
//		for( int i = 0 ; i < getChildCount() ; i++ )
//		{
//			ItemInfo info = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
//			if( mFocus != getChildAt( i ) )
//			{
//				if( ( x - info.x > 0 ) && ( x - info.x < distance ) )
//				{
//					cursor = info.screen + 1;
//					distance = x - info.x;
//				}
//			}
//			//			if(info.container==LauncherSettings.Favorites.CONTAINER_HOTSEAT){
//			//				hotsetCurrCount++;
//			//			}
//		}
//		//这种情况发生在同样是在dockbar上的元素交换位置，而不是从其他的容器加进来的新元素
//		if( mFocus != null )
//		{
//			ItemInfo info = ( (IconBase3D)mFocus ).getItemInfo();
//			if( info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
//			{
//				//当与后面的元素交换的时候
//				if( info.screen < cursor )
//				{
//					cursor--;
//				}
//				//当与第一个元素交换的时候
//				else
//				{
//					for( int i = 0 ; i < getChildCount() ; i++ )
//					{
//						ItemInfo item = ( (IconBase3D)getChildAt( i ) ).getItemInfo();
//						if( item.screen == 0 )
//						{
//							if( x < item.x + mFocus.width / 3 )
//								cursor = 0;
//							break;
//						}
//					}
//				}
//				//				if(cursor>=hotsetCurrCount)
//				//					cursor=hotsetCurrCount-1;
//			}
//		}
//		else {
//			ItemInfo item = ( (IconBase3D)HotDockGroup.outView ).getItemInfo();
//			if(item.screen<cursor)
//				cursor--;
//			
//		}
//		//		if(HotDockGroup.outView!=null){
//		//			mFocus =null;
//		//		}
//		if( cursor == 5 )
//			cursor = -1;
//		Log.v( "revisionDockbar" , "cursor " + cursor );
//		return cursor;
//	}
	
	public void MyMoveInTween(
			float x ,
			float y )
	{
		float duration = 0.5f;
		bNeedAdjustIndex = true;
		if( mCountX == 0 )
		{
		}
		else if( mCountX == 1 && children.size() > 0 )
		{
			View3D view = children.get( 0 );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			int index = getIndex( (int)x , (int)y );
			dragViewScreen = index;
			mCountX = 2;
			if( index == 0 )
			{
				if( !creatFold )
				{
					getPos( 1 );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
					info.screen = 1;
					Log.v( "ondrop" , "MyMoveInTween" + creatFold );
				}
				else
				{
					mCountX = 1;
					dealTouchDragged2( x , y );
				}
			}
			else if( index == 1 )
			{
				getPos( 0 );
				view.stopAllTween();
				view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
				info.screen = 0;
			}
			info.x = mPosx;
			info.y = mPosx;
			info.angle = HotSeat3D.TYPE_WIDGET;
			info.cellX = pageIndex;
			//			Root3D.addOrMoveDB(info,
			//					LauncherSettings.Favorites.CONTAINER_HOTSEAT);
		}
		else if( mCountX == 2 )
		{
			if( getChildCount() == 2 )
			{
				int index = getIndex( (int)x , (int)y );
				mCountX = 3;
				if( !creatFold )
				{
					dragViewScreen = index;
					for( int i = 0 ; i < getChildCount() ; i++ )
					{
						View3D view = getChildAt( i );
						ItemInfo info = ( (IconBase3D)view ).getItemInfo();
						if( index == 0 )
						{
							if( info.screen == 0 )
							{
								getPos( 1 );
								info.screen = 1;
							}
							else if( info.screen == 1 )
							{
								getPos( 2 );
								info.screen = 2;
							}
						}
						else if( index == 1 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 2 );
								info.screen = 2;
							}
						}
						else if( index == 2 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 1 );
								info.screen = 1;
							}
						}
						view.stopAllTween();
						view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
						info.x = mPosx;
						info.y = mPosx;
						info.angle = HotSeat3D.TYPE_WIDGET;
						info.cellX = pageIndex;
						( (IconBase3D)view ).setItemInfo( info );
						//						Root3D.addOrMoveDB(info,
						//								LauncherSettings.Favorites.CONTAINER_HOTSEAT);
					}
				}
				else
				{
					mCountX = 2;
					dealTouchDragged2( x , y );
				}
			}
			else
			{
				dealTouchDragged1( x , y );
			}
		}
		else if( mCountX == 3 )
		{
			if( getChildCount() == 3 )
			{
				int index = getIndex( (int)x , (int)y );
				mCountX = 4;
				if( !creatFold )
				{
					dragViewScreen = index;
					for( int i = 0 ; i < getChildCount() ; i++ )
					{
						View3D view = getChildAt( i );
						ItemInfo info = ( (IconBase3D)view ).getItemInfo();
						if( index == 0 )
						{
							if( info.screen == 0 )
							{
								getPos( 1 );
								info.screen = 1;
							}
							else if( info.screen == 1 )
							{
								getPos( 2 );
								info.screen = 2;
							}
							else if( info.screen == 2 )
							{
								getPos( 3 );
								info.screen = 3;
							}
						}
						else if( index == 1 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 2 );
								info.screen = 2;
							}
							else if( info.screen == 2 )
							{
								getPos( 3 );
								info.screen = 3;
							}
						}
						else if( index == 2 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 1 );
								info.screen = 1;
							}
							else if( info.screen == 2 )
							{
								getPos( 3 );
								info.screen = 3;
							}
						}
						else if( index == 3 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 1 );
								info.screen = 1;
							}
							else if( info.screen == 2 )
							{
								getPos( 2 );
								info.screen = 2;
							}
						}
						view.stopAllTween();
						view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
						info.x = mPosx;
						info.y = mPosx;
						info.angle = HotSeat3D.TYPE_WIDGET;
						info.cellX = pageIndex;
						( (IconBase3D)view ).setItemInfo( info );
						//						Root3D.addOrMoveDB(info,
						//								LauncherSettings.Favorites.CONTAINER_HOTSEAT);
					}
				}
				else
				{
					mCountX = 3;
					dealTouchDragged2( x , y );
				}
			}
			else
			{
				dealTouchDragged1( x , y );
			}
		}
		else if( mCountX == 4 )
		{
			if( getChildCount() == 4 )
			{
				int index = getIndex( (int)x , (int)y );
				mCountX = 5;
				if( !creatFold )
				{
					dragViewScreen = index;
					for( int i = 0 ; i < getChildCount() ; i++ )
					{
						View3D view = getChildAt( i );
						ItemInfo info = ( (IconBase3D)view ).getItemInfo();
						if( index == 0 )
						{
							if( info.screen == 0 )
							{
								getPos( 1 );
								info.screen = 1;
							}
							else if( info.screen == 1 )
							{
								getPos( 2 );
								info.screen = 2;
							}
							else if( info.screen == 2 )
							{
								getPos( 3 );
								info.screen = 3;
							}
							else if( info.screen == 3 )
							{
								getPos( 4 );
								info.screen = 4;
							}
						}
						else if( index == 1 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 2 );
								info.screen = 2;
							}
							else if( info.screen == 2 )
							{
								getPos( 3 );
								info.screen = 3;
							}
							else if( info.screen == 3 )
							{
								getPos( 4 );
								info.screen = 4;
							}
						}
						else if( index == 2 )
						{
							if( info.screen == 0 )
							{
								getPos( 0 );
								info.screen = 0;
							}
							else if( info.screen == 1 )
							{
								getPos( 1 );
								info.screen = 1;
							}
							else if( info.screen == 2 )
							{
								getPos( 3 );
								info.screen = 3;
							}
							else if( info.screen == 3 )
							{
								getPos( 4 );
								info.screen = 4;
							}
						}
						else if( index == 3 )
						{
			
							if( info.screen == 3 )
							{
								getPos( 4 );
								info.screen = 4;
							}
							else{
								getPos( info.screen );
							}
						}
						else if( index == 4 )
						{
							getPos( info.screen );
							
						}
						view.stopAllTween();
						view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , mPosx , mPosy , 0 );
						info.x = mPosx;
						info.y = mPosx;
						info.angle = HotSeat3D.TYPE_WIDGET;
						info.cellX = pageIndex;
						( (IconBase3D)view ).setItemInfo( info );
						//						Root3D.addOrMoveDB(info,
						//								LauncherSettings.Favorites.CONTAINER_HOTSEAT);
					}
				}
				else
				{
					mCountX = 4;
					dealTouchDragged2( x , y );
				}
			}
			else
			{
				dealTouchDragged1( x , y );
			}
		}
		else if( mCountX == 5 )
		{
			if( getChildCount() == 5 )
			{
				if( creatFold )
				{
					mCountX = 5;
					dealTouchDragged2( x , y );
				}
			}
			if( getChildCount() < 5 )
			{
				dealTouchDragged1( x , y );
			}
		}
	}
	
	private void dealTouchDragged1(
			float x ,
			float y )
	{
		exchangeIndex = true;
		int index = getIndex( (int)x , (int)y );
		exchangeIndex = false;
		View3D findView = findExistView( index );
		if( findView == null )
		{
			dragViewScreen = index;
			return;
		}
		if( dragViewScreen < index )
		{
			index--;
			getPos( index );
			if( x > mPosx + mCellWidth * 3 / 4 )
			{
				changeItemCellIndex1( findView );
			}
			else if( x > mPosx + mCellWidth * 1 / 4 && x < mPosx + mCellWidth * 3 / 4 )
			{
				this.setTag( findView );
				dragState = State_CreateFolder;
				viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
			}
		}
		else
		{
			getPos( index );
			if( x < mPosx + mCellWidth / 4 )
			{
				changeItemCellIndex1( findView );
			}
			else if( x > mPosx + mCellWidth / 4 && x < mPosx + mCellWidth * 3 / 4 )
			{
				this.setTag( findView );
				dragState = State_CreateFolder;
				viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
			}
		}
	}
	
	private void dealTouchDragged2(
			float x ,
			float y )
	{
		bNeedAdjustIndex = false;
		exchangeIndex = true;
		int index = getIndex( (int)x , (int)y );
		exchangeIndex = false;
		View3D findView = findExistView( index );
		Log.v( "ondrop" , "dealTouchDragged:" + index );
		if( findView == null )
		{
			return;
		}
		if( dragViewScreen == index )
		{
			Log.v( "ondrop" , "dealTouchDragged111:" + dragViewScreen );
			this.setTag( findView );
			dragState = State_CreateFolder;
			viewParent.onCtrlEvent( this , MSG_VIEW_MOVED );
		}
	}
	
	private void changeItemCellIndex1(
			View3D findView )
	{
		ItemInfo findView_ItemInfo = ( (IconBase3D)findView ).getItemInfo();
		int focus_screen = dragViewScreen;
		dragViewScreen = findView_ItemInfo.screen;
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View3D view = getChildAt( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			if( focus_screen < findView_ItemInfo.screen )
			{
				if( info.screen > focus_screen && info.screen < findView_ItemInfo.screen )
				{
					info.screen--;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
			else
			{
				if( info.screen >= findView_ItemInfo.screen && info.screen < focus_screen )
				{
					info.screen++;
					getPos( info.screen );
					info.x = mPosx;
					info.y = mPosy;
					( (IconBase3D)view ).setItemInfo( info );
					view.stopAllTween();
					view.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , mPosx , mPosy , 0 );
					//					Root3D.addOrMoveDB(info,
					//							LauncherSettings.Favorites.CONTAINER_HOTSEAT);
				}
			}
		}
	}
	
	private int clickIcon(
			int x ,
			int y )
	{
		int iconIndex = -1;
		if( mCountX == 1 )
		{
			if( x > (int)( this.width - viewWidth ) / 2 && x < (int)( this.width - viewWidth ) / 2 + viewWidth )
			{
				iconIndex = 0;
			}
		}
		else if( mCountX == 2 )
		{
			if( x > (int)( ( this.width - viewWidth * 2 ) / 3 ) && x < (int)( ( this.width - viewWidth * 2 ) / 3 + viewWidth ) )
			{
				iconIndex = 0;
			}
			else if( x > (int)( 2 * ( this.width - viewWidth * 2 ) / 3 + viewWidth ) && x < (int)( 2 * ( this.width - viewWidth * 2 ) / 3 + 2 * viewWidth ) )
			{
				iconIndex = 1;
			}
		}
		else if( mCountX == 3 )
		{
			if( x > (int)( ( this.width - viewWidth * 3 ) / 4 ) && x < (int)( ( this.width - viewWidth * 3 ) / 4 + viewWidth ) )
			{
				iconIndex = 0;
			}
			else if( x > (int)( 2 * ( this.width - viewWidth * 3 ) / 4 + viewWidth ) && x < (int)( ( 1 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 2 * viewWidth ) )
			{
				iconIndex = 1;
			}
			else if( x > (int)( 3 * ( this.width - viewWidth * 3 ) / 4 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 3 ) / 4 + 3 * viewWidth ) )
			{
				iconIndex = 2;
			}
		}
		else if( mCountX == 4 )
		{
			if( x > (int)( ( this.width - viewWidth * 4 ) / 5 ) && x < (int)( ( this.width - viewWidth * 4 ) / 5 + viewWidth ) )
			{
				iconIndex = 0;
			}
			else if( x > (int)( 2 * ( this.width - viewWidth * 4 ) / 5 + viewWidth ) && x < (int)( ( 1 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 2 * viewWidth ) )
			{
				iconIndex = 1;
			}
			else if( x > (int)( 3 * ( this.width - viewWidth * 4 ) / 5 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 3 * viewWidth ) )
			{
				iconIndex = 2;
			}
			else if( x > (int)( 4 * ( this.width - viewWidth * 4 ) / 5 + 3 * viewWidth ) && x < (int)( ( 3 + 1 ) * ( this.width - viewWidth * 4 ) / 5 + 4 * viewWidth ) )
			{
				iconIndex = 3;
			}
		}
		else if( mCountX == 5 )
		{
			if( x > (int)( ( this.width - viewWidth * 5 ) / 6 ) && x < (int)( ( this.width - viewWidth * 5 ) / 6 + viewWidth ) )
			{
				iconIndex = 0;
			}
			else if( x > (int)( 2 * ( this.width - viewWidth * 5 ) / 6 + viewWidth ) && x < (int)( 2 * ( this.width - viewWidth * 5 ) / 6 + 2 * viewWidth ) )
			{
				iconIndex = 1;
			}
			else if( x > (int)( 3 * ( this.width - viewWidth * 5 ) / 6 + 2 * viewWidth ) && x < (int)( ( 2 + 1 ) * ( this.width - viewWidth * 5 ) / 6 + 3 * viewWidth ) )
			{
				iconIndex = 2;
			}
			else if( x > (int)( 4 * ( this.width - viewWidth * 5 ) / 6 + 3 * viewWidth ) && x < (int)( ( 3 + 1 ) * ( this.width - viewWidth * 5 ) / 6 + 4 * viewWidth ) )
			{
				iconIndex = 3;
			}
			else if( x > (int)( 5 * ( this.width - viewWidth * 5 ) / 6 + 4 * viewWidth ) && x < (int)( ( 4 + 1 ) * ( this.width - viewWidth * 5 ) / 6 + 5 * viewWidth ) )
			{
				iconIndex = 4;
			}
		}
		return iconIndex;
	}
	
	public void setPageIndex(
			int pageIndex )
	{
		this.pageIndex = pageIndex;
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	public void updateItemInfoDB()
	{
		Log.i( "" , "###### Hot updateItemIndexDB #######" );
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			View3D view = getChildAt( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			getPos( info.screen );
			info.x = mPosx;
			info.y = mPosy;
			( (IconBase3D)view ).setItemInfo( info );
			Root3D.addOrMoveDB( info , LauncherSettings.Favorites.CONTAINER_HOTSEAT );
		}
	}
	
	public void adjustItemIndex()
	{
		Log.i( "Hotseat3Dlayout" , "###### Hot adjustItemIndex #######" );
		int childCount = getChildCount();
		int emptyIndex = 0xff;
		int count = 0;
		for( int i = 0 ; i < childCount ; i++ )
		{
			for( int j = 0 ; j < childCount ; j++ )
			{
				View3D view = children.get( j );
				ItemInfo info = ( (IconBase3D)view ).getItemInfo();
				if( info.screen == i )
				{
					count = 0;
					break;
				}
				count++;
			}
			if( count == childCount )
			{
				emptyIndex = i;
				break;
			}
		}
		for( int i = 0 ; i < childCount ; i++ )
		{
			View3D view = children.get( i );
			ItemInfo info = ( (IconBase3D)view ).getItemInfo();
			if( info.screen > 0 && info.screen >= emptyIndex )
			{
				info.screen--;
				( (IconBase3D)view ).setItemInfo( info );
			}
		}
	}
	
	public float getIconGap()
	{
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		// paint.setColor(Color.WHITE);
		// paint.setAntiAlias(true);
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		float paddingTop;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
		}
		if( paddingTop < 0 )
		{
			paddingTop = 0;
		}
		return paddingTop + space_height;
	}
	
	public float getIconSpace()
	{
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		// paint.setColor(Color.WHITE);
		// paint.setAntiAlias(true);
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		float paddingTop;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;//bmpHeight / 10
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
		}
		if( paddingTop < 0 )
		{
			paddingTop = 0;
		}
		return space_height;
	}
}
