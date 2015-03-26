package com.coco.font.fontbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.coco.download.DownloadList;
import com.coco.theme.themebox.DownloadApkContentService;
import com.coco.theme.themebox.PullToRefreshView;
import com.coco.theme.themebox.PullToRefreshView.OnFooterRefreshListener;
import com.coco.theme.themebox.PullToRefreshView.OnHeaderRefreshListener;
import com.coco.theme.themebox.util.DownModule;
import com.coco.theme.themebox.util.FunctionConfig;
import com.coco.theme.themebox.util.Log;
import com.iLoong.base.themebox.R;

public class TabFontFactory implements TabHost.TabContentFactory,
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private Context mContext;
	private GridView gridViewLocal;
	private GridView gridViewHot;
	private FontGridHotAdapter hotAdapter;
	private ViewPager gridPager;
	private GridPagerAdapter gagerAdapter;
	private DownModule downModule;
	private final int INDEX_LOCAL = 0;
	private final int INDEX_HOT = 1;
	private View hotView;
	private PullToRefreshView mPullToRefreshView;
	private boolean footerRefresh = false;
	private boolean headerRefresh = false;
	private Handler handler = new Handler();
	private boolean listRefresh = false;
	private static boolean interneterr = false;
	List<Map<String, Object>> fontsList = new ArrayList<Map<String, Object>>();
	GridLocalFontAdapter mAdapter;
	FontMessenger.ServiceConnected servicecon = new FontMessenger.ServiceConnected() {

		@Override
		public void onServiceConnected() {
			Log.v("font", "bind success");
		}
	};
	FontMessenger mFontMessenger;

	/**
	 * 判断是否联网
	 */
	public boolean IsHaveInternet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger.getActiveNetworkInfo();
			return (info != null && info.isConnected());
		} catch (Exception e) {
			return false;
		}
	}

	public TabFontFactory(Context context, DownModule module) {
		mContext = context;
		downModule = module;
		PathTool.makeDirApp();
		mFontMessenger = new FontMessenger(servicecon);
	}

	public void onDestroy() {
		if (packageReceiver != null)
			mContext.unregisterReceiver(packageReceiver);
		if (mAdapter != null) {
			mAdapter.onDestory();
		}
		if (hotAdapter != null) {
			hotAdapter.onDestory();
		}
	}

	@Override
	public View createTabContent(String tag) {
		long preTime = System.currentTimeMillis();
		View result = View.inflate(mContext, R.layout.theme_main, null);
		View containHot = result.findViewById(R.id.containHot);
		// 本地主题
		final RadioButton LocalButton = (RadioButton) result
				.findViewById(R.id.btnLocalTheme);
		LocalButton.setText(R.string.text_lcoal_Fonts);
		gridViewLocal = (GridView) (View.inflate(mContext, R.layout.lock_grid,
				null));
		if (!FunctionConfig.isdoovStyle()) {
			gridViewLocal.setNumColumns(2);
			gridViewLocal.setPadding(0, 0, 0, 0);
			gridViewLocal.setHorizontalSpacing(0);
			gridViewLocal.setVerticalSpacing(0);
			gridViewLocal.setColumnWidth(mContext.getResources()
					.getDisplayMetrics().widthPixels / 2);
		}
		mAdapter = new GridLocalFontAdapter(mContext);
		gridViewLocal.setAdapter(mAdapter);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getAssertFontsList();
			}
		}).start();
		gridViewLocal.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (position < fontsList.size()) {
					Intent intent = new Intent();
					intent.setClass(mContext, FontPreviewLocalActivity.class);
					intent.putExtra("font_type", Integer
							.parseInt((String) fontsList.get(position).get(
									"font_type")));
					intent.putExtra("font_name",
							(String) fontsList.get(position).get("font_name"));
					intent.putExtra("font_file",
							(String) fontsList.get(position).get("font_file"));
					mContext.startActivity(intent);
				} else {
					FontInformation infor = (FontInformation) parent
							.getItemAtPosition(position);
					Intent i = new Intent();
					i.putExtra(StaticClass.EXTRA_PACKAGE_NAME,
							infor.getPackageName());
					i.putExtra(StaticClass.EXTRA_CLASS_NAME,
							infor.getClassName());
					i.setClass(mContext, FontPreviewActivity.class);
					mContext.startActivity(i);
				}
			}
		});
		if (com.coco.theme.themebox.util.FunctionConfig.isShowHotFont()) {
			// 热门主题
			hotView = View.inflate(mContext, R.layout.lock_grid_hot, null);
			mPullToRefreshView = (PullToRefreshView) hotView
					.findViewById(R.id.main_pull_refresh_view);
			mPullToRefreshView.setOnHeaderRefreshListener(this);
			mPullToRefreshView.setOnFooterRefreshListener(this);
			gridViewHot = (GridView) hotView.findViewById(R.id.gridViewLock);
			if (!FunctionConfig.isdoovStyle()) {
				gridViewHot.setNumColumns(2);
				gridViewHot.setPadding(0, 0, 0, 0);
				gridViewHot.setHorizontalSpacing(0);
				gridViewHot.setVerticalSpacing(0);
				gridViewHot.setColumnWidth(mContext.getResources()
						.getDisplayMetrics().widthPixels / 2);
			}
			hotAdapter = new FontGridHotAdapter(mContext, downModule);
			// hotAdapter.queryPackage();
			gridViewHot.setAdapter(hotAdapter);
			// ViewPager
			gridPager = (ViewPager) result.findViewById(R.id.themeGridPager);
			gagerAdapter = new GridPagerAdapter(gridViewLocal, hotView);
			gagerAdapter.setGridView(gridViewHot);
			gridPager.setAdapter(gagerAdapter);
			if (hotAdapter.showProgress() || downModule.isRefreshList()) {
				downModule.downloadList();
				if (IsHaveInternet(mContext)) {
					interneterr = false;
					listRefresh = true;
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							downModule.stopDownlist();
							if (listRefresh) {
								if (com.coco.theme.themebox.util.FunctionConfig
										.isPromptVisible()) {
									if (gridPager.getCurrentItem() == INDEX_HOT)
										Toast.makeText(mContext,
												R.string.internet_unusual,
												Toast.LENGTH_SHORT).show();
								}
							}
							if (gagerAdapter != null
									&& gagerAdapter.viewDownloading != null) {
								gagerAdapter.viewDownloading
										.setVisibility(View.INVISIBLE);
							}
						}
					}, 1000 * 30);
				} else {
					if (gridPager.getCurrentItem() == INDEX_HOT)
						Toast.makeText(mContext, R.string.internet_err,
								Toast.LENGTH_SHORT).show();
					interneterr = true;
					if (gagerAdapter != null
							&& gagerAdapter.viewDownloading != null) {
						gagerAdapter.viewDownloading
								.setVisibility(View.INVISIBLE);
					}
				}
			}
			gridViewHot.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					FontInformation infor = (FontInformation) parent
							.getItemAtPosition(position);
					Intent i = new Intent();
					i.putExtra(StaticClass.EXTRA_PACKAGE_NAME,
							infor.getPackageName());
					i.putExtra(StaticClass.EXTRA_CLASS_NAME,
							infor.getClassName());
					i.setClass(mContext, FontPreviewActivity.class);
					// i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					mContext.startActivity(i);
				}
			});
			gridPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
			Log.v("time", "lockcreate = "
					+ (System.currentTimeMillis() - preTime) + "");
			// 热门按钮
			final RadioButton hotButton = (RadioButton) result
					.findViewById(R.id.btnHotTheme);
			final RadioButton localButton = (RadioButton) result
					.findViewById(R.id.btnLocalTheme);
			gridPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageSelected(int index) {
					if (index == INDEX_LOCAL) {
						localButton.toggle();
					} else if (index == INDEX_HOT) {
						if (com.coco.theme.themebox.StaticClass
								.isAllowDownload(mContext)) {
							// 无网�?
							if (IsHaveInternet(mContext) == false) {
								Toast.makeText(mContext, R.string.internet_err,
										Toast.LENGTH_SHORT).show();
								if (gagerAdapter != null
										&& gagerAdapter.viewDownloading != null) {
									gagerAdapter.viewDownloading
											.setVisibility(View.INVISIBLE);
								}
							} else {
								if (gagerAdapter != null
										&& gagerAdapter.viewDownloading != null) {
									gagerAdapter.viewDownloading
											.setVisibility(View.VISIBLE);
								}
								Log.v("Tab", "count = " + hotAdapter.getCount());
								if (hotAdapter.getCount() == 0) {
									gagerAdapter.notifyDataSetChanged();
									downModule.downloadList();
									if (IsHaveInternet(mContext)) {
										listRefresh = true;
										handler.postDelayed(new Runnable() {

											@Override
											public void run() {
												downModule.stopDownlist();
												if (listRefresh) {
													if (com.coco.theme.themebox.util.FunctionConfig
															.isPromptVisible()) {
														Toast.makeText(
																mContext,
																R.string.internet_unusual,
																Toast.LENGTH_SHORT)
																.show();
													}
												}
												if (gagerAdapter.viewDownloading != null) {
													gagerAdapter.viewDownloading
															.setVisibility(View.INVISIBLE);
												}
											}
										}, 1000 * 30);
									}
								}
							}
						} else {
							Toast.makeText(mContext,
									R.string.sdcard_not_available,
									Toast.LENGTH_SHORT).show();
						}
						hotButton.toggle();
					}
				}
			});
			// 热门
			hotButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					gridPager.setCurrentItem(INDEX_HOT, true);
				}
			});
			// 本地按钮
			localButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					gridPager.setCurrentItem(INDEX_LOCAL, true);
				}
			});
		} else {
			// 热门按钮
			if (FunctionConfig.isdoovStyle()) {
				containHot.setVisibility(View.GONE);
			} else {
				containHot.setVisibility(View.VISIBLE);
				final RadioButton themeHotButton = (RadioButton) result
						.findViewById(R.id.btnHotTheme);
				themeHotButton.setVisibility(View.GONE);
			}
			// ViewPager
			gridPager = (ViewPager) result.findViewById(R.id.themeGridPager);
			gagerAdapter = new GridPagerAdapter(gridViewLocal);
			gridPager.setAdapter(gagerAdapter);
			gridPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		packageReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String actionName = intent.getAction();
				if (actionName.equals(StaticClass.ACTION_THUMB_CHANGED)) {
					if (com.coco.theme.themebox.util.FunctionConfig
							.isShowHotFont()) {
						hotAdapter
								.updateThumb(intent
										.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME));
					}
				} else if (actionName
						.equals(StaticClass.ACTION_HOTLIST_CHANGED)) {
					Log.v("***************", "222222"
							+ StaticClass.ACTION_HOTLIST_CHANGED);
					if (com.coco.theme.themebox.util.FunctionConfig
							.isShowHotFont()) {
						hotAdapter.reloadPackage();
						hotAdapter.setShowProgress(false);
					}
					gagerAdapter.notifyDataSetChanged();
					if (listRefresh) {
						listRefresh = false;
					}
					if (footerRefresh) {
						mPullToRefreshView.onFooterRefreshComplete();
						footerRefresh = false;
					}
					if (headerRefresh) {
						mPullToRefreshView.onHeaderRefreshComplete();
						headerRefresh = false;
					}
				} else if (actionName
						.equals(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mAdapter.reloadPackage();
						}
					}).start();
					if (com.coco.theme.themebox.util.FunctionConfig
							.isShowHotFont()) {
						hotAdapter.reloadPackage();
					}
				} else if (actionName
						.equals(StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED)) {
					if (com.coco.theme.themebox.util.FunctionConfig
							.isShowHotFont()) {
						hotAdapter
								.updateDownloadSize(
										intent.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME),
										intent.getIntExtra(
												StaticClass.EXTRA_DOWNLOAD_SIZE,
												0),
										intent.getIntExtra(
												StaticClass.EXTRA_TOTAL_SIZE, 0));
					}
				} else if (actionName
						.equals(StaticClass.ACTION_START_DOWNLOAD_APK)) {
					String curdownApkname = intent
							.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME);
					// downModule.downloadApk(curdownApkname);
					Intent it = new Intent();
					it.putExtra("name", intent.getStringExtra("apkname"));
					it.putExtra("packageName", curdownApkname);
					it.putExtra("type", DownloadList.Font_Type);
					it.putExtra("status", "download");
					it.setClass(mContext, DownloadApkContentService.class);
					mContext.startService(it);
					if (com.coco.theme.themebox.util.FunctionConfig
							.isShowHotFont()) {
						hotAdapter.notifyDataSetChanged();
					}
				} else if (actionName
						.equals(StaticClass.ACTION_PAUSE_DOWNLOAD_APK)) {
					String packName = intent
							.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME);
					// downModule.stopDownApk(packName);
					Intent it = new Intent();
					it.putExtra("packageName", packName);
					it.putExtra("type", DownloadList.Font_Type);
					it.putExtra("name", intent.getStringExtra("apkname"));
					it.putExtra("status", "pause");
					it.setClass(mContext, DownloadApkContentService.class);
					mContext.startService(it);
					if (com.coco.theme.themebox.util.FunctionConfig
							.isShowHotFont()) {
						hotAdapter.notifyDataSetChanged();
					}
					Log.v("********", "receive packName = " + packName);
				}
			}
		};
		// 注册删除事件
		IntentFilter pkgFilter = new IntentFilter();
		pkgFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		pkgFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		pkgFilter.addDataScheme("package");
		mContext.registerReceiver(packageReceiver, pkgFilter);
		// 下载成功
		IntentFilter screenFilter1 = new IntentFilter();
		screenFilter1.addAction(StaticClass.ACTION_START_DOWNLOAD_APK);
		screenFilter1.addAction(StaticClass.ACTION_THUMB_CHANGED);
		screenFilter1.addAction(StaticClass.ACTION_HOTLIST_CHANGED);
		screenFilter1.addAction(StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED);
		screenFilter1.addAction(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED);
		screenFilter1.addAction(StaticClass.ACTION_PAUSE_DOWNLOAD_APK);
		screenFilter1.addAction(StaticClass.ACTION_DEFAULT_FONT_CHANGED);
		mContext.registerReceiver(packageReceiver, screenFilter1);
		return result;
	}

	private BroadcastReceiver packageReceiver = null;

	private void getAssertFontsList() {
		// Typeface face = Typeface.createFromAsset (mContext.getAssets() ,
		// "fonts/"+one );
		fontsList.clear();
		SAXParserFactory factoey = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factoey.newSAXParser();
			XMLReader xmlreader = parser.getXMLReader();
			FontConfigHandler handler = new FontConfigHandler();
			xmlreader.setContentHandler(handler);
			InputSource xmlin;
			InputStream in = mContext.getAssets().open("fonts/font_config.xml");
			if (in == null) {
				return;
			} else {
				xmlin = new InputSource(in);
			}
			xmlreader.parse(xmlin);
			handler = null;
			xmlin = null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// String fontspath = "/system/fonts";
		// File file = new File(fontspath);
	}

	class FontConfigHandler extends DefaultHandler {

		private final String TAG_ITEM = "item";

		@Override
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (localName.equals(TAG_ITEM)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("font_name", atts.getValue("font_name"));
				map.put("font_type", atts.getValue("font_type"));
				map.put("font_file", atts.getValue("font_file"));
				fontsList.add(map);
			}
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mAdapter.reloadFont();
				}
			}).start();
		}
	}

	/**
	 * ViewPager适配�?
	 */
	private static class GridPagerAdapter extends PagerAdapter {

		private final String LOG_TAG = "GridPagerAdapter";
		private GridView gridLocal;
		private GridView gridHot;
		private View hotView;
		private View viewDownloading = null;
		private FontGridHotAdapter hotAdapter;

		public GridPagerAdapter(GridView local, View view) {
			gridLocal = local;
			hotView = view;
		}

		public GridPagerAdapter(GridView local) {
			gridLocal = local;
		}

		public void setGridView(GridView hot) {
			gridHot = hot;
			hotAdapter = (FontGridHotAdapter) gridHot.getAdapter();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.d(LOG_TAG, "destroyItem,pos" + position);
			if (viewDownloading != null
					&& isViewFromObject(viewDownloading, object)) {
				container.removeView(viewDownloading);
				viewDownloading = null;
			}
		}

		@Override
		public int getItemPosition(Object object) {
			if (viewDownloading != null
					&& isViewFromObject(viewDownloading, object)
					&& !hotAdapter.showProgress()) {
				return PagerAdapter.POSITION_NONE;
			}
			return PagerAdapter.POSITION_UNCHANGED;
		}

		@Override
		public int getCount() {
			if (!com.coco.theme.themebox.util.FunctionConfig.isShowHotFont()) {
				return 1;
			}
			return 2;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.d(LOG_TAG, "instantiateItem,pos=" + position);
			if (position == 0) {
				container.addView(gridLocal);
				return gridLocal;
			}
			if (com.coco.theme.themebox.util.FunctionConfig.isShowHotFont()) {
				if (hotAdapter.showProgress()) {
					viewDownloading = View.inflate(container.getContext(),
							R.layout.grid_item_downloading, null);
					if (interneterr) {
						viewDownloading.setVisibility(View.GONE);
					} else {
						viewDownloading.setVisibility(View.VISIBLE);
					}
					container.addView(viewDownloading);
					return viewDownloading;
				}
				// ((ViewPager) container).addView(gridHot);
				((ViewPager) container).addView(hotView);
				return hotView;
			}
			return gridLocal;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		Log.v("PullToRefreshView", "tablock_onFooterRefresh");
		if (gridPager.getCurrentItem() == INDEX_HOT) {
			if (IsHaveInternet(mContext)) {
				footerRefresh = true;
				downModule.downloadList();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						downModule.stopDownlist();
						if (footerRefresh) {
							mPullToRefreshView.onFooterRefreshComplete();
							footerRefresh = false;
							if (com.coco.theme.themebox.util.FunctionConfig
									.isPromptVisible()) {
								Toast.makeText(mContext,
										R.string.internet_unusual,
										Toast.LENGTH_SHORT).show();
							}
						}
						if (headerRefresh) {
							mPullToRefreshView.onHeaderRefreshComplete();
							headerRefresh = false;
							if (com.coco.theme.themebox.util.FunctionConfig
									.isPromptVisible()) {
								Toast.makeText(mContext,
										R.string.internet_unusual,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				}, 1000 * 30);
			} else {
				Toast.makeText(mContext, R.string.internet_err,
						Toast.LENGTH_SHORT).show();
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		Log.v("PullToRefreshView", "tablock_onFooterRefresh");
		if (gridPager.getCurrentItem() == INDEX_HOT) {
			Log.v("onHeaderRefresh", "**************");
			if (IsHaveInternet(mContext)) {
				headerRefresh = true;
				downModule.downloadList();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Log.v("onHeaderRefresh", "Run footerRefresh = "
								+ footerRefresh + " headerRefresh = "
								+ headerRefresh);
						downModule.stopDownlist();
						if (footerRefresh) {
							mPullToRefreshView.onFooterRefreshComplete();
							footerRefresh = false;
							if (com.coco.theme.themebox.util.FunctionConfig
									.isPromptVisible()) {
								Toast.makeText(mContext,
										R.string.internet_unusual,
										Toast.LENGTH_SHORT).show();
							}
						}
						if (headerRefresh) {
							mPullToRefreshView.onHeaderRefreshComplete();
							headerRefresh = false;
							if (com.coco.theme.themebox.util.FunctionConfig
									.isPromptVisible()) {
								Toast.makeText(mContext,
										R.string.internet_unusual,
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				}, 1000 * 30);
			} else {
				Toast.makeText(mContext, R.string.internet_err,
						Toast.LENGTH_SHORT).show();
				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}
	}

	class GridLocalFontAdapter extends BaseAdapter {

		Context mContext;
		ContentResolver resolver;
		String currentFont;
		private List<Bitmap> images = new ArrayList<Bitmap>();
		private List<FontInformation> localList = new ArrayList<FontInformation>();
		private Bitmap imgDefaultThumb;

		public void onDestory() {
			for (Bitmap bmp : images) {
				bmp.recycle();
			}
			if (imgDefaultThumb != null && !imgDefaultThumb.isRecycled()) {
				imgDefaultThumb.recycle();
			}
		}

		private void loadImage() {
			for (Bitmap bmp : images) {
				if (bmp != null && !bmp.isRecycled()) {
					bmp.recycle();
				}
				bmp = null;
			}
			images.clear();
			try {
				for (int i = 0; i < fontsList.size(); i++) {
					InputStream in = mContext.getAssets().open(
							"fonts/" + "font_type_"
									+ fontsList.get(i).get("font_type")
									+ ".png");
					images.add(BitmapFactory.decodeStream(in));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public GridLocalFontAdapter(Context context) {
			mContext = context;
			resolver = mContext.getContentResolver();
			if (FunctionConfig.isEnable_topwise_style()) {
				currentFont = SystemProperties.get("persist.sys.font",
						"settings.default.font");
			} else {
				currentFont = Settings.System.getInt(resolver, "font_type", 1)
						+ "";
			}
			Log.v("currentFont", "currentFont = " + currentFont);
			imgDefaultThumb = ((BitmapDrawable) context.getResources()
					.getDrawable(R.drawable.default_img_large)).getBitmap();
		}

		private void queryPackage() {
			for (FontInformation info : localList) {
				info.disposeThumb();
				info = null;
			}
			localList.clear();
			FontService themeSv = new FontService(mContext);
			List<FontInformation> installList = themeSv.queryDownloadList();
			System.out.println("installList.size() = " + installList.size());
			for (FontInformation info : installList) {
				localList.add(info);
			}
			System.out.println("localList.size() = " + localList.size());
		}

		public void reloadPackage() {
			queryPackage();
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			System.out.println("localList.size() getcount = "
					+ localList.size());
			return images.size() + localList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if (position < images.size()) {
				return position;
			}
			return localList.get(position - images.size());
		}

		public void reloadFont() {
			if (FunctionConfig.isEnable_topwise_style()) {
				currentFont = SystemProperties.get("persist.sys.font",
						"settings.default.font");
			} else {
				currentFont = Settings.System.getInt(resolver, "font_type", 1)
						+ "";
			}
			Log.v("currentFont", "reload currentFont = " + currentFont);
			loadImage();
			queryPackage();
			((Activity) mContext).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {

			ImageView viewThumb;
			TextView viewName;
			ImageView imageCover;
			ImageView imageUsed;
			ProgressBar barPause;
			ProgressBar barDownloading;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			Log.v("test", "PageItemTask: getView position:" + position
					+ " convertView:" + convertView);
			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				viewHolder = new ViewHolder();
				if (FunctionConfig.isdoovStyle())
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.main_wallpaper_item, null);
				else
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.main_font_item, null);
				viewHolder.viewName = (TextView) convertView
						.findViewById(R.id.textAppName);
				viewHolder.viewThumb = (ImageView) convertView
						.findViewById(R.id.imageThumb);
				viewHolder.imageCover = (ImageView) convertView
						.findViewById(R.id.imageCover);
				viewHolder.imageUsed = (ImageView) convertView
						.findViewById(R.id.imageUsed);
				viewHolder.barPause = (ProgressBar) convertView
						.findViewById(R.id.barPause);
				viewHolder.barDownloading = (ProgressBar) convertView
						.findViewById(R.id.barDownloading);
			}
			// ImageView viewThumb = (ImageView)convertView.findViewById(
			// R.id.imageThumb );
			// TextView viewName = (TextView)convertView.findViewById(
			// R.id.textAppName );
			if (position < images.size()) {
				viewHolder.viewThumb.setImageBitmap(images.get(position));
				int font_type = -1;
				if (position < fontsList.size()) {
					font_type = Integer.parseInt((String) fontsList.get(
							position).get("font_type"));
				}
				if ((FunctionConfig.isEnable_topwise_style() && currentFont
						.equals((String) fontsList.get(position).get(
								"font_file")))
						|| (!FunctionConfig.isEnable_topwise_style() && currentFont
								.equals(font_type + ""))
						|| (currentFont.equals("settings.default.font") && position == 0)) {
					viewHolder.imageUsed.setVisibility(View.VISIBLE);
				} else {
					viewHolder.imageUsed.setVisibility(View.INVISIBLE);
				}
			} else {
				FontInformation info = (FontInformation) getItem(position);
				if (info.isNeedLoadDetail()) {
					info.loadDetail(mContext);
					if (info.getThumbImage() == null) {
						downModule.downloadThumb(info.getPackageName(),
								DownloadList.Font_Type);
					}
				}
				Bitmap imgThumb = info.getThumbImage();
				if (imgThumb == null) {
					imgThumb = imgDefaultThumb;
				}
				viewHolder.viewThumb.setImageBitmap(imgThumb);
				if (FunctionConfig.isEnable_topwise_style()
						&& currentFont.contains(info.getPackageName())) {
					viewHolder.imageUsed.setVisibility(View.VISIBLE);
				} else {
					viewHolder.imageUsed.setVisibility(View.INVISIBLE);
				}
			}
			viewHolder.viewName.setVisibility(View.GONE);
			viewHolder.barPause.setVisibility(View.GONE);
			viewHolder.barDownloading.setVisibility(View.GONE);
			viewHolder.imageCover.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
			return convertView;
		}
	}
}
