package com.iLoong.launcher.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.graphics.Bitmap;
import android.util.Log;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.coco.theme.themebox.util.Tools;
import com.iLoong.launcher.Desktop3D.ListView3D;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.desktop.iLoongLauncher;

public class AllAppList extends ListView3D {

	public static Set<String> mLetterList = new TreeSet<String>();
	private View3D mBottomFill;
	private SearchEditTextGroup mSearchEditTextGroup;
	public static List<SearchApp> allApp;

	public AllAppList(String name) {
		super(name);
		setSize(QsearchConstants.W_ALL_APP_LIST,
				QsearchConstants.H_ALL_APP_LIST);
		mSearchEditTextGroup = new SearchEditTextGroup("searchEditTextGroup");
		try {
			Bitmap bgBmp = Tools.getImageFromInStream(iLoongLauncher
					.getInstance().getAssets()
					.open("theme/quick_search/qs_bg.png"));
			NinePatch bgNp = new NinePatch(new BitmapTexture(bgBmp, true), 1,
					1, 1, 1);
			setBackgroud(bgNp);
			mBottomFill = new View3D("bottomFill");
			mBottomFill.setSize(QsearchConstants.W_SEARCH_EDIT_GROUP,
					QsearchConstants.H_SEARCH_EDIT_GROUP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void release() {
		removeAllViews();
	}

	public void load() {
		allApp = QSearchGroup.getAllApp();
		if (mLetterList != null) {
			mLetterList.clear();
		}
		for (int i = 0; i < allApp.size(); i++) {
			String name = allApp.get(i).title_pinyin;
			Log.i("FUCK", "PinYinName : " + name);
			String firstLetter = name.substring(0, 1);
			for (int k = 0; k < LettersGroup.b.length; k++) {
				if (firstLetter.equalsIgnoreCase(LettersGroup.b[k])) {
					mLetterList.add(LettersGroup.b[k]);
				} else {
					mLetterList.add(LettersGroup.b[0]);
				}
			}
		}
		for (Iterator<String> it = mLetterList.iterator(); it.hasNext();) {
			String fName = it.next();
			int categoryCount = 0;
			for (int i = 0; i < allApp.size(); i++) {
				SearchApp btn = allApp.get(i);
				String firstLetter = btn.title_pinyin.substring(0, 1);
				if (fName.equals("#")) {
					if (!firstLetter.equalsIgnoreCase("A")
							&& !firstLetter.equalsIgnoreCase("B")
							&& !firstLetter.equalsIgnoreCase("C")
							&& !firstLetter.equalsIgnoreCase("D")
							&& !firstLetter.equalsIgnoreCase("E")
							&& !firstLetter.equalsIgnoreCase("F")
							&& !firstLetter.equalsIgnoreCase("G")
							&& !firstLetter.equalsIgnoreCase("H")
							&& !firstLetter.equalsIgnoreCase("I")
							&& !firstLetter.equalsIgnoreCase("J")
							&& !firstLetter.equalsIgnoreCase("K")
							&& !firstLetter.equalsIgnoreCase("L")
							&& !firstLetter.equalsIgnoreCase("M")
							&& !firstLetter.equalsIgnoreCase("N")
							&& !firstLetter.equalsIgnoreCase("O")
							&& !firstLetter.equalsIgnoreCase("P")
							&& !firstLetter.equalsIgnoreCase("Q")
							&& !firstLetter.equalsIgnoreCase("R")
							&& !firstLetter.equalsIgnoreCase("S")
							&& !firstLetter.equalsIgnoreCase("T")
							&& !firstLetter.equalsIgnoreCase("U")
							&& !firstLetter.equalsIgnoreCase("V")
							&& !firstLetter.equalsIgnoreCase("W")
							&& !firstLetter.equalsIgnoreCase("X")
							&& !firstLetter.equalsIgnoreCase("Y")
							&& !firstLetter.equalsIgnoreCase("Z")) {
						categoryCount++;
					}
				} else if (firstLetter.equalsIgnoreCase(fName)) {
					categoryCount++;
				}
			}
			int numCells = 4 * 1;
			int row = (categoryCount + numCells - 1) / numCells;
			AppListItem item = new AppListItem(fName, row);
			addItem(item);
		}
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			if (getChildAt(i) instanceof AppListItem) {
				AppListItem item = (AppListItem) getChildAt(i);
				item.load();
			}
		}
		addItem(mBottomFill);
	}

	public void reLoad() {
		release();
		load();
	}

	@Override
	public boolean onClick(float x, float y) {
		if (mSearchEditTextGroup.mEditText != null) {
			if (y > mSearchEditTextGroup.mEditText.y) {
				mSearchEditTextGroup.mEditText.hideInputKeyboard();
			}
		}
		if (SearchEditTextGroup.mStatus == SearchEditTextGroup.POS_STATUS_TOP) {
			return true;
		}
		return super.onClick(x, y);
	}

	@Override
	public boolean onDoubleClick(float x, float y) {
		return true;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		return true;
	}
}
