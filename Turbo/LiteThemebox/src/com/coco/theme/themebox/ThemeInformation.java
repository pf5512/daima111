package com.coco.theme.themebox;


import java.io.File;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.model.ThemeInfoItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.service.ThemeDescription;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.PathTool;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.base.themebox.R;


public class ThemeInformation
{
	
	// private final String LOG_TAG = "LockInformation";
	private static final String THEME_PREFIX = "CoCo主题_";
	protected String className = "";
	protected boolean installed = false;
	protected String displayName = "";
	protected ThemeInfoItem themeInfo = new ThemeInfoItem();
	protected long downloadSize = 0;
	protected DownloadStatus downloadStatus = DownloadStatus.StatusInit;
	protected Bitmap thumbImage = null;
	protected Bitmap IconImage=null;
	public Drawable iconDrawable;
	protected boolean needLoadDetail = true;
	protected boolean mSystem = false;
	protected boolean downloaded = false;
	
	public ThemeInformation()
	{
	}
	
	public String getPackageName()
	{
		return themeInfo.getPackageName();
	}
	
	public String getClassName()
	{
		return className;
	}
	
	public boolean isInstalled(
			Context context )
	{
		if( !installed )
		{
			return isAppInstalled( context , getPackageName() );
		}
		return installed;
	}
	
	private boolean isAppInstalled(
			Context cxt ,
			String uri )
	{
		PackageManager pm = cxt.getPackageManager();
		boolean installed = false;
		try
		{
			pm.getPackageInfo( uri , 0 );
			installed = true;
		}
		catch( PackageManager.NameNotFoundException e )
		{
			installed = false;
		}
		return installed;
	}
	
	public boolean isSystem()
	{
		return mSystem;
	}
	
	public boolean isDownloaded(
			Context context )
	{
		File file = new File( PathTool.getAppDir() + "/" + getPackageName() + ".apk" );
		if( !file.exists() && ( getDownloadStatus() == DownloadStatus.StatusInit || getDownloadStatus() == DownloadStatus.StatusFinish ) )
		{
			File f1 = new File( PathTool.getDownloadingDir() + getPackageName() + "_app.tmp" );
			f1.delete();
			DownloadThemeService dSv = new DownloadThemeService( context );
			dSv.updateDownloadSizeAndStatus( getPackageName() , 0 , DownloadStatus.StatusInit , DownloadList.Theme_Type );
			return false;
		}
		return downloaded;
	}
	
	public void setSystem(
			boolean system )
	{
		mSystem = system;
	}
	
	public long getApplicationSize()
	{
		return themeInfo.getApplicationSize();
	}
	
	public String getAuthor(
			Context context )
	{
		if( getAuthor() != null && !getAuthor().equals( "" ) )
		{
			return getAuthor();
		}
		return context.getString( R.string.unknown );
	}
	
	public String getAuthor()
	{
		return themeInfo.getAuthor();
	}
	
	public int getPrice()
	{// zjp
		return themeInfo.getPrice();
	}
	
	public String getPricePoint()
	{
		return themeInfo.getPricepoint();
	}
	
	public String getIntroduction()
	{
		return themeInfo.getIntroduction();
	}
	
	public ThemeInfoItem getInfoItem()
	{
		return themeInfo;
	}
	
	public String getApplicationName_en()
	{
		return themeInfo.getApplicationName_en();
	}
	
	public String getIntroduction_en()
	{
		return themeInfo.getIntroduction_en();
	}
	
