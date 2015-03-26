package com.iLoong.launcher.Desktop3D;


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
import com.iLoong.launcher.DesktopEdit.CustomShortcutIcon;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;


public class TrashIcon3D extends View3D implements DropTarget3D
{
	
	public final static int MSG_TRASH_DELETE = 0;
	public final static int STATE_DELETE = 1;
	public final static int STATE_UNSTALL = 2;
	private TextureRegion focus;
	private TextureRegion normal;
	private TextureRegion focusBack;
	//teapotXu add start
	private TextureRegion focus_whole_screen;
	private TextureRegion normal_whole_screen;
	private TextureRegion focus_right_screen;
	private TextureRegion normal_right_screen;
	//teapotXu add end
	private Tween hideTween , showTween;
	public static boolean animating = false;
	public boolean dragOver = false;
	public final static int TRASH_POS_TOP = 0;
	public final static int TRASH_POS_MIDDLE = 2;
	public final static int TRASH_POS_RIGHT = 4;
	public final static int TRASH_POS_TOP_FLAG_MIDDLE = 0;
	public final static int TRASH_POS_TOP_FLAG_RIGHT = 1;
	private static int posFlag = TRASH_POS_TOP_FLAG_MIDDLE;
	private TextureRegion trash_normal;
	private TextureRegion trash_focus;
	private NinePatch trash_bg_normal;
	private NinePatch trash_bg_focus;
	private float mScale = 1.0f;
	private boolean is_foucs = false;
	
	public TrashIcon3D(
			String name )
	{
		super( name );
	}
	
