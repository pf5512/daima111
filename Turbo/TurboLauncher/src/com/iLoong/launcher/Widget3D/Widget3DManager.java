package com.iLoong.launcher.Widget3D;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.WidgetIcon;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.adapter.IRefreshRender;
import com.iLoong.launcher.Widget3D.Widget3DHost.Widget3DProvider;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;

import dalvik.system.DexClassLoader;


public class Widget3DManager
{
	
	private static Widget3DManager mWidget3DManagerInstance = null;
	private static final Object mSyncObject = new Object();
	private Widget3DHost mWidget3DHost = null;
	private List<ResolveInfo> mWidgetResolveInfoList;
	private IRefreshRender refreshRender;
	
	public IRefreshRender getRefreshRender()
	{
		return refreshRender;
	}
	
	public static final int WIDGET_STATE_OPEN = 0;
	public static final int WIDGET_STATE_CLOSE = 1;
	private String mWidget3DThemeConfig = "/widget/config.xml";
	private HashMap<String , DexClassLoader> mWidgetClassLoaderHash = null;
	
	public static Widget3DManager getInstance()
	{
		if( mWidget3DManagerInstance == null )
		{
			synchronized( mSyncObject )
			{
				if( mWidget3DManagerInstance == null )
				{
					mWidget3DManagerInstance = new Widget3DManager();
				}
			}
		}
		return mWidget3DManagerInstance;
	}
	
	private Widget3DManager()
	{
		refreshRender = new Widget3DRefreshRender();
		mWidgetClassLoaderHash = new HashMap<String , DexClassLoader>();
		Intent intent = new Intent( "com.iLoong.widget" , null );
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		mWidget3DHost = Widget3DHost.getInstance();
		mWidget3DHost.initialize( mWidgetResolveInfoList );
		// processDefaultWidgetView();
		// preInitializeWidget();
	}
	
	public boolean isWidgetInstalled(
			String packageName )
	{
		boolean isInstalled = false;
		for( int i = 0 ; i < mWidgetResolveInfoList.size() ; i++ )
		{
			if( mWidgetResolveInfoList.get( i ).activityInfo.packageName.equals( packageName ) )
			{
				isInstalled = true;
				break;
			}
		}
		return isInstalled;
	}
	
