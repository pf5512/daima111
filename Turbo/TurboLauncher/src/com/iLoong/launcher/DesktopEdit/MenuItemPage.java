package com.iLoong.launcher.DesktopEdit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import aurelienribon.tweenengine.Timeline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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


public abstract class MenuItemPage extends NPageBase implements DragSource3D
{
	
	public boolean isLongClick = false;
	public static HashMap<String , Widget2DShortcut> widget2DMap;
	List<Widget3DShortcut> mWidgetList;
	private int pageCapacity = 1;
	private int mCellCountX = 4 , mCellCountY = 1;
	private GridPool gridPool = null;
	public static int pageCount = 1;
	public static int widgetPageCount = 0;
	public int totalPageCount = 0;
	public ArrayList<View3D> buttonItems;
	private ArrayList<View3D> dragObjects = new ArrayList<View3D>();
	private ArrayList<View3D> selectedObjects = new ArrayList<View3D>();
	public static ArrayList<View3D> mWidget3DList;
	public ArrayList<View3D> currList;
	public static ArrayList<WidgetShortcutInfo> mWidgets = new ArrayList<WidgetShortcutInfo>();
	private NinePatch right_top = null;
	private NinePatch left_top = null;
	private NinePatch right_bottom = null;
	private NinePatch left_bottom = null;
	public Timeline indicatorLine;
	public boolean disposable = false;
	public boolean loadinited = false;
	public int marginTop;
	public boolean showProgressBar = false;
	public static boolean widgetCreateDone = false;
	public static Object lock = new Object();
	public int max_widget_count = 50;
	
	public MenuItemPage(
			String name )
	{
		super( name );
		needXRotation = false;
		setWholePageList();
		setEffectType( APageEase.COOLTOUCH_EFFECT_DEFAULT );
		if( widget2DMap == null )
			widget2DMap = new HashMap<String , Widget2DShortcut>();
		x = 0;
		y = 0;
		transform = true;
		pageCount = 0;
		generateBg();
		indicatorView = new IndicatorView( "LongClickIndicator" , IndicatorView.INDICATOR_STYLE_COMET );
		indicatorView.setPosition( 0 , 0 );
		indicatorView.setSize( this.getWidth() , R3D.pop_menu_indicator_height );
	}
	
	public void generateBg()
	{
		Bitmap rttmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/right_top.png" );
		right_top = new NinePatch( new TextureRegion( new BitmapTexture( rttmp ) ) , 1 , 1 , 1 , 1 );
		rttmp.recycle();
		Bitmap rbtmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/right_bottom.png" );
		right_bottom = new NinePatch( new TextureRegion( new BitmapTexture( rbtmp ) ) , 1 , 1 , 1 , 1 );
		rbtmp.recycle();
		Bitmap lttmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/left_top.png" );
		left_top = new NinePatch( new TextureRegion( new BitmapTexture( lttmp ) ) , 1 , 1 , 1 , 1 );
		lttmp.recycle();
		Bitmap lbtmp = ThemeManager.getInstance().getBitmap( "theme/desktopEdit/left_bottom.png" );
		left_bottom = new NinePatch( new TextureRegion( new BitmapTexture( lbtmp ) ) , 1 , 1 , 1 , 1 );
		lbtmp.recycle();
	}
	
	public void setProgressMargin(
			int marginTop )
	{
		this.marginTop = marginTop;
	}
	
	public void setButtons(
			ArrayList<? extends View3D> items ,
			int countX ,
			int countY ,
			float width ,
			float height )
	{
		mCellCountX = countX;
		mCellCountY = countY;
		this.buttonItems = (ArrayList<View3D>)items;
		this.currList = buttonItems;
		this.width = width; //Utils3D.getScreenWidth();
		this.height = height;//R3D.pop_menu_height-R3D.pop_menu_title_height;
		gridPool = new GridPool(
				name ,
				pageCapacity ,
				width ,
				height ,
				mCellCountX ,
				mCellCountY ,
				R3D.applist_padding_left ,
				R3D.applist_padding_left ,
				R3D.pop_menu_page_grid_padding_top ,
				R3D.pop_menu_page_grid_padding_bottom );
		syncButtonPages();
	}
	
