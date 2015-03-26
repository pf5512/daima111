package com.coco.theme.themebox;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.iLoong.base.themebox.R;


public class ThemeGridHotAdapter extends BaseAdapter
{
	
	private List<ThemeInformation> appList = new ArrayList<ThemeInformation>();
	private Context context;
	private ThemeDownModule downThumb;
	private Bitmap imgDefaultThumb;
	private boolean mShowProgress = false;
	
	public ThemeGridHotAdapter(
			Context cxt ,
			ThemeDownModule down )
	{
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
	}
	
	public boolean showProgress()
	{
		return mShowProgress;
	}
	
	public void reloadPackage(
			Set<String> pkgNameSet )
	{
		queryPackage( pkgNameSet );
		notifyDataSetChanged();
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
	
	public void queryPackage(
			Set<String> pkgNameSet )
	{
		appList.clear();
		ThemeService service = new ThemeService( context );
		List<ThemeInformation> hotList = service.queryShowList();
		if( hotList.size() == 0 )
		{
			if( !StaticClass.isAllowDownload( context ) || com.coco.theme.themebox.StaticClass.canDownToInternal )
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
			//			if (!pkgNameSet.contains(item.getPackageName())) {				
			appList.add( item );
			//			}
		}
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
	
	@Override
	public View getView(
			int position ,
			View convertView ,
			ViewGroup parent )
	{
		View retView = convertView;
		if( retView == null )
		{
			retView = View.inflate( context , R.layout.theme_grid_item , null );
			retView.findViewById( R.id.imageUsed ).setVisibility( View.INVISIBLE );
			//			retView.findViewById(R.id.imageCover).setVisibility(View.INVISIBLE);
			//			retView.findViewById(R.id.barDownloading).setVisibility(
			//					View.INVISIBLE);
			//			retView.findViewById(R.id.barPause).setVisibility(View.INVISIBLE);
		}
		ThemeInformation lockInfo = (ThemeInformation)getItem( position );
		if( lockInfo.isNeedLoadDetail() )
		{
			lockInfo.loadDetail( context );
			if( lockInfo.getThumbImage() == null )
			{
				Log.v( "11111111111111111" , "111111111111111111111111" );
				downThumb.downloadThumb( lockInfo.getPackageName() );
			}
		}
		Bitmap imgThumb = lockInfo.getThumbImage();
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
			Log.v( "11111111111111111" , "222222222222222222222" );
		}
		ImageView viewThumb = (ImageView)retView.findViewById( R.id.imageThumb );
		viewThumb.setImageBitmap( imgThumb );
		TextView viewName = (TextView)retView.findViewById( R.id.textAppName );
		String showName = lockInfo.getDisplayName();
		int index = showName.indexOf( "-" );
		if( index >= 0 )
		{
			showName = showName.substring( 0 , index );
		}
		viewName.setText( showName );
		ProgressBar barPause = (ProgressBar)retView.findViewById( R.id.barPause );
		ProgressBar barDownloading = (ProgressBar)retView.findViewById( R.id.barDownloading );
		if( lockInfo.getDownloadStatus() == DownloadStatus.StatusInit || lockInfo.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
			retView.findViewById( R.id.imageCover ).setVisibility( View.INVISIBLE );
			retView.findViewById( R.id.imageCover ).setVisibility( View.INVISIBLE );
			barPause.setVisibility( View.INVISIBLE );
			barDownloading.setVisibility( View.INVISIBLE );
		}
		else
		{
			retView.findViewById( R.id.imageCover ).setVisibility( View.VISIBLE );
			if( lockInfo.getDownloadStatus() == DownloadStatus.StatusDownloading )
			{
				barDownloading.setVisibility( View.VISIBLE );
				barPause.setVisibility( View.INVISIBLE );
				barDownloading.setProgress( lockInfo.getDownloadPercent() );
			}
			else
			{
				barDownloading.setVisibility( View.INVISIBLE );
				barPause.setVisibility( View.VISIBLE );
				barPause.setProgress( lockInfo.getDownloadPercent() );
			}
		}
		return retView;
	}
}
