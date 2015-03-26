package com.iLoong.launcher.Folder3D;


import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.NPageBase;
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


/* Folder3D Layout height=Utils3D.getScreenHeight()-R3D.workspace_cell_height girdHeight=height-R3D.icongroup_button_height/2
 * 
 * buttonHeight/2 girdHeight R3D.workspace_Cell_height */
public class FolderMIUI3D extends ViewGroup3D implements DragSource3D , TextFieldListener
{
	
	private float iconX;
	private float iconY;
	public static final int MSG_ON_DROP = 0;
	public static final int MSG_UPDATE_VIEW = 1;
	private FolderIcon3D mFolderIcon;
	private IconContainer iconContainer;
	private ViewGroup3D folderContainer;
	private ImageView3D inputTextView;
	private float inputTextWidth = 220;
	private float inputTextPosX = 247f;
	private float inputTextPosY = 858f;
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
	// private static boolean displayButton = true;
	private float rename_button_width;
	private float rename_button_height;
	private float rename_button_offset;
	private final static float rename_button_width_const = 75;
	private final static float rename_button_height_const = 54;
	private final static float rename_button_offset_const = 12;
	// private static TextureRegion buttonOKFocusRegion = null;
	// private static TextureRegion buttonOKNormalRegion = null;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	// zhujieping add
	private static ImageView3D bgView = null;
	private Timeline v5_animation_line = null;
	private static ImageView3D folderBg;
	private static float folderWidth;
	private static float folderHeight;
	private static ViewGroup3D folderCoverContainer;
	private static ImageView3D folderCover;
	public float folderCoverRotation = -100;
	private static float folderCoverWidth;
	private static float folderCoverHeight;
	private int gridPaddingLeft = 60;
	private int gridPaddingRight = 60;
	private int gridPaddingTop = 80;
	private int gridPaddingBottom = 50;
	public static boolean useOrignalDialog = true;
	
	public FolderMIUI3D()
	{
		this( null );
		// TODO Auto-generated constructor stub
	}
	
	public FolderMIUI3D(
			String name )
	{
		super( name );
		float scaleFactorW = Utils3D.getScreenWidth() / 720f;
		gridPaddingLeft = (int)( gridPaddingLeft * scaleFactorW + 0.5f );
		gridPaddingRight = (int)( gridPaddingRight * scaleFactorW + 0.5f );
		gridPaddingTop = (int)( gridPaddingTop * scaleFactorW + 0.5f );
		gridPaddingBottom = (int)( gridPaddingBottom * scaleFactorW + 0.5f );
		if( folderBg == null )
		{
			folderBg = new ImageView3D( "folderBg" , R3D.getTextureRegion( R3D.folder_bg ) );
			folderWidth = folderBg.getWidth() * scaleFactorW;
			folderHeight = folderBg.getHeight() * scaleFactorW;
			folderBg.setSize( folderWidth , folderHeight );
			folderBg.setOrigin( folderWidth / 2 , folderHeight / 2 );
		}
		if( folderCover == null )
		{
			folderCover = new ImageView3D( "folderCover" , R3D.getTextureRegion( R3D.folder_cover ) );
			folderCoverWidth = folderCover.getWidth() * scaleFactorW;
			folderCoverHeight = folderCover.getHeight() * scaleFactorW;
			folderCover.setSize( folderCoverWidth , folderCoverHeight );
			folderCover.setOrigin( folderCoverWidth / 2 , folderCoverHeight );
		}
		if( folderCoverContainer == null )
		{
			folderCoverContainer = new ViewGroup3D( "folderCoverContainer" );
			folderCoverContainer.transform = true;
			folderCoverContainer.setRotationVector( 1 , 0 , 0 );
			folderCoverContainer.setSize( folderCoverWidth , folderCoverHeight );
			folderCoverContainer.setOrigin( folderCoverWidth / 2 , folderCoverHeight );
		}
		folderContainer = new ViewGroup3D( "folderContainer" );
		folderContainer.transform = true;
		folderContainer.setSize( folderWidth , folderHeight );
		inputTextPosX = inputTextPosX * scaleFactorW;
		inputTextPosY = ( Utils3D.getScreenHeight() - folderHeight ) / 2 + 568 * scaleFactorW;
		if( Utils3D.getScreenWidth() == 480 )
		{
			inputTextPosY -= 5;
		}
		inputTextWidth = inputTextWidth * scaleFactorW;
	}
	
	public void buildV5Elements()
	{
		rename_button_width = rename_button_width_const * SetupMenu.mScale;
		rename_button_height = rename_button_height_const * SetupMenu.mScale;
		rename_button_offset = rename_button_offset_const * SetupMenu.mScale;
		buildV5IconGroup();
	}
	
	public void setUpdateValue(
			boolean bFlag )
	{
		bNeedUpdate = bFlag;
		bEnableTouch = true;
	}
	
	public boolean getColseFolderByDragVal()
	{
		return bCloseFolderByDrag;
	}
	
