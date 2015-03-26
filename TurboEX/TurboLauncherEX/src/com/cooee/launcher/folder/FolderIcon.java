/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cooee.launcher.folder;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.launcher.framework.ApplicationInfo;
import com.android.launcher.framework.FolderInfo;
import com.android.launcher.framework.FolderInfo.FolderListener;
import com.android.launcher.framework.IconCache;
import com.android.launcher.framework.ItemInfo;
import com.android.launcher.framework.LauncherSettings;
import com.android.launcher.framework.ShortcutInfo;
import com.cooee.launcher.BubbleTextView;
import com.cooee.launcher.CellLayout;
import com.cooee.launcher.CheckLongPressHelper;
import com.cooee.launcher.Launcher;
import com.cooee.launcher.LauncherAnimUtils;
import com.cooee.launcher.Workspace;
import com.cooee.launcher.framework.drag.DragLayer;
import com.cooee.launcher.framework.drag.DragView;
import com.cooee.launcher.framework.drag.DropTarget;
import com.cooee.launcher.framework.drag.DropTarget.DragObject;
import com.cooeeui.brand.turbolauncher.R;

/**
 * An icon that can appear on in the workspace representing an
 * {@link UserFolder}.
 */
public class FolderIcon extends LinearLayout implements FolderListener {
	private Launcher mLauncher;
	private Folder mFolder;
	private FolderInfo mInfo;
	private static boolean sStaticValuesDirty = true;

	private CheckLongPressHelper mLongPressHelper;

	// The number of icons to display in the
	private static final int NUM_ITEMS_IN_PREVIEW = 3;
	private static final int CONSUMPTION_ANIMATION_DURATION = 100;
	private static final int DROP_IN_ANIMATION_DURATION = 400;
	private static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
	private static final int FINAL_ITEM_ANIMATION_DURATION = 200;

	// The degree to which the inner ring grows when accepting drop
	// leexingwang 修改缩放尺寸 leexingwang@2014/12/17 UPD START
	// private static final float INNER_RING_GROWTH_FACTOR = 0.15f;
	private static final float INNER_RING_GROWTH_FACTOR = 0.35f;
	// leexingwang 修改缩放尺寸 leexingwang@2014/12/17 UPD END

	// leexingwang 删除外部蓝色光环放大尺寸 @2014/12/17 DEL START
	// private static final float OUTER_RING_GROWTH_FACTOR = 0.3f;
	// leexingwang 删除外部蓝色光环放大尺寸@2014/12/17 DEL END

	// The amount of vertical spread between items in the stack [0...1]

	// leexingwang 修改小的缩略图标的尺寸 @2014/12/17 UPD START
	// private static final float PERSPECTIVE_SHIFT_FACTOR = 0.24f;
	private static final float PERSPECTIVE_SHIFT_FACTOR = 0.3f;
	// leexingwang 修改小的缩略图标的尺寸 @2014/12/17 UPD END

	// The degree to which the item in the back of the stack is scaled [0...1]
	// (0 means it's not scaled at all, 1 means it's scaled to nothing)
	private static final float PERSPECTIVE_SCALE_FACTOR = 0.35f;

	// leexingwang 删除最底部影子 @2014/12/17 DEL START
	// public static Drawable sSharedFolderLeaveBehind = null;
	// leexingwang 删除最底部影子 @2014/12/17 DEL END

	private ImageView mPreviewBackground;
	private BubbleTextView mFolderName;

	FolderRingAnimator mFolderRingAnimator = null;

	// These variables are all associated with the drawing of the preview; they
	// are stored
	// as member variables for shared usage and to avoid computation on each
	// frame
	private int mIntrinsicIconSize;
	private float mBaselineIconScale;
	private int mBaselineIconSize;
	private int mAvailableSpaceInPreview;
	private int mTotalWidth = -1;
	private int mPreviewOffsetX;
	private int mPreviewOffsetY;
	private float mMaxPerspectiveShift;
	boolean mAnimating = false;

