package com.coco.theme.themebox.preview;


import android.R.integer;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.Log;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.PreViewGallery;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;


public class ThemePreviewHotActivity extends Activity
{
	
	private final String LOG_TAG = "PreviewHotActivity";
	private ThemeDownModule downModule;
	private ThemeInformation themeInformation;
	//	private PageControl pageControl = null;
	private ImageView pageControl;
	private PreViewGallery galleryPreview;
	private String packageName;
	private String destClassName;
	private String curPackageName;
	private String curClassName;
	private Context mContext;
	private ThemePreviewLocalAdapter themeLocalAdapter;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		ActivityManager.pushActivity( this );
		mContext = this;
		setContentView( R.layout.preview_hot_picture );
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics( dm );
		Bitmap bitmap1 = BitmapFactory.decodeResource( getResources() , R.drawable.comet_indicator );
		int width = bitmap1.getWidth();
		int height = bitmap1.getHeight();
		float scaleWidth = ( (float)dm.widthPixels / 3 ) / width;
		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth , 1 );
		Bitmap resizedBitmap = Bitmap.createBitmap( bitmap1 , 0 , 0 , width , height , matrix , true );
		bitmap1 = null;
		pageControl = (ImageView)findViewById( R.id.page_control );
		pageControl.setImageBitmap( resizedBitmap );
		resizedBitmap = null;
		//		pageControl = (PageControl) findViewById(R.id.page_control);
		//		pageControl.setPageCount(3,dm.widthPixels);
		//		pageControl.setCurrentPage(0);
		galleryPreview = (PreViewGallery)findViewById( R.id.galleryPreview );
		galleryPreview.setFullScreen( true );
		downModule = new ThemeDownModule( this );
		Intent intent = this.getIntent();
		packageName = intent.getStringExtra( StaticClass.EXTRA_PACKAGE_NAME );
		destClassName = intent.getStringExtra( StaticClass.EXTRA_CLASS_NAME );
		curPackageName = intent.getStringExtra( "current_theme_packagename" );
		curClassName = intent.getStringExtra( "current_theme_classname" );
		if( destClassName == null || destClassName.equals( "" ) )
		{
			destClassName = "";
		}
		loadThemeInformation( true );
		updateShowInfo();
		//		galleryPreview.setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		//			@Override
		//			public void onItemSelected(AdapterView<?> parent, View view,
		//					int position, long id) {
		//				Log.d(LOG_TAG, "galleryPreview,position=" + position);
		//
		//				pageControl.setCurrentPage(position);
		//			}
		//
		//			@Override
		//			public void onNothingSelected(AdapterView<?> parent) {
		//				Log.d(LOG_TAG, "galleryPreview,onNothingSelected");
		//				pageControl.setCurrentPage(0);
		//			}
		//
		//		});
		galleryPreview.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View view ,
					int position ,
					long id )
			{
				ThemeService service = new ThemeService( mContext );
				themeInformation = service.queryTheme( packageName , destClassName );
				boolean local = themeInformation.isInstalled();
				Intent i = new Intent();
				i.putExtra( "position" , position );
				i.putExtra( "local" , local );
				i.putExtra( "packname" , packageName );
				i.putExtra( "classname" , destClassName );
				i.setClass( mContext , ThemePreviewFullActivity.class );
				startActivity( i );
				overridePendingTransition( R.anim.enter_anim , 0 );
				//				if (fullScreen) {
				//					
				//				}else {
				//					setContentView(R.layout.preview_fullscreen);
				//					themeLocalAdapter.updatePreviewImage(true);
				////					galleryPreview.startAnimation(AnimationUtils.loadAnimation(mContext,
				////							R.anim.enter_anim));
				//					pageControl.setVisibility(View.INVISIBLE);
				//					((TextView) findViewById(R.id.textAppName)).setVisibility(View.INVISIBLE);
				//					((ImageButton)findViewById(R.id.buttonApply)).setVisibility(View.INVISIBLE);
				//					((ImageButton)findViewById(R.id.buttonUsed)).setVisibility(View.INVISIBLE);
				//					((ImageView)findViewById(R.id.watingView1)).setVisibility(View.INVISIBLE);
				//					((ImageView)findViewById(R.id.watingView2)).setVisibility(View.INVISIBLE);
				//				}
			}
		} );
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
		// 应用按钮
		ImageButton btnApply = (ImageButton)findViewById( R.id.buttonApply );
		btnApply.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				//友盟  应用统计
				MobclickAgent.onEvent( mContext , "Apply" , packageName + "-" + destClassName );
				ThemeService sv = new ThemeService( ThemePreviewHotActivity.this );
				sv.applyTheme( new ComponentName( packageName , destClassName ) );
				Toast.makeText( ThemePreviewHotActivity.this , getString( R.string.toastPreviewApply ) , Toast.LENGTH_SHORT ).show();
				sendBroadcast( new Intent( StaticClass.ACTION_DEFAULT_THEME_CHANGED ) );
				ActivityManager.KillActivity();
			}
		} );
	}
	
	private void loadThemeInformation(
			boolean reloadGallery )
	{
		ThemeService service = new ThemeService( this );
		themeInformation = service.queryTheme( packageName , destClassName );
		if( themeInformation.isInstalled() )
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
			Log.v( LOG_TAG , "2222222222222222destClassName = " + destClassName );
			ContentConfig destContent = new ContentConfig();
			destContent.loadConfig( dstContext , destClassName );
			themeInformation.loadInstallDetail( dstContext , destContent );
			if( reloadGallery )
			{
				themeLocalAdapter = new ThemePreviewLocalAdapter( this , destContent , dstContext , false );
				galleryPreview.setAdapter( themeLocalAdapter );
			}
			return;
		}
		if( reloadGallery )
		{
			galleryPreview.setAdapter( new ThemePreviewHotAdapter( this , packageName , downModule , false ) );
		}
	}
	
	@Override
	protected void onDestroy()
	{
		ActivityManager.popupActivity( this );
		unregisterReceiver( previewReceiver );
		downModule.dispose();
		super.onDestroy();
	}
	
	private void updateShowInfo()
	{
		TextView text = (TextView)findViewById( R.id.textAppName );
		text.setText( themeInformation.getDisplayName() );
		text.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				finish();
			}
		} );
		ImageButton buttonApply = (ImageButton)findViewById( R.id.buttonApply );
		ImageButton buttonUsed = (ImageButton)findViewById( R.id.buttonUsed );
		if( curPackageName.equals( packageName ) && curClassName.equals( destClassName ) )
		{
			buttonApply.setVisibility( View.INVISIBLE );
			buttonUsed.setVisibility( View.VISIBLE );
		}
		else
		{
			buttonApply.setVisibility( View.VISIBLE );
			buttonUsed.setVisibility( View.INVISIBLE );
		}
	}
	
	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition( 0 , 0 );
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
				if( apt != null && apt instanceof ThemePreviewHotAdapter )
				{
					( (ThemePreviewHotAdapter)apt ).reload();
					//					pageControl.setCurrentPage(galleryPreview
					//							.getSelectedItemPosition());
				}
			}
		}
	};
	
	@Override
	protected void onPause()
	{
		super.onPause();
		//友盟统计
		MobclickAgent.onPause( this );
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		//友盟统计
		MobclickAgent.onResume( this );
	}
	
	public void setPageControlPosition(
			int offsetX )
	{
		pageControl.layout( offsetX / 3 , pageControl.getTop() , pageControl.getRight() , pageControl.getBottom() );
		;
	}
}
