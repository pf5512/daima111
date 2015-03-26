package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL11;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.net.Uri;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.coco.theme.themebox.util.Tools;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.cooee.android.launcher.framework.LauncherSettings.Favorites;
import com.iLoong.RR;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.ParticleAnim.ParticleCallback;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewCircled3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DVirtual;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.recent.RecentApp;
import com.iLoong.launcher.recent.RecentAppHolder;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class Icon3D extends ViewCircled3D implements IconBase3D
{
	
	public static final int MSG_ICON_DOUBLECLICK = 0;
	public static final int MSG_ICON_LONGCLICK = 1;
	public static final int MSG_ICON_CLICK = 2;
	public static final int MSG_ICON_SELECTED = 3;
	public static final int MSG_ICON_UNSELECTED = 4;
	public static final int MSG_ICON_SHORTCLICK = 5;
	public static boolean isDrawnew = false;
	ItemInfo info;
	// ImageView3D selectedImage;
	// ImageView3D uninstallImage;
	// ImageView3D hideImage;
	// ImageView3D showImage;
	// teapotXu add start for icon3D's double-click optimization
	protected boolean selected = false;
	// teapotXu add end
	private boolean isSelected = false;
	protected boolean uninstall = false;
	protected boolean canUninstall;
	protected boolean hide = false;
	protected boolean isHide = false;
	// public static Bitmap iconBg;
	// public static Bitmap iconBg_hl;
	public static Bitmap titleBg;
	private static Bitmap[] iconBgs;
	private static Bitmap mask;
	private static Bitmap cover;
	public static TextureRegion selectedTexture;
	public static TextureRegion resumeTexture;
	public static TextureRegion hideTexture;
	public static TextureRegion uninstallTexture;
	public static TextureRegion paopaoRegion1;
	public static TextureRegion paopaoRegion2;
	public static TextureRegion[] numTextureRegion;
	public static ArrayList<ItemInfo> shortcutInfoListDb;
	private TextureRegion paopaoRegion;
	protected static int stateIconWidth;
	protected static int stateIconHeight;
	protected static int paopaoStateIconPadding_X;
	protected static int paopaoStateIconPadding_Y;
	protected static int paopaoStateIconWidth1;
	protected static int paopaoStateIconWidth2;
	protected static int paopaoNumStateIconWidth;
	protected static int paopaoNumStateIconHeight;
	private int paopaoStateIconWidth;
	private View3D tmpView = null;
	protected boolean needShowPaoPao = false;
	protected boolean inShowFolder = false;
	private boolean isSmsFlag = true; // 是短信还是电�?
	// private TextureRegion uninstallRegion = R3D.findRegion("app-uninstall");
	// private TextureRegion stopRegion = R3D.findRegion("task_stop");
	// private Tween shakeAnimation = null;
	// private Tween scaleAnimation = null;
	int number = 0;
	float paopao_stateX = 0f;
	float paopao_stateY = 0f;
	public float tempWidth;
	public float tempHeight;
	// xiatian add start //DownloadIcon
	public static TextureRegion dynamicMenuDownloadTexture;
	protected static int dynamicMenuDownloadIconWidth;
	protected static int dynamicMenuDownloadIconHeight;
	// xiatian add end
	// teapotXu add start for new added app flag
	protected boolean b_first_added = false;
	public static TextureRegion newAddedTexture;
	// teapotXu add end for new added app flag
	/************************ added by zhenNan.ye begin ***************************/
	protected boolean doubleClickFlag = false;
	private boolean particle_need_launch_app = false;
	/************************ added by zhenNan.ye end ***************************/
	/************************ added by diaosixu begin ***************************/
	private boolean isNeedToRemind = false;
	private static TextureRegion redPoint = null;
	/************************ added by diaosixu end ***************************/
	protected static DrawDynamicIcon drawDyIcon;
	public static float mScreenScale = 1;
	static
	{
		iconBgs = new Bitmap[R3D.icon_bg_num];
		for( int i = 0 ; i < iconBgs.length ; i++ )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/icon_" + i + ".png" );
			Bitmap bmp2 = Bitmap.createScaledBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			iconBgs[i] = bmp2;
			if( bmp != bmp2 )
				bmp.recycle();
		}
		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/icon_cover_plate.png" );
		if( original_icon_cover_bmp != null )
		{
			cover = Bitmap.createScaledBitmap( original_icon_cover_bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			if( original_icon_cover_bmp != cover )
				original_icon_cover_bmp.recycle();
		}
		Bitmap oriMask = ThemeManager.getInstance().getBitmap( "theme/iconbg/mask.png" );
		if( oriMask != null )
		{
			mask = Bitmap.createScaledBitmap( oriMask , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			if( oriMask != mask )
				oriMask.recycle();
		}
		// FileHandle file =
		// ThemeManager.getInstance().getGdxTextureResource("theme/iconbg/icon_hl.png");
		// Bitmap bmp = BitmapFactory.decodeStream(file.read());
		// iconBg_hl = Bitmap.createScaledBitmap(bmp, R3D.workspace_cell_width,
		// R3D.workspace_cell_width,false);
		if( DefaultLayout.show_font_bg && !DefaultLayout.font_double_line )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/title_bg.png" );
			Paint p = new Paint();
			p.setTextSize( R3D.icon_title_font );
			FontMetrics fontMetrics = p.getFontMetrics();
			float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
			titleBg = Bitmap.createScaledBitmap( bmp , R3D.workspace_cell_width , (int)( singleLineHeight + singleLineHeight / 5 ) , true );
			bmp.recycle();
		}
		stateIconWidth = R3D.workspace_multicon_width;
		stateIconHeight = R3D.workspace_multicon_height;
		paopaoStateIconWidth1 = stateIconWidth;
		paopaoStateIconWidth2 = R3D.workspace_paopao_big_width;
		paopaoNumStateIconWidth = R3D.workspace_paopao_num_width;
		paopaoNumStateIconHeight = R3D.workspace_paopao_num_height;
		paopaoStateIconPadding_X = R3D.paopao_state_icon_padding_x;
		paopaoStateIconPadding_Y = R3D.paopao_state_icon_padding_y;
		// xiatian add start //DownloadIcon
		dynamicMenuDownloadIconWidth = R3D.dynamic_menu_download_icon_width;
		dynamicMenuDownloadIconHeight = R3D.dynamic_menu_download_icon_height;
		// xiatian add end
		// resumeTexture = new TextureRegion(new Texture3D(new
		// Pixmap(Gdx.files.internal("theme/pack_source/app-resume.png"))));
		resumeTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-resume.png" ) , true ) );
		hideTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-hide.png" ) , true ) );
		uninstallTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) , true ) );
		if( !DefaultLayout.net_lite )
		{
			paopaoRegion1 = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian.png" ) , true ) );
			paopaoRegion2 = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian_2.png" ) , true ) );
			numTextureRegion = new TextureRegion[10];
			for( int i = 0 ; i < 10 ; i++ )
			{
				numTextureRegion[i] = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian_num" + i + ".png" ) , true ) );
			}
		}
		selectedTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-multi-choice.png" ) , true ) );
		// xiatian add start //DownloadIcon
		dynamicMenuDownloadTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/dynamic_menu_download.png" ) , true ) );
		// xiatian add end
		// teapotXu add start for new added app flag
		if( DefaultLayout.enable_new_add_app_flag == true )
		{
			newAddedTexture = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-new-added.png" ) , true ) );
		}
		// teapotXu add end for new added app flag
		if( DefaultLayout.dynamic_icon )
		{
			drawDyIcon = new DrawDynamicIcon();
		}
		/************************ added by diaosixu begin ***************************/
		redPoint = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-reminder-red-point.png" ) , true ) );
		/************************ added by diaosixu end ***************************/
	}
	
	// onThemeChanged
	public static void reInit()
	{
		if( iconBgs == null )
			return;
		for( int i = 0 ; i < iconBgs.length ; i++ )
		{
			iconBgs[i].recycle();
			iconBgs[i] = null;
		}
		iconBgs = new Bitmap[R3D.icon_bg_num];
		for( int i = 0 ; i < iconBgs.length ; i++ )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/icon_" + i + ".png" );
			Bitmap bmp2 = Bitmap.createScaledBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			iconBgs[i] = bmp2;
			if( bmp != bmp2 )
				bmp.recycle();
		}
		if( cover != null )
		{
			cover.recycle();
			cover = null;
		}
		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/icon_cover_plate.png" );
		if( original_icon_cover_bmp != null )
		{
			cover = Bitmap.createScaledBitmap( original_icon_cover_bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			if( original_icon_cover_bmp != cover )
				original_icon_cover_bmp.recycle();
		}
		if( mask != null )
		{
			mask.recycle();
			mask = null;
		}
		Bitmap oriMask = ThemeManager.getInstance().getBitmap( "theme/iconbg/mask.png" );
		if( oriMask != null )
		{
			mask = Bitmap.createScaledBitmap( oriMask , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
			if( oriMask != mask )
				oriMask.recycle();
		}
		if( titleBg != null )
		{
			titleBg.recycle();
			titleBg = null;
		}
		if( DefaultLayout.show_font_bg && !DefaultLayout.font_double_line )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/iconbg/title_bg.png" );
			Paint p = new Paint();
			p.setTextSize( R3D.icon_title_font );
			FontMetrics fontMetrics = p.getFontMetrics();
			float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
			titleBg = Bitmap.createScaledBitmap( bmp , R3D.workspace_cell_width , (int)( singleLineHeight + singleLineHeight / 5 ) , true );
			bmp.recycle();
		}
		stateIconWidth = R3D.workspace_multicon_width;
		stateIconHeight = R3D.workspace_multicon_height;
		paopaoStateIconWidth1 = stateIconWidth;
		paopaoStateIconWidth2 = R3D.workspace_paopao_big_width;
		paopaoNumStateIconWidth = R3D.workspace_paopao_num_width;
		paopaoNumStateIconHeight = R3D.workspace_paopao_num_height;
		paopaoStateIconPadding_X = R3D.paopao_state_icon_padding_x;
		paopaoStateIconPadding_Y = R3D.paopao_state_icon_padding_x;
		// xiatian add start //DownloadIcon
		dynamicMenuDownloadIconWidth = R3D.dynamic_menu_download_icon_width;
		dynamicMenuDownloadIconHeight = R3D.dynamic_menu_download_icon_height;
		// xiatian add end
		( (BitmapTexture)resumeTexture.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-resume.png" ) , true );
		( (BitmapTexture)hideTexture.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-hide.png" ) , true );
		( (BitmapTexture)uninstallTexture.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall.png" ) , true );
		( (BitmapTexture)selectedTexture.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/shell-multi-choice.png" ) , true );
		if( !DefaultLayout.net_lite )
		{
			( (BitmapTexture)paopaoRegion1.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian.png" ) , true );
			( (BitmapTexture)paopaoRegion2.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian_2.png" ) , true );
			paopaoRegion1.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			paopaoRegion2.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			for( int i = 0 ; i < 10 ; i++ )
			{
				( (BitmapTexture)numTextureRegion[i].getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/icon_dian_num" + i + ".png" ) , true );
				numTextureRegion[i].getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
		// xiatian add start //DownloadIcon
		( (BitmapTexture)dynamicMenuDownloadTexture.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/dynamic_menu_download.png" ) , true );
		// xiatian add end
		// teapotXu add start for new added app flag
		if( DefaultLayout.enable_new_add_app_flag == true )
		{
			( (BitmapTexture)newAddedTexture.getTexture() ).changeBitmap( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-new-added.png" ) , true );
		}
		// teapotXu add end for new added app flag
		// need to do later
		// drawDyIcon.onThemeChanged();
	}
	
	public Icon3D(
			String name )
	{
		super( name );
	}
	
	public Icon3D(
			String name ,
			Texture texture )
	{
		super( name , texture );
		// selectedImage = new ImageView3D("selectedicon", selectedTexture,
		// R3D.workspace_multicon_width, R3D.workspace_multicon_height);
	}
	
	public Icon3D(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name , new IconToTexture3D( bmp , title , getIconBg() , titleBg ) );
		// selectedImage = new ImageView3D("selectedicon", selectedTexture,
		// R3D.workspace_multicon_width, R3D.workspace_multicon_height);
	}
	
	public Icon3D(
			String name ,
			Bitmap bmp ,
			String title ,
			boolean ifshadow )
	{
		super( name , new IconToTexture3D( bmp , title , getIconBg() , titleBg , ifshadow ) );
	}
	
	public Icon3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap IconBg )
	{
		super( name , new IconToTexture3D( bmp , title , IconBg , titleBg ) );
		// selectedImage = new ImageView3D("selectedicon", selectedTexture,
		// R3D.workspace_multicon_width, R3D.workspace_multicon_height);
	}
	
	public Icon3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap IconBg ,
			boolean ifshadow )
	{
		super( name , new IconToTexture3D( bmp , title , IconBg , titleBg , ifshadow ) );
	}
	
	public Icon3D(
			String name ,
			TextureRegion region )
	{
		super( name , region );
		// selectedImage = new ImageView3D("selectedicon", selectedTexture,
		// R3D.workspace_multicon_width, R3D.workspace_multicon_height);
	}
	
	public void onThemeChanged()
	{
		if( !( info instanceof ShortcutInfo ) )
			return;
		// Log.d("theme", "icon3d onThemeChanged:"+info.title);
		Bitmap bmp;
		ShortcutInfo shortcutInfo = (ShortcutInfo)info;
		width = R3D.workspace_cell_width;
		height = R3D.workspace_cell_height;
		originX = width / 2.0f;
		originY = height / 2.0f;
		if( shortcutInfoListDb != null )
		{
			for( ItemInfo ItemInfoDb : shortcutInfoListDb )
			{
				ShortcutInfo shortcutInfoDb = (ShortcutInfo)ItemInfoDb;
				if( shortcutInfoDb.id == shortcutInfo.id && shortcutInfoDb.container == shortcutInfo.container )
				{
					shortcutInfo.mIcon = shortcutInfoDb.mIcon;
				}
			}
		}
		if( shortcutInfo.intent != null && shortcutInfo.intent.getAction().equals( "com.android.contacts.action.QUICK_CONTACT" ) )
		{
			if( shortcutInfo.mIcon == null || shortcutInfo.mIcon.isRecycled() )
			{
				shortcutInfo.mIcon = null;
				bmp = Bitmap.createBitmap( shortcutInfo.getIcon( iLoongApplication.getInstance().mIconCache ) );
			}
			else
			{
				bmp = shortcutInfo.mIcon.copy( shortcutInfo.mIcon.getConfig() , true );
			}
			if( DefaultLayout.thirdapk_icon_scaleFactor != 1f )
			{
				bmp = Bitmap.createScaledBitmap( bmp , (int)( bmp.getWidth() * DefaultLayout.thirdapk_icon_scaleFactor ) , (int)( bmp.getHeight() * DefaultLayout.thirdapk_icon_scaleFactor ) , true );
			}
			( (BitmapTexture)this.region.getTexture() ).changeBitmap( Utils3D.IconToPixmap3D( bmp , shortcutInfo.title.toString() , Icon3D.getIconBg() , titleBg ) , true );
			region.setV2( 1 );
		}
		else
		{
			if( shortcutInfo.mIcon != null && shortcutInfo.mIcon.isRecycled() )
				shortcutInfo.mIcon = null;
			Desktop3DListener.replaceItemInfoIcon( shortcutInfo );
			String name = shortcutInfo.title.toString();
			if( !name.equals( R3D.folder3D_name ) )
			{
				if( shortcutInfo.container == Favorites.CONTAINER_HOTSEAT )
				{
					if( !name.equals( R3D.folder3D_name ) && Contact3DShortcut.isAContactShortcut( shortcutInfo.intent ) != Contact3DShortcut.CONTACT_DEFAULT )
					{
						if( R3D.icon_bg_num > 0 )
						{
							boolean needAddBg = false;
							if( shortcutInfo.intent != null && shortcutInfo.intent.getComponent() != null && shortcutInfo.intent.getComponent().getPackageName() != null )
							{
								String pkgname = shortcutInfo.intent.getComponent().getPackageName();
								String clsname = shortcutInfo.intent.getComponent().getClassName();
								String iconPath = DefaultLayout.getInstance().getReplaceIconPath( pkgname , clsname );
								if( iconPath != null && null != ThemeManager.getInstance().getCurrentThemeBitmap( iconPath ) )
								{
									needAddBg = false;
								}
								else
								{
									needAddBg = true;
								}
							}
							if( DefaultLayout.app_icon_size > ( shortcutInfo.getIcon( iLoongApplication.mIconCache ).getHeight() ) || needAddBg )
							{
								R3D.packHotseat( shortcutInfo , true , true );
							}
							else
							{
								R3D.packHotseat( shortcutInfo , false , true );
							}
						}
						else
						{
							R3D.packHotseat( shortcutInfo , false , true );
						}
					}
					R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
					if( DefaultLayout.hotseat_hide_title )
					{
						View3D temView = Workspace3D.createShortcut( shortcutInfo , false );
						region = temView.region;
						region.setV2( 1 );
						Utils3D.changeTextureRegion( this , Utils3D.getIconBmpHeight() , true );
					}
					else
					{
						if( Contact3DShortcut.isAContactShortcut( shortcutInfo.intent ) != Contact3DShortcut.CONTACT_DEFAULT )
							region = new TextureRegion( R3D.findRegion( shortcutInfo ) );
						else
							region = new TextureRegion( R3D.findRegion( R3D.contact_name ) );
						region.setV2( 1 );
					}
				}
				else
				{
					if( Contact3DShortcut.isAContactShortcut( shortcutInfo.intent ) != Contact3DShortcut.CONTACT_DEFAULT )
					{
						R3D.pack( shortcutInfo , "" , true );
						R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
						region = new TextureRegion( R3D.findRegion( shortcutInfo ) );
					}
					else
					{
						R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
						region = new TextureRegion( R3D.findRegion( R3D.contact_name ) );
					}
					if( this.viewParent != null && this.viewParent instanceof FolderIcon3D )
					{
						R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
						if( Contact3DShortcut.isAContactShortcut( shortcutInfo.intent ) != Contact3DShortcut.CONTACT_DEFAULT )
							region = new TextureRegion( R3D.findRegion( shortcutInfo ) );
						else
							region = new TextureRegion( R3D.findRegion( R3D.contact_name ) );
						region.setV2( 1 );
					}
					else if( shortcutInfo.intent != null && iLoongLauncher.getInstance().equalHotSeatIntent( shortcutInfo.intent ) != -1 )
					{
						R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
						if( Contact3DShortcut.isAContactShortcut( shortcutInfo.intent ) != Contact3DShortcut.CONTACT_DEFAULT )
							region = new TextureRegion( R3D.findRegion( shortcutInfo ) );
						else
							region = new TextureRegion( R3D.findRegion( R3D.contact_name ) );
						region.setV2( 1 );
					}
				}
			}
		}
	}
	
	public void updateNumber()
	{
		//		if( isSmsFlag == true )
		//		{
		//			number = iLoongLauncher.getInstance().getMissedSmsNum();
		//		}
		//		else
		//		{
		//			number = iLoongLauncher.getInstance().getMissedCallNum();
		//		}
	}
	
	public void setting()
	{
		if( !DefaultLayout.show_missed_call_sms )
			return;
		//		Log.v( "Icon3D" , "paopao setting" );
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		if( info.intent.getComponent() == null )
		{
			if( info.intent.getAction() != null )
			{
				if( info.intent.getAction().equals( "android.intent.action.MAIN" ) )
				{
					if( info.intent.getType() != null && info.intent.getType().equals( "vnd.android-dir/mms-sms" ) )
					{
						needShowPaoPao = true;
						isSmsFlag = true;
					}
				}
				else if( info.intent.getAction().equals( "android.intent.action.DIAL" ) )
				{
					needShowPaoPao = true;
					isSmsFlag = false;
				}
			}
		}
		else
		{
			String packageName = info.intent.getComponent().getPackageName();
			String className = info.intent.getComponent().getClassName();
			needShowPaoPao = false;
			if( packageName != null && className != null )
			{
				if( packageName.equals( "com.android.mms" ) )
				{
					needShowPaoPao = true;
					isSmsFlag = true;
				}
				else if( packageName.equals( "com.android.contacts" ) && ( className.equals( DefaultLayout.call_component_name ) || ( DefaultLayout.show_missed_call_sms_in_appList && className
						.equals( DefaultLayout.call_component_name_applist ) ) ) )
				{
					needShowPaoPao = true;
					isSmsFlag = false;
				}
			}
		}
		if( needShowPaoPao == true )
		{
			Log.v( "Icon3D" , "setting ok" );
			// regsitedMissCall();
			updateNumber();
		}
	}
	
	public static final Vector2 point = new Vector2();
	static final Vector2 coords = new Vector2();
	
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "click" , "View3D onClick:" + name + " x:" + x + " y:" + y );
		SendMsgToAndroid.sysPlaySoundEffect();
		if( !hide && "浏览器".equals( name ) && !selected )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
			String pkgname = prefs.getString( "BROWERpackagerName" , null );
			String classname = prefs.getString( "BROWERclassName" , null );
			if( pkgname != null && classname != null )
			{
				Intent intent = new Intent();
				intent.setClassName( pkgname , classname );
				iLoongLauncher.getInstance().startActivity( intent );
				return true;
			}
		}
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				particle_need_launch_app = false;
				ParticleManager manager = ParticleManager.getParticleManager();
				if( manager != null && true == manager.isAnyParticleAnimPlayingExpSelf( this , ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON ) )
				{
					Log.v( "cooee" , "----icon3D ---onClick ----- another icon3D is play particle animation , so discards this one ---" );
					stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON );
					return true;
				}
			}
		}
		// teapotXu add start for icon3D's double-click optimization
		if( selected == true )
		{
			if( hide || uninstall )
			{
				selected = false;
				return true;
			}
			if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( inShowFolder ) )
			{
				selected = false;
				if( ( uninstall ) && ( canUninstall ) && ( info instanceof ShortcutInfo ) )
				{
					uninstall();
				}
				return true;
			}
			//xiatian add end
			//xiatian end
			// 当点击中的是虚图标，那么直接退出选中模式
			if( isApplistVirtualIcon() == true )
			{
				selected = false;
				return false;
			}
			if( isSelected() )
			{
				cancelSelected();
			}
			else
			{
				//xiatian add start	//for mainmenu sort by user
				if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( uninstall ) && ( canUninstall ) && ( info instanceof ShortcutInfo ) && ( isPointInBreakUpFolderIconRect( x , y ) ) )
				{
					uninstall();
					return true;
				}
				//xiatian add end
				selected();
			}
			return true;
		}
		// teapotXu add end
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			return true;
		}
		if( isSelected() )
		{
			cancelSelected();
			/************************ added by zhenNan.ye begin *************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					doubleClickFlag = true;
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			return true;
		}
		else if( uninstall )
		{
			if( !canUninstall )
			{
			}
			else if( info instanceof ShortcutInfo )
			{
				uninstall();
			}
			return true;
		}
		else if( hide )
		{
			ShortcutInfo temp = (ShortcutInfo)this.getItemInfo();
			ComponentName name = null;
			if( temp != null && temp.intent != null )
			{
				name = temp.intent.getComponent();
			}
			if( name != null )
			{
				isHide = !isHide;
				PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putBoolean( "HIDE:" + name.toString() , isHide ).commit();
				if( temp.appInfo != null )
					temp.appInfo.isHideIcon = isHide;
			}
			return true;
		}
		else
		{
			if( ( iLoongLauncher.getInstance().getD3dListener().getAppList().appList.mode != iLoongLauncher.getInstance().getD3dListener().getAppList().appList.APPLIST_MODE_NORMAL ) && ( isApplistVirtualIcon() == true ) )
			{
				return true;
			}
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					if( particleCanRender )
					{
						particle_need_launch_app = true;
						return true;
					}
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			if( viewParent != null )
			{
				return viewParent.onCtrlEvent( this , MSG_ICON_CLICK );
			}
			else
			{
				return false;
			}
		}
		// return viewParent.onCtrlEvent(this, MSG_ICON_CLICK);
	}
	
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( isApplistVirtualIcon() == true )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
			return true;
		}
		if( hide || uninstall )
			return true;
		if( isSelected() )
		{
			cancelSelected();
		}
		else
		{
			selected();
		}
		return true;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		this.color.a = 1f;
		Log.v( "click" , " icon3d onLongClick:" + name + " x:" + x + " y:" + y );
		if( isApplistVirtualIcon() == true )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
			return true;
		}
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
		{
			if( hide )
				return true;
		}
		else if( hide || uninstall )
			return true;
		// teapotXu add end for Folder in Mainmenu
		if( !this.isDragging )
		{
			// if(this.getParent().getParent() instanceof AppList3D){
			// ClingManager.getInstance().cancelAllAppCling();
			// }
			this.toAbsoluteCoords( point );
			this.setTag( new Vector2( point.x , point.y ) );
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			DragLayer3D.dragStartX = point.x;
			DragLayer3D.dragStartY = point.y;
			return viewParent.onCtrlEvent( this , MSG_ICON_LONGCLICK );
		}
		return false;
	}
	
	/* 画数�?x,y是中心点 */
	public void drawNumber(
			SpriteBatch batch ,
			float parentAlpha ,
			float f ,
			float g ,
			int number )
	{
		int num = number;
		int i = 0 , w = 0;
		if( num != 0 )
		{
			while( num != 0 )
			{
				num /= 10;
				i++;
			}
		}
		else
		{
			i = 1;
		}
		w = ( i + 1 ) * paopaoNumStateIconWidth / 2;
		g -= paopaoNumStateIconHeight / 2;
		for( int j = 0 ; j < i ; j++ )
		{
			int k = number % 10;
			number /= 10;
			batch.draw( numTextureRegion[k] , f - w + ( i - 1 - j ) * paopaoNumStateIconHeight , g );
		}
	}
	
	/* 画数�?x,y是中心点 */
	public void drawNumber(
			SpriteBatch batch ,
			float parentAlpha ,
			float f ,
			float g ,
			int number ,
			float orgx ,
			float orgy ,
			float scalex ,
			float scaley ,
			float rotate )
	{
		int num = number;
		int i = 0 , w = 0;
		if( num != 0 )
		{
			while( num != 0 )
			{
				num /= 10;
				i++;
			}
		}
		else
		{
			i = 1;
		}
		w = (int)( i * paopaoNumStateIconWidth / 2 );
		g -= paopaoNumStateIconHeight / 2;
		for( int j = 0 ; j < i ; j++ )
		{
			int k = number % 10;
			number /= 10;
			float x = f - w + ( i - 1 - j ) * paopaoNumStateIconWidth;
			batch.draw( numTextureRegion[k] , x , g , this.x + orgx - x , orgy - this.height + paopaoNumStateIconHeight , paopaoNumStateIconWidth , paopaoNumStateIconHeight , scalex , scaley , rotate );
		}
	}
	
	public boolean is3dRotation()
	{
		if( DefaultLayout.dynamic_icon )
		{
			ShortcutInfo textureinfo = (ShortcutInfo)this.getItemInfo();
			if( DefaultLayout.getInstance().getDynamicIcon( textureinfo ) != -1 )
			{
				return rotation != 0;
			}
		}
		return rotation != 0 && ( getRotation3DVector().x != 0 || getRotation3DVector().y != 0 );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( region.getTexture() == null )
			return;
		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect( batch );
		/************************ added by zhenNan.ye end ***************************/
		if( region.getTexture() instanceof BitmapTexture )
		{
			BitmapTexture texture = (BitmapTexture)region.getTexture();
			if( needAntiAlias() )
				texture.changeFilter( TextureFilter.Linear , TextureFilter.Linear );
			else
				texture.changeFilter( TextureFilter.Nearest , TextureFilter.Nearest );
		}
		// Log.d("icon",
		// "AA:"+(region.getTexture().getMinFilter()==TextureFilter.Linear));
		int srcBlendFunc = 0 , dstBlendFunc = 0;
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			/* 获取获取混合方式 */
			srcBlendFunc = batch.getSrcBlendFunc();
			dstBlendFunc = batch.getDstBlendFunc();
		}
		// teapotXu add start for Folder in Mainmenu
		float iconRotation = this.rotation;
		if( DefaultLayout.mainmenu_folder_function && isShake )
		{
			iconRotation = 0f;
		}
		// new added app flag
		if( DefaultLayout.enable_new_add_app_flag == true )
		{
			if( ( (ShortcutInfo)this.info ).appInfo != null )
			{
				if( canUninstall && ( (ShortcutInfo)this.info ).appInfo.extendible_flag == 0 )
				{
					b_first_added = true;
				}
				else
					b_first_added = false;
			}
		}
		// teapotXu add end for Folder in Mainmenu
		int x = Math.round( this.x );
		int y = Math.round( this.y );
		float alpha = color.a;
		if( isHide && ( uninstall || hide ) )
		{
			alpha *= 0.2;
		}
		updateNumber();
		if( number > 9 )
		{
			paopaoRegion = paopaoRegion2;
			paopaoStateIconWidth = paopaoStateIconWidth2;
		}
		else
		{
			paopaoRegion = paopaoRegion1;
			paopaoStateIconWidth = paopaoStateIconWidth1;
		}
		batch.setColor( color.r , color.g , color.b , alpha * parentAlpha );
		float stateX = this.x + this.width - stateIconWidth;
		float stateY = this.y + this.height - stateIconHeight;
		paopao_stateX = this.x + this.width - paopaoStateIconWidth - paopaoStateIconPadding_X;
		paopao_stateY = this.y + this.height - stateIconHeight - paopaoStateIconPadding_Y;
		// xiatian add start //DownloadIcon
		float dynamicMenuDownloadX = 0f , dynamicMenuDownloadY = 0f;
		dynamicMenuDownloadX = getDownloadIconX();
		dynamicMenuDownloadY = getDownloadIconY();
		// xiatian add end
		if( is3dRotation() )
		{
			batch.draw( region , x , y , width , height );
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA );
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			if( DefaultLayout.dynamic_icon )
			{
				ItemInfo temp = this.getItemInfo();
				drawDyIcon.drawDynamicIcon( batch , temp , x , y , originX , originY , width , height , 1 , 1 , 0 );
				// Gdx.graphics.requestRendering();
			}
			if( uninstall && canUninstall )
			{
				if( !isApplistVirtualIcon() )
				{
					batch.draw( uninstallTexture , stateX , stateY , stateIconWidth , stateIconHeight );
				}
			}
			if( hide && isHide )
			{
				batch.draw( resumeTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
			if( hide && !isHide )
			{
				batch.draw( hideTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
			if( !isSelected && needShowPaoPao && number > 0
			// xiatian add start //for mainmenu sort by user
			&& ( !( hide ) && !( uninstall ) )
			// xiatian add end
			)
			{
				batch.draw( paopaoRegion , paopao_stateX , paopao_stateY , paopaoStateIconWidth , stateIconHeight );
				drawNumber( batch , parentAlpha , paopao_stateX + paopaoStateIconWidth / 2 , paopao_stateY + stateIconHeight / 2 , number );
			}
			// teapotXu add start for new added app flag
			if( b_first_added && DefaultLayout.enable_new_add_app_flag )
			{
				batch.draw( newAddedTexture , this.x , stateY , stateIconWidth , stateIconHeight );
			}
			// teapotXu add end for new added app flag
			/************************ added by diaosixu begin ***************************/
			if( isNeedToRemind && !isDragging && !( viewParent instanceof FolderIcon3D ) )
			{
				batch.draw( redPoint , stateX + stateIconWidth * 0.2f , stateY + stateIconHeight * 0.4f , stateIconWidth * 0.4f , stateIconHeight * 0.4f );
			}
			/************************ added by diaosixu end ***************************/
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
			if( isApplistVirtualIcon() == true )
			{
				batch.draw( dynamicMenuDownloadTexture , dynamicMenuDownloadX , dynamicMenuDownloadY , dynamicMenuDownloadIconWidth , dynamicMenuDownloadIconHeight );
			}
			if( this instanceof WidgetIcon )
			{
				WidgetIcon icon = (WidgetIcon)this;
				ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
				if( !icon.isFeatureShortcut() && info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
				{
					batch.draw( dynamicMenuDownloadTexture , dynamicMenuDownloadX , dynamicMenuDownloadY , dynamicMenuDownloadIconWidth , dynamicMenuDownloadIconHeight );
				}
			}
			// xiatian add start //EffectPreview
			if( ( DefaultLayout.enable_effect_preview ) && ( isCurEffect() ) )
			{
				batch.draw( R3D.findRegion( R3D.mEffectPreviewSelectRegionName ) , dynamicMenuDownloadX , dynamicMenuDownloadY , dynamicMenuDownloadIconWidth , dynamicMenuDownloadIconHeight );
			}
			// xiatian add end
			if( isSelected )
			{
				batch.draw( selectedTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
		}
		else
		{
			batch.draw( region , x , y , originX , originY , width , height , scaleX , scaleY , iconRotation );
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA );
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			if( DefaultLayout.dynamic_icon )
			{
				ItemInfo temp = this.getItemInfo();
				drawDyIcon.drawDynamicIcon( batch , temp , x , y , originX , originY , width , height , scaleX , scaleY , iconRotation );
				// Gdx.graphics.requestRendering();
			}
			float stateOriginX = originX - this.width + stateIconWidth;
			float stateOriginY = originY - this.height + stateIconHeight;
			// xiatian add start //DownloadIcon
			float dynamicMenuDownloadOriginX = 0f , dynamicMenuDownloadOriginY = 0f;
			dynamicMenuDownloadOriginX = this.x + this.originX - dynamicMenuDownloadX;
			dynamicMenuDownloadOriginY = this.y + this.originY - dynamicMenuDownloadY - 2;
			// xiatian add end
			if( uninstall && canUninstall )
			{
				if( !isApplistVirtualIcon() )
				{
					batch.draw( uninstallTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
				}
			}
			if( hide && isHide )
			{
				batch.draw( resumeTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
			}
			if( hide && !isHide )
			{
				batch.draw( hideTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
			}
			if( !isSelected && needShowPaoPao && number > 0
			// xiatian add start //for mainmenu sort by user
			&& ( !( hide ) && !( uninstall ) )
			// xiatian add end
			)
				if( !DefaultLayout.net_lite )
				{
					float stateOriginX2 = this.x + this.originX - paopao_stateX;
					float stateOriginY2 = originY - this.height + stateIconHeight;
					batch.draw( paopaoRegion , paopao_stateX , paopao_stateY , stateOriginX2 , stateOriginY2 , paopaoStateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation/*
																																														* this.rotation
																																														*/);
					drawNumber(
							batch ,
							parentAlpha ,
							paopao_stateX + paopaoStateIconWidth / 2 ,
							paopao_stateY + stateIconHeight / 2 ,
							number ,
							this.originX ,
							this.originY ,
							scaleX ,
							scaleY ,
							iconRotation/* this.rotation */);
				}
			// teapotXu add start for new added app flag
			if( b_first_added && DefaultLayout.enable_new_add_app_flag )
			{
				batch.draw( newAddedTexture , x , stateY , 0 , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation );
			}
			// teapotXu add end for new added app flag
			/************************ added by diaosixu begin ***************************/
			if( isNeedToRemind && !isDragging && !( viewParent instanceof FolderIcon3D ) )
			{
				batch.draw(
						redPoint ,
						stateX + stateIconWidth * 0.2f ,
						stateY + stateIconHeight * 0.4f ,
						0 ,
						stateOriginY ,
						stateIconWidth * 0.4f ,
						stateIconHeight * 0.4f ,
						scaleX ,
						scaleY ,
						iconRotation );
			}
			/************************ added by diaosixu end ***************************/
			if( DefaultLayout.blend_func_dst_gl_one && ( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA ) )
				batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
			if( isApplistVirtualIcon() == true )
			{
				batch.draw(
						dynamicMenuDownloadTexture ,
						dynamicMenuDownloadX ,
						dynamicMenuDownloadY ,
						dynamicMenuDownloadOriginX ,
						dynamicMenuDownloadOriginY ,
						dynamicMenuDownloadIconWidth ,
						dynamicMenuDownloadIconHeight ,
						scaleX ,
						scaleY ,
						iconRotation/* rotation */);
			}
			if( this instanceof WidgetIcon )
			{
				WidgetIcon icon = (WidgetIcon)this;
				ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
				if( !icon.isFeatureShortcut() && info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
				{
					batch.draw(
							dynamicMenuDownloadTexture ,
							dynamicMenuDownloadX ,
							dynamicMenuDownloadY ,
							dynamicMenuDownloadOriginX ,
							dynamicMenuDownloadOriginY ,
							dynamicMenuDownloadIconWidth ,
							dynamicMenuDownloadIconHeight ,
							scaleX ,
							scaleY ,
							iconRotation/* rotation */
					);
				}
			}
			// xiatian add start //EffectPreview
			if( ( DefaultLayout.enable_effect_preview ) && ( isCurEffect() ) )
			{
				batch.draw(
						R3D.findRegion( R3D.mEffectPreviewSelectRegionName ) ,
						dynamicMenuDownloadX ,
						dynamicMenuDownloadY ,
						dynamicMenuDownloadOriginX ,
						dynamicMenuDownloadOriginY ,
						dynamicMenuDownloadIconWidth ,
						dynamicMenuDownloadIconHeight ,
						scaleX ,
						scaleY ,
						iconRotation/* rotation */
				);
			}
			// xiatian add end
			if( isSelected )
			{
				batch.draw( selectedTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , iconRotation/* rotation */);
			}
		}
		if( tmpView != null )
		{
			tmpView.applyTransformChild( batch );
			tmpView.draw( batch , parentAlpha );
			tmpView.resetTransformChild( batch );
		}
		// point.x = 0;
		// point.y = 0;
		// this.toAbsoluteCoords(point);
		// Log.v("coords"," name:" + name + " x:" + x + " y:" + y + " absX:" +
		// point.x + " absY:" + point.y);
		drawRecentapp( batch , x , y , width , height );
		if( isDrawnew )
		{
			drawLateduse( batch , x , y , width , height );
		}
	}
	
	public TextureRegion lateuse;
	
	private void drawLateduse(
			SpriteBatch batch ,
			int x ,
			int y ,
			float width ,
			float height )
	{
		if( mScreenScale == 1 )
		{
			float mScreenScaleWidth = Utils3D.getScreenWidth() / 720f;
			float mScreenScaleHeight = Utils3D.getScreenHeight() / 1280f;
			if( mScreenScaleHeight > mScreenScaleWidth )
			{
				mScreenScale = mScreenScaleHeight;
			}
			else
			{
				mScreenScale = mScreenScaleWidth;
			}
		}
		if( info instanceof ShortcutInfo )
		{
			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
			if( shortcutInfo.appInfo == null )
				return;
			com.iLoong.launcher.data.ApplicationInfo applicationInfo = shortcutInfo.appInfo;
			if( applicationInfo.isNews == true )
			{
				if( lateuse == null )
				{
					Bitmap uselate = ThemeManager.getInstance().getBitmap( "theme/pack_source/new2.png" );
					uselate = Tools.resizeBitmap( uselate , mScreenScale );
					lateuse = new TextureRegion( new BitmapTexture( uselate ) );
				}
				if( lateuse != null )
				{
					batch.draw( lateuse , x , y + height - 11 * mScreenScale - lateuse.getRegionHeight() );
				}
			}
		}
	}
	
	public boolean belongShowContainer(
			ShortcutInfo shortcutInfo )
	{
		// Log.v("jbc","belongShowContainer");
		long container_id = shortcutInfo.container;
		if( container_id == LauncherSettings.Favorites.CONTAINER_DESKTOP )
		{
			return true;
		}
		if( container_id == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
		{
			return true;
		}
		if( container_id > 0 && ( container_id != LauncherSettings.Favorites.CONTAINER_APPLIST )// xiatian
																								// add
																								// //for
																								// mainmenu
																								// sort
																								// by
																								// user
		)
		{// 文件�?
			return false;
		}
		if( container_id == shortcutInfo.NO_ID || container_id == LauncherSettings.Favorites.CONTAINER_APPLIST// xiatian
																												// add
																												// //for
																												// mainmenu
																												// sort
																												// by
																												// user
		)
		{
			if( DefaultLayout.show_missed_call_sms_in_appList )
			{
				return true;
			}
			return false;
		}
		return false;
	}
	
	public void setInShowFolder(
			boolean canshow )
	{
		inShowFolder = canshow;
	}
	
	public boolean getInShowFolder()
	{
		return inShowFolder;
	}
	
	public boolean canShowPop(
			ShortcutInfo shortcutInfo )
	{
		if( belongShowContainer( shortcutInfo ) || ( getInShowFolder() && isShowFolderIconCanShowPop( shortcutInfo )// xiatian
																													// add
																													// //for
																													// mainmenu
																													// sort
																													// by
																													// user
		) )
			return true;
		needShowPaoPao = false;
		return false;
	}
	
	public void setItemInfo(
			ItemInfo info )
	{
		this.info = info;
		if( info instanceof ShortcutInfo )
		{
			ShortcutInfo shortcutInfo = (ShortcutInfo)info;
			if( ( shortcutInfo.flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
			{
				Intent intent = shortcutInfo.intent;
				if( intent != null && intent.getComponent() != null && intent.getComponent().getPackageName().equals( RR.getPackageName() ) )
					canUninstall = false;
				else
					canUninstall = true;
			}
			else if( ( shortcutInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) == 0 )
			{
				Intent intent = shortcutInfo.intent;
				if( intent != null && intent.getComponent() != null && intent.getComponent().getPackageName().equals( RR.getPackageName() ) )
					canUninstall = false;
				else
					canUninstall = true;
			}
			else
				canUninstall = false;
			Intent intent = shortcutInfo.intent;
			if( canShowPop( shortcutInfo ) )
				setting();
			if( intent != null && intent.getComponent() != null )
			{
				isHide = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "HIDE:" + intent.getComponent().toString() , false );
			}
		}
	}
	
	@Override
	public Icon3D clone()
	{
		if( this.region.getTexture() == null )
		{
			Log.e( "iLoong" , " icon:" + this + " region is null!!" );
			return null;
		}
		Icon3D icon;
		if( this instanceof RecentApp )
		{
			icon = new RecentApp( this.name , this.region );
			icon.mLockIcon = ( (RecentApp)this ).mLockIcon;
			icon.isLocked = this.isLocked;
			icon.recentOrgPos = this.recentOrgPos;
		}
		else
		{
			icon = new Icon3D( this.name , this.region );
		}
		if( this.background9 != null )
		{
			icon.setBackgroud( this.background9 );
		}
		// this.info.itemType=
		// LauncherSettings.BaseLauncherColumns.ITEM_TYPE_SHORTCUT;
		if( this.info != null )
		{
			icon.setItemInfo( new ShortcutInfo( (ShortcutInfo)this.info ) );
		}
		icon.needShowPaoPao = this.needShowPaoPao;
		// teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			// 该标志也需要clone
			icon.canUninstall = this.canUninstall;
			icon.uninstall = this.uninstall;
			icon.hide = this.hide;
			icon.isHide = this.isHide;
		}
		// teapotXu add end for Folder in Mainmenu
		return icon;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		// if(type == TweenCallback.COMPLETE){
		// isSelected = false;
		// }
		if( tmpView != null && source == tmpView.getTween() && type == TweenCallback.COMPLETE )
		{
			tmpView.remove();
			tmpView.stopTween();
			tmpView = null;
		}
		// teapotXu add start for FolderMainMenu && shake
		if( DefaultLayout.mainmenu_folder_function == true && source == shakeAnimation && type == TweenCallback.COMPLETE )
		{
			//xiatian start	//for mainmenu sort by user
			//xiatian del start
			//			float r;
			//			if (this.rotation == SHAKE_ROTATE_ANGLE) {
			////			this.setOrigin(point1.x, point1.y);
			//				r = 0f;
			//			} else {
			////			this.setOrigin(point2.x, point2.y);
			//				r = SHAKE_ROTATE_ANGLE;
			//			}
			//			shakeAnimation = this.startTween(View3DTweenAccessor.ROTATION,Linear.INOUT, shakeTime, r, 0, 0).setCallback(this);			
			//xiatian del end
			//xiatian add start
			if( true )//( DefaultLayout.mainmenu_sort_by_user_fun )
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
				float mShakeTime = MathUtils.random( FIRST_SHAKE_TIME_MIN , FIRST_SHAKE_TIME_MAX );
				shakeAnimation = this.startTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , mShakeTime , scale , scale , 0 ).setUserData( data ).setCallback( this );
			}
			else
			{
				float r;
				//				
				if( this.rotation == SHAKE_ROTATE_ANGLE )
				{
					//				this.setOrigin(point1.x, point1.y);
					r = 0f;
				}
				else
				{
					//				this.setOrigin(point2.x, point2.y);
					r = SHAKE_ROTATE_ANGLE;
				}
				shakeAnimation = this.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , shakeTime , r , 0 , 0 ).setCallback( this );
			}
			//xiatian add end
			//xiatian end
		}
		// teapotXu add end for FolderMainMenu && shake
	}
	
	public void selected()
	{
		//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "DoubleClickIcon" );
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "DoubleRefersToPageEdit" );
		isSelected = true;
		viewParent.onCtrlEvent( this , MSG_ICON_SELECTED );
	}
	
	public void cancelSelected()
	{
		// selectedImage.setScale(1, 1);
		// selectedImage.startTween(View3DTweenAccessor.SCALE_XY, Linear.INOUT,
		// 0.3f, 0.3f, 0.3f, 0).setCallback(this);
		if( !isSelected )
			return;
		isSelected = false;
		if( viewParent == null )
		{
			// Log.d("launcher", "error");
		}
		else
			viewParent.onCtrlEvent( this , MSG_ICON_UNSELECTED );
	}
	
	public void hideSelectedIcon()
	{
		// selectedImage.setScale(1, 1);
		// selectedImage.startTween(View3DTweenAccessor.SCALE_XY, Linear.INOUT,
		// 0.3f, 0.3f, 0.3f, 0).setCallback(this);
		isSelected = false;
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void showUninstall()
	{
		uninstall = true;
		hide = false;
		selected = false; //teapotXu add start for icon3D's double-click optimization
		cancelSelected();
		// teapotXu add start for FolderMainMenu && shake
		if( DefaultLayout.mainmenu_folder_function )
		{
			// widget icon donot need shaking
			if( !( this instanceof Widget3DVirtual ) )
			{
				if( ( this.getParent() instanceof FolderIcon3D ) && ( inShowFolder == false ) )
				{
					shake( false );
				}
				else
				{
					shake( true );
				}
			}
		}
		// teapotXu add end for FolderMainMenu && shake
	}
	
	public void showHide()
	{
		if( isApplistVirtualIcon() == true )
		{
			return;
		}
		hide = true;
		uninstall = false;
		selected = false; // teapotXu add
		cancelSelected();
	}
	
	// teapotXu add start for icon3D's double-click optimization
	public void setSelectMode(
			boolean is_select_mode )
	{
		// if (isApplistVirtualIcon() == true) {
		// return;
		// }
		if( is_select_mode == true )
		{
			selected = true;
			hide = false;
			uninstall = false;
		}
		else
		{
			selected = false;
			hide = false;
			uninstall = false;
		}
	}
	
	// teapotXu add end
	public void clearState()
	{
		cancelSelected();
		uninstall = false;
		hide = false;
		// teapotXu add start for icon3D's double-click optimization
		selected = false;
		// teapotXu add end
		// teapotXu add start for FolderMainMenu && shake
		if( DefaultLayout.mainmenu_folder_function )
		{
			// widget icon donot need shaking
			if( !( this instanceof Widget3DVirtual ) )
			{
				reset_shake_status();
			}
		}
		// teapotXu add end for FolderMainMenu && shake
	}
	
	// teapotXu add start for Folder in Mainmenu
	public void setUninstallStatus(
			boolean is_uninstall )
	{
		uninstall = is_uninstall;
	}
	
	public boolean getUninstallStatus()
	{
		return uninstall;
	}
	
	public boolean getHideStatus()
	{
		return hide;
	}
	
	// add for shake
	private boolean isShake = false;
	private Vector2 point1 = new Vector2();
	private Vector2 point2 = new Vector2();
	private Tween shakeAnimation = null;
	private final static float SHAKE_ROTATE_ANGLE = 5.0f;
	private float shakeTime = 0.25f;
	private final static float SHAKE_SCALE_MAX = 1.0f;
	private final static float SHAKE_SCALE_MIN = 0.98f;
	private final static float FIRST_SHAKE_TIME_MAX = 0.25f;
	private final static float FIRST_SHAKE_TIME_MIN = 0.1f;
	
	public void reset_shake_status()
	{
		isShake = false;
		if( shakeAnimation != null )
		{
			shakeAnimation.free();
		}
		shakeAnimation = null;
		//xiatian start	//for mainmenu sort by user
		//		this.setRotation(0);//xiatian del
		//xiatian add start
		if( true )//( DefaultLayout.mainmenu_sort_by_user_fun )
		{
			if( this.getParent() instanceof FolderIcon3D )
			{
				this.setScale( R3D.folder_icon_scale_factor / 100f , R3D.folder_icon_scale_factor / 100f );
			}
			else
			{
				this.setScale( 1f , 1f );
			}
		}
		else
		{
			this.setRotation( 0 );
		}
		//xiatian add end
		//xiatian end
	}
	
	public void shake(
			boolean shake )
	{
		if( isShake == shake )
			return;
		if( !shake )
		{
			//Log.e(TAG, "icon:" + this + " shake:" + shake);
		}
		isShake = shake;
		if( shake )
		{
			//			Log.e("cooee", "shake:" + shake + " isShake:" + isShake + " icon:" + this);
			//			Log.e("cooee", "shake: shakeAnimation = " + shakeAnimation);
			if( shakeAnimation != null )
			{
				shakeAnimation.free();
			}
			//xiatian start	//for mainmenu sort by user
			//xiatian del start
			//			Double d = Math.random();
			//			shakeTime = (float) (d * 0.03f + 0.09f);
			//			point1.x = width / 4 + (float) (d * (width / 2));
			//			point1.y = height / 4 + (float) (d * height / 2);
			//			d = Math.random();
			//			point2.x = width / 4 + (float) (d * (width / 2));
			//			point2.y = height / 4 + (float) (d * height / 2);
			//			//this.setOrigin(point1.x, point1.y);			
			//			shakeAnimation = this.startTween(View3DTweenAccessor.ROTATION,Linear.INOUT, shakeTime, SHAKE_ROTATE_ANGLE, 0, 0).setCallback(this);			
			//xiatian del end
			//xiatian add start
			if( true )//( DefaultLayout.mainmenu_sort_by_user_fun )
			{
				float scale = SHAKE_SCALE_MIN;
				int data = 0;
				float mShakeTime = MathUtils.random( FIRST_SHAKE_TIME_MIN , FIRST_SHAKE_TIME_MAX );
				shakeAnimation = this.startTween( View3DTweenAccessor.SCALE_XY , Linear.INOUT , mShakeTime , scale , scale , 0 ).setUserData( data ).setCallback( this );
			}
			else
			{
				Double d = Math.random();
				shakeTime = (float)( d * 0.03f + 0.09f );
				point1.x = width / 4 + (float)( d * ( width / 2 ) );
				point1.y = height / 4 + (float)( d * height / 2 );
				d = Math.random();
				point2.x = width / 4 + (float)( d * ( width / 2 ) );
				point2.y = height / 4 + (float)( d * height / 2 );
				//				this.setOrigin(point1.x, point1.y);
				shakeAnimation = this.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , shakeTime , SHAKE_ROTATE_ANGLE , 0 , 0 ).setCallback( this );
			}
			//xiatian add end
			//xiatian end
		}
		else
		{
			// this.stopTween();
			if( shakeAnimation != null )
			{
				shakeAnimation.free();
			}
			shakeAnimation = null;
			//this.setOrigin(width / 2, height / 2);
			//xiatian start	//for mainmenu sort by user
			//			this.setRotation(0);//xiatian del
			//xiatian add start
			if( this.getParent() instanceof FolderIcon3D )
			{
				this.setScale( R3D.folder_icon_scale_factor / 100f , R3D.folder_icon_scale_factor / 100f );
			}
			else
			{
				this.setScale( 1f , 1f );
			}
			//xiatian add end
			//xiatian end
		}
	}
	
	// teapotXu add end for Folder in Mainmenu
	public boolean uninstall()
	{
		if( !this.canUninstall )
			return false;
		ShortcutInfo temp = (ShortcutInfo)this.getItemInfo();
		ComponentName name = temp.intent.getComponent();
		if( !( name == null ) )
		{
			String packageName = name.getPackageName();
			if( packageName != null && !packageName.equals( "" ) )
			{
				Uri packageURI = Uri.parse( "package:" + packageName );
				Intent intent = new Intent( Intent.ACTION_DELETE , packageURI );
				// 执行卸载程序
				iLoongLauncher.getInstance().startActivity( intent );
				return true;
			}
		}
		if( isApplistVirtualIcon() == true )
		{
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		if( !hide && !uninstall )
		{
			Desktop3D.doubleClick = true;
			if( DefaultLayout.enable_icon_effect )
			{
				if( tmpView == null )
				{
					tmpView = this.clone();
					if( tmpView != null )
					{
						this.toAbsoluteCoords( point );
						tmpView.x = point.x;
						tmpView.y = point.y;
						tmpView.startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.3f , 0 , 0 , 0 );
						tmpView.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 1.5f , 1.5f , 0 ).setCallback( this );
					}
				}
			}
			else
			{
				if( !( this instanceof RecentApp ) && iLoongLauncher.getInstance().isShowNews )
					this.color.a = 0.6f;
				// teapotXu add start
				this.addTouchedView();
				// teapotXu add end
			}
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					ParticleManager.disableClickIcon = false;
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		if( !Workspace3D.isRecentAppVisible() )
			this.color.a = 1f;
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean onTouchUp(
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
				if( !particleCanRender && !ParticleManager.disableClickIcon )
				{
					Vector2 point = new Vector2();
					this.toAbsoluteCoords( point );
					float iconHeight = Utils3D.getIconBmpHeight();
					float positionX = point.x + this.width / 2;
					float positionY = point.y + ( this.height - iconHeight ) + iconHeight / 2;
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON , positionX , positionY ).setCallback( this );
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		if( !Workspace3D.isRecentAppVisible() )
			this.color.a = 1f;
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean handleActionWhenTouchLeave()
	{
		// do something when touch area leave this icon
		this.color.a = 1.0f;
		this.removeTouchedView();
		return true;
	}
	
	public static Bitmap getIconBg()
	{
		if( iconBgs == null )
			return null;
		if( iconBgs.length == 0 )
			return null;
		return iconBgs[(int)( Math.random() * iconBgs.length )];
	}
	
	public static Bitmap getMask()
	{
		return mask;
	}
	
	public static Bitmap getCover()
	{
		return cover;
	}
	
	public static void setCover(
			Bitmap bitmap )
	{
		cover = bitmap;
	}
	
	@Override
	public ItemInfo getItemInfo()
	{
		// TODO Auto-generated method stub
		return info;
	}
	
	public int getClingX()
	{
		point.x = 0;
		point.y = 0;
		this.toAbsolute( point );
		return (int)( point.x + width / 2 );
	}
	
	public int getClingY()
	{
		point.x = 0;
		point.y = 0;
		this.toAbsolute( point );
		return (int)( point.y + height / 2 + 5 );
	}
	
	public int getClingR()
	{
		return (int)( width / 2 );
	}
	
	public void setShowPop(
			boolean show )
	{
		this.needShowPaoPao = show;
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( DefaultLayout.hotseat_title_disable_click )
		{
			if( this.info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
			{
				Group.toChildCoordinates( this , x , y , point );
				return( ( point.x >= 0 && point.x < width ) && ( point.y >= height - Utilities.sIconTextureHeight && point.y < height ) );
			}
		}
		return super.pointerInParent( x , y );
	}
	
	public boolean isApplistVirtualIcon()
	{
		ShortcutInfo mShortcutInfo = (ShortcutInfo)this.getItemInfo();
		if( mShortcutInfo.appInfo != null && mShortcutInfo.appInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// xiatian add start //DownloadIcon
	public float getDownloadIconX()
	{
		float ret = 0f;
		float scale = 1f;
		if( ( this.getItemInfo() != null ) && ( this.getItemInfo().container == -101 ) && ( this.getItemInfo().angle == 0 ) && DefaultLayout.enable_hotseat_rolling )
		{// mainGroup
			scale = this.height / iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar().getMainGroup().height;
		}
		ret = this.x + ( ( this.width - scale * Utilities.sIconTextureWidth ) / 2 ) + scale * Utilities.sIconTextureWidth - dynamicMenuDownloadIconWidth + 2;
		return ret;
	}
	
	public float getDownloadIconY()
	{
		float ret = 0f;
		float bmpHeight = Utilities.sIconTextureHeight;
		float space_height = 0;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
		}
		else
		{
			space_height = bmpHeight / 10;
		}
		ret = this.y + ( this.height - Utils3D.getIconBmpHeight() + space_height ) - 2;
		if( ( this.getItemInfo() != null ) && ( this.getItemInfo().container == -101 ) && ( this.getItemInfo().angle == 0 ) && DefaultLayout.enable_hotseat_rolling )
		{// mainGroup
			float scale = this.height / iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar().getMainGroup().height;
			ret = this.y + ( this.height - scale * bmpHeight - scale * space_height ) / 2 - 3;
		}
		return ret;
	}
	
	// xiatian add end
	/************************ added by zhenNan.ye begin *************************/
	@Override
	public void onParticleCallback(
			int type )
	{
		// TODO Auto-generated method stub
		if( type == ParticleCallback.END )
		{
			if( !isSelected && !doubleClickFlag )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_CLICK_ICON );
				if( true == particle_need_launch_app )
				{
					viewParent.onCtrlEvent( this , MSG_ICON_CLICK );
					particle_need_launch_app = false;
				}
			}
			else
			{
				doubleClickFlag = false;
			}
		}
	}
	
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
	// xiatian add start //EffectPreview
	private boolean isCurEffect()
	{
		ShortcutInfo mShortcutInfo = (ShortcutInfo)this.getItemInfo();
		if( ( mShortcutInfo != null ) && ( mShortcutInfo.intent != null ) && ( mShortcutInfo.intent.getAction() != null ) && ( mShortcutInfo.intent.getAction().equals( "EffectPreview3D" ) ) )
		{
			int type = mShortcutInfo.intent.getIntExtra( EffectPreview3D.ICON_EXTRA_TYPE , -1 );
			int index = mShortcutInfo.intent.getIntExtra( EffectPreview3D.ICON_EXTRA_INDEX , -1 );
			if( ( ( type == EffectPreview3D.TYPE_WORKSPACE ) && ( index == SetupMenuActions.getInstance().getStringToIntger( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) ) ) || ( ( type == EffectPreview3D.TYPE_APPLIST ) && ( index == SetupMenuActions
					.getInstance().getStringToIntger( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) ) ) )
			{
				return true;
			}
		}
		return false;
	}
	
	// xiatian add end
	// xiatian add start //for mainmenu sort by user
	public boolean isShowFolderIconCanShowPop(
			ShortcutInfo shortcutInfo )
	{
		if( !inShowFolder )
		{
			return false;
		}
		long container_id = shortcutInfo.container;
		if( container_id > LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
			if( DefaultLayout.show_missed_call_sms_in_appList )
			{
				return true;
			}
			return false;
		}
		return true;
	}
	
	public boolean isPointInBreakUpFolderIconRect(
			float x ,
			float y )
	{
		boolean ret = false;
		float rect_left = this.width - stateIconWidth;
		float rect_bottom = this.height - stateIconHeight;
		float rect_top = rect_bottom + stateIconHeight;
		float rect_right = rect_left + stateIconWidth;
		if( ( ( x >= rect_left ) && ( x <= rect_right ) ) && ( ( y >= rect_bottom ) && ( y <= rect_top ) ) )
		{
			ret = true;
		}
		return ret;
	}
	
	// xiatian add end
	public Vector2 recentOrgPos;
	public TextureRegion mLockIcon;
	public boolean isLocked = false;
	
	public void drawRecentapp(
			SpriteBatch batch ,
			float x ,
			float y ,
			float width ,
			float height )
	{
		if( !( this instanceof RecentApp ) )
			return;
		if( scaleX != 1 || scaleY != 1 )
		{
			return;
		}
		if( isLocked )
		{
			if( mLockIcon == null )
			{
				generateLockIcon();
			}
			batch.draw( mLockIcon , x + width - mLockIcon.getRegionWidth() , y + height - mLockIcon.getRegionHeight() , mLockIcon.getRegionWidth() , mLockIcon.getRegionHeight() );
		}
	}
	
	public void generateLockIcon()
	{
		Bitmap lock = ThemeManager.getInstance().getBitmap( "theme/recentApplications/lock.png" );
		lock = Tools.resizeBitmap( lock , RecentAppHolder.mScaleFactor );
		mLockIcon = new TextureRegion( new BitmapTexture( lock ) );
	}
	
	/************************ added by diaosixu begin ***************************/
	public void setAppRemind(
			boolean needRemind )
	{
		isNeedToRemind = needRemind;
	}
	/************************ added by diaosixu end ***************************/
}

class IconToTexture3D extends BitmapTexture
{
	
	public IconToTexture3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg )
	{
		super( Utils3D.IconToPixmap3D( b , title , icn_bg , title_bg ) , true );
		// setFilter(TextureFilter.Linear,TextureFilter.Linear);
		setFilter( R3D.filter , R3D.Magfilter );
	}
	
	public IconToTexture3D(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			boolean ifshadow )
	{
		super( Utils3D.IconToPixmap3D( b , title , icn_bg , title_bg , true , ifshadow ) , true );
		setFilter( R3D.filter , R3D.Magfilter );
	}
}
