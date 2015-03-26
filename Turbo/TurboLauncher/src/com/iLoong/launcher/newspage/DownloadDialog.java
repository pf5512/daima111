package com.iLoong.launcher.newspage;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooeeui.brand.turbolauncher.R;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.thirdParty.analytics.umeng.UmengMobclickAgent;


public class DownloadDialog extends Activity implements OnClickListener
{
	
	public static final String KEY_EXTRA_ICON = "icon";
	public static final String KEY_EXTRA_TITLE = "title";
	public static final String KEY_EXTRA_MESSAGE = "message";
	public static final String KEY_EXTRA_URL = "url";
	public static final String KEY_EXTRA_FILE_NAME = "file_name";
	public static final String KEY_EXTRA_DOWN_ID = "down_id";
	public static final String KEY_EXTRA_DOWN_ID_SP_KEY = "down_id_sp_key";
	public static final String KEY_EXTRA_DOWNLOAD_BY_GOOGLE_PLAY = "download_by_google_play";
	private ImageView iv_icon;
	private TextView tv_title;
	private TextView tv_message;
	private ImageButton btn_download;
	private RelativeLayout rl_close;
	private int mIconId;
	private int mTitleId;
	private int mMessageId;
	private String mUrl;
	private String mFileName;
	private DownloadManager mDownloadManager;
	private DownloadManager.Query query;
	public static long mFileSize;
	private long mDownId = -1;
	private String mDownIdSpKey;
	private boolean mDownloadByGooglePlay;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dialog_download_hint );
		if( VERSION.SDK_INT > 11 )
			setFinishOnTouchOutside( true );
		init();
		mDownloadManager = (DownloadManager)getSystemService( Context.DOWNLOAD_SERVICE );
	}
	
	private void init()
	{
		iv_icon = (ImageView)findViewById( R.id.iv_icon );
		tv_title = (TextView)findViewById( R.id.tv_title );
		tv_message = (TextView)findViewById( R.id.tv_message );
		btn_download = (ImageButton)findViewById( R.id.btn_download );
		rl_close = (RelativeLayout)findViewById( R.id.rl_close );
		btn_download.setOnClickListener( this );
		rl_close.setOnClickListener( this );
		mIconId = getIntent().getIntExtra( KEY_EXTRA_ICON , R.drawable.logo3 );
		mTitleId = getIntent().getIntExtra( KEY_EXTRA_TITLE , -1 );
		mMessageId = getIntent().getIntExtra( KEY_EXTRA_MESSAGE , -1 );
		mUrl = getIntent().getStringExtra( KEY_EXTRA_URL );
		mFileName = getIntent().getStringExtra( KEY_EXTRA_FILE_NAME );
		mDownId = getIntent().getLongExtra( KEY_EXTRA_DOWN_ID , -1 );
		mDownIdSpKey = getIntent().getStringExtra( KEY_EXTRA_DOWN_ID_SP_KEY );
		mDownloadByGooglePlay = getIntent().getBooleanExtra( KEY_EXTRA_DOWNLOAD_BY_GOOGLE_PLAY , false );
		iv_icon.setImageResource( mIconId );
		tv_title.setText( mTitleId );
		tv_message.setText( mMessageId );
	}
	
	private void reDoDownload()
	{
		mDownloadManager.remove( mDownId );
		doDownload();
	}
	
	private void doDownload()
	{
		try
		{
			if( !isConnectingToInternet() )
			{
				Toast.makeText( getApplicationContext() , R.string.dlmng_check_network , Toast.LENGTH_SHORT ).show();
				return;
			}
			if( VERSION.SDK_INT < 9 || !Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) )
			{
				doDownloadBrowser( mUrl );
				return;
			}
			DownloadManager.Request request = new DownloadManager.Request( Uri.parse( mUrl ) );
			request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI );
			request.setShowRunningNotification( true );
			request.setVisibleInDownloadsUi( true );
			request.setDestinationInExternalPublicDir( "/CooeeDownload/" , mFileName );
			long downId = mDownloadManager.enqueue( request );
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( this );
			Editor editor = sp.edit();
			editor.putLong( mDownIdSpKey , downId );
			editor.commit();
		}
		catch( IllegalArgumentException e )
		{
			SendMsgToAndroid.sendToastMsg( iLoongLauncher.getInstance().getString( RR.string.dlmng_sys_download_unable ) );
			return;
		}
	}
	
	private void doDownloadBrowser(
			String url )
	{
		Uri uri = Uri.parse( mUrl );
		Intent intent = new Intent( Intent.ACTION_VIEW , uri );
		startActivity( intent );
	}
	
	private void queryDownloadStatus()
	{
		if( query == null )
			query = new DownloadManager.Query();
		query.setFilterById( mDownId );
		Cursor c = mDownloadManager.query( query );
		if( c.moveToFirst() )
		{
			int status = c.getInt( c.getColumnIndex( DownloadManager.COLUMN_STATUS ) );
			mFileSize = c.getLong( c.getColumnIndex( DownloadManager.COLUMN_TOTAL_SIZE_BYTES ) );
			switch( status )
			{
				case DownloadManager.STATUS_PAUSED:
					Log.v( "download" , "STATUS_PAUSED" );
					break;
				case DownloadManager.STATUS_PENDING:
					Log.v( "download" , "STATUS_PENDING" );
					break;
				case DownloadManager.STATUS_RUNNING:
					Log.v( "download" , "STATUS_RUNNING" );
					SendMsgToAndroid.sendToastMsg( getString( R.string.umeng_common_action_info_exist ) );
					break;
				case DownloadManager.STATUS_SUCCESSFUL:
					Log.v( "download" , "STATUS_SUCCESSFUL" );
					String uri = c.getString( c.getColumnIndex( DownloadManager.COLUMN_LOCAL_URI ) );
					if( !DownloadReceiver.installApk( this , uri.substring( 5 ) ) )
					{
						reDoDownload();
					}
					break;
				case DownloadManager.STATUS_FAILED:
					Log.v( "download" , "STATUS_FAILED" );
					reDoDownload();
					break;
				default:
					break;
			}
		}
	}
	
	@Override
	public void onClick(
			View v )
	{
		switch( v.getId() )
		{
			case R.id.btn_download:
				if( mDownIdSpKey.equals( "Ami_download_id" ) )
				{
					UmengMobclickAgent.FirstTime( iLoongLauncher.getInstance() , "FirstTimeDownloadAmi" );
				}
				else if( mDownIdSpKey.equals( "calendar_download_id" ) )
				{
					UmengMobclickAgent.FirstTime( iLoongLauncher.getInstance() , "FirstTimeDownloadCalendar" );
				}
				else if( mDownIdSpKey.equals( "newspage_download_id" ) )
				{
					UmengMobclickAgent.FirstTime( iLoongLauncher.getInstance() , "FirstTimeDownloadNewspage" );
				}
				if( mDownloadByGooglePlay && isPlayStoreInstalled() )
				{
					Uri playUri = Uri.parse( mUrl );
					Intent browserIntent = new Intent( Intent.ACTION_VIEW , playUri );
					browserIntent.setClassName( "com.android.vending" , "com.android.vending.AssetBrowserActivity" );
					browserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
					startActivity( browserIntent );
				}
				else
				{
					if( mDownId != -1 )
					{
						queryDownloadStatus();
					}
					else
					{
						doDownload();
					}
				}
				finish();
				break;
			case R.id.rl_close:
				finish();
				break;
			default:
				break;
		}
	}
	
	private boolean isPlayStoreInstalled()
	{
		String playPkgName = "com.android.vending";
		try
		{
			PackageInfo pckInfo = this.getPackageManager().getPackageInfo( playPkgName , PackageManager.GET_ACTIVITIES );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isConnectingToInternet()
	{
		ConnectivityManager connectivity = (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );
		if( connectivity != null )
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if( info != null )
			{
				for( int i = 0 ; i < info.length ; i++ )
				{
					if( info[i].getState() == NetworkInfo.State.CONNECTED )
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
