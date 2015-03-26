package com.zxing.activity;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.iLoong.scan.DBHelper;
import com.iLoong.scan.DialogScanDetail;
import com.iLoong.scan.HistoryActivity;
import com.iLoong.scan.HistoryBean;
import com.iLoong.scan.R;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.decoding.RGBLuminanceSource;
import com.zxing.view.ViewfinderView;


public class CaptureActivity extends Activity implements Callback , OnClickListener
{
	
	public static int screenWidth;
	public static int screenHeight;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private ToggleButton tb_flashlight;
	private ImageButton btn_history;
	private ImageButton btn_album;
	private DBHelper dbHelper;
	private List<HistoryBean> histroyList;
	private boolean isBackChoose = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;
		setContentView( R.layout.camera );
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init( getApplication() );
		viewfinderView = (ViewfinderView)findViewById( R.id.viewfinder_view );
		btn_history = (ImageButton)findViewById( R.id.btn_history );
		btn_album = (ImageButton)findViewById( R.id.btn_album );
		tb_flashlight = (ToggleButton)findViewById( R.id.tb_flashlight );
		hasSurface = false;
		inactivityTimer = new InactivityTimer( this );
		btn_history.setOnClickListener( this );
		btn_album.setOnClickListener( this );
		histroyList = new ArrayList<HistoryBean>();
		dbHelper = new DBHelper( getApplicationContext() );
		Cursor c = dbHelper.query();
		while( c.moveToNext() )
		{
			HistoryBean bean = new HistoryBean();
			bean.set_id( c.getInt( c.getColumnIndex( DBHelper.COLUMN_NAME_ID ) ) );
			bean.setCurrtime( c.getString( c.getColumnIndex( DBHelper.COLUMN_NAME_TIME ) ) );
			bean.setText( c.getString( c.getColumnIndex( DBHelper.COLUMN_NAME_CODE ) ) );
			bean.setType( c.getInt( c.getColumnIndex( DBHelper.COLUMN_NAME_TYPE ) ) );
			histroyList.add( bean );
		}
		tb_flashlight.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(
					CompoundButton buttonView ,
					boolean isChecked )
			{
				if( isChecked )
				{
					Parameters param = CameraManager.get().getCamera().getParameters();
					param.setFlashMode( Parameters.FLASH_MODE_TORCH );
					CameraManager.get().getCamera().setParameters( param );
				}
				else
				{
					Parameters param = CameraManager.get().getCamera().getParameters();
					param.setFlashMode( Parameters.FLASH_MODE_OFF );
					CameraManager.get().getCamera().setParameters( param );
				}
			}
		} );
	}
	
	@Override
	protected void onResume()
	{
		Log.i( "Turbo Scan" , "onResume" );
		super.onResume();
		re();
		decodeFormats = null;
		characterSet = null;
		playBeep = true;
		AudioManager audioService = (AudioManager)getSystemService( AUDIO_SERVICE );
		if( audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL )
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}
	
	private void re()
	{
		SurfaceView surfaceView = (SurfaceView)findViewById( R.id.preview_view );
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if( hasSurface )
		{
			initCamera( surfaceHolder );
		}
		else
		{
			surfaceHolder.addCallback( this );
			surfaceHolder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
		}
	}
	
	@Override
	protected void onPause()
	{
		Log.i( "Turbo Scan" , "onPause" );
		super.onPause();
		if( handler != null )
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}
	
	@Override
	protected void onDestroy()
	{
		inactivityTimer.shutdown();
		super.onDestroy();
	}
	
	/**
	 * Handler scan result
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(
			Result result ,
			Bitmap barcode )
	{
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		//FIXME
		if( resultString.equals( "" ) )
		{
			Toast.makeText( CaptureActivity.this , "Scan failed!" , Toast.LENGTH_SHORT ).show();
		}
		else
		{
			int contentType;
			if( resultString.contains( "http://" ) || resultString.contains( "https://" ) )
			{
				contentType = 1;
			}
			else
			{
				contentType = 2;
			}
			int repeat = isRepeat( resultString );
			if( repeat != -1 )
			{
				ContentValues cv = new ContentValues();
				cv.put( DBHelper.COLUMN_NAME_CODE , resultString );
				cv.put( DBHelper.COLUMN_NAME_TIME , getCurrTime() );
				cv.put( DBHelper.COLUMN_NAME_TYPE , contentType );
				dbHelper.update( cv , resultString );
			}
			else
			{
				ContentValues cv = new ContentValues();
				cv.put( DBHelper.COLUMN_NAME_CODE , resultString );
				cv.put( DBHelper.COLUMN_NAME_TIME , getCurrTime() );
				cv.put( DBHelper.COLUMN_NAME_TYPE , contentType );
				dbHelper.insert( cv );
				HistoryBean bean = new HistoryBean();
				bean.setCurrtime( getCurrTime() );
				bean.setText( resultString );
				bean.setType( contentType );
				histroyList.add( bean );
			}
			Toast.makeText( getApplicationContext() , resultString , Toast.LENGTH_LONG ).show();
			Intent intent = new Intent( CaptureActivity.this , DialogScanDetail.class );
			intent.putExtra( "content" , resultString );
			intent.putExtra( "type" , contentType );
			startActivity( intent );
		}
		//CaptureActivity.this.finish();
	}
	
	private void initCamera(
			SurfaceHolder surfaceHolder )
	{
		try
		{
			CameraManager.get().openDriver( surfaceHolder );
		}
		catch( IOException ioe )
		{
			return;
		}
		catch( RuntimeException e )
		{
			return;
		}
		if( handler == null )
		{
			handler = new CaptureActivityHandler( this , decodeFormats , characterSet );
		}
	}
	
	@Override
	public void surfaceChanged(
			SurfaceHolder holder ,
			int format ,
			int width ,
			int height )
	{
		Log.i( "Turbo Scan" , "surfaceChanged" );
	}
	
	@Override
	public void surfaceCreated(
			SurfaceHolder holder )
	{
		Log.i( "Turbo Scan" , "surfaceCreated" );
		if( !hasSurface )
		{
			hasSurface = true;
			initCamera( holder );
		}
	}
	
	@Override
	public void surfaceDestroyed(
			SurfaceHolder holder )
	{
		Log.i( "Turbo Scan" , "surfaceDestroyed" );
		hasSurface = false;
	}
	
	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}
	
	public Handler getHandler()
	{
		return handler;
	}
	
	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();
	}
	
	private void initBeepSound()
	{
		if( playBeep && mediaPlayer == null )
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream( AudioManager.STREAM_MUSIC );
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType( AudioManager.STREAM_MUSIC );
			mediaPlayer.setOnCompletionListener( beepListener );
			AssetFileDescriptor file = getResources().openRawResourceFd( R.raw.beep );
			try
			{
				mediaPlayer.setDataSource( file.getFileDescriptor() , file.getStartOffset() , file.getLength() );
				file.close();
				mediaPlayer.setVolume( BEEP_VOLUME , BEEP_VOLUME );
				mediaPlayer.prepare();
			}
			catch( IOException e )
			{
				mediaPlayer = null;
			}
		}
	}
	
	private static final long VIBRATE_DURATION = 200L;
	
	private void playBeepSoundAndVibrate()
	{
		if( playBeep && mediaPlayer != null )
		{
			mediaPlayer.start();
		}
		if( vibrate )
		{
			Vibrator vibrator = (Vibrator)getSystemService( VIBRATOR_SERVICE );
			vibrator.vibrate( VIBRATE_DURATION );
		}
	}
	
	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		
		public void onCompletion(
				MediaPlayer mediaPlayer )
		{
			mediaPlayer.seekTo( 0 );
		}
	};
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.btn_history:
				Intent intent = new Intent( CaptureActivity.this , HistoryActivity.class );
				startActivity( intent );
				break;
			case R.id.btn_album:
				Intent pickIntent = new Intent( Intent.ACTION_PICK );
				//				pickIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				pickIntent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , "image/*" );
				startActivityForResult( pickIntent , 11 );
				break;
		}
	}
	
	@Override
	protected void onActivityResult(
			int requestCode ,
			int resultCode ,
			Intent data )
	{
		switch( requestCode )
		{
			case 11:
				if( data != null )
					startPhotoZoom( data.getData() );
				else
				{
					Toast.makeText( getApplicationContext() , "没有选择照片" , Toast.LENGTH_SHORT ).show();
				}
				break;
			case 3:
				if( data == null )
				{
					Toast.makeText( getApplicationContext() , "没有选择照片" , Toast.LENGTH_SHORT ).show();
					return;
				}
				int type;
				isBackChoose = false;
				Result rs = decodeWithBitmap( data );
				if( rs == null )
				{
					final AlertDialog dlg = new AlertDialog.Builder( CaptureActivity.this ).create();
					dlg.show();
					Window window = dlg.getWindow();
					window.setContentView( R.layout.dl_crop );
					ImageButton back_choose = (ImageButton)window.findViewById( R.id.btn_back_choose );
					back_choose.setOnClickListener( new View.OnClickListener() {
						
						@Override
						public void onClick(
								View v )
						{
							isBackChoose = true;
							Intent pickIntent = new Intent( Intent.ACTION_PICK , null );
							pickIntent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , "image/*" );
							startActivityForResult( pickIntent , 11 );
							dlg.dismiss();
						}
					} );
					ImageButton back_scan = (ImageButton)window.findViewById( R.id.btn_back_scan );
					back_scan.setOnClickListener( new View.OnClickListener() {
						
						@Override
						public void onClick(
								View v )
						{
							dlg.dismiss();
						}
					} );
					dlg.setOnDismissListener( new OnDismissListener() {
						
						@Override
						public void onDismiss(
								DialogInterface dialog )
						{
							//							if( handler != null )
							//							{
							//								handler.quitSynchronously();
							//								handler = null;
							//							}
							//							re();
							if( !isBackChoose )
							{
								onPause();
								onResume();
							}
						}
					} );
					Toast.makeText( getApplicationContext() , "解码出错" , Toast.LENGTH_SHORT ).show();
				}
				else
				{
					String s = rs.getText();
					if( s.contains( "http://" ) || s.contains( "https://" ) )
					{
						type = 1;
					}
					else
					{
						type = 2;
					}
					Intent intent = new Intent( CaptureActivity.this , DialogScanDetail.class );
					intent.putExtra( "type" , type );
					intent.putExtra( "content" , s );
					startActivity( intent );
				}
				break;
			default:
				break;
		}
		super.onActivityResult( requestCode , resultCode , data );
	}
	
	public void startPhotoZoom(
			Uri uri )
	{
		Intent intent = new Intent( "com.android.camera.action.CROP" );
		intent.setDataAndType( uri , "image/*" );
		intent.putExtra( "crop" , "true" );
		intent.putExtra( "aspectX" , 1 );
		intent.putExtra( "aspectY" , 1 );
		intent.putExtra( "outputX" , 150 );
		intent.putExtra( "outputY" , 150 );
		intent.putExtra( "return-data" , true );
		startActivityForResult( intent , 3 );
	}
	
	private Result decodeWithBitmap(
			Intent picdata )
	{
		Bitmap photo = null;
		Bundle extras = picdata.getExtras();
		if( extras != null )
		{
			photo = extras.getParcelable( "data" );
		}
		Hashtable<EncodeHintType , String> hints = new Hashtable<EncodeHintType , String>();
		hints.put( EncodeHintType.CHARACTER_SET , "utf-8" );
		RGBLuminanceSource source = new RGBLuminanceSource( photo );
		BinaryBitmap bitmap = new BinaryBitmap( new HybridBinarizer( source ) );
		QRCodeReader reader = new QRCodeReader();
		Result result = null;
		try
		{
			result = reader.decode( bitmap );
		}
		catch( NotFoundException e )
		{
			e.printStackTrace();
			return null;
		}
		catch( ChecksumException e )
		{
			e.printStackTrace();
			return null;
		}
		catch( FormatException e )
		{
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	private String getCurrTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		Date curDate = new Date( System.currentTimeMillis() );
		return formatter.format( curDate );
	}
	
	private int isRepeat(
			String text )
	{
		int repeat = -1;
		if( histroyList.size() == 0 )
		{
			return repeat;
		}
		else
		{
			for( int i = 0 ; i < histroyList.size() ; i++ )
			{
				if( text.equals( histroyList.get( i ).getText() ) )
				{
					repeat = histroyList.get( i ).get_id();
				}
			}
		}
		return repeat;
	}
	
	private long exitTime = 0;
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN )
		{
			if( System.currentTimeMillis() - exitTime > 2000 )
			{
				Toast.makeText( getApplicationContext() , getString( R.string.exit_toast ) , Toast.LENGTH_SHORT ).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				finish();
				System.exit( 0 );
			}
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
}
