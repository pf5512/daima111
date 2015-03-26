package com.iLoong.launcher.Folder3D;

import java.util.ArrayList;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotGridView3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupCircled3D;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.UserFolderInfo.FolderListener;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class FolderIcon3D extends ViewGroupCircled3D implements FolderListener,
		DropTarget3D, IconBase3D, ClingManager.ClingTarget {

	public static final int MSG_FOLDERICON3D_DOUBLECLICK = 0;
	public static final int MSG_FOLDERICON3D_LONGCLICK = 1;
	public static final int MSG_FOLDERICON3D_CLICK = 2;
	public static final int MSG_UPDATE_VIEW = 3;
	public static final int MSG_FOLDERICON_TO_ROOT3D = 4;
	public static final int MSG_FOLDERICON_TO_CELLLAYOUT = 5;
	public static final int MSG_FOLDER_CLING_VISIBLE = 6;
	public static final int MSG_FOLDERICON_TO_HOTSEAT = 7;
	public static final int MSG_FOLDERICON_BACKTO_ORIG = 8;
	private static final int tween_anim_mfolder = 0;
	private static final int tween_anim_open = 1;
	private static final int tween_anim_close = 2;
	private static final int animation_line_trans = 0;
	private static final int animation_line_rotation = 1;
	private static final int animation_line_ondrop = 2;
	// private static final int animation_line_scale=2;
	private static final int START_DRAG_DST = 5;
	public static final int folder_iphone_style = 1;
	public static final int folder_rotate_style = 2;

//teapotXu add start for Folder in Mainmenu	
	private int applistMode = AppList3D.APPLIST_MODE_NORMAL;
	
	public static final int FROM_GRIDVIEW3D = 2;
	public static final int MSG_FOLDERICON_TO_APPLIST = 1009;
	public static final int MSG_FOLDERICON_FROME_APPLIST = 1010;
	
	public static final int TO_CELLLAYOUT = 0;
	public static final int TO_APPLIST = 1;
	public int exit_to_where = TO_CELLLAYOUT;
	public boolean is_applist_folder_no_refresh = true;
//teapotXu add end for Folder in Mainmenu	
	
	public static final int FROM_CELLLAYOUT = 0;
	public static final int FROM_HOTSEAT = 1;
	private int from_Where = FROM_CELLLAYOUT;
	private final int CIRCLE_DST_TOLERANCE = 50;
	public Folder3D mFolder;
	public FolderIconPath folderIconPath = null;
	public UserFolderInfo mInfo;
	private String mFolderName;
	TextureRegion titleTexture;
	//teapotXu_20130411 add start: add folder cover plate pic
	public static TextureRegion folderCoverPlateTexture = null;
	//teapotXu_20130411 add end
	ImageView3D folder_back;
	ImageView3D folder_front;
	TextureRegion foldernumTexture;
	private Tween folderTween = null;
	private Timeline animation_line = null;
	private int mMaxNumItems;
	private int mCurNumItems;
	public boolean bRenameFolder = false;
	// float icon_pos_x;
	//
	public boolean bAnimate = false;

	// private boolean blongClick=false;
	// private boolean bScroll=false;
	private boolean bOnDropIcon = false;
	private boolean bOnDrop = false;
	// private boolean bFling=false;
	private Vector2 downPoint = new Vector2();
	// private boolean bTouchDown=false;
	public int folder_style = folder_rotate_style;
	private int clingState = ClingManager.CLING_STATE_WAIT;
	private boolean bDisplayFolderName = true;
	private float folder_front_height = R3D.folder_front_height;
	private float mPosx;
	private float mPosy;
	private float scaleFactor;
	float num_pos_x;
	float num_pos_y;
	float rotateDegree;
	private int icon_row_num;
	float icon_pos_y;

	public static Bitmap folder_front_bmp = null;
	public static TextureRegion folderFrontRegion = null;
	private static float frontBmpLeftPadding = 0;
	private float IconBmpHeight = 0;
	private int folder_front_padding_left;
	private int folder_front_padding_top;
	private int folder_front_padding_right;
	private int folder_front_padding_bottom;

	// private boolean bNeedLayoutDelay=false;
	/* 从侧拉条开始构建的文件�? */
	//added by zhujieping start
	// suyu
	public FrameBuffer fbo = null;
	public FrameBuffer fbo2 = null;
	public static SpriteBatch blurBatch = null;
	public TextureRegion fboRegion = null;
	public TextureRegion fboRegion2 = null;
	public static ShaderProgram shader = createDefaultShader();
		
	public boolean blurInitialised = false;
	public static boolean captureCurScreen = false;
	public boolean blurBegin = false;
	public boolean blurCompleted = true;
	public int blurCount = 0;
	public View3D blurredView = null;
	public static TextureRegion wallpaperTextureRegion = new TextureRegion();
	public int wpOffsetX;
	
	public int mFolderIndex;
	public FolderMIUI3D mFolderMIUI3D = null;
	public static final int MSG_WORKSPACE_ALPAH_TO_ONE = 9;
	public static final int MSG_BRING_TO_FRONT = 15;
	public static final int MSG_WORKSPACE_RECOVER = 16;
	public boolean ishide = false;
	public static boolean liveWallpaperActive;
	public static ImageView3D lwpBackView;
	
	
	static {
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable)
		{
			genWallpaperTextureRegion();
		}
	}
	//added by zhujieping end
	
	
	public FolderIcon3D(UserFolderInfo folderInfo) {
		this("folder", folderInfo);
	}

	public FolderIcon3D(String name, UserFolderInfo folderInfo) {
		super(name);
		from_Where = FROM_CELLLAYOUT;
		mFolderName = folderInfo.title.toString();
		mInfo = folderInfo;
		this.setTag(folderInfo);
		folder_front_padding_left = R3D.getInteger("folder_front_padding_left");
		if (folder_front_padding_left == -1) {
			folder_front_padding_left = R3D
					.getInteger("folder_front_margin_offset");
		}
		folder_front_padding_top = R3D.getInteger("folder_front_padding_top");
		if (folder_front_padding_top == -1) {
			folder_front_padding_top = R3D
					.getInteger("folder_front_margin_offset");
		}
		folder_front_padding_right = R3D
				.getInteger("folder_front_padding_right");
		if (folder_front_padding_right == -1) {
			folder_front_padding_right = R3D
					.getInteger("folder_front_margin_offset");
		}
		folder_front_padding_bottom = R3D
				.getInteger("folder_front_padding_bottom");
		if (folder_front_padding_bottom == -1) {
			folder_front_padding_bottom = R3D
					.getInteger("folder_front_margin_offset");
		}
		mMaxNumItems = R3D.folder_max_num;
		mCurNumItems = folderInfo.contents.size();
		scaleFactor = R3D.folder_icon_scale_factor / 100f;
		rotateDegree = R3D.folder_icon_rotation_degree;
		icon_row_num = R3D.folder_icon_row_num;
		if (R3D.folder_transform_num == 3
				&& R3D.getInteger("folder_style") != 1) {
			folder_style = folder_rotate_style;
		} else {
			folder_style = folder_iphone_style;
			// this.transform = true;
			folder_back = null;
		}

		mInfo.addListener(this);
		onLeafCountChanged(mCurNumItems);
		onInitTitle(folderInfo.title.toString());
		//teapotXu_20130411 add start: add folder_cover_plate_pic
		createFolderCoverPlatePicTexture();
		//teapotXu_20130411 add end
		buildElements();
	}

	//zhujieping add
	public FolderMIUI3D getMIUI3DFolder(){
		return mFolderMIUI3D;
	}
	// private static float getFrontTopingMargin()
	// {
	// float padding=0;
	// if (R3D.folder_transform_num==3 &&R3D.getInteger("folder_style")!=1)
	// {
	// padding=0;
	// }
	// else
	// {
	// if (R3D.icon_bg_num>0)
	// {
	// if (R3D.icon_bg_width!=R3D.workspace_cell_width
	// && R3D.icon_bg_height!=R3D.workspace_cell_height)
	// {
	// padding=Utils3D.getTopPading()-(folder_front_bmp.getHeight()-Utilities.sIconTextureWidth)/2;
	// if (padding<0)
	// {
	// padding=0;
	// }
	// }
	// else
	// {
	// padding=0;
	// }
	// }
	// else
	// {
	// padding = Utils3D.getTopPading();
	// }
	// }
	// return padding;
	// }

	public float getIconBmpHeight() {
		if (IconBmpHeight == 0) {
			IconBmpHeight = Utils3D.getIconBmpHeight();
		}
		return IconBmpHeight;
	}

	private static Bitmap MergeBmpToPixmap(Bitmap folderFront, Bitmap titleBg) {
		int textureWidth = R3D.workspace_cell_width;
		int textureHeight = R3D.folder_front_height;
		float padding = (textureWidth - folderFront.getWidth()) / 2;

		// if(titleBg != null)canvas.drawBitmap(titleBg,0,textureHeight -
		// titleBg.getHeight(),null);
		if (padding < 0) {
			padding = 0;
		}
		if (R3D.folder_transform_num == 3
				&& R3D.getInteger("folder_style") != 1) {
			Bitmap bmp = Bitmap.createBitmap(textureWidth, textureHeight,
					Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);
			// if(titleBg != null)canvas.drawBitmap(titleBg,0,textureHeight -
			// titleBg.getHeight(),null);
			canvas.drawBitmap(folderFront, padding, 0, null);
			return bmp;
			// return Utils3D.bmp2Pixmap(bmp);
		} else {
			return Utils3D.titleToBitmap(folderFront, null, null, null,
					textureWidth, textureHeight);
			// return Utils3D.bmp2Pixmap(temp);
		}
	}
	
	//teapotXu_20130411 add start: add folder cover plate pic
	public static void createFolderCoverPlatePicTexture()
	{
		if(folderCoverPlateTexture == null)
		{
			Bitmap folder_cover_plate_bmp = null;
			Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap("theme/iconbg/folder_cover_plate_pic.png");
			if(null != original_icon_cover_bmp){
				float density = iLoongApplication.ctx.getResources()
						.getDisplayMetrics().density;
				if (R3D.folder_transform_num == 3
						&& R3D.getInteger("folder_style") != 1) {
					folder_cover_plate_bmp = Tools.resizeBitmap(original_icon_cover_bmp, density / 1.5f);
				} else {
					if (R3D.icon_bg_num > 0) {
						folder_cover_plate_bmp = Bitmap.createScaledBitmap(original_icon_cover_bmp,
								DefaultLayout.app_icon_size,
								DefaultLayout.app_icon_size, false);

					} else {
						BitmapDrawable drawable = new BitmapDrawable(
								(Bitmap) original_icon_cover_bmp);
						drawable.setTargetDensity(iLoongLauncher.getInstance()
								.getResources().getDisplayMetrics());
						folder_cover_plate_bmp = Utilities.createIconBitmap(drawable,
								iLoongLauncher.getInstance());
					}
					original_icon_cover_bmp.recycle();
				}
			}	
			
			if(folder_cover_plate_bmp != null)
			{
				folderCoverPlateTexture = new TextureRegion(new BitmapTexture(MergeBmpToPixmap(folder_cover_plate_bmp, null)));				
			}
		}
	}
	//teapotXu_20130411 add end

	public static void createFrontBmp() {
		if (folder_front_bmp == null) {
			Bitmap tmp = ThemeManager.getInstance().getBitmap(
					"theme/folder/widget-folder-bg.png");
			if (tmp != null) {
				float density = iLoongApplication.ctx.getResources()
						.getDisplayMetrics().density;
				if (R3D.folder_transform_num == 3
						&& R3D.getInteger("folder_style") != 1) {
					folder_front_bmp = Tools.resizeBitmap(tmp, density / 1.5f);
				} else {
					if (R3D.icon_bg_num > 0) {
						if (tmp.getWidth()==DefaultLayout.app_icon_size && tmp.getHeight()==DefaultLayout.app_icon_size) {
	        				folder_front_bmp = tmp;
	        			} else {
	        				folder_front_bmp = Bitmap.createScaledBitmap(tmp,DefaultLayout.app_icon_size, DefaultLayout.app_icon_size,false);
	        				tmp.recycle();
	        			}

					} else {
						BitmapDrawable drawable = new BitmapDrawable(
								(Bitmap) tmp);
						drawable.setTargetDensity(iLoongLauncher.getInstance()
								.getResources().getDisplayMetrics());
						folder_front_bmp = Utilities.createIconBitmap(drawable,
								iLoongLauncher.getInstance());
					}
					if(folder_front_bmp != tmp)tmp.recycle();
				}

			}
		}
	}

	public static void folder_front_ToPixmap3D() {
		createFrontBmp();
		frontBmpLeftPadding = getFrontLeftPadding();
		if (folderFrontRegion == null) {
			Bitmap frontPixmap = MergeBmpToPixmap(folder_front_bmp, null);
			folderFrontRegion = new TextureRegion(
					new BitmapTexture(frontPixmap));
			folderFrontRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		// return folderFrontRegion;
	}

	public float getScaleFactor(int index) {
		// mAnimParams.transX = transX0 + progress * (finalParams.transX -
		// transX0);
		// mAnimParams.transY = transY0 + progress * (finalParams.transY -
		// transY0);
		// mAnimParams.scale = scale0 + progress * (finalParams.scale - scale0);
		if (R3D.getInteger("folder_style") == 1
				&& R3D.folder_transform_num == 3) {
			if (index == 0) {
				return scaleFactor;
			} else if (index == 1) {
				return scaleFactor - 0.1f;
			} else if (index >= 2) {
				return scaleFactor - 0.1f;
			}
		}

		return scaleFactor;
	}

	public float getPosx() {
		return mPosx;
	}

	public float getPosy() {
		return mPosy;
	}

	public float getRotateDegree() {
		return rotateDegree;
	}

	private void sortItemsByIndex(ArrayList<ShortcutInfo> items) {
		int i, j;
		int index1 = 0, index2 = 0;
		ShortcutInfo tempInfo1 = null, tempInfo2 = null;

		int count = items.size();
		for (j = 0; j <= count - 1; j++) {
			for (i = j; i < count; i++) {
				tempInfo1 = items.get(j);
				tempInfo2 = items.get(i);
				index1 = tempInfo1.screen;
				index2 = tempInfo2.screen;
				if (index1 > index2) {
					items.set(j, tempInfo2);
					items.set(i, tempInfo1);
				}
			}

		}
	}

	// public void changeTextureRegion(View3D myActor,int x, int y, int width,
	// int height)
	// {
	// myActor.setSize(width,height);
	// myActor.setOrigin(myActor.width/2, myActor.height/2);
	// myActor.region.setRegion(x,y,width,height);
	// }
	public void changeOrigin(View3D myActor) {
		if (R3D.folder_transform_num == 3
				&& R3D.getInteger("folder_style") != 1) {
			myActor.setOrigin(myActor.width / 2, myActor.height / 2);
		} else {

			// if (R3D.folder_transform_num == 3
			// && R3D.getInteger("folder_style") == 1) {
			// myActor.setOrigin(
			// (R3D.workspace_cell_width - DefaultLayout.app_icon_size) / 2f,
			// 0);
			// } else {
			// myActor.setOrigin(
			// (R3D.workspace_cell_width - DefaultLayout.app_icon_size) / 2f,
			// R3D.workspace_cell_height - getIconBmpHeight());
			// }

			if (R3D.folder_transform_num == 3
					&& R3D.getInteger("folder_style") == 1) {
				myActor.setOrigin(
						(R3D.workspace_cell_width - DefaultLayout.app_icon_size) / 2f,
						0);
			} else {
				myActor.setOrigin(0, 0);
			}
		}
	}

	public void changeTextureRegion(View3D myActor, float iconHeight) {

		float scale = ((float) getIconBmpHeight() / (float) R3D.workspace_cell_height);
		// Log.d("title",
		// "iconHeight,iconBmp="+iconHeight+","+getIconBmpHeight());
		if (myActor.height == R3D.workspace_cell_height
				&& iconHeight != myActor.height) {
			float V2 = myActor.region.getV()
					+ (myActor.region.getV2() - myActor.region.getV()) * scale;
			myActor.region.setV2(V2);
			myActor.setSize(R3D.workspace_cell_width, iconHeight);
			changeOrigin(myActor);

		}

		else if (myActor.height != R3D.workspace_cell_height
				&& iconHeight != myActor.height) {
			float V2 = myActor.region.getV()
					+ (myActor.region.getV2() - myActor.region.getV()) / scale;
			myActor.region.setV2(V2);
			myActor.setSize(R3D.workspace_cell_width, iconHeight);
			myActor.setOrigin(myActor.width / 2, myActor.height / 2);

		}

	}

	public void changeTextureRegion(ArrayList<View3D> view3DArray,
			float iconHeight) {
		View3D myActor;
		int Count = view3DArray.size();
		for (int i = 0; i < Count; i++) {
			myActor = view3DArray.get(i);
			changeTextureRegion(myActor, iconHeight);
		}
	}

	/* 从开机开始创建文件夹内容 */
	public void createAndAddShortcut(IconCache iconCache,
			UserFolderInfo folderInfo) {
		mInfo = folderInfo;
		ArrayList<ShortcutInfo> children = folderInfo.contents;
		sortItemsByIndex(children);
		int Count = children.size();
		for (int i = 0; i < Count; i++) {

			ShortcutInfo child = (ShortcutInfo) children.get(i);
			
			if (child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION
					|| child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) {
				Icon3D icon = null;
				if (child.intent != null
						&& child.intent.getAction().equals(
								"com.android.contacts.action.QUICK_CONTACT")) {
					Bitmap bmp = Bitmap.createBitmap(child
							.getIcon(iLoongApplication.mIconCache));
					float scale = 1f;
					if (bmp.getWidth() != DefaultLayout.app_icon_size
							|| bmp.getHeight() != DefaultLayout.app_icon_size) {
						scale = (float) DefaultLayout.app_icon_size
								/ bmp.getWidth();
					}
					if (DefaultLayout.thirdapk_icon_scaleFactor != 1f
							&& !R3D.doNotNeedScale(null, null)) {
						scale = scale * DefaultLayout.thirdapk_icon_scaleFactor;
					}
					if (scale != 1f) {
						bmp = Tools.resizeBitmap(bmp, scale);
					}
					icon = new Icon3D(child.title.toString(), bmp,
							child.title.toString(), Icon3D.getIconBg());
				} else {
					icon = new Icon3D(child.title.toString(),
							R3D.findRegion(((ShortcutInfo) child)));
				}
				icon.setItemInfo((ShortcutInfo) child);
				addViewBefore(folder_front, icon);

			} else if (child.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW) {
				View3D virView = DefaultLayout
						.showDefaultWidgetView(((ShortcutInfo) child));

				if (virView != null)
					addViewBefore(folder_front, virView);
			}
		}

		mCurNumItems = Count;
		if (mCurNumItems > 0) {
			onItemsChanged();
			folder_layout_poweron();

		}

	}

	private void onLeafCountChanged(int count) {
		if (folder_style == folder_iphone_style) {
			if (count == mMaxNumItems && viewParent != null) {
				this.setTag(mFolderName);
				viewParent.onCtrlEvent(this, MSG_UPDATE_VIEW);
			} else if (count == mMaxNumItems - 1 && viewParent != null) {
				this.setTag(mFolderName);
				viewParent.onCtrlEvent(this, MSG_UPDATE_VIEW);
			}
		} else {
			if (count == R3D.folder_max_num) {
				this.foldernumTexture = R3D.findRegion("folder-maxnumber-"
						+ count);
			} else {
				this.foldernumTexture = R3D
						.findRegion("folder-number-" + count);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iLoong.launcher.UI3DEngine.ViewGroup3D#addViewBefore(com.iLoong.launcher
	 * .UI3DEngine.View3D, com.iLoong.launcher.UI3DEngine.View3D)
	 */
	@Override
	public void addViewBefore(View3D actorBefore, View3D actor) {
		// TODO Auto-generated method stub
		if (folder_style == folder_rotate_style) {
			super.addViewBefore(actorBefore, actor);
		} else {
			super.addView(actor);
		}
		if (actor instanceof WidgetIcon) {
			((WidgetIcon) actor).setTag2(this);
		}
	}

	private void startAddFolderNodeAnim(ArrayList<View3D> mCircleSomething) {
		View3D myActor;
		stopAnimation();
		animation_line = Timeline.createParallel();
		ArrayList<Icon3D> Icon3DCircle = new ArrayList<Icon3D>();
		Icon3DCircle.clear();
		int Count = mCircleSomething.size();
		for (int i = 0; i < Count; i++) {
			myActor = mCircleSomething.get(i);
			if (myActor instanceof Icon3D) {
				Icon3DCircle.add((Icon3D) myActor);
			}
		}

		Count = Icon3DCircle.size();
		float delayFactor = 0;
		if (Count > R3D.folder_max_num / 2) {
			delayFactor = 0.06f;
		} else {
			delayFactor = 0.1f;
		}
		float pos_x = folder_front.x;
		float pos_y = folder_front.y + folder_front_height
				+ R3D.workspace_cell_width / 2;
		if (folder_style == folder_iphone_style) {
			pos_y = pos_y - 3 * R3D.workspace_cell_width / 4;
		}

		for (int i = 0; i < Count; i++) {

			myActor = Icon3DCircle.get(i);
			changeTextureRegion(myActor, getIconBmpHeight());
			animation_line.push(Tween
					.to(myActor, View3DTweenAccessor.POS_XY, 0.8f)
					.target(pos_x, pos_y).ease(Quad.INOUT)
					.delay(delayFactor * i));
		}
		// animation_line.pushPause(0.2f);
		bAnimate = true;
		animation_line.start(View3DTweenAccessor.manager)
				.setUserData(animation_line_trans).setCallback(this);

	}

	private void startAddFolderNodeAnim() {
		View3D myActor;
		stopAnimation();
		float pos_x = folder_front.x;
		float pos_y = folder_front.y + folder_front_height
				+ R3D.workspace_cell_width / 2;
		if (folder_style == folder_iphone_style) {
			pos_y = pos_y - 3 * R3D.workspace_cell_width / 4;
		}

		animation_line = Timeline.createParallel();
		int Count = getChildCount();
		float delayFactor = 0;
		if ((Count - 2) > R3D.folder_max_num / 2) {
			delayFactor = 0.06f;
		} else {
			delayFactor = 0.1f;
		}
		for (int i = 0; i < Count; i++) {
			myActor = getChildAt(i);
			if (myActor instanceof Icon3D) {
				animation_line.push(Tween
						.to(myActor, View3DTweenAccessor.POS_XY, 0.8f)
						.target(pos_x, pos_y).delay(delayFactor * i)
						.ease(Quad.INOUT));
			}
		}
		// animation_line.pushPause(0.2f);
		bAnimate = true;
		animation_line.start(View3DTweenAccessor.manager)
				.setUserData(animation_line_trans).setCallback(this);

	}

	public void addIcon(Icon3D newIcon) {
		ItemInfo info = newIcon.getItemInfo();
		if (mInfo.opened == true) {
			info.screen = mInfo.contents.size();
			mInfo.add((ShortcutInfo) info);
			Root3D.addOrMoveDB(newIcon.getItemInfo(), mInfo.id);
			//zhujieping
			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
				mFolderMIUI3D.addIcon(newIcon);
			}else
				mFolder.addIcon(newIcon);			
		}
		// else
		// {
		// addFolderNode(newIcon);
		// }

	}

	public void updateIcon(Icon3D widgetIcon, Icon3D newIcon) {
		int index = mInfo.contents.indexOf(widgetIcon.getItemInfo());
		if (mInfo.opened == true && index != -1) {
			//zhujieping add
			if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder)
				mFolderMIUI3D.updateIcon(widgetIcon, newIcon);
			else
				mFolder.updateIcon(widgetIcon, newIcon);
			mInfo.contents.set(index, (ShortcutInfo) newIcon.getItemInfo());
			newIcon.getItemInfo().screen = widgetIcon.getItemInfo().screen;
			// newIcon.getItemInfo().container=
			// widgetIcon.getItemInfo().container;
			Root3D.addOrMoveDB(newIcon.getItemInfo(), mInfo.id);
			Root3D.deleteFromDB(widgetIcon.getItemInfo());
		} else {
			if (this.children.contains(widgetIcon) && index != -1) {

				newIcon.x = widgetIcon.x;
				newIcon.y = widgetIcon.y;
				newIcon.rotation = widgetIcon.rotation;
				newIcon.getItemInfo().screen = widgetIcon.getItemInfo().screen;
				
				if(this.folderIconPath != null){
					this.folderIconPath.changeIcon(widgetIcon, newIcon);
				}
				
				replaceView(widgetIcon, newIcon);
				mInfo.contents.set(index, (ShortcutInfo) newIcon.getItemInfo());
				Root3D.addOrMoveDB(newIcon.getItemInfo(), mInfo.id);
				Root3D.deleteFromDB(widgetIcon.getItemInfo());
			}
		}
		if(this.folderIconPath == null)
			folder_layout_without_anim();
	}

	public void addFolderNode(ArrayList<View3D> mCircleSomething) {
		int Count = mCircleSomething.size();
		View3D myActor;

		for (int i = 0; i < Count; i++) {
			myActor = mCircleSomething.get(i);
			if (myActor instanceof Icon3D) {
				ItemInfo info = ((Icon3D) myActor).getItemInfo();
				info.x = (int) folder_front.x;
				info.y = (int) (folder_front.y);
				info.screen = mInfo.contents.size();
				Root3D.addOrMoveDB(info, mInfo.id);
				mInfo.add((ShortcutInfo) info);
				calcCoordinate(myActor);
				addViewBefore(folder_front, myActor);
			}
		}
		startAddFolderNodeAnim(mCircleSomething);
	}

	public void addFolderNode(Icon3D view) {
		ItemInfo info = view.getItemInfo();

		switch (info.itemType) {
		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
		case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
		case LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW:
			if (info instanceof ApplicationInfo) {
				info = new ShortcutInfo((ApplicationInfo) info);
			}

			info.x = (int) folder_front.x;
			info.y = (int) (folder_front.y);
			info.screen = mInfo.contents.size();
			Root3D.addOrMoveDB(info, mInfo.id);
			mInfo.add((ShortcutInfo) info);

			addViewBefore(folder_front, view);
			view.x = view.x - this.x;
			view.y = view.y - this.y;
			// calcCoordinate(view);
			startAddFolderNodeAnim();
		}

	}

	private void addDropNode(Icon3D view) {
		ItemInfo info = view.getItemInfo();
		switch (info.itemType) {
		case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
		case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
		case LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW:
			if (info instanceof ApplicationInfo) {
				info = new ShortcutInfo((ApplicationInfo) info);
			}

			addViewBefore(folder_front, view);
			// view.setPosition(folder_front.x+icon_pos_x,
			// folder_front.y+icon_pos_y);
			info.x = 0;
			info.y = (int) view.y;
			info.screen = mInfo.contents.size();	
			
			Root3D.addOrMoveDB(info, mInfo.id);
			Icon3D iconView = (Icon3D) view;
			iconView.setInShowFolder(false);
			iconView.setItemInfo(info);
			mInfo.add((ShortcutInfo) info);

		}
	}

	private void stopAnimation() {
		if (animation_line != null && !animation_line.isFinished()) {
			animation_line.free();
			animation_line = null;
			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
				if (bOnDropIcon){
					bOnDropIcon=false;
				}
			}
		}
	}

	public void onInitTitle(String title) {
		this.titleTexture = R3D.findRegion(title);
	}

	public void setTexture() {

		this.titleTexture = R3D.findRegion(mFolderName);

	}

	private void setFolderIconPathFullScreen() {
		setPosition(0, 0);
		setSize(Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		setOrigin(this.width / 2, this.height / 2);
		Log.v("testdrag", "setFolderIconPathFullScreen 111" + this);
		folder_front.setPosition(mInfo.x, mInfo.y);
		folder_front.setSize(R3D.workspace_cell_width, folder_front_height);
		if (folder_style == folder_rotate_style) {
			folder_back.setPosition(folder_front.x + frontBmpLeftPadding,
					folder_front.y + folder_front_height
							- R3D.folder_gap_height);
			folder_back.setSize(R3D.folder_back_width, R3D.folder_back_height);
		}
		folder_layout(false);
		folderIconPath.dragPath.x += mInfo.x;
		folderIconPath.dragPath.y += mInfo.y;
	}

	public void setFolderIconSize(float pos_x, float pos_y,
			float front_offset_x, float front_offset_y) {

		if (mInfo.opened == false) {
			Log.v("testdrag", "setFolderIconSize 111");
			this.setPosition(pos_x, pos_y);
			this.setSize(R3D.workspace_cell_width, R3D.workspace_cell_height);
		} else {
			Log.v("testdrag", "setFolderIconSize 222");
			this.setPosition(0, 0);
			this.setSize(Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		}

		this.setOrigin(this.width / 2, this.height / 2);
		// {
		// NinePatch ninePatch= new
		// NinePatch(R3D.findRegion("widget-folder-windows-bg"), 3,
		// 3, 3, 3);
		// this. setBackgroud(ninePatch);
		// }
		folder_front.setPosition(front_offset_x, front_offset_y);
		// changeFolderFrontRegion(true);
		if (folder_style == folder_rotate_style) {
			folder_back.setPosition(folder_front.x + frontBmpLeftPadding,
					folder_front.y + folder_front_height
							- R3D.folder_gap_height);

		}

	}

	public void changeFolderFrontRegion(boolean bCompress) {
		float scale = 0.63f;
		if (folder_style == folder_rotate_style) {

		} else {
			// scale= R3D.folder_front_width/R3D.workspace_cell_height;
			scale = getIconBmpHeight() / R3D.workspace_cell_height;
		}
		if (bDisplayFolderName == true && bCompress == false) {
			return;
		}
		if (bDisplayFolderName == false && bCompress == true) {
			return;
		}
		if (bCompress) {
			float V2 = folder_front.region.getV()
					+ (folder_front.region.getV2() - folder_front.region.getV())
					* scale;
			folder_front.region.setV2(V2);
			if (folder_style == folder_rotate_style) {
				folder_front.setSize(R3D.workspace_cell_width,
						R3D.folder_front_height * scale);

			} else {
				folder_front.setSize(R3D.workspace_cell_width,
						getIconBmpHeight());
			}
			// folder_front.y -= R3D.folder_front_height*(1-scale)/2;
			// folder_front.y=0;

			bDisplayFolderName = false;
			folder_front_height = (R3D.folder_front_height * scale);

			// icon_pos_y=R3D.folder_icon_posy-R3D.folder_front_height*(1-scale);
		} else {
			float V2 = folder_front.region.getV()
					+ (folder_front.region.getV2() - folder_front.region.getV())
					/ scale;
			folder_front.region.setV2(V2);
			folder_front.setSize(R3D.workspace_cell_width,
					R3D.folder_front_height);
			// folder_front.y += R3D.folder_front_height*(1-scale)/2;
			// folder_front.y=0;
			folder_front_height = R3D.folder_front_height;
			bDisplayFolderName = true;
			// icon_pos_y=R3D.folder_icon_posy;
		}
		folder_front.setOrigin(folder_front.width / 2, folder_front.height / 2);
		if (folder_style == folder_rotate_style) {
			folder_back.setPosition(folder_front.x + frontBmpLeftPadding,
					folder_front.y + folder_front_height
							- R3D.folder_gap_height);

		}
		folder_layout_without_anim();
		// folder_layout(true);
	}

	private static float getFrontLeftPadding() {
		int textureWidth = R3D.workspace_cell_width;
		float padding = (textureWidth - folder_front_bmp.getWidth()) / 2;
		// if (R3D.icon_bg_num > 0) {
		// return 0;
		// } else {
		return padding;
		// }

	}

	private void buildElements() {

		mFolder = new Folder3D("folder3D");
		mFolder.setFolderIcon(this);
	     //zhujieping add
		 if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
			 mFolderMIUI3D = new FolderMIUI3D("folderMIUI3D");
			 mFolderMIUI3D.setFolderIcon(this);
		 }

		folder_front_ToPixmap3D();
		// TextureRegion folderFrontRegion = new TextureRegion(new
		// Texture3D(frontPixmap));
		if (folderFrontRegion != null) {
			folder_front = new ImageView3D("folder_front", folderFrontRegion);
			folder_front.setSize(R3D.workspace_cell_width, folder_front_height);
		}

		if (folder_style == folder_iphone_style) {
			folder_front.hide();
			folder_back = null;
		} else {
			folder_back = new ImageView3D("folder_behind",
					R3D.findRegion("widget-folder-bg2"));
			folder_back.setSize(R3D.folder_back_width, R3D.folder_back_height);
			addView(folder_back);
		}
		 addView(folder_front);
		 //zhujieping add
		 if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
			addView(mFolderMIUI3D);
		 }else
			 addView(mFolder);
		 //zhujieping add
		 if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
			   mFolderMIUI3D.setSize(R3D.workspace_cell_width, R3D.workspace_cell_height);
			   mFolderMIUI3D.hide();
		 }else{
		   mFolder.setSize(R3D.workspace_cell_width, R3D.workspace_cell_height);
		   mFolder.hide();
		 }
		setFolderIconSize(mInfo.x, mInfo.y, 0, 0);
		folder_layout(true);

	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		// Log.e("folder", "fling");
		this.color.a = 1f;
		return super.fling(velocityX, velocityY);
	}

	@Override
	public boolean onLongClick(float x, float y) {
		// Log.e("folder", "onLongClick");
		// Log.v("test123", " Foldericon3d onLongClick:" + name + " x:" + x +
		// " y:" + y);
		this.color.a = 1f;
		if (!this.isDragging && mInfo.opened == false) {
			this.toAbsoluteCoords(point);
			this.setTag(new Vector2(point.x, point.y));
			point.x = x;
			point.y = y;
			this.toAbsolute(point);
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			return viewParent.onCtrlEvent(this, MSG_FOLDERICON3D_LONGCLICK);
		}
		return super.onLongClick(x, y);
	}

	@Override
	public ItemInfo getItemInfo() {

		return this.mInfo;
	}

	public Bitmap titleToPixmap() {
		int color = Color.WHITE;
		if (this.mCurNumItems == mMaxNumItems
				&& folder_iphone_style == folder_style) {
			color = Color.RED;
		}
		return titleToTexture(mFolderName, color);
	}

	public static Bitmap titleToTexture(String title, int color) {
		int textureWidth = R3D.workspace_cell_width;
		// int textureHeight = R3D.workspace_cell_text_height;
		if (title.endsWith("x.z")) {
			int length = title.length();
			if (length > 3) {
				title = title.substring(0, length - 3);
			}
		}
		return Utils3D.folderTitleToBitmap(title, Icon3D.titleBg, textureWidth);
		// return Utils3D.titleToBitmap(null, title, null, Icon3D.titleBg,
		// textureWidth, R3D.workspace_cell_height);
	}

	private boolean isFull() {
		return mInfo.contents.size() >= mMaxNumItems;
	}

	private void closeFolder() {
	  	//zhujieping
		if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			mFolderMIUI3D.setPosition(0, 0);
			mFolderMIUI3D.setSize(R3D.workspace_cell_width, R3D.workspace_cell_height);
			mFolderMIUI3D.setOrigin(mFolderMIUI3D.width / 2f, mFolderMIUI3D.height / 2f);
			mFolderMIUI3D.hide();
		}else{
			mFolder.setPosition(0, 0);
			mFolder.setSize(R3D.workspace_cell_width, R3D.workspace_cell_height);
			mFolder.hide();
		}
		Log.v("testdrag", "closeFolder 111");
		//zhujieping add start
		boolean miuiV5folder = ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder;
		if (miuiV5folder && DefaultLayout.blur_enable)
		{
			blurFree();
			releaseFocus();
			
		} else {
			releaseFocus();
		}
		//zhujieping add end
	//teapotXu add start for Folder in Mainmenu
		if(DefaultLayout.mainmenu_folder_function == true)
		{
			if(from_Where == FROM_GRIDVIEW3D && exit_to_where == TO_APPLIST )
			{
				viewParent.onCtrlEvent(this,MSG_FOLDERICON_TO_APPLIST);
			}
			else
				viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_CELLLAYOUT);
		}
		else
			viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_CELLLAYOUT);
	//teapotXu add end for Folder in Mainmenu
		
		bAnimate = false;
		SendMsgToAndroid.sendShowClingPointMsg();
	}

	public void FolderIconNormalScreen() {
		Log.v("testdrag", "FolderIconNormalScreen 111");

		this.setPosition(mInfo.x, mInfo.y);
		this.setSize(R3D.folder_front_width, R3D.workspace_cell_height);
		this.setOrigin(this.width / 2, this.height / 2);
		folder_front.setPosition(0, 0);
		if (folder_style == folder_rotate_style) {
			folder_back.setPosition(folder_front.x + frontBmpLeftPadding,
					folder_front.y + folder_front_height
							- R3D.folder_gap_height);
		}
	}

	//zhujieping add start
	private void openV5Folder() {

		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder;
		float duration = 0.3f;
		stopTween();
		if (mFolderMIUI3D == null){
			return;
		}
		mFolderMIUI3D.stopTween();
		mFolderMIUI3D.setPosition(0, 0);
		mFolderMIUI3D.setSize(Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		mFolderMIUI3D.setOrigin(mFolderMIUI3D.width / 2, mFolderMIUI3D.height / 2);
		mFolderMIUI3D.show();
		mFolderMIUI3D.bEnableTouch = false;
		setFolderIconSize(0, 0, mInfo.x, mInfo.y);
		folder_layout(false);
		mFolderMIUI3D.buildV5Elements();
		mFolderMIUI3D.addGridChild();
		bAnimate = true;
		if (miuiV5Folder && DefaultLayout.blur_enable)
			mFolderMIUI3D.setScale(1, 1);
		else
			mFolderMIUI3D.setScale(0, 0);
		mFolderMIUI3D.transform = true;
		Log.d("testdrag", "openFolder");
		mFolderMIUI3D.setOrigin(mInfo.x + R3D.workspace_cell_width / 2, mInfo.y
				+ R3D.workspace_cell_height / 2);
		if (miuiV5Folder && DefaultLayout.blur_enable) {
			mFolderMIUI3D.openV5FolderIconsAnim();
			folderTween = mFolderMIUI3D.getBgView().startTween(View3DTweenAccessor.OPACITY, Quint.IN,
					DefaultLayout.blurDuration, DefaultLayout.maskOpacity, 0, 0)
					.setUserData(tween_anim_mfolder).setCallback(this);			
		} else {
			folderTween = mFolderMIUI3D
					.startTween(View3DTweenAccessor.SCALE_XY, Cubic.INOUT,
							duration, 1.0f, 1.0f, 0)
					.setUserData(tween_anim_mfolder).setCallback(this);
		}
		
		SendMsgToAndroid.sendHideClingPointMsg();
	}
	//zhujieping add end
	public void openFolder() {
		stopTween();
		mInfo.opened = true;
		mFolder.setPosition(R3D.folder_group_left_margin,
				-Utils3D.getScreenHeight());
		mFolder.setSize(Utils3D.getScreenWidth() - R3D.folder_group_left_margin
				- R3D.folder_group_right_margin, Utils3D.getScreenHeight()
				- R3D.workspace_cell_height);
		mFolder.show();
		mFolder.bEnableTouch = false;
		setFolderIconSize(0, 0,
				(Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
				R3D.folder_group_bottom_margin);
		folder_layout(false);
		mFolder.buildElements();
		Log.d("testdrag", "openFolder");
		folderTween = mFolder
				.startTween(View3DTweenAccessor.POS_XY, Linear.INOUT, 0.4f,
						R3D.folder_group_left_margin,
						R3D.workspace_cell_height, 0)
				.setUserData(tween_anim_mfolder).setCallback(this);

		SendMsgToAndroid.sendHideClingPointMsg();
	}

	@Override
	public void setItemInfo(ItemInfo info) {
		// TODO Auto-generated method stub

		this.mInfo = (UserFolderInfo) info;
	}

	@Override
	public boolean multiTouch2(Vector2 initialFirstPointer,
			Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer) {
		this.color.a = 1f;
		if (mInfo.opened == false) {
			if (initialFirstPointer.x > x && initialFirstPointer.x < x + width
					&& initialFirstPointer.y > y
					&& initialFirstPointer.y < y + height) {

				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	public int getFromWhere() {
		return from_Where;
	}

	//teapotXu add start for Folder in Mainmenu
	 
	public Folder3D getFolder(){
		return mFolder;
	}
	
	public boolean getOnDrop() {
		return bOnDrop;
	}
	
	public void setApplistMode(int mode)
	{
		applistMode = mode;
	}
	
	public int getApplistMode()
	{
		return applistMode;
	}

	
	public int getExitToWhere(){
		return exit_to_where;
	}
	public void setExitToWhere(int where){
		exit_to_where = where;
	}	
	//teapotXu add end for Fodler in Mainmenu	
	
	public void closeFolderIconPath() {
		if (folderIconPath != null) {
			removeView(folderIconPath);
			folderIconPath = null;
			mFolder.bCloseFolderByDrag = false;
			if (folder_style == FolderIcon3D.folder_rotate_style) {
				folder_layout(true);

			} else {
				folder_layout_scale_without_anim();
			}
			
			releaseFocus();
			
			
			viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_CELLLAYOUT);
			bAnimate = false;
			//zhujieping add
			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder)
				Root3D.isDragon = false;
			SendMsgToAndroid.sendShowClingPointMsg();
		}
	}

	// @Override
	// public boolean fling(float velocityX, float velocityY)
	// {
	// bFling = true;
	// Log.v("test123","fling true velocityX="+velocityX+" velocityY="+velocityY);
	// return super.fling(velocityX, velocityY);
	// }
	private boolean canDragIcon(float deltaX, float deltaY) {
		if (bOnDropIcon) {
			return false;
		}

		if (mCurNumItems > 0
				&& ((Math.abs(deltaX) == 0 && Math.abs(deltaY) > 0) || (Math
						.abs(deltaY) / Math.abs(deltaX) > 1))
				&& folderIconPath == null
				&& this.viewParent instanceof CellLayout3D) {
			from_Where = FROM_CELLLAYOUT;
			return true;
		} else {
			if (DefConfig.DEF_NEW_SIDEBAR == true) {
				if (mCurNumItems > 0
						&& ((Math.abs(deltaX) == 0 && Math.abs(deltaY) > 0) || (Math
								.abs(deltaY) / Math.abs(deltaX) > 1))
						&& folderIconPath == null
						&& this.viewParent instanceof HotGridView3D) {
					from_Where = FROM_HOTSEAT;
					return true;
				} else {
					return false;
				}
			}
		}

		// Log.v("test123","canDragIcon return false");
		return false;
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		// Log.e("folder", "scroll");
		// TODO Auto-generated method stub
		// Log.v("testdrag", "deltaX="+deltaX + " deltaY="+deltaY + "x="+x +
		// " y="+y);
		// Log.v("testdrag", "downPoint.x="+downPoint.x +
		// " downPoint.y="+downPoint.y + "x="+x + " y="+y);
		if (mInfo.opened == false) {
			// bScroll=true;
			if (x > 0 && x < width && y > 0 && y < height) {
				if (!DefaultLayout.folder_no_dragon
						&& canDragIcon(deltaX, deltaY)) {
					Log.e("launcher", "drag folder clingState=" + clingState);
					//zhujieping add
					if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder)
						Root3D.isDragon = true;
					folderIconPath = new FolderIconPath("folderIconPath");
					folderIconPath.dragPath.x = x;
					folderIconPath.dragPath.y = y;
					setFolderIconPathFullScreen();
					folderIconPath.setFolderIcon(this);
					addView(folderIconPath);
					
				//teapotXu add start for Folder in Mainmenu
					if(DefaultLayout.mainmenu_folder_function == true)
					{
						if(from_Where == FROM_GRIDVIEW3D)
						{
							exit_to_where = TO_APPLIST;
							viewParent.onCtrlEvent(this,MSG_FOLDERICON_FROME_APPLIST);
						}
						else
							viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_ROOT3D);
					}
					else
						viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_ROOT3D);
					
					//viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_ROOT3D);
				//teapotXu add end for Folder in Mainmenu
					
					requestFocus();
					// if(clingState == ClingManager.CLING_STATE_SHOW)
					// {
					Log.e("launcher", "cancel folder cling");
					clingState = ClingManager.CLING_STATE_DISMISSED;
					ClingManager.getInstance().cancelFolderCling();
					// }
					SendMsgToAndroid.sendHideClingPointMsg();
					return true;
				} else {
					return super.scroll(x, y, deltaX, deltaY);
				}
			}
		}
		return super.scroll(x, y, deltaX, deltaY);
	}

	public void setLongClick(boolean bFalse) {
		// blongClick =bFalse;
	}

	public void setOnDropFalse() {
		if (bOnDrop) {
			bOnDrop = false;
		}
	}

	public boolean onMyClick(float x, float y) {
		if (bOnDropIcon) {
			return true;
		}
		if (bAnimate) {
			return true;
		}
		if (mCurNumItems == 0) {
			SendMsgToAndroid.sysPlaySoundEffect();
			this.bRenameFolder = true;
			SendMsgToAndroid.sendRenameFolderMsg(this);
			return true;
		}
		if (folderIconPath != null) {
			return super.onClick(x, y);
		}
		if (mInfo.opened) {
			View3D hitView = hit(x, y);
			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
				if (hitView.name == folder_front.name) {
					mFolderMIUI3D.DealButtonOKDown();
					return true;
				} else if (folder_style == folder_rotate_style
						&& hitView.name == folder_back.name) {
					mFolderMIUI3D.DealButtonOKDown();
					return true;
				} else if (hitView.name == this.name) {
					mFolderMIUI3D.DealButtonOKDown();
					return true;
				} else {
					return super.onClick(x, y);
				}
			}else{
				if (hitView.name == folder_front.name) {
					mFolder.DealButtonOKDown();
					return true;
				} else if (folder_style == folder_rotate_style
						&& hitView.name == folder_back.name) {
					mFolder.DealButtonOKDown();
					return true;
				} else if (hitView.name == this.name) {
					mFolder.DealButtonOKDown();
					return true;
				} else {
					return super.onClick(x, y);
				}
			}
		} else {
			// if (x > 0 && x < width && y > 0 && y < height && bTouchDown) {
			if (x > 0 && x < width && y > 0 && y < height) {
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder){
					if (mInfo.opened == false) {
						if (this.viewParent instanceof CellLayout3D) {
							viewParent.onCtrlEvent(this, MSG_BRING_TO_FRONT);
							from_Where = FROM_CELLLAYOUT;
							//teapotXu add start for Folder in Mainmenu
						}else if(DefaultLayout.mainmenu_folder_function == true && this.viewParent instanceof GridView3D)
							{
							//从主菜单中进入文件夹
							from_Where = FROM_GRIDVIEW3D;
							exit_to_where = TO_APPLIST;
							
							boolean bPermitAnim = viewParent.onCtrlEvent(this,
									MSG_FOLDERICON_FROME_APPLIST);

							this.setTag("miui_v5_folder");
							//Log.e("whywhy", " onMyClick bPermitAnim=" + bPermitAnim);
							if (bPermitAnim) {
								SendMsgToAndroid.sysPlaySoundEffect();
								mInfo.opened = true;
								requestFocus();
		
								openV5Folder();
							}
							return true;
						//teapotXu add end for Folder in Mainmenu								
						} else {
							bringToFront();
							from_Where = FROM_HOTSEAT;
						}
						this.setTag("miui_v5_folder");
						boolean bPermitAnim = viewParent.onCtrlEvent(this,
									MSG_FOLDERICON_TO_ROOT3D);
						Log.e("whywhy", " onMyClick bPermitAnim=" + bPermitAnim);
						if (bPermitAnim) {
							SendMsgToAndroid.sysPlaySoundEffect();
							mInfo.opened = true;
							requestFocus();
							SendMsgToAndroid.sendHideWorkspaceMsg();
							openV5Folder();
						}
					}
				}else{
				SendMsgToAndroid.sysPlaySoundEffect();
				if (this.viewParent instanceof CellLayout3D) {
					bAnimate = true;
					from_Where = FROM_CELLLAYOUT;
					viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_ROOT3D);
					folderTween = this
							.startTween(
									View3DTweenAccessor.POS_XY,
									Linear.INOUT,
									0.2f,
									(Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
									R3D.folder_group_bottom_margin, 0)
							.setUserData(tween_anim_open).setCallback(this);
					SendMsgToAndroid.sendHideWorkspaceMsg();
			//teapotXu add start for Folder in Mainmenu
				}else if(DefaultLayout.mainmenu_folder_function == true && this.viewParent instanceof GridView3D){
					
					//从主菜单中进入文件夹
					bAnimate = true;
					from_Where = FROM_GRIDVIEW3D;
					exit_to_where = TO_APPLIST;
					
					viewParent.onCtrlEvent(this,
							MSG_FOLDERICON_FROME_APPLIST);
					folderTween = this
							.startTween(
									View3DTweenAccessor.POS_XY,
									Linear.INOUT,
									0.2f,
									(Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
									R3D.folder_group_bottom_margin, 0)
									.setUserData(tween_anim_open)
									.setCallback(this);
					return true;
			//teapotXu add end for Folder in Mainmenu					
				} else {
					if (DefConfig.DEF_NEW_SIDEBAR == true) {
						if (this.viewParent instanceof HotGridView3D) {
							bAnimate = true;
							from_Where = FROM_HOTSEAT;
							viewParent.onCtrlEvent(this,
									MSG_FOLDERICON_TO_ROOT3D);
							folderTween = this
									.startTween(
											View3DTweenAccessor.POS_XY,
											Linear.INOUT,
											0.2f,
											(Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
											R3D.folder_group_bottom_margin, 0)
									.setUserData(tween_anim_open)
									.setCallback(this);
							SendMsgToAndroid.sendHideWorkspaceMsg();

						}
					}
				}
				}
			}
			return true;
		}

	}

	@Override
	public boolean onClick(float x, float y) {
		//zhujieping add  begin 	
		Utils3D.showPidMemoryInfo("1111111");
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable)
		{
			if (!mInfo.opened && folderIconPath == null && mCurNumItems > 0)//长龙后，关闭长龙，不要响应
			{	
				calcWallpaperOffset();
				blurInitial();			
			}
		}
		//zhujieping add end 	
		// Log.e("folder", "onClick");
		
		return onMyClick(x, y);
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		if (folderIconPath != null) {
			return true;
		}

		if (bAnimate) {
			return true;
		}

		if (mInfo.opened) {
			return true;
		}
		if (mCurNumItems == 0) {
			// SendMsgToAndroid.sendRenameFolderMsg(this);
			return true;
		} else {
			// Log.v("test123","onDoubleClick viewParent="+this.viewParent.name);
			return true;
		}
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		if (!mInfo.opened) {
			this.color.a = 1f;	
		}
		
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				if (!mInfo.opened && folderIconPath == null){
					ParticleManager.disableClickIcon = true;
				}
				else {
					ParticleManager.disableClickIcon = false;
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/	
		
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		boolean bRet = false;
		if (x > 0 && x < width && y > 0 && y < height && pointer == 0) {
			downPoint.set(x, y);
		}
		//
		if (folderIconPath != null && bAnimate == false) {
			bRet = super.onTouchDown(x, y, pointer);
			if (bRet == true) {

			} else {
				folderIconPath.DealButtonOKDown();
			}
			
			return true;
		}
	
		if (!mInfo.opened && bAnimate == false) {
			this.color.a = 0.6f;
		} else {
			if(!bAnimate){
				super.onTouchDown(x, y, pointer);	
			}
		}
		return true;
	}

	// @Override
	// public boolean onTouchUp(float x, float y, int pointer)
	// {
	// if (pointer ==1 ||( pointer ==0 && (blongClick==true
	// ||bOnDropIcon==true||bOnDrop==true)))
	// {
	//
	// blongClick=false;
	// bOnDrop=false;
	// // return super.onTouchUp(x, y, pointer);
	// }
	// Log.d("touch", " FolderIcon onTouchUp:" + name + " x:" + x + " y:" + y);
	// return super.onTouchUp(x, y, pointer);
	//
	// //return onMyTouchUp(x,y,pointer);
	// }
	// public boolean onMyTouchUp (float x, float y,int Pointer)
	// {
	// if (bAnimate)
	// {
	// bTouchDown=false;
	// return true;
	// }
	// if (mCurNumItems==0)
	// {
	// bTouchDown=false;
	// return false;
	// }
	// if (folderIconPath!=null )
	// {
	// bTouchDown=false;
	// return super.onTouchUp(x, y,Pointer);
	// }
	// if (mInfo.opened)
	// {
	//
	// View3D hitView= hit(x,y);
	// //bScroll=false;
	// bTouchDown=false;
	// if (hitView.name == folder_front.name)
	// {
	// mFolder.DealButtonOKDown();
	// return true;
	// }
	// else if (folder_style==folder_rotate_style && hitView.name ==
	// folder_back.name)
	// {
	// mFolder.DealButtonOKDown();
	// return true;
	// }
	// else
	// {
	// return super.onTouchUp(x, y,Pointer);
	// }
	// }
	// else
	// {
	// //if (x > 0 && x < width && y > 0 && y < height && bTouchDown) {
	// if (x > 0 && x < width && y > 0 && y < height ) {
	// if (this.viewParent instanceof CellLayout3D) {
	// bAnimate = true;
	// from_Where= FROM_CELLLAYOUT;
	// viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_ROOT3D);
	// folderTween = this
	// .startTween(
	// View3DTweenAccessor.POS_XY,
	// Linear.INOUT,
	// 0.2f,
	// (Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
	// R3D.folder_group_bottom_margin, 0)
	// .setUserData(tween_anim_open).setCallback(this);
	// SendMsgToAndroid.sendHideWorkspaceMsg();
	// } else {
	// if (DefConfig.DEF_NEW_SIDEBAR == true) {
	// if (this.viewParent instanceof HotGridView3D) {
	// bAnimate = true;
	// from_Where= FROM_HOTSEAT;
	// viewParent.onCtrlEvent(this,
	// MSG_FOLDERICON_TO_ROOT3D);
	// folderTween = this
	// .startTween(
	// View3DTweenAccessor.POS_XY,
	// Linear.INOUT,
	// 0.2f,
	// (Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
	// R3D.folder_group_bottom_margin, 0)
	// .setUserData(tween_anim_open)
	// .setCallback(this);
	// SendMsgToAndroid.sendHideWorkspaceMsg();
	//
	// }
	// }
	// }
	//
	// }
	// bTouchDown = false;
	// return true;
	// }
	//
	// }

	public boolean onHomeKey(boolean alreadyOnHome) {
		Log.d("testdrag", " FolderIcon3D onHomeKey bAnimate=" + bAnimate);
		if (bAnimate)
			return false;
		if (bRenameFolder) {
			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder && mInfo.opened){
				bRenameFolder = false;
			}else{
				iLoongLauncher.getInstance().renameFoldercleanup();
			}
		}
		if (folderIconPath != null) {
			folderIconPath.DealButtonOKDown();
			return true;
		}
		if (mInfo.opened) {
			Log.d("testdrag", " FolderIcon3D DealButtonOKDown bAnimate="
					+ bAnimate);
			//zhujieping add
			if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder){
				mFolderMIUI3D.DealButtonOKDown();
			}else
				mFolder.DealButtonOKDown();
			return true;
		}
		return false;
	}

	public void closeFolderStartAnim()

	{
		Log.v("testdrag", "closeFolderStartAnim 111");
		mInfo.opened = false;
		this.setFolderIconSize(
				(Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
				R3D.folder_group_bottom_margin, 0, 0);
		if (folder_style == FolderIcon3D.folder_rotate_style) {
			folder_layout_rotate_without_anim();
		} else {
			folder_layout_scale_without_anim();
		}
		folderTween = this
				.startTween(View3DTweenAccessor.POS_XY, Cubic.OUT, 0.2f,
						mInfo.x, mInfo.y, 0).setUserData(tween_anim_close)
				.setCallback(this);

	}

	@Override
	public void onEvent(int type, BaseTween source) {
		if (source == folderTween && type == TweenCallback.COMPLETE) {
			folderTween = null;
			int animKind = (Integer) (source.getUserData());
			if (animKind == tween_anim_open) {
				if (mInfo.opened == false) {
					Log.d("testdrag", "on Event openFolder");
					openFolder();
				} else {
					Log.d("testdrag", "on Event bAnimate false");
					bAnimate = false;
				}
			} else if (animKind == tween_anim_mfolder) {
				//zhujieping add
				if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder){
					if (DefaultLayout.ui_style_miui2 == false) 
						mFolderMIUI3D.addGridChild();
					mFolderMIUI3D.setUpdateValue(true);
				}else{
					mFolder.addGridChild();
				mFolder.setUpdateValue(true);
				}
				bAnimate = false;
				//zhujieping add 
				if ((ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder) && DefaultLayout.blur_enable)
				{
					MiuiV5FolderBlurFinish();
				}
			} else if (animKind == tween_anim_close) {
				closeFolder();
			}
		}
		if (type == TweenCallback.COMPLETE && source == animation_line) {
			int animKind = (Integer) (source.getUserData());
			stopAnimation();
			// animation_line = null;
			if (animKind == animation_line_trans) {
				bAnimate = false;
				folder_layout(true);
				bOnDropIcon = false;
				Log.v("test12345", "bAnimate is false");
			} else if (animation_line_ondrop == animKind) {
				// bringToFront
				int index = 0;
				float posx = 0;
				float posy = 0;
				if (folder_style == folder_rotate_style) {
					index = folder_front.getIndexInParent() + 1;
					// posx= folder_front.x;
					// posy=folder_front.y+icon_pos_y;
					getPos(0);
					posx = mPosx;
					posy = mPosy + R3D.workspace_cell_width / 4;
				} else {
					index = mCurNumItems + 2;/* Folde3Dr and folder Front */
					posx = -R3D.folder_icon_rotation_offsetx;
					posy = R3D.folder_icon_rotation_offsety
							+ R3D.workspace_cell_width * scaleFactor
							+ R3D.folder_icon_rotation_offsety / 4;
				}

				int Count = getChildCount();
				View3D myActor;
				animation_line = Timeline.createParallel();
				for (int i = index; i < Count; i++) {
					myActor = this.getChildAt(i);
					myActor.stopTween();
					if (folder_style == folder_rotate_style) {
						// addDropNode((Icon3D) myActor);
					}
					animation_line.push(Tween
							.to(myActor, View3DTweenAccessor.POS_XY, 0.3f)
							.target(posx, posy, 0).ease(Linear.INOUT));
				}
				bAnimate = true;
				Log.v("test12345", "bAnimate is true animation_line_ondrop");
				animation_line.start(View3DTweenAccessor.manager)
						.setUserData(animation_line_trans).setCallback(this);

			}
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
	}

	private void RemoveViewByItemInfo(ItemInfo item) {
		View3D myActor;
		boolean bFind = false;
		//zhujieping
		if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder){
			if(mInfo.opened && mFolderMIUI3D.isVisible()){
				mFolderMIUI3D.RemoveViewByItemInfo(item);
			}else
			{
				int Count = getChildCount() - 1;
				for (int i = Count; i >= 0; i--) {
					myActor = getChildAt(i);
					if (myActor instanceof Icon3D) {
						ItemInfo info = ((Icon3D) myActor).getItemInfo();
						if (item.equals(info) && (mInfo.opened == false)) {
							removeView(myActor);
							myActor = null;
							bFind = true;
							break;
						}
					}
				}
				if (bFind && folderIconPath==null) 
				{
					folder_layout(true);
				}
			}
		}else{ 
		if (mInfo.opened == true && mFolder.isVisible() == true) {
			mFolder.RemoveViewByItemInfo(item);
		} else {
			int Count = getChildCount() - 1;
			for (int i = Count; i >= 0; i--) {
				myActor = getChildAt(i);
				if (myActor instanceof Icon3D) {
					ItemInfo info = ((Icon3D) myActor).getItemInfo();
					if (item.equals(info) && (mInfo.opened == false)) {
						removeView(myActor);
						myActor = null;
						bFind = true;
						break;
					}
				}
			}
			if (bFind && folderIconPath == null) {
				folder_layout(true);
			}
			}
		}
	}

	@Override
	public void onAdd(ItemInfo item) {
		// TODO Auto-generated method stub
		if (mCurNumItems < mMaxNumItems) {
			mCurNumItems++;
		}

	}

	@Override
	public void onRemove(ItemInfo item) {
		// TODO Auto-generated method stub

		if (mCurNumItems > 0) {
			mCurNumItems--;
		}
		RemoveViewByItemInfo(item);
	}

	@Override
	public void onTitleChanged(CharSequence title) {
		// TODO Auto-generated method stub
		mFolderName = title.toString();
		this.setTag(mFolderName);
		viewParent.onCtrlEvent(this, MSG_UPDATE_VIEW);
	}

	private void startSequenceAnim(View3D cur_view, float startx, float starty) {
		float pos_y = folder_front_height + R3D.workspace_cell_width / 2;
		Timeline cur_view_line = Timeline.createSequence();
		if (folder_style == folder_iphone_style) {
			pos_y = pos_y - 3 * R3D.workspace_cell_width / 4;
			addDropNode((Icon3D) cur_view);
			getPos(mCurNumItems - 1);
			cur_view.setPosition(mPosx, mPosy);
			cur_view.stopAllTween();
			cur_view_line.push(Tween
					.to(cur_view, View3DTweenAccessor.SCALE_XY, 0.2f)
					.target(getScaleFactor(mCurNumItems - 1),
							getScaleFactor(mCurNumItems - 1))
					.ease(Linear.INOUT));
		} else {
			// addView(cur_view);

			addDropNode((Icon3D) cur_view);
			getPos(0);
			cur_view.stopAllTween();
			// cur_view.setPosition(folder_front.x, folder_front.y+icon_pos_y);
			cur_view.setPosition(mPosx, mPosy);
			cur_view_line.push(Tween
					.to(cur_view, View3DTweenAccessor.POS_XY, 0.4f)
					.target(cur_view.x, pos_y).ease(Linear.INOUT));
		}

		// cur_view_line.push(Tween.to(cur_view,View3DTweenAccessor.POS_XY
		// ,3f).target(cur_view.x,cur_view.y).ease(Linear.INOUT));
		animation_line.push(cur_view_line);

	}

	public void buildChildParallelAnim(Timeline childTween, View3D myActor,
			float duration, float degree, float targetX, float targetY) {
		if (childTween == null || myActor == null) {
			return;
		}
		myActor.setRotationVector(0, 0, 1);
		childTween.push(Tween
				.to(myActor, View3DTweenAccessor.ROTATION, duration)
				.target(degree, 0, 0).ease(Linear.INOUT));
		childTween.push(Tween
				.to(myActor, View3DTweenAccessor.SCALE_XY, duration)
				.target(getScaleFactor(0), getScaleFactor(0))
				.ease(Linear.INOUT));
		childTween.push(Tween.to(myActor, View3DTweenAccessor.POS_XY, duration)
				.target(targetX, targetY, 0).ease(Linear.INOUT));
	}

	private void folder_layout_poweron_rotate() {
		View3D myActor;
		int iconIndex = 0;
		int Count = getChildCount() - 1;
		Log.d("testtetstest", "Folder Count:" + mCurNumItems);
		for (int i = Count; i >= 0; i--) {
			myActor = getChildAt(i);

			if (myActor instanceof Icon3D) {
				myActor.stopAllTween();
				getPos(iconIndex);
				buildChildTrans(myActor, rotateDegree, mPosx, mPosy);
				if (iconIndex > R3D.folder_transform_num - 1) {
					myActor.hide();
				}
				changeTextureRegion(myActor, getIconBmpHeight());
				iconIndex++;
			}

		}
		Log.d("testtetstest", "iconIndex=" + iconIndex);
	}

	private void folder_layout_scale_without_anim() {
		int iconIndex = 0;
		int Count = getChildCount();
		View3D myActor;
		stopAnimation();

		for (int i = 0; i < Count; i++) {
			myActor = getChildAt(i);
			if (myActor instanceof Icon3D) {
				myActor.stopAllTween();
				changeTextureRegion(myActor, getIconBmpHeight());
				myActor.setScale(getScaleFactor(iconIndex),
						getScaleFactor(iconIndex));
				getPos(iconIndex);
				myActor.setPosition(mPosx, mPosy);
				if (iconIndex >= R3D.folder_transform_num) {
					myActor.hide();
				}
				iconIndex++;
			}
		}
	}

	private void folder_layout_poweron() {
		if (folder_style == folder_rotate_style) {
			folder_layout_poweron_rotate();
		} else {
			folder_layout_scale_without_anim();
		}

	}

	public float getFolderFrontHeight() {
		if (folder_front != null) {
			return folder_front.height;
		} else {
			return R3D.folder_front_height;
		}

	}

	private void buildChildTrans(View3D myActor, float degree, float targetX,
			float targetY) {
		myActor.setRotation(degree);
		myActor.setScale(getScaleFactor(0), getScaleFactor(0));
		myActor.setPosition(targetX, targetY);
	}

	private void folder_layout_rotate_without_anim() {
		stopAnimation();
		folder_layout_poweron_rotate();
	}

	private void folder_layout_rotate(boolean bRotation) {
		Timeline viewTween = null;
		View3D myActor;
		stopAnimation();

		if (bRotation) {
			animation_line = Timeline.createParallel();
		}
		int iconIndex = 0;
		int Count = getChildCount() - 1;
		for (int i = Count; i >= 0; i--) {
			myActor = getChildAt(i);
			myActor.stopAllTween();
			viewTween = null;
			if (bRotation) {
				viewTween = Timeline.createParallel();
			}
			if (myActor instanceof Icon3D) {

				if (bRotation) {
					changeTextureRegion(myActor, getIconBmpHeight());
					getPos(iconIndex);
					buildChildParallelAnim(viewTween, myActor, 0.3f,
							rotateDegree, mPosx, mPosy);
					if (iconIndex > R3D.folder_transform_num - 1) {
						myActor.hide();
					}
					iconIndex++;
					animation_line.push(viewTween);
				} else {
					// myActor.setRotation(0);
					// myActor.setOrigin(0, 0);
					myActor.setPosition(folder_front.x + myActor.x,
							(float) (folder_front.y + myActor.y));
				}

			}
		}
		if (bRotation) {
			animation_line.start(View3DTweenAccessor.manager)
					.setUserData(animation_line_rotation).setCallback(this);
		}
	}

	private void getAndroid4Pos(int index) {
		rotateDegree = 0;
		mPosx = (int) (folder_front.x + R3D
				.getInteger("folder_front_margin_offset"));
		// mPosy=(int)
		// (folder_front.height-getIconBmpHeight())-R3D.getInteger("folder_front_margin_offset");
		mPosy = (int) (folder_front.height - getIconBmpHeight() + R3D
				.getInteger("folder_front_margin_offset"));
		if (index == 1) {
			mPosx += R3D.folder_icon_rotation_offsetx;
			mPosy += R3D.folder_icon_rotation_offsety;
		} else if (index >= 2) {
			mPosx += folder_front_bmp.getWidth() / 3;
			mPosy += folder_front_bmp.getHeight() / 3;
		}

	}

	void getPos(int index) {
		if (folder_style == folder_rotate_style) {
			rotateDegree = 0;
			mPosx = (int) folder_front.x;
			mPosy = (int) (folder_front.y + folder_front.height - getIconBmpHeight()
					* getScaleFactor(0));
			if (index == 1) {
				rotateDegree = -R3D.folder_icon_rotation_degree;
				mPosx += R3D.folder_icon_rotation_offsetx;
				mPosy += R3D.folder_icon_rotation_offsety;
			} else if (index == 2) {
				rotateDegree = R3D.folder_icon_rotation_degree;
				mPosx -= R3D.folder_icon_rotation_offsetx;
				mPosy += R3D.folder_icon_rotation_offsety;
			}
		}

		else {

			int mCountY = icon_row_num;
			int mCountX = R3D.folder_transform_num / mCountY;
			if (R3D.getInteger("folder_style") == 1
					&& R3D.folder_transform_num == 3) {
				getAndroid4Pos(index);
				return;
			}

			float cellWidth = (folder_front_bmp.getWidth()) / (float) mCountX;
			float cellHeight = (folder_front_bmp.getHeight()
					- folder_front_padding_top - folder_front_padding_bottom)
					/ mCountY;

			rotateDegree = 0;
			if (index >= R3D.folder_transform_num) {
				index = R3D.folder_transform_num - 1;
			}
			int titleHeight = titleTexture.getRegionHeight();
			// 外边�?workpspacecell 到folder_front_bmp的边�?
			float padding_out = (R3D.workspace_cell_width - folder_front_bmp
					.getWidth()) / 2f;

			if (R3D.folder_transform_num == 4) {
				// icon到folder的内边距
				float padding_inner = (cellWidth - R3D.workspace_cell_width
						* scaleFactor) / 2f;
				mPosx = (padding_out + padding_inner
						+ folder_front_padding_left / 2 + (index % mCountX)
						* (cellWidth - folder_front_padding_left));
			} else if (R3D.folder_transform_num == 9) {
				cellWidth = (folder_front_bmp.getWidth()
						- folder_front_padding_left - folder_front_padding_right)
						/ mCountX;
				float mGapX = ((cellWidth - R3D.workspace_cell_width
						* scaleFactor) / (mCountX - 1));
				mPosx = padding_out + (index % mCountX) * (cellWidth + mGapX)
						+ folder_front_padding_left;
			}

			// ok版本
			// mPosx = (int) Math
			// .ceil(((R3D.workspace_cell_width / (float) mCountX -
			// R3D.workspace_cell_width
			// * scaleFactor) / 2f)
			// + (index % mCountX) * cellWidth);
			mPosy = (titleHeight + folder_front_padding_bottom
					+ folder_front_height - R3D.folder_front_height
					+ DefaultLayout.app_icon_size / 10f + icon_row_num
					* cellHeight - cellHeight - (index / mCountX) * cellHeight);
			if (mInfo.opened == true) {
				//zhujieping add
				if(ThemeManager.getInstance().getBoolean("miui_v5_folder")
						|| DefaultLayout.miui_v5_folder){
					mPosx += mInfo.x;
					mPosy += mInfo.y;
				}else{
				mPosx += (Utils3D.getScreenWidth() - R3D.folder_front_width) / 2;
				mPosy += R3D.folder_group_bottom_margin;
				}
			}
			if (R3D.folder_transform_num == 6) {
				mPosy += folder_front_padding_bottom;
			}

			//xiatian add start	//folder transform icon offset
			mPosx += R3D.Folder_Transform_Icon_Offset_X;
			if (mInfo.opened == false)
				mPosy += R3D.Folder_Transform_Icon_Offset_Y;
			//xiatian add end
			
		}
	}
	private void folder_layout_scale(boolean bAnim) {
		int iconIndex = 0;
		int Count = getChildCount();
		View3D myActor;
		float duration = 0.3f;
		Timeline viewTween = null;
		stopAnimation();
		if (bAnim) {
			animation_line = Timeline.createParallel();
		}
		for (int i = 0; i < Count; i++) {
			myActor = getChildAt(i);
			if (myActor instanceof Icon3D) {
           	//teapotXu add start for Folder in Mainmenu && shake
            	if(DefaultLayout.mainmenu_folder_function == true)
            	{
            		//because stopAllTween() does not have effect
            		myActor.stopTween();
            		//then start shake animation
            		if(myActor instanceof Icon3D)
            		{
            			//if(!(myActor instanceof Widget3DVirtual))
            			{
            				((Icon3D) myActor).reset_shake_status();
            				if(((Icon3D) myActor).getUninstallStatus() == true)
            				{
            					((Icon3D) myActor).shake(true);
            				}
            			}
            		}
            	}
            	else
            	{
                	myActor.stopAllTween();
            	}
				//myActor.stopAllTween();
        	//teapotXu add end for Folder in Mainmenu					
				if (bAnim) {
					viewTween = null;
					viewTween = Timeline.createParallel();
					changeTextureRegion(myActor, getIconBmpHeight());
					
					myActor.setScale(getScaleFactor(iconIndex),
							getScaleFactor(iconIndex));

					getPos(iconIndex);
					viewTween.push(Tween
							.to(myActor, View3DTweenAccessor.POS_XY, duration)
							.target(mPosx, mPosy, 0).ease(Linear.INOUT));
					if (iconIndex >= R3D.folder_transform_num) {
						myActor.hide();
					}
					animation_line.push(viewTween);
					iconIndex++;

				} else {
					myActor.setPosition(folder_front.x + myActor.x,
							folder_front.y + myActor.y);
				}
			}
		}

		if (bAnim) {
			animation_line.start(View3DTweenAccessor.manager)
					.setUserData(animation_line_rotation).setCallback(this);
		}

	}

	private void folder_layout_without_anim() {
		if (folder_style == folder_rotate_style) {
			folder_layout_rotate_without_anim();
		} else {
			folder_layout_scale_without_anim();
		}
	}

	public void folder_layout(boolean bRotation) {
		if (folder_style == folder_rotate_style) {
			folder_layout_rotate(bRotation);
		} else {
			folder_layout_scale(bRotation);
		}
	}

	public int getFolderIconNum() {
		return mCurNumItems;
	}

	@Override
	public void onItemsChanged() {
		// TODO Auto-generated method stub
		onLeafCountChanged(mCurNumItems);
	}

	@Override
	public boolean onDrop(ArrayList<View3D> list, float x, float y) {
		// TODO Auto-generated method stub
		Log.v("test12345", " folderIcon3D onDrop");
		com.badlogic.gdx.graphics.Color selectcolor = new com.badlogic.gdx.graphics.Color(
				1, 1, 1, 1);
		int Count = list.size();
		boolean bNeedDeal = true;
		if (list.get(0) instanceof Icon3D) {
			bNeedDeal = true;
		} else {
			bNeedDeal = false;
		}
		if (bNeedDeal == false) {
			bOnDrop = false;
			return false;
		}

		boolean bCanStartAnimation = false;
		bOnDrop = true;
		if (Count + mCurNumItems > mMaxNumItems) {
			for (View3D view : list) {

				if (view instanceof Icon3D) {
					ItemInfo itemInfo = ((Icon3D) view).getItemInfo();

					itemInfo.cellX = -1;
					itemInfo.cellY = -1;

					bCanStartAnimation = true;
					view.setColor(selectcolor);
					((Icon3D) view).cancelSelected();
				}
			}
			if (bCanStartAnimation == true) {
				this.setTag(list);
				bOnDrop = false;
				viewParent.onCtrlEvent(this, MSG_FOLDERICON_BACKTO_ORIG);
				SendMsgToAndroid.sendOurToastMsg(R3D.folder3D_full);
			}

			return true;
		}
		stopAnimation();
		animation_line = Timeline.createParallel();

		for (View3D view : list) {

			if (view instanceof Icon3D) {
				view.setColor(selectcolor);
				((Icon3D) view).cancelSelected();
				if (view.getHeight() == getIconBmpHeight()) {
					changeOrigin(view);
				}
				changeTextureRegion(view, getIconBmpHeight());
				startSequenceAnim(view, x, y);
				bCanStartAnimation = true;
			}
		}
		if (bCanStartAnimation) {
			bOnDropIcon = true;
			animation_line.start(View3DTweenAccessor.manager)
					.setUserData(animation_line_ondrop).setCallback(this);
			return true;
		} else {
			stopAnimation();
			return false;
		}
	}

	@Override
	public boolean onDropOver(ArrayList<View3D> list, float x, float y) {
		// TODO Auto-generated method stub
		for (View3D view : list) {

			if (view instanceof Icon3D) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iLoong.launcher.UI3DEngine.ViewGroup3D#draw(com.badlogic.gdx.graphics
	 * .g2d.SpriteBatch, float)
	 */

	protected void drawChildren(SpriteBatch batch, float parentAlpha) {

		if (folder_style == folder_rotate_style) {
			super.drawChildren(batch, parentAlpha);
			return;
		}
		parentAlpha *= color.a;
		if (cullingArea != null) {

		} else {
			if (transform) {
				for (int i = 0; i >= 0; i--) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					if (child instanceof ViewGroup3D) {
						if (child.background9 != null) {
							if (child.is3dRotation())
								child.applyTransformChild(batch);
							batch.setColor(child.color.r, child.color.g,
									child.color.b, child.color.a * parentAlpha);
							child.background9.draw(batch, child.x, child.y,
									child.width, child.height);
							if (child.is3dRotation())
								child.resetTransformChild(batch);
						}
						child.draw(batch, parentAlpha);
						continue;
					}
					if (child.is3dRotation())
						child.applyTransformChild(batch);
					if (child.background9 != null) {
						batch.setColor(child.color.r, child.color.g,
								child.color.b, child.color.a * parentAlpha);
						child.background9.draw(batch, child.x, child.y,
								child.width, child.height);
					}
					child.draw(batch, parentAlpha);
					if (child.is3dRotation())
						child.resetTransformChild(batch);
				}
				if (folder_front.region != null
						&& folder_style == folder_iphone_style) {
					if (folder_front.is3dRotation())
						folder_front.applyTransformChild(batch);
					folder_front.draw(batch, parentAlpha);
					if (folder_front.is3dRotation())
						folder_front.resetTransformChild(batch);
				}

				for (int i = children.size() - 1; i > 0; i--) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					if (child instanceof ViewGroup3D) {
						if (child.background9 != null) {
							if (child.is3dRotation())
								child.applyTransformChild(batch);
							batch.setColor(child.color.r, child.color.g,
									child.color.b, child.color.a * parentAlpha);
							child.background9.draw(batch, child.x, child.y,
									child.width, child.height);
							if (child.is3dRotation())
								child.resetTransformChild(batch);
						}
						child.draw(batch, parentAlpha);
						continue;
					}
					if (child.is3dRotation())
						child.applyTransformChild(batch);
					if (child.background9 != null) {
						batch.setColor(child.color.r, child.color.g,
								child.color.b, child.color.a * parentAlpha);
						child.background9.draw(batch, child.x, child.y,
								child.width, child.height);
					}
					child.draw(batch, parentAlpha);
					if (child.is3dRotation())
						child.resetTransformChild(batch);
				}
				// batch.flush();
			} else {
				float offsetX = x;
				float offsetY = y;
				x = 0;
				y = 0;
				{
					View3D child = children.get(0);
					if (child.visible) {
						child.x += offsetX;
						child.y += offsetY;
						if (child.background9 != null) {
							batch.setColor(child.color.r, child.color.g,
									child.color.b, child.color.a * parentAlpha);
							child.background9.draw(batch, child.x, child.y,
									child.width, child.height);
						}
						child.draw(batch, parentAlpha);
						child.x -= offsetX;
						child.y -= offsetY;
					}
					if (folder_front.region != null
							&& folder_style == folder_iphone_style) {
						//zjp
//						batch.draw(folder_front.region, offsetX
//								+ folder_front.x,
//								offsetY + folder_front.y + folder_front_height
//										- folder_front.getHeight());
						if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
								|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable){
							if (!ishide){
								batch.draw(folder_front.region, offsetX+ folder_front.x,
										offsetY + folder_front.y + folder_front_height
												- folder_front.getHeight(), folder_front.originX, folder_front.originY, 
												folder_front.width, folder_front.height, scaleX, scaleY, 0);
							}
						}else
							batch.draw(folder_front.region, offsetX+ folder_front.x,
								offsetY + folder_front.y + folder_front_height
										- folder_front.getHeight(), folder_front.originX, folder_front.originY, 
										folder_front.width, folder_front.height, scaleX, scaleY, 0);
					
					}
				}
				for (int i = children.size() - 1; i > 0; i--) {
					View3D child = children.get(i);
					if (!child.visible)
						continue;
					child.x += offsetX;
					child.y += offsetY;
					if (child.background9 != null) {
						batch.setColor(child.color.r, child.color.g,
								child.color.b, child.color.a * parentAlpha);
						child.background9.draw(batch, child.x, child.y,
								child.width, child.height);
					}
					child.draw(batch, parentAlpha);
					child.x -= offsetX;
					child.y -= offsetY;
				}
				x = offsetX;
				y = offsetY;
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		
		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect(batch);
		/************************ added by zhenNan.ye end *************************/

		float offsetX = x;
		float offsetY = y;
		// if (folderFrontRegion!=null && folder_style==folder_iphone_style)
		// {
		// batch.draw(folderFrontRegion,
		// offsetX+folder_front.x+(R3D.folder_front_width-folderFrontRegion.getRegionWidth())/2,
		// offsetY+folder_front.y+(R3D.folder_front_width-folderFrontRegion.getRegionWidth())/2);
		// }
		//zhujieping add
		if (titleTexture != null && bDisplayFolderName == true) {
			batch.draw(titleTexture, (int) (offsetX + folder_front.x),
					(int) (offsetY + folder_front.y),
					titleTexture.getRegionWidth(),
					titleTexture.getRegionHeight());
		}
		super.draw(batch, parentAlpha);
		
		//teapotXu_20130411 add start: add folder_cover_plate_pic
		if(folderCoverPlateTexture != null)
		{
			batch.draw(folderCoverPlateTexture, 
					(offsetX + folder_front.x),
					(offsetY + folder_front.y),
					folderCoverPlateTexture.getRegionWidth(),
					folderCoverPlateTexture.getRegionHeight());		
		}
		//teapotXu_20130411 add end
		

//		if (titleTexture != null && bDisplayFolderName == true) {
//			batch.draw(titleTexture, (int) (offsetX + folder_front.x),
//					(int) (offsetY + folder_front.y),
//					titleTexture.getRegionWidth(),
//					titleTexture.getRegionHeight());
//		}
		if (folder_style == folder_rotate_style && foldernumTexture != null) {
			num_pos_x = offsetX + folder_front.x + R3D.workspace_cell_width
					- frontBmpLeftPadding - R3D.folder_num_circle_width
					+ (R3D.folder_num_circle_width - R3D.folder_num_width) / 2;

			num_pos_y = offsetY + folder_front.y + folder_front_height
					- R3D.folder_gap_height - R3D.folder_num_circle_width
					- R3D.folder_num_offset_y
					+ (R3D.folder_num_circle_width - R3D.folder_num_height) / 2;

			batch.draw(foldernumTexture, num_pos_x, num_pos_y,
					R3D.folder_num_width, R3D.folder_num_height);
		}

		if (mInfo.contents.size() > 0
				&& clingState == ClingManager.CLING_STATE_WAIT
				&& this.getItemInfo().cellX < 2
				&& (this.getItemInfo().cellY == 0 || this.getItemInfo().cellY == 2)) {
			clingState = ClingManager.getInstance().fireFolderCling(this);
		}

		if (clingState == ClingManager.CLING_STATE_SHOW) {
			if (this.getItemInfo().cellX >= 2 || this.getItemInfo().cellY == 1
					|| this.getItemInfo().cellY == 3) {
				clingState = ClingManager.CLING_STATE_WAIT;
				ClingManager.getInstance().resetFolderCling(this);
			}
		}
	}

	@Override
	public void show() {

		super.show();
		if (clingState == ClingManager.CLING_STATE_SHOW) {
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}

	@Override
	public void hide() {

		super.hide();
		if (clingState == ClingManager.CLING_STATE_SHOW) {
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}

	@Override
	public boolean visible() {
		if (viewParent == null)
			return false;
		if (!viewParent.isVisible())
			return false;
		return this.isVisible()
				&& viewParent.onCtrlEvent(this, MSG_FOLDER_CLING_VISIBLE);
	}

	@Override
	public int getClingPriority() {
		// TODO Auto-generated method stub
		return ClingManager.FOLDER_CLING;
	}

	@Override
	public void dismissCling() {
		clingState = ClingManager.CLING_STATE_DISMISSED;
	}

	@Override
	public void setPriority(int priority) {
		// TODO Auto-generated method stub

	}
	
	/************************ added by zhenNan.ye begin *************************/
	private void drawParticleEffect(SpriteBatch batch)
	{
		if (DefaultLayout.enable_particle)
		{
			if (ParticleManager.particleManagerEnable)
			{
				if (ParticleManager.dropEnable)
				{
					Tween tween = getTween();
					if (tween != null)
					{
						float targetValues[] = tween.getTargetValues();
						float iconHeight = Utils3D.getIconBmpHeight();
						float targetCenterX = targetValues[0]+width/2;
						float targetCenterY = targetValues[1]+(height-iconHeight)+iconHeight/2;
						float curCenterX = x+width/2;
						float curCenterY = y+(height-iconHeight)+iconHeight/2;
						if (Math.abs(curCenterX-targetCenterX) < 2 
								|| Math.abs(curCenterY-targetCenterY) < 2)
						{
							stopParticle(ParticleManager.PARTICLE_TYPE_NAME_DROP);
							startParticle(ParticleManager.PARTICLE_TYPE_NAME_DROP, targetCenterX, targetCenterY);
							ParticleManager.dropEnable = false;
						}
					}
				}
				
				drawParticle(batch);
			}
		}
	}
	/************************ added by zhenNan.ye end ***************************/

	// @Override
	// protected void drawChildren (SpriteBatch batch, float parentAlpha) {
	// parentAlpha *= color.a;
	//
	// if (transform) {
	// for (int i = 0; i < children.size(); i++) {
	// View3D child = children.get(i);
	// if (!child.visible) continue;
	// batch.setColor(child.color.r, child.color.g, child.color.b, child.color.a
	// * parentAlpha);
	// // if(child.background9 != null){
	// //
	// child.background9.draw(batch,child.x,child.y,child.width,child.height);
	// // }
	// if(child instanceof ViewGroup3D)
	// child.draw(batch, parentAlpha);
	// else{
	//
	// if (child.region.getTexture() == null) continue;
	// if (child.scaleX == 1 && child.scaleY == 1 && child.rotation == 0)
	// batch.draw(child.region, child.x, child.y,child.width, child.height);
	// else
	// batch.draw(child.region, child.x, child.y, child.originX, child.originY,
	// child.width, child.height, child.scaleX,child.scaleY, child.rotation);
	// }
	// }
	// }
	// // batch.flush();
	// }
	//zhujieping add start
	private void blurInitial()
	{
		if (!blurInitialised)
		{			
			// suyu
			int fboWidth = (int)(Gdx.graphics.getWidth() * DefaultLayout.fboScale);
			int fboHeight = (int)(Gdx.graphics.getHeight() * DefaultLayout.fboScale);
			fbo = new FrameBuffer(Format.RGBA8888, fboWidth, fboHeight, false);
			fbo2 = new FrameBuffer(Format.RGBA8888, fboWidth, fboHeight, false);
			fboRegion = new TextureRegion(fbo.getColorBufferTexture(), 0,
					fbo.getHeight(), fbo.getWidth(), -fbo.getHeight());
			fboRegion2 = new TextureRegion(fbo2.getColorBufferTexture(), 0,
					fbo2.getHeight(), fbo2.getWidth(), -fbo2.getHeight());
			if (blurBatch == null){
				blurBatch = new SpriteBatch();
				Matrix4 pMat = new Matrix4();
				pMat.setToOrtho2D(0, 0, fboWidth, fboHeight);
				blurBatch.setProjectionMatrix(pMat);
				blurBatch.setShader(shader);
			}
			blurInitialised = true;
			captureCurScreen = true;
			blurCount = 0;
		}
	}
	
	
	private void blurFree()
	{
		blurInitialised = false;
		blurCompleted = true;
		
		if (liveWallpaperActive) {
			if (lwpBackView != null) {
				lwpBackView.remove();
				lwpBackView.dispose();
				lwpBackView = null;
			}
		}

		if (blurredView != null)
		{
			blurredView.remove();
			blurredView.dispose();
			blurredView = null;
		}
		
//		if (blurBatch != null)
//		{
//			blurBatch.dispose();
//			blurBatch = null;
//		}
		if (fbo != null)
		{
			fbo.dispose();
			fbo = null;
		}
		
		if (fbo2 != null)
		{
			fbo2.dispose();
			fbo2 = null;
		}
		if (fboRegion.getTexture() != null){
			fboRegion.getTexture().dispose();
			fboRegion = null;
		}
		if (fboRegion2.getTexture() != null){
			fboRegion2.getTexture().dispose();
			fboRegion2 = null;
		}
	}
	
	// suyu
	public static ShaderProgram createDefaultShader() {
		if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
				|| DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable)
		{
			FileHandle vShader = null;
			FileHandle fShader = null;
			vShader = Gdx.files.internal("theme/folder/blur.vert.txt");
			fShader = Gdx.files.internal("theme/folder/blur.frag.txt");
			String vShaderString;
			String fShaderString;
			vShaderString = vShader.readString();
			fShaderString = fShader.readString();
			ShaderProgram shader = new ShaderProgram(vShaderString, fShaderString);
			if (shader.isCompiled() == false)
				throw new IllegalArgumentException("couldn't compile shader "
						+ ": " + shader.getLog());
			return shader;
		}
		return null;
	}

	// suyu
	public  void renderTo(FrameBuffer src, FrameBuffer des) {
		if (des != null) {
			des.begin();
		}
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT );
		if (des == null) {
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.1f);
		}
		blurBatch.begin();
		TextureRegion region = null;
		shader.setUniformf("xStep", 1.0f / fbo.getWidth());
		shader.setUniformf("yStep", 1.0f / fbo.getHeight());
		shader.setUniformi("radius", DefaultLayout.blurRadius);
		if (src == fbo) {
			region = fboRegion;
			shader.setUniformi("vertical", 0);
		} else if (src == fbo2) {
			region = fboRegion2;
			shader.setUniformi("vertical", 1);
		}
		blurBatch.draw(region, 0, 0);
		blurBatch.end();
		if (des != null) {
			des.end();
		}
	}
	public static void genWallpaperTextureRegion()
	{
		Gdx.app.postRunnable(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WallpaperManager wallpaperManager = WallpaperManager.getInstance(iLoongLauncher.getInstance());
			    WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
			    if (wallpaperInfo != null) {
			    	liveWallpaperActive = true;
			    	wallpaperTextureRegion = null;
			    } else {
			    	liveWallpaperActive = false;
			    	
			    	Drawable drawable = wallpaperManager.getDrawable();
				    Bitmap wallpaperBitmap = ((BitmapDrawable) drawable).getBitmap();
				    BitmapTexture texture = new BitmapTexture(wallpaperBitmap);
				    Texture t =  wallpaperTextureRegion.getTexture();
				    if(t != null)t.dispose();
					wallpaperTextureRegion.setRegion(texture);
			    }
			}
		});
	}

	public void calcWallpaperOffset()
	{
		if (wallpaperTextureRegion != null) {
			int wpWidth = wallpaperTextureRegion.getTexture().getWidth();
			int screenWidth = Utils3D.getScreenWidth();
			if (wpWidth > screenWidth)
			{
				int curScreen = iLoongLauncher.getInstance().getCurrentScreen();
				int screenNum = iLoongLauncher.getInstance().getScreenCount();
				int gapWidth = wpWidth - screenWidth;
				wpOffsetX = -(int)((float)gapWidth*curScreen/(screenNum-1));
			} else {
				wpOffsetX = 0;
			}
		}
	}
	
	private void MiuiV5FolderBlurFinish()
	{
		blurBegin = false;
		if (blurCount < (DefaultLayout.blurInterate<<1))
		{ 
			blurCompleted = false;
		} else {
			if (blurredView == null)
			{
				Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
				float scaleFactor = 1/DefaultLayout.fboScale;
				blurredView = new ImageView3D("blurredView", fboRegion);
				blurredView.setScale(scaleFactor, scaleFactor);
				blurredView.show();
				blurredView.setPosition(blurredView.getWidth()/2+(Gdx.graphics.getWidth()/2-blurredView.getWidth()), 
						blurredView.getHeight()/2+(Gdx.graphics.getHeight()/2-blurredView.getHeight()));
				
				//teapotXu add start for Folder in Mainmenu 
				if(DefaultLayout.mainmenu_folder_function == true
					&& this.mInfo.opened == true	
					&& this.from_Where == FolderIcon3D.FROM_GRIDVIEW3D 
					&& this.exit_to_where == FolderIcon3D.TO_APPLIST)
				{
					viewParent.transform = false;
					viewParent.setScale(1.0f, 1.0f);	
					
					this.addViewAt(this.mFolderIndex, this.mFolderMIUI3D);
					//viewParent.addViewBefore(this, this.blurredView);
					this.addView(this.blurredView);
					//viewParent.addView( this.blurredView);
					//color.a = 0f;
					
					return;
				}
				//teappotXu add end 
				
				root.transform = false;
				root.setScale(1.0f, 1.0f);
				if (getFromWhere() == FROM_CELLLAYOUT) {
					addViewAt(mFolderIndex, mFolderMIUI3D);
					addView(blurredView);
					root.addView(this);
					root.hideOtherView();
					
					if (liveWallpaperActive) {
						lwpBackView = new ImageView3D("lwpBackView", ThemeManager
								.getInstance().getBitmap(
										"theme/pack_source/translucent-black.png"));
						lwpBackView.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						lwpBackView.setPosition(0, 0);
						addView(lwpBackView);
					}
				} else {
					if (liveWallpaperActive) {
						lwpBackView = new ImageView3D("lwpBackView",
								ThemeManager.getInstance().getBitmap(
										"theme/pack_source/translucent-black.png"));
						lwpBackView.setSize(Gdx.graphics.getWidth(),
								Gdx.graphics.getHeight());
						lwpBackView.setPosition(0, 0);
						root.addView(lwpBackView);
					}
					
					this.addViewAt(this.mFolderIndex, this.mFolderMIUI3D);
					root.addView( this.blurredView);
					root.addView(this);
					root.hideOtherView();
					color.a = 0f;
					ishide = true;
				}				
			}
		}
	}
	
	public int getViewIndex(View3D view)
	{
		return children.indexOf(view);
	}
	public void closeFolderStartAnimMIUI() {
		mInfo.opened = false;
		setFolderIconSize(mInfo.x, mInfo.y, 0, 0);
		if (folder_style == FolderIcon3D.folder_rotate_style) {
			folder_layout_rotate_without_anim();
		} else {
			Log.v("miui3D",
					"FolderICON3D closeFolderStartAnimMIUI mInfo.opened="
							+ mInfo.opened);
			folder_layout_scale_without_anim();
		}
//		if (getFromWhere() == FROM_HOTSEAT) {
//			if (DefaultLayout.hotseat_hide_title) {
//				setSize(R3D.workspace_cell_width, getIconBmpHeight());
//			}
//		}
		closeFolder();
	}

	//zhujieping add end
}