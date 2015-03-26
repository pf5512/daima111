package com.iLoong.launcher.miui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.DragSource3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Widget2DShortcut;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Contact3DShortcut;
import com.iLoong.launcher.Widget3D.Folder3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DShortcut;
import com.iLoong.launcher.Widget3D.Widget3DVirtual;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.WidgetShortcutInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class MIUIWidgetList extends NPageBase implements DragSource3D
{
	
	public static boolean inited = false;
	// private int mCellCountX = 4;
	// private int mCellCountY = 5;
	public static int mWidgetCountX = 4;
	public static int mWidgetCountY = 1;
	public static ArrayList<WidgetShortcutInfo> mWidgets;
	private List<View3D> mWidget3DList;
	private GridPool widgetGridPool;
	private HashMap<String , Widget2DShortcut> widget2DMap;
	public static int widget3DPageCount = 0;
	public static int widget2DPageCount = 0;
	public static int widgetPageCount = 0;
	// private IconCache iconCache;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	private ArrayList<View3D> selectedObjects = new ArrayList<View3D>();
	public static final int MSG_MIUIWIDGET_DECIDE_PAGE_ADDED = 0;
	public int mode = APPLIST_MODE_NORMAL;
	public boolean saveType = true;
	public int normalType;
	public static final int APP_LIST3D_KEY_BACK = 0;
	public static final int APP_LIST3D_SHOW = 1;
	public static final int APP_LIST3D_HIDE = 2;
	public static final int APPLIST_MODE_UNINSTALL = 0;
	public static final int APPLIST_MODE_HIDE = 1;
	public static final int APPLIST_MODE_NORMAL = 2;
	public static final int APPLIST_MODE_USERAPP = 3;
	private static int singleLineHeight = 0;
	
	// public static final int MSG_START_DRAG = 0;
	// Mesh mesh = null;
	// private boolean mScrollToWidget = true;
	// public boolean mHideMainmenuWidget = false;
	public MIUIWidgetList(
			String name )
	{
		super( name );
		mWidgets = new ArrayList<WidgetShortcutInfo>();
		mWidget3DList = new ArrayList<View3D>();
		x = 0;
		y = 0;
		drawIndicator = false;
		setSingleLineHeight();
		width = Utils3D.getScreenWidth();
		height = DefaultLayout.app_icon_size + R3D.miui_widget_indicator_height + R3D.app_widget3d_gap + getSingleLineHeight();
		setEffectType( APageEase.COOLTOUCH_EFFECT_DEFAULT );
		widgetGridPool = new GridPool( 3 , Utils3D.getScreenWidth() , (int)height , mWidgetCountX , mWidgetCountY );
		widget2DMap = new HashMap<String , Widget2DShortcut>();
		widget3DPageCount = 0;
		widget2DPageCount = 0;
		transform = true;
	}
	
	public static int getSingleLineHeight()
	{
		if( singleLineHeight == 0 )
		{
			setSingleLineHeight();
		}
		return singleLineHeight;
	}
	
	private static void setSingleLineHeight()
	{
		if( singleLineHeight != 0 )
		{
			return;
		}
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		if( DefaultLayout.title_style_bold )
			paint.setFakeBoldText( true );
		FontMetrics fontMetrics = paint.getFontMetrics();
		singleLineHeight = (int)Math.ceil( fontMetrics.bottom - fontMetrics.top );
	}
	
	public void syncWidgetPageItems(
			int page ,
			boolean immediate )
	{
		//notice: it is necessary to return here ,
		//because following code will clear bitmap of widget2D after initialize.
		if( RR.net_version )
			return;
		int numCells = mWidgetCountX * mWidgetCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getWidgetCount() );
		GridView3D layout = (GridView3D)view_list.get( page );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; i++ )
		{
			if( i < getWidget3DCount() )
			{
				View3D widget = getWidget3D( i );
				if( widget instanceof Widget3DShortcut )
				{
					Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
					widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetShortcut.setWidget3DShortcutShownPlace( true );
					widgetShortcut.makeShortcut();
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetShortcut.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetShortcut.showHide();
					else
						widgetShortcut.clearState();
					widgetShortcut.newAppGridIndex = page;
					if( ( widgetShortcut.newAppGridIndex != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
						widgetShortcut.oldAppGridIndex = page;
					widgetShortcut.oldX = widgetShortcut.x;
					widgetShortcut.oldY = widgetShortcut.y;
				}
				else if( widget instanceof Widget3DVirtual )
				{
					Widget3DVirtual widgetIcon = (Widget3DVirtual)widget;
					if( widgetIcon.uninstalled )
					{
						mWidget3DList.remove( widgetIcon );
						endIndex--;
						continue;
					}
					widgetIcon.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetIcon.makeShortcut();
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetIcon.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetIcon.showHide();
					else
						widgetIcon.clearState();
					widgetIcon.newAppGridIndex = page;
					if( ( widgetIcon.newAppGridIndex != page_index && widgetIcon.oldAppGridIndex != page_index ) )
						widgetIcon.oldAppGridIndex = page;
					widgetIcon.oldX = widgetIcon.x;
					widgetIcon.oldY = widgetIcon.y;
				}
				layout.addItem( widget );
			}
			else
			{
				WidgetShortcutInfo widgetInfo = getWidget2D( i - getWidget3DCount() );
				if( widgetInfo == null )
					continue;
				Widget2DShortcut widgetShortcut = widget2DMap.get( widgetInfo.textureName );
				if( widgetShortcut != null )
				{
					widgetShortcut.clearState();
					widgetShortcut.newAppGridIndex = page;
					if( ( page != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
						widgetShortcut.oldAppGridIndex = page;
					widgetShortcut.oldX = widgetShortcut.x;
					widgetShortcut.oldY = widgetShortcut.y;
					layout.addItem( widgetShortcut );
				}
				else
				{
					if( widgetInfo.isWidget == true )
					{
						if( widgetInfo.widgetHostBitmap != null )
						{
							Texture texture = new BitmapTexture( widgetInfo.widgetHostBitmap );
							texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
							TextureRegion widgetRegion = new TextureRegion( texture );
							if( !widgetInfo.widgetHostBitmap.isRecycled() )
							{
								widgetInfo.widgetHostBitmap.recycle();
								Log.v( "hjwmiui" , "widgetHostBitmap is recycle label is " + widgetInfo.label );
								widgetInfo.widgetHostBitmap = null;
							}
							widgetShortcut = new Widget2DShortcut( widgetInfo.label , widgetRegion );
						}
					}
					else
					{
						if( widgetInfo.widget2DBitmap != null )
						{
							Texture texture = new BitmapTexture( widgetInfo.widget2DBitmap );
							texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
							TextureRegion widgetRegion = new TextureRegion( texture );
							if( !widgetInfo.widget2DBitmap.isRecycled() )
							{
								widgetInfo.widget2DBitmap.recycle();
								Log.v( "hjwmiui" , "widget2DBitmapResolveInfo is recycle label is " + widgetInfo.label );
								widgetInfo.widget2DBitmap = null;
							}
							widgetShortcut = new Widget2DShortcut( widgetInfo.label , widgetRegion );
							layout.addItem( widgetShortcut );
						}
					}
					if( widgetShortcut != null )
					{
						widgetShortcut.oldAppGridIndex = page;
						widgetShortcut.newAppGridIndex = page;
						if( page == page_index && inited )
						{
							widgetShortcut.oldVisible = false;
						}
						else
						{
							widgetShortcut.oldVisible = true;
						}
						widget2DMap.put( widgetInfo.textureName , widgetShortcut );
						layout.addItem( widgetShortcut );
					}
				}
				if( widgetShortcut != null )
				{
					if( mode == APPLIST_MODE_HIDE )
						widgetShortcut.showHide();
					widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetShortcut.setWidget2DShortcutShownPlace( true );
					widgetShortcut.setInfo( widgetInfo );
				}
			}
		}
	}
	
	public void syncWidget3DPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getWidget3DCount() );
		GridView3D layout = (GridView3D)view_list.get( page );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; i++ )
		{
			View3D widget = getWidget3D( i );
			if( widget instanceof Widget3DShortcut )
			{
				Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
				widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
				widgetShortcut.makeShortcut();
				if( mode == APPLIST_MODE_UNINSTALL )
					widgetShortcut.showUninstall();
				else if( mode == APPLIST_MODE_HIDE )
					widgetShortcut.showHide();
				else
					widgetShortcut.clearState();
				widgetShortcut.newAppGridIndex = page;
				if( ( widgetShortcut.newAppGridIndex != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
					widgetShortcut.oldAppGridIndex = page;
				widgetShortcut.oldX = widgetShortcut.x;
				widgetShortcut.oldY = widgetShortcut.y;
			}
			else if( widget instanceof Widget3DVirtual )
			{
				Widget3DVirtual widgetIcon = (Widget3DVirtual)widget;
				if( widgetIcon.uninstalled )
				{
					mWidget3DList.remove( widgetIcon );
					endIndex--;
					continue;
				}
				widgetIcon.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
				widgetIcon.makeShortcut();
				if( mode == APPLIST_MODE_UNINSTALL )
					widgetIcon.showUninstall();
				else if( mode == APPLIST_MODE_HIDE )
					widgetIcon.showHide();
				else
					widgetIcon.clearState();
				widgetIcon.newAppGridIndex = page;
				if( ( widgetIcon.newAppGridIndex != page_index && widgetIcon.oldAppGridIndex != page_index ) )
					widgetIcon.oldAppGridIndex = page;
				widgetIcon.oldX = widgetIcon.x;
				widgetIcon.oldY = widgetIcon.y;
			}
			layout.addItem( widget );
		}
	}
	
	public void syncWidget2DPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getWidget2DCount() );
		GridView3D layout = (GridView3D)view_list.get( page + widget3DPageCount );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; ++i )
		{
			WidgetShortcutInfo widgetInfo = getWidget2D( i );
			Widget2DShortcut widgetShortcut = widget2DMap.get( widgetInfo.textureName );
			if( widgetShortcut != null )
			{
				widgetShortcut.clearState();
				widgetShortcut.newAppGridIndex = page;
				if( ( page != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
					widgetShortcut.oldAppGridIndex = page;
				widgetShortcut.oldX = widgetShortcut.x;
				widgetShortcut.oldY = widgetShortcut.y;
				layout.addItem( widgetShortcut );
			}
			else
			{
				if( widgetInfo.isWidget == true )
				{
					widgetShortcut = new Widget2DShortcut( widgetInfo.label , R3D.findRegion( widgetInfo.textureName + "_editMode" ) );
				}
				else
				{
					widgetShortcut = new Widget2DShortcut( widgetInfo.label , R3D.findRegion( widgetInfo.textureName ) );
				}
				//				widgetShortcut = new Widget2DShortcut(widgetInfo.label,
				//						R3D.findRegion(widgetInfo.textureName));
				widgetShortcut.oldAppGridIndex = page;
				widgetShortcut.newAppGridIndex = page;
				if( page == page_index && inited )
				{
					widgetShortcut.oldVisible = false;
				}
				else
				{
					widgetShortcut.oldVisible = true;
				}
				widget2DMap.put( widgetInfo.textureName , widgetShortcut );
				layout.addItem( widgetShortcut );
			}
			if( mode == APPLIST_MODE_HIDE )
				widgetShortcut.showHide();
			widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
			widgetShortcut.setWidget2DShortcutShownPlace( true );
			widgetShortcut.setInfo( widgetInfo );
		}
	}
	
	public void syncWidgetPages()
	{
		int n = view_list.size();
		for( int i = 0 ; i < n ; i++ )
		{
			widgetGridPool.free( (GridView3D)view_list.get( 0 ) );
			this.removeView( view_list.get( 0 ) );
			view_list.remove( 0 );
		}
		syncWidgetPageCount();
		for( int i = 0 ; i < widgetPageCount ; i++ )
		{
			GridView3D grid = widgetGridPool.get();
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( i , grid );
			syncWidgetPageItems( i , true );
			grid.setAutoDrag( false );
		}
		if( widgetPageCount == 0 && !iLoongApplication.BuiltIn )
		{
			widgetPageCount = 1;
			GridView3D grid = widgetGridPool.get();
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( 0 , grid );
			grid.setAutoDrag( false );
		}
		if( page_index >= view_list.size() )
			setCurrentPage( view_list.size() - 1 );
		else
			setCurrentPage( page_index );
	}
	
	public void syncWidgetPageCount()
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		widgetPageCount = ( getWidget3DCount() + getWidget2DCount() + numCells - 1 ) / numCells;
		if( page_index >= widgetPageCount )
			page_index = widgetPageCount - 1;
	}
	
	public void syncWidget3DPageCount()
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		widget3DPageCount = ( getWidget3DCount() + numCells - 1 ) / numCells;
		if( page_index >= widget3DPageCount + widget2DPageCount )
			page_index = widget3DPageCount + widget2DPageCount - 1;
	}
	
	public void syncWidget2DPageCount()
	{
		int numCells = mWidgetCountX * mWidgetCountY;
		widget2DPageCount = ( getWidget2DCount() + numCells - 1 ) / numCells;
		if( page_index >= widget3DPageCount + widget2DPageCount )
			page_index = widget3DPageCount + widget2DPageCount - 1;
	}
	
	public void startAnimation()
	{
		Iterator widgetIter = widget2DMap.entrySet().iterator();
		while( widgetIter.hasNext() )
		{
			Map.Entry entry = (Map.Entry)widgetIter.next();
			Widget2DShortcut val = (Widget2DShortcut)entry.getValue();
			if( val.getParent() != null )
			{
				if( !val.oldVisible )
				{
					val.setScale( 0 , 0 );
					val.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
					val.oldVisible = true;
				}
				else if( val.oldAppGridIndex != val.newAppGridIndex || ( ( val.oldX != val.x || val.oldY != val.y ) && val.newAppGridIndex == page_index ) )
				{
					// Log.d("launcher",
					// "anim:"+val.name+","+val.oldAppGridIndex+","+val.newAppGridIndex);
					float oldX = ( val.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + val.oldX;
					float oldY = val.oldY;
					float newX = ( val.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + val.x;
					float newY = val.y;
					val.setPosition( oldX , oldY );
					val.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , newX , newY , 0 );
					val.oldX = newX;
					val.oldY = newY;
					val.oldAppGridIndex = val.newAppGridIndex;
				}
			}
			else
				val.oldVisible = false;
		}
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof Widget3DShortcut )
			{
				Widget3DShortcut widget = (Widget3DShortcut)view;
				if( widget.getParent() != null )
				{
					if( !widget.inited )
					{
						widget.inited = true;
						widget.oldVisible = true;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
					if( !widget.oldVisible )
					{
						widget.setScale( 0 , 0 );
						widget.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.5f , 1 , 1 , 0 );
						widget.oldVisible = true;
					}
					else if( widget.oldAppGridIndex != widget.newAppGridIndex || ( ( widget.oldX != widget.x || widget.oldY != widget.y ) && widget.newAppGridIndex == page_index ) )
					{
						float oldX = ( widget.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.oldX;
						float oldY = widget.oldY;
						float newX = ( widget.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.x;
						float newY = widget.y;
						widget.setPosition( oldX , oldY );
						widget.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.5f , newX , newY , 0 );
						widget.oldX = newX;
						widget.oldY = newY;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
				}
				else
				{
					widget.inited = true;
					widget.oldVisible = false;
				}
			}
			else if( view instanceof WidgetIcon )
			{
				WidgetIcon widget = (WidgetIcon)view;
				if( widget.getParent() != null )
				{
					if( !widget.inited )
					{
						widget.inited = true;
						widget.oldVisible = true;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
					if( !widget.oldVisible )
					{
						widget.setScale( 0 , 0 );
						widget.startTween( View3DTweenAccessor.SCALE_XY , Cubic.OUT , 0.8f , 1 , 1 , 0 );
						widget.oldVisible = true;
					}
					else if( widget.oldAppGridIndex != widget.newAppGridIndex || ( ( widget.oldX != widget.x || widget.oldY != widget.y ) && widget.newAppGridIndex == page_index ) )
					{
						float oldX = ( widget.oldAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.oldX;
						float oldY = widget.oldY;
						float newX = ( widget.newAppGridIndex - page_index ) * Utils3D.getScreenWidth() + widget.x;
						float newY = widget.y;
						widget.setPosition( oldX , oldY );
						widget.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.8f , newX , newY , 0 );
						widget.oldX = newX;
						widget.oldY = newY;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
				}
				else
				{
					widget.oldVisible = false;
					if( !widget.inited )
					{
						widget.inited = true;
						widget.oldAppGridIndex = widget.newAppGridIndex;
					}
				}
			}
		}
		needLayout = true;
	}
	
	public void setWidgets(
			ArrayList<WidgetShortcutInfo> widgets )
	{
		if( DefaultLayout.mainmenu_widget_dispale_sys_widgets )
		{
			mWidgets = (ArrayList<WidgetShortcutInfo>)widgets.clone();
		}
		else
		{
			for( WidgetShortcutInfo widgetInfo : widgets )
			{
				String packageName = widgetInfo.component.getPackageName();
				try
				{
					PackageInfo pInfo = iLoongLauncher.getInstance().getPackageManager().getPackageInfo( packageName , 0 );
					//该系统widget是安装的widget，那么显示
					if( pInfo != null && DefaultLayout.getInstance().isUserApp( pInfo ) )
					{
						boolean b_same_widget = false;
						for( WidgetShortcutInfo mWidgetInfo : mWidgets )
						{
							if( ( widgetInfo.component != null && widgetInfo.component.toString() != null ) && ( widgetInfo.component.toString().equals( mWidgetInfo.component.toString() ) && ( widgetInfo.label != null && widgetInfo.label
									.equals( mWidgetInfo.label ) ) ) )
							{
								b_same_widget = true;
								break;
							}
						}
						if( b_same_widget == false )
							mWidgets.add( widgetInfo );
					}
				}
				catch( NameNotFoundException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		syncWidgetPages();
	}
	
	public void setInited()
	{
		if( inited )
		{
			Log.d( "launcher" , "setApps:has init,return" );
			return;
		}
		setWidget3D();
		if( !iLoongApplication.BuiltIn || DefaultLayout.hide_mainmenu_widget )
		{
			syncWidgetPages();
		}
		inited = true;
	}
	
	public void setWidget3D()
	{
		mWidget3DList.clear();
		if( true/*DefaultLayout.mainmenu_widget_display_folder*/)
		{
			Folder3DShortcut folder3DHost = new Folder3DShortcut( "folder3d" );
			folder3DHost.setWidget3DShortcutShownPlace( true );
			mWidget3DList.add( folder3DHost );
		}
		if( DefaultLayout.mainmenu_widget_display_contacts )
		{
			Contact3DShortcut contact3DHost = new Contact3DShortcut( "contact3d" );
			contact3DHost.setWidget3DShortcutShownPlace( true );
			mWidget3DList.add( contact3DHost );
		}
		List<Widget3DShortcut> mWidgetList = Widget3DManager.getInstance().getWidgetList();
		// if (DefaultLayout.ui_style_miui2==false)
		{
			for( int i = 0 ; i < mWidgetList.size() ; i++ )
			{
				Widget3DShortcut view = mWidgetList.get( i );
				view.setWidget3DShortcutShownPlace( true );
				mWidget3DList.add( view );
			}
			for( int j = 0 ; j < Widget3DManager.defIcon3DList.size() ; j++ )
			{
				WidgetIcon widgetIcon_org = Widget3DManager.defIcon3DList.get( j );
				WidgetIcon widgetIcon_tmp;
				if( widgetIcon_org instanceof Widget3DVirtual )
				{
					if( !DefaultLayout.isWidgetLoadByInternal( ( ( (Widget3DVirtual)Widget3DManager.defIcon3DList.get( j ) ).packageName ) ) )
					{
						continue;
					}
					Widget3DVirtual widgetIconVirtual = new Widget3DVirtual( widgetIcon_org.name , ( (Widget3DVirtual)widgetIcon_org ).getPreviewBitmap() , widgetIcon_org.name );
					widgetIconVirtual.setItemInfo( widgetIcon_org.getItemInfo() );
					widgetIconVirtual.widget_icon_shown_workspace_edit_mode = true;
					widgetIcon_tmp = widgetIconVirtual;
					mWidget3DList.add( widgetIcon_tmp );
				}
				//				else
				//				{
				//					widgetIcon_tmp = new WidgetIcon( widgetIcon_org.name , widgetIcon_org.region );
				//					widgetIcon_tmp.setItemInfo( widgetIcon_org.getItemInfo() );
				//				}
				//				mWidget3DList.add( widgetIcon_tmp );
			}
		}
		if( iLoongApplication.BuiltIn == false )
		{
			mWidget3DList.add( Desktop3DListener.listShortcutHost );
			mWidget3DList.add( Desktop3DListener.otherToolsHost );
		}
		else
		{
			if( true/*DefaultLayout.widget_display_shortcut3D*/)
			{
				mWidget3DList.add( Desktop3DListener.listShortcutHost );
			}
		}
		// syncWidget3DPages();
	}
	
	private int getWidgetCount()
	{
		int widgetCount = 0;
		if( mode == APPLIST_MODE_HIDE )
			widgetCount = mWidget3DList.size() + mWidgets.size();
		else
		{
			for( int i = 0 ; i < mWidget3DList.size() ; i++ )
			{
				if( mWidget3DList.get( i ) instanceof Widget3DShortcut )
				{
					Widget3DShortcut shortcut = (Widget3DShortcut)mWidget3DList.get( i );
					if( !shortcut.isHideWidget )
						widgetCount++;
				}
				else if( mWidget3DList.get( i ) instanceof Widget3DVirtual )
				{
					Widget3DVirtual shortcut = (Widget3DVirtual)mWidget3DList.get( i );
					if( !shortcut.getIsHideStatus() )
						widgetCount++;
				}
			}
			for( int i = 0 ; i < mWidgets.size() ; i++ )
			{
				WidgetShortcutInfo info = mWidgets.get( i );
				if( !info.isHide )
					widgetCount++;
			}
		}
		return widgetCount;
	}
	
	private int getWidget3DCount()
	{
		int widgetCount = 0;
		if( mode == APPLIST_MODE_HIDE )
			widgetCount = mWidget3DList.size();
		else
		{
			for( int i = 0 ; i < mWidget3DList.size() ; i++ )
			{
				if( mWidget3DList.get( i ) instanceof Widget3DShortcut )
				{
					Widget3DShortcut shortcut = (Widget3DShortcut)mWidget3DList.get( i );
					if( !shortcut.isHideWidget )
						widgetCount++;
				}
				else if( mWidget3DList.get( i ) instanceof Widget3DVirtual )
				{
					Widget3DVirtual shortcut = (Widget3DVirtual)mWidget3DList.get( i );
					if( !shortcut.getIsHideStatus() )
						widgetCount++;
				}
			}
		}
		return widgetCount;
	}
	
	public void onThemeChanged()
	{
		for( int i = 0 ; i < getWidget3DCount() ; i++ )
		{
			View3D widget = getWidget3D( i );
			if( widget instanceof Widget3DVirtual && ( DefaultLayout.isWidgetLoadByInternal( ( (Widget3DVirtual)widget ).packageName ) ) )
			{
				Widget3DVirtual widget3DVirtual = (Widget3DVirtual)widget;
				widget3DVirtual.onThemeChanged();
			}
			else if( widget instanceof Widget3DShortcut )
			{
				Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
				widgetShortcut.onThemeChanged();
			}
		}
	}
	
	private View3D getWidget3D(
			int position )
	{
		if( mode == APPLIST_MODE_HIDE )
		{
			return mWidget3DList.get( position );
		}
		int n = -1;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof Widget3DShortcut )
			{
				if( !( (Widget3DShortcut)view ).isHideWidget )
					n++;
			}
			else if( view instanceof Widget3DVirtual )
			{
				if( !( (Widget3DVirtual)view ).getIsHideStatus() )
					n++;
			}
			if( n == position )
				return view;
		}
		return null;
	}
	
	private int getWidget2DCount()
	{
		int widgetCount = 0;
		if( mode == APPLIST_MODE_HIDE )
			widgetCount = mWidgets.size();
		else
		{
			for( int i = 0 ; i < mWidgets.size() ; i++ )
			{
				WidgetShortcutInfo info = mWidgets.get( i );
				if( !info.isHide )
					widgetCount++;
			}
		}
		return widgetCount;
	}
	
	private WidgetShortcutInfo getWidget2D(
			int position )
	{
		if( mode == APPLIST_MODE_HIDE )
		{
			return mWidgets.get( position );
		}
		int n = -1;
		for( int i = 0 ; i < mWidgets.size() ; i++ )
		{
			WidgetShortcutInfo info = mWidgets.get( i );
			if( !info.isHide )
				n++;
			if( n == position )
				return info;
		}
		return null;
	}
	
	private void refresh()
	{
		for( int i = 0 ; i < view_list.size() ; i++ )
		{
			GridView3D layout = (GridView3D)view_list.get( i );
			for( int j = 0 ; j < layout.getChildCount() ; j++ )
			{
				View3D child = layout.getChildAt( j );
				if( child instanceof Icon3D )
				{
					Icon3D icon = (Icon3D)child;
					if( mode == APPLIST_MODE_UNINSTALL )
						icon.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						icon.showHide();
					else
						icon.clearState();
				}
				else if( child instanceof Widget3DShortcut )
				{
					Widget3DShortcut widgetShortcut = (Widget3DShortcut)child;
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetShortcut.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetShortcut.showHide();
					else
						widgetShortcut.clearState();
				}
				else if( child instanceof Widget3DVirtual )
				{
					Widget3DVirtual widgetVirtual = (Widget3DVirtual)child;
					if( mode == APPLIST_MODE_UNINSTALL )
						widgetVirtual.showUninstall();
					else if( mode == APPLIST_MODE_HIDE )
						widgetVirtual.showHide();
					else
						widgetVirtual.clearState();
				}
				else if( child instanceof Widget2DShortcut )
				{
					Widget2DShortcut shortcut = (Widget2DShortcut)child;
					if( mode == APPLIST_MODE_HIDE )
						shortcut.showHide();
					else
						shortcut.clearState();
				}
			}
		}
	}
	
	public ArrayList<View3D> getDragObjects()
	{
		return dragObjects;
	}
	
	@Override
	public boolean onCtrlEvent(
			View3D sender ,
			int event_id )
	{
		if( sender instanceof Widget3DVirtual )
		{
			Widget3DVirtual widget = (Widget3DVirtual)sender;
			ShortcutInfo info = (ShortcutInfo)widget.getItemInfo();
			switch( event_id )
			{
				case Widget3DVirtual.MSG_WIDGET3D_SHORTCUT_LONGCLICK:
				{
					String className = DefaultLayout.getWidgetItemClassName( info.intent.getComponent().getPackageName() );
					Widget3D dragObj = Widget3DManager.getInstance().getWidget3D( info.intent.getComponent().getPackageName() , className );
					if( dragObj == null )
						return true;
					dragObj.setPosition( DragLayer3D.dragStartX - dragObj.width / 2 , DragLayer3D.dragStartY - dragObj.height / 2 );
					clearDragObjs();
					dragObjects.clear();
					dragObjects.add( dragObj );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				}
				case Widget3DShortcut.MSG_WIDGET3D_SHORTCUT_CLICK:
				{
					int index = widget.getIndexInParent();
					int position = (int)index % 4;
					this.setTag( (float)position );
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_WIDGET;
					iLoongLauncher.getInstance().add3DWidget( info.intent.getComponent().getPackageName() );
					break;
				}
			}
		}
		else if( sender instanceof Icon3D )
		{
			Icon3D icon = (Icon3D)sender;
			switch( event_id )
			{
				case Icon3D.MSG_ICON_LONGCLICK:
					if( selectedObjects.size() == 0 )
					{
						selectedObjects.add( icon );
						// dragObjects.add(icon.clone());
					}
					if( !selectedObjects.contains( icon ) )
					{
						clearDragObjs();
						selectedObjects.add( icon );
					}
					dragObjects.clear();
					for( View3D view : selectedObjects )
					{
						if( view instanceof Icon3D )
						{
							Icon3D icon3d = (Icon3D)view;
							icon3d.hideSelectedIcon();
							Icon3D iconClone = icon3d.clone();
							icon3d.toAbsoluteCoords( point );
							iconClone.x = point.x;
							iconClone.y = point.y;
							dragObjects.add( iconClone );
						}
					}
					selectedObjects.clear();
					this.setTag( icon.getTag() );
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					releaseFocus();
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Icon3D.MSG_ICON_SELECTED:
					selectedObjects.add( icon );
					// dragObjects.add(icon.clone());
					return true;
				case Icon3D.MSG_ICON_UNSELECTED:
					selectedObjects.remove( icon );
					// dragObjects.remove(icon);
					return true;
			}
		}
		else if( sender instanceof Widget2DShortcut )
		{
			Widget2DShortcut widget = (Widget2DShortcut)sender;
			switch( event_id )
			{
				case Widget2DShortcut.MSG_WIDGET_SHORTCUT_LONGCLICK:
					if( widget.canAddWidget() == false )
					{
						SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getString( RR.string.widget_cannot_add_duplicate , widget.name ) );
						return true;
					}
					clearDragObjs();
					dragObjects.clear();
					Widget2DShortcut widgetClone = widget.createDragView();
					widgetClone.toAbsoluteCoords( point );
					dragObjects.add( widgetClone );
					this.setTag( new Vector2( widgetClone.x , widgetClone.y ) );
					Vector2 v = (Vector2)widget.getTag();
					widgetClone.setPosition( v.x - widgetClone.width / 2 , v.y - widgetClone.height / 2 );
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Widget2DShortcut.MSG_WIDGET_SHORTCUT_CLICK:
					if( widget.widgetInfo != null )
					{
						WidgetShortcutInfo info = widget.widgetInfo;
						//widget.setPosition(0, 0);
						viewParent.onCtrlEvent( this , MSG_MIUIWIDGET_DECIDE_PAGE_ADDED );
						if( info.isWidget )
						{
							SendMsgToAndroid.addWidgetFromAllApp( widget.widgetInfo.component , (int)0 , (int)0 );
						}
						else if( info.isShortcut )
						{
							SendMsgToAndroid.addShortcutFromAllApp( widget.widgetInfo.component , 0 , 0 );
						}
					}
					break;
			}
		}
		else if( sender instanceof Widget3DShortcut )
		{
			Widget3DShortcut widget = (Widget3DShortcut)sender;
			switch( event_id )
			{
				case Widget3DShortcut.MSG_WIDGET3D_SHORTCUT_LONGCLICK:
					View3D dragObj = widget.getWidget3D();
					if( dragObj == null )
						return true;
					clearDragObjs();
					dragObjects.clear();
					dragObjects.add( dragObj );
					this.setTag( new Vector2( dragObj.x , dragObj.y ) );
					Vector2 v = (Vector2)widget.getTag();
					dragObj.setPosition( v.x - dragObj.width / 2 , v.y - dragObj.height / 2 );
					releaseFocus();
					if( DefaultLayout.generate_new_folder_in_top_trash_bar )
					{
						viewParent.onCtrlEvent( sender , Root3D.MSG_SET_TRASH_POS );
					}
					return viewParent.onCtrlEvent( this , DragSource3D.MSG_START_DRAG );
				case Widget3DShortcut.MSG_WIDGET3D_SHORTCUT_CLICK:
					int index = widget.getIndexInParent();
					int position = (int)index % 4;
					this.setTag( (float)position );
					if( widget instanceof Contact3DShortcut )
					{
						Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_SHORTCUT;
						viewParent.onCtrlEvent( this , MSG_MIUIWIDGET_DECIDE_PAGE_ADDED );
						iLoongLauncher.getInstance().add3DContact();
					}
					else if( widget instanceof Folder3DShortcut )
					{
						Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_SHORTCUT;
						viewParent.onCtrlEvent( this , MSG_MIUIWIDGET_DECIDE_PAGE_ADDED );
						iLoongLauncher.getInstance().add3DFolder();
					}
					else
					{
						Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_WIDGET;
						iLoongLauncher.getInstance().add3DWidget( widget.getResolveInfo() );
					}
					break;
			}
		}
		return viewParent.onCtrlEvent( sender , event_id );
	}
	
	public void addWidget(
			Widget3DShortcut widget )
	{
		// if (!iLoongApplication.BuiltIn ||
		// DefaultLayout.hide_mainmenu_widget)return;
		int widgetIconPosition = -1;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof WidgetIcon )
			{
				WidgetIcon widgetIcon = (WidgetIcon)view;
				ShortcutInfo info = (ShortcutInfo)widgetIcon.getItemInfo();
				if( info.intent.getComponent().getPackageName().equals( widget.resolve_info.activityInfo.packageName ) )
				{
					mWidget3DList.remove( view );
					widgetIconPosition = i;
					break;
				}
			}
		}
		if( widgetIconPosition != -1 )
			mWidget3DList.add( widgetIconPosition , widget );
		else
			mWidget3DList.add( widget );
		syncWidgetPages();
		// syncWidget3DPages();
	}
	
	public void removeWidget(
			String packageName )
	{
		// if (!iLoongApplication.BuiltIn ||
		// DefaultLayout.hide_mainmenu_widget)return;
		int widgetIconPosition = -1;
		Widget3DShortcut shortcut = null;
		boolean b_3d_widget = false;
		for( int i = 0 ; i < mWidget3DList.size() ; i++ )
		{
			View3D view = mWidget3DList.get( i );
			if( view instanceof Widget3DVirtual )
			{
				Widget3DVirtual widgetIcon = (Widget3DVirtual)view;
				ShortcutInfo info = (ShortcutInfo)widgetIcon.getItemInfo();
				if( info.intent.getComponent().getPackageName().equals( packageName ) )
				{
					widgetIcon.removeHide();
					mWidget3DList.remove( view );
					b_3d_widget = true;
					break;
				}
			}
			else if( view instanceof Widget3DShortcut )
			{
				Widget3DShortcut widget = (Widget3DShortcut)view;
				if( widget.resolve_info != null && widget.resolve_info.activityInfo.packageName.equals( packageName ) )
				{
					widget.removeHide();
					mWidget3DList.remove( view );
					widgetIconPosition = i;
					shortcut = widget;
					b_3d_widget = true;
					break;
				}
			}
		}
		if( b_3d_widget == false )
		{
			ArrayList<WidgetShortcutInfo> mWidgets_cpy = (ArrayList<WidgetShortcutInfo>)mWidgets.clone();
			for( WidgetShortcutInfo widgetInfo : mWidgets_cpy )
			{
				String localpackageName = widgetInfo.component.getPackageName();
				if( localpackageName != null && localpackageName.equals( packageName ) )
				{
					mWidgets.remove( widgetInfo );
				}
			}
		}
		if( true/*!DefaultLayout.disable_show_widget3d_virtral*/)
		{
			if( widgetIconPosition != -1 )
			{
				if( DefaultLayout.GetDefaultWidgetImage( packageName ) != null )
				{
					String imageName = DefaultLayout.THEME_WIDGET_APPLIST + DefaultLayout.GetDefaultWidgetImage( packageName );
					String name = DefaultLayout.GetDefaultWidgetName( packageName );
					int spanX = DefaultLayout.GetDefaultWidgetHSpan( packageName );
					int spanY = DefaultLayout.GetDefaultWidgetVSpan( packageName );
					ShortcutInfo info = new ShortcutInfo();
					info.title = name;
					info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
					info.intent.setComponent( new ComponentName( packageName , packageName ) );
					info.spanX = spanX;
					info.spanY = spanY;
					info.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
					Bitmap bmp = ThemeManager.getInstance().getBitmap( imageName );
					if( bmp != null )
					{
						Widget3DVirtual icon = new Widget3DVirtual( name , bmp , name );
						icon.setItemInfo( info );
						//icon.uninstall = shortcut.uninstall;
						//icon.hide = shortcut.hide;
						icon.setUninstallStatus( shortcut.uninstall );
						icon.setHideStatus( shortcut.hide );
						mWidget3DList.add( widgetIconPosition , icon );
					}
				}
			}
		}
		syncWidgetPages();
		// syncWidget3DPages();
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
	
	protected void clearDragObjs()
	{
		for( View3D view : selectedObjects )
		{
			( (Icon3D)view ).hideSelectedIcon();
		}
		selectedObjects.clear();
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		boolean ret = super.onClick( x , y );
		if( !ret )
			clearDragObjs();
		return ret;
	}
	
	public void show()
	{
		super.show();
		// viewParent.onCtrlEvent(this, APP_LIST3D_SHOW);
	}
	
	@Override
	public void hide()
	{
		// viewParent.onCtrlEvent(this, APP_LIST3D_HIDE);
		// clearDragObjs();
		super.hide();
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		// if (keycode == KeyEvent.KEYCODE_BACK)
		// return true;
		return super.keyDown( keycode );
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		// if (keycode == KeyEvent.KEYCODE_BACK) {
		// if (inited) {
		// viewParent.onCtrlEvent(this, APP_LIST3D_KEY_BACK);
		// }
		// return true;
		//
		// }
		return super.keyUp( keycode );
	}
	
	public int estimateWidgetCellWidth(
			int cellHSpan )
	{
		if( cellHSpan > 4 )
			cellHSpan = 4;
		float widgetWidth = (float)( width * Root3D.scaleFactor ) / ( (float)mWidgetCountX );
		if( widgetWidth < R3D.workspace_editmode_optimum_widget_icon_size )
		{
			widgetWidth = R3D.workspace_editmode_optimum_widget_icon_size;
		}
		float widgetCellWidth = widgetWidth / ( (float)4 );
		return (int)( widgetCellWidth * ( (float)cellHSpan ) );
	}
	
	public int estimateWidgetCellHeight(
			int cellVSpan )
	{
		if( cellVSpan > 4 )
			cellVSpan = 4;
		float widgetHeight = ( (float)( height - getSingleLineHeight() - R3D.miui_widget_indicator_height ) / ( (float)mWidgetCountY ) - R3D.app_widget3d_gap );
		if( widgetHeight < R3D.workspace_editmode_optimum_widget_icon_size )
		{
			widgetHeight = R3D.workspace_editmode_optimum_widget_icon_size;
		}
		float widgetCellHeight = widgetHeight / ( (float)4 );
		return (int)( widgetCellHeight * ( (float)cellVSpan ) );
	}
	
	public View3D getFirstIcon()
	{
		if( view_list.size() < 1 )
			return null;
		ViewGroup3D vg = (ViewGroup3D)view_list.get( 0 );
		if( vg.getChildCount() < 1 )
			return null;
		if( vg.getChildAt( 0 ) == null || !( vg.getChildAt( 0 ) instanceof Icon3D ) )
			return null;
		return vg.getChildAt( 0 );
	}
	
	public int getIconGap()
	{
		if( view_list.size() < 1 )
			return -1;
		ViewGroup3D vg = (ViewGroup3D)view_list.get( 0 );
		if( vg.getChildCount() < 2 )
			return -1;
		if( vg.getChildAt( 0 ) == null || !( vg.getChildAt( 0 ) instanceof Icon3D ) )
			return -1;
		if( vg.getChildAt( 1 ) == null || !( vg.getChildAt( 1 ) instanceof Icon3D ) )
			return -1;
		return (int)( vg.getChildAt( 1 ).x - vg.getChildAt( 0 ).x );
	}
	
	@Override
	public boolean fling(
			float velocityX ,
			float velocityY )
	{
		// TODO Auto-generated method stub
		if( widgetPageCount == 1 )
			return true;
		mType = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		APageEase.setStandard( true );
		return super.fling( velocityX , velocityY );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// Log.v("AppList3D", "scroll: x:" + x + " y:" + y + " deltaX:" + deltaX
		// + " deltaY:" + deltaY + " xScale:" + xScale);
		if( widgetPageCount == 1 )
			return true;
		mType = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		APageEase.setStandard( true );
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	class GridPool
	{
		
		private ArrayList<GridView3D> grids;
		private float width;
		private float height;
		private int countX;
		private int countY;
		
		public GridPool(
				int initCapacity ,
				float width ,
				float height ,
				int countX ,
				int countY )
		{
			grids = new ArrayList<GridView3D>( initCapacity );
			this.width = width;
			this.height = height;
			this.countX = countX;
			this.countY = countY;
		}
		
		private GridView3D create()
		{
			int padding = (int)( width * ( 1 - Root3D.scaleFactor ) / 2f );
			GridView3D grid = new GridView3D( "miuiWidgetList" , width , height , countX , countY );
			grid.setPadding( padding , padding , 0 , 0 );
			return grid;
		}
		
		public GridView3D get()
		{
			GridView3D grid = grids.isEmpty() ? create() : grids.remove( grids.size() - 1 );
			return grid;
		}
		
		public void free(
				GridView3D grid )
		{
			if( !grids.contains( grid ) )
			{
				grid.removeAllViews();
				grids.add( grid );
			}
		}
	}
	
	@Override
	public boolean multiTouch2(
			Vector2 initialFirstPointer ,
			Vector2 initialSecondPointer ,
			Vector2 firstPointer ,
			Vector2 secondPointer )
	{
		return false;
	}
	
	public void onPause()
	{
		Iterator<View3D> ite = view_list.iterator();
		while( ite.hasNext() )
		{
			GridView3D grid = (GridView3D)ite.next();
			grid.releaseRegion();
			widgetGridPool.free( grid );
			this.removeView( grid );
			ite.remove();
			grid = null;
		}
		if( widget2DMap != null )
		{
			widget2DMap.clear();
		}
	}
}
