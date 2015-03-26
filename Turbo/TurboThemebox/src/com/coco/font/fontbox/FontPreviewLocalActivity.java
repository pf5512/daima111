package com.coco.font.fontbox;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.PreViewGallery;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class FontPreviewLocalActivity extends Activity
{
	
	private final String LOG_TAG = "PreviewHotActivity";
	private ScrollView previewScroll;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private SeekBar scrollGallery;
	private PreViewGallery galleryPreview;
	int fontType;
	String fontName;
	String fontFile;
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		ActivityManager.pushActivity( this );
		setContentView( R.layout.preview_hot );
		dialog = new ProgressDialog( this );
		scrollGallery = (SeekBar)findViewById( R.id.scrollGallery );
		galleryPreview = (PreViewGallery)findViewById( R.id.galleryPreview );
		fontType = getIntent().getIntExtra( "font_type" , 1 );
		fontName = getIntent().getStringExtra( "font_name" );
		fontFile = getIntent().getStringExtra( "font_file" );
		if( fontName == null )
		{
			fontName = getString( R.string.defaultFont );
		}
		galleryPreview.setAdapter( new FontPreviewLocalAdapter( this , fontType ) );
		updateShowInfo();
		{
			scrollGallery.setThumbOffset( -2 );
			scrollGallery.setEnabled( false );
			scrollGallery.setVisibility( View.INVISIBLE );
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
		relativeNormal = (RelativeLayout)findViewById( R.id.layoutNormal );
		relativeDownload = (RelativeLayout)findViewById( R.id.layoutDownload );
		reLayoutScroll();
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
		btnShare.setVisibility( View.INVISIBLE );
		// 更多
		Button btnMore = (Button)findViewById( R.id.btnMore );
		btnMore.setVisibility( View.INVISIBLE );
		// 应用按钮
		Button btnApply = (Button)findViewById( R.id.btnApply );
		btnApply.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( FunctionConfig.isEnable_topwise_style() )
				{
					Intent intent = new Intent( "com.topwise.fontpath" );
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( FontPreviewLocalActivity.this );
					if( fontType == 1 )
					{
						intent.putExtra( "action" , "settings.default.font" );
						pref.edit().putString( "currentFont" , "settings.default.font" ).commit();
					}
					else
					{
						intent.putExtra( "action" , fontFile );
						pref.edit().putString( "currentFont" , fontFile ).commit();
					}
					sendBroadcast( intent );
				}
				else
				{
					Intent intent = new Intent( "com.cooee.font.type.ACTION" );
					intent.putExtra( "FONT_TYPE" , fontType );
					sendBroadcast( intent );
				}
			}
		} );
	}
	
	@Override
	protected void onDestroy()
	{
		ActivityManager.popupActivity( this );
		//		if (dialog!= null && dialog.isShowing()){
		//			dialog.dismiss();
		//		}
		if( galleryPreview.getAdapter() != null )
		{
			BaseAdapter adapter = (BaseAdapter)galleryPreview.getAdapter();
			if( adapter instanceof FontPreviewLocalAdapter )
			{
				( (FontPreviewLocalAdapter)adapter ).onDestory();
			}
		}
		super.onDestroy();
	}
	
	private void updateShowInfo()
	{
		TextView text = (TextView)findViewById( R.id.textAppName );
		text.setText( fontName );
		View view = findViewById( R.id.info_view );
		view.setVisibility( View.GONE );
	}
	
	private void updateInforButton()
	{
		findViewById( R.id.btnDelete ).setVisibility( View.GONE );
		findViewById( R.id.btnUninstall ).setVisibility( View.GONE );
	}
	
	private void updateShowStatus()
	{
		updateInforButton();
		findViewById( R.id.btnSetting ).setVisibility( View.GONE );
		findViewById( R.id.btnShare ).setVisibility( View.GONE );
		relativeDownload.setClickable( false );
		relativeDownload.setVisibility( View.GONE );
		relativeNormal.setVisibility( View.VISIBLE );
		relativeNormal.findViewById( R.id.btnDownload ).setVisibility( View.GONE );
		relativeNormal.findViewById( R.id.btnApply ).setVisibility( View.VISIBLE );
		relativeNormal.findViewById( R.id.btnInstall ).setVisibility( View.GONE );
		relativeNormal.findViewById( R.id.btnBuy ).setVisibility( View.GONE );
	}
	
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
	
	public class FontPreviewLocalAdapter extends BaseAdapter
	{
		
		// 定义Content
		private Context mContext;
		private int fontType;
		private List<Bitmap> previewImages = new ArrayList<Bitmap>();
		
		// 构造
		public FontPreviewLocalAdapter(
				Context cxt ,
				int font_type )
		{
			mContext = cxt;
			fontType = font_type;
			loadImage();
		}
		
		private void loadImage()
		{
			previewImages.clear();
			try
			{
				InputStream in = mContext.getAssets().open( "fonts/font_preview_" + fontType + ".png" );
				DisplayMetrics dis = mContext.getResources().getDisplayMetrics();
				Bitmap tmp = Tools.getPurgeableBitmap( in , dis.widthPixels / 2 , dis.heightPixels / 2 );
				previewImages.add( tmp );
			}
			catch( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch( OutOfMemoryError error )
			{
				error.printStackTrace();
			}
		}
		
		public void onDestory()
		{
			for( Bitmap bmp : previewImages )
			{
				if( bmp != null && !bmp.isRecycled() )
				{
					bmp.recycle();
					bmp = null;
				}
			}
		}
		
		@Override
		public int getCount()
		{
			return previewImages.size();
		}
		
		@Override
		public Object getItem(
				int position )
		{
			return position;
		}
		
		@Override
		public long getItemId(
				int position )
		{
			return position;
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			ImageView imageView = (ImageView)convertView;
			if( imageView == null )
			{
				imageView = new ImageView( mContext );
				imageView.setLayoutParams( new Gallery.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT ) );
				imageView.setScaleType( ImageView.ScaleType.FIT_CENTER );
			}
			imageView.setImageBitmap( previewImages.get( position ) );
			return imageView;
		}
	}
}
