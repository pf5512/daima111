package com.coco.theme.themebox;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import com.coco.lock2.lockbox.DownloadLockBoxService;
import com.coco.lock2.lockbox.OnPanelStatusChangedListener;
import com.coco.lock2.lockbox.TabLockFactory;
import com.coco.lock2.lockbox.util.DownModule;
import com.coco.lock2.lockbox.util.PhoneUrlParams;
import com.coco.theme.themebox.apprecommend.IconAsyncTask;
import com.coco.theme.themebox.apprecommend.LoadRecomandActivity;
import com.coco.theme.themebox.apprecommend.MyAsyncTask;
import com.coco.theme.themebox.apprecommend.Profile;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.PathTool;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.cooee.statistics.StatisticsBase;
import com.cooee.statistics.StatisticsExpand;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.LocalActivityManager;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.coco.theme.themebox.util.Log;

public class MainActivity extends Activity implements OnPanelStatusChangedListener,OnTouchListener,GestureDetector.OnGestureListener{
	private final String TAG_THEME = "tagTheme";
	private final String TAG_LOCK = "tagLock";
	private DownModule downModule;
	private TabLockFactory tabLock;
	private ThemeDownModule themedownModule;
	private TabThemeFactory tabTheme;
	private TabHost tabHost;

	//teapotXu_20130304: add start
	//set a flag that indicates whether the ThemeSelectIcon launched the ThemeBox or Launcher app.	
	private boolean b_theme_icon_start_launcher = false;
	//teapotXu_20130304: add end
	
	private LinearLayout layout_recommend;
	private ImageView iv;
	private GestureDetector mGestureDetector;
	private boolean hasMeasured=false;
	private boolean isScrolling=false;
	private float mScrollY;
	private int MAX_HEIGHT=0;
	
//	private Animation starScaleAnim;
	private ListView listView;
	private ArrayList<AppInfos> appInfos=null;
	private AppAdapter appAdapter;
	private String appIconUrl[];
	private ImageView imageView;
	private boolean isUnfold = false;
	private ImageView starIv;
	private int pressY;
	private ProgressBar mProgressBar;
	private TextView netPrompt=null;
	private Context mContext;
	private Handler mHandler = new Handler();
	private AnimationDrawable draw;
	
	public static String PATH_ENABLE_LOG = "enablelog.log";	// added by zhenNan.ye
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActivityManager.pushActivity(this);
		super.onCreate(savedInstanceState);		
		mContext = this;
		
		// added by zhenNan.ye begin
		String sdpath = getSDPath();
		if (sdpath != null) {
			PATH_ENABLE_LOG = sdpath + File.separator + "enablelog.log";
			File dir = new File(PATH_ENABLE_LOG);
			if (dir.exists()) {
				Log.setEnableLog(true);
			}
		}
		// added by zhenNan.ye end
		
		//xiatian add start	//Statistics
		if(StatisticsExpand.getEnableStatisticsInLauncher() == false)
		{
			StatisticsBase.setApplicationContext(this.getApplicationContext());
			StatisticsBase.setLogSenderDelayed(60);
			StatisticsBase.loadAppChannel(this);
		}
		//xiatian add end
		
		if (!com.coco.theme.themebox.StaticClass.isAllowDownload(mContext) && com.coco.theme.themebox.util.FunctionConfig.isDownToInternal()) {
			com.coco.theme.themebox.StaticClass.canDownToInternal = true; 
		}
		PathTool.makeDirApp();
//teapotXu_20130304: add start
		//set a flag that indicates whether the ThemeSelectIcon launched the ThemeBox or Launcher app.
		String pkgNameFromThemeBox = getIntent().getStringExtra("FROM_PACKAGE");
		
		if(pkgNameFromThemeBox != null && pkgNameFromThemeBox.length() > 16 && pkgNameFromThemeBox.substring(0, 16).equals("com.coco.themes.")){
			b_theme_icon_start_launcher = true;
			return;
		}

//teapotXu_20130304: add end		
		downModule = new DownModule(this);
		themedownModule = new ThemeDownModule(this);
		if (!com.coco.theme.themebox.util.FunctionConfig.isLockVisible()&& !com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(this)){
			setContentView(R.layout.man_tab_nolock);		
		}else{
			setContentView(R.layout.main_tab_lock);		
		}
		tabHost = (TabHost)findViewById(R.id.tabhost);
		LocalActivityManager groupActivity = new LocalActivityManager(this,
				false);
		groupActivity.dispatchCreate(savedInstanceState);
		tabHost.setup(groupActivity);
		// 添加主题页面
		
