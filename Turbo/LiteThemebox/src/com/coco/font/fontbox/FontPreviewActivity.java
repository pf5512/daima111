package com.coco.font.fontbox;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.PreViewGallery;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class FontPreviewActivity extends Activity
{
	
	private final String LOG_TAG = "PreviewHotActivity";
	private ScrollView previewScroll;
	private DownModule downModule;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private FontInformation Information;
	private SeekBar scrollGallery;
	private PreViewGallery galleryPreview;
	private String packageName;
	private String destClassName;
	private Context mContext; // xiatian add //Statistics
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		ActivityManager.pushActivity( this );
		setContentView( R.layout.preview_hot );
		mContext = this; // xiatian add //Statistics
		scrollGallery = (SeekBar)findViewById( R.id.scrollGallery );
		galleryPreview = (PreViewGallery)findViewById( R.id.galleryPreview );
		downModule = DownModule.getInstance( this );
		Intent intent = this.getIntent();
		packageName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
		destClassName = intent.getStringExtra( StaticClass.EXTRA_CLASS_NAME );
		if( destClassName == null || destClassName.equals( "" ) )
		{
			destClassName = "";
		}
		loadFontInformation( true );
		updateShowInfo();
		{
			scrollGallery.setThumbOffset( -2 );
			scrollGallery.setEnabled( false );
			galleryPreview.setOnItemSelectedListener( new OnItemSelectedListener() {
				
				@Override
				public void onItemSelected(
						AdapterView<?> parent ,
						View view ,
						int position ,
						long id )
				{
					Log.d( LOG_TAG , "galleryPreview,position=" + position );
					if( parent.getAdapter().getCount() <= 1 )
					{
						scrollGallery.setVisibility( View.INVISIBLE );
					}
					else
					{
						scrollGallery.setVisibility( View.VISIBLE );
						scrollGallery.setProgress( position );
						scrollGallery.setMax( parent.getAdapter().getCount() - 1 );
					}
				}
				
				@Override
				public void onNothingSelected(
						AdapterView<?> parent )
				{
					Log.d( LOG_TAG , "galleryPreview,onNothingSelected" );
					if( parent.getAdapter().getCount() <= 1 )
					{
						scrollGallery.setVisibility( View.INVISIBLE );
					}
					else
					{
						scrollGallery.setVisibility( View.VISIBLE );
						scrollGallery.setProgress( 0 );
						scrollGallery.setMax( parent.getAdapter().getCount() - 1 );
					}
				}
			} );
		}
		previewScroll = (ScrollView)findViewById( R.id.previewScroll );
		final int apiLevel = Build.VERSION.SDK_INT;
		if( apiLevel >= 9 )
		{ // 2.3
			previewScroll.setOverScrollMode( View.OVER_SCROLL_NEVER );
		}
		relativeNormal = (RelativeLayout)findViewById( R.id.layoutNormal );
		relativeDownload = (RelativeLayout)findViewById( R.id.layoutDownload );
		reLayoutScroll();
		IntentFilter screenFilter = new IntentFilter();
		screenFilter.addAction( StaticClass.ACTION_PREVIEW_CHANGED );
		screenFilter.addAction( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED );
		screenFilter.addAction( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
		registerReceiver( previewReceiver , screenFilter );
		// 注册删除事件
		IntentFilter pkgFilter = new IntentFilter();
		pkgFilter.addAction( Intent.ACTION_PACKAGE_REMOVED );
		pkgFilter.addAction( Intent.ACTION_PACKAGE_ADDED );
		pkgFilter.addDataScheme( "package" );
		registerReceiver( previewReceiver , pkgFilter );
		updateShowStatus();
		// 监听返回按钮
		ImageButton btnReturn = (ImageButton)findViewById( R.id.btnReturn );
		btnReturn.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				finish();
			}
		} );
		// 分享
		ImageButton btnShare = (ImageButton)findViewById( R.id.btnShare );
		if( !com.coco.theme.themebox.util.FunctionConfig.isShareVisible() )
		{
			btnShare.setVisibility( View.GONE );
		}
		btnShare.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				sendShare();
			}
		} );
		// 更多
		Button btnMore = (Button)findViewById( R.id.btnMore );
		btnMore.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				smoothScrollMore();
			}
		} );
		if( !FunctionConfig.isThemeMoreShow() )
		{
			btnMore.setVisibility( View.GONE );
		}
		else
		{
			btnMore.setVisibility( View.VISIBLE );
		}
		// 应用按钮
		Button btnApply = (Button)findViewById( R.id.btnApply );
		btnApply.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( FunctionConfig.isEnable_topwise_style() )
				{
					String path = copyFile( PathTool.getAppFile( packageName ) , packageName );
					Intent intent = new Intent( "com.topwise.fontpath" );
					intent.putExtra( "action" , path );
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( FontPreviewActivity.this );
					pref.edit().putString( "currentFont" , packageName ).commit();
					sendBroadcast( intent );
				}
				else
				{
					String path = copyFile( PathTool.getAppFile( packageName ) , packageName );
					Intent intent = new Intent( "com.cooee.font.type.ACTION" );
					intent.putExtra( "FONT_FILE" , path );
					sendBroadcast( intent );
				}
			}
		} );
		// 安装
		Button btnInstall = (Button)findViewById( R.id.btnInstall );
		btnInstall.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				downModule.installApk( packageName , DownloadList.Font_Type );
			}
		} );
		// 暂停
		relativeDownload.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( relativeDownload.findViewById( R.id.linearDownload ).getVisibility() == View.VISIBLE )
				{
					Intent intent = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
					if( Information != null )
						intent.putExtra( "apkname" , Information.getDisplayName() );
					else
						intent.putExtra( "apkname" , packageName );
					sendBroadcast( intent );
					switchToPause();
				}
				else
				{
					Intent intent = new Intent( StaticClass.ACTION_START_DOWNLOAD_APK );
					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
					if( Information != null )
						intent.putExtra( "apkname" , Information.getDisplayName() );
					else
						intent.putExtra( "apkname" , packageName );
					sendBroadcast( intent );
					switchToDownloading();
				}
			}
		} );
		// 下载按钮
		Button btnDown = (Button)findViewById( R.id.btnDownload );
		btnDown.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( com.coco.theme.themebox.StaticClass.canDownToInternal )
				{
					File f = new File( PathTool.getAppDir() );
					int num = f.listFiles().length;
					if( num >= 5 )
					{
						recursionDeleteFile( new File( PathTool.getDownloadingDir() ) );
						Toast.makeText( mContext , mContext.getString( R.string.memory_prompt ) , Toast.LENGTH_SHORT ).show();
					}
				}
				else if( !com.coco.theme.themebox.StaticClass.isAllowDownloadWithToast( FontPreviewActivity.this ) )
				{
					return;
				}
				String enginePKG = Information.getEnginepackname();
				if( enginePKG != null && !enginePKG.equals( "" ) && !enginePKG.equals( "null" ) )
				{
					if( !Tools.isAppInstalled( mContext , enginePKG ) )
					{// 第三方引擎没有安装
						Tools.showNoticeDialog( mContext , enginePKG , Information.getEnginedesc() , Information.getEngineurl() , Information.getEnginesize() );
						return;
					}
				}
				DownloadThemeService dSv = new DownloadThemeService( FontPreviewActivity.this );
				DownloadThemeItem dItem = dSv.queryByPackageName( packageName , DownloadList.Font_Type );
				if( dItem == null )
				{
					dItem = new DownloadThemeItem();
					dItem.copyFromThemeInfo( Information.getInfoItem() );
					dItem.setDownloadStatus( DownloadStatus.StatusDownloading );
					dSv.insertItem( dItem );
				}
				loadFontInformation( false );
				Intent intent = new Intent();
				intent.setAction( StaticClass.ACTION_START_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				if( Information != null )
					intent.putExtra( "apkname" , Information.getDisplayName() );
				else
					intent.putExtra( "apkname" , packageName );
				sendBroadcast( intent );
				Log.v( "********" , "pressDown" );
				updateShowStatus();
			}
		} );
		// 购买
		Button butBuy = (Button)findViewById( R.id.btnBuy );
		butBuy.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				// TODO Auto-generated method stub
