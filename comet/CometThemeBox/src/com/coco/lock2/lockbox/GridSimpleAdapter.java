package com.coco.lock2.lockbox;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.coco.theme.themebox.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.coco.lock2.lockbox.database.model.DownloadLockItem;
import com.coco.lock2.lockbox.database.model.DownloadStatus;
import com.coco.lock2.lockbox.database.remoting.DownloadLockService;
import com.coco.lock2.lockbox.util.DownModule;
import com.coco.lock2.lockbox.util.LockManager;
import com.coco.lock2.lockbox.util.PathTool;
import com.iLoong.base.themebox.R;


public class GridSimpleAdapter extends BaseAdapter
{
	
	private List<LockInformation> appList = new ArrayList<LockInformation>();
	private Context context;
	private DownModule downThumb;
	private Bitmap imgDefaultThumb;
	private boolean mShowProgress = false;
	private DownloadLockService downApkDb;
	
	public GridSimpleAdapter(
			Context cxt ,
			DownModule down )
	{
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ( (BitmapDrawable)cxt.getResources().getDrawable( R.drawable.default_img ) ).getBitmap();
		downApkDb = new DownloadLockService( context );
	}
	
	public boolean showProgress()
	{
		return mShowProgress;
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
	
	public void updateDownState(
			String pkgname ,
			DownloadStatus status )
	{
		int findIndex = findPackageIndex( pkgname );
		if( findIndex < 0 )
		{
			return;
		}
		LockInformation info = appList.get( findIndex );
		info.setDownloadStatus( status );
		Log.d( "GridHotAdapter" , "percent=" + info.getDownloadPercent() );
		notifyDataSetChanged();
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
	
	public void queryPackage(
			Set<String> pkgNameSet )
	{
		appList.clear();
		LockManager mgr = new LockManager( context );
		List<LockInformation> hotList = mgr.queryHotList();
		if( hotList.size() == 0 )
		{
			mShowProgress = true;
		}
		else
		{
			mShowProgress = false;
		}
		List<LockInformation> showList = mgr.queryShowSimpleList();
		for( LockInformation item : showList )
		{
			appList.add( item );
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
			final int position ,
			View convertView ,
			ViewGroup parent )
	{
		View retView = convertView;
		if( retView == null )
		{
			retView = View.inflate( context , R.layout.grid_item , null );
			retView.findViewById( R.id.imageUsed ).setVisibility( View.INVISIBLE );
		}
		final LockInformation lockInfo = (LockInformation)getItem( position );
		Log.v( "getView" , "position = " + position + "  packname = " + lockInfo.getPackageName() + "   status = " + lockInfo.getDownloadStatus() );
		if( lockInfo.isNeedLoadDetail() )
		{
			lockInfo.loadDetail( context );
			if( lockInfo.getThumbImage() == null )
			{
				downThumb.downloadThumb( lockInfo.getPackageName() );
			}
		}
		Bitmap imgThumb = lockInfo.getThumbImage();
		if( imgThumb == null )
		{
			imgThumb = imgDefaultThumb;
		}
		ImageButton viewThumb = (ImageButton)retView.findViewById( R.id.imageThumb );
		viewThumb.setImageBitmap( imgThumb );
		viewThumb.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				LockInformation infor = (LockInformation)getItem( position );
				Intent i = new Intent();
				i.putExtra( StaticClass.EXTRA_PACKAGE_NAME , infor.getPackageName() );
				i.putExtra( StaticClass.EXTRA_CLASS_NAME , infor.getClassName() );
				i.setClassName( StaticClass.LOCKBOX_PACKAGE_NAME , StaticClass.LOCKBOX_PREVIEW_ACTIVITY );
				context.startActivity( i );
			}
		} );
		final ImageButton downIcon = (ImageButton)retView.findViewById( R.id.downicon );
		final ImageButton continueButton = (ImageButton)retView.findViewById( R.id.continueicon );
		final ImageButton stopButton = (ImageButton)retView.findViewById( R.id.stopicon );
		final ProgressBar barPause = (ProgressBar)retView.findViewById( R.id.barPause );
		final ImageView cover = (ImageView)retView.findViewById( R.id.imageCover );
		final ProgressBar barDownloading = (ProgressBar)retView.findViewById( R.id.barDownloading );
		if( lockInfo.getDownloadStatus() == DownloadStatus.StatusDownloading )
		{
			downIcon.setVisibility( View.INVISIBLE );
			stopButton.setVisibility( View.VISIBLE );
			continueButton.setVisibility( View.INVISIBLE );
		}
		else if( lockInfo.getDownloadStatus() == DownloadStatus.StatusPause )
		{
			downIcon.setVisibility( View.INVISIBLE );
			continueButton.setVisibility( View.VISIBLE );
			stopButton.setVisibility( View.INVISIBLE );
		}
		else
		{
			downIcon.setVisibility( View.VISIBLE );
			continueButton.setVisibility( View.INVISIBLE );
			stopButton.setVisibility( View.INVISIBLE );
		}
		downIcon.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				DownloadLockService dSv = new DownloadLockService( context );
				DownloadLockItem dItem = dSv.queryByPackageName( lockInfo.getPackageName() );
				if( dItem == null )
				{
					new File( PathTool.getDownloadingApp( lockInfo.getPackageName() ) ).delete();
					dItem = new DownloadLockItem();
					dItem.copyFromLockInfo( lockInfo.getInfoItem() );
					dItem.setDownloadStatus( DownloadStatus.StatusDownloading );
					dSv.insertItem( dItem );
				}
				cover.setVisibility( View.VISIBLE );
				downIcon.setVisibility( View.INVISIBLE );
				stopButton.setVisibility( View.VISIBLE );
				barDownloading.setVisibility( View.VISIBLE );
				barDownloading.setProgress( lockInfo.getDownloadPercent() );
				Log.v( "HotAdapter" , "clickposition = " + position );
				downApkDb.updateDownloadStatus( lockInfo.getPackageName() , DownloadStatus.StatusDownloading );
				Intent intent = new Intent();
				intent.setAction( StaticClass.ACTION_START_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , lockInfo.getPackageName() );
				context.sendBroadcast( intent );
			}
		} );
		continueButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				continueButton.setVisibility( View.INVISIBLE );
				stopButton.setVisibility( View.VISIBLE );
				barDownloading.setVisibility( View.VISIBLE );
				barDownloading.setProgress( lockInfo.getDownloadPercent() );
				downApkDb.updateDownloadStatus( lockInfo.getPackageName() , DownloadStatus.StatusDownloading );
				Intent intent = new Intent( StaticClass.ACTION_START_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , lockInfo.getPackageName() );
				context.sendBroadcast( intent );
			}
		} );
		stopButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				stopButton.setVisibility( View.INVISIBLE );
				continueButton.setVisibility( View.VISIBLE );
				barPause.setVisibility( View.VISIBLE );
				barPause.setProgress( lockInfo.getDownloadPercent() );
				downApkDb.updateDownloadStatus( lockInfo.getPackageName() , DownloadStatus.StatusPause );
				Intent intent = new Intent( StaticClass.ACTION_PAUSE_DOWNLOAD_APK );
				intent.putExtra( StaticClass.EXTRA_PACKAGE_NAME , lockInfo.getPackageName() );
				context.sendBroadcast( intent );
			}
		} );
		TextView viewName = (TextView)retView.findViewById( R.id.textAppName );
		String displayName = lockInfo.getDisplayName();
		String showName = displayName;
		if( displayName.length() > 10 )
		{
			showName = displayName.substring( 0 , 8 ) + "...";
		}
		viewName.setText( showName );
		if( lockInfo.getDownloadStatus() == DownloadStatus.StatusInit || lockInfo.getDownloadStatus() == DownloadStatus.StatusFinish )
		{
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
