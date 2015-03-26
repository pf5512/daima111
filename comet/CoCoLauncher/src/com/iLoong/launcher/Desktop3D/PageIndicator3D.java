package com.iLoong.launcher.Desktop3D;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.iLoong.launcher.Desktop3D.Log;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.coco.launcher.R;
import com.iLoong.launcher.SetupMenu.SetupMenu;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.UI3DEngine.ImageView3D;
import com.iLoong.launcher.UI3DEngine.Texture3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.cling.ClingManager;
import com.iLoong.launcher.desktop.iLoongLauncher;
import com.iLoong.launcher.theme.ThemeManager;
import com.iLoong.launcher.tween.View3DTweenAccessor;

public class PageIndicator3D extends View3D implements PageScrollListener,
		ClingManager.ClingTarget {

	private View3D scrollAnimView;
	private View3D indicatorAnimView;
	private int currentPage = -1;
	private int targetPage;
	private int pageNum;
	private float degree;
	private Tween myTween;
	private Tween s4indicatorClickTween;
	private static final int hide_indicator=0;
	private static final int show_indicator=1;
	private Tween indicatorTween;
	private float indicatorAlpha = 0;
	private static float INDICATOR_FADE_TWEEN_DURATION = 1.0f; 
//	private TextureRegion indicatorBg;
	private TextureRegion unselectedIndicator = null;
	private TextureRegion selectedIndicator = null;
	private NinePatch nineIndicator = null;
	private TextureRegion []indicatorNumber;
	private NinePatch bgIndicator_s4 = null;
	private TextureRegion scrollIndicator_s4 = null;
	private TextureRegion []indicatorNumber_s4;
//	private NinePatch bg;
	
//	public static float pageIndicatorWidth;
//	public static float pageIndicatorHeight;
//	private float indicatorWidth;
//	private float indicatorHeight;
//	private float originX;
//	private float originY;
//	private float radius;
//	private float indicatorStartDegree;
//	private float indicatorEndDegree;
	public int clingR;
	public int clingX;
	public int clingY;
	//private boolean hasDown = false;
	private long downTime = 0;
	private float downX = 0;
	private float downY = 0;
	private int clingState = ClingManager.CLING_STATE_WAIT;
	//public double pointPage = -1;
	public int pageMode = MODE_NORMAL;
	public static boolean animating = false;
	public static final float NORMAL_SCALE = 0.6f;
	public static final float ACTIVATE_SCALE = 0.75f;
	public static final int CLICK_TIME = 500;
	public static final int CLICK_MOVE = 40;
	public static final int PAGE_INDICATOR_CLICK = 0;
	public static final int PAGE_INDICATOR_UP = 1;
	public static final int PAGE_INDICATOR_DROP_OVER = 2;
	public static final int PAGE_INDICATOR_SCROLL = 3;
	//public static final int PAGE_INDICATOR_CLICK = 4;
	
	public static final int MODE_ACTIVATE = 0;
	public static final int MODE_NORMAL = 1;
	public static final int PAGE_MODE_EDIT = 2;
	
	public float indicatorSize = (float)R3D.page_indicator_size; 
	public final int indicatorFocusW = R3D.page_indicator_focus_w; 
	public final int indicatorNormalW = R3D.page_indicator_normal_w; 
	public final int indicatorStyle = R3D.page_indicator_style;
	public final int indicatorTotalSize = R3D.page_indicator_total_size;
	
	public final int s4_page_indicator_bg_height = R3D.s4_page_indicator_bg_height;
	public final int s4_page_indicator_scroll_width = R3D.s4_page_indicator_scroll_width;
	public final int s4_page_indicator_scroll_height = R3D.s4_page_indicator_scroll_height;
	public final int s4_page_indicator_number_bg_size = R3D.s4_page_indicator_number_bg_size;
	public static float scroll_degree = 0;
	
	public static final int INDICATOR_STYLE_ANDROID4 = 0;
	public static final int INDICATOR_STYLE_S3 = 1;
	public static final int INDICATOR_STYLE_S2 = 2;
//	public static final int INDICATOR_STYLE_S4 = 3;
	public PageIndicator3D(String name) {
		super(name);
		if (indicatorStyle == INDICATOR_STYLE_S3){
			if(nineIndicator==null) {
				Bitmap bmIndicator= ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator_current.png");
				Texture t = new BitmapTexture(bmIndicator);
				//t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				//TextureRegion region = R3D.findRegion("default_indicator_current");
				//region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				nineIndicator = new NinePatch(new TextureRegion(t),6,6,6,6);
				if(!iLoongLauncher.releaseTexture)bmIndicator.recycle();
				//bmIndicator.recycle();
				//nineIndicator = new NinePatch(R3D.getTextureRegion("default_indicator_current"),6,6,6,6);
			}
		} else if (indicatorStyle == INDICATOR_STYLE_S2) {
			if (selectedIndicator==null) {
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator_current_s2.png");
				Texture t = new BitmapTexture(bmIndicator);
				selectedIndicator = new TextureRegion(t);
				if(!iLoongLauncher.releaseTexture)bmIndicator.recycle();
				indicatorNumber = new TextureRegion[9];
				for (int i=0; i<9; i++) {
					bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/indicator_num_"+(i+1)+".png");;
					t = new BitmapTexture(bmIndicator);
					indicatorNumber[i] = new TextureRegion(t);
					if(!iLoongLauncher.releaseTexture)bmIndicator.recycle();
				}
			}
		} else {
			if(unselectedIndicator==null){
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator.png");
				Texture t = new BitmapTexture(bmIndicator);
				unselectedIndicator = new TextureRegion(t);
				if(!iLoongLauncher.releaseTexture)bmIndicator.recycle();
			}
			if(selectedIndicator==null){
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/default_indicator_current.png");
				Texture t = new BitmapTexture(bmIndicator);
				selectedIndicator = new TextureRegion(t);
				if(!iLoongLauncher.releaseTexture)bmIndicator.recycle();
			}
		}
		
		if (DefaultLayout.enable_DesktopIndicatorScroll) {
			//scroll indicator
			if (bgIndicator_s4 == null) {
				Bitmap bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/bg_indicator_s4.png");
				Texture t = new BitmapTexture(bmIndicator);
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				bgIndicator_s4 = new NinePatch(new TextureRegion(t),7,7,0,0);
				bmIndicator.recycle();
				
				bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/scroll_indicator_s4.png");
				t = new BitmapTexture(bmIndicator);
				t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				scrollIndicator_s4 = new TextureRegion(t);
				bmIndicator.recycle();
				
				indicatorNumber_s4 = new TextureRegion[9];
				for(int i=0; i<9; i++) {
					bmIndicator = ThemeManager.getInstance().getBitmap("theme/pack_source/indicator_num_s4_"+(i+1)+".png");
					t = new BitmapTexture(bmIndicator);
					t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
					indicatorNumber_s4[i] = new TextureRegion(t);
					bmIndicator.recycle();
				}
			}
		}
		setSize(R3D.getInteger("page_indicator_width") == 0 ? Utils3D.getScreenWidth() : R3D.getInteger("page_indicator_width"), R3D.getInteger("page_indicator_height"));
		setPosition((Utils3D.getScreenWidth() - this.width)/2, R3D.page_indicator_y);
		
		ClingManager.getInstance().firePageIndicatorCling(this);
		indicatorAlpha = 0;
		clingX = Utils3D.getScreenWidth()/2;
		clingY = (int) (y+height/2);
		clingR = (int) (indicatorSize*2);
	}


	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (!Desktop3DListener.bSetHomepageDone) return;
		batch.setColor(color.r, color.g, color.b, color.a);
		Color old = batch.getColor();
		float oldA = old.a;		
		int size = pageNum;
		float focusWidth = indicatorSize * size;
		int nextPage = currentPage;
		int signDirection = degree >= 0 ? 1: -1;
		if (degree == 0)
			nextPage = currentPage;
		else if (degree == 1)
			nextPage = currentPage == 0? size - 1: currentPage - 1;
		else if (degree == -1)
			nextPage = currentPage == size - 1? 0: currentPage + 1;
		else if (currentPage == 0 && degree > 0)
			nextPage = size - 1;
		else if (currentPage == size - 1 && degree < 0)
			nextPage = 0;
		else
			nextPage = currentPage - (int)(degree + 1.0 * signDirection);		
		//Log.v("jbc","eee currentPage="+currentPage+" nextPage="+nextPage+" degree="+degree);
		
		float normalH, focusH, normalY, focusY;
		float startX = this.x + (this.width - focusWidth)/2.0f;
		
		if (DefaultLayout.enable_DesktopIndicatorScroll && Root3D.scroll_indicator) {
			normalY = this.y + (this.height - s4_page_indicator_bg_height)/2.0f;
			focusY = this.y + (this.height - s4_page_indicator_scroll_height)/2.0f;

			old.a = oldA;
			batch.setColor(old.r,old.g,old.b,old.a);
			bgIndicator_s4.draw(batch, (int)startX, (int)normalY, focusWidth, s4_page_indicator_bg_height);
			
			startX = startX + indicatorSize/2 - s4_page_indicator_scroll_width/2 + scroll_degree*indicatorSize;
			batch.draw(scrollIndicator_s4, (int)startX, (int)focusY, s4_page_indicator_scroll_width, s4_page_indicator_scroll_height);
			batch.draw(indicatorNumber_s4[(int)(scroll_degree+0.5f)], (this.width - s4_page_indicator_number_bg_size)/2.0f, (int)(this.y + s4_page_indicator_number_bg_size*1.5), 
					s4_page_indicator_number_bg_size, s4_page_indicator_number_bg_size);
		} else {
			switch (indicatorStyle) {
			case INDICATOR_STYLE_ANDROID4:
				if (selectedIndicator != null && unselectedIndicator != null) {
					focusWidth = indicatorTotalSize;
					indicatorSize = focusWidth/size;
					startX = this.x + (this.width - focusWidth)/2.0f;
					
					focusH = normalH = indicatorNormalW;
					normalY = this.y + (this.height - normalH)/2.0f;
					focusY = this.y + (this.height - focusH)/2.0f;
	
					float focus_offset_x = 0.0f;
					old.a = oldA;
					batch.setColor(old.r,old.g,old.b,old.a);
					batch.draw(unselectedIndicator, startX, (int)normalY, focusWidth, normalH);
					
					focus_offset_x = -indicatorSize*degree;
					old.a = oldA*indicatorAlpha;
					batch.setColor(old.r,old.g,old.b,old.a);
					if (currentPage * indicatorSize + focus_offset_x < 0) {
						batch.draw(selectedIndicator, startX + currentPage * indicatorSize, (int)focusY, indicatorSize + focus_offset_x, focusH);
						batch.draw(selectedIndicator, this.width - startX + focus_offset_x, (int)focusY, -focus_offset_x, focusH);
					} else if (startX + currentPage * indicatorSize + focus_offset_x > this.width - startX - indicatorSize) {
						batch.draw(selectedIndicator, startX + currentPage * indicatorSize + focus_offset_x, (int)focusY, indicatorSize - focus_offset_x, focusH);
						batch.draw(selectedIndicator, startX, (int)focusY, focus_offset_x, focusH);
					} else {
						batch.draw(selectedIndicator, startX + currentPage * indicatorSize + focus_offset_x, (int)focusY, indicatorSize, focusH);
					}
				}
				break;
			case INDICATOR_STYLE_S3:
				if (nineIndicator != null) {
					focusH = normalH = indicatorNormalW;
					normalY = this.y + (this.height - normalH)/2.0f;
					focusY = this.y + (this.height - focusH)/2.0f;				
					
					for(int i = 0; i < size; i++){
						if (i==currentPage){
							old.a = oldA;
							old.a *= 1 - Math.abs(degree*0.5);
							batch.setColor(old.r,old.g,old.b,old.a);
							float w = indicatorNormalW + (1 - Math.abs(degree))*(indicatorFocusW-indicatorNormalW);
							float offset_x = (indicatorSize - w)/2.0f;
							nineIndicator.draw(batch, startX + i * indicatorSize + offset_x, focusY, w, focusH);
						} else if (i==nextPage){
							old.a = oldA;
							old.a *= Math.abs(degree*0.5)+0.5;
							batch.setColor(old.r,old.g,old.b,old.a);
							float w = indicatorNormalW + (Math.abs(degree))*(indicatorFocusW-indicatorNormalW);
							float offset_x = (indicatorSize - w)/2.0f;
							nineIndicator.draw(batch, startX + i * indicatorSize + offset_x, focusY, w, focusH);
						} else {
							old.a = oldA*0.5f;
							batch.setColor(old.r,old.g,old.b,old.a);
							float offset_x = (indicatorSize - indicatorNormalW)/2.0f;
							nineIndicator.draw(batch, startX + i * indicatorSize + offset_x, normalY, indicatorNormalW, normalH);
						}
					}
				}
				break;
			case INDICATOR_STYLE_S2:
				if(selectedIndicator != null) {
					normalH = indicatorNormalW;
					focusH = indicatorFocusW;
					normalY = this.y + (this.height - normalH)/2.0f;
					
					old.a = oldA;
					batch.setColor(old.r,old.g,old.b,old.a);
					for(int i = 0; i < size; i++){
						if (i==currentPage) {
							float w = indicatorNormalW + (1 - Math.abs(degree))*(indicatorFocusW-indicatorNormalW);
							float offset_x = (indicatorSize - w)/2.0f;
							focusY = this.y + (this.height - w)/2.0f;
							batch.draw(selectedIndicator, startX + i * indicatorSize + offset_x, focusY, w, w);
							batch.draw(indicatorNumber[i], startX + i * indicatorSize + offset_x+w/5, focusY+w/5, w*3/5, w*3/5);
						} else if(i==nextPage) {
							float w = indicatorNormalW + (Math.abs(degree))*(indicatorFocusW-indicatorNormalW);
							float offset_x = (indicatorSize - w)/2.0f;
							focusY = this.y + (this.height - w)/2.0f;
							batch.draw(selectedIndicator, startX + i * indicatorSize + offset_x, focusY, w, w);
							batch.draw(indicatorNumber[i], startX + i * indicatorSize + offset_x+w/5, focusY+w/5, w*3/5, w*3/5);
						} else {
							float offset_x = (indicatorSize - indicatorNormalW)/2.0f;
							batch.draw(selectedIndicator, startX + i * indicatorSize + offset_x, normalY, indicatorNormalW, normalH);
						}
					}
				}
				break;			
			default:
				if(selectedIndicator != null && unselectedIndicator != null) {
					normalH = indicatorNormalW;
					focusH = indicatorFocusW;
					normalY = this.y + (this.height - normalH)/2.0f;
					focusY = this.y + (this.height - focusH)/2.0f;				
					
					float normal_offset_x = (indicatorSize - indicatorNormalW)/2.0f;
					float focus_offset_x = (indicatorSize - indicatorFocusW)/2.0f;
					for(int i = 0; i < size; i++){
						if (i==currentPage) {
							old.a = oldA;
							old.a *= Math.abs(degree);
							batch.setColor(old.r,old.g,old.b,old.a);
						} else if(i==nextPage) {
							old.a = oldA;
							old.a *= 1 - Math.abs(degree);
							batch.setColor(old.r,old.g,old.b,old.a);
						} else {
							old.a = oldA;
							batch.setColor(old.r,old.g,old.b,old.a);				
						}
						batch.draw(unselectedIndicator, startX + i * indicatorSize + normal_offset_x, normalY, indicatorNormalW, normalH);
						
						if (i==currentPage) {
							old.a = oldA;
							old.a *= 1 - Math.abs(degree);
							batch.setColor(old.r,old.g,old.b,old.a);
							batch.draw(selectedIndicator, startX + i * indicatorSize + focus_offset_x, focusY, indicatorFocusW, focusH);
						} else if (i==nextPage) {
							old.a = oldA;
							old.a *= Math.abs(degree);
							batch.setColor(old.r,old.g,old.b,old.a);
							batch.draw(selectedIndicator, startX + i * indicatorSize + focus_offset_x, focusY, indicatorFocusW, focusH);
						}
					}
				}
				break;
			}
		}
		old.a = oldA;
		batch.setColor(old.r,old.g,old.b,old.a);
		
		if(clingState==ClingManager.CLING_STATE_WAIT){
			clingState = ClingManager.getInstance().firePageIndicatorCling(this);
		}
	}
	
	
