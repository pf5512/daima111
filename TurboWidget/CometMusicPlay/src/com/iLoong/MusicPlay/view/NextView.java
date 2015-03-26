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

public class NextView extends PluginViewObject3D implements MusicListener {
	
	private static final String NEXT_OBJ = "pause.obj";
	private static final String NEXT_TEXTURE = "next.png";
	private static final String NEXT_PRESS_TEXTURE = "next_press.png";
	
	public MusicController musicController = null;
	private TextureRegion nextRegion = null;
	private TextureRegion nextPressRegion = null;
	private Timer mTimer = new Timer();
	private TimerTask mTimerTask = null;

	private MainAppContext mAppContext = null;
	
	public MusicController getMusicController() {
		return musicController;
	}

	public void setMusicController(MusicController musicController) {
		this.musicController = musicController;
		musicController.addMusicListener(this);
	}

	public NextView(String name, MainAppContext appContext) {
		super(appContext, name, NEXT_TEXTURE,NEXT_OBJ);
		
		this.mAppContext=appContext;
		this.setDepthMode(true);
		initTextureRegion(mAppContext);
		this.region = nextRegion;
		super.build();
		move( WidgetMusicPlayGroup.NEXTX , WidgetMusicPlayGroup.NEXTY , Parameter.NEXT_TOZ * WidgetMusicPlayGroup.scale );
	}
	private void initTextureRegion(MainAppContext appContext)
	{
		AndroidFiles gdxFile = new AndroidFiles(appContext.mWidgetContext.getAssets());
		String texturePath = getThemeTexturePath(NEXT_TEXTURE);
		FileHandle fileHandle = gdxFile.internal(texturePath);
		Bitmap bm = BitmapFactory.decodeStream(fileHandle.read());
		BitmapTexture bt = new BitmapTexture(bm);
		bt.setFilter(TextureFilter.Linear, TextureFilter.Linear);			
		nextRegion = new TextureRegion(bt);
		bm.recycle();
		bm = null;
		
		texturePath = getThemeTexturePath(NEXT_PRESS_TEXTURE);
		fileHandle = gdxFile.internal(texturePath);
		Bitmap bm1 = BitmapFactory.decodeStream(fileHandle.read());
		BitmapTexture bt1 = new BitmapTexture(bm1);
		bt1.setFilter(TextureFilter.Linear, TextureFilter.Linear);			
		nextPressRegion = new TextureRegion(bt1);
		bm1.recycle();
		bm1 = null;
	}
	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub

		if (musicController != null) {
			musicController.nextMusic();
		}

		return true;
	}

	@Override
	public boolean onLongClick(float x, float y) {
		// TODO Auto-generated method stub
		this.region = nextRegion;
		return super.onLongClick(x, y);
	}

	@Override
	public boolean onTouchDown(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		this.region = nextPressRegion;
		releaseNextPressed();
		return super.onTouchDown(x, y, pointer);
	}

	@Override
	public boolean onTouchUp(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		this.region = nextRegion;
		return super.onTouchUp(x, y, pointer);
	}

	@Override
	public void onNext(MusicData music) {
		// TODO Auto-generated method stub
		this.region = nextPressRegion;
		releaseNextPressed();
	}

	@Override
	public void onPrevious(MusicData music) {
		// TODO Auto-generated method stub

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
	public void onMetaChanged(MusicData music) {
		// TODO Auto-generated method stub
		this.region = nextRegion;
	}

	private void releaseNextPressed() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				NextView.this.region = nextRegion;
			}
		};
		mTimer.schedule(mTimerTask, 50);
	}

	public void changeSkin(final String subTheme) {
		appContext.mGdxApplication.postRunnable(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Texture nextTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "next.png");
				if (nextTexture != null) {
					nextTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (NextView.this.region.equals(nextRegion)) {
						TextureRegion oldRegion = NextView.this.region;
						NextView.this.region = nextRegion = new TextureRegion(
								nextTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = nextRegion;
						nextRegion = new TextureRegion(nextTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					}
				}
				Texture nextPressTexture = ThemeHelper.getThemeSubTexture(
						appContext, subTheme, "next_press.png");
				if (nextPressTexture != null) {
					nextPressTexture.setFilter(TextureFilter.Linear,
							TextureFilter.Linear);
					if (NextView.this.region.equals(nextPressRegion)) {
						TextureRegion oldRegion = NextView.this.region;
						NextView.this.region = nextPressRegion = new TextureRegion(
								nextPressTexture);
						if (oldRegion != null && oldRegion.getTexture() != null) {
							oldRegion.getTexture().dispose();
						}
					} else {
						TextureRegion oldRegion = nextPressRegion;
						nextPressRegion = new TextureRegion(nextPressTexture);
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
		if (nextRegion != null) {
			if (nextRegion.getTexture() != null) {
				nextRegion.getTexture().dispose();
			}
		}
		if (nextPressRegion != null) {
			if (nextPressRegion.getTexture() != null) {
				nextPressRegion.getTexture().dispose();
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
