package com.iLoong.launcher.Folder3D;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.IconCache;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.cooee.android.launcher.framework.LauncherSettings.Favorites;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
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
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.CustomShortcutIcon;
import com.iLoong.launcher.HotSeat3D.HotGridView3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupCircled3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
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


public class FolderIcon3D extends ViewGroupCircled3D implements FolderListener , DropTarget3D , IconBase3D
{
	
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
	// teapotXu add start for Folder in Mainmenu
	private static int applistPreMode = AppList3D.APPLIST_MODE_NORMAL;
	public static int applistMode = AppList3D.APPLIST_MODE_NORMAL;
	public static final int FROM_APPLIST = 2;
	public static final int MSG_FOLDERICON_TO_APPLIST = 1009;
	public static final int MSG_FOLDERICON_FROME_APPLIST = 1010;
	public static final int TO_CELLLAYOUT = 0;
	public static final int TO_APPLIST = 1;
	public int exit_to_where = TO_CELLLAYOUT;
	public boolean is_applist_folder_no_refresh = true;
	// teapotXu add end for Folder in Mainmenu
	public static final int FROM_CELLLAYOUT = 0;
	public static final int FROM_HOTSEAT = 1;
	private int from_Where = FROM_CELLLAYOUT;
	private final int CIRCLE_DST_TOLERANCE = 50;
	public Folder3D mFolder;
	public FolderIconPath folderIconPath = null;
	public UserFolderInfo mInfo;
	private String mFolderName;
	TextureRegion titleTexture;
	// teapotXu_20130411 add start: add folder cover plate pic
	private TextureRegion folderCoverPlateTexture = null;
	// teapotXu_20130411 add end
	ImageView3D folder_back;
	public ImageView3D folder_front;
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
	public static TextureRegion folderFrontWithoutShadowRegion = null;
	public static HashMap<String , Bitmap> bitmapMap = new HashMap<String , Bitmap>();
	public static HashMap<String , TextureRegion> regionMap = new HashMap<String , TextureRegion>();
	private static float frontBmpLeftPadding = 0;
	private float IconBmpHeight = 0;
	private int folder_front_padding_left;
	private int folder_front_padding_top;
	private int folder_front_padding_right;
	private int folder_front_padding_bottom;
	// private boolean bNeedLayoutDelay=false;
	/* 从侧拉条开始构建的文件�? */
	// added by zhujieping start
	// suyu
	public FrameBuffer fbo = null;
	public FrameBuffer fbo2 = null;
	public static SpriteBatch blurBatch = null;
	public TextureRegion fboRegion = null;
	public TextureRegion fboRegion2 = null;
	public static ShaderProgram shader = null;
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
	public String iconResource = null;
	public Bitmap iconBitmap = null;
	public TextureRegion iconRegion = null;
	// xiatian add start //for mainmenu sort by user
	public boolean uninstall = false;
	private boolean isShake = false;
	private Tween shakeAnimation = null;
	private final static float SHAKE_ROTATE_ANGLE = 5.0f;
	public static TextureRegion uninstallTexture;
	protected static int stateIconWidth;
	protected static int stateIconHeight;
	public static final int MSG_FOLDERICON_BREAK_UP_SELF_IN_APPLIST = 1011;
	private float shakeTime = 0.25f;
	private final static float SHAKE_SCALE_MAX = 1.0f;
	private final static float SHAKE_SCALE_MIN = 0.98f;
	private final static float FIRST_SHAKE_TIME_MAX = 0.25f;
	private final static float FIRST_SHAKE_TIME_MIN = 0.1f;
	private boolean m_bOpenFolderAnim;
	
	// xiatian add end
	// static {
	// if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
	// || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable)
	// {
	// genWallpaperTextureRegion();
	// }
	// }
	// added by zhujieping end
	public FolderIcon3D(
			UserFolderInfo folderInfo )
	{
		this( "folder" , folderInfo );
	}
	
