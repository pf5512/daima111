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
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;


/* Folder3D Layout height=Utils3D.getScreenHeight()-R3D.workspace_cell_height girdHeight=height-R3D.icongroup_button_height/2
 * 
 * buttonHeight/2 girdHeight R3D.workspace_Cell_height */
public class Folder3D extends ViewGroup3D implements DragSource3D
{
	
	public static final int MSG_ON_DROP = 0;
	public static final int MSG_UPDATE_VIEW = 1;
	public static final int MSG_UPDATE_GRIDVIEW_FOR_LOCATION = 3;//xiatian add	//for mainmenu sort by user
	private FolderIcon3D mFolderIcon;
	private GridView3D iconContain;
	private ImageView3D buttonOK;
	private ImageView3D inputTextView;
	Texture titleTexture;
	private String inputNameString;
	private int titleWidth;
	private int titleHeight;
	private boolean bNeedUpdate = false;
	private Timeline animation_line = null;
	// private Timeline    viewTween=null;
	public boolean bEnableTouch = true;
	boolean bOutDragRemove = false;
	public boolean bCloseFolderByDrag = false;
	private static boolean displayButton = false;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	
	public Folder3D()
	{
		this( null );
		// TODO Auto-generated constructor stub
	}
	
	public Folder3D(
			String name )
	{
		super( name );
	}
	
	public void onThemeChanged()
	{
		if( findView( "button_ok" ) != null )
		{
			int childMargin = R3D.icongroup_margin_left;
			TextureRegion gridbgTexture = R3D.getTextureRegion( "widget-folder-windows-bg" );
			NinePatch gridbackground = new NinePatch( gridbgTexture , R3D.folder_group_left_round + 2 , R3D.folder_group_right_round , R3D.folder_group_top_round + 2 , R3D.folder_group_bottom_round );
			iconContain.setBackgroud( gridbackground );
			titleWidth = (int)iconContain.width - R3D.folder_group_left_round - R3D.folder_group_right_round;
			titleHeight = R3D.folder_group_text_height;
			inputTextView.setPosition( R3D.folder_group_left_round , (float)( iconContain.height - R3D.folder_group_text_height - R3D.folder_group_top_round ) );
			if( Utils3D.getScreenHeight() < 400 && R3D.icon_bg_num > 0 )
			{
				iconContain.setPadding( 2 * childMargin , 2 * childMargin , (int)( R3D.folder_group_text_height + R3D.folder_group_top_round ) , 2 * childMargin );
			}
			else
			{
				iconContain.setPadding( 2 * childMargin , 2 * childMargin , (int)( R3D.folder_group_text_height + R3D.folder_group_top_round + R3D.icongroup_margin_top ) , 2 * childMargin );
			}
			inputTextView.setSize( titleWidth , titleHeight );
			TextureRegion gridTitleTexture = R3D.getTextureRegion( "widget-folder-windows-title" );
			NinePatch gridTitlebackground = new NinePatch( gridTitleTexture , R3D.folder_group_text_round , R3D.folder_group_text_round , R3D.folder_group_text_round , R3D.folder_group_text_round );
			inputTextView.setBackgroud( gridTitlebackground );
			inputNameString = mFolderIcon.mInfo.title.toString();
			buttonOK.region = R3D.getTextureRegion( "public-button-return" );
			buttonOK.setSize( R3D.icongroup_button_width , R3D.icongroup_button_height );
			buttonOK.setPosition( this.width + R3D.folder_group_left_margin - R3D.icongroup_button_width , this.height - R3D.icongroup_button_height - R3D.icongroup_button_height / 4 );
		}
	}
	
