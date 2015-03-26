package com.iLoong.launcher.camera;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Environment;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class CameraManager implements PictureCallback
{
	
	public static final int EDIT_TEXT_H = 30;
	// 保存图片目录
	public final static String FILE_DIR = "DCIM/Photo space/";
	// 打开摄像头广播
	public final static String ACTION_OPEN_CAMERA = "com.cooee.openCamera";
	// 关闭摄像头广播
	public final static String ACTION_CLOSE_CAMERA = "com.cooee.closeCamera";
	// 开启闪光灯广播
	public final static String ACTION_TURN_LIGHT_ON = "com.cooee.turnLightOn";
	// 关闭闪光灯广播
	public final static String ACTION_TURN_LIGHT_OFF = "com.cooee.turnLightOff";
	// 切换摄像头广播
	public final static String ACTION_SWITCH_CAMERA = "com.cooee.switchCamera";
	// 拍照广播
	public final static String ACTION_TAKE_PICTURE = "com.cooee.takePicture";
	// 完成拍照广播
	public final static String ACTION_PICTURE_TAKEN = "com.cooee.pictureTaken";
	// 照相机完成开启
	public final static String ACTION_CAMERA_OPENED = "com.cooee.cameraOpened";
	//预览框的位置和大小
	public final static String ACTION_CAMERA_RECT = "com.cooee.cameraRect";
	//隐藏预览
	public final static String ACTION_HIDE_PREVIEW = "com.cooee.hidePreview";
	//进入照片编辑界面
	public final static String ACTION_OPEN_EDIT_ACTIVITY = "com.cooee.openEditActivity";
	//拍照失败
	public final static String ACTION_TAKE_PICTURE_FAILED = "com.cooee.takePictureFailed";
	//删除图片
	public final static String ACTION_DELETE_PICTURE = "com.cooee.deletePicture";
	//上下文
	private Context mContext;
	//实例
	private static CameraManager mInstance;
	//
	private TextureView mTextureView;
	//
	private FrameLayout mPreviewLayout = null;
	//
	private SurfaceTexture mSurfaceTexture = null;
	//
	private ImageView focusView = null;
	//
	private ImageView switchButton = null;
	//
	private ImageView mPreviewFg = null;
	//预览图宽度
	//private int mPreviewWidth = 0;
	// 预览图高度，决定了拍出来的照片的大小,此高度非可见高度，而是包含被遮罩部分的高度
	private int mPreviewHeight = 0;
	public static final int PICTURE_HEIGHT = 430;
	
	/**
	 * Singleton
	 */
	private CameraManager()
	{
		init();
	}
	
	public static CameraManager instance()
	{
		if( mInstance == null )
		{
			mInstance = new CameraManager();
		}
		return mInstance;
	}
	
	/**
	 * 初始化
	 */
	private void init()
	{
		mCameraHolder = CameraHolder.getInstance();
		mCameraHolder.init();
	}
	
	public void setContext(
			Context context )
	{
		mContext = context;
	}
	
	/**
	 * 注册广播
	 */
	public void registerBroadcastReceiver()
	{
		if( mContext == null )
		{
			return;
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction( ACTION_CLOSE_CAMERA );
		filter.addAction( ACTION_OPEN_CAMERA );
		filter.addAction( ACTION_TAKE_PICTURE );
		filter.addAction( ACTION_TURN_LIGHT_OFF );
		filter.addAction( ACTION_TURN_LIGHT_ON );
		filter.addAction( ACTION_CAMERA_RECT );
		//filter.addAction( ACTION_HIDE_PREVIEW );
		filter.addAction( ACTION_OPEN_EDIT_ACTIVITY );
		mContext.registerReceiver( mBroadcastReceiver , filter );
	}
	
	/**
	 * 注销广播
	 */
	public void unregisterBroadcastReceiver()
	{
		if( mContext == null )
		{
			return;
		}
		mContext.unregisterReceiver( mBroadcastReceiver );
	}
	
	/**
	 * 
	 * 播放预览图淡出动画
	 */
	private void playFgAnimation()
	{
		if( alphaAnimation == null )
		{
			alphaAnimation = new AlphaAnimation( 1 , 0 );
		}
		alphaAnimation.reset();
		alphaAnimation.setStartOffset( 500 );
		alphaAnimation.setDuration( 800 );
		alphaAnimation.setFillAfter( true );
		alphaAnimation.setInterpolator( new AccelerateInterpolator() );
		mPreviewFg.startAnimation( alphaAnimation );
	}
	
	private AlphaAnimation alphaAnimation;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		public void onReceive(
				Context context ,
				android.content.Intent intent )
		{
			try
			{
				if( intent == null )
				{
					return;
				}
				String action = intent.getAction();
				if( action.equals( ACTION_CLOSE_CAMERA ) )
				{
					if( mPreviewLayout != null )
					{
						mPreviewLayout.setVisibility( View.INVISIBLE );
					}
					if( alphaAnimation != null )
					{
						alphaAnimation.cancel();
					}
					Thread thread = new Thread() {
						
						public void run()
						{
							release();
						};
					};
					thread.start();
				}
				else if( action.equals( ACTION_OPEN_CAMERA ) )
				{
					if( mPreviewLayout != null )
					{
						mPreviewLayout.setVisibility( View.VISIBLE );
					}
					mPreviewFg.post( new Runnable() {
						
						@Override
						public void run()
						{
							if( alphaAnimation != null )
							{
								alphaAnimation.reset();
							}
							mPreviewFg.setAlpha( 1.0f );
						}
					} );
					startPreview();
					//
					if( mContext != null )
					{
						Intent openedIntent = new Intent();
						openedIntent.setAction( ACTION_CAMERA_OPENED );
						mContext.sendBroadcast( openedIntent );
					}
				}
				else if( action.equals( ACTION_TAKE_PICTURE ) )
				{
					//拍照
					takePicture();
				}
				else if( action.equals( ACTION_TURN_LIGHT_OFF ) )
				{
					//关闭闪光灯
					turnLightOff();
				}
				else if( action.equals( ACTION_TURN_LIGHT_ON ) )
				{
					//开启闪光灯
					turnLightOn();
				}
				else if( action.equals( ACTION_CAMERA_RECT ) )//设置大小
				{
					//按照x,y,w,h顺序
					int[] size = intent.getIntArrayExtra( "previewSize" );
					//设置frame的大小和位置
					if( mPreviewLayout != null )
					{
						if( size == null || size.length < 4 )
						{
							return;
						}
						LayoutParams params = (LayoutParams)mPreviewLayout.getLayoutParams();
						params.x = size[0];
						params.y = size[1];
						params.width = size[2];
						params.height = size[3];
						mPreviewLayout.requestLayout();
					}
				}
				else if( action.equals( ACTION_OPEN_EDIT_ACTIVITY ) )//编辑照片
				{
					String path = intent.getStringExtra( "path" );
					float scale = intent.getFloatExtra( "scale" , 1 );
					if( path == null )
					{
						return;
					}
					openEditActivity( path , scale );
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		};
	};
	
	/**
	 * 启动编辑Activity
	 * 
	 */
	private void openEditActivity(
			String path ,
			float scale )
	{
		if( path == null )
		{
			return;
		}
		Intent intent = new Intent( mContext , PictureEditActivity.class );
		Activity activity = iLoongLauncher.getInstance();
		//
		Rect rectgle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame( rectgle );
		int StatusBarHeight = rectgle.top;
		intent.putExtra( "path" , path );
		intent.putExtra( "scale" , scale );
		intent.putExtra( "offsetY" , -StatusBarHeight );
		activity.startActivity( intent );
		//播放动画
		activity.overridePendingTransition( RR.anim.fade_in , 0 );
	}
	
	public void setPreivewFrameLayout(
			FrameLayout frameLayout )
	{
		mPreviewLayout = frameLayout;
	}
	
	/**
	 * 寻找View
	 */
	private void findViews()
	{
		mTextureView = (TextureView)mPreviewLayout.findViewById( RR.id.preview );
		switchButton = (ImageView)mPreviewLayout.findViewById( RR.id.iv_switch_button );
		focusView = (ImageView)mPreviewLayout.findViewById( RR.id.iv_page_indicator );
		mPreviewFg = (ImageView)mPreviewLayout.findViewById( RR.id.iv_preview_fg );
	}
	
	/**
	 * 设置各种相关数据
	 */
	public void setup()
	{
		// 设置所有与camera相关数据
		findViews();
		if( mTextureView == null )
		{
			return;
		}
		mTextureView.setScaleX( 1.00001f );
		mPreviewHeight = (int)( mPreviewLayout.getLayoutParams().width * 4.0f / 3 );
		registerBroadcastReceiver();
		if( switchButton != null )
		{
			switchButton.setOnClickListener( new ImageView.OnClickListener() {
				
				@Override
				public void onClick(
						View v )
				{
					Intent intent = new Intent();
					intent.setAction( ACTION_SWITCH_CAMERA );
					if( mContext != null )
					{
						mContext.sendBroadcast( intent );
					}
					toggleCamera();
				}
			} );
		}
	}
	
	/**
	 * SurfaceTextureListener
	 */
	private SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
		
		@Override
		public void onSurfaceTextureUpdated(
				SurfaceTexture surface )
		{
			mSurfaceTexture = surface;
			if( mCamera == null )
			{
				mCamera = openBackCamera();
				if( mCamera == null )
				{
					return;
				}
				// 开始预览
				mCamera.startPreview();
				//淡出动画
				playFgAnimation();
			}
		}
		
		@Override
		public void onSurfaceTextureSizeChanged(
				SurfaceTexture surface ,
				int width ,
				int height )
		{
			mSurfaceTexture = surface;
			mPreviewHeight = height;
		}
		
		@Override
		public boolean onSurfaceTextureDestroyed(
				SurfaceTexture surface )
		{
			mSurfaceTexture = null;
			return false;
		}
		
		@Override
		public void onSurfaceTextureAvailable(
				SurfaceTexture surface ,
				int width ,
				int height )
		{
			mSurfaceTexture = surface;
			if( mCamera == null )
			{
				mCamera = openBackCamera();
				if( mCamera == null )
				{
					return;
				}
				// 开始预览
				mCamera.startPreview();
				//淡出动画
				playFgAnimation();
			}
		}
	};
	
	/**
	 * 拍照函数
	 */
	@Override
	public void onPictureTaken(
			final byte[] data ,
			final Camera camera )
	{
		if( data == null )
		{
			return;
		}
		// 拍摄日期
		String date = new SimpleDateFormat( "yyyyMMddHHmmss" ).format( new Date() );
		// 文件路径
		String dirPath = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + FILE_DIR;
		File dirFile = new File( dirPath );
		// 如果文件不存在就创建
		if( !dirFile.exists() )
		{
			dirFile.mkdir();
		}
		// 文件路径
		final String filepath = dirPath + "IMG" + date + ".jpg";
		//保存照片
		savePicture( data , filepath );
		//由于拍照后，照相机会停止预览，因此需要再次开启预览功能
		camera.stopPreview();
		camera.startPreview();
		//重新开启自动对焦
		if( Build.VERSION.SDK_INT >= 16 )
		{
			if( autoFocusObject == null )
			{
				autoFocusObject = new AutoFocusObject();
			}
			autoFocusObject.clearFocusView();
			mCamera.setAutoFocusMoveCallback( autoFocusObject.mAutoFocusMoveCallback );
		}
	}
	
	// 字体
	private Typeface mTypeFace;
	// 日期画笔
	private TextPaint mDatePaint;
	
	/**
	 * 初始化画笔
	 **/
	private void initPaint()
	{
		if( this.mDatePaint == null )
		{
			this.mDatePaint = new TextPaint();
			this.mDatePaint.setColor( Color.argb( 0xff , 0xb1 , 0xb1 , 0xb1 ) );
			this.mDatePaint.setDither( true );
			this.mDatePaint.setAntiAlias( true );
		}
	}
	
	/**
	 * 将图片数据保存到指定路径下
	 * @param data 图片数据
	 * @param path 文件路径
	 */
	private void savePicture(
			byte[] data ,
			String path )
	{
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FILE_DIR;
		File dirFile = new File( dir );
		if( !dirFile.exists() )
		{
			dirFile.mkdirs();
		}
		//
		if( data == null || path == null )
		{
			sendTakePictureFailBroadcast();
			return;
		}
		// 图像大小
		int w = mPreviewLayout.getLayoutParams().width;
		int h = mPreviewLayout.getLayoutParams().height;
		//解析图片
		Bitmap bitmap = BitmapFactory.decodeByteArray( data , 0 , data.length );
		if( bitmap == null )
		{
			Toast.makeText( mContext , mContext.getResources().getString( RR.string.no_img_data ) , 500 ).show();
			sendTakePictureFailBroadcast();
			return;
		}
		Bitmap bitmapFlipped = bitmap;
		//创建一个新图片
		//如果是前置摄像头，需要延X轴反转
		if( mCameraId != mCameraHolder.getBackCameraId() )
		{
			Matrix rotateRight = new Matrix();
			rotateRight.preRotate( 90 );
			if( android.os.Build.VERSION.SDK_INT > 13 )
			{
				float[] mirrorY = { -1 , 0 , 0 , 0 , 1 , 0 , 0 , 0 , 1 };
				rotateRight = new Matrix();
				Matrix matrixMirrorY = new Matrix();
				matrixMirrorY.setValues( mirrorY );
				rotateRight.postConcat( matrixMirrorY );
				int angle = 270;
				try
				{
					angle = Integer.parseInt( mContext.getResources().getString( RR.string.front_rotate_angle ) );
				}
				catch( Exception e )
				{
					e.printStackTrace();
				}
				rotateRight.preRotate( angle );
			}
			bitmapFlipped = Bitmap.createBitmap( bitmap , 0 , 0 , bitmap.getWidth() , bitmap.getHeight() , rotateRight , true );
		}
		Bitmap bitmap2 = bitmapFlipped;
		if( mCameraId == mCameraHolder.getBackCameraId() )
		{
			bitmap2 = BitmapUtil.rotateBitmap( bitmapFlipped , true );
		}
		Bitmap bitmap3 = BitmapUtil.resizeAndCropCenter( bitmap2 , new int[]{ w , h } , true );
		Bitmap finalBitmap = bitmap3;
		if( finalBitmap == null )
		{
			sendTakePictureFailBroadcast();
			return;
		}
		// 生成边框
		//计算背景图片大小
		//计算缩放比例
		float scale = 1.0f * w / 582;
		//带背景的宽度
		int picW = (int)( w + scale * 24 );
		int picH = (int)( PICTURE_HEIGHT * scale );
		Bitmap newBitmap = Bitmap.createBitmap( picW , picH , Bitmap.Config.ARGB_8888 );
		Canvas canvas = new Canvas( newBitmap );
		initPictureBg();
		int fgLeft = ( picW - w ) / 2;
		//将图片画到背景前面
		// 设置背景
		if( this.mPictureBgDrawable != null )
		{
			this.mPictureBgDrawable.setBounds( 0 , 0 , picW , picH );
			// 将图片画到Canvas上
			this.mPictureBgDrawable.draw( canvas );
		}
		canvas.save();
		// 左上方偏移 fgLeft量后开始绘制
		canvas.drawBitmap( finalBitmap , fgLeft , fgLeft , null );
		// 获取当前日期
		CharSequence date = getCurrentDate( mContext );
		//初始化pait
		initPaint();
		int targetY = (int)( EDIT_TEXT_H * scale );
		int size = calculateFontHeight( mDatePaint.getTypeface() , targetY );
		this.mDatePaint.setTextSize( size * 0.8f );
		int textX = (int)( picW - this.mDatePaint.measureText( date.toString() ) - 12 * scale );
		int textY = (int)( 378 * scale + EDIT_TEXT_H * scale );
		canvas.drawText( date.toString() , textX , textY , mDatePaint );
		canvas.restore();
		//
		File file = new File( path );
		BufferedOutputStream bos = null;
		//释放资源
		finalBitmap.recycle();
		try
		{
			//将图片输出到目录下
			bos = new BufferedOutputStream( new FileOutputStream( file ) );
			newBitmap.compress( Bitmap.CompressFormat.JPEG , 100 , bos );//
			bos.flush();
		}
		catch( FileNotFoundException e )
		{
			sendTakePictureFailBroadcast();
			return;
		}
		catch( IOException e )
		{
			sendTakePictureFailBroadcast();
			return;
		}
		finally
		{
			if( bos != null )
			{
				try
				{
					bos.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
				bos = null;
			}
		}
		newBitmap.recycle();
		// 发送广播，告诉相应地址
		Intent intent = new Intent();
		intent.setAction( ACTION_PICTURE_TAKEN );
		intent.putExtra( "pic" , path );
		mContext.sendBroadcast( intent );
		// 通知gallery更新,content provider 由widget收到后显示
	}
	
	/**
	 * 发送拍照失败广播
	 */
	private void sendTakePictureFailBroadcast()
	{
		Intent intent = new Intent();
		intent.setAction( ACTION_TAKE_PICTURE_FAILED );
		mContext.sendBroadcast( intent );
	}
	
	//
	private Drawable mPictureBgDrawable = null;
	
	private CharSequence getCurrentDate(
			Context context )
	{
		return DateFormat.format( "yyyy.MM.dd" , System.currentTimeMillis() );
	}
	
	/**
	 * 初始化图片背景
	 */
	private void initPictureBg()
	{
		if( this.mPictureBgDrawable != null )
		{
			return;
		}
		try
		{
			XmlResourceParser localXmlResourceParser = mContext.getResources().getXml( RR.xml.ninebg );
			this.mPictureBgDrawable = Drawable.createFromXml( mContext.getResources() , localXmlResourceParser );
			return;
		}
		catch( Exception exception )
		{
			exception.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////
	//camera 操作相关方法与变量
	private CameraHolder mCameraHolder = null;
	// 当前camera是否打开
	private boolean mCameraOpened = false;
	// 当前CameraId
	private int mCameraId = -1;
	// 当前使用的照相机
	private Camera mCamera;
	
	public boolean cameraIsOpened()
	{
		return mCamera != null;
	}
	
	/**
	 * 开启预览
	 */
	public void startPreview()
	{
		if( mTextureView == null )
		{
			return;
		}
		mTextureView.setSurfaceTextureListener( mSurfaceTextureListener );
	}
	
	/**
	 * 停止预览
	 */
	public void stopPreview()
	{
		if( mCamera == null )
		{
			return;
		}
		// 停止预览
		mCamera.stopPreview();
	}
	
	/**
	 * 拍照
	 */
	public void takePicture()
	{
		//判断是否有足够空间
		if( !StorageUtil.hasFreeExternalSpace() )
		{
			Intent intent = new Intent();
			intent.setAction( ACTION_TAKE_PICTURE_FAILED );
			mContext.sendBroadcast( intent );
			Toast.makeText( mContext , mContext.getResources().getString( RR.string.no_storage ) , 1000 ).show();
			return;
		}
		if( mCameraOpened == false || mCamera == null )
		{
			return;
		}
		mCamera.cancelAutoFocus();
		//对焦并拍摄
		mCamera.autoFocus( new AutoFocusCallback() {
			
			@Override
			public void onAutoFocus(
					boolean success ,
					Camera camera )
			{
				mCamera.takePicture( null , null , null , CameraManager.this );
			}
		} );
	}
	
	/**
	 * 开启正面camera
	 * 
	 * @return 正面camera
	 */
	public Camera openFrontCamera()
	{
		return openCamera( mCameraHolder.getFrontCameraId() );
	}
	
	/**
	 * 开启背面camera
	 * 
	 * @return 背面camera
	 */
	public Camera openBackCamera()
	{
		return openCamera( mCameraHolder.getBackCameraId() );
	}
	
	/**
	 * 切换前后摄像头
	 */
	public void toggleCamera()
	{
		//隐藏对焦
		focusView.postDelayed( new Runnable() {
			
			@Override
			public void run()
			{
				focusView.setVisibility( View.INVISIBLE );
			}
		} , 200 );
		// 如果camera没有打开就不切换
		if( !mCameraOpened )
		{
			return;
		}
		if( mCameraId == -1 )
		{
			return;
		}
		int backId = mCameraHolder.getBackCameraId();
		int frontId = mCameraHolder.getFrontCameraId();
		mCameraOpened = true;
		// 正面切换到背面
		if( frontId == mCameraId )
		{
			mCameraId = backId;
		}
		else if( backId == mCameraId )
		{
			mCameraId = frontId;
		}
		// 关闭已有的camera
		if( mCamera != null )
		{
			mCamera.stopPreview();
			mCamera.release();
		}
		mCamera = null;
		mCamera = openCamera( mCameraId );
		mCamera.startPreview();
		focusView.setVisibility( View.INVISIBLE );
	}
	
	/**
	 * 释放当前camera
	 * 
	 */
	public void release()
	{
		if( mCamera != null )
		{
			mCamera.stopPreview();
			mCamera.release();
			mCameraOpened = false;
			mCameraId = -1;
			mCamera = null;
		}
	}
	
	/**
	 * 开启指定camera
	 * 
	 * @param cameraId
	 *            相机id
	 * @return 相机实例
	 */
	public Camera openCamera(
			int cameraId )
	{
		Camera camera = null;
		try
		{
			if( mTextureView == null )
			{
				throw new Exception( "mTextureView == null" );
			}
			// 施放原有camera
			release();
			camera = Camera.open( cameraId );
			if( camera == null )
			{
				throw new Exception( "camera == null" );
			}
			mCameraId = cameraId;
			mCameraOpened = true;
			mCamera = camera;
			Camera.Parameters params = mCamera.getParameters();
			// 设置预览大小
			SizeUtil.setCameraFrameHeight( mPreviewHeight );
			Camera.Size previewSize = SizeUtil.getSuitablePreviewSize( params );
			// 设置图片大小
			Camera.Size pictureSize = SizeUtil.getSuitablePictureSizeFromPreview( params );
			params.setPreviewSize( previewSize.width , previewSize.height );
			params.setPictureSize( pictureSize.width , pictureSize.height );
			params.setPictureFormat( ImageFormat.JPEG );
			//设置图像质量
			params.setJpegQuality( 100 );
			//对于支持连续对焦的camera，设置相关对焦功能
			java.util.List<String> focusType = params.getSupportedFocusModes();
			if( focusType.contains( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE ) )
			{
				//连续对焦
				params.setFocusMode( Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE );
				//设置自动对焦相关参数
				//TODO 判断版本，只有api大于等于16才进行对焦
				if( Build.VERSION.SDK_INT >= 16 )
				{
					if( autoFocusObject == null )
					{
						autoFocusObject = new AutoFocusObject();
					}
					autoFocusObject.clearFocusView();
					mCamera.setAutoFocusMoveCallback( autoFocusObject.mAutoFocusMoveCallback );
					//mCamera.autoFocus( mAutoFocusCallback );
				}
				//
			}
			mCamera.setParameters( params );
			mCamera.setPreviewTexture( mSurfaceTexture );
			setCameraDisplayOrientation( cameraId , mCamera );
		}
		catch( Exception exception )
		{
			try
			{
				SendMsgToAndroid.sendToastMsg( mContext.getResources().getString( RR.string.camera_in_use ) );
				Intent intent = new Intent();
				intent.setAction( ACTION_HIDE_PREVIEW );
				mContext.sendBroadcast( intent );
				release();
				return null;
			}
			catch( Exception e )
			{
			}
		}
		return camera;
	}
	
	/**
	 * 开启闪光灯
	 */
	public void turnLightOn()
	{
		if( !mCameraOpened || mCamera == null || mCameraId == -1 )
		{
			return;
		}
		// 仅开启背面闪光灯
		if( mCameraId != mCameraHolder.getBackCameraId() )
		{
			return;
		}
		Parameters param = mCamera.getParameters();
		param.setFlashMode( Parameters.FLASH_MODE_ON );
		mCamera.setParameters( param );
	}
	
	/**
	 * 关闭闪光灯
	 */
	public void turnLightOff()
	{
		if( !mCameraOpened || mCamera == null || mCameraId == -1 )
		{
			return;
		}
		// 仅关闭背面闪光灯
		if( mCameraId != mCameraHolder.getBackCameraId() )
		{
			return;
		}
		Parameters param = mCamera.getParameters();
		param.setFlashMode( Parameters.FLASH_MODE_OFF );
		mCamera.setParameters( param );
	}
	
	/**
	 * 由于前置摄像头与后置摄像头方向不同，因此需要根据摄像头类型设置摄像头方向
	 * @param cameraId 摄像头id
	 * @param camera 摄像头对象
	 */
	public static void setCameraDisplayOrientation(
			int cameraId ,
			android.hardware.Camera camera )
	{
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo( cameraId , info );
		//设备方向
		int rotation = 0;
		int degrees = 0;
		switch( rotation )
		{
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}
		int result;
		if( info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
		{
			result = ( info.orientation + degrees ) % 360;
			result = ( 360 - result ) % 360; // compensate the mirror
		}
		else
		{ // back-facing
			result = ( info.orientation - degrees + 360 ) % 360;
		}
		camera.setDisplayOrientation( result );
	}
	
	public void onPause()
	{
		release();
	}
	
	public void onDestroy()
	{
		release();
		unregisterBroadcastReceiver();
	}
	
	/**
	 * 隐藏预览
	 */
	public void hidePreview()
	{
		SendMsgToAndroid.sendHidePreviewMessage();
		if( mContext == null )
		{
			return;
		}
		if( !mCameraOpened )
		{
			return;
		}
		Intent intent = new Intent();
		intent.setAction( ACTION_HIDE_PREVIEW );
		mContext.sendBroadcast( intent );
	}
	
	/**
	 * 在ui线程中关闭preview
	 */
	public void onHidePreviewMessage()
	{
		//关闭camera
		if( mPreviewLayout != null )
		{
			mPreviewLayout.setVisibility( View.INVISIBLE );
		}
		release();
		//允许显示applist
		synchronized( CameraManager.instance() )
		{
			CameraManager.instance().notify();
		}
	}
	
	/**
	 * 
	 * 用于存放一个对焦回调变量，主要用于解决低api level机器无法初始化找不到的类导致的问题
	 * @author Xu Jin
	 *
	 */
	private class AutoFocusObject
	{
		
		public void clearFocusView()
		{
			focusView.clearAnimation();
			focusView.setImageResource( RR.drawable.ic_focus_focusing );
			focusView.setImageAlpha( 0 );
			focusView.setVisibility( View.INVISIBLE );
		}
		
		public AutoFocusMoveCallback mAutoFocusMoveCallback = new AutoFocusMoveCallback() {
			
			@Override
			public void onAutoFocusMoving(
					boolean start ,
					Camera camera )
			{
				if( start )
				{
					focusView.clearAnimation();
					focusView.setVisibility( View.VISIBLE );
					focusView.setImageResource( RR.drawable.ic_focus_focusing );
					focusView.setImageAlpha( 255 );
					Animation anim = AnimationUtils.loadAnimation( mContext , RR.anim.scale_indicator );
					anim.setAnimationListener( new AnimationListener() {
						
						@Override
						public void onAnimationStart(
								Animation animation )
						{
						}
						
						@Override
						public void onAnimationRepeat(
								Animation animation )
						{
						}
						
						@Override
						public void onAnimationEnd(
								Animation animation )
						{
							focusView.setImageResource( RR.drawable.ic_focus_focused );
							focusView.postDelayed( new Runnable() {
								
								@Override
								public void run()
								{
									focusView.setVisibility( View.INVISIBLE );
									focusView.setImageAlpha( 0 );
								}
							} , 300 );
						}
					} );
					focusView.startAnimation( anim );
				}
				else
				{
				}
			}
		};
	}
	
	//负责对自动对焦进行处理
	private AutoFocusObject autoFocusObject = null;
	
	/**
	 * 计算字体大小
	 * @param targetHeight 期望的字体高度
	 * @return
	 */
	private int calculateFontHeight(
			Typeface typeface ,
			int targetHeight )
	{
		int textSize = 30;
		Paint paint = new Paint();
		paint.setTypeface( typeface );
		while( textSize > 0 )
		{
			paint.setTextSize( textSize );
			FontMetrics fm = paint.getFontMetrics();
			int h = (int)Math.ceil( fm.descent - fm.top ) + 2;
			if( h <= targetHeight )
			{
				break;
			}
			textSize--;
		}
		return textSize;
	}
}