//	@Override
//	public void applyTransformChild(SpriteBatch batch) {
//		
//	}

	@Override
	public boolean scroll(float x, float y, float deltaX, float deltaY) {
		//Log.v("jbc", "eee scroll degree="+degree);
		if(!Desktop3DListener.bCreatDone)
			return true;
		if (s4indicatorClickTween != null)
			return true;
		if(/*degree != 0 || */!DefaultLayout.click_indicator_enter_pageselect)
			return false;
		if (DefaultLayout.enable_DesktopIndicatorScroll) {
			Root3D.scroll_indicator = true;
			this.requestFocus();
			SendMsgToAndroid.sendHideWorkspaceMsgEx();
			scroll_degree = getTempDegree(x, R3D.page_indicator_size);
			setCurrentPage((int)(scroll_degree+0.5));
			Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			workspace.setCurrentPage((int)(scroll_degree+0.5));			
			float temp_degree = scroll_degree - (int)scroll_degree;
			if (temp_degree < 0.5) {
				setDegree(-temp_degree);
				workspace.setDegreeOnly(-temp_degree);
			} else {
				setDegree(1-temp_degree);
				workspace.setDegreeOnly(1-temp_degree);
			}			
			workspace.updateEffect(scroll_degree);
			if (!Workspace3D.is_longKick)
				((Root3D)viewParent).startDragScrollIndicatorEffect();
		} else {
			if(Math.abs(x-downX)>CLICK_MOVE || Math.abs(y-downY)>CLICK_MOVE){
				downTime = 0;
//				viewParent.onCtrlEvent(this, PAGE_INDICATOR_SCROLL);
			}
		}
		return false;
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		if (pointer != 0)
			return true;
		if(!Desktop3DListener.bCreatDone)
			return true;
		if (s4indicatorClickTween != null)
			return true;
		if(degree != 0 || !DefaultLayout.click_indicator_enter_pageselect)
			return false;
		downTime = System.currentTimeMillis();
		downX = x;
		downY = y;
		ClingManager.getInstance().cancelPageIndicatorCling();
		return true;
	}

	//
	public boolean onTouchUp(float x, float y, int pointer) {
		if (pointer != 0)
			return true;
		if(!Desktop3DListener.bCreatDone)
			return true;
		if (s4indicatorClickTween != null)
			return true;
		if(/*degree != 0 || */!DefaultLayout.click_indicator_enter_pageselect)
			return false;	
		//if (downConsumed && System.currentTimeMillis() - downTime < CLICK_TIME)
		//	return true;
		if (DefaultLayout.enable_DesktopIndicatorScroll) {
			if (Root3D.scroll_indicator) {		
				this.releaseFocus();
				SendMsgToAndroid.sendShowWorkspaceMsgEx();
				scroll_degree = getTempDegree(x, R3D.page_indicator_size);
				setCurrentPage((int)(scroll_degree+0.5));
				Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
				workspace.setCurrentPage((int)(scroll_degree+0.5));
				float temp_degree = scroll_degree - (int)scroll_degree;
				if (temp_degree < 0.5) {
					setDegree(-temp_degree);
					workspace.setDegree(-temp_degree);
				} else {
					setDegree(1-temp_degree);
					workspace.setDegree(1-temp_degree);
				}			
				workspace.startAutoEffect();
				Root3D.scroll_indicator = false;
				((Root3D)viewParent).stopDragScrollIndicatorEffect(false);
			} else if (System.currentTimeMillis() - downTime < CLICK_TIME &&
					Math.abs(x-downX) < CLICK_MOVE &&
					Math.abs(y-downY) < CLICK_MOVE) {
//				float focusWidth = indicatorSize * pageNum;
//				float startX = this.x + (this.width - focusWidth)/2.0f;
//				if (x>=startX && x<this.width-startX) {
//					float temp_scroll_degree = getTempDegree(x, R3D.page_indicator_size);
//					int desPage = (int)(temp_scroll_degree+0.5);
//					if (currentPage == desPage)
//						return true;
//					this.requestFocus();
//					SendMsgToAndroid.sendHideWorkspaceMsgEx();
//					setUser2((float)currentPage);
//					s4indicatorClickTween = startTween(View3DTweenAccessor.USER2, Cubic.OUT,
//							0.3f, (float)desPage, 0f, 0f).setCallback(this);
//				}
				if(System.currentTimeMillis() - downTime < CLICK_TIME &&
						Math.abs(x-downX) < CLICK_MOVE &&
						Math.abs(y-downY) < CLICK_MOVE)
					return viewParent.onCtrlEvent(this, PAGE_INDICATOR_CLICK);
			}
		} else {
			if(System.currentTimeMillis() - downTime < CLICK_TIME &&
					Math.abs(x-downX) < CLICK_MOVE &&
					Math.abs(y-downY) < CLICK_MOVE)
				return viewParent.onCtrlEvent(this, PAGE_INDICATOR_CLICK);
		}
		return true;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		//Log.v("jbc", "eee onLongClick degree="+degree);
		if(!Desktop3DListener.bCreatDone)
			return true;
		if (s4indicatorClickTween != null)
			return true;
		if(degree != 0 || !DefaultLayout.click_indicator_enter_pageselect)
			return false;
		downTime = 0;
		if (DefaultLayout.enable_DesktopIndicatorScroll) {
			Root3D.scroll_indicator = true;
			this.requestFocus();
			SendMsgToAndroid.sendHideWorkspaceMsgEx();
			scroll_degree = getTempDegree(x, R3D.page_indicator_size);
			setCurrentPage((int)(scroll_degree+0.5));
			Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			workspace.setCurrentPage((int)(scroll_degree+0.5));
			float temp_degree = scroll_degree - (int)scroll_degree;			
			if (temp_degree < 0.5) {
				setDegree(-temp_degree);
				workspace.setDegreeOnly(-temp_degree);
			} else {
				setDegree(1-temp_degree);
				workspace.setDegreeOnly(1-temp_degree);
			}	
			workspace.updateEffect(scroll_degree);
			((Root3D)viewParent).startDragScrollIndicatorEffect();
		} else {
			viewParent.onCtrlEvent(this, PAGE_INDICATOR_SCROLL);
		}
		return true;
	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		//Log.v("jbc", "eee fling degree="+degree);
		if(!Desktop3DListener.bCreatDone)
			return true;
		if(degree != 0 || !DefaultLayout.click_indicator_enter_pageselect)
			return false;
		return true;
	}

	@Override
	public void pageScroll(float degree, int index, int count) {
		indicatorAlpha = 1;
		if(indicatorTween != null && !indicatorTween.isFinished()){
			indicatorTween.free();
			indicatorTween = null;
		}
		currentPage = index;
		this.degree = degree;
		pageNum = count;
	}


	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return currentPage;
	}
	
	public void setPageNum(int num){
		pageNum = num;
	}
	
