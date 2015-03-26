package com.iLoong.launcher.ultils;


import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;

import com.iLoong.launcher.Desktop3D.Log;


public class ImmersiveModeUtils
{
	
	public static final String TAG = "ImmersiveModeUtils";
	public static final String SP_KEY_TRANSPARENT_STATUS_BAR = "transparent status bar";
	public static final String SP_KEY_STATUS_BAR_SHOW = "status bar show";
	public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000;
	public static final int FLAG_TRANSLUCENT_STATUS = 0x04000000;
	public static final int FLAG_FULLSCREEN = 0x00000400;
	public static final int FLAG_NAVIGATION_HIDDEN_YES = 0x00000002;
	public static final int FLAG_NAVIGATION_HIDDEN_NO = 0x00000001;
	private static final String KEY_IMMERSIVE_MODE_HACK_FLAG = "IMMERSIVE_MODE_HACK_FLAG";
	private static final String KEY_IMMERSIVE_MODE_HAS_HARDWARE_NAVIGATION_BAR = "IMMERSIVE_MODE_HAS_HARDWARE_NAVIGATION_BAR";
	private static int sImmersiveModeHackFlag;
	private static ImmersiveModeState sImmersiveModeState = ImmersiveModeState.DISABLED;
	private static int sNavigationBarHeight;
	private static int sStatusBarHeight;
	
	private static enum ImmersiveModeState
	{
		DISABLED , ENABLED_KIT_KAT , ENABLED_HACK
	}
	
	public static void init(
			Context paramContext ,
			SharedPreferences paramSharedPreferences )
	{
		if( !SystemInfoUtils.isKitKat() )
		{
			if( resolveHackFlag( paramContext , paramSharedPreferences ) )
				sImmersiveModeState = ImmersiveModeState.ENABLED_HACK;
		}
		else
			sImmersiveModeState = ImmersiveModeState.ENABLED_KIT_KAT;
		if( isImmersiveModeEnabled() )
		{
			paramSharedPreferences.edit().putBoolean( SP_KEY_TRANSPARENT_STATUS_BAR , true ).commit();
			sStatusBarHeight = getResourceDimensionPixelSize( paramContext , "status_bar_height" );
			if( !deviceHasHardwareNevigationBar( paramContext , paramSharedPreferences ) )
				sNavigationBarHeight = getResourceDimensionPixelSize( paramContext , "navigation_bar_height" );
			else
				sNavigationBarHeight = 0;
		}
	}
	
	public static boolean isImmersiveModeEnabled()
	{
		boolean i;
		if( sImmersiveModeState == ImmersiveModeState.DISABLED )
			i = false;
		else
			i = true;
		return i;
	}
	
	public static boolean isImmersiveModeHackEnabled()
	{
		boolean i;
		if( sImmersiveModeState != ImmersiveModeState.ENABLED_HACK )
			i = false;
		else
			i = true;
		return i;
	}
	
	public static int getNavigationBarHeight()
	{
		return sNavigationBarHeight;
	}
	
	public static int getStatusBarHeight()
	{
		return sStatusBarHeight;
	}
	
	public static boolean enableImmersiveModeIfSupported(
			Activity paramActivity )
	{
		boolean i = false;
		Log.d( TAG , String.format( "enableImmersiveModeIfSupported() +" ) );
		Object localObject = paramActivity.getWindow();
		final View localView = ( (Window)localObject ).getDecorView();
		if( sImmersiveModeState != ImmersiveModeState.DISABLED )
		{
			if( localObject != null )
			{
				if( sImmersiveModeState != ImmersiveModeState.ENABLED_KIT_KAT )
				{
					if( sImmersiveModeState == ImmersiveModeState.ENABLED_HACK )
					{
						if( localView != null )
						{
							localObject = getContentContainerView( localView );
							if( localObject != null && !SystemInfoUtils.isBelowICS() )
							{
								localView.setSystemUiVisibility( sImmersiveModeHackFlag );
								adjustMargin( paramActivity , 0 , 0 , 0 , -getNavigationBarHeight() );
								// ((View)
								// localObject).setBackgroundResource(2130837911);
								i = true;
							}
						}
					}
				}
				else
				{
					( (Window)localObject ).setFlags( FLAG_TRANSLUCENT_STATUS , FLAG_TRANSLUCENT_STATUS );
					adjustMargin( paramActivity , 0 , ImmersiveModeUtils.getStatusBarHeight() , 0 , 0 );
					//					//if nexus5 do not perform translucent
					//					if(!SystemInfoUtils.isNexus5())
					//					    ( (Window)localObject ).setFlags( FLAG_TRANSLUCENT_NAVIGATION , FLAG_TRANSLUCENT_NAVIGATION );
					//					//((Window) localObject).setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
					//					
					//			    	if(SystemInfoUtils.isMeiZuMx2() || SystemInfoUtils.isHtcOne()){
					//			    		//so far tested mx2 & htc one
					//			    		adjustMargin(paramActivity, 0, ImmersiveModeUtils.getStatusBarHeight(), 0, 0);
					//			    	}else{
					//			    		if(!SystemInfoUtils.isNexus5())
					//			    	        adjustMargin(paramActivity, 0, ImmersiveModeUtils.getStatusBarHeight(), 0, ImmersiveModeUtils.getNavigationBarHeight());
					//			    		else
					//			    			adjustMargin(paramActivity, 0, ImmersiveModeUtils.getStatusBarHeight(), 0, 0);
					//			    	}
					/*		
					//localView.setSystemUiVisibility(flag);
					localView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
						
						@Override
						public void onSystemUiVisibilityChange(int visibility) {
							// TODO Auto-generated method stub
							final int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
									|  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_FULLSCREEN;
									
							//if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
								localView.setSystemUiVisibility(flag);
								Log.d( TAG , String.format("onSystemUiVisibilityChange() set setSystemUiVisibility(%d)!",flag) );			
							//}
							
						}
					});*/
					i = true;
				}
			}
		}
		Log.d( TAG , String.format( "enableImmersiveModeIfSupported() -" ) );
		return i;
	}
	
