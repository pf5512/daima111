//wanghongjian add whole file	//enable_DefaultScene
package com.iLoong.launcher.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iLoong.RR;
import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.SetupMenu.PageGridView;
import com.iLoong.launcher.SetupMenu.PagedView;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.SetupMenu.Tools;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.desktop.iLoongLauncher;

public class SceneDetailed extends FrameLayout implements View.OnClickListener,
		PagedView.ViewSwitchListener {
	private static final int COMMON_PADDING = 6;
	private static final int TITLETXT_FONTSIZE = 18;
	private static final int BUTTON_FONTSIZE = 12;

	private static final int BUTTON_EVENT_APPLYTHEME = 10101;
	private static final int BUTTON_EVENT_DELETETHEME = 10102;
	private static final int BUTTON_EVENT_RETURN = 10103;
	private Context mContext;
	private SceneDescription mData;
	private LinearLayout mdesktop;
	private ScenePagePointer mIndicatorLayout;
	private TextView mMyTitle;
	private Button mButtonApplyTheme;
	private Button mButtonRemoveTheme;
	private ArrayList<Bitmap> mbitmaps = new ArrayList<Bitmap>();
	private ScenesGridLayout mThemesGridLayout;
	private Root3D root = null;

	public SceneDetailed(Context context) {
		super(context);
		mContext = context;

	}

	public void Release() {
		for (int i = 0; i < mbitmaps.size(); i++) {
			Bitmap bmp = mbitmaps.get(i);
			if (bmp != null)
				bmp.recycle();
		}
		if (mIndicatorLayout != null)
			mIndicatorLayout.Release();
	}

	public SceneDescription getThemeDesc() {
		return mData;
	}

	@Override
	public void onSwitched(View view, int position) {
		mIndicatorLayout.SelectPage(position);
	}

	protected void onAttachedToWindow() {
		mThemesGridLayout.InitToScreen(1, SetupMenu.getInstance().mWidth);
	}

	private boolean IsSystemByComponent(ComponentName componentName) {
		Intent intent = new Intent();
		intent.setComponent(componentName);
		List<ResolveInfo> mWidgetResolveInfoList = mContext.getPackageManager()
				.queryIntentActivities(
						intent,
						PackageManager.GET_ACTIVITIES
								| PackageManager.GET_META_DATA);
		int flags = mWidgetResolveInfoList.get(0).activityInfo.applicationInfo.flags;
		boolean installed = false;
		if ((flags & android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			installed = true;
		} else if ((flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0) {
			installed = true;
		}
		return installed;
	}

	private void LoadLayout() {
		mdesktop = new LinearLayout(mContext);
		LayoutParams desktoplp = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mdesktop.setOrientation(LinearLayout.VERTICAL);
		mdesktop.setBackgroundColor(Color.rgb(250, 250, 250));
		addView(mdesktop, desktoplp);

		LinearLayout titleLayout = (LinearLayout) View.inflate(mContext,
				RR.layout.detil_scene_title, null);
		mMyTitle = (TextView) titleLayout.findViewById(RR.id.textSceneAppName);
		ImageButton btnReturn = (ImageButton) titleLayout
				.findViewById(RR.id.btnSceneReturn);
		ImageButton btnDelete = (ImageButton) titleLayout
				.findViewById(RR.id.btnSceneDelete);

		boolean installed = IsSystemByComponent(mData.componentName);
		// Log.v("", "install is " + installed);
		if (!installed) {
			btnDelete.setEnabled(false);
		}
		int titleSize = 0;

		if (Utils3D.getScreenWidth() < 500) {
			titleSize = (int) iLoongLauncher.getInstance().getResources()
					.getDimension(RR.dimen.detile_scene_title_text_size);
		} else if (Utils3D.getScreenWidth() > 500
				&& Utils3D.getScreenWidth() < 800) {
			titleSize = (int) iLoongLauncher.getInstance().getResources()
					.getDimension(RR.dimen.detile_scene_title_text_size_seven);

		} else {
			titleSize = (int) iLoongLauncher.getInstance().getResources()
					.getDimension(RR.dimen.detile_scene_title_text_size_big);

		}
		mMyTitle.setTextSize(titleSize);
		mMyTitle.setText(mData.title);
		mMyTitle.setTextColor(Color.rgb(0x3d, 0x3d, 0x3d));
		btnReturn.setTag(Integer.valueOf(BUTTON_EVENT_RETURN));
		btnReturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SceneDetailedActivity.getInstance().finish();
			}
		});
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SceneManager.getInstance().RemoveTheme(mData);
			}
		});
		mdesktop.addView(titleLayout);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mThemesGridLayout = new ScenesGridLayout(mContext);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		lp.topMargin = (int) iLoongLauncher.getInstance().getResources()
				.getDimension(RR.dimen.detile_scene_grid_margint_top);
		mdesktop.addView(mThemesGridLayout, lp);

		int bh = (int) (84 * SetupMenu.mScale);
		int bw = (int) (168 * SetupMenu.mScale);

		LinearLayout Layout = new LinearLayout(mContext);
		Layout.setOrientation(LinearLayout.HORIZONTAL);
		Layout.setBackgroundColor(Color.rgb(0xb5, 0xb5, 0xb5));
		LayoutParams flp = new LayoutParams(LayoutParams.FILL_PARENT, bh);
		Layout.setGravity(Gravity.CENTER);
		flp.gravity = Gravity.BOTTOM;
		addView(Layout, flp);

		mIndicatorLayout = new ScenePagePointer(mContext, true);
		mIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				mIndicatorLayout.getheight());
		mdesktop.addView(mIndicatorLayout, lp);
		mButtonApplyTheme = new Button(mContext);
		mButtonApplyTheme.setText(RR.string.applyScene);
		mButtonApplyTheme.setTextColor(Color.WHITE);
		// mButtonApplyTheme.setTextSize(BUTTON_FONTSIZE);
		mButtonApplyTheme.setTag(Integer.valueOf(BUTTON_EVENT_APPLYTHEME));
		mButtonApplyTheme.setBackgroundDrawable(iLoongLauncher.getInstance()
				.getResources().getDrawable(RR.drawable.aplay_scene_selector));
		mButtonApplyTheme.setOnClickListener(this);
		lp = new LinearLayout.LayoutParams(bw, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		Layout.setBackgroundColor(Color.rgb(0xea, 0xe9, 0xe7));
		// lp.height = (int) (bh * hScale);
		Layout.addView(mButtonApplyTheme, lp);

		// if (!DefaultLayout.hide_remove_theme_button) {
		// mButtonRemoveTheme = new Button(mContext);
		//
		// mButtonRemoveTheme.setText(RR.string.removeScene);
		// mButtonRemoveTheme.setTextColor(Color.BLACK);
		// mButtonRemoveTheme.setTextSize(BUTTON_FONTSIZE);
		// mButtonRemoveTheme.setTag(Integer.valueOf(BUTTON_EVENT_DELETETHEME));
		// mButtonRemoveTheme.setOnClickListener(this);
		// lp = new LinearLayout.LayoutParams(bw, LayoutParams.WRAP_CONTENT);
		// lp.gravity = Gravity.CENTER;
		// // lp.height = (int) (bh * hScale);
		// Layout.addView(mButtonRemoveTheme, lp);
		// }
	}

	public void LoadData(int index) {

		mData = SceneManager.getInstance().getThemeDescriptions()
				.elementAt(index);
		LoadLayout();
		ArrayList<String> bitmaps = mData.getBitmaps();

		int cellX = 0;
		int cellY = 0;
		int cellwidth = (int) (SetupMenu.getInstance().mWidth);
		int cellheight = (int) (SetupMenu.getInstance().mHeight * 0.6f);
		// int cellwidth = (int)
		// iLoongLauncher.getInstance().getResources().getDimension(RR.dimen.detile_scene_gridwidth);
		// int cellheight = (int)
		// iLoongLauncher.getInstance().getResources().getDimension(RR.dimen.detile_scene_gridheight);
		int count = bitmaps.size();
		mIndicatorLayout.Init(count);
		LinearLayout.LayoutParams nvlp = (LinearLayout.LayoutParams) mIndicatorLayout
				.getLayoutParams();
		if (Utils3D.getScreenWidth() < 500) {
			nvlp.topMargin = (int) iLoongLauncher.getInstance().getResources()
					.getDimension(RR.dimen.detile_scene_indi_margint_top);

		} else if (Utils3D.getScreenWidth() > 500
				&& Utils3D.getScreenWidth() < 800) {
			nvlp.topMargin = (int) iLoongLauncher.getInstance().getResources()
					.getDimension(RR.dimen.detile_scene_indi_margint_top_seven);

		} else {
			nvlp.topMargin = (int) iLoongLauncher.getInstance().getResources()
					.getDimension(RR.dimen.detile_scene_indi_margint_top_big);

		}
		nvlp.gravity = Gravity.CENTER;
		mIndicatorLayout.setLayoutParams(nvlp);

		LinearLayout Layout;
		PageGridView ThemesGridView;
		LinearLayout.LayoutParams mglp;
		LayoutParams lp;
		PageGridView.LayoutParams pagelp;

		ThemesGridView = new PageGridView(mContext);
		mglp = new LinearLayout.LayoutParams(cellwidth, cellheight);

		ThemesGridView.setCellDimensions(cellwidth, cellheight, 0, 0);

		TextView info = new TextView(mContext);
		mData.getInfo(info);
		info.setTextColor(Color.BLACK);

		Layout = new LinearLayout(mContext);
		Layout.addView(info);

		int textMargin = (int) (80 * SetupMenu.mScale);
		pagelp = new PageGridView.LayoutParams(cellX, cellY);
		pagelp.topMargin = textMargin;
		pagelp.leftMargin = textMargin;
		ThemesGridView.addView(Layout, pagelp);
		mThemesGridLayout.addView(ThemesGridView, mglp);

		for (int i = 0; i < count; i++) {

			LinearLayout detilLinearLayout = (LinearLayout) View.inflate(
					mContext, RR.layout.scene_detil_item, null);
			ImageView detilImage = (ImageView) detilLinearLayout
					.findViewById(RR.id.imageSceneDetilThumb);

			ThemesGridView = new PageGridView(mContext);
			mglp = new LinearLayout.LayoutParams(cellwidth, cellheight);

			ThemesGridView.setCellDimensions(cellwidth, cellheight, 0, 0);

			ImageView image = new ImageView(mContext);

			Bitmap bmp = null;
			try {
				bmp = Tools.getImageFromInStream(mData.getContext().getAssets()
						.open(SceneDescription.PREVIEW_DIR + bitmaps.get(i)));
			} catch (IOException e) {
			}

			if (bmp != null) {
				image.setImageBitmap(bmp);
				detilImage.setImageBitmap(bmp);
				mbitmaps.add(bmp);
			}

			// Layout = new LinearLayout(mContext);
			// Layout.setOrientation(LinearLayout.HORIZONTAL);
			// int imagew = cellwidth;
			// int imageh = cellheight;
			// lp = new LayoutParams(imagew, imageh);
			// lp.gravity = Gravity.CENTER;
			// Layout.addView(image, lp);

			pagelp = new PageGridView.LayoutParams(cellX, cellY);
			pagelp.topMargin = (int) (COMMON_PADDING * SetupMenu.mScale);
			ThemesGridView.addView(detilLinearLayout, pagelp);
			mThemesGridLayout.addView(ThemesGridView, mglp);
			// mThemesGridLayout.addView(detilLinearLayout);
		}

		if (mButtonApplyTheme != null) {
			if (mData.mUse) {
				mButtonApplyTheme.setEnabled(false);
				mButtonApplyTheme.setTextColor(Color.GRAY);
			}
		}

		mThemesGridLayout.setLoop(false);
		mThemesGridLayout.setOverScroll(false);
		mThemesGridLayout.setScrollingSpeed(2.0f);
		mThemesGridLayout.setSwitchListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v instanceof Button) {
			Integer event = (Integer) v.getTag();
			String sceneToast = "";
			switch (event.intValue()) {
			case BUTTON_EVENT_APPLYTHEME:

				String cls = mData.componentName.getClassName();
				String pkg = mData.componentName.getPackageName();

				if (root == null) {
					root = iLoongLauncher.getInstance().d3dListener.root;
				}

				if (root != null) {
					if (!root.isSceneTheme) {
						sceneToast = iLoongLauncher.getInstance()
								.getResources().getString(RR.string.sceneToast);
					} else {
						sceneToast = iLoongLauncher.getInstance()
								.getResources()
								.getString(RR.string.sceneChangeToast);
					}

					if (mData.mUse) {
						sceneToast = iLoongLauncher.getInstance()
								.getResources().getString(RR.string.sceneToast);
					} else {
						if (SceneManager.getInstance().getThemeDescriptions() != null) {
							for (int i = 0; i < SceneManager.getInstance()
									.getThemeDescriptions().size(); i++) {

								SceneManager.getInstance()
										.getThemeDescriptions().elementAt(i).mUse = false;
							}

						}
						mData.mUse = true;
					}

					root.setSceneTheme(pkg, cls);
					iLoongLauncher.getInstance().postRunnable(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							root.startScene();
						}
					});
					Toast.makeText(mContext, sceneToast, Toast.LENGTH_SHORT)
							.show();

					SceneManager.getInstance().KillActivity();
				}
				Log.v("", "theme pkg is " + pkg + " cls is " + cls);

				break;
			case BUTTON_EVENT_DELETETHEME:
				SceneManager.getInstance().RemoveTheme(mData);
				break;

			default:
				break;
			}
		}
	}
}