//				CooeePaymentInfo paymentInfo = new CooeePaymentInfo();
//				paymentInfo.setPrice( Information.getPrice() );
//				paymentInfo.setPayDesc( Information.getDisplayName() );
//				String payid = Information.getPricePoint();
//				Log.v( "themebox" , "payid font = " + payid );
//				if( payid == null || payid.equals( "" ) )
//				{
//					paymentInfo.setPayId( FunctionConfig.getCooeePayID( Information.getPrice() ) );
//				}
//				else
//				{
//					paymentInfo.setPayId( payid );
//				}
//				paymentInfo.setCpId( Information.getThirdparty() );
//				paymentInfo.setPayName( Information.getDisplayName() );
//				paymentInfo.setPayType( CooeePaymentInfo.PAY_TYPE_EVERY_TIME );
//				paymentInfo.setNotify( new PaymentResultReceiver() );
//				CooeePayment.getInstance().startPayService( FontPreviewActivity.this , paymentInfo );
			}
		} );
		// 删除
		Button btnDelete = (Button)findViewById( R.id.btnDelete );
		btnDelete.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				Log.d( LOG_TAG , "btnDelete" );
				Intent intent = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				if( Information != null )
					intent.putExtra( "apkname" , Information.getDisplayName() );
				else
					intent.putExtra( "apkname" , packageName );
				sendBroadcast( intent );
				DownloadThemeService dSv = new DownloadThemeService( FontPreviewActivity.this );
				dSv.deleteItem( packageName , DownloadList.Font_Type );
				intent = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				sendBroadcast( intent );
				updateShowStatus();
			}
		} );
		// 卸载按钮
		Button btnUninstall = (Button)findViewById( R.id.btnUninstall );
		btnUninstall.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				removePackage();
			}
		} );
	}
	
	@Override
	protected void onNewIntent(
			Intent intent )
	{
		// TODO Auto-generated method stub
		super.onNewIntent( intent );
		packageName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
		destClassName = intent.getStringExtra( StaticClass.EXTRA_CLASS_NAME );
		if( destClassName == null || destClassName.equals( "" ) )
		{
			destClassName = "";
		}
		loadFontInformation( true );
		updateShowInfo();
		updateShowStatus();
		if( previewScroll.getScrollY() != 0 )
		{
			previewScroll.smoothScrollTo( 0 , 0 );
		}
	}
	
	private void updateProgressSize()
	{
		if( findViewById( R.id.linearDownload ).getVisibility() == View.VISIBLE )
		{
			ProgressBar progressBar = (ProgressBar)findViewById( R.id.progressBarDown );
			progressBar.setProgress( Information.getDownloadPercent() );
			TextView text = (TextView)findViewById( R.id.textDownPercent );
			text.setText( getString( R.string.textDownloading , Information.getDownloadPercent() ) );
		}
		else
		{
			ProgressBar progressBar = (ProgressBar)findViewById( R.id.progressBarPause );
			progressBar.setProgress( Information.getDownloadPercent() );
			TextView text = (TextView)findViewById( R.id.textPausePercent );
			text.setText( getString( R.string.textPause , Information.getDownloadPercent() ) );
		}
	}
	
	@Override
	protected void onDestroy()
	{
		ActivityManager.popupActivity( this );
		unregisterReceiver( previewReceiver );
		downModule.dispose();
		if( galleryPreview.getAdapter() != null )
		{
			BaseAdapter adapter = (BaseAdapter)galleryPreview.getAdapter();
			if( adapter instanceof FontPreviewHotAdapter )
			{
				( (FontPreviewHotAdapter)adapter ).onDestory();
			}
		}
		super.onDestroy();
	}
	
	private void updateShowInfo()
	{
		TextView text = (TextView)findViewById( R.id.textAppName );
		if( Information.getPackageName().equals( getPackageName() ) )
		{
			text.setText( R.string.defaultFont );
		}
		else
			text.setText( Information.getDisplayName() );
		TextView viewAuthor = (TextView)findViewById( R.id.author );
		TextView viewInfo = (TextView)findViewById( R.id.info );
		if( !FunctionConfig.isThemeMoreShow() )
		{
			viewAuthor.setVisibility( View.GONE );
			viewInfo.setVisibility( View.GONE );
		}
		else
		{
			viewAuthor.setVisibility( View.VISIBLE );
			viewInfo.setVisibility( View.VISIBLE );
			String author = getString( R.string.previewThemeSize , Information.getApplicationSize() / 1024 , Information.getAuthor( mContext ) );
			viewAuthor.setText( author );
			if( FunctionConfig.isIntroductionVisible() )
			{
				String info = getString( R.string.previewIntroduction ) + "\n" + Information.getIntroduction();
				viewInfo.setText( info );
			}
			else
			{
				viewInfo.setVisibility( View.GONE );
			}
		}
	}
	
	private void updateInforButton()
	{
		findViewById( R.id.btnDelete ).setVisibility( View.GONE );
		findViewById( R.id.btnUninstall ).setVisibility( View.GONE );
		if( !Information.isDownloaded( this ) )
		{
			return;
		}
		if( Information.getDownloadStatus() != DownloadStatus.StatusDownloading )
		{
			findViewById( R.id.btnDelete ).setVisibility( View.VISIBLE );
		}
	}
	
	private void updateShowStatus()
	{
		updateInforButton();
		findViewById( R.id.btnSetting ).setVisibility( View.GONE );
		findViewById( R.id.btnShare ).setVisibility( View.GONE );
		relativeDownload.setClickable( false );
		if( !Information.isDownloaded( this ) )
		{
			boolean ispay = Tools.isContentPurchased( mContext , DownloadList.Font_Type , packageName );
			if( FunctionConfig.isPriceVisible() && Information.getPrice() > 0 && !ispay )
			{
				relativeNormal.setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.VISIBLE );
				return;
			}
			else
			{
				relativeDownload.setVisibility( View.GONE );
				relativeNormal.setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.VISIBLE );
				relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
				relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
				return;
			}
		}
		if( Information.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.INVISIBLE );
			relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
			return;
		}
		relativeDownload.setClickable( true );
		if( Information.getDownloadStatus() == DownloadStatus.StatusDownloading )
		{
			switchToDownloading();
		}
		else
		{
			switchToPause();
		}
	}
	
	private void switchToDownloading()
	{
		relativeDownload.setVisibility( View.VISIBLE );
		relativeNormal.setVisibility( View.GONE );
		relativeDownload.findViewById( R.id.linearDownload ).setVisibility( View.VISIBLE );
		relativeDownload.findViewById( R.id.linearPause ).setVisibility( View.GONE );
		updateProgressSize();
	}
	
	private void switchToPause()
	{
		relativeDownload.setVisibility( View.VISIBLE );
		relativeNormal.setVisibility( View.GONE );
		relativeDownload.findViewById( R.id.linearDownload ).setVisibility( View.GONE );
		relativeDownload.findViewById( R.id.linearPause ).setVisibility( View.VISIBLE );
		updateProgressSize();
	}
	
	private void loadFontInformation(
			boolean reloadGallery )
	{
		FontService service = new FontService( this );
		Information = service.queryFont( packageName );
		if( reloadGallery )
		{
			galleryPreview.setAdapter( new FontPreviewHotAdapter( this , packageName , downModule ) );
		}
	}
	
	private BroadcastReceiver previewReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String actionName = intent.getAction();
			Log.d( LOG_TAG , "action=" + actionName );
			if( actionName.equals( StaticClass.ACTION_PREVIEW_CHANGED ) )
			{
				SpinnerAdapter apt = galleryPreview.getAdapter();
				if( apt != null && apt instanceof FontPreviewHotAdapter )
				{
					( (FontPreviewHotAdapter)apt ).reload();
					scrollGallery.setMax( galleryPreview.getCount() );
					scrollGallery.setProgress( galleryPreview.getSelectedItemPosition() );
				}
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
			{
				String name = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
				if( name.equals( packageName ) )
				{
					loadFontInformation( false );
					updateShowStatus();
				}
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED ) )
			{
				if( packageName.equals( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ) )
				{
					Information.setDownloadSize( intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) );
					Information.setTotalSize( intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
					updateProgressSize();
				}
			}
		}
	};
	private boolean drawScroll = true;
	
	private void reLayoutScroll()
	{
		previewScroll.getViewTreeObserver().addOnGlobalLayoutListener( new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout()
			{
				final int pictureHeight = findViewById( R.id.preview_picture ).getLayoutParams().height;
				Log.d( "PreviewHotActivity" , "reLayoutScroll,pictureH=" + pictureHeight + ",scrollH=" + previewScroll.getHeight() );
				findViewById( R.id.preview_picture ).getLayoutParams().height = previewScroll.getHeight();
				if( pictureHeight == previewScroll.getHeight() )
				{
					drawScroll = true;
					previewScroll.getViewTreeObserver().removeGlobalOnLayoutListener( this );
				}
				else
				{
					drawScroll = false;
				}
			}
		} );
		previewScroll.getViewTreeObserver().addOnPreDrawListener( new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw()
			{
				if( drawScroll )
				{
					previewScroll.getViewTreeObserver().removeOnPreDrawListener( this );
				}
				return drawScroll;
			}
		} );
	}
	
	private void smoothScrollMore()
	{
		if( previewScroll.getScrollY() != 0 )
		{
			previewScroll.smoothScrollTo( 0 , 0 );
		}
		else
		{
			previewScroll.smoothScrollTo( 0 , previewScroll.getMaxScrollAmount() );
		}
	}
	
	private void sendShare()
	{
		Intent intent = new Intent( Intent.ACTION_SEND );
		intent.setType( "image/*" );// "text/plain"
		intent.putExtra( Intent.EXTRA_SUBJECT , getString( R.string.shareSubject ) );
		intent.putExtra( Intent.EXTRA_TEXT , getString( R.string.shareText , Information.getDisplayName() ) );
		if( saveThumb() )
		{
			intent.putExtra( Intent.EXTRA_STREAM , Uri.fromFile( new File( PathTool.getThumbTempFile() ) ) );
		}
		intent.putExtra( "sms_body" , getString( R.string.shareText , Information.getDisplayName() ) );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( Intent.createChooser( intent , getString( R.string.shareSubject ) ) );
	}
	
	private boolean saveThumb()
	{
		String thumbPath = PathTool.getThumbTempFile();
		if( thumbPath.equals( "" ) )
		{
			return false;
		}
		Context dstContext = null;
		try
		{
			dstContext = createPackageContext( packageName , Context.CONTEXT_IGNORE_SECURITY );
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
		ContentConfig destContent = new ContentConfig();
		destContent.loadConfig( dstContext , destClassName );
		return destContent.saveThumb( dstContext , thumbPath );
	}
	
	private void removePackage()
	{
		String delApkPackname = "package:" + packageName;
		Uri packageURI = Uri.parse( delApkPackname );
		Intent uninstallIntent = new Intent( Intent.ACTION_DELETE , packageURI );
		startActivity( uninstallIntent );
	}
	
	private void recursionDeleteFile(
			File file )
	{
		if( file.isFile() )
		{
			file.delete();
			return;
		}
		if( file.isDirectory() )
		{
			File[] childFile = file.listFiles();
			if( childFile == null )
			{
				return;
			}
			for( File f : childFile )
			{
				recursionDeleteFile( f );
			}
			file.delete();
		}
	}
	
	public String copyFile(
			String oldPath ,
			String pkgname )
	{
		String path = null;
		try
		{
			int byteread = 0;
			File oldfile = new File( oldPath );
			if( oldfile.exists() )
			{ //文件存在时   
				InputStream inStream = new FileInputStream( oldPath ); //读入原文件   
				//				File f = new File( "/data/data/" + getPackageName() + "/TTF" );
				//				File f = getDir( "ttf" , Context.MODE_WORLD_WRITEABLE );
				String driPath = getFilesDir().getParent() + File.separator + "TTF";
				File f = new File( driPath );
				if( f.exists() )
				{
					DeleteFile( f );
				}
				f.mkdir();
				File fileName = new File( driPath + "/" + pkgname + ".ttf" );
				String str = "chmod " + "-R 777 " + driPath;
				Runtime.getRuntime().exec( str );
				FileOutputStream fs = new FileOutputStream( fileName , false );
				byte[] buffer = new byte[1444];
				int length;
				while( ( byteread = inStream.read( buffer ) ) != -1 )
				{
					fs.write( buffer , 0 , byteread );
				}
				inStream.close();
				path = fileName.getAbsolutePath();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		return path;
	}
	
	private void DeleteFile(
			File file )
	{
		if( file.exists() == false )
		{
			return;
		}
		else
		{
			if( file.isFile() )
			{
				file.delete();
				return;
			}
			if( file.isDirectory() )
			{
				File[] childFile = file.listFiles();
				if( childFile == null || childFile.length == 0 )
				{
					file.delete();
					return;
				}
				for( File f : childFile )
				{
					DeleteFile( f );
				}
				file.delete();
			}
		}
	}
	
//	class PaymentResultReceiver implements CooeePaymentResultNotify
//	{
//		
//		public void paymentResult(
//				int resultCode ,
//				CooeePaymentInfo paymentInfo )
//		{
//			switch( resultCode )
//			{
//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_SUCCESS:
//					Toast.makeText( FontPreviewActivity.this , "计费成功" , Toast.LENGTH_SHORT ).show();
//					Tools.writePurchasedData( mContext , DownloadList.Font_Type , packageName );
//					// Message message = iapHandler.obtainMessage(BILLING_FINISH);
//					// message.sendToTarget();
//					Intent intent = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
//					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
//					mContext.sendBroadcast( intent );
//					break;
//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_FAIL:
//					Toast.makeText( FontPreviewActivity.this , "计费失败" + paymentInfo.getVersionName() , Toast.LENGTH_SHORT ).show();
//					break;
//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_CANCEL_BY_USER:
//					Toast.makeText( FontPreviewActivity.this , "用户取消付费" , Toast.LENGTH_SHORT ).show();
//					break;
//				case 1:
//					Toast.makeText( FontPreviewActivity.this , "配置免费" , Toast.LENGTH_SHORT ).show();
//					break;
//				case 2:
//					Toast.makeText( FontPreviewActivity.this , "不需要重复计费" , Toast.LENGTH_SHORT ).show();
//					break;
//				case 3:
//					Toast.makeText( FontPreviewActivity.this , "无可用指令" , Toast.LENGTH_SHORT ).show();
//					break;
//			}
//		}
//	}
}
