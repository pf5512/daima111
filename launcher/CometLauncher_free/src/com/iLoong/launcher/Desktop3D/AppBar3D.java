package com.iLoong.launcher.Desktop3D;


import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.media.MediaList;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.thirdParty.analytics.umeng.CoCoMobclickAgent;


public class AppBar3D extends ViewGroup3D implements PageScrollListener , TweenCallback
{
	
	public AppTab tab;
	public AppTabIndicator tabIndicator;
	private AppMenuButton menu;
	//private AppContentButton content;
	private AppHomeButton home;
	public AppPopMenu popMenu;
	public AppPopMenu2 popMenu2;
	private OnTabChangeListener listener;
	private AppList3D appList;
	private MediaList mediaList;//zqh on 05-12-2012
	private NinePatch indicatorBg;
	public static TextureRegion tr_appbarBg = null;
	public static TextureRegion tr_appItemBg = null;
	public NinePatch rm_popup_bg = new NinePatch( R3D.findRegion( "appbar-content-pop-bg" ) , 6 , 6 , 6 , 6 );
	// public static TextureRegion itemBg;
	public static final int TAB_CONTENT = 0;
	public static final int TAB_WIDGET = 1;
	public static final int TEXT_ALIGN_LEFT = 0;
	public static final int TEXT_ALIGN_CENTER = 1;
	public static final int TEXT_ALIGN_RIGHT = 2;
	public static int prePopItem = 0;
	public static int popItem = 3;
	public TextureRegion appTexture = R3D.findRegion( "appbar-tab-item-app" );
	public TextureRegion mediaTexture = R3D.findRegion( "appbar-tab-item-media" );
	public TextureRegion albumTexture = R3D.findRegion( "appbar-tab-item-album" );
	public TextureRegion musicTexture = R3D.findRegion( "appbar-tab-item-music" );
	public TextureRegion happTexture = R3D.findRegion( "appbar-tab-highlight-item-music" );
	public TextureRegion hmediaTexture = R3D.findRegion( "appbar-tab-highlight-item-media" );
	public TextureRegion halbumTexture = R3D.findRegion( "appbar-tab-highlight-item-album" );
	public TextureRegion hmusicTexture = R3D.findRegion( "appbar-tab-highlight-item-music" );
	public AppHost3D appHost;
	