		tabTheme = new TabThemeFactory(MainActivity.this,themedownModule);
		final View indicatorTheme = View.inflate(MainActivity.this,
				R.layout.indicator_theme, null);
		tabHost.addTab(tabHost.newTabSpec(TAG_THEME)
				.setIndicator(indicatorTheme)
				.setContent(tabTheme));
		
		if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
				|| com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(this)) {
			int Screen_W = getWindowManager().getDefaultDisplay().getWidth();
			int Screen_H = getWindowManager().getDefaultDisplay().getHeight();
			// 添加锁屏页面
			tabLock = new TabLockFactory(MainActivity.this, downModule, Screen_W, Screen_H);
			View indicatorLock = View.inflate(MainActivity.this, R.layout.indicator_lock, null);
			tabHost.addTab(tabHost.newTabSpec(TAG_LOCK).setIndicator(indicatorLock)
					.setContent(tabLock));
			
			int tabIndex = getIntent().getIntExtra(StaticClass.EXTRA_MAIN_TAB_INDEX, 0);
			switch (tabIndex) {
			default:
			case 0:
				tabHost.setCurrentTabByTag(TAG_THEME);
				break;
			case 1:
				tabHost.setCurrentTabByTag(TAG_LOCK);
				break;
			}
		}		
		if (com.coco.theme.themebox.util.FunctionConfig.isRecommendVisible()) {
		IntentFilter recommendFilter = new IntentFilter();
		recommendFilter.addAction(StaticClass.ACTION_THEME_UPDATE_RECOMMEND);
		recommendFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(recommendReceiver, recommendFilter);
		if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
				&& com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(this)) {
			layout_recommend = (LinearLayout)findViewById(R.id.mainRelLayout);
			mProgressBar = (ProgressBar)findViewById(R.id.circleProgressBar);
			netPrompt = (TextView)findViewById(R.id.internetPrompt);
			imageView = (ImageView)findViewById(R.id.imageViewRecom);
			imageView.setClickable(false);
			iv = (ImageView)findViewById(R.id.labelImageView);
			iv.setOnTouchListener(this);
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				}
			});
			mGestureDetector = new GestureDetector(this,this);
	  		mGestureDetector.setIsLongpressEnabled(false);
	  		
	        calculatorWidth();
	     
	        starIv = (ImageView)findViewById(R.id.labelImageView);
	        starIv.setBackgroundResource(R.anim.lvteng);
	        draw = (AnimationDrawable) starIv.getBackground(); 
	        
//	        starScaleAnim = AnimationUtils.loadAnimation(this, R.anim.star_scale_anim);
//			starIv = (ImageView)findViewById(R.id.starImageView);
//			starIv.startAnimation(starScaleAnim);
			listView = (ListView)findViewById(R.id.pushListView);
			appInfos = new ArrayList<AppInfos>();
			appAdapter = new AppAdapter(MainActivity.this,appInfos);
			
			if (tabHost.getCurrentTab() == 1) {
				layout_recommend.setVisibility(View.VISIBLE);
				mHandler.postDelayed(recommendRun, 0);
			}
			tabHost.getTabWidget().getChildAt(0).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tabHost.getCurrentTab() != 0) {//一定要判断这个是为了防止阻碍切换事�?
						tabHost.setCurrentTab(0);  
	                }else{  
	                	//做你要做的事
	                }  
					layout_recommend.setVisibility(View.INVISIBLE);
				} 
				
			});
			tabHost.getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (tabHost.getCurrentTab() != 1) {//一定要判断这个是为了防止阻碍切换事�?
						tabHost.setCurrentTab(1);  
	                }else{  
	                	//做你要做的事
	                }  
					layout_recommend.setVisibility(View.VISIBLE);
					mHandler.postDelayed(recommendRun, 0);
				} 
				
			});
			}
	}
		//友盟  进入主题盒子统计
		MobclickAgent.onEvent(mContext, "StartBox");
		//友盟 新用户统计
		NewUser(mContext);
		//友盟 活跃用户统计
		ActiveUser(mContext);
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus)
		{
			if(draw != null){
				draw.setOneShot(false);
				draw.start();				
			}
		}
		else
		{
			if(draw != null){
				draw.stop();				
			}
		}
		Log.v("hasFocus", "hasFocus = "+hasFocus);
		}
	
