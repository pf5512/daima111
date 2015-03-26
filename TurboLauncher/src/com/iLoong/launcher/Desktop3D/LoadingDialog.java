package com.iLoong.launcher.Desktop3D;


import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.SetupMenu.ImageUtils;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;


public class LoadingDialog extends View
{
	
	private Object lock = new Object();
	private Bitmap imgBackground;
	private Bitmap imgTitle;
	private Bitmap imgLoading;
	private final String BG_NAME_EG = "launcher/loading/bg-en.jpg";
	private final String LOAD_TITLE_EG = "launcher/loading/title-en.png";
	private final String LOAD_TITLE_CN = "launcher/loading/title-zh.png";
	private final String LOAD_TIP = "launcher/loading/loading.png";
	private Matrix matrix;
	private Paint mBitmapPaint;
	private float mScale;
	private int viewHeight = 0;
	private int viewWidth = 0;
	
	public LoadingDialog(
			Context context )
	{
		super( context );
		mScale = ( Utils3D.getScreenHeight() ) / 1280.0f;
		Resources res = context.getResources();
		viewWidth = res.getDisplayMetrics().widthPixels;
		viewHeight = res.getDisplayMetrics().heightPixels;
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			imgTitle = ThemeManager.getInstance().getBitmap( LOAD_TITLE_CN );
		}
		else
		{
			imgTitle = ThemeManager.getInstance().getBitmap( LOAD_TITLE_EG );
		}
		imgLoading = ThemeManager.getInstance().getBitmap( LOAD_TIP );
		imgBackground = getImageFromAssetsFile( BG_NAME_EG );
		imgTitle = Tools.resizeBitmap( imgTitle , mScale );
		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap( true );
		mBitmapPaint.setAntiAlias( true );
	}
	
	public LoadingDialog(
			Context context ,
			AttributeSet attrs )
	{
		// TODO Auto-generated constructor stub
		super( context , attrs );
	}
	
	public LoadingDialog(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		// TODO Auto-generated constructor stub
		super( context , attrs , defStyle );
	}
	
	@Override
	protected void onSizeChanged(
			int w ,
			int h ,
			int oldw ,
			int oldh )
	{
		// TODO Auto-generated method stub
		super.onSizeChanged( w , h , oldw , oldh );
		if( imgBackground != null )
		{
			float scaleWidth = ( (float)getWidth() ) / imgBackground.getWidth();
			float scaleHeight = ( (float)getHeight() ) / imgBackground.getHeight();
			matrix = new Matrix();
			matrix.postScale( scaleWidth , scaleHeight );
		}
	}
	
	@Override
	protected void onDraw(
			Canvas canvas )
	{
		// TODO Auto-generated method stub
		super.onDraw( canvas );
		synchronized( lock )
		{
			if( imgBackground != null )
			{
				if( imgBackground != null && !imgBackground.isRecycled() )
					canvas.drawBitmap( imgBackground , 0 , 0 , mBitmapPaint );
				canvas.drawBitmap( imgTitle , ( Utils3D.getScreenWidth() - imgTitle.getWidth() ) / 2.0f , 664 * mScale + imgTitle.getHeight() , mBitmapPaint );
				canvas.drawBitmap( imgLoading , ( Utils3D.getScreenWidth() - imgLoading.getWidth() ) / 2.0f , 820 * mScale + imgTitle.getHeight() , mBitmapPaint );
			}
		}
	}
	
	private Bitmap getImageFromAssetsFile(
			String fileName )
	{
		Bitmap image = null;
		AssetManager am = getResources().getAssets();
		try
		{
			InputStream is = am.open( fileName );
			image = ImageUtils.zoomBitmap( is , Utils3D.getScreenWidth() , iLoongLauncher.getInstance().getResources().getDisplayMetrics().heightPixels ); //  BitmapFactory.decodeStream( is );
			is.close();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return image;
	}
	
	public void destory()
	{
		synchronized( lock )
		{
			if( imgBackground != null )
			{
				imgBackground.recycle();
				imgBackground = null;
			}
			if( imgTitle != null )
			{
				imgTitle.recycle();
				imgTitle = null;
			}
			if( imgLoading != null )
			{
				imgLoading.recycle();
				imgLoading = null;
			}
		}
	}
}
