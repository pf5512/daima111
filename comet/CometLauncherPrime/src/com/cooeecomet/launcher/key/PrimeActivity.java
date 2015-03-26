package com.cooeecomet.launcher.key;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;


public class PrimeActivity extends Activity
{
	
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6qW+Zab7gorWrhXYG6bad7V3PRtXZFTB2OjQv25s30pK1tt8xFvKxlP7oVXu5/b2h/bxzvbcDWmCLpw1YU0NxMXcLFDNVu2hS7bFJa3IYxXgOK6Wyn3WSsY5voKZijDc2Sd5tJA0rUqyKtypS9SkRMZHrQQlzpcEN14hFaUAI7intP4reFZ+IZ3lgfhzFWFVOuoNxexeVWwTGjo68kkK/Mvj89x+Y/D/Ff74qPDLDn4c5CDOlLXPwT4VkY1sK3tMviqZ3+97wbTCuHtULzRkDG7VI8OCy5/2sWFiZGeyT4BMXuwjjQn9H//cib4UfLgdfI3K0oKqJskJoYk0x3fl7QIDAQAB";
	// Generate your own 20 random bytes, and put them here.
	//private static final byte[] SALT = new byte[]{ -46 , 65 , 30 , -128 , -103 , -57 , 74 , -64 , 51 , 88 , -95 , -45 , 77 , -117 , -36 , -113 , -11 , 32 , -64 , 89 };
	private static final byte[] SALT = new byte[]{ -8 , -41 , -89 , 30 , -32 , -30 , 94 , 104 , -118 , 20 , 60 , 32 , 118 , -96 , -5 , 66 , -77 , 94 , -89 , -26 };
	private LicenseCheckerCallback mLicenseCheckerCallback;
	private LicenseChecker mChecker;
	// A handler on the UI thread.
	private ViewPager previewPager;
	private ImageButton ibTryNow;
	private ImageButton ibTryAgain;
	private ImageButton ibDownloadNow;
	private ImageView ivCheck;
	private final String CometPackageName = "com.cooeecomet.launcher";
	private TextView tvLicensing;
	private GalleryIndicator galleryIndicator;
	// A handler on the UI thread.
	private Handler mHandler;
	private final String TAG = "PrimeActivity";
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_prime );
		ibTryNow = (ImageButton)findViewById( R.id.ibTryNow );
		ibTryNow.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				EasyTracker tracker = EasyTracker.getInstance( PrimeActivity.this );
				tracker.send( MapBuilder.createEvent( "Button" , "TryNow" , "" , null ).build() );
				setResult( RESULT_OK );
				finish();
			}
		} );
		ibDownloadNow = (ImageButton)findViewById( R.id.ibDownloadNow );
		ibDownloadNow.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				EasyTracker tracker = EasyTracker.getInstance( PrimeActivity.this );
				tracker.send( MapBuilder.createEvent( "Button" , "DownloadNow" , "" , null ).build() );
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setPackage( "com.android.vending" );
				intent.setData( Uri.parse( "market://details?id=" + CometPackageName ) );
				startActivity( intent );
				finish();
			}
		} );
		ibTryAgain = (ImageButton)findViewById( R.id.ibTryAgain );
		ibTryAgain.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				EasyTracker tracker = EasyTracker.getInstance( PrimeActivity.this );
				tracker.send( MapBuilder.createEvent( "Button" , "TryAgain" , "" , null ).build() );
				doCheck();
			}
		} );
		ivCheck = (ImageView)findViewById( R.id.ivCheck );
		tvLicensing = (TextView)findViewById( R.id.tvLicensing );
		//ibExperience.setEnabled( false );
		previewPager = (ViewPager)findViewById( R.id.viewPager );
		PreviewAdapter preAdapter = new PreviewAdapter( this );
		LinearLayout layoutIndicator = (LinearLayout)findViewById( R.id.layoutIndicator );
		galleryIndicator = new GalleryIndicator( layoutIndicator , preAdapter.getOriginalCount() );
		previewPager.setAdapter( preAdapter );
		previewPager.setCurrentItem( preAdapter.getCount() / 2 );
		previewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(
					int position )
			{
				galleryIndicator.setFocus( position );
			}
		} );
		previewPager.postDelayed( nextPreview , 5000 );
		previewPager.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(
					View v ,
					MotionEvent event )
			{
				if( event.getAction() == MotionEvent.ACTION_DOWN )
				{
					previewPager.removeCallbacks( nextPreview );
				}
				else if( event.getAction() == MotionEvent.ACTION_UP )
				{
					previewPager.postDelayed( nextPreview , PREVIEW_TICK );
				}
				return false;
			}
		} );
		//check
		mHandler = new Handler();
		// Try to use more data here. ANDROID_ID is a single point of attack.
		String deviceId = Secure.getString( getContentResolver() , Secure.ANDROID_ID );
		// Library calls this when it's done.
		mLicenseCheckerCallback = new MyLicenseCheckerCallback();
		// Construct the LicenseChecker with a policy.
		mChecker = new LicenseChecker( this , new ServerManagedPolicy( this , new AESObfuscator( SALT , getPackageName() , deviceId ) ) , BASE64_PUBLIC_KEY );
		doCheck();
		//
		GAServiceManager.getInstance().setLocalDispatchPeriod( 2 );
		EasyTracker tracker = EasyTracker.getInstance( PrimeActivity.this );
		tracker.send( MapBuilder.createEvent( "Activity" , "onCreate" , "" , null ).build() );
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mChecker.onDestroy();
	}
	
	private boolean isPackageInstalled(
			String pkgName )
	{
		try
		{
			PackageInfo pi = getPackageManager().getPackageInfo( pkgName , PackageManager.GET_ACTIVITIES );
			Log.d( TAG , pi.packageName );
			return true;
		}
		catch( NameNotFoundException e )
		{
			return false;
		}
	}
	
	private void checkSucceed()
	{
		hideAllButtons();
		if( isPackageInstalled( CometPackageName ) )
		{
			ibTryNow.setVisibility( View.VISIBLE );
			tvLicensing.setText( R.string.licensing_succeed );
		}
		else
		{
			ibDownloadNow.setVisibility( View.VISIBLE );
			tvLicensing.setText( R.string.licensing_download );
		}
	}
	
	private void hideAllButtons()
	{
		ibTryAgain.setVisibility( View.GONE );
		ibTryNow.setVisibility( View.GONE );
		ivCheck.setVisibility( View.GONE );
		ibDownloadNow.setVisibility( View.GONE );
	}
	
	private void doCheck()
	{
		hideAllButtons();
		ivCheck.setVisibility( View.VISIBLE );
		tvLicensing.setText( R.string.licensing_checking );
		mChecker.checkAccess( mLicenseCheckerCallback );
	}
	
	private class MyLicenseCheckerCallback implements LicenseCheckerCallback
	{
		
		public void allow(
				int policyReason )
		{
			if( isFinishing() )
			{
				// Don't update UI if Activity is finishing.
				return;
			}
			Log.d( TAG , "Callback.allow=" + policyReason );
			// Should allow user access.
			mHandler.post( new Runnable() {
				
				@Override
				public void run()
				{
					checkSucceed();
				}
			} );
		}
		
		public void dontAllow(
				int policyReason )
		{
			if( isFinishing() )
			{
				// Don't update UI if Activity is finishing.
				return;
			}
			Log.d( TAG , "Callback.dontAllow=" + policyReason );
			// Should not allow access. In most cases, the app should assume
			// the user has access unless it encounters this. If it does,
			// the app should inform the user of their unlicensed ways
			// and then either shut down the app or limit the user to a
			// restricted set of features.
			// In this example, we show a dialog that takes the user to Market.
			// If the reason for the lack of license is that the service is
			// unavailable or there is another problem, we display a
			// retry button on the dialog and a different message.
			final String strReason = Integer.toString( policyReason );
			mHandler.post( new Runnable() {
				
				@Override
				public void run()
				{
					EasyTracker tracker = EasyTracker.getInstance( PrimeActivity.this );
					tracker.send( MapBuilder.createEvent( "CheckResult" , "dontAllow" + strReason , "dontAllow" , null ).build() );
					hideAllButtons();
					ibTryAgain.setVisibility( View.VISIBLE );
					tvLicensing.setText( R.string.licensing_failed );
				}
			} );
		}
		
		public void applicationError(
				int errorCode )
		{
			if( isFinishing() )
			{
				// Don't update UI if Activity is finishing.
				return;
			}
			Log.d( TAG , "Callback.applicationError=" + errorCode );
			// This is a polite way of saying the developer made a mistake
			// while setting up or calling the license checker library.
			// Please examine the error code and fix the error.
			final String strError = Integer.toString( errorCode );
			mHandler.post( new Runnable() {
				
				@Override
				public void run()
				{
					EasyTracker tracker = EasyTracker.getInstance( PrimeActivity.this );
					tracker.send( MapBuilder.createEvent( "CheckResult" , "applicationError" + strError , "applicationError" , null ).build() );
					hideAllButtons();
					ibTryAgain.setVisibility( View.VISIBLE );
					tvLicensing.setText( R.string.licensing_failed );
				}
			} );
		}
	}
	
	private final int PREVIEW_TICK = 3000;
	private Runnable nextPreview = new Runnable() {
		
		@Override
		public void run()
		{
			int curIndex = previewPager.getCurrentItem();
			if( curIndex < previewPager.getAdapter().getCount() - 1 )
			{
				previewPager.setCurrentItem( curIndex + 1 , true );
			}
			else
			{
				curIndex = previewPager.getAdapter().getCount() / 2;
				previewPager.setCurrentItem( curIndex , false );
			}
			previewPager.removeCallbacks( this );
			previewPager.postDelayed( this , PREVIEW_TICK );
		}
	};
	
	@Override
	public void onStart()
	{
		super.onStart();
		EasyTracker.getInstance( this ).activityStart( this );
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		EasyTracker.getInstance( this ).activityStop( this );
	}
}
