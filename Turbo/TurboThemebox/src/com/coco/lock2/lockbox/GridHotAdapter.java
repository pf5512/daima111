package com.coco.lock2.lockbox;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class GridHotAdapter extends BaseAdapter
{
	
	private List<LockInformation> appList = new ArrayList<LockInformation>();
	private Context context;
	private DownModule downThumb;
	private Bitmap imgDefaultThumb;
	private boolean mShowProgress = false;
	private PageTask pageTask = null;
	private Set<ImageView> recycle = new HashSet<ImageView>();;
	
	public GridHotAdapter(
			Context cxt ,
			DownModule down )
	{
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
		int size = preferences.getInt( "list-" + DownloadList.Lock_Type , 0 );
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
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
	}
	
	public void onDestory()
	{
		for( LockInformation info : appList )
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
		LockInformation info = appList.get( findIndex );
		info.setDownloadSize( downSize );
		info.setTotalSize( totalSize );
		Log.d( "GridHotAdapter" , "percent=" + info.getDownloadPercent() );
		notifyDataSetChanged();
	}
	
	public void reloadPackage(
			Set<String> pkgNameSet )
	{
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
		// queryPackage(pkgNameSet);
		// notifyDataSetChanged();
	}
	
	public void updateThumb(
			String pkgName )
	{
		int findIndex = findPackageIndex( pkgName );
		if( findIndex < 0 )
		{
			return;
		}
		LockInformation info = appList.get( findIndex );
		info.reloadThumb();
		notifyDataSetChanged();
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
	
	public List<LockInformation> queryPackage(
			Set<String> pkgNameSet )
	{
		// appList.clear();
		List<LockInformation> appList = new ArrayList<LockInformation>();
		LockManager mgr = new LockManager( context );
		List<LockInformation> hotList = mgr.queryShowList();
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
		for( LockInformation item : hotList )
		{
			if( !pkgNameSet.contains( item.getPackageName() ) )
			{
				appList.add( item );
			}
		}
		return appList;
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
		TextView price;
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
			viewHolder.price = (TextView)retView.findViewById( R.id.price );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			// retView.findViewById(R.id.imageUsed).setVisibility(View.INVISIBLE);
			int itemWidth = (int)( context.getResources().getDisplayMetrics().widthPixels / 3 - 6 * 2 * context.getResources().getDisplayMetrics().density );
			viewHolder.viewThumb.setLayoutParams( new RelativeLayout.LayoutParams(
					(int)( itemWidth + 6 * 2 * context.getResources().getDisplayMetrics().density ) ,
					(int)( itemWidth / 0.6f + 6 * 2 * context.getResources().getDisplayMetrics().density ) ) );
		}
		recycle.add( viewHolder.viewThumb );
		Recyclebitmap( viewHolder.viewThumb );
		LockInformation lockInfo = (LockInformation)getItem( position );
		if( lockInfo.isNeedLoadDetail() )
		{
			lockInfo.loadDetail( context );
			if( lockInfo.getThumbImage() == null )
			{
				downThumb.downloadThumb( lockInfo.getPackageName() , DownloadList.Lock_Type );
			}
		}
		Bitmap imgThumb = lockInfo.getThumbImage();
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
		}
		viewHolder.viewThumb.setImageBitmap( imgThumb );
		if( FunctionConfig.isPriceVisible() )
		{
			int price = lockInfo.getPrice();
			if( lockInfo.getPrice() > 0 )
			{
				viewHolder.price.setVisibility( View.VISIBLE );
				boolean ispay = Tools.isContentPurchased( context , DownloadList.Lock_Type , lockInfo.getPackageName() );
				if( ispay )
				{
					viewHolder.price.setBackgroundResource( R.drawable.buyed_bg );
					viewHolder.price.setText( R.string.has_bought );
				}
				else
				{
					viewHolder.price.setBackgroundResource( R.drawable.price_bg );
					viewHolder.price.setText( "￥" + price / 100 + ".00" );
				}
			}
			else
			{
				viewHolder.price.setVisibility( View.GONE );
			}
		}
		viewHolder.viewName.setText( lockInfo.getDisplayName() );
		if( lockInfo.getDownloadStatus() == DownloadStatus.StatusInit || lockInfo.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			viewHolder.imageCover.setVisibility( View.INVISIBLE );
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
		retView.setTag( viewHolder );
		return retView;
	}
	
	private void Recyclebitmap(
			ImageView view )
	{
		boolean isrecycle = true;
		Bitmap bmp = Tools.recycleImageBitmap( view );
		if( bmp == null || bmp.isRecycled() || bmp == imgDefaultThumb )
		{
			return;
		}
		for( ImageView v : recycle )
		{
			if( v == view )
			{
				continue;
			}
			Bitmap temp = Tools.recycleImageBitmap( v );
			if( temp == bmp )
			{
				isrecycle = false;
				break;
			}
		}
		if( isrecycle )
		{
			bmp.recycle();
		}
	}
	
	public class PageTask extends AsyncTask<String , Integer , List<LockInformation>>
	{
		
		public PageTask()
		{
		}
		
		@Override
		protected void onPostExecute(
				List<LockInformation> result )
		{
			// TODO Auto-generated method stub
			for( LockInformation info : appList )
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
		protected List<LockInformation> doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			// List<LockInformation> result = queryPackage();
			// return result;
			Set<String> pkgNameSet = new HashSet<String>();
			Intent intentLockView = new Intent( StaticClass.ACTION_LOCK_VIEW );
			intentLockView.addCategory( Intent.CATEGORY_INFO );
			List<ResolveInfo> infoList = context.getPackageManager().queryIntentActivities( intentLockView , 0 );
			for( ResolveInfo info : infoList )
			{
				pkgNameSet.add( info.activityInfo.packageName );
			}
			intentLockView.setAction( "com.coco.third.lock.action.VIEW" );
			intentLockView.addCategory( Intent.CATEGORY_INFO );
			infoList = context.getPackageManager().queryIntentActivities( intentLockView , 0 );
			for( ResolveInfo info : infoList )
			{
				pkgNameSet.add( info.activityInfo.packageName );
			}
			return queryPackage( pkgNameSet );
		}
	}
	
	@Override
	public void notifyDataSetChanged()
	{
		// TODO Auto-generated method stub
		recycle.clear();
		super.notifyDataSetChanged();
	}
}
