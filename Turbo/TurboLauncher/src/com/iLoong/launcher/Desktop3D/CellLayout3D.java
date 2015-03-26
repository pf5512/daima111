package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.SetupMenu.SetMenuDesktop;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;


public class CellLayout3D extends ViewGroup3D
{
	
	static final String TAG = "cellLayout";
	public static final int MSG_ADD_DRAGLAYER = 0;
	public static final int MSG_PAGE_TO = 1;
	public static final int MSG_JUMP_TO_HOTSEAT = 2;
	public static final int MSG_REFRESH_PAGE = 3;
	public static final int MSG_CHANGE_TO_DEL_PAGE = 4;
	public static final int MSG_SET_CURRENT_PAGE = 5;
	public static float currentX = 0;
	public static float currentY = 0;
	private int mCountX;
	private int mCountY;
	private int mCellWidth;
	private int mCellHeight;
	private int mWidthGap;
	private int mHeightGap;
	private boolean mScrollingTransformsDirty = false;
	private final Rect mRect = new Rect();
	private final CellInfo mCellInfo = new CellInfo();
	// These are temporary variables to prevent having to allocate a new object
	// just to
	// return an (x, y) value from helper functions. Do NOT use them to maintain
	// other state.
	private final int[] mTmpXY = new int[2];
	private final int[] mTmpPoint = new int[2];
	int[] mTempLocation = new int[2];
	boolean[][] mOccupied;
	boolean[][] mTmpOccupied;
	View3D[][] cellViewList;
	//Jone add start
	public boolean isNewPage = false;
	boolean[][] mCopyOccupied;
	View3D[][] mCopycellViewList;
	//Jone end
	private boolean mLastDownOnOccupiedCell = false;
	private int[] mFolderLeaveBehindCell = { -1 , -1 };
	// If we're actively dragging something over this screen, mIsDragOverlapping
	// is true
	private boolean mIsDragOverlapping = false;
	private final Point mDragCenter = new Point();
	// These arrays are used to implement the drag visualization on x-large
	// screens.
	// They are used as circular arrays, indexed by mDragOutlineCurrent.
	private Point[] mDragOutlines = new Point[4];
	private float[] mDragOutlineAlphas = new float[mDragOutlines.length];
	// new InterruptibleInOutAnimator[mDragOutlines.length];
	// Used as an index into the above 3 arrays; indicates which is the most
	// current value.
	private int mDragOutlineCurrent = 0;
	private View3D mPressedOrFocusedIcon;
	// When a drag operation is in progress, holds the nearest cell to the touch
	// point
	private final int[] mDragCell = new int[2];
	private boolean mDragging = false;
	// private TimeInterpolator mEaseOutInterpolator;
	private CellLayoutChildren mChildren;
	/* need repair lq_grp start */
	int mPaddingLeft = 0;
	int mPaddingRight = 48;
	int mPaddingTop = 0;
	int mPaddingBottom = 48;
	public static final String TEXT_REMINDER = "轻敲并按住屏幕以添加项目";
	public static TextureRegion tr_textReminder = null;
	public static boolean canShowReminder = false;
	public static boolean keyPadInvoked = false;
	public static int nextPageIndex = 0;
	public int cursorX = 0;
	public int cursorY = 0;
	private float locationX = 0;
	private float locationY = 0;
	private float iconWidth = 0;
	private float iconHeight = 0;
	public static boolean firstlyCome = true;
	public static boolean hideFocus = true;
	public static boolean hasItem = true;// are there some items blow current
											// row ?
	public static boolean iconExist = false;
	public static boolean touchEvent = false;
	public static int origin = 0;
	public static String direction = "left";
	public final String LEFT_DIR = "left";
	public final String RIGHT_DIR = "right";
	public float gs4_alpha = 0;
	// teapotXu add start for longClick in Workspace to EditMode like miui
	private TextureRegion delIcon_normal = null;
	private TextureRegion delIcon_focus = null;
	private TextureRegion delIcon_indicate = null;
	private float delIconX;
	private float delIconY;
	private float delIconWidth;
	private float delIconheight;
	public static float zoomWidth = 100f;
	public static float zoomHeight = 100f;
	public static final int LeftMove = 1;
	public static final int RightMove = 2;
	public static final int TopMove = 3;
	public static final int BottomMove = 4;
	
	// teapotXu add end
	int getPaddingLeft()
	{
		return 0;
	}
	
	int getPaddingBottom()
	{
		return (int)( R3D.Workspace_celllayout_bottompadding );
	}
	
	int getPaddingTop()
	{
		return (int)( R3D.Workspace_celllayout_toppadding );
	}
	
	int mScrollX = 0;
	int mScrollY = 0;
	/* need repair lq_grp end */
	public static final int TYPE_CLOCK = 0;
	public static final int TYPE_ICON = 1;
	public static final int TYPE_FOLDER = 2;
	private ArrayList<View3D> clockList = new ArrayList<View3D>();
	private ArrayList<View3D> iconList = new ArrayList<View3D>();
	private ArrayList<View3D> folderList = new ArrayList<View3D>();
	// jbc start	
	public static final int MODE_DRAG_OVER = 0;
	public static final int MODE_ON_DROP = 1;
	public static final int MODE_ON_DROP_EXTERNAL = 2;
	public static final int MODE_ACCEPT_DROP = 3;
	private ArrayList<View3D> mIntersectingViews = new ArrayList<View3D>();
	private Rect mOccupiedRect = new Rect();
	private int[] mDirectionVector = new int[2];
	int[] mPreviousReorderDirection = new int[2];
	private static final int INVALID_DIRECTION = -100;
	private static int lastReorderCellX = -1;
	private static int lastReorderCellY = -1;
	private static boolean cellChanged = false;
	private static long waitStartTime;
	private static boolean isWaiting = false;
	public static boolean isDrawBg = false;
	
	public static void setLastReorder(
			int valueX ,
			int valueY )
	{
		lastReorderCellX = valueX;
		lastReorderCellY = valueY;
	}
	
	public static int getLastReorderX()
	{
		return lastReorderCellX;
	}
	
	public static int getLastReorderY()
	{
		return lastReorderCellY;
	}
	
	public static void cleanLastReorder()
	{
		setLastReorder( -1 , -1 );
	}
	
