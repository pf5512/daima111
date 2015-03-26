/* Copyright (C) 2011 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.iLoong.launcher.cling;


import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import com.iLoong.launcher.Desktop3D.Log;
import android.widget.FrameLayout;


public class Cling extends FrameLayout
{
	
	private iLoongLauncher mLauncher;
	public String mDrawIdentifier;
	//private Drawable mBackground;
	private int[] mPositionData;
	private GradientDrawable grad;
	private Bitmap clingAllapp_1;
	private Bitmap clingAllapp_2;
	private Bitmap clingPageindicator_1;
	private Bitmap clingPageindicator_2;
	private Bitmap clingFolder_1;
	private Bitmap clingSelect_1;
	private Bitmap clingSelect_2;
	private BitmapFactory.Options options;
	private Rect src1;
	private Rect dst1;
	private Rect src2;
	private Rect dst2;
	//    private Canvas canvas;
	private Paint mErasePaint;
	private Paint normalPaint;
	private int strokeWidth = 5;
	private int strokeColor = 0xFF18b7de;
	
	public Cling(
			Context context )
	{
		this( context , null , 0 );
		//        bmp = Bitmap.createBitmap(Utils3D.getScreenWidth(), Utils3D.getScreenHeight(),
		//                Bitmap.Config.ARGB_8888);
		//        canvas = new Canvas(bmp);
		grad = new GradientDrawable( //渐变色  
				Orientation.TOP_BOTTOM ,
				new int[]{ Color.BLACK , Color.WHITE } );
		options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		src1 = new Rect();
		dst1 = new Rect();
		src2 = new Rect();
		dst2 = new Rect();
		mErasePaint = new Paint();
		mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
		//        mErasePaint.setColor(0xFF18b7de);
		mErasePaint.setAlpha( 0 );
		mErasePaint.setAntiAlias( true );
		mErasePaint.setStyle( Paint.Style.FILL );
		mErasePaint.setStrokeWidth( 4 );
		normalPaint = new Paint();
		normalPaint.setAntiAlias( true );
		normalPaint.setStyle( Paint.Style.STROKE );
		normalPaint.setStrokeWidth( strokeWidth );
		normalPaint.setColor( strokeColor );
	}
	
	public Cling(
			Context context ,
			AttributeSet attrs )
	{
		this( context , attrs , 0 );
	}
	
	public Cling(
			Context context ,
			AttributeSet attrs ,
			int defStyle )
	{
		super( context , attrs , defStyle );
	}
	
	void init(
			iLoongLauncher l ,
			String identifier )
	{
		mLauncher = l;
		mDrawIdentifier = identifier;
	}
	
	public void setPosition(
			int[] position )
	{
		mPositionData = position;
	}
	
	void cleanup()
	{
		if( clingAllapp_1 != null )
		{
			clingAllapp_1.recycle();
			clingAllapp_1 = null;
		}
		if( clingAllapp_2 != null )
		{
			clingAllapp_2.recycle();
			clingAllapp_2 = null;
		}
		if( clingPageindicator_1 != null )
		{
			clingPageindicator_1.recycle();
			clingPageindicator_1 = null;
		}
		if( clingPageindicator_2 != null )
		{
			clingPageindicator_2.recycle();
			clingPageindicator_2 = null;
		}
		if( clingFolder_1 != null )
		{
			clingFolder_1.recycle();
			clingFolder_1 = null;
		}
		if( clingSelect_1 != null )
		{
			clingSelect_1.recycle();
			clingSelect_1 = null;
		}
		if( clingSelect_2 != null )
		{
			clingSelect_2.recycle();
			clingSelect_2 = null;
		}
	}
	
	@Override
	public boolean onTouchEvent(
			android.view.MotionEvent event )
	{
		return true;
	};
	
	@Override
	protected void dispatchDraw(
			Canvas canvas )
	{
		//grad.draw(canvas);
		//Log.d("launcher", "cling draw");
		canvas.drawColor( 0xCC000000 );
		canvas.drawCircle( mPositionData[0] , mPositionData[1] , mPositionData[2] , mErasePaint );
		canvas.drawCircle( mPositionData[0] , mPositionData[1] , mPositionData[2] , normalPaint );
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		float scale = ( (float)width ) / 480;
		scale = 1;
		// Draw the background
		//            if (mBackground == null) {
		if( mDrawIdentifier.equals( ClingManager.ALLAPP ) )
		{
			if( clingAllapp_1 == null )
				clingAllapp_1 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_allapp_1 , options );
			if( clingAllapp_2 == null )
				clingAllapp_2 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_allapp_2 , options );
			int bgWidth1 = (int)( clingAllapp_1.getWidth() * scale );
			int bgHeight1 = (int)( clingAllapp_1.getHeight() * scale );
			src1.set( 0 , 0 , clingAllapp_1.getWidth() , clingAllapp_1.getHeight() );
			dst1.set(
					(int)( mPositionData[0] - mPositionData[2] - 132 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[1] - mPositionData[2] - 99 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[0] - mPositionData[2] - 132 * SetupMenu.mScreenScale + bgWidth1 ) ,
					(int)( mPositionData[1] - mPositionData[2] - 99 * SetupMenu.mScreenScale + bgHeight1 ) );
			canvas.drawBitmap( clingAllapp_1 , src1 , dst1 , null );
			int bgWidth2 = (int)( clingAllapp_2.getWidth() * scale );
			int bgHeight2 = (int)( clingAllapp_2.getHeight() * scale );
			src2.set( 0 , 0 , clingAllapp_2.getWidth() , clingAllapp_2.getHeight() );
			dst2.set(
					(int)( mPositionData[0] - mPositionData[2] + 46 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[1] - mPositionData[2] - 72 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[0] - mPositionData[2] + 46 * SetupMenu.mScreenScale + bgWidth2 ) ,
					(int)( mPositionData[1] - mPositionData[2] - 72 * SetupMenu.mScreenScale + bgHeight2 ) );
			canvas.drawBitmap( clingAllapp_2 , src2 , dst2 , null );
		}
		else if( mDrawIdentifier.equals( ClingManager.PAGEINDICATOR ) )
		{
			if( clingPageindicator_1 == null )
				clingPageindicator_1 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_pageindicator_1 , options );
			if( clingPageindicator_2 == null )
				clingPageindicator_2 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_pageindicator_2 , options );
			int bgWidth1 = (int)( clingPageindicator_1.getWidth() * scale );
			int bgHeight1 = (int)( clingPageindicator_1.getHeight() * scale );
			src1.set( 0 , 0 , clingPageindicator_1.getWidth() , clingPageindicator_1.getHeight() );
			dst1.set(
					(int)( mPositionData[0] - mPositionData[2] - 99 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[1] - mPositionData[2] - 64 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[0] - mPositionData[2] - 99 * SetupMenu.mScreenScale + bgWidth1 ) ,
					(int)( mPositionData[1] - mPositionData[2] - 64 * SetupMenu.mScreenScale + bgHeight1 ) );
			canvas.drawBitmap( clingPageindicator_1 , src1 , dst1 , null );
			int bgWidth2 = (int)( clingPageindicator_2.getWidth() * scale );
			int bgHeight2 = (int)( clingPageindicator_2.getHeight() * scale );
			src2.set( 0 , 0 , clingPageindicator_2.getWidth() , clingPageindicator_2.getHeight() );
			dst2.set(
					(int)( mPositionData[0] - mPositionData[2] + 49 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[1] - mPositionData[2] - 64 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[0] - mPositionData[2] + 49 * SetupMenu.mScreenScale + bgWidth2 ) ,
					(int)( mPositionData[1] - mPositionData[2] - 64 * SetupMenu.mScreenScale + bgHeight2 ) );
			canvas.drawBitmap( clingPageindicator_2 , src2 , dst2 , null );
		}
		else if( mDrawIdentifier.equals( ClingManager.SELECT ) )
		{
			canvas.drawCircle( mPositionData[0] + mPositionData[3] , mPositionData[1] , mPositionData[2] , mErasePaint );
			canvas.drawCircle( mPositionData[0] + mPositionData[3] , mPositionData[1] , mPositionData[2] , normalPaint );
			canvas.drawCircle( mPositionData[0] + 2 * mPositionData[3] , mPositionData[1] , mPositionData[2] , mErasePaint );
			canvas.drawCircle( mPositionData[0] + 2 * mPositionData[3] , mPositionData[1] , mPositionData[2] , normalPaint );
			if( clingSelect_1 == null )
				clingSelect_1 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_select_1 , options );
			if( clingSelect_2 == null )
				clingSelect_2 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_select_2 , options );
			int bgWidth1 = (int)( clingSelect_1.getWidth() * scale );
			int bgHeight1 = (int)( clingSelect_1.getHeight() * scale );
			src1.set( 0 , 0 , clingSelect_1.getWidth() , clingSelect_1.getHeight() );
			dst1.set(
					(int)( mPositionData[0] + mPositionData[2] - clingSelect_1.getWidth() ) ,
					(int)( mPositionData[1] - mPositionData[2] ) ,
					(int)( mPositionData[0] + mPositionData[2] - clingSelect_1.getWidth() + bgWidth1 ) ,
					(int)( mPositionData[1] - mPositionData[2] + bgHeight1 ) );
			canvas.drawBitmap( clingSelect_1 , src1 , dst1 , null );
			dst1.set(
					(int)( mPositionData[0] + mPositionData[2] + mPositionData[3] - clingSelect_1.getWidth() ) ,
					(int)( mPositionData[1] - mPositionData[2] ) ,
					(int)( mPositionData[0] + mPositionData[2] + mPositionData[3] - clingSelect_1.getWidth() + bgWidth1 ) ,
					(int)( mPositionData[1] - mPositionData[2] + bgHeight1 ) );
			canvas.drawBitmap( clingSelect_1 , src1 , dst1 , null );
			int bgWidth2 = (int)( clingSelect_2.getWidth() * scale );
			int bgHeight2 = (int)( clingSelect_2.getHeight() * scale );
			src2.set( 0 , 0 , clingSelect_2.getWidth() , clingSelect_2.getHeight() );
			dst2.set(
					(int)( mPositionData[0] - mPositionData[2] + 2 * mPositionData[3] - 140 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[1] - mPositionData[2] + 29 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[0] - mPositionData[2] + 2 * mPositionData[3] - 140 * SetupMenu.mScreenScale + bgWidth2 ) ,
					(int)( mPositionData[1] - mPositionData[2] + 29 * SetupMenu.mScreenScale + bgHeight2 ) );
			canvas.drawBitmap( clingSelect_2 , src2 , dst2 , null );
			//               
		}
		else if( mDrawIdentifier.equals( ClingManager.FOLDER ) )
		{
			if( clingFolder_1 == null )
				clingFolder_1 = BitmapFactory.decodeResource( getResources() , RR.drawable.cling_folder_1 , options );
			int bgWidth1 = (int)( clingFolder_1.getWidth() * scale );
			int bgHeight1 = (int)( clingFolder_1.getHeight() * scale );
			src1.set( 0 , 0 , clingFolder_1.getWidth() , clingFolder_1.getHeight() );
			dst1.set(
					(int)( mPositionData[0] - mPositionData[2] + 21 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[1] - mPositionData[2] - 105 * SetupMenu.mScreenScale ) ,
					(int)( mPositionData[0] - mPositionData[2] + 21 * SetupMenu.mScreenScale + bgWidth1 ) ,
					(int)( mPositionData[1] - mPositionData[2] - 105 * SetupMenu.mScreenScale + bgHeight1 ) );
			canvas.drawBitmap( clingFolder_1 , src1 , dst1 , null );
			//                } else if (mDrawIdentifier.equals(ClingManager.CIRCLE)) {
			//                	
			//                	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cling_circle, options);
			//                    int bgWidth = (int) (bmp.getWidth()*scale);
			//                    int bgHeight = (int) (bmp.getHeight()*scale);
			//                    src.set(0,0,bmp.getWidth(),bmp.getHeight());
			//                    dst.set((width-bgWidth)/2, (height-bgHeight)/2, bgWidth+(width-bgWidth)/2, bgHeight+(height-bgHeight)/2);
			//                    canvas.drawBitmap(bmp, src, dst, null);
			//                    
			//                } else if (mDrawIdentifier.equals(ClingManager.PAGEEDIT)) {
			//                	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cling_pageedit, options);
			//                    int bgWidth = (int) (bmp.getWidth()*scale);
			//                    int bgHeight = (int) (bmp.getHeight()*scale);
			//                    src.set(0,0,bmp.getWidth(),bmp.getHeight());
			//                    dst.set((width-bgWidth)/2, 30, bgWidth+(width-bgWidth)/2, bgHeight+20);
			//                    canvas.drawBitmap(bmp, src, dst, null);
			//                    
			//                } else if (mDrawIdentifier.equals(ClingManager.PAGESELECT)) {
			//                	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cling_pageselect, options);
			//                    int bgWidth = (int) (bmp.getWidth()*scale);
			//                    int bgHeight = (int) (bmp.getHeight()*scale);
			//                    src.set(0,0,bmp.getWidth(),bmp.getHeight());
			//                    dst.set((width-bgWidth)/2, (height-bgHeight)/2, bgWidth+(width-bgWidth)/2, bgHeight+(height-bgHeight)/2);
			//                    canvas.drawBitmap(bmp, src, dst, null);
			//                    
			//                } else if (mDrawIdentifier.equals(ClingManager.SETTING)) {
			//                	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cling_setting, options);
			//                    int bgWidth = (int) (bmp.getWidth()*scale);
			//                    int bgHeight = (int) (bmp.getHeight()*scale);
			//                    src.set(0,0,bmp.getWidth(),bmp.getHeight());
			//                    dst.set((width-bgWidth)/2, height-bgHeight, bgWidth+(width-bgWidth)/2, height);
			//                    canvas.drawBitmap(bmp, src, dst, null);
			//                    
			//                } else if (mDrawIdentifier.equals(ClingManager.WIDGET)) {
			//                	bmp = BitmapFactory.decodeResource(getResources(), R.drawable.cling_widget, options);
			//                    int bgWidth = (int) (bmp.getWidth()*scale);
			//                    int bgHeight = (int) (bmp.getHeight()*scale);
			//                    src.set(0,0,bmp.getWidth(),bmp.getHeight());
			//                    int y = (int) (height-bgHeight-50*SetupMenu.mScreenScale);
			//                    if(y < 0)y = 0;
			//                    dst.set((width-bgWidth)/2, y, bgWidth+(width-bgWidth)/2, bgHeight+y);
			//                    canvas.drawBitmap(bmp, src, dst, null);
			//                    
		}
		//            }
		//            if (mBackground != null) {
		//                mBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
		//                mBackground.draw(canvas);
		//                
		//            } else {
		//                canvas.drawColor(0x99000000);
		//            }
		//            canvas.drawBitmap(b, 0, 0, null);
		//            c.setBitmap(null);
		//            b = null;
		// Draw the rest of the cling
		super.dispatchDraw( canvas );
	};
}
