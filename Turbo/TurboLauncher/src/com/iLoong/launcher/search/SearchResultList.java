package com.iLoong.launcher.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.Desktop3DListener;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.ListView3D;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;

public class SearchResultList extends ListView3D {

	private float mScale = UtilsBase.getScreenWidth() / 720f;
	private List<SearchApp> mResultList;
	private int mRowCount;
	private int pageCapacity = 4;
	private int mCellCountX = 4;
	private int mCellCountY = 1;
	private GridPool gridPool = null;
	private ArrayList<View3D> buttonItems;
	private List<SearchApp> allApps;

	public SearchResultList(String name) {
		super(name);
		setSize(QsearchConstants.W_SEARCH_RESULT_LIST,
				QsearchConstants.H_SEARCH_RESULT_LIST);
	}

	public void load() {
		if (allApps == null) {
			allApps = QSearchGroup.getAllApp();
		}
	}

	public void reLoad() {
		if (allApps != null) {
			allApps.clear();
		}
		allApps = QSearchGroup.getAllApp();
	}

	@SuppressWarnings("unchecked")
	public void setButtons(ArrayList<? extends View3D> items, int countX,
			int countY, float width, float height) {
		mCellCountX = countX;
		mCellCountY = countY;
		buttonItems = (ArrayList<View3D>) items;
		gridPool = new GridPool(name, pageCapacity, width, height, countX,
				countY, R3D.qs_category_grid_padding_left,
				R3D.qs_category_grid_padding_right,
				R3D.qs_category_grid_padding_top,
				R3D.qs_category_grid_padding_bottom);
		syncButtonRows();
	}

	public synchronized void syncButtonRows() {
		int childCount = getChildCount();
		if (childCount > 0) {
			for (int i = 0; i < childCount; i++) {
				gridPool.free((GridView3D) getChildAt(i));
				removeItem(getChildAt(i));
			}
		}
		syncAppRowCount();
		for (int j = 0; j < mRowCount; j++) {
			GridView3D grid = gridPool.get(mCellCountX, mCellCountY);
			grid.enableAnimation(false);
			grid.transform = true;
			addItem(grid);
			syncButtonRowItems(j);
			grid.setAutoDrag(false);
		}
	}

	private void syncAppRowCount() {
		int numCells = mCellCountX * mCellCountY;
		mRowCount = (getItemCount() + numCells - 1) / numCells;
	}

	private void syncButtonRowItems(int row) {
		int numCells = mCellCountX * mCellCountY;
		int startIndex = row * numCells;
		int endIndex = Math.min(startIndex + numCells, getItemCount());
		GridView3D layout = (GridView3D) getChildAt(row);
		layout.removeAllViews();
		for (int i = startIndex; i < endIndex; i++) {
			View3D view = getIconItem(i);
			view.setScale(0.8f, 0.8f);
			layout.addItem(view);
		}
	}

	private int getItemCount() {
		return buttonItems.size();
	}

	private View3D getIconItem(int position) {
		return (View3D) (buttonItems.get(position));
	}

	class SearchResultItem extends ViewGroup3D {

		private View3D mDivider;

		public SearchResultItem(String name) {
			super(name);
			this.width = UtilsBase.getScreenWidth();
			this.height = R3D.qs_app_list_row_height;
			try {
				Bitmap dividerBmp = Tools.getImageFromInStream(iLoongLauncher
						.getInstance().getAssets()
						.open("theme/quick_search/qs_quess_line.9.png"));
				NinePatch dividerNp = new NinePatch(new BitmapTexture(
						dividerBmp, true), 1, 1, 1, 1);
				mDivider = new View3D("divider");
				mDivider.setSize(UtilsBase.getScreenWidth(), 1 * mScale);
				mDivider.setBackgroud(dividerNp);
				mDivider.setPosition(0, 0);
				addView(mDivider);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void query(String key) {
		removeAllViews();
		if (mResultList != null) {
			mResultList.clear();
		}
		if (TextUtils.isEmpty(key)) {
			return;
		}
		for (int i = 0; i < allApps.size(); i++) {
			String name_pinyin = allApps.get(i).initial_pinyin;
			String name = allApps.get(i).name;
			if (matching(name_pinyin, key) || name.contains(key)) {
				if (mResultList == null) {
					mResultList = new ArrayList<SearchApp>();
				}
				mResultList.add(allApps.get(i));
			}
		}
		if (mResultList != null && mResultList.size() > 0) {
			Desktop3DListener.root.qSearchGroup.mSearchResultGroup.mNoResultTip
					.hide();
			setButtons((ArrayList<? extends View3D>) mResultList, 4, 1,
					UtilsBase.getScreenWidth(), R3D.qs_app_list_row_height);
		} else {
			Desktop3DListener.root.qSearchGroup.mSearchResultGroup.mNoResultTip
					.show();
		}
	}

	public boolean matching(String name, String key) {
		int nameLength = name.length();
		int keyLength = key.length();
		List<String> debris = new ArrayList<String>();
		for (int i = 0; i < (nameLength - keyLength + 1); i++) {
			debris.add(name.substring(0 + i, keyLength + i));
		}
		for (int j = 0; j < debris.size(); j++) {
			if (debris.get(j).equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

	class GridPool {

		private ArrayList<GridView3D> grids;
		private float width;
		private float height;
		private int countX;
		private int countY;
		private String name;
		private int paddingLeft;
		private int paddingRight;
		private int paddingTop;
		private int paddingBottom;

		public GridPool(String name, int initCapacity, float width,
				float height, int countX, int countY, int paddingLeft,
				int paddingRight, int paddingTop, int paddingBottom) {
			this.name = name;
			grids = new ArrayList<GridView3D>(initCapacity);
			this.width = width;
			this.height = height;
			this.countX = countX;
			this.countY = countY;
			this.paddingLeft = paddingLeft;
			this.paddingRight = paddingRight;
			this.paddingTop = paddingTop;
			this.paddingBottom = paddingBottom;
		}

		private GridView3D create() {
			GridView3D grid = new GridView3D(name, width, height, countX,
					countY);
			grid.setPadding(paddingLeft, paddingRight, paddingTop,
					paddingBottom);
			return grid;
		}

		public GridView3D get(int countX, int countY) {
			this.countX = countX;
			this.countY = countY;
			GridView3D grid = grids.isEmpty() ? create() : grids.remove(grids
					.size() - 1);
			return grid;
		}

		public void free(GridView3D grid) {
			if (!grids.contains(grid)) {
				grid.removeAllViews();
				grids.add(grid);
			}
		}
	}
}