	public void onThemeChanged()
	{
		mCellWidth = R3D.Workspace_cell_each_width;
		mCellHeight = R3D.Workspace_cell_each_height;
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
			else if( view instanceof Widget3D )
			{
				( (Widget3D)view ).onThemeChanged();
			}
		}
	}
	
	public boolean checkIconNums(
			int addNums )
	{
		if( iconList.size() + addNums > 100 )
		{
			return false;
		}
		else
			return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( iLoongLauncher.isShowNews )
		{
			return true;
		}
		return super.onClick( x , y );
	}
	
	boolean addToList(
			View3D view )
	{
		ArrayList<View3D> temp = new ArrayList<View3D>();
		cellDropType = CellgetViewType( view );
		temp.add( view );
		return addView( temp , (int)( view.x + view.width / 2 ) , (int)( view.y + view.height / 2 ) );
	}
	
	private void removeList(
			View3D view )
	{
		if( view instanceof Icon3D )
			iconList.remove( view );
		else if( view instanceof Widget3D )
			clockList.remove( view );
		else if( view instanceof FolderIcon3D )
			folderList.remove( view );
	}
	
	public ArrayList<View3D> getList(
			int type )
	{
		switch( type )
		{
			case TYPE_CLOCK:
				return clockList;
			case TYPE_ICON:
				return iconList;
			case TYPE_FOLDER:
				return folderList;
			default:
				Log.e( "getlist" , "Error type : " + type );
				return null;
		}
	}
	
	/**
	 * Drop a child at the specified position
	 * 
	 * @param child
	 *            The child that is being dropped
	 * @param targetXY
	 *            Destination area to move to
	 */
	void onDropChild(
			View3D child ,
			int[] targetXY )
	{
		if( child != null )
		{
			child.x = targetXY[0];
			child.y = targetXY[1];
			if( child.x < 0 )
				child.x = 0;
			if( child.y < 0 )
				child.y = 0;
			child.show();
			child.isDragging = false;
		}
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		return super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( DefaultLayout.keypad_event_of_focus )
		{
			// this.resetInfo();
		}
		// teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			this.releaseFocus();
			if( x < delIconX + delIconWidth && x > delIconX && y < delIconY + delIconheight - UtilsBase.getStatusBarHeight() && y > delIconY - UtilsBase.getStatusBarHeight() )
			{
				ViewGroup3D parent = this.getParent();
				if( getChildCount() == 0 && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && ( parent instanceof Workspace3D && ( (Workspace3D)parent ).getIndicatorPageCount() > 3 ) )
				{
					/* 减掉1是因为在编辑模式，前面有一个指示可以添加新页的的firstView */
					// int i= this.getIndexInParent()-1;
					// setTag(i);
					viewParent.onCtrlEvent( this , MSG_CHANGE_TO_DEL_PAGE );
					delIcon_indicate = null;
					return true;
				}
				Log.v( "page" , " CellLayout3D return true onTouchUp:" + name + " x:" + x + " y:" + y + " pointer:" + pointer );
				return false;
			}
			else
			{
				setDelIcon_indicate_NormalRegion();
				return super.onTouchUp( x , y , pointer );
			}
		}
		else
		// teapotXu add end
		{
			return super.onTouchUp( x , y , pointer );
		}
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( DefaultLayout.keypad_event_of_focus )
		{
			resetCurrFocus();
			switchOff();
			CellLayout3D.keyPadInvoked = false;
		}
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( x < delIconX + delIconWidth && x > delIconX && y < delIconY + delIconheight - UtilsBase.getStatusBarHeight() && y > delIconY - UtilsBase.getStatusBarHeight() )
			{
				ViewGroup3D parent = this.getParent();
				if( getChildCount() == 0 && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && ( parent instanceof Workspace3D && ( (Workspace3D)parent ).getIndicatorPageCount() > 3 ) )
				{
					delIcon_indicate = delIcon_focus;
					this.requestFocus();
				}
			}
		}
		onDropLeave();
		return super.onTouchDown( x , y , pointer );
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == FolderLargeTween && FolderLargeTween != null && type == TweenCallback.COMPLETE )
		{
			FolderLargeTween = null;
			return;
		}
		if( source == reorderTween && type == TweenCallback.COMPLETE )
		{
			int animKind = (Integer)( source.getUserData() );
			if( reorderTween != null )
				reorderTween.free();
			reorderTween = null;
			switch( animKind )
			{
				case REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_SWING:
					startReorderTween( REORDER_TWEEN_TYPE_SWING_TO_ORI );
					break;
				case REORDER_TWEEN_TYPE_SWING_TO_ORI:
					startReorderTween( REORDER_TWEEN_TYPE_SWING_TO_TEMP );
					break;
				case REORDER_TWEEN_TYPE_SWING_TO_TEMP:
					startReorderTween( REORDER_TWEEN_TYPE_SWING_TO_ORI );
					break;
				case REORDER_TWEEN_TYPE_MOVE_TO_ORI:
					break;
				case REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP:
					break;
			}
			return;
		}
		if( source == revertTween && type == TweenCallback.COMPLETE )
		{
			if( revertTween != null )
				revertTween.free();
			revertTween = null;
			return;
		}
	}
	
	public float hl_x = 0.0f;
	public float hl_w = 0.0f;
	public float hl_u0 = 0.0f;
	public float hl_u1 = 1.0f;
	public float alpha = 1.0f;
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( debug && debugTexture != null && parent != null )
			batch.draw(
					debugTexture ,
					x ,
					y ,
					originX ,
					originY ,
					width == 0 ? 200 : width ,
					height == 0 ? 200 : height ,
					scaleX ,
					scaleY ,
					rotation ,
					0 ,
					0 ,
					debugTexture.getWidth() ,
					debugTexture.getHeight() ,
					false ,
					false );
		if( transform )
			applyTransform( batch );
		View3D parent = this.getParent();
		float temp_xScale = 0;
		float temp_alpha = 0;
		if( parent instanceof Workspace3D )
		{
			temp_xScale = ( (Workspace3D)this.getParent() ).getX();
			// added by zhenNan.ye disable draw panelFrame when standard
			if( !APageEase.is_standard || APageEase.is_standard )
			{
				temp_xScale = 0;
			}
			temp_alpha = Workspace3D.is_longKick ? ( (Workspace3D)this.getParent() ).getUser() : 0;// (1-((Workspace3D)this.getParent()).getScaleX())*5;
			if( temp_alpha < 0 )
				temp_alpha = 0;
			else if( temp_alpha > 1 )
				temp_alpha = 1;
			if( cell_editmode_bg != null && isDrawBg )
			{
				batch.setColor( 1.0f , 1.0f , 1.0f , Desktop3DListener.root.getUser2() );
				cell_editmode_bg.draw(
						batch ,
						0 ,
						getPaddingBottom() - R3D.workspace_celllayout_offset_y ,
						this.width ,
						this.height - Utils3D.getStatusBarHeight() - getPaddingTop() - getPaddingBottom() + R3D.workspace_celllayout_bg_padding_top );
				temp_alpha = 0;
			}
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && getChildCount() == 0 && ( ( (Workspace3D)parent ).getIndicatorPageCount() > 3 ) && delIcon_indicate != null && mDragging == false && Desktop3DListener
					.initDone() == true )
			{
				batch.draw( delIcon_indicate , delIconX , delIconY - Utils3D.getStatusBarHeight() , delIconWidth , delIconheight );
			}
		}
		float panelFrame_y = getPaddingBottom();
		float panelFrame_h = this.height - getPaddingTop() - getPaddingBottom();
		if( ( !DefaultLayout.desktop_hide_frame || DefaultLayout.desktop_hide_frame && Workspace3D.is_longKick ) && !( DefaultLayout.enable_workspace_miui_edit_mode == true && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) // teapotXu
																																																												// add
		)
		{
			if( cell_editmode_bg == null )
			{
				Desktop3DListener.root.getWorkspace().setFactorAndOrigY( Utils3D.getScreenHeight() / 2 );
				batch.setColor( 1.0f , 1.0f , 1.0f , ( 1 - 2 * Math.abs( Math.abs( temp_xScale ) - 0.5f ) ) + temp_alpha );
				if( cell_editmode_bg != null )
				{
					cell_editmode_bg.draw(
							batch ,
							0 ,
							getPaddingBottom() - R3D.workspace_celllayout_offset_y ,
							this.width ,
							this.height - getPaddingTop() - getPaddingBottom() + R3D.workspace_celllayout_bg_padding_top );
				}
			}
			else
			{
				batch.setColor( 1.0f , 1.0f , 1.0f , ( 1 - 2 * Math.abs( Math.abs( temp_xScale ) - 0.5f ) ) + temp_alpha );
				cell_editmode_bg.draw(
						batch ,
						0 ,
						getPaddingBottom() - R3D.workspace_celllayout_offset_y ,
						this.width ,
						this.height - getPaddingTop() - getPaddingBottom() + R3D.workspace_celllayout_bg_padding_top );
			}
		}
		if( DefaultLayout.empty_page_add_reminder )
		{
			if( canShowReminder && children.size() == 0 && !Workspace3D.is_longKick )
			{
				if( Root3D.scroll_indicator )
					batch.setColor( 1.0f , 1.0f , 1.0f , gs4_alpha );
				else if( Root3D.isDragAutoEffect )
					batch.setColor( 1.0f , 1.0f , 1.0f , temp_alpha );
				else
					batch.setColor( 1.0f , 1.0f , 1.0f , color.a * parentAlpha );
				batch.draw(
						tr_textReminder ,
						( this.width - tr_textReminder.getRegionWidth() ) / 2 ,
						panelFrame_y + ( panelFrame_h - tr_textReminder.getRegionHeight() ) / 2 ,
						tr_textReminder.getRegionWidth() ,
						tr_textReminder.getRegionHeight() );
			}
		}
		drawChildren( batch , parentAlpha );
		if( transform )
			resetTransform( batch );
	}
	
	@Override
	public void addViewAt(
			int index ,
			View3D actor )
	{
		super.addViewAt( index , actor );
		ItemInfo info = ( (IconBase3D)actor ).getItemInfo();
		if( info.cellX != -1 && info.cellY != -1 )
		{
			for( int k = 0 ; k < info.spanX && k < mCountX ; k++ )
				for( int j = 0 ; j < info.spanY && j < mCountY ; j++ )
				{
					mOccupied[info.cellX + k][info.cellY + j] = true;
					cellViewList[info.cellX + k][info.cellY + j] = actor;
				}
		}
	}
	
	// teaptoXu add start for longClick in Workspace to editMode like miui
	//Jone modify start: one image is enough for all pages.
	public static NinePatch cell_editmode_bg = null;
	public static boolean b_cell_editmode_bg_show = true;
	
	//Jone end 
	public void setDelIcon_indicate_NormalRegion()
	{
		if( delIcon_normal != null )
		{
			delIcon_indicate = delIcon_normal;
		}
	}
	
	public void setCellBackgroud(
			NinePatch ninePatch )
	{
		cell_editmode_bg = ninePatch;
	}
	
	// teapotXu add end
	@Override
	public void removeView(
			View3D actor )
	{
		removeFromSuperClass( actor );
		removeList( actor );
		cellFindViewAndRemove( actor );
		Log.v( "bind" , "remove:" + actor );
	}
	
	@Override
	public void setScaleZ(
			float f )
	{
		for( View3D view : clockList )
			view.setScaleZ( f );
	}
	
	@Override
	public void show()
	{
		if( DefaultLayout.enable_takein_workspace_by_longclick && Workspace3D.isHideAll )
		{
			for( int i = 0 ; i < this.getChildCount() ; i++ )
			{
				View3D icon = this.getChildAt( i );
				if( icon instanceof Widget3D )
				{
					icon.hide();
				}
			}
			super.hide();
		}
		else
		{
			for( int i = 0 ; i < this.getChildCount() ; i++ )
			{
				View3D icon = this.getChildAt( i );
				if( icon instanceof Widget3D )
				{
					icon.show();
				}
			}
			super.show();
		}
	}
	
	@Override
	public void hide()
	{
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			View3D icon = this.getChildAt( i );
			if( icon instanceof Widget3D )
			{
				icon.hide();
			}
		}
		super.hide();
	}
	
	public CellLayout3D(
			String name )
	{
		super( name );
		transform = true;
		mCountX = R3D.Workspace_cellCountX;
		mCountY = R3D.Workspace_cellCountY;
		mOccupied = new boolean[mCountX][mCountY];
		mTmpOccupied = new boolean[mCountX][mCountY];
		mPreviousReorderDirection[0] = INVALID_DIRECTION;
		mPreviousReorderDirection[1] = INVALID_DIRECTION;
		mCellWidth = R3D.Workspace_cell_each_width;
		mCellHeight = R3D.Workspace_cell_each_height;
		cellViewList = new View3D[mCountX][mCountY];
		mOccupied = new boolean[mCountX][mCountY];
		if( DefaultLayout.empty_page_add_reminder )
		{
			if( tr_textReminder == null )
			{
				Bitmap bm_textReminder = reminderToBitmap();
				tr_textReminder = new TextureRegion( new BitmapTexture( bm_textReminder ) );
				tr_textReminder.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				bm_textReminder.recycle();
			}
		}
		mDragCell[0] = mDragCell[1] = -1;
		for( int i = 0 ; i < mDragOutlines.length ; i++ )
		{
			mDragOutlines[i] = new Point( -1 , -1 );
		}
		// When dragging things around the home screens, we show a green outline
		// of
		// where the item will land. The outlines gradually fade out, leaving a
		// trail
		// behind the drag path.
		// Set up all the animations that are used to implement this fading.
		// final int duration =
		// res.getInteger(R.integer.config_dragOutlineFadeTime);
		final float fromAlphaValue = 0;
		Arrays.fill( mDragOutlineAlphas , fromAlphaValue );
		// teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode )
		{
			if( delIcon_normal == null )
			{
				delIcon_normal = R3D.findRegion( "miui-delpage" );
			}
			if( delIcon_focus == null )
			{
				delIcon_focus = R3D.findRegion( "miui-delpage-focus" );
			}
			delIcon_indicate = delIcon_normal;
			delIconX = width - getPaddingTop() - delIcon_indicate.getRegionWidth() * SetupMenu.mScale;
			delIconY = height - 2 * getPaddingTop() - delIcon_indicate.getRegionHeight() * SetupMenu.mScale;
			delIconWidth = delIcon_indicate.getRegionWidth() * SetupMenu.mScale;
			delIconheight = delIcon_indicate.getRegionHeight() * SetupMenu.mScale;
		}
		mChildren = new CellLayoutChildren();
	}
	
	public int getColumnCount()
	{
		return mCountX;
	}
	
	public int getRowCount()
	{
		return mCountY;
	}
	
	public Bitmap reminderToBitmap()
	{
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.reminder_font );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );
		if( DefaultLayout.title_style_bold )
			paint.setFakeBoldText( true );
		FontMetrics fontMetrics = paint.getFontMetrics();
		float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
		float textWidth = paint.measureText( TEXT_REMINDER );
		float text_x = 0;
		float text_y = singleLineHeight;
		Bitmap bmp = Bitmap.createBitmap( Math.round( textWidth + 4 ) , Math.round( singleLineHeight + 4 ) , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		if( !DefaultLayout.hide_title_bg_shadow )
		{
			int text_color = DefaultLayout.title_outline_color;
			paint.setColor( text_color );
			int shadow_color = DefaultLayout.title_shadow_color;
			for( int j = 1 ; j <= DefaultLayout.title_outline_shadow_size ; j++ )
			{
				paint.setShadowLayer( 1f , -j , 0f , shadow_color );
				canvas.drawText( TEXT_REMINDER , text_x - j , text_y , paint );
				paint.setShadowLayer( 1f , 0f , -j , shadow_color );
				canvas.drawText( TEXT_REMINDER , text_x , text_y - j , paint );
				paint.setShadowLayer( 1f , j , 0f , shadow_color );
				canvas.drawText( TEXT_REMINDER , text_x + j , text_y , paint );
				paint.setShadowLayer( 1f , 0f , j , shadow_color );
				canvas.drawText( TEXT_REMINDER , text_x , text_y + j , paint );
			}
		}
		paint.clearShadowLayer();
		paint.setColor( android.graphics.Color.WHITE );
		canvas.drawText( TEXT_REMINDER , text_x , text_y , paint );
		return bmp;
	}
	
	static int widthInPortrait(
			Resources r ,
			int numCells )
	{
		// We use this method from Workspace to figure out how many rows/columns
		// Launcher should
		// have. We ignore the left/right padding on CellLayout because it turns
		// out in our design
		// the padding extends outside the visible screen size, but it looked
		// fine anyway.
		int cellWidth = r.getDimensionPixelSize( RR.dimen.workspace_cell_width );
		int minGap = Math.min( r.getDimensionPixelSize( RR.dimen.workspace_width_gap ) , r.getDimensionPixelSize( RR.dimen.workspace_height_gap ) );
		return minGap * ( numCells - 1 ) + cellWidth * numCells;
	}
	
	static int heightInLandscape(
			Resources r ,
			int numCells )
	{
		// We use this method from Workspace to figure out how many rows/columns
		// Launcher should
		// have. We ignore the left/right padding on CellLayout because it turns
		// out in our design
		// the padding extends outside the visible screen size, but it looked
		// fine anyway.
		int cellHeight = R3D.Workspace_cell_each_height;// r.getDimensionPixelSize(RR.dimen.workspace_cell_height);
		int minGap = Math.min( r.getDimensionPixelSize( RR.dimen.workspace_width_gap ) , r.getDimensionPixelSize( RR.dimen.workspace_height_gap ) );
		return minGap * ( numCells - 1 ) + cellHeight * numCells;
	}
	
	public void enableHardwareLayers()
	{
		mChildren.enableHardwareLayers();
	}
	
	public void setGridSize(
			int x ,
			int y )
	{
		mCountX = x;
		mCountY = y;
		if( mOccupied == null )
			mOccupied = new boolean[mCountX][mCountY];
	}
	
	private void invalidateBubbleTextView(
			View3D icon )
	{
		// final int padding = icon.getPressedOrFocusedBackgroundPadding();
		// invalidate(icon.x + getPaddingLeft() - padding,
		// icon.y + getPaddingTop() - padding,
		// icon.x + icon.width + getPaddingLeft() + padding,
		// icon.y + icon.height + getPaddingTop() + padding);
	}
	
	void setOverScrollAmount(
			float r ,
			boolean left )
	{
		// if (left && mOverScrollForegroundDrawable != mOverScrollLeft) {
		// mOverScrollForegroundDrawable = mOverScrollLeft;
		// } else if (!left && mOverScrollForegroundDrawable !=
		// mOverScrollRight) {
		// mOverScrollForegroundDrawable = mOverScrollRight;
		// }
		//
		// mForegroundAlpha = (int) Math.round((r * 255));
		// mOverScrollForegroundDrawable.setAlpha(mForegroundAlpha);
		// invalidate();
	}
	
	void setPressedOrFocusedIcon(
			View3D icon )
	{
		// We draw the pressed or focused BubbleTextView's background in
		// CellLayout because it
		// requires an expanded clip rect (due to the glow's blur radius)
		View3D oldIcon = mPressedOrFocusedIcon;
		mPressedOrFocusedIcon = icon;
		if( oldIcon != null )
		{
			invalidateBubbleTextView( oldIcon );
		}
		if( mPressedOrFocusedIcon != null )
		{
			invalidateBubbleTextView( mPressedOrFocusedIcon );
		}
	}
	
	public CellLayoutChildren getChildrenLayout()
	{
		if( getChildCount() > 0 )
		{
			return (CellLayoutChildren)getChildAt( 0 );
		}
		return null;
	}
	
	void setIsDragOverlapping(
			boolean isDragOverlapping )
	{
		if( mIsDragOverlapping != isDragOverlapping )
		{
			mIsDragOverlapping = isDragOverlapping;
		}
	}
	
	boolean getIsDragOverlapping()
	{
		return mIsDragOverlapping;
	}
	
	protected void setOverscrollTransformsDirty(
			boolean dirty )
	{
		mScrollingTransformsDirty = dirty;
	}
	
	protected void resetOverscrollTransforms()
	{
		if( mScrollingTransformsDirty )
		{
			setOverscrollTransformsDirty( false );
			setRotationY( 0 );
			// It doesn't matter if we pass true or false here, the important
			// thing is that we
			// pass 0, which results in the overscroll drawable not being drawn
			// any more.
			setOverScrollAmount( 0 , false );
		}
	}
	
	public void setFolderLeaveBehindCell(
			int x ,
			int y )
	{
		mFolderLeaveBehindCell[0] = x;
		mFolderLeaveBehindCell[1] = y;
	}
	
	public void clearFolderLeaveBehind()
	{
		mFolderLeaveBehindCell[0] = -1;
		mFolderLeaveBehindCell[1] = -1;
	}
	
	public boolean shouldDelayChildPressedState()
	{
		return false;
	}
	
	int getCountX()
	{
		return mCountX;
	}
	
	int getCountY()
	{
		return mCountY;
	}
	
	public boolean addViewToCellLayout(
			View3D child ,
			int index ,
			int childId ,
			LayoutParams params ,
			boolean markCells )
	{
		final LayoutParams lp = params;
		// Generate an id for each view, this assumes we have at most 256x256
		// cells
		// per workspace screen
		if( lp.cellX >= 0 && lp.cellX <= mCountX - 1 && lp.cellY >= 0 && lp.cellY <= mCountY - 1 )
		{
			// If the horizontal or vertical span is set to -1, it is taken to
			// mean that it spans the extent of the CellLayout
			if( lp.cellHSpan < 0 )
				lp.cellHSpan = mCountX;
			if( lp.cellVSpan < 0 )
				lp.cellVSpan = mCountY;
			if( markCells )
				markCellsAsOccupiedForView( child );
			return true;
		}
		return false;
	}
	
	@Override
	public void removeAllViews()
	{
		clearOccupiedCells();
		if( this instanceof MediaView3D )
			return;
		super.removeAllViews();
	}
	
	public void removeAllViewsInLayout()
	{
		if( mChildren.getChildCount() > 0 )
		{
			clearOccupiedCells();
			mChildren.removeAllViewsInLayout();
		}
	}
	
	public void removeViewWithoutMarkingCells(
			View3D view )
	{
		mChildren.removeView( view );
	}
	
	public void removeViewSub(
			View3D view )
	{
		markCellsAsUnoccupiedForView( view );
		mChildren.removeView( view );
	}
	
	public void removeViewAt(
			int index )
	{
		markCellsAsUnoccupiedForView( mChildren.getChildAt( index ) );
		mChildren.removeViewAt( index );
	}
	
	public void removeViewInLayout(
			View3D view )
	{
		markCellsAsUnoccupiedForView( view );
		mChildren.removeViewInLayout( view );
	}
	
	public void removeViews(
			int start ,
			int count )
	{
		for( int i = start ; i < start + count ; i++ )
		{
			markCellsAsUnoccupiedForView( mChildren.getChildAt( i ) );
		}
		mChildren.removeViews( start , count );
	}
	
	public void removeViewsInLayout(
			int start ,
			int count )
	{
		for( int i = start ; i < start + count ; i++ )
		{
			markCellsAsUnoccupiedForView( mChildren.getChildAt( i ) );
		}
		mChildren.removeViewsInLayout( start , count );
	}
	
	protected void onAttachedToWindow()
	{
		// super.onAttachedToWindow();
		// mCellInfo.screen = ((ViewGroup) getParent()).indexOfChild(this);
	}
	
	public void setTagToCellInfoForPoint(
			int touchX ,
			int touchY )
	{
		final CellInfo cellInfo = mCellInfo;
		final int x = touchX + mScrollX;
		final int y = touchY + mScrollY;
		boolean found = false;
		mLastDownOnOccupiedCell = found;
		if( !found )
		{
			final int cellXY[] = mTmpXY;
			pointToCellExact( x , y , cellXY );
			cellInfo.cell = null;
			cellInfo.cellX = cellXY[0];
			cellInfo.cellY = cellXY[1];
			cellInfo.spanX = 1;
			cellInfo.spanY = 1;
		}
		setTag( cellInfo );
	}
	
	public boolean onInterceptTouchEvent(
			MotionEvent ev )
	{
		// First we clear the tag to ensure that on every touch down we start
		// with a fresh slate,
		// even in the case where we return early. Not clearing here was causing
		// bugs whereby on
		// long-press we'd end up picking up an item from a previous drag
		// operation.
		final int action = ev.getAction();
		if( action == MotionEvent.ACTION_DOWN )
		{
			clearTagCellInfo();
		}
		if( action == MotionEvent.ACTION_DOWN )
		{
			setTagToCellInfoForPoint( (int)ev.getX() , (int)ev.getY() );
		}
		return false;
	}
	
	private void clearTagCellInfo()
	{
		final CellInfo cellInfo = mCellInfo;
		cellInfo.cell = null;
		cellInfo.cellX = -1;
		cellInfo.cellY = -1;
		cellInfo.spanX = 0;
		cellInfo.spanY = 0;
		setTag( cellInfo );
	}
	
	// public CellInfo getTag() {
	// return (CellInfo) super.getTag();
	// }
	/**
	 * Given a point, return the cell that strictly encloses that point
	 * 
	 * @param x
	 *            X coordinate of the point
	 * @param y
	 *            Y coordinate of the point
	 * @param result
	 *            Array of 2 ints to hold the x and y coordinate of the cell
	 */
	void pointToCellExact(
			int x ,
			int y ,
			int[] result )
	{
		final int hStartPadding = getPaddingLeft();
		final int vStartPadding = getPaddingBottom();
		result[0] = ( x - hStartPadding ) / ( mCellWidth + mWidthGap );
		result[1] = ( y - vStartPadding ) / ( mCellHeight + mHeightGap );
		final int xAxis = mCountX;
		final int yAxis = mCountY;
		if( result[0] < 0 )
			result[0] = 0;
		if( result[0] >= xAxis )
			result[0] = xAxis - 1;
		if( result[1] < 0 )
			result[1] = 0;
		if( result[1] >= yAxis )
			result[1] = yAxis - 1;
	}
	
	/**
	 * Given a point, return the cell that most closely encloses that point
	 * 
	 * @param x
	 *            X coordinate of the point
	 * @param y
	 *            Y coordinate of the point
	 * @param result
	 *            Array of 2 ints to hold the x and y coordinate of the cell
	 */
	void pointToCellRounded(
			int x ,
			int y ,
			int[] result )
	{
		pointToCellExact( x + ( mCellWidth / 2 ) , y + ( mCellHeight / 2 ) , result );
	}
	
	/**
	 * Given a cell coordinate, return the point that represents the upper left
	 * corner of that cell
	 * 
	 * @param cellX
	 *            X coordinate of the cell
	 * @param cellY
	 *            Y coordinate of the cell
	 * 
	 * @param result
	 *            Array of 2 ints to hold the x and y coordinate of the point
	 */
	void cellToPoint(
			int cellX ,
			int cellY ,
			int[] result )
	{
		final int hStartPadding = getPaddingLeft();
		final int vStartPadding = getPaddingBottom();
		result[0] = hStartPadding + cellX * ( mCellWidth + mWidthGap );
		result[1] = vStartPadding + cellY * ( mCellHeight + mHeightGap );
		// result[0] = vStartPadding + cellX * (mCellWidth + mWidthGap);
		// result[1] = hStartPadding + cellY * (mCellHeight + mHeightGap);
	}
	
	void cellToPointEditMode4x2(
			int cellX ,
			int cellY ,
			int[] result )
	{
		final int hStartPadding = (int)( getPaddingLeft() + Utils3D.getScreenWidth() * ( 1 - DesktopEditHost.scaleFactor2 ) / 2 );
		final int vStartPadding = (int)( R3D.workspace_offset_high_y + getPaddingBottom() * DesktopEditHost.scaleFactor2 + Utils3D.getScreenHeight() * ( 1 - DesktopEditHost.scaleFactor2 ) / 2 );
		result[0] = (int)( hStartPadding + cellX * ( mCellWidth + mWidthGap ) * DesktopEditHost.scaleFactor2 );
		result[1] = (int)( vStartPadding + cellY * ( mCellHeight + mHeightGap ) * DesktopEditHost.scaleFactor2 );
	}
	
	void cellToCenterPoint(
			int cellX ,
			int cellY ,
			int[] result )
	{
		regionToCenterPoint( cellX , cellY , 1 , 1 , result );
	}
	
	void regionToCenterPoint(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			int[] result )
	{
		final int hStartPadding = getPaddingLeft();
		final int vStartPadding = getPaddingBottom();
		result[0] = hStartPadding + cellX * ( mCellWidth + mWidthGap ) + ( spanX * mCellWidth + ( spanX - 1 ) * mWidthGap ) / 2;
		result[1] = vStartPadding + cellY * ( mCellHeight + mHeightGap ) + ( spanY * mCellHeight + ( spanY - 1 ) * mHeightGap ) / 2;
	}
	
	void regionToRect(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			Rect result )
	{
		final int hStartPadding = getPaddingLeft();
		final int vStartPadding = getPaddingBottom();
		final int left = hStartPadding + cellX * ( mCellWidth + mWidthGap );
		final int top = vStartPadding + cellY * ( mCellHeight + mHeightGap );
		result.set( left , top , left + ( spanX * mCellWidth + ( spanX - 1 ) * mWidthGap ) , top + ( spanY * mCellHeight + ( spanY - 1 ) * mHeightGap ) );
	}
	
	public float getDistanceFromCell(
			float x ,
			float y ,
			int[] cell )
	{
		cellToCenterPoint( cell[0] , cell[1] , mTmpPoint );
		float distance = (float)Math.sqrt( Math.pow( x - mTmpPoint[0] , 2 ) + Math.pow( y - mTmpPoint[1] , 2 ) );
		return distance;
	}
	
	int getCellWidth()
	{
		return mCellWidth;
	}
	
	int getCellHeight()
	{
		return mCellHeight;
	}
	
	int getWidthGap()
	{
		return mWidthGap;
	}
	
	int getHeightGap()
	{
		return mHeightGap;
	}
	
	Rect getContentRect(
			Rect r )
	{
		if( r == null )
		{
			r = new Rect();
		}
		int left = getPaddingLeft();
		int top = getPaddingBottom();
		int right = (int)( left + getWidth() - mPaddingLeft - mPaddingRight );
		int bottom = (int)( top + getHeight() - mPaddingTop - mPaddingBottom );
		r.set( left , top , right , bottom );
		return r;
	}
	
	public View getChildAt(
			int x ,
			int y )
	{
		return mChildren.getChildAt( x , y );
	}
	
	/**
	 * Estimate where the top left cell of the dragged item will land if it is
	 * dropped.
	 * 
	 * @param originX
	 *            The X value of the top left corner of the item
	 * @param originY
	 *            The Y value of the top left corner of the item
	 * @param spanX
	 *            The number of horizontal cells that the item spans
	 * @param spanY
	 *            The number of vertical cells that the item spans
	 * @param result
	 *            The estimated drop cell X and Y.
	 */
	void estimateDropCell(
			int originX ,
			int originY ,
			int spanX ,
			int spanY ,
			int[] result )
	{
		final int countX = mCountX;
		final int countY = mCountY;
		// pointToCellRounded takes the top left of a cell but will pad that
		// with
		// cellWidth/2 and cellHeight/2 when finding the matching cell
		pointToCellRounded( originX , originY , result );
		// If the item isn't fully on this screen, snap to the edges
		int rightOverhang = result[0] + spanX - countX;
		if( rightOverhang > 0 )
		{
			result[0] -= rightOverhang; // Snap to right
		}
		result[0] = Math.max( 0 , result[0] ); // Snap to left
		int bottomOverhang = result[1] + spanY - countY;
		if( bottomOverhang > 0 )
		{
			result[1] -= bottomOverhang; // Snap to bottom
		}
		result[1] = Math.max( 0 , result[1] ); // Snap to top
	}
	
	void visualizeDropLocation(
			View v ,
			Bitmap dragOutline ,
			int originX ,
			int originY ,
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			boolean resize ,
			Point dragOffset ,
			Rect dragRegion )
	{
		final int oldDragCellX = mDragCell[0];
		final int oldDragCellY = mDragCell[1];
		if( v != null && dragOffset == null )
		{
			mDragCenter.set( originX + ( v.getWidth() / 2 ) , originY + ( v.getHeight() / 2 ) );
		}
		else
		{
			mDragCenter.set( originX , originY );
		}
		if( dragOutline == null && v == null )
		{
			return;
		}
		if( cellX != oldDragCellX || cellY != oldDragCellY )
		{
			mDragCell[0] = cellX;
			mDragCell[1] = cellY;
			// Find the top left corner of the rect the object will occupy
			final int[] topLeft = mTmpPoint;
			cellToPoint( cellX , cellY , topLeft );
			int left = topLeft[0];
			int top = topLeft[1];
			if( v != null && dragOffset == null )
			{
				// When drawing the drag outline, it did not account for margin
				// offsets
				// added by the view's parent.
				MarginLayoutParams lp = (MarginLayoutParams)v.getLayoutParams();
				left += lp.leftMargin;
				top += lp.topMargin;
				// Offsets due to the size difference between the View and the
				// dragOutline.
				// There is a size difference to account for the outer blur,
				// which may lie
				// outside the bounds of the view.
				top += ( v.getHeight() - dragOutline.getHeight() ) / 2;
				// We center about the x axis
				left += ( ( mCellWidth * spanX ) + ( ( spanX - 1 ) * mWidthGap ) - dragOutline.getWidth() ) / 2;
			}
			else
			{
				if( dragOffset != null && dragRegion != null )
				{
					// Center the drag region *horizontally* in the cell and
					// apply a drag
					// outline offset
					left += dragOffset.x + ( ( mCellWidth * spanX ) + ( ( spanX - 1 ) * mWidthGap ) - dragRegion.width() ) / 2;
					top += dragOffset.y;
				}
				else
				{
					// Center the drag outline in the cell
					left += ( ( mCellWidth * spanX ) + ( ( spanX - 1 ) * mWidthGap ) - dragOutline.getWidth() ) / 2;
					top += ( ( mCellHeight * spanY ) + ( ( spanY - 1 ) * mHeightGap ) - dragOutline.getHeight() ) / 2;
				}
			}
		}
	}
	
	public void clearDragOutlines()
	{
		mDragCell[0] = -1;
		mDragCell[1] = -1;
	}
	
	/**
	 * Find a vacant area that will fit the given bounds nearest the requested
	 * cell location. Uses Euclidean distance to score multiple vacant areas.
	 * 
	 * @param pixelX
	 *            The X location at which you want to search for a vacant area.
	 * @param pixelY
	 *            The Y location at which you want to search for a vacant area.
	 * @param spanX
	 *            Horizontal span of the object.
	 * @param spanY
	 *            Vertical span of the object.
	 * @param result
	 *            Array in which to place the result, or null (in which case a
	 *            new array will be allocated)
	 * @return The X, Y cell of a vacant area that can contain this object,
	 *         nearest the requested location.
	 */
	int[] findNearestVacantArea(
			int pixelX ,
			int pixelY ,
			int spanX ,
			int spanY ,
			int[] result )
	{
		return findNearestVacantArea( pixelX , pixelY , spanX , spanY , null , result );
	}
	
	int[] findNearestVacantArea(
			int pixelX ,
			int pixelY ,
			int spanX ,
			int spanY ,
			View3D ignoreView ,
			int[] result )
	{
		return findNearestArea( pixelX , pixelY , spanX , spanY , ignoreView , true , result );
	}
	
	private final Stack<Rect> mTempRectStack = new Stack<Rect>();
	
	private void lazyInitTempRectStack()
	{
		if( mTempRectStack.isEmpty() )
		{
			for( int i = 0 ; i < mCountX * mCountY ; i++ )
			{
				mTempRectStack.push( new Rect() );
			}
		}
	}
	
	private void recycleTempRects(
			Stack<Rect> used )
	{
		while( !used.isEmpty() )
		{
			mTempRectStack.push( used.pop() );
		}
	}
	
	/**
	 * Find a vacant area that will fit the given bounds nearest the requested
	 * cell location. Uses Euclidean distance to score multiple vacant areas.
	 * 
	 * @param pixelX
	 *            The X location at which you want to search for a vacant area.
	 * @param pixelY
	 *            The Y location at which you want to search for a vacant area.
	 * @param spanX
	 *            Horizontal span of the object.
	 * @param spanY
	 *            Vertical span of the object.
	 * @param ignoreOccupied
	 *            If true, the result can be an occupied cell
	 * @param result
	 *            Array in which to place the result, or null (in which case a
	 *            new array will be allocated)
	 * @return The X, Y cell of a vacant area that can contain this object,
	 *         nearest the requested location.
	 */
	// int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY,
	// View3D ignoreView,
	// boolean ignoreOccupied, int[] result) {
	// // mark space take by ignoreView as available (method checks if
	// ignoreView is null)
	// markCellsAsUnoccupiedForView(ignoreView);
	//
	// // For items with a spanX / spanY > 1, the passed in point (pixelX,
	// pixelY) corresponds
	// // to the center of the item, but we are searching based on the top-left
	// cell, so
	// // we translate the point over to correspond to the top-left.
	// pixelX -= (mCellWidth + mWidthGap) * (spanX - 1) / 2f;
	// pixelY -= (mCellHeight + mHeightGap) * (spanY - 1) / 2f;
	//
	// // Keep track of best-scoring drop area
	// final int[] bestXY = result != null ? result : new int[2];
	// double bestDistance = Double.MAX_VALUE;
	//
	// final int countX = mCountX;
	// final int countY = mCountY;
	// final boolean[][] occupied = mOccupied;
	//
	// for (int y = 0; y < countY - (spanY - 1); y++) {
	// inner:
	// for (int x = 0; x < countX - (spanX - 1); x++) {
	// if (ignoreOccupied) {
	// for (int i = 0; i < spanX; i++) {
	// for (int j = 0; j < spanY; j++) {
	// if (occupied[x + i][y + j]) {
	// // small optimization: we can skip to after the column we
	// // just found an occupied cell
	// x += i;
	// continue inner;
	// }
	// }
	// }
	// }
	// final int[] cellXY = mTmpXY;
	// cellToCenterPoint(x, y, cellXY);
	//
	// double distance = Math.sqrt(Math.pow(cellXY[0] - pixelX, 2)
	// + Math.pow(cellXY[1] - pixelY, 2));
	// if (distance <= bestDistance) {
	// bestDistance = distance;
	// bestXY[0] = x;
	// bestXY[1] = y;
	// }
	// }
	// }
	// // re-mark space taken by ignoreView as occupied
	// markCellsAsOccupiedForView(ignoreView);
	//
	// // Return -1, -1 if no suitable location found
	// if (bestDistance == Double.MAX_VALUE) {
	// bestXY[0] = -1;
	// bestXY[1] = -1;
	// }
	// return bestXY;
	// }
	/**
	 * Find a vacant area that will fit the given bounds nearest the requested
	 * cell location. Uses Euclidean distance to score multiple vacant areas.
	 * 
	 * @param pixelX
	 *            The X location at which you want to search for a vacant area.
	 * @param pixelY
	 *            The Y location at which you want to search for a vacant area.
	 * @param minSpanX
	 *            The minimum horizontal span required
	 * @param minSpanY
	 *            The minimum vertical span required
	 * @param spanX
	 *            Horizontal span of the object.
	 * @param spanY
	 *            Vertical span of the object.
	 * @param ignoreOccupied
	 *            If true, the result can be an occupied cell
	 * @param result
	 *            Array in which to place the result, or null (in which case a
	 *            new array will be allocated)
	 * @return The X, Y cell of a vacant area that can contain this object,
	 *         nearest the requested location.
	 */
	int[] findNearestArea(
			int pixelX ,
			int pixelY ,
			int minSpanX ,
			int minSpanY ,
			int spanX ,
			int spanY ,
			View3D ignoreView ,
			boolean ignoreOccupied ,
			int[] result ,
			int[] resultSpan ,
			boolean[][] occupied )
	{
		if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
		{
			if( Workspace3D.EditModeAddItemType != Workspace3D.EditModeAddItemType_NONE )
			{
				return findFirstFitArea( minSpanX , minSpanY , spanX , spanY , result , occupied );
			}
		}
		lazyInitTempRectStack();
		// mark space take by ignoreView as available (method checks if
		// ignoreView is null)
		markCellsAsUnoccupiedForView( ignoreView , occupied );
		// For items with a spanX / spanY > 1, the passed in point (pixelX,
		// pixelY) corresponds
		// to the center of the item, but we are searching based on the top-left
		// cell, so
		// we translate the point over to correspond to the top-left.
		pixelX -= ( mCellWidth + mWidthGap ) * ( spanX - 1 ) / 2f;
		pixelY -= ( mCellHeight + mHeightGap ) * ( spanY - 1 ) / 2f;
		// Keep track of best-scoring drop area
		final int[] bestXY = result != null ? result : new int[2];
		double bestDistance = Double.MAX_VALUE;
		final Rect bestRect = new Rect( -1 , -1 , -1 , -1 );
		final Stack<Rect> validRegions = new Stack<Rect>();
		final int countX = mCountX;
		final int countY = mCountY;
		if( minSpanX <= 0 || minSpanY <= 0 || spanX <= 0 || spanY <= 0 || spanX < minSpanX || spanY < minSpanY )
		{
			return bestXY;
		}
		for( int y = 0 ; y < countY - ( minSpanY - 1 ) ; y++ )
		{
			inner:
			for( int x = 0 ; x < countX - ( minSpanX - 1 ) ; x++ )
			{
				int ySize = -1;
				int xSize = -1;
				if( ignoreOccupied )
				{
					// First, let's see if this thing fits anywhere
					for( int i = 0 ; i < minSpanX ; i++ )
					{
						for( int j = 0 ; j < minSpanY ; j++ )
						{
							if( occupied[x + i][y + j] )
							{
								continue inner;
							}
						}
					}
					xSize = minSpanX;
					ySize = minSpanY;
					// We know that the item will fit at _some_ acceptable size,
					// now let's see
					// how big we can make it. We'll alternate between
					// incrementing x and y spans
					// until we hit a limit.
					boolean incX = true;
					boolean hitMaxX = xSize >= spanX;
					boolean hitMaxY = ySize >= spanY;
					while( !( hitMaxX && hitMaxY ) )
					{
						if( incX && !hitMaxX )
						{
							for( int j = 0 ; j < ySize ; j++ )
							{
								if( x + xSize > countX - 1 || occupied[x + xSize][y + j] )
								{
									// We can't move out horizontally
									hitMaxX = true;
								}
							}
							if( !hitMaxX )
							{
								xSize++;
							}
						}
						else if( !hitMaxY )
						{
							for( int i = 0 ; i < xSize ; i++ )
							{
								if( y + ySize > countY - 1 || occupied[x + i][y + ySize] )
								{
									// We can't move out vertically
									hitMaxY = true;
								}
							}
							if( !hitMaxY )
							{
								ySize++;
							}
						}
						hitMaxX |= xSize >= spanX;
						hitMaxY |= ySize >= spanY;
						incX = !incX;
					}
					incX = true;
					hitMaxX = xSize >= spanX;
					hitMaxY = ySize >= spanY;
				}
				final int[] cellXY = mTmpXY;
				cellToCenterPoint( x , y , cellXY );
				// We verify that the current rect is not a sub-rect of any of
				// our previous
				// candidates. In this case, the current rect is disqualified in
				// favour of the
				// containing rect.
				Rect currentRect = mTempRectStack.pop();
				currentRect.set( x , y , x + xSize , y + ySize );
				boolean contained = false;
				for( Rect r : validRegions )
				{
					if( r.contains( currentRect ) )
					{
						contained = true;
						break;
					}
				}
				validRegions.push( currentRect );
				double distance = Math.sqrt( Math.pow( cellXY[0] - pixelX , 2 ) + Math.pow( cellXY[1] - pixelY , 2 ) );
				if( ( distance <= bestDistance && !contained ) || currentRect.contains( bestRect ) )
				{
					bestDistance = distance;
					bestXY[0] = x;
					bestXY[1] = y;
					if( resultSpan != null )
					{
						resultSpan[0] = xSize;
						resultSpan[1] = ySize;
					}
					bestRect.set( currentRect );
				}
			}
		}
		// re-mark space taken by ignoreView as occupied
		markCellsAsOccupiedForView( ignoreView , occupied );
		// Return -1, -1 if no suitable location found
		if( bestDistance == Double.MAX_VALUE )
		{
			bestXY[0] = -1;
			bestXY[1] = -1;
		}
		recycleTempRects( validRegions );
		return bestXY;
	}
	
	int[] findNearestArea(
			int pixelX ,
			int pixelY ,
			int spanX ,
			int spanY ,
			View3D ignoreView ,
			boolean ignoreOccupied ,
			int[] result )
	{
		return findNearestArea( pixelX , pixelY , spanX , spanY , spanX , spanY , ignoreView , ignoreOccupied , result , null , mOccupied );
	}
	
	/**
	 * Find a vacant area that will fit the given bounds nearest the requested
	 * cell location. Uses Euclidean distance to score multiple vacant areas.
	 * 
	 * @param pixelX
	 *            The X location at which you want to search for a vacant area.
	 * @param pixelY
	 *            The Y location at which you want to search for a vacant area.
	 * @param spanX
	 *            Horizontal span of the object.
	 * @param spanY
	 *            Vertical span of the object.
	 * @param ignoreView
	 *            Considers space occupied by this view as unoccupied
	 * @param result
	 *            Previously returned value to possibly recycle.
	 * @return The X, Y cell of a vacant area that can contain this object,
	 *         nearest the requested location.
	 */
	int[] findNearestVacantArea(
			int pixelX ,
			int pixelY ,
			int minSpanX ,
			int minSpanY ,
			int spanX ,
			int spanY ,
			View3D ignoreView ,
			int[] result ,
			int[] resultSpan )
	{
		return findNearestArea( pixelX , pixelY , minSpanX , minSpanY , spanX , spanY , ignoreView , true , result , resultSpan , mOccupied );
	}
	
	/**
	 * Find a starting cell position that will fit the given bounds nearest the
	 * requested cell location. Uses Euclidean distance to score multiple vacant
	 * areas.
	 * 
	 * @param pixelX
	 *            The X location at which you want to search for a vacant area.
	 * @param pixelY
	 *            The Y location at which you want to search for a vacant area.
	 * @param spanX
	 *            Horizontal span of the object.
	 * @param spanY
	 *            Vertical span of the object.
	 * @param ignoreView
	 *            Considers space occupied by this view as unoccupied
	 * @param result
	 *            Previously returned value to possibly recycle.
	 * @return The X, Y cell of a vacant area that can contain this object,
	 *         nearest the requested location.
	 */
	int[] findNearestArea(
			int pixelX ,
			int pixelY ,
			int spanX ,
			int spanY ,
			int[] result )
	{
		return findNearestArea( pixelX , pixelY , spanX , spanY , null , false , result );
	}
	
	int[] findNearestAreaAvailuable(
			int pixelX ,
			int pixelY ,
			int spanX ,
			int spanY ,
			int[] result )
	{
		findNearestArea( pixelX , pixelY , spanX , spanY , spanX , spanY , null , true , mTargetCell , null , mOccupied );
		result[0] = mTargetCell[0];
		result[1] = mTargetCell[1];
		return result;
	}
	
	private int[] findNearestArea(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			int[] direction ,
			boolean[][] occupied ,
			boolean blockOccupied[][] ,
			int[] result )
	{
		// Keep track of best-scoring drop area
		final int[] bestXY = result != null ? result : new int[2];
		float bestDistance = Float.MAX_VALUE;
		int bestDirectionScore = Integer.MIN_VALUE;
		final int countX = mCountX;
		final int countY = mCountY;
		for( int y = 0 ; y < countY - ( spanY - 1 ) ; y++ )
		{
			inner:
			for( int x = 0 ; x < countX - ( spanX - 1 ) ; x++ )
			{
				// First, let's see if this thing fits anywhere
				for( int i = 0 ; i < spanX ; i++ )
				{
					for( int j = 0 ; j < spanY ; j++ )
					{
						if( occupied[x + i][y + j] && ( blockOccupied == null || blockOccupied[i][j] ) )
						{
							continue inner;
						}
					}
				}
				float distance = (float)Math.sqrt( ( x - cellX ) * ( x - cellX ) + ( y - cellY ) * ( y - cellY ) );
				int[] curDirection = mTmpPoint;
				computeDirectionVector( x - cellX , y - cellY , curDirection );
				// The direction score is just the dot product of the two
				// candidate direction
				// and that passed in.
				int curDirectionScore = direction[0] * curDirection[0] + direction[1] * curDirection[1];
				boolean exactDirectionOnly = false;
				boolean directionMatches = direction[0] == curDirection[0] && direction[0] == curDirection[0];
				if( ( directionMatches || !exactDirectionOnly ) && Float.compare( distance , bestDistance ) < 0 || ( Float.compare( distance , bestDistance ) == 0 && curDirectionScore > bestDirectionScore ) )
				{
					bestDistance = distance;
					bestDirectionScore = curDirectionScore;
					bestXY[0] = x;
					bestXY[1] = y;
				}
			}
		}
		// Return -1, -1 if no suitable location found
		if( bestDistance == Float.MAX_VALUE )
		{
			bestXY[0] = -1;
			bestXY[1] = -1;
		}
		return bestXY;
	}
	
	private void computeDirectionVector(
			float deltaX ,
			float deltaY ,
			int[] result )
	{
		double angle = Math.atan( ( (float)deltaY ) / deltaX );
		result[0] = 0;
		result[1] = 0;
		if( Math.abs( Math.cos( angle ) ) > 0.5f )
		{
			result[0] = (int)Math.signum( deltaX );
		}
		if( Math.abs( Math.sin( angle ) ) > 0.5f )
		{
			result[1] = (int)Math.signum( deltaY );
		}
	}
	
	private void copyOccupiedArray(
			boolean[][] occupied )
	{
		for( int i = 0 ; i < mCountX ; i++ )
		{
			for( int j = 0 ; j < mCountY ; j++ )
			{
				occupied[i][j] = mOccupied[i][j];
			}
		}
	}
	
	boolean existsEmptyCell()
	{
		return findCellForSpan( null , 1 , 1 );
	}
	
	/**
	 * Finds the upper-left coordinate of the first rectangle in the grid that
	 * can hold a cell of the specified dimensions. If intersectX and intersectY
	 * are not -1, then this method will only return coordinates for rectangles
	 * that contain the cell (intersectX, intersectY)
	 * 
	 * @param cellXY
	 *            The array that will contain the position of a vacant cell if
	 *            such a cell can be found.
	 * @param spanX
	 *            The horizontal span of the cell we want to find.
	 * @param spanY
	 *            The vertical span of the cell we want to find.
	 * 
	 * @return True if a vacant cell of the specified dimension was found, false
	 *         otherwise.
	 */
	public boolean findCellForSpan(
			int[] cellXY ,
			int spanX ,
			int spanY )
	{
		return findCellForSpanThatIntersectsIgnoring( cellXY , spanX , spanY , -1 , -1 , null );
	}
	
	public boolean findCellForSpan(
			int[] cellXY ,
			int spanX ,
			int spanY ,
			boolean mediaView3d )
	{
		if( !mediaView3d )
		{
			return findCellForSpan( cellXY , spanX , spanY );
		}
		else
		{
			if( ( this instanceof MediaView3D ) || ( this.name.equals( "newsView" ) ) )
			{
				return false;
			}
			else
			{
				return findCellForSpanFromUpToDown( cellXY , spanX , spanY , -1 , -1 , null );
			}
		}
	}
	
	boolean findCellForSpanFromUpToDown(
			int[] cellXY ,
			int spanX ,
			int spanY ,
			int intersectX ,
			int intersectY ,
			View3D ignoreView )
	{
		// mark space take by ignoreView as available (method checks if
		// ignoreView is null)
		markCellsAsUnoccupiedForView( ignoreView );
		boolean foundCell = false;
		while( true )
		{
			int startX = 0;
			if( intersectX >= 0 )
			{
				startX = Math.max( startX , intersectX - ( spanX - 1 ) );
			}
			int endX = mCountX - ( spanX - 1 );
			if( intersectX >= 0 )
			{
				endX = Math.min( endX , intersectX + ( spanX - 1 ) + ( spanX == 1 ? 1 : 0 ) );
			}
			int startY = 0;
			if( intersectY >= 0 )
			{
				startY = Math.max( startY , intersectY - ( spanY - 1 ) );
			}
			int endY = mCountY - ( spanY - 1 );
			if( intersectY >= 0 )
			{
				endY = Math.min( endY , intersectY + ( spanY - 1 ) + ( spanY == 1 ? 1 : 0 ) );
			}
			for( int y = endY - 1 ; y >= startY && !foundCell ; y-- )
			{
				inner:
				for( int x = startX ; x < endX ; x++ )
				{
					for( int i = 0 ; i < spanX ; i++ )
					{
						for( int j = 0 ; j < spanY ; j++ )
						{
							if( mOccupied[x + i][y + j] )
							{
								// small optimization: we can skip to after the
								// column we just found
								// an occupied cell
								x += i;
								continue inner;
							}
						}
					}
					if( cellXY != null )
					{
						if( ( iLoongLauncher.getInstance().d3dListener.mIsDoingAddShortCut == false ) && ( isCellUesdByShortcutlistToAdd( x , y ) ) )
						{
							continue;
						}
						cellXY[0] = x;
						cellXY[1] = y;
					}
					foundCell = true;
					break;
				}
			}
			if( intersectX == -1 && intersectY == -1 )
			{
				break;
			}
			else
			{
				// if we failed to find anything, try again but without any
				// requirements of
				// intersecting
				intersectX = -1;
				intersectY = -1;
				continue;
			}
		}
		// re-mark space taken by ignoreView as occupied
		markCellsAsOccupiedForView( ignoreView );
		return foundCell;
	}
	
	/**
	 * The superset of the above two methods
	 */
	boolean findCellForSpanThatIntersectsIgnoring(
			int[] cellXY ,
			int spanX ,
			int spanY ,
			int intersectX ,
			int intersectY ,
			View3D ignoreView )
	{
		// mark space take by ignoreView as available (method checks if
		// ignoreView is null)
		markCellsAsUnoccupiedForView( ignoreView );
		boolean foundCell = false;
		while( true )
		{
			int startX = 0;
			if( intersectX >= 0 )
			{
				startX = Math.max( startX , intersectX - ( spanX - 1 ) );
			}
			int endX = mCountX - ( spanX - 1 );
			if( intersectX >= 0 )
			{
				endX = Math.min( endX , intersectX + ( spanX - 1 ) + ( spanX == 1 ? 1 : 0 ) );
			}
			int startY = 0;
			if( intersectY >= 0 )
			{
				startY = Math.max( startY , intersectY - ( spanY - 1 ) );
			}
			int endY = mCountY - ( spanY - 1 );
			if( intersectY >= 0 )
			{
				endY = Math.min( endY , intersectY + ( spanY - 1 ) + ( spanY == 1 ? 1 : 0 ) );
			}
			for( int y = startY ; y < endY && !foundCell ; y++ )
			{
				inner:
				for( int x = startX ; x < endX ; x++ )
				{
					for( int i = 0 ; i < spanX ; i++ )
					{
						for( int j = 0 ; j < spanY ; j++ )
						{
							if( mOccupied[x + i][y + j] )
							{
								// small optimization: we can skip to after the
								// column we just found
								// an occupied cell
								x += i;
								continue inner;
							}
						}
					}
					if( cellXY != null )
					{
						if( ( iLoongLauncher.getInstance().d3dListener.mIsDoingAddShortCut == false ) && ( isCellUesdByShortcutlistToAdd( x , y ) ) )
						{
							continue;
						}
						cellXY[0] = x;
						cellXY[1] = y;
					}
					foundCell = true;
					break;
				}
			}
			if( intersectX == -1 && intersectY == -1 )
			{
				break;
			}
			else
			{
				// if we failed to find anything, try again but without any
				// requirements of
				// intersecting
				intersectX = -1;
				intersectY = -1;
				continue;
			}
		}
		// re-mark space taken by ignoreView as occupied
		markCellsAsOccupiedForView( ignoreView );
		return foundCell;
	}
	
	public boolean onDropCompleted(
			View3D target ,
			boolean success )
	{
		return onDropLeave();
	}
	
	public boolean onDropLeave()
	{
		cellCleanDropStatus();
		if( DefaultLayout.enable_workspace_push_icon )
		{
			cleanLastReorder();
			startReorderTween( REORDER_TWEEN_TYPE_MOVE_TO_ORI );
			Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
		}
		return true;
	}
	
	public void cellCleanDropStatus()
	{
		haveReflect = false;
		haveEstablishFolder = false;
		if( mflect9patchView != null )
		{
			removeView( mflect9patchView );
			mflect9patchView = null;
		}
		for( View3D i : reflectView )
		{
			removeView( i );
			// i.remove();
		}
		// cellSelfDrag = false;
		reflectView.clear();
		if( FolderLargeTween != null )
		{
			FolderLargeTween.free();
			FolderLargeTween = null;
		}
	}
	
	/**
	 * this value only use for array list drop
	 */
	int curWorkSpaceScreen = 0;
	
	public void setScreen(
			int value )
	{
		curWorkSpaceScreen = value;
	}
	
	public void addView(
			View3D view )
	{
		addToList( view );
	}
	
	boolean testValid(
			int[] array )
	{
		boolean res = true;
		if( array[0] < 0 || array[1] < 0 || array[0] >= mCountX || array[1] >= mCountY )
		{
			res = false;
		}
		return res;
	}
	
	public View3D getViewInCell(
			int cellX ,
			int cellY )
	{
		View3D res = null;
		if( cellX < 0 || cellY < 0 || cellY >= mCountX || cellY >= mCountY )
		{
			return null;
		}
		if( mOccupied[cellX][cellY] )
			res = cellViewList[cellX][cellY];
		else
			res = null;
		return res;
	}
	
	/**
	 * 按照所在格子坐标添加图�?
	 * 
	 * @param view
	 * @param x
	 *            :
	 * @param y
	 *            :
	 */
	public boolean addView(
			View3D view ,
			int x ,
			int y )
	{
		int spanX , spanY;
		ItemInfo itemInfo;
		Log.d( "launcher" , "addView:" + view.name );
		if( view == null )
		{
			return false;
		}
		if( x < 0 || x >= mCountX )
		{
			Log.v( "test" , "addView x cell error.... x = " + x );
			return false;
		}
		if( y < 0 || y >= mCountY )
		{
			Log.v( "test" , "addView y cell error.... y = " + y );
			return false;
		}
		clearDragTemp();
		spanX = drapGetSpanX( view );
		spanY = drapGetSpanY( view );
		if( spanX > mCountX )
			spanX = mCountX;
		if( spanY > mCountY )
			spanY = mCountY;
		if( !cellTestDropSucess( x , y , spanX , spanY ) )
		{
			if( RR.net_version )
			{
				return false;
			}
			if( view instanceof Widget3D )
			{
				itemInfo = ( (IconBase3D)view ).getItemInfo();
				Root3D.deleteFromDB( itemInfo );
				Widget3D widget3D = (Widget3D)view;
				Widget3DManager.getInstance().deleteWidget3D( widget3D );
				Log.d( "launcher" , "deleteWidget3D:" + itemInfo.title );
			}
			// SendMsgToAndroid.sendOurToastMsg(R3D.getString(R.string.no_space_add_icon));
			return false;
		}
		if( view instanceof FolderIcon3D )
		{
			if( spanX != 1 || spanY != 1 )
				return false;
		}
		itemInfo = ( (IconBase3D)view ).getItemInfo();
		cellToPoint( x , y , mTargetPoint );
		view.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
		view.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
		itemInfo.cellX = x;
		itemInfo.cellY = y;
		itemInfo.cellTempX = x;
		itemInfo.cellTempY = y;
		itemInfo.x = (int)view.x;
		itemInfo.y = (int)view.y;
		// Root3D.addOrMoveDB(itemInfo);
		if( view instanceof Widget3D )
		{
			clockList.add( view );
		}
		for( int k = 0 ; k < spanX ; k++ )
			for( int j = 0 ; j < spanY ; j++ )
			{
				mOccupied[x + k][y + j] = true;
				cellViewList[x + k][y + j] = view;
			}
		addtoSuperClass( view );
		return true;
	}
	
	public void addAutoSortView(
			ArrayList<View3D> list )
	{
		int spanX;
		int spanY;
		clearDragTemp();
		int curListInd = 0;
		int listTotalCount = list.size();
		View3D temp;
		// xiatian start //双指圈选后自动排序时，从下向上找空位.
		// out:for (int j = mCountY - 1; j >= 0; j--) //xiatian del
		out:
		for( int j = 0 ; j < mCountY ; j++ )
			// xiatian add
			// xiatian end
			for( int i = 0 ; i < mCountX ; i++ )
			{
				if( curListInd >= listTotalCount )
					break out;
				if( !mOccupied[i][j] )
				{
					temp = list.get( curListInd );
					spanX = drapGetSpanX( temp );
					spanY = drapGetSpanY( temp );
					cellToPoint( i , j , mTargetPoint );
					point.x = mTargetPoint[0] + ( spanX * mCellWidth - temp.width ) / 2;
					point.y = mTargetPoint[1] + ( spanY * mCellHeight - temp.height ) / 2;
					addtoSuperClass( temp );
					curListInd++;
					cellViewList[i][j] = temp;
					mOccupied[i][j] = true;
					ItemInfo info = ( (IconBase3D)temp ).getItemInfo();
					info.screen = curWorkSpaceScreen;
					info.x = (int)point.x;
					info.y = (int)point.y;
					info.cellX = i;
					info.cellY = j;
					info.cellTempX = i;
					info.cellTempY = j;
					Root3D.addOrMoveDB( info );
					temp.stopTween();
					temp.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , info.x , info.y , 0 );
				}
			}
		cellSelfDrag = false;
		cellSelfDragView = null;
		onDropLeave();
	}
	
	public Vector2 getTargetAbsolutePosition(
			ArrayList<View3D> list )
	{
		int spanX;
		int spanY;
		clearDragTemp();
		int curListInd = 0;
		int listTotalCount = list.size();
		View3D temp;
		out:
		for( int j = mCountY - 1 ; j >= 0 ; j-- )
		{
			for( int i = 0 ; i < mCountX ; i++ )
			{
				if( curListInd >= listTotalCount )
				{
					break out;
				}
				if( !mOccupied[i][j] )
				{
					temp = list.get( curListInd );
					spanX = drapGetSpanX( temp );
					spanY = drapGetSpanY( temp );
					cellToPointEditMode4x2( i , j , mTargetPoint );
					point.x = mTargetPoint[0] + DesktopEditHost.scaleFactor2 * ( spanX * mCellWidth - temp.width ) / 2;
					point.y = mTargetPoint[1] + DesktopEditHost.scaleFactor2 * ( spanY * mCellHeight - temp.height ) / 2;
					return point;
				}
			}
		}
		return null;
	}
	
	public boolean addViewInDesktopEditMode(
			ArrayList<View3D> list )
	{
		boolean ret = false;
		int spanX;
		int spanY;
		clearDragTemp();
		int curListInd = 0;
		int listTotalCount = list.size();
		View3D temp;
		out:
		for( int j = mCountY - 1 ; j >= 0 ; j-- )
		{
			for( int i = 0 ; i < mCountX ; i++ )
			{
				if( curListInd >= listTotalCount )
					break out;
				if( !mOccupied[i][j] )
				{
					temp = list.get( curListInd );
					spanX = drapGetSpanX( temp );
					spanY = drapGetSpanY( temp );
					cellToPoint( i , j , mTargetPoint );
					point.x = mTargetPoint[0] + ( spanX * mCellWidth - temp.width ) / 2;
					point.y = mTargetPoint[1] + ( spanY * mCellHeight - temp.height ) / 2;
					addtoSuperClass( temp );
					curListInd++;
					cellViewList[i][j] = temp;
					mOccupied[i][j] = true;
					ItemInfo info = ( (IconBase3D)temp ).getItemInfo();
					info.screen = curWorkSpaceScreen;
					info.x = (int)point.x;
					info.y = (int)point.y;
					info.cellX = i;
					info.cellY = j;
					info.cellTempX = i;
					info.cellTempY = j;
					info.spanX = spanX;
					info.spanY = spanY;
					//					Root3D.addOrMoveDB( info );
					temp.setPosition( info.x , info.y );
					ret = true;
				}
			}
		}
		if( !ret )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
		}
		return ret;
	}
	
	public boolean addView(
			ArrayList<View3D> list ,
			int x ,
			int y )
	{
		int temX = x;
		int temY = y;
		boolean val = false;
		boolean addNewPage = false;
		boolean res = true;
		int spanX , spanY;
		ItemInfo itemInfo;
		clearDragTemp();
		//Jone add start
		if( RR.net_version )
		{
			if( isNewPage )
			{
				clearOccupiedCells();
			}
		}
		//Jone add end
		int realY = (int)y;
		if( list.size() > 1 )
		{
			// teapotXu deleted
			// cellDropType = CELL_DROPTYPE_ARRAY;
		}
		else
		{
			if( cellDropType != CELL_DROPTYPE_SINGLE_DROP_FOLDER )
				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
		}
		if( !Workspace3D.isMoved && Workspace3D.isDragFromIcon3D )
		{
			cellDropType = CELL_DROPTYPE_NOT_MOVE;
		}
		Workspace3D.isDragFromIcon3D = false;
		switch( cellDropType )
		{
			case CELL_DROPTYPE_NOT_MOVE:
				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
				for( View3D i : list )
				{
					itemInfo = ( (IconBase3D)i ).getItemInfo();
					if( res && addView( i , itemInfo.cellX , itemInfo.cellY ) )
						res = true;
					else
						res = false;
				}
				break;
			case CELL_DROPTYPE_SINGLE_DROP:
			case CELL_DROPTYPE_FOLDER:
			case CELL_DROPTYPE_WIDGET:
			case CELL_DROPTYPE_WIDGET_3D:
			default:
				for( View3D i : list )
				{
					cellDropType = CELL_DROPTYPE_SINGLE_DROP;
					spanX = drapGetSpanX( i );
					spanY = drapGetSpanY( i );
					//					if( ( i instanceof Widget3D ) && ( DefaultLayout.isWidgetLoadByInternal( ( (Widget3D)i ).packageName ) ) )
					//					{
					//						for( int j = 0 ; j < DefaultLayout.allWidget.size() ; j++ )
					//						{
					//							if( DefaultLayout.allWidget.get( j ).pkgName.equals( ( ( (Widget3D)i ).packageName ) ) )
					//							{
					//								spanX = DefaultLayout.allWidget.get( j ).spanX;
					//								spanY = DefaultLayout.allWidget.get( j ).spanY;
					//							}
					//						}
					//					}
					if( spanX > mCountX )
						spanX = mCountX;
					if( spanY > mCountY )
						spanY = mCountY;
					findNearestArea( (int)x , (int)realY , spanX , spanY , spanX , spanY , null , true , mTargetCell , null , mOccupied );
					if( mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY )
					{
						res = false;
					}
					if( res && addView( i , mTargetCell[0] , mTargetCell[1] ) )
						res = true;
					else
						res = false;
				}
				break;
			case CELL_DROPTYPE_ARRAY:
				res = produceMultiList( list , x , y , false );
				break;
			// teapotXu add start for Multi-drop folder
			case CELL_DROPTYPE_ARRAY_DROP_FOLDER:
				View3D mfolder ,
				mquiet;
				// View3D view = list.get(0);
				mfolder = cellFindFolderInTemp();
				if( mfolder != null && mfolder.getVisible() )
				{
					// xiatian add start //fix bug:Preview view position Y
					// error,when cell make and preview folder in
					// folder_iphone_style
					if( ( (FolderIcon3D)mfolder ).folder_style == FolderIcon3D.folder_iphone_style )
					{
						mfolder.y -= ( mfolder.height - ( (FolderIcon3D)mfolder ).getIconBmpHeight() );
					}
					// xiatian add end
					findCellbyCoodinate( (int)mfolder.x , (int)mfolder.y , mTempCell );
					mquiet = cellViewList[mTempCell[0]][mTempCell[1]];
					cellDropType = CELL_DROPTYPE_ARRAY_DROP_FOLDER;
					if( mquiet == null || !( mquiet instanceof ViewCircled3D ) )
						break;
					ArrayList<View3D> addList = new ArrayList<View3D>();
					addList.add( mquiet );
					for( View3D tmpView : list )
					{
						addList.add( tmpView );
					}
					if( addList.size() > R3D.folder_max_num )
					{
						res = produceMultiList( list , x , y , false );
						SendMsgToAndroid.sendOurToastMsg( R3D.folder3D_full );
					}
					else
					{
						ItemInfo info , foldInfo;
						addtoSuperClass( mfolder );
						foldInfo = ( (FolderIcon3D)mfolder ).getItemInfo();
						foldInfo.screen = curWorkSpaceScreen;
						foldInfo.x = (int)mfolder.x;
						foldInfo.y = (int)mfolder.y;
						foldInfo.cellX = mTempCell[0];
						foldInfo.cellY = mTempCell[1];
						foldInfo.cellTempX = mTempCell[0];
						foldInfo.cellTempY = mTempCell[1];
						Root3D.addOrMoveDB( foldInfo );
						iLoongLauncher.getInstance().addFolderInfoToSFolders( (UserFolderInfo)foldInfo );
						// ArrayList<View3D> addList = new ArrayList<View3D>();
						// addList.add(mquiet);
						//
						// for(View3D tmpView: list){
						// addList.add(tmpView);
						// }
						FolderIcon3D fo = ( (FolderIcon3D)mfolder );
						fo.changeFolderFrontRegion( false );
						fo.onDrop( addList , 0 , 0 );
						Color color = mfolder.getColor();
						color.a = 1.0f;
						mfolder.setColor( color );
						mfolder.setScale( 1.0f , 1.0f );
						cellRemoveFolderInTemp();
						mfolder.setReflect( false );
						cellViewList[mTempCell[0]][mTempCell[1]] = mfolder;
						mOccupied[mTempCell[0]][mTempCell[1]] = true;
						this.setTag( mfolder );
						viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
						addList.clear();
						res = true;
					}
				}
				else
				{
					// multi-icon drop icons are all Icon3D
					spanX = 1;
					spanY = 1;
					cellDropType = CELL_DROPTYPE_ARRAY;
					findNearestArea( (int)x , (int)realY , spanX , spanY , spanX , spanY , null , true , mTargetCell , null , mOccupied );
					if( mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY )
					{
						res = false;
						break;
					}
					if( res && produceMultiList( list , x , y , false ) )
						res = true;
					else
						res = false;
				}
				break;
			// teapotXu add end for Multi-drop folder
			case CELL_DROPTYPE_SINGLE_DROP_FOLDER:
				View3D folder ,
				quiet;
				View3D view = list.get( 0 );
				folder = cellFindFolderInTemp();
				if( folder != null && folder.getVisible() )
				{
					// xiatian add start //fix bug:Preview view position Y
					// error,when cell make and preview folder in
					// folder_iphone_style
					if( ( (FolderIcon3D)folder ).folder_style == FolderIcon3D.folder_iphone_style )
					{
						folder.y -= ( folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight() );
					}
					// xiatian add end
					findCellbyCoodinate( (int)folder.x , (int)folder.y , mTempCell );
					quiet = cellViewList[mTempCell[0]][mTempCell[1]];
					cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
					if( quiet == null || !( quiet instanceof ViewCircled3D ) )
						break;
					ItemInfo info , foldInfo;
					addtoSuperClass( folder );
					foldInfo = ( (FolderIcon3D)folder ).getItemInfo();
					foldInfo.screen = curWorkSpaceScreen;
					foldInfo.x = (int)folder.x;
					foldInfo.y = (int)folder.y;
					foldInfo.cellX = mTempCell[0];
					foldInfo.cellY = mTempCell[1];
					foldInfo.cellTempX = mTempCell[0];
					foldInfo.cellTempY = mTempCell[1];
					Root3D.addOrMoveDB( foldInfo );
					iLoongLauncher.getInstance().addFolderInfoToSFolders( (UserFolderInfo)foldInfo );
					ArrayList<View3D> addList = new ArrayList<View3D>();
					addList.add( quiet );
					addList.add( view );
					FolderIcon3D fo = ( (FolderIcon3D)folder );
					fo.changeFolderFrontRegion( false );
					fo.onDrop( addList , 0 , 0 );
					Color color = folder.getColor();
					color.a = 1.0f;
					folder.setColor( color );
					folder.setScale( 1.0f , 1.0f );
					cellRemoveFolderInTemp();
					folder.setReflect( false );
					cellViewList[mTempCell[0]][mTempCell[1]] = folder;
					mOccupied[mTempCell[0]][mTempCell[1]] = true;
					this.setTag( folder );
					viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
					addList.clear();
					res = true;
				}
				else
				{
					spanX = drapGetSpanX( view );
					spanY = drapGetSpanY( view );
					cellDropType = CELL_DROPTYPE_SINGLE_DROP;
					findNearestArea( (int)x , (int)realY , spanX , spanY , spanX , spanY , null , true , mTargetCell , null , mOccupied );
					if( mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY )
					{
						res = false;
						break;
					}
					if( res && addView( view , mTargetCell[0] , mTargetCell[1] ) )
						res = true;
					else
						res = false;
				}
				break;
		}
		//Jone add start
		if( res )
		{
			isNewPage = false;
		}
		//Jone add end
		if( !res )
		{
			if( cellSelfDrag && cellDropType != CELL_DROPTYPE_ARRAY )
			{
				// SendMsgToAndroid.sendOurToastMsg("无法放置在此位置上！");
				// View3D back = cellSelfDragView;
				//
				// if (back != null) {
				// spanX = drapGetSpanX(back);
				// spanY = drapGetSpanY(back);
				// cellToPoint(cellSelfDragFrom[0], cellSelfDragFrom[1],
				// mTargetPoint);
				// back.x = mTargetPoint[0] + (spanX * mCellWidth - back.width)
				// / 2;
				// back.y = mTargetPoint[1] + (spanY * mCellHeight -
				// back.height) / 2;
				//
				// Log.v("test", "x, y = " + mTargetCell[0] + ", " +
				// mTargetCell[1]);
				//
				// for (int k = 0; k < spanX; k++)
				// for (int j = 0; j < spanY; j++) {
				// mOccupied[cellSelfDragFrom[0] + k][cellSelfDragFrom[1] + j] =
				// true;
				// cellViewList[cellSelfDragFrom[0] + k][cellSelfDragFrom[1] +
				// j] = back;
				// }
				// addtoSuperClass(back);
				// reflectView.remove(0);
				// }
			}
			else
			{
				//Jone start 
				if( false )//( RR.net_version )
				{
					addNewPage = addViewsToNewPage( list , temX , temY );
					if( addNewPage )
					{
						isNewPage = false;
					}
				}
				if( addNewPage == false )
				//Jone end
				{
					IconBase3D view = (IconBase3D)list.get( 0 );
					ItemInfo info = view.getItemInfo();
					itemInfo = ( (IconBase3D)view ).getItemInfo();
					if( view instanceof Widget3D && itemInfo.container == -1 )
					{/*
						 * widget3D
						 * from
						 * mainmenu
						 */
						Root3D.deleteFromDB( info );
						Widget3D widget3D = (Widget3D)view;
						Widget3DManager.getInstance().deleteWidget3D( widget3D );
						Log.d( "launcher" , "deleteWidget3D:" + info.title );
					}
					if( false )//( RR.net_version )
						SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getResources().getString( RR.string.page_num_over_maximum ) );
					else
						SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.no_space_add_icon ) );
				}
			}
		}
		cellSelfDrag = false;
		cellSelfDragView = null;
		onDropLeave();
		if( val || addNewPage )
		{
			res = true;
		}
		return res;
	}
	
	public boolean addViewsToNewPage(
			ArrayList<View3D> toAddList ,
			int x ,
			int y )
	{
		boolean result;
		ArrayList<View3D> cellLayoutList;
		Workspace3D workspace = Desktop3DListener.root.getWorkspace();
		PageIndicator3D pageIndicator = Desktop3DListener.root.getPageIndicator();
		int addIndex = pageIndicator.getIndex();
		CellLayout3D addedCell = new CellLayout3D( "celllayout" );
		cellLayoutList = workspace.getViewList();
		if( ( Workspace3D.b_editmode_include_addpage && ( DefaultLayout.default_workspace_pagecount_max + 2 <= workspace.getPageNum() ) ) )
		{
			View3D firstView = Desktop3DListener.root.findView( "lastView" );
			View3D lastView = Desktop3DListener.root.findView( "firstView" );
			if( firstView != null && lastView != null )
			{
				workspace.removePage( firstView );
				workspace.removePage( lastView );
			}
			Workspace3D.b_editmode_include_addpage = false;
			return false;
		}
		else if( !Workspace3D.b_editmode_include_addpage && ( DefaultLayout.default_workspace_pagecount_max <= workspace.getPageNum() ) )
		{
			return false;
		}
		cellLayoutList.add( addIndex , addedCell );
		workspace.addView( addedCell );
		cellLayoutList = workspace.getViewList();
		for( int i = addIndex + 1 ; i < cellLayoutList.size() ; i++ )
		{
			ViewGroup3D cell = (ViewGroup3D)cellLayoutList.get( i );
			for( int j = 0 ; j < cell.getChildCount() ; j++ )
			{
				View3D v = cell.getChildAt( j );
				if( v instanceof IconBase3D )
				{
					boolean rel = false;
					if( rel == false )
					{
						ItemInfo info1 = ( (IconBase3D)v ).getItemInfo();
						info1.screen = i;
						Root3D.addOrMoveDB( info1 );
					}
				}
			}
		}
		for( View3D toAdd : toAddList )
		{
			ItemInfo info = ( (IconBase3D)toAdd ).getItemInfo();
			info.screen = addIndex;
			Root3D.addOrMoveDB( info );
			//				 CellLayout3D cell = (CellLayout3D)cellLayoutList.get( addIndex );
			//				
			//				cell.addtoSuperClass( toAdd );
		}
		//		 copyOccupiedCells();
		//		 CellLayout3D nextCell = (CellLayout3D)cellLayoutList.get( addIndex+1 );
		//			
		//		 nextCell.setOccupiedCells(mCopyOccupied,mCopycellViewList);
		//			
		ThemeManager.getInstance().getThemeDB().SaveScreenCount( workspace.getPageNum() );
		//				SendMsgToAndroid.sendAddWorkspaceCellMsg(-1);
		SendMsgToAndroid.sendAddWorkspaceCellMsg( addIndex );
		pageIndicator.setPageNum( workspace.getPageNum() );
		CellLayout3D cell = (CellLayout3D)cellLayoutList.get( addIndex );
		cell.removeAllViews();
		result = cell.addView( toAddList , x , y );
		workspace.setCurrentPage( addIndex );
		Gdx.graphics.requestRendering();
		return result;
	}
	
	public void cellFindViewAndRemove(
			View3D view )
	{
		for( int i = 0 ; i < mCountX ; i++ )
			for( int j = 0 ; j < mCountY ; j++ )
			{
				if( cellViewList[i][j] == view )
				{
					mOccupied[i][j] = false;
					cellViewList[i][j] = null;
					Log.d( "launcher" , "remove" + view.name );
				}
			}
	}
	
	public void cellFindViewAndRemove(
			int cellX ,
			int cellY )
	{
		Log.d( "launcher" , "remove:" + cellX + "," + cellY );
		mOccupied[cellX][cellY] = false;
		View3D view = cellViewList[cellX][cellY];
		if( view != null )
			view.remove();
		cellViewList[cellX][cellY] = null;
	}
	
	boolean cellTestDropSucess(
			int h ,
			int v ,
			int spanX ,
			int spanY )
	{
		boolean res = true;
		int maxX = h + spanX - 1;
		int maxY = v + spanY - 1;
		if( maxX < 0 || maxY < 0 || maxX >= mCountX || maxY >= mCountY )
		{
			return false;
		}
		for( int k = 0 ; k < spanX ; k++ )
			for( int j = 0 ; j < spanY ; j++ )
			{
				if( mOccupied[h + k][v + j] )
				{
					res = false;
					break;
				}
			}
		return res;
	}
	
	boolean haveReflect = false;
	boolean haveEstablishFolder = false;
	ArrayList<View3D> reflectView = new ArrayList<View3D>();
	
	@SuppressWarnings( "unchecked" )
	public boolean produceMultiList(
			ArrayList<View3D> list ,
			float x ,
			float y ,
			boolean preview )
	{
		boolean res = false;
		int remainGrid = 0;
		int spanX;
		int spanY;
		int listTotalCount = list.size();
		int curListInd = 0;
		View3D temp , view;
		boolean needFolder = false;
		if( listTotalCount < 1 )
			return res;
		for( int i = 0 ; i < mCountX ; i++ )
			for( int j = 0 ; j < mCountY ; j++ )
			{
				if( !mOccupied[i][j] )
				{
					remainGrid++;
				}
			}
		if( remainGrid == 0 )
			return res;
		if( remainGrid + R3D.folder_max_num - 1 < listTotalCount )
		{
			if( !preview )
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.exceed_num_add_icon ) );
			return res;// 选择的数量超过最大�? }
		}
		// teapotXu add start
		int[] nearestCell = null;
		nearestCell = findNearestArea( (int)x , (int)y , 1 , 1 , nearestCell );
		int i = nearestCell[0];
		int j = nearestCell[1];
		out:
		for( int y_index = 0 ; y_index < mCountY ; y_index++ )
			for( int x_index = 0 ; x_index < mCountX ; x_index++ )
			{
				if( nearestCell[0] + x_index < mCountX )
				{
					i = nearestCell[0] + x_index;
				}
				else
				{
					i = mCountX - 1 - x_index;
				}
				if( nearestCell[1] + y_index < mCountY )
				{
					j = nearestCell[1] + y_index;
				}
				else
				{
					j = mCountY - 1 - y_index;
				}
				// teapotXu add end
				if( curListInd >= listTotalCount )
					break out;
				if( remainGrid == 1 )
				{
					if( listTotalCount - curListInd == 1 )
					{
					}
					else
					{
						needFolder = true;
					}
				}
				if( !mOccupied[i][j] && needFolder )
				{
					temp = cellFindFolderInTemp();
					if( temp == null )
						if( preview )
						{
							temp = cellMakeFolder( i , j , true , true );
							reflectView.add( temp );
						}
						else
						{
							temp = cellMakeFolder( i , j , false , true );
						}
					cellToPoint( i , j , mTargetPoint );
					temp.x = mTargetPoint[0] + ( 1 * mCellWidth - temp.width ) / 2;
					temp.y = mTargetPoint[1] + ( 1 * mCellHeight - temp.height ) / 2;
					addtoSuperClass( temp );
					if( !preview )
					{
						FolderIcon3D fo = ( (FolderIcon3D)temp );
						fo.mInfo.x = (int)temp.x;
						fo.mInfo.y = (int)temp.y;
						cellViewList[i][j] = temp;
						mOccupied[i][j] = true;
						ArrayList<View3D> leftList;// = new ArrayList<View3D>();
						leftList = (ArrayList<View3D>)list.clone();
						for( int k = 0 ; k < curListInd ; k++ )
							leftList.remove( list.get( k ) );
						// fo.addFolderNode(leftList);
						ItemInfo info = ( (FolderIcon3D)temp ).getItemInfo();
						info.screen = curWorkSpaceScreen;
						info.x = (int)temp.x;
						info.y = (int)temp.y;
						info.cellX = i;
						info.cellY = j;
						info.cellTempX = i;
						info.cellTempY = j;
						Root3D.addOrMoveDB( info );
						iLoongLauncher.getInstance().addFolderInfoToSFolders( (UserFolderInfo)info );
						fo.changeFolderFrontRegion( false );
						fo.setReflect( false );
						fo.onDrop( leftList , 0 , 0 );
						this.setTag( fo );
						viewParent.onCtrlEvent( this , MSG_ADD_DRAGLAYER );
						reflectView.remove( temp );
					}
					break out;
				}
				if( !mOccupied[i][j] )
				{
					view = list.get( curListInd );
					spanX = drapGetSpanX( view );
					spanY = drapGetSpanY( view );
					if( preview )
					{
						// teapotXu add start
						temp = cellFindTheViewInTemp( view );
						if( temp == null )
						{
							temp = view.clone();
							Color color = temp.getColor();
							color.a = 0.5f;
							temp.setColor( color );
						}
						else
						{
							temp.setVisible( true );
							cellToPoint( i , j , mTargetPoint );
							temp.x = mTargetPoint[0] + ( spanX * mCellWidth - temp.width ) / 2;
							temp.y = mTargetPoint[1] + ( spanY * mCellHeight - temp.height ) / 2;
							curListInd++;
							remainGrid--;
							res = true;
							if( remainGrid == 0 )
								break out;
							continue;
						}
						// temp = view.clone();
						// Color color = temp.getColor();
						// color.a = 0.5f;
						// temp.setColor(color);
						// teapotXu add end
					}
					else
					{
						temp = view;
					}
					cellToPoint( i , j , mTargetPoint );
					temp.x = mTargetPoint[0] + ( spanX * mCellWidth - temp.width ) / 2;
					temp.y = mTargetPoint[1] + ( spanY * mCellHeight - temp.height ) / 2;
					addtoSuperClass( temp );
					if( preview )
						reflectView.add( temp );
					curListInd++;
					remainGrid--;
					if( !preview )
					{
						cellViewList[i][j] = temp;
						mOccupied[i][j] = true;
						ItemInfo info = ( (IconBase3D)temp ).getItemInfo();
						info.screen = curWorkSpaceScreen;
						info.x = (int)temp.x;
						info.y = (int)temp.y;
						info.cellX = i;
						info.cellY = j;
						info.cellTempX = i;
						info.cellTempY = j;
						int tx = (int)temp.x;
						int ty = (int)temp.y;
						if( listTotalCount > 1 )
						{
							if( x != tx || y != ty )
							{
								temp.setPosition( x , y );
								temp.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , tx , ty , 0 );
							}
						}
						Root3D.addOrMoveDB( info );
						if( temp instanceof Icon3D )
						{
							Icon3D iconView = (Icon3D)view;
							iconView.setItemInfo( iconView.getItemInfo() );
						}
					}
					res = true;
				}
				if( remainGrid == 0 )
					break out;
			}
		res = true;
		return res;
	}
	
	final public static int CELL_SENSITIVE_DIP = 10 * (int)Utils3D.getDensity();
	final static int CELL_MAKE_FOLDER = 0;
	int cellDropType = CELL_DROPTYPE_SINGLE_DROP;
	final static int CELL_DROPTYPE_NONE = 0;
	final static int CELL_DROPTYPE_SINGLE_DROP = 1;
	final static int CELL_DROPTYPE_SINGLE_DROP_FOLDER = 2;
	final static int CELL_DROPTYPE_ARRAY = 3;
	final static int CELL_DROPTYPE_FOLDER = 4;
	final static int CELL_DROPTYPE_WIDGET = 5;
	final static int CELL_DROPTYPE_WIDGET_3D = 6;
	final static int CELL_DROPTYPE_ARRAY_DROP_FOLDER = 7; // teapotXu add:
															// 多选多个ICON形成文件夹
	final static int CELL_DROPTYPE_NOT_MOVE = 8;
	
	// final static int CELL_MOVE_ = 0;
	void setCellDropTypeArrayDrop()
	{
		cellDropType = CELL_DROPTYPE_ARRAY;
	}
	
	int getCellDropType()
	{
		return cellDropType;
	}
	
	View3D cellFindFolderInTemp()
	{
		View3D view = null;
		for( View3D i : reflectView )
		{
			if( i instanceof FolderIcon3D )
			{
				view = i;
				break;
			}
		}
		return view;
	}
	
	// teapotXu add start
	// 根据传入的View3d 查找reflectView中对应的view
	View3D cellFindTheViewInTemp(
			View3D original_view )
	{
		View3D view = null;
		for( View3D i : reflectView )
		{
			if( i instanceof ViewCircled3D )
			{
				if( i.name != null && i.name.equals( original_view.name ) )
				{
					if( i instanceof Icon3D && original_view instanceof Icon3D )
					{
						ShortcutInfo i_sInfo = (ShortcutInfo)( (Icon3D)i ).getItemInfo();
						ShortcutInfo orig_sInfo = (ShortcutInfo)( (Icon3D)original_view ).getItemInfo();
						// 需要区分桌面上不同位置上的相同ICON
						if( i_sInfo != null && orig_sInfo != null && i_sInfo.cellX == orig_sInfo.cellX && i_sInfo.cellY == orig_sInfo.cellY )
						{
							view = i;
							break;
						}
					}
					else
					{
						view = i;
						break;
					}
				}
			}
		}
		return view;
	}
	
	void cellRemoveFolderInTemp()
	{
		View3D view = null;
		for( View3D i : reflectView )
		{
			if( i instanceof FolderIcon3D )
			{
				view = i;
				break;
			}
		}
		if( view != null )
		{
			reflectView.remove( view );
		}
	}
	
	View3D cellFindSingleViewInTemp()
	{
		View3D view = null;
		for( View3D i : reflectView )
		{
			if( i instanceof ViewCircled3D )
			{
				view = i;
				break;
			}
		}
		return view;
	}
	
	View3D cellFindWidgetInTemp()
	{
		View3D view = null;
		for( View3D i : reflectView )
		{
			if( i instanceof Widget )
			{
				view = i;
				break;
			}
		}
		return view;
	}
	
	View3D cellFindWidget3DInTemp()
	{
		View3D view = null;
		for( View3D i : reflectView )
		{
			if( i instanceof Widget3D )
			{
				view = i;
				break;
			}
		}
		return view;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( iLoongLauncher.isShowNews )
		{
			return true;
		}
		clearDragTemp();
		if( super.onLongClick( x , y ) )
		{
			if( findCellbyCoodinate( (int)x , (int)y , mTempCell ) )
			{
				View3D view = cellViewList[mTempCell[0]][mTempCell[1]];
				if( view != null )
				{
					Log.v( "test" , "<Error> Celllayout3D onLongClick have picked one view, this shouldn't happen!!!" );
				}
			}
			return true;
		}
		else
			return false;
	}
	
	boolean cellSelfDrag = false;
	View3D cellSelfDragView = null;
	int[] cellSelfDragFrom = new int[2];
	
	void cellClearWidgetOccupiedList(
			View3D view )
	{
		for( int i = 0 ; i < mCountX ; i++ )
			for( int j = 0 ; j < mCountY ; j++ )
			{
				View3D temp = cellViewList[i][j];
				if( temp == view )
				{
					mOccupied[i][j] = false;
					cellViewList[i][j] = null;
				}
			}
	}
	
	int CellgetViewType(
			View3D view )
	{
		int res = 0;
		if( view instanceof ViewCircled3D )
			res = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
		else if( view instanceof FolderIcon3D )
			res = CELL_DROPTYPE_FOLDER;
		else if( view instanceof Widget )
			res = CELL_DROPTYPE_WIDGET;
		else if( view instanceof Widget3D )
			res = CELL_DROPTYPE_WIDGET_3D;
		else
			res = 0;
		return res;
	}
	
	ArrayList<View3D> tempViewList = new ArrayList<View3D>();
	int m = 1;
	
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		boolean res = true;
		View3D view;
		if( list == null )
			return res;
		if( x < 0 || y < this.getPaddingBottom() || y > ( height - this.getPaddingTop() ) )
		{
			return false;
		}
		int realY = (int)y;
		if( list.size() > 1 )
		{
			// teapotXu add start for Multi-drop folder
			clearDragTemp();
			View3D targetView , folder;
			folder = cellFindFolderInTemp();
			int spanX = 1;
			int spanY = 1;
			mTargetCell = findNearestArea( (int)x , (int)realY , spanX , spanY , mTargetCell );
			if( getLastReorderX() != mTargetCell[0] || getLastReorderY() != mTargetCell[1] )
			{
				cellChanged = true;
				Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
				setLastReorder( mTargetCell[0] , mTargetCell[1] );
			}
			if( cellChanged )
			{
				cellChanged = false;
				boolean isOccupy = cellViewList[mTargetCell[0]][mTargetCell[1]] == null ? false : true;
				if( !isOccupy )
				{
					if( haveEstablishFolder && folder != null )
					{
						if( folder.isVisible() )
						{
							folder.setVisible( false );
							reflectView.remove( folder );
							removeView( folder );
						}
					}
					haveEstablishFolder = false;
					cellDropType = CELL_DROPTYPE_ARRAY;
					haveReflect = produceMultiList( list , x , realY , true );
					res = true;
				}
				else
				{
					for( View3D tempview : list )
					{
						// 当需要形成文件夹时，此时需要将refkectView中ICon的阴影set disVisible
						View3D tmp = cellFindTheViewInTemp( tempview );
						if( tmp != null )
						{
							tmp.setVisible( false );
						}
					}
					view = list.get( 0 );
					if( findCellbyCoodinate( (int)x , realY , mTempCell ) )
					{
						targetView = getCellViewAtPosition( mTempCell[0] , mTempCell[1] );
						if( testCellapproach( view , targetView , (int)x , realY ) == CELL_MAKE_FOLDER )
						{
							if( !haveEstablishFolder )
							{
								folder = cellFindFolderInTemp();
								if( folder != null )
								{
									removeView( folder );
									reflectView.remove( folder );
									folder = null;
								}
								folder = cellMakeFolder( mTargetCell[0] , mTargetCell[1] , true , true );
								cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
								folder.x = mTargetPoint[0] + ( spanX * mCellWidth - folder.width ) / 2;
								folder.y = mTargetPoint[1] + ( spanY * mCellHeight - folder.height ) / 2;
								if( ( (FolderIcon3D)folder ).folder_style == FolderIcon3D.folder_iphone_style )
								{
									folder.y += ( folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight() );
								}
								reflectView.add( folder );
								haveEstablishFolder = true;
							}
							else
							{
								folder = cellFindFolderInTemp();
								if( folder == null )
								{
									folder = cellMakeFolder( mTargetCell[0] , mTargetCell[1] , true , true );
									reflectView.add( folder );
									haveEstablishFolder = true;
								}
								FolderLargeAnim( folder );
								cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
								folder.x = mTargetPoint[0] + ( spanX * mCellWidth - folder.width ) / 2;
								folder.y = mTargetPoint[1] + ( spanY * mCellHeight - folder.height ) / 2;
								if( ( (FolderIcon3D)folder ).folder_style == FolderIcon3D.folder_iphone_style )
								{
									folder.y += ( folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight() );
								}
							}
							cellDropType = CELL_DROPTYPE_ARRAY_DROP_FOLDER;
						}
						else
						{
							folder = cellFindFolderInTemp();
							if( folder != null )
								if( folder.isVisible() )
								{
									FolderInvisible( folder );
									reflectView.remove( folder );
									removeView( folder );
								}
						}
						if( folder == null || ( folder != null && !folder.getVisible() ) )
						{
							cellToPoint( mTempCell[0] , mTempCell[1] , mTargetPoint );
						}
					}
				}
				/*for( int i = 0 ; i < reflectView.size() ; i++ )
				{
					reflectView.get( i ).stopTween();
					reflectView.get( i ).color.a = 0.0f;
					reflectView.get( i ).startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.8f , 0.5f , 0 , 0 );
				}*/
			}
		}
		else
		{
			view = list.get( 0 );// only drag one source
			clearDragTemp();
			if( DefaultLayout.enable_workspace_push_icon )
			{
				if( view instanceof ViewCircled3D || view instanceof FolderIcon3D || view instanceof Widget3D || view instanceof Widget || view instanceof Widget2DShortcut )
				{
					final int spanX = drapGetSpanX( view );
					final int spanY = drapGetSpanY( view );
					mTargetCell = findNearestArea( (int)x , (int)realY , spanX , spanY , mTargetCell );
					//					boolean isOccupy = testIfViewIsOccupy( view , mTargetCell[0] , mTargetCell[1] , spanX , spanY );
					boolean isOccupy = isNearestDropLocationOccupied( (int)x , (int)realY , spanX , spanY , view , mTargetCell );
					if( getLastReorderX() != mTargetCell[0] || getLastReorderY() != mTargetCell[1] )
					{
						cellChanged = true;
						Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
						setLastReorder( mTargetCell[0] , mTargetCell[1] );
						Log.v( "jbc" , "pushIcon changedCell:[" + mTargetCell[0] + "," + mTargetCell[1] + "] isOccupy:" + isOccupy );
					}
					if( !isOccupy )
					{
						if( view instanceof ViewCircled3D )
						{
							View3D temp , folder;
							temp = cellFindSingleViewInTemp();
							folder = cellFindFolderInTemp();
							if( folder != null )
							{
								if( folder.isVisible() )
								{
									FolderInvisible( folder );
								}
							}
							if( !haveReflect )
							{
								if( dropReflectView( view , (int)x , (int)realY ) != null )
									haveReflect = true;
								cellDropType = CELL_DROPTYPE_SINGLE_DROP;
							}
							else if( temp != null )
							{
								temp.setVisible( true );
								cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
								temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
								temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
							}
						}
						else if( view instanceof FolderIcon3D || view instanceof Widget3D || view instanceof Widget )
						{
							View3D temp = mflect9patchView;
							if( !haveReflect )
							{
								if( temp == null )
									temp = reflect9PatchView( view , (int)x , (int)y );
								haveReflect = true;
								cellDropType = CELL_DROPTYPE_FOLDER;
							}
							else if( temp != null )
							{
								temp.setVisible( true );
								cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
								temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
								temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
							}
						}
						if( cellChanged )
						{
							// diaosixu
							if( reflectView.size() > 0 )
							{
								reflectView.get( 0 ).stopTween();
								reflectView.get( 0 ).color.a = 0.0f;
								reflectView.get( 0 ).startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.8f , 0.5f , 0 , 0 );
							}
							else
							{
								if(mflect9patchView!=null)
								{
								mflect9patchView.stopTween();
								mflect9patchView.color.a = 0.0f;
								mflect9patchView.startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.8f , 0.5f , 0 , 0 );
								}
							}
							cellChanged = false;
							startReorderTween( REORDER_TWEEN_TYPE_MOVE_TO_ORI );
						}
					}
					else
					//pushIcon isOccupy
					{
						if( cellChanged )
						{
							cellChanged = false;
							waitStartTime = System.currentTimeMillis();
							isWaiting = true;
						}
						if( isWaiting )
						{
							long curTime = System.currentTimeMillis();
							if( curTime - waitStartTime > REORDER_WAIT_DUARATION * 1000 )
							{
								final View3D dragTargetView = getCellViewAtPosition( mTargetCell[0] , mTargetCell[1] );
								float targetCellDistance = getDistanceFromCell( (int)x , realY , mTargetCell );
								Log.v( "jbc" , "pushIcon distance:" + targetCellDistance );
								manageFolderFeedback( view , targetCellDistance , dragTargetView );
								if( Workspace3D.getDragMode() == Workspace3D.DRAG_MODE_NONE )
								{
									int[] resultSpan = new int[2];
									mTargetCell = createArea( (int)x , realY , spanX , spanY , spanX , spanY , view , mTargetCell , resultSpan , CellLayout3D.MODE_DRAG_OVER );
									if( mTargetCell[0] < 0 || mTargetCell[1] < 0 )
									{
										View3D temp , folder;
										temp = cellFindSingleViewInTemp();
										folder = cellFindFolderInTemp();
										if( temp != null )
										{
											temp.setVisible( false );
										}
										if( folder != null )
										{
											if( folder.isVisible() )
											{
												FolderInvisible( folder );
											}
										}
									}
									else
									{
										if( view instanceof ViewCircled3D )
										{
											View3D temp , folder;
											temp = cellFindSingleViewInTemp();
											folder = cellFindFolderInTemp();
											if( folder != null )
											{
												if( folder.isVisible() )
												{
													FolderInvisible( folder );
												}
											}
											if( !haveReflect )
											{
												if( temp == null )
												{
													temp = view.clone();
													temp.setReflect( true );
													cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
													temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
													temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
													Color color = temp.getColor();
													color.a = 0.5f;
													temp.setColor( color );
													addtoSuperClass( temp );
													reflectView.add( temp );
												}
												haveReflect = true;
												cellDropType = CELL_DROPTYPE_SINGLE_DROP;
											}
											else if( temp != null )
											{
												temp.setVisible( true );
												cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
												temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
												temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
											}
										}
										else if( view instanceof FolderIcon3D || view instanceof Widget3D || view instanceof Widget )
										{
											View3D temp = mflect9patchView;
											if( !haveReflect )
											{
												if( temp == null )
												{
													temp = new View3D( "CellReflect" );
													temp.setReflect( true );
													temp.setBackgroud( Workspace3D.reflectView );
													temp.setSize( view.width , view.height );
													cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
													temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
													temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
													Color color = temp.getColor();
													color.a = 0.8f;
													temp.setColor( color );
													addtoSuperClass( temp );
													mflect9patchView = temp;
												}
												haveReflect = true;
												cellDropType = CELL_DROPTYPE_FOLDER;
											}
											else if( temp != null )
											{
												temp.setVisible( true );
												cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
												temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
												temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
											}
										}
									}
								}
								else if( Workspace3D.getDragMode() == Workspace3D.DRAG_MODE_CREATE_FOLDER )
								{
									startReorderTween( REORDER_TWEEN_TYPE_MOVE_TO_ORI );
									View3D temp = cellFindSingleViewInTemp();
									if( temp != null )
									{
										temp.setVisible( false );
									}
									View3D folder = cellFindFolderInTemp();
									if( folder == null )
									{
										folder = cellMakeFolder( mTargetCell[0] , mTargetCell[1] , true , true );
										reflectView.add( folder );
										haveEstablishFolder = true;
									}
									else
									{
										FolderLargeAnim( folder );
									}
									cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
									folder.x = mTargetPoint[0] + ( spanX * mCellWidth - folder.width ) / 2;
									folder.y = mTargetPoint[1] + ( spanY * mCellHeight - folder.height ) / 2;
									if( ( (FolderIcon3D)folder ).folder_style == FolderIcon3D.folder_iphone_style )
									{
										folder.y += ( folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight() );
									}
									cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
								}
								else if( Workspace3D.getDragMode() == Workspace3D.DRAG_MODE_ADD_TO_FOLDER )
								{
									startReorderTween( REORDER_TWEEN_TYPE_MOVE_TO_ORI );
									View3D temp = cellFindSingleViewInTemp();
									if( temp != null )
									{
										temp.setVisible( false );
									}
								}
								isWaiting = false;
							}
						}
					}
					if( view instanceof Widget3D )
					{
						if( ( (Widget3D)view ).getPackageName().equals( "com.iLoong.Calendar" ) )
						{
							( (Widget3D)view ).onCellMove( mTargetCell[0] , mTargetCell[1] );
						}
					}
				}
			}
			else
			{
				if( view instanceof ViewCircled3D )
				{
					View3D temp , targetView , folder;
					temp = cellFindSingleViewInTemp();
					int spanX = drapGetSpanX( view );
					int spanY = drapGetSpanY( view );
					mTargetCell = findNearestArea( (int)x , (int)realY , spanX , spanY , mTargetCell );
					if( getLastReorderX() != mTargetCell[0] || getLastReorderY() != mTargetCell[1] )
					{
						cellChanged = true;
						Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
						setLastReorder( mTargetCell[0] , mTargetCell[1] );
					}
					if( cellChanged )
					{
						cellChanged = false;
						boolean isOccupy = testIfViewIsOccupy( view , mTargetCell[0] , mTargetCell[1] , spanX , spanY );
						if( !isOccupy )
						{
							folder = cellFindFolderInTemp();
							if( folder != null )
							{
								if( folder.isVisible() )
								{
									FolderInvisible( folder );
								}
							}
							if( !haveReflect )
							{
								if( dropReflectView( view , (int)x , (int)realY ) != null )
									haveReflect = true;
								cellDropType = CELL_DROPTYPE_SINGLE_DROP;
							}
							else if( temp != null )
							{
								temp.setVisible( true );
								cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
								temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
								temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
							}
						}
						else
						{
							if( temp != null )
								temp.setVisible( false );
							if( findCellbyCoodinate( (int)x , realY , mTempCell ) )
							{
								targetView = getCellViewAtPosition( mTempCell[0] , mTempCell[1] );
								if( testCellapproach( view , targetView , (int)x , realY ) == CELL_MAKE_FOLDER )
								{
									folder = cellFindFolderInTemp();
									if( folder == null )
									{
										folder = cellMakeFolder( mTargetCell[0] , mTargetCell[1] , true , true );
										reflectView.add( folder );
										haveEstablishFolder = true;
									}
									else
									{
										FolderLargeAnim( folder );
									}
									cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
									folder.x = mTargetPoint[0] + ( spanX * mCellWidth - folder.width ) / 2;
									folder.y = mTargetPoint[1] + ( spanY * mCellHeight - folder.height ) / 2;
									// xiatian add start //fix bug:Preview view
									// position Y error,when cell make and
									// preview
									// folder in folder_iphone_style
									if( ( (FolderIcon3D)folder ).folder_style == FolderIcon3D.folder_iphone_style )
									{
										folder.y += ( folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight() );
									}
									// xiatian add end
									cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
								}
								else
								{
									folder = cellFindFolderInTemp();
									if( folder != null )
										if( folder.isVisible() )
										{
											FolderInvisible( folder );
										}
								}
								if( folder == null || ( folder != null && !folder.getVisible() ) )
								{
									cellToPoint( mTempCell[0] , mTempCell[1] , mTargetPoint );
								}
							}
						}
						res = true;
					}
				}
				else if( view instanceof FolderIcon3D || view instanceof Widget3D || view instanceof Widget )
				{
					View3D temp = mflect9patchView;
					int spanX = drapGetSpanX( view );
					int spanY = drapGetSpanY( view );
					mTargetCell = findNearestArea( (int)x , (int)y , spanX , spanY , spanX , spanY , null , true , mTargetCell , null , mOccupied );
					if( mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY )
					{
						return true;
					}
					if( getLastReorderX() != mTargetCell[0] || getLastReorderY() != mTargetCell[1] )
					{
						cellChanged = true;
						Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
						setLastReorder( mTargetCell[0] , mTargetCell[1] );
					}
					if( cellChanged )
					{
						cellChanged = false;
						if( !haveReflect )
						{
							if( temp == null )
								temp = reflect9PatchView( view , (int)x , (int)y );
							haveReflect = true;
							cellDropType = CELL_DROPTYPE_FOLDER;
						}
						else if( temp != null )
						{
							temp.setVisible( true );
							cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
							temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
							temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
						}
					}
				}
			}
		}
		return res;
	}
	
	View3D mflect9patchView = null;
	
	View3D getCellViewAtPosition(
			int cellX ,
			int cellY )
	{
		if( cellX < 0 || cellY < 0 || cellX >= mCountX || cellY >= mCountY )
		{
			Log.v( "test" , "getCellViewAtPosition exception occur!!!!" );
			return null;
		}
		return cellViewList[cellX][cellY];
	}
	
	boolean findCellbyCoodinate(
			int x ,
			int y ,
			int[] result )
	{
		boolean res = false;
		result[0] = x / mCellWidth;
		int start = getPaddingBottom();
		if( y > start && y < start + mCountY * mCellHeight )
		{
			result[1] = ( y - start ) / mCellHeight;
			res = true;
		}
		else
			res = false;
		if( res )
		{
			result[0] = result[0] >= mCountX ? mCountX - 1 : result[0];
			result[1] = result[1] >= mCountY ? mCountY - 1 : result[1];
		}
		return res;
	}
	
	boolean findCellbyView(
			View3D view ,
			int[] result )
	{
		boolean res = false;
		if( view == null )
			return res;
		result[0] = (int)view.x / mCellWidth;
		int start = getPaddingBottom();
		if( (int)view.y > start && y < mCellWidth * mCellHeight )
		{
			result[1] = ( (int)view.y - start ) / mCellHeight;
			res = true;
		}
		else
			res = false;
		if( res )
		{
			result[0] = result[0] >= mCountX ? mCountX - 1 : result[0];
			result[1] = result[1] >= mCountY ? mCountY - 1 : result[1];
		}
		return res;
	}
	
	View3D createVirturFolder(
			boolean compress )
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.x = 0;
		folderInfo.y = 0;
		FolderIcon3D folderIcon3D = new FolderIcon3D( "FolderIcon3DView" , folderInfo );
		if( compress )
			folderIcon3D.changeFolderFrontRegion( true );
		return folderIcon3D;
	}
	
	View3D cellMakeFolder(
			int cellX ,
			int cellY ,
			boolean preview ,
			boolean compress )
	{
		View3D folder;
		cellToPoint( cellX , cellY , mTargetPoint );
		folder = createVirturFolder( compress );
		folder.setReflect( true );
		folder.x = mTargetPoint[0] + ( mCellWidth - folder.width ) / 2;
		folder.y = mTargetPoint[1] + ( mCellHeight - folder.height ) / 2;
		// xiatian add start //fix bug:Preview view position Y error,when cell
		// make and preview folder in folder_iphone_style
		if( ( (FolderIcon3D)folder ).folder_style == FolderIcon3D.folder_iphone_style )
		{
			folder.y += ( folder.height - ( (FolderIcon3D)folder ).getIconBmpHeight() );
		}
		super.addViewAt( 0 , folder );
		if( preview )
		{
			FolderLargeAnim( folder );
		}
		return folder;
	}
	
	int testCellapproach(
			View3D dyna ,
			View3D quiet ,
			int x ,
			int y )
	{
		int res = 0;
		boolean isFolder = false;
		if( dyna == null || quiet == null )
			return 1;
		if( !( quiet instanceof ViewCircled3D ) )
			return 1;
		Vector2 v1 = new Vector2();
		dyna.toAbsoluteCoords( v1 );
		Vector2 v2 = new Vector2();
		quiet.toAbsoluteCoords( v2 );
		Log.v( "test" , "dyna x, y = " + v1.x + ", " + v1.y );
		Log.v( "test" , "quiet x, y = " + v2.x + ", " + v2.y );
		int xOffset = (int)( v2.x + quiet.width / 2 - v1.x - dyna.width / 2 );
		int yOffset = (int)( v2.y + quiet.height / 2 - v1.y - dyna.height / 2 );
		if( (int)dyna.width - CELL_SENSITIVE_DIP > Math.abs( xOffset ) )
			isFolder = true;
		else
			isFolder = false;
		if( isFolder && (int)dyna.height - CELL_SENSITIVE_DIP > Math.abs( yOffset ) )
		{
		}
		else
			isFolder = false;
		if( isFolder )
			res = CELL_MAKE_FOLDER;
		else
			res = 1;
		return res;
	}
	
	int drapGetSpanX(
			View3D view )
	{
		if( view instanceof Widget2DShortcut )
		{
			int spanX = ( (Widget2DShortcut)view ).widgetInfo.cellHSpan;
			if( spanX > mCountX )
			{
				spanX = mCountX;
			}
			return spanX;
		}
		ItemInfo itemInfo = ( (IconBase3D)view ).getItemInfo();
		if( itemInfo != null && itemInfo.spanX != 0 )
		{
			if( itemInfo.spanX > mCountX )
				itemInfo.spanX = mCountX;
			return itemInfo.spanX;
		}
		else
			return ( (int)view.width - 1 ) / mCellWidth + 1;
	}
	
	int drapGetSpanY(
			View3D view )
	{
		if( view instanceof Widget2DShortcut )
		{
			int spanY = ( (Widget2DShortcut)view ).widgetInfo.cellVSpan;
			if( spanY > mCountY )
			{
				spanY = mCountY;
			}
			return spanY;
		}
		ItemInfo itemInfo = ( (IconBase3D)view ).getItemInfo();
		if( itemInfo != null && itemInfo.spanY != 0 )
		{
			if( itemInfo.spanY > mCountY )
				itemInfo.spanY = mCountY;
			return itemInfo.spanY;
		}
		else
			return ( (int)view.height - 1 ) / mCellHeight + 1;
	}
	
	/* for efficient */
	private int[] mTargetCell = new int[2];
	private int[] mTargetPoint = new int[2];
	private int[] mTempCell = new int[2];
	/*for pushIcon add by jbc*/
	private int[] mOriPoint = new int[2];
	private int[] mTempPoint = new int[2];
	private int[] mSwingPoint = new int[2];
	
	void clearDragTemp()
	{
		mTargetCell[0] = 0;
		mTargetCell[1] = 0;
		mTargetPoint[0] = 0;
		mTargetPoint[1] = 0;
		mTempCell[0] = 0;
		mTempCell[1] = 0;
	}
	
	/**
	 * drop type: ViewCircled3D: icon FolderIcon3D:folder Widget: Widget3D:
	 * 
	 * @param view
	 * @param h
	 * @param v
	 * @return
	 */
	boolean testIfViewIsOccupy(
			View3D view ,
			int h ,
			int v ,
			int spanX ,
			int spanY )
	{
		if( view == null )
			return false;
		View3D quiet = cellViewList[h][v];
		if( quiet == null )
			return false;
		return true;
	}
	
	View3D dropReflectView(
			View3D view ,
			int x ,
			int y )
	{
		if( !( view instanceof Icon3D ) )
			return null;
		View3D temp;
		int spanX = drapGetSpanX( view );
		int spanY = drapGetSpanY( view );
		mTargetCell = findNearestArea( x , y , spanX , spanY , mTargetCell );
		if( testIfViewIsOccupy( view , mTargetCell[0] , mTargetCell[1] , spanX , spanY ) )
			return null;
		temp = view.clone();
		temp.setReflect( true );
		cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
		temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
		temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
		Color color = temp.getColor();
		color.a = 0.5f;
		temp.setColor( color );
		addtoSuperClass( temp );
		reflectView.add( temp );
		return temp;
	}
	
	View3D reflect9PatchView(
			View3D view ,
			int x ,
			int y )
	{
		View3D temp;
		int spanX = drapGetSpanX( view );
		int spanY = drapGetSpanY( view );
		mTargetCell = findNearestArea( (int)x , (int)y , spanX , spanY , spanX , spanY , null , true , mTargetCell , null , mOccupied );
		if( mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY )
		{
			return null;
		}
		temp = new View3D( "CellReflect" );
		temp.setReflect( true );
		temp.setBackgroud( Workspace3D.reflectView );
		temp.setSize( view.width , view.height );
		cellToPoint( mTargetCell[0] , mTargetCell[1] , mTargetPoint );
		temp.x = mTargetPoint[0] + ( spanX * mCellWidth - view.width ) / 2;
		temp.y = mTargetPoint[1] + ( spanY * mCellHeight - view.height ) / 2;
		Color color = temp.getColor();
		color.a = 0.8f;
		temp.setColor( color );
		addtoSuperClass( temp );
		mflect9patchView = temp;
		return temp;
	}
	
	public View3D reflectView(
			View3D view )
	{
		View3D view3d = null;
		ItemInfo info = null;
		if( view instanceof Widget3D )
		{
			info = ( (Widget3D)view ).getItemInfo();
		}
		else if( view instanceof Widget )
		{
			info = ( (Widget)view ).getItemInfo();
		}
		if( info != null )
		{
			if( info.cellX < 0 || info.cellY < 0 || info.cellX >= mCountX || info.cellY >= mCountY )
			{
				return null;
			}
			view3d = new View3D( "zoomkuang" );
			view3d.setReflect( true );
			view3d.setBackgroud( Workspace3D.reflectView );
			view3d.setSize( view.width , view.height );
			cellToPoint( info.cellX , info.cellY , mTargetPoint );
			view3d.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2;
			view3d.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2;
			return view3d;
		}
		return null;
	}
	
	public ViewGroup3D reflectZoomView(
			View3D view )
	{
		float scale = Utils3D.getScreenWidth() / 720f;
		ViewGroup3D vg = new ViewGroup3D( "ZoomView" );
		ItemInfo info = null;
		if( view instanceof Widget3D )
		{
			info = ( (Widget3D)view ).getItemInfo();
		}
		else if( view instanceof Widget )
		{
			info = ( (Widget)view ).getItemInfo();
		}
		if( info != null )
		{
			if( info.cellX < 0 || info.cellY < 0 || info.cellX >= mCountX || info.cellY >= mCountY )
			{
				return null;
			}
			ZoomBox temp = new ZoomBox( "ZoomViewbg" , view );
			temp.setReflect( true );
			temp.setBackgroud( Workspace3D.zoomView );
			temp.setSize( view.width , view.height );
			cellToPoint( info.cellX , info.cellY , mTargetPoint );
			temp.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2;
			temp.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2;
			vg.addView( temp );
			ZoomArrow viewarrowleft;
			if( info.cellX == 0 )
			{
				viewarrowleft = new ZoomArrow( "viewarrowleft" , Workspace3D.zoomarrow_right , view );
				viewarrowleft.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowleft.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 - viewarrowleft.width / 2;
				viewarrowleft.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 + ( view.height - viewarrowleft.height ) / 2;
			}
			else
			{
				viewarrowleft = new ZoomArrow( "viewarrowleft" , Workspace3D.zoomarrow , view );
				viewarrowleft.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowleft.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 - viewarrowleft.width / 2;
				viewarrowleft.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 + ( view.height - viewarrowleft.height ) / 2;
			}
			vg.addView( viewarrowleft );
			ZoomArrow viewarrowright;
			if( info.cellX + info.spanX == 4 )
			{
				viewarrowright = new ZoomArrow( "viewarrowright" , Workspace3D.zoomarrow_left , view );
				viewarrowright.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowright.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 + view.width - viewarrowright.width / 2;
				viewarrowright.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 + ( temp.height - viewarrowright.height ) / 2;
			}
			else
			{
				viewarrowright = new ZoomArrow( "viewarrowright" , Workspace3D.zoomarrow , view );
				viewarrowright.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowright.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 + view.width - viewarrowright.width / 2;
				viewarrowright.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 + ( temp.height - viewarrowright.height ) / 2;
			}
			vg.addView( viewarrowright );
			ZoomArrow viewarrowtop;
			if( info.cellY + info.spanY == 4 )
			{
				viewarrowtop = new ZoomArrow( "viewarrowtop" , Workspace3D.zoomarrow_bottom , view );
				viewarrowtop.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowtop.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 + ( view.width - viewarrowtop.width ) / 2;
				viewarrowtop.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 + temp.height - viewarrowtop.height / 2;
			}
			else
			{
				viewarrowtop = new ZoomArrow( "viewarrowtop" , Workspace3D.zoomarrow , view );
				viewarrowtop.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowtop.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 + ( view.width - viewarrowtop.width ) / 2;
				viewarrowtop.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 + temp.height - viewarrowtop.height / 2;
			}
			vg.addView( viewarrowtop );
			ZoomArrow viewarrowbottom;
			if( info.cellY == 0 )
			{
				viewarrowbottom = new ZoomArrow( "viewarrowbottom" , Workspace3D.zoomarrow_top , view );
				viewarrowbottom.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowbottom.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 + ( view.width - viewarrowbottom.width ) / 2;
				viewarrowbottom.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 - viewarrowbottom.height / 2;
			}
			else
			{
				viewarrowbottom = new ZoomArrow( "viewarrowbottom" , Workspace3D.zoomarrow , view );
				viewarrowbottom.setSize( zoomWidth * scale , zoomHeight * scale );
				viewarrowbottom.x = mTargetPoint[0] + ( info.spanX * mCellWidth - view.width ) / 2 + ( view.width - viewarrowbottom.width ) / 2;
				viewarrowbottom.y = mTargetPoint[1] + ( info.spanY * mCellHeight - view.height ) / 2 - viewarrowbottom.height / 2;
			}
			vg.addView( viewarrowbottom );
		}
		return vg;
	}
	
	public void changeRegion(
			View3D widget ,
			ZoomArrow viewarrowleft ,
			ZoomArrow viewarrowright ,
			ZoomArrow viewarrowtop ,
			ZoomArrow viewarrowbottom )
	{
		ItemInfo info = null;
		if( widget instanceof Widget3D )
		{
			info = ( (Widget3D)widget ).getItemInfo();
		}
		else if( widget instanceof Widget )
		{
			info = ( (Widget)widget ).getItemInfo();
		}
		if( info != null )
		{
			cellToPoint( info.cellX , info.cellY , mTargetPoint );
			if( info.cellX == 0 )
			{
				viewarrowleft.region = Workspace3D.zoomarrow_right;
				viewarrowleft.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 - viewarrowleft.width / 2;
				viewarrowleft.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 + ( widget.height - viewarrowleft.height ) / 2;
			}
			else
			{
				viewarrowleft.region = Workspace3D.zoomarrow;
				viewarrowleft.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 - viewarrowleft.width / 2;
				viewarrowleft.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 + ( widget.height - viewarrowleft.height ) / 2;
			}
			if( info.cellX + info.spanX == 4 )
			{
				viewarrowright.region = Workspace3D.zoomarrow_left;
				viewarrowright.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 + widget.width - viewarrowright.width / 2;
				viewarrowright.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 + ( widget.height - viewarrowright.height ) / 2;
			}
			else
			{
				viewarrowright.region = Workspace3D.zoomarrow;
				viewarrowright.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 + widget.width - viewarrowright.width / 2;
				viewarrowright.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 + ( widget.height - viewarrowright.height ) / 2;
			}
			if( info.cellY + info.spanY == 4 )
			{
				viewarrowtop.region = Workspace3D.zoomarrow_bottom;
				viewarrowtop.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 + ( widget.width - viewarrowtop.width ) / 2;
				viewarrowtop.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 + widget.height - viewarrowtop.height / 2;
			}
			else
			{
				viewarrowtop.region = Workspace3D.zoomarrow;
				viewarrowtop.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 + ( widget.width - viewarrowtop.width ) / 2;
				viewarrowtop.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 + widget.height - viewarrowtop.height / 2;
			}
			if( info.cellY == 0 )
			{
				viewarrowbottom.region = Workspace3D.zoomarrow_top;
				viewarrowbottom.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 + ( widget.width - viewarrowbottom.width ) / 2;
				viewarrowbottom.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 - viewarrowbottom.height / 2;
			}
			else
			{
				viewarrowbottom.region = Workspace3D.zoomarrow;
				viewarrowbottom.x = mTargetPoint[0] + ( info.spanX * mCellWidth - widget.width ) / 2 + ( widget.width - viewarrowbottom.width ) / 2;
				viewarrowbottom.y = mTargetPoint[1] + ( info.spanY * mCellHeight - widget.height ) / 2 - viewarrowbottom.height / 2;
			}
		}
	}
	
	public void scrollForChange(
			float moveX ,
			float moveY ,
			int what ,
			View3D widget )
	{
		ViewGroup3D vg = (ViewGroup3D)iLoongLauncher.getInstance().getD3dListener().getRoot().findView( "ZoomView" );
		if( vg != null )
		{
			ZoomBox zoombox = (ZoomBox)vg.findView( "ZoomViewbg" );
			ZoomArrow viewarrowleft = (ZoomArrow)vg.findView( "viewarrowleft" );
			ZoomArrow viewarrowright = (ZoomArrow)vg.findView( "viewarrowright" );
			ZoomArrow viewarrowtop = (ZoomArrow)vg.findView( "viewarrowtop" );
			ZoomArrow viewarrowbottom = (ZoomArrow)vg.findView( "viewarrowbottom" );
			ItemInfo info = null;
			if( widget instanceof Widget3D )
			{
				info = ( (Widget3D)widget ).getItemInfo();
			}
			else if( widget instanceof Widget )
			{
				info = ( (Widget)widget ).getItemInfo();
			}
			if( info != null )
			{
				cellToPoint( info.cellX , info.cellY , mTargetPoint );
				switch( what )
				{
					case LeftMove:
						if( !( zoombox.width < R3D.Workspace_cell_each_width || zoombox.width > R3D.Workspace_cell_each_width * 4 ) )
						{
							if( zoombox.x >= 0 )
							{
								if( info.cellX == 0 )
								{
									if( moveX > 0 )
									{
										viewarrowleft.region = Workspace3D.zoomarrow;
									}
								}
								else
								{
									if( moveX < 0 && zoombox.width >= ( ( info.spanX + info.cellX ) * R3D.Workspace_cell_each_width ) )
									{
										viewarrowleft.region = Workspace3D.zoomarrow_right;
									}
								}
								zoombox.width = zoombox.width - moveX;
								zoombox.x = zoombox.x + moveX;
								viewarrowleft.x = viewarrowleft.x + moveX;
								viewarrowtop.x = viewarrowtop.x + moveX / 2;
								viewarrowbottom.x = viewarrowbottom.x + moveX / 2;
							}
						}
						break;
					case RightMove:
						if( !( zoombox.width < R3D.Workspace_cell_each_width || zoombox.width > R3D.Workspace_cell_each_width * 4 ) )
						{
							if( zoombox.x <= Utils3D.getScreenWidth() )
							{
								if( info.cellX + info.spanX == 4 )
								{
									if( moveX < 0 )
									{
										viewarrowright.region = Workspace3D.zoomarrow;
									}
								}
								else
								{
									if( moveX > 0 && zoombox.width >= ( ( info.spanX - info.cellX + 4 ) * R3D.Workspace_cell_each_width ) )
									{
										viewarrowright.region = Workspace3D.zoomarrow_left;
									}
								}
								zoombox.width = zoombox.width + moveX;
								viewarrowright.x = viewarrowright.x + moveX;
								viewarrowtop.x = viewarrowtop.x + moveX / 2;
								viewarrowbottom.x = viewarrowbottom.x + moveX / 2;
							}
						}
						break;
					case TopMove:
						if( !( zoombox.height < R3D.Workspace_cell_each_height || zoombox.height > R3D.Workspace_cell_each_height * 4 ) )
						{
							if( info.cellY + info.spanY == 4 )
							{
								if( moveY > 0 )
								{
									viewarrowtop.region = Workspace3D.zoomarrow;
								}
							}
							else
							{
								if( moveY < 0 && zoombox.height >= R3D.Workspace_cell_each_height * ( info.spanY + 4 - info.cellY ) )
								{
									viewarrowtop.region = Workspace3D.zoomarrow_bottom;
								}
							}
							zoombox.height = zoombox.height - moveY;
							viewarrowleft.y = viewarrowleft.y - moveY / 2;
							viewarrowright.y = viewarrowright.y - moveY / 2;
							viewarrowtop.y = viewarrowtop.y - moveY;
						}
						break;
					case BottomMove:
						if( !( zoombox.height < R3D.Workspace_cell_each_height || zoombox.height > R3D.Workspace_cell_each_height * 4 ) )
						{
							//							Log.d( "lxl" , "zoombox.y:" + zoombox.y );
							if( zoombox.y >= R3D.hot_obj_height )
							{
								if( info.cellY == 0 )
								{
									if( moveY < 0 )
									{
										viewarrowbottom.region = Workspace3D.zoomarrow;
									}
								}
								else
								{
									if( moveY > 0 && zoombox.height >= R3D.Workspace_cell_each_height * ( info.spanY + info.cellY ) )
									{
										viewarrowbottom.region = Workspace3D.zoomarrow_top;
									}
								}
								zoombox.height = zoombox.height + moveY;
								zoombox.y = zoombox.y - moveY;
								viewarrowleft.y = viewarrowleft.y - moveY / 2;
								viewarrowright.y = viewarrowright.y - moveY / 2;
								viewarrowbottom.y = viewarrowbottom.y - moveY;
							}
						}
						break;
					default:
						break;
				}
			}
		}
	}
	
	public boolean isHasLocation(
			float moveX ,
			float moveY ,
			View3D widget ,
			int what )
	{
		CellLayout3D celllayout = iLoongLauncher.getInstance().getD3dListener().getRoot().getWorkspace().getCurrentCellLayout();
		ItemInfo widgetinfo = null;
		if( widget instanceof Widget3D )
		{
			widgetinfo = ( (Widget3D)widget ).getItemInfo();
		}
		else if( widget instanceof Widget )
		{
			widgetinfo = ( (Widget)widget ).getItemInfo();
		}
		if( widgetinfo != null )
		{
			float TempX = 0;
			float TempY = 0;
			if( !( widgetinfo.cellX < 0 || widgetinfo.cellY < 0 || widgetinfo.cellX >= mCountX || widgetinfo.cellY >= mCountY ) )
			{
				switch( what )
				{
					case LeftMove:
						if( moveX > 0 )
						{
							if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
							{
								TempX = moveX - moveX % R3D.Workspace_cell_each_width;
							}
							else
							{
								TempX = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
							}
							for( int i = 0 ; i < (int)( TempX / R3D.Workspace_cell_each_width ) ; i++ )
							{
								for( int j = 0 ; j < widgetinfo.spanY ; j++ )
								{
									if( celllayout.mOccupied[widgetinfo.cellX - i - 1][widgetinfo.cellY + j] )
									{
										return true;
									}
								}
							}
							return false;
						}
						else
						{
							return true;
						}
					case RightMove:
						if( moveX > 0 )
						{
							if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
							{
								TempX = moveX - moveX % R3D.Workspace_cell_each_width;
							}
							else
							{
								TempX = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
							}
							for( int i = 0 ; i < (int)( TempX / R3D.Workspace_cell_each_width ) ; i++ )
							{
								for( int j = 0 ; j < widgetinfo.spanY ; j++ )
								{
									if( celllayout.mOccupied[widgetinfo.cellX + widgetinfo.spanX + i][widgetinfo.cellY + j] )
									{
										return true;
									}
								}
							}
							return false;
						}
						else
						{
							return true;
						}
					case TopMove:
						if( moveY > 0 )
						{
							if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
							{
								TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
							}
							else
							{
								TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
							}
							for( int i = 0 ; i < widgetinfo.spanX ; i++ )
							{
								for( int j = 0 ; j < (int)( TempY / R3D.Workspace_cell_each_height ) ; j++ )
								{
									if( celllayout.mOccupied[widgetinfo.cellX + i][widgetinfo.cellY + widgetinfo.spanY + j] )
									{
										return true;
									}
								}
							}
							return false;
						}
						else
						{
							return true;
						}
					case BottomMove:
						if( moveY > 0 )
						{
							if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
							{
								TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
							}
							else
							{
								TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
							}
							for( int i = 0 ; i < widgetinfo.spanX ; i++ )
							{
								for( int j = 0 ; j < (int)( TempY / R3D.Workspace_cell_each_height ) ; j++ )
								{
									if( celllayout.mOccupied[widgetinfo.cellX + i][widgetinfo.cellY - j - 1] )
									{
										return true;
									}
								}
							}
							return false;
						}
						else
						{
							return true;
						}
					default:
						break;
				}
			}
		}
		return false;
	}
	
	public void adjustZoombox(
			float moveX ,
			float moveY ,
			int what ,
			View3D widget )
	{
		ViewGroup3D vg = (ViewGroup3D)iLoongLauncher.getInstance().getD3dListener().getRoot().findView( "ZoomView" );
		if( vg != null )
		{
			ZoomBox zoombox = (ZoomBox)vg.findView( "ZoomViewbg" );
			ZoomArrow viewarrowleft = (ZoomArrow)vg.findView( "viewarrowleft" );
			ZoomArrow viewarrowright = (ZoomArrow)vg.findView( "viewarrowright" );
			ZoomArrow viewarrowtop = (ZoomArrow)vg.findView( "viewarrowtop" );
			ZoomArrow viewarrowbottom = (ZoomArrow)vg.findView( "viewarrowbottom" );
			float TempX = 0;
			float TempY = 0;
			float TempX1 = 0;
			float TempY1 = 0;
			CellLayout3D celllayout = iLoongLauncher.getInstance().getD3dListener().getRoot().getWorkspace().getCurrentCellLayout();
			if( widget instanceof Widget3D )
			{
				Widget3DInfo info = ( (Widget3D)widget ).getItemInfo();
				if( !( info.cellX < 0 || info.cellY < 0 || info.cellX >= mCountX || info.cellY >= mCountY ) )
				{
					switch( what )
					{
						case LeftMove:
							if( moveX > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
									{
										TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width - TempX;
										zoombox.x = zoombox.x + TempX;
										viewarrowleft.x = viewarrowleft.x + TempX;
										viewarrowtop.x = viewarrowtop.x + TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									else
									{
										TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width + TempX;
										zoombox.x = zoombox.x - TempX;
										viewarrowleft.x = viewarrowleft.x - TempX;
										viewarrowtop.x = viewarrowtop.x - TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									widget.setPosition( widget.x - TempX1 , widget.y );
									Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
									provider.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
									Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
									for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
									{
										for( int j = 0 ; j < info.spanY ; j++ )
										{
											celllayout.mOccupied[info.cellX - i - 1][info.cellY + j] = true;
											celllayout.cellViewList[info.cellX - i - 1][info.cellY + j] = widget;
										}
									}
									info.cellX = info.cellX - (int)( TempX1 / R3D.Workspace_cell_each_width );
									( (Widget3D)widget ).onChangeSize( moveX , moveY , 1 , info.cellX , info.cellY );
									( (Widget3D)widget ).setItemInfo( info );
								}
								else
								{
									zoombox.width = zoombox.width - moveX;
									zoombox.x = zoombox.x + moveX;
									viewarrowleft.x = viewarrowleft.x + moveX;
									viewarrowtop.x = viewarrowtop.x + moveX / 2;
									viewarrowbottom.x = viewarrowbottom.x + moveX / 2;
								}
							}
							else
							{
								if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
								{
									TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width + TempX;
									zoombox.x = zoombox.x - TempX;
									viewarrowleft.x = viewarrowleft.x - TempX;
									viewarrowtop.x = viewarrowtop.x - TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									widget.width = widget.width - TempX1;
								}
								else
								{
									TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width - TempX;
									zoombox.x = zoombox.x + TempX;
									viewarrowleft.x = viewarrowleft.x + TempX;
									viewarrowtop.x = viewarrowtop.x + TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
									widget.width = widget.width - TempX1;
								}
								widget.setPosition( widget.x + TempX1 , widget.y );
								Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
								provider.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
								Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
								for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
								{
									for( int j = 0 ; j < info.spanY ; j++ )
									{
										celllayout.mOccupied[info.cellX + i][info.cellY + j] = false;
										celllayout.cellViewList[info.cellX + i][info.cellY + j] = null;
									}
								}
								info.cellX = info.cellX + (int)( TempX1 / R3D.Workspace_cell_each_width );
								( (Widget3D)widget ).onChangeSize( moveX , moveY , 1 , info.cellX , info.cellY );
								( (Widget3D)widget ).setItemInfo( info );
							}
							break;
						case RightMove:
							if( moveX > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
									{
										TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width - TempX;
										viewarrowright.x = viewarrowright.x - TempX;
										viewarrowtop.x = viewarrowtop.x - TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									else
									{
										TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width + TempX;
										viewarrowright.x = viewarrowright.x + TempX;
										viewarrowtop.x = viewarrowtop.x + TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									( (Widget3D)widget ).onChangeSize( moveX , moveY , 2 , info.cellX , info.cellY );
									Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
									provider.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
									Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
									for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
									{
										for( int j = 0 ; j < info.spanY ; j++ )
										{
											celllayout.mOccupied[info.cellX + info.spanX + i][info.cellY + j] = true;
											celllayout.cellViewList[info.cellX + info.spanX + i][info.cellY + j] = widget;
										}
									}
								}
								else
								{
									zoombox.width = zoombox.width - moveX;
									viewarrowright.x = viewarrowright.x - moveX;
									viewarrowtop.x = viewarrowtop.x - moveX / 2;
									viewarrowbottom.x = viewarrowbottom.x - moveX / 2;
								}
							}
							else
							{
								if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
								{
									TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width + TempX;
									viewarrowright.x = viewarrowright.x + TempX;
									viewarrowtop.x = viewarrowtop.x + TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									widget.width = widget.width - TempX1;
								}
								else
								{
									TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width - TempX;
									viewarrowright.x = viewarrowright.x - TempX;
									viewarrowtop.x = viewarrowtop.x - TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
									widget.width = widget.width - TempX1;
								}
								( (Widget3D)widget ).onChangeSize( moveX , moveY , 2 , info.cellX , info.cellY );
								Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
								provider.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
								Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
								for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
								{
									for( int j = 0 ; j < info.spanY ; j++ )
									{
										celllayout.mOccupied[info.cellX + info.spanX - i - 1][info.cellY + j] = false;
										celllayout.cellViewList[info.cellX + info.spanX - i - 1][info.cellY + j] = null;
									}
								}
							}
							break;
						case TopMove:
							if( moveY > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
									{
										TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height - TempY;
										viewarrowleft.y = viewarrowleft.y - TempY / 2;
										viewarrowright.y = viewarrowright.y - TempY / 2;
										viewarrowtop.y = viewarrowtop.y - TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										widget.height = widget.height + TempY1;
									}
									else
									{
										TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height + TempY;
										viewarrowleft.y = viewarrowleft.y + TempY / 2;
										viewarrowright.y = viewarrowright.y + TempY / 2;
										viewarrowtop.y = viewarrowtop.y + TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
										widget.height = widget.height + TempY1;
									}
									( (Widget3D)widget ).onChangeSize( moveX , moveY , 3 , info.cellX , info.cellY );
									Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
									provider.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
									Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
									for( int i = 0 ; i < info.spanX ; i++ )
									{
										for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
										{
											celllayout.mOccupied[info.cellX + i][info.cellY + info.spanY + j] = true;
											celllayout.cellViewList[info.cellX + i][info.cellY + info.spanY + j] = widget;
										}
									}
								}
								else
								{
									zoombox.height = zoombox.height - moveY;
									viewarrowleft.y = viewarrowleft.y - moveY / 2;
									viewarrowright.y = viewarrowright.y - moveY / 2;
									viewarrowtop.y = viewarrowtop.y - moveY;
								}
							}
							else
							{
								if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
								{
									TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height + TempY;
									viewarrowleft.y = viewarrowleft.y + TempY / 2;
									viewarrowright.y = viewarrowright.y + TempY / 2;
									viewarrowtop.y = viewarrowtop.y + TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									widget.height = widget.height - TempY1;
								}
								else
								{
									TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height - TempY;
									viewarrowleft.y = viewarrowleft.y - TempY / 2;
									viewarrowright.y = viewarrowright.y - TempY / 2;
									viewarrowtop.y = viewarrowtop.y - TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
									widget.height = widget.height - TempY1;
								}
								( (Widget3D)widget ).onChangeSize( moveX , moveY , 3 , info.cellX , info.cellY );
								Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
								provider.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
								Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
								for( int i = 0 ; i < info.spanX ; i++ )
								{
									for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
									{
										celllayout.mOccupied[info.cellX + i][info.cellY + info.spanY - j - 1] = false;
										celllayout.cellViewList[info.cellX + i][info.cellY + info.spanY - j - 1] = null;
									}
								}
							}
							break;
						case BottomMove:
							if( moveY > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
									{
										TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height - TempY;
										zoombox.y = zoombox.y + TempY;
										viewarrowleft.y = viewarrowleft.y + TempY / 2;
										viewarrowright.y = viewarrowright.y + TempY / 2;
										viewarrowbottom.y = viewarrowbottom.y + TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										widget.height = widget.height + TempY1;
									}
									else
									{
										TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height + TempY;
										zoombox.y = zoombox.y - TempY;
										viewarrowleft.y = viewarrowleft.y - TempY / 2;
										viewarrowright.y = viewarrowright.y - TempY / 2;
										viewarrowbottom.y = viewarrowbottom.y - TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
										widget.height = widget.height + TempY1;
									}
									widget.setPosition( widget.x , widget.y - TempY1 );
									Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
									provider.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
									Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
									for( int i = 0 ; i < info.spanX ; i++ )
									{
										for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
										{
											celllayout.mOccupied[info.cellX + i][info.cellY - j - 1] = true;
											celllayout.cellViewList[info.cellX + i][info.cellY - j - 1] = widget;
										}
									}
									info.cellY = info.cellY - (int)( TempY1 / R3D.Workspace_cell_each_height );
									( (Widget3D)widget ).onChangeSize( moveX , moveY , 4 , info.cellX , info.cellY );
									( (Widget3D)widget ).setItemInfo( info );
								}
								else
								{
									zoombox.height = zoombox.height - moveY;
									zoombox.y = zoombox.y + moveY;
									viewarrowleft.y = viewarrowleft.y + moveY / 2;
									viewarrowright.y = viewarrowright.y + moveY / 2;
									viewarrowbottom.y = viewarrowbottom.y + moveY;
								}
							}
							else
							{
								if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
								{
									TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height + TempY;
									zoombox.y = zoombox.y - TempY;
									viewarrowleft.y = viewarrowleft.y - TempY / 2;
									viewarrowright.y = viewarrowright.y - TempY / 2;
									viewarrowbottom.y = viewarrowbottom.y - TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									widget.height = widget.height - TempY1;
								}
								else
								{
									TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height - TempY;
									zoombox.y = zoombox.y + TempY;
									viewarrowleft.y = viewarrowleft.y + TempY / 2;
									viewarrowright.y = viewarrowright.y + TempY / 2;
									viewarrowbottom.y = viewarrowbottom.y + TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
									widget.height = widget.height - TempY1;
								}
								widget.setPosition( widget.x , widget.y + TempY1 );
								Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
								provider.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
								Widget3DManager.getInstance().updateWidgetInfo( ( (Widget3D)widget ).getPackageName() , provider );
								for( int i = 0 ; i < info.spanX ; i++ )
								{
									for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
									{
										celllayout.mOccupied[info.cellX + i][info.cellY + j] = false;
										celllayout.cellViewList[info.cellX + i][info.cellY + j] = null;
									}
								}
								info.cellY = info.cellY + (int)( TempY1 / R3D.Workspace_cell_each_height );
								( (Widget3D)widget ).onChangeSize( moveX , moveY , 4 , info.cellX , info.cellY );
								( (Widget3D)widget ).setItemInfo( info );
							}
							break;
						default:
							break;
					}
					changeRegion( widget , viewarrowleft , viewarrowright , viewarrowtop , viewarrowbottom );
					Widget3DProvider provider = Widget3DManager.getInstance().getWidget3DProvider( ( (Widget3D)widget ).getPackageName() );
					Root3D.addOrMoveDBs( ( (Widget3D)widget ).getItemInfo() , provider.spanX , provider.spanY );
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					Editor editor = sp.edit();
					editor.putBoolean( info.packageName , true );
					editor.putInt( info.packageName + ":spanX" , ( (Widget3D)widget ).getItemInfo().spanX );
					editor.putInt( info.packageName + ":spanY" , ( (Widget3D)widget ).getItemInfo().spanY );
					editor.commit();
				}
			}
			else if( widget instanceof Widget )
			{
				Widget2DInfo info = ( (Widget)widget ).getItemInfo();
				if( !( info.cellX < 0 || info.cellY < 0 || info.cellX >= mCountX || info.cellY >= mCountY ) )
				{
					switch( what )
					{
						case LeftMove:
							if( moveX > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
									{
										TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width - TempX;
										zoombox.x = zoombox.x + TempX;
										viewarrowleft.x = viewarrowleft.x + TempX;
										viewarrowtop.x = viewarrowtop.x + TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									else
									{
										TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width + TempX;
										zoombox.x = zoombox.x - TempX;
										viewarrowleft.x = viewarrowleft.x - TempX;
										viewarrowtop.x = viewarrowtop.x - TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									widget.setPosition( widget.x - TempX1 , widget.y );
									info.spanX += (int)( TempX1 / R3D.Workspace_cell_each_width );
									for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
									{
										for( int j = 0 ; j < info.spanY ; j++ )
										{
											celllayout.mOccupied[info.cellX - i - 1][info.cellY + j] = true;
											celllayout.cellViewList[info.cellX - i - 1][info.cellY + j] = widget;
										}
									}
									info.x = (int)widget.x;
									info.y = (int)widget.y;
									info.cellX = info.cellX - (int)( TempX1 / R3D.Workspace_cell_each_width );
									( (Widget)widget ).setItemInfo( info );
									SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
								}
								else
								{
									zoombox.width = zoombox.width - moveX;
									zoombox.x = zoombox.x + moveX;
									viewarrowleft.x = viewarrowleft.x + moveX;
									viewarrowtop.x = viewarrowtop.x + moveX / 2;
									viewarrowbottom.x = viewarrowbottom.x + moveX / 2;
								}
							}
							else
							{
								if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
								{
									TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width + TempX;
									zoombox.x = zoombox.x - TempX;
									viewarrowleft.x = viewarrowleft.x - TempX;
									viewarrowtop.x = viewarrowtop.x - TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									widget.width = widget.width - TempX1;
								}
								else
								{
									TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width - TempX;
									zoombox.x = zoombox.x + TempX;
									viewarrowleft.x = viewarrowleft.x + TempX;
									viewarrowtop.x = viewarrowtop.x + TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
									widget.width = widget.width - TempX1;
								}
								widget.setPosition( widget.x + TempX1 , widget.y );
								info.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
								for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
								{
									for( int j = 0 ; j < info.spanY ; j++ )
									{
										celllayout.mOccupied[info.cellX + i][info.cellY + j] = false;
										celllayout.cellViewList[info.cellX + i][info.cellY + j] = null;
									}
								}
								info.x = (int)widget.x;
								info.y = (int)widget.y;
								info.cellX = info.cellX + (int)( TempX1 / R3D.Workspace_cell_each_width );
								( (Widget)widget ).setItemInfo( info );
								SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
							}
							break;
						case RightMove:
							if( moveX > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
									{
										TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width - TempX;
										viewarrowright.x = viewarrowright.x - TempX;
										viewarrowtop.x = viewarrowtop.x - TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									else
									{
										TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
										zoombox.width = zoombox.width + TempX;
										viewarrowright.x = viewarrowright.x + TempX;
										viewarrowtop.x = viewarrowtop.x + TempX / 2;
										viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
										TempX1 = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
										widget.width = widget.width + TempX1;
									}
									for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
									{
										for( int j = 0 ; j < info.spanY ; j++ )
										{
											celllayout.mOccupied[info.cellX + info.spanX + i][info.cellY + j] = true;
											celllayout.cellViewList[info.cellX + info.spanX + i][info.cellY + j] = widget;
										}
									}
									info.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
									( (Widget)widget ).setItemInfo( info );
									SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
								}
								else
								{
									zoombox.width = zoombox.width - moveX;
									viewarrowright.x = viewarrowright.x - moveX;
									viewarrowtop.x = viewarrowtop.x - moveX / 2;
									viewarrowbottom.x = viewarrowbottom.x - moveX / 2;
								}
							}
							else
							{
								if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
								{
									TempX = Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width + TempX;
									viewarrowright.x = viewarrowright.x + TempX;
									viewarrowtop.x = viewarrowtop.x + TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x + TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									widget.width = widget.width - TempX1;
								}
								else
								{
									TempX = R3D.Workspace_cell_each_width - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
									zoombox.width = zoombox.width - TempX;
									viewarrowright.x = viewarrowright.x - TempX;
									viewarrowtop.x = viewarrowtop.x - TempX / 2;
									viewarrowbottom.x = viewarrowbottom.x - TempX / 2;
									TempX1 = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
									widget.width = widget.width - TempX1;
								}
								for( int i = 0 ; i < (int)( TempX1 / R3D.Workspace_cell_each_width ) ; i++ )
								{
									for( int j = 0 ; j < info.spanY ; j++ )
									{
										celllayout.mOccupied[info.cellX + info.spanX - i - 1][info.cellY + j] = false;
										celllayout.cellViewList[info.cellX + info.spanX - i - 1][info.cellY + j] = null;
									}
								}
								info.spanX = ( (int)widget.width - 1 ) / R3D.Workspace_cell_each_width + 1;
								( (Widget)widget ).setItemInfo( info );
								SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
							}
							break;
						case TopMove:
							if( moveY > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
									{
										TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height - TempY;
										viewarrowleft.y = viewarrowleft.y - TempY / 2;
										viewarrowright.y = viewarrowright.y - TempY / 2;
										viewarrowtop.y = viewarrowtop.y - TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										widget.height = widget.height + TempY1;
									}
									else
									{
										TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height + TempY;
										viewarrowleft.y = viewarrowleft.y + TempY / 2;
										viewarrowright.y = viewarrowright.y + TempY / 2;
										viewarrowtop.y = viewarrowtop.y + TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
										widget.height = widget.height + TempY1;
									}
									for( int i = 0 ; i < info.spanX ; i++ )
									{
										for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
										{
											celllayout.mOccupied[info.cellX + i][info.cellY + info.spanY + j] = true;
											celllayout.cellViewList[info.cellX + i][info.cellY + info.spanY + j] = widget;
										}
									}
									info.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
									( (Widget)widget ).setItemInfo( info );
									SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
								}
								else
								{
									zoombox.height = zoombox.height - moveY;
									viewarrowleft.y = viewarrowleft.y - moveY / 2;
									viewarrowright.y = viewarrowright.y - moveY / 2;
									viewarrowtop.y = viewarrowtop.y - moveY;
								}
							}
							else
							{
								if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
								{
									TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height + TempY;
									viewarrowleft.y = viewarrowleft.y + TempY / 2;
									viewarrowright.y = viewarrowright.y + TempY / 2;
									viewarrowtop.y = viewarrowtop.y + TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									widget.height = widget.height - TempY1;
								}
								else
								{
									TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height - TempY;
									viewarrowleft.y = viewarrowleft.y - TempY / 2;
									viewarrowright.y = viewarrowright.y - TempY / 2;
									viewarrowtop.y = viewarrowtop.y - TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
									widget.height = widget.height - TempY1;
								}
								for( int i = 0 ; i < info.spanX ; i++ )
								{
									for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
									{
										celllayout.mOccupied[info.cellX + i][info.cellY + info.spanY - j - 1] = false;
										celllayout.cellViewList[info.cellX + i][info.cellY + info.spanY - j - 1] = null;
									}
								}
								info.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
								( (Widget)widget ).setItemInfo( info );
								SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
							}
							break;
						case BottomMove:
							if( moveY > 0 )
							{
								if( !isHasLocation( moveX , moveY , widget , what ) )
								{
									if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
									{
										TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height - TempY;
										zoombox.y = zoombox.y + TempY;
										viewarrowleft.y = viewarrowleft.y + TempY / 2;
										viewarrowright.y = viewarrowright.y + TempY / 2;
										viewarrowbottom.y = viewarrowbottom.y + TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										widget.height = widget.height + TempY1;
									}
									else
									{
										TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
										zoombox.height = zoombox.height + TempY;
										zoombox.y = zoombox.y - TempY;
										viewarrowleft.y = viewarrowleft.y - TempY / 2;
										viewarrowright.y = viewarrowright.y - TempY / 2;
										viewarrowbottom.y = viewarrowbottom.y - TempY;
										TempY1 = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
										widget.height = widget.height + TempY1;
									}
									widget.setPosition( widget.x , widget.y - TempY1 );
									info.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
									for( int i = 0 ; i < info.spanX ; i++ )
									{
										for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
										{
											celllayout.mOccupied[info.cellX + i][info.cellY - j - 1] = true;
											celllayout.cellViewList[info.cellX + i][info.cellY - j - 1] = widget;
										}
									}
									info.x = (int)widget.x;
									info.y = (int)widget.y;
									info.cellY = info.cellY - (int)( TempY1 / R3D.Workspace_cell_each_height );
									( (Widget)widget ).setItemInfo( info );
									SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
								}
								else
								{
									zoombox.height = zoombox.height - moveY;
									zoombox.y = zoombox.y + moveY;
									viewarrowleft.y = viewarrowleft.y + moveY / 2;
									viewarrowright.y = viewarrowright.y + moveY / 2;
									viewarrowbottom.y = viewarrowbottom.y + moveY;
								}
							}
							else
							{
								if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
								{
									TempY = Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height + TempY;
									zoombox.y = zoombox.y - TempY;
									viewarrowleft.y = viewarrowleft.y - TempY / 2;
									viewarrowright.y = viewarrowright.y - TempY / 2;
									viewarrowbottom.y = viewarrowbottom.y - TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									widget.height = widget.height - TempY1;
								}
								else
								{
									TempY = R3D.Workspace_cell_each_height - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
									zoombox.height = zoombox.height - TempY;
									zoombox.y = zoombox.y + TempY;
									viewarrowleft.y = viewarrowleft.y + TempY / 2;
									viewarrowright.y = viewarrowright.y + TempY / 2;
									viewarrowbottom.y = viewarrowbottom.y + TempY;
									TempY1 = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
									widget.height = widget.height - TempY1;
								}
								widget.setPosition( widget.x , widget.y + TempY1 );
								info.spanY = ( (int)widget.height - 1 ) / R3D.Workspace_cell_each_height + 1;
								for( int i = 0 ; i < info.spanX ; i++ )
								{
									for( int j = 0 ; j < (int)( TempY1 / R3D.Workspace_cell_each_height ) ; j++ )
									{
										celllayout.mOccupied[info.cellX + i][info.cellY + j] = false;
										celllayout.cellViewList[info.cellX + i][info.cellY + j] = null;
									}
								}
								info.x = (int)widget.x;
								info.y = (int)widget.y;
								info.cellY = info.cellY + (int)( TempY1 / R3D.Workspace_cell_each_height );
								( (Widget)widget ).setItemInfo( info );
								SendMsgToAndroid.sendMoveWidgetMsg( ( (Widget)widget ) , ( (Widget)widget ).getItemInfo().screen );
							}
							break;
						default:
							break;
					}
					changeRegion( widget , viewarrowleft , viewarrowright , viewarrowtop , viewarrowbottom );
					Root3D.addOrMoveDBs( ( (Widget)widget ).getItemInfo() , ( (Widget)widget ).getItemInfo().spanX , ( (Widget)widget ).getItemInfo().spanY );
				}
			}
		}
	}
	
	void addtoSuperClass(
			View3D view )
	{
		if( isNewPage )
			return;
		super.addView( view );
	}
	
	void removeFromSuperClass(
			View3D view )
	{
		super.removeView( view );
	}
	
	private int[] findNearestAreaInDirection(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			int[] direction ,
			boolean[][] occupied ,
			boolean blockOccupied[][] ,
			int[] result )
	{
		// Keep track of best-scoring drop area
		final int[] bestXY = result != null ? result : new int[2];
		bestXY[0] = -1;
		bestXY[1] = -1;
		float bestDistance = Float.MAX_VALUE;
		// We use this to march in a single direction
		if( ( direction[0] != 0 && direction[1] != 0 ) || ( direction[0] == 0 && direction[1] == 0 ) )
		{
			return bestXY;
		}
		// This will only incrememnet one of x or y based on the assertion above
		int x = cellX + direction[0];
		int y = cellY + direction[1];
		while( x >= 0 && x + spanX <= mCountX && y >= 0 && y + spanY <= mCountY )
		{
			boolean fail = false;
			for( int i = 0 ; i < spanX ; i++ )
			{
				for( int j = 0 ; j < spanY ; j++ )
				{
					if( occupied[x + i][y + j] && ( blockOccupied == null || blockOccupied[i][j] ) )
					{
						fail = true;
					}
				}
			}
			if( !fail )
			{
				float distance = (float)Math.sqrt( ( x - cellX ) * ( x - cellX ) + ( y - cellY ) * ( y - cellY ) );
				if( Float.compare( distance , bestDistance ) < 0 )
				{
					bestDistance = distance;
					bestXY[0] = x;
					bestXY[1] = y;
				}
			}
			x += direction[0];
			y += direction[1];
		}
		return bestXY;
	}
	
	private boolean addViewToTempLocation(
			View3D v ,
			Rect rectOccupiedByPotentialDrop ,
			int[] direction ,
			ItemConfiguration currentState )
	{
		CellAndSpan c = currentState.map.get( v );
		boolean success = false;
		markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , false );
		markCellsForRect( rectOccupiedByPotentialDrop , mTmpOccupied , true );
		findNearestArea( c.x , c.y , c.spanX , c.spanY , direction , mTmpOccupied , null , mTempLocation );
		if( mTempLocation[0] >= 0 && mTempLocation[1] >= 0 )
		{
			c.x = mTempLocation[0];
			c.y = mTempLocation[1];
			success = true;
		}
		markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , true );
		return success;
	}
	
	private boolean addViewsToTempLocation(
			ArrayList<View3D> views ,
			Rect rectOccupiedByPotentialDrop ,
			int[] direction ,
			boolean push ,
			View3D dragView ,
			ItemConfiguration currentState )
	{
		if( views.size() == 0 )
			return true;
		boolean success = false;
		Rect boundingRect = null;
		// We construct a rect which represents the entire group of views passed
		// in
		for( View3D v : views )
		{
			CellAndSpan c = currentState.map.get( v );
			if( boundingRect == null )
			{
				boundingRect = new Rect( c.x , c.y , c.x + c.spanX , c.y + c.spanY );
			}
			else
			{
				boundingRect.union( c.x , c.y , c.x + c.spanX , c.y + c.spanY );
			}
		}
		// Mark the occupied state as false for the group of views we want to
		// move.
		for( View3D v : views )
		{
			CellAndSpan c = currentState.map.get( v );
			markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , false );
		}
		boolean[][] blockOccupied = new boolean[boundingRect.width()][boundingRect.height()];
		int top = boundingRect.top;
		int left = boundingRect.left;
		// We mark more precisely which parts of the bounding rect are truly
		// occupied, allowing
		// for tetris-style interlocking.
		for( View3D v : views )
		{
			CellAndSpan c = currentState.map.get( v );
			markCellsForView( c.x - left , c.y - top , c.spanX , c.spanY , blockOccupied , true );
		}
		markCellsForRect( rectOccupiedByPotentialDrop , mTmpOccupied , true );
		if( push )
		{
			findNearestAreaInDirection( boundingRect.left , boundingRect.top , boundingRect.width() , boundingRect.height() , direction , mTmpOccupied , blockOccupied , mTempLocation );
		}
		else
		{
			findNearestArea( boundingRect.left , boundingRect.top , boundingRect.width() , boundingRect.height() , direction , mTmpOccupied , blockOccupied , mTempLocation );
		}
		// If we successfuly found a location by pushing the block of views, we
		// commit it
		if( mTempLocation[0] >= 0 && mTempLocation[1] >= 0 )
		{
			int deltaX = mTempLocation[0] - boundingRect.left;
			int deltaY = mTempLocation[1] - boundingRect.top;
			for( View3D v : views )
			{
				CellAndSpan c = currentState.map.get( v );
				c.x += deltaX;
				c.y += deltaY;
			}
			success = true;
		}
		// In either case, we set the occupied array as marked for the location
		// of the views
		for( View3D v : views )
		{
			CellAndSpan c = currentState.map.get( v );
			markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , true );
		}
		return success;
	}
	
	private class ItemConfiguration
	{
		
		HashMap<View3D , CellAndSpan> map = new HashMap<View3D , CellAndSpan>();
		private HashMap<View3D , CellAndSpan> savedMap = new HashMap<View3D , CellAndSpan>();
		ArrayList<View3D> sortedViews = new ArrayList<View3D>();
		boolean isSolution = false;
		int dragViewX , dragViewY , dragViewSpanX , dragViewSpanY;
		
		void save()
		{
			// Copy current state into savedMap
			for( View3D v : map.keySet() )
			{
				map.get( v ).copy( savedMap.get( v ) );
			}
		}
		
		void restore()
		{
			// Restore current state from savedMap
			for( View3D v : savedMap.keySet() )
			{
				savedMap.get( v ).copy( map.get( v ) );
			}
		}
		
		void add(
				View3D v ,
				CellAndSpan cs )
		{
			map.put( v , cs );
			savedMap.put( v , new CellAndSpan() );
			sortedViews.add( v );
		}
		
		int area()
		{
			return dragViewSpanX * dragViewSpanY;
		}
	}
	
	private class CellAndSpan
	{
		
		int x , y;
		int spanX , spanY;
		
		public CellAndSpan()
		{
		}
		
		public void copy(
				CellAndSpan copy )
		{
			copy.x = x;
			copy.y = y;
			copy.spanX = spanX;
			copy.spanY = spanY;
		}
		
		public CellAndSpan(
				int x ,
				int y ,
				int spanX ,
				int spanY )
		{
			this.x = x;
			this.y = y;
			this.spanX = spanX;
			this.spanY = spanY;
		}
		
		public String toString()
		{
			return "(" + x + ", " + y + ": " + spanX + ", " + spanY + ")";
		}
	}
	
	private void markCellsForRect(
			Rect r ,
			boolean[][] occupied ,
			boolean value )
	{
		markCellsForView( r.left , r.top , r.width() , r.height() , occupied , value );
	}
	
	/**
	 * Computes a bounding rectangle for a range of cells
	 * 
	 * @param cellX
	 *            X coordinate of upper left corner expressed as a cell position
	 * @param cellY
	 *            Y coordinate of upper left corner expressed as a cell position
	 * @param cellHSpan
	 *            Width in cells
	 * @param cellVSpan
	 *            Height in cells
	 * @param resultRect
	 *            Rect into which to put the results
	 */
	public void cellToRect(
			int cellX ,
			int cellY ,
			int cellHSpan ,
			int cellVSpan ,
			RectF resultRect )
	{
		final int cellWidth = mCellWidth;
		final int cellHeight = mCellHeight;
		final int widthGap = mWidthGap;
		final int heightGap = mHeightGap;
		final int hStartPadding = getPaddingLeft();
		final int vStartPadding = getPaddingBottom();
		int width = cellHSpan * cellWidth + ( ( cellHSpan - 1 ) * widthGap );
		int height = cellVSpan * cellHeight + ( ( cellVSpan - 1 ) * heightGap );
		int x = hStartPadding + cellX * ( cellWidth + widthGap );
		int y = vStartPadding + cellY * ( cellHeight + heightGap );
		resultRect.set( x , y , x + width , y + height );
	}
	
	/**
	 * Computes the required horizontal and vertical cell spans to always fit
	 * the given rectangle.
	 * 
	 * @param width
	 *            Width in pixels
	 * @param height
	 *            Height in pixels
	 * @param result
	 *            An array of length 2 in which to store the result (may be
	 *            null).
	 */
	public static int[] rectToCell(
			int width ,
			int height ,
			int[] result )
	{
		return rectToCell( iLoongLauncher.getInstance().getResources() , width , height , result );
	}
	
	public static int[] rectToCell(
			Resources resources ,
			int width ,
			int height ,
			int[] result )
	{
		int smallerSize = Math.min( R3D.Workspace_cell_each_width_ori , R3D.Workspace_cell_each_height_ori );
		// Always round up to next largest cell
		int spanX = (int)Math.ceil( width / (float)smallerSize );
		int spanY = (int)Math.ceil( height / (float)smallerSize );
		// int spanX = (width + smallerSize) / smallerSize;
		// int spanY = (height + smallerSize) / smallerSize;
		if( result == null )
		{
			return new int[]{ spanX , spanY };
		}
		result[0] = spanX;
		result[1] = spanY;
		return result;
		// // Always assume we're working with the smallest span to make sure we
		// // reserve enough space in both orientations.
		// int actualWidth = iLoongLauncher.WORKSPACE_CELL_WIDTH;
		// int actualHeight = iLoongLauncher.WORKSPACE_CELL_HEIGHT;
		// int smallerSize = Math.min(actualWidth, actualHeight);
		//
		// // Always round up to next largest cell
		// int spanX = (int) Math.ceil(width / (float) smallerSize);
		// int spanY = (int) Math.ceil(height / (float) smallerSize);
		//
		// if (result == null) {
		// return new int[] { spanX, spanY };
		// }
		// result[0] = spanX;
		// result[1] = spanY;
		// return result;
	}
	
	public int[] cellSpansToSize(
			int hSpans ,
			int vSpans )
	{
		int[] size = new int[2];
		size[0] = hSpans * mCellWidth + ( hSpans - 1 ) * mWidthGap;
		size[1] = vSpans * mCellHeight + ( vSpans - 1 ) * mHeightGap;
		return size;
	}
	
	/**
	 * Calculate the grid spans needed to fit given item
	 */
	public void calculateSpans(
			View3D view )
	{
		// final int minWidth;
		// final int minHeight;
		//
		// if (view instanceof Widget3D) {
		// minWidth = (int) view.width;
		// minHeight = (int) view.height;
		// } else if (view instanceof PendingAddWidgetInfo) {
		// minWidth = ((PendingAddWidgetInfo) info).minWidth;
		// minHeight = ((PendingAddWidgetInfo) info).minHeight;
		// } else {
		// // It's not a widget, so it must be 1x1
		// view.spanX = view.spanY = 1;
		// return;
		// }
		// int[] spans = rectToCell(minWidth, minHeight, null);
		// view.spanX = spans[0];
		// view.spanY = spans[1];
	}
	
	/**
	 * Find the first vacant cell, if there is one.
	 * 
	 * @param vacant
	 *            Holds the x and y coordinate of the vacant cell
	 * @param spanX
	 *            Horizontal cell span.
	 * @param spanY
	 *            Vertical cell span.
	 * 
	 * @return True if a vacant cell was found
	 */
	public boolean getVacantCell(
			int[] vacant ,
			int spanX ,
			int spanY )
	{
		return findVacantCell( vacant , spanX , spanY , mCountX , mCountY , mOccupied );
	}
	
	static boolean findVacantCell(
			int[] vacant ,
			int spanX ,
			int spanY ,
			int xCount ,
			int yCount ,
			boolean[][] occupied )
	{
		for( int y = 0 ; y < yCount ; y++ )
		{
			for( int x = 0 ; x < xCount ; x++ )
			{
				boolean available = !occupied[x][y];
				out:
				for( int i = x ; i < x + spanX - 1 && x < xCount ; i++ )
				{
					for( int j = y ; j < y + spanY - 1 && y < yCount ; j++ )
					{
						available = available && !occupied[i][j];
						if( !available )
							break out;
					}
				}
				if( available )
				{
					vacant[0] = x;
					vacant[1] = y;
					return true;
				}
			}
		}
		return false;
	}
	
	private void clearOccupiedCells()
	{
		for( int x = 0 ; x < mCountX ; x++ )
		{
			for( int y = 0 ; y < mCountY ; y++ )
			{
				mOccupied[x][y] = false;
				cellViewList[x][y] = null;
			}
		}
	}
	
	public static final int LEFT = 0;
	public static final int TOP = 1;
	public static final int RIGHT = 2;
	public static final int BOTTOM = 3;
	
	/**
	 * Given a view, determines how much that view can be expanded in all
	 * directions, in terms of whether or not there are other items occupying
	 * adjacent cells. Used by the AppWidgetResizeFrame to determine how the
	 * widget can be resized.
	 */
	public void getExpandabilityArrayForView(
			View view ,
			int[] expandability )
	{
		final LayoutParams lp = (LayoutParams)view.getLayoutParams();
		boolean flag;
		expandability[LEFT] = 0;
		for( int x = lp.cellX - 1 ; x >= 0 ; x-- )
		{
			flag = false;
			for( int y = lp.cellY ; y < lp.cellY + lp.cellVSpan ; y++ )
			{
				if( mOccupied[x][y] )
					flag = true;
			}
			if( flag )
				break;
			expandability[LEFT]++;
		}
		expandability[TOP] = 0;
		for( int y = lp.cellY - 1 ; y >= 0 ; y-- )
		{
			flag = false;
			for( int x = lp.cellX ; x < lp.cellX + lp.cellHSpan ; x++ )
			{
				if( mOccupied[x][y] )
					flag = true;
			}
			if( flag )
				break;
			expandability[TOP]++;
		}
		expandability[RIGHT] = 0;
		for( int x = lp.cellX + lp.cellHSpan ; x < mCountX ; x++ )
		{
			flag = false;
			for( int y = lp.cellY ; y < lp.cellY + lp.cellVSpan ; y++ )
			{
				if( mOccupied[x][y] )
					flag = true;
			}
			if( flag )
				break;
			expandability[RIGHT]++;
		}
		expandability[BOTTOM] = 0;
		for( int y = lp.cellY + lp.cellVSpan ; y < mCountY ; y++ )
		{
			flag = false;
			for( int x = lp.cellX ; x < lp.cellX + lp.cellHSpan ; x++ )
			{
				if( mOccupied[x][y] )
					flag = true;
			}
			if( flag )
				break;
			expandability[BOTTOM]++;
		}
	}
	
	public void onMove(
			View3D view ,
			int newCellX ,
			int newCellY ,
			int newSpanX ,
			int newSpanY )
	{
		markCellsAsUnoccupiedForView( view );
		markCellsForView( newCellX , newCellY , newSpanX , newSpanY , mOccupied , true );
	}
	
	public void markCellsAsUnoccupiedForView(
			View3D view )
	{
		markCellsAsUnoccupiedForView( view , mOccupied );
	}
	
	public void markCellsAsUnoccupiedForView(
			View3D view ,
			boolean occupied[][] )
	{
		if( view == null )
			return;
		// LayoutParams lp = (LayoutParams) view.getLayoutParams();
		markCellsForView( (int)view.x , (int)view.y , (int)view.width , (int)view.height , occupied , false );
	}
	
	public void markCellsAsOccupiedForView(
			View3D view )
	{
		markCellsAsOccupiedForView( view , mOccupied );
	}
	
	public void markCellsAsOccupiedForView(
			View3D view ,
			boolean[][] occupied )
	{
		if( view == null )
			return;
		// LayoutParams lp = (LayoutParams) view.getLayoutParams();
		markCellsForView( (int)view.x , (int)view.y , (int)view.width , (int)view.height , occupied , true );
	}
	
	private void markCellsForView(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			boolean[][] occupied ,
			boolean value )
	{
		if( cellX < 0 || cellY < 0 )
			return;
		for( int x = cellX ; x < cellX + spanX && x < mCountX ; x++ )
		{
			for( int y = cellY ; y < cellY + spanY && y < mCountY ; y++ )
			{
				occupied[x][y] = value;
			}
		}
	}
	
	// need repair the follow two method mPaddingLeft + mPaddingRight
	public int getDesiredWidth()
	{
		return 0 + ( mCountX * mCellWidth ) + ( Math.max( ( mCountX - 1 ) , 0 ) * mWidthGap );
	}
	
	public int getDesiredHeight()
	{
		return 0 + ( mCountY * mCellHeight ) + ( Math.max( ( mCountY - 1 ) , 0 ) * mHeightGap );
	}
	
	public boolean isOccupied(
			int x ,
			int y )
	{
		if( x < mCountX && y < mCountY )
		{
			return mOccupied[x][y];
		}
		else
		{
			throw new RuntimeException( "Position exceeds the bound of this CellLayout" );
		}
	}
	
	protected boolean checkLayoutParams(
			ViewGroup.LayoutParams p )
	{
		return p instanceof CellLayout3D.LayoutParams;
	}
	
	protected ViewGroup.LayoutParams generateLayoutParams(
			ViewGroup.LayoutParams p )
	{
		return new CellLayout3D.LayoutParams( p );
	}
	
	public static class CellLayoutAnimationController extends LayoutAnimationController
	{
		
		public CellLayoutAnimationController(
				Animation animation ,
				float delay )
		{
			super( animation , delay );
		}
		
		@Override
		protected long getDelayForView(
				View view )
		{
			return (int)( Math.random() * 150 );
		}
	}
	
	public static class LayoutParams extends ViewGroup.MarginLayoutParams
	{
		
		/**
		 * Horizontal location of the item in the grid.
		 */
		@ViewDebug.ExportedProperty
		public int cellX;
		/**
		 * Vertical location of the item in the grid.
		 */
		@ViewDebug.ExportedProperty
		public int cellY;
		/**
		 * Temporary horizontal location of the item in the grid during reorder
		 */
		public int tmpCellX;
		/**
		 * Temporary vertical location of the item in the grid during reorder
		 */
		public int tmpCellY;
		/**
		 * Indicates that the temporary coordinates should be used to layout the
		 * items
		 */
		public boolean useTmpCoords;
		/**
		 * Number of cells spanned horizontally by the item.
		 */
		@ViewDebug.ExportedProperty
		public int cellHSpan;
		/**
		 * Number of cells spanned vertically by the item.
		 */
		@ViewDebug.ExportedProperty
		public int cellVSpan;
		/**
		 * Indicates whether the item will set its x, y, width and height
		 * parameters freely, or whether these will be computed based on cellX,
		 * cellY, cellHSpan and cellVSpan.
		 */
		public boolean isLockedToGrid = true;
		/**
		 * Indicates whether this item can be reordered. Always true except in
		 * the case of the the AllApps button.
		 */
		public boolean canReorder = true;
		// X coordinate of the view in the layout.
		@ViewDebug.ExportedProperty
		int x;
		// Y coordinate of the view in the layout.
		@ViewDebug.ExportedProperty
		int y;
		boolean dropped;
		
		public LayoutParams(
				Context c ,
				AttributeSet attrs )
		{
			super( c , attrs );
			cellHSpan = 1;
			cellVSpan = 1;
		}
		
		public LayoutParams(
				ViewGroup.LayoutParams source )
		{
			super( source );
			cellHSpan = 1;
			cellVSpan = 1;
		}
		
		public LayoutParams(
				LayoutParams source )
		{
			super( source );
			this.cellX = source.cellX;
			this.cellY = source.cellY;
			this.cellHSpan = source.cellHSpan;
			this.cellVSpan = source.cellVSpan;
		}
		
		public LayoutParams(
				int cellX ,
				int cellY ,
				int cellHSpan ,
				int cellVSpan )
		{
			super( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
			this.cellX = cellX;
			this.cellY = cellY;
			this.cellHSpan = cellHSpan;
			this.cellVSpan = cellVSpan;
		}
		
		public void setup(
				int cellWidth ,
				int cellHeight ,
				int widthGap ,
				int heightGap )
		{
			if( isLockedToGrid )
			{
				final int myCellHSpan = cellHSpan;
				final int myCellVSpan = cellVSpan;
				final int myCellX = useTmpCoords ? tmpCellX : cellX;
				final int myCellY = useTmpCoords ? tmpCellY : cellY;
				width = myCellHSpan * cellWidth + ( ( myCellHSpan - 1 ) * widthGap ) - leftMargin - rightMargin;
				height = myCellVSpan * cellHeight + ( ( myCellVSpan - 1 ) * heightGap ) - topMargin - bottomMargin;
				x = (int)( myCellX * ( cellWidth + widthGap ) + leftMargin );
				y = (int)( myCellY * ( cellHeight + heightGap ) + topMargin );
			}
		}
		
		public String toString()
		{
			return "(" + this.cellX + ", " + this.cellY + ")";
		}
		
		public void setWidth(
				int width )
		{
			this.width = width;
		}
		
		public int getWidth()
		{
			return width;
		}
		
		public void setHeight(
				int height )
		{
			this.height = height;
		}
		
		public int getHeight()
		{
			return height;
		}
		
		public void setX(
				int x )
		{
			this.x = x;
		}
		
		public int getX()
		{
			return x;
		}
		
		public void setY(
				int y )
		{
			this.y = y;
		}
		
		public int getY()
		{
			return y;
		}
	}
	
	// This class stores info for two purposes:
	// 1. When dragging items (mDragInfo in Workspace), we store the View, its
	// cellX & cellY,
	// its spanX, spanY, and the screen it is on
	// 2. When long clicking on an empty cell in a CellLayout, we save
	// information about the
	// cellX and cellY coordinates and which page was clicked. We then set this
	// as a tag on
	// the CellLayout that was long clicked
	static final class CellInfo
	{
		
		View cell;
		int cellX = -1;
		int cellY = -1;
		int spanX;
		int spanY;
		int screen;
		long container;
		
		@Override
		public String toString()
		{
			return "Cell[view=" + ( cell == null ? "null" : cell.getClass() ) + ", x=" + cellX + ", y=" + cellY + "]";
		}
	}
	
	public boolean lastDownOnOccupiedCell()
	{
		return mLastDownOnOccupiedCell;
	}
	
	public void resetCurrFocus()
	{
		this.resetInfo();
		this.setInvisible();
		setHide();
	}
	
	public void disableCurPageFocus()
	{
		Log.v( "touch" , "switchOff" );
		touchEvent = true;
		setHide();
		setInvisible();
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		Log.v( "background" , "SetMenuDesktop.origin11" + SetMenuDesktop.origin );
		if( DefaultLayout.keypad_event_of_focus )
		{
			Log.v( "track" , "keyDown" );
			if( SetMenuDesktop.origin )
			{
				return false;
			}
			if( keycode == KeyEvent.KEYCODE_MENU || keycode == KeyEvent.KEYCODE_HOME )
			{
				this.resetInfo();
				this.setInvisible();
				this.setHide();
				return true;
			}
			if( iconExist == true )
			{
				if( keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP || keycode == KeyEvent.KEYCODE_DPAD_LEFT || keycode == KeyEvent.KEYCODE_DPAD_RIGHT )
				{
					touchEvent = false;
					if( hideFocus == true )
					{
						setUnhide();
						setVisible();
						return true;
					}
				}
				if( keycode == KeyEvent.KEYCODE_DPAD_CENTER )
				{
					onKeySelect();
					return true;
				}
			}
			updateFocus( keycode );
		}
		return super.keyDown( keycode );
	}
	
	public boolean findItem(
			int row )
	{
		hasItem = false;
		for( int i = row - 1 ; i >= 0 ; i-- )
		{
			for( int j = 0 ; j < mCountX ; j++ )
			{
				if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
				{
					hasItem = true;
					break;
				}
			}
			if( hasItem )
				break;
		}
		return hasItem;
	}
	
	public void onKeySelect()
	{
		if( ( hideFocus == false ) && ( firstlyCome == false ) )
		{
			cellViewList[cursorX][cursorY].onClick( iconWidth / 2 , iconHeight / 2 );
		}
	}
	
	public void processKeyDown(
			int cursor )
	{
		boolean preFind = false;
		boolean nextFind = false;
		View3D view = null;
		int leftTemp = 0;
		int rightTemp = 0;
		int preDistance = 0;
		int nextDistance = 1;
		int preTemp = 0;
		int nextTemp = 0;
		// 往上移一
		for( int i = cursor - 1 ; i >= 0 ; i-- )
		{
			preDistance = 0;
			nextDistance = 1;
			preTemp = 0;
			nextTemp = 0;
			preFind = false;
			nextFind = false;
			// 判断该行前面第一个有效图标的位置
			for( int j = cursorX ; j >= 0 ; j-- )
			{
				//
				if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
				{
					preFind = true;
					preTemp = j;// 获得列的位置
					leftTemp = i;
					break;
				}
				else
				{
					preDistance++;
				}
			}
			// 判断是否已经找到最合适的位置
			// if(!(find==true)&&(preDistance==0)){
			// 判断该行后面第一个有效图标位�?
			for( int j = cursorX + 1 ; j < mCountX ; j++ )
			{
				// if(cursorX==mCountY-1) break;//on last column
				if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
				{
					nextTemp = j;// 获得列的位置
					nextFind = true;
					rightTemp = i;
					break;
				}
				else
				{
					nextDistance++;
				}
			}
			// }
			// 找到具体的位置了，跳出所有的查找
			if( ( preFind == true ) || ( nextFind == true ) )
				break;
		}
		if( ( nextFind == true ) && ( preFind == true ) )
		{
			// there is a bug here .
			if( preDistance <= nextDistance )
			{
				setLocationInfo( preTemp , leftTemp );
			}
			else
			{
				setLocationInfo( nextTemp , rightTemp );
			}
		}
		else if( preFind == true )
		{
			setLocationInfo( preTemp , leftTemp );
		}
		else if( nextFind == true )
		{
			setLocationInfo( nextTemp , rightTemp );
		}
		else
		{
			Log.v( "focus" , "jump to hotseatBar" );
		}
	}
	
	public void processKeyUp(
			int cursor )
	{
		View3D view = null;
		int leftTemp = 0;
		int rightTemp = 0;
		int preDistance = 0;
		int nextDistance = 1;
		int preTemp = 0;
		int nextTemp = 0;
		boolean preFind = false;
		boolean nextFind = false;
		// 往上移一�?
		for( int i = cursor + 1 ; i < mCountY ; i++ )
		{
			preDistance = 0;
			nextDistance = 1;
			preTemp = 0;
			nextTemp = 0;
			preFind = false;
			nextFind = false;
			// 判断该行前面第一个有效图标的位置
			for( int j = cursorX ; j >= 0 ; j-- )
			{
				//
				if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
				{
					preFind = true;
					preTemp = j;// 获得列的位置
					leftTemp = i;
					break;
				}
				else
				{
					preDistance++;
				}
			}
			// 判断是否已经找到最合适的位置
			// if(!(find==true)&&(preDistance==0)){
			// 判断该行后面第一个有效图标位�?
			for( int j = cursorX + 1 ; j < mCountX ; j++ )
			{
				// if(cursorX==mCountY-1) break;//on last column
				if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
				{
					nextTemp = j;// 获得列的位置
					nextFind = true;
					rightTemp = i;
					break;
				}
				else
				{
					nextDistance++;
				}
			}
			// }
			// 找到具体的位置了，跳出所有的查找
			if( ( preFind == true ) || ( nextFind == true ) )
				break;
		}
		if( ( nextFind == true ) && ( preFind == true ) )
		{
			if( preDistance <= nextDistance )
			{
				setLocationInfo( preTemp , leftTemp );
			}
			else
			{
				setLocationInfo( nextTemp , rightTemp );
			}
		}
		else if( preFind == true )
		{
			setLocationInfo( preTemp , leftTemp );
		}
		else if( nextFind == true )
		{
			setLocationInfo( nextTemp , rightTemp );
		}
	}
	
	public void processKeyLeft(
			int cursor )
	{
		boolean find = false;
		for( int i = cursor - 1 ; i >= 0 ; i-- )
		{
			if( ( cellViewList[i][cursorY] instanceof Icon3D ) || ( ( cellViewList[i][cursorY] instanceof FolderIcon3D ) ) )
			{
				setLocationInfo( i , cursorY );
				find = true;
				break;
			}
		}
		if( find == false )
		{
			onScroll( LEFT_DIR );
		}
	}
	
	public void processKeyRight(
			int cursor )
	{
		boolean find = false;
		for( int i = cursor + 1 ; i < mCountX ; i++ )
		{
			if( ( cellViewList[i][cursorY] instanceof Icon3D ) || ( ( cellViewList[i][cursorY] instanceof FolderIcon3D ) ) )
			{
				setLocationInfo( i , cursorY );
				find = true;
				break;
			}
		}
		if( find == false )
		{
			onScroll( RIGHT_DIR );
		}
	}
	
	public void setLocationInfo(
			int column ,
			int row )
	{
		Log.v( "focus" , "setLocationInfo" );
		cursorX = column;
		cursorY = row;
		locationX = cellViewList[column][row].x;
		locationY = cellViewList[column][row].y;
		iconHeight = cellViewList[column][row].height;
		iconWidth = cellViewList[column][row].width;
		jumpFrom();
		findItem( row );
	}
	
	public int getNextPageSize()
	{
		// viewParent.getIndexInParent()
		return ( (Workspace3D)viewParent ).getNextCellLayoutCount();
	}
	
	public int getPreviousPageSize()
	{
		return ( (Workspace3D)viewParent ).getPreviousCellLayoutCount();
	}
	
	/**
	 * 
	 * @param direction
	 *            : to judge which direction(X or Y) is being operated on .
	 * 
	 * **/
	private void getValidIcon(
			int direction ,
			int cursor )
	{
		// 我靠！！！！！！第一维表示的是列
		switch( direction )
		{
			case 1:
				processKeyUp( cursor );
				break;
			case 2:
				processKeyDown( cursor );
				break;
			case 3:
				processKeyLeft( cursor );
				break;
			case 4:
				processKeyRight( cursor );
				break;
		}
	}
	
	public void setVisible()
	{
		if( touchEvent == false )
		{
			Log.v( "track" , "setVisible" );
			firstlyCome = false;
		}
	}
	
	public void setHide()
	{
		hideFocus = true;
	}
	
	public void setUnhide()
	{
		hideFocus = false;
	}
	
	public void setInvisible()
	{
		firstlyCome = true;
	}
	
	public void hideFocus()
	{
		changeFocus();
		setInvisible();
	}
	
	public void switchOff()
	{
		Log.v( "touch" , "switchOff" );
		touchEvent = true;
		setHide();
		setInvisible();
		// HotMainMenuView3D.onFocus=false;
		// HotSeat3D.setInvisible();
	}
	
	public void switchOn()
	{
		setUnhide();
		setVisible();
	}
	
	public void onScroll(
			String page )
	{
		keyPadInvoked = true;
		onDropLeave();/* sometimes it is necessary */
		direction = page;
		if( page.equals( RIGHT_DIR ) )
		{
			// if(getNextPageSize()<=0)
			if( !( (Workspace3D)viewParent ).hasNextPage() )
				page = "first";
		}
		else if( page.equals( LEFT_DIR ) )
		{
			// if(getPreviousPageSize()<=0)
			if( !( (Workspace3D)viewParent ).hasPreviousPage() )
				page = "last";
		}
		setInvisible();
		this.setTag( page );
		viewParent.onCtrlEvent( this , MSG_PAGE_TO );
	}
	
	public void refreshLocation(
			int index )
	{
		Log.v( "focus" , "fresh index" + index );
		View3D view = null;
		int leftTemp = 0;
		int rightTemp = 0;
		int preDistance = 0;
		int nextDistance = 1;
		int preTemp = 0;
		int nextTemp = 0;
		boolean preFind = false;
		boolean nextFind = false;
		int realX = 0;
		int realY = 0;
		iconExist = false;
		if( index == R3D.hot_dock_icon_number )
		{
			for( int i = 0 ; i < mCountY ; i++ )
			{
				for( int j = mCountX - 1 ; j >= 0 ; j-- )
				{
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						realX = i;
						realY = j;
						preFind = true;
						iconExist = true;
						break;
					}
				}
				if( preFind )
				{
					break;
				}
			}
			// return;
		}
		else
		{
			for( int i = 0 ; i < mCountY ; i++ )
			{
				for( int j = index ; j >= 0 ; j-- )
				{
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						preFind = true;
						preTemp = j;
						leftTemp = i;
						break;
					}
				}
				for( int j = index + 1 ; j < mCountX ; j++ )
				{
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						nextFind = true;
						nextTemp = j;
						rightTemp = i;
						break;
					}
				}
				if( ( preFind ) || ( nextFind ) )
				{
					iconExist = true;
					if( preFind && nextFind )
					{
						if( leftTemp == rightTemp )
						{
							if( preTemp <= nextTemp )
							{
								realX = leftTemp;
								realY = preTemp;
							}
							else
							{
								realX = rightTemp;
								realY = nextTemp;
							}
						}
					}
					else if( preFind )
					{
						realX = leftTemp;
						realY = preTemp;
					}
					else
					{
						realX = rightTemp;
						realY = nextTemp;
					}
					break;
				}
				Log.v( "focus" , "times" );
			}
		}
		if( preFind || nextFind )
			setLocationInfo( realY , realX );
	}
	
	public void changeFocus()
	{
		View3D view = null;
		int leftTemp = 0;
		int rightTemp = 0;
		int preDistance = 0;
		int nextDistance = 1;
		int preTemp = 0;
		int nextTemp = 0;
		boolean preFind = false;
		boolean nextFind = false;
		iconExist = false;
		if( RIGHT_DIR.equals( direction ) )
		{
			for( int i = cursorY ; i < mCountY ; i++ )
			{
				// for(int j=mCountX-1;j>=0;j--){
				for( int j = 0 ; j < mCountX ; j++ )
				{
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						preFind = true;
						preTemp = j;
						leftTemp = i;
						iconExist = true;
						break;
					}
				}
				if( preFind == true )
					break;
			}
			for( int i = cursorY - 1 ; i >= 0 ; i-- )
			{
				// for(int j=mCountX-1;j>=0;j--){
				for( int j = 0 ; j < mCountX ; j++ )
				{
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						nextTemp = j;
						nextFind = true;
						rightTemp = i;
						iconExist = true;
						break;
					}
				}
				if( nextFind == true )
					break;
			}
		}
		else if( LEFT_DIR.equals( direction ) )
		{
			for( int i = cursorY ; i < mCountY ; i++ )
			{
				for( int j = mCountX - 1 ; j >= 0 ; j-- )
				{
					// for(int j=0;j<mCountX;j++){
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						preFind = true;
						preTemp = j;
						leftTemp = i;
						iconExist = true;
						break;
					}
				}
				if( preFind == true )
					break;
			}
			for( int i = cursorY - 1 ; i >= 0 ; i-- )
			{
				for( int j = mCountX - 1 ; j >= 0 ; j-- )
				{
					// for(int j=0;j<mCountX;j++){
					if( ( cellViewList[j][i] instanceof Icon3D ) || ( ( cellViewList[j][i] instanceof FolderIcon3D ) ) )
					{
						nextTemp = j;
						nextFind = true;
						rightTemp = i;
						iconExist = true;
						break;
					}
				}
				if( nextFind == true )
					break;
			}
		}
		// if((preFind==true) ||(nextFind==true))break;
		if( preFind == true )
		{
			setLocationInfo( preTemp , leftTemp );
		}
		else if( nextFind == true )
		{
			setLocationInfo( nextTemp , rightTemp );
		}
	}
	
	public void resetInfo()
	{
		iconExist = false;
		for( int i = mCountY - 1 ; i >= 0 ; i-- )
		{
			for( int j = 0 ; j < mCountX ; j++ )
			{
				if( ( cellViewList[j][i] instanceof Icon3D ) || ( cellViewList[j][i] instanceof FolderIcon3D ) )
				{
					cursorX = j;
					cursorY = i;
					locationX = cellViewList[cursorX][cursorY].x;
					locationY = cellViewList[cursorX][cursorY].y;
					iconHeight = cellViewList[cursorX][cursorY].height;
					iconWidth = cellViewList[cursorX][cursorY].width;
					iconExist = true;
					jumpFrom();
					findItem( cursorY );
					// setVisible();
					return;
				}
				else
				{
					iconExist = false;
				}
			}
		}
	}
	
	public void jumpFrom()
	{
		currentX = cellViewList[cursorX][cursorY].x;
		currentY = cellViewList[cursorX][cursorY].y;
		if( cursorX == mCountX - 1 )
		{
			CellLayout3D.origin = 2;
		}
		else
		{
			CellLayout3D.origin = 1;
		}
		Log.v( "focus" , "x :" + currentX + "y:" + currentY );
	}
	
	/**
	 * 
	 * @param 1:竖直向上
	 * @param 2:竖直向下
	 * @param 3:水平向左
	 * @param 4:水平向右
	 * 
	 * **/
	protected void updateFocus(
			int direction )
	{
		switch( direction )
		{
			case KeyEvent.KEYCODE_DPAD_UP:
				if( cursorY >= mCountY - 1 )
				{
					Log.v( "focus" , "updateFocus" );
				}
				else
				{
					getValidIcon( 1 , cursorY );
				}
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				if( ( ( cursorX + 1 ) % mCountX ) == 0 )
				{
					onScroll( RIGHT_DIR );
				}
				else
				{
					getValidIcon( 4 , cursorX );
				}
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if( findItem( cursorY ) )
				{
					getValidIcon( 2 , cursorY );
				}
				else
				{
					iconExist = false;
				}
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				if( ( ( cursorX + 1 ) % mCountX ) == 1 )
				{
					onScroll( "left" );
				}
				else
				{
					getValidIcon( 3 , cursorX );
				}
				break;
			default:
				break;
		}
	}
	
	/** zqh end **/
	// zhujieping add start
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof FolderIcon3D )
		{
			switch( event_id )
			{
				case FolderIcon3D.MSG_BRING_TO_FRONT:
					if( sender != null && sender instanceof FolderIcon3D )
					{
						bringToFront( sender );
					}
			}
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	private void bringToFront(
			View3D view )
	{
		int toIndex = this.getChildCount() - 1;
		if( view != null )
		{
			removeFromSuperClass( view );
			super.addViewAt( toIndex , view );
		}
		// removeFromSuperClass(view);
	}
	
	// zhujieping add end
	private Tween FolderLargeTween = null;
	
	//	private Tween FolderSmallTween = null;
	private void FolderLargeAnim(
			View3D folder )
	{
		if( FolderLargeTween != null )
		{
			FolderLargeTween.free();
			FolderLargeTween = null;
		}
		if( folder == null )
		{
			return;
		}
		folder.stopTween();
		folder.setVisible( true );
		folder.setScale( 0 , 0 );
		FolderLargeTween = folder.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , FOLDER_MAKE_TWEEN_DUARATION , 1.2f , 1.2f , 0 ).setUserData( folder ).setCallback( this );
	}
	
	//取代原本的FolderSmallAnim()
	//原因1：只有一个folder的reflectView，与large动画操作的是同一个posXY，但不可能与large动画同时做
	//原因2：small动画效果不明显
	private void FolderInvisible(
			View3D folder )
	{
		if( FolderLargeTween != null )
		{
			FolderLargeTween.free();
			FolderLargeTween = null;
		}
		if( folder == null )
		{
			return;
		}
		folder.stopTween();
		folder.setScale( 0 , 0 );
		folder.setVisible( false );
	}
	
	private boolean isCellUesdByShortcutlistToAdd(
			int cellX ,
			int cellY )
	{
		ArrayList<ShortcutInfo> addShortcutList = iLoongLauncher.getInstance().d3dListener.getShortcutlist();
		int mScreenCount = iLoongLauncher.getInstance().d3dListener.getScreenCount();
		int mScreen = -1;
		Workspace3D workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D();
		if( workspace == null )
			return false;
		for( int i = 0 ; i < mScreenCount ; i++ )
		{
			CellLayout3D cell;
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				View3D child = workspace.getChildAt( i );
				if( child instanceof CellLayout3D )
				{
					cell = (CellLayout3D)child;
				}
				else
				{
					return false;
				}
			}
			else
			{
				cell = (CellLayout3D)workspace.getChildAt( i );
			}
			if( cell == this )
			{
				mScreen = i;
				break;
			}
		}
		if( addShortcutList != null )
		{
			for( ShortcutInfo currentinfo : addShortcutList )
			{
				if( ( currentinfo.screen == mScreen ) && ( currentinfo.cellX == cellX ) && ( currentinfo.cellY == cellY ) )
				{
					return true;
				}
			}
		}
		return false;
	}
	
	// for push icon jbc start
	/**
	 * This helper class defines a cluster of views. It helps with defining
	 * complex edges of the cluster and determining how those edges interact
	 * with other views. The edges essentially define a fine-grained boundary
	 * around the cluster of views -- like a more precise version of a bounding
	 * box.
	 */
	private class ViewCluster
	{
		
		final static int LEFT = 0;
		final static int TOP = 1;
		final static int RIGHT = 2;
		final static int BOTTOM = 3;
		ArrayList<View3D> views;
		ItemConfiguration config;
		Rect boundingRect = new Rect();
		int[] leftEdge = new int[mCountY];
		int[] rightEdge = new int[mCountY];
		int[] topEdge = new int[mCountX];
		int[] bottomEdge = new int[mCountX];
		boolean leftEdgeDirty , rightEdgeDirty , topEdgeDirty , bottomEdgeDirty , boundingRectDirty;
		
		@SuppressWarnings( "unchecked" )
		public ViewCluster(
				ArrayList<View3D> views ,
				ItemConfiguration config )
		{
			this.views = (ArrayList<View3D>)views.clone();
			this.config = config;
			resetEdges();
		}
		
		void resetEdges()
		{
			for( int i = 0 ; i < mCountX ; i++ )
			{
				topEdge[i] = -1;
				bottomEdge[i] = -1;
			}
			for( int i = 0 ; i < mCountY ; i++ )
			{
				leftEdge[i] = -1;
				rightEdge[i] = -1;
			}
			leftEdgeDirty = true;
			rightEdgeDirty = true;
			bottomEdgeDirty = true;
			topEdgeDirty = true;
			boundingRectDirty = true;
		}
		
		void computeEdge(
				int which ,
				int[] edge )
		{
			int count = views.size();
			for( int i = 0 ; i < count ; i++ )
			{
				CellAndSpan cs = config.map.get( views.get( i ) );
				switch( which )
				{
					case LEFT:
						int left = cs.x;
						for( int j = cs.y ; j < cs.y + cs.spanY ; j++ )
						{
							if( j < mCountY )
							{
								if( left < edge[j] || edge[j] < 0 )
								{
									edge[j] = left;
								}
							}
						}
						break;
					case RIGHT:
						int right = cs.x + cs.spanX;
						for( int j = cs.y ; j < cs.y + cs.spanY ; j++ )
						{
							if( j < mCountY )
							{
								if( right > edge[j] )
								{
									edge[j] = right;
								}
							}
						}
						break;
					case TOP:
						int top = cs.y;
						for( int j = cs.x ; j < cs.x + cs.spanX ; j++ )
						{
							if( j < mCountX )
							{
								if( top < edge[j] || edge[j] < 0 )
								{
									edge[j] = top;
								}
							}
						}
						break;
					case BOTTOM:
						int bottom = cs.y + cs.spanY;
						for( int j = cs.x ; j < cs.x + cs.spanX ; j++ )
						{
							if( j < mCountX )
							{
								if( bottom > edge[j] )
								{
									edge[j] = bottom;
								}
							}
						}
						break;
				}
			}
		}
		
		boolean isViewTouchingEdge(
				View3D v ,
				int whichEdge )
		{
			CellAndSpan cs = config.map.get( v );
			int[] edge = getEdge( whichEdge );
			switch( whichEdge )
			{
				case LEFT:
					for( int i = cs.y ; i < cs.y + cs.spanY ; i++ )
					{
						if( i < mCountY )
						{
							if( edge[i] == cs.x + cs.spanX )
							{
								return true;
							}
						}
					}
					break;
				case RIGHT:
					for( int i = cs.y ; i < cs.y + cs.spanY ; i++ )
					{
						if( i < mCountY )
						{
							if( edge[i] == cs.x )
							{
								return true;
							}
						}
					}
					break;
				case TOP:
					for( int i = cs.x ; i < cs.x + cs.spanX ; i++ )
					{
						if( i < mCountX )
						{
							if( edge[i] == cs.y + cs.spanY )
							{
								return true;
							}
						}
					}
					break;
				case BOTTOM:
					for( int i = cs.x ; i < cs.x + cs.spanX ; i++ )
					{
						if( i < mCountX )
						{
							if( edge[i] == cs.y )
							{
								return true;
							}
						}
					}
					break;
			}
			return false;
		}
		
		void shift(
				int whichEdge ,
				int delta )
		{
			for( View3D v : views )
			{
				CellAndSpan c = config.map.get( v );
				switch( whichEdge )
				{
					case LEFT:
						c.x -= delta;
						break;
					case RIGHT:
						c.x += delta;
						break;
					case TOP:
						c.y -= delta;
						break;
					case BOTTOM:
					default:
						c.y += delta;
						break;
				}
			}
			resetEdges();
		}
		
		public void addView(
				View3D v )
		{
			views.add( v );
			resetEdges();
		}
		
		public Rect getBoundingRect()
		{
			if( boundingRectDirty )
			{
				boolean first = true;
				for( View3D v : views )
				{
					CellAndSpan c = config.map.get( v );
					if( first )
					{
						boundingRect.set( c.x , c.y , c.x + c.spanX , c.y + c.spanY );
						first = false;
					}
					else
					{
						boundingRect.union( c.x , c.y , c.x + c.spanX , c.y + c.spanY );
					}
				}
			}
			return boundingRect;
		}
		
		public int[] getEdge(
				int which )
		{
			switch( which )
			{
				case LEFT:
					return getLeftEdge();
				case RIGHT:
					return getRightEdge();
				case TOP:
					return getTopEdge();
				case BOTTOM:
				default:
					return getBottomEdge();
			}
		}
		
		public int[] getLeftEdge()
		{
			if( leftEdgeDirty )
			{
				computeEdge( LEFT , leftEdge );
			}
			return leftEdge;
		}
		
		public int[] getRightEdge()
		{
			if( rightEdgeDirty )
			{
				computeEdge( RIGHT , rightEdge );
			}
			return rightEdge;
		}
		
		public int[] getTopEdge()
		{
			if( topEdgeDirty )
			{
				computeEdge( TOP , topEdge );
			}
			return topEdge;
		}
		
		public int[] getBottomEdge()
		{
			if( bottomEdgeDirty )
			{
				computeEdge( BOTTOM , bottomEdge );
			}
			return bottomEdge;
		}
		
		PositionComparator comparator = new PositionComparator();
		
		class PositionComparator implements Comparator<View3D>
		{
			
			int whichEdge = 0;
			
			public int compare(
					View3D left ,
					View3D right )
			{
				CellAndSpan l = config.map.get( left );
				CellAndSpan r = config.map.get( right );
				switch( whichEdge )
				{
					case LEFT:
						return ( r.x + r.spanX ) - ( l.x + l.spanX );
					case RIGHT:
						return l.x - r.x;
					case TOP:
						return ( r.y + r.spanY ) - ( l.y + l.spanY );
					case BOTTOM:
					default:
						return l.y - r.y;
				}
			}
		}
		
		public void sortConfigurationForEdgePush(
				int edge )
		{
			comparator.whichEdge = edge;
			Collections.sort( config.sortedViews , comparator );
		}
	}
	
	private boolean pushViewsToTempLocation(
			ArrayList<View3D> views ,
			Rect rectOccupiedByPotentialDrop ,
			int[] direction ,
			View3D dragView ,
			ItemConfiguration currentState )
	{
		ViewCluster cluster = new ViewCluster( views , currentState );
		Rect clusterRect = cluster.getBoundingRect();
		int whichEdge;
		int pushDistance;
		boolean fail = false;
		// Determine the edge of the cluster that will be leading the push and
		// how far
		// the cluster must be shifted.
		if( direction[0] < 0 )
		{
			whichEdge = ViewCluster.LEFT;
			pushDistance = clusterRect.right - rectOccupiedByPotentialDrop.left;
		}
		else if( direction[0] > 0 )
		{
			whichEdge = ViewCluster.RIGHT;
			pushDistance = rectOccupiedByPotentialDrop.right - clusterRect.left;
		}
		else if( direction[1] < 0 )
		{
			whichEdge = ViewCluster.TOP;
			pushDistance = clusterRect.bottom - rectOccupiedByPotentialDrop.top;
		}
		else
		{
			whichEdge = ViewCluster.BOTTOM;
			pushDistance = rectOccupiedByPotentialDrop.bottom - clusterRect.top;
		}
		// Break early for invalid push distance.
		if( pushDistance <= 0 )
		{
			return false;
		}
		// Mark the occupied state as false for the group of views we want to
		// move.
		for( View3D v : views )
		{
			CellAndSpan c = currentState.map.get( v );
			markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , false );
		}
		// We save the current configuration -- if we fail to find a solution we
		// will revert
		// to the initial state. The process of finding a solution modifies the
		// configuration
		// in place, hence the need for revert in the failure case.
		currentState.save();
		// The pushing algorithm is simplified by considering the views in the
		// order in which
		// they would be pushed by the cluster. For example, if the cluster is
		// leading with its
		// left edge, we consider sort the views by their right edge, from right
		// to left.
		cluster.sortConfigurationForEdgePush( whichEdge );
		while( pushDistance > 0 && !fail )
		{
			for( View3D v : currentState.sortedViews )
			{
				// For each view that isn't in the cluster, we see if the
				// leading edge of the
				// cluster is contacting the edge of that view. If so, we add
				// that view to the
				// cluster.
				if( !cluster.views.contains( v ) && v != dragView )
				{
					if( cluster.isViewTouchingEdge( v , whichEdge ) )
					{
						// LayoutParams lp = (LayoutParams) v.getLayoutParams();
						// if (!lp.canReorder) {
						// // The push solution includes the all apps button,
						// // this is not viable.
						// fail = true;
						// break;
						// }
						cluster.addView( v );
						CellAndSpan c = currentState.map.get( v );
						// Adding view to cluster, mark it as not occupied.
						markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , false );
					}
				}
			}
			pushDistance--;
			// The cluster has been completed, now we move the whole thing over
			// in the appropriate
			// direction.
			cluster.shift( whichEdge , 1 );
		}
		boolean foundSolution = false;
		clusterRect = cluster.getBoundingRect();
		// Due to the nature of the algorithm, the only check required to verify
		// a valid solution
		// is to ensure that completed shifted cluster lies completely within
		// the cell layout.
		if( !fail && clusterRect.left >= 0 && clusterRect.right <= mCountX && clusterRect.top >= 0 && clusterRect.bottom <= mCountY )
		{
			foundSolution = true;
		}
		else
		{
			currentState.restore();
		}
		// In either case, we set the occupied array as marked for the location
		// of the views
		for( View3D v : cluster.views )
		{
			CellAndSpan c = currentState.map.get( v );
			markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , true );
		}
		return foundSolution;
	}
	
	// This method tries to find a reordering solution which satisfies the push
	// mechanic by trying
	// to push items in each of the cardinal directions, in an order based on
	// the direction vector
	// passed.
	private boolean attemptPushInDirection(
			ArrayList<View3D> intersectingViews ,
			Rect occupied ,
			int[] direction ,
			View3D ignoreView ,
			ItemConfiguration solution )
	{
		if( ( Math.abs( direction[0] ) + Math.abs( direction[1] ) ) > 1 )
		{
			// If the direction vector has two non-zero components, we try
			// pushing
			// separately in each of the components.
			int temp = direction[1];
			direction[1] = 0;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			direction[1] = temp;
			temp = direction[0];
			direction[0] = 0;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			// Revert the direction
			direction[0] = temp;
			// Now we try pushing in each component of the opposite direction
			direction[0] *= -1;
			direction[1] *= -1;
			temp = direction[1];
			direction[1] = 0;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			direction[1] = temp;
			temp = direction[0];
			direction[0] = 0;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			// revert the direction
			direction[0] = temp;
			direction[0] *= -1;
			direction[1] *= -1;
		}
		else
		{
			// If the direction vector has a single non-zero component, we push
			// first in the
			// direction of the vector
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			// Then we try the opposite direction
			direction[0] *= -1;
			direction[1] *= -1;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			// Switch the direction back
			direction[0] *= -1;
			direction[1] *= -1;
			// If we have failed to find a push solution with the above, then we
			// try
			// to find a solution by pushing along the perpendicular axis.
			// Swap the components
			int temp = direction[1];
			direction[1] = direction[0];
			direction[0] = temp;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			// Then we try the opposite direction
			direction[0] *= -1;
			direction[1] *= -1;
			if( pushViewsToTempLocation( intersectingViews , occupied , direction , ignoreView , solution ) )
			{
				return true;
			}
			// Switch the direction back
			direction[0] *= -1;
			direction[1] *= -1;
			// Swap the components back
			temp = direction[1];
			direction[1] = direction[0];
			direction[0] = temp;
		}
		return false;
	}
	
	private boolean rearrangementExists(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			int[] direction ,
			View3D ignoreView ,
			ItemConfiguration solution )
	{
		// Return early if get invalid cell positions
		if( cellX < 0 || cellY < 0 )
			return false;
		mIntersectingViews.clear();
		mOccupiedRect.set( cellX , cellY , cellX + spanX , cellY + spanY );
		// Mark the desired location of the view currently being dragged.
		if( ignoreView != null )
		{
			CellAndSpan c = solution.map.get( ignoreView );
			if( c != null )
			{
				c.x = cellX;
				c.y = cellY;
			}
		}
		Rect r0 = new Rect( cellX , cellY , cellX + spanX , cellY + spanY );
		Rect r1 = new Rect();
		for( View3D child : solution.map.keySet() )
		{
			if( child == ignoreView )
				continue;
			CellAndSpan c = solution.map.get( child );
			// LayoutParams lp = (LayoutParams) child.getLayoutParams();
			r1.set( c.x , c.y , c.x + c.spanX , c.y + c.spanY );
			if( Rect.intersects( r0 , r1 ) )
			{
				// if (!lp.canReorder) {
				// return false;
				// }
				mIntersectingViews.add( child );
			}
		}
		// First we try to find a solution which respects the push mechanic.
		// That is,
		// we try to find a solution such that no displaced item travels through
		// another item
		// without also displacing that item.
		if( attemptPushInDirection( mIntersectingViews , mOccupiedRect , direction , ignoreView , solution ) )
		{
			return true;
		}
		// Next we try moving the views as a block, but without requiring the
		// push mechanic.
		if( addViewsToTempLocation( mIntersectingViews , mOccupiedRect , direction , false , ignoreView , solution ) )
		{
			return true;
		}
		// Ok, they couldn't move as a block, let's move them individually
		for( View3D v : mIntersectingViews )
		{
			if( !addViewToTempLocation( v , mOccupiedRect , direction , solution ) )
			{
				return false;
			}
		}
		return true;
	}
	
	ItemConfiguration simpleSwap(
			int pixelX ,
			int pixelY ,
			int minSpanX ,
			int minSpanY ,
			int spanX ,
			int spanY ,
			int[] direction ,
			View3D dragView ,
			boolean decX ,
			ItemConfiguration solution )
	{
		// Copy the current state into the solution. This solution will be
		// manipulated as necessary.
		copyCurrentStateToSolution( solution , dragView , false );
		// Copy the current occupied array into the temporary occupied array.
		// This array will be
		// manipulated as necessary to find a solution.
		copyOccupiedArray( mTmpOccupied );
		// We find the nearest cell into which we would place the dragged item,
		// assuming there's
		// nothing in its way.
		int result[] = new int[2];
		result = findNearestArea( pixelX , pixelY , spanX , spanY , result );
		boolean success = false;
		// First we try the exact nearest position of the item being dragged,
		// we will then want to try to move this around to other neighbouring
		// positions
		success = rearrangementExists( result[0] , result[1] , spanX , spanY , direction , dragView , solution );
		if( !success )
		{
			// We try shrinking the widget down to size in an alternating
			// pattern, shrink 1 in
			// x, then 1 in y etc.
			if( spanX > minSpanX && ( minSpanY == spanY || decX ) )
			{
				return simpleSwap( pixelX , pixelY , minSpanX , minSpanY , spanX - 1 , spanY , direction , dragView , false , solution );
			}
			else if( spanY > minSpanY )
			{
				return simpleSwap( pixelX , pixelY , minSpanX , minSpanY , spanX , spanY - 1 , direction , dragView , true , solution );
			}
			solution.isSolution = false;
		}
		else
		{
			solution.isSolution = true;
			solution.dragViewX = result[0];
			solution.dragViewY = result[1];
			solution.dragViewSpanX = spanX;
			solution.dragViewSpanY = spanY;
		}
		return solution;
	}
	
	private void copyCurrentStateToSolution(
			ItemConfiguration solution ,
			View3D dragView ,
			boolean temp )
	{
		int childCount = getChildCount();
		for( int i = 0 ; i < childCount ; i++ )
		{
			View3D child = getChildAt( i );
			if( child.isReflectView() )
			{
				continue;
			}
			if( child instanceof IconBase3D )
			{
				// LayoutParams lp = (LayoutParams) child.getLayoutParams();
				ItemInfo itemInfo = ( (IconBase3D)child ).getItemInfo();
				int spanX = drapGetSpanX( child );
				int spanY = drapGetSpanY( child );
				CellAndSpan c;
				if( temp )
				{
					// c = new CellAndSpan(lp.tmpCellX, lp.tmpCellY,
					// lp.cellHSpan,
					// lp.cellVSpan);
					c = new CellAndSpan( itemInfo.cellTempX , itemInfo.cellTempY , spanX , spanY );
				}
				else
				{
					c = new CellAndSpan( itemInfo.cellX , itemInfo.cellY , spanX , spanY );
				}
				solution.add( child , c );
			}
		}
		//如果dragView之前是否属于该cellLayout3D就加上去
		if( dragView instanceof IconBase3D )
		{
			ItemInfo itemInfo = ( (IconBase3D)dragView ).getItemInfo();
			int view_screen = itemInfo.screen;
			int cur_screen = -1;
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.NormalMode )
			{
				cur_screen = ( (Workspace3D)viewParent ).getCurrentScreen() - 1;
			}
			else
			{
				cur_screen = ( (Workspace3D)viewParent ).getCurrentScreen();
			}
			if( view_screen == cur_screen && cur_screen != -1 )
			{
				int spanX = drapGetSpanX( dragView );
				int spanY = drapGetSpanY( dragView );
				CellAndSpan c;
				if( temp )
				{
					c = new CellAndSpan( itemInfo.cellTempX , itemInfo.cellTempY , spanX , spanY );
				}
				else
				{
					c = new CellAndSpan( itemInfo.cellX , itemInfo.cellY , spanX , spanY );
				}
				solution.add( dragView , c );
			}
		}
	}
	
	private void copySolutionToTempState(
			ItemConfiguration solution ,
			View3D dragView )
	{
		for( int i = 0 ; i < mCountX ; i++ )
		{
			for( int j = 0 ; j < mCountY ; j++ )
			{
				mTmpOccupied[i][j] = false;
			}
		}
		int childCount = getChildCount();
		for( int i = 0 ; i < childCount ; i++ )
		{
			View3D child = getChildAt( i );
			if( child == dragView )
				continue;
			if( child.isReflectView() )
				continue;
			if( !( child instanceof IconBase3D ) )
				continue;
			// LayoutParams lp = (LayoutParams) child.getLayoutParams();
			CellAndSpan c = solution.map.get( child );
			if( c != null )
			{
				// lp.tmpCellX = c.x;
				// lp.tmpCellY = c.y;
				// lp.cellHSpan = c.spanX;
				// lp.cellVSpan = c.spanY;
				ItemInfo itemInfo = ( (IconBase3D)child ).getItemInfo();
				itemInfo.cellTempX = c.x;
				itemInfo.cellTempY = c.y;
				//				itemInfo.spanX = c.spanX;
				//				itemInfo.spanY = c.spanY;
				( (IconBase3D)child ).setItemInfo( itemInfo );
				markCellsForView( c.x , c.y , c.spanX , c.spanY , mTmpOccupied , true );
			}
		}
		markCellsForView( solution.dragViewX , solution.dragViewY , solution.dragViewSpanX , solution.dragViewSpanY , mTmpOccupied , true );
	}
	
	int[] createArea(
			int pixelX ,
			int pixelY ,
			int minSpanX ,
			int minSpanY ,
			int spanX ,
			int spanY ,
			View3D dragView ,
			int[] result ,
			int resultSpan[] ,
			int mode )
	{
		// First we determine if things have moved enough to cause a different
		// layout
		result = findNearestArea( pixelX , pixelY , spanX , spanY , result );
		if( resultSpan == null )
		{
			resultSpan = new int[2];
		}
		// When we are checking drop validity or actually dropping, we don't
		// recompute the
		// direction vector, since we want the solution to match the preview,
		// and it's possible
		// that the exact position of the item has changed to result in a new
		// reordering outcome.
		if( ( mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL || mode == MODE_ACCEPT_DROP ) && mPreviousReorderDirection[0] != INVALID_DIRECTION )
		{
			mDirectionVector[0] = mPreviousReorderDirection[0];
			mDirectionVector[1] = mPreviousReorderDirection[1];
			// We reset this vector after drop
			if( mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL )
			{
				mPreviousReorderDirection[0] = INVALID_DIRECTION;
				mPreviousReorderDirection[1] = INVALID_DIRECTION;
			}
		}
		else
		{
			getDirectionVectorForDrop( pixelX , pixelY , spanX , spanY , dragView , mDirectionVector );
			mPreviousReorderDirection[0] = mDirectionVector[0];
			mPreviousReorderDirection[1] = mDirectionVector[1];
		}
		ItemConfiguration swapSolution = simpleSwap( pixelX , pixelY , minSpanX , minSpanY , spanX , spanY , mDirectionVector , dragView , true , new ItemConfiguration() );
		// We attempt the approach which doesn't shuffle views at all
		ItemConfiguration noShuffleSolution = findConfigurationNoShuffle( pixelX , pixelY , minSpanX , minSpanY , spanX , spanY , dragView , new ItemConfiguration() );
		ItemConfiguration finalSolution = null;
		if( swapSolution.isSolution && swapSolution.area() >= noShuffleSolution.area() )
		{
			finalSolution = swapSolution;
			Workspace3D.setDragMode( Workspace3D.DRAG_MODE_REORDER );
		}
		else if( noShuffleSolution.isSolution )
		{
			finalSolution = noShuffleSolution;
			Workspace3D.setDragMode( Workspace3D.DRAG_MODE_NONE );
		}
		boolean foundSolution = true;
		if( finalSolution != null )
		{
			result[0] = finalSolution.dragViewX;
			result[1] = finalSolution.dragViewY;
			resultSpan[0] = finalSolution.dragViewSpanX;
			resultSpan[1] = finalSolution.dragViewSpanY;
			// If we're just testing for a possible location (MODE_ACCEPT_DROP),
			// we don't bother
			// committing anything or animating anything as we just want to
			// determine if a solution
			// exists
			if( mode == MODE_DRAG_OVER || mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL )
			{
				copySolutionToTempState( finalSolution , dragView );
				animateItemsToSolution( finalSolution , dragView , mode == MODE_ON_DROP );
				//				if( !DESTRUCTIVE_REORDER && ( mode == MODE_ON_DROP || mode == MODE_ON_DROP_EXTERNAL ) )
				//				{
				//					commitTempPlacement();
				//					completeAndClearReorderHintAnimations();
				//					setItemPlacementDirty( false );
				//				}
				//				else
				//				{
				//					beginOrAdjustHintAnimations( finalSolution , dragView , REORDER_ANIMATION_DURATION );
				//				}
			}
		}
		else
		{
			foundSolution = false;
			result[0] = result[1] = resultSpan[0] = resultSpan[1] = -1;
			startReorderTween( REORDER_TWEEN_TYPE_MOVE_TO_ORI );
		}
		return result;
	}
	
	/*
	 * This seems like it should be obvious and straight-forward, but when the
	 * direction vector needs to match with the notion of the dragView pushing
	 * other views, we have to employ a slightly more subtle notion of the
	 * direction vector. The question is what two points is the vector between?
	 * The center of the dragView and its desired destination? Not quite, as
	 * this doesn't necessarily coincide with the interaction of the dragView
	 * and items occupying those cells. Instead we use some heuristics to often
	 * lock the vector to up, down, left or right, which helps make pushing feel
	 * right.
	 */
	private void getDirectionVectorForDrop(
			int dragViewCenterX ,
			int dragViewCenterY ,
			int spanX ,
			int spanY ,
			View3D dragView ,
			int[] resultDirection )
	{
		int[] targetDestination = new int[2];
		findNearestArea( dragViewCenterX , dragViewCenterY , spanX , spanY , targetDestination );
		Rect dragRect = new Rect();
		regionToRect( targetDestination[0] , targetDestination[1] , spanX , spanY , dragRect );
		dragRect.offset( dragViewCenterX - dragRect.centerX() , dragViewCenterY - dragRect.centerY() );
		Rect dropRegionRect = new Rect();
		getViewsIntersectingRegion( targetDestination[0] , targetDestination[1] , spanX , spanY , dragView , dropRegionRect , mIntersectingViews );
		int dropRegionSpanX = dropRegionRect.width();
		int dropRegionSpanY = dropRegionRect.height();
		regionToRect( dropRegionRect.left , dropRegionRect.top , dropRegionRect.width() , dropRegionRect.height() , dropRegionRect );
		int deltaX = ( dropRegionRect.centerX() - dragViewCenterX ) / spanX;
		int deltaY = ( dropRegionRect.centerY() - dragViewCenterY ) / spanY;
		if( dropRegionSpanX == mCountX || spanX == mCountX )
		{
			deltaX = 0;
		}
		if( dropRegionSpanY == mCountY || spanY == mCountY )
		{
			deltaY = 0;
		}
		if( deltaX == 0 && deltaY == 0 )
		{
			// No idea what to do, give a random direction.
			resultDirection[0] = 1;
			resultDirection[1] = 0;
		}
		else
		{
			computeDirectionVector( deltaX , deltaY , resultDirection );
		}
	}
	
	// For a given cell and span, fetch the set of views intersecting the
	// region.
	private void getViewsIntersectingRegion(
			int cellX ,
			int cellY ,
			int spanX ,
			int spanY ,
			View3D dragView ,
			Rect boundingRect ,
			ArrayList<View3D> intersectingViews )
	{
		if( boundingRect != null )
		{
			boundingRect.set( cellX , cellY , cellX + spanX , cellY + spanY );
		}
		intersectingViews.clear();
		Rect r0 = new Rect( cellX , cellY , cellX + spanX , cellY + spanY );
		Rect r1 = new Rect();
		final int count = getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D child = getChildAt( i );
			if( child == dragView )
				continue;
			if( child.isReflectView() )
				continue;
			if( !( child instanceof IconBase3D ) )
				continue;
			// LayoutParams lp = (LayoutParams) child.getLayoutParams();
			// r1.set(lp.cellX, lp.cellY, lp.cellX + lp.cellHSpan, lp.cellY
			// + lp.cellVSpan);
			ItemInfo itemInfo = ( (IconBase3D)child ).getItemInfo();
			r1.set( itemInfo.cellX , itemInfo.cellY , itemInfo.cellX + drapGetSpanX( child ) , itemInfo.cellY + drapGetSpanY( child ) );
			if( Rect.intersects( r0 , r1 ) )
			{
				mIntersectingViews.add( child );
				if( boundingRect != null )
				{
					boundingRect.union( r1 );
				}
			}
		}
	}
	
	boolean isNearestDropLocationOccupied(
			int pixelX ,
			int pixelY ,
			int spanX ,
			int spanY ,
			View3D dragView ,
			int[] result )
	{
		result = findNearestArea( pixelX , pixelY , spanX , spanY , result );
		getViewsIntersectingRegion( result[0] , result[1] , spanX , spanY , dragView , null , mIntersectingViews );
		return !mIntersectingViews.isEmpty();
	}
	
	ItemConfiguration findConfigurationNoShuffle(
			int pixelX ,
			int pixelY ,
			int minSpanX ,
			int minSpanY ,
			int spanX ,
			int spanY ,
			View3D dragView ,
			ItemConfiguration solution )
	{
		int[] result = new int[2];
		int[] resultSpan = new int[2];
		findNearestVacantArea( pixelX , pixelY , minSpanX , minSpanY , spanX , spanY , null , result , resultSpan );
		if( result[0] >= 0 && result[1] >= 0 )
		{
			copyCurrentStateToSolution( solution , dragView , false );
			solution.dragViewX = result[0];
			solution.dragViewY = result[1];
			solution.dragViewSpanX = resultSpan[0];
			solution.dragViewSpanY = resultSpan[1];
			solution.isSolution = true;
		}
		else
		{
			solution.isSolution = false;
		}
		return solution;
	}
	
	private void manageFolderFeedback(
			View3D dragView ,
			float distance ,
			View3D dragTargetView )
	{
		if( Workspace3D.getDragMode() != Workspace3D.DRAG_MODE_NONE )
		{
			return;
		}
		if( distance > DefaultLayout.app_icon_size * 0.55f )
		{
			return;
		}
		if( !( dragView instanceof Icon3D ) )
		{
			return;
		}
		if( dragTargetView == null )
		{
			return;
		}
		if( dragTargetView instanceof Icon3D )
		{
			Workspace3D.setDragMode( Workspace3D.DRAG_MODE_CREATE_FOLDER );
		}
		else if( dragTargetView instanceof FolderIcon3D )
		{
			Workspace3D.setDragMode( Workspace3D.DRAG_MODE_ADD_TO_FOLDER );
		}
	}
	
	private Timeline reorderTween = null;
	private Timeline revertTween = null;
	
	private void animateItemsToSolution(
			ItemConfiguration solution ,
			View3D dragView ,
			boolean commitDragView )
	{
		reorderViewList.clear();
		boolean[][] occupied = mTmpOccupied;
		for( int i = 0 ; i < mCountX ; i++ )
		{
			for( int j = 0 ; j < mCountY ; j++ )
			{
				occupied[i][j] = false;
			}
		}
		int childCount = getChildCount();
		for( int i = 0 ; i < childCount ; i++ )
		{
			View3D child = getChildAt( i );
			if( child == dragView )
				continue;
			if( child.isReflectView() )
				continue;
			if( !( child instanceof IconBase3D ) )
				continue;
			CellAndSpan c = solution.map.get( child );
			if( c != null )
			{
				ItemInfo itemInfo = ( (IconBase3D)child ).getItemInfo();
				int oriCellX = itemInfo.cellX;
				int oriCellY = itemInfo.cellY;
				if( oriCellX != c.x || oriCellY != c.y )
				{
					//					int spanX = drapGetSpanX( child );
					//					int spanY = drapGetSpanY( child );
					ReorderViewInfo rvInfo = new ReorderViewInfo();
					rvInfo.cellX = oriCellX;
					rvInfo.cellY = oriCellY;
					rvInfo.cellTempX = c.x;
					rvInfo.cellTempY = c.y;
					//					rvInfo.spanX = spanX;
					//					rvInfo.spanY = spanY;
					reorderViewList.put( child , rvInfo );
				}
				markCellsForView( c.x , c.y , c.spanX , c.spanY , occupied , true );
			}
		}
		if( commitDragView )
		{
			markCellsForView( solution.dragViewX , solution.dragViewY , solution.dragViewSpanX , solution.dragViewSpanY , occupied , true );
		}
		//从revertViewList中剔除掉reorderViewList也有的view		
		for( View3D reorderView : reorderViewList.keySet() )
		{
			if( revertViewList.containsKey( reorderView ) )
			{
				revertViewList.remove( reorderView );
			}
		}
		startReorderTween( REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_SWING );
		startRevertTween();
		revertViewList.clear();
		revertViewList = (HashMap<View3D , ReorderViewInfo>)( reorderViewList.clone() );
	}
	
	public void regionPointToCell(
			int x ,
			int y ,
			int spanX ,
			int spanY ,
			int[] result )
	{
		final int hStartPadding = getPaddingLeft();
		final int vStartPadding = getPaddingBottom();
		if( x - hStartPadding < 0 )
		{
			result[0] = 0;
		}
		else
		{
			result[0] = ( x - hStartPadding ) / ( mCellWidth + mWidthGap );
		}
		if( y - vStartPadding < 0 )
		{
			result[1] = 0;
		}
		else
		{
			result[1] = ( y - vStartPadding ) / ( mCellHeight + mHeightGap );
		}
	}
	
	public static void setSwingPoint(
			int[] oriPoint ,
			int[] tmpPoint ,
			float radius ,
			int[] swingPoint )
	{
		float ori2tmpDistance = (float)( Math.sqrt( (double)( ( oriPoint[0] - tmpPoint[0] ) * ( oriPoint[0] - tmpPoint[0] ) + ( oriPoint[1] - tmpPoint[1] ) * ( oriPoint[1] - tmpPoint[1] ) ) ) );
		swingPoint[0] = (int)( radius * ( oriPoint[0] - tmpPoint[0] ) / ori2tmpDistance + tmpPoint[0] );
		swingPoint[1] = (int)( radius * ( oriPoint[1] - tmpPoint[1] ) / ori2tmpDistance + tmpPoint[1] );
	}
	
	private HashMap<View3D , ReorderViewInfo> reorderViewList = new HashMap<View3D , ReorderViewInfo>();
	private HashMap<View3D , ReorderViewInfo> revertViewList = new HashMap<View3D , ReorderViewInfo>();
	
	class ReorderViewInfo
	{
		
		int cellX;
		int cellY;
		int cellTempX;
		int cellTempY;
		
		public ReorderViewInfo()
		{
			cellX = cellY = cellTempX = cellTempY = -1;
		}
	}
	
	public static final float FOLDER_MAKE_TWEEN_DUARATION = 0.25f;
	public static final float REORDER_WAIT_DUARATION = 0.3f;
	public static final float REORDER_MOVE_DUARATION = 0.1f;
	public static final float REORDER_SWING_DUARATION = 0.3f;
	public static final float SWING_DISTANCE_RATIO = 0.2f;
	public static final int REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_SWING = 0;
	public static final int REORDER_TWEEN_TYPE_SWING_TO_ORI = 1;
	public static final int REORDER_TWEEN_TYPE_SWING_TO_TEMP = 2;
	public static final int REORDER_TWEEN_TYPE_MOVE_TO_ORI = 3;
	public static final int REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP = 4;
	
	public void startReorderTween(
			int type )
	{
		if( reorderTween != null )
		{
			int oldType = (Integer)( reorderTween.getUserData() );
			if( oldType == type )
				return;
			if( oldType == REORDER_TWEEN_TYPE_MOVE_TO_ORI || oldType == REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP )
				return;
			reorderTween.free();
			reorderTween = null;
		}
		if( reorderViewList.isEmpty() )
		{
			return;
		}
		reorderTween = Timeline.createParallel();
		for( View3D v : reorderViewList.keySet() )
		{
			int cellX = reorderViewList.get( v ).cellX;
			int cellY = reorderViewList.get( v ).cellY;
			int cellTempX = reorderViewList.get( v ).cellTempX;
			int cellTempY = reorderViewList.get( v ).cellTempY;
			int spanX = drapGetSpanX( v );//reorderViewList.get( v ).spanX;
			int spanY = drapGetSpanY( v );//reorderViewList.get( v ).spanY;
			cellToPoint( cellX , cellY , mTargetPoint );
			mOriPoint[0] = (int)( mTargetPoint[0] + ( spanX * mCellWidth - v.width ) / 2 );
			mOriPoint[1] = (int)( mTargetPoint[1] + ( spanY * mCellHeight - v.height ) / 2 );
			cellToPoint( cellTempX , cellTempY , mTargetPoint );
			mTempPoint[0] = (int)( mTargetPoint[0] + ( spanX * mCellWidth - v.width ) / 2 );
			mTempPoint[1] = (int)( mTargetPoint[1] + ( spanY * mCellHeight - v.height ) / 2 );
			switch( type )
			{
				case REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_SWING:
					reorderTween.push( Tween.to( v , View3DTweenAccessor.POS_XY , REORDER_MOVE_DUARATION ).ease( Linear.INOUT ).target( mTempPoint[0] , mTempPoint[1] , 0 ) );
					break;
				case REORDER_TWEEN_TYPE_SWING_TO_ORI:
					float swingDistance = DefaultLayout.app_icon_size * SWING_DISTANCE_RATIO;
					setSwingPoint( mOriPoint , mTempPoint , swingDistance , mSwingPoint );
					reorderTween.push( Tween.to( v , View3DTweenAccessor.POS_XY , REORDER_SWING_DUARATION ).ease( Linear.INOUT ).target( mSwingPoint[0] , mSwingPoint[1] , 0 ) );
					break;
				case REORDER_TWEEN_TYPE_SWING_TO_TEMP:
					reorderTween.push( Tween.to( v , View3DTweenAccessor.POS_XY , REORDER_SWING_DUARATION ).ease( Linear.INOUT ).target( mTempPoint[0] , mTempPoint[1] , 0 ) );
					break;
				case REORDER_TWEEN_TYPE_MOVE_TO_ORI:
					reorderTween.push( Tween.to( v , View3DTweenAccessor.POS_XY , REORDER_MOVE_DUARATION ).ease( Linear.INOUT ).target( mOriPoint[0] , mOriPoint[1] , 0 ) );
					if( v instanceof IconBase3D )
					{
						ItemInfo itemInfo = ( (IconBase3D)v ).getItemInfo();
						itemInfo.cellTempX = itemInfo.cellX;
						itemInfo.cellTempY = itemInfo.cellY;
					}
					break;
				case REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP:
					reorderTween.push( Tween.to( v , View3DTweenAccessor.POS_XY , REORDER_MOVE_DUARATION ).ease( Linear.INOUT ).target( mTempPoint[0] , mTempPoint[1] , 0 ) );
					removeView( v );
					break;
			}
		}
		if( type == REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP )
		{
			for( View3D v : reorderViewList.keySet() )
			{
				int cellTempX = reorderViewList.get( v ).cellTempX;
				int cellTempY = reorderViewList.get( v ).cellTempY;
				if( v instanceof IconBase3D )
				{
					ItemInfo itemInfo = ( (IconBase3D)v ).getItemInfo();
					itemInfo.cellX = cellTempX;
					itemInfo.cellY = cellTempY;
					itemInfo.cellTempX = cellTempX;
					itemInfo.cellTempY = cellTempY;
					addView( v , itemInfo.cellX , itemInfo.cellY );
					Root3D.addOrMoveDB( itemInfo );
				}
			}
		}
		reorderTween.setUserData( type ).start( View3DTweenAccessor.manager ).setCallback( this );
		if( type == REORDER_TWEEN_TYPE_MOVE_TO_ORI || type == REORDER_TWEEN_TYPE_MOVE_TO_TEMP_FOR_DROP )
		{
			reorderViewList.clear();
			revertViewList.clear();
		}
	}
	
	private void startRevertTween()
	{
		if( revertTween != null )
		{
			revertTween.free();
			revertTween = null;
		}
		if( revertViewList.isEmpty() )
		{
			return;
		}
		revertTween = Timeline.createParallel();
		for( View3D v : revertViewList.keySet() )
		{
			int cellX = revertViewList.get( v ).cellX;
			int cellY = revertViewList.get( v ).cellY;
			int spanX = drapGetSpanX( v );
			int spanY = drapGetSpanY( v );
			cellToPoint( cellX , cellY , mTargetPoint );
			mOriPoint[0] = (int)( mTargetPoint[0] + ( spanX * mCellWidth - v.width ) / 2 );
			mOriPoint[1] = (int)( mTargetPoint[1] + ( spanY * mCellHeight - v.height ) / 2 );
			revertTween.push( Tween.to( v , View3DTweenAccessor.POS_XY , REORDER_MOVE_DUARATION ).ease( Linear.INOUT ).target( mOriPoint[0] , mOriPoint[1] , 0 ) );
			if( v instanceof IconBase3D )
			{
				ItemInfo itemInfo = ( (IconBase3D)v ).getItemInfo();
				itemInfo.cellTempX = itemInfo.cellX;
				itemInfo.cellTempY = itemInfo.cellY;
			}
		}
		revertTween.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	int[] findFirstFitArea(
			int minSpanX ,
			int minSpanY ,
			int spanX ,
			int spanY ,
			int[] result ,
			boolean[][] occupied )
	{
		final int[] bestXY = result != null ? result : new int[2];
		final int countX = mCountX;
		final int countY = mCountY;
		if( minSpanX <= 0 || minSpanY <= 0 || spanX <= 0 || spanY <= 0 || spanX < minSpanX || spanY < minSpanY )
		{
			return bestXY;
		}
		if( Workspace3D.EditModeAddItemType == Workspace3D.EditModeAddItemType_SHORTCUT )
		{
			for( int y = 0 ; y < countY - ( minSpanY - 1 ) ; y++ )
			{
				inner:
				for( int x = 0 ; x < countX - ( minSpanX - 1 ) ; x++ )
				{
					for( int i = 0 ; i < minSpanX ; i++ )
					{
						for( int j = 0 ; j < minSpanY ; j++ )
						{
							if( occupied[x + i][y + j] )
							{
								continue inner;
							}
						}
					}
					bestXY[0] = x;
					bestXY[1] = y;
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
					return bestXY;
				}
			}
			bestXY[0] = -1;
			bestXY[1] = -1;
		}
		else if( Workspace3D.EditModeAddItemType == Workspace3D.EditModeAddItemType_WIDGET )
		{
			for( int y = countY - ( minSpanY - 1 ) - 1 ; y > -1 ; y-- )
			{
				inner:
				for( int x = 0 ; x < countX - ( minSpanX - 1 ) ; x++ )
				{
					for( int i = 0 ; i < minSpanX ; i++ )
					{
						for( int j = 0 ; j < minSpanY ; j++ )
						{
							if( occupied[x + i][y + j] )
							{
								continue inner;
							}
						}
					}
					bestXY[0] = x;
					bestXY[1] = y;
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
					return bestXY;
				}
			}
			bestXY[0] = -1;
			bestXY[1] = -1;
		}
		Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
		return bestXY;
	}
}
