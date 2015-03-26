package com.coco.wf.wfbox;


import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.coco.download.Assets;
import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class TabEffectFactory implements TabHost.TabContentFactory
{
	
	private Context mContext;
	private GridView effectWorkspace;
	private GridView effectApp;
	private ViewPager effectGridPager;
	private final int INDEX_WORKSPACE = 0;
	private final int INDEX_APP = 1;
	private GridPagerAdapter effectPagerAdapter;
	private GridViewAdapter workspaceAdapter;
	private GridViewAdapter appAdaper;
	private final String ACTION_EFFECT_PREVIEW = "com.cool.action.EffectPreview";
	private final String ACTION_EFFECT_PREVIEW_EXTRA_TYPE = "EffectPreviewExtraType";
	private final String ACTION_EFFECT_PREVIEW_EXTRA_INDEX = "EffectPreviewExtraIndex";
	private int index = 0;
	private final String DEFAULT_CHANGE = "com.coco.effect.action.DEFAULT_EFFECT_CHANGED";
	private BroadcastReceiver mReceiver = null;
	
	@Override
	public View createTabContent(
			String tag )
	{
		// TODO Auto-generated method stub
		View result = View.inflate( mContext , R.layout.effect_main , null );
		effectWorkspace = (GridView)( View.inflate( mContext , R.layout.lock_grid , null ) );
		effectWorkspace.setColumnWidth( Tools.dip2px( mContext , 86 ) );
		workspaceAdapter = new GridViewAdapter( mContext , INDEX_WORKSPACE );
		effectWorkspace.setAdapter( workspaceAdapter );
		effectWorkspace.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View v ,
					int position ,
					long id )
			{
				Intent it = new Intent( ACTION_EFFECT_PREVIEW );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_TYPE , INDEX_WORKSPACE );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_INDEX , position );
				mContext.sendBroadcast( it );
				ActivityManager.KillActivity();
			}
		} );
		effectApp = (GridView)View.inflate( mContext , R.layout.lock_grid , null );
		effectApp.setColumnWidth( Tools.dip2px( mContext , 86 ) );
		appAdaper = new GridViewAdapter( mContext , INDEX_APP );
		effectApp.setAdapter( appAdaper );
		// ViewPager
		effectGridPager = (ViewPager)result.findViewById( R.id.effectGridPager );
		effectPagerAdapter = new GridPagerAdapter( effectWorkspace , effectApp );
		effectGridPager.setAdapter( effectPagerAdapter );
		effectApp.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View v ,
					int position ,
					long id )
			{
				Intent it = new Intent( ACTION_EFFECT_PREVIEW );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_TYPE , INDEX_APP );
				it.putExtra( ACTION_EFFECT_PREVIEW_EXTRA_INDEX , position );
				mContext.sendBroadcast( it );
				ActivityManager.KillActivity();
			}
		} );
		effectGridPager.setOverScrollMode( View.OVER_SCROLL_NEVER );
		final RadioButton effectWorkspaceButton = (RadioButton)result.findViewById( R.id.btnWorkspaceEffect );
		final RadioButton effectAppButton = (RadioButton)result.findViewById( R.id.btnApplistEffect );
		effectGridPager.setOnPageChangeListener( new OnPageChangeListener() {
			
			@Override
			public void onPageScrollStateChanged(
					int arg0 )
			{
			}
			
			@Override
			public void onPageScrolled(
					int arg0 ,
					float arg1 ,
					int arg2 )
			{
			}
			
			@Override
			public void onPageSelected(
					int index )
			{
				if( index == INDEX_WORKSPACE )
				{
					effectWorkspaceButton.toggle();
				}
				else if( index == INDEX_APP )
				{
					effectAppButton.toggle();
				}
			}
		} );
		effectWorkspaceButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				effectGridPager.setCurrentItem( INDEX_WORKSPACE , true );
			}
		} );
		effectAppButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View arg0 )
			{
				effectGridPager.setCurrentItem( INDEX_APP , true );
			}
		} );
		if( index != -1 && ( index == 0 || index == 1 ) )
		{
			effectGridPager.setCurrentItem( index );
			effectWorkspace.setSelection( workspaceAdapter.getCurrentEffect() );
			effectApp.setSelection( appAdaper.getCurrentEffect() );
		}
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(
					Context context ,
					Intent intent )
			{
				// TODO Auto-generated method stub
				if( intent == null )
				{
					return;
				}
				String action = intent.getAction();
				if( DEFAULT_CHANGE.equals( action ) )
				{
					appAdaper.reloadDefault();
					workspaceAdapter.reloadDefault();
				}
			}
		};
		IntentFilter screenFilter1 = new IntentFilter();
		screenFilter1.addAction( DEFAULT_CHANGE );
		mContext.registerReceiver( mReceiver , screenFilter1 );
		return result;
	}
	
	public TabEffectFactory(
			Context context ,
			int index )
	{
		mContext = context;
		this.index = index;
	}
	
	public void setPagerIndex(
			int index )
	{
		effectGridPager.setCurrentItem( index );
	}
	
	private class GridPagerAdapter extends PagerAdapter
	{
		
		private final String LOG_TAG = "GridPagerAdapter";
		private GridView gridWorkspace;
		private GridView gridApp;
		
		public GridPagerAdapter(
				GridView workspace ,
				GridView app )
		{
			gridWorkspace = workspace;
			gridApp = app;
		}
		
		@Override
		public void destroyItem(
				ViewGroup container ,
				int position ,
				Object object )
		{
			Log.d( LOG_TAG , "destroyItem,pos" + position );
			container.removeViewAt( position );
		}
		
		@Override
		public int getCount()
		{
			return 2;
		}
		
		@Override
		public Object instantiateItem(
				ViewGroup container ,
				int position )
		{
			Log.d( LOG_TAG , "instantiateItem,pos=" + position );
			if( position == 0 )
			{
				container.addView( gridWorkspace );
				return gridWorkspace;
			}
			else
			{
				container.addView( gridApp );
				return gridApp;
			}
		}
		
		@Override
		public boolean isViewFromObject(
				View view ,
				Object object )
		{
			return view == ( object );
		}
		
		@Override
		public void restoreState(
				Parcelable state ,
				ClassLoader loader )
		{
		}
		
		@Override
		public Parcelable saveState()
		{
			return null;
		}
	}
	
	public void onDestory()
	{
		if( workspaceAdapter != null )
		{
			workspaceAdapter.onDestory();
		}
		if( appAdaper != null )
		{
			appAdaper.onDestory();
		}
		if( mReceiver != null )
			mContext.unregisterReceiver( mReceiver );
	}
	
	private class GridViewAdapter extends BaseAdapter
	{
		
		private Context mContext;
		private String[] list;
		private String path;
		private int current = 0;
		private int Tab = 0;
		//		private ArrayList<Bitmap> fileList = new ArrayList<Bitmap>();
		private LruCache<Integer , Bitmap> mMemoryCache;
		
		public GridViewAdapter(
				Context context ,
				int tab )
		{
			Tab = tab;
			mContext = context;
			if( tab == INDEX_WORKSPACE )
			{
				list = FunctionConfig.getWorkSpaceliststring();
				path = "launcher/effects/workspace/";
				current = Integer.parseInt( Assets.getEffect( context , "desktopeffects" ) );
			}
			else if( tab == INDEX_APP )
			{
				list = FunctionConfig.getAppliststring();
				path = "launcher/effects/applist/";
				current = Integer.parseInt( Assets.getEffect( context , "appeffects" ) );
			}
			int maxMemory = (int)Runtime.getRuntime().maxMemory();
			int cacheSize = maxMemory / 16;
			mMemoryCache = new LruCache<Integer , Bitmap>( cacheSize );
			//			fileList.clear();
			new PageItemTask().execute();
		}
		
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			if( list == null )
			{
				return 0;
			}
			return list.length;
		}
		
		public int getCurrentEffect()
		{
			return current;
		}
		
		public void reloadDefault()
		{
			if( Tab == INDEX_WORKSPACE )
			{
				current = Integer.parseInt( Assets.getEffect( mContext , "desktopeffects" ) );
			}
			else if( Tab == INDEX_APP )
			{
				current = Integer.parseInt( Assets.getEffect( mContext , "appeffects" ) );
			}
			notifyDataSetChanged();
		}
		
		@Override
		public Object getItem(
				int position )
		{
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public long getItemId(
				int position )
		{
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			// TODO Auto-generated method stub
			if( convertView == null )
				convertView = LayoutInflater.from( mContext ).inflate( R.layout.main_effect_item , null );
			ImageView item = (ImageView)convertView.findViewById( R.id.thumbnail );
			TextView tv = (TextView)convertView.findViewById( R.id.indication );
			ImageView fontUsed = (ImageView)convertView.findViewById( R.id.imageUsed );
			try
			{
				if( mMemoryCache.get( position ) != null )
					item.setImageBitmap( mMemoryCache.get( position ) );
			}
			catch( Exception e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( current == position )
			{
				fontUsed.setVisibility( View.VISIBLE );
			}
			else
			{
				fontUsed.setVisibility( View.GONE );
			}
			tv.setText( list[position] );
			return convertView;
		}
		
		public void onDestory()
		{
			for( int i = 0 ; i < mMemoryCache.size() ; i++ )
			{
				Bitmap bmp = mMemoryCache.get( i );
				if( bmp != null && !bmp.isRecycled() )
				{
					bmp.recycle();
					bmp = null;
				}
			}
		}
		
		public class PageItemTask extends AsyncTask<String , Integer , Bitmap>
		{
			
			public PageItemTask()
			{
			}
			
			@Override
			protected void onPostExecute(
					Bitmap result )
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
			protected Bitmap doInBackground(
					String ... params )
			{
				// TODO Auto-generated method stub
				String file = null;
				for( int i = 0 ; i < getCount() ; i++ )
				{
					if( FunctionConfig.isPage_effect_no_radom_style() && i != 0 )
					{
						file = path + ( i + 1 ) + ".png";
					}
					else
					{
						file = path + i + ".png";
					}
					try
					{
						Bitmap bmp = Tools.getPurgeableBitmap( mContext.getAssets().open( file ) , -1 , -1 );//BitmapFactory.decodeStream( mContext.getAssets().open( file ) );
						//						fileList.add( i , bmp );
						mMemoryCache.put( i , bmp );
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
				return null;
			}
		}
	}
}