	private PreviewItemDrawingParams mParams = new PreviewItemDrawingParams(0,
			0, 0, 0);
	private PreviewItemDrawingParams mAnimParams = new PreviewItemDrawingParams(
			0, 0, 0, 0);
	private ArrayList<ShortcutInfo> mHiddenItems = new ArrayList<ShortcutInfo>();

	public FolderIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FolderIcon(Context context) {
		super(context);
		init();
	}

	private void init() {
		mLongPressHelper = new CheckLongPressHelper(this);
	}

	public boolean isDropEnabled() {
		final ViewGroup cellLayoutChildren = (ViewGroup) getParent();
		final ViewGroup cellLayout = (ViewGroup) cellLayoutChildren.getParent();
		final Workspace workspace = (Workspace) cellLayout.getParent();
		return !workspace.isSmall();
	}

	public static FolderIcon fromXml(int resId, Launcher launcher,
			ViewGroup group, FolderInfo folderInfo, IconCache iconCache) {
		@SuppressWarnings("all")
		// suppress dead code warning
		final boolean error = INITIAL_ITEM_ANIMATION_DURATION >= DROP_IN_ANIMATION_DURATION;
		if (error) {
			throw new IllegalStateException(
					"DROP_IN_ANIMATION_DURATION must be greater than "
							+ "INITIAL_ITEM_ANIMATION_DURATION, as sequencing of adding first two items "
							+ "is dependent on this");
		}

		FolderIcon icon = (FolderIcon) LayoutInflater.from(launcher).inflate(
				resId, group, false);
		icon.mFolderName = (BubbleTextView) icon
				.findViewById(R.id.folder_icon_name);
		icon.mFolderName.setText(folderInfo.title);
		icon.mPreviewBackground = (ImageView) icon
				.findViewById(R.id.preview_background);
		icon.setTag(folderInfo);
		icon.setOnClickListener(launcher);
		icon.mInfo = folderInfo;
		icon.mLauncher = launcher;
		icon.setContentDescription(String.format(
				launcher.getString(R.string.folder_name_format),
				folderInfo.title));
		Folder folder = Folder.fromXml(launcher);
		folder.setDragController(launcher.getDragController());
		folder.setFolderIcon(icon);
		folder.bind(folderInfo);
		icon.mFolder = folder;

		icon.mFolderRingAnimator = new FolderRingAnimator(launcher, icon);
		folderInfo.addListener(icon);

		return icon;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		sStaticValuesDirty = true;
		return super.onSaveInstanceState();
	}

	public static class FolderRingAnimator {
		public int mCellX;
		public int mCellY;
		private CellLayout mCellLayout;
		// leexingwang@2014/12/18 DEL START
		// public float mOuterRingSize;
		// leexingwang@2014/12/18 DEL END
		public float mInnerRingSize;
		public FolderIcon mFolderIcon = null;
		// leexingwang@2014/12/17 DEL START
		// public Drawable mOuterRingDrawable = null;
		// leexingwang@2014/12/17 DEL END
		public Drawable mInnerRingDrawable = null;
		// leexingwang@2014/12/17 DEL START
		// public static Drawable sSharedOuterRingDrawable = null;
		// leexingwang@2014/12/17 DEL END
		public static Drawable sSharedInnerRingDrawable = null;
		public static int sPreviewSize = -1;
		public static int sPreviewPadding = -1;

		private ValueAnimator mAcceptAnimator;
		private ValueAnimator mNeutralAnimator;

