package com.coco.theme.themebox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.download.Assets;
import com.coco.download.DownloadList;
import com.coco.download.PlatformInfo;
import com.coco.font.fontbox.TabFontFactory;
import com.coco.pub.provider.PubContentProvider;
import com.coco.pub.provider.PubProviderHelper;
import com.coco.theme.themebox.apprecommend.IconAsyncTask;
import com.coco.theme.themebox.apprecommend.LoadRecomandActivity;
import com.coco.theme.themebox.apprecommend.MyDBHelper;
import com.coco.theme.themebox.apprecommend.Profile;
import com.coco.theme.themebox.database.service.ConfigurationTabService;
import com.coco.theme.themebox.service.ThemeManager;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.service.ThemesDB;
import com.coco.theme.themebox.update.UpdateManager;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.coco.theme.themebox.util.PathTool;
import com.coco.theme.themebox.util.Tools;
import com.coco.wallpaper.wallpaperbox.TabWallpaperFactory;
import com.coco.wf.wfbox.TabEffectFactory;
import com.coco.widget.widgetbox.TabWidgetFactory;
import com.iLoong.base.themebox.R;

public class MainActivity extends Activity implements OnTouchListener,
		GestureDetector.OnGestureListener {

	private static final String GOOGLE_VERSION_APP_ID = "f2986";
	public static boolean sbGoogleVersion;
	private final String TAG_THEME = "tagTheme";
	private final String TAG_LOCK = "tagLock";
	private DownModule downModule;
	private TabThemeFactory tabTheme;
	private TabWallpaperFactory tabWallpaper;
	private TabFontFactory tabFont;
	private TabWidgetFactory tabWidget;
	private TabEffectFactory effect;
	private TabHost tabHost;
	// teapotXu_20130304: add start
	// set a flag that indicates whether the ThemeSelectIcon launched the
	// ThemeBox or Launcher app.
	private boolean b_theme_icon_start_launcher = false;
	// teapotXu_20130304: add end
	private LinearLayout layout_recommend;
	private ImageView iv;
	private GestureDetector mGestureDetector;
	private boolean hasMeasured = false;
	private boolean isScrolling = false;
	private float mScrollY;
	private int MAX_HEIGHT = 0;
	private Animation starScaleAnim;
	private ListView listView;
	private ArrayList<AppInfos> appInfos = null;
	private AppAdapter appAdapter;
	private String appIconUrl[];
	private ImageView imageView;
	private boolean isUnfold = false;
	private ImageView starIv;
	private int pressY;
	private ProgressBar mProgressBar;
	private TextView netPrompt = null;
	private Context mContext;
	private Handler mHandler = new Handler();
	private MyDBHelper mDbHelper = null;
	private final String TAG_WALLPAPER = "tagWallpaper";
	private final String TAG_FONT = "tagFont";
	private final String TAG_EFFECT = "tagEffect";
	private boolean isChange = false;
	private View load;
	private final String TAG_SCENE = "tagScene";
	private final String TAG_WIDGET = "tagWidget";
	private int KILL_DELAY = 0 * 1000;
	private MessageReceiver receiver = null;
	public static boolean isExit = false;
	private View progress = null;
	private Handler handler = new Handler();
	private Bundle bundle;
	private List<String> listTab;
	private UpdateManager updateManager;
	private Handler mDelayedStopHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.v("themebox", "themebox  kill");
			if (Tools.isServiceRunning(mContext,
					"com.coco.theme.themebox.DownloadApkContentService")
					|| Tools.isServiceRunning(mContext,
							"com.coco.theme.themebox.update.UpdateService")) {
				Message m = mDelayedStopHandler.obtainMessage();
				mDelayedStopHandler.sendMessageDelayed(m, 10000);
				return;
			}
			try {
				if (isExit) {
					if (receiver != null) {
						unregisterReceiver(receiver);
						receiver = null;
					}
					System.exit(0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Jone modify. make sure this function will be called before all
		// initialization
		// Zhongqihong@2014/12/24 DEL START
		// bindData(this.getIntent());
		// Zhongqihong@2014/12/24 DEL END
		// Jone end
		ActivityManager.pushActivity(this);
		System.out.println("oncreate start11111");
		System.out.println("oncreate start22222");
		super.onCreate(savedInstanceState);
		mContext = this;
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		isExit = false;
		new PubProviderHelper(this);
		if (Assets.getAppId(mContext).equals(GOOGLE_VERSION_APP_ID)) {
			sbGoogleVersion = true;
		}
		readDefaultData();
		System.out.println("oncreate start33333");
		if ((!FunctionConfig.personal_center_internal && receiver == null)
				|| FunctionConfig.isEnable_background_configuration_tab()) {
			receiver = new MessageReceiver();
			IntentFilter filter = new IntentFilter();
			if (!com.coco.theme.themebox.util.FunctionConfig.personal_center_internal) {
				filter.addAction("com.cooee.launcher.action.start");
			}
			if (FunctionConfig.isEnable_background_configuration_tab()) {
				filter.addAction("com.coco.action.TAB_CHANGED");
			}
			registerReceiver(receiver, filter);
		}
		if (!com.coco.theme.themebox.StaticClass.isAllowDownload(mContext)
				&& com.coco.theme.themebox.util.FunctionConfig
						.isDownToInternal()) {
			com.coco.theme.themebox.StaticClass.canDownToInternal = true;
		}
		PathTool.makeDirApp();
		// teapotXu_20130304: add start
		// set a flag that indicates whether the ThemeSelectIcon launched the
		// ThemeBox or Launcher app.
		String pkgNameFromThemeBox = getIntent().getStringExtra("FROM_PACKAGE");
		if (pkgNameFromThemeBox != null
				&& pkgNameFromThemeBox
						.startsWith(ThemeManager.ACTION_INTENT_THEME)) {
			b_theme_icon_start_launcher = true;
			return;
		}
		// teapotXu_20130304: add end
		setContentView(R.layout.main_tab_lock);// 因锁屏代码合入，判断锁屏盒子是否安装无效
		progress = findViewById(R.id.progress);
		tabHost = (TabHost) findViewById(R.id.tabhost);
		if (FunctionConfig.isEnable_background_configuration_tab()) {
			progress.setVisibility(View.VISIBLE);
			bundle = savedInstanceState;
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ConfigurationTabService service = new ConfigurationTabService(
							mContext);
					listTab = service.queryTabList();
					runOnUiThread(new Runnable() {

						public void run() {
							if (listTab.size() == 0
									|| DownloadList.getInstance(mContext)
											.isRefreshTab()) {// 没有读取到后台数据或者需要更新后台tab
								if (StaticClass.isHaveInternet(mContext)) {// 有网的情况，下载后台tab数据
									DownloadList.getInstance(mContext)
											.downTab();
									handler.postDelayed(new Runnable() {

										@Override
										public void run() {// 30s后没有读到后台数据，显示默认配置的tab
															// TODO
															// Auto-generated
															// method stub
											DownloadList.getInstance(mContext)
													.stopDownloadTab();
											if (progress != null
													&& progress.getVisibility() == View.VISIBLE) {
												progress.setVisibility(View.GONE);
											}
											if (tabHost.getTabWidget() == null) {
												for (String item : DownloadList.types) {
													listTab.add(item);
												}
												if (tabHost.getTabWidget() == null) {
													initContentView(bundle);
												}
											}
										}
									}, 1000 * 30);
								} else {
									Toast.makeText(mContext,
											R.string.internet_err,
											Toast.LENGTH_SHORT).show();
									if (progress != null
											&& progress.getVisibility() == View.VISIBLE) {
										progress.setVisibility(View.GONE);
									}
									for (String item : DownloadList.types) {// 没有网的情况，且没有读到保存下来的数据，显示默认配置的tab
										listTab.add(item);
									}
									if (tabHost.getTabWidget() == null) {
										initContentView(bundle);
									}
								}
							} else {
								resetTabs();// 读到后台配置数据，按照后台配置，重新设置开关值
								if (progress != null
										&& progress.getVisibility() == View.VISIBLE) {
									progress.setVisibility(View.GONE);
								}
								if (tabHost.getTabWidget() == null) {
									initContentView(bundle);
								}
							}
						}
					});
				}
			}).start();
		} else {// 后台配置开关没有打开，直接显示默认配置的tab
			listTab = new ArrayList<String>();
			if (FunctionConfig.getTab_sequence() != null) {
				String types[] = FunctionConfig.getTab_sequence().split(",");
				for (String item : types) {
					listTab.add(item);
				}
			} else {
				for (String item : DownloadList.types) {
					listTab.add(item);
				}
				listTab.add("0");
			}
			if (tabHost.getTabWidget() == null) {
				initContentView(bundle);
			}
		}
		System.out.println("oncreate start444444");
	}

	private void initContentView(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isfirst = prefs.getBoolean("firstThemebox", true);
		Log.v("loading", "----first----");
		load = findViewById(R.id.load);
		if (FunctionConfig.isLoadingShow() && isfirst) {
			ImageView bg = (ImageView) load.findViewById(R.id.loadbg);
			String systemLauncher = Locale.getDefault().getLanguage()
					.toString();
			if (systemLauncher.equals("zh")) {
				bg.setImageResource(R.drawable.load);
			} else {
				bg.setImageResource(R.drawable.loades);
			}
			bg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					load.setVisibility(View.GONE);
				}
			});
			load.setVisibility(View.VISIBLE);
			mHandler.postDelayed(loading, 6000);
			prefs.edit().putBoolean("firstThemebox", false).commit();
		} else {
			load.setVisibility(View.GONE);
		}
		downModule = DownModule.getInstance(this);// new DownModule(this);
		LocalActivityManager groupActivity = new LocalActivityManager(this,
				false);
		groupActivity.dispatchCreate(savedInstanceState);
		tabHost.setup(groupActivity);
		// 添加主题页面
		for (String tabid : listTab) {
			if (tabid.equals(DownloadList.Theme_Type)
					&& FunctionConfig.isThemeVisible()) {
				boolean ifselecthot = getIntent().getBooleanExtra(
						"selcetHotTheme", false);
				tabTheme = new TabThemeFactory(MainActivity.this, downModule,
						ifselecthot);
				final View indicatorTheme = View.inflate(MainActivity.this,
						R.layout.indicator_theme, null);
				tabHost.addTab(tabHost.newTabSpec(TAG_THEME)
						.setIndicator(indicatorTheme).setContent(tabTheme));
				continue;
			}
			if (tabid.equals(DownloadList.Lock_Type)
					&& com.coco.theme.themebox.util.FunctionConfig
							.isDisplayLock()) {
				if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()) {
					View indicatorLock = View.inflate(MainActivity.this,
							R.layout.indicator_lock, null);
				}
				continue;
			}
			// 添加壁纸页面
			if (tabid.equals(DownloadList.Wallpaper_Type)
					&& FunctionConfig.isWallpaperVisible()) {
				boolean ifselecthot = getIntent().getBooleanExtra("selcetHot",
						false);
				tabWallpaper = new TabWallpaperFactory(mContext, downModule,
						ifselecthot);
				View indicatorWallpaper = View.inflate(mContext,
						R.layout.indicator_wallpaper, null);
				tabHost.addTab(tabHost.newTabSpec(TAG_WALLPAPER)
						.setIndicator(indicatorWallpaper)
						.setContent(tabWallpaper));
				continue;
			}
			// 添加字体页面
			if (tabid.equals(DownloadList.Font_Type)
					&& FunctionConfig.isFontVisible()) {
				tabFont = new TabFontFactory(mContext, downModule);
				View indicatorFont = View.inflate(mContext,
						R.layout.indicator_font, null);
				tabHost.addTab(tabHost.newTabSpec(TAG_FONT)
						.setIndicator(indicatorFont).setContent(tabFont));
				continue;
			}
			if (tabid.equals(DownloadList.Widget_Type)
					&& FunctionConfig.isShowWidgetTab()) {
				tabWidget = new TabWidgetFactory(mContext, downModule);
				View indicatorFont = View.inflate(mContext,
						R.layout.indicator_widget, null);
				tabHost.addTab(tabHost.newTabSpec(TAG_WIDGET)
						.setIndicator(indicatorFont).setContent(tabWidget));
				continue;
			}
			if (!FunctionConfig.isEnable_background_configuration_tab()) {
				if (tabid.equals("0") && FunctionConfig.isEffectVisiable()) {
					int type = getIntent().getIntExtra("type", -1);
					effect = new TabEffectFactory(mContext, type);
					View indicatorEffect = View.inflate(mContext,
							R.layout.indicator_effect, null);
					tabHost.addTab(tabHost.newTabSpec(TAG_EFFECT)
							.setIndicator(indicatorEffect).setContent(effect));
					continue;
				}
			}
		}
		if (FunctionConfig.isEnable_background_configuration_tab()) {
			if (FunctionConfig.isEffectVisiable()) {
				int type = getIntent().getIntExtra("type", -1);
				TabEffectFactory effect = new TabEffectFactory(mContext, type);
				View indicatorEffect = View.inflate(mContext,
						R.layout.indicator_effect, null);
				tabHost.addTab(tabHost.newTabSpec(TAG_EFFECT)
						.setIndicator(indicatorEffect).setContent(effect));
			}
		}
		if (tabHost.getChildCount() != 2) {
			tabHost.getTabWidget()
					.setBackgroundResource(R.drawable.manager_bar);
		} else {
			tabHost.getTabWidget().setBackgroundResource(
					R.drawable.manager_bar_1);
		}
		// if( com.coco.theme.themebox.util.FunctionConfig.isDisplayLock() )
		// {
		// if( com.coco.theme.themebox.util.FunctionConfig.isRecommendVisible()
		// )
		// {
		// mDbHelper = new MyDBHelper( mContext );
		// IntentFilter recommendFilter = new IntentFilter();
		// recommendFilter.addAction( StaticClass.ACTION_THEME_UPDATE_RECOMMEND
		// );
		// recommendFilter.addAction( "android.net.conn.CONNECTIVITY_CHANGE" );
		// recommendFilter.addAction( Intent.ACTION_WALLPAPER_CHANGED );
		// registerReceiver( recommendReceiver , recommendFilter );
		// if( com.coco.theme.themebox.util.FunctionConfig.isLockVisible() )
		// {
		// layout_recommend = (LinearLayout)findViewById( R.id.mainRelLayout );
		// mProgressBar = (ProgressBar)findViewById( R.id.circleProgressBar );
		// netPrompt = (TextView)findViewById( R.id.internetPrompt );
		// imageView = (ImageView)findViewById( R.id.imageViewRecom );
		// imageView.setClickable( false );
		// iv = (ImageView)findViewById( R.id.labelImageView );
		// iv.setOnTouchListener( this );
		// iv.setOnClickListener( new OnClickListener() {
		//
		// @Override
		// public void onClick(
		// View v )
		// {
		// }
		// } );
		// mGestureDetector = new GestureDetector( this , this );
		// mGestureDetector.setIsLongpressEnabled( false );
		// calculatorWidth();
		// starScaleAnim = AnimationUtils.loadAnimation( this ,
		// R.anim.star_scale_anim );
		// starIv = (ImageView)findViewById( R.id.starImageView );
		// starIv.startAnimation( starScaleAnim );
		// listView = (ListView)findViewById( R.id.pushListView );
		// appInfos = new ArrayList<AppInfos>();
		// appAdapter = new AppAdapter( MainActivity.this , appInfos );
		// if( tabHost.getCurrentTab() == 1 )
		// {
		// layout_recommend.setVisibility( View.VISIBLE );
		// mHandler.postDelayed( recommendRun , 0 );
		// }
		// tabHost.getTabWidget().getChildAt( 0 ).setOnClickListener( new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(
		// View v )
		// {
		// if( tabHost.getCurrentTab() != 0 )
		// {// 一定要判断这个是为了防止阻碍切换事�?
		// tabHost.setCurrentTab( 0 );
		// }
		// else
		// {
		// // 做你要做的事
		// }
		// layout_recommend.setVisibility( View.INVISIBLE );
		// }
		// } );
		// tabHost.getTabWidget().getChildAt( 1 ).setOnClickListener( new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(
		// View v )
		// {
		// if( tabHost.getCurrentTab() != 1 )
		// {// 一定要判断这个是为了防止阻碍切换事�?
		// tabHost.setCurrentTab( 1 );
		// }
		// else
		// {
		// // 做你要做的事
		// }
		// layout_recommend.setVisibility( View.VISIBLE );
		// mHandler.postDelayed( recommendRun , 0 );
		// }
		// } );
		// }
		// }
		// }
		final SharedPreferences perferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String def = null;
		if (FunctionConfig.getTabdefaultHighlight() != null) {
			def = getTabTag(FunctionConfig.getTabdefaultHighlight());
		} else {
			def = TAG_THEME;
		}
		String currentTab = perferences.getString("currentTab", def);
		int tabIndex = getIntent().getIntExtra(
				StaticClass.EXTRA_MAIN_TAB_INDEX, 0);
		if (tabIndex != 1) {
			String tab = getIntent().getStringExtra("currentTab");
			if (tab != null) {
				tabHost.setCurrentTabByTag(tab);
			} else
				tabHost.setCurrentTabByTag(currentTab);
		}
		perferences.edit().putString("currentTab", tabHost.getCurrentTabTag())
				.commit();
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				Editor edit = perferences.edit();
				edit.putString("currentTab", tabId);
				edit.commit();
				if (tabId.equals(TAG_WALLPAPER) || tabId.equals(TAG_FONT)
						|| tabId.equals(TAG_EFFECT)) {
					if (layout_recommend != null)
						layout_recommend.setVisibility(View.INVISIBLE);
				}
				if (downModule != null) {
					String type = null;
					if (tabId.equals(TAG_THEME)) {
						type = DownloadList.Theme_Type;
					} else if (tabId.equals(TAG_LOCK)) {
						type = DownloadList.Lock_Type;
					} else if (tabId.equals(TAG_WALLPAPER)) {
						type = DownloadList.Wallpaper_Type;
					} else if (tabId.equals(TAG_FONT)) {
						type = DownloadList.Font_Type;
					} else if (tabId.equals(TAG_SCENE)) {
						type = DownloadList.Scene_Type;
					} else if (tabId.equals(TAG_WIDGET)) {
						type = DownloadList.Widget_Type;
					}
					downModule.resetdownThumbList(type);
				}
			}
		});
		int count = prefs.getInt("useCount", 0);
		prefs.edit().putInt("useCount", ++count).commit();
		DownloadList.getInstance(mContext).startUICenterLog(
				DownloadList.ACTION_USE_LOG, "", "");
		if (FunctionConfig.isEnableUpdateself())
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (updateManager == null) {
						updateManager = new UpdateManager(mContext);
					}
					updateManager.updateApkInfo(false);
				}
			});
	}

	private String getTabTag(String tabId) {
		String tag = null;
		if (tabId.equals(DownloadList.Theme_Type)) {
			tag = TAG_THEME;
		} else if (tabId.equals(TAG_LOCK)) {
			tag = TAG_LOCK;
		} else if (tabId.equals(DownloadList.Wallpaper_Type)) {
			tag = TAG_WALLPAPER;
		} else if (tabId.equals(DownloadList.Font_Type)) {
			tag = TAG_FONT;
		} else if (tabId.equals(DownloadList.Scene_Type)) {
			tag = TAG_SCENE;
		} else if (tabId.equals(DownloadList.Widget_Type)) {
			tag = TAG_WIDGET;
		} else if (tabId.equals("0")) {
			tag = TAG_EFFECT;
		} else {
			tag = TAG_THEME;
		}
		return tag;
	}

	private void bindData(Intent intent) {
		Bundle data = intent.getExtras();
		if (data != null) {
			Log.v("test", "MainActivity bindData size:" + data.size());
			com.coco.theme.themebox.util.FunctionConfig.setNetVersion(data
					.getBoolean("net_version", false));
			if (com.coco.theme.themebox.util.FunctionConfig.isNetVersion()) {
				Log.v("ThemeBox", "init turbo theme box");
				ThemesDB.LAUNCHER_PACKAGENAME = "com.cooeeui.brand.turbolauncher";
				PubContentProvider.LAUNCHER_AUTHORITY = "com.cooeeui.brand.turbolauncher.pub.provider";
			} else {
				com.coco.theme.themebox.service.ThemesDB.LAUNCHER_PACKAGENAME = data
						.getString("launcherPackageName");
			}
			com.coco.theme.themebox.service.ThemesDB.default_theme_package_name = data
					.getString("defaultThemePackageName");
			com.coco.theme.themebox.service.ThemesDB.ACTION_LAUNCHER_APPLY_THEME = data
					.getString("launcherApplyThemeAction");
			com.coco.theme.themebox.service.ThemesDB.ACTION_LAUNCHER_RESTART = data
					.getString("launcherRestartAction");
			com.coco.theme.themebox.util.FunctionConfig.personal_center_internal = data
					.getBoolean("personal_center_internal");
			com.coco.theme.themebox.util.FunctionConfig
					.setPage_effect_no_radom_style(data
							.getBoolean("page_effect_no_radom_style"));
			com.coco.theme.themebox.util.FunctionConfig
					.setCustomWallpaperPath(data
							.getString("customWallpaperPath"));
			com.coco.theme.themebox.util.FunctionConfig.setdoovStyle(data
					.getBoolean("isdoovStyle"));
			// com.coco.theme.themebox.util.FunctionConfig.setGalleryPkg(
			// data.getString( "galleryPkg" ) );
			com.coco.theme.themebox.util.FunctionConfig.setEffectVisiable(data
					.getBoolean("isEffectVisiable"));
			com.coco.theme.themebox.util.FunctionConfig.setAppliststring(data
					.getStringArray("app_list_string"));
			com.coco.theme.themebox.util.FunctionConfig
					.setWorkSpaceliststring(data
							.getStringArray("workSpace_list_string"));
			com.coco.theme.themebox.util.FunctionConfig
					.setDisableSetWallpaperDimensions(data
							.getBoolean("disableSetWallpaperDimensions"));
			com.coco.theme.themebox.util.FunctionConfig
					.setEnable_add_widget(data
							.getBoolean("enable_personalcenetr_click_widget_to_add"));
		} else {
			com.coco.theme.themebox.util.FunctionConfig.personal_center_internal = false;
			com.coco.theme.themebox.util.FunctionConfig.setdoovStyle(false);
			com.coco.theme.themebox.util.FunctionConfig
					.setEffectVisiable(false);
			com.coco.theme.themebox.util.FunctionConfig
					.setDisableSetWallpaperDimensions(false);
			com.coco.theme.themebox.util.FunctionConfig
					.setEnable_add_widget(false);
			Log.v("test", "MainActivity bindData null");
		}
	}

	private final String PERSONALBOX_CONFIG_FILENAME = "personalbox_config.xml";
	private final String CUSTOM_PERSONALBOX_CONFIG_FILENAME = "/system/launcher/personalbox_config.xml";
	private final String CUSTOM_FIRST_PERSONALBOX_CONFIG_FILENAME = "/system/oem/launcher/personalbox_config.xml";

	private void readDefaultData() {
		InputSource xmlin = null;
		File f1 = new File(CUSTOM_FIRST_PERSONALBOX_CONFIG_FILENAME);
		if (!f1.exists()) {
			f1 = new File(CUSTOM_PERSONALBOX_CONFIG_FILENAME);
		}
		boolean builtIn = (getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0;
		try {
			if (builtIn && f1.exists())
				xmlin = new InputSource(new FileInputStream(
						f1.getAbsolutePath()));
			else
				xmlin = new InputSource(mContext.getAssets().open(
						PERSONALBOX_CONFIG_FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (xmlin != null) {
			try {
				SAXParserFactory factoey = SAXParserFactory.newInstance();
				SAXParser parser = factoey.newSAXParser();
				XMLReader xmlreader = parser.getXMLReader();
				DefaultLayoutHandler handler = new DefaultLayoutHandler();
				parser = factoey.newSAXParser();
				xmlreader = parser.getXMLReader();
				xmlreader.setContentHandler(handler);
				xmlreader.parse(xmlin);
				handler = null;
				xmlin = null;
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class DefaultLayoutHandler extends DefaultHandler {

		public static final String GENERAl_CONFIG = "general_config";

		public DefaultLayoutHandler() {
		}

		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals(GENERAl_CONFIG)) {
				String temp;
				temp = atts.getValue("themebox_theme_visible");
				if (temp != null) {
					FunctionConfig.setThemeVisible(temp.equals("true"));
				}
				temp = atts.getValue("enable_wallpapervisible");
				if (temp != null) {
					FunctionConfig.setWallpaperVisible(temp.equals("true"));
				}
				temp = atts.getValue("enable_fontvisible");
				if (temp != null) {
					FunctionConfig.setFontVisible(temp.equals("true"));
				}
				temp = atts.getValue("show_theme_lock");
				if (temp != null) {
					FunctionConfig.setDisplayLock(temp.equals("true"));
				}
				temp = atts.getValue("themebox_widget_visible");
				if (temp != null) {
					// FunctionConfig.setShowWidgetTab( temp.equals( "true" ) );
					// if( sbGoogleVersion )
					// {
					FunctionConfig.setShowWidgetTab(true);
					// }
				}
				if (FunctionConfig.isNetVersion()) {
					FunctionConfig.setShowSceneTab(false);
				} else {
					temp = atts.getValue("themebox_scene_visible");
					if (temp != null) {
						FunctionConfig.setShowSceneTab(temp.equals("true"));
					}
				}
				temp = atts.getValue("themebox_hot_scene_visible");
				if (temp != null) {
					FunctionConfig.setShowHotScene(temp.equals("true"));
				}
				temp = atts.getValue("enable_hotlockVisible");
				if (temp != null) {
					FunctionConfig.setHotLockVisible(temp.equals("true"));
				}
				temp = atts.getValue("themebox_hot_widget_visible");
				if (temp != null) {
					FunctionConfig.setShowHotWidget(temp.equals("true"));
				}
				temp = atts.getValue("themebox_hot_wallpaper_visible");
				if (temp != null) {
					FunctionConfig.setShowHotWallpaper(temp.equals("true"));
				}
				temp = atts.getValue("themebox_hot_font_visible");
				if (temp != null) {
					FunctionConfig.setShowHotFont(temp.equals("true"));
				}
				temp = atts.getValue("enable_hotthemeVisible");
				if (temp != null) {
					FunctionConfig.setThemeHotVisible(temp.equals("true"));
				}
				temp = atts.getValue("themebox_livewallpaper_show");
				if (temp != null) {
					FunctionConfig.setLiveWallpaperShow(temp.equals("true"));
				}
				temp = atts.getValue("enable_lockset_visible");
				if (temp != null) {
					FunctionConfig.setLockSetVisible(temp.equals("true"));
				}
				temp = atts.getValue("enable_theme_introducation");
				if (temp != null) {
					FunctionConfig.setIntroductionVisible(temp.equals("true"));
				}
				temp = atts.getValue("show_theme_share");
				if (temp != null) {
					FunctionConfig.setShareVisible(temp.equals("true"));
				}
				temp = atts.getValue("enable_themebox_loading");
				if (temp != null) {
					FunctionConfig.setLoadingShow(temp.equals("true"));
				}
				temp = atts.getValue("enable_themebox_showmore");
				if (temp != null) {
					FunctionConfig.setThemeMoreShow(temp.equals("true"));
				}
				temp = atts.getValue("enable_themebox_showstar");
				if (temp != null) {
					FunctionConfig.setRecommendVisible(temp.equals("true"));
				}
				temp = atts
						.getValue("lockvisible_regradless_whether_install_lockbox");
				if (temp != null) {
					// FunctionConfig.setLockVisible( temp.equals( "true" ) );
					FunctionConfig.setLockVisible(false);
				}
				// temp = atts.getValue( "themebox_price_enable" );
				// if( temp != null )
				// {
				// FunctionConfig.setPriceVisible( temp.equals( "true" ) );
				// }
				temp = atts.getValue("enable_background_configuration_tab");
				if (temp != null) {
					FunctionConfig.setEnable_background_configuration_tab(temp
							.equals("true"));
				}
				// temp = atts.getValue( "enable_update_self" );
				// if( temp != null )
				// {
				// FunctionConfig.setEnableUpdateself( temp.equals( "true" ) );
				// }
				temp = atts.getValue("tab_sequence");
				if (temp != null) {
					if (temp.equals("nothing")) {
						FunctionConfig.setTab_sequence(null);
					} else {
						FunctionConfig.setTab_sequence(temp);
					}
				}
				temp = atts.getValue("tab_first_default_highlight");
				if (temp != null) {
					if (temp.equals("nothing")) {
						FunctionConfig.setTabdefaultHighlight(null);
					} else {
						FunctionConfig.setTabdefaultHighlight(temp);
					}
				}
				temp = atts.getValue("static_wallpapers_to_icon");
				if (temp != null) {
					FunctionConfig.setStatictoIcon(temp.equals("true"));
				}
				temp = atts.getValue("lockwallpaper_icon_show");
				if (temp != null) {
					FunctionConfig.setLockwallpaperShow(temp.equals("true"));
				}
				temp = atts.getValue("lockwallpaper_custom_path");
				if (temp != null) {
					if (temp.equals("nothing")) {
						FunctionConfig.setCustomLockWallpaperPath(null);
					} else {
						FunctionConfig.setCustomLockWallpaperPath(temp);
					}
				}
				temp = atts.getValue("enable_topwise_style");
				if (temp != null) {
					FunctionConfig.setEnable_topwise_style(temp.equals("true"));
				}
				temp = atts.getValue("enable_tophard_style");
				if (temp != null) {
					FunctionConfig.setEnable_tophard_style(temp.equals("true"));
					if (FunctionConfig.isEnable_tophard_style()) {
						FunctionConfig.setStatictoIcon(true);
						FunctionConfig.setLockwallpaperShow(true);
					}
				}
				temp = atts.getValue("enable_manual_update");
				if (temp != null) {
					FunctionConfig.setEnable_manual_update(temp.equals("true"));
				}
				temp = atts.getValue("wallpapers_from_other_apk");
				if (temp != null) {
					if (temp.equals("nothing")) {
						FunctionConfig.setWallpapers_from_other_apk(null);
					} else {
						FunctionConfig.setWallpapers_from_other_apk(temp);
					}
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (FunctionConfig.isEnable_manual_update()) {
			getMenuInflater().inflate(R.menu.activity_main, menu);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.menu_settings) {
			if (Tools.isServiceRunning(mContext,
					"com.coco.theme.themebox.update.UpdateService")) {
				Toast.makeText(mContext, R.string.soft_updating,
						Toast.LENGTH_SHORT);
			} else {
				if (updateManager == null) {
					updateManager = new UpdateManager(mContext);
				}
				updateManager.updateApkInfo(true);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private Runnable loading = new Runnable() {

		@Override
		public void run() {
			Log.v("loading", "----end----");
			load.setVisibility(View.GONE);
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		isExit = false;
		if (com.coco.theme.themebox.util.FunctionConfig
				.isStatusBarTranslucent()) {
			Log.v("status", "onCreate STATUSBAR_OPAQUE");
			String lostFocusAction = com.coco.theme.themebox.util.FunctionConfig
					.getLostFocusAction();
			if (lostFocusAction == null) {
				/* 通过反射机制来实现状态栏透明 */
				StatusBarUtils.setStatusBarBackgroundTransparent(mContext,
						false);
			} else {
				Intent intent = new Intent();
				intent.setAction(lostFocusAction);
				sendBroadcast(intent);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 255) {
			Log.d("mytag", "模拟点击哦");
		}
		if (requestCode == 2000) {//
			if (resultCode == Activity.RESULT_OK) {
				isChange = true;
			}
		} else if (requestCode == 2001) {
			WallpaperManager wallpaperManager = WallpaperManager
					.getInstance(this);
			WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
			if (wallpaperInfo != null) {
				isChange = true;
			}
		}
		if (isChange) {
			isChange = false;
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(this);
			pref.edit().putString("currentWallpaper", "other").commit();
			PubProviderHelper.addOrUpdateValue(
					PubContentProvider.LAUNCHER_AUTHORITY, "wallpaper",
					"currentWallpaper", "other");
			Intent intent = new Intent();
			intent.setAction("com.coco.wallpaper.update");
			sendBroadcast(intent);
		}
	}

	@Override
	public void onStop() {
		isChange = false;
		super.onStop();
		String lostFocusAction = com.coco.theme.themebox.util.FunctionConfig
				.getLostFocusAction();
		if (lostFocusAction == null) {
			/* 通过反射机制来实现状态栏透明 */
			StatusBarUtils.setStatusBarBackgroundTransparent(mContext, true);
		}
	}

	// teapotXu_20130304: add start
	// set a flag that indicates whether the ThemeSelectIcon launched the
	// ThemeBox or Launcher app.
	@Override
	protected void onResume() {
		super.onResume();
		if (updateManager != null) {
			updateManager.showDialog();
		}
		mDelayedStopHandler.removeCallbacksAndMessages(null);
		isExit = false;
		if (b_theme_icon_start_launcher == true) {
			String theme_icon_pkgName = getIntent().getStringExtra(
					"FROM_PACKAGE");
			// start the launcher directly
			ThemeService sv = new ThemeService(this);
			sv.applyTheme(sv.queryComponent(theme_icon_pkgName));
			sendBroadcast(new Intent(StaticClass.ACTION_DEFAULT_THEME_CHANGED));
			ActivityManager.KillActivity();
		}
		if (com.coco.theme.themebox.util.FunctionConfig.isDisplayLock()) {
			if (com.coco.theme.themebox.util.FunctionConfig
					.isRecommendVisible()) {
				if (layout_recommend != null) {
					RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_recommend
							.getLayoutParams();
					if (isScrolling) {
						lp.topMargin = -MAX_HEIGHT;
					}
					isScrolling = false;
					starIv.setBackgroundResource(R.drawable.star);
					starIv.startAnimation(starScaleAnim);
					layout_recommend.setLayoutParams(lp);
					imageView.setClickable(false);
					netPrompt.setVisibility(View.GONE);
					mProgressBar.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		// Zhongqihong@2014/12/24 DEL START
		// bindData(this.getIntent());
		// Zhongqihong@2014/12/24 DEL END
		if (intent != null) {
			String tab = intent.getStringExtra("currentTab");
			tabHost.setCurrentTabByTag(tab);
			Log.v("test", "onNewIntent tab :" + tab);
		}
	}

	// teapotXu_20130304: add end
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
					&& FunctionConfig.isLockSetVisible()) {
				// 在lock界面按设置才有用
				String tab = tabHost.getCurrentTabTag();
				if (tab.equals(TAG_LOCK)
						&& !PlatformInfo.getInstance(mContext)
								.isSupportViewLock()) {
					Intent intentSetting = new Intent();
					intentSetting.setClassName(getPackageName(),
							StaticClass.LOCKBOX_SETTING_ACTIVITY);
					startActivity(intentSetting);
				}
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ActivityManager.popupActivity(this);
		// teapotXu_20130304: add start
		// set a flag that indicates whether the ThemeSelectIcon launched the
		// ThemeBox or Launcher app.
		if (b_theme_icon_start_launcher == false) {
			if (FunctionConfig.isThemeVisible() && tabTheme != null)
				tabTheme.onDestroy();
		}
		if (downModule != null)
			downModule.dispose();
		// teapotXu_20130304: add end
		if (FunctionConfig.isWallpaperVisible()) {
			if (tabWallpaper != null) {
				tabWallpaper.onDestroy();
			}
		}
		if (FunctionConfig.isFontVisible()) {
			if (tabFont != null) {
				tabFont.onDestroy();
			}
		}
		if (FunctionConfig.isShowWidgetTab()) {
			if (tabWidget != null) {
				tabWidget.onDestroy();
			}
		}
		// if( com.coco.theme.themebox.util.FunctionConfig.isDisplayLock() )
		// {
		// if( com.coco.theme.themebox.util.FunctionConfig.isRecommendVisible()
		// )
		// {
		// if( b_theme_icon_start_launcher == false )
		// {
		// //this exception module is modified by Jone
		// //for fix an IllegalArgumentException exception thrown by system
		// //while network is not available.
		// try
		// {
		// unregisterReceiver( recommendReceiver );
		// }
		// catch( IllegalArgumentException e )
		// {
		// e.printStackTrace();
		// }
		// }
		// }
		// }
		if (FunctionConfig.isEffectVisiable()) {
			if (effect != null) {
				effect.onDestory();
			}
		}
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		isExit = true;
		// Jone add start, There is no necessary to restart Launcher if Personal
		// Center is embeded into Launcher,
		// or it will occurs an exception.
		if (!com.coco.theme.themebox.util.FunctionConfig.isNetVersion()) {
			if (PlatformInfo.getInstance(this).isSupportViewLock()
					|| !FunctionConfig.isDisplayLock()) {
				Message msg = mDelayedStopHandler.obtainMessage();
				mDelayedStopHandler.sendMessageDelayed(msg, KILL_DELAY);
			}
		}
		// Jone end
	}

	public static int getProxyPort(Context context) {
		int res = Proxy.getPort(context);
		if (res == -1)
			res = Proxy.getDefaultPort();
		return res;
	}

	public static boolean isCWWAPConnect(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if ((Proxy.getDefaultHost() != null || Proxy.getHost(context) != null)
					&& (Proxy.getPort(context) != -1 || Proxy.getDefaultPort() != -1)) {
				result = true;
			}
		}
		return result;
	}

	public static String getProxyHost(Context context) {
		String res = Proxy.getHost(context);
		if (res == null)
			res = Proxy.getDefaultHost();
		return res;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		isScrolling = true;
		mScrollY += distanceY;// distanceX:向左为正，右为负
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_recommend
				.getLayoutParams();
		lp.topMargin = lp.topMargin - (int) mScrollY;
		if (lp.topMargin <= -MAX_HEIGHT) {// 展开之后
			isScrolling = false;// 拖过头了不需要再执行AsynMove
			isUnfold = false;
			lp.topMargin = -MAX_HEIGHT;
			starIv.setBackgroundResource(R.drawable.star);
			starIv.startAnimation(starScaleAnim);
		}
		if (lp.topMargin >= 0) {// 收缩之后
			isScrolling = false;
			isUnfold = true;
			lp.topMargin = 0;
			starIv.clearAnimation();
			starIv.setBackgroundResource(R.drawable.close);
		}
		layout_recommend.setLayoutParams(lp);
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int y = (int) event.getRawY();
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_recommend
				.getLayoutParams();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isScrolling = true;
			pressY = y;
		} else if (event.getAction() == MotionEvent.ACTION_UP
				&& (isScrolling == true)) {
			imageView.setClickable(true);
			if (lp.topMargin <= -MAX_HEIGHT) {
				imageView.setClickable(true);
				new AsynMove().execute(new Integer[] { 30 });
			} else if (lp.topMargin >= 0) {
				imageView.setClickable(true);
				new AsynMove().execute(new Integer[] { -30 });
			} else if (y - pressY > 0) {
				if (y - pressY >= MAX_HEIGHT / 5) {
					new AsynMove().execute(new Integer[] { 30 });
				} else {
					new AsynMove().execute(new Integer[] { -30 });
				}
			} else {
				if (y - pressY <= -MAX_HEIGHT / 5) {
					new AsynMove().execute(new Integer[] { -30 });
				} else {
					new AsynMove().execute(new Integer[] { 30 });
				}
			}
		}
		return mGestureDetector.onTouchEvent(event);
	}

	private void calculatorWidth() {
		ViewTreeObserver observer = layout_recommend.getViewTreeObserver();
		// 为了取得控件的宽
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

			public boolean onPreDraw() {
				if (hasMeasured == false) {
					MAX_HEIGHT = 0 - layout_recommend.getTop();
					if (MAX_HEIGHT > 0) {
						hasMeasured = true;
					}
				}
				return true;
			}
		});
	}

	private Runnable recommendRun = new Runnable() {

		public void run() {
			loadDatas();
			listView.setAdapter(appAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (appInfos.get(arg2).getAppItemType().equals("app")) {
						if (isInstalled(appInfos.get(arg2).getAppPackage())) {
							PackageManager pm = getPackageManager();
							Intent intentActivity = pm
									.getLaunchIntentForPackage(appInfos.get(
											arg2).getAppPackage());
							startActivity(intentActivity);
						} else {
							Intent intentActivity = new Intent();
							intentActivity.setClass(MainActivity.this,
									LoadRecomandActivity.class);
							intentActivity.putExtra("name", appInfos.get(arg2)
									.getAppName());
							String[] str = appInfos.get(arg2).getAppApkUrl()
									.split(",");
							String url = str[(int) (Math.random() * 10)
									% (str.length)]
									+ "?p01="
									+ appInfos.get(arg2).getAppPackage()
									+ "&p06=1&";
							intentActivity.putExtra("apkurl", url);
							startActivity(intentActivity);
						}
					} else if (appInfos.get(arg2).getAppItemType()
							.equals("url")) {
						Uri uri = Uri.parse(appInfos.get(arg2).getAppPackage());
						startActivity(new Intent(Intent.ACTION_VIEW, uri));
					}
				}
			});
		}
	};

	public boolean isInstalled(String packname) {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					packname, 0);
			Log.d("isInstall", "packageInfo=" + packageInfo);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void loadDatas() {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor mCursor = db.query(Profile.TABLE_NAME, null, null, null, null,
				null, null);
		String language = Locale.getDefault().toString();
		if (appInfos != null) {
			appInfos.clear();
		}
		if (mCursor != null) {
			if (mCursor.getCount() > 0) {
				int index = 0;
				while (mCursor.moveToNext()) {
					if (index > 0) {
						AppInfos ai = new AppInfos();
						if (language.equals("zh_CN")
								|| language.equals("zh_TW")) {
							ai.setAppName(mCursor.getString(mCursor
									.getColumnIndex(Profile.COLUMN_NAME_CH)));
						} else {
							ai.setAppName(mCursor.getString(mCursor
									.getColumnIndex(Profile.COLUMN_NAME_EN)));
						}
						ai.setAppItemType(mCursor.getString(mCursor
								.getColumnIndex(Profile.COLUMN_ITEMTYPE)));
						ai.setAppPackage(mCursor.getString(mCursor
								.getColumnIndex(Profile.COLUMN_PACKAGE)));
						ai.setAppApkUrl(mCursor.getString(mCursor
								.getColumnIndex(Profile.COLUMN_URL_APK)));
						ai.setAppIconName(mCursor.getString(mCursor
								.getColumnIndex(Profile.COLUMN_ICON)));
						Bitmap bitimap = null;
						bitimap = BitmapFactory.decodeFile(PathTool
								.getRecommendDir() + "/" + ai.getAppIconName());
						if (bitimap == null) {
							bitimap = ((BitmapDrawable) getResources()
									.getDrawable(R.drawable.ic_launcher))
									.getBitmap();
							String iconUrl = appIconUrl[(int) (Math.random() * 10)
									% (appIconUrl.length)]
									+ ai.getAppIconName();
							try {
								new IconAsyncTask(this).execute(iconUrl,
										PathTool.getRecommendDir(),
										ai.getAppIconName());
							} catch (RejectedExecutionException e) {
								e.printStackTrace();
							}
						}
						ai.setAppIcon(bitimap);
						appInfos.add(ai);
					} else if (index == 0) {
						appIconUrl = mCursor
								.getString(
										mCursor.getColumnIndex(Profile.COLUMN_URL_ICON))
								.split(",");
					}
					index++;
				}
			}
		}
		if (mCursor != null) {
			if (mCursor.getCount() > 0) {
				mHandler.removeCallbacks(promptRun);
				mProgressBar.setVisibility(View.GONE);
				netPrompt.setVisibility(View.GONE);
			} else {
				netPrompt.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
				mHandler.postDelayed(promptRun, 1000 * 30);
			}
		} else {
			netPrompt.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			mHandler.postDelayed(promptRun, 1000 * 30);
		}
		if (mCursor != null) {
			mCursor.close();
		}
		db.close();
	}

	private Runnable promptRun = new Runnable() {

		@Override
		public void run() {
			netPrompt.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}
	};

	public class AppAdapter extends BaseAdapter {

		private Context mAdapterContext;
		private ArrayList<AppInfos> appInfo;

		public AppAdapter(Context c, ArrayList<AppInfos> appInfo) {
			this.mAdapterContext = c;
			this.appInfo = appInfo;
		}

		public int getCount() {
			return appInfos.size();
		}

		public Object getItem(int position) {
			return appInfos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (appInfo == null) {
				return null;
			}
			ImageView imageView = null;
			TextView textView1 = null;
			convertView = View.inflate(mAdapterContext, R.layout.push_app_item,
					null);
			imageView = (ImageView) convertView.findViewById(R.id.app_icon);
			textView1 = (TextView) convertView.findViewById(R.id.app_name);
			AppInfos info = appInfo.get(position);
			imageView.setImageBitmap(info.app_icon);
			textView1.setText(info.app_name);
			return convertView;
		}
	}

	public class AppInfos {

		private Bitmap app_icon;
		private String app_icon_name;
		private String app_name;
		private String app_describe;
		private String app_item_type;
		private String app_package;
		private String app_apk_url;
		private String app_icon_url;

		public Bitmap getImages() {
			return app_icon;
		}

		public void setImages(Bitmap bitmap) {
			this.app_icon = bitmap;
		}

		public String getAppName() {
			return app_name;
		}

		public void setAppName(String appName) {
			app_name = appName;
		}

		public String getAppDescribe() {
			return app_describe;
		}

		public void setAppDescribe(String appDescribe) {
			app_describe = appDescribe;
		}

		public String getAppItemType() {
			return app_item_type;
		}

		public void setAppItemType(String appItemType) {
			app_item_type = appItemType;
		}

		public String getAppPackage() {
			return app_package;
		}

		public void setAppPackage(String appPackage) {
			app_package = appPackage;
		}

		public String getAppApkUrl() {
			return app_apk_url;
		}

		public void setAppApkUrl(String appApkUrl) {
			app_apk_url = appApkUrl;
		}

		public String getAppIconUrl() {
			return app_icon_url;
		}

		public void setAppIconUrl(String appIconUrl) {
			app_icon_url = appIconUrl;
		}

		public String getAppIconName() {
			return app_icon_name;
		}

		public void setAppIconName(String appIconName) {
			app_icon_name = appIconName;
		}

		public Bitmap getAppIcon() {
			return app_icon;
		}

		public void setAppIcon(Bitmap appIcon) {
			app_icon = appIcon;
		}
	}

	// private BroadcastReceiver recommendReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(
	// Context context ,
	// Intent intent )
	// {
	// if( intent.getAction().equals( StaticClass.ACTION_THEME_UPDATE_RECOMMEND
	// ) )
	// {
	// if( com.coco.theme.themebox.util.FunctionConfig.isLockVisible() )
	// {
	// loadDatas();
	// appAdapter.notifyDataSetChanged();
	// }
	// }
	// else if( intent.getAction().equals(
	// ConnectivityManager.CONNECTIVITY_ACTION ) )
	// {
	// if( com.coco.theme.themebox.util.FunctionConfig.isLockVisible() )
	// {
	// if( StaticClass.isHaveInternet( context ) )
	// {
	// SQLiteDatabase db = mDbHelper.getReadableDatabase();
	// Cursor mCursor = db.query( Profile.TABLE_NAME , null , null , null , null
	// , null , null );
	// if( mCursor == null || mCursor.getCount() <= 0 )
	// {
	// String[] str = { "http://yu01.coomoe.com/uimenu/getlist.ashx" ,
	// "http://yu02.coomoe.com/uimenu/getlist.ashx" };
	// String oldVersion = "";
	// String url = "";
	// SharedPreferences sharedPrefer1 =
	// PreferenceManager.getDefaultSharedPreferences( context );
	// oldVersion = sharedPrefer1.getString( "recommendVersion" , "" );
	// if( oldVersion != null )
	// {
	// if( oldVersion.equals( "" ) )
	// {
	// url = str[(int)( Math.random() * 10 ) % ( str.length )] +
	// "?p07=com.coco.lock2.lockbox" + "&p02=" + getVersionCode( context ) + "&"
	// + Assets
	// .getPhoneParams( context );
	// }
	// else
	// {
	// url = str[(int)( Math.random() * 10 ) % ( str.length )] +
	// "?p07=com.coco.lock2.lockbox" + "&p02=" + getVersionCode( context ) +
	// "&p08=" + oldVersion + "&" + Assets
	// .getPhoneParams( context );
	// }
	// }
	// else
	// {
	// url = str[(int)( Math.random() * 10 ) % ( str.length )] +
	// "?p07=com.coco.lock2.lockbox" + "&p02=" + getVersionCode( context ) + "&"
	// + Assets.getPhoneParams( context );
	// }
	// new MyAsyncTask( context ).execute( url );
	// if( netPrompt != null )
	// {
	// netPrompt.setVisibility( View.GONE );
	// mProgressBar.setVisibility( View.VISIBLE );
	// }
	// }
	// db.close();
	// }
	// }
	// }
	// else if( intent.getAction().equals( Intent.ACTION_WALLPAPER_CHANGED ) )
	// {
	// isChange = true;
	// }
	// }
	// };
	/**
	 * 获取软件版本�?
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (isUnfold) {
				new AsynMove().execute(new Integer[] { -30 });
				isUnfold = false;
				return true;
			}
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	class AsynMove extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int times;
			if (MAX_HEIGHT % Math.abs(params[0]) == 0)// 整除
				times = MAX_HEIGHT / Math.abs(params[0]);
			else
				times = MAX_HEIGHT / Math.abs(params[0]) + 1;// 有余
			for (int i = 0; i < times; i++) {
				publishProgress(params);
				try {
					Thread.sleep(Math.abs(params[0]));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... params) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout_recommend
					.getLayoutParams();
			if (params[0] < 0)
				lp.topMargin = Math.max(lp.topMargin + params[0], -MAX_HEIGHT);
			else
				lp.topMargin = Math.min(lp.topMargin + params[0], 0);
			if (lp.topMargin >= 0) {// 展开之后
				imageView.setClickable(true);
				starIv.clearAnimation();
				starIv.setBackgroundResource(R.drawable.close);
				isScrolling = false;
				isUnfold = true;
			} else if (lp.topMargin <= (-MAX_HEIGHT)) {// 收缩之后
				imageView.setClickable(false);
				starIv.setBackgroundResource(R.drawable.star);
				starIv.startAnimation(starScaleAnim);
				isScrolling = false;
				isUnfold = false;
			}
			layout_recommend.setLayoutParams(lp);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action != null) {
				if (action.equals("com.cooee.launcher.action.start")
						|| action.equals("com.cooee.scene.action.SHOW_IDLE")) {
					if (mDelayedStopHandler != null) {
						if (PlatformInfo.getInstance(MainActivity.this)
								.isSupportViewLock()) {
							Message msg = mDelayedStopHandler.obtainMessage();
							mDelayedStopHandler.sendMessageDelayed(msg,
									KILL_DELAY);
						}
					}
				} else if (action.equals("com.coco.action.TAB_CHANGED")) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							ConfigurationTabService service = new ConfigurationTabService(
									MainActivity.this);
							listTab = service.queryTabList();
							resetTabs();
							runOnUiThread(new Runnable() {

								public void run() {
									if (progress != null
											&& progress.getVisibility() == View.VISIBLE) {
										progress.setVisibility(View.GONE);
									}
									if (tabHost.getTabWidget() == null) {
										initContentView(bundle);
									}
								}
							});
						}
					}).start();
				}
			}
		}
	}

	private void resetTabs() {
		if (FunctionConfig.isEnable_background_configuration_tab()) {
			FunctionConfig.setThemeVisible(false);
			FunctionConfig.setDisplayLock(false);
			FunctionConfig.setWallpaperVisible(false);
			FunctionConfig.setFontVisible(false);
			FunctionConfig.setShowWidgetTab(false);
			FunctionConfig.setShowSceneTab(false);
			FunctionConfig.setLiveWallpaperShow(false);
			for (String item : listTab) {
				if (item.equals(DownloadList.Theme_Type)) {
					FunctionConfig.setThemeVisible(true);
					continue;
				}
				if (item.equals(DownloadList.Lock_Type)) {
					FunctionConfig.setDisplayLock(true);
					continue;
				}
				if (item.equals(DownloadList.Wallpaper_Type)) {
					FunctionConfig.setWallpaperVisible(true);
					continue;
				}
				if (item.equals(DownloadList.Font_Type)) {
					FunctionConfig.setFontVisible(true);
					continue;
				}
				if (item.equals(DownloadList.Widget_Type)) {
					FunctionConfig.setShowWidgetTab(true);
					continue;
				}
				if (item.equals(DownloadList.Scene_Type)) {
					FunctionConfig.setShowSceneTab(true);
					continue;
				}
				if (item.equals(DownloadList.LiveWallpaper_Type)) {
					FunctionConfig.setLiveWallpaperShow(true);
					continue;
				}
			}
		}
	}
}
