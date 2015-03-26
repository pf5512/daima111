package com.iLoong.launcher.Desktop3D;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.Desktop3D.APageEase.APageEase;
import com.iLoong.launcher.SetupMenu.Actions.SetupMenuActions;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ParticleManager;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class NPageBase extends ViewGroup3D {

	final public static float MAX_X_ROTATION = 0.5f;
	boolean needXRotation = true;
	public IndicatorView indicatorView;
	public float indicatorPaddingBottom = 0;
	protected boolean drawIndicator = true;
	// private TextureRegion indicator1 = null;
	// private TextureRegion indicator2 = null;
	protected int page_index;
	protected ArrayList<View3D> view_list;

	private boolean random;
	private boolean sequence;
	private boolean canScroll;
	protected float xScale;
	protected float yScale;
	protected boolean moving;
	protected int mType;
	protected float mVelocityX;

	protected boolean needLayout = false;
	private static float INDICATOR_FADE_TWEEN_DURATION = 1.0f;

	protected Tween tween;
	List<PageScrollListener> scrollListeners = new ArrayList<PageScrollListener>();

	protected ArrayList<Integer> mTypelist;

	/************************ added by zhenNan.ye begin ***************************/
	private float last_x = 0;
	private float last_y = 0;

	/************************ added by zhenNan.ye end ***************************/

	public NPageBase(String name) {
		super(name);
		// if(indicatorView == null)indicatorView = new
		// IndicatorView("npage_indicator",R3D.getTextureRegion("application-page-nv-point1"),R3D.getTextureRegion("application-page-nv-point2"));
		page_index = 0;
		xScale = 0f;
		yScale = 0f;
		mType = APageEase.COOLTOUCH_EFFECT_DEFAULT;
		random = true;
		canScroll = false;
		view_list = new ArrayList<View3D>();
		moving = false;
		mTypelist = new ArrayList<Integer>();
		APageEase.initEffectMap();
		setTotalList();
	}

	void setWholePageList() {
		mTypelist.clear();

		for (int i = 0; i < R3D.workSpace_list_string.length; i++) {
			mTypelist.add(APageEase.mEffectMap
					.get(R3D.workSpace_list_string[i]));
		}
	}

	void setTotalList() {

		mTypelist.clear();
		for (int i = 0; i < R3D.app_list_string.length; i++) {
			mTypelist.add(APageEase.mEffectMap.get(R3D.app_list_string[i]));
		}
		setEffectType(SetupMenuActions.getInstance().getStringToIntger(
				"desktopeffects"));
	}

	protected void initView() {
		View3D view;
		for (int i = 0; i < view_list.size(); i++) {
			view = view_list.get(i);
			view.setPosition(0, 0);
			view.setRotationZ(0);
			view.setScale(1.0f, 1.0f);
			view.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
			view.setOrigin(view.width / 2, view.height / 2);
			view.setZ(0);
			view.setOriginZ(0);

			if (i != page_index)
				view.hide();
			else
				view.show();

			// 还原icon
			if (view instanceof ViewGroup3D) {
				int size = ((ViewGroup3D) view).getChildCount();
				View3D icon;

				for (int j = 0; j < size; j++) {
					icon = ((ViewGroup3D) view).getChildAt(j);
					icon.setRotationZ(0);
					icon.setScale(1.0f, 1.0f);
					icon.setColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
					icon.setOrigin(icon.width / 2, icon.height / 2);
					icon.setOriginZ(0);
					// Object obj = icon.getTag();
					// if(obj!= null)
					// {
					// if(obj instanceof Vector2)
					// {
					// icon.setPosition(((Vector2)icon.getTag()).x,((Vector2)icon.getTag()).y);
					// }
					// }
				}
				if (view instanceof GridView3D)
					((GridView3D) view).layout_pub(0, false);
			}
		}
		// setDegree(0f);
		// setDegree(0f, 0f);
		moving = false;
	}

	public void initData(ViewGroup3D view) {
		// Log.v("NpageBase", "initData");
		// Color temp_color;
		//
		// view.setPosition(0, 0);
		// view.setZ(0);
		// view.setRotationVector(0, 0, 1);
		// view.setRotation(0);
		// view.setRotationAngle(0, 0, 0);
		// view.setScale(1.0f, 1.0f);
		// view.setScaleZ(1.0f);
		// view.setOrigin(width / 2, height / 2);
		// view.setOriginZ(0);
		// temp_color = getColor();
		// temp_color.a = 1;
		// view.setColor(temp_color);
		//
		// int size;
		// if(view instanceof GridView3D){
		// size = ((GridView3D)view).getChildCount();
		//
		// }
		// else
		// size = view.getChildCount();
		// for(int i = 0;i<size;i++)
		// {
		// View3D icon;
		// if(view instanceof GridView3D)
		// {
		// icon = ((GridView3D)view).getChildAt(i);
		// Object obj = icon.getTag();
		// if(obj!= null)
		// {
		// if(obj instanceof Vector2)
		// {
		// icon.setPosition(((Vector2)icon.getTag()).x,((Vector2)icon.getTag()).y);
		// }
		// }
		// }
		// else
		// {
		// icon = view.getChildAt(i);
		// }
		// icon.setZ(0);
		// icon.setRotationVector(0, 0, 1);
		// icon.setRotation(0);
		// icon.setRotationAngle(0, 0, 0);
		// icon.setScale(1.0f, 1.0f);
		// icon.setScaleZ(1.0f);
		// icon.setOriginZ(0);
		// temp_color = icon.getColor();
		// temp_color.a = 1;
		// icon.setColor(temp_color);
		// icon.show();
		// }
	}

	public boolean getRandom() {
		return this.random;
	}

	protected int getIndicatorPageCount() {
		return this.view_list.size();
	}

	protected int getIndicatorPageIndex() {
		return this.page_index;
	}

	protected void setEffectType(int type) {
		this.random = false;
		this.sequence = false;
		/*
		 * if (type == APageEase.COOLTOUCH_EFFECT_SEQUENCE) { this.mType =
		 * APageEase.COOLTOUCH_EFFECT_BINARIES; this.sequence = true; } else
		 */
		if (type == 1) {
			this.mType = 2;
			this.random = true;
		} else {
			this.mType = type;
		}
		if (this.isVisible())
			initView();
	}

	protected void addPage(View3D view) {
		view.setPosition(0, 0);
		if (view_list.size() != 0) {
			view.hide();
		}
		view_list.add(view);

		addView(view);
	}

	public void addPage(int index, View3D view) {
		view.setPosition(0, 0);
		if (view_list.size() != 0) {
			view.hide();
		}
		view_list.add(index, view);

		this.addViewAt(index, view);
	}

	protected int nextIndex() {
		return (page_index == view_list.size() - 1 ? 0 : page_index + 1);
	}

	protected int preIndex() {
		return (page_index == 0 ? view_list.size() - 1 : page_index - 1);
	}

	protected void changeEffect() {
		initView();
		if (this.random) {
			moving = true;
			mType = MathUtils.random(3, mTypelist.size() - 1);
		} else if (this.sequence) {
			moving = true;
			mType++;
			if (mType == mTypelist.size())
				mType = 3;
		}
	}

	protected void updateEffect() {
		// Log.v("NPageBase", "updateEffect");
		if (view_list.size() == 0)
			return;
		if (page_index < 0) {
			page_index = 0;
			return;
		}
		if (page_index > view_list.size() - 1) {
			page_index = view_list.size() - 1;
			return;
		}
		if (DefaultLayout.enable_DesktopIndicatorScroll
				&& Root3D.scroll_indicator) {
			return;
		}
		ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
		ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
		ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());
		if (!moving) {
			changeEffect();
			moving = true;
			for (View3D i : view_list) {
				if (i instanceof GridView3D) {
					for (int j = 0; j < ((GridView3D) i).getChildCount(); j++) {
						View3D icon = ((GridView3D) i).getChildAt(j);
						icon.setTag(new Vector2(icon.getX(), icon.getY()));
					}
				}
			}

		}
		float tempYScale = 0;

		if (needXRotation) {
			if (yScale > MAX_X_ROTATION) {
				tempYScale = MAX_X_ROTATION;
			} else if (yScale < -MAX_X_ROTATION) {
				tempYScale = -MAX_X_ROTATION;
			} else {
				tempYScale = yScale;
			}
		}

		tempYScale = -tempYScale;
		if (this.random == false && this.mType == 0) {
			APageEase.setStandard(true);
		} else {
			APageEase.setStandard(false);
		}
		if (xScale > 0) {
			next_view.hide();
			// initData(next_view);
			APageEase.updateEffect(pre_view, cur_view, xScale - 1, tempYScale,
					mTypelist.get(mType));
		} else if (xScale < 0) {
			pre_view.hide();
			// initData(pre_view);
			APageEase.updateEffect(cur_view, next_view, xScale, tempYScale,
					mTypelist.get(mType));
		} else if (yScale != 0) {
			APageEase.updateEffect(cur_view, next_view, xScale, tempYScale,
					mTypelist.get(mType));
		}

		if (xScale < -1f) {
			cur_view.hide();
			// initData(cur_view);
			page_index = nextIndex();
			setDegree(xScale + 1f);
			changeEffect();
		}
		if (xScale > 1f) {
			cur_view.hide();
			// initData(cur_view);
			page_index = preIndex();
			changeEffect();
		}

	}

	public void updateEffect(float scroll_degree) {
		if (page_index < 0) {
			page_index = 0;
			return;
		}
		if (page_index > view_list.size() - 1) {
			page_index = view_list.size() - 1;
			return;
		}
		if (view_list.size() <= 1) {
			return;
		}
		if (getRandom() == false && mType == 0) {
			APageEase.setStandard(true);
		} else {
			APageEase.setStandard(false);
		}

		page_index = (int) (scroll_degree + 0.5);
		// Log.v("jbc","888update temp_degree="+scroll_degree+" page_index="+page_index);

		ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
		ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
		ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());
		if (!moving) {
			moving = true;
		}

		APageEase.updateEffect(pre_view, cur_view, next_view, scroll_degree,
				false);
	}

	protected void setDegree(float degree) {
		this.xScale = degree;

		// TODO Auto-generated method stub
		if (this.scrollListeners != null) {
			for (int i = 0; i < scrollListeners.size(); i++) {
				PageScrollListener scrollListener = scrollListeners.get(i);

				scrollListener.pageScroll(xScale, page_index, view_list.size());
				if (xScale == 0
						&& scrollListener.getIndex() != this.getCurrentPage()) {
					scrollListener.setCurrentPage(this.getCurrentPage());
				}
			}
		}
		onDegreeChanged();
	}

	void setDegreeOnly(float degree) {
		this.xScale = degree;
		// TODO Auto-generated method stub
	}

	void setDegree(float xScale, float yScale) {
		this.xScale = xScale;
		this.yScale = yScale;
		if (this.scrollListeners != null) {
			for (int i = 0; i < scrollListeners.size(); i++) {
				PageScrollListener scrollListener = scrollListeners.get(i);
				scrollListener.pageScroll(xScale, page_index, view_list.size());

				if (xScale == 0
						&& scrollListener.getIndex() != this.getCurrentPage()) {
					scrollListener.setCurrentPage(this.getCurrentPage());
				}
			}
		}
		onDegreeChanged();
	}

	void stopAutoEffect() {
		// TODO Auto-generated method stub
		if (tween != null && !tween.isFinished()) {
			tween.free();
			tween = null;
		}
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return xScale;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return yScale;
	}

	public void onDegreeChanged() {

	}

	protected boolean isManualScrollTo = false;
	int ScrollDestPage = 0;
	int ScrollstartPage = 0;
	int scrollDire = 1;
	// float scrollxScale = 0;
	int scrollCurPage = 0;

	// teapotXu add start for Folder in Mainmenu
	public boolean NPage_IsManualScrollTo() {
		return isManualScrollTo;
	}

	// teapotXu add end for Folder in Mainmenu

	public boolean scrollTo(int destPage) {
		// scrollxScale = 0;
		// Log.d("launcher", "eee NPAGE scrollTo:"+destPage);
		if (destPage < 0 || destPage > view_list.size() - 1)
			return false;

		ScrollDestPage = destPage;
		ScrollstartPage = page_index;

		if (page_index == destPage)
			return false;
		indicatorView.setAlpha(1.0f);
		if (indicatorView.getIndicatorTween() != null
				&& !indicatorView.getIndicatorTween().isFinished()) {
			indicatorView.getIndicatorTween().free();
			indicatorView.setIndicatorTween(null);
		}
		if (page_index > destPage) {
			scrollDire = -1;
			xScale = 0.0001f;
		} else {
			scrollDire = 1;
			xScale = -0.0001f;
		}
		// Log.d("launcher",
		// "currentPage scrollTo isManualScrollTo="+isManualScrollTo);
		isManualScrollTo = true;

		stopAutoEffect();
		startAutoEffectMini();

		// indicatorView.stopTween();
		// indicatorView.color.a = 1;
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 0.0f, 0, 0).delay(1f);

		return true;
	}

	// ViewGroup3D lastCurView = null;

	private void updateEffectMini() {
		ViewGroup3D next_view;
		int type = APageEase.COOLTOUCH_EFFECT_DEFAULT;

		if (view_list.size() == 0)
			return;
		int cur_page = ScrollstartPage;
		// Log.v("npg",
		// "xScale="+xScale+" ScrollstartPage="+ScrollstartPage+" cur_page="+cur_page);
		if (cur_page < 0 || cur_page >= view_list.size())
			return;

		if (!moving) {
			initView();
			moving = true;
			for (View3D i : view_list) {
				if (i instanceof GridView3D) {
					for (int j = 0; j < ((GridView3D) i).getChildCount(); j++) {
						View3D icon = ((GridView3D) i).getChildAt(j);
						icon.setTag(new Vector2(icon.getX(), icon.getY()));
					}
				}
			}
		}

		float tempxScale = xScale - (int) xScale;

		// scrollxScale = xScale;

		ViewGroup3D cur_view = (ViewGroup3D) view_list.get(cur_page);

		// if (lastCurView != null && lastCurView != cur_view)
		// lastCurView.hide();

		int next_page = ScrollDestPage;
		if (next_page >= 0 && next_page < view_list.size())
			next_view = (ViewGroup3D) view_list.get(next_page);
		else
			next_view = cur_view;

		// if(this.random==false && this.mType==0)
		// {
		// APageEase.setStandard(true);
		// }
		// else
		// {
		// APageEase.setStandard(false);
		// }
		// Log.d("launcher", "tempxScale="+tempxScale);
		if (tempxScale > 0)
			APageEase
					.updateEffect(next_view, cur_view, tempxScale - 1, 0, type);
		else if (tempxScale < 0)
			APageEase.updateEffect(cur_view, next_view, tempxScale, 0, type);

		// lastCurView = cur_view;
		// scrollCurPage = cur_page;
		// Log.d("launcher", "cur,next="+cur_page+","+next_page);
		for (int i = 0; i < view_list.size(); i++) {
			if (i != cur_page && i != next_page)
				view_list.get(i).hide();
			// Log.d("launcher", "i:"+view_list.get(i).visible);
		}

	}

	void startAutoEffectMini() {
		int totalOffset = 1;// (ScrollDestPage - ScrollstartPage) * scrollDire;

		float duration = DefaultLayout.page_tween_time;// + totalOffset * 1 / 8;

		mVelocityX = 1000f;
		if (xScale > 0) {
			tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
					.ease(Cubic.OUT).target(1 * totalOffset, 0)
					.setCallback(this);
		} else {
			tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
					.ease(Cubic.OUT).target(-1 * totalOffset, 0)
					.setCallback(this);
		}

		mVelocityX = 0;
		tween.start(View3DTweenAccessor.manager);
	}

	@Override
	public void setPosition(float x, float y) {
		// TODO Auto-generated method stub

		if (isManualScrollTo) {
			// Log.d("launcher", "setPosition 1");
			this.xScale = x;
			this.yScale = y;
			updateEffectMini();
			onDegreeChanged();
		} else {
			// Log.d("launcher", "setPosition 2");
			setDegree(x, y);
			updateEffect();
		}

	}

	public void startAutoEffect() {
		float duration = DefaultLayout.page_tween_time;
		// Log.d("launcher",
		// "currentPage startAutoEffect isManualScrollTo="+isManualScrollTo);
		TweenEquation easeEquation = Quint.OUT;
		// teapotXu_20130316 add start:
		if (DefaultLayout.external_applist_page_effect == true) {
			if (mTypelist.get(mType) == APageEase.COOLTOUCH_EFFECT_ELASTICITY) {
				// 弹性特效 才使用如下的动画方式
				APageEase.setTouchUpAnimEffectStatus(true);
				if (xScale > 0) {
					APageEase.saveDegreeInfoWhnTouchUp(xScale - 1);
				} else {
					APageEase.saveDegreeInfoWhnTouchUp(xScale);
				}
				easeEquation = Bounce.OUT;
				duration = duration + 0.2f;
			}
		}

		// teapotXu_20130316: add end

		isManualScrollTo = false;

		if (xScale == 0 && mVelocityX == 0)
			return;
		if (xScale + mVelocityX / 1000 > 0.5) {
			// speed = 2.0f - (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;
			// teapotXu_20130316 add start: adding new effect
			if (DefaultLayout.external_applist_page_effect == true) {
				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
						.ease(easeEquation).target(1, 0).setCallback(this);
			} else {
				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
						.ease(Quint.OUT).target(1, 0).setCallback(this);
			}
			// tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			// .ease(Quint.OUT).target(1, 0).setCallback(this);
			// teapotXu_20130316 add end

			/************************ added by zhenNan.ye begin ***************************/
			if (DefaultLayout.enable_particle) {
				if (ParticleManager.particleManagerEnable) {
					startParticle(ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT,
							0, 0);
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		} else if (xScale + mVelocityX / 1000 < -0.5) {
			// speed = 2.0f + (xScale + mVelocityX / 5000);
			// speed = speed < 0.5f ? 0.5f : speed;

			// teapotXu_20130316 add start: adding new effect
			if (DefaultLayout.external_applist_page_effect == true) {
				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
						.ease(easeEquation).target(-1, 0).setCallback(this);
			} else {
				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
						.ease(Quint.OUT).target(-1, 0).setCallback(this);
			}
			// tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			// .ease(Quint.OUT).target(-1, 0).setCallback(this);
			// teapotXu_20130316 add end

			/************************ added by zhenNan.ye begin ***************************/
			if (DefaultLayout.enable_particle) {
				if (ParticleManager.particleManagerEnable) {
					startParticle(
							ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT,
							Gdx.graphics.getWidth(), 0);
				}
			}
			/************************ added by zhenNan.ye end ***************************/
		} else {
			// speed = 0.5f + Math.abs((xScale + mVelocityX / 1000));
			// speed = speed > 2.0f ? 2.0f : speed;
			// teapotXu_20130316 add start: adding new effect
			if (DefaultLayout.external_applist_page_effect == true) {
				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
						.ease(easeEquation).target(0, 0).setCallback(this);
			} else {
				tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
						.ease(Quint.OUT).target(0, 0).setCallback(this);

			}
			// tween = Tween.to(this, View3DTweenAccessor.POS_XY, duration)
			// .ease(Quint.OUT).target(0, 0).setCallback(this);
			// teapotXu_20130316 add end
		}

		mVelocityX = 0;
		tween.start(View3DTweenAccessor.manager);
	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		if (view_list.size() <= 1)
			return super.fling(velocityX, velocityY);
		mVelocityX = velocityX;
		return super.fling(velocityX, velocityY);
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle) {
			if (ParticleManager.particleManagerEnable) {
				last_x = x;
				last_y = y;
			}
		}
		/************************ added by zhenNan.ye end ***************************/

		canScroll = true;
		// Log.d("launcher", "eee NPAGE onTouchDown:"+x);
		if (view_list.size() <= 1)
			return super.onTouchDown(x, y, pointer);
		if (isManualScrollTo) {
			return true;
		}
		mVelocityX = 0;

		if (xScale > 0.5) {
			page_index = preIndex();
			setDegree(xScale - 1f);
			changeEffect();
		}
		if (xScale < -0.5) {
			page_index = nextIndex();
			setDegree(xScale + 1f);
			changeEffect();
		}
		stopAutoEffect();
		isManualScrollTo = false;
		if (indicatorView != null) // wanghongjian add //enable_DefaultScene
			indicatorView.stopTween();
		boolean res = super.onTouchDown(x, y, pointer);
		if (pointer == 0) {
			Log.i("focus", "npagebase");
			requestFocus();
		}
		// Color c = indicatorView.getColor();
		// indicatorView.setColor(c.r, c.g, c.b, 0);
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.2f, 1.0f, 0, 0);
		return res;
	}

	@Override
	public boolean multiTouch2(Vector2 initialFirstPointer,
			Vector2 initialSecondPointer, Vector2 firstPointer,
			Vector2 secondPointer) {
		// TODO Auto-generated method stub
		// Log.d(
		// "test12345","NPageBase Math.abs(xScale)="+Math.abs(xScale)+" moving="+moving);
		if (Math.abs(xScale) < 0.00005f) {
			if (moving)
				initView();
			return super.multiTouch2(initialFirstPointer, initialSecondPointer,
					firstPointer, secondPointer);
		} else
			return true;

	}

	@Override
	public boolean onClick(float x, float y) {
		// Log.d("launcher", "eee NPAGE onClick:"+x);
		// TODO Auto-generated method stub
		if (Math.abs(xScale) < 0.01f) {
			if (moving) {
				initView();
			}
			return super.onClick(x, y);
		}
		return true;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		// TODO Auto-generated method stub
		if (Math.abs(xScale) < 0.01f) {
			if (moving) {
				initView();
			}
			return super.onLongClick(x, y);
		}
		return true;
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {

		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle) {
			if (ParticleManager.particleManagerEnable) {
				stopParticle(ParticleManager.PARTICLE_TYPE_NAME_EDGE_LEFT);
				stopParticle(ParticleManager.PARTICLE_TYPE_NAME_EDGE_RIGHT);
				stopParticle(ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING);
			}
		}
		/************************ added by zhenNan.ye end ***************************/

		canScroll = false;
		// Log.d("launcher", "eee NPAGE onTouchUp:"+x);
		if (view_list.size() == 0)
			return super.onTouchUp(x, y, pointer);
		if (isManualScrollTo && xScale != 0) {
			return true;
		}
		if (view_list.size() == 1)
			return super.onTouchUp(x, y, pointer);
		if (indicatorView != null) // wanghongjian add //enable_DefaultScene
			indicatorView.stopTween();
		// Color c = indicatorView.getColor();
		// indicatorView.setColor(c.r, c.g, c.b, 1);
		// indicatorView.startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
		// 0.5f, 0.0f, 0, 0).delay(1f);
		startAutoEffect();
		releaseFocus();
		/* if workspace moving,not distribute TouchUp to children by zfshi */
		if (moving) {
			return false;
		}
		/* added by zfshi ended */
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		if (!canScroll)
			return false;
		// Log.d("launcher", "eee NPAGE scroll:"+x);
		if (view_list.size() <= 1)
			return super.scroll(x, y, deltaX, deltaY);
		if (isManualScrollTo) {
			return true;
		}
		if (!moving && super.scroll(x, y, deltaX, deltaY)) {
			// Log.d("launcher", "eee NPAGE scroll222:"+x);
			return true;
		}

		// setDegree(xScale - (-deltaX) / this.width);

		if (DefaultLayout.enable_DesktopIndicatorScroll
				&& Root3D.scroll_indicator) {
			return false;
		}

		float yAmplify = deltaY * 1.3f;
		setDegree(xScale - (-deltaX) / this.width, yScale + (-yAmplify)
				/ this.height);

		// teapotXu_20130319: add start
		if (DefaultLayout.external_applist_page_effect == true) {
			APageEase.setTouchUpAnimEffectStatus(false);
		}
		// teapotXu_20130319: add end

		updateEffect();

		/************************ added by zhenNan.ye begin ***************************/
		if (DefaultLayout.enable_particle) {
			if (ParticleManager.particleManagerEnable) {
				if ((Math.abs(x - last_x) > 10) || (Math.abs(y - last_y) > 10)) {
					updateParticle(
							ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING,
							x, y);

					last_x = x;
					last_y = y;
				} else {
					pauseParticle(ParticleManager.PARTICLE_TYPE_NAME_FINGER_MOVING);
				}
			}
		}
		/************************ added by zhenNan.ye end ***************************/

		if (moving)
			return true;
		return super.scroll(x, y, deltaX, deltaY);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		super.draw(batch, parentAlpha);
		if (drawIndicator)
			if (indicatorView != null) {
				indicatorView.draw(batch, parentAlpha);
			}

		/************************ added by zhenNan.ye begin *************************/
		drawParticleEffect(batch);
		/************************ added by zhenNan.ye end *************************/
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		initView();
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
		this.stopAutoEffect();
		// setDegree(0f);
		setDegree(0f, 0f);
		mVelocityX = 0;
		initView();
	}

	public int getPageNum() {
		return view_list.size();
	}

	public float getTotalOffset() {

		float ret = 0;

		int totalPage = getPageNum();
		// int tmp = (totalPage-1);
		// Log.v("jbc", "getTotalOffset xScale="+xScale+", ret="+ret);

		if (isManualScrollTo) {

			int destPage = iLoongLauncher.getInstance().getD3dListener()
					.getWorkspace3D().getHomePage();
			if (CellLayout3D.keyPadInvoked
					&& DefaultLayout.keypad_event_of_focus) {
				destPage = CellLayout3D.nextPageIndex;
			}
			if (page_index != destPage)
				ret = 1.0f
						/ (totalPage - 1)
						* (page_index * (1 - Math.abs(xScale)) + destPage
								* Math.abs(xScale));
		} else {
			if (page_index == 0 && xScale > 0) {
				ret = (((float) totalPage - 1) / (totalPage - 1)) * xScale;
			} else if (page_index == totalPage - 1 && xScale < 0) {
				ret = ((float) totalPage - 1) / (totalPage - 1)
						+ (((float) totalPage - 1) / (totalPage - 1)) * xScale;
			} else
				ret = (page_index - xScale) / ((totalPage - 1));
		}
		// Log.v("jbc", "getTotalOffset isManualScrollTo="+isManualScrollTo);
		// Log.v("jbc", "getTotalOffset ret="+ret);
		if (ret < 0) {
			ret = 0;
		} else if (ret > (totalPage - 1.0f) / (totalPage - 1)) {
			ret = (totalPage - 1.0f) / (totalPage - 1);
		}
		return ret;
	}

	public ArrayList<View3D> getViewList() {
		return this.view_list;
	}

	public View3D getCurrentView() {
		if (view_list.size() <= page_index)
			return null;
		return view_list.get(page_index);
	}

	public int getCurrentPage() {
		return page_index;
	}

	public void setCurrentPage(int index) {
		if (index < 0)
			index = 0;
		else if (index >= view_list.size())
			index = view_list.size() - 1;
		page_index = index;
		// setDegree(0f);
		initView();
		setDegree(0f, 0f);
	}

	public void addScrollListener(PageScrollListener l) {
		scrollListeners.add(l);
	}

	// xiatian add start //Widget adaptation "com.android.gallery3d"
	public void removeScrollListener(PageScrollListener l) {
		if (scrollListeners.contains(l))
			scrollListeners.remove(l);
	}

	// xiatian add end

	protected void finishAutoEffect() {
		// ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
		// ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
		// ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());
		// initData(cur_view);
		// initData(pre_view);
		// initData(next_view);

		// teapotXu_20130325 add start : for effect
		if (DefaultLayout.external_applist_page_effect == true) {
			ViewGroup3D cur_view = (ViewGroup3D) view_list.get(page_index);
			ViewGroup3D pre_view = (ViewGroup3D) view_list.get(preIndex());
			ViewGroup3D next_view = (ViewGroup3D) view_list.get(nextIndex());
			if (cur_view.getChildrenDrawOrder() == true) {
				cur_view.setChildrenDrawOrder(false);
			}
			if (pre_view.getChildrenDrawOrder() == true) {
				pre_view.setChildrenDrawOrder(false);
			}
			if (next_view.getChildrenDrawOrder() == true) {
				next_view.setChildrenDrawOrder(false);
			}
		}
		// teapotXu_20130325 add end : for effect
	}

	public void recoverPageSequence() {
		for (int i = 0; i < view_list.size() && i < children.size(); i++) {
			children.set(i, view_list.get(i));
		}
	}

	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (type == TweenCallback.COMPLETE && source == tween) {
			if (isManualScrollTo) {
				setCurrentPage(ScrollDestPage);
				isManualScrollTo = false;
			} else {
				if (xScale <= -1f) {
					setCurrentPage(nextIndex());
				}
				if (xScale >= 1f) {
					setCurrentPage(preIndex());
				}
			}
			initView();
			tween = null;
			finishAutoEffect();
			recoverPageSequence();
		}
		super.onEvent(type, source);
	}

	/************************ added by zhenNan.ye begin *************************/
	private void drawParticleEffect(SpriteBatch batch) {
		if (DefaultLayout.enable_particle) {
			if (ParticleManager.particleManagerEnable) {
				drawParticle(batch);
			}
		}
	}
	/************************ added by zhenNan.ye end ***************************/

	public class IndicatorView extends View3D {

		// NinePatch focus, unfocus;
		private TextureRegion unselectedIndicator = null;
		private TextureRegion selectedIndicator = null;
		private NinePatch nineIndicator = null;
		private TextureRegion[] indicatorNumber;
		public float indicatorSize = (float) R3D.page_indicator_size;
		public final int indicatorFocusW = R3D.page_indicator_focus_w;
		public final int indicatorNormalW = R3D.page_indicator_normal_w;
		public final int indicatorStyle = R3D.page_indicator_style;
		public final int indicatorTotalSize = R3D.page_indicator_total_size;

		public static final int INDICATOR_STYLE_ANDROID4 = 0;
		public static final int INDICATOR_STYLE_S3 = 1;
		public static final int INDICATOR_STYLE_S2 = 2;

		// xiatian add start //add new page_indicator_style
		public static final int INDICATOR_STYLE_COCO_AND_ANDROID4 = 3;
		private TextureRegion unselectedIndicatorXian = null;
		private TextureRegion selectedIndicatorXian = null;
		public float indicatorSizeXian = (float) R3D.page_indicator_size;
		// xiatian add end
		private Tween myTween;
		private Tween indicatorTween;
		private float indicatorAlpha = 0;

		public IndicatorView(String name) {
			super(name);
			// this.focus = new NinePatch(focus,3,3,3,3);
			// this.unfocus = new NinePatch(unfocus,3,3,3,3);
			// this.height = 5;
			// this.width = Utils3D.getScreenWidth();
			setSize(R3D.getInteger("page_indicator_width") == 0 ? Utils3D.getScreenWidth()
					: R3D.getInteger("page_indicator_width"),
					R3D.getInteger("page_indicator_height"));
			setPosition((Utils3D.getScreenWidth() - this.width) / 2,
					R3D.applist_indicator_y + NPageBase.this.y);

			if (indicatorStyle == INDICATOR_STYLE_S3) {
				if (nineIndicator == null) {
					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap(
							"theme/pack_source/default_indicator_current.png");
					Texture t = new BitmapTexture(bmIndicator);
					// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					// TextureRegion region =
					// R3D.findRegion("default_indicator_current");
					// region.getTexture().setFilter(TextureFilter.Linear,
					// TextureFilter.Linear);
					nineIndicator = new NinePatch(new TextureRegion(t), 6, 6,
							6, 6);
					if (!iLoongLauncher.releaseTexture)
						bmIndicator.recycle();
					// bmIndicator.recycle();
					// nineIndicator = new
					// NinePatch(R3D.getTextureRegion("default_indicator_current"),6,6,6,6);
				}
			} else if (indicatorStyle == INDICATOR_STYLE_S2) {
				if (selectedIndicator == null) {
					Bitmap bmIndicator = ThemeManager
							.getInstance()
							.getBitmap(
									"theme/pack_source/default_indicator_current_s2.png");
					Texture t = new BitmapTexture(bmIndicator);
					selectedIndicator = new TextureRegion(t);
					if (!iLoongLauncher.releaseTexture)
						bmIndicator.recycle();
					indicatorNumber = new TextureRegion[9];
					for (int i = 0; i < 9; i++) {
						bmIndicator = ThemeManager.getInstance().getBitmap(
								"theme/pack_source/indicator_num_" + (i + 1)
										+ ".png");
						// bmIndicator =
						// BitmapFactory.decodeStream(file.read());
						t = new BitmapTexture(bmIndicator);
						indicatorNumber[i] = new TextureRegion(t);
						if (!iLoongLauncher.releaseTexture)
							bmIndicator.recycle();
					}
				}
			} else {
				if (unselectedIndicator == null) {
					// unselectedIndicator =
					// R3D.getTextureRegion("default_indicator");

					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap(
							"theme/pack_source/default_indicator.png");
					Texture t = new BitmapTexture(bmIndicator);
					// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					// TextureRegion region =
					// R3D.findRegion("default_indicator");
					// region.getTexture().setFilter(TextureFilter.Linear,
					// TextureFilter.Linear);
					unselectedIndicator = new TextureRegion(t);
					if (!iLoongLauncher.releaseTexture)
						bmIndicator.recycle();
				}
				if (selectedIndicator == null) {
					// selectedIndicator =
					// R3D.getTextureRegion("default_indicator_current");

					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap(
							"theme/pack_source/default_indicator_current.png");
					Texture t = new BitmapTexture(bmIndicator);
					// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					// TextureRegion region =
					// R3D.findRegion("default_indicator_current");
					// region.getTexture().setFilter(TextureFilter.Linear,
					// TextureFilter.Linear);
					selectedIndicator = new TextureRegion(t);
					if (!iLoongLauncher.releaseTexture)
						bmIndicator.recycle();
				}

				// xiatian add start //add new page_indicator_style
				if (unselectedIndicatorXian == null) {
					// unselectedIndicator =
					// R3D.getTextureRegion("default_indicator");

					Bitmap bmIndicator = ThemeManager.getInstance().getBitmap(
							"theme/pack_source/default_indicator_xian.png");
					Texture t = new BitmapTexture(bmIndicator);
					// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					// TextureRegion region =
					// R3D.findRegion("default_indicator");
					// region.getTexture().setFilter(TextureFilter.Linear,
					// TextureFilter.Linear);
					unselectedIndicatorXian = new TextureRegion(t);
					if (!iLoongLauncher.releaseTexture)
						bmIndicator.recycle();
				}
				if (selectedIndicatorXian == null) {
					// selectedIndicator =
					// R3D.getTextureRegion("default_indicator_current");

					Bitmap bmIndicator = ThemeManager
							.getInstance()
							.getBitmap(
									"theme/pack_source/default_indicator_current_xian.png");
					Texture t = new BitmapTexture(bmIndicator);
					// t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					// TextureRegion region =
					// R3D.findRegion("default_indicator_current");
					// region.getTexture().setFilter(TextureFilter.Linear,
					// TextureFilter.Linear);
					selectedIndicatorXian = new TextureRegion(t);
					if (!iLoongLauncher.releaseTexture)
						bmIndicator.recycle();
				}
				// xiatian add end

			}
		}

		public void show() {
			// if ( this.touchable==true)
			// {
			// return;
			// }
			super.show();
			indicatorAlpha = 1;
			stopTween();
			Log.v("IndicatorView", "TweenStart show");
			myTween = startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
					0.4f, 1, 0, 0).setCallback(this);
		}

		public void showEx() {
			// if ( this.touchable==true)
			// {
			// return;
			// }
			super.show();
			indicatorAlpha = 1;
			stopTween();
			Log.v("IndicatorView", "TweenStart show");
			myTween = startTween(View3DTweenAccessor.OPACITY, Linear.INOUT,
					0.0f, 1, 0, 0).setCallback(this);
		}

		@Override
		public void setUser(float value) {
			// TODO Auto-generated method stub
			indicatorAlpha = value;
		}

		@Override
		public float getUser() {
			// TODO Auto-generated method stub
			return indicatorAlpha;
		}

		public void setAlpha(float value) {
			indicatorAlpha = value;
		}

		public void setIndicatorTween(Tween tween) {
			indicatorTween = tween;
		}

		public Tween getIndicatorTween() {
			return indicatorTween;
		}

		public void finishAutoEffect() {
			// TODO Auto-generated method stub
			indicatorAlpha = 1;
			if (indicatorTween != null && !indicatorTween.isFinished()) {
				indicatorTween.free();
				indicatorTween = null;
			}
			indicatorTween = startTween(View3DTweenAccessor.USER, Cubic.OUT,
					INDICATOR_FADE_TWEEN_DURATION, 0, 0, 0).setCallback(this);
		}

		@Override
		public void onEvent(int type, BaseTween source) {
			// TODO Auto-generated method stub
			if (type == TweenCallback.COMPLETE && source == myTween) {
				myTween = null;
				this.color.a = 1;
				super.show();
				finishAutoEffect();
				return;
			}
			if (type == TweenCallback.COMPLETE && source == indicatorTween) {
				indicatorTween = null;
				indicatorAlpha = 0;
				return;
			}
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (!visible) {
				return;
			}
			this.y += indicatorPaddingBottom;
			batch.setColor(color.r, color.g, color.b, color.a);
			Color old = batch.getColor();
			float oldA = old.a;
			int size = getIndicatorPageCount();
			float focusWidth = indicatorSize * size;
			int currentPage = getIndicatorPageIndex();
			int nextPage = currentPage;
			float degree = xScale;
			int signDirection = degree >= 0 ? 1 : -1;
			if (degree == 0)
				nextPage = currentPage;
			else if (degree == 1)
				nextPage = currentPage == 0 ? size - 1 : currentPage - 1;
			else if (degree == -1)
				nextPage = currentPage == size - 1 ? 0 : currentPage + 1;
			else if (currentPage == 0 && degree > 0)
				nextPage = size - 1;
			else if (currentPage == size - 1 && degree < 0)
				nextPage = 0;
			else
				nextPage = currentPage - (int) (degree + 1.0 * signDirection);

			float normalH, focusH, normalY, focusY;
			float startX = this.x + (this.width - focusWidth) / 2.0f;
			// Log.v("jbc","isManualScrollToab="+isManualScrollTo);

			switch (indicatorStyle) {
			case INDICATOR_STYLE_ANDROID4:
				if (selectedIndicator != null && unselectedIndicator != null) {
					focusWidth = indicatorTotalSize;
					indicatorSize = focusWidth / size;
					startX = this.x + (this.width - focusWidth) / 2.0f;

					focusH = normalH = indicatorNormalW;
					normalY = 0;// this.y + (this.height - normalH)/2.0f;
					focusY = 0;// this.y + (this.height - focusH)/2.0f;

					float focus_offset_x = 0.0f;
					// old.a = oldA;
					// batch.setColor(old);
					// batch.draw(unselectedIndicator, startX, (int)normalY,
					// focusWidth, normalH);

					focus_offset_x = -indicatorSize * degree;
					old.a = oldA * indicatorAlpha;
					batch.setColor(old.r, old.g, old.b, old.a);
					if (!isManualScrollTo) {
						if (currentPage * indicatorSize + focus_offset_x < 0) {
							batch.draw(selectedIndicator, startX + currentPage
									* indicatorSize, (int) focusY,
									indicatorSize + focus_offset_x, focusH);
							batch.draw(selectedIndicator, this.width - startX
									+ focus_offset_x, (int) focusY,
									-focus_offset_x, focusH);
						} else if (startX + currentPage * indicatorSize
								+ focus_offset_x > this.width - startX
								- indicatorSize) {
							batch.draw(selectedIndicator, startX + currentPage
									* indicatorSize + focus_offset_x,
									(int) focusY, indicatorSize
											- focus_offset_x, focusH);
							batch.draw(selectedIndicator, startX, (int) focusY,
									focus_offset_x, focusH);
						} else {
							batch.draw(selectedIndicator, startX + currentPage
									* indicatorSize + focus_offset_x,
									(int) focusY, indicatorSize, focusH);
						}
					}
				}
				break;
			case INDICATOR_STYLE_S3:
				if (nineIndicator != null) {
					focusH = normalH = indicatorNormalW;
					normalY = this.y + (this.height - normalH) / 2.0f;
					focusY = this.y + (this.height - focusH) / 2.0f;

					for (int i = 0; i < size; i++) {
						if (!isManualScrollTo) {
							if (i == currentPage) {
								old.a = oldA;
								old.a *= 1 - Math.abs(degree * 0.5f);
								batch.setColor(old.r, old.g, old.b, old.a);
								float w = indicatorNormalW
										+ (1 - Math.abs(degree))
										* (indicatorFocusW - indicatorNormalW);
								float offset_x = (indicatorSize - w) / 2.0f;
								nineIndicator.draw(batch, startX + i
										* indicatorSize + offset_x, focusY, w,
										focusH);
							} else if (i == nextPage) {
								old.a = oldA;
								old.a *= Math.abs(degree * 0.5f) + 0.5f;
								batch.setColor(old.r, old.g, old.b, old.a);
								float w = indicatorNormalW + (Math.abs(degree))
										* (indicatorFocusW - indicatorNormalW);
								float offset_x = (indicatorSize - w) / 2.0f;
								nineIndicator.draw(batch, startX + i
										* indicatorSize + offset_x, focusY, w,
										focusH);
							} else {
								old.a = oldA * 0.5f;
								batch.setColor(old.r, old.g, old.b, old.a);
								float offset_x = (indicatorSize - indicatorNormalW) / 2.0f;
								nineIndicator.draw(batch, startX + i
										* indicatorSize + offset_x, normalY,
										indicatorNormalW, normalH);
							}
						} else {
							old.a = oldA * 0.5f;
							batch.setColor(old.r, old.g, old.b, old.a);
							float offset_x = (indicatorSize - indicatorNormalW) / 2.0f;
							nineIndicator.draw(batch, startX + i
									* indicatorSize + offset_x, normalY,
									indicatorNormalW, normalH);
						}
					}
				}
				break;
			case INDICATOR_STYLE_S2:
				if (selectedIndicator != null) {
					normalH = indicatorNormalW;
					focusH = indicatorFocusW;
					normalY = this.y + (this.height - normalH) / 2.0f;

					old.a = oldA;
					batch.setColor(old.r, old.g, old.b, old.a);

					for (int i = 0; i < size; i++) {
						if (!isManualScrollTo) {
							if (i == currentPage) {
								float w = indicatorNormalW
										+ (1 - Math.abs(degree))
										* (indicatorFocusW - indicatorNormalW);
								float offset_x = (indicatorSize - w) / 2.0f;
								focusY = this.y + (this.height - w) / 2.0f;
								batch.draw(selectedIndicator, startX + i
										* indicatorSize + offset_x, focusY, w,
										w);
								batch.draw(indicatorNumber[i], startX + i
										* indicatorSize + offset_x + w / 5,
										focusY + w / 5, w * 3 / 5, w * 3 / 5);
							} else if (i == nextPage) {
								float w = indicatorNormalW + (Math.abs(degree))
										* (indicatorFocusW - indicatorNormalW);
								float offset_x = (indicatorSize - w) / 2.0f;
								focusY = this.y + (this.height - w) / 2.0f;
								batch.draw(selectedIndicator, startX + i
										* indicatorSize + offset_x, focusY, w,
										w);
								batch.draw(indicatorNumber[i], startX + i
										* indicatorSize + offset_x + w / 5,
										focusY + w / 5, w * 3 / 5, w * 3 / 5);
							} else {
								float offset_x = (indicatorSize - indicatorNormalW) / 2.0f;
								batch.draw(selectedIndicator, startX + i
										* indicatorSize + offset_x, normalY,
										indicatorNormalW, normalH);
							}
						} else {
							float offset_x = (indicatorSize - indicatorNormalW) / 2.0f;
							batch.draw(selectedIndicator, startX + i
									* indicatorSize + offset_x, normalY,
									indicatorNormalW, normalH);
						}
					}
				}
				break;

			// xiatian add start //add new page_indicator_style
			case INDICATOR_STYLE_COCO_AND_ANDROID4:
				if (size * indicatorSize >= this.width) {
					// ANDROID4
					if (selectedIndicatorXian != null
							&& unselectedIndicatorXian != null) {
						// Log.v("xiatian408830131","ANDROID4  ----  size*indicatorSize="+(size*indicatorSize));
						// Log.v("xiatian408830131","ANDROID4  ----  this.width="+(this.width));
						focusWidth = indicatorTotalSize;
						indicatorSizeXian = focusWidth / size;
						startX = this.x + (this.width - focusWidth) / 2.0f;

						focusH = normalH = indicatorNormalW;
						normalY = 0;// this.y + (this.height - normalH)/2.0f;
						focusY = 0;// this.y + (this.height - focusH)/2.0f;

						float focus_offset_x = 0.0f;
						// old.a = oldA;
						// batch.setColor(old);
						// batch.draw(unselectedIndicator, startX, (int)normalY,
						// focusWidth, normalH);

						focus_offset_x = -indicatorSize * degree;
						old.a = oldA * indicatorAlpha;
						batch.setColor(old.r, old.g, old.b, old.a);
						if (!isManualScrollTo) {
							if (currentPage * indicatorSizeXian
									+ focus_offset_x < 0) {
								batch.draw(selectedIndicatorXian, startX
										+ currentPage * indicatorSizeXian,
										focusY, indicatorSizeXian
												+ focus_offset_x, focusH);
								batch.draw(selectedIndicatorXian, this.width
										- startX + focus_offset_x, focusY,
										-focus_offset_x, focusH);
							} else if (startX + currentPage * indicatorSizeXian
									+ focus_offset_x > this.width - startX
									- indicatorSizeXian) {
								batch.draw(selectedIndicatorXian, startX
										+ currentPage * indicatorSizeXian
										+ focus_offset_x, focusY,
										indicatorSizeXian - focus_offset_x,
										focusH);
								batch.draw(selectedIndicatorXian, startX,
										focusY, focus_offset_x, focusH);
							} else {
								batch.draw(selectedIndicatorXian, startX
										+ currentPage * indicatorSizeXian
										+ focus_offset_x, focusY,
										indicatorSizeXian, focusH);
							}
						}
					}
				} else {
					// COCO
					if (selectedIndicator != null
							&& unselectedIndicator != null) {
						// Log.v("xiatian408830131","COCO  ----  size*indicatorSize="+(size*indicatorSize));
						// Log.v("xiatian408830131","COCO  ----  this.width="+(this.width));
						normalH = indicatorNormalW;
						focusH = indicatorFocusW;
//						normalY = this.y + (this.height - normalH) / 2.0f;
//						focusY = this.y + (this.height - focusH) / 2.0f;
						normalY = this.y;
						focusY = this.y + (normalH - focusH)/2.0f;


						float normal_offset_x = (indicatorSize - indicatorNormalW) / 2.0f;
						float focus_offset_x = (indicatorSize - indicatorFocusW) / 2.0f;
						for (int i = 0; i < size; i++) {
							if (!isManualScrollTo) {
								if (i == currentPage) {
									old.a = oldA;
									old.a *= Math.abs(degree);
									batch.setColor(old.r, old.g, old.b, old.a);
								} else if (i == nextPage) {
									old.a = oldA;
									old.a *= 1 - Math.abs(degree);
									batch.setColor(old.r, old.g, old.b, old.a);
								} else {
									old.a = oldA;
									batch.setColor(old.r, old.g, old.b, old.a);
								}
							} else {
								old.a = oldA;
								batch.setColor(old.r, old.g, old.b, old.a);
							}
							batch.draw(unselectedIndicator, startX + i
									* indicatorSize + normal_offset_x, normalY,
									indicatorNormalW, normalH);

							if (i == currentPage) {
								old.a = oldA;
								old.a *= 1 - Math.abs(degree);
								batch.setColor(old.r, old.g, old.b, old.a);
								if (!isManualScrollTo)
									batch.draw(selectedIndicator, startX + i
											* indicatorSize + focus_offset_x,
											focusY, indicatorFocusW, focusH);
							} else if (i == nextPage) {
								old.a = oldA;
								old.a *= Math.abs(degree);
								batch.setColor(old.r, old.g, old.b, old.a);
								if (!isManualScrollTo)
									batch.draw(selectedIndicator, startX + i
											* indicatorSize + focus_offset_x,
											focusY, indicatorFocusW, focusH);
							}
						}
					}
				}
				break;
			// xiatian add end

			default:
				if (selectedIndicator != null && unselectedIndicator != null) {
					normalH = indicatorNormalW;
					focusH = indicatorFocusW;
					normalY = this.y + (this.height - normalH) / 2.0f;
					focusY = this.y + (this.height - focusH) / 2.0f;

					float normal_offset_x = (indicatorSize - indicatorNormalW) / 2.0f;
					float focus_offset_x = (indicatorSize - indicatorFocusW) / 2.0f;
					for (int i = 0; i < size; i++) {
						if (!isManualScrollTo) {
							if (i == currentPage) {
								old.a = oldA;
								old.a *= Math.abs(degree);
								batch.setColor(old.r, old.g, old.b, old.a);
							} else if (i == nextPage) {
								old.a = oldA;
								old.a *= 1 - Math.abs(degree);
								batch.setColor(old.r, old.g, old.b, old.a);
							} else {
								old.a = oldA;
								batch.setColor(old.r, old.g, old.b, old.a);
							}
						} else {
							old.a = oldA;
							batch.setColor(old.r, old.g, old.b, old.a);
						}
						batch.draw(unselectedIndicator, startX + i
								* indicatorSize + normal_offset_x, normalY,
								indicatorNormalW, normalH);

						if (i == currentPage) {
							old.a = oldA;
							old.a *= 1 - Math.abs(degree);
							batch.setColor(old.r, old.g, old.b, old.a);
							if (!isManualScrollTo)
								batch.draw(selectedIndicator, startX + i
										* indicatorSize + focus_offset_x,
										focusY, indicatorFocusW, focusH);
						} else if (i == nextPage) {
							old.a = oldA;
							old.a *= Math.abs(degree);
							batch.setColor(old.r, old.g, old.b, old.a);
							if (!isManualScrollTo)
								batch.draw(selectedIndicator, startX + i
										* indicatorSize + focus_offset_x,
										focusY, indicatorFocusW, focusH);
						}
					}
				}
				break;
			}
			old.a = oldA;
			batch.setColor(old.r, old.g, old.b, old.a);
			this.y -= indicatorPaddingBottom;
		}
	}

}
