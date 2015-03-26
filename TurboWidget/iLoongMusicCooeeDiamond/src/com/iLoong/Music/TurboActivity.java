package com.iLoong.Music;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;


public class TurboActivity extends Activity
{
	
	private static final String TAG = "CometActivity";
	private GalleryIndicator galleryIndicator;
	private ViewPager previewPager;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_turbo );
		LinearLayout layoutIndicator = (LinearLayout)findViewById( R.id.layoutIndicator );
		Button btnPlay = (Button)findViewById( R.id.btnGooglePlay );
		btnPlay.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				Uri playUri = Uri.parse( "https://play.google.com/store/apps/details?id=com.cooeeui.turbolauncher&referrer=utm_source%3Dtheme%26utm_medium%3Dapk" );
				Intent browserIntent = new Intent( Intent.ACTION_VIEW , playUri );
				if( isPlayStoreInstalled() )
				{
					browserIntent.setClassName( "com.android.vending" , "com.android.vending.AssetBrowserActivity" );
				}
				browserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivity( browserIntent );
				finish();
			}
		} );
		previewPager = (ViewPager)findViewById( R.id.viewPager );
		PreviewAdapter preAdapter = new PreviewAdapter( this );
		galleryIndicator = new GalleryIndicator( layoutIndicator , preAdapter.getCount() );
		previewPager.setAdapter( preAdapter );
		previewPager.setCurrentItem( 0 );
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
	}
	
	private final int PREVIEW_TICK = 5000;
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
				previewPager.setCurrentItem( 0 , true );
			}
			previewPager.removeCallbacks( this );
			previewPager.postDelayed( this , PREVIEW_TICK );
		}
	};
	
	private boolean isPlayStoreInstalled()
	{
		String playPkgName = "com.android.vending";
		try
		{
			PackageInfo pckInfo = this.getPackageManager().getPackageInfo( playPkgName , PackageManager.GET_ACTIVITIES );
			Log.d( TAG , pckInfo.packageName + " installed" );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			Log.d( TAG , "play market not installed" );
			return false;
		}
	}
}
