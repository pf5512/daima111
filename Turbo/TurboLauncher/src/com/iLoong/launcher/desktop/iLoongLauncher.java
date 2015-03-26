package com.iLoong.launcher.desktop;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FixedResolutionStrategy;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.blueflash.interfaces.IKingsReviveService2;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.wallpaper.wallpaperbox.PathTool;
import com.cooee.android.launcher.framework.LauncherModel;
import com.cooee.android.launcher.framework.LauncherProvider;
import com.cooee.android.launcher.framework.LauncherSettings;
import com.cooeeui.brand.turbolauncher.R;
import com.cooeeui.brand.turbolauncher.R.id;
import com.cooeeui.brand.turbolauncher.R.layout;
import com.cooeeui.brand.turbolauncher.R.string;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.ApkConfig;
import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.AppReminder;
import com.iLoong.launcher.Desktop3D.ApplicationListHost;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.CooeeWeather;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.MediaView3D;
import com.iLoong.launcher.Desktop3D.PageIndicator3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Desktop3D.circleSomethingDraw;
import com.iLoong.launcher.DesktopEdit.CustomShortcutIcon;
import com.iLoong.launcher.DesktopEdit.DesktopEditHost;
import com.iLoong.launcher.DesktopEdit.PopImageView3D;
import com.iLoong.launcher.DesktopEdit.ThemeAndPagerHelper;
import com.iLoong.launcher.Folder3D.Folder3D;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.Folder3D.FolderMIUI3D;
import com.iLoong.launcher.Functions.EffectPreview.EffectPreview3D;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.HotSeatMainGroup;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.SetupMenu.ForegroundServiceClient;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.DesktopAction.DesktopSettingActivity;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.AboutActivity;
import com.iLoong.launcher.SetupMenu.Actions.DesktopSettings.FirstActivity;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.Widget3D.NewsFlowReflect;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Workspace.AddListAdapter;
import com.iLoong.launcher.Workspace.AddWidget3DListAdapter;
import com.iLoong.launcher.Workspace.AddWidget3DListAdapter.ListItem;
import com.iLoong.launcher.Workspace.CellLayout;
import com.iLoong.launcher.Workspace.Workspace;
import com.iLoong.launcher.Workspace.Workspace.WorkspaceStatusEnum;
import com.iLoong.launcher.action.ActionHolder;
import com.iLoong.launcher.app.AirDefaultLayout;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.app.LauncherBase;
import com.iLoong.launcher.camera.CameraManager;
import com.iLoong.launcher.clean.InstallCleanMasterActivity;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.excpetion.ActErrorReport;
import com.iLoong.launcher.miui.WorkspaceEditView;
import com.iLoong.launcher.newspage.DownloadApkManager;
import com.iLoong.launcher.newspage.DownloadDialog;
import com.iLoong.launcher.newspage.NewsView;
import com.iLoong.launcher.newspage.NewspageShakeListener;
import com.iLoong.launcher.pub.UmEventUtil;
import com.iLoong.launcher.pub.provider.PubProviderHelper;
import com.iLoong.launcher.search.QSearchGroup;
import com.iLoong.launcher.search.SearchEditTextGroup;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.ultils.ImmersiveModeUtils;
import com.iLoong.launcher.ultils.SystemInfoUtils;
import com.iLoong.launcher.update.UpdateManager;
import com.iLoong.launcher.wallpeper.ShakeListener;
import com.iLoong.launcher.wallpeper.ThemeShakeListener;
import com.iLoong.launcher.wallpeper.WallpaperShakeListener;
import com.iLoong.launcher.widget.Widget;
import com.iLoong.launcher.widget.Widget.MotionEventPool;
import com.iLoong.launcher.widget.WidgetHost;
import com.iLoong.launcher.widget.WidgetHostView;
import com.iLoong.theme.adapter.DownloadLockBoxService;
import com.iLoong.theme.adapter.ThemeConfig;
import com.mediatek.common.widget.IMtkWidget;
import com.thirdParty.analytics.umeng.UmengMobclickAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import dalvik.system.DexClassLoader;


public class iLoongLauncher extends AndroidApplication implements MenuActionListener , LauncherBase
{
	
	public static final String TAG = "Launcher";
	static final boolean LOGD = false;
	static final boolean PROFILE_STARTUP = false;
	static final boolean DEBUG_WIDGETS = false;
	private boolean mWaitingForResult;
	public static final int APPWIDGET_HOST_ID = 1024;
	private static final int REQUEST_CREATE_SHORTCUT_FROM_DROP = 2;
	private static final int REQUEST_CREATE_CONTACT_SHORTCUT = 3;
	private static final int REQUEST_CREATE_LIVE_FOLDER = 4;
	private static final int REQUEST_CREATE_APPWIDGET = 5;
	private static final int REQUEST_PICK_APPLICATION = 6;
	private static final int REQUEST_PICK_SHORTCUT = 7;
	private static final int REQUEST_PICK_LIVE_FOLDER = 8;
	public static final int REQUEST_PICK_APPWIDGET = 9;
	private static final int REQUEST_PICK_WALLPAPER = 10;
	private static final int REQUEST_PICK_3DWIDGET = 11;
	private static final int REQUEST_BIND_APPWIDGET = 12;
	public static final int ACTION_WIFI_SETTINGS = 13;
	public static final int ACTION_BLUETOOTH_SETTINGS = 14;
	public static final int ACTION_DATA_ROAMING_SETTINGS = 15;
	public static final int ACTION_LOCATION_SOURCE_SETTINGS = 16;
	public static final int ACTION_DISPLAY_SETTINGS = 17;
	private static final String LAUNCHER_STATE_PAUSE = "com.android.launcher.changed.pause";
	private static final String LAUNCHER_STATE_RESUME = "com.android.launcher.changed.resume";
	static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
	public static final int SCREEN_COUNT = 5;
	public static final int DEFAULT_SCREEN = 2;
	public static final int NUMBER_CELLS_X = 4;
	public static final int NUMBER_CELLS_Y = 4;
	static final int DIALOG_CREATE_SHORTCUT = 1;
	static final int DIALOG_RENAME_FOLDER = 2;
	static final int DIALOG_CIRCLE_DELALL = 3;
	static final int DIALOG_CREATE_SYSTEM_WIDGET = 4;
	static final int DIALOG_DELETE_PAGE = 5;
	static final int DIALOG_DELETE_FOLDER = 6;
	static final int DIALOG_DOWNLOAD_WIDGET = 7;
	static final int DIALOG_SORT_APP = 8;
	static final int DIALOG_PICK_3DWIDGET = 9;
	static final int DIALOG_APK_CANNOT_FOUND = 10;
	static final int DIALOG_RESTART = 11;
	static final int DIALOG_APP_EFFECT = 12;
	static final int DIALOG_MAINMENU_BG = 13;
	public int popResult = Workspace3D.CIRCLE_POP_NONE_ACTION;
	public int trashdeleteFolderResult = Workspace3D.CIRCLE_POP_NONE_ACTION;
	private AppWidgetManager mAppWidgetManager;
	public WidgetHost mAppWidgetHost;
	private FolderInfo mFolderInfo;
	public FolderIcon3D folderIcon;
	private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();
	private Bundle mSavedState;
	private SpannableStringBuilder mDefaultKeySsb = null;
	private boolean mWorkspaceLoading = true;
	private boolean PageDeleteDialogShow = false;
	private boolean ShortcutDialogShow = false;
	private boolean mRestoring = false;
	protected static boolean isOrNotSendBroadcast = true;
	private Bundle mSavedInstanceState;
	public LinearLayout mMainMenuLayout;
	private LauncherModel mModel;
	private static HashMap<Long , FolderInfo> sFolders = new HashMap<Long , FolderInfo>();
	public Workspace xWorkspace;
	private static circleSomethingDraw circleDrawView;
	private static iLoongLauncher mInstance;
	public static BluetoothAdapter adapter;
	ProgressDialog m_dialog;
	public static boolean dialogCancelled = false;
	public Desktop3DListener d3dListener;
	public CooeeWeather cooeeweather;
	private String[] mHotseatConfig = null;
	public String[] mDefaultHotseatConfig = null;
	private Intent[] mHotseats = null;
	private Bitmap[] mHotseatIcons = null;
	public static String[] mHotseatIconsName = null;
	private CharSequence[] mHotseatLabels = null;
	private final int[] mCellCoordinates = new int[2];
	private Vibrator mVibrator;
	private int vibratorAble = 1;
	private boolean showIntroductionAgain = false;
	private int sortAppCheckId = -1;
	private int sortOrigin = -1;
	public static final int SORT_ORIGIN_APPLIST = 0;
	public static final int SORT_ORIGIN_ADD_APP_TO_FOLDER = 1;
	private boolean showIntroduction = false;
	public boolean introductionShown = false;
	private int mWallpaperWidth;
	private int mWallpaperHeight;
	private WallpaperManager mWallpaperManager;
	private static final float WALLPAPER_SCREENS_SPAN = 2f;
	private ComponentName mDeviceAdminSample;
	private Vector2 clickPoint = new Vector2();
	static final String ACTION_RESTART = "com.cooee.launcher.restart";
	static final String EXTRA_SEARCH_WIDGET = "com.iLoong.widget";
	static final String EXTRA_CUSTOM_WIDGET = "iLoong.widget.3d";
	public static boolean writeBootTime = false;
	public static long bootTime = -1;
	private AddWidget3DListAdapter widget3DArray;
	private Toast toast;
	private ShakeListener mShaker;
	private boolean installAssertAPK = false;
	public static long _time = 0;
	public static boolean releaseTexture = false;
	private String currentLocale = null;
	public static String languageSpace = "";
	/**
	 * 0:zh_CN, 1: zh_TW, 2:en
	 */
	public static int curLanguage = 0;
	public static int mainThreadId;
	public static boolean showAllAppFirst = false;
	public static String RUNTIME_STATE_SHOWALLAPP = "showAllApp";
	public static String RUNTIME_STATE_RESTART = "restart";
	public static boolean booting = false;
	public boolean stoped = false;
	public boolean hasRemoveGLView = false;
	public boolean checkSize = false;
	private mWeatherReceiver mweatherreceiver = null;
	private View glView;
	public static final String WALLPAPER_BOX_MAIN_ACTIVITY = "com.coco.wallpaper.wallpaperbox.MainActivity";
	public static final String SCENE_BOX_MAIN_ACTIVITY = "com.coco.scene.scenebox.MainActivity";
	public static TextView mTextView;
	private ForegroundServiceClient fsc;
	public boolean themeChanging = false;
	static final String STATUS_BAR_MANAGER = "android.app.StatusBarManager";
	public static Method methodSetViewToStatusBar;
	public final String STATUS_BAR_SERVICE = "statusbar";
	private static final String ACTION_CLEAN_OVER = "action_clean_over";
	private static final String ACTION_CLEAN_FAIL = "action_clean_fail";
	public static Object clientService;
	public static boolean findClientMethod = false;
	public static ArrayList<SysMenuItem> desktopMenuList = new ArrayList<SysMenuItem>();
	public static ArrayList<SysMenuItem> appMenuList = new ArrayList<SysMenuItem>();
	public boolean optionsMenuOpen = false;
	public ThemeConfig themeConfig;
	public static boolean isTrimMemory = false;
	public NewsView newsView;
	public AllScreenFrameLayout newsScreenframeLayout = null;
	public static boolean isShowNews = false;
	public static boolean finishLoad = false;
	private static String widgetDownloadgoogleplayUri = "https://play.google.com/store/apps/details?id=";
	public ArrayList<ShortcutInfo> recentApp = new ArrayList<ShortcutInfo>();
	private static Thread cleanThread = null;
	private Thread updateThread = null;
	private static int upDateTime = 1000 * 60 * 60 * 24 * 3;
	private IKingsReviveService2 mIService = null;
	boolean isNeedClean = false;
	public static int entryWorksapceCount = 0;
	public final static int DOCKBAR_DELAY_COUNT = 2;
	public boolean exist = false;
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(
				ComponentName arg0 ,
				IBinder arg1 )
		{
			mIService = IKingsReviveService2.Stub.asInterface( arg1 );
			if( isNeedClean )
			{
				doCleanProcess();
				isNeedClean = false;
			}
		}
		
