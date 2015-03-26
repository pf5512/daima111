package com.coco.lock2.lockbox;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.iLoong.base.themebox.R;


public class LockHomeSettingActivity extends PreferenceActivity
{
	
	private static final String SCHEME = "package";
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	private static final String APP_PKG_NAME_22 = "pkg";
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	private String defaultLauncherPackname = "";
	private Preference clearLauncher;
	private Preference lockhome;
	private Map<String , String> allMap;
	
	@Override
	protected void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		allMap = new HashMap<String , String>();
		addPreferencesFromResource( R.xml.lock_home );
		clearLauncher = findPreference( StaticClass.CLEAR_LAUNCHER );
		clearLauncher.setOnPreferenceClickListener( mClearLauncher );
		lockhome = findPreference( StaticClass.LOCK_HOME );
		lockhome.setOnPreferenceClickListener( mLockHome );
		boolean hasDefaultLauncher = isSetDefaultLauncher( this );
		if( hasDefaultLauncher )
		{
			clearLauncher.setEnabled( true );
			lockhome.setEnabled( false );
		}
		else
		{
			clearLauncher.setEnabled( false );
			lockhome.setEnabled( true );
		}
		getlauncher();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		boolean hasDefaultLauncher = isSetDefaultLauncher( LockHomeSettingActivity.this );
		if( hasDefaultLauncher )
		{
			clearLauncher.setEnabled( true );
			lockhome.setEnabled( false );
		}
		else
		{
			lockhome.setEnabled( true );
			clearLauncher.setEnabled( false );
		}
	}
	
	private void getlauncher()
	{
		PackageManager pkgMgt = this.getPackageManager();
		Intent it = new Intent( Intent.ACTION_MAIN );
		it.addCategory( Intent.CATEGORY_HOME );
		List<ResolveInfo> ra = pkgMgt.queryIntentActivities( it , 0 );
		String first_launcher_classname = "";
		String first_launcher_packname = "";
		SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( LockHomeSettingActivity.this );
		final ListPreference listPreferenceCategory = (ListPreference)findPreference( StaticClass.LAUNCHER );
		if( listPreferenceCategory != null )
		{
			CharSequence entries[] = new String[ra.size()];
			CharSequence entryValues[] = new String[ra.size()];
			int i = 0;
			for( ResolveInfo category : ra )
			{
				if( i == 0 )
				{
					first_launcher_packname = category.activityInfo.packageName;
					first_launcher_classname = category.activityInfo.name;
				}
				// 去掉CoCo锁屏的home项
				if( category.activityInfo.packageName.equals( StaticClass.ZHESHAN_PACKAGE ) )
				{
					continue;
				}
				entries[i] = category.loadLabel( pkgMgt ).toString();
				entryValues[i] = category.activityInfo.packageName;
				allMap.put( category.activityInfo.packageName , category.activityInfo.name );
				i++;
			}
			if( i != ra.size() )
			{
				entries[ra.size() - 1] = "";
				entryValues[ra.size() - 1] = "";
			}
			listPreferenceCategory.setEntries( entries );
			listPreferenceCategory.setEntryValues( entryValues );
			String cur_launcher = sharedPrefer.getString( StaticClass.CUR_LAUNCHER_PACKNAME , "" );
			if( cur_launcher.equals( "" ) )
			{
				Editor edit = sharedPrefer.edit();
				edit.putString( StaticClass.CUR_LAUNCHER_PACKNAME , first_launcher_packname );
				edit.putString( StaticClass.CUR_LAUNCHER_CLASSNAME , first_launcher_classname );
				edit.commit();
				listPreferenceCategory.setValue( first_launcher_packname );
			}
			else
			{
				listPreferenceCategory.setValue( sharedPrefer.getString( StaticClass.CUR_LAUNCHER_PACKNAME , "" ) );
			}
		}
		listPreferenceCategory.setOnPreferenceChangeListener( new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(
					Preference preference ,
					Object newValue )
			{
				listPreferenceCategory.setValue( newValue.toString() );
				SharedPreferences sharedPrefer = PreferenceManager.getDefaultSharedPreferences( LockHomeSettingActivity.this );
				Editor edit = sharedPrefer.edit();
				edit.putString( StaticClass.CUR_LAUNCHER_PACKNAME , newValue.toString() );
				String classname = allMap.get( newValue.toString() );
				edit.putString( StaticClass.CUR_LAUNCHER_CLASSNAME , classname );
				edit.commit();
				return false;
			}
		} );
	}
	
	private OnPreferenceClickListener mClearLauncher = new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(
				Preference preference )
		{
			if( clearLauncher == preference )
			{
				boolean SetDefault = isSetDefaultLauncher( LockHomeSettingActivity.this );
				if( SetDefault )
				{
					String systemLauncher = Locale.getDefault().getLanguage().toString();
					if( systemLauncher.equals( "en" ) )
					{
						View view = View.inflate( LockHomeSettingActivity.this , R.layout.clear_launcher_en , null );
						( (ImageView)view.findViewById( R.id.imageView01 ) ).setBackgroundResource( R.drawable.clearlauncher_en );
						Dialog alertDialog = new AlertDialog.Builder( LockHomeSettingActivity.this ).setPositiveButton( "Next" , new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(
									DialogInterface dialog ,
									int which )
							{
								showInstalledAppDetails( LockHomeSettingActivity.this , defaultLauncherPackname );
							}
						} ).setNegativeButton( "Cancel" , new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(
									DialogInterface dialog ,
									int which )
							{
							}
						} ).setView( view ).create();
						alertDialog.show();
					}
					else
					{
						View view = View.inflate( LockHomeSettingActivity.this , R.layout.clear_launcher , null );
						( (ImageView)view.findViewById( R.id.imageView01 ) ).setBackgroundResource( R.drawable.clearlauncher );
						Dialog alertDialog = new AlertDialog.Builder( LockHomeSettingActivity.this ).setPositiveButton( "下一步" , new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(
									DialogInterface dialog ,
									int which )
							{
								showInstalledAppDetails( LockHomeSettingActivity.this , defaultLauncherPackname );
							}
						} ).setNegativeButton( "取消" , new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(
									DialogInterface dialog ,
									int which )
							{
							}
						} ).setView( view ).create();
						alertDialog.show();
					}
				}
				return true;
			}
			return false;
		}
	};
	private OnPreferenceClickListener mLockHome = new OnPreferenceClickListener() {
		
		@Override
		public boolean onPreferenceClick(
				Preference preference )
		{
			if( lockhome == preference )
			{
				String systemLauncher = Locale.getDefault().getLanguage().toString();
				if( systemLauncher.equals( "en" ) )
				{
					View view = View.inflate( LockHomeSettingActivity.this , R.layout.lockhome_en , null );
					( (ImageView)view.findViewById( R.id.imageView01 ) ).setBackgroundResource( R.drawable.lockhome_en );
					Dialog alertDialog = new AlertDialog.Builder( LockHomeSettingActivity.this ).setPositiveButton( "Next" , new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(
								DialogInterface dialog ,
								int which )
						{
							// 启动home键功能
							Intent intent = new Intent( Intent.ACTION_MAIN );
							intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							intent.addCategory( Intent.CATEGORY_HOME );
							startActivity( intent );
						}
					} ).setNegativeButton( "Cancel" , new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(
								DialogInterface dialog ,
								int which )
						{
						}
					} ).setView( view ).create();
					alertDialog.show();
				}
				else
				{
					View view = View.inflate( LockHomeSettingActivity.this , R.layout.lockhome , null );
					( (ImageView)view.findViewById( R.id.imageView01 ) ).setBackgroundResource( R.drawable.lockhome );
					Dialog alertDialog = new AlertDialog.Builder( LockHomeSettingActivity.this ).setPositiveButton( "下一步" , new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(
								DialogInterface dialog ,
								int which )
						{
							// 启动home键功能
							Intent intent = new Intent( Intent.ACTION_MAIN );
							intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
							intent.addCategory( Intent.CATEGORY_HOME );
							startActivity( intent );
						}
					} ).setNegativeButton( "取消" , new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(
								DialogInterface dialog ,
								int which )
						{
						}
					} ).setView( view ).create();
					alertDialog.show();
				}
				return true;
			}
			return false;
		}
	};
	
	// 判断是否有默认的launcher
	public boolean isSetDefaultLauncher(
			Context cxt )
	{
		Intent intent = new Intent( Intent.ACTION_MAIN );
		intent.addCategory( Intent.CATEGORY_HOME );
		ResolveInfo info = cxt.getPackageManager().resolveActivity( intent , 0 );
		if( info == null )
		{
			return false;
		}
		List<ResolveInfo> list = cxt.getPackageManager().queryIntentActivities( intent , 0 );
		if( list.size() == 0 )
		{
			return false;
		}
		for( ResolveInfo queryItem : list )
		{
			if( info.activityInfo.packageName.equals( queryItem.activityInfo.packageName ) && info.activityInfo.name.equals( queryItem.activityInfo.name ) )
			{
				defaultLauncherPackname = info.activityInfo.packageName;
				return true;
			}
		}
		return false;
	}
	
	public static void showInstalledAppDetails(
			Context context ,
			String packageName )
	{
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if( apiLevel >= 9 )
		{
			intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
			Uri uri = Uri.fromParts( SCHEME , packageName , null );
			intent.setData( uri );
		}
		else
		{
			final String appPkgName = ( apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21 );
			intent.setAction( Intent.ACTION_VIEW );
			intent.setClassName( APP_DETAILS_PACKAGE_NAME , APP_DETAILS_CLASS_NAME );
			intent.putExtra( appPkgName , packageName );
		}
		context.startActivity( intent );
	}
}
