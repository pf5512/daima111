package com.iLoong.launcher.core;


import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Process;

import com.iLoong.launcher.Desktop3D.Log;


public class AppManager
{
	
	public static int a1(
			Context paramContext ,
			String paramString )
	{
		int i = 0;
		PackageManager localPackageManager = null;
		if( paramString != null )
			localPackageManager = paramContext.getPackageManager();
		try
		{
			i = localPackageManager.getPackageInfo( paramString , 0 ).versionCode;
			return i;
		}
		catch( PackageManager.NameNotFoundException localNameNotFoundException )
		{
			while( true )
				localNameNotFoundException.printStackTrace();
		}
	}
	
	public static PackageInfo a2(
			Context paramContext ,
			String paramString )
	{
		PackageInfo localPackageInfo2 = null;
		try
		{
			PackageInfo localPackageInfo1 = paramContext.getPackageManager().getPackageInfo( paramString , 0 );
			localPackageInfo2 = localPackageInfo1;
			return localPackageInfo2;
		}
		catch( Exception localException )
		{
			while( true )
			{
				localException.printStackTrace();
			}
		}
	}
	
	public static String a3(
			Context paramContext )
	{
		PackageManager localPackageManager = paramContext.getPackageManager();
		Object localObject1 = new ArrayList();
		localPackageManager.getPreferredActivities( new ArrayList() , (List)localObject1 , null );
		Object localObject2 = new Intent( "android.intent.action.MAIN" );
		( (Intent)localObject2 ).addCategory( "android.intent.category.HOME" );
		( (Intent)localObject2 ).addCategory( "android.intent.category.DEFAULT" );
		localObject2 = localPackageManager.queryIntentActivities( (Intent)localObject2 , 0 );
		int k = ( (List)localObject2 ).size();
		int i = ( (List)localObject1 ).size();
		int j = 0;
		if( j >= k )
		{
			localObject1 = null;
		}
		else
		{
			ResolveInfo localResolveInfo = (ResolveInfo)( (List)localObject2 ).get( j );
			String str = null;
			if( localResolveInfo != null )
			{
				str = localResolveInfo.activityInfo.packageName;
				if( str == null )
					;
			}
			for( int m = 0 ; ; m++ )
			{
				if( m >= i )
				{
					j++;
					break;
				}
				ComponentName localComponentName = (ComponentName)( (List)localObject1 ).get( m );
				if( ( localComponentName != null ) && ( str.equals( localComponentName.getPackageName() ) ) )
					break;
			}
			localObject1 = str;
		}
		return (String)(String)localObject1;
	}
	
	public static String a4(
			Context paramContext ,
			String paramString )
	{
		String str = "0.0";
		PackageManager localPackageManager = null;
		if( paramString != null )
			localPackageManager = paramContext.getPackageManager();
		try
		{
			str = localPackageManager.getPackageInfo( paramString , 0 ).versionName;
			return str;
		}
		catch( PackageManager.NameNotFoundException localNameNotFoundException )
		{
			while( true )
				localNameNotFoundException.printStackTrace();
		}
	}
	
	public static String a5(
			Intent paramIntent )
	{
		ComponentName localObject;
		if( paramIntent != null )
		{
			localObject = paramIntent.getComponent();
			if( localObject != null )
				;
		}
		else
		{
			localObject = null;
		}
		Object localObject2 = ( (ComponentName)localObject ).getPackageName();
		return (String)localObject2;
	}
	
	public static List a6(
			Context paramContext )
	{
		List localList = null;
		PackageManager localPackageManager = paramContext.getPackageManager();
		Intent localIntent = new Intent( "android.intent.action.MAIN" );
		localIntent.addCategory( "android.intent.category.LAUNCHER" );
		try
		{
			localList = localPackageManager.queryIntentActivities( localIntent , 0 );
			localList = localList;
			return localList;
		}
		catch( OutOfMemoryError localOutOfMemoryError )
		{
			//      while (true)
			//        alj.a();
		}
		catch( Exception localException )
		{
			while( true )
				localException.printStackTrace();
		}
		return localList;
	}
	