	public FolderIcon3D(
			String name ,
			UserFolderInfo folderInfo )
	{
		super( name );
		iconResource = folderInfo.iconResource;
		from_Where = FROM_CELLLAYOUT;
		mFolderName = folderInfo.title.toString();
		mInfo = folderInfo;
		this.setTag( folderInfo );
		folder_front_padding_left = R3D.getInteger( "folder_front_padding_left" );
		if( folder_front_padding_left == -1 )
		{
			folder_front_padding_left = R3D.getInteger( "folder_front_margin_offset" );
		}
		folder_front_padding_top = R3D.getInteger( "folder_front_margin_top" );
		if( folder_front_padding_top == -1 )
		{
			folder_front_padding_top = R3D.getInteger( "folder_front_margin_offset" );
		}
		folder_front_padding_right = R3D.getInteger( "folder_front_padding_right" );
		if( folder_front_padding_right == -1 )
		{
			folder_front_padding_right = R3D.getInteger( "folder_front_margin_offset" );
		}
		folder_front_padding_bottom = R3D.getInteger( "folder_front_padding_bottom" );
		if( folder_front_padding_bottom == -1 )
		{
			folder_front_padding_bottom = R3D.getInteger( "folder_front_margin_offset" );
		}
		mMaxNumItems = R3D.folder_max_num;
		mCurNumItems = folderInfo.contents.size();
		scaleFactor = R3D.folder_icon_scale_factor / 100f;
		rotateDegree = R3D.folder_icon_rotation_degree;
		icon_row_num = R3D.folder_icon_row_num;
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			folder_style = folder_rotate_style;
		}
		else
		{
			folder_style = folder_iphone_style;
			// this.transform = true;
			folder_back = null;
		}
		mInfo.addListener( this );
		onLeafCountChanged( mCurNumItems );
		onInitTitle( folderInfo.title.toString() );
		// teapotXu_20130411 add start: add folder_cover_plate_pic
		createFolderCoverPlatePicTexture();
		// teapotXu_20130411 add end
		this.transform = true;
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_folder_function )
		{
			uninstallTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) , true ) );
			stateIconWidth = R3D.workspace_multicon_width;
			stateIconHeight = R3D.workspace_multicon_height;
		}
		// xiatian add end
		buildElements();
	}
	
	public void onThemeChanged()
	{
		folder_front_padding_left = R3D.getInteger( "folder_front_padding_left" );
		if( folder_front_padding_left == -1 )
		{
			folder_front_padding_left = R3D.getInteger( "folder_front_margin_offset" );
		}
		folder_front_padding_top = R3D.getInteger( "folder_front_padding_top" );
		if( folder_front_padding_top == -1 )
		{
			folder_front_padding_top = R3D.getInteger( "folder_front_margin_offset" );
		}
		folder_front_padding_right = R3D.getInteger( "folder_front_padding_right" );
		if( folder_front_padding_right == -1 )
		{
			folder_front_padding_right = R3D.getInteger( "folder_front_margin_offset" );
		}
		folder_front_padding_bottom = R3D.getInteger( "folder_front_padding_bottom" );
		if( folder_front_padding_bottom == -1 )
		{
			folder_front_padding_bottom = R3D.getInteger( "folder_front_margin_offset" );
		}
		scaleFactor = R3D.folder_icon_scale_factor / 100f;
		rotateDegree = R3D.folder_icon_rotation_degree;
		icon_row_num = R3D.folder_icon_row_num;
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			folder_style = folder_rotate_style;
		}
		else
		{
			folder_style = folder_iphone_style;
		}
		changeFolderCoverPlatePicTexture();
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			mFolderMIUI3D.onThemeChanged();
		}
		else
		{
			mFolder.onThemeChanged();
		}
		folder_front.region.setV2( 1 );
		if( bDisplayFolderName )
		{
			folder_front.setSize( R3D.workspace_cell_width , R3D.folder_front_height );
			setSize( R3D.workspace_cell_width , R3D.folder_front_height );
			folder_front_height = R3D.folder_front_height;
		}
		else
		{
			bDisplayFolderName = true;
			changeFolderFrontRegion( true );
		}
		if( folder_style == folder_iphone_style )
		{
			if( folder_back != null )
			{
				this.removeView( folder_back );
				folder_back = null;
			}
			if( folder_front != null )
			{
				this.removeView( folder_front );
				this.addViewAt( 0 , folder_front );
				folder_front.hide();
			}
		}
		else
		{
			if( folder_back == null )
			{
				folder_back = new ImageView3D( "folder_behind" , R3D.findRegion( "widget-folder-bg2" ) );
				folder_back.setSize( R3D.folder_back_width , R3D.folder_back_height );
				addViewAt( 0 , folder_back );
			}
			folder_back.region = R3D.findRegion( "widget-folder-bg2" );
			folder_back.setSize( R3D.folder_back_width , R3D.folder_back_height );
			folder_front.show();
			folder_front.bringToFront();
		}
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			mFolderMIUI3D.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			mFolderMIUI3D.setOrigin( mFolder.width / 2 , mFolder.height / 2 );
		}
		else
		{
			mFolder.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			mFolder.setOrigin( mFolder.width / 2 , mFolder.height / 2 );
		}
		setFolderIconSize( mInfo.x , mInfo.y , 0 , 0 );
		for( int j = 0 ; j < getChildCount() ; j++ )
		{
			View3D view = getChildAt( j );
			if( view == null )
				continue;
			if( view instanceof Icon3D )
			{
				( (Icon3D)view ).onThemeChanged();
			}
		}
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_folder_function )
		{
			uninstallTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) , true ) );
			stateIconWidth = R3D.workspace_multicon_width;
			stateIconHeight = R3D.workspace_multicon_height;
		}
		// xiatian add end
		folder_layout_poweron();
	}
	
	// zhujieping add
	public FolderMIUI3D getMIUI3DFolder()
	{
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
	public float getIconBmpHeight()
	{
		return Utils3D.getIconBmpHeight();
	}
	
	private static Bitmap MergeBmpToPixmap(
			Bitmap folderFront ,
			Bitmap titleBg ,
			boolean ifShadow )
	{
		int textureWidth = R3D.workspace_cell_width;
		int textureHeight = R3D.folder_front_height;
		float padding = ( textureWidth - folderFront.getWidth() ) / 2;
		// if(titleBg != null)canvas.drawBitmap(titleBg,0,textureHeight -
		// titleBg.getHeight(),null);
		if( padding < 0 )
		{
			padding = 0;
		}
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
			Canvas canvas = new Canvas( bmp );
			// if(titleBg != null)canvas.drawBitmap(titleBg,0,textureHeight -
			// titleBg.getHeight(),null);
			canvas.drawBitmap( folderFront , padding , 0 , null );
			folderFront.recycle();
			return bmp;
			// return Utils3D.bmp2Pixmap(bmp);
		}
		else
		{
			return Utils3D.titleToBitmap( folderFront , null , null , null , textureWidth , textureHeight , true , ifShadow );
			// return Utils3D.bmp2Pixmap(temp);
		}
	}
	
	// teapotXu_20130411 add start: add folder cover plate pic
	public void createFolderCoverPlatePicTexture()
	{
		if( folderCoverPlateTexture == null )
		{
			Bitmap folder_cover_plate_bmp = null;
			Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/folder_cover_plate_pic.png" );
			if( null != original_icon_cover_bmp )
			{
				float density = iLoongApplication.getInstance().getResources().getDisplayMetrics().density;
				if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
				{
					folder_cover_plate_bmp = Tools.resizeBitmap( original_icon_cover_bmp , density / 1.5f );
				}
				else
				{
					if( R3D.icon_bg_num > 0 )
					{
						folder_cover_plate_bmp = Bitmap.createScaledBitmap( original_icon_cover_bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
					}
					else
					{
						BitmapDrawable drawable = new BitmapDrawable( (Bitmap)original_icon_cover_bmp );
						drawable.setTargetDensity( iLoongLauncher.getInstance().getResources().getDisplayMetrics() );
						folder_cover_plate_bmp = Utilities.createIconBitmap( drawable , iLoongLauncher.getInstance() );
					}
					original_icon_cover_bmp.recycle();
				}
			}
			if( folder_cover_plate_bmp != null )
			{
				folderCoverPlateTexture = new TextureRegion( new BitmapTexture( MergeBmpToPixmap( folder_cover_plate_bmp , null , true ) , true ) );
			}
		}
	}
	
	public void changeFolderCoverPlatePicTexture()
	{
		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/folder_cover_plate_pic.png" );
		if( original_icon_cover_bmp != null )
		{
			Bitmap folder_cover_plate_bmp = null;
			float density = iLoongApplication.getInstance().getResources().getDisplayMetrics().density;
			if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
			{
				folder_cover_plate_bmp = Tools.resizeBitmap( original_icon_cover_bmp , density / 1.5f );
			}
			else
			{
				if( R3D.icon_bg_num > 0 )
				{
					folder_cover_plate_bmp = Bitmap.createScaledBitmap( original_icon_cover_bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
				}
				else
				{
					BitmapDrawable drawable = new BitmapDrawable( (Bitmap)original_icon_cover_bmp );
					drawable.setTargetDensity( iLoongLauncher.getInstance().getResources().getDisplayMetrics() );
					folder_cover_plate_bmp = Utilities.createIconBitmap( drawable , iLoongLauncher.getInstance() );
				}
				original_icon_cover_bmp.recycle();
			}
			Bitmap temBitmap = MergeBmpToPixmap( folder_cover_plate_bmp , null , true );
			if( folderCoverPlateTexture != null )
			{
				( (BitmapTexture)folderCoverPlateTexture.getTexture() ).changeBitmap( temBitmap , true );
			}
			else
			{
				folderCoverPlateTexture = new TextureRegion( new BitmapTexture( temBitmap , true ) );
			}
			folderCoverPlateTexture.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		else
		{
			if( folderCoverPlateTexture != null )
			{
				folderCoverPlateTexture.getTexture().dispose();
				folderCoverPlateTexture = null;
			}
		}
	}
	
	// teapotXu_20130411 add end
	// onThemeChanged
	public static void changeFrontBmp()
	{
		// so there is no folder exist, no need change font bmp
		if( folderFrontRegion == null )
		{
			return;
		}
		if( folder_front_bmp != null )
		{
			folder_front_bmp.recycle();
			folder_front_bmp = null;
		}
		folder_front_bmp = createFrontBmp();
		frontBmpLeftPadding = getFrontLeftPadding();
		Bitmap frontPixmap = MergeBmpToPixmap( folder_front_bmp , null , true );
		( (BitmapTexture)folderFrontRegion.getTexture() ).changeBitmap( frontPixmap , true );
		folderFrontRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		if( DefaultLayout.hotseat_hide_title )
		{
			Bitmap bitmap = createFrontBmp();
			frontPixmap = MergeBmpToPixmap( bitmap , null , false );
			( (BitmapTexture)folderFrontWithoutShadowRegion.getTexture() ).changeBitmap( frontPixmap , true );
			folderFrontWithoutShadowRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
	}
	
	public static Bitmap createFrontBmp()
	{
		Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/widget-folder-icon.png" );
		Bitmap bitmap = null;
		if( tmp != null )
		{
			float density = iLoongApplication.getInstance().getResources().getDisplayMetrics().density;
			if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
			{
				bitmap = Tools.resizeBitmap( tmp , density / 1.5f );
			}
			else
			{
				if( R3D.icon_bg_num > 0 )
				{
					if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
					{
						bitmap = tmp;
					}
					else
					{
						bitmap = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
						tmp.recycle();
					}
				}
				else
				{
					BitmapDrawable drawable = new BitmapDrawable( (Bitmap)tmp );
					drawable.setTargetDensity( iLoongLauncher.getInstance().getResources().getDisplayMetrics() );
					bitmap = Utilities.createIconBitmap( drawable , iLoongLauncher.getInstance() );
				}
				if( bitmap != tmp )
					tmp.recycle();
			}
		}
		return bitmap;
	}
	
	public void createIconResourceBmp()
	{
		if( iconResource != null )
		{
			Bitmap tmp = null;
			try
			{
				tmp = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( iconResource ) );
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( tmp != null )
			{
				float density = iLoongApplication.getInstance().getResources().getDisplayMetrics().density;
				{
					if( R3D.icon_bg_num > 0 )
					{
						if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
						{
							iconBitmap = tmp;
						}
						else
						{
							iconBitmap = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
							tmp.recycle();
						}
					}
					else
					{
						BitmapDrawable drawable = new BitmapDrawable( (Bitmap)tmp );
						drawable.setTargetDensity( iLoongLauncher.getInstance().getResources().getDisplayMetrics() );
						iconBitmap = Utilities.createIconBitmap( drawable , iLoongLauncher.getInstance() );
					}
					if( iconBitmap != tmp )
						tmp.recycle();
				}
			}
		}
	}
	
	private void folder_front_ToPixmap3D()
	{
		folder_front_bmp = createFrontBmp();
		frontBmpLeftPadding = getFrontLeftPadding();
		if( folderFrontRegion == null )
		{
			Bitmap frontPixmap = MergeBmpToPixmap( folder_front_bmp , null , true );
			folderFrontRegion = new TextureRegion( new BitmapTexture( frontPixmap , true ) );
			folderFrontRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
		if( mInfo.folderid != null && regionMap.get( mInfo.folderid ) == null )
		{
			Bitmap bitmap = bitmapMap.get( mInfo.folderid );
			if( bitmap != null )
			{
				Bitmap frontPixmap = MergeBmpToPixmap( bitmap , null , true );
				TextureRegion region = new TextureRegion( new BitmapTexture( frontPixmap , true ) );
				region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				regionMap.put( mInfo.folderid , region );
			}
		}
		// return folderFrontRegion;   
	}
	
	private static void createFrontWithoutShadowRegion()
	{
		if( folderFrontWithoutShadowRegion == null )
		{
			Bitmap bitmap = createFrontBmp();
			Bitmap frontPixmap = MergeBmpToPixmap( bitmap , null , false );
			folderFrontWithoutShadowRegion = new TextureRegion( new BitmapTexture( frontPixmap , true ) );
			folderFrontWithoutShadowRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
		}
	}
	
	public float getScaleFactor(
			int index )
	{
		// mAnimParams.transX = transX0 + progress * (finalParams.transX -
		// transX0);
		// mAnimParams.transY = transY0 + progress * (finalParams.transY -
		// transY0);
		// mAnimParams.scale = scale0 + progress * (finalParams.scale - scale0);
		if( R3D.getInteger( "folder_style" ) == 1 && R3D.folder_transform_num == 3 )
		{
			if( index == 0 )
			{
				return scaleFactor;
			}
			else if( index == 1 )
			{
				return scaleFactor - 0.1f;
			}
			else if( index >= 2 )
			{
				return scaleFactor - 0.1f;
			}
		}
		return scaleFactor;
	}
	
	public float getPosx()
	{
		return mPosx;
	}
	
	public float getPosy()
	{
		return mPosy;
	}
	
	public float getRotateDegree()
	{
		return rotateDegree;
	}
	
	private void sortItemsByIndex(
			ArrayList<ShortcutInfo> items )
	{
		int i , j;
		int index1 = 0 , index2 = 0;
		ShortcutInfo tempInfo1 = null , tempInfo2 = null;
		if( items == null )
		{
			return;
		}
		int count = items.size();
		for( j = 0 ; j <= count - 1 ; j++ )
		{
			for( i = j ; i < count ; i++ )
			{
				tempInfo1 = items.get( j );
				tempInfo2 = items.get( i );
				index1 = tempInfo1.screen;
				index2 = tempInfo2.screen;
				if( index1 > index2 )
				{
					items.set( j , tempInfo2 );
					items.set( i , tempInfo1 );
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
	public void changeOrigin(
			View3D myActor )
	{
		if( myActor == null )
		{
			return;
		}
		if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) != 1 )
		{
			myActor.setOrigin( myActor.width / 2 , myActor.height / 2 );
		}
		else
		{
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
			if( R3D.folder_transform_num == 3 && R3D.getInteger( "folder_style" ) == 1 )
			{
				myActor.setOrigin( ( R3D.workspace_cell_width - DefaultLayout.app_icon_size ) / 2f , 0 );
			}
			else
			{
				myActor.setOrigin( 0 , 0 );
			}
		}
	}
	
	public void changeTextureRegion(
			View3D myActor ,
			float iconHeight )
	{
		float scale = ( (float)getIconBmpHeight() / (float)R3D.workspace_cell_height );
		if( myActor == null )
		{
			return;
		}
		// Log.d("title",
		// "iconHeight,iconBmp="+iconHeight+","+getIconBmpHeight());
		if( myActor.height == R3D.workspace_cell_height && iconHeight != myActor.height )
		{
			float V2 = myActor.region.getV() + ( myActor.region.getV2() - myActor.region.getV() ) * scale;
			myActor.region.setV2( V2 );
			myActor.setSize( R3D.workspace_cell_width , iconHeight );
			changeOrigin( myActor );
		}
		else if( myActor.height != R3D.workspace_cell_height && iconHeight != myActor.height )
		{
			float V2 = myActor.region.getV() + ( myActor.region.getV2() - myActor.region.getV() ) / scale;
			myActor.region.setV2( V2 );
			myActor.setSize( R3D.workspace_cell_width , iconHeight );
			myActor.setOrigin( myActor.width / 2 , myActor.height / 2 );
		}
	}
	
	public void changeTextureRegion(
			ArrayList<View3D> view3DArray ,
			float iconHeight )
	{
		if( view3DArray == null )
		{
			return;
		}
		View3D myActor;
		int Count = view3DArray.size();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = view3DArray.get( i );
			changeTextureRegion( myActor , iconHeight );
		}
	}
	
	/* 从开机开始创建文件夹内容 */
	public void createAndAddShortcut(
			IconCache iconCache ,
			UserFolderInfo folderInfo )
	{
		if( folderInfo == null || folderInfo.contents == null )
		{
			return;
		}
		mInfo = folderInfo;
		ArrayList<ShortcutInfo> children = folderInfo.contents;
		sortItemsByIndex( children );
		int Count = children.size();
		for( int i = 0 ; i < Count ; i++ )
		{
			ShortcutInfo child = (ShortcutInfo)children.get( i );
			if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || child.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
			{
				Icon3D icon = null;
				if( child.intent != null && child.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
				{
					Bitmap bmp = Bitmap.createBitmap( child.getIcon( iLoongApplication.mIconCache ) );
					float scale = 1f;
					if( bmp.getWidth() != DefaultLayout.app_icon_size || bmp.getHeight() != DefaultLayout.app_icon_size )
					{
						scale = (float)DefaultLayout.app_icon_size / bmp.getWidth();
					}
					if( DefaultLayout.thirdapk_icon_scaleFactor != 1f && !R3D.doNotNeedScale( null , null ) )
					{
						scale = scale * DefaultLayout.thirdapk_icon_scaleFactor;
					}
					if( scale != 1f )
					{
						bmp = Tools.resizeBitmap( bmp , scale );
					}
					icon = new Icon3D( child.title.toString() , bmp , child.title.toString() , Icon3D.getIconBg() );
				}
				else
				{
					icon = new Icon3D( child.title.toString() , R3D.findRegion( ( (ShortcutInfo)child ) ) );
				}
				icon.setItemInfo( (ShortcutInfo)child );
				addViewBefore( folder_front , icon );
			}
			else if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
			{
				View3D virView = DefaultLayout.showDefaultWidgetView( ( (ShortcutInfo)child ) );
				if( virView != null )
					addViewBefore( folder_front , virView );
			}
			else if( child.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT )
			{
				View3D customShortcut = CustomShortcutIcon.createCustomShortcut( (ShortcutInfo)child , true );
				if( customShortcut != null )
				{
					addViewBefore( folder_front , customShortcut );
				}
			}
		}
		mCurNumItems = Count;
		if( mCurNumItems > 0 )
		{
			onItemsChanged();
			folder_layout_poweron();
		}
	}
	
	private void onLeafCountChanged(
			int count )
	{
		if( folder_style == folder_iphone_style )
		{
			if( count == mMaxNumItems && viewParent != null )
			{
				this.setTag( mFolderName );
				viewParent.onCtrlEvent( this , MSG_UPDATE_VIEW );
			}
			else if( count == mMaxNumItems - 1 && viewParent != null )
			{
				this.setTag( mFolderName );
				viewParent.onCtrlEvent( this , MSG_UPDATE_VIEW );
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
	public void addViewBefore(
			View3D actorBefore ,
			View3D actor )
	{
		// TODO Auto-generated method stub
		if( folder_style == folder_rotate_style )
		{
			super.addViewBefore( actorBefore , actor );
		}
		else
		{
			super.addView( actor );
		}
		if( actor instanceof WidgetIcon )
		{
			( (WidgetIcon)actor ).setTag2( this );
		}
	}
	
	private void startAddFolderNodeAnim(
			ArrayList<View3D> mCircleSomething )
	{
		View3D myActor;
		stopAnimation();
		animation_line = Timeline.createParallel();
		ArrayList<Icon3D> Icon3DCircle = new ArrayList<Icon3D>();
		Icon3DCircle.clear();
		int Count = mCircleSomething.size();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = mCircleSomething.get( i );
			if( myActor instanceof Icon3D )
			{
				Icon3DCircle.add( (Icon3D)myActor );
			}
		}
		Count = Icon3DCircle.size();
		float delayFactor = 0;
		if( Count > R3D.folder_max_num / 2 )
		{
			delayFactor = 0.06f;
		}
		else
		{
			delayFactor = 0.1f;
		}
		float pos_x = folder_front.x;
		float pos_y = folder_front.y + folder_front_height + R3D.workspace_cell_width / 2;
		if( folder_style == folder_iphone_style )
		{
			pos_y = pos_y - 3 * R3D.workspace_cell_width / 4;
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = Icon3DCircle.get( i );
			changeTextureRegion( myActor , getIconBmpHeight() );
			animation_line.push( Tween.to( myActor , View3DTweenAccessor.POS_XY , 0.8f ).target( pos_x , pos_y ).ease( Quad.INOUT ).delay( delayFactor * i ) );
		}
		// animation_line.pushPause(0.2f);
		bAnimate = true;
		animation_line.start( View3DTweenAccessor.manager ).setUserData( animation_line_trans ).setCallback( this );
	}
	
	private void startAddFolderNodeAnim()
	{
		View3D myActor;
		stopAnimation();
		float pos_x = folder_front.x;
		float pos_y = folder_front.y + folder_front_height + R3D.workspace_cell_width / 2;
		if( folder_style == folder_iphone_style )
		{
			pos_y = pos_y - 3 * R3D.workspace_cell_width / 4;
		}
		animation_line = Timeline.createParallel();
		int Count = getChildCount();
		float delayFactor = 0;
		if( ( Count - 2 ) > R3D.folder_max_num / 2 )
		{
			delayFactor = 0.06f;
		}
		else
		{
			delayFactor = 0.1f;
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				animation_line.push( Tween.to( myActor , View3DTweenAccessor.POS_XY , 0.8f ).target( pos_x , pos_y ).delay( delayFactor * i ).ease( Quad.INOUT ) );
			}
		}
		// animation_line.pushPause(0.2f);
		bAnimate = true;
		animation_line.start( View3DTweenAccessor.manager ).setUserData( animation_line_trans ).setCallback( this );
	}
	
	public void addIcon(
			Icon3D newIcon )
	{
		ItemInfo info = newIcon.getItemInfo();
		if( mInfo.opened == true )
		{
			info.screen = mInfo.contents.size();
			mInfo.add( (ShortcutInfo)info );
			Root3D.addOrMoveDB( newIcon.getItemInfo() , mInfo.id );
			// zhujieping
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				mFolderMIUI3D.addIcon( newIcon );
			}
			else
				mFolder.addIcon( newIcon );
		}
		// else
		// {
		// addFolderNode(newIcon);
		// }
	}
	
	public void updateIcon(
			Icon3D widgetIcon ,
			Icon3D newIcon )
	{
		if( widgetIcon == null || newIcon == null )
		{
			return;
		}
		int index = mInfo.contents.indexOf( widgetIcon.getItemInfo() );
		if( mInfo.opened == true && index != -1 )
		{
			// zhujieping add
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				mFolderMIUI3D.updateIcon( widgetIcon , newIcon );
			else
				mFolder.updateIcon( widgetIcon , newIcon );
			mInfo.contents.set( index , (ShortcutInfo)newIcon.getItemInfo() );
			newIcon.getItemInfo().screen = widgetIcon.getItemInfo().screen;
			// newIcon.getItemInfo().container=
			// widgetIcon.getItemInfo().container;
			Root3D.addOrMoveDB( newIcon.getItemInfo() , mInfo.id );
			Root3D.deleteFromDB( widgetIcon.getItemInfo() );
		}
		else
		{
			if( this.children.contains( widgetIcon ) && index != -1 )
			{
				newIcon.x = widgetIcon.x;
				newIcon.y = widgetIcon.y;
				newIcon.rotation = widgetIcon.rotation;
				newIcon.getItemInfo().screen = widgetIcon.getItemInfo().screen;
				if( this.folderIconPath != null )
				{
					this.folderIconPath.changeIcon( widgetIcon , newIcon );
				}
				replaceView( widgetIcon , newIcon );
				mInfo.contents.set( index , (ShortcutInfo)newIcon.getItemInfo() );
				Root3D.addOrMoveDB( newIcon.getItemInfo() , mInfo.id );
				Root3D.deleteFromDB( widgetIcon.getItemInfo() );
			}
		}
		if( this.folderIconPath == null )
			folder_layout_without_anim();
	}
	
	public void addFolderNode(
			ArrayList<View3D> mCircleSomething ,
			boolean anim )
	{
		if( mCircleSomething == null )
		{
			return;
		}
		int Count = mCircleSomething.size();
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = mCircleSomething.get( i );
			if( myActor instanceof Icon3D )
			{
				ItemInfo info = ( (Icon3D)myActor ).getItemInfo();
				info.x = (int)folder_front.x;
				info.y = (int)( folder_front.y );
				info.screen = mInfo.contents.size();
				Root3D.addOrMoveDB( info , mInfo.id );
				mInfo.add( (ShortcutInfo)info );
				calcCoordinate( myActor );
				addViewBefore( folder_front , myActor );
			}
		}
		if( anim )
		{
			startAddFolderNodeAnim( mCircleSomething );
		}
		else
		{
			folder_layout_without_anim();
		}
	}
	
	public void addFolderNode(
			Icon3D view )
	{
		if( view == null )
		{
			return;
		}
		ItemInfo info = view.getItemInfo();
		switch( info.itemType )
		{
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW:
				if( info instanceof ApplicationInfo )
				{
					info = new ShortcutInfo( (ApplicationInfo)info );
				}
				info.x = (int)folder_front.x;
				info.y = (int)( folder_front.y );
				info.screen = mInfo.contents.size();
				Root3D.addOrMoveDB( info , mInfo.id );
				mInfo.add( (ShortcutInfo)info );
				addViewBefore( folder_front , view );
				view.x = view.x - this.x;
				view.y = view.y - this.y;
				// calcCoordinate(view);
				startAddFolderNodeAnim();
		}
	}
	
	private void addDropNode(
			Icon3D view )
	{
		if( view == null )
		{
			return;
		}
		ItemInfo info = view.getItemInfo();
		switch( info.itemType )
		{
			case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
			case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
			case LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW:
			case LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT:
				if( info instanceof ApplicationInfo )
				{
					info = new ShortcutInfo( (ApplicationInfo)info );
				}
				// xiatian add start //for mainmenu sort by user
				if( DefaultLayout.mainmenu_sort_by_user_fun == true )
				{
					if( ( info instanceof ShortcutInfo ) && ( ( (ShortcutInfo)info ).appInfo != null ) )
					{
						info.location_in_mainmenu = ( (ShortcutInfo)info ).appInfo.location_in_mainmenu;
					}
				}
				// xiatian add end
				addViewBefore( folder_front , view );
				// view.setPosition(folder_front.x+icon_pos_x,
				// folder_front.y+icon_pos_y);
				info.x = 0;
				info.y = (int)view.y;
				info.screen = mInfo.contents.size();
				Root3D.addOrMoveDB( info , mInfo.id );
				Icon3D iconView = (Icon3D)view;
				iconView.setInShowFolder( false );
				iconView.setItemInfo( info );
				mInfo.add( (ShortcutInfo)info );
		}
	}
	
	private void stopAnimation()
	{
		if( animation_line != null && !animation_line.isFinished() )
		{
			animation_line.free();
			animation_line = null;
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				if( bOnDropIcon )
				{
					bOnDropIcon = false;
				}
			}
		}
	}
	
	public void onInitTitle(
			String title )
	{
		boolean is_num = false;
		for( int i = 1 ; i <= 5 ; i++ )
		{
			if( title.equals( "" + i ) )
			{
				is_num = true;
				break;
			}
		}
		if( is_num )
		{
			mFolderName = title + "x.z";
			this.setTag( mFolderName );
			R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
			setTexture();
		}
		else
		{
			mFolderName = title;
			this.setTag( mFolderName );
			if( R3D.findRegion( "app-default-icon" ) == R3D.findRegion( title ) )
			{
				R3D.pack( mFolderName , titleToPixmap() );
				R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
			}
			this.titleTexture = R3D.findRegion( title );
		}
	}
	
	public void setTexture()
	{
		this.titleTexture = R3D.findRegion( mFolderName );
	}
	
	private void setFolderIconPathFullScreen()
	{
		setPosition( 0 , 0 );
		setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		setOrigin( this.width / 2 , this.height / 2 );
		Log.v( "testdrag" , "setFolderIconPathFullScreen 111" + this );
		folder_front.setPosition( mInfo.x , mInfo.y );
		folder_front.setSize( R3D.workspace_cell_width , folder_front_height );
		if( folder_style == folder_rotate_style )
		{
			folder_back.setPosition( folder_front.x + frontBmpLeftPadding , folder_front.y + folder_front_height - R3D.folder_gap_height );
			folder_back.setSize( R3D.folder_back_width , R3D.folder_back_height );
		}
		folder_layout( false );
		folderIconPath.dragPath.x += mInfo.x;
		folderIconPath.dragPath.y += mInfo.y;
	}
	
	public void setFolderIconSize(
			float pos_x ,
			float pos_y ,
			float front_offset_x ,
			float front_offset_y )
	{
		if( mInfo.opened == false )
		{
			Log.v( "testdrag" , "setFolderIconSize 111" );
			this.setPosition( pos_x , pos_y );
			this.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
		}
		else
		{
			Log.v( "testdrag" , "setFolderIconSize 222" );
			this.setPosition( 0 , 0 );
			this.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		}
		this.setOrigin( this.width / 2 , this.height / 2 );
		// {
		// NinePatch ninePatch= new
		// NinePatch(R3D.findRegion("widget-folder-windows-bg"), 3,
		// 3, 3, 3);
		// this. setBackgroud(ninePatch);
		// }
		folder_front.setPosition( front_offset_x , front_offset_y );
		// changeFolderFrontRegion(true);
		if( folder_style == folder_rotate_style )
		{
			folder_back.setPosition( folder_front.x + frontBmpLeftPadding , folder_front.y + folder_front_height - R3D.folder_gap_height );
		}
	}
	
	public void changeFolderFrontRegion(
			boolean bCompress )
	{
		float scale = 0.63f;
		if( folder_style == folder_rotate_style )
		{
		}
		else
		{
			// scale= R3D.folder_front_width/R3D.workspace_cell_height;
			scale = getIconBmpHeight() / R3D.workspace_cell_height;
		}
		if( bDisplayFolderName == true && bCompress == false )
		{
			return;
		}
		if( bDisplayFolderName == false && bCompress == true )
		{
			return;
		}
		if( bCompress )
		{
			float V2 = folder_front.region.getV() + ( folder_front.region.getV2() - folder_front.region.getV() ) * scale;
			folder_front.region.setV2( V2 );
			if( folderCoverPlateTexture != null )
			{
				folderCoverPlateTexture.setV2( V2 );
			}
			if( folder_style == folder_rotate_style )
			{
				folder_front.setSize( R3D.workspace_cell_width , R3D.folder_front_height * scale );
			}
			else
			{
				folder_front.setSize( R3D.workspace_cell_width , getIconBmpHeight() );
			}
			// folder_front.y -= R3D.folder_front_height*(1-scale)/2;
			// folder_front.y=0;
			bDisplayFolderName = false;
			folder_front_height = ( R3D.folder_front_height * scale );
			// icon_pos_y=R3D.folder_icon_posy-R3D.folder_front_height*(1-scale);
		}
		else
		{
			float V2 = folder_front.region.getV() + ( folder_front.region.getV2() - folder_front.region.getV() ) / scale;
			folder_front.region.setV2( V2 );
			if( folderCoverPlateTexture != null )
			{
				folderCoverPlateTexture.setV2( V2 );
			}
			folder_front.setSize( R3D.workspace_cell_width , R3D.folder_front_height );
			// folder_front.y += R3D.folder_front_height*(1-scale)/2;
			// folder_front.y=0;
			folder_front_height = R3D.folder_front_height;
			bDisplayFolderName = true;
			// icon_pos_y=R3D.folder_icon_posy;
		}
		folder_front.setOrigin( folder_front.width / 2 , folder_front.height / 2 );
		// teapotXu add start: restore the icon size
		this.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
		// teapotXu add end
		if( folder_style == folder_rotate_style )
		{
			if( folder_back == null )
			{
				folder_back = new ImageView3D( "folder_behind" , R3D.findRegion( "widget-folder-bg2" ) );
				folder_back.setSize( R3D.folder_back_width , R3D.folder_back_height );
				addViewBefore( folder_front , folder_back );
			}
			folder_back.setPosition( folder_front.x + frontBmpLeftPadding , folder_front.y + folder_front_height - R3D.folder_gap_height );
		}
		folder_layout_without_anim();
		// folder_layout(true);
	}
	
	private static float getFrontLeftPadding()
	{
		int textureWidth = R3D.workspace_cell_width;
		float padding = 0;
		if( folder_front_bmp != null )
		{
			padding = ( textureWidth - folder_front_bmp.getWidth() ) / 2;
		}
		// if (R3D.icon_bg_num > 0) {
		// return 0;
		// } else {
		return padding;
		// }
	}
	
	private void buildElements()
	{
		mFolder = new Folder3D( "folder3D" );
		mFolder.setFolderIcon( this );
		// zhujieping add
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			mFolderMIUI3D = new FolderMIUI3D( "folderMIUI3D" );
			mFolderMIUI3D.setFolderIcon( this );
		}
		if( iconResource != null )
		{
			createIconResourceBmp();
			if( iconBitmap != null && iconRegion == null )
			{
				Bitmap frontPixmap = MergeBmpToPixmap( iconBitmap , null , true );
				iconRegion = new TextureRegion( new BitmapTexture( frontPixmap , true ) );
				iconRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
		folder_front_ToPixmap3D();
		if( DefaultLayout.hotseat_hide_title )
		{
			createFrontWithoutShadowRegion();
		}
		// TextureRegion folderFrontRegion = new TextureRegion(new
		// Texture3D(frontPixmap));
		if( mInfo.folderid != null )
		{
			TextureRegion region = regionMap.get( mInfo.folderid );
			if( region == null )
			{
				region = folderFrontRegion;
			}
			if( region != null )
			{
				if( iconResource != null && iconRegion != null )
					folder_front = new ImageView3D( "folder_front" , iconRegion );
				else
					folder_front = new ImageView3D( "folder_front" , region );
				folder_front.setSize( R3D.workspace_cell_width , folder_front_height );
			}
		}
		else
		{
			if( folderFrontRegion != null )
			{
				if( iconResource != null && iconRegion != null )
					folder_front = new ImageView3D( "folder_front" , iconRegion );
				else
					folder_front = new ImageView3D( "folder_front" , folderFrontRegion );
				folder_front.setSize( R3D.workspace_cell_width , folder_front_height );
			}
		}
		if( folder_style == folder_iphone_style )
		{
			folder_front.hide();
			folder_back = null;
		}
		else
		{
			folder_back = new ImageView3D( "folder_behind" , R3D.findRegion( "widget-folder-bg2" ) );
			folder_back.setSize( R3D.folder_back_width , R3D.folder_back_height );
			addView( folder_back );
		}
		addView( folder_front );
		// zhujieping add
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			addView( mFolderMIUI3D );
		}
		else
			addView( mFolder );
		// zhujieping add
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			mFolderMIUI3D.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			mFolderMIUI3D.hide();
		}
		else
		{
			mFolder.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			mFolder.hide();
		}
		setFolderIconSize( mInfo.x , mInfo.y , 0 , 0 );
		folder_layout( true );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		// Log.e("folder", "fling");
		if( !Workspace3D.isRecentAppVisible() )
			this.color.a = 1f;
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		// Log.e("folder", "onLongClick");
		// Log.v("test123", " Foldericon3d onLongClick:" + name + " x:" + x +
		// " y:" + y);
		this.color.a = 1f;
		if( !this.isDragging && mInfo.opened == false )
		{
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			return viewParent.onCtrlEvent( this , MSG_FOLDERICON3D_LONGCLICK );
		}
		return super.onLongClick( x , y );
	}
	
	@Override
	public ItemInfo getItemInfo()
	{
		return this.mInfo;
	}
	
	public Bitmap titleToPixmap()
	{
		int color = Color.WHITE;
		if( this.mCurNumItems == mMaxNumItems && folder_iphone_style == folder_style )
		{
			color = Color.RED;
		}
		return titleToTexture( mFolderName , color );
	}
	
	public static Bitmap titleToTexture(
			String title ,
			int color )
	{
		int textureWidth = R3D.workspace_cell_width;
		// int textureHeight = R3D.workspace_cell_text_height;
		if( title.endsWith( "x.z" ) )
		{
			int length = title.length();
			if( length > 3 )
			{
				title = title.substring( 0 , length - 3 );
			}
		}
		return Utils3D.folderTitleToBitmap( title , Icon3D.titleBg , textureWidth );
		// return Utils3D.titleToBitmap(null, title, null, Icon3D.titleBg,
		// textureWidth, R3D.workspace_cell_height);
	}
	
	private boolean isFull()
	{
		return mInfo.contents.size() >= mMaxNumItems;
	}
	
	public void closeFolder()
	{
		mInfo.opened = false;
		// zhujieping
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			mFolderMIUI3D.setPosition( 0 , 0 );
			mFolderMIUI3D.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			mFolderMIUI3D.setOrigin( mFolderMIUI3D.width / 2f , mFolderMIUI3D.height / 2f );
			mFolderMIUI3D.hide();
		}
		else
		{
			mFolder.setPosition( 0 , 0 );
			mFolder.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			mFolder.hide();
		}
		Log.v( "testdrag" , "closeFolder 111" );
		// zhujieping add start
		boolean miuiV5folder = ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder;
		if( miuiV5folder && DefaultLayout.blur_enable )
		{
			blurFree();
			releaseFocus();
		}
		else
		{
			releaseFocus();
		}
		// zhujieping add end
		if( viewParent != null )
		{
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				if( from_Where == FROM_APPLIST && exit_to_where == TO_APPLIST )
				{
					viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_APPLIST );
				}
				else
				{
					viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_CELLLAYOUT );
				}
			}
			else
			{
				viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_CELLLAYOUT );
			}
			// teapotXu add end for Folder in Mainmenu
		}
		bAnimate = false;
		SendMsgToAndroid.sendShowClingPointMsg();
		if( folder_front != null && folder_style == folder_rotate_style )
			folder_front.color.a = 1f;
		if( miuiV5folder && DefaultLayout.blur_enable )
		{
			if( this.viewParent instanceof CellLayout3D )
			{
				for( int i = 0 ; i < viewParent.getChildCount() ; i++ )
				{
					View3D child = viewParent.getChildAt( i );
					if( child instanceof Widget3D )
					{
						( (Widget3D)child ).folderOpened = false;
					}
				}
			}
		}
		is_folder_open = false;
	}
	
	public void FolderIconNormalScreen()
	{
		Log.v( "testdrag" , "FolderIconNormalScreen 111" );
		this.setPosition( mInfo.x , mInfo.y );
		this.setSize( R3D.folder_front_width , R3D.workspace_cell_height );
		this.setOrigin( this.width / 2 , this.height / 2 );
		folder_front.setPosition( 0 , 0 );
		if( folder_style == folder_rotate_style )
		{
			folder_back.setPosition( folder_front.x + frontBmpLeftPadding , folder_front.y + folder_front_height - R3D.folder_gap_height );
		}
	}
	
	// zhujieping add start
	private void openV5Folder()
	{
		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder;
		float duration = 0.4f;
		TweenEquation tweenEquation = Expo.IN;
		stopTween();
		if( mFolderMIUI3D == null )
		{
			return;
		}
		mFolderMIUI3D.stopTween();
		mFolderMIUI3D.bEnableTouch = false;
		if( getApplistMode() == AppList3D.APPLIST_MODE_HIDE )
		{
			mFolderMIUI3D.buildV5Elements( duration , tweenEquation , 0 );
		}
		else
		{
			mFolderMIUI3D.buildV5Elements( duration , tweenEquation , hideIconList.size() );
		}
		bAnimate = true;
		mFolderMIUI3D.transform = true;
		m_bOpenFolderAnim = true;
		is_folder_open = true;
		this.stopAllTween();
		float mScale = 1.0f;
		this.startTween( View3DTweenAccessor.SCALE_XY , tweenEquation , duration , mFolderMIUI3D.getWidth() / folder_front.getWidth() , mFolderMIUI3D.getHeight() / folder_front.getHeight() , 0 );
		if( this.getX() < Utils3D.getScreenWidth() / 4 )
		{
			this.startTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , mFolderMIUI3D.targetPosX + mFolderMIUI3D.getWidth() * 2 / 3 , mFolderMIUI3D.targetPosY , 0 );
		}
		else if( this.getX() >= Utils3D.getScreenWidth() / 4 && this.getX() < Utils3D.getScreenWidth() / 2 )
		{
			this.startTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , mFolderMIUI3D.targetPosX + mFolderMIUI3D.getWidth() / 2 , mFolderMIUI3D.targetPosY , 0 );
		}
		else if( this.getX() >= Utils3D.getScreenWidth() / 2 )
		{
			this.startTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , mFolderMIUI3D.targetPosX + mFolderMIUI3D.getWidth() / 4 , mFolderMIUI3D.targetPosY , 0 );
		}
		this.setRotationX( 0 );
		this.startTween( View3DTweenAccessor.ROTATION , tweenEquation , duration , 180 , 0 , 0 );
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			mScale = 1.0f / Root3D.scaleFactor;
			mFolderMIUI3D.setOrigin( mFolderMIUI3D.getWidth() / 2 , mFolderMIUI3D.getHeight() / 2 );
		}
		SendMsgToAndroid.sendHideClingPointMsg();
		if( folder_front != null && folder_style == folder_rotate_style )
			folder_front.color.a = 0.2f;
	}
	
	// zhujieping add end
	public void openFolder()
	{
		stopTween();
		mInfo.opened = true;
		mFolder.setPosition( R3D.folder_group_left_margin , -Utils3D.getScreenHeight() );
		mFolder.setSize( Utils3D.getScreenWidth() - R3D.folder_group_left_margin - R3D.folder_group_right_margin , Utils3D.getScreenHeight() - R3D.workspace_cell_height );
		mFolder.show();
		mFolder.bEnableTouch = false;
		setFolderIconSize( 0 , 0 , ( Utils3D.getScreenWidth() - R3D.folder_front_width ) / 2 , R3D.folder_group_bottom_margin );
		folder_layout( false );
		mFolder.buildElements();
		Log.d( "testdrag" , "openFolder" );
		folderTween = mFolder.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.4f , R3D.folder_group_left_margin , R3D.workspace_cell_height , 0 ).setUserData( tween_anim_mfolder )
				.setCallback( this );
		SendMsgToAndroid.sendHideClingPointMsg();
	}
	
	@Override
	public void setItemInfo(
			ItemInfo info )
	{
		// TODO Auto-generated method stub
		this.mInfo = (UserFolderInfo)info;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		this.color.a = 1f;
		if( mInfo.opened == false )
		{
			if( initialFirstPointer.x > x && initialFirstPointer.x < x + width && initialFirstPointer.y > y && initialFirstPointer.y < y + height )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public int getFromWhere()
	{
		return from_Where;
	}
	
	// teapotXu add start for Folder in Mainmenu
	public void setFromWhere(
			int where )
	{
		from_Where = where;
	}
	
	public Folder3D getFolder()
	{
		return mFolder;
	}
	
	public boolean getOnDrop()
	{
		return bOnDrop;
	}
	
	public static void setApplistMode(
			int mode )
	{
		applistMode = mode;
	}
	
	public static int getApplistMode()
	{
		return applistMode;
	}
	
	public static void setApplistPreMode(
			int Premode )
	{
		applistPreMode = Premode;
	}
	
	public static int getApplistPreMode()
	{
		return applistPreMode;
	}
	
	public int getExitToWhere()
	{
		return exit_to_where;
	}
	
	public void setExitToWhere(
			int where )
	{
		exit_to_where = where;
	}
	
	private ArrayList<Icon3D> hideIconList = new ArrayList<Icon3D>();
	
	private boolean syncHideIconInFolder()
	{
		boolean is_need_re_calculate = false;
		ArrayList<ShortcutInfo> hideIconInfoList = new ArrayList<ShortcutInfo>();
		ArrayList<Icon3D> removedIconList = new ArrayList<Icon3D>();
		for( int index = 0 ; index < this.mInfo.contents.size() ; index++ )
		{
			ShortcutInfo child_sInfo = mInfo.contents.get( index );
			if( child_sInfo != null && child_sInfo.appInfo != null && child_sInfo.appInfo.isHideIcon == true )
			{
				hideIconInfoList.add( child_sInfo );
			}
		}
		int hideIconSize = hideIconList.size();
		for( int idx = 0 ; idx < hideIconSize ; idx++ )
		{
			Icon3D hideIcon = hideIconList.get( idx );
			ShortcutInfo hideIcon_sInfo = (ShortcutInfo)hideIcon.getItemInfo();
			if( hideIcon_sInfo != null && !hideIconInfoList.contains( hideIcon_sInfo ) )
			{
				removedIconList.add( hideIcon );
				continue;
			}
			if( FolderIcon3D.applistMode == AppList3D.APPLIST_MODE_HIDE )
			{
				hideIcon.showHide();
				hideIcon.reset_shake_status();
				if( !this.children.contains( hideIcon ) )
				{
					addView( hideIcon );
				}
				is_need_re_calculate = true;
			}
			// xiatian add start //for mainmenu sort by user
			else if( DefaultLayout.mainmenu_folder_function && FolderIcon3D.applistMode == AppList3D.APPLIST_MODE_UNINSTALL )
			{
				if( this.children.contains( hideIcon ) )
				{
					this.children.remove( hideIcon );
				}
				is_need_re_calculate = true;
			}
			// xiatian add end
		}
		if( removedIconList.size() > 0 )
		{
			hideIconList.removeAll( removedIconList );
		}
		return is_need_re_calculate;
	}
	
	// recalculate the position of each child because someone is hiden
	private void reCalcIconsPositionInFolder()
	{
		int actual_item_index = 0;
		ArrayList<View3D> clone_children;
		boolean is_need_re_calculate = false;
		if( mInfo.opened == true || from_Where != FROM_APPLIST )
		{
			return;
		}
		is_need_re_calculate = syncHideIconInFolder();
		clone_children = new ArrayList<View3D>( this.children );
		for( int i = 0 ; i < clone_children.size() ; i++ )
		{
			View3D child = clone_children.get( i );
			if( child instanceof Icon3D )
			{
				if( FolderIcon3D.applistMode == AppList3D.APPLIST_MODE_NORMAL || ( FolderIcon3D.applistMode == AppList3D.APPLIST_MODE_UNINSTALL && FolderIcon3D.applistPreMode != AppList3D.APPLIST_MODE_HIDE && ( DefaultLayout.mainmenu_sort_by_user_fun == false )// xiatian
																																																																		// add
																																																																		// //for
																																																																		// mainmenu
																																																																		// sort
																																																																		// by
																																																																		// user
				) )
				{
					if( ( (Icon3D)child ).getItemInfo() instanceof ShortcutInfo )
					{
						ApplicationInfo appInfo = ( (ShortcutInfo)( (Icon3D)child ).getItemInfo() ).appInfo;
						if( appInfo != null && appInfo.isHideIcon == true )
						{
							// do nothing, so this icon donot need update
							// position
							is_need_re_calculate = true;
							// if(((Icon3D) child).getHideStatus() == false)
							{
								// it is not in hide mode, and this icon should
								// been hided
								if( children.contains( child ) )
								{
									if( !hideIconList.contains( child ) )
									{
										hideIconList.add( (Icon3D)child );
									}
									children.remove( child );
								}
								continue;
							}
						}
					}
				}
			}
			else
			{
				continue;
			}
			actual_item_index++;
			if( is_need_re_calculate )
			{
				if( folder_style == folder_iphone_style )
				{
					getPos( actual_item_index - 1 );
					child.setPosition( mPosx , mPosy );
					if( child.getVisible() == false && ( actual_item_index - 1 ) < R3D.folder_transform_num )
					{
						child.show();
					}
				}
				else
				{
					getPos( 0 );
					child.setPosition( mPosx , mPosy );
					if( child.getVisible() == false && ( actual_item_index - 1 ) < R3D.folder_transform_num )
					{
						child.show();
					}
				}
			}
		}
	}
	
	// teapotXu add end for Fodler in Mainmenu
	public void closeFolderIconPath()
	{
		if( folderIconPath != null )
		{
			removeView( folderIconPath );
			folderIconPath = null;
			mFolder.bCloseFolderByDrag = false;
			if( folder_style == FolderIcon3D.folder_rotate_style )
			{
				folder_layout( true );
			}
			else
			{
				folder_layout_scale_without_anim();
			}
			releaseFocus();
			viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_CELLLAYOUT );
			bAnimate = false;
			SendMsgToAndroid.sendShowClingPointMsg();
		}
	}
	
	private boolean canDragIcon(
			float deltaX ,
			float deltaY )
	{
		if( bOnDropIcon || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
		{
			return false;
		}
		if( mCurNumItems > 0 && ( ( Math.abs( deltaX ) == 0 && Math.abs( deltaY ) > 0 ) || ( Math.abs( deltaY ) / Math.abs( deltaX ) > 1 ) ) && folderIconPath == null && this.viewParent instanceof CellLayout3D )
		{
			from_Where = FROM_CELLLAYOUT;
			return true;
		}
		else
		{
			if( mCurNumItems > 0 && ( ( Math.abs( deltaX ) == 0 && Math.abs( deltaY ) > 0 ) || ( Math.abs( deltaY ) / Math.abs( deltaX ) > 1 ) ) && folderIconPath == null && this.viewParent instanceof HotGridView3D )
			{
				from_Where = FROM_HOTSEAT;
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
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
		if( mInfo.opened == false )
		{
			// bScroll=true;
			if( x > 0 && x < width && y > 0 && y < height )
			{
				if( !DefaultLayout.folder_no_dragon && canDragIcon( deltaX , deltaY ) )
				{
					folderIconPath = new FolderIconPath( "folderIconPath" );
					folderIconPath.dragPath.x = x;
					folderIconPath.dragPath.y = y;
					setFolderIconPathFullScreen();
					folderIconPath.setFolderIcon( this );
					addView( folderIconPath );
					// teapotXu add start for Folder in Mainmenu
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						if( from_Where == FROM_APPLIST )
						{
							exit_to_where = TO_APPLIST;
							viewParent.onCtrlEvent( this , MSG_FOLDERICON_FROME_APPLIST );
						}
						else
							viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_ROOT3D );
					}
					else
						viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_ROOT3D );
					// viewParent.onCtrlEvent(this, MSG_FOLDERICON_TO_ROOT3D);
					// teapotXu add end for Folder in Mainmenu
					requestFocus();
					SendMsgToAndroid.sendHideClingPointMsg();
					return true;
				}
				else
				{
					return super.scroll( x , y , deltaX , deltaY );
				}
			}
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	public void setLongClick(
			boolean bFalse )
	{
		// blongClick =bFalse;
	}
	
	public void setOnDropFalse()
	{
		if( bOnDrop )
		{
			bOnDrop = false;
		}
	}
	
	public boolean onMyClick(
			float x ,
			float y )
	{
		if( bOnDropIcon )
		{
			return true;
		}
		if( bAnimate )
		{
			return true;
		}
		// if (mCurNumItems == 0) {
		// SendMsgToAndroid.sysPlaySoundEffect();
		// this.bRenameFolder = true;
		// SendMsgToAndroid.sendRenameFolderMsg(this);
		// return true;
		// }
		if( folderIconPath != null )
		{
			return super.onClick( x , y );
		}
		if( mInfo.opened )
		{
			View3D hitView = hit( x , y );
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				if( this.viewParent instanceof CellLayout3D )
				{
					for( int i = 0 ; i < viewParent.getChildCount() ; i++ )
					{
						View3D child = viewParent.getChildAt( i );
						if( child instanceof Widget3D )
						{
							( (Widget3D)child ).folderOpened = true;
						}
					}
				}
				if( hitView != null )
				{
					if( hitView.name == folder_front.name )
					{
						mFolderMIUI3D.DealButtonOKDown();
						return true;
					}
					else if( folder_style == folder_rotate_style && hitView.name == folder_back.name )
					{
						mFolderMIUI3D.DealButtonOKDown();
						return true;
					}
					else if( hitView.name == this.name )
					{
						mFolderMIUI3D.DealButtonOKDown();
						return true;
					}
					else
					{
						return super.onClick( x , y );
					}
				}
				else
				{
					mFolderMIUI3D.DealButtonOKDown();
					return true;
				}
			}
			else
			{
				if( hitView.name == folder_front.name )
				{
					mFolder.DealButtonOKDown();
					return true;
				}
				else if( folder_style == folder_rotate_style && hitView.name == folder_back.name )
				{
					mFolder.DealButtonOKDown();
					return true;
				}
				else if( hitView.name == this.name )
				{
					mFolder.DealButtonOKDown();
					return true;
				}
				else
				{
					return super.onClick( x , y );
				}
			}
		}
		else
		{
			// if (x > 0 && x < width && y > 0 && y < height && bTouchDown) {
			if( x > 0 && x < width && y > 0 && y < height )
			{
				if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				{
					if( mInfo.opened == false )
					{
						if( this.viewParent instanceof CellLayout3D )
						{
							for( int i = 0 ; i < viewParent.getChildCount() ; i++ )
							{
								View3D child = viewParent.getChildAt( i );
								if( child instanceof Widget3D )
								{
									( (Widget3D)child ).folderOpened = true;
								}
							}
							viewParent.onCtrlEvent( this , MSG_BRING_TO_FRONT );
							from_Where = FROM_CELLLAYOUT;
							// teapotXu add start for Folder in Mainmenu
						}
						else if( DefaultLayout.mainmenu_folder_function == true && this.viewParent instanceof GridView3D )
						{
							// 从主菜单中进入文件夹
							from_Where = FROM_APPLIST;
							exit_to_where = TO_APPLIST;
							boolean bPermitAnim = viewParent.onCtrlEvent( this , MSG_FOLDERICON_FROME_APPLIST );
							this.setTag( "miui_v5_folder" );
							// Log.e("whywhy", " onMyClick bPermitAnim=" +
							// bPermitAnim);
							if( bPermitAnim )
							{
								SendMsgToAndroid.sysPlaySoundEffect();
								mInfo.opened = true;
								requestFocus();
								clearState();
								openV5Folder();
							}
							return true;
							// teapotXu add end for Folder in Mainmenu
						}
						else
						{
							bringToFront();
							from_Where = FROM_HOTSEAT;
						}
						this.setTag( "miui_v5_folder" );
						boolean bPermitAnim = viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_ROOT3D );
						Log.e( "whywhy" , " onMyClick bPermitAnim=" + bPermitAnim );
						if( bPermitAnim )
						{
							SendMsgToAndroid.sysPlaySoundEffect();
							mInfo.opened = true;
							requestFocus();
							SendMsgToAndroid.sendHideWorkspaceMsg();
							openV5Folder();
						}
					}
				}
				else
				{
					SendMsgToAndroid.sysPlaySoundEffect();
					if( this.viewParent instanceof CellLayout3D )
					{
						bAnimate = true;
						from_Where = FROM_CELLLAYOUT;
						viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_ROOT3D );
						folderTween = this
								.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.2f , ( Utils3D.getScreenWidth() - R3D.folder_front_width ) / 2 , R3D.folder_group_bottom_margin , 0 )
								.setUserData( tween_anim_open ).setCallback( this );
						SendMsgToAndroid.sendHideWorkspaceMsg();
						// teapotXu add start for Folder in Mainmenu
					}
					else if( DefaultLayout.mainmenu_folder_function == true && this.viewParent instanceof GridView3D )
					{
						// 从主菜单中进入文件夹
						bAnimate = true;
						from_Where = FROM_APPLIST;
						exit_to_where = TO_APPLIST;
						viewParent.onCtrlEvent( this , MSG_FOLDERICON_FROME_APPLIST );
						folderTween = this
								.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.2f , ( Utils3D.getScreenWidth() - R3D.folder_front_width ) / 2 , R3D.folder_group_bottom_margin , 0 )
								.setUserData( tween_anim_open ).setCallback( this );
						return true;
						// teapotXu add end for Folder in Mainmenu
					}
					else
					{
						if( this.viewParent instanceof HotGridView3D )
						{
							bAnimate = true;
							from_Where = FROM_HOTSEAT;
							viewParent.onCtrlEvent( this , MSG_FOLDERICON_TO_ROOT3D );
							folderTween = this
									.startTween( View3DTweenAccessor.POS_XY , Linear.INOUT , 0.2f , ( Utils3D.getScreenWidth() - R3D.folder_front_width ) / 2 , R3D.folder_group_bottom_margin , 0 )
									.setUserData( tween_anim_open ).setCallback( this );
							SendMsgToAndroid.sendHideWorkspaceMsg();
						}
					}
				}
			}
			return true;
		}
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// zhujieping add begin
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
		{
			if( !mInfo.opened && folderIconPath == null /* && mCurNumItems > 0 */)// 长龙后，关闭长龙，不要响应
			{
				if( DefaultLayout.blur_bg_enable && wallpaperTextureRegion != null && wallpaperTextureRegion.getTexture() == null )
				{
					return true;
				}
				calcWallpaperOffset();
				blurInitial();
			}
		}
		// zhujieping add end
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return true;
		}
		return onMyClick( x , y );
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		if( folderIconPath != null )
		{
			return true;
		}
		if( bAnimate )
		{
			return true;
		}
		if( mInfo.opened )
		{
			return true;
		}
		if( mCurNumItems == 0 )
		{
			// SendMsgToAndroid.sendRenameFolderMsg(this);
			return true;
		}
		else
		{
			// Log.v("test123","onDoubleClick viewParent="+this.viewParent.name);
			return true;
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( !mInfo.opened )
		{
			this.color.a = 1f;
		}
		// xiatian add start //for mainmenu sort by user
		if( ( DefaultLayout.mainmenu_folder_function ) && ( applistMode == AppList3D.APPLIST_MODE_UNINSTALL ) )
		{
			if( isPointInBreakUpFolderIconRect( x , y ) )
			{
				if( mInfo.folderid == null )
				{
					viewParent.onCtrlEvent( this , MSG_FOLDERICON_BREAK_UP_SELF_IN_APPLIST );
				}
				return true;
			}
		}
		// xiatian add end
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( !mInfo.opened && folderIconPath == null )
				{
					ParticleManager.disableClickIcon = true;
				}
				else
				{
					ParticleManager.disableClickIcon = false;
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		boolean bRet = false;
		if( x > 0 && x < width && y > 0 && y < height && pointer == 0 )
		{
			downPoint.set( x , y );
		}
		//
		if( DefaultLayout.enable_new_particle && mInfo.opened == true )
		{
			Desktop3DListener.root.particleStart( this.x , this.y , x , y );
		}
		if( folderIconPath != null && bAnimate == false )
		{
			bRet = super.onTouchDown( x , y , pointer );
			if( bRet == true )
			{
			}
			else
			{
				folderIconPath.DealButtonOKDown();
			}
			return true;
		}
		if( !mInfo.opened && bAnimate == false )
		{
			this.color.a = 0.6f;
			// teapotXu add start
			this.addTouchedView();
			// teapotXu add end
		}
		else
		{
			if( !bAnimate )
			{
				super.onTouchDown( x , y , pointer );
			}
		}
		return true;
	}
	
	@Override
	public boolean handleActionWhenTouchLeave()
	{
		// do something when touch area leave this icon
		this.color.a = 1.0f;
		this.removeTouchedView();
		return true;
	}
	
	public boolean onHomeKey(
			boolean alreadyOnHome )
	{
		Log.d( "testdrag" , " FolderIcon3D onHomeKey bAnimate=" + bAnimate );
		if( bAnimate )
			return false;
		if( bRenameFolder )
		{
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && mInfo.opened )
			{
				bRenameFolder = false;
			}
			else
			{
				iLoongLauncher.getInstance().renameFoldercleanup();
			}
		}
		if( folderIconPath != null )
		{
			folderIconPath.DealButtonOKDown();
			return true;
		}
		if( mInfo.opened )
		{
			Log.d( "testdrag" , " FolderIcon3D DealButtonOKDown bAnimate=" + bAnimate );
			// zhujieping add
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				mFolderMIUI3D.DealButtonOKDown();
			}
			else
				mFolder.DealButtonOKDown();
			return true;
		}
		return false;
	}
	
	public void closeFolderStartAnim()
	{
		Log.v( "testdrag" , "closeFolderStartAnim 111" );
		mInfo.opened = false;
		this.setFolderIconSize( ( Utils3D.getScreenWidth() - R3D.folder_front_width ) / 2 , R3D.folder_group_bottom_margin , 0 , 0 );
		if( folder_style == FolderIcon3D.folder_rotate_style )
		{
			folder_layout_rotate_without_anim();
		}
		else
		{
			folder_layout_scale_without_anim();
		}
		folderTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , mInfo.x , mInfo.y , 0 ).setUserData( tween_anim_close ).setCallback( this );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == folderTween && type == TweenCallback.COMPLETE )
		{
			folderTween = null;
			int animKind = (Integer)( source.getUserData() );
			if( animKind == tween_anim_open )
			{
				if( mInfo.opened == false )
				{
					Log.d( "testdrag" , "on Event openFolder" );
					openFolder();
				}
				else
				{
					Log.d( "testdrag" , "on Event bAnimate false" );
					bAnimate = false;
				}
			}
			else if( animKind == tween_anim_mfolder )
			{
				// zhujieping add
				if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				{
					mFolderMIUI3D.showFolderIconUninstall();
					mFolderMIUI3D.setUpdateValue( true );
				}
				else
				{
					mFolder.addGridChild();
					mFolder.setUpdateValue( true );
				}
				bAnimate = false;
				// zhujieping add
				if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && DefaultLayout.blur_enable )
				{
					MiuiV5FolderBlurFinish();
				}
			}
			else if( animKind == tween_anim_close )
			{
				closeFolder();
			}
		}
		if( type == TweenCallback.COMPLETE && source == animation_line )
		{
			int animKind = (Integer)( source.getUserData() );
			stopAnimation();
			// animation_line = null;
			if( animKind == animation_line_trans )
			{
				bAnimate = false;
				folder_layout( true );
				bOnDropIcon = false;
				Log.v( "test12345" , "bAnimate is false" );
			}
			else if( animation_line_ondrop == animKind )
			{
				// bringToFront
				int index = 0;
				float posx = 0;
				float posy = 0;
				if( folder_style == folder_rotate_style )
				{
					index = folder_front.getIndexInParent() + 1;
					// posx= folder_front.x;
					// posy=folder_front.y+icon_pos_y;
					getPos( 0 );
					posx = mPosx;
					posy = mPosy + R3D.workspace_cell_width / 4;
				}
				else
				{
					index = mCurNumItems + 2;/* Folde3Dr and folder Front */
					posx = -R3D.folder_icon_rotation_offsetx;
					posy = R3D.folder_icon_rotation_offsety + R3D.workspace_cell_width * scaleFactor + R3D.folder_icon_rotation_offsety / 4;
				}
				int Count = getChildCount();
				View3D myActor;
				animation_line = Timeline.createParallel();
				for( int i = index ; i < Count ; i++ )
				{
					myActor = this.getChildAt( i );
					myActor.stopTween();
					if( folder_style == folder_rotate_style )
					{
						// addDropNode((Icon3D) myActor);
					}
					animation_line.push( Tween.to( myActor , View3DTweenAccessor.POS_XY , 0.3f ).target( posx , posy , 0 ).ease( Linear.INOUT ) );
				}
				bAnimate = true;
				Log.v( "test12345" , "bAnimate is true animation_line_ondrop" );
				animation_line.start( View3DTweenAccessor.manager ).setUserData( animation_line_trans ).setCallback( this );
			}
			SendMsgToAndroid.sendShowWorkspaceMsg();
		}
		// xiatian add start //for mainmenu sort by user
		if( DefaultLayout.mainmenu_folder_function == true && source == shakeAnimation && type == TweenCallback.COMPLETE )
		{
			// xiatian start //for mainmenu sort by user
			// xiatian del start
			// float r;
			// if (this.rotation == SHAKE_ROTATE_ANGLE)
			// {
			// r = 0f;
			// }
			// else
			// {
			// r = SHAKE_ROTATE_ANGLE;
			// }
			// shakeAnimation =
			// this.startTween(View3DTweenAccessor.ROTATION,Linear.INOUT,
			// shakeTime, r, 0, 0).setCallback(this);
			// xiatian del end
			// xiatian add start
			if( true )
			{
				float scale;
				int data = -1;
				int animKind = (Integer)( source.getUserData() );
				if( animKind == 0 )
				{
					scale = SHAKE_SCALE_MAX;
					data = 1;
				}
				else
				{
					scale = SHAKE_SCALE_MIN;
					data = 0;
				}
				this.setRotation( 0 );
				float mShakeTime = com.badlogic.gdx.math.MathUtils.random( FIRST_SHAKE_TIME_MIN , FIRST_SHAKE_TIME_MAX );
				shakeAnimation = this.startTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , mShakeTime , scale , scale , 0 ).setUserData( data ).setCallback( this );
			}
			else
			{
				float r;
				//
				if( this.rotation == SHAKE_ROTATE_ANGLE )
				{
					r = 0f;
				}
				else
				{
					r = SHAKE_ROTATE_ANGLE;
				}
				shakeAnimation = this.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , shakeTime , r , 0 , 0 ).setCallback( this );
			}
			// xiatian add end
			// xiatian end
		}
		// xiatian add end
	}
	
	private void RemoveViewByItemInfo(
			ItemInfo item )
	{
		View3D myActor;
		boolean bFind = false;
		// zhujieping
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			if( mInfo.opened && mFolderMIUI3D.isVisible() )
			{
				mFolderMIUI3D.RemoveViewByItemInfo( item );
			}
			else
			{
				int Count = getChildCount() - 1;
				for( int i = Count ; i >= 0 ; i-- )
				{
					myActor = getChildAt( i );
					if( myActor instanceof Icon3D )
					{
						ItemInfo info = ( (Icon3D)myActor ).getItemInfo();
						if( item.equals( info ) && ( mInfo.opened == false ) )
						{
							removeView( myActor );
							myActor = null;
							bFind = true;
							break;
						}
					}
				}
				if( bFind && folderIconPath == null )
				{
					folder_layout( true );
				}
			}
		}
		else
		{
			if( mInfo.opened == true && mFolder.isVisible() == true )
			{
				mFolder.RemoveViewByItemInfo( item );
			}
			else
			{
				int Count = getChildCount() - 1;
				for( int i = Count ; i >= 0 ; i-- )
				{
					myActor = getChildAt( i );
					if( myActor instanceof Icon3D )
					{
						ItemInfo info = ( (Icon3D)myActor ).getItemInfo();
						if( item.equals( info ) && ( mInfo.opened == false ) )
						{
							removeView( myActor );
							myActor = null;
							bFind = true;
							break;
						}
					}
				}
				if( bFind && folderIconPath == null )
				{
					folder_layout( true );
				}
			}
		}
	}
	
	public void setCurNumItems(
			int nums )
	{
		mCurNumItems = nums;
	}
	
	@Override
	public void onAdd(
			ItemInfo item )
	{
		// TODO Auto-generated method stub
		if( mCurNumItems < mMaxNumItems )
		{
			mCurNumItems++;
		}
	}
	
	@Override
	public void onRemove(
			ItemInfo item )
	{
		// TODO Auto-generated method stub
		if( mCurNumItems > 0 )
		{
			mCurNumItems--;
		}
		RemoveViewByItemInfo( item );
	}
	
	@Override
	public void onTitleChanged(
			CharSequence title )
	{
		// TODO Auto-generated method stub
		if( title != null )
		{
			mFolderName = title.toString();
			this.setTag( mFolderName );
			if( viewParent != null )
			{
				viewParent.onCtrlEvent( this , MSG_UPDATE_VIEW );
			}
		}
	}
	
	private void startSequenceAnim(
			View3D cur_view ,
			float startx ,
			float starty )
	{
		float pos_y = folder_front_height + R3D.workspace_cell_width / 2;
		Timeline cur_view_line = Timeline.createSequence();
		if( folder_style == folder_iphone_style )
		{
			pos_y = pos_y - 3 * R3D.workspace_cell_width / 4;
			addDropNode( (Icon3D)cur_view );
			getPos( mCurNumItems - 1 );
			cur_view.setPosition( mPosx , mPosy );
			cur_view.stopAllTween();
			cur_view_line.push( Tween.to( cur_view , View3DTweenAccessor.SCALE_XY , 0.2f ).target( getScaleFactor( mCurNumItems - 1 ) , getScaleFactor( mCurNumItems - 1 ) ).ease( Linear.INOUT ) );
		}
		else
		{
			// addView(cur_view);
			addDropNode( (Icon3D)cur_view );
			getPos( 0 );
			cur_view.stopAllTween();
			// cur_view.setPosition(folder_front.x, folder_front.y+icon_pos_y);
			cur_view.setPosition( mPosx , mPosy );
			cur_view_line.push( Tween.to( cur_view , View3DTweenAccessor.POS_XY , 0.4f ).target( cur_view.x , pos_y ).ease( Linear.INOUT ) );
		}
		// cur_view_line.push(Tween.to(cur_view,View3DTweenAccessor.POS_XY
		// ,3f).target(cur_view.x,cur_view.y).ease(Linear.INOUT));
		animation_line.push( cur_view_line );
	}
	
	public void buildChildParallelAnim(
			Timeline childTween ,
			View3D myActor ,
			float duration ,
			float degree ,
			float targetX ,
			float targetY )
	{
		if( childTween == null || myActor == null )
		{
			return;
		}
		myActor.setRotationVector( 0 , 0 , 1 );
		childTween.push( Tween.to( myActor , View3DTweenAccessor.ROTATION , duration ).target( degree , 0 , 0 ).ease( Linear.INOUT ) );
		childTween.push( Tween.to( myActor , View3DTweenAccessor.SCALE_XY , duration ).target( getScaleFactor( 0 ) , getScaleFactor( 0 ) ).ease( Linear.INOUT ) );
		childTween.push( Tween.to( myActor , View3DTweenAccessor.POS_XY , duration ).target( targetX , targetY , 0 ).ease( Linear.INOUT ) );
	}
	
	private void folder_layout_poweron_rotate()
	{
		View3D myActor;
		int iconIndex = 0;
		int Count = getChildCount() - 1;
		Log.d( "testtetstest" , "Folder Count:" + mCurNumItems );
		for( int i = Count ; i >= 0 ; i-- )
		{
			myActor = getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				myActor.stopAllTween();
				getPos( iconIndex );
				buildChildTrans( myActor , rotateDegree , mPosx , mPosy );
				if( iconIndex > R3D.folder_transform_num - 1 || isHideItemIcon() )
				{
					myActor.hide();
				}
				changeTextureRegion( myActor , getIconBmpHeight() );
				iconIndex++;
			}
		}
		Log.d( "testtetstest" , "iconIndex=" + iconIndex );
	}
	
	private void folder_layout_scale_without_anim()
	{
		int iconIndex = 0;
		int Count = getChildCount();
		View3D myActor;
		stopAnimation();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				myActor.stopAllTween();
				changeTextureRegion( myActor , getIconBmpHeight() );
				myActor.setRotation( 0 );
				myActor.setScale( getScaleFactor( iconIndex ) , getScaleFactor( iconIndex ) );
				getPos( iconIndex );
				myActor.setPosition( mPosx , mPosy );
				if( iconIndex >= R3D.folder_transform_num || isHideItemIcon() )
				{
					myActor.hide();
				}
				iconIndex++;
			}
		}
	}
	
	public boolean isHideItemIcon()
	{
		boolean result = mInfo.folderid != null && bitmapMap.get( mInfo.folderid ) != null;
		return result;
	}
	
	private void folder_layout_poweron()
	{
		if( folder_style == folder_rotate_style )
		{
			folder_layout_poweron_rotate();
		}
		else
		{
			folder_layout_scale_without_anim();
		}
	}
	
	public float getFolderFrontHeight()
	{
		if( folder_front != null )
		{
			return folder_front.height;
		}
		else
		{
			return R3D.folder_front_height;
		}
	}
	
	private void buildChildTrans(
			View3D myActor ,
			float degree ,
			float targetX ,
			float targetY )
	{
		myActor.setRotation( degree );
		myActor.setScale( getScaleFactor( 0 ) , getScaleFactor( 0 ) );
		myActor.setPosition( targetX , targetY );
	}
	
	private void folder_layout_rotate_without_anim()
	{
		stopAnimation();
		folder_layout_poweron_rotate();
	}
	
	private void folder_layout_rotate(
			boolean bRotation )
	{
		Timeline viewTween = null;
		View3D myActor;
		stopAnimation();
		if( bRotation )
		{
			animation_line = Timeline.createParallel();
		}
		int iconIndex = 0;
		int Count = getChildCount() - 1;
		for( int i = Count ; i >= 0 ; i-- )
		{
			myActor = getChildAt( i );
			myActor.stopAllTween();
			viewTween = null;
			if( bRotation )
			{
				viewTween = Timeline.createParallel();
			}
			if( myActor instanceof Icon3D )
			{
				if( bRotation )
				{
					changeTextureRegion( myActor , getIconBmpHeight() );
					getPos( iconIndex );
					buildChildParallelAnim( viewTween , myActor , 0.3f , rotateDegree , mPosx , mPosy );
					if( iconIndex > R3D.folder_transform_num - 1 || isHideItemIcon() )
					{
						myActor.hide();
					}
					iconIndex++;
					animation_line.push( viewTween );
				}
				else
				{
					// myActor.setRotation(0);
					// myActor.setOrigin(0, 0);
					myActor.setPosition( folder_front.x + myActor.x , (float)( folder_front.y + myActor.y ) );
				}
			}
		}
		if( bRotation )
		{
			animation_line.start( View3DTweenAccessor.manager ).setUserData( animation_line_rotation ).setCallback( this );
		}
	}
	
	private void getAndroid4Pos(
			int index )
	{
		rotateDegree = 0;
		mPosx = (int)( folder_front.x + R3D.getInteger( "folder_front_margin_offset" ) );
		// mPosy=(int)
		// (folder_front.height-getIconBmpHeight())-R3D.getInteger("folder_front_margin_offset");
		mPosy = (int)( folder_front.height - getIconBmpHeight() + R3D.getInteger( "folder_front_margin_offset" ) );
		if( index == 1 )
		{
			mPosx += R3D.folder_icon_rotation_offsetx;
			mPosy += R3D.folder_icon_rotation_offsety;
		}
		else if( index >= 2 )
		{
			mPosx += folder_front_bmp.getWidth() / 3;
			mPosy += folder_front_bmp.getHeight() / 3;
		}
	}
	
	void getPos(
			int index )
	{
		if( folder_style == folder_rotate_style )
		{
			rotateDegree = 0;
			mPosx = (int)folder_front.x;
			mPosy = (int)( folder_front.y + folder_front.height - getIconBmpHeight() * getScaleFactor( 0 ) );
			if( index == 1 )
			{
				rotateDegree = -R3D.folder_icon_rotation_degree;
				mPosx += R3D.folder_icon_rotation_offsetx;
				mPosy += R3D.folder_icon_rotation_offsety;
			}
			else if( index == 2 )
			{
				rotateDegree = R3D.folder_icon_rotation_degree;
				mPosx -= R3D.folder_icon_rotation_offsetx;
				mPosy += R3D.folder_icon_rotation_offsety;
			}
		}
		else
		{
			int mCountY = icon_row_num;
			int mCountX = R3D.folder_transform_num / mCountY;
			if( R3D.getInteger( "folder_style" ) == 1 && R3D.folder_transform_num == 3 )
			{
				getAndroid4Pos( index );
				return;
			}
			float cellWidth = ( folder_front_bmp.getWidth() ) / (float)mCountX;
			float cellHeight = ( folder_front_bmp.getHeight() - folder_front_padding_top - folder_front_padding_bottom ) / mCountY;
			rotateDegree = 0;
			if( index >= R3D.folder_transform_num )
			{
				index = R3D.folder_transform_num - 1;
			}
			int titleHeight = titleTexture.getRegionHeight();
			// 外边�?workpspacecell 到folder_front_bmp的边�?
			float padding_out = ( R3D.workspace_cell_width - folder_front_bmp.getWidth() ) / 2f;
			if( R3D.folder_transform_num == 4 )
			{
				// icon到folder的内边距
				float padding_inner = ( cellWidth - R3D.workspace_cell_width * scaleFactor ) / 2f;
				mPosx = ( padding_out + padding_inner + folder_front_padding_left / 2 + ( index % mCountX ) * ( cellWidth - folder_front_padding_left ) );
			}
			else if( R3D.folder_transform_num == 9 )
			{
				cellWidth = ( folder_front_bmp.getWidth() - folder_front_padding_left - folder_front_padding_right ) / mCountX;
				float mGapX = ( ( cellWidth - R3D.workspace_cell_width * scaleFactor ) / ( mCountX - 1 ) );
				mPosx = padding_out + ( index % mCountX ) * ( cellWidth + mGapX ) + folder_front_padding_left;
			}
			// ok版本
			// mPosx = (int) Math
			// .ceil(((R3D.workspace_cell_width / (float) mCountX -
			// R3D.workspace_cell_width
			// * scaleFactor) / 2f)
			// + (index % mCountX) * cellWidth);
			mPosy = ( titleHeight + folder_front_padding_bottom + folder_front_height - R3D.folder_front_height + DefaultLayout.app_icon_size / 10f + icon_row_num * cellHeight - cellHeight - ( index / mCountX ) * cellHeight );
			if( mInfo.opened == true )
			{
				// zhujieping add
				if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				{
					mPosx += mInfo.x;
					mPosy += mInfo.y;
				}
				else
				{
					mPosx += ( Utils3D.getScreenWidth() - R3D.folder_front_width ) / 2;
					mPosy += R3D.folder_group_bottom_margin;
				}
			}
			if( R3D.folder_transform_num == 6 )
			{
				mPosy += folder_front_padding_bottom;
			}
			// xiatian add start //folder transform icon offset
			mPosx += R3D.Folder_Transform_Icon_Offset_X;
			if( mInfo.opened == false )
				mPosy += R3D.Folder_Transform_Icon_Offset_Y;
			// xiatian add end
		}
	}
	
	private void folder_layout_scale(
			boolean bAnim )
	{
		int iconIndex = 0;
		int Count = getChildCount();
		View3D myActor;
		float duration = 0.3f;
		Timeline viewTween = null;
		stopAnimation();
		if( bAnim )
		{
			animation_line = Timeline.createParallel();
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				// teapotXu add start for Folder in Mainmenu && shake
				if( DefaultLayout.mainmenu_folder_function )
				{
					// because stopAllTween() does not have effect
					myActor.stopTween();
					// then start shake animation
					if( myActor instanceof Icon3D )
					{
						( (Icon3D)myActor ).reset_shake_status();
						if( ( (Icon3D)myActor ).getUninstallStatus() )
						{
							( (Icon3D)myActor ).shake( true );
						}
					}
				}
				else
				{
					myActor.stopAllTween();
				}
				// myActor.stopAllTween();
				// teapotXu add end for Folder in Mainmenu
				if( bAnim )
				{
					viewTween = null;
					viewTween = Timeline.createParallel();
					changeTextureRegion( myActor , getIconBmpHeight() );
					myActor.setScale( getScaleFactor( iconIndex ) , getScaleFactor( iconIndex ) );
					getPos( iconIndex );
					viewTween.push( Tween.to( myActor , View3DTweenAccessor.POS_XY , duration ).target( mPosx , mPosy , 0 ).ease( Linear.INOUT ) );
					if( iconIndex >= R3D.folder_transform_num || isHideItemIcon() )
					{
						myActor.hide();
					}
					animation_line.push( viewTween );
					iconIndex++;
				}
				else
				{
					myActor.setPosition( folder_front.x + myActor.x , folder_front.y + myActor.y );
				}
			}
		}
		if( bAnim )
		{
			animation_line.start( View3DTweenAccessor.manager ).setUserData( animation_line_rotation ).setCallback( this );
		}
	}
	
	private void folder_layout_without_anim()
	{
		if( folder_style == folder_rotate_style )
		{
			folder_layout_rotate_without_anim();
		}
		else
		{
			folder_layout_scale_without_anim();
		}
	}
	
	public void folder_layout(
			boolean bRotation )
	{
		if( folder_style == folder_rotate_style )
		{
			folder_layout_rotate( bRotation );
		}
		else
		{
			folder_layout_scale( bRotation );
		}
	}
	
	public int getFolderIconNum()
	{
		return mCurNumItems;
	}
	
	@Override
	public void onItemsChanged()
	{
		// TODO Auto-generated method stub
		onLeafCountChanged( mCurNumItems );
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		if( list == null || list.size() == 0 )
		{
			return true;
		}
		for( View3D view : list )
		{
			Root3D.hotseatBar.getDockGroup().deleteFromHotseatItemsList( view );
		}
		Root3D.hotseatBar.getDockGroup().childPositonUpdate();
		ArrayList<View3D> need_removed_list = null;
		com.badlogic.gdx.graphics.Color selectcolor = new com.badlogic.gdx.graphics.Color( 1 , 1 , 1 , 1 );
		int Count = list.size();
		boolean bNeedDeal = true;
		if( list.get( 0 ) instanceof Icon3D )
		{
			if( DefaultLayout.enable_workspace_push_icon && mInfo.container == Favorites.CONTAINER_DESKTOP )
			{
				if( list.size() == 1 )
				{
					if( Workspace3D.getDragMode() == Workspace3D.DRAG_MODE_ADD_TO_FOLDER )
					{
						bNeedDeal = true;
					}
					else
					{
						bNeedDeal = false;
					}
				}
				else
				{
					bNeedDeal = true;
				}
			}
			else
			{
				bNeedDeal = true;
			}
		}
		else
		{
			bNeedDeal = false;
		}
		if( bNeedDeal == false )
		{
			bOnDrop = false;
			return false;
		}
		boolean bCanStartAnimation = false;
		bOnDrop = true;
		if( Count + mCurNumItems > mMaxNumItems )
		{
			int index = 0;
			need_removed_list = new ArrayList<View3D>( list );
			for( View3D view : list )
			{
				if( view instanceof Icon3D )
				{
					ItemInfo itemInfo = ( (Icon3D)view ).getItemInfo();
					itemInfo.cellX = -1;
					itemInfo.cellY = -1;
					bCanStartAnimation = true;
					view.setColor( selectcolor );
					( (Icon3D)view ).cancelSelected();
					// teapotXu add start:当添加的icons超过总数时，需要先将文件夹填满
					index++;
					if( mCurNumItems + index <= mMaxNumItems )
					{
						need_removed_list.remove( view );
					}
					// teapotXu add end
				}
			}
			if( bCanStartAnimation == true )
			{
				if( need_removed_list.size() != 0 )
				{
					this.setTag( need_removed_list );
				}
				else
				{
					this.setTag( list );
				}
				bOnDrop = false;
				viewParent.onCtrlEvent( this , MSG_FOLDERICON_BACKTO_ORIG );
				SendMsgToAndroid.sendOurToastMsg( R3D.folder3D_full );
			}
			if( need_removed_list.size() == list.size() )
				return true;
		}
		stopAnimation();
		animation_line = Timeline.createParallel();
		for( View3D view : list )
		{
			// teapotXu add start:
			if( need_removed_list != null && need_removed_list.contains( view ) )
			{
				continue;
			}
			// teapotXu add end
			if( view instanceof Icon3D )
			{
				view.setColor( selectcolor );
				( (Icon3D)view ).cancelSelected();
				if( view.getHeight() == getIconBmpHeight() )
				{
					changeOrigin( view );
				}
				changeTextureRegion( view , getIconBmpHeight() );
				startSequenceAnim( view , x , y );
				bCanStartAnimation = true;
			}
		}
		if( bCanStartAnimation )
		{
			bOnDropIcon = true;
			animation_line.start( View3DTweenAccessor.manager ).setUserData( animation_line_ondrop ).setCallback( this );
			return true;
		}
		else
		{
			stopAnimation();
			return false;
		}
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		for( View3D view : list )
		{
			if( view instanceof Icon3D )
			{
				if( DefaultLayout.enable_workspace_push_icon && mInfo.container == Favorites.CONTAINER_DESKTOP )
				{
					if( list.size() == 1 )
					{
						if( Workspace3D.getDragMode() == Workspace3D.DRAG_MODE_ADD_TO_FOLDER )
						{
							return true;
						}
						else
						{
							return false;
						}
					}
					else
					{
						return true;
					}
				}
				else
				{
					return true;
				}
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
	protected void drawChildren(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( folder_style == folder_rotate_style )
		{
			if( ishide )
			{
				for( int i = 0 ; i < getChildCount() ; i++ )
				{
					View3D actor = getChildAt( i );
					if( actor != folder_back && actor != folder_front )
					{
						actor.draw( batch , parentAlpha );
					}
				}
			}
			else
			{
				super.drawChildren( batch , parentAlpha );
			}
			return;
		}
		parentAlpha *= color.a;
		if( transform )
		{
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				reCalcIconsPositionInFolder();
			}
			// teapotXu add end for Folder in Mainmenu
			for( int i = 0 ; i >= 0 ; i-- )
			{
				View3D child = children.get( i );
				if( !child.visible )
					continue;
				if( child instanceof ViewGroup3D )
				{
					if( child.background9 != null )
					{
						if( child.is3dRotation() )
							child.applyTransformChild( batch );
						batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
						child.background9.draw( batch , child.x , child.y , child.width , child.height );
						if( child.is3dRotation() )
							child.resetTransformChild( batch );
					}
					child.draw( batch , parentAlpha );
					continue;
				}
				if( child.is3dRotation() )
					child.applyTransformChild( batch );
				if( child.background9 != null )
				{
					batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
					child.background9.draw( batch , child.x , child.y , child.width , child.height );
				}
				child.draw( batch , parentAlpha );
				if( child.is3dRotation() )
					child.resetTransformChild( batch );
			}
			if( folder_front.region != null && folder_style == folder_iphone_style && !mFolderMIUI3D.isVisible() )
			{
				if( folder_front.is3dRotation() )
					folder_front.applyTransformChild( batch );
				if( !ishide )
					folder_front.draw( batch , parentAlpha );
				if( folderCoverPlateTexture != null )
				{
					batch.draw( folderCoverPlateTexture , folder_front.x , folder_front.y , folderCoverPlateTexture.getRegionWidth() , folderCoverPlateTexture.getRegionHeight() );
				}
				if( folder_front.is3dRotation() )
					folder_front.resetTransformChild( batch );
			}
			for( int i = children.size() - 1 ; i > 0 ; i-- )
			{
				View3D child = children.get( i );
				if( !child.visible )
					continue;
				if( iconResource != null && iconRegion != null && child instanceof Icon3D && !mInfo.opened )
				{
					continue;
				}
				if( child instanceof ViewGroup3D )
				{
					if( child.background9 != null )
					{
						child.applyTransformChild( batch );
						batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
						child.background9.draw( batch , child.x , child.y , child.width , child.height );
						child.resetTransformChild( batch );
					}
					child.draw( batch , parentAlpha );
					continue;
				}
				if( child.background9 != null )
				{
					child.applyTransformChild( batch );
					batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
					child.background9.draw( batch , child.x , child.y , child.width , child.height );
					child.resetTransformChild( batch );
				}
				if( child.is3dRotation() )
					child.applyTransformChild( batch );
				child.draw( batch , parentAlpha );
				if( child.is3dRotation() )
					child.resetTransformChild( batch );
			}
			// batch.flush();
		}
	}
	
	public boolean is_folder_open = false;
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( m_bOpenFolderAnim && this.getRotation() > 90 && this.getRotateDegree() < 180 )
		{
			m_bOpenFolderAnim = false;
			this.stopAllTween();
			this.setScale( 1 , 1 );
			this.setRotation( 0 );
			setFolderIconSize( 0 , 0 , mInfo.x , mInfo.y );
			mFolderMIUI3D.show();
			mFolderMIUI3D.addGridChild();
			float duration = 0.4f;
			TweenEquation tweenEquation = Expo.OUT;
			mFolderMIUI3D.setScale( Utils3D.getIconBmpHeight() / mFolderMIUI3D.getWidth() , Utils3D.getIconBmpHeight() / mFolderMIUI3D.getWidth() );
			folderTween = mFolderMIUI3D.startTween( View3DTweenAccessor.SCALE_XY , tweenEquation , duration , 1 , 1 , 0 ).setUserData( tween_anim_mfolder ).setCallback( this );
			mFolderMIUI3D.setRotationX( 270 );
			mFolderMIUI3D.startTween( View3DTweenAccessor.ROTATION , tweenEquation , duration , 360 , 0 , 0 );
		}
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect( batch );
		/************************ added by zhenNan.ye end *************************/
		if( transform )
		{
			applyTransform( batch );
			if( titleTexture != null && bDisplayFolderName == true && !ishide && !mFolderMIUI3D.isVisible() )
			{
				batch.draw( titleTexture , folder_front.x , folder_front.y , titleTexture.getRegionWidth() , titleTexture.getRegionHeight() );
			}
			drawChildren( batch , parentAlpha );
			if( ( DefaultLayout.mainmenu_folder_function ) && ( uninstall ) && mInfo.folderid == null )
			{
				drawDelTipIcon( batch , 0 , 0 );
			}
			// xiatian add end
			// xiatian add end
			resetTransform( batch );
		}
		if( DefaultLayout.enable_new_particle )
		{
			newDrawParticle( batch );
		}
	}
	
	/************************ added by zhenNan.ye begin *************************/
	private void drawParticleEffect(
			SpriteBatch batch )
	{
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( ParticleManager.dropEnable )
				{
					Tween tween = getTween();
					if( tween != null )
					{
						float targetValues[] = tween.getTargetValues();
						float iconHeight = Utils3D.getIconBmpHeight();
						float targetCenterX = targetValues[0] + width / 2;
						float targetCenterY = targetValues[1] + ( height - iconHeight ) + iconHeight / 2;
						float curCenterX = x + width / 2;
						float curCenterY = y + ( height - iconHeight ) + iconHeight / 2;
						if( Math.abs( curCenterX - targetCenterX ) < 2 || Math.abs( curCenterY - targetCenterY ) < 2 )
						{
							stopParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP );
							startParticle( ParticleManager.PARTICLE_TYPE_NAME_DROP , targetCenterX , targetCenterY );
							ParticleManager.dropEnable = false;
						}
					}
				}
				drawParticle( batch );
			}
		}
	}
	
	/************************ added by zhenNan.ye end ***************************/
	private void blurInitial()
	{
		if( shader == null )
		{
			shader = createDefaultShader();
		}
		if( !blurInitialised )
		{
			// suyu
			int fboWidth = (int)( Gdx.graphics.getWidth() * DefaultLayout.fboScale );
			int fboHeight = (int)( Gdx.graphics.getHeight() * DefaultLayout.fboScale );
			fbo = new FrameBuffer( Format.RGBA8888 , fboWidth , fboHeight , false );
			fbo2 = new FrameBuffer( Format.RGBA8888 , fboWidth , fboHeight , false );
			fboRegion = new TextureRegion( fbo.getColorBufferTexture() , 0 , fbo.getHeight() , fbo.getWidth() , -fbo.getHeight() );
			fboRegion2 = new TextureRegion( fbo2.getColorBufferTexture() , 0 , fbo2.getHeight() , fbo2.getWidth() , -fbo2.getHeight() );
			if( blurBatch == null )
			{
				blurBatch = new SpriteBatch();
				Matrix4 pMat = new Matrix4();
				pMat.setToOrtho2D( 0 , 0 , fboWidth , fboHeight );
				blurBatch.setProjectionMatrix( pMat );
				blurBatch.setShader( shader );
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
		if( liveWallpaperActive )
		{
			if( lwpBackView != null )
			{
				lwpBackView.remove();
				lwpBackView.dispose();
				lwpBackView = null;
			}
		}
		if( blurredView != null )
		{
			blurredView.remove();
			blurredView.dispose();
			blurredView = null;
		}
		// if (blurBatch != null)
		// {
		// blurBatch.dispose();
		// blurBatch = null;
		// }
		if( fbo != null )
		{
			fbo.dispose();
			fbo = null;
		}
		if( fbo2 != null )
		{
			fbo2.dispose();
			fbo2 = null;
		}
		if( fboRegion != null && fboRegion.getTexture() != null )
		{
			fboRegion.getTexture().dispose();
			fboRegion = null;
		}
		if( fboRegion2 != null && fboRegion2.getTexture() != null )
		{
			fboRegion2.getTexture().dispose();
			fboRegion2 = null;
		}
	}
	
	// suyu
	public static ShaderProgram createDefaultShader()
	{
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
		{
			FileHandle vShader = null;
			FileHandle fShader = null;
			vShader = Gdx.files.internal( "theme/folder/blur.vert.txt" );
			fShader = Gdx.files.internal( "theme/folder/blur.frag.txt" );
			String vShaderString;
			String fShaderString;
			vShaderString = vShader.readString();
			fShaderString = fShader.readString();
			ShaderProgram shader = new ShaderProgram( vShaderString , fShaderString );
			if( shader.isCompiled() == false )
			{
				if( RR.net_version )
				{
					return null;
				}
				else
					throw new IllegalArgumentException( "couldn't compile shader " + ": " + shader.getLog() );
			}
			return shader;
		}
		return null;
	}
	
	// suyu
	public void renderTo(
			FrameBuffer src ,
			FrameBuffer des )
	{
		if( !RR.net_version )
		{
			if( des != null )
			{
				des.begin();
			}
			Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
			if( des == null )
			{
				Gdx.gl.glClearColor( 0.0f , 0.0f , 0.0f , 0.1f );
			}
			blurBatch.begin();
			TextureRegion region = null;
			shader.setUniformf( "xStep" , 1.0f / fbo.getWidth() );
			shader.setUniformf( "yStep" , 1.0f / fbo.getHeight() );
			shader.setUniformf( "radius" , DefaultLayout.blurRadius );
			if( src == fbo )
			{
				region = fboRegion;
				shader.setUniformi( "vertical" , 0 );
			}
			else if( src == fbo2 )
			{
				region = fboRegion2;
				shader.setUniformi( "vertical" , 1 );
			}
			blurBatch.draw( region , 0 , 0 );
			blurBatch.end();
			if( des != null )
			{
				des.end();
			}
		}
	}
	
	public static void genWallpaperTextureRegion()
	{
		if( !( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) || !DefaultLayout.blur_enable )
			return;
		if( !( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) || !DefaultLayout.blur_enable || !DefaultLayout.blur_bg_enable )
		{
			return;
		}
		if( Gdx.app == null )
		{
			return;
		}
		Gdx.app.postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
				WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
				if( wallpaperInfo != null )
				{
					liveWallpaperActive = true;
					wallpaperTextureRegion = null;
				}
				else
				{
					liveWallpaperActive = false;
					Drawable drawable = wallpaperManager.getDrawable();
					Bitmap wallpaperBitmap = ( (BitmapDrawable)drawable ).getBitmap();
					BitmapTexture texture = new BitmapTexture( wallpaperBitmap );
					if( wallpaperTextureRegion == null )
					{
						wallpaperTextureRegion = new TextureRegion();
					}
					Texture t = wallpaperTextureRegion.getTexture();
					if( t != null )
						t.dispose();
					wallpaperTextureRegion.setRegion( texture );
				}
			}
		} );
	}
	
	public void calcWallpaperOffset()
	{
		if( DefaultLayout.blur_bg_enable )
		{
			if( wallpaperTextureRegion != null )
			{
				int wpWidth = wallpaperTextureRegion.getTexture().getWidth();
				int screenWidth = Utils3D.getScreenWidth();
				if( wpWidth > screenWidth )
				{
					int curScreen = iLoongLauncher.getInstance().getCurrentScreen();
					int screenNum = iLoongLauncher.getInstance().getScreenCount();
					int gapWidth = wpWidth - screenWidth;
					wpOffsetX = -(int)( (float)gapWidth * curScreen / ( screenNum - 1 ) );
				}
				else
				{
					wpOffsetX = 0;
				}
			}
		}
	}
	
	private void MiuiV5FolderBlurFinish()
	{
		blurBegin = false;
		if( blurCount < ( DefaultLayout.blurInterate << 1 ) )
		{
			blurCompleted = false;
		}
		else
		{
			if( blurredView == null )
			{
				Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
				Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
				float scaleFactor = 1 / DefaultLayout.fboScale;
				blurredView = new ImageView3D( "blurredView" , fboRegion );
				blurredView.setScale( scaleFactor , scaleFactor );
				blurredView.show();
				blurredView.setPosition(
						blurredView.getWidth() / 2 + ( Gdx.graphics.getWidth() / 2 - blurredView.getWidth() ) ,
						blurredView.getHeight() / 2 + ( Gdx.graphics.getHeight() / 2 - blurredView.getHeight() ) );
				// teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true && this.mInfo.opened == true && this.from_Where == FolderIcon3D.FROM_APPLIST && this.exit_to_where == FolderIcon3D.TO_APPLIST )
				{
					viewParent.transform = false;
					viewParent.setScale( 1.0f , 1.0f );
					this.addViewAt( this.mFolderIndex , this.mFolderMIUI3D );
					// viewParent.addViewBefore(this, this.blurredView);
					this.addView( this.blurredView );
					// viewParent.addView( this.blurredView);
					// color.a = 0f;
					if( viewParent instanceof AppHost3D )
					{
						( (AppHost3D)viewParent ).hideApphostV5();
						ishide = true;
					}
					// if (liveWallpaperActive) {
					// lwpBackView = new ImageView3D("lwpBackView", ThemeManager
					// .getInstance().getBitmap(
					// "theme/pack_source/translucent-bg.png"));
					// lwpBackView.setSize(Gdx.graphics.getWidth(),
					// Gdx.graphics.getHeight());
					// lwpBackView.setPosition(0, 0);
					// addView(lwpBackView);
					// }
					return;
				}
				// teappotXu add end
				root.transform = false;
				root.setScale( 1.0f , 1.0f );
				if( getFromWhere() == FROM_CELLLAYOUT )
				{
					addViewAt( mFolderIndex , mFolderMIUI3D );
					if( folder_style == folder_rotate_style )
					{
						addViewBefore( mFolderMIUI3D , blurredView );
					}
					else
					{
						addView( blurredView );
					}
					// teapotXu add start
					if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
					{
						Log.v( "teapot" , "FolderIcon3D ----- MiuiV5FolderBlurFinish ---- donot add folder into root ---" );
						this.setScale( 1.0f / Root3D.scaleFactor , 1.0f / Root3D.scaleFactor );
					}
					else
					{
						root.addView( this );
						// teapotXu add start
						this.requestFocus();
						// teapotXu add end
					}
					// teapotXu add end
					root.hideOtherView();
					ishide = true;
					// if (liveWallpaperActive) {
					// lwpBackView = new ImageView3D("lwpBackView", ThemeManager
					// .getInstance().getBitmap(
					// "theme/pack_source/translucent-bg.png"));
					// lwpBackView.setSize(Gdx.graphics.getWidth(),
					// Gdx.graphics.getHeight());
					// lwpBackView.setPosition(0, 0);
					// addView(lwpBackView);
					// ishide = true;
					// }
				}
				else
				{
					// if (liveWallpaperActive) {
					// lwpBackView = new ImageView3D("lwpBackView",
					// ThemeManager.getInstance().getBitmap(
					// "theme/pack_source/translucent-bg.png"));
					// lwpBackView.setSize(Gdx.graphics.getWidth(),
					// Gdx.graphics.getHeight());
					// lwpBackView.setPosition(0, 0);
					// root.addView(lwpBackView);
					// }
					this.addViewAt( this.mFolderIndex , this.mFolderMIUI3D );
					root.addView( this.blurredView );
					root.addView( this );
					root.hideOtherView();
					color.a = 0f;
					ishide = true;
				}
			}
		}
	}
	
	public int getViewIndex(
			View3D view )
	{
		return children.indexOf( view );
	}
	
	public void closeFolderStartAnimMIUI()
	{
		mInfo.opened = false;
		setFolderIconSize( mInfo.x , mInfo.y , 0 , 0 );
		if( folder_style == FolderIcon3D.folder_rotate_style )
		{
			folder_layout_rotate_without_anim();
		}
		else
		{
			Log.v( "miui3D" , "FolderICON3D closeFolderStartAnimMIUI mInfo.opened=" + mInfo.opened );
			folder_layout_scale_without_anim();
		}
		if( getFromWhere() == FROM_HOTSEAT )
		{
			if( DefaultLayout.hotseat_hide_title )
			{
				setSize( R3D.workspace_cell_width , getIconBmpHeight() );
			}
		}
	}
	
	// zhujieping add end
	// xiatian add start //for mainmenu sort by user
	public void showUninstall(
			boolean isFast )
	{
		uninstall = true;
		shake( true , isFast );
	}
	
	public void shake(
			boolean shake ,
			boolean isFast )
	{
		if( isShake == shake )
			return;
		if( !shake )
		{
			// Log.e(FolderIcon3D, "FolderIcon3D:" + this + " shake:" + shake);
		}
		isShake = shake;
		if( shake )
		{
			if( shakeAnimation != null )
			{
				shakeAnimation.free();
			}
			// xiatian start //for mainmenu sort by user
			// xiatian del start
			// Double d = Math.random();
			// shakeTime = (float) (d * 0.03f + 0.09f);
			//
			// shakeAnimation = this.startTween(View3DTweenAccessor.ROTATION,
			// Linear.INOUT, shakeTime, SHAKE_ROTATE_ANGLE, 0,
			// 0).setCallback(this);
			// xiatian del end
			// xiatian add start
			if( true )
			{
				float scale = SHAKE_SCALE_MIN;
				int data = 0;
				float mShakeTime = MathUtils.random( FIRST_SHAKE_TIME_MIN , FIRST_SHAKE_TIME_MAX );
				if( isFast )
				{
					mShakeTime = shakeTime / 2;
				}
				shakeAnimation = this.startTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , mShakeTime , scale , scale , 0 ).setUserData( data ).setCallback( this );
			}
			else
			{
				Double d = Math.random();
				shakeTime = (float)( d * 0.03f + 0.09f );
				shakeAnimation = this.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , shakeTime , SHAKE_ROTATE_ANGLE , 0 , 0 ).setCallback( this );
			}
			// xiatian add end
			// xiatian end
		}
		else
		{
			if( shakeAnimation != null )
			{
				shakeAnimation.free();
			}
			shakeAnimation = null;
			this.setScale( 1f , 1f );
		}
	}
	
	public void reset_shake_status()
	{
		isShake = false;
		if( shakeAnimation != null )
		{
			shakeAnimation.free();
		}
		shakeAnimation = null;
		this.setScale( 1f , 1f );
	}
	
	public void clearState()
	{
		uninstall = false;
		reset_shake_status();
	}
	
	private void drawDelTipIcon(
			SpriteBatch batch ,
			float offsetX ,
			float offsetY )
	{
		float pos_x = 0;
		float pos_y = 0;
		pos_x = offsetX + folder_front.x + R3D.workspace_cell_width - stateIconWidth;
		pos_y = offsetY + folder_front.y + folder_front_height - R3D.folder_gap_height - stateIconHeight;
		batch.draw( uninstallTexture , pos_x , pos_y , stateIconWidth , stateIconHeight );
	}
	
	public boolean isPointInBreakUpFolderIconRect(
			float x ,
			float y )
	{
		boolean ret = false;
		float pos_x = 0;
		float pos_y = 0;
		pos_x = folder_front.x + R3D.workspace_cell_width - stateIconWidth;
		pos_y = folder_front.y + folder_front_height - R3D.folder_gap_height - stateIconHeight;
		if( ( ( x >= pos_x ) && ( x <= ( pos_x + stateIconWidth ) ) ) && ( ( y >= pos_y ) && ( y <= ( pos_y + stateIconHeight ) ) ) )
		{
			ret = true;
		}
		return ret;
	}
	
	public void showHideItemsInOpenedFolder()
	{
		if( hideIconList.size() == 0 )
		{
			return;
		}
		GridView3D mGridView3D = null;
		for( int i = 0 ; i < mFolderMIUI3D.getChildCount() ; i++ )
		{
			View3D mView3D = mFolderMIUI3D.getChildAt( i );
			if( mView3D instanceof GridView3D )
			{
				mGridView3D = (GridView3D)mView3D;
				break;
			}
		}
		ArrayList<View3D> hidelist = new ArrayList<View3D>();
		for( Icon3D mIcon3D : hideIconList )
		{
			changeTextureRegion( mIcon3D , R3D.workspace_cell_height );
			mIcon3D.setInShowFolder( true );
			mIcon3D.setVisible( true );
			mIcon3D.setScale( 1 , 1 );
			mIcon3D.setOrigin( 0 , 0 );// 设置icon中心点，否则脉冲动画的放大缩小的中心点不统一。
			hidelist.add( mIcon3D );
		}
		mGridView3D.addItem( hidelist );
	}
	// xiatian add end
}
