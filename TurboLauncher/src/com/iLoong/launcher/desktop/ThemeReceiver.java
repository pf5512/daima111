// enable_themebox
package com.iLoong.launcher.desktop;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.pub.provider.PubProviderHelper;


public class ThemeReceiver extends BroadcastReceiver
{
	
	public static final String ACTION_DEFAULT_THEME_CHANGED = "com.coco.theme.action.DEFAULT_THEME_CHANGED";
	public static final String ACTION_LAUNCHER_CLICK_THEME = "com.cooee.launcher.click_theme";
	public static final String ACTION_LAUNCHER_REQ_RESUME_TIME = "com.cooee.launcher.req_resume_time";
	public static final String ACTION_LAUNCHER_RSP_RESUME_TIME = "com.cooee.launcher.rsp_resume_time";
	public static String ACTION_LAUNCHER_RESTART = "com.coco.launcher.restart";
	public static final String ACTION_LAUNCHER_APPLY_THEME = "com.coco.launcher.apply_theme";
	public static final String ACTI0N_LAUNCHER_ADD_WIDGET = "com.coco.launcher.add.widget";
	
	@Override
	public void onReceive(
			Context c ,
			Intent intent )
	{
		Log.d( "launcher" , "intent:" + intent.getAction() );
		final String action = intent.getAction();
		if( action.equals( ACTION_LAUNCHER_RESTART ) )
		{
			restart( c , false );
			return;
		}
		else if( action.equals( ACTION_LAUNCHER_APPLY_THEME ) )
		{
			applyTheme( c , intent );
			return;
		}
		else if( action.equals( ACTION_LAUNCHER_CLICK_THEME ) )
		{
			String selectedLauncher = intent.getStringExtra( "selected_launcher" );
			String themePkgName = intent.getStringExtra( "theme_pkg_name" );
			if( selectedLauncher != null && selectedLauncher.equals( c.getPackageName() ) && themePkgName != null )
			{
				Intent intent2 = new Intent( ACTION_LAUNCHER_APPLY_THEME );
				intent2.putExtra( "theme_status" , 1 );
				intent2.putExtra( "theme" , themePkgName );
				applyTheme( c , intent2 );
				restart( c , true );
				c.sendBroadcast( new Intent( ACTION_DEFAULT_THEME_CHANGED ) );
				Context mcontext = iLoongLauncher.getInstance();
				SharedPreferences sp = mcontext.getSharedPreferences( "CurrentTheme" , mcontext.MODE_PRIVATE );
				sp.edit().putString( "currenttheme_pkg" , themePkgName ).commit();
			}
		}
		else if( action.equals( ACTION_LAUNCHER_REQ_RESUME_TIME ) )
		{
			SharedPreferences prefs2 = c.getSharedPreferences( "launcher" , Context.MODE_WORLD_READABLE );
			Intent intent2 = new Intent( ACTION_LAUNCHER_RSP_RESUME_TIME );
			intent2.putExtra( "resume_time" , prefs2.getLong( "resume_time" , -1 ) );
			intent2.putExtra( "launcher_pkg_name" , c.getPackageName() );
			c.sendBroadcast( intent2 );
		}
		else if( action.equals( ACTI0N_LAUNCHER_ADD_WIDGET ) )
		{
			if( DefaultLayout.enable_personalcenetr_click_widget_to_add && iLoongLauncher.getInstance() != null )
			{
				String pkgName = intent.getStringExtra( "packageName" );
				if( pkgName != null )
					iLoongLauncher.getInstance().addWidgetFromPersonalCenter( pkgName );
			}
		}
	}
	
	public void applyTheme(
			Context c ,
			Intent intent )
	{
		SharedPreferences prefs = c.getSharedPreferences( "theme" , Activity.MODE_WORLD_WRITEABLE );
		prefs.edit().putString( "theme" , intent.getStringExtra( "theme" ) ).commit();
		prefs.edit().putInt( "theme_status" , intent.getIntExtra( "theme_status" , 1 ) ).commit();
		PubProviderHelper.addOrUpdateValue( "theme" , "theme" , intent.getStringExtra( "theme" ) );
		PubProviderHelper.addOrUpdateValue( "theme" , "theme_status" , String.valueOf( intent.getIntExtra( "theme_status" , 1 ) ) );
		c.sendBroadcast( new Intent( ACTION_DEFAULT_THEME_CHANGED ) );
		if( ( iLoongLauncher.getInstance() != null && DefaultLayout.fast_change_theme ) )
		{
			iLoongLauncher.getInstance().onThemeChanged();
		}
		//zjp
		if( DefaultLayout.enable_content_staistic )
		{
			String theme = intent.getStringExtra( "theme" );
			SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences( c );
			boolean register = prefs1.getBoolean( "registerTheme-" + theme , false );
			if( !register && !theme.equals( c.getPackageName() ) )
			{
				System.out.println( "theme register" );
				JSONObject tmp = Assets.config;
				try
				{
					JSONObject config = tmp.getJSONObject( "config" );
					String appid = config.getString( "app_id" );
					String sn = config.getString( "serialno" );
					Context context = c.createPackageContext( theme , Context.CONTEXT_IGNORE_SECURITY );
					int launcherVersion = context.getPackageManager().getPackageInfo( context.getPackageName() , 0 ).versionCode;
				}
				catch( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch( NameNotFoundException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				prefs1.edit().putBoolean( "registerTheme-" + theme , true ).commit();
			}
		}
	}
	
	public void restart(
			Context c ,
			boolean fromThemeClick )
	{
		if( !iLoongApplication.init )
		{
			System.out.println( "theme restart 1" );
			final Intent intent2 = new Intent();
			intent2.setClass( c , iLoongLauncher.class );
			intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			c.startActivity( intent2 );
		}
		else if( iLoongLauncher.getInstance() == null )
		{
			System.out.println( "theme restart 2" );
			final Intent intent2 = new Intent();
			intent2.setClass( c , iLoongLauncher.class );
			intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			c.startActivity( intent2 );
			System.exit( 0 );
		}
		else if( !DefaultLayout.fast_change_theme )
		{
			System.out.println( "theme restart 3" );
			if( iLoongLauncher.getInstance().stoped )
			{
				iLoongApplication.needRestart = true;
				final Intent intent2 = new Intent();
				intent2.setClass( c , iLoongLauncher.class );
				intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
				c.startActivity( intent2 );
				System.exit( 0 );
			}
			else
				SystemAction.RestartSystem();
		}
		else if( fromThemeClick )
		{
			final Intent intent2 = new Intent();
			intent2.setClass( c , iLoongLauncher.class );
			intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			c.startActivity( intent2 );
		}
	}
}
//enable_themebox
