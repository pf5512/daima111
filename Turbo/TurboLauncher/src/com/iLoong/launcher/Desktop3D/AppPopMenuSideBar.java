package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.DrawerActivity;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class AppPopMenuSideBar extends AppPopMenu2
{
	
	private int itemHeight = R3D.app_pop_menu_item_height;
	private int itemWidth = R3D.app_pop_menu_item_width;
	private int paddingRight = R3D.app_pop_menu_padding_right;
	private int paddingTop = R3D.app_pop_menu_padding_top;
	private AppList3D appList;
	private List<Integer> listItem = new ArrayList<Integer>();
	private List<String> listIcon = new ArrayList<String>();
	private List<NinePatch> bgIcon = new ArrayList<NinePatch>();
	private List<View3D> newlist = new ArrayList<View3D>();
	private float duration = 0.2f;
	
	public AppPopMenuSideBar(
			String name )
	{
		super( name );
		this.transform = true;
		this.color.a = 0;
		listItem.add( RR.string.app_pop_appSetting );
		listIcon.add( "theme/appPopMenu/setting.png" );
		listItem.add( RR.string.uninstall_app );
		listIcon.add( "theme/appPopMenu/edit.png" );
		listItem.add( RR.string.app_pop_appHide );
		listIcon.add( "theme/appPopMenu/hiding.png" );
		listIcon.add( "theme/appPopMenu/effect.png" );
		listItem.add( RR.string.app_pop_appEffect );
		listItem.add( RR.string.app_pop_appSort );
		listIcon.add( "theme/appPopMenu/sort.png" );
		if( DefaultLayout.mainmenu_folder_function )
		{
			listItem.add( RR.string.app_pop_newfolder );
			listIcon.add( "theme/appPopMenu/folder.png" );
		}
		Bitmap b1 = ThemeManager.getInstance().getBitmap( "theme/appPopMenu/bg1.png" );
		bgIcon.add( new NinePatch( new BitmapTexture( b1 ) ) );
		b1.recycle();
		Bitmap b2 = ThemeManager.getInstance().getBitmap( "theme/appPopMenu/bg2.png" );
		bgIcon.add( new NinePatch( new BitmapTexture( b2 ) ) );
		b2.recycle();
		Bitmap select = ThemeManager.getInstance().getBitmap( "theme/appPopMenu/select.png" );
		bgIcon.add( new NinePatch( new BitmapTexture( select ) ) );
		select.recycle();
		this.height = listIcon.size() * itemHeight + R3D.applist_menu_padding_top;
		this.width = itemWidth;
		this.originX = width;
		this.originY = height;
		this.transform = true;
		this.x = Utils3D.getScreenWidth() - this.width - 10;
		//		this.y = Utils3D.getScreenHeight()+this.height;
		this.y = -this.height;
		for( int i = 0 ; i < listItem.size() ; i++ )
		{
			addItem( R3D.getString( listItem.get( i ) ) , i , listItem.size() );
		}
	}
	
	public void show()
	{
		super.showNoAnim();
		this.requestFocus();
		if( DefaultLayout.show_popup_menu_anim )
		{
			stopTween();
			startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , duration , Utils3D.getScreenWidth() - this.width - paddingRight , R3D.appbar_height + paddingTop , 0 );
			startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 1 , 0 , 0 );
			startTween( View3DTweenAccessor.USER , Cubic.OUT , duration , 1 , 0 , 0 );
		}
		else
		{
			setPosition( x , 0 );
			setUser( 1.0f );
		}
		isVisible = true;//
	}
	
	public void hide()
	{
		Log.d( "launcher" , "hide" );
		this.releaseFocus();
		touchable = false;
		if( DefaultLayout.show_popup_menu_anim )
		{
			stopTween();
			startTween( View3DTweenAccessor.OPACITY , Linear.INOUT , duration , 0 , 1 , 0 );
			startTween( View3DTweenAccessor.POS_XY , Cubic.IN , duration , Utils3D.getScreenWidth() - this.width - paddingRight , -this.height , 0 ).setCallback( this );
			startTween( View3DTweenAccessor.USER , Cubic.IN , duration , 0 , 0 , 0 );
		}
		else
		{
			visible = false;
			setPosition( x , -height );
			setUser( 0 );
		}
		isVisible = false;// zqh
		origin = false;// this only for the solution without focus effect.//1221
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		int index = (int)( y / itemHeight );
		View3D actor = newlist.get( index );
		String title = actor.name;
		if( title == null )
		{
			return true;
		}
		else if( title.equals( R3D.getString( RR.string.app_pop_newfolder ) ) )
		{
			if( DefaultLayout.mainmenu_folder_function )
			{
				MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ApplistNewFolder" );
				appList.setMode( AppList3D.APPLIST_MODE_NORMAL );
				viewParent.onCtrlEvent( this , MSG_NEW_FOLDER_IN_APPLIST );
			}
		}
		else if( title.equals( R3D.getString( RR.string.app_pop_appEffect ) ) )
		{
			final int INDEX_WORKSPACE = 0;
			final int INDEX_APP = 1;
			final int position = Desktop3DListener.root.getAppHost().appList.getEffectType();
			Context mContext = iLoongLauncher.getInstance();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( mContext );
			String prefsKey = mContext.getResources().getString( RR.string.setting_key_appeffects );
			String positions = prefs.getString( prefsKey , String.valueOf( position ) );
			final String ACTION_EFFECT_PREVIEW = "com.cool.action.EffectPreview";
			final String ACTION_EFFECT_PREVIEW_EXTRA_TYPE = "EffectPreviewExtraType";
			final String ACTION_EFFECT_PREVIEW_EXTRA_INDEX = "EffectPreviewExtraIndex";
			Intent it = new Intent( ACTION_EFFECT_PREVIEW );
			it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_TYPE , INDEX_APP );
			it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_INDEX , Integer.parseInt( positions ) );
			if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) )
				iLoongLauncher.getInstance().getD3dListener().getRoot().dealEffectPreview( it );
		}
		else if( title.equals( R3D.getString( RR.string.app_pop_appSort ) ) )
		{
			SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId , iLoongLauncher.SORT_ORIGIN_APPLIST );
		}
		else if( title.equals( R3D.getString( RR.string.app_pop_appHide ) ) )
		{
			appList.setMode( AppList3D.APPLIST_MODE_HIDE );
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "HideApplications" );
		}
		else if( title.equals( R3D.getString( RR.string.uninstall_app ) ) )
		{
			appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
			MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ApplistEditModel" );
		}
		else if( title.equals( R3D.getString( RR.string.app_pop_appSetting ) ) )
		{
			//MobclickAgent.onEvent( iLoongLauncher.getInstance() , "ApplistToDrawerSet" );
			Intent intent = new Intent( iLoongLauncher.getInstance() , DrawerActivity.class );
			iLoongLauncher.getInstance().startActivity( intent );
		}
		SendMsgToAndroid.sysPlaySoundEffect();
		origin = true;
		this.hide();
		return true;
	}
	
	@Override
	public void setAppList(
			AppList3D appList )
	{
		// TODO Auto-generated method stub
		this.appList = appList;
	}
	
	private void addItem(
			String title ,
			final int index ,
			int size )
	{
		ViewGroup3D item = new ViewGroup3D( title );
		item.setSize( itemWidth , itemHeight );
		item.setBackgroud( bgIcon.get( index % 2 ) );
		item.x = 0;
		item.y = index * itemHeight;
		Bitmap b = ThemeManager.getInstance().getBitmap( listIcon.get( index ) );
		final TextureRegion iconRegion = new TextureRegion();
		final TextureRegion titleRegion = new TextureRegion();
		final float[] iconPosition = new float[2];
		final float[] titlePosition = new float[2];
		b = Tools.resizeBitmap( b , R3D.app_pop_menu_img_size , R3D.app_pop_menu_img_size );
		SetupMenu3D.getTitleRegion( iconPosition[1] , title , (int)itemWidth , itemHeight , Color.WHITE , titleRegion , titlePosition );
		iconRegion.setRegion( new BitmapTexture( b ) );
		b.recycle();
		b = null;
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
						0 ,
						y + ( height - iconRegion.getRegionHeight() ) / 2 ,
						originX ,
						originY ,
						iconRegion.getRegionWidth() ,
						iconRegion.getRegionHeight() ,
						scaleX ,
						scaleY ,
						rotation );
				batch.draw(
						titleRegion ,
						x + iconRegion.getRegionWidth() + 10 ,
						y + ( height - titleRegion.getRegionHeight() ) / 2 + iconPosition[1] + titlePosition[1] ,
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
		itemTitle.setPosition( 0 , 0 );
		item.addView( itemTitle );
		this.addView( item );
		//		allChildren.add( item );
		newlist.add( item );
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		super.draw( batch , parentAlpha );
	}
}
