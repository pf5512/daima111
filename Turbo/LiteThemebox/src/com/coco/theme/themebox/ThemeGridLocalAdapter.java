package com.coco.theme.themebox;


import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;


public class ThemeGridLocalAdapter extends BaseAdapter
{
	
	private List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
	private Context context;
	private Bitmap imgDefaultThumb;
	private DownModule downThumb;
	private ComponentName currentTheme = null;
	private PageTask pageTask = null;
	private LruCache<String , Bitmap> mMemoryCache;
	
	public ThemeGridLocalAdapter(
			Context cxt ,
			DownModule down )
	{
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
		int maxMemory = (int)Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 16;
		mMemoryCache = new LruCache<String , Bitmap>( cacheSize );
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
	}
	
	private List<ThemeInformation> queryPackage()
	{
		// packageNameSet.clear();
		// localList.clear();
		List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
		ThemeService themeSv = new ThemeService( context );
		List<ThemeInformation> installList = themeSv.queryInstallList();
		for( ThemeInformation info : installList )
		{
			info.setThumbImage( context , info.getPackageName() , info.getClassName() );
			addBitmapToMemoryCache( info.getPackageName() , info.getThumbImage() );
			if( info.getThumbImage() == null )
				new PageItemTask().execute( info );
			localList.add( info );
			// packageNameSet.add(info.getPackageName());
		}
		currentTheme = themeSv.queryCurrentTheme();
		return localList;
	}
	
	public void addBitmapToMemoryCache(
			String key ,
			Bitmap bitmap )
	{
		if( bitmap == null )
		{
			return;
		}
		if( getBitmapFromMemCache( key ) != null )
		{
			mMemoryCache.remove( key );
		}
		mMemoryCache.put( key , bitmap );
	}
	
	public Bitmap getBitmapFromMemCache(
			String key )
	{
		return mMemoryCache.get( key );
	}
	
	public void onDestory()
	{
		for( ThemeInformation info : localList )
		{
			info.disposeThumb();
			info = null;
		}
		if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
		{
			imgDefaultThumb.recycle();
		}
	}
	
	public void reloadCurrent()
	{
		ThemeService themeSv = new ThemeService( context );
		currentTheme = themeSv.queryCurrentTheme();
		notifyDataSetChanged();
	}
	
	public void reloadPackage()
	{
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
	}
	
	public boolean containPackage(
			String packageName )
	{
		return findPackageIndex( packageName ) >= 0;
	}
	
