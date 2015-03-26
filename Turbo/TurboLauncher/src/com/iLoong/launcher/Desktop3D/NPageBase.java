package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.ViscousFluid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.Desktop3D.APageEase.CrystalCore;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.recent.RecentAppPage;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class NPageBase extends ViewGroup3D
{
	
	final public static float MAX_X_ROTATION = 0.5f;
	protected boolean needXRotation = true;
	public IndicatorView indicatorView;
	public float indicatorPaddingBottom = 0;
	protected boolean drawIndicator = true;
	// private TextureRegion indicator1 = null;
	// private TextureRegion indicator2 = null;
	protected int page_index;
	protected ArrayList<View3D> view_list;
	private boolean random;
	private boolean sequence;
	protected boolean canScroll;
	public float xScale;
	public float yScale;
	public boolean moving;
	public int mType;
	protected int temp_mType = -1;
	protected float mVelocityX;
	protected boolean needLayout = false;
	private static float INDICATOR_FADE_TWEEN_DURATION = 1.0f;
	public Color initColor = new Color( 1.0f , 1.0f , 1.0f , 1.0f );
	protected Tween tween;
	protected Tween dragTween;
	List<PageScrollListener> scrollListeners = new ArrayList<PageScrollListener>();
	protected ArrayList<Integer> mTypelist;
	/************************ added by zhenNan.ye begin ***************************/
	private float last_x = 0;
	private float last_y = 0;
	/************************ added by zhenNan.ye end ***************************/
	/** crystal effect -xp begin**/
	static public CrystalCore crystalGroup = null;
	/** crystal effect -xp end**/
	//xiatian add start	//EffectPreview
	protected Tween mPreviewTween;
	protected static boolean mPreviewFirst = false;
	//xiatian add end
	private PageIndicator3D pageIndicator3D = null;
	/**
	 * 区分手动特效还是自动特效
	 */
	public static boolean autoEffect = false;
	public static boolean scoolif = true; //上滑最近使用后  判断是否左右滑动
	
	public NPageBase(
			String name )
	{
		super( name );
		// if(indicatorView == null)indicatorView = new
		// IndicatorView("npage_indicator",R3D.getTextureRegion("application-page-nv-point1"),R3D.getTextureRegion("application-page-nv-point2"));
		page_index = 0;
		xScale = 0f;
		yScale = 0f;
		mType = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		temp_mType = -1;
		random = true;
		canScroll = false;
		view_list = new ArrayList<View3D>();
		moving = false;
		mTypelist = new ArrayList<Integer>();
		APageEase.initEffectMap();
		setTotalList();
	}
	
	public void setWholePageList()
	{
		mTypelist.clear();
		for( int i = 0 ; i < R3D.workSpace_list_string.length ; i++ )
		{
			mTypelist.add( APageEase.mEffectMap.get( R3D.workSpace_list_string[i] ) );
		}
		setEffectType( SetupMenuActions.getInstance().getStringToIntger( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) );
	}
	
	public void setTotalList()
	{
		mTypelist.clear();
		for( int i = 0 ; i < R3D.app_list_string.length ; i++ )
		{
			mTypelist.add( APageEase.mEffectMap.get( R3D.app_list_string[i] ) );
		}
		setEffectType( SetupMenuActions.getInstance().getStringToIntger( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) );
	}
	
	public void initView()
	{
		View3D view;
		//		this.removeAllViews();
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			view = view_list.get( i );
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				if( this instanceof AppList3D && this.indicatorView != null )
				{
					this.addViewBefore( this.indicatorView , view );
				}
				else
				{
					this.addViewAt( i , view );
				}
			}
			else
			{
				this.addViewAt( i , view );
			}
			//teapotXu add end
			view.setPosition( 0 , 0 );
			view.setRotationZ( 0 );
			view.setScale( 1.0f , 1.0f );
			if( !( Workspace3D.isRecentAppVisible() ) )
				view.setColor( initColor );
			view.setOrigin( view.width / 2 , view.height / 2 );
			view.setZ( 0 );
			view.setOriginZ( 0 );
			if( i != page_index )
				view.hide();
			else
				view.show();
			// 还原icon
			if( view instanceof ViewGroup3D )
			{
				int size = ( (ViewGroup3D)view ).getChildCount();
				View3D icon;
				for( int j = 0 ; j < size ; j++ )
				{
					icon = ( (ViewGroup3D)view ).getChildAt( j );
					icon.setRotationZ( 0 );
					icon.setScale( 1.0f , 1.0f );
					if( !( Workspace3D.isRecentAppVisible() ) )
						icon.setColor( initColor );
					icon.setOrigin( icon.width / 2 , icon.height / 2 );
					icon.setOriginZ( 0 );
				}
				if( view instanceof GridView3D )
					( (GridView3D)view ).layout_pub( 0 , false );
			}
		}
		// setDegree(0f);
		// setDegree(0f, 0f);
		moving = false;
	}
	
	public void initData(
			ViewGroup3D view )
	{
		// Log.v("NpageBase", "initData");
		// Color temp_color;
		//
		// view.setPosition(0, 0);
		// view.setZ(0);
		// view.setRotationVector(0, 0, 1);
		// view.setRotation(0);
		// view.setRotationAngle(0, 0, 0);
		// view.setScale(1.0f, 1.0f);
		// view.setScaleZ(1.0f);
		// view.setOrigin(width / 2, height / 2);
		// view.setOriginZ(0);
		// temp_color = getColor();
		// temp_color.a = 1;
		// view.setColor(temp_color);
		//
		// int size;
		// if(view instanceof GridView3D){
		// size = ((GridView3D)view).getChildCount();
		//
		// }
		// else
		// size = view.getChildCount();
		// for(int i = 0;i<size;i++)
		// {
		// View3D icon;
		// if(view instanceof GridView3D)
		// {
		// icon = ((GridView3D)view).getChildAt(i);
		// Object obj = icon.getTag();
		// if(obj!= null)
		// {
		// if(obj instanceof Vector2)
		// {
		// icon.setPosition(((Vector2)icon.getTag()).x,((Vector2)icon.getTag()).y);
		// }
		// }
		// }
		// else
		// {
		// icon = view.getChildAt(i);
		// }
		// icon.setZ(0);
		// icon.setRotationVector(0, 0, 1);
		// icon.setRotation(0);
		// icon.setRotationAngle(0, 0, 0);
		// icon.setScale(1.0f, 1.0f);
		// icon.setScaleZ(1.0f);
		// icon.setOriginZ(0);
		// temp_color = icon.getColor();
		// temp_color.a = 1;
		// icon.setColor(temp_color);
		// icon.show();
		// }
	}
	
	public boolean getRandom()
	{
		return this.random;
	}
	
	protected int getIndicatorPageCount()
	{
		return this.view_list.size();
	}
	
	protected int getIndicatorPageIndex()
	{
		return this.page_index;
	}
	
	public void setEffectType(
			int type )
	{
		this.random = false;
		this.sequence = false;
		/*
		 * if (type == APageEase.COOLTOUCH_EFFECT_SEQUENCE) { this.mType =
		 * APageEase.COOLTOUCH_EFFECT_BINARIES; this.sequence = true; } else
		 */
		//		if (type == 1 && !DefaultLayout.page_effect_no_radom_style) {
		//			this.mType = 2;
		//			this.random = true;
		//		} else {
		//			this.mType = type;
		//		}
		if( type < mTypelist.size() )
		{
			if( mTypelist.get( type ) == APageEase.COOLTOUCH_EFFECT_RANDOM )
			{
				this.mType = MathUtils.random( 0 , mTypelist.size() - 1 );
				this.random = true;
			}
			else
			{
				this.mType = type;
			}
		}
		else
		{
			Log.v( "cooee" , "NpageBase --- setEffectType ---error -- type : " + type + " -- mTypelist.size: " + mTypelist.size() );
			return;
		}
		if( this instanceof AppList3D || ( this instanceof Workspace3D ) )
		{
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
			{
				if( crystalGroup == null )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							crystalGroup = new CrystalCore( "Crystal" , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - ( R3D.appbar_height + R3D.applist_indicator_y ) );
						}
					} );
				}
			}
			else
			{
				if( crystalGroup != null )
				{
					boolean b_recycle_crystalgroup = false;
					if( this instanceof AppList3D )
					{
						ArrayList<Integer> mTypelist_workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D().mTypelist;
						if( mTypelist_workspace.get( iLoongLauncher.getInstance().d3dListener.getWorkspace3D().getEffectType() ) != APageEase.COOLTOUCH_EFFECT_CRYSTAL )
						{
							b_recycle_crystalgroup = true;
						}
					}
					else if( this instanceof Workspace3D )
					{
						ArrayList<Integer> mTypelist_appList = iLoongLauncher.getInstance().d3dListener.getAppList().appList.mTypelist;
						if( mTypelist_appList.get( iLoongLauncher.getInstance().d3dListener.getAppList().appList.getEffectType() ) != APageEase.COOLTOUCH_EFFECT_CRYSTAL )
						{
							b_recycle_crystalgroup = true;
						}
					}
					else
					{
						b_recycle_crystalgroup = true;
					}
					if( b_recycle_crystalgroup == true )
					{
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								crystalGroup.dispose();
								crystalGroup = null;
							}
						} );
					}
				}
			}
		}
		//		
		if( this.isVisible() )
			initView();
	}
	
	public int getEffectType()
	{
		return this.mType;
	}
	
	public void setTempEffectType(
			int type )
	{
		this.temp_mType = type;
	}
	
	public int getTempEffectType()
	{
		return this.temp_mType;
	}
	
	public void recoveryEffectType()
	{
		//teapotXu add start
		if( DefaultLayout.enable_workspace_miui_edit_mode && this instanceof Workspace3D )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				initView();
				return;
			}
		}
		//teapotXu add end
		if( temp_mType != -1 )
		{
			setEffectType( temp_mType );
			temp_mType = -1;
		}
		initView();
	}
	
	public void addPage(
			View3D view )
	{
		view.setPosition( 0 , 0 );
		if( view_list.size() != 0 )
		{
			view.hide();
		}
		view_list.add( view );
		addView( view );
	}
	
	public void addPage(
			int index ,
			View3D view )
	{
		view.setPosition( 0 , 0 );
		if( view_list.size() != 0 )
		{
			view.hide();
		}
		view_list.add( index , view );
		//teapotXu add start: 添加保护，当水晶滑动时，容易造成AppList.children 个数不对
		if( index > this.getChildCount() )
			index = this.getChildCount();
		//teapotXu add end
		this.addViewAt( index , view );
	}
	
	protected int nextIndex()
	{
		//teapotXu add start for doov customization
		if( DefaultLayout.workspace_npages_circle_scroll_config && ( this instanceof Workspace3D ) )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
			if( prefs.getBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) , true ) == false || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
			{
				return( page_index == view_list.size() - 1 ? view_list.size() - 1 : page_index + 1 );
			}
			else
			{
				return( page_index == view_list.size() - 1 ? 0 : page_index + 1 );
			}
		}
		else if( DefaultLayout.enable_workspace_miui_edit_mode && this instanceof Workspace3D )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
				return( page_index == view_list.size() - 1 ? view_list.size() - 1 : page_index + 1 );
			else
				return( page_index == view_list.size() - 1 ? 0 : page_index + 1 );
		}
		else
			//teapotXu add end		
			return( page_index == view_list.size() - 1 ? 0 : page_index + 1 );
	}
	
	protected int preIndex()
	{
		//teapotXu add start for doov customization
		if( DefaultLayout.workspace_npages_circle_scroll_config && ( this instanceof Workspace3D ) )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
			if( prefs.getBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) , true ) == false || ( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
			{
				return( page_index == 0 ? 0 : page_index - 1 );
			}
			else
			{
				return( page_index == 0 ? view_list.size() - 1 : page_index - 1 );
			}
		}
		else if( DefaultLayout.enable_workspace_miui_edit_mode && this instanceof Workspace3D )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
				return( page_index == 0 ? 0 : page_index - 1 );
			else
				return( page_index == 0 ? view_list.size() - 1 : page_index - 1 );
		}
		else
			//teapotXu add end
			return( page_index == 0 ? view_list.size() - 1 : page_index - 1 );
	}
	
	protected void changeEffect()
	{
		if( this.random )
		{
			initView();
			moving = true;
			mType = MathUtils.random( 0 , mTypelist.size() - 1 );
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
			{
				mType = 3;
			}
			if( this instanceof AppList3D || ( this instanceof Workspace3D ) )
			{
				if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
				{
					if( crystalGroup == null )
					{
						crystalGroup = new CrystalCore( "Crystal" , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - ( R3D.appbar_height + R3D.applist_indicator_y ) );
					}
				}
				else
				{
					boolean b_recycle_crystalgroup = false;
					if( this instanceof AppList3D )
					{
						ArrayList<Integer> mTypelist_workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D().mTypelist;
						if( mTypelist_workspace.get( iLoongLauncher.getInstance().d3dListener.getWorkspace3D().getEffectType() ) != APageEase.COOLTOUCH_EFFECT_CRYSTAL )
						{
							b_recycle_crystalgroup = true;
						}
					}
					else if( this instanceof Workspace3D )
					{
						ArrayList<Integer> mTypelist_appList = iLoongLauncher.getInstance().d3dListener.getAppList().appList.mTypelist;
						if( mTypelist_appList.get( iLoongLauncher.getInstance().d3dListener.getAppList().appList.getEffectType() ) != APageEase.COOLTOUCH_EFFECT_CRYSTAL )
						{
							b_recycle_crystalgroup = true;
						}
					}
					else
					{
						b_recycle_crystalgroup = true;
					}
					if( b_recycle_crystalgroup )
					{
						if( crystalGroup != null )
						{
							crystalGroup.dispose();
							crystalGroup = null;
						}
					}
				}
			}
		}
		else if( this.sequence )
		{
			initView();
			moving = true;
			mType++;
			if( mType == mTypelist.size() )
				mType = 3;
		}
	}
	
	protected void updateEffect()
	{
		if( view_list.size() == 0 )
			return;
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( DefaultLayout.enable_DesktopIndicatorScroll && Root3D.scroll_indicator )
		{
			return;
		}
		if( indicatorView != null )
		{
			if( indicatorView.indicatorStyle == IndicatorView.INDICATOR_STYLE_COMET )
			{
				indicatorView.pariticle_handle();
			}
		}
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex() );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex() );
		if( !moving )
		{
			changeEffect();
			moving = true;
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					for( int j = 0 ; j < ( (GridView3D)i ).getChildCount() ; j++ )
					{
						View3D icon = ( (GridView3D)i ).getChildAt( j );
						icon.setTag( new Vector2( icon.getX() , icon.getY() ) );
					}
				}
			}
		}
		float tempYScale = 0;
		if( needXRotation )
		{
			if( yScale > MAX_X_ROTATION )
			{
				tempYScale = MAX_X_ROTATION;
			}
			else if( yScale < -MAX_X_ROTATION )
			{
				tempYScale = -MAX_X_ROTATION;
			}
			else
			{
				tempYScale = yScale;
			}
		}
		tempYScale = -tempYScale;
		if( this.random == false && ( !( this.mType == 0 ) || this.mType == 0 ) )
		{
			APageEase.setStandard( true );
		}
		else
		{
			APageEase.setStandard( false );
		}
		//xujin 用来判断当前worksace页面
		Workspace3D workspace = iLoongLauncher.getInstance().d3dListener.getWorkspace3D();
		if( xScale > 0 )
		{
			// initData(next_view);
			//if(DefaultLayout.workspace_npages_circle_scroll_config && (this instanceof Workspace3D))
			{
				APageEase.setScrolldirection( false );
			}
			boolean needDefaultEffect = pre_view.name.equals( "cameraView" ) || pre_view.name.equals( "newsView" ) || cur_view.name.equals( "cameraView" ) || cur_view.name.equals( "newsView" );
			if( workspace != null && workspace.isVisible() && needDefaultEffect )
			{
				if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && NPageBase.this instanceof Workspace3D && !autoEffect )
				{
					APageEase.is_scroll_anim_in_eidt_mode = false;
					if( xScale > 0.5f )
					{
						int preIndex = preIndex();
						next_view = (ViewGroup3D)view_list.get( preIndex == 0 ? 0 : preIndex - 1 );
						APageEase.is_scroll_anim_in_eidt_mode = true;
					}
					next_view.show();
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , APageEase.COOLTOUCH_EFFECT_DEFAULT );
				}
				else
				{
					next_view.hide();
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( 0 ) );
				}
			}
			else
			{
				if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && NPageBase.this instanceof Workspace3D && !autoEffect )
				{
					APageEase.is_scroll_anim_in_eidt_mode = false;
					if( xScale > 0.5f )
					{
						int preIndex = preIndex();
						next_view = (ViewGroup3D)view_list.get( preIndex == 0 ? 0 : preIndex - 1 );
						APageEase.is_scroll_anim_in_eidt_mode = true;
					}
					next_view.show();
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , APageEase.COOLTOUCH_EFFECT_DEFAULT );
				}
				else
				{
					next_view.hide();
					next_view = null;
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , mTypelist.get( mType ) );
				}
			}
			if( ( DefaultLayout.show_music_page || DefaultLayout.enable_camera ) && temp_mType == -1 )
			{
				HotSeat3D hotseat = iLoongLauncher.getInstance().getD3dListener().getRoot().getHotSeatBar();
				if( !( cur_view instanceof MediaView3D ) && pre_view instanceof MediaView3D )
				{
					hotseat.stopShakeTween();
					MediaView3D mediaView = (MediaView3D)pre_view;
					if( xScale < 0.5f )
					{
						float hotseatY = -R3D.seatbar_hide_height * ( xScale * 2 );
						hotseat.setPosition( hotseat.getX() , hotseatY );
						mediaView.setSeatY( -R3D.seatbar_hide_height );
						Workspace3D.mediaBgAlpha = 0;
					}
					else if( xScale >= 0.5f && xScale <= 1.0f )
					{
						hotseat.setPosition( hotseat.getX() , -R3D.seatbar_hide_height );
						mediaView.setSeatY( -R3D.seatbar_hide_height * ( ( 1.0f - xScale ) * 2 ) );
						Workspace3D.mediaBgAlpha = 1 - ( ( 1.0f - xScale ) * 2 );
					}
				}
				else if( cur_view instanceof MediaView3D && !( pre_view instanceof MediaView3D ) )
				{
					hotseat.stopShakeTween();
					MediaView3D mediaView = (MediaView3D)cur_view;
					if( xScale < 0.5f )
					{
						mediaView.setSeatY( -R3D.seatbar_hide_height * ( xScale * 2 ) );
						hotseat.setPosition( hotseat.getX() , -R3D.seatbar_hide_height );
						Workspace3D.mediaBgAlpha = 1 - ( xScale * 2 );
					}
					else if( xScale >= 0.5f && xScale <= 1.0f )
					{
						float hotseatY = -R3D.seatbar_hide_height * ( ( 1.0f - xScale ) * 2 );
						hotseat.setPosition( hotseat.getX() , hotseatY );
						mediaView.setSeatY( -R3D.seatbar_hide_height );
						Workspace3D.mediaBgAlpha = 0;
					}
				}
				//xujin
				else if( ( cur_view instanceof MediaView3D ) && ( pre_view instanceof MediaView3D ) )
				{
					MediaView3D pre = (MediaView3D)pre_view;
					MediaView3D cur = (MediaView3D)cur_view;
					float hotseatY = -R3D.seatbar_hide_height;
					hotseat.setPosition( hotseat.getX() , hotseatY );
					if( xScale < 0.5f )
					{
						cur.setSeatY( -R3D.seatbar_hide_height * ( xScale * 2 ) );
						pre.setSeatY( -R3D.seatbar_hide_height );
					}
					else if( xScale >= 0.5f && xScale <= 1.0f )
					{
						cur.setSeatY( -R3D.seatbar_hide_height );
						pre.setSeatY( -R3D.seatbar_hide_height * ( ( 1.0f - xScale ) * 2 ) );
					}
				}
				else if( !( cur_view instanceof MediaView3D ) && !( pre_view instanceof MediaView3D ) )
				{
					hotseat.setPosition( hotseat.getX() , 0 );
					Workspace3D.mediaBgAlpha = 0;
				}
			}
		}
		else if( xScale < 0 )
		{
			// initData(pre_view);
			//if(DefaultLayout.workspace_npages_circle_scroll_config && (this instanceof Workspace3D))
			{
				APageEase.setScrolldirection( true );
			}
			//xujin
			//如果附近3页有专属页，就使用默认的动画
			boolean needDefaultEffect = next_view.name.equals( "cameraView" ) || next_view.name.equals( "newsView" ) || cur_view.name.equals( "cameraView" ) || cur_view.name.equals( "newsView" );
			if( workspace != null && workspace.isVisible() && needDefaultEffect )
			{
				if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && NPageBase.this instanceof Workspace3D && !autoEffect )
				{
					APageEase.is_scroll_anim_in_eidt_mode = false;
					if( xScale < -0.5f )
					{
						int nextIndex = nextIndex();
						pre_view = (ViewGroup3D)view_list.get( nextIndex == view_list.size() - 1 ? view_list.size() - 1 : nextIndex + 1 );
						APageEase.is_scroll_anim_in_eidt_mode = true;
					}
					pre_view.show();
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , APageEase.COOLTOUCH_EFFECT_DEFAULT );
				}
				else
				{
					pre_view.hide();
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( 0 ) );
				}
			}
			else
			{
				if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && NPageBase.this instanceof Workspace3D && !autoEffect )
				{
					APageEase.is_scroll_anim_in_eidt_mode = false;
					if( xScale < -0.5f )
					{
						int nextIndex = nextIndex();
						pre_view = (ViewGroup3D)view_list.get( nextIndex == view_list.size() - 1 ? view_list.size() - 1 : nextIndex + 1 );
						APageEase.is_scroll_anim_in_eidt_mode = true;
					}
					pre_view.show();
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , APageEase.COOLTOUCH_EFFECT_DEFAULT );
				}
				else
				{
					pre_view.hide();
					pre_view = null;
					APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
				}
			}
			if( ( DefaultLayout.show_music_page || DefaultLayout.enable_camera ) && temp_mType == -1 )
			{
				HotSeat3D hotseat = iLoongLauncher.getInstance().getD3dListener().getRoot().getHotSeatBar();
				if( !( cur_view instanceof MediaView3D ) && next_view instanceof MediaView3D )
				{
					hotseat.stopShakeTween();
					MediaView3D mediaView = (MediaView3D)next_view;
					if( xScale > -0.5f )
					{
						float hotseatY = -R3D.seatbar_hide_height * ( xScale * -2 );
						hotseat.setPosition( hotseat.getX() , hotseatY );
						mediaView.setSeatY( -R3D.seatbar_hide_height );
						Workspace3D.mediaBgAlpha = 0;
					}
					else if( xScale <= -0.5f && xScale >= -1.0f )
					{
						hotseat.setPosition( hotseat.getX() , -R3D.seatbar_hide_height );
						mediaView.setSeatY( -R3D.seatbar_hide_height * ( ( 1.0f + xScale ) * 2 ) );
						Workspace3D.mediaBgAlpha = 1 - ( ( 1.0f + xScale ) * 2 );
					}
				}
				else if( cur_view instanceof MediaView3D && !( next_view instanceof MediaView3D ) )
				{
					hotseat.stopShakeTween();
					MediaView3D mediaView = (MediaView3D)cur_view;
					if( xScale > -0.5f )
					{
						mediaView.setSeatY( -R3D.seatbar_hide_height * ( xScale * -2 ) );
						hotseat.setPosition( hotseat.getX() , -R3D.seatbar_hide_height );
						Workspace3D.mediaBgAlpha = 1 - ( xScale * -2 );
					}
					else if( xScale <= -0.5f && xScale >= -1.0f )
					{
						float hotseatY = -R3D.seatbar_hide_height * ( ( 1.0f + xScale ) * 2 );
						hotseat.setPosition( hotseat.getX() , hotseatY );
						mediaView.setSeatY( -R3D.seatbar_hide_height );
						Workspace3D.mediaBgAlpha = 0;
					}
				}
				//xujin
				else if( ( cur_view instanceof MediaView3D ) && ( next_view instanceof MediaView3D ) )
				{
					MediaView3D cur = (MediaView3D)cur_view;
					MediaView3D nxt = (MediaView3D)next_view;
					float hotseatY = -R3D.seatbar_hide_height;
					hotseat.setPosition( hotseat.getX() , hotseatY );
					if( xScale > -0.5f )
					{
						cur.setSeatY( -R3D.seatbar_hide_height * ( xScale * -2 ) );
						nxt.setSeatY( -R3D.seatbar_hide_height );
					}
					else if( xScale <= -0.5f && xScale >= -1.0f )
					{
						cur.setSeatY( -R3D.seatbar_hide_height );
						nxt.setSeatY( -R3D.seatbar_hide_height * ( ( 1.0f + xScale ) * 2 ) );
					}
				}
				else if( !( cur_view instanceof MediaView3D ) && !( next_view instanceof MediaView3D ) )
				{
					hotseat.setPosition( hotseat.getX() , 0 );
					Workspace3D.mediaBgAlpha = 0;
				}
			}
		}
		else if( yScale != 0 )
		{
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && NPageBase.this instanceof Workspace3D && !autoEffect )
			{
				APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , APageEase.COOLTOUCH_EFFECT_DEFAULT );
			}
			else
			{
				pre_view = null;
				APageEase.updateEffect( pre_view , cur_view , next_view , xScale , tempYScale , mTypelist.get( mType ) );
			}
		}
		else
		{
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
			{
				if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && NPageBase.this instanceof Workspace3D && !autoEffect )
				{
					APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , tempYScale , 0 );
				}
				else
				{
					next_view = null;
					APageEase.updateEffect( next_view , cur_view , next_view , xScale , 0 , mTypelist.get( mType ) );
				}
			}
		}
		if( xScale < -1f )
		{
			cur_view.hide();
			// initData(cur_view);
			page_index = nextIndex();
			setDegree( xScale + 1f );
			changeEffect();
		}
		if( xScale > 1f )
		{
			cur_view.hide();
			// initData(cur_view);
			page_index = preIndex();
			changeEffect();
		}
	}
	
	public void updateEffect(
			float scroll_degree )
	{
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( view_list.size() <= 1 )
		{
			return;
		}
		if( getRandom() == false && mType == 0 )
		{
			APageEase.setStandard( true );
		}
		else
		{
			APageEase.setStandard( false );
		}
		page_index = (int)( scroll_degree + 0.5 );
		//		Log.v("jbc","888update temp_degree="+scroll_degree+" page_index="+page_index);
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( page_index == 0 ? view_list.size() - 1 : page_index - 1 );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( page_index == view_list.size() - 1 ? 0 : page_index + 1 );
		if( !moving )
		{
			moving = true;
		}
		APageEase.updateEffect( pre_view , cur_view , next_view , scroll_degree , false );
	}
	
	public void updateEffectDrag()
	{
		//		Log.v("NPageBase", "updateEffect xScale="+xScale);//left:0~-1 right:0~1
		if( view_list.size() == 0 )
			return;
		if( page_index < 0 )
		{
			page_index = 0;
			return;
		}
		if( page_index > view_list.size() - 1 )
		{
			page_index = view_list.size() - 1;
			return;
		}
		if( /*DefaultLayout.style_s4 &&*/Root3D.scroll_indicator )
		{
			return;
		}
		if( getRandom() == false && mType == 0 )
		{
			APageEase.setStandard( true );
		}
		else
		{
			APageEase.setStandard( false );
		}
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index );
		ViewGroup3D pre_view = (ViewGroup3D)view_list.get( preIndex() );
		ViewGroup3D next_view = (ViewGroup3D)view_list.get( nextIndex() );
		if( xScale > 0 )
		{
			next_view.hide();
			if( cur_view instanceof CellLayout3D )
				( (CellLayout3D)cur_view ).cellCleanDropStatus();
			APageEase.updateEffect( next_view , pre_view , cur_view , xScale - 1 , 0 , mTypelist.get( 0 )/*APageEase.COOLTOUCH_EFFECT_S4_DRAG*/);
		}
		else if( xScale < 0 )
		{
			pre_view.hide();
			if( cur_view instanceof CellLayout3D )
				( (CellLayout3D)cur_view ).cellCleanDropStatus();
			APageEase.updateEffect( pre_view , cur_view , next_view , xScale , 0 , mTypelist.get( 0 )/*APageEase.COOLTOUCH_EFFECT_S4_DRAG*/);
		}
	}
	
	protected void setDegree(
			float degree )
	{
		this.xScale = degree;
		if( this.scrollListeners != null )
		{
			for( int i = 0 ; i < scrollListeners.size() ; i++ )
			{
				PageScrollListener scrollListener = scrollListeners.get( i );
				scrollListener.pageScroll( xScale , page_index , view_list.size() );
				if( xScale == 0 && scrollListener.getIndex() != this.getCurrentPage() )
				{
					scrollListener.setCurrentPage( this.getCurrentPage() );
				}
			}
		}
		onDegreeChanged();
	}
	
	void setDegreeOnly(
			float degree )
	{
		this.xScale = degree;
	}
	
	void setDegree(
			float xScale ,
			float yScale )
	{
		this.xScale = xScale;
		this.yScale = yScale;
		if( this.scrollListeners != null )
		{
			for( int i = 0 ; i < scrollListeners.size() ; i++ )
			{
				PageScrollListener scrollListener = scrollListeners.get( i );
				scrollListener.pageScroll( xScale , page_index , view_list.size() );
				if( xScale == 0 && scrollListener.getIndex() != this.getCurrentPage() )
				{
					scrollListener.setCurrentPage( this.getCurrentPage() );
				}
			}
		}
		onDegreeChanged();
		//crystalGroup.setRotationY(180*xScale);
	}
	
	protected void stopAutoEffect()
	{
		if( tween != null && !tween.isFinished() )
		{
			tween.free();
			tween = null;
		}
	}
	
	@Override
	public float getX()
	{
		return xScale;
	}
	
	@Override
	public float getY()
	{
		return yScale;
	}
	
	public void onDegreeChanged()
	{
	}
	
	protected boolean isManualScrollTo = false;
	int ScrollDestPage = 0;
	int ScrollstartPage = 0;
	int scrollDire = 1;
	// float scrollxScale = 0;
	int scrollCurPage = 0;
	
	//teapotXu add start for Folder in Mainmenu
	public boolean NPage_IsManualScrollTo()
	{
		return isManualScrollTo;
	}
	
	//teapotXu add end for Folder in Mainmenu	
	public boolean scrollTo(
			int destPage )
	{
		if( destPage < 0 || destPage > view_list.size() - 1 )
			return false;
		ScrollDestPage = destPage;
		ScrollstartPage = page_index;
		if( page_index == destPage )
			return false;
		indicatorView.setAlpha( 1.0f );
		if( indicatorView.getIndicatorTween() != null && !indicatorView.getIndicatorTween().isFinished() )
		{
			indicatorView.getIndicatorTween().free();
			indicatorView.setIndicatorTween( null );
		}
		if( page_index > destPage )
		{
			scrollDire = -1;
			xScale = 0.0001f;
		}
		else
		{
			scrollDire = 1;
			xScale = -0.0001f;
		}
		if( indicatorView != null )
		{
			if( indicatorView.indicatorStyle == IndicatorView.INDICATOR_STYLE_COMET )
			{
				indicatorView.pariticle_handle();
			}
		}
		isManualScrollTo = true;
		stopAutoEffect();
		startAutoEffectMini();
		return true;
	}
	
	// ViewGroup3D lastCurView = null;
	private void updateEffectMini()
	{
		ViewGroup3D next_view;
		int type = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		if( view_list.size() == 0 )
			return;
		int cur_page = ScrollstartPage;
		// Log.v("npg",
		// "xScale="+xScale+" ScrollstartPage="+ScrollstartPage+" cur_page="+cur_page);
		if( cur_page < 0 || cur_page >= view_list.size() )
			return;
		if( !moving )
		{
			initView();
			moving = true;
			for( View3D i : view_list )
			{
				if( i instanceof GridView3D )
				{
					for( int j = 0 ; j < ( (GridView3D)i ).getChildCount() ; j++ )
					{
						View3D icon = ( (GridView3D)i ).getChildAt( j );
						icon.setTag( new Vector2( icon.getX() , icon.getY() ) );
					}
				}
			}
		}
		float tempxScale = xScale - (int)xScale;
		// scrollxScale = xScale;
		ViewGroup3D cur_view = (ViewGroup3D)view_list.get( cur_page );
		// if (lastCurView != null && lastCurView != cur_view)
		// lastCurView.hide();
		int next_page = ScrollDestPage;
		if( next_page >= 0 && next_page < view_list.size() )
			next_view = (ViewGroup3D)view_list.get( next_page );
		else
			next_view = cur_view;
		// if(this.random==false && this.mType==0)
		// {
		// APageEase.setStandard(true);
		// }
		// else
		// {
		// APageEase.setStandard(false);
		// }
		// Log.d("launcher", "tempxScale="+tempxScale);
		if( tempxScale > 0 )
			APageEase.updateEffect( null , next_view , cur_view , tempxScale - 1 , 0 , type );
		else if( tempxScale < 0 )
			APageEase.updateEffect( null , cur_view , next_view , tempxScale , 0 , type );
		// lastCurView = cur_view;
		// scrollCurPage = cur_page;
		// Log.d("launcher", "cur,next="+cur_page+","+next_page);
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			if( i != cur_page && i != next_page )
				view_list.get( i ).hide();
			// Log.d("launcher", "i:"+view_list.get(i).visible);
		}
	}
	
	void startAutoEffectMini()
	{
		int totalOffset = 1;// (ScrollDestPage - ScrollstartPage) * scrollDire;
		float duration = DefaultLayout.page_tween_time;// + totalOffset * 1 / 8;
		mVelocityX = 1000f;
		if( xScale > 0 )
		{
			tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Cubic.OUT ).target( 1 * totalOffset , 0 ).setCallback( this );
		}
		else
		{
			tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Cubic.OUT ).target( -1 * totalOffset , 0 ).setCallback( this );
		}
		mVelocityX = 0;
		tween.start( View3DTweenAccessor.manager );
	}
	
	@Override
	public void setPosition(
			float x ,
			float y )
	{
		if( Root3D.isDragAutoEffect )
		{
			setDegree( x , y );
			updateEffectDrag();
			return;
		}
		if( isManualScrollTo )
		{
			this.xScale = x;
			this.yScale = y;
			updateEffectMini();
			onDegreeChanged();
		}
		else
		{
			// Log.d("launcher", "setPosition 2");
			setDegree( x , y );
			updateEffect();
		}
	}
	
	public void startAutoEffect()
	{
		float duration = DefaultLayout.page_tween_time;
		float density = Gdx.graphics.getDensity();
		if( Math.abs( mVelocityX ) >= DefaultLayout.MIN_FLING_VELOCITY * density )
		{
			duration = getFlingDuration();
			Log.i( "" , "############ flingDuration = " + duration );
		}
		// Log.d("launcher",
		// "currentPage startAutoEffect isManualScrollTo="+isManualScrollTo);
		TweenEquation easeEquation = Quart.OUT;
		//teapotXu_20130316 add start:
		if( DefaultLayout.external_applist_page_effect == true )
		{
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_ELASTICITY )
			{
				//弹性特效 才使用如下的动画方式
				APageEase.setTouchUpAnimEffectStatus( true );
				if( xScale > 0 )
				{
					APageEase.saveDegreeInfoWhnTouchUp( xScale - 1 );
				}
				else
				{
					APageEase.saveDegreeInfoWhnTouchUp( xScale );
				}
				easeEquation = Bounce.OUT;
				duration = duration + 0.2f;
			}
		}
		//teapotXu_20130316: add end		
		isManualScrollTo = false;
		//teapotXu add start for doov customization: circle scroll config
		boolean isAutoScrollBack = false;
		//&& ( this instanceof Workspace3D ) 
		if( ( DefaultLayout.workspace_npages_circle_scroll_config || DefaultLayout.enable_workspace_miui_edit_mode ) && (this instanceof Workspace3D) )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
			if( ( prefs.getBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) , true ) == false ) || Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			{
				//can not scroll screen circle
				if( ( page_index == ( view_list.size() - 1 ) && APageEase.is_scroll_from_right_to_left == true ) || ( page_index == 0 && APageEase.is_scroll_from_right_to_left == false ) )
				{
					isAutoScrollBack = true;
				}
				else
				{
					isAutoScrollBack = false;
				}
			}
			else
			{
				isAutoScrollBack = false;
			}
		}
		//teapotXu add end
		if( xScale == 0 && mVelocityX == 0 )
		{
			if( DefaultLayout.show_music_page || DefaultLayout.enable_camera )
			{
				if( this instanceof Workspace3D )
				{
					( (Root3D)viewParent ).showSuitableSeat();
				}
			}
			return;
		}
		float scroll_sensitive = DefaultLayout.npagbse_scroll_nextpage_sensitive;
		float scroll_velocity_coefficient = DefaultLayout.SCROLL_VELOCITY_COEFFICIENT * density;
		Log.i( "" , "################# xScale + mVelocityX / s = " + ( xScale + mVelocityX / scroll_velocity_coefficient ) );
		// && (!(this instanceof Workspace3D )||(scoolif &&this instanceof Workspace3D ))
		if( xScale + mVelocityX / scroll_velocity_coefficient > scroll_sensitive && !isAutoScrollBack )
		{
			// speed = 2.0f - (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			//teapotXu_20130316 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( 1 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 1 , 0 ).setCallback( this );
			}
			//			tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			//					.ease(Quint.OUT).target(1, 0).setCallback(this);
			//teapotXu_20130316 add end
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle && !DefaultLayout.enable_doov_spec_customization )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT , 0 , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			doPrecious();
			Log.v( "test11111" , "上一页 " );
		}
		else if( ( xScale + mVelocityX / scroll_velocity_coefficient < ( -1 ) * scroll_sensitive ) && !( isAutoScrollBack ) && ( !( this instanceof Workspace3D ) || ( scoolif && this instanceof Workspace3D ) ) )
		{
			// speed = 2.0f + (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			//teapotXu_20130316 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( -1 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( -1 , 0 ).setCallback( this );
			}
			//			tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			//			.ease(Quint.OUT).target(-1, 0).setCallback(this);
			//teapotXu_20130316 add end			
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle && !DefaultLayout.enable_doov_spec_customization )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT , Gdx.graphics.getWidth() , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			Log.v( "test11111" , "下一页 " );
			doNext();
		}
		else
		{
			// speed = 0.5f + Math.abs((xScale + mVelocityX / 1000));
			// speed = speed > 2.0f ? 2.0f : speed;
			//teapotXu_20130316 add start: adding new effect
			if( DefaultLayout.external_applist_page_effect == true )
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( easeEquation ).target( 0 , 0 ).setCallback( this );
			}
			else
			{
				tween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 0 , 0 ).setCallback( this );
			}
			//			tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			//					.ease(Quint.OUT).target(0, 0).setCallback(this);
			//teapotXu_20130316 add end			
			Log.v( "test11111" , "不画页 " );
		}
		mVelocityX = 0;
		tween.start( View3DTweenAccessor.manager );
		if( crystalGroup != null )
			crystalGroup.stop( duration );
	}
	
	public void doNext()
	{
	}
	
	public void doPrecious()
	{
	}
	
	public float getFlingDuration()
	{
		float duration = 0;
		float distanceRatio = Math.min( 1f , Math.abs( xScale ) );
		float distance = Utils3D.getScreenWidth() / 2 + Utils3D.getScreenWidth() / 2 * distanceInfluenceForSnapDuration( distanceRatio );
		duration = Math.abs( distance / mVelocityX ) * 3;
		if( duration > DefaultLayout.page_tween_time )
		{
			duration = DefaultLayout.page_tween_time;
		}
		else if( duration < DefaultLayout.page_tween_time_min )
		{
			duration = DefaultLayout.page_tween_time_min;
		}
		return duration;
	}
	
	// We want the duration of the page snap animation to be influenced by the distance that
	// the screen has to travel, however, we don't want this duration to be effected in a
	// purely linear fashion. Instead, we use this method to moderate the effect that the distance
	// of travel has on the overall snap duration.
	float distanceInfluenceForSnapDuration(
			float f )
	{
		f -= 0.5f; // center the values about 0.
		f *= 0.3f * Math.PI / 2.0f;
		return (float)Math.sin( f );
	}
	
	public void startAutoEffect(
			boolean next )
	{
		float duration = DefaultLayout.page_tween_time;
		// The old value is Quint.OUT.
		final TweenEquation equation = ViscousFluid.INOUT;
		Log.d( "NPageBase" , "startAutoEffect next=" + next );
		//Log.d("launcher", "currentPage startAutoEffect isManualScrollTo="+isManualScrollTo);
		isManualScrollTo = false;
		if( !next )
		{
			// speed = 2.0f - (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			dragTween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( equation ).target( 1 , 0 ).setCallback( this );
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT , 0 , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		else
		{
			// speed = 2.0f + (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			dragTween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( equation ).target( -1 , 0 ).setCallback( this );
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT , Gdx.graphics.getWidth() , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
			//			} else {
			//				// speed = 0.5f + Math.abs((xScale + mVelocityX / 1000));
			//				// speed = speed > 2.0f ? 2.0f : speed;
			//				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			//						.ease(Quint.OUT).target(0, 0).setCallback(this);
		}
		mVelocityX = 0;
		dragTween.start( View3DTweenAccessor.manager );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		if( view_list.size() <= 1 || !canScroll )
			return super.fling( velocityX , velocityY );
		mVelocityX = velocityX;
		Log.i( "" , "########### mVelocityX=" + mVelocityX );
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		boolean needchangefous;
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
		needchangefous = !moving;
		canScroll = true;
		//这里加了一个判断是因为当最近使用也只有一页的时候，他的焦点会被Worksapce3D获取，在这里拦截了先。
		if( !( this instanceof RecentAppPage ) )
		{
			if( view_list.size() <= 1 )
				return super.onTouchDown( x , y , pointer );
		}
		// Log.d("launcher", "eee NPAGE onTouchDown:"+x);
		if( view_list.size() <= 1 )
			return super.onTouchDown( x , y , pointer );
		if( isManualScrollTo )
		{
			return true;
		}
		mVelocityX = 0;
		if( xScale > 0.5 )
		{
			page_index = preIndex();
			setDegree( xScale - 1f );
			changeEffect();
		}
		if( xScale < -0.5 )
		{
			page_index = nextIndex();
			setDegree( xScale + 1f );
			changeEffect();
		}
		if( needchangefous )
		{
			recoveryEffectType();
		}
		stopAutoEffect();
		isManualScrollTo = false;
		if( indicatorView != null )
		{
			if( indicatorView.indicatorStyle == IndicatorView.INDICATOR_STYLE_COMET )
			{
				indicatorView.stopTween();
			}
			else
			{
				Color color = indicatorView.getColor();
				color.a = 1.0f;
				indicatorView.setColor( color );
				indicatorView.stopTween();
			}
		}
		boolean res;
		if( needchangefous )
		{
			res = super.onTouchDown( x , y , pointer );
		}
		else
		{
			res = true;
		}
		if( pointer == 0 )
		{
			Log.i( "focus" , "npagebase" );
			requestFocus();
		}
		// Color c = indicatorView.getColor();
		// indicatorView.setColor(c.r, c.g, c.b, 0);
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.2f, 1.0f, 0, 0);
		return res;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		if( Math.abs( xScale ) < 0.00005f )
		{
			if( moving )
				initView();
			return super.multiTouch2( initialFirstPointer , initialSecondPointer , firstPointer , secondPointer );
		}
		else
			return true;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( Math.abs( xScale ) < 0.01f )
		{
			if( moving )
			{
				initView();
			}
			return super.onClick( x , y );
		}
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( Math.abs( xScale ) < 0.01f )
		{
			if( moving )
			{
				initView();
			}
			return super.onLongClick( x , y );
		}
		return true;
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT );
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT );
				stopParticle( ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING );
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		canScroll = false;
		// Log.d("launcher", "eee NPAGE onTouchUp:"+x);
		if( view_list.size() == 0 )
			return super.onTouchUp( x , y , pointer );
		if( isManualScrollTo && xScale != 0 )
		{
			return true;
		}
		if( view_list.size() == 1 )
		{
			releaseFocus();
			return super.onTouchUp( x , y , pointer );
		}
		if( indicatorView != null )
		{
			if( indicatorView.indicatorStyle == IndicatorView.INDICATOR_STYLE_COMET )
			{
				indicatorView.stopTween();
			}
			else
			{
				Color color = indicatorView.getColor();
				color.a = 1.0f;
				indicatorView.setColor( color );
				indicatorView.stopTween();
			}
		}
		// Color c = indicatorView.getColor();
		// indicatorView.setColor(c.r, c.g, c.b, 1);
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 0.0f, 0, 0).delay(1f);
		if( !iLoongLauncher.getInstance().d3dListener.d3d.mLeaveDesktop )
			startAutoEffect();
		releaseFocus();
		/* if workspace moving,not distribute TouchUp to children by zfshi */
		if( moving )
		{
			return false;
		}
		/* added by zfshi ended */
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( !canScroll )
			return false;
		// Log.d("launcher", "eee NPAGE scroll:"+x);
		if( view_list.size() <= 1 )
			return super.scroll( x , y , deltaX , deltaY );
		if( isManualScrollTo )
		{
			return true;
		}
		if( !moving && super.scroll( x , y , deltaX , deltaY ) )
		{
			// Log.d("launcher", "eee NPAGE scroll222:"+x);
			return true;
		}
		// setDegree(xScale - (-deltaX) / this.width);
		if( DefaultLayout.enable_DesktopIndicatorScroll && Root3D.scroll_indicator )
		{
			return false;
		}
		float yAmplify = deltaY * 1.3f;
		setDegree( xScale - ( -deltaX ) / this.width , yScale + ( -yAmplify ) / this.height );
		//teapotXu_20130319: add start
		if( DefaultLayout.external_applist_page_effect == true )
		{
			APageEase.setTouchUpAnimEffectStatus( false );
		}
		//teapotXu_20130319: add end
		updateEffect();
		/************************ added by zhenNan.ye begin ***************************/
		if( DefaultLayout.enable_particle && !DefaultLayout.enable_doov_spec_customization )
		{
			if( ParticleManager.particleManagerEnable )
			{
				if( ( Math.abs( x - last_x ) > 10 ) || ( Math.abs( y - last_y ) > 10 ) )
				{
					updateParticle( ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING , x , y );
					last_x = x;
					last_y = y;
				}
				else
				{
					pauseParticle( ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING );
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/
		if( crystalGroup != null )
		{
			if( NPageBase.this instanceof AppList3D || ( NPageBase.this instanceof Workspace3D ) )
				crystalGroup.start();
		}
		if( moving )
			return true;
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
		//teapotXu add start for scroll the indicator of AppList
		if( drawIndicator && getPageNum() > 1 )
		{
			if( indicatorView != null )
			{
				indicatorView.draw( batch , parentAlpha );
			}
		}
		if( crystalGroup != null )
		{
			if( this instanceof AppList3D && mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
			{
				AppList3D appList = (AppList3D)this;
				if( appList.appList_need_show_crystalGroup() )
				{
					crystalGroup.draw( batch , parentAlpha );
				}
			}
			else if( this instanceof Workspace3D && mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
			{
				crystalGroup.draw( batch , parentAlpha );
			}
		}
		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect( batch );
		/************************ added by zhenNan.ye end *************************/
	}
	
	@Override
	public void show()
	{
		if( indicatorView != null )
		{
			if( indicatorView.indicatorStyle == IndicatorView.INDICATOR_STYLE_COMET )
			{
				indicatorView.anim_time = 0;
			}
		}
		initView();
		super.show();
	}
	
	@Override
	public void hide()
	{
		if( indicatorView != null )
		{
			if( indicatorView.indicatorStyle == IndicatorView.INDICATOR_STYLE_COMET )
			{
				indicatorView.anim_time = 0;
			}
		}
		super.hide();
		this.stopAutoEffect();
		// setDegree(0f);
		setDegree( 0f , 0f );
		mVelocityX = 0;
		initView();
	}
	
	public int getPageNum()
	{
		return view_list.size();
	}
	
	public float getTotalOffset()
	{
		float ret = 0;
		int totalPage = getPageNum();
		// int tmp = (totalPage-1);
		// Log.v("jbc", "getTotalOffset xScale="+xScale+", ret="+ret);
		if( isManualScrollTo )
		{
			int destPage = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D().getHomePage();
			if( CellLayout3D.keyPadInvoked && DefaultLayout.keypad_event_of_focus )
			{
				destPage = CellLayout3D.nextPageIndex;
			}
			if( page_index != destPage )
				ret = 1.0f / ( totalPage - 1 ) * ( page_index * ( 1 - Math.abs( xScale ) ) + destPage * Math.abs( xScale ) );
		}
		else
		{
			if( page_index == 0 && xScale > 0 )
			{
				ret = ( ( (float)totalPage - 1 ) / ( totalPage - 1 ) ) * xScale;
			}
			else if( page_index == totalPage - 1 && xScale < 0 )
			{
				ret = ( (float)totalPage - 1 ) / ( totalPage - 1 ) + ( ( (float)totalPage - 1 ) / ( totalPage - 1 ) ) * xScale;
			}
			else
				ret = ( page_index - xScale ) / ( ( totalPage - 1 ) );
		}
		// Log.v("jbc", "getTotalOffset isManualScrollTo="+isManualScrollTo);
		// Log.v("jbc", "getTotalOffset ret="+ret);
		if( ret < 0 )
		{
			ret = 0;
		}
		else if( ret > ( totalPage - 1.0f ) / ( totalPage - 1 ) )
		{
			ret = ( totalPage - 1.0f ) / ( totalPage - 1 );
		}
		return ret;
	}
	
	public ArrayList<View3D> getViewList()
	{
		return this.view_list;
	}
	
	public View3D getCurrentView()
	{
		if( view_list.size() <= page_index )
			return null;
		return view_list.get( page_index );
	}
	
	public int getCurrentPage()
	{
		return page_index;
	}
	
	public void setCurrentPage(
			int index )
	{
		if( index < 0 )
			index = 0;
		else if( index >= view_list.size() )
			index = view_list.size() - 1;
		page_index = index;
		// setDegree(0f);
		initView();
		setDegree( 0f , 0f );
	}
	
	public void addScrollListener(
			PageScrollListener l )
	{
		scrollListeners.add( l );
	}
	
	// xiatian add start //Widget adaptation "com.android.gallery3d"
	public void removeScrollListener(
			PageScrollListener l )
	{
		if( scrollListeners.contains( l ) )
			scrollListeners.remove( l );
	}
	
	// xiatian add end
	protected void finishAutoEffect()
	{
		APageEase.is_scroll_anim_in_eidt_mode = false;
		// ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
		// ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
		// ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());
		// initData(cur_view);
		// initData(pre_view);
		// initData(next_view);
		recoveryEffectType();
		//teapotXu_20130325 add start : for effect
		if( DefaultLayout.external_applist_page_effect == true )
		{
			if( view_list != null && view_list.size() != 0 )
			{
				//Log.v( "NPageBase" , "NPageBase:view_list:" + view_list.size() );
				ViewGroup3D cur_view = (ViewGroup3D)view_list.get( page_index < 0 ? 0 : page_index );
				ViewGroup3D pre_view = (ViewGroup3D)view_list.get( ( preIndex() < 0 ? 0 : preIndex() ) );
				ViewGroup3D next_view = (ViewGroup3D)view_list.get( ( nextIndex() < 0 ? 0 : nextIndex() ) );
				if( cur_view.getChildrenDrawOrder() == true )
				{
					cur_view.setChildrenDrawOrder( false );
				}
				if( pre_view.getChildrenDrawOrder() == true )
				{
					pre_view.setChildrenDrawOrder( false );
				}
				if( next_view.getChildrenDrawOrder() == true )
				{
					next_view.setChildrenDrawOrder( false );
				}
			}
			else
			{
				Log.v( "NPageBase" , "NPageBase:view_list:" + view_list.size() );
				Log.v( "NPageBase" , "NPageBase:view_list error" );
			}
		}
		//teapotXu_20130325 add end : for effect		 
	}
	
	public void recoverPageSequence()
	{
		for( int i = 0 ; i < view_list.size() && i < children.size() ; i++ )
		{
			children.set( i , view_list.get( i ) );
		}
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( type == TweenCallback.COMPLETE && source == tween )
		{
			if( isManualScrollTo )
			{
				setCurrentPage( ScrollDestPage );
				isManualScrollTo = false;
			}
			else
			{
				if( xScale <= -1f )
				{
					setCurrentPage( nextIndex() );
				}
				if( xScale >= 1f )
				{
					setCurrentPage( preIndex() );
				}
			}
			//			this.addView(crystalGroup.getFace(Face.Front));
			//			this.addView(crystalGroup.getFace(Face.Verso));
			initView();
			tween = null;
			finishAutoEffect();
			recoverPageSequence();
		}
		else if( type == TweenCallback.COMPLETE && source == dragTween )
		{
			if( xScale <= -1f )
			{
				setCurrentPage( nextIndex() );
			}
			if( xScale >= 1f )
			{
				setCurrentPage( preIndex() );
			}
			initView();
			dragTween = null;
			finishAutoEffect();
			recoverPageSequence();
			Root3D.isDragAutoEffect = false;
		}
		//xiatian add start	//EffectPreview
		else if( ( DefaultLayout.enable_effect_preview ) && ( type == TweenCallback.COMPLETE && source == mPreviewTween ) )
		{
			if( xScale <= -1f )
			{
				setCurrentPage( nextIndex() );
			}
			if( xScale >= 1f )
			{
				setCurrentPage( preIndex() );
			}
			initView();
			finishAutoEffect();
			recoverPageSequence();
			Root3D.isDragAutoEffect = false;
			if( mPreviewFirst )
			{
				startPreviewEffect( false );
			}
			else
			{
				mPreviewTween = null;
				autoEffect = false;
			}
		}
		//xiatian add end
		super.onEvent( type , source );
	}
	
	/************************ added by zhenNan.ye begin *************************/
	private void drawParticleEffect(
			SpriteBatch batch )
	{
		if( DefaultLayout.enable_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				drawParticle( batch );
			}
		}
	}
	
	/************************ added by zhenNan.ye end ***************************/
	public class IndicatorView extends View3D
	{
		
		// NinePatch focus, unfocus;
		public final int s4_page_indicator_bg_height = R3D.s4_page_indicator_bg_height;
		public final int s4_page_indicator_scroll_width = R3D.s4_page_indicator_scroll_width;
		public final int s4_page_indicator_scroll_height = R3D.s4_page_indicator_scroll_height;
		public final int s4_page_indicator_number_bg_size = R3D.s4_page_indicator_number_bg_size;
		public final int s4_page_indicator_number_w = s4_page_indicator_number_bg_size / 2;
		public final int s4_page_indicator_number_x_offset = R3D.s4_page_indicator_number_x_offset;
		private TextureRegion nineIndicatorRegion = null;
		public TextureRegion unselectedIndicator = null;
		public TextureRegion selectedIndicator = null;
		private NinePatch nineIndicator = null;
		private TextureRegion[] indicatorNumber;
		public float indicatorSize = (float)R3D.page_indicator_size;
		public int indicatorFocusW = R3D.page_indicator_focus_w;
		public int indicatorNormalW = R3D.page_indicator_normal_w;
		public int indicatorFocusH = R3D.page_indicator_focus_h;
		public int indicatorNormalH = R3D.page_indicator_normal_h;
		private int indicatorStyle = R3D.page_indicator_style;
		public int indicatorTotalSize = R3D.page_indicator_total_size;
		public static final int INDICATOR_STYLE_ANDROID4 = 0;
		public static final int INDICATOR_STYLE_S3 = 1;
		public static final int INDICATOR_STYLE_S2 = 2;
		// xiatian add start //add new page_indicator_style
		public static final int INDICATOR_STYLE_COCO_AND_ANDROID4 = 3;
		public static final int INDICATOR_STYLE_COMET = 4;
		public static final int INDICATOR_STYLE_QUICKSEARCH = 5;
		private TextureRegion unselectedIndicatorXian = null;
		private TextureRegion selectedIndicatorXian = null;
		public float indicatorSizeXian = (float)R3D.page_indicator_size;
		// xiatian add end
		private Tween myTween;
		private Tween indicatorTween;
		private float indicatorAlpha = 0;
		public float scroll_degree = 0;
		public static final int CLICK_TIME = 500;
		public static final int CLICK_MOVE = 40;
		private long downTime = 0;
		private float downX = 0;
		private float downY = 0;
		public Tween s4indicatorClickTween;
		private TextureRegion bgIndicator_s4Region = null;
		private NinePatch bgIndicator_s4 = null;
		private TextureRegion scrollIndicator_s4 = null;
		private TextureRegion indicatorNumberBg_s4 = null;
		private TextureRegion[] indicatorNumber_s4;
		/** comet style begin*/
		protected int anim_time = 0;
		private long drawTime = 0;
		float old_degree = 0;
		public static final int BEI = 1000;
		private final int ANIM_TIME = 4000 * BEI;
		//		public List<Particle> particlelist = Collections.synchronizedList( new ArrayList<Particle>() );
		private TextureRegion selectedIndicator_bg = null;
		private TextureRegion selectedIndicator1 = null;
		private TextureRegion selectedIndicator2 = null;
		
		/** comet style end*/
		public IndicatorView(
				String name ,
				int indicatorStyle )
		{
			super( name );
			this.indicatorStyle = indicatorStyle;
			if( PageIndicator3D.getInstance() != null )
			{
				pageIndicator3D = PageIndicator3D.getInstance();
			}
			setSize( R3D.getInteger( "page_indicator_width" ) == 0 ? Utils3D.getScreenWidth() : R3D.getInteger( "page_indicator_width" ) , R3D.getInteger( "page_indicator_height" ) );
			//teapotXu add start for scrolling the indicator of AppList
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , NPageBase.this.y );
			}
			else
			{
				setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , R3D.applist_indicator_y + R3D.appbar_height + NPageBase.this.y );
			}
			//teapotXu add end
			if( this.indicatorStyle == INDICATOR_STYLE_S3 )
			{
				if( nineIndicator == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
					Texture t = new BitmapTexture( bmIndicator );
					nineIndicatorRegion = new TextureRegion( t );
					nineIndicator = new NinePatch( nineIndicatorRegion , 6 , 6 , 6 , 6 );
					if( !iLoongLauncher.releaseTexture )
						bmIndicator.recycle();
				}
			}
			else if( this.indicatorStyle == INDICATOR_STYLE_S2 )
			{
				if( selectedIndicator == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_s2.png" );
					Texture t = new BitmapTexture( bmIndicator );
					selectedIndicator = new TextureRegion( t );
					if( !iLoongLauncher.releaseTexture )
						bmIndicator.recycle();
					indicatorNumber = new TextureRegion[9];
					for( int i = 0 ; i < 9 ; i++ )
					{
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_" + ( i + 1 ) + ".png" );
						t = new BitmapTexture( bmIndicator );
						indicatorNumber[i] = new TextureRegion( t );
						if( !iLoongLauncher.releaseTexture )
							bmIndicator.recycle();
					}
				}
			}
			else if( this.indicatorStyle == INDICATOR_STYLE_COMET )
			{
				if( selectedIndicator == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pageIndicator/default_indicator_current2.png" );
					Texture t = new BitmapTexture( bmIndicator );
					selectedIndicator = new TextureRegion( t );
					Bitmap bmIndicator2 = ThemeManager.getInstance().getBitmap( "theme/pageIndicator/default_indicator_current1.png" );
					Texture t2 = new BitmapTexture( bmIndicator2 );
					selectedIndicator1 = new TextureRegion( t2 );
					Bitmap bmIndicator3 = ThemeManager.getInstance().getBitmap( "theme/pageIndicator/default_indicator_current3.png" );
					Texture t3 = new BitmapTexture( bmIndicator3 );
					selectedIndicator2 = new TextureRegion( t3 );
					Bitmap bmIndicator1 = ThemeManager.getInstance().getBitmap( "theme/pageIndicator/default_indicator_current_bg.png" );
					Texture t1 = new BitmapTexture( bmIndicator1 );
					selectedIndicator_bg = new TextureRegion( t1 );
					if( !iLoongLauncher.releaseTexture )
					{
						bmIndicator.recycle();
					}
				}
				indicatorFocusH = R3D.page_indicator_focus_w / 4;
				setSize( Utils3D.getScreenWidth() , indicatorFocusH * 1.1f );
				setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , this.height - indicatorFocusH );
			}
			else if( this.indicatorStyle == INDICATOR_STYLE_QUICKSEARCH )
			{
				if( unselectedIndicator == null )
				{
					if( DefaultLayout.isScaleBitmap )
					{
						unselectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator.png" , indicatorNormalW , indicatorNormalH , false );
					}
					else
					{
						Bitmap bmIndicator = null;
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator.png" , false );
						Texture t = new BitmapTexture( bmIndicator );
						unselectedIndicator = new TextureRegion( t );
						bmIndicator.recycle();
					}
				}
				if( selectedIndicator == null )
				{
					if( DefaultLayout.isScaleBitmap )
					{
						selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , (int)indicatorFocusW , indicatorFocusH , false );
					}
					else
					{
						Bitmap bmIndicator = null;
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" , false );
						Texture t = new BitmapTexture( bmIndicator );
						selectedIndicator = new TextureRegion( t );
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
						Bitmap bmIndicator = null;
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator.png" );
						Texture t = new BitmapTexture( bmIndicator );
						unselectedIndicator = new TextureRegion( t );
						bmIndicator.recycle();
					}
				}
				if( selectedIndicator == null )
				{
					if( DefaultLayout.isScaleBitmap )
					{
						selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , (int)indicatorFocusW , indicatorFocusH );
					}
					else
					{
						Bitmap bmIndicator = null;
						bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
						Texture t = new BitmapTexture( bmIndicator );
						selectedIndicator = new TextureRegion( t );
						bmIndicator.recycle();
					}
				}
				if( DefaultLayout.enable_AppListIndicatorScroll )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/bg_indicator_s4.png" );
					Texture t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					bgIndicator_s4Region = new TextureRegion( t );
					bgIndicator_s4 = new NinePatch( bgIndicator_s4Region , 7 , 7 , 0 , 0 );
					bmIndicator.recycle();
					if( DefaultLayout.isScaleBitmap && pageIndicator3D != null && pageIndicator3D.scrollIndicator_s4 != null )
					{
						scrollIndicator_s4 = pageIndicator3D.scrollIndicator_s4;
						Log.v( "" , "Indicator scrollIndicator_s4 not new" );
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
						if( DefaultLayout.isScaleBitmap && pageIndicator3D != null && pageIndicator3D.indicatorNumberBg_s4 != null )
						{
							indicatorNumberBg_s4 = pageIndicator3D.indicatorNumberBg_s4;
							indicatorNumber_s4 = pageIndicator3D.indicatorNumber_s4;
							Log.v( "" , "Indicator indicatorNumberBg_s4 not new" );
						}
						else
						{
							bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicatorNumberBg_s4.png" );
							t = new BitmapTexture( bmIndicator );
							t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
							indicatorNumberBg_s4 = new TextureRegion( t );
							bmIndicator.recycle();
							indicatorNumber_s4 = new TextureRegion[10];
							for( int i = 0 ; i < 10 ; i++ )
							{
								bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_s4_" + i + ".png" );
								t = new BitmapTexture( bmIndicator );
								t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
								indicatorNumber_s4[i] = new TextureRegion( t );
								bmIndicator.recycle();
							}
						}
					}
				}
				//teapotXu add end
			}
		}
		
		public void onThemeChanged()
		{
			//style change,need dispose!!!
			indicatorStyle = R3D.page_indicator_style;
			indicatorSize = (float)R3D.page_indicator_size;
			indicatorFocusW = R3D.page_indicator_focus_w;
			indicatorNormalW = R3D.page_indicator_normal_w;
			indicatorFocusH = R3D.page_indicator_focus_h;
			indicatorNormalH = R3D.page_indicator_normal_h;
			indicatorTotalSize = R3D.page_indicator_total_size;
			setSize( R3D.getInteger( "page_indicator_width" ) == 0 ? Utils3D.getScreenWidth() : R3D.getInteger( "page_indicator_width" ) , R3D.getInteger( "page_indicator_height" ) );
			//			setPosition((Utils3D.getScreenWidth() - this.width)/2, R3D.applist_indicator_y + NPageBase.this.y);
			//teapotXu add start for scrolling the indicator of AppList
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , NPageBase.this.y < 0 ? 0 : NPageBase.this.y );
			}
			else
			{
				setPosition( ( Utils3D.getScreenWidth() - this.width ) / 2 , R3D.applist_indicator_y + R3D.appbar_height + ( NPageBase.this.y < 0 ? 0 : NPageBase.this.y ) );
			}
			//teapotXu add end			
			if( indicatorStyle == INDICATOR_STYLE_S3 )
			{
				if( nineIndicator == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
					BitmapTexture t = new BitmapTexture( bmIndicator );
					t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					nineIndicatorRegion = new TextureRegion( t );
					nineIndicator = new NinePatch( nineIndicatorRegion , 6 , 6 , 6 , 6 );
					bmIndicator.recycle();
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
					( (BitmapTexture)nineIndicatorRegion.getTexture() ).changeBitmap( bmIndicator , true );
					nineIndicatorRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
					nineIndicator = new NinePatch( nineIndicatorRegion , 6 , 6 , 6 , 6 );
				}
			}
			else if( indicatorStyle == INDICATOR_STYLE_S2 )
			{
				if( selectedIndicator == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_s2.png" );
					BitmapTexture t = new BitmapTexture( bmIndicator );
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
					//					if( DefaultLayout.isScaleBitmap && pageIndicator3D != null && pageIndicator3D.unselectedIndicator != null )
					//					{
					//						unselectedIndicator = pageIndicator3D.unselectedIndicator;
					//					}
					if( DefaultLayout.isScaleBitmap )
					{
						unselectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator.png" , indicatorNormalW , indicatorNormalH );
					}
					else
					{
						//unselectedIndicator = R3D.getTextureRegion("default_indicator");
						Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator.png" );
						BitmapTexture t = new BitmapTexture( bmIndicator );
						t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
						//TextureRegion region = R3D.findRegion("default_indicator");
						//region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
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
						selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , (int)indicatorFocusW , indicatorFocusH );
					}
					else
					{
						Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
						BitmapTexture t = new BitmapTexture( bmIndicator );
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
						selectedIndicator = Tools.getTextureByPicName( "theme/pack_source/default_indicator_current.png" , (int)indicatorFocusW , indicatorFocusH );
					}
					else
					{
						Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current.png" );
						BitmapTexture t = (BitmapTexture)selectedIndicator.getTexture();
						t.changeBitmap( bmIndicator , true );
						t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
					}
				}
				// xiatian add start //add new page_indicator_style
				if( unselectedIndicatorXian == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_xian.png" );
					Texture t = new BitmapTexture( bmIndicator );
					unselectedIndicatorXian = new TextureRegion( t );
					if( !iLoongLauncher.releaseTexture )
						bmIndicator.recycle();
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_xian.png" );
					( (BitmapTexture)unselectedIndicatorXian.getTexture() ).changeBitmap( bmIndicator , true );
				}
				if( selectedIndicatorXian == null )
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_xian.png" );
					Texture t = new BitmapTexture( bmIndicator );
					selectedIndicatorXian = new TextureRegion( t );
					if( !iLoongLauncher.releaseTexture )
						bmIndicator.recycle();
				}
				else
				{
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_xian.png" );
					( (BitmapTexture)selectedIndicatorXian.getTexture() ).changeBitmap( bmIndicator , true );
				}
				// xiatian add end
				//teapotXu add start
				//scroll indicator
				if( DefaultLayout.enable_AppListIndicatorScroll )
				{
					if( bgIndicator_s4 == null )
					{
						Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/bg_indicator_s4.png" );
						Texture t = new BitmapTexture( bmIndicator );
						t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
						bgIndicator_s4Region = new TextureRegion( t );
						bgIndicator_s4 = new NinePatch( bgIndicator_s4Region , 7 , 7 , 0 , 0 );
						bmIndicator.recycle();
						if( DefaultLayout.isScaleBitmap && pageIndicator3D != null && pageIndicator3D.scrollIndicator_s4 != null )
						{
							scrollIndicator_s4 = pageIndicator3D.scrollIndicator_s4;
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
							if( DefaultLayout.isScaleBitmap && pageIndicator3D != null && pageIndicator3D.indicatorNumberBg_s4 != null )
							{
								indicatorNumberBg_s4 = pageIndicator3D.indicatorNumberBg_s4;
								indicatorNumber_s4 = pageIndicator3D.indicatorNumber_s4;
							}
							else
							{
								bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicatorNumberBg_s4.png" );
								t = new BitmapTexture( bmIndicator );
								t.setFilter( TextureFilter.Linear , TextureFilter.Linear );
								indicatorNumberBg_s4 = new TextureRegion( t );
								bmIndicator.recycle();
								indicatorNumber_s4 = new TextureRegion[10];
								for( int i = 0 ; i < 10 ; i++ )
								{
									bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_s4_" + i + ".png" );
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
						if( DefaultLayout.isScaleBitmap && pageIndicator3D != null && pageIndicator3D.indicatorNumberBg_s4 != null )
						{
							indicatorNumberBg_s4 = pageIndicator3D.indicatorNumberBg_s4;
							indicatorNumber_s4 = pageIndicator3D.indicatorNumber_s4;
						}
						else
						{
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
									bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/indicator_num_s4_" + i + ".png" );
									( (BitmapTexture)indicatorNumber_s4[i].getTexture() ).changeBitmap( bmIndicator , true );
									indicatorNumber_s4[i].getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
								}
							}
						}
					}
				}
				//teapotXu add end
			}
		}
		
		public void show()
		{
			super.show();
			indicatorAlpha = 1;
			stopTween();
			myTween = startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.4f , 1 , 0 , 0 ).setCallback( this );
		}
		
		public void showEx()
		{
			super.show();
			indicatorAlpha = 1;
			stopTween();
			myTween = startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.0f , 1 , 0 , 0 ).setCallback( this );
		}
		
		@Override
		public void setUser(
				float value )
		{
			indicatorAlpha = value;
		}
		
		@Override
		public float getUser()
		{
			return indicatorAlpha;
		}
		
		public void setAlpha(
				float value )
		{
			indicatorAlpha = value;
		}
		
		public void setIndicatorTween(
				Tween tween )
		{
			indicatorTween = tween;
		}
		
		public Tween getIndicatorTween()
		{
			return indicatorTween;
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
		}
		
		@Override
		public void onEvent(
				int type ,
				BaseTween source )
		{
			if( type == TweenCallback.COMPLETE && source == myTween )
			{
				myTween = null;
				this.color.a = 1;
				super.show();
				finishAutoEffect();
				return;
			}
			if( type == TweenCallback.COMPLETE && source == indicatorTween )
			{
				indicatorTween = null;
				indicatorAlpha = 0;
				return;
			}
			//teapotXu add start for scroll the indicator of AppList
			if( DefaultLayout.enable_AppListIndicatorScroll && type == TweenCallback.COMPLETE && source == s4indicatorClickTween )
			{
				( (AppList3D)viewParent ).recoverPageSequence();
				( (AppList3D)viewParent ).initView();
				( (AppList3D)viewParent ).setDegree( 0 );
				this.releaseFocus();
				s4indicatorClickTween = null;
				return;
			}
			//teapotXu add end
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			if( !visible )
			{
				return;
			}
			int size = 0;
			float focusWidth = 0;
			int currentPage = 0;
			int nextPage = 0;
			float degree = 0;
			int signDirection = 0;
			float normalH = 0 , focusH = 0 , normalY = 0 , focusY = 0;
			float startX = 0;
			batch.setColor( color.r , color.g , color.b , color.a );
			Color old = batch.getColor();
			float oldA = old.a;
			if( indicatorStyle != INDICATOR_STYLE_COMET )
			{
				this.y += indicatorPaddingBottom;
				size = getIndicatorPageCount();
				focusWidth = indicatorSize * size;
				currentPage = getIndicatorPageIndex();
				nextPage = currentPage;
				degree = xScale;
				signDirection = degree >= 0 ? 1 : -1;
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
				startX = this.x + ( this.width - focusWidth ) / 2.0f;
			}
			switch( indicatorStyle )
			{
				case INDICATOR_STYLE_ANDROID4:
					if( selectedIndicator != null && unselectedIndicator != null )
					{
						focusWidth = indicatorTotalSize;
						indicatorSize = focusWidth / size;
						startX = this.x + ( this.width - focusWidth ) / 2.0f;
						//focusH = normalH = indicatorNormalW;
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = 0;// this.y + (this.height - normalH)/2.0f;
						focusY = 0;// this.y + (this.height - focusH)/2.0f;
						float focus_offset_x = 0.0f;
						// old.a = oldA;
						// batch.setColor(old);
						// batch.draw(unselectedIndicator, startX, (int)normalY,
						// focusWidth, normalH);
						focus_offset_x = -indicatorSize * degree;
						old.a = oldA * indicatorAlpha;
						batch.setColor( old.r , old.g , old.b , old.a );
						if( !isManualScrollTo )
						{
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
					}
					break;
				case INDICATOR_STYLE_S3:
					if( nineIndicator != null )
					{
						//focusH = normalH = indicatorNormalW;
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						focusY = this.y + ( this.height - focusH ) / 2.0f;
						for( int i = 0 ; i < size ; i++ )
						{
							if( !isManualScrollTo )
							{
								if( i == currentPage )
								{
									old.a = oldA;
									old.a *= 1 - Math.abs( degree * 0.5f );
									batch.setColor( old.r , old.g , old.b , old.a );
									float w = indicatorNormalW + ( 1 - Math.abs( degree ) ) * ( indicatorFocusW - indicatorNormalW );
									float offset_x = ( indicatorSize - w ) / 2.0f;
									nineIndicator.draw( batch , startX + i * indicatorSize + offset_x , focusY , w , focusH );
								}
								else if( i == nextPage )
								{
									old.a = oldA;
									old.a *= Math.abs( degree * 0.5f ) + 0.5f;
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
						//					normalH = indicatorNormalW;
						//					focusH = indicatorFocusW;
						normalH = indicatorNormalH;
						focusH = indicatorFocusH;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						old.a = oldA;
						batch.setColor( old.r , old.g , old.b , old.a );
						for( int i = 0 ; i < size ; i++ )
						{
							if( !isManualScrollTo )
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
							else
							{
								float offset_x = ( indicatorSize - indicatorNormalW ) / 2.0f;
								batch.draw( selectedIndicator , startX + i * indicatorSize + offset_x , normalY , indicatorNormalW , normalH );
							}
						}
					}
					break;
				// xiatian add start //add new page_indicator_style
				case INDICATOR_STYLE_COCO_AND_ANDROID4:
				case INDICATOR_STYLE_QUICKSEARCH:
					if( size * indicatorSize >= this.width )
					{
						// ANDROID4
						if( unselectedIndicatorXian == null )
						{
							Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_xian.png" );
							Texture t = new BitmapTexture( bmIndicator );
							unselectedIndicatorXian = new TextureRegion( t );
							if( !iLoongLauncher.releaseTexture )
								bmIndicator.recycle();
						}
						if( selectedIndicatorXian == null )
						{
							Bitmap bmIndicator = ThemeManager.getInstance().getBitmap( "theme/pack_source/default_indicator_current_xian.png" );
							Texture t = new BitmapTexture( bmIndicator );
							selectedIndicatorXian = new TextureRegion( t );
							if( !iLoongLauncher.releaseTexture )
								bmIndicator.recycle();
						}
						if( selectedIndicatorXian != null && unselectedIndicatorXian != null )
						{
							// Log.v("xiatian408830131","ANDROID4  ----  size*indicatorSize="+(size*indicatorSize));
							// Log.v("xiatian408830131","ANDROID4  ----  this.width="+(this.width));
							focusWidth = indicatorTotalSize;
							indicatorSizeXian = focusWidth / size;
							startX = this.x + ( this.width - focusWidth ) / 2.0f;
							//focusH = normalH = indicatorNormalW;
							normalH = indicatorNormalH;
							focusH = indicatorFocusH;
							normalY = 0;// this.y + (this.height - normalH)/2.0f;
							focusY = 0;// this.y + (this.height - focusH)/2.0f;
							float focus_offset_x = 0.0f;
							// old.a = oldA;
							// batch.setColor(old);
							// batch.draw(unselectedIndicator, startX, (int)normalY,
							// focusWidth, normalH);
							focus_offset_x = -indicatorSize * degree;
							old.a = oldA * indicatorAlpha;
							batch.setColor( old.r , old.g , old.b , old.a );
							if( !isManualScrollTo )
							{
								if( currentPage * indicatorSizeXian + focus_offset_x < 0 )
								{
									batch.draw( selectedIndicatorXian , startX + currentPage * indicatorSizeXian , focusY , indicatorSizeXian + focus_offset_x , focusH );
									batch.draw( selectedIndicatorXian , this.width - startX + focus_offset_x , focusY , -focus_offset_x , focusH );
								}
								else if( startX + currentPage * indicatorSizeXian + focus_offset_x > this.width - startX - indicatorSizeXian )
								{
									batch.draw( selectedIndicatorXian , startX + currentPage * indicatorSizeXian + focus_offset_x , focusY , indicatorSizeXian - focus_offset_x , focusH );
									batch.draw( selectedIndicatorXian , startX , focusY , focus_offset_x , focusH );
								}
								else
								{
									batch.draw( selectedIndicatorXian , startX + currentPage * indicatorSizeXian + focus_offset_x , focusY , indicatorSizeXian , focusH );
								}
							}
						}
					}
					else
					{
						//Coco
						if( DefaultLayout.enable_AppListIndicatorScroll && Root3D.scroll_indicator )
						{
							normalY = this.y + R3D.applist_indicator_y + 0/*(this.height - s4_page_indicator_bg_height)/2.0f*/;
							focusY = this.y + R3D.applist_indicator_y + ( s4_page_indicator_bg_height - s4_page_indicator_scroll_height ) / 2;
							old.a = oldA;
							batch.setColor( old.r , old.g , old.b , old.a );
							bgIndicator_s4.draw( batch , (int)startX , (int)normalY , focusWidth , s4_page_indicator_bg_height );
							startX = startX + indicatorSize / 2 - s4_page_indicator_scroll_width / 2 + scroll_degree * indicatorSize;
							batch.draw( scrollIndicator_s4 , (int)startX , (int)focusY , s4_page_indicator_scroll_width , s4_page_indicator_scroll_height );
							if( DefaultLayout.pageIndicator_scroll_num_shown )
							{
								batch.draw(
										indicatorNumberBg_s4 ,
										(int)( ( this.width - s4_page_indicator_number_bg_size ) / 2.0f ) ,
										(int)( this.y + R3D.applist_indicator_y + s4_page_indicator_number_bg_size * 1.5 ) ,
										s4_page_indicator_number_bg_size ,
										s4_page_indicator_number_bg_size );
								int pageNumber = (int)( scroll_degree + 0.5f ) + 1;
								if( pageNumber < 10 )
								{
									batch.draw(
											indicatorNumber_s4[pageNumber] ,
											(int)( ( this.width - s4_page_indicator_number_w ) / 2.0f ) ,
											(int)( this.y + R3D.applist_indicator_y + s4_page_indicator_number_bg_size * 1.5 ) ,
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
											(int)( this.y + R3D.applist_indicator_y + s4_page_indicator_number_bg_size * 1.5 ) ,
											s4_page_indicator_number_w ,
											s4_page_indicator_number_bg_size );
									batch.draw(
											indicatorNumber_s4[single_digit] ,
											(int)( this.width / 2.0f - s4_page_indicator_number_x_offset ) ,
											(int)( this.y + R3D.applist_indicator_y + s4_page_indicator_number_bg_size * 1.5 ) ,
											s4_page_indicator_number_w ,
											s4_page_indicator_number_bg_size );
								}
							}
						}
						else
						{
							// COCO
							if( selectedIndicator != null && unselectedIndicator != null )
							{
								// Log.v("xiatian408830131","COCO  ----  size*indicatorSize="+(size*indicatorSize));
								// Log.v("xiatian408830131","COCO  ----  this.width="+(this.width));
								normalH = indicatorNormalH;
								//focusH = indicatorFocusW;
								focusH = indicatorFocusH;
								if( DefaultLayout.enable_AppListIndicatorScroll )
								{
									normalY = this.y + R3D.applist_indicator_y;//(this.height - normalH) / 2.0f;
									focusY = this.y + R3D.applist_indicator_y + ( normalH - focusH ) / 2.0f;//(this.height - focusH) / 2.0f;
								}
								else
								{
									normalY = this.y + 0;//(this.height - normalH) / 2.0f;
									focusY = this.y + ( normalH - focusH ) / 2.0f;//(this.height - focusH) / 2.0f;
								}
								float normal_offset_x = ( indicatorSize - indicatorNormalW ) / 2.0f;
								float focus_offset_x = ( indicatorSize - indicatorFocusW ) / 2.0f;
								for( int i = 0 ; i < size ; i++ )
								{
									if( !isManualScrollTo )
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
									}
									else
									{
										old.a = oldA;
										batch.setColor( old.r , old.g , old.b , old.a );
									}
									batch.draw( unselectedIndicator , startX + i * indicatorSize + normal_offset_x , normalY , indicatorNormalW , normalH );
									if( i == currentPage )
									{
										old.a = oldA;
										old.a *= 1 - Math.abs( degree );
										batch.setColor( old.r , old.g , old.b , old.a );
										if( !isManualScrollTo )
											batch.draw( selectedIndicator , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
									}
									else if( i == nextPage )
									{
										old.a = oldA;
										old.a *= Math.abs( degree );
										batch.setColor( old.r , old.g , old.b , old.a );
										if( !isManualScrollTo )
											batch.draw( selectedIndicator , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
									}
								}
							}
						}
					}
					break;
				// xiatian add end
				case INDICATOR_STYLE_COMET:
					if( selectedIndicator != null && anim_time > 0 )
					{
						//						for( int i = 0 ; i < particlelist.size() ; i++ )
						//						{
						//							particlelist.get( i ).draw( batch , old );
						//						}
						old.a = oldA;
						old.a *= 1.0f * anim_time / ANIM_TIME;
						batch.setColor( old.r , old.g , old.b , old.a );
						PointF[] location = getLocation( xScale );
						if( location != null )
							for( int i = 0 ; i < location.length ; i++ )
							{
								float indicatorFocusW1 = indicatorFocusW;
								float indicatorFocusW2 = 0;
								if( location[i].x < x - 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
								{
									indicatorFocusW2 = (float)( x - 10 * (float)Utils3D.getScreenWidth() / 720 - location[i].x );
								}
								if( indicatorFocusW2 > indicatorFocusW - indicatorFocusH * 2 )
								{
									continue;
								}
								if( location[i].x + indicatorFocusW1 > x + width + 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
								{
									indicatorFocusW1 = (float)( x + width - location[i].x + 10 * (float)Utils3D.getScreenWidth() / 720 );
								}
								if( indicatorFocusW1 < indicatorFocusH * 2 )
								{
									continue;
								}
								batch.draw( selectedIndicator1 , location[i].x + indicatorFocusW2 , location[i].y , indicatorFocusH , indicatorFocusH );
								batch.draw(
										selectedIndicator ,
										location[i].x + indicatorFocusH + indicatorFocusW2 ,
										location[i].y ,
										indicatorFocusW1 - indicatorFocusH * 2 - indicatorFocusW2 ,
										indicatorFocusH );
								batch.draw( selectedIndicator2 , location[i].x + indicatorFocusW1 - indicatorFocusH , location[i].y , indicatorFocusH , indicatorFocusH );
							}
						long tick = ( System.currentTimeMillis() - drawTime ) * BEI;
						anim_time -= tick;
						//						List<Particle> particlelists = new ArrayList<Particle>();
						//						for( Particle p : particlelist )
						//						{
						//							p.time -= tick;
						//							if( p.time <= 0 || p.time > p.TIME )
						//							{
						//								particlelists.add( p );
						//							}
						//						}
						//						if( particlelists.size() > 0 )
						//							particlelist.removeAll( particlelists );
						Gdx.graphics.requestRendering();
					}
					//					else
					//					{
					//						particlelist.retainAll( particlelist );
					//					}
					drawTime = System.currentTimeMillis();
					break;
				default:
					if( selectedIndicator != null && unselectedIndicator != null )
					{
						normalH = indicatorNormalW;
						focusH = indicatorFocusW;
						normalY = this.y + ( this.height - normalH ) / 2.0f;
						focusY = this.y + ( this.height - focusH ) / 2.0f;
						float normal_offset_x = ( indicatorSize - indicatorNormalW ) / 2.0f;
						float focus_offset_x = ( indicatorSize - indicatorFocusW ) / 2.0f;
						for( int i = 0 ; i < size ; i++ )
						{
							if( !isManualScrollTo )
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
							}
							else
							{
								old.a = oldA;
								batch.setColor( old.r , old.g , old.b , old.a );
							}
							batch.draw( unselectedIndicator , startX + i * indicatorSize + normal_offset_x , normalY , indicatorNormalW , normalH );
							if( i == currentPage )
							{
								old.a = oldA;
								old.a *= 1 - Math.abs( degree );
								batch.setColor( old.r , old.g , old.b , old.a );
								if( !isManualScrollTo )
								{
									batch.draw( selectedIndicator , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
							}
							else if( i == nextPage )
							{
								old.a = oldA;
								old.a *= Math.abs( degree );
								batch.setColor( old.r , old.g , old.b , old.a );
								if( !isManualScrollTo )
								{
									batch.draw( selectedIndicator , startX + i * indicatorSize + focus_offset_x , focusY , indicatorFocusW , focusH );
								}
							}
						}
					}
					break;
			}
			old.a = oldA;
			batch.setColor( old.r , old.g , old.b , old.a );
			if( indicatorStyle != INDICATOR_STYLE_COMET )
			{
				this.y -= indicatorPaddingBottom;
			}
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			if( pointer != 0 )
				return true;
			if( s4indicatorClickTween != null )
				return true;
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				if( xScale != 0 || pointer != 0 )
				{
					return false;
				}
				downTime = System.currentTimeMillis();
				downX = x;
				downY = y;
				requestFocus();
			}
			return false;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			if( pointer != 0 )
				return true;
			if( s4indicatorClickTween != null )
				return true;
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				this.releaseFocus();
				if( Root3D.scroll_indicator )
				{
					if( xScale == 0 )
					{
						( (AppList3D)viewParent ).setDegree( 0.0f );
					}
					( (AppList3D)viewParent ).startAutoEffect();
					Root3D.scroll_indicator = false;
					return true;
				}
				else if( System.currentTimeMillis() - downTime < CLICK_TIME && Math.abs( x - downX ) < CLICK_MOVE && Math.abs( y - downY ) < CLICK_MOVE )
				{
					int pageNum = ( (AppList3D)viewParent ).getIndicatorPageCount();
					float focusWidth = indicatorSize * pageNum;
					float startX = this.x + ( this.width - focusWidth ) / 2.0f;
					if( x >= startX && x < this.width - startX )
					{
						float temp_scroll_degree = getTempDegree( x , R3D.page_indicator_size );
						int curDesPageIndex = (int)( temp_scroll_degree + 0.5 );
						int totalDesPageIndex = ( (AppList3D)viewParent ).getTotalPageIndex( curDesPageIndex );
						if( page_index == totalDesPageIndex )
							return true;
						this.requestFocus();
						setUser2( (float)page_index );
						if( ( (AppList3D)viewParent ).temp_mType == -1 )
						{
							int type = ( (AppList3D)viewParent ).getEffectType();
							boolean random = ( (AppList3D)viewParent ).getRandom();
							( (AppList3D)viewParent ).setEffectType( 0 );
							if( random )
							{
								( (AppList3D)viewParent ).setTempEffectType( 1 );
							}
							else
							{
								( (AppList3D)viewParent ).setTempEffectType( type );
							}
						}
						s4indicatorClickTween = startTween( View3DTweenAccessor.USER2 , Cubic.OUT , 0.3f , (float)totalDesPageIndex , 0f , 0f ).setCallback( this );
						return true;
					}
				}
			}
			return false;
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			if( s4indicatorClickTween != null )
				return true;
			if( xScale != 0 )
				return false;
			downTime = 0;
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				if( !Root3D.scroll_indicator )
				{
					if( ( (AppList3D)viewParent ).temp_mType == -1 )
					{
						int type = ( (AppList3D)viewParent ).getEffectType();
						boolean random = ( (AppList3D)viewParent ).getRandom();
						( (AppList3D)viewParent ).setEffectType( 0 );
						if( random )
						{
							( (AppList3D)viewParent ).setTempEffectType( 1 );
						}
						else
						{
							( (AppList3D)viewParent ).setTempEffectType( type );
						}
					}
					Root3D.scroll_indicator = true;
					this.requestFocus();
				}
				scroll_degree = getTempDegree( x , R3D.page_indicator_size );
				int curDesPageIndex = (int)( scroll_degree + 0.5 );
				int totalDesPageIndex = ( (AppList3D)viewParent ).getTotalPageIndex( curDesPageIndex );
				( (AppList3D)viewParent ).setCurrentPageOnly( totalDesPageIndex );
				float temp_degree = scroll_degree - (int)scroll_degree;
				if( temp_degree < 0.5 )
				{
					( (AppList3D)viewParent ).setDegreeOnly( -temp_degree );
				}
				else
				{
					( (AppList3D)viewParent ).setDegreeOnly( 1 - temp_degree );
				}
				( (AppList3D)viewParent ).updateEffectS4();
				return true;
			}
			return false;
		}
		
		@Override
		public boolean scroll(
				float x ,
				float y ,
				float deltaX ,
				float deltaY )
		{
			if( s4indicatorClickTween != null )
				return true;
			if( DefaultLayout.enable_AppListIndicatorScroll )
			{
				Object obj = this.getStage();
				if( obj != null && obj instanceof Desktop3D )
				{
					Desktop3D desktop = (Desktop3D)obj;
					if( desktop.getFocus() != null && desktop.getFocus() instanceof AppList3D )
						return false;
				}
				if( DefaultLayout.enable_new_particle )
				{
					Desktop3DListener.root.particleSetRepeatPosition( this.x , this.y , x , y );
				}
				if( !Root3D.scroll_indicator )
				{
					if( ( (AppList3D)viewParent ).temp_mType == -1 )
					{
						int type = ( (AppList3D)viewParent ).getEffectType();
						boolean random = ( (AppList3D)viewParent ).getRandom();
						( (AppList3D)viewParent ).setEffectType( 0 );
						if( random )
						{
							( (AppList3D)viewParent ).setTempEffectType( 1 );
						}
						else
						{
							( (AppList3D)viewParent ).setTempEffectType( type );
						}
					}
					Root3D.scroll_indicator = true;
					this.requestFocus();
				}
				scroll_degree = getTempDegree( x , R3D.page_indicator_size );
				//				Log.v("jbc", "qwq scroll_degree="+scroll_degree);
				int curDesPageIndex = (int)( scroll_degree + 0.5 );
				int totalDesPageIndex = ( (AppList3D)viewParent ).getTotalPageIndex( curDesPageIndex );
				//				Log.v("jbc", "qwq totalDesPageIndex="+totalDesPageIndex);
				( (AppList3D)viewParent ).setCurrentPageOnly( totalDesPageIndex );
				float temp_degree = scroll_degree - (int)scroll_degree;
				if( temp_degree < 0.5 )
				{
					( (AppList3D)viewParent ).setDegreeOnly( -temp_degree );
				}
				else
				{
					( (AppList3D)viewParent ).setDegreeOnly( 1 - temp_degree );
				}
				( (AppList3D)viewParent ).updateEffectS4();
				return true;
			}
			return false;
		}
		
		public float getTempDegree(
				float touch_x ,
				int indicator_space )
		{
			int screenWidth = Utils3D.getScreenWidth();
			int pageNum = ( (AppList3D)viewParent ).getIndicatorPageCount();
			int total_w = indicator_space * ( pageNum - 1 );
			float start_x = ( screenWidth - total_w ) / 2;
			float degree = (float)( touch_x - start_x ) / indicator_space;
			if( degree < 0 )
				return 0;
			if( degree > pageNum - 1 )
				return pageNum - 1;
			return degree;
		}
		
		@Override
		public void setUser2(
				float value )
		{
			super.setUser2( value );
			( (AppList3D)viewParent ).setCurrentPageOnly( (int)( value + 0.5 ) );
			float temp_degree = value - (int)value;
			if( temp_degree < 0.5 )
			{
				( (AppList3D)viewParent ).setDegreeOnly( -temp_degree );
			}
			else
			{
				( (AppList3D)viewParent ).setDegreeOnly( 1 - temp_degree );
			}
			( (AppList3D)viewParent ).updateEffectS4();
		}
		
		/** comet style begin*/
		public void pariticle_handle()
		{
			if( !visible )
			{
				return;
			}
			anim_time = ANIM_TIME;
			//			particle_handle();
		}
		
		class Particle
		{
			
			PointF location = null;
			public final static int Time = 400 * BEI;
			long time = Time;
			long TIME = Time;
			
			public void draw(
					SpriteBatch batch ,
					Color old )
			{
				if( location.x + indicatorFocusW > x + width + 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
				{
					return;
				}
				if( location.x < x - 10 * (float)Utils3D.getScreenWidth() / 720 && width < Utils3D.getScreenWidth() )
				{
					return;
				}
				if( time > 0 )
				{
					float oldA = old.a;
					// if(time>=(int)TIME*0.5)
					// {
					// old.a *= 1.0f*(0.8+0.2*(time-(int)TIME*0.5)/(TIME*0.5));
					// }else{
					// old.a *= 1.0f*(0.8*(time)/(TIME*0.5));
					// }
					old.a *= 0.2f * time / (float)( TIME );
					// old.a *=0.30f;
					batch.setColor( old.r , old.g , old.b , old.a );
					batch.draw( selectedIndicator_bg , location.x , location.y , indicatorFocusW , indicatorFocusH );
					old.a = oldA;
					batch.setColor( old.r , old.g , old.b , old.a );
					// Log.d("Particle","draw ,time,old.a="+time+","+old.a);
				}
			}
		}
		
		//		private void particle_handle()
		//		{
		//			PointF[] ps = getLocation( xScale );
		//			if( ps != null && Math.abs( xScale ) != 1.0f )
		//			{
		//				for( int j = 0 ; j < ps.length ; j++ )
		//				{
		//					Particle pi = new Particle();
		//					pi.location = ps[j];
		//					addParticle( pi );
		//				}
		//			}
		//		}
		//		public void addParticle(
		//				Particle pi )
		//		{
		//			if( particlelist.size() <= 2000 )
		//			{
		//				particlelist.add( pi );
		//				Log.d( "addParticle" , "pi.location.x  particlelist.size()" + pi.location.x + "," + particlelist.size() );
		//			}
		//		}
		//		public void print_particle()
		//		{
		//			try
		//			{
		//				Log.d( "print_particle" , "particlelist.size()=" + particlelist.size() );
		//				for( int i = 0 ; i < particlelist.size() ; i++ )
		//				{
		//					Log.d( "print_particle" , "particlelist[" + i + "]  x=" + particlelist.get( i ).location.x + ",y=" + particlelist.get( i ).location.y + ",time=" + particlelist.get( i ).time );
		//				}
		//			}
		//			catch( Exception e )
		//			{
		//				e.printStackTrace();
		//			}
		//		}
		//		
		public PointF[] getLocation(
				float degree )
		{
			PointF[] location = null;
			int pageNum = getIndicatorPageCount();
			int size = getIndicatorPageCount();
			if( size == 0 )
				return location;
			indicatorFocusW = (int)( width / size );
			indicatorSize = indicatorFocusW;
			int currentPage = getIndicatorPageIndex();
			float focusWidth = width;
			int nextPage = getIndicatorPageIndex();
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
			// Log.d("getLocation","nextPage,currentPage,degree"+nextPage+","+currentPage+","+degree);
			float focusH , focusY;
			float startX = this.x + ( this.width - focusWidth ) / 2.0f;
			focusH = indicatorFocusH;
			focusY = this.y + ( this.height - focusH ) / 2.0f;
			float focus_offset_x = ( indicatorSize - indicatorFocusW ) / 2.0f;
			if( currentPage == pageNum - 1 && nextPage == 0 )
			{
				location = new PointF[2];
				location[0] = new PointF();
				location[0].x = startX + ( -degree - 1 ) * indicatorSize + focus_offset_x;
				location[0].y = focusY;
				location[1] = new PointF();
				location[1].x = startX + ( currentPage - degree ) * indicatorSize + focus_offset_x;
				location[1].y = focusY;
			}
			else if( currentPage == 0 && nextPage == pageNum - 1 )
			{
				location = new PointF[2];
				location[0] = new PointF();
				location[0].x = startX + ( -degree ) * indicatorSize + focus_offset_x;
				location[0].y = focusY;
				location[1] = new PointF();
				location[1].x = startX + ( nextPage + 1 - degree ) * indicatorSize + focus_offset_x;
				location[1].y = focusY;
			}
			else
			{
				if( nextPage > currentPage )
				{
					location = new PointF[1];
					location[0] = new PointF();
					location[0].x = startX + ( nextPage - 1 - degree ) * indicatorSize + focus_offset_x;
					location[0].y = focusY;
				}
				else if( nextPage == currentPage )
				{
					location = new PointF[1];
					location[0] = new PointF();
					location[0].x = startX + nextPage * indicatorSize + focus_offset_x;
					location[0].y = focusY;
				}
				else
				{
					location = new PointF[1];
					location[0] = new PointF();
					location[0].x = startX + ( nextPage + 1 - degree ) * indicatorSize + focus_offset_x;
					location[0].y = focusY;
				}
			}
			return location;
		}
		/** comet style end*/
	}
	
	//xiatian add start	//EffectPreview
	public void startPreviewEffect(
			boolean isFirst )
	{
		float duration = 0.75f;
		float delayDuration = 0.5f;
		//Log.d("launcher", "currentPage startAutoEffect isManualScrollTo="+isManualScrollTo);
		isManualScrollTo = false;
		if( NPageBase.this instanceof AppList3D || ( NPageBase.this instanceof Workspace3D ) )
		{
			if( mTypelist.get( mType ) == APageEase.COOLTOUCH_EFFECT_CRYSTAL )
			{
				if( crystalGroup == null )
				{
					crystalGroup = new CrystalCore( "Crystal" , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() - ( R3D.appbar_height + R3D.applist_indicator_y ) );
				}
			}
		}
		if( isFirst )
		{
			mPreviewFirst = true;
			mPreviewTween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( -1 , 0 ).delay( delayDuration ).setCallback( this );
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT , Gdx.graphics.getWidth() , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		else
		{
			mPreviewFirst = false;
			mPreviewTween = Tween.to( this , View3DTweenAccessor.POS_XY , duration ).ease( Quint.OUT ).target( 1 , 0 ).setCallback( this );
			/************************ added by zhenNan.ye begin ***************************/
			if( DefaultLayout.enable_particle )
			{
				if( ParticleManager.particleManagerEnable )
				{
					startParticle( ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT , 0 , 0 );
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		}
		mVelocityX = 0;
		mPreviewTween.start( View3DTweenAccessor.manager );
		if( crystalGroup != null )
		{
			crystalGroup.syncView( this );
			if( isFirst )
			{
				crystalGroup.start( delayDuration );
				crystalGroup.stop( duration + delayDuration );
			}
			else
			{
				crystalGroup.start();
				crystalGroup.stop( duration );
			}
		}
	}
	//xiatian add end	
}
