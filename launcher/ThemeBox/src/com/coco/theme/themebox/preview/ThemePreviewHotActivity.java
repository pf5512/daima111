package com.coco.theme.themebox.preview;

import java.io.File;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import com.coco.theme.themebox.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.coco.theme.themebox.ActivityManager;
import com.coco.theme.themebox.PageControl;
import com.coco.theme.themebox.PreViewGallery;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.ThemeInformation;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.database.model.DownloadThemeItem;
import com.coco.theme.themebox.database.service.DownloadThemeService;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.ContentConfig;
import com.coco.theme.themebox.util.PathTool;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.iLoong.base.themebox.R;
import com.umeng.analytics.MobclickAgent;

public class ThemePreviewHotActivity extends Activity {

	private final String LOG_TAG = "PreviewHotActivity";
	private ScrollView previewScroll;
	private ThemeDownModule downModule;
	private RelativeLayout relativeNormal;
	private RelativeLayout relativeDownload;
	private ThemeInformation themeInformation;
	private PageControl pageControl = null;
	private PreViewGallery galleryPreview;
	private String packageName;
	private String destClassName;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActivityManager.pushActivity(this);
		mContext = this;
		setContentView(R.layout.preview_hot);
		pageControl = (PageControl) findViewById(R.id.page_control);
		pageControl.setPageCount(3);
		pageControl.setCurrentPage(0);
		galleryPreview = (PreViewGallery) findViewById(R.id.galleryPreview);
		downModule = new ThemeDownModule(this);
		Intent intent = this.getIntent();
		packageName = intent.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME);
		destClassName = intent.getStringExtra(StaticClass.EXTRA_CLASS_NAME);
		if (destClassName == null || destClassName.equals("")) {
			destClassName = "";
		}

		loadThemeInformation(true);
		updateShowInfo();

		galleryPreview.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(LOG_TAG, "galleryPreview,position=" + position);

				pageControl.setCurrentPage(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.d(LOG_TAG, "galleryPreview,onNothingSelected");
				pageControl.setCurrentPage(0);
			}

		});

		galleryPreview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ThemeService service = new ThemeService(mContext);
				themeInformation = service.queryTheme(packageName, destClassName);

				boolean local =themeInformation.isInstalled();
				Intent i = new Intent();
				i.putExtra("position", position);
				i.putExtra("local", local);
				i.putExtra("packname", packageName);
				i.putExtra("classname",destClassName);
				i.setClass(mContext,ThemePreviewFullActivity.class);
				startActivity(i);
				overridePendingTransition(0, 0);
				
			}
		});

		previewScroll = (ScrollView) findViewById(R.id.previewScroll);
		relativeNormal = (RelativeLayout) findViewById(R.id.layoutNormal);
		relativeDownload = (RelativeLayout) findViewById(R.id.layoutDownload);
		reLayoutScroll();

		IntentFilter screenFilter = new IntentFilter();
		screenFilter.addAction(StaticClass.ACTION_PREVIEW_CHANGED);
		screenFilter.addAction(StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED);
		screenFilter.addAction(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED);
		registerReceiver(previewReceiver, screenFilter);

		// 注册删除事件
		IntentFilter pkgFilter = new IntentFilter();
		pkgFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		pkgFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		pkgFilter.addDataScheme("package");
		registerReceiver(previewReceiver, pkgFilter);

		updateShowStatus();

		// 监听返回按钮
		ImageButton btnReturn = (ImageButton) findViewById(R.id.btnReturn);
		btnReturn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		// 分享
		ImageButton btnShare = (ImageButton) findViewById(R.id.btnShare);
		if (!com.coco.theme.themebox.util.FunctionConfig.isShareVisible()) {
			btnShare.setVisibility(View.GONE);
		}
		btnShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendShare();
			}
		});

		// 更多
		Button btnMore = (Button) findViewById(R.id.btnMore);
		btnMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				smoothScrollMore();
			}
		});

		// 应用按钮
		Button btnApply = (Button) findViewById(R.id.btnApply);
		btnApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				
				//友盟  应用统计
				MobclickAgent.onEvent(mContext, "Apply", packageName);
				ThemeService sv = new ThemeService(ThemePreviewHotActivity.this);
				sv.applyTheme(new ComponentName(packageName, destClassName));
				Toast.makeText(
						ThemePreviewHotActivity.this,
						getString(R.string.toastPreviewApply,
								themeInformation.getDisplayName()),
						Toast.LENGTH_SHORT).show();
				sendBroadcast(new Intent(
						StaticClass.ACTION_DEFAULT_THEME_CHANGED));
				ActivityManager.KillActivity();
			}
		});

		// 安装
		Button btnInstall = (Button) findViewById(R.id.btnInstall);
		btnInstall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				downModule.installApk(packageName);
				
				//友盟  安装解锁
				MobclickAgent.onEvent(mContext, "Install", packageName);
			}
		});

		// 暂停
		relativeDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v(LOG_TAG, "relativeDownload click");

				if (relativeDownload.findViewById(R.id.linearDownload)
						.getVisibility() == View.VISIBLE) {
					Intent intent = new Intent(
							StaticClass.ACTION_PAUSE_DOWNLOAD_APK);
					intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, packageName);
					sendBroadcast(intent);
					switchToPause();
				} else {

					
					//友盟  继续下载统计
					MobclickAgent.onEvent(mContext, "ContinueDown",packageName);
					
					Intent intent = new Intent(
							StaticClass.ACTION_START_DOWNLOAD_APK);
					intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, packageName);
					sendBroadcast(intent);
					switchToDownloading();
				}

			}
		});

		// 下载按钮
		Button btnDown = (Button) findViewById(R.id.btnDownload);
		btnDown.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (com.coco.theme.themebox.StaticClass.canDownToInternal) {
					File f = new File(PathTool.getAppDir());
					int num = f.listFiles().length;
					if (num >= 5) {
						recursionDeleteFile(new File(PathTool.getDownloadingDir()));
						Toast.makeText(mContext, mContext.getString(R.string.memory_prompt),
								Toast.LENGTH_SHORT).show();
					}
				}else if (!StaticClass
						.isAllowDownloadWithToast(ThemePreviewHotActivity.this)) {
					return;
				}
				DownloadThemeService dSv = new DownloadThemeService(
						ThemePreviewHotActivity.this);
				DownloadThemeItem dItem = dSv.queryByPackageName(packageName);
				if (dItem == null) {

					//友盟  开始下载统计
					MobclickAgent.onEvent(mContext, "StartDown",packageName);
					
					dItem = new DownloadThemeItem();
					dItem.copyFromThemeInfo(themeInformation.getInfoItem());
					dItem.setDownloadStatus(DownloadStatus.StatusDownloading);
					dSv.insertItem(dItem);
				}
				loadThemeInformation(false);
				Intent intent = new Intent();
				intent.setAction(StaticClass.ACTION_START_DOWNLOAD_APK);
				intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, packageName);
				sendBroadcast(intent);
				Log.v("********", "pressDown");
				updateShowStatus();
			}
		});

		// 删除
		Button btnDelete = (Button) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.d(LOG_TAG, "btnDelete");
				
				//友盟  删除解锁
				MobclickAgent.onEvent(mContext, "Delete", packageName);
				
				Intent intent = new Intent(
						StaticClass.ACTION_PAUSE_DOWNLOAD_APK);
				intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, packageName);
				sendBroadcast(intent);

				DownloadThemeService dSv = new DownloadThemeService(
						ThemePreviewHotActivity.this);
				dSv.deleteItem(packageName);

				intent = new Intent(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED);
				intent.putExtra(StaticClass.EXTRA_PACKAGE_NAME, packageName);
				sendBroadcast(intent);

				updateShowStatus();
			}
		});

		// 卸载按钮
		Button btnUninstall = (Button) findViewById(R.id.btnUninstall);
		btnUninstall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				
				//友盟  删除解锁
				MobclickAgent.onEvent(mContext, "Uninstall", packageName);
				
				removePackage();
			}
		});
	}

	private void updateProgressSize() {
		if (findViewById(R.id.linearDownload).getVisibility() == View.VISIBLE) {
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarDown);
			progressBar.setProgress(themeInformation.getDownloadPercent());
			TextView text = (TextView) findViewById(R.id.textDownPercent);
			text.setText(getString(R.string.textDownloading,
					themeInformation.getDownloadPercent()));
		} else {
			ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarPause);
			progressBar.setProgress(themeInformation.getDownloadPercent());
			TextView text = (TextView) findViewById(R.id.textPausePercent);
			text.setText(getString(R.string.textPause,
					themeInformation.getDownloadPercent()));
		}
	}

	@Override
	protected void onDestroy() {
		ActivityManager.popupActivity(this);
		unregisterReceiver(previewReceiver);
		downModule.dispose();
		super.onDestroy();
	}

	private void updateShowInfo() {
		TextView text = (TextView) findViewById(R.id.textAppName);
		text.setText(themeInformation.getDisplayName());

		TextView viewAuthor = (TextView) findViewById(R.id.author);
		String author = getString(R.string.previewThemeSize,
				themeInformation.getApplicationSize() / 1024,
				themeInformation.getAuthor());
		viewAuthor.setText(author);

		TextView viewInfo = (TextView) findViewById(R.id.info);
		String info = getString(R.string.previewIntroduction) + "\n"
				+ themeInformation.getIntroduction();
		viewInfo.setText(info);
	}

	private void updateInforButton() {
		findViewById(R.id.btnDelete).setVisibility(View.GONE);
		findViewById(R.id.btnUninstall).setVisibility(View.GONE);

		if (themeInformation.isInstalled()) {
			if (themeInformation.isSystem()) {
				return;
			}
			ThemeService service = new ThemeService(this);
			ComponentName curTheme = service.queryCurrentTheme();
			if (themeInformation.isComponent(curTheme)) {
				return;
			}

			findViewById(R.id.btnUninstall).setVisibility(View.VISIBLE);
			return;
		}

		if (!themeInformation.isDownloaded()) {
			return;
		}

		if (themeInformation.getDownloadStatus() != DownloadStatus.StatusDownloading) {
			findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
		}
	}

	private void updateShowStatus() {
		updateInforButton();

		findViewById(R.id.btnSetting).setVisibility(View.GONE);
		findViewById(R.id.btnShare).setVisibility(View.GONE);

		relativeDownload.setClickable(false);
		if (themeInformation.isInstalled()) {
			relativeDownload.setVisibility(View.GONE);
			relativeNormal.setVisibility(View.VISIBLE);
			relativeNormal.findViewById(R.id.btnDownload).setVisibility(
					View.GONE);
			relativeNormal.findViewById(R.id.btnApply).setVisibility(
					View.VISIBLE);
			relativeNormal.findViewById(R.id.btnInstall).setVisibility(
					View.GONE);
			findViewById(R.id.btnShare).setVisibility(View.VISIBLE);
			return;
		}

		if (!themeInformation.isDownloaded()) {
			relativeDownload.setVisibility(View.GONE);
			relativeNormal.setVisibility(View.VISIBLE);
			relativeNormal.findViewById(R.id.btnDownload).setVisibility(
					View.VISIBLE);
			relativeNormal.findViewById(R.id.btnApply).setVisibility(View.GONE);
			relativeNormal.findViewById(R.id.btnInstall).setVisibility(
					View.GONE);
			return;
		}

		if (themeInformation.getDownloadStatus() == DownloadStatus.StatusFinish) {
			relativeDownload.setVisibility(View.GONE);
			relativeNormal.setVisibility(View.VISIBLE);
			relativeNormal.findViewById(R.id.btnDownload).setVisibility(
					View.GONE);
			relativeNormal.findViewById(R.id.btnApply).setVisibility(View.GONE);
			relativeNormal.findViewById(R.id.btnInstall).setVisibility(
					View.VISIBLE);
			return;
		}

		relativeDownload.setClickable(true);
		if (themeInformation.getDownloadStatus() == DownloadStatus.StatusDownloading) {
			switchToDownloading();
		} else {
			switchToPause();
		}
	}

	private void switchToDownloading() {
		relativeDownload.setVisibility(View.VISIBLE);
		relativeNormal.setVisibility(View.GONE);
		relativeDownload.findViewById(R.id.linearDownload).setVisibility(
				View.VISIBLE);
		relativeDownload.findViewById(R.id.linearPause)
				.setVisibility(View.GONE);

		updateProgressSize();
	}

	private void switchToPause() {
		relativeDownload.setVisibility(View.VISIBLE);
		relativeNormal.setVisibility(View.GONE);
		relativeDownload.findViewById(R.id.linearDownload).setVisibility(
				View.GONE);
		relativeDownload.findViewById(R.id.linearPause).setVisibility(
				View.VISIBLE);

		updateProgressSize();
	}

	private void loadThemeInformation(boolean reloadGallery) {

		ThemeService service = new ThemeService(this);
		themeInformation = service.queryTheme(packageName, destClassName);

		if (themeInformation.isInstalled()) {
			Context dstContext = null;
			try {
				dstContext = createPackageContext(packageName,
						Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				return;
			}
			Log.v(LOG_TAG, "2222222222222222destClassName = " + destClassName);
			ContentConfig destContent = new ContentConfig();
			destContent.loadConfig(dstContext, destClassName);
			themeInformation.loadInstallDetail(dstContext, destContent);
			if (reloadGallery) {
				galleryPreview.setAdapter(new ThemePreviewLocalAdapter(this,
						destContent, dstContext, false));
			}
			return;
		}
		if (reloadGallery) {
			galleryPreview.setAdapter(new ThemePreviewHotAdapter(this,
					packageName, downModule, false));
		}
	}

	private String queryClassName(String pkgName) {
		ThemeService service = new ThemeService(this);
		ComponentName comName = service.queryComponent(pkgName);
		if (comName == null) {
			return "";
		}

		return comName.getClassName();
	}

	private BroadcastReceiver previewReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String actionName = intent.getAction();
			Log.d(LOG_TAG, "action=" + actionName);
			if (actionName.equals(StaticClass.ACTION_PREVIEW_CHANGED)) {
				SpinnerAdapter apt = galleryPreview.getAdapter();
				if (apt != null && apt instanceof ThemePreviewHotAdapter) {
					((ThemePreviewHotAdapter) apt).reload();
					pageControl.setCurrentPage(galleryPreview
							.getSelectedItemPosition());
				}
			} else if (actionName
					.equals(StaticClass.ACTION_DOWNLOAD_STATUS_CHANGED)) {
				String name = intent
						.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME);
				if (name.equals(packageName)) {
					loadThemeInformation(false);
					updateShowStatus();
				}
			} else if (actionName
					.equals(StaticClass.ACTION_DOWNLOAD_SIZE_CHANGED)) {

				if (packageName.equals(intent
						.getStringExtra(StaticClass.EXTRA_PACKAGE_NAME))) {
					themeInformation.setDownloadSize(intent.getIntExtra(
							StaticClass.EXTRA_DOWNLOAD_SIZE, 0));
					themeInformation.setTotalSize(intent.getIntExtra(
							StaticClass.EXTRA_TOTAL_SIZE, 0));
					updateProgressSize();
				}
			} else if (Intent.ACTION_PACKAGE_REMOVED.equals(actionName)) {
				String actionPkgName = intent.getData().getSchemeSpecificPart();
				if (actionPkgName.equals(packageName)) {
					finish();
				}
			} else if (Intent.ACTION_PACKAGE_ADDED.equals(actionName)) {
				String actionPkgName = intent.getData().getSchemeSpecificPart();
				if (actionPkgName.equals(packageName)) {
					destClassName = queryClassName(packageName);
					loadThemeInformation(true);
					updateShowInfo();
					updateShowStatus();
				}
			}
		}
	};

	private boolean drawScroll = true;

	private void reLayoutScroll() {
		previewScroll.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						final int pictureHeight = findViewById(
								R.id.preview_picture).getLayoutParams().height;
						Log.d("PreviewHotActivity",
								"reLayoutScroll,pictureH=" + pictureHeight
										+ ",scrollH="
										+ previewScroll.getHeight());
						findViewById(R.id.preview_picture).getLayoutParams().height = previewScroll
								.getHeight();
						if (pictureHeight == previewScroll.getHeight()) {
							drawScroll = true;
							previewScroll.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						} else {
							drawScroll = false;
						}
					}
				});

		previewScroll.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						if (drawScroll) {
							previewScroll.getViewTreeObserver()
									.removeOnPreDrawListener(this);
						}
						return drawScroll;
					}
				});
	}

	private void smoothScrollMore() {
		if (previewScroll.getScrollY() != 0) {
			previewScroll.smoothScrollTo(0, 0);
		} else {
			previewScroll.smoothScrollTo(0, previewScroll.getMaxScrollAmount());
		}
	}

	private void sendShare() {
		//友盟 分享统计
		MobclickAgent.onEvent(mContext, "Share",themeInformation.getPackageName());
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");// "text/plain"
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareSubject));
		intent.putExtra(
				Intent.EXTRA_TEXT,
				getString(R.string.shareText, themeInformation.getDisplayName()));
		if (saveShare()) {
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(PathTool.getThumbTempFile())));
		}
		
		intent.putExtra("sms_body", getString(R.string.shareText, themeInformation.getDisplayName()));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent,
				getString(R.string.shareSubject)));
	}

	private boolean saveShare() {

		String thumbPath = PathTool.getThumbTempFile();
		if (thumbPath.equals("")) {
			return false;
		}

		Context dstContext = null;
		try {
			dstContext = createPackageContext(packageName,
					Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		ContentConfig destContent = new ContentConfig();
		destContent.loadConfig(dstContext, destClassName);

		return destContent.saveShare(dstContext, thumbPath);
	}

	private void removePackage() {
		String delApkPackname = "package:" + packageName;
		Uri packageURI = Uri.parse(delApkPackname);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent);
	}
	
	private void recursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null) {
				return;
			}
			for (File f : childFile) {
				recursionDeleteFile(f);
			}
			file.delete();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//友盟统计
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计
		MobclickAgent.onResume(this);
	}
}
