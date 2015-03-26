package com.iLoong.launcher.Folder3D;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.TextField3D;
import com.iLoong.launcher.UI3DEngine.TextField3D.TextFieldListener;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

/*
 * Folder3D  Layout
 *  height=Utils3D.getScreenHeight()-R3D.workspace_cell_height
 *  girdHeight=height-R3D.icongroup_button_height/2
 * 
 *                       buttonHeight/2
 *                       girdHeight
 *                       R3D.workspace_Cell_height
 * */

public class FolderMIUI3D extends ViewGroup3D implements DragSource3D,
		TextFieldListener {
	public static final int MSG_ON_DROP = 0;
	public static final int MSG_UPDATE_VIEW = 1;
	private FolderIcon3D mFolderIcon;
	private GridView3D iconContain;
	private ButtonView3D buttonOK;
	private TextField3D inputTextField3D;
	private ImageView3D inputTextView;
	Texture titleTexture;
	private String inputNameString;
	private int titleWidth_const = 300;
	private int titleHeight;
	private boolean bNeedUpdate = false;
	private Timeline animation_line = null;
	// private Timeline viewTween=null;
	public boolean bEnableTouch = true;
	boolean bOutDragRemove = false;
	public boolean bCloseFolderByDrag = false;
//	private static boolean displayButton = true;
	private float rename_button_width;
	private float rename_button_height;
	private float rename_button_offset;
	private final static float rename_button_width_const = 75;
	private final static float rename_button_height_const = 54;
	private final static float rename_button_offset_const = 12;
//	private static TextureRegion buttonOKFocusRegion = null;
//	private static TextureRegion buttonOKNormalRegion = null;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	//zhujieping add
	private static ImageView3D bgView = null;
	private static ImageView3D gridBgView = null;
	private Timeline v5_animation_line = null;
	public FolderMIUI3D() {
		this(null);

		// TODO Auto-generated constructor stub
	}

	public FolderMIUI3D(String name) {
		super(name);
	}

//	public void buildElements() {
//		rename_button_width = rename_button_width_const * SetupMenu.mScale;
//		rename_button_height = rename_button_height_const * SetupMenu.mScale;
//		rename_button_offset = rename_button_offset_const * SetupMenu.mScale;
//		buttonOKFocusRegion = R3D.getTextureRegion("miui-input-ack-focus");
//		buttonOKNormalRegion = R3D.getTextureRegion("miui-input-ack");
//		if (findView("button_ok") == null) {
//
//			buttonOK = new ButtonView3D("button_ok", buttonOKNormalRegion);
//
//			buttonOK.setSize(rename_button_width, rename_button_height);
//			buildIconGroup();
//
//			addView(buttonOK);
//			if (displayButton) {
//				if (true) {
//					buttonOK.hide();
//				}
//
//			} else {
//				buttonOK.hide();
//			}
//		}
//
//	}
	public void buildV5Elements() {
		rename_button_width = rename_button_width_const * SetupMenu.mScale;
		rename_button_height = rename_button_height_const * SetupMenu.mScale;
		rename_button_offset = rename_button_offset_const * SetupMenu.mScale;
		
		if (findView("button_ok") == null) {
			buttonOK = new ButtonView3D("button_ok");

			buildV5IconGroup();

			addView(buttonOK);
			buttonOK.hide();
		}
		
	}

	public void setUpdateValue(boolean bFlag) {
		bNeedUpdate = bFlag;
		bEnableTouch = true;
	}

	public boolean getColseFolderByDragVal() {
		return bCloseFolderByDrag;
	}

	public void addGridChild() {
		View3D myActor;
		int Count = mFolderIcon.getChildCount();
		if (FolderIcon3D.folder_iphone_style != mFolderIcon.folder_style) {

			if (Count > R3D.folder_max_num / 2) {
				iconContain.setAnimationDelay(0.02f);
			} else {
				iconContain.setAnimationDelay(0.04f);
			}
		}
		bOutDragRemove = false;
		bCloseFolderByDrag = false;
		Icon3D temp = null;
		// iconContain.setAnimationDelay(0);
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		for (int i = 0; i < Count; i++) {
			myActor = mFolderIcon.getChildAt(i);
			// if (myActor instanceof Icon3D) {
			//
			// temp=(Icon3D) myActor.clone();
			// temp.setItemInfo(((Icon3D) myActor).getItemInfo());
			// mFolderIcon.changeTextureRegion(temp,R3D.workspace_cell_height);
			// // myActor.show();
			// // myActor.setRotation(0);
			// // myActor.setScale(1.0f, 1.0f);
			// //
			// //
			// mFolderIcon.changeTextureRegion(myActor,R3D.workspace_cell_height);
			// temp.setInShowFolder(true);
			// temp.setItemInfo(((Icon3D)myActor).getItemInfo());
			// //
			// // myActor.x=myActor.x-this.x;
			// // myActor.y=myActor.y-this.y;
			// templist.add(temp);
			// }

			if (myActor instanceof Icon3D) {
				myActor.show();
				myActor.setRotation(0);
				myActor.setScale(1.0f, 1.0f);

				mFolderIcon.changeTextureRegion(myActor,
						R3D.workspace_cell_height);
				((Icon3D) myActor).setInShowFolder(true);
				((Icon3D) myActor)
						.setItemInfo(((Icon3D) myActor).getItemInfo());

				myActor.x = myActor.x - this.x;
				myActor.y = myActor.y - this.y;
				
				//teapotXu add start for Folder in Mainmenu
				//only when folder is in Mainmenu, hide icons in folder are needed to hide
				if(DefaultLayout.mainmenu_folder_function == true && (mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D))
				{
					if(((Icon3D) myActor).getItemInfo()instanceof ShortcutInfo)
					{
						ApplicationInfo appInfo = ((ShortcutInfo)((Icon3D) myActor).getItemInfo()).appInfo;
						
						if(appInfo != null && appInfo.isHideIcon && ((Icon3D) myActor).getHideStatus() == false)
						{
							//don't add this icon into the FolderList.
							continue;
						}
						
					}
				}
				//teapotXu add end for Folder in Mainmenu
				myActor.show();
				templist.add(myActor);
			}
		}
		iconContain.addItem(templist);	
		
	//teapotXu add start for Folder in Mainmenu
		if(DefaultLayout.mainmenu_folder_function == true)
		{
			if(mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D)
			{
				if(templist.size()>0 && ((Icon3D)templist.get(0)).getUninstallStatus() == false)
				{
					iconContain.setAutoDrag(false);
				}
			}
		}
    //teapotXu add end for Folder in Mainmenu			

	}

	public void addIcon(Icon3D newIcon) {
		if (iconContain.getChildCount() == 0) {
			return;
		}
		newIcon.x = iconContain.getChildAt(0).x;
		newIcon.y = iconContain.getChildAt(0).y;
		iconContain.addItem(newIcon);
	}

	public void updateIcon(Icon3D widgetIcon, Icon3D newIcon) {
		newIcon.x = widgetIcon.x;
		newIcon.y = widgetIcon.y;
		iconContain.removeView(widgetIcon);
		iconContain.addItem(newIcon);
	}

	public void updateTexture() {
		if (iconContain == null) {
			return;
		}
		int Count = iconContain.getChildCount();
		View3D myActor;
		ItemInfo tempInfo;
		for (int i = 0; i < Count; i++) {
			myActor = iconContain.getChildAt(i);
			if (myActor instanceof Icon3D) {
				tempInfo = ((Icon3D) myActor).getItemInfo();
				Log.e("test", "mFolder 3d updateTexture i=" + i + "myActor="
						+ myActor);
				if (((ShortcutInfo) tempInfo).usingFallbackIcon == true) {
					((ShortcutInfo) tempInfo).usingFallbackIcon = false;
					Log.e("test", "mFolder change using fallbackIcon");
				}
				myActor.region = new TextureRegion(
						R3D.findRegion((ShortcutInfo) tempInfo));

			}
		}
	}

	private void buildTextField3D(float textWidth, float textHeight,
			String title) {
		buttonOK.y = (float) (iconContain.y + iconContain.height
				- R3D.folder_group_text_height - R3D.folder_group_top_round
				- R3D.icongroup_margin_top + (R3D.folder_group_text_height
				+ R3D.folder_group_top_round + R3D.icongroup_margin_top - rename_button_height) / 2);

		if (findView("inputTextField3D") == null) {

			Paint paint = new Paint();
			paint.setColor(R3D.folder_title_color);
			paint.setAntiAlias(true);
			paint.setTextSize(textHeight / 2);
			inputTextField3D = new TextField3D("inputTextView", textWidth,
					textHeight, paint);
			float inputTextField3D_X = R3D.folder_group_left_round
					+ (iconContain.width - R3D.folder_group_left_round * 2
							- textWidth - rename_button_width - rename_button_offset)
					/ 2;
			buttonOK.x = inputTextField3D_X + textWidth + rename_button_offset;
			inputTextField3D.setPosition(inputTextField3D_X, buttonOK.y);
			inputTextField3D.setOrigin(textWidth / 2, textHeight / 2);
			inputTextField3D.setText(title);
			// inputTextField3D.setSelection(0, title.trim().length());
			inputTextField3D.setKeyboardAdapter(null);
			inputTextField3D.setEditable(true);
			TextureRegion gridbgTexture = R3D
					.getTextureRegion("miui-rename-bg");
			NinePatch gridbackground = new NinePatch(gridbgTexture, 10, 10, 10,
					10);
			inputTextField3D.setBackgroud(gridbackground);

			inputTextField3D.hide();
			mFolderIcon.bRenameFolder = false;
		}else{
			if (ThemeManager.getInstance().getBoolean(
					"miui_v5_folder")|| DefaultLayout.miui_v5_folder){
				if (inputTextField3D != null){
					float inputTextField3D_X = R3D.folder_group_left_round
							+ (iconContain.width - R3D.folder_group_left_round * 2
									- textWidth - rename_button_width - rename_button_offset)
							/ 2;
					buttonOK.x = inputTextField3D_X + textWidth + rename_button_offset;
					inputTextField3D.setPosition(inputTextField3D_X, buttonOK.y);
					inputTextField3D.setOrigin(textWidth / 2, textHeight / 2);
					inputTextField3D.setText(title);
					// inputTextField3D.setSelection(0, title.trim().length());
					inputTextField3D.setKeyboardAdapter(null);
					inputTextField3D.setEditable(true);
				}
			}
		}

	}

//	private void buildIconGroup() {
//		int countX = 4;
//		//zhujieping add start
//		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean(
//				"miui_v5_folder")
//				|| DefaultLayout.miui_v5_folder;
//		if (miuiV5Folder) {
//			countX = R3D.folder_group_child_count_x;
//			if (countX <= 0) {
//				countX = 4;
//			}
//		}
//		//zhujieping add end
//		int CountSize = mFolderIcon.mInfo.contents.size();
//		int countY = (CountSize + countX - 1) / countX;
//		if (countY == 0) {
//			/* 空文件夹 */
//			countY = 1;
//		}
//		int childMargin = R3D.icongroup_margin_left;
//		//zhujieping
//		if (miuiV5Folder) {
//			openLineRegion = new TextureRegion(new BitmapTexture(ThemeManager
//					.getInstance().getBitmap(
//							"theme/folder/folder-open-line.png")));
//			if (bgView == null){
//				bgView = new ImageView3D("full_screen_bg");
//				if (DefaultLayout.blur_enable)
//				{
//					bgView.setSize(Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
//					bgView.getColor().a = 0;
//				} else {
//					bgView.setSize(this.width, this.height);
//					bgView.getColor().a = 0.77f;
//				}
//				
//				bgView.setBackgroud(new NinePatch(R3D.screenBackRegion));
//			}
//		} else {
//			
//				openLineRegion = R3D.getTextureRegion("widget-folder-open-line");
//			
//		}
//		
//		//zhujieping
//		TextureRegion gridbgTexture = null;
//		NinePatch gridbackground = null;
//		if (!miuiV5Folder) {
//			gridbgTexture = R3D
//				.getTextureRegion("mi-widget-folder-windows-bg");
//			gridbackground = new NinePatch(gridbgTexture,
//				R3D.folder_group_left_round + 2, R3D.folder_group_right_round,
//				R3D.folder_group_top_round + 2, R3D.folder_group_bottom_round);
//		} else {
//			gridbgTexture = new TextureRegion(new BitmapTexture(ThemeManager
//					.getInstance().getBitmap(
//							"theme/folder/widget-v5-folder-bg.png")));
//			gridbackground = new NinePatch(gridbgTexture, 39, 39, 39, 39);
//
//		}
//		int gridHeight = R3D.folder_group_top_round
//				+ R3D.folder_group_text_height + countY
//				* R3D.workspace_cell_height + R3D.folder_group_bottom_round;
//		if (countY == 1) {
//			gridHeight += R3D.folder_group_bottom_round / 2;
//		}
//		int gridWidth = Utils3D.getScreenWidth();
//		//zhujieping
//		if (miuiV5Folder) {
//			int tempPaddingLeft = ThemeManager.getInstance().getInteger(
//					"open_folder_margin_left");
//			int tempPaddingRight = ThemeManager.getInstance().getInteger(
//					"open_folder_margin_right");
//			gridWidth = Utils3D.getScreenWidth() - tempPaddingLeft
//					- tempPaddingRight;
//
//		}
//		iconContain = new GridView3D("iconGroupGrid111", gridWidth, gridHeight,
//				countX, countY);
//		iconContain.setPosition(0, (this.height - gridHeight) / 2f);
//		//zhujieping
//		if (!miuiV5Folder) {
//			iconContain.setBackgroud(gridbackground);
//		}
//		iconContain.enableAnimation(false);
//		// titleWidth = (int)
//		// iconContain.width-R3D.folder_group_left_round-R3D.folder_group_right_round;
//		titleHeight = R3D.folder_group_text_height;
//		inputTextView = new ImageView3D("inputTextView");
//
//		inputTextView
//				.setPosition(
//						R3D.folder_group_left_round,
//						(float) (iconContain.y + iconContain.height
//								- R3D.folder_group_text_height - R3D.folder_group_top_round));
//
//		if (Utils3D.getScreenHeight() < 400 && R3D.icon_bg_num > 0) {
//			iconContain
//					.setPadding(
//							2 * childMargin,
//							2 * childMargin,
//							(int) (R3D.folder_group_text_height + R3D.folder_group_top_round),
//							2 * childMargin);
//		} else {
//			//zhujieping
//			if (miuiV5Folder) {
//				int tempPadding = (int) ((iconContain.width - countX
//						* R3D.workspace_cell_width) / (countX + 1));
//				iconContain
//						.setPadding(
//								tempPadding,
//								tempPadding,
//								(int) (R3D.folder_group_text_height
//										+ R3D.folder_group_top_round + R3D.icongroup_margin_top),
//								2 * childMargin);
//			} else {
//				iconContain
//						.setPadding(
//								2 * childMargin,
//								2 * childMargin,
//								(int) (R3D.folder_group_text_height
//										+ R3D.folder_group_top_round + R3D.icongroup_margin_top),
//								2 * childMargin);
//			}
//		}
//		int titleTextureWidth = (int) (Utils3D.getScreenWidth()
//				- R3D.folder_group_left_round - R3D.folder_group_left_round
//				- R3D.folder_group_left_margin - R3D.folder_group_right_margin);
//		inputTextView.setSize(titleTextureWidth, titleHeight);
//		inputNameString = mFolderIcon.mInfo.title.toString();
//		String title = inputNameString;
//		if (title.endsWith("x.z")) {
//			int length = title.length();
//			if (length > 3) {
//				title = title.substring(0, length - 3);
//			}
//
//		}
//
//		titleTexture = new BitmapTexture(titleToTexture(
//				mFolderIcon.mInfo.title.toString(), titleHeight));
//		titleTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//		//zhujieping 
//		if (miuiV5Folder) {
//			if (gridBgView == null){
//				gridBgView = new ImageView3D("gridBgView");
//				gridBgView.setBackgroud(gridbackground);
//				gridBgView.setSize(gridWidth,
//						gridHeight /*- iconContain.getPaddingTop()*/
//								+ R3D.folder_group_top_round);
//				gridBgView.setPosition((this.width - gridWidth) / 2,
//						(this.height - gridHeight) / 2f);
//				gridBgView.setVisible(false);
//			}
//		}
//		buildTextField3D((int) (titleWidth_const * SetupMenu.mScale),
//				rename_button_height, title);
//		//zhujieping
//		if (miuiV5Folder) {
//			addView(gridBgView);
//		}
//		addView(iconContain);
//		addView(inputTextView);
//		addView(inputTextField3D);
//		inputTextField3D.hide();
//		mFolderIcon.bRenameFolder = false;
//
//	}
	private void buildV5IconGroup() {
		int countX = R3D.folder_group_child_count_x;
		if (countX <= 0) {
			countX = 4;
		}
		int CountSize = mFolderIcon.mInfo.contents.size();
		int countY = (CountSize + countX - 1) / countX;
		if (countY == 0) {
			countY = 1;
		}
		int childMargin = R3D.icongroup_margin_left;
		if (bgView == null) {
			bgView = new ImageView3D("full_screen_bg");
			if (DefaultLayout.blur_enable) {
				bgView.setSize(Utils3D.getScreenWidth(),
						Utils3D.getScreenHeight());
				bgView.getColor().a = 0;
			} else {
				bgView.setSize(this.width, this.height);
				bgView.getColor().a = 0.77f;
			}

			bgView.setBackgroud(new NinePatch(R3D.screenBackRegion));
		}
		
		int gridHeight = R3D.folder_group_top_round
				+ R3D.folder_group_text_height + countY
				* R3D.workspace_cell_height + R3D.folder_group_bottom_round;
		if (countY == 1) {
			gridHeight += R3D.folder_group_bottom_round / 2;
		}
		int gridWidth = Utils3D.getScreenWidth();
		
		int tempPaddingLeft = ThemeManager.getInstance().getInteger(
					"open_folder_margin_left");
		int tempPaddingRight = ThemeManager.getInstance().getInteger(
					"open_folder_margin_right");
		gridWidth = Utils3D.getScreenWidth() - tempPaddingLeft
					- tempPaddingRight;
		if (iconContain == null){
			iconContain = new GridView3D("iconGroupGrid111", gridWidth, gridHeight,
					countX, countY);
			if (Utils3D.getScreenHeight() < 400 && R3D.icon_bg_num > 0) {
				iconContain
						.setPadding(
								2 * childMargin,
								2 * childMargin,
								(int) (R3D.folder_group_text_height + R3D.folder_group_top_round),
								2 * childMargin);
			} else {
				int tempPadding = (int) ((iconContain.width - countX
							* R3D.workspace_cell_width) / (countX + 1));
				iconContain
						.setPadding(
									tempPadding,
									tempPadding,
									(int) (R3D.folder_group_text_height
											+ R3D.folder_group_top_round + R3D.icongroup_margin_top),
									2 * childMargin);
				
			}  
		}
		iconContain.setCellCount(countX, countY);
		iconContain.setPosition((this.width - gridWidth) / 2,
				(this.height - gridHeight) / 2f);
		iconContain.setSize(gridWidth, gridHeight);
		
		iconContain.enableAnimation(false);
		titleHeight = R3D.folder_group_text_height;
		if (inputTextView == null){
			inputTextView = new ImageView3D("inputTextView");
			
		}
		inputTextView
				.setPosition(
						R3D.folder_group_left_round,
						(float) (iconContain.y + iconContain.height
								- R3D.folder_group_text_height - R3D.folder_group_top_round));
		int titleTextureWidth = (int) (Utils3D.getScreenWidth()
				- R3D.folder_group_left_round - R3D.folder_group_left_round
				- R3D.folder_group_left_margin - R3D.folder_group_right_margin);
		inputTextView.setSize(titleTextureWidth, titleHeight);
		if (!inputTextView.isVisible()) {
			inputTextView.show();
		}
		
		inputNameString = mFolderIcon.mInfo.title.toString();
		String title = inputNameString;
		if (title.endsWith("x.z")) {
			int length = title.length();
			if (length > 3) {
				title = title.substring(0, length - 3);
			}

		}
		if (titleTexture == null){
			Bitmap bmp = titleToTexture(
					mFolderIcon.mInfo.title.toString(), titleHeight);
			titleTexture = new BitmapTexture(bmp);
			if(bmp != null && !bmp .isRecycled())bmp.recycle();
		}

		if (gridBgView == null){
			TextureRegion gridbgTexture = new TextureRegion(new BitmapTexture(ThemeManager
					.getInstance().getBitmap(
							"theme/folder/widget-v5-folder-bg.png")));;
			NinePatch gridbackground = new NinePatch(gridbgTexture, 39, 39, 39, 39);
			gridBgView = new ImageView3D("gridBgView");
			gridBgView.setBackgroud(gridbackground);
		}
		gridBgView.setSize(gridWidth,
				gridHeight /*- iconContain.getPaddingTop()*/
						+ R3D.folder_group_top_round);
		gridBgView.setPosition((this.width - gridWidth) / 2,
				(this.height - gridHeight) / 2f);
		gridBgView.setVisible(false);
		
		buildTextField3D((int) (titleWidth_const * SetupMenu.mScale),
				rename_button_height, title);
		addView(gridBgView);
		addView(iconContain);
		addView(inputTextView);
		addView(inputTextField3D);
//		addView(buttonOK);
		inputTextField3D.hide();
		mFolderIcon.bRenameFolder = false;

	}
	public void onInputNameChanged() {
		Texture t;
		Texture texture;

		titleHeight = R3D.folder_group_text_height;
		texture = new BitmapTexture(
				titleToTexture(inputNameString, titleHeight));
		t = this.titleTexture;
		this.titleTexture = texture;
		if (t != null)
			t.dispose();
	}

	private Bitmap titleToTexture(String title, int titleHeight) {
		int titleTextureWidth = (int) (Utils3D.getScreenWidth()
				- R3D.folder_group_left_round - R3D.folder_group_left_round
				- R3D.folder_group_left_margin - R3D.folder_group_right_margin);
		Bitmap bmp = Bitmap.createBitmap(titleTextureWidth, titleHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setColor(R3D.folder_title_color);
		
		//teapotXu add start for MIUI folder
		if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder)//zhujieping
		{
			paint.setColor(Color.WHITE);
		}
		//teapotXu add end for MIUI folder
		
		paint.setAntiAlias(true);
		// paint.setTextSize(Utils3D.getDensity()*titleHeight/3);

		if (title.endsWith("x.z")) {
			int length = title.length();
			if (length > 3) {
				title = title.substring(0, length - 3);
			}
		}
		paint.setTextSize(titleHeight / 2);
		if (Utils3D.measureText(paint,title) > titleTextureWidth - 2) {
			while (Utils3D.measureText(paint,title) > titleTextureWidth
					- Utils3D.measureText(paint,"...") - 2) {
				title = title.substring(0, title.length() - 1);
			}
			title += "...";
		}

		FontMetrics fontMetrics = paint.getFontMetrics();
		float offsetX = 0;
		// if (Utils3D.getDensity()<1f)
		// {
		// offsetX= titleHeight/2;
		// }
		// else
		// {
		// offsetX= titleHeight/2;
		// }
		// offsetX=paint.measureText(title);
		offsetX = (titleTextureWidth - Utils3D.measureText(paint,title)) / 2f;
		float fontPosY = (float) Math.ceil(fontMetrics.descent
				- fontMetrics.ascent);
		fontPosY = titleHeight - (titleHeight - fontPosY) / 2f
				- fontMetrics.bottom;
		canvas.drawText(title, offsetX, fontPosY, paint);
		return bmp;
	}

	private void startAnimation(ArrayList<View3D> templist) {

		float delayFactor = 0.05f;
		View3D view;
		animation_line = Timeline.createParallel();
		int Count = templist.size();
		float duration = 0.3f;
		//zhujieping add 
		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder;
		if (miuiV5Folder) {
			if (DefaultLayout.blur_enable) {
				if (mFolderIcon.blurredView != null)
				{
					mFolderIcon.blurredView.remove();
				}
				
				if (mFolderIcon.lwpBackView != null) {
					mFolderIcon.lwpBackView.remove();
				}

				mFolderIcon.mFolderIndex = mFolderIcon
						.getViewIndex(mFolderIcon.mFolderMIUI3D);
				mFolderIcon.mFolderMIUI3D.hide();

				final Root3D root = iLoongLauncher.getInstance()
						.getD3dListener().getRoot();
				root.transform = true;
				root.setScale(0.8f, 0.8f);
				root.showOtherView();
				root.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT,
						duration, 1.0f, 1.0f, 0);		
				
				root.getHotSeatBar().getModel3DGroup().startTween(View3DTweenAccessor.OPACITY,
						Cubic.OUT, duration, 1, 0, 0);
				
			} else {
				duration = 0.1f;
			}
			if (bgView != null) {
				animation_line.push(Tween
						.to(bgView, View3DTweenAccessor.OPACITY, duration)
						.target(0, 0, 0).ease(Cubic.OUT));
			}
		}
		//teapotXu add end for Miui2 folder
		for (int i = 0; i < Count; i++) {
			view = templist.get(i);
			view.stopAllTween();
			mFolderIcon.addViewBefore(mFolderIcon.folder_front, view);
			mFolderIcon.changeOrigin(view);
			mFolderIcon.changeTextureRegion(view,
					mFolderIcon.getIconBmpHeight());
			view.y = view.y + R3D.workspace_cell_height
					- R3D.workspace_cell_width;
			mFolderIcon.getPos(i);
			//zhujieping add 
			float delay = 0;
			if (miuiV5Folder) {
				delay = 0;
			} else {
				delay = delayFactor * (i / R3D.folder_icon_row_num);
			}
			if (miuiV5Folder) {
				animation_line.push(Tween
						.to(view, View3DTweenAccessor.POS_XY, duration)
						.target(mFolderIcon.getPosx(), mFolderIcon.getPosy(), 0)
						.ease(Cubic.OUT).delay(delay));
				animation_line.push(Tween
						.to(view, View3DTweenAccessor.SCALE_XY, duration)
						.target(mFolderIcon.getScaleFactor(i),
								mFolderIcon.getScaleFactor(i), 0).ease(Back.OUT)
						.delay(delay));
			} else {
				animation_line.push(Tween
					.to(view, View3DTweenAccessor.POS_XY, duration)
					.target(mFolderIcon.getPosx(), mFolderIcon.getPosy(), 0)
					.ease(Linear.INOUT)
					.delay(delayFactor * (i / R3D.folder_icon_row_num)));
				animation_line.push(Tween
					.to(view, View3DTweenAccessor.SCALE_XY, duration)
					.target(mFolderIcon.getScaleFactor(i),
							mFolderIcon.getScaleFactor(i), 0).ease(Cubic.OUT)
					.delay(delayFactor * (i / R3D.folder_icon_row_num)));
			}
		}

		setOrigin(mFolderIcon.mInfo.x + R3D.workspace_cell_width / 2,
				mFolderIcon.mInfo.y + R3D.workspace_cell_height / 2);
		this.stopAllTween();
		animation_line.push(Tween
				.to(this, View3DTweenAccessor.SCALE_XY, duration)
				.target(0, 0, 0).ease(Cubic.OUT));

		animation_line.start(View3DTweenAccessor.manager).setCallback(this);
		//mFolderIcon.RootToCellLayoutAnimTime = duration;
		Log.v("FolderMIUI3D", "MSG_WORKSPACE_ALPAH_TO_ONE");
		//viewParent.onCtrlEvent(mFolderIcon,
			//	FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);

	}
	public void openV5FolderIconsAnim(){
		v5_animation_line = Timeline.createParallel();
		int Count = iconContain.getChildCount();
		View3D myActor;
		for (int i = 0; i < Count; i++) {
			myActor = (Icon3D) iconContain.getChildAt(i);
			myActor.stopAllTween();
			mFolderIcon.changeOrigin(myActor);
			mFolderIcon.getPos(i);
			float pointx = myActor.x;
			float pointy = myActor.y;
			myActor.setScale(mFolderIcon.getScaleFactor(i), mFolderIcon.getScaleFactor(i));	
			myActor.x = mFolderIcon.getPosx()-iconContain.x;
			myActor.y = mFolderIcon.getPosy()-iconContain.y 
					-(R3D.workspace_cell_height - R3D.workspace_cell_width)* mFolderIcon.getScaleFactor(i);
			v5_animation_line.push(Tween
					.to(myActor, View3DTweenAccessor.POS_XY, DefaultLayout.blurDuration)
					.target(pointx, pointy)
					.ease(Cubic.IN).delay(0));
			v5_animation_line.push(Tween
					.to(myActor, View3DTweenAccessor.SCALE_XY, DefaultLayout.blurDuration)
					.target(1.0f,1.0f).ease(Back.IN)
					.delay(0));
			
		}
		v5_animation_line.start(View3DTweenAccessor.manager);
		
	}
	public void DealRenameOnPause() {
		buttonOK.hide();
		inputTextView.show();
		inputTextField3D.hideInputKeyboard();
		inputTextField3D.hide();
		mFolderIcon.bRenameFolder = false;
	}

	public void DealButtonNoAnim() {
		
		iconContain.enableAnimation(false);
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			
		}else{
			if (titleTexture != null) {
				/* 释放内存 */
				Log.v("miui3D", "titleTexture.dispose()");
				titleTexture.dispose();
			}
			titleTexture = null;
		}
		
		if (inputTextField3D != null) {
			inputTextField3D.hideInputKeyboard();
			inputTextField3D.hide();
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		int Count = iconContain.getChildCount();
		View3D myActor;
		for (int i = 0; i < Count; i++) {
			myActor = (Icon3D) iconContain.getChildAt(i);
			myActor.releaseDark();
			if (iconContain.getFocusView() != myActor) {
				tempInfo = ((Icon3D) myActor).getItemInfo();
				tempInfo.screen = i;
				Root3D.addOrMoveDB(tempInfo, mFolderIcon.mInfo.id);
				((Icon3D) myActor).setInShowFolder(false);
				((Icon3D) myActor)
						.setItemInfo(((Icon3D) myActor).getItemInfo());
				calcCoordinate(myActor);
				if (mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style) {
					myActor.y = myActor.y - R3D.folder_group_bottom_margin;
				} else {
					myActor.y = myActor.y - R3D.folder_group_bottom_margin
							+ R3D.workspace_cell_height
							- R3D.workspace_cell_width;
				}
				templist.add(myActor);
			}

		}
		iconContain.removeAllViews();
		bOutDragRemove = false;

		View3D view;
		animation_line = Timeline.createParallel();
		Count = templist.size();
		float duration = 0.3f;

		//zhujieping add 
		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder;
		if (miuiV5Folder) {
			if (DefaultLayout.blur_enable) {
				if (mFolderIcon.blurredView != null) {
					mFolderIcon.blurredView.remove();
				}
				
				if (mFolderIcon.lwpBackView != null) {
					mFolderIcon.lwpBackView.remove();
				}

				mFolderIcon.mFolderIndex = mFolderIcon
						.getViewIndex(mFolderIcon.mFolderMIUI3D);
				mFolderIcon.mFolderMIUI3D.hide();

				// 当主菜单文件夹中拖回到主菜单时，不需要如下的操作
				if (DefaultLayout.mainmenu_folder_function == true
						&& !(mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && 
						(mFolderIcon.getExitToWhere() == FolderIcon3D.TO_APPLIST))) {

					final Root3D root = iLoongLauncher.getInstance()
							.getD3dListener().getRoot();
					root.transform = true;
					root.setScale(0.8f, 0.8f);
					root.showOtherView();
					root.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT,
							duration, 1.0f, 1.0f, 0);
				}
			} 
		}
		
		for (int i = 0; i < Count; i++) {
			view = templist.get(i);
			mFolderIcon.addViewBefore(mFolderIcon.folder_front, view);
			mFolderIcon.changeOrigin(view);
			mFolderIcon.changeTextureRegion(view,
					mFolderIcon.getIconBmpHeight());
			view.y = view.y + R3D.workspace_cell_height
					- R3D.workspace_cell_width;
			mFolderIcon.getPos(i);
			view.setPosition(mFolderIcon.getPosx(), mFolderIcon.getPosy());
			view.setScale(mFolderIcon.getScaleFactor(i),
					mFolderIcon.getScaleFactor(i));
		}
		setOrigin(mFolderIcon.mInfo.x + R3D.workspace_cell_width / 2,
				mFolderIcon.mInfo.y + R3D.workspace_cell_height / 2);
		setScale(0, 0);
		//mFolderIcon.RootToCellLayoutAnimTime = duration;
		Log.v("FolderMIUI3D", "DealButtonNoAnim MSG_WORKSPACE_ALPAH_TO_ONE");
		//viewParent.onCtrlEvent(mFolderIcon,
			//	FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);
		dealAnimationLineFinished();
	}
	//zhujieping add start
	private void showGridBgView() {
		if (gridBgView != null) {
			gridBgView.color.a = 0f;
			gridBgView.setVisible(true);
			Tween.to(gridBgView, View3DTweenAccessor.OPACITY, 0.4f)
					.target(1, 1, 1).ease(Linear.INOUT)
					.start(View3DTweenAccessor.manager);
		}
	}

	private void removeGridBgView() {
		if (gridBgView != null) {
			Tween.to(gridBgView, View3DTweenAccessor.OPACITY, 0.4f)
					.target(0, 0, 0).ease(Linear.INOUT)
					.start(View3DTweenAccessor.manager)
					.setCallback(new TweenCallback() {

						@Override
						public void onEvent(int type, BaseTween source) {
							// TODO Auto-generated method stub
							if (type == TweenCallback.COMPLETE) {
								if (!ThemeManager.getInstance().getBoolean("miui_v5_folder")
										&& !DefaultLayout.miui_v5_folder){
									gridBgView.dispose();
									gridBgView.remove();
								}
							}
						}
					});
		}
	}

	private void hideGridBgView() {
		if (gridBgView != null) {
			Tween.to(gridBgView, View3DTweenAccessor.OPACITY, 0.5f)
					.target(0, 0, 0).ease(Linear.INOUT)
					.start(View3DTweenAccessor.manager);
		}
	}
	//zhujieping add end
	public void DealButtonOKDown() {
		if (mFolderIcon.bAnimate==true)
		{
			return;
		}
		if (iconContain==null)
		{
		  return;
		}
		//zhujieping
		if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder)
			removeGridBgView();
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			
		}else{
			if (titleTexture != null) {
				/* 释放内存 */
				Log.v("miui3D", "titleTexture.dispose()");
				titleTexture.dispose();
			}
			titleTexture = null;
		}
		if (inputTextField3D != null && inputTextField3D.isVisible()==true) {
			inputTextField3D.hideInputKeyboard();
			inputTextField3D.hide();
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		int Count = iconContain.getChildCount();
		View3D myActor;
		for (int i = 0; i < Count; i++) {
			myActor = (Icon3D) iconContain.getChildAt(i);
			myActor.releaseDark();
			myActor.stopAllTween();
			if (iconContain.getFocusView() != myActor) {
				tempInfo = ((Icon3D) myActor).getItemInfo();
				tempInfo.screen = i;
				Root3D.addOrMoveDB(tempInfo, mFolderIcon.mInfo.id);
				((Icon3D) myActor).setInShowFolder(false);
				((Icon3D) myActor)
						.setItemInfo(((Icon3D) myActor).getItemInfo());
				calcCoordinate(myActor);
				if (mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style) {
					myActor.y = myActor.y - R3D.folder_group_bottom_margin;
				} else {
					myActor.y = myActor.y - R3D.folder_group_bottom_margin
							+ R3D.workspace_cell_height
							- R3D.workspace_cell_width;
				}
				templist.add(myActor);
			}

		}
		iconContain.removeAllViews();
		bOutDragRemove = false;
		viewParent.onCtrlEvent(mFolderIcon,
				FolderIcon3D.MSG_WORKSPACE_RECOVER);
		startAnimation(templist);

	}

	private void dealAnimationLineFinished() {
		mFolderIcon.stopTween();
		if (buttonOK != null) {
			removeView(buttonOK);
			buttonOK = null;
		}
		if (!ThemeManager.getInstance().getBoolean("miui_v5_folder")
				&& !DefaultLayout.miui_v5_folder){
			if (buttonOK != null) {
				removeView(buttonOK);
				buttonOK = null;
			}
			if (iconContain != null) {
//				iconContain.dispose();
				removeView(iconContain);
				iconContain = null;
			}
			if (inputTextView != null) {
				removeView(inputTextView);
				inputTextView = null;
			}
			if (inputTextField3D != null) {
				inputTextField3D.hide();
				inputTextField3D.dispose();
				removeView(inputTextField3D);
				inputTextField3D = null;
			}
			if (titleTexture != null) {
				/* 释放内存 */
				Log.v("miui3D", "titleTexture.dispose()");
				titleTexture.dispose();
				titleTexture = null;
			}
		}
		Log.v("miui3D", "closeFolderStartAnimMIUI");
		mFolderIcon.closeFolderStartAnimMIUI();
		
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		if (source == animation_line && type == TweenCallback.COMPLETE) {
			//zhujieping
			String data = (String) source.getUserData();
			if (data != null && data.equals("close_folder_v5")) {
				DealButtonNoAnim();
			} else {
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder) {
					
					if (DefaultLayout.blur_enable)
					{
						iLoongLauncher.getInstance().getD3dListener().getRoot().transform = false;
						mFolderIcon.addViewAt(mFolderIcon.mFolderIndex, mFolderIcon.mFolderMIUI3D);
					}
					viewParent.onCtrlEvent(mFolderIcon,
							FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);
				}
				dealAnimationLineFinished();
			}
		}

	}

	private void dealFolderRename() {
		// if (mFolderIcon.mInfo.title
		mFolderIcon.bRenameFolder = true;
		inputTextField3D.show();
		inputTextField3D.showInputKeyboard();

		buttonOK.displayFocusBG(false);
		buttonOK.show();
		inputTextView.hide();
		//zhujieping add
		if(!ThemeManager.getInstance().getBoolean("miui_v5_folder")
					&& !DefaultLayout.miui_v5_folder){
			SendMsgToAndroid.sendRenameFolderMsg(mFolderIcon);
		}			
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub

		if (keycode == KeyEvent.KEYCODE_BACK) {
			bCloseFolderByDrag = false;
			DealButtonOKDown();
			return true;

		}
		return super.keyUp(keycode);
	}

	@Override
	public boolean onLongClick(float x, float y) {
		View3D hitView = hit(x, y);
		if (hitView != null && hitView.scaleX == 1.0f) {
			if (hitView instanceof Icon3D || hitView instanceof FolderIcon3D)
				hitView.releaseDark();
			hitView = null;
		}
		if (mFolderIcon.bRenameFolder == true) {
			return true;
		}

		/*
		 * 这里要特别判断，因为目前的情况是girdview的cellWidth会小于Icon3D�?
		 * 实际大小，在计算index的时候，会导致计算有误差，导致onLongClick传�?到ICON本身，从而导致垃圾桶不消失和悬浮问题
		 */
		point.x = x;
		point.y = y;
		this.toLocalCoordinates(iconContain, point);
		int retIndex = iconContain.getIndex((int) point.x, (int) point.y);
		if (retIndex < 0 || retIndex >= iconContain.getChildCount()) {
			return true;
		}
		//zhujieping
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			boolean result = super.onLongClick(x, y);
			if (result && iconContain!=null && iconContain.getFocusView() instanceof Icon3D) {
				showGridBgView();
			}
			return result;
		}else{
			return super.onLongClick(x, y);
		}
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		if (mFolderIcon.mInfo.opened == true) {
			if(false){
				super.scroll(x, y, deltaX, deltaY);	
			}
			return true;
		}

		return super.scroll(x, y, deltaX, deltaY);

	}
	private int lastIndex;
	private int currentIndex;
	
	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		if (pointer > 0) {
			return false;
		}

		View3D hitView = hit(x, y);
		if (bEnableTouch) {
			if (hitView != null && hitView.name == buttonOK.name) {
				super.onTouchDown(x, y, pointer);
				return true;
			} else {
				if (mFolderIcon.bRenameFolder == true) {
					return true;
				}
			}
		}
		if (false) {
			point.x = x;
			point.y = y;
			this.toLocalCoordinates(iconContain, point);
			lastIndex=iconContain.getIndex((int)point.x, (int)point.y);
		}
		
		super.onTouchDown(x, y, pointer);
		return true;
	}

	void closeMIUI2Folder() {
		if (mFolderIcon.bAnimate == true) {
			return;
		}
		if (iconContain == null) {
			return;
		}
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			
		}else{
			if (titleTexture != null) {
				/* 释放内存 */
				Log.v("miui3D", "titleTexture.dispose()");
				titleTexture.dispose();
			}
			titleTexture = null;
		}
		if (inputTextField3D != null && inputTextField3D.isVisible() == true) {
			inputTextField3D.hideInputKeyboard();
			inputTextField3D.hide();
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		float duration = 0.5f;
		this.stopTween();
		float scale_x = (float) R3D.workspace_cell_width / (float) this.width;
		float scale_y = (float) R3D.workspace_cell_width / (float) this.height;
		animation_line = Timeline.createParallel();
		animation_line.push(this.startTween(View3DTweenAccessor.SCALE_XY,
				Cubic.OUT, duration, 0, 0, 0));
		animation_line.setUserData("close_folder_v5");
		// animation_line.push(mFolder.startTween(View3DTweenAccessor.POS_XY,
		// Cubic.INOUT, duration, mInfo.x, mInfo.y, 0).setUserData(
		// tween_anim_close));
		animation_line.start(View3DTweenAccessor.manager).setCallback(this);
		SendMsgToAndroid.sendHideClingPointMsg();
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		//zhujieping
		if (pointer > 0) {
			return false;
		}
		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean(
				"miui_v5_folder") || DefaultLayout.miui_v5_folder;
		if (miuiV5Folder) {
			hideGridBgView();
		}
		View3D myActor;
		//Log.v("folder", "ontouchup ***");
		int Count = iconContain.getChildCount();
		for (int i = 0; i < Count; i++) {
			myActor = iconContain.getChildAt(i);
			if (myActor instanceof Icon3D) {
				myActor.releaseDark();
			}
		}
		if (pointer > 0) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
			//Log.v("folder", "ontouchup 0");
			return false;
		}

		View3D hitView = hit(x, y);
		//Log.v("folder", "ontouchup 0-1");	
		if (hitView == null) {
			//Log.v("folder", "ontouchup 1");	
			return iconContain.onTouchUp(x, y, pointer);
		}
		Log.v("folder", "receive hitView=" + hitView);
		if (hitView.name == this.name) {
			if(iconContain.getFocusView()!=null){
				//Log.v("folder", "ontouchup 2");
				//iconContain.onTouchUp(x, y, pointer);
				iconContain.clearFocusView();
				DealButtonOKDown();
			}else{
				//Log.v("folder", "ontouchup 3");
				DealButtonOKDown();
				return true;		
			}
		}
		if (bEnableTouch) {
			if (hitView.name == inputTextView.name) {
				dealFolderRename();
				return true;
			} else if (hitView instanceof GridView3D) {
				Log.v("folder", "ontouchup 3-1");
				return true;
			} else if (hitView instanceof ButtonView3D) {
				return super.onTouchUp(x, y, pointer);
			} else {
				Log.v("folder", "ontouchup 4");
				if (mFolderIcon.bRenameFolder == true) {
					return true;
				}
				iconContain.onTouchUp(x, y, pointer);
				if (false) {
					if(iconContain.getFocusView()==null){
						point.x = x;
						point.y = y;
						this.toLocalCoordinates(iconContain, point);
						currentIndex=iconContain.getIndex((int)point.x, (int)point.y);
						if(lastIndex!=-1&&currentIndex!=-1&& lastIndex==currentIndex){
							return onClick(x, y);	
						}
					}
				}
				return super.onTouchUp(x, y, pointer);

			}
		} else {
			Log.v("folder", "ontouchup 5");
			return super.onTouchUp(x, y, pointer);
		}
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		// Log.v("CircleSomething","circlePopWnd onDoubleClick");
		return true;
	}

	
	void setFolderIcon(FolderIcon3D icon) {
		mFolderIcon = icon;
	}
	public void setEditText(String text) {
		text = text.trim();
		text = text.concat("x.z");
		int length = text.length();
		if (length > 3) {
			mFolderIcon.mInfo.setTitle(text);
			Root3D.updateItemInDatabase(mFolderIcon.mInfo);
			inputNameString = text;
			viewParent.onCtrlEvent(this, MSG_UPDATE_VIEW);
		}
	}

	public void RemoveViewByItemInfo(ItemInfo item) {
		if (bOutDragRemove) {
			return;
		}
		View3D myActor;
		int Count = iconContain.getChildCount();
		for (int i = 0; i < Count; i++) {
			myActor = iconContain.getChildAt(i);
			if (myActor instanceof Icon3D) {
				ItemInfo info = ((Icon3D) myActor).getItemInfo();
				if (item.equals(info)) {
					iconContain.removeView(myActor);
					myActor = null;
					break;
				}
			}
		}
	}

	@Override
	public boolean onCtrlEvent(View3D sender, int event_id) {
		if (sender instanceof GridView3D) {
			switch (event_id) {
			case GridView3D.MSG_VIEW_OUTREGION_DRAG:
				if (mFolderIcon.mInfo.opened == true) {
					//zhujieping
					if (gridBgView != null) {
						removeGridBgView();
					}
					View3D focus = iconContain.getFocusView();
					iconContain.releaseFocus();
					focus.stopTween();
					focus.setScale(1f, 1f);
					focus.toAbsoluteCoords(point);
					bOutDragRemove = true;
					bCloseFolderByDrag = true;
					
					Log.v("FolderIcon3DMi", "FolderMIUI3D ---- MSG_VIEW_OUTREGION_DRAG --- mFolderIcon viewParant is : " + mFolderIcon.getParent().name);

					//teapotXu add start for Folder in Mainmenu
					if(DefaultLayout.mainmenu_folder_function == true)
					{
						if(mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && ((Icon3D)focus).getUninstallStatus() == false)
						{
							//do not remove the focus icon, but it needs set the flag that indicates exiting to CellLayout
							mFolderIcon.setExitToWhere(FolderIcon3D.TO_CELLLAYOUT);
						}
						else
						{
							mFolderIcon.mInfo.remove((ShortcutInfo) ((Icon3D) focus)
									.getItemInfo());
						}
					}
					else
					{
						mFolderIcon.mInfo.remove((ShortcutInfo) ((Icon3D) focus)
								.getItemInfo());
					}
					//teapotXu add end for Folder in Mainmenu					
					
					DealRenameOnPause();
					//zhujieping add start
					if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
							|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable){
						Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
						root.isDragEnd = true;
						root.folderOpened = false;
					}
					//zhujieping add start
					DealButtonOKDown();
					this.setTag(new Vector2(point.x, point.y));
					dragObjects.clear();

					//teapotXu add start for Folder in Mainmenu
					if(DefaultLayout.mainmenu_folder_function == true)
					{
						if(mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && (((Icon3D)focus).getUninstallStatus() == false))
						{
							Icon3D icon3d = (Icon3D) focus;
							icon3d.hideSelectedIcon();
							Icon3D iconClone = icon3d.clone();
							
							// add back to mFolderIcon
							mFolderIcon.changeOrigin(focus);
							((Icon3D) focus).cancelSelected();
							focus.y= focus.y+R3D.workspace_cell_height-R3D.workspace_cell_width;
							mFolderIcon.addViewBefore(mFolderIcon.folder_front, focus);
							
							//should clear the iconState
							iconClone.clearState();
							dragObjects.add(iconClone);
							
						}
						else
						{
							dragObjects.add(focus);
						}					
					}
					else
					{
						dragObjects.add(focus);
					}
					//dragObjects.add(focus);
					//teapotXu add end for Folder in Mainmenu		

					return viewParent.onCtrlEvent(this,
							DragSource3D.MSG_START_DRAG);
				}
				
				case GridView3D.MSG_VIEW_TOUCH_UP:
					mFolderIcon.requestFocus();
					boolean miuiV5Folder = ThemeManager.getInstance().getBoolean(
							"miui_v5_folder")
							|| DefaultLayout.miui_v5_folder;
					if (miuiV5Folder) {
						hideGridBgView();
					}
					return false;
					
				case GridView3D.MSG_GRID_VIEW_REQUEST_FOCUS:
					mFolderIcon.releaseFocus();
					return true;
			}
		}
		//teapotXu add start for Folder In Mainmenu
		else if(sender instanceof Icon3D)
		{
			switch (event_id) {
			case Icon3D.MSG_ICON_LONGCLICK:
				Icon3D focus_icon = (Icon3D)sender;

				if(DefaultLayout.mainmenu_folder_function == true)
				{
					//Now only in Folder of Mainmenu && this folder is open && Applist is not in Uninstall Mode
					if(mFolderIcon.mInfo.opened == true
					&& mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D 
					&& focus_icon.getUninstallStatus() == false
					)
					{
						//zhujieping
						if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
								|| DefaultLayout.miui_v5_folder){
							if (gridBgView != null) {
								removeGridBgView();
							}
						}
						iconContain.releaseFocus();
						focus_icon.stopTween();
						focus_icon.setScale(1f, 1f);
						focus_icon.toAbsoluteCoords(point);
						bOutDragRemove = true;
						bCloseFolderByDrag = true;
						
						dragObjects.clear();
						Icon3D icon3d = focus_icon;
						icon3d.hideSelectedIcon();
						Icon3D iconClone = icon3d.clone();
						
						iconClone.x = point.x;
						iconClone.y = point.y;							
						//should clear the iconState
						iconClone.clearState();
						dragObjects.add(iconClone);
						
						//do not remove the focus icon, but it needs set the flag that indicates exiting to CellLayout
						mFolderIcon.setExitToWhere(FolderIcon3D.TO_CELLLAYOUT);

						DealRenameOnPause();

						//zhujieping add start
						if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
								|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable){
							Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
							root.isDragEnd = true;
							root.folderOpened = false;
						}
						//zhujieping add end
						
//						DealButtonOKDown();
						DealButtonNoAnim();
						this.setTag(new Vector2(point.x, point.y));

						return viewParent.onCtrlEvent(this,
								DragSource3D.MSG_START_DRAG);	
					}
				}
				return true;
			}
		}
		//teapotxu add end for Folder in Mainmenu
		return viewParent.onCtrlEvent(sender, event_id);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		// int srcBlendFunc = 0,dstBlendFunc = 0;
		// if(DefaultLayout.blend_func_dst_gl_one){
		// /*获取获取混合方式*/
		// srcBlendFunc = batch.getSrcBlendFunc();
		// dstBlendFunc = batch.getDstBlendFunc();
		// if(srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE)
		// batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		// }
		batch.setColor(color.r, color.g, color.b, color.a);
		//zhujieping
		if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder)
		{
			if (bgView != null && bgView.background9 != null) {
				batch.setColor(color.r, color.g, color.b, bgView.getColor().a);
				bgView.background9.draw(batch, 0, 0, this.width, this.height);
				batch.setColor(color.r, color.g, color.b, color.a);
			}
			
			if (!mFolderIcon.captureCurScreen) {
				super.draw(batch, color.a);
			}
		} else {
			super.draw(batch, color.a);
		}
		
		if (titleTexture != null && inputTextField3D!=null && inputTextField3D.isVisible() == false
				&& bNeedUpdate) {
			batch.setColor(color.r, color.g, color.b, color.a);
			batch.draw(titleTexture, inputTextView.x, inputTextView.y + this.y,
					inputTextView.width, titleHeight);

		}
