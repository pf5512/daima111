package com.coco.scene.scenebox;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class SceneGridHotAdapter extends BaseAdapter
{
	
	private List<SceneInformation> appList = new ArrayList<SceneInformation>();
	private Context context;
	private DownModule downThumb;
	private Bitmap imgDefaultThumb;
	private boolean mShowProgress = false;
	private PageTask pageTask = null;
	//	private Set<ImageView> recycle = new HashSet<ImageView>();;
	private LruCache<String , Bitmap> mMemoryCache;
	
	public SceneGridHotAdapter(
			Context cxt ,
			DownModule down )
	{
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
		int size = preferences.getInt( "list-" + DownloadList.Scene_Type , 0 );
		if( size == 0 )
		{
			if( !com.coco.theme.themebox.StaticClass.isAllowDownload( context ) )
			{
				mShowProgress = false;
			}
			else
			{
				mShowProgress = true;
			}
		}
		else
		{
			mShowProgress = false;
		}
		int maxMemory = (int)Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 16;
		mMemoryCache = new LruCache<String , Bitmap>( cacheSize );
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
	}
	
	public void onDestory()
	{
		for( SceneInformation info : appList )
		{
			info.disposeThumb();
			info = null;
		}
		if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
		{
			imgDefaultThumb.recycle();
		}
	}
	
	public boolean showProgress()
	{
		return mShowProgress;
	}
	
	public void setShowProgress(
			boolean isShow )
	{
		mShowProgress = isShow;
	}
	
	public void reloadPackage(
			Set<String> pkgNameSet )
	{
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
	}
	
	public List<SceneInformation> queryPackage(
			Set<String> pkgNameSet )
	{
		// appList.clear();
		List<SceneInformation> appList = new ArrayList<SceneInformation>();
		SceneService service = new SceneService( context );
		List<SceneInformation> hotList = service.queryShowList();
		if( hotList.size() == 0 )
		{
			if( !StaticClass.isAllowDownload( context ) )
			{
				mShowProgress = false;
			}
			else
			{
				mShowProgress = true;
			}
		}
		else
		{
			mShowProgress = false;
		}
		for( SceneInformation item : hotList )
		{
			if( !pkgNameSet.contains( item.getPackageName() ) )
			{
				appList.add( item );
				item.loadDetail( context );
				if( item.getThumbImage() != null )
				{
					addBitmapToMemoryCache( item.getPackageName() , item.getThumbImage() );
				}
				else
				{
					downThumb.downloadThumb( item.getPackageName() , DownloadList.Scene_Type );
				}
				//				new PageItemTask().execute( item );
			}
		}
		return appList;
	}
	
	public class PageItemTask extends AsyncTask<SceneInformation , Integer , SceneInformation>
	{
		
		public PageItemTask()
		{
		}
		
		@Override
		protected void onPostExecute(
				SceneInformation lockInfo )
		{
			notifyDataSetChanged();
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected SceneInformation doInBackground(
				SceneInformation ... params )
		{
			// TODO Auto-generated method stub
			SceneInformation item = (SceneInformation)params[0];
			item.loadDetail( context );
			if( item.getThumbImage() != null )
			{
				addBitmapToMemoryCache( item.getPackageName() , item.getThumbImage() );
			}
			else
			{
				downThumb.downloadThumb( item.getPackageName() , DownloadList.Scene_Type );
			}
			return null;
		}
	}
	
	private int findPackageIndex(
			String packageName )
	{
		int i = 0;
		for( i = 0 ; i < appList.size() ; i++ )
		{
			if( packageName.equals( appList.get( i ).getPackageName() ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	public void updateThumb(
			String pkgName )
	{
		int findIndex = findPackageIndex( pkgName );
		if( findIndex < 0 )
		{
			return;
		}
		SceneInformation info = appList.get( findIndex );
		info.reloadThumb( context );
		addBitmapToMemoryCache( info.getPackageName() , info.getThumbImage() );
		notifyDataSetChanged();
	}
	
	public void updateDownloadSize(
			String pkgName ,
			long downSize ,
			long totalSize )
	{
		// Log.d("GridLocalAdapter",
		// "downSize="+downSize+",totalSize="+totalSize);
		int findIndex = findPackageIndex( pkgName );
		if( findIndex < 0 )
		{
			return;
		}
		SceneInformation info = appList.get( findIndex );
		info.setDownloadSize( downSize );
		info.setTotalSize( totalSize );
		// Log.d("GridLocalAdapter", "percent="+info.getDownloadPercent());
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
		return appList.size();
	}
	
	@Override
	public Object getItem(
			int position )
	{
		return appList.get( position );
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
		TextView pricetxt;
	}
	
	@Override
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		View retView = convertView;
		ViewHolder viewHolder = null;
		if( retView != null )
		{
			// Log.e("test", "convertView!=null");
			viewHolder = (ViewHolder)convertView.getTag();
		}
		else
		{
			retView = View.inflate( context , R.layout.grid_item , null );
			viewHolder = new ViewHolder();
			viewHolder.viewName = (TextView)retView.findViewById( R.id.textAppName );
			viewHolder.viewThumb = (ImageView)retView.findViewById( R.id.imageThumb );
			viewHolder.imageCover = (ImageView)retView.findViewById( R.id.imageCover );
			viewHolder.imageUsed = (ImageView)retView.findViewById( R.id.imageUsed );
			viewHolder.barPause = (ProgressBar)retView.findViewById( R.id.barPause );
			viewHolder.barDownloading = (ProgressBar)retView.findViewById( R.id.barDownloading );
			viewHolder.pricetxt = (TextView)retView.findViewById( R.id.price );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			int itemWidth = (int)( context.getResources().getDisplayMetrics().widthPixels / 3 - 6 * 2 * context.getResources().getDisplayMetrics().density );
			viewHolder.viewThumb.setLayoutParams( new RelativeLayout.LayoutParams(
					(int)( itemWidth + 6 * 2 * context.getResources().getDisplayMetrics().density ) ,
					(int)( itemWidth / 0.6f + 6 * 2 * context.getResources().getDisplayMetrics().density ) ) );
		}
		//		recycle.add( viewHolder.viewThumb );
		//		Recyclebitmap( viewHolder.viewThumb );
		SceneInformation info = (SceneInformation)getItem( position );
		//		if( info.isNeedLoadDetail() )
		//		{
		//			info.loadDetail( context );
		//			if( info.getThumbImage() == null )
		//			{
		//				downThumb.downloadThumb( info.getPackageName() , DownloadList.Scene_Type );
		//			}
		//		}
		Bitmap imgThumb = mMemoryCache.get( info.getPackageName() );
		if( imgThumb == null || imgThumb.isRecycled() )
		{
			imgThumb = imgDefaultThumb;
		}
		viewHolder.viewThumb.setImageBitmap( imgThumb );
		viewHolder.viewName.setText( info.getDisplayName() );
		if( FunctionConfig.isPriceVisible() )
		{
			int price = info.getPrice();
			if( info.getPrice() > 0 )
			{
				viewHolder.pricetxt.setVisibility( View.VISIBLE );
				boolean ispay = Tools.isContentPurchased( context , DownloadList.Scene_Type , info.getPackageName() );
				if( ispay )
				{
					viewHolder.pricetxt.setBackgroundResource( R.drawable.buyed_bg );
					viewHolder.pricetxt.setText( R.string.has_bought );
				}
				else
				{
					viewHolder.pricetxt.setBackgroundResource( R.drawable.price_bg );
					viewHolder.pricetxt.setText( "￥：" + price / 100 );
				}
			}
			else
			{
				viewHolder.pricetxt.setVisibility( View.GONE );
			}
		}
		if( info.getDownloadStatus() == DownloadStatus.StatusInit || info.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			viewHolder.imageCover.setVisibility( View.INVISIBLE );
			viewHolder.barPause.setVisibility( View.INVISIBLE );
			viewHolder.barDownloading.setVisibility( View.INVISIBLE );
		}
		else
		{
			viewHolder.imageCover.setVisibility( View.VISIBLE );
			if( info.getDownloadStatus() == DownloadStatus.StatusDownloading )
			{
				viewHolder.barDownloading.setVisibility( View.VISIBLE );
				viewHolder.barPause.setVisibility( View.INVISIBLE );
				viewHolder.barDownloading.setProgress( info.getDownloadPercent() );
			}
			else
			{
				viewHolder.barDownloading.setVisibility( View.INVISIBLE );
				viewHolder.barPause.setVisibility( View.VISIBLE );
				viewHolder.barPause.setProgress( info.getDownloadPercent() );
			}
		}
		retView.setTag( viewHolder );
		return retView;
	}
	
	//	private void Recyclebitmap(
	//			ImageView view )
	//	{
	//		boolean isrecycle = true;
	//		Bitmap bmp = Tools.recycleImageBitmap( view );
	//		if( bmp == null || bmp.isRecycled() || bmp == imgDefaultThumb )
	//		{
	//			return;
	//		}
	//		for( ImageView v : recycle )
	//		{
	//			if( v == view )
	//			{
	//				continue;
	//			}
	//			Bitmap temp = Tools.recycleImageBitmap( v );
	//			if( temp == bmp )
	//			{
	//				isrecycle = false;
	//				break;
	//			}
	//		}
	//		if( isrecycle )
	//		{
	//			bmp.recycle();
	//		}
	//	}
	public class PageTask extends AsyncTask<String , Integer , List<SceneInformation>>
	{
		
		public PageTask()
		{
		}
		
		@Override
		protected void onPostExecute(
				List<SceneInformation> result )
		{
			// TODO Auto-generated method stub
			for( SceneInformation info : appList )
			{
				info.disposeThumb();
				info = null;
			}
			appList.clear();
			appList.addAll( result );
			notifyDataSetChanged();
			pageTask = null;
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected List<SceneInformation> doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			// List<LockInformation> result = queryPackage();
			// return result;
			Set<String> pkgNameSet = new HashSet<String>();
			SceneService themeSv = new SceneService( context );
			List<SceneInformation> installList = themeSv.queryInstallList();
			for( SceneInformation info : installList )
			{
				pkgNameSet.add( info.getPackageName() );
			}
			return queryPackage( pkgNameSet );
		}
	}
	
	//	@Override
	//	public void notifyDataSetChanged()
	//	{
	//		// TODO Auto-generated method stub
	//		recycle.clear();
	//		super.notifyDataSetChanged();
	//	}
	//	
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