		@Override
		public void onServiceDisconnected(
				ComponentName arg0 )
		{
			mIService = null;
			Log.i( "ydy4" , "*****conn*****onServiceDisConnected**" );
		}
	};
	
	private void doCleanProcess()
	{
		Log.i( "ydy4" , "***doCleanProcess****" );
		if( mIService != null )
		{
			try
			{
				mIService.doKillProcessResult( true );
				if( isOrNotSendBroadcast )
				{
					this.sendBroadcast( new Intent( ACTION_CLEAN_OVER ) );
				}
				Log.i( "ydy4" , "***doCleanProcess****ok" );
			}
			catch( RemoteException e )
			{
				if( isOrNotSendBroadcast )
				{
					this.sendBroadcast( new Intent( ACTION_CLEAN_FAIL ) );
				}
				e.printStackTrace();
			}
		}
		else
		{
			Log.i( "ydy4" , "***doCleanProcess****null bindServicee" );
			isNeedClean = true;
			bindMyService();
			if( isOrNotSendBroadcast )
			{
				this.sendBroadcast( new Intent( ACTION_CLEAN_FAIL ) );
			}
		}
	}
	
	private void bindMyService()
	{
		Intent intent = new Intent( "com.kingsgame.sdks.kingsdaemon2" );
		bindService( intent , conn , Context.BIND_AUTO_CREATE );
	}
	
	//	*****************************闪电结束**********************************
	class SysMenuItem
	{
		
		public int id;
		public String name;
		public String icon;
		public Bitmap iconbmp;
		public Drawable icondrawable;
	}
	
	public static final int MENU_GROUP_DESKTOP = 0;
	public static final int MENU_GROUP_MAINMENU = 1;
	public ThemeCenterDownload themeCenterDown;
	
	private class AppWidgetResetObserver extends ContentObserver
	{
		
		public AppWidgetResetObserver()
		{
			super( new Handler() );
		}
		
		@Override
		public void onChange(
				boolean selfChange )
		{
			onAppWidgetReset();
		}
	}
	
	private void onAppWidgetReset()
	{
		mAppWidgetHost.startListening();
	}
	
	public View setCircleView()
	{
		LayoutInflater inflater = getLayoutInflater();
		circleDrawView = (circleSomethingDraw)inflater.inflate( layout.circleselect , null );
		return circleDrawView;
	}
	
	public static circleSomethingDraw getCircleView()
	{
		return circleDrawView;
	}
	
	public static iLoongLauncher getInstance()
	{
		return mInstance;
	}
	
	public CooeeWeather getCooeeWeather()
	{
		return CooeeWeather.getInstance();
	}
	
	public Intent getHotSeatIntent(
			int index )
	{
		return mHotseats[index];
	}
	
	public int equalHotSeatIntent(
			Intent intent )
	{
		for( int i = 0 ; i < mHotseatConfig.length ; i++ )
		{
			if( mHotseats[i].filterEquals( intent ) )
			{
				return i;
			}
		}
		return -1;
	}
	
	public Bitmap findHotSeatBitmap(
			int index )
	{
		if( index == -1 || index >= mHotseatConfig.length )
		{
			return null;
		}
		if( mHotseatIcons[index] == null || mHotseatIcons[index].isRecycled() )
		{
			mHotseatIcons[index] = GetBmpFromImageName( mHotseatIconsName[index] , mHotseats[index] , index );
		}
		return mHotseatIcons[index];
	}
	
	public Intent getHotSeatIntent(
			String findStr )
	{
		for( int i = 0 ; i < mHotseatConfig.length ; i++ )
		{
			if( mHotseatConfig[i].equals( findStr ) )
			{
				return mHotseats[i];
			}
		}
		return null;
	}
	
	public String getHotSeatString(
			int index )
	{
		if( index == -1 || index >= mHotseatConfig.length )
		{
			if( DefaultLayout.hotseatbar_browser_special_name )
				return getString( string.Internet );
			else
				return getString( string.Explorer );
		}
		String retStr = null;
		retStr = mHotseatLabels[index].toString();
		if( retStr == null )
		{
			if( DefaultLayout.hotseatbar_browser_special_name )
				retStr = getString( string.Internet );
			else
				retStr = getString( string.Explorer );
		}
		return retStr;
	}
	
	public int getHotSeatLength()
	{
		return mHotseatConfig.length;
	}
	
	private void deletePackFile()
	{
		File dir = new File( getApplicationInfo().dataDir + "/" );
		if( dir.exists() )
		{
			File[] files = dir.listFiles();
			if( files == null )
				return;
			for( File file : files )
			{
				if( file.isFile() )
					file.delete();
			}
		}
	}
	
	public FolderIcon3D getOpenFolder()
	{
		if( d3dListener == null )
		{
			return null;
		}
		return d3dListener.getOpenFolder();
	}
	
	public SharedPreferences prefs;
	private BroadcastReceiver desktopsettings = null;
	/************************ added by diaosixu begin ***************************/
	public AppReminder appReminder;
	
	/************************ added by diaosixu end ***************************/
	public boolean isAppFirstRun()
	{
		boolean flag = false;
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		flag = prefs.getBoolean( "isAppFirstRun" , true );
		if( flag )
		{
			prefs.edit().putBoolean( "isAppFirstRun" , false ).commit();
		}
		return flag;
	}
	
	@Override
	public void onCreate(
			Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		themeConfig = iLoongApplication.themeConfig;
		prefs = this.getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		mTextView = new TextView( getApplicationContext() );
		mainThreadId = android.os.Process.myTid();
		_time = System.currentTimeMillis();
		mInstance = this;
		dialogCancelled = false;
		if( VERSION.SDK_INT == 19 && isAppFirstRun() )
		{
			finish();
			Intent intent = new Intent();
			intent.setPackage( this.getPackageName() );
			intent.setAction( Intent.ACTION_MAIN );
			intent.addCategory( Intent.CATEGORY_HOME );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			startActivity( intent );
			System.exit( 0 );
		}
		if( iLoongApplication.needRestart )
		{
			SystemAction.RestartSystem();
			return;
		}
		Utils3D.calibration();
		hideNavigationBar();
		checkCurrentLanguage();
		initHotseats();
		DefaultLayout.getInstance().DefaultLayout_init2();
		checkLiteEdition();
		if( DefaultLayout.enable_texture_pack )
		{
			deletePackFile();
		}
		if( getFirstRun() )
		{
			ShortcutInfo.isFirstRun = true;
		}
		else
		{
			ShortcutInfo.isFirstRun = false;
		}
		if( iLoongApplication.BuiltIn )
			installAssertAPK = true;
		AppListDB.getInstance().Init( iLoongLauncher.getInstance() );
		initBase();
		// 开启硬件加速
		if( DefaultLayout.enable_hardware_accel )
		{
			getWindow().setFlags( WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED , WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED );
		}
		if( DefaultLayout.hide_launcher_wallpapers )
		{
			PackageManager pm = getPackageManager();
			pm.setComponentEnabledSetting( new ComponentName( this , "com.iLoong.launcher.desktop.WallpaperChooser" ) , PackageManager.COMPONENT_ENABLED_STATE_DISABLED , 0 );
		}
		if( mWallpaperManager == null )
		{
			mWallpaperManager = WallpaperManager.getInstance( iLoongLauncher.this );
		}
		applyWallpaperForFirstRun();
		d3dListener = new Desktop3DListener( this );
		glView = initializeDesktop3D( d3dListener , true );
		setContentView( glView );
		glView.setLayoutParams( createLayoutParams() );
		View androidView = LayoutInflater.from( this ).inflate( layout.workspace , null );
		addContentView( androidView , createLayoutParams() );
		if( !DefaultLayout.disable_circled )
			addContentView( setCircleView() , createLayoutParams() );
		SetupMenuActions.getInstance().init( this );
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		SetupMenu.mScreenScale = displayMetrics.density;
		SetupMenu.mScale = SetupMenu.mScreenScale / SetupMenu.BITMAPSCALE;
		iLoongApplication app = ( (iLoongApplication)getApplication() );
		app.setNeed2Exit( false );
		mModel = app.setLauncher( this );
		mAppWidgetManager = AppWidgetManager.getInstance( this );
		mAppWidgetHost = new WidgetHost( this , APPWIDGET_HOST_ID );
		mAppWidgetHost.startListening();
		xWorkspace = (Workspace)findViewById( id.workspace );
		xWorkspace.setLauncher( this );
		int cellNum = ThemeManager.getInstance().getThemeDB().getScreenCount();
		if( cellNum == 0 )
		{
			cellNum = DefaultLayout.default_workspace_pagecounts;
			ThemeManager.getInstance().getThemeDB().SaveScreenCount( cellNum );
		}
		for( int i = 0 ; i < cellNum ; i++ )
		{
			xWorkspace.addCell();
		}
		showIntroduction();
		registerContentObservers();
		mSavedState = savedInstanceState;
		restoreState( mSavedState );
		mDefaultKeySsb = new SpannableStringBuilder();
		Selection.setSelection( mDefaultKeySsb , 0 );
		mVibrator = (Vibrator)getSystemService( VIBRATOR_SERVICE );
		setActionListener();
		SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		vibratorAble = share.getInt( "vibratorAble" , SetupMenuActions.getInstance().getStringToIntger( "vibrator" ) );
		Root3D.IsProhibiteditMode = SetupMenuActions.getInstance().getBoolean( getString( string.setting_key_edit_mode ) );
		setVolumeControlStream( 2 );
		if( DefaultLayout.enable_service )
		{
			fsc = new ForegroundServiceClient( this );
			fsc.StartServer();
		}
		themeCenterDown = new ThemeCenterDownload();
		registerAllReceivers();
		if( !DefaultLayout.enable_google_version || VERSION.SDK_INT >= 15 )
		{
			initNewsView();
		}
		/************************ added by diaosixu begin ***************************/
		appReminder = new AppReminder( this );
		/************************ added by diaosixu end ***************************/
		// statusBar&navigationBar immersive, hide menu key
		ImmersiveInit();
		//绑定闪电清理大师
		bindMyService();
		//		FeedbackInit();
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.update( iLoongLauncher.this );
		UmengUpdateAgent.setRichNotification( true );
		UmengUpdateAgent.setUpdateUIStyle( UpdateStatus.STYLE_NOTIFICATION );
		updateThread = new UpdateThread( "updateThread" );
		updateThread.start();
	}
	
	class UpdateThread extends Thread
	{
		
		public UpdateThread(
				String name )
		{
			super( name );
		}
		
		public void run()
		{
			for( ; ; )
			{
				Log.i( "upDate" , "upDate111" );
				try
				{
					Thread.sleep( upDateTime );
					UmengUpdateAgent.setDefault();
					UmengUpdateAgent.update( iLoongLauncher.this );
					UmengUpdateAgent.setRichNotification( true );
					UmengUpdateAgent.setUpdateListener( updateListener );
					UmengUpdateAgent.setUpdateUIStyle( UpdateStatus.STYLE_NOTIFICATION );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	UmengUpdateListener updateListener = new UmengUpdateListener() {
		
		@Override
		public void onUpdateReturned(
				int updateStatus ,
				UpdateResponse updateInfo )
		{
			switch( updateStatus )
			{
				case UpdateStatus.Yes: //has update
					//upDateTime1 = 1000 * 120;
					upDateTime = 1000 * 60 * 60 * 24 * 3;//每三天检测一次更新
					break;
				case UpdateStatus.No: //has no update
					//upDateTime1 = 1000 * 120;
					upDateTime = 1000 * 60 * 60 * 24 * 3;//每三天检测一次更新
					break;
				case UpdateStatus.NoneWifi: //none wifi
					upDateTime = 1000 * 60 * 60;
					//upDateTime1 = 1000 * 60;
					Log.i( "updateListener" , "NoneWifi" );
					break;
				case UpdateStatus.Timeout: //time out
					upDateTime = 1000 * 60 * 60;
					Log.i( "updateListener" , "Timeout" );
					//upDateTime1 = 1000 * 60;
					break;
				default:
					upDateTime = 1000 * 60 * 60;
					Log.i( "updateListener" , "default" );
					//upDateTime1 = 1000 * 60;
					break;
			}
			AboutActivity.isAllowOnclick = true;
		}
	};
	
	/** 适配特定机型,删除导航栏 */
	private void hideNavigationBar()
	{
		if( Utils3D.hasMeiZuSmartBar() )
		{
			requestWindowFeature( Window.FEATURE_NO_TITLE );
			Utils3D.hideNavigationBar( getWindow().getDecorView() );
		}
	}
	
	private void checkCurrentLanguage()
	{
		String lan = Locale.getDefault().getLanguage();
		if( lan.equals( "zh" ) )
		{
			languageSpace = "";
			lan = Locale.getDefault().toString();
			if( lan.equals( "zh_TW" ) )
				curLanguage = 1;
			else
				curLanguage = 0;
		}
		else
		{
			languageSpace = " ";
			curLanguage = 2;
		}
		currentLocale = this.getResources().getConfiguration().locale.toString();
	}
	
	private void applyWallpaperForFirstRun()
	{
		if( ThemeManager.getInstance().getThemeDB().getThemesStatus() > 0 )
		{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
			if( DefaultLayout.enable_scene_wallpaper && ThemeManager.getInstance().currentThemeIsSystemTheme() )
			{
				pref.edit().putString( "currentWallpaper" , "default" ).commit();
				PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , "default" );
				Intent intent = new Intent( this , WallpaperChangedReceiver.class );
				intent.setAction( WallpaperChangedReceiver.SCENE_WALLPAPER_CHANGE );
				PendingIntent pendingIntent = PendingIntent.getBroadcast( this , 0 , intent , 0 );
				AlarmManager am = (AlarmManager)getSystemService( ALARM_SERVICE );
				am.set( AlarmManager.RTC , System.currentTimeMillis() , pendingIntent );
			}
			else
			{
				new Thread( new Runnable() {
					
					public void run()
					{
						ThemeManager.getInstance().ApplyWallpaper();
					}
				} ).start();
				ThemeManager.getInstance().getThemeDB().SaveThemesStatus( 0 );
			}
		}
		else
		{
			// launcher被强制停止，重新开始情景壁纸
			if( DefaultLayout.enable_scene_wallpaper )
			{
				Intent intent = new Intent( this , WallpaperChangedReceiver.class );
				intent.setAction( WallpaperChangedReceiver.SCENE_WALLPAPER_CHANGE );
				PendingIntent pendingIntent = PendingIntent.getBroadcast( this , 0 , intent , 0 );
				AlarmManager am = (AlarmManager)getSystemService( ALARM_SERVICE );
				am.set( AlarmManager.RTC , System.currentTimeMillis() , pendingIntent );
			}
		}
	}
	
	private void registerAllReceivers()
	{
		if( DefaultLayout.dynamic_icon )
		{
			mweatherreceiver = new mWeatherReceiver();
			mweatherreceiver.registerweatherReceiver( this );
		}
		desktopsettings = new DesktopSettingsReceiver();
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction( "com.cooee.desktopsettings.system.vibratorchange" );
		intentfilter.addAction( "com.cooee.desktopsettings.screen.screenchange" );
		intentfilter.addAction( "com.cooee.desktopsettings.drawer.drawerchange" );
		intentfilter.addAction( "com.cooee.desktopsettings.drawer.gridlinechange" );
		intentfilter.addAction( "com.cooee.desktopsettings.iconsizechange" );
		registerReceiver( desktopsettings , intentfilter );
	}
	
	private void ImmersiveInit()
	{
		if( SystemInfoUtils.isKitKat() || SystemInfoUtils.isGalaxyS3() )
		{
			ImmersiveModeUtils.init( this , prefs );
			ImmersiveModeUtils.enableImmersiveModeIfSupported( this );
		}
	}
	
	private void FeedbackInit()
	{
		SharedPreferences sp = getSharedPreferences( Assets.PREFERENCE_KEY_CONFIG , Activity.MODE_WORLD_READABLE );
		String appId = sp.getString( Assets.PREFERENCE_KEY_CONFIG_APPID , "" );
		feedbackjni.startService( appId );
	}
	
	private void initThemeBox()
	{
		themeConfig.defaultThemePackageName = DefaultLayout.default_theme_package_name;
		themeConfig.launcherPackageName = RR.getPackageName();
		themeConfig.launcherApplyThemeAction = ThemeReceiver.ACTION_LAUNCHER_APPLY_THEME;
		themeConfig.launcherRestartAction = ThemeReceiver.ACTION_LAUNCHER_RESTART;
		themeConfig.page_effect_no_radom_style = DefaultLayout.page_effect_no_radom_style;
		if( DefaultLayout.custom_wallpapers_path.equals( "" ) )
		{
			if( DefaultLayout.getInstance().isCustomAssetsFileExist( "/launcher/wallpapers" ) )
			{
				themeConfig.customWallpaperPath = DefaultLayout.custom_assets_path + "/launcher/wallpapers";
			}
			else
			{
				themeConfig.customWallpaperPath = DefaultLayout.custom_wallpapers_path;
			}
		}
		else
		{
			themeConfig.customWallpaperPath = DefaultLayout.custom_wallpapers_path;
		}
		themeConfig.disable_set_wallpaper_dimensions = DefaultLayout.disable_set_wallpaper_dimensions;
		themeConfig.isdoovStyle = DefaultLayout.enable_doov_spec_customization;
		themeConfig.setGalleryPkg( DefaultLayout.getGalleryPackage() );
		themeConfig.isEffectVisiable = DefaultLayout.enable_effect_preview;
		themeConfig.personal_center_internal = DefaultLayout.personal_center_internal;
	}
	
	private void initBase()
	{
		UtilsBase.init( this );
		Messenger.init( mMainHandler );
		ConfigBase.disable_double_click = DefaultLayout.disable_double_click;
		ConfigBase.widget_revise_complete = DefaultLayout.widget_revise_complete;
		ConfigBase.halfTapSquareSize = DefaultLayout.halfTapSquareSize;
		ConfigBase.enable_news = true;// = DefaultLayout.enable_news;
		ConfigBase.releaseGL = DefaultLayout.release_gl;
		ConfigBase.net_version = RR.net_version;
		// zjp
		initThemeBox();
	}
	
	private void checkLiteEdition()
	{
		if( DefaultLayout.lite_edition )
		{
			DefaultLayout.enable_texture_pack = false;
			DefaultLayout.disable_circled = true;
			DefaultLayout.blur_enable = false;
			DefaultLayout.enable_air_default_layout = false;
			DefaultLayout.enable_hotseat_rolling = false;
			DefaultLayout.newHotSeatMainGroup = false;
			// DefaultLayout.enable_shell = false;
		}
	}
	
	protected FrameLayout.LayoutParams createLayoutParams()
	{
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams( android.view.ViewGroup.LayoutParams.MATCH_PARENT , android.view.ViewGroup.LayoutParams.MATCH_PARENT );
		layoutParams.gravity = Gravity.BOTTOM;
		return layoutParams;
	}
	
	public void StopServer()
	{
		if( DefaultLayout.enable_service )
			fsc.StopServer();
	}
	
	private void activeManage()
	{
		// 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
		Intent intent = new Intent( DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN );
		// 权限列表
		intent.putExtra( DevicePolicyManager.EXTRA_DEVICE_ADMIN , mDeviceAdminSample );
		// 描述(additional explanation)
		intent.putExtra( DevicePolicyManager.EXTRA_ADD_EXPLANATION , "------ 其他描述 ------" );
		iLoongLauncher.getInstance().startActivityForResult( intent , 1 );
	}
	
	public void toStartLoader()
	{
		new Thread( new Runnable() {
			
			// int i = 0;
			@Override
			public void run()
			{
				synchronized( Desktop3DListener.lock )
				{
					while( !Desktop3DListener.bCreat1Done )
					{
						try
						{
							Desktop3DListener.lock.wait();
						}
						catch( InterruptedException e )
						{
							e.printStackTrace();
						}
					}
				}
				if( !mRestoring )
				{
					mModel.startLoader( mInstance , true );
				}
			}
		} ).start();
	}
	
	public View initializeDesktop3D(
			Desktop3DListener listener ,
			boolean useGL2IfAvailable )
	{
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = useGL2IfAvailable;
		config.useAccelerometer = false;
		config.useCompass = false;
		if( DefaultLayout.screen_mess )
		{
			config.numSamples = 0;
			config.depth = 1;
			config.stencil = 1;
		}
		else
		{
			config.numSamples = 2;
		}
		config.a = 8;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.handleKeyTypedChinese = true;
		config.resolutionStrategy = new FixedResolutionStrategy( Utils3D.getScreenWidth() , Utils3D.getScreenHeight() );
		config.pauseEglHelper = DefaultLayout.pauseEglHelper;
		return initializeForView( listener , config );
	}
	
	// teapotXu add start
	public boolean WhetherCurWallPaperIsOurPics()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		String currentWallpaper = pref.getString( "currentWallpaper" , "default" );
		boolean b_userdefined_wallpaper = pref.getBoolean( "userDefinedWallpaper" , false );
		WallpaperManager wallpaperManager = WallpaperManager.getInstance( iLoongLauncher.getInstance() );
		WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
		if( wallpaperInfo != null )
		{// 在墙纸变换的广播不接收动态壁纸的变化，因此这边只有动态壁纸时，才get
			b_userdefined_wallpaper = true;
			pref.edit().putBoolean( "userDefinedWallpaper" , true ).commit();
		}
		Log.v( "cooee" , "iLoongLauncher ---- WhetherCurWallPaperIsOurPics --- currentWallpaper: " + currentWallpaper );
		Log.v( "cooee" , "iLoongLauncher ---- WhetherCurWallPaperIsOurPics --- b_userdefined_wallpaper: " + b_userdefined_wallpaper );
		if( b_userdefined_wallpaper == true && currentWallpaper.equals( "other" ) )
		{
			return false;
		}
		return true;
	}
	
	// teapotXu add end
	protected void setWallpaperDimension()
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		final int maxDim = Math.max( displayMetrics.widthPixels , displayMetrics.heightPixels );
		final int minDim = Math.min( displayMetrics.widthPixels , displayMetrics.heightPixels );
		// We need to ensure that there is enough extra space in the wallpaper
		// for the intended
		// parallax effects
		// if (LauncherApplication.isScreenLarge()) {
		// mWallpaperWidth = (int) (maxDim *
		// wallpaperTravelToScreenWidthRatio(maxDim, minDim));
		// mWallpaperHeight = maxDim;
		// } else {
		// }
		Drawable drawable = null;
		try
		{
			drawable = mWallpaperManager.getDrawable();
		}
		catch( Exception e )
		{
		}
		if( drawable != null )
		{
			mWallpaperWidth = drawable.getIntrinsicWidth();
			mWallpaperHeight = drawable.getIntrinsicHeight();
			if( mWallpaperWidth < minDim )
				mWallpaperWidth = minDim;
			if( mWallpaperHeight < maxDim )
				mWallpaperHeight = maxDim;
			// teapotXu add start: 如果是Coco提供壁纸，当获取到壁纸大于2倍屏宽时，强制设置屏宽为2倍屏宽
			if( WhetherCurWallPaperIsOurPics() )
			{
				if( mWallpaperWidth > minDim * WALLPAPER_SCREENS_SPAN )
					mWallpaperWidth = (int)( minDim * WALLPAPER_SCREENS_SPAN );
				if( mWallpaperHeight > maxDim )
				{
					mWallpaperHeight = maxDim;
				}
			}
			// teapotXu add end
		}
		else
		{
			// when drawable is null, return
			Log.v( "cooee" , "iLoongLauncher --- setWallpaperDimension --- get Wallpaper Drawable == null " );
			return;
			// mWallpaperWidth = Math.max((int) (minDim *
			// WALLPAPER_SCREENS_SPAN),
			// maxDim);
			// mWallpaperHeight = maxDim;
		}
		if( DefaultLayout.disable_move_wallpaper )
		{
			// teapotXu add start for mv wallpaper's config menu
			if( DefaultLayout.enable_configmenu_for_move_wallpaper )
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
				if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == false )
				{
					// also disable move wallpaper
					mWallpaperWidth = minDim;
				}
				else
				{
					// if(mWallpaperWidth < ((int) (minDim *
					// WALLPAPER_SCREENS_SPAN))){
					// mWallpaperWidth = (int) (minDim *
					// WALLPAPER_SCREENS_SPAN);
					// }
				}
			}
			else
			{
				mWallpaperWidth = minDim;
			}
			// teapotXu add end
		}
		new Thread( "setWallpaperDimension" ) {
			
			public void run()
			{
				mWallpaperManager.suggestDesiredDimensions( mWallpaperWidth , mWallpaperHeight );
				// teapotXu add start for mv wallpaper's config menu
				if( DefaultLayout.enable_configmenu_for_move_wallpaper )
				{
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() );
					if( prefs.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , true ) == false )
					{
						IBinder token = iLoongLauncher.getInstance().getWindow().getCurrentFocus().getWindowToken();
						if( token != null )
						{
							mWallpaperManager.setWallpaperOffsets( token , 0 , 0 );
						}
					}
				}
				// teapotXu add end
			}
		}.start();
		// zhujieping add
		if( !ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) && !DefaultLayout.miui_v5_folder )
		{
			if( drawable != null )
				( (BitmapDrawable)drawable ).getBitmap().recycle();
		}
	}
	
	public void onStart()
	{
		stoped = false;
		if( !DefaultLayout.disable_set_wallpaper_dimensions )
		{
			setWallpaperDimension();
		}
		if( DefaultLayout.broadcast_state )
		{
			sendBroadcast( new Intent( "com.cooee.launcher.action.start" ) );
		}
		super.onStart();
		if( ( (iLoongApplication)getApplication() ).need2Exit() )
		{
			Log.d( "ANDROID_LAB" , "ActOccurError.finish()" );
			iLoongLauncher.this.finish();
		}
	}
	
	public void ReportError()
	{
		// 处理记录于error.log中的异常
		String errorContent = "";// getErrorLog();
		if( errorContent != null )
		{
			Intent intent = new Intent( this , ActErrorReport.class );
			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			intent.putExtra( "error" , errorContent );
			intent.putExtra( "by" , "error.log" );
			startActivity( intent );
		}
	}
	
	private void launchHotButton()
	{
		startActivitySafely( mHotseats[0] , "hotseat" );
	}
	
	private Uri getDefaultBrowserUri()
	{
		String url = getString( RR.string.default_browser_url );
		if( url.indexOf( "{CID}" ) != -1 )
		{
			url = url.replace( "{CID}" , "android-google" );
		}
		return Uri.parse( url );
	}
	
	private boolean isUserAppEx(
			ResolveInfo info )// 不同于Applist里的isUserApp，内置应用的更新版本也被认为是内置应�?
	{
		boolean canUninstall = false;
		int flags = info.activityInfo.applicationInfo.flags;
		if( ( flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP ) != 0 )
		{
			canUninstall = false;
		}
		else if( ( flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM ) == 0 )
		{
			canUninstall = true;
		}
		return canUninstall;
	}
	
	private Bitmap GetBmpFromImageName(
			String imageName ,
			Intent intent ,
			int index )
	{
		Bitmap image = null;
		if( imageName != null )
		{
			image = ThemeManager.getInstance().getBitmap( imageName );
			image = Tools.resizeBitmap( image , DefaultLayout.app_icon_size , DefaultLayout.app_icon_size );
		}
		// float density = getResources().getDisplayMetrics().density;
		// if(intent.getPackage() != null || image == null ||
		// !DefaultLayout.hotseat_hide_title)
		{
			boolean bNeedDeal = false;
			PackageManager pm = getPackageManager();
			List<ResolveInfo> allMatches = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
			if( mHotseatConfig[index].equals( "*BROWSER*" ) )
			{
				if( DefaultLayout.hotseatbar_browser_special_name )
					mHotseatLabels[index] = getString( RR.string.Internet );
				else
					mHotseatLabels[index] = getString( RR.string.Explorer );
				bNeedDeal = true;
			}
			else if( mHotseatConfig[index].equals( "*MAINMENU*" ) )
			{
				mHotseatLabels[index] = getString( RR.string.mainmenu );
			}
			for( ResolveInfo ri : allMatches )
			{
				// if ((ri.activityInfo.applicationInfo.flags &
				// ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)
				if( isUserAppEx( ri ) == false )
				{
					if( bNeedDeal )
					{
						// mHotseatLabels[index] =
						// getString(RR.string.Explorer);//getString(RR.string.Internet);//ri.activityInfo.loadLabel(pm);
						String packageName = ri.activityInfo.applicationInfo.packageName;
						if( findBrowserByPackageName( packageName ) )
						{
							if( image == null )
							{
								Drawable drawable = ri.activityInfo.loadIcon( pm );
								image = Utilities.createIconBitmap( drawable , getApplicationContext() );
							}
							bNeedDeal = false;
							break;
						}
					}
					else
					{
						if( image == null && ri.activityInfo.applicationInfo.packageName != null )
						{
							DefaultLayout.getInstance().setDefaultIcon();
							String iconPath = DefaultLayout.getInstance().getReplaceIconPath( ri.activityInfo.applicationInfo.packageName , ri.activityInfo.applicationInfo.className );
							image = ThemeManager.getInstance().getCurrentThemeBitmap( iconPath );
							if( image != null )
							{
								image = Tools.resizeBitmap( image , (int)( DefaultLayout.app_icon_size ) , (int)( DefaultLayout.app_icon_size ) );
							}
						}
						if( image == null )
						{
							Drawable drawable = ri.activityInfo.loadIcon( pm );
							image = Utilities.createIconBitmap( drawable , getApplicationContext() );
							if( Icon3D.getIconBg() != null )
							{
								image = Tools.resizeBitmap( image , DefaultLayout.thirdapk_icon_scaleFactor );
							}
						}
						mHotseatLabels[index] = ri.activityInfo.loadLabel( pm );
						break;
					}
				}
				else
				{
					if( intent.getPackage() != null )
					{
						intent.setPackage( null );
						return GetBmpFromImageName( imageName , intent , index );
					}
				}
			}
		}
		if( mHotseatLabels[index] == null )
		{
			if( DefaultLayout.hotseatbar_browser_special_name )
				mHotseatLabels[index] = getString( RR.string.Internet );
			else
				mHotseatLabels[index] = getString( RR.string.Explorer );
		}
		return image;
	}
	
	private void initHotseats()
	{
		if( mHotseatConfig == null )
		{
			mHotseatConfig = getResources().getStringArray( RR.array.hotseats );
			if( mHotseatConfig.length > 0 )
			{
				mHotseats = new Intent[mHotseatConfig.length];
				mHotseatLabels = new CharSequence[mHotseatConfig.length];
				mHotseatIcons = new Bitmap[mHotseatConfig.length];
				mHotseatIconsName = getResources().getStringArray( RR.array.hotseat_icons );
				for( int i = 0 ; i < mHotseatIconsName.length ; i++ )
				{
					mHotseatIconsName[i] = mHotseatIconsName[i];
				}
			}
			else
			{
				mHotseats = null;
				mHotseatIcons = null;
				mHotseatLabels = null;
				mHotseatIconsName = null;
			}
		}
		mDefaultHotseatConfig = getResources().getStringArray( RR.array.hotseats );
	}
	
	public void initHotseatItem(
			int item ,
			String imageName ,
			String configName )
	{
		mHotseatConfig[item] = configName;
		mHotseatIconsName[item] = imageName;
	}
	
	// onThemeChanged
	public void changeHotseats()
	{
		for( int i = 0 ; i < mHotseatConfig.length ; i++ )
		{
			if( mHotseatIcons[i] != null )
			{
				mHotseatIcons[i].recycle();
				mHotseatIcons[i] = null;
			}
		}
	}
	
	public void loadHotseats(
			String customUri )
	{
		PackageManager pm = getPackageManager();
		for( int i = 0 ; i < mHotseatConfig.length ; i++ )
		{
			Intent intent = null;
			if( mHotseatConfig[i].equals( "*BROWSER*" ) )
			{
				// magic value meaning "launch user's default web browser"
				// replace it with a generic web request so we can see if there
				// is indeed a default
				String defaultUri;// = getString(RR.string.default_browser_url);
				if( customUri != null )
				{
					defaultUri = customUri;
				}
				else
				{
					defaultUri = DefaultLayout.googleHomePage;// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
				}
				//	intent = new Intent( Intent.ACTION_VIEW  ).addCategory( Intent.CATEGORY_BROWSABLE );
				intent = new Intent( Intent.ACTION_VIEW , ( ( defaultUri != null ) ? Uri.parse( defaultUri ) : getDefaultBrowserUri() ) ).addCategory( Intent.CATEGORY_BROWSABLE );
				if( DefaultLayout.default_explorer != null )
				{
					intent.setPackage( DefaultLayout.default_explorer );
					Utils3D.showTimeFromStart( "9.1" + i );
					List<ResolveInfo> allMatches = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
					Utils3D.showTimeFromStart( "9.2" + i );
					if( allMatches == null || allMatches.size() == 0 )
					{
						intent.setPackage( null );
					}
				}
				// note: if the user launches this without a default set, she
				// will always be taken to the default URL above; this is
				// unavoidable as we must specify a valid URL in order for the
				// chooser to appear, and once the user selects something, that
				// URL is unavoidably sent to the chosen app.
			}
			else
			{
				try
				{
					intent = Intent.parseUri( mHotseatConfig[i] , 0 );
					//Jone add ,google has already remove the sms app upper to 4.4
					if( RR.net_version )
					{
						if( i == 3 )
						{
							final PackageManager packageManager = this.getPackageManager();
							List<ResolveInfo> msgIntents = packageManager.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES );
							if( msgIntents == null || msgIntents.size() < 1 )
							{
								if( Integer.valueOf( android.os.Build.VERSION.SDK_INT ) >= 19 )
								{
									intent = new Intent( Intent.ACTION_MAIN );
									intent.setComponent( new ComponentName( "com.google.android.talk" , "com.google.android.talk.SigningInActivity" ) );
								}
							}
						}
					}
					//Jone add
				}
				catch( java.net.URISyntaxException ex )
				{
					Log.w( TAG , "Invalid hotseat intent: " + mHotseatConfig[i] );
					// bogus; leave intent=null
				}
			}
			if( intent == null )
			{
				mHotseats[i] = null;
				mHotseatLabels[i] = getText( RR.string.activity_not_found );
				continue;
			}
			mHotseats[i] = intent;
			mHotseatIcons[i] = GetBmpFromImageName( mHotseatIconsName[i] , intent , i );
		}
	}
	
	public void initDefaultHotseatItem(
			int item ,
			String configName )
	{
		mDefaultHotseatConfig[item] = configName;
	}
	
	public boolean isDefaultHotseats(
			Intent intent )
	{
		PackageManager pm = getPackageManager();
		Intent hotseatintent = null;
		String sun1;
		String sun2;
		for( int i = 0 ; i < mDefaultHotseatConfig.length ; i++ )
		{
			if( mDefaultHotseatConfig[i].equals( "*BROWSER*" ) )
			{
				String defaultUri;// = getString(RR.string.default_browser_url);
				defaultUri = DefaultLayout.googleHomePage;// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
				// }
				hotseatintent = new Intent( Intent.ACTION_VIEW , ( ( defaultUri != null ) ? Uri.parse( defaultUri ) : getDefaultBrowserUri() ) ).addCategory( Intent.CATEGORY_BROWSABLE );
				if( DefaultLayout.default_explorer != null )
				{
					hotseatintent.setPackage( DefaultLayout.default_explorer );
					List<ResolveInfo> allMatches = pm.queryIntentActivities( intent , PackageManager.MATCH_DEFAULT_ONLY );
					if( allMatches == null || allMatches.size() == 0 )
					{
						hotseatintent.setPackage( null );
					}
				}
			}
			else
			{
				try
				{
					hotseatintent = Intent.parseUri( mDefaultHotseatConfig[i] , 0 );
				}
				catch( java.net.URISyntaxException ex )
				{
					Log.w( TAG , "Invalid hotseat intent: " + mDefaultHotseatConfig[i] );
				}
			}
			if( hotseatintent == null || intent == null )
			{
				continue;
			}
			if( intent.filterEquals( hotseatintent ) )
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 读取是否有未处理的报错信�?每次读取后都会将error.log清空>
	 * 
	 * @return 返回未处理的报错信息或null
	 */
	private String getErrorLog()
	{
		File fileErrorLog = new File( iLoongApplication.PATH_ERROR_LOG );
		String content = null;
		FileInputStream fis = null;
		try
		{
			if( fileErrorLog.exists() )
			{
				byte[] data = new byte[(int)fileErrorLog.length()];
				fis = new FileInputStream( fileErrorLog );
				fis.read( data );
				content = new String( data );
				data = null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( fis != null )
				{
					fis.close();
				}
				if( fileErrorLog.exists() )
				{
					fileErrorLog.delete();
				}
			}
			catch( Exception e )
			{
				e.printStackTrace();
			}
		}
		return content;
	}
	
	public Handler mMainHandler = new Handler() {
		
		@SuppressLint( "NewApi" )
		public void handleMessage(
				Message msg )
		{
			Bundle b = msg.getData();
			PackageManager pm = SetupMenuActions.getInstance().getContext().getPackageManager();
			final String TAG_WALLPAPER = "tagWallpaper";
			final String TAG_STRING = "currentTab";
			final String TAG_FONT = "tagFont";
			final String TAG_EFFECT = "tagEffect";
			final String TAG_LOCK = "tagLock";
			final String TAG_THEME = "tagTheme";
			final String TAG_SCENE = "tagScene";
			switch( msg.what )
			{
				case Messenger.MSG_SHOW_NEWSVIEW_HANDLE:
					if( newsView != null && DefaultLayout.enable_news && DefaultLayout.show_newspage_with_handle )
					{
						newsView.getNewsLeftHandle().setVisibility( View.VISIBLE );
						newsView.getNewsRightHandle().setVisibility( View.VISIBLE );
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								if( Desktop3DListener.root != null )
								{
									if( Desktop3DListener.root.newsHandle != null )
									{
										Desktop3DListener.root.newsHandle.show();
									}
								}
							}
						} );
					}
					break;
				case Messenger.MSG_HIDE_NEWSVIEW_HANDLE:
					if( newsView != null )
					{
						newsView.getNewsLeftHandle().setVisibility( View.INVISIBLE );
						newsView.getNewsRightHandle().setVisibility( View.INVISIBLE );
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								if( Desktop3DListener.root != null )
								{
									if( Desktop3DListener.root.newsHandle != null )
									{
										Desktop3DListener.root.newsHandle.hide();
									}
								}
							}
						} );
					}
					break;
				case Messenger.MSG_UNINSTALL_NEWS_VIEW:
				case Messenger.MSG_INSTALL_NEWS_VIEW:
					if( newsView != null )
					{
						newsView.onNewsViewInstall();
						newsView.requestLayout();
					}
					break;
				case Messenger.MSG_CHANGE_NEWS_HANDLE_POS:
					Vector2 v = (Vector2)msg.obj;
					if( v.x == 0 )
					{
						newsView.scrollTo( 0 , 0 );
						newsView.getNewsRightHandle().setY( v.y );
					}
					else
					{
						newsView.scrollTo( -UtilsBase.getScreenWidth() * 2 , 0 );
						newsView.getNewsLeftHandle().setY( v.y );
					}
					newsView.getNewsLeftHandle().setVisibility( View.VISIBLE );
					newsView.getNewsRightHandle().setVisibility( View.VISIBLE );
					Desktop3DListener.root.newsHandle.color.a = 0.0f;
					break;
				case Messenger.MSG_SHOW_NEWS_AUTO:
					if( newsView != null )
					{
						if( (Integer)msg.obj == 0 )
						{
							newsView.startLeftShow();
						}
						else if( (Integer)msg.obj == 1 )
						{
							newsView.startRightShow();
						}
					}
					break;
				case Messenger.MSG_REMOVE_NEWS_AUTO:
					if( newsView != null )
					{
						newsView.startClose();
					}
					break;
				case Messenger.EVENT_HIDE_STATUS_BAR:
					if( Desktop3DListener.root != null && Desktop3DListener.root.newsHandle != null )
					{
						if( !Desktop3DListener.root.newsHandle.isDragging )
						{
							Messenger.sendMsg( Messenger.MSG_HIDE_NEWSVIEW_HANDLE , null );
						}
					}
					prefs.edit().putBoolean( ImmersiveModeUtils.SP_KEY_STATUS_BAR_SHOW , false ).commit();
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
					getWindow().setAttributes( lp );
					getWindow().addFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
					break;
				case Messenger.EVENT_SHOW_STATUS_BAR:
					prefs.edit().putBoolean( ImmersiveModeUtils.SP_KEY_STATUS_BAR_SHOW , true ).commit();
					WindowManager.LayoutParams attr = getWindow().getAttributes();
					attr.flags &= ( ~WindowManager.LayoutParams.FLAG_FULLSCREEN );
					getWindow().setAttributes( attr );
					getWindow().clearFlags( WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
					if( Desktop3DListener.root != null && Desktop3DListener.root.newsHandle != null )
					{
						if( !Desktop3DListener.root.newsHandle.isDragging )
						{
							Messenger.sendMsg( Messenger.MSG_SHOW_NEWSVIEW_HANDLE , null );
						}
					}
					break;
				case Messenger.CIRCLE_EVENT_UP:
					circleDrawView.libgdxAdaptAndroidViewEvent( msg.what , msg.arg1 , msg.arg2 , null );
					break;
				case Messenger.CIRCLE_EVENT_DOWN:
					circleDrawView.libgdxAdaptAndroidViewEvent( msg.what , msg.arg1 , msg.arg2 , null );
					break;
				case Messenger.CIRCLE_EVENT_DRAG:
					circleDrawView.libgdxAdaptAndroidViewEvent( msg.what , msg.arg1 , msg.arg2 , null );
					break;
				case Messenger.CIRCLE_EVENT_TOAST:
				case Messenger.EVENT_TOAST_USER:
				case Messenger.EVENT_TOAST:
				case Messenger.MSG_TOAST:
					String s = b.getString( "toastString" );
					if( toast != null )
					{
						toast.setText( s );
					}
					else
					{
						toast = Toast.makeText( iLoongLauncher.this , s , Toast.LENGTH_SHORT );
					}
					toast.show();
					break;
				case Messenger.EVENT_CREATE_POP_DIALOG:
					showDialog( DIALOG_CIRCLE_DELALL );
					break;
				case Messenger.EVENT_CANNOT_FOUND_APK_DIALOG:
					showDialog( DIALOG_APK_CANNOT_FOUND );
					break;
				case Messenger.EVENT_DOWNLOAD_WIDGET_DIALOG:
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
					long downId = sp.getLong( "calendar_download_id" , -1 );
					DownloadApkManager manager = new DownloadApkManager( iLoongLauncher.getInstance() );
					if( !manager.queryDownloadStatus( downId ) )
					{
						Intent intent = new Intent( iLoongLauncher.getInstance() , DownloadDialog.class );
						intent.putExtra( DownloadDialog.KEY_EXTRA_ICON , R.drawable.calendar_icon );
						intent.putExtra( DownloadDialog.KEY_EXTRA_TITLE , R.string.calendar_download_dialog_title_text );
						intent.putExtra( DownloadDialog.KEY_EXTRA_MESSAGE , R.string.calendar_download_dialog_message_text );
						if( DefaultLayout.enable_google_version )
						{
							String pkgname = Widget3DManager.curDownload.intent.getComponent().getPackageName();
							intent.putExtra( DownloadDialog.KEY_EXTRA_URL , widgetDownloadgoogleplayUri + pkgname );
							intent.putExtra( DownloadDialog.KEY_EXTRA_DOWNLOAD_BY_GOOGLE_PLAY , true );
						}
						else
						{
							intent.putExtra( DownloadDialog.KEY_EXTRA_URL , "http://www.coolauncher.cn/download/apk/TurboCalendar.apk" );
						}
						intent.putExtra( DownloadDialog.KEY_EXTRA_FILE_NAME , "calendar.apk" );
						intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID , downId );
						intent.putExtra( DownloadDialog.KEY_EXTRA_DOWN_ID_SP_KEY , "calendar_download_id" );
						iLoongLauncher.getInstance().startActivity( intent );
					}
					//					showDialog( DIALOG_DOWNLOAD_WIDGET );
					break;
				case Messenger.EVENT_CREATE_PROG_DIALOG:
					showProgressDialog();
					break;
				case Messenger.EVENT_CREATE_RENAME_FOLDER:
					folderIcon = (FolderIcon3D)msg.obj;
					showRenameDialog( folderIcon.mInfo );
					break;
				case Messenger.EVENT_UPDATE_SYS_WIDGET:
					View view = (View)msg.obj;
					MotionEvent event = Widget.MotionEventPool.getEvent( msg.arg1 );
					// Log.i("widget","event:"+view.toString()+",x,y="+event.getX()+","+event.getY());
					if( view != null )
						view.dispatchTouchEvent( event );
					event.recycle();
					break;
				case Messenger.EVENT_ADD_SYS_WIDGET:
					if( msg.obj == null )
						break;
					Widget widget = (Widget)msg.obj;
					Widget2DInfo info = (Widget2DInfo)widget.getItemInfo();
					addAppWidget( info , true );
					info.hostView.setWidget( widget );
					break;
				case Messenger.MSG_WIDGET_FOCUS:
					if( msg.obj == null )
						break;
					WidgetPluginView3D widgetPluginView = (WidgetPluginView3D)msg.obj;
					d3dListener.focusWidget( widgetPluginView , msg.arg1 );
					break;
				case Messenger.EVENT_CLICK_TO_WALLPAPER:
					mWallpaperManager.sendWallpaperCommand( getWindow().getCurrentFocus().getWindowToken() , "android.wallpaper.tap" , msg.arg1 , msg.arg2 , 0 , null );
					break;
				case Messenger.EVENT_ADD_SYS_SHORTCUT:
					clickPoint.x = msg.arg1;
					clickPoint.y = msg.arg2;
					dropPos[0] = msg.arg1;
					dropPos[1] = msg.arg2;
					if( DefaultLayout.enable_workspace_miui_edit_mode == true )
					{
						if( msg.arg1 == 0 && msg.arg2 == 0 )
						{
							displaySystemWidget();
						}
						else
						{
							pickShortcut( REQUEST_PICK_SHORTCUT , RR.string.title_select_shortcut );
						}
					}
					else
					{
						showAddShortcutDialog();
					}
					break;
				// xiatian start //add 3 virtueIcon
				case Messenger.EVENT_SELECT_ZHUTI:
				case Messenger.EVENT_SELECTS_PERSONAL_LOCK:
				case Messenger.EVENT_SELECT_CHANGJINGZHUOMIAN:
				case Messenger.EVENT_SELECT_WALLPAPER:
				case Messenger.EVENT_SELECTS_PERSONAL_ZITI:
				case Messenger.EVENT_SELECTS_PERSONAL_TEXIAO:
				case Messenger.EVENT_SELECT_HOT_THEME:
				case Messenger.EVENT_SELECT_HOT_WALLPAPER:
				{
					Intent intent = new Intent();
					if( !DefaultLayout.enable_themebox )
						intent.setComponent( new ComponentName( SetupMenu.getContext() , com.iLoong.launcher.theme.ThemeManagerActivity.class ) );
					else
					{
						if( DefaultLayout.personal_center_internal )
						{
							intent.setComponent( new ComponentName( iLoongLauncher.getInstance() , "com.coco.theme.themebox.MainActivity" ) );
						}
						else
						{
							intent.setComponent( new ComponentName( "com.iLoong.base.themebox" , "com.coco.theme.themebox.MainActivity" ) );
							// startWallpaper();
							if( pm.queryIntentActivities( intent , 0 ).size() == 0 )
							{
								iLoongLauncher.getInstance().themeCenterDown.ToDownloadApkDialog( iLoongLauncher.getInstance() , iLoongLauncher.getInstance().getResources()
										.getString( RR.string.theme ) , "com.iLoong.base.themebox" );
								return;
							}
						}
						bindThemeActivityData( intent );
					}
					if( msg.what == Messenger.EVENT_SELECT_ZHUTI )
					{
						intent.putExtra( TAG_STRING , TAG_THEME );
					}
					else if( msg.what == Messenger.EVENT_SELECTS_PERSONAL_LOCK )
					{
						intent.putExtra( TAG_STRING , TAG_LOCK );
					}
					else if( msg.what == Messenger.EVENT_SELECT_CHANGJINGZHUOMIAN )
					{
						intent.putExtra( TAG_STRING , TAG_SCENE );
					}
					else if( msg.what == Messenger.EVENT_SELECT_WALLPAPER )
					{
						intent.putExtra( TAG_STRING , TAG_WALLPAPER );
					}
					else if( msg.what == Messenger.EVENT_SELECTS_PERSONAL_ZITI )
					{
						intent.putExtra( TAG_STRING , TAG_FONT );
					}
					else if( msg.what == Messenger.EVENT_SELECTS_PERSONAL_TEXIAO )
					{
						intent.putExtra( TAG_STRING , TAG_EFFECT );
					}
					else if( msg.what == Messenger.EVENT_SELECT_HOT_WALLPAPER )
					{
						intent.putExtra( TAG_STRING , TAG_WALLPAPER );
						intent.putExtra( "selcetHot" , true );
					}
					else if( msg.what == Messenger.EVENT_SELECT_HOT_THEME )
					{
						intent.putExtra( TAG_STRING , TAG_THEME );
						intent.putExtra( "selcetHotTheme" , true );
					}
					else
					{
						intent.putExtra( TAG_STRING , TAG_THEME );
					}
					SetupMenuActions.getInstance().getContext().startActivity( intent );
					break;
				}
				case Messenger.EVENT_SEETINGS_ZHUOMIAN:
					Intent intent1 = new Intent();
					intent1.setComponent( new ComponentName( SetupMenu.getContext() , DesktopSettingActivity.class ) );
					if( pm.queryIntentActivities( intent1 , 0 ).size() == 0 )
					{
						iLoongLauncher.getInstance().themeCenterDown.ToDownloadApkDialog(
								iLoongLauncher.getInstance() ,
								iLoongLauncher.getInstance().getResources().getString( RR.string.theme ) ,
								"com.iLoong.base.themebox" );
						return;
					}
					else
					{
						bindThemeActivityData( intent1 );
						try
						{
							SetupMenuActions.getInstance().getContext().startActivity( intent1 );
						}
						catch( Exception e )
						{
							e.printStackTrace();
						}
					}
					break;
				case Messenger.EVENT_DELETE_SYS_WIDGET:
					deleteWidget( (Widget)msg.obj );
					break;
				case Messenger.EVENT_WIDGET_GET_VIEW:
					Log.i( "widget" , "event:capture" );
					WidgetHostView widget1 = (WidgetHostView)msg.obj;
					widget1.buildCustomCache();
					// long ms = System.currentTimeMillis();
					// Bitmap bmp = Utilities.getViewBitmap(view1);
					// Log.v("ms", "ms1:" + (System.currentTimeMillis() - ms));
					// ms = System.currentTimeMillis();
					// widget1.updateView(bmp);
					// Log.v("ms", "ms2:" + (System.currentTimeMillis() - ms));
					break;
				case Messenger.EVENT_SELECT_SHORTCUT:
					processShortcut( (Intent)msg.obj , REQUEST_CREATE_CONTACT_SHORTCUT );
					break;
				case Messenger.EVENT_DELETE_PAGE:
					showDialog( DIALOG_DELETE_PAGE );
					break;
				case Messenger.EVENT_START_ACTIVITY:
					ItemInfo itemInfo = (ItemInfo)msg.obj;
					// teapotXu add start for new added app flag
					itemInfo.extendible_flag = -1;
					if( ( (ShortcutInfo)itemInfo ).appInfo != null )
					{
						( (ShortcutInfo)itemInfo ).appInfo.extendible_flag = -1;
					}
					// teapotXu add end for new added app flag
					startActivitySafely( ( (ShortcutInfo)itemInfo ).intent , itemInfo );
					break;
				case Messenger.EVENT_START_ACTIVITY_FROM_INTENT:
					startActivitySafelyFromIntent( (Intent)msg.obj );
					break;
				case Messenger.EVENT_LAUNCH_HOTBUTTON:
					launchHotButton();
					break;
				case 7777:
					Log.e( "exit" , "!!!Exit!!!" );
					iLoongLauncher.getInstance().finish();
					System.exit( 0 );
					break;
				case Messenger.MSG_SHOW_WORKSPACE:
					xWorkspace.show();
					// Log.d("launcher", "workspace visible");
					// xWorkspace.ShowCurScreen();
					break;
				case Messenger.MSG_HIDE_WORKSPACE:
					xWorkspace.hide();
					// Log.d("launcher", "workspace invisible");
					// xWorkspace.hideCurScreen();
					break;
				case Messenger.MSG_SHOW_WORKSPACE_EX:
					xWorkspace.show();
					if( !DefaultLayout.enable_news )
						xWorkspace.setVisibility( View.VISIBLE );
					// Log.d("launcher", "workspace visible");
					break;
				case Messenger.MSG_HIDE_WORKSPACE_EX:
					xWorkspace.hide();
					if( !DefaultLayout.enable_news )
						xWorkspace.setVisibility( View.GONE );
					// Log.d("launcher", "workspace invisible");
					break;
				case Messenger.MSG_SCROLL_WORKSPACE:
					if( !DefaultLayout.enable_news )
						xWorkspace.scrollTo( msg.arg1 , 0 );
					// xWorkspace.showCurrentPage();
					break;
				case Messenger.MSG_ADD_WORKSPACE_CELL:
					xWorkspace.addCellAt( msg.arg1 );
					break;
				case Messenger.MSG_REMOVE_WORKSPACE_CELL:
					xWorkspace.removeCellAt( msg.arg1 );
					break;
				case Messenger.MSG_PAGEEDIT_ADD_WORKSPACE_CELL:
					xWorkspace.pageEditAddCellAt( msg.arg1 );
					break;
				case Messenger.MSG_PAGEEDIT_REMOVE_WORKSPACE_CELL:
					xWorkspace.pageEditRemoveCellAt( msg.arg1 );
					break;
				case Messenger.MSG_VIBRATOR:
					if( !DefaultLayout.disable_vibrator && vibratorAble == 1 )
						mVibrator.vibrate( msg.arg1 );
					break;
				case Messenger.MSG_SYS_VIBRATOR:
					xWorkspace.performHapticFeedback( HapticFeedbackConstants.LONG_PRESS , HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING );
					break;
				case Messenger.MSG_SYS_PLAY_SOUND_EFFECT:
					xWorkspace.playSoundEffect( SoundEffectConstants.CLICK );
					break;
				case Messenger.MSG_ON_BEGIN_OPEN_WIDGET:
					xWorkspace.setVisibility( View.INVISIBLE );
					break;
				case Messenger.MSG_ON_COMPLETE_CLOSE_WIDGET:
					xWorkspace.setVisibility( View.VISIBLE );
					break;
				case Messenger.MSG_MOVE_WIDGET:
					moveAppWidget( (View3D)msg.obj , msg.arg1 );
					break;
				case Messenger.MSG_ADD_WIDGET:
					addAppWidgetFromDrop( (ComponentName)msg.obj , new int[]{ (int)msg.arg1 , (int)msg.arg2 } );
					break;
				case Messenger.MSG_PICK_SYS_WIDGET_SHORTCUT:
					if( msg.obj != null && msg.obj instanceof Intent )
					{
						if( msg.arg1 == 0 )
							addAppWidgetFromPick( (Intent)msg.obj );
						else if( msg.arg1 == 1 )
						{
							int screen = d3dListener.getCurrentScreen();
							// if(DefaultLayout.enable_workspace_miui_edit_mode &&
							// Workspace3D.WorkspaceStatus ==
							// WorkspaceStatusEnum.EditMode && true ==
							// Workspace3D.b_editmode_include_addpage){
							// screen -=1;
							// }
							completeAddShortcutFromDrop( (Intent)msg.obj , screen , 0 , 0 , true );
						}
					}
					break;
				case Messenger.MSG_ADD_SHORTCUT:
					processShortcutFromDrop( (ComponentName)msg.obj , msg.arg1 , msg.arg2 );
					break;
				case Messenger.EVENT_CREATE_BLUETOOTH:
					adapter = BluetoothAdapter.getDefaultAdapter();
					break;
				case Messenger.EVENT_CREATE_LOCKSCREEN:
					break;
				case Messenger.EVENT_CREATE_RESTART_DIALOG:
					showDialog( DIALOG_RESTART );
					break;
				case Messenger.EVENT_CREATE_EFFECT_DIALOG:
					showDialog( DIALOG_APP_EFFECT );
					break;
				case Messenger.EVENT_DELETE_NOT_EMPTY_FOLDER:
					showDialog( DIALOG_DELETE_FOLDER );
					break;
				case Messenger.MSG_SHOW_SORT_DIALOG:
					sortAppCheckId = msg.arg1;
					sortOrigin = msg.arg2;
					showDialog( DIALOG_SORT_APP );
					break;
				case Messenger.MSG_SYS_SHOW_NOTICEBAR:
					getWindow().setFlags( ~WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
					break;
				case Messenger.MSG_SYS_HIDE_NOTICEBAR:
					getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN );
					break;
				case Messenger.MSG_START_COVER_MTKWIDGET:
					xWorkspace.startCoverMTKWidgetView();
					break;
				case Messenger.MSG_STOP_COVER_MTKWIDGET:
					xWorkspace.stopCoverMTKWidgetView();
					break;
				case Messenger.MSG_MOVE_IN_MTKWIDGET:
					xWorkspace.moveInMTKWidgetView();
					break;
				case Messenger.MSG_MOVE_OUT_MTKWIDGET:
					xWorkspace.moveOutMTKWidgetView();
					break;
				case Messenger.MSG_UPDATE_TEXTURE_DELAY:
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
						}
					} );
					Gdx.graphics.requestRendering();
					break;
				case Messenger.MSG_UPDATE_TEXTURE_CIRCLE:
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							Gdx.graphics.requestRendering();
							mMainHandler.postDelayed( this , 1000 );
						}
					} );
					break;
				case Messenger.MSG_RESET_TEXTURE_WRITE:
					// iLoongLauncher.getInstance().postRunnable( new Runnable() {
					//
					// @Override
					// public void run()
					// {
					// MyPixmapPacker.write = true;
					// R3D.packer.updateTextureAtlas( R3D.packerAtlas ,
					// TextureFilter.Linear , TextureFilter.Linear );
					// }
					// } );
					Gdx.graphics.requestRendering();
					break;
				case Messenger.MSG_UPDATE_PACKAGE:
					mModel.updatePackage();
					break;
				case Messenger.MSG_CHANGE_THREAD_PRIORITY:
					mModel.changeLoadThreadPriority();
					break;
				case Messenger.EVENT_ADD_APP_USE_FREQUENCY:
					// Utils3D.showTimeFromStart("EVENT_ADD_APP_USE_FREQUENCY 1");
					String string = (String)msg.obj;
					int useFrequency = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).getInt( string , 0 );
					PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() ).edit().putInt( string , useFrequency + 1 ).commit();
					// Utils3D.showTimeFromStart("EVENT_ADD_APP_USE_FREQUENCY 2"+" "+(String)msg.obj);
					break;
				case Messenger.MSG_SYS_LIHGHT_CHANGE:
					Object bright = msg.obj;
					if( bright instanceof Integer )
					{
						int brightness = (Integer)bright;
						Log.v( "brightness" , " brightness is " + brightness );
						setBrightnesss( brightness );
						saveBrightness( iLoongLauncher.getInstance().getContentResolver() , brightness );
					}
					break;
				case Messenger.EVENT_CHECK_SIZE:
					if( d3dListener.mPaused )
						break;
					if( checkSize )
					{
						checkSize = false;
						Utils3D.resetSize();
						Log.v( "resize" , "utils3d width:" + Utils3D.getScreenWidth() + "height:" + Utils3D.getScreenHeight() );
						if( Utils3D.getScreenWidth() > Utils3D.getScreenHeight() )
						{
							Log.e( "resize" , "checkSize:now Restarting..." );
							SystemAction.RestartSystem();
						}
					}
					break;
				// teapotXu add start
				case Messenger.MSG_EVT_ADD_WIDGETS_DIALOG:
					if( DefaultLayout.enable_doov_spec_customization )
					{
						iLoongLauncher.getInstance().displaySystemWidget();
					}
					break;
				// teapotXu add end
				// xiatian add start //mainmenu_background_alpha_progress
				case Messenger.MSG_SHOW_MAINMENU_BG_DIALOG:
					showDialog( DIALOG_MAINMENU_BG );
					break;
				// xiatian add end
				case Messenger.MSG_FORCE_RESET_MAINMENU:
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							if( d3dListener.getRoot() != null && d3dListener.getRoot().getAppHost() != null && d3dListener.getRoot().getAppHost().appList != null )
								d3dListener.getRoot().getAppHost().appList.finishBind();
						}
					} );
					break;
				case Messenger.MSG_WRITE_RESUME_TIME:
					SharedPreferences prefs = iLoongLauncher.this.getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
					prefs.edit().putLong( "resume_time" , System.nanoTime() ).commit();
					break;
				case Messenger.MSG_HIDE_CAMERA_PREVIEW:
					CameraManager.instance().onHidePreviewMessage();
					break;
				case Messenger.MSG_SHOW_SHARE_PROGRESS_DIALOG:
					showShareProgress();
					break;
				case Messenger.MSG_CANCEL_SHARE_PROGRESS_DIALOG:
					cancelShareProgress();
					break;
				case Messenger.MSG_CHANGE_WALLPAPER:
					String name = b.getString( "displayname" );
					String path = b.getString( "pkgname" );
					ChangeWallpager( name , path );
					break;
				case Messenger.MSG_CHANGE_THEME:
					String pkgname = b.getString( "pkgname" );
					String classname = b.getString( "classname" );
					String displayname = b.getString( "displayname" );
					ChangeTheme( pkgname , classname , displayname );
					break;
				case Messenger.MSG_SHOW_DESKTOP_SETTING:
					Intent intent = new Intent( iLoongLauncher.getInstance() , FirstActivity.class );
					iLoongLauncher.getInstance().startActivity( intent );
					break;
				case Messenger.MSG_NOT_FIND_SD_CARD:
				{
					if( toast != null )
						toast.cancel();
					toast = Toast.makeText( iLoongLauncher.getInstance() , RR.string.insert_sd_card , Toast.LENGTH_SHORT );
					toast.show();
				}
				case Messenger.MSG_SHOW_CUSTOM_DIALOG:
				{
					if( msg.obj != null && msg.obj instanceof CustomDialogInfo )
					{
						showCustomProgress( ( (CustomDialogInfo)msg.obj ).x , ( (CustomDialogInfo)msg.obj ).y );
					}
				}
					break;
				case Messenger.MSG_CANCEL_CUSTOM_DIALOG:
				{
					cancelCustomProgress();
				}
					break;
				case Messenger.MSG_DELETE_MAIN_MENU_TIP:
				{
					if( toast != null )
						toast.cancel();
					toast = Toast.makeText( iLoongLauncher.getInstance() , RR.string.delete_main_menu_tip , Toast.LENGTH_LONG );
					toast.show();
				}
					break;
				case Messenger.MSG_WIDGETClEAN4_CLEAN:
				{
					if( !Utils3D.isAPKInstalled( iLoongLauncher.this , "com.blueflash.kingscleanmaster" ) )
					{
						Intent intentInstall = new Intent( iLoongLauncher.this , InstallCleanMasterActivity.class );
						intentInstall.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivity( intentInstall );
					}
					else
					{
						isOrNotSendBroadcast = true;
						cleanThread = new CleanThread();
						cleanThread.start();
					}
				}
					break;
				case Messenger.MSG_WIDGETClEAN_CLEAN:
				{
					if( !Utils3D.isAPKInstalled( iLoongLauncher.this , "com.blueflash.kingscleanmaster" ) )
					{
						Intent intentInstall = new Intent( iLoongLauncher.this , InstallCleanMasterActivity.class );
						intentInstall.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivity( intentInstall );
					}
					else
					{
						isOrNotSendBroadcast = false;
						cleanThread = new CleanThread();
						cleanThread.start();
					}
				}
					break;
				//进入清理大师
				case Messenger.MSG_ENTER_MASTER_CLEAN:
				{
					if( !Utils3D.isAPKInstalled( iLoongLauncher.this , "com.blueflash.kingscleanmaster" ) )
					{
						Intent intentinstall = new Intent( iLoongLauncher.this , InstallCleanMasterActivity.class );
						intentinstall.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivity( intentinstall );
					}
					else
					{
						PackageManager packageManager = iLoongLauncher.this.getPackageManager();
						Intent intentEnter = packageManager.getLaunchIntentForPackage( "com.blueflash.kingscleanmaster" );//"com.blueflash.kingscleanmaster"就是我们获得要启动应用的包名   
						intentEnter.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						startActivity( intentEnter );
					}
				}
					break;
				case Messenger.MSG_CANCEL_LOADING_DIALOG:
				{
					DefaultLayout.getInstance().dismissLoadingDialog();
				}
					break;
			}
		}
	};
	
	//清理大师线程  
	private class CleanThread extends Thread
	{
		
		@Override
		public void run()
		{
			doCleanProcess();
		}
	}
	
	public void ChangeTheme(
			final String pkgname ,
			final String classname ,
			String displayname )
	{
		if( pkgname != null && classname != null && displayname != null )
		{
			final ProgressDialog dialog = new ProgressDialog( this );
			dialog.setMessage( getString( R.string.changingTheme ) );
			dialog.setCancelable( false );
			dialog.show();
			final ThemeService sv = new ThemeService( this );
			new Thread( new Runnable() {
				
				@Override
				public void run()
				{
					try
					{
						Thread.sleep( 500 );
					}
					catch( InterruptedException e )
					{
						e.printStackTrace();
					}
					sv.applyTheme( new ComponentName( pkgname , classname ) );
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							Intent intent = new Intent( StaticClass.ACTION_DEFAULT_THEME_CHANGED );
							intent.putExtra( "pkgName" , pkgname );
							sendBroadcast( intent );
							dialog.dismiss();
						}
					} );
				}
			} ).start();
		}
	}
	
	public void ChangeWallpager(
			final String displayname ,
			final String pkgname )
	{
		final com.coco.wallpaper.wallpaperbox.WallpaperInfo wi = new com.coco.wallpaper.wallpaperbox.WallpaperInfo( iLoongLauncher.getInstance() );
		String select = null;
		if( pkgname != null )
		{
			select = pkgname;
		}
		else
		{
			select = displayname.replace( "_small" , "" );
		}
		Intent it = new Intent( "com.coco.wallpaper.update" );
		it.putExtra( "wallpaper" , select );
		sendBroadcast( it );
		new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( 200 );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
				if( pkgname != null )
				{
					wi.setWallpaperByPath( PathTool.getAppFile( pkgname ) );
				}
				else
				{
					selectWallpaper( "assets/launcher/wallpapers" , displayname.replace( "_small" , "" ) );
				}
				try
				{
					Thread.sleep( 3000 );
				}
				catch( InterruptedException e )
				{
					e.printStackTrace();
				}
				iLoongLauncher.getInstance().postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						sendBroadcast( new Intent( "com.cooee.scene.wallpaper.change" ) );
						runOnUiThread( new Runnable() {
							
							@Override
							public void run()
							{
								PopImageView3D.result = true;
								SendMsgToAndroid.sendToastMsg( SetupMenu.getKey( R.string.toast_setwallpaper_success ) );
							}
						} );
					}
				} );
			}
		} ).start();
	}
	
	public void selectWallpaper(
			String path ,
			String name )
	{
		InputStream bit = ThemeAndPagerHelper.GetBitByName( name );
		if( bit != null )
		{
			try
			{
				SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
				WallpaperManager wpm = (WallpaperManager)iLoongLauncher.getInstance().getSystemService( "wallpaper" );
				wpm.setStream( bit );
				if( bit != null )
				{
					bit.close();
				}
				setWallpaperNewDim( wpm );
				prefs.edit().putLong( "apply_wallpaper_time" , System.currentTimeMillis() ).commit();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private void setWallpaperNewDim(
			WallpaperManager wpm )
	{
		DisplayMetrics displayMetrics = new DisplayMetrics();
		iLoongLauncher.getInstance().getWindowManager().getDefaultDisplay().getMetrics( displayMetrics );
		wpm.suggestDesiredDimensions( (int)( displayMetrics.widthPixels * 2 ) , (int)( displayMetrics.heightPixels ) );
	}
	
	public void setBrightnesss(
			int brightness )
	{
		WindowManager.LayoutParams lp = iLoongLauncher.getInstance().getWindow().getAttributes();
		if( brightness <= 1 )
		{
			return;
		}
		else
		{
			lp.screenBrightness = brightness / 255.0f;
			iLoongLauncher.getInstance().getWindow().setAttributes( lp );
		}
	}
	
	public static void saveBrightness(
			ContentResolver resolver ,
			int brightness )
	{
		Uri uri = android.provider.Settings.System.getUriFor( "screen_brightness" );
		android.provider.Settings.System.putInt( resolver , "screen_brightness" , brightness );
		resolver.notifyChange( uri , null );
	}
	
	public long cur;
	
	protected void onPause()
	{
		if( DefaultLayout.enable_camera )
		{
			CameraManager.instance().hidePreview();
		}
		Log.v( "iLoongLauncher" , "Launcher onPause()0" );
		d3dListener.forceTouchUp();
		/*
		 * 当桌面在暂停状态时发送一个广播给场景，whj add
		 */
		Intent sceneIntent = new Intent();
		sceneIntent.setAction( "com.cooee.launcher.action.onpause" );
		sendBroadcast( sceneIntent );
		UtilsBase.pauseTime = System.nanoTime();
		UtilsBase.resumeTime = 0;
		if( writeBootTime )
		{
			writeBootTime = false;
			SharedPreferences prefs = this.getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
			prefs.edit().putLong( "boot_complete_time" , bootTime ).commit();
		}
		Utils3D.changeStatusbar( "topwisemenu" , false , false );
		Utils3D.changeStatusbar( "topwiseidle" , false , false );
		Intent pauseBroadCast = new Intent();
		pauseBroadCast.setAction( LAUNCHER_STATE_PAUSE );
		sendBroadcast( pauseBroadCast );
		View mtkWidgetView = xWorkspace.searchIMTKWidget( xWorkspace );
		if( mtkWidgetView != null )
		{
			( (IMtkWidget)mtkWidgetView ).onPauseWhenShown( d3dListener.getCurrentScreen() );
			Log.e( "launcher" , "onPauseWhenShown" );
		}
		d3dListener.mPaused = true;
		Widget3DManager.getInstance().pauseAllWidget3D();
		if( DefaultLayout.setupmenu_by_view3d )
		{
			d3dListener.closeSetupMenu();
		}
		if( mShaker != null )
		{
			mShaker.pause();
		}
		if( folderIcon != null && folderIcon.bRenameFolder )
		{
			renameFoldercleanup();
		}
		View3DTweenAccessor.manager.pause();
		if( DefaultLayout.enable_particle || DefaultLayout.enable_new_particle )
		{
			if( ParticleManager.particleManagerEnable )
			{
				ParticleManager manager = ParticleManager.getParticleManager();
				manager.stopAllAnims();
			}
		}
		super.onPause();
		MobclickAgent.onPause( this );
		Log.v( "iLoongLauncher" , "Launcher onPause() end" );
	}
	
	protected void onResume()
	{
		Log.v( "iLoongLauncher" , "Launcher onResume() start" );
		if( DefaultLayout.enable_quick_search && Desktop3DListener.root != null && Desktop3DListener.root.qSearchGroup != null && Desktop3DListener.root.qSearchGroup.visible )
		{
			Desktop3DListener.root.qSearchGroup.resetPostion();
		}
		if( AppList3D.allInit && Desktop3DListener.bWidgetDone )
			DefaultLayout.getInstance().cancelProgressDialog();
		if( newsView != null )
		{
			newsView.requestLayout();
		}
		_time = System.currentTimeMillis();
		UtilsBase.resumeTime = System.nanoTime();
		UtilsBase.pauseTime = Long.MAX_VALUE;
		if( RR.net_version )
		{
			HotSeat3D.enableRollDockbar = SetupMenuActions.getInstance().getBoolean( getString( string.setting_key_roll_dockbar ) );
		}
		if( d3dListener != null && Desktop3DListener.root != null && Root3D.hotseatBar != null )
		{
			if( HotSeat3D.curType == HotSeat3D.TYPE_WIDGET )
			{
			}
			else
			{
				Root3D.hotseatBar.rollDockbarToMenu();
				Desktop3DListener.d3d.isOnLongClick = false;
			}
		}
		if( d3dListener != null && Desktop3DListener.root != null && Desktop3DListener.root.zoomview != null )
		{
			ViewGroup3D zoom = Desktop3DListener.root.zoomview;
			if( zoom != null )
			{
				Desktop3DListener.root.removeView( zoom );
				zoom = null;
				Desktop3DListener.root.zoomview = null;
			}
			Desktop3DListener.root.ReturnClor();
		}
		if( writeBootTime )
		{
			writeBootTime = false;
			SharedPreferences prefs = this.getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
			prefs.edit().putLong( "boot_complete_time" , bootTime ).commit();
		}
		Messenger.sendMsgDelay( Messenger.MSG_WRITE_RESUME_TIME , null , 300 );
		if( d3dListener != null && d3dListener.getRoot() != null && d3dListener.getRoot().getAppHost() != null && d3dListener.getRoot().getAppHost().getVisible() == true )
		{
			// when applist is shown, return
			Utils3D.changeStatusbar( "topwisemenu" , true , false );
			Utils3D.changeStatusbar( "topwiseidle" , true , true );
		}
		else
		{
			Utils3D.changeStatusbar( "topwiseidle" , true , true );
		}
		// enable_themebox
		if( iLoongApplication.needRestart )
		{
			SystemAction.RestartSystem();
			super.onResume();
			return;
		}
		Intent resumeBroadCast = new Intent();
		resumeBroadCast.setAction( LAUNCHER_STATE_RESUME );
		sendBroadcast( resumeBroadCast );
		if( DefaultLayout.enable_content_staistic )
		{
			String theme = ThemeManager.getInstance().getCurrentThemeDescription().componentName.getPackageName();
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
			long uptime = pref.getLong( "use-" + theme , 0 );
			long curtime = System.currentTimeMillis();
			Calendar up = Calendar.getInstance();
			up.setTimeInMillis( uptime );
			Calendar cur = Calendar.getInstance();
			cur.setTimeInMillis( System.currentTimeMillis() );
			if( up.get( Calendar.DAY_OF_MONTH ) != cur.get( Calendar.DAY_OF_MONTH ) || up.get( Calendar.MONTH ) != cur.get( Calendar.MONTH ) || up.get( Calendar.YEAR ) != cur.get( Calendar.YEAR ) )
			{
				try
				{
					Context context = createPackageContext( theme , Context.CONTEXT_IGNORE_SECURITY );
					if( theme.equals( getPackageName() ) )
					{
						String pkgName = getDefaultThemePkgName( context );
						if( pkgName == null )
						{
							pkgName = theme;
						}
					}
					else
					{
					}
					pref.edit().putLong( "use-" + theme , curtime ).commit();
				}
				catch( NameNotFoundException e )
				{
					e.printStackTrace();
				}
			}
		}
		cur = System.currentTimeMillis();
		View mtkWidgetView = xWorkspace.searchIMTKWidget( xWorkspace );
		if( mtkWidgetView != null )
		{
			( (IMtkWidget)mtkWidgetView ).onResumeWhenShown( d3dListener.getCurrentScreen() );
		}
		View3DTweenAccessor.manager.resume();
		super.onResume();
		mRestoring = false;
		d3dListener.mPaused = false;
		if( d3dListener.mOnResumeNeedsLoad )
		{
			mWorkspaceLoading = true;
			mModel.startLoader( this , true );
			d3dListener.mOnResumeNeedsLoad = false;
		}
		Widget3DManager.getInstance().resumeAllWidget3D();
		if( this.hasWindowFocus() )
		{
			registerShakeWallpapers();
		}
		if( DefaultLayout.enable_news && DefaultLayout.show_newspage_with_shake )
		{
			registerShakeNewspage();
		}
		if( checkSize )
			SendMsgToAndroid.sendCheckSizeMsg( 3000 );
		if( d3dListener.getDragLayer() != null )
		{
			d3dListener.getDragLayer().onResume();
		}
		Gdx.graphics.requestRendering();
		if( ( DefaultLayout.enable_effect_preview ) && ( d3dListener.getRoot() != null ) )
		{
			Root3D mRoot = d3dListener.getRoot();
			if( Root3D.mIsInEffectPreviewMode == -1 )
			{
				if( ( mRoot.mWorkspaceEffectPreview != null ) && ( mRoot.mWorkspaceEffectPreview.isVisible() ) )
				{
					mRoot.mWorkspaceEffectPreview.hide();
					mRoot.getHotSeatBar().showNoAnim();
				}
				if( ( mRoot.mApplistEffectPreview != null ) && ( mRoot.mApplistEffectPreview.isVisible() ) )
				{
					mRoot.mApplistEffectPreview.hide();
					AppHost3D.appList.setUser( 0 );
					if( AppHost3D.appBar != null )
					{
						AppHost3D.appBar.show();
					}
				}
				if( ( mRoot.mEffectPreviewTips3D != null ) && ( mRoot.mEffectPreviewTips3D.isVisible() ) )
				{
					mRoot.mEffectPreviewTips3D.hide();
				}
			}
			else
			{
				if( Root3D.mIsInEffectPreviewMode == EffectPreview3D.TYPE_WORKSPACE )
				{
					if( ( mRoot.mApplistEffectPreview != null ) && ( mRoot.mApplistEffectPreview.isVisible() ) )
					{
						mRoot.mApplistEffectPreview.hide();
					}
				}
				else if( Root3D.mIsInEffectPreviewMode == EffectPreview3D.TYPE_APPLIST )
				{
					if( ( mRoot.mWorkspaceEffectPreview != null ) && ( mRoot.mWorkspaceEffectPreview.isVisible() ) )
					{
						mRoot.mWorkspaceEffectPreview.hide();
					}
				}
			}
		}
		if( RR.net_version )
		{
			MobclickAgent.onResume( this );
			UmengMobclickAgent.NewUser( this );
		}
		_time = System.currentTimeMillis();
		//		if( d3dListener != null && Desktop3DListener.root != null && Desktop3DListener.root.getWorkspace() != null && Desktop3DListener.root.getWorkspace().isVisible() )
		//			fireupRecentApp();
		showActionGuide();
	}
	
	public void showActionGuide()
	{
		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
		if( Workspace3D.isRecentAppVisible() )
		{
			return;
		}
		if( d3dListener != null && d3dListener.workspace != null && d3dListener.workspace.isVisible() && Desktop3DListener.root != null && Desktop3DListener.root.qSearchGroup != null && !Desktop3DListener.root.qSearchGroup
				.isVisible() )
		{
			if( iLoongLauncher.entryWorksapceCount == DOCKBAR_DELAY_COUNT )
			{
				if( ActionHolder.getInstance() != null )
					this.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							ActionHolder.getInstance().onDockBarStarted();
						}
					} );
			}
			else if( iLoongLauncher.entryWorksapceCount < DOCKBAR_DELAY_COUNT )
				if( HotSeat3D.curType == 1 && Workspace3D.WorkspaceStatus != WorkspaceStatusEnum.EditMode )
				{
					iLoongLauncher.entryWorksapceCount++;
				}
		}
	}
	
	private String getDefaultThemePkgName(
			Context context )
	{
		String pkgName = null;
		JSONObject jObject = null;
		InputStream inputStream = null;
		AssetManager assetManager = context.getAssets();
		try
		{
			inputStream = assetManager.open( "theme/config.ini" );
			String config = readTextFile( inputStream );
			try
			{
				jObject = new JSONObject( config );
				JSONObject jRes = new JSONObject( jObject.getString( "config" ) );
				pkgName = jRes.getString( "packageName" );
			}
			catch( JSONException e1 )
			{
				e1.printStackTrace();
			}
		}
		catch( IOException e )
		{
			Log.v( "iLoongLauncher" , e.getMessage() );
		}
		finally
		{
			try
			{
				if( inputStream != null )
				{
					inputStream.close();
				}
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		return pkgName;
	}
	
	private String readTextFile(
			InputStream inputStream )
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try
		{
			while( ( len = inputStream.read( buf ) ) != -1 )
			{
				outputStream.write( buf , 0 , len );
			}
			outputStream.close();
			inputStream.close();
		}
		catch( IOException e )
		{
		}
		return outputStream.toString();
	}
	
	protected void onStop()
	{
		Log.v( "iLoongLauncher" , "Launcher onStop() start" );
		stoped = true;
		Widget3DManager.getInstance().stopAllWidget3D();
		if( mShaker != null )
		{
			mShaker.pause();
		}
		if( R3D.packer != null )
			R3D.packer.updateTextureAtlas( R3D.packerAtlas , TextureFilter.Linear , TextureFilter.Linear );
		if( DefaultLayout.broadcast_state )
		{
			sendBroadcast( new Intent( "com.cooee.launcher.action.stop" ) );
		}
		super.onStop();
		Log.v( "iLoongLauncher" , "Launcher onStop() end" );
	}
	
	protected void onDestroy()
	{
		Log.v( "iLoongLauncher" , "Launcher onDestroy() start" );
		if( m_dialog != null )
		{
			m_dialog.cancel();
		}
		dialogCancelled = true;
		super.onDestroy();
		ThemeManager.getInstance().Release();
		if( DefaultLayout.dynamic_icon )
		{
			mweatherreceiver.unRegisterweatherReceiver( this );
		}
		android.os.Process.killProcess( android.os.Process.myPid() );
		if( desktopsettings != null )
		{
			this.unregisterReceiver( desktopsettings );
		}
		unbindService( conn );
		Log.v( "iLoongLauncher" , "Launcher onDestroy() end" );
	}
	
	@Override
	public void onWindowFocusChanged(
			boolean hasFocus )
	{
		super.onWindowFocusChanged( hasFocus );
		Log.i( "opt" , "opt onWindowFocusChanged:focus=" + hasFocus + ",time=" + ( System.currentTimeMillis() - _time ) );
		_time = System.currentTimeMillis();
		Intent intent;
		if( hasFocus )
		{
			if( Utils3D.hasMeiZuSmartBar() )
			{
				Utils3D.hideNavigationBar( getWindow().getDecorView() );
			}
			// 在锁屏状态不开启壁纸甩动，只有当屏幕获取焦点后才进行开启桌面甩动换壁纸//mayuqing
			registerShakeWallpapers();
			if( DefaultLayout.send_msg_in_appList_onWindowFocusChanged && d3dListener != null && d3dListener.getRoot() != null && d3dListener.getRoot().getAppHost() != null && d3dListener.getRoot()
					.getAppHost().getVisible() == true )
			{
				// when applist is shown, so send show_app message
				intent = new Intent();
				Log.v( "sendBroadcast" , " onWindowFocusChanged com.cooee.launcher.action.show_app" );
				intent.setAction( "com.cooee.launcher.action.show_app" );
			}
			else
			{
				intent = new Intent();
				intent.setAction( "com.cooee.launcher.action.start" );
			}
		}
		else
		{
			// 隐藏preview
			if( DefaultLayout.enable_camera )
			{
				CameraManager.instance().hidePreview();
			}
			intent = new Intent();
			intent.setAction( "com.cooee.launcher.action.stop" );
		}
		sendBroadcast( intent );
		if( d3dListener != null && hasFocus )
			d3dListener.reset();
	}
	
	private String getTypedText()
	{
		return mDefaultKeySsb.toString();
	}
	
	private void clearTypedText()
	{
		mDefaultKeySsb.clear();
		mDefaultKeySsb.clearSpans();
		Selection.setSelection( mDefaultKeySsb , 0 );
	}
	
	@Override
	public void startSearch(
			String initialQuery ,
			boolean selectInitialQuery ,
			Bundle appSearchData ,
			boolean globalSearch )
	{
		Log.v( "search" , "startSearch" );
		if( initialQuery == null )
		{
			// Use any text typed in the launcher as the initial query
			initialQuery = getTypedText();
			clearTypedText();
		}
		if( appSearchData == null )
		{
			appSearchData = new Bundle();
			appSearchData.putString( "source" , "launcher-search" );
		}
		final SearchManager searchManager = (SearchManager)getSystemService( Context.SEARCH_SERVICE );
		searchManager.startSearch( initialQuery , selectInitialQuery , getComponentName() , appSearchData , globalSearch );
	}
	
	@Override
	public boolean onSearchRequested()
	{
		startSearch( null , false , null , true );
		return true;
	}
	
	private boolean haskeyDown = false;
	
	@Override
	public boolean onKeyDown(
			int keyCode ,
			KeyEvent event )
	{
		if( ActionHolder.getInstance() != null && ActionHolder.getInstance().action != null && ActionHolder.getInstance().action.visible )
		{
			return true;
		}
		if( keyCode == android.view.KeyEvent.KEYCODE_ENVELOPE )
			return false;
		if( d3dListener == null || !Desktop3DListener.bCreatDone )
			return true;
		haskeyDown = true;
		boolean handled = false;
		final FolderIcon3D findFolder = d3dListener.getOpenFolder();
		final FolderIcon3D findFolderInMainmenu = d3dListener.getOpenFolderInMainmenu();
		if( keyCode == KeyEvent.KEYCODE_MENU && findFolder != null && findFolder.mInfo.opened == true )
		{
			if( !d3dListener.isApplitionListToAddShow() )
			{
				this.postRunnable( new Runnable() {
					
					@Override
					public void run()
					{
						if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
						{
							if( findFolder.mFolderMIUI3D.bEnableTouch == true )
							{
								findFolder.mFolderMIUI3D.DealButtonOKDown();
							}
						}
						else if( findFolder.mFolder.bEnableTouch == true )
						{
							findFolder.mFolder.DealButtonOKDown();
						}
					}
				} );
			}
			return true;
		}
		else if( keyCode == KeyEvent.KEYCODE_MENU && findFolder != null && findFolder.folderIconPath != null )
		{
			this.postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					findFolder.folderIconPath.DealButtonOKDown();
				}
			} );
			return true;
		}
		// Eat the long press event so the keyboard doesn't come up.
		if( event.isLongPress() )
		{
			return true;
		}
		return handled;
	}
	
	@Override
	public boolean onKeyUp(
			int keyCode ,
			KeyEvent event )
	{
		if( d3dListener == null || !Desktop3DListener.bCreatDone )
			return true;
		if( ActionHolder.getInstance() != null && ActionHolder.getInstance().action != null && ActionHolder.getInstance().action.visible )
		{
			return true;
		}
		if( haskeyDown )
		{
			haskeyDown = false;
		}
		if( keyCode == KeyEvent.KEYCODE_MENU && !d3dListener.isAllAppsVisible() && !showIntroduction && d3dListener.getWorkspace3D().isVisible() )
		{
			if( DefaultLayout.enable_news || DefaultLayout.show_news_page_enable_config )
			{
				if( d3dListener.getWorkspace3D().getXScale() != 0 )
				{
					return true;// 切页时不能弹出菜单
				}
			}
			boolean canOpenSetupMenu = true;
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			if( curCellLayout != null )
			{
				for( int i = 0 ; i < curCellLayout.getChildCount() ; i++ )
				{
					if( curCellLayout.getChildAt( i ) instanceof Widget3D )
					{
						Widget3D curWidget3D = (Widget3D)curCellLayout.getChildAt( i );
						if( curWidget3D.isOpened() )
						{
							canOpenSetupMenu = false;
							break;
						}
					}
				}
			}
			if( canOpenSetupMenu )
			{
				if( DefaultLayout.enable_effect_preview )
				{
					Root3D mRoot3D = getD3dListener().getRoot();
					if( mRoot3D.isWorkspaceEffectPreviewMode() )
						return true;
				}
				if( d3dListener.isApplitionListToAddShow() )
				{
					return true;
				}
				if( !DefaultLayout.diable_enter_applist_when_takein_mode || !Workspace3D.isHideAll )
				{
					if( DefaultLayout.setupmenu_by_system )
					{
						return false;// 让系统处理
					}
					else
					{
						if( DefaultLayout.setupmenu_by_view3d )
						{
							d3dListener.setupOnMenuKey();
						}
						else
						{
							if( !Workspace3D.isRecentAppVisible() )
								Root3D.hotseatBar.rollDockbarToMenu();
						}
					}
				}
			}
			return true;
		}
		else if( keyCode == KeyEvent.KEYCODE_BACK && DefaultLayout.show_page_edit_on_key_back && !d3dListener.isAllAppsVisible() && !showIntroduction && d3dListener.getWorkspace3D().isVisible() )
		{
			boolean canOpenPageEdit = true;
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			for( int i = 0 ; i < curCellLayout.getChildCount() ; i++ )
			{
				if( curCellLayout.getChildAt( i ) instanceof Widget3D )
				{
					Widget3D curWidget3D = (Widget3D)curCellLayout.getChildAt( i );
					if( curWidget3D.isOpened() )
					{
						canOpenPageEdit = false;
						break;
					}
				}
			}
			if( canOpenPageEdit )
			{
				Workspace3D workspace = d3dListener.getWorkspace3D();
				workspace.getParent().onCtrlEvent( workspace , Workspace3D.MSG_PAGE_SHOW_EDIT );
				return true;
			}
		}
		if( keyCode == KeyEvent.KEYCODE_BACK )
		{
			if( DefaultLayout.enable_quick_search && Desktop3DListener.root.qSearchGroup != null && Desktop3DListener.root.qSearchGroup.visible )
			{
				if( SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP )
				{
					Desktop3DListener.root.qSearchGroup.getSearchEditTextGroup().startAnimQuit();
				}
				else
				{
					Desktop3DListener.root.qSearchGroup.startQuickSearchAnimQuit( false );
				}
				return true;
			}
		}
		return false;
	}
	
	public void onThemeChanged()
	{
		if( !dialogCancelled )
			return;
		if( themeChanging )
			return;
		if( !iLoongApplication.getInstance().mThemeManager.changeTheme() )
			return;
		themeChanging = true;
		Desktop3DListener.d3d.ignoreClick( true );
		Desktop3DListener.d3d.ignoreLongClick( true );
		if( ThemeManager.getInstance().getThemeDB().getThemesStatus() > 0 )
		{
			// xiatian add start
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
			if( DefaultLayout.enable_scene_wallpaper && ThemeManager.getInstance().currentThemeIsSystemTheme() )
			{
				pref.edit().putString( "currentWallpaper" , "default" ).commit();
				PubProviderHelper.addOrUpdateValue( "wallpaper" , "currentWallpaper" , "default" );
				Intent intent = new Intent( this , WallpaperChangedReceiver.class );
				intent.setAction( WallpaperChangedReceiver.SCENE_WALLPAPER_CHANGE );
				PendingIntent pendingIntent = PendingIntent.getBroadcast( this , 0 , intent , 0 );
				AlarmManager am = (AlarmManager)getSystemService( ALARM_SERVICE );
				am.set( AlarmManager.RTC , System.currentTimeMillis() , pendingIntent );
			}
			else
				new Thread( new Runnable() {
					
					public void run()
					{
						ThemeManager.getInstance().ApplyWallpaper();
					}
				} ).start();
			// teapotXu add start:换主题后，标识当前的壁纸为主题自带默认壁纸,非用户自定义图片
			pref.edit().putBoolean( "userDefinedWallpaper" , false ).commit();
			// teapotXu add end
			ThemeManager.getInstance().getThemeDB().SaveThemesStatus( 0 );
		}
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				d3dListener.onThemeChanged();
			}
		} );
	}
	
	@Override
	public void onConfigurationChanged(
			Configuration newConfig )
	{
		Log.v( "launcher" , "onConfigurationChanged:" + newConfig );
		Utils3D.resetSize();
		Log.v( "launcher" , "utils3d width:" + Utils3D.getScreenWidth() + " height:" + Utils3D.getScreenHeight() );
		if( DefaultLayout.restartWhenOrientationChange )
		{
			super.onConfigurationChanged( newConfig );
			android.os.Process.killProcess( android.os.Process.myPid() );
		}
		if( currentLocale != null )
		{
			if( !currentLocale.equals( newConfig.locale.toString() ) )
			{
				Log.v( "launcher" , "restartSystem:" + currentLocale + "," + newConfig.locale );
				currentLocale = newConfig.locale.toString();
				super.onConfigurationChanged( newConfig );
				SystemAction.RestartSystem();
				return;
			}
		}
		currentLocale = newConfig.locale.toString();
		super.onConfigurationChanged( newConfig );
	}
	
	private void restoreState(
			Bundle savedState )
	{
		if( savedState == null )
		{
			return;
		}
		mRestoring = true;
	}
	
	@Override
	protected void onNewIntent(
			Intent intent )
	{
		Log.v( "iLoongLauncher" , "Launcher onNewIntent() start" );
		super.onNewIntent( intent );
		if( !dialogCancelled )
			return;
		if( Desktop3DListener.bCreatDone )
		{
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			if( curCellLayout != null )
			{
				for( int i = 0 ; i < curCellLayout.getChildCount() ; i++ )
				{
					if( curCellLayout.getChildAt( i ) instanceof Widget3D )
					{
						Widget3D curWidget3D = (Widget3D)curCellLayout.getChildAt( i );
						if( curWidget3D.isOpened() )
						{
							curWidget3D.onKeyEvent( KeyEvent.KEYCODE_HOME , KeyEvent.ACTION_UP );
							break;
						}
					}
				}
			}
			AppHost3D.appList.setMode( AppList3D.APPLIST_MODE_NORMAL );
			if( AppHost3D.popMenu2 != null )
			{
				AppHost3D.popMenu2.reset();
			}
		}
		if( Intent.ACTION_MAIN.equals( intent.getAction() ) )
		{
			getWindow().closeAllPanels();
			final View v = getWindow().peekDecorView();
			if( v != null && v.getWindowToken() != null )
			{
				InputMethodManager imm = (InputMethodManager)getSystemService( INPUT_METHOD_SERVICE );
				imm.hideSoftInputFromWindow( v.getWindowToken() , 0 );
			}
			//			boolean alreadyOnHome = ( ( intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT ) != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT );
			//				d3dListener.onHomeKey( alreadyOnHome );
			//			Log.d( "launcher" , "alreadyOnHome=" + alreadyOnHome );
			d3dListener.onHomeKey( false );
		}
	}
	
	@Override
	protected void onSaveInstanceState(
			Bundle outState )
	{
	}
	
	@Override
	public boolean dispatchKeyEvent(
			KeyEvent event )
	{
		if( event.getAction() == KeyEvent.ACTION_DOWN )
		{
			switch( event.getKeyCode() )
			{
				case KeyEvent.KEYCODE_HOME:
					return true;
				case KeyEvent.KEYCODE_VOLUME_DOWN:
					break;
				case KeyEvent.KEYCODE_BACK:
					if( introductionShown )
					{
						dismissIntroduction();
						return true;
					}
			}
		}
		else if( event.getAction() == KeyEvent.ACTION_UP )
		{
			switch( event.getKeyCode() )
			{
				case KeyEvent.KEYCODE_HOME:
					return true;
			}
		}
		return super.dispatchKeyEvent( event );
	}
	
	/**
	 * Prints out out state for debugging.
	 */
	public void dumpState()
	{
		Log.d( TAG , "BEGIN launcher2 dump state for launcher " + this );
		Log.d( TAG , "mSavedState=" + mSavedState );
		Log.d( TAG , "mWorkspaceLoading=" + mWorkspaceLoading );
		Log.d( TAG , "mRestoring=" + mRestoring );
		Log.d( TAG , "mWaitingForResult=" + mWaitingForResult );
		Log.d( TAG , "mSavedInstanceState=" + mSavedInstanceState );
		mModel.dumpState();
		Log.d( TAG , "END launcher2 dump state" );
	}
	
	/**
	 * Refreshes the shortcuts shown on the workspace.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void startBinding()
	{
		final Workspace workspace = xWorkspace;
		int count = workspace.getChildCount();
		for( int i = 0 ; i < count ; i++ )
		{
			// Use removeAllViewsInLayout() to avoid an extra requestLayout()
			// and invalidate().
			( (ViewGroup)workspace.getChildAt( i ) ).removeAllViewsInLayout();
		}
	}
	
	public void removeFolder(
			FolderInfo folder )
	{
		sFolders.remove( folder.id );
	}
	
	public UserFolderInfo getFolderInfo(
			long folderId )
	{
		return (UserFolderInfo)sFolders.get( folderId );
	}
	
	private boolean findBrowserByPackageName(
			String packageName )
	{
		if( packageName == null )
		{
			return false;
		}
		if( packageName.equals( "com.android.browser" ) || packageName.equals( "com.uc.browser" ) || packageName.equals( "com.google.android.browser" ) || packageName.equals( "com.skymobi.browser" ) || packageName
				.equals( "com.android.browser.BrowserActivity" ) || packageName.equals( "com.uc.browser.hd" ) || packageName.equals( "com.UCMobile" ) || packageName.equals( "com.tencent.mtt" ) || packageName
				.equals( "com.tencent.qbx" ) || packageName.equals( "com.opera.mini.android" ) || packageName.equals( "com.opera.browser" ) || packageName.equals( "com.oupeng.mini.android" ) || packageName
				.equals( "com.oupeng.mobile" ) || packageName.equals( "com.mx.browser" ) || packageName.equals( "com.android.chrome" ) || packageName.equals( "org.mozilla.firefox" ) || packageName
				.equals( "com.qihoo.browser" ) || packageName.equals( "com.qihoo.padbrowser" ) || packageName.equals( "com.baidu.browser.apps" ) || packageName.equals( "cn.dolphin.browser" ) || packageName
				.equals( "com.dolphin.browser.cn" ) || packageName.equals( "com.dolphin.browser.engine" ) || packageName.equals( "com.dolphin.browser.android.pad.cn" ) )
		{
			return true;
		}
		return false;
	}
	
	private boolean isCustomShortcut(
			Intent intent )
	{
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_THEME.equals( intent.getAction() ) )
		{
			SendMsgToAndroid.sendSelectHotZhuTi();
			return true;
		}
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_WALLPAPER.equals( intent.getAction() ) )
		{
			SendMsgToAndroid.sendSelectHotWallpaper();
			return true;
		}
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_EFFECT.equals( intent.getAction() ) )
		{
			if( Desktop3DListener.root != null )
			{
				Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_LONGCLICK );
			}
			if( DesktopEditHost.getInstance() != null )
			{
				DesktopEditHost.getInstance().mulpMenuHost.MenuCallBack( 3 );
			}
			return true;
		}
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_PREVIEW.equals( intent.getAction() ) )
		{
			if( Desktop3DListener.root != null )
			{
				Desktop3DListener.root.onCtrlEvent( iLoongLauncher.getInstance().d3dListener.getWorkspace3D() , Workspace3D.MSG_PAGE_SHOW_EDIT );
			}
			return true;
		}
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_SETTINGS.equals( intent.getAction() ) )
		{
			Intent setIntent = new Intent( this , FirstActivity.class );
			startActivity( setIntent );
			return true;
		}
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_APP.equals( intent.getAction() ) )
		{
			Intent appIntent = new Intent( Intent.ACTION_MANAGE_PACKAGE_STORAGE );
			startActivity( appIntent );
			return true;
		}
		if( CustomShortcutIcon.CUSTOM_SHORTCUT_ACTION_APPLIST.equals( intent.getAction() ) )
		{
			if( DefaultLayout.mainmenu_inout_no_anim )
			{
				if( Desktop3DListener.root != null )
					Desktop3DListener.root.showAllAppFromWorkspaceEx();
			}
			else
			{
				if( Desktop3DListener.root != null )
					Desktop3DListener.root.showAllAppFromWorkspace();
			}
			return true;
		}
		return false;
	}
	
	public void addRecentApp(
			Object tag )
	{
		if( tag instanceof ShortcutInfo )
		{
			Iterator<ShortcutInfo> ite = recentApp.iterator();
			while( ite.hasNext() )
			{
				ShortcutInfo info = ite.next();
				String pkgname = ( (ShortcutInfo)tag ).intent.getComponent().getPackageName();
				if( isVirtureIcon( pkgname ) )
				{
					if( info.intent.getComponent().getPackageName().equals( pkgname ) )
					{
						ite.remove();
						break;
					}
				}
				else if( info.intent.getComponent().getClassName().equals( ( (ShortcutInfo)tag ).intent.getComponent().getClassName() ) && info.intent.getComponent().getPackageName()
						.equals( ( (ShortcutInfo)tag ).intent.getComponent().getPackageName() ) )
				{
					ite.remove();
					break;
				}
			}
			recentApp.add( 0 , (ShortcutInfo)tag );
			if( recentApp.size() > 8 )
			{
				for( int i = 8 ; i < recentApp.size() ; i++ )
				{
					recentApp.remove( i );
				}
			}
		}
	}
	
	public static boolean isVirtureIcon(
			String pkgname )
	{
		if( pkgname.equalsIgnoreCase( "coco.bizhi" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhutixuanze" ) || pkgname.equalsIgnoreCase( "coco.uicenter" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.zhuomianshezhi" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.texiao" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.ziti" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.lock" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.scene" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.desktopsettings" ) )
		{
			return true;
		}
		else if( pkgname.equalsIgnoreCase( "coco.pingmuyulan" ) )
		{
			return true;
		}
		return false;
	}
	
	public boolean startActivitySafely(
			Intent intent ,
			Object tag )
	{
		if( isCustomShortcut( intent ) )
		{
			return true;
		}
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		try
		{
			if( intent.getComponent() != null && intent.getComponent().getClassName().equals( "com.coco.theme.themebox.MainActivity" ) )
			{
				if( DefaultLayout.personal_center_internal )
				{
					bindThemeActivityData( intent );
					startActivity( intent );
				}
				else
				{
					PackageManager pm = SetupMenuActions.getInstance().getContext().getPackageManager();
					if( pm.queryIntentActivities( intent , 0 ).size() == 0 )
					{
						iLoongLauncher.getInstance().mMainHandler.post( new Runnable() {
							
							@Override
							public void run()
							{
								themeCenterDown.ToDownloadApkDialog(
										iLoongLauncher.getInstance() ,
										iLoongLauncher.getInstance().getResources().getString( RR.string.theme ) ,
										"com.iLoong.base.themebox" );
							}
						} );
					}
					else
					{
						bindThemeActivityData( intent );
						startActivity( intent );
					}
				}
			}
			else
			{
				if( RR.net_version && intent.getCategories() != null && intent.getCategories().contains( "android.intent.category.BROWSABLE" ) && intent.getAction() != null && intent.getAction()
						.equals( "android.intent.action.VIEW" ) )
				{
					Intent browserIntent = new Intent( "android.intent.action.VIEW" );
					browserIntent.addCategory( "android.intent.category.DEFAULT" );
					browserIntent.addCategory( "android.intent.category.BROWSABLE" );
					Uri uri = Uri.parse( DefaultLayout.googleHomePage );
					browserIntent.setDataAndType( uri , null );
					Intent chooser = Intent.createChooser( browserIntent , getText( RR.string.chooser_3dwidget ) );
					startActivity( chooser );
				}
				else
					startActivity( intent );
			}
			if( intent != null && intent.getComponent() != null )
			{
				if( tag instanceof ShortcutInfo )
				{
					ShortcutInfo shortcutInfo = (ShortcutInfo)tag;
					if( !( shortcutInfo.appInfo == null ) )
					{
						com.iLoong.launcher.data.ApplicationInfo applicationInfo = shortcutInfo.appInfo;
						if( applicationInfo.isNews = true )
						{
							applicationInfo.isNews = false;
						}
					}
				}
				Messenger.sendMsgDelay( Messenger.EVENT_ADD_APP_USE_FREQUENCY , "FREQUENCY:" + intent.getComponent().toString() , 2000 );
				if( DefaultLayout.newHotSeatMainGroup && tag instanceof ShortcutInfo )
				{
					HotSeatMainGroup mMainGroup = (HotSeatMainGroup)( getD3dListener().getRoot().getHotSeatBar().getMainGroup() );
					mMainGroup.setLastInfo( ( (ShortcutInfo)tag ) );
				}
				addRecentApp( tag );
			}
			else
			{
				if( tag != null && tag instanceof ItemInfo )
				{
					ItemInfo tagInfo = (ItemInfo)tag;
					intent.putExtra( "itemType" , tagInfo.itemType );
					if( tagInfo.title != null )
					{
						intent.putExtra( "title" , ( (ItemInfo)tag ).title.toString() );
					}
				}
			}
			return true;
		}
		catch( ActivityNotFoundException e )
		{
			if( tag instanceof ShortcutInfo )
			{
				ShortcutInfo mShortcutInfo = (ShortcutInfo)tag;
				if( mShortcutInfo.intent.getBooleanExtra( "isApplistVirtualIcon" , false ) == true )
				{
					File file = new File( DownloadLockBoxService.DOWNLOAD_PATH , mShortcutInfo.title.toString() + ".apk" );
					if( file.exists() )
					{
						Intent intent2 = new Intent();
						intent2.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
						intent2.setAction( android.content.Intent.ACTION_VIEW );
						intent2.setDataAndType( Uri.fromFile( file ) , "application/vnd.android.package-archive" );
						startActivity( intent2 );
					}
					else
						downloadAPK( this , this.getPackageName() , mShortcutInfo.appInfo.packageName , mShortcutInfo.title.toString() );
					return true;
				}
			}
			Toast.makeText( this , string.activity_not_found , Toast.LENGTH_SHORT ).show();
			Log.e( TAG , "Unable to launch. tag=" + tag + " intent=" + intent , e );
		}
		catch( SecurityException e )
		{
			Toast.makeText( this , string.activity_not_found , Toast.LENGTH_SHORT ).show();
			Log.e(
					TAG ,
					"Launcher does not have the permission to launch " + intent + ". Make sure to create a MAIN intent-filter for the corresponding activity " + "or use the exported attribute for this activity. " + "tag=" + tag + " intent=" + intent ,
					e );
		}
		return false;
	}
	
	public void bindThemeActivityData(
			Intent intent )
	{
		ThemeConfig themeConfig = iLoongApplication.themeConfig;
		Bundle bundle = new Bundle();
		bundle.putBoolean( "personal_center_internal" , DefaultLayout.personal_center_internal );
		bundle.putString( "launcherApplyThemeAction" , themeConfig.launcherApplyThemeAction );
		bundle.putString( "launcherRestartAction" , themeConfig.launcherRestartAction );
		bundle.putString( "defaultThemePackageName" , themeConfig.defaultThemePackageName );
		bundle.putString( "launcherPackageName" , themeConfig.launcherPackageName );
		bundle.putString( "customWallpaperPath" , themeConfig.customWallpaperPath );
		bundle.putBoolean( "isdoovStyle" , themeConfig.isdoovStyle );
		bundle.putString( "galleryPkg" , themeConfig.galleryPkg );
		bundle.putBoolean( "isEffectVisiable" , themeConfig.isEffectVisiable );
		bundle.putStringArray( "app_list_string" , themeConfig.app_list_string );
		bundle.putStringArray( "workSpace_list_string" , themeConfig.workSpace_list_string );
		bundle.putBoolean( "page_effect_no_radom_style" , themeConfig.page_effect_no_radom_style );
		bundle.putBoolean( "disableSetWallpaperDimensions" , themeConfig.disable_set_wallpaper_dimensions );
		if( RR.net_version )
		{
			bundle.putBoolean( "net_version" , true );
		}
		bundle.putBoolean( "enable_personalcenetr_click_widget_to_add" , DefaultLayout.enable_personalcenetr_click_widget_to_add );
		Log.e( "test" , "bingData size:" + bundle.size() );
		intent.putExtras( bundle );
	}
	
	public static void downloadAPK(
			Context context ,
			String srcPackageName ,
			String destPackageName ,
			String fileName )
	{
		Intent intent = new Intent();
		intent.setClass( context , DownloadLockBoxService.class );
		intent.putExtra( DownloadLockBoxService.DOWNLOAD_FILE_NAME , fileName );
		intent.putExtra( DownloadLockBoxService.DOWNLOAD_URL_KEY , new UpdateManager( context ).getApkUrl( context , srcPackageName , destPackageName ) );
		Intent intentGetToDownloadAPKName = new Intent( "com.coco.lock2.lockbox.GetToDownloadAPKName" );
		if( destPackageName.equals( "com.cool.launcher" ) )
		{
			intent.putExtra( "logo" , "cocolauncher" );
		}
		else if( destPackageName.equals( "com.coco.lock2.lockbox" ) )
		{
			intent.putExtra( "logo" , "cocolock" );
		}
		intentGetToDownloadAPKName.putExtra( "ToDownloadAPKName" , destPackageName );
		context.sendBroadcast( intentGetToDownloadAPKName );
		context.startService( intent );
	}
	
	public boolean startActivitySafelyFromIntent(
			Intent intent )
	{
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		try
		{
			startActivity( intent );
			if( intent.getComponent() == null )
			{
				intent.putExtra( "itemType" , 1 );
			}
			return true;
		}
		catch( ActivityNotFoundException e )
		{
			Toast.makeText( this , string.activity_not_found_from_intent , Toast.LENGTH_SHORT ).show();
		}
		catch( SecurityException e )
		{
			Toast.makeText( this , string.activity_not_found_from_intent , Toast.LENGTH_SHORT ).show();
		}
		return false;
	}
	
	public void showRenameDialog(
			FolderInfo info )
	{
		mFolderInfo = info;
		mWaitingForResult = true;
		showDialog( DIALOG_RENAME_FOLDER );
	}
	
	public void showAddShortcutDialog()
	{
		showDialog( DIALOG_CREATE_SHORTCUT );
	}
	
	public void showAddSystemWidget()
	{
		mWaitingForResult = true;
		showDialog( DIALOG_CREATE_SYSTEM_WIDGET , null );
	}
	
	private int mCurrentDialogId = 0;
	private int[] dropPos = new int[2];
	private boolean bDialogShow = false;
	
	@Override
	protected Dialog onCreateDialog(
			int id )
	{
		mCurrentDialogId = id;
		switch( id )
		{
			case DIALOG_CREATE_SYSTEM_WIDGET:
			case DIALOG_CREATE_SHORTCUT:
				return new CreateShortcut().createDialog();
			case DIALOG_RENAME_FOLDER:
				return new RenameFolder().createDialog();
			case DIALOG_CIRCLE_DELALL:
				return new PopupDeleteAll().createDialog();
			case DIALOG_DOWNLOAD_WIDGET:
				return new DownLoadWidget().createDialog();
			case DIALOG_DELETE_PAGE:
				return new PageDelete().createDialog();
			case DIALOG_DELETE_FOLDER:
				return new TrashDeleteFolder().createDialog();
			case DIALOG_SORT_APP:
				return new SortApp().createDialog();
			case DIALOG_PICK_3DWIDGET:
				return new CreateShortcut().creagePick3DWidgetDialog();
			case DIALOG_APK_CANNOT_FOUND:
				return new ApkCannotFound().createDialog();
			case DIALOG_RESTART:
				return new RestartDialog().createDialog();
			case DIALOG_APP_EFFECT:
				return new AppEffectDialog().createDialog();
			case DIALOG_MAINMENU_BG:
				return new MainmenuBg().createDialog();
		}
		return super.onCreateDialog( id );
	}
	
	@SuppressWarnings( "static-access" )
	@Override
	protected void onPrepareDialog(
			int id ,
			Dialog dialog )
	{
		// teapotXu add start for dismissed the dialog when home-key pressed
		bDialogShow = true;
		if( mCurrentDialogId != id )
			mCurrentDialogId = id;
		// teapotXu add end
		switch( id )
		{
			case DIALOG_CREATE_SHORTCUT:
				break;
			case DIALOG_RENAME_FOLDER:
				if( mFolderInfo != null )
				{
					EditText input = (EditText)dialog.findViewById( RR.id.folder_name );
					String title = (String)mFolderInfo.title;
					if( title.endsWith( "x.z" ) )
					{
						int length = title.length();
						if( length > 3 )
						{
							mFolderInfo.title = title.substring( 0 , length - 3 );
						}
					}
					final CharSequence text = mFolderInfo.title;
					input.setText( text );
					input.setSelection( text.length() );
				}
				break;
			case DIALOG_SORT_APP:
				final RadioGroup radioGroup = (RadioGroup)dialog.findViewById( RR.id.radioGroup );
				switch( sortAppCheckId )
				{
					case AppList3D.SORT_NAME:
						radioGroup.check( RR.id.radioName );
						break;
					case AppList3D.SORT_INSTALL:
						radioGroup.check( RR.id.radioInstall );
						break;
					case AppList3D.SORT_USE:
						radioGroup.check( RR.id.radioFrequency );
						break;
					case AppList3D.SORT_FACTORY:
						radioGroup.check( RR.id.radioFactory );
						break;
					case AppList3D.SORT_BY_USER:
						radioGroup.clearCheck();
						break;
				}
				break;
			case DIALOG_APP_EFFECT:
				ListView listView = (ListView)dialog.findViewById( 0 );
				String initValue = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( SetupMenu.getKey( RR.string.setting_key_appeffects ) , "2" );
				Log.e( "effect" , "init=" + initValue );
				listView.setItemChecked( Integer.parseInt( initValue ) , true );
				break;
		}
	}
	
	private void pickShortcut(
			int requestCode ,
			int title )
	{
		Intent pickIntent = new Intent( Intent.ACTION_PICK_ACTIVITY );
		pickIntent.putExtra( Intent.EXTRA_INTENT , new Intent( Intent.ACTION_CREATE_SHORTCUT ) );
		pickIntent.putExtra( Intent.EXTRA_TITLE , getText( title ) );
		startActivityForResult( pickIntent , requestCode );
	}
	
	public void displaySystemWidget()
	{
		int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
		Intent pickIntent = new Intent( AppWidgetManager.ACTION_APPWIDGET_PICK );
		pickIntent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId );
		if( !DefaultLayout.enable_workspace_miui_edit_mode )
		{
			widget3DArray = new AddWidget3DListAdapter( iLoongLauncher.this );
			if( widget3DArray.getCount() != 0 )
			{
				ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
				ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
				for( int i = 0 ; i < widget3DArray.getCount() ; i++ )
				{
					ListItem item = (ListItem)widget3DArray.getItem( i );
					if( item.installed )
					{
						AppWidgetProviderInfo info = new AppWidgetProviderInfo();
						info.provider = new ComponentName( item.resolveInfo.activityInfo.packageName , item.resolveInfo.activityInfo.packageName );
						info.icon = item.resolveInfo.getIconResource();
						info.label = item.text.toString();
						customInfo.add( info );
						Bundle b = new Bundle();
						b.putString( EXTRA_CUSTOM_WIDGET , item.resolveInfo.activityInfo.packageName );
						customExtras.add( b );
					}
					else
					{
						continue;
					}
				}
				pickIntent.putParcelableArrayListExtra( AppWidgetManager.EXTRA_CUSTOM_INFO , customInfo );
				pickIntent.putParcelableArrayListExtra( AppWidgetManager.EXTRA_CUSTOM_EXTRAS , customExtras );
			}
		}
		if( DefaultLayout.appbar_widgets_special_name == true )
		{
			pickIntent.putExtra( Intent.EXTRA_TITLE , getString( RR.string.choose_widget_ex ) );
		}
		else
		{
			pickIntent.putExtra( Intent.EXTRA_TITLE , getString( RR.string.choose_widget ) );
		}
		startActivityForResult( pickIntent , REQUEST_PICK_APPWIDGET );
	}
	
	/**
	 * Displays the shortcut creation dialog and launches, if necessary, the
	 * appropriate activity.
	 */
	private class CreateShortcut implements DialogInterface.OnClickListener , DialogInterface.OnCancelListener , DialogInterface.OnDismissListener , DialogInterface.OnShowListener
	{
		
		private AddListAdapter mAdapter;
		
		@SuppressWarnings( "static-access" )
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			mAdapter = new AddListAdapter( iLoongLauncher.this );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = VERSION.SDK_INT;
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setTitle( getString( RR.string.setting_add_system_widget ) );
			builder.setAdapter( mAdapter , this );
			builder.setInverseBackgroundForced( true );
			AlertDialog dialog = builder.create();
			dialog.setOnCancelListener( this );
			dialog.setOnDismissListener( this );
			dialog.setOnShowListener( this );
			return dialog;
		}
		
		@SuppressWarnings( "static-access" )
		Dialog creagePick3DWidgetDialog()
		{
			Log.e( "launcher" , "create pick 3dWidget" );
			final AddWidget3DListAdapter widget3DAdapter = new AddWidget3DListAdapter( iLoongLauncher.this );
			final AlertDialog.Builder builder = new AlertDialog.Builder( iLoongLauncher.this );
			if( widget3DAdapter.getCount() == 0 )
			{
				builder.setTitle( RR.string.widget_empty_title );
				View layout = View.inflate( iLoongLauncher.this , RR.layout.widget_empty_dialog , null );
				TextView text = (TextView)layout.findViewById( RR.id.widget_empty_content );
				text.setText( RR.string.widget_empty_dialog );
				builder.setView( layout );
				builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
					
					public void onClick(
							DialogInterface dialog ,
							int which )
					{
						cleanup();
					}
				} );
				AlertDialog alertDialog = builder.create();
				alertDialog.setOnCancelListener( this );
				alertDialog.setOnDismissListener( this );
				alertDialog.setOnShowListener( this );
				return alertDialog;
			}
			else
			{
				builder.setTitle( getString( RR.string.chooser_3dwidget ) );
				builder.setAdapter( widget3DAdapter , new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(
							DialogInterface dialog ,
							int which )
					{
						ListItem item = (ListItem)widget3DAdapter.getItem( which );
						if( item.isInternal )
						{
							addInternal3DWidget( item.packageName );
						}
						else
						{
							if( item.installed )
							{
								add3DWidget( item.resolveInfo );
							}
							else
							{
								ShortcutInfo info = new ShortcutInfo();
								info.title = item.text;
								info.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
								info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
								info.intent.setComponent( new ComponentName( item.packageName , item.packageName ) );
								WidgetDownload.checkToDownload( info , true );
							}
						}
					}
				} );
				builder.setInverseBackgroundForced( true );
				AlertDialog alertDialog = builder.create();
				alertDialog.setOnCancelListener( this );
				alertDialog.setOnDismissListener( this );
				alertDialog.setOnShowListener( this );
				return alertDialog;
			}
		}
		
		public void onCancel(
				DialogInterface dialog )
		{
			mWaitingForResult = false;
			cleanup();
		}
		
		public void onDismiss(
				DialogInterface dialog )
		{
			try
			{
				dismissDialog( DIALOG_CREATE_SHORTCUT );
				ShortcutDialogShow = false;
				bDialogShow = false; // teapotXu added for dismissed the dialog
										// when home-key pressed
			}
			catch( Exception e )
			{
				// An exception is thrown if the dialog is not visible, which is
				// fine
				e.printStackTrace();
			}
		}
		
		private void cleanup()
		{
			try
			{
				removeDialog( DIALOG_PICK_3DWIDGET );
				bDialogShow = false;// teapotXu added for dismissed the dialog
									// when home-key pressed
			}
			catch( Exception e )
			{
				// An exception is thrown if the dialog is not visible, which is
				// fine
				e.printStackTrace();
			}
		}
		
		/**
		 * Handle the action clicked in the "Add to home" dialog.
		 */
		public void onClick(
				DialogInterface dialog ,
				int which )
		{
			Resources res = getResources();
			cleanup();
			which = mAdapter.getSelectId( which );
			switch( which )
			{
				case AddListAdapter.ITEM_3D_WIDGET:
				{
					showDialog( DIALOG_PICK_3DWIDGET );
					break;
				}
				case AddListAdapter.ITEM_CONTACT:
				{
					iLoongLauncher.this.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							add3DContact();
						}
					} );
					break;
				}
				case AddListAdapter.ITEM_APPWIDGET:
				{
					displaySystemWidget();
					break;
				}
				// folder is not in add list
				case AddListAdapter.ITEM_LIVE_FOLDER:
				{
					// Insert extra item to handle inserting folder
					iLoongLauncher.this.postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							add3DFolder();
						}
					} );
					break;
				}
				case AddListAdapter.ITEM_SHORTCUT:
				{
					// Insert extra item to handle picking application
					// dropPos[0] = -1;
					// dropPos[1] = -1;
					pickShortcut( REQUEST_PICK_SHORTCUT , RR.string.title_select_shortcut );
					break;
				}
				case AddListAdapter.ITEM_WALLPAPER:
				{
					startWallpaper();
					break;
				}
			}
		}
		
		public void onShow(
				DialogInterface dialog )
		{
			ShortcutDialogShow = true;
			mWaitingForResult = true;
		}
	}
	
	private void registerContentObservers()
	{
		ContentResolver resolver = getContentResolver();
		resolver.registerContentObserver( LauncherProvider.CONTENT_APPWIDGET_RESET_URI , true , mWidgetObserver );
		resolver.registerContentObserver( Settings.System.getUriFor( Settings.System.SCREEN_BRIGHTNESS ) , false , mScreenBrightnessObserver );
		resolver.registerContentObserver( Settings.System.getUriFor( Settings.System.SCREEN_BRIGHTNESS_MODE ) , false , mScreenBrightnessObserver );
	}
	
	private class ScreenBrightnessObserver extends ContentObserver
	{
		
		public ScreenBrightnessObserver()
		{
			super( new Handler() );
		}
		
		@Override
		public void onChange(
				boolean selfChange )
		{
			Log.i( "CooeeMainScene" , "screen brightness change!!" );
			try
			{
				boolean isbrightness = Settings.System.getInt( iLoongLauncher.getInstance().getContentResolver() , Settings.System.SCREEN_BRIGHTNESS_MODE ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
				if( !isbrightness )
				{
					ContentResolver resolver = getContentResolver();
					int brightness = android.provider.Settings.System.getInt( resolver , Settings.System.SCREEN_BRIGHTNESS , 100 );
					setBrightnesss( brightness );
				}
			}
			catch( SettingNotFoundException e )
			{
				e.printStackTrace();
			}
		}
	}
	
	private void addInternal3DWidget(
			final String packageName )
	{
		final String className = DefaultLayout.getWidgetItemClassName( packageName );
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				Widget3D widget3D = Widget3DManager.getInstance().getWidget3D( packageName , className );
				if( widget3D != null )
				{
					info = widget3D.getItemInfo();
					info.x = (int)( clickPoint.x - widget3D.width / 2 );
					info.y = (int)( clickPoint.y - widget3D.height / 2 );
					info.screen = workspace.getCurrentScreen();
					workspace.addInScreen( widget3D , info.screen , info.x , info.y , false );
				}
			}
		} );
	}
	
	@SuppressLint( "NewApi" )
	public void addWidgetFromPersonalCenter(
			final String pkgname )
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				Intent intent = new Intent( "com.iLoong.widget" , null );
				intent.setPackage( pkgname );
				PackageManager pm = iLoongApplication.getInstance().getPackageManager();
				List<ResolveInfo> mWidgetResolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA );
				if( mWidgetResolveInfoList.size() > 0 )
				{
					Widget3D widget3D = Widget3DManager.getInstance().getWidget3D( mWidgetResolveInfoList.get( 0 ) );
					if( widget3D != null )
					{
						if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
						{
							View3D tempGroup = workspace.getChildAt( workspace.getCurrentScreen() );
							if( tempGroup instanceof WorkspaceEditView )
							{
								{
									tempGroup.setTag( tempGroup );
									workspace.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
								}
							}
						}
						int[] cellXY = new int[2];
						info = widget3D.getItemInfo();
						int page_index = findCellXYandScreen( cellXY , info.spanX , info.spanY );
						RectF resultRect = new RectF();
						CellLayout3D cell = workspace.getCurrentCellLayout();
						if( cell != null )
							cell.cellToRect( cellXY[0] , cellXY[1] , info.spanX , info.spanY , resultRect );
						else
							Log.v( "cooee" , "addWidgetFromPersonalCenter --- current cell is null" );
						info.x = (int)( resultRect.centerX() - widget3D.width / 2 );
						info.y = (int)( resultRect.centerY() - widget3D.height / 2 );
						info.screen = page_index;
						workspace.addInScreen( widget3D , info.screen , info.x , info.y , false );
						workspace.setCurrentPage( page_index );
						if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode ) )
						{
							widget3D.onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_ENTRY , Widget3D.WIDGET_ANIMATION_DIRECTION_NONE );
						}
					}
				}
				else
				{
					if( !iLoongApplication.BuiltIn )
					{
						AddSystemWidget();
						return;
					}
					List<AppWidgetProviderInfo> providers = AppWidgetManager.getInstance( mInstance ).getInstalledProviders();
					for( AppWidgetProviderInfo prov : providers )
					{
						if( prov.provider.getPackageName().equals( pkgname ) )
						{
							int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
							UtilsBase.bindAppWidgetId( AppWidgetManager.getInstance( iLoongLauncher.this ) , appWidgetId , prov.provider );
							final Widget2DInfo launcherInfo = new Widget2DInfo( appWidgetId );
							AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo( appWidgetId );
							launcherInfo.setInfo( appWidgetInfo.provider.getPackageName() , appWidgetInfo.provider.getClassName() );
							if( beyondMTKWidgetNum( launcherInfo.getPackageName() ) )
							{
								Toast.makeText( mInstance , getString( RR.string.beyond_mtkwidget_num ) , Toast.LENGTH_SHORT ).show();
								return;
							}
							int requiredWidth = appWidgetInfo.minWidth;
							int requiredHeight = appWidgetInfo.minHeight;
							if( VERSION.SDK_INT >= 15 )
							{
								Rect padding = AppWidgetHostView.getDefaultPaddingForWidget( mInstance , appWidgetInfo.provider , null );
								requiredWidth = appWidgetInfo.minWidth + padding.left + padding.right;
								requiredHeight = appWidgetInfo.minHeight + padding.top + padding.bottom;
							}
							int[] spans = CellLayout3D.rectToCell( requiredWidth , requiredHeight , null );
							if( spans[0] > R3D.Workspace_cellCountX )
								spans[0] = R3D.Workspace_cellCountX;
							if( spans[1] > R3D.Workspace_cellCountY )
								spans[1] = R3D.Workspace_cellCountY;
							if( launcherInfo.getPackageName().equals( "com.mediatek.appwidget.video" ) || spans[1] > R3D.Workspace_cellCountY )
								spans[1] = R3D.Workspace_cellCountY;
							launcherInfo.spanX = spans[0];
							launcherInfo.spanY = spans[1];
							launcherInfo.minWidth = appWidgetInfo.minWidth;
							launcherInfo.minHeight = appWidgetInfo.minHeight;
							int[] cellXY = new int[2];
							int page_index = findCellXYandScreen( cellXY , launcherInfo.spanX , launcherInfo.spanY );
							workspace.setCurrentPage( page_index );
							launcherInfo.screen = page_index;
							RectF resultRect = new RectF();
							workspace.getCurrentCellLayout().cellToRect( cellXY[0] , cellXY[1] , launcherInfo.spanX , launcherInfo.spanY , resultRect );
							launcherInfo.x = (int)( resultRect.centerX() - spans[0] * R3D.Workspace_cell_each_width / 2 );
							launcherInfo.y = (int)( resultRect.centerY() - spans[1] * R3D.Workspace_cell_each_height / 2 );
							if( appWidgetInfo.minWidth >= Utils3D.getScreenWidth() / 2 )
								launcherInfo.x = 0;
							runOnUiThread( new Runnable() {
								
								public void run()
								{
									Widget widget = d3dListener.addAppWidget( launcherInfo );
									if( widget == null )
										return;
									if( !addAppWidget( launcherInfo , true ) )
										return;
									if( DefaultLayout.enable_haocheng_sys_widget_anim )
									{
										ComponentName cmpName = new ComponentName( launcherInfo.getPackageName() , launcherInfo.getClassName() );
										Intent it = new Intent( "hct.appwidget.action.APPWIDGET_ANIMATION" );
										it.putExtra( "provider" , cmpName );
										it.putExtra( "state" , 0 );// 0:create; 1:
																	// slide
										sendBroadcast( it );
									}
									launcherInfo.hostView.setWidget( widget );
								}
							} );
						}
					}
				}
			}
		} );
	}
	
	private int findCellXYandScreen(
			int cellXY[] ,
			int spanX ,
			int spanY )
	{
		Workspace3D workspace = d3dListener.getWorkspace3D();
		int page_index = workspace.getCurrentScreen();
		if( workspace.getCurrentCellLayout() == null )
		{
			return 0;
		}
		boolean findEmptyCell = workspace.getCurrentCellLayout().findCellForSpan( cellXY , spanX , spanY , true );
		if( !findEmptyCell )
		{
			int current = workspace.getCurrentScreen();
			int max = current > ( workspace.getChildCount() - current ) ? current : ( workspace.getChildCount() - current );
			for( int i = 1 ; i <= ( max * 2 ) ; i++ )
			{
				if( i % 2 == 1 )
					page_index = current + ( i + 1 ) / 2;
				else
					page_index = current - ( i + 1 ) / 2;
				if( page_index < 0 || page_index >= workspace.getChildCount() )
				{
					continue;
				}
				View3D child = workspace.getChildAt( page_index );
				if( child instanceof CellLayout3D && !( child instanceof MediaView3D ) && !child.name.equals( "newsView" ) )
				{
					findEmptyCell = ( (CellLayout3D)child ).findCellForSpan( cellXY , spanX , spanY , true );
					if( findEmptyCell )
					{
						break;
					}
				}
			}
		}
		if( !findEmptyCell )
		{
			CellLayout3D cell = new CellLayout3D( "celllayout" );
			if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
			{
				int before = 0;
				if( DefaultLayout.show_music_page )
				{
					before++;
				}
				if( DefaultLayout.enable_news )
				{
					before++;
				}
				if( DefaultLayout.enable_camera )
				{
					before++;
				}
				if( ( workspace.getPageNum() - before ) == DefaultLayout.default_workspace_pagecount_max )
				{
					return workspace.getCurrentScreen();
				}
				workspace.addPage( workspace.getPageNum() - before , cell );
				Log.v( "create2" , " Workspace3D onAddInstallApp NormalMode cellNum=" + workspace.getPageNum() );
				SendMsgToAndroid.sendAddWorkspaceCellMsg( workspace.getPageNum() - before - 1 );
				ThemeManager.getInstance().getThemeDB().SaveScreenCount( workspace.getPageNum() - before );
				PageIndicator3D pageIndicator = iLoongLauncher.getInstance().d3dListener.getRoot().getPageIndicator();
				pageIndicator.setPageNum( workspace.getPageNum() );
			}
			else
			{
				if( ( workspace.getPageNum() - 2 ) == DefaultLayout.default_workspace_pagecount_max )
				{
					return workspace.getCurrentScreen();
				}
				workspace.setEditModeOrigY( cell );
				workspace.addPage( workspace.getPageNum() - 1 , cell );
				Root3D root = iLoongLauncher.getInstance().d3dListener.getRoot();
				workspace.setTag( root.findView( "lastView" ) );
				root.onCtrlEvent( workspace , Workspace3D.MSG_CHANGE_TO_APPEND_PAGE );
			}
			for( page_index = workspace.getChildCount() - 1 ; page_index >= 0 ; page_index-- )
			{
				View3D view3D = workspace.getChildAt( page_index );
				if( view3D instanceof CellLayout3D && !( view3D instanceof MediaView3D ) && !view3D.name.equals( "newsView" ) )
				{
					( (CellLayout3D)view3D ).findCellForSpan( cellXY , spanX , spanY , true );
					break;
				}
			}
		}
		return page_index;
	}
	
	public void add3DWidget(
			final ResolveInfo resolveInfo )
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				Widget3D widget3D = Widget3DManager.getInstance().getWidget3D( resolveInfo );
				if( widget3D != null )
				{
					if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
					{
						View3D tempGroup = workspace.getChildAt( workspace.getCurrentScreen() );
						if( tempGroup instanceof WorkspaceEditView )
						{
							{
								tempGroup.setTag( tempGroup );
								workspace.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
							}
						}
					}
					info = widget3D.getItemInfo();
					info.x = (int)( clickPoint.x - widget3D.width / 2 );
					info.y = (int)( clickPoint.y - widget3D.height / 2 );
					info.screen = workspace.getCurrentScreen();
					workspace.addInScreen( widget3D , info.screen , info.x , info.y , false );
					if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode ) )
					{
						widget3D.onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_ENTRY , Widget3D.WIDGET_ANIMATION_DIRECTION_NONE );
					}
				}
			}
		} );
	}
	
	public void addIcon3D(
			final Icon3D icon3D )
	{
		Workspace3D workspace = d3dListener.getWorkspace3D();
		icon3D.setOrigin( icon3D.getWidth() / 2 , icon3D.getHeight() / 2 );
		icon3D.setScale( 1 , 1 );
		workspace.addInCurrenScreen( icon3D , -1 , -1 , true );
	}
	
	public void add3DWidget(
			final Widget3D widget3D )
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				if( widget3D != null )
				{
					if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
					{
						View3D tempGroup = workspace.getChildAt( workspace.getCurrentScreen() );
						if( tempGroup instanceof WorkspaceEditView )
						{
							{
								tempGroup.setTag( tempGroup );
								workspace.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
							}
						}
					}
					info = widget3D.getItemInfo();
					info.x = (int)( clickPoint.x - widget3D.width / 2 );
					info.y = (int)( clickPoint.y - widget3D.height / 2 );
					info.screen = workspace.getCurrentScreen();
					workspace.addInScreen( widget3D , info.screen , info.x , info.y , false );
					if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode ) )
					{
						widget3D.onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_ENTRY , Widget3D.WIDGET_ANIMATION_DIRECTION_NONE );
					}
				}
			}
		} );
	}
	
	public void add3DWidget(
			final String packageName )
	{
		iLoongLauncher.getInstance().postRunnable( new Runnable() {
			
			@Override
			public void run()
			{
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				String className = DefaultLayout.getWidgetItemClassName( packageName );
				Widget3D widget3D = Widget3DManager.getInstance().getWidget3D( packageName , className );
				if( widget3D != null )
				{
					if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
					{
						View3D tempGroup = workspace.getChildAt( workspace.getCurrentScreen() );
						if( tempGroup instanceof WorkspaceEditView )
						{
							{
								tempGroup.setTag( tempGroup );
								workspace.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
							}
						}
					}
					info = widget3D.getItemInfo();
					info.x = (int)( clickPoint.x - widget3D.width / 2 );
					info.y = (int)( clickPoint.y - widget3D.height / 2 );
					info.screen = workspace.getCurrentScreen();
					workspace.addInScreen( widget3D , info.screen , info.x , info.y , false );
					if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode ) )
					{
						widget3D.onStartWidgetAnimation( null , Widget3D.WIDGET_ANIMATION_TYPE_ENTRY , Widget3D.WIDGET_ANIMATION_DIRECTION_NONE );
					}
				}
			}
		} );
	}
	
	@SuppressWarnings( "static-access" )
	public void startWallpaper()
	{
		if( startWallpaperBox() == true )
		{
			return;
		}
		final Intent pickWallpaper = new Intent( Intent.ACTION_SET_WALLPAPER );
		if( DefaultLayout.wallpaper_has_edage )
		{
			startActivity( pickWallpaper );
			return;
		}
		Intent chooser = Intent.createChooser( pickWallpaper , getText( RR.string.chooser_wallpaper ) );
		startActivity( chooser );
	}
	
	@SuppressWarnings( "static-access" )
	void startActivityForResultSafely(
			Intent intent ,
			int requestCode )
	{
		String str = android.os.Build.BRAND;
		if( str.equals( "htc" ) )
		{
			String strclass = intent.getComponent().getClassName();
			String strpackagename = intent.getComponent().getPackageName();
			if( strclass.equals( "com.android.contacts.ContactShortcut" ) && strpackagename.equals( "com.android.contacts" ) )
			{
				intent.setComponent( new ComponentName( "com.htc.contacts" , "com.htc.contacts.BrowseLayerCarouselActivity" ) );
			}
		}
		try
		{
			startActivityForResult( intent , requestCode );
		}
		catch( ActivityNotFoundException e )
		{
			Toast.makeText( this , RR.string.activity_not_found , Toast.LENGTH_SHORT ).show();
		}
		catch( SecurityException e )
		{
			Toast.makeText( this , RR.string.activity_not_found , Toast.LENGTH_SHORT ).show();
			Log.e(
					TAG ,
					"Launcher does not have the permission to launch " + intent + ". Make sure to create a MAIN intent-filter for the corresponding activity " + "or use the exported attribute for this activity." ,
					e );
		}
	}
	
	private void dealAdd3DWidget(
			String WidgetpkgName )
	{
		if( widget3DArray.getCount() == 0 )
		{
			return;
		}
		for( int i = 0 ; i < widget3DArray.getCount() ; i++ )
		{
			ListItem item = (ListItem)widget3DArray.getItem( i );
			if( item.isInternal )
			{
				if( item.packageName.equals( WidgetpkgName ) )
				{
					addInternal3DWidget( item.packageName );
				}
			}
			else
			{
				if( widget3DArray.getItemResolveInfo( i ) == null )
				{
					if( item.packageName.equals( WidgetpkgName ) && item.installed == false )
					{
						ShortcutInfo info = new ShortcutInfo();
						info.title = item.text;
						info.intent = new Intent( Intent.ACTION_PACKAGE_INSTALL );
						info.intent.setComponent( new ComponentName( item.packageName , item.packageName ) );
						WidgetDownload.checkToDownload( info , true );
						return;
					}
				}
				else if( widget3DArray.getItemResolveInfo( i ).activityInfo.packageName.equals( WidgetpkgName ) )
				{
					if( item.installed )
					{
						add3DWidget( item.resolveInfo );
					}
					return;
				}
			}
		}
	}
	
	public void addAppWidgetFromPick(
			Intent data )
	{
		int appWidgetId = data.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , -1 );
		{
			String customWidget = data.getStringExtra( EXTRA_CUSTOM_WIDGET );
			if( customWidget != null )
			{
				dealAdd3DWidget( customWidget );
				return;
			}
		}
		if( false == canDropIntoWorkspaceWhileEditMode( data , 0 ) )
			return;
		addAppWidgetImpl( appWidgetId );
	}
	
	public boolean canDropIntoWorkspaceWhileEditMode(
			final Intent data ,
			final int dropType )
	{
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			if( Workspace3D.b_editmode_include_addpage )
			{
				// When drop widget/shortcut in addPageView, add the drop
				// widget/shortcut after appending the page.
				if( d3dListener.getCurrentScreen() <= 0 || d3dListener.getCurrentScreen() >= d3dListener.getScreenCount() - 1 )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							if( d3dListener == null || d3dListener.getWorkspace3D() == null )
								return;
							Workspace3D workspace3d = d3dListener.getWorkspace3D();
							View3D tempGroup = workspace3d.getChildAt( workspace3d.getCurrentScreen() );
							if( tempGroup instanceof WorkspaceEditView )
							{
								{
									tempGroup.setTag( tempGroup );
									// 1.save the added info first
									workspace3d.saveDropWidgetsInfo( dropType , data , null , null );
									// 2.Append the page of workspace
									workspace3d.onCtrlEvent( tempGroup , WorkspaceEditView.MSG_CHANGE_TO_APPEND_PAGE );
									// 3. now add the drop widget
									if( workspace3d.b_continue_add_widget_shortcut() )
									{
										workspace3d.continue_adding_widget_shortcut();
									}
								}
							}
						}
					} );
					return false;
				}
			}
		}
		return true;
	}
	
	@SuppressLint( "NewApi" )
	public void addAppWidgetFromDrop(
			ComponentName componentName ,
			int[] loc )
	{
		dropPos[0] = loc[0];
		dropPos[1] = loc[1];
		clickPoint.x = loc[0];
		clickPoint.y = loc[1];
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( iLoongLauncher.getInstance() ); // Jone
		int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
		if( RR.net_version )
		{
			//=============================Jone modify for matching 4.1 or upper.================================
			//label: JONE_MOD_WIDGET
			if( appWidgetManager.bindAppWidgetIdIfAllowed( appWidgetId , componentName ) )
			{
				AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo( appWidgetId );
				if( appWidgetInfo.configure != null )
				{
					Intent intent = new Intent( AppWidgetManager.ACTION_APPWIDGET_CONFIGURE );
					intent.setComponent( appWidgetInfo.configure );
					intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId );
					startActivityForResult( intent , REQUEST_CREATE_APPWIDGET );
				}
				else
					completeAddAppWidget( appWidgetId );
			}
			else
			{
				Intent intent = new Intent( AppWidgetManager.ACTION_APPWIDGET_BIND );
				intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId );
				intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_PROVIDER , componentName );
				iLoongLauncher.getInstance().startActivityForResult( intent , REQUEST_BIND_APPWIDGET );
			}
			// =========================================original
			// source===============================
		}
		else
		{
			// xujin 适用于api 16+
			UtilsBase.bindAppWidgetId( AppWidgetManager.getInstance( this ) , appWidgetId , componentName );
			addAppWidgetImpl( appWidgetId );
		}
	}
	
	void addAppWidgetImpl(
			int appWidgetId )
	{
		AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo( appWidgetId );
		if( appWidget.configure != null )
		{
			// Launch over to configure widget, if needed
			Intent intent = new Intent( AppWidgetManager.ACTION_APPWIDGET_CONFIGURE );
			intent.setComponent( appWidget.configure );
			intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId );
			startActivityForResultSafely( intent , REQUEST_CREATE_APPWIDGET );
		}
		else
		{
			// Otherwise just add it
			if( !DefaultLayout.enable_allow_add_more_widgets_to_desktop )
			{
				if( appWidget != null || appWidget.provider != null )
				{
					boolean isWidgetExist = LauncherModel.isWidgetExists( iLoongLauncher.getInstance() , appWidget.provider.getPackageName() , appWidget.provider.getClassName() );
					if( isWidgetExist )
					{
						SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getString(
								RR.string.widget_cannot_add_duplicate ,
								iLoongLauncher.getInstance().getString( RR.string.system_widget ) ) );
						return;
					}
				}
			}
			completeAddAppWidget( appWidgetId );
			Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
		}
	}
	
	public void AddSystemWidget()
	{
		Intent pickIntent = new Intent( AppWidgetManager.ACTION_APPWIDGET_PICK );
		int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
		pickIntent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , appWidgetId );
		startActivityForResult( pickIntent , REQUEST_PICK_APPWIDGET );
	}
	
	void processShortcutFromDrop(
			ComponentName componentName ,
			int x ,
			int y )
	{
		dropPos[0] = x;
		dropPos[1] = y;
		Intent createShortcutIntent = new Intent( Intent.ACTION_CREATE_SHORTCUT );
		createShortcutIntent.setComponent( componentName );
		processShortcut( createShortcutIntent , REQUEST_CREATE_SHORTCUT_FROM_DROP );
	}
	
	void processShortcut(
			Intent intent ,
			int request )
	{
		// Handle case where user selected "Applications"
		String applicationName = getResources().getString( RR.string.group_applications );
		String shortcutName = intent.getStringExtra( Intent.EXTRA_SHORTCUT_NAME );
		if( applicationName != null && applicationName.equals( shortcutName ) )
		{
			Intent mainIntent = new Intent( Intent.ACTION_MAIN , null );
			mainIntent.addCategory( Intent.CATEGORY_LAUNCHER );
			Intent pickIntent = new Intent( Intent.ACTION_PICK_ACTIVITY );
			pickIntent.putExtra( Intent.EXTRA_INTENT , mainIntent );
			pickIntent.putExtra( Intent.EXTRA_TITLE , getText( RR.string.title_select_application ) );
			startActivityForResultSafely( pickIntent , REQUEST_PICK_APPLICATION );
		}
		else
		{
			startActivityForResultSafely( intent , request );
		}
	}
	
	private void completeAddContactShortcut(
			Intent data )
	{
		final ShortcutInfo info = mModel.infoFromContactShortcutIntent( this , data );
		info.x = Utils3D.getScreenWidth() / 2;
		info.y = Utils3D.getScreenHeight() / 2;
		info.screen = d3dListener.getCurrentScreen();
		d3dListener.addShortcut( info );
	}
	
	public boolean completeAddShortcutFromDrop(
			Intent data ,
			int screen ,
			int x ,
			int y ,
			boolean toast )
	{
		if( false == canDropIntoWorkspaceWhileEditMode( data , 1 ) )
			return false;
		final ShortcutInfo info = mModel.infoFromShortcutIntent( this , data );
		if( info == null )
		{
			return false;
		}
		info.x = Utils3D.getScreenWidth() / 2;
		info.y = Utils3D.getScreenHeight() / 2;
		info.screen = screen;
		if( !findSlot( info.screen , x , y , mCellCoordinates , 1 , 1 , toast ) )
		{
			if( toast )
				Toast.makeText( this , getString( RR.string.no_space_add_icon ) , Toast.LENGTH_SHORT ).show();
			if( info.mIcon != null && !info.mIcon.isRecycled() )
				info.mIcon.recycle();
			return false;
		}
		info.cellX = mCellCoordinates[0];
		info.cellY = mCellCoordinates[1];
		Log.d( "launcher" , "cellX,cellY=" + info.cellX + "," + info.cellY );
		d3dListener.addShortcutFromDrop( info );
		return true;
	}
	
	public boolean deleteShortcutOnWorkspace(
			Intent data )
	{
		ComponentName deleteCmpName = data.getComponent();
		if( deleteCmpName == null )
			return false;
		Log.v( "cooee" , " iLoongLauncher ---- Original Packagename: " + deleteCmpName.getPackageName().toString() );
		final int count = d3dListener.getWorkspace3D().getChildCount();
		ArrayList<View3D> removeViewlist = new ArrayList<View3D>();
		for( int i = 0 ; i < count ; i++ )
		{
			View3D viewTmp = d3dListener.getWorkspace3D().getChildAt( i );
			if( !( viewTmp instanceof CellLayout3D ) )
				continue;
			final CellLayout3D layout = (CellLayout3D)viewTmp;
			int childCount = layout.getChildCount();
			for( int j = 0 ; j < childCount ; j++ )
			{
				View3D childview = layout.getChildAt( j );
				if( childview instanceof Icon3D )
				{
					ItemInfo itemInfo = ( (Icon3D)childview ).getItemInfo();
					if( itemInfo instanceof ShortcutInfo )
					{
						Intent intent = ( (ShortcutInfo)itemInfo ).intent;
						if( intent == null )
							continue;
						ComponentName cmpName = intent.getComponent();
						if( deleteCmpName.getPackageName() != null && cmpName != null && deleteCmpName.getPackageName().toString().equals( cmpName.getPackageName().toString() ) )
						{
							// remove this shortcut icon
							Root3D.deleteFromDB( itemInfo );
							removeViewlist.add( childview );
						}
					}
				}
				else if( childview instanceof FolderIcon3D )
				{
					FolderIcon3D folder = (FolderIcon3D)childview;
					for( int index = 0 ; index < folder.getChildCount() ; index++ )
					{
						View3D view = folder.getChildAt( index );
						if( view instanceof Icon3D )
						{
							ItemInfo itemInfo = ( (Icon3D)view ).getItemInfo();
							if( itemInfo instanceof ShortcutInfo )
							{
								Intent intent = ( (ShortcutInfo)itemInfo ).intent;
								if( intent == null )
									continue;
								ComponentName cmpName = intent.getComponent();
								if( deleteCmpName.getPackageName() != null && cmpName != null && deleteCmpName.getPackageName().toString().equals( cmpName.getPackageName().toString() ) )
								{
									// remove this shortcut icon
									Root3D.deleteFromDB( itemInfo );
									removeViewlist.add( view );
								}
							}
						}
					}
				}
			}
		}
		if( removeViewlist != null && removeViewlist.size() > 0 )
		{
			for( View3D view : removeViewlist )
			{
				view.remove();
			}
		}
		else
		{
			return false;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(
			int requestCode ,
			int resultCode ,
			Intent data )
	{
		if( DefaultLayout.enable_news && requestCode == NewsFlowReflect.getNewChannelValue() )
		{
			NewsFlowReflect.setfinishstate();
		}
		mWaitingForResult = false;
		Log.v( "launcher" , "onActivityResult requestCode=" + requestCode + "resultCode=" + resultCode );
		if( resultCode == RESULT_OK )
		{
			switch( requestCode )
			{
				case REQUEST_BIND_APPWIDGET:
					if( data == null )
					{
						return;
					}
					completeAddAppWidget( data.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , 1 ) );
					break;
				case REQUEST_PICK_APPLICATION:
					// completeAddApplication(this, data);
					break;
				case REQUEST_PICK_SHORTCUT:
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_SHORTCUT;
					processShortcut( data , REQUEST_CREATE_SHORTCUT_FROM_DROP );
					break;
				case REQUEST_CREATE_CONTACT_SHORTCUT:
					completeAddContactShortcut( data );
					break;
				case REQUEST_CREATE_SHORTCUT_FROM_DROP:
					completeAddShortcutFromDrop( data , d3dListener.getCurrentScreen() , dropPos[0] , dropPos[1] , true );
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
					break;
				case REQUEST_PICK_LIVE_FOLDER:
					// addLiveFolder(data);
					break;
				case REQUEST_CREATE_LIVE_FOLDER:
					// completeAddLiveFolder(data, mAddItemCellInfo);
					break;
				case REQUEST_PICK_APPWIDGET:
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_WIDGET;
					if( DefaultLayout.enable_takein_workspace_by_longclick )
					{
						iLoongLauncher.getInstance().postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								if( Workspace3D.isHideAll )
								{
									d3dListener.getRoot().hideorShowWorkspace( false );
								}
							}
						} );
					}
					addAppWidgetFromPick( data );
					break;
				case REQUEST_CREATE_APPWIDGET:
					int appWidgetId = data.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , -1 );
					completeAddAppWidget( appWidgetId );
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
					break;
				case REQUEST_PICK_WALLPAPER:
					break;
			}
		}
		else if( ( requestCode == REQUEST_PICK_APPWIDGET || requestCode == REQUEST_CREATE_APPWIDGET ) && resultCode == RESULT_CANCELED && data != null )
		{
			int appWidgetId = data.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID , -1 );
			if( appWidgetId != -1 )
			{
				mAppWidgetHost.deleteAppWidgetId( appWidgetId );
			}
		}
		else if( ( DefaultLayout.enable_workspace_miui_edit_mode ) && ( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode ) )
		{
			if( resultCode == RESULT_CANCELED )
			{
				if( requestCode == REQUEST_PICK_SHORTCUT || requestCode == REQUEST_PICK_APPWIDGET )
				{
					Workspace3D.EditModeAddItemType = Workspace3D.EditModeAddItemType_NONE;
				}
			}
		}
	}
	
	/**
	 * Add a widget to the workspace.
	 * 
	 * @param data
	 *            The intent describing the appWidgetId.
	 * @param cellInfo
	 *            The position on screen where to create the widget.
	 */
	@SuppressLint( "NewApi" )
	private void completeAddAppWidget(
			int appWidgetId )
	{
		if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
		{
			if( Workspace3D.b_editmode_include_addpage )
			{
				if( d3dListener.getCurrentScreen() <= 0 || d3dListener.getCurrentScreen() >= d3dListener.getScreenCount() - 1 )
				{
					SendMsgToAndroid.sendOurToastMsg( iLoongLauncher.getInstance().getString( RR.string.add_widget_error_tip ) );
					return;
				}
			}
		}
		Widget2DInfo launcherInfo = new Widget2DInfo( appWidgetId );
		launcherInfo.screen = d3dListener.getCurrentScreen();
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo( appWidgetId );
		launcherInfo.setInfo( appWidgetInfo.provider.getPackageName() , appWidgetInfo.provider.getClassName() );
		Log.v( "addAppWidget" , "packageName=" + launcherInfo.getPackageName() + " className=" + launcherInfo.getClassName() );
		if( beyondMTKWidgetNum( launcherInfo.getPackageName() ) )
		{
			Toast.makeText( this , getString( RR.string.beyond_mtkwidget_num ) , Toast.LENGTH_SHORT ).show();
			return;
		}
		int requiredWidth = appWidgetInfo.minWidth;
		int requiredHeight = appWidgetInfo.minHeight;
		if( VERSION.SDK_INT >= 15 )
		{
			Rect padding = AppWidgetHostView.getDefaultPaddingForWidget( this , appWidgetInfo.provider , null );
			requiredWidth = appWidgetInfo.minWidth + padding.left + padding.right;
			requiredHeight = appWidgetInfo.minHeight + padding.top + padding.bottom;
		}
		int[] spans = CellLayout3D.rectToCell( requiredWidth , requiredHeight , null );
		if( spans[0] > R3D.Workspace_cellCountX )
			spans[0] = R3D.Workspace_cellCountX;
		if( spans[1] > R3D.Workspace_cellCountY )
			spans[1] = R3D.Workspace_cellCountY;
		if( launcherInfo.getPackageName().equals( "com.mediatek.appwidget.video" ) || spans[1] > R3D.Workspace_cellCountY )
			spans[1] = R3D.Workspace_cellCountY;
		launcherInfo.spanX = spans[0];
		launcherInfo.spanY = spans[1];
		launcherInfo.minWidth = appWidgetInfo.minWidth;
		launcherInfo.minHeight = appWidgetInfo.minHeight;
		launcherInfo.x = (int)( clickPoint.x - spans[0] * R3D.Workspace_cell_each_width / 2 );
		launcherInfo.y = (int)( clickPoint.y - spans[1] * R3D.Workspace_cell_each_height / 2 );
		if( appWidgetInfo.minWidth >= Utils3D.getScreenWidth() / 2 )
			launcherInfo.x = 0;
		Widget widget = d3dListener.addAppWidget( launcherInfo );
		if( widget == null )
			return;
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && true == Workspace3D.b_editmode_include_addpage )
		{
			launcherInfo.screen = d3dListener.getCurrentScreen() - 1;
		}
		if( !addAppWidget( launcherInfo , true ) )
			return;
		if( DefaultLayout.enable_haocheng_sys_widget_anim )
		{
			ComponentName cmpName = new ComponentName( launcherInfo.getPackageName() , launcherInfo.getClassName() );
			Intent intent = new Intent( "hct.appwidget.action.APPWIDGET_ANIMATION" );
			intent.putExtra( "provider" , cmpName );
			intent.putExtra( "state" , 0 );// 0:create; 1: slide
			sendBroadcast( intent );
		}
		launcherInfo.hostView.setWidget( widget );
	}
	
	public boolean beyondMTKWidgetNum(
			String packageName )
	{
		if( packageName.contains( "com.mediatek.appwidget.video" ) )
		{
			View mtkWidgetView = xWorkspace.searchIMTKWidget( xWorkspace );
			if( mtkWidgetView != null )
				return true;
		}
		return false;
	}
	
	public void moveAppWidget(
			View3D view ,
			int screen )
	{
		Widget widget = (Widget)view;
		Widget2DInfo launcherInfo = widget.getItemInfo();
		if( !launcherInfo.canMove )
			return;
		xWorkspace.removeWidget( widget , screen );
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode && Workspace3D.b_editmode_include_addpage == true )
		{
			xWorkspace.addInScreen( launcherInfo.hostView , d3dListener.getCurrentScreen() - 1 , launcherInfo.x , launcherInfo.y , launcherInfo.spanX , launcherInfo.spanY , false );
		}
		else
		{
			xWorkspace.addInScreen( launcherInfo.hostView , d3dListener.getCurrentScreen() , launcherInfo.x , launcherInfo.y , launcherInfo.spanX , launcherInfo.spanY , false );
		}
		if( xWorkspace.searchIMTKWidget( launcherInfo.hostView ) != null )
		{
			xWorkspace.stopCoverMTKWidgetView();
		}
	}
	
	public boolean addAppWidgetHM(
			Widget2DInfo launcherInfo ,
			boolean force ,
			Widget widget )
	{
		int appWidgetId = launcherInfo.appWidgetId;
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo( appWidgetId );
		if( !mRestoring )
		{
			// Perform actual inflation because we're live
			launcherInfo.hostView = (WidgetHostView)mAppWidgetHost.createView( this , appWidgetId , appWidgetInfo );
			launcherInfo.hostView.setAppWidget( appWidgetId , appWidgetInfo );
			launcherInfo.hostView.setTag( launcherInfo );
			int[] spans = new int[]{ launcherInfo.spanX , launcherInfo.spanY };
			// // Try finding open space on Launcher screen
			final int[] xy = mCellCoordinates;
			if( !force && !findSlot( launcherInfo.screen , dropPos[0] , dropPos[1] , xy , spans[0] , spans[1] , true ) )
			{
				Toast.makeText( this , getString( RR.string.no_space_add_icon ) , Toast.LENGTH_SHORT ).show();
				if( appWidgetId != -1 )
					mAppWidgetHost.deleteAppWidgetId( appWidgetId );
				return false;
			}
			launcherInfo.spanX = spans[0];
			launcherInfo.spanY = spans[1];
			Desktop3DListener.root.addFlySysWidget( launcherInfo , widget );
		}
		return true;
	}
	
	public boolean addAppWidget(
			Widget2DInfo launcherInfo ,
			boolean force )
	{
		int appWidgetId = launcherInfo.appWidgetId;
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo( appWidgetId );
		if( !mRestoring )
		{
			// Perform actual inflation because we're live
			launcherInfo.hostView = (WidgetHostView)mAppWidgetHost.createView( this , appWidgetId , appWidgetInfo );
			launcherInfo.hostView.setAppWidget( appWidgetId , appWidgetInfo );
			launcherInfo.hostView.setTag( launcherInfo );
			int[] spans = new int[]{ launcherInfo.spanX , launcherInfo.spanY };
			// // Try finding open space on Launcher screen
			final int[] xy = mCellCoordinates;
			if( !force && !findSlot( launcherInfo.screen , dropPos[0] , dropPos[1] , xy , spans[0] , spans[1] , true ) )
			{
				Toast.makeText( this , getString( RR.string.no_space_add_icon ) , Toast.LENGTH_SHORT ).show();
				if( appWidgetId != -1 )
					mAppWidgetHost.deleteAppWidgetId( appWidgetId );
				return false;
			}
			// Build Launcher-specific widget info and save to databas
			launcherInfo.spanX = spans[0];
			launcherInfo.spanY = spans[1];
			xWorkspace.addInScreen( launcherInfo.hostView , launcherInfo.screen , launcherInfo.x , launcherInfo.y , launcherInfo.spanX , launcherInfo.spanY , false );
		}
		return true;
	}
	
	private boolean findSlot(
			int screen ,
			int x ,
			int y ,
			int[] xy ,
			int spanX ,
			int spanY ,
			boolean toast )
	{
		Log.d( "widget" , "x,y,spanX,spanY=" + xy[0] + " " + xy[1] + " " + spanX + " " + spanY );
		if( !d3dListener.findCellForSpan( screen , x , y , xy , spanX , spanY ) )
		{
			return false;
		}
		return true;
	}
	
	public WidgetHost getWidgetHost()
	{
		return mAppWidgetHost;
	}
	
	public static void LauncherbindFolders(
			final HashMap<Long , FolderInfo> folders )
	{
		sFolders.clear();
		sFolders.putAll( folders );
	}
	
	public UserFolderInfo addFolder(
			int x ,
			int y )
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = d3dListener.getCurrentScreen();
		folderInfo.x = x;
		folderInfo.y = y;
		return folderInfo;
	}
	
	public void addFolderInfoToSFolders(
			UserFolderInfo folderInfo )
	{
		if( folderInfo == null )
		{
			return;
		}
		UserFolderInfo findFolderInfo = getFolderInfo( folderInfo.id );
		if( findFolderInfo != null )
		{
			return;
		}
		sFolders.put( folderInfo.id , folderInfo );
	}
	
	public UserFolderInfo addHotSeatFolder(
			int x ,
			int y ,
			int index )
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = index;
		folderInfo.x = x;
		folderInfo.y = y;
		folderInfo.angle = HotSeat3D.TYPE_WIDGET;
		// Update the model
		LauncherModel.addItemToDatabase( this , folderInfo , LauncherSettings.Favorites.CONTAINER_HOTSEAT , folderInfo.screen , x , y , -1 , -1 , false );
		sFolders.put( folderInfo.id , folderInfo );
		return folderInfo;
	}
	
	public UserFolderInfo addFolder(
			String name )
	{
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = name;
		folderInfo.x = 0;
		folderInfo.y = 0;
		return folderInfo;
	}
	
	public void removeAppWidget(
			Widget2DInfo launcherInfo )
	{
		launcherInfo.hostView = null;
	}
	
	public WidgetHost getAppWidgetHost()
	{
		return mAppWidgetHost;
	}
	
	private class RenameFolder
	{
		
		private EditText mInput;
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.rename_folder , null );
			mInput = (EditText)layout.findViewById( RR.id.folder_name );
			mInput.setTextColor( R3D.folder_rename_text_color );
			InputFilter[] filters = new InputFilter[1];
			filters[0] = new InputFilter.LengthFilter( 11 ) {
				
				@Override
				public CharSequence filter(
						CharSequence source ,
						int start ,
						int end ,
						Spanned dest ,
						int dstart ,
						int dend )
				{
					if( source.equals( "\n" ) )
					{
						source = " ";
					}
					int destLen = dest.length();
					int srcLen = source.length();
					if( destLen + srcLen > 11 )
					{
						Toast.makeText( mInstance , getString( RR.string.fold_name_too_long ) , Toast.LENGTH_SHORT ).show();
						return "";
					}
					return source;
				}
			};
			mInput.setFilters( filters );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = VERSION.SDK_INT;
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			builder.setTitle( getString( RR.string.rename_folder_title ) );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					renameFoldercleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					renameFoldercleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.rename_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					changeFolderName();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
					mWaitingForResult = true;
					mInput.requestFocus();
					InputMethodManager inputManager = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE );
					inputManager.showSoftInput( mInput , 0 );
				}
			} );
			return dialog;
		}
		
		private void changeFolderName()
		{
			String name = mInput.getText().toString();
			name = name.trim();
			if( !TextUtils.isEmpty( name ) && mFolderInfo != null )
			{
				// Make sure we have the right folder info
				mFolderInfo = sFolders.get( mFolderInfo.id );
				name = name.concat( "x.z" );
				mFolderInfo.title = name;
				LauncherModel.updateItemInDatabase( iLoongLauncher.this , mFolderInfo );
				if( folderIcon != null )
				{
					if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
					{
						if( name.endsWith( "x.z" ) )
						{
							int length = name.length();
							if( length > 3 )
							{
								name = name.substring( 0 , length - 3 );
							}
						}
						folderIcon.mFolderMIUI3D.setEditText( name );
					}
					else
						folderIcon.mFolder.setEditText( name );
				}
			}
			renameFoldercleanup();
		}
	}
	
	public void renameFoldercleanup()
	{
		dismissDialog( DIALOG_RENAME_FOLDER );
		mWaitingForResult = false;
		folderIcon.bRenameFolder = false;
		mFolderInfo = null;
		folderIcon = null;
		bDialogShow = false;
		mCurrentDialogId = 0;
	}
	
	public void showProgressDialog()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
		boolean restart = prefs.getBoolean( iLoongLauncher.RUNTIME_STATE_RESTART , false );
		prefs.edit().putBoolean( iLoongLauncher.RUNTIME_STATE_RESTART , false ).commit();
		if( restart )
		{
			return;
		}
	}
	
	public void cancelProgressDialog()
	{
		if( m_dialog != null )
		{
			m_dialog.cancel();
		}
		dialogCancelled = true;
	}
	
	public void finishLoad()
	{
		if( DefaultLayout.enable_quick_search )
		{
			iLoongLauncher.getInstance().postRunnable( new Runnable() {
				
				@Override
				public void run()
				{
					if( iLoongLauncher.getInstance().d3dListener.root.qSearchGroup == null )
					{
						iLoongLauncher.getInstance().d3dListener.root.qSearchGroup = new QSearchGroup( "qSearchGroup" );
						iLoongLauncher.getInstance().d3dListener.root.addView( iLoongLauncher.getInstance().d3dListener.root.qSearchGroup );
					}
					//					Desktop3DListener.root.qSearchGroup.load();
				}
			} );
		}
		Log.i( "launcher" , "finishLoad" );
		if( DefaultLayout.enable_quick_search )
		{
			//Desktop3DListener.root.qSearchGroup.load();
		}
		if( new ConfigBase().isEnableShellSDK( this ) ) // xiatian add //change the
														// way to read the
														// switch of
														// CooeeShellSDK
		{
			Log.v( "shell" , "init shell" );
		}
		for( int i = 0 ; i < mHotseatIcons.length ; i++ )
		{
			if( mHotseatIcons[i] != null )
				mHotseatIcons[i].recycle();
		}
		ApkConfig apkConfig = new ApkConfig();
		if( apkConfig.apkConfigRight() == false )
		{
			SendMsgToAndroid.sendCreateRestartDialogMsg();
		}
		else
		{
			installAssertAPK = true;
			if( installAssertAPK )
			{
				installAssertAPK();
				installAssertAPK = false;
			}
		}
		if( ( DefaultLayout.enable_air_default_layout ) && ( DefaultLayout.is_demo_version == false ) )
		{
			AirDefaultLayout.getInstance().start( this );
		}
		if( d3dListener.pageContainer != null && !d3dListener.pageContainer.isVisible() )
		{
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			if( curCellLayout == null )
			{
				Log.i( "launcher" , "error no icon" );
				android.os.Process.killProcess( android.os.Process.myPid() );
			}
			else
				Log.i( "launcher" , "error no icon:ok" );
		}
		finishLoad = true;
		DefaultLayout.getInstance().cancelProgressDialog();
	}
	
	private void installAssertAPK()
	{
		if( RR.net_version )
		{
			return;
		}
		final File fileDir = getFilesDir();
		Thread thread = new Thread( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					Utils3D.showTimeFromStart( "wait install" );
					Thread.sleep( DefaultLayout.install_apk_delay * 1000 );
					Utils3D.showTimeFromStart( "start install" );
				}
				catch( InterruptedException e3 )
				{
					e3.printStackTrace();
				}
				String[] apks = null;
				try
				{
					apks = iLoongLauncher.this.getAssets().list( "apk" );
				}
				catch( IOException e1 )
				{
					e1.printStackTrace();
					return;
				}
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
				PackageManager pm = getPackageManager();
				for( int i = 0 ; i < apks.length ; i++ )
				{
					final String apkName = apks[i];
					File tmp = new File( fileDir.getAbsolutePath() + "/" + apkName );
					if( tmp.exists() )
						tmp.delete();
					if( true )
					{
						try
						{
							String packageName = apkName.substring( 0 , apkName.length() - 4 );
							pm.getPackageInfo( packageName , PackageManager.GET_ACTIVITIES );
							Log.e( "apk" , "installed:" + packageName );
							continue;
						}
						catch( Exception e )
						{
							Log.e( "apk" , "install package again:" + apkName );
						}
					}
					else
					{
						boolean hasInstall = prefs.getBoolean( apkName , false );
						if( hasInstall )
						{
							Log.e( "apk" , "have install:" + apkName );
							continue;
						}
					}
					try
					{
						tmp.createNewFile();
					}
					catch( IOException e1 )
					{
						e1.printStackTrace();
					}
					FileOutputStream fos = null;
					BufferedInputStream bis = null;
					int BUFFER_SIZE = 1024;
					byte[] buf = new byte[BUFFER_SIZE];
					int size = 0;
					try
					{
						bis = new BufferedInputStream( iLoongLauncher.this.getAssets().open( "apk/" + apkName ) );
						fos = new FileOutputStream( tmp );
						while( ( size = bis.read( buf ) ) != -1 )
							fos.write( buf , 0 , size );
						fos.close();
						bis.close();
					}
					catch( FileNotFoundException e )
					{
						e.printStackTrace();
					}
					catch( IOException e )
					{
						e.printStackTrace();
					}
					PackageInfo info = pm.getPackageArchiveInfo( tmp.getAbsolutePath() , PackageManager.GET_ACTIVITIES );
					String packageName = null;
					if( info != null )
					{
						try
						{
							ApplicationInfo appInfo = info.applicationInfo;
							packageName = appInfo.packageName;
							pm.getPackageInfo( packageName , PackageManager.GET_ACTIVITIES );
							Log.e( "apk" , "already exist:" + packageName );
							tmp.delete();
							prefs.edit().putBoolean( apkName , true ).commit();
							continue;
						}
						catch( Exception e )
						{
							Log.e( "apk" , "need install:" + packageName );
						}
					}
					int n = 0;
					while( n < 5 )
					{
						UtilsBase.sync_do_exec( "chmod 777 " + tmp.getAbsolutePath() );
						boolean success = UtilsBase.do_exec( "pm install " + tmp.getAbsolutePath() , packageName );
						if( !success )
						{
							Log.e( "apk" , "install fail:" + apkName );
							break;
						}
						if( packageName != null )
						{
							try
							{
								pm.getPackageInfo( packageName , PackageManager.GET_ACTIVITIES );
								Log.e( "apk" , "install ok:" + packageName );
								prefs.edit().putBoolean( apkName , true ).commit();
								break;
							}
							catch( Exception e )
							{
								Log.e( "apk" , "install package again:" + packageName );
							}
						}
						else
							n++;
						try
						{
							Thread.sleep( 5000 );
							Log.e( "apk" , "sleep:" + apkName );
						}
						catch( InterruptedException e2 )
						{
							e2.printStackTrace();
						}
						n++;
					}
					tmp.delete();
				}
			}
		} );
		thread.setPriority( android.os.Process.THREAD_PRIORITY_BACKGROUND );
		thread.start();
	}
	
	private class TrashDeleteFolder
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.trash_pop_delfolder , null );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
				TextView text = (TextView)layout.findViewById( RR.id.label );
				text.setTextColor( Color.BLACK );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.circle_cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					trashdeleteFolderResult = Workspace3D.CIRCLE_POP_ACK_ACTION;
					Gdx.graphics.requestRendering();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
					mWaitingForResult = true;
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			dismissDialog( DIALOG_DELETE_FOLDER );
			bDialogShow = false;
			trashdeleteFolderResult = Workspace3D.CIRCLE_POP_CANCEL_ACTION;
			Gdx.graphics.requestRendering();
		}
	}
	
	private class SortApp
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final ViewGroup layout = (ViewGroup)View.inflate( iLoongLauncher.this , RR.layout.sort_dialog , null );
			final RadioGroup radioGroup = (RadioGroup)layout.findViewById( RR.id.radioGroup );
			if( !DefaultLayout.show_default_app_sort )
			{
				radioGroup.findViewById( RR.id.radioFactory ).setVisibility( View.GONE );
			}
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				for( int i = 0 ; i < radioGroup.getChildCount() ; i++ )
				{
					View child = radioGroup.getChildAt( i );
					if( child instanceof RadioButton )
					{
						( (RadioButton)child ).setTextColor( Color.BLACK );
					}
				}
			}
			switch( sortAppCheckId )
			{
				case AppList3D.SORT_NAME:
					radioGroup.check( RR.id.radioName );
					break;
				case AppList3D.SORT_INSTALL:
					radioGroup.check( RR.id.radioInstall );
					break;
				case AppList3D.SORT_USE:
					radioGroup.check( RR.id.radioFrequency );
					break;
				case AppList3D.SORT_FACTORY:
					radioGroup.check( RR.id.radioFactory );
					break;
				case AppList3D.SORT_BY_USER:
					radioGroup.clearCheck();
					break;
			}
			AlertDialog.Builder builder = null;
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			builder.setTitle( getString( RR.string.sort_dialog_title ) );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.circle_cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					int checkId = radioGroup.getCheckedRadioButtonId();
					int checked = -1;
					if( checkId == RR.id.radioName )
					{
						checked = AppList3D.SORT_NAME;
						UmEventUtil.applistSort( mInstance , "ApplistSort" , "SORT_NAME" , 60000 );
					}
					else if( checkId == RR.id.radioInstall )
					{
						checked = AppList3D.SORT_INSTALL;
						UmEventUtil.applistSort( mInstance , "ApplistSort" , "SORT_INSTALL" , 60000 );
					}
					else if( checkId == RR.id.radioFrequency )
					{
						checked = AppList3D.SORT_USE;
						UmEventUtil.applistSort( mInstance , "ApplistSort" , "SORT_USE" , 60000 );
					}
					else if( checkId == RR.id.radioFactory )
					{
						checked = AppList3D.SORT_FACTORY;
						UmEventUtil.applistSort( mInstance , "ApplistSort" , "SORT_FACTORY" , 60000 );
					}
					d3dListener.sortApp( checked , sortOrigin );
					cleanup();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			return dialog;
		}
		
		private void cleanup()
		{
			dismissDialog( DIALOG_SORT_APP );
			bDialogShow = false;
		}
	}
	
	private class PopupDeleteAll
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.circle_pop_delall , null );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
				TextView text = (TextView)layout.findViewById( RR.id.label );
				text.setTextColor( Color.BLACK );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.circle_cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					popResult = Workspace3D.CIRCLE_POP_CANCEL_ACTION;
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					popResult = Workspace3D.CIRCLE_POP_ACK_ACTION;
					Gdx.graphics.requestRendering();
					cleanup();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
					mWaitingForResult = true;
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			dismissDialog( DIALOG_CIRCLE_DELALL );
			bDialogShow = false;
		}
	}
	
	private class RestartDialog
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.restart_pop_dialog , null );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
				TextView text = (TextView)layout.findViewById( RR.id.label );
				text.setTextColor( Color.BLACK );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			builder.setMessage( "桌面程序已被破坏!!!" );
			builder.setCancelable( false );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					int i = 1 / 0;
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			dismissDialog( DIALOG_RESTART );
			bDialogShow = false;
		}
	}
	
	private class AppEffectDialog
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			String initValue;
			initValue = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getString( SetupMenu.getKey( RR.string.setting_key_appeffects ) , "0" );
			Log.e( "effect" , "init=" + initValue );
			String[] data = R3D.app_list_string;
			if( data == null )
			{
				Log.v( "cooee" , "AppEffectDialog --- createDialog --- data == null-- return " );
				return null;
			}
			final ListView listview = new ListView( iLoongLauncher.this );
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				listview.setAdapter( new ArrayAdapter<String>( iLoongLauncher.this , RR.layout.simple_list_item_single_choice , data ) );
			}
			else
			{
				listview.setAdapter( new ArrayAdapter<String>( iLoongLauncher.this , android.R.layout.simple_list_item_single_choice , data ) );
			}
			listview.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
			listview.setItemChecked( Integer.parseInt( initValue ) , true );
			listview.setId( 0 );
			AlertDialog.Builder builder = null;
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
				listview.setDivider( new ColorDrawable( 0xffdbdbdb ) );
				listview.setDividerHeight( 1 );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setTitle( RR.string.appeffects_dialog_title );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					int i = listview.getCheckedItemPosition();
					String[] value = R3D.app_list_string;
					int select = i;
					PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).edit().putString( SetupMenu.getKey( RR.string.setting_key_appeffects ) , "" + select ).commit();
					PubProviderHelper.addOrUpdateValue( "effect" , SetupMenu.getKey( RR.string.setting_key_appeffects ) , "" + select );
					d3dListener.setAppEffectType( select );
					SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
					cleanup();
				}
			} );
			builder.setView( listview );
			Dialog dialog = builder.create();
			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams p = dialogWindow.getAttributes();
			p.height = (int)( Utils3D.getScreenHeight() * 0.8 );
			p.width = (int)( Utils3D.getScreenWidth() * 0.95 );
			dialogWindow.setAttributes( p );
			return dialog;
		}
		
		private void cleanup()
		{
			dismissDialog( DIALOG_APP_EFFECT );
			bDialogShow = false;
		}
	}
	
	@SuppressLint( "NewApi" )
	private class ApkCannotFound
	{
		
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.apk_cannot_found_dialog , null );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			String DownLoadtitle = null;
			if( Widget3DManager.curDownload != null )
				DownLoadtitle = (String)Widget3DManager.curDownload.title;
			builder.setTitle( DownLoadtitle );
			TextView text = (TextView)layout.findViewById( RR.id.label );
			String content = text.getText().toString();
			text.setText( content );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				text.setTextColor( Color.BLACK );
			}
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					Widget3DManager.curDownload = null;
					cleanup();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			removeDialog( DIALOG_APK_CANNOT_FOUND );
			bDialogShow = false;
		}
	}
	
	private class DownLoadWidget
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.widget_download_dialog , null );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( RR.drawable.ic_launcher );
			String DownLoadtitle = null;
			if( Widget3DManager.curDownload != null )
				DownLoadtitle = (String)Widget3DManager.curDownload.title;
			else
				DownLoadtitle = DefaultLayout.getmmSettingTitle();
			builder.setTitle( DownLoadtitle );
			TextView text = (TextView)layout.findViewById( RR.id.label );
			String content = text.getText().toString();
			content = DownLoadtitle + languageSpace + content;
			text.setText( content );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				text.setTextColor( Color.BLACK );
			}
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.circle_cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					Widget3DManager.curDownload = null;
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					if( Widget3DManager.curDownload == null )
					{
						DefaultLayout.startDownLoadmmSetting();
						cleanup();
						return;
					}
					String pkgname = Widget3DManager.curDownload.intent.getComponent().getPackageName();
					String classname = Widget3DManager.curDownload.intent.getComponent().getClassName();
					String apkname = DefaultLayout.GetDefaultWidgetApkname( pkgname , null );
					String dlTitle = (String)Widget3DManager.curDownload.title;
					String customID = null;
					if( Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW )
						customID = DefaultLayout.GetDefaultWidgetCustomID( pkgname );
					else if( Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW )
						customID = DefaultLayout.GetVirtureCustomID( pkgname , classname );
					DLManager.getInstance().DownloadWidget( dlTitle , apkname , pkgname , customID , WidgetDownload.getInstance() );
					cleanup();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			removeDialog( DIALOG_DOWNLOAD_WIDGET );
			bDialogShow = false;
		}
	}
	
	private class PageDelete
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final View layout = View.inflate( iLoongLauncher.this , RR.layout.circle_pop_delall , null );
			TextView tv = (TextView)layout.findViewById( RR.id.label );
			tv.setText( RR.string.delete_page );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
				tv.setTextColor( Color.BLACK );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			builder.setIcon( 0 );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					cleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.circle_cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					cleanup();
					d3dListener.exeDeletePage();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
					mWaitingForResult = true;
					PageDeleteDialogShow = true;
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			PageDeleteDialogShow = false;
			bDialogShow = false;
			dismissDialog( DIALOG_DELETE_PAGE );
		}
	}
	
	public void DismissPageDeleteDialog()
	{
		if( PageDeleteDialogShow == true )
		{
			PageDeleteDialogShow = false;
			bDialogShow = false;
			mCurrentDialogId = 0;
			dismissDialog( DIALOG_DELETE_PAGE );
		}
	}
	
	public void DismissShortcutDialog()
	{
		if( ShortcutDialogShow == true )
		{
			ShortcutDialogShow = false;
			bDialogShow = false;
			mCurrentDialogId = 0;
			dismissDialog( DIALOG_CREATE_SHORTCUT );
		}
	}
	
	// teapotXu added start for dismissed the dialog when home-key pressed
	public void DismissLauncherDialogs()
	{
		if( bDialogShow && mCurrentDialogId > 0 )
		{
			if( mCurrentDialogId == DIALOG_MAINMENU_BG )
			{
				AppHost3D.mainmenuBgAlpha = AppHost3D.old_mainmenuBgAlpha;
				prefs.edit().putInt( "mainmenu_bg_alpha" , AppHost3D.mainmenuBgAlpha ).commit();
			}
			this.dismissDialog( mCurrentDialogId );
		}
		bDialogShow = false;
		mCurrentDialogId = 0;
	}
	
	// teapotXu add end
	public void deleteWidget(
			Widget widget )
	{
		xWorkspace.removeWidget( widget , widget.getItemInfo().screen );
		mAppWidgetHost.deleteAppWidgetId( widget.getItemInfo().appWidgetId );
		Log.e( "delete widget " , "widgetId:" + widget.name );
	}
	
	public Desktop3DListener getD3dListener()
	{
		return d3dListener;
	}
	
	public boolean isWorkspaceVisible()
	{
		if( d3dListener == null )
			return true;
		return d3dListener.isWorkspaceVisible();
	}
	
	@Override
	public void setActionListener()
	{
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_DESKTOP_SETTINGS , this );
		SetupMenuActions.getInstance().RegisterListener( ActionSetting.ACTION_INSTALL_HELP , this );
	}
	
	@Override
	public void OnAction(
			int actionid ,
			Bundle bundle )
	{
		if( actionid == ActionSetting.ACTION_DESKTOP_SETTINGS )
		{
			if( bundle.containsKey( "vibrator" ) )
			{
				vibratorAble = bundle.getInt( "vibrator" );
			}
			if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) ) )
			{
				int effect = bundle.getInt( SetupMenu.getKey( RR.string.setting_key_desktopeffects ) , 0 );
				PubProviderHelper.addOrUpdateValue( "effect" , SetupMenu.getKey( RR.string.setting_key_desktopeffects ) , String.valueOf( effect ) );
				SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
			}
			if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_appeffects ) ) )
			{
				int effect = bundle.getInt( SetupMenu.getKey( RR.string.setting_key_appeffects ) , 0 );
				PubProviderHelper.addOrUpdateValue( "effect" , SetupMenu.getKey( RR.string.setting_key_appeffects ) , String.valueOf( effect ) );
				SetupMenu.getContext().sendBroadcast( new Intent( "com.coco.effect.action.DEFAULT_EFFECT_CHANGED" ) );
			}
			/****************** added by zhenNan.ye begin *******************/
			if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_particle ) ) )
			{
				ParticleManager.particleManagerEnable = bundle.getBoolean( SetupMenu.getKey( RR.string.setting_key_particle ) );
			}
			/****************** added by zhenNan.ye end *******************/
			// zjp
			if( DefaultLayout.enable_edit_mode_function && bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_edit_mode ) ) )
			{
				Root3D.IsProhibiteditMode = bundle.getBoolean( SetupMenu.getKey( RR.string.setting_key_edit_mode ) );
			}
			// xujin 完成设置，添加或者移除camera页
			if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_camera_page ) ) )
			{
				DefaultLayout.enable_camera = bundle.getBoolean( SetupMenu.getKey( RR.string.setting_key_camera_page ) );
				final Workspace3D workspace = d3dListener.getWorkspace3D();
				if( DefaultLayout.enable_camera )
				{
					if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
					{
						postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								synchronized( this )
								{
									workspace.addCameraView();
								}
							}
						} );
					}
				}
				else
				{
					workspace.removeCameraViewAndRelease();
				}
			}
			// jbc add
			if( bundle.containsKey( SetupMenu.getKey( RR.string.setting_key_music_page ) ) )
			{
				DefaultLayout.show_music_page = bundle.getBoolean( SetupMenu.getKey( RR.string.setting_key_music_page ) );
				final Workspace3D workspace = d3dListener.getWorkspace3D();
				if( DefaultLayout.show_music_page )
				{
					if( Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.NormalMode )
					{
						postRunnable( new Runnable() {
							
							@Override
							public void run()
							{
								synchronized( this )
								{
									workspace.addMusicView();
								}
							}
						} );
					}
				}
				else
				{
					workspace.removeMusicViewAndRelease();
				}
			}
		}
		else if( actionid == ActionSetting.ACTION_INSTALL_HELP )
		{
			showIntroductionAgain = true;
			showIntroduction();
		}
		// teapotXu add start
		if( bundle.containsKey( SetupMenu.getKey( RR.string.screen_scroll_circle ) ) )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
			boolean screen_scrollcircle_state = bundle.getBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) );
			prefs.edit().putBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) , screen_scrollcircle_state ).commit();
		}
		if( bundle.containsKey( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) ) )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
			boolean wallpaper_mv_state = bundle.getBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) );
			prefs.edit().putBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , wallpaper_mv_state ).commit();
		}
		// teapotXu add end
	}
	
	public void showIntroduction()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );
		if( !DefaultLayout.show_introduction )
		{
			showIntroduction = false;
		}
		if( showIntroduction )
		{
			d3dListener.showIntroduction();
		}
		else
		{
			showIntroduction = false;
			mModel.initialize( d3dListener );// add to model callbacks to bind icon,
			showProgressDialog();
			toStartLoader();
		}
	}
	
	public void dismissIntroduction()
	{
		if( !showIntroductionAgain )
		{
			mModel.initialize( d3dListener );// add to model callbacks to bind icon,
			SendMsgToAndroid.sendCreateProgressDialogMsg();
			toStartLoader();
		}
		else
			showIntroductionAgain = false;
		introductionShown = false;
		showIntroduction = false;
		d3dListener.dismissIntroduction();
		// }
		// });
	}
	
	public void add3DFolder()
	{
		ItemInfo info = null;
		Workspace3D workspace = d3dListener.getWorkspace3D();
		FolderIcon3D fo = (FolderIcon3D)Desktop3DListener.folder3DHost.getWidget3D();
		if( fo == null )
			return;
		info = fo.getItemInfo();
		info.x = (int)( clickPoint.x - fo.width / 2 );
		info.y = (int)( clickPoint.y - fo.height / 2 );
		info.screen = workspace.getCurrentScreen();
		if( workspace.addInScreen( fo , info.screen , info.x , info.y , false ) )
		{
			FolderInfo mFolderInfo = fo.mInfo;
			mFolderInfo.title = R3D.folder3D_name;
			LauncherModel.updateItemInDatabase( iLoongLauncher.getInstance() , mFolderInfo );
			// zhujieping add
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				fo.mFolderMIUI3D.setEditText( R3D.folder3D_name );
			else
				fo.mFolder.setEditText( R3D.folder3D_name );
		}
		else
		{
			LauncherModel.deleteItemFromDatabase( this , info );
			sFolders.remove( info.id );
		}
		// Root3D.addOrMoveDB(info);
	}
	
	// teapotXu add start for add new folder in top-trash bar
	public void add3DFolder(
			ArrayList<View3D> list )
	{
		ArrayList<View3D> iconList = new ArrayList<View3D>();
		ItemInfo info = null;
		Workspace3D workspace = d3dListener.getWorkspace3D();
		FolderIcon3D fo = (FolderIcon3D)Desktop3DListener.folder3DHost.getWidget3D();
		if( fo == null )
			return;
		Icon3D icon = (Icon3D)list.get( 0 );
		ItemInfo itemInfo = icon.getItemInfo();
		float desX = -1 , desY = -1;
		if( itemInfo.container == ShortcutInfo.NO_ID )
		{
		}
		else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
		{
			desX = itemInfo.x;
			desY = itemInfo.y;
		}
		else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
		{
			desX = itemInfo.x;
			desY = itemInfo.y;
		}
		else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
		}
		else if( itemInfo.container > 0 )
		{
		}
		info = fo.getItemInfo();
		info.x = (int)( desX - fo.width / 2 );
		info.y = (int)( desY - fo.height / 2 );
		info.screen = workspace.getCurrentScreen();
		if( ( workspace.getCurrentView() instanceof CellLayout3D ) && workspace.addInScreen( fo , info.screen , info.x , info.y , false ) )
		{
			FolderInfo mFolderInfo = fo.mInfo;
			mFolderInfo.title = R3D.folder3D_name;
			Root3D.updateItemInDatabase( mFolderInfo );
			FolderMIUI3D miuiFolder;
			Folder3D folder;
			fo.addFolderNode( list , true );
			{
				// zhujieping add
				if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
				{
					fo.getMIUI3DFolder().setEditText( R3D.folder3D_name );
				}
				else
				{
					folder = fo.getFolder();
					folder.setEditText( R3D.folder3D_name );
				}
			}
		}
		else
		{
			Root3D.deleteFromDB( info );
			sFolders.remove( info.id );
			getD3dListener().getRoot().dropListBacktoOrig( list );
		}
		// Root3D.addOrMoveDB(info);
	}
	
	public void addNewFolder(
			ArrayList<View3D> list )
	{
		if( list.size() <= 0 )
		{
			return;
		}
		ArrayList<View3D> iconList = new ArrayList<View3D>();
		ItemInfo info = null;
		Workspace3D workspace = d3dListener.getWorkspace3D();
		FolderIcon3D folderIcon = (FolderIcon3D)Desktop3DListener.folder3DHost.getWidget3D();
		if( folderIcon == null )
			return;
		Icon3D icon = (Icon3D)list.get( 0 );
		ItemInfo itemInfo = icon.getItemInfo();
		float desX = -1 , desY = -1;
		if( itemInfo.container == ShortcutInfo.NO_ID )
		{
		}
		else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_DESKTOP )
		{
			desX = itemInfo.x;
			desY = itemInfo.y;
		}
		else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT )
		{
			desX = itemInfo.x;
			desY = itemInfo.y;
		}
		else if( itemInfo.container == LauncherSettings.Favorites.CONTAINER_APPLIST )
		{
		}
		else if( itemInfo.container > 0 )
		{
		}
		info = folderIcon.getItemInfo();
		info.x = (int)( desX - folderIcon.width / 2 );
		info.y = (int)( desY - folderIcon.height / 2 );
		info.screen = workspace.getCurrentScreen();
		if( ( workspace.getCurrentView() instanceof CellLayout3D ) && workspace.addInScreen( folderIcon , info.screen , info.x , info.y , true ) )
		{
			FolderInfo mFolderInfo = folderIcon.mInfo;
			Root3D.updateItemInDatabase( mFolderInfo );
			FolderMIUI3D miuiFolder;
			Folder3D folder;
			for( View3D v : list )
			{
				Icon3D ic = (Icon3D)v;
				View3D shortcut = Workspace3D.createShortcut( (ShortcutInfo)ic.getItemInfo() , true );
				iconList.add( shortcut );
			}
			folderIcon.addFolderNode( iconList , false );
			// zhujieping add
			if( ThemeManager.getInstance().getBoolean( "miui_v5_folder" ) || DefaultLayout.miui_v5_folder )
			{
				folderIcon.getMIUI3DFolder().setEditText( ApplicationListHost.folderName );
			}
			else
			{
				folder = folderIcon.getFolder();
				folder.setEditText( ApplicationListHost.folderName );
			}
		}
		else
		{
			Root3D.deleteFromDB( info );
			sFolders.remove( info.id );
			getD3dListener().getRoot().dropListBacktoOrig( list );
		}
		// Root3D.addOrMoveDB(info);
	}
	
	// teapotXu add end
	public void add3DContact()
	{
		Icon3D icon = (Icon3D)Desktop3DListener.contact3DHost.getWidget3D();
		MobclickAgent.onEvent( iLoongLauncher.getInstance() , "AddContacts" );
		Workspace3D workspace = d3dListener.getWorkspace3D();
		if( icon != null )
		{
			ItemInfo info = icon.getItemInfo();
			info.x = (int)( clickPoint.x - icon.width / 2 );
			info.y = (int)( clickPoint.y - icon.height / 2 );
			info.screen = workspace.getCurrentScreen();
			//Desktop3DListener.root.addFlyView( icon );//带飞入动画添加快捷联系人
			workspace.addInScreen( icon , info.screen , info.x , info.y , false );
			// Root3D.addOrMoveDB(info);
		}
	}
	
	public int[] getSpanForWidget(
			AppWidgetProviderInfo info ,
			int[] spanXY )
	{
		int sysVersion = Integer.parseInt( VERSION.SDK );
		if( sysVersion < 11 )
		{
			if( info != null )
			{
				// Converting complex to dp.
				info.minWidth = TypedValue.complexToDimensionPixelSize( info.minWidth , this.getApplicationContext().getResources().getDisplayMetrics() );
				info.minHeight = TypedValue.complexToDimensionPixelSize( info.minHeight , this.getApplicationContext().getResources().getDisplayMetrics() );
			}
		}
		return getSpanForWidget( info.provider , info.minWidth , info.minHeight , spanXY );
	}
	
	@SuppressLint( "NewApi" )
	public int[] getSpanForWidget(
			ComponentName component ,
			int minWidth ,
			int minHeight ,
			int[] spanXY )
	{
		int requiredWidth = minWidth;
		int requiredHeight = minHeight;
		if( VERSION.SDK_INT >= 15 )
		{
			Rect padding = AppWidgetHostView.getDefaultPaddingForWidget( this , component , null );
			requiredWidth = minWidth + padding.left + padding.right;
			requiredHeight = minHeight + padding.top + padding.bottom;
		}
		return CellLayout3D.rectToCell( getResources() , requiredWidth , requiredHeight , spanXY );
	}
	
	public int getCurrentScreen()
	{
		if( d3dListener != null )
			return d3dListener.getCurrentScreen();
		return 0;
	}
	
	public int getScreenCount()
	{
		if( d3dListener != null )
			return d3dListener.getScreenCount();
		return 0;
	}
	
	private void registerShakeNewspage()
	{
		if( DefaultLayout.enable_news && DefaultLayout.show_newspage_with_shake )
		{
			if( checkSensorSupported() )
			{
				if( mShaker == null )
				{
					mShaker = new ShakeListener( this );
				}
				mShaker.setOnShakeListener( new NewspageShakeListener() );
				mShaker.resume();
			}
		}
		else
		{
			if( mShaker != null )
			{
				mShaker.pause();
			}
		}
	}
	
	private void registerShakeWallpapers()
	{
		boolean enable_shake_wallpaper = !DefaultLayout.disable_shake_wallpaper;
		boolean enable_shake_change_theme = !DefaultLayout.disable_shake_change_theme;
		// this.getResources().getBoolean(RR.bool.enable_shake_wallpaper);
		boolean cbx_shake_wallpaper = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean(
				this.getResources().getString( RR.string.setting_key_shake_wallpaper ) ,
				DefaultLayout.default_open_shake_wallpaper );
		boolean cbx_shake_changed_theme = PreferenceManager.getDefaultSharedPreferences( SetupMenu.getContext() ).getBoolean(
				this.getResources().getString( RR.string.setting_key_shake_theme ) ,
				DefaultLayout.default_open_shake_theme );
		if( enable_shake_wallpaper || enable_shake_change_theme )
		{
			if( ( enable_shake_wallpaper && cbx_shake_wallpaper ) || ( enable_shake_change_theme && cbx_shake_changed_theme ) )
			{
				if( mShaker == null )
				{
					mShaker = new ShakeListener( this );
					if( checkSensorSupported() )
					{
						if( cbx_shake_wallpaper )
						{
							mShaker.setOnShakeListener( new WallpaperShakeListener( this ) );
						}
						if( cbx_shake_changed_theme )
						{
							mShaker.setOnShakeListener( new ThemeShakeListener( this ) );
						}
						mShaker.resume();
					}
				}
				else
				{
					if( checkSensorSupported() )
					{
						if( enable_shake_wallpaper && cbx_shake_wallpaper )
						{
							mShaker.setOnShakeListener( new WallpaperShakeListener( this ) );
						}
						if( enable_shake_change_theme && cbx_shake_changed_theme )
						{
							mShaker.setOnShakeListener( new ThemeShakeListener( this ) );
						}
						mShaker.resume();
					}
				}
			}
			else
			{
				if( mShaker != null )
				{
					mShaker.pause();
				}
			}
		}
	}
	
	public boolean checkSensorSupported()
	{
		if( mShaker == null )
		{
			mShaker = new ShakeListener( this );
		}
		boolean supported = mShaker.checkSensorSupported();
		return supported;
	}
	
	public void cleaWidgetStatus(
			int page )
	{
		CellLayout cell = (CellLayout)xWorkspace.getChildAt( page );
		View view = null;
		if( cell != null )
		{
			for( int i = 0 ; i < cell.getChildCount() ; i++ )
			{
				view = cell.getChildAt( i );
				int index = MotionEventPool.get( 0 , 0 , MotionEvent.ACTION_CANCEL , 0 , 0 );
				Messenger.sendMsg( Messenger.EVENT_UPDATE_SYS_WIDGET , view , index , 0 );
			}
		}
	}
	
	@Override
	public Desktop3D getDesktop()
	{
		return Desktop3DListener.d3d;
	}
	
	@Override
	public View getGLView()
	{
		return glView;
	}
	
	@Override
	public boolean isWorkspace3DTouchable()
	{
		if( d3dListener == null )
			return false;
		Workspace3D workspace = d3dListener.getWorkspace3D();
		if( workspace == null )
			return false;
		if( !workspace.touchable )
			return false;
		if( workspace.color.a != 1 )
			return false;
		if( workspace.scaleX != 1 )
			return false;
		if( workspace.getX() != 0 )
			return false;
		if( workspace.getY() != 0 )
			return false;
		if( DefaultLayout.enable_workspace_miui_edit_mode && Workspace3D.WorkspaceStatus == WorkspaceStatusEnum.EditMode )
			return false;
		if( workspace.curIsMusicView() )
			return false;
		return true;
	}
	
	@Override
	public void setCurrentFocusX(
			int arg0 )
	{
		CellLayout3D.currentX = arg0;
	}
	
	// xiatian add start //New Requirement 20130507
	public void startChangJingZhuoMian()
	{
		if( DefaultLayout.enable_SceneBox )
		{
			Intent intent = new Intent();
			intent.setComponent( new ComponentName( this , SCENE_BOX_MAIN_ACTIVITY ) );
			startActivity( intent );
		}
		// else
		// {
		// Toast.makeText(
		// iLoongLauncher.getInstance(),
		// "DefaultLayout.enable_SceneBox==false\n--\n--没有安装场景盒子，do nothing",
		// Toast.LENGTH_SHORT
		// )
		// .show();
		// }
	}
	
	public boolean startWallpaperBox()
	{
		if( DefaultLayout.enable_WallpaperBox )
		{
			Intent intent = new Intent();
			intent.setComponent( new ComponentName( this , WALLPAPER_BOX_MAIN_ACTIVITY ) );
			startActivity( intent );
			return true;
		}
		return false;
	}
	
	// xiatian add end
	@Override
	public boolean hasCancelDialog()
	{
		return dialogCancelled;
	}
	
	// xiatian add start //mainmenu_background_alpha_progress
	private class MainmenuBg
	{
		
		@SuppressLint( "NewApi" )
		Dialog createDialog()
		{
			final ViewGroup layout = (ViewGroup)LayoutInflater.from( iLoongLauncher.getInstance() ).inflate( RR.layout.dialog_mainmenu_bg , null );
			final SeekBar bar = (SeekBar)layout.findViewById( RR.id.seekbar );
			bar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(
						SeekBar seekBar )
				{
				}
				
				@Override
				public void onStartTrackingTouch(
						SeekBar seekBar )
				{
				}
				
				@Override
				public void onProgressChanged(
						SeekBar seekBar ,
						int progress ,
						boolean fromUser )
				{
					if( seekBar.equals( bar ) )
					{
						AppHost3D.mainmenuBgAlpha = progress;
						Gdx.graphics.requestRendering();
					}
				}
			} );
			bar.setThumbOffset( 20 );
			bar.setMax( 100 );
			bar.setProgress( AppHost3D.mainmenuBgAlpha );
			AlertDialog.Builder builder = null;
			int androidSDKVersion = Integer.parseInt( VERSION.SDK );
			if( DefaultLayout.desktop_setting_style_white && androidSDKVersion >= 11 )
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this , AlertDialog.THEME_HOLO_LIGHT );
			}
			else
			{
				builder = new AlertDialog.Builder( iLoongLauncher.this );
			}
			if( androidSDKVersion >= 11 )
			{
				bar.setPadding( R3D.android4_seekbar_padding_left , R3D.android4_seekbar_padding_top , R3D.android4_seekbar_padding_right , R3D.android4_seekbar_padding_bottom );
			}
			builder.setIcon( 0 );
			builder.setTitle( getString( RR.string.mainmenu_bg_dialog_title ) );
			builder.setCancelable( true );
			builder.setOnCancelListener( new Dialog.OnCancelListener() {
				
				public void onCancel(
						DialogInterface dialog )
				{
					AppHost3D.mainmenuBgAlpha = AppHost3D.old_mainmenuBgAlpha;
					Gdx.graphics.requestRendering();
					cleanup();
				}
			} );
			builder.setNegativeButton( getString( RR.string.circle_cancel_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					AppHost3D.mainmenuBgAlpha = AppHost3D.old_mainmenuBgAlpha;
					Gdx.graphics.requestRendering();
					cleanup();
				}
			} );
			builder.setPositiveButton( getString( RR.string.circle_ok_action ) , new Dialog.OnClickListener() {
				
				public void onClick(
						DialogInterface dialog ,
						int which )
				{
					cleanup();
				}
			} );
			builder.setView( layout );
			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener( new DialogInterface.OnShowListener() {
				
				public void onShow(
						DialogInterface dialog )
				{
					AppHost3D.old_mainmenuBgAlpha = AppHost3D.mainmenuBgAlpha;
					bar.setProgress( AppHost3D.mainmenuBgAlpha );
				}
			} );
			return dialog;
		}
		
		private void cleanup()
		{
			dismissDialog( DIALOG_MAINMENU_BG );
			bDialogShow = false;
			prefs.edit().putInt( "mainmenu_bg_alpha" , AppHost3D.mainmenuBgAlpha ).commit();
		}
	}
	
	// xiatian add end
	public boolean getFirstRun()
	{
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		int icon_size_type = DefaultLayout.getDefaultIconSizeType();
		if( icon_size_type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			return prefs.getBoolean( "first_run_big_icon" , true );
		}
		else if( icon_size_type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			return prefs.getBoolean( "first_run_small_icon" , true );
		}
		else
		{
			return prefs.getBoolean( "first_run" , true );
		}
	}
	
	public boolean getFirstRunByType(
			int type )
	{
		SharedPreferences prefs = iLoongLauncher.getInstance().getSharedPreferences( "launcher" , Activity.MODE_PRIVATE );
		if( type == 0 && DefaultLayout.show_icon_size_different_layout )
		{
			return prefs.getBoolean( "first_run_big_icon" , true );
		}
		else if( type == 2 && DefaultLayout.show_icon_size_different_layout )
		{
			return prefs.getBoolean( "first_run_small_icon" , true );
		}
		else
		{
			return prefs.getBoolean( "first_run" , true );
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(
			Menu menu )
	{
		Log.v( "jbc" , "OptionsMenu onCreateOptionsMenu" );
		if( !DefaultLayout.setupmenu_by_system && !RR.net_version )
		{
			return false;
		}
		if( !Desktop3DListener.bCreatDone )
			return false;
		// setIconEnable(menu, true);
		/*
		 * add()方法的四个参数，依次是： 1、组别，如果不分组的话就写Menu.NONE,
		 * 2、Id，这个很重要，Android根据这个Id来确定不同的菜单 3、顺序，那个菜单现在在前面由这个参数的大小决定
		 * 4、文本，菜单的显示文本
		 */
		// setIcon()方法为菜单设置图标，这里使用的是系统自带的图标，以
		// android.R开头的资源是系统提供的，我们自己提供的资源是以R开头的
		menu.clear();
		addDesktopMenu( menu );
		addAppMenu( menu );
		// 返回true则显示，返回false则不显示
		return true;
	}
	
	// 菜单添加图标是否有效。4.0系统默认为false
	private void setIconEnable(
			Menu menu ,
			boolean enable )
	{
		try
		{
			Class<?> clazz = Class.forName( "com.android.internal.view.menu.MenuBuilder" );
			Method m = clazz.getDeclaredMethod( "setOptionalIconsVisible" , boolean.class );
			m.setAccessible( true );
			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke( menu , enable );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(
			MenuItem item )
	{
		Log.v( "jbc" , "OptionsMenu onOptionsItemSelected id:" + item.getItemId() );
		if( item.getGroupId() == MENU_GROUP_DESKTOP )
		{
			SetupMenuActions.getInstance().Handle( item.getItemId() );
		}
		else if( item.getGroupId() == MENU_GROUP_MAINMENU )
		{
			switch( item.getItemId() )
			{
				case 100:
					SendMsgToAndroid.sendShowMainmenuBgDialogMsg();
					break;
				case 101:
					SendMsgToAndroid.sendShowAppEffectDialogMsg();
					break;
				case 102:
					SendMsgToAndroid.sendShowSortDialogMsg( AppHost3D.appList.sortId , SORT_ORIGIN_APPLIST );
					break;
				case 103:
					AppHost3D.appList.setMode( AppList3D.APPLIST_MODE_HIDE );
					break;
				case 104:
				case 105:
					AppHost3D.appList.setMode( AppList3D.APPLIST_MODE_UNINSTALL );
					break;
			}
		}
		// 返回false则正常显示，返回true则被消费掉
		return false;
	}
	
	@Override
	public void onOptionsMenuClosed(
			Menu menu )
	{
		Log.v( "jbc" , "OptionsMenu onOptionsMenuClosed" );
		optionsMenuOpen = false;
		Log.v( "jbc" , "OptionsMenu onOptionsMenuClosed optionsMenuOpen=" + optionsMenuOpen );
	}
	
	@Override
	public boolean onMenuOpened(
			int featureId ,
			Menu menu )
	{
		optionsMenuOpen = true;
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(
			Menu menu )
	{
		// 选项菜单显示之前onPrepareOptionsMenu方法会被调用，可以用此方法来根据打当时的情况调整菜单
		// 注意：return false表示用户把点击menu的动作给消费了，不让系统处理菜单
		Log.v( "jbc" , "OptionsMenu onPrepareOptionsMenu" );
		if( !DefaultLayout.setupmenu_by_system )
		{
			return false;
		}
		if( !Desktop3DListener.bCreatDone )
			return false;
		if( showIntroduction )
			return false;
		if( Root3D.startWorkspaceAndAppTween )
		{
			return false;
		}
		if( d3dListener.getAppList().isVisible() && !d3dListener.getWorkspace3D().isVisible() )
		{// launcher主菜单
			if( AppHost3D.appList.getXScale() != 0 || !AppHost3D.appList.canPopMenu() )
			{
				return false;
			}
			menu.setGroupVisible( MENU_GROUP_DESKTOP , false );
			menu.setGroupVisible( MENU_GROUP_MAINMENU , true );
		}
		else if( !d3dListener.getAppList().isVisible() && d3dListener.getWorkspace3D().isVisible() )
		{// launcher
			// idle
			boolean canOpenSetupMenu = true;
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			if( curCellLayout != null )
			{
				for( int i = 0 ; i < curCellLayout.getChildCount() ; i++ )
				{
					if( curCellLayout.getChildAt( i ) instanceof Widget3D )
					{
						Widget3D curWidget3D = (Widget3D)curCellLayout.getChildAt( i );
						if( curWidget3D.isOpened() )
						{
							canOpenSetupMenu = false;
							break;
						}
					}
				}
			}
			if( canOpenSetupMenu )
			{
				// xiatian add start //EffectPreview
				if( DefaultLayout.enable_effect_preview )
				{
					Root3D mRoot3D = getD3dListener().getRoot();
					if( mRoot3D.isWorkspaceEffectPreviewMode() )
						return false;
				}
				// xiatian add end
				if( DefaultLayout.diable_enter_applist_when_takein_mode && Workspace3D.isHideAll )
				{
					return false;
				}
			}
			else
			{
				return false;
			}
			menu.setGroupVisible( MENU_GROUP_DESKTOP , true );
			menu.setGroupVisible( MENU_GROUP_MAINMENU , false );
			// menu.removeGroup(MENU_GROUP_MAINMENU);
			// addDesktopMenu(menu);
		}
		else
		{// 其它如pageEdit等
			return false;
		}
		Log.v( "jbc" , "OptionsMenu onPrepareOptionsMenu return true" );
		return true;
	}
	
	private void addDesktopMenu(
			Menu menu )
	{
		// menu.clear();
		for( int i = 0 ; i < desktopMenuList.size() ; i++ )
		{
			menu.add( MENU_GROUP_DESKTOP , desktopMenuList.get( i ).id , i , desktopMenuList.get( i ).name ).setIcon( desktopMenuList.get( i ).icondrawable );
		}
	}
	
	private void addAppMenu(
			Menu menu )
	{
		// menu.clear();
		for( int i = 0 ; i < appMenuList.size() ; i++ )
		{
			menu.add( MENU_GROUP_MAINMENU , appMenuList.get( i ).id , i , appMenuList.get( i ).name ).setIcon( appMenuList.get( i ).icondrawable );
		}
	}
	
	public void loadSetupMenuXml()
	{
		try
		{
			String[] menus;
			if( DefaultLayout.setupmenu_yitong )
			{
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenu_eton );
			}
			else
			{
				menus = iLoongLauncher.getInstance().getResources().getStringArray( RR.array.setupMenuAll );
			}
			ArrayList<String> menulistall = new ArrayList<String>();
			ArrayList<String> menulistallid = new ArrayList<String>();
			for( int i = 0 ; i < menus.length ; i++ )
			{
				menulistall.add( menus[i] );
				String[] tmp = menus[i].split( "," );
				menulistallid.add( tmp[0] );
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( DefaultLayout.setupmenu_show_theme )
				{
					// teapotXu add start for doov special customization
					if( DefaultLayout.enable_doov_spec_customization == true )
					{
						if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_WALLPAPER )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					else
					{
						if( !DefaultLayout.setupmenu_idle_sofwareManager_shown && Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SOFTWARE_MANAGEMENT )
						{
							menulistall.remove( i );
							menulistallid.remove( i );
						}
					}
					// teapotXu add end
				}
				else
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_THEME )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
					}
				}
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_LOCKER_SETTING )
				{
					menulistall.remove( i );
					menulistallid.remove( i );
					break;
				}
			}
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( DefaultLayout.hide_desktop_setup )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_DESKTOP_SETTINGS )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
					}
				}
			}
			// teapotXu add start:
			ArrayList<String> remove_menulist = new ArrayList<String>();
			ArrayList<String> remove_menulistallid = new ArrayList<String>();
			for( int i = 0 ; i < menulistall.size() ; i++ )
			{
				if( ( !DefaultLayout.setupmenu_idle_wallpaper_shown && Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_WALLPAPER ) || ( !DefaultLayout.setupmenu_idle_systemWidget_shown && Integer
						.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SYSTEM_PLUG ) || ( !DefaultLayout.setupmenu_idle_systemSettings_shown && Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SYSTEM_SETTINGS ) )
				{// zjp
					remove_menulist.add( menulistall.get( i ) );
					remove_menulistallid.add( menulistallid.get( i ) );
				}
			}
			if( remove_menulist.size() > 0 )
				menulistall.removeAll( remove_menulist );
			if( remove_menulistallid.size() > 0 )
				menulistallid.removeAll( remove_menulistallid );
			// teapotXu add end
			if( DefaultLayout.desk_menu_change_SystemWidget_to_OnKeyLove )
			{
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_SYSTEM_PLUG )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
			}
			else
			{
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_OnKeyLove_1 )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
			}
			if( !DefaultLayout.desk_menu_add_OnKeyLove_item )
			{
				for( int i = 0 ; i < menulistall.size() ; i++ )
				{
					if( Integer.parseInt( menulistallid.get( i ) ) == ActionSetting.ACTION_OnKeyLove )
					{
						menulistall.remove( i );
						menulistallid.remove( i );
						break;
					}
				}
			}
			// if (DefaultLayout.popmenu_style == POPMENU_STYLE_ANDROID4) {
			// mrows = menulistall.size() - 1;
			// mcolumns = 1;
			// }
			// SetupTabMenu menu = new SetupTabMenu();
			// String[] menuAttrs = menulistall.get(0).split(",");
			// menu.id = Integer.parseInt(menuAttrs[0]);
			// Field f = (Field) RR.getStringClass()
			// .getDeclaredField(menuAttrs[1]);
			// menu.name = R3D.getString(f.getInt(RR.getStringClass()));
			// smenu = menu;
			// mTabMenus.add(menu);
			// ArrayList<SetupMenuItem> menulist = new
			// ArrayList<SetupMenuItem>();
			// mMenuItems.put(Integer.valueOf(menu.id), menulist);
			for( int i = 1 ; i < menulistall.size() ; i++ )
			{
				// ArrayList<SetupMenuItem> menuitems = mMenuItems.get(Integer
				// .valueOf(smenu.id));
				if( desktopMenuList != null )
				{
					String[] tmp = menulistall.get( i ).split( "," );
					SysMenuItem smi = new SysMenuItem();
					// SetupMenuItem mi = new SetupMenuItem();
					// mi.page = smenu.id;
					// mi.id = Integer.parseInt(tmp[0]);
					smi.id = Integer.parseInt( tmp[0] );
					Field tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
					if( DefaultLayout.appbar_widgets_special_name == true )
					{
						if( tmp[1] != null && tmp[0] != null && ( tmp[0].equals( "1001" ) ) && ( tmp[1].equals( "system_widget" ) ) )
						{
							tmp[1] = "appbar_tab_widget_ex";
							tmpF = (Field)RR.getStringClass().getDeclaredField( tmp[1] );
						}
					}
					smi.name = R3D.getString( tmpF.getInt( RR.getStringClass() ) );
					// Log.v("","setup3D miname is " + mi.name);
					smi.icon = SetupMenu.SETUPMENU_ORIGINAL_FOLDERNAME + tmp[2] + ".png";
					smi.iconbmp = ThemeManager.getInstance().getBitmap( ( smi.icon ) );
					smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
					desktopMenuList.add( smi );
				}
			}
		}
		catch( NumberFormatException e )
		{
			e.printStackTrace();
		}
		catch( NotFoundException e )
		{
			e.printStackTrace();
		}
		catch( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch( NoSuchFieldException e )
		{
			e.printStackTrace();
		}
		catch( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}
	
	public void initAppMenu()
	{
		SysMenuItem smi = new SysMenuItem();
		if( DefaultLayout.mainmenu_background_alpha_progress && !DefaultLayout.mainmenu_background_translucent )
		{
			// 背景透明度
			smi.id = 100;
			smi.name = R3D.getString( RR.string.mainmenu_bg_alpha );
			smi.icon = "theme/pack_source/app-background-alpha-button.png";
			smi.iconbmp = ThemeManager.getInstance().getBitmap( smi.icon );
			smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
			appMenuList.add( smi );
		}
		// 切页特效
		smi = new SysMenuItem();
		smi.id = 101;
		smi.name = R3D.getString( RR.string.effect_icon );
		smi.icon = "theme/pack_source/app-effect-button.png";
		smi.iconbmp = ThemeManager.getInstance().getBitmap( smi.icon );
		smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
		appMenuList.add( smi );
		// 图标排序
		smi = new SysMenuItem();
		smi.id = 102;
		smi.name = R3D.getString( RR.string.sort_icon );
		smi.icon = "theme/pack_source/app-sort-button.png";
		smi.iconbmp = ThemeManager.getInstance().getBitmap( smi.icon );
		smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
		appMenuList.add( smi );
		// addItem(new TextureRegion(new
		// BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-sort-button.png"),true)),
		// R3D.getString(RR.string.sort_icon));
		// 隐藏图标
		smi = new SysMenuItem();
		smi.id = 103;
		smi.name = R3D.getString( RR.string.hide_icon );
		smi.icon = "theme/pack_source/app-hide-button.png";
		smi.iconbmp = ThemeManager.getInstance().getBitmap( smi.icon );
		smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
		appMenuList.add( smi );
		// addItem(new TextureRegion(new
		// BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-hide-button.png"),true)),
		// R3D.getString(RR.string.hide_icon));
		if( DefaultLayout.mainmenu_folder_function == true && DefaultLayout.mainmenu_edit_mode == true )
		{
			// 编辑模式
			smi = new SysMenuItem();
			smi.id = 104;
			smi.name = R3D.getString( RR.string.edit_mode );
			smi.icon = "theme/pack_source/app-edit-button.png";
			smi.iconbmp = ThemeManager.getInstance().getBitmap( smi.icon );
			smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
			appMenuList.add( smi );
			// addItem(new TextureRegion(new
			// BitmapTexture(ThemeManager.getInstance().getBitmap("theme/pack_source/app-edit-button.png"),true)),
			// R3D.getString(RR.string.edit_mode));
		}
		else
		{
			// 卸载应用
			smi = new SysMenuItem();
			smi.id = 105;
			smi.name = R3D.getString( RR.string.uninstall_app );
			smi.icon = "theme/pack_source/app-uninstall-button.png";
			smi.iconbmp = ThemeManager.getInstance().getBitmap( smi.icon );
			smi.icondrawable = new BitmapDrawable( iLoongLauncher.getInstance().getResources() , smi.iconbmp );
			appMenuList.add( smi );
		}
	}
	
	public static int getSceneMenu(
			String pkg )
	{
		int size = 0;
		Intent intent = new Intent( pkg , null );
		PackageManager pm = iLoongApplication.getInstance().getPackageManager();
		List<ResolveInfo> resolveInfoList = pm.queryIntentActivities( intent , PackageManager.GET_META_DATA );
		if( resolveInfoList != null )
		{
			size = resolveInfoList.size();
		}
		return size;
	}
	
	@Override
	public void onTrimMemory(
			int level )
	{
		super.onTrimMemory( level );
		if( level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE )
		{
			isTrimMemory = true;
			Log.v( "" , "isTrimMemory is low isTrimMemory is " + isTrimMemory );
		}
		else
		{
			isTrimMemory = false;
		}
	}
	
	//Jone start
	Dialog shareProgressDialog;
	Dialog customProcess;
	
	public void showShareProgress()
	{
		shareProgressDialog = new Dialog( this , RR.style.popProgressStyle );
		LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
		ProgressBar mProgress;
		final LayoutInflater inflater = LayoutInflater.from( this );
		View v = inflater.inflate( RR.layout.progress_spinner_dialog , null );
		mProgress = (ProgressBar)v.findViewById( RR.id.progressBar );
		Window window = shareProgressDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity( Gravity.LEFT | Gravity.TOP );
		lp.x = 100; // 新位置X坐标
		lp.y = 100; // 新位置Y坐标
		lp.width = 300; // 宽度
		lp.height = 300; // 高度
		lp.alpha = 0.7f; // 透明度
		window.setAttributes( lp );
		shareProgressDialog.setCancelable( false );
		shareProgressDialog.addContentView( v , params );
		shareProgressDialog.show();
	}
	
	public void cancelShareProgress()
	{
		if( shareProgressDialog != null )
		{
			Log.v( "Root3D " , "cancelShareProgress" );
			shareProgressDialog.cancel();
		}
	}
	
	public void showCustomProgress(
			int x ,
			int y )
	{
		Log.v( "Root3D " , "showShareProgress" );
		if( customProcess != null )
		{
			customProcess.cancel();
			customProcess = null;
		}
		customProcess = new Dialog( this , RR.style.popProgressStyle ); //RR.style.ShareProgressDialog );
		LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT );
		ProgressBar mProgress;
		final LayoutInflater inflater = LayoutInflater.from( this );
		View v = inflater.inflate( RR.layout.progress_pop_dialog , null );
		mProgress = (ProgressBar)v.findViewById( RR.id.progressBar );
		Window window = customProcess.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity( Gravity.LEFT | Gravity.TOP );
		lp.x = x; // 新位置X坐标
		lp.y = y; // 新位置Y坐标
		lp.width = 300; // 宽度
		lp.height = 300; // 高度
		lp.alpha = 1.0f; // 透明度
		//lp.dimAmount = 1.0f;
		//
		window.setAttributes( lp );
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		customProcess.setCancelable( true );
		customProcess.addContentView( v , params );
		customProcess.show();
	}
	
	public void cancelCustomProgress()
	{
		if( customProcess != null )
		{
			Log.v( "Root3D " , "customProcess" );
			customProcess.cancel();
		}
	}
	
	//Jone end
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
		DexClassLoader loader = new DexClassLoader( dexPath , dexOutputDir , libPath , iLoongApplication.getInstance().getClassLoader() );
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
	
	//xiatian add end
	public boolean isNewsInstalled()
	{
		try
		{
			Context NewsContext = iLoongLauncher.getInstance().createPackageContext( "com.inveno.newpiflow" , Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY );
		}
		catch( NameNotFoundException e )
		{
			return false;
		}
		return true;
	}
	
	//	public void fireupRecentApp()
	//	{
	//		if( DefaultLayout.WorkspaceActionGuide )
	//		{
	//			if( d3dListener.getDragLayer() != null && d3dListener.getDragLayer().isVisible() )
	//			{
	//				return;
	//			}
	//			if( ( d3dListener.root != null && d3dListener.root.folderOpened ) || ( Root3D.appHost != null && Root3D.appHost.folderOpened ) )
	//			{
	//				return;
	//			}
	//			if( DefaultLayout.enable_quick_search && d3dListener.root != null && d3dListener.root.qSearchGroup != null && d3dListener.root.qSearchGroup.isVisible() )
	//			{
	//				return;
	//			}
	//			if( iLoongLauncher.getInstance().getD3dListener() != null && iLoongLauncher.getInstance().getD3dListener().getRoot() != null )
	//			{
	//				FolderIcon3D folder = iLoongLauncher.getInstance().getD3dListener().getRoot().getOpenFolder();
	//				if( folder != null && folder.mInfo.opened )
	//				{
	//					return;
	//				}
	//			}
	//			final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
	//			if( !pref.getBoolean( "first_back_from_app" , false ) )
	//			{
	//				if( recentApp.size() > 0 )
	//				{
	//					//pref.edit().putBoolean( "first_back_from_app" , true ).commit();
	//					Log.v( TAG , "fire up an app..." );
	//					this.postRunnable( new Runnable() {
	//						
	//						@Override
	//						public void run()
	//						{
	//							//	pref.edit().putBoolean( "first_back_from_app" , true  ).commit();
	//							Workspace3D.getInstance().showRecentApplications();
	//						}
	//					} );
	//				}
	//			}
	//		}
	//	}
	class DesktopSettingsReceiver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(
				Context context ,
				Intent intent )
		{
			String action = intent.getAction();
			if( action.equals( "com.cooee.desktopsettings.system.vibratorchange" ) )
			{
				boolean vibratorstate = intent.getBooleanExtra( "vibratorstate" , true );
				vibratorAble = vibratorstate ? 1 : 0;
				SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				share.edit().putInt( "vibratorAble" , vibratorAble ).commit();
			}
			else if( action.equals( "com.cooee.desktopsettings.screen.screenchange" ) )
			{
				boolean wallpaper_mv_state = intent.getBooleanExtra( "wallpaperscrollstate" , true );
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				prefs.edit().putBoolean( SetupMenu.getKey( RR.string.desktop_wallpaper_mv ) , wallpaper_mv_state ).commit();
				boolean screenstate = intent.getBooleanExtra( "ScreenInfinitescrollstate" , true );
				DefaultLayout.workspace_npages_circle_scroll_config = !screenstate;
				SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				share.edit().putBoolean( SetupMenu.getKey( RR.string.screen_scroll_circle ) , screenstate ).commit();
			}
			else if( action.equals( "com.cooee.desktopsettings.drawer.drawerchange" ) )
			{
				boolean drawerscrollstate = intent.getBooleanExtra( "drawerscrollstate" , true );
				DefaultLayout.drawer_npages_circle_scroll_config = !drawerscrollstate;
				SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				share.edit().putBoolean( "drawer_npages_circle_scroll_config" , drawerscrollstate ).commit();
			}
			else if( action.equals( "com.cooee.desktopsettings.drawer.gridlinechange" ) )
			{
				int currenthang;
				int currentlie;
				if( Utils3D.getScreenDisplayMetricsHeight() >= 800 )
				{
					currenthang = intent.getIntExtra( "gridlinehang" , 4 );
					currentlie = intent.getIntExtra( "gridlinelie" , 5 );
				}
				else
				{
					currenthang = intent.getIntExtra( "gridlinehang" , 4 );
					currentlie = intent.getIntExtra( "gridlinelie" , 4 );
				}
				d3dListener.afainApp( currenthang , currentlie );
				SharedPreferences share = PreferenceManager.getDefaultSharedPreferences( iLoongLauncher.getInstance() );
				Editor edit = share.edit();
				edit.putInt( "savehang" , currenthang );
				edit.putInt( "savelie" , currentlie );
				edit.commit();
			}
			else if( action.equals( "com.cooee.desktopsettings.iconsizechange" ) )
			{
				int dest_icon_size_type = DefaultLayout.getDefaultIconSizeType();
				if( dest_icon_size_type != ConfigBase.icon_size_type )
				{
					iLoongLauncher.getInstance().postRunnable( new Runnable() {
						
						@Override
						public void run()
						{
							SetupMenuActions.getInstance().Handle( ActionSetting.ACTION_RESTART );
						}
					} );
				}
			}
		}
	}
	
	public static class CustomDialogInfo
	{
		
		public int x;
		public int y;
	}
	
	class AllScreenFrameLayout extends FrameLayout
	{
		
		public AllScreenFrameLayout(
				Context context )
		{
			super( context );
		}
		
		@Override
		public boolean dispatchTouchEvent(
				MotionEvent ev )
		{
			if( isShowNews )
			{
				return super.dispatchTouchEvent( ev );
			}
			else
			{
				return xWorkspace.dispatchTouchEvent( ev );
			}
		}
	}
	
	public void initNewsView()
	{
		if( newsView == null )
		{
			newsView = new NewsView( this );
		}
		if( newsScreenframeLayout == null )
		{
			newsScreenframeLayout = new AllScreenFrameLayout( this );
		}
		newsScreenframeLayout.setLayoutParams( new LayoutParams( UtilsBase.getScreenWidth() , UtilsBase.getScreenHeight() ) );
		newsScreenframeLayout.addView( newsView );
		addContentView( newsScreenframeLayout , new LayoutParams( UtilsBase.getScreenWidth() , UtilsBase.getScreenHeight() ) );
	}
	
	private final ContentObserver mScreenBrightnessObserver = new ScreenBrightnessObserver();
}
