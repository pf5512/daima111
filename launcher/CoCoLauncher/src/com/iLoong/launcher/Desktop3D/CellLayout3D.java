package com.iLoong.launcher.Desktop3D;

//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ObjectAnimator;
//import android.animation.PropertyValuesHolder;
//import android.animation.TimeInterpolator;
//import android.animation.ValueAnimator;
//import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

//import com.android.launcher.R;
//import com.android.launcher2.FolderIcon.FolderRingAnimator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetMenuDesktop;
import com.iLoong.launcher.SideBar.SidebarMainGroup;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class CellLayout3D extends ViewGroup3D{
    static final String TAG = "cellLayout";

    public static final int MSG_ADD_DRAGLAYER = 0;
    public static final int MSG_PAGE_TO =1;
    public static final int MSG_JUMP_TO_HOTSEAT=2;
    public static final int MSG_REFRESH_PAGE=3;
    public static float currentX=0;
    public static float currentY=0;
    private int mOriginalCellWidth;
    private int mOriginalCellHeight;
    private int mCellWidth;
    private int mCellHeight;

    private int mOriginalWidthGap;
    private int mOriginalHeightGap;
    private int mWidthGap;
    private int mHeightGap;
    private int mMaxGap;
    private boolean mScrollingTransformsDirty = false;

    private final Rect mRect = new Rect();
    private final CellInfo mCellInfo = new CellInfo();

    // These are temporary variables to prevent having to allocate a new object just to
    // return an (x, y) value from helper functions. Do NOT use them to maintain other state.
    private final int[] mTmpXY = new int[2];
    private final int[] mTmpPoint = new int[2];
    private final PointF mTmpPointF = new PointF();
    int[] mTempLocation = new int[2];

    boolean[][] mOccupied;
    boolean[][] mTmpOccupied;
    View3D[][] cellViewList;
    
    private boolean mLastDownOnOccupiedCell = false;

//    private OnTouchListener mInterceptTouchListener;

//    private ArrayList<FolderRingAnimator> mFolderOuterRings = new ArrayList<FolderRingAnimator>();
    private int[] mFolderLeaveBehindCell = {-1, -1};

    private int mForegroundAlpha = 0;
    private float mBackgroundAlpha;
    private float mBackgroundAlphaMultiplier = 1.0f;
    
//    private Drawable mOverScrollForegroundDrawable;
//    private Drawable mOverScrollLeft;
//    private Drawable mOverScrollRight;
    private Rect mBackgroundRect;
    private Rect mForegroundRect;
    private int mForegroundPadding;

    // If we're actively dragging something over this screen, mIsDragOverlapping is true
    private boolean mIsDragOverlapping = false;
    private final Point mDragCenter = new Point();

    // These arrays are used to implement the drag visualization on x-large screens.
    // They are used as circular arrays, indexed by mDragOutlineCurrent.
    private Point[] mDragOutlines = new Point[4];
    private float[] mDragOutlineAlphas = new float[mDragOutlines.length];
//    private InterruptibleInOutAnimator[] mDragOutlineAnims =
//            new InterruptibleInOutAnimator[mDragOutlines.length];

    // Used as an index into the above 3 arrays; indicates which is the most current value.
    private int mDragOutlineCurrent = 0;
    private final Paint mDragOutlinePaint = new Paint();

    private View3D mPressedOrFocusedIcon;

    private Drawable mCrosshairsDrawable = null;
//    private InterruptibleInOutAnimator mCrosshairsAnimator = null;
    private float mCrosshairsVisibility = 0.0f;

    // When a drag operation is in progress, holds the nearest cell to the touch point
    private final int[] mDragCell = new int[2];

    private boolean mDragging = false;

//    private TimeInterpolator mEaseOutInterpolator;
    private CellLayoutChildren mChildren;
    
    /* need repair lq_grp start*/
    int mPaddingLeft = 0;
    int mPaddingRight = 48;
    int mPaddingTop = 0;
    int mPaddingBottom = 48;
    
    public static TextureRegion tr_panelFrame_bottom = null;
    public static TextureRegion tr_panelFrame_top = null;
    public static TextureRegion tr_panelFrame_left = null;
    public static TextureRegion tr_panelFrame_right = null;
    public static TextureRegion tr_panelHighlight = null;
    public static final String TEXT_REMINDER = "轻敲并按住屏幕以添加项目";
    public static TextureRegion tr_textReminder = null;
    public static boolean canShowReminder = false;
    public static boolean keyPadInvoked=false;
    public static int nextPageIndex =0;
    public  static  NinePatch iconFocus =null;// = new NinePatch(R3D.findRegion("icon_focus"),20,20,20,20);
    public int cursorX = 0;
    public int cursorY = 0;//zqh
 	private float locationX=0;
 	private float locationY=0;
 	private float iconWidth =0;
 	private float iconHeight=0;
 	public static boolean firstlyCome=true;
 	public static  boolean hideFocus =true;
 	public static boolean hasItem=true;//are there some items blow current row ?
 	public static boolean iconExist=false;
 	private boolean hasInited=false;
 	public static boolean touchEvent=false;
 	public static int origin=0;
 	public static String direction ="left";
 	public final String LEFT_DIR="left";
 	public final String RIGHT_DIR="right";
 	public float gs4_alpha = 0;
 	
    int getPaddingLeft() {
    	return 0;
    }
    
    int getPaddingBottom() {
    	return (int) (R3D.Workspace_celllayout_bottompadding);
    }
    
    int getPaddingTop() {
    	return (int) (R3D.Workspace_celllayout_toppadding);
    }
    
    int mScrollX = 0;
    int mScrollY = 0;
    /* need repair lq_grp end*/
    
	public static final int TYPE_CLOCK = 0;
	public static final int TYPE_ICON = 1;
	public static final int TYPE_FOLDER = 2;
//	private View3D tmpView = null;
	private ArrayList<View3D> clockList = new ArrayList<View3D>();
//	private ArrayList<View3D> memoList = new ArrayList<View3D>();
	private int clingState = ClingManager.CLING_STATE_WAIT;
	private ArrayList<View3D> iconList = new ArrayList<View3D>();
	private ArrayList<View3D> folderList = new ArrayList<View3D>();
	 
	public boolean checkIconNums(int addNums){
		if(iconList.size() + addNums > 100){
//			SendMsgToAndroid.sendOurToastMsg("本页已达最大图标上�?00，无法继续添�?);
			return false;
		}
		else 
			return true;
	}
	
	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
		return super.onClick(x, y);
	}

	boolean addToList(View3D view){
		boolean ret = true;
		
		ArrayList<View3D> temp = new ArrayList<View3D>();
		cellDropType = CellgetViewType(view);
		temp.add(view);
		
//		Log.v("test", "addToList !!!!");
		
//		if(view instanceof Icon3D){
//			if(iconList.size() < 100){
//				iconList.add(view);
//			}
//			else{
//				ret = false;
//				SendMsgToAndroid.sendOurToastMsg("本页已达最大图标上�?00，无法继续添�?);
//			}		
//			
//		}
//		else if(view instanceof Widget3D){
//			clockList.add(view);
////			if(clockList.size() < 1){
////				clockList.add(view);
////			}
////			else{
////				ret = false;
////				SendMsgToAndroid.sendOurToastMsg("max clock");
////			}
////				
//			
//		}
//		else if(view instanceof FolderIcon3D){
//			if(folderList.size() < 20){
//				folderList.add(view);
//			}
//			else{
//				ret = false;
//				SendMsgToAndroid.sendOurToastMsg("本页已达文件夹最大上�?0，无法继续添�?);
//			}
//				
//			
//		}
		
		return addView(temp, (int)(view.x + view.width / 2), (int)(view.y + view.height / 2));
//		return ret;	
	}
	private void removeList(View3D view){
		if(view instanceof Icon3D) iconList.remove(view);
		else if(view instanceof Widget3D) clockList.remove(view);
		else if(view instanceof FolderIcon3D) folderList.remove(view);
	}
	
	public ArrayList<View3D> getList(int type){
		switch (type) {
		case TYPE_CLOCK:
			return clockList;
		case TYPE_ICON:
			return iconList;
		case TYPE_FOLDER:
			return folderList;
		default:
			Log.e("getlist", "Error type : " + type);
			return null;
		}
	}
	
	 /**
     * Drop a child at the specified position
     *
     * @param child The child that is being dropped
     * @param targetXY Destination area to move to
     */
    void onDropChild(View3D child, int[] targetXY) {
        if (child != null) {
           
        	child.x = targetXY[0];
        	child.y = targetXY[1];
            if(child.x < 0)child.x = 0;
            if(child.y < 0)child.y = 0;
            
//            if(child.x + child.width > mDisplayMetrics.widthPixels) lp.x = mDisplayMetrics.widthPixels - lp.width;
//            if(child.y + child.height > mDisplayMetrics.heightPixels) lp.y = mDisplayMetrics.heightPixels - lp.height;
            child.show();
            child.isDragging = false;
         
        }
    }
	
    @Override
	public boolean multiTouch2(Vector2 initialFirstPointer,
			Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer)
    {
    	
    	return super.multiTouch2(initialFirstPointer, initialSecondPointer,
				firstPointer, secondPointer);
    }
    @Override
	public boolean onTouchUp(float x, float y, int pointer) {
    	if(DefaultLayout.keypad_event_of_focus){
    		//this.resetInfo();
    	}
    
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean onTouchDown(float x, float y,int pointer) {
		// TODO Auto-generated method stub
//		View3D view = null;
		Log.v("touch", "onTouchDown");
		if(DefaultLayout.keypad_event_of_focus){
			resetCurrFocus();
			switchOff();//zqh
			CellLayout3D.keyPadInvoked=false;
		}
		onDropLeave();/*sometimes it is necessary*/
//		if((view = hit(x,y)) instanceof Icon3D 	)
//		{
//			if (((Icon3D)view).getItemInfo().container==LauncherSettings.Favorites.CONTAINER_DESKTOP)
//			{
//				tmpView = view.clone();
//				view.toAbsoluteCoords(point);
//				
//				tmpView.x = point.x;
//				tmpView.y = point.y;
//				tmpView.startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.3f, 0, 0, 0);
//				tmpView.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.3f, 2f, 2f, 0).setCallback(this);
//			}
//		}
		
		
		return super.onTouchDown(x, y,pointer);
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
//		if(tmpView != null && source == tmpView.getTween() && type == TweenCallback.COMPLETE ){
//			tmpView.remove();
//			tmpView = null;
//		}
		if (FolderSmallTween != null && type == TweenCallback.COMPLETE) {
			View3D view3D = (View3D) source.getUserData();
			// Log.e("testFolder", "why FolderSmallTween onEvent me");
			if (view3D instanceof FolderIcon3D) {
				FolderIcon3D tempFolder = (FolderIcon3D) view3D;
				if (tempFolder.mInfo.contents.size() == 0) {
					view3D.setVisible(false);
				}
			}

			FolderSmallTween = null;
		}
		if (FolderLargeTween != null && type == TweenCallback.COMPLETE) {
			FolderLargeTween = null;
		}
	}
	public float hl_x = 0.0f;
	public float hl_w = 0.0f;
	public float hl_u0 = 0.0f;
	public float hl_u1 = 1.0f;
	public float alpha = 1.0f;
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		
		
	    if (debug && debugTexture != null && parent != null)
			batch.draw(debugTexture, x, y, originX, originY, width == 0 ? 200 : width, height == 0 ? 200 : height, scaleX, scaleY,
				rotation, 0, 0, debugTexture.getWidth(), debugTexture.getHeight(), false, false);
//	    if((iconExist)&&(!firstlyCome)&&!HotSeat3D.isOccupy&&DefaultLayout.keypad_event_of_focus)
//		{//zqh 
//			iconFocus.draw(batch, locationX- (R3D.Workspace_cell_each_width-R3D.workspace_cell_width)/2, locationY+(R3D.workspace_cell_height-R3D.Workspace_cell_each_height)/2, R3D.Workspace_cell_each_width+2, R3D.Workspace_cell_each_height);
//		}
		if (transform) applyTransform(batch);
		View3D parent = this.getParent();
		float temp_xScale = 0;
		float temp_alpha = 0;
		if(parent instanceof Workspace3D)
		{
			temp_xScale = ((Workspace3D)this.getParent()).getX();
			// added by zhenNan.ye disable draw panelFrame when standard
			if (APageEase.is_standard) {
				temp_xScale = 0;
			}
			temp_alpha = Workspace3D.is_longKick?((Workspace3D)this.getParent()).getUser():0;//(1-((Workspace3D)this.getParent()).getScaleX())*5;
			if(temp_alpha<0)temp_alpha=0;
			else if(temp_alpha>1)temp_alpha=1;
			//Log.v("jbc","dduser="+((Workspace3D)this.getParent()).getUser());			
			//Log.v("jbc","ddxScale="+temp_xScale+" alpha="+temp_alpha);
		}
		float panelFrame_y = getPaddingBottom();
		float panelFrame_h = this.height - getPaddingTop() - getPaddingBottom();
		float panelPadding = 6;
		if (!DefaultLayout.desktop_hide_frame 
				|| DefaultLayout.desktop_hide_frame && Workspace3D.is_longKick) {
	    if (tr_panelHighlight!=null)
	    {
	    	if (Root3D.scroll_indicator)
	    		batch.setColor(1.0f, 1.0f, 1.0f, gs4_alpha);
	    	else
	    		batch.setColor(1.0f, 1.0f, 1.0f, (1-2*Math.abs(Math.abs(temp_xScale)-0.5f))+temp_alpha);
	    	tr_panelHighlight.setRegion(hl_u0, 0.0f, hl_u1, 1.0f);
	    	//Log.v("jbc","ddx="+hl_x+" w="+hl_w);
		    	batch.draw(tr_panelHighlight, hl_x, panelFrame_y + panelFrame_h*4/5 + panelPadding,hl_w,panelFrame_h/5);
	    }
	    
	    if (tr_panelFrame_top!=null)
		{
	    	if (Root3D.scroll_indicator)
	    		batch.setColor(1.0f, 1.0f, 1.0f, gs4_alpha);
	    	else
	    		batch.setColor(1.0f, 1.0f, 1.0f, (1-2*Math.abs(Math.abs(temp_xScale)-0.5f))+temp_alpha);
//			float height = (this.height-getPaddingBottom()+5)*
//					((float)tr_panelFrame_top.getRegionHeight()/(float)(tr_panelFrame_top.getRegionHeight()+tr_panelFrame_bottom.getRegionHeight()+tr_panelFrame_left.getRegionHeight()));
				batch.draw(tr_panelFrame_top,0, panelFrame_y+panelFrame_h-tr_panelFrame_top.getRegionHeight()+panelPadding,this.width,tr_panelFrame_top.getRegionHeight());
		}
		if (tr_panelFrame_bottom!=null)
		{
			if (Root3D.scroll_indicator)
	    		batch.setColor(1.0f, 1.0f, 1.0f, gs4_alpha);
	    	else
	    		batch.setColor(1.0f, 1.0f, 1.0f, (1-2*Math.abs(Math.abs(temp_xScale)-0.5f))+temp_alpha);
//			bottomHeight = (this.height-getPaddingBottom()+5)*
//					((float)tr_panelFrame_bottom.getRegionHeight()/(float)(tr_panelFrame_top.getRegionHeight()+tr_panelFrame_bottom.getRegionHeight()+tr_panelFrame_left.getRegionHeight()));
				batch.draw(tr_panelFrame_bottom,0, panelFrame_y-panelPadding,this.width,tr_panelFrame_bottom.getRegionHeight());
		}
		if (tr_panelFrame_left!=null)
		{
			if (Root3D.scroll_indicator)
	    		batch.setColor(1.0f, 1.0f, 1.0f, gs4_alpha);
	    	else
	    		batch.setColor(1.0f, 1.0f, 1.0f, (1-2*Math.abs(Math.abs(temp_xScale)-0.5f))+temp_alpha);
//			float width = this.width*((float)tr_panelFrame_left.getRegionWidth()/(float)(tr_panelFrame_top.getRegionWidth()));
				batch.draw(tr_panelFrame_left,0, panelFrame_y+tr_panelFrame_bottom.getRegionHeight()-panelPadding,tr_panelFrame_left.getRegionWidth(),
						panelFrame_h-tr_panelFrame_top.getRegionHeight()-tr_panelFrame_bottom.getRegionHeight()+panelPadding*2);
		}
		if (tr_panelFrame_right!=null)
		{
			if (Root3D.scroll_indicator)
	    		batch.setColor(1.0f, 1.0f, 1.0f, gs4_alpha);
	    	else
	    		batch.setColor(1.0f, 1.0f, 1.0f, (1-2*Math.abs(Math.abs(temp_xScale)-0.5f))+temp_alpha);
				batch.draw(tr_panelFrame_right,this.width-tr_panelFrame_right.getRegionWidth(), panelFrame_y+tr_panelFrame_bottom.getRegionHeight()-panelPadding,tr_panelFrame_right.getRegionWidth(),
						panelFrame_h-tr_panelFrame_top.getRegionHeight()-tr_panelFrame_bottom.getRegionHeight()+panelPadding*2);
		}
		}
		if (DefaultLayout.empty_page_add_reminder) {
			if (canShowReminder && children.size()==0 && !Workspace3D.is_longKick) {
				if (Root3D.scroll_indicator)
		    		batch.setColor(1.0f, 1.0f, 1.0f, gs4_alpha);
		    	else
		    		batch.setColor(1.0f, 1.0f, 1.0f, color.a*parentAlpha);
				batch.draw(tr_textReminder,(this.width-tr_textReminder.getRegionWidth())/2, panelFrame_y+(panelFrame_h-tr_textReminder.getRegionHeight())/2,
						tr_textReminder.getRegionWidth(), tr_textReminder.getRegionHeight());
			}
		}
		drawChildren(batch, parentAlpha);
		if (transform) resetTransform(batch);
//	    super.draw(batch, parentAlpha);
//		if(tmpView != null){
//			
//			tmpView.applyTransformChild(batch);
//			tmpView.draw(batch, parentAlpha);
//			tmpView.resetTransformChild(batch);
//		}
//		if(this.getChildCount()>0 && clingState==ClingManager.CLING_STATE_WAIT){
//			clingState = ClingManager.getInstance().fireCircleCling(this);
//		}
	}

