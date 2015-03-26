package com.iLoong.launcher.SetupMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Graphics;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.PageContainer3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.Actions.Action;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SetupMenu.Actions.ShowDesktop;
import com.iLoong.launcher.SetupMenu.SetupMenu.SetupMenuItem;
import com.iLoong.launcher.SetupMenu.SetupMenu.SetupTabMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.cling.Cling;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.iLoong.launcher.Desktop3D.Log;

import android.os.Environment;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SetMenuDesktop extends FrameLayout implements View.OnClickListener, PagedView.ViewSwitchListener,
		View.OnKeyListener, Animation.AnimationListener {
	private SetupMenu mSetupMenu;
	public static ArrayList<SetupTabMenu> mTabMenus;
	private HashMap<Integer, ArrayList<SetupMenuItem>> mMenuItems;
	public static  Context mContext;
	private FrameLayout mdesktop;
	public static FrameLayout mpopwin,mbgwin;
	private MenuGridLayout mMenuGridLayout;
	private SetupMenuControl mSetMenuControl;

	private TranslateAnimation inanimation, outanimation;
	private AnimationSet mInAnimationSet, mOutAnimationSet;
	private boolean mClose = false;
	private boolean mAnimationClose = false;
	private ArrayList<Bitmap> mbitmaps = new ArrayList<Bitmap>();
	public static boolean origin=false;
	public static ImageView indicatorView ;
	public static ImageView indicatorBarView;
	public static int frameTopMargin =5;
	public static int cellTopMargin=0;
	
	//private int clingState = ClingManager.CLING_STATE_WAIT;
	//private ImageView clingPoint;
	//private Button clingOk;
	//private Cling cling;

	public SetMenuDesktop(Context context) {
		super(context);
		mContext = context;
		mSetMenuControl = new SetupMenuControl(this);
		setOnKeyListener(this);
		//clingState = ClingManager.getInstance().fireSettingCling();
	}

	public void Load() {
//		SendMsgToAndroid.sendHideWorkspaceMsg();
		mAnimationClose = false;
		mClose = false;
		StarInAnimation();
		//PagedView.mCurrentScreen
		SelectPage(0);
		setFocusable(true);
		ShowItem();
//		if(clingPoint != null){
//			Rect frame = new Rect();  
//			((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
//			int statusBarHeight = frame.top;   
//			((FrameLayout.LayoutParams)clingPoint.getLayoutParams()).topMargin = 15+statusBarHeight;
//		}
		
	}

	public boolean IsClose() {
		return mClose;
	}

	public void OnUnLoad() {
		if (mClose)
			return;
		mClose = true;
		if (mAnimationClose)
			StarOutAnimation();
			
	}

	public void Release() {
		for (int i = 0; i < mbitmaps.size(); i++) {
			Bitmap bmp = mbitmaps.get(i);
			if (bmp != null)
				bmp.recycle();
		}
		
	}

	void setSetupMenu(SetupMenu sm) {
		mSetupMenu = sm;
	}

	void setMenuItems(ArrayList<SetupTabMenu> tabmenus, HashMap<Integer, ArrayList<SetupMenuItem>> menuitems) {
		mTabMenus = tabmenus;
		mMenuItems = menuitems;
	}

	public int width() {
		return mSetupMenu.mWidth;
	}

	
	public int height() {
	    return (int) (mSetupMenu.mrows * mSetupMenu.mCellHeight+Tools.dip2px(mContext,frameTopMargin)*mSetupMenu.mScale*2);
//		return (int) (mSetupMenu.mrows * mSetupMenu.mCellHeight + Tools.dip2px(mContext,frameTopMargin)*mSetupMenu.mScale*2+
//		        Tools.dip2px(mContext,cellTopMargin)*mSetupMenu.mScale);
	}

	private void SelectPage(int page) {
	    PagedView.isInited=false;
		mMenuGridLayout.snapToScreen(page);
		PagedView.isInited=true;
		if(DefaultLayout.setup_menu_support_scroll_page){
		    PagedView.setIndicatorMargin(Integer.MAX_VALUE);
		}
	}

	private void StarInAnimation() {
		if(iLoongLauncher.getInstance().delaySetupMenu){
			iLoongLauncher.getInstance().delaySetupMenu = false;
			inanimation.setStartOffset(250);
		}
		else {
			inanimation.setStartOffset(0);
		}

		mpopwin.startAnimation(mInAnimationSet);
	}

	public void StarOutAnimation() {
		mpopwin.startAnimation(mOutAnimationSet);
	}

	public void LoadLayout() {
		int inatime = 250;
		int outtime = 250;
		mInAnimationSet = new AnimationSet(false);
		inanimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		inanimation.setDuration(inatime);
		mInAnimationSet.addAnimation(inanimation);
		mInAnimationSet.setAnimationListener(this);

		mOutAnimationSet = new AnimationSet(false);
		outanimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
		outanimation.setDuration(outtime);
		mOutAnimationSet.addAnimation(outanimation);
		mOutAnimationSet.setAnimationListener(this);

		mdesktop = new FrameLayout(mContext);
		LayoutParams desktoplp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		desktoplp.gravity = Gravity.BOTTOM;
		//desktoplp.setMargins(20, 0, 0, 0);
		mdesktop.setBackgroundColor(Color.TRANSPARENT);
		addView(mdesktop, desktoplp);
		mdesktop.setOnClickListener(this);
		mdesktop.setTag(Integer.valueOf(1));
//		if(clingState == ClingManager.CLING_STATE_SHOW){
//			clingPoint = new ImageView(mContext);
//			Drawable bg = mContext.getResources().getDrawable(R.drawable.sh);
//	    	clingPoint.setBackgroundDrawable(bg);
//	    	clingPoint.setOnClickListener(this);
//	    	FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
//	    			android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//	    	pointParams.gravity = Gravity.TOP|Gravity.RIGHT;
//	    	int x = 15;
//			int y = 15;
//	    	pointParams.setMargins(0, y, x, 0);
//	    	clingPoint.setLayoutParams(pointParams);
//	    	mdesktop.addView(clingPoint);
//		}

		this.setFocusable(true);
		mpopwin = new FrameLayout(mContext);
		FrameLayout.LayoutParams popwinlp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popwinlp.gravity = Gravity.BOTTOM;
		addView(mpopwin, popwinlp);
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)	
		{	
			//mpopwin.setBackgroundColor(Color.BLACK);
			int horMargin = mSetupMenu.mWidth/15;
			popwinlp.setMargins(horMargin, 0, horMargin, 0);
			mpopwin.setBackgroundDrawable(iLoongLauncher.getInstance().getResources().getDrawable(RR.drawable.tanchu));
		}
		else
		{
			
			Bitmap bg = null;
			try {
			    if(DefaultLayout.setup_menu_support_scroll_page&& mTabMenus.size()>1){
			        bg=ThemeManager.getInstance().getBitmap(SetupMenu.SETUPMENU_FOLDERNAME+"bg_ex.png");
			    }
			    else
				bg=ThemeManager.getInstance().getBitmap(SetupMenu.SETUPMENU_BG_CLOLOR);
				//bg = Tools.getImageFromInStream(iLoongLauncher.getInstance().getAssets().open(SetupMenu.SETUPMENU_BG_CLOLOR));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mbitmaps.add(bg);
			ImageView img = new ImageView(mContext);
			img.setImageBitmap(bg);
			img.setScaleType(ImageView.ScaleType.FIT_XY);
	    	FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(width(),
	    	        height());
	    	mpopwin.addView(img,pointParams);
		}
		
//		for (int i = 2; i < width()/bg.getWidth()-1; i++) {			
//			ImageView img = new ImageView(mContext);
//			img.setImageBitmap(bg);
//	    	FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
//	    			LayoutParams.FILL_PARENT);
//	    	pointParams.gravity = Gravity.TOP|Gravity.LEFT;
//	    	int x = i * bg.getWidth();
//			int y = 0;
//	    	pointParams.setMargins(x, y, 0, 0);
//	    	mpopwin.addView(img,pointParams);
//		}
	
		mbgwin = new FrameLayout(mContext);
		FrameLayout.LayoutParams popwinlp2 = new FrameLayout.LayoutParams(mSetupMenu.mWidth,
		        height());
				//mSetupMenu.mrows * mSetupMenu.mCellHeight);
		popwinlp2.gravity = Gravity.BOTTOM;
		mpopwin.addView(mbgwin, popwinlp2);
		
		Bitmap bg2 = null;
		try {
			bg2=ThemeManager.getInstance().getBitmap(SetupMenu.SETUPMENU_FOLDERNAME+"bg-2.png");
			//bg2 = Tools.getImageFromInStream(iLoongLauncher.getInstance().getAssets().open(SetupMenu.SETUPMENU_FOLDERNAME+"bg-2.png"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)	
		{
			
		}
		else
		{
		    bg2 = Tools.resizeBitmap(bg2, SetupMenu.mScale);
		}
		mbitmaps.add(bg2);
		
		
		mMenuGridLayout = new MenuGridLayout(mContext);
		mMenuGridLayout.setOrientation(LinearLayout.HORIZONTAL);

		FrameLayout.LayoutParams lpgrid = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		lpgrid.gravity = Gravity.TOP;
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)
		{
			lpgrid.topMargin =0;
		}
		else
		{
		  lpgrid.topMargin = (int) (8 * SetupMenu.mScale);
		}
		mpopwin.addView(mMenuGridLayout, lpgrid);

		
		
		int menusize = mTabMenus.size();

		for (int index = 0; index < menusize; index++) {
			SetupTabMenu sm = mTabMenus.get(index);
			ArrayList<SetupMenuItem> menuitems = mMenuItems.get(Integer.valueOf(sm.id));

			PageGridView mgv = new PageGridView(mContext);
			LinearLayout.LayoutParams mglp = new LinearLayout.LayoutParams(width(), mSetupMenu.mrows * mSetupMenu.mCellHeight);
			mgv.setTag(sm);
			mgv.setCellDimensions(mSetupMenu.mCellWidth, mSetupMenu.mCellHeight, mSetupMenu.mWidthGap,
					mSetupMenu.mHeightGap);

			int cellX = 0;
			int cellY = -1;

			if (menuitems != null) {
				int micount = menuitems.size();
				
				for (int i = 0; i < micount; i++) {

					int id = menuitems.get(i).id;
					Bitmap icon;
					
					Action action = SetupMenuActions.getInstance().getAction(id);
					if (action != null)
						icon = action.getBitmap();
					else
						icon = Action.getBitmap(id);

					Bitmap bmp = Tools.resizeBitmap(icon, SetupMenu.mScale*0.8f);
					MenuButton menuitem = new MenuButton(mContext, bmp, menuitems.get(i).name,mContext);
					menuitem.setTag(menuitems.get(i));
					
					cellX = i % mSetupMenu.mcolumns;
//					int yushu = micount % mSetupMenu.mcolumns;
//					if (yushu > 0) {
//						if (i < yushu) {
//							cellX = i % mSetupMenu.mcolumns;
//						} else {
//							cellX = (i-yushu) % mSetupMenu.mcolumns;
//						}
//					} else {
//						cellX = i % mSetupMenu.mcolumns;		
//					}						
					cellY = (cellX == 0) ? ++cellY : cellY;
					PageGridView.LayoutParams lp = new PageGridView.LayoutParams(cellX, cellY);
					//lp.setMargins(0, Tools.dip2px(mContext,SetMenuDesktop.cellTopMargin), 0, 1);
					menuitem.setClickable(true);
					menuitem.setOnClickListener(mSetMenuControl);
					menuitem.setOnTouchListener(mSetMenuControl);
					mgv.addView(menuitem, lp);
					mbitmaps.add(bmp);					
				}
				if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)	
				{
					for (int i = 1; i < mSetupMenu.mrows; i++) {
						for (int j = 1; j <= mSetupMenu.mcolumns; j++) {
							ImageView img = new ImageView(mContext);
							img.setImageBitmap(bg2);
							img.setScaleType(ImageView.ScaleType.FIT_XY);
							//img.setBackgroundDrawable(new BitmapDrawable(bg2));
					    	FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
					    			LayoutParams.WRAP_CONTENT);
					    	//pointParams.gravity = Gravity.TOP|Gravity.RIGHT;
					    	pointParams.gravity = Gravity.TOP|Gravity.LEFT;;
					    	int x = j * mSetupMenu.mCellWidth;// - bg2.getWidth()/2;
							int y = i * mSetupMenu.mCellHeight;
					    	//pointParams.setMargins(0, i*hight()/mSetupMenu.mrows,0, 0);
					    	pointParams.setMargins(0,y,0, 0);
					    	mbgwin.addView(img,pointParams);
						}
					}
				}
				else
				{
				    if(!(DefaultLayout.setup_menu_support_scroll_page&&mTabMenus.size()>1)){
					for (int i = 0; i < mSetupMenu.mrows; i++) {
						for (int j = 1; j <= mSetupMenu.mcolumns-1; j++) {
							ImageView img = new ImageView(mContext);
							img.setImageBitmap(bg2);
					    	FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					    			LayoutParams.WRAP_CONTENT);
					    	pointParams.gravity = Gravity.TOP|Gravity.RIGHT;
					    	int x = j * mSetupMenu.mCellWidth;// - bg2.getWidth()/2;
							int y = (int) (i * mSetupMenu.mCellHeight + (mSetupMenu.mCellHeight-bg2.getHeight())/2);
					    	pointParams.setMargins(0, y, x, 0);
					    	mbgwin.addView(img,pointParams);
						}
					}			
				}
				    else{
				        
				        
				    }
				}
			}	
			mMenuGridLayout.addView(mgv, mglp);
			mMenuGridLayout.setSwitchListener(this);
			mMenuGridLayout.setLoop(false);
			mMenuGridLayout.setOverScroll(false);
		}
		if(DefaultLayout.setup_menu_support_scroll_page&&mTabMenus.size()>1){
            Bitmap indicatorBmp = null;
            Bitmap indicatorBarBmp =null;
               indicatorView = new ImageView(mContext);
               indicatorBarView=new ImageView(mContext);
            try {
                indicatorBarBmp=ThemeManager.getInstance().getBitmap(SetupMenu.SETUPMENU_FOLDERNAME+"indicatorBar.png");
                indicatorBmp=ThemeManager.getInstance().getBitmap(SetupMenu.SETUPMENU_FOLDERNAME+"color.png");
            } catch (Exception e) {
                 e.printStackTrace();
            }

            
           // indicatorBmp = Tools.resizeBitmap(indicatorBmp, SetupMenu.mScale);
            Object objTag =1000;
            indicatorBmp = Tools.resizeBitmap(indicatorBmp, SetupMenu.mScale);
            indicatorBmp = Tools.resizeBitmap(indicatorBmp, Utils3D.getScreenWidth()/PagedView.PageCount+2, indicatorBmp.getHeight());

            PagedView.indicatorStep=Utils3D.getScreenWidth()/PagedView.PageCount;
            indicatorView.setImageBitmap(indicatorBmp);
            indicatorView.setTag(objTag);
            //indicatorView.setScaleType(ImageView.ScaleType.FIT_XY);
            Object objTagbar =1001;
            indicatorBarBmp=Tools.resizeBitmap(indicatorBarBmp, SetupMenu.mScale);
            indicatorBarBmp = Tools.resizeBitmap(indicatorBarBmp,width(), indicatorBarBmp.getHeight());
       
            indicatorBarView.setImageBitmap(indicatorBarBmp);
            indicatorBarView.setTag(objTagbar);
             FrameLayout.LayoutParams indicatorParam = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                     LayoutParams.WRAP_CONTENT);
           
            indicatorParam.setMargins(0, 0, 0, 1);
            
             indicatorParam.gravity = Gravity.BOTTOM;
             
             mbgwin.addView(indicatorBarView,indicatorParam);
             mbgwin.addView(indicatorView,indicatorParam);
         
		}
	}
	