		public FolderRingAnimator(Launcher launcher, FolderIcon folderIcon) {
			mFolderIcon = folderIcon;
			Resources res = launcher.getResources();
			// leexingwang 去掉了外部放大的动画的图片 leexingwang@2014/12/17 DEL START
			// sSharedOuterRingDrawable = res
			// .getDrawable(R.drawable.portal_ring_outer_holo);
			// leexingwang 去掉了外部放大的动画的图片 leexingwang@2014/12/17 DEL END
			mInnerRingDrawable = res
					.getDrawable(R.drawable.portal_ring_inner_holo);

			// We need to reload the static values when configuration changes in
			// case they are
			// different in another configuration
			if (sStaticValuesDirty) {
				sPreviewSize = res
						.getDimensionPixelSize(R.dimen.folder_preview_size);
				sPreviewPadding = res
						.getDimensionPixelSize(R.dimen.folder_preview_padding);
				// leexingwang@2014/12/17 DEL START
				// sSharedOuterRingDrawable = res
				// .getDrawable(R.drawable.portal_ring_outer_holo);
				// leexingwang@2014/12/17 DEL END
				sSharedInnerRingDrawable = res
						.getDrawable(R.drawable.portal_ring_inner_holo);
				// leexingwang@2014/12/17 DEL START
				// sSharedFolderLeaveBehind = res
				// .getDrawable(R.drawable.portal_ring_rest);
				// leexingwang@2014/12/17 DEL END
				sStaticValuesDirty = false;
			}
		}

