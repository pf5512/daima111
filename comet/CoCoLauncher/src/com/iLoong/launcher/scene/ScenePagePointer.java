//wanghongjian add whole file	//enable_DefaultScene
package com.iLoong.launcher.scene;

import java.io.IOException;

import com.iLoong.RR;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.desktop.iLoongLauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ScenePagePointer extends LinearLayout {
	// public static final String PAGE_NV_NORMAL =
	// "setupmenu/page_nv_normal.png";
	// public static final String PAGE_NV_FOCUSED =
	// "setupmenu/page_nv_focused.png";
	public static final String PAGE_SCENE_NORMAL = "theme/scenetheme/scene_dot_notselect.png";
	public static final String PAGE_SCENE_FOCUSED = "theme/scenetheme/scene_dot_select.png";
	public static final String PAGE_INFO_NORMAL = "setupmenu/page_info_normal.png";
	public static final String PAGE_INFO_FOCUSED = "setupmenu/page_info_focused.png";

	private boolean mUseInfo = true;
	private Context mContext;
	private int page;
	private Bitmap mvnf, mvnn, minf, minn;
	private int mHight;

	public ScenePagePointer(Context context, boolean use) {
		super(context);
		page = 0;
		mUseInfo = use;
		mContext = context;
		try {

			mvnf = Tools.getImageFromInStream(mContext.getAssets().open(
					PAGE_SCENE_FOCUSED));
			mvnf = Tools.resizeBitmap(mvnf,
					(int) (mvnf.getWidth() * SetupMenu.mScale),
					(int) (mvnf.getHeight() * SetupMenu.mScale));

			mvnn = Tools.getImageFromInStream(mContext.getAssets().open(
					PAGE_SCENE_NORMAL));

			mvnn = Tools.resizeBitmap(mvnn,
					(int) (mvnn.getWidth() * SetupMenu.mScale),
					(int) (mvnn.getHeight() * SetupMenu.mScale));
			if (mUseInfo) {
				minf = Tools.getImageFromInStream(mContext.getAssets().open(
						PAGE_INFO_FOCUSED));
				minf = Tools.resizeBitmap(minf,
						(int) (minf.getWidth() * SetupMenu.mScale),
						(int) (minf.getHeight() * SetupMenu.mScale));

				minn = Tools.getImageFromInStream(mContext.getAssets().open(
						PAGE_INFO_NORMAL));
				minn = Tools.resizeBitmap(minn,
						(int) (minn.getWidth() * SetupMenu.mScale),
						(int) (minn.getHeight() * SetupMenu.mScale));
			}
		} catch (IOException e) {
		}
		mHight = (int) (mvnf.getHeight() * SetupMenu.mScale);
	}

	public void Release() {
		if (mvnf != null)
			mvnf.recycle();
		if (mvnn != null)
			mvnn.recycle();
		if (minf != null)
			minf.recycle();
		if (minn != null)
			minn.recycle();
	}

	public int getheight() {
		return mHight;
	}

	public final void Init(int count) {
		removeAllViews();
		page = 0;
		if (mUseInfo) {
			page = 1;
			ImageView imageview = new ImageView(mContext);
			if (minn != null)
				imageview.setImageBitmap(minn);
			addView(imageview);
		}
		for (int j = 0; j < count; j++) {
			ImageView imageview = new ImageView(mContext);
			if (mvnn != null) {
				imageview.setImageBitmap(mvnn);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.rightMargin = (int) iLoongLauncher.getInstance()
						.getResources()
						.getDimension(RR.dimen.detile_scene_indi_margint_right);
				addView(imageview, lp);
			}

		}
		SelectPage(page);
	}

	public final void SelectPage(int nextpage) {
		if (nextpage >= getChildCount())
			return;

		if (!mUseInfo) {
			ImageView child = (ImageView) getChildAt(page);
			if (mvnn != null)
				child.setImageBitmap(mvnn);
			page = nextpage;
			child = (ImageView) getChildAt(page);
			if (mvnf != null)
				child.setImageBitmap(mvnf);
		} else {

			ImageView child = (ImageView) getChildAt(page);
			if (page == 0) {
				if (minn != null)
					child.setImageBitmap(minn);
			} else {
				if (mvnn != null)
					child.setImageBitmap(mvnn);
			}
			page = nextpage;

			child = (ImageView) getChildAt(page);
			if (page == 0) {
				if (minf != null)
					child.setImageBitmap(minf);
			} else {
				if (mvnf != null)
					child.setImageBitmap(mvnf);
			}
		}
		invalidate();
	}
}