//	@Override
//	public void addView(View3D actor) {
//		// TODO Auto-generated method stub
//		addToList(actor);
//	}
//
	@Override
	public void addViewAt(int index, View3D actor) {
		// TODO Auto-generated method stub
		super.addViewAt(index, actor);
		
		ItemInfo info = ((IconBase3D)actor).getItemInfo();
		
		Log.v("test", "celllayout <addViewAt>" + "Cell x, y = " + info.cellX +", " + info.cellY);
		
		if (info.cellX != -1 && info.cellY != -1) {
			for (int k = 0; k < info.spanX; k++)
				for (int j = 0; j < info.spanY; j++) {
					mOccupied[info.cellX + k][info.cellY + j] = true;
					cellViewList[info.cellX + k][info.cellY + j] = actor;
				}
		}
	}
//
//	@Override
//	public void addViewBefore(View3D actorBefore, View3D actor) {
//		// TODO Auto-generated method stub
//		addToList(actor);
//	}
//
//	@Override
//	public void addViewAfter(View3D actorAfter, View3D actor) {
//		// TODO Auto-generated method stub
//		addToList(actor);
//	}

	
	
	@Override
	public void removeView(View3D actor) {
		// TODO Auto-generated method stub
		removeFromSuperClass(actor);
		removeList(actor);
		cellFindViewAndRemove(actor);
		Log.v("bind","remove:" + actor);
	}

	@Override
	public void setScaleZ(float f) {
		// TODO Auto-generated method stub
		for(View3D view : clockList)
			view.setScaleZ(f);
	}

	@Override
	public void show() {
		
		super.show();
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}

	@Override
	public void hide() {
		
		super.hide();
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}

//	@Override
//	public boolean visible() {
//		return this.isVisible();
//	}
//
//	@Override
//	public int getClingPriority() {
//		// TODO Auto-generated method stub
//		return ClingManager.CIRCLE_CLING;
//	}
//
//
//	@Override
//	public void dismissCling() {
//		clingState = ClingManager.CLING_STATE_DISMISSED;
//	}
//
//	@Override
//	public void setPriority(int priority) {
//		// TODO Auto-generated method stub
//		
//	}
	
	/* Celllayout3d */
	
	int mCountX;
    int mCountY;

    public CellLayout3D(String name) {
        super(name);
		transform = true;
		iconFocus = new NinePatch(R3D.findRegion("icon_focus"),20,20,20,20);
        // A ViewGroup usually does not draw, but CellLayout needs to draw a rectangle to show
        // the user where a dragged item will land when dropped.
//        setWillNotDraw(false);

//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CellLayout2, defStyle, 0);

//        mOriginalCellWidth =
//            mCellWidth = a.getDimensionPixelSize(R.styleable.CellLayout_cellWidth, 10);
//        mOriginalCellHeight =
//            mCellHeight = a.getDimensionPixelSize(R.styleable.CellLayout_cellHeight, 10);
//        mWidthGap = mOriginalWidthGap = a.getDimensionPixelSize(R.styleable.CellLayout_widthGap, 0);
//        mHeightGap = mOriginalHeightGap = a.getDimensionPixelSize(R.styleable.CellLayout_heightGap, 0);
//        mMaxGap = a.getDimensionPixelSize(R.styleable.CellLayout_maxGap, 0);
        mCountX = R3D.Workspace_cellCountX;
        mCountY = R3D.Workspace_cellCountY;
        mOccupied = new boolean[mCountX][mCountY];
        mTmpOccupied = new boolean[mCountX][mCountY];
        mCellWidth = R3D.Workspace_cell_each_width;
        mCellHeight = R3D.Workspace_cell_each_height;
        cellViewList = new View3D[mCountX][mCountY];

  
        if(tr_panelFrame_top == null)
        {
        	Bitmap bm_panelFrame_top = ((BitmapDrawable)iLoongLauncher.getInstance().getResources().getDrawable(RR.drawable.panel_frame_top)).getBitmap();
			
        	tr_panelFrame_top = new TextureRegion(new BitmapTexture(bm_panelFrame_top));
//        	tr_panelFrame = new TextureRegion(new Texture3D(new Pixmap(Gdx.files.internal("theme/desktop_effect/panel_frame.png"))));
        	tr_panelFrame_top.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
        	bm_panelFrame_top.recycle();
        }
        if(tr_panelFrame_bottom == null)
        {
        	Bitmap bm_panelFrame_bottom = ((BitmapDrawable)iLoongLauncher.getInstance().getResources().getDrawable(RR.drawable.panel_frame_bottom)).getBitmap();
			
        	tr_panelFrame_bottom = new TextureRegion(new BitmapTexture(bm_panelFrame_bottom));
//        	tr_panelFrame = new TextureRegion(new Texture3D(new Pixmap(Gdx.files.internal("theme/desktop_effect/panel_frame.png"))));
        	tr_panelFrame_bottom.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
        	bm_panelFrame_bottom.recycle();
        }
        if(tr_panelFrame_left == null)
        {
        	Bitmap bm_panelFrame_left = ((BitmapDrawable)iLoongLauncher.getInstance().getResources().getDrawable(RR.drawable.panel_frame_left)).getBitmap();
			
        	tr_panelFrame_left = new TextureRegion(new BitmapTexture(bm_panelFrame_left));
//        	tr_panelFrame = new TextureRegion(new Texture3D(new Pixmap(Gdx.files.internal("theme/desktop_effect/panel_frame.png"))));
        	tr_panelFrame_left.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
        	bm_panelFrame_left.recycle();
        }
        if(tr_panelFrame_right == null)
        {
        	Bitmap bm_panelFrame_right = ((BitmapDrawable)iLoongLauncher.getInstance().getResources().getDrawable(RR.drawable.panel_frame_right)).getBitmap();
			
        	tr_panelFrame_right = new TextureRegion(new BitmapTexture(bm_panelFrame_right));
//        	tr_panelFrame = new TextureRegion(new Texture3D(new Pixmap(Gdx.files.internal("theme/desktop_effect/panel_frame.png"))));
        	tr_panelFrame_right.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
        	bm_panelFrame_right.recycle();
        }
        if(tr_panelHighlight == null)
        {
        	Bitmap bm_panelHighlight = ((BitmapDrawable)iLoongLauncher.getInstance().getResources().getDrawable(RR.drawable.panel_highlight)).getBitmap();
        	float scale = 1f;
        	int newW = 1,newH = 1;
        	Bitmap tmpFrame = bm_panelHighlight;
			if (bm_panelHighlight.getWidth() > this.width) {
				scale = (float)this.width / (float) bm_panelHighlight.getWidth();
			}
			if (bm_panelHighlight.getHeight() * scale > this.height) {
				scale = (float)this.height / (float)bm_panelHighlight.getHeight() ;
			}
			if (scale != 1f) {
				newW = (int) (scale * bm_panelHighlight.getWidth());
				newH = (int) (scale * bm_panelHighlight.getHeight());
				tmpFrame = Bitmap.createScaledBitmap(bm_panelHighlight, newW, newH, false);
				bm_panelHighlight.recycle();
			}
        	
        	tr_panelHighlight = new TextureRegion(new BitmapTexture(tmpFrame));
//        	tr_panelHighlight = new TextureRegion(new Texture3D(new Pixmap(Gdx.files.internal("theme/desktop_effect/panel_highlight.png"))));
           	tr_panelHighlight.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
           	if(!iLoongLauncher.releaseTexture)tmpFrame.recycle();
        }
        if (DefaultLayout.empty_page_add_reminder) {
	        if (tr_textReminder == null) {
	        	Bitmap bm_textReminder = reminderToBitmap();
	        	tr_textReminder = new TextureRegion(new BitmapTexture(bm_textReminder));
	        	tr_textReminder.getTexture().setFilter(TextureFilter.Linear,TextureFilter.Linear);
	        	bm_textReminder.recycle();
	        }
        }
//        panelFrameView = new NinePatch(panelFrame,1,1,1,1);       
//        panelHightView=new NinePatch(panelHight,1,1,1,1);
       
//
//        a.recycle();
//
////        setAlwaysDrawnWithCacheEnabled(false);
//
//        final Resources res = iLoongLauncher.getInstance().getResources();
//
//        mNormalBackground = res.getDrawable(R.drawable.homescreen_blue_normal_holo);
//        mActiveGlowBackground = res.getDrawable(R.drawable.homescreen_blue_strong_holo);
//
//        mOverScrollLeft = res.getDrawable(R.drawable.overscroll_glow_left);
//        mOverScrollRight = res.getDrawable(R.drawable.overscroll_glow_right);
//        mForegroundPadding =
//                res.getDimensionPixelSize(R.dimen.workspace_overscroll_drawable_padding);

//        mNormalBackground.setFilterBitmap(true);
//        mActiveGlowBackground.setFilterBitmap(true);

        // Initialize the data structures used for the drag visualization.

//        mCrosshairsDrawable = res.getDrawable(R.drawable.gardening_crosshairs);
//        mEaseOutInterpolator = new DecelerateInterpolator(2.5f); // Quint ease out

        // Set up the animation for fading the crosshairs in and out
//        int animDuration = res.getInteger(R.integer.config_crosshairsFadeInTime);
//        mCrosshairsAnimator = new InterruptibleInOutAnimator(animDuration, 0.0f, 1.0f);
//        mCrosshairsAnimator.getAnimator().addUpdateListener(new AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                mCrosshairsVisibility = ((Float) animation.getAnimatedValue()).floatValue();
//                invalidate();
//            }
//        });
//        mCrosshairsAnimator.getAnimator().setInterpolator(mEaseOutInterpolator);

        mDragCell[0] = mDragCell[1] = -1;
        for (int i = 0; i < mDragOutlines.length; i++) {
            mDragOutlines[i] = new Point(-1, -1);
        }

        // When dragging things around the home screens, we show a green outline of
        // where the item will land. The outlines gradually fade out, leaving a trail
        // behind the drag path.
        // Set up all the animations that are used to implement this fading.
//        final int duration = res.getInteger(R.integer.config_dragOutlineFadeTime);
        final float fromAlphaValue = 0;
//        final float toAlphaValue = (float)res.getInteger(R.integer.config_dragOutlineMaxAlpha);

        Arrays.fill(mDragOutlineAlphas, fromAlphaValue);

//        for (int i = 0; i < mDragOutlineAnims.length; i++) {
//            final InterruptibleInOutAnimator anim =
//                new InterruptibleInOutAnimator(duration, fromAlphaValue, toAlphaValue);
//            anim.getAnimator().setInterpolator(mEaseOutInterpolator);
//            final int thisIndex = i;
//            anim.getAnimator().addUpdateListener(new AnimatorUpdateListener() {
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    final Bitmap outline = (Bitmap)anim.getTag();
//
//                    // If an animation is started and then stopped very quickly, we can still
//                    // get spurious updates we've cleared the tag. Guard against this.
//                    if (outline == null) {
//                        if (false) {
//                            Object val = animation.getAnimatedValue();
//                            Log.d(TAG, "anim " + thisIndex + " update: " + val +
//                                     ", isStopped " + anim.isStopped());
//                        }
//                        // Try to prevent it from continuing to run
//                        animation.cancel();
//                    } else {
//                        mDragOutlineAlphas[thisIndex] = (Float) animation.getAnimatedValue();
//                        final int left = mDragOutlines[thisIndex].x;
//                        final int top = mDragOutlines[thisIndex].y;
//                        CellLayout2.this.invalidate(left, top,
//                                left + outline.getWidth(), top + outline.getHeight());
//                    }
//                }
//            });
//            // The animation holds a reference to the drag outline bitmap as long is it's
//            // running. This way the bitmap can be GCed when the animations are complete.
//            anim.getAnimator().addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    if ((Float) ((ValueAnimator) animation).getAnimatedValue() == 0f) {
//                        anim.setTag(null);
//                    }
//                }
//            });
//            mDragOutlineAnims[i] = anim;
//        }

        mBackgroundRect = new Rect();
        mForegroundRect = new Rect();

        mChildren = new CellLayoutChildren();
//        mChildren.setCellDimensions(mCellWidth, mCellHeight, mWidthGap, mHeightGap);
//        addView(mChildren);
    }

    public Bitmap reminderToBitmap() {
    	Paint paint = new Paint();			
		paint.setAntiAlias(true);
		paint.setTextSize(R3D.reminder_font);
		if (DefaultLayout.title_style_bold)
			paint.setFakeBoldText(true);
		FontMetrics fontMetrics = paint.getFontMetrics();
		float singleLineHeight = (float) Math.ceil(fontMetrics.bottom
				- fontMetrics.top);	
		float textWidth = paint.measureText(TEXT_REMINDER);
		float text_x = 0;//x+(width-textWidth)/2;
		float text_y = singleLineHeight;//height/2;
		
		Bitmap bmp = Bitmap.createBitmap(Math.round(textWidth+4), Math.round(singleLineHeight+4),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);

		if (!DefaultLayout.hide_title_bg_shadow) {
			int text_color = DefaultLayout.title_outline_color;					
			paint.setColor(text_color);
			int shadow_color = DefaultLayout.title_shadow_color;
			for (int j=1; j<=DefaultLayout.title_outline_shadow_size; j++) {
				paint.setShadowLayer(1f, -j, 0f, shadow_color);
				canvas.drawText(TEXT_REMINDER, text_x - j, text_y, paint);
				paint.setShadowLayer(1f, 0f, -j, shadow_color);
				canvas.drawText(TEXT_REMINDER, text_x, text_y - j, paint);
				paint.setShadowLayer(1f, j, 0f, shadow_color);
				canvas.drawText(TEXT_REMINDER, text_x + j, text_y, paint);
				paint.setShadowLayer(1f, 0f, j, shadow_color);
				canvas.drawText(TEXT_REMINDER, text_x, text_y + j, paint);
			}
//			paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
//			canvas.drawText(TEXT_REMINDER, text_x, text_y, paint);				
		}

		paint.clearShadowLayer();
		paint.setColor(android.graphics.Color.WHITE);
		canvas.drawText(TEXT_REMINDER, text_x, text_y, paint);
		
		return bmp;
    }
    static int widthInPortrait(Resources r, int numCells) {
        // We use this method from Workspace to figure out how many rows/columns Launcher should
        // have. We ignore the left/right padding on CellLayout because it turns out in our design
        // the padding extends outside the visible screen size, but it looked fine anyway.
        int cellWidth = r.getDimensionPixelSize(RR.dimen.workspace_cell_width);
        int minGap = Math.min(r.getDimensionPixelSize(RR.dimen.workspace_width_gap),
                r.getDimensionPixelSize(RR.dimen.workspace_height_gap));

        return  minGap * (numCells - 1) + cellWidth * numCells;
    }

    static int heightInLandscape(Resources r, int numCells) {
        // We use this method from Workspace to figure out how many rows/columns Launcher should
        // have. We ignore the left/right padding on CellLayout because it turns out in our design
        // the padding extends outside the visible screen size, but it looked fine anyway.
        int cellHeight = R3D.Workspace_cell_each_height;//r.getDimensionPixelSize(RR.dimen.workspace_cell_height);
        int minGap = Math.min(r.getDimensionPixelSize(RR.dimen.workspace_width_gap),
                r.getDimensionPixelSize(RR.dimen.workspace_height_gap));

        return minGap * (numCells - 1) + cellHeight * numCells;
    }

    public void enableHardwareLayers() {
        mChildren.enableHardwareLayers();
    }

    public void setGridSize(int x, int y) {
        mCountX = x;
        mCountY = y;
        mOccupied = new boolean[mCountX][mCountY];
//        requestLayout();
    }

    private void invalidateBubbleTextView(View3D icon) {
//        final int padding = icon.getPressedOrFocusedBackgroundPadding();
//        invalidate(icon.x + getPaddingLeft() - padding,
//                icon.y + getPaddingTop() - padding,
//                icon.x + icon.width + getPaddingLeft() + padding,
//                icon.y + icon.height + getPaddingTop() + padding);
    }

    void setOverScrollAmount(float r, boolean left) {
//        if (left && mOverScrollForegroundDrawable != mOverScrollLeft) {
//            mOverScrollForegroundDrawable = mOverScrollLeft;
//        } else if (!left && mOverScrollForegroundDrawable != mOverScrollRight) {
//            mOverScrollForegroundDrawable = mOverScrollRight;
//        }
//
//        mForegroundAlpha = (int) Math.round((r * 255));
//        mOverScrollForegroundDrawable.setAlpha(mForegroundAlpha);
//        invalidate();
    }

    void setPressedOrFocusedIcon(View3D icon) {
        // We draw the pressed or focused BubbleTextView's background in CellLayout because it
        // requires an expanded clip rect (due to the glow's blur radius)
    	View3D oldIcon = mPressedOrFocusedIcon;
        mPressedOrFocusedIcon = icon;
        if (oldIcon != null) {
            invalidateBubbleTextView(oldIcon);
        }
        if (mPressedOrFocusedIcon != null) {
            invalidateBubbleTextView(mPressedOrFocusedIcon);
        }
    }

    public CellLayoutChildren getChildrenLayout() {
        if (getChildCount() > 0) {
            return (CellLayoutChildren) getChildAt(0);
        }
        return null;
    }

    void setIsDragOverlapping(boolean isDragOverlapping) {
        if (mIsDragOverlapping != isDragOverlapping) {
            mIsDragOverlapping = isDragOverlapping;
//            invalidate();
        }
    }

    boolean getIsDragOverlapping() {
        return mIsDragOverlapping;
    }

    protected void setOverscrollTransformsDirty(boolean dirty) {
        mScrollingTransformsDirty = dirty;
    }

    protected void resetOverscrollTransforms() {
        if (mScrollingTransformsDirty) {
            setOverscrollTransformsDirty(false);
//            setTranslationX(0);
            setRotationY(0);
            // It doesn't matter if we pass true or false here, the important thing is that we
            // pass 0, which results in the overscroll drawable not being drawn any more.
            setOverScrollAmount(0, false);
//            setPivotX(getMeasuredWidth() / 2);
//            setPivotY(getMeasuredHeight() / 2);
        }
    }