	/**
	 * 获取Widget3D源数据信�?
	 * 
	 * @param packageName
	 * @return
	 */
	public Widget3DProvider getWidget3DProvider(
			String packageName )
	{
		if( mWidget3DHost != null )
		{
			return mWidget3DHost.getWidget3DProvider( packageName );
		}
		return null;
	}
	
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			String className )
	{
		Class<?> clazz;
		IWidget3DPlugin plugin = null;
		try
		{
			clazz = iLoongLauncher.getInstance().getClassLoader().loadClass( className );
			plugin = (IWidget3DPlugin)clazz.newInstance();
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MainAppContext appContext = new MainAppContext( iLoongLauncher.getInstance() , iLoongLauncher.getInstance() , iLoongLauncher.getInstance() , null );
		return plugin.getWidgetPluginMetaData( appContext , -1 );
	}
	
	// 对Widget进行初始�?主要是预加载一些数�?
	public void preInitializeWidget()
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@SuppressLint( "NewApi" )
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				for( ResolveInfo info : mWidgetResolveInfoList )
				{
					Bundle metaData = info.activityInfo.applicationInfo.metaData;
					if( metaData != null && metaData.containsKey( "preInitialize" ) && metaData.containsKey( "preInitialize" ) && metaData.getBoolean( "preInitialize" ) == true )
					{
						try
						{
							Context widgetPluginContext = iLoongApplication.getInstance().createPackageContext(
									info.activityInfo.packageName ,
									Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
							DexClassLoader loader = getClassLoader( info );
							Class<?> clazz = loader.loadClass( info.activityInfo.name );
							IWidget3DPlugin plugin = (IWidget3DPlugin)clazz.newInstance();
							MainAppContext appContext = new MainAppContext( iLoongLauncher.getInstance() , widgetPluginContext , iLoongLauncher.getInstance() , null );
							plugin.preInitialize( appContext );
						}
						catch( Exception e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		} );
	}
	
	public ResolveInfo getResolveInfo(
			String packageName )
	{
		if( mWidget3DHost != null )
		{
			return mWidget3DHost.getResolveInfo( packageName );
		}
		return null;
	}
	
	class WidgetStruct
	{
		
		public String pkgName;
		public String imageName;
		public String name;
		public int minWidth;
		public int minHeight;
		boolean isDownload;
		public int spanX;
		public int spanY;
		
		public WidgetStruct(
				String name ,
				String image ,
				String pkg ,
				int spanX ,
				int spanY )
		{
			this.pkgName = pkg;
			this.imageName = image;
			this.name = name;
			this.isDownload = false;
			this.spanX = spanX;
			this.spanY = spanY;
		}
	}
	
	public static ArrayList<WidgetIcon> defIcon3DList = new ArrayList<WidgetIcon>();
	public static ShortcutInfo curDownload = null;
	
	public void processDefaultWidgetView()
	{
		ArrayList<WidgetStruct> defWidget = new ArrayList<WidgetStruct>();
		List<ResolveInfo> input = this.mWidgetResolveInfoList;
		String pkgName;
		String imageName;
		String thumbName;//Jone 
		String name;
		int spanX;
		int spanY;
		boolean haveInstall = false;
		// ArrayList<String> delPkgList = new ArrayList<String>();
		for( int i = 0 ; i < DefaultLayout.GetTotalDefaultWidgetNumber() ; i++ )
		{
			pkgName = DefaultLayout.GetDefaultWidgetPkgname( i );
			boolean loadByInternal = DefaultLayout.isWidgetLoadByInternal( pkgName );
			if( loadByInternal )
			{
				thumbName = DefaultLayout.getThumbName( pkgName );
				mWidget3DHost.removeWidget3D( pkgName );
				if( mWidgetResolveInfoList != null )
				{
					for( int j = 0 ; j < this.mWidgetResolveInfoList.size() ; j++ )
					{
						if( mWidgetResolveInfoList.get( j ).activityInfo.packageName.equals( pkgName ) )
						{
							mWidgetResolveInfoList.remove( j );
							break;
						}
					}
				}
				String widgetShortName;
				if( RR.net_version )
					widgetShortName = pkgName.substring( pkgName.lastIndexOf( "." ) + 1 ).toLowerCase( Locale.ENGLISH );
				else
					widgetShortName = pkgName;
				if( thumbName != null && !thumbName.equals( "" ) )
				{
					imageName = "theme/widget/" + widgetShortName + "/" + thumbName + "/image/widget_ico.png";
				}
				else
				{
					imageName = "theme/widget/" + widgetShortName + "/" + ThemeManager.getInstance().getCurrentThemeDescription().widgettheme + "/image/widget_ico.png";
				}
			}
			else
			{
				imageName = DefaultLayout.THEME_WIDGET_APPLIST + DefaultLayout.GetDefaultWidgetImage( i );
			}
			name = DefaultLayout.GetDefaultWidgetName( i );
			spanX = DefaultLayout.GetDefaultWidgetHSpan( i );
			spanY = DefaultLayout.GetDefaultWidgetVSpan( i );
			ResolveInfo widget3d = null;
			haveInstall = false;
			for( ResolveInfo j : input )
			{
				widget3d = j;
				if( j.activityInfo.packageName.equals( pkgName ) )
				{
					haveInstall = true;
					break;
				}
			}
			if( loadByInternal )
			{
				defWidget.add( new WidgetStruct( name , imageName , pkgName , spanX , spanY ) );
			}
			else
			{
				if( !haveInstall )
				{
					defWidget.add( new WidgetStruct( name , imageName , pkgName , spanX , spanY ) );
				}
				else
				{
					if( !loadByInternal )
					{
						// delPkgList.add(pkgName);
						DefaultLayout.installWidget( widget3d , pkgName );
						haveInstall = false;/* to add next one */
					}
				}
			}
		}
		// for (int i = 0; i < delPkgList.size(); i++) {
		// DefaultLayout.RemoveDefWidgetWithPkgname(delPkgList.get(i));
		// }
		Widget3DVirtual icon;
		/* produce uninstall widget */
		for( int j = 0 ; j < defWidget.size() ; j++ )
		{
			ShortcutInfo info = new ShortcutInfo();
			info.title = defWidget.get( j ).name;
			info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
			info.intent.setComponent( new ComponentName( defWidget.get( j ).pkgName , defWidget.get( j ).pkgName ) );
			info.spanX = defWidget.get( j ).spanX;
			info.spanY = defWidget.get( j ).spanY;
			info.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
			Bitmap bmp = ThemeManager.getInstance().getBitmap( defWidget.get( j ).imageName );
			// if (!AppHost3D.V2)
			// bmp = Bitmap.createScaledBitmap(bmp1, R3D.sidebar_widget_w,
			// R3D.sidebar_widget_h, true);
			// if
			// (DefaultLayout.isWidgetLoadByInternal(defWidget.get(j).pkgName))
			// {
			// bmp = Tools.resizeBitmap(bmp,
			// (int) ((88 / 1.5f) * Gdx.graphics.getDensity()),
			// (int) ((88 / 1.5f) * Gdx.graphics.getDensity()));
			// }
			if( bmp == null )
				continue;
			if( RR.net_version )
			{
				if( defWidget.get( j ).pkgName.equals( AppWidgetManager.ACTION_APPWIDGET_PICK ) )
				{
					icon = new OtherTools3DShortcutIn( defWidget.get( j ).name , bmp , defWidget.get( j ).name );
				}
				else
				{
					icon = new Widget3DVirtual( defWidget.get( j ).name , bmp , defWidget.get( j ).name );
					icon.setPckName( defWidget.get( j ).pkgName );
					//icon.packageName=defWidget.get( j ).pkgName;
				}
			}
			else
			{
				icon = new Widget3DVirtual( defWidget.get( j ).name , bmp , defWidget.get( j ).name );
			}
			icon.packageName = defWidget.get( j ).pkgName;
			icon.setItemInfo( info );
			defIcon3DList.add( j , icon );
		}
	}
	
	public boolean hasUninstall(
			String pkgName )
	{
		return PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getBoolean( "UNINSTALL:" + pkgName , false );
	}
	
	public List<Widget3DShortcut> getWidgetList()
	{
		List<Widget3DShortcut> widget_list = new ArrayList<Widget3DShortcut>();
		for( ResolveInfo i : mWidgetResolveInfoList )
		{
			widget_list.add( new Widget3DShortcut( "Widget3DShortcut" , i ) );
		}
		// processDefaultWidgetView();
		// for (ResolveInfo i : mWidgetResolveInfoList) {
		// installWidget(i);
		// }
		return widget_list;
	}
	
	public List<ResolveInfo> getWidgetResolveInfoList()
	{
		return mWidgetResolveInfoList;
	}
	
	public List<Widget3D> getWidget3DInstanceList()
	{
		return mWidget3DHost.getWidget3DInstanceList();
	}
	
	public void updateWidgetInfo(
			String pkg ,
			Widget3DProvider provider )
	{
		mWidget3DHost.updateWidgetInfo( pkg , provider );
	}
	
	public void installWidget(
			ResolveInfo resolveInfo )
	{
		if( DefaultLayout.isWidgetLoadByInternal( resolveInfo.activityInfo.packageName ) )
		{
			return;
		}
		if( !mWidget3DHost.containsWidget3DProvider( resolveInfo.activityInfo.packageName ) )
		{
			ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>();
			list.add( resolveInfo );
			mWidget3DHost.initialize( list );
		}
		// 新安装Widget 后ClassLoader需要更新，所以要删除旧的
		if( mWidgetClassLoaderHash.containsKey( resolveInfo.activityInfo.packageName ) )
		{
			mWidgetClassLoaderHash.remove( resolveInfo.activityInfo.packageName );
		}
		for( int i = 0 ; i < defIcon3DList.size() ; i++ )
		{
			WidgetIcon icon = defIcon3DList.get( i );
			ShortcutInfo info = (ShortcutInfo)icon.getItemInfo();
			if( info.intent.getComponent().getPackageName().equals( resolveInfo.activityInfo.packageName ) )
			{
				// SidebarMainGroup mainGroup = (SidebarMainGroup)
				// root.getSidebar()
				// .getMainGroup();
				// mainGroup.removeWidget3D(icon);
				icon.remove();
				defIcon3DList.remove( i );
			}
		}
		if( DefaultLayout.needAddWidget( resolveInfo.activityInfo.packageName ) )
		{
			Widget3D wid3D = getWidget3D( resolveInfo );
			DefaultLayout.addWidgetView( wid3D , resolveInfo.activityInfo.packageName );
		}
	}
	
	public void unInstallWidget(
			String packageName )
	{
		if( mWidget3DHost.containsWidget3DProvider( packageName ) )
		{
			mWidget3DHost.uninstallWidget3D( packageName );
			String dexOutputDir = iLoongLauncher.getInstance().getApplicationInfo().dataDir;
			dexOutputDir = dexOutputDir + File.separator + "widget" + File.separator + packageName.substring( packageName.lastIndexOf( "." ) + 1 );
			try
			{
				clearWidgetDir( dexOutputDir );
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		// 卸载Widget 后ClassLoader需要更新，所以要删除旧的
		if( mWidgetClassLoaderHash.containsKey( packageName ) )
		{
			mWidgetClassLoaderHash.remove( packageName );
		}
	}
	
	private void clearWidgetDir(
			String widgetDir )
	{
		File dir = new File( widgetDir );
		if( dir == null || !dir.exists() || dir.isFile() )
		{
			return;
		}
		for( File file : dir.listFiles() )
		{
			if( file.isFile() )
			{
				file.delete();
			}
			else if( file.isDirectory() )
			{
				clearWidgetDir( file.getAbsolutePath() );// 递归
			}
		}
		dir.delete();
	}
	
	/**
	 * 从数据库加载Widget3D,通过widget3DInfo创建widget3D，添加到桌面�?
	 * 
	 * @param widget3DInfo
	 *            从数据库查询出的widget相关信息封装
	 * @return
	 */
	public Widget3D getWidget3D(
			Widget3DInfo widget3DInfo )
	{
		Widget3D widget3D = null;
		if( mWidget3DHost.containsWidget3DProvider( widget3DInfo.packageName ) )
		{
			Widget3DProvider provider = mWidget3DHost.getWidget3DProvider( widget3DInfo.packageName );
			ResolveInfo resolveInfo = provider.resolveInfo;
			if( canAddWidget( resolveInfo , false ) )
			{
				WidgetPluginView3D pluginView = ReflectPluginView( resolveInfo , widget3DInfo.widgetId );
				pluginView.setRefreshRender( refreshRender );
				if( pluginView != null )
				{
					widget3D = new Widget3D( "Widget3D" , pluginView );
					widget3D.setWidgetId( widget3DInfo.widgetId );
					widget3D.setPackageName( widget3DInfo.packageName );
					widget3D.setPosition( widget3DInfo.x , widget3DInfo.y );
					widget3D.itemInfo = widget3DInfo;
				}
				// 计算当前最大WidgetId，保存在Provider�?
				provider.addWidgetId( widget3DInfo.widgetId );
			}
		}
		if( widget3D != null )
		{
			mWidget3DHost.addWidget3D( widget3D );
		}
		// Log.v("Widget3DManager", "getWidget3D:" + widget3D.getWidgetId());
		return widget3D;
	}
	
	/**
	 * 根据在manifest中的源数据信息生成widget3D
	 * 
	 * @param resolveInfo
	 * @return
	 */
	public Widget3D getWidget3D(
			ResolveInfo resolveInfo )
	{
		return this.getWidget3D( null , resolveInfo );
	}
	
	public Widget3D getWidget3D(
			Gdx gdx ,
			ResolveInfo resolveInfo )
	{
		Widget3D ret_widget = null;
		// int count = 0;
		// while (true) {
		// Log.e("test", "count=" + count++);
		// int widgetId = mWidget3DHost
		// .generateWidgetID(resolveInfo.activityInfo.packageName);
		// WidgetPluginView3D pluginView = ReflectPluginView(resolveInfo,
		// widgetId);
		// if (pluginView != null) {
		// pluginView.setRefreshRender(refreshRender);
		// ret_widget = new Widget3D("Widget3D", pluginView);
		// ret_widget.setWidgetId(widgetId);
		// ret_widget.setPackageName(resolveInfo.activityInfo.packageName);
		// // Log.v("Widget3DManager", "getWidget3D:" + widgetId);
		// ret_widget.onDelete();
		// ret_widget = null;
		// }
		//
		// // try {
		// // Thread.sleep(100);
		// // } catch (InterruptedException e) {
		// // // TODO Auto-generated catch block
		// // e.printStackTrace();
		// // }
		// }
		if( canAddWidget( resolveInfo , true ) )
		{
			int widgetId = mWidget3DHost.generateWidgetID( resolveInfo.activityInfo.packageName );
			WidgetPluginView3D pluginView = ReflectPluginView( resolveInfo , widgetId );
			if( pluginView != null )
			{
				pluginView.setRefreshRender( refreshRender );
				ret_widget = new Widget3D( "Widget3D" , pluginView );
				ret_widget.setWidgetId( widgetId );
				ret_widget.setPackageName( resolveInfo.activityInfo.packageName );
				// Log.v("Widget3DManager", "getWidget3D:" + widgetId);
			}
		}
		if( ret_widget != null )
		{
			mWidget3DHost.addWidget3D( ret_widget );
		}
		return ret_widget;
	}
	
	/**
	 * 删除widget时需要将目前widget的实例个�?1，否则对widget的个数限定有问题
	 * 
	 * @param widget
	 */
	public void deleteWidget3D(
			Widget3D widget )
	{
		// Log.v("Widget3D*",
		// "********************deleteWidget3D***************:"
		// + widget.getPackageName());
		if( mWidget3DHost.containsWidget3DProvider( widget.getPackageName() ) )
		{
			Widget3DProvider provider = mWidget3DHost.getWidget3DProvider( widget.getPackageName() );
			if( provider.instanceCount > 0 && mWidget3DHost.containsWidget( widget ) )
			{
				provider.deleteWidgetId();
				mWidget3DHost.deleteWidget3DInstance( widget );
				widget.releaseFocus();
				widget.onDelete();
			}
		}
	}
	
	public int getWidgetInstanceCount(
			String packageName )
	{
		if( mWidget3DHost.containsWidget3DProvider( packageName ) )
		{
			Widget3DProvider provider = mWidget3DHost.getWidget3DProvider( packageName );
			return provider.instanceCount;
		}
		return 0;
	}
	
	private boolean canAddWidget(
			final String packageName )
	{
		if( mWidget3DHost.containsWidget3DProvider( packageName ) )
		{
			final Widget3DProvider provider = mWidget3DHost.getWidget3DProvider( packageName );
			if( provider.maxInstanceCount != -1 && provider.maxInstanceCount > 0 )
			{
				if( provider.instanceCount >= provider.maxInstanceCount )
				{
					iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
						
						@Override
						public void run()
						{
							Resources res = iLoongLauncher.getInstance().getResources();
							String appNameKey = "appName";
							int appNameIdentifier = res.getIdentifier( appNameKey , "string" , packageName );
							String appName = res.getString( appNameIdentifier );
							String alertMessage = null;
							int alert_message_Identifier = res.getIdentifier( "max_instance_alert" , "string" , packageName );
							if( alert_message_Identifier != -1 )
							{
								alertMessage = res.getString( alert_message_Identifier );
							}
							if( alertMessage == null || alertMessage.length() == 0 )
							{
								alertMessage = R3D.getString( RR.string.common_add ) + appName + R3D.getString( RR.string.reach_max );
							}
							Toast.makeText( iLoongApplication.getInstance() , R3D.getString( RR.string.already_exist ) , Toast.LENGTH_SHORT ).show();
						}
					} );
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 判断是否可以继续添加widget
	 * 
	 * @param resolveInfo
	 * @return
	 */
	private boolean canAddWidget(
			final ResolveInfo resolveInfo ,
			boolean showAlert )
	{
		if( mWidget3DHost.containsWidget3DProvider( resolveInfo.activityInfo.packageName ) )
		{
			final Widget3DProvider provider = mWidget3DHost.getWidget3DProvider( resolveInfo.activityInfo.packageName );
			final Bundle metaData;
			if( provider.resolveInfo == null )
			{
				provider.resolveInfo = resolveInfo;
				metaData = provider.resolveInfo.activityInfo.applicationInfo.metaData;
			}
			else
			{
				metaData = provider.resolveInfo.activityInfo.applicationInfo.metaData;
			}
			if( metaData != null )
			{
				int maxInstanceCount = metaData.getInt( "max_instance_count" );
				if( maxInstanceCount != -1 && maxInstanceCount > 0 )
				{
					if( provider.instanceCount >= maxInstanceCount )
					{
						if( showAlert )
						{
							iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
								
								@Override
								public void run()
								{
									PackageManager pm = iLoongApplication.getInstance().getPackageManager();
									String appName = resolveInfo.loadLabel( pm ).toString();
									String alertMessage = null;
									try
									{
										Context widgetPluginContext = iLoongApplication.getInstance().createPackageContext(
												resolveInfo.activityInfo.packageName ,
												Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
										widgetPluginContext.getResources().updateConfiguration(
												iLoongApplication.getInstance().getResources().getConfiguration() ,
												iLoongApplication.getInstance().getResources().getDisplayMetrics() );
										int alert_message_Identifier = widgetPluginContext.getResources().getIdentifier( "max_instance_alert" , "string" , resolveInfo.activityInfo.packageName );
										if( alert_message_Identifier != -1 )
										{
											alertMessage = widgetPluginContext.getResources().getString( alert_message_Identifier );
										}
									}
									catch( NameNotFoundException e )
									{
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
									if( DefaultLayout.isWidgetLoadByInternal( resolveInfo.activityInfo.packageName ) )
									{
										alertMessage = null;
									}
									if( alertMessage == null || alertMessage.length() == 0 )
									{
										alertMessage = R3D.getString( RR.string.common_add ) + appName + R3D.getString( RR.string.reach_max );
									}
									// TODO Auto-generated method stub
									Toast.makeText( iLoongApplication.getInstance() , R3D.getString( RR.string.already_exist ) , Toast.LENGTH_SHORT ).show();
								}
							} );
						}
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 通过反射获取WidgetPluginView3D实例
	 * 
	 * @param resolveInfo
	 * @param widgetId
	 * @return
	 */
	private WidgetPluginView3D ReflectPluginView(
			ResolveInfo resolveInfo ,
			int widgetId )
	{
		// Log.e("Widget3DManager", "come in  reflectPluginView");
		WidgetPluginView3D pluginView = null;
		try
		{
			Context widgetPluginContext = iLoongApplication.getInstance().createPackageContext( resolveInfo.activityInfo.packageName , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
			// 设置同launcher语言一�?
			widgetPluginContext.getResources().updateConfiguration(
					iLoongApplication.getInstance().getResources().getConfiguration() ,
					iLoongApplication.getInstance().getResources().getDisplayMetrics() );
			DexClassLoader loader = null;
			if( !mWidgetClassLoaderHash.containsKey( resolveInfo.activityInfo.packageName ) )
			{
				loader = getClassLoader( resolveInfo );
				mWidgetClassLoaderHash.put( resolveInfo.activityInfo.packageName , loader );
			}
			else
			{
				loader = mWidgetClassLoaderHash.get( resolveInfo.activityInfo.packageName );
			}
			// String type=(String)
			// resolveInfo.activityInfo.applicationInfo.metaData.getString("type");
			// Log.d("loader.loadClass", resolveInfo.activityInfo.name);
			Class<?> clazz = ( (WidgetClassLoader)loader ).loadWidgetClass( resolveInfo.activityInfo.name );
			IWidget3DPlugin plugin = (IWidget3DPlugin)clazz.newInstance();
			MainAppContext appContext = new MainAppContext( iLoongLauncher.getInstance() , widgetPluginContext , iLoongLauncher.getInstance() , null );
			appContext.paramsMap.put( "launchcellwidth" , R3D.Workspace_cell_each_width );
			appContext.paramsMap.put( "launchcellheight" , R3D.Workspace_cell_each_height );
			if( ThemeManager.getInstance().getCurrentThemeDescription().widgettheme == null || ThemeManager.getInstance().getCurrentThemeDescription().widgettheme.trim().equals( "" ) )
			{
				appContext.mThemeName = getWidget3DTheme( resolveInfo.activityInfo.packageName , ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName() );
			}
			else
			{
				appContext.mThemeName = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme;
			}
			// if (!appContext.mThemeName.equals("iLoong")) {
			// String packageName = resolveInfo.activityInfo.packageName;
			// String widgetName = packageName.substring(
			// packageName.lastIndexOf(".") + 1).toLowerCase();
			// appContext.mThemeName = checkThemeExist(widgetPluginContext,
			// widgetName, appContext.mThemeName);
			// }
			pluginView = (WidgetPluginView3D)plugin.getWidget( appContext , widgetId );
			if( DefaultLayout.enable_content_staistic )
				pluginView.registerOnCreate( appContext );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		// Log.e("Widget3DManager", "come out  reflectPluginView");
		return pluginView;
	}
	
	public Widget3D getWidget3D(
			final String packageName ,
			final String className )
	{
		Widget3D widget3D = null;
		if( mWidget3DHost.containsWidget3DProvider( packageName ) )
		{
			Widget3DProvider provider = getWidget3DProvider( packageName );
			if( provider.maxInstanceCount != -1 && provider.instanceCount >= provider.maxInstanceCount )
			{
				Widget3D widget = mWidget3DHost.getWidget3D( packageName );
				final WidgetPluginViewMetaData metaData = widget.getPluginViewMetaData();
				if( metaData != null && metaData.maxInstanceAlert != null )
				{
					iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
						
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							Toast.makeText( iLoongLauncher.getInstance() , metaData.maxInstanceAlert , Toast.LENGTH_SHORT ).show();
						}
					} );
				}
				else
				{
					if( DefaultLayout.isWidgetLoadByInternal( packageName ) )
					{
						iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
							
							@Override
							public void run()
							{
								Resources res = iLoongLauncher.getInstance().getResources();
								String appName = DefaultLayout.InternalWidgetTitle( packageName );
								String alertMessage = null;
								if( alertMessage == null || alertMessage.length() == 0 )
								{
									alertMessage = R3D.getString( RR.string.common_add ) + appName + R3D.getString( RR.string.reach_max );
								}
								Toast.makeText( iLoongApplication.getInstance() , R3D.getString( RR.string.already_exist ) , Toast.LENGTH_SHORT ).show();
							}
						} );
					}
				}
				return widget3D;
			}
		}
		int widgetId = -1;
		Utils3D.showTimeFromStart( "load 3dwidget 2.1:" + packageName );
		WidgetPluginView3D pluginView = this.ReflectPluginView( packageName , className , widgetId );
		Utils3D.showTimeFromStart( "load 3dwidget 2.2:" + packageName );
		widgetId = mWidget3DHost.generateWidgetID( packageName );
		if( pluginView != null )
		{
			widget3D = new Widget3D( "Widget3D" , pluginView );
			widget3D.setWidgetId( widgetId );
			widget3D.setPackageName( packageName );
		}
		if( widget3D != null )
		{
			mWidget3DHost.addWidget3D( widget3D );
		}
		return widget3D;
	}
	
	public WidgetPluginView3D ReflectPluginView(
			String packageName ,
			final String className ,
			int widgetId )
	{
		Class<?> clazz = null;
		WidgetPluginView3D pluginView = null;
		try
		{
			clazz = iLoongLauncher.getInstance().getClassLoader().loadClass( className );
			IWidget3DPlugin plugin = null;
			if( clazz != null )
			{
				plugin = (IWidget3DPlugin)clazz.newInstance();
				MainAppContext appContext = new MainAppContext( iLoongLauncher.getInstance() , iLoongLauncher.getInstance() , iLoongLauncher.getInstance() , null );
				if( DefaultLayout.isWidgetLoadByInternal( packageName ) )
				{
					appContext.paramsMap.put( "widgetpackagename" , packageName );
				}
				if( ThemeManager.getInstance().getCurrentThemeDescription().widgettheme == null || ThemeManager.getInstance().getCurrentThemeDescription().widgettheme.trim().equals( "" ) )
				{
					appContext.mThemeName = "iLoong";
				}
				else
				{
					appContext.mThemeName = ThemeManager.getInstance().getCurrentThemeDescription().widgettheme;
				}
				// if (!appContext.mThemeName.equals("iLoong")) {
				// String widgetName = packageName.substring(
				// packageName.lastIndexOf(".") + 1).toLowerCase();
				// appContext.mThemeName = checkThemeExist(ThemeManager
				// .getInstance().getCurrentThemeContext(),
				// widgetName, appContext.mThemeName);
				// }
				pluginView = (WidgetPluginView3D)plugin.getWidget( appContext , widgetId );
				if( DefaultLayout.isWidgetLoadByInternal( packageName ) )
				{
					if( !mWidget3DHost.containsWidget3DProvider( packageName ) )
					{
						WidgetPluginViewMetaData meta = pluginView.getPluginViewMetaData();
						Widget3DProvider provider = mWidget3DHost.newWidget3DProvider();
						if( meta != null )
						{
							provider.maxInstanceCount = meta.maxInstanceCount;
						}
						else
						{
							provider.maxInstanceCount = 1;
						}
						provider.spanX = DefaultLayout.InternalWidgetSpanX( packageName );
						provider.spanY = DefaultLayout.InternalWidgetSpanY( packageName );
						mWidget3DHost.addWidget3DProvider( packageName , provider );
					}
				}
				else
				{
					if( !mWidget3DHost.containsWidget3DProvider( packageName ) )
					{
						WidgetPluginViewMetaData meta = pluginView.getPluginViewMetaData();
						Widget3DProvider provider = mWidget3DHost.newWidget3DProvider();
						provider.spanX = meta.spanX;
						provider.spanY = meta.spanY;
						provider.maxInstanceCount = meta.maxInstanceCount;
						mWidget3DHost.addWidget3DProvider( packageName , provider );
					}
				}
				pluginView.setRefreshRender( refreshRender );
			}
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pluginView;
	}
	
	public String checkThemeExist(
			Context widgetContext ,
			String widgetName ,
			String themeName )
	{
		try
		{
			String widgetThemeDir = "theme/widget/" + widgetName + "/" + themeName;
			String themeDir = ThemeManager.getInstance().getAssetFileDir( widgetContext , widgetThemeDir , true );
			if( themeDir == null )
			{
				return "iLoong";
			}
			// boolean foundTheme = false;
			// String[] themeArray = widgetContext.getAssets().list("");
			// for (String tmpTheme : themeArray) {
			// if (tmpTheme.equals(themeName)) {
			// foundTheme = true;
			// break;
			// }
			// }
			// if (!foundTheme) {
			// themeName = "iLoong";
			// }
		}
		catch( Exception e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return themeName;
	}
	
	/**
	 * 获取ClassLoader，用于加载类
	 * 
	 * @param resolveInfo
	 * @return
	 */
	@SuppressLint( "NewApi" )
	private DexClassLoader getClassLoader(
			ResolveInfo resolveInfo )
	{
		// Log.e("Widget3DManager", "come in  getClassLoader");
		ActivityInfo ainfo = resolveInfo.activityInfo;
		String dexPath = ainfo.applicationInfo.sourceDir;
		// String dexOutputDir = ainfo.applicationInfo.dataDir;
		// 插件输出目录，目前为launcher的子目录
		String dexOutputDir = iLoongLauncher.getInstance().getApplicationInfo().dataDir;
		dexOutputDir = dexOutputDir + File.separator + "widget" + File.separator + ainfo.packageName.substring( ainfo.packageName.lastIndexOf( "." ) + 1 );
		creatDataDir( dexOutputDir );
		// String libPath = ainfo.applicationInfo.nativeLibraryDir;
		Integer sdkVersion = Integer.valueOf( android.os.Build.VERSION.SDK );
		String libPath = null;
		if( sdkVersion > 8 )
		{
			libPath = ainfo.applicationInfo.nativeLibraryDir;
		}
		WidgetClassLoader loader = new WidgetClassLoader( dexPath , dexOutputDir , libPath , iLoongApplication.getInstance().getClassLoader() );
		// DexClassLoader loader = new DexClassLoader(dexPath, dexOutputDir,
		// libPath, iLoongApplication.ctx.getClassLoader());
		// Log.e("Widget3DManager", "come out  getClassLoader");
		return loader;
	}
	
	private File creatDataDir(
			String dirName )
	{
		File dir = new File( dirName );
		if( !dir.exists() )
		{
			dir.mkdirs();
		}
		return dir;
	}
	
	public void pauseAllWidget3D()
	{
		List<Widget3D> widget3DList = getWidget3DInstanceList();
		if( widget3DList != null )
		{
			for( int i = 0 ; i < widget3DList.size() ; i++ )
			{
				widget3DList.get( i ).onPause();
			}
		}
	}
	
	public void startAllWidget3D()
	{
		List<Widget3D> widget3DList = getWidget3DInstanceList();
		if( widget3DList != null )
		{
			for( int i = 0 ; i < widget3DList.size() ; i++ )
			{
				widget3DList.get( i ).onStart();
			}
		}
	}
	
	public void stopAllWidget3D()
	{
		List<Widget3D> widget3DList = getWidget3DInstanceList();
		if( widget3DList != null )
		{
			for( int i = 0 ; i < widget3DList.size() ; i++ )
			{
				widget3DList.get( i ).onStop();
			}
		}
	}
	
	public void resumeAllWidget3D()
	{
		List<Widget3D> widget3DList = getWidget3DInstanceList();
		if( widget3DList != null && widget3DList.size() > 0 )
		{
			for( int i = 0 ; i < widget3DList.size() ; i++ )
			{
				if( DefaultLayout.enable_content_staistic )
				{
					Widget3D widget = widget3DList.get( i );
					JSONObject tmp = Assets.config;
					Context mContext = iLoongLauncher.getInstance();
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
					// long uptime =
					// pref.getLong("startup-"+widget.getPackageName(), 0);
					long uptime = pref.getLong( "use-" + widget.getPackageName() , 0 );
					long curtime = System.currentTimeMillis();
					Calendar up = Calendar.getInstance();
					up.setTimeInMillis( uptime );
					Calendar cur = Calendar.getInstance();
					cur.setTimeInMillis( System.currentTimeMillis() );
					System.out.println( "up.get(Calendar.DAY_OF_MONTH) = " + up.get( Calendar.DAY_OF_MONTH ) + " cur.get(Calendar.DAY_OF_MONTH) = " + cur.get( Calendar.DAY_OF_MONTH ) );
					if( up.get( Calendar.DAY_OF_MONTH ) != cur.get( Calendar.DAY_OF_MONTH ) || up.get( Calendar.MONTH ) != cur.get( Calendar.MONTH ) || up.get( Calendar.YEAR ) != cur
							.get( Calendar.YEAR ) )
					{
						try
						{
							// System.out.println("startup --- widget");
							System.out.println( "use --- widget" );
							Context context = mContext.createPackageContext( widget.getPackageName() , Context.CONTEXT_IGNORE_SECURITY );
							int version = context.getPackageManager().getPackageInfo( context.getPackageName() , 0 ).versionCode;
							JSONObject config = tmp.getJSONObject( "config" );
							String appid = config.getString( "app_id" );
							String mobileSn = config.getString( "serialno" );
							// StatisticsExpandNew.startUp(mContext,
							// mobileSn,appid,
							// CooeeSdk.cooeeGetCooeeId(mContext),
							// 2, widget.getPackageName(), ""+ version);
							// pref.edit().putLong("startup-"+widget.getPackageName(),
							// curtime).commit();
							pref.edit().putLong( "use-" + widget.getPackageName() , curtime ).commit();
						}
						catch( NameNotFoundException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch( JSONException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				widget3DList.get( i ).onResume();
			}
		}
	}
	
	public void updateWidget3DInfo()
	{
		Intent intent = new Intent( "com.iLoong.widget" , null );
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
		mWidget3DHost = Widget3DHost.getInstance();
		mWidget3DHost.initialize( mWidgetResolveInfoList );
		preInitializeWidget();
	}
	
	public String getWidget3DTheme(
			String packageName ,
			String hostTheme )
	{
		Widget3DTheme theme3D = new Widget3DTheme();
		theme3D.setWidgetThemeConfig( mWidget3DThemeConfig );
		return theme3D.getWidget3DThemeName( packageName , hostTheme );
	}
}
