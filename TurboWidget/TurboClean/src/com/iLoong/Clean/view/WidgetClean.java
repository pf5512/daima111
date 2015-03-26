package com.iLoong.Clean.view;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.cooee.android.launcher.framework.IconCache;
import com.iLoong.Clean.R;
import com.iLoong.Clean.theme.WidgetThemeManager;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.DrawDynamicIcon;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.umeng.analytics.MobclickAgent;


public class WidgetClean extends WidgetPluginView3D
{
	
	private boolean isOrnotUseCleanMaster = false;
	public static Canvas canvas = new Canvas();
	public static Paint paint = new Paint();
	public static FontMetrics fontMetrics = new FontMetrics();
	public static Bitmap bgBitmap;
	public float paddingTop;
	public float paddingLeft;
	private TextureRegion region_red;
	private TextureRegion region_yellow;
	private TextureRegion region_green;
	private TextureRegion region_current;
	private long totalMemory;
	private long clearedMemory = 0;
	private long preTime;
	private boolean isFirstIn = true;
	public static float MODEL_WIDTH = 320;
	public static float MODEL_HEIGHT = 320;
	public static MainAppContext mAppContext;
	private Context mContext = null;
	public static CooGdx cooGdx;
	private MainAppContext maincontext;
	public static WidgetClean widgetClean;
	public static float scale = 1f;
	private View3D bg3D = null;
	private View3D shade3D = null;
	private View3D text3D = null;
	private View3D frame3D = null;
	private Timeline animation_line = null;
	private boolean isStart = false;
	public static final String ACTION_KILL_PROGRESS = "action_kill_progress";
	private boolean isInitiativeRefresh = false;
	private KillProgressReceiver mKillProgressReceiver;
	
	public static final WidgetClean getInstance()
	{
		return widgetClean;
	}
	
