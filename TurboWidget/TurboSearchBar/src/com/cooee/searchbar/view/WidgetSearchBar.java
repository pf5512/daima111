package com.cooee.searchbar.view;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.searchbar.R;
import com.cooee.searchbar.theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.umeng.analytics.MobclickAgent;


public class WidgetSearchBar extends WidgetPluginView3D
{
	
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static MainAppContext mAppContext;
	private Context mContext = null;
	public static CooGdx cooGdx;
	private MainAppContext maincontext;
	public static WidgetSearchBar widgetSearchBar;
	public static float scale = 1f;
	public static float height_scale = 1f;
	private PluginViewObject3D searchBarBg = null;
	
	private String searchPackage = null;
	private boolean voiceSearchAvailable = false;
	public static final int  LeftMove=1;
	public static final int  RightMove=2;
	public static final int  TopMove=3;
	public static final int  BottomMove=4;
	public static final WidgetSearchBar getInstance()
	{
		return widgetSearchBar;
	}
	
	public WidgetSearchBar(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		this.transform = true;
		widgetSearchBar = this;
		this.maincontext = context;
		this.mContext = context.mWidgetContext;
		new WidgetThemeManager( context.mGdxApplication );
		mAppContext = context;
		MODEL_WIDTH = Utils3D.getScreenWidth();
		MODEL_HEIGHT = R3D.Workspace_cell_each_height;
		cooGdx = new CooGdx( context.mGdxApplication );
		this.width = MODEL_WIDTH;
		this.height = MODEL_HEIGHT;
		scale = Utils3D.getScreenWidth() / 720f; 
		if( DefaultLayout.enable_google_version )
		{
			searchBarBg = new PluginViewObject3D( maincontext , "searchBarBg" , getRegion( "google.png" ) , "01.obj" );
		}
		else
		{
			searchBarBg = new PluginViewObject3D( maincontext , "searchBarBg" , getRegion( "bg.png" ) , "01.obj" );
		}
		
		searchBarBg.build();
		searchBarBg.move( this.width / 2 , this.height / 2 , 0f );
		addView( searchBarBg );
		
		ChangeResize();
		
		PackageManager pm = mContext.getPackageManager();
		Intent intent = new Intent( SearchManager.INTENT_ACTION_GLOBAL_SEARCH );
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
		if( resolveInfos != null )
		{
			for( int i = 0 ; i < resolveInfos.size() ; i++ )
			{
				if( ( resolveInfos.get( i ).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM ) > 0 )
				{
					searchPackage = resolveInfos.get( i ).activityInfo.packageName;
					return;
				}
			}
		}
	}
	
	public void ChangeResize()
	{
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		if( sp.getBoolean( "com.cooee.searchbar" , false ) )
		{
			int spanX = sp.getInt( "com.cooee.searchbar" + ":spanX" , -1 );
			int spanY = sp.getInt( "com.cooee.searchbar" + ":spanY" , -1 );
			if( spanX != -1 && spanY != -1 )
			{
				this.width = spanX * R3D.Workspace_cell_each_width;
				this.height = spanY * R3D.Workspace_cell_each_height;
				
				float nowscale = this.width / R3D.Workspace_cell_each_width / 4;
				scale = nowscale * Utils3D.getScreenWidth() / 720f;
				searchBarBg.build();
				
				searchBarBg.move( this.width / 2 , this.height / 2 , 0 );
			}
		}
	}
	