	public int getDownloadPercent()
	{
		if( themeInfo.getApplicationSize() <= 0 )
		{
			return 0;
		}
		int result = (int)( downloadSize * 100 / themeInfo.getApplicationSize() );
		if( result < 0 )
		{
			result = 0;
		}
		else if( result > 100 )
		{
			result = 100;
		}
		return result;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	public Bitmap getThumbImage()
	{
		if( thumbImage != null && thumbImage.isRecycled() )
		{
			thumbImage = null;
		}
		return thumbImage;
	}
	
	public Bitmap getIconImage() {
		if( IconImage != null && IconImage.isRecycled() )
		{
			IconImage = null;
		}
		return IconImage;
	}

	public void setIconImage(Bitmap iconImage) {
		IconImage = iconImage;
	}

	public void setThumbImage(
			Context mContext ,
			String pkgName ,
			String actName )
	{
		try
		{
			File f = mContext.getDir( "theme" , Context.MODE_PRIVATE );
			String thumbPath = f + "/" + pkgName + "/" + actName + ".tupian";
			thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );
		}
		catch( OutOfMemoryError e )
		{
			e.printStackTrace();
		}
	}
	
	public boolean isNeedLoadDetail()
	{
		if( thumbImage == null || thumbImage.isRecycled() )
		{
			return true;
		}
		return needLoadDetail;
	}
	
	public DownloadStatus getDownloadStatus()
	{
		return downloadStatus;
	}
	
	public void setDownloadStatus(
			DownloadStatus status )
	{
		downloadStatus = status;
	}
	
	public void setDownloadSize(
			long downSize )
	{
		downloadSize = downSize;
	}
	
	public void setTotalSize(
			long totalSize )
	{
		themeInfo.setApplicationSize( totalSize );
	}
	
	public String getEnginepackname()
	{
		return themeInfo.getEnginepackname();
	}
	
	public String getEngineurl()
	{
		return themeInfo.getEngineurl();
	}
	
	public String getEnginesize()
	{
		return themeInfo.getEnginesize();
	}
	
	public String getEnginedesc()
	{
		return themeInfo.getEnginedesc();
	}
	
	public String getThirdparty()
	{
		return themeInfo.getThirdparty();
	}
	
	public boolean isComponent(
			String pkgName ,
			String clsName )
	{
		if( themeInfo.getPackageName().equals( pkgName ) && className.equals( clsName ) )
		{
			return true;
		}
		return false;
	}
	
	public boolean isComponent(
			ComponentName comName )
	{
		if( themeInfo.getPackageName().equals( comName.getPackageName() ) && className.equals( comName.getClassName() ) )
		{
			return true;
		}
		return false;
	}
	
	public boolean isPackage(
			String pkgName )
	{
		return themeInfo.getPackageName().equals( pkgName );
	}
	