	public static void a7()
	{
		// cy.b("ggheart", "killProcess");
		Process.killProcess( Process.myPid() );
	}
	
	public static void a(
			Context paramContext ,
			int paramInt1 ,
			CharSequence paramCharSequence ,
			int paramInt2 )
	{
		a10( paramContext , null , paramInt1 , paramCharSequence , null , null , paramInt2 );
		( (NotificationManager)paramContext.getSystemService( "notification" ) ).cancel( paramInt2 );
	}
	
	public static void a9(
			Context paramContext ,
			Intent paramIntent )
	{
		if( paramContext != null )
			;
		try
		{
			paramContext.startActivity( paramIntent );
			return;
		}
		catch( ActivityNotFoundException localActivityNotFoundException )
		{
			while( true )
				Log.e( "ggheart" , "saveStartActivity err " + localActivityNotFoundException.getMessage() );
		}
		catch( SecurityException localSecurityException )
		{
			while( true )
				Log.e( "ggheart" , "saveStartActivity err " + localSecurityException.getMessage() );
		}
	}
	
	public static void a10(
			Context paramContext ,
			Intent paramIntent ,
			int paramInt1 ,
			CharSequence paramCharSequence1 ,
			CharSequence paramCharSequence2 ,
			CharSequence paramCharSequence3 ,
			int paramInt2 )
	{
		try
		{
			PendingIntent localPendingIntent = PendingIntent.getActivity( paramContext , 0 , paramIntent , 0 );
			Notification localNotification = new Notification( paramInt1 , paramCharSequence1 , System.currentTimeMillis() );
			localNotification.setLatestEventInfo( paramContext , paramCharSequence2 , paramCharSequence3 , localPendingIntent );
			localNotification.flags = ( 0x10 | localNotification.flags );
			( (NotificationManager)paramContext.getSystemService( "notification" ) ).notify( paramInt2 , localNotification );
			return;
		}
		catch( Exception localException )
		{
			while( true )
				Log.i( "ggheart" , "start notification error id = " + paramInt2 );
		}
	}
	
	public static void a11(
			Context paramContext ,
			Uri paramUri )
	{
		paramContext.startActivity( new Intent( "android.intent.action.DELETE" , paramUri ) );
	}
	
	public static void a12(
			Context paramContext ,
			String paramString )
	{
		paramContext.startActivity( new Intent( "android.intent.action.DELETE" , Uri.parse( "package:" + paramString ) ) );
	}
	
	public static void a13(
			Context paramContext ,
			String paramString1 ,
			String paramString2 )
	{
		if( !b21( paramContext , paramString1 ) )
			c22( paramContext , paramString2 );
	}
	
	public static boolean a14(
			ActivityManager paramActivityManager ,
			String paramString1 ,
			String paramString2 )
	{
		boolean k = false;
		List localList = paramActivityManager.getRunningServices( 30 );
		int j;
		if( localList != null )
			j = localList.size();
		else
			j = 0;
		int i = 0;
		while( i < j )
		{
			ActivityManager.RunningServiceInfo localRunningServiceInfo = (ActivityManager.RunningServiceInfo)localList.get( i );
			if( ( localRunningServiceInfo != null ) && ( localRunningServiceInfo.service != null ) )
			{
				String str1 = localRunningServiceInfo.service.getPackageName();
				String str2 = localRunningServiceInfo.service.getClassName();
				if( ( str1 != null ) && ( str1.contains( paramString1 ) ) && ( str2 != null ) && ( str2.contains( paramString2 ) ) )
					;
			}
			else
			{
				i++;
				continue;
			}
			Log.i( "Notification" , "package = " + localRunningServiceInfo.service.getPackageName() + " class = " + localRunningServiceInfo.service.getClassName() );
			k = true;
		}
		return k;
	}
	