//teapotXu_20130304: add start
	//set a flag that indicates whether the ThemeSelectIcon launched the ThemeBox or Launcher app.	
	@Override
	protected void onResume(){
		super.onResume();
		MobclickAgent.onResume(this);
		if(b_theme_icon_start_launcher == true){
			String theme_icon_pkgName = getIntent().getStringExtra("FROM_PACKAGE");
			
			StatisticsExpand.LauncherFastApplyTheme(mContext, theme_icon_pkgName);	//xiatian add	//Statistics
			
			//start the launcher directly
			ThemeService sv = new ThemeService(this);
			
			sv.applyTheme(sv.queryComponent(theme_icon_pkgName));
			sendBroadcast(new Intent(StaticClass.ACTION_DEFAULT_THEME_CHANGED));
			ActivityManager.KillActivity();			
		}
		if (com.coco.theme.themebox.util.FunctionConfig.isRecommendVisible()) {
		if (layout_recommend != null) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)layout_recommend.getLayoutParams();
			if (isScrolling) {
				lp.topMargin=-MAX_HEIGHT;
			}
			isScrolling=false;
//			starIv.setBackgroundResource(R.drawable.star);
//	    	starIv.startAnimation(starScaleAnim);
	    	layout_recommend.setLayoutParams(lp);
	    	imageView.setClickable(false);
	    	netPrompt.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}
		}
	}
