package com.coco.lock2.lockbox;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.download.DownloadList;
import com.coco.lock2.lockbox.util.LockManager;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.util.DownModule;
import com.iLoong.base.themebox.R;


public class GridLocalAdapter extends BaseAdapter
{
	
	private List<LockInformation> localList = new ArrayList<LockInformation>();
	private Context context;
	private Bitmap imgDefaultThumb;
	private ComponentName currentLock;
	private DownModule downThumb;
	private Set<String> packageNameSet = new HashSet<String>();
	private PageTask pageTask = null;
	private LruCache<String , Bitmap> mMemoryCache;
	
	//	private PageItemTask itemTask = null;
	public GridLocalAdapter(
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
	
	private List<LockInformation> queryPackage()
	{
		// packageNameSet.clear();
		// localList.clear();
		List<LockInformation> localList = new ArrayList<LockInformation>();
		LockManager mgr = new LockManager( context );
		currentLock = mgr.queryCurrentLock();
		List<LockInformation> installList = mgr.queryInstallList();
		for( LockInformation infor : installList )
		{
			infor.setThumbImage( context , infor.getPackageName() , infor.getClassName() );
			addBitmapToMemoryCache( infor.getPackageName() , infor.getThumbImage() );
			if( infor.getThumbImage() == null )
				new PageItemTask().execute( infor );
			localList.add( infor );
			// packageNameSet.add(infor.getPackageName());
		}
		return localList;
	}
	
	public void onDestory()
	{
		for( LockInformation info : localList )
		{
			info.disposeThumb();
			info = null;
		}
		if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
		{
			imgDefaultThumb.recycle();
		}
	}
	
	public void reloadPackage()
	{
		// queryPackage();
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
	}
	
	public void updateThumb(
			String pkgName )
	{
		int findIndex = findPackageIndex( pkgName );
		if( findIndex < 0 )
		{
			return;
		}
		LockInformation info = localList.get( findIndex );
		info.reloadThumb();
		notifyDataSetChanged();
	}
	
	public void updateDownloadSize(
			String pkgName ,
			long downSize ,
			long totalSize )
	{
		int findIndex = findPackageIndex( pkgName );
		if( findIndex < 0 )
		{
			return;
		}
		LockInformation info = localList.get( findIndex );
		info.setDownloadSize( downSize );
		info.setTotalSize( totalSize );
		notifyDataSetChanged();
	}
	
	public Set<String> getPackageNameSet()
	{
		return packageNameSet;
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
		return localList.get( position );
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
		ViewHolder viewHolder = null;
		if( convertView != null )
		{
			viewHolder = (ViewHolder)convertView.getTag();
			viewHolder.viewName.setText( "" );
			viewHolder.viewThumb.setImageBitmap( imgDefaultThumb );
			viewHolder.imageCover.setVisibility( View.INVISIBLE );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			viewHolder.barPause.setVisibility( View.INVISIBLE );
			viewHolder.barDownloading.setVisibility( View.INVISIBLE );
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
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			int itemWidth = (int)( context.getResources().getDisplayMetrics().widthPixels / 3 - 6 * 2 * context.getResources().getDisplayMetrics().density );
			viewHolder.viewThumb.setLayoutParams( new RelativeLayout.LayoutParams(
					(int)( itemWidth + 6 * 2 * context.getResources().getDisplayMetrics().density ) ,
					(int)( itemWidth / 0.6f + 6 * 2 * context.getResources().getDisplayMetrics().density ) ) );
		}
		LockInformation lockInfo = (LockInformation)getItem( position );
		Bitmap imgThumb = mMemoryCache.get( lockInfo.getPackageName() );//lockInfo.getThumbImage();
		//		recycle.add( viewHolder.viewThumb );
		//		Tools.Recyclebitmap( imgDefaultThumb , imgThumb , viewHolder.viewThumb , recycle );
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
		}
		viewHolder.viewThumb.setImageBitmap( imgThumb );
		String displayName = lockInfo.getDisplayName();
		String showName = displayName;
		if( displayName.length() > 10 )
		{
			showName = displayName.substring( 0 , 10 ) + "...";
		}
		viewHolder.viewName.setText( showName );
		// viewName.setText(lockInfo.getDisplayName());
		if( lockInfo.isComponent( currentLock ) )
		{
			viewHolder.imageCover.setVisibility( View.VISIBLE );
			viewHolder.imageUsed.setVisibility( View.VISIBLE );
		}
		else
		{
			viewHolder.imageCover.setVisibility( View.INVISIBLE );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
		}
		if( lockInfo.isInstalled( context ) || lockInfo.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			viewHolder.barPause.setVisibility( View.INVISIBLE );
			viewHolder.barDownloading.setVisibility( View.INVISIBLE );
		}
		else
		{
			viewHolder.imageCover.setVisibility( View.VISIBLE );
			if( lockInfo.getDownloadStatus() == DownloadStatus.StatusDownloading )
			{
				viewHolder.barDownloading.setVisibility( View.VISIBLE );
				viewHolder.barPause.setVisibility( View.INVISIBLE );
				viewHolder.barDownloading.setProgress( lockInfo.getDownloadPercent() );
			}
			else
			{
				viewHolder.barDownloading.setVisibility( View.INVISIBLE );
				viewHolder.barPause.setVisibility( View.VISIBLE );
				viewHolder.barPause.setProgress( lockInfo.getDownloadPercent() );
			}
		}
		convertView.setTag( viewHolder );
		return convertView;
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
	
	public class PageItemTask extends AsyncTask<LockInformation , Integer , LockInformation>
	{
		
		public PageItemTask()
		{
			// Log.e("test", "PageItemTask");
		}
		
		@Override
		protected void onPostExecute(
				LockInformation lockInfo )
		{
			// TODO Auto-generated method stub
			notifyDataSetChanged();
			//			itemTask = null;
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected LockInformation doInBackground(
				LockInformation ... params )
		{
			// TODO Auto-generated method stub
			/*for (int i = 0; i < getCount(); i++)*/{
				LockInformation themeInfo = (LockInformation)params[0];
				Bitmap imgThumb = themeInfo.getThumbImage();
				if( imgThumb == null || imgThumb.isRecycled() )
				{
					themeInfo.setThumbImage( context , themeInfo.getPackageName() , themeInfo.getClassName() );
				}
				if( themeInfo.isNeedLoadDetail() )
				{
					if( imgThumb == null || imgThumb.isRecycled() )
					{
						themeInfo.loadDetail( context );
						if( themeInfo.getThumbImage() != null )
						{
							StaticClass.saveMyBitmap( context , themeInfo.getPackageName() , themeInfo.getClassName() , themeInfo.getThumbImage() );
						}
					}
					if( themeInfo.getThumbImage() == null )
					{
						downThumb.downloadThumb( themeInfo.getPackageName() , DownloadList.Lock_Type );
					}
				}
				addBitmapToMemoryCache( themeInfo.getPackageName() , themeInfo.getThumbImage() );
			}
			return null;
		}
	}
	
	public class PageTask extends AsyncTask<String , Integer , List<LockInformation>>
	{
		
		public PageTask()
		{
			// localList.clear();
			// packageNameSet.clear();
		}
		
		@Override
		protected void onPostExecute(
				List<LockInformation> result )
		{
			// TODO Auto-generated method stub
			packageNameSet.clear();
			for( LockInformation info : localList )
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
			if( backgroundListener != null )
			{
				backgroundListener.setBackground();
			}
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected List<LockInformation> doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			List<LockInformation> result = queryPackage();
			return result;
		}
	}
	
	private BackgroundChangeListener backgroundListener;
	
	public void setBackgroundListener(
			BackgroundChangeListener backgroundListener )
	{
		this.backgroundListener = backgroundListener;
	}
	
	interface BackgroundChangeListener
	{
		
		public void setBackground();
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
}
