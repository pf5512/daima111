package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class AppPopMenu2 extends ViewGroup3D
{
	
	public static final int MSG_HIDE_APPLIST_FOCUS = 0;
	public static final int MSG_NEW_FOLDER_IN_APPLIST = 1;
	private int itemWidth = 150;
	//xiatian start	////New AppList Popmenu
	//	private int itemHeight = 50;	//xiatian del	
	private int itemHeight = 45; //xiatian add
	//xiatian end
	public static NinePatch appItemBgFrame = null;
	public static TextureRegion appItemBgLine = null;
	private static TextureRegion appPopMenuBg = null;
	protected float appMenuBg_alpha = 0;
	private AppList3D appList;
	private boolean isFirstTouch = true;
	public static boolean isVisible = false;
	public NinePatch menuFocus = new NinePatch( R3D.findRegion( "icon_focus" ) , 20 , 20 , 20 , 20 );
	private float focusItem = 0;
	public static boolean origin = false;
	private float itemTitlePaddingLeft = 0f; //xiatian add	//New AppList Popmenu
	private ArrayList<View3D> allChildren = new ArrayList<View3D>();//zjp
	private List<View3D> newlist = new ArrayList<View3D>();
	
	public AppPopMenu2(
			String name )
	{//zjp
		super( name );
	}
	
	public AppPopMenu2(
			String name ,
			String temp )
	{
		super( name );
		//xiatian start	////New AppList Popmenu
		//xiatian del start
		//		itemWidth = Utils3D.getScreenWidth()*13/15;
		//		itemHeight = Tools.dip2px(iLoongLauncher.getInstance(), 48);
		//xiatian del end
		//xiatian add start
		itemWidth = Utils3D.getScreenWidth() * 6 / 9;
		itemHeight = Tools.dip2px( iLoongLauncher.getInstance() , 45 );
		itemTitlePaddingLeft = Utils3D.getScreenWidth() * 3 / 80f;
		//xiatian add end
		//xiatian end
		//		addItem(R3D.findRegion("app-uninstall-button"),
		//				R3D.getString(RR.string.uninstall_app));
		//		addItem(R3D.findRegion("app-hide-button"),
		//				R3D.getString(RR.string.hide_icon));
		//		addItem(R3D.findRegion("app-sort-button"),
		//				R3D.getString(RR.string.sort_icon));
		//xiatian start	////New AppList Popmenu
		//xiatian del start
		//		addItem(new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-uninstall-button.png"))),
		//				R3D.getString(RR.string.uninstall_app));
		//		addItem(new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-hide-button.png"))),
		//				R3D.getString(RR.string.hide_icon));
		//		addItem(new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-sort-button.png"))),
		//				R3D.getString(RR.string.sort_icon));
		//		addItem(new TextureRegion(new BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-effect-button.png"))),
		//				R3D.getString(RR.string.effect_icon));
		//xiatian del end
		//xiatian add start
		//teapotXu add start for Folder in Mainmenu
		if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
		{
			//			addItem(R3D.getString(RR.string.edit_mode));
			addItem( new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-edit-button.png" ) , true ) ) , R3D.getString( RR.string.edit_mode ) );
		}
		else
		{
			//			addItem(R3D.getString(RR.string.uninstall_app));
			addItem( new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-uninstall-button.png" ) , true ) ) , R3D.getString( RR.string.uninstall_app ) );
		}
		//addItem(R3D.getString(RR.string.uninstall_app));
		//teapotXu add end for Folder in Mainmenu		
		//		addItem(R3D.getString(RR.string.hide_icon));
		//		addItem(R3D.getString(RR.string.sort_icon));
		//		addItem(R3D.getString(RR.string.effect_icon));
		addItem( new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-hide-button.png" ) , true ) ) , R3D.getString( RR.string.hide_icon ) );
		addItem( new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-sort-button.png" ) , true ) ) , R3D.getString( RR.string.sort_icon ) );
		addItem( new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-effect-button.png" ) , true ) ) , R3D.getString( RR.string.effect_icon ) );
		//xiatian add end
		//xiatian end
		//xiatian add start	//mainmenu_background_alpha_progress
		if( DefaultLayout.mainmenu_background_alpha_progress && !DefaultLayout.mainmenu_background_translucent )
		{
			//			addItem(R3D.getString(RR.string.mainmenu_bg_alpha));
			addItem(
					new TextureRegion( new BitmapTexture( ThemeManager.getInstance().getBitmap( "theme/pack_source/app-background-alpha-button.png" ) , true ) ) ,
					R3D.getString( RR.string.mainmenu_bg_alpha ) );
		}
		//xiatian add end
		width = itemWidth;
		//xiatian start	////New AppList Popmenu
		//xiatian del start		
		//		height = this.getChildCount() * itemHeight;
		//		x = Utils3D.getScreenWidth()/15f;
		//xiatian del end
		//xiatian add start		
		height = this.getChildCount() * itemHeight + R3D.applist_menu_padding_top;
		x = ( Utils3D.getScreenWidth() - itemWidth ) / 2;//Utils3D.getScreenWidth()*2/9f;
		//xiatian add end
		//xiatian end		
		y = -height;
		this.originX = width;
		this.originY = height;
		this.transform = true;
		//		FileHandle file = ThemeManager.getInstance().getGdxTextureResource("setupmenu_android4/bg_frame.png");
		//		Bitmap bm = BitmapFactory.decodeStream(file.read());
		//		Bitmap bm = BitmapFactory.decodeResource(iLoongLauncher.getInstance().getResources(), RR.drawable.tanchu_applist);
		//		Bitmap bm  = ThemeManager.getInstance().getBitmap("launcher/setupmenu_android4/tanchu_applist.png");
		Bitmap bm = null;
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
		if(bm != null)
		{
			Texture t = new BitmapTexture( bm );
			if( appItemBgFrame == null )
				//			appItemBgFrame = new NinePatch(new TextureRegion(t),6,6,6,6);	//xiatian del	//New AppList Popmenu
				appItemBgFrame = new NinePatch( new TextureRegion( t ) , 5 , 5 , 5 , 5 ); //xiatian add	//New AppList Popmenu
			bm.recycle();
		}
		//		file = ThemeManager.getInstance().getGdxTextureResource("setupmenu_android4/bg-2.png");
		//		bm = BitmapFactory.decodeStream(file.read());
		if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/setupmenu_android4/bg-2.png" ) )
		{
			bm = BitmapFactory.decodeFile( DefaultLayout.custom_assets_path + "/launcher/setupmenu_android4/bg-2.png" );
		}
		else
		{
			bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu_android4/bg-2.png" );
		}
		if(bm != null)
		{
			if( appItemBgLine == null )
				appItemBgLine = new TextureRegion( new BitmapTexture( bm ) );
			bm.recycle();
		}
		bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/pop_menu_bg.png" );
		bm = Tools.resizeBitmap( bm , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		if( !DefaultLayout.popup_menu_no_background_shadow )
			appPopMenuBg = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		focusItem = ( getChildCount() - 1 );//zqh
	}
	
	private void updateAppPopMenu()
	{
		newlist.clear();
		if( Root3D.IsProhibiteditMode )
		{
			View3D actor;
			for( int i = 0 ; i < allChildren.size() ; i++ )
			{
				actor = allChildren.get( i );
				if( !actor.name.equals( R3D.getString( RR.string.sort_icon ) ) && !actor.name.equals( R3D.getString( RR.string.hide_icon ) ) && !actor.name
						.equals( R3D.getString( RR.string.edit_mode ) ) && !actor.name.equals( R3D.getString( RR.string.uninstall_app ) ) )
				{
					newlist.add( actor );
				}
			}
		}
		else
		{
			newlist.addAll( allChildren );
		}
		layout( newlist );
	}
	
	private void layout(
			List<View3D> list )
	{
		this.removeAllViews();
		View3D actor;
		for( int i = 0 ; i < list.size() ; i++ )
		{
			actor = list.get( i );
			actor.setPosition( 0 , i * itemHeight );
			this.addView( actor );
		}
		height = list.size() * itemHeight + R3D.applist_menu_padding_top;
		x = ( Utils3D.getScreenWidth() - itemWidth ) / 2;//Utils3D.getScreenWidth()*2/9f;
		y = -height;
		this.originX = width;
		this.originY = height;
	}
	
	private void addItem(
			TextureRegion region ,
			String title )
	{
		ViewGroup3D item = new ViewGroup3D( title );
		item.y = ( this.getChildCount() ) * itemHeight;
		item.x = 0;
		item.setSize( itemWidth , itemHeight );
		View3D itemIcon = new View3D( "itemicon" , region );
		float iconWidth = region.getRegionWidth();
		float iconHeight = region.getRegionHeight();
		if( iconHeight * Utils3D.getDensity() / 1.5f > itemHeight - 2 * R3D.appmenu_icon_padding_top )
		{
			iconHeight = itemHeight - 2 * R3D.appmenu_icon_padding_top - Tools.dip2px( iLoongLauncher.getInstance() , 5 );
			iconWidth = ( iconHeight ) / region.getRegionHeight() * iconWidth;
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
			itemIcon.setPosition( itemWidth - iconWidth - Tools.dip2px( iLoongLauncher.getInstance() , 20 ) , ( itemHeight - iconHeight ) / 2 );
		}
		else
		{
			itemIcon.setPosition( Tools.dip2px( iLoongLauncher.getInstance() , 20 ) , itemHeight / 2 - iconHeight / 2 );
		}
		if( DefaultLayout.setupmenu_android4_with_no_icons )
		{
			itemIcon.width = 0;
		}
		else
		{
			item.addView( itemIcon );
		}
		Bitmap bmp = titleToPixmapWidthLimit( title , (int)( itemWidth - Tools.dip2px( iLoongLauncher.getInstance() , 50 ) - itemIcon.width ) );
		TextureRegion titleRegion = new TextureRegion( new BitmapTexture( bmp ) );//R3D.findRegion(title);
		if( bmp != null && !bmp.isRecycled() )
		{
			bmp.recycle();
			bmp = null;
		}
		View3D itemTitle = new View3D( "itemtitle" , titleRegion );
		if( DefaultLayout.popmenu_gravity_right_when_special_language && ( language != null && ( language.equals( "ar" ) || language.equals( "fa" ) || language.equals( "he" ) || language
				.equals( "iw" ) || language.equals( "ug" ) ) ) )
		{
			if( DefaultLayout.setupmenu_android4_with_no_icons )
			{
				itemTitle.setPosition( itemWidth - iconWidth - itemTitle.width , ( itemHeight - itemTitle.height ) / 2 );
			}
			else
			{
				itemTitle.setPosition( itemWidth - iconWidth - itemTitle.width - 2 * Tools.dip2px( iLoongLauncher.getInstance() , 20 ) , ( itemHeight - itemTitle.height ) / 2 );
			}
		}
		else
		{
			if( DefaultLayout.setupmenu_android4_with_no_icons )
			{
				itemTitle.setPosition( Tools.dip2px( iLoongLauncher.getInstance() , 20 ) + itemIcon.width , itemHeight / 2 - itemTitle.height / 2 );
			}
			else
			{
				itemTitle.setPosition( Tools.dip2px( iLoongLauncher.getInstance() , 20 ) + itemIcon.width + Tools.dip2px( iLoongLauncher.getInstance() , 20 ) , itemHeight / 2 - itemTitle.height / 2 );
			}
		}
		item.addView( itemTitle );
		this.addView( item );
		allChildren.add( item );
		newlist.add( item );
	}
	
	@Override
	public boolean keyDown(
			int keycode )
	{
		origin = true;
		if( DefaultLayout.pop_menu_focus_focus_effect )
		{
			if( isFirstTouch )
			{
				isFirstTouch = false;
				return super.keyDown( keycode );
			}
			origin = true;
			viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );
			if( keycode == KeyEvent.KEYCODE_DPAD_DOWN )
			{
				if( focusItem > 0 )
					focusItem--;
			}
			else if( keycode == KeyEvent.KEYCODE_DPAD_UP )
			{
				if( focusItem < getChildCount() - 1 )
					focusItem++;
			}
			else if( keycode == KeyEvent.KEYCODE_DPAD_CENTER )
			{
				onKeySelect();
				return super.keyDown( keycode );
			}
		}
		return super.keyDown( keycode );
	}
	
	public void onKeySelect()
	{
		if( isVisible )
		{
			if( focusItem == 0 )
			{
				appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
			}
			else if( focusItem == 1 )
			{
				appList.setMode( AppList3D.APPLIST_MODE_HIDE );
			}
			else if( focusItem == 2 )
			{
				SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId , iLoongLauncher.SORT_ORIGIN_APPLIST );
			}
			this.hide();
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// if(itemBg != null){
		if( !DefaultLayout.popup_menu_no_background_shadow && appPopMenuBg != null )
		{
			batch.setColor( color.r , color.g , color.b , appMenuBg_alpha * parentAlpha );
			batch.draw( appPopMenuBg , 0 , 0 , appPopMenuBg.getRegionWidth() , appPopMenuBg.getRegionHeight() );
		}
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		if( appItemBgFrame != null )
			appItemBgFrame.draw( batch , x , y , width , height );
		if( appItemBgLine != null )
			for( int i = 0 ; i < getChildCount() ; i++ )
			{
				batch.draw( appItemBgLine ,
				//					x,	//xiatian del	//New AppList Popmenu
						x + Tools.dip2px( iLoongLauncher.getInstance() , 6f ) , //xiatian add	//New AppList Popmenu
						y + i * itemHeight - 1 ,
						//					width,	//xiatian del	//New AppList Popmenu
						width - 2 * Tools.dip2px( iLoongLauncher.getInstance() , 6f ) , //xiatian add	//New AppList Popmenu
						1 );
			}
		super.draw( batch , parentAlpha );
	}
	
	public void superdraw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
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
			Log.d( "launcher" , "popmenu:onTouchUp" );
			hide();
			return true;
		}
	}
	
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
		// TODO Auto-generated method stub
		if( DefaultLayout.enable_new_particle )
		{
			Desktop3DListener.root.particleStart( this.x , this.y , x , y );
		}
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		int index = (int)( y / itemHeight );
		View3D actor = newlist.get( index );
		if( actor.name.equals( R3D.getString( RR.string.edit_mode ) ) || actor.name.equals( R3D.getString( RR.string.uninstall_app ) ) )
		{
			appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
		}
		else if( actor.name.equals( R3D.getString( RR.string.hide_icon ) ) )
		{
			appList.setMode( AppList3D.APPLIST_MODE_HIDE );
		}
		else if( actor.name.equals( R3D.getString( RR.string.sort_icon ) ) )
		{
			SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId , iLoongLauncher.SORT_ORIGIN_APPLIST );
		}
		else if( actor.name.equals( R3D.getString( RR.string.effect_icon ) ) )
		{
			SendMsgToAndroid.sendShowAppEffectDialogMsg();
		}
		else if( actor.name.equals( R3D.getString( RR.string.mainmenu_bg_alpha ) ) )
		{
			if( DefaultLayout.mainmenu_background_alpha_progress && !DefaultLayout.mainmenu_background_translucent )
				SendMsgToAndroid.sendShowMainmenuBgDialogMsg();
		}
		SendMsgToAndroid.sysPlaySoundEffect();
		origin = true;
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );
		this.hide();
		return true;
	}
	
	public void showNoAnim()
	{
		super.show();
	}
	
	public void show()
	{
		if( DefaultLayout.enable_edit_mode_function )
			updateAppPopMenu();
		super.show();
		this.requestFocus();
		if( DefaultLayout.show_popup_menu_anim )
		{
			stopTween();
			startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , x , 0 , 0 );
			startTween( View3DTweenAccessor.USER , Cubic.OUT , 0.2f , 1 , 0 , 0 );
		}
		else
		{
			setPosition( x , 0 );
			setUser( 1 );
		}
		isVisible = true;//
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
	}
	
	public void hide()
	{
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
			visible = false;
			setPosition( x , -height );
			setUser( 0 );
		}
		isVisible = false;
		origin = false;// this only for the solution without focus effect.//1221
	}
	
	public void hideNoAnim()
	{
		this.visible = false;
		this.touchable = false;
	}
	
	public void setAppList(
			AppList3D appList )
	{
		this.appList = appList;
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
	
	@Override
	public float getUser()
	{
		return appMenuBg_alpha;
	}
	
	@Override
	public void setUser(
			float value )
	{
		appMenuBg_alpha = value;
	}
	
	//xiatian add start	////New AppList Popmenu
	private void addItem(
			String title )
	{
		ViewGroup3D item = new ViewGroup3D( "popitem" );
		item.y = ( this.getChildCount() ) * itemHeight;
		item.x = 0;
		item.setSize( itemWidth , itemHeight );
		TextureRegion titleRegion = R3D.findRegion( title );
		View3D itemTitle = new View3D( "itemtitle" , titleRegion );
		itemTitle.setPosition( itemTitlePaddingLeft , itemHeight / 2 - itemTitle.height / 2 );
		item.addView( itemTitle );
		this.addView( item );
	}
	
	//xiatian add end
	private Bitmap titleToPixmapWidthLimit(
			String title ,
			int widthLimit )
	{
		Paint paint = new Paint();
		paint.setColor( Color.BLACK );
		paint.setAntiAlias( true );
		paint.setTextSize( Tools.dip2px( iLoongLauncher.getInstance() , 16 ) );
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
		int titleHeight = (int)Math.ceil( -fontMetrics.ascent + fontMetrics.descent );
		Bitmap bmp = Bitmap.createBitmap( titleWidth , titleHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float x = 0;
		float titleY = (float)( -fontMetrics.ascent );
		canvas.drawText( title , x , titleY , paint );
		return bmp;
	}
}
