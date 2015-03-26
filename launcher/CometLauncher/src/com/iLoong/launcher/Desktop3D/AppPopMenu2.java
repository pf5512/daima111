package com.iLoong.launcher.Desktop3D;


import android.graphics.Bitmap;
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
	private int itemWidth = 150;
	//xiatian start	////New AppList Popmenu
	//	private int itemHeight = 50;	//xiatian del	
	private int itemHeight = 45; //xiatian add
	//xiatian end
	public static NinePatch appItemBgFrame = null;
	public static TextureRegion appItemBgLine = null;
	private AppList3D appList;
	private boolean isFirstTouch = true;
	public static boolean isVisible = false;
	public NinePatch menuFocus = new NinePatch( R3D.findRegion( "icon_focus" ) , 20 , 20 , 20 , 20 );
	private float focusItem = 0;
	public static boolean origin = false;
	private float itemTitlePaddingLeft = 0f; //xiatian add	//New AppList Popmenu
	
	public AppPopMenu2(
			String name )
	{
		super( name );
		//xiatian start	////New AppList Popmenu
		//xiatian del start
		//		itemWidth = Utils3D.getScreenWidth()*13/15;
		//		itemHeight = Tools.dip2px(iLoongLauncher.getInstance(), 48);
		//xiatian del end
		//xiatian add start
		itemWidth = Utils3D.getScreenWidth() * 5 / 9;
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
		if( DefaultLayout.mainmenu_folder_function == true )
		{
			addItem( R3D.getString( RR.string.edit_mode ) );
		}
		else
		{
			addItem( R3D.getString( RR.string.uninstall_app ) );
		}
		//addItem(R3D.getString(RR.string.uninstall_app));
		//teapotXu add end for Folder in Mainmenu		
		addItem( R3D.getString( RR.string.hide_icon ) );
		addItem( R3D.getString( RR.string.sort_icon ) );
		addItem( R3D.getString( RR.string.effect_icon ) );
		//xiatian add end
		//xiatian end
		width = itemWidth;
		//xiatian start	////New AppList Popmenu
		//xiatian del start		
		//		height = this.getChildCount() * itemHeight;
		//		x = Utils3D.getScreenWidth()/15f;
		//xiatian del end
		//xiatian add start		
		height = this.getChildCount() * itemHeight + R3D.applist_menu_padding_top;
		x = Utils3D.getScreenWidth() * 2 / 9f;
		//xiatian add end
		//xiatian end		
		y = -height;
		this.originX = width;
		this.originY = height;
		this.transform = true;
		//		FileHandle file = ThemeManager.getInstance().getGdxTextureResource("setupmenu_android4/bg_frame.png");
		//		Bitmap bm = BitmapFactory.decodeStream(file.read());
		Bitmap bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu_android4/bg_frame.png" );
		Texture t = new BitmapTexture( bm );
		if( appItemBgFrame == null )
			//			appItemBgFrame = new NinePatch(new TextureRegion(t),6,6,6,6);	//xiatian del	//New AppList Popmenu
			appItemBgFrame = new NinePatch( new TextureRegion( t ) , 20 , 20 , 20 , 20 ); //xiatian add	//New AppList Popmenu
		bm.recycle();
		//		file = ThemeManager.getInstance().getGdxTextureResource("setupmenu_android4/bg-2.png");
		//		bm = BitmapFactory.decodeStream(file.read());
		bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu_android4/bg-2.png" );
		if( appItemBgLine == null )
			appItemBgLine = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		focusItem = ( getChildCount() - 1 );//zqh
	}
	
	private void addItem(
			TextureRegion region ,
			String title )
	{
		ViewGroup3D item = new ViewGroup3D( "popitem" );
		item.y = ( this.getChildCount() ) * itemHeight;
		item.x = 0;
		item.setSize( itemWidth , itemHeight );
		View3D itemIcon = new View3D( "itemicon" , region );
		itemIcon.setSize( region.getRegionWidth() * Utils3D.getDensity() / 1.5f , region.getRegionHeight() * Utils3D.getDensity() / 1.5f );
		itemIcon.setPosition( Utils3D.getScreenWidth() / 15f , itemHeight / 2 - ( region.getRegionHeight() * Utils3D.getDensity() / 1.5f ) / 2 );
		item.addView( itemIcon );
		TextureRegion titleRegion = R3D.findRegion( title );
		View3D itemTitle = new View3D( "itemtitle" , titleRegion );
		itemTitle.setPosition( Utils3D.getScreenWidth() / 15f + itemIcon.width + Tools.dip2px( iLoongLauncher.getInstance() , 4 ) , itemHeight / 2 - itemTitle.height / 2 );
		item.addView( itemTitle );
		this.addView( item );
	}
	
	// public boolean keyUp(int keycode) {
	// Log.d("launcher", "keyUp");
	// if (keycode == KeyEvent.KEYCODE_MENU) {
	// // if
	// (R3D.getInteger("pop_setupmenu_style")==SetupMenu.POPMENU_STYLE_ANDROID4)
	// {
	// if (isVisible()) {
	// hide();
	// } else {
	// show();
	// }
	// return true;
	// // if (popMenu2 != null) {
	// // if (tabIndicator.tabId == TAB_APP) {
	// // if (popMenu2.isVisible()) {
	// // popMenu2.hide();
	// // } else {
	// // popMenu2.show();
	// // }
	// // return true;
	// // }
	// // }
	// // } else {
	// // if(menu != null && menu.isVisible()) {
	// // if (popMenu != null) {
	// // if (popMenu.isVisible()) {
	// // popMenu.hide();
	// // } else {
	// // popMenu.show();
	// // }
	// // return true;
	// // }
	// // }
	// // }
	// }
	// return false;
	// }
	@Override
	public boolean keyDown(
			int keycode )
	{
		// TODO Auto-generated method stub
		origin = true;// zqh
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
				SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId );
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
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		appItemBgFrame.draw( batch , x , y , width , height );
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
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		// TODO Auto-generated method stub
		return super.onTouchDown( x , y , pointer );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		int index = (int)( y / itemHeight );
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
		else if( index == 3 )
		{
			SendMsgToAndroid.sendShowAppEffectDialogMsg();
		}
		origin = true;
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
		this.hide();
		return true;
	}
	
	public void show()
	{
		super.show();
		Root3D.getInstance().getAppHost().releaseFocus();
		this.requestFocus();
		stopTween();
		startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , x , 0 , 0 );
		isVisible = true;//
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
		// setScale(0, 0);
		// color.a = 0.5f;
		// startTween(View3DTweenAccessor.SCALE_XY, Cubic.OUT, 0.2f, 1, 1, 0);
		// startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.2f, 1, 0, 0);
	}
	
	public void hide()
	{
		Log.d( "launcher" , "hide" );
		this.releaseFocus();
		touchable = false;
		stopTween();
		startTween( View3DTweenAccessor.POS_XY , Cubic.IN , 0.2f , x , -height , 0 ).setCallback( this );
		isVisible = false;// zqh
		origin = false;// this only for the solution without focus effect.//1221
		Root3D.getInstance().getAppHost().requestFocus();
		// setScale(1, 1);
		// color.a = 1; 
		// startTween(View3DTweenAccessor.SCALE_XY, Cubic.IN, 0.2f, 0, 0, 0)
		// .setCallback(this);
		// startTween(View3DTweenAccessor.OPACITY, Cubic.IN, 0.2f, 0.5f, 0, 0);
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
}