//	public void activate() {
//		show();
//		startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT, 0.8f, ACTIVATE_SCALE, ACTIVATE_SCALE, 0);
//		pageMode = MODE_ACTIVATE;
//	}
//
//	public void normal() {
//		show();
//		startTween(View3DTweenAccessor.SCALE_XY, Elastic.OUT, 0.8f, NORMAL_SCALE, NORMAL_SCALE, 0);
//		pageMode = MODE_NORMAL;
//	}
	
	public void show(){
		if ( this.touchable==true)
		{
			return;
		}
		super.show();
		indicatorAlpha = 1;
		stopTween();		
		animating = true;
		Log.v("PageIndicator3D", "TweenStart show");
		myTween = startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.4f, 1, 0, 0)
				.setUserData(show_indicator).setCallback(this);
		//Log.v("indicator","myTween show_indicator:" + myTween);
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	public void showEx(){
		if ( this.touchable==true)
		{
			return;
		}
		super.show();
		indicatorAlpha = 1;
		stopTween();		
		animating = true;
		Log.v("PageIndicator3D", "TweenStart show");
		myTween = startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.0f, 1, 0, 0)
				.setUserData(show_indicator).setCallback(this);
		//Log.v("indicator","myTween show_indicator:" + myTween);
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
	public void hide(){
		if ( this.touchable==false)
		{
			return;
		}
		this.touchable = false;
		stopTween();
		animating = true;
		Log.v("PageIndicator3D", "TweenStart hide");
		myTween = startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.4f, 0, 0, 0)
				.setUserData(hide_indicator).setCallback(this);
		//Log.v("indicator","myTween hide_indicator:" + myTween);
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	public void hideEx(){
		if ( this.touchable==false)
		{
			return;
		}
		this.touchable = false;
		stopTween();
		animating = true;
		Log.v("PageIndicator3D", "TweenStart hide");
		myTween = startTween(View3DTweenAccessor.OPACITY, Cubic.OUT, 0.0f, 0, 0, 0)
				.setUserData(hide_indicator).setCallback(this);
		//Log.v("indicator","myTween hide_indicator:" + myTween);
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	public void hideNoAnim(){
		this.touchable = false;
		this.visible = false;
		if(clingState==ClingManager.CLING_STATE_SHOW){
			SendMsgToAndroid.sendRefreshClingStateMsg();
		}
	}
	
//	public void showIndicator(){
//		//if(indicatorAnimView.rotation == 0)return;
//		animating = true;
//		//indicatorAnimView.rotation = -180;
//		setRotationVector(0, 0, 1);
//		View3DTweenAccessor.manager.killTarget(indicatorAnimView);
//		Tween.to(indicatorAnimView, View3DTweenAccessor.ROTATION, 0.4f).ease(Quad.OUT)
//			.target(0)
//			.start(View3DTweenAccessor.manager);
//	}
//	
//	public void hideIndicator(){
//		//if(indicatorAnimView.rotation == -180)return;
//		animating = true;
//		//indicatorAnimView.rotation = 0;
//		setRotationVector(0, 0, 1);
//		View3DTweenAccessor.manager.killTarget(indicatorAnimView);
//		Tween.to(indicatorAnimView, View3DTweenAccessor.ROTATION, 0.4f).ease(Quad.OUT)
//			.target(-180)
//			.start(View3DTweenAccessor.manager);
//	}

	@Override
	public void setCurrentPage(int current) {
		if(currentPage == -1){
			currentPage = current;
			targetPage = currentPage;
		}
		currentPage = current;
	}
	public void setDegree(float degree) {
		this.degree = degree;
	}
	@Override
	public boolean visible() {
		return this.isVisible() && color.a != 0 && touchable;
	}

	@Override
	public int getClingPriority() {
		return ClingManager.PAGEINDICATOR_CLING;
	}

	@Override
	public void dismissCling() {
		clingState = ClingManager.CLING_STATE_DISMISSED;
	}

	@Override
	public void setPriority(int priority) {
		// TODO Auto-generated method stub
		
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
	@Override
	public void setUser2(float value) {
		super.setUser2(value);
		setCurrentPage((int)(value+0.5));
		Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
		workspace.setCurrentPage((int)(value+0.5));
		float temp_degree = value - (int)value;			
		if (temp_degree < 0.5) {
			setDegree(-temp_degree);
			workspace.setDegreeOnly(-temp_degree);
		} else {
			setDegree(1-temp_degree);
			workspace.setDegreeOnly(1-temp_degree);
		}	
		workspace.updateEffect(value);
	}
	public void finishAutoEffect(){
		indicatorAlpha = 1;
		if(indicatorTween != null && !indicatorTween.isFinished()){
			indicatorTween.free();
			indicatorTween = null;
		}
		indicatorTween = startTween(View3DTweenAccessor.USER, Cubic.OUT, INDICATOR_FADE_TWEEN_DURATION, 0, 0, 0)
				.setCallback(this);
		//Log.v("indicator","indicatorTween:" + indicatorTween);
	}
	
	@Override
	public void onEvent(int type, BaseTween source) {
		// TODO Auto-generated method stub
		if (type==TweenCallback.COMPLETE && source==myTween)
		{			
			//Log.v("indicator","source:" + source);
			myTween = null;
			int animKind = (Integer) (source.getUserData());
			if (animKind==hide_indicator)
			{
				Log.v("PageIndicator3D", "TweenComplete hide");
				this.color.a = 0;
				if (!touchable)
					super.hide();
			}
			else if (animKind ==show_indicator)
			{
				Log.v("PageIndicator3D", "TweenComplete show");
				this.color.a = 1;
				super.show();
				finishAutoEffect();
			}
			return;
		}
		if (type == TweenCallback.COMPLETE && source == indicatorTween) {
			indicatorTween = null;
			indicatorAlpha = 0;
			return;
		}
		if (type == TweenCallback.COMPLETE && source == s4indicatorClickTween) {
			s4indicatorClickTween = null;
			Workspace3D workspace = iLoongLauncher.getInstance().getD3dListener().getWorkspace3D();
			workspace.recoverPageSequence();
			this.releaseFocus();
			SendMsgToAndroid.sendShowWorkspaceMsgEx();
			return;
		}
	}
	
	public float getTempDegree(float touch_x, int indicator_space) {
		int screenWidth = Utils3D.getScreenWidth();
		int total_w = indicator_space*(pageNum-1);
		float start_x = (screenWidth-total_w)/2;
		float degree = (float)(touch_x - start_x)/indicator_space;
		if (degree<0) return 0;
		if (degree>pageNum-1) return pageNum-1;
		return degree;
	}
	
	public float getScrollDegree() {
		return scroll_degree;
	}
}
