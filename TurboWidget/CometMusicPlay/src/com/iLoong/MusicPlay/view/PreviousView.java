package com.iLoong.MusicPlay.view;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.MusicPlay.common.Parameter;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class PreviousView extends PluginViewObject3D implements MusicListener {
	
	private static final String FRONT_OBJ = "pause.obj";
	private static final String FRONT_TEXTURE = "previous.png";
	private final static String FRONT_PRESS_TEXTURE = "previous_press.png";
	
	private MusicController musicController = null;
	private TextureRegion previousRegion = null;
	private TextureRegion previousPressRegion = null;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private MainAppContext mAppContext = null;
	public PreviousView(String name, MainAppContext appContext) {
		super(appContext, name, FRONT_TEXTURE, FRONT_OBJ);
		mAppContext = appContext;
		this.setDepthMode(true);
		initTextureRegion(mAppContext);
		this.region = previousRegion;
		super.build();
		move( WidgetMusicPlayGroup.PREVIEWX , WidgetMusicPlayGroup.PREVIEWY , Parameter.PREVIEW_TOZ * WidgetMusicPlayGroup.scale );
		mTimer = new Timer();
	}

	private void initTextureRegion(MainAppContext appContext)
	{
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath(FRONT_TEXTURE);
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		BitmapTexture bt = new BitmapTexture(bm);
		bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);			
		previousRegion = new TextureRegion(bt);
		bm.recycle();
		bm = null;
		
		texturePath = getThemeTexturePath(FRONT_PRESS_TEXTURE);
		fileHandle = gdxFile.internal(texturePath);
		Bitmap bm1 = BitmapFactory.decodeStream(fileHandle.read());
		BitmapTexture bt1 = new BitmapTexture(bm1);
		bt1.setFilter(TextureFilter.Linear, TextureFilter.Linear);			
		previousPressRegion = new TextureRegion(bt1);
		bm1.recycle();
		bm1 = null;
	}
	@Override
	public boolean onClick(float x, float y) {
		if (musicController != null) {
			musicController.prevMusic();
		}
		return true;
	}

	public void setMusicController(MusicController musicController) {
		// TODO Auto-generated method stub
		this.musicController = musicController;
		musicController.addMusicListener(this);
	}

	@Override
	public void onNext(MusicData music) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrevious(MusicData music) {
		// TODO Auto-generated method stub
		releasePreviousPressed();
	}

	@Override
	public void onPause(MusicData music) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlay(MusicData music) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		this.region = previousPressRegion;
		releasePreviousPressed();
		return super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		this.region = previousRegion;
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public boolean onLongClick(float x, float y) {
		// TODO Auto-generated method stub
		this.region = previousRegion;
		return super.onLongClick(x, y);
	}


	@Override
	public void onMetaChanged(MusicData music) {
		// TODO Auto-generated method stub
		this.region = previousRegion;
	}

	private void releasePreviousPressed() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PreviousView.this.region = previousRegion;
			}
		};
		mTimer.schedule(mTimerTask, 50);
	}

	public void changeSkin(final String subTheme) {
		appContext.mGdxApplication.postRunnable(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Texture previousTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "previous.png");
				if (previousTexture != null) {
					previousTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (PreviousView.this.region.equals(previousRegion)) {
						TextureRegion oldRegion = PreviousView.this.region;
						PreviousView.this.region = previousRegion = new TextureRegion(
								previousTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = previousRegion;
						previousRegion = new TextureRegion(previousTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}

				Texture previousPressTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "previous_press.png");
				if (previousPressTexture != null) {
					previousPressTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (PreviousView.this.region.equals(previousPressRegion)) {
						TextureRegion oldRegion = PreviousView.this.region;
						PreviousView.this.region = previousPressRegion = new TextureRegion(
								previousPressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = previousPressRegion;
						previousPressRegion = new TextureRegion(
								previousPressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}
			}
		});
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		if (previousRegion != null) {
			if (previousRegion.getTexture() != null) {
				previousRegion.getTexture().dispose();
			}
		}
		if (previousPressRegion != null) {
			if (previousPressRegion.getTexture() != null) {
				previousPressRegion.getTexture().dispose();
			}
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
}
