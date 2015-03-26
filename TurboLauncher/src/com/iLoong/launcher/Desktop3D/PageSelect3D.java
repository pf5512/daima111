package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Circ;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class PageSelect3D extends ViewGroup3D
{
	
	public int currentIndex = 0;
	public int enterIndex = 0;
	public int homePage = 0;
	private PageContainer3D pageContainer;
	private ArrayList<View3D> viewList;
	private ArrayList<View3D> cellLayoutList;
	private float pagePaddingVertical = 45f;
	private float pagePaddingHorizontal = 100f;
	private float pageviewPaddingX = 20f;
	private float pageviewPaddingY = 20f;
	private float pageBottom = 120f;
	private float pageWidth = 240f;
	private float pageHeight = 300f;
	private float lastX;
	private long lastTime;
	private boolean checkTimer = false;
	private boolean repeatTimer = false;
	private boolean allowLongPress = true;
	private static final long LONG_PRESS_TIME = 800;
	private static final float MOVE_DISTANCE = 20f;
	private Timer timer;
	private LongPressTask longPressTask;
	public boolean needHide = false;
	private boolean animatingHide = false;
	private float targetX = -1f;
	public boolean animatingShow = false;
	private Timeline showTimeline;
	private Timeline hideTimeline;
	//public float wallpaperOffset = -1f;
	public float targetOffset = -1f;
	private View3D animView;
	private iLoongLauncher launcher;
	private WallpaperManager mWallpaperManager;
	//public int clingState = ClingManager.CLING_STATE_WAIT;
	//private int priority = ClingManager.PAGESELECT_CLING;
	private float downX = 0;
	private float downTime = 0;
	private boolean click = false;
	public static float bgScaleX;
	public static float bgScaleY;
	public static final int BG_9_WIDTH = 26;
	public static final int BG_9_HEIGHT = 37;
	public static final float ROTATE_DEGREE = 55f;
	private static final float PAGE_PADDING_VERTICAL_MAX = 80f;
	private static final float PAGE_PADDING_VERTICAL_MIN = 30f;
	private static float PAGE_WIDTH_MIN = 250f;
	private static float PAGE_HEIGHT_MIN = 305f;
	
	public PageSelect3D(
			String name )
	{
		//super(name);
		float screenWidth = Utils3D.getScreenWidth();
		float screenScale = screenWidth / 480 < 1 ? screenWidth / 480 : 1;
		PAGE_WIDTH_MIN *= screenScale;
		PAGE_HEIGHT_MIN *= screenScale;
		float densityScale = Utils3D.getDensity() * 160 / 240;
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight();
		pageWidth = width / 2.1f * densityScale;
		pageHeight = height / 2.5f * densityScale;
		if( pageWidth < PAGE_WIDTH_MIN || pageHeight < PAGE_HEIGHT_MIN )
		{
			pageWidth = PAGE_WIDTH_MIN;
			pageHeight = PAGE_HEIGHT_MIN;
		}
		bgScaleX = pageWidth / BG_9_WIDTH < 1 ? pageWidth / BG_9_WIDTH : 1;
		bgScaleY = pageHeight / BG_9_HEIGHT < 1 ? pageHeight / BG_9_HEIGHT : 1;
		pageviewPaddingX *= bgScaleX;
		pageviewPaddingY *= bgScaleY;
		transform = true;
		animView = new View3D( "anim" );
		visible = false;
		//clingState = ClingManager.getInstance().firePageSelectCling(this);
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
	
	private void init()
	{
		timer = new Timer();
		longPressTask = new LongPressTask();
		checkTimer = false;
		repeatTimer = false;
	}
	
	//for show PageEdit
	public void setupPageEdit(
			ArrayList<View3D> pageList )
	{
		cellLayoutList = pageList;
		for( int i = 0 ; i < cellLayoutList.size() ; i++ )
		{
			View3D view = cellLayoutList.get( i );
			ViewInfoHolder tmp = new ViewInfoHolder();
			tmp.getInfo( view );
			view.setTag( tmp );
		}
		removeAllViews();
		width = Utils3D.getScreenWidth();
		int i = 0;
		int count = cellLayoutList.size();
		while( i < count )
		{
			View3D view = cellLayoutList.get( i );
			ViewGroup3D page = new ViewGroup3D( "page_select" );
			page.transform = true;
			page.setSize( pageWidth , pageHeight );
			page.setOrigin( pageWidth / 2 , pageHeight / 2 );
			page.setRotationVector( 0 , 1 , 0 );
			if( i != currentIndex )
			{
				page.setColor( Color.DARK_GRAY );
				page.setBackgroud( PageContainer3D.pageBg );
			}
			else
				page.setBackgroud( PageContainer3D.pageSelectedBg );
			( (ViewInfoHolder)view.getTag() ).parent = view.getParent();//save parent
			page.addView( view ); //add to new parent		
			float scaleX = ( pageWidth - pageviewPaddingX * 2 ) / view.width;
			float scaleY = ( pageHeight - pageviewPaddingY * 2 ) / view.height;
			float scale = scaleX < scaleY ? scaleX : scaleY;
			view.x = ( pageWidth - scale * view.width ) / 2;
			view.y = ( pageHeight - scale * view.height ) / 2;
			view.setScale( scale , scale );
			view.setOrigin( 0 , 0 );
			view.setRotation( 0f );
			view.show();
			page.touchable = false;
			addView( page );
			i++;
		}
		View3D page = this.getChildAt( currentIndex );
		page.remove();
		this.addView( page );
	}
	
	public void setupPage(
			ArrayList<View3D> pageList )
	{
		cellLayoutList = pageList;
		for( int i = 0 ; i < cellLayoutList.size() ; i++ )
		{
			View3D view = null;
			view = cellLayoutList.get( i );
			ViewInfoHolder tmp = new ViewInfoHolder();
			tmp.getInfo( view );
			view.setTag( tmp );
		}
		removeAllViews();
		animatingShow = true;
		showTimeline = Timeline.createParallel();
		calculateParams( true );
		int i = 0;
		int count = cellLayoutList.size();
		while( i < count )
		{
			if( i != enterIndex )
			{
				addPage( cellLayoutList.get( i ) , i , false );
			}
			i++;
		}
		addPage( cellLayoutList.get( enterIndex ) , enterIndex , false );
		showTimeline.setCallback( this ).start( View3DTweenAccessor.manager );
		show();
		targetOffset = -1;
	}
	
	private void calculateParams(
			boolean anim )
	{
		width = Utils3D.getScreenWidth();
		x = 0;
		y = 0;
		int count = cellLayoutList.size();
		pagePaddingHorizontal = pageWidth / 3.1f;
		pageBottom = 0;
		pagePaddingVertical = ( height - 1.3f * pageBottom - pageHeight ) / ( count - 1 );
		if( pagePaddingVertical < PAGE_PADDING_VERTICAL_MIN )
		{
			pagePaddingVertical = ( height - pageBottom - pageHeight ) / ( count - 1 );
		}
		else if( pagePaddingVertical > PAGE_PADDING_VERTICAL_MAX )
		{
			pagePaddingVertical = PAGE_PADDING_VERTICAL_MAX;
			pageBottom = ( height - pagePaddingVertical * ( count - 1 ) - pageHeight ) / 1.3f;
		}
		if( count > 0 )
		{
			width = pagePaddingHorizontal * ( count - 1 ) + pageWidth + 20;
			if( width <= Utils3D.getScreenWidth() )
			{
				x = ( Utils3D.getScreenWidth() - width ) / 2;
				targetX = x;
				width = Utils3D.getScreenWidth();
			}
			else
			{
				x = Utils3D.getScreenWidth() / 2 - pagePaddingHorizontal * currentIndex - pageWidth / 2;
				if( currentIndex != enterIndex && anim )
				{
					targetX = Utils3D.getScreenWidth() / 2 - pagePaddingHorizontal * enterIndex - pageWidth / 2;
					if( targetX > 0 )
						targetX = 0;
					if( targetX < Utils3D.getScreenWidth() - width )
						targetX = Utils3D.getScreenWidth() - width;
					showTimeline.push( obtainTween( View3DTweenAccessor.POS_XY , Quad.IN , 0.3f , targetX , y , 0 ) );
				}
				else
				{
					targetX = x;
					if( x > 0 )
					{
						targetX = 0;
						if( anim )
							showTimeline.push( obtainTween( View3DTweenAccessor.POS_XY , Quad.OUT , 0.3f , targetX , y , 0 ) );
						else
							x = targetX;
					}
					if( x < Utils3D.getScreenWidth() - width )
					{
						targetX = Utils3D.getScreenWidth() - width;
						if( anim )
							showTimeline.push( obtainTween( View3DTweenAccessor.POS_XY , Quad.OUT , 0.3f , targetX , y , 0 ) );
						else
							x = targetX;
					}
				}
			}
		}
	}
	
	public View3D addPage(
			View3D view ,
			int index ,
			boolean fromUser )
	{
		ViewGroup3D page = new ViewGroup3D( "page_select" );
		page.transform = true;
		page.setSize( pageWidth , pageHeight );
		page.setOrigin( pageWidth / 2 , pageHeight / 2 );
		float x = 10 + pagePaddingHorizontal * index;
		float y = pageBottom + pagePaddingVertical * index;
		if( !fromUser )
		{
			page.x = x;
			page.y = height / 2 - pageHeight / 2;
			showTimeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Quad.OUT , 0.3f , x , y , 0 ) );
		}
		else
		{
			page.x = x;
			page.y = y;
		}
		if( index != enterIndex )
		{
			page.setColor( Color.DARK_GRAY );
			page.setBackgroud( PageContainer3D.pageBg );
			page.setRotationVector( 0 , 1 , 0 );
			if( index == currentIndex )
				page.startTween( View3DTweenAccessor.ROTATION , Quad.IN , 0.3f , ROTATE_DEGREE , 0 , 0 );
			else
				page.setRotation( ROTATE_DEGREE );
		}
		else
		{
			if( index != currentIndex )
			{
				page.setBackgroud( PageContainer3D.pageBg );
			}
			else
				page.setBackgroud( PageContainer3D.pageSelectedBg );
		}
		if( fromUser )
		{
			ViewInfoHolder tmp = new ViewInfoHolder();
			tmp.getInfo( view );
			view.setTag( tmp );
		}
		( (ViewInfoHolder)view.getTag() ).parent = view.getParent();//save parent
		page.addView( view ); //add to new parent		
		float scaleX = ( pageWidth - pageviewPaddingX * 2 ) / view.width;
		float scaleY = ( pageHeight - pageviewPaddingY * 2 ) / view.height;
		float scale = scaleX < scaleY ? scaleX : scaleY;
		if( index == currentIndex && !fromUser )
		{
			view.x = -page.x - this.x;
			view.y = -page.y - this.y;
			showTimeline.push( view.obtainTween(
					View3DTweenAccessor.POS_XY ,
					( enterIndex != currentIndex ) ? Quad.IN : Cubic.OUT ,
					( enterIndex != currentIndex ) ? 0.3f : 0.4f ,
					( pageWidth - scale * view.width ) / 2 ,
					( pageHeight - scale * view.height ) / 2 ,
					0 ) );
			showTimeline.push( view
					.obtainTween( View3DTweenAccessor.SCALE_XY , ( enterIndex != currentIndex ) ? Quad.IN : Cubic.OUT , ( enterIndex != currentIndex ) ? 0.3f : 0.4f , scale , scale , 0 ) );
		}
		else
		{
			view.x = ( pageWidth - scale * view.width ) / 2;
			view.y = ( pageHeight - scale * view.height ) / 2;
			view.setScale( scale , scale );
		}
		view.setOrigin( 0 , 0 );
		view.setRotation( 0f );
		view.show();
		page.touchable = false;
		addView( page );
		return page;
	}
	
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( animatingHide || animatingShow )
		{
			return true;
		}
		if( deltaX != 0 && width > Utils3D.getScreenWidth() )
		{
			float screenX = x + this.x;
			if( deltaX > 0 )
				targetX -= deltaX * ( width + targetX - Utils3D.getScreenWidth() ) / ( Utils3D.getScreenWidth() - screenX );
			else
			{
				if( targetX != 0 )
					targetX += deltaX * targetX / screenX;
			}
			if( -targetX + Utils3D.getScreenWidth() > width )
				targetX = Utils3D.getScreenWidth() - width;
			if( targetX > 0 )
				targetX = 0;
			stopTween();
			startTween( View3DTweenAccessor.POS_XY , Circ.OUT , 1.0f , targetX , 0 , 0 );
		}
		int pointer = -1;
		if( deltaX > 0 )
		{
			pointer = pointerNextPage( x );
			if( pointer == -1 )
			{
				pointer = pointerCurrentPage( x );
			}
			if( pointer == -1 )
			{
				pointer = pointerPreviousPage( x );
			}
		}
		else if( deltaX < 0 )
		{
			pointer = pointerCurrentPage( x );
			if( pointer == -1 )
			{
				pointer = pointerPreviousPage( x );
			}
			if( pointer == -1 )
			{
				pointer = pointerNextPage( x );
			}
		}
		else
		{
			if( click == true )
			{
				click = false;
				pointer = pointerNextPage( x );
				if( pointer == -1 )
					pointer = pointerCurrentPage( x );
				if( pointer == -1 )
					pointer = pointerPreviousPage( x );
				if( pointer == currentIndex )
					pointer = getChildCount() - 1;
				if( pointer == getChildCount() - 1 )
				{
					if( needHide )
					{
						needHide = false;
						pageContainer.hide( true );
					}
				}
			}
			else
			{
				pointer = pointerCurrentPage( x );
				if( pointer == -1 )
					pointer = pointerPreviousPage( x );
				if( pointer == -1 )
					pointer = pointerNextPage( x );
			}
		}
		if( checkTimer )
		{
			if( pointer == -1 )
			{
				allowLongPress = false;
			}
			else if( lastX - x > MOVE_DISTANCE || lastX - x < -MOVE_DISTANCE || allowLongPress != pointerYInPage( getChildAt( pointer ) , y ) || pointer != getChildCount() - 1 )
			{
				lastTime = System.currentTimeMillis();
				lastX = x;
				repeatTimer = true;
				allowLongPress = pointerYInPage( getChildAt( pointer ) , y );
			}
		}
		if( pointer == -1 || pointer == getChildCount() - 1 )
			return true;
		if( !checkTimer )
		{
			checkTimer = true;
			lastTime = System.currentTimeMillis();
			longPressTask = new LongPressTask();
			timer = new Timer();
			timer.schedule( longPressTask , LONG_PRESS_TIME );
			lastX = x;
		}
		if( pointer >= currentIndex )
			pointer++;
		startSwitch( pointer );
		//priority = ClingManager.PAGEEDIT_CLING;
		//ClingManager.getInstance().cancelPageContainerCling(ClingManager.PAGESELECT_CLING);
		return true;
	}
	
	public void switchToPage(
			int selected )
	{
		if( animatingHide || animatingShow )
		{
			return;
		}
		if( currentIndex == selected )
		{
			pageContainer.hide( true );
			return;
		}
		if( width > Utils3D.getScreenWidth() )
		{
			targetX = Utils3D.getScreenWidth() / 2 - pagePaddingHorizontal * selected - pageWidth / 2;
			if( targetX > 0 )
			{
				targetX = 0;
			}
			if( targetX < Utils3D.getScreenWidth() - width )
			{
				targetX = Utils3D.getScreenWidth() - width;
			}
			stopTween();
			startTween( View3DTweenAccessor.POS_XY , Circ.OUT , 1.5f , targetX , y , 0 );
		}
		if( checkTimer )
		{
			lastTime = System.currentTimeMillis();
			lastX = x;
			repeatTimer = true;
			allowLongPress = false;
		}
		if( !checkTimer )
		{
			checkTimer = true;
			lastTime = System.currentTimeMillis();
			longPressTask = new LongPressTask();
			timer = new Timer();
			timer.schedule( longPressTask , LONG_PRESS_TIME );
			lastX = x;
		}
		startSwitch( selected );
		needHide = true;
	}
	
	public void startSwitch(
			int selected )
	{
		View3D previousTarget = getChildAt( this.getChildCount() - 1 );
		View3D nextTarget = getChildAt( selected );
		if( selected > currentIndex )
			nextTarget = getChildAt( selected - 1 );
		removeView( previousTarget );
		addViewAt( currentIndex , previousTarget );
		removeView( nextTarget );
		addView( nextTarget );
		pageContainer.setSelectedIndex( selected );
		calculateWallpaperOffset();
		View3D view;
		for( int i = 0 ; i < getChildCount() - 1 ; i++ )
		{
			view = this.getChildAt( i );
			view.setColor( Color.DARK_GRAY );
			view.setBackgroud( PageContainer3D.pageBg );
			view.stopTween();
			if( view.rotation != ROTATE_DEGREE )
			{
				view.setRotationVector( 0 , 1 , 0 );
				view.startTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , ROTATE_DEGREE , 0 , 0 );
			}
		}
		nextTarget.setColor( Color.WHITE );
		nextTarget.setBackgroud( PageContainer3D.pageSelectedBg );
		nextTarget.stopTween();
		if( nextTarget.rotation != 0 )
		{
			nextTarget.setRotationVector( 0 , 1 , 0 );
			nextTarget.startTween( View3DTweenAccessor.ROTATION , Cubic.OUT , 0.5f , 0 , 0 , 0 ).setCallback( this );
		}
	}
	
	public int pointerCurrentPage(
			float x )
	{
		View3D view = getChildAt( getChildCount() - 1 );
		if( pointerXInPage( view , x ) )
		{
			return getChildCount() - 1;
		}
		return -1;
	}
	
	public int pointerPreviousPage(
			float x )
	{
		View3D view;
		for( int i = currentIndex - 1 ; i >= 0 ; i-- )
		{
			view = getChildAt( i );
			if( pointerXInPage( view , x ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	public int pointerNextPage(
			float x )
	{
		View3D view;
		for( int i = getChildCount() - 2 ; i >= currentIndex ; i-- )
		{
			view = getChildAt( i );
			if( pointerXInPage( view , x ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	public boolean pointerXInPage(
			View3D page ,
			float x )
	{
		point.x = x - page.x;
		return( ( point.x > pageWidth / 3 && point.x < page.width + pageWidth / 3 ) );
	}
	
	public boolean pointerYInPage(
			View3D page ,
			float y )
	{
		point.y = y - page.y;
		return( ( point.y > 0 && point.y < page.height ) );
	}
	
	private void resetToParent(
			View3D view )
	{
		ViewGroup3D parent = ( (ViewInfoHolder)view.getTag() ).parent;
		if( parent != null )
			parent.addView( view );
	}
	
	public void resort()
	{
		calculateParams( false );
		for( int i = 0 ; i < viewList.size() ; i++ )
		{
			( (ViewInfoHolder)viewList.get( i ).getTag() ).x = 10 + pagePaddingHorizontal * i;
			( (ViewInfoHolder)viewList.get( i ).getTag() ).y = pageBottom + pagePaddingVertical * i;
			if( i != currentIndex )
			{
				( (ViewInfoHolder)viewList.get( i ).getTag() ).rotation = ROTATE_DEGREE;
			}
			else
			{
				( (ViewInfoHolder)viewList.get( i ).getTag() ).rotation = 0;
			}
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		setWallpaperOffset();
		super.draw( batch , parentAlpha );
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
				if( this.x + child.x + child.width < 0 )
				{
					continue;
				}
				if( this.x + child.x > Utils3D.getScreenWidth() )
				{
					continue;
				}
				child.applyTransformChild( batch );
				if( child.background9 != null )
				{
					batch.setColor( child.color.r , child.color.g , child.color.b , child.color.a * parentAlpha );
					Matrix4 temp = batch.getTransformMatrix();
					temp.translate( child.x , child.y , 0 );
					temp.scale( bgScaleX , bgScaleY , 1 );
					temp.translate( -child.x , -child.y , 0 );
					batch.setTransformMatrix( temp );
					child.background9.draw( batch , child.x , child.y , child.width / bgScaleX , child.height / bgScaleY );
					temp.translate( child.x , child.y , 0 );
					temp.scale( 1 / bgScaleX , 1 / bgScaleY , 1 );
					temp.translate( -child.x , -child.y , 0 );
					batch.setTransformMatrix( temp );
				}
				child.draw( batch , parentAlpha );
				child.resetTransformChild( batch );
			}
			//			batch.flush();
		}
	}
	
	public void show()
	{
		super.show();
		init();
		//		if(clingState==ClingManager.CLING_STATE_SHOW){
		//			SendMsgToAndroid.sendRefreshClingStateMsg();
		//		}
	}
	
	@Override
	public void hide()
	{
		Log.d( "launcher" , "hide" );
		super.hide();
		//		if(clingState==ClingManager.CLING_STATE_SHOW){
		//			SendMsgToAndroid.sendRefreshClingStateMsg();
		//		}
	}
	
	public boolean hideForEdit()
	{
		if( animatingHide || animatingShow )
		{
			return false;
		}
		stopTween();
		if( timer != null )
			timer.cancel();
		super.hide();
		//		if(clingState==ClingManager.CLING_STATE_SHOW){
		//			SendMsgToAndroid.sendRefreshClingStateMsg();
		//		}
		return true;
	}
	
	public boolean hide(
			boolean anim )
	{
		if( animatingHide || animatingShow )
		{
			return false;
		}
		stopTween();
		if( !anim )
		{
			prepareHide();
			if( timer != null )
				timer.cancel();
			super.hide();
			//			if(clingState==ClingManager.CLING_STATE_SHOW){
			//				SendMsgToAndroid.sendRefreshClingStateMsg();
			//			}
			pageContainer.onPageSelectHide();
			return true;
		}
		animatingHide = true;
		hideTimeline = Timeline.createParallel();
		for( int i = 0 ; i < getChildCount() ; i++ )
		{
			ViewGroup3D page = (ViewGroup3D)getChildAt( i );
			if( i == getChildCount() - 1 )
			{
				page.setBackgroud( null );
				View3D homeIcon = page.findView( "home_icon" );
				if( homeIcon != null )
					page.removeView( homeIcon );
				View3D v = null;
				v = cellLayoutList.get( currentIndex );
				ViewInfoHolder tmp = (ViewInfoHolder)v.getTag();
				hideTimeline.push( v.obtainTween( View3DTweenAccessor.SCALE_XY , Quad.OUT , 0.4f , tmp.scaleX , tmp.scaleY , 0 ) );
				hideTimeline.push( v.obtainTween( View3DTweenAccessor.POS_XY , Quad.OUT , 0.4f , -page.x - this.x , -page.y - this.y , 0 ) );
				continue;
			}
			hideTimeline.push( page.obtainTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.4f , page.x , -pageHeight , 0 ) );
		}
		hideTimeline.setCallback( this ).start( View3DTweenAccessor.manager );
		return true;
	}
	
	public boolean isAnimating()
	{
		if( animatingHide || animatingShow )
		{
			return true;
		}
		return false;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source instanceof Tween )
		{
			if( needHide )
			{
				needHide = false;
				pageContainer.hide( true );
			}
		}
		else if( type == TweenCallback.COMPLETE && source instanceof Timeline )
		{
			if( animatingHide )
			{
				prepareHide();
				if( timer != null )
					timer.cancel();
				animatingHide = false;
				pageContainer.onPageSelectHide();
			}
			else if( animatingShow )
			{
				animatingShow = false;
				if( enterIndex != currentIndex )
				{
					currentIndex = enterIndex;
					pageContainer.hide( true );
				}
				else if( needHide )
				{
					needHide = false;
					pageContainer.hide( true );
				}
			}
		}
	}
	
	public void prepareHide()
	{
		Log.d( "launcher" , "prepareHide" );
		if( cellLayoutList != null )
		{
			for( int i = 0 ; i < cellLayoutList.size() ; i++ )
			{
				View3D v = cellLayoutList.get( i );
				resetToParent( v );
				ViewInfoHolder tmp = (ViewInfoHolder)v.getTag();
				tmp.applyInfo( v );
			}
		}
		removeAllViews();
	}
	
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		return true;
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		if( keycode == KeyEvent.KEYCODE_BACK )
			pageContainer.hide( true );
		return true;
	}
	
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		Log.d( "launcher" , "onTouchUp" );
		if( pointer == 0 )
		{
			if( System.currentTimeMillis() - downTime < PageIndicator3D.CLICK_TIME && Math.abs( x - downX ) < PageIndicator3D.CLICK_MOVE )
			{
				click = true;
				needHide = true;
				scroll( x + pageWidth / 6 , y , 0 , 0 );
			}
			else
				pageContainer.hide( true );
		}
		downX = 0;
		downTime = 0;
		return true;
	}
	
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		downX = x;
		downTime = System.currentTimeMillis();
		return true;
	}
	
	class LongPressTask extends TimerTask
	{
		
		@Override
		public void run()
		{
			long time = System.currentTimeMillis();
			if( !allowLongPress )
			{
				longPressTask = new LongPressTask();
				timer = new Timer();
				timer.schedule( longPressTask , LONG_PRESS_TIME );
				return;
			}
			if( repeatTimer && LONG_PRESS_TIME + lastTime - time > 0 )
			{
				repeatTimer = false;
				longPressTask = new LongPressTask();
				timer = new Timer();
				timer.schedule( longPressTask , LONG_PRESS_TIME + lastTime - time );
			}
			else
				pageContainer.hide( true );
		}
	}
	
	public void setCurrentCell(
			int index )
	{
		//		Log.d("launcher", "setCurrentCell");
		//		animView.setUser((float)(index)/(cellLayoutList.size()));
		//		setWallpaperOffset();
		//		targetOffset = (float)(index)/(cellLayoutList.size());
	}
	
	private void calculateWallpaperOffset()
	{
		targetOffset = (float)( currentIndex ) / ( cellLayoutList.size() - 1 );
		Tween.to( animView , View3DTweenAccessor.USER , 0.5f ).ease( Cubic.OUT ).target( targetOffset ).start( View3DTweenAccessor.manager );
		//unitOffset = (float)(targetOffset-wallpaperOffset)/(duration*Gdx.graphics.getFramesPerSecond()/1000);
		//Log.d("launcher", "calculateWallpaperOffset:targetOffset,currentOffset="+targetOffset+","+animView.getUser());
	}
	
	private void setWallpaperOffset()
	{
		//teapotXu add start for wallpaper mv's config menu
		if( DefaultLayout.enable_configmenu_for_move_wallpaper )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
			if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == false )
			{
				//当菜单中设置壁纸不随滑动而滚动时，不需要设置offset
				return;
			}
		}
		//teapotXu add end
		if( animView.getUser() == targetOffset || targetOffset == -1 )
			return;
		IBinder token = launcher.getWindow().getCurrentFocus().getWindowToken();
		if( token == null )
			return;
		mWallpaperManager.setWallpaperOffsets( token , animView.getUser() , 0 );
	}
	
	public List<View3D> getViewList()
	{
		if( viewList == null )
		{
			viewList = new ArrayList<View3D>();
		}
		viewList.clear();
		int size = children.size();
		for( int i = 0 ; i < size ; i++ )
		{
			View3D view = children.get( i );
			ViewInfoHolder viewInfo = new ViewInfoHolder();
			viewInfo.getInfo( view );
			view.setTag( viewInfo );
			if( i == size - 1 )
				viewList.add( currentIndex , view );
			else
				viewList.add( view );
		}
		return viewList;
	}
	
	class ViewInfoHolder
	{
		
		public ViewGroup3D parent;
		public float rotation;
		public float scaleX;
		public float scaleY;
		public float originX;
		public float originY;
		public float x;
		public float y;
		public float width;
		public float height;
		
		public ViewInfoHolder()
		{
		}
		
		public void getInfo(
				View3D view )
		{
			this.x = view.x;
			this.y = view.y;
			this.width = view.width;
			this.height = view.height;
			this.originX = view.originX;
			this.originY = view.originY;
			this.scaleX = view.scaleX;
			this.scaleY = view.scaleY;
			this.rotation = view.rotation;
		}
		
		public void applyInfo(
				View3D v )
		{
			v.x = this.x;
			v.y = this.y;
			v.width = this.width;
			v.height = this.height;
			v.originX = this.originX;
			v.originY = this.originY;
			v.scaleX = this.scaleX;
			v.scaleY = this.scaleY;
			v.rotation = this.rotation;
		}
	}
	//	@Override
	//	public boolean visible() {
	//		if(!this.isVisible()){
	//			return false;
	//		}
	//		if(this.viewParent != null){
	//			if(!this.viewParent.isVisible()){
	//				return false;
	//			}
	//		}
	//		else{
	//			return false;
	//		}
	//		return true;
	//	}
	//
	//	@Override
	//	public int getClingPriority() {
	//		
	//		return priority;
	//	}
	//
	//	@Override
	//	public void dismissCling() {
	//		clingState = ClingManager.CLING_STATE_DISMISSED;
	//	}
	//
	//	@Override
	//	public void setPriority(int priority) {
	//		this.priority = priority;
	//	}
}
