package com.iLoong.launcher.desktop;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
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
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.IMTKWidget;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.FixedResolutionStrategy;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.coco.lock2.lockbox.DownloadLockBoxService;
import com.coco.lock2.lockbox.util.DownModule;
import com.coco.theme.themebox.MainActivity;
import com.cooee.shell.sdk.CooeeSdk;
import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.ApkConfig;
import com.iLoong.launcher.Desktop3D.AppList3D;
import com.iLoong.launcher.Desktop3D.CellLayout3D;
import com.iLoong.launcher.Desktop3D.DefaultLayout;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.Icon3D;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.Desktop3D.SendMsgToAndroid;
import com.iLoong.launcher.Desktop3D.Workspace3D;
import com.iLoong.launcher.Desktop3D.circleSomethingDraw;
import com.iLoong.launcher.Folder3D.FolderIcon3D;
import com.iLoong.launcher.HotSeat3D.DefConfig;
import com.iLoong.launcher.HotSeat3D.HotSeat3D;
import com.iLoong.launcher.HotSeat3D.MissedCallIntent;
import com.iLoong.launcher.HotSeat3D.MissedCallIntent.MissedCallsContentObserver;
import com.iLoong.launcher.HotSeat3D.MissedCallIntent.newMmsContentObserver;
import com.iLoong.launcher.SetupMenu.DLManager;
import com.iLoong.launcher.SetupMenu.SensorListener;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.SetupMenu.Actions.ActionSetting;
import com.iLoong.launcher.SetupMenu.Actions.DesktopAction.DesktopSettingActivity;
import com.iLoong.launcher.SetupMenu.Actions.MenuActionListener;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.SetupMenu.Actions.SystemAction;
import com.iLoong.launcher.UI3DEngine.ConfigBase;
import com.iLoong.launcher.UI3DEngine.Desktop3D;
import com.iLoong.launcher.UI3DEngine.Messenger;
import com.iLoong.launcher.UI3DEngine.MyPixmapPacker;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.Widget3D;
import com.iLoong.launcher.Widget3D.Widget3DManager;
import com.iLoong.launcher.Widget3D.Widget3DRefreshRender;
import com.iLoong.launcher.Widget3D.WidgetDownload;
import com.iLoong.launcher.Widget3D.WidgetPluginView3D;
import com.iLoong.launcher.Workspace.AddListAdapter;
import com.iLoong.launcher.Workspace.AddWidget3DListAdapter;
import com.iLoong.launcher.Workspace.AddWidget3DListAdapter.ListItem;
import com.iLoong.launcher.Workspace.CellLayout;
import com.iLoong.launcher.Workspace.Workspace;
import com.iLoong.launcher.airpush.AirPush;
import com.iLoong.launcher.airpush.AirPushConfig;
import com.iLoong.launcher.app.AirDefaultLayout;
import com.iLoong.launcher.app.AppListDB;
import com.iLoong.launcher.app.LauncherBase;
import com.iLoong.launcher.app.LauncherModel;
import com.iLoong.launcher.app.LauncherProvider;
import com.iLoong.launcher.app.LauncherSettings;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.cling.Introduction;
import com.iLoong.launcher.core.Assets;
import com.iLoong.launcher.core.Utilities;
import com.iLoong.launcher.data.FolderInfo;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.data.ShortcutInfo;
import com.iLoong.launcher.data.UserFolderInfo;
import com.iLoong.launcher.data.Widget2DInfo;
import com.iLoong.launcher.data.Widget3DInfo;
import com.iLoong.launcher.excpetion.ActErrorReport;
//import com.iLoong.launcher.macinfo.LaunchStatistics;
import com.iLoong.launcher.media.MediaCache;
import com.iLoong.launcher.scene.SceneManager;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;
import com.iLoong.launcher.user.SystemAppRetriever;
//import com.iLoong.launcher.user.SystemAppRetriever;
import com.iLoong.launcher.wallpeper.ShakeListener;
import com.iLoong.launcher.wallpeper.WallpaperShakeListener;
import com.iLoong.launcher.widget.Widget;
import com.iLoong.launcher.widget.Widget.MotionEventPool;
import com.iLoong.launcher.widget.WidgetHost;
import com.iLoong.launcher.widget.WidgetHostView;
import com.umeng.analytics.CoCoMobclickAgent;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.pm.PackageManager.NameNotFoundException;
import com.cooee.statistics.StatisticsBaseNew;
import com.cooee.statistics.StatisticsExpandNew;
public class iLoongLauncher extends AndroidApplication implements
		MenuActionListener, LauncherBase {

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
	static final int DIALOG_DESKTOP_EFFECT=13;
	// public static int WORKSPACE_CELL_WIDTH;
	// public static int WORKSPACE_CELL_HEIGHT;

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

	private Bundle mSavedInstanceState;

	public LinearLayout mMainMenuLayout;
	private LauncherModel mModel;

	private static HashMap<Long, FolderInfo> sFolders = new HashMap<Long, FolderInfo>();

	public Workspace xWorkspace;

	public SetupMenu mSetupMenu;

	private static circleSomethingDraw circleDrawView;

	private static iLoongLauncher mInstance;
	public static BluetoothAdapter adapter;

	public Desktop3DListener d3dListener;
	// Hotseats (quick-launch icons next to AllApps)
	private static final int NUM_HOTSEATS = 2;
	private String[] mHotseatConfig = null;
	private Intent[] mHotseats = null;
	private Bitmap[] mHotseatIcons = null;
	private String[] mHotseatIconsName = null;
	private CharSequence[] mHotseatLabels = null;

	private final int[] mCellCoordinates = new int[2];
	private CellLayout.CellInfo cellInfo = new CellLayout.CellInfo();
	private Vibrator mVibrator;
	private int vibratorAble = 1;

	private Introduction introduction;
	private boolean showIntroductionAgain = false;
	private int sortAppCheckId = -1;
	private boolean showIntroduction = false;
	public boolean introductionShown = false;
	private int mWallpaperWidth;
	private int mWallpaperHeight;
	private WallpaperManager mWallpaperManager;
	private static final float WALLPAPER_SCREENS_SPAN = 2f;

	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminSample;
	private Vector2 clickPoint = new Vector2();
	private BroadcastReceiver mReceiver;
	static final String ACTION_RESTART = "com.cooee.launcher.restart";
	static final String EXTRA_SEARCH_WIDGET = "com.iLoong.widget";
	static final String EXTRA_CUSTOM_WIDGET = "iLoong.widget.3d";
	public static boolean writeBootTime = false;
	public static long bootTime = -1;

	private AddWidget3DListAdapter widget3DArray;

	private Toast toast;

	// 手机甩动监听
	private ShakeListener mShaker;

	private boolean installAssertAPK = false;

	public static long _time = 0;

	public static boolean releaseTexture = false;

	private String currentLocale = null;
	// private float currentFontScale = 1;
	public static int mainThreadId;

	public static boolean showAllAppFirst = false;
	public static String RUNTIME_STATE_SHOWALLAPP = "showAllApp";
	public static String RUNTIME_STATE_RESTART = "restart";

	public static boolean booting = false;
	public boolean stoped = false;
	public boolean delaySetupMenu = false;

	//public LaunchStatistics launchStatistics;
	public AirPush airpush;
	public boolean hasRemoveGLView = false;
	public boolean checkSize = false;
	private newMmsContentObserver newMmsobserver = null;
	private MissedCallIntent mMissedCallIntent = null;
	private MissedCallsContentObserver mIntentReceiver = null;

	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	// 重力感应监听
	public SensorListener mSensorListener;
	private boolean mIsOnpauseAlready;
	// xiatian add end

	private View glView;

	// xiatian add start //New Requirement 20130507
	public static final String WALLPAPER_BOX_MAIN_ACTIVITY = "com.coco.wallpaper.wallpaperbox.MainActivity";
	public static final String SCENE_BOX_MAIN_ACTIVITY = "com.coco.scene.scenebox.MainActivity";

	// xiatian add end
    //xiatian add start //StatisticsNew
    String appid = null;
    String sn = null;
    int launcherVersion = -1;
    //xiatian add end
    
	public void regsitedMissCall() {
		Context mContext = iLoongLauncher.getInstance().getApplicationContext();
		Handler myHandler = new Handler(this.getMainLooper());
		if (mMissedCallIntent == null) {
			mMissedCallIntent = new MissedCallIntent(mContext, myHandler);
		}
		if (mIntentReceiver == null) {
			mIntentReceiver = mMissedCallIntent.getMissedCallIntentReceiver();
			mContext.getContentResolver().registerContentObserver(
					Calls.CONTENT_URI, true, mIntentReceiver);
			mIntentReceiver.onChange(false);
		}
		if (newMmsobserver == null) {
			newMmsobserver = mMissedCallIntent.getnewMmsContentObserver();
			mContext.getContentResolver().registerContentObserver(
					Uri.parse("content://mms-sms/"), true, newMmsobserver);
			newMmsobserver.onChange(false);
		}
	}

	public int getMissedCallNum() {
		int number = 0;
		if (mMissedCallIntent != null) {
			number = mMissedCallIntent.missedCallCount;
			if (number > 99)
				number = 99;
		}
		return number;
	}

	public int getMissedSmsNum() {
		int number = 0;
		if (mMissedCallIntent != null) {
			number = mMissedCallIntent.newMmsCount
					+ mMissedCallIntent.newSmsCount;
			if (number > 99)
				number = 99;
		}
		return number;
	}

	private class AppWidgetResetObserver extends ContentObserver {
		public AppWidgetResetObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			onAppWidgetReset();
		}
	}

	private void onAppWidgetReset() {
		mAppWidgetHost.startListening();
	}

	public View setCircleView() {
		LayoutInflater inflater = getLayoutInflater();
		circleDrawView = (circleSomethingDraw) inflater.inflate(
				RR.layout.circleselect, null);
		return circleDrawView;
	}

	public static circleSomethingDraw getCircleView() {
		return circleDrawView;
	}

	public static iLoongLauncher getInstance() {
		return mInstance;
	}

	public static String languageSpace = "";
	public static int curLanguage = 0;/* 0: zh_CN, 1: zh_TW 2: en */

	public Intent getHotSeatIntent(int index) {
		return mHotseats[index];
	}

	public int equalHotSeatIntent(Intent intent) {
		for (int i = 0; i < mHotseatConfig.length; i++) {
			if (mHotseats[i].filterEquals(intent)) {
				return i;
			}
		}
		return -1;
	}

	public Bitmap findHotSeatBitmap(int index) {
		if (index == -1 || index >= mHotseatConfig.length) {
			return null;
		}
		return mHotseatIcons[index];
	}

	public Intent getHotSeatIntent(String findStr) {
		for (int i = 0; i < mHotseatConfig.length; i++) {
			if (mHotseatConfig[i].equals(findStr)) {
				return mHotseats[i];
			}
		}
		return null;
	}

	public String getHotSeatString(int index) {
		if (index == -1 || index >= mHotseatConfig.length) {
			if (DefaultLayout.hotseatbar_browser_special_name)
				return getString(RR.string.Internet);
			else
				return getString(RR.string.Explorer);
		}
		String retStr = null;
		retStr = mHotseatLabels[index].toString();
		if (retStr == null) {
			if (DefaultLayout.hotseatbar_browser_special_name)
				retStr = getString(RR.string.Internet);
			else
				retStr = getString(RR.string.Explorer);
		}
		return retStr;

	}

	public int getHotSeatLength() {
		return mHotseatConfig.length;
	}

	// public String getHotSeatString(String findStr) {
	//
	// for (int i = 0; i < mHotseatConfig.length; i++) {
	// if (mHotseatConfig[i].equals(findStr)) {
	// return mHotseatLabels[i].toString();
	// }
	// }
	// return null;
	//
	// }

	private void deletePackFile() {
		File dir = new File(getApplicationInfo().dataDir + "/");
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isFile())
					file.delete();
			}
		}
	}

	public FolderIcon3D getOpenFolder() {
		if (d3dListener == null) {
			return null;
		}
		return d3dListener.getOpenFolder();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Debug.startMethodTracing();
		Log.v("iLoongLauncher", "onCreate() start");
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = this.getSharedPreferences("launcher",
				Activity.MODE_PRIVATE);
		mainThreadId = android.os.Process.myTid();
		_time = System.currentTimeMillis();
		long bootSpent = SystemClock.elapsedRealtime();
		long bootTime = _time - bootSpent;
		long bootCompleteTime = prefs.getLong("boot_complete_time", -1);
		if (bootCompleteTime == -1) {
			if (bootSpent <= 2 * 60 * 1000)
				booting = true;
			else
				booting = false;
		} else if (bootTime <= bootCompleteTime) {
			if (_time < bootCompleteTime)
				booting = true;
			else
				booting = false;
		} else {
			if (bootSpent <= 2 * 60 * 1000)
				booting = true;
			else
				booting = false;
		}
		mInstance = this;

		// enable_themebox
		if (iLoongApplication.needRestart) {
			SystemAction.RestartSystem();
			return;
		}
		// enable_themebox
		Utils3D.calibration();
		deletePackFile();
		String lan = Locale.getDefault().getLanguage();
		// Locale.getDefault().getCountry()
		// Log.v("jbc", "lan lan="+Locale.getDefault().getLanguage());
		// Log.v("jbc", "lan str="+Locale.getDefault().toString());
		if (lan.equals("zh")) {
			languageSpace = "";
			lan = Locale.getDefault().toString();
			if (lan.equals("zh_TW"))
				curLanguage = 1;
			else
				curLanguage = 0;
		} else {
			languageSpace = " ";
			curLanguage = 2;
		}
		currentLocale = this.getResources().getConfiguration().locale
				.toString();
		// currentFontScale = this.getResources().getConfiguration().fontScale;

		Utils3D.showTimeFromStart("before default layout");
		initHotseats(); // xiatian add //Statistics
		new DefaultLayout();// 可以优化，不用读取所有的 //xiatian add //Statistics
		Utils3D.showTimeFromStart("after default layout");

        //xiatian add start //StatisticsNew
        StatisticsBaseNew.setApplicationContext(this.getApplicationContext());
        JSONObject tmp = Assets.config;
        PackageManager mPackageManager = getPackageManager();
        
        try {
            JSONObject config = tmp.getJSONObject("config");
            appid = config.getString("app_id");
            sn = config.getString("serialno");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try {
            launcherVersion = mPackageManager.getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //xiatian add end
        
        if (prefs.getBoolean("first_run", true)) {
            ShortcutInfo.isFirstRun = true; 
            
            //xiatian add start //StatisticsNew
            StatisticsExpandNew.register(
                    this,
                    sn,
                    appid,
                    CooeeSdk.cooeeGetCooeeId(this),
                    1,
                    this.getApplication().getPackageName(),
                    "" + launcherVersion
                    );
            //xiatian add end

        } else {
            ShortcutInfo.isFirstRun = false;
        }
        
        //xiatian add start //StatisticsNew
        StatisticsExpandNew.startUp(
                this,
                sn,
                appid,
                CooeeSdk.cooeeGetCooeeId(this),
                1,
                this.getApplication().getPackageName(),
                "" + launcherVersion
                );
        //xiatian add end
        
		if (iLoongApplication.BuiltIn)
			installAssertAPK = true;
		// Utils3D.showPidMemoryInfo("mem");
		Utils3D.showTimeFromStart("onCreate 1");
		// initHotseats(); /xiatian del //Statistics
		// teapotXu add start for Folder in mainmenu
		AppListDB.getInstance().Init(iLoongLauncher.getInstance());
		Utils3D.showTimeFromStart("onCreate 2");
		// new DefaultLayout();// 可以优化，不用读取所有的 //xiatian del //Statistics
		initBase();
		if (DefaultLayout.hide_launcher_wallpapers) {
			PackageManager pm = getPackageManager();
			pm.setComponentEnabledSetting(new ComponentName(this,
					"com.iLoong.launcher.desktop.WallpaperChooser"),
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
		}
		Utils3D.showTimeFromStart("onCreate 3");

		// Utils3D.showPidMemoryInfo("onCreate2");
		if (mWallpaperManager == null) {
			mWallpaperManager = WallpaperManager
					.getInstance(iLoongLauncher.this);
		}

		// showAllAppFirst =
		// prefs.getBoolean(iLoongLauncher.RUNTIME_STATE_SHOWALLAPP, false);
		// prefs.edit().putBoolean(iLoongLauncher.RUNTIME_STATE_SHOWALLAPP,
		// false).commit();

		// Utils3D.showPidMemoryInfo("onCreate3");
		// 首先我们要获得android设备管理代理
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		// LockScreen 继承�?DeviceAdminReceiver
		mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
		// Utils3D.showPidMemoryInfo("onCreate4");
		if (ThemeManager.getInstance().getThemeDB().getThemesStatus() > 0) {
			
			//xiatian start	//when change theme,wallpaper not show whole pic
//			ThemeManager.getInstance().ApplyWallpaper();//xiatian del
			//xiatian add start
			new Thread(new Runnable() {
				public void run() {
					ThemeManager.getInstance().ApplyWallpaper();
				}
			}).start();
			//xiatian add end
			//xiatian end
			
			ThemeManager.getInstance().getThemeDB().SaveThemesStatus(0);
		}
		if (DefaultLayout.show_missed_call_sms) {
			new Thread(new Runnable() {
				public void run() {
					regsitedMissCall();
				}
			}).start();
		}
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				SetupMenuActions.getInstance().Handle(
						ActionSetting.ACTION_RESTART);
			}

		};
		this.registerReceiver(mReceiver, new IntentFilter(ACTION_RESTART));
		// Utils3D.showPidMemoryInfo("onCreate5");
		Utils3D.showTimeFromStart("onCreate 4");
		d3dListener = new Desktop3DListener(this);
		// Utils3D.showPidMemoryInfo("onCreate6");
		glView = initializeDesktop3D(d3dListener, true);
		setContentView(glView);
		glView.setLayoutParams(createLayoutParams());
		Utils3D.showTimeFromStart("onCreate 5");
		View androidView = LayoutInflater.from(this).inflate(
				RR.layout.workspace, null);
		addContentView(androidView, createLayoutParams());
		// /*added by zfshi */
		addContentView(setCircleView(), createLayoutParams());
		// Utils3D.showPidMemoryInfo("onCreate7");
		if (showIntroduction)
			ClingManager.getInstance().setLauncher(this);
		// Utils3D.showPidMemoryInfo("onCreate8");
		Utils3D.showTimeFromStart("before setup");
		mSetupMenu = new SetupMenu(this);
		Utils3D.showTimeFromStart("after setup");
		iLoongApplication.ctx = getApplicationContext();
		iLoongApplication app = ((iLoongApplication) getApplication());
		app.setNeed2Exit(false);

		// Utils3D.showPidMemoryInfo("onCreate9");
		mModel = app.setLauncher(this);
		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mAppWidgetHost = new WidgetHost(this, APPWIDGET_HOST_ID);
		mAppWidgetHost.startListening();

		xWorkspace = (Workspace) findViewById(RR.id.workspace);
		xWorkspace.setLauncher(this);
		// int cellNum = PreferenceManager.getDefaultSharedPreferences(this)
		// .getInt("cell_num", DefaultLayout.default_workspace_pagecounts);
		int cellNum = ThemeManager.getInstance().getThemeDB().getScreenCount();
		if (cellNum == 0) {
			cellNum = DefaultLayout.default_workspace_pagecounts;
			ThemeManager.getInstance().getThemeDB().SaveScreenCount(cellNum);
		}
		for (int i = 0; i < cellNum; i++) {
			// Utils3D.showPidMemoryInfo("xworkspace");
			xWorkspace.addCell();
		}
		showIntroduction();

		registerContentObservers();

		mSavedState = savedInstanceState;
		restoreState(mSavedState);
		Utils3D.showTimeFromStart("onCreate 6");
		mDefaultKeySsb = new SpannableStringBuilder();
		Selection.setSelection(mDefaultKeySsb, 0);
		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		setActionListener();
		vibratorAble = SetupMenuActions.getInstance().getStringToIntger(
				"vibrator");
		// ReportError();
		Utils3D.showTimeFromStart("onCreate 6.1");
		setVolumeControlStream(2);
		AirPushConfig config = new AirPushConfig();
		config.appType = AirPushConfig.APP_TYPE_LAUNCHER;
		config.appTitle = "CoCoLauncher";
		config.appIcon = RR.drawable.ic_launcher;
		airpush = new AirPush(this, config);
		Utils3D.showTimeFromStart("onCreate 6.2");
		//launchStatistics = new LaunchStatistics(this);
		Utils3D.showTimeFromStart("onCreate end");
		// Utils3D.showPidMemoryInfo("onCreate13");
	}

	private void initBase() {
		UtilsBase.init(this);
		Messenger.init(mMainHandler);
		ConfigBase.disable_double_click = DefaultLayout.disable_double_click;
		ConfigBase.show_sensor = DefaultLayout.show_sensor;
		ConfigBase.widget_revise_complete = DefaultLayout.widget_revise_complete;
	}

	protected FrameLayout.LayoutParams createLayoutParams() {
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.gravity = Gravity.BOTTOM;
		return layoutParams;
	}

	private void mylock() {

		boolean active = mDPM.isAdminActive(mDeviceAdminSample);
		activeManage();// 去获得权�?
		if (!active) {// 若无权限
			activeManage();// 去获得权�?
		} else {
			Log.d("adminActive", "yes");
			mDPM.lockNow();// 并锁�?
		}
	}

	private void activeManage() {
		// 启动设备管理(隐式Intent) - 在AndroidManifest.xml中设定相应过滤器
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		// 权限列表
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
				mDeviceAdminSample);

		// 描述(additional explanation)
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"------ 其他描述 ------");
		iLoongLauncher.getInstance().startActivityForResult(intent, 1);

	}

	public void toStartLoader() {
		new Thread(new Runnable() {
			// int i = 0;
			@Override
			public void run() {
				synchronized (Desktop3DListener.lock) {
					while (!d3dListener.bCreat1Done) {
						try {
							Desktop3DListener.lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (!mRestoring) {
					mModel.startLoader(mInstance, true);
				}
			}

		}).start();
	}

	public View initializeDesktop3D(Desktop3DListener listener,
			boolean useGL2IfAvailable) {
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGL20 = useGL2IfAvailable;
		config.useAccelerometer = false;
		config.numSamples = 2;
		config.a = 8;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.handleKeyTypedChinese = true;
		config.resolutionStrategy = new FixedResolutionStrategy(
				Utils3D.getScreenWidth(), Utils3D.getScreenHeight());
		return initializeForView(listener, config);
	}

	protected void setWallpaperDimension() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		final int maxDim = Math.max(displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		final int minDim = Math.min(displayMetrics.widthPixels,
				displayMetrics.heightPixels);

		// We need to ensure that there is enough extra space in the wallpaper
		// for the intended
		// parallax effects
		// if (LauncherApplication.isScreenLarge()) {
		// mWallpaperWidth = (int) (maxDim *
		// wallpaperTravelToScreenWidthRatio(maxDim, minDim));
		// mWallpaperHeight = maxDim;
		// } else {

		// }
		final Drawable drawable = mWallpaperManager.getDrawable();
		if (drawable != null) {
			mWallpaperWidth = drawable.getIntrinsicWidth();
			mWallpaperHeight = drawable.getIntrinsicHeight();

			if (mWallpaperWidth < minDim)
				mWallpaperWidth = minDim;
			if (mWallpaperHeight < maxDim)
				mWallpaperHeight = maxDim;
		} else {
			mWallpaperWidth = Math.max((int) (minDim * WALLPAPER_SCREENS_SPAN),
					maxDim);
			mWallpaperHeight = maxDim;
		}

		if (DefaultLayout.disable_move_wallpaper) {
			mWallpaperWidth = minDim;
		}

		new Thread("setWallpaperDimension") {
			public void run() {
				mWallpaperManager.suggestDesiredDimensions(mWallpaperWidth,
						mWallpaperHeight);
			}
		}.start();
		// zhujieping add
		if (!ThemeManager.getInstance().getBoolean("miui_v5_folder")
				&& !DefaultLayout.miui_v5_folder) {
			((BitmapDrawable) drawable).getBitmap().recycle();
		}

	}

	public void onStart() {
		Log.v("iLoongLauncher", "onStart() start");

		if (airpush != null)
			airpush.onStart();
		stoped = false;
		setWallpaperDimension();
		if (DefaultLayout.broadcast_state) {
			sendBroadcast(new Intent("com.cooee.launcher.action.start"));
		}
		super.onStart();
		if (((iLoongApplication) getApplication()).need2Exit()) {
			Log.d("ANDROID_LAB", "ActOccurError.finish()");
			iLoongLauncher.this.finish();
		} else {
			// do normal things
		}

		Log.v("iLoongLauncher", "onStart() end");
	}

	public void ReportError() {
		// 处理记录于error.log中的异常
		String errorContent = "";// getErrorLog();
		if (errorContent != null) {
			Intent intent = new Intent(this, ActErrorReport.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("error", errorContent);
			intent.putExtra("by", "error.log");
			startActivity(intent);
		}
	}

	private void launchHotButton() {
		startActivitySafely(mHotseats[0], "hotseat");
	}

	private Uri getDefaultBrowserUri() {
		String url = getString(RR.string.default_browser_url);
		if (url.indexOf("{CID}") != -1) {
			url = url.replace("{CID}", "android-google");
		}
		return Uri.parse(url);
	}

	private boolean isUserAppEx(ResolveInfo info)// 不同于Applist里的isUserApp，内置应用的更新版本也被认为是内置应�?
	{
		boolean canUninstall = false;
		int flags = info.activityInfo.applicationInfo.flags;
		if ((flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			canUninstall = false;
		} else if ((flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
			canUninstall = true;
		}

		return canUninstall;
	}

	private Bitmap GetBmpFromImageName(String imageName, Intent intent,
			int index) {
		Bitmap image = null;
		if (imageName != null) {
			image = ThemeManager.getInstance().getBitmap(imageName);
			image = Tools.resizeBitmap(image, DefaultLayout.app_icon_size,
					DefaultLayout.app_icon_size);
		}
		// float density = getResources().getDisplayMetrics().density;

		// if(intent.getPackage() != null || image == null ||
		// !DefaultLayout.hotseat_hide_title)
		{
			boolean bNeedDeal = false;
			PackageManager pm = getPackageManager();
			List<ResolveInfo> allMatches = pm.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (mHotseatConfig[index].equals("*BROWSER*")) {
				if (DefaultLayout.hotseatbar_browser_special_name)
					mHotseatLabels[index] = getString(RR.string.Internet);
				else
					mHotseatLabels[index] = getString(RR.string.Explorer);
				bNeedDeal = true;
			}
			for (ResolveInfo ri : allMatches) {
				// if ((ri.activityInfo.applicationInfo.flags &
				// ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)
				if (isUserAppEx(ri) == false) {
					if (bNeedDeal) {
						// mHotseatLabels[index] =
						// getString(RR.string.Explorer);//getString(RR.string.Internet);//ri.activityInfo.loadLabel(pm);
						String packageName = ri.activityInfo.applicationInfo.packageName;
						if (findBrowserByPackageName(packageName)) {
							if (image == null) {
								Drawable drawable = ri.activityInfo
										.loadIcon(pm);
								image = Utilities.createIconBitmap(drawable,
										getApplicationContext());
							}
							bNeedDeal = false;
							break;
						}
					} else {

						if (image == null) {
							Drawable drawable = ri.activityInfo.loadIcon(pm);
							image = Utilities.createIconBitmap(drawable,
									getApplicationContext());
						}
						mHotseatLabels[index] = ri.activityInfo.loadLabel(pm);
						break;
					}
				} else {
					if (intent.getPackage() != null) {
						intent.setPackage(null);
						return GetBmpFromImageName(imageName, intent, index);
					}
				}
			}
		}

		if (mHotseatLabels[index] == null) {
			if (DefaultLayout.hotseatbar_browser_special_name)
				mHotseatLabels[index] = getString(RR.string.Internet);
			else
				mHotseatLabels[index] = getString(RR.string.Explorer);
		}
		return image;
	}

	private void initHotseats() {
		if (mHotseatConfig == null) {

			mHotseatConfig = getResources().getStringArray(RR.array.hotseats);
			if (mHotseatConfig.length > 0) {
				mHotseats = new Intent[mHotseatConfig.length];
				mHotseatLabels = new CharSequence[mHotseatConfig.length];
				mHotseatIcons = new Bitmap[mHotseatConfig.length];
				mHotseatIconsName = getResources().getStringArray(
						RR.array.hotseat_icons);
				for (int i = 0; i < mHotseatIconsName.length; i++) {
					mHotseatIconsName[i] = mHotseatIconsName[i];
				}
			} else {
				mHotseats = null;
				mHotseatIcons = null;
				mHotseatLabels = null;
				mHotseatIconsName = null;
			}

		}
	}

	public void initHotseatItem(int item, String imageName, String configName) {

		mHotseatConfig[item] = configName;

		mHotseatIconsName[item] = imageName;
	}

	public void loadHotseats(String customUri) {

		PackageManager pm = getPackageManager();
		for (int i = 0; i < mHotseatConfig.length; i++) {
			Intent intent = null;
			if (mHotseatConfig[i].equals("*BROWSER*")) {
				// magic value meaning "launch user's default web browser"
				// replace it with a generic web request so we can see if there
				// is indeed a default

				String defaultUri;// = getString(RR.string.default_browser_url);
				if (customUri != null) {
					defaultUri = customUri;
				} else {
					defaultUri = "http://www.google.cn";// 用百度代替空白网�?about:blank"否则欧鹏浏览器无法识�?
				}

				intent = new Intent(Intent.ACTION_VIEW,
						((defaultUri != null) ? Uri.parse(defaultUri)
								: getDefaultBrowserUri()))
						.addCategory(Intent.CATEGORY_BROWSABLE);
				if (DefaultLayout.default_explorer != null) {
					intent.setPackage(DefaultLayout.default_explorer);
					Utils3D.showTimeFromStart("9.1" + i);
					List<ResolveInfo> allMatches = pm.queryIntentActivities(
							intent, PackageManager.MATCH_DEFAULT_ONLY);
					Utils3D.showTimeFromStart("9.2" + i);
					if (allMatches == null || allMatches.size() == 0) {
						intent.setPackage(null);
					}
				}
				// note: if the user launches this without a default set, she
				// will always be taken to the default URL above; this is
				// unavoidable as we must specify a valid URL in order for the
				// chooser to appear, and once the user selects something, that
				// URL is unavoidably sent to the chosen app.
			} else {
				try {
					intent = Intent.parseUri(mHotseatConfig[i], 0);
				} catch (java.net.URISyntaxException ex) {
					Log.w(TAG, "Invalid hotseat intent: " + mHotseatConfig[i]);
					// bogus; leave intent=null
				}
			}

			if (intent == null) {
				mHotseats[i] = null;
				mHotseatLabels[i] = getText(RR.string.activity_not_found);
				continue;
			}

			mHotseats[i] = intent;
			mHotseatIcons[i] = GetBmpFromImageName(mHotseatIconsName[i],
					intent, i);
		}
	}

	/**
	 * 读取是否有未处理的报错信�?每次读取后都会将error.log清空>
	 * 
	 * @return 返回未处理的报错信息或null
	 */
	private String getErrorLog() {
		File fileErrorLog = new File(iLoongApplication.PATH_ERROR_LOG);
		String content = null;
		FileInputStream fis = null;
		try {
			if (fileErrorLog.exists()) {
				byte[] data = new byte[(int) fileErrorLog.length()];
				fis = new FileInputStream(fileErrorLog);
				fis.read(data);
				content = new String(data);
				data = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (fileErrorLog.exists()) {
					fileErrorLog.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	public Handler mMainHandler = new Handler() {
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();

			switch (msg.what) {
			case Messenger.CIRCLE_EVENT_UP:
				circleDrawView.libgdxAdaptAndroidViewEvent(msg.what, msg.arg1,
						msg.arg2, null);
				break;
			case Messenger.CIRCLE_EVENT_DOWN:
				circleDrawView.libgdxAdaptAndroidViewEvent(msg.what, msg.arg1,
						msg.arg2, null);
				break;
			case Messenger.CIRCLE_EVENT_DRAG:
				circleDrawView.libgdxAdaptAndroidViewEvent(msg.what, msg.arg1,
						msg.arg2, null);
				break;
			case Messenger.CIRCLE_EVENT_TOAST:
			case Messenger.EVENT_TOAST_USER:
				circleDrawView.libgdxAdaptAndroidViewEvent(msg.what, msg.arg1,
						msg.arg2, b.getString("toastString"));
				break;
			case Messenger.EVENT_TOAST:
				String s = b.getString("toastString");
				if (toast == null)
					toast = Toast.makeText(iLoongLauncher.this, "",
							Toast.LENGTH_SHORT);
				toast.cancel();
				toast.setText(s);
				toast.show();
				break;
			case Messenger.EVENT_CREATE_POP_DIALOG:
				showDialog(DIALOG_CIRCLE_DELALL);
				break;
			case Messenger.EVENT_CANNOT_FOUND_APK_DIALOG:
				showDialog(DIALOG_APK_CANNOT_FOUND);
				break;
			case Messenger.EVENT_DOWNLOAD_WIDGET_DIALOG:
				showDialog(DIALOG_DOWNLOAD_WIDGET);
				break;
			case Messenger.EVENT_CREATE_PROG_DIALOG:
				showProgressDialog();
				break;
			case Messenger.EVENT_CREATE_RENAME_FOLDER:
				folderIcon = (FolderIcon3D) msg.obj;
				showRenameDialog(folderIcon.mInfo);
				break;
			case Messenger.EVENT_UPDATE_SYS_WIDGET:
				View view = (View) msg.obj;
				MotionEvent event = Widget.MotionEventPool.getEvent(msg.arg1);
				// Log.i("widget","event:"+view.toString()+",x,y="+event.getX()+","+event.getY());
				if (view != null)
					view.dispatchTouchEvent(event);
				event.recycle();
				break;
			case Messenger.EVENT_ADD_SYS_WIDGET:
				if (msg.obj == null)
					break;
				Widget widget = (Widget) msg.obj;
				Widget2DInfo info = (Widget2DInfo) widget.getItemInfo();
				addAppWidget(info, true);
				info.hostView.setWidget(widget);
				break;
			case Messenger.MSG_WIDGET_FOCUS:
				if (msg.obj == null)
					break;
				WidgetPluginView3D widgetPluginView = (WidgetPluginView3D) msg.obj;
				d3dListener.focusWidget(widgetPluginView, msg.arg1);
				break;

			case Messenger.EVENT_CLICK_TO_WALLPAPER:
				mWallpaperManager.sendWallpaperCommand(getWindow()
						.getCurrentFocus().getWindowToken(),
						"android.wallpaper.tap", msg.arg1, msg.arg2, 0, null);
				break;
			case Messenger.EVENT_ADD_SYS_SHORTCUT:
				delaySetupMenu = true;
				clickPoint.x = msg.arg1;
				clickPoint.y = msg.arg2;
				dropPos[0] = msg.arg1;
				dropPos[1] = msg.arg2;
				if (DefaultLayout.hide_add_shortcut_dialog)
					startWallpaper();
				else
					showAddShortcutDialog();
				break;

			// xiatian start //add 3 virtueIcon
			case Messenger.EVENT_SELECT_WALLPAPER:
				startWallpaper();
				break;

			case Messenger.EVENT_SELECT_ZHUTI:
				Intent intent0 = new Intent();
				if (!FeatureConfig.enable_themebox)
					intent0.setComponent(new ComponentName(
							SetupMenu.getContext(),
							com.iLoong.launcher.theme.ThemeManagerActivity.class));
				else
					intent0.setComponent(new ComponentName(SetupMenu
							.getContext(), MainActivity.class));

				SetupMenuActions.getInstance().getContext()
						.startActivity(intent0);
				break;

			case Messenger.EVENT_SEETINGS_ZHUOMIAN:
				Intent intent1 = new Intent();
				intent1.setComponent(new ComponentName(SetupMenu.getContext(),
						DesktopSettingActivity.class));
				SetupMenuActions.getInstance().getContext()
						.startActivity(intent1);
				break;
			// xiatian end

			// xiatian add start //New Requirement 20130507
			case Messenger.EVENT_SELECT_CHANGJINGZHUOMIAN:
				startChangJingZhuoMian();
				break;
			// xiatian add end

			case Messenger.EVENT_DELETE_SYS_WIDGET:
				deleteWidget((Widget) msg.obj);
				break;
			case Messenger.EVENT_WIDGET_GET_VIEW:
				Log.i("widget", "event:capture");
				WidgetHostView widget1 = (WidgetHostView) msg.obj;
				widget1.buildCustomCache();
				// long ms = System.currentTimeMillis();
				// Bitmap bmp = Utilities.getViewBitmap(view1);
				// Log.v("ms", "ms1:" + (System.currentTimeMillis() - ms));
				// ms = System.currentTimeMillis();
				// widget1.updateView(bmp);
				// Log.v("ms", "ms2:" + (System.currentTimeMillis() - ms));
				break;
			case Messenger.EVENT_SELECT_SHORTCUT:
				processShortcut((Intent) msg.obj,
						REQUEST_CREATE_CONTACT_SHORTCUT);
				break;
			case Messenger.EVENT_DELETE_PAGE:
				showDialog(DIALOG_DELETE_PAGE);
				break;
			case Messenger.EVENT_START_ACTIVITY:
				ItemInfo itemInfo = (ItemInfo) msg.obj;
				startActivitySafely(((ShortcutInfo) itemInfo).intent, itemInfo);
				break;
			case Messenger.EVENT_START_ACTIVITY_FROM_INTENT:
				startActivitySafelyFromIntent((Intent) msg.obj);
				break;
			case Messenger.EVENT_LAUNCH_HOTBUTTON:
				launchHotButton();
				break;
			case 7777:
				Log.e("exit", "!!!Exit!!!");
				iLoongLauncher.getInstance().finish();
				System.exit(0);
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
				xWorkspace.setVisibility(View.VISIBLE);
				// Log.d("launcher", "workspace visible");
				// xWorkspace.ShowCurScreen();
				break;
			case Messenger.MSG_HIDE_WORKSPACE_EX:
				xWorkspace.hide();
				xWorkspace.setVisibility(View.GONE);
				// Log.d("launcher", "workspace invisible");
				// xWorkspace.hideCurScreen();
				break;
			case Messenger.MSG_SCROLL_WORKSPACE:
				xWorkspace.scrollTo(msg.arg1, 0);
				// xWorkspace.showCurrentPage();
				break;
			case Messenger.MSG_ADD_WORKSPACE_CELL:
				xWorkspace.addCellAt(msg.arg1);
				break;
			case Messenger.MSG_REMOVE_WORKSPACE_CELL:
				xWorkspace.removeCellAt(msg.arg1);
				break;
			case Messenger.MSG_VIBRATOR:
				if (!DefaultLayout.disable_vibrator && vibratorAble == 1)
					mVibrator.vibrate(msg.arg1);
				break;
			case Messenger.MSG_SYS_VIBRATOR:
				xWorkspace.performHapticFeedback(
						HapticFeedbackConstants.LONG_PRESS,
						HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
				break;
			case Messenger.MSG_SYS_PLAY_SOUND_EFFECT:
				xWorkspace.playSoundEffect(SoundEffectConstants.CLICK);
				break;
			case Messenger.MSG_ON_BEGIN_OPEN_WIDGET:
				xWorkspace.setVisibility(View.INVISIBLE);
				break;
			case Messenger.MSG_ON_COMPLETE_CLOSE_WIDGET:
				xWorkspace.setVisibility(View.VISIBLE);
				break;
			case Messenger.MSG_MOVE_WIDGET:
				moveAppWidget((View3D) msg.obj, msg.arg1);
				break;
			case Messenger.MSG_ADD_WIDGET:
				addAppWidgetFromDrop((ComponentName) msg.obj, new int[] {
						(int) msg.arg1, (int) msg.arg2 });
				break;
			case Messenger.MSG_ADD_SHORTCUT:
				processShortcutFromDrop((ComponentName) msg.obj, msg.arg1,
						msg.arg2);
				break;
			case Messenger.EVENT_CREATE_BLUETOOTH:
				adapter = BluetoothAdapter.getDefaultAdapter();
				break;
			case Messenger.EVENT_CREATE_LOCKSCREEN:
				mylock();
				break;
			case Messenger.MSG_REFRESH_CLING_STATE:
				ClingManager.getInstance().refreshClingState();
				break;
			case Messenger.MSG_HIDE_CLING_POINT:
				ClingManager.getInstance().hideClingPoint();
				break;
			case Messenger.MSG_SHOW_CLING_POINT:
				ClingManager.getInstance().showClingPoint();
				break;
			case Messenger.MSG_WAIT_CLING:
				ClingManager.getInstance().startWait();
				break;
			case Messenger.MSG_CANCEL_WAIT_CLING:
				ClingManager.getInstance().cancelWait();
				break;
			case Messenger.EVENT_CREATE_RESTART_DIALOG:
				showDialog(DIALOG_RESTART);
				break;
			case Messenger.EVENT_CREATE_EFFECT_DIALOG:
				showDialog(DIALOG_APP_EFFECT);
				break;
		    //zqh start
			case Messenger.MSG_CREAT_DESKTOP_EFFECT:
			    showDialog(DIALOG_DESKTOP_EFFECT);
			    break;
			//zqh end
			case Messenger.EVENT_DELETE_NOT_EMPTY_FOLDER:
				showDialog(DIALOG_DELETE_FOLDER);
				break;
			case Messenger.MSG_SHOW_SORT_DIALOG:
				sortAppCheckId = msg.arg1;
				showDialog(DIALOG_SORT_APP);
				break;
			case Messenger.MSG_SYS_SHOW_NOTICEBAR:
				getWindow().setFlags(
						~WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				break;
			case Messenger.MSG_SYS_HIDE_NOTICEBAR:
				getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
				iLoongLauncher.getInstance().postRunnable(new Runnable() {

					@Override
					public void run() {
						R3D.packer.updateTextureAtlas(R3D.packerAtlas,
								TextureFilter.Linear, TextureFilter.Linear);
					}

				});
				Gdx.graphics.requestRendering();
				break;
			case Messenger.MSG_RESET_TEXTURE_WRITE:
				iLoongLauncher.getInstance().postRunnable(new Runnable() {

					@Override
					public void run() {
						MyPixmapPacker.write = true;
						R3D.packer.updateTextureAtlas(R3D.packerAtlas,
								TextureFilter.Linear, TextureFilter.Linear);
					}

				});
				Gdx.graphics.requestRendering();
				break;
			case Messenger.MSG_UPDATE_PACKAGE:
				mModel.updatePackage();
				break;
			case Messenger.MSG_CHANGE_THREAD_PRIORITY:
				mModel.changeLoadThreadPriority();
				break;
			case Messenger.MSG_SYS_LIHGHT_CHANGE:
				Object bright = msg.obj;
				if (bright instanceof Integer) {

					int brightness = (Integer) bright;
					Log.v("brightness", " brightness is " + brightness);
					setBrightnesss(brightness);
					saveBrightness(iLoongLauncher.getInstance()
							.getContentResolver(), brightness);

				}
				break;
			case Messenger.EVENT_CHECK_SIZE:
				if (d3dListener.mPaused)
					break;
				if (checkSize) {
					checkSize = false;
					Utils3D.resetSize();
					Log.v("resize", "utils3d width:" + Utils3D.getScreenWidth()
							+ "height:" + Utils3D.getScreenHeight());
					if (Utils3D.getScreenWidth() > Utils3D.getScreenHeight()) {
						Log.e("resize", "checkSize:now Restarting...");
						SystemAction.RestartSystem();
					}
				}
				break;
			}
		}
	};

	public void setBrightnesss(int brightness) {
		WindowManager.LayoutParams lp = iLoongLauncher.getInstance()
				.getWindow().getAttributes();
		if (brightness <= 1) {
			return;
		} else {
			lp.screenBrightness = brightness / 255.0f;
			iLoongLauncher.getInstance().getWindow().setAttributes(lp);
		}
	}

	public void getScreenShot(){
	    //ScreenUtils.getFrameBufferTexture(0,0,Utils3D.getScreenWidth(),Utils3D.getScreenHeight());
	}
	
	public static void saveBrightness(ContentResolver resolver, int brightness) {
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(resolver, "screen_brightness",
				brightness);
		resolver.notifyChange(uri, null);
	}

	public long cur;

	// Events
	protected void onPause() {
		Log.v("iLoongLauncher", "Launcher onPause()0");
		delaySetupMenu = true;
		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			mIsOnpauseAlready = true;
		}
		// xiatian add end
		d3dListener.forceTouchUp();

		//友盟统计
		MobclickAgent.onPause(this);
		
		/*
		 * 当桌面在暂停状态时发送一个广播给场景，whj add
		 */
		Intent sceneIntent = new Intent();
		sceneIntent.setAction("com.cooee.launcher.action.onpause");
		sendBroadcast(sceneIntent);

		UtilsBase.pauseTime = System.nanoTime();
		UtilsBase.resumeTime = 0;
		if (writeBootTime) {
			writeBootTime = false;
			SharedPreferences prefs = this.getSharedPreferences("launcher",
					Activity.MODE_PRIVATE);
			prefs.edit().putLong("boot_complete_time", bootTime).commit();

		}
		CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
		if (curCellLayout != null)
			curCellLayout.resetCurrFocus();
		if (d3dListener.getRoot() != null) {
			if (d3dListener.getRoot().getAppHost() != null) {
				if (d3dListener.getRoot().getAppHost().appList != null) {
					d3dListener.getRoot().getAppHost().appList
							.hideCurrPageFocus();
				}
			}
		}
		Intent pauseBroadCast = new Intent();
		pauseBroadCast.setAction(LAUNCHER_STATE_PAUSE);
		sendBroadcast(pauseBroadCast);
		long cur = System.currentTimeMillis();
		// View hostView =
		// xWorkspace.getChildAt(d3dListener.getCurrentScreen());
		View mtkWidgetView = xWorkspace.searchIMTKWidget(xWorkspace);
		if (mtkWidgetView != null) {
			((IMTKWidget) mtkWidgetView).onPauseWhenShown(d3dListener
					.getCurrentScreen());
			Log.e("launcher", "onPauseWhenShown");
		}

		d3dListener.mPaused = true;
		Widget3DManager.getInstance().pauseAllWidget3D();
		if (mSetupMenu != null)
			mSetupMenu.CloseMenu();
		if (mShaker != null) {
			mShaker.pause();
		}
		if (folderIcon != null && folderIcon.bRenameFolder) {
			renameFoldercleanup();
		}
		View3DTweenAccessor.manager.pause();
		if (airpush != null)
			airpush.onPause();

		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			sensorListenerPause();
		}
		// xiatian add end

		super.onPause();
		// Log.v("click","onPause() time:" + (System.currentTimeMillis() -
		// cur));
		Log.v("iLoongLauncher", "Launcher onPause() end");
	}

	protected void onResume() {
		Log.v("iLoongLauncher", "Launcher onResume() start");
		UtilsBase.resumeTime = System.nanoTime();
		UtilsBase.pauseTime = Long.MAX_VALUE;
		if (writeBootTime) {
			writeBootTime = false;
			SharedPreferences prefs = this.getSharedPreferences("launcher",
					Activity.MODE_PRIVATE);
			prefs.edit().putLong("boot_complete_time", bootTime).commit();

		}
		// enable_themebox
		if (iLoongApplication.needRestart) {
			SystemAction.RestartSystem();
			super.onResume();
			return;
		}
		// enable_themebox
		CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
		if (curCellLayout != null)
			curCellLayout.resetCurrFocus();
		if (d3dListener.getRoot() != null) {
			if (d3dListener.getRoot().getAppHost() != null) {
				if (d3dListener.getRoot().getAppHost().appList != null) {
					d3dListener.getRoot().getAppHost().appList
							.hideCurrPageFocus();
				}
			}
		}
		Intent resumeBroadCast = new Intent();
		resumeBroadCast.setAction(LAUNCHER_STATE_RESUME);
		sendBroadcast(resumeBroadCast);
		if (airpush != null)
			airpush.onResume();
//		if (launchStatistics != null)
//			launchStatistics.onResume();


        //xiatian add start //StatisticsNew
        StatisticsExpandNew.dailyAttendance(
                this,
                sn,
                appid,
                CooeeSdk.cooeeGetCooeeId(this),
                1,
                this.getApplication().getPackageName(),
                "" + launcherVersion
                );
        StatisticsExpandNew.use(
                this,
                sn,
                appid,
                CooeeSdk.cooeeGetCooeeId(this),
                1,
                this.getApplication().getPackageName(),
                "" + launcherVersion
                );
        //xiatian add end       
		
		//友盟统计 
		MobclickAgent.onResume(this);
		CoCoMobclickAgent.NewUser(this);
		CoCoMobclickAgent.OnceEvent(this);
		
		// xiatian add end

		Utils3D.showTimeFromStart("start onResume");
		cur = System.currentTimeMillis();
		// xWorkspace.setVisibility(View.INVISIBLE);
		// View hostView = xWorkspace.getChildAt(xWorkspace.getCurrentScreen());
		View mtkWidgetView = xWorkspace.searchIMTKWidget(xWorkspace);
		if (mtkWidgetView != null) {
			((IMTKWidget) mtkWidgetView).onResumeWhenShown(d3dListener
					.getCurrentScreen());
			Log.v("launcher", "onResumeWhenShown");
		}
		View3DTweenAccessor.manager.resume();
		// Utils3D.showPidMemoryInfo("onResume2");
		super.onResume();
		// Utils3D.showPidMemoryInfo("onResume3");
		mRestoring = false;
		d3dListener.mPaused = false;
		if (d3dListener.mOnResumeNeedsLoad) {
			mWorkspaceLoading = true;
			mModel.startLoader(this, true);
			d3dListener.mOnResumeNeedsLoad = false;
			Log.v("launcher", "mOnResumeNeedsLoad");
		}
		Widget3DManager.getInstance().resumeAllWidget3D();

		registerShakeWallpapers();

		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			registerSensor();
		}
		// xiatian add end

		if (checkSize)
			SendMsgToAndroid.sendCheckSizeMsg(3000);
		if (d3dListener.getDragLayer() != null) {
			d3dListener.getDragLayer().onResume();
		}
		Gdx.graphics.requestRendering();
		// refreshWidget();
		// Utils3D.showPidMemoryInfo("onResume4");
		Utils3D.showTimeFromStart("end onResume");
		Log.v("iLoongLauncher", "Launcher onResume() end");
		SystemAppRetriever appInfo =new SystemAppRetriever(this,5000);
	}

	protected void onStop() {
		Log.v("iLoongLauncher", "Launcher onStop() start");
		delaySetupMenu = false;
		stoped = true;
		Widget3DManager.getInstance().stopAllWidget3D();
		if (mShaker != null) {
			mShaker.pause();
		}
		if (R3D.packer != null)
			R3D.packer.updateTextureAtlas(R3D.packerAtlas,
					TextureFilter.Linear, TextureFilter.Linear);
		if (DefaultLayout.broadcast_state) {
			sendBroadcast(new Intent("com.cooee.launcher.action.stop"));
		}
		if (airpush != null)
			airpush.onStop();
		super.onStop();
		Log.v("iLoongLauncher", "Launcher onStop() end");
	}

	protected void onDestroy() {
		Log.v("iLoongLauncher", "Launcher onDestroy() start");
		if (airpush != null)
			airpush.onDestroy();
//		if (launchStatistics != null)
//			launchStatistics.onDestroy();

		super.onDestroy();
		if (mSetupMenu != null)
			mSetupMenu.Release();
		ThemeManager.getInstance().Release();

		// wanghongjian add start //enable_DefaultScene
		if (FeatureConfig.enable_DefaultScene) {
			if (SceneManager.getInstance() != null)
				SceneManager.getInstance().Release();
		}
		// wanghongjian add end

		if (mReceiver != null)
			unregisterReceiver(mReceiver);
		android.os.Process.killProcess(android.os.Process.myPid());
		Log.v("iLoongLauncher", "Launcher onDestroy() end");
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// super.onPrepareOptionsMenu(menu);
	// mSetupMenu.PopSetupMenu(findViewById(RR.id.workspace));
	//
	// return false;
	// }
	private String getTypedText() {
		return mDefaultKeySsb.toString();
	}

	private void clearTypedText() {
		mDefaultKeySsb.clear();
		mDefaultKeySsb.clearSpans();
		Selection.setSelection(mDefaultKeySsb, 0);
	}

	@Override
	public void startSearch(String initialQuery, boolean selectInitialQuery,
			Bundle appSearchData, boolean globalSearch) {

		// closeAllApps(true);

		Log.v("search", "startSearch");
		if (initialQuery == null) {
			// Use any text typed in the launcher as the initial query
			initialQuery = getTypedText();
			clearTypedText();
		}
		if (appSearchData == null) {
			appSearchData = new Bundle();
			appSearchData.putString("source", "launcher-search");
		}

		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchManager.startSearch(initialQuery, selectInitialQuery,
				getComponentName(), appSearchData, globalSearch);
	}

	@Override
	public boolean onSearchRequested() {
		startSearch(null, false, null, true);
		return true;
	}

	private boolean haskeyDown = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == android.view.KeyEvent.KEYCODE_ENVELOPE)
			return false;
		if (d3dListener == null || !d3dListener.bCreatDone)
			return true;
		haskeyDown = true;

		boolean handled = false;
		// if (!handled && acceptFilter() && keyCode != KeyEvent.KEYCODE_ENTER)
		// {
		// boolean gotKey = TextKeyListener.getInstance().onKeyDown(mWorkspace,
		// mDefaultKeySsb,
		// keyCode, event);
		// if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0)
		// {
		// // something usable has been typed - start a search
		// // the typed text will be retrieved and cleared by
		// // showSearchDialog()
		// // If there are multiple keystrokes before the search dialog takes
		// focus,
		// // onSearchRequested() will be called for every keystroke,
		// // but it is idempotent, so it's fine.
		// return onSearchRequested();
		// }
		// }
		final FolderIcon3D findFolder = d3dListener.getOpenFolder();
		if (DefConfig.DEF_S3_SUPPORT) {
			showIntroduction = false;
		}
		if ((((keyCode >= KeyEvent.KEYCODE_0) && (keyCode <= KeyEvent.KEYCODE_9))
				|| keyCode == KeyEvent.KEYCODE_STAR
				|| keyCode == KeyEvent.KEYCODE_POUND)
				&& findFolder == null) {
			Log.v("search", "keydown");
			Log.v("search", "on search");
			return onSearchRequested();
		} else if (keyCode == KeyEvent.KEYCODE_MENU && findFolder != null
				&& findFolder.mInfo.opened == true) {
			this.postRunnable(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// zhujieping add
					if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
							|| DefaultLayout.miui_v5_folder) {
						if (findFolder.mFolderMIUI3D.bEnableTouch == true) {
							findFolder.mFolderMIUI3D.DealButtonOKDown();
						}
					} else if (findFolder.mFolder.bEnableTouch == true) {
						findFolder.mFolder.DealButtonOKDown();
					}
				}
			});
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU && findFolder != null
				&& findFolder.folderIconPath != null) {
			this.postRunnable(new Runnable() {
				@Override
				public void run() {
					findFolder.folderIconPath.DealButtonOKDown();
				}

			});
			return true;
		}
		// Eat the long press event so the keyboard doesn't come up.
		if (event.isLongPress()) {
			return true;
		}

		return handled;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == android.view.KeyEvent.KEYCODE_ENVELOPE)
			return false;
		if (d3dListener == null || !d3dListener.bCreatDone)
			return true;
		if (haskeyDown) {
			haskeyDown = false;
		} else {
			return false;
		}

		// wanghongjian add start //enable_DefaultScene
		if (d3dListener.freeMainVisible()) {
			return true;
		}
		// wanghongjian add end

		if (keyCode == KeyEvent.KEYCODE_MENU && !d3dListener.isAllAppsVisible()
				&& !showIntroduction
				&& d3dListener.getWorkspace3D().isVisible()) {
			boolean canOpenSetupMenu = true;
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			if (curCellLayout != null) {
				for (int i = 0; i < curCellLayout.getChildCount(); i++) {
					if (curCellLayout.getChildAt(i) instanceof Widget3D) {
						Widget3D curWidget3D = (Widget3D) curCellLayout
								.getChildAt(i);
						if (curWidget3D.isOpened()) {
							canOpenSetupMenu = false;
							break;
						}
					}
				}
			}
			if (canOpenSetupMenu) {
				mSetupMenu.PopSetupMenu(/* findViewById(R.id.workspace) */);
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& DefaultLayout.show_page_edit_on_key_back
				&& !d3dListener.isAllAppsVisible() && !showIntroduction
				&& d3dListener.getWorkspace3D().isVisible()) {
			boolean canOpenPageEdit = true;
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			for (int i = 0; i < curCellLayout.getChildCount(); i++) {
				if (curCellLayout.getChildAt(i) instanceof Widget3D) {
					Widget3D curWidget3D = (Widget3D) curCellLayout
							.getChildAt(i);
					if (curWidget3D.isOpened()) {
						canOpenPageEdit = false;
						break;
					}
				}
			}
			if (canOpenPageEdit) {
				Workspace3D workspace = d3dListener.getWorkspace3D();
				workspace.getParent().onCtrlEvent(workspace,
						Workspace3D.MSG_PAGE_SHOW_EDIT);
				// Gdx.graphics.requestRendering();
				return true;
			}
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.v("launcher", "onConfigurationChanged:" + newConfig);
		Utils3D.resetSize();
		Log.v("launcher", "utils3d width:" + Utils3D.getScreenWidth()
				+ " height:" + Utils3D.getScreenHeight());
		if (DefaultLayout.restartWhenOrientationChange) {
			super.onConfigurationChanged(newConfig);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		if (currentLocale != null) {
			if (!currentLocale.equals(newConfig.locale.toString())) {
				Log.v("launcher", "restartSystem:" + currentLocale + ","
						+ newConfig.locale);
				// currentFontScale = newConfig.fontScale;
				currentLocale = newConfig.locale.toString();
				super.onConfigurationChanged(newConfig);
				SystemAction.RestartSystem();
				return;
			}
		}
		// currentFontScale = newConfig.fontScale;
		currentLocale = newConfig.locale.toString();
		super.onConfigurationChanged(newConfig);
	}

	private void restoreState(Bundle savedState) {

		if (savedState == null) {
			return;
		}

		mRestoring = true;

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.v("iLoongLauncher", "Launcher onNewIntent() start");
		super.onNewIntent(intent);

		// if (d3dListener.getRoot() != null)
		if (Desktop3DListener.bCreatDone) {
			CellLayout3D curCellLayout = d3dListener.getCurrentCellLayout();
			if (curCellLayout != null) {
				for (int i = 0; i < curCellLayout.getChildCount(); i++) {
					if (curCellLayout.getChildAt(i) instanceof Widget3D) {
						Widget3D curWidget3D = (Widget3D) curCellLayout
								.getChildAt(i);
						if (curWidget3D.isOpened()) {
							curWidget3D.onKeyEvent(KeyEvent.KEYCODE_HOME,
									KeyEvent.ACTION_UP);
							break;
						}
					}
				}
			}
			d3dListener.getRoot().getAppHost().appList
					.setMode(AppList3D.APPLIST_MODE_NORMAL);
			if (d3dListener.getRoot().getAppHost().popMenu2 != null) {
				d3dListener.getRoot().getAppHost().popMenu2.reset();
			}
		}
		if (Intent.ACTION_MAIN.equals(intent.getAction())) {
			// also will cancel mWaitingForResult.
			getWindow().closeAllPanels();

			final View v = getWindow().peekDecorView();
			if (v != null && v.getWindowToken() != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

			boolean alreadyOnHome = ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			d3dListener.onHomeKey(alreadyOnHome);

			Log.d("launcher", "alreadyOnHome=" + alreadyOnHome);
		}
		if (airpush != null)
			airpush.onNewIntent(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_HOME:
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				break;
			case KeyEvent.KEYCODE_BACK:
				if (introductionShown) {
					dismissIntroduction();
					return true;
				} else if (ClingManager.getInstance().hide())
					return true;
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_HOME:
				return true;
			}
		}

		return super.dispatchKeyEvent(event);
	}

	/**
	 * Prints out out state for debugging.
	 */
	public void dumpState() {
		Log.d(TAG, "BEGIN launcher2 dump state for launcher " + this);
		Log.d(TAG, "mSavedState=" + mSavedState);
		Log.d(TAG, "mWorkspaceLoading=" + mWorkspaceLoading);
		Log.d(TAG, "mRestoring=" + mRestoring);
		Log.d(TAG, "mWaitingForResult=" + mWaitingForResult);
		Log.d(TAG, "mSavedInstanceState=" + mSavedInstanceState);
		// Log.d(TAG, "sFolders.size=" + sFolders.size());
		mModel.dumpState();
		Log.d(TAG, "END launcher2 dump state");
	}

	/**
	 * Refreshes the shortcuts shown on the workspace.
	 * 
	 * Implementation of the method from LauncherModel.Callbacks.
	 */
	public void startBinding() {
		final Workspace workspace = xWorkspace;
		int count = workspace.getChildCount();
		for (int i = 0; i < count; i++) {
			// Use removeAllViewsInLayout() to avoid an extra requestLayout()
			// and invalidate().
			((ViewGroup) workspace.getChildAt(i)).removeAllViewsInLayout();
		}
	}

	public void removeFolder(FolderInfo folder) {
		sFolders.remove(folder.id);
	}

	public UserFolderInfo getFolderInfo(long folderId) {
		return (UserFolderInfo) sFolders.get(folderId);
	}

	private boolean findBrowserByPackageName(String packageName) {
		if (packageName.equals("com.android.browser")
				|| packageName.equals("com.uc.browser")
				|| packageName.equals("com.google.android.browser")
				|| packageName.equals("com.skymobi.browser")
				|| packageName.equals("com.android.browser.BrowserActivity")
				|| packageName.equals("com.uc.browser.hd")
				|| packageName.equals("com.UCMobile")
				|| packageName.equals("com.tencent.mtt")
				|| packageName.equals("com.tencent.qbx")
				|| packageName.equals("com.opera.mini.android")
				|| packageName.equals("com.opera.browser")
				|| packageName.equals("com.oupeng.mini.android")
				|| packageName.equals("com.oupeng.mobile")
				|| packageName.equals("com.mx.browser")
				|| packageName.equals("com.android.chrome")
				|| packageName.equals("org.mozilla.firefox")
				|| packageName.equals("com.qihoo.browser")
				|| packageName.equals("com.qihoo.padbrowser")
				|| packageName.equals("com.baidu.browser.apps")
				|| packageName.equals("cn.dolphin.browser")
				|| packageName.equals("com.dolphin.browser.cn")
				|| packageName.equals("com.dolphin.browser.engine")
				|| packageName.equals("com.dolphin.browser.android.pad.cn")) {
			return true;
		}
		return false;
	}

	public boolean startActivitySafely(Intent intent, Object tag) {
		if (DefaultLayout.show_missed_call_sms) {
			if (getMissedCallNum() > 0) {
				if (intent != null
						&& intent.getComponent() == null
						&& intent.getAction() != null
						&& intent.getAction().equals(
								"android.intent.action.DIAL")
						|| intent != null
						&& intent.getComponent() != null
						&& intent.getComponent().getPackageName() != null
						&& intent.getComponent().getClassName() != null
						&& intent.getComponent().getPackageName()
								.equals("com.android.contacts")
						&& intent.getComponent().getClassName()
								.equals(DefaultLayout.call_component_name)) {
					Intent tempIntent = new Intent();
					tempIntent.setAction(Intent.ACTION_CALL_BUTTON);
					tempIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						startActivity(tempIntent);
						return true;
					} catch (ActivityNotFoundException e) {
						Toast.makeText(this, RR.string.activity_not_found,
								Toast.LENGTH_SHORT).show();
						Log.e(TAG, "Unable to launch. tag=" + tag + " intent="
								+ intent, e);
					} catch (SecurityException e) {
						Toast.makeText(this, RR.string.activity_not_found,
								Toast.LENGTH_SHORT).show();
						Log.e(TAG,
								"Launcher does not have the permission to launch "
										+ intent
										+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
										+ "or use the exported attribute for this activity. "
										+ "tag=" + tag + " intent=" + intent, e);
					}
					return false;
				}
			}
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			String url = DefaultLayout.defaultUri;
			if (DefaultLayout.mainmenu_explorers_use_default_uri && url != null) {
				if (intent != null && intent.getComponent() != null) {
					ComponentName cn = intent.getComponent();
					String packageName = cn.getPackageName();
					if (findBrowserByPackageName(packageName)) {
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						intent.setData(Uri.parse(url));
					}
				}
			}

			startActivity(intent);
			if (intent != null && intent.getComponent() != null) {
				int useFrequency = PreferenceManager
						.getDefaultSharedPreferences(
								iLoongLauncher.getInstance())
						.getInt("FREQUENCY:" + intent.getComponent().toString(),
								0);
				PreferenceManager
						.getDefaultSharedPreferences(
								iLoongLauncher.getInstance())
						.edit()
						.putInt("FREQUENCY:" + intent.getComponent().toString(),
								useFrequency + 1).commit();
				// Log.d("launcher", "intent,frequency="
				// + intent.getComponent().toString() + ","
				// + (useFrequency + 1));
			}

			return true;
		} catch (ActivityNotFoundException e) {
			ShortcutInfo mShortcutInfo = (ShortcutInfo) tag;
			if (mShortcutInfo.intent.getBooleanExtra("isApplistVirtualIcon",
					false) == true) {
				File file = new File(DownloadLockBoxService.DOWNLOAD_PATH,
						mShortcutInfo.title.toString() + ".apk");
				if (file.exists()) {
					Intent intent2 = new Intent();
					intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent2.setAction(android.content.Intent.ACTION_VIEW);
					intent2.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					startActivity(intent2);
				} else
					downloadAPK(this, this.getPackageName(),
							mShortcutInfo.appInfo.packageName,
							mShortcutInfo.title.toString());
				return true;
			}

			Toast.makeText(this, RR.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
		} catch (SecurityException e) {
			Toast.makeText(this, RR.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG,
					"Launcher does not have the permission to launch "
							+ intent
							+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
							+ "or use the exported attribute for this activity. "
							+ "tag=" + tag + " intent=" + intent, e);
		}
		return false;
	}

	public static void downloadAPK(Context context, String srcPackageName,
			String destPackageName, String fileName) {
		Intent intent = new Intent();
		intent.setClass(context, DownloadLockBoxService.class);
		intent.putExtra(DownloadLockBoxService.DOWNLOAD_FILE_NAME, fileName);
		intent.putExtra(DownloadLockBoxService.DOWNLOAD_URL_KEY,
				DownModule.getApkUrl(context, srcPackageName, destPackageName));
		Intent intentGetToDownloadAPKName = new Intent(
				"com.coco.lock2.lockbox.GetToDownloadAPKName");
		if (destPackageName.equals("com.coco.launcher")) {
			intent.putExtra("logo", "cocolauncher");
		} else if (destPackageName.equals("com.coco.lock2.lockbox")) {
			intent.putExtra("logo", "cocolock");
		}
		intentGetToDownloadAPKName.putExtra("ToDownloadAPKName",
				destPackageName);
		context.sendBroadcast(intentGetToDownloadAPKName);

		context.startService(intent);
	}

	public boolean startActivitySafelyFromIntent(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, RR.string.activity_not_found_from_intent,
					Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, RR.string.activity_not_found_from_intent,
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	public void showRenameDialog(FolderInfo info) {
		mFolderInfo = info;
		mWaitingForResult = true;
		showDialog(DIALOG_RENAME_FOLDER);
	}

	// public void showAddDialog(CellLayout.CellInfo cellInfo) {
	// mAddItemCellInfo = cellInfo;
	// mWaitingForResult = true;
	// showDialog(DIALOG_CREATE_SHORTCUT, null);
	// }

	public void showAddShortcutDialog() {
		showDialog(DIALOG_CREATE_SHORTCUT);

		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			sensorListenerPause();
		}
		// xiatian add end

	}

	public void showAddSystemWidget() {
		mWaitingForResult = true;
		showDialog(DIALOG_CREATE_SYSTEM_WIDGET, null);

		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			sensorListenerPause();
		}
		// xiatian add end

	}

	private int mCurrentDialogId = 0;
	private int[] dropPos = new int[2];

	@Override
	protected Dialog onCreateDialog(int id) {

		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			sensorListenerPause();
		}
		// xiatian add end

		mCurrentDialogId = id;
		switch (id) {
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
			return new SortApp().createDialog(sortAppCheckId);
		case DIALOG_PICK_3DWIDGET:
			return new CreateShortcut().creagePick3DWidgetDialog();
		case DIALOG_APK_CANNOT_FOUND:
			return new ApkCannotFound().createDialog();
		case DIALOG_RESTART:
			return new RestartDialog().createDialog();
		case DIALOG_APP_EFFECT:
			return new AppEffectDialog().createDialog();
		case DIALOG_DESKTOP_EFFECT:
		    return new DesktopEffectDialog().createDialog();
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_CREATE_SHORTCUT:
			break;
		case DIALOG_RENAME_FOLDER:

			if (mFolderInfo != null) {
				EditText input = (EditText) dialog
						.findViewById(RR.id.folder_name);
				String title = (String) mFolderInfo.title;
				if (title.endsWith("x.z")) {
					int length = title.length();
					if (length > 3) {
						mFolderInfo.title = title.substring(0, length - 3);
					}

				}
				final CharSequence text = mFolderInfo.title;
				input.setText(text);
				// input.setSelection(0, text.length());
				input.setSelection(text.length());
			}

			break;
		case DIALOG_APP_EFFECT:
			ListView listView = (ListView) dialog.findViewById(0);
			String initValue = PreferenceManager.getDefaultSharedPreferences(
					SetupMenu.getContext()).getString(
					SetupMenu.getKey(RR.string.setting_key_appeffects), "2");
			Log.e("effect", "init=" + initValue);
			listView.setItemChecked(Integer.parseInt(initValue), true);
			break;
		case DIALOG_DESKTOP_EFFECT:
		    delaySetupMenu=true;
		    break;
		}
	}

	private void pickShortcut(int requestCode, int title) {

		// Bundle bundle = new Bundle();

		// ArrayList<String> shortcutNames = new ArrayList<String>();
		// shortcutNames.add(getString(RR.string.group_applications));
		// bundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, shortcutNames);
		//
		// ArrayList<ShortcutIconResource> shortcutIcons = new
		// ArrayList<ShortcutIconResource>();
		// shortcutIcons.add(ShortcutIconResource.fromContext(iLoongLauncher.this,
		// RR.drawable.ic_launcher_application));
		// bundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		// shortcutIcons);

		Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
		pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(
				Intent.ACTION_CREATE_SHORTCUT));
		pickIntent.putExtra(Intent.EXTRA_TITLE, getText(title));
		// pickIntent.putExtras(bundle);

		startActivityForResult(pickIntent, requestCode);
	}

	public void displaySystemWidget() {
		int appWidgetId = mAppWidgetHost.allocateAppWidgetId();

		Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// start the pick activity
		// Field f=
		// (Field)RR.drawable.class.getDeclaredField(getItem(position).mValue);
		// int i=f.getInt(RR.drawable.class);

		{
			widget3DArray = new AddWidget3DListAdapter(iLoongLauncher.this);
			if (widget3DArray.getCount() != 0) {
				ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
				ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
				for (int i = 0; i < widget3DArray.getCount(); i++) {
					ListItem item = (ListItem) widget3DArray.getItem(i);

					if (item.installed) {
						AppWidgetProviderInfo info = new AppWidgetProviderInfo();
						info.provider = new ComponentName(
								item.resolveInfo.activityInfo.packageName,
								item.resolveInfo.activityInfo.packageName);
						info.icon = item.resolveInfo.getIconResource();
						info.label = item.text.toString();
						// if (item.resolveInfo.activityInfo.packageName
						// .equals("com.iLoong.Robot")) {
						// if (item.actionTag ==
						// AddWidget3DListAdapter.ITEM_THEME_DEFAULT_ICON) {
						// info.icon = RR.drawable.applistrobot;
						// } else {
						// info.icon = RR.drawable.applistrobot_female;
						// }
						// } else if (item.resolveInfo.activityInfo.packageName
						// .equals("com.iLoong.Clock")) {
						// if (item.actionTag ==
						// AddWidget3DListAdapter.ITEM_THEME_DEFAULT_ICON) {
						// info.icon = RR.drawable.applistclock;
						// } else {
						// info.icon = RR.drawable.applistclock_female;
						// }
						//
						// }
						// if (item.resolveInfo.activityInfo.packageName
						// .equals("com.iLoong.Memo")) {
						// info.icon = RR.drawable.applistmemo;
						// }
						// if (item.resolveInfo.activityInfo.packageName
						// .equals("com.iLoong.Knife")) {
						// info.icon = RR.drawable.applistknife;
						// }
						// if (item.resolveInfo.activityInfo.packageName
						// .equals("com.TradClock")) {
						// info.icon = RR.drawable.xljclock;
						// }
						// if (item.resolveInfo.activityInfo.packageName
						// .equals("com.CoCo.Trad")) {
						// info.icon = RR.drawable.xljrobot;
						// }
						customInfo.add(info);

						Bundle b = new Bundle();
						b.putString(EXTRA_CUSTOM_WIDGET,
								item.resolveInfo.activityInfo.packageName);
						customExtras.add(b);
					} else {
						// AppWidgetProviderInfo info = new
						// AppWidgetProviderInfo();
						// if (item.isInternal) {
						// info.provider = new ComponentName(
						// iLoongLauncher.this.getPackageName(),
						// item.packageName);
						// info.icon = item.iconResourceId;
						// info.label = item.text.toString();
						// customInfo.add(info);
						// Bundle b = new Bundle();
						// b.putString(EXTRA_CUSTOM_WIDGET, item.packageName);
						// customExtras.add(b);
						//
						// }
						// info.provider = new
						// ComponentName(iLoongLauncher.this.getPackageName(),
						// item.packageName);
						// if
						// (item.packageName.equals("com.iLoong.Robot"))
						// {
						// info.icon=RR.drawable.applistrobot;
						// info.label =
						// iLoongLauncher.this.getResources().getString(RR.string.robot);
						// }
						// if
						// (item.packageName.equals("com.iLoong.Memo"))
						// {
						// info.icon=RR.drawable.applistnote;
						// info.label =
						// iLoongLauncher.this.getResources().getString(RR.string.memo);
						// }
						// if
						// (item.packageName.equals("com.iLoong.Clock"))
						// {
						// info.icon=RR.drawable.applistclock;
						// info.label =
						// iLoongLauncher.this.getResources().getString(RR.string.clock);
						// }
						continue;
					}

				}

				pickIntent.putParcelableArrayListExtra(
						AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
				pickIntent.putParcelableArrayListExtra(
						AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);

			}
		}
		pickIntent.putExtra(Intent.EXTRA_TITLE,
				getString(RR.string.choose_widget));
		startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);

	}

	public void test() {
		// iLoongLauncher.getInstance().postRunnable(new Runnable() {
		// @Override
		// public void run() {
		// final PluginManager manager = new PluginManager(iLoongLauncher
		// .getInstance());
		// manager.build();
		//
		// DexFileItem clockItem = manager.getDexFileItem("iLoongClock");
		//
		// String dexSource = clockItem.getTargetFileAbsolutePath();
		// String dexTarget = clockItem.getTargetFileAbsoluteDir();
		// final DexClassLoader classLoader = new DexClassLoader(
		// dexSource, dexTarget, null, getClassLoader());
		// PluginContext pluginContext = new PluginContext(iLoongLauncher
		// .getInstance(), dexSource, dexTarget, null);
		// final DexAppContext dexAppContext = new DexAppContext(
		// iLoongLauncher.getInstance(), pluginContext);
		// // TODO Auto-generated method stub
		// Class<?> clazz;
		// try {
		// clazz = classLoader
		// .loadClass("com.iLoong.Clock.iLoongClock");
		// IWidget3DPlugin plugin = (IWidget3DPlugin) clazz
		// .newInstance();
		// MainAppContext appContext = new MainAppContext(
		// iLoongLauncher.getInstance(), dexAppContext,
		// iLoongLauncher.getInstance(), null);
		// appContext.mThemeName = "iLoong";
		// WidgetPluginView3D pluginView = (WidgetPluginView3D) plugin
		// .getWidget(appContext, 1);
		// Widget3DRefreshRender refreshRender = new Widget3DRefreshRender();
		// pluginView.setRefreshRender(refreshRender);
		// Workspace3D workspace = d3dListener.getWorkspace3D();
		// Widget3D widget3D = new Widget3D("test", pluginView);
		// if (widget3D != null) {
		// Widget3DInfo info = widget3D.getItemInfo();
		// info.x = (int) (clickPoint.x - widget3D.width / 2);
		// info.y = (int) (clickPoint.y - widget3D.height / 2);
		// info.screen = workspace.getCurrentScreen();
		// workspace.addInScreen(widget3D, info.screen, info.x,
		// info.y, false);
		// // Root3D.addOrMoveDB(info);
		// }
		// Gdx.graphics.requestRendering();
		// // Toast.makeText(this, money.toString(),
		// // Toast.LENGTH_LONG).show();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// });

		iLoongLauncher.getInstance().postRunnable(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Class<?> clazz;
				try {
					clazz = iLoongLauncher.getInstance().getClass()
							.getClassLoader()
							.loadClass("com.iLoong.Clock.iLoongClock");
					IWidget3DPlugin plugin = (IWidget3DPlugin) clazz
							.newInstance();
					MainAppContext appContext = new MainAppContext(
							iLoongLauncher.getInstance(), iLoongLauncher
									.getInstance(), iLoongLauncher
									.getInstance(), null);
					appContext.mThemeName = "iLoong";
					WidgetPluginView3D pluginView = (WidgetPluginView3D) plugin
							.getWidget(appContext, 1);
					Widget3DRefreshRender refreshRender = new Widget3DRefreshRender();
					pluginView.setRefreshRender(refreshRender);
					Workspace3D workspace = d3dListener.getWorkspace3D();
					Widget3D widget3D = new Widget3D("test", pluginView);
					if (widget3D != null) {
						Widget3DInfo info = widget3D.getItemInfo();
						info.x = (int) (clickPoint.x - widget3D.width / 2);
						info.y = (int) (clickPoint.y - widget3D.height / 2);
						info.screen = workspace.getCurrentScreen();
						workspace.addInScreen(widget3D, info.screen, info.x,
								info.y, false);
						// Root3D.addOrMoveDB(info);
					}
					Gdx.graphics.requestRendering();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * Displays the shortcut creation dialog and launches, if necessary, the
	 * appropriate activity.
	 */
	private class CreateShortcut implements DialogInterface.OnClickListener,
			DialogInterface.OnCancelListener,
			DialogInterface.OnDismissListener, DialogInterface.OnShowListener {

		private AddListAdapter mAdapter;

		Dialog createDialog() {
			mAdapter = new AddListAdapter(iLoongLauncher.this);
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setTitle(getString(RR.string.setting_add_system_widget));
			builder.setAdapter(mAdapter, this);

			builder.setInverseBackgroundForced(true);

			AlertDialog dialog = builder.create();
			dialog.setOnCancelListener(this);
			dialog.setOnDismissListener(this);
			dialog.setOnShowListener(this);

			return dialog;
		}

		Dialog creagePick3DWidgetDialog() {
			Log.e("launcher", "create pick 3dWidget");
			final AddWidget3DListAdapter widget3DAdapter = new AddWidget3DListAdapter(
					iLoongLauncher.this);
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			if (widget3DAdapter.getCount() == 0) {
				builder.setTitle(RR.string.widget_empty_title);
				View layout = View.inflate(iLoongLauncher.this,
						RR.layout.widget_empty_dialog, null);
				TextView text = (TextView) layout
						.findViewById(RR.id.widget_empty_content);
				text.setText(RR.string.widget_empty_dialog);
				builder.setView(layout);
				builder.setPositiveButton(
						getString(RR.string.circle_ok_action),
						new Dialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								cleanup();
							}
						});
				// builder.setInverseBackgroundForced(true);

				AlertDialog alertDialog = builder.create();
				alertDialog.setOnCancelListener(this);
				alertDialog.setOnDismissListener(this);
				alertDialog.setOnShowListener(this);
				return alertDialog;
			} else {
				// final AlertDialog.Builder builder = new AlertDialog.Builder(
				// iLoongLauncher.this);
				builder.setTitle(getString(RR.string.chooser_3dwidget));
				builder.setAdapter(widget3DAdapter,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// TODO Auto-generated method stub
								ListItem item = (ListItem) widget3DAdapter
										.getItem(which);
								if (item.isInternal) {
									addInternal3DWidget(item.packageName);
								} else {
									if (item.installed) {
										add3DWidget(item.resolveInfo);
									} else {
										ShortcutInfo info = new ShortcutInfo();
										info.title = item.text;
										info.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW;
										info.intent = new Intent(
												Intent.ACTION_PACKAGE_INSTALL);
										info.intent
												.setComponent(new ComponentName(
														item.packageName,
														item.packageName));

										WidgetDownload.checkToDownload(info,
												true);
									}
								}

							}
						});
				builder.setInverseBackgroundForced(true);
				AlertDialog alertDialog = builder.create();
				alertDialog.setOnCancelListener(this);
				alertDialog.setOnDismissListener(this);
				alertDialog.setOnShowListener(this);
				return alertDialog;
			}
		}

		public void onCancel(DialogInterface dialog) {
			mWaitingForResult = false;
			cleanup();
		}

		public void onDismiss(DialogInterface dialog) {
			try {
				dismissDialog(DIALOG_CREATE_SHORTCUT);
				ShortcutDialogShow = false;
			} catch (Exception e) {
				// An exception is thrown if the dialog is not visible, which is
				// fine
				e.printStackTrace();
			}

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				if (mIsOnpauseAlready == false) {
					sensorListenerResume();
				} else {
					mIsOnpauseAlready = false;
				}
			}
			// xiatian add end

		}

		private void cleanup() {
			try {
				removeDialog(DIALOG_PICK_3DWIDGET);
			} catch (Exception e) {
				// An exception is thrown if the dialog is not visible, which is
				// fine
				e.printStackTrace();
			}
		}

		/**
		 * Handle the action clicked in the "Add to home" dialog.
		 */
		public void onClick(DialogInterface dialog, int which) {
			Resources res = getResources();
			cleanup();
			which = mAdapter.getSelectId(which);
			switch (which) {
			case AddListAdapter.ITEM_3D_WIDGET: {
				showDialog(DIALOG_PICK_3DWIDGET);
				break;
			}
			case AddListAdapter.ITEM_CONTACT: {
				iLoongLauncher.this.postRunnable(new Runnable() {

					@Override
					public void run() {
						add3DContact();
					}

				});

				break;
			}
			case AddListAdapter.ITEM_APPWIDGET: {

				displaySystemWidget();
				break;
			}
			// folder is not in add list
			case AddListAdapter.ITEM_LIVE_FOLDER: {
				// Insert extra item to handle inserting folder
				iLoongLauncher.this.postRunnable(new Runnable() {

					@Override
					public void run() {
						add3DFolder();
					}

				});
				break;
			}
			case AddListAdapter.ITEM_SHORTCUT: {
				// Insert extra item to handle picking application
				// dropPos[0] = -1;
				// dropPos[1] = -1;
				pickShortcut(REQUEST_PICK_SHORTCUT,
						RR.string.title_select_shortcut);
				break;
			}

			case AddListAdapter.ITEM_WALLPAPER: {
				startWallpaper();
				break;
			}
			}
		}

		public void onShow(DialogInterface dialog) {
			ShortcutDialogShow = true;
			mWaitingForResult = true;

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerPause();
			}
			// xiatian add end

		}
	}

	private void registerContentObservers() {
		ContentResolver resolver = getContentResolver();
		resolver.registerContentObserver(
				LauncherProvider.CONTENT_APPWIDGET_RESET_URI, true,
				mWidgetObserver);
	}

	private void start3DWidget() {
		// closeAllApps(true);
		final Intent pick3dWidget = new Intent();
		Intent chooser = Intent.createChooser(pick3dWidget,
				getText(RR.string.chooser_3dwidget));
		// NOTE: Adds a configure option to the chooser if the wallpaper
		// supports it
		// Removed in Eclair MR1
		// WallpaperManager wm = (WallpaperManager)
		// getSystemService(Context.WALLPAPER_SERVICE);
		// WallpaperInfo wi = wm.getWallpaperInfo();
		// if (wi != null && wi.getSettingsActivity() != null) {
		// LabeledIntent li = new LabeledIntent(getPackageName(),
		// RR.string.configure_wallpaper, 0);
		// li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
		// chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
		// }
		startActivityForResult(chooser, REQUEST_PICK_3DWIDGET);
	}

	private void addInternal3DWidget(final String packageName) {
		final String className = DefaultLayout
				.getWidgetItemClassName(packageName);
		iLoongLauncher.getInstance().postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				Widget3D widget3D = Widget3DManager.getInstance().getWidget3D(
						packageName, className);
				if (widget3D != null) {
					info = widget3D.getItemInfo();
					info.x = (int) (clickPoint.x - widget3D.width / 2);
					info.y = (int) (clickPoint.y - widget3D.height / 2);
					info.screen = workspace.getCurrentScreen();
					workspace.addInScreen(widget3D, info.screen, info.x,
							info.y, false);
					// Root3D.addOrMoveDB(info);
				}
			}
		});
	}

	private void add3DWidget(final ResolveInfo resolveInfo) {
		// test();
		iLoongLauncher.getInstance().postRunnable(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				ItemInfo info = null;
				Workspace3D workspace = d3dListener.getWorkspace3D();
				Widget3D widget3D = Widget3DManager.getInstance().getWidget3D(
						resolveInfo);
				if (widget3D != null) {
					info = widget3D.getItemInfo();
					info.x = (int) (clickPoint.x - widget3D.width / 2);
					info.y = (int) (clickPoint.y - widget3D.height / 2);
					info.screen = workspace.getCurrentScreen();
					workspace.addInScreen(widget3D, info.screen, info.x,
							info.y, false);
					// Root3D.addOrMoveDB(info);
				}
			}
		});
	}

	public void startWallpaper() {

		// xiatian add start //New Requirement 20130507
		if (startWallpaperBox() == true) {
			return;
		}
		// xiatian add end

		// closeAllApps(true);
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		if (DefaultLayout.wallpaper_has_edage) {
			startActivity(pickWallpaper);
			return;
		}
		Intent chooser = Intent.createChooser(pickWallpaper,
				getText(RR.string.chooser_wallpaper));
		// NOTE: Adds a configure option to the chooser if the wallpaper
		// supports it
		// Removed in Eclair MR1
		// WallpaperManager wm = (WallpaperManager)
		// getSystemService(Context.WALLPAPER_SERVICE);
		// WallpaperInfo wi = wm.getWallpaperInfo();
		// if (wi != null && wi.getSettingsActivity() != null) {
		// LabeledIntent li = new LabeledIntent(getPackageName(),
		// RR.string.configure_wallpaper, 0);
		// li.setClassName(wi.getPackageName(), wi.getSettingsActivity());
		// chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { li });
		// }
		startActivity(chooser);
	}

	void startActivityForResultSafely(Intent intent, int requestCode) {
		try {
			startActivityForResult(intent, requestCode);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, RR.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, RR.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG,
					"Launcher does not have the permission to launch "
							+ intent
							+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
							+ "or use the exported attribute for this activity.",
					e);
		}
	}

	private void dealAdd3DWidget(String WidgetpkgName) {
		if (widget3DArray.getCount() == 0) {
			return;
		}
		// String pkgName = data.getComponent().getPackageName();
		// String WidgetpkgName = data.getComponent().getClassName();
		for (int i = 0; i < widget3DArray.getCount(); i++) {
			ListItem item = (ListItem) widget3DArray.getItem(i);
			if (item.isInternal) {
				if (item.packageName.equals(WidgetpkgName)) {
					addInternal3DWidget(item.packageName);
				}
			} else {
				if (widget3DArray.getItemResolveInfo(i) == null) {
					if (item.packageName.equals(WidgetpkgName)
							&& item.installed == false) {
						ShortcutInfo info = new ShortcutInfo();
						info.title = item.text;
						info.intent = new Intent(Intent.ACTION_PACKAGE_INSTALL);
						info.intent.setComponent(new ComponentName(
								item.packageName, item.packageName));

						WidgetDownload.checkToDownload(info, true);
						return;
					}

				}

				else if (widget3DArray.getItemResolveInfo(i).activityInfo.packageName
						.equals(WidgetpkgName)) {
					if (item.installed) {
						add3DWidget(item.resolveInfo);
					}
					return;
				}
			}
		}
	}

	public void addAppWidgetFromPick(Intent data) {
		int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				-1);
		{
			String customWidget = data.getStringExtra(EXTRA_CUSTOM_WIDGET);
			if (customWidget != null) {
				dealAdd3DWidget(customWidget);
				return;
			}
		}
		addAppWidgetImpl(appWidgetId);
	}

	public void addAppWidgetFromDrop(ComponentName componentName, int[] loc) {
		dropPos[0] = loc[0];
		dropPos[1] = loc[1];
		clickPoint.x = loc[0];
		clickPoint.y = loc[1];
		int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
		AppWidgetManager.getInstance(this).bindAppWidgetId(appWidgetId,
				componentName);
		addAppWidgetImpl(appWidgetId);
		// Intent pickIntent = new
		// Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		// int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
		// pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
		// appWidgetId);
		// pickIntent.putExtra(AppWidgetManager.META_DATA_APPWIDGET_PROVIDER,
		// componentName);
		// startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
	}

	void addAppWidgetImpl(int appWidgetId) {
		// TODO: catch bad widget exception when sent

		AppWidgetProviderInfo appWidget = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);

		if (appWidget.configure != null) {
			// Launch over to configure widget, if needed
			Intent intent = new Intent(
					AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
			intent.setComponent(appWidget.configure);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

			startActivityForResultSafely(intent, REQUEST_CREATE_APPWIDGET);
		} else {
			// Otherwise just add it
			completeAddAppWidget(appWidgetId);
		}
	}

	public void AddSystemWidget() {
		Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
		int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
		pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
	}

	void processShortcutFromDrop(ComponentName componentName, int x, int y) {
		// resetAddInfo();
		// mPendingAddInfo.container = container;
		// mPendingAddInfo.screen = screen;
		// mPendingAddInfo.dropPos = loc;
		//
		// if (cell != null) {
		// mPendingAddInfo.cellX = cell[0];
		// mPendingAddInfo.cellY = cell[1];
		// }
		dropPos[0] = x;
		dropPos[1] = y;

		Intent createShortcutIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		createShortcutIntent.setComponent(componentName);
		processShortcut(createShortcutIntent, REQUEST_CREATE_SHORTCUT_FROM_DROP);
	}

	void processShortcut(Intent intent, int request) {
		// Handle case where user selected "Applications"
		String applicationName = getResources().getString(
				RR.string.group_applications);
		String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

		if (applicationName != null && applicationName.equals(shortcutName)) {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
			mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

			Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
			pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
			pickIntent.putExtra(Intent.EXTRA_TITLE,
					getText(RR.string.title_select_application));
			startActivityForResultSafely(pickIntent, REQUEST_PICK_APPLICATION);
		} else {
			startActivityForResultSafely(intent, request);
		}
	}

	private void completeAddContactShortcut(Intent data) {
		final ShortcutInfo info = mModel.infoFromContactShortcutIntent(this,
				data);
		info.x = Utils3D.getScreenWidth() / 2;
		info.y = Utils3D.getScreenHeight() / 2;
		info.screen = d3dListener.getCurrentScreen();
		d3dListener.addShortcut(info);
	}

	public boolean completeAddShortcutFromDrop(Intent data, int screen, int x,
			int y, boolean toast) {
		final ShortcutInfo info = mModel.infoFromShortcutIntent(this, data);
		if (info == null) {
			return false;
		}
		info.x = Utils3D.getScreenWidth() / 2;
		info.y = Utils3D.getScreenHeight() / 2;
		info.screen = screen;

		if (!findSlot(info.screen, x, y, mCellCoordinates, 1, 1, toast)) {
			if (toast)
				Toast.makeText(this, getString(RR.string.no_space_add_icon),
						Toast.LENGTH_SHORT).show();
			if (info.mIcon != null && !info.mIcon.isRecycled())
				info.mIcon.recycle();
			return false;
		}

		info.cellX = mCellCoordinates[0];
		info.cellY = mCellCoordinates[1];
		Log.d("launcher", "cellX,cellY=" + info.cellX + "," + info.cellY);
		d3dListener.addShortcutFromDrop(info);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mWaitingForResult = false;

		Log.v("launcher", "onActivityResult requestCode=" + requestCode
				+ "resultCode=" + resultCode);

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_PICK_APPLICATION:
				// completeAddApplication(this, data);
				break;
			case REQUEST_PICK_SHORTCUT:
				processShortcut(data, REQUEST_CREATE_SHORTCUT_FROM_DROP);
				break;
			case REQUEST_CREATE_CONTACT_SHORTCUT:
				completeAddContactShortcut(data);
				break;
			case REQUEST_CREATE_SHORTCUT_FROM_DROP:
				completeAddShortcutFromDrop(data,
						d3dListener.getCurrentScreen(), dropPos[0], dropPos[1],
						true);
				break;
			case REQUEST_PICK_LIVE_FOLDER:
				// addLiveFolder(data);
				break;
			case REQUEST_CREATE_LIVE_FOLDER:
				// completeAddLiveFolder(data, mAddItemCellInfo);
				break;
			case REQUEST_PICK_APPWIDGET:
				addAppWidgetFromPick(data);
				// test();
				break;
			case REQUEST_CREATE_APPWIDGET:
				int appWidgetId = data.getIntExtra(
						AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
				completeAddAppWidget(appWidgetId);
				break;
			case REQUEST_PICK_WALLPAPER:
				break;
			}
		} else if ((requestCode == REQUEST_PICK_APPWIDGET || requestCode == REQUEST_CREATE_APPWIDGET)
				&& resultCode == RESULT_CANCELED && data != null) {
			int appWidgetId = data.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
			if (appWidgetId != -1) {
				mAppWidgetHost.deleteAppWidgetId(appWidgetId);
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
	private void completeAddAppWidget(int appWidgetId) {
		Widget2DInfo launcherInfo = new Widget2DInfo(appWidgetId);

		launcherInfo.screen = d3dListener.getCurrentScreen();

		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);
		launcherInfo.setInfo(appWidgetInfo.provider.getPackageName(),
				appWidgetInfo.provider.getClassName());
		Log.v("addAppWidget", "packageName=" + launcherInfo.getPackageName()
				+ " className=" + launcherInfo.getClassName());
		if (beyondMTKWidgetNum(launcherInfo.getPackageName())) {
			Toast.makeText(this, getString(RR.string.beyond_mtkwidget_num),
					Toast.LENGTH_SHORT).show();
			return;
		}
		int[] spans = CellLayout3D.rectToCell(appWidgetInfo.minWidth,
				appWidgetInfo.minHeight, null);
		if (spans[0] > 4)
			spans[0] = 4;
		if (launcherInfo.getPackageName()
				.equals("com.mediatek.appwidget.video") || spans[1] > 4)
			spans[1] = 4;

		launcherInfo.spanX = spans[0];
		launcherInfo.spanY = spans[1];

		launcherInfo.minWidth = appWidgetInfo.minWidth;
		launcherInfo.minHeight = appWidgetInfo.minHeight;

		launcherInfo.x = (int) (clickPoint.x - spans[0]
				* R3D.Workspace_cell_each_width / 2);
		launcherInfo.y = (int) (clickPoint.y - spans[1]
				* R3D.Workspace_cell_each_height / 2);
		if (appWidgetInfo.minWidth >= Utils3D.getScreenWidth() / 2)
			launcherInfo.x = 0;

		Widget widget = d3dListener.addAppWidget(launcherInfo);
		if (widget == null)
			return;
		if (!addAppWidget(launcherInfo, true))
			return;
		// launcherInfo.x = launcherInfo.cellX
		// * iLoongLauncher.WORKSPACE_CELL_WIDTH;
		// launcherInfo.y = Utils3D.getScreenHeight() - launcherInfo.cellY
		// * iLoongLauncher.WORKSPACE_CELL_HEIGHT - launcherInfo.spanY
		// * iLoongLauncher.WORKSPACE_CELL_HEIGHT;

		launcherInfo.hostView.setWidget(widget);
		// Root3D.addOrMoveDB(widget.getItemInfo());

	}

	public boolean beyondMTKWidgetNum(String packageName) {
		if (packageName.contains("com.mediatek.appwidget.video")) {
			View mtkWidgetView = xWorkspace.searchIMTKWidget(xWorkspace);
			if (mtkWidgetView != null)
				return true;
		}
		return false;
	}

	public void moveAppWidget(View3D view, int screen) {
		Widget widget = (Widget) view;

		Widget2DInfo launcherInfo = widget.getItemInfo();
		// int appWidgetId = launcherInfo.appWidgetId;
		// AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
		// .getAppWidgetInfo(appWidgetId);
		// final int[] xy = mCellCoordinates;
		// CellLayout layout = (CellLayout)
		// xWorkspace.getChildAt(d3dListener.getCurrentScreen());
		// int[] spans = layout.rectToCell(appWidgetInfo.minWidth,
		// appWidgetInfo.minHeight);
		// if (!findSlot(cellInfo, d3dListener.getCurrentScreen(),xy, spans[0],
		// spans[1])) {
		// d3dListener.moveAppWidget(view, launcherInfo.screen, x, y);
		// return;
		// }
		// launcherInfo.spanX = spans[0];
		// launcherInfo.spanY = spans[1];
		if (!launcherInfo.canMove)
			return;
		xWorkspace.removeWidget(widget, screen);
		xWorkspace.addInScreen(launcherInfo.hostView,
				d3dListener.getCurrentScreen(), launcherInfo.x, launcherInfo.y,
				launcherInfo.spanX, launcherInfo.spanY, false);
		if (xWorkspace.searchIMTKWidget(launcherInfo.hostView) != null) {
			xWorkspace.stopCoverMTKWidgetView();
		}
		// d3dListener.moveAppWidget(view, d3dListener.getCurrentScreen(), x,
		// y);
	}

	public boolean addAppWidget(Widget2DInfo launcherInfo, boolean force) {
		int appWidgetId = launcherInfo.appWidgetId;
		AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager
				.getAppWidgetInfo(appWidgetId);
		if (!mRestoring) {
			// Perform actual inflation because we're live
			launcherInfo.hostView = (WidgetHostView) mAppWidgetHost.createView(
					this, appWidgetId, appWidgetInfo);
			// launcherInfo.hostView.setWidget(widget);

			launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
			launcherInfo.hostView.setTag(launcherInfo);

			CellLayout layout = (CellLayout) xWorkspace
					.getChildAt(launcherInfo.screen);
			int[] spans = new int[] { launcherInfo.spanX, launcherInfo.spanY };

			// // Try finding open space on Launcher screen
			final int[] xy = mCellCoordinates;
			if (!force
					&& !findSlot(launcherInfo.screen, dropPos[0], dropPos[1],
							xy, spans[0], spans[1], true)) {
				Toast.makeText(this, getString(RR.string.no_space_add_icon),
						Toast.LENGTH_SHORT).show();
				if (appWidgetId != -1)
					mAppWidgetHost.deleteAppWidgetId(appWidgetId);
				return false;
			}
			// Build Launcher-specific widget info and save to databas
			launcherInfo.spanX = spans[0];
			launcherInfo.spanY = spans[1];
			// launcherInfo.cellX = xy[0];
			// launcherInfo.cellY = xy[1];
			xWorkspace.addInScreen(launcherInfo.hostView, launcherInfo.screen,
					launcherInfo.x, launcherInfo.y,
					/* xy[0], xy[1], */launcherInfo.spanX, launcherInfo.spanY,
					false);

			// this.mMainHandler.post(new Runnable(){
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// Intent intent=new Intent();
			// intent.setAction(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			// sendBroadcast(intent);
			// }
			//
			// });
		}
		return true;
	}

	private boolean findSlot(int screen, int x, int y, int[] xy, int spanX,
			int spanY, boolean toast) {
		Log.d("widget", "x,y,spanX,spanY=" + xy[0] + " " + xy[1] + " " + spanX
				+ " " + spanY);
		// if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {

		if (!d3dListener.findCellForSpan(screen, x, y, xy, spanX, spanY)) {
			return false;
		}
		// }
		return true;
	}

	public WidgetHost getWidgetHost() {
		return mAppWidgetHost;
	}

	public static void LauncherbindFolders(
			final HashMap<Long, FolderInfo> folders) {
		sFolders.clear();
		sFolders.putAll(folders);
	}

	// public UserFolderInfo buildFolderInfo(int x,int y)
	// {
	// UserFolderInfo folderInfo = new UserFolderInfo();
	// folderInfo.title = R3D.folder3D_name;
	// folderInfo.x=x;
	// folderInfo.y=y;
	// folderInfo.screen=getCurrentScreen();
	// Root3D.addOrMoveDB(folderInfo,LauncherSettings.Favorites.CONTAINER_DESKTOP);
	// sFolders.put(folderInfo.id, folderInfo);
	// return folderInfo;
	// }
	public UserFolderInfo addFolder(int x, int y) {
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = d3dListener.getCurrentScreen();
		folderInfo.x = x;
		folderInfo.y = y;
		// Update the model
		// LauncherModel.addItemToDatabase(this, folderInfo,
		// LauncherSettings.Favorites.CONTAINER_DESKTOP,
		// folderInfo.screen, x, y, -1, -1, false);
		// sFolders.put(folderInfo.id, folderInfo);
		return folderInfo;
	}

	public void addFolderInfoToSFolders(UserFolderInfo folderInfo) {
		if (folderInfo == null) {
			return;
		}
		UserFolderInfo findFolderInfo = getFolderInfo(folderInfo.id);
		if (findFolderInfo != null) {
			/* 已经加入，不需要重复加�? */
			return;
		}
		sFolders.put(folderInfo.id, folderInfo);

	}

	public UserFolderInfo addHotSeatFolder(int x, int y, int index) {
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = R3D.folder3D_name;
		folderInfo.screen = index;
		folderInfo.x = x;
		folderInfo.y = y;
		folderInfo.angle = HotSeat3D.TYPE_WIDGET;
		// Update the model
		LauncherModel.addItemToDatabase(this, folderInfo,
				LauncherSettings.Favorites.CONTAINER_HOTSEAT,
				folderInfo.screen, x, y, -1, -1, false);
		sFolders.put(folderInfo.id, folderInfo);
		return folderInfo;
	}

	public UserFolderInfo addFolder(String name) {
		UserFolderInfo folderInfo = new UserFolderInfo();
		folderInfo.title = name;
		// folderInfo.screen = d3dListener.getCurrentScreen();
		folderInfo.x = 0;
		folderInfo.y = 0;
		// Update the model
		// LauncherModel.addItemToDatabase(this, folderInfo,
		// LauncherSettings.Favorites.CONTAINER_DESKTOP,
		// folderInfo.screen, 0, 0, -1, -1, false);
		// sFolders.put(folderInfo.id, folderInfo);
		return folderInfo;
	}

	// void addLiveFolder(Intent intent) {
	// // Handle case where user selected "Folder"
	// String folderName = getResources().getString(RR.string.group_folder);
	// String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
	//
	// if (folderName != null && folderName.equals(shortcutName)) {
	// addFolder();
	// } else {
	// startActivityForResultSafely(intent, REQUEST_CREATE_LIVE_FOLDER);
	// }
	// }

	// private boolean findSingleSlot(CellLayout.CellInfo cellInfo) {
	// final int[] xy = new int[2];
	// if (findSlot(cellInfo, xy, 1, 1)) {
	// cellInfo.x = xy[0];
	// cellInfo.y = xy[1];
	// return true;
	// }
	// return false;
	// }

	// private boolean findSlot(CellLayout.CellInfo cellInfo, int[] xy, int
	// spanX, int spanY) {
	// // if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {
	// // boolean[] occupied = mSavedState != null ?
	// // mSavedState.getBooleanArray(RUNTIME_STATE_PENDING_ADD_OCCUPIED_CELLS)
	// // : null;
	// // cellInfo = mWorkspace.findAllVacantCells(occupied);
	// // if (!cellInfo.findCellForSpan(xy, spanX, spanY)) {
	// // Toast.makeText(this, getString(RR.string.out_of_space),
	// // Toast.LENGTH_SHORT).show();
	// // return false;
	// // }
	// // }
	// xy[0] = xWorkspace.mLastPointX;
	// xy[1] = xWorkspace.mLastPointY;
	// return true;
	// }
	//
	// void addFolder() {
	// UserFolderInfo folderInfo = new UserFolderInfo();
	// folderInfo.title = getText(RR.string.folder_name);
	//
	// CellLayout.CellInfo cellInfo = mAddItemCellInfo;
	// cellInfo.screen = xWorkspace.getCurrentScreen();
	// if (!findSingleSlot(cellInfo))
	// return;
	//
	// // Update the model
	// LauncherModel.addItemToDatabase(this, folderInfo,
	// LauncherSettings.Favorites.CONTAINER_DESKTOP,
	// xWorkspace.getCurrentScreen(), cellInfo.x, cellInfo.y, -1, -1, false);
	// sFolders.put(folderInfo.id, folderInfo);
	//
	// // FolderIcon newFolder = FolderIcon.fromXml(RR.layout.folder_icon, this,
	// // (ViewGroup) xWorkspace.getChildAt(xWorkspace.getCurrentScreen()),
	// folderInfo, mIconCache);
	//
	// // xWorkspace.addInCurrentScreen(newFolder, cellInfo.x, cellInfo.y, 1, 1,
	// false);
	// }

	// private void handleFolderClick(FolderIcon foldericon) {
	// if (!foldericon.mInfo.opened) {
	// closeFolder();
	// openFolder(foldericon);
	// } else {
	// Folder openFolder = foldericon.mFolder;
	// int folderScreen;
	// if (openFolder != null) {
	// folderScreen = xWorkspace.getScreenForView(openFolder);
	// // .. and close it
	// closeFolder(openFolder);
	// if (folderScreen != xWorkspace.getCurrentScreen()) {
	// // Close any folder open on the current screen
	// closeFolder();
	// // Pull the folder onto this screen
	// openFolder(foldericon);
	// }
	// }
	// }
	// }
	//
	// private void openFolder(FolderIcon foldericon) {
	// Folder openFolder = foldericon.mFolder;
	//
	// openFolder.setDragController(mDragController);
	// openFolder.setLauncher(this);
	//
	// foldericon.mInfo.opened = true;
	//
	// if (openFolder.getParent() == null) {
	// xWorkspace.addInScreen(openFolder, foldericon.mInfo.screen, 0, 0, 3, 4);
	// }
	//
	// openFolder.onOpen();
	// }
	//
	// private void closeFolder() {
	// Folder folder = xWorkspace.getOpenFolder();
	// if (folder != null) {
	// closeFolder(folder);
	// }
	// }
	//
	// public void closeFolder(Folder folder) {
	// folder.getInfo().opened = false;
	// ViewGroup parent = (ViewGroup) folder.getParent();
	// if (parent != null) {
	// parent.removeView(folder);
	// }
	// folder.onClose();
	// }

	public void removeAppWidget(Widget2DInfo launcherInfo) {
		launcherInfo.hostView = null;
	}

	public WidgetHost getAppWidgetHost() {
		return mAppWidgetHost;
	}

	// // set this item is selected or not
	// public void setItemSelected(View v, boolean b) {
	// Object obj = v.getTag();
	// if (obj instanceof ItemInfo) {
	// ItemInfo itemInfo = (ItemInfo) obj;
	// itemInfo.isSelected = b;
	// }
	// }

	private class RenameFolder {
		private EditText mInput;

		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.rename_folder, null);
			mInput = (EditText) layout.findViewById(RR.id.folder_name);
			// InputFilter[]ifp =mInput.getFilters();
			InputFilter[] filters = new InputFilter[1];
			filters[0] = new InputFilter.LengthFilter(11) {
				@Override
				public CharSequence filter(CharSequence source, int start,
						int end,

						Spanned dest, int dstart, int dend) {
					if (source.equals("\n")) {
						source = " ";
					}
					int destLen = dest.length();
					int srcLen = source.length();
					if (destLen + srcLen > 11) {
						Toast.makeText(mInstance,
								getString(RR.string.fold_name_too_long),
								Toast.LENGTH_SHORT).show();
						return "";
					}

					return source;

				}
			};
			mInput.setFilters(filters);
			// mInput.addTextChangedListener(new TextWatcher()
			//
			// {
			//
			// @Override
			// public void beforeTextChanged(CharSequence s, int start,
			// int count, int after) {
			// // TODO Auto-generated method stub
			// Log.d("testwatcher", "beforeTextChanged count="+count);
			// }
			//
			// @Override
			// public void onTextChanged(CharSequence s, int start,
			// int before, int count) {
			// // TODO Auto-generated method stub
			// Log.d("testwatcher", "onTextChanged count="+count);
			// if (s.length() >= 11) {
			// Toast.makeText(mInstance,
			// "文件夹名称超过最大长�?, Toast.LENGTH_SHORT)
			// .show();
			// }
			// }
			//
			// @Override
			// public void afterTextChanged(Editable s) {
			// // TODO Auto-generated method stub
			//
			// Log.d("testwatcher", "afterTextChanged count="+s.length());
			//
			// }
			//
			// }
			// );
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			builder.setTitle(getString(RR.string.rename_folder_title));
			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					renameFoldercleanup();
				}
			});
			builder.setNegativeButton(getString(RR.string.cancel_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							renameFoldercleanup();
						}
					});
			builder.setPositiveButton(getString(RR.string.rename_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							changeFolderName();
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					mWaitingForResult = true;
					mInput.requestFocus();
					// getWindow().setFlags(
					// WindowManager.LayoutParams.FLAG_FULLSCREEN,
					// WindowManager.LayoutParams.FLAG_FULLSCREEN);
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.showSoftInput(mInput, 0);
					// getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		// private View3D getViewForFolderInfo(FolderInfo mFolderInfo) {
		//
		// if (mFolderInfo instanceof UserFolderInfo)
		// {
		//
		// ItemInfo tempinfo;
		// CellLayout3D currentScreen = d3dListener.getCurrentCellLayout();
		// int count = currentScreen.getChildCount();
		// for (int i = 0; i < count; i++) {
		// View3D child = currentScreen.getChildAt(i);
		// if (child instanceof IconBase3D)
		// {
		// tempinfo=((IconBase3D) child).getItemInfo();
		// if (tempinfo instanceof UserFolderInfo)
		// {
		// if (tempinfo.id== mFolderInfo.id)
		// {
		// return child;
		// }
		// }
		// }
		// }
		// }
		// return null;
		// }
		// public void sendToRenderThread(final String name) {
		// Gdx.app.postRunnable(new Runnable(){
		// public void run() {
		// folderIcon.mFolder.setEditText(name);
		// }
		// });
		// }

		private void changeFolderName() {
			String name = mInput.getText().toString();
			name = name.trim();
			if (!TextUtils.isEmpty(name) && mFolderInfo != null) {
				// Make sure we have the right folder info
				mFolderInfo = sFolders.get(mFolderInfo.id);
				name = name.concat("x.z");
				mFolderInfo.title = name;
				LauncherModel.updateItemInDatabase(iLoongLauncher.this,
						mFolderInfo);
				// getViewForFolderInfo(mFolderInfo);
				// final FolderIcon3D
				// folderIcon=(FolderIcon3D)getViewForFolderInfo(mFolderInfo);
				if (folderIcon != null) {
					// zhujieping add
					if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
							|| DefaultLayout.miui_v5_folder) {
						if (name.endsWith("x.z")) {
							int length = name.length();
							if (length > 3) {
								name = name.substring(0, length - 3);
							}
						}
						folderIcon.mFolderMIUI3D.setEditText(name);
					} else {
						folderIcon.mFolder.setEditText(name);
					}
				}
				// sendToRenderThread(name);
				// Folder3D.getInstance().setEditText(name);
				// Folder3D.foldName = name;
				// if (mWorkspaceLoading) {
				// mModel.startLoader(iLoongLauncher.this, false);
				// } else {
				// // final FolderIcon folderIcon = (FolderIcon)
				// // // xWorkspace.getViewForTag(mFolderInfo);
				// // if (folderIcon != null) {
				// // folderIcon.mFolder.setEditText(name);
				// // getWorkspace().requestLayout();
				// Folder3D.foldName = name;
				// } else {
				// mWorkspaceLoading = true;
				// mModel.startLoader(iLoongLauncher.this, false);
				// }
				// }
			}
			renameFoldercleanup();
		}

	}

	public void renameFoldercleanup() {
		dismissDialog(DIALOG_RENAME_FOLDER);
		mWaitingForResult = false;
		folderIcon.bRenameFolder = false;
		mFolderInfo = null;
		folderIcon = null;

		// xiatian add start //Widget3D adaptation "Naked eye 3D"
		if (DefaultLayout.show_sensor) {
			sensorListenerResume();
		}
		// xiatian add end

	}

	public void showProgressDialog() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(iLoongLauncher.getInstance());
		boolean restart = prefs.getBoolean(
				iLoongLauncher.RUNTIME_STATE_RESTART, false);
		prefs.edit().putBoolean(iLoongLauncher.RUNTIME_STATE_RESTART, false)
				.commit();
		if (restart) {
			// ImageView iv = new ImageView(this);
			// iv.setImageResource(RR.drawable.cache);
			// this.addContentView(iv, this.createLayoutParams());
			return;
		}
	}
   

	public void finishLoad() {

		// if(DefaultLayout.enable_shell) //xiatian del //change the way to read
		// the switch of CooeeShellSDK
		if (new ConfigBase().isEnableShellSDK(this)) // xiatian add //change the
														// way to read the
														// switch of
														// CooeeShellSDK
		{
			Log.v("shell", "init shell");
			CooeeSdk.initCooeeSdk(this);
		}
		MediaCache.getInstance().initData();
		ClingManager.getInstance().fireAllAppCling(
				d3dListener.getHotDockGroup());
		// Debug.stopMethodTracing();
		ApkConfig apkConfig = new ApkConfig();
		if (apkConfig.apkConfigRight() == false) {
			SendMsgToAndroid.sendCreateRestartDialogMsg();
		} else {
			installAssertAPK = true;
			if (installAssertAPK) {
				installAssertAPK();
				installAssertAPK = false;
			}
		}
//		if (launchStatistics != null)
//			launchStatistics.onLaunchFinish();

		// removed by zhenNan.ye 
//		if ((DefaultLayout.enable_air_default_layout)
//				&& (DefaultLayout.is_demo_version == false)) {
//			AirDefaultLayout.getInstance().start(this);
//		}

	}

	private void installAssertAPK() {
		final File fileDir = getFilesDir();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Utils3D.showTimeFromStart("wait install");
					Thread.sleep(DefaultLayout.install_apk_delay * 1000);
					Utils3D.showTimeFromStart("start install");
				} catch (InterruptedException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}
				String[] apks = null;
				apks = ThemeManager.getInstance().listAssetFiles("theme/apk");
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				PackageManager pm = getPackageManager();
				for (int i = 0; i < apks.length; i++) {
					final String apkName = apks[i];
					File tmp = new File(fileDir.getAbsolutePath() + "/"
							+ apkName);
					if (tmp.exists())
						tmp.delete();
					boolean hasInstall = prefs.getBoolean(apkName, false);
					if (hasInstall) {
						Log.e("apk", "have install:" + apkName);
						continue;
					}
					try {
						tmp.createNewFile();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					FileOutputStream fos = null;
					BufferedInputStream bis = null;
					int BUFFER_SIZE = 1024;
					byte[] buf = new byte[BUFFER_SIZE];
					int size = 0;
					try {
						// bis = new BufferedInputStream(iLoongLauncher.this
						// .getAssets().open("apk/" + apkName));
						bis = new BufferedInputStream(ThemeManager
								.getInstance().getInputStream("apk/" + apkName));
						fos = new FileOutputStream(tmp);
						while ((size = bis.read(buf)) != -1)
							fos.write(buf, 0, size);
						fos.close();
						bis.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PackageInfo info = pm.getPackageArchiveInfo(
							tmp.getAbsolutePath(),
							PackageManager.GET_ACTIVITIES);
					String packageName = null;
					if (info != null) {
						try {
							ApplicationInfo appInfo = info.applicationInfo;
							packageName = appInfo.packageName; // 得到安装包名�?
							pm.getPackageInfo(packageName,
									PackageManager.GET_ACTIVITIES);
							Log.e("apk", "already exist:" + packageName);
							tmp.delete();
							prefs.edit().putBoolean(apkName, true).commit();
							continue;
						} catch (Exception e) {
							Log.e("apk", "need install:" + packageName);
						}
					}
					int n = 0;
					while (n < 5) {
						Utils3D.sync_do_exec("chmod 777 "
								+ tmp.getAbsolutePath());
						boolean success = Utils3D.do_exec(
								"pm install " + tmp.getAbsolutePath(),
								packageName);
						if (!success) {
							Log.e("apk", "install fail:" + apkName);
							break;
						}
						// Log.e("apk", "install:"+s);
						if (packageName != null) {
							try {
								pm.getPackageInfo(packageName,
										PackageManager.GET_ACTIVITIES);
								Log.e("apk", "install ok:" + packageName);
								prefs.edit().putBoolean(apkName, true).commit();
								break;
							} catch (Exception e) {
								Log.e("apk", "install package again:"
										+ packageName);
							}
						} else
							n++;
						try {
							Thread.sleep(5000);
							Log.e("apk", "sleep:" + apkName);
						} catch (InterruptedException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						n++;
					}
					tmp.delete();
				}
			}

		});
		thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
	}

	private class TrashDeleteFolder {
		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.trash_pop_delfolder, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			// builder.setTitle(getString(RR.string.rename_folder_title));
			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setNegativeButton(
					getString(RR.string.circle_cancel_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							cleanup();

						}
					});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							trashdeleteFolderResult = Workspace3D.CIRCLE_POP_ACK_ACTION;
							Gdx.graphics.requestRendering();
							// cleanup();
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					mWaitingForResult = true;

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		private void cleanup() {
			dismissDialog(DIALOG_DELETE_FOLDER);
			trashdeleteFolderResult = Workspace3D.CIRCLE_POP_CANCEL_ACTION;
			Gdx.graphics.requestRendering();
			// mWaitingForResult = false;

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	private class SortApp {
		Dialog createDialog(final int check) {
			final ViewGroup layout = (ViewGroup) View.inflate(
					iLoongLauncher.this, RR.layout.sort_dialog, null);
			final RadioGroup radioGroup = (RadioGroup) layout
					.findViewById(RR.id.radioGroup);
			if (!DefaultLayout.show_default_app_sort) {
				radioGroup.findViewById(RR.id.radioFactory).setVisibility(
						View.GONE);
			}
			switch (check) {
			case AppList3D.SORT_NAME:
				radioGroup.check(RR.id.radioName);
				break;
			case AppList3D.SORT_INSTALL:
				radioGroup.check(RR.id.radioInstall);
				break;
			case AppList3D.SORT_USE:
				radioGroup.check(RR.id.radioFrequency);
				break;
			case AppList3D.SORT_FACTORY:
				radioGroup.check(RR.id.radioFactory);
				break;
			case AppList3D.SORT_DEFAULT:
				radioGroup.check(RR.id.radioDefault);
				break;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			builder.setTitle(getString(RR.string.sort_dialog_title));
			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setNegativeButton(
					getString(RR.string.circle_cancel_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							cleanup();

						}
					});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							int checkId = radioGroup.getCheckedRadioButtonId();
							int checked = -1;
							if (checkId == RR.id.radioName) {
								checked = AppList3D.SORT_NAME;
							} else if (checkId == RR.id.radioInstall) {
								checked = AppList3D.SORT_INSTALL;
							} else if (checkId == RR.id.radioFrequency) {
								checked = AppList3D.SORT_USE;
							} else if (checkId == RR.id.radioFactory) {
								checked = AppList3D.SORT_FACTORY;
							} else if (checkId == RR.id.radioDefault) {
								checked = AppList3D.SORT_DEFAULT;
							}

							d3dListener.sortApp(checked);
							cleanup();
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();

			return dialog;
		}

		private void cleanup() {
			dismissDialog(DIALOG_SORT_APP);

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	private class PopupDeleteAll {
		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.circle_pop_delall, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			// builder.setTitle(getString(RR.string.rename_folder_title));
			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setNegativeButton(
					getString(RR.string.circle_cancel_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							popResult = Workspace3D.CIRCLE_POP_CANCEL_ACTION;
							cleanup();

						}
					});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							popResult = Workspace3D.CIRCLE_POP_ACK_ACTION;
							Gdx.graphics.requestRendering();
							cleanup();
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					mWaitingForResult = true;

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		private void cleanup() {
			dismissDialog(DIALOG_CIRCLE_DELALL);
			// mWaitingForResult = false;

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	private class RestartDialog {
		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.restart_pop_dialog, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			builder.setMessage("桌面程序已被破坏!!!");
			// builder.setTitle(getString(RR.string.rename_folder_title));
			builder.setCancelable(false);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							int i = 1 / 0;
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		private void cleanup() {
			dismissDialog(DIALOG_RESTART);
			// mWaitingForResult = false;

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}

	}
    private class DesktopEffectDialog{
        
        
        Dialog createDialog() {
           
            String initValue = PreferenceManager.getDefaultSharedPreferences(
                    SetupMenu.getContext()).getString(
                    SetupMenu.getKey(RR.string.setting_key_desktopeffects), "2");
            Log.e("effect", "init=" + initValue);
            String[] data = iLoongLauncher.this.getResources().getStringArray(
                    RR.array.workspace_effects_list_preference);
            final ListView listview = new ListView(iLoongLauncher.this);
            listview.setAdapter(new ArrayAdapter<String>(iLoongLauncher.this,
                    android.R.layout.simple_list_item_single_choice, data));
            listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listview.setItemChecked(Integer.parseInt(initValue), true);
            listview.setId(0);
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    iLoongLauncher.this);
            builder.setTitle(RR.string.appeffects_dialog_title);
            builder.setCancelable(true);
            builder.setOnCancelListener(new Dialog.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    cleanup();
                }
            });
            builder.setPositiveButton(getString(RR.string.circle_ok_action),
                    new Dialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int i = listview.getCheckedItemPosition();

                            String[] value = iLoongLauncher.this
                                    .getResources()
                                    .getStringArray(
                                            RR.array.workspace_effectsvalue_list_preference);
                            int select = Integer.parseInt(value[i]);
                            PreferenceManager
                                    .getDefaultSharedPreferences(
                                            SetupMenu.getContext())
                                    .edit()
                                    .putString(
                                            SetupMenu
                                                    .getKey(RR.string.setting_key_desktopeffects),
                                            value[i]).commit();
                            d3dListener.workspace.setDesktopEffectType(select);
                            cleanup();
                        }
                    });
            builder.setView(listview);
            Dialog dialog = builder.create();
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams p = dialogWindow.getAttributes();
            p.height = (int) (Utils3D.getScreenHeight() * 0.8);
            p.width = (int) (Utils3D.getScreenWidth() * 0.95);
            dialogWindow.setAttributes(p);
            return dialog;
        }

        private void cleanup() {
            dismissDialog(DIALOG_DESKTOP_EFFECT);

            
        }
    }
	
	private class AppEffectDialog {
		Dialog createDialog() {
			String initValue = PreferenceManager.getDefaultSharedPreferences(
					SetupMenu.getContext()).getString(
					SetupMenu.getKey(RR.string.setting_key_appeffects), "2");
			Log.e("effect", "init=" + initValue);
			String[] data = iLoongLauncher.this.getResources().getStringArray(
					RR.array.app_effects_list_preference);
			final ListView listview = new ListView(iLoongLauncher.this);
			listview.setAdapter(new ArrayAdapter<String>(iLoongLauncher.this,
					android.R.layout.simple_list_item_single_choice, data));
			listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listview.setItemChecked(Integer.parseInt(initValue), true);
			listview.setId(0);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setTitle(RR.string.appeffects_dialog_title);
			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							int i = listview.getCheckedItemPosition();

							String[] value = iLoongLauncher.this
									.getResources()
									.getStringArray(
											RR.array.app_effectsvalue_list_preference);
							int select = Integer.parseInt(value[i]);
							PreferenceManager
									.getDefaultSharedPreferences(
											SetupMenu.getContext())
									.edit()
									.putString(
											SetupMenu
													.getKey(RR.string.setting_key_appeffects),
											value[i]).commit();
							d3dListener.setAppEffectType(select);
							cleanup();
						}
					});
			builder.setView(listview);
			Dialog dialog = builder.create();
			Window dialogWindow = dialog.getWindow();
			WindowManager.LayoutParams p = dialogWindow.getAttributes();
			p.height = (int) (Utils3D.getScreenHeight() * 0.8);
			p.width = (int) (Utils3D.getScreenWidth() * 0.95);
			dialogWindow.setAttributes(p);
			return dialog;
		}

		private void cleanup() {
			dismissDialog(DIALOG_APP_EFFECT);

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add endif

		}

	}

	private class ApkCannotFound {
		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.apk_cannot_found_dialog, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			String DownLoadtitle = null;

			if (Widget3DManager.curDownload != null)
				DownLoadtitle = (String) Widget3DManager.curDownload.title;
			// else /* downlaod mm_setting */
			// DownLoadtitle = DefaultLayout.getmmSettingTitle();

			builder.setTitle(DownLoadtitle);
			TextView text = (TextView) layout.findViewById(RR.id.label);

			String content = text.getText().toString();
			text.setText(content);

			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Widget3DManager.curDownload = null;
							cleanup();

						}
					});

			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		private void cleanup() {
			removeDialog(DIALOG_APK_CANNOT_FOUND);
			// mWaitingForResult = false;

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	private class DownLoadWidget {
		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.widget_download_dialog, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(RR.drawable.ic_launcher);
			String DownLoadtitle = null;

			if (Widget3DManager.curDownload != null)
				DownLoadtitle = (String) Widget3DManager.curDownload.title;
			else
				/* downlaod mm_setting */
				DownLoadtitle = DefaultLayout.getmmSettingTitle();

			builder.setTitle(DownLoadtitle);
			TextView text = (TextView) layout.findViewById(RR.id.label);

			String content = text.getText().toString();

			content = DownLoadtitle + languageSpace + content;

			text.setText(content);

			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setNegativeButton(
					getString(RR.string.circle_cancel_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Widget3DManager.curDownload = null;
							cleanup();

						}
					});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (Widget3DManager.curDownload == null) {
								DefaultLayout.startDownLoadmmSetting();
								cleanup();
								return;
							}

							String pkgname = Widget3DManager.curDownload.intent
									.getComponent().getPackageName();
							String classname = Widget3DManager.curDownload.intent
									.getComponent().getClassName();
							String apkname = DefaultLayout
									.GetDefaultWidgetApkname(pkgname, null);
							String dlTitle = (String) Widget3DManager.curDownload.title;
							String customID = null;

							if (Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_WIDGET_VIEW)
								customID = DefaultLayout
										.GetDefaultWidgetCustomID(pkgname);
							else if (Widget3DManager.curDownload.itemType == LauncherSettings.Favorites.ITEM_TYPE_VIRTURE_VIEW)
								customID = DefaultLayout.GetVirtureCustomID(
										pkgname, classname);
							DLManager.getInstance().DownloadWidget(dlTitle,
									apkname, pkgname, customID,
									WidgetDownload.getInstance());
							// SendMsgToAndroid.sendOurToastMsg(getString(RR.string.app_downloading)
							// + languageSpace + dlTitle);
							cleanup();
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		private void cleanup() {
			removeDialog(DIALOG_DOWNLOAD_WIDGET);
			// mWaitingForResult = false;

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	private class PageDelete {
		Dialog createDialog() {
			final View layout = View.inflate(iLoongLauncher.this,
					RR.layout.circle_pop_delall, null);
			TextView tv = (TextView) layout.findViewById(RR.id.label);
			tv.setText(RR.string.delete_page);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					iLoongLauncher.this);
			builder.setIcon(0);
			// builder.setTitle(getString(RR.string.rename_folder_title));
			builder.setCancelable(true);
			builder.setOnCancelListener(new Dialog.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					cleanup();
				}
			});
			builder.setNegativeButton(
					getString(RR.string.circle_cancel_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							cleanup();
						}
					});
			builder.setPositiveButton(getString(RR.string.circle_ok_action),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							cleanup();
							d3dListener.exeDeletePage();
						}
					});
			builder.setView(layout);

			final AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				public void onShow(DialogInterface dialog) {
					mWaitingForResult = true;
					PageDeleteDialogShow = true;

					// xiatian add start //Widget3D adaptation "Naked eye 3D"
					if (DefaultLayout.show_sensor) {
						sensorListenerPause();
					}
					// xiatian add end

				}
			});

			return dialog;
		}

		private void cleanup() {
			PageDeleteDialogShow = false;
			dismissDialog(DIALOG_DELETE_PAGE);

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	public void DismissPageDeleteDialog() {
		if (PageDeleteDialogShow == true) {
			PageDeleteDialogShow = false;
			dismissDialog(DIALOG_DELETE_PAGE);

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	public void DismissShortcutDialog() {
		if (ShortcutDialogShow == true) {
			ShortcutDialogShow = false;
			dismissDialog(DIALOG_CREATE_SHORTCUT);

			// xiatian add start //Widget3D adaptation "Naked eye 3D"
			if (DefaultLayout.show_sensor) {
				sensorListenerResume();
			}
			// xiatian add end

		}
	}

	public void deleteWidget(Widget widget) {
		xWorkspace.removeWidget(widget, widget.getItemInfo().screen);
		mAppWidgetHost.deleteAppWidgetId(widget.getItemInfo().appWidgetId);
		Log.e("delete widget ", "widgetId:" + widget.name);

	}

	public Desktop3DListener getD3dListener() {
		return d3dListener;
	}

	public boolean isWorkspaceVisible() {
		if (d3dListener == null)
			return true;
		return d3dListener.isWorkspaceVisible();
	}

	@Override
	public void setActionListener() {
		// TODO Auto-generated method stub
		SetupMenuActions.getInstance().RegisterListener(
				ActionSetting.ACTION_DESKTOP_SETTINGS, this);
		SetupMenuActions.getInstance().RegisterListener(
				ActionSetting.ACTION_INSTALL_HELP, this);
	}

	@Override
	public void OnAction(int actionid, Bundle bundle) {
		if (actionid == ActionSetting.ACTION_DESKTOP_SETTINGS) {
			if (bundle.containsKey("vibrator"))
				vibratorAble = bundle.getInt("vibrator");
			else if (bundle.containsKey(SetupMenu
					.getKey(RR.string.icon_size_key))) {
				String value = bundle.getString(SetupMenu
						.getKey(RR.string.icon_size_key));
				if (DefaultLayout.app_icon_size != (int) DefaultLayout.iconSizeMap
						.get(value)) {
					this.postRunnable(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							SetupMenuActions.getInstance().Handle(
									ActionSetting.ACTION_RESTART);
						}
					});

				}

			}

			/****************** added by zhenNan.ye begin *******************/
			if (bundle.containsKey(SetupMenu
					.getKey(RR.string.setting_key_particle))) {
				ParticleManager.particleManagerEnable = bundle
						.getBoolean(SetupMenu
								.getKey(RR.string.setting_key_particle));
			}
			/****************** added by zhenNan.ye end *******************/
		} else if (actionid == ActionSetting.ACTION_INSTALL_HELP) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			prefs.edit()
					.putBoolean(ClingManager.INTRODUCTION_DISMISSED_KEY, false)
					.commit();
			showIntroductionAgain = true;
			showIntroduction();
		}

	}

	public void showIntroduction() {
		// Utils3D.showPidMemoryInfo("intro1");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (!DefaultLayout.show_introduction) {
			showIntroduction = false;
		}
		if (!prefs.getBoolean(ClingManager.INTRODUCTION_DISMISSED_KEY, false)
				&& showIntroduction) {
			d3dListener.showIntroduction();
		} else {
			// Utils3D.showPidMemoryInfo("intro2");
			showIntroduction = false;
			mModel.initialize(d3dListener);// add to model callbacks to bind
											// icon,
			// Utils3D.showPidMemoryInfo("intro3");
			showProgressDialog();
			// Utils3D.showPidMemoryInfo("intro4");
			toStartLoader();
		}
	}

	public void dismissIntroduction() {
		// parent.post(new Runnable() {
		// @Override
		// public void run() {
		if (!showIntroductionAgain) {
			mModel.initialize(d3dListener);// add to model callbacks to
											// bind icon,
			SendMsgToAndroid.sendCreateProgressDialogMsg();
			toStartLoader();
		} else
			showIntroductionAgain = false;
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(ClingManager.INTRODUCTION_DISMISSED_KEY, true);
		editor.commit();
		introductionShown = false;
		showIntroduction = false;
		d3dListener.dismissIntroduction();
		// }
		// });
	}

	private void add3DFolder() {
		ItemInfo info = null;
		Workspace3D workspace = d3dListener.getWorkspace3D();
		FolderIcon3D fo = (FolderIcon3D) Desktop3DListener.folder3DHost
				.getWidget3D();

		if (fo == null)
			return;
		info = fo.getItemInfo();
		info.x = (int) (clickPoint.x - fo.width / 2);
		info.y = (int) (clickPoint.y - fo.height / 2);
		info.screen = workspace.getCurrentScreen();

		if (workspace.addInScreen(fo, info.screen, info.x, info.y, false)) {
			FolderInfo mFolderInfo = fo.mInfo;
			mFolderInfo.title = R3D.folder3D_name;
			LauncherModel.updateItemInDatabase(iLoongLauncher.getInstance(),
					mFolderInfo);
			// zhujieping add
			if (ThemeManager.getInstance().getBoolean("miui_v5_folder")
					|| DefaultLayout.miui_v5_folder)
				fo.mFolderMIUI3D.setEditText(R3D.folder3D_name);
			else
				fo.mFolder.setEditText(R3D.folder3D_name);
		} else {
			LauncherModel.deleteItemFromDatabase(this, info);
			sFolders.remove(info.id);
		}
		// Root3D.addOrMoveDB(info);
	}

	private void add3DContact() {
		Icon3D icon = (Icon3D) Desktop3DListener.contact3DHost.getWidget3D();
		Workspace3D workspace = d3dListener.getWorkspace3D();
		if (icon != null) {
			ItemInfo info = icon.getItemInfo();
			info.x = (int) (clickPoint.x - icon.width / 2);
			info.y = (int) (clickPoint.y - icon.height / 2);
			info.screen = workspace.getCurrentScreen();
			workspace.addInScreen(icon, info.screen, info.x, info.y, false);
			// Root3D.addOrMoveDB(info);
		}

	}

	public int[] getSpanForWidget(AppWidgetProviderInfo info, int[] spanXY) {
		int sysVersion = Integer.parseInt(VERSION.SDK);
		if (sysVersion < 11) {
			if (info != null) {
				// Converting complex to dp.
				info.minWidth = TypedValue.complexToDimensionPixelSize(
						info.minWidth, this.getApplicationContext()
								.getResources().getDisplayMetrics());
				info.minHeight = TypedValue.complexToDimensionPixelSize(
						info.minHeight, this.getApplicationContext()
								.getResources().getDisplayMetrics());
			}
		}
		return getSpanForWidget(info.provider, info.minWidth, info.minHeight,
				spanXY);
	}

	public int[] getSpanForWidget(ComponentName component, int minWidth,
			int minHeight, int[] spanXY) {
		if (spanXY == null) {
			spanXY = new int[2];
		}

		// Log.d("launcher", "minWidth,minHeight=" + minWidth + "," +
		// minHeight);
		int sysVersion = Integer.parseInt(VERSION.SDK);
		Rect padding = new Rect();
		// if(sysVersion >= 15)padding =
		// AppWidgetHostView.getDefaultPaddingForWidget(this, component, null);
		// else padding = new Rect();
		int requiredWidth = minWidth + padding.left + padding.right;
		int requiredHeight = minHeight + padding.top + padding.bottom;
		return CellLayout3D.rectToCell(getResources(), requiredWidth,
				requiredHeight, null);
	}

	public int getCurrentScreen() {
		if (d3dListener != null)
			return d3dListener.getCurrentScreen();
		return 0;
	}

	public int getScreenCount() {
		if (d3dListener != null)
			return d3dListener.getScreenCount();
		return 0;
	}

	private void registerShakeWallpapers() {
		boolean enable_shake_wallpaper = !DefaultLayout.disable_shake_wallpaper;
		// this.getResources().getBoolean(RR.bool.enable_shake_wallpaper);
		boolean cbx_shake_wallpaper = PreferenceManager
				.getDefaultSharedPreferences(SetupMenu.getContext())
				.getBoolean(
						this.getResources().getString(
								RR.string.setting_key_shake_wallpaper),
						DefaultLayout.default_open_shake_wallpaper);
		if (enable_shake_wallpaper) {
			if (cbx_shake_wallpaper) {
				if (mShaker == null) {
					mShaker = new ShakeListener(this);
					if (checkSensorSupported()) {
						mShaker.setOnShakeListener(new WallpaperShakeListener(
								this));
						mShaker.resume();
					}
				} else {
					if (checkSensorSupported()) {
						mShaker.resume();
					}
				}
			} else {
				if (mShaker != null) {
					mShaker.pause();
				}
			}
		}
	}

	public boolean checkSensorSupported() {
		if (mShaker == null) {
			mShaker = new ShakeListener(this);
		}
		boolean supported = mShaker.checkSensorSupported();
		return supported;
	}

	public void cleaWidgetStatus(int page) {
		CellLayout cell = (CellLayout) xWorkspace.getChildAt(page);
		View view = null;
		for (int i = 0; i < cell.getChildCount(); i++) {
			view = cell.getChildAt(i);
			int index = MotionEventPool.get(0, 0, MotionEvent.ACTION_CANCEL, 0,
					0);
			Messenger
					.sendMsg(Messenger.EVENT_UPDATE_SYS_WIDGET, view, index, 0);
		}
	}

	// xiatian add start //Widget3D adaptation "Naked eye 3D"
	private void registerSensor() {
		if (DefaultLayout.show_sensor) {
			if (isOpenSensor() == true) {
				if (DefaultLayout.default_open_sensor) {
					if (!isPhoneSupportSensor()) {
						PreferenceManager
								.getDefaultSharedPreferences(
										SetupMenu.getContext())
								.edit()
								.putBoolean(
										this.getResources().getString(
												RR.string.setting_key_sensor),
										false).commit();
						// Log.v("xiatian",
						// "sensor--set cbx_sensor from on to off");
						return;
					}
				}

				if (mSensorListener == null) {
					mSensorListener = new SensorListener(this);
				}
				sensorListenerResume();
			} else {
				sensorListenerPause();
			}
		}
	}

	public boolean isPhoneSupportSensor() {
		if (DefaultLayout.show_sensor) {
			if (DefaultLayout.is_supported_sensor != -1) {
				return (DefaultLayout.is_supported_sensor > 0 ? true : false);
			}

			SensorManager sensorMgr = (SensorManager) this
					.getSystemService(Context.SENSOR_SERVICE);
			Sensor sensor = sensorMgr
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (sensor == null) {
				// Log.v("xiatian","isPhoneSupportSensor -- no");
				DefaultLayout.is_supported_sensor = 0;
				return false;
			} else {
				// Log.v("xiatian","isPhoneSupportSensor -- ok");
				DefaultLayout.is_supported_sensor = 1;
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isOpenSensor() {
		if (DefaultLayout.show_sensor) {
			boolean cbx_sensor = PreferenceManager.getDefaultSharedPreferences(
					SetupMenu.getContext())
					.getBoolean(
							this.getResources().getString(
									RR.string.setting_key_sensor),
							DefaultLayout.default_open_sensor);
			// Log.v("xiatian","isOpenSensor -- cbx_sensor:" + cbx_sensor);
			return cbx_sensor;
		} else {
			return false;
		}
	}

	public void sensorListenerPause() {
		if (DefaultLayout.show_sensor) {
			if (mSensorListener != null) {
				mSensorListener.pause();
			}
		}
	}

	public void sensorListenerResume() {
		if (DefaultLayout.show_sensor) {
			if (mSensorListener != null) {
				mSensorListener.resume();
			}
		}
	}

	// xiatian add end

	@Override
	public Desktop3D getDesktop() {
		// TODO Auto-generated method stub
		return Desktop3DListener.d3d;
	}

	@Override
	public View getGLView() {
		// TODO Auto-generated method stub
		return glView;
	}

	@Override
	public boolean isWorkspace3DTouchable() {
		// TODO Auto-generated method stub
		if (d3dListener == null)
			return false;
		Workspace3D workspace = d3dListener.getWorkspace3D();
		if (workspace == null)
			return false;
		if (!workspace.touchable)
			return false;
		if (workspace.color.a != 1)
			return false;
		if (workspace.scaleX != 1)
			return false;
		if (workspace.getX() != 0)
			return false;
		if (workspace.getY() != 0)
			return false;
		if (mSetupMenu != null && mSetupMenu.mOpen)
			return false;
		return true;
	}

	@Override
	public void setCurrentFocusX(int arg0) {
		CellLayout3D.currentX = arg0;
	}

	// xiatian add start //New Requirement 20130507
	public void startChangJingZhuoMian() {
		if (FeatureConfig.enable_SceneBox) {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(this, SCENE_BOX_MAIN_ACTIVITY));
			startActivity(intent);
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

	public boolean startWallpaperBox() {
		if (FeatureConfig.enable_WallpaperBox) {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(this,
					WALLPAPER_BOX_MAIN_ACTIVITY));
			startActivity(intent);
			return true;
		}

		// Toast.makeText(
		// iLoongLauncher.getInstance(),
		// "FeatureConfig.enable_WallpaperBox==false\n--\n--没有安装壁纸盒子，打开默认壁纸",
		// Toast.LENGTH_SHORT
		// )
		// .show();
		return false;
	}
	// xiatian add end

	@Override
	public boolean hasCancelDialog() {
		// TODO Auto-generated method stub
		return false;
	}

}
