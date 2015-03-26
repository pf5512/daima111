package com.iLoong.launcher.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.iLoong.launcher.Desktop3D.AppHost3D;
import com.iLoong.launcher.Desktop3D.GridView3D;
import com.iLoong.launcher.Desktop3D.NPageBase;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.UtilsBase;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.data.ItemInfo;
import com.iLoong.launcher.desktop.iLoongApplication;

public class GuessPage extends NPageBase {

	private int pageCount;
	private int pageCapacity = 4;
	private int mCellCountX = 5;
	private int mCellCountY = 1;
	private GridPool gridPool = null;
	private ArrayList<View3D> buttonItems;
	public static List<SearchApp> allApps;

	public GuessPage(String name) {
		super(name);
		setSize(QsearchConstants.W_GUESS_PAGE, QsearchConstants.H_GUESS_PAGE);
		setEffectType(APageEase.COOLTOUCH_EFFECT_DEFAULT);
		indicatorView = new IndicatorView("npage_indicator",
				IndicatorView.INDICATOR_STYLE_QUICKSEARCH);
		indicatorView.setPosition(0, -QsearchConstants.H_GUESS_GROUP);
	}

	private void resetView() {
		for (int i = 0; i < this.getChildCount(); i++) {
			GridView3D gridView = (GridView3D) view_list.get(i);
			for (int j = 0; j < gridView.getChildCount(); j++) {
				View3D view = gridView.getChildAt(j);
				view.setScale(0.8f, 0.8f);
			}
		}
	}

	@Override
	public void initView() {
		super.initView();
		resetView();
	}

	public void load() {
		if (allApps == null) {
			allApps = QSearchGroup.getAllApp();
		}
		setButtons((ArrayList<? extends View3D>) getGuessList(), 5, 1,
				Utils3D.getScreenWidth(),
				190 * UtilsBase.getScreenWidth() / 720f);
	}

	public void reLoad() {
		if (allApps != null) {
			allApps.clear();
		}
		allApps = QSearchGroup.getAllApp();
		setButtons((ArrayList<? extends View3D>) getGuessList(), 5, 1,
				Utils3D.getScreenWidth(),
				190 * UtilsBase.getScreenWidth() / 720f);
	}

	@SuppressWarnings("unchecked")
	public void setButtons(ArrayList<? extends View3D> items, int countX,
			int countY, float width, float height) {
		mCellCountX = countX;
		mCellCountY = countY;
		this.buttonItems = (ArrayList<View3D>) items;
		this.width = width;
		this.height = height;
		if (gridPool == null) {
			gridPool = new GridPool(name, pageCapacity, width, height, countX,
					countY, 0, 0, 0, 0);
		}
		syncButtonPages();
	}

	public synchronized void syncButtonPages() {
		Iterator<View3D> ite = view_list.iterator();
		while (ite.hasNext()) {
			GridView3D grid = (GridView3D) ite.next();
			gridPool.free(grid);
			this.removeView(grid);
			ite.remove();
		}
		syncAppPageCount();
		for (int i = 0; i < pageCount; i++) {
			GridView3D grid = gridPool.get(mCellCountX, mCellCountY);
			grid.enableAnimation(false);
			grid.transform = true;
			addPage(i, grid);
			syncButtonPageItems(i, true);
			grid.setAutoDrag(false);
		}
	}

	private void syncAppPageCount() {
		int numCells = mCellCountX * mCellCountY;
		pageCount = (getItemCount() + numCells - 1) / numCells;
	}

	public int getItemCount() {
		return buttonItems.size();
	}

	private void syncButtonPageItems(int page, boolean immediate) {
		int numCells = mCellCountX * mCellCountY;
		int startIndex = page * numCells;
		int endIndex = Math.min(startIndex + numCells, getItemCount());
		GridView3D layout = (GridView3D) view_list.get(page);
		layout.removeAllViews();
		for (int i = startIndex; i < endIndex; i++) {
			float scale = 0.8f;
			View3D view = getIconItem(i);
			view.setScale(scale, scale);
			layout.addItem(view);
		}
		setCurrentPage(0);
	}

	private View3D getIconItem(int position) {
		return (View3D) (buttonItems.get(position));
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

	private List<SearchApp> getGuessList() {
		List<SearchApp> guessApps = new ArrayList<SearchApp>();
		iLoongApplication application = iLoongApplication.getInstance();
		ArrayList<ItemInfo> desktopItems = application.mModel.getDesktopIcon();
		ArrayList<Integer> removedIndex = new ArrayList<Integer>();
		for (int i = 0; i < allApps.size(); i++) {
			String name = allApps.get(i).name;
			for (int l = 0; l < desktopItems.size(); l++) {
				if (name.equals(desktopItems.get(l).title)) {
					removedIndex.add(i);
					break;
				}
			}
		}
		if (AppHost3D.appList == null) {
			return guessApps;
		}
		int[] sortArray = AppHost3D.appList.getUseFrequency();
		if (allApps.size() != sortArray.length) {
			try {
				throw new Exception("allApps.size()!=sortArray.length");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (allApps != null) {
				allApps.clear();
			}
			allApps = QSearchGroup.getAllApp();
			QSearchGroup.sNeedChange = true;
		}
		ArrayList<Integer> sortList = new ArrayList<Integer>();
		for (int i = 0; i < sortArray.length; i++) {
			sortList.add(sortArray[i]);
		}
		sortList.removeAll(removedIndex);
		if (sortList.size() == 0) {
			if (sortArray.length >= 15) {
				for (int i = sortArray.length - 1; i > sortArray.length - 16; i--) {
					guessApps.add(allApps.get(sortArray[i]));
				}
			} else {
				for (int i = sortArray.length - 1; i > -1; i--) {
					guessApps.add(allApps.get(sortArray[i]));
				}
			}
		} else if (sortList.size() >= 15) {
			for (int i = 0; i < 2; i++) {
				guessApps.add(allApps.get(sortList.get(i)));
			}
			for (int j = sortList.size() - 1; j > sortList.size() - 4; j--) {
				guessApps.add(allApps.get(sortList.get(j)));
			}
			for (int i = 2; i < 4; i++) {
				guessApps.add(allApps.get(sortList.get(i)));
			}
			for (int j = sortList.size() - 4; j > sortList.size() - 7; j--) {
				guessApps.add(allApps.get(sortList.get(j)));
			}
			for (int i = 4; i < 6; i++) {
				guessApps.add(allApps.get(sortList.get(i)));
			}
			for (int j = sortList.size() - 7; j > sortList.size() - 10; j--) {
				guessApps.add(allApps.get(sortList.get(j)));
			}
		} else {
			for (int i = 0; i < sortList.size(); i++) {
				guessApps.add(allApps.get(sortList.get(i)));
			}
		}
		return guessApps;
	}
}
