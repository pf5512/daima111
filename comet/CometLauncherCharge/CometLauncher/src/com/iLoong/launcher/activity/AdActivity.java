package com.iLoong.launcher.activity;

import com.iLoong.RR;

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
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( RR.layout.adactivity );
		
		LinearLayout layoutIndicator = (LinearLayout)findViewById( RR.id.adlayoutIndicator );
		btndownload=(Button)findViewById( RR.id.adlvbtndownload );
//		calbtn=(Button)findViewById( RR.id.adcalbtn );
		previewPager=(ViewPager)findViewById( RR.id.adviewPager );
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
					Toast.makeText( getApplicationContext() , RR.string.download_toast , Toast.LENGTH_SHORT ).show();
				}
			}
		} );
		final Advertisement preAdapter = new Advertisement( this );
		galleryIndicator = new GalleryIndicator( layoutIndicator , 8 );
		previewPager.setAdapter( preAdapter );
		previewPager.setCurrentItem( 4*100 );
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