	public void setActivity(
			Context cxt ,
			ActivityInfo activity )
	{
		className = activity.name;
		installed = true;
		disposeThumb();
		thumbImage = null;
		iconDrawable = activity.loadIcon( cxt.getPackageManager() );
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName( activity.packageName );
		downloadSize = 0;
		displayName = activity.loadLabel( cxt.getPackageManager() ).toString();
		checkThemePrefix();
		mSystem = activity.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME );
		// CoCo桌面修改成默认主�?
		String defaultTheme = ThemesDB.LAUNCHER_PACKAGENAME;
		if( ThemesDB.default_theme_package_name != null )
			defaultTheme = ThemesDB.default_theme_package_name;
		if( activity.packageName.equals( defaultTheme ) )
			displayName = cxt.getString( R.string.default_theme );
		else if( activity.packageName.equals( ThemesDB.LAUNCHER_PACKAGENAME ) )
			displayName = cxt.getString( R.string.system_theme );
	}
	
	public void setTheme(
			Context cxt ,
			ThemeDescription des )
	{
		className = des.componentName.getClassName();
		installed = true;
		mSystem = des.mSystem;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusFinish;
		themeInfo = new ThemeInfoItem();
		themeInfo.setPackageName( des.componentName.getPackageName() );
		downloadSize = 0;
		displayName = des.title.toString();
		checkThemePrefix();
	}
	
	public void setDownloadItem(
			DownloadThemeItem item )
	{
		setThemeItem( item.getThemeInfo() );
		downloadStatus = item.getDownloadStatus();
		downloadSize = item.getDownloadSize();
		downloaded = true;
	}
	
	public void setThemeItem(
			ThemeInfoItem item )
	{
		className = "";
		installed = false;
		disposeThumb();
		thumbImage = null;
		needLoadDetail = true;
		downloadStatus = DownloadStatus.StatusInit;
		themeInfo.copyFrom( item );
		downloadSize = 0;
		String systemLauncher = Locale.getDefault().getLanguage().toString();
		if( systemLauncher.equals( "zh" ) )
		{
			displayName = themeInfo.getApplicationName();
		}
		else
		{
			displayName = themeInfo.getApplicationName_en();
		}
		checkThemePrefix();
		downloaded = false;
		mSystem = false;
	}
	
	public void loadDetail(
			Context cxt )
	{
		needLoadDetail = false;
		disposeThumb();
		thumbImage = null;
		if( installed )
		{
			ContentConfig cfg = new ContentConfig();
			try
			{
				Context remoteContext = cxt.createPackageContext( themeInfo.getPackageName() , Context.CONTEXT_IGNORE_SECURITY );
				cfg.loadConfig( remoteContext , className );
				loadInstallDetail( remoteContext , cfg );
			}
			catch( NameNotFoundException e )
			{
				e.printStackTrace();
			}
		}
		else
		{
			String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
			if( new File( thumbPath ).exists() )
			{
				try
				{
					thumbImage = Tools.getPurgeableBitmap( thumbPath , Tools.dip2px( cxt , FunctionConfig.getGridWidth() ) , Tools.dip2px( cxt , FunctionConfig.getGridHeight() ) );//BitmapFactory.decodeFile( thumbPath );
				}
				catch( OutOfMemoryError error )
				{
					Log.v( "thumbImage" , error.toString() );
				}
			}
		}
	}
	
	public Bitmap getThumbImage(
			Context remoteContext ,
			ContentConfig cfg )
	{
		return cfg.loadThumbImage( remoteContext );
	}
	
	public void loadInstallDetail(
			Context remoteContext ,
			ContentConfig cfg )
	{
		needLoadDetail = false;
		disposeThumb();
		thumbImage = null;
		installed = true;
		thumbImage = getThumbImage( remoteContext , cfg );
		themeInfo.setApplicationName( cfg.getApplicationName() );
		themeInfo.setVersionCode( cfg.getVersionCode() );
		themeInfo.setVersionName( cfg.getVersionName() );
		themeInfo.setApplicationSize( cfg.getApplicationSize() );
		downloadSize = cfg.getApplicationSize();
		themeInfo.setAuthor( cfg.getAuthor() );
		themeInfo.setIntroduction( cfg.getIntroduction() );
		themeInfo.setUpdateTime( cfg.getUpdateTime() );
		if( !mSystem )
		{
			mSystem = cfg.getReflection();
		}
	}
	
	public void reloadThumb()
	{
		// Log.d(LOG_TAG, "reloadThumb,pkg="+lockInfo.getPackageName());
		String thumbPath = PathTool.getThumbFile( themeInfo.getPackageName() );
		if( new File( thumbPath ).exists() )
		{
			try
			{
				thumbImage = Tools.getPurgeableBitmap( thumbPath , -1 , -1 );//BitmapFactory.decodeFile( thumbPath );
			}
			catch( OutOfMemoryError error )
			{
				error.printStackTrace();
			}
		}
	}
	
	protected void checkThemePrefix()
	{
		String namePrefix[] = { "CoCo主题_" , "CoCoTheme_" , "CoCo主題_" };
		for( String name : namePrefix )
		{
			if( displayName != null && displayName.startsWith( name ) )
			{
				displayName = displayName.substring( name.length() );
				break;
			}
		}
	}
	
	public void disposeThumb()
	{
		if( thumbImage != null && !thumbImage.isRecycled() )
		{
			Log.v( "meminfo" , "meminfo recycle " + "(w,h)=" + thumbImage.getWidth() + " " + thumbImage.getHeight() + " " + thumbImage );
			thumbImage.recycle();
		}
		thumbImage = null;
	}
}
