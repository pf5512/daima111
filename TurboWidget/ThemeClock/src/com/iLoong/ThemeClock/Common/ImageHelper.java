package com.iLoong.ThemeClock.Common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.graphics.Pixmap;
import com.iLoong.ThemeClock.R;

public class ImageHelper {
	/**
	 * ͨ�����RowResource��ʽ��ȡͼƬ�ļ����˷�����ȡ���ļ�Ϊ������Ļ��С��Ӧ���ͼƬ
	 * 
	 * @param res
	 * @param resourceId
	 * @return
	 */
	public static Bitmap getImageFromResource(Context context, int resourceId) {
		return BitmapFactory.decodeResource(context.getResources(), resourceId);
	}

	public static Bitmap getImageFromResource(Context context, int resourceId,
			BitmapFactory.Options options) {
		return BitmapFactory.decodeResource(context.getResources(), resourceId,
				options);
	}

	/**
	 * ͨ�����RowResource��ʽ��ȡͼƬ�ļ����˷�����ȡ���ļ�Ϊԭʼ�ļ���С�����������Ļ��С��Ӧ
	 * 
	 * @param paramContext
	 *            ����������
	 * @param resourceId
	 *            ͼƬ��ԴID
	 * @return
	 */
	public static Bitmap getImageFromRawResource(Context paramContext,
			int resourceId) {
		InputStream stream = paramContext.getResources().openRawResource(
				resourceId);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(stream);
		} finally {
			try {
				stream.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int target) {
		int w = options.outWidth;
		int h = options.outHeight;
		int candidateW = w / target;
		int candidateH = h / target;
		int candidate = Math.max(candidateW, candidateH);
		if (candidate == 0)
			return 1;
		if (candidate > 1) {
			if ((w > target) && (w / candidate) < target)
				candidate -= 1;
		}
		if (candidate > 1) {
			if ((h > target) && (h / candidate) < target)
				candidate -= 1;
		}
		return candidate;
	}

	public static Bitmap getImageFromRawResource(Context paramContext,
			int resourceId, float toWidth, float toHeight) {

		BitmapFactory.Options opt = new Options();
		opt.inJustDecodeBounds = true;
		Bitmap virtualBitmap = BitmapFactory.decodeResource(
				paramContext.getResources(), resourceId, opt);
		virtualBitmap.recycle();
		opt.inSampleSize = computeSampleSize(opt, (int) toWidth);
		opt.inJustDecodeBounds = false;
		try {
			virtualBitmap = BitmapFactory.decodeResource(
					paramContext.getResources(), resourceId, opt);
		} catch (OutOfMemoryError err) {
		}
		return virtualBitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static Bitmap Bytes2Bimap(byte[] b, Options options) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length, options);
		} else {
			return null;
		}
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, float width, float height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		// ȡ drawable �ĳ���
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// ȡ drawable ����ɫ��ʽ
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// ������Ӧ bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// ������Ӧ bitmap �Ļ���
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// �� drawable ���ݻ���������
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * ���Բ��ͼƬ
	 * */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		// ����һ����ԭʼͼƬһ���Сλͼ
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);

		// ��������λͼroundConcerImage�Ļ���
		Canvas canvas = new Canvas(output);

		// ��������
		final Paint paint = new Paint();
		// ȥ���
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);

		// ����һ����ԭʼͼƬһ���С�ľ���
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);

		// ��һ����ԭʼͼƬһ���С��Բ�Ǿ���
		// canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		// �����ཻģʽ
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		// ��ͼƬ��������ȥ
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Pixmap bmp2Pixmap(Bitmap bmp) {
		byte[] b = Bitmap2Bytes(bmp);
		return new Pixmap(b, 0, b.length);
	}

	// ��ô�Ӱ��ͼƬ
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	public static Drawable bitmapToDrawable(Resources resource, Bitmap bitmap) {
		return new BitmapDrawable(resource, bitmap);
	}

	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		// drawableת����bitmap
		Bitmap oldbmp = drawableToBitmap(drawable);
		// ��������ͼƬ�õ�Matrix����
		Matrix matrix = new Matrix();
		// �������ű���
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		// �������ű���
		matrix.postScale(sx, sy);
		// �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}

	/**
	 * Returns a Bitmap representing the thumbnail of the specified Bitmap. The
	 * size of the thumbnail is defined by the dimension
	 * android.R.dimen.launcher_application_icon_size.
	 * 
	 * This method is not thread-safe and should be invoked on the UI thread
	 * only.
	 * 
	 * @param bitmap
	 *            The bitmap to get a thumbnail of.
	 * @param context
	 *            The application's context.
	 * 
	 * @return A thumbnail for the specified bitmap or the bitmap itself if the
	 *         thumbnail could not be created.
	 */
	public static Bitmap createBitmapThumbnail(Bitmap bitmap, int width,
			int height) {
		Bitmap.Config c = Bitmap.Config.ARGB_8888;
		Bitmap thumb = Bitmap.createBitmap(width, height, c);
		Canvas canvas = new Canvas();
		canvas.setBitmap(thumb);
		return bitmap;
	}

	/**
	 * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 */
	public static int dip2px(Context context, float dpValue) {

		final float scale = context.getResources().getDisplayMetrics().density;
		Log.v("dip2px", "scale:" + scale + "original:" + dpValue + " now:"
				+ (dpValue * scale + 0.5f));
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	//add weijie
	public static Bitmap getImageFromAssetsFile(Context context, String fileName)  
    {  
      Bitmap image = null;
      Log.v("asdf", "getImageFromAssetsFile context = "+context);
      Log.v("asdf", "getImageFromAssetsFile context is "+context.getPackageName());
      AssetManager am = context.getResources().getAssets();  
      try  
      {  
    	  BitmapFactory.Options opts=new Options();
    	  opts.inJustDecodeBounds=false;
    	  opts.inTargetDensity = 1;
          InputStream is = am.open(fileName);  
          image = BitmapFactory.decodeStream(is, new Rect(), opts);  
          is.close();  
      }  
      catch (IOException e)  
      {  
          e.printStackTrace();  
      }  
	  return image;  
    }
	/*
	public static enum ADAPT_MODE
	{
		ADAPT_MODE_
	};
	*/
	//make an scale image by current Width and height Pixels,
	//notice:
	//this param src is promised by 720X1280 Pixels
	public static Bitmap makeAdaptBitmap(Context context, Bitmap src) {
		Resources res = context.getResources();
		int display_w = res.getDisplayMetrics().widthPixels;
		
		int display_h = res.getDisplayMetrics().heightPixels;
	//	float display_w = res.getDimension(R.dimen.simulatorclock_width);
	//	float display_h = res.getDimension(R.dimen.simulatorclock_height);
		int w = (int)src.getWidth();
		int h = (int)src.getHeight();
		int dest_w = 0;
		int dest_h = 0;
		//there is a bug here 
		if(res.getDisplayMetrics().widthPixels>=480){
		    dest_w =(w * display_w)/720;
    		dest_h = (h * display_h)/1280;
		}
		else{
		    dest_w = (w * display_w)/720;
	        dest_h = (h * display_h)/1080;
		}
		float scale_x = (float)dest_w/(float)w;
		float scale_y = (float)dest_h/(float)h;

		if(scale_x == 1.0f && scale_y == 1.0)
		{
			return src;
		}
		Matrix matrix = new Matrix();
		matrix.postScale((float)scale_x, (float)scale_y, w/2, h/2);

	    Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0,
	    		w, h, matrix, true);  
	    src.recycle();
		return resizedBitmap;
	}
	
	//get current widthly Pixels
	public static int getCurrentWidthPixels(Context context)
	{
		return context.getResources().getDisplayMetrics().widthPixels;
	}
	
	//get current heightly Pixels
	public static int getCurrentHeightPixels(Context context)
	{
		return context.getResources().getDisplayMetrics().heightPixels;
	}
	
	// get the quantity of width that it need be drawed at piont in other area
	public static int getAdaptInParentCenterWitdth(int src_width,int dest_width)
	{
		return (int)(src_width - dest_width)/2;
	}
	
	// get the quantity of heigth that it need be drawed at piont in other area
	public static int getAdaptInParentCenterHeigth(int src_heigth,int dest_heigth)
	{
		return (int)(src_heigth - dest_heigth)/2;
	}
	
	public static Bitmap makeAdaptBitmap(Context context, Bitmap src, float display_width, float display_height) {
		Resources res = context.getResources();
		float display_w = display_width;
		float display_h = display_height;
		float scale_x_real;
		float scale_y_real;
		
		int w = src.getWidth();
		int h = src.getHeight();
		float scale_x = (float)(src.getWidth())/display_w;
		float scale_y = (float)(src.getHeight())/display_h;
		float scale = scale_x >= scale_y?scale_x:scale_y;
	//	if(scale == scale_x)
	//	{
	//		scale_x_real = scale_x;
	//		scale_y_real = 
	//	}
		
		
	//	int dest_w = (int)(w * display_w)/720;
	//	int dest_h = (int)(h * display_h)/1280;
	//	float scale_x = (float)dest_w/(float)w;
	//	float scale_y = (float)dest_h/(float)h;
	//	Log.v("ddddff", "makeAdaptBitmap display_w="+display_w+",display_h="+display_h+",w="+w+",h="+h+",dest_w="
	//			+dest_w+",dest_h="+dest_h+",scale_x="+scale_x+",scale_y="+scale_y);
		
		
		if(scale == 1.0f)
		{
			return src;
		}
		Matrix matrix = new Matrix();
		matrix.setScale((float)scale, (float)scale, w/2, h/2);
	 //   matrix.postScale((float)scale_x, (float)scale_y, w/2, h/2);
	    Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0,
	    		w, h, matrix, true);  
	    src.recycle();
		return resizedBitmap;
	}
}
