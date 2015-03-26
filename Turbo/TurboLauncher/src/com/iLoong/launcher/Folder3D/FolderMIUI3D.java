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
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.IconBase3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.DesktopEdit.CustomShortcutIcon;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.TextField3D;
import com.iLoong.launcher.UI3DEngine.TextFieldListener;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.data.ApplicationInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


/* Folder3D Layout height=Utils3D.getScreenHeight()-R3D.workspace_cell_height girdHeight=height-R3D.icongroup_button_height/2
 * 
 * buttonHeight/2 girdHeight R3D.workspace_Cell_height */
public class FolderMIUI3D extends ViewGroup3D implements DragSource3D , TextFieldListener
{
	
	public static final int MSG_ON_DROP = 0;
	public static final int MSG_UPDATE_VIEW = 1;
	public static final int MSG_ADD_FROM_ALLAPP = 2;
	public static final int MSG_UPDATE_GRIDVIEW_FOR_LOCATION = 3;//xiatian add	//for mainmenu sort by user
	private static float m_scaleFactor;
	private static NinePatch m_bgNinePatch;
	private static View3D m_dividerView;
	private static final int ONE_PAGE_ICONS_MAX = 9;
	private TextField3D m_editableTilte;
	private int m_titleBarHeight = 123;
	private int m_titleWidth;
	private int m_titleHeight = 50;
	private int m_titleMarginTop = 44;
	private int folder_icons_gap = 30;
	private int folder_icon_padding_left = 60;
	private int folder_icon_padding_right = 60;
	private int folder_icon_padding_top = 30;
	private int folder_icon_padding_bottom = 30;
	private Paint paint;
	public FolderIcon3D mFolderIcon;
	private IconsNPage iconsContainer;
	private ButtonView3D buttonAdd;
	private String inputNameString;
	private boolean bNeedUpdate = false;
	private Timeline animation_line = null;
	// private Timeline viewTween=null;
	private static boolean displayButton = true;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	private Timeline v5_animation_line = null;
	private boolean bDragItemClose = false;
	private int m_posGapMin;
	private boolean m_bClosFolderAnim;
	public Icon3D mIcon3DToDragView = null;//xiatian add	//for mainmenu sort by user
	public boolean bEnableTouch = true;
	public boolean bOutDragRemove = false;
	public boolean bCloseFolderByDrag = false;
	public float targetPosX;
	public float targetPosY;
	private int m_countX;
	private int m_countY;
	private int m_gridWidth;
	private int m_gridHeight;
	private boolean bShowAppList;
	
	public FolderMIUI3D()
	{
		this( null );
		// TODO Auto-generated constructor stub
	}
	
	public FolderMIUI3D(
			String name )
	{
		super( name );
		m_scaleFactor = Utils3D.getScreenWidth() / 720f;
		m_titleBarHeight *= m_scaleFactor;
		m_titleHeight *= m_scaleFactor;
		m_titleMarginTop *= m_scaleFactor;
		folder_icons_gap *= m_scaleFactor;
		folder_icon_padding_left *= m_scaleFactor;
		folder_icon_padding_right *= m_scaleFactor;
		folder_icon_padding_top *= m_scaleFactor;
		folder_icon_padding_bottom *= m_scaleFactor;
		if( m_bgNinePatch == null )
		{
			m_bgNinePatch = new NinePatch( R3D.getTextureRegion( R3D.folder_bg_name ) , 20 , 20 , 20 , 20 );
		}
		setBackgroud( m_bgNinePatch );
		int countXMax = 3;
		int widthMax = countXMax * ( R3D.workspace_cell_width + folder_icons_gap ) + folder_icon_padding_left + folder_icon_padding_right;
		m_posGapMin = ( Utils3D.getScreenWidth() - widthMax ) / 2;
		if( paint == null )
		{
			paint = new Paint();
			paint.setColor( Color.WHITE );
			paint.setAntiAlias( true );
			paint.setTextSize( m_titleHeight - 10 * m_scaleFactor );
		}
	}
	
	public void onThemeChanged()
	{
	}
	
