package com.coco.widget.widgetbox;


import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Handler;
import android.os.Message;
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
import com.coco.theme.themebox.util.PathTool;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class WidgetPreviewHotActivity extends Activity
{
	
	private final String LOG_TAG = "PreviewHotActivity";
	private ScrollView previewScroll;
	private DownModule downModule;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private WidgetInformation Information;
	private SeekBar scrollGallery;
	private PreViewGallery galleryPreview;
	private String packageName;
	private String destClassName;
	private Context mContext; // xiatian add //Statistics
	private static final String APPID = "300002916383";
	private static final String APPKEY = "6F866C05CFF72AE3";
	private String LEASE_PAYCODE = null;
	private ProgressDialog dialog;
	private static final int WIDGET_INDICATION = 3;
	
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
		loadWidgetInformation( true );
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
					scrollGallery.setProgress( position );
					scrollGallery.setMax( parent.getAdapter().getCount() - 1 );
				}
				
				@Override
				public void onNothingSelected(
						AdapterView<?> parent )
				{
					Log.d( LOG_TAG , "galleryPreview,onNothingSelected" );
					scrollGallery.setProgress( 0 );
					scrollGallery.setMax( parent.getAdapter().getCount() - 1 );
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
		btnMore.setVisibility( View.GONE );
		// 应用按钮；支持添加到桌面，则显示为应用，反之，小组件不显示应用，显示为删除按钮，删除应用
		Button btnApply = (Button)findViewById( R.id.btnApply );
		btnApply.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( FunctionConfig.isEnable_add_widget() )
				{
					Intent it = new Intent( "com.coco.launcher.add.widget" );
					it.putExtra( "packageName" , packageName );
					sendBroadcast( it );
					ActivityManager.KillActivity();
				}
				else
				{
					//MobclickAgent.onEvent( mContext , "BoxUninstallWidget" , packageName );
					removePackage();
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
				downModule.installApk( packageName , DownloadList.Widget_Type );
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
				else if( !com.coco.theme.themebox.StaticClass.isAllowDownloadWithToast( WidgetPreviewHotActivity.this ) )
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
				//MobclickAgent.onEvent( mContext , "BoxDownloadWidget" , packageName );
				DownloadThemeService dSv = new DownloadThemeService( WidgetPreviewHotActivity.this );
				DownloadThemeItem dItem = dSv.queryByPackageName( packageName , DownloadList.Widget_Type );
				if( dItem == null )
				{
					dItem = new DownloadThemeItem();
					dItem.copyFromThemeInfo( Information.getInfoItem() );
					dItem.setDownloadStatus( DownloadStatus.StatusDownloading );
					dSv.insertItem( dItem );
				}
				loadWidgetInformation( false );
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
				//				// TODO Auto-generated method stub
				//				CooeePaymentInfo paymentInfo = new CooeePaymentInfo();
				//				paymentInfo.setPrice( Information.getPrice() );
				//				paymentInfo.setPayDesc( Information.getDisplayName() );
				//				String payid = Information.getPricePoint();
				//				Log.v( "themebox" , "payid widget = " + payid );
				//				if( payid == null || payid.equals( "" ) || payid.equals( "null" ) )
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
				//				CooeePayment.getInstance().startPayService( WidgetPreviewHotActivity.this , paymentInfo );
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
				DownloadThemeService dSv = new DownloadThemeService( WidgetPreviewHotActivity.this );
				dSv.deleteItem( packageName , DownloadList.Widget_Type );
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
		loadWidgetInformation( true );
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
			if( adapter instanceof WidgetPreviewLocalAdapter )
			{
				( (WidgetPreviewLocalAdapter)adapter ).onDestory();
			}
			else if( adapter instanceof WidgetPreviewHotAdapter )
			{
				( (WidgetPreviewHotAdapter)adapter ).onDestory();
			}
		}
		super.onDestroy();
	}
	
	private void updateShowInfo()
	{
		TextView text = (TextView)findViewById( R.id.textAppName );
		text.setText( Information.getDisplayName() );
		TextView viewAuthor = (TextView)findViewById( R.id.author );
		TextView viewInfo = (TextView)findViewById( R.id.info );
		if( true/* !FunctionConfig.isThemeMoreShow() */)
		{// 小组件不显示更多的信息
			viewAuthor.setVisibility( View.GONE );
			viewInfo.setVisibility( View.GONE );
		}
		else
		{
			viewAuthor.setVisibility( View.VISIBLE );
			viewInfo.setVisibility( View.VISIBLE );
			String author = getString( R.string.previewSize , Information.getApplicationSize() / 1024 , Information.getAuthor( mContext ) );
			viewAuthor.setText( author );
			if( FunctionConfig.isIntroductionVisible() )
			{
				String info = null;
				if( FunctionConfig.isPriceVisible() && Information.getPrice() > 0 )
				{
					info = getString( R.string.previewIntroduction ) + "\n" + Information.getIntroduction() + "\n" + getString( R.string.theme_price , Information.getPrice() / 100 );
				}
				else
				{
					info = getString( R.string.previewIntroduction ) + "\n" + Information.getIntroduction();
				}
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
		if( Information.isInstalled( this ) )
		{
			if( Information.isSystem() )
			{
				return;
			}
			if( FunctionConfig.isEnable_add_widget() )
			{
				findViewById( R.id.btnUninstall ).setVisibility( View.VISIBLE );
			}
			else
			{
				findViewById( R.id.btnUninstall ).setVisibility( View.GONE );
			}
			return;
		}
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
		if( Information.isInstalled( this ) )
		{
			relativeDownload.setVisibility( View.GONE );
			relativeNormal.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
			Button btn = (Button)relativeNormal.findViewById( R.id.btnApply );
			if( FunctionConfig.isEnable_add_widget() )
			{
				btn.setText( R.string.btnApply );
			}
			else
			{
				btn.setText( R.string.btnUninstall );
			}
			btn.setVisibility( View.VISIBLE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
			findViewById( R.id.btnShare ).setVisibility( View.GONE );
			return;
		}
		if( !Information.isDownloaded( this ) )
		{
			boolean ispay = Tools.isContentPurchased( this , DownloadList.Widget_Type , packageName );
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
			relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.GONE );
			relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.VISIBLE );
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
	
	private void loadWidgetInformation(
			boolean reloadGallery )
	{
		WidgetService service = new WidgetService( this );
		Information = service.queryWidget( packageName , destClassName );
		if( Information.isInstalled( this ) )
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
			Information.loadInstallDetail( dstContext , destContent );
			if( reloadGallery )
			{
				galleryPreview.setAdapter( new WidgetPreviewLocalAdapter( this , packageName , dstContext ) );
			}
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
			int count = pref.getInt( "widgetIndication" , 0 );
			if( count < 3 && pref.getInt( "firstWidgetLocal" , 0 ) != 1 )
			{
				pref.edit().putInt( "widgetIndication" , count + 1 ).commit();
				iapHandler.sendEmptyMessageDelayed( WIDGET_INDICATION , 1000 );
			}
			else if( pref.getInt( "firstWidgetLocal" , 0 ) == 1 )
			{
				pref.edit().putInt( "firstWidgetLocal" , 2 ).commit();
			}
			return;
		}
		if( reloadGallery )
		{
			galleryPreview.setAdapter( new WidgetPreviewHotAdapter( this , packageName , downModule ) );
		}
	}
	
	private String queryClassName(
			String pkgName )
	{
		WidgetService service = new WidgetService( this );
		ComponentName comName = service.queryComponent( pkgName );
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
			Log.d( LOG_TAG , "action=" + actionName );
			if( actionName.equals( StaticClass.ACTION_PREVIEW_CHANGED ) )
			{
				SpinnerAdapter apt = galleryPreview.getAdapter();
				if( apt != null && apt instanceof WidgetPreviewHotAdapter )
				{
					( (WidgetPreviewHotAdapter)apt ).reload();
					scrollGallery.setMax( galleryPreview.getCount() );
					scrollGallery.setProgress( galleryPreview.getSelectedItemPosition() );
				}
			}
			else if( actionName.equals( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED ) )
			{
				String name = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
				if( name.equals( packageName ) )
				{
					loadWidgetInformation( false );
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
					loadWidgetInformation( true );
					updateShowInfo();
					updateShowStatus();
					Log.v( "@@@@@@@@@@@@" , "11111111111111111" );
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
	
	private Handler iapHandler = new Handler() {
		
		@Override
		public void handleMessage(
				Message msg )
		{
			super.handleMessage( msg );
			int what = msg.what;
			switch( what )
			{
				case WIDGET_INDICATION:
					if( !FunctionConfig.isEnable_add_widget() )
						Toast.makeText( mContext , R.string.widget_local_click_toast , Toast.LENGTH_SHORT ).show();
					break;
				default:
					break;
			}
		}
	};
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
	//					Toast.makeText( WidgetPreviewHotActivity.this , "计费成功" , Toast.LENGTH_SHORT ).show();
	//					Tools.writePurchasedData( WidgetPreviewHotActivity.this , DownloadList.Widget_Type , packageName );
	//					// Message message = iapHandler.obtainMessage(BILLING_FINISH);
	//					// message.sendToTarget();
	//					Intent intent = new Intent( StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED );
	//					intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , packageName );
	//					mContext.sendBroadcast( intent );
	//					break;
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_FAIL:
	//					Toast.makeText( WidgetPreviewHotActivity.this , "计费失败" + paymentInfo.getVersionName() , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case CooeePaymentResultNotify.COOEE_PAYMENT_RESULT_CANCEL_BY_USER:
	//					Toast.makeText( WidgetPreviewHotActivity.this , "用户取消付费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 1:
	//					Toast.makeText( WidgetPreviewHotActivity.this , "配置免费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 2:
	//					Toast.makeText( WidgetPreviewHotActivity.this , "不需要重复计费" , Toast.LENGTH_SHORT ).show();
	//					break;
	//				case 3:
	//					Toast.makeText( WidgetPreviewHotActivity.this , "无可用指令" , Toast.LENGTH_SHORT ).show();
	//					break;
	//			}
	//		}
	//	}
}