	private int findPackageIndex(
			String packageName )
	{
		int i = 0;
		for( i = 0 ; i < localList.size() ; i++ )
		{
			if( packageName.equals( localList.get( i ).getPackageName() ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int getCount()
	{
		return localList.size();
	}
	
	@Override
	public Object getItem(
			int position )
	{
		// Log.e("test", "PageItemTask getItem:" + position);
		return localList.get( position );
	}
	
	@Override
	public long getItemId(
			int position )
	{
		return position;
	}
	
	public class ViewHolder
	{
		
		ImageView viewThumb;
		TextView viewName;
		ImageView imageCover;
		ImageView imageUsed;
		ProgressBar barPause;
		ProgressBar barDownloading;
	}
	
	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(
				Message msg )
		{
			// TODO Auto-generated method stub
			if( msg.what == 0 )
			{
				notifyDataSetChanged();
			}
			super.handleMessage( msg );
		}
	};
	
	@Override
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		ViewHolder viewHolder = null;
		Log.v( "test" , "PageItemTask: getView position:" + position + " convertView:" + convertView );
		if( convertView != null )
		{
			viewHolder = (ViewHolder)convertView.getTag();
		}
		else
		{
			viewHolder = new ViewHolder();
			convertView = View.inflate( context , R.layout.grid_item , null );
			viewHolder.viewName = (TextView)convertView.findViewById( R.id.textAppName );
			viewHolder.viewThumb = (ImageView)convertView.findViewById( R.id.imageThumb );
			viewHolder.imageCover = (ImageView)convertView.findViewById( R.id.imageCover );
			viewHolder.imageUsed = (ImageView)convertView.findViewById( R.id.imageUsed );
			viewHolder.barPause = (ProgressBar)convertView.findViewById( R.id.barPause );
			viewHolder.barDownloading = (ProgressBar)convertView.findViewById( R.id.barDownloading );
			int itemWidth = (int)( context.getResources().getDisplayMetrics().widthPixels / 3 - 6 * 2 * context.getResources().getDisplayMetrics().density );
			viewHolder.viewThumb.setLayoutParams( new RelativeLayout.LayoutParams(
					(int)( itemWidth + 6 * 2 * context.getResources().getDisplayMetrics().density ) ,
					(int)( itemWidth / 0.6f + 6 * 2 * context.getResources().getDisplayMetrics().density ) ) );
		}
		final ThemeInformation themeInfo = (ThemeInformation)getItem( position );
		Bitmap imgThumb = getBitmapFromMemCache( themeInfo.getPackageName() );//themeInfo.getThumbImage();
		//		recycle.add( viewHolder.viewThumb );
		//		Tools.Recyclebitmap( imgDefaultThumb , imgThumb , viewHolder.viewThumb , recycle );
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
			//			new Thread( new Runnable() {
			//				
			//				@Override
			//				public void run()
			//				{
			//					// TODO Auto-generated method stub
			//					if( themeInfo.isNeedLoadDetail() )
			//					{
			//						Bitmap imgThumb = themeInfo.getThumbImage();
			//						if( imgThumb == null )
			//						{
			//							themeInfo.loadDetail( context );
			//							if( themeInfo.getThumbImage() != null )
			//							{
			//								StaticClass.saveMyBitmap( context , themeInfo.getPackageName() , themeInfo.getClassName() , themeInfo.getThumbImage() );
			//							}
			//							addBitmapToMemoryCache( themeInfo.getPackageName() , themeInfo.getThumbImage() );
			//							handler.sendEmptyMessage( 0 );
			//						}
			//					}
			//				}
			//			} ).start();
		}
		viewHolder.viewThumb.setImageBitmap( imgThumb );
		String title = "";
		String[] tiltles = themeInfo.getDisplayName().split( "_" );
		if( tiltles.length == 2 )
		{
			if( !tiltles[0].contains( "Turbo" ) && tiltles[1].contains( "Turbo" ) )
			{
				title = tiltles[0];
			}
			else if( !tiltles[1].contains( "Turbo" ) && tiltles[0].contains( "Turbo" ) )
			{
				title = tiltles[1];
			}
			else
			{
				title = tiltles[1];
			}
		}
		else
		{
			title = themeInfo.getDisplayName();
		}
		viewHolder.viewName.setText( title );
		viewHolder.barPause.setVisibility( View.VISIBLE );
		if( currentTheme != null && currentTheme.getPackageName().equals( themeInfo.getPackageName() ) && currentTheme.getClassName().equals( themeInfo.getClassName() ) )
		{
			viewHolder.imageCover.setVisibility( View.VISIBLE );
			viewHolder.imageUsed.setVisibility( View.VISIBLE );
		}
		else
		{
			viewHolder.imageCover.setVisibility( View.INVISIBLE );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
		}
		if( themeInfo.isInstalled( context ) || themeInfo.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			viewHolder.barPause.setVisibility( View.INVISIBLE );
			viewHolder.barDownloading.setVisibility( View.INVISIBLE );
		}
		else
		{
			viewHolder.imageCover.setVisibility( View.VISIBLE );
			if( themeInfo.getDownloadStatus() == DownloadStatus.StatusDownloading )
			{
				viewHolder.barDownloading.setVisibility( View.VISIBLE );
				viewHolder.barPause.setVisibility( View.INVISIBLE );
				viewHolder.barDownloading.setProgress( themeInfo.getDownloadPercent() );
			}
			else
			{
				viewHolder.barDownloading.setVisibility( View.INVISIBLE );
				viewHolder.barPause.setVisibility( View.VISIBLE );
				viewHolder.barPause.setProgress( themeInfo.getDownloadPercent() );
			}
		}
		convertView.setTag( viewHolder );
		return convertView;
	}
	
	public class PageItemTask extends AsyncTask<ThemeInformation , Integer , ThemeInformation>
	{
		
		public PageItemTask()
		{
			// Log.e("test", "PageItemTask");
		}
		
		@Override
		protected void onPostExecute(
				ThemeInformation themeInfo )
		{
			// TODO Auto-generated method stub
			notifyDataSetChanged();
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected ThemeInformation doInBackground(
				ThemeInformation ... params )
		{
			// TODO Auto-generated method stub
			//			for( int i = 0 ; i < getCount() ; i++ )
			{
				ThemeInformation themeInfo = params[0];
				getItemThumb( themeInfo );
			}
			return null;
		}
		
		private void getItemThumb(
				ThemeInformation themeInfo )
		{
			if( themeInfo.getThumbImage() == null )
			{
				themeInfo.setThumbImage( context , themeInfo.getPackageName() , themeInfo.getClassName() );
			}
			if( themeInfo.isNeedLoadDetail() )
			{
				Bitmap imgThumb = themeInfo.getThumbImage();
				if( imgThumb == null )
				{
					themeInfo.loadDetail( context );
					if( themeInfo.getThumbImage() != null )
					{
						StaticClass.saveMyBitmap( context , themeInfo.getPackageName() , themeInfo.getClassName() , themeInfo.getThumbImage() );
					}
				}
				if( themeInfo.getThumbImage() == null )
				{
					downThumb.downloadThumb( themeInfo.getPackageName() , DownloadList.Theme_Type );
				}
			}
			addBitmapToMemoryCache( themeInfo.getPackageName() , themeInfo.getThumbImage() );
		}
	}
	
	public class PageTask extends AsyncTask<String , Integer , List<ThemeInformation>>
	{
		
		public PageTask()
		{
		}
		
		@Override
		protected void onPostExecute(
				List<ThemeInformation> result )
		{
			// TODO Auto-generated method stub
			if( result == null )
			{
				return;
			}
			for( ThemeInformation info : localList )
			{
				info.disposeThumb();
				info = null;
			}
			localList.clear();
			localList.addAll( result );
			notifyDataSetChanged();
			pageTask = null;
			//			if( itemTask != null && itemTask.getStatus() != PageItemTask.Status.FINISHED )
			//			{
			//				itemTask.cancel( true );
			//			}
			//			itemTask = (PageItemTask)new PageItemTask().execute();
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected List<ThemeInformation> doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			List<ThemeInformation> result = queryPackage();
			return result;
		}
	}
}
