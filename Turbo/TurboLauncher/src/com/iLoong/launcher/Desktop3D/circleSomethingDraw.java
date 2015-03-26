package com.iLoong.launcher.Desktop3D;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.theme.ThemeManager;


public class circleSomethingDraw extends View
{
	
	private Paint mPaint;
	private Path mPath;
	//private Path mPathLine;
	private PathEffect[] mEffects;
	private float mPhase;
	private float mx , my;
	private static final float TOUCH_TOLERANCE = 4;
	private float touchDownX;
	private float touchDownY;
	private float moveLastX;
	private float moveLastY;
	private Bitmap mBitFocus = null;
	private Bitmap mBitNotice = null;
	private Bitmap mBitConnectPoint = null;
	private Bitmap mBitTmp;
	private int mBitFocusW;
	private int mBitFocusH;
	//	private int mBitNoticeW;
	//	private int mBitNoticeH;
	private int mBitConnectPointW;
	private int mBitConnectPointH;
	private boolean bNeedDraw = false;
	private Matrix mMatrix = new Matrix();
	private float rotDegrees = 0;
	private Context mContext;
	private String NoticeToast;
	//private  String DstOverToast;
	private static String CIRCLE_SHELL_FOCUS = "theme/path/shell_picker_focus.png";
	private static String CIRCLE_SHELL_NOTICE = "theme/path/shell_picker_notic_bg.png";
	private static String CIRCLE_SHELL_CONNECT = "theme/path/shell_picker_connect_point.png";
	
	public circleSomethingDraw(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		NoticeToast = getResources().getString( RR.string.circle_noticeToast );
		//DstOverToast=getResources().getString(R.string.circle_dstOverToast);
		mPaint = new Paint();
		mContext = context;
		mPaint.setAntiAlias( true );
		mPaint.setDither( true );
		mPaint.setColor( Color.WHITE/*0xFFFF0000*/);
		mPaint.setStyle( Paint.Style.STROKE );
		mPaint.setStrokeJoin( Paint.Join.ROUND );
		mPaint.setStrokeCap( Paint.Cap.ROUND );
		mPaint.setStrokeWidth( (float)3.5 );
		mPath = new Path();
		CIRCLE_SHELL_FOCUS = CIRCLE_SHELL_FOCUS;
		CIRCLE_SHELL_NOTICE = CIRCLE_SHELL_NOTICE;
		CIRCLE_SHELL_CONNECT = CIRCLE_SHELL_CONNECT;
		mEffects = new PathEffect[6];
		makeEffects( mEffects , mPhase );
		mBitFocus = ThemeManager.getInstance().getBitmap( CIRCLE_SHELL_FOCUS );
		mBitFocus = Tools.resizeBitmap( mBitFocus , SetupMenu.mScale );
		//	mBitTmp = ThemeManager.getInstance().getBitmap(CIRCLE_SHELL_NOTICE);
		//	mBitTmp = Tools.resizeBitmap(mBitTmp, SetupMenu.mScale);
		mBitConnectPoint = ThemeManager.getInstance().getBitmap( CIRCLE_SHELL_CONNECT );
		mBitConnectPoint = Tools.resizeBitmap( mBitConnectPoint , SetupMenu.mScale );
		mBitFocusW = mBitFocus.getWidth();
		mBitFocusH = mBitFocus.getHeight();
		mBitConnectPointW = mBitConnectPoint.getWidth();
		mBitConnectPointH = mBitConnectPoint.getHeight();
	}
	
	public circleSomethingDraw(
			Context context )
	{
		super( context );
		mContext = context;
	}
	
	private void makeEffects(
			PathEffect[] e ,
			float phase )
	{
		e[0] = null; // no effect
		e[1] = new CornerPathEffect( 50 );
		e[2] = new DashPathEffect( new float[]{ 12 , 12 , 4 , 12 } , phase );
		e[3] = new PathDashPathEffect( makePathDash() , 12 , phase , PathDashPathEffect.Style.ROTATE );
		e[4] = new ComposePathEffect( e[2] , e[1] );
		e[5] = new ComposePathEffect( e[3] , e[1] );
	}
	
	private Path makePathDash()
	{
		Path p = new Path();
		p.moveTo( 4 , 0 );
		p.lineTo( 0 , -4 );
		p.lineTo( 8 , -4 );
		p.lineTo( 12 , 0 );
		p.lineTo( 8 , 4 );
		p.lineTo( 0 , 4 );
		return p;
	}
	