//    public void showFolderAccept(FolderRingAnimator fra) {
//        mFolderOuterRings.add(fra);
//    }
//
//    public void hideFolderAccept(FolderRingAnimator fra) {
//        if (mFolderOuterRings.contains(fra)) {
//            mFolderOuterRings.remove(fra);
//        }
//        invalidate();
//    }

    public void setFolderLeaveBehindCell(int x, int y) {
        mFolderLeaveBehindCell[0] = x;
        mFolderLeaveBehindCell[1] = y;
//        invalidate();
    }

    public void clearFolderLeaveBehind() {
        mFolderLeaveBehindCell[0] = -1;
        mFolderLeaveBehindCell[1] = -1;
//        invalidate();
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void cancelLongPress() {
//        super.cancelLongPress();

        // Cancel long press for all children
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View3D child = getChildAt(i);
//            child.cancelLongPress();
        }
    }

    public void setOnInterceptTouchListener(View.OnTouchListener listener) {
//        mInterceptTouchListener = listener;
    }

    int getCountX() {
        return mCountX;
    }

    int getCountY() {
        return mCountY;
    }

    public boolean addViewToCellLayout(
            View3D child, int index, int childId, LayoutParams params, boolean markCells) {
        final LayoutParams lp = params;

        // Generate an id for each view, this assumes we have at most 256x256 cells
        // per workspace screen
        if (lp.cellX >= 0 && lp.cellX <= mCountX - 1 && lp.cellY >= 0 && lp.cellY <= mCountY - 1) {
            // If the horizontal or vertical span is set to -1, it is taken to
            // mean that it spans the extent of the CellLayout
            if (lp.cellHSpan < 0) lp.cellHSpan = mCountX;
            if (lp.cellVSpan < 0) lp.cellVSpan = mCountY;

//            child.setId(childId);

//            mChildren.addView(child, index, lp);

            if (markCells) markCellsAsOccupiedForView(child);

            return true;
        }
        return false;
    }

    @Override
    public void removeAllViews() {
        clearOccupiedCells();
        super.removeAllViews();
    }

    public void removeAllViewsInLayout() {
        if (mChildren.getChildCount() > 0) {
            clearOccupiedCells();
            mChildren.removeAllViewsInLayout();
        }
    }

    public void removeViewWithoutMarkingCells(View3D view) {
        mChildren.removeView(view);
    }

    public void removeViewSub(View3D view) {
        markCellsAsUnoccupiedForView(view);
        mChildren.removeView(view);
    }

    public void removeViewAt(int index) {
        markCellsAsUnoccupiedForView(mChildren.getChildAt(index));
        mChildren.removeViewAt(index);
    }

    public void removeViewInLayout(View3D view) {
        markCellsAsUnoccupiedForView(view);
        mChildren.removeViewInLayout(view);
    }

    public void removeViews(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(mChildren.getChildAt(i));
        }
        mChildren.removeViews(start, count);
    }

    public void removeViewsInLayout(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(mChildren.getChildAt(i));
        }
        mChildren.removeViewsInLayout(start, count);
    }

//    public void drawChildren(Canvas canvas) {
//        mChildren.draw(canvas);
//    }

    void buildChildrenLayer() {
//        mChildren.buildLayer();
    }

    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        mCellInfo.screen = ((ViewGroup) getParent()).indexOfChild(this);
    }

    public void setTagToCellInfoForPoint(int touchX, int touchY) {
        final CellInfo cellInfo = mCellInfo;
        final Rect frame = mRect;
        final int x = touchX + mScrollX;
        final int y = touchY + mScrollY;
        final int count = mChildren.getChildCount();

        boolean found = false;
        for (int i = count - 1; i >= 0; i--) {
            final View3D child = mChildren.getChildAt(i);
//            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

//            if ((child.getVisibility() == VISIBLE || child.getAnimation() != null) &&
//                    lp.isLockedToGrid) {
//                child.getHitRect(frame);
//
//                // The child hit rect is relative to the CellLayoutChildren parent, so we need to
//                // offset that by this CellLayout's padding to test an (x,y) point that is relative
//                // to this view.
//                frame.offset(mPaddingLeft, mPaddingTop);
//
//                if (frame.contains(x, y)) {
//                    cellInfo.cell = child;
//                    cellInfo.cellX = lp.cellX;
//                    cellInfo.cellY = lp.cellY;
//                    cellInfo.spanX = lp.cellHSpan;
//                    cellInfo.spanY = lp.cellVSpan;
//                    found = true;
//                    break;
//                }
//            }
        }

        mLastDownOnOccupiedCell = found;

        if (!found) {
            final int cellXY[] = mTmpXY;
            pointToCellExact(x, y, cellXY);

            cellInfo.cell = null;
            cellInfo.cellX = cellXY[0];
            cellInfo.cellY = cellXY[1];
            cellInfo.spanX = 1;
            cellInfo.spanY = 1;
        }
        setTag(cellInfo);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // First we clear the tag to ensure that on every touch down we start with a fresh slate,
        // even in the case where we return early. Not clearing here was causing bugs whereby on
        // long-press we'd end up picking up an item from a previous drag operation.
        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            clearTagCellInfo();
        }

//        if (mInterceptTouchListener != null && mInterceptTouchListener.onTouch(this, ev)) {
//            return true;
//        }

        if (action == MotionEvent.ACTION_DOWN) {
            setTagToCellInfoForPoint((int) ev.getX(), (int) ev.getY());
        }
        return false;
    }

    private void clearTagCellInfo() {
        final CellInfo cellInfo = mCellInfo;
        cellInfo.cell = null;
        cellInfo.cellX = -1;
        cellInfo.cellY = -1;
        cellInfo.spanX = 0;
        cellInfo.spanY = 0;
        setTag(cellInfo);
    }