	public AppBar3D(
			String name )
	{
		super( name );
		x = 0;
		y = Utils3D.getScreenHeight() - R3D.appbar_height;
		width = Utils3D.getScreenWidth();
		height = R3D.appbar_height;
		this.originY = height / 2;
		tab = new AppTab( "apptab" );
		this.addView( tab );
		tabIndicator = new AppTabIndicator( "apptabindicator" );
		this.addView( tabIndicator );
		if( DefaultLayout.enable_explorer )
		{
			popMenu = new AppPopMenu( "apppopmenu" );
			popMenu.hideNoAnim();
			//			content = new AppContentButton("appcontentbutton");
			//			this.addView(content);	
		}
		if( DefaultLayout.show_home_button )
		{
			home = new AppHomeButton( "apphomebutton" );
			//home.setScale(1.5f, 1.5f);
			this.addView( home );
		}
		//		if (!DefaultLayout.appbar_no_menu) {
		//			menu = new AppMenuButton("appmenubutton");
		//			this.addView(menu);			
		//		}
		if( indicatorBg == null )
		{
			Bitmap bmp = ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-indicator.png" );
			if( bmp.getConfig() != Config.ARGB_8888 )
			{
				bmp = bmp.copy( Config.ARGB_8888 , false );
			}
			Texture t = new BitmapTexture( bmp );
			//			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			indicatorBg = new NinePatch( new TextureRegion( t ) , 1 , 1 , 1 , 1 );
			bmp.recycle();
		}
		// itemBg = new NinePatch(R3D.findRegion("app-item-bg"),3,3,3,3);
		this.transform = true;
		if( tr_appbarBg == null )
		{
			//xiatian start	//Mainmenu Bg
			//xiatian del start	
			//			String bg_name = null;
			//			if (DefaultLayout.mainmenu_add_black_ground)
			//				bg_name = "theme/pack_source/appbar-bg-black.png";
			//			else
			//				bg_name = "theme/pack_source/appbar-bg.png";
			//xiatian del end
			//xiatian add start
			String bg_name = "theme/pack_source/appbar-bg.png";
			//xiatian add end
			//xiatian end
			Bitmap bmp = ThemeManager.getInstance().getBitmap( bg_name );
			if( bmp.getConfig() != Config.ARGB_8888 )
			{
				bmp = bmp.copy( Config.ARGB_8888 , false );
			}
			Texture t = new BitmapTexture( bmp );
			//			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			tr_appbarBg = new TextureRegion( t );
			bmp.recycle();
		}
		Bitmap appItemBg = ThemeManager.getInstance().getBitmap( "theme/pack_source/app-item-bg.png" );
		if( tr_appItemBg == null )
			tr_appItemBg = new TextureRegion( new BitmapTexture( appItemBg ) );
		appItemBg.recycle();
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
			case AppHost3D.CONTENT_TYPE_PHOTO_BUCKET:
				appHost.showPhotoBuckets();
				mediaList.show();
				appList.justHide();
				tab.changeRegion( R3D.findRegion( "appbar-tab-navigator-photobucket" ) );
				//友盟统计
				CoCoMobclickAgent.add( "CONTENT_TYPE_PHOTO_BUCKET" );
				break;
			case AppHost3D.CONTENT_TYPE_AUDIO_ALBUM:
				appHost.showAudioAlbums();
				mediaList.show();
				appList.justHide();
				tab.changeRegion( R3D.findRegion( "appbar-tab-navigator-audioalbum" ) );
				//友盟统计
				CoCoMobclickAgent.add( "CONTENT_TYPE_AUDIO_ALBUM" );
				break;
			case AppHost3D.CONTENT_TYPE_APP:
				AppHost3D.currentContentType = AppHost3D.CONTENT_TYPE_APP;
				tab.changeRegion( tab.appTitle );
				appList.show();
				mediaList.hide();
				break;
			case AppHost3D.CONTENT_TYPE_VIDEO:
				tab.changeRegion( R3D.findRegion( "appbar-tab-navigator-video" ) );
				appHost.showVideos();
				mediaList.show();
				appList.justHide();
				//友盟统计
				CoCoMobclickAgent.add( "CONTENT_TYPE_VIDEO" );
				break;
		}
		//Log.v("resMan", "preContent:"+getPreviousContent());
	}
	
	public void setAppList(
			AppList3D appList )
	{
		this.appList = appList;
	}
	
	public void setMediaList(
			MediaList mediaList )
	{
		this.mediaList = mediaList;
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
				//					paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
				//					canvas.drawText(title, x, titleY, paint);				
			}
		}
		paint.clearShadowLayer();
		paint.setColor( color );
		canvas.drawText( title , x , titleY , paint );
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
				//					paint.setShadowLayer(2f, 0.0f, 1.0f, 0xcc000000);
				//					canvas.drawText(title, x, titleY, paint);				
			}
		}
		paint.clearShadowLayer();
		paint.setColor( color );
		canvas.drawText( title , x , titleY , paint );
		//saveBmp(bmp,title);
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
		//saveBmp(bmp,title);
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
		
		public AppTab(
				String name )
		{
			super( name );
			x = 0;
			y = 0;
			width = Utils3D.getScreenWidth();
			height = R3D.appbar_height;
			this.originY = height / 2;
			widgetTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_widget , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );//R3D.findRegion("appbar-tab-widget");	
			contentTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_app , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );//R3D.findRegion("appbar-tab-app");
			appTitle = contentTitle;
			tabArrow = new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/appbar-tab-arrow.png" ) , true ) );//R3D.findRegion("appbar-tab-arrow");
			if( DefaultLayout.enable_explorer )
			{
				tabContentWidth = 5 * R3D.appbar_menuitem_paddingleft + musicTexture.getRegionWidth() * Utils3D.getDensity() / 1.5f + getTabContentWidth();
			}
			else
				tabContentWidth = contentTitle.getRegionWidth() + 2 * R3D.appbar_tab_padding;
			tabWidgetWidth = widgetTitle.getRegionWidth() + 2 * R3D.appbar_tab_padding;
			//teapotXu add start for Folder in Mainmenu
			if( DefaultLayout.mainmenu_folder_function == true )
			{
				uninstallTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_edit_mode , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );
			}
			else
			{
				uninstallTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_uninstall , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );//R3D.findRegion("appbar-"+R3D.getString(RR.string.uninstall_app));
			}
			//teapotXu add end for Folder in Mainmenu
			//			downloadAppTitle= R3D.findRegion("appbar-"+R3D.getString(RR.string.download_apps));
			hideTitle = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap( R3D.appbar_tab_hide , (int)R3D.appbar_height , false , R3D.appbar_tab_color , false ) , true ) );//R3D.findRegion("appbar-"+R3D.getString(RR.string.hide_icon));
			naviBackX = R3D.appbar_tab_padding;
			naviBackY = height / 2 - R3D.appbar_menu_height / 2;
			naviBack = R3D.findRegion( "appbar-navi-back" );
		}
		
		public float getTabContentWidth()
		{
			float res = Math.max( contentTitle.getRegionWidth() , R3D.findRegion( "appbar-tab-navigator-audioalbum" ).getRegionWidth() );
			res = Math.max( R3D.findRegion( "appbar-tab-navigator-photobucket" ).getRegionWidth() , res );
			res = Math.max( R3D.findRegion( "appbar-tab-navigator-video" ).getRegionWidth() , res );
			return res;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// super.draw(batch, parentAlpha);
			//			int srcBlendFunc = 0,dstBlendFunc = 0;
			//			if(DefaultLayout.blend_func_dst_gl_one){
			//				/*获取获取混合方式*/
			//				srcBlendFunc = batch.getSrcBlendFunc();
			//				dstBlendFunc = batch.getDstBlendFunc();
			//				if(srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE)
			//					batch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			//			}
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			int naviBackOffsetX = R3D.appbar_menu_width / 2;
			float naviBackWidth = naviBack.getRegionWidth() * Utils3D.getDensity() / 1.5f;
			float naviBackHeight = naviBack.getRegionHeight() * Utils3D.getDensity() / 1.5f;
			if( DefaultLayout.appbar_show_userapp_list == true )
			{
				naviBackOffsetX = R3D.appbar_menu_width / 4;
			}
			if( mode == AppList3D.APPLIST_MODE_NORMAL )
			{
				if( DefaultLayout.enable_explorer )
				{
					batch.draw(
							tabArrow ,
							R3D.appbar_menuitem_paddingleft + ( musicTexture.getRegionWidth() * Utils3D.getDensity() / 1.5f - tabArrow.getRegionWidth() ) / 2 ,
							y + ( height - tabArrow.getRegionHeight() ) / 2 );
					batch.draw( contentTitle , R3D.appbar_menuitem_paddingleft + musicTexture.getRegionWidth() * Utils3D.getDensity() / 1.5f , y + ( height - contentTitle.getRegionHeight() ) / 2 );
				}
				else
				{
					batch.draw( contentTitle , R3D.appbar_padding_left + R3D.appbar_tab_padding , y + ( height - contentTitle.getRegionHeight() ) / 2 );
				}
				if( !appList.mHideMainmenuWidget )
					batch.draw(
							widgetTitle ,
							tabContentWidth + R3D.appbar_padding_left + R3D.findRegion( "appbar-divider" ).getRegionWidth() + R3D.appbar_tab_padding ,
							y + ( height - widgetTitle.getRegionHeight() ) / 2 );
			}
			else if( mode == AppList3D.APPLIST_MODE_HIDE )
			{
				//				batch.draw(naviBack, naviBackX +naviBackOffsetX
				//						- naviBackWidth / 2, naviBackY
				//						+ (R3D.appbar_menu_height - naviBackHeight)
				//						/ 2, naviBackWidth,
				//						naviBackHeight);
				batch.draw( hideTitle , R3D.appbar_tab_padding , y + ( height - hideTitle.getRegionHeight() ) / 2 );
			}
			else if( mode == AppList3D.APPLIST_MODE_UNINSTALL )
			{
				//				batch.draw(naviBack, naviBackX + naviBackOffsetX
				//						- naviBackWidth / 2, naviBackY
				//						+ (R3D.appbar_menu_height - naviBackHeight)
				//						/ 2, naviBackWidth,
				//						naviBackHeight);
				batch.draw( uninstallTitle , R3D.appbar_tab_padding , y + ( height - uninstallTitle.getRegionHeight() ) / 2 );
			}
			else if( mode == AppList3D.APPLIST_MODE_USERAPP )
			{
				//				batch.draw(naviBack, naviBackX + naviBackOffsetX
				//						- naviBackWidth / 2, naviBackY
				//						+ (R3D.appbar_menu_height - naviBackHeight)
				//						/ 2, naviBackWidth,
				//						naviBackHeight);
				batch.draw( downloadAppTitle , R3D.appbar_tab_padding , y + ( height - downloadAppTitle.getRegionHeight() ) / 2 );
			}
			//			if(DefaultLayout.blend_func_dst_gl_one){
			//				batch.setBlendFunction(srcBlendFunc, dstBlendFunc);
			//			}
		}
		
		@Override
		public boolean onClick(
				float x ,
				float y )
		{
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
					else if( popMenu != null && DefaultLayout.enable_explorer )
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
					return true;
				}
				else if( appList.mHideMainmenuWidget )
					return false;
				else if( x >= tabContentWidth + R3D.appbar_padding_left && x < tabContentWidth + R3D.appbar_padding_left + tabWidgetWidth )
				{
					if( tabIndicator.tabId != TAB_WIDGET )
					{
						listener.onTabChange( TAB_WIDGET );
						tabIndicator.onTabChange( TAB_WIDGET );
					}
					return true;
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
	}
	
	class AppTabIndicator extends View3D
	{
		
		public int tabId = TAB_CONTENT;
		
		public AppTabIndicator(
				String name )
		{
			super( name );
			x = 0;
			y = 0;
			width = Utils3D.getScreenWidth();
			height = R3D.appbar_indicator_height;
			this.originY = height / 2;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			// super.draw(batch, parentAlpha);
			float w = this.getUser() / ( tab.tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() );
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			// batch.draw(R3D.findRegion("appbar-indicator"), 0,
			//			 0,width,height/4);
			float thin_height = height / 4;
			indicatorBg.draw( batch , 0 , 0 , width , thin_height );
			if( !appList.mHideMainmenuWidget )
				indicatorBg.draw( batch , this.getUser() , 0 , ( 1 - w ) * tab.tabContentWidth + w * tab.tabWidgetWidth , height * 3 / 4 );
			// batch.draw(R3D.findRegion("appbar-indicator"), this.getUser(), 0
			// ,R3D.appbar_tab_width,height*3/4);
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
				//				if(content != null)
				//					content.show();
				// this.setUser(R3D.appbar_tab_width);
				if( DefaultLayout.appbar_show_userapp_list == true )
				{
					if( home != null )
					{
						home.show();
					}
				}
				this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0f ,//0.5f,
						0 ,
						0 ,
						0 );
			}
			else if( tabId == TAB_WIDGET )
			{
				if( menu != null )
					menu.hide();
				//				if(content != null)
				//					content.hide();
				if( DefaultLayout.appbar_show_userapp_list == true )
				{
					if( home != null )
					{
						home.hide();
					}
				}
				stopTween();
				// this.setUser(0);
				this.startTween( View3DTweenAccessor.USER , Cubic.OUT , 0f ,//0.5f
						tab.tabContentWidth + R3D.findRegion( "appbar-divider" ).getRegionWidth() ,
						0 ,
						0 );
			}
		}
	}
	
	class AppHomeButton extends View3D
	{
		
		public AppHomeButton(
				String name )
		{
			super( name );
			Bitmap appBitmap = null;
			float ImagScale = iLoongLauncher.getInstance().getResources().getDisplayMetrics().widthPixels / 720.0f;//iLoongLauncher.getInstance().getResources().getDisplayMetrics().density/1.5f;
			Log.v( "Home" , "ImagScale " + ImagScale );
			if( ImagScale < 1 )
			{
				ImagScale = 1.0f;
			}
			else if( ImagScale > 2 )
			{
				ImagScale = 1.5f;
			}
			try
			{
				appBitmap = BitmapFactory.decodeStream( iLoongLauncher.getInstance().getAssets().open( "theme/pack_source/app-home-button.png" ) );
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
			// (float)(R3D.appbar_menu_width/appBitmap.getWidth())*ImagScale
			appBitmap = Tools.resizeBitmap( appBitmap , ImagScale );
			//region=new TextureRegion();
			if( DefaultLayout.getMMSettingValue() == 0 )
			{
				Texture texture = new BitmapTexture( appBitmap , true );
				region = new TextureRegion( texture );//R3D.findRegion("app-home-button");
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
			this.setOrigin( this.width / 2.0f , this.height / 2 / 0f );
			if( menu == null || !menu.isVisible() )
			{
				x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
			}
			else
			{
				x = Utils3D.getScreenWidth() - 2 * width - R3D.appbar_menu_right - R3D.appbar_home_right;
			}
			y = ( R3D.appbar_height ) / 2 - height / 2;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			if( menu == null || !menu.isVisible() )
			{
				x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
			}
			else
			{
				x = Utils3D.getScreenWidth() - 2 * width - R3D.appbar_menu_right - R3D.appbar_home_right;
			}
			float regionWidth = region.getRegionWidth();
			float regionHeight = region.getRegionHeight();
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			//super.draw(batch, parentAlpha);
			batch.draw( region , x + width / 2 - regionWidth / 2 , y + height / 2 - regionHeight / 2 , regionWidth , regionHeight );
		}
		
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
			return true;
		}
	}
	
	class AppMenuButton extends View3D
	{
		
		public AppMenuButton(
				String name )
		{
			super( name );
			region = R3D.findRegion( "app-menu-button" );
			width = region.getRegionWidth();//R3D.appbar_menu_width;
			height = R3D.appbar_height;//region.getRegionHeight();//R3D.appbar_menu_height;
			x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right;
			y = 0;//(R3D.appbar_height) / 2 - height / 2;
		}
		
		@Override
		public void draw(
				SpriteBatch batch ,
				float parentAlpha )
		{
			//			float regionWidth = region.getRegionWidth()*Utils3D.getDensity()/1.5f;
			//			float regionHeight = region.getRegionHeight()*Utils3D.getDensity()/1.5f;
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			//			batch.draw(region, x + width / 2 - regionWidth / 2, y + height / 2
			//					- regionHeight / 2,regionWidth,regionHeight);
			batch.draw( region , x , y + height / 2 - region.getRegionHeight() / 2 , width , region.getRegionHeight() );
			//			setBackgroud(new NinePatch(new Texture(Gdx.files.internal("bg.png"))));
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
			//zqh modifies on 12-05-2012
			if( !DefaultLayout.enable_explorer )
			{
				//teapotXu add start for Folder in Mainmenu
				if( DefaultLayout.mainmenu_folder_function == true )
				{
					addItem( R3D.findRegion( "app-uninstall-button" ) , R3D.getString( RR.string.edit_mode ) );
				}
				else
				{
					addItem( R3D.findRegion( "app-uninstall-button" ) , R3D.getString( RR.string.uninstall_app ) );
				}
				//				addItem(R3D.findRegion("app-uninstall-button"),
				//						R3D.getString(RR.string.uninstall_app));
				//teapotXu add end for Folder in Mainmenu				
				addItem( R3D.findRegion( "app-hide-button" ) , R3D.getString( RR.string.hide_icon ) );
				addItem( R3D.findRegion( "app-sort-button" ) , R3D.getString( RR.string.sort_icon ) );
			}
			else
			{
				addItem( musicTexture , "appbar-pop-audioalbum" );
				addItem( albumTexture , "appbar-pop-photobucket" );
				addItem( mediaTexture , "appbar-pop-video" );
			}
			if( DefaultLayout.enable_explorer )
			{
				width = 5 * R3D.appbar_menuitem_paddingleft + musicTexture.getRegionWidth() * Utils3D.getDensity() / 1.5f + getTabContentWidth();
				itemWidth = width;
				height = this.getChildCount() * itemHeight;
				x = 0;
				y = Utils3D.getScreenHeight() - height - R3D.appbar_height;
			}
			else
			{
				width = itemWidth;
				height = this.getChildCount() * itemHeight;
				x = Utils3D.getScreenWidth() - width - R3D.appbar_menu_right / 3;
				y = Utils3D.getScreenHeight() - height - R3D.appbar_height;
			}
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
			super.clear();
			final int itemCount = 4;
			for( int i = 0 ; i < itemCount ; i++ )
			{
				if( i == item )
					continue;
				switch( i )
				{
					case 0:
						addItem( musicTexture , "appbar-pop-audioalbum" );
						break;
					case 1:
						addItem( albumTexture , "appbar-pop-photobucket" );
						break;
					case 2:
						addItem( mediaTexture , "appbar-pop-video" );
						break;
					case 3:
						addItem( appTexture , "appbar-pop-app" );
						break;
				}
			}
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
			//itemTitle.setSize(itemWidth - itemTitle.x, itemHeight);
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
			if( DefaultLayout.enable_explorer )
			{
				rm_popup_bg.draw( batch , x , y , width , height );
			}
			else
			{
				int h = tr_appItemBg.getRegionHeight();
				for( int i = 0 ; i < height * scaleY / h ; i++ )
				{
					batch.draw( tr_appItemBg , x + width * ( 1 - scaleX ) , y + height - ( i + 1 ) * h , width * scaleX , h );
					//batch.draw(tr, x+width*(1-scaleX), y+height-(i+1)*h, width*scaleX, h);
				}
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
			//zqh modifies on 12-05-2012
			if( !DefaultLayout.enable_explorer )
			{
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
					SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId );
				}
			}
			else
			{
				ClickItem( index );
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
		//TextureRegion tr = R3D.findRegion("appbar-bg");
		//int w = tr.getRegionWidth();
		int w = tr_appbarBg.getRegionWidth();
		for( int i = 0 ; i < width / w + 1 ; i++ )
		{
			batch.draw( tr_appbarBg , i * w , y , w , height );
			//batch.draw(tr, i*w, y, w, height);
		}
		//		batch.draw(R3D.findRegion("appbar-bg"), 0, y, width, height);
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
		if( current < AppList3D.appPageCount || AppList3D.appPageCount == 0 )
			tabIndicator.onTabChange( TAB_CONTENT );
		else
			tabIndicator.onTabChange( TAB_WIDGET );
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
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "debug" , " x: " + x + " y: " + y );
		return super.onTouchDown( x , y , pointer );
	}
}
