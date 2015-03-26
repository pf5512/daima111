package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Functions.Tab.ITabTitlePlugin;
import com.iLoong.launcher.Functions.Tab.Plugin;
import com.iLoong.launcher.Functions.Tab.TabContext;
import com.iLoong.launcher.Functions.Tab.TabPluginManager;
import com.iLoong.launcher.Functions.Tab.TabPluginMetaData;
import com.iLoong.launcher.Functions.Tab.TabTitle3D;
import com.iLoong.launcher.Functions.Tab.TabTitlePlugin3D;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class AppBar3D extends ViewGroup3D implements PageScrollListener , TweenCallback
{
	
	public AppTab appTab;
	public AppTabIndicator tabIndicator;
	private AppMenuButton menu;
	// private AppContentButton content;
	private AppHomeButton home;
	public AppPopMenu popMenu;
	public AppPopMenu2 popMenu2;
	private OnTabChangeListener listener;
	private AppList3D appList;
	private TextureRegion indicatorBgRegion;
	private NinePatch indicatorBg;
	public static TextureRegion tr_appbarBg = null;
	public static TextureRegion tr_appItemBg = null;
	public NinePatch rm_popup_bg = new NinePatch( R3D.findRegion( "appbar-content-pop-bg" ) , 6 , 6 , 6 , 6 );
	// public static TextureRegion itemBg;
	public static final int TAB_CONTENT = 0;
	public static final int TAB_WIDGET = 1;
	public static final int TAB_PLUGIN = 2;
	public static final int TAB_MORE = 3;
	public static final int TEXT_ALIGN_LEFT = 0;
	public static final int TEXT_ALIGN_CENTER = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	public static int prePopItem = 0;
	public static int popItem = 3;
	public AppHost3D appHost;
	public TabPluginManager tabPluginManager;
	public PluginTab pluginTab;
	public NinePatch tab_pop_bg;
	public NinePatch tab_pop_item_select_bg;
	private AppBarMoreButton moreButton;
	public TabPopMenu tabPopMenu;
	private TabTitle3D lastSelectedTabTitle = null;
	private String lastSelectPluginId = null;
	public static NinePatch appbarTabSelectBgNinePatch = null;
	public static NinePatch appbarTabDividerNinePatch = null;
	
	public AppBar3D(
			String name )
	{
		super( name );
		x = 0;
		//		y = Utils3D.getScreenHeight() - R3D.appbar_height;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = R3D.appbar_height;
		this.originY = height / 2;
		tab_pop_bg = new NinePatch( R3D.findRegion( "appbar-tab-pop-bg" ) , 1 , 1 , 1 , 7 );
		tab_pop_item_select_bg = new NinePatch( R3D.findRegion( "appbar-tab-pop-item-select-bg" ) );
		indicatorBg = new NinePatch( R3D.findRegion( "appbar-indicator" ) );
		tr_appbarBg = R3D.findRegion( "appbar-bg" );
		tr_appItemBg = R3D.findRegion( "app-item-bg" );
		transform = true;
		build();
	}
	
	private float measureTitleWidth(
			String title )
	{
		Paint paint = new Paint();
		paint.setColor( R3D.appbar_tab_color );
		paint.setAntiAlias( true );
		if( DefaultLayout.title_style_bold )
			paint.setFakeBoldText( true );
		paint.setTextSize( R3D.appbar_height / 3f );
		int titleWidth = (int)( paint.measureText( title ) );
		return titleWidth;
	}
	
	private float getMeasureContentTitleWidth()
	{
		return measureTitleWidth( R3D.appbar_tab_app );
	}
	
	private float getMeasureWidgetTitleWidth()
	{
		return measureTitleWidth( R3D.appbar_tab_widget );
	}
	
	private float getContentTitleWidth()
	{
		float tabContentTitleWidth = measureTitleWidth( R3D.appbar_tab_app );
		tabContentTitleWidth = tabContentTitleWidth + 2 * R3D.appbar_tab_padding + R3D.appbar_padding_left;
		return tabContentTitleWidth;
	}
	
	private float getWidgetTitleWidth()
	{
		float tabWidgetTitleWidth = getMeasureWidgetTitleWidth();
		tabWidgetTitleWidth = tabWidgetTitleWidth + 2 * R3D.appbar_tab_padding;
		return tabWidgetTitleWidth;
	}
	
	public boolean showMoreButton = false;
	public int showPluginTabCount;
	public int lastTabPadding = R3D.appbar_tab_padding;
	
	private int calcTabPadding(
			ArrayList<Plugin> plugins )
	{
		int result = R3D.appbar_tab_padding;
		float maxPluginWidth = Utils3D.getScreenWidth();
		if( DefaultLayout.show_home_button )
		{
			maxPluginWidth -= ( R3D.appbar_menu_width + R3D.appbar_menu_right * 2 );
		}
		float minPluginWidth = maxPluginWidth - R3D.appbar_more_width - R3D.appbar_menu_right;
		float tabContentTitleWidth = getContentTitleWidth();
		float tabWidgthTitleWidth = getWidgetTitleWidth();
		float dividerWidth = R3D.findRegion( "appbar-divider" ).getRegionWidth();
		int measureCount = 0;
		float measureWidth = 0;
		float totalWidth = 0;
		totalWidth = tabContentTitleWidth + dividerWidth + tabWidgthTitleWidth;
		if( plugins.size() > 0 )
		{
			for( int i = 0 ; i < plugins.size() ; i++ )
			{
				measureWidth += measureTitleWidth( plugins.get( i ).getTabPluginMetaData().pluginTitle ) + R3D.appbar_tab_padding * 2;
				totalWidth += dividerWidth + measureWidth;
				if( totalWidth > maxPluginWidth )
				{
					break;
				}
				measureCount++;
			}
			if( measureCount == plugins.size() )
			{
				//可以放下
				showMoreButton = false;
			}
			else
			{
				showMoreButton = true;
				if( measureCount > 1 )
				{
					measureCount = 0;
					measureWidth = 0;
					totalWidth = tabContentTitleWidth + dividerWidth + tabWidgthTitleWidth;
					for( int i = 0 ; i < plugins.size() ; i++ )
					{
						measureWidth += measureTitleWidth( plugins.get( i ).getTabPluginMetaData().pluginTitle ) + R3D.appbar_tab_padding * 2;
						totalWidth = totalWidth + dividerWidth + measureWidth;
						if( totalWidth > minPluginWidth )
						{
							break;
						}
						measureCount++;
					}
				}
			}
			if( measureCount == 0 )
			{
				measureCount = 1;
			}
			float titleFontWidth = 0;
			titleFontWidth += getMeasureContentTitleWidth();
			titleFontWidth += getMeasureWidgetTitleWidth();
			titleFontWidth += R3D.appbar_padding_left;
			titleFontWidth += dividerWidth + dividerWidth * measureCount;
			if( measureCount == 1 )
			{
				titleFontWidth += measureTitleWidth( plugins.get( 0 ).getTabPluginMetaData().pluginTitle );
			}
			else
			{
				for( int i = 0 ; i < measureCount ; i++ )
				{
					titleFontWidth += measureTitleWidth( plugins.get( i ).getTabPluginMetaData().pluginTitle );
				}
			}
			if( measureCount == plugins.size() )
			{
				float tmpWidth = (int)( maxPluginWidth - titleFontWidth );
				R3D.appbar_tab_padding = (int)( tmpWidth / ( ( 2 + measureCount ) * 2 ) );
				//return (int)(tmpWidth/((2+measureCount)*2));
			}
			else
			{
				float tmpWidth = (int)( minPluginWidth - titleFontWidth );
				//return (int)(tmpWidth/((2+measureCount)*2));
				R3D.appbar_tab_padding = (int)( tmpWidth / ( ( 2 + measureCount ) * 2 ) );
			}
		}
		else
		{
			showMoreButton = false;
			measureCount = 0;
			R3D.appbar_tab_padding = R3D.getInteger( "appbar_tab_padding" );
		}
		showPluginTabCount = measureCount;
		return result;
	}
	
	public float getConfigTabContentWidth(
			float defaultTitleWidth )
	{
		float res = Math.max( defaultTitleWidth , R3D.findRegion( "appbar-tab-navigator-audioalbum" ).getRegionWidth() );
		res = Math.max( R3D.findRegion( "appbar-tab-navigator-photobucket" ).getRegionWidth() , res );
		res = Math.max( R3D.findRegion( "appbar-tab-navigator-video" ).getRegionWidth() , res );
		return res;
	}
	
	public void build()
	{
		if( DefaultLayout.enable_tab_plugin )
		{
			tabPluginManager = TabPluginManager.getInstance();
			calcTabPadding( tabPluginManager.getPluginList() );
		}
		appTab = new AppTab( "apptab" );
		this.addView( appTab );
		tabIndicator = new AppTabIndicator( "apptabindicator" );
		this.addView( tabIndicator );
		if( DefaultLayout.show_home_button )
		{
			DeleteButton deleteButton = new DeleteButton( "deleteButton" );
			this.addView( deleteButton );
			HomeButton homeButton = new HomeButton( "homeButton" );
			this.addView( homeButton );
			home = new AppHomeButton( "apphomebutton" );
			this.addView( home );
		}
	}
	
	public void buildPluginTab()
	{
		if( DefaultLayout.enable_tab_plugin )
		{
			tabPluginManager = TabPluginManager.getInstance();
			moreButton = new AppBarMoreButton( "more" );
			moreButton.hide();
			this.addView( moreButton );
			pluginTab = new PluginTab( "TitlePlugin" );
			this.addView( pluginTab );
		}
	}
	
	public void onThemeChanged()
	{
		//		y = Utils3D.getScreenHeight() - R3D.appbar_height;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = R3D.appbar_height;
		this.originY = height / 2;
		tr_appbarBg = R3D.findRegion( "appbar-bg" );
		tr_appItemBg = R3D.findRegion( "app-item-bg" );
		indicatorBg = new NinePatch( R3D.findRegion( "appbar-indicator" ) );
		appbarTabDividerNinePatch = new NinePatch( R3D.findRegion( "appbar-tab-divider" ) );
		appbarTabSelectBgNinePatch = new NinePatch( R3D.findRegion( "appbar-tab-select-bg" ) , 0 , 0 , 0 , 3 );
		tab_pop_bg = new NinePatch( R3D.findRegion( "appbar-tab-pop-bg" ) , 1 , 1 , 1 , 7 );
		tab_pop_item_select_bg = new NinePatch( R3D.findRegion( "appbar-tab-pop-item-select-bg" ) );
		appTab.onThemeChanged();
		if( pluginTab != null )
		{
			pluginTab.onThemeChanged();
		}
		if( tabPopMenu != null )
		{
			tabPopMenu.onThemeChanged();
		}
	}
	
	public void setAppHost(
			AppHost3D ah )
	{
		appHost = ah;
	}
	
	public void initContent()
	{
		popItem = 3;
		popMenu.PopItem( popItem );
		onContentTableChange( popItem );
	}
	
	public int getContentType()
	{
		return AppHost3D.currentContentType;
	}
	
	public void onContentTableChange(
			int content )
	{
		appHost.changeContentType();
		switch( content )
		{
			case AppHost3D.CONTENT_TYPE_APP:
				AppHost3D.currentContentType = AppHost3D.CONTENT_TYPE_APP;
				appTab.changeRegion( appTab.appTitle );
				appList.show();
				break;
		}
	}
	
	public void setAppList(
			AppList3D appList )
	{
		this.appList = appList;
	}
	
	public void setAppPopMenu2(
			AppPopMenu2 popMenu2 )
	{
		this.popMenu2 = popMenu2;
	}
	
	public static Bitmap titleToPixmap(
			String title ,
			int titleWidth ,
			int titleHeight ,
			int alignH ,
			int alignV ,
			int padding ,
			boolean bigWord ,
			int color )
	{
		Paint paint = new Paint();
		paint.setColor( color );
		paint.setAntiAlias( true );
		if( DefaultLayout.title_style_bold )
			paint.setFakeBoldText( true );
		if( bigWord )
			paint.setTextSize( titleHeight / 2 );
		else
			paint.setTextSize( titleHeight / 3f );
		if( titleWidth == -1 )
		{
			titleWidth = (int)( paint.measureText( title ) + padding * 2 );
		}
		else if( paint.measureText( title ) > titleWidth - 2 )
		{
			while( paint.measureText( title ) > titleWidth - paint.measureText( ".." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "..";
		}
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		FontMetrics fontMetrics = paint.getFontMetrics();
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		float x = 10;
		if( alignH == TEXT_ALIGN_CENTER )
			x = (float)( titleWidth - paint.measureText( title ) ) / 2f;
		else if( alignH == TEXT_ALIGN_RIGHT )
			x = (float)( titleWidth - paint.measureText( title ) ) - x;
		if( x < 0 )
			x = 0;
		float titleY = (float)( titleHeight - Math.ceil( fontMetrics.ascent + fontMetrics.descent ) ) / 2f;
		if( color == Color.WHITE )
		{
			if( !DefaultLayout.hide_title_bg_shadow )
			{
				int text_color = DefaultLayout.title_outline_color;
				paint.setColor( text_color );
				int shadow_color = DefaultLayout.title_shadow_color;
				for( int j = 1 ; j <= DefaultLayout.title_outline_shadow_size ; j++ )
				{
					paint.setShadowLayer( 1f , -j , 0f , shadow_color );
					canvas.drawText( title , x - j , titleY , paint );
					paint.setShadowLayer( 1f , 0f , -j , shadow_color );
					canvas.drawText( title , x , titleY - j , paint );
					paint.setShadowLayer( 1f , j , 0f , shadow_color );
					canvas.drawText( title , x + j , titleY , paint );
					paint.setShadowLayer( 1f , 0f , j , shadow_color );
					canvas.drawText( title , x , titleY + j , paint );
				}
				// paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
				// canvas.drawText(title, x, titleY, paint);
			}
		}
		paint.clearShadowLayer();
		paint.setColor( color );
		canvas.drawText( title , x , titleY , paint );
		return bmp;
	}
	
	public static Bitmap titleToPixmapNew(
			String title ,
			int titleHeight ,
			boolean bigWord ,
			int color ,
			boolean shadow )
	{
		Paint paint = new Paint();
		paint.setColor( color );
		paint.setAntiAlias( true );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		if( DefaultLayout.title_style_bold )
			paint.setFakeBoldText( true );
		if( bigWord )
			paint.setTextSize( titleHeight / 2 );
		else
			paint.setTextSize( titleHeight / 3f );
		int titleWidth = (int)( paint.measureText( title ) );
		FontMetrics fontMetrics = paint.getFontMetrics();
		titleHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float x = 0;
		float titleY = (float)( -fontMetrics.ascent );
		if( color == Color.WHITE )
		{
			if( shadow )
			{
				int text_color = DefaultLayout.title_outline_color;
				paint.setColor( text_color );
				int shadow_color = DefaultLayout.title_shadow_color;
				for( int j = 1 ; j <= DefaultLayout.title_outline_shadow_size ; j++ )
				{
					paint.setShadowLayer( 1f , -j , 0f , shadow_color );
					canvas.drawText( title , x - j , titleY , paint );
					paint.setShadowLayer( 1f , 0f , -j , shadow_color );
					canvas.drawText( title , x , titleY - j , paint );
					paint.setShadowLayer( 1f , j , 0f , shadow_color );
					canvas.drawText( title , x + j , titleY , paint );
					paint.setShadowLayer( 1f , 0f , j , shadow_color );
					canvas.drawText( title , x , titleY + j , paint );
				}
				// paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
				// canvas.drawText(title, x, titleY, paint);
			}
		}
		paint.clearShadowLayer();
		paint.setColor( color );
		canvas.drawText( title , x , titleY , paint );
		// saveBmp(bmp,title);
		return bmp;
	}
	
	public static Bitmap titleToPixmap(
			String title ,
			int titleHeight ,
			boolean bigWord ,
			int color ,
			boolean shadow )
	{
		Paint paint = new Paint();
		paint.setColor( color );
		paint.setAntiAlias( true );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		if( DefaultLayout.title_style_bold )
			paint.setFakeBoldText( true );
		if( bigWord )
			paint.setTextSize( titleHeight / 2 );
		else
			paint.setTextSize( titleHeight / 3f );
		int titleWidth = (int)( paint.measureText( title ) );
		FontMetrics fontMetrics = paint.getFontMetrics();
		titleHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float x = 0;
		float titleY = (float)( -fontMetrics.ascent );
		if( color == Color.WHITE )
		{
			if( !DefaultLayout.hide_title_bg_shadow )
			{
				int text_color = DefaultLayout.title_outline_color;
				paint.setColor( text_color );
				int shadow_color = DefaultLayout.title_shadow_color;
				for( int j = 1 ; j <= DefaultLayout.title_outline_shadow_size ; j++ )
				{
					paint.setShadowLayer( 1f , -j , 0f , shadow_color );
					canvas.drawText( title , x - j , titleY , paint );
					paint.setShadowLayer( 1f , 0f , -j , shadow_color );
					canvas.drawText( title , x , titleY - j , paint );
					paint.setShadowLayer( 1f , j , 0f , shadow_color );
					canvas.drawText( title , x + j , titleY , paint );
					paint.setShadowLayer( 1f , 0f , j , shadow_color );
					canvas.drawText( title , x , titleY + j , paint );
				}
				// paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
				// canvas.drawText(title, x, titleY, paint);
			}
		}
		paint.clearShadowLayer();
		paint.setColor( color );
		canvas.drawText( title , x , titleY , paint );
		// saveBmp(bmp,title);
		return bmp;
	}
	
	public static Bitmap titleToPixmapWidthLimit(
			String title ,
			int widthLimit ,
			int size ,
			int color ,
			boolean shadow )
	{
		Paint paint = new Paint();
		paint.setColor( color );
		paint.setAntiAlias( true );
		paint.setTextSize( size );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		if( paint.measureText( title ) > widthLimit - 2 )
		{
			while( paint.measureText( title ) > widthLimit - paint.measureText( ".." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "..";
		}
		int titleWidth = (int)( paint.measureText( title ) );
		FontMetrics fontMetrics = paint.getFontMetrics();
		int titleHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float x = 0;
		float titleY = (float)( -fontMetrics.ascent );
		if( color == Color.WHITE )
		{
			paint.setColor( 0x80000000 );
			paint.setShadowLayer( 1f , -1f , 0f , 0x20000000 );
			canvas.drawText( title , x - 1 , titleY , paint );
			paint.setShadowLayer( 1f , 0f , -1f , 0x20000000 );
			canvas.drawText( title , x , titleY - 1 , paint );
			paint.setShadowLayer( 1f , 1f , 0f , 0x20000000 );
			canvas.drawText( title , x + 1 , titleY , paint );
			paint.setShadowLayer( 1f , 0f , 1f , 0x20000000 );
			canvas.drawText( title , x , titleY + 1 , paint );
			paint.setShadowLayer( 2f , 0.0f , 1.0f , 0xcc000000 );
			canvas.drawText( title , x , titleY , paint );
			paint.clearShadowLayer();
		}
		paint.setColor( color );
		canvas.drawText( title , x , titleY , paint );
		// saveBmp(bmp,title);
		return bmp;
	}
	
	public static Bitmap titleToPixmap(
			String title ,
			int titleWidth ,
			int titleHeight ,
			int alignH ,
			int alignV ,
			int padding ,
			int color )
	{
		return titleToPixmap( title , titleWidth , titleHeight , alignH , alignV , padding , false , color );
	}
	
	public static Bitmap titleToPixmap(
			String title ,
			int titleWidth ,
			int titleHeight ,
			int alignH ,
			int alignV ,
			int color )
	{
		return titleToPixmap( title , titleWidth , titleHeight , alignH , alignV , 0 , false , color );
	}
	
	public static Bitmap titleToPixmap(
			String title ,
			int titleWidth ,
			int titleHeight ,
			int alignH ,
			int alignV ,
			boolean bigWord ,
			int color )
	{
		return titleToPixmap( title , titleWidth , titleHeight , alignH , alignV , 0 , bigWord , color );
	}
	
	public interface OnTabChangeListener
	{
		
		public void onTabChange(
				int tabId );
	}
	
	public void setOnTabChangeListener(
			OnTabChangeListener listener )
	{
		this.listener = listener;
	}
	
	public class AppTab extends View3D
	{
		
		private TextureRegion tabArrow;
		private TextureRegion contentTitle;
		private TextureRegion widgetTitle;
		public TextureRegion appTitle;
		private TextureRegion uninstallTitle;
		private TextureRegion downloadAppTitle;
		private TextureRegion hideTitle;
		private TextureRegion naviBack;
		public float tabContentWidth;
		public float tabWidgetWidth;
		private float naviBackX;
		private float naviBackY;
		private int mode = AppList3D.APPLIST_MODE_NORMAL;
		private boolean selected = false;
		
		public AppTab(
				String name )
		{
			super( name );
			x = 0;
			y = 0;
			this.originY = height / 2;
			widgetTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_widget , (int)R3D.appbar_height , false , Color.WHITE , false ) , true ) );// R3D.findRegion("appbar-tab-widget");
			contentTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_app , (int)R3D.appbar_height , false , Color.WHITE , false ) , true ) );// R3D.findRegion("appbar-tab-app");
			appTitle = contentTitle;
			tabArrow = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-arrow.png" ) , true ) );// R3D.findRegion("appbar-tab-arrow");
			tabContentWidth = contentTitle.getRegionWidth() + 2 * R3D.appbar_tab_padding;
			tabWidgetWidth = widgetTitle.getRegionWidth() + 2 * R3D.appbar_tab_padding;
			width = R3D.appbar_padding_left + tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() + tabWidgetWidth;
			height = R3D.appbar_height;
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
			{
				uninstallTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_edit_mode , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );
			}
			else
			{
				uninstallTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_uninstall , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );// R3D.findRegion("appbar-"+R3D.getString(RR.string.uninstall_app));
			}
			// teapotXu add end for Folder in Mainmenu
			// downloadAppTitle=
			// R3D.findRegion("appbar-"+R3D.getString(RR.string.download_apps));
			hideTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_hide , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );// R3D.findRegion("appbar-"+R3D.getString(RR.string.hide_icon));
			naviBackX = R3D.appbar_tab_padding;
			naviBackY = height / 2 - R3D.appbar_menu_height / 2;
			naviBack = R3D.findRegion( "appbar-navi-back" );
		}
		
		public void onThemeChanged()
		{
			// TODO Auto-generated method stub
			//			( (BitmapTexture)widgetTitle.getTexture() ).changeBitmap( AppBar3D.titleToPixmap( R3D.appbar_tab_widget , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
			//			( (BitmapTexture)contentTitle.getTexture() ).changeBitmap( AppBar3D.titleToPixmap( R3D.appbar_tab_app , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
			tabArrow = R3D.findRegion( "appbar-tab-arrow" );
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
			{
				( (BitmapTexture)uninstallTitle.getTexture() ).changeBitmap( AppBar3D.titleToPixmap( R3D.appbar_tab_edit_mode , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
			}
			else
			{
				( (BitmapTexture)uninstallTitle.getTexture() ).changeBitmap( AppBar3D.titleToPixmap( R3D.appbar_tab_uninstall , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
			}
			// teapotXu add end for Folder in Mainmenu
			( (BitmapTexture)hideTitle.getTexture() ).changeBitmap( AppBar3D.titleToPixmap( R3D.appbar_tab_hide , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
		}
		
		public float getConfigTabContentWidth()
		{
			float res = Math.max( contentTitle.getRegionWidth() , R3D.findRegion( "appbar-tab-navigator-audioalbum" ).getRegionWidth() );
			res = Math.max( R3D.findRegion( "appbar-tab-navigator-photobucket" ).getRegionWidth() , res );
			res = Math.max( R3D.findRegion( "appbar-tab-navigator-video" ).getRegionWidth() , res );
			return res;
		}
		
		public float getTabContentWidth()
		{
			return tabContentWidth;
		}
		
		public float getTabWidgetWidth()
		{
			return tabWidgetWidth;
		}
		
		public float getAppTabTotalWidth()
		{
			return R3D.appbar_padding_left + tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() + tabWidgetWidth;
		}
		
		public int getMode()
		{
			return mode;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// super.draw(batch, parentAlpha);
			// int srcBlendFunc = 0,dstBlendFunc = 0;
			// if(DefaultLayout.blend_func_dst_gl_one){
			// /*获取获取混合方式*/
			// srcBlendFunc = batch.getSrcBlendFunc();
			// dstBlendFunc = batch.getDstBlendFunc();
			// if(srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc !=
			// GL11.GL_ONE)
			// batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			// }
			//			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			batch.setColor( color.r , color.g , color.b , color.a * 0 );
			int naviBackOffsetX = R3D.appbar_menu_width / 2;
			float naviBackWidth = naviBack.getRegionWidth() * Utils3D.getDensity() / 1.5f;
			float naviBackHeight = naviBack.getRegionHeight() * Utils3D.getDensity() / 1.5f;
			if( DefaultLayout.appbar_show_userapp_list == true )
			{
				naviBackOffsetX = R3D.appbar_menu_width / 4;
			}
			if( mode == AppList3D.APPLIST_MODE_NORMAL )
			{
				batch.draw( contentTitle , R3D.appbar_padding_left + R3D.appbar_tab_padding , y + ( height - contentTitle.getRegionHeight() ) / 2 );
				//				if( !appList.mHideMainmenuWidget )
				//					batch.draw(
				//							widgetTitle ,
				//							tabContentWidth + R3D.appbar_padding_left + R3D.findRegion( "appbar-divider" ).getRegionWidth() + R3D.appbar_tab_padding ,
				//							y + ( height - widgetTitle.getRegionHeight() ) / 2 );
			}
			else if( mode == AppList3D.APPLIST_MODE_HIDE )
			{
				// batch.draw(naviBack, naviBackX +naviBackOffsetX
				// - naviBackWidth / 2, naviBackY
				// + (R3D.appbar_menu_height - naviBackHeight)
				// / 2, naviBackWidth,
				// naviBackHeight);
				batch.draw( hideTitle , R3D.appbar_tab_padding , y + ( height - hideTitle.getRegionHeight() ) / 2 );
			}
			else if( mode == AppList3D.APPLIST_MODE_UNINSTALL )
			{
				// batch.draw(naviBack, naviBackX + naviBackOffsetX
				// - naviBackWidth / 2, naviBackY
				// + (R3D.appbar_menu_height - naviBackHeight)
				// / 2, naviBackWidth,
				// naviBackHeight);
				batch.draw( uninstallTitle , R3D.appbar_tab_padding , y + ( height - uninstallTitle.getRegionHeight() ) / 2 );
			}
			else if( mode == AppList3D.APPLIST_MODE_USERAPP )
			{
				// batch.draw(naviBack, naviBackX + naviBackOffsetX
				// - naviBackWidth / 2, naviBackY
				// + (R3D.appbar_menu_height - naviBackHeight)
				// / 2, naviBackWidth,
				// naviBackHeight);
				batch.draw( downloadAppTitle , R3D.appbar_tab_padding , y + ( height - downloadAppTitle.getRegionHeight() ) / 2 );
			}
			// if(DefaultLayout.blend_func_dst_gl_one){
			// batch.setBlendFunction(srcBlendFunc, dstBlendFunc);
			// }
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			//Jone :disable  tab changing effect if destoktop is not inited done.
			if( !Desktop3DListener.initDone() )
			{
				return true;
			}
			if( mode != AppList3D.APPLIST_MODE_NORMAL )
			{
				if( x <= R3D.appbar_tab_padding + R3D.appbar_menu_width + contentTitle.getRegionWidth() )
					appList.setMode( AppList3D.APPLIST_MODE_NORMAL );
			}
			else
			{
				if( x < tabContentWidth + R3D.appbar_padding_left )
				{
					if( tabIndicator.tabId != TAB_CONTENT )
					{
						listener.onTabChange( TAB_CONTENT );
						tabIndicator.onTabChange( TAB_CONTENT );
					}
					if( pluginTab != null )
					{
						pluginTab.cancelSelected();
					}
					if( moreButton != null )
					{
						moreButton.cancelSelect();
					}
					return true;
				}
				else if( appList.mHideMainmenuWidget )
				{
					if( pluginTab != null )
					{
						pluginTab.cancelSelected();
					}
					if( moreButton != null )
					{
						moreButton.cancelSelect();
					}
					return false;
				}
				else if( x >= tabContentWidth + R3D.appbar_padding_left && x < tabContentWidth + R3D.appbar_padding_left + tabWidgetWidth )
				{
					//					if( tabIndicator.tabId != TAB_WIDGET )
					//					{
					//						listener.onTabChange( TAB_WIDGET );
					//						tabIndicator.onTabChange( TAB_WIDGET );
					//					}
					//					if( pluginTab != null )
					//					{
					//						pluginTab.cancelSelected();
					//					}
					//					if( moreButton != null )
					//					{
					//						moreButton.cancelSelect();
					//					}
					//					return true;
				}
			}
			return true;
		}
		
		public void setMode(
				int mode )
		{
			if( DefaultLayout.appbar_show_userapp_list == true )
			{
				if( mode != AppList3D.APPLIST_MODE_USERAPP )
				{
					if( home != null )
					{
						home.show();
					}
				}
			}
			this.mode = mode;
		}
		
		public void changeRegion(
				TextureRegion region )
		{
			contentTitle = region;
		}
		
		public void cancelSelect()
		{
			selected = false;
		}
		
		public void select()
		{
			if( tabIndicator.tabId == AppBar3D.TAB_CONTENT || tabIndicator.tabId == AppBar3D.TAB_WIDGET )
				selected = true;
		}
		
		public void update()
		{
			// TODO Auto-generated method stub
			tabContentWidth = contentTitle.getRegionWidth() + 2 * R3D.appbar_tab_padding;
			tabWidgetWidth = widgetTitle.getRegionWidth() + 2 * R3D.appbar_tab_padding;
			width = R3D.appbar_padding_left + tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() + tabWidgetWidth;
			height = R3D.appbar_height;
			naviBackX = R3D.appbar_tab_padding;
			naviBackY = height / 2 - R3D.appbar_menu_height / 2;
			naviBack = R3D.findRegion( "appbar-navi-back" );
		}
	}
	
	class AppTabIndicator extends View3D
	{
		
		public int tabId = TAB_CONTENT;
		float dividerWidth = 0;
		ArrayList<TabTitle3D> tabTitle3DList = null;
		
		public AppTabIndicator(
				String name )
		{
			super( name );
			x = 0;
			y = 0;
			width = Utils3D.getScreenWidth();
			height = R3D.appbar_indicator_height;
			this.originY = height / 2;
			dividerWidth = R3D.findRegion( "appbar-divider" ).getRegionWidth();
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// super.draw(batch, parentAlpha);
			float w = 0;
			batch.setColor( color.r , color.g , color.b , color.a * 0 );
			float user = this.getUser();
			float drawWidth = 0;
			if( user <= ( appTab.tabContentWidth + R3D.appbar_padding_left + dividerWidth ) )
			{
				// content and tab
				w = user / ( appTab.tabContentWidth + dividerWidth );
				drawWidth = ( 1 - w ) * appTab.tabContentWidth + w * appTab.tabWidgetWidth;
			}
			else if( user > ( appTab.tabContentWidth + R3D.appbar_padding_left + dividerWidth ) && user <= ( appTab.tabContentWidth + R3D.appbar_padding_left + dividerWidth + appTab.tabWidgetWidth + dividerWidth ) )
			{
				// tab and plugin
				w = ( user - appTab.tabContentWidth - R3D.appbar_padding_left - dividerWidth ) / ( appTab.tabWidgetWidth + dividerWidth );
				TabTitle3D tabTitle = (TabTitle3D)pluginTab.getChildAt( 0 );
				drawWidth = ( 1 - w ) * appTab.tabWidgetWidth + w * ( tabTitle.width );
			}
			else if( user > ( R3D.appbar_padding_left + appTab.tabContentWidth + dividerWidth + appTab.tabWidgetWidth + dividerWidth ) && user <= ( appTab.tabContentWidth + R3D.appbar_padding_left + dividerWidth + appTab.tabWidgetWidth + dividerWidth + pluginTab.width ) )
			{
				int curIndex = 0;
				TabTitle3D tabTitle = null;
				for( int i = 0 ; i < pluginTab.getChildCount() ; i++ )
				{
					View3D tmp = pluginTab.getChildAt( i );
					if( tmp instanceof TabTitle3D )
					{
						tabTitle = (TabTitle3D)tmp;
					}
					if( user > ( pluginTab.x + tabTitle.x - R3D.appbar_tab_padding ) && user < ( pluginTab.x + tabTitle.x + tabTitle.width + R3D.appbar_tab_padding + dividerWidth ) )
					{
						curIndex = i;
						break;
					}
				}
				if( curIndex >= 0 && curIndex < pluginTab.getChildCount() )
				{
					w = ( user - pluginTab.x - tabTitle.x + R3D.appbar_tab_padding ) / ( tabTitle.width + dividerWidth );
				}
				if( curIndex < pluginTab.getChildCount() - 1 )
				{
					TabTitle3D nextTabTitle = (TabTitle3D)pluginTab.getChildAt( curIndex + 1 );
					drawWidth = ( 1 - w ) * ( tabTitle.width ) + w * ( nextTabTitle.width );
				}
				else
				{
					drawWidth = tabTitle.width;
				}
			}
			else
			{
				if( tabId == AppBar3D.TAB_MORE )
				{
					drawWidth = moreButton.width;
				}
			}
			float thin_height = height / 4;
			if( DefaultLayout.show_appbar_indicatorBg )
				indicatorBg.draw( batch , 0 , 0 , width , thin_height );
			if( !appList.mHideMainmenuWidget )
				indicatorBg.draw( batch , this.getUser() , 0 , drawWidth , height * 3 / 4 );
			// if(this.getUser()<pluginTab.x){
			// w = user/(tab.tabContentWidth + dividerWidth);
			// drawWidth=(1 - w)
			// * tab.tabContentWidth + w * tab.tabWidgetWidth;
			// float thin_height = height / 4;
			// indicatorBg.draw(batch, 0, 0, width, thin_height);
			// if (!appList.mHideMainmenuWidget)
			// indicatorBg.draw(batch, this.getUser(), 0, (1 - w)
			// * tab.tabContentWidth + w * tab.tabWidgetWidth,
			// height * 3 / 4);
			// }
		}
		
		public void onTabChange(
				int id )
		{
			tabId = id;
			if( tabId == TAB_CONTENT )
			{
				stopTween();
				if( menu != null )
					menu.show();
				// if(content != null)
				// content.show();
				// this.setUser(R3D.appbar_tab_width);
				if( DefaultLayout.appbar_show_userapp_list == true )
				{
					if( home != null )
					{
						home.show();
					}
				}
				this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0f ,// 0.5f,
						0 ,
						0 ,
						0 );
			}
			else if( tabId == TAB_WIDGET )
			{
				if( menu != null )
					menu.hide();
				// if(content != null)
				// content.hide();
				if( DefaultLayout.appbar_show_userapp_list == true )
				{
					if( home != null )
					{
						home.hide();
					}
				}
				stopTween();
				// this.setUser(0);
				this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0f ,// 0.5f
						appTab.tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() ,
						0 ,
						0 );
			}
			else if( tabId == TAB_PLUGIN )
			{
				if( menu != null )
					menu.hide();
				// if(content != null)
				// content.hide();
				if( DefaultLayout.appbar_show_userapp_list == true )
				{
					if( home != null )
					{
						home.hide();
					}
				}
				stopTween();
				// this.setUser(0);
				float targetX = appTab.tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() + appTab.tabWidgetWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth();
				ArrayList<Plugin> plugins = TabPluginManager.getInstance().getPluginList();
				for( int i = 0 ; i < plugins.size() ; i++ )
				{
					Plugin plugin = plugins.get( i );
					if( plugin.mSelected )
					{
						TabTitle3D tabTitle = plugin.getTabTitle();
						targetX = pluginTab.x + tabTitle.x;
						break;
					}
				}
				this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0f ,// 0.5f
						targetX ,
						0 ,
						0 );
			}
			else if( tabId == TAB_MORE )
			{
				if( menu != null )
					menu.hide();
				if( DefaultLayout.appbar_show_userapp_list == true )
				{
					if( home != null )
					{
						home.hide();
					}
				}
				stopTween();
				float targetX = moreButton.x;
				this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0f ,// 0.5f
						targetX ,
						0 ,
						0 );
			}
		}
	}
	
	class PluginTab extends ViewGroup3D
	{
		
		ArrayList<Plugin> plugins = null;
		ArrayList<TabTitle3D> allTabTitles = null;
		ArrayList<TabTitle3D> showTabTitles = null;
		ArrayList<TabTitle3D> hideTabTitles = null;
		private int mode = AppList3D.APPLIST_MODE_NORMAL;
		private Plugin lastSelectedTabTitle = null;
		
		public PluginTab(
				String name )
		{
			super( name );
			tabPopMenu = new TabPopMenu( "apppopmenu" );
			tabPopMenu.hideNoAnim();
			build();
			//			 this.setBackgroud(new NinePatch(new
			//			 BitmapTexture(BitmapFactory.decodeResource(iLoongLauncher.getInstance().getResources(),
			//			 R.drawable.red))));
			transform = true;
		}
		
		public void onThemeChanged()
		{
			// TODO Auto-generated method stub
			for( int i = 0 ; i < showTabTitles.size() ; i++ )
			{
				showTabTitles.get( i ).onThemeChanged();
			}
			for( int i = 0 ; i < hideTabTitles.size() ; i++ )
			{
				hideTabTitles.get( i ).onThemeChanged();
			}
		}
		
		public Plugin getSelectedTabTitle()
		{
			Plugin selectedPlugin = null;
			ArrayList<Plugin> plugins = TabPluginManager.getInstance().getPluginList();
			for( int i = 0 ; i < plugins.size() ; i++ )
			{
				Plugin plugin = plugins.get( i );
				if( plugin.mSelected )
				{
					selectedPlugin = plugin;
					break;
				}
			}
			return selectedPlugin;
		}
		
		public void update()
		{
			lastSelectedTabTitle = pluginTab.getSelectedTabTitle();
			build();
			//			if(lastSelectedTabTitle!=null){
			//				ArrayList<Plugin> plugins=TabPluginManager.getInstance().getPluginList();
			//				for (int i = 0; i < plugins.size(); i++) {
			//					Plugin plugin=plugins.get(i);
			//					if (plugin.getTabPluginMetaData().packageName.equals(lastSelectedTabTitle.getTabPluginMetaData().packageName)) {
			//						plugin.selected();
			//						break;
			//					}
			//				}	
			//			}
		}
		
		public void build()
		{
			this.removeAllViews();
			plugins = tabPluginManager.getPluginList();
			allTabTitles = new ArrayList<TabTitle3D>( plugins.size() );
			showTabTitles = new ArrayList<TabTitle3D>( plugins.size() );
			hideTabTitles = new ArrayList<TabTitle3D>( plugins.size() );
			for( int i = 0 ; i < plugins.size() ; i++ )
			{
				TabTitle3D newTitle = plugins.get( i ).buildTabTitle3D();
				Log.e( "appbar" , "pluginTab build packageName:" + plugins.get( i ).getTabPluginMetaData().packageName );
				newTitle.setPadding( R3D.appbar_tab_padding , 0 , R3D.appbar_tab_padding , 0 );
				allTabTitles.add( newTitle );
			}
			for( int i = 0 ; i < showPluginTabCount ; i++ )
			{
				showTabTitles.add( allTabTitles.get( i ) );
			}
			for( int i = showPluginTabCount ; i < allTabTitles.size() ; i++ )
			{
				hideTabTitles.add( allTabTitles.get( i ) );
			}
			float tabWidth = 0;
			for( int i = 0 ; i < showTabTitles.size() ; i++ )
			{
				tabWidth += showTabTitles.get( i ).width;
			}
			if( plugins.size() > 0 )
			{
				tabWidth += ( showTabTitles.size() - 1 ) * ( R3D.findRegion( "appbar-divider" ).getRegionWidth() );
			}
			else
			{
				tabWidth = 0;
			}
			this.setSize( tabWidth , R3D.appbar_height );
			this.setOrigin( this.width / 2 , this.height / 2 );
			float pluginTabX = appTab.getAppTabTotalWidth();
			if( mode == AppList3D.APPLIST_MODE_NORMAL )
			{
				this.x = pluginTabX + R3D.findRegion( "appbar-divider" ).getRegionWidth();
			}
			y = 0;
			for( int i = 0 ; i < showTabTitles.size() ; i++ )
			{
				if( i == 0 )
				{
					showTabTitles.get( i ).x = 0;
				}
				else
				{
					showTabTitles.get( i ).x = showTabTitles.get( i - 1 ).x + showTabTitles.get( i - 1 ).width + R3D.findRegion( "appbar-divider" ).getRegionWidth();
				}
				showTabTitles.get( i ).y = height / 2 - showTabTitles.get( i ).getHeight() / 2;
				this.addView( showTabTitles.get( i ) );
			}
			if( showMoreButton && hideTabTitles.size() > 0 )
			{
				moreButton.show();
				tabPopMenu.build( hideTabTitles );
			}
			else
			{
				moreButton.hide();
				tabPopMenu.hide();
			}
		}
		
		public void setMode(
				int mode )
		{
			this.mode = mode;
			if( this.mode == AppList3D.APPLIST_MODE_NORMAL )
			{
				this.show();
			}
			else
			{
				this.hide();
			}
			if( moreButton != null )
			{
				moreButton.setMode( mode );
			}
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			// mode = tab.getMode();
			// float pluginTabX = tab.getAppTabTotalWidth();
			// if (mode == AppList3D.APPLIST_MODE_NORMAL) {
			// if (DefaultLayout.enable_explorer) {
			// pluginTabX = R3D.appbar_padding_left
			// + tab.getTabContentWidth();
			// if (!appList.mHideMainmenuWidget)
			// pluginTabX = R3D.appbar_padding_left
			// + tab.getTabContentWidth()
			// + R3D.findRegion("appbar-divider")
			// .getRegionWidth()
			// + tab.getTabWidgetWidth();
			// }
			// this.x = pluginTabX
			// + R3D.findRegion("appbar-divider").getRegionWidth();
			// }
			super.draw( batch , parentAlpha );
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			super.onClick( x , y );
			moreButton.cancelSelect();
			appTab.cancelSelect();
			listener.onTabChange( TAB_PLUGIN );
			tabIndicator.onTabChange( TAB_PLUGIN );
			SendMsgToAndroid.sysPlaySoundEffect();
			return true;
		}
		
		public void select()
		{
			// TODO Auto-generated method stub
		}
		
		public void cancelSelect()
		{
			// TODO Auto-generated method stub
			// for (int i = 0; i < this.getChildCount(); i++) {
			// View3D child = this.getChildAt(i);
			// if (child instanceof TabPopItem) {
			// ((TabPopItem) child).selected = false;
			// }
			// }
		}
		
		public void cancelSelected()
		{
			// TODO Auto-generated method stub
		}
	}
	
	class DeleteButton extends View3D
	{
		
		TextureRegion textureRegion[];;
		int index = 0;
		Thread mThread = null;
		
		public DeleteButton(
				String name )
		{
			super( name );
			region = R3D.findRegion( "delete-button" );
			width = R3D.appbar_menu_width;
			height = R3D.appbar_menu_height;
			x = ( Utils3D.getScreenWidth() / 3 - width ) / 2;
			y = ( R3D.appbar_height ) / 2 - height / 2;
		}
		
		//		@Override
		//		public void draw(
		//				SpriteBatch batch ,
		//				float parentAlpha )
		//		{
		//			x=(Utils3D.getScreenWidth()/3-width)/2;
		//			float regionWidth = 0;
		//			float regionHeight = 0;
		//			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		//			if( DefaultLayout.getMMSettingValue() == 2 )
		//			{
		//				if( textureRegion.length > 0 )
		//				{
		//					regionWidth = textureRegion[index].getRegionWidth();
		//					regionHeight = textureRegion[index].getRegionHeight();
		//					batch.draw( textureRegion[index] , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		//				}
		//			}
		//			else
		//			{
		//				regionWidth = region.getRegionWidth();
		//				regionHeight = region.getRegionHeight();
		//				batch.draw( region , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		//			}
		//		}
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			if( DefaultLayout.appbar_show_userapp_list == true )
			{
				appList.setMode( AppList3D.APPLIST_MODE_USERAPP );
				this.hide();
			}
			else
			{
				if( !RR.net_version )
				{
					appList.setMode( AppList3D.APPLIST_MODE_NORMAL );
					if( DefaultLayout.startKeyAction() == 0 )
						viewParent.onCtrlEvent( appList , AppList3D.APP_LIST3D_KEY_BACK );
				}
				else
				{
					appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
				}
				AndroidGraphics graphics = (AndroidGraphics)Gdx.graphics;
				graphics.forceRender( 30 );
			}
			SendMsgToAndroid.sysPlaySoundEffect();
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "AppHostClickTrash" );
			return true;
		}
	}
	
	class HomeButton extends View3D
	{
		
		TextureRegion textureRegion[];;
		int index = 0;
		Thread mThread = null;
		
		public HomeButton(
				String name )
		{
			super( name );
			region = R3D.findRegion( "home-button" );
			width = R3D.appbar_menu_width;
			height = R3D.appbar_menu_height;
			x = ( Utils3D.getScreenWidth() - width ) / 2;
			y = ( R3D.appbar_height ) / 2 - height / 2;
		}
		
		//		@Override
		//		public void draw(
		//				SpriteBatch batch ,
		//				float parentAlpha )
		//		{
		//			x=(Utils3D.getScreenWidth()-width)/2;
		//			float regionWidth = 0;
		//			float regionHeight = 0;
		//			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		//			if( DefaultLayout.getMMSettingValue() == 2 )
		//			{
		//				if( textureRegion.length > 0 )
		//				{
		//					regionWidth = textureRegion[index].getRegionWidth();
		//					regionHeight = textureRegion[index].getRegionHeight();
		//					batch.draw( textureRegion[index] , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		//				}
		//			}
		//			else
		//			{
		//				regionWidth = region.getRegionWidth();
		//				regionHeight = region.getRegionHeight();
		//				batch.draw( region , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		//			}
		//		}
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			if( DefaultLayout.appbar_show_userapp_list == true )
			{
				appList.setMode( AppList3D.APPLIST_MODE_USERAPP );
				this.hide();
			}
			else
			{
				appList.setMode( AppList3D.APPLIST_MODE_NORMAL );
				if( DefaultLayout.startKeyAction() == 0 )
					viewParent.onCtrlEvent( appList , AppList3D.APP_LIST3D_KEY_BACK );
			}
			SendMsgToAndroid.sysPlaySoundEffect();
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "AppHostClickHome" );
			return true;
		}
	}
	
	class AppHomeButton extends View3D
	{
		
		TextureRegion textureRegion[];;
		int index = 0;
		Thread mThread = null;
		
		public AppHomeButton(
				String name )
		{
			super( name );
			if( DefaultLayout.getMMSettingValue() == 0 )
				region = R3D.findRegion( "more-button" );
			//				region = R3D.findRegion( "app-home-button" );
			else if( DefaultLayout.getMMSettingValue() == 2 )
			{
				int count = DefaultLayout.getAppHomeIconCount();
				if( count > 0 )
				{
					textureRegion = new TextureRegion[count];
					String language = Locale.getDefault().getLanguage();
					String path = null;
					if( "zh".equals( language ) )
					{
						path = "theme/pack_source/app_home_";
					}
					else
					{
						path = "theme/pack_source/app_home_en_";
					}
					for( int i = 0 ; i < count ; i++ )
					{
						textureRegion[i] = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( path + ( i + 1 ) + ".png" ) , true ) );
					}
					if( count > 1 )
					{
						mThread = new Thread( new Runnable() {
							
							@Override
							public void run()
							{
								// TODO Auto-generated method stub
								while( true )
								{
									try
									{
										Thread.sleep( 500 );
										index++;
										if( index == textureRegion.length )
										{
											index = 0;
										}
										Gdx.graphics.requestRendering();
									}
									catch( InterruptedException e )
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						} );
						mThread.start();
					}
				}
			}
			else if( DefaultLayout.getMMSettingValue() == 3 )
			{
				region = R3D.findRegion( "app-home-personal" );
			}
			else
			{
				if( DefaultLayout.appbar_bag_icon )
					region = R3D.findRegion( "app-menu-bag" );
				else
					region = R3D.findRegion( "app-menu-downarray" );
			}
			width = R3D.appbar_menu_width;
			height = R3D.appbar_menu_height;
			if( menu == null || !menu.isVisible() )
				//				{
				//					x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
				//				}
				//				else
				//				{
				//					x = Utils3D.getScreenWidth() - 2 * width - R3D.appbar_menu_right - R3D.appbar_home_right;
				//				}
				x = Utils3D.getScreenWidth() / 3 * 2 + ( Utils3D.getScreenWidth() / 3 - width ) / 2;
			y = ( R3D.appbar_height ) / 2 - height / 2;
		}
		
		//		@Override
		//		public void draw(
		//				SpriteBatch batch ,
		//				float parentAlpha )
		//		{
		//			if( menu == null || !menu.isVisible() )
		//			{
		//				x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
		//			}
		//			else
		//			{
		//				x = Utils3D.getScreenWidth() - 2 * width - R3D.appbar_menu_right - R3D.appbar_home_right;
		//			}
		//			float regionWidth = 0;
		//			float regionHeight = 0;
		//			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		//			if( DefaultLayout.getMMSettingValue() == 2 )
		//			{
		//				if( textureRegion.length > 0 )
		//				{
		//					regionWidth = textureRegion[index].getRegionWidth();
		//					regionHeight = textureRegion[index].getRegionHeight();
		//					batch.draw( textureRegion[index] , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		//				}
		//			}
		//			else
		//			{
		//				regionWidth = region.getRegionWidth();
		//				regionHeight = region.getRegionHeight();
		//				batch.draw( region , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		//			}
		//		}
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			if( DefaultLayout.appbar_show_userapp_list == true )
			{
				appList.setMode( AppList3D.APPLIST_MODE_USERAPP );
				this.hide();
			}
			else
			{
				if( !RR.net_version )
				{
					appList.setMode( AppList3D.APPLIST_MODE_NORMAL );
					if( DefaultLayout.startKeyAction() == 0 )
						viewParent.onCtrlEvent( appList , AppList3D.APP_LIST3D_KEY_BACK );
				}
				else
				{
					appHost.popMenu2.show();
					MobclickAgent.onEvent( iLoongLauncher.getInstance() , "AppHostClickMenu" );
				}
				AndroidGraphics graphics = (AndroidGraphics)Gdx.graphics;
				graphics.forceRender( 30 );
			}
			SendMsgToAndroid.sysPlaySoundEffect();
			return true;
		}
		
		public void onThemeChanged()
		{
			if( DefaultLayout.getMMSettingValue() == 0 )
				region = R3D.findRegion( "app-home-button" );
			else if( DefaultLayout.getMMSettingValue() == 2 )
			{
				int count = DefaultLayout.getAppHomeIconCount();
				if( count > 0 )
				{
					textureRegion = new TextureRegion[count];
					String language = Locale.getDefault().getLanguage();
					String path = null;
					if( "zh".equals( language ) )
					{
						path = "theme/pack_source/app_home_";
					}
					else
					{
						path = "theme/pack_source/app_home_en_";
					}
					for( int i = 0 ; i < count ; i++ )
					{
						textureRegion[i] = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( path + ( i + 1 ) + ".png" ) , true ) );
					}
				}
			}
			else if( DefaultLayout.getMMSettingValue() == 3 )
			{
				region = R3D.findRegion( "app-home-personal" );
			}
			else
			{
				if( DefaultLayout.appbar_bag_icon )
					region = R3D.findRegion( "app-menu-bag" );
				else
					region = R3D.findRegion( "app-menu-downarray" );
			}
		}
	}
	
	class AppMenuButton extends View3D
	{
		
		public AppMenuButton(
				String name )
		{
			super( name );
			region = R3D.findRegion( "app-menu-button" );
			width = region.getRegionWidth();// R3D.appbar_menu_width;
			height = R3D.appbar_height;// region.getRegionHeight();//R3D.appbar_menu_height;
			x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
			y = 0;// (R3D.appbar_height) / 2 - height / 2;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// float regionWidth =
			// region.getRegionWidth()*Utils3D.getDensity()/1.5f;
			// float regionHeight =
			// region.getRegionHeight()*Utils3D.getDensity()/1.5f;
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			// batch.draw(region, x + width / 2 - regionWidth / 2, y + height /
			// 2
			// - regionHeight / 2,regionWidth,regionHeight);
			batch.draw( region , x , y + height / 2 - region.getRegionHeight() / 2 , width , region.getRegionHeight() );
			// setBackgroud(new NinePatch(new
			// Texture(Gdx.files.internal("bg.png"))));
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			Log.d( "launcher" , "menu:onClick" );
			if( popMenu != null )
			{
				if( appList.canPopMenu() && appList.xScale == 0 )
				{
					if( popMenu.isVisible() )
					{
						popMenu.hide();
					}
					else
					{
						popMenu.show();
					}
				}
			}
			return true;
		}
	}
	
	class AppPopMenu extends ViewGroup3D
	{
		
		private float itemWidth = 150;
		private float itemHeight = 50;
		
		public AppPopMenu(
				String name )
		{
			super( name );
			itemWidth = R3D.appbar_menuitem_width;
			itemHeight = R3D.appbar_menuitem_height;
			// zqh modifies on 12-05-2012
			// teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
			{
				addItem( R3D.findRegion( "app-uninstall-button" ) , R3D.getString( RR.string.edit_mode ) );
			}
			else
			{
				addItem( R3D.findRegion( "app-uninstall-button" ) , R3D.getString( RR.string.uninstall_app ) );
			}
			// addItem(R3D.findRegion("app-uninstall-button"),
			// R3D.getString(RR.string.uninstall_app));
			// teapotXu add end for Folder in Mainmenu
			addItem( R3D.findRegion( "app-hide-button" ) , R3D.getString( RR.string.hide_icon ) );
			addItem( R3D.findRegion( "app-sort-button" ) , R3D.getString( RR.string.sort_icon ) );
			width = itemWidth;
			height = this.getChildCount() * itemHeight;
			x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right / 3;
			y = Utils3D.getScreenHeight() - height - R3D.appbar_height;
			this.originX = width;
			this.originY = height;
			this.transform = true;
			// itemBg = new TextureRegion
		}
		
		public int getTabContentWidth()
		{
			float res = Math.max( R3D.findRegion( "appbar-pop-app" ).getRegionWidth() , R3D.findRegion( "appbar-pop-audioalbum" ).getRegionWidth() );
			res = Math.max( R3D.findRegion( "appbar-pop-photobucket" ).getRegionWidth() , res );
			res = Math.max( R3D.findRegion( "appbar-pop-video" ).getRegionWidth() , res );
			return (int)res;
		}
		
		public void ClickItem(
				int index )
		{
			final int itemCount = 4;
			for( int i = 0 ; i < itemCount ; i++ )
			{
				if( i == popItem )
					continue;
				if( index == 0 )
				{
					popItem = i;
					break;
				}
				else
					index--;
			}
			PopItem( popItem );
			onContentTableChange( popItem );
		}
		
		private void PopItem(
				int item )
		{
		}
		
		private void addItem(
				TextureRegion region ,
				String title )
		{
			ViewGroup3D item = new ViewGroup3D( "popitem" );
			item.y = ( this.getChildCount() ) * itemHeight;
			item.x = 0;
			View3D itemIcon = new View3D( "itemicon" , region );
			itemIcon.setPosition( R3D.appbar_menuitem_paddingleft , itemHeight / 2 - ( region.getRegionHeight() * Utils3D.getDensity() / 1.5f ) / 2 );
			itemIcon.setSize( region.getRegionWidth() * Utils3D.getDensity() / 1.5f , region.getRegionHeight() * Utils3D.getDensity() / 1.5f );
			item.addView( itemIcon );
			TextureRegion titleRegion = R3D.findRegion( title );
			View3D itemTitle = new View3D( "itemtitle" , titleRegion );
			itemTitle.setPosition( 2 * R3D.appbar_menuitem_paddingleft + itemIcon.width , itemHeight / 2 - itemTitle.height / 2 );
			// itemTitle.setSize(itemWidth - itemTitle.x, itemHeight);
			item.addView( itemTitle );
			item.setSize( 2 * R3D.appbar_menuitem_paddingleft + itemIcon.width + titleRegion.getRegionWidth() , itemHeight );
			View3D itemDivider = new View3D( "itemtitle" , R3D.findRegion( "appbar-popmenu-divider" ) );
			itemDivider.setPosition( 0 , itemHeight - 2 );
			itemDivider.setSize( itemWidth , 2 );
			item.addView( itemDivider );
			this.addView( item );
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			int h = tr_appItemBg.getRegionHeight();
			for( int i = 0 ; i < height * scaleY / h ; i++ )
			{
				batch.draw( tr_appItemBg , x + width * ( 1 - scaleX ) , y + height - ( i + 1 ) * h , width * scaleX , h );
				// batch.draw(tr, x+width*(1-scaleX), y+height-(i+1)*h,
				// width*scaleX, h);
			}
			super.draw( batch , parentAlpha );
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			if( x >= 0 && x < width && y >= 0 && y < height )
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			if( x >= 0 && x < width && y >= 0 && y < height )
			{
				return false;
			}
			else
			{
				hide();
				return true;
			}
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			int index = (int)( y / itemHeight );
			// zqh modifies on 12-05-2012
			if( index == 0 )
			{
				appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
			}
			else if( index == 1 )
			{
				appList.setMode( AppList3D.APPLIST_MODE_HIDE );
			}
			else if( index == 2 )
			{
				SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId , iLoongLauncher.SORT_ORIGIN_APPLIST );
			}
			this.hide();
			return true;
		}
		
		public void show()
		{
			super.show();
			this.requestFocus();
		}
		
		public void hide()
		{
			this.releaseFocus();
			super.hide();
		}
		
		public void hideNoAnim()
		{
			this.visible = false;
			this.touchable = false;
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		// TextureRegion tr = R3D.findRegion("appbar-bg");
		// int w = tr.getRegionWidth();
		int w = tr_appbarBg.getRegionWidth();
		for( int i = 0 ; i < width / w + 1 ; i++ )
		{
			batch.draw( tr_appbarBg , i * w , y , w , height );
			// batch.draw(tr, i*w, y, w, height);
		}
		// batch.draw(R3D.findRegion("appbar-bg"), 0, y, width, height);
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public void pageScroll(
			float degree ,
			int index ,
			int count )
	{
		if( index == AppList3D.appPageCount - 1 && degree < 0 )
		{
			// tabIndicator.setUser((tab.tabAppWidth+R3D.findRegion("appbar-divider").getRegionWidth())*Math.abs(degree));
		}
		else if( index == AppList3D.appPageCount && degree > 0 )
		{
			// tabIndicator.setUser((tab.tabAppWidth+R3D.findRegion("appbar-divider").getRegionWidth())*(1-Math.abs(degree)));
		}
		else if( index == AppList3D.appPageCount + AppList3D.widgetPageCount - 1 && degree < 0 )
		{
			// tabIndicator.setUser((tab.tabAppWidth+R3D.findRegion("appbar-divider").getRegionWidth())*(1-Math.abs(degree)));
		}
		else if( index == 0 && degree > 0 )
		{
			// tabIndicator.setUser((tab.tabAppWidth+R3D.findRegion("appbar-divider").getRegionWidth())*Math.abs(degree));
		}
	}
	
	@Override
	public void setCurrentPage(
			int current )
	{
		if( tabIndicator.tabId == TAB_CONTENT || tabIndicator.tabId == TAB_WIDGET )
		{
			if( current < AppList3D.appPageCount || AppList3D.appPageCount == 0 )
				tabIndicator.onTabChange( TAB_CONTENT );
			else
				tabIndicator.onTabChange( TAB_WIDGET );
		}
		else if( tabIndicator.tabId == TAB_PLUGIN )
		{
			boolean tabSelected = false;
			ArrayList<Plugin> plugins = TabPluginManager.getInstance().getPluginList();
			for( int i = 0 ; i < plugins.size() ; i++ )
			{
				Plugin plugin = plugins.get( i );
				if( plugin.mSelected )
				{
					tabSelected = true;
					tabIndicator.onTabChange( TAB_PLUGIN );
					break;
				}
			}
			if( !tabSelected )
			{
				if( current < AppList3D.appPageCount || AppList3D.appPageCount == 0 )
					tabIndicator.onTabChange( TAB_CONTENT );
				else
					tabIndicator.onTabChange( TAB_WIDGET );
			}
		}
	}
	
	@Override
	public int getIndex()
	{
		// TODO Auto-generated method stub
		return -1;
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( keycode == KeyEvent.KEYCODE_MENU )
		{
			if( R3D.getInteger( "pop_setupmenu_style" ) == SetupMenu.POPMENU_STYLE_ANDROID4 )
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		return super.keyDown( keycode );
	}
	
	public void update(
			ArrayList<Plugin> plugins )
	{
		if( moreButton != null )
		{
			showMoreButton = false;
			moreButton.hide();
		}
		calcTabPadding( plugins );
		appTab.update();
		pluginTab.update();
		if( listener instanceof AppHost3D )
		{
			if( ( (AppHost3D)listener ).isVisible() )
			{
				listener.onTabChange( TAB_CONTENT );
			}
		}
		tabIndicator.onTabChange( TAB_CONTENT );
		SendMsgToAndroid.sysPlaySoundEffect();
	}
	
	public static class TabTitleFactory implements ITabTitlePlugin
	{
		
		@Override
		public TabTitlePlugin3D getTabTitle(
				TabContext tabContext ,
				TabPluginMetaData pluginMetaData )
		{
			return new TabTitle( tabContext , pluginMetaData );
		}
	}
	
	public static class TabTitle extends TabTitlePlugin3D
	{
		
		private boolean selected = false;
		private BitmapTexture normalTexture;
		private BitmapTexture selectTexture;
		private TextureRegion normalRegion = null;
		private TextureRegion selectRegion = null;
		
		public TabTitle(
				TabContext tabContext ,
				TabPluginMetaData pluginMetaData )
		{
			super( tabContext , pluginMetaData );
			if( pluginMetaData.pluginTitle == null || pluginMetaData.pluginTitle.length() == 0 )
			{
				String locale = tabContext.mContainerContext.getResources().getConfiguration().locale.toString();
				if( locale.contains( "CN" ) )
				{
					pluginMetaData.pluginTitle = pluginMetaData.cnName;
				}
				else if( locale.contains( "TW" ) )
				{
					pluginMetaData.pluginTitle = pluginMetaData.twName;
				}
				else if( locale.contains( "EN" ) )
				{
					pluginMetaData.pluginTitle = pluginMetaData.enName;
				}
				else
				{
					pluginMetaData.pluginTitle = "UNKNOW";
				}
				if( pluginMetaData.pluginTitle == null || pluginMetaData.pluginTitle.length() == 0 )
				{
					pluginMetaData.pluginTitle = "UNKNOW";
				}
			}
			normalTexture = new BitmapTexture( AppBar3D.titleToPixmap( pluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
			normalRegion = new TextureRegion( normalTexture );
			selectTexture = new BitmapTexture( AppBar3D.titleToPixmap( pluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_select_color , false ) , true );
			selectRegion = new TextureRegion( selectTexture );
			// this.setBackgroud(new NinePatch(new
			// BitmapTexture(BitmapFactory.decodeResource(tabContext.mContainerContext.getResources(),
			// R.drawable.red2))));
			// View3D tabTitle = new View3D("tabTitle");
			// tabTitle.region = new TextureRegion(new BitmapTexture(
			// AppBar3D.titleToPixmap(pluginMetaData.pluginTitle, (int)
			// R3D.appbar_height,
			// false, R3D.appbar_tab_color, false), true));
			// tabTitle.setSize(tabTitle.region.getRegionWidth(),
			// tabTitle.region.getRegionHeight());
			// tabTitle.setOrigin(tabTitle.width/2, tabTitle.height/2);
			// this.setSize(R3D.appbar_tab_width, R3D.appbar_height);
			this.setSize( normalRegion.getRegionWidth() + R3D.appbar_tab_padding * 2 , R3D.appbar_height );
			// tabTitle.x=(this.width-tabTitle.width)/2;
			// tabTitle.y=(this.height-tabTitle.height)/2;
			// this.addView(tabTitle);
			this.setOrigin( width / 2 , height / 2 );
			transform = true;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// TODO Auto-generated method stub
			super.draw( batch , parentAlpha );
			ArrayList<Plugin> plugins = TabPluginManager.getInstance().getPluginList();
			// for (Plugin plugin : plugins) {
			// if (plugin.getTabPluginMetaData().pluginId ==
			// mPluginMetaData.pluginId) {
			// if (plugin.getTabTitle().selected) {
			// selected = true;
			// } else {
			// selected = false;
			// }
			// break;
			// }
			// }
			if( selected )
			{
				if( selectRegion != null )
				{
					// appbarTabSelectBgNinePatch.draw(batch, x, y, width, height);
					batch.draw(
							selectRegion ,
							x + ( this.width - selectRegion.getRegionWidth() ) / 2 ,
							y + ( this.height - selectRegion.getRegionHeight() ) / 2 ,
							selectRegion.getRegionWidth() ,
							selectRegion.getRegionHeight() );
				}
			}
			else
			{
				if( normalRegion != null )
				{
					batch.draw(
							normalRegion ,
							x + ( this.width - normalRegion.getRegionWidth() ) / 2 ,
							y + ( this.height - selectRegion.getRegionHeight() ) / 2 ,
							normalRegion.getRegionWidth() ,
							normalRegion.getRegionHeight() );
				}
			}
			// appbarTabDividerNinePatch.draw(batch, x + width, y,
			// appbarTabDividerNinePatch.getTotalWidth(), height);
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			// TODO Auto-generated method stub
			ArrayList<Plugin> plugins = TabPluginManager.getInstance().getPluginList();
			for( Plugin plugin : plugins )
			{
				if( plugin.mSelected == true )
				{
					if( !plugin.getTabPluginMetaData().packageName.equals( mPluginMetaData.packageName ) )
					{
						plugin.cancelSelected();
						break;
					}
				}
			}
			for( Plugin plugin : plugins )
			{
				if( plugin.getTabPluginMetaData().packageName.equals( mPluginMetaData.packageName ) )
				{
					plugin.mSelected = true;
					break;
				}
			}
			return true;
		}
		
		@Override
		public void onUninstall()
		{
			// TODO Auto-generated method stub
			super.onUninstall();
			dispose();
		}
		
		@Override
		public void onThemeChanged()
		{
			// TODO Auto-generated method stub
			if( normalTexture != null )
			{
				normalTexture.changeBitmap( AppBar3D.titleToPixmap( mPluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true );
			}
			if( selectTexture != null )
			{
				selectTexture.changeBitmap( AppBar3D.titleToPixmap( mPluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_select_color , false ) , true );
			}
		}
		
		@Override
		public void dispose()
		{
			if( normalTexture != null )
			{
				normalTexture.dispose();
				normalTexture = null;
			}
			if( normalRegion != null )
			{
				if( normalRegion.getTexture() != null )
				{
					normalRegion.getTexture().dispose();
				}
				normalRegion = null;
			}
			super.dispose();
		}
		
		public void setPadding(
				float paddingLeft ,
				float paddingTop ,
				float paddingRight ,
				float paddingBottom )
		{
			// TODO Auto-generated method stub
			this.setSize( normalRegion.getRegionWidth() + paddingLeft + paddingRight , R3D.appbar_height + paddingTop + paddingBottom );
			this.setOrigin( width / 2 , height / 2 );
			viewParent.setSize( width , height );
		}
	}
	
	class TabPopMenu extends ViewGroup3D
	{
		
		private float itemWidth = 150;
		private float itemHeight = 50;
		private int itemCount = 0;
		
		public TabPopMenu(
				String name )
		{
			super( name );
			itemWidth = R3D.appbar_tab_width + R3D.appbar_more_width;
			itemHeight = R3D.appbar_tab_popitem_height;
			width = itemWidth;
			this.transform = true;
		}
		
		public void onThemeChanged()
		{
			// TODO Auto-generated method stub
			for( int i = 0 ; i < this.getChildCount() ; i++ )
			{
				View3D child = this.getChildAt( i );
				if( child instanceof TabPopItem )
				{
					( (TabPopItem)child ).onThemeChanged();
				}
			}
		}
		
		public void cancelSelect()
		{
			// TODO Auto-generated method stub
			for( int i = 0 ; i < this.getChildCount() ; i++ )
			{
				View3D child = this.getChildAt( i );
				if( child instanceof TabPopItem )
				{
					( (TabPopItem)child ).selected = false;
				}
			}
		}
		
		public void build(
				ArrayList<TabTitle3D> hideTabTitles )
		{
			// TODO Auto-generated method stub
			this.removeAllViews();
			itemCount = hideTabTitles.size();
			itemWidth = R3D.appbar_tab_width + R3D.appbar_more_width;
			itemHeight = R3D.appbar_tab_popitem_height;
			width = itemWidth;
			height = itemCount * itemHeight;
			this.originX = width;
			this.originY = height;
			x = Utils3D.getScreenWidth() - width;
			y = Utils3D.getScreenHeight() - height - R3D.appbar_height;
			for( int i = 0 ; i < hideTabTitles.size() ; i++ )
			{
				TabPopItem item = new TabPopItem( hideTabTitles.get( i ) );
				item.y = height - ( i + 1 ) * itemHeight;
				item.x = 0;
				item.build();
				this.addView( item );
			}
		}
		
		public void ClickItem(
				int index )
		{
			final int itemCount = 4;
			for( int i = 0 ; i < itemCount ; i++ )
			{
				if( i == popItem )
					continue;
				if( index == 0 )
				{
					popItem = i;
					break;
				}
				else
					index--;
			}
			onContentTableChange( popItem );
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			tab_pop_bg.draw( batch , x , y - 6 , width , height + 6 );
			super.draw( batch , parentAlpha );
		}
		
		@Override
		public boolean onTouchDown(
				float x ,
				float y ,
				int pointer )
		{
			if( x >= 0 && x < width && y >= 0 && y < height )
			{
				for( int i = 0 ; i < this.getChildCount() ; i++ )
				{
					View3D child = this.getChildAt( i );
					if( child instanceof TabPopItem )
					{
						( (TabPopItem)child ).selected = false;
					}
				}
				super.onTouchDown( x , y , pointer );
				return false;
			}
			else
			{
				return true;
			}
		}
		
		@Override
		public boolean onTouchUp(
				float x ,
				float y ,
				int pointer )
		{
			if( x >= 0 && x < width && y >= 0 && y < height )
			{
				super.onTouchUp( x , y , pointer );
				return false;
			}
			else
			{
				hide();
				return true;
			}
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			if( x >= 0 && x < width && y >= 0 && y < height )
			{
				for( int i = 0 ; i < this.getChildCount() ; i++ )
				{
					View3D child = this.getChildAt( i );
					if( child instanceof TabPopItem )
					{
						( (TabPopItem)child ).selected = false;
					}
				}
				super.onClick( x , y );
				listener.onTabChange( TAB_MORE );
				tabIndicator.onTabChange( TAB_MORE );
				SendMsgToAndroid.sysPlaySoundEffect();
				hide();
				return true;
			}
			else
			{
				hide();
				return true;
			}
		}
		
		public void show()
		{
			this.bringToFront();
			super.show();
			this.requestFocus();
		}
		
		public void hide()
		{
			this.releaseFocus();
			super.hide();
			appTab.select();
			if( pluginTab != null )
			{
				pluginTab.select();
			}
			if( tabIndicator.tabId == AppBar3D.TAB_CONTENT || tabIndicator.tabId == AppBar3D.TAB_WIDGET )
			{
				moreButton.cancelSelect();
			}
			else
			{
				if( pluginTab != null )
				{
					for( int i = 0 ; i < pluginTab.getChildCount() ; i++ )
					{
						TabTitle3D tabTitle = (TabTitle3D)pluginTab.getChildAt( i );
						if( tabTitle.pluginMetaData.pluginId.equals( lastSelectPluginId ) )
						{
							moreButton.cancelSelect();
							break;
						}
					}
				}
			}
		}
		
		public void hideNoAnim()
		{
			this.visible = false;
			this.touchable = false;
		}
		
		class TabPopItem extends ViewGroup3D
		{
			
			public TabTitle3D tabTitle3D;
			private BitmapTexture normalTexture;
			private BitmapTexture selectTexture;
			private View3D normalTitleView;
			private View3D selectTitleView;
			private View3D dividerView;
			private boolean selected = false;
			
			public TabPopItem(
					TabTitle3D tabTitle3D )
			{
				super( "tabpopitem" );
				this.tabTitle3D = tabTitle3D;
				width = itemWidth;
				height = itemHeight;
				transform = true;
			}
			
			public void build()
			{
				this.removeAllViews();
				normalTexture = new BitmapTexture( AppBar3D.titleToPixmap( tabTitle3D.pluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_pop_color , false ) , true );
				normalTitleView = new View3D( "normal" , normalTexture );
				normalTitleView.x = ( width - normalTitleView.width ) / 2;
				normalTitleView.y = ( height - normalTitleView.height ) / 2;
				this.addView( normalTitleView );
				selectTexture = new BitmapTexture( AppBar3D.titleToPixmap( tabTitle3D.pluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_pop_select_color , false ) , true );
				selectTitleView = new View3D( "select" , selectTexture );
				selectTitleView.x = ( width - selectTitleView.width ) / 2;
				selectTitleView.y = ( height - selectTitleView.height ) / 2;
				selectTitleView.hide();
				this.addView( selectTitleView );
				View3D itemDivider = new View3D( "itemtitle" );
				itemDivider.setBackgroud( new NinePatch( R3D.findRegion( "appbar-tab-pop-item-divider" ) ) );
				itemDivider.setPosition( 0 , itemHeight - 2 );
				itemDivider.setSize( width , 2 );
				this.addView( itemDivider );
			}
			
			@Override
			public void draw(
					SpriteBatch batch ,
					float parentAlpha )
			{
				// TODO Auto-generated method stub
				// selected=true;
				if( selected )
				{
					tab_pop_item_select_bg.draw( batch , x , y , width , height );
					normalTitleView.hide();
					selectTitleView.show();
				}
				else
				{
					normalTitleView.show();
					selectTitleView.hide();
				}
				super.draw( batch , parentAlpha );
			}
			
			@Override
			public boolean onClick(
					float x ,
					float y )
			{
				// TODO Auto-generated method stub
				Log.v( "tabpopitem" , "tab tabpopitem onClick :" + tabTitle3D.pluginMetaData.cnName + " " + tabTitle3D.pluginMetaData.pluginId );
				lastSelectedTabTitle = tabTitle3D;
				lastSelectPluginId = tabTitle3D.pluginMetaData.pluginId;
				selected = true;
				return tabTitle3D.onClick( tabTitle3D.width / 2 , tabTitle3D.height / 2 );
			}
			
			@Override
			public boolean onTouchDown(
					float x ,
					float y ,
					int pointer )
			{
				// TODO Auto-generated method stub
				selected = true;
				return tabTitle3D.onTouchDown( tabTitle3D.width / 2 , tabTitle3D.height / 2 , pointer );
			}
			
			@Override
			public boolean onTouchUp(
					float x ,
					float y ,
					int pointer )
			{
				// TODO Auto-generated method stub
				selected = true;
				return tabTitle3D.onTouchUp( tabTitle3D.width / 2 , tabTitle3D.height / 2 , pointer );
			}
			
			public void onThemeChanged()
			{
				// TODO Auto-generated method stub
				if( normalTexture != null )
				{
					normalTexture.changeBitmap( AppBar3D.titleToPixmap( tabTitle3D.pluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_pop_color , false ) , true );
				}
				if( selectTexture != null )
				{
					selectTexture.changeBitmap( AppBar3D.titleToPixmap( tabTitle3D.pluginMetaData.pluginTitle , (int)R3D.appbar_height , false , R3D.appbar_tab_pop_select_color , false ) , true );
				}
			}
		}
	}
	
	class AppBarMoreButton extends View3D
	{
		
		public boolean selected = false;
		private TextureRegion normalRegion = null;
		private TextureRegion selectRegion = null;
		
		public AppBarMoreButton(
				String name )
		{
			super( name );
			normalRegion = R3D.findRegion( "appbar-more-button" );
			selectRegion = R3D.findRegion( "appbar-more-button-select" );
			width = R3D.appbar_more_width;
			// width = this.region.getRegionWidth();
			height = R3D.appbar_height;
			//			 this.setBackgroud(new NinePatch(new
			//					 BitmapTexture(BitmapFactory.decodeResource(iLoongLauncher.getInstance().getResources(),
			//					 R.drawable.red2))));
			if( menu == null || !menu.isVisible() )
			{
				if( DefaultLayout.show_home_button )
				{
					x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_width - R3D.appbar_menu_right * 2;
				}
				else
				{
					x = Utils3D.getScreenWidth() - width;
				}
				// x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
			}
			else
			{
				if( DefaultLayout.show_home_button )
				{
					x = Utils3D.getScreenWidth() - width - 2 * R3D.appbar_menu_width - 3 * R3D.appbar_home_right;
				}
				else
				{
					x = Utils3D.getScreenWidth() - width - 2 * R3D.appbar_menu_width - R3D.appbar_menu_right;
				}
			}
			y = ( R3D.appbar_height ) / 2 - height / 2;
			this.setOrigin( width / 2 , height / 2 );
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
			// if (DefaultLayout.appbar_show_userapp_list == true) {
			// appList.setMode(AppList3D.APPLIST_MODE_USERAPP);
			// this.hide();
			// } else {
			// appList.setMode(AppList3D.APPLIST_MODE_NORMAL);
			// if (DefaultLayout.startKeyAction() == 0)
			// viewParent.onCtrlEvent(appList,
			// AppList3D.APP_LIST3D_KEY_BACK);
			// }
			// SendMsgToAndroid.sysPlaySoundEffect();
			if( tabPopMenu != null )
			{
				if( tabPopMenu.isVisible() )
				{
					tabPopMenu.hide();
				}
				else
				{
					tabPopMenu.show();
				}
			}
			// selected = true;
			appTab.cancelSelect();
			pluginTab.cancelSelected();
			return true;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// TODO Auto-generated method stub
			if( false )
			{
				// appbarTabSelectBgNinePatch.draw(batch, x, y, width, height);
				batch.draw(
						selectRegion ,
						x + ( this.width - selectRegion.getRegionWidth() ) / 2 ,
						y + ( this.height - selectRegion.getRegionHeight() ) / 2 ,
						selectRegion.getRegionWidth() ,
						selectRegion.getRegionHeight() );
			}
			else
			{
				batch.draw(
						normalRegion ,
						x + ( this.width - normalRegion.getRegionWidth() ) / 2 ,
						y + ( this.height - normalRegion.getRegionHeight() ) / 2 ,
						normalRegion.getRegionWidth() ,
						normalRegion.getRegionHeight() );
			}
		}
		
		public void cancelSelect()
		{
			selected = false;
			tabPopMenu.cancelSelect();
		}
		
		public void setMode(
				int mode )
		{
			if( mode == AppList3D.APPLIST_MODE_NORMAL )
			{
				if( showMoreButton )
				{
					this.show();
				}
				else
				{
					moreButton.hide();
					tabPopMenu.hide();
				}
			}
			else
			{
				this.hide();
			}
		}
	}
}