	public WidgetClean(
			String name ,
			MainAppContext context ,
			int widgetId )
	{
		super( name );
		//		this.transform = true;
		widgetClean = this;
		this.maincontext = context;
		this.mContext = context.mWidgetContext;
		new WidgetThemeManager( context.mGdxApplication );
		mAppContext = context;
		//		MODEL_WIDTH = Utils3D.getScreenWidth() / 4;
		//		MODEL_HEIGHT = R3D.Workspace_cell_each_height;
		cooGdx = new CooGdx( context.mGdxApplication );
		this.width = R3D.workspace_cell_width;
		this.height = R3D.workspace_cell_height;
		totalMemory = getTotalMemory();
		scale = Utils3D.getScreenWidth() / 720f;
		region_current = new TextureRegion();
		region_red = new TextureRegion( getRegion( "shade_red.png" ) );
		region_yellow = new TextureRegion( getRegion( "shade_yellow.png" ) );
		region_green = new TextureRegion( getRegion( "shade_green.png" ) );
		shade3D = new View3D( "cleanAnter" , getRegion( "shade_red.png" ) );
		isOrnotUseCleanMaster = Utils3D.isOrnotFileExists( mContext , "CleanMaster.apk" );
		try
		{
			Bitmap tmp = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/clean/iLoongClean/image/" + "bg.png" ) );
			if( tmp.getWidth() == DefaultLayout.app_icon_size && tmp.getHeight() == DefaultLayout.app_icon_size )
			{
				bgBitmap = tmp;
			}
			else
			{
				bgBitmap = Bitmap.createScaledBitmap( tmp , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size , true );
				tmp.recycle();
			}
			Bitmap b = titleToBitmap( bgBitmap , mContext.getResources().getString( R.string.clean_up ) , null , null , R3D.workspace_cell_width , R3D.workspace_cell_height , true );
			//			Bitmap b = Utils3D.titleToBitmap(
			//					BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/clean/iLoongClean/image/" + "bg.png" ) ) ,
			//					"一键清理" ,
			//					R3D.workspace_cell_width ,
			//					R3D.folder_front_height ,
			//					true );
			//			Bitmap b = BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/clean/iLoongClean/image/" + "bg.png" ) );
			bg3D = new View3D( "cleanBg" , new BitmapTexture( b , true ) );
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		this.setUser( DefaultLayout.app_icon_size * ( 1 - getAvailMemory() / (float)totalMemory ) );
		addView( bg3D );
		shade3D.setSize( DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
		shade3D.setPosition( paddingLeft , R3D.workspace_cell_height - DefaultLayout.app_icon_size - paddingTop );
		addView( shade3D );
		text3D = new View3D( "text3D" , getTextureRegion( mAppContext , ( Math.round( ( ( 1 - getAvailMemory() / (float)totalMemory ) * 100 ) ) ) + "%" ) );
		text3D.setSize( DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
		text3D.setPosition( paddingLeft , R3D.workspace_cell_height - DefaultLayout.app_icon_size - paddingTop );
		addView( text3D );
		frame3D = new View3D( "frame3D" , getRegion( "frame.png" ) );
		frame3D.setSize( DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
		frame3D.setPosition( paddingLeft , R3D.workspace_cell_height - DefaultLayout.app_icon_size - paddingTop );
		addView( frame3D );
		preTime = System.currentTimeMillis();
		mKillProgressReceiver = new KillProgressReceiver();
		mContext.registerReceiver( mKillProgressReceiver , new IntentFilter( ACTION_KILL_PROGRESS ) );
	}
	
	float currentHeight = 0;
	
	@Override
	public void draw(
			SpriteBatch batch ,
			float parentAlpha )
	{
		if( isStart )
		{
			float userData = this.getUser();
			currentHeight = userData;
			if( userData >= DefaultLayout.app_icon_size * 0.9 )
			{
				region_current = region_red;
				region_current.setV( 1 - userData / DefaultLayout.app_icon_size );
				shade3D.region.setRegion( region_current );
			}
			else if( userData <= DefaultLayout.app_icon_size * 0.6 )
			{
				region_current = region_green;
				region_current.setV( 1 - userData / DefaultLayout.app_icon_size );
				shade3D.region.setRegion( region_current );
			}
			else
			{
				region_current = region_yellow;
				region_current.setV( 1 - userData / DefaultLayout.app_icon_size );
				shade3D.region.setRegion( region_current );
			}
			shade3D.setSize( DefaultLayout.app_icon_size , userData );
			if( text3D.region != null )
			{
				text3D.region.getTexture().dispose();
			}
			text3D.region.setRegion( getTextureRegion( mAppContext , ( Math.round( ( ( userData / (float)DefaultLayout.app_icon_size ) * 100 ) ) ) + "%" ) );
			//			Log.v( "WidgetClean" , "userData " + userData );
		}
		else
		{
			long currTime = System.currentTimeMillis();
			if( currTime - preTime > 30000 || isFirstIn || isInitiativeRefresh )
			{
				preTime = currTime;
				isFirstIn = false;
				float memoryScale = ( 1 - getAvailMemory() / (float)totalMemory );
				if( memoryScale >= 0.9 )
				{
					region_current = region_red;
					region_current.setV( 1 - memoryScale );
					shade3D.region.setRegion( region_current );
				}
				else if( memoryScale <= 0.6 )
				{
					region_current = region_green;
					region_current.setV( 1 - memoryScale );
					shade3D.region.setRegion( region_current );
				}
				else
				{
					region_current = region_yellow;
					region_current.setV( 1 - memoryScale );
					shade3D.region.setRegion( region_current );
				}
				shade3D.setSize( DefaultLayout.app_icon_size , memoryScale * DefaultLayout.app_icon_size );
				if( text3D.region != null )
				{
					text3D.region.getTexture().dispose();
				}
				text3D.region.setRegion( getTextureRegion( mAppContext , ( Math.round( ( memoryScale * 100 ) ) ) + "%" ) );
			}
			isInitiativeRefresh = false;
		}
		super.draw( batch , parentAlpha );
	}
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		if( isOrnotUseCleanMaster )
		{
			if( isStart )
			{
				return true;
			}
			isStart = true;
			//		Log.e( "totalMemory" , getTotalMemory() + "" );
			final long beforeClearMemory = getAvailMemory();
			//		Log.i( "beforeClearMemory" , beforeClearMemory + "" );
			long start = System.currentTimeMillis();
			//		Log.i( "startTime" , start + "" );
			long end = System.currentTimeMillis();
			//		Log.i( "endTime" , end + "" );
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis( end - start );
			int seconds = c.get( Calendar.SECOND );
			//		Log.i( "seconds" , seconds + "" );
			if( animation_line != null )
			{
				animation_line.free();
				animation_line = null;
			}
			float targetUser = ( DefaultLayout.app_icon_size * ( 1 - getAvailMemory() / (float)totalMemory ) );
			animation_line = Timeline.createParallel();
			if( Utils3D.isAPKInstalled( mContext , "com.blueflash.kingscleanmaster" ) )
			{
				animation_line.push( Tween.to( this , View3DTweenAccessor.USER , 1 ).target( 0f , 0 , 0 ).ease( Linear.INOUT ) );
				animation_line.push( Tween.to( this , View3DTweenAccessor.USER , 1 ).target( targetUser , 20 , 0 ).ease( Linear.INOUT ).delay( 1f ) );
				animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
				text3D.region.setRegion( getTextureRegion( mAppContext , ( Math.round( ( ( 1 - getAvailMemory() / (float)totalMemory ) * 100 ) ) ) + "%" ) );
				Log.i( "WidgetClean" , "Utils3D.isAPKInstalled:" + Utils3D.isAPKInstalled( mContext , "com.blueflash.kingscleanmaster" ) );
			}
			else
			{
				isStart = false;
			}
			kill2();
			Log.i( "WidgetClean" , "Kill2()" );
			mContext.sendBroadcast( new Intent( ACTION_KILL_PROGRESS ) );
			MobclickAgent.onEventValue( mContext , "CleanClick" , null , 24 * 60 * 60 );
			return true;
		}
		else
		{
			if( isStart )
			{
				return true;
			}
			isStart = true;
			//		Log.e( "totalMemory" , getTotalMemory() + "" );
			final long beforeClearMemory = getAvailMemory();
			//		Log.i( "beforeClearMemory" , beforeClearMemory + "" );
			long start = System.currentTimeMillis();
			//		Log.i( "startTime" , start + "" );
			kill();
			long end = System.currentTimeMillis();
			//		Log.i( "endTime" , end + "" );
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis( end - start );
			int seconds = c.get( Calendar.SECOND );
			//		Log.i( "seconds" , seconds + "" );
			final long afterClearMemory = getAvailMemory();
			//		Log.i( "afterClearMemory" , afterClearMemory + "" );
			clearedMemory = ( afterClearMemory - beforeClearMemory ) / 1024;
			if( animation_line != null )
			{
				animation_line.free();
				animation_line = null;
			}
			float targetUser = ( DefaultLayout.app_icon_size * ( 1 - getAvailMemory() / (float)totalMemory ) );
			animation_line = Timeline.createParallel();
			animation_line.push( Tween.to( this , View3DTweenAccessor.USER , 1 ).target( 0f , 0 , 0 ).ease( Linear.INOUT ) );
			animation_line.push( Tween.to( this , View3DTweenAccessor.USER , 1 ).target( targetUser , 20 , 0 ).ease( Linear.INOUT ).delay( 1f ) );
			animation_line.start( View3DTweenAccessor.manager ).setCallback( this );
			text3D.region.setRegion( getTextureRegion( mAppContext , ( Math.round( ( ( 1 - getAvailMemory() / (float)totalMemory ) * 100 ) ) ) + "%" ) );
			MobclickAgent.onEventValue( mContext , "CleanClick" , null , 24 * 60 * 60 );
			return true;
		}
	}
	
	@Override
	public WidgetPluginViewMetaData getPluginViewMetaData()
	{
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = 1;
		metaData.spanY = 1;
		metaData.maxInstanceCount = 1;
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			metaData.maxInstanceAlert = "已存在，不可重复添加";
		}
		else
		{
			metaData.maxInstanceAlert = "Already exists, can not add another one";
		}
		return metaData;
	}
	
	private TextureRegion getRegion(
			String name )
	{
		try
		{
			BitmapTexture bt = new BitmapTexture( BitmapFactory.decodeStream( mAppContext.mWidgetContext.getAssets().open( "theme/widget/clean/iLoongClean/image/" + name ) ) , true );
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
	
	private int clearedAppCount = 0;
	
	private void kill2()
	{
		Log.i( "WidgetClean" , "Messenger.sendMsg" );
		Messenger.sendMsg( Messenger.MSG_WIDGETClEAN_CLEAN , "WidgetClean" );
	}
	
	private void kill()
	{
		clearedAppCount = 0;
		ActivityManager activityManager = (ActivityManager)mAppContext.mWidgetContext.getSystemService( Context.ACTIVITY_SERVICE );
		List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
		if( list != null )
		{
			for( int i = 0 ; i < list.size() ; i++ )
			{
				ActivityManager.RunningAppProcessInfo appInfo = list.get( i );
				String[] pkgList = appInfo.pkgList;
				if( appInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE )
				{
					for( int j = 0 ; j < pkgList.length ; j++ )
					{
						activityManager.killBackgroundProcesses( pkgList[j] );
						clearedAppCount++;
					}
				}
			}
		}
	}
	
	private long getAvailMemory()
	{
		long MEM_UNUSED;
		// 得到ActivityManager
		ActivityManager am = (ActivityManager)mContext.getSystemService( Context.ACTIVITY_SERVICE );
		// 创建ActivityManager.MemoryInfo对象  
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo( mi );
		// 取得剩余的内存空间 
		MEM_UNUSED = mi.availMem / 1024;
		return MEM_UNUSED;
	}
	
	private long getTotalMemory()
	{
		long mTotal;
		// /proc/meminfo读出的内核信息进行解释
		String path = "/proc/meminfo";
		String content = null;
		BufferedReader br = null;
		try
		{
			br = new BufferedReader( new FileReader( path ) , 8 );
			String line;
			if( ( line = br.readLine() ) != null )
			{
				content = line;
			}
		}
		catch( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if( br != null )
			{
				try
				{
					br.close();
				}
				catch( IOException e )
				{
					e.printStackTrace();
				}
			}
		}
		// beginIndex
		int begin = content.indexOf( ':' );
		// endIndex
		int end = content.indexOf( 'k' );
		// 截取字符串信息
		content = content.substring( begin + 1 , end ).trim();
		mTotal = Integer.parseInt( content );
		return mTotal;
	}
	
	public synchronized Bitmap titleToBitmap(
			Bitmap b ,
			String title ,
			Bitmap icn_bg ,
			Bitmap title_bg ,
			int textureWidth ,
			int textureHeight ,
			boolean recycle )
	{
		Bitmap bmp = Bitmap.createBitmap( textureWidth , textureHeight , Config.ARGB_8888 );
		canvas.setBitmap( bmp );
		float bmpHeight = Utilities.sIconTextureHeight;
		paint.reset();
		paint.setColor( Color.WHITE );
		paint.setAntiAlias( true );
		paint.setTextSize( R3D.icon_title_font );
		paint.getFontMetrics( fontMetrics );
		float singleLineHeight = (float)Math.ceil( fontMetrics.descent - fontMetrics.ascent );
		float space_height;
		String[] splitTitle;
		float iconbgPaddingTop = 0;
		paddingLeft = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
		float iconcoverplatePaddingTop = 0;
		//		Bitmap icon_cover_plate_bmp = null;
		//		Bitmap original_icon_cover_bmp = ThemeManager.getInstance().getBitmap("theme/iconbg/icon_cover_plate.png");
		//		if(null != original_icon_cover_bmp){
		//			icon_cover_plate_bmp = Bitmap.createScaledBitmap(original_icon_cover_bmp,
		//					DefaultLayout.app_icon_size, DefaultLayout.app_icon_size,true);
		//		}		
		//		Rect iconbgRect = new Rect();
		//		int iconbgRectTop = 0;
		if( DefaultLayout.font_double_line )
		{
			space_height = 0;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - 2 * singleLineHeight ) / 2;
			if( title != null )
			{
				splitTitle = Utils3D.splitTitle( title , R3D.icon_title_font , textureWidth );
			}
			else
			{
				splitTitle = null;
			}
		}
		else
		{
			space_height = bmpHeight / R3D.icon_title_gap;
			paddingTop = ( R3D.workspace_cell_height - bmpHeight - space_height - singleLineHeight ) / 2;
			if( title != null )
			{
				splitTitle = new String[1];
				splitTitle[0] = title;
			}
			else
			{
				splitTitle = null;
			}
		}
		if( paddingTop < 0 )
			paddingTop = 0;
		iconcoverplatePaddingTop = paddingTop;
		//		iconbgRectTop = (int)paddingTop;
		bmpHeight += ( paddingTop + space_height );
		if( title_bg != null )
			canvas.drawBitmap( title_bg , 0 , bmpHeight + singleLineHeight / 2 - title_bg.getHeight() / 2 , null );
		int saveLayer = -1;
		if( b != null && !b.isRecycled() && Icon3D.getMask() != null && icn_bg != null )
		{
			saveLayer = canvas.saveLayer(
					0 ,
					0 ,
					textureWidth ,
					textureHeight ,
					null ,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG );
		}
		if( b != null && !b.isRecycled() )
		{
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg == null ) )
			{
				//				b = addShadowToBitmap( b , true );
			}
			// paint.setShadowLayer(2f, 0f, 2f, 0xff000000);
			if( b.getHeight() != Utilities.sIconTextureHeight )
			{
				if( icn_bg != null )
				{
					if( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f )
					{
						paddingTop = iconbgPaddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
					}
					else
					{
						paddingTop = iconbgPaddingTop;
					}
				}
				else
				{
					paddingTop = paddingTop - ( b.getHeight() - Utilities.sIconTextureHeight ) / 2;
				}
			}
			else
			{
				if( ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f ) && ( DefaultLayout.icon_shadow_radius != 0f ) )
				{
					paddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
				}
			}
			if( b.getWidth() != Utilities.sIconTextureWidth )
			{
				paddingLeft = paddingLeft - ( b.getWidth() - Utilities.sIconTextureWidth ) / 2;
			}
			if( paddingTop < 0 )
			{
				paddingTop = 0;
			}
			if( paddingLeft < 0 )
			{
				paddingLeft = 0;
			}
			if( ( DefaultLayout.icon_shadow_radius != 0f ) && ( icn_bg != null ) && ( DefaultLayout.thirdapk_icon_scaleFactor != 1.0f ) )
			{
				paddingTop += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
			}
			if( ( icn_bg != null ) && ( ( R3D.Third_APK_Icon_Offset_X != 0 ) || ( R3D.Third_APK_Icon_Offset_Y != 0 ) ) )
			{
				//				drawThirdAPKIconByOffsetInTheme( b , iconbgRect , iconbgPaddingTop , paddingTop , paddingLeft , canvas );
			}
			else
			//canvas.drawBitmap(b, paddingLeft, paddingTop, null);	//xiatian del
			{
				if( ( DefaultLayout.thirdapk_icon_scaleFactor > 1.0f ) && ( icn_bg != null ) )
				{
					Rect src = new Rect( 0 , 0 , 0 , 0 );
					src.left = ( b.getWidth() - DefaultLayout.app_icon_size ) / 2;
					src.top = ( b.getHeight() - DefaultLayout.app_icon_size ) / 2;
					src.right = src.left + DefaultLayout.app_icon_size;
					src.bottom = src.top + DefaultLayout.app_icon_size;
					Rect dst = new Rect( 0 , 0 , 0 , 0 );
					dst.left = ( textureWidth - Utilities.sIconTextureHeight ) / 2;
					dst.top = (int)iconbgPaddingTop;
					if( DefaultLayout.icon_shadow_radius != 0f )
					{
						dst.top += Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					dst.right = dst.left + DefaultLayout.app_icon_size;
					dst.bottom = dst.top + DefaultLayout.app_icon_size;
					canvas.drawBitmap( b , src , dst , null );
				}
				else
				{
					canvas.drawBitmap( b , paddingLeft , paddingTop , null );
				}
			}
			if( recycle && b != IconCache.mDefaultIcon )
				b.recycle();
		}
		if( DefaultLayout.dynamic_icon && icn_bg == null )
		{
			float icony;
			float hotseaty;
			icony = R3D.workspace_cell_height - ( paddingTop + Utilities.sIconTextureHeight ) - ( Math.min( b.getHeight() , b.getWidth() ) - Utilities.sIconTextureHeight ) / 2;
			DrawDynamicIcon.seticony( icony );
			hotseaty = icony + Utils3D.getIconBmpHeight() - R3D.workspace_cell_height;
			DrawDynamicIcon.sethotseaty( hotseaty );
		}
		if( Icon3D.getMask() != null && icn_bg != null && saveLayer != -1 )
		{
			float maskPaddingTop = 0;
			float maskPaddingLeft = 0;
			//			Bitmap icon_mask;
			//			if(DefaultLayout.icon_shadow_radius != 0){
			//				icon_mask = Bitmap.createBitmap(icn_bg.getWidth(), icn_bg.getHeight(),Config.ARGB_8888);
			//				Canvas c = new Canvas(icon_mask);
			//				
			//				c.drawBitmap(Icon3D.getMask(), DefaultLayout.icon_shadow_radius, DefaultLayout.icon_shadow_radius-1, null);
			//			}else{
			//				icon_mask = Icon3D.getMask();
			//			}
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					maskPaddingTop = iconcoverplatePaddingTop - ( Icon3D.getMask().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( DefaultLayout.icon_shadow_radius != 0 )
					{
						if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
							maskPaddingTop = paddingTop;
						else
							maskPaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					if( maskPaddingTop < 0 )
					{
						maskPaddingTop = 0;
					}
				}
				maskPaddingLeft = ( textureWidth - Icon3D.getMask().getWidth() ) / 2;
			}
			paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.MULTIPLY ) );
			canvas.drawBitmap( Icon3D.getMask() , maskPaddingLeft , maskPaddingTop , paint );
			paint.setXfermode( null );
			canvas.restoreToCount( saveLayer );
		}
		if( ( icn_bg != null ) && Icon3D.getCover() != null )
		{
			if( DefaultLayout.app_icon_size != R3D.workspace_cell_width && DefaultLayout.app_icon_size != R3D.workspace_cell_height )
			{
				if( b != null )
				{
					iconcoverplatePaddingTop = iconcoverplatePaddingTop - ( Icon3D.getCover().getHeight() - Utilities.sIconTextureHeight ) / 2;
					if( DefaultLayout.icon_shadow_radius != 0 )
					{
						if( DefaultLayout.thirdapk_icon_scaleFactor == 1.0f )
							iconcoverplatePaddingTop = paddingTop;
						else
							iconcoverplatePaddingTop = iconbgPaddingTop + Tools.dip2px( iLoongLauncher.getInstance() , DefaultLayout.icon_shadow_radius );
					}
					if( iconcoverplatePaddingTop < 0 )
					{
						iconcoverplatePaddingTop = 0;
					}
				}
				canvas.drawBitmap( Icon3D.getCover() , ( textureWidth - Icon3D.getCover().getWidth() ) / 2 , iconcoverplatePaddingTop , null );
			}
			else
			{
				canvas.drawBitmap( Icon3D.getCover() , 0 , 0 , null );
			}
		}
		if( splitTitle != null && splitTitle.length > 0 )
		{
			for( int i = 0 ; i < splitTitle.length ; i++ )
			{
				if( splitTitle[i].equals( "" ) )
					break;
				paint.setTypeface( iLoongLauncher.mTextView.getTypeface() );//zjp
				float textWidth = paint.measureText( splitTitle[i] );
				float paddintText = ( textureWidth - textWidth ) / 2;
				if( textWidth > R3D.workspace_cell_width )
				{
					if( title_bg != null )
					{
						paddintText = title_bg.getWidth() / 10f;
						if( textWidth > textureWidth - 2 * paddintText )
						{
							int length = paint.breakText( splitTitle[i] , 0 , splitTitle[i].length() , true , textureWidth - 2 * paddintText , null );
							splitTitle[i] = splitTitle[i].substring( 0 , length + 1 );
						}
					}
					else
					{
						paddintText = 0;
					}
				}
				// float tmpf = (textureHeight - bmpHeight -
				// 2*singleLineHeight)/ 2;
				// if(tmpf < 0)tmpf = 0;
				// float titleY = bmpHeight + tmpf -
				// fontMetrics.top+i*singleLineHeight;
				float titleY = bmpHeight - fontMetrics.ascent + i * singleLineHeight;
				paint.setColor( Color.WHITE );
				if( title_bg == null )
				{
					paint.setShadowLayer( 2.0f , 0.0f , 2.0f , 0xDD000000 );
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					//teapotXu add start for eton specific requirement
					if( DefaultLayout.setupmenu_yitong && DefaultLayout.icon_title_font == 15 )
					{
						// do nothing
					}
					else
					{
						//						paint.setShadowLayer( SHADOW_SMALL_RADIUS , 0.0f , 0.0f , SHADOW_SMALL_COLOUR );
						canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
					}
				}
				else
				{
					paint.clearShadowLayer();
					canvas.drawText( splitTitle[i] , paddintText , titleY , paint );
				}
				// draw opacity
				if( textWidth > textureWidth && title_bg == null )
				{
					Paint mErasePaint = new Paint();
					mErasePaint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.DST_IN ) );
					mErasePaint.setAntiAlias( true );
					float alphaW = paint.measureText( "x" );
					float a = 255f / alphaW;
					for( int j = 0 ; j < alphaW ; j++ )
					{
						mErasePaint.setAlpha( (int)( a * j ) );
						canvas.drawLine( textureWidth - j - 1 , (float)( titleY - Math.ceil( fontMetrics.descent - fontMetrics.ascent ) ) , textureWidth - j , textureHeight , mErasePaint );
					}
				}
			}
		}
		return bmp;
	}
	
	public TextureRegion getTextureRegion(
			MainAppContext appContext ,
			String title )
	{
		Bitmap backImage = null;
		float width = DefaultLayout.app_icon_size;
		float height = DefaultLayout.app_icon_size;
		float Titletextsize = 30f * scale;
		backImage = Bitmap.createBitmap( (int)width , (int)height , Config.ARGB_8888 );
		Canvas canvas = new Canvas( backImage );
		canvas.drawColor( Color.TRANSPARENT );//.TRANSPARENT .TRANSPARENT
		Paint paint = new Paint();
		paint.setAntiAlias( true );//防锯齿
		paint.setDither( true );//防抖动
		paint.setColor( Color.WHITE );
		paint.setSubpixelText( true );
		paint.setStrokeJoin( Paint.Join.ROUND );
		paint.setStrokeCap( Paint.Cap.ROUND );
		paint.setTextSize( Titletextsize );
		paint.setShadowLayer( 2.0f , 0.0f , 2.0f , 0xDD000000 );
		canvas.drawText( title , ( DefaultLayout.app_icon_size - paint.measureText( title ) ) / 2 , ( DefaultLayout.app_icon_size - 35f * scale ) , paint );
		TextureRegion newTextureRegion = new TextureRegion( new BitmapTexture( backImage ) );
		backImage.recycle();
		return newTextureRegion;
	}
	
	@Override
	public void onEvent(
			int type ,
			BaseTween source )
	{
		if( source == animation_line && type == TweenCallback.COMPLETE )
		{
			isStart = false;
			animation_line.free();
			animation_line = null;
			if( !isOrnotUseCleanMaster )
			{
				if( clearedMemory <= 0 )
				{
					SendMsgToAndroid.sendOurToastMsg( mContext.getResources().getString( R.string.complete_clean ) );
				}
				else
				{
					SendMsgToAndroid.sendOurToastMsg( mContext.getResources().getString( R.string.cleaning , clearedMemory ) );
				}
			}
			mContext.sendBroadcast( new Intent( ACTION_KILL_PROGRESS ) );
			//			Log.i( "clearedMemory" , clearedMemory + "" );
		}
	}
	
	class KillProgressReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			//			Log.d( "kill" , "killProgressReceiver" );
			if( intent.getAction().equals( ACTION_KILL_PROGRESS ) )
			{
				//Log.i("Receive", "Receive---------WidgetClean");
				isInitiativeRefresh = true;
			}
		}
	}
}
