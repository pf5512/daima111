package com.cooee.searchbar;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.iLoong.launcher.Desktop3D.Log;


public class GoogleSearchActivity extends Activity implements OnEditorActionListener
{
	
	private static final String GOOGLE_WEB = "https://www.google.com/search?q=";
	private EditText et_key_input;
	private Button btn_search;
	private ListView lv_history;
	private DBHelper dbHelper;
	private List<HistoryBean> historyList;
	private HistoryAdapter adapter;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.search_bar_activity_main );
		dbHelper = new DBHelper( getApplicationContext() );
		findViewById();
		setListener();
		historyList = new ArrayList<HistoryBean>();
		Cursor c = dbHelper.query();
		while( c.moveToNext() )
		{
			HistoryBean bean = new HistoryBean();
			bean.set_id( c.getInt( c.getColumnIndex( DBHelper.COLUMN_NAME_ID ) ) );
			bean.setKeywords( c.getString( c.getColumnIndex( DBHelper.COLUMN_NAME_KEY ) ) );
			bean.setUrl( c.getString( c.getColumnIndex( DBHelper.COLUMN_NAME_URL ) ) );
			historyList.add( bean );
		}
		adapter = new HistoryAdapter( this , historyList );
		lv_history.setAdapter( adapter );
	}
	
	private void findViewById()
	{
		et_key_input = (EditText)findViewById( R.id.et_key_input );
		lv_history = (ListView)findViewById( R.id.lv_history );
		btn_search = (Button)findViewById( R.id.btn_search );
	}
	
	private void setListener()
	{
		et_key_input.setOnEditorActionListener( this );
		btn_search.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( TextUtils.isEmpty( et_key_input.getText().toString().trim() ) )
				{
					Toast.makeText( GoogleSearchActivity.this , R.string.nothing_input_tips , Toast.LENGTH_SHORT ).show();
					return;
				}
				doSearch( et_key_input.getText().toString().trim() );
			}
		} );
		lv_history.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					AdapterView<?> arg0 ,
					View arg1 ,
					int arg2 ,
					long arg3 )
			{
				Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( historyList.get( arg2 ).getUrl() ) );
				startActivity( intent );
			}
		} );
	}
	
	@Override
	public boolean onCreateOptionsMenu(
			Menu menu )
	{
		getMenuInflater().inflate( R.menu.search_bar_menu_main , menu );
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(
			MenuItem item )
	{
		switch( item.getOrder() )
		{
			case 100:
				dbHelper.del();
				historyList.clear();
				adapter.notifyDataSetChanged();
				break;
			case 101:
				GoogleSearchActivity.this.finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected( item );
	}
	
	@Override
	public boolean onEditorAction(
			TextView v ,
			int actionId ,
			KeyEvent event )
	{
		if( TextUtils.isEmpty( v.getText().toString().trim() ) )
		{
			Toast.makeText( GoogleSearchActivity.this , R.string.nothing_input_tips , Toast.LENGTH_SHORT ).show();
			return false;
		}
		switch( actionId )
		{
			case EditorInfo.IME_NULL:
				break;
			case EditorInfo.IME_ACTION_DONE:
				doSearch( v.getText().toString().trim() );
				break;
		}
		return false;
	}
	
	private void doSearch(
			String keywords )
	{
		boolean repeat = false;
		for( int i = 0 ; i < historyList.size() ; i++ )
		{
			if( keywords.equals( historyList.get( i ).getKeywords() ) )
			{
				repeat = true;
			}
		}
//		SearchEngineParams sep = new SearchEngineParams();
//		String url = sep.getSearchAddr( this , keywords );
		String url = GOOGLE_WEB + keywords;
		Intent intent = new Intent( Intent.ACTION_VIEW , Uri.parse( url ) );
//		Log.i( "url" , url );
		if( !repeat )
		{
			ContentValues cv = new ContentValues();
			cv.put( DBHelper.COLUMN_NAME_KEY , keywords );
			cv.put( DBHelper.COLUMN_NAME_URL , url );
			dbHelper.insert( cv );
			HistoryBean bean = new HistoryBean();
			bean.setKeywords( keywords );
			bean.setUrl( url );
			historyList.add( bean );
			adapter.notifyDataSetChanged();
		}
		startActivity( intent );
	}
	
	@Override
	protected void onDestroy()
	{
		if( dbHelper != null )
		{
			//			dbHelper.close();
		}
		super.onDestroy();
	}
}
