package com.coco.theme.themebox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.coco.theme.themebox.StaticClass;
import com.coco.theme.themebox.database.model.DownloadStatus;
import com.coco.theme.themebox.service.ThemeService;
import com.coco.theme.themebox.util.ThemeDownModule;
import com.iLoong.base.themebox.R;
import android.graphics.drawable.BitmapDrawable;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ThemeGridLocalAdapter extends BaseAdapter {
	private List<ThemeInformation> localList = new ArrayList<ThemeInformation>();
	private Context context;
	private Bitmap imgDefaultThumb;
	private ThemeDownModule downThumb;
	private Set<String> packageNameSet = new HashSet<String>();
	private ComponentName currentTheme = new ComponentName("", "");
	private Handler mMainHandler = new Handler();
	
	public ThemeGridLocalAdapter(Context cxt, ThemeDownModule down) {
		context = cxt;
		downThumb = down;
		imgDefaultThumb = ((BitmapDrawable) cxt.getResources().getDrawable(
				R.drawable.default_img)).getBitmap();
		if (com.coco.theme.themebox.util.FunctionConfig.isLoadVisible()) {
			mMainHandler.postDelayed(new Runnable(){
				@Override
				public void run() {
					queryPackage();
					notifyDataSetChanged();
				}
			}, 200);
		}
	}

	private void queryPackage() {
		packageNameSet.clear();
		localList.clear();
		
		ThemeService themeSv = new ThemeService(context);
		List<ThemeInformation> installList = themeSv.queryInstallList();
		
		for(ThemeInformation info:installList) {
			info.setThumbImage(context, info.getPackageName(),info.getClassName());
			localList.add(info);
			packageNameSet.add(info.getPackageName());
		}
		currentTheme = themeSv.queryCurrentTheme();
	}

	public void reloadPackage() {
		queryPackage();
		((Activity)context).runOnUiThread(new Runnable(){			
			@Override
			public void run() {
				notifyDataSetChanged();
			}
		});		
	}

	public void updateDownloadSize(String pkgName, long downSize, long totalSize) {
		int findIndex = findPackageIndex(pkgName);
		if (findIndex < 0) {
			return;
		}
		ThemeInformation info = localList.get(findIndex);
		info.setDownloadSize(downSize);
		info.setTotalSize(totalSize);
		notifyDataSetChanged();
	}

	public Set<String> getPackageNameSet() {
		return packageNameSet;
	}

	public boolean containPackage(String packageName) {
		return findPackageIndex(packageName) >= 0;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		View retView = convertView;
		if (retView == null) {
			retView = View.inflate(context, R.layout.theme_grid_item, null);
		}
		ThemeInformation themeInfo = (ThemeInformation) getItem(position);
		if (themeInfo.isNeedLoadDetail()) {
			Bitmap imgThumb = themeInfo.getThumbImage();
			if (imgThumb == null) {
				themeInfo.loadDetail(context);
				if (themeInfo.getThumbImage() != null) {
					StaticClass.saveMyBitmap(context,
							themeInfo.getPackageName(), themeInfo.getClassName(),
							themeInfo.getThumbImage());
				}
			}
			if (themeInfo.getThumbImage() == null) {
				downThumb.downloadThumb(themeInfo.getPackageName());
			}
		}

		Bitmap imgThumb = themeInfo.getThumbImage();
		if (imgThumb == null) {
			imgThumb = imgDefaultThumb;
		}
		ImageView viewThumb = (ImageView) retView.findViewById(R.id.imageThumb);
		viewThumb.setImageBitmap(imgThumb);
		TextView viewName = (TextView) retView.findViewById(R.id.textAppName);
		String showName = themeInfo.getDisplayName();
		if(themeInfo.getDisplayName().length()>10){
			showName = themeInfo.getDisplayName().substring(0, 8)+"...";
		}
		
		viewName.setText(showName);

		if (currentTheme.getPackageName().equals(themeInfo.getPackageName())
				&& currentTheme.getClassName().equals(themeInfo.getClassName())) {
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
		if (themeInfo.isInstalled()
				|| themeInfo.getDownloadStatus() == DownloadStatus.StatusFinish) {
			barPause.setVisibility(View.INVISIBLE);
			barDownloading.setVisibility(View.INVISIBLE);
		} else {
			retView.findViewById(R.id.imageCover).setVisibility(View.VISIBLE);
			if (themeInfo.getDownloadStatus() == DownloadStatus.StatusDownloading) {
				barDownloading.setVisibility(View.VISIBLE);
				barPause.setVisibility(View.INVISIBLE);
				barDownloading.setProgress(themeInfo.getDownloadPercent());
			} else {
				barDownloading.setVisibility(View.INVISIBLE);
				barPause.setVisibility(View.VISIBLE);
				barPause.setProgress(themeInfo.getDownloadPercent());
			}
		}
		return retView;
	}

}
