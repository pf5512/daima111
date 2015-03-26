package com.iLoong.Robot.Launcher;



import com.cooeeui.cometrobot.R;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MyCalender extends Activity
{
	
	private ImageButton btndownload;
	private ViewPager previewPager;
	private GalleryIndicator galleryIndicator;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.myrobot );
		LinearLayout layoutIndicator = (LinearLayout)findViewById( R.id.layoutIndicator );
		btndownload = (ImageButton)findViewById( R.id.btndownload );
		previewPager = (ViewPager)findViewById( R.id.viewPager );
		btndownload.setOnClickListener( new ImageButton.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( isHaveInternet() )
				{
					Uri playUri = Uri.parse( "https://play.google.com/store/apps/details?id=com.cooeecomet.launcher" );
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
		final PreviewAdapter preAdapter = new PreviewAdapter( this );
		galleryIndicator = new GalleryIndicator( layoutIndicator , 4 );
		previewPager.setAdapter( preAdapter );
		previewPager.setCurrentItem( 4 * 100 );
		previewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(
					int position )
			{
				galleryIndicator.setFocus( position % 4 );
			}
		} );
		//		previewPager.postDelayed( nextPreview , 3000 );
		//		previewPager.setOnTouchListener( new OnTouchListener() {
		//			
		//			@Override
		//			public boolean onTouch(
		//					View v ,
		//					MotionEvent event )
		//			{
		//				if( event.getAction() == MotionEvent.ACTION_DOWN )
		//				{
		//					previewPager.removeCallbacks( nextPreview );
		//				}
		//				else if( event.getAction() == MotionEvent.ACTION_UP )
		//				{
		//					previewPager.postDelayed( nextPreview , PREVIEW_TICK );
		//				}
		//				return false;
		//			}
		//		} );
	}
	
	//	private final int PREVIEW_TICK = 3000;
	//	private Runnable nextPreview = new Runnable() {
	//		@Override
	//		public void run()
	//		{
	//			
	//			try {
	//			    Field mScroller;
	//			    mScroller = ViewPager.class.getDeclaredField("mScroller");
	//			    mScroller.setAccessible(true); 
	//			    FixedSpeedScroller scroller = new FixedSpeedScroller(previewPager.getContext(), new AccelerateInterpolator());
	//			    mScroller.set(previewPager, scroller);
	//			} catch (NoSuchFieldException e) {
	//			} catch (IllegalArgumentException e) {
	//			} catch (IllegalAccessException e) {
	//			}
	//			
	//			int curIndex = previewPager.getCurrentItem();
	//			if( curIndex < previewPager.getAdapter().getCount() - 1 )
	//			{
	//				previewPager.setCurrentItem( curIndex + 1 , true );
	//			}
	//			else
	//			{
	//				previewPager.setCurrentItem( 0 , true );
	//			}
	//			previewPager.removeCallbacks( this );
	//			previewPager.postDelayed( this , PREVIEW_TICK );
	//		}
	//	};
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
// class FixedSpeedScroller extends Scroller {
//
//    private int mDuration = 500;
//
//    public FixedSpeedScroller(Context context) {
//        super(context);
//    }
//
//    public FixedSpeedScroller(Context context, Interpolator interpolator) {
//        super(context, interpolator);
//    }
//
//    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
//        super(context, interpolator, flywheel);
//    }
//
//
//    @Override
//    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
//        // Ignore received duration, use fixed one instead
//        super.startScroll(startX, startY, dx, dy, mDuration);
//    }
//
//    @Override
//    public void startScroll(int startX, int startY, int dx, int dy) {
//        // Ignore received duration, use fixed one instead
//        super.startScroll(startX, startY, dx, dy, mDuration);
//    }
//}
