package com.coco.theme.themebox.preview;


import com.coco.theme.themebox.PageControl;
import com.coco.theme.themebox.PreViewGallery;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class ThemePreviewFullActivity extends Activity
{
	
	private PreViewGallery fullgalleryPreview;
	private ThemeDownModule downModule;
	
	//	private PageControl pageControl = null;
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.preview_fullscreen );
		fullgalleryPreview = (PreViewGallery)findViewById( R.id.fullgalleryPreview );
		fullgalleryPreview.setFullScreen( false );
		downModule = new ThemeDownModule( this );
		Intent intent = this.getIntent();
		int position = intent.getIntExtra( "position" , 0 );
		boolean local = intent.getBooleanExtra( "local" , false );
		String packname = intent.getStringExtra( "packname" );
		String destClassName = intent.getStringExtra( "classname" );
		//		DisplayMetrics dm = new DisplayMetrics();
		//        getWindowManager().getDefaultDisplay().getMetrics(dm);
		//		pageControl = (PageControl) findViewById(R.id.page_control);
		//		pageControl.setPageCount(3,dm.widthPixels);
		//		pageControl.setCurrentPage(position);
		if( local )
		{
			Context dstContext = null;
			try
			{
				dstContext = createPackageContext( packname , Context.CONTEXT_IGNORE_SECURITY );
			}
			catch( NameNotFoundException e )
			{
				e.printStackTrace();
				return;
			}
			ContentConfig destContent = new ContentConfig();
			destContent.loadConfig( dstContext , destClassName );
			fullgalleryPreview.setAdapter( new ThemePreviewLocalAdapter( this , destContent , dstContext , true ) );
		}
		else
		{
			fullgalleryPreview.setAdapter( new ThemePreviewHotAdapter( this , packname , downModule , true ) );
		}
		fullgalleryPreview.setSelection( position );
		fullgalleryPreview.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View view ,
					int position ,
					long id )
			{
				finish();
			}
		} );
		//		fullgalleryPreview
		//		.setOnItemSelectedListener(new OnItemSelectedListener() {
		//			@Override
		//			public void onItemSelected(AdapterView<?> parent,
		//					View view, int position, long id) {
		//				pageControl.setCurrentPage(position);
		//			}
		//
		//			@Override
		//			public void onNothingSelected(AdapterView<?> parent) {
		//				pageControl.setCurrentPage(0);
		//			}
		//		});
	}
	
	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition( 0 , R.anim.exit_anim );
	}
	
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
}
