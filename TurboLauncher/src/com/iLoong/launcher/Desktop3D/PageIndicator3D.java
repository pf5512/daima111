package com.iLoong.launcher.Desktop3D;


import android.graphics.Bitmap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.camera.CameraManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.recent.RecentAppHolder;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PageIndicator3D extends View3D implements PageScrollListener
{
	
	private View3D scrollAnimView;
	private View3D indicatorAnimView;
	private int currentPage = -1;
	private int targetPage;
	private int pageNum;
	private float degree;
	private Tween myTween;
	private Tween s4indicatorClickTween;
	private static final int hide_indicator = 0;
	private static final int show_indicator = 1;
	private Tween indicatorTween;
	private float indicatorAlpha = 0;
	private static float INDICATOR_FADE_TWEEN_DURATION = 1.0f;
	// private TextureRegion indicatorBg;
	public TextureRegion unselectedIndicator = null;
	public TextureRegion selectedIndicator = null;
	private TextureRegion unselectedIndicatorMusic = null;
	private TextureRegion selectedIndicatorMusic = null;
	private TextureRegion nineRegion = null;
	private NinePatch nineIndicator = null;
	public TextureRegion[] indicatorNumber;
	public TextureRegion bgIndicator_s4Region = null;
	private NinePatch bgIndicator_s4 = null;
	public TextureRegion scrollIndicator_s4 = null;
	public TextureRegion indicatorNumberBg_s4 = null;
	public TextureRegion[] indicatorNumber_s4;
	private TextureRegion unselectedIndicatorCamera = null;
	private TextureRegion selectedIndicatorCamera = null;
	// private NinePatch bg;
	// public static float pageIndicatorWidth;
	// public static float pageIndicatorHeight;
	// private float indicatorWidth;
	// private float indicatorHeight;
	// private float originX;
	// private float originY;
	// private float radius;
	// private float indicatorStartDegree;
	// private float indicatorEndDegree;
	public int clingR;
	public int clingX;
	public int clingY;
	// private boolean hasDown = false;
	private long downTime = 0;
	private float downX = 0;
	private float downY = 0;
	// public double pointPage = -1;
	public int pageMode = MODE_NORMAL;
	public static boolean animating = false;
	public static final float NORMAL_SCALE = 0.6f;
	public static final float ACTIVATE_SCALE = 0.75f;
	public static final int CLICK_TIME = 500;
	public static final int CLICK_MOVE = 40;
	public static final int PAGE_INDICATOR_CLICK = 0;
	public static final int PAGE_INDICATOR_UP = 1;
	public static final int PAGE_INDICATOR_DROP_OVER = 2;
	public static final int PAGE_INDICATOR_SCROLL = 3;
	// public static final int PAGE_INDICATOR_CLICK = 4;
	public static final int MODE_ACTIVATE = 0;
	public static final int MODE_NORMAL = 1;
	public static final int PAGE_MODE_EDIT = 2;
	public float indicatorSize = (float)R3D.page_indicator_size;
	public int indicatorFocusW = R3D.page_indicator_focus_w;
	public int indicatorNormalW = R3D.page_indicator_normal_w;
	public int indicatorFocusH = R3D.page_indicator_focus_h;
	public int indicatorNormalH = R3D.page_indicator_normal_h;
	public int indicatorStyle = R3D.page_indicator_style;
	public int indicatorTotalSize = R3D.page_indicator_total_size;
	public int s4_page_indicator_bg_height = R3D.s4_page_indicator_bg_height;
	public int s4_page_indicator_scroll_width = R3D.s4_page_indicator_scroll_width;
	public int s4_page_indicator_scroll_height = R3D.s4_page_indicator_scroll_height;
	public int s4_page_indicator_number_bg_size = R3D.s4_page_indicator_number_bg_size;
	public int s4_page_indicator_number_w = s4_page_indicator_number_bg_size / 2;
	public int s4_page_indicator_number_x_offset = R3D.s4_page_indicator_number_x_offset;
	public static float scroll_degree = 0;
	public static final int INDICATOR_STYLE_ANDROID4 = 0;
	public static final int INDICATOR_STYLE_S3 = 1;
	public static final int INDICATOR_STYLE_S2 = 2;
	// public static final int INDICATOR_STYLE_S4 = 3;
	private EffectPreview3D mWorkspaceEffectPreview; // xiatian add //EffectPreview
	private static PageIndicator3D pageIndicator = null;
	
	public PageIndicator3D(
			String name )
	{
		super( name );
		pageIndicator = this;
		if( indicatorStyle == INDICATOR_STYLE_S3 )
		{
			if( nineIndicator == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
				Texture t = new BitmapTexture( bmIndicator );
				// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				// TextureRegion region =
				// R3D.findRegion("default_indicator_current");
				// region.getTexture().setFilter(TextureFilter.Linear,
				// TextureFilter.Linear);
				nineRegion = new TextureRegion( t );
				nineIndicator = new NinePatch( nineRegion , 6 , 6 , 6 , 6 );
				bmIndicator.recycle();
				// bmIndicator.recycle();
				// nineIndicator = new
				// NinePatch(R3D.getTextureRegion("default_indicator_current"),6,6,6,6);
			}
		}
		else if( indicatorStyle == INDICATOR_STYLE_S2 )
		{
			if( selectedIndicator == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_s2.png" );
				Texture t = new BitmapTexture( bmIndicator );
				selectedIndicator = new TextureRegion( t );
				bmIndicator.recycle();
				indicatorNumber = new TextureRegion[9];
				for( int i = 0 ; i < 9 ; i++ )
				{
					bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_" + ( i + 1 ) + ".png" );
					;
					t = new BitmapTexture( bmIndicator );
					indicatorNumber[i] = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
		}
		else
		{
			if( unselectedIndicator == null )
			{
				if( DefaultLayout.isScaleBitmap )
				{
					unselectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator.png" , indicatorNormalW , indicatorNormalH );
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator.png" );
					Texture t = new BitmapTexture( bmIndicator );
					unselectedIndicator = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
			if( selectedIndicator == null )
			{
				if( DefaultLayout.isScaleBitmap )
				{
					selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , indicatorFocusW , indicatorFocusH );
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
					Texture t = new BitmapTexture( bmIndicator );
					selectedIndicator = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
			if( DefaultLayout.show_music_page || DefaultLayout.show_music_page_enable_config )
			{
				if( unselectedIndicatorMusic == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/mediaview/music/indicator_music.png" );
					Texture t = new BitmapTexture( bmIndicator );
					unselectedIndicatorMusic = new TextureRegion( t );
					bmIndicator.recycle();
				}
				if( selectedIndicatorMusic == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/mediaview/music/indicator_music_focus.png" );
					Texture t = new BitmapTexture( bmIndicator );
					selectedIndicatorMusic = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
			//xujin camera 图标
			if( DefaultLayout.enable_camera || DefaultLayout.show_camera_page_enable_config )
			{
				if( unselectedIndicatorCamera == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/mediaview/camera/indicator_camera.png" );
					Texture t = new BitmapTexture( bmIndicator );
					unselectedIndicatorCamera = new TextureRegion( t );
					bmIndicator.recycle();
				}
				if( selectedIndicatorCamera == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/mediaview/camera/indicator_camera_focus.png" );
					Texture t = new BitmapTexture( bmIndicator );
					selectedIndicatorCamera = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
		}
		if( DefaultLayout.enable_DesktopIndicatorScroll )
		{
			// scroll indicator
			if( bgIndicator_s4 == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/bg_indicator_s4.png" );
				Texture t = new BitmapTexture( bmIndicator );
				bgIndicator_s4Region = new TextureRegion( t );
				bgIndicator_s4 = new NinePatch( bgIndicator_s4Region , 7 , 7 , 0 , 0 );
				bmIndicator.recycle();
				if( DefaultLayout.isScaleBitmap )
				{
					scrollIndicator_s4 = Tools.getTextureByPicName( "theme/pack_source/scroll_indicator_s4.png" , s4_page_indicator_scroll_width , s4_page_indicator_scroll_height );
				}
				else
				{
					bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/scroll_indicator_s4.png" );
					t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					scrollIndicator_s4 = new TextureRegion( t );
					bmIndicator.recycle();
				}
				if( DefaultLayout.pageIndicator_scroll_num_shown )
				{
					if( DefaultLayout.isScaleBitmap )
					{
						indicatorNumberBg_s4 = Tools.getTextureByPicName( "theme/pack_source/indicatorNumberBg_s4.png" , s4_page_indicator_number_bg_size , s4_page_indicator_number_bg_size );
					}
					else
					{
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicatorNumberBg_s4.png" );
						t = new BitmapTexture( bmIndicator );
						t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
						indicatorNumberBg_s4 = new TextureRegion( t );
						bmIndicator.recycle();
					}
					indicatorNumber_s4 = new TextureRegion[10];
					for( int i = 0 ; i < 10 ; i++ )
					{
						if( DefaultLayout.isScaleBitmap )
						{
							indicatorNumber_s4[i] = Tools.getTextureByPicName( "theme/pack_source/indicator_num_s4_" + ( i ) + ".png" , s4_page_indicator_number_w , s4_page_indicator_number_bg_size );
						}
						else
						{
							bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_s4_" + ( i ) + ".png" );
							t = new BitmapTexture( bmIndicator );
							t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
							indicatorNumber_s4[i] = new TextureRegion( t );
							bmIndicator.recycle();
						}
					}
				}
			}
		}
		setSize( R3D.getInteger( "page_indicator_width" ) == 0 ? Utils3D.getScreenWidth() : R3D.getInteger( "page_indicator_width" ) , R3D.getInteger( "page_indicator_height" ) );
		setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , R3D.page_indicator_y );
		indicatorAlpha = 0;
		clingX = Utils3D.getScreenWidth() / 2;
		clingY = (int)( y + height / 2 );
		clingR = (int)( indicatorSize * 2 );
	}
	
	public void onThemeChanged()
	{
		// style change,need dispose!!!
		indicatorStyle = R3D.page_indicator_style;
		indicatorSize = (float)R3D.page_indicator_size;
		indicatorFocusW = R3D.page_indicator_focus_w;
		indicatorNormalW = R3D.page_indicator_normal_w;
		indicatorFocusH = R3D.page_indicator_focus_h;
		indicatorNormalH = R3D.page_indicator_normal_h;
		indicatorTotalSize = R3D.page_indicator_total_size;
		s4_page_indicator_bg_height = R3D.s4_page_indicator_bg_height;
		s4_page_indicator_scroll_width = R3D.s4_page_indicator_scroll_width;
		s4_page_indicator_scroll_height = R3D.s4_page_indicator_scroll_height;
		s4_page_indicator_number_bg_size = R3D.s4_page_indicator_number_bg_size;
		s4_page_indicator_number_w = s4_page_indicator_number_bg_size / 2;
		s4_page_indicator_number_x_offset = R3D.s4_page_indicator_number_x_offset;
		if( indicatorStyle == INDICATOR_STYLE_S3 )
		{
			if( nineIndicator == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
				Texture t = new BitmapTexture( bmIndicator );
				t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				nineRegion = new TextureRegion( t );
				nineIndicator = new NinePatch( nineRegion , 6 , 6 , 6 , 6 );
				bmIndicator.recycle();
			}
			else
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
				( (BitmapTexture)nineRegion.getTexture() ).changeBitmap( bmIndicator , true );
				nineIndicator = new NinePatch( nineRegion , 6 , 6 , 6 , 6 );
			}
		}
		else if( indicatorStyle == INDICATOR_STYLE_S2 )
		{
			if( selectedIndicator == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_s2.png" );
				Texture t = new BitmapTexture( bmIndicator );
				t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				selectedIndicator = new TextureRegion( t );
				bmIndicator.recycle();
				indicatorNumber = new TextureRegion[9];
				for( int i = 0 ; i < 9 ; i++ )
				{
					bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_" + ( i + 1 ) + ".png" );
					t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					indicatorNumber[i] = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
			else
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_s2.png" );
				BitmapTexture t = (BitmapTexture)selectedIndicator.getTexture();
				t.changeBitmap( bmIndicator , true );
				t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				for( int i = 0 ; i < 9 ; i++ )
				{
					bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_" + ( i + 1 ) + ".png" );
					t = (BitmapTexture)indicatorNumber[i].getTexture();
					t.changeBitmap( bmIndicator , true );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				}
			}
		}
		else
		{
			if( unselectedIndicator == null )
			{
				if( DefaultLayout.isScaleBitmap )
				{
					unselectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator.png" , indicatorNormalW , indicatorNormalH );
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator.png" );
					Texture t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					unselectedIndicator = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
			else
			{
				if( DefaultLayout.isScaleBitmap )
				{
					if( unselectedIndicator.getTexture() != null )
					{
						unselectedIndicator.getTexture().dispose();
						unselectedIndicator = null;
					}
					unselectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator.png" , indicatorNormalW , indicatorNormalH );
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator.png" );
					BitmapTexture t = (BitmapTexture)unselectedIndicator.getTexture();
					t.changeBitmap( bmIndicator , true );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				}
			}
			if( selectedIndicator == null )
			{
				if( DefaultLayout.isScaleBitmap )
				{
					selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , indicatorFocusW , indicatorFocusH );
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
					Texture t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					selectedIndicator = new TextureRegion( t );
					bmIndicator.recycle();
				}
			}
			else
			{
				if( DefaultLayout.isScaleBitmap )
				{
					if( selectedIndicator.getTexture() != null )
					{
						selectedIndicator.getTexture().dispose();
						selectedIndicator = null;
					}
					selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , indicatorFocusW , indicatorFocusH );
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
					BitmapTexture t = (BitmapTexture)selectedIndicator.getTexture();
					t.changeBitmap( bmIndicator , true );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				}
			}
		}
		if( DefaultLayout.enable_AppListIndicatorScroll )
		{
			if( bgIndicator_s4 == null )
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/bg_indicator_s4.png" );
				Texture t = new BitmapTexture( bmIndicator );
				bgIndicator_s4Region = new TextureRegion( t );
				bgIndicator_s4 = new NinePatch( bgIndicator_s4Region , 7 , 7 , 0 , 0 );
				bmIndicator.recycle();
				if( DefaultLayout.isScaleBitmap )
				{
					if( scrollIndicator_s4 != null && scrollIndicator_s4.getTexture() != null )
					{
						scrollIndicator_s4.getTexture().dispose();
						scrollIndicator_s4 = null;
					}
					scrollIndicator_s4 = Tools.getTextureByPicName( "theme/pack_source/scroll_indicator_s4.png" , s4_page_indicator_scroll_width , s4_page_indicator_scroll_height );
				}
				else
				{
					bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/scroll_indicator_s4.png" );
					t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					scrollIndicator_s4 = new TextureRegion( t );
					bmIndicator.recycle();
				}
				if( DefaultLayout.pageIndicator_scroll_num_shown )
				{
					if( DefaultLayout.isScaleBitmap )
					{
						indicatorNumberBg_s4 = Tools.getTextureByPicName( "theme/pack_source/indicatorNumberBg_s4.png" , s4_page_indicator_number_bg_size , s4_page_indicator_number_bg_size );
					}
					else
					{
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicatorNumberBg_s4.png" );
						t = new BitmapTexture( bmIndicator );
						t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
						indicatorNumberBg_s4 = new TextureRegion( t );
						bmIndicator.recycle();
					}
					indicatorNumber_s4 = new TextureRegion[10];
					for( int i = 0 ; i < 10 ; i++ )
					{
						if( DefaultLayout.isScaleBitmap )
						{
							indicatorNumber_s4[i] = Tools.getTextureByPicName( "theme/pack_source/indicator_num_s4_" + ( i ) + ".png" , s4_page_indicator_number_w , s4_page_indicator_number_bg_size );
						}
						else
						{
							bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_s4_" + ( i ) + ".png" );
							t = new BitmapTexture( bmIndicator );
							t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
							indicatorNumber_s4[i] = new TextureRegion( t );
							bmIndicator.recycle();
						}
					}
				}
			}
			else
			{
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/bg_indicator_s4.png" );
				( (BitmapTexture)bgIndicator_s4Region.getTexture() ).changeBitmap( bmIndicator , true );
				bgIndicator_s4Region.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				bgIndicator_s4 = new NinePatch( bgIndicator_s4Region , 7 , 7 , 0 , 0 );
				bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/scroll_indicator_s4.png" );
				( (BitmapTexture)scrollIndicator_s4.getTexture() ).changeBitmap( bmIndicator , true );
				scrollIndicator_s4.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
				if( DefaultLayout.pageIndicator_scroll_num_shown )
				{
					bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicatorNumberBg_s4.png" );
					( (BitmapTexture)indicatorNumberBg_s4.getTexture() ).changeBitmap( bmIndicator , true );
					indicatorNumberBg_s4.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
					for( int i = 0 ; i < 10 ; i++ )
					{
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_s4_" + ( i ) + ".png" );
						( (BitmapTexture)indicatorNumber_s4[i].getTexture() ).changeBitmap( bmIndicator , true );
						indicatorNumber_s4[i].getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
					}
				}
			}
		}
		setSize( R3D.getInteger( "page_indicator_width" ) == 0 ? Utils3D.getScreenWidth() : R3D.getInteger( "page_indicator_width" ) , R3D.getInteger( "page_indicator_height" ) );
		// setPosition((Utils3D.getScreenWidth() - this.width)/2,
		// R3D.page_indicator_y);
		clingX = Utils3D.getScreenWidth() / 2;
		clingY = (int)( y + height / 2 );
		clingR = (int)( indicatorSize * 2 );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( !Desktop3DListener.bSetHomepageDone )
			return;
		batch.setColor( color.r , color.g , color.b , color.a );
		Color old = batch.getColor();
		float oldA = old.a;
		int size = pageNum;
		float focusWidth = indicatorSize * size;
		int nextPage = currentPage;
		int signDirection = degree >= 0 ? 1 : -1;
		if( degree == 0 )
			nextPage = currentPage;
		else if( degree == 1 )
			nextPage = currentPage == 0 ? size - 1 : currentPage - 1;
		else if( degree == -1 )
			nextPage = currentPage == size - 1 ? 0 : currentPage + 1;
		else if( currentPage == 0 && degree > 0 )
			nextPage = size - 1;
		else if( currentPage == size - 1 && degree < 0 )
			nextPage = 0;
		else
			nextPage = currentPage - (int)( degree + 1.0 * signDirection );
		// Log.v("jbc","eee currentPage="+currentPage+" nextPage="+nextPage+" degree="+degree);
		float normalH , focusH , normalY , focusY;
		float startX = this.x + ( this.width - focusWidth ) / 2.0f;
		if( DefaultLayout.enable_DesktopIndicatorScroll && Root3D.scroll_indicator )
		{
			normalY = this.y + ( this.height - s4_page_indicator_bg_height ) / 2.0f;
			focusY = this.y + ( this.height - s4_page_indicator_scroll_height ) / 2.0f;
			old.a = oldA;
			batch.setColor( old.r , old.g , old.b , old.a );
			bgIndicator_s4.draw( batch , (int)startX , (int)normalY , focusWidth , s4_page_indicator_bg_height );
			startX = startX + indicatorSize / 2 - s4_page_indicator_scroll_width / 2 + scroll_degree * indicatorSize;
			batch.draw( scrollIndicator_s4 , (int)startX , (int)focusY , s4_page_indicator_scroll_width , s4_page_indicator_scroll_height );
			if( DefaultLayout.pageIndicator_scroll_num_shown )
			{
				// batch.draw(indicatorNumber_s4[(int)(scroll_degree+0.5f)],
				// (this.width - s4_page_indicator_number_bg_size)/2.0f,
				// (int)(this.y + s4_page_indicator_number_bg_size*1.5),
				// s4_page_indicator_number_bg_size,
				// s4_page_indicator_number_bg_size);
				batch.draw(
						indicatorNumberBg_s4 ,
						(int)( ( this.width - s4_page_indicator_number_bg_size ) / 2.0f ) ,
						(int)( this.y + s4_page_indicator_number_bg_size * 1.5 ) ,
						s4_page_indicator_number_bg_size ,
						s4_page_indicator_number_bg_size );
				int pageNumber = (int)( scroll_degree + 0.5f ) + 1;
				if( pageNumber < 10 )
				{
					batch.draw(
							indicatorNumber_s4[pageNumber] ,
							(int)( ( this.width - s4_page_indicator_number_w ) / 2.0f ) ,
							(int)( this.y + s4_page_indicator_number_bg_size * 1.5 ) ,
							s4_page_indicator_number_w ,
							s4_page_indicator_number_bg_size );
				}
				else
				{
					int tens_digit = pageNumber / 10;
					int single_digit = pageNumber % 10;
					batch.draw(
							indicatorNumber_s4[tens_digit] ,
							(int)( ( this.width - s4_page_indicator_number_bg_size ) / 2.0f + s4_page_indicator_number_x_offset ) ,
							(int)( this.y + s4_page_indicator_number_bg_size * 1.5 ) ,
							s4_page_indicator_number_w ,
							s4_page_indicator_number_bg_size );
					batch.draw(
							indicatorNumber_s4[single_digit] ,
							(int)( this.width / 2.0f - s4_page_indicator_number_x_offset ) ,
							(int)( this.y + s4_page_indicator_number_bg_size * 1.5 ) ,
							s4_page_indicator_number_w ,
							s4_page_indicator_number_bg_size );
				}
			}
		}
		else
		{
			switch( indicatorStyle )
			{
				case INDICATOR_STYLE_ANDROID4:
					if( selectedIndicator != null && unselectedIndicator != null )
					{
						focusWidth = indicatorTotalSize;
						indicatorSize = focusWidth / size;
						startX = this.x + ( this.width - focusWidth ) / 2.0f;
						// focusH = normalH = indicatorNormalW;
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						focusY = this.y + ( this.height - focusH ) / 2.0f;
						float focus_offset_x = 0.0f;
						old.a = oldA;
						batch.setColor( old.r , old.g , old.b , old.a );
						batch.draw( unselectedIndicator , startX , (int)normalY , focusWidth , normalH );
						focus_offset_x = -indicatorSize * degree;
						old.a = oldA * indicatorAlpha;
						batch.setColor( old.r , old.g , old.b , old.a );
						if( currentPage * indicatorSize + focus_offset_x < 0 )
						{
							batch.draw( selectedIndicator , startX + currentPage * indicatorSize , (int)focusY , indicatorSize + focus_offset_x , focusH );
							batch.draw( selectedIndicator , this.width - startX + focus_offset_x , (int)focusY , -focus_offset_x , focusH );
						}
						else if( startX + currentPage * indicatorSize + focus_offset_x > this.width - startX - indicatorSize )
						{
							batch.draw( selectedIndicator , startX + currentPage * indicatorSize + focus_offset_x , (int)focusY , indicatorSize - focus_offset_x , focusH );
							batch.draw( selectedIndicator , startX , (int)focusY , focus_offset_x , focusH );
						}
						else
						{
							batch.draw( selectedIndicator , startX + currentPage * indicatorSize + focus_offset_x , (int)focusY , indicatorSize , focusH );
						}
					}
					break;
				case INDICATOR_STYLE_S3:
					if( nineIndicator != null )
					{
						// focusH = normalH = indicatorNormalW;
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						focusY = this.y + ( this.height - focusH ) / 2.0f;
						for( int i = 0 ; i < size ; i++ )
						{
							if( i == currentPage )
							{
								old.a = oldA;
								old.a *= 1 - Math.abs( degree * 0.5 );
								batch.setColor( old.r , old.g , old.b , old.a );
								float w = indicatorNormalW + ( 1 - Math.abs( degree ) ) * ( indicatorFocusW - indicatorNormalW );
								float offset_x = ( indicatorSize - w ) / 2.0f;
								nineIndicator.draw( batch , startX + i * indicatorSize + offset_x , focusY , w , focusH );
							}
							else if( i == nextPage )
							{
								old.a = oldA;
								old.a *= Math.abs( degree * 0.5 ) + 0.5;
								batch.setColor( old.r , old.g , old.b , old.a );
								float w = indicatorNormalW + ( Math.abs( degree ) ) * ( indicatorFocusW - indicatorNormalW );
								float offset_x = ( indicatorSize - w ) / 2.0f;
								nineIndicator.draw( batch , startX + i * indicatorSize + offset_x , focusY , w , focusH );
							}
							else
							{
								old.a = oldA * 0.5f;
								batch.setColor( old.r , old.g , old.b , old.a );
								float offset_x = ( indicatorSize - indicatorNormalW ) / 2.0f;
								nineIndicator.draw( batch , startX + i * indicatorSize + offset_x , normalY , indicatorNormalW , normalH );
							}
						}
					}
					break;
				case INDICATOR_STYLE_S2:
					if( selectedIndicator != null )
					{
						// normalH = indicatorNormalW;
						// focusH = indicatorFocusW;
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						old.a = oldA;
						batch.setColor( old.r , old.g , old.b , old.a );
						for( int i = 0 ; i < size ; i++ )
						{
							if( i == currentPage )
							{
								float w = indicatorNormalW + ( 1 - Math.abs( degree ) ) * ( indicatorFocusW - indicatorNormalW );
								float offset_x = ( indicatorSize - w ) / 2.0f;
								focusY = this.y + ( this.height - w ) / 2.0f;
								batch.draw( selectedIndicator , startX + i * indicatorSize + offset_x , focusY , w , w );
								batch.draw( indicatorNumber[i] , startX + i * indicatorSize + offset_x + w / 5 , focusY + w / 5 , w * 3 / 5 , w * 3 / 5 );
							}
							else if( i == nextPage )
							{
								float w = indicatorNormalW + ( Math.abs( degree ) ) * ( indicatorFocusW - indicatorNormalW );
								float offset_x = ( indicatorSize - w ) / 2.0f;
								focusY = this.y + ( this.height - w ) / 2.0f;
								batch.draw( selectedIndicator , startX + i * indicatorSize + offset_x , focusY , w , w );
								batch.draw( indicatorNumber[i] , startX + i * indicatorSize + offset_x + w / 5 , focusY + w / 5 , w * 3 / 5 , w * 3 / 5 );
							}
							else
							{
								float offset_x = ( indicatorSize - indicatorNormalW ) / 2.0f;
								batch.draw( selectedIndicator , startX + i * indicatorSize + offset_x , normalY , indicatorNormalW , normalH );
							}
						}
					}
					break;
				default:
					if( selectedIndicator != null && unselectedIndicator != null )
					{
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						focusY = this.y + ( this.height - focusH ) / 2.0f;
						float normal_offset_x = ( indicatorSize - indicatorNormalW ) / 2.0f;
						float focus_offset_x = ( indicatorSize - indicatorFocusW ) / 2.0f;
						for( int i = 0 ; i < size ; i++ )
						{
							if( i == currentPage )
							{
								old.a = oldA;
								old.a *= Math.abs( degree );
								batch.setColor( old.r , old.g , old.b , old.a );
							}
							else if( i == nextPage )
							{
								old.a = oldA;
								old.a *= 1 - Math.abs( degree );
								batch.setColor( old.r , old.g , old.b , old.a );
							}
							else
							{
								old.a = oldA;
								batch.setColor( old.r , old.g , old.b , old.a );
							}
							//if( DefaultLayout.show_music_page && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && ( ( DefaultLayout.enable_news && i == size - 2 ) || ( !DefaultLayout.enable_news && i == size - 1 ) ) )
							if( DefaultLayout.show_music_page && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && iLoongLauncher.getInstance().d3dListener
									.getWorkspace3D().isMusicView( i ) )
							{
								batch.draw( unselectedIndicatorMusic , startX + i * indicatorSize + normal_offset_x , normalY , indicatorNormalW , normalH );
							}
							else if( DefaultLayout.enable_camera && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && iLoongLauncher.getInstance().d3dListener
									.getWorkspace3D().isCameraView( i ) )
							{
								batch.draw( unselectedIndicatorCamera , startX + i * indicatorSize + normal_offset_x , normalY , indicatorNormalW , normalH );
							}
							else
							{
								batch.draw( unselectedIndicator , startX + i * indicatorSize + normal_offset_x , normalY , indicatorNormalW , normalH );
							}
							if( i == currentPage )
							{
								old.a = oldA;
								old.a *= 1 - Math.abs( degree );
								if( Workspace3D.isRecentAppVisible() )
								{
									batch.setColor( old.r , old.g , old.b , RecentAppHolder.colorAlpha );
								}
								else
									batch.setColor( old.r , old.g , old.b , old.a );
								//if( DefaultLayout.show_music_page && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && ( ( DefaultLayout.enable_news && i == size - 2 ) || ( !DefaultLayout.enable_news && i == size - 1 ) ) )
								if( DefaultLayout.show_music_page && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && iLoongLauncher.getInstance().d3dListener
										.getWorkspace3D().isMusicView( i ) )
								{
									batch.draw( selectedIndicatorMusic , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
								else if( DefaultLayout.enable_camera && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && iLoongLauncher.getInstance().d3dListener
										.getWorkspace3D().isCameraView( i ) )
								{
									batch.draw( selectedIndicatorCamera , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
								else
								{
									batch.draw( selectedIndicator , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
							}
							else if( i == nextPage )
							{
								old.a = oldA;
								old.a *= Math.abs( degree );
								batch.setColor( old.r , old.g , old.b , old.a );
								if( DefaultLayout.show_music_page && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && iLoongLauncher.getInstance().d3dListener
										.getWorkspace3D().isMusicView( i ) )
								{
									batch.draw( selectedIndicatorMusic , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
								else if( DefaultLayout.enable_camera && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode && !Workspace3D.is_longKick && iLoongLauncher.getInstance().d3dListener
										.getWorkspace3D().isCameraView( i ) )
								{
									batch.draw( selectedIndicatorCamera , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
								else
								{
									batch.draw( selectedIndicator , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
							}
						}
					}
					break;
			}
		}
		old.a = oldA;
		batch.setColor( old.r , old.g , old.b , old.a );
		if( DefaultLayout.enable_new_particle )
		{
			newDrawParticle( batch );
		}
	}
	
	// @Override
	// public void applyTransformChild(SpriteBatch batch) {
	//
	// }
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( Workspace3D.mCheckScrollDirection )
		{
			return true;
		}
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) )
		{
			return true;
		}
		// xiatian add end
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		}
		// Log.v("jbc", "eee scroll degree="+degree);
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( /* degree != 0 || */!DefaultLayout.click_indicator_enter_pageselect )
			return false;
		if( DefaultLayout.enable_DesktopIndicatorScroll )
		{
			// teapotXu add start for optimize the hotseat scroll
			if( Root3D.scroll_indicator == false && DefaultLayout.optimize_hotseat_scroll_back )
			{
				// 当pagIndicator 已经在滑动时，将不再强制响应为hotSeatScroll
				if( ( Math.abs( deltaX ) < Math.abs( deltaY ) && Math.abs( deltaX ) < R3D.getInteger( "scroll_up_and_down_min_delta_y" ) ) )
				{
					HotSeat3D hotseat = (HotSeat3D)iLoongLauncher.getInstance().d3dListener.getRoot().getHotSeatBar();
					if( hotseat != null && HotSeat3D.STATE_BACK == hotseat.getHot3DState() )
					{
						if( hotseat.getMainGroup() != null )
						{
							hotseat.getMainGroup().scroll( x , y , deltaX , deltaY );
							Root3D.hotSeat_scrolling_back = true;
							return true;
						}
					}
				}
				// 防止在同一次scroll中，先让hotseat
				// scrollback，紧接着又执行pageIndicator的scroll操作
				if( Root3D.hotSeat_scrolling_back == true )
					return true;
			}
			// teapotXu add end
			Root3D.scroll_indicator = true;
			this.requestFocus();
			SendMsgToAndroid.sendHideWorkspaceMsgEx();
			//xujin 移除新闻页
			Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			//			if( DefaultLayout.enable_news && workspace.findNewsPage() )
			//			{
			//				pageNum = workspace.getPageNum() - 1;
			//			}
			//			scroll_degree = getTempDegree( x , R3D.page_indicator_size );
			scroll_degree = getTempDegree2( x , R3D.page_indicator_size );
			int currentPage = (int)( scroll_degree + 0.5 );
			//确保不显示新闻页
			//			if( DefaultLayout.enable_news && workspace.findNewsPage() )
			//			{
			//				currentPage = currentPage == workspace.getPageNum() - 1 ? workspace.getPageNum() - 2 : currentPage;
			//			}
			setCurrentPage( currentPage );
			workspace.setCurrentPage( currentPage );
			float temp_degree = scroll_degree - (int)scroll_degree;
			//			Log.i( "jinxu" , "temp_degree = " + temp_degree );
			if( temp_degree < 0.5 )
			{
				setDegree( -temp_degree );
				workspace.setDegreeOnly( -temp_degree );
			}
			else
			{
				setDegree( 1 - temp_degree );
				workspace.setDegreeOnly( 1 - temp_degree );
			}
			workspace.updateEffect( scroll_degree );
			if( !Workspace3D.is_longKick )
				( (Root3D)viewParent ).startDragScrollIndicatorEffect();
		}
		else
		{
			if( Math.abs( x - downX ) > CLICK_MOVE || Math.abs( y - downY ) > CLICK_MOVE )
			{
				downTime = 0;
				// viewParent.onCtrlEvent(this, PAGE_INDICATOR_SCROLL);
			}
		}
		return false;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( Workspace3D.isRecentAppVisible() )
		{
			return false;
		}
		Workspace3D.prepareGesture( y );
		//xujin 
		if( DefaultLayout.enable_camera )
		{
			CameraManager.instance().hidePreview();
		}
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) )
		{
			return true;
		}
		// xiatian add end
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleStart( this.x , this.y , x , y );
		}
		if( pointer != 0 )
			return true;
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( degree != 0 || !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		downTime = System.currentTimeMillis();
		downX = x;
		downY = y;
		return true;
	}
	
	//
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( Workspace3D.mScrollGestureAction || Workspace3D.isRecentAppVisible() )
		{
			return false;
		}
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		// xiatian add start //EffectPreview
		if( ( DefaultLayout.enable_effect_preview ) && ( mWorkspaceEffectPreview != null && mWorkspaceEffectPreview.isVisible() ) )
		{
			return true;
		}
		// xiatian add end
		if( pointer != 0 )
			return true;
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( /* degree != 0 || */!DefaultLayout.click_indicator_enter_pageselect )
			return false;
		// if (downConsumed && System.currentTimeMillis() - downTime <
		// CLICK_TIME)
		// return true;
		if( DefaultLayout.enable_DesktopIndicatorScroll )
		{
			// teapotXu add start for optimize the hotseat scroll
			if( DefaultLayout.optimize_hotseat_scroll_back )
			{
				Root3D.hotSeat_scrolling_back = false;
			}
			// teapotXu add end
			if( Root3D.scroll_indicator )
			{
				this.releaseFocus();
				Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
				//				SendMsgToAndroid.sendShowWorkspaceMsgEx();
				//scroll_degree = getTempDegree( x , R3D.page_indicator_size );
				scroll_degree = getTempDegree2( x , R3D.page_indicator_size );
				int currentPage = (int)( scroll_degree + 0.5 );
				//xujin
				if( DefaultLayout.enable_news && workspace.findNewsPage() )
				{
					currentPage = currentPage == workspace.getPageNum() - 1 ? workspace.getPageNum() - 2 : currentPage;
				}
				setCurrentPage( currentPage );
				workspace.setCurrentPage( currentPage );
				//xujin 恢复
				if( DefaultLayout.enable_news && workspace.findNewsPage() )
				{
					pageNum = workspace.getPageNum();
				}
				SendMsgToAndroid.sendShowWorkspaceMsgEx();
				float temp_degree = scroll_degree - (int)scroll_degree;
				if( temp_degree < 0.5 )
				{
					setDegree( -temp_degree );
					workspace.setDegree( -temp_degree );
				}
				else
				{
					setDegree( 1 - temp_degree );
					workspace.setDegree( 1 - temp_degree );
				}
				if( workspace.temp_mType == -1 )
				{
					int type = workspace.getEffectType();
					boolean random = workspace.getRandom();
					for( int i = 0 ; i < R3D.workSpace_list_string.length ; i++ )
					{
						if( APageEase.mEffectMap.get( R3D.workSpace_list_string[i] ) == APageEase.COOLTOUCH_EFFECT_GS3 )
						{
							workspace.setEffectType( i );
							break;
						}
					}
					if( random )
					{
						workspace.setTempEffectType( 1 );
					}
					else
					{
						workspace.setTempEffectType( type );
					}
				}
				workspace.startAutoEffect();
				Root3D.scroll_indicator = false;
				( (Root3D)viewParent ).stopDragScrollIndicatorEffect( false );
			}
			else if( System.currentTimeMillis() - downTime < CLICK_TIME && Math.abs( x - downX ) < CLICK_MOVE && Math.abs( y - downY ) < CLICK_MOVE )
			{
				if( !DefaultLayout.page_container_shown )
				{
					float focusWidth = indicatorSize * pageNum;
					float startX = this.x + ( this.width - focusWidth ) / 2.0f;
					if( x >= startX && x < this.width - startX )
					{
						float temp_scroll_degree = getTempDegree( x , R3D.page_indicator_size );
						int desPage = (int)( temp_scroll_degree + 0.5 );
						if( currentPage == desPage )
							return true;
						this.requestFocus();
						SendMsgToAndroid.sendHideWorkspaceMsgEx();
						setUser2( (float)currentPage );
						s4indicatorClickTween = startTween( View3DTweenAccessor.USER2 , Cubic.OUT , 0.3f , (float)desPage , 0f , 0f ).setCallback( this );
					}
				}
				else
				{
					if( System.currentTimeMillis() - downTime < CLICK_TIME && Math.abs( x - downX ) < CLICK_MOVE && Math.abs( y - downY ) < CLICK_MOVE )
						return viewParent.onCtrlEvent( this , PAGE_INDICATOR_CLICK );
				}
			}
		}
		else
		{
			if( System.currentTimeMillis() - downTime < CLICK_TIME && Math.abs( x - downX ) < CLICK_MOVE && Math.abs( y - downY ) < CLICK_MOVE )
				return viewParent.onCtrlEvent( this , PAGE_INDICATOR_CLICK );
		}
		return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		return super.onClick( x , y );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( iLoongLauncher.isShowNews )
		{
			return false;
		}
		// Log.v("jbc", "eee onLongClick degree="+degree);
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( s4indicatorClickTween != null )
			return true;
		if( degree != 0 || !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		downTime = 0;
		if( DefaultLayout.enable_DesktopIndicatorScroll )
		{
			Root3D.scroll_indicator = true;
			this.requestFocus();
			//xujin 修改页面数量
			Workspace3D workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D();
			SendMsgToAndroid.sendHideWorkspaceMsgEx();
			//xujin
			if( DefaultLayout.enable_news && workspace.findNewsPage() )
			{
				pageNum = workspace.getPageNum() - 1;
			}
			scroll_degree = getTempDegree2( x , R3D.page_indicator_size );
			//			scroll_degree = getTempDegree( x , R3D.page_indicator_size );
			int currentPage = (int)( scroll_degree + 0.5 );
			//xujin确保不显示新闻页
			if( DefaultLayout.enable_news && workspace.findNewsPage() )
			{
				currentPage = currentPage == workspace.getPageNum() - 1 ? workspace.getPageNum() - 2 : currentPage;
			}
			setCurrentPage( currentPage );
			workspace.setCurrentPage( currentPage );
			float temp_degree = scroll_degree - (int)scroll_degree;
			if( temp_degree < 0.5 )
			{
				setDegree( -temp_degree );
				workspace.setDegreeOnly( -temp_degree );
			}
			else
			{
				setDegree( 1 - temp_degree );
				workspace.setDegreeOnly( 1 - temp_degree );
			}
			workspace.updateEffect( scroll_degree );
			( (Root3D)viewParent ).startDragScrollIndicatorEffect();
		}
		else
		{
			viewParent.onCtrlEvent( this , PAGE_INDICATOR_SCROLL );
		}
		return true;
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		// Log.v("jbc", "eee fling degree="+degree);
		if( !Desktop3DListener.bCreatDone )
			return true;
		if( degree != 0 || !DefaultLayout.click_indicator_enter_pageselect )
			return false;
		return true;
	}
	
	@Override
	public void pageScroll(
			float degree ,
			int index ,
			int count )
	{
		indicatorAlpha = 1;
		if( indicatorTween != null && !indicatorTween.isFinished() )
		{
			indicatorTween.free();
			indicatorTween = null;
		}
		currentPage = index;
		this.degree = degree;
		pageNum = count;
	}
	
	@Override
	public int getIndex()
	{
		// TODO Auto-generated method stub
		return currentPage;
	}
	
	public void setPageNum(
			int num )
	{
		pageNum = num;
	}
	
	// public void activate() {
	// show();
	// startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT, 0.8f,
	// ACTIVATE_SCALE, ACTIVATE_SCALE, 0);
	// pageMode = MODE_ACTIVATE;
	// }
	//
	// public void normal() {
	// show();
	// startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT, 0.8f, NORMAL_SCALE,
	// NORMAL_SCALE, 0);
	// pageMode = MODE_NORMAL;
	// }
	public void show()
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		indicatorAlpha = 1;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart show" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.4f , 1 , 0 , 0 ).setUserData( show_indicator ).setCallback( this );
		// Log.v("indicator","myTween show_indicator:" + myTween);
	}
	
	public void showEx()
	{
		if( this.touchable == true )
		{
			return;
		}
		super.show();
		indicatorAlpha = 1;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart show" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.0f , 1 , 0 , 0 ).setUserData( show_indicator ).setCallback( this );
		// Log.v("indicator","myTween show_indicator:" + myTween);
	}
	
	public void hide()
	{
		if( this.touchable == false )
		{
			return;
		}
		this.touchable = false;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart hide" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.4f , 0 , 0 , 0 ).setUserData( hide_indicator ).setCallback( this );
		// Log.v("indicator","myTween hide_indicator:" + myTween);
	}
	
	public void hideEx()
	{
		if( this.touchable == false )
		{
			return;
		}
		this.touchable = false;
		stopTween();
		animating = true;
		Log.v( "PageIndicator3D" , "TweenStart hide" );
		myTween = startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.0f , 0 , 0 , 0 ).setUserData( hide_indicator ).setCallback( this );
		// Log.v("indicator","myTween hide_indicator:" + myTween);
	}
	
	public void hideNoAnim()
	{
		this.touchable = false;
		this.visible = false;
	}
	
	// public void showIndicator(){
	// //if(indicatorAnimView.rotation == 0)return;
	// animating = true;
	// //indicatorAnimView.rotation = -180;
	// setRotationVector(0, 0, 1);
	// View3DTweenAccessor.manager.killTarget(indicatorAnimView);
	// Tween.to(indicatorAnimView, View3DTweenAccessor.ROTATION,
	// 0.4f).ease(Quad.OUT)
	// .target(0)
	// .start(View3DTweenAccessor.manager);
	// }
	//
	// public void hideIndicator(){
	// //if(indicatorAnimView.rotation == -180)return;
	// animating = true;
	// //indicatorAnimView.rotation = 0;
	// setRotationVector(0, 0, 1);
	// View3DTweenAccessor.manager.killTarget(indicatorAnimView);
	// Tween.to(indicatorAnimView, View3DTweenAccessor.ROTATION,
	// 0.4f).ease(Quad.OUT)
	// .target(-180)
	// .start(View3DTweenAccessor.manager);
	// }
	@Override
	public void setCurrentPage(
			int current )
	{
		if( currentPage == -1 )
		{
			currentPage = current;
			targetPage = currentPage;
		}
		currentPage = current;
	}
	
	public void setDegree(
			float degree )
	{
		this.degree = degree;
	}
	
	@Override
	public void setUser(
			float value )
	{
		// TODO Auto-generated method stub
		indicatorAlpha = value;
	}
	
	@Override
	public float getUser()
	{
		// TODO Auto-generated method stub
		return indicatorAlpha;
	}
	
	@Override
	public void setUser2(
			float value )
	{
		super.setUser2( value );
		setCurrentPage( (int)( value + 0.5 ) );
		Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
		workspace.setCurrentPage( (int)( value + 0.5 ) );
		float temp_degree = value - (int)value;
		if( temp_degree < 0.5 )
		{
			setDegree( -temp_degree );
			workspace.setDegreeOnly( -temp_degree );
		}
		else
		{
			setDegree( 1 - temp_degree );
			workspace.setDegreeOnly( 1 - temp_degree );
		}
		workspace.updateEffect( value );
	}
	
	public void finishAutoEffect()
	{
		indicatorAlpha = 1;
		if( indicatorTween != null && !indicatorTween.isFinished() )
		{
			indicatorTween.free();
			indicatorTween = null;
		}
		indicatorTween = startTween( View3DTweenAccessor.USER , Cubic.OUT , INDICATOR_FADE_TWEEN_DURATION , 0 , 0 , 0 ).setCallback( this );
		// Log.v("indicator","indicatorTween:" + indicatorTween);
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( type == TweenCallback.COMPLETE && source == myTween )
		{
			// Log.v("indicator","source:" + source);
			myTween = null;
			int animKind = (Integer)( source.getUserData() );
			if( animKind == hide_indicator )
			{
				Log.v( "PageIndicator3D" , "TweenComplete hide" );
				this.color.a = 0;
				if( !touchable )
					super.hide();
			}
			else if( animKind == show_indicator )
			{
				Log.v( "PageIndicator3D" , "TweenComplete show" );
				this.color.a = 1;
				super.show();
				finishAutoEffect();
			}
			return;
		}
		if( type == TweenCallback.COMPLETE && source == indicatorTween )
		{
			indicatorTween = null;
			indicatorAlpha = 0;
			return;
		}
		if( type == TweenCallback.COMPLETE && source == s4indicatorClickTween )
		{
			s4indicatorClickTween = null;
			Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			workspace.initView();
			workspace.recoverPageSequence();
			this.releaseFocus();
			if( DefaultLayout.show_music_page || DefaultLayout.enable_camera )
			{
				( (Root3D)viewParent ).showSuitableSeat();
			}
			//			//xujin
			//			if( DefaultLayout.enable_camera )
			//			{
			//				( (Root3D)viewParent ).showSuitableSeat();
			//			}
			SendMsgToAndroid.sendShowWorkspaceMsgEx();
			return;
		}
	}
	
	public float getTempDegree(
			float touch_x ,
			int indicator_space )
	{
		int screenWidth = Utils3D.getScreenWidth();
		int total_w = indicator_space * ( pageNum - 1 );
		float start_x = ( screenWidth - total_w ) / 2;
		float degree = (float)( touch_x - start_x ) / indicator_space;
		if( degree < 0 )
			return 0;
		if( degree > pageNum - 1 )
			return pageNum - 1;
		return degree;
	}
	
	public float getTempDegree2(
			float touch_x ,
			int indicator_space )
	{
		int screenWidth = Utils3D.getScreenWidth();
		int total_w = indicator_space * ( pageNum - 1 );
		Workspace3D workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D();
		float start_x = ( screenWidth - total_w ) / 2;
		float degree = (float)( touch_x - start_x ) / indicator_space;
		if( degree < 0 )
			return 0;
		if( DefaultLayout.enable_news && workspace.findNewsPage() )
		{
			if( degree > pageNum - 2 )
				return pageNum - 2;
		}
		else
		{
			if( degree > pageNum - 1 )
				return pageNum - 1;
		}
		return degree;
	}
	
	public float getScrollDegree()
	{
		return scroll_degree;
	}
	
	// xiatian add start //EffectPreview
	public void setWorkspaceEffectPreview3D(
			View3D v )
	{
		this.mWorkspaceEffectPreview = (EffectPreview3D)v;
	}
	
	// xiatian add end
	public static PageIndicator3D getInstance()
	{
		return pageIndicator;
	}
}
