package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.PageSelect3D.ViewInfoHolder;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PageEdit3D extends ViewGroup3D implements DropTarget3D , DragSource3D
{
	
	private iLoongLauncher launcher;
	private WallpaperManager mWallpaperManager;
	private PageContainer3D pageContainer;
	private List<View3D> selectPageList;
	private ArrayList<View3D> dragList;
	private View3D dragObj;
	private View3D addIconPage;
	private View3D deletedPage;
	private int toDeleteIndex = -1;
	private boolean draging = false;
	private int oldEmptyPosition = -1;
	private int emptyPosition = -1;
	public int homePage = 0;
	private float PAGE_PADDING_TOP = 15f;
	private float PAGE_PADDING_BOTTOM = 80f;
	private float PAGE_PADDING_LEFT = 0f;
	private float pageWidth;
	private float pageHeight;
	private float pagePaddingHorizontal = 0f;
	private float pagePaddingVertical = 5f;
	private float pageViewWidth;
	private float pageViewHeight;
	private float pageScaleX;
	private float pageScaleY;
	private int pageNum;
	private Timeline timeline;
	private float animDelay = 0.00f;
	private boolean animatingShow = false;
	private boolean animatingHide = false;
	private long downTime = 0;
	private boolean animatingEdit = false;
	private boolean animatingDrop = false;
	private boolean animatingEnter = false;
	private boolean multiTouch = false;
	private int pageSource = PAGE_SOURCE_SELECT;
	public static final int ZOOM_DISTANCE = 100;
	public static final int CLICK_TIME = 500;
	public static float PAGE_HOME_PADDING_BOTTOM = 18f;
	public static int PAGE_COUNT_MAX = 9;
	public static final int PAGE_COUNT_MIN = DefaultLayout.default_workspace_pagecount_min;
	public static final int PAGE_SOURCE_USER = 0;
	public static final int PAGE_SOURCE_SELECT = 1;
	public static final int PAGE_SOURCE_WORKSPACE = 2;
	public static final int BG_9_WIDTH = 70;
	public static final int BG_9_HEIGHT = 88;
	
	public PageEdit3D(
			String name )
	{
		super( name );
		float screenWidth = Utils3D.getScreenWidth();
		float screenScale = screenWidth / 480 < 1 ? screenWidth / 480 : 1;
		PAGE_PADDING_BOTTOM *= screenScale;
		pageWidth = ( width - 2 * PAGE_PADDING_LEFT - 2 * pagePaddingHorizontal ) / 3;
		pageHeight = ( height - PAGE_PADDING_TOP - PAGE_PADDING_BOTTOM - 2 * pagePaddingVertical ) / 3;
		PAGE_HOME_PADDING_BOTTOM *= screenScale;
		transform = true;
		PAGE_COUNT_MAX = DefaultLayout.default_workspace_pagecount_max;
	}
	
	public void onThemeChanged()
	{
	}
	
	public void setPageContainer(
			PageContainer3D pageContainer )
	{
		this.pageContainer = pageContainer;
	}
	
	public void setLauncher(
			iLoongLauncher launcher )
	{
		this.launcher = launcher;
		mWallpaperManager = WallpaperManager.getInstance( launcher );
	}
	
	public void setPageList(
			List<View3D> pageList ,
			int pageSource )
	{
		this.selectPageList = pageList;
		this.pageSource = pageSource;
		timeline = Timeline.createParallel();
		animatingShow = true;
		pageNum = pageList.size();
		for( int i = 0 ; i < pageList.size() ; i++ )
		{
			addEditPage( pageList.get( i ) , i , pageSource );
		}
		addEditPage( null , pageList.size() , pageSource );
		timeline.setCallback( this ).start( View3DTweenAccessor.manager );
		multiTouch = false;
	}
	
	private void addEditPage(
			View3D view ,
			int index ,
			int pageSource )
	{
		ViewGroup3D page = new EditPageView( "edit_page" );
		//		setLayoutParams(index,page,false);	//xiatian del	//fix bug:in Edit Page View, the content in child views deformation
		if( view == null )
		{
			view = new View3D( "add_page" );
			view.setBackgroud( PageContainer3D.pageBg );
			view.setColor( Color.DARK_GRAY );
			view.setSize( pageViewWidth , pageViewHeight );
			view.setOrigin( 0 , 0 );
			//xiatian add start	//fix bug:in Edit Page View, the content in child views deformation
			float mScaleX = pageWidth / view.width;
			float mScaleY = pageHeight / view.height;
			float mScale = mScaleX < mScaleY ? mScaleX : mScaleY;
			if( mScaleX < mScaleY )
			{
				pageHeight = mScale * view.height;
			}
			else if( mScaleX > mScaleY )
			{
				pageWidth = mScale * view.width;
			}
			setLayoutParams( index , page , false );
			//xiatian add end
			view.setScale( pageWidth / view.width , pageHeight / view.height );
			page.addView( view );
			view.show();
			View3D addIcon = new View3D( "add_icon" , R3D.getTextureRegion( "page-add-icon" ) );
			addIcon.x = ( pageWidth - addIcon.width ) / 2;
			addIcon.y = ( pageHeight - addIcon.height ) / 2;
			addIcon.setOrigin( 0 , 0 );
			page.addView( addIcon );
			addIcon.show();
			page.setColor( new Color( 1f , 1f , 1f , 0f ) );
			timeline.push( page.obtainTween( View3DTweenAccessor.OPACITY , Linear.INOUT , 0.3f , 1f , 0 , 0 ).delay( pageNum * animDelay + 0.3f ) );
			addIconPage = page;
		}
		else
		{
			//xiatian add start	//fix bug:in Edit Page View, the content in child views deformation
			float mScaleX = pageWidth / view.width;
			float mScaleY = pageHeight / view.height;
			float mScale = mScaleX < mScaleY ? mScaleX : mScaleY;
			if( mScaleX < mScaleY )
			{
				pageHeight = mScale * view.height;
			}
			else if( mScaleX > mScaleY )
			{
				pageWidth = mScale * view.width;
			}
			setLayoutParams( index , page , false );
			//xiatian add end
			if( pageSource == PAGE_SOURCE_SELECT )
			{
				float delay = index * animDelay;
				float tmpX = page.x;
				float tmpY = page.y;
				page.x = view.x + view.getParent().x;
				page.y = view.y + view.getParent().y;
				page.rotation = view.rotation;//-360;
				page.setOrigin( view.originX , view.originY );
				page.setRotationVector( 0 , 1 , 0 );
				timeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , tmpX , tmpY , 0 ).delay( delay ) );
				timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , 0 , 0 , 0 ).delay( delay ) );
				timeline.push( view.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , pageWidth / view.width , pageHeight / view.height , 0 ).delay( delay ) );
			}
			else if( pageSource == PAGE_SOURCE_WORKSPACE )
			{
				if( index == pageContainer.getSelectedIndex() )
				{
					timeline.push( view.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , pageWidth / view.width , pageHeight / view.height , 0 ) );
					View3D cell = ( (ViewGroup3D)view ).findView( "celllayout" );
					if( cell != null )
					{
						float scaleX = cell.getScaleX();
						float scaleY = cell.getScaleY();
						float x = cell.x;
						float y = cell.y;
						cell.setScale( 1 , 1 );
						cell.x = -page.x;
						cell.y = -page.y;
						//						float tmpScaleX = view.scaleX;
						//						float tmpScaleY = view.scaleY;
						//						cell.x -= (1-tmpScaleX)*cell.x;
						//						cell.y -= (1-tmpScaleY)*cell.y;
						timeline.push( cell.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , x , y , 0 ) );
						timeline.push( cell.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , scaleX , scaleY , 0 ) );
					}
				}
				else
				{
					page.setOrigin( -page.x , 0 );
					page.setRotation( -90 );
					page.setRotationVector( 0 , 1 , 0 );
					timeline.push( page.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.3f , 0 , 0 , 0 ).delay( 0.05f * index ) );
					view.setScale( pageWidth / view.width , pageHeight / view.height );
				}
			}
			else
				view.setScale( pageWidth / view.width , pageHeight / view.height );
			view.setOrigin( 0 , 0 );
			view.setRotation( 0 );
			view.setPosition( 0 , 0 );
			( (ViewInfoHolder)view.getTag() ).parent = view.getParent();//save parent
			page.addView( view );
			view.show();
			view.touchable = false;
			float screenWidth = Utils3D.getScreenWidth();
			float screenScale = screenWidth / 480 < 1 ? screenWidth / 480 : 1;
			View3D deleteIcon = new View3D( "delete_icon" , R3D.getTextureRegion( "page-edit3" ) );
			deleteIcon.setSize( screenScale * deleteIcon.width , screenScale * deleteIcon.height );
			deleteIcon.x = pageWidth - deleteIcon.width * 1.1f;
			deleteIcon.y = pageHeight - deleteIcon.height * 1.15f;
			deleteIcon.setColor( new Color( 1 , 1 , 1 , 0.6f ) );
			page.addView( deleteIcon );
			if( pageNum <= PAGE_COUNT_MIN )
				deleteIcon.hide();
			else
				deleteIcon.show();
			View3D homeIcon = null;
			if( index == homePage )
			{
				homeIcon = new View3D( "home_icon" , R3D.getTextureRegion( "page-edit2b" ) );
			}
			else
				homeIcon = new View3D( "home_icon" , R3D.getTextureRegion( "page-edit2" ) );
			//homeIcon.region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			homeIcon.setSize( screenScale * homeIcon.width , screenScale * homeIcon.height );
			if( Utils3D.getScreenWidth() < 480 )
			{
				homeIcon.y = PAGE_HOME_PADDING_BOTTOM + 5 * screenScale;
			}
			else
			{
				homeIcon.y = PAGE_HOME_PADDING_BOTTOM;
			}
			homeIcon.x = ( pageWidth - homeIcon.width ) / 2;
			homeIcon.show();
			page.addView( homeIcon );
			if( pageSource != PAGE_SOURCE_USER )
			{
				deleteIcon.setColor( new Color( 1 , 1 , 1 , 0f ) );
				homeIcon.setColor( new Color( 1 , 1 , 1 , 0f ) );
				timeline.push( deleteIcon.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , 0.5f , 0.6f , 0 , 0 ) );
				timeline.push( homeIcon.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , 0.5f , 1f , 0 , 0 ) );
			}
			pageViewWidth = view.width;
			pageViewHeight = view.height;
			pageScaleX = pageWidth / view.width;
			pageScaleY = pageHeight / view.height;
		}
		if( index < PAGE_COUNT_MAX )
		{
			addViewAt( index , page );
		}
	}
	
	private void setLayoutParams(
			int index ,
			View3D page ,
			boolean anim )
	{
		page.width = pageWidth;
		page.height = pageHeight;
		int canvasHeiht = Utils3D.getScreenHeight();
		float x = 0;
		float y = 0;
		if( DefaultLayout.butterfly_style )
		{
			if( pageNum == 1 )
			{
				if( index == 0 )
				{
					x = ( Utils3D.getScreenWidth() - pageWidth ) / 2;
					y = ( canvasHeiht + R3D.page_edit_ygap234 ) / 2;
				}
				else if( index == 1 )
				{
					x = ( Utils3D.getScreenWidth() - pageWidth ) / 2;
					y = ( canvasHeiht - R3D.page_edit_ygap234 ) / 2 - pageHeight;
				}
			}
			else if( pageNum > 1 && pageNum < 5 )
			{
				if( index == 0 )
				{
					x = ( Utils3D.getScreenWidth() - R3D.page_edit_xgap234 ) / 2 - pageWidth;
					y = ( canvasHeiht + R3D.page_edit_ygap234 ) / 2;
				}
				else if( index == 1 )
				{
					x = ( Utils3D.getScreenWidth() + R3D.page_edit_xgap234 ) / 2;
					y = ( canvasHeiht + R3D.page_edit_ygap234 ) / 2;
				}
				else if( index == 2 )
				{
					x = ( Utils3D.getScreenWidth() - pageWidth ) / 2 - R3D.page_edit_xgap234 - pageWidth;
					y = ( canvasHeiht - R3D.page_edit_ygap234 ) / 2 - pageHeight;
				}
				else if( index == 3 )
				{
					x = ( Utils3D.getScreenWidth() - pageWidth ) / 2;
					y = ( canvasHeiht - R3D.page_edit_ygap234 ) / 2 - pageHeight;
				}
				else if( index == 4 )
				{
					x = ( Utils3D.getScreenWidth() + pageWidth ) / 2 + R3D.page_edit_xgap234;
					y = ( canvasHeiht - R3D.page_edit_ygap234 ) / 2 - pageHeight;
				}
			}
			else if( pageNum >= 5 && pageNum <= 7 )
			{
				if( index == 0 )
				{
					x = ( Utils3D.getScreenWidth() - R3D.page_edit_xgap234 ) / 2 - pageWidth;
					y = ( canvasHeiht + pageHeight ) / 2 + R3D.page_edit_ygap567;
				}
				else if( index == 1 )
				{
					x = ( Utils3D.getScreenWidth() + R3D.page_edit_xgap234 ) / 2;
					y = ( canvasHeiht + pageHeight ) / 2 + R3D.page_edit_ygap567;
				}
				else if( index == 2 )
				{
					x = ( Utils3D.getScreenWidth() - pageWidth ) / 2 - R3D.page_edit_xgap234 - pageWidth;
					y = ( canvasHeiht - pageHeight ) / 2;
				}
				else if( index == 3 )
				{
					x = ( Utils3D.getScreenWidth() - pageWidth ) / 2;
					y = ( canvasHeiht - pageHeight ) / 2;
				}
				else if( index == 4 )
				{
					x = ( Utils3D.getScreenWidth() + pageWidth ) / 2 + R3D.page_edit_xgap234;
					y = ( canvasHeiht - pageHeight ) / 2;
				}
				else if( index == 5 )
				{
					x = ( Utils3D.getScreenWidth() - R3D.page_edit_xgap234 ) / 2 - pageWidth;
					y = ( canvasHeiht - pageHeight ) / 2 - R3D.page_edit_ygap567 - pageHeight;
				}
				else if( index == 6 )
				{
					x = ( Utils3D.getScreenWidth() + R3D.page_edit_xgap234 ) / 2;
					y = ( canvasHeiht - pageHeight ) / 2 - R3D.page_edit_ygap567 - pageHeight;
				}
			}
			page.setOrigin( pageWidth / 2 , pageHeight / 2 );
		}
		else
		{
			x = PAGE_PADDING_LEFT + index % 3 * ( pageWidth + pagePaddingHorizontal );
			y = Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - ( PAGE_PADDING_TOP + index / 3 * ( pageHeight + pagePaddingVertical ) + pageHeight );
			page.setOrigin( pageWidth / 2 , pageHeight / 2 );
		}
		if( anim )
		{
			page.stopTween();
			page.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , x , y , 0 ).setCallback( this );
		}
		else
		{
			page.x = x;
			page.y = y;
		}
	}
	
	private void onAppendPage()
	{
		if( isAnimating() )
			return;
		View3D newSelectPage = pageContainer.onAppendSelectPage();
		ViewInfoHolder tmp = pageContainer.pageSelect.new ViewInfoHolder();
		tmp.getInfo( newSelectPage );
		newSelectPage.setTag( tmp );
		pageNum++;
		selectPageList.add( newSelectPage );
		addEditPage( newSelectPage , pageNum - 1 , PAGE_SOURCE_USER );
		View3D newEditPage = this.getChildAt( pageNum - 1 );
		newEditPage.setScale( 0.0f , 0.0f );
		timeline = Timeline.createSequence();
		animatingEdit = true;
		//teapotXu add start
		if( DefaultLayout.butterfly_style == true )
		{
			if( pageNum == 5 || pageNum == 2 )
			{
				for( int j = 0 ; j < getChildCount() ; j++ )
				{
					View3D child = this.getChildAt( j );
					setLayoutParams( j , child , true );
				}
			}
		}
		//teapotXu add end
		if( pageNum == PAGE_COUNT_MAX )
		{
			timeline.push( addIconPage.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.3f , 0 , 0 , 0 ).setCallback( this ) );
			timeline.push( newEditPage.obtainTween( View3DTweenAccessor.SCALE_XY , Back.OUT , 0.3f , 1 , 1 , 0 ) );
		}
		else
		{
			float x = addIconPage.x;
			float y = addIconPage.y;
			setLayoutParams( pageNum , addIconPage , false );
			timeline.push( addIconPage.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , addIconPage.x , addIconPage.y , 0 ) );
			addIconPage.x = x;
			addIconPage.y = y;
			timeline.push( newEditPage.obtainTween( View3DTweenAccessor.SCALE_XY , Back.OUT , 0.3f , 1 , 1 , 0 ) );
		}
		timeline.setCallback( this ).start( View3DTweenAccessor.manager );
		if( pageNum == PAGE_COUNT_MIN + 1 )
		{
			for( int j = 0 ; j < this.getChildCount() ; j++ )
			{
				ViewGroup3D page = (ViewGroup3D)this.getChildAt( j );
				View3D deleteIcon = page.findView( "delete_icon" );
				if( deleteIcon != null )
				{
					deleteIcon.show();
				}
			}
		}
	}
	
	private void onDeletePage(
			int i )
	{
		if( isAnimating() )
			return;
		if( pageNum == PAGE_COUNT_MIN )
		{
			SendMsgToAndroid.sendOurToastMsg( R3D.pageselect_canNotDeletePage );
			return;
		}
		toDeleteIndex = i;
		if( ( (ViewGroup3D)pageContainer.celllayoutList.get( i ) ).getChildCount() > 0 )
		{
			SendMsgToAndroid.sendDeletePageMsg();
		}
		else
			exeDeletePage();
	}
	
	public void exeDeletePage()
	{
		pageNum--;
		if( toDeleteIndex <= pageContainer.getSelectedIndex() )
		{
			if( toDeleteIndex == pageContainer.getSelectedIndex() )
			{
				int j = toDeleteIndex - 1;
				if( toDeleteIndex == 0 )
					j = 1;
				View3D v = selectPageList.get( j );
				v.setBackgroud( PageContainer3D.pageSelectedBg );
				v.setColor( Color.WHITE );
			}
			if( pageContainer.getSelectedIndex() > 0 )
				pageContainer.pageSelect.currentIndex--;
		}
		pageContainer.removeCellLayout( toDeleteIndex , false );
		selectPageList.remove( toDeleteIndex );
		deletedPage = getChildAt( toDeleteIndex );
		deletedPage.stopTween();
		deletedPage.setOrigin( deletedPage.width / 2 , deletedPage.height / 2 );
		this.removeView( deletedPage );
		if( toDeleteIndex <= homePage )
		{
			if( homePage > 0 )
				setHomePage( homePage - 1 );
			else
				setHomePage( homePage );
		}
		animatingEdit = true;
		deletedPage.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.2f , 0 , 0 , 0 ).setCallback( this );
		//teapotXu add start
		int sortPageIndex = toDeleteIndex;
		if( DefaultLayout.butterfly_style == true )
		{
			if( pageNum == 4 || pageNum == 1 )
				sortPageIndex = 0;
		}
		//teapotXu add end
		for( int j = sortPageIndex ; j < getChildCount() ; j++ )
		{
			View3D child = this.getChildAt( j );
			setLayoutParams( j , child , true );
		}
		if( pageNum == PAGE_COUNT_MAX - 1 )
		{
			setLayoutParams( selectPageList.size() , addIconPage , false );
			addIconPage.scaleX = 0;
			addIconPage.scaleY = 0;
			addViewAt( selectPageList.size() , addIconPage );
			addIconPage.stopTween();
			addIconPage.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.2f , 1 , 1 , 0 );
		}
		this.addView( deletedPage );
		if( pageNum == PAGE_COUNT_MIN )
		{
			for( int j = 0 ; j < this.getChildCount() ; j++ )
			{
				ViewGroup3D page = (ViewGroup3D)this.getChildAt( j );
				View3D deleteIcon = page.findView( "delete_icon" );
				if( deleteIcon != null )
				{
					deleteIcon.hide();
				}
			}
		}
		toDeleteIndex = -1;
	}
	
	private void setHomePage(
			int i )
	{
		if( isAnimating() )
			return;
		homePage = i;
		for( int j = 0 ; j < this.getChildCount() ; j++ )
		{
			ViewGroup3D page = (ViewGroup3D)this.getChildAt( j );
			View3D homeIcon = page.findView( "home_icon" );
			if( homeIcon != null )
			{
				if( j == homePage )
				{
					homeIcon.region = R3D.getTextureRegion( "page-edit2b" );
				}
				else
				{
					homeIcon.region = R3D.getTextureRegion( "page-edit2" );
				}
			}
		}
		pageContainer.setHomePage( i );
	}
	
	private void sortPage(
			int newEmptyPosition )
	{
		if( newEmptyPosition > emptyPosition )
		{
			for( int i = emptyPosition ; i < newEmptyPosition ; i++ )
			{
				setLayoutParams( i , this.getChildAt( i ) , true );
			}
		}
		else
		{
			for( int i = emptyPosition - 1 ; i >= newEmptyPosition ; i-- )
			{
				setLayoutParams( i + 1 , this.getChildAt( i ) , true );
			}
		}
		emptyPosition = newEmptyPosition;
	}
	
	private void enterPage(
			ViewGroup3D editPage )
	{
		ViewGroup3D selectPage = (ViewGroup3D)editPage.findView( "page_select" );
		if( selectPage != null )
		{
			View3D cell = selectPage.findView( "celllayout" );
			if( cell == null )
				return;
			selectPage.setBackgroud( null );
			View3D homeIcon = editPage.findView( "home_icon" );
			if( homeIcon != null )
				editPage.removeView( homeIcon );
			View3D deleteIcon = editPage.findView( "delete_icon" );
			if( deleteIcon != null )
				editPage.removeView( deleteIcon );
			animatingEnter = true;
			timeline = Timeline.createParallel();
			pageContainer.setSelectedIndex( pageContainer.celllayoutList.indexOf( cell ) );
			//teapotXu add start for mv wallpaper's config menu
			if( DefaultLayout.enable_configmenu_for_move_wallpaper )
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
				IBinder token = launcher.getWindow().getCurrentFocus().getWindowToken();
				if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == true )
				{
					float targetOffset = (float)( pageContainer.celllayoutList.indexOf( cell ) ) / ( pageContainer.celllayoutList.size() - 1 );
					if( token != null )
						mWallpaperManager.setWallpaperOffsets( token , targetOffset , 0 );
				}
				else
				{
					if( token != null )
						mWallpaperManager.setWallpaperOffsets( token , 0.0f , 0 );
				}
			}
			else
			{
				float targetOffset = (float)( pageContainer.celllayoutList.indexOf( cell ) ) / ( pageContainer.celllayoutList.size() - 1 );
				IBinder token = launcher.getWindow().getCurrentFocus().getWindowToken();
				if( token != null )
					mWallpaperManager.setWallpaperOffsets( token , targetOffset , 0 );
			}
			//teapotXu add end
			float tmpScaleX = selectPage.scaleX;
			float tmpScaleY = selectPage.scaleY;
			selectPage.setScale( 1 , 1 );
			cell.x -= ( 1 - tmpScaleX ) * cell.x;
			cell.y -= ( 1 - tmpScaleY ) * cell.y;
			cell.setScale( tmpScaleX * cell.scaleX , tmpScaleY * cell.scaleY );
			timeline.push( cell.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , 1 , 1 , 0 ).delay( 0.3f ) );
			timeline.push( cell.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , -editPage.x - selectPage.x , -editPage.y - selectPage.y , 0 ).delay( 0.3f ) );
			int len = this.getChildCount() - 1;
			for( int i = len ; i >= 0 ; i-- )
			{
				ViewGroup3D child = (ViewGroup3D)getChildAt( i );
				if( child != editPage )
				{
					child.setOrigin( -child.x , 0 );
					child.setRotationVector( 0 , 1 , 0 );
					timeline.push( child.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.3f , -90 , 0 , 0 ).delay( 0.05f * i ) );
				}
			}
			timeline.setCallback( this ).start( View3DTweenAccessor.manager );
		}
	}
	
	private int calculateEmptyPosition(
			float x ,
			float y )
	{
		x -= PAGE_PADDING_LEFT;
		y = Utils3D.getScreenHeight() - y - PAGE_PADDING_TOP;
		//teapotXu add start
		if( DefaultLayout.butterfly_style == true )
		{
			int y_index = (int)( y / ( pageHeight + pagePaddingVertical ) );
			if( y_index > 1 )
			{
				return 1 + (int)( x / ( pageWidth + pagePaddingHorizontal ) ) + 2 * ( y_index );
			}
			else
			{
				return (int)( x / ( pageWidth + pagePaddingHorizontal ) ) + 2 * ( y_index );
			}
		}
		else
		{
			return (int)( x / ( pageWidth + pagePaddingHorizontal ) ) + 3 * (int)( y / ( pageHeight + pagePaddingVertical ) );
		}
		//teapotXu add end
	}
	
	private int adjustAfterDrop(
			int i )
	{
		if( i <= emptyPosition && i > oldEmptyPosition )
		{
			return i - 1;
		}
		else if( i >= emptyPosition && i < oldEmptyPosition )
		{
			return i + 1;
		}
		else if( i == oldEmptyPosition )
		{
			return emptyPosition;
		}
		return i;
	}
	
	private boolean weakPointerInParent(
			View3D v ,
			float x ,
			float y )
	{
		float weakX = (float)( v.x - v.width * 0.5 / 2 );
		float weakY = (float)( v.y - v.height * 0.5 / 2 );
		return( ( x - weakX ) > 0 && ( x - weakX ) < v.width * 1.5 && ( y - weakY ) > 0 && ( y - weakY ) < v.height * 1.5 );
	}
	
	public void goBack()
	{
		if( !isAnimating() )
		{
			if( pageSource == PAGE_SOURCE_SELECT )
				pageContainer.changeMode();
			else
			{
				int index = pageContainer.getSelectedIndex();
				if( index < 0 )
					index = 0;
				if( index >= this.getChildCount() )
					index = this.getChildCount() - 1;
				ViewGroup3D child = (ViewGroup3D)getChildAt( index );
				enterPage( child );
			}
		}
	}
	
	public void enterHomePage(
			boolean anim )
	{
		if( !anim )
		{
			removeAllViews();
			pageContainer.pageSelect.prepareHide();
			super.hide();
			pageContainer.onPageSelectHide();
			return;
		}
		if( !isAnimating() )
		{
			int index = homePage;
			if( index >= this.getChildCount() )
				index = this.getChildCount() - 1;
			ViewGroup3D child = (ViewGroup3D)getChildAt( index );
			enterPage( child );
		}
	}
	
	@Override
	protected void drawChildren(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( transform )
		{
			for( int i = 0 ; i < children.size() ; i++ )
			{
				View3D child = children.get( i );
				if( !child.visible )
					continue;
				child.applyTransformChild( batch );
				if( child.background9 != null )
				{
					batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
					Matrix4 temp = batch.getTransformMatrix();
					temp.translate( child.x , child.y , 0 );
					temp.scale( PageSelect3D.bgScaleX , PageSelect3D.bgScaleY , 1 );
					temp.translate( -child.x , -child.y , 0 );
					batch.setTransformMatrix( temp );
					child.background9.draw( batch , child.x , child.y , child.width / PageSelect3D.bgScaleX , child.height / PageSelect3D.bgScaleY );
					temp.translate( child.x , child.y , 0 );
					temp.scale( 1 / PageSelect3D.bgScaleX , 1 / PageSelect3D.bgScaleY , 1 );
					temp.translate( -child.x , -child.y , 0 );
					batch.setTransformMatrix( temp );
				}
				child.draw( batch , parentAlpha );
				child.resetTransformChild( batch );
			}
			batch.flush();
		}
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( !draging )
				goBack();
		}
		return true;
	}
	
	@Override
	public boolean onTouchDragged(
			float x ,
			float y ,
			int pointer )
	{
		if( !draging )
			return false;
		if( pointer != 0 )
			return true;
		//Log.v("page", "onTouchDragged:" + name + " x:" + x + " y:" + y+" pointer:"+pointer);
		int tmp = calculateEmptyPosition( x , y );
		if( tmp >= pageNum )
			tmp = pageNum - 1;
		if( tmp == emptyPosition )
			return true;
		sortPage( tmp );
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "page" , "onTouchDown:" + name + " x:" + x + " y:" + y + " pointer:" + pointer );
		if( pointer == 0 )
			downTime = System.currentTimeMillis();
		return true;
	}
	
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "page" , "onTouchUp:" + name + " x:" + x + " y:" + y + " pointer:" + pointer );
		if( pointer != 0 )
			return true;
		if( System.currentTimeMillis() - downTime < CLICK_TIME )
			click( x , y );
		return true;
	}
	
	//	@Override
	public boolean zoom(
			float originalDistance ,
			float currentDistance )
	{
		//		multiTouch = true;
		//		if(originalDistance-currentDistance > ZOOM_DISTANCE && !draging){
		//			goBack();
		//		}
		return true;
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		return true;
	}
	
	public boolean click(
			float x ,
			float y )
	{
		if( !touchable || !visible )
			return false;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			ViewGroup3D child = (ViewGroup3D)getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			if( child.pointerInParent( point.x , point.y ) )
			{
				if( child == addIconPage )
				{
					onAppendPage();
					return true;
				}
				else
				{
					toLocalCoordinates( child , point );
					View3D deleteIcon = child.findView( "delete_icon" );
					View3D homeIcon = child.findView( "home_icon" );
					if( deleteIcon != null && deleteIcon.isVisible() && weakPointerInParent( deleteIcon , point.x , point.y ) )
					{
						onDeletePage( i );
						return true;
					}
					else if( homeIcon != null && homeIcon.isVisible() && weakPointerInParent( homeIcon , point.x , point.y ) )
					{
						if( homePage != i )
							setHomePage( i );
						return true;
					}
					else
					{
						if( !isAnimating() )
						{
							enterPage( child );
						}
					}
				}
			}
		}
		goBack();
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		if( !touchable || !visible )
			return false;
		if( isAnimating() )
			return true;
		int len = this.getChildCount() - 1;
		for( int i = len ; i >= 0 ; i-- )
		{
			ViewGroup3D child = (ViewGroup3D)getChildAt( i );
			if( !child.touchable || !child.visible )
				continue;
			point.x = x;
			point.y = y;
			if( child.pointerInParent( point.x , point.y ) )
			{
				if( child == addIconPage )
				{
					return true;
				}
				else
				{
					draging = true;
					child.toAbsoluteCoords( point );
					this.setTag( new Vector2( point.x , point.y ) );
					dragObj = child;
					if( pageNum < PAGE_COUNT_MAX )
					{
						addIconPage.stopTween();
						addIconPage.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.4f , 0 , 0 , 0 ).setCallback( this );
					}
					oldEmptyPosition = i;
					emptyPosition = i;
					selectPageList.remove( i );
					pageContainer.removeCellLayout( i , true );
					point.x = x;
					point.y = y;
					this.toAbsolute( point );
					DragLayer3D.dragStartX = point.x;
					DragLayer3D.dragStartY = point.y;
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				}
			}
		}
		return true;
	}
	
	@Override
	public void onDropCompleted(
			View3D target ,
			boolean success )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public ArrayList<View3D> getDragList()
	{
		if( dragList == null )
			dragList = new ArrayList<View3D>();
		dragList.clear();
		dragList.add( dragObj );
		return dragList;
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		if( list.size() < 1 )
			return true;
		View3D page = list.get( 0 );
		page.x = page.getParent().x;
		page.y = page.getParent().y;
		setLayoutParams( emptyPosition , page , true );
		addViewAt( emptyPosition , page );
		View3D selectPage = ( (ViewGroup3D)page ).findView( "page_select" );
		if( selectPage != null )
		{
			selectPageList.add( emptyPosition , selectPage );
			CellLayout3D cell = (CellLayout3D)( (ViewGroup3D)selectPage ).findView( "celllayout" );
			if( cell != null )
				pageContainer.addCellLayout( emptyPosition , cell );
		}
		//page.startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.5f, 1, 1, 0);
		setHomePage( adjustAfterDrop( homePage ) );
		pageContainer.setSelectedIndex( adjustAfterDrop( pageContainer.getSelectedIndex() ) );
		if( pageNum < PAGE_COUNT_MAX )
		{
			this.addView( addIconPage );
			addIconPage.stopTween();
			addIconPage.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.4f , 1 , 1 , 0 );
		}
		animatingDrop = true;
		draging = false;
		return true;
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean hide(
			boolean anim )
	{
		if( isAnimating() )
			return false;
		if( !anim )
		{
			prepareHide();
			super.hide();
			return true;
		}
		animatingHide = true;
		timeline = Timeline.createParallel();
		if( pageContainer.getSelectedIndex() >= 0 )
		{
			View3D page = this.getChildAt( pageContainer.getSelectedIndex() );
			page.bringToFront();
		}
		for( int index = 0 ; index < selectPageList.size() ; index++ )
		{
			View3D v = selectPageList.get( index );
			ViewGroup3D parent = v.getParent();
			ViewInfoHolder info = (ViewInfoHolder)v.getTag();
			ViewGroup3D oldParent = info.parent;
			View3D deleteIcon = parent.findView( "delete_icon" );
			View3D homeIcon = parent.findView( "home_icon" );
			if( deleteIcon != null )
				parent.removeView( deleteIcon );
			if( homeIcon != null )
				parent.removeView( homeIcon );
			float delay = 0f;
			//			int selected = pageContainer.getSelectedIndex()-1;
			//			if(selected < 0)selected = 0;
			delay = index * animDelay;
			parent.setRotationVector( 0 , 1 , 0 );
			parent.setOrigin( info.originX , info.originY );
			timeline.push( parent.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , info.x + oldParent.x , info.y + oldParent.y , 0 ).delay( delay ) );
			timeline.push( parent.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , info.rotation , 0 , 0 ).delay( delay ) );
			timeline.push( v.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , info.scaleX , info.scaleY , 0 ).delay( delay ) );
		}
		if( pageNum < PAGE_COUNT_MAX )
			timeline.push( addIconPage.obtainTween( View3DTweenAccessor.OPACITY , Cubic.OUT , 0.3f , 0f , 0 , 0 ) );
		timeline.setCallback( this ).start( View3DTweenAccessor.manager );
		return true;
	}
	
	private void prepareHide()
	{
		if( selectPageList != null )
		{
			View3D v;
			v = selectPageList.get( pageContainer.getSelectedIndex() );
			selectPageList.remove( v );
			selectPageList.add( v );
			for( int i = 0 ; i < selectPageList.size() ; i++ )
			{
				v = selectPageList.get( i );
				ViewInfoHolder info = (ViewInfoHolder)v.getTag();
				ViewGroup3D parent = info.parent;
				parent.addViewAt( i , v );
				info.applyInfo( v );
			}
		}
		removeAllViews();
	}
	
	public boolean isAnimating()
	{
		return animatingEdit || animatingHide || animatingShow || animatingDrop || animatingEnter;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source instanceof Tween )
		{
			Tween tween = (Tween)source;
			if( tween.getTarget() == addIconPage )
			{
				if( addIconPage.scaleX == 0 )
				{
					this.removeView( addIconPage );
				}
			}
			else if( tween.getTarget() == deletedPage )
			{
				animatingEdit = false;
				if( deletedPage.scaleX == 0 )
				{
					this.removeView( deletedPage );
				}
			}
			else if( animatingDrop )
			{
				Log.d( "page" , "draging=" + draging );
				animatingDrop = false;
			}
		}
		else if( source instanceof Timeline )
		{
			if( animatingShow )
			{
				animatingShow = false;
			}
			if( animatingHide )
			{
				Log.d( "page" , "editpage hide anim complete" );
				prepareHide();
				super.hide();
				pageContainer.onPageEditHide();
				animatingHide = false;
			}
			if( animatingEnter )
			{
				removeAllViews();
				pageContainer.pageSelect.prepareHide();
				super.hide();
				pageContainer.onPageSelectHide();
				animatingEnter = false;
			}
			if( animatingEdit )
			{
				animatingEdit = false;
			}
		}
	}
	
	class EditPageView extends ViewGroup3D
	{
		
		public EditPageView(
				String string )
		{
			super( string );
			transform = true;
		}
		
		@Override
		protected void drawChildren(
				SpriteBatch batch ,
				float parentAlpha )
		{
			parentAlpha *= color.a;
			for( int i = 0 ; i < children.size() ; i++ )
			{
				View3D child = children.get( i );
				if( !child.visible )
					continue;
				child.applyTransformChild( batch );
				if( child.background9 != null )
				{
					batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
					Matrix4 temp = batch.getTransformMatrix();
					float bgScaleX = child.width / BG_9_WIDTH < 1 ? child.width / BG_9_WIDTH : 1;
					float bgScaleY = child.height / BG_9_HEIGHT < 1 ? child.height / BG_9_HEIGHT : 1;
					float m = ( bgScaleX - PageSelect3D.bgScaleX ) / ( pageScaleX - 1 );
					float n = PageSelect3D.bgScaleX - m;
					float j = ( bgScaleY - PageSelect3D.bgScaleY ) / ( pageScaleY - 1 );
					float k = PageSelect3D.bgScaleY - j;
					temp.scale( m * child.scaleX + n , j * child.scaleY + k , 1 );
					batch.setTransformMatrix( temp );
					child.background9.draw( batch , child.x , child.y , child.width / ( m * child.scaleX + n ) , child.height / ( j * child.scaleY + k ) );
					temp.scale( 1 / ( m * child.scaleX + n ) , 1 / ( j * child.scaleY + k ) , 1 );
					batch.setTransformMatrix( temp );
				}
				child.draw( batch , parentAlpha );
				child.resetTransformChild( batch );
			}
			batch.flush();
		}
	}
}