//	public void removeTip(){
//		ClingManager.getInstance().removeView(clingPoint);
//	}
	
//	public void showCling(View[] views){
//		cling = (Cling) views[0];
//		clingOk = (Button) views[1];
//		addView(cling, cling.getLayoutParams());
//		addView(clingOk, clingOk.getLayoutParams());
//	}

	public void UpdateMenuItemBitmap(int page, int action, Bitmap bmp) {
		PageGridView mgv = (PageGridView) mMenuGridLayout.getChildAt(page);
		if (mgv != null) {
			for (int i = 0; i < mgv.getChildCount(); i++) {
				ImageView v = (ImageView) mgv.getChildAt(i);
				SetupMenuItem item = (SetupMenuItem) v.getTag();
				if (item.id == action) {
					v.setImageBitmap(bmp);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v instanceof FrameLayout) {
			Integer I = (Integer) v.getTag();
			if (I.intValue() == 1) {
				OnUnLoad();
			}
		}
	}

	@Override
	public void onSwitched(View view, int position) {
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)) {
			if(DefaultLayout.keypad_event_of_focus){
			SetMenuDesktop.origin=false;
			CellLayout3D.firstlyCome=false;
			}
		
			if(!ClingManager.getInstance().hide())OnUnLoad();
			return true;
		}
		return false;
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}
	
	public Bitmap getWallpaper(){
	    int wpOffsetX;
	    Resources res = iLoongLauncher.getInstance().getResources();
        int screenWidth  = res.getDisplayMetrics().widthPixels;
        int screenHeight = res.getDisplayMetrics().heightPixels;
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(iLoongLauncher.getInstance());
        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        Drawable drawable = wallpaperManager.getDrawable();
        Bitmap wallpaperBitmap = ((BitmapDrawable) drawable).getBitmap();
        int wpWidth = wallpaperBitmap.getWidth();
        int wpHeight =wallpaperBitmap.getHeight();
        int []pixData=new int[wallpaperBitmap.getWidth()*wallpaperBitmap.getHeight()];
        if (wpWidth > screenWidth)
        {
            int curScreen = iLoongLauncher.getInstance().getCurrentScreen();
            int screenNum = iLoongLauncher.getInstance().getScreenCount();
            int gapWidth = wpWidth - screenWidth;
            wpOffsetX = (int)((float)gapWidth*curScreen/(screenNum-1));
            //wallpaperBitmap.getPixels(pixData, 0, wpWidth, wpOffsetX, 0, screenWidth, screenHeight);
          //  Bitmap screenShot =Bitmap.createBitmap(pixData, 0, wpWidth, wpWidth, wpHeight, Bitmap.Config.ARGB_8888);
            Bitmap screenShot=Bitmap.createBitmap(wallpaperBitmap, wpOffsetX, Utils3D.getStatusBarHeight(), screenWidth, screenHeight-Utils3D.getStatusBarHeight());
            return screenShot;

        } 

        
        return wallpaperBitmap;
	}

	
	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation.equals(mInAnimationSet)) {
			mAnimationClose = true;
			 String dir=Environment.getExternalStorageDirectory()+File.separator+"coco"+File.separator+"share";
			 File fileDir =new File(dir);
			 boolean ret =true;
			 if ( ! (fileDir.exists())  &&   ! (fileDir.isDirectory())) {
                 ret  =  fileDir.mkdirs();
                 if(ret){
                     Log.v("shareDesk", "creat dir completely");
                 }
                 else{
                     Log.v("shareDesk", "creat dir fail");
                     return ;
                 }
			 }
			 if(ret){
    			  iLoongLauncher.getInstance().postRunnable(new Runnable() {
    		            public void run() {
    		                Log.v("retrieve", " getScreenShot");
    		                Utils3D.getScreenShot(getWallpaper());
    	
    		            }
    		        });
			 }
			if (mClose)
				StarOutAnimation();
		} else if (animation.equals(mOutAnimationSet)) {
			mSetupMenu.CloseMenu();
//			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
	}

	@Override
	public View findFocus() {
		// TODO Auto-generated method stub
		return super.findFocus();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	public void close() {
		mSetupMenu.CloseMenu();
	}

	
	private void ShowItem() {
		origin= true;
		PageGridView mgv = (PageGridView) mMenuGridLayout.getChildAt(0);
		if (mgv != null) {
			for (int i = 0; i < mgv.getChildCount(); i++) {
				MenuButton v = (MenuButton) mgv.getChildAt(i);
				SetupMenuItem item = (SetupMenuItem) v.getTag();
				if (item.id == ActionSetting.ACTION_SYSTEM_PLUG) {
					if (iLoongLauncher.getInstance().isWorkspaceVisible()) {
						v.ButtonEnable();
					} else {
						v.ButtonDisable();
					}
				}
			}
		}
	}
}

