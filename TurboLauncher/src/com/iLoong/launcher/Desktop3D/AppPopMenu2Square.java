package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import aurelienribon.tweenengine.equations.Cubic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class AppPopMenu2Square extends AppPopMenu2
{
	
	private int itemHeight = 100;
	private static NinePatch appItemBgFrame = null;
	private static TextureRegion appItemBgLine1 = null;
	private static TextureRegion appItemBgLine2 = null;
	private static NinePatch appPopMenuBg = null;
	private AppList3D appList;
	public NinePatch menuFocus = new NinePatch( R3D.findRegion( "icon_focus" ) , 20 , 20 , 20 , 20 );
	private List<Integer> listItem = new ArrayList<Integer>();
	private List<String> listIcon = new ArrayList<String>();
	private int linewidth = 0;
	private ArrayList<View3D> allChildren = new ArrayList<View3D>();//zjp
	private List<View3D> newlist = new ArrayList<View3D>();
	
	public AppPopMenu2Square(
			String name )
	{
		super( name );
		itemHeight = Tools.dip2px( iLoongLauncher.getInstance() , 70 );
		if( DefaultLayout.mainmenu_background_alpha_progress )
		{
			listItem.add( RR.string.mainmenu_bg_alpha );
			listIcon.add( "theme/pack_source/app-background-alpha-button.png" );
		}
		listItem.add( RR.string.effect_icon );
		listItem.add( RR.string.sort_icon );
		listItem.add( RR.string.hide_icon );
		listIcon.add( "theme/pack_source/app-effect-button.png" );
		listIcon.add( "theme/pack_source/app-sort-button.png" );
		listIcon.add( "theme/pack_source/app-hide-button.png" );
		if( DefaultLayout.mainmenu_sort_by_user_fun == false || RR.net_version )//xiatian add	//for mainmenu sort by user
			if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
			{
				listItem.add( RR.string.edit_mode );
				listIcon.add( "theme/pack_source/app-edit-button.png" );
			}
			else
			{
				listItem.add( RR.string.uninstall_app );
				listIcon.add( "theme/pack_source/app-uninstall-button.png" );
			}
		width = Utils3D.getScreenWidth();
		height = 2 * itemHeight + R3D.applist_menu_padding_top;
		x = 0;
		y = -height;
		this.originX = width;
		this.originY = height;
		this.transform = true;
		//		Bitmap bm = BitmapFactory.decodeResource(iLoongLauncher.getInstance().getResources(), RR.drawable.tanchu_applist);
		Bitmap bm;
		if( RR.net_version )
			bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/mm_bg.png" );
		else
			bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/bg.png" );
		Texture t = new BitmapTexture( bm );
		if( appItemBgFrame == null )
			appItemBgFrame = new NinePatch( new TextureRegion( t ) , 2 , 2 , 10 , 2 );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/bg-1.png" );
		linewidth = bm.getHeight();
		//bm = Tools.resizeBitmap( bm , bm.getWidth() , itemHeight );
		if( appItemBgLine1 == null )
			appItemBgLine1 = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "launcher/setupmenu/bg-2.png" );
		//bm = Tools.resizeBitmap( bm , (int)width , bm.getHeight() );
		if( appItemBgLine2 == null )
			appItemBgLine2 = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		if( !DefaultLayout.popup_menu_no_background_shadow )
		{
			bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/pop_menu_bg.png" );
			appPopMenuBg = new NinePatch( new TextureRegion( new BitmapTexture( bm ) ) , 1 , 1 , 1 , 1 );
			bm.recycle();
		}
		for( int i = 0 ; i < listItem.size() ; i++ )
		{
			addItem( R3D.getString( listItem.get( i ) ) , i , listItem.size() );
		}
	}
	
	public void show()
	{
		if( DefaultLayout.enable_edit_mode_function )
			updateAppPopMenu();
		super.showNoAnim();
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
			setUser( 1.0f );
		}
		isVisible = true;//
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
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
		int size = list.size();
		for( int i = 0 ; i < size ; i++ )
		{
			actor = list.get( i );
			float itemX = 0;
			float itemY = 0;
			int cols;
			float itemWidth;
			if( size <= 3 )
			{
				cols = size;
				itemWidth = Utils3D.getScreenWidth() * 1.0f / cols;
				itemY = 0;
				itemX = x + i * itemWidth;
			}
			else
			{
				cols = i < size / 2 ? size / 2 : ( size - size / 2 );
				itemWidth = Utils3D.getScreenWidth() * 1.0f / cols;
				if( i < size / 2 )
				{
					itemY = itemHeight;
					itemX = x + i * itemWidth;
				}
				else
				{
					itemY = 0;
					itemX = x + ( i - size / 2 ) * itemWidth;
				}
			}
			actor.setSize( itemWidth , itemHeight );
			actor.setPosition( itemX , itemY );
			if( actor instanceof ViewGroup3D )
			{
				for( int j = 0 ; j < ( (ViewGroup3D)actor ).getChildCount() ; j++ )
				{
					View3D child = ( (ViewGroup3D)actor ).getChildAt( j );
					child.setPosition( ( itemWidth - child.width ) / 2 , ( itemHeight - child.height ) / 2 );
				}
			}
			this.addView( actor );
		}
		if( list.size() <= 3 )
		{
			height = itemHeight + R3D.applist_menu_padding_top;
		}
		else
			height = 2 * itemHeight + R3D.applist_menu_padding_top;
		x = 0;
		y = -height;
		this.originX = width;
		this.originY = height;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// if(itemBg != null){
		if( !DefaultLayout.popup_menu_no_background_shadow )
		{
			batch.setColor( color.r , color.g , color.b , this.appMenuBg_alpha * parentAlpha );
			appPopMenuBg.draw( batch , 0 , 0 , Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		}
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		appItemBgFrame.draw( batch , x , y , width , height );
		int nums = getChildCount();
		int positionX = 0;
		if( nums <= 3 )
		{
			for( int i = 0 ; i < nums ; i++ )
			{
				positionX = (int)( ( i + 1 ) * width / nums );
				batch.draw( appItemBgLine2 , positionX , y , linewidth , itemHeight );
			}
		}
		else
		{
			batch.draw( appItemBgLine1 , x , y + itemHeight , width , linewidth );
			for( int i = 0 ; i < nums ; i++ )
			{
				if( i < nums / 2 - 1 )
				{
					positionX = (int)( ( i + 1 ) * width / ( nums / 2 ) );
					batch.draw( appItemBgLine2 , positionX , y + itemHeight + linewidth , linewidth , itemHeight - linewidth );
				}
				else if( i >= nums / 2 && i < nums - 1 )
				{
					positionX = (int)( ( i + 1 - nums / 2 ) * width / ( nums - nums / 2 ) );
					batch.draw( appItemBgLine2 , positionX , y , linewidth , itemHeight );
				}
			}
		}
		superdraw( batch , parentAlpha );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		int row = (int)( ( this.height - y - R3D.applist_menu_padding_top - 1 ) / itemHeight );
		if( row < 0 )
		{
			row = 0;
		}
		Log.v( "AppPopmenu" , "y = " + y + " row = " + row );
		int nums = 0;
		float itemwidth = 0;
		String title = null;
		if( newlist.size() <= 3 )
		{
			itemwidth = this.width / newlist.size();
			title = newlist.get( (int)( x / itemwidth ) ).name;
		}
		else
		{
			if( row == 0 )
			{
				nums = newlist.size() / 2;
				itemwidth = this.width / nums;
				title = newlist.get( (int)( x / itemwidth ) ).name;
			}
			else if( row == 1 )
			{
				nums = newlist.size() - listItem.size() / 2;
				itemwidth = this.width / nums;
				title = newlist.get( newlist.size() / 2 + (int)( x / itemwidth ) ).name;
			}
		}
		if( title == null )
		{
			return true;
		}
		if( title.equals( R3D.getString( RR.string.mainmenu_bg_alpha ) ) )
		{
			if( DefaultLayout.mainmenu_background_alpha_progress )
				SendMsgToAndroid.sendShowMainmenuBgDialogMsg();
		}
		else if( title.equals( R3D.getString( RR.string.effect_icon ) ) )
		{
			if( RR.net_version )
			{
				final int INDEX_WORKSPACE = 0;
				final int INDEX_APP = 1;
				final int position = Desktop3DListener.root.getAppHost().appList.getEffectType();
				final String ACTION_EFFECT_PREVIEW = "com.cool.action.EffectPreview";
				final String ACTION_EFFECT_PREVIEW_EXTRA_TYPE = "EffectPreviewExtraType";
				final String ACTION_EFFECT_PREVIEW_EXTRA_INDEX = "EffectPreviewExtraIndex";
				Intent it = new Intent( ACTION_EFFECT_PREVIEW );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_TYPE , INDEX_APP );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_INDEX , position );
				if( ( iLoongLauncher.getInstance() != null ) && ( iLoongLauncher.getInstance().getD3dListener() != null ) && ( iLoongLauncher.getInstance().getD3dListener().getRoot() != null ) )
					iLoongLauncher.getInstance().getD3dListener().getRoot().dealEffectPreview( it );
				//				final String TAG_STRING = "currentTab";
				//				final String TAG_THEME = "tagEffect";
				//				final Intent intent = new Intent();
				//				ComponentName mComponent = new ComponentName("com.cooeeui.brand.turbolauncher", "com.coco.theme.themebox.MainActivity");
				//				intent.setComponent(mComponent);
				//				try {
				//					if(DefaultLayout.personal_center_internal){
				//					//	intent.setComponent(new ComponentName(iLoongLauncher.getInstance(), "com.coco.theme.themebox.MainActivity"));
				//						iLoongLauncher.getInstance().bindThemeActivityData(intent);
				//						intent.putExtra(TAG_STRING, TAG_THEME);
				//						SetupMenuActions.getInstance().getContext().startActivity(intent);	
				//					}else{
				//						
				//						PackageManager pm=SetupMenuActions.getInstance().getContext().getPackageManager();
				//						if(pm.queryIntentActivities(intent, 0).size()==0){
				//							iLoongLauncher.getInstance().mMainHandler.post(new Runnable(){
				//
				//								@Override
				//								public void run() {
				//									// TODO Auto-generated method stub
				//									iLoongLauncher.getInstance().themeCenterDown.ToDownloadApkDialog(iLoongLauncher.getInstance(), iLoongLauncher.getInstance().getResources().getString(RR.string.theme), "com.iLoong.base.themebox");
				//								}
				//							});
				//							
				//						}else{
				//							iLoongLauncher.getInstance().bindThemeActivityData(intent);
				//							SetupMenuActions.getInstance().getContext().startActivity(intent);	
				//						}
				//					}
				//				} catch (Exception e) {
				//					e.printStackTrace();
				//				}
			}
			else
			{
				SendMsgToAndroid.sendShowAppEffectDialogMsg();
			}
		}
		else if( title.equals( R3D.getString( RR.string.sort_icon ) ) )
		{
			SendMsgToAndroid.sendShowSortDialogMsg( appList.sortId , iLoongLauncher.SORT_ORIGIN_APPLIST );
		}
		else if( title.equals( R3D.getString( RR.string.hide_icon ) ) )
		{
			appList.setMode( AppList3D.APPLIST_MODE_HIDE );
		}
		else if( title.equals( R3D.getString( RR.string.edit_mode ) ) || title.equals( R3D.getString( RR.string.uninstall_app ) ) )
		{
			appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
		}
		SendMsgToAndroid.sysPlaySoundEffect();
		origin = true;
		viewParent.onCtrlEvent( this , MSG_HIDE_APPLIST_FOCUS );// zqh
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
			int index ,
			int size )
	{
		ViewGroup3D item = new ViewGroup3D( title );
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
			itemY = 0;
			itemX = x + ( index - size / 2 ) * itemWidth;
		}
		item.y = itemY;
		item.x = itemX;
		item.setSize( itemWidth , itemHeight );
		Bitmap b = ThemeManager.getInstance().getBitmap( listIcon.get( index ) );
		final TextureRegion iconRegion = new TextureRegion();
		final TextureRegion titleRegion = new TextureRegion();
		final float[] iconPosition = new float[2];
		final float[] titlePosition = new float[2];
		SetupMenu3D.getIconRegion( b , title , (int)itemWidth , itemHeight , iconRegion , iconPosition );
		SetupMenu3D.getTitleRegion( iconPosition[1] , title , (int)itemWidth , itemHeight , Color.BLACK , titleRegion , titlePosition );
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
		itemTitle.setPosition( item.width / 2 - itemTitle.width / 2 , item.height / 2 - itemTitle.height / 2 );
		item.addView( itemTitle );
		this.addView( item );
		allChildren.add( item );
		newlist.add( item );
	}
	
	public static Bitmap IconToPixmap3D(
			Bitmap temp ,
			String title ,
			int textureWidth ,
			int textureHeight )
	{
		int widthLimit = textureWidth - Tools.dip2px( iLoongLauncher.getInstance() , 12 );
		Paint paint = new Paint();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );// zjp
		FontMetrics fontMetrics = paint.getFontMetrics();
		if( paint.measureText( title ) > widthLimit - 2 )
		{
			while( paint.measureText( title ) > widthLimit - paint.measureText( ".." ) - 2 )
			{
				title = title.substring( 0 , title.length() - 1 );
			}
			title += "..";
		}
		textureWidth = (int)paint.measureText( title );
		Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
		Canvas canvas = new Canvas( bmp );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height = R3D.setupmenu_icon_and_text_spacing;
		float height = textureHeight - singleLineHeight - space_height - R3D.appmenu_icon_padding_top;
		Bitmap bitmap = Tools.scaleBitmap( temp , textureWidth , (int)height );
		float paddingTop;
		float paddingLeft = ( textureWidth - bitmap.getWidth() ) / 2;
		float bmpHeight = bitmap.getHeight();
		paddingTop = ( textureHeight - bmpHeight - space_height - singleLineHeight ) / 2;
		if( paddingTop <= 0 )
			paddingTop = R3D.appmenu_icon_padding_top;
		bmpHeight += ( paddingTop + space_height );
		if( bitmap != null && !bitmap.isRecycled() )
		{
			if( paddingTop <= 0 )
			{
				paddingTop = R3D.appmenu_icon_padding_top;
			}
			if( paddingLeft < 0 )
			{
				paddingLeft = 0;
			}
			canvas.drawBitmap( bitmap , paddingLeft , paddingTop , null );
			bitmap.recycle();
		}
		if( title != null )
		{
			float titleY = textureHeight - paddingTop;
			paint.setColor( Color.BLACK );
			canvas.drawText( title , 0 , titleY - fontMetrics.bottom , paint );
		}
		return bmp;
	}
}
