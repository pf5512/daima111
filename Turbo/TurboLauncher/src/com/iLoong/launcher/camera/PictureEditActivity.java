package com.iLoong.launcher.camera;


import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iLoong.RR;


public class PictureEditActivity extends Activity implements android.view.View.OnClickListener
{
	
	public int OFFSET_Y = 0;
	public int OFFSET_X = 0;
	//定义常量
	//图片x，y
	public static final int IMAGE_X = 57;
	public static final int IMAGE_Y = 72;
	//图片w，h
	public static final int IMAGE_W = 606;
	public static final int IMAGE_H = 421;
	//
	public static final int EDIT_TEXT_X = 69;
	public static final int EDIT_TEXT_Y = 444;
	public static final int EDIT_TEXT_W = 450;
	public static final int EDIT_TEXT_H = 30;
	//
	public static final int BUTTON_LAYOUT_Y = 575;
	public static final int DELETE_BUTTON_W = 144;
	public static final int DELETE_BUTTON_H = 74;
	//
	public static final int COUNT_VIEW_X = 519;
	public static final int COUNT_VIEW_Y = 450;
	public static final int COUNT_VIEW_W = 132;
	public static final int COUNT_VIEW_H = 0;
	public static int DATE_RIGHT_X = 0;
	public static int DATE_RIGHT_Y = 0;
	//
	public static final int COVER_X = 69 + 10;
	public static final int COVER_y = 423;
	public static final int COVER_W = 582;
	public static final int COVER_H = 60;
	//views
	private ImageView imageView = null;
	private EditText editText = null;
	private TextView wordCountTextView = null;
	private Button deleteButton = null;
	private ImageView coverView = null;
	//
	private LinearLayout buttonLayout = null;
	//缩放比例
	private float mScale = 1.0f;
	//普通 
	private static final int STATE_NORMAL = 0x01;
	//编辑 
	private static final int STATE_EDIT = 0x02;
	// 编辑模式
	private int editState = STATE_NORMAL;
	// 图片路径
	private String mPicPath;
	private Bitmap bitmap;
	private Typeface mTypeFace;
	private AbsoluteLayout windowLayout;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
		super.onCreate( savedInstanceState );
		Intent intent = getIntent();
		mScale = intent.getFloatExtra( "scale" , 1.0f );
		OFFSET_Y = intent.getIntExtra( "offsetY" , 0 );
		int width = getResources().getDisplayMetrics().widthPixels;
		OFFSET_X = (int)( width - mScale * 720 ) / 2;
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate( RR.layout.camera_edit_frame , null );
		setContentView( view );
		//
		initPictureBg();
		//查找view
		findViews();
		registerListener();
		//初始化图片
		setLayout();
		//
		setupImageView();
		// 
		initPaint();
		//
		android.view.ViewGroup.LayoutParams lay = getWindow().getAttributes();
		setParams( lay );
	}
	
	@Override
	protected void onDestroy()
	{
		if( bitmap != null && !bitmap.isRecycled() )
		{
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}
		super.onDestroy();
	}
	
	private void findViews()
	{
		windowLayout = (AbsoluteLayout)findViewById( RR.id.AbsoluteLayout1 );
		buttonLayout = (LinearLayout)findViewById( RR.id.btn_layout );
		imageView = (ImageView)findViewById( RR.id.iv_img );
		editText = (EditText)findViewById( RR.id.et_mood );
		wordCountTextView = (TextView)findViewById( RR.id.tv_text_count );
		deleteButton = (Button)findViewById( RR.id.btn_delete );
		coverView = (ImageView)findViewById( RR.id.iv_cover );
	}
	
	private void setParams(
			android.view.ViewGroup.LayoutParams lay )
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( dm );
		Rect rect = new Rect();
		View view = getWindow().getDecorView();
		view.getWindowVisibleDisplayFrame( rect );
		lay.height = dm.heightPixels;// - rect.top;
		lay.width = dm.widthPixels;
	}
	
	/**
	 * 注册监听
	 */
	private void registerListener()
	{
		windowLayout.setOnClickListener( this );
		imageView.setOnClickListener( this );
		deleteButton.setOnClickListener( this );
	}
	
	/**
	 * 处理图片点击事件
	 */
	private void onClickImage()
	{
		if( editState == STATE_EDIT )
		{
			return;
		}
		editState = STATE_EDIT;
		// 显示 edittext，开启编辑模式
		this.editText.setVisibility( View.VISIBLE );
		this.editText.requestFocus();
		this.wordCountTextView.setVisibility( View.VISIBLE );
		this.coverView.setVisibility( View.VISIBLE );
		//
		InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE );
		imm.showSoftInput( editText , InputMethodManager.SHOW_IMPLICIT );
		wordCountTextView.setText( "0 / 15" );
		editText.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged(
					CharSequence s ,
					int start ,
					int before ,
					int count )
			{
			}
			
			@Override
			public void beforeTextChanged(
					CharSequence s ,
					int start ,
					int count ,
					int after )
			{
			}
			
			@Override
			public void afterTextChanged(
					Editable s )
			{
				// 设置文字数量
				int count = editText.getText().length();
				wordCountTextView.setText( count + " / 15" );
			}
		} );
	}
	
	private void deleteImage()
	{
		deleteSDFile( mPicPath );
		scanMediaProvider( mPicPath );
		this.finish();
		overridePendingTransition( 0 , RR.anim.fade_out );
	}
	
	/**
	 * 扫描媒体库
	 * @param path 文件
	 */
	private void scanMediaProvider(
			String path )
	{
		try
		{
			String[] projection = { MediaStore.Images.Media._ID };
			// Match on the file path
			String selection = MediaStore.Images.Media.DATA + " = ?";
			String[] selectionArgs = new String[]{ path };
			// Query for the ID of the media matching the file path
			Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			ContentResolver contentResolver = getContentResolver();
			Cursor c = contentResolver.query( queryUri , projection , selection , selectionArgs , null );
			if( c.moveToFirst() )
			{
				// We found the ID. Deleting the item via the content provider will also remove the file
				long id = c.getLong( c.getColumnIndexOrThrow( MediaStore.Images.Media._ID ) );
				Uri deleteUri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , id );
				contentResolver.delete( deleteUri , null , null );
			}
			else
			{
				// File not found in media store DB
			}
			c.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	private Drawable mPictureBgDrawable = null;
	
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
			XmlResourceParser localXmlResourceParser = getResources().getXml( RR.xml.ninebg );
			this.mPictureBgDrawable = Drawable.createFromXml( getResources() , localXmlResourceParser );
			return;
		}
		catch( Exception exception )
		{
			exception.printStackTrace();
		}
	}
	
	private void saveImage()
	{
		if( bitmap == null )
		{
			return;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap newBitmap = Bitmap.createBitmap( width , height , Bitmap.Config.ARGB_8888 );
		Canvas canvas = new Canvas( newBitmap );
		// draw image
		canvas.drawBitmap( bitmap , new Matrix() , new Paint() );
		// draw bg
		Rect clipRect = new Rect( 0 , (int)( 351 * mScale ) , width , height );
		canvas.save();
		canvas.clipRect( clipRect );
		if( this.mPictureBgDrawable != null )
		{
			this.mPictureBgDrawable.setBounds( 0 , 0 , width , height );
			// 将图片画到Canvas上
			this.mPictureBgDrawable.draw( canvas );
		}
		canvas.restore();
		int x = (int)( 12 * mScale );
		//FontMetrics fm = mMoodPaint.getFontMetrics();
		int y = (int)( 378 * mScale + EDIT_TEXT_H * mScale );
		// draw mood
		canvas.drawText( editText.getEditableText().toString() , x , y , mMoodPaint );
		// draw date
		String dateString = getCurrentDate( this ).toString();
		DATE_RIGHT_X = width - (int)( 12 * mScale );
		DATE_RIGHT_Y = y;
		float w = mDatePaint.measureText( dateString );
		canvas.drawText( dateString , DATE_RIGHT_X - w , DATE_RIGHT_Y , mDatePaint );
		//将图片保存到 本地
		//String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/a/" + "123" + ".jpg";
		boolean ret = BitmapUtil.saveBitmap( newBitmap , mPicPath );
		if( !ret )
		{
			Toast.makeText( getApplicationContext() , getResources().getString( RR.string.camera_save_error ) , 500 ).show();
			//提示保存失败 
			//			getResources().getString( id );
		}
		// 通知gallery更新content provider
		if( mPicPath != null )
		{
			Uri uri = Uri.parse( "file://" + mPicPath );
			sendBroadcast( new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE , uri ) );
		}
		newBitmap.recycle();
	}
	
	private CharSequence getCurrentDate(
			Context context )
	{
		return DateFormat.format( "yyyy.MM.dd" , System.currentTimeMillis() );
	}
	
	/**
	 * 设置显示的图片
	 */
	private void setupImageView()
	{
		mPicPath = getIntent().getStringExtra( "path" );
		if( mPicPath == null )
		{
			return;
		}
		bitmap = BitmapFactory.decodeFile( mPicPath );
		if( bitmap == null )
		{
			return;
		}
		this.imageView.setImageBitmap( bitmap );
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		//如果用户编辑了文字，就保存
		if( editState != STATE_NORMAL )
		{
			saveImage();
		}
		overridePendingTransition( 0 , RR.anim.fade_out );
	}
	
	/**
	 * 初始化布局
	 */
	private void setLayout()
	{
		//imageView
		AbsoluteLayout.LayoutParams params = (LayoutParams)imageView.getLayoutParams();
		params.x = (int)( IMAGE_X * mScale ) + OFFSET_X;
		params.y = (int)( IMAGE_Y * mScale ) + OFFSET_Y;
		params.width = (int)( IMAGE_W * mScale );
		params.height = (int)( IMAGE_H * mScale );
		//button layout
		params = (LayoutParams)buttonLayout.getLayoutParams();
		params.y = (int)( BUTTON_LAYOUT_Y * mScale ) + OFFSET_Y;
		//
		params = (LayoutParams)editText.getLayoutParams();
		params.x = (int)( EDIT_TEXT_X * mScale ) + OFFSET_X;
		params.y = (int)( EDIT_TEXT_Y * mScale ) + OFFSET_Y;
		params.width = (int)( EDIT_TEXT_W * mScale );
		params.height = (int)( EDIT_TEXT_H * mScale );
		//
		int size = calculateFontHeight( params.height );
		editText.setTextSize( size / getResources().getDisplayMetrics().density );
		//delete button
		LinearLayout.LayoutParams buttonParam = (LinearLayout.LayoutParams)deleteButton.getLayoutParams();
		buttonParam.width = (int)( DELETE_BUTTON_W * mScale );
		buttonParam.height = (int)( DELETE_BUTTON_H * mScale );
		//count text view
		params = (LayoutParams)wordCountTextView.getLayoutParams();
		params.width = (int)( COUNT_VIEW_W * mScale );
		params.height = (int)( EDIT_TEXT_H * mScale );
		params.x = (int)( COUNT_VIEW_X * mScale ) + OFFSET_X;
		params.y = (int)( COUNT_VIEW_Y * mScale ) + OFFSET_Y;
		size = calculateFontHeight( params.height );
		wordCountTextView.setTextSize( size / getResources().getDisplayMetrics().density );
		//cover view
		params = (LayoutParams)coverView.getLayoutParams();
		params.x = (int)( COVER_X * mScale ) + OFFSET_X;
		params.y = (int)( COVER_y * mScale ) + OFFSET_Y;
		params.width = (int)( COVER_W * mScale );
		params.height = (int)( COVER_H * mScale );
		coverView.setBackgroundDrawable( mPictureBgDrawable );
	}
	
	/**
	 * 计算字体大小
	 * @param targetHeight 期望的字体高度
	 * @return
	 */
	private int calculateFontHeight(
			int targetHeight )
	{
		int textSize = 30;
		Typeface typeface = editText.getTypeface();
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
	
	@Override
	public void onClick(
			View v )
	{
		if( v == deleteButton )
		{
			deleteImage();
		}
		else if( v == imageView )
		{
			onClickImage();
		}
		else if( v == windowLayout )
		{
			if( editState != STATE_NORMAL )
			{
				//保存图片
				saveImage();
			}
			this.finish();
			overridePendingTransition( 0 , RR.anim.fade_out );
		}
	}
	
	/////////////////////////////////////////////
	//画笔相关
	private TextPaint mDatePaint;
	private TextPaint mMoodPaint;
	
	/**
	 * 初始化画笔，分别对应日期画笔和心情画笔
	 */
	private void initPaint()
	{
		this.mMoodPaint = new TextPaint();
		this.mMoodPaint.setColor( Color.argb( 0xff , 0x50 , 0x50 , 0x50 ) );
		this.mMoodPaint.setTextSize( editText.getTextSize() );
		this.mMoodPaint.setTypeface( this.mTypeFace );
		this.mMoodPaint.setDither( true );
		this.mMoodPaint.setAntiAlias( true );
		this.mDatePaint = new TextPaint();
		this.mDatePaint.setTextSize( editText.getTextSize() * 0.8f );
		this.mDatePaint.setTypeface( this.mTypeFace );
		this.mDatePaint.setDither( true );
		this.mDatePaint.setAntiAlias( true );
		this.mDatePaint.setColor( Color.argb( 0xff , 0xb1 , 0xb1 , 0xb1 ) );
	}
	
	public void deleteSDFile(
			String path )
	{
		if( isEmtryString( path ) )
		{
			return;
		}
		File file = new File( path );
		if( !file.isFile() || !file.exists() )
		{
			return;
		}
		file.delete();
	}
	
	/**
	 * 判断字符串内容是否为空
	 * 
	 * @param str
	 *            字符串
	 * @return 如果不为空返回false,为空返回true
	 */
	public static boolean isEmtryString(
			String str )
	{
		if( ( str == null ) || ( "".equals( str ) ) )
		{
			return true;
		}
		return false;
	}
}
