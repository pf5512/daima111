package com.coco.theme.themebox;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class ThemeGridHotAdapter extends BaseAdapter
{
	
	private List<ThemeInformation> appList = new ArrayList<ThemeInformation>();
	private Context context;
	private DownModule downThumb;
	private Bitmap imgDefaultThumb;
	private boolean mShowProgress = false;
	private PageTask pageTask = null;
	private Set<ImageView> recycle = new HashSet<ImageView>();;
	
	public ThemeGridHotAdapter(
			Context cxt ,
			DownModule down )
	{
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );
		int size = preferences.getInt( "list-" + DownloadList.Theme_Type , 0 );
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
	
	public boolean showProgress()
	{
		return mShowProgress;
	}
	
	public void setShowProgress(
			boolean isShow )
	{
		mShowProgress = isShow;
	}
	
	public void reloadPackage()
	{
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
		ThemeInformation info = appList.get( findIndex );
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
		ThemeInformation info = appList.get( findIndex );
		info.setDownloadSize( downSize );
		info.setTotalSize( totalSize );
		// Log.d("GridLocalAdapter", "percent="+info.getDownloadPercent());
		notifyDataSetChanged();
	}
	
	public List<ThemeInformation> queryPackage(
			Set<String> pkgNameSet )
	{
		List<ThemeInformation> appList = new ArrayList<ThemeInformation>();
		ThemeService service = new ThemeService( context );
		List<ThemeInformation> hotList = service.queryShowList();
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
		for( ThemeInformation item : hotList )
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
	
	public void onDestory()
	{
		for( ThemeInformation info : appList )
		{
			info.disposeThumb();
			info = null;
		}
		if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
		{
			imgDefaultThumb.recycle();
		}
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
		ImageView imageUsed;
		ImageView imageCover;
		TextView viewName;
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
		ViewHolder viewHolder = null;
		View retView = convertView;
		if( retView != null )
		{
			viewHolder = (ViewHolder)convertView.getTag();
			// Log.e("test", "convertView!=null ");
		}
		else
		{
			viewHolder = new ViewHolder();
			retView = View.inflate( context , R.layout.grid_item , null );
			viewHolder.viewThumb = (ImageView)retView.findViewById( R.id.imageThumb );
			viewHolder.imageUsed = (ImageView)retView.findViewById( R.id.imageUsed );
			viewHolder.imageCover = (ImageView)retView.findViewById( R.id.imageCover );
			viewHolder.viewName = (TextView)retView.findViewById( R.id.textAppName );
			viewHolder.barPause = (ProgressBar)retView.findViewById( R.id.barPause );
			viewHolder.barDownloading = (ProgressBar)retView.findViewById( R.id.barDownloading );
			viewHolder.pricetxt = (TextView)retView.findViewById( R.id.price );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
			int itemWidth = (int)( context.getResources().getDisplayMetrics().widthPixels / 3 - 6 * 2 * context.getResources().getDisplayMetrics().density );
			viewHolder.viewThumb.setLayoutParams( new RelativeLayout.LayoutParams(
					(int)( itemWidth + 6 * 2 * context.getResources().getDisplayMetrics().density ) ,
					(int)( itemWidth / 0.6f + 6 * 2 * context.getResources().getDisplayMetrics().density ) ) );
		}
		recycle.add( viewHolder.viewThumb );
		Recyclebitmap( viewHolder.viewThumb );
		ThemeInformation lockInfo = (ThemeInformation)getItem( position );
		if( lockInfo.isNeedLoadDetail() )
		{
			lockInfo.loadDetail( context );
			if( lockInfo.getThumbImage() == null )
			{
				downThumb.downloadThumb( lockInfo.getPackageName() , DownloadList.Theme_Type );
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
				viewHolder.pricetxt.setVisibility( View.VISIBLE );
				boolean ispay = Tools.isContentPurchased( context , DownloadList.Theme_Type , lockInfo.getPackageName() );
				if( ispay )
				{
					viewHolder.pricetxt.setBackgroundResource( R.drawable.buyed_bg );
					viewHolder.pricetxt.setText( R.string.has_bought );
				}
				else
				{
					viewHolder.pricetxt.setBackgroundResource( R.drawable.price_bg );
					viewHolder.pricetxt.setText( "￥" + price / 100 + ".00" );
				}
			}
			else
			{
				viewHolder.pricetxt.setVisibility( View.GONE );
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
			for( ThemeInformation info : appList )
			{
				info.disposeThumb();
				info = null;
			}
			appList.clear();
			appList.addAll( result );
			// appList = result;
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
		protected List<ThemeInformation> doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			Set<String> pkgNameSet = new HashSet<String>();
			ThemeService themeSv = new ThemeService( context );
			List<ActivityInfo> infoList = themeSv.queryThemeActivityList();
			for( ActivityInfo info : infoList )
			{
				pkgNameSet.add( info.applicationInfo.packageName );
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
