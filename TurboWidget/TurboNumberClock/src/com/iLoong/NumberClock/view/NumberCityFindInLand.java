package com.iLoong.NumberClock.view;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.iLoong.NumberClock.R;
import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.CooeeClient;
import com.iLoong.NumberClock.common.MyToast;
import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.WeatherEntity;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class NumberCityFindInLand extends Activity
{
	
	EditText citytext = null;
	ListView listview = null;
	private CityAdapter adpt;
	private static final String DB_PATH = "/data/data/com.cooeeui.brand.turbolauncher/databases/";
	private static final String DB_NAME = "city_db.db";
	private String PATH = iLoongLauncher.getInstance().getFilesDir() + File.separator + "numberclock" + File.separator + "listinland.dat";
	private SQLiteDatabase CitysDb;
	private static final String TABLE_CITYS = "CITY_LIST";
	private SharedPreferences sharepreference = null;
	public static Handler mHandler = null;
	public static final int MSG_SUCCESS = 5;
	public static final int MSG_FAILURE = 6;
	ImageView inlandsearchcity = null;
	private CustomProcessDialog progressDialog = null;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.inlandcityfinder );
		if( VERSION.SDK_INT > 11 )
			setFinishOnTouchOutside( true );
		
		
		sharepreference = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		mHandler = new Handler() {
			public void handleMessage(
					final Message msg )
			{
				switch( msg.what )
				{
					case MSG_SUCCESS:
						Bundle bundles = (Bundle)msg.obj;
						WeatherEntity weathers = (WeatherEntity)bundles.getSerializable( "weatherdataentity" );
						
						Editor editor = sharepreference.edit();
						editor.putString( "currentnumbercityname" , weathers.getCity() );
						editor.commit();
						
						List<String> list = new ArrayList<String>();
						list.add( weathers.getCity() );
						List<String> listdate = GetData();
						if( listdate.size() != 0 )
						{
							for( int i = 0 ; i < listdate.size() ; i++ )
							{
								if( list.size() < 10 )
								{
									boolean ifExist = false;
									for( int j = 0 ; j < list.size() ; j++ )
									{
										if( list.get( j ).equals( listdate.get( i ) ) )
										{
											ifExist = true;
											break;
										}
									}
									if( !ifExist )
									{
										list.add( listdate.get( i ) );
									}
								}
								else
								{
									break;
								}
							}
						}
						saveData( list );
						
						
						Intent intents = new Intent();
						intents.setAction( "com.iLoong.numberclock.inlandsearch" );
						Bundle myBundle = new Bundle();
						myBundle.putSerializable( "weatherdataentity" , weathers );
						intents.putExtras( myBundle );
						sendBroadcast( intents );
						
						if( progressDialog != null )
						{
							progressDialog.dismiss();
						}
						finish();
						break;
					case MSG_FAILURE:
						if( progressDialog != null )
						{
							progressDialog.dismiss();
						}
						MyToast.getToast( NumberCityFindInLand.this , getString( R.string.networt_notconnetederror)).show();
						break;
				}
			}
		};
		
		copyDatabase( this );
		CitysDb = SQLiteDatabase.openDatabase( DB_PATH + DB_NAME , null , SQLiteDatabase.OPEN_READONLY );
		adpt = new CityAdapter( this , null );
		citytext = (EditText)this.findViewById( R.id.inlandedtCity );
		listview = (ListView)this.findViewById( R.id.inlandlistview );
		
		String currentString = sharepreference.getString( "currentnumbercityname" , null );
		if( currentString != null )
		{
			ArrayList<String> list = GetData();
			List<CityResult> crlist=new ArrayList<CityResult>();
			for( int j = 0 ; j < list.size() ; j++ )
			{
				CityResult cr=new CityResult();
				cr.setCityName( list.get( j ) );
				crlist.add( cr );
			}
			adpt = new CityAdapter( this , crlist );
		}
		
		citytext.clearFocus();
		( (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE ) ).showSoftInput( citytext , InputMethodManager.SHOW_FORCED );
		citytext.addTextChangedListener( filterTextWatcher );
		listview.setAdapter( adpt );
		listview.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(
					AdapterView<?> parent ,
					View view ,
					int position ,
					long id )
			{
				if( NumberClockHelper.isHaveInternet( NumberCityFindInLand.this ) )
				{
					final CityResult result = (CityResult)parent.getItemAtPosition( position );
					progressDialog = CustomProcessDialog.createDialog( NumberCityFindInLand.this );
					progressDialog.show();
					new Thread( new Runnable() {
						
						@Override
						public void run()
						{
							CooeeClient.getWeatherInfo( NumberCityFindInLand.this , result.getCityName(),1 );
						}
					} ).start();
				}
				else
				{
					NumberCityFindInLand.this.runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							MyToast.getToast( NumberCityFindInLand.this , getString( R.string.networt_notconneted)).show();
						}
					} );
				}
			}
		} );
		inlandsearchcity = (ImageView)findViewById( R.id.inlandsearchcity );
		inlandsearchcity.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(
					View v )
			{
				if( adpt.getCount() > 0 )
				{
					if( NumberClockHelper.isHaveInternet( NumberCityFindInLand.this ) )
					{
						final CityResult result = (CityResult)adpt.getItem( 0 );
						progressDialog = CustomProcessDialog.createDialog( NumberCityFindInLand.this );
						progressDialog.show();
						new Thread( new Runnable() {
							@Override
							public void run()
							{
								CooeeClient.getWeatherInfo( NumberCityFindInLand.this , result.getCityName() ,1);
							}
						} ).start();
					}
					else
					{
						runOnUiThread( new Runnable() {
							
							@Override
							public void run()
							{
								MyToast.getToast( NumberCityFindInLand.this , getString( R.string.networt_notconneted ) ).show();
							}
						} );
					}
				}else{
//					MyToast.getToast( NumberCityFindInLand.this , getString( R.string.city_notfound)).show();
					runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							Toast.makeText(NumberCityFindInLand.this , getString( R.string.city_notfound) , Toast.LENGTH_SHORT ).show();
						}
					} );
					
				}
			}
		} );
	}
	
	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(
				Editable s )
		{
		}
		public void beforeTextChanged(
				CharSequence s ,
				int start ,
				int count ,
				int after )
		{
		}
		
		public void onTextChanged(
				CharSequence s ,
				int start ,
				int before ,
				int count )
		{
			adpt.getFilter().filter( s );
			adpt.notifyDataSetChanged();
		}
	};
	
	private class CityAdapter extends ArrayAdapter<CityResult> implements Filterable
	{
		
		private Context ctx;
		private List<CityResult> cityList = new ArrayList<CityResult>();
		
		public CityAdapter(
				Context ctx ,
				List<CityResult> cityList )
		{
			super( ctx , R.layout.cityresult_layout , cityList );
			this.cityList = cityList;
			this.ctx = ctx;
		}
		
		@Override
		public CityResult getItem(
				int position )
		{
			if( cityList != null )
				return cityList.get( position );
			return null;
		}
		
		@Override
		public int getCount()
		{
			if( cityList != null )
				return cityList.size();
			return 0;
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			View result = convertView;
			if( result == null )
			{
				LayoutInflater inf = (LayoutInflater)ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				result = inf.inflate( R.layout.cityresult_layout , parent , false );
			}
			TextView tv = (TextView)result.findViewById( R.id.txtCityName );
			tv.setText( cityList.get( position ).getCityName() );
			return result;
		}
		
		@Override
		public long getItemId(
				int position )
		{
			if( cityList != null )
				return cityList.get( position ).hashCode();
			return 0;
		}
		
		@Override
		public Filter getFilter()
		{
			Filter cityFilter = new Filter() {
				
				@Override
				protected FilterResults performFiltering(
						CharSequence constraint )
				{
					FilterResults results = new FilterResults();
					if( constraint == null || constraint.length() < 1 )
						return results;
					String selection = null;
					selection = CityResult.NAME + " LIKE " + "'%" + constraint + "%'";
					Cursor cursor = queryCitys( CityResult.projection , selection , null , null );
					List<CityResult> list = new ArrayList<CityResult>();
					if( cursor != null && cursor.getCount() > 0 )
					{
						while( cursor.moveToNext() )
						{
							CityResult cityresult = new CityResult();
							cityresult.setCityName( cursor.getString( cursor.getColumnIndex( "city" ) ) );
							list.add( cityresult );
						}
						cursor.close();
					}
					results.values = list;
					results.count = list.size();
					return results;
				}
				
				@Override
				protected void publishResults(
						CharSequence constraint ,
						FilterResults results )
				{
					cityList = (List)results.values;
					notifyDataSetChanged();
				}
			};
			return cityFilter;
		}
	}
	
	private static void copyDatabase(
			Context context )
	{
		// 拷贝assets中的数据�?
		String outFileName;
		InputStream myInput;
		if( checkDataBase() == false )
		{
			try
			{
				myInput = context.getAssets().open( DB_NAME );
				outFileName = DB_PATH + DB_NAME;
				File f = new File( DB_PATH );
				if( !f.exists() )
				{
					f.mkdirs();
				}
				f = new File( outFileName );
				if( !f.exists() )
				{
					f.createNewFile();
				}
				OutputStream myOutput = new FileOutputStream( outFileName );
				byte[] buffer = new byte[1024];
				int length;
				while( ( length = myInput.read( buffer ) ) > 0 )
				{
					myOutput.write( buffer , 0 , length );
				}
				myOutput.flush();
				myOutput.close();
				myInput.close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private static boolean checkDataBase()
	{
		String myPath = DB_PATH + DB_NAME;
		File dbFile = new File( myPath );
		if( dbFile != null && dbFile.exists() )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private Cursor queryCitys(
			String[] projection ,
			String selection ,
			String[] selectionArgs ,
			String sortOrder )
	{
		copyDatabase( this );
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String limit = null;
		qb.setTables( TABLE_CITYS );
		return qb.query( CitysDb , projection , selection , selectionArgs , null , null , sortOrder , limit );
	}
	
	private void saveData(
			List<String> list )
	{
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try
		{
			//存入数据
			File file = new File( PATH );
			if( file.exists() )
			{
				file.delete();
			}
			if( !file.getParentFile().exists() )
			{
				file.getParentFile().mkdirs();
			}
			if( !file.exists() )
			{
				file.createNewFile();
			}
			fileOutputStream = new FileOutputStream( file.toString() );
			objectOutputStream = new ObjectOutputStream( fileOutputStream );
			objectOutputStream.writeObject( list );
		}
		catch( Exception e )
		{
		}
		finally
		{
			if( objectOutputStream != null )
			{
				try
				{
					objectOutputStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			if( fileOutputStream != null )
			{
				try
				{
					fileOutputStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private ArrayList<String> GetData()
	{
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		ArrayList<String> savedArrayList = new ArrayList<String>();
		try
		{
			//存入数据
			File file = new File( PATH );
			if( !file.exists() )
			{
				return savedArrayList;
			}
			else
			{
				fileInputStream = new FileInputStream( file.toString() );
				objectInputStream = new ObjectInputStream( fileInputStream );
				savedArrayList = (ArrayList<String>)objectInputStream.readObject();
				return savedArrayList;
			}
		}
		catch( Exception e )
		{
		}
		finally
		{
			if( objectInputStream != null )
			{
				try
				{
					objectInputStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
			if( fileInputStream != null )
			{
				try
				{
					fileInputStream.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