//    public CellInfo getTag() {
//        return (CellInfo) super.getTag();
//    }

    /**
     * Given a point, return the cell that strictly encloses that point
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @param result Array of 2 ints to hold the x and y coordinate of the cell
     */
    void pointToCellExact(int x, int y, int[] result) {
        final int hStartPadding = getPaddingLeft();
        final int vStartPadding = getPaddingBottom();

        result[0] = (x - hStartPadding) / (mCellWidth + mWidthGap);
        result[1] = (y - vStartPadding) / (mCellHeight + mHeightGap);

        final int xAxis = mCountX;
        final int yAxis = mCountY;

        if (result[0] < 0) result[0] = 0;
        if (result[0] >= xAxis) result[0] = xAxis - 1;
        if (result[1] < 0) result[1] = 0;
        if (result[1] >= yAxis) result[1] = yAxis - 1;
    }

    /**
     * Given a point, return the cell that most closely encloses that point
     * @param x X coordinate of the point
     * @param y Y coordinate of the point
     * @param result Array of 2 ints to hold the x and y coordinate of the cell
     */
    void pointToCellRounded(int x, int y, int[] result) {
        pointToCellExact(x + (mCellWidth / 2), y + (mCellHeight / 2), result);
    }

    /**
     * Given a cell coordinate, return the point that represents the upper left corner of that cell
     *
     * @param cellX X coordinate of the cell
     * @param cellY Y coordinate of the cell
     *
     * @param result Array of 2 ints to hold the x and y coordinate of the point
     */
    void cellToPoint(int cellX, int cellY, int[] result) {
        final int hStartPadding = getPaddingLeft();
        final int vStartPadding = getPaddingBottom();

        result[0] = hStartPadding + cellX * (mCellWidth + mWidthGap);
        result[1] = vStartPadding + cellY * (mCellHeight + mHeightGap);
//        result[0] = vStartPadding + cellX * (mCellWidth + mWidthGap);
//        result[1] = hStartPadding + cellY * (mCellHeight + mHeightGap);
        //Log.d("launcher", "x,y="+result[0]+","+result[1]);
    }
    
    void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }
    
    void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
        final int hStartPadding = getPaddingLeft();
        final int vStartPadding = getPaddingBottom();
        result[0] = hStartPadding + cellX * (mCellWidth + mWidthGap) +
                (spanX * mCellWidth + (spanX - 1) * mWidthGap) / 2;
        result[1] = vStartPadding + cellY * (mCellHeight + mHeightGap) +
                (spanY * mCellHeight + (spanY - 1) * mHeightGap) / 2;
    }
    
    void regionToRect(int cellX, int cellY, int spanX, int spanY, Rect result) {
        final int hStartPadding = getPaddingLeft();
        final int vStartPadding = getPaddingBottom();
        final int left = hStartPadding + cellX * (mCellWidth + mWidthGap);
        final int top = vStartPadding + cellY * (mCellHeight + mHeightGap);
        result.set(left, top, left + (spanX * mCellWidth + (spanX - 1) * mWidthGap),
                top + (spanY * mCellHeight + (spanY - 1) * mHeightGap));
    }
    
    public float getDistanceFromCell(float x, float y, int[] cell) {
        cellToCenterPoint(cell[0], cell[1], mTmpPoint);
        float distance = (float) Math.sqrt( Math.pow(x - mTmpPoint[0], 2) +
                Math.pow(y - mTmpPoint[1], 2));
        return distance;
    }

    int getCellWidth() {
        return mCellWidth;
    }

    int getCellHeight() {
        return mCellHeight;
    }

    int getWidthGap() {
        return mWidthGap;
    }

    int getHeightGap() {
        return mHeightGap;
    }

    Rect getContentRect(Rect r) {
        if (r == null) {
            r = new Rect();
        }
        int left = getPaddingLeft();
        int top = getPaddingBottom();
        int right = (int) (left + getWidth() - mPaddingLeft - mPaddingRight);
        int bottom = (int) (top + getHeight() - mPaddingTop - mPaddingBottom);
        r.set(left, top, right, bottom);
        return r;
    }

    public View getChildAt(int x, int y) {
        return mChildren.getChildAt(x, y);
    }

    /**
     * Estimate where the top left cell of the dragged item will land if it is dropped.
     *
     * @param originX The X value of the top left corner of the item
     * @param originY The Y value of the top left corner of the item
     * @param spanX The number of horizontal cells that the item spans
     * @param spanY The number of vertical cells that the item spans
     * @param result The estimated drop cell X and Y.
     */
    void estimateDropCell(int originX, int originY, int spanX, int spanY, int[] result) {
        final int countX = mCountX;
        final int countY = mCountY;

        // pointToCellRounded takes the top left of a cell but will pad that with
        // cellWidth/2 and cellHeight/2 when finding the matching cell
        pointToCellRounded(originX, originY, result);

        // If the item isn't fully on this screen, snap to the edges
        int rightOverhang = result[0] + spanX - countX;
        if (rightOverhang > 0) {
            result[0] -= rightOverhang; // Snap to right
        }
        result[0] = Math.max(0, result[0]); // Snap to left
        int bottomOverhang = result[1] + spanY - countY;
        if (bottomOverhang > 0) {
            result[1] -= bottomOverhang; // Snap to bottom
        }
        result[1] = Math.max(0, result[1]); // Snap to top
    }

    void visualizeDropLocation(View v, Bitmap dragOutline, int originX, int originY, int cellX,
            int cellY, int spanX, int spanY, boolean resize, Point dragOffset, Rect dragRegion) {
        final int oldDragCellX = mDragCell[0];
        final int oldDragCellY = mDragCell[1];

        if (v != null && dragOffset == null) {
            mDragCenter.set(originX + (v.getWidth() / 2), originY + (v.getHeight() / 2));
        } else {
            mDragCenter.set(originX, originY);
        }

        if (dragOutline == null && v == null) {
            return;
        }

        if (cellX != oldDragCellX || cellY != oldDragCellY) {
            mDragCell[0] = cellX;
            mDragCell[1] = cellY;
            // Find the top left corner of the rect the object will occupy
            final int[] topLeft = mTmpPoint;
            cellToPoint(cellX, cellY, topLeft);

            int left = topLeft[0];
            int top = topLeft[1];

            if (v != null && dragOffset == null) {
                // When drawing the drag outline, it did not account for margin offsets
                // added by the view's parent.
                MarginLayoutParams lp = (MarginLayoutParams) v.getLayoutParams();
                left += lp.leftMargin;
                top += lp.topMargin;

                // Offsets due to the size difference between the View and the dragOutline.
                // There is a size difference to account for the outer blur, which may lie
                // outside the bounds of the view.
                top += (v.getHeight() - dragOutline.getHeight()) / 2;
                // We center about the x axis
                left += ((mCellWidth * spanX) + ((spanX - 1) * mWidthGap)
                        - dragOutline.getWidth()) / 2;
            } else {
                if (dragOffset != null && dragRegion != null) {
                    // Center the drag region *horizontally* in the cell and apply a drag
                    // outline offset
                    left += dragOffset.x + ((mCellWidth * spanX) + ((spanX - 1) * mWidthGap)
                             - dragRegion.width()) / 2;
                    top += dragOffset.y;
                } else {
                    // Center the drag outline in the cell
                    left += ((mCellWidth * spanX) + ((spanX - 1) * mWidthGap)
                            - dragOutline.getWidth()) / 2;
                    top += ((mCellHeight * spanY) + ((spanY - 1) * mHeightGap)
                            - dragOutline.getHeight()) / 2;
                }
            }
//            final int oldIndex = mDragOutlineCurrent;
//            mDragOutlineAnims[oldIndex].animateOut();
//            mDragOutlineCurrent = (oldIndex + 1) % mDragOutlines.length;
//            Rect r = mDragOutlines[mDragOutlineCurrent];
//            r.set(left, top, left + dragOutline.getWidth(), top + dragOutline.getHeight());
//            if (resize) {
//                cellToRect(cellX, cellY, spanX, spanY, r);
//            }
//
//            mDragOutlineAnims[mDragOutlineCurrent].setTag(dragOutline);
//            mDragOutlineAnims[mDragOutlineCurrent].animateIn();
        }
    }

    public void clearDragOutlines() {
        final int oldIndex = mDragOutlineCurrent;
//        mDragOutlineAnims[oldIndex].animateOut();
        mDragCell[0] = -1;
        mDragCell[1] = -1;
    }

    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param result Array in which to place the result, or null (in which case a new array will
     *        be allocated)
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    int[] findNearestVacantArea(
            int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestVacantArea(pixelX, pixelY, spanX, spanY, null, result);
    }
    
    int[] findNearestVacantArea(
            int pixelX, int pixelY, int spanX, int spanY, View3D ignoreView, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, ignoreView, true, result);
    }
    
    private final Stack<Rect> mTempRectStack = new Stack<Rect>();
    private void lazyInitTempRectStack() {
        if (mTempRectStack.isEmpty()) {
            for (int i = 0; i < mCountX * mCountY; i++) {
                mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> used) {
        while (!used.isEmpty()) {
            mTempRectStack.push(used.pop());
        }
    }
    
    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreOccupied If true, the result can be an occupied cell
     * @param result Array in which to place the result, or null (in which case a new array will
     *        be allocated)
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
//    int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, View3D ignoreView,
//            boolean ignoreOccupied, int[] result) {
//        // mark space take by ignoreView as available (method checks if ignoreView is null)
//        markCellsAsUnoccupiedForView(ignoreView);
//
//        // For items with a spanX / spanY > 1, the passed in point (pixelX, pixelY) corresponds
//        // to the center of the item, but we are searching based on the top-left cell, so
//        // we translate the point over to correspond to the top-left.
//        pixelX -= (mCellWidth + mWidthGap) * (spanX - 1) / 2f;
//        pixelY -= (mCellHeight + mHeightGap) * (spanY - 1) / 2f;
//
//        // Keep track of best-scoring drop area
//        final int[] bestXY = result != null ? result : new int[2];
//        double bestDistance = Double.MAX_VALUE;
//
//        final int countX = mCountX;
//        final int countY = mCountY;
//        final boolean[][] occupied = mOccupied;
//
//        for (int y = 0; y < countY - (spanY - 1); y++) {
//            inner:
//            for (int x = 0; x < countX - (spanX - 1); x++) {
//                if (ignoreOccupied) {
//                    for (int i = 0; i < spanX; i++) {
//                        for (int j = 0; j < spanY; j++) {
//                            if (occupied[x + i][y + j]) {
//                                // small optimization: we can skip to after the column we
//                                // just found an occupied cell
//                                x += i;
//                                continue inner;
//                            }
//                        }
//                    }
//                }
//                final int[] cellXY = mTmpXY;
//                cellToCenterPoint(x, y, cellXY);
//
//                double distance = Math.sqrt(Math.pow(cellXY[0] - pixelX, 2)
//                        + Math.pow(cellXY[1] - pixelY, 2));
//                if (distance <= bestDistance) {
//                    bestDistance = distance;
//                    bestXY[0] = x;
//                    bestXY[1] = y;
//                }
//            }
//        }
//        // re-mark space taken by ignoreView as occupied
//        markCellsAsOccupiedForView(ignoreView);
//
//        // Return -1, -1 if no suitable location found
//        if (bestDistance == Double.MAX_VALUE) {
//            bestXY[0] = -1;
//            bestXY[1] = -1;
//        }
//        return bestXY;
//    }

    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param minSpanX The minimum horizontal span required
     * @param minSpanY The minimum vertical span required
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreOccupied If true, the result can be an occupied cell
     * @param result Array in which to place the result, or null (in which case a new array will
     *        be allocated)
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY,
            View3D ignoreView, boolean ignoreOccupied, int[] result, int[] resultSpan,
            boolean[][] occupied) {
        lazyInitTempRectStack();
        // mark space take by ignoreView as available (method checks if ignoreView is null)
        markCellsAsUnoccupiedForView(ignoreView, occupied);

        // For items with a spanX / spanY > 1, the passed in point (pixelX, pixelY) corresponds
        // to the center of the item, but we are searching based on the top-left cell, so
        // we translate the point over to correspond to the top-left.
        pixelX -= (mCellWidth + mWidthGap) * (spanX - 1) / 2f;
        pixelY -= (mCellHeight + mHeightGap) * (spanY - 1) / 2f;

        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        double bestDistance = Double.MAX_VALUE;
        final Rect bestRect = new Rect(-1, -1, -1, -1);
        final Stack<Rect> validRegions = new Stack<Rect>();

        final int countX = mCountX;
        final int countY = mCountY;

        if (minSpanX <= 0 || minSpanY <= 0 || spanX <= 0 || spanY <= 0 ||
                spanX < minSpanX || spanY < minSpanY) {
            return bestXY;
        }

        for (int y = 0; y < countY - (minSpanY - 1); y++) {
            inner:
            for (int x = 0; x < countX - (minSpanX - 1); x++) {
                int ySize = -1;
                int xSize = -1;
                if (ignoreOccupied) {
                    // First, let's see if this thing fits anywhere
                    for (int i = 0; i < minSpanX; i++) {
                        for (int j = 0; j < minSpanY; j++) {
                            if (occupied[x + i][y + j]) {
                                continue inner;
                            }
                        }
                    }
                    xSize = minSpanX;
                    ySize = minSpanY;

                    // We know that the item will fit at _some_ acceptable size, now let's see
                    // how big we can make it. We'll alternate between incrementing x and y spans
                    // until we hit a limit.
                    boolean incX = true;
                    boolean hitMaxX = xSize >= spanX;
                    boolean hitMaxY = ySize >= spanY;
                    while (!(hitMaxX && hitMaxY)) {
                        if (incX && !hitMaxX) {
                            for (int j = 0; j < ySize; j++) {
                                if (x + xSize > countX -1 || occupied[x + xSize][y + j]) {
                                    // We can't move out horizontally
                                    hitMaxX = true;
                                }
                            }
                            if (!hitMaxX) {
                                xSize++;
                            }
                        } else if (!hitMaxY) {
                            for (int i = 0; i < xSize; i++) {
                                if (y + ySize > countY - 1 || occupied[x + i][y + ySize]) {
                                    // We can't move out vertically
                                    hitMaxY = true;
                                }
                            }
                            if (!hitMaxY) {
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
                cellToCenterPoint(x, y, cellXY);

                // We verify that the current rect is not a sub-rect of any of our previous
                // candidates. In this case, the current rect is disqualified in favour of the
                // containing rect.
                Rect currentRect = mTempRectStack.pop();
                currentRect.set(x, y, x + xSize, y + ySize);
                boolean contained = false;
                for (Rect r : validRegions) {
                    if (r.contains(currentRect)) {
                        contained = true;
                        break;
                    }
                }
                validRegions.push(currentRect);
                double distance = Math.sqrt(Math.pow(cellXY[0] - pixelX, 2)
                        + Math.pow(cellXY[1] - pixelY, 2));

                if ((distance <= bestDistance && !contained) ||
                        currentRect.contains(bestRect)) {
                    bestDistance = distance;
                    bestXY[0] = x;
                    bestXY[1] = y;
                    if (resultSpan != null) {
                        resultSpan[0] = xSize;
                        resultSpan[1] = ySize;
                    }
                    bestRect.set(currentRect);
                }
            }
        }
        // re-mark space taken by ignoreView as occupied
        markCellsAsOccupiedForView(ignoreView, occupied);

        // Return -1, -1 if no suitable location found
        if (bestDistance == Double.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        recycleTempRects(validRegions);
        return bestXY;
    }
    
    int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, View3D ignoreView,
            boolean ignoreOccupied, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY,
                spanX, spanY, ignoreView, ignoreOccupied, result, null, mOccupied);
    }
    
    /**
     * Find a vacant area that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreView Considers space occupied by this view as unoccupied
     * @param result Previously returned value to possibly recycle.
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
//    int[] findNearestVacantArea(
//            int pixelX, int pixelY, int spanX, int spanY, View3D ignoreView, int[] result) {
//        return findNearestArea(pixelX, pixelY, spanX, spanY, ignoreView, true, result);
//    }
    
    int[] findNearestVacantArea(int pixelX, int pixelY, int minSpanX, int minSpanY,
            int spanX, int spanY, View3D ignoreView, int[] result, int[] resultSpan) {
        return findNearestArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, ignoreView, true,
                result, resultSpan, mOccupied);
    }

    /**
     * Find a starting cell position that will fit the given bounds nearest the requested
     * cell location. Uses Euclidean distance to score multiple vacant areas.
     *
     * @param pixelX The X location at which you want to search for a vacant area.
     * @param pixelY The Y location at which you want to search for a vacant area.
     * @param spanX Horizontal span of the object.
     * @param spanY Vertical span of the object.
     * @param ignoreView Considers space occupied by this view as unoccupied
     * @param result Previously returned value to possibly recycle.
     * @return The X, Y cell of a vacant area that can contain this object,
     *         nearest the requested location.
     */
    int[] findNearestArea(
            int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, null, false, result);
    }
    
    int [] findNearestAreaAvailuable (int pixelX, int pixelY, int spanX, int spanY, int[] result) {
    	findNearestArea(pixelX, pixelY, spanX, spanY,
                spanX, spanY, null, true, mTargetCell, null, mOccupied);
    	result[0] = mTargetCell[0];
    	result[1] = mTargetCell[1];
    	
    	return result;
    }
    
    private int[] findNearestArea(int cellX, int cellY, int spanX, int spanY, int[] direction,
            boolean[][] occupied, boolean blockOccupied[][], int[] result) {
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        float bestDistance = Float.MAX_VALUE;
        int bestDirectionScore = Integer.MIN_VALUE;

        final int countX = mCountX;
        final int countY = mCountY;

        for (int y = 0; y < countY - (spanY - 1); y++) {
            inner:
            for (int x = 0; x < countX - (spanX - 1); x++) {
                // First, let's see if this thing fits anywhere
                for (int i = 0; i < spanX; i++) {
                    for (int j = 0; j < spanY; j++) {
                        if (occupied[x + i][y + j] && (blockOccupied == null || blockOccupied[i][j])) {
                            continue inner;
                        }
                    }
                }

                float distance = (float)
                        Math.sqrt((x - cellX) * (x - cellX) + (y - cellY) * (y - cellY));
                int[] curDirection = mTmpPoint;
                computeDirectionVector(x - cellX, y - cellY, curDirection);
                // The direction score is just the dot product of the two candidate direction
                // and that passed in.
                int curDirectionScore = direction[0] * curDirection[0] +
                        direction[1] * curDirection[1];
                boolean exactDirectionOnly = false;
                boolean directionMatches = direction[0] == curDirection[0] &&
                        direction[0] == curDirection[0];
                if ((directionMatches || !exactDirectionOnly) &&
                        Float.compare(distance,  bestDistance) < 0 || (Float.compare(distance,
                        bestDistance) == 0 && curDirectionScore > bestDirectionScore)) {
                    bestDistance = distance;
                    bestDirectionScore = curDirectionScore;
                    bestXY[0] = x;
                    bestXY[1] = y;
                }
            }
        }

        // Return -1, -1 if no suitable location found
        if (bestDistance == Float.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        return bestXY;
    }
    
    private void computeDirectionVector(float deltaX, float deltaY, int[] result) {
        double angle = Math.atan(((float) deltaY) / deltaX);

        result[0] = 0;
        result[1] = 0;
        if (Math.abs(Math.cos(angle)) > 0.5f) {
            result[0] = (int) Math.signum(deltaX);
        }
        if (Math.abs(Math.sin(angle)) > 0.5f) {
            result[1] = (int) Math.signum(deltaY);
        }
    }

    private void copyOccupiedArray(boolean[][] occupied) {
        for (int i = 0; i < mCountX; i++) {
            for (int j = 0; j < mCountY; j++) {
                occupied[i][j] = mOccupied[i][j];
            }
        }
    }

    boolean existsEmptyCell() {
        return findCellForSpan(null, 1, 1);
    }

    /**
     * Finds the upper-left coordinate of the first rectangle in the grid that can
     * hold a cell of the specified dimensions. If intersectX and intersectY are not -1,
     * then this method will only return coordinates for rectangles that contain the cell
     * (intersectX, intersectY)
     *
     * @param cellXY The array that will contain the position of a vacant cell if such a cell
     *               can be found.
     * @param spanX The horizontal span of the cell we want to find.
     * @param spanY The vertical span of the cell we want to find.
     *
     * @return True if a vacant cell of the specified dimension was found, false otherwise.
     */
    public boolean findCellForSpan(int[] cellXY, int spanX, int spanY) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, -1, -1, null);
    }

    /**
     * The superset of the above two methods
     */
    boolean findCellForSpanThatIntersectsIgnoring(int[] cellXY, int spanX, int spanY,
            int intersectX, int intersectY, View3D ignoreView) {
        // mark space take by ignoreView as available (method checks if ignoreView is null)
        markCellsAsUnoccupiedForView(ignoreView);

        boolean foundCell = false;
        while (true) {
            int startX = 0;
            if (intersectX >= 0) {
                startX = Math.max(startX, intersectX - (spanX - 1));
            }
            int endX = mCountX - (spanX - 1);
            if (intersectX >= 0) {
                endX = Math.min(endX, intersectX + (spanX - 1) + (spanX == 1 ? 1 : 0));
            }
            int startY = 0;
            if (intersectY >= 0) {
                startY = Math.max(startY, intersectY - (spanY - 1));
            }
            int endY = mCountY - (spanY - 1);
            if (intersectY >= 0) {
                endY = Math.min(endY, intersectY + (spanY - 1) + (spanY == 1 ? 1 : 0));
            }

            for (int y = startY; y < endY && !foundCell; y++) {
                inner:
                for (int x = startX; x < endX; x++) {
                    for (int i = 0; i < spanX; i++) {
                        for (int j = 0; j < spanY; j++) {
                            if (mOccupied[x + i][y + j]) {
                                // small optimization: we can skip to after the column we just found
                                // an occupied cell
                                x += i;
                                continue inner;
                            }
                        }
                    }
                    if (cellXY != null) {
                        cellXY[0] = x;
                        cellXY[1] = y;
                    }
                    foundCell = true;
                    break;
                }
            }
            if (intersectX == -1 && intersectY == -1) {
                break;
            } else {
                // if we failed to find anything, try again but without any requirements of
                // intersecting
                intersectX = -1;
                intersectY = -1;
                continue;
            }
        }

        // re-mark space taken by ignoreView as occupied
        markCellsAsOccupiedForView(ignoreView);
        return foundCell;
    }
    
    public boolean onDropCompleted(View3D target, boolean success) {
		// TODO Auto-generated method stub
   	
		return onDropLeave();
	}
    
    public boolean onDropLeave() {
//    	Log.v("test", "onDropLeave");
    	//Log.d("launcher", "onDropLeave");
    	cellCleanDropStatus();
    	return true;
    }
    
    public void cellCleanDropStatus() {
    	haveReflect = false;
    	haveEstablishFolder = false;
    	
    	if (mflect9patchView != null) {
    		removeView(mflect9patchView);
    		mflect9patchView = null;
    	}
    	
    	for (View3D i: reflectView) {
    		removeView(i);
//    		i.remove();
    	}
    	
//    	cellSelfDrag = false;
    	reflectView.clear();
    	
    	if (FolderLargeTween != null) {
			FolderLargeTween.free();
			FolderLargeTween = null;
		}
		if (FolderSmallTween != null) {

			FolderSmallTween.free();
			FolderSmallTween = null;
		}
    }
    
    /**
     * this value only use for array list drop
     */
    int curWorkSpaceScreen = 0;
    
    void setScreen(int value) {
    	curWorkSpaceScreen = value;
    }
    
    public void addView(View3D view) {
    	addToList(view);
    }
    
    boolean testValid(int[] array) {
    	boolean res = true;
    	
    	if (array[0] < 0 || array[1] < 0 || array[0] >= mCountX || array[1] >= mCountY) {
			res = false;
		}
    	
    	return res;
    }
    
    public View3D getViewInCell(int cellX, int cellY) {
    	View3D res = null;
    	
    	if (cellX < 0 || cellY < 0 || cellY >= mCountX || cellY >= mCountY) {
			return null;
		}
    	
    	if (mOccupied[cellX][cellY])
    		res = cellViewList[cellX][cellY];
    	else
    		res = null;
    	
    	return res;
    }
       
    /**
     * 按照所在格子坐标添加图�?
     * 
     * @param view
     * @param x: 
     * @param y: 
     */
    public boolean addView(View3D view, int x, int y) {
    	int spanX, spanY;
    	ItemInfo itemInfo;
    	Log.d("launcher", "addView:"+view.name);
    	if (view == null)
    		return false;
    	
    	if (x < 0 || x >= mCountX) {
    		Log.v("test", "addView x cell error.... x = " + x);
			return false;
		}
    	
    	if (y < 0 || y >= mCountY) {
    		Log.v("test", "addView y cell error.... y = " + y);
			return false;
		}
    	clearDragTemp();
    	
    	spanX = drapGetSpanX(view);
		spanY = drapGetSpanY(view);
		
		if (!cellTestDropSucess(x, y, spanX, spanY)) {
			if (view instanceof Widget3D) {
				itemInfo = ((IconBase3D)view).getItemInfo();
				Root3D.deleteFromDB(itemInfo);
				Widget3D widget3D = (Widget3D)view;
				Widget3DManager.getInstance().deleteWidget3D(widget3D);
				Log.d("launcher", "deleteWidget3D:"+itemInfo.title);
			}
//			SendMsgToAndroid.sendOurToastMsg(R3D.getString(R.string.no_space_add_icon));
			return false;
		}
		
		itemInfo = ((IconBase3D)view).getItemInfo();
		cellToPoint(x, y, mTargetPoint);
		view.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
		view.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
		itemInfo.cellX = x;
		itemInfo.cellY = y;
		itemInfo.cellTempX = x;
		itemInfo.cellTempY = y;
		itemInfo.x = (int) view.x;
		itemInfo.y = (int) view.y;
		
//		Root3D.addOrMoveDB(itemInfo);
		
		if (view instanceof Widget3D) {
			clockList.add(view);
		}
		
		for (int k = 0; k < spanX; k++)
			for (int j = 0; j < spanY; j++) {
				mOccupied[x + k][y + j] = true;
				cellViewList[x + k][y + j] = view;
			}
		addtoSuperClass(view);
		
		return true;
    }
    
    public void addAutoSortView(ArrayList<View3D> list)
    {
    	int spanX;
     	int spanY;
    	clearDragTemp();
    	int curListInd = 0;
    	int listTotalCount = list.size();
        View3D temp;
    	out:for (int j = mCountY - 1; j >= 0; j--) 
			for (int i = 0; i < mCountX; i++) {
				if (curListInd >= listTotalCount)
					break out;
				if (!mOccupied[i][j]) {
					temp = list.get(curListInd);
					spanX = drapGetSpanX(temp);
					spanY = drapGetSpanY(temp);
					cellToPoint(i, j, mTargetPoint);
					point.x = mTargetPoint[0]
							+ (spanX * mCellWidth - temp.width) / 2;
					point.y = mTargetPoint[1]
							+ (spanY * mCellHeight - temp.height) / 2;
					addtoSuperClass(temp);
					curListInd++;
					cellViewList[i][j] = temp;
					mOccupied[i][j] = true;
					ItemInfo info = ((IconBase3D) temp).getItemInfo();
					info.screen = curWorkSpaceScreen;
					info.x = (int) point.x;
					info.y = (int) point.y;
					info.cellX = i;
					info.cellY = j;
					info.cellTempX = i;
					info.cellTempY = j;
					Root3D.addOrMoveDB(info);
					temp.stopTween();
					temp.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.8f,
							info.x, info.y, 0);

				}
			}
	
    	cellSelfDrag = false;
    	cellSelfDragView = null;
    	onDropLeave();
    	
    }
    public boolean addView(ArrayList<View3D> list, int x, int y) {
		// TODO Auto-generated method stub
    	boolean res = true;
    	int spanX, spanY;
    	ItemInfo itemInfo;
    	
    	clearDragTemp();
    	
//    	Log.v("test", "addView  x, y = " + x + ", " + y);
    	
    	int realY = (int)y;
    	
    	if (list.size() > 1)
    		cellDropType = CELL_DROPTYPE_ARRAY;
    	else {
   			if (cellDropType != CELL_DROPTYPE_SINGLE_DROP_FOLDER)cellDropType = CELL_DROPTYPE_SINGLE_DROP;
    	}

    	switch (cellDropType) {
    		case CELL_DROPTYPE_SINGLE_DROP:
    		case CELL_DROPTYPE_FOLDER:
    		case CELL_DROPTYPE_WIDGET:
    		case CELL_DROPTYPE_WIDGET_3D:
    		default:
	       	for (View3D i: list) {
	       		cellDropType = CELL_DROPTYPE_SINGLE_DROP;
	       		spanX = drapGetSpanX(i);
				spanY = drapGetSpanY(i);
				findNearestArea((int)x, (int)realY, spanX, spanY,
			                spanX, spanY, null, true, mTargetCell, null, mOccupied);
				
				if (mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY) {
					res = false;
				}
					
				if (res && addView(i, mTargetCell[0], mTargetCell[1]))
					res = true;
				else
					res = false;
	       	}
	       		
	       		break;
    		case CELL_DROPTYPE_ARRAY:
    			res = produceMultiList(list, x, y, false);
    			break;
    		case CELL_DROPTYPE_SINGLE_DROP_FOLDER:
    			View3D folder, quiet;
    			View3D view = list.get(0);
    			folder = cellFindFolderInTemp();
    			if (folder != null && folder.getVisible()) {
    				
    		    	//xiatian add start	//fix bug:Preview view position Y error,when cell make and preview folder in folder_iphone_style
    		    	if(((FolderIcon3D)folder).folder_style == FolderIcon3D.folder_iphone_style)
    		    	{
    		    		folder.y -= ( folder.height - ((FolderIcon3D) folder).getIconBmpHeight() );
    		    	}
    		    	//xiatian add end
    				
    				findCellbyCoodinate((int)folder.x, (int)folder.y, mTempCell);
    				quiet = cellViewList[mTempCell[0]][mTempCell[1]];
    				
    				cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
    				if (quiet == null || !(quiet instanceof ViewCircled3D))
    					break;
    				ItemInfo info, foldInfo;
    				addtoSuperClass(folder);
    				foldInfo = ((FolderIcon3D) folder).getItemInfo();
    				foldInfo.screen = curWorkSpaceScreen;
    				foldInfo.x = (int) folder.x;
    				foldInfo.y = (int) folder.y;
    				foldInfo.cellX = mTempCell[0];
    				foldInfo.cellY = mTempCell[1];
    				foldInfo.cellTempX = mTempCell[0];
    				foldInfo.cellTempY = mTempCell[1];
    				Root3D.addOrMoveDB(foldInfo);
    				iLoongLauncher.getInstance().addFolderInfoToSFolders((UserFolderInfo) foldInfo);
    				ArrayList<View3D> addList = new ArrayList<View3D>();
    				addList.add(quiet);
    				addList.add(view);

    				FolderIcon3D fo = ((FolderIcon3D) folder);
    				fo.changeFolderFrontRegion(false);
    				fo.onDrop(addList, 0, 0);

    				Color color = folder.getColor();
    				color.a = 1.0f;
    				folder.setColor(color);

    				cellRemoveFolderInTemp();
    				cellViewList[mTempCell[0]][mTempCell[1]] = folder;
    				mOccupied[mTempCell[0]][mTempCell[1]] = true;
    				
    				this.setTag(folder);
    				viewParent.onCtrlEvent(this, MSG_ADD_DRAGLAYER);
    				addList.clear();
    				
    				
    				res = true;
    			} else {
    				
    				spanX = drapGetSpanX(view);
    				spanY = drapGetSpanY(view);
    				
    				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
    				
    				findNearestArea((int)x, (int)realY, spanX, spanY,
			                spanX, spanY, null, true, mTargetCell, null, mOccupied);
    				
    				if (mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY) {
    					res = false;
    					break;
    				}
    				
    				if (res && addView(view, mTargetCell[0], mTargetCell[1]))
    					res = true;
    				else
    					res = false;
    			}
    			break;
    	}

    	if (!res) {
    		if (cellSelfDrag && cellDropType != CELL_DROPTYPE_ARRAY) {
//    			SendMsgToAndroid.sendOurToastMsg("无法放置在此位置上！");
//    			View3D back = cellSelfDragView;
//    			
//    			if (back != null) {
//    				spanX = drapGetSpanX(back);
//    				spanY = drapGetSpanY(back);
//    				cellToPoint(cellSelfDragFrom[0], cellSelfDragFrom[1], mTargetPoint);
//    				back.x = mTargetPoint[0] + (spanX * mCellWidth - back.width) / 2;
//    				back.y = mTargetPoint[1] + (spanY * mCellHeight - back.height) / 2;
//    				
//    				Log.v("test", "x, y = " + mTargetCell[0] + ", " + mTargetCell[1]);
//    				
//    				for (int k = 0; k < spanX; k++)
//    					for (int j = 0; j < spanY; j++) {
//    						mOccupied[cellSelfDragFrom[0] + k][cellSelfDragFrom[1] + j] = true;
//    						cellViewList[cellSelfDragFrom[0] + k][cellSelfDragFrom[1] + j] = back;
//    					}
//    				addtoSuperClass(back);
//    	       		reflectView.remove(0);
//    			}
    		} else {
    			IconBase3D view = (IconBase3D) list.get(0);
    			ItemInfo info = view.getItemInfo();
    			Log.v("test", "error start...........");
    	    	Log.v("test", "cellDropType " + cellDropType + ",name = " + list.get(0).name + " screen: " + info.screen + " x:" + info.x + " y:" + info.y + " sx:" + info.spanX + " sy:" + info.spanY + " cx:" + info.cellX + " cy:" + info.cellY);
    	    	Log.v("test", "error end  ...........");
	    		itemInfo = ((IconBase3D)view).getItemInfo();
    	    	if (view instanceof Widget3D && itemInfo.container == -1) {/* widget3D from mainmenu */
    				Root3D.deleteFromDB(info);
    				Widget3D widget3D = (Widget3D)view;
    				Widget3DManager.getInstance().deleteWidget3D(widget3D);
    				Log.d("launcher", "deleteWidget3D:"+info.title);
    			}
    			SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.no_space_add_icon));
    		}
    	}
    	
    	cellSelfDrag = false;
    	cellSelfDragView = null;
    	onDropLeave();
       	return res;
	}
    
    public void cellFindViewAndRemove(View3D view) {
    	for (int i = 0; i < mCountX; i++)
			for (int j = 0; j < mCountY; j++) {
				if (cellViewList[i][j] == view) {
					mOccupied[i][j] = false;
					cellViewList[i][j] = null;
					Log.d("launcher", "remove"+view.name);
				}
			}
    }
    
    public void cellFindViewAndRemove(int cellX,int cellY) {
    	Log.d("launcher", "remove:"+cellX+","+cellY);
    	mOccupied[cellX][cellY] = false;
		View3D view = cellViewList[cellX][cellY];
		if(view != null)view.remove();
		cellViewList[cellX][cellY] = null;
    }
    
    boolean cellTestDropSucess(int h, int v, int spanX, int spanY) {
    	boolean res = true;
    	int maxX = h + spanX - 1;
    	int maxY = v + spanY - 1;
    	
    	if (maxX < 0 || maxY < 0 || maxX >= mCountX || maxY >= mCountY) {
			return false;
		}
    	
    	for (int k = 0; k < spanX; k++)
			for (int j = 0; j < spanY; j++) {
				if (mOccupied[h + k][v + j]) {
					res = false;
					break;
				}
			}
    	
    	return res;
    }
    
    boolean haveReflect = false;
    boolean haveEstablishFolder = false;
    ArrayList<View3D> reflectView = new ArrayList<View3D>();
    
    @SuppressWarnings("unchecked")
	public boolean produceMultiList(ArrayList<View3D> list, float x, float y, boolean preview) {
    	boolean res = false;
     	int remainGrid = 0;
     	int spanX;
     	int spanY;
     	int listTotalCount = list.size();
     	int curListInd = 0;
     	View3D temp, view;
     	boolean needFolder = false;
     	
     	if (listTotalCount < 1)
     		return res;
     	
     	for (int i = 0; i < mCountX; i++)
     		for (int j = 0; j < mCountY; j++) {
     			if (!mOccupied[i][j]) {
     				remainGrid++;
     			}
     		}
     	
     	if (remainGrid == 0)
     		return res;
     	
     	if (remainGrid + R3D.folder_max_num - 1 < listTotalCount) {
     		if (!preview) SendMsgToAndroid.sendOurToastMsg(R3D.getString(RR.string.exceed_num_add_icon));
     		return res;//选择的数量超过最大�?     	}
     		}
     	
     	out:for (int j = mCountY - 1; j >= 0; j--) 
     		for (int i = 0; i < mCountX; i++) {
     			if (curListInd >= listTotalCount)
     				break out;
     			
     			if (remainGrid == 1) {
     				if (listTotalCount - curListInd == 1) {
     					
     				} else {
     					needFolder = true;
     				}
     			}
     			
     			if (!mOccupied[i][j] && needFolder) {
     				temp = cellFindFolderInTemp();
     				if (temp == null)
	     				if (preview) {
	     					temp = cellMakeFolder(i, j, true,true);
	 						reflectView.add(temp);
	     				}
	 					else {
	 						temp = cellMakeFolder(i, j, false,true);
	 					}
     				
     				cellToPoint(i, j, mTargetPoint);
     				temp.x = mTargetPoint[0] + (1 * mCellWidth - temp.width) / 2;
 					temp.y = mTargetPoint[1] + (1 * mCellHeight - temp.height) / 2;
 					addtoSuperClass(temp);
 					
 					if (!preview) {
 						FolderIcon3D fo = ((FolderIcon3D) temp);
 	    				fo.mInfo.x = (int)temp.x;
 	    				fo.mInfo.y = (int)temp.y;
 						cellViewList[i][j] = temp;
 						mOccupied[i][j] = true;
 						ArrayList<View3D> leftList;// = new ArrayList<View3D>();
 						leftList = (ArrayList<View3D>) list.clone();
 						for (int k = 0; k < curListInd; k++)
 							leftList.remove(list.get(k));
 						
// 						fo.addFolderNode(leftList);
 						ItemInfo info = ((FolderIcon3D) temp).getItemInfo();
						info.screen = curWorkSpaceScreen;
						info.x = (int) temp.x;
						info.y = (int) temp.y;
						info.cellX = i;
						info.cellY = j;
						info.cellTempX = i;
						info.cellTempY = j;
						Root3D.addOrMoveDB(info);
						iLoongLauncher.getInstance().addFolderInfoToSFolders((UserFolderInfo) info);
 						fo.changeFolderFrontRegion(false);
 						fo.onDrop(leftList, 0, 0);
 						this.setTag(fo);
 	    				viewParent.onCtrlEvent(this, MSG_ADD_DRAGLAYER);
 						reflectView.remove(temp);
 						
 					}
 					
     				break out;
     			}
     			
     			if (!mOccupied[i][j]) {
     				view = list.get(curListInd);
     				
     				spanX = drapGetSpanX(view);
     				spanY = drapGetSpanY(view);
     				
     				if (preview) {
		     			temp = view.clone();
		     			Color color = temp.getColor();
		     			color.a = 0.5f;
		     			temp.setColor(color);
     				} else {
     					temp = view;
     				}
     					
     					cellToPoint(i, j, mTargetPoint);
     					temp.x = mTargetPoint[0] + (spanX * mCellWidth - temp.width) / 2;
     					temp.y = mTargetPoint[1] + (spanY * mCellHeight - temp.height) / 2;
     					addtoSuperClass(temp);
     					if (preview)
     						reflectView.add(temp);
     				curListInd++;
     				remainGrid--;
     				if (!preview) {
						cellViewList[i][j] = temp;
						mOccupied[i][j] = true;
						ItemInfo info = ((IconBase3D) temp).getItemInfo();
						info.screen = curWorkSpaceScreen;
						info.x = (int) temp.x;
						info.y = (int) temp.y;
						info.cellX = i;
						info.cellY = j;
						info.cellTempX = i;
						info.cellTempY = j;
						int tx = (int) temp.x;
						int ty = (int) temp.y;
						if (listTotalCount > 1) {
							if (x != tx || y != ty) {
								temp.setPosition(x, y);
								temp.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.8f,
										tx, ty, 0);
							}
						}
						Root3D.addOrMoveDB(info);
						if (temp instanceof Icon3D) {
							Icon3D iconView = (Icon3D)view;
							iconView.setItemInfo(iconView.getItemInfo());
						}
					}
     				res = true;
     			}

     			if (remainGrid == 0)
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
//    final static int CELL_MOVE_ = 0;
    
    void setCellDropTypeArrayDrop() {
    	cellDropType = CELL_DROPTYPE_ARRAY;
    }
    
    int getCellDropType()
    {
    	return cellDropType;
    }
    View3D cellFindFolderInTemp() {
    	View3D view = null;
    	
    	for (View3D i: reflectView) {
    		if (i instanceof FolderIcon3D) {
    			view = i;
    			break;
    		}
    	}
    	
    	return view;
    }
    
    void cellRemoveFolderInTemp() {
    	View3D view = null;
    	
    	for (View3D i: reflectView) {
    		if (i instanceof FolderIcon3D) {
    			view = i;
    			break;
    		}
    	}
    	
    	if (view != null) {
    		reflectView.remove(view);
    	}
    }
    
    View3D cellFindSingleViewInTemp() {
    	View3D view = null;
    	
    	for (View3D i: reflectView) {
    		if (i instanceof ViewCircled3D) {
    			view = i;
    			break;
    		}
    	}
    	
    	return view;
    }
    
    View3D cellFindWidgetInTemp() {
    	View3D view = null;
    	
    	for (View3D i: reflectView) {
    		if (i instanceof Widget) {
    			view = i;
    			break;
    		}
    	}
    	
    	return view;
    }
    
    View3D cellFindWidget3DInTemp() {
    	View3D view = null;
    	
    	for (View3D i: reflectView) {
    		if (i instanceof Widget3D) {
    			view = i;
    			break;
    		}
    	}
    	
    	return view;
    }
    
    @Override
	public boolean onLongClick(float x, float y) {
		// TODO Auto-generated method stub
    	clearDragTemp();
    	
		if (super.onLongClick(x, y)) {
			if (findCellbyCoodinate((int)x, (int)y, mTempCell)) {
	    		View3D view = cellViewList[mTempCell[0]][mTempCell[1]];
//	    		cellSelfDrag = true;
	    		
	    		
	    		if (view != null) {
//	    			findCellbyView(view, mTempCell);
//					cellClearWidgetOccupiedList(view);
//					
//					ItemInfo item = ((IconBase3D)view).getItemInfo();
//					item.cellX = -1;
//					item.cellY = -1;
	    			Log.v("test", "<Error> Celllayout3D onLongClick have picked one view, this shouldn't happen!!!");
	    		}
//	    		cellSelfDragFrom[0] = mTempCell[0];
//	    		cellSelfDragFrom[1] = mTempCell[1];
//	    		cellSelfDragView = view;
	    	}
			
			return true;
		} else 
			return false;
		
	}
    
    boolean cellSelfDrag = false;
    View3D cellSelfDragView = null;
    
    int[] cellSelfDragFrom = new int[2];
    
    void cellClearWidgetOccupiedList(View3D view) {
    	for (int i = 0; i < mCountX; i++)
			for (int j = 0; j < mCountY; j++) {
				View3D temp = cellViewList[i][j];
				
				if (temp == view) {
					mOccupied[i][j] = false;
					cellViewList[i][j] = null;
				}
			}
    }
    
    int CellgetViewType(View3D view) {
    	int res = 0;
    	
    	if (view instanceof ViewCircled3D)
    		res = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
    	else if (view instanceof FolderIcon3D)
    		res = CELL_DROPTYPE_FOLDER;
    	else if (view instanceof Widget)
    		res = CELL_DROPTYPE_WIDGET;
    	else if (view instanceof Widget3D)
    		res = CELL_DROPTYPE_WIDGET_3D;
    	else
    		res = 0;
    	return res;
    }
    
    ArrayList<View3D> tempViewList = new ArrayList<View3D>();
    
    public boolean onDropOver(ArrayList<View3D> list, float x, float y) {
    	boolean res = true;
    	View3D view;
    	if (list == null)
    		return res;
    	if(x <0 || y < this.getPaddingBottom() || y > (height-this.getPaddingTop())){
    		return false;
    	}
    	
    	int realY = (int)y;
    	
    	if (list.size() > 1) {
    		if (!haveReflect) {
    			cellDropType = CELL_DROPTYPE_ARRAY;
    			haveReflect = produceMultiList(list, x, realY, true);
    		}
    	} else {
    		view = list.get(0);//only drag one source
    		clearDragTemp();

    		if (view instanceof ViewCircled3D) {
    			View3D temp, targetView, folder;
    			temp = cellFindSingleViewInTemp();
    			folder = cellFindFolderInTemp();
    			
    			int spanX = drapGetSpanX(view);
    			int spanY = drapGetSpanY(view);
    			mTargetCell = findNearestArea((int)x, (int)realY, spanX, spanY, mTargetCell);
    			boolean isOccupy = testIfViewIsOccupy(view, mTargetCell[0], mTargetCell[1], spanX, spanY);
    			
    			if(!isOccupy){
    				//zjp 
//    				if(folder != null)folder.setVisible(false);
    				if (folder != null) {
						if (folder.isVisible()) {
							FolderSmallAnim(0.3f, folder);
						}
					}
    				if (!haveReflect) {
        				if (dropReflectView(view, (int)x, (int)realY) != null)
        					haveReflect = true;
        				
        				cellDropType = CELL_DROPTYPE_SINGLE_DROP;
        			} 
    				else if(temp != null){
    					temp.setVisible(true);
    					cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
	    				temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
	    				temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
    				}
    			}
    			
    			else {
    				if(temp != null)temp.setVisible(false);
    				if (findCellbyCoodinate((int)x, realY, mTempCell)) {
//    					targetView = cellViewList[mTempCell[0]][mTempCell[1]];
    					targetView = getCellViewAtPosition(mTempCell[0], mTempCell[1]);
    					if (testCellapproach(view, targetView, (int)x, realY) == CELL_MAKE_FOLDER) {
    						if (!haveEstablishFolder) {
    							folder = cellMakeFolder(mTargetCell[0], mTargetCell[1], true,true);
//	    						targetView.setVisible(false);
    							cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
    							folder.x = mTargetPoint[0] + (spanX * mCellWidth - folder.width) / 2;
    							folder.y = mTargetPoint[1] + (spanY * mCellHeight - folder.height) / 2;
    							
    							//xiatian add start	//fix bug:Preview view position Y error,when cell make and preview folder in folder_iphone_style
    							if(((FolderIcon3D)folder).folder_style == FolderIcon3D.folder_iphone_style)
    							{
    								folder.y += ( folder.height - ((FolderIcon3D) folder).getIconBmpHeight() );
    							}
    							//xiatian add end
    							
	    						reflectView.add(folder);
	    						haveEstablishFolder = true;
    						} else {
    							folder = cellFindFolderInTemp();
    							if (folder == null) {
    								folder = cellMakeFolder(mTargetCell[0], mTargetCell[1], true,true);
    	    						reflectView.add(folder);
    	    						haveEstablishFolder = true;
    							}
    							//folder.setVisible(true);
    							if (folder.isVisible() == false) {
    								FolderLargeAnim(0.3f, folder);
    							}
    							cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
    							folder.x = mTargetPoint[0] + (spanX * mCellWidth - folder.width) / 2;
    							folder.y = mTargetPoint[1] + (spanY * mCellHeight - folder.height) / 2;
    							
    							//xiatian add start	//fix bug:Preview view position Y error,when cell make and preview folder in folder_iphone_style
    							if(((FolderIcon3D)folder).folder_style == FolderIcon3D.folder_iphone_style)
    							{
    								folder.y += ( folder.height - ((FolderIcon3D) folder).getIconBmpHeight() );
    							}
    							//xiatian add end
    					    	
    						}
    						//temp.setVisible(false);
    						cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
    					} else {
    						folder = cellFindFolderInTemp();
    						if (folder != null)
    							//folder.setVisible(false);
    							if (folder.isVisible()) {
    								FolderSmallAnim(0.3f, folder);
    							}
    					}
    					
						if (folder == null || (folder != null && !folder.getVisible())) {
							//temp.setVisible(true);
	    					cellToPoint(mTempCell[0], mTempCell[1], mTargetPoint);
		    				//temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
		    				//temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
						}
						
    				}
    					
    			}
    			
//    			Log.v("test", "x, y = " + mTargetCell[0] + ", " + mTargetCell[1]);
    			res = true;
    		} 
//    		else if (view instanceof Widget3D || view instanceof Widget) {
//    			View3D temp;
//    			if (!haveReflect) {
//    				if (dropReflectView(view, (int)x, (int)realY) != null)
//    					haveReflect = true;
//    				
//    				cellDropType = CELL_DROPTYPE_WIDGET_3D;
//    			} else {
//    				temp = cellFindWidget3DInTemp();
//    				if(temp == null) return true;
//    				int spanX = drapGetSpanX(temp);
//    				int spanY = drapGetSpanY(temp);
//    				
//    				mTargetCell = findNearestArea((int)x, (int)realY, spanX, spanY, mTargetCell);
//    				
//    				if (testIfViewIsOccupy(view, mTargetCell[0], mTargetCell[1], spanX, spanY)) {
//    					temp.setVisible(false);
//    				} else {
//	    				temp.setVisible(true);
//						cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
//	    				temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
//	    				temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
//    				}
//    			}
//    		}
    		else if (view instanceof FolderIcon3D || view instanceof Widget3D || view instanceof Widget) {
    			View3D temp = mflect9patchView;
    			int spanX = drapGetSpanX(view);
    			int spanY = drapGetSpanY(view);
    			
    			mTargetCell = findNearestArea((int)x, (int)y, spanX, spanY,
    	                spanX, spanY, null, true, mTargetCell, null, mOccupied);
    			
    			if (mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY) {
    				return true;
    			}
    			
    				if (!haveReflect) {
    					if(temp == null)temp = reflect9PatchView(view, (int)x, (int)y);
	    				haveReflect = true;
	    				cellDropType = CELL_DROPTYPE_FOLDER;
        			} 
    				else if(temp != null){
    					temp.setVisible(true);
    					cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
	    				temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
	    				temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
	    				//Log.d("launcher", "x,y="+temp.x+","+temp.y+" width,height="+view.width+","+view.height);
    				}
    		}
    	}
    	
    	return res;
    }
    
    View3D mflect9patchView = null;
    
    View3D getCellViewAtPosition(int cellX, int cellY) {
    	if (cellX < 0 || cellY < 0 || cellX >= mCountX || cellY >= mCountY) {
    		Log.v("test", "getCellViewAtPosition exception occur!!!!");
    		return null;
    	}
    	
    	return cellViewList[cellX][cellY];
    }
    
//    boolean singleViewDropOver(View3D view, float x, float y) {
//    	boolean res = false;
//    	View3D temp, targetView, folder;
//    	
//		if (!haveReflect) {
//			if (dropReflectView(view, (int)x, (int)y) != null)
//				haveReflect = true;
//
//		} else {
//			temp = cellFindSingleViewInTemp();
//			int spanX = drapGetSpanX(temp);
//			int spanY = drapGetSpanY(temp);
//
//			mTargetCell = findNearestArea((int)x, (int)y, spanX, spanY, mTargetCell);
//			if (testIfViewIsOccupy(view, mTargetCell[0], mTargetCell[1], spanX, spanY))
//				temp.setVisible(false);
//			else if (findCellbyCoodinate((int)x, (int)y, mTempCell)) {
//				targetView = getCellViewAtPosition(mTempCell[0], mTempCell[1]);
//				if (testCellapproach(temp, targetView, (int)x, (int)y) == CELL_MAKE_FOLDER) {
//					if (!haveEstablishFolder) {
//						folder = cellMakeFolder(mTargetCell[0], mTargetCell[1], true);
////						targetView.setVisible(false);
//						cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
//						folder.x = mTargetPoint[0] + (spanX * mCellWidth - folder.width) / 2;
//						folder.y = mTargetPoint[1] + (spanY * mCellHeight - folder.height) / 2;
//						reflectView.add(folder);
//						haveEstablishFolder = true;
//					} else {
//						folder = cellFindFolderInTemp();
//						if (folder == null) {
//							folder = cellMakeFolder(mTargetCell[0], mTargetCell[1], true);
//    						reflectView.add(folder);
//    						haveEstablishFolder = true;
//						}
//						folder.setVisible(true);
//						cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
//						folder.x = mTargetPoint[0] + (spanX * mCellWidth - folder.width) / 2;
//						folder.y = mTargetPoint[1] + (spanY * mCellHeight - folder.height) / 2;
//					}
//					temp.setVisible(false);
//					cellDropType = CELL_DROPTYPE_SINGLE_DROP_FOLDER;
//				} else {
//					folder = cellFindFolderInTemp();
//					if (folder != null)
//						folder.setVisible(false);
//				}
//				
//				folder = cellFindFolderInTemp();
//				if (folder == null || (folder != null && !folder.getVisible())) {
//					temp.setVisible(true);
//					cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
//    				temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
//    				temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
//				}
//			}
//		}
//		
////		Log.v("test", "x, y = " + mTargetCell[0] + ", " + mTargetCell[1]);
//		res = true;
//		
//    	return res;
//    }
    
    boolean findCellbyCoodinate(int x, int y, int[]result) {
    	boolean res = false;
    	result[0] = x / mCellWidth;
    	int start = getPaddingBottom();

    	if (y > start && y < start + mCountY * mCellHeight) {
    		result[1] = (y - start) / mCellHeight;
    		res = true;
    	}
    	else
    		res = false;
    	
    	if (res) {
    		result[0] = result[0] >= mCountX ? mCountX - 1 : result[0];
    		result[1] = result[1] >= mCountY ? mCountY - 1 : result[1];
    	}
    			
    	return res;
    }
    
    boolean findCellbyView(View3D view, int[]result) {
    	boolean res = false;
    	if (view == null)
    		return res;
    	
    	result[0] = (int)view.x / mCellWidth;
    	int start = getPaddingBottom();

    	if ((int)view.y > start && y < mCellWidth * mCellHeight) {
    		result[1] = ((int)view.y - start) / mCellHeight;
    		res = true;
    	}
    	else
    		res = false;
    	
    	if (res) {
    		result[0] = result[0] >= mCountX ? mCountX - 1 : result[0];
    		result[1] = result[1] >= mCountY ? mCountY - 1 : result[1];
    	}
    			
    	return res;
    }
    
    View3D createVirturFolder(boolean compress)
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
        folderInfo.title = R3D.folder3D_name;
//        folderInfo.screen = workspace.getCurrentScreen();
        folderInfo.x=0;
		folderInfo.y=0;
		FolderIcon3D folderIcon3D =new FolderIcon3D("FolderIcon3DView",
				folderInfo);
		if(compress)folderIcon3D.changeFolderFrontRegion(true);
		return folderIcon3D;
	}
    
    View3D cellMakeFolder(int cellX, int cellY, boolean preview,boolean compress) {
    	//Log.d("launcher", "make folder");
    	View3D folder;
    	cellToPoint(cellX, cellY, mTargetPoint);

    	folder = createVirturFolder(compress);

    	folder.x = mTargetPoint[0] + (mCellWidth - folder.width) / 2;
    	folder.y = mTargetPoint[1] + (mCellHeight - folder.height) / 2;
    	
    	//xiatian add start	//fix bug:Preview view position Y error,when cell make and preview folder in folder_iphone_style
    	if(((FolderIcon3D)folder).folder_style == FolderIcon3D.folder_iphone_style)
    	{
    		folder.y += ( folder.height - ((FolderIcon3D) folder).getIconBmpHeight() );
    	}
    	//xiatian add end
    	
    	//Log.d("launcher", "x,y="+folder.x+","+folder.y+" width,height="+folder.width+","+folder.height);
//    	if (preview) {
//    		Color color = folder.getColor();
//    		color.a = 0.5f;
//			folder.setColor(color);
//    	}
//    	
//    	addtoSuperClass(folder);
    	super.addViewAt(0, folder);
    	if (preview) {
    		FolderLargeAnim(0.3f, folder);
    	}
 //    	this.setTag(folder);
//		viewParent.onCtrlEvent(this, MSG_ADD_DRAGLAYER);
    	
    	return folder;
    }
    
    int testCellapproach(View3D dyna, View3D quiet, int x, int y) {
    	int res = 0;
    	boolean isFolder = false;
    	
    	if (dyna == null || quiet == null)
    		return 1;
    	
    	if (!(quiet instanceof ViewCircled3D))
    		return 1;
    	
    	Vector2 v1 = new Vector2();
    	dyna.toAbsoluteCoords(v1);
    	Vector2 v2 = new Vector2();
    	quiet.toAbsoluteCoords(v2);
    	
    	Log.v("test", "dyna x, y = " + v1.x + ", " + v1.y);
    	Log.v("test", "quiet x, y = " + v2.x + ", " + v2.y);
    	
    	int xOffset = (int) (v2.x + quiet.width / 2 - v1.x - dyna.width / 2);
    	int yOffset = (int) (v2.y + quiet.height /2 - v1.y - dyna.height /2);
    	

    		if ((int)dyna.width - CELL_SENSITIVE_DIP > Math.abs(xOffset))
    			isFolder = true;
    		else 
    			isFolder = false;

    	if (isFolder && (int)dyna.height - CELL_SENSITIVE_DIP > Math.abs(yOffset)) {
    		
    	} else
    		isFolder = false;
    	
    	if (isFolder)
    		res = CELL_MAKE_FOLDER;
    	else
    		res = 1;
    	
    	return res;
    }
    
    int drapGetSpanX(View3D view) {
    	ItemInfo itemInfo = ((IconBase3D)view).getItemInfo();
    	
    	if (itemInfo != null && itemInfo.spanX != 0)
    		return itemInfo.spanX;
    	else
    		return ((int)view.width - 1) / mCellWidth + 1;
    }
    
    int drapGetSpanY(View3D view) {
    	ItemInfo itemInfo = ((IconBase3D)view).getItemInfo();
    	
    	if (itemInfo != null && itemInfo.spanY != 0)
    		return itemInfo.spanY;
    	else
    		return ((int)view.height - 1) / mCellHeight + 1;
    }
    
    /* for efficient*/
    private int[] mTargetCell = new int[2];
    private int[] mTargetPoint = new int[2];
    
    private int[]mTempCell = new int[2];
    
    void clearDragTemp() {
    	mTargetCell[0] = 0;
    	mTargetCell[1] = 0;
    	mTargetPoint[0] = 0;
    	mTargetPoint[1] = 0;
    	mTempCell[0] = 0;
    	mTempCell[1] = 0;
    }
    
    /**
     * drop type:
     * ViewCircled3D: icon
     * FolderIcon3D:folder
     * Widget:
     * Widget3D:
     * @param view
     * @param h
     * @param v
     * @return
     */
    boolean testIfViewIsOccupy(View3D view, int h, int v, int spanX, int spanY) {
//    	boolean res = false;
    	if (view == null)
    		return false;
    	
    	View3D quiet = cellViewList[h][v];
    	
    	if (quiet == null)
    		return false;
    	
    	return true;
//    		}
//    	}
    	
//    	if (view instanceof ViewCircled3D) {
//    		if (quiet instanceof Widget || quiet instanceof Widget3D) {
//    			return true;
//    		}
//    	}
//    	
//    	if (view instanceof Widget || view instanceof Widget3D) {
//    		if (quiet instanceof ViewCircled3D || quiet instanceof Widget || quiet instanceof Widget3D
//    				|| quiet instanceof FolderIcon3D
//    				) {
//    			return true;
//    		}
//    	}
//    	
//    	if (view instanceof FolderIcon3D) {
//    		if (quiet instanceof ViewCircled3D || quiet instanceof Widget || quiet instanceof Widget3D ||
//    				quiet instanceof FolderIcon3D
//    				) {
//    			return true;
//    		}
//    	}
//    		
//    	return res;
    }
    
    View3D dropReflectView(View3D view, int x, int y) {
    	//Log.d("launcher", "dropReflectView");
    	if(!(view instanceof Icon3D)) return null;
    	View3D temp;
		int spanX = drapGetSpanX(view);
		int spanY = drapGetSpanY(view);
		
		mTargetCell = findNearestArea(x, y, spanX, spanY, mTargetCell);
		
		if (testIfViewIsOccupy(view, mTargetCell[0], mTargetCell[1], spanX, spanY))
			return null;
		
		temp = view.clone();
		cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
		temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
		temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
		Color color = temp.getColor();
		color.a = 0.5f;
		temp.setColor(color);
		addtoSuperClass(temp);
		reflectView.add(temp);
			
    	return temp;
    }
    
    View3D reflect9PatchView(View3D view, int x, int y) {
    	//Log.d("launcher", "dropReflectView");
    	View3D temp;
		int spanX = drapGetSpanX(view);
		int spanY = drapGetSpanY(view);
		
		mTargetCell = findNearestArea((int)x, (int)y, spanX, spanY,
                spanX, spanY, null, true, mTargetCell, null, mOccupied);
		
		if (mTargetCell[0] < 0 || mTargetCell[1] < 0 || mTargetCell[0] >= mCountX || mTargetCell[1] >= mCountY) {
			return null;
		}
		
		temp = new View3D("CellReflect");
		temp.setBackgroud(Workspace3D.reflectView);
		temp.setSize(view.width, view.height);
				
		cellToPoint(mTargetCell[0], mTargetCell[1], mTargetPoint);
		temp.x = mTargetPoint[0] + (spanX * mCellWidth - view.width) / 2;
		temp.y = mTargetPoint[1] + (spanY * mCellHeight - view.height) / 2;
		Color color = temp.getColor();
		color.a = 0.8f;
		temp.setColor(color);
		addtoSuperClass(temp);
		mflect9patchView = temp;
			
    	return temp;
    }
    
    void addtoSuperClass(View3D view) {
    	super.addView(view);
    }
    
    void removeFromSuperClass(View3D view) {
    	super.removeView(view);
    }
    
    private int[] findNearestAreaInDirection(int cellX, int cellY, int spanX, int spanY, 
            int[] direction,boolean[][] occupied,
            boolean blockOccupied[][], int[] result) {
        // Keep track of best-scoring drop area
        final int[] bestXY = result != null ? result : new int[2];
        bestXY[0] = -1;
        bestXY[1] = -1;
        float bestDistance = Float.MAX_VALUE;

        // We use this to march in a single direction
        if ((direction[0] != 0 && direction[1] != 0) ||
                (direction[0] == 0 && direction[1] == 0)) {
            return bestXY;
        }

        // This will only incrememnet one of x or y based on the assertion above
        int x = cellX + direction[0];
        int y = cellY + direction[1];
        while (x >= 0 && x + spanX <= mCountX && y >= 0 && y + spanY <= mCountY) {

            boolean fail = false;
            for (int i = 0; i < spanX; i++) {
                for (int j = 0; j < spanY; j++) {
                    if (occupied[x + i][y + j] && (blockOccupied == null || blockOccupied[i][j])) {
                        fail = true;                    
                    }
                }
            }
            if (!fail) {
                float distance = (float)
                        Math.sqrt((x - cellX) * (x - cellX) + (y - cellY) * (y - cellY));
                if (Float.compare(distance,  bestDistance) < 0) {
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

    private boolean addViewToTempLocation(View v, Rect rectOccupiedByPotentialDrop,
            int[] direction, ItemConfiguration currentState) {
        CellAndSpan c = currentState.map.get(v);
        boolean success = false;
        markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, false);
        markCellsForRect(rectOccupiedByPotentialDrop, mTmpOccupied, true);

        findNearestArea(c.x, c.y, c.spanX, c.spanY, direction, mTmpOccupied, null, mTempLocation);

        if (mTempLocation[0] >= 0 && mTempLocation[1] >= 0) {
            c.x = mTempLocation[0];
            c.y = mTempLocation[1];
            success = true;

        }
        markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, true);
        return success;
    }

    // This method looks in the specified direction to see if there is an additional view
    // immediately adjecent in that direction
//    private boolean addViewInDirection(ArrayList<View> views, Rect boundingRect, int[] direction,
//            boolean[][] occupied, View dragView, ItemConfiguration currentState) {
//        boolean found = false;
//
//        int childCount = mShortcutsAndWidgets.getChildCount();
//        Rect r0 = new Rect(boundingRect);
//        Rect r1 = new Rect();
//
//        int deltaX = 0;
//        int deltaY = 0;
//        if (direction[1] < 0) {
//            r0.set(r0.left, r0.top - 1, r0.right, r0.bottom);
//            deltaY = -1;
//        } else if (direction[1] > 0) {
//            r0.set(r0.left, r0.top, r0.right, r0.bottom + 1);
//            deltaY = 1;
//        } else if (direction[0] < 0) {
//            r0.set(r0.left - 1, r0.top, r0.right, r0.bottom);
//            deltaX = -1;
//        } else if (direction[0] > 0) {
//            r0.set(r0.left, r0.top, r0.right + 1, r0.bottom);
//            deltaX = 1;
//        }
//
//        for (int i = 0; i < childCount; i++) {
//            View child = mShortcutsAndWidgets.getChildAt(i);
//            if (views.contains(child) || child == dragView) continue;
//            CellAndSpan c = currentState.map.get(child);
//
//            LayoutParams lp = (LayoutParams) child.getLayoutParams();
//            r1.set(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
//            if (Rect.intersects(r0, r1)) {
//                if (!lp.canReorder) {
//                    return false;
//                }
//                boolean pushed = false;
//                for (int x = c.x; x < c.x + c.spanX; x++) {
//                    for (int y = c.y; y < c.y + c.spanY; y++) {
//                        boolean inBounds = x - deltaX >= 0 && x -deltaX < mCountX
//                                && y - deltaY >= 0 && y - deltaY < mCountY;
//                        if (inBounds && occupied[x - deltaX][y - deltaY]) {
//                            pushed = true;
//                        }
//                    }
//                }
//                if (pushed) {
//                    views.add(child);
//                    boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
//                    found = true;
//                }
//            }
//        }
//        return found;
//    }

    private boolean addViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop,
            int[] direction, boolean push, View dragView, ItemConfiguration currentState) {
        if (views.size() == 0) return true;

        boolean success = false;
        Rect boundingRect = null;
        // We construct a rect which represents the entire group of views passed in
        for (View v: views) {
            CellAndSpan c = currentState.map.get(v);
            if (boundingRect == null) {
                boundingRect = new Rect(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            } else {
                boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            }
        }

        @SuppressWarnings("unchecked")
        ArrayList<View> dup = (ArrayList<View>) views.clone();
        // We try and expand the group of views in the direction vector passed, based on
        // whether they are physically adjacent, ie. based on "push mechanics".
//        while (push && addViewInDirection(dup, boundingRect, direction, mTmpOccupied, dragView,
//                currentState)) {
//        }

        // Mark the occupied state as false for the group of views we want to move.
        for (View v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, false);
        }

        boolean[][] blockOccupied = new boolean[boundingRect.width()][boundingRect.height()];
        int top = boundingRect.top;
        int left = boundingRect.left;
        // We mark more precisely which parts of the bounding rect are truly occupied, allowing
        // for tetris-style interlocking.
        for (View v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.x - left, c.y - top, c.spanX, c.spanY, blockOccupied, true);
        }

        markCellsForRect(rectOccupiedByPotentialDrop, mTmpOccupied, true);

        if (push) {
            findNearestAreaInDirection(boundingRect.left, boundingRect.top, boundingRect.width(),
                    boundingRect.height(), direction, mTmpOccupied, blockOccupied, mTempLocation);
        } else {
            findNearestArea(boundingRect.left, boundingRect.top, boundingRect.width(),
                    boundingRect.height(), direction, mTmpOccupied, blockOccupied, mTempLocation);
        }

        // If we successfuly found a location by pushing the block of views, we commit it
        if (mTempLocation[0] >= 0 && mTempLocation[1] >= 0) {
            int deltaX = mTempLocation[0] - boundingRect.left;
            int deltaY = mTempLocation[1] - boundingRect.top;
            for (View v: dup) {
                CellAndSpan c = currentState.map.get(v);
                c.x += deltaX;
                c.y += deltaY;
            }
            success = true;
        }

        // In either case, we set the occupied array as marked for the location of the views
        for (View v: dup) {
            CellAndSpan c = currentState.map.get(v);
            markCellsForView(c.x, c.y, c.spanX, c.spanY, mTmpOccupied, true);
        }
        return success;
    }
    
    private class ItemConfiguration {
        HashMap<View, CellAndSpan> map = new HashMap<View, CellAndSpan>();
        boolean isSolution = false;
        int dragViewX, dragViewY, dragViewSpanX, dragViewSpanY;

        int area() {
            return dragViewSpanX * dragViewSpanY;
        }
    }

    private class CellAndSpan {
        int x, y;
        int spanX, spanY;

        public CellAndSpan(int x, int y, int spanX, int spanY) {
            this.x = x;
            this.y = y;
            this.spanX = spanX;
            this.spanY = spanY;
        }
    }
    
    private void markCellsForRect(Rect r, boolean[][] occupied, boolean value) {
        markCellsForView(r.left, r.top, r.width(), r.height(), occupied, value);
    }
    
    /**
     * Computes a bounding rectangle for a range of cells
     *
     * @param cellX X coordinate of upper left corner expressed as a cell position
     * @param cellY Y coordinate of upper left corner expressed as a cell position
     * @param cellHSpan Width in cells
     * @param cellVSpan Height in cells
     * @param resultRect Rect into which to put the results
     */
    public void cellToRect(int cellX, int cellY, int cellHSpan, int cellVSpan, RectF resultRect) {
        final int cellWidth = mCellWidth;
        final int cellHeight = mCellHeight;
        final int widthGap = mWidthGap;
        final int heightGap = mHeightGap;

        final int hStartPadding = getPaddingLeft();
        final int vStartPadding = getPaddingBottom();

        int width = cellHSpan * cellWidth + ((cellHSpan - 1) * widthGap);
        int height = cellVSpan * cellHeight + ((cellVSpan - 1) * heightGap);

        int x = hStartPadding + cellX * (cellWidth + widthGap);
        int y = vStartPadding + cellY * (cellHeight + heightGap);

        resultRect.set(x, y, x + width, y + height);
    }

    /**
     * Computes the required horizontal and vertical cell spans to always
     * fit the given rectangle.
     *
     * @param width Width in pixels
     * @param height Height in pixels
     * @param result An array of length 2 in which to store the result (may be null).
     */
    public static int[] rectToCell(int width, int height, int[] result) {
        return rectToCell(iLoongLauncher.getInstance().getResources(), width, height, result);
    }

    public static int[] rectToCell(Resources resources, int width, int height, int[] result) {
    	int smallerSize = Math.min(R3D.Workspace_cell_each_width_ori, R3D.Workspace_cell_each_height);

        // Always round up to next largest cell
        int spanX = (width + smallerSize) / smallerSize;
        int spanY = (height + smallerSize) / smallerSize;
 
        if (result == null) {
            return new int[] { spanX, spanY };
        }
        result[0] = spanX;
        result[1] = spanY;
        return result;
//    	// Always assume we're working with the smallest span to make sure we
//        // reserve enough space in both orientations.
//        int actualWidth = iLoongLauncher.WORKSPACE_CELL_WIDTH;
//        int actualHeight = iLoongLauncher.WORKSPACE_CELL_HEIGHT;
//        int smallerSize = Math.min(actualWidth, actualHeight);
//
//        // Always round up to next largest cell
//        int spanX = (int) Math.ceil(width / (float) smallerSize);
//        int spanY = (int) Math.ceil(height / (float) smallerSize);
//
//        if (result == null) {
//            return new int[] { spanX, spanY };
//        }
//        result[0] = spanX;
//        result[1] = spanY;
//        return result;
    }

    public int[] cellSpansToSize(int hSpans, int vSpans) {
        int[] size = new int[2];
        size[0] = hSpans * mCellWidth + (hSpans - 1) * mWidthGap;
        size[1] = vSpans * mCellHeight + (vSpans - 1) * mHeightGap;
        return size;
    }

    /**
     * Calculate the grid spans needed to fit given item
     */
    public void calculateSpans(View3D view) {
//        final int minWidth;
//        final int minHeight;
//
//        if (view instanceof Widget3D) {
//            minWidth = (int) view.width;
//            minHeight = (int) view.height;
//        } else if (view instanceof PendingAddWidgetInfo) {
//            minWidth = ((PendingAddWidgetInfo) info).minWidth;
//            minHeight = ((PendingAddWidgetInfo) info).minHeight;
//        } else {
//            // It's not a widget, so it must be 1x1
//            view.spanX = view.spanY = 1;
//            return;
//        }
//        int[] spans = rectToCell(minWidth, minHeight, null);
//        view.spanX = spans[0];
//        view.spanY = spans[1];
    }

    /**
     * Find the first vacant cell, if there is one.
     *
     * @param vacant Holds the x and y coordinate of the vacant cell
     * @param spanX Horizontal cell span.
     * @param spanY Vertical cell span.
     *
     * @return True if a vacant cell was found
     */
    public boolean getVacantCell(int[] vacant, int spanX, int spanY) {

        return findVacantCell(vacant, spanX, spanY, mCountX, mCountY, mOccupied);
    }

    static boolean findVacantCell(int[] vacant, int spanX, int spanY,
            int xCount, int yCount, boolean[][] occupied) {

        for (int y = 0; y < yCount; y++) {
            for (int x = 0; x < xCount; x++) {
                boolean available = !occupied[x][y];
out:            for (int i = x; i < x + spanX - 1 && x < xCount; i++) {
                    for (int j = y; j < y + spanY - 1 && y < yCount; j++) {
                        available = available && !occupied[i][j];
                        if (!available) break out;
                    }
                }

                if (available) {
                    vacant[0] = x;
                    vacant[1] = y;
                    return true;
                }
            }
        }

        return false;
    }

    private void clearOccupiedCells() {
        for (int x = 0; x < mCountX; x++) {
            for (int y = 0; y < mCountY; y++) {
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
     * Given a view, determines how much that view can be expanded in all directions, in terms of
     * whether or not there are other items occupying adjacent cells. Used by the
     * AppWidgetResizeFrame to determine how the widget can be resized.
     */
    public void getExpandabilityArrayForView(View view, int[] expandability) {
        final LayoutParams lp = (LayoutParams) view.getLayoutParams();
        boolean flag;

        expandability[LEFT] = 0;
        for (int x = lp.cellX - 1; x >= 0; x--) {
            flag = false;
            for (int y = lp.cellY; y < lp.cellY + lp.cellVSpan; y++) {
                if (mOccupied[x][y]) flag = true;
            }
            if (flag) break;
            expandability[LEFT]++;
        }

        expandability[TOP] = 0;
        for (int y = lp.cellY - 1; y >= 0; y--) {
            flag = false;
            for (int x = lp.cellX; x < lp.cellX + lp.cellHSpan; x++) {
                if (mOccupied[x][y]) flag = true;
            }
            if (flag) break;
            expandability[TOP]++;
        }

        expandability[RIGHT] = 0;
        for (int x = lp.cellX + lp.cellHSpan; x < mCountX; x++) {
            flag = false;
            for (int y = lp.cellY; y < lp.cellY + lp.cellVSpan; y++) {
                if (mOccupied[x][y]) flag = true;
            }
            if (flag) break;
            expandability[RIGHT]++;
        }

        expandability[BOTTOM] = 0;
        for (int y = lp.cellY + lp.cellVSpan; y < mCountY; y++) {
            flag = false;
            for (int x = lp.cellX; x < lp.cellX + lp.cellHSpan; x++) {
                if (mOccupied[x][y]) flag = true;
            }
            if (flag) break;
            expandability[BOTTOM]++;
        }
    }
    
    public void onMove(View3D view, int newCellX, int newCellY, int newSpanX, int newSpanY) {
        markCellsAsUnoccupiedForView(view);
        markCellsForView(newCellX, newCellY, newSpanX, newSpanY, mOccupied, true);
    }
    
    public void markCellsAsUnoccupiedForView(View3D view) {
        markCellsAsUnoccupiedForView(view, mOccupied);
    }
    
    public void markCellsAsUnoccupiedForView(View3D view, boolean occupied[][]) {
        if (view == null) return;
//        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        markCellsForView((int)view.x, (int)view.y, (int)view.width, (int)view.height, occupied, false);
    }
    
    public void markCellsAsOccupiedForView(View3D view) {
        markCellsAsOccupiedForView(view, mOccupied);
    }
    public void markCellsAsOccupiedForView(View3D view, boolean[][] occupied) {
        if (view == null) return;
//        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        markCellsForView((int)view.x, (int)view.y, (int)view.width, (int)view.height, occupied, true);
    }

    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied,
            boolean value) {
        if (cellX < 0 || cellY < 0) return;
        for (int x = cellX; x < cellX + spanX && x < mCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCountY; y++) {
                occupied[x][y] = value;
            }
        }
    }
    
    //need repair the follow two method mPaddingLeft + mPaddingRight

    public int getDesiredWidth() {
        return 0 + (mCountX * mCellWidth) +
                (Math.max((mCountX - 1), 0) * mWidthGap);
    }

    public int getDesiredHeight()  {
        return 0 + (mCountY * mCellHeight) +
                (Math.max((mCountY - 1), 0) * mHeightGap);
    }

    public boolean isOccupied(int x, int y) {
        if (x < mCountX && y < mCountY) {
            return mOccupied[x][y];
        } else {
            throw new RuntimeException("Position exceeds the bound of this CellLayout");
        }
    }


    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof CellLayout3D.LayoutParams;
    }


    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new CellLayout3D.LayoutParams(p);
    }

    public static class CellLayoutAnimationController extends LayoutAnimationController {
        public CellLayoutAnimationController(Animation animation, float delay) {
            super(animation, delay);
        }

        @Override
        protected long getDelayForView(View view) {
            return (int) (Math.random() * 150);
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
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
         * Indicates that the temporary coordinates should be used to layout the items
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
         * Indicates whether the item will set its x, y, width and height parameters freely,
         * or whether these will be computed based on cellX, cellY, cellHSpan and cellVSpan.
         */
        public boolean isLockedToGrid = true;

        /**
         * Indicates whether this item can be reordered. Always true except in the case of the
         * the AllApps button.
         */
        public boolean canReorder = true;

        // X coordinate of the view in the layout.
        @ViewDebug.ExportedProperty
        int x;
        // Y coordinate of the view in the layout.
        @ViewDebug.ExportedProperty
        int y;

        boolean dropped;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.cellX = source.cellX;
            this.cellY = source.cellY;
            this.cellHSpan = source.cellHSpan;
            this.cellVSpan = source.cellVSpan;
        }

        public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.cellX = cellX;
            this.cellY = cellY;
            this.cellHSpan = cellHSpan;
            this.cellVSpan = cellVSpan;
        }

        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap) {
            if (isLockedToGrid) {
                final int myCellHSpan = cellHSpan;
                final int myCellVSpan = cellVSpan;
                final int myCellX = useTmpCoords ? tmpCellX : cellX;
                final int myCellY = useTmpCoords ? tmpCellY : cellY;

                width = myCellHSpan * cellWidth + ((myCellHSpan - 1) * widthGap) -
                        leftMargin - rightMargin;
                height = myCellVSpan * cellHeight + ((myCellVSpan - 1) * heightGap) -
                        topMargin - bottomMargin;
                x = (int) (myCellX * (cellWidth + widthGap) + leftMargin);
                y = (int) (myCellY * (cellHeight + heightGap) + topMargin);
            }
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ")";
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return y;
        }
    }

    // This class stores info for two purposes:
    // 1. When dragging items (mDragInfo in Workspace), we store the View, its cellX & cellY,
    //    its spanX, spanY, and the screen it is on
    // 2. When long clicking on an empty cell in a CellLayout, we save information about the
    //    cellX and cellY coordinates and which page was clicked. We then set this as a tag on
    //    the CellLayout that was long clicked
    static final class CellInfo {
        View cell;
        int cellX = -1;
        int cellY = -1;
        int spanX;
        int spanY;
        int screen;
        long container;

        @Override
        public String toString() {
            return "Cell[view=" + (cell == null ? "null" : cell.getClass())
                    + ", x=" + cellX + ", y=" + cellY + "]";
        }
    }

    public boolean lastDownOnOccupiedCell() {
        return mLastDownOnOccupiedCell;
    }
    
    public void resetCurrFocus(){
    	//switchOff();
    	Log.v("onCtrlEvent", "resetCurrFocus");
    	this.resetInfo();
		this.setInvisible();
		setHide();
		//HotSeat3D.setInvisible();
	}
    
    public void disableCurPageFocus(){
		Log.v("touch", "switchOff");
		touchEvent =true;
		setHide();
		setInvisible();
    }
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		//zqh start 
		//
		Log.v("background", "SetMenuDesktop.origin11"+SetMenuDesktop.origin);
		if(DefaultLayout.keypad_event_of_focus){
			
			Log.v("track", "keyDown");
		if(SetMenuDesktop.origin){
			
			
			return  false;
		}
		if(keycode == KeyEvent.KEYCODE_MENU ||keycode == KeyEvent.KEYCODE_HOME){
			this.resetInfo();
			this.setInvisible();
			this.setHide();
			//HotSeat3D.setInvisible();
			return true;//super.keyDown(keycode);
		}
		
//		if(HotSeat3D.isOccupy){
//			return true;
//		}
		
		if(iconExist==true){// judge whether any icon is on desktop.
			if(keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP || keycode == KeyEvent.KEYCODE_DPAD_LEFT || keycode == KeyEvent.KEYCODE_DPAD_RIGHT){
				touchEvent =false;
				if(hideFocus==true){
					setUnhide();	
					setVisible();
					return true;
					//return super.keyDown(keycode);
				}
			}
			
			
			if(keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
				onKeySelect();
				return true;//super.keyDown(keycode);
			}
			//updateFocus(keycode);
		}
		
		updateFocus(keycode);
		//zqh end
		}
		return super.keyDown(keycode);
		
	}
	
	
	public boolean findItem(int row){
		hasItem =false;
		for(int i=row-1;i>=0;i--){
			for(int j=0;j<mCountX;j++){
				if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
					hasItem =true;
					break;
				}
			}
			if(hasItem)
				break;
		}
		
		return hasItem;
		 
	}
	
	public void onKeySelect(){
		if((hideFocus==false)&&(firstlyCome==false)){
			cellViewList[cursorX][cursorY].onClick(iconWidth/2, iconHeight/2);
		}
	}
	
	public void processKeyDown(int cursor){
		boolean preFind =false;
		boolean nextFind=false;
		View3D view =null;
		int leftTemp=0;
		int rightTemp=0;
		int preDistance =0;
		int nextDistance =1;
		int preTemp =0;
		int nextTemp =0;
		
		//往上移一
		for(int i=cursor-1;i>=0;i--){
			preDistance =0;
			nextDistance =1;
			preTemp =0;
			nextTemp =0;
			preFind =false;
			nextFind=false;
			//判断该行前面第一个有效图标的位置
			for(int j=cursorX;j>=0;j--){
				//
				if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
					preFind =true;
					preTemp =j;//获得列的位置
					leftTemp=i;
					break;
				}
				else{
					preDistance ++;
				}
			}
			//判断是否已经找到最合适的位置
			//if(!(find==true)&&(preDistance==0)){
				//判断该行后面第一个有效图标位�?				
			for(int j=cursorX+1;j<mCountX;j++){
					//if(cursorX==mCountY-1) break;//on last column 
					if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						nextTemp =j;//获得列的位置
						nextFind =true;
						rightTemp=i;
						break;
					}
					else{
						nextDistance++;
					}
				}
			//}
			//找到具体的位置了，跳出所有的查找
			if((preFind==true) ||(nextFind==true))break;
		}
		if((nextFind == true)&&(preFind==true))
		{
			// there is a bug here .
			if(preDistance<=nextDistance){
				setLocationInfo(preTemp,leftTemp);
			}
			else{
				setLocationInfo(nextTemp,rightTemp);
			}
			
		}
		else if(preFind==true){setLocationInfo(preTemp,leftTemp);}
		else if(nextFind==true){setLocationInfo(nextTemp,rightTemp);}
		else{
			Log.v("focus", "jump to hotseatBar");
		}
	}
	public void processKeyUp(int cursor){
		
		View3D view =null;
		int leftTemp=0;
		int rightTemp=0;
		int preDistance =0;
		int nextDistance =1;
		int preTemp =0;
		int nextTemp =0;
		boolean preFind =false;
		boolean nextFind=false;
		//往上移一�?		
		for(int i=cursor+1;i<mCountY;i++){
			preDistance =0;
			nextDistance =1;
			preTemp =0;
			nextTemp =0;
			preFind =false;
			nextFind=false;
			//判断该行前面第一个有效图标的位置
			for(int j=cursorX;j>=0;j--){
				//
				if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
					preFind =true;
					preTemp =j;//获得列的位置
					leftTemp=i;
					break;
				}
				else{
					preDistance ++;
				}
			}
			//判断是否已经找到最合适的位置
			//if(!(find==true)&&(preDistance==0)){
				//判断该行后面第一个有效图标位�?				
			for(int j=cursorX+1;j<mCountX;j++){
					//if(cursorX==mCountY-1) break;//on last column 
					if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						nextTemp =j;//获得列的位置
						nextFind =true;
						rightTemp=i;
						break;
					}
					else{
						nextDistance++;
					}
				}
			//}
			//找到具体的位置了，跳出所有的查找
			if((preFind==true) ||(nextFind==true))break;
		}
		if((nextFind == true)&&(preFind==true))
		{
			
			if(preDistance<=nextDistance){
				setLocationInfo(preTemp,leftTemp);
			}
			else{
				setLocationInfo(nextTemp,rightTemp);
			}
			
		}
		else if(preFind==true){setLocationInfo(preTemp,leftTemp);}
		else if(nextFind==true){setLocationInfo(nextTemp,rightTemp);}
	}
	public void processKeyLeft(int cursor){
		boolean find =false;
		for(int i=cursor-1;i>=0;i--){
			if ((cellViewList[i][cursorY] instanceof Icon3D)||((cellViewList[i][cursorY] instanceof FolderIcon3D))){
				setLocationInfo(i,cursorY);
				find =true;
				break;
			}
		}
		if(find==false){
			onScroll(LEFT_DIR);
		}
		
	}
	public void processKeyRight(int cursor){
		boolean find =false;
		for(int i=cursor+1;i<mCountX;i++){
			if ((cellViewList[i][cursorY] instanceof Icon3D)||((cellViewList[i][cursorY] instanceof FolderIcon3D))){
				setLocationInfo(i,cursorY);
				find =true;
				break;
			}
		}
	
		if(find==false){
			onScroll(RIGHT_DIR);
		}
		
		
	}
	public void setLocationInfo(int column,int row){
		Log.v("focus","setLocationInfo");
		cursorX=column;cursorY=row;
		locationX=cellViewList[column][row].x;
		locationY=cellViewList[column][row].y;
		iconHeight=cellViewList[column][row].height;
		iconWidth=cellViewList[column][row].width;
		
		jumpFrom();
		findItem(row);
		
	}
	
	public int getNextPageSize(){
	//	viewParent.getIndexInParent()
		return ((Workspace3D)viewParent).getNextCellLayoutCount();
		 
	}
	
	public int getPreviousPageSize(){
		return ((Workspace3D)viewParent).getPreviousCellLayoutCount();
	}
	/**
	 * 
	 * @param  direction: to judge which direction(X or Y) is being operated on .
	 * 
	 * **/
	private void getValidIcon(int direction,int cursor){

		//我靠！！！！！！第一维表示的是列
		switch(direction){
		case 1:
			processKeyUp(cursor);
			break;
		case 2:
			processKeyDown(cursor);
			break;
		case 3:
			processKeyLeft(cursor);
			break;
		case 4:
			processKeyRight(cursor);
			break;
		}
	
		
	}
	public void setVisible(){
		if(touchEvent==false)
		{
			Log.v("track", "setVisible");
		    firstlyCome =false;
		}
	}
	public void setHide(){
		hideFocus=true;
	}
	public void setUnhide(){
		hideFocus=false;
	}
	public void setInvisible(){
		firstlyCome =true;
	}
	public void hideFocus(){
		changeFocus();
		setInvisible();
	}
	public void switchOff(){
		Log.v("touch", "switchOff");
		touchEvent =true;
		setHide();
		setInvisible();
		//HotMainMenuView3D.onFocus=false;
//		HotSeat3D.setInvisible();
	}
	public void switchOn(){
		setUnhide();
		setVisible();
	}
	public void onScroll(String page){
		keyPadInvoked =true;
		onDropLeave();/*sometimes it is necessary*/
		direction=page;
		if(page.equals(RIGHT_DIR)){
			//if(getNextPageSize()<=0)
			if(!((Workspace3D)viewParent).hasNextPage())
				page="first";
		}
		else if(page.equals(LEFT_DIR)){
			//if(getPreviousPageSize()<=0)
			if(!((Workspace3D)viewParent).hasPreviousPage())
				page="last";
		}
		setInvisible();
		this.setTag(page);
		viewParent.onCtrlEvent(this, MSG_PAGE_TO);
		
	}

	public void refreshLocation(int index){
		
		Log.v("focus", "fresh index"+index);
		View3D view =null;
		int leftTemp=0;
		int rightTemp=0;
		int preDistance =0;
		int nextDistance =1;
		int preTemp =0;
		int nextTemp =0;
		boolean preFind =false;
		boolean nextFind=false;
		int realX=0;
		int realY=0;
		iconExist=false;
		
		if(index ==R3D.hot_dock_icon_number){
			for(int i=0;i<mCountY;i++){
				for(int j=mCountX-1;j>=0;j--){
				if((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						realX=i;
						realY=j;
						preFind=true;
						iconExist=true;
						break;
					}
				}
				if(preFind){
					break;
				}
			
			}
			//return;
		}
		else{
			for(int i=0;i<mCountY;i++)
			{for(int j=index;j>=0;j--){
				if((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						preFind=true;
						preTemp=j;
						leftTemp=i;
						break;
					}
				}
				
				for(int j=index+1;j<mCountX;j++){
					if((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						nextFind=true;
						nextTemp=j;
						rightTemp=i;
						break;
					}
				}
				
				if((preFind)||(nextFind)){
					iconExist=true;
								if(preFind&&nextFind){
						
						if(leftTemp==rightTemp){
														if(preTemp<=nextTemp){
								realX=leftTemp;							realY=preTemp;						}
							else{
								realX=rightTemp;							realY=nextTemp;						}
						}
					}
					else if(preFind){
						realX=leftTemp;						realY=preTemp;					}
					else{
						realX=rightTemp;					realY=nextTemp;					}
					break;
				}
				Log.v("focus", "times");
			}
		}
	
		if(preFind||nextFind)
			setLocationInfo(realY,realX);
		
	}
	
	public void changeFocus(){
		View3D view =null;
		int leftTemp=0;
		int rightTemp=0;
		int preDistance =0;
		int nextDistance =1;
		int preTemp =0;
		int nextTemp =0;
		boolean preFind =false;
		boolean nextFind=false;
		iconExist=false;
			
		if(RIGHT_DIR.equals(direction)){
			for(int i=cursorY;i<mCountY;i++){
				//for(int j=mCountX-1;j>=0;j--){
				for(int j=0;j<mCountX;j++){
					if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						preFind =true;
						preTemp =j;
						leftTemp=i;
						iconExist=true;
						break;
					}
				}
				if(preFind==true)
					break;
			}
		
			for(int i=cursorY-1;i>=0;i--){				
				//for(int j=mCountX-1;j>=0;j--){
				for(int j=0;j<mCountX;j++){
						if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
							nextTemp =j;
							nextFind =true;
							rightTemp=i;
							iconExist=true;
							break;
						}
	
					}
				if(nextFind==true)
					break;
			}
		}
		else if(LEFT_DIR.equals(direction)){
			for(int i=cursorY;i<mCountY;i++){
				for(int j=mCountX-1;j>=0;j--){
				//for(int j=0;j<mCountX;j++){
					if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
						preFind =true;
						preTemp =j;
						leftTemp=i;
						iconExist=true;
						break;
					}
				}
				if(preFind==true)
					break;
			}
		
			for(int i=cursorY-1;i>=0;i--){				
				for(int j=mCountX-1;j>=0;j--){
				//for(int j=0;j<mCountX;j++){
						if ((cellViewList[j][i] instanceof Icon3D)||((cellViewList[j][i] instanceof FolderIcon3D))){
							nextTemp =j;
							nextFind =true;
							rightTemp=i;
							iconExist=true;
							break;
						}
	
					}
				if(nextFind==true)
					break;
			}
		}
		//if((preFind==true) ||(nextFind==true))break;
		if(preFind==true){
			setLocationInfo(preTemp,leftTemp);
		}
		else if(nextFind == true){
			setLocationInfo(nextTemp,rightTemp);
		}
		
	}
	
	
	public void resetInfo(){
		Log.v("focus","resetInfo");
		iconExist=false;
		for(int i =mCountY-1;i>=0;i--){	
			for(int j=0;j<mCountX;j++){		
				if ((cellViewList[j][i] instanceof Icon3D)||(cellViewList[j][i] instanceof FolderIcon3D)){
					cursorX=j;cursorY=i;
					locationX=cellViewList[cursorX][cursorY].x;
					locationY=cellViewList[cursorX][cursorY].y;
					iconHeight=cellViewList[cursorX][cursorY].height;
					iconWidth=cellViewList[cursorX][cursorY].width;
					iconExist=true;
					jumpFrom();
					findItem(cursorY);
				//	setVisible();
					return;
				}
				else{
					//Log.v("zqh--"," !!!!!!!!");
					iconExist=false;
				}
			}
		}
		Log.v("zqh--","iconExist: "+iconExist);
	}
	
	
	public void jumpFrom(){
		
		currentX =cellViewList[cursorX][cursorY].x;
		currentY =cellViewList[cursorX][cursorY].y;
		if(cursorX==mCountX-1){
			CellLayout3D.origin=2;
		}
		else{
			CellLayout3D.origin=1;
		}
		Log.v("focus", "x :"+ currentX+ "y:"+currentY);
	}
	
	
	
	/**
	 * 
	 * @param 1:竖直向上
	 * @param 2:竖直向下
	 * @param 3:水平向左
	 * @param 4:水平向右
	 * 
	 * **/
	protected void updateFocus(int direction){
		int numCells = mCountX * mCountY;

		//JumpFrom();
		switch(direction){
			case  KeyEvent.KEYCODE_DPAD_UP:	
				if(cursorY>=mCountY-1){
					Log.v("focus", "updateFocus");
				}
//				else if(HotSeat3D.origin){
//					HotSeat3D.origin=false;
//					refreshLocation(HotSeat3D.realIndex);
//					this.firstlyCome=false;
//				}
				else{
					
					getValidIcon(1,cursorY);
				}
				break;
			
			case  KeyEvent.KEYCODE_DPAD_RIGHT:
				if(((cursorX+1)%mCountX )==0){
						onScroll(RIGHT_DIR);
					}
				else{
					getValidIcon(4,cursorX);
				}
				break;
			case  KeyEvent.KEYCODE_DPAD_DOWN:

					//if(cursorY>0){
					if(findItem(cursorY)){
						getValidIcon(2,cursorY);
						
					}
					else{
						//
						//if(!iconExist)
						//	return;
						iconExist =false;
//						HotSeat3D.isOccupy=true;
						//viewParent.onCtrlEvent(this, MSG_JUMP_TO_HOTSEAT);
						//Log.v("focus", "updateFocus down");
						//temFocus=(currentCell - startIndex)%numCells;
						//currentCell = temFocus+startIndex;
					}

				break;
			case  KeyEvent.KEYCODE_DPAD_LEFT:
					if(((cursorX+1)%mCountX)==1){
						onScroll("left");
					}
					else{
						getValidIcon(3,cursorX);
		
					}
				break;
				
			default : 
				break;
		}
		
		
	}
	/**zqh end**/
	//zhujieping add start
	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		if (sender instanceof FolderIcon3D) {
			switch (event_id) {
			
			case FolderIcon3D.MSG_BRING_TO_FRONT:
				if (sender != null && sender instanceof FolderIcon3D) {
					bringToFront(sender);
				}
			}
		} 
		return viewParent.onCtrlEvent(sender, event_id);
	}
	private void bringToFront(View3D view) {
		int toIndex = this.getChildCount() - 1;
		if (view != null) {
			removeFromSuperClass(view);
			super.addViewAt(toIndex, view);
		}
		// removeFromSuperClass(view);
	}
	//zhujieping add end
	
	private Tween FolderLargeTween = null;
	private Tween FolderSmallTween = null;
	private void FolderLargeAnim(float duration, View3D view3D) {
		if (view3D == null || duration == 0) {
			return;
		}
		if (FolderLargeTween != null) {
			return;
		}
		view3D.stopTween();
		view3D.setVisible(true);
		view3D.setScale(0, 0);
		FolderLargeTween = view3D
				.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, duration,
						1.2f, 1.2f, 0).setUserData(view3D).setCallback(this);
		

	}

	private void FolderSmallAnim(float duration, View3D view3D) {
		if (view3D == null || duration == 0) {
			return;
		}
		if (FolderSmallTween != null) {
			return;
		}
		view3D.stopTween();
		FolderSmallTween = view3D
				.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, duration,
						0, 0, 0).setUserData(view3D).setCallback(this);
	}

	

}
