package com.iLoong.launcher.Desktop3D;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class circlePopWnd3D extends ViewGroup3D
{
	
	public static final int CIRCLE_POP_AUTOSORT_EVENT = 0;
	public static final int CIRCLE_POP_MULTISEL_EVENT = 1;
	public static final int CIRCLE_POP_CREATEFOLDER_EVENT = 2;
	public static final int CIRCLE_POP_DELALL_EVENT = 3;
	public static final int CICLE_POP_OVERLAP_EVENT = 4;
	public static final int CIRCLE_POP_DESTROY_EVENT = 5;
	private static AtlasRegion backgroudRegion;
	private static AtlasRegion backgroudUnfocusRegion;
	private float centerX;
	private float centerY;
	// private float offsetUnfocusX;
	private float offsetX;
	private int mHighLightIndex = -1;
	private ImageView3D overLapImgFocus;
	private ImageView3D overLapImgUnFocus;
	private ImageView3D deleteAllImgFocus;
	private ImageView3D deleteAllImgUnFocus;
	private ImageView3D CreateFolderImgFocus;
	private ImageView3D CreateFolderImgUnFocus;
	private ImageView3D multiSelectImgFocus;
	private ImageView3D multiSelectImgUnFocus;
	private ImageView3D autoSortImgFocus;
	private ImageView3D autoSortImgUnFocus;
	private ImageView3D backgroudImgFocus;
	private ImageView3D backgroudImgUnFocus;
	private ImageView3D toastAutoSort;
	private ImageView3D toastOverLap;
	private ImageView3D toastDelAll;
	private ImageView3D toastCreateFolder;
	private ImageView3D toastMultiSelect;
	private float unfocusRadius;
	private float toastHeight;
	private float toastWidth;
	private float toastTweenDuration;
	private int lastIndex = -1;
	private final int blockNum = 4;
	private Timeline animation_line = null;
	private Tween tween;
	boolean bCanScroll = false;
	
	public circlePopWnd3D(
			float x ,
			float y )
	{
		this( null , x , y );
	}
	
	public circlePopWnd3D(
			String name ,
			float x ,
			float y )
	{
		super( name );
		centerX = Utils3D.getScreenWidth() / 2;
		if( y < R3D.circle_unfocus_backgroud_width / 2 + R3D.getInteger( "page_indicator_height" ) + R3D.page_indicator_y )
		{
			centerY = R3D.circle_unfocus_backgroud_width / 2 + R3D.getInteger( "page_indicator_height" ) + R3D.page_indicator_y;
		}
		else if( y > 3 * Utils3D.getScreenHeight() / 4 )
		{
			centerY = 3 * Utils3D.getScreenHeight() / 4;
		}
		else
		{
			centerY = y;
		}
		// buildElements();
		this.setOrigin( centerX , centerY );
	}
	
	private Bitmap createBmpWithString(
			int w ,
			int h ,
			String title )
	{
		Paint textPaint = new Paint();
		textPaint.setColor( 0xFFFFFFFF );
		textPaint.setAntiAlias( true );
		textPaint.setTextSize( R3D.icon_title_font );
		textPaint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
		// 创建一个新的和SRC长度宽度一样的位图
		Bitmap newb = Bitmap.createBitmap( w , h , Config.ARGB_8888 );
		Canvas cv = new Canvas( newb );
		textPaint.setColor( 0xFFFFFFFF );
		float paddintText = ( w - textPaint.measureText( title ) ) / 2;
		FontMetrics fontMetrics = textPaint.getFontMetrics();
		cv.drawText( title , paddintText , (float)( h - Math.ceil( fontMetrics.ascent + fontMetrics.descent ) ) / 2f , textPaint );
		// return Utils3D.bmp2Pixmap(newb);
		return newb;
	}
	
	private void startToastAnimation()
	{
		animation_line = null;
		float animDuration = 0.5f;
		animation_line = Timeline.createParallel();
		TweenEquation easeEquation = Back.OUT;
		toastAutoSort.setScale( 0f , 0f );
		animation_line.push( Tween.to( toastAutoSort , View3DTweenAccessor.POS_XY , animDuration ).target( centerX - toastWidth / 2 , centerY + unfocusRadius ).ease( easeEquation ) );
		animation_line.push( Tween.to( toastAutoSort , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		toastOverLap.setScale( 0 , 0 );
		animation_line.push( Tween.to( toastOverLap , View3DTweenAccessor.POS_XY , animDuration ).target( centerX + unfocusRadius - toastHeight , centerY + toastHeight ).ease( easeEquation ) );
		animation_line.push( Tween.to( toastOverLap , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		animation_line.push( Tween.to( toastDelAll , View3DTweenAccessor.POS_XY , animDuration ).target( centerX + unfocusRadius / 2 , centerY - unfocusRadius + toastHeight ).ease( easeEquation ) );
		toastDelAll.setScale( 0 , 0 );
		animation_line.push( Tween.to( toastDelAll , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		animation_line.push( Tween.to( toastCreateFolder , View3DTweenAccessor.POS_XY , animDuration ).target( centerX - unfocusRadius - toastWidth / 2 , centerY - unfocusRadius + toastHeight )
				.ease( easeEquation ) );
		toastCreateFolder.setScale( 0 , 0 );
		animation_line.push( Tween.to( toastCreateFolder , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		animation_line.push( Tween.to( toastMultiSelect , View3DTweenAccessor.POS_XY , animDuration ).target( centerX - unfocusRadius - toastWidth + toastHeight , centerY + toastHeight )
				.ease( easeEquation ) );
		toastMultiSelect.setScale( 0 , 0 );
		animation_line.push( Tween.to( toastMultiSelect , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		animation_line.start( View3DTweenAccessor.manager );
	}
	
	private void buildToast()
	{
		// TextureRegion temp= new TextureRegion(new
		// Texture(Gdx.files.internal("data/pop_autosort.png")));
		// bmp = Bitmap.createScaledBitmap(titleBg,R3D.workspace_cell_width,
		// 26,false);
		AtlasRegion tempRegion = R3D.getTextureRegion( "shell-picker-bg" );
		NinePatch ninePatch = new NinePatch( tempRegion , 30 , 30 , 10 , 10 );
		Bitmap temp = createBmpWithString( R3D.pop_toast_width , R3D.pop_toast_height , R3D.circle_autoSort );
		toastAutoSort = new ImageView3D( "toastAutoSort" , new BitmapTexture( temp , true ) );
		toastAutoSort.setBackgroud( ninePatch );
		temp = createBmpWithString( R3D.pop_toast_width , R3D.pop_toast_height , R3D.circle_iconTrans );
		toastOverLap = new ImageView3D( "toastOverLap" , new BitmapTexture( temp , true ) );
		toastOverLap.setBackgroud( ninePatch );
		temp = createBmpWithString( R3D.pop_toast_width , R3D.pop_toast_height , R3D.circle_delAll );
		toastDelAll = new ImageView3D( "toastDelAll" , new BitmapTexture( temp , true ) );
		toastDelAll.setBackgroud( ninePatch );
		temp = createBmpWithString( R3D.pop_toast_width , R3D.pop_toast_height , R3D.circle_createFolder );
		toastCreateFolder = new ImageView3D( "toastCreateFolder" , new BitmapTexture( temp , true ) );
		toastCreateFolder.setBackgroud( ninePatch );
		temp = createBmpWithString( R3D.pop_toast_width , R3D.pop_toast_height , R3D.circle_multiSelect );
		// temp=new TextureRegion(new
		// Texture(Gdx.files.internal("data/pop_multisel.png")));
		toastMultiSelect = new ImageView3D( "toastMultiSelect" , new BitmapTexture( temp , true ) );
		toastMultiSelect.setBackgroud( ninePatch );
		// toastAutoSort.setSize(R3D.pop_toast_width,32);
		// toastOverLap.setSize(R3D.pop_toast_width, R3D.pop_toast_height);
		// toastDelAll.setSize(R3D.pop_toast_width, R3D.pop_toast_height);
		// toastCreateFolder.setSize(R3D.pop_toast_width, R3D.pop_toast_height);
		// toastMultiSelect.setSize(R3D.pop_toast_width, R3D.pop_toast_height);
		toastWidth = toastAutoSort.getWidth();
		toastHeight = toastAutoSort.getHeight();
		// toastAutoSort.setPosition(centerX-toastWidth/2,centerY+unfocusRadius);
		// toastOverLap.setPosition(centerX+unfocusRadius-toastHeight,
		// centerY+toastHeight);
		// toastDelAll.setPosition(centerX+unfocusRadius/2,
		// centerY-unfocusRadius+toastHeight);
		// toastCreateFolder.setPosition(centerX-unfocusRadius-toastWidth/2,
		// centerY-unfocusRadius+toastHeight);
		// toastMultiSelect.setPosition(centerX-unfocusRadius-toastWidth+toastHeight,
		// centerY+toastHeight);
		toastAutoSort.setPosition( centerX - R3D.pop_toast_width / 2 , centerY - R3D.pop_toast_height / 2 );
		toastOverLap.setPosition( centerX - R3D.pop_toast_width / 2 , centerY - R3D.pop_toast_height / 2 );
		toastDelAll.setPosition( centerX - R3D.pop_toast_width / 2 , centerY - R3D.pop_toast_height / 2 );
		toastCreateFolder.setPosition( centerX - R3D.pop_toast_width / 2 , centerY - R3D.pop_toast_height / 2 );
		toastMultiSelect.setPosition( centerX - R3D.pop_toast_width / 2 , centerY - R3D.pop_toast_height / 2 );
		addView( toastAutoSort );
		addView( toastOverLap );
		addView( toastDelAll );
		addView( toastCreateFolder );
		addView( toastMultiSelect );
		startToastAnimation();
		backgroudImgFocus.setPosition( centerX - offsetX , centerY - R3D.circle_focus_offset_y );
		backgroudImgFocus.setOrigin( offsetX , R3D.circle_focus_offset_y );
		backgroudImgFocus.setRotation( 0 );
		// addView(backgroudImgFocus);
		// tween=backgroudImgFocus.startTween(View3DTweenAccessor.ROTATION,
		// Circ.IN, 0.3f,
		// 720, 0,0);
	}
	
	public void buildElements()
	{
		requestFocus();
		backgroudUnfocusRegion = R3D.getTextureRegion( "shell-picker-menu-item1" );
		backgroudRegion = R3D.getTextureRegion( "shell-picker-menu-item2" );
		backgroudImgFocus = new ImageView3D( "backgroudImgFocus" , backgroudRegion );
		backgroudImgFocus.setSize( R3D.circle_focus_backgroud_width , R3D.circle_focus_backgroud_height );
		overLapImgFocus = new ImageView3D( "overLapImgFocus" , R3D.getTextureRegion( "shell-picker-menu-item5b" ) );
		overLapImgUnFocus = new ImageView3D( "overLapImgUnFocus" , R3D.getTextureRegion( "shell-picker-menu-item5a" ) );
		overLapImgFocus.setSize( R3D.circle_overlap_width , R3D.circle_overlap_height );
		overLapImgUnFocus.setSize( R3D.circle_overlap_width , R3D.circle_overlap_height );
		deleteAllImgFocus = new ImageView3D( "deleteAllImgFocus" , R3D.getTextureRegion( "shell-picker-menu-item4b" ) );
		deleteAllImgUnFocus = new ImageView3D( "deleteAllImgUnFocus" , R3D.getTextureRegion( "shell-picker-menu-item4a" ) );
		deleteAllImgFocus.setSize( R3D.circle_delall_width , R3D.circle_delall_height );
		deleteAllImgUnFocus.setSize( R3D.circle_delall_width , R3D.circle_delall_height );
		CreateFolderImgFocus = new ImageView3D( "CreateFolderImgFocus" , R3D.getTextureRegion( "shell-picker-menu-item6b" ) );
		CreateFolderImgUnFocus = new ImageView3D( "CreateFolderImgUnFocus" , R3D.getTextureRegion( "shell-picker-menu-item6a" ) );
		CreateFolderImgFocus.setSize( R3D.circle_folder_width , R3D.circle_folder_height );
		CreateFolderImgUnFocus.setSize( R3D.circle_folder_width , R3D.circle_folder_height );
		multiSelectImgFocus = new ImageView3D( "multiSelectImgFocus" , R3D.getTextureRegion( "shell-picker-menu-item3b" ) );
		multiSelectImgUnFocus = new ImageView3D( "multiSelectImgUnFocus" , R3D.getTextureRegion( "shell-picker-menu-item3a" ) );
		multiSelectImgFocus.setSize( R3D.circle_multiselect_width , R3D.circle_multiselect_height );
		multiSelectImgUnFocus.setSize( R3D.circle_multiselect_width , R3D.circle_multiselect_height );
		autoSortImgFocus = new ImageView3D( "autoSortImgFocus" , R3D.getTextureRegion( "shell-picker-menu-item7b" ) );
		autoSortImgUnFocus = new ImageView3D( "autoSortImgUnFocus" , R3D.getTextureRegion( "shell-picker-menu-item7a" ) );
		autoSortImgFocus.setSize( R3D.circle_autosort_width , R3D.circle_autosort_height );
		autoSortImgUnFocus.setSize( R3D.circle_autosort_width , R3D.circle_autosort_height );
		offsetX = backgroudImgFocus.getWidth() / 2;
		backgroudImgUnFocus = new ImageView3D( "backgroudImgUnFocus" , backgroudUnfocusRegion );
		backgroudImgUnFocus.setSize( R3D.circle_unfocus_backgroud_width , R3D.circle_unfocus_backgroud_height );
		unfocusRadius = backgroudImgUnFocus.getWidth() / 2;
		backgroudImgUnFocus.setPosition( centerX - unfocusRadius , centerY - unfocusRadius );
		addView( backgroudImgUnFocus );
		setUnFocusImgPos();
	}
	
	private void setUnFocusImgPos()
	{
		float rotateRadius = R3D.circle_unfocus_backgroud_width / 2f;
		rotateRadius = rotateRadius / 2f;
		addView( multiSelectImgUnFocus );
		addView( CreateFolderImgUnFocus );
		addView( autoSortImgUnFocus );
		addView( deleteAllImgUnFocus );
		multiSelectImgUnFocus.hide();
		multiSelectImgUnFocus.setPosition( centerX , centerY );
		CreateFolderImgUnFocus.hide();
		CreateFolderImgUnFocus.setPosition( centerX , centerY );
		autoSortImgUnFocus.hide();
		autoSortImgUnFocus.setPosition( centerX , centerY );
		deleteAllImgUnFocus.hide();
		deleteAllImgUnFocus.setPosition( centerX , centerY );
		backgroudImgFocus.setPosition( centerX - backgroudImgFocus.getWidth() / 2 , centerY + rotateRadius / 2f );
		backgroudImgFocus.setOrigin( backgroudImgFocus.getWidth() / 2 , -rotateRadius / 2f );
		backgroudImgFocus.setRotation( 0 );
		startElemAnimation();
	}
	
	private void startElemAnimation()
	{
		float rotateRadius = R3D.circle_unfocus_backgroud_width / 2f;
		rotateRadius = rotateRadius / 2f;
		animation_line = null;
		float animDuration = 0.5f;
		animation_line = Timeline.createParallel();
		TweenEquation easeEquation = Back.OUT;
		autoSortImgUnFocus.show();
		autoSortImgUnFocus.setScale( 0f , 0f );
		animation_line.push( Tween.to( autoSortImgUnFocus , View3DTweenAccessor.POS_XY , animDuration )
				.target( centerX - autoSortImgUnFocus.getWidth() / 2 , centerY + rotateRadius + rotateRadius / 2f - R3D.circle_drawtext_y - autoSortImgUnFocus.getHeight() / 2 ).ease( easeEquation ) );
		animation_line.push( Tween.to( autoSortImgUnFocus , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		multiSelectImgUnFocus.show();
		multiSelectImgUnFocus.setScale( 0 , 0 );
		animation_line.push( Tween.to( multiSelectImgUnFocus , View3DTweenAccessor.POS_XY , animDuration )
				.target( centerX - rotateRadius - rotateRadius / 2f - multiSelectImgUnFocus.getWidth() / 2 , centerY - multiSelectImgUnFocus.getHeight() / 2 ).ease( easeEquation ) );
		animation_line.push( Tween.to( multiSelectImgUnFocus , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		CreateFolderImgUnFocus.show();
		CreateFolderImgUnFocus.setScale( 0 , 0 );
		animation_line.push( Tween.to( CreateFolderImgUnFocus , View3DTweenAccessor.POS_XY , animDuration )
				.target( centerX - CreateFolderImgUnFocus.getWidth() / 2 , centerY - rotateRadius - rotateRadius / 2f + R3D.circle_drawtext_y - CreateFolderImgUnFocus.getHeight() / 2 )
				.ease( easeEquation ) );
		animation_line.push( Tween.to( CreateFolderImgUnFocus , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		deleteAllImgUnFocus.show();
		deleteAllImgUnFocus.setScale( 0 , 0 );
		animation_line.push( Tween.to( deleteAllImgUnFocus , View3DTweenAccessor.POS_XY , animDuration )
				.target( centerX + rotateRadius / 2f + rotateRadius - deleteAllImgUnFocus.getWidth() / 2 , centerY - deleteAllImgUnFocus.getHeight() / 2 ).ease( easeEquation ) );
		animation_line.push( Tween.to( deleteAllImgUnFocus , View3DTweenAccessor.SCALE_XY , animDuration ).target( 1f , 1f ).ease( Linear.INOUT ) );
		animation_line.start( View3DTweenAccessor.manager );
	}
	
	private void circle_addView(
			View3D actor )
	{
		if( findView( autoSortImgFocus.name ) != null )
		{
			removeView( autoSortImgFocus );
		}
		if( findView( multiSelectImgFocus.name ) != null )
		{
			removeView( multiSelectImgFocus );
		}
		if( findView( CreateFolderImgFocus.name ) != null )
		{
			removeView( CreateFolderImgFocus );
		}
		if( findView( deleteAllImgFocus.name ) != null )
		{
			removeView( deleteAllImgFocus );
		}
		if( findView( overLapImgFocus.name ) != null )
		{
			removeView( overLapImgFocus );
		}
		addView( actor );
	}
	
	private void circle_operate(
			float time_duration ,
			int x ,
			int y )
	{
		getHighLightIndex( x , y );
		if( lastIndex == mHighLightIndex )
		{
			return;
		}
		// float currentRation=mHighLightIndex*360/blockNum;
		switch( mHighLightIndex )
		{
			case CIRCLE_POP_AUTOSORT_EVENT:
				autoSortImgFocus.setPosition( autoSortImgUnFocus.x , autoSortImgUnFocus.y );
				circle_addView( autoSortImgFocus );
				break;
			case CIRCLE_POP_MULTISEL_EVENT:
				multiSelectImgFocus.setPosition( multiSelectImgUnFocus.x , multiSelectImgUnFocus.y );
				// beforeHighLight=CIRCLE_POP_MULTISEL_EVENT;
				circle_addView( multiSelectImgFocus );
				break;
			case CIRCLE_POP_CREATEFOLDER_EVENT:
				CreateFolderImgFocus.setPosition( CreateFolderImgUnFocus.x , CreateFolderImgUnFocus.y );
				circle_addView( CreateFolderImgFocus );
				break;
			case CIRCLE_POP_DELALL_EVENT:
				deleteAllImgFocus.setPosition( deleteAllImgUnFocus.x , deleteAllImgUnFocus.y );
				circle_addView( deleteAllImgFocus );
				break;
			case CICLE_POP_OVERLAP_EVENT:
				overLapImgFocus.setPosition( overLapImgUnFocus.x , overLapImgUnFocus.y );
				// beforeHighLight=CICLE_POP_OVERLAP_EVENT;
				circle_addView( overLapImgFocus );
				break;
		}
		backgroudImgFocus.stopTween();
		if( mHighLightIndex == CIRCLE_POP_AUTOSORT_EVENT )
		{
			if( lastIndex == CIRCLE_POP_DELALL_EVENT )
			{
				tween = backgroudImgFocus.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , time_duration , 360 , 0 , 0 );
			}
			else
			{
				tween = backgroudImgFocus.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , time_duration , 0 , 0 , 0 );
			}
		}
		else
		{
			if( ( blockNum - mHighLightIndex ) > mHighLightIndex )
			{
				/* 顺时针旋 */
				if( lastIndex == CIRCLE_POP_AUTOSORT_EVENT )
				{
					backgroudImgFocus.setRotation( 0 );
				}
			}
			else
			{
				/* 反时针旋 */
				if( lastIndex == CIRCLE_POP_AUTOSORT_EVENT || lastIndex == -1 )
				{
					backgroudImgFocus.setRotation( 360 );
				}
			}
			tween = backgroudImgFocus.startTween( View3DTweenAccessor.ROTATION , Linear.INOUT , time_duration , mHighLightIndex * 360 / blockNum , 0 , 0 );
		}
		lastIndex = mHighLightIndex;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		return true;
	}
	
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		return true;
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iLoong.launcher.UI3DEngine.ViewGroup3D#scroll(int, int, int,
	 * int)
	 */
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		if( bCanScroll == false )
		{
			return true;
		}
		if( mHighLightIndex == CIRCLE_POP_DESTROY_EVENT )
		{
			return true;
		}
		circle_operate( 0.2f , (int)x , (int)y );
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return true;
		}
		View3D hitView = hit( x , y );
		if( hitView == null )
		{
			return false;
		}
		bCanScroll = true;
		if( hit( x , y ).name == this.name )
		{
			mHighLightIndex = CIRCLE_POP_DESTROY_EVENT;
			return true;
		}
		if( findView( backgroudImgFocus.name ) != null )
		{
			removeView( backgroudImgFocus );
			addView( backgroudImgFocus );
		}
		else
		{
			addView( backgroudImgFocus );
		}
		circle_operate( 0.3f , (int)x , (int)y );
		// circle_setHighLight(x,y);
		return true;
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
			return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			mHighLightIndex = CIRCLE_POP_DESTROY_EVENT;
			releaseFocus();
			this.viewParent.onCtrlEvent( this , mHighLightIndex );
			return true;
		}
		return super.keyUp( keycode );
	}
	
	private void getHighLightIndex(
			float x ,
			float y )
	{
		float retDeg = Utils3D.getRotDegrees( x , y , centerX , centerY );
		int blockDeg = 360 / blockNum;
		if( retDeg > 0 && retDeg <= ( blockDeg / 2 ) )
		{
			// mHighLightIndex=CICLE_POP_OVERLAP_EVENT;
			mHighLightIndex = CIRCLE_POP_DELALL_EVENT;
		}
		else
		{
			mHighLightIndex = (int)( ( retDeg - ( 90 - blockDeg / 2 ) ) / blockDeg );
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer == 1 )
		{
			return true;
		}
		if( mHighLightIndex >= 0 )
		{
			this.viewParent.onCtrlEvent( this , mHighLightIndex );
		}
		return true;
	}
}