class MenuButton extends LinearLayout {

	private ImageView mIcon;
	private TextView mText;
	private int txtdefaultcolor = 0;

	
	
	public MenuButton(Context context, Bitmap icon, String txt,Context mContext) {
		super(context);
		mIcon = new ImageView(context);
		
		mIcon.setImageBitmap(icon);
		mText = new TextView(context);
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)
		{
		   mText.setTextSize(20);
		   mText.setGravity(Gravity.CENTER_VERTICAL);
			setOrientation(LinearLayout.HORIZONTAL);
			setGravity(Gravity.CENTER_VERTICAL);
		}
		else
		{
			mText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			setOrientation(LinearLayout.VERTICAL);
			setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		

		}
		mText.setText(txt);
		
		int top = context.getResources().getDimensionPixelSize(RR.dimen.menu_padding_top);
		int bottom = 0;
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)
		{
			setPadding((int)(SetupMenu.getInstance().mCellWidth/15f),0, 0, 0);
		}
		else
		{
		  
		    
		        setPadding(0, top, 0, bottom);
		    
		}
		addView(mIcon);
		addView(mText);
//		FrameLayout.LayoutParams pointParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,
//    			LayoutParams.WRAP_CONTENT);
//		addView(mText,pointParams);
		if (DefaultLayout.popmenu_style==SetupMenu.POPMENU_STYLE_ANDROID4)
		{
			txtdefaultcolor = Color.WHITE;//mText.getTextColors().getDefaultColor();
		}
		else
		{
			txtdefaultcolor = Color.rgb(0, 0, 0);//mText.getTextColors().getDefaultColor();

		}
		ButtonEnable();
	}

	public void ButtonDown() {
		mIcon.setColorFilter(Color.rgb(30,165,250));
		mText.setTextColor(Color.rgb(30,165,250));
		
	}

	public void ButtonUp() {
		mIcon.clearColorFilter();
		mText.setTextColor(txtdefaultcolor);
	}

	public void ButtonEnable() {
		setEnabled(true);
		mIcon.clearColorFilter();
		mText.setTextColor(txtdefaultcolor);
	}

	public void ButtonDisable() {
		setEnabled(false);
		mIcon.setColorFilter(Color.DKGRAY);
		mText.setTextColor(Color.DKGRAY);
	}
}
