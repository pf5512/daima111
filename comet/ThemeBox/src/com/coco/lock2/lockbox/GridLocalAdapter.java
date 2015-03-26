package com.coco.lock2.lockbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coco.lock2.lockbox.database.model.DownloadStatus;
import com.coco.lock2.lockbox.util.DownModule;
import com.coco.lock2.lockbox.util.LockManager;
import com.iLoong.base.themebox.R;

import android.app.Activity;

import android.graphics.drawable.BitmapDrawable;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import com.coco.theme.themebox.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GridLocalAdapter extends BaseAdapter {
	private List<LockInformation> localList = new ArrayList<LockInformation>();
	Map<String, LockInformation> allMap = new HashMap<String, LockInformation>();
	private Context context;
	private Bitmap imgDefaultThumb;
	private ComponentName currentLock;
	private DownModule downThumb;
	private Set<String> packageNameSet = new HashSet<String>();

	public GridLocalAdapter(Context cxt, DownModule down) {
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ((BitmapDrawable) cxt.getResources().getDrawable(
				R.drawable.default_img)).getBitmap();
		new Thread() {
			@Override
			public void run() {
				long preTime = System.currentTimeMillis();
				freshUpdate();// new图标更新
				queryPackage();
				Log.v("time", "searchlock = "
						+ (System.currentTimeMillis() - preTime) + "");
			}
		}.start();
	}

	private synchronized void queryPackage() {
		packageNameSet.clear();
		localList.clear();

		LockManager mgr = new LockManager(context);

		long preTime = System.currentTimeMillis();
		currentLock = mgr.queryCurrentLock();
		Log.v("time", "getCurrentlock = "
				+ (System.currentTimeMillis() - preTime) + "");
		preTime = System.currentTimeMillis();
		List<LockInformation> installList = mgr.queryInstallList();
		Log.v("time", "getallInstalledLock = "
				+ (System.currentTimeMillis() - preTime) + "");
		preTime = System.currentTimeMillis();
		for (LockInformation infor : installList) {
			infor.setThumbImage(context, infor.getPackageName(),
					infor.getClassName());
			localList.add(infor);
			packageNameSet.add(infor.getPackageName());
		}
		List<LockInformation> downFinishList = mgr.queryDownFinishList();
		for (LockInformation infor : downFinishList) {
			if (!installList.contains(infor)) {
				localList.add(infor);
				packageNameSet.add(infor.getPackageName());
			}
		}
		Log.v("time", "for..set = " + (System.currentTimeMillis() - preTime)
				+ "");
	}

	public void freshUpdate() {
		LockManager mgr = new LockManager(context);
		List<LockInformation> hotList = mgr.queryHotList();
		for (LockInformation itm : hotList) {
			allMap.put(itm.getPackageName(), itm);
		}
		// notifyDataSetChanged();
	}

	public void reloadPackage() {
		freshUpdate();
		queryPackage();
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void updateThumb(String pkgName) {
		int findIndex = findPackageIndex(pkgName);
		if (findIndex < 0) {
			return;
		}
		LockInformation info = localList.get(findIndex);
		info.reloadThumb();
		notifyDataSetChanged();
	}

	public void updateDownloadSize(String pkgName, long downSize, long totalSize) {
		int findIndex = findPackageIndex(pkgName);
		if (findIndex < 0) {
			return;
		}
		LockInformation info = localList.get(findIndex);
		info.setDownloadSize(downSize);
		info.setTotalSize(totalSize);
		notifyDataSetChanged();
	}

	public Set<String> getPackageNameSet() {
		return packageNameSet;
	}

	private int findPackageIndex(String packageName) {
		int i = 0;
		for (i = 0; i < localList.size(); i++) {
			if (packageName.equals(localList.get(i).getPackageName())) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public int getCount() {
		return localList.size();
	}

	@Override
	public Object getItem(int position) {
		return localList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View retView = convertView;
		if (retView == null) {
			retView = View.inflate(context, R.layout.grid_item, null);
		}
		LockInformation lockInfo = (LockInformation) getItem(position);
		if (lockInfo.isNeedLoadDetail()) {
			Bitmap imgThumb = lockInfo.getThumbImage();
			if (imgThumb == null) {
				lockInfo.loadDetail(context);
				if (lockInfo.getThumbImage() != null) {
					StaticClass.saveMyBitmap(context,
							lockInfo.getPackageName(), lockInfo.getClassName(),
							lockInfo.getThumbImage());
				}
			}
			if (lockInfo.getThumbImage() == null) {
				downThumb.downloadThumb(lockInfo.getPackageName());
			}
		}

		Bitmap imgThumb = lockInfo.getThumbImage();
		if (imgThumb == null) {
			imgThumb = imgDefaultThumb;
		}
		ImageButton viewThumb = (ImageButton) retView
				.findViewById(R.id.imageThumb);
		viewThumb.setImageBitmap(imgThumb);
		viewThumb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LockInformation lockInfo = (LockInformation) getItem(position);

				Intent i = new Intent();
				i.putExtra(StaticClass.EXTRA_PACKAGE_NAME,
						lockInfo.getPackageName());
				i.putExtra(StaticClass.EXTRA_CLASS_NAME,
						lockInfo.getClassName());
				i.setClassName(StaticClass.LOCKBOX_PACKAGE_NAME,
						StaticClass.LOCKBOX_PREVIEW_ACTIVITY);
				context.startActivity(i);
			}
		});
		ImageView update = (ImageView) retView
				.findViewById(R.id.imageNeedUpdate);
		// 安装的解锁才有判断有没有更新
		if (lockInfo.isInstalled()) {
			if (!allMap.isEmpty()) {
				Log.v("Local", "allMap.size = " + allMap.size());
				LockInformation item = allMap.get(lockInfo.getPackageName());
				if (item != null) {
					Log.v("Local", "name  = " + item.getDisplayName()+"hotver = "+item.getVersionCode()+" ownver = "+lockInfo.getVersionCode());
					if (item.getVersionCode() > lockInfo.getVersionCode()) {
						update.setVisibility(View.VISIBLE);
					} else {
						update.setVisibility(View.INVISIBLE);
					}
				}
			}
		}

		ImageView buttonDown = (ImageView) retView.findViewById(R.id.downicon);
		buttonDown.setVisibility(View.INVISIBLE);
		TextView viewName = (TextView) retView.findViewById(R.id.textAppName);
		String displayName = lockInfo.getDisplayName();
		String showName = displayName;
		if (displayName.length() > 10) {
			showName = displayName.substring(0, 8) + "...";
		}
		viewName.setText(showName);
		if (lockInfo.isComponent(currentLock)) {
			retView.findViewById(R.id.imageCover).setVisibility(View.VISIBLE);
			retView.findViewById(R.id.imageUsed).setVisibility(View.VISIBLE);
		} else {
			retView.findViewById(R.id.imageCover).setVisibility(View.INVISIBLE);
			retView.findViewById(R.id.imageUsed).setVisibility(View.INVISIBLE);
		}
		ProgressBar barPause = (ProgressBar) retView
				.findViewById(R.id.barPause);
		ProgressBar barDownloading = (ProgressBar) retView
				.findViewById(R.id.barDownloading);
		if (lockInfo.isInstalled()
				|| lockInfo.getDownloadStatus() == DownloadStatus.StatusFinish) {
			barPause.setVisibility(View.INVISIBLE);
			barDownloading.setVisibility(View.INVISIBLE);
		} else {
			retView.findViewById(R.id.imageCover).setVisibility(View.VISIBLE);
			if (lockInfo.getDownloadStatus() == DownloadStatus.StatusDownloading) {
				barDownloading.setVisibility(View.VISIBLE);
				barPause.setVisibility(View.INVISIBLE);
				barDownloading.setProgress(lockInfo.getDownloadPercent());
			} else {
				barDownloading.setVisibility(View.INVISIBLE);
				barPause.setVisibility(View.VISIBLE);
				barPause.setProgress(lockInfo.getDownloadPercent());
			}
		}
		return retView;
	}

}
