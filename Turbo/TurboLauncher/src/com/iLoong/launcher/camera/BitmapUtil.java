package com.iLoong.launcher.camera;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.iLoong.launcher.Desktop3D.Log;


public class BitmapUtil
{
	
	private static final int DEFAULT_JPEG_QUALITY = 100;
	
	/**
	 * 将位图转换为byte数组
	 * 
	 * @param bitmap
	 *            位图
	 * @return byte数组
	 */
	public static byte[] compressToBytes(
			Bitmap bitmap )
	{
		return compressToBytes( bitmap , Bitmap.CompressFormat.JPEG , DEFAULT_JPEG_QUALITY );
	}
	
	/**
	 * 将位图转换为byte数组
	 * 
	 * @param bitmap
	 *            位图
	 * @param format
	 *            图片格式
	 * @param quality
	 *            图片质量
	 * @return byte数组
	 */
	public static byte[] compressToBytes(
			Bitmap bitmap ,
			Bitmap.CompressFormat format ,
			int quality )
	{
		if( bitmap == null )
		{
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( 65536 );
		bitmap.compress( format , quality , byteArrayOutputStream );
		return byteArrayOutputStream.toByteArray();
	}
	
	/**
	 * 获得bitmap的配置
	 * 
	 * @param bitmap
	 *            位图
	 * @return 位图配置
	 */
	public static Bitmap.Config getConfig(
			Bitmap bitmap )
	{
		if( bitmap == null )
		{
			return Bitmap.Config.ARGB_8888;
		}
		return bitmap.getConfig();
	}
	
	/**
	 * 保存位图
	 * 
	 * @param bitmap
	 *            要保存的位图
	 * @param fileName
	 *            文件名
	 * @return 
	 */
	public static boolean saveBitmap(
			Bitmap bitmap ,
			String fileName )
	{
		boolean succeed = true;
		if( bitmap == null || fileName == null )
		{
			return false;
		}
		File file = new File( fileName );
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream( file );
			fos.write( compressToBytes( bitmap ) );
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
			succeed = false;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			succeed = false;
		}
		finally
		{
			if( fos != null )
			{
				try
				{
					fos.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return succeed;
	}
	
	/**
	 * 计算不同大小情况下图片采样率
	 * 
	 * @param options
	 *            参数
	 * @param reqWidth
	 *            宽度
	 * @param reqHeight
	 *            高度
	 * @return 采样率
	 */
	public static int calculateInSampleSize(
			BitmapFactory.Options options ,
			int reqWidth ,
			int reqHeight )
	{
		if( options == null )
		{
			return 0;
		}
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if( height > reqHeight || width > reqWidth )
		{
			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round( (float)height / (float)reqHeight );
			final int widthRatio = Math.round( (float)width / (float)reqWidth );
			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	
	/**
	 * 根据采样率生成bitmap
	 * 
	 * @param res
	 *            资源
	 * @param resId
	 *            资源id
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 * @return 重新采样后的bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(
			Resources res ,
			int resId ,
			int reqWidth ,
			int reqHeight )
	{
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource( res , resId , options );
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize( options , reqWidth , reqHeight );
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource( res , resId , options );
	}
	
	/**
	 * 根据采样率生成bitmap
	 * 
	 * @param path
	 *            路径
	 * @param reqWidth
	 *            目标宽度
	 * @param reqHeight
	 *            目标高度
	 * @return 重新采样后的bitmap
	 */
	public static Bitmap decodeSampledBitmapFromFile(
			String path ,
			int reqWidth ,
			int reqHeight )
	{
		if( path == null )
		{
			return null;
		}
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile( path , options );
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize( options , reqWidth , reqHeight );
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile( path , options );
	}
	
	/**
	 * 截取图片，并进行重新采样
	 * 
	 * @param bitmap
	 *            位图
	 * @param reqWidth
	 *            新的宽度
	 * @param reqHeight
	 *            新的高度
	 * @param dimension
	 *            截取部分大小
	 * @return
	 */
	// public static Bitmap cropSampledBitmapFromFile(Bitmap bitmap, int
	// reqWidth,
	// int reqHeight, int[] dimension) {
	//
	// if (bitmap == null || dimension == null || dimension.length != 4) {
	// return null;
	// }
	//
	// final BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inJustDecodeBounds = true;
	//
	// // Calculate inSampleSize
	// options.inSampleSize = calculateInSampleSize(options, reqWidth,
	// reqHeight);
	// // Decode bitmap with inSampleSize set
	// options.inJustDecodeBounds = false;
	//
	// // 截取图片
	// Bitmap cropBitmap = Bitmap.createBitmap(bitmap, dimension[0],
	// dimension[1], dimension[2], dimension[3]);
	// byte[] data = compressToBytes(cropBitmap);
	// if (data == null) {
	// return null;
	// }
	// Log.i("jinxu", "sample size "+ options.inSampleSize);
	// // 根据采样率和截取的图片重新创建bitmap
	// Bitmap sampledBitmap = BitmapFactory.decodeByteArray(data, 0,
	// data.length, options);
	//
	// // 释放
	// cropBitmap.recycle();
	//
	// return sampledBitmap;
	//
	// }
	/**
	 * 对图片进行截取并根据显示需要对图片进行重新采样，避免在底端设备上消耗过多内存
	 * 
	 * @param path
	 *            文件路径
	 * @param reqWidth
	 *            期望宽度
	 * @param reqHeight
	 *            期望高度
	 * @param dimension
	 *            从原图上截取的矩形，从左上点开始
	 * @return
	 */
	public static Bitmap cropSampledBitmapFromFile(
			String path ,
			int reqWidth ,
			int reqHeight ,
			int[] dimension )
	{
		if( path == null || dimension == null || dimension.length != 4 )
		{
			return null;
		}
		// 载入bitmap
		Bitmap originBitmap = BitmapFactory.decodeFile( path );
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile( path , options );
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize( options , reqWidth , reqHeight );
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		if( originBitmap == null )
		{
			Log.i( "jinxu" , "originBitmap == null" );
		}
		// 截取图片
		Bitmap cropBitmap = Bitmap.createBitmap( originBitmap , dimension[0] , dimension[1] , dimension[2] , dimension[3] );
		// 释放原有originBitmap
		originBitmap.recycle();
		byte[] data = compressToBytes( cropBitmap );
		if( data == null )
		{
			return null;
		}
		Log.i( "jinxu" , "sample size " + options.inSampleSize );
		// 根据采样率和新截取的图片重新创建bitmap
		Bitmap sampledBitmap = BitmapFactory.decodeByteArray( data , 0 , data.length , options );
		// 释放
		cropBitmap.recycle();
		return sampledBitmap;
	}
	
	public static Bitmap scaleAndCropBitmap(
			Bitmap bitmap ,
			int[] size ,
			boolean recycle )
	{
		if( bitmap == null || size == null )
		{
			return null;
		}
		Bitmap retBitmap = null;
		if( recycle )
		{
			bitmap.recycle();
		}
		return retBitmap;
	}
	
	/**
	 * 根据给定的bitmap和大小生成一个可以铺满size，但是不改变比例的图片
	 * 
	 * @param bitmap
	 *            位图
	 * @param newSize
	 *            新的大小
	 * @param recycle
	 *            是否回收
	 * @return
	 */
	public static Bitmap resizeAndCropCenter(
			Bitmap bitmap ,
			int[] newSize ,
			boolean recycle )
	{
		if( bitmap == null )
		{
			Log.i( "jinxu" , "resizeAndCropCenter bitmap == null" );
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if( ( newSize[0] == w ) && ( newSize[1] == h ) )
		{
			return bitmap;
		}
		float ratio = Math.max( 1.0f * newSize[0] / w , 1.0f * newSize[1] / h );
		// 创建一个新的位图
		Bitmap newBitmap = Bitmap.createBitmap( newSize[0] , newSize[1] , getConfig( bitmap ) );
		// 新的大小
		int k = Math.round( ratio * bitmap.getWidth() );
		int l = Math.round( ratio * bitmap.getHeight() );
		Canvas canvas = new Canvas( newBitmap );
		// 位移
		canvas.translate( ( newSize[0] - k ) / 2.0F , ( newSize[1] - l ) / 2.0F );
		canvas.scale( ratio , ratio );
		canvas.drawBitmap( bitmap , 0.0F , 0.0F , new Paint( 6 ) );
		if( recycle )
		{
			bitmap.recycle();
		}
		return newBitmap;
	}
	
	/**
	 * 旋转图片
	 * 
	 * @param bitmap
	 *            位图
	 * @param angle
	 *            角度
	 * @param shouldRecycle
	 *            是否要回收
	 * @return
	 */
	private Bitmap rotateBitmap(
			Bitmap bitmap ,
			int angle ,
			boolean shouldRecycle )
	{
		if( bitmap == null )
		{
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		//
		Matrix mtx = new Matrix();
		mtx.postRotate( angle );
		Bitmap newBitmap = Bitmap.createBitmap( bitmap , 0 , 0 , w , h , mtx , true );
		if( shouldRecycle )
		{
			bitmap.recycle();
		}
		return newBitmap;
	}
	
	/**
	 * 旋转图片
	 * 
	 * @param bitmap 位图
	 * @param recycle 是否释放原有位图
	 * @return
	 */
	public static Bitmap rotateBitmap(
			Bitmap bitmap ,
			boolean recycle )
	{
		if( bitmap == null )
		{
			Log.i( "jinxu" , "rotateBitmap bitmap == null" );
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		//
		Matrix mtx = new Matrix();
		mtx.postRotate( 90 );
		Bitmap bitmap2 = Bitmap.createBitmap( bitmap , 0 , 0 , w , h , mtx , true );
		//释放已有位图
		if( recycle )
		{
			bitmap.recycle();
		}
		return bitmap2;
	}
	
	public static Bitmap flipBitmapVertically(
			Bitmap bitmap ,
			boolean recycle )
	{
		if( bitmap == null )
		{
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		//
		Matrix mtx = new Matrix();
		mtx.postScale( 1 , -1 );
		Bitmap newBitmap = Bitmap.createBitmap( bitmap , 0 , 0 , w , h , mtx , true );
		if( recycle )
		{
			bitmap.recycle();
		}
		return newBitmap;
	}
}