	public void buildElements()
	{
		if( findView( "button_ok" ) == null )
		{
			buildIconGroup();
			buttonOK = new ImageView3D( "button_ok" , R3D.getTextureRegion( "public-button-return" ) );
			buttonOK.setSize( R3D.icongroup_button_width , R3D.icongroup_button_height );
			buttonOK.setPosition( this.width + R3D.folder_group_left_margin - R3D.icongroup_button_width , this.height - R3D.icongroup_button_height - R3D.icongroup_button_height / 4 );
			addView( buttonOK );
			if( displayButton )
			{
			}
			else
			{
				buttonOK.hide();
			}
		}
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
		if( FolderIcon3D.folder_iphone_style != mFolderIcon.folder_style )
		{
			if( Count > R3D.folder_max_num / 2 )
			{
				iconContain.setAnimationDelay( 0.02f );
			}
			else
			{
				iconContain.setAnimationDelay( 0.04f );
			}
		}
		bOutDragRemove = false;
		bCloseFolderByDrag = false;
		//iconContain.setAnimationDelay(0);
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = mFolderIcon.getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				//				myActor.show();
				myActor.setRotation( 0 );
				myActor.setScale( 1.0f , 1.0f );
				mFolderIcon.changeTextureRegion( myActor , R3D.workspace_cell_height );
				( (Icon3D)myActor ).setInShowFolder( true );
				( (Icon3D)myActor ).setItemInfo( ( (Icon3D)myActor ).getItemInfo() );
				myActor.x = myActor.x - this.x;
				myActor.y = myActor.y - this.y;
				//teapotXu add start for Folder in Mainmenu
				//only when folder is in Mainmenu, hide icons in folder are needed to hide
				if( DefaultLayout.mainmenu_folder_function == true && ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST ) )
				{
					if( ( (Icon3D)myActor ).getItemInfo() instanceof ShortcutInfo )
					{
						ApplicationInfo appInfo = ( (ShortcutInfo)( (Icon3D)myActor ).getItemInfo() ).appInfo;
						if( appInfo != null && appInfo.isHideIcon && ( (Icon3D)myActor ).getHideStatus() == false )
						{
							//don't add this icon into the FolderList.
							continue;
						}
					}
				}
				//teapotXu add end for Folder in Mainmenu				
				myActor.show();
				templist.add( myActor );
			}
		}
		iconContain.addItem( templist );
	}
	
	public void addIcon(
			Icon3D newIcon )
	{
		if( iconContain.getChildCount() == 0 )
		{
			return;
		}
		newIcon.x = iconContain.getChildAt( 0 ).x;
		newIcon.y = iconContain.getChildAt( 0 ).y;
		iconContain.addItem( newIcon );
	}
	
	public void updateIcon(
			Icon3D widgetIcon ,
			Icon3D newIcon )
	{
		newIcon.x = widgetIcon.x;
		newIcon.y = widgetIcon.y;
		iconContain.removeView( widgetIcon );
		iconContain.addItem( newIcon );
	}
	
	public void updateTexture()
	{
		if( iconContain == null )
		{
			return;
		}
		int Count = iconContain.getChildCount();
		View3D myActor;
		ItemInfo tempInfo;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = iconContain.getChildAt( i );
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
	
	private void buildIconGroup()
	{
		int countX = 4;
		int countY = R3D.folder_max_num / countX;
		int childMargin = R3D.icongroup_margin_left;
		TextureRegion gridbgTexture = R3D.getTextureRegion( "widget-folder-windows-bg" );
		NinePatch gridbackground = new NinePatch( gridbgTexture , R3D.folder_group_left_round + 2 , R3D.folder_group_right_round , R3D.folder_group_top_round + 2 , R3D.folder_group_bottom_round );
		iconContain = new GridView3D( "iconGroupGrid" , this.width , this.height , countX , countY );
		iconContain.setBackgroud( gridbackground );
		iconContain.enableAnimation( true );
		titleWidth = (int)iconContain.width - R3D.folder_group_left_round - R3D.folder_group_right_round;
		titleHeight = R3D.folder_group_text_height;
		inputTextView = new ImageView3D( "inputTextView" );
		inputTextView.setPosition( R3D.folder_group_left_round , (float)( iconContain.height - R3D.folder_group_text_height - R3D.folder_group_top_round ) );
		if( Utils3D.getScreenHeight() < 400 && R3D.icon_bg_num > 0 )
		{
			iconContain.setPadding( 2 * childMargin , 2 * childMargin , (int)( R3D.folder_group_text_height + R3D.folder_group_top_round ) , 2 * childMargin );
		}
		else
		{
			iconContain.setPadding( 2 * childMargin , 2 * childMargin , (int)( R3D.folder_group_text_height + R3D.folder_group_top_round + R3D.icongroup_margin_top ) , 2 * childMargin );
		}
		//		else
		//		{
		//			if (R3D.icon_bg_num==1)
		//			{
		//		      iconContain.setPadding(2 * childMargin, 2 * childMargin, 
		//		    		  (int)(iconContain.height-inputTextView.y+R3D.icongroup_margin_top),childMargin);
		//			}
		//			else
		//			{
		//				iconContain.setPadding(2 * childMargin, 2 * childMargin, 
		//						(int)(iconContain.height-inputTextView.y+R3D.icongroup_margin_top),2*childMargin);
		//
		//			}
		//		}
		inputTextView.setSize( titleWidth , titleHeight );
		TextureRegion gridTitleTexture = R3D.getTextureRegion( "widget-folder-windows-title" );
		NinePatch gridTitlebackground = new NinePatch( gridTitleTexture , R3D.folder_group_text_round , R3D.folder_group_text_round , R3D.folder_group_text_round , R3D.folder_group_text_round );
		inputTextView.setBackgroud( gridTitlebackground );
		inputNameString = mFolderIcon.mInfo.title.toString();
		titleTexture = new BitmapTexture( titleToTexture( mFolderIcon.mInfo.title.toString() , titleWidth , titleHeight ) , true );
		addView( iconContain );
		addView( inputTextView );
	}
	
	public void onInputNameChanged()
	{
		Texture t;
		Texture texture;
		titleWidth = (int)( Utils3D.getScreenWidth() - R3D.folder_group_left_margin - R3D.folder_group_right_margin - R3D.icongroup_button_width * 2 );
		titleHeight = R3D.folder_group_text_height;
		;
		texture = new BitmapTexture( titleToTexture( inputNameString , titleWidth , titleHeight ) , true );
		t = this.titleTexture;
		this.titleTexture = texture;
		if( t != null )
			t.dispose();
	}
	
	private Bitmap titleToTexture(
			String title ,
			int titleWidth ,
			int titleHeight )
	{
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
		//paint.setTextSize(Utils3D.getDensity()*titleHeight/3);
		if( title.endsWith( "x.z" ) )
		{
			int length = title.length();
			if( length > 3 )
			{
				title = title.substring( 0 , length - 3 );
			}
		}
		paint.setTextSize( titleHeight / 2 );
		if( paint.measureText( title ) > titleWidth - 2 )
		{
			while( paint.measureText( title ) > titleWidth - paint.measureText( "..." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "...";
		}
		FontMetrics fontMetrics = paint.getFontMetrics();
		float offsetX = 0;
		if( Utils3D.getDensity() < 1f )
		{
			offsetX = titleHeight / 2;
		}
		else
		{
			offsetX = titleHeight / 2;
		}
		float fontPosY = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		fontPosY = titleHeight - ( titleHeight - fontPosY ) / 2f - fontMetrics.bottom;
		canvas.drawText( title , offsetX , fontPosY , paint );
		return bmp;
	}
	
	private void stopAnimation()
	{
		if( animation_line != null && !animation_line.isFinished() )
		{
			animation_line.free();
			animation_line = null;
		}
	}
	
	private void startAnimation_rotate(
			ArrayList<View3D> templist )
	{
		float delayFactor = 0;
		View3D myActor;
		stopAnimation();
		animation_line = Timeline.createParallel();
		int Count = templist.size();
		if( Count > R3D.folder_max_num / 2 )
		{
			delayFactor = 0.02f;
		}
		else
		{
			delayFactor = 0.04f;
		}
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = templist.get( i );
			myActor.stopTween();
			myActor.setRotationVector( 0 , 0 , 1 );
			//viewTween = null;
			//viewTween = Timeline.createParallel();
			mFolderIcon.addViewBefore( mFolderIcon.folder_front , myActor );
			//pos_x = mFolderIcon.folder_front.x ;
			//pos_y = mFolderIcon.folder_front.y + mFolderIcon.icon_pos_y;
			if( i == Count - 2 )
			{
				mFolderIcon.getPos( 1 );
				//pos_x = pos_x + R3D.folder_icon_rotation_offsetx;
				//pos_y=pos_y+R3D.folder_icon_rotation_offsety;
				animation_line.push( Tween.to( myActor , View3DTweenAccessor.ROTATION , 0.2f ).target( -R3D.folder_icon_rotation_degree , 0 , 0 ).ease( Linear.INOUT )
						.delay( 0.1f + delayFactor * ( Count - i ) ) );
			}
			else if( i == Count - 3 )
			{
				mFolderIcon.getPos( 2 );
				//pos_x = pos_x - R3D.folder_icon_rotation_offsetx;
				//pos_y=pos_y+R3D.folder_icon_rotation_offsety;
				animation_line.push( Tween.to( myActor , View3DTweenAccessor.ROTATION , 0.2f ).target( R3D.folder_icon_rotation_degree , 0 , 0 ).ease( Linear.INOUT )
						.delay( 0.1f + delayFactor * ( Count - i ) ) );
			}
			else
			{
				mFolderIcon.getPos( 0 );
				animation_line.push( Tween.to( myActor , View3DTweenAccessor.ROTATION , 0.2f ).target( 0 , 0 , 0 ).ease( Linear.INOUT ).delay( 0.1f + delayFactor * ( Count - i ) ) );
			}
			animation_line.push( Tween.to( myActor , View3DTweenAccessor.SCALE_XY , 0.2f ).target( mFolderIcon.getScaleFactor( 0 ) , mFolderIcon.getScaleFactor( 0 ) , 0 )
					.delay( 0.1f + delayFactor * ( Count - i ) ).ease( Linear.INOUT ) );
			animation_line.push( Tween.to( myActor , View3DTweenAccessor.POS_XY , 0.3f ).target( mFolderIcon.getPosx() , mFolderIcon.getPosy() , 0 ).ease( Cubic.OUT )
					.delay( delayFactor * ( Count - i ) ) );
			//animation_line.push(viewTween);
		}
		//this.x = this.x - mFolderIcon.x;
		//this.y = this.y - mFolderIcon.y;
		animation_line.push( Tween.to( this , View3DTweenAccessor.POS_XY , 0.4f ).target( -mFolderIcon.x , -Utils3D.getScreenHeight() ).ease( Linear.INOUT ) );
		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	private void startAnimation(
			ArrayList<View3D> templist )
	{
		mFolderIcon.changeTextureRegion( templist , mFolderIcon.getIconBmpHeight() );
		if( mFolderIcon.folder_style == FolderIcon3D.folder_rotate_style )
		{
			startAnimation_rotate( templist );
		}
		else
		{
			startAnimation_scale( templist );
		}
	}
	
	private void startAnimation_scale(
			ArrayList<View3D> templist )
	{
		float delayFactor = 0;
		View3D view;
		stopAnimation();
		animation_line = Timeline.createParallel();
		int Count = templist.size();
		if( Count > R3D.folder_max_num / 2 )
		{
			delayFactor = 0.02f;
		}
		else
		{
			delayFactor = 0.04f;
		}
		float duration = 0.3f;
		//teapotXu add start for folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function )
		{
			duration = 0.15f;
		}
		//teapotXu add end for folder in Mainmenu		
		for( int i = 0 ; i < Count ; i++ )
		{
			view = templist.get( i );
			mFolderIcon.changeOrigin( view );
			view.y = view.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
			mFolderIcon.addViewBefore( mFolderIcon.folder_front , view );
			mFolderIcon.getPos( i );
			animation_line.push( Tween.to( view , View3DTweenAccessor.POS_XY , duration ).target( mFolderIcon.getPosx() , mFolderIcon.getPosy() , 0 ).ease( Linear.INOUT ).delay( delayFactor * i ) );
			animation_line.push( Tween.to( view , View3DTweenAccessor.SCALE_XY , duration ).target( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) , 0 ).delay( delayFactor * i )
					.ease( Cubic.OUT ) );
		}
		//this.x = this.x - mFolderIcon.x;
		//this.y = this.y - mFolderIcon.y;
		duration = 0.5f;
		animation_line.push( Tween.to( this , View3DTweenAccessor.POS_XY , duration ).target( -mFolderIcon.x , -Utils3D.getScreenHeight() ).ease( Linear.INOUT ) );
		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
	}
	
	public void DealButtonOKDown()
	{
		iconContain.enableAnimation( false );
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		//		mFolderIcon.mInfo.opened = false;
		//		mFolderIcon.setFolderIconSize(
		//				(Utils3D.getScreenWidth() - R3D.folder_front_width) / 2,
		//				R3D.folder_group_bottom_margin, 0, 0);
		int Count = iconContain.getChildCount();
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = (Icon3D)iconContain.getChildAt( i );
			if( iconContain.getFocusView() != myActor )
			{
				tempInfo = ( (Icon3D)myActor ).getItemInfo();
				tempInfo.screen = i;
				Root3D.addOrMoveDB( tempInfo , mFolderIcon.mInfo.id );
				( (Icon3D)myActor ).setInShowFolder( false );
				( (Icon3D)myActor ).setItemInfo( ( (Icon3D)myActor ).getItemInfo() );
				//mFolderIcon.calcCoordinate(myActor);
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
		iconContain.removeAllViews();
		bOutDragRemove = false;
		startAnimation( templist );
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			animation_line = null;
			mFolderIcon.stopTween();
			removeView( buttonOK );
			removeView( iconContain );
			removeView( inputTextView );
			mFolderIcon.closeFolderStartAnim();
		}
	}
	
	private void dealFolderRename()
	{
		mFolderIcon.bRenameFolder = true;
		SendMsgToAndroid.sendRenameFolderMsg( mFolderIcon );
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
		if( keycode == KeyEvent.KEYCODE_BACK && bEnableTouch )
		{
			bCloseFolderByDrag = false;
			DealButtonOKDown();
			return true;
		}
		return super.keyUp( keycode );
	}
	
	/* (non-Javadoc)
	 * @see com.iLoong.launcher.UI3DEngine.ViewGroup3D#onTouchUp(float, float, int)
	 */
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		View3D hitView = hit( x , y );
		Log.d( "touch" , " Folder3D onTouchUp:" + name + " bEnableTouch=" + bEnableTouch + " y:" + y );
		if( bEnableTouch )
		{
			if( buttonOK.isVisible() && x > buttonOK.x - buttonOK.width / 2 && y > buttonOK.y - buttonOK.height / 2 && x < Utils3D.getScreenWidth() && y < Utils3D.getScreenHeight() )
			{
				DealButtonOKDown();
				Log.d( "touch" , " Folder3D 111 onTouchUp:" + name + " bEnableTouch=" + bEnableTouch + " y:" + y );
				return true;
			}
			else if( hitView.name == inputTextView.name )
			{
				dealFolderRename();
				return true;
			}
			else
			{
				Log.d( "touch" , " Folder3D 222 onTouchUp:" + name + " bEnableTouch=" + bEnableTouch + " y:" + y );
				return super.onTouchUp( x , y , pointer );
			}
		}
		else
		{
			return false;
		}
		//return super.onTouchUp(x, y, pointer);
	}
	
	//	@Override
	//	public boolean onClick (float x, float y)
	//	{
	//		View3D hitView= hit(x,y);
	//		
	//		if (hitView.name == buttonOK.name)
	//		{
	//			DealButtonOKDown();
	//			return true;
	//		}
	//		else if (hitView.name ==inputTextView.name )
	//		{
	//			dealFolderRename();
	//			return true;
	//		}
	//		else
	//		{
	//		   return super.onClick(x, y);
	//		}
	//	}
	void setFolderIcon(
			FolderIcon3D icon )
	{
		mFolderIcon = icon;
	}
	
	public void setEditText(
			String text )
	{
		mFolderIcon.mInfo.setTitle( text );
		inputNameString = text;
		viewParent.onCtrlEvent( this , MSG_UPDATE_VIEW );
	}
	
	public void RemoveViewByItemInfo(
			ItemInfo item )
	{
		if( bOutDragRemove )
		{
			return;
		}
		View3D myActor;
		int Count = iconContain.getChildCount();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = iconContain.getChildAt( i );
			if( myActor instanceof Icon3D )
			{
				ItemInfo info = ( (Icon3D)myActor ).getItemInfo();
				if( item.equals( info ) )
				{
					iconContain.removeView( myActor );
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
						View3D focus = iconContain.getFocusView();
						iconContain.releaseFocus();
						focus.toAbsoluteCoords( point );
						//focus.setPosition(point.x, point.y);
						bOutDragRemove = true;
						//teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST && ( (Icon3D)focus ).getUninstallStatus() == false )
							{
								//do not remove the focus icon, but it needs set the flag that indicates exiting to CellLayout
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
						//				mFolderIcon.mInfo.remove((ShortcutInfo) ((Icon3D)focus).getItemInfo());
						//teapotXu add end for Folder in Mainmenu				
						DealButtonOKDown();
						this.setTag( new Vector2( point.x , point.y ) );
						dragObjects.clear();
						//teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST && ( ( (Icon3D)focus ).getUninstallStatus() == false ) )
							{
								Icon3D icon3d = (Icon3D)focus;
								icon3d.hideSelectedIcon();
								Icon3D iconClone = icon3d.clone();
								// add back to mFolderIcon
								mFolderIcon.changeOrigin( focus );
								( (Icon3D)focus ).cancelSelected();
								focus.y = focus.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
								mFolderIcon.addViewBefore( mFolderIcon.folder_front , focus );
								iconClone.clearState();
								dragObjects.add( iconClone );
							}
							else
							{
								dragObjects.add( focus );
								//xiatian add start	//for mainmenu sort by user
								if( DefaultLayout.mainmenu_sort_by_user_fun == true )
								{
									//notice AppHost3D that one icon is draging outof a folder,
									viewParent.onCtrlEvent( this , FolderMIUI3D.MSG_UPDATE_GRIDVIEW_FOR_LOCATION );
								}
								//xiatian add end
							}
						}
						else
						{
							dragObjects.add( focus );
						}
						//				dragObjects.add(focus);
						//teapotXu add end for Folder in Mainmenu	
						bCloseFolderByDrag = true;
						return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					}
			}
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		super.draw( batch , parentAlpha );
		if( titleTexture != null && inputTextView != null && bNeedUpdate )
		{
			batch.draw( titleTexture , inputTextView.x ,
			//titleOffsetY,
					inputTextView.y + this.y ,
					titleWidth ,
					titleHeight );
		}
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
}