	private void attrConstruct(
			float duration ,
			TweenEquation tweenEquation ,
			int hideIconNum )
	{
		int countXMax = 3;
		m_countX = 1;
		int iconNum = mFolderIcon.mInfo.contents.size() + 1 - hideIconNum; // +1 for buttonAdd
		if( iconNum <= 2 )
		{
			m_countX = iconNum;
		}
		else if( iconNum <= 4 )
		{
			m_countX = 2;
		}
		else
		{
			m_countX = countXMax;
		}
		m_countY = ( iconNum + m_countX - 1 ) / m_countX;
		m_gridWidth = m_countX * ( R3D.workspace_cell_width + folder_icons_gap ) + folder_icon_padding_left + folder_icon_padding_right;
		if( m_countY > 3 )
		{
			m_countY = 3;
		}
		m_gridHeight = m_countY * ( R3D.workspace_cell_height + folder_icons_gap / 2 ) + folder_icon_padding_top + folder_icon_padding_bottom;
		if( m_countX == 1 )
		{
			m_gridWidth = R3D.workspace_cell_width + folder_icon_padding_left + folder_icon_padding_right;
		}
		setSize( m_gridWidth , m_gridHeight + m_titleBarHeight );
		setPosition( mFolderIcon.mInfo.x - ( this.getWidth() - R3D.workspace_cell_width ) / 2 , mFolderIcon.mInfo.y - ( this.getHeight() - R3D.workspace_cell_height ) / 2 );
		setOrigin( this.getWidth() / 2 , this.getHeight() / 2 );
		targetPosX = this.getX();
		targetPosY = this.getY();
		if( this.getX() < m_posGapMin )
		{
			targetPosX = m_posGapMin;
		}
		else if( ( this.getX() + this.getWidth() ) > ( Utils3D.getScreenWidth() - m_posGapMin ) )
		{
			targetPosX = Utils3D.getScreenWidth() - m_posGapMin - this.getWidth();
		}
		if( this.getY() < R3D.Workspace_celllayout_bottompadding )
		{
			targetPosY = R3D.Workspace_celllayout_bottompadding;
		}
		else if( ( this.getY() + this.getHeight() ) > ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - m_posGapMin ) )
		{
			targetPosY = Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - m_posGapMin - this.getHeight();
		}
		startTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , targetPosX , targetPosY , 0 );
	}
	
	public void buildV5Elements(
			float duration ,
			TweenEquation tweenEquation ,
			int hideIconNum )
	{
		attrConstruct( duration , tweenEquation , hideIconNum );
		if( iconsContainer == null )
		{
			iconsContainer = new IconsNPage( "iconsContainer" );
		} 
		else if( iconsContainer.indicatorView != null )
		{
			iconsContainer.indicatorView.setSize( m_gridWidth - 2 * 25f * m_scaleFactor , iconsContainer.indicatorView.getHeight() );
			iconsContainer.indicatorView.setPosition( 25f * m_scaleFactor , m_gridHeight - iconsContainer.indicatorView.getHeight() );
		}
		iconsContainer.setSize( m_gridWidth , m_gridHeight );
		iconsContainer.setOrigin( iconsContainer.getWidth() / 2 , iconsContainer.getHeight() / 2 );
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
		m_titleWidth = (int)this.getWidth() - (int)( 50f * m_scaleFactor );
		buildTextField3D( m_titleWidth , m_titleHeight , title );
		if( buttonAdd == null )
		{
			buttonAdd = new ButtonView3D( "button_add" , "theme/folder/folder_add_app.png" , "theme/folder/folder_add_app_focus.png" );
		}
		if( m_dividerView == null )
		{
			Bitmap tmp = ThemeManager.getInstance().getBitmap( "theme/folder/folder_divider.9.png" );
			m_dividerView = new View3D( "dividerView" );
			m_dividerView.setBackgroud( new NinePatch( new BitmapTexture( tmp , true ) ) );
		}
		m_dividerView.setSize( this.getWidth() - 50f * m_scaleFactor , m_dividerView.background9.getTotalHeight() );
		m_dividerView.setOrigin( m_dividerView.getWidth() / 2 , m_dividerView.getHeight() / 2 );
		m_dividerView.setPosition( 25f * m_scaleFactor , iconsContainer.getHeight() );
		addView( m_dividerView );
		addView( iconsContainer );
		addView( m_editableTilte );
	}
	
	public void setUpdateValue(
			boolean bFlag )
	{
		bNeedUpdate = bFlag;
		bEnableTouch = true;
		if( buttonAdd != null && !buttonAdd.isVisible() )
		{
			UserFolderInfo folderInfo = (UserFolderInfo)mFolderIcon.getItemInfo();
			buttonAdd.show();
		}
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
		bDragItemClose = false;
		Icon3D temp = null;
		// iconContain.setAnimationDelay(0);
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = mFolderIcon.getChildAt( i );
			// if (myActor instanceof Icon3D) {
			//
			// temp=(Icon3D) myActor.clone();
			// temp.setItemInfo(((Icon3D) myActor).getItemInfo());
			// mFolderIcon.changeTextureRegion(temp,R3D.workspace_cell_height);
			// // myActor.show();
			// // myActor.setRotation(0);
			// // myActor.setScale(1.0f, 1.0f);
			// //
			// //
			// mFolderIcon.changeTextureRegion(myActor,R3D.workspace_cell_height);
			// temp.setInShowFolder(true);
			// temp.setItemInfo(((Icon3D)myActor).getItemInfo());
			// //
			// // myActor.x=myActor.x-this.x;
			// // myActor.y=myActor.y-this.y;
			// templist.add(temp);
			// }
			if( myActor instanceof Icon3D )
			{
				myActor.setRotation( 0 );
				myActor.setScale( 1.0f , 1.0f );
				mFolderIcon.changeTextureRegion( myActor , R3D.workspace_cell_height );
				( (Icon3D)myActor ).setInShowFolder( true );
				( (Icon3D)myActor ).setItemInfo( ( (Icon3D)myActor ).getItemInfo() );
				myActor.x = myActor.x - this.x;
				myActor.y = myActor.y - this.y;
				//teapotXu add start for Folder in Mainmenu
				//only when folder is in Mainmenu, hide icons in folder are needed to hide
				/*if(DefaultLayout.mainmenu_folder_function == true && (mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST))
				{
					if(((Icon3D) myActor).getItemInfo()instanceof ShortcutInfo)
					{
						ApplicationInfo appInfo = ((ShortcutInfo)((Icon3D) myActor).getItemInfo()).appInfo;
						
						if(appInfo != null && appInfo.isHideIcon && ((Icon3D) myActor).getHideStatus() == false)
						{
							//don't add this icon into the FolderList.
							continue;
						}
						
					}
				}*/
				//teapotXu add end for Folder in Mainmenu
				myActor.show();
				templist.add( myActor );
			}
		}
		if( iconsContainer != null )
		{
			if( buttonAdd != null )
			{
				templist.add( buttonAdd );
			}
			int gridPageCount;
			if( templist.size() % ONE_PAGE_ICONS_MAX == 0 )
			{
				gridPageCount = templist.size() / ONE_PAGE_ICONS_MAX;
			}
			else
			{
				gridPageCount = templist.size() / ONE_PAGE_ICONS_MAX + 1;
			}
			if( gridPageCount == 0 )
			{
				gridPageCount = 1;
			}
			ArrayList<View3D> iconList = new ArrayList<View3D>();
			for( int i = 0 , j = 0 ; i < gridPageCount ; i++ )
			{
				IconGridView gridPage = new IconGridView( "IconGridView" + i , m_gridWidth , m_gridHeight , m_countX , m_countY );
				gridPage.setPadding( folder_icon_padding_left , folder_icon_padding_right , folder_icon_padding_top , folder_icon_padding_bottom );
				gridPage.setCellCount( m_countX , m_countY );
				gridPage.enableAnimation( false );
				for( ; j < templist.size() ; )
				{
					iconList.add( templist.get( j ) );
					j++;
					if( j % ONE_PAGE_ICONS_MAX == 0 )
					{
						break;
					}
				}
				gridPage.addItem( iconList );
				iconList.clear();
				iconsContainer.addPage( i , gridPage );
			}
			iconsContainer.show();
			iconsContainer.setCurrentPage( 0 );
		}
		//teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST )
			{
				if( templist.size() > 0 )
				{
					//xiatian start	//for mainmenu sort by user
					//xiatian del start
					//					if(((Icon3D)templist.get(0)).getUninstallStatus() == false){
					//						iconContain.setAutoDrag(false);
					//					}else{
					//						iconContain.setAutoDrag(true);
					//					}
					//xiatian del end
					//xiatian add start
					if( DefaultLayout.mainmenu_sort_by_user_fun )
					{
						if( iconsContainer != null )
						{
							for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
							{
								( (IconGridView)iconsContainer.getChildAt( i ) ).setAutoDrag( true );
							}
						}
					}
					else
					{
						if( templist.get( 0 ) instanceof Icon3D && ( (Icon3D)templist.get( 0 ) ).getUninstallStatus() )
						{
							if( iconsContainer != null )
							{
								for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
								{
									( (IconGridView)iconsContainer.getChildAt( i ) ).setAutoDrag( false );
								}
							}
						}
						else
						{
							if( iconsContainer != null )
							{
								for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
								{
									( (IconGridView)iconsContainer.getChildAt( i ) ).setAutoDrag( true );
								}
							}
						}
					}
					//xiatian add end
					//xiatian end
				}
			}
		}
		//teapotXu add end for Folder in Mainmenu			
	}
	
	public void addIcon(
			Icon3D newIcon )
	{
		GridView3D curGridPage = null;
		if( iconsContainer != null )
		{
			curGridPage = (GridView3D)iconsContainer.getCurrentView();
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
		if( iconsContainer != null )
		{
			curGridPage = (GridView3D)iconsContainer.getCurrentView();
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
		if( iconsContainer == null )
		{
			return;
		}
		int Count = iconsContainer.getChildCount();
		View3D myActor;
		IconGridView gridPage;
		ItemInfo tempInfo;
		for( int i = 0 ; i < Count ; i++ )
		{
			gridPage = (IconGridView)iconsContainer.getChildAt( i );
			for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
			{
				myActor = gridPage.getChildAt( j );
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
	}
	
	private void buildTextField3D(
			float textWidth ,
			float textHeight ,
			String title )
	{
		if( findView( "editableTilte" ) == null )
		{
			m_editableTilte = new TextField3D( "editableTilte" , textWidth , textHeight , paint );
			m_editableTilte.setPosition( ( this.getWidth() - m_editableTilte.getWidth() ) / 2 , iconsContainer.getHeight() + ( m_titleBarHeight - m_editableTilte.getHeight() ) / 2 );
			m_editableTilte.setOrigin( m_editableTilte.getWidth() / 2 , m_editableTilte.getHeight() / 2 );
			m_editableTilte.setText( title );
			m_editableTilte.updateDisplayOpacityText();
			m_editableTilte.setTextFieldListener( this );
			// inputTextField3D.setSelection(0, title.trim().length());
			m_editableTilte.setKeyboardAdapter( null );
			m_editableTilte.setEditable( true );
			m_editableTilte.show();
			mFolderIcon.bRenameFolder = false;
		}
		else
		{
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				if( m_editableTilte != null )
				{
					m_editableTilte.setSize( textWidth , m_titleHeight );
					m_editableTilte.setOrigin( m_editableTilte.getWidth() / 2 , m_editableTilte.getHeight() / 2 );
					m_editableTilte.setPosition( ( this.getWidth() - m_editableTilte.getWidth() ) / 2 , iconsContainer.getHeight() + ( m_titleBarHeight - m_editableTilte.getHeight() ) / 2 );
					m_editableTilte.setText( title );
					m_editableTilte.updateDisplayOpacityText();
					// inputTextField3D.setSelection(0, title.trim().length());
					m_editableTilte.setKeyboardAdapter( null );
					m_editableTilte.setEditable( true );
					m_editableTilte.show();
					mFolderIcon.bRenameFolder = false;
				}
			}
		}
	}
	
	private Bitmap titleToTexture(
			String title ,
			int titleHeight )
	{
		Bitmap bmp = Bitmap.createBitmap( m_titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		//teapotXu add start for MIUI folder
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )//zhujieping
		{
			paint.setColor( Color.WHITE );
		}
		//teapotXu add end for MIUI folder
		paint.setAntiAlias( true );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
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
		if( Utils3D.measureText( paint , title ) > m_titleWidth - 2 )
		{
			while( Utils3D.measureText( paint , title ) > m_titleWidth - Utils3D.measureText( paint , "..." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "...";
		}
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
		offsetX = ( m_titleWidth - Utils3D.measureText( paint , title ) ) / 2f;
		float fontPosY = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		fontPosY = titleHeight - ( titleHeight - fontPosY ) / 2f - fontMetrics.bottom;
		canvas.drawText( title , offsetX , fontPosY , paint );
		return bmp;
	}
	
	private void startAnimation(
			ArrayList<View3D> templist )
	{
		float duration = 0.5f;
		TweenEquation tweenEquation = Quad.IN;
		float targetScale = Utils3D.getIconBmpHeight() / this.getWidth();
		float targetPosX = mFolderIcon.mInfo.x - ( this.getWidth() - R3D.workspace_cell_width ) / 2;
		float targetPosY = mFolderIcon.mInfo.y - ( this.getHeight() - R3D.workspace_cell_height ) / 2;
		m_bClosFolderAnim = true;
		v5_animation_line = Timeline.createParallel();
		v5_animation_line.push( this.obtainTween( View3DTweenAccessor.SCALE_XY , tweenEquation , duration , targetScale , targetScale , 0 ) );
		v5_animation_line.push( this.obtainTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , targetPosX , targetPosY , 0 ) );
		this.setRotationX( 0 );
		v5_animation_line.push( this.obtainTween( View3DTweenAccessor.ROTATION , tweenEquation , duration , -180 , 0 , 0 ) );
		v5_animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		//		animation_line = Timeline.createParallel();
		//		animation_line.push( obtainTween( View3DTweenAccessor.SCALE_XY , tweenEquation , duration , targetScale , targetScale , 0 ) );
		//		animation_line.push( obtainTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , targetPosX , targetPosY , 0 ) );
		//		this.setRotationX( 0 );
		//		animation_line.push( obtainTween( View3DTweenAccessor.ROTATION , tweenEquation , duration , -180 , 0 , 0 ) );
		//		mFolderIcon.setScale( (float)Utils3D.getScreenWidth() / mFolderIcon.folder_front.getWidth() , (float)Utils3D.getScreenHeight() / mFolderIcon.folder_front.getHeight() );
		//		animation_line.push( mFolderIcon.obtainTween( View3DTweenAccessor.SCALE_XY , tweenEquation , duration , 1 , 1 , 0 ) );
		//		animation_line.push( mFolderIcon.obtainTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , mFolderIcon.mInfo.x , mFolderIcon.mInfo.y , 0 ) );
		//		mFolderIcon.setRotationX( -180 );
		//		animation_line.push( mFolderIcon.obtainTween( View3DTweenAccessor.ROTATION , tweenEquation , duration , -360 , 0 , 0 ) );
		//		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		/*original code begin*/
		//		View3D view;
		//		animation_line = Timeline.createParallel();
		//		int Count = templist.size();
		//		float duration = 0.3f;
		//		float delayFactor = 0.05f;
		//		//zhujieping add 
		//		boolean miuiV5Folder = ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder;
		//		if( miuiV5Folder )
		//		{
		//			duration = DefaultLayout.blurDuration;
		//			if( DefaultLayout.blur_enable )
		//			{
		//				if( mFolderIcon.blurredView != null )
		//				{
		//					mFolderIcon.blurredView.remove();
		//				}
		//				if( mFolderIcon.lwpBackView != null )
		//				{
		//					mFolderIcon.lwpBackView.remove();
		//				}
		//				mFolderIcon.mFolderIndex = mFolderIcon.getViewIndex( mFolderIcon.mFolderMIUI3D );
		//				mFolderIcon.mFolderMIUI3D.hide();
		//				final Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
		//				root.transform = true;
		//				root.setScale( 0.8f , 0.8f );
		//				root.showOtherView();
		//				root.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1.0f , 1.0f , 0 );
		//				if( DefaultLayout.enable_hotseat_rolling )
		//				{
		//					root.getHotSeatBar().getModel3DGroup().startTween( View3DTweenAccessor.OPACITY , Cubic.OUT , duration , 1 , 0 , 0 );
		//				}
		//				if( DefaultLayout.mainmenu_folder_function == true )
		//				{
		//					if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST )
		//					{
		//						root.appHost.showAppHostV5();
		//						mFolderIcon.ishide = false;
		//					}
		//				}
		//			}
		//			else
		//			{
		//				//				duration = 0.1f;
		//			}
		//		}
		//		//teapotXu add end for Miui2 folder
		//		for( int i = 0 ; i < Count ; i++ )
		//		{
		//			view = templist.get( i );
		//			view.stopAllTween();
		//			mFolderIcon.addViewBefore( mFolderIcon.folder_front , view );
		//			mFolderIcon.changeOrigin( view );
		//			mFolderIcon.changeTextureRegion( view , mFolderIcon.getIconBmpHeight() );
		//			view.y = view.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
		//			mFolderIcon.getPos( i );
		//			//zhujieping add 
		//			float delay = 0;
		//			if( miuiV5Folder )
		//			{
		//				delay = 0;
		//			}
		//			else
		//			{
		//				delay = delayFactor * ( i / R3D.folder_icon_row_num );
		//			}
		//			if( miuiV5Folder )
		//			{
		//				animation_line.push( Tween.to( view , View3DTweenAccessor.POS_XY , duration ).target( mFolderIcon.getPosx() , mFolderIcon.getPosy() , 0 ).ease( Cubic.OUT ).delay( delay ) );
		//				if( mFolderIcon.isHideItemIcon() )
		//				{
		//					animation_line.push( Tween.to( view , View3DTweenAccessor.SCALE_XY , duration ).target( 0 , 0 , 0 ).ease( Back.OUT ).delay( delay ) );
		//				}
		//				else
		//				{
		//					animation_line.push( Tween.to( view , View3DTweenAccessor.SCALE_XY , duration ).target( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) , 0 ).ease( Back.OUT )
		//							.delay( delay ) );
		//				}
		//			}
		//			else
		//			{
		//				animation_line.push( Tween.to( view , View3DTweenAccessor.POS_XY , duration ).target( mFolderIcon.getPosx() , mFolderIcon.getPosy() , 0 ).ease( Linear.INOUT )
		//						.delay( delayFactor * ( i / R3D.folder_icon_row_num ) ) );
		//				animation_line.push( Tween.to( view , View3DTweenAccessor.SCALE_XY , duration ).target( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) , 0 ).ease( Cubic.OUT )
		//						.delay( delayFactor * ( i / R3D.folder_icon_row_num ) ) );
		//			}
		//		}
		//		setOrigin( mFolderIcon.mInfo.x + R3D.workspace_cell_width / 2 , mFolderIcon.mInfo.y + R3D.workspace_cell_height / 2 );
		//		this.stopAllTween();
		//		animation_line.push( Tween.to( this , View3DTweenAccessor.SCALE_XY , duration ).target( 0 , 0 , 0 ).ease( Cubic.OUT ) );
		//		animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		//		//mFolderIcon.RootToCellLayoutAnimTime = duration;
		//		Log.v( "FolderMIUI3D" , "MSG_WORKSPACE_ALPAH_TO_ONE" );
		//		//viewParent.onCtrlEvent(mFolderIcon,
		//		//	FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);
		/*original code end*/
	}
	
	public void DealRenameOnPause()
	{
		m_editableTilte.hideInputKeyboard();
		mFolderIcon.bRenameFolder = false;
	}
	
	public void DealButtonNoAnim()
	{
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && mFolderIcon.getScaleX() != 1 )
			{
				mFolderIcon.setScale( 1 , 1 );
			}
		}
		if( m_editableTilte != null )
		{
			m_editableTilte.hideInputKeyboard();
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		GridView3D curGridPage = null;
		if( iconsContainer != null )
		{
			curGridPage = (GridView3D)iconsContainer.getCurrentView();
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
			if( ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST ) || curGridPage.getFocusView() != myActor )
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
		if( curGridPage != null )
		{
			curGridPage.removeAllViews();
		}
		bOutDragRemove = false;
		View3D view;
		animation_line = Timeline.createParallel();
		Count = templist.size();
		float duration = 0.3f;
		//zhujieping add 
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
				//当主菜单文件夹中拖回到主菜单时，不需要如下的操作
				final Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
				if( DefaultLayout.mainmenu_folder_function == true && !( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST && ( mFolderIcon.getExitToWhere() == FolderIcon3D.TO_APPLIST ) ) )
				{
					root.transform = true;
					root.setScale( 0.8f , 0.8f );
					root.showOtherView();
					root.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , duration , 1.0f , 1.0f , 0 );
					if( mFolderIcon.getExitToWhere() == FolderIcon3D.TO_CELLLAYOUT )
					{
						root.appHost.showAppHostV5();
						mFolderIcon.ishide = false;
					}
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
		//mFolderIcon.RootToCellLayoutAnimTime = duration;
		Log.v( "FolderMIUI3D" , "DealButtonNoAnim MSG_WORKSPACE_ALPAH_TO_ONE" );
		//viewParent.onCtrlEvent(mFolderIcon,
		//	FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE);
		dealAnimationLineFinished();
	}
	
	//zhujieping add end
	public void DealButtonOKDown()
	{
		if( mFolderIcon.bAnimate == true )
		{
			return;
		}
		if( iconsContainer == null )
		{
			return;
		}
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && mFolderIcon.getScaleX() != 1 )
			{
				mFolderIcon.setScale( 1 , 1 );
			}
		}
		if( m_editableTilte != null && m_editableTilte.isVisible() == true )
		{
			m_editableTilte.hideInputKeyboard();
			if( m_editableTilte.getText().length() <= 0 )
			{
				m_editableTilte.setText( inputNameString );
			}
			else
			{
				setEditText( m_editableTilte.getText() );
			}
		}
		ItemInfo tempInfo;
		ArrayList<View3D> templist = new ArrayList<View3D>();
		templist.clear();
		mFolderIcon.releaseFocus();
		if( !bCloseFolderByDrag )
		{
			this.requestFocus();
		}
		if( iconsContainer != null )
		{
			IconGridView gridPage = null;
			View3D myActor;
			ArrayList<ItemInfo> infoList = new ArrayList<ItemInfo>();
			int screen = 0;
			for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
			{
				gridPage = (IconGridView)iconsContainer.getChildAt( i );
				for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
				{
					myActor = gridPage.getChildAt( j );
					if( myActor instanceof Icon3D )
					{
						if( ( mFolderIcon.getApplistMode() == AppList3D.APPLIST_MODE_UNINSTALL ) && ( ( (Icon3D)myActor ).getUninstallStatus() ) && ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST ) )
						{
							( (Icon3D)myActor ).reset_shake_status();
						}
						myActor.releaseDark();
						myActor.stopAllTween();
						if( ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST ) || gridPage.getFocusView() != myActor )
						{
							tempInfo = ( (Icon3D)myActor ).getItemInfo();
							tempInfo.screen = screen;
							screen++;
							infoList.add( tempInfo );
							( (Icon3D)myActor ).setInShowFolder( false );
							( (Icon3D)myActor ).setItemInfo( tempInfo );
						}
					}
				}
			}
			if( mFolderIcon.mInfo.id >= LauncherSettings.Favorites.CONTAINER_APPLIST )
			{
				AppListDB.getInstance().BatchItemsUpdate( infoList );
			}
			else
			{
				Root3D.batchUpdateItems( infoList );
			}
		}
		bOutDragRemove = false;
		viewParent.onCtrlEvent( mFolderIcon , FolderIcon3D.MSG_WORKSPACE_RECOVER );
		startAnimation( templist );
	}
	
	private void dealAnimationLineFinished()
	{
		this.releaseFocus();
		mFolderIcon.stopTween();
		mFolderIcon.setRotation( 0 );
		mFolderIcon.closeFolder();
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			//zhujieping
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
				dealAnimationLineFinished();
				if( bShowAppList )
				{
					iLoongLauncher.getInstance().getD3dListener().showAllApp();
					bShowAppList = false;
				}
			}
		}
		// xiatian add start //for mainmenu sort by user
		else if( source == v5_animation_line && type == TweenCallback.COMPLETE )
		{
			v5_animation_line.free();
			v5_animation_line = null;
			m_bClosFolderAnim = false;
			this.hide();
			ArrayList<View3D> templist = new ArrayList<View3D>();
			IconGridView gridPage;
			for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
			{
				gridPage = (IconGridView)iconsContainer.getChildAt( i );
				for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
				{
					View3D view = gridPage.getChildAt( j );
					if( view instanceof Icon3D )
					{
						templist.add( view );
					}
				}
			}
			for( int i = 0 ; i < templist.size() ; i++ )
			{
				View3D view = templist.get( i );
				view.stopAllTween();
				mFolderIcon.addViewBefore( mFolderIcon.folder_front , view );
				mFolderIcon.changeOrigin( view );
				mFolderIcon.changeTextureRegion( view , mFolderIcon.getIconBmpHeight() );
				view.y = view.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
				mFolderIcon.getPos( i );
				view.x = mFolderIcon.getPosx();
				view.y = mFolderIcon.getPosy();
				view.setScale( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) );
			}
			mFolderIcon.closeFolderStartAnimMIUI();
			mFolderIcon.mInfo.opened = true;
			viewParent.onCtrlEvent( mFolderIcon , FolderIcon3D.MSG_WORKSPACE_ALPAH_TO_ONE );
			dealAnimationLineFinished();
			if( bShowAppList )
			{
				iLoongLauncher.getInstance().getD3dListener().showAllApp();
				bShowAppList = false;
			}
		}
		// xiatian add end
	}
	
	private void dealFolderRename()
	{
		mFolderIcon.bRenameFolder = true;
		if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
		{
			if( inputNameString.endsWith( "x.z" ) )
			{
				int length = inputNameString.length();
				if( length > 3 )
				{
					inputNameString = inputNameString.substring( 0 , length - 3 );
					mFolderIcon.mInfo.title = inputNameString;
				}
			}
		}
		m_editableTilte.showInputKeyboard();
		if( targetPosY == R3D.Workspace_celllayout_bottompadding && m_countY == 1 )
		{
			startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , targetPosX , targetPosY * 2 , 0 );
		}
		//zhujieping add
		if( !ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) && !DefaultLayout.miui_v5_folder )
		{
			SendMsgToAndroid.sendRenameFolderMsg( mFolderIcon );
		}
	}
	
	@Override
	public boolean keyTyped(
			char character )
	{
		// TODO Auto-generated method stub
		boolean ret = super.keyTyped( character );
		if( character == '\n' || character == '\0' )
		{
			m_editableTilte.hideInputKeyboard();
			mFolderIcon.bRenameFolder = false;
			if( targetPosY == R3D.Workspace_celllayout_bottompadding && m_countY == 1 )
			{
				startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.3f , targetPosX , targetPosY , 0 );
			}
		}
		return ret;
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
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		// Log.v("click","View3D onClick:" + name +" x:" + x + " y:"+ y);
		View3D hitView = hit( x , y );
		if( hitView != null )
		{
			if( hitView.name == m_editableTilte.name )
			{
				return true;
			}
			if( hitView.name.equals( iLoongLauncher.getInstance().getResources().getString( RR.string.mainmenu ) ) || hitView.name.equals( CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_APPLIST ) )
			{
				DealButtonOKDown();
				bShowAppList = true;
				return true;
			}
		}
		return super.onClick( x , y );
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
				hitView.releaseDark();
			hitView = null;
		}
		if( mFolderIcon.bRenameFolder == true )
		{
			return true;
		}
		/*
		 * 这里要特别判断，因为目前的情况是girdview的cellWidth会小于Icon3D�?
		 * 实际大小，在计算index的时候，会导致计算有误差，导致onLongClick传�?到ICON本身，从而导致垃圾桶不消失和悬浮问题
		 */
		point.x = x;
		point.y = y;
		GridView3D curGridPage = null;
		if( iconsContainer != null )
		{
			curGridPage = (GridView3D)iconsContainer.getCurrentView();
		}
		this.toLocalCoordinates( iconsContainer , point );
		int retIndex = -1;
		if( curGridPage != null )
		{
			retIndex = curGridPage.getIndex( (int)point.x , (int)point.y );
		}
		if( retIndex < 0 || retIndex >= curGridPage.getChildCount() )
		{
			return true;
		}
		if( ( DefaultLayout.diable_enter_applist_when_takein_mode && Workspace3D.isHideAll ) || ( DefaultLayout.enable_edit_mode_function && Root3D.IsProhibiteditMode ) )
		{
			return true;
		}
		//xiatian add start	//for mainmenu sort by user
		if( ( DefaultLayout.mainmenu_folder_function ) && ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST ) )
		{
			AppHost3D appHost = iLoongLauncher.getInstance().getD3dListener().getAppList();
			appHost.setModeOnly( AppList3D.APPLIST_MODE_UNINSTALL );
			mFolderIcon.showHideItemsInOpenedFolder();
			appHost.appList.resetAppIconsStatusInFolder( mFolderIcon );
		}
		//xiatian add end
		//zhujieping
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && DefaultLayout.blur_enable )
		{
			boolean result = super.onLongClick( x , y );
			return result;
		}
		else
		{
			return super.onLongClick( x , y );
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
			if( hitView.name == m_editableTilte.name )
			{
				return m_editableTilte.onTouchDown( x , y , pointer );
			}
			else
			{
				if( hitView != null && hitView.name.equals( buttonAdd.name ) )
				{
					super.onTouchDown( x , y , pointer );
					return true;
				}
				else
				{
					if( mFolderIcon.bRenameFolder == true )
					{
						return true;
					}
				}
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
		if( iconsContainer == null )
		{
			return;
		}
		bEnableTouch = false;
		bNeedUpdate = false;
		mFolderIcon.bAnimate = true;
		mFolderIcon.bRenameFolder = false;
		if( m_editableTilte != null && m_editableTilte.isVisible() == true )
		{
			m_editableTilte.hideInputKeyboard();
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
		if( pointer > 0 )
		{
			return false;
		}
		View3D myActor;
		int Count = iconsContainer.getChildCount();
		IconGridView gridPage;
		for( int i = 0 ; i < Count ; i++ )
		{
			gridPage = (IconGridView)iconsContainer.getChildAt( i );
			for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
			{
				myActor = gridPage.getChildAt( j );
				if( myActor instanceof Icon3D )
				{
					myActor.releaseDark();
				}
			}
		}
		View3D hitView = hit( x , y );
		if( hitView == null )
		{
			return iconsContainer.onTouchUp( x , y , pointer );
		}
		else
		{
			if( bEnableTouch )
			{
				if( hitView.name.equals( buttonAdd.name ) )
				{
					return super.onTouchUp( x , y , pointer );
				}
				else if( hitView.name == m_editableTilte.name )
				{
					dealFolderRename();
					return true;
				}
				else if( hitView instanceof GridView3D )
				{
					return true;
				}
				else
				{
					iconsContainer.onTouchUp( x , y , pointer );
					return super.onTouchUp( x , y , pointer );
				}
			}
			if( hitView.name == this.name )
			{
				GridView3D curGridPage = (GridView3D)iconsContainer.getCurrentView();
				if( curGridPage.getFocusView() != null )
				{
					curGridPage.clearFocusView();
					DealButtonOKDown();
				}
				else
				{
					DealButtonOKDown();
					return true;
				}
			}
			return super.onTouchUp( x , y , pointer );
		}
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
		{//zhujieping modify
			mFolderIcon.mInfo.setTitle( text );
			Root3D.updateItemInDatabase( mFolderIcon.mInfo );
			inputNameString = text;
			viewParent.onCtrlEvent( this , MSG_UPDATE_VIEW );
		}
	}
	
	public void changeEditText(
			String text )
	{
		text = text.trim();
		// zhujieping add
		// if (!ThemeManager.getInstance().getBoolean(
		// "miui_v5_folder")
		// && !DefaultLayout.miui_v5_folder)
		if( !text.endsWith( "x.z" ) )
		{
			text = text.concat( "x.z" );
		}
		int length = text.length();
		if( length > 3 )
		{// zhujieping modify
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
		IconGridView gridPage;
		int Count = iconsContainer.getChildCount();
		for( int i = 0 ; i < Count ; i++ )
		{
			gridPage = (IconGridView)iconsContainer.getChildAt( i );
			for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
			{
				myActor = gridPage.getChildAt( j );
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
						for( int i = 0 ; i < iconsContainer.getPageNum() ; i++ )
						{
							GridView3D gridPage = (GridView3D)iconsContainer.getChildAt( i );
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
						bDragItemClose = true;
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
						//teapotXu add end for Folder in Mainmenu					
						DealRenameOnPause();
						//zhujieping add start
						if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
						{
							Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
							root.isDragEnd = true;
							root.folderOpened = false;
						}
						//zhujieping add start
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
								//should clear the iconState
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
							//xiatian add start	//for mainmenu sort by user
							if( DefaultLayout.mainmenu_sort_by_user_fun )
							{
								mIcon3DToDragView = (Icon3D)focus;
							}
							//xiatian add end
						}
						else
						{
							dragObjects.add( focus );
						}
						//dragObjects.add(focus);
						//teapotXu add end for Folder in Mainmenu		
						//xiatian add start	//for mainmenu sort by user
						if( ( DefaultLayout.mainmenu_sort_by_user_fun ) && ( FolderIcon3D.getApplistMode() == AppList3D.APPLIST_MODE_UNINSTALL ) )
						{
							AppList3D mAppList3D = iLoongLauncher.getInstance().getD3dListener().getAppList().appList;
							mAppList3D.show2WorkspaceView();
							mAppList3D.needAddDragViewBackToFolder = true;
						}
						//xiatian add end
						return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					}
				case GridView3D.MSG_VIEW_TOUCH_UP:
					mFolderIcon.requestFocus();
					return false;
				case GridView3D.MSG_GRID_VIEW_REQUEST_FOCUS:
					mFolderIcon.releaseFocus();
					return true;
				case IconGridView.MSG_ICON_LONG_CLICK:
					if( mFolderIcon.mInfo.opened )
					{
						View3D focus = null;
						for( int i = 0 ; i < iconsContainer.getPageNum() ; i++ )
						{
							GridView3D gridPage = (GridView3D)iconsContainer.getChildAt( i );
							if( gridPage != null )
							{
								focus = gridPage.getFocusView();
								gridPage.releaseFocus();
								if( focus != null )
								{
									Vector2 vec = (Vector2)gridPage.getTag();
									DragLayer3D.dragStartX = vec.x;
									DragLayer3D.dragStartY = vec.y;
									break;
								}
							}
						}
						focus.stopTween();
						focus.setScale( 1f , 1f );
						focus.toAbsoluteCoords( point );
						this.setTag( new Vector2( point.x , point.y ) );
						bOutDragRemove = true;
						bCloseFolderByDrag = true;
						bDragItemClose = true;
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
						//teapotXu add end for Folder in Mainmenu					
						DealRenameOnPause();
						DealButtonOKDown();
						dragObjects.clear();
						//teapotXu add start for Folder in Mainmenu
						if( DefaultLayout.mainmenu_folder_function == true )
						{
							if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST && ( ( (Icon3D)focus ).getUninstallStatus() == false ) )
							{
								Icon3D icon3d = (Icon3D)focus;
								icon3d.hideSelectedIcon();
								Icon3D iconClone = icon3d.clone();
								iconClone.x = point.x;
								iconClone.y = point.y;
								//should clear the iconState
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
						return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
					}
					return true;
			}
		}
		//teapotXu add start for Folder In Mainmenu
		else if( sender instanceof Icon3D )
		{
			switch( event_id )
			{
				case Icon3D.MSG_ICON_LONGCLICK:
					Icon3D focus_icon = (Icon3D)sender;
					if( ( DefaultLayout.mainmenu_folder_function == true ) && ( DefaultLayout.mainmenu_sort_by_user_fun == false ) //xiatian add	//for mainmenu sort by user
					)
					{
						//Now only in Folder of Mainmenu && this folder is open && Applist is not in Uninstall Mode
						if( mFolderIcon.mInfo.opened == true && mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST && focus_icon.getUninstallStatus() == false )
						{
							iconsContainer.releaseFocus();
							focus_icon.stopTween();
							focus_icon.setScale( 1f , 1f );
							focus_icon.toAbsoluteCoords( point );
							bOutDragRemove = true;
							bCloseFolderByDrag = true;
							bDragItemClose = true;
							dragObjects.clear();
							Icon3D icon3d = focus_icon;
							icon3d.hideSelectedIcon();
							Icon3D iconClone = icon3d.clone();
							iconClone.x = point.x;
							iconClone.y = point.y;
							//should clear the iconState
							iconClone.clearState();
							dragObjects.add( iconClone );
							//do not remove the focus icon, but it needs set the flag that indicates exiting to CellLayout
							mFolderIcon.setExitToWhere( FolderIcon3D.TO_CELLLAYOUT );
							DealRenameOnPause();
							//zhujieping add start
							if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder && DefaultLayout.blur_enable )
							{
								Root3D root = iLoongLauncher.getInstance().getD3dListener().getRoot();
								root.isDragEnd = true;
								root.folderOpened = false;
							}
							//zhujieping add end
							//						DealButtonOKDown();
							//xiatian start	//for Folder in Mainmenu
							//xiatian del start
							//						DealButtonNoAnim();
							//						this.setTag(new Vector2(point.x, point.y));
							//
							//						return viewParent.onCtrlEvent(this,
							//								DragSource3D.MSG_START_DRAG);	
							//xiatian del end
							//xiatian add start
							this.setTag( new Vector2( point.x , point.y ) );
							boolean ret = viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
							DealButtonNoAnim();
							return ret;
							//xiatian add end
							//xiatian end
						}
					}
					return true;
			}
		}
		else if( sender instanceof ButtonView3D )
		{
			switch( event_id )
			{
				case ButtonView3D.MSG_ADD_FROM_ALLAPP:
					MobclickAgent.onEvent( iLoongLauncher.getInstance() , "FolderEditWithAllApp" );
					mFolderIcon.releaseFocus();
					viewParent.onCtrlEvent( this , MSG_ADD_FROM_ALLAPP );
					break;
			}
			return true;
		}
		//teapotxu add end for Folder in Mainmenu
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( m_bClosFolderAnim && this.getRotation() > -180 && this.getRotation() < -90 )
		{
			m_bClosFolderAnim = false;
			v5_animation_line.free();
			v5_animation_line = null;
			this.hide();
			ArrayList<View3D> templist = new ArrayList<View3D>();
			for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
			{
				GridView3D gridPage = (GridView3D)iconsContainer.getChildAt( i );
				for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
				{
					View3D view = gridPage.getChildAt( j );
					if( view instanceof Icon3D )
					{
						templist.add( view );
					}
				}
			}
			iconsContainer.getViewList().clear();
			iconsContainer.removeAllViews();
			for( int i = 0 ; i < templist.size() ; i++ )
			{
				View3D view = templist.get( i );
				view.stopAllTween();
				mFolderIcon.addViewBefore( mFolderIcon.folder_front , view );
				mFolderIcon.changeOrigin( view );
				mFolderIcon.changeTextureRegion( view , mFolderIcon.getIconBmpHeight() );
				view.y = view.y + R3D.workspace_cell_height - R3D.workspace_cell_width;
				mFolderIcon.getPos( i );
				view.x = mFolderIcon.getPosx();
				view.y = mFolderIcon.getPosy();
				view.setScale( mFolderIcon.getScaleFactor( i ) , mFolderIcon.getScaleFactor( i ) );
			}
			mFolderIcon.closeFolderStartAnimMIUI();
			mFolderIcon.mInfo.opened = true;
			float duration = 0.3f;
			TweenEquation tweenEquation = Cubic.OUT;
			animation_line = Timeline.createParallel();
			mFolderIcon.setScale( ( this.getScaleX() * this.getWidth() ) / mFolderIcon.folder_front.getWidth() , ( this.getScaleY() * this.getHeight() ) / mFolderIcon.folder_front.getHeight() );
			animation_line.push( mFolderIcon.obtainTween( View3DTweenAccessor.SCALE_XY , tweenEquation , duration , 1 , 1 , 0 ) );
			//			mFolderIcon.setPosition( this.getX() , this.getY() );
			//			animation_line.push( mFolderIcon.obtainTween( View3DTweenAccessor.POS_XY , tweenEquation , duration , mFolderIcon.mInfo.x , mFolderIcon.mInfo.y , 0 ) );
			mFolderIcon.setRotationX( -270 );
			animation_line.push( mFolderIcon.obtainTween( View3DTweenAccessor.ROTATION , tweenEquation , duration , -360 , 0 , 0 ) );
			animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
		}
		batch.setColor( color.r , color.g , color.b , color.a );
		//zhujieping
		if( ( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder ) && DefaultLayout.blur_enable )
		{
			if( !mFolderIcon.captureCurScreen )
			{
				super.draw( batch , color.a );
			}
		}
		else
		{
			super.draw( batch , color.a );
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
	
	@Override
	public void valueChanged(
			TextField3D textField ,
			String newValue )
	{
		// TODO Auto-generated method stub
	}
	
	public ArrayList<View3D> getFolderChildren()
	{
		ArrayList<View3D> list = new ArrayList<View3D>();
		int Count = iconsContainer.getChildCount();
		IconGridView gridPage = null;
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			gridPage = (IconGridView)iconsContainer.getChildAt( i );
			for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
			{
				myActor = gridPage.getChildAt( j );
				if( myActor instanceof Icon3D )
				{
					list.add( myActor );
				}
			}
		}
		return list;
	}
	
	private void attrReConstruct(
			int iconsNum )
	{
		int countXMax = 3;
		m_countX = 1;
		if( iconsNum <= 2 )
		{
			m_countX = iconsNum;
		}
		else if( iconsNum <= 4 )
		{
			m_countX = 2;
		}
		else
		{
			m_countX = countXMax;
		}
		m_countY = ( iconsNum + m_countX - 1 ) / m_countX;
		m_gridWidth = m_countX * ( R3D.workspace_cell_width + folder_icons_gap ) + folder_icon_padding_left + folder_icon_padding_right;
		if( m_countY > 3 )
		{
			m_countY = 3;
		}
		m_gridHeight = m_countY * ( R3D.workspace_cell_height + folder_icons_gap / 2 ) + folder_icon_padding_top + folder_icon_padding_bottom;
		if( m_countX == 1 )
		{
			m_gridWidth = R3D.workspace_cell_width + folder_icon_padding_left + folder_icon_padding_right;
		}
		setSize( m_gridWidth , m_gridHeight + m_titleBarHeight );
		setOrigin( this.getWidth() / 2 , this.getHeight() / 2 );
		setPosition( mFolderIcon.mInfo.x - ( this.getWidth() - R3D.workspace_cell_width ) / 2 , mFolderIcon.mInfo.y - ( this.getHeight() - R3D.workspace_cell_height ) / 2 );
		targetPosX = this.getX();
		targetPosY = this.getY();
		if( this.getX() < m_posGapMin )
		{
			targetPosX = m_posGapMin;
		}
		else if( ( this.getX() + this.getWidth() ) > ( Utils3D.getScreenWidth() - m_posGapMin ) )
		{
			targetPosX = Utils3D.getScreenWidth() - m_posGapMin - this.getWidth();
		}
		if( this.getY() < R3D.Workspace_celllayout_bottompadding )
		{
			targetPosY = R3D.Workspace_celllayout_bottompadding;
		}
		else if( ( this.getY() + this.getHeight() ) > ( Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - m_posGapMin ) )
		{
			targetPosY = Utils3D.getScreenHeight() - Utils3D.getStatusBarHeight() - m_posGapMin - this.getHeight();
		}
		setPosition( targetPosX , targetPosY );
	}
	
	private void reBuildElements(
			int iconsNum )
	{
		attrReConstruct( iconsNum );
		iconsContainer.setSize( m_gridWidth , m_gridHeight );
		iconsContainer.setOrigin( iconsContainer.getWidth() / 2 , iconsContainer.getHeight() / 2 );
		if( iconsContainer.indicatorView != null )
		{
			iconsContainer.indicatorView.setSize( m_gridWidth - 2 * 25f * m_scaleFactor , iconsContainer.indicatorView.getHeight() );
			iconsContainer.indicatorView.setPosition( 25f * m_scaleFactor , m_gridHeight - iconsContainer.indicatorView.getHeight() );
		}
		String title = inputNameString;
		if( title.endsWith( "x.z" ) )
		{
			int length = title.length();
			if( length > 3 )
			{
				title = title.substring( 0 , length - 3 );
			}
		}
		m_titleWidth = (int)this.getWidth() - (int)( 50f * m_scaleFactor );
		buildTextField3D( m_titleWidth , m_titleHeight , title );
		m_dividerView.setSize( this.getWidth() - 50f * m_scaleFactor , m_dividerView.background9.getTotalHeight() );
		m_dividerView.setOrigin( m_dividerView.getWidth() / 2 , m_dividerView.getHeight() / 2 );
		m_dividerView.setPosition( 25f * m_scaleFactor , iconsContainer.getHeight() );
	}
	
	public void addChildrenToFolder(
			ArrayList<View3D> list )
	{
		View3D actor = null;
		ItemInfo info = null;
		Icon3D icon3d = null;
		IconGridView gridPage = null;
		boolean exist = false;
		for( int i = 0 ; i < iconsContainer.getChildCount() ; i++ )
		{
			gridPage = (IconGridView)iconsContainer.getChildAt( i );
			for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
			{
				actor = gridPage.getChildAt( j );
				if( actor instanceof IconBase3D )
				{
					for( View3D view : list )
					{
						if( view.name.equals( actor.name ) )
						{
							exist = true;
							break;
						}
					}
					if( !exist )
					{
						info = ( (IconBase3D)actor ).getItemInfo();
						Root3D.deleteFromDB( info );
					}
					exist = false;
				}
			}
		}
		iconsContainer.getViewList().clear();
		iconsContainer.removeAllViews();
		if( buttonAdd != null )
		{
			list.add( buttonAdd );
		}
		reBuildElements( list.size() );
		int gridPageCount;
		if( list.size() % ONE_PAGE_ICONS_MAX == 0 )
		{
			gridPageCount = list.size() / ONE_PAGE_ICONS_MAX;
		}
		else
		{
			gridPageCount = list.size() / ONE_PAGE_ICONS_MAX + 1;
		}
		ArrayList<View3D> iconList = new ArrayList<View3D>();
		for( int i = 0 , j = 0 ; i < gridPageCount ; i++ )
		{
			gridPage = new IconGridView( "IconGridView" + i , m_gridWidth , m_gridHeight , m_countX , m_countY );
			gridPage.setPadding( folder_icon_padding_left , folder_icon_padding_right , folder_icon_padding_top , folder_icon_padding_bottom );
			gridPage.setCellCount( m_countX , m_countY );
			gridPage.enableAnimation( false );
			for( ; j < list.size() ; )
			{
				actor = list.get( j );
				if( actor instanceof Icon3D )
				{
					info = ( (Icon3D)actor ).getItemInfo();
					if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || info.itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT )
					{
						icon3d = (Icon3D)Workspace3D.createShortcut( (ShortcutInfo)info , true );
					}
					else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
					{
						icon3d = (Icon3D)DefaultLayout.showDefaultWidgetView( (ShortcutInfo)info );
					}
					else if( info.itemType == LauncherSettings.Favorites.ITEM_TYPE_CUSTOM_SHORTCUT )
					{
						icon3d = (Icon3D)CustomShortcutIcon.createCustomShortcut( (ShortcutInfo)info , true );
					}
					icon3d.hideSelectedIcon();
					icon3d.toAbsoluteCoords( point );
					iconList.add( icon3d );
					addDropNode( icon3d );
				}
				else
				{
					iconList.add( actor );
				}
				j++;
				if( j % ONE_PAGE_ICONS_MAX == 0 )
				{
					break;
				}
			}
			gridPage.addItem( iconList );
			iconList.clear();
			iconsContainer.addPage( i , gridPage );
		}
		iconsContainer.show();
		iconsContainer.setCurrentPage( 0 );
		mFolderIcon.setCurNumItems( mFolderIcon.mInfo.contents.size() );
		list.clear();
	}
	
	private void addDropNode(
			Icon3D view )
	{
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
				// view.setPosition(folder_front.x+icon_pos_x,
				// folder_front.y+icon_pos_y);
				info.x = 0;
				info.y = (int)view.y;
				info.screen = mFolderIcon.mInfo.contents.size();
				Root3D.addOrMoveDB( info , mFolderIcon.mInfo.id );
				Icon3D iconView = (Icon3D)view;
				iconView.setInShowFolder( false );
				iconView.setItemInfo( info );
				if (!mFolderIcon.mInfo.contents.contains((ShortcutInfo)info))
				{
					mFolderIcon.mInfo.add( (ShortcutInfo)info );
				}
		}
	}
	
	class ButtonView3D extends View3D
	{
		
		public static final int MSG_ADD_FROM_ALLAPP = 1;
		private TextureRegion backNormalground = null;
		private TextureRegion backPressground = null;
		private boolean press = false;
		private float posX;
		private float posY;
		
		public ButtonView3D(
				String name ,
				String normal ,
				String focus )
		{
			// TODO Auto-generated constructor stub
			super( name );
			this.width = R3D.workspace_cell_width;
			this.height = R3D.workspace_cell_height;
			this.setOrigin( width / 2 , height / 2 );
			Bitmap bmp = ThemeManager.getInstance().getBitmap( normal );
			backNormalground = new TextureRegion( new BitmapTexture( Tools.resizeBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size ) , true ) );
			bmp = ThemeManager.getInstance().getBitmap( focus );
			backPressground = new TextureRegion( new BitmapTexture( Tools.resizeBitmap( bmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size ) , true ) );
			posX = ( this.getWidth() - backNormalground.getRegionWidth() ) / 2;
			Paint paint = new Paint();
			FontMetrics fontMetrics = new FontMetrics();
			paint.setColor( Color.WHITE );
			paint.setAntiAlias( true );
			paint.setTextSize( R3D.icon_title_font );
			paint.getFontMetrics( fontMetrics );
			float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
			float paddingTop;
			if( DefaultLayout.font_double_line )
			{
				paddingTop = ( this.getHeight() - backNormalground.getRegionHeight() - 2 * singleLineHeight ) / 2;
			}
			else
			{
				paddingTop = ( this.getHeight() - backNormalground.getRegionHeight() - backNormalground.getRegionHeight() / R3D.icon_title_gap - singleLineHeight ) / 2;
			}
			posY = this.getHeight() - paddingTop - backNormalground.getRegionHeight();
		}
		
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
			press = true;
			this.addTouchedView();
			return true;
		}
		
		public boolean handleActionWhenTouchLeave()
		{
			press = false;
			this.removeTouchedView();
			return true;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			/************************ added by zhenNan.ye begin *************************/
			if( ParticleManager.particleManagerEnable )
			{
				drawParticle( batch );
			}
			/************************ added by zhenNan.ye end ***************************/
			if( FolderIcon3D.applistMode == AppList3D.APPLIST_MODE_UNINSTALL ||  FolderIcon3D.applistMode == AppList3D.APPLIST_MODE_HIDE)
			{
				batch.setColor( color.r , color.g , color.b , 0.3f );
			}
			else
			{
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			}
			if( press )
			{
				batch.draw( backPressground , posX + this.getX() , posY + this.getY() , backPressground.getRegionWidth() , backPressground.getRegionHeight() );
			}
			batch.draw( backNormalground , posX + this.getX() , posY + this.getY() , backNormalground.getRegionWidth() , backNormalground.getRegionHeight() );
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			if( FolderIcon3D.applistMode != AppList3D.APPLIST_MODE_UNINSTALL&&FolderIcon3D.applistMode != AppList3D.APPLIST_MODE_HIDE && name.equals( "button_add" ) )
			{
				if( m_editableTilte.isVisible() )
				{
					m_editableTilte.hideInputKeyboard();
					setEditText( m_editableTilte.getText() );
					mFolderIcon.bRenameFolder = false;
				}
				viewParent.onCtrlEvent( this , MSG_ADD_FROM_ALLAPP );
				return true;
			}
			mFolderIcon.bRenameFolder = false;
			return true;
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			if( pointer > 0 )
			{
				return false;
			}
			releaseFocus();
			press = false;
			this.removeTouchedView();
			return false;
		}
	}
	
	public boolean isbDragItemClose()
	{
		return bDragItemClose;
	}
	
	public void resetbDragItemClose()
	{
		bDragItemClose = false;
	}
	
	//xiatian add start	//for mainmenu sort by user
	public void showButtonAdd()
	{
		buttonAdd.show();
	}
	
	public void showFolderIconUninstall()
	{
		if( iconsContainer != null )
		{
			int Count = iconsContainer.getChildCount();
			View3D myActor;
			IconGridView gridPage;
			for( int i = 0 ; i < Count ; i++ )
			{
				gridPage = (IconGridView)iconsContainer.getChildAt( i );
				for( int j = 0 ; j < gridPage.getChildCount() ; j++ )
				{
					myActor = gridPage.getChildAt( j );
					if( myActor instanceof Icon3D )
					{
						if( ( mFolderIcon.getApplistMode() == AppList3D.APPLIST_MODE_UNINSTALL ) && ( ( (Icon3D)myActor ).getUninstallStatus() ) && ( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST ) )
						{
							( (Icon3D)myActor ).shake( true );
						}
					}
				}
			}
		}
	}
	
	//xiatian add end
	class IconsNPage extends NPageBase
	{
		
		private boolean bCanScroll = true;
		
		//		private int m_indicatorSize = 18;
		public IconsNPage(
				String name )
		{
			super( name );
			// TODO Auto-generated constructor stub
			transform = true;
			setWholePageList();
			setEffectType( 0 );
			indicatorView = new IndicatorView( "folder_indicator" , IndicatorView.INDICATOR_STYLE_COMET );
			indicatorView.setSize( m_gridWidth - 2 * 25f * m_scaleFactor , indicatorView.height );
			indicatorView.setOrigin( indicatorView.getWidth() / 2 , indicatorView.getHeight() / 2 );
			indicatorView.setPosition( 25f * m_scaleFactor , m_gridHeight - indicatorView.getHeight() );
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
			boolean ret = super.onTouchUp( x , y , pointer );
			if( isManualScrollTo && xScale != 0 )
			{
				if( xScale < 0 )
				{
					IconGridView nextGrid = (IconGridView)iconsContainer.getChildAt( nextIndex() );
					nextGrid.onTouchUp( 0 , 0 , 0 );
				}
				else
				{
					IconGridView preGrid = (IconGridView)iconsContainer.getChildAt( preIndex() );
					preGrid.onTouchUp( 0 , 0 , 0 );
				}
			}
			if( !ret )
			{
				mFolderIcon.requestFocus();
			}
			else
			{
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
				//				bDrawIndicator = false;
				return;
			}
			else
			{
				//				bDrawIndicator = true;
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
		public boolean onCtrlEvent(
				View3D sender ,
				int event_id )
		{
			// TODO Auto-generated method stub
			if( isManualScrollTo )
			{
				return false;
			}
			if( sender instanceof GridView3D )
			{
				IconGridView preGrid = null;
				IconGridView nextGrid = null;
				IconGridView curGrid = null;
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
					case IconGridView.MSG_ICON_MOVE_TO_PRE_PAGE:
						if( page_index > 0 && xScale == 0 )
						{
							curGrid = (IconGridView)iconsContainer.getChildAt( getCurrentPage() );
							preGrid = (IconGridView)iconsContainer.getChildAt( preIndex() );
							temView = preGrid.getChildAt( preGrid.getChildCount() - 1 );
							curGrid.releaseFocus();
							preGrid.requestFocus();
							curGrid.addItem( temView , 0 );
							focusView = curGrid.getFocusView();
							curGrid.clearFocusView();
							preGrid.addItem( focusView );
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
					case IconGridView.MSG_ICON_MOVE_TO_NEXT_PAGE:
						if( page_index < getPageNum() - 1 && xScale == 0 )
						{
							curGrid = (IconGridView)iconsContainer.getChildAt( getCurrentPage() );
							nextGrid = (IconGridView)iconsContainer.getChildAt( nextIndex() );
							focusView = curGrid.getFocusView();
							curGrid.clearFocusView();
							if( ( nextIndex() == getPageNum() - 1 ) && ( ( nextGrid.getChildCount() - 2 ) >= 0 ) )
							{
								nextGrid.addItem( focusView , nextGrid.getChildCount() - 2 );
							}
							else
							{
								nextGrid.addItem( focusView );
							}
							nextGrid.setFocusView( focusView );
							nextGrid.enableAnimation( true );
							curGrid.releaseFocus();
							nextGrid.requestFocus();
							temView = nextGrid.getChildAt( 0 );
							curGrid.addItem( temView );
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
				Gdx.gl.glScissor( (int)( targetPosX + 5f * m_scaleFactor ) , (int)targetPosY , (int)( m_gridWidth - 5f * 2 * m_scaleFactor ) , (int)m_gridHeight );
			}
			super.draw( batch , parentAlpha );
			if( xScale != 0 )
			{
				Gdx.gl.glDisable( GL10.GL_SCISSOR_TEST );
			}
		}
	}
	
	class IconGridView extends GridView3D
	{
		
		public static final int MSG_ICON_MOVE_TO_PRE_PAGE = -2;
		public static final int MSG_ICON_MOVE_TO_NEXT_PAGE = -3;
		public static final int MSG_ICON_LONG_CLICK = -4;
		
		public IconGridView(
				String name ,
				float width ,
				float height ,
				int countx ,
				int county )
		{
			// TODO Auto-generated constructor stub
			super( name , width , height , countx , county );
		}
		
		@Override
		public boolean onLongClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			boolean rst = false;
			if( hit( x , y ).name.equals( "button_add" ) )
			{
				return true;
			}
			rst = super.onLongClick( x , y );
			if( mFolderIcon.getFromWhere() == FolderIcon3D.FROM_APPLIST && ( FolderIcon3D.getApplistMode() != AppList3D.APPLIST_MODE_UNINSTALL ) )
			{
				point.x = x;
				point.y = y;
				this.toAbsolute( point );
				this.setTag( point );
				return viewParent.onCtrlEvent( this , MSG_ICON_LONG_CLICK );
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
			int index = getIndex( (int)x , (int)y );
			if( index >= 0 && index < getChildCount() )
			{
				if( getChildAt( index ).name.equals( "button_add" ) )
					return true;
			}
			if( getFocusView() != null )
			{
				enableAnimation( true );
				if( iconsContainer.getPageNum() > 1 && iconsContainer.getCurrentPage() > 0 && x < folder_icon_padding_left )
				{
					return viewParent.onCtrlEvent( this , MSG_ICON_MOVE_TO_PRE_PAGE );
				}
				else if( iconsContainer.getPageNum() > 1 && iconsContainer.getCurrentPage() < iconsContainer.getPageNum() - 1 && x > this.width - folder_icon_padding_right )
				{
					return viewParent.onCtrlEvent( this , MSG_ICON_MOVE_TO_NEXT_PAGE );
				}
			}
			return super.onTouchDragged( x , y , pointer );
		}
	}
}
