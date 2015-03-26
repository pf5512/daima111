package com.iLoong.launcher.action;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.FontMetrics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class ActionTitleUtil
{
	private  Paint paint = new Paint();
	private  Canvas canvas = new Canvas();
	private  FontMetrics fontMetrics = new FontMetrics();
	private  float mScale=1;
	private float mFontSize=R3D.icon_title_font ;
	private float singleLineHeight;
	private float textWidth ;
	private String title="unspecified string";
	private int color = Color.WHITE;
	public ActionTitleUtil(){
		mScale=Utils3D.getScreenHeight()/720.0f;
		
		setFontSize(this.mFontSize);
	}
	
	public void setFontSize(float fontsize){
		this.mFontSize=fontsize;
		paint.reset();
		paint.setColor( color);
		paint.setAntiAlias( true );
		paint.setTextSize( mFontSize );
		paint.getFontMetrics( fontMetrics );
		paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );
	    singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );

		textWidth= paint.measureText( title );
	}
	
	public void setPaintColor(int color){
		this.color=color;
	}
	
	
	public void setTitle(String title){
		this.title=title;
		textWidth= paint.measureText( title );
	}
	
	public float getTitleWidth(String title){
		return 	paint.measureText( title );
	}
	
	
	
	public float getTitleWidth(){
		return paint.measureText( title );
	}
	
	public float getTitleHeight(){
		return singleLineHeight;
	}
	
	public Bitmap drawTitleBitmap(
			String title )
	{
		
		Bitmap bmp = Bitmap.createBitmap( (int)( textWidth ) + measureSize( 6 ) , (int)singleLineHeight + measureSize( 6 ) , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		float paddintText = measureSize( 3 );
		float titleY = singleLineHeight;//measureSize( 3 );
		canvas.drawText( title , paddintText , titleY , paint );
		return bmp;
	}
	
	public TextureRegion getTextureRegion(
			String title )
	{
		Bitmap bitmap = drawTitleBitmap( title );
		TextureRegion t = new TextureRegion( new BitmapTexture( bitmap ) );
		if( !bitmap.isRecycled() )
		{
			bitmap.recycle();
			bitmap = null;
		}
		return t;
	}
	public TextureRegion getTextureRegion( )
	{
		Bitmap bitmap = drawTitleBitmap( title );
		TextureRegion t = new TextureRegion( new BitmapTexture( bitmap ) );
		if( !bitmap.isRecycled() )
		{
			bitmap.recycle();
			bitmap = null;
		}
		return t;
	}
	
	
	public int measureSize(float demin){
		
		return (int)(mScale*demin);
	}
	
	public Bitmap drawTitleWithBg(String title,Bitmap bg){
		
		Bitmap bmp = Bitmap.createBitmap( bg.getWidth() ,bg.getHeight() , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		canvas.drawBitmap( bg , 0 , 0,null );
		
		setTitle(title);
		
		
		float paddingText = (bg.getWidth()-textWidth)/2;
		float titleY = (bg.getHeight()-singleLineHeight)/2;
		
		if(paddingText<0) paddingText=0;
		if(titleY<0) titleY=0;
		canvas.drawText( title , paddingText , mFontSize+titleY , paint );
		return bmp;
	}
	
	public TextureRegion getTextureRegion(String title,Bitmap bg )
	{
		Bitmap bitmap = drawTitleWithBg( title , bg );
		
		TextureRegion t = new TextureRegion( new BitmapTexture( bitmap ) );
		if( !bitmap.isRecycled() )
		{
			bitmap.recycle();
			bitmap = null;
		}
		return t;
	}
}
