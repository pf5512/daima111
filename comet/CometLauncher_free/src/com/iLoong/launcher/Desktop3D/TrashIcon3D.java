package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooeecomet.launcher.R;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.desktopEdit.PageContainer;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.widget.Widget;


public class TrashIcon3D extends View3D implements DropTarget3D
{
	
	public final static int MSG_TRASH_DELETE = 0;
	public final static int STATE_DELETE = 1;
	public final static int STATE_UNSTALL = 2;
	private TextureRegion focus;
	private TextureRegion normal;
	private TextureRegion focusBack;
	private Tween hideTween , showTween;
	public static boolean animating = false;
	public boolean dragOver = false;
	public final static int TRASH_POS_TOP = 0;
	public final static int TRASH_POS_MIDDLE = 2;
	public final static int TRASH_POS_RIGHT = 4;
	
	public TrashIcon3D(
			String name )
	{
		super( name );
	}
	
	public TrashIcon3D()
	{
		super( "trashicon" );
		normal = R3D.findRegion( "xiezai-bg" );
		focus = R3D.findRegion( "xiezai-bg2" );
		if( DefaultLayout.hotseat_hide_title && DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			float scale = ( (float)Utils3D.getIconBmpHeight() / (float)R3D.workspace_cell_height );
			float V2 = normal.getV() + ( normal.getV2() - normal.getV() ) * scale;
			normal.setV2( V2 );
			float V3 = focus.getV() + ( focus.getV2() - focus.getV() ) * scale;
			focus.setV2( V3 );
		}
		if( DefaultLayout.trash_icon_pos != TrashIcon3D.TRASH_POS_TOP )
		{
			focusBack = R3D.getTextureRegion( "trash-background" );
		}
		this.region = normal;
		if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_TOP )
		{
			setSize( Utils3D.getScreenWidth() , R3D.trash_icon_height );
			setPosition( 0 , Utils3D.getScreenHeight() );
			originX = width / 2.0f;
		}
		else if( DefaultLayout.trash_icon_pos == TrashIcon3D.TRASH_POS_MIDDLE )
		{
			// float temp =Utils3D.getScreenWidth();
			float posX = ( Utils3D.getScreenWidth() - region.getRegionWidth() ) / 2;
			setPosition( posX , 0 );
			// setPosition(Utils3D.getScreenWidth()*2/5,0);
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
		// setBackgroud(new NinePatch(new
		// Texture(Gdx.files.internal("bgtest.png"))));
	}
	
	@Override
	public void show()
	{
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
			showTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , 0 , Utils3D.getScreenHeight() - this.height , 0 ).setCallback( this );
		}
		animating = true;
		// SendMsgToAndroid.sendHideNoticeMsg();
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
			this.startTween( View3DTweenAccessor.USER , Elastic.OUT , 1f , 0f , 0f , 0f );
			hideTween = this.startTween( View3DTweenAccessor.POS_XY , Cubic.OUT , 0.6f , 0 , Utils3D.getScreenHeight() , 0 ).setCallback( this );
		}
		animating = true;
		// SendMsgToAndroid.sendShowNoticeMsg();
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
			this.region = normal;
		}
		else if( type == TweenCallback.COMPLETE && source == showTween )
		{
			this.startTween( View3DTweenAccessor.USER , Elastic.OUT , 0.8f , 100f , 0f , 0f );
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
				if( info.title != null && info.title.equals( iLoongLauncher.getInstance().getResources().getString( R.string.mainmenu ) ) )
				{
					Messenger.sendMsg( Messenger.MSG_DELETE_MAIN_MENU_TIP , null );
				}
			}
			view.remove(); // delete from parent
			DefaultLayout.onDropToTrash( view );
		}
		// list.clear();
		// 消息回传，供launcher删除widget
		this.setTag( list );
		return viewParent.onCtrlEvent( this , MSG_TRASH_DELETE );
	}
	
	public void set(
			boolean dragOver )
	{
		if( dragOver )
			this.region = focus;
		else
			this.region = normal;
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
			super.draw( batch , parentAlpha );
		}
	}
}
