package com.iLoong.NumberClock.view;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.iLoong.NumberClock.R;
import com.iLoong.NumberClock.common.CityResult;
import com.iLoong.NumberClock.common.MyToast;
import com.iLoong.NumberClock.common.NumberClockHelper;
import com.iLoong.NumberClock.common.Weather;
import com.iLoong.NumberClock.common.YahooClient;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class NumberCityFinderActivity extends Activity
{
	
	private EditText edt;
	private ImageView searchcity;
	private ImageView sheshidu;
	private ImageView huashi;
	private ListView listview;
	private CityAdapter adpt;
	private SharedPreferences sharedPref;
	private String PATH = iLoongLauncher.getInstance().getFilesDir() + File.separator + "numberclock" + File.separator + "listforeign.dat";
	public static Handler mHandler = null;
	public static final int MSG_SUCCESS = 0;//获取图片成功的标识  
	public static final int MSG_FAILURE = 1;//获取图片失败的标识  
	public static final int MSG_TOASTREQUESTFAILED=2;
	public static final int MSG_TOASTCONNECTEDERROR=3;
	public static final int MSG_TOASTNOTCONNECTED=4;
	private CustomProcessDialog progressDialog = null;
	
	@Override
	public void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.cityfinder_layout );
		if( VERSION.SDK_INT > 11 )
			setFinishOnTouchOutside( true );
		mHandler = new Handler() {
			
			public void handleMessage(
					final Message msg )
			{
				switch( msg.what )
				{
					case MSG_SUCCESS:
						Bundle bundle = (Bundle)msg.obj;
						Weather weather = (Weather)bundle.getSerializable( "mynumberweatherinfo" );
						CityResult result = (CityResult)bundle.getSerializable( "mynumberresult" );
						SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
						Editor editor = sharedPref.edit();
						editor.putString( "currentnumberweatherId" , result.getWoeid() );
						editor.putString( "currentnumberweathercityname" , result.getCityName() );
						editor.putString( "currentnumberweatherCountry" , result.getCountry() );
						editor.commit();
						List<CityResult> list = new ArrayList<CityResult>();
						list.add( result );
						List<CityResult> listdate = GetData();
						if( listdate.size() != 0 )
						{
							for( int i = 0 ; i < listdate.size() ; i++ )
							{
								if( list.size() < 10 )
								{
									boolean ifExist = false;
									for( int j = 0 ; j < list.size() ; j++ )
									{
										if( list.get( j ).getWoeid().equals( listdate.get( i ).getWoeid() ) )
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
						Intent intent = new Intent();
						intent.setAction( "com.iLoong.numberclock.forsearch" );
						Bundle mBundle = new Bundle();
						mBundle.putSerializable( "numberweatherinfo" , weather );
						intent.putExtras( mBundle );
						sendBroadcast( intent );
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
						MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.networt_notconnetederror)).show();
						break;
					case MSG_TOASTREQUESTFAILED:
						MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.requestfailed)).show();
						break;
					case MSG_TOASTCONNECTEDERROR:
						MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.networt_notconnetederror)).show();
						break;
					case MSG_TOASTNOTCONNECTED:
						MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.networt_notconneted)).show();
						break;
				}
			}
		};
		sharedPref = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		adpt = new CityAdapter( this , null );
		edt = (EditText)this.findViewById( R.id.edtCity );
		listview = (ListView)findViewById( R.id.listview );
		String currentString = sharedPref.getString( "currentnumberweathercityname" , null );
		if( currentString != null )
		{
			//			edt.setText( currentString );
			ArrayList<CityResult> list = GetData();
			adpt = new CityAdapter( this , list );
		}
		listview.setAdapter( adpt );
		edt.addTextChangedListener( filterTextWatcher );
		searchcity = (ImageView)findViewById( R.id.searchcity );
		sheshidu = (ImageView)findViewById( R.id.sheshidu );
		sheshidu.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				sheshidu.setImageResource( R.drawable.wc_sheshiclick );
				huashi.setImageResource( R.drawable.wc_huashi );
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString( "numbertmpType" , "c" );
				editor.commit();
			}
		} );
		huashi = (ImageView)findViewById( R.id.huashi );
		huashi.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				sheshidu.setImageResource( R.drawable.wc_sheshidu );
				huashi.setImageResource( R.drawable.wc_huashiclick );
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString( "numbertmpType" , "f" );
				editor.commit();
			}
		} );
		String tmpType = sharedPref.getString( "numbertmpType" , "f" );
		if( tmpType.equals( "c" ) )
		{
			sheshidu.setImageResource( R.drawable.wc_sheshiclick );
			huashi.setImageResource( R.drawable.wc_huashi );
		}
		else
		{
			sheshidu.setImageResource( R.drawable.wc_sheshidu );
			huashi.setImageResource( R.drawable.wc_huashiclick );
		}
		listview.setOnItemClickListener( new OnItemClickListener() {
			
			@Override
			public void onItemClick(
					final AdapterView<?> parent ,
					View view ,
					int position ,
					long id )
			{
				if( NumberClockHelper.isHaveInternet( NumberCityFinderActivity.this ) )
				{
					final String tmpType = sharedPref.getString( "numbertmpType" , "f" );
					final CityResult result = (CityResult)parent.getItemAtPosition( position );
					progressDialog = CustomProcessDialog.createDialog( NumberCityFinderActivity.this );
					progressDialog.show();
					new Thread( new Runnable() {
						
						@Override
						public void run()
						{
							YahooClient.getWeatherInfo( result , tmpType , NumberCityFinderActivity.this , 1 );
						}
					} ).start();
				}
				else
				{
					NumberCityFinderActivity.this.runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.networt_notconneted)).show();
						}
					} );
				}
			}
		} );
		searchcity.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(
					View v )
			{
				if( adpt.getCount() > 0 )
				{
					if( NumberClockHelper.isHaveInternet( NumberCityFinderActivity.this ) )
					{
						final String tmpType = sharedPref.getString( "numbertmpType" , "f" );
						final CityResult result = adpt.getItem( 0 );
						progressDialog = CustomProcessDialog.createDialog( NumberCityFinderActivity.this );
						progressDialog.show();
						new Thread( new Runnable() {
							
							@Override
							public void run()
							{
								YahooClient.getWeatherInfo( result , tmpType , NumberCityFinderActivity.this , 1 );
							}
						} ).start();
					}
					else
					{
						runOnUiThread( new Runnable() {
							
							@Override
							public void run()
							{
								MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.networt_notconneted)).show();
							}
						} );
					}
				}
				else
				{
//					MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.city_notfound)).show();
					runOnUiThread( new Runnable() {
						
						@Override
						public void run()
						{
							Toast.makeText(NumberCityFinderActivity.this , getString( R.string.city_notfound) , Toast.LENGTH_SHORT ).show();
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
			tv.setText( cityList.get( position ).getCityName() + "," + cityList.get( position ).getCountry() );
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
					if( NumberClockHelper.isHaveInternet( getContext() ) )
					{
						List<CityResult> cityResultList = YahooClient.getCityList( constraint.toString() , getContext() );
						if( cityResultList.size() != 0 )
						{
							results.values = cityResultList;
							results.count = cityResultList.size();
						}
						return results;
					}
					else
					{
						NumberCityFinderActivity.this.runOnUiThread( new Runnable() {
							
							@Override
							public void run()
							{
								MyToast.getToast( NumberCityFinderActivity.this , getString( R.string.networt_notconneted)).show();
							}
						} );
						return results;
					}
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
	
	private void saveData(
			List<CityResult> list )
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
	
	private ArrayList<CityResult> GetData()
	{
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		ArrayList<CityResult> savedArrayList = new ArrayList<CityResult>();
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
				savedArrayList = (ArrayList<CityResult>)objectInputStream.readObject();
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
	
	@Override
	protected void onStop()
	{
		if( !sharedPref.getString( "currentType" , "f" ).equals( sharedPref.getString( "numbertmpType" , "f" ) ) )
		{
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					String weatherid = sharedPref.getString( "currentnumberweatherId" , null );
					String weathername = sharedPref.getString( "currentnumberweathercityname" , null );
					String weathercountry = sharedPref.getString( "currentnumberweatherCountry" , null );
					CityResult cityresult = new CityResult();
					cityresult.setWoeid( weatherid );
					cityresult.setCityName( weathername );
					cityresult.setCountry( weathercountry );
					if( weatherid != null )
					{
						YahooClient.getWeatherInfo( cityresult , sharedPref.getString( "numbertmpType" , "f" ) , NumberCityFinderActivity.this , 2 );
						//						if( weather != null && weather.getList() != null && weather.getList().size() == 5 )
						//						{
						//							Intent intent = new Intent();
						//							intent.setAction( "com.iLoong.weatherclock.search" );
						//							Bundle mBundle = new Bundle();
						//							mBundle.putSerializable( "weatherinfo" , weather );
						//							intent.putExtras( mBundle );
						//							sendBroadcast( intent );
						//						}
					}
				}
			} ).start();
		}
		super.onStop();
	}
	/** 
	 * 将焦点放在输入框中 
	 * 如果想要选中输入框中的文本必须要将焦点放在输入框中 
	 * 如果想要焦点在输入框中必须设置下面三个方法 
	 * @param editText 
	 */
	//    private void setEditFocus(EditText editText){  
	//        editText.setFocusable(true);  
	//        editText.setFocusableInTouchMode(true);  
	//        editText.requestFocus();  
	//    }  
}