	public void addGridChild()
	{
		View3D myActor;
		int Count = mFolderIcon.getChildCount();
		bOutDragRemove = false;
		bCloseFolderByDrag = false;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = mFolderIcon.getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				myActor.show();
				myActor.setRotation( 0 );
				myActor.setScale( 1.0f , 1.0f );
				mFolderIcon.changeTextureRegion( myActor , R3D.workspace_cell_height );
				( (Icon3D)myActor ).setInShowFolder( true );
				( (Icon3D)myActor ).setItemInfo( ( (Icon3D)myActor ).getItemInfo() );
				myActor.x = myActor.x - this.x;
				myActor.y = myActor.y - this.y;
				// teapotXu add start for Folder in Mainmenu
				// only when folder is in Mainmenu, hide icons in folder are
				// needed to hide
				if( DefaultLayout.mainmenu_folder_function == true && ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D ) )
				{
					if( ( (Icon3D)myActor ).getItemInfo() instanceof ShortcutInfo )
					{
						ApplicationInfo appInfo = ( (ShortcutInfo)( (Icon3D)myActor ).getItemInfo() ).appInfo;
						if( appInfo != null && appInfo.isHideIcon && ( (Icon3D)myActor ).getHideStatus() == false )
						{
							// don't add this icon into the FolderList.
							continue;
						}
					}
				}
				// teapotXu add end for Folder in Mainmenu
				myActor.show();
				templist.add( myActor );
			}
		}
		if( iconContainer != null )
		{
			int gridPageCount;
			if( templist.size() % R3D.folder_max_num == 0 )
			{
				gridPageCount = templist.size() / R3D.folder_max_num;
			}
			else
			{
				gridPageCount = templist.size() / R3D.folder_max_num + 1;
			}
			ArrayList<View3D> iconList = new ArrayList<View3D>();
			for( int i = 0 , j = 0 ; i < gridPageCount ; i++ )
			{
				IconGrid gridPage = new IconGrid( "gridPage" , folderWidth , folderHeight , R3D.folder_group_child_count_x , R3D.folder_group_child_count_y );
				gridPage.setPadding( gridPaddingLeft , gridPaddingRight , gridPaddingTop , gridPaddingBottom );
				for( ; j < templist.size() ; )
				{
					iconList.add( templist.get( j ) );
					j++;
					if( j % R3D.folder_max_num == 0 )
					{
						break;
					}
				}
				gridPage.addItem( iconList );
				iconList.clear();
				iconContainer.addPage( i , gridPage );
			}
			iconContainer.show();
			iconContainer.setCurrentPage( 0 );
		}
		// teapotXu add start for Folder in Mainmenu
		// if (DefaultLayout.mainmenu_folder_function == true) {
		// if (mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D) {
		// if (templist.size() > 0
		// && ((Icon3D) templist.get(0)).getUninstallStatus() == false) {
		// iconContain.setAutoDrag(false);
		// }
		// }
		// }
		// teapotXu add end for Folder in Mainmenu
	}
	
	public void addIcon(
			Icon3D newIcon )
	{
		GridView3D curGridPage = null;
		if( iconContainer != null )
		{
			curGridPage = (GridView3D)iconContainer.getCurrentView();
		}
		if( curGridPage == null || curGridPage.getChildCount() == 0 )
		{
			return;
		}
		newIcon.x = curGridPage.getChildAt( 0 ).x;
		newIcon.y = curGridPage.getChildAt( 0 ).y;
		curGridPage.addItem( newIcon );
	}
	
	public void updateIcon(
			Icon3D widgetIcon ,
			Icon3D newIcon )
	{
		GridView3D curGridPage = null;
		if( iconContainer != null )
		{
			curGridPage = (GridView3D)iconContainer.getCurrentView();
		}
		if( curGridPage == null || curGridPage.getChildCount() == 0 )
		{
			return;
		}
		newIcon.x = widgetIcon.x;
		newIcon.y = widgetIcon.y;
		curGridPage.removeView( widgetIcon );
		curGridPage.addItem( newIcon );
	}
	
	public void updateTexture()
	{
		GridView3D curGridPage = null;
		if( iconContainer != null )
		{
			curGridPage = (GridView3D)iconContainer.getCurrentView();
		}
		if( curGridPage == null || curGridPage.getChildCount() == 0 )
		{
			return;
		}
		int Count = curGridPage.getChildCount();
		View3D myActor;
		ItemInfo tempInfo;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = curGridPage.getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				tempInfo = ( (Icon3D)myActor ).getItemInfo();
				Log.e( "test" , "mFolder 3d updateTexture i=" + i + "myActor=" + myActor );
				if( ( (ShortcutInfo)tempInfo ).usingFallbackIcon == true )
				{
					( (ShortcutInfo)tempInfo ).usingFallbackIcon = false;
					Log.e( "test" , "mFolder change using fallbackIcon" );
				}
				myActor.region = new TextureRegion( R3D.findRegion( (ShortcutInfo)tempInfo ) );
			}
		}
	}
	
	private void buildV5IconGroup()
	{
		int countX = R3D.folder_group_child_count_x;
		if( countX <= 0 )
		{
			countX = 4;
		}
		int CountSize = mFolderIcon.mInfo.contents.size();
		int countY = 3;// (CountSize + countX - 1) / countX;
		if( countY == 0 )
		{
			countY = 1;
		}
		int childMargin = R3D.icongroup_margin_left;
		if( bgView == null )
		{
			bgView = new ImageView3D( "full_screen_bg" );
			bgView.setSize( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
			bgView.getColor().a = 0;
			bgView.setBackgroud( new NinePatch( R3D.screenBackRegion ) );
		}
		if( iconContainer == null )
		{
			iconContainer = new IconContainer( "iconContainer" );
			iconContainer.setSize( folderWidth , folderHeight );
		}
		titleHeight = R3D.folder_group_text_height;
		if( inputTextView == null )
		{
			inputTextView = new ImageView3D( "inputTextView" );
		}
		inputTextView.setPosition( inputTextPosX , inputTextPosY );
		inputTextView.setSize( inputTextWidth , titleHeight );
		inputTextView.setOrigin( inputTextView.getWidth() / 2 , inputTextView.getHeight() / 2 );
		if( !inputTextView.isVisible() )
		{
			inputTextView.show();
		}
		inputNameString = mFolderIcon.mInfo.title.toString();
		String title = inputNameString;
		if( title.endsWith( "x.z" ) )
		{
			int length = title.length();
			if( length > 3 )
			{
				title = title.substring( 0 , length - 3 );
			}
		}
		if( titleTexture == null )
		{
			Bitmap bmp = titleToTexture( mFolderIcon.mInfo.title.toString() , titleHeight );
			titleTexture = new BitmapTexture( bmp );
			if( bmp != null && !bmp.isRecycled() )
				bmp.recycle();
		}
		animation_line = Timeline.createParallel();
		if( folderContainer != null )
		{
			folderContainer.setOrigin( 0 , 0 );
			float posX = 0;
			float posY = 0;
			float scaleX = 0;
			float scaleY = 0;
			if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
			{
				posX = mFolderIcon.mInfo.x + ( R3D.workspace_cell_width - DefaultLayout.app_icon_size ) / 2;
				posY = mFolderIcon.mInfo.y + R3D.workspace_cell_height - DefaultLayout.app_icon_size - FolderIcon3D.folderMarginTop;
				scaleX = DefaultLayout.app_icon_size / folderBg.getWidth();
				scaleY = DefaultLayout.app_icon_size / folderBg.getHeight();
			}
			else if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_HOTSEAT )
			{
				posX = mFolderIcon.mInfo.x + ( R3D.workspace_cell_width - DefaultLayout.app_icon_size ) / 2;
				posY = mFolderIcon.mInfo.y - 10;
				scaleX = DefaultLayout.app_icon_size / folderBg.getWidth();
				scaleY = mFolderIcon.getIconBmpHeight() / folderBg.getHeight();
			}
			folderContainer.setPosition( posX , posY );
			folderContainer.setScale( scaleX , scaleY );
			animation_line.push( folderContainer.obtainTween(
					View3DTweenAccessor.POS_XY ,
					Cubic.INOUT ,
					DefaultLayout.blurDuration ,
					( Utils3D.getScreenWidth() - folderContainer.getWidth() ) / 2 ,
					( Utils3D.getScreenHeight() - folderContainer.getHeight() ) / 2 ,
					0 ) );
			animation_line.push( folderContainer.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.INOUT , DefaultLayout.blurDuration , 1 , 1 , 0 ) );
			addView( folderContainer );
		}
		if( folderBg != null )
		{
			folderContainer.addView( folderBg );
		}
		folderContainer.addView( iconContainer );
		if( folderCover != null )
		{
			folderCoverContainer.addView( folderCover );
		}
		if( folderCoverContainer != null )
		{
			folderContainer.addView( folderCoverContainer );
			folderCoverContainer.setRotation( 0 );
			animation_line.push( folderCoverContainer.obtainTween( View3DTweenAccessor.ROTATION , Back.OUT , 0.6f , folderCoverRotation , 0 , 0 ).delay( 0.3f ) );
		}
		addView( inputTextView );
		mFolderIcon.bRenameFolder = false;
		animation_line.start( View3DTweenAccessor.manager );
	}
	
	public void onInputNameChanged()
	{
		Texture t;
		Texture texture;
		titleHeight = R3D.folder_group_text_height;
		texture = new BitmapTexture( titleToTexture( inputNameString , titleHeight ) , true );
		t = this.titleTexture;
		this.titleTexture = texture;
		if( t != null )
			t.dispose();
	}
	
	private Bitmap titleToTexture(
			String title ,
			int titleHeight )
	{
		Bitmap bmp = Bitmap.createBitmap( (int)inputTextWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		Paint paint = new Paint();
		paint.setColor( R3D.folder_title_color );
		// teapotXu add start for MIUI folder
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )// zhujieping
		{
			paint.setColor( Color.WHITE );
		}
		// teapotXu add end for MIUI folder
		paint.setAntiAlias( true );
		// paint.setTextSize(Utils3D.getDensity()*titleHeight/3);
		if( title.endsWith( "x.z" ) )
		{
			int length = title.length();
			if( length > 3 )
			{
				title = title.substring( 0 , length - 3 );
			}
		}
		paint.setTextSize( titleHeight / 2 );
		//		if (Utils3D.measureText(paint, title) > inputTextWidth - 2) {
		//			while (Utils3D.measureText(paint, title) > inputTextWidth
		//					- Utils3D.measureText(paint, "...") - 2) {
		//				title = title.substring(0, title.length() - 1);
		//			}
		//			title += "...";
		//		}
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
		offsetX = ( inputTextWidth - Utils3D.measureText( paint , title ) ) / 2f;
		if( offsetX < 0 )
		{
			offsetX = 0;
		}
		float fontPosY = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		fontPosY = titleHeight - ( titleHeight - fontPosY ) / 2f - fontMetrics.bottom;
		canvas.drawText( title , offsetX , fontPosY , paint );
		// draw opacity
		if( Utils3D.measureText( paint , title ) > inputTextWidth )
		{
			Paint mErasePaint = new Paint();
			mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
			mErasePaint.setAntiAlias( true );
			float alphaW = paint.measureText( "x" );
			float a = 255f / alphaW;
			for( int j = 0 ; j < alphaW ; j++ )
			{
				mErasePaint.setAlpha( (int)( a * j ) );
				canvas.drawLine( inputTextWidth - j - 1 , (float)( fontPosY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , inputTextWidth - j , titleHeight , mErasePaint );
			}
		}
		return bmp;
	}
	
	private void startAnimation()
	{
		//		float delayFactor = 0.05f;
		//		View3D view;
		animation_line = Timeline.createParallel();
		//		int Count = templist.size();
		float duration = DefaultLayout.blurDuration;
		// zhujieping add
		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder;
		if( miuiV5Folder )
		{
			if( DefaultLayout.blur_enable )
			{
				if( mFolderIcon.blurredView != null )
				{
					mFolderIcon.blurredView.remove();
				}
				if( mFolderIcon.lwpBackView != null )
				{
					mFolderIcon.lwpBackView.remove();
				}
				mFolderIcon.mFolderIndex = mFolderIcon.getViewIndex( mFolderIcon.mFolderMIUI3D );
				mFolderIcon.mFolderMIUI3D.remove();
				final Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
				root.transform = true;
				root.setScale( 0.8f , 0.8f );
				root.showOtherView();
				animation_line.push( root.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.INOUT , duration , 1.0f , 1.0f , 0 ) );
				animation_line.push( root.getHotSeatBar().getModel3DGroup().obtainTween( View3DTweenAccessor.OPACITY , Cubic.INOUT , duration , 1 , 0 , 0 ) );
			}
			else
			{
				duration = 0.4f;
			}
			if( bgView != null )
			{
				animation_line.push( Tween.to( bgView , View3DTweenAccessor.OPACITY , 0.4f ).target( 0 , 0 , 0 ).ease( Cubic.INOUT ) );
			}
		}
		// teapotXu add end for Miui2 folder
		// removed by Hugo.ye begin 20131115
		// for (int i = 0; i < Count; i++) {
		// view = templist.get(i);
		// view.stopAllTween();
		// mFolderIcon.addViewBefore(mFolderIcon.folder_front, view);
		// mFolderIcon.changeOrigin(view);
		// mFolderIcon.changeTextureRegion(view,
		// mFolderIcon.getIconBmpHeight());
		// view.y = view.y + R3D.workspace_cell_height
		// - R3D.workspace_cell_width;
		// mFolderIcon.getPos(i);
		// //zhujieping add
		// float delay = 0;
		// if (miuiV5Folder) {
		// delay = 0;
		// } else {
		// delay = delayFactor * (i / R3D.folder_icon_row_num);
		// }
		// if (miuiV5Folder) {
		// animation_line.push(Tween
		// .to(view, View3DTweenAccessor.POS_XY, duration)
		// .target(mFolderIcon.getPosx(), mFolderIcon.getPosy(), 0)
		// .ease(Cubic.OUT).delay(delay));
		// animation_line.push(Tween
		// .to(view, View3DTweenAccessor.SCALE_XY, duration)
		// .target(mFolderIcon.getScaleFactor(i),
		// mFolderIcon.getScaleFactor(i), 0).ease(Back.OUT)
		// .delay(delay));
		// } else {
		// animation_line.push(Tween
		// .to(view, View3DTweenAccessor.POS_XY, duration)
		// .target(mFolderIcon.getPosx(), mFolderIcon.getPosy(), 0)
		// .ease(Linear.INOUT)
		// .delay(delayFactor * (i / R3D.folder_icon_row_num)));
		// animation_line.push(Tween
		// .to(view, View3DTweenAccessor.SCALE_XY, duration)
		// .target(mFolderIcon.getScaleFactor(i),
		// mFolderIcon.getScaleFactor(i), 0).ease(Cubic.OUT)
		// .delay(delayFactor * (i / R3D.folder_icon_row_num)));
		// }
		// }
		// setOrigin(mFolderIcon.mInfo.x + R3D.workspace_cell_width / 2,
		// mFolderIcon.mInfo.y + R3D.workspace_cell_height / 2);
		//
		// this.stopAllTween();
		// animation_line.push(Tween
		// .to(this, View3DTweenAccessor.SCALE_XY, duration)
		// .target(0, 0, 0).ease(Cubic.OUT));
		// removed by Hugo.ye end 20131115
		this.stopAllTween();
		if( folderContainer != null )
		{
			float posX = 0;
			float posY = 0;
			float scaleX = 0;
			float scaleY = 0;
			if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_CELLLAYOUT )
			{
				posX = mFolderIcon.mInfo.x + ( R3D.workspace_cell_width - DefaultLayout.app_icon_size ) / 2;
				posY = mFolderIcon.mInfo.y + R3D.workspace_cell_height - DefaultLayout.app_icon_size - FolderIcon3D.folderMarginTop;
				scaleX = DefaultLayout.app_icon_size / folderBg.getWidth();
				scaleY = DefaultLayout.app_icon_size / folderBg.getHeight();
			}
			else if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_HOTSEAT )
			{
				posX = mFolderIcon.mInfo.x + ( R3D.workspace_cell_width - DefaultLayout.app_icon_size ) / 2;
				posY = mFolderIcon.mInfo.y;
				scaleX = DefaultLayout.app_icon_size / folderBg.getWidth();
				scaleY = mFolderIcon.getIconBmpHeight() / folderBg.getHeight();
			}
			if( !DefaultLayout.blur_enable )
			{
				animation_line.push( folderContainer.obtainTween( View3DTweenAccessor.POS_XY , Cubic.INOUT , duration , posX , posY , 0 ) );
			}
			else
			{
				animation_line.push( folderContainer.obtainTween( View3DTweenAccessor.POS_XY , Cubic.INOUT , duration , posX , posY - 10 , 0 ) );
			}
			animation_line.push( folderContainer.obtainTween( View3DTweenAccessor.SCALE_XY , Cubic.INOUT , duration , scaleX , scaleY , 0 ) );
		}
		if( folderCoverContainer != null )
		{
			animation_line.push( folderCoverContainer.obtainTween( View3DTweenAccessor.ROTATION , Cubic.OUT , duration , 0 , 0 , 0 ) );
		}
		if( iconContainer != null )
		{
			if( iconContainer.getCurrentPage() != 0 )
			{
				animation_line.push( iconContainer.obtainTween( View3DTweenAccessor.OPACITY , Cubic.IN , duration , 0 , 0 , 0 ) );
			}
		}
		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		// mFolderIcon.RootToCellLayoutAnimTime = duration;
		Log.v( "FolderMIUI3D" , "MSG_WORKSPACE_ALPAH_TO_ONE" );
		// viewParent.onCtrlEvent(mFolderIcon,
		// FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);
	}
	
	public void openV5FolderIconsAnim()
	{
		// v5_animation_line = Timeline.createParallel();
		// int Count = iconContain.getChildCount();
		// View3D myActor;
		// for (int i = 0; i < Count; i++) {
		// myActor = (Icon3D) iconContain.getChildAt(i);
		// myActor.stopAllTween();
		// mFolderIcon.changeOrigin(myActor);
		// mFolderIcon.getPos(i);
		// float pointx = myActor.x;
		// float pointy = myActor.y;
		// myActor.setScale(mFolderIcon.getScaleFactor(i),
		// mFolderIcon.getScaleFactor(i));
		// myActor.x = mFolderIcon.getPosx()-iconContain.x;
		// myActor.y = mFolderIcon.getPosy()-iconContain.y
		// -(R3D.workspace_cell_height - R3D.workspace_cell_width)*
		// mFolderIcon.getScaleFactor(i);
		// v5_animation_line.push(Tween
		// .to(myActor, View3DTweenAccessor.POS_XY, DefaultLayout.blurDuration)
		// .target(pointx, pointy)
		// .ease(Cubic.IN).delay(0));
		// v5_animation_line.push(Tween
		// .to(myActor, View3DTweenAccessor.SCALE_XY,
		// DefaultLayout.blurDuration)
		// .target(1.0f,1.0f).ease(Back.IN)
		// .delay(0));
		// }
		// v5_animation_line.start(View3DTweenAccessor.manager);
	}
	
	public void DealRenameOnPause()
	{
		inputTextView.show();
		mFolderIcon.bRenameFolder = false;
	}
	
	public void DealButtonNoAnim()
	{
		// iconContain.enableAnimation(false);
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
		}
		else
		{
			if( titleTexture != null )
			{
				/* 释放内存 */
				Log.v( "miui3D" , "titleTexture.dispose()" );
				titleTexture.dispose();
			}
			titleTexture = null;
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		GridView3D curGridPage = null;
		if( iconContainer != null )
		{
			curGridPage = (GridView3D)iconContainer.getCurrentView();
		}
		int Count = 0;
		if( curGridPage != null )
		{
			Count = curGridPage.getChildCount();
		}
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = (Icon3D)curGridPage.getChildAt( i );
			myActor.releaseDark();
			if( curGridPage.getFocusView() != myActor )
			{
				tempInfo = ( (Icon3D)myActor ).getItemInfo();
				tempInfo.screen = i;
				Root3D.addOrMoveDB( tempInfo , mFolderIcon.mInfo.id );
				( (Icon3D)myActor ).setInShowFolder( false );
				( (Icon3D)myActor ).setItemInfo( ( (Icon3D)myActor ).getItemInfo() );
				calcCoordinate( myActor );
				if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
				{
					myActor.y = myActor.y - R3D.folder_group_bottom_margin;
				}
				else
				{
					myActor.y = myActor.y - R3D.folder_group_bottom_margin + R3D.workspace_cell_height - R3D.workspace_cell_width;
				}
				templist.add( myActor );
			}
		}
		curGridPage.removeAllViews();
		bOutDragRemove = false;
		View3D view;
		animation_line = Timeline.createParallel();
		Count = templist.size();
		float duration = 0.3f;
		// zhujieping add
		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder;
		if( miuiV5Folder )
		{
			if( DefaultLayout.blur_enable )
			{
				if( mFolderIcon.blurredView != null )
				{
					mFolderIcon.blurredView.remove();
				}
				if( mFolderIcon.lwpBackView != null )
				{
					mFolderIcon.lwpBackView.remove();
				}
				mFolderIcon.mFolderIndex = mFolderIcon.getViewIndex( mFolderIcon.mFolderMIUI3D );
				mFolderIcon.mFolderMIUI3D.hide();
				// 当主菜单文件夹中拖回到主菜单时，不需要如下的操作
				if( DefaultLayout.mainmenu_folder_function == true && !( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && ( mFolderIcon.getExitToWhere() == FolderIcon3D.TO_APPLIST ) ) )
				{
					final Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
					root.transform = true;
					root.setScale( 0.8f , 0.8f );
					root.showOtherView();
					root.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1.0f , 1.0f , 0 );
				}
			}
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			view = templist.get( i );
			mFolderIcon.addViewBefore( mFolderIcon.folder_front , view );
			mFolderIcon.changeOrigin( view );
			mFolderIcon.changeTextureRegion( view , mFolderIcon.getIconBmpHeight() );
			view.y = view.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
			mFolderIcon.getPos( i );
			view.setPosition( mFolderIcon.getPosx() , mFolderIcon.getPosy() );
			view.setScale( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) );
		}
		setOrigin( mFolderIcon.mInfo.x + R3D.workspace_cell_width / 2 , mFolderIcon.mInfo.y + R3D.workspace_cell_height / 2 );
		setScale( 0 , 0 );
		// mFolderIcon.RootToCellLayoutAnimTime = duration;
		Log.v( "FolderMIUI3D" , "DealButtonNoAnim MSG_WORKSPACE_ALPAH_TO_ONE" );
		// viewParent.onCtrlEvent(mFolderIcon,
		// FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);
		dealAnimationLineFinished();
	}
	
	// zhujieping add end
	public void DealButtonOKDown()
	{
		if( mFolderIcon.bAnimate == true )
		{
			return;
		}
		if( iconContainer == null )
		{
			return;
		}
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
		}
		else
		{
			if( titleTexture != null )
			{
				/* 释放内存 */
				Log.v( "miui3D" , "titleTexture.dispose()" );
				titleTexture.dispose();
			}
			titleTexture = null;
		}
		//		ItemInfo tempInfo;
		//		ArrayList<View3D> templist = new ArrayList<View3D>();
		//		templist.clear();
		mFolderIcon.releaseFocus();
		//		int screen = 0;
		//		for (int i = 0; i < iconContainer.getChildCount(); i++) {
		//			IconGrid gridView = (IconGrid)iconContainer.getChildAt(i);
		//			for (int j = 0; j < gridView.getChildCount(); j++) {
		//				Icon3D myActor = (Icon3D)gridView.getChildAt(j);
		//				myActor.releaseDark();
		//				myActor.stopAllTween();
		//				if (gridView.getFocusView() != myActor) {
		//					tempInfo = ((Icon3D) myActor).getItemInfo();
		//					tempInfo.screen = screen;
		//					screen++;
		//					Root3D.addOrMoveDB(tempInfo, mFolderIcon.mInfo.id);
		//					((Icon3D) myActor).setInShowFolder(false);
		//					((Icon3D) myActor)
		//							.setItemInfo(((Icon3D) myActor).getItemInfo());
		//					templist.add(myActor);
		//				}
		//			}
		//		}
		//		GridView3D curGridPage = null;
		//		if (iconContainer != null) {
		//			curGridPage = (GridView3D) iconContainer.getCurrentView();
		//		}
		//		int Count = 0;
		//		if (curGridPage != null) {
		//			Count = curGridPage.getChildCount();
		//		}
		//		View3D myActor;
		//		for (int i = 0; i < Count; i++) {
		//			myActor = (Icon3D) curGridPage.getChildAt(i);
		//			myActor.releaseDark();
		//			myActor.stopAllTween();
		//			if (curGridPage.getFocusView() != myActor) {
		//				tempInfo = ((Icon3D) myActor).getItemInfo();
		//				tempInfo.screen = i;
		//				Root3D.addOrMoveDB(tempInfo, mFolderIcon.mInfo.id);
		//				((Icon3D) myActor).setInShowFolder(false);
		//				((Icon3D) myActor)
		//						.setItemInfo(((Icon3D) myActor).getItemInfo());
		//				// removed by Hugo.ye begin 20131115
		//				// calcCoordinate(myActor);
		//				// if (mFolderIcon.folder_style ==
		//				// FolderIcon3D.folder_rotate_style) {
		//				// myActor.y = myActor.y - R3D.folder_group_bottom_margin;
		//				// } else {
		//				// myActor.y = myActor.y - R3D.folder_group_bottom_margin
		//				// + R3D.workspace_cell_height
		//				// - R3D.workspace_cell_width;
		//				// }
		//				// removed by Hugo.ye end 20131115
		//				templist.add(myActor);
		//			}
		//
		//		}
		// iconContain.removeAllViews(); // removed by Hugo.ye 20131115
		bOutDragRemove = false;
		viewParent.onCtrlEvent( mFolderIcon , FolderIcon3D.MSG_WORKSPACE_RECOVER );
		startAnimation();
	}
	
	private void dealAnimationLineFinished()
	{
		mFolderIcon.stopTween();
		if( !ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) && !DefaultLayout.miui_v5_folder )
		{
			if( iconContainer != null )
			{
				// iconContain.dispose();
				removeView( iconContainer );
				iconContainer = null;
			}
			if( inputTextView != null )
			{
				removeView( inputTextView );
				inputTextView = null;
			}
			if( titleTexture != null )
			{
				/* 释放内存 */
				Log.v( "miui3D" , "titleTexture.dispose()" );
				titleTexture.dispose();
				titleTexture = null;
			}
		}
		Log.v( "miui3D" , "closeFolderStartAnimMIUI" );
		mFolderIcon.closeFolderStartAnimMIUI();
	}
	
	public void setIconPosition(
			float x ,
			float y )
	{
		iconX = x;
		iconY = y;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			// zhujieping
			String data = (String)source.getUserData();
			if( data != null && data.equals( "close_folder_v5" ) )
			{
				DealButtonNoAnim();
			}
			else
			{
				if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				{
					if( DefaultLayout.blur_enable )
					{
						iLoongLauncher.getInstance().getD3dListener().getRoot().transform = false;
						mFolderIcon.addViewAt( mFolderIcon.mFolderIndex , mFolderIcon.mFolderMIUI3D );
					}
					viewParent.onCtrlEvent( mFolderIcon , FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE );
				}
				iconContainer.color.a = 1;
				ArrayList<View3D> templist = new ArrayList<View3D>();
				ItemInfo tempInfo;
				int screen = 0;
				for( int i = 0 ; i < iconContainer.getChildCount() ; i++ )
				{
					GridView3D gridPage = (GridView3D)iconContainer.getChildAt( i );
					for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
					{
						View3D view = gridPage.getChildAt( j );
						view.releaseDark();
						view.stopAllTween();
						tempInfo = ( (Icon3D)view ).getItemInfo();
						tempInfo.screen = screen;
						screen++;
						//						Root3D.addOrMoveDB(tempInfo, mFolderIcon.mInfo.id);
						( (Icon3D)view ).setInShowFolder( false );
						( (Icon3D)view ).setItemInfo( ( (Icon3D)view ).getItemInfo() );
						templist.add( view );
					}
				}
				iconContainer.removeAllViews();
				iconContainer.getViewList().clear();
				for( int i = 0 ; i < templist.size() ; i++ )
				{
					View3D view = templist.get( i );
					mFolderIcon.addViewBefore( mFolderIcon.folder_front , view );
					mFolderIcon.changeOrigin( view );
					mFolderIcon.changeTextureRegion( view , mFolderIcon.getIconBmpHeight() );
				}
				if( iconContainer.getCurrentPage() != 0 )
				{
					mFolderIcon.folder_layout_after_close( true );
				}
				else
				{
					mFolderIcon.folder_layout_after_close( false );
				}
				dealAnimationLineFinished();
			}
			if( Root3D.mainMenuEntry )
			{
				Root3D.mainMenuEntry = false;
				Root3D.getInstance().showAllAppFromWorkspace( iconX , iconY );
			}
		}
	}
	
	private void dealFolderRename()
	{
		Log.v( "RenameFolder" , "RenameFolder" );
		if( useOrignalDialog )
		{
			Root3D.getInstance().showScreenBg();
			SendMsgToAndroid.SendCometFolderRename( mFolderIcon );
		}
		else
		{
			mFolderIcon.bRenameFolder = true;
			Root3D.getInstance().showFolderRename();
			if( !ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) && !DefaultLayout.miui_v5_folder )
			{
				SendMsgToAndroid.sendRenameFolderMsg( mFolderIcon );
			}
		}
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
			bCloseFolderByDrag = false;
			DealButtonOKDown();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	@Override
	public boolean onLongClick(
			float x ,
			float y )
	{
		View3D hitView = hit( x , y );
		if( hitView != null && hitView.scaleX == 1.0f )
		{
			if( hitView instanceof Icon3D || hitView instanceof FolderIcon3D )
			{
				hitView.releaseDark();
			}
			hitView = null;
		}
		// if (mFolderIcon.bRenameFolder == true) {
		// return true;
		// }
		/*
		 * 这里要特别判断，因为目前的情况是girdview的cellWidth会小于Icon3D�?
		 * 实际大小，在计算index的时候，会导致计算有误差，导致onLongClick传�?到ICON本身，从而导致垃圾桶不消失和悬浮问题
		 */
		point.x = x;
		point.y = y;
		GridView3D curGridPage = null;
		if( iconContainer != null )
		{
			curGridPage = (GridView3D)iconContainer.getCurrentView();
		}
		this.toLocalCoordinates( iconContainer , point );
		int retIndex = -1;
		if( curGridPage != null )
		{
			retIndex = curGridPage.getIndex( (int)point.x , (int)point.y );
		}
		if( retIndex < 0 || retIndex >= curGridPage.getChildCount() )
		{
			return true;
		}
		// zhujieping
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			super.onLongClick( x , y );
			return true;
		}
		else
		{
			return super.onLongClick( x , y );
		}
	}
	
	private int lastIndex;
	private int currentIndex;
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( pointer > 0 )
		{
			return false;
		}
		View3D hitView = hit( x , y );
		if( bEnableTouch )
		{
			if( hitView.name == inputTextView.name )
			{
				mFolderIcon.releaseFocus();
				requestFocus();
			}
			else
			{
				// if (mFolderIcon.bRenameFolder == true) {
				// return true;
				// }
			}
		}
		super.onTouchDown( x , y , pointer );
		return true;
	}
	
	void closeMIUI2Folder()
	{
		if( mFolderIcon.bAnimate == true )
		{
			return;
		}
		if( iconContainer == null )
		{
			return;
		}
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
		}
		else
		{
			if( titleTexture != null )
			{
				/* 释放内存 */
				Log.v( "miui3D" , "titleTexture.dispose()" );
				titleTexture.dispose();
			}
			titleTexture = null;
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		float duration = 0.5f;
		this.stopTween();
		float scale_x = (float)R3D.workspace_cell_width / (float)this.width;
		float scale_y = (float)R3D.workspace_cell_width / (float)this.height;
		animation_line = Timeline.createParallel();
		animation_line.push( this.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 0 , 0 , 0 ) );
		animation_line.setUserData( "close_folder_v5" );
		// animation_line.push(mFolder.startTween(View3DTweenAccessor.POS_XY,
		// Cubic.INOUT, duration, mInfo.x, mInfo.y, 0).setUserData(
		// tween_anim_close));
		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		SendMsgToAndroid.sendHideClingPointMsg();
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// zhujieping
		if( pointer > 0 )
		{
			return false;
		}
		View3D hitView = hit( x , y );
		// Log.v("folder", "ontouchup 0-1");
		if( hitView == null )
		{
			// Log.v("folder", "ontouchup 1");
			return iconContainer.onTouchUp( x , y , pointer );
		}
		Log.v( "folder" , "receive hitView=" + hitView );
		if( hitView.name == this.name || hitView.name.equals( "folderCover" ) )
		{
			GridView3D gridPage = (GridView3D)iconContainer.getCurrentView();
			if( gridPage.getFocusView() != null )
			{
				// Log.v("folder", "ontouchup 2");
				// iconContain.onTouchUp(x, y, pointer);
				gridPage.clearFocusView();
				DealButtonOKDown();
			}
			else
			{
				// Log.v("folder", "ontouchup 3");
				DealButtonOKDown();
			}
			return true;
		}
		if( bEnableTouch )
		{
			if( hitView.name == inputTextView.name )
			{
				releaseFocus();
				mFolderIcon.requestFocus();
				dealFolderRename();
				return true;
			}
			else if( hitView instanceof GridView3D )
			{
				Log.v( "folder" , "ontouchup 3-1" );
				return true;
			}
			else
			{
				Log.v( "folder" , "ontouchup 4" );
				// if (mFolderIcon.bRenameFolder == true) {
				// return true;
				// }
				iconContainer.onTouchUp( x , y , pointer );
				return super.onTouchUp( x , y , pointer );
			}
		}
		else
		{
			Log.v( "folder" , "ontouchup 5" );
			return super.onTouchUp( x , y , pointer );
		}
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		if( mFolderIcon.mInfo.opened == true )
		{
			return true;
		}
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// Log.v("CircleSomething","circlePopWnd onDoubleClick");
		return true;
	}
	
	void setFolderIcon(
			FolderIcon3D icon )
	{
		mFolderIcon = icon;
	}
	
	public void setEditText(
			String text )
	{
		text = text.trim();
		text = text.concat( "x.z" );
		int length = text.length();
		if( length > 3 )
		{
			mFolderIcon.mInfo.setTitle( text );
			Root3D.updateItemInDatabase( mFolderIcon.mInfo );
			inputNameString = text;
			viewParent.onCtrlEvent( this , MSG_UPDATE_VIEW );
		}
	}
	
	public void RemoveViewByItemInfo(
			ItemInfo item )
	{
		if( bOutDragRemove )
		{
			return;
		}
		View3D myActor;
		GridView3D gridPage = null;
		if( iconContainer != null )
		{
			gridPage = (GridView3D)iconContainer.getCurrentView();
		}
		int Count = 0;
		if( gridPage != null )
		{
			gridPage.getChildCount();
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = gridPage.getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				ItemInfo info = ( (Icon3D)myActor ).getItemInfo();
				if( item.equals( info ) )
				{
					gridPage.removeView( myActor );
					myActor = null;
					break;
				}
			}
		}
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof GridView3D )
		{
			switch( event_id )
			{
				case GridView3D.MSG_VIEW_OUTREGION_DRAG:
					if( mFolderIcon.mInfo.opened == true )
					{
						View3D focus = null;
						for( int i = 0 ; i < iconContainer.getPageNum() ; i++ )
						{
							GridView3D gridPage = (GridView3D)iconContainer.getChildAt( i );
							if( gridPage != null )
							{
								focus = gridPage.getFocusView();
								gridPage.releaseFocus();
								if( focus != null )
								{
									break;
								}
							}
						}
						focus.stopTween();
						focus.setScale( 1f , 1f );
						focus.toAbsoluteCoords( point );
						bOutDragRemove = true;
						bCloseFolderByDrag = true;
						Log.v( "FolderIcon3DMi" , "FolderMIUI3D ---- MSG_VIEW_OUTREGION_DRAG --- mFolderIcon viewParant is : " + mFolderIcon.getParent().name );
						// teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && ( (Icon3D)focus ).getUninstallStatus() == false )
							{
								// do not remove the focus icon, but it needs set
								// the flag that indicates exiting to CellLayout
								mFolderIcon.setExitToWhere( FolderIcon3D.TO_CELLLAYOUT );
							}
							else
							{
								mFolderIcon.mInfo.remove( (ShortcutInfo)( (Icon3D)focus ).getItemInfo() );
							}
						}
						else
						{
							mFolderIcon.mInfo.remove( (ShortcutInfo)( (Icon3D)focus ).getItemInfo() );
						}
						// teapotXu add end for Folder in Mainmenu
						DealRenameOnPause();
						// zhujieping add start
						if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
						{
							Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
							root.isDragEnd = true;
							root.folderOpened = false;
						}
						// zhujieping add start
						//					DealButtonOKDown();
						this.setTag( new Vector2( point.x , point.y ) );
						dragObjects.clear();
						// teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && ( ( (Icon3D)focus ).getUninstallStatus() == false ) )
							{
								Icon3D icon3d = (Icon3D)focus;
								icon3d.hideSelectedIcon();
								Icon3D iconClone = icon3d.clone();
								// add back to mFolderIcon
								mFolderIcon.changeOrigin( focus );
								( (Icon3D)focus ).cancelSelected();
								focus.y = focus.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
								mFolderIcon.addViewBefore( mFolderIcon.folder_front , focus );
								// should clear the iconState
								iconClone.clearState();
								dragObjects.add( iconClone );
							}
							else
							{
								dragObjects.add( focus );
							}
						}
						else
						{
							dragObjects.add( focus );
						}
						// dragObjects.add(focus);
						// teapotXu add end for Folder in Mainmenu
						return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					}
				case GridView3D.MSG_VIEW_TOUCH_UP:
					mFolderIcon.requestFocus();
					return false;
				case GridView3D.MSG_GRID_VIEW_REQUEST_FOCUS:
					mFolderIcon.releaseFocus();
					return true;
			}
		}
		// teapotXu add start for Folder In Mainmenu
		else if( sender instanceof Icon3D )
		{
			switch( event_id )
			{
				case Icon3D.MSG_ICON_LONGCLICK:
					Icon3D focus_icon = (Icon3D)sender;
					if( DefaultLayout.mainmenu_folder_function == true )
					{
						// Now only in Folder of Mainmenu && this folder is open &&
						// Applist is not in Uninstall Mode
						if( mFolderIcon.mInfo.opened == true && mFolderIcon.getFromWhere() == FolderIcon3D.FROM_GRIDVIEW3D && focus_icon.getUninstallStatus() == false )
						{
							GridView3D gridPage = null;
							if( iconContainer != null )
							{
								gridPage = (GridView3D)iconContainer.getCurrentView();
							}
							if( gridPage != null )
							{
								gridPage.releaseFocus();
							}
							focus_icon.stopTween();
							focus_icon.setScale( 1f , 1f );
							focus_icon.toAbsoluteCoords( point );
							bOutDragRemove = true;
							bCloseFolderByDrag = true;
							dragObjects.clear();
							Icon3D icon3d = focus_icon;
							icon3d.hideSelectedIcon();
							Icon3D iconClone = icon3d.clone();
							iconClone.x = point.x;
							iconClone.y = point.y;
							// should clear the iconState
							iconClone.clearState();
							dragObjects.add( iconClone );
							// do not remove the focus icon, but it needs set the
							// flag that indicates exiting to CellLayout
							mFolderIcon.setExitToWhere( FolderIcon3D.TO_CELLLAYOUT );
							DealRenameOnPause();
							// zhujieping add start
							if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
							{
								Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
								root.isDragEnd = true;
								root.folderOpened = false;
							}
							// zhujieping add end
							// DealButtonOKDown();
							DealButtonNoAnim();
							this.setTag( new Vector2( point.x , point.y ) );
							return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
						}
					}
					return true;
			}
		}
		// teapotxu add end for Folder in Mainmenu
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	private Matrix4 bgOldTransformMatrix4 = new Matrix4();
	private Matrix4 bgTransformMatrix = new Matrix4();
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		// int srcBlendFunc = 0,dstBlendFunc = 0;
		// if(DefaultLayout.blend_func_dst_gl_one){
		// /*获取获取混合方式*/
		// srcBlendFunc = batch.getSrcBlendFunc();
		// dstBlendFunc = batch.getDstBlendFunc();
		// if(srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE)
		// batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		// }
		batch.setColor( color.r , color.g , color.b , color.a );
		// zhujieping
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			//  if(DefaultLayout.blur_enable){
			if( bgView != null && bgView.background9 != null )
			{
				batch.setColor( color.r , color.g , color.b , bgView.getColor().a );
				bgOldTransformMatrix4.set( batch.getTransformMatrix() );
				batch.end();
				batch.setTransformMatrix( bgTransformMatrix );
				batch.begin();
				bgView.background9.draw( batch , 0 , 0 , bgView.width , bgView.height );
				batch.setColor( color.r , color.g , color.b , color.a );
				batch.end();
				batch.setTransformMatrix( bgOldTransformMatrix4 );
				batch.begin();
			}
			//  }
			if( !mFolderIcon.captureCurScreen )
			{
				super.draw( batch , color.a );
			}
		}
		else
		{
			super.draw( batch , color.a );
		}
		if( titleTexture != null && bNeedUpdate )
		{
			batch.setColor( color.r , color.g , color.b , color.a );
			batch.draw( titleTexture , inputTextView.x , inputTextView.y + this.y , inputTextView.width , titleHeight );
		}
		// if (openLineRegion != null && inputTextView != null && bNeedUpdate) {
		// batch.setColor(color.r, color.g, color.b, color.a);
		// batch.draw(
		// openLineRegion,
		// 2 * R3D.icongroup_margin_left
		// + R3D.folder_group_left_margin,
		//
		// inputTextView.y + this.y - openLineRegion.getRegionHeight(),
		// width
		// - 2
		// * (2 * R3D.icongroup_margin_left + R3D.folder_group_left_margin),
		// openLineRegion.getRegionHeight());
		// }
		// if(DefaultLayout.blend_func_dst_gl_one){
		// if(srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE)
		// batch.setBlendFunction(srcBlendFunc, dstBlendFunc);
		// }
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
		// TODO Auto-generated method stub
		return dragObjects;
	}
	
	@Override
	public void valueChanged(
			TextField3D textField ,
			String newValue )
	{
		// TODO Auto-generated method stub
	}
	
	class IconContainer extends NPageBase
	{
		
		private int indicatorWidth = 440;
		private float posX = 100f;
		private float posY = 35f;
		private boolean bDrawIndicator = true;
		private boolean bCanScroll = true;
		
		public IconContainer(
				String name )
		{
			super( name );
			transform = true;
			setWholePageList();
			setEffectType( 0 );
			float scaleFactor = Utils3D.getScreenWidth() / 720f;
			indicatorWidth *= scaleFactor;
			posX *= scaleFactor;
			posY *= scaleFactor;
			indicatorView = new ChildIndicatorView( "npage_indicator" , indicatorWidth );
			indicatorView.setPosition( posX , posY );
		}
		
		@Override
		protected int preIndex()
		{
			// TODO Auto-generated method stub
			return( page_index == 0 ? 0 : page_index - 1 );
		}
		
		@Override
		protected int nextIndex()
		{
			// TODO Auto-generated method stub
			return( page_index == view_list.size() - 1 ? view_list.size() - 1 : page_index + 1 );
		}
		
		@Override
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
			// Log.v("jbc",
			// "getTotalOffset isManualScrollTo="+isManualScrollTo);
			// Log.v("jbc", "getTotalOffset ret="+ret);
			if( ret < 0 )
			{
				ret = 0;
			}
			else if( ret > ( totalPage - 1.0f ) / ( totalPage - 1 ) )
			{
				ret = ( totalPage - 1.0f ) / ( totalPage - 1 );
			}
			if( xScale < 0 && page_index == totalPage - 1 )
			{
				ret = 1;
			}
			if( xScale > 0 && page_index == 0 )
			{
				ret = 0;
			}
			return ret;
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			mFolderIcon.releaseFocus();
			bCanScroll = true;
			if( getPageNum() == 1 )
			{
				requestFocus();
			}
			super.onTouchDown( x , y , pointer );
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			bCanScroll = true;
			if( getPageNum() == 1 )
			{
				releaseFocus();
			}
			super.onTouchUp( x , y , pointer );
			if( isManualScrollTo && xScale != 0 )
			{
				if( xScale < 0 )
				{
					IconGrid nextGrid = (IconGrid)iconContainer.getChildAt( nextIndex() );
					nextGrid.onTouchUp( 0 , 0 , 0 );
				}
				else
				{
					IconGrid preGrid = (IconGrid)iconContainer.getChildAt( preIndex() );
					preGrid.onTouchUp( 0 , 0 , 0 );
				}
			}
			mFolderIcon.requestFocus();
			return false;
		}
		
		@Override
		public boolean scroll(
				float x ,
				float y ,
				float deltaX ,
				float deltaY )
		{
			// TODO Auto-generated method stub
			if( !bCanScroll )
			{
				return true;
			}
			if( getPageNum() == 1 )
			{
				return true;
			}
			return super.scroll( x , y , deltaX , deltaY );
		}
		
		@Override
		protected void updateEffect()
		{
			// TODO Auto-generated method stub
			if( ( page_index == 0 && xScale > 0 ) || ( page_index == getPageNum() - 1 && xScale < 0 ) )
			{
				bDrawIndicator = false;
				return;
			}
			else
			{
				bDrawIndicator = true;
			}
			super.updateEffect();
		}
		
		@Override
		public void startAutoEffect()
		{
			// TODO Auto-generated method stub
			if( ( page_index == 0 && xScale > 0 ) || ( page_index == getPageNum() - 1 && xScale < 0 ) )
			{
				xScale = 0;
				yScale = 0;
				mVelocityX = 0;
				return;
			}
			super.startAutoEffect();
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// TODO Auto-generated method stub
			if( xScale != 0 )
			{
				batch.flush();
				Gdx.gl.glEnable( GL10.GL_SCISSOR_TEST );
				Gdx.gl.glScissor( (int)( Utils3D.getScreenWidth() - folderWidth ) / 2 + gridPaddingLeft , 0 , (int)( folderWidth - gridPaddingLeft - gridPaddingRight ) , Utils3D.getScreenHeight() );
			}
			super.draw( batch , parentAlpha );
			if( xScale != 0 )
			{
				Gdx.gl.glDisable( GL10.GL_SCISSOR_TEST );
			}
		}
		
		@Override
		public boolean onCtrlEvent(
				View3D sender ,
				int event_id )
		{
			// TODO Auto-generated method stub
			if( sender instanceof GridView3D )
			{
				IconGrid preGrid = null;
				IconGrid nextGrid = null;
				IconGrid curGrid = null;
				View3D focusView = null;
				View3D temView = null;
				switch( event_id )
				{
					case GridView3D.MSG_VIEW_OUTREGION_DRAG:
						xScale = 0;
						yScale = 0;
						this.releaseFocus();
						break;
					case GridView3D.MSG_GRID_VIEW_REQUEST_FOCUS:
						this.releaseFocus();
						return true;
					case IconGrid.MSG_ICON_MOVE_TO_PRE_PAGE:
						if( page_index > 0 && xScale == 0 )
						{
							curGrid = (IconGrid)iconContainer.getChildAt( getCurrentPage() );
							preGrid = (IconGrid)iconContainer.getChildAt( preIndex() );
							temView = preGrid.getChildAt( preGrid.getChildCount() - 1 );
							curGrid.releaseFocus();
							preGrid.requestFocus();
							curGrid.addViewAt( 0 , temView );
							focusView = curGrid.getFocusView();
							curGrid.clearFocusView();
							preGrid.addView( focusView );
							preGrid.setFocusView( focusView );
							preGrid.enableAnimation( true );
							scrollTo( preIndex() );
						}
						else if( page_index == 0 )
						{
							//						curGrid = (IconGrid)iconContainer.getChildAt(page_index);
							//						curGrid.onTouchUp(0, 0, 0);
							bCanScroll = false;
						}
						return true;
					case IconGrid.MSG_ICON_MOVE_TO_NEXT_PAGE:
						if( page_index < getPageNum() - 1 && xScale == 0 )
						{
							curGrid = (IconGrid)iconContainer.getChildAt( getCurrentPage() );
							nextGrid = (IconGrid)iconContainer.getChildAt( nextIndex() );
							focusView = curGrid.getFocusView();
							curGrid.clearFocusView();
							nextGrid.addView( focusView );
							nextGrid.setFocusView( focusView );
							nextGrid.enableAnimation( true );
							curGrid.releaseFocus();
							nextGrid.requestFocus();
							temView = nextGrid.getChildAt( 0 );
							curGrid.addView( temView );
							scrollTo( nextIndex() );
						}
						else if( page_index == getPageNum() - 1 )
						{
							//						curGrid = (IconGrid)iconContainer.getChildAt(page_index);
							//						curGrid.onTouchUp(0, 0, 0);
							bCanScroll = false;
						}
						return true;
				}
			}
			return super.onCtrlEvent( sender , event_id );
		}
		
		class ChildIndicatorView extends IndicatorView
		{
			
			public ChildIndicatorView(
					String name ,
					int width )
			{
				super( name , width );
				// TODO Auto-generated constructor stub
			}
			
			@Override
			public void draw(
					SpriteBatch batch ,
					float parentAlpha )
			{
				// TODO Auto-generated method stub
				if( bDrawIndicator )
				{
					super.draw( batch , parentAlpha );
				}
			}
		}
	}
	
	class IconGrid extends GridView3D
	{
		
		public static final int MSG_ICON_MOVE_TO_PRE_PAGE = -2;
		public static final int MSG_ICON_MOVE_TO_NEXT_PAGE = -3;
		
		public IconGrid(
				String name ,
				float width ,
				float height ,
				int countx ,
				int county )
		{
			super( name , width , height , countx , county );
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public int getIndex(
				int x ,
				int y )
		{
			// TODO Auto-generated method stub
			int mCellWidth = (int)( ( this.width - gridPaddingLeft - gridPaddingRight ) / R3D.folder_group_child_count_x );
			int mCellHeight = (int)( ( this.height - gridPaddingTop - gridPaddingBottom ) / R3D.folder_group_child_count_y );
			if( y < gridPaddingBottom || y > this.height - gridPaddingTop )
			{
				return -1;
			}
			else if( x < gridPaddingLeft )
			{
				return MSG_ICON_MOVE_TO_PRE_PAGE;
			}
			else if( x > this.width - gridPaddingRight )
			{
				return MSG_ICON_MOVE_TO_NEXT_PAGE;
			}
			return( ( ( (int)this.height - y - gridPaddingTop ) / mCellHeight ) * R3D.folder_group_child_count_x + ( x - gridPaddingLeft ) / mCellWidth );
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			boolean rst = super.onLongClick( x , y );
			if( getFocusView() != null )
			{
				FolderIcon3D.bIconPosChanged = true;
			}
			return rst;
		}
		
		@Override
		public boolean onTouchDragged(
				float x ,
				float y ,
				int pointer )
		{
			// TODO Auto-generated method stub
			if( getFocusView() != null )
			{
				enableAnimation( true );
				int index = getIndex( (int)x , (int)y );
				if( index == MSG_ICON_MOVE_TO_PRE_PAGE )
				{
					return viewParent.onCtrlEvent( this , MSG_ICON_MOVE_TO_PRE_PAGE );
				}
				else if( index == MSG_ICON_MOVE_TO_NEXT_PAGE )
				{
					return viewParent.onCtrlEvent( this , MSG_ICON_MOVE_TO_NEXT_PAGE );
				}
			}
			return super.onTouchDragged( x , y , pointer );
		}
	}
	
	/******** added by zhenNan.ye begin ********/
	public ImageView3D getBgView()
	{
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			return bgView;
		}
		return null;
	}
	/******** added by zhenNan.ye end ********/
}
