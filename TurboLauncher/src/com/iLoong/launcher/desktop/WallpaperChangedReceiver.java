package com.iLoong.launcher.desktop;


import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.theme.ThemeManager;


public class WallpaperChangedReceiver extends BroadcastReceiver
{
	
	public static final String SCENE_WALLPAPER_CHANGE = "com.cooee.scene.wallpaper.change";
	
	@Override
	public void onReceive(
			Context context ,
			Intent intent )
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if( action != null && Intent.ACTION_WALLPAPER_CHANGED.equals( action ) )
		{
			FolderIcon3D.genWallpaperTextureRegion();
			try
			{
				Thread.sleep( 200 );
			}
			catch( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
			if( Assets.getWallpaperCooeeChange( context , "cooeechange" ).equals( "true" ) )
			{
				pref.edit().putBoolean( "cooeechange" , false ).commit();
				//teapotXu add start: ��ֽΪcooee�ṩ��ֽ
				pref.edit().putBoolean( "userDefinedWallpaper" , false ).commit();
				//teapotXu add end				
				PubProviderHelper.addOrUpdateValue( "wallpaper" , "cooeechange" , "false" );
				PubProviderHelper.addOrUpdateValue( "wallpaper" , "userDefinedWallpaper" , "false" );
			}
			else
			{
				pref.edit().putString( "currentWallpaper" , "other" ).commit();
				//teapotXu add start: ��ֽΪ�û��趨��ֽ
				pref.edit().putBoolean( "userDefinedWallpaper" , true ).commit();
				PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , "other" );
				PubProviderHelper.addOrUpdateValue( "wallpaper" , "userDefinedWallpaper" , "true" );
				//teapotXu add end
			}
		}
		else if( action != null && ( SCENE_WALLPAPER_CHANGE.equals( action ) || Intent.ACTION_TIME_CHANGED.equals( action ) ) )
		{
			if( DefaultLayout.enable_scene_wallpaper )
			{
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
				if( pref.getBoolean( "cooeechange" , false ) )
				{
					return;
				}
				String currentWallpaper = intent.getStringExtra( "wallpaper" );
				if( currentWallpaper == null )
					currentWallpaper = pref.getString( "currentWallpaper" , "default" );
				if( currentWallpaper.equals( "default" ) )
				{
					List<String> list = ThemeManager.getInstance().getWallpaperImages();
					if( list.size() <= 0 )
					{
						ThemeManager.getInstance().findWallpapers();
					}
					currentWallpaper = ThemeManager.getInstance().getWallpaperImages().get( 0 );
				}
				if( !currentWallpaper.equals( "other" ) )
				{
					Calendar c = Calendar.getInstance();
					c.setTimeInMillis( System.currentTimeMillis() );
					int hour = c.get( Calendar.HOUR_OF_DAY );
					int minute = c.get( Calendar.MINUTE );
					long delay = 0;
					StringBuffer path = new StringBuffer();
					String name = currentWallpaper.substring( 0 , currentWallpaper.length() - 4 );
					String suffix = currentWallpaper.substring( currentWallpaper.length() - 3 , currentWallpaper.length() );
					if( hour < 5 || hour >= 21 )
					{
						if( hour >= 21 && hour <= 24 )
						{
							delay = ( 5 * 60 + ( 24 - hour ) * 60 - minute - 1 ) * 60 * 1000 + +( 60 - c.get( Calendar.SECOND ) ) * 1000;
						}
						else
						{
							delay = ( ( 5 - hour ) * 60 - minute - 1 ) * 60 * 1000 + ( 60 - c.get( Calendar.SECOND ) ) * 1000;
						}
						path.append( currentWallpaper );
					}
					else if( hour >= 5 && hour < 11 )
					{
						delay = ( ( 11 - hour ) * 60 - minute - 1 ) * 60 * 1000 + ( 60 - c.get( Calendar.SECOND ) ) * 1000;
						path.append( name + "_scene1." + suffix );
					}
					else if( hour >= 11 && hour < 17 )
					{
						delay = ( ( 17 - hour ) * 60 - minute - 1 ) * 60 * 1000 + ( 60 - c.get( Calendar.SECOND ) ) * 1000;
						path.append( name + "_scene2." + suffix );
					}
					else if( hour >= 17 && hour < 21 )
					{
						delay = ( ( 21 - hour ) * 60 - minute - 1 ) * 60 * 1000 + ( 60 - c.get( Calendar.SECOND ) ) * 1000;
						path.append( name + "_scene3." + suffix );
					}
					boolean isScene = ThemeManager.getInstance().setWallpaperByPath( path.toString() , currentWallpaper );
					Intent it = new Intent( context , WallpaperChangedReceiver.class );
					it.setAction( SCENE_WALLPAPER_CHANGE );
					PendingIntent pendingIntent = PendingIntent.getBroadcast( context , 0 , it , 0 );
					AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
					am.cancel( pendingIntent );
					if( isScene )
						am.set( AlarmManager.RTC , System.currentTimeMillis() + delay , pendingIntent );
				}
				else
				{
					Intent it = new Intent( context , WallpaperChangedReceiver.class );
					it.setAction( SCENE_WALLPAPER_CHANGE );
					PendingIntent pendingIntent = PendingIntent.getBroadcast( context , 0 , it , 0 );
					AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
					am.cancel( pendingIntent );
				}
			}
		}
	}
}
