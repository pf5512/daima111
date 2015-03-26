package com.iLoong.scan;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class HistoryActivity extends Activity
{
	
	private ImageView iv_back;
	private ListView lv_history;
	//private ImageView iv_advertisement;
	private ImageButton ib_download;
	private RelativeLayout rl_ad;
	private LinearLayoutListView lll;
	private HistoryListAdapter adapter;
	private DBHelper dbHelper;
	private List<HistoryBean> historyList;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.test );
		dbHelper = new DBHelper( getApplicationContext() );
		findViewById();
		setListener();
		historyList = new ArrayList<HistoryBean>();
		Cursor c = dbHelper.query();
		while( c.moveToNext() )
		{
			HistoryBean bean = new HistoryBean();
			bean.set_id( c.getInt( c.getColumnIndex( DBHelper.COLUMN_NAME_ID ) ) );
			bean.setText( c.getString( c.getColumnIndex( DBHelper.COLUMN_NAME_CODE ) ) );
			bean.setType( c.getInt( c.getColumnIndex( DBHelper.COLUMN_NAME_TYPE ) ) );
			bean.setCurrtime( c.getString( c.getColumnIndex( DBHelper.COLUMN_NAME_TIME ) ) );
			historyList.add( bean );
		}
		adapter = new HistoryListAdapter( HistoryActivity.this , historyList );
		lll.setAdapter( adapter );
		//lv_history.setAdapter( adapter );
	}
	
	private void findViewById()
	{
		iv_back = (ImageView)findViewById( R.id.iv_back );
		//lv_history = (ListView)findViewById( R.id.lv_history );
		lll = (LinearLayoutListView)findViewById( R.id.lll );
		//iv_advertisement = (ImageView)findViewById( R.id.iv_advertisement );
		ib_download = (ImageButton)findViewById( R.id.ib_download );
		rl_ad = (RelativeLayout)findViewById( R.id.rl_ad );
	}
	
	private void setListener()
	{
		iv_back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				HistoryActivity.this.finish();
			}
		} );
		ib_download.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				downloadLauncher();
			}
		} );
		rl_ad.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				downloadLauncher();
			}
		} );
	}
	
	private void downloadLauncher()
	{
		Uri playUri = Uri.parse( "https://play.google.com/store/apps/details?id=com.cooeeui.turbolauncher" );
		Intent browserIntent = new Intent( Intent.ACTION_VIEW , playUri );
		if( isPlayStoreInstalled() )
		{
			browserIntent.setClassName( "com.android.vending" , "com.android.vending.AssetBrowserActivity" );
		}
		browserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( browserIntent );
	}
	
	private boolean isPlayStoreInstalled()
	{
		String playPkgName = "com.android.vending";
		try
		{
			PackageInfo pckInfo = getPackageManager().getPackageInfo( playPkgName , PackageManager.GET_ACTIVITIES );
			return true;
		}
		catch( NameNotFoundException e )
		{
			e.printStackTrace();
			return false;
		}
	}
}
