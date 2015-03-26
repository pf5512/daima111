package com.cooee.launcher.desktopedit;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coco.theme.themebox.ThemeInformation;
import com.cooee.launcher.Launcher;
import com.cooeeui.brand.turbolauncher.R;

/**
 * 桌面编辑模块代码
 * 
 * @author LinYu
 * 
 */
public class DesktopEditThemePage extends DesktopEditPage {

	private static final String TAG = "DesktopEditThemePage";
	private final int mCountX = 3;
	private final int mCountY = 1;
	private int mPageCount = 1;
	private final LayoutInflater mInflater;
	private List<ThemeInformation> mLocalList;
	private Context mContext;

	public DesktopEditThemePage(Context context) {
		this(context, null);
	}

	public DesktopEditThemePage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DesktopEditThemePage(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mContentIsRefreshable = false;
		setDataIsReady();
		getLocalList();
		syncPageCount();
		syncPages();
	}

	private void getLocalList() {
		ThemeQuery themeQuery = new ThemeQuery(mContext);
		mLocalList = themeQuery.localList;
	}

	private void syncPageCount() {
		mPageCount = mLocalList.size() / 3 + 1;
		Log.i(TAG, "PageCount : " + mPageCount);
		Log.i(TAG, "mLocalList.size() : " + mLocalList.size());
	}

	@Override
	public void syncPages() {
		for (int i = 0; i < mPageCount; i++) {
			PagedViewLinearLayout layout = new PagedViewLinearLayout(mContext);
			syncPageItems(layout, i);
			addView(layout);
		}
	}

	private void syncPageItems(PagedViewLinearLayout layout, int page) {
		int numCells = mCountX * mCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min(startIndex + numCells, mLocalList.size() + 1);
		for (int i = startIndex; i < endIndex; ++i) {
			if (i == 0) {
				LinearLayout ll = (LinearLayout) mInflater.inflate(
						R.layout.hot_and_default, null);
				layout.addView(ll);
			} else {
				if (mLocalList.size() > 0) {
					RelativeLayout rl_theme = (RelativeLayout) mInflater
							.inflate(R.layout.de_theme_item, null);
					RelativeLayout rl_font = (RelativeLayout) rl_theme
							.findViewById(R.id.rl_theme_name);
					rl_font.setGravity(Gravity.BOTTOM);
					RelativeLayout rl_themebg = (RelativeLayout) rl_theme
							.findViewById(R.id.rl_themebackground);
					TextView tv_thememname = (TextView) rl_theme
							.findViewById(R.id.tv_themename);
					final ThemeInformation themeInfo = (ThemeInformation) mLocalList
							.get(i - 1);
					Bitmap imgThumb = themeInfo.getThumbImage();
					if (imgThumb == null) {
						imgThumb = ((BitmapDrawable) Launcher.getInstance()
								.getResources()
								.getDrawable(R.drawable.default_img))
								.getBitmap();
					}
					rl_themebg.setBackground(new BitmapDrawable(imgThumb));
					String title = "";
					String[] tiltles = themeInfo.getDisplayName().split("_");
					if (tiltles.length == 2) {
						if (!tiltles[0].contains("Turbo")
								&& tiltles[1].contains("Turbo")) {
							title = tiltles[0];
						} else if (!tiltles[1].contains("Turbo")
								&& tiltles[0].contains("Turbo")) {
							title = tiltles[1];
						} else {
							title = tiltles[1];
						}
					} else {
						title = themeInfo.getDisplayName();
					}
					tv_thememname.setText(title);
					ImageView iv = (ImageView) rl_theme
							.findViewById(R.id.iv_selected);
					iv.setVisibility(View.INVISIBLE);
					layout.addView(rl_theme);
				}
			}

		}
	}

	// class GridPool {
	// private Context mContext;
	// private ArrayList<GridView> mGrids;
	// private float mWidth;
	// private float mHeight;
	// private int mCountX;
	// private int mCountY;
	// private int mPaddingLeft;
	// private int mPaddingRight;
	// private int mPaddingTop;
	// private int mPaddingBottom;
	//
	// public GridPool(Context context, int initCapacity, float width,
	// float height, int countX, int countY, int paddingLeft,
	// int paddingRight, int paddingTop, int paddingBottom) {
	// mGrids = new ArrayList<GridView>(initCapacity);
	// mWidth = width;
	// mHeight = height;
	// mCountX = countX;
	// mCountY = countY;
	// mPaddingLeft = paddingLeft;
	// mPaddingRight = paddingRight;
	// mPaddingTop = paddingTop;
	// mPaddingBottom = paddingBottom;
	// }
	//
	// private GridView create() {
	// GridView grid = new GridView(mContext);
	// grid.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight,
	// mPaddingBottom);
	// grid.setNumColumns(mCountX);
	// return grid;
	// }
	//
	// public GridView get(int countX, int countY) {
	// mCountX = countX;
	// mCountY = countY;
	// GridView grid = mGrids.isEmpty() ? create() : mGrids.remove(mGrids
	// .size() - 1);
	// return grid;
	// }
	//
	// public void free(GridView grid) {
	//
	// }
	// }

	// class GridAdapter extends BaseAdapter {
	//
	// private Context mContext;
	// private LayoutInflater mInflater;
	//
	// public GridAdapter(Context context) {
	// mContext = context;
	// mInflater = LayoutInflater.from(context);
	// }
	//
	// @Override
	// public int getCount() {
	// return 3;
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return 0;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder holder;
	// if (convertView == null) {
	// convertView = mInflater.inflate(R.layout.hot_and_default, null);
	// holder = new ViewHolder();
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	// return convertView;
	// }
	//
	// }
	//
	// private class ViewHolder {
	//
	// }
}
