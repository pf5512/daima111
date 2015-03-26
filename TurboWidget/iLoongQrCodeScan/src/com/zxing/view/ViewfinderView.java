/* Copyright (C) 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package com.zxing.view;


import java.util.Collection;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.iLoong.scan.R;
import com.zxing.activity.CaptureActivity;
import com.zxing.camera.CameraManager;


/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 * �Զ����View������ʱ�м���ʾ��
 */
public final class ViewfinderView extends View
{
	
	private static final int[] SCANNER_ALPHA = { 0 , 64 , 128 , 192 , 255 , 192 , 128 , 64 };
	private static final long ANIMATION_DELAY = 100L;
	private static final int OPAQUE = 0xFF;
	private final Paint paint;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int laserColor;
	private final int resultPointColor;
	private int scannerAlpha;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	private Bitmap left_top;
	private Bitmap right_top;
	private Bitmap left_bottom;
	private Bitmap right_bottom;
	private Bitmap scan_line;
	private Rect srcL;
	private RectF dstL;
	private int speed_y = 10;
	private float scale;
	
	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		// Initialize these once for performance rather than calling them every time in onDraw().
		paint = new Paint();
		scale = CaptureActivity.screenWidth / 720f;
		speed_y = (int)( 10 * scale );
		Resources resources = getResources();
		maskColor = resources.getColor( R.color.viewfinder_mask );
		resultColor = resources.getColor( R.color.result_view );
		frameColor = resources.getColor( R.color.viewfinder_frame );
		laserColor = resources.getColor( R.color.viewfinder_laser );
		resultPointColor = resources.getColor( R.color.possible_result_points );
		scannerAlpha = 0;
		possibleResultPoints = new HashSet<ResultPoint>( 5 );
		left_top = BitmapFactory.decodeResource( resources , R.drawable.left_top );
		right_top = BitmapFactory.decodeResource( resources , R.drawable.right_top );
		left_bottom = BitmapFactory.decodeResource( resources , R.drawable.left_bottom );
		right_bottom = BitmapFactory.decodeResource( resources , R.drawable.right_bottom );
		scan_line = BitmapFactory.decodeResource( resources , R.drawable.scan_line );
		srcL = new Rect( 0 , 0 , 370 , 75 );
		dstL = new RectF( ( 176 + 2 ) * scale , ( 398 ) * scale , ( 370 + 176 - 5 ) * scale , ( 398 + 75 ) * scale );
	}
	
	@Override
	public void onDraw(
			Canvas canvas )
	{
		//		Log.i( "Scan" , "onDraw" );
		Rect frame = CameraManager.get().getFramingRect();
		if( frame == null )
		{
			return;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		float scale = width / 720f;
		dstL.top += speed_y;
		dstL.bottom += speed_y;
		if( dstL.bottom >= frame.bottom )
		{
			dstL.top = 398 * scale;
			dstL.bottom = ( 398 + 75 ) * scale;
		}
		//		line_y1 = frame.top + speed_y;
		//		if( line_y1 + scan_line.getHeight() >= frame.bottom )
		//		{
		//			speed_y = 0;
		//			line_y1 = frame.top + speed_y;
		//		}
		//		speed_y += 5;
		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor( laserColor );
		//		canvas.drawBitmap( scan_line , frame.left , line_y1 , paint );
		canvas.drawBitmap( scan_line , srcL , dstL , paint );
		paint.setColor( resultBitmap != null ? resultColor : maskColor );
		canvas.drawRect( 0 , 0 , width , frame.top , paint );
		canvas.drawRect( 0 , frame.top , frame.left , frame.bottom + 1 , paint );
		canvas.drawRect( frame.right + 1 , frame.top , width , frame.bottom + 1 , paint );
		canvas.drawRect( 0 , frame.bottom + 1 , width , height , paint );
		if( resultBitmap != null )
		{
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha( OPAQUE );
			canvas.drawBitmap( resultBitmap , frame.left , frame.top , paint );
		}
		else
		{
			// Draw a two pixel solid black border inside the framing rect
			paint.setColor( frameColor );
			canvas.drawBitmap( left_top , 176 * scale , 398 * scale , paint );
			canvas.drawBitmap( right_top , 515 * scale , 398 * scale , paint );
			canvas.drawBitmap( left_bottom , 176 * scale , 734 * scale , paint );
			canvas.drawBitmap( right_bottom , 515 * scale , 734 * scale , paint );
			//			canvas.drawRect( frame.left , frame.top , frame.right + 1 , frame.top + 2 , paint );
			//			canvas.drawRect( frame.left , frame.top + 2 , frame.left + 2 , frame.bottom - 1 , paint );
			//			canvas.drawRect( frame.right - 1 , frame.top , frame.right + 1 , frame.bottom - 1 , paint );
			//			canvas.drawRect( frame.left , frame.bottom - 1 , frame.right + 1 , frame.bottom + 1 , paint );
			// Draw a red "laser scanner" line through the middle to show decoding is active
			paint.setColor( laserColor );
			paint.setAlpha( SCANNER_ALPHA[scannerAlpha] );
			scannerAlpha = ( scannerAlpha + 1 ) % SCANNER_ALPHA.length;
			int middle = frame.height() / 2 + frame.top;
			//canvas.drawRect( frame.left + 2 , middle - 1 , frame.right - 1 , middle + 2 , paint );
			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if( currentPossible.isEmpty() )
			{
				lastPossibleResultPoints = null;
			}
			else
			{
				possibleResultPoints = new HashSet<ResultPoint>( 5 );
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha( OPAQUE );
				paint.setColor( resultPointColor );
				for( ResultPoint point : currentPossible )
				{
					canvas.drawCircle( frame.left + point.getX() , frame.top + point.getY() , 6.0f , paint );
				}
			}
			if( currentLast != null )
			{
				paint.setAlpha( OPAQUE / 2 );
				paint.setColor( resultPointColor );
				for( ResultPoint point : currentLast )
				{
					canvas.drawCircle( frame.left + point.getX() , frame.top + point.getY() , 3.0f , paint );
				}
			}
			// Request another update at the animation interval, but only repaint the laser line,
			// not the entire viewfinder mask.
			postInvalidateDelayed( ANIMATION_DELAY , frame.left , frame.top , frame.right , frame.bottom );
		}
	}
	
	public void drawViewfinder()
	{
		resultBitmap = null;
		invalidate();
	}
	
	/**
	 * Draw a bitmap with the result points highlighted instead of the live scanning display.
	 *
	 * @param barcode An image of the decoded barcode.
	 */
	public void drawResultBitmap(
			Bitmap barcode )
	{
		resultBitmap = barcode;
		invalidate();
	}
	
	public void addPossibleResultPoint(
			ResultPoint point )
	{
		possibleResultPoints.add( point );
	}
}
