package com.iLoong.Flashlight.view;


import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DrawDynamicIcon;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.app.IconCache;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class WidgetFlashlight extends WidgetPluginView3D
{
	
	public static Canvas canvas = new Canvas();
	public static Paint paint = new Paint();
	public static FontMetrics fontMetrics = new FontMetrics();
	public static Bitmap bgBitmap;
	public float paddingTop;
	public float paddingLeft;
	private Parameters parameter;
	private Camera camera;
	public static MainAppContext mAppContext;
	private Context mContext = null;
	private MainAppContext mainContext;
	public static WidgetFlashlight widgetFlashlight;
	public static float scale = 1f;
	private boolean isOn = false;
	private View3D bg3D = null;
	
	public static final WidgetFlashlight getInstance()
	{
		return widgetFlashlight;
	}
	
	public WidgetFlashlight(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		widgetFlashlight = this;
		this.mainContext = context;
		this.mContext = context.mWidgetContext;
		mAppContext = context;
		this.width = R3D.workspace_cell_width;
		this.height = R3D.workspace_cell_height;
		scale = Utils3D.getScreenWidth() / 720f;
		camera = Camera.open();
		parameter = camera.getParameters();
		try
		{
			Bitmap tmp = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/flashlight/iLoongFlashlight/image/" + "widget_ico.png" ) );
			if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
			{
				bgBitmap = tmp;
			}
			else
			{
				bgBitmap = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
				tmp.recycle();
			}
			Bitmap b = titleToBitmap( bgBitmap , "手电筒" , null , null , R3D.workspace_cell_width , R3D.workspace_cell_height , true );
			bg3D = new View3D( "cleanBg" , new BitmapTexture( b , true ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		addView( bg3D );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( !isOn )
		{
			parameter.setFlashMode( Parameters.FLASH_MODE_TORCH );
			camera.setParameters( parameter );
			isOn = true;
		}
		else
		{
			parameter.setFlashMode( Parameters.FLASH_MODE_OFF );
			camera.setParameters( parameter );
			isOn = false;
		}
		return true;
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = 1;
		metaData.spanY = 1;
		metaData.maxInstanceCount = 1;
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重复添加";
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/flashlight/iLoongFlashlight/image" + name ) ) , true );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized Bitmap titleToBitmap(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			int textureWidth ,
			int textureHeight ,
			boolean recycle )
	{
		Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		String[] splitTitle;
		float iconbgPaddingTop = 0;
		paddingLeft = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
		float iconcoverplatePaddingTop = 0;
		//		Bitmap icon_cover_plate_bmp = null;
		//		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap("theme/iconbg/icon_cover_plate.png");
		//		if(null != original_icon_cover_bmp){
		//			icon_cover_plate_bmp = Bitmap.createScaledBitmap(original_icon_cover_bmp,
		//					DefaultLayout.app_icon_size, DefaultLayout.app_icon_size,true);
		//		}		
		//		Rect iconbgRect = new Rect();
		//		int iconbgRectTop = 0;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
			if( title != null )
			{
				splitTitle = Utils3D.splitTitle( title , R3D.icon_title_font , textureWidth );
			}
			else
			{
				splitTitle = null;
			}
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
		}
		if( paddingTop < 0 )
			paddingTop = 0;
		iconcoverplatePaddingTop = paddingTop;
		//		iconbgRectTop = (int)paddingTop;
		bmpHeight += ( paddingTop + space_height );
		if( title_bg != null )
			canvas.drawBitmap( title_bg , 0 , bmpHeight + singleLineHeight / 2 - title_bg.getHeight() / 2 , null );
		int saveLayer = -1;
		if( b != null && !b.isRecycled() && Icon3D.getMask() != null && icn_bg != null )
		{
			saveLayer = canvas.saveLayer(
					0 ,
					0 ,
					textureWidth ,
					textureHeight ,
					null ,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG );
		}
		if( b != null && !b.isRecycled() )
		{
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg == null ) )
			{
				//				b = addShadowToBitmap( b , true );
			}
			// paint.setShadowLayer(2f, 0f, 2f, 0xff000000);
			if( b.getHeight() != Utilities.sIconTextureHeight )
			{
				if( icn_bg != null )
				{
					if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f )
					{
						paddingTop = iconbgPaddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
					}
					else
					{
						paddingTop = iconbgPaddingTop;
					}
				}
				else
				{
					paddingTop = paddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
				}
			}
			else
			{
				if( ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f ) && ( DefaultLayout.icon_shadow_radius != 0f ) )
				{
					paddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
			}
			if( b.getWidth() != Utilities.sIconTextureWidth )
			{
				paddingLeft = paddingLeft - ( b.getWidth() - Utilities.sIconTextureWidth ) / 2;
			}
			if( paddingTop < 0 )
			{
				paddingTop = 0;
			}
			if( paddingLeft < 0 )
			{
				paddingLeft = 0;
			}
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f ) )
			{
				paddingTop += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
			}
			if( ( icn_bg != null ) && ( ( R3D.Third_APK_Icon_Offset_X != 0 ) || ( R3D.Third_APK_Icon_Offset_Y != 0 ) ) )
			{
				//				drawThirdAPKIconByOffsetInTheme( b , iconbgRect , iconbgPaddingTop , paddingTop , paddingLeft , canvas );
			}
			else
			//canvas.drawBitmap(b, paddingLeft, paddingTop, null);	//xiatian del
			{
				if( ( DefaultLayout.thirdapk_icon_scaleFactor > 1.0f ) && ( icn_bg != null ) )
				{
					Rect src = new Rect( 0 , 0 , 0 , 0 );
					src.left = ( b.getWidth() - DefaultLayout.app_icon_size ) / 2;
					src.top = ( b.getHeight() - DefaultLayout.app_icon_size ) / 2;
					src.right = src.left + DefaultLayout.app_icon_size;
					src.bottom = src.top + DefaultLayout.app_icon_size;
					Rect dst = new Rect( 0 , 0 , 0 , 0 );
					dst.left = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
					dst.top = (int)iconbgPaddingTop;
					if( DefaultLayout.icon_shadow_radius != 0f )
					{
						dst.top += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					dst.right = dst.left + DefaultLayout.app_icon_size;
					dst.bottom = dst.top + DefaultLayout.app_icon_size;
					canvas.drawBitmap( b , src , dst , null );
				}
				else
				{
					canvas.drawBitmap( b , paddingLeft , paddingTop , null );
				}
			}
			if( recycle && b != IconCache.mDefaultIcon )
				b.recycle();
		}
		if( DefaultLayout.dynamic_icon && icn_bg == null )
		{
			float icony;
			float hotseaty;
			icony = R3D.workspace_cell_height - ( paddingTop + Utilities.sIconTextureHeight ) - ( Math.min( b.getHeight() , b.getWidth() ) - Utilities.sIconTextureHeight ) / 2;
			DrawDynamicIcon.seticony( icony );
			hotseaty = icony + Utils3D.getIconBmpHeight() - R3D.workspace_cell_height;
			DrawDynamicIcon.sethotseaty( hotseaty );
		}
		if( Icon3D.getMask() != null && icn_bg != null && saveLayer != -1 )
		{
			float maskPaddingTop = 0;
			float maskPaddingLeft = 0;
			//			Bitmap icon_mask;
			//			if(DefaultLayout.icon_shadow_radius != 0){
			//				icon_mask = Bitmap.createBitmap(icn_bg.getWidth(), icn_bg.getHeight(),Config.ARGB_8888);
			//				Canvas c = new Canvas(icon_mask);
			//				
			//				c.drawBitmap(Icon3D.getMask(), DefaultLayout.icon_shadow_radius, DefaultLayout.icon_shadow_radius-1, null);
			//			}else{
			//				icon_mask = Icon3D.getMask();
			//			}
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					maskPaddingTop = iconcoverplatePaddingTop - ( Icon3D.getMask().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( DefaultLayout.icon_shadow_radius != 0 )
					{
						if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
							maskPaddingTop = paddingTop;
						else
							maskPaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					if( maskPaddingTop < 0 )
					{
						maskPaddingTop = 0;
					}
				}
				maskPaddingLeft = ( textureWidth - Icon3D.getMask().getWidth() ) / 2;
			}
			paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
			canvas.drawBitmap( Icon3D.getMask() , maskPaddingLeft , maskPaddingTop , paint );
			paint.setXfermode( null );
			canvas.restoreToCount( saveLayer );
		}
		if( ( icn_bg != null ) && Icon3D.getCover() != null )
		{
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					iconcoverplatePaddingTop = iconcoverplatePaddingTop - ( Icon3D.getCover().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( DefaultLayout.icon_shadow_radius != 0 )
					{
						if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
							iconcoverplatePaddingTop = paddingTop;
						else
							iconcoverplatePaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					if( iconcoverplatePaddingTop < 0 )
					{
						iconcoverplatePaddingTop = 0;
					}
				}
				canvas.drawBitmap( Icon3D.getCover() , ( textureWidth - Icon3D.getCover().getWidth() ) / 2 , iconcoverplatePaddingTop , null );
			}
			else
			{
				canvas.drawBitmap( Icon3D.getCover() , 0 , 0 , null );
			}
		}
		if( splitTitle != null && splitTitle.length > 0 )
		{
			for( int i = 0 ; i < splitTitle.length ; i++ )
			{
				if( splitTitle[i].equals( "" ) )
					break;
				paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
				float textWidth = paint.measureText( splitTitle[i] );
				float paddintText = ( textureWidth - textWidth ) / 2;
				if( textWidth > R3D.workspace_cell_width )
				{
					if( title_bg != null )
					{
						paddintText = title_bg.getWidth() / 10f;
						if( textWidth > textureWidth - 2 * paddintText )
						{
							int length = paint.breakText( splitTitle[i] , 0 , splitTitle[i].length() , true , textureWidth - 2 * paddintText , null );
							splitTitle[i] = splitTitle[i].substring( 0 , length + 1 );
						}
					}
					else
					{
						paddintText = 0;
					}
				}
				// float tmpf = (textureHeight - bmpHeight -
				// 2*singleLineHeight)/ 2;
				// if(tmpf < 0)tmpf = 0;
				// float titleY = bmpHeight + tmpf -
				// fontMetrics.top+i*singleLineHeight;
				float titleY = bmpHeight - fontMetrics.ascent + i * singleLineHeight;
				paint.setColor( Color.WHITE );
				if( title_bg == null )
				{
					paint.setShadowLayer( 2.0f , 0.0f , 2.0f , 0xDD000000 );
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					//teapotXu add start for eton specific requirement
					if( DefaultLayout.setupmenu_yitong && DefaultLayout.icon_title_font == 15 )
					{
						// do nothing
					}
					else
					{
						//						paint.setShadowLayer( SHADOW_SMALL_RADIUS , 0.0f , 0.0f , SHADOW_SMALL_COLOUR );
						canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					}
				}
				else
				{
					paint.clearShadowLayer();
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
				}
				// draw opacity
				if( textWidth > textureWidth && title_bg == null )
				{
					Paint mErasePaint = new Paint();
					mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
					mErasePaint.setAntiAlias( true );
					float alphaW = paint.measureText( "x" );
					float a = 255f / alphaW;
					for( int j = 0 ; j < alphaW ; j++ )
					{
						mErasePaint.setAlpha( (int)( a * j ) );
						canvas.drawLine( textureWidth - j - 1 , (float)( titleY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , textureWidth - j , textureHeight , mErasePaint );
					}
				}
			}
		}
		return bmp;
	}
}