	public void setWidgets(
			ArrayList<View3D> items ,
			int countX ,
			int countY ,
			float width ,
			float height )
	{
		mCellCountX = countX;
		mCellCountY = countY;
		mWidget3DList = new ArrayList<View3D>();
		currList = new ArrayList<View3D>();
		widget2DMap.clear();
		gridPool = new GridPool( name , pageCapacity , width , height , mCellCountX , mCellCountY , 0 , 0 , 0 , 0 );
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
				setWidget3D();
				if( iLoongApplication.BuiltIn )
				{
					loadWidgetAndShortcut();
				}
				else
				{
					widgetCreateDone = true;
				}
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						SendMsgToAndroid.cancelCustomDialog();
						syncWidget3D();
					}
				} );
			}
		} ).start();
	}
	
	public static void bindWidget2D()
	{
		mWidgets = new ArrayList<WidgetShortcutInfo>();
	}
	
	public synchronized void syncButtonPages()
	{
		Iterator<View3D> ite = view_list.iterator();
		while( ite.hasNext() )
		{
			GridView3D grid = (GridView3D)ite.next();
			if( grid.name.equals( name ) )
			{
				gridPool.free( grid );
				this.removeView( grid );
				ite.remove();
			}
		}
		syncAppPageCount();
		for( int i = 0 ; i < pageCount ; i++ )
		{
			GridView3D grid = gridPool.get( mCellCountX , mCellCountY );
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( i , grid );
			syncButtonPageItems( i , true );
			grid.setAutoDrag( false );
		}
	}
	
	public void setWidget3D()
	{
		mWidget3DList.clear();
		Intent intent = new Intent( "com.iLoong.widget" , null );
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		mWidgetList = new ArrayList<Widget3DShortcut>();
		if( mWidgetResolveInfoList != null && mWidgetResolveInfoList.size() > 0 )
		{
			for( ResolveInfo i : mWidgetResolveInfoList )
			{
				mWidgetList.add( new Widget3DShortcut( "Widget3DShortcut" , i ) );
			}
		}
		if( DefaultLayout.mainmenu_widget_display_contacts )
		{
			Contact3DShortcut contact3DHost = new Contact3DShortcut( "contact3d" );
			contact3DHost.setWidget3DShortcutShownPlace( true );
			mWidget3DList.add( contact3DHost );
		}
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
		}
		if( iLoongApplication.BuiltIn == false )
		{
			mWidget3DList.add( Desktop3DListener.otherToolsHost );
		}
	}
	
	@SuppressLint( "NewApi" )
	public void loadWidgetAndShortcut()
	{
		widgetCreateDone = false;
		final Desktop3DListener d3d = iLoongLauncher.getInstance().getD3dListener();
		final iLoongLauncher launcher = iLoongLauncher.getInstance();
		final ArrayList<Object> allWidgets = new ArrayList<Object>();
		mWidgets.clear();
		Context mContext = iLoongLauncher.getInstance();
		final PackageManager packageManager = mContext.getPackageManager();
		List<AppWidgetProviderInfo> widgets = AppWidgetManager.getInstance( mContext ).getInstalledProviders();
		Intent shortcutsIntent = new Intent( Intent.ACTION_CREATE_SHORTCUT );
		List<ResolveInfo> shortcuts = packageManager.queryIntentActivities( shortcutsIntent , 0 );
		for( AppWidgetProviderInfo widget : widgets )
		{
			if( widget.minWidth > 0 && widget.minHeight > 0 )
			{
				allWidgets.add( widget );
			}
		}
		allWidgets.addAll( shortcuts );
		//		new Thread( new Runnable() {
		//			
		//			@SuppressLint( "NewApi" )
		//			@Override
		//			public void run()
		{
			synchronized( lock )
			{
				mWidgets.clear();
				max_widget_count = allWidgets.size() > max_widget_count ? max_widget_count : allWidgets.size();
				for( int i = 0 ; i < max_widget_count ; i++ )
				{
					WidgetShortcutInfo widgetInfo = new WidgetShortcutInfo();
					Object rawInfo = allWidgets.get( i );
					Bitmap b = null;
					String name = "";
					String label = "";
					if( rawInfo instanceof AppWidgetProviderInfo )
					{
						AppWidgetProviderInfo info = (AppWidgetProviderInfo)rawInfo;
						Log.v( "jbc" , "sysWidgetName=" + info.label + " packageName=" + info.provider.getPackageName() + " providerName=" + info.provider.getClassName() );
						int[] cellSpans = launcher.getSpanForWidget( info , null );
						/* 在小组件界面，屏蔽spanX或spanY大于4的小组件 */
						if( cellSpans[0] > 4 || cellSpans[1] > 4 )
						{
							continue;
						}
						boolean should_hide = false;
						for( int j = 0 ; j < DefaultLayout.hideWidgetList.size() ; j++ )
						{
							String compName = DefaultLayout.hideWidgetList.get( j );
							if( info.provider.toString().contains( compName ) )
							{
								should_hide = true;
								break;
							}
						}
						if( should_hide )
						{
							continue;
						}
						int previewImage = 0;
						int sysVersion = VERSION.SDK_INT;
						if( sysVersion >= 11 )
							previewImage = info.previewImage;
						name = info.provider.toString();
						//	if( !d3d.widget2DpkgName.contains( name ) )
						{
							b = d3d.getWidgetPreview( info.provider , previewImage , info.icon , cellSpans[0] , cellSpans[1] , -1 , -1 );
							//		d3d.widget2DpkgName.add( name );
						}
						label = info.label;
						widgetInfo.cellHSpan = cellSpans[0];
						widgetInfo.cellVSpan = cellSpans[1];
						widgetInfo.label = label;
						widgetInfo.component = info.provider;
						widgetInfo.isWidget = true;
						Bitmap widgetHost_bmp = d3d.getWidgetPreviewWorkspaceEditMode( info.provider , previewImage , info.icon , cellSpans[0] , cellSpans[1] , -1 , -1 );
						if( widgetHost_bmp != null )
						{
							widgetInfo.widgetHostBitmap = widgetHost_bmp;
						}
					}
					else if( rawInfo instanceof ResolveInfo )
					{
						ResolveInfo info = (ResolveInfo)rawInfo;
						name = info.activityInfo.name;
						//	if( !d3d.widget2DpkgName.contains( name ) )
						{
							b = d3d.getShortcutPreview( info );
							//		d3d.widget2DpkgName.add( name );
						}
						label = d3d.iconCache.getLabel( info );
						widgetInfo.label = label;
						widgetInfo.component = new ComponentName( info.activityInfo.packageName , info.activityInfo.name );
						widgetInfo.isShortcut = true;
					}
					if( b != null )
					{
						widgetInfo.widget2DBitmap = b;
						//d3d.allwidget2Dbitmap.add( b );
					}
					widgetInfo.textureName = name;
					mWidgets.add( widgetInfo );
				}
				widgetCreateDone = true;
				lock.notify();
			}
		}
		//		} ).start();
		allWidgets.clear();
	}
	
	public void syncWidgetPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mCellCountX * mCellCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getAllWidgetCount() );
		GridView3D layout = (GridView3D)view_list.get( page );
		layout.removeAllViews();
		NinePatch bg = null;
		//generateBg();
		for( int i = startIndex ; i < endIndex ; i++ )
		{
			if( i % ( mCellCountX * mCellCountY ) == 0 )
			{
				bg = left_top;
			}
			else if( i % ( mCellCountX * mCellCountY ) == 1 )
			{
				bg = right_top;
			}
			else if( i % ( mCellCountX * mCellCountY ) == 2 )
			{
				bg = left_bottom;
			}
			else if( i % ( mCellCountX * mCellCountY ) == 3 )
			{
				bg = right_top;
			}
			else
			{
				bg = left_top;
			}
			if( i < getWidget3DCount() )
			{
				View3D widget = getWidgetItem( i );
				if( widget instanceof Widget3DShortcut )
				{
					Widget3DShortcut widgetShortcut = (Widget3DShortcut)widget;
					widgetShortcut.setSize( layout.getCellWidth() , layout.getCellHeight() );
					if( bg != null )
						widgetShortcut.setBackgroud( bg );
					//widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetShortcut.setWidget3DShortcutShownPlace( true );
					widgetShortcut.makeShortcut();
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
					if( bg != null )
						widgetIcon.setBackgroud( bg );
					widgetIcon.setSize( layout.getCellWidth() , layout.getCellHeight() );
					//widgetIcon.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetIcon.makeShortcut();
					widgetIcon.clearState();
					widgetIcon.newAppGridIndex = page;
					if( ( widgetIcon.newAppGridIndex != page_index && widgetIcon.oldAppGridIndex != page_index ) )
						widgetIcon.oldAppGridIndex = page;
					widgetIcon.oldX = widgetIcon.x;
					widgetIcon.oldY = widgetIcon.y;
				}
				layout.addItem( widget );
				if( !currList.contains( widget ) )
					currList.add( widget );
			}
			else
			{
				WidgetShortcutInfo widgetInfo = getWidget2D( i - getWidget3DCount() );
				if( widgetInfo == null )
					continue;
				Widget2DShortcut widgetShortcut = null;//= widget2DMap.get( widgetInfo.textureName );
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
								Log.v( "fuck" , "widgetHostBitmap is recycle label is " + widgetInfo.label );
								widgetInfo.widgetHostBitmap = null;
							}
							widgetShortcut = new Widget2DShortcut( widgetInfo.label , widgetRegion );
							if( bg != null )
								widgetShortcut.setBackgroud( bg );
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
								Log.v( "fuck" , "widget2DBitmapResolveInfo is recycle label is " + widgetInfo.label );
								widgetInfo.widget2DBitmap = null;
							}
							widgetShortcut = new Widget2DShortcut( widgetInfo.label , widgetRegion );
							if( bg != null )
								widgetShortcut.setBackgroud( bg );
							layout.addItem( widgetShortcut );
							//							
						}
					}
					if( widgetShortcut != null )
					{
						widgetShortcut.oldAppGridIndex = page;
						widgetShortcut.newAppGridIndex = page;
						{
							widgetShortcut.oldVisible = true;
						}
						layout.addItem( widgetShortcut );
					}
				}
				if( widgetShortcut != null )
				{
					widgetShortcut.setSize( layout.getCellWidth() , layout.getCellHeight() );
					//widgetShortcut.setSize( layout.getCellWidth() - R3D.app_widget3d_gap , layout.getCellHeight() - R3D.app_widget3d_gap );
					widgetShortcut.setWidget2DShortcutShownPlace( true );
					widgetShortcut.setInfo( widgetInfo );
					if( !currList.contains( widgetShortcut ) )
						currList.add( widgetShortcut );
				}
			}
		}
	}
	
	private WidgetShortcutInfo getWidget2D(
			int position )
	{
		return mWidgets.get( position );
	}
	
	public synchronized void syncWidget3D()
	{
		synchronized( lock )
		{
			while( !widgetCreateDone )
			{
				try
				{
					lock.wait();
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		}
		Iterator<View3D> ite = view_list.iterator();
		while( ite.hasNext() )
		{
			GridView3D grid = (GridView3D)ite.next();
			if( grid.name.equals( name ) )
			{
				gridPool.free( grid );
				this.removeView( grid );
				ite.remove();
			}
		}
		syncWidgetPageCount();
		for( int i = 0 ; i < widgetPageCount ; i++ )
		{
			GridView3D grid = gridPool.get( mCellCountX , mCellCountY );
			grid.enableAnimation( false );
			grid.transform = true;
			addPage( i , grid );
			syncWidgetPageItems( i , true );
			grid.setAutoDrag( false );
		}
		DesktopEditMenuItem.widgetInited = true;
	}
	
	private View3D getWidgetItem(
			int position )
	{
		return (View3D)( mWidget3DList.get( position ) );
	}
	
	public void syncWidgetPageCount()
	{
		int numCells = mCellCountX * mCellCountY;
		totalPageCount = widgetPageCount = ( getWidget3DCount() + getWidget2DCount() + numCells - 1 ) / numCells;
	}
	
	private int getWidget3DCount()
	{
		return mWidget3DList.size();
	}
	
	private int getWidget2DCount()
	{
		return mWidgets.size();
	}
	
	private int getAllWidgetCount()
	{
		return getWidget2DCount() + getWidget3DCount();
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
						//	viewParent.onCtrlEvent( this , MSG_MIUIWIDGET_DECIDE_PAGE_ADDED );
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
						//viewParent.onCtrlEvent( this , MSG_MIUIWIDGET_DECIDE_PAGE_ADDED );
						iLoongLauncher.getInstance().add3DContact();
					}
					else if( widget instanceof Folder3DShortcut )
					{
						Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_SHORTCUT;
						//viewParent.onCtrlEvent( this , MSG_MIUIWIDGET_DECIDE_PAGE_ADDED );
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
	
	protected void clearDragObjs()
	{
		for( View3D view : selectedObjects )
		{
			( (Icon3D)view ).hideSelectedIcon();
		}
		selectedObjects.clear();
	}
	
	public void syncButtonPageItems(
			int page ,
			boolean immediate )
	{
		int numCells = mCellCountX * mCellCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min( startIndex + numCells , getItemCount() );
		GridView3D layout = (GridView3D)view_list.get( page );
		layout.removeAllViews();
		for( int i = startIndex ; i < endIndex ; ++i )
		{
			View3D view = getIconItem( i );
			if( view instanceof Widget3DShortcut )
			{
				Widget3DShortcut widgetShortcut = (Widget3DShortcut)view;
				widgetShortcut.setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
				widgetShortcut.setWidget3DShortcutShownPlace( true );
				widgetShortcut.makeShortcut();
				widgetShortcut.clearState();
				widgetShortcut.newAppGridIndex = page;
				if( ( widgetShortcut.newAppGridIndex != page_index && widgetShortcut.oldAppGridIndex != page_index ) )
					widgetShortcut.oldAppGridIndex = page;
				widgetShortcut.oldX = widgetShortcut.x;
				widgetShortcut.oldY = widgetShortcut.y;
			}
			else if( view instanceof Icon3D )
			{
				//view.setSize( DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
			}
			layout.addItem( view );
		}
	}
	
	private View3D getIconItem(
			int position )
	{
		return (View3D)( buttonItems.get( position ) );
	}
	
	public void syncAppPageCount()
	{
		int numCells = mCellCountX * mCellCountY;
		totalPageCount = pageCount = ( getItemCount() + numCells - 1 ) / numCells;
	}
	
	public int getItemCount()
	{
		return buttonItems.size();
	}
	
	class GridPool
	{
		
		private ArrayList<GridView3D> grids;
		private float width;
		private float height;
		private int countX;
		private int countY;
		private String name;
		private int padding_left;
		private int padding_right;
		private int padding_top;
		private int padding_bottom;
		
		public GridPool(
				String name ,
				int initCapacity ,
				float width ,
				float height ,
				int countX ,
				int countY ,
				int padding_left ,
				int padding_right ,
				int padding_top ,
				int padding_bottom )
		{
			this.name = name;
			grids = new ArrayList<GridView3D>( initCapacity );
			this.width = width;
			this.height = height;
			this.countX = countX;
			this.countY = countY;
			this.padding_left = padding_left;
			this.padding_right = padding_right;
			this.padding_top = padding_top;
			this.padding_bottom = padding_bottom;
		}
		
		private GridView3D create()
		{
			GridView3D grid = new GridView3D( name , width , height , countX , countY );
			grid.setPadding( padding_left , padding_right , padding_top , padding_bottom );
			Log.v( "Debug" , "padding_left , padding_right ,padding_top ,padding_bottom:" + padding_left + padding_right + padding_top + padding_bottom );
			return grid;
		}
		
		public GridView3D get(
				int countX ,
				int countY )
		{
			this.countX = countX;
			this.countY = countY;
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
		
		public void clear()
		{
			for( int i = 0 ; i < grids.size() ; i++ )
			{
				for( int j = 0 ; j < grids.get( i ).getChildCount() ; j++ )
				{
					grids.get( i ).getChildAt( j ).disposeTexture();
				}
			}
		}
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		isLongClick = true;
		return super.onLongClick( x , y );
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
		return dragObjects;
	}
	
	public static void addWidget(
			Widget3DShortcut widget )
	{
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
		//syncWidget3D();
	}
	
	public void loadPage()
	{
		if( !loadinited )
		{
			if( showProgressBar )
				SendMsgToAndroid.showCustomDialog( (int)( Utils3D.getScreenWidth() - 40 * iLoongLauncher.getInstance().getResources().getDisplayMetrics().density ) / 2 , marginTop );
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					loadinited = true;
					pageInitialize();
					show();
					if( showProgressBar )
						SendMsgToAndroid.cancelCustomDialog();
				}
			} );
		}
		else
		{
			show();
		}
	}
	
	public void loadWallpaperPage()
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				loadinited = true;
				pageInitialize();
				show();
				DesktopEditMenu.ifCanClick=true;
			}
		} );
	}
	
	public void unloadPage()
	{
		this.hide();
		if( disposable )
		{
			if( loadinited == false )
				return;
			loadinited = false;
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					pageFinalize();
					view_list.clear();
					clear();
					gridPool.clear();
					if( mWidgets != null )
						mWidgets.clear();
				}
			} );
		}
	}
	
	public void setDisposable(
			boolean disposable )
	{
		this.disposable = disposable;
	}
	
	public void setShowprogress(
			boolean sp )
	{
		this.showProgressBar = sp;
	}
	
	public abstract boolean pageInitialize();
	
	public abstract boolean pageFinalize();
	
	public ArrayList<View3D> getCurrList()
	{
		return currList;
	}
}
