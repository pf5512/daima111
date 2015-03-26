package com.iLoong.launcher.Widget3D;


import javax.microedition.khronos.opengles.GL11;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.AppBar3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DragLayer3D;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class Widget3DVirtual extends WidgetIcon
{
	
	private String title;
	private String cellTitle;
	private Bitmap preview;
	private TextureRegion titleRegion;
	private TextureRegion[] cellTitleRegion = new TextureRegion[2];
	private TextureRegion previewRegion;
	private String packageName;
	public boolean uninstalled = false;
	public int iconResourceId;
	public boolean isInternal = false;
	public static final int MSG_WIDGET3D_SHORTCUT_LONGCLICK = 0;
	public static final int MSG_WIDGET3D_SHORTCUT_CLICK = 1;
	
	public Widget3DVirtual(
			String name ,
			Bitmap bmp ,
			String title )
	{
		super( name );
		preview = bmp;
	}
	
	public Bitmap getPreviewBitmap()
	{
		return preview;
	}
	
	public boolean onDoubleClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public boolean onLongClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		boolean isLoadByInternal = DefaultLayout.isWidgetLoadByInternal( info.intent.getComponent().getPackageName() );
		if( isLoadByInternal )
		{
			Vector2 point = new Vector2();
			point.x = x;
			point.y = y;
			this.toAbsolute( point );
			float longClickX = point.x;
			float longClickY = point.y;
			point.x = width / 2;
			point.y = height / 2;
			this.toAbsolute( point );
			this.setTag( new Vector2( point.x , point.y ) );
			Log.d( "launcher" , "long click:" + point.x + "," + point.y );
			// clingState = ClingManager.CLING_STATE_DISMISSED;
			// ClingManager.getInstance().cancelWidgetCling();
			DragLayer3D.dragStartX = longClickX;
			DragLayer3D.dragStartY = longClickY;
			return viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_LONGCLICK );
		}
		else
		{
			if( info.intent != null && info.intent.getAction().equals( Intent.ACTION_PACKAGE_INSTALL ) )
			{
				SendMsgToAndroid.sendOurToastMsg( R3D.getString( RR.string.download_to_install ) );
				return true;
			}
			return super.onLongClick( x , y );
		}
	}
	
	public boolean onClick(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		Log.v( "click" , "View3D onClick:" + name + " x:" + x + " y:" + y );
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		//		if (uninstall) {
		//			// PreferenceManager.getDefaultSharedPreferences(
		//			// iLoongLauncher.getInstance()).edit().putBoolean("UNINSTALL:"+packageName,true).commit();
		//			// this.remove();
		//			// uninstalled = true;
		//			return true;
		//		} else if (hide) {
		//			ComponentName name = info.intent.getComponent();
		//			if (name != null) {
		//				isHide = !isHide;
		//				PreferenceManager
		//						.getDefaultSharedPreferences(
		//								iLoongLauncher.getInstance()).edit()
		//						.putBoolean("HIDE:" + name.toString(), isHide).commit();
		//			}
		//			return true;
		//		}
		Vector2 point = new Vector2();
		point.x = x;
		point.y = y;
		this.toAbsolute( point );
		float longClickX = point.x;
		float longClickY = point.y;
		point.x = width / 2;
		point.y = height / 2;
		this.toAbsolute( point );
		this.setTag( new Vector2( point.x , point.y ) );
		DragLayer3D.dragStartX = longClickX;
		DragLayer3D.dragStartY = longClickY;
		boolean isLoadByInternal = DefaultLayout.isWidgetLoadByInternal( info.intent.getComponent().getPackageName() );
		if( !isLoadByInternal )
		{
			return WidgetDownload.checkToDownload( info , true );
		}
		return viewParent.onCtrlEvent( this , MSG_WIDGET3D_SHORTCUT_CLICK );
	}
	
	@Override
	public void setSize(
			float w ,
			float h )
	{
		super.setSize( w , h );
		this.setOrigin( w / 2 , h / 2 );
	}
	
	public void makeShortcut()
	{
		if( titleRegion == null || cellTitleRegion == null || previewRegion == null )
		{
			ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
			packageName = info.intent.getComponent().getPackageName();
			title = info.title.toString();
			if( title.equals( "clock" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.clock );
			}
			if( title.equals( "memo" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.memo );
			}
			if( title.equals( "robot" ) )
			{
				title = iLoongLauncher.getInstance().getResources().getString( RR.string.robot );
			}
			// int minWidth = info.minWidth;
			// int minHeight = info.minHeight;
			// int[] span =
			// CellLayout3D.rectToCell(iLoongApplication.ctx.getResources(),
			// minWidth, minHeight, null);
			cellTitle = info.spanX + "x" + info.spanY;
			titleRegion = new TextureRegion( new BitmapTexture( AppBar3D.titleToPixmap(
					title ,
					(int)( width * 3 / 4 ) ,
					(int)( R3D.widget_preview_title_weight * height * 0.8f ) ,
					AppBar3D.TEXT_ALIGN_LEFT ,
					AppBar3D.TEXT_ALIGN_CENTER ,
					true ,
					Color.WHITE ) , true ) );
			cellTitleRegion[0] = R3D.findRegion( info.spanX + "" );
			cellTitleRegion[1] = R3D.findRegion( info.spanY + "" );
			if( preview != null )
			{
				float scale = 1 , scale1 = 1 , scale2 = 1;
				if( preview.getWidth() > width * 0.8f )
				{
					scale1 = width * 0.8f / (float)preview.getWidth();
				}
				if( preview.getHeight() * scale > height * 0.6f )
				{
					scale2 = height * 0.6f / (float)preview.getHeight();
				}
				scale = scale1 > scale2 ? scale2 : scale1;
				int bitmapWidth = (int)( scale * preview.getWidth() );
				int bitmapHeight = (int)( scale * preview.getHeight() );
				Bitmap bitmap = Bitmap.createScaledBitmap( preview , bitmapWidth , bitmapHeight , false );
				if( !bitmap.equals( preview ) )
					preview.recycle();
				previewRegion = new TextureRegion( new BitmapTexture( bitmap , true ) );
				previewRegion.getTexture().setFilter( TextureFilter.Linear , TextureFilter.Linear );
			}
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		//		Bitmap bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator_current2.png");
		//		Texture t = new BitmapTexture(bmIndicator);				
		//		TextureRegion  selectedIndicator = new TextureRegion(t);
		//		batch.draw(selectedIndicator, x, y, width, height);
		float alpha = color.a;
		if( isHide && ( uninstall || hide ) )
		{
			alpha *= 0.2;
		}
		batch.setColor( color.r , color.g , color.b , alpha * parentAlpha );
		int srcBlendFunc = 0 , dstBlendFunc = 0;
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			/* 获取获取混合方式 */
			srcBlendFunc = batch.getSrcBlendFunc();
			dstBlendFunc = batch.getDstBlendFunc();
			if( srcBlendFunc != GL11.GL_SRC_ALPHA || dstBlendFunc != GL11.GL_ONE_MINUS_SRC_ALPHA )
				batch.setBlendFunction( GL11.GL_SRC_ALPHA , GL11.GL_ONE_MINUS_SRC_ALPHA );
		}
		if( previewRegion != null )
		{
			TextureRegion bg = R3D.findRegion( "widget-shortcut-bg" );
			if( DefaultLayout.show_widget_shortcut_bg )
			{
				if( bg != null )
				{
					if( DefaultLayout.widget_shortcut_title_top )
					{
						if( is3dRotation() )
						{
							batch.draw( bg , x , y , width , height * ( 1 - R3D.widget_preview_title_weight ) );
						}
						else
						{
							batch.draw(
									bg ,
									x ,
									y ,
									originX ,
									originY - height * R3D.widget_preview_title_weight ,
									width ,
									height * ( 1 - R3D.widget_preview_title_weight ) ,
									scaleX ,
									scaleY ,
									rotation );
						}
					}
					else
					{
						if( is3dRotation() )
						{
							batch.draw( bg , x , y + height * R3D.widget_preview_title_weight , width , height * ( 1 - R3D.widget_preview_title_weight ) );
						}
						else
						{
							batch.draw(
									bg ,
									x ,
									y + height * R3D.widget_preview_title_weight ,
									originX ,
									originY - height * R3D.widget_preview_title_weight ,
									width ,
									height * ( 1 - R3D.widget_preview_title_weight ) ,
									scaleX ,
									scaleY ,
									rotation );
						}
					}
				}
			}
			int drawWidth = (int)( previewRegion.getRegionWidth() );
			if( drawWidth < R3D.workspace_cell_height )
			{
				drawWidth = R3D.workspace_cell_height;
			}
			int drawHeight = (int)( previewRegion.getRegionHeight() );
			if( drawHeight < R3D.workspace_cell_height )
			{
				drawHeight = R3D.workspace_cell_height;
			}
			float previewX = width / 2 - drawWidth / 2;
			float previewY = height * ( 1 - R3D.widget_preview_title_weight ) / 2 - drawHeight / 2 + height * R3D.widget_preview_title_weight;
			if( DefaultLayout.widget_shortcut_title_top )
			{
				previewY = previewY - height * R3D.widget_preview_title_weight;
			}
			if( DefaultLayout.widget_shortcut_lefttop )
			{
				previewX = width / 20;
				previewY = height - height * ( 1 - R3D.widget_preview_title_weight ) / 20 - drawHeight;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					previewY = previewY - height * R3D.widget_preview_title_weight;
				}
			}
			//			batch.draw(previewRegion, previewX + x, previewY + y, drawWidth,
			//					drawHeight);
			if( is3dRotation() )
			{
				batch.draw( previewRegion , previewX + x , previewY + y , drawWidth , drawHeight );
			}
			else
			{
				batch.draw( previewRegion , previewX + x , previewY + y , originX//###
						- previewX , originY - previewY , drawWidth , drawHeight , scaleX , scaleY , rotation );
			}
		}
		if( DefaultLayout.blend_func_dst_gl_one )
		{
			batch.setBlendFunction( srcBlendFunc , dstBlendFunc );
		}
		if( titleRegion != null )
		{
			if( DefaultLayout.widget_shortcut_title_top )
			{
				if( is3dRotation() )
					batch.draw( titleRegion , x , y + height * ( 1 - R3D.widget_preview_title_weight ) );
				else
					batch.draw(
							titleRegion ,
							x ,
							y + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX ,
							originY ,
							titleRegion.getRegionWidth() ,
							titleRegion.getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
			}
			else
			{
				if( is3dRotation() )
					batch.draw( titleRegion , x , y );
				else
					batch.draw( titleRegion , x , y , originX , originY ,//###
							titleRegion.getRegionWidth() ,
							titleRegion.getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
			}
		}
		if( cellTitleRegion != null )
		{
			if( is3dRotation() )
			{
				TextureRegion xRegion = R3D.findRegion( "x" );
				float titleX = 10;
				titleX = (float)( width / 4 - 2 * cellTitleRegion[0].getRegionWidth() - xRegion.getRegionWidth() ) - titleX;
				if( titleX < 0 )
					titleX = 0;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					batch.draw(
							cellTitleRegion[0] ,
							width * 3 / 4 + x + titleX ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
					batch.draw(
							xRegion ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
					batch.draw(
							cellTitleRegion[1] ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) );
				}
				else
				{
					batch.draw( cellTitleRegion[0] , width * 3 / 4 + x + titleX , y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 );
					batch.draw( xRegion , width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() , y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 );
					batch.draw(
							cellTitleRegion[1] ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 );
				}
			}
			else
			{
				TextureRegion xRegion = R3D.findRegion( "x" );//##
				float titleX = 10;
				titleX = (float)( width / 4 - 2 * cellTitleRegion[0].getRegionWidth() - xRegion.getRegionWidth() ) - titleX;
				if( titleX < 0 )
					titleX = 0;
				if( DefaultLayout.widget_shortcut_title_top )
				{
					batch.draw(
							cellTitleRegion[0] ,
							width * 3 / 4 + x + titleX ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX - width * 3 / 4 ,
							originY ,
							cellTitleRegion[0].getRegionWidth() ,
							cellTitleRegion[0].getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
					batch.draw(
							xRegion ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX - width * 3 / 4 ,
							originY ,
							xRegion.getRegionWidth() ,
							xRegion.getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
					batch.draw(
							cellTitleRegion[1] ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 + height * ( 1 - R3D.widget_preview_title_weight ) ,
							originX - width * 3 / 4 ,
							originY ,
							cellTitleRegion[1].getRegionWidth() ,
							cellTitleRegion[1].getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
				}
				else
				{
					batch.draw( //##
							cellTitleRegion[0] ,
							width * 3 / 4 + x + titleX ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[0].getRegionHeight() ) / 2 ,
							originX - width * 3 / 4 ,
							originY ,
							cellTitleRegion[0].getRegionWidth() ,
							cellTitleRegion[0].getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
					batch.draw(
							xRegion ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - xRegion.getRegionHeight() ) / 2 ,
							originX - width * 3 / 4 ,
							originY ,
							xRegion.getRegionWidth() ,
							xRegion.getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
					batch.draw(
							cellTitleRegion[1] ,
							width * 3 / 4 + x + titleX + cellTitleRegion[0].getRegionWidth() + xRegion.getRegionWidth() ,
							y + ( ( R3D.widget_preview_title_weight * height ) - cellTitleRegion[1].getRegionHeight() ) / 2 ,
							originX - width * 3 / 4 ,
							originY ,
							cellTitleRegion[1].getRegionWidth() ,
							cellTitleRegion[1].getRegionHeight() ,
							scaleX ,
							scaleY ,
							rotation );
				}
			}
		}
		batch.setColor( color.r , color.g , color.b , color.a * parentAlpha );
		float stateX = this.x + this.width - stateIconWidth;
		float stateY = this.y + this.height - stateIconHeight;
		if( is3dRotation() )
		{
			if( uninstall )
			{
				// batch.draw(R3D.findRegion("app-uninstall"), stateX,
				// stateY,stateIconWidth,stateIconHeight);
			}
			if( hide && isHide )
			{
				batch.draw( Icon3D.resumeTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
			if( hide && !isHide )
			{
				batch.draw( Icon3D.hideTexture , stateX , stateY , stateIconWidth , stateIconHeight );
			}
		}
		else
		{
			float stateOriginX = originX - this.width + stateIconWidth;
			float stateOriginY = originY - this.height + stateIconHeight;
			if( uninstall )
			{
				// batch.draw(R3D.findRegion("app-uninstall"), stateX, stateY,
				// stateOriginX, stateOriginY,stateIconWidth,stateIconHeight,
				// scaleX,scaleY, rotation);
			}
			if( hide && isHide )
			{
				batch.draw( Icon3D.resumeTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , rotation );
			}
			if( hide && !isHide )
			{
				batch.draw( Icon3D.hideTexture , stateX , stateY , stateOriginX , stateOriginY , stateIconWidth , stateIconHeight , scaleX , scaleY , rotation );
			}
		}
	}
	
	public void removeHide()
	{
		ShortcutInfo info = (ShortcutInfo)this.getItemInfo();
		ComponentName name = info.intent.getComponent();
		if( name != null )
		{
			PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().remove( "HIDE:" + name.toString() ).commit();
		}
	}
}