//		if (openLineRegion != null && inputTextView != null && bNeedUpdate) {
//			batch.setColor(color.r, color.g, color.b, color.a);
//			batch.draw(
//					openLineRegion,
//					2 * R3D.icongroup_margin_left
//							+ R3D.folder_group_left_margin,
//
//					inputTextView.y + this.y - openLineRegion.getRegionHeight(),
//					width
//							- 2
//							* (2 * R3D.icongroup_margin_left + R3D.folder_group_left_margin),
//					openLineRegion.getRegionHeight());
//		}
		// if(DefaultLayout.blend_func_dst_gl_one){
		// if(srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE)
		// batch.setBlendFunction(srcBlendFunc, dstBlendFunc);
		// }

	}

	@Override
	public void onDropCompleted(View3D target, boolean success) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<View3D> getDragList() {
		// TODO Auto-generated method stub
		return dragObjects;
	}

	@Override
	public void valueChanged(TextField3D textField, String newValue) {
		// TODO Auto-generated method stub

	}

	class ButtonView3D extends View3D {
		private NinePatch backNormalground = null;
		private NinePatch backFocusground = null;
		private TextureRegion titleRegion = null;
		private float titleTopOffset = 0;

		public ButtonView3D(String name) {
			super(name);
			// TODO Auto-generated constructor stub
			setSize(rename_button_width, rename_button_height);
			this.setOrigin(width / 2, height / 2);

			TextureRegion backgroundTexture = R3D
					.findRegion("miui-input-ack-focus");
			backFocusground = new NinePatch(backgroundTexture, 15, 15, 15, 15);
			backgroundTexture = R3D.findRegion("miui-input-ack");
			backNormalground = new NinePatch(backgroundTexture, 15, 15, 15, 15);

			Bitmap titleBmp = Utils3D.TitleToBitmap(
					R3D.getString(RR.string.rename_action), null,
					(int) this.width, (int) rename_button_height / 2,
					Color.BLACK);
			titleRegion = new TextureRegion(new BitmapTexture(titleBmp));
			titleRegion.getTexture().setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
			titleTopOffset = (this.height - titleRegion.getRegionHeight()) / 2f;
			if (titleTopOffset < 0) {
				titleTopOffset = 0;
			}
		}

		@Override
		public boolean onTouchDown(float x, float y, int pointer) {
			if (pointer > 0) {
				return false;
			}
			requestFocus();
			displayFocusBG(true);
			return true;
		}

		public void displayFocusBG(boolean bFocus) {
			if (bFocus) {
				this.setBackgroud(backFocusground);
			} else {
				this.setBackgroud(backNormalground);
			}
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.draw(titleRegion, this.x, this.y + titleTopOffset);
		}

		@Override
		public boolean onTouchUp(float x, float y, int pointer) {
			if (pointer > 0) {
				return false;
			}
			releaseFocus();
			displayFocusBG(false);
			mFolderIcon.requestFocus();
			inputTextField3D.hideInputKeyboard();
			inputTextField3D.hide();
			setEditText(inputTextField3D.getText());
			buttonOK.hide();
			inputTextView.show();

			mFolderIcon.bRenameFolder = false;
			return true;
		}
	}
	
	/******** added by zhenNan.ye begin ********/
	public ImageView3D getBgView()
	{
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder)
		{
			return bgView;
		}
		
		return null;
	}
	/******** added by zhenNan.ye end ********/
}
