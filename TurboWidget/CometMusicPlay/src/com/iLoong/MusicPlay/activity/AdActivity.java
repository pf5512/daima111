package com.iLoong.MusicPlay.activity;
import com.cooeeui.cometmusicplay.R;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


public class AdActivity extends Activity
{
	private Button btndownload;
//	private Button calbtn;
	private ViewPager previewPager;
	private GalleryIndicator galleryIndicator;
	private Context context;
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.adactivity );
		context=this;
		
		LinearLayout layoutIndicator = (LinearLayout)findViewById( R.id.adlayoutIndicator );
		btndownload=(Button)findViewById( R.id.adlvbtndownload );
//		calbtn=(Button)findViewById( R.id.adcalbtn );
		previewPager=(ViewPager)findViewById( R.id.adviewPager );
//		calbtn.setOnClickListener( new Button.OnClickListener() {
//			@Override
//			public void onClick(
//					View v )
//			{
//				finish();
//			}
//		} );
		
		btndownload.setOnClickListener( new Button.OnClickListener() {
			@Override
			public void onClick(
					View v )
			{
				GAServiceManager.getInstance().setLocalDispatchPeriod( 10 ); //表示延迟10秒提交 
				GoogleAnalytics ga = GoogleAnalytics.getInstance(context); //context使用comet launcher的context 
				Tracker tracker = ga.getTracker( "UA-48567460-2" ); //使用日历widget的property 
				tracker.send( MapBuilder.createEvent( "CometCalendar" , "ClickDownload" , "" , null ).build() ); 
				
				if(isHaveInternet()){
				Uri playUri = Uri.parse( "https://play.google.com/store/apps/details?id=com.cooeecomet.launcher.key" );
				Intent browserIntent = new Intent( Intent.ACTION_VIEW , playUri );
				if( isPlayStoreInstalled() )
				{
					browserIntent.setClassName( "com.android.vending" , "com.android.vending.AssetBrowserActivity" );
				}
				browserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				startActivity( browserIntent );
//				finish();
				}
				else
				{
					Toast.makeText( getApplicationContext() , R.string.download_toast , Toast.LENGTH_SHORT ).show();
				}
			}
		} );
		final Advertisement preAdapter = new Advertisement( this );
		galleryIndicator = new GalleryIndicator( layoutIndicator , 8 );
		previewPager.setAdapter( preAdapter );
		previewPager.setCurrentItem( 8*100 );
		previewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(
					int position )
			{
				galleryIndicator.setFocus( position% 8 );
			}
			
			
		} );
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
	private boolean isHaveInternet()
	{
		try
		{
			ConnectivityManager manger = (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );
			NetworkInfo info = manger.getActiveNetworkInfo();
			return( info != null && info.isConnected() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return false;
		}
	}
}

