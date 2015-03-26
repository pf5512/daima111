package com.iLoong.Music.View;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.Music.R;
import com.iLoong.Widget3D.BaseView.PluginViewObject3D;
import com.iLoong.Widget3D.Theme.ThemeHelper;
import com.iLoong.Widget3D.Theme.WidgetThemeManager;
import com.iLoong.launcher.Widget3D.MainAppContext;

public class PreviousView extends PluginViewObject3D implements MusicListener {
	private MusicController musicController = null;
	private TextureRegion previousRegion = null;
	private TextureRegion previousPressRegion = null;
	private Timer mTimer;
	private TimerTask mTimerTask;

	public PreviousView(String name, MainAppContext appContext,
			TextureRegion region) {
		super(appContext, name, region, "previous.obj", null);
		this.region = previousRegion = new TextureRegion(WidgetThemeManager
				.getInstance().getThemeTexture("previous.png"));
		previousPressRegion = new TextureRegion(WidgetThemeManager
				.getInstance().getThemeTexture("previous_press.png"));
		float pre_width = WidgetThemeManager.getInstance().getFloat(
				"previous_width");
		float pre_height = WidgetThemeManager.getInstance().getFloat(
				"previous_height");
		this.setSize(pre_width, pre_height);
		this.x = WidgetThemeManager.getInstance().getFloat("previous_x");
		this.y = WidgetThemeManager.getInstance().getFloat("previous_y");
		super.build();
		mTimer = new Timer();
	}

	@Override
	public boolean onClick(float x, float y) {
		// TODO Auto-generated method stub
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