	public static boolean a15(
			Context paramContext )
	{
		return a17( paramContext , "com.android.vending" );
	}
	
	public static boolean a16(
			Context paramContext ,
			Intent paramIntent )
	{
		boolean i = false;
		try
		{
			List localList = paramContext.getPackageManager().queryIntentActivities( paramIntent , 0 );
			localList = localList;
			if( ( localList != null ) && ( localList.size() > 0 ) )
				i = true;
			return true;
		}
		catch( Exception localObject )
		{
			localObject.printStackTrace();
			localObject = null;
		}
		return i;
	}
	
	public static boolean a17(
			Context paramContext ,
			String paramString )
	{
		boolean i = false;
		if( ( paramContext == null ) || ( paramString == null ) )
			return i;
		try
		{
			paramContext.getPackageManager().getPackageInfo( paramString , 1024 );
			i = true;
		}
		catch( Exception localException )
		{
		}
		return i;
	}
	
	public static boolean a18(
			Context paramContext ,
			String paramString1 ,
			String paramString2 )
	{
		return a14( (ActivityManager)paramContext.getSystemService( "activity" ) , paramString1 , paramString2 );
	}
	
	public static void b20(
			Context paramContext ,
			String paramString )
	{
		Intent localIntent = new Intent();
		int i = Build.VERSION.SDK_INT;
		if( i >= 9 )
		{
			localIntent.setAction( "android.settings.APPLICATION_DETAILS_SETTINGS" );
			localIntent.setData( Uri.fromParts( "package" , paramString , null ) );
		}
		try
		{
			paramContext.startActivity( localIntent );
			if( i == 8 )
				return;
			for( String str = "pkg" ; ; str = "com.android.settings.ApplicationPkgName" )
			{
				localIntent.setAction( "android.intent.action.VIEW" );
				localIntent.setClassName( "com.android.settings" , "com.android.settings.InstalledAppDetails" );
				localIntent.putExtra( str , paramString );
				break;
			}
		}
		catch( Exception localException )
		{
			while( true )
				localException.printStackTrace();
		}
	}
	
	public static boolean b21(
			Context paramContext ,
			String paramString )
	{
		boolean i = false;
		Intent localIntent = new Intent( "android.intent.action.VIEW" , Uri.parse( paramString ) );
		localIntent.setPackage( "com.android.vending" );
		localIntent.setFlags( 268435456 );
		try
		{
			paramContext.startActivity( localIntent );
			i = true;
			return i;
		}
		catch( ActivityNotFoundException localActivityNotFoundException )
		{
			while( true )
				Log.i( "ggheart" , "gotoMarketForAPK error, uri = " + paramString );
		}
		catch( Exception localException )
		{
			while( true )
				Log.i( "ggheart" , "gotoMarketForAPK error, uri = " + paramString );
		}
	}
	
	public static void c22(
			Context paramContext ,
			String paramString )
	{
		Intent localIntent = new Intent( "android.intent.action.VIEW" , Uri.parse( "market://details?id=" + paramString ) );
		try
		{
			paramContext.startActivity( localIntent );
			return;
		}
		catch( Exception localException )
		{
			while( true )
				localException.printStackTrace();
		}
	}
	
	public static boolean c23(
			Context paramContext ,
			String paramString )
	{
		boolean i = false;
		if( paramString == null )
			return i;
		Object localObject = Uri.parse( paramString );
		if( localObject == null )
			return i;
		localObject = new Intent( "android.intent.action.VIEW" , (Uri)localObject );
		( (Intent)localObject ).setFlags( 268435456 );
		try
		{
			paramContext.startActivity( (Intent)localObject );
			i = true;
		}
		catch( ActivityNotFoundException localActivityNotFoundException )
		{
			Log.i( "ggheart" , "gotoBrowser error, uri = " + paramString );
		}
		catch( Exception localException )
		{
			Log.i( "ggheart" , "gotoBrowser error, uri = " + paramString );
		}
		return i;
	}
}