	private Bitmap getTextOnBmp(
			Bitmap oldBmp )
	{
		Paint textPaint = new Paint();
		if( oldBmp == null )
		{
			return null;
		}
		int w = oldBmp.getWidth();
		int h = oldBmp.getHeight();
		// 创建一个新的和SRC长度宽度一样的位图
		Bitmap newb = Bitmap.createBitmap( w , h , Config.ARGB_8888 );
		Canvas cv = new Canvas( newb );
		// draw src into
		cv.drawBitmap( oldBmp , 0 , 0 , null );
		textPaint.setColor( 0xFFFFFFFF );
		textPaint.setTextSize( R3D.icon_title_font );
		textPaint.setTypeface( Typeface.DEFAULT );
		textPaint.setAntiAlias( true );
		cv.drawText( NoticeToast , R3D.circle_drawtext_x , R3D.circle_drawtext_y , textPaint );
		// save all clip
		// cv.save( Canvas.ALL_SAVE_FLAG );//保存
		// store
		// cv.restore();//存储
		return newb;
	}
	
	private void drawImage(
			Canvas canvas )
	{
		canvas.save();
		int mBitFocusX = (int)( touchDownX - mBitFocusW / 2 );
		int mBitFocusY = (int)( touchDownY - mBitFocusH / 2 );
		int mBitConnectPointX = (int)( moveLastX - mBitConnectPointW / 2 );
		int mBitConnectPointY = (int)( moveLastY - mBitConnectPointH / 2 );
		rotDegrees = Utils3D.getRotDegrees( moveLastX , moveLastY , touchDownX , touchDownY );
		canvas.drawBitmap( mBitConnectPoint , touchDownX - mBitConnectPointW / 2 , touchDownY - mBitConnectPointH / 2 , null );
		canvas.drawBitmap( mBitFocus , mBitFocusX , mBitFocusY , null );
		canvas.drawBitmap( mBitConnectPoint , mBitConnectPointX , mBitConnectPointY , null );
		//		mMatrix.reset();
		//		float sx = (float) (touchDownX + mBitFocusW
		//				* Math.sin((rotDegrees) / 180 * Math.PI));
		//		float sy = (float) (touchDownY - mBitFocusW
		//				* Math.cos((rotDegrees) / 180 * Math.PI));
		//		canvas.translate(sx, sy);
		//		mMatrix.setTranslate(mBitFocusW / 2, 2 * mBitConnectPointH);
		//		mMatrix.postRotate(rotDegrees);
		//		canvas.drawBitmap(mBitNotice, mMatrix, null);
		//		canvas.translate(-sx, -sy);
		mPaint.setPathEffect( null );
		mPaint.setStrokeWidth( 2 );
		//mPathLine.reset();
		float sx = (float)( touchDownX + mBitConnectPointW / 2 * Math.cos( rotDegrees / 180 * Math.PI ) );
		float sy = (float)( touchDownY + mBitConnectPointW / 2 * Math.sin( rotDegrees / 180 * Math.PI ) );
		//mPathLine.moveTo(sx, sy);
		//mPathLine.lineTo(moveLastX, moveLastX);
		//canvas.drawPath(mPathLine, mPaint);
		canvas.drawLine( sx , sy , moveLastX , moveLastY , mPaint );
		makeEffects( mEffects , mPhase );
		mPhase += 1;
		invalidate();
		mPaint.setPathEffect( mEffects[4] );
		mPaint.setStrokeWidth( 4 );
		canvas.drawPath( mPath , mPaint );
		canvas.restore();
	}
	