//teapotXu_20130304: add end	
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	  switch(keyCode){
	     case KeyEvent.KEYCODE_MENU:
			if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
					&& com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(this)) {
				// 在lock界面按设置才有用
				int tabIndex = tabHost.getCurrentTab();
				if (tabIndex == 1 && !isUnfold && !isScrolling) {
					Intent intentSetting = new Intent();
					intentSetting.setClassName(StaticClass.LOCKBOX_PACKAGE_NAME,
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
	//teapotXu_20130304: add start
		//set a flag that indicates whether the ThemeSelectIcon launched the ThemeBox or Launcher app.			
		if(b_theme_icon_start_launcher == false){
			if (tabLock != null)
				tabLock.onDestroy();
			tabTheme.onDestroy();
			downModule.dispose();
			themedownModule.dispose();
		}
	//teapotXu_20130304: add end		
		if (com.coco.theme.themebox.util.FunctionConfig.isRecommendVisible()) {
			unregisterReceiver(recommendReceiver);			
		}
		super.onDestroy();
	}
	
	public static int getProxyPort(Context context) {
		int res = Proxy.getPort(context);
		if(res == -1)res = Proxy.getDefaultPort();
		return res;
	}

	public static boolean isCWWAPConnect(Context context) {
		boolean result = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			if ((Proxy.getDefaultHost() != null
					|| Proxy.getHost(context) != null)&&(Proxy.getPort(context) != -1 || Proxy.getDefaultPort() != -1)) {
				result = true;
			}
		}
		return result;
	}

	public static int getNetWorkType(Context context) {
		int netType = -1;

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000")
					|| simOperator.startsWith("46002")) {
				netType = DownloadLockBoxService.NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				netType = DownloadLockBoxService.NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				netType = DownloadLockBoxService.NETTYPE_TELECOM;
			}
		}
		return netType;
	}
	
	public static String getProxyHost(Context context) {
		String res = Proxy.getHost(context);
		if(res == null)res = Proxy.getDefaultHost();
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
		isScrolling=true;
		mScrollY+=distanceY;//distanceX:向左为正，右为负
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)layout_recommend.getLayoutParams();
		lp.topMargin=lp.topMargin-(int)mScrollY;
		if(lp.topMargin<=-MAX_HEIGHT){//展开之后  
			isScrolling=false;//拖过头了不需要再执行AsynMove
			isUnfold = false;
			lp.topMargin=-MAX_HEIGHT;
//			starIv.setBackgroundResource(R.drawable.star);
//        	starIv.startAnimation(starScaleAnim);
			onPanelOpened();//调用OPEN回调函数  
		}
		if(lp.topMargin>=0){//收缩之后  
			isScrolling=false;
			isUnfold = true;
			lp.topMargin=0;
//			starIv.clearAnimation();
//        	starIv.setBackgroundResource(R.drawable.close);
			onPanelClosed();//调用CLOSE回调函数  
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
		int y = (int)event.getRawY();
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)layout_recommend.getLayoutParams();
		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			isScrolling = true;
			pressY = y;
		}else if(event.getAction()==MotionEvent.ACTION_UP && (isScrolling==true)){
			imageView.setClickable(true);
			if (lp.topMargin <= -MAX_HEIGHT) {
				imageView.setClickable(true);
				new AsynMove().execute(new Integer[] { 30 });
			} else if (lp.topMargin >= 0){
				imageView.setClickable(true);
				new AsynMove().execute(new Integer[] { -30 });
			}else if (y - pressY > 0) {
				
				if (y - pressY >= MAX_HEIGHT/5) {
					new AsynMove().execute(new Integer[] { 30 });
				} else {
					new AsynMove().execute(new Integer[] { -30 });
				}
			}else {
				if (y - pressY <= -MAX_HEIGHT/5) {
					new AsynMove().execute(new Integer[] { -30 });
				} else {
					new AsynMove().execute(new Integer[] { 30 });
				}
			}
		}
		return mGestureDetector.onTouchEvent(event); 
	}

	@Override
	public void onPanelOpened() {
	
	}

	@Override
	public void onPanelClosed() {

	}
	
	private void calculatorWidth(){
		ViewTreeObserver observer = layout_recommend.getViewTreeObserver();
      //为了取得控件的宽
      observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
          public boolean onPreDraw(){
              if (hasMeasured == false){
            	  MAX_HEIGHT = 0-layout_recommend.getTop();
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
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					if (appInfos.get(arg2).getAppItemType().equals("app")) {

						if (isInstalled(appInfos.get(arg2).getAppPackage())) {
							PackageManager pm = getPackageManager();
							Intent intentActivity = pm
									.getLaunchIntentForPackage(appInfos.get(arg2).getAppPackage());
							startActivity(intentActivity);
						}else {
							Intent intentActivity = new Intent();
							intentActivity.setClass(MainActivity.this,
									LoadRecomandActivity.class);
							intentActivity.putExtra("name", appInfos.get(arg2).getAppName());
							String[] str = appInfos.get(arg2).getAppApkUrl().split(",");
							String url = str[(int) (Math.random() * 10)
													% (str.length)]+"?p01="+ appInfos.get(arg2).getAppPackage()+"&p06=1&";
							intentActivity.putExtra("apkurl", url);
							startActivity(intentActivity);
						}
					} else if (appInfos.get(arg2).getAppItemType().equals("url")) {
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

	public void loadDatas(){
		Cursor mCursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null);
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
						bitimap = BitmapFactory.decodeFile(getRecommendDir()+"/"+ai.getAppIconName());
						if (bitimap == null) {
							bitimap = ((BitmapDrawable) getResources().getDrawable(
									R.drawable.ic_launcher)).getBitmap();
							String iconUrl = appIconUrl[(int) (Math.random() * 10)
													% (appIconUrl.length)]
													+ ai.getAppIconName();
							new IconAsyncTask(this).execute(iconUrl,getRecommendDir(),ai.getAppIconName());
						}
						ai.setAppIcon(bitimap);
						appInfos.add(ai);
					}else if (index == 0) {
						appIconUrl = mCursor.getString(mCursor
								.getColumnIndex(Profile.COLUMN_URL_ICON)).split(",");
					}
					index++;
				}
			}
//			mCursor.close();
		}
		if (mCursor != null) {
			if (mCursor.getCount() > 0) {
				mHandler.removeCallbacks(promptRun);
				mProgressBar.setVisibility(View.GONE);
				netPrompt.setVisibility(View.GONE);
			}else {
				netPrompt.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
				mHandler.postDelayed(promptRun, 1000 * 30);
			}
		}else {
			netPrompt.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			mHandler.postDelayed(promptRun, 1000 * 30);
		}
		
		if (mCursor != null) {
			mCursor.close();
		}
	}
	private String getRecommendDir() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/Coco/Lock2/recommend";
		new File(path).mkdirs();
		return path;
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
		public AppAdapter(Context c,ArrayList<AppInfos> appInfo) {
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
			convertView = View.inflate(mAdapterContext,R.layout.push_app_item, null);
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
	 
	private BroadcastReceiver recommendReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(StaticClass.ACTION_THEME_UPDATE_RECOMMEND)) {
				if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
						&& com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(context)) {
					loadDatas();
					appAdapter.notifyDataSetChanged();
				}
			}else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (com.coco.theme.themebox.util.FunctionConfig.isLockVisible()
						&& com.coco.lock2.lockbox.StaticClass.isLockBoxInstalled(context)) {
					if (StaticClass.isHaveInternet(context)) {
						Cursor mCursor = getContentResolver().query(Profile.CONTENT_URI, null, null, null, null);
						if (mCursor == null || mCursor.getCount() <= 0) {
							String[] str = { "http://yu01.coomoe.com/uimenu/getlist.ashx",
									"http://yu02.coomoe.com/uimenu/getlist.ashx" };
							String oldVersion = "";
							String url = "";
							SharedPreferences sharedPrefer1 = PreferenceManager.getDefaultSharedPreferences(context);
							oldVersion = sharedPrefer1.getString("recommendVersion", "");
							oldVersion="";
							if (oldVersion != null) {
								if (oldVersion.equals("")) {
									url = str[(int) (Math.random() * 10) % (str.length)]+"?p07=com.coco.lock2.lockbox"
											+ "&p02=" + getVersionCode(context)
											+ "&" + getPhoneParams();
								}else {
									url = str[(int) (Math.random() * 10) % (str.length)]+"?p07=com.coco.lock2.lockbox"
											+ "&p02=" + getVersionCode(context)
											+ "&p08=" + oldVersion + "&" + getPhoneParams();
								}
							}else {
								url = str[(int) (Math.random() * 10) % (str.length)]+"?p07=com.coco.lock2.lockbox"
										+ "&p02=" + getVersionCode(context)
										+ "&" + getPhoneParams();
							}
							new MyAsyncTask(context).execute(url);
							if (netPrompt != null) {
								netPrompt.setVisibility(View.GONE);
								mProgressBar.setVisibility(View.VISIBLE);
							}
						}
					}
				}
			}
		}
	};
	/**
	 * 获取软件版本号
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
	/**
	 * 获取手机的其他信息
	 */
	private String getPhoneParams() {
		return new PhoneUrlParams().getParams(this.mContext);
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
        	RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)layout_recommend.getLayoutParams();
            if (params[0] < 0)  
                lp.topMargin = Math.max(lp.topMargin + params[0], -MAX_HEIGHT);
            else  
                lp.topMargin = Math.min(lp.topMargin + params[0], 0);
  
            if(lp.topMargin>=0){//展开之后  
            	imageView.setClickable(true);
//            	starIv.clearAnimation();
//            	starIv.setBackgroundResource(R.drawable.close);
            	isScrolling = false;
            	isUnfold = true;
                onPanelOpened();//调用OPEN回调函数  
            }
            else if(lp.topMargin<=(-MAX_HEIGHT)){//收缩之后  
            	imageView.setClickable(false);
//            	starIv.setBackgroundResource(R.drawable.star);
//            	starIv.startAnimation(starScaleAnim);
            	isScrolling = false;
            	isUnfold = false;
                onPanelClosed();//调用CLOSE回调函数  
            }
            layout_recommend.setLayoutParams(lp);  
        }  
    }
	
	// added by zhenNan.ye 20130724
	private String getSDPath() {
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDdir = Environment.getExternalStorageDirectory();
		}
		if (SDdir != null) {
			return SDdir.toString();
		} else {
			return null;
		}
	}
	
	private void NewUser(Context content) {
		SharedPreferences sp = content.getSharedPreferences("theme_analytics", Context.MODE_PRIVATE);
		Boolean flag = sp.getBoolean("NewUser", false);
		if (!flag) {
			MobclickAgent.onEvent(content, "NewUser");
			Editor sharedata = sp.edit();
			sharedata.putBoolean("NewUser", true);
			sharedata.commit();
		}

	}

	private void ActiveUser(Context content) {
		Time time = new Time("GMT+8");
		time.setToNow();
		int day = time.yearDay;
		SharedPreferences sp = content.getSharedPreferences("theme_analytics", Context.MODE_PRIVATE);
		int aday = sp.getInt("Day", -1);
		if (day != aday) {
			MobclickAgent.onEvent(content, "ActiveUser");
			Editor sharedata = sp.edit();
			sharedata.putInt("Day", day);
			sharedata.commit();
		}

	}
}
