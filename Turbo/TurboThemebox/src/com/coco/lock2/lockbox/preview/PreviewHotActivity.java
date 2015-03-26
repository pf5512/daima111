package com.coco.lock2.lockbox.preview;


import java.io.File;
import java.net.URISyntaxException;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
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

import com.android.internal.widget.LockPatternUtils;
import com.coco.download.DownloadList;
import com.coco.lock2.lockbox.LockInformation;
import com.coco.lock2.lockbox.LockService;
import com.coco.lock2.lockbox.PlatformInfo;
import com.coco.lock2.lockbox.StaticClass;
import com.coco.lock2.lockbox.util.ContentConfig;
import com.coco.lock2.lockbox.util.LockManager;
import com.coco.lock2.lockbox.util.PathTool;
import com.coco.theme.themebox.PreViewGallery;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class PreviewHotActivity extends Activity
{
	
	private ScrollView previewScroll;
	private DownModule downModule;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private LockInformation lockInformation;
	private SeekBar scrollGallery;
	private PreViewGallery galleryPreview;
	private String packageName;
	private String destClassName;
	private Context mContext;
	private boolean isShowShare = true;
	private boolean isShowMore = true;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		mContext = this;
		setContentView( R.layout.preview_hot );
		scrollGallery = (SeekBar)findViewById( R.id.scrollGallery );
		galleryPreview = (PreViewGallery)findViewById( R.id.galleryPreview );
		downModule = DownModule.getInstance( this );
		Intent intent = this.getIntent();
		packageName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
		destClassName = intent.getStringExtra( StaticClass.EXTRA_CLASS_NAME );
		String path = intent.getStringExtra( "CustomRootPath" );
		isShowShare = intent.getBooleanExtra( "isshare" , true );
		isShowMore = intent.getBooleanExtra( "ishowmore" , true );
		if( path != null )
			PathTool.setCustomRootPath( path );
		else
			PathTool.setCustomRootPath( "/Coco/" );
		if( destClassName == null || destClassName.equals( "" ) )
		{
			destClassName = "";
		}
		loadLockInformation( true );
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
					scrollGallery.setMax( parent.getAdapter().getCount() - 1 );
					scrollGallery.setProgress( position );
				}
				
				@Override
				public void onNothingSelected(
						AdapterView<?> parent )
				{
					scrollGallery.setMax( parent.getAdapter().getCount() - 1 );
					scrollGallery.setProgress( 0 );
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
		screenFilter.addAction( StaticClass.ACTION_DOWNLOAD_ERROR );
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
		btnShare.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				sendShare();
			}
		} );
		// 设置
		ImageButton btnSetting = (ImageButton)findViewById( R.id.btnSetting );
		btnSetting.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				Intent intentSetting = new Intent();
				intentSetting.setClassName( packageName , lockInformation.getSettingClassName() );
				startActivity( intentSetting );
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
		if( isShowMore )
		{
			btnMore.setVisibility( View.VISIBLE );
		}
		else
			btnMore.setVisibility( View.GONE );
		// 应用按钮
		Button btnApply = (Button)findViewById( R.id.btnApply );
		btnApply.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				// //应用统计
				// StatisticsExpand.Apply(mContext, packageName, destClassName);
				// Log.v("Statistics","apply packname ="+packageName+" classname = "+destClassName);
				LockPatternUtils utils = new LockPatternUtils( mContext );
				if( FunctionConfig.isEnable_topwise_style() && ( Settings.System.getInt( getContentResolver() , "system.settings.lockstyle" , -1 ) != 20 || utils.isSecure() || utils
						.isLockScreenDisabled() ) )
				{
					try
					{
						Toast.makeText( PreviewHotActivity.this , getString( R.string.lock_apply_toast ) , Toast.LENGTH_SHORT ).show();
						Intent intent = Intent.parseUri( "intent:#Intent;action=android.intent.action.MAIN;component=com.android.settings/.Settings$SecuritySettingsActivity;end" , 0 );
						startActivity( intent );
					}
					catch( URISyntaxException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				LockManager mgr = new LockManager( PreviewHotActivity.this );
				mgr.applyLock( packageName , destClassName , lockInformation.getWrapName() );
				Toast.makeText( PreviewHotActivity.this , getString( R.string.toastPreviewApply , lockInformation.getDisplayName() ) , Toast.LENGTH_SHORT ).show();
				sendBroadcast( new Intent( StaticClass.ACTION_DEFAULT_LOCK_CHANGED ) );
				updateShowStatus();
				// 打开启用锁屏的开�?
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( PreviewHotActivity.this ).edit();
				editor.putBoolean( StaticClass.ENABLE_LOCK , true );
				editor.commit();
				if( !PlatformInfo.getInstance( mContext ).isSupportViewLock() )
				{
					Intent intent1 = new Intent( mContext , LockService.class );
					intent1.setAction( StaticClass.ACTION_KILL_SYSLOCK );
					mContext.startService( intent1 );
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
				downModule.installApk( packageName , DownloadList.Lock_Type );
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
					if( lockInformation != null )
						intent.putExtra( "apkname" , lockInformation.getDisplayName() );
					else
						intent.putExtra( "apkname" , packageName );
					sendBroadcast( intent );
					switchToPause();
				}
				else
				{
					Intent intent = new Intent( StaticClass.ACTION_START_DOWNLOAD_APK );
					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
					if( lockInformation != null )
						intent.putExtra( "apkname" , lockInformation.getDisplayName() );
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
				if( !StaticClass.isAllowDownloadWithToast( PreviewHotActivity.this ) )
				{
					return;
				}
				String enginePKG = lockInformation.getEnginepackname();
				if( enginePKG != null && !enginePKG.equals( "" ) && !enginePKG.equals( "null" ) )
				{
					if( !Tools.isAppInstalled( mContext , enginePKG ) )
					{// 第三方引擎没有安装
						Tools.showNoticeDialog( mContext , enginePKG , lockInformation.getEnginedesc() , lockInformation.getEngineurl() , lockInformation.getEnginesize() );
						return;
					}
				}
				DownloadThemeService dSv = new DownloadThemeService( PreviewHotActivity.this );
				DownloadThemeItem dItem = dSv.queryByPackageName( packageName , DownloadList.Lock_Type );
				if( dItem == null )
				{
					// //下载统计
					// StatisticsExpand.StartDown(mContext, packageName);
					// Log.v("Statistics","down packname ="+packageName);
					new File( PathTool.getDownloadingApp( packageName ) ).delete();
					dItem = new DownloadThemeItem();
					dItem.copyFromThemeInfo( lockInformation.getInfoItem() );
					dItem.setDownloadStatus( DownloadStatus.StatusDownloading );
					dSv.insertItem( dItem );
				}
				else
				{
					// //继续下载统计
					// StatisticsExpand.ContinueDown(mContext, packageName);
					// Log.v("Statistics","continuedown packname ="+packageName);
				}
				loadLockInformation( false );
				updateShowStatus();
				Intent intent = new Intent();
				intent.setAction( StaticClass.ACTION_START_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				if( lockInformation != null )
					intent.putExtra( "apkname" , lockInformation.getDisplayName() );
				else
					intent.putExtra( "apkname" , packageName );
				sendBroadcast( intent );
			}
		} );
		// 购买
		Button butBuy = (Button)findViewById( R.id.btnBuy );
		butBuy.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
//				// TODO Auto-generated method stub
//				CooeePaymentInfo paymentInfo = new CooeePaymentInfo();
//				paymentInfo.setPrice( lockInformation.getPrice() );
//				paymentInfo.setPayDesc( lockInformation.getDisplayName() );
//				String payid = lockInformation.getPricePoint();
//				Log.v( "themebox" , "payid lock = " + payid );
//				if( payid == null || payid.equals( "" ) || payid.equals( "null" ) )
//				{
//					paymentInfo.setPayId( FunctionConfig.getCooeePayID( lockInformation.getPrice() ) );
//				}
//				else
//				{
//					paymentInfo.setPayId( payid );
//				}
//				paymentInfo.setCpId( lockInformation.getThirdparty() );
//				paymentInfo.setPayName( lockInformation.getDisplayName() );
//				paymentInfo.setPayType( CooeePaymentInfo.PAY_TYPE_EVERY_TIME );
//				paymentInfo.setNotify( new PaymentResultReceiver() );
//				CooeePayment.getInstance().startPayService( PreviewHotActivity.this , paymentInfo );
			}
		} );
		// 删除
		Button btnDelete = (Button)findViewById( R.id.btnDelete );
		btnDelete.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				// //删除统计
				// StatisticsExpand.Delete(mContext, packageName);
				// Log.v("Statistics","delete packname ="+packageName);
				//
				Intent intent = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				if( lockInformation != null )
					intent.putExtra( "apkname" , lockInformation.getDisplayName() );
				else
					intent.putExtra( "apkname" , packageName );
				sendBroadcast( intent );
				DownloadThemeService dSv = new DownloadThemeService( PreviewHotActivity.this );
				dSv.deleteItem( packageName , DownloadList.Lock_Type );
				new File( PathTool.getDownloadingApp( packageName ) ).delete();
				// ***********
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
				// 卸载统计
				// StatisticsExpand.StartDown(mContext, packageName);
				// Log.v("Statistics","uninstall packname ="+packageName);
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
		String path = intent.getStringExtra( "CustomRootPath" );
		isShowShare = intent.getBooleanExtra( "isshare" , true );
		isShowMore = intent.getBooleanExtra( "ishowmore" , true );
		if( path != null )
			PathTool.setCustomRootPath( path );
		else
			PathTool.setCustomRootPath( "/Coco/" );
		if( destClassName == null || destClassName.equals( "" ) )
		{
			destClassName = "";
		}
		loadLockInformation( true );
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
			progressBar.setProgress( lockInformation.getDownloadPercent() );
			TextView text = (TextView)findViewById( R.id.textDownPercent );
			text.setText( getString( R.string.textDownloading , lockInformation.getDownloadPercent() ) );
		}
		else
		{
			ProgressBar progressBar = (ProgressBar)findViewById( R.id.progressBarPause );
			progressBar.setProgress( lockInformation.getDownloadPercent() );
			TextView text = (TextView)findViewById( R.id.textPausePercent );
			text.setText( getString( R.string.textPause , lockInformation.getDownloadPercent() ) );
		}
	}
	
	@Override
	protected void onDestroy()
	{
		unregisterReceiver( previewReceiver );
		downModule.dispose();
		if( galleryPreview.getAdapter() != null )
		{
			BaseAdapter adapter = (BaseAdapter)galleryPreview.getAdapter();
			if( adapter instanceof PreviewHotAdapter )
			{
				( (PreviewHotAdapter)adapter ).onDestory();
			}
			else if( adapter instanceof PreviewLocalAdapter )
			{
				( (PreviewLocalAdapter)adapter ).onDestory();
			}
		}
		super.onDestroy();
	}
	
	private void updateShowInfo()
	{
		TextView text = (TextView)findViewById( R.id.textAppName );
		text.setText( lockInformation.getDisplayName() );
		TextView viewAuthor = (TextView)findViewById( R.id.author );
		TextView viewInfo = (TextView)findViewById( R.id.info );
		if( !isShowMore )
		{
			viewAuthor.setVisibility( View.GONE );
			viewInfo.setVisibility( View.GONE );
			return;
		}
		else
		{
			viewAuthor.setVisibility( View.VISIBLE );
			viewInfo.setVisibility( View.VISIBLE );
		}
		String author = getString( R.string.previewLockSize , lockInformation.getApplicationSize() / 1024 , lockInformation.getAuthor( mContext ) );
		viewAuthor.setText( author );
		String systemLauncher = Locale.getDefault().getLanguage().toString();
		String info = "";
		if( systemLauncher.equals( "zh" ) )
		{
			info = getString( R.string.previewIntroduction ) + "\n" + lockInformation.getIntroduction();
		}
		else
		{
			String introduction = lockInformation.getIntroduction_en();
			if( introduction.equals( "" ) )
			{
				introduction = lockInformation.getIntroduction();
			}
			info = getString( R.string.previewIntroduction ) + "\n" + introduction;
		}
		viewInfo.setText( info );
	}
	
	private void updateInforButton()
	{
		findViewById( R.id.btnDelete ).setVisibility( View.GONE );
		findViewById( R.id.btnUninstall ).setVisibility( View.GONE );
		if( lockInformation.isInstalled( this ) )
		{
			if( lockInformation.isSystem() )
			{
				return;
			}
			findViewById( R.id.btnUninstall ).setVisibility( View.VISIBLE );
			return;
		}
		if( lockInformation.isDownloaded( mContext ) && lockInformation.getDownloadStatus() != DownloadStatus.StatusDownloading )
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
		if( lockInformation.isInstalled( this ) )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
			if( lockInformation.isSettingExist() )
			{
				findViewById( R.id.btnSetting ).setVisibility( View.VISIBLE );
			}
			if( isShowShare )
				findViewById( R.id.btnShare ).setVisibility( View.VISIBLE );
			else
				findViewById( R.id.btnShare ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
			return;
		}
		if( !lockInformation.isDownloaded( mContext ) )
		{
			boolean ispay = Tools.isContentPurchased( this , DownloadList.Lock_Type , packageName );
			if( FunctionConfig.isPriceVisible() && lockInformation.getPrice() > 0 && !ispay )
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
		if( lockInformation.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
			return;
		}
		relativeDownload.setClickable( true );
		if( lockInformation.getDownloadStatus() == DownloadStatus.StatusDownloading )
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
	
	private void loadLockInformation(
			boolean reloadGallery )
	{
		LockManager mgr = new LockManager( this );
		lockInformation = mgr.queryLock( packageName , destClassName );
		if( lockInformation.isInstalled( this ) )
		{
			Context dstContext = null;
			try
			{
				dstContext = createPackageContext( packageName , Context.CONTEXT_IGNORE_SECURITY );
			}
			catch( NameNotFoundException e )
			{
				e.printStackTrace();
				return;
			}
			ContentConfig destContent = new ContentConfig();
			destContent.loadConfig( dstContext , destClassName );
			lockInformation.loadInstallDetail( dstContext , destContent );
			if( reloadGallery )
			{
				galleryPreview.setAdapter( new PreviewLocalAdapter( this , destContent , dstContext ) );
			}
			return;
		}
		if( reloadGallery )
		{
			galleryPreview.setAdapter( new PreviewHotAdapter( this , packageName , downModule ) );
		}
	}
	
	private String queryClassName(
			String pkgName )
	{
		LockManager mgr = new LockManager( this );
		ComponentName comName = mgr.queryComponent( packageName );
		if( comName == null )
		{
			return "";
		}
		return comName.getClassName();
	}
	
	private BroadcastReceiver previewReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String actionName = intent.getAction();
			if( actionName.equals( StaticClass.ACTION_PREVIEW_CHANGED ) )
			{
				SpinnerAdapter apt = galleryPreview.getAdapter();
				if( apt != null && apt instanceof PreviewHotAdapter )
				{
					( (PreviewHotAdapter)apt ).reload();
					scrollGallery.setMax( galleryPreview.getCount() );
					scrollGallery.setProgress( galleryPreview.getSelectedItemPosition() );
				}
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
			{
				// ***********
				String name = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
				if( name.equals( packageName ) )
				{
					loadLockInformation( false );
					updateShowStatus();
				}
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED ) )
			{
				if( packageName.equals( intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME ) ) )
				{
					lockInformation.setDownloadSize( intent.getIntExtra( StaticClass.EXTRA_DOWNLOAD_SIZE , 0 ) );
					lockInformation.setTotalSize( intent.getIntExtra( StaticClass.EXTRA_TOTAL_SIZE , 0 ) );
					updateProgressSize();
				}
			}
			else if( Intent.ACTION_PACKAGE_REMOVED.equals( actionName ) )
			{
				String actionPkgName = intent.getData().getSchemeSpecificPart();
				if( actionPkgName.equals( packageName ) )
				{
					finish();
				}
			}
			else if( Intent.ACTION_PACKAGE_ADDED.equals( actionName ) )
			{
				String actionPkgName = intent.getData().getSchemeSpecificPart();
				if( actionPkgName.equals( packageName ) )
				{
					destClassName = queryClassName( packageName );
					loadLockInformation( true );
					updateShowInfo();
					updateShowStatus();
				}
			}
			else if( StaticClass.ACTION_DOWNLOAD_ERROR.equals( actionName ) )
			{
				Toast.makeText( context , context.getString( R.string.server_download_fail ) , Toast.LENGTH_SHORT ).show();
				Intent it = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
				it.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				if( lockInformation != null )
					intent.putExtra( "apkname" , lockInformation.getDisplayName() );
				else
					intent.putExtra( "apkname" , packageName );
				sendBroadcast( it );
				DownloadThemeService dSv = new DownloadThemeService( PreviewHotActivity.this );
				dSv.deleteItem( packageName , DownloadList.Lock_Type );
				new File( PathTool.getDownloadingApp( packageName ) ).delete();
				it = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
				it.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
				sendBroadcast( it );
				updateShowStatus();
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
		intent.putExtra( Intent.EXTRA_TEXT , getString( R.string.shareText , lockInformation.getDisplayName() ) );
		if( saveThumb() )
		{
			intent.putExtra( Intent.EXTRA_STREAM , Uri.fromFile( new File( PathTool.getThumbTempFile() ) ) );
		}
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
	
}
