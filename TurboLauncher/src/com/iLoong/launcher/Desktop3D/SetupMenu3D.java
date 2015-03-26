package com.iLoong.launcher.Desktop3D;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.view.KeyEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class SetupMenu3D extends ViewGroup3D
{
	
	public static final int POPMENU_STYLE_ORIGINAL = 0;
	public static final int POPMENU_STYLE_ANDROID4 = 1;
	public String SETUPMENU_FOLDERNAME = "setupmenu_android4/";
	public static final int MSG_HIDE_APPLIST_FOCUS = 0;
	private int itemWidth = 150;
	private int itemHeight = 50;
	public int mcolumns = 0;
	public int mrows = 0;
	public static NinePatch appItemBgFrame = null;
	public static TextureRegion appItemBgLine1 = null;
	public static TextureRegion appItemBgLine2 = null;
	public static NinePatch popmenu_bg = null;
	private ArrayList<SetupTabMenu> mTabMenus = new ArrayList<SetupTabMenu>();
	private HashMap<Integer , ArrayList<SetupMenuItem>> mMenuItems = new HashMap<Integer , ArrayList<SetupMenuItem>>();
	public static boolean isVisible = false;
	// public NinePatch menuFocus = new
	// NinePatch(R3D.findRegion("icon_focus_popmenu"),20,20,20,20);
	public static int focusItem = 0;
	public static int preFocusItem = 0;
	public static boolean origin = false;
	private int linewidth = 0;
	private float anim_alpha = 0;
	private ArrayList<View3D> allChildren = new ArrayList<View3D>();//zjp
	private List<View3D> newlist = new ArrayList<View3D>();
	
	public SetupMenu3D(
			String name )
	{
		super( name );
		if( DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4 )
		{
			SETUPMENU_FOLDERNAME = "setupmenu_android4/";
		}
		else
		{
			SETUPMENU_FOLDERNAME = "setupmenu/";
		}
		//		SetupMenuActions.getInstance().init(iLoongLauncher.getInstance());
		loadSetupMenuXml();
		layout();
	}
	
	public void loadSetupMenuXml()
	{
		SetupTabMenu smenu;
		try
		{
			mrows = iLoongLauncher.getInstance().getResources().getInteger( RR.integer.setup_menu_row );
			mcolumns = iLoongLauncher.getInstance().getResources().getInteger( RR.integer.setup_menu_columns );
			String[] menus;
			if( DefaultLayout.setupmenu_yitong )
			{
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenu_eton );
			}
			else
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenuAll );
			ArrayList<String> menulistall = new ArrayList<String>();
			ArrayList<String> menulistallid = new ArrayList<String>();
			for( int i = 0 ; i < menus.length ; i++ )
			{
				menulistall.add( menus[i] );
				String[] tmp = menus[i].split( "," );
				menulistallid.add( tmp[0] );
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( DefaultLayout.setupmenu_show_theme )
				{
					// teapotXu add start for doov special customization
					if( DefaultLayout.enable_doov_spec_customization == true )
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_WALLPAPER )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					else
					{
						if( !DefaultLayout.setupmenu_idle_sofwareManager_shown && Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SOFTWARE_MANAGEMENT )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					// teapotXu add end
				}
				else
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_THEME )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
					}
				}
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_LOCKER_SETTING )
				{
					menulistall.remove( i );
					menulistallid.remove( i );
					break;
				}
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( DefaultLayout.hide_desktop_setup )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_DESKTOP_SETTINGS )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
					}
				}
			}
			//teapotXu add start:  
			ArrayList<String> remove_menulist = new ArrayList<String>();
			ArrayList<String> remove_menulistallid = new ArrayList<String>();
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( ( !DefaultLayout.setupmenu_idle_wallpaper_shown && Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_WALLPAPER ) || ( !DefaultLayout.setupmenu_idle_systemWidget_shown && Integer
						.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SYSTEM_PLUG ) || ( !DefaultLayout.setupmenu_idle_systemSettings_shown && Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SYSTEM_SETTINGS ) )
				{//zjp
					remove_menulist.add( menulistall.get( i ) );
					remove_menulistallid.add( menulistallid.get( i ) );
				}
			}
			if( remove_menulist.size() > 0 )
				menulistall.removeAll( remove_menulist );
			if( remove_menulistallid.size() > 0 )
				menulistallid.removeAll( remove_menulistallid );
			//teapotXu add end
			if( DefaultLayout.desk_menu_change_SystemWidget_to_OnKeyLove )
			{
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SYSTEM_PLUG )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
			}
			else
			{
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_OnKeyLove_1 )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
			}
			if( !DefaultLayout.desk_menu_add_OnKeyLove_item )
			{
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_OnKeyLove )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
			}
			if( DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4 )
			{
				mrows = menulistall.size() - 1;
				mcolumns = 1;
			}
			SetupTabMenu menu = new SetupTabMenu();
			String[] menuAttrs = menulistall.get( 0 ).split( "," );
			menu.id = Integer.parseInt( menuAttrs[0] );
			Field f = (Field)RR.getStringClass().getDeclaredField( menuAttrs[1] );
			menu.name = R3D.getString( f.getInt( RR.getStringClass() ) );
			smenu = menu;
			mTabMenus.add( menu );
			ArrayList<SetupMenuItem> menulist = new ArrayList<SetupMenuItem>();
			mMenuItems.put( Integer.valueOf( menu.id ) , menulist );
			for( int i = 1 ; i < menulistall.size() ; i++ )
			{
				ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( smenu.id ) );
				if( menuitems != null )
				{
					String[] tmp = menulistall.get( i ).split( "," );
					SetupMenuItem mi = new SetupMenuItem();
					mi.page = smenu.id;
					mi.id = Integer.parseInt( tmp[0] );
					Field tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
					if( DefaultLayout.appbar_widgets_special_name == true )
					{
						if( tmp[1] != null && tmp[0] != null && ( tmp[0].equals( "1001" ) ) && ( tmp[1].equals( "system_widget" ) ) )
						{
							tmp[1] = "appbar_tab_widget_ex";
							tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
						}
					}
					mi.name = R3D.getString( tmpF.getInt( RR.getStringClass() ) );
					//					Log.v("","setup3D miname is " + mi.name);
					mi.icon = SETUPMENU_FOLDERNAME + tmp[2] + ".png";
					//					mi.iconbmp = ThemeManager.getInstance()
					//							.getBitmap((mi.icon));
					menuitems.add( mi );
				}
			}
		}
		catch( NumberFormatException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( NotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalArgumentException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( NoSuchFieldException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getSceneMenu(
			String pkg )
	{
		// TODO Auto-generated method stub
		int size = 0;
		Intent intent = new Intent( pkg , null );
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_META_DATA );
		if( resolveInfoList != null )
		{
			size = resolveInfoList.size();
		}
		return size;
		// Log.v("", "resolveInfoList size is " + size);
	}
	
	private void layout()
	{
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
		{
			itemWidth = Utils3D.getScreenWidth() * 12 / 15;
			itemHeight = Tools.dip2px( iLoongLauncher.getInstance() , R3D.setupmenu_android4_item_height );
		}
		else
		{
			itemWidth = Utils3D.getScreenWidth();
			itemHeight = Tools.dip2px( iLoongLauncher.getInstance() , R3D.setupmenu_square_item_height );
		}
		int menusize = mTabMenus.size();
		for( int index = 0 ; index < menusize ; index++ )
		{
			SetupTabMenu sm = mTabMenus.get( index );
			ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( sm.id ) );
			if( menuitems != null )
			{
				int micount = menuitems.size();
				if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
				{
					for( int i = micount - 1 ; i >= 0 ; i-- )
					{
						SetupMenuItem item = menuitems.get( i );
						if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/" + item.icon ) )
						{
							addItem( new TextureRegion( new BitmapTexture( BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/launcher/" + item.icon ) , true ) ) , item.name , true );
						}
						else
						{
							addItem(
									new TextureRegion( new BitmapTexture( BitmapFactory.decodeStream( Gdx.files.internal( "launcher/" + item.icon ).read() )/*Action.getBitmap(item.id)*/, true ) ) ,
									item.name ,
									true );
						}
						if( DefaultLayout.enable_edit_mode_function )
						{
							if( item.name.equals( R3D.getString( RR.string.system_widget ) ) || item.name.equals( R3D.getString( RR.string.software_manager ) ) || item.name.equals( R3D
									.getString( RR.string.screen_editor ) ) )
							{
								if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/" + item.icon.replace( ".png" , "_dis.png" ) ) )
								{
									addItem(
											new TextureRegion( new BitmapTexture(
													BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/launcher/" + item.icon.replace( ".png" , "_dis.png" ) ) ,
													true ) ) ,
											item.name ,
											false );
								}
								else
								{
									addItem( new TextureRegion( new BitmapTexture(
											BitmapFactory.decodeStream( Gdx.files.internal( "launcher/" + item.icon.replace( ".png" , "_dis.png" ) ).read() )/*Action.getBitmap(item.id)*/,
											true ) ) , item.name , false );
								}
							}
						}
					}
				}
				else
				{
					for( int i = 0 ; i < micount ; i++ )
					{
						SetupMenuItem item = menuitems.get( i );
						System.out.println( "item.id = " + item.id );
						//						Log.v("", "setup3D layout name is " + item.name);
						if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/" + item.icon ) )
						{
							addItem( BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/launcher/" + item.icon ) , item.name , i , micount , true );
						}
						else
						{
							addItem( BitmapFactory.decodeStream( Gdx.files.internal( "launcher/" + item.icon ).read() ) , item.name , i , micount , true );
						}
						if( DefaultLayout.enable_edit_mode_function )
						{
							if( item.name.equals( R3D.getString( RR.string.system_widget ) ) || item.name.equals( R3D.getString( RR.string.software_manager ) ) || item.name.equals( R3D
									.getString( RR.string.screen_editor ) ) )
							{
								if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/" + item.icon.replace( ".png" , "_dis.png" ) ) )
								{
									addItem( BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/launcher/" + item.icon.replace( ".png" , "_dis.png" ) ) , item.name , i , micount , false );
								}
								else
								{
									addItem( BitmapFactory.decodeStream( Gdx.files.internal( "launcher/" + item.icon.replace( ".png" , "_dis.png" ) ).read() ) , item.name , i , micount , false );
								}
							}
						}
					}
				}
			}
		}
		width = itemWidth;
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
			height = this.getChildCount() * itemHeight + 8 * SetupMenu.mScale;
		else
			height = 2 * itemHeight + 8 * SetupMenu.mScale;
		x = ( Utils3D.getScreenWidth() - itemWidth ) / 2.0f;
		y = -height;
		this.originX = width;
		this.originY = height;
		this.transform = true;
		Bitmap bm = null;
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
		{
			if( DefaultLayout.setupmenu_android4_with_no_icons )
			{
				bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu_android4/tanchu_applist_closed_angle.png" );
			}
			else
			{
				if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/setupmenu_android4/tanchu_applist.png" ) )
				{
					bm = BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/launcher/setupmenu_android4/tanchu_applist.png" );
				}
				else
				{
					bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu_android4/tanchu_applist.png" );
				}
			}
		}
		else
			bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/bg.png" );
		Texture t = new BitmapTexture( bm );
		if( appItemBgFrame == null )
			appItemBgFrame = new NinePatch( new TextureRegion( t ) , 2 , 2 , 10 , 2 );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/bg-1.png" );
		//bm = Tools.resizeBitmap( bm , (int)width , bm.getHeight() );
		if( appItemBgLine1 == null )
			appItemBgLine1 = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/bg-2.png" );
		linewidth = bm.getWidth();
		//bm = Tools.resizeBitmap( bm , bm.getWidth() , itemHeight );
		if( appItemBgLine2 == null )
			appItemBgLine2 = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		if( !DefaultLayout.popup_menu_no_background_shadow )
		{
			bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/pop_menu_bg.png" );
			popmenu_bg = new NinePatch( new TextureRegion( new BitmapTexture( bm ) ) , 1 , 1 , 1 , 1 );
			bm.recycle();
		}
	}
	
	private void addItem(
			Bitmap icon ,
			String title ,
			int index ,
			int size ,
			boolean enable )
	{
		ViewGroup3D item = new ViewGroup3D( title + enable );
		float itemX = 0;
		float itemY = 0;
		int cols = index < size / 2 ? size / 2 : ( size - size / 2 );
		float itemWidth = Utils3D.getScreenWidth() * 1.0f / cols;
		if( index < size / 2 )
		{
			itemY = itemHeight;
			itemX = x + index * itemWidth;
		}
		else
		{
			itemY = 0 + R3D.setupmenu_items_btw_space;
			itemX = x + ( index - size / 2 ) * itemWidth;
		}
		item.y = itemY + R3D.setupmenu_item_padding_y;
		item.x = itemX;
		item.setSize( itemWidth , itemHeight );
		final TextureRegion iconRegion = new TextureRegion();
		final TextureRegion titleRegion = new TextureRegion();
		final float[] iconPosition = new float[2];
		final float[] titlePosition = new float[2];
		if( enable )
		{
			getIconRegion( icon , title , (int)itemWidth , itemHeight - R3D.setupmenu_item_padding_y , iconRegion , iconPosition );
			getTitleRegion( iconPosition[1] , title , (int)itemWidth , itemHeight - R3D.setupmenu_item_padding_y , Color.BLACK , titleRegion , titlePosition );
		}
		else
		{
			getIconRegion( icon , title , (int)itemWidth , itemHeight - R3D.setupmenu_item_padding_y , iconRegion , iconPosition );
			getTitleRegion( iconPosition[1] , title , (int)itemWidth , itemHeight - R3D.setupmenu_item_padding_y , Color.GRAY , titleRegion , titlePosition );
		}
		View3D itemTitle = new View3D( "itemtitle" , iconRegion ) {
			
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
				int x = Math.round( this.x );
				int y = Math.round( this.y );
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				batch.draw(
						iconRegion ,
						x + iconPosition[0] ,
						y + ( height - iconPosition[1] - iconRegion.getRegionHeight() ) ,
						originX ,
						originY ,
						iconRegion.getRegionWidth() ,
						iconRegion.getRegionHeight() ,
						scaleX ,
						scaleY ,
						rotation );
				batch.draw(
						titleRegion ,
						x + titlePosition[0] ,
						y + titlePosition[1] + iconPosition[1] ,
						originX ,
						originY ,
						titleRegion.getRegionWidth() ,
						titleRegion.getRegionHeight() ,
						scaleX ,
						scaleY ,
						rotation );
			}
		};
		itemTitle.setSize( itemWidth , itemHeight );
		itemTitle.setPosition( itemWidth / 2 - itemTitle.width / 2 , itemHeight / 2 - itemTitle.height / 2 );
		item.addView( itemTitle );
		this.addView( item );
		allChildren.add( item );
		newlist.add( item );
	}
	
	private void addItem(
			TextureRegion region ,
			String title ,
			boolean enable )
	{
		ViewGroup3D item = new ViewGroup3D( title + enable );
		item.y = ( this.getChildCount() ) * itemHeight;
		item.x = 0;
		item.setSize( itemWidth , itemHeight );
		View3D itemIcon = new View3D( "itemicon" , region );
		float iconWidth = region.getRegionWidth();
		float iconHeight = region.getRegionHeight();
		if( iconHeight * Utils3D.getDensity() / 1.5f > itemHeight - Tools.dip2px( iLoongLauncher.getInstance() , 5 ) - 2 * R3D.setupmenu_icon_padding_top )
		{
			iconHeight = itemHeight - 2 * R3D.appmenu_icon_padding_top - Tools.dip2px( iLoongLauncher.getInstance() , 5 );
			iconWidth = iconHeight / region.getRegionHeight() * iconWidth;
		}
		else
		{
			iconWidth = region.getRegionWidth() * Utils3D.getDensity() / 1.5f;
			iconHeight = region.getRegionHeight() * Utils3D.getDensity() / 1.5f;
		}
		itemIcon.setSize( iconWidth , iconHeight );
		String language = Locale.getDefault().getLanguage();
		if( DefaultLayout.popmenu_gravity_right_when_special_language && ( language != null && ( language.equals( "ar" ) || language.equals( "fa" ) || language.equals( "he" ) || language
				.equals( "iw" ) || language.equals( "ug" ) ) ) )
		{
			itemIcon.setPosition( itemWidth - iconWidth - Utils3D.getScreenWidth() / 15f , ( itemHeight - iconHeight ) / 2 );
		}
		else
		{
			itemIcon.setPosition( Utils3D.getScreenWidth() / 15f , itemHeight / 2 - iconHeight / 2 );
		}
		//		itemIcon.setPosition(Utils3D.getScreenWidth() / 15f, itemHeight / 2
		//				- (region.getRegionHeight() * Utils3D.getDensity() / 1.5f) / 2);
		if( DefaultLayout.setupmenu_android4_with_no_icons )
		{
			itemIcon.width = 0;
		}
		else
		{
			item.addView( itemIcon );
		}
		int title_height = Tools.dip2px( iLoongLauncher.getInstance() , 60 );
		TextureRegion titleRegion = null;
		if( enable )
			titleRegion = new TextureRegion( new BitmapTexture( titleToPixmapEx(
					title ,
					title_height ,
					(int)( itemWidth - ( Utils3D.getScreenWidth() / 15f + itemIcon.width + Tools.dip2px( iLoongLauncher.getInstance() , 12 ) ) ) ,
					false ,
					Color.parseColor( "#000000" ) ,
					false ) , true ) );
		else
			titleRegion = new TextureRegion( new BitmapTexture( titleToPixmapEx(
					title ,
					title_height ,
					(int)( itemWidth - ( Utils3D.getScreenWidth() / 15f + itemIcon.width + Tools.dip2px( iLoongLauncher.getInstance() , 12 ) ) ) ,
					false ,
					Color.parseColor( "#808080" ) ,
					false ) ) );
		View3D itemTitle = new View3D( "itemtitle" , titleRegion );
		if( DefaultLayout.popmenu_gravity_right_when_special_language && ( language != null && ( language.equals( "ar" ) || language.equals( "fa" ) || language.equals( "he" ) || language
				.equals( "iw" ) || language.equals( "ug" ) ) ) )
		{
			if( DefaultLayout.setupmenu_android4_with_no_icons )
			{
				itemTitle.setPosition( itemWidth - iconWidth - itemTitle.width - ( Utils3D.getScreenWidth() / 15f ) , ( itemHeight - itemTitle.height ) / 2 );
			}
			else
			{
				itemTitle.setPosition(
						itemWidth - iconWidth - itemTitle.width - ( Utils3D.getScreenWidth() / 15f + Tools.dip2px( iLoongLauncher.getInstance() , 6 ) ) ,
						( itemHeight - itemTitle.height ) / 2 );
			}
		}
		else
		{
			if( DefaultLayout.setupmenu_android4_with_no_icons )
			{
				itemTitle.setPosition( Utils3D.getScreenWidth() / 15f + itemIcon.width , itemHeight / 2 - itemTitle.height / 2 );
			}
			else
			{
				itemTitle.setPosition( Utils3D.getScreenWidth() / 15f + itemIcon.width + Tools.dip2px( iLoongLauncher.getInstance() , 6 ) , itemHeight / 2 - itemTitle.height / 2 );
			}
		}
		item.addView( itemTitle );
		this.addView( item );
		allChildren.add( item );
		newlist.add( item );
	}
	
	private Bitmap titleToPixmapEx(
			String title ,
			int titleHeight ,
			int widthLimit ,
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
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
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
		titleHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float x = 0;
		float titleY = (float)( -fontMetrics.ascent );
		canvas.drawText( title , x , titleY , paint );
		return bmp;
	}
	
	public static void getIconRegion(
			Bitmap temp ,
			String title ,
			int textureWidth ,
			int textureHeight ,
			TextureRegion region ,
			float[] position )
	{
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.setupmenu_text_font_size );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		FontMetrics fontMetrics = paint.getFontMetrics();
		float singleLineHeight = (float)Math.ceil( fontMetrics.bottom - fontMetrics.top );
		float space_height = R3D.setupmenu_icon_and_text_spacing;
		float height = textureHeight - singleLineHeight - space_height - R3D.setupmenu_icon_padding_top;
		Bitmap bitmap = Tools.scaleBitmap( temp , textureWidth , (int)height );
		float paddingTop;
		float paddingLeft = ( textureWidth - bitmap.getWidth() ) / 2;
		float bmpHeight = bitmap.getHeight();
		paddingTop = ( textureHeight - bmpHeight - space_height - singleLineHeight ) / 2;
		if( paddingTop <= 0 )
			paddingTop = R3D.setupmenu_icon_padding_top;
		if( paddingLeft < 0 )
		{
			paddingLeft = 0;
		}
		position[0] = paddingLeft;
		position[1] = paddingTop;
		region.setRegion( new BitmapTexture( bitmap , true ) );
		temp.recycle();
	}
	
	public static void getTitleRegion(
			float paddingTop ,
			String title ,
			int textureWidth ,
			int textureHeight ,
			int color ,
			TextureRegion region ,
			float[] position )
	{
		int widthLimit = textureWidth - Tools.dip2px( iLoongLauncher.getInstance() , 12 );
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.setupmenu_text_font_size );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		if( paint.measureText( title ) > widthLimit - 2 )
		{
			while( paint.measureText( title ) > widthLimit - paint.measureText( ".." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "..";
		}
		if( title != null )
		{
			FontMetrics fontMetrics = paint.getFontMetrics();
			int singleLineHeight = (int)Math.ceil( fontMetrics.bottom - fontMetrics.top );
			float textWidth = paint.measureText( title );
			float paddingText = ( textureWidth - textWidth ) / 2;
			paint.setColor( color );
			Bitmap bmp = Bitmap.createBitmap( Math.round( textWidth + 1 ) , singleLineHeight + Tools.dip2px( iLoongLauncher.getInstance() , 5 ) , Bitmap.Config.ARGB_8888 );
			Canvas canvas = new Canvas( bmp );
			canvas.drawText( title , 0 , singleLineHeight + Tools.dip2px( iLoongLauncher.getInstance() , 2 ) , paint );
			region.setRegion( new BitmapTexture( bmp , true ) );
			position[0] = paddingText;
			position[1] = fontMetrics.bottom;
		}
	}
	
	// keypad start
	public void hideItemFocus()
	{
		ViewGroup3D item = (ViewGroup3D)this.getChildAt( focusItem );
		for( int j = 0 ; j < item.getChildCount() ; j++ )
		{
			View3D itemIcon = item.getChildAt( j );
			itemIcon.setColor( color.r , color.g , color.b , color.a );
		}
	}
	
	public void unHighlightItem()
	{
		// if(preFocusItem!=focusItem){
		ViewGroup3D preItem = (ViewGroup3D)this.getChildAt( focusItem );
		for( int j = 0 ; j < preItem.getChildCount() ; j++ )
		{
			View3D itemIcon = preItem.getChildAt( j );
			itemIcon.setColor( color.r , color.g , color.b , color.a );
		}
		// }
	}
	
	public void highlightItem()
	{
		ViewGroup3D item = (ViewGroup3D)this.getChildAt( focusItem );
		for( int j = 0 ; j < item.getChildCount() ; j++ )
		{
			View3D itemIcon = item.getChildAt( j );
			// itemIcon.setColor((float)0.11,(float) 0.2549,(float)0.6470,
			// color.a);
			itemIcon.setColor( (float)0.4549 , (float)0.48235 , (float)0.6862 , color.a );
		}
		if( preFocusItem != focusItem )
		{
			ViewGroup3D preItem = (ViewGroup3D)this.getChildAt( preFocusItem );
			for( int j = 0 ; j < preItem.getChildCount() ; j++ )
			{
				View3D itemIcon = preItem.getChildAt( j );
				itemIcon.setColor( color.r , color.g , color.b , color.a );
			}
		}
	}
	
	// keypad end
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		preFocusItem = focusItem;
		origin = true;// zqh
		if( keycode == KeyEvent.KEYCODE_BACK )
		{
			if( visible && touchable )
			{
				hide();
				return true;
			}
		}
		return super.keyDown( keycode );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// if(itemBg != null){
		if( !DefaultLayout.popup_menu_no_background_shadow )
		{
			batch.setColor( color.r , color.g , color.b , anim_alpha * parentAlpha );
			popmenu_bg.draw( batch , 0 , 0 , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		}
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		appItemBgFrame.draw( batch , x , y , width , height );
		SetupTabMenu sm = mTabMenus.get( 0 );
		ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( sm.id ) );
		int micount = menuitems.size();
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
		{
			for( int i = 0 ; i < micount ; i++ )
			{
				batch.draw( appItemBgLine1 , x + Tools.dip2px( iLoongLauncher.getInstance() , 7f ) , y + i * itemHeight - 1 , width - Tools.dip2px( iLoongLauncher.getInstance() , 7f ) * 2 , 1 );
			}
		}
		else
		{
			batch.draw( appItemBgLine1 , x , y + itemHeight , width , linewidth );
			int nums = micount;
			int positionX = 0;
			for( int i = 0 ; i < nums ; i++ )
			{
				if( i < nums / 2 - 1 )
				{
					positionX = (int)( ( i + 1 ) * width / ( nums / 2 ) );
					batch.draw( appItemBgLine2 , positionX , y + itemHeight , linewidth , itemHeight - linewidth );
				}
				else if( i >= nums / 2 && i < nums - 1 )
				{
					positionX = (int)( ( i + 1 - nums / 2 ) * width / ( nums - nums / 2 ) );
					batch.draw( appItemBgLine2 , positionX , y , linewidth , itemHeight );
				}
			}
		}
		super.draw( batch , parentAlpha );
	}
	
	private int getIndexByPosition(
			float x ,
			float y )
	{
		int index = -1;
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
		{
			index = (int)( y / itemHeight );
		}
		else
		{
			int row = (int)( ( this.height - y - 8 * SetupMenu.mScale ) / itemHeight );
			int nums = 0;
			float itemwidth = 0;
			if( row == 0 )
			{
				nums = getChildCount() / 2;
				itemwidth = this.width / nums;
				index = (int)( x / itemwidth );
			}
			else if( row == 1 )
			{
				nums = getChildCount() - getChildCount() / 2;
				itemwidth = this.width / nums;
				index = getChildCount() / 2 + (int)( x / itemwidth );
			}
		}
		return index;
	}
	
	// keypad start
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		Desktop3DListener.root.particleScrollRefresh( this.x , this.y , x , y );
		return super.scroll( x , y , deltaX , deltaY );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleStart( this.x , this.y , x , y );
		}
		unHighlightItem();
		int index = getIndexByPosition( x , y );
		if( index >= 0 && index < this.getChildCount() )
		{
			focusItem = index;
			preFocusItem = focusItem;
			highlightItem();
		}
		return true;
	}
	
	// keypad end
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		unHighlightItem();
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
		int index = 0;
		SetupTabMenu sm = mTabMenus.get( 0 );
		ArrayList<SetupMenuItem> menuitems = mMenuItems.get( Integer.valueOf( sm.id ) );
		index = getIndexByPosition( x , y );
		if( index >= 0 && index < menuitems.size() )
		{
			if( getChildAt( index ).name.endsWith( "false" ) )
			{
				return true;
			}
			if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
			{
				SetupMenuItem item = menuitems.get( menuitems.size() - index - 1 );
				SetupMenuActions.getInstance().Handle( item.id );
			}
			else
			{
				SetupMenuItem item = menuitems.get( index );
				SetupMenuActions.getInstance().Handle( item.id );
			}
			SendMsgToAndroid.sysPlaySoundEffect();
		}
		origin = true;
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
		this.hideNoAnim();
		AndroidGraphics graphics = (AndroidGraphics)Gdx.graphics;
		graphics.forceRender( 30 );
		return true;
	}
	
	private void updateAppPopMenu()
	{
		View3D actor;
		newlist.clear();
		newlist.addAll( allChildren );
		if( Root3D.IsProhibiteditMode )
		{
			for( int i = 0 ; i < allChildren.size() ; i++ )
			{
				actor = allChildren.get( i );
				if( actor.name.equals( R3D.getString( RR.string.system_widget ) + true ) || actor.name.equals( R3D.getString( RR.string.software_manager ) + true ) || actor.name.equals( R3D
						.getString( RR.string.screen_editor ) + true ) )
				{
					newlist.remove( actor );
				}
			}
		}
		else
		{
			for( int i = 0 ; i < allChildren.size() ; i++ )
			{
				actor = allChildren.get( i );
				if( actor.name.equals( R3D.getString( RR.string.system_widget ) + false ) || actor.name.equals( R3D.getString( RR.string.software_manager ) + false ) || actor.name.equals( R3D
						.getString( RR.string.screen_editor ) + false ) )
				{
					newlist.remove( actor );
				}
			}
		}
		layout( newlist );
	}
	
	private void layout(
			List<View3D> list )
	{
		this.removeAllViews();
		View3D actor;
		int size = list.size();
		for( int i = 0 ; i < size ; i++ )
		{
			actor = list.get( i );
			if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
			{
				actor.setPosition( 0 , i * itemHeight );
			}
			this.addView( actor );
		}
		if( DefaultLayout.popmenu_style == SetupMenu.POPMENU_STYLE_ANDROID4 )
		{
			height = list.size() * itemHeight + 8 * SetupMenu.mScale;
			x = ( Utils3D.getScreenWidth() - itemWidth ) / 2.0f;
			y = -height;
			this.originX = width;
			this.originY = height;
		}
	}
	
	public void show()
	{
		if( DefaultLayout.enable_edit_mode_function )
		{
			updateAppPopMenu();
		}
		super.show();
		this.requestFocus();
		preFocusItem = focusItem;
		if( DefaultLayout.show_popup_menu_anim )
		{
			stopTween();
			startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , x , 0 , 0 );
			startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.4f , 1 , 0 , 0 );
		}
		else
		{
			setUser( 1.0f );
			setPosition( x , 0 );
		}
		// if(DefaultLayout.pop_menu_focus_focus_effect){
		isVisible = true;//
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
		// }
		// setScale(0, 0);
		// color.a = 0.5f;
		// startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.2f, 1, 1, 0);
		// startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.2f, 1, 0, 0);
	}
	
	public void hide()
	{
		// focusItem =0;//zqh
		preFocusItem = focusItem;
		// hideItemFocus();
		Log.d( "launcher" , "hide" );
		this.releaseFocus();
		touchable = false;
		if( DefaultLayout.show_popup_menu_anim )
		{
			stopTween();
			startTween( View3DTweenAccessor.POS_XY , Cubic.IN , 0.2f , x , -height , 0 ).setCallback( this );
			startTween( View3DTweenAccessor.USER , Cubic.IN , 0.2f , 0 , 0 , 0 );
		}
		else
		{
			setPosition( x , -height );
			setUser( 0.0f );
			this.visible = false;
		}
		// if(DefaultLayout.pop_menu_focus_focus_effect){
		isVisible = false;// zqh
		origin = false;// this only for the solution without focus effect.//1221
		// }
		// setScale(1, 1);
		// color.a = 1;
		// startTween(View3DTweenAccessor.SCALE_XY, Cubic.IN, 0.2f, 0, 0, 0)
		// .setCallback(this);
		// startTween(View3DTweenAccessor.OPACITY, Cubic.IN, 0.2f, 0.5f, 0, 0);
	}
	
	public void hideNoAnim()
	{
		this.releaseFocus();
		this.visible = false;
		this.touchable = false;
	}
	
	@Override
	public float getUser()
	{
		return anim_alpha;
	}
	
	@Override
	public void setUser(
			float value )
	{
		anim_alpha = value;
	}
	
	public void reset()
	{
		this.releaseFocus();
		y = -height;
		visible = false;
		touchable = false;
	}
	
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( isVisible() )
		{
			visible = false;
			touchable = false;
		}
	}
	
	class SetupTabMenu
	{
		
		int id;
		int page;
		String name;
		
		public int getID()
		{
			return id;
		}
	}
	
	public class SetupMenuItem
	{
		
		public int page;
		public int id;
		public String name;
		public String icon;
		public Bitmap iconbmp;
		public String icon2;
		public Bitmap icon2bmp;
	}
}
