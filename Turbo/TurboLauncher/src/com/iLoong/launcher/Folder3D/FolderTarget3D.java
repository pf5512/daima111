package com.iLoong.launcher.Folder3D;


import java.util.ArrayList;

import android.graphics.Bitmap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DropTarget3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class FolderTarget3D extends View3D implements DropTarget3D
{
	
	private TextureRegion title;
	private TextureRegion folder_normal;
	private TextureRegion folder_focus;
	private float mScale = 1.0f;
	private NinePatch folder_bg_normal;
	private NinePatch folder_bg_focus;
	private Tween hideTween , showTween;
	private boolean is_focus = false;
	public static boolean animating = false;
	public boolean dragOver = false;
	public static final int MSG_FOLDERTARGET_BACKTO_ORIG = 0;
	public final static int FLAG_INVISIBLE = 0;
	public final static int FLAG_VISIBLE = 1;
	private static int visibleFlag = FLAG_VISIBLE;// FLAG_INVISIBLE;
	
	public FolderTarget3D(
			String name )
	{
		super( name );
	}
	
	public FolderTarget3D()
	{
		super( "folderTarget" );
		//		if ( DefaultLayout.isScaleBitmap){
		//			normal = Tools.getTextureByPicName( "theme/pack_source/create_folder.png" , Utils3D.getScreenWidth() / 2  ,  R3D.toolbar_height );
		//			focus = Tools.getTextureByPicName( "theme/pack_source/create_folder2.png" , Utils3D.getScreenWidth() / 2  ,  R3D.toolbar_height );
		//		}else{
		//			normal = R3D.findRegion( "create_folder" );
		//			focus = R3D.findRegion( "create_folder2" );
		//		}
		Bitmap bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/trash_folder_bg_normal.png" );
		Texture t = new BitmapTexture( bm );
		if( folder_bg_normal == null )
			folder_bg_normal = new NinePatch( new TextureRegion( t ) , 1 , 5 , 0 , 0 );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/trash_folder_bg_focus.png" );
		t = new BitmapTexture( bm );
		if( folder_bg_focus == null )
			folder_bg_focus = new NinePatch( new TextureRegion( t ) , 1 , 5 , 0 , 0 );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/folder_normal.png" );
		if( folder_normal == null )
			folder_normal = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/folder_focus.png" );
		if( folder_focus == null )
			folder_focus = new TextureRegion( new BitmapTexture( bm ) );
		bm.recycle();
		if( folder_focus != null )
			mScale = (float)R3D.toolbar_icon_region_height / folder_focus.getRegionHeight();
		//this.region = normal;
		setSize( Utils3D.getScreenWidth() / 2 , R3D.toolbar_height );
		setPosition( 0 , Utils3D.getScreenHeight() );
		title = R3D.findRegion( R3D.getString( RR.string.Create_folder ) );
		originX = width / 2.0f;
		originY = height / 2.0f;
	}
	
	public FolderTarget3D(
			String name ,
			Texture texture )
	{
		super( name , texture );
	}
	
	public FolderTarget3D(
			String name ,
			TextureRegion region )
	{
		super( name , region );
	}
	
	@Override
	public void show()
	{
		if( visibleFlag == FLAG_INVISIBLE )
			return;
		super.show();
		if( hideTween != null )
		{
			hideTween.free();
			hideTween = null;
		}
		this.setUser( 0f );
		this.stopTween();
		showTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , this.x , Utils3D.getScreenHeight() - R3D.toolbar_height/* this.height */, 0 ).setCallback( this );
		animating = true;
	}
	
	@Override
	public void hide()
	{
		if( visibleFlag == FLAG_INVISIBLE )
			return;
		this.stopTween();
		this.startTween( View3DTweenAccessor.USER , Elastic.OUT , 1f , 0f , 0f , 0f );
		hideTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , this.x , Utils3D.getScreenHeight() , 0 ).setCallback( this );
		animating = true;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		// TODO Auto-generated method stub
		if( type == TweenCallback.COMPLETE && source == hideTween )
		{
			super.hide();
			hideTween = null;
			//this.region = normal;
			is_focus = false;
		}
		else if( type == TweenCallback.COMPLETE && source == showTween )
		{
			showTween = null;
		}
		animating = false;
	}
	
	@Override
	public boolean onDrop(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( visibleFlag == FLAG_INVISIBLE )
			return false;
		if( list.size() <= 0 )
		{
			Log.e( "ondrop" , "FolderTarget3D list size <= 0!!!" );
			return true;
		}
		if( list.size() > R3D.folder_max_num )
		{
			this.setTag( list );
			viewParent.onCtrlEvent( this , MSG_FOLDERTARGET_BACKTO_ORIG );
			Log.e( "ondrop" , "FolderTarget3D list size > R3D.folder_max_num!!!" );
			SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.exceed_num_add_icon ) );
			return true;
		}
		for( View3D view : list )
		{
			if( !( view instanceof Icon3D ) )
			{
				Log.e( "ondrop" , "FolderTarget3D not icon3D!!!" );
				return true;
			}
		}
		iLoongLauncher.getInstance().add3DFolder( list );
		// for (View3D view : list) {
		// if (view instanceof IconBase3D) {
		// ItemInfo info = ((IconBase3D) view).getItemInfo();
		// Root3D.deleteFromDB(info);
		// }
		// view.remove(); // delete from parent
		// DefaultLayout.onDropToTrash(view);
		// }
		// // list.clear();
		// //消息回传，供launcher删除widget
		// this.setTag(list);
		// return viewParent.onCtrlEvent(this, MSG_TRASH_DELETE);
		return true;
	}
	
	public void set(
			boolean dragOver )
	{
		if( dragOver )
			is_focus = true;//this.region = focus;
		else
			is_focus = false;//this.region = normal;
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		if( visibleFlag == FLAG_INVISIBLE )
			return false;
		return true;
	}
	
	public static void setVisibleFlag(
			int flag )
	{
		visibleFlag = flag;
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		if( is_focus )
		{
			folder_bg_focus.draw( batch , x , y , width , height );
			batch.draw( folder_focus , x + width / 2 - ( folder_focus.getRegionWidth() * mScale ) / 2 , y , folder_focus.getRegionWidth() * mScale , R3D.toolbar_icon_region_height );
		}
		else
		{
			folder_bg_normal.draw( batch , x , y , width , height );
			batch.draw( folder_normal , x + width / 2 - ( folder_normal.getRegionWidth() * mScale ) / 2 , y , folder_normal.getRegionWidth() * mScale , R3D.toolbar_icon_region_height );
		}
		//int x = Math.round( this.x );
		//int y = Math.round( this.y );
		//		if( region.getTexture() == null )
		//			return;
		//batch.draw( region , x , y , Utils3D.getScreenWidth() / 2 , R3D.toolbar_icon_region_height );
		//int titleW = title.getRegionWidth();
		//batch.draw(title, x+(width-titleW)/2 , y + R3D.toolbar_title_region_y);
	}
}
