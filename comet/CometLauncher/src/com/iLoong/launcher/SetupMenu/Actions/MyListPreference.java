package com.iLoong.launcher.SetupMenu.Actions;


import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cooeecomet.launcher.R;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.activity.AdActivity;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.thirdParty.analytics.umeng.UmengMobclickAgent;
import com.umeng.analytics.MobclickAgent;


public class MyListPreference extends ListPreference
{
	
	public static HashMap<String , String> map = new HashMap<String , String>();
	private CharSequence[] data;
	public Context c;
	public static int selectedItem;
	private SharedPreferences mPreferences;
	private String key;
	private static final String desktopkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_desktopeffects );
	private static final String appkey = SetupMenu.getContext().getResources().getString( RR.string.setting_key_appeffects );
	private static final String dock_row_key = SetupMenu.getContext().getResources().getString( RR.string.dock_row_key );
	private Adpter adpter;
	private static final int DESKTOP_EFFECTS_NUMS = R3D.workSpace_list_string.length;
	private static final int APP_EFFECTS_NUMS = R3D.app_list_string.length;
	private static final int DOCK_ROW_NUMS = 4;
	private static final int DESKTOP_PRO_NUM = 5;
	public static final int APP_PRO_NUM = 10;
	private static final int DOCK_ROW_NUM = 2;
	private static boolean[] desktopEffectsUses = new boolean[DESKTOP_EFFECTS_NUMS];
	public static boolean[] appEffectsUses = new boolean[APP_EFFECTS_NUMS];
	private static boolean[] dockRowUses = new boolean[DOCK_ROW_NUMS];
	private static final String DESKTOP_EFFECTS_USED = "desktopEffectsUsed";
	private static final String APP_EFFECTS_USED = "appEffectsUsed";
	private static final String DOCK_ROW_USED = "dockRowUsed";
	private ImageView img_new , img_pro;
	static
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
		for( int i = 0 ; i < DESKTOP_EFFECTS_NUMS ; i++ )
		{
			desktopEffectsUses[i] = preferences.getBoolean( DESKTOP_EFFECTS_USED + i , false );
		}
		for( int i = 0 ; i < APP_EFFECTS_NUMS ; i++ )
		{
			appEffectsUses[i] = preferences.getBoolean( APP_EFFECTS_USED + i , false );
		}
		for( int i = 0 ; i < DOCK_ROW_NUMS ; i++ )
		{
			dockRowUses[i] = preferences.getBoolean( DOCK_ROW_USED + i , false );
		}
	}
	
	public MyListPreference(
			Context context ,
			AttributeSet attrs )
	{
		super( context , attrs );
		setLayoutResource( R.layout.app_summary_item );
		c = context;
		data = getEntries();
		key = getKey();
		adpter = new Adpter();
		mPreferences = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );//c.getSharedPreferences( "com.cooeecomet.launcher_preferences" , Context.MODE_PRIVATE );
		selectedItem = Integer.valueOf( mPreferences.getString( key , "0" ) );
	}
	
	@Override
	protected void onBindView(
			View view )
	{
		super.onBindView( view );
		img_new = (ImageView)view.findViewById( R.id.img_new_activity );
		img_pro = (ImageView)view.findViewById( R.id.img_pro_activity );
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
		{
			img_pro.setVisibility( View.INVISIBLE );
			if( showNew() )
			{
				img_new.setVisibility( View.VISIBLE );
			}
			else
			{
				img_new.setVisibility( View.INVISIBLE );
			}
		}
		else
		{
			img_pro.setVisibility( View.VISIBLE );
		}
		TextView title = (TextView)view.findViewById( R.id.txt_title );
		title.setText( getTitle() );
		TextView summary = (TextView)view.findViewById( R.id.txt_summary );
		summary.setText( getSummary() );
	}
	
	private boolean showNew()
	{
		if( getKey().equals( MyListPreference.desktopkey ) )
		{
			for( int i = 0 ; i < MyListPreference.DESKTOP_EFFECTS_NUMS ; i++ )
			{
				if( i > MyListPreference.DESKTOP_PRO_NUM && false == MyListPreference.desktopEffectsUses[i] )
				{
					return true;
				}
			}
			return false;
		}
		else if( getKey().equals( MyListPreference.appkey ) )
		{
			for( int i = 0 ; i < MyListPreference.APP_EFFECTS_NUMS ; i++ )
			{
				if( i > MyListPreference.APP_PRO_NUM && false == MyListPreference.appEffectsUses[i] )
				{
					return true;
				}
			}
			return false;
		}
		else if( getKey().equals( MyListPreference.dock_row_key ) )
		{
			for( int i = 0 ; i < MyListPreference.DOCK_ROW_NUMS ; i++ )
			{
				if( i != MyListPreference.DOCK_ROW_NUM && false == MyListPreference.dockRowUses[i] )
				{
					return true;
				}
			}
			return false;
		}
		else
		{
			Log.e( "123" , "key error" );
			return false;
		}
	}
	
	@Override
	protected void onPrepareDialogBuilder(
			Builder builder )
	{
		selectedItem = Integer.valueOf( mPreferences.getString( key , "0" ) );
		super.onPrepareDialogBuilder( builder );
		builder.setSingleChoiceItems( adpter , selectedItem , new DialogInterface.OnClickListener() {
			
			public void onClick(
					DialogInterface dialog ,
					int which )
			{
				MyListPreference.this.onClick( dialog , DialogInterface.BUTTON_POSITIVE );
				if( key.equals( desktopkey ) )
				{
					if( which > DESKTOP_PRO_NUM )
					{
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
						if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
						{
							selectedItem = which;
							dialog.dismiss();
							if( selectedItem >= 0 && data != null )
							{
								String value = data[selectedItem].toString();
								MyListPreference.this.setSummary( value );
							}
						}
						else
						{
							if( Utils3D.isUpgradePacketInstalled() )
							{
								Intent intent = new Intent();
								intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
								( (Activity)c ).startActivityForResult( intent , 1 );
							}
							else
							{
								if( data != null )
								{
									if( which < data.length )
									{
										map.clear();
										map.put( "desktopeffects" , data[which].toString() );
										MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , map );
									}
								}
								Intent intent = new Intent( iLoongLauncher.getInstance().getApplicationContext() , AdActivity.class );
								SendMsgToAndroid.startActivity( intent );
							}
						}
					}
					else
					{
						dialog.dismiss();
						selectedItem = which;
						if( selectedItem >= 0 && data != null )
						{
							String value = data[selectedItem].toString();
							MyListPreference.this.setSummary( value );
						}
					}
				}
				else if( key.equals( appkey ) )
				{
					if( which > APP_PRO_NUM )
					{
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
						if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
						{
							selectedItem = which;
							dialog.dismiss();
							if( selectedItem >= 0 && data != null )
							{
								String value = data[selectedItem].toString();
								MyListPreference.this.setSummary( value );
							}
						}
						else
						{
							if( Utils3D.isUpgradePacketInstalled() )
							{
								Intent intent = new Intent();
								intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
								( (Activity)c ).startActivityForResult( intent , 1 );
							}
							else
							{
								if( data != null )
								{
									if( which < data.length )
									{
										map.clear();
										map.put( "appeffects" , data[which].toString() );
										MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , map );
									}
								}
								Intent intent = new Intent( iLoongLauncher.getInstance().getApplicationContext() , AdActivity.class );
								SendMsgToAndroid.startActivity( intent );
							}
						}
					}
					else
					{
						dialog.dismiss();
						selectedItem = which;
						if( selectedItem >= 0 && data != null )
						{
							String value = data[selectedItem].toString();
							MyListPreference.this.setSummary( value );
						}
					}
				}
				else if( key.equals( dock_row_key ) )
				{
					if( which != DOCK_ROW_NUM )
					{
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
						if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
						{
							selectedItem = which;
							dialog.dismiss();
							if( selectedItem >= 0 && data != null )
							{
								String value = data[selectedItem].toString();
								MyListPreference.this.setSummary( value );
							}
						}
						else
						{
							if( Utils3D.isUpgradePacketInstalled() )
							{
								Intent intent = new Intent();
								intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
								( (Activity)c ).startActivityForResult( intent , 1 );
							}
							else
							{
								if( data != null )
								{
									if( which < data.length )
									{
										map.clear();
										map.put( "dock_row" , data[which].toString() );
										MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , map );
									}
								}
								Intent intent = new Intent( iLoongLauncher.getInstance().getApplicationContext() , AdActivity.class );
								SendMsgToAndroid.startActivity( intent );
							}
						}
					}
					else
					{
						dialog.dismiss();
						selectedItem = which;
						if( selectedItem >= 0 && data != null )
						{
							String value = data[selectedItem].toString();
							MyListPreference.this.setSummary( value );
						}
					}
				}
			}
		} );
	}
	
	public void update()
	{
		if( adpter != null )
		{
			adpter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onDialogClosed(
			boolean positiveResult )
	{
		if( callChangeListener( selectedItem ) )
		{
			setValue( selectedItem + "" );
			if( key.equals( desktopkey ) )
			{
				desktopEffectsUses[selectedItem] = true;
				mPreferences.edit().putBoolean( DESKTOP_EFFECTS_USED + selectedItem , true ).commit();
			}
			else if( key.equals( appkey ) )
			{
				appEffectsUses[selectedItem] = true;
				mPreferences.edit().putBoolean( APP_EFFECTS_USED + selectedItem , true ).commit();
			}
			else if( key.equals( dock_row_key ) )
			{
				dockRowUses[selectedItem] = true;
				mPreferences.edit().putBoolean( DOCK_ROW_USED + selectedItem , true ).commit();
			}
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
		{
			img_pro.setVisibility( View.INVISIBLE );
			if( showNew() )
			{
				img_new.setVisibility( View.VISIBLE );
			}
			else
			{
				img_new.setVisibility( View.INVISIBLE );
			}
		}
		else
		{
			img_pro.setVisibility( View.VISIBLE );
		}
	}
	
	class Adpter extends BaseAdapter
	{
		
		@Override
		public int getCount()
		{
			return data.length;
		}
		
		@Override
		public Object getItem(
				int position )
		{
			return null;
		}
		
		@Override
		public long getItemId(
				int position )
		{
			return 0;
		}
		
		@Override
		public View getView(
				int position ,
				View convertView ,
				ViewGroup parent )
		{
			if( null == convertView )
			{
				convertView = LayoutInflater.from( c ).inflate( R.layout.list_preference_item , null );
			}
			TextView textView = (TextView)convertView.findViewById( R.id.txt );
			textView.setText( data[position] );
			RadioButton rb = (RadioButton)convertView.findViewById( R.id.rb );
			rb.setTag( position + "" );
			rb.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(
						View v )
				{
					int tempItem = Integer.valueOf( ( (RadioButton)v ).getTag().toString() );
					if( key.equals( desktopkey ) )
					{
						if( tempItem > DESKTOP_PRO_NUM )
						{
							SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
							if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
							{
								( (RadioButton)v ).setChecked( true );
								selectedItem = tempItem;
								getDialog().dismiss();
								if( selectedItem >= 0 && data != null )
								{
									String value = data[selectedItem].toString();
									MyListPreference.this.setSummary( value );
								}
							}
							else
							{
								( (RadioButton)v ).setChecked( false );
								if( Utils3D.isUpgradePacketInstalled() )
								{
									Intent intent = new Intent();
									intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
									( (Activity)c ).startActivityForResult( intent , 1 );
								}
								else
								{
									if( data != null )
									{
										if( tempItem < data.length )
										{
											map.clear();
											map.put( "desktopeffects" , data[tempItem].toString() );
											MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , map );
										}
									}
									Intent intent = new Intent( iLoongLauncher.getInstance().getApplicationContext() , AdActivity.class );
									SendMsgToAndroid.startActivity( intent );
								}
							}
						}
						else
						{
							getDialog().dismiss();
							( (RadioButton)v ).setChecked( true );
							selectedItem = tempItem;
							if( selectedItem >= 0 && data != null )
							{
								String value = data[selectedItem].toString();
								MyListPreference.this.setSummary( value );
							}
						}
					}
					else if( key.equals( appkey ) )
					{
						if( tempItem > APP_PRO_NUM )
						{
							SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
							if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
							{
								( (RadioButton)v ).setChecked( true );
								selectedItem = tempItem;
								getDialog().dismiss();
								if( selectedItem >= 0 && data != null )
								{
									String value = data[selectedItem].toString();
									MyListPreference.this.setSummary( value );
								}
							}
							else
							{
								( (RadioButton)v ).setChecked( false );
								if( Utils3D.isUpgradePacketInstalled() )
								{
									Intent intent = new Intent();
									intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
									( (Activity)c ).startActivityForResult( intent , 1 );
								}
								else
								{
									if( data != null )
									{
										if( tempItem < data.length )
										{
											map.clear();
											map.put( "appeffects" , data[tempItem].toString() );
											MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , map );
										}
									}
									Intent intent = new Intent( iLoongLauncher.getInstance().getApplicationContext() , AdActivity.class );
									SendMsgToAndroid.startActivity( intent );
								}
							}
						}
						else
						{
							getDialog().dismiss();
							( (RadioButton)v ).setChecked( true );
							selectedItem = tempItem;
							if( selectedItem >= 0 && data != null )
							{
								String value = data[selectedItem].toString();
								MyListPreference.this.setSummary( value );
							}
						}
					}
					else if( key.equals( dock_row_key ) )
					{
						if( tempItem != DOCK_ROW_NUM )
						{
							SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
							if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
							{
								( (RadioButton)v ).setChecked( true );
								selectedItem = tempItem;
								getDialog().dismiss();
								if( selectedItem >= 0 && data != null )
								{
									String value = data[selectedItem].toString();
									MyListPreference.this.setSummary( value );
								}
							}
							else
							{
								( (RadioButton)v ).setChecked( false );
								if( Utils3D.isUpgradePacketInstalled() )
								{
									Intent intent = new Intent();
									intent.setClassName( "com.cooeecomet.launcher.key" , "com.cooeecomet.launcher.key.PrimeActivity" );
									( (Activity)c ).startActivityForResult( intent , 1 );
								}
								else
								{
									if( data != null )
									{
										if( tempItem < data.length )
										{
											map.clear();
											map.put( "dock_row" , data[tempItem].toString() );
											MobclickAgent.onEvent( iLoongLauncher.getInstance() , UmengMobclickAgent.EVENT_ID_ENTRYPRIMEADS , map );
										}
									}
									Intent intent = new Intent( iLoongLauncher.getInstance().getApplicationContext() , AdActivity.class );
									SendMsgToAndroid.startActivity( intent );
								}
							}
						}
						else
						{
							getDialog().dismiss();
							( (RadioButton)v ).setChecked( true );
							selectedItem = tempItem;
							if( selectedItem >= 0 && data != null )
							{
								String value = data[selectedItem].toString();
								MyListPreference.this.setSummary( value );
							}
						}
					}
				}
			} );
			if( position == selectedItem )
			{
				rb.setChecked( true );
			}
			else
			{
				rb.setChecked( false );
			}
			ImageView img_pro = (ImageView)convertView.findViewById( R.id.img_pro );
			ImageView img_new = (ImageView)convertView.findViewById( R.id.img_new );
			if( key.equals( desktopkey ) )
			{
				if( position > DESKTOP_PRO_NUM )
				{
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
					{
						if( !desktopEffectsUses[position] )
						{
							img_pro.setVisibility( View.INVISIBLE );
							img_new.setVisibility( View.VISIBLE );
						}
						else
						{
							img_new.setVisibility( View.INVISIBLE );
						}
					}
					else
					{
						img_pro.setVisibility( View.VISIBLE );
					}
				}
				else
				{
					img_pro.setVisibility( View.INVISIBLE );
					img_new.setVisibility( View.INVISIBLE );
				}
			}
			else if( key.equals( appkey ) )
			{
				if( position > APP_PRO_NUM )
				{
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
					{
						if( !appEffectsUses[position] )
						{
							img_pro.setVisibility( View.INVISIBLE );
							img_new.setVisibility( View.VISIBLE );
						}
						else
						{
							img_new.setVisibility( View.INVISIBLE );
						}
					}
					else
					{
						img_pro.setVisibility( View.VISIBLE );
					}
				}
				else
				{
					img_pro.setVisibility( View.INVISIBLE );
					img_new.setVisibility( View.INVISIBLE );
				}
			}
			else if( key.equals( dock_row_key ) )
			{
				if( position != DOCK_ROW_NUM )
				{
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					if( preferences.getBoolean( Utils3D.UPGRADE_VERIFICATION , false ) )
					{
						if( !dockRowUses[position] )
						{
							img_pro.setVisibility( View.INVISIBLE );
							img_new.setVisibility( View.VISIBLE );
						}
						else
						{
							img_new.setVisibility( View.INVISIBLE );
						}
					}
					else
					{
						img_pro.setVisibility( View.VISIBLE );
					}
				}
				else
				{
					img_pro.setVisibility( View.INVISIBLE );
					img_new.setVisibility( View.INVISIBLE );
				}
			}
			return convertView;
		}
	}
}
