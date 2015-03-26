package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class DesktopEditWallpaperPage extends DesktopEditPage {

	private final LayoutInflater mInflater;
	private Context mContext;

	private int mCountX = 3;
	private int mCountY = 1;
	private int mPageCount = 3;
	private int[] mPackageWallpapers;

	private RelativeLayout mBtnHotWallpaper;
	private RelativeLayout mBtnPhotosWallpaper;

	public DesktopEditWallpaperPage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DesktopEditWallpaperPage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mContentIsRefreshable = false;
		initLocalWallpaper();
		setDataIsReady();

		syncPageCount();
		syncPages();
	}

	private void initLocalWallpaper() {
		mPackageWallpapers = new int[3];
		mPackageWallpapers[0] = R.drawable.wallpaper_01;
		mPackageWallpapers[1] = R.drawable.wallpaper_02;
		mPackageWallpapers[2] = R.drawable.wallpaper_03;
	}

	private void syncPageCount() {
		mPageCount = 1 + (1 + mPackageWallpapers.length) / 3;
	}

	@Override
	public void syncPages() {
		for (int i = 0; i < mPageCount; i++) {
			PagedViewLinearLayout layout = new PagedViewLinearLayout(mContext);
			syncPageItems(layout, i);
			addView(layout);
		}
	}

	@Override
	public void syncPageItems(int page, boolean immediate) {
		int numCells = mCountX * mCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min(startIndex + numCells, mCountX * mPageCount);
		PagedViewLinearLayout layout = (PagedViewLinearLayout) getChildAt(page);
		for (int i = startIndex; i < endIndex; i++) {
			if (i == 0) {
				LinearLayout ll = (LinearLayout) mInflater.inflate(
						R.layout.hot_and_default, null);

				layout.addView(ll);
			} else {
				RelativeLayout rl = (RelativeLayout) mInflater.inflate(
						R.layout.de_wallpaper_item, null);
				layout.addView(rl);
			}
		}
	}

	private void syncPageItems(PagedViewLinearLayout layout, int page) {
		int numCells = mCountX * mCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min(startIndex + numCells, mCountX * mPageCount);
		for (int i = startIndex; i < endIndex; i++) {
			if (i == 0) {
				LinearLayout ll = (LinearLayout) mInflater.inflate(
						R.layout.hot_and_default, null);
				mBtnHotWallpaper = (RelativeLayout) ll
						.findViewById(R.id.rl_hot);
				mBtnPhotosWallpaper = (RelativeLayout) ll
						.findViewById(R.id.rl_local);

				ImageView ivHot = (ImageView) ll.findViewById(R.id.iv_hot);
				ImageView ivLocal = (ImageView) ll.findViewById(R.id.iv_local);
				TextView tvHot = (TextView) ll.findViewById(R.id.tv_hot);
				TextView tvLocal = (TextView) ll.findViewById(R.id.tv_local);

				ivHot.setImageResource(R.drawable.de_hot_wallpaper);
				ivLocal.setImageResource(R.drawable.de_wallpaper_photos);
				tvHot.setText(getResources().getString(
						R.string.de_hot_wallpapers_text));
				tvLocal.setText(getResources().getString(
						R.string.de_photos_wallpapers_text));

				layout.addView(ll);
			} else {
				RelativeLayout rl = (RelativeLayout) mInflater.inflate(
						R.layout.de_wallpaper_item, null);
				switch (i) {
				case 1:
					rl.setBackgroundResource(mPackageWallpapers[0]);
					break;
				case 2:
					rl.setBackgroundResource(mPackageWallpapers[1]);
					break;
				case 3:
					rl.setBackgroundResource(mPackageWallpapers[2]);
					break;
				default:
					break;
				}
				layout.addView(rl);
			}
		}
	}
}
