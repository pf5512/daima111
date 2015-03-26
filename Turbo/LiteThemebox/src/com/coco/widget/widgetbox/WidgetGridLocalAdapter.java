package com.coco.widget.widgetbox;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.iLoong.base.themebox.R;


public class WidgetGridLocalAdapter extends BaseAdapter
{
	
	private List<WidgetInformation> localList = new ArrayList<WidgetInformation>();
	private Context context;
	private Bitmap imgDefaultThumb;
	private DownModule downThumb;
	private Set<String> packageNameSet = new HashSet<String>();
	private Set<ImageView> recycle = new HashSet<ImageView>();
	private PageTask pageTask = null;
	private LruCache<String , Bitmap> mMemoryCache;
	private Handler mMainHandler = new Handler() {
		
		@Override
		public void handleMessage(
				Message msg )
		{
			// TODO Auto-generated method stub
			super.handleMessage( msg );
			if( !FunctionConfig.isEnable_add_widget() )
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
				int count = pref.getInt( "firstWidgetLocal" , 0 );
				if( count <= 0 )
				{
					pref.edit().putInt( "firstWidgetLocal" , count + 1 ).commit();
					Toast.makeText( context , R.string.widget_local_click_toast , Toast.LENGTH_SHORT ).show();
				}
				else if( count == 1 )
				{
					pref.edit().putInt( "firstWidgetLocal" , count + 1 ).commit();
				}
			}
		}
	};
	private HashMap<String , WidgetInformation> taskSet = new HashMap<String , WidgetInformation>();
	private boolean pageLoad = false;
	private static Object taskObj = new Object();
	
	public WidgetGridLocalAdapter(
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
	
	public void onDestory()
	{
		for( WidgetInformation info : localList )
		{
			info.disposeThumb();
			info = null;
		}
		if( imgDefaultThumb != null && !imgDefaultThumb.isRecycled() )
		{
			imgDefaultThumb.recycle();
		}
	}
	
	private List<WidgetInformation> queryPackage()
	{
		packageNameSet.clear();
		// localList.clear();
		List<WidgetInformation> localList = new ArrayList<WidgetInformation>();
		WidgetService themeSv = new WidgetService( context );
		List<WidgetInformation> installList = themeSv.queryInstallList();
		for( WidgetInformation info : installList )
		{
			info.setThumbImage( context , info.getPackageName() , info.getClassName() );
			addBitmapToMemoryCache( info.getPackageName() , info.getThumbImage() );
			if( info.getThumbImage() == null )
			{
				new PageItemTask().execute( info );
			}
			localList.add( info );
			packageNameSet.add( info.getPackageName() );
		}
		return localList;
	}
	
	public void reloadPackage()
	{
		// queryPackage();
		// ((Activity)context).runOnUiThread(new Runnable(){
		// @Override
		// public void run() {
		// notifyDataSetChanged();
		// }
		// });
		if( pageTask != null && pageTask.getStatus() != PageTask.Status.FINISHED )
		{
			pageTask.cancel( true );
		}
		pageTask = (PageTask)new PageTask().execute();
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
		WidgetInformation info = localList.get( findIndex );
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
		// TODO Auto-generated method stub
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
			convertView = View.inflate( context , R.layout.main_widget_item , null );
			viewHolder.viewName = (TextView)convertView.findViewById( R.id.textAppName );
			viewHolder.viewThumb = (ImageView)convertView.findViewById( R.id.imageThumb );
			viewHolder.imageCover = (ImageView)convertView.findViewById( R.id.imageCover );
			viewHolder.imageUsed = (ImageView)convertView.findViewById( R.id.imageUsed );
			viewHolder.barPause = (ProgressBar)convertView.findViewById( R.id.barPause );
			viewHolder.barDownloading = (ProgressBar)convertView.findViewById( R.id.barDownloading );
			viewHolder.imageUsed.setVisibility( View.INVISIBLE );
		}
		WidgetInformation Info = (WidgetInformation)getItem( position );
		Bitmap imgThumb = mMemoryCache.get( Info.getPackageName() );//Info.getThumbImage();
		//		recycle.add( viewHolder.viewThumb );
		//		Tools.Recyclebitmap( imgDefaultThumb , imgThumb , viewHolder.viewThumb , recycle );
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
		}
		viewHolder.viewThumb.setImageBitmap( imgThumb );
		viewHolder.viewThumb.setScaleType( ScaleType.FIT_CENTER );
		// viewHolder.viewThumb.setBackgroundDrawable(null);
		viewHolder.viewName.setText( Info.getDisplayName() );
		viewHolder.imageCover.setVisibility( View.INVISIBLE );
		viewHolder.imageUsed.setVisibility( View.INVISIBLE );
		if( Info.isInstalled( context ) || Info.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			viewHolder.barPause.setVisibility( View.INVISIBLE );
			viewHolder.barDownloading.setVisibility( View.INVISIBLE );
		}
		else
		{
			viewHolder.imageCover.setVisibility( View.VISIBLE );
			if( Info.getDownloadStatus() == DownloadStatus.StatusDownloading )
			{
				viewHolder.barDownloading.setVisibility( View.VISIBLE );
				viewHolder.barPause.setVisibility( View.INVISIBLE );
				viewHolder.barDownloading.setProgress( Info.getDownloadPercent() );
			}
			else
			{
				viewHolder.barDownloading.setVisibility( View.INVISIBLE );
				viewHolder.barPause.setVisibility( View.VISIBLE );
				viewHolder.barPause.setProgress( Info.getDownloadPercent() );
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
	
	public class PageItemTask extends AsyncTask<WidgetInformation , Integer , WidgetInformation>
	{
		
		public PageItemTask()
		{
		}
		
		@Override
		protected void onPostExecute(
				WidgetInformation lockInfo )
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
		protected WidgetInformation doInBackground(
				WidgetInformation ... params )
		{
			// TODO Auto-generated method stub
			WidgetInformation themeInfo = (WidgetInformation)params[0];
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
				downThumb.downloadThumb( themeInfo.getPackageName() , DownloadList.Widget_Type );
			}
			addBitmapToMemoryCache( themeInfo.getPackageName() , themeInfo.getThumbImage() );
			return themeInfo;
		}
	}
	
	public class PageTask extends AsyncTask<String , Integer , List<WidgetInformation>>
	{
		
		public PageTask()
		{
			localList.clear();
			packageNameSet.clear();
			pageLoad = false;
		}
		
		@Override
		protected void onPostExecute(
				List<WidgetInformation> result )
		{
			// TODO Auto-generated method stub
			packageNameSet.clear();
			for( WidgetInformation info : localList )
			{
				info.disposeThumb();
				info = null;
			}
			localList.clear();
			localList.addAll( result );
			if( result != null )
			{
				for( WidgetInformation info : result )
				{
					packageNameSet.add( info.getPackageName() );
				}
			}
			pageLoad = true;
			recycle.clear();
			notifyDataSetChanged();
			pageTask = null;
			if( backgroundListener != null )
			{
				backgroundListener.setBackground();
			}
			if( localList != null )
			{
				mMainHandler.sendEmptyMessage( 0 );
			}
		}
		
		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected List<WidgetInformation> doInBackground(
				String ... params )
		{
			// TODO Auto-generated method stub
			List<WidgetInformation> result = queryPackage();
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