	/** 

	 * 动态构建旋转矩阵Matrix对象 

	 * @param matrix  需要计算的矩阵 

	 * @param canvas  画布 

	 * @param degrees 图片旋转的角度，正值为顺时针，负值为逆时?

	 * @param pivotX  轴心的X坐标 

	 * @param pivotY  轴心的Y坐标 

	 */
	//	public boolean onTouchEvent(final MotionEvent event) {
	//
	//
	//
	//		queueEvent(new Runnable(){
	//
	//
	//
	//		public void run() {
	//
	//
	//
	//		mRenderer.setColor(event.getX() / getWidth(),
	//
	//
	//
	//		event.getY() / getHeight(), 1.0f);
	//
	//
	//
	//		}
	//    private Matrix getMyMatrix(Matrix matrix ,float degrees,float pivotX , float pivotY ){  
	//
	//        //重置Matrix  
	//    	
	//        matrix.reset();  
	//
	//        float cosValue = (float) Math.cos(Math.PI/(180/degrees));    
	//
	//        float sinValue = (float) Math.sin(Math.PI/(180/degrees));    
	//
	//        //设置旋转矩阵? 
	//
	//        matrix.setValues(    
	//
	//                new float[]{    
	//
	//                        cosValue, -sinValue, pivotX,    
	//
	//                        sinValue, cosValue, pivotY,    
	//
	//                        0, 0, 1});   
	//
	//        return matrix;  
	//
	//    }  
	public void onDraw(
			Canvas canvas )
	{
		if( bNeedDraw == false )
		{
			return;
		}
		drawImage( canvas );
	}
	
	private void touch_start(
			float x ,
			float y )
	{
		mPath.reset();
		mPath.moveTo( x , y );
		//	mBitNotice=getTextOnBmp(mBitTmp);
		mx = x;
		my = y;
		if( mx == 0 || my == 0 )
		{
			bNeedDraw = false;
		}
	}
	
	//	private float getRotDegrees()
	//	{
	//		float retDeg;
	//		float dx=moveLastX-touchDownX;
	//		float dy=moveLastY-touchDownY;
	//		if (moveLastX==touchDownX)
	//		{
	//			if (moveLastY-touchDownY>0)
	//			{
	//				retDeg=270;
	//				return retDeg;
	//			}
	//			else
	//			{
	//				retDeg=90;
	//				return retDeg;
	//			}
	//		}
	//		retDeg = (float) Math.atan((dy/dx));
	//		retDeg=(float) (retDeg*180/Math.PI);
	//		if (dx>0 && dy>0)
	//		{
	//			//retDeg= retDeg;
	//		}
	//		else if (dx<0 && dy>0)
	//		{
	//			retDeg= 180+retDeg;
	//		}
	//		else if (dx<0 && dy<0)
	//		{
	//			retDeg= retDeg+180;
	//		}
	//		else if (dx>0 && dy<0)
	//		{
	//			retDeg= 360+retDeg;
	//		}
	//		return retDeg;
	//		
	//	}
	private void touch_move(
			float x ,
			float y )
	{
		float dx = Math.abs( x - mx );
		float dy = Math.abs( y - my );
		moveLastX = x;
		moveLastY = y;
		if( dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE )
		{
			mPath.quadTo( mx , my , ( x + mx ) / 2 , ( y + my ) / 2 );
			mx = x;
			my = y;
		}
	}
	
	//	private void testhandleMessage()
	//	{
	//		Handler myHandler=iLoongLauncher.getInstance().glSurfacehandler;
	//		Message msg = new Message();  
	//		msg.what = 1;  
	//        msg.setTarget(myHandler);  
	//        msg.sendToTarget();  
	//
	//		
	//	}
	private void touch_up()
	{
		mPath.reset();
		//	mPathLine.reset();
	}
	
	public void DisplayToast(
			String str )
	{
		Toast.makeText( mContext , str , Toast.LENGTH_SHORT ).show();
	}
	
	public void libgdxAdaptAndroidViewEvent(
			int event ,
			float x ,
			float y ,
			String toastString )
	{
		switch( event )
		{
		/*touch down*/
			case Messenger.CIRCLE_EVENT_DOWN:
				touch_start( x , y );
				touchDownX = x;
				touchDownY = y;
				bNeedDraw = false;
				//setVisibility(View.VISIBLE);
				//invalidate();
				break;
			/*touch move*/
			case Messenger.CIRCLE_EVENT_UP:
				touch_up();
				bNeedDraw = false;
				invalidate();
				//setVisibility(View.GONE);
				break;
			/*touch up*/
			case Messenger.CIRCLE_EVENT_DRAG:
				touch_move( x , y );
				//moveLastX=x;
				//moveLastY=y;
				bNeedDraw = true;
				invalidate();
				break;
			case Messenger.EVENT_TOAST_USER:
				DisplayToast( toastString );
				bNeedDraw = false;
				break;
			case Messenger.CIRCLE_EVENT_TOAST:
				touch_up();
				DisplayToast( toastString );
				bNeedDraw = false;
				break;
		}
		//onTouchEvent(event);
	}
}