	@Override
	public void onChangeSize(
			float moveX ,
			float moveY ,
			int what ,
			int cellX ,
			int cellY )
	{
		super.onChangeSize( moveX , moveY , what , cellX , cellY );
		float TempX = 0;
		float TempY = 0;
		if( moveX > 0 )
		{
			if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
			{
				TempX = moveX - moveX % R3D.Workspace_cell_each_width;
				this.width = this.width + TempX;
			}
			else
			{
				TempX = moveX - moveX % R3D.Workspace_cell_each_width + R3D.Workspace_cell_each_width;
				this.width = this.width + TempX;
			}
		}
		else
		{
			if( Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) < R3D.Workspace_cell_each_width / 2 )
			{
				TempX = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) );
				this.width = this.width - TempX;
			}
			else
			{
				TempX = Math.abs( moveX ) - Math.abs( moveX % ( R3D.Workspace_cell_each_width ) ) + R3D.Workspace_cell_each_width;
				this.width = this.width - TempX;
			}
		}
		if( moveY > 0 )
		{
			if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
			{
				TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
				this.height = this.height + TempY;
			}
			else
			{
				TempY = moveY - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
				this.height = this.height + TempY;
			}
		}
		else
		{
			if( Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) < R3D.Workspace_cell_each_height / 2 )
			{
				TempY = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) );
				this.height = this.height - TempY;
			}
			else
			{
				TempY = Math.abs( moveY ) - Math.abs( moveY % ( R3D.Workspace_cell_each_height ) ) + R3D.Workspace_cell_each_height;
				this.height = this.height - TempY;
			}
		}
		float nowscale = 1f;
		switch( what )
		{
			case LeftMove:
				nowscale = this.width / R3D.Workspace_cell_each_width / 4;
				scale = nowscale * Utils3D.getScreenWidth() / 720f;
				Log.d( "lxl" , "scale:" + scale );
				searchBarBg.build();
				searchBarBg.move( this.width / 2 , this.height / 2 , 0 );
				break;
			case RightMove:
				nowscale = this.width / R3D.Workspace_cell_each_width / 4;
				scale = nowscale * Utils3D.getScreenWidth() / 720f;
				Log.d( "lxl" , "scale:" + scale );
				searchBarBg.build();
				searchBarBg.move( this.width / 2 , this.height / 2 , 0 );
				break;
			case TopMove:
				if( moveY > 0 )
				{
					searchBarBg.move( 0 , TempY / 2 , 0f );
				}
				else
				{
					searchBarBg.move( 0 , -TempY / 2 , 0f );
				}
				//				searchBarBg.build();
				//				searchBarBg.move( this.width / 2 , this.height / 2, 0 );
				break;
			case BottomMove:
				if( moveY > 0 )
				{
					searchBarBg.move( 0 , TempY / 2 , 0f );
				}
				else
				{
					searchBarBg.move( 0 , -TempY / 2 , 0f );
				}
				//				searchBarBg.build();
				//				searchBarBg.move( this.width / 2 , this.height/2, 0 );
				break;
			default:
				break;
		}
	}
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		// TODO Auto-generated method stub
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = 4;//Integer.valueOf( mContext.getResources().getInteger( R.integer.spanX ) );
		metaData.spanY = 1;//Integer.valueOf( mContext.getResources().getInteger( R.integer.spanY ) );
		metaData.maxInstanceCount = 1;//= mContext.getResources().getInteger( R.integer.max_instance );
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重新添加";
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if (DefaultLayout.enable_google_version) {
			float width = getWidth();
			if (x < width / 6.0f * 5.0f) {
				goWebSearch();
			} else {
				goVoiceSearch();
			}
		} else {
			goWebSearch();
		}
		
		MobclickAgent.onEventValue( mContext , "SearchBarClick" , null , 24 * 60 * 60 );
		return true;
	}
	
	
	private void goWebSearch() {
		if (DefaultLayout.enable_google_version) {
			if (searchPackage != null) {
				Intent intent = new Intent();
				intent.setAction(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
				intent.setPackage(searchPackage);
				mContext.startActivity(intent);
			} else {
				Intent intent = new Intent();
				intent.setComponent( new ComponentName( mContext , "com.cooee.searchbar.GoogleSearchActivity" ) );
				intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				intent.setAction( "com.iLoong.searchbar.MAIN" );
				mContext.startActivity( intent );
			}
		} else {
			Intent intent = new Intent();
			intent.setComponent( new ComponentName( mContext , "com.cooee.searchbar.EasouActivity" ) );
			intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			intent.setAction( "com.iLoong.searchbar.MAIN" );
			mContext.startActivity( intent );
		}
	}
	
	private void goVoiceSearch() {
		Intent intent = new Intent();
		intent.setAction(RecognizerIntent.ACTION_WEB_SEARCH);
		
		PackageManager pm = mContext.getPackageManager();
		ResolveInfo ri = pm.resolveActivity(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		
		if (ri == null) {
			SendMsgToAndroid.sendToastMsg(mContext.getResources().getText(R.string.searchbar_no_voice_search_alert).toString());
			return;	
		}
		
		mContext.startActivity(intent);
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/searchbar/iLoongSearchBar/image/" + name ) ) );
			bt.setFilter( TextureFilter.Linear , TextureFilter.Linear );
			TextureRegion mBackRegion = new TextureRegion( bt );
			return mBackRegion;
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		return null;
	}
}
