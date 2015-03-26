/**
 * 
 */
package com.cooee.launcher.trashcan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.cooee.launcher.Launcher;
import com.cooee.launcher.LauncherAnimUtils;
import com.cooee.launcher.framework.drag.DragController;
import com.cooee.launcher.framework.drag.DragSource;
import com.cooeeui.brand.turbolauncher.R;

/**
 * @author Hugo.ye added 20141220 垃圾箱
 */
public class Trashcan extends FrameLayout implements
		DragController.DragListener {

	private static final int sTransitionInDuration = 200;

	private ObjectAnimator mDropTargetBarAnim;
	private static final AccelerateInterpolator sAccelerateInterpolator = new AccelerateInterpolator();
	private ButtonDropTarget mDeleteDropTarget;
	private int mBarHeight;

	/**
	 * @param context
	 */
	public Trashcan(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public Trashcan(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public Trashcan(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void setup(Launcher launcher, DragController dragController) {
		dragController.addDragListener(this);
		dragController.addDragListener(mDeleteDropTarget);
		dragController.addDropTarget(mDeleteDropTarget);
		dragController.setFlingToDeleteDropTarget(mDeleteDropTarget);
		mDeleteDropTarget.setLauncher(launcher);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		mDeleteDropTarget = (ButtonDropTarget) findViewById(R.id.delete_target_text);
		mBarHeight = getResources().getDimensionPixelSize(
				R.dimen.trashcan_height);

		// Create the animations
		mDropTargetBarAnim = LauncherAnimUtils.ofFloat(mDeleteDropTarget,
				"translationY", -mBarHeight, 0f);
		setupAnimation(mDropTargetBarAnim, mDeleteDropTarget);
	}

	private void prepareStartAnimation(View v) {
		// Enable the hw layers before the animation starts (will be disabled in
		// the onAnimationEnd
		// callback below)
		v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		v.buildLayer();
	}

	private void setupAnimation(ObjectAnimator anim, final View v) {
		anim.setInterpolator(sAccelerateInterpolator);
		anim.setDuration(sTransitionInDuration);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				v.setLayerType(View.LAYER_TYPE_NONE, null);
			}
		});
	}

	@Override
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// TODO Auto-generated method stub
		prepareStartAnimation(mDeleteDropTarget);
		mDropTargetBarAnim.start();
	}

	@Override
	public void onDragEnd() {
		// TODO Auto-generated method stub
		prepareStartAnimation(mDeleteDropTarget);
		mDropTargetBarAnim.reverse();
	}

}