	public TrashIcon3D()
	{
		super( "trashicon" );
		if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			if( DefaultLayout.isScaleBitmap )
			{
				normal = Tools.getTextureByPicName( "theme/pack_source/xiezai-bg-top.png" , Utils3D.getScreenWidth() / 2 , R3D.trash_icon_height );
				focus = Tools.getTextureByPicName( "theme/pack_source/xiezai-bg2-top.png" , Utils3D.getScreenWidth() / 2 , R3D.trash_icon_height );
			}
			else
			{
				normal = R3D.findRegion( "xiezai-bg" );
				focus = R3D.findRegion( "xiezai-bg2" );
			}
			if( DefaultLayout.hotseat_hide_title )
			{
				float scale = ( (float)Utils3D.getIconBmpHeight() / (float)R3D.workspace_cell_height );
				float V2 = normal.getV() + ( normal.getV2() - normal.getV() ) * scale;
				normal.setV2( V2 );
				float V3 = focus.getV() + ( focus.getV2() - focus.getV() ) * scale;
				focus.setV2( V3 );
			}
			focusBack = R3D.getTextureRegion( "trash-background" );
			this.region = normal;
		}
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			Bitmap bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/trash_folder_bg_normal.png" );
			Texture t = new BitmapTexture( bm );
			if( trash_bg_normal == null )
				trash_bg_normal = new NinePatch( new TextureRegion( t ) , 1 , 5 , 0 , 0 );
			bm.recycle();
			bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/trash_folder_bg_focus2.png" );
			t = new BitmapTexture( bm );
			if( trash_bg_focus == null )
				trash_bg_focus = new NinePatch( new TextureRegion( t ) , 1 , 5 , 0 , 0 );
			bm.recycle();
			bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/trash_icon_normal.png" );
			if( trash_normal == null )
				trash_normal = new TextureRegion( new BitmapTexture( bm ) );
			bm.recycle();
			bm = ThemeManager.getInstance().getBitmap( "theme/pack_source/trash_icon_focus.png" );
			if( trash_focus == null )
				trash_focus = new TextureRegion( new BitmapTexture( bm ) );
			bm.recycle();
			if( trash_focus != null )
				mScale = (float)R3D.trash_icon_height / trash_focus.getRegionHeight();
			setSize( Utils3D.getScreenWidth() , R3D.trash_icon_height );
			setPosition( 0 , Utils3D.getScreenHeight() );
			originX = width / 2.0f;
			//teaotXu add start
			//			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			//			{
			//				if( DefaultLayout.isScaleBitmap )
			//				{
			//					normal_whole_screen = Tools.getTextureByPicName( "theme/pack_source/xiezai-bg-top2.png" , Utils3D.getScreenWidth() , R3D.trash_icon_height );
			//					focus_whole_screen = Tools.getTextureByPicName( "theme/pack_source/xiezai-bg2-top2.png" , Utils3D.getScreenWidth() , R3D.trash_icon_height );
			//					normal_right_screen = normal;
			//					focus_right_screen = focus;
			//				}
			//				else
			//				{
			//					normal_whole_screen = R3D.getTextureRegion( "xiezai-bg_screen_width" );
			//					focus_whole_screen = R3D.getTextureRegion( "xiezai-bg2_screen_width" );
			//					normal_right_screen = R3D.findRegion( "xiezai-bg" );
			//					focus_right_screen = R3D.findRegion( "xiezai-bg2" );
			//				}
			//			}
			//teapotXu add end
		}
		else if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_MIDDLE )
		{
			//float temp =Utils3D.getScreenWidth();
			float posX = ( Utils3D.getScreenWidth() - region.getRegionWidth() ) / 2;
			setPosition( posX , 0 );
			//setPosition(Utils3D.getScreenWidth()*2/5,0);
			setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
			originX = width / 2.0f;
		}
		else
		{
			int mCellWidth = Utils3D.getScreenWidth() / ( DefaultLayout.hot_dock_icon_number + 1 );
			setPosition( R3D.hot_dock_icon_number * mCellWidth + ( mCellWidth - R3D.workspace_cell_width ) / 2 , 0 );
			originX = Utils3D.getScreenWidth() / ( DefaultLayout.hot_dock_icon_number + 1 ) - Utils3D.getScreenWidth() / 2.0f;
			setSize( R3D.trash_icon_width , R3D.workspace_cell_height );
		}
		originY = height / 2.0f;
	}
	
	public TrashIcon3D(
			String name ,
			Texture texture )
	{
		super( name , texture );
	}
	
	public TrashIcon3D(
			String name ,
			TextureRegion region )
	{
		super( name , region );
		//		setBackgroud(new NinePatch(new Texture(Gdx.files.internal("bgtest.png"))));
	}
	
	public void onThemeChanged()
	{
		if( DefaultLayout.hotseat_hide_title && DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			float scale = ( (float)Utils3D.getIconBmpHeight() / (float)R3D.workspace_cell_height );
			float V2 = normal.getV() + ( normal.getV2() - normal.getV() ) * scale;
			normal.setV2( V2 );
			float V3 = focus.getV() + ( focus.getV2() - focus.getV() ) * scale;
			focus.setV2( V3 );
		}
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			setSize( Utils3D.getScreenWidth() , R3D.trash_icon_height );
			setPosition( 0 , Utils3D.getScreenHeight() );
		}
		else if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_MIDDLE )
		{
			float posX = ( Utils3D.getScreenWidth() - region.getRegionWidth() ) / 2;
			setPosition( posX , 0 );
			setSize( R3D.workspace_cell_width , R3D.workspace_cell_height );
		}
		else
		{
			int mCellWidth = Utils3D.getScreenWidth() / ( DefaultLayout.hot_dock_icon_number + 1 );
			setPosition( R3D.hot_dock_icon_number * mCellWidth + ( mCellWidth - R3D.workspace_cell_width ) / 2 , 0 );
			setSize( R3D.trash_icon_width , R3D.workspace_cell_height );
		}
		originX = Utils3D.getScreenWidth() / 2.0f - this.x;
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			originY = height / 2.0f;
		}
		else
		{
			originY = -R3D.hot_grid_bottom_margin + R3D.hot_obj_height / 2.0f;
		}
	}
	
	@Override
	public void show()
	{
		SendMsgToAndroid.hideStatusBar();
		super.show();
		if( hideTween != null )
		{
			hideTween.free();
			hideTween = null;
		}
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			this.setUser( 0f );
			this.stopTween();
			//teapotXu add start for new_folder in top-trah bar
			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				showTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.2f , this.x , Utils3D.getScreenHeight() - R3D.toolbar_height , 0 ).delay( 0.1f ).setCallback( this );
			}
			else
			{
				showTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , 0 , Utils3D.getScreenHeight() - this.height , 0 ).delay( 0.3f ).setCallback( this );
			}
			//teapotXu add end
		}
		animating = true;
		//		SendMsgToAndroid.sendHideNoticeMsg();
	}
	
	@Override
	public void hide()
	{
		if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			super.hide();
		}
		else
		{
			this.stopTween();
			this.startTween( View3DTweenAccessor.USER , Cubic.IN , 0.6f , 0f , 0f , 0f );
			//teapotXu add start for new folder in top-trash bar
			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				hideTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.IN , 0.3f , this.x , Utils3D.getScreenHeight() , 0 ).setCallback( this );
			}
			else
			//teapotXu add end
			{
				hideTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.IN , 0.5f , 0 , Utils3D.getScreenHeight() , 0 ).setCallback( this );
			}
		}
		animating = true;
		//		SendMsgToAndroid.sendShowNoticeMsg();
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
			if( normal != null )
				this.region = normal;
			is_foucs = false;
			SendMsgToAndroid.showStatusBar();
		}
		else if( type == TweenCallback.COMPLETE && source == showTween )
		{
			if( !DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				this.startTween( View3DTweenAccessor.USER , Elastic.OUT , 0.8f , 100f , 0f , 0f );
			}
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
		if( list.size() <= 0 )
		{
			Log.e( "ondrop" , "list size <= 0!!!" );
			return true;
		}
		for( View3D view : list )
		{
			if( view instanceof IconBase3D )
			{
				ItemInfo info = ( (IconBase3D)view ).getItemInfo();
				Root3D.deleteFromDB( info );
				if( info.title != null && info.title.equals( CustomShortcutIcon.CUSTOM_SHORTCUT_TITLE_APPLIST ) )
				{
					Messenger.sendMsg( Messenger.MSG_DELETE_MAIN_MENU_TIP , null );
				}
			}
			view.remove(); // delete from parent
			DefaultLayout.onDropToTrash( view );
		}
		//			list.clear();
		//消息回传，供launcher删除widget
		this.setTag( list );
		return viewParent.onCtrlEvent( this , MSG_TRASH_DELETE );
	}
	
	public void forceRecycle(
			ArrayList<View3D> list )
	{
		this.onDrop( list , 0 , 0 );
	}
	
	public void set(
			boolean dragOver )
	{
		if( dragOver )
		{
			if( focus != null )
				this.region = focus;
			is_foucs = true;
		}
		else
		{
			if( normal != null )
				this.region = normal;
			is_foucs = false;
		}
	}
	
	@Override
	public boolean onDropOver(
			ArrayList<View3D> list ,
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public void setPosFlag(
			int flag )
	{
		if( !( DefaultLayout.generate_new_folder_in_top_trash_bar && DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP ) )
			return;
		//		switch( flag )
		//		{
		//			case TRASH_POS_TOP_FLAG_MIDDLE:
		//				setSize( Utils3D.getScreenWidth() , R3D.trash_icon_height );
		//				setPosition( 0 , Utils3D.getScreenHeight() );
		//				originX = width / 2.0f;
		//				normal = normal_whole_screen;
		//				focus = focus_whole_screen;
		//				this.region = normal;
		//				posFlag = flag;
		//				break;
		//			case TRASH_POS_TOP_FLAG_RIGHT:
		//				setSize( Utils3D.getScreenWidth() / 2 , R3D.trash_icon_height );
		//				setPosition( Utils3D.getScreenWidth() / 2 , Utils3D.getScreenHeight() );
		//				originX = width / 2.0f;
		//				normal = normal_right_screen;
		//				focus = focus_right_screen;
		//				this.region = normal;
		//				posFlag = flag;
		//				break;
		//		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			int x = Math.round( this.x );
			int y = Math.round( this.y );
			if( region.getTexture() == null )
				return;
			if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_MIDDLE )
			{
				if( region.equals( focus ) )
				{
					batch.draw( focusBack , x , y + R3D.hot_grid_bottom_margin , focus.getRegionWidth() , height );
				}
			}
			else
			{
				if( region.equals( focus ) )
				{
					batch.draw( focusBack , x - this.region.getRegionWidth() , y , width , height );
				}
			}
			batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
			batch.draw( region , x , y + R3D.hot_grid_bottom_margin , originX , originY , this.region.getRegionWidth() , this.region.getRegionHeight() , scaleX , scaleY , rotation );
		}
		else
		{
			if( DefaultLayout.generate_new_folder_in_top_trash_bar )
			{
				batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
				if( is_foucs )
				{
					trash_bg_focus.draw( batch , x , y , width , height );
					batch.draw( trash_focus , x + width / 2 - ( trash_focus.getRegionWidth() * mScale ) / 2 , y , trash_focus.getRegionWidth() * mScale , R3D.trash_icon_height );
				}
				else
				{
					trash_bg_normal.draw( batch , x , y , width , height );
					batch.draw( trash_normal , x + width / 2 - ( trash_normal.getRegionWidth() * mScale ) / 2 , y , trash_normal.getRegionWidth() * mScale , R3D.trash_icon_height );
				}
				//				int x = Math.round( this.x );
				//				int y = Math.round( this.y );
				//				if( region.getTexture() == null )
				//					return;
				//				if( posFlag == TRASH_POS_TOP_FLAG_RIGHT )
				//				{
				//					batch.draw( region , Utils3D.getScreenWidth() / 2 , y , Utils3D.getScreenWidth() / 2 , R3D.toolbar_icon_region_height );
				//				}
				//				else if( posFlag == TRASH_POS_TOP_FLAG_MIDDLE )
				//				{
				//					batch.draw( region , x , y , Utils3D.getScreenWidth() , R3D.toolbar_icon_region_height );
				//				}
			}
			else
			{
				super.draw( batch , parentAlpha );
			}
		}
	}
}