		public void animateToAcceptState() {
			if (mNeutralAnimator != null) {
				mNeutralAnimator.cancel();
			}
			mAcceptAnimator = LauncherAnimUtils.ofFloat(0f, 1f);
			mAcceptAnimator.setDuration(CONSUMPTION_ANIMATION_DURATION);

			final int previewSize = sPreviewSize;
			mAcceptAnimator.addUpdateListener(new AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					final float percent = (Float) animation.getAnimatedValue();
					// leexingwang@2014/12/17 DEL START
					// mOuterRingSize = (1 + percent * OUTER_RING_GROWTH_FACTOR)
					// * previewSize;
					// leexingwang@2014/12/17 DEL END
					mInnerRingSize = (1 + percent * INNER_RING_GROWTH_FACTOR)
							* previewSize;
					if (mCellLayout != null) {
						mCellLayout.invalidate();
					}
				}
			});
			mAcceptAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					if (mFolderIcon != null) {
						mFolderIcon.mPreviewBackground.setVisibility(INVISIBLE);
					}
				}
			});
			mAcceptAnimator.start();
		}

		public void animateToNaturalState() {
			if (mAcceptAnimator != null) {
				mAcceptAnimator.cancel();
			}
			mNeutralAnimator = LauncherAnimUtils.ofFloat(0f, 1f);
			mNeutralAnimator.setDuration(CONSUMPTION_ANIMATION_DURATION);

			final int previewSize = sPreviewSize;
			mNeutralAnimator.addUpdateListener(new AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					final float percent = (Float) animation.getAnimatedValue();
					// leexingwang@2014/12/17 DEL START
					// mOuterRingSize = (1 + (1 - percent)
					// * OUTER_RING_GROWTH_FACTOR)
					// * previewSize;
					// leexingwang@2014/12/17 DEL END
					mInnerRingSize = (1 + (1 - percent)
							* INNER_RING_GROWTH_FACTOR)
							* previewSize;
					if (mCellLayout != null) {
						mCellLayout.invalidate();
					}
				}
			});
			mNeutralAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					if (mCellLayout != null) {
						mCellLayout.hideFolderAccept(FolderRingAnimator.this);
					}
					if (mFolderIcon != null) {
						mFolderIcon.mPreviewBackground.setVisibility(VISIBLE);
					}
				}
			});
			mNeutralAnimator.start();
		}

		// Location is expressed in window coordinates
		public void getCell(int[] loc) {
			loc[0] = mCellX;
			loc[1] = mCellY;
		}

		// Location is expressed in window coordinates
		public void setCell(int x, int y) {
			mCellX = x;
			mCellY = y;
		}

		public void setCellLayout(CellLayout layout) {
			mCellLayout = layout;
		}

		// leexingwang@2014/12/18 DEL START
		// public float getOuterRingSize() {
		// return mOuterRingSize;
		// }
		// leexingwang@2014/12/18 DEL END

		public float getInnerRingSize() {
			return mInnerRingSize;
		}
	}

	public Folder getFolder() {
		return mFolder;
	}

	public FolderInfo getFolderInfo() {
		return mInfo;
	}

	private boolean willAcceptItem(ItemInfo item) {
		final int itemType = item.itemType;
		return ((itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION || itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT)
				&& !mFolder.isFull() && item != mInfo && !mInfo.opened);
	}

	public boolean acceptDrop(Object dragInfo) {
		final ItemInfo item = (ItemInfo) dragInfo;
		return !mFolder.isDestroyed() && willAcceptItem(item);
	}

	public void addItem(ShortcutInfo item) {
		mInfo.add(item);
	}

	public void onDragEnter(Object dragInfo) {
		if (mFolder.isDestroyed() || !willAcceptItem((ItemInfo) dragInfo))
			return;
		CellLayout.LayoutParams lp = (CellLayout.LayoutParams) getLayoutParams();
		CellLayout layout = (CellLayout) getParent().getParent();
		mFolderRingAnimator.setCell(lp.cellX, lp.cellY);
		mFolderRingAnimator.setCellLayout(layout);
		mFolderRingAnimator.animateToAcceptState();
		layout.showFolderAccept(mFolderRingAnimator);
	}

	public void onDragOver(Object dragInfo) {
	}

	public void performCreateAnimation(final ShortcutInfo destInfo,
			final View destView, final ShortcutInfo srcInfo,
			final DragView srcView, Rect dstRect,
			float scaleRelativeToDragLayer, Runnable postAnimationRunnable) {

		// These correspond two the drawable and view that the icon was dropped
		// _onto_
		Drawable animateDrawable = ((TextView) destView).getCompoundDrawables()[1];
		computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(),
				destView.getMeasuredWidth());

		// This will animate the first item from it's position as an icon into
		// its
		// position as the first item in the preview
		animateFirstItem(animateDrawable, INITIAL_ITEM_ANIMATION_DURATION,
				false, null);
		addItem(destInfo);

		// This will animate the dragView (srcView) into the new folder
		onDrop(srcInfo, srcView, dstRect, scaleRelativeToDragLayer, 1,
				postAnimationRunnable, null);
	}

	public void performDestroyAnimation(final View finalView,
			Runnable onCompleteRunnable) {
		Drawable animateDrawable = ((TextView) finalView)
				.getCompoundDrawables()[1];
		computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(),
				finalView.getMeasuredWidth());

		// This will animate the first item from it's position as an icon into
		// its
		// position as the first item in the preview
		animateFirstItem(animateDrawable, FINAL_ITEM_ANIMATION_DURATION, true,
				onCompleteRunnable);
	}

	public void onDragExit(Object dragInfo) {
		onDragExit();
	}

	public void onDragExit() {
		mFolderRingAnimator.animateToNaturalState();
	}

	private void onDrop(final ShortcutInfo item, DragView animateView,
			Rect finalRect, float scaleRelativeToDragLayer, int index,
			Runnable postAnimationRunnable, DragObject d) {
		item.cellX = -1;
		item.cellY = -1;

		// Typically, the animateView corresponds to the DragView; however, if
		// this is being done
		// after a configuration activity (ie. for a Shortcut being dragged from
		// AllApps) we
		// will not have a view to animate
		if (animateView != null) {
			DragLayer dragLayer = mLauncher.getDragLayer();
			Rect from = new Rect();
			dragLayer.getViewRectRelativeToSelf(animateView, from);
			Rect to = finalRect;
			if (to == null) {
				to = new Rect();
				Workspace workspace = mLauncher.getWorkspace();
				// Set cellLayout and this to it's final state to compute final
				// animation locations
				workspace.setFinalTransitionTransform((CellLayout) getParent()
						.getParent());
				float scaleX = getScaleX();
				float scaleY = getScaleY();
				setScaleX(1.0f);
				setScaleY(1.0f);
				scaleRelativeToDragLayer = dragLayer
						.getDescendantRectRelativeToSelf(this, to);
				// Finished computing final animation locations, restore current
				// state
				setScaleX(scaleX);
				setScaleY(scaleY);
				workspace.resetTransitionTransform((CellLayout) getParent()
						.getParent());
			}

			int[] center = new int[2];
			float scale = getLocalCenterForIndex(index, center);
			center[0] = (int) Math.round(scaleRelativeToDragLayer * center[0]);
			center[1] = (int) Math.round(scaleRelativeToDragLayer * center[1]);

			to.offset(center[0] - animateView.getMeasuredWidth() / 2, center[1]
					- animateView.getMeasuredHeight() / 2);

			float finalAlpha = index < NUM_ITEMS_IN_PREVIEW ? 0.5f : 0f;

			float finalScale = scale * scaleRelativeToDragLayer;
			dragLayer.animateView(animateView, from, to, finalAlpha, 1, 1,
					finalScale, finalScale, DROP_IN_ANIMATION_DURATION,
					new DecelerateInterpolator(2),
					new AccelerateInterpolator(2), postAnimationRunnable,
					DragLayer.ANIMATION_END_DISAPPEAR, null);
			addItem(item);
			mHiddenItems.add(item);
			postDelayed(new Runnable() {
				public void run() {
					mHiddenItems.remove(item);
					invalidate();
				}
			}, DROP_IN_ANIMATION_DURATION);
		} else {
			addItem(item);
		}
	}

	public void onDrop(DragObject d) {
		ShortcutInfo item;
		if (d.dragInfo instanceof ApplicationInfo) {
			// Came from all apps -- make a copy
			item = ((ApplicationInfo) d.dragInfo).makeShortcut();
		} else {
			item = (ShortcutInfo) d.dragInfo;
		}
		mFolder.notifyDrop();
		onDrop(item, d.dragView, null, 1.0f, mInfo.contents.size(),
				d.postAnimationRunnable, d);
	}

	public DropTarget getDropTargetDelegate(DragObject d) {
		return null;
	}

	private void computePreviewDrawingParams(int drawableSize, int totalSize) {
		if (mIntrinsicIconSize != drawableSize || mTotalWidth != totalSize) {
			mIntrinsicIconSize = drawableSize;
			mTotalWidth = totalSize;

			final int previewSize = FolderRingAnimator.sPreviewSize;
			final int previewPadding = FolderRingAnimator.sPreviewPadding;

			mAvailableSpaceInPreview = (previewSize - 2 * previewPadding);
			// cos(45) = 0.707 + ~= 0.1) = 0.8f
			int adjustedAvailableSpace = (int) ((mAvailableSpaceInPreview / 2) * (1 + 0.8f));

			int unscaledHeight = (int) (mIntrinsicIconSize * (1 + PERSPECTIVE_SHIFT_FACTOR));
			mBaselineIconScale = (1.0f * adjustedAvailableSpace / unscaledHeight);

			mBaselineIconSize = (int) (mIntrinsicIconSize * mBaselineIconScale);
			mMaxPerspectiveShift = mBaselineIconSize * PERSPECTIVE_SHIFT_FACTOR;

			mPreviewOffsetX = (mTotalWidth - mAvailableSpaceInPreview) / 2;
			mPreviewOffsetY = previewPadding;
		}
	}

	private void computePreviewDrawingParams(Drawable d) {
		computePreviewDrawingParams(d.getIntrinsicWidth(), getMeasuredWidth());
	}

	class PreviewItemDrawingParams {
		PreviewItemDrawingParams(float transX, float transY, float scale,
				int overlayAlpha) {
			this.transX = transX;
			this.transY = transY;
			this.scale = scale;
			this.overlayAlpha = overlayAlpha;
		}

		float transX;
		float transY;
		float scale;
		int overlayAlpha;
		Drawable drawable;
	}

	private float getLocalCenterForIndex(int index, int[] center) {
		mParams = computePreviewItemDrawingParams(
				Math.min(NUM_ITEMS_IN_PREVIEW, index), mParams);

		mParams.transX += mPreviewOffsetX;
		mParams.transY += mPreviewOffsetY;
		center[0] = mTotalWidth / 2;
		center[1] = FolderRingAnimator.sPreviewSize
				/ 2
				+ mLauncher.getResources().getDimensionPixelSize(
						R.dimen.folder_paddingTop);
		return mParams.scale;
	}

	private PreviewItemDrawingParams computePreviewItemDrawingParams(int index,
			PreviewItemDrawingParams params) {
		index = NUM_ITEMS_IN_PREVIEW - index - 1;
		float r = (index * 1.0f) / (NUM_ITEMS_IN_PREVIEW - 1);
		float scale = (1 - PERSPECTIVE_SCALE_FACTOR * (1 - r));

		float offset = (1 - r) * mMaxPerspectiveShift;
		float scaledSize = scale * mBaselineIconSize;
		float scaleOffsetCorrection = (1 - scale) * mBaselineIconSize;

		// We want to imagine our coordinates from the bottom left, growing up
		// and to the
		// right. This is natural for the x-axis, but for the y-axis, we have to
		// invert things.
		float transY = mAvailableSpaceInPreview
				- (offset + scaledSize + scaleOffsetCorrection);
		float transX = offset + scaleOffsetCorrection;
		float totalScale = mBaselineIconScale * scale;
		final int overlayAlpha = (int) (80 * (1 - r));

		if (params == null) {
			params = new PreviewItemDrawingParams(transX, transY, totalScale,
					overlayAlpha);
		} else {
			params.transX = transX;
			params.transY = transY;
			params.scale = totalScale;
			params.overlayAlpha = overlayAlpha;
		}
		return params;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		DrawIconUI(canvas);
	}

	// leexingwang 重新绘制小的缩略图 leexingwang@2014/12/17 ADD START
	private void DrawIconUI(Canvas canvas) {
		if (mFolder == null)
			return;
		if (mFolder.getItemCount() == 0 && !mAnimating)
			return;
		ArrayList<View> items = mFolder.getItemsInReadingOrder();
		computeInstrinctIconPosition(items);
		if (items.size() >= 1) {
			drawPreviewItem(canvas, colum1X, row1Y, 0, items);
		}
		if (items.size() >= 2) {
			drawPreviewItem(canvas, colum2X, row1Y, 1, items);
		}
		if (items.size() >= 3) {
			drawPreviewItem(canvas, colum1X, row2Y, 2, items);
		}
		if (items.size() >= 4) {
			drawPreviewItem(canvas, colum2X, row2Y, 3, items);
		}
	}

	// leexingwang 重新绘制小的缩略图 leexingwang@2014/12/17 ADD END
	private void drawPreviewItem(Canvas canvas, float x, float y, int index,
			ArrayList<View> items) {
		Drawable d;
		TextView v;
		canvas.save();
		canvas.scale(iconScaleFactor, iconScaleFactor);
		canvas.translate(x, y);
		v = (TextView) items.get(index);
		d = v.getCompoundDrawables()[1];
		if (d != null) {
			d.setBounds(0, 0, mIntrinsicIconSize, mIntrinsicIconSize);
			d.setFilterBitmap(true);
			d.setColorFilter(Color.argb(0, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
			d.draw(canvas);
			d.clearColorFilter();
			d.setFilterBitmap(false);
		}
		canvas.restore();
	}

	private void animateFirstItem(final Drawable d, int duration,
			final boolean reverse, final Runnable onCompleteRunnable) {
		final PreviewItemDrawingParams finalParams = computePreviewItemDrawingParams(
				0, null);

		final float scale0 = 1.0f;
		final float transX0 = (mAvailableSpaceInPreview - d.getIntrinsicWidth()) / 2;
		final float transY0 = (mAvailableSpaceInPreview - d
				.getIntrinsicHeight()) / 2;
		mAnimParams.drawable = d;

		ValueAnimator va = LauncherAnimUtils.ofFloat(0f, 1.0f);
		va.addUpdateListener(new AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				float progress = (Float) animation.getAnimatedValue();
				if (reverse) {
					progress = 1 - progress;
					mPreviewBackground.setAlpha(progress);
				}

				mAnimParams.transX = transX0 + progress
						* (finalParams.transX - transX0);
				mAnimParams.transY = transY0 + progress
						* (finalParams.transY - transY0);
				mAnimParams.scale = scale0 + progress
						* (finalParams.scale - scale0);
				invalidate();
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mAnimating = true;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mAnimating = false;
				if (onCompleteRunnable != null) {
					onCompleteRunnable.run();
				}
			}
		});
		va.setDuration(duration);
		va.start();
	}

	public void setTextVisible(boolean visible) {
		if (visible) {
			mFolderName.setVisibility(VISIBLE);
		} else {
			mFolderName.setVisibility(INVISIBLE);
		}
	}

	public boolean getTextVisible() {
		return mFolderName.getVisibility() == VISIBLE;
	}

	public void onItemsChanged() {
		invalidate();
		requestLayout();
	}

	public void onAdd(ShortcutInfo item) {
		invalidate();
		requestLayout();
	}

	public void onRemove(ShortcutInfo item) {
		invalidate();
		requestLayout();
	}

	public void onTitleChanged(CharSequence title) {
		mFolderName.setText(title.toString());
		setContentDescription(String.format(
				getContext().getString(R.string.folder_name_format), title));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Call the superclass onTouchEvent first, because sometimes it changes
		// the state to
		// isPressed() on an ACTION_UP
		boolean result = super.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLongPressHelper.postCheckForLongPress();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mLongPressHelper.cancelLongPress();
			break;
		}
		return result;
	}

	@Override
	public void cancelLongPress() {
		super.cancelLongPress();

		mLongPressHelper.cancelLongPress();
	}

	// leexingwang 计算小的缩略图的位置 leexingwang@2014/12/17 ADD START
	int row1Y;
	int row2Y;
	int colum1X;
	int colum2X;
	int iconSize;
	static final float iconScaleFactor = 0.33f;
	static final int iconMargin = 9;
	TextView v;
	Drawable d;

	private void computeInstrinctIconPosition(ArrayList<View> items) {
		v = (TextView) items.get(0);
		d = v.getCompoundDrawables()[1];
		computePreviewDrawingParams(d);
		int left = mPreviewBackground.getLeft();
		int top = mPreviewBackground.getTop();
		int width = mPreviewBackground.getWidth();
		int height = mPreviewBackground.getWidth();
		int iconSize = (int) (mIntrinsicIconSize * iconScaleFactor);
		row1Y = top + (height - iconSize * 2 - iconMargin) / 2;
		row2Y = row1Y + iconSize + iconMargin;
		colum1X = left + (width - iconSize * 2 - iconMargin) / 2;
		colum2X = colum1X + iconSize + iconMargin;
		row1Y = (int) (row1Y / iconScaleFactor);
		row2Y = (int) (row2Y / iconScaleFactor);
		colum1X = (int) (colum1X / iconScaleFactor);
		colum2X = (int) (colum2X / iconScaleFactor);
	}
	// leexingwang 计算小的缩略图的位置 leexingwang@2014/12/17 ADD END
}