	private static View getContentContainerView(
			View paramView )
	{
		return (View)paramView.findViewById( 16908290 ).getParent();
	}
	
	private static int getResourceDimensionPixelSize(
			Context paramContext ,
			String paramString )
	{
		Resources localResources = paramContext.getResources();
		int j = localResources.getIdentifier( paramString , "dimen" , "android" );
		int i;
		if( j <= 0 )
			i = 0;
		else
			i = localResources.getDimensionPixelSize( j );
		return i;
	}
	
	private static boolean deviceHasHardwareNevigationBar(
			Context paramContext ,
			SharedPreferences paramSharedPreferences )
	{
		boolean bool1 = true;
		if( SystemInfoUtils.isHtcOne() )
		{// note that htc one has both sw & hw
			// keys
			bool1 = true;
			return bool1;
		}
		if( !paramSharedPreferences.contains( KEY_IMMERSIVE_MODE_HAS_HARDWARE_NAVIGATION_BAR ) )
		{
			boolean bool2 = SystemInfoUtils.isGenyMotion();
			if( !bool2 )
			{
				bool2 = ViewConfiguration.get( paramContext ).hasPermanentMenuKey();
				boolean bool3 = hasNavigationBar( paramContext );
				if( ( !bool2 ) && ( bool3 ) )
					bool1 = false;
				else
					paramSharedPreferences.edit().putBoolean( KEY_IMMERSIVE_MODE_HAS_HARDWARE_NAVIGATION_BAR , bool1 ).apply();
			}
			else if( bool2 )
			{
				bool1 = false;
			}
		}
		else
		{
			bool1 = paramSharedPreferences.getBoolean( KEY_IMMERSIVE_MODE_HAS_HARDWARE_NAVIGATION_BAR , false );
		}
		return bool1;
	}
	
	private static boolean resolveHackFlag(
			Context paramContext ,
			SharedPreferences paramSharedPreferences )
	{
		boolean i = true;
		String[] arrayOfString;
		int j = paramSharedPreferences.getInt( KEY_IMMERSIVE_MODE_HACK_FLAG , 0 );
		if( j > 0 )
			sImmersiveModeHackFlag = j;
		arrayOfString = paramContext.getPackageManager().getSystemSharedLibraryNames();
		if( arrayOfString == null )
		{
			i = false;
			return i;
		}
		String str = null;
		int m = arrayOfString.length;
		String localField;
		Field localField1;
		for( int k = 0 ; k < m ; k++ )
		{
			localField = arrayOfString[k];
			if( !localField.equals( "touchwiz" ) && !localField.startsWith( "com.sonyericsson.navigationbar" ) )
				continue;
			for( str = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND" ; ; str = "SYSTEM_UI_FLAG_TRANSPARENT" )
			{
				if( str != null )
				{
					try
					{
						localField1 = View.class.getField( str );
						if( localField1.getType() == Integer.TYPE )
						{
							sImmersiveModeHackFlag = localField1.getInt( null );
							if( sImmersiveModeHackFlag > 0 )
								paramSharedPreferences.edit().putInt( KEY_IMMERSIVE_MODE_HACK_FLAG , sImmersiveModeHackFlag ).apply();
							break;
						}
					}
					catch( Exception localException )
					{
						i = false;
					}
				}
			}
		}
		return i;
	}
	
	private static boolean hasNavigationBar(
			Context paramContext )
	{
		int i = paramContext.getResources().getIdentifier( "config_showNavigationBar" , "bool" , "android" );
		boolean j;
		if( i <= 0 )
			j = false;
		else
			j = paramContext.getResources().getBoolean( i );
		return j;
	}
	
	public static void adjustMargin(
			Activity paramActivity ,
			int paramInt1 ,
			int paramInt2 ,
			int paramInt3 ,
			int paramInt4 )
	{
		Object localObject = paramActivity.getWindow();
		View localView = ( (Window)localObject ).getDecorView();
		View paramView = getContentContainerView( localView );
		ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
		localMarginLayoutParams.setMargins(
				paramInt1 + localMarginLayoutParams.leftMargin ,
				paramInt2 + localMarginLayoutParams.topMargin ,
				paramInt3 + localMarginLayoutParams.rightMargin ,
				paramInt4 + localMarginLayoutParams.bottomMargin );
	}
	
	public static void adjustPadding(
			Activity paramActivity ,
			int paramInt1 ,
			int paramInt2 ,
			int paramInt3 ,
			int paramInt4 )
	{
		Object localObject = paramActivity.getWindow();
		View localView = ( (Window)localObject ).getDecorView();
		View paramView = getContentContainerView( localView );
		paramView.setPadding( paramInt1 + paramView.getPaddingLeft() , paramInt2 + paramView.getPaddingTop() , paramInt3 + paramView.getPaddingRight() , paramInt4 + paramView.getPaddingBottom() );
	}
	
	public static void adjustSize(
			Activity paramActivity ,
			int paramInt1 ,
			int paramInt2 )
	{
		Object localObject = paramActivity.getWindow();
		View localView = ( (Window)localObject ).getDecorView();
		View paramView = getContentContainerView( localView );
		ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
		localLayoutParams.width = ( paramInt1 + localLayoutParams.width );
		localLayoutParams.height = ( paramInt2 + localLayoutParams.height );
	}
}
